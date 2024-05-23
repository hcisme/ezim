package org.chc.ezim.service.impl;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.dto.*;
import org.chc.ezim.entity.enums.*;
import org.chc.ezim.entity.model.*;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.entity.vo.UserContactSearchResultVO;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.mapper.*;
import org.chc.ezim.redis.RedisComponent;
import org.chc.ezim.service.UserContactService;
import org.chc.ezim.utils.CopyTools;
import org.chc.ezim.utils.StringTools;
import org.chc.ezim.websocket.ChannelContextUtils;
import org.chc.ezim.websocket.MessageHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 联系人 业务接口实现
 */
@Service("userContactService")
public class UserContactServiceImpl implements UserContactService {

    @Resource
    private UserContactMapper<UserContact, UserContactDto> userContactMapper;

    @Resource
    private UserMapper<User, UserDto> userMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserGroupMapper<UserGroup, UserGroupDto> userGroupMapper;

    @Resource
    private ChatSessionMapper<ChatSession, ChatSessionDto> chatSessionMapper;

    @Resource
    private ChatSessionUserMapper<ChatSessionUser, ChatSessionUserDto> chatSessionUserMapper;

    @Resource
    private ChatMessageMapper<ChatMessage, ChatMessageDto> chatMessageMapper;

    @Resource
    private MessageHandler messageHandler;

    @Resource
    private ChannelContextUtils channelContextUtils;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<UserContact> findListByParam(UserContactDto param) {
        return this.userContactMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(UserContactDto param) {
        return this.userContactMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<UserContact> findListByPage(UserContactDto param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPage(), count, pageSize);
        param.setSimplePage(page);
        List<UserContact> list = this.findListByParam(param);
        PaginationResultVO<UserContact> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(UserContact bean) {
        return this.userContactMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<UserContact> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userContactMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<UserContact> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userContactMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(UserContact bean, UserContactDto param) {
        StringTools.checkParam(param);
        return this.userContactMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(UserContactDto param) {
        StringTools.checkParam(param);
        return this.userContactMapper.deleteByParam(param);
    }

    /**
     * 根据UserIdAndContactId获取对象
     */
    @Override
    public UserContact getUserContactByUserIdAndContactId(String userId, String contactId) {
        return this.userContactMapper.selectByUserIdAndContactId(userId, contactId);
    }

    /**
     * 根据UserIdAndContactId修改
     */
    @Override
    public Integer updateUserContactByUserIdAndContactId(UserContact bean, String userId, String contactId) {
        return this.userContactMapper.updateByUserIdAndContactId(bean, userId, contactId);
    }

    /**
     * 根据UserIdAndContactId删除
     */
    @Override
    public Integer deleteUserContactByUserIdAndContactId(String userId, String contactId) {
        return this.userContactMapper.deleteByUserIdAndContactId(userId, contactId);
    }

    @Override
    public UserContactSearchResultVO searchContact(String userId, String contactId) {
        UserContactTypeEnum userContactTypeEnum = UserContactTypeEnum.getByPrefix(contactId);
        if (userContactTypeEnum == null) {
            return null;
        }
        UserContactSearchResultVO resultVO = new UserContactSearchResultVO();
        switch (userContactTypeEnum) {
            case USER -> {
                User user = userMapper.selectById(contactId);
                if (user == null) {
                    return null;
                }
                resultVO = CopyTools.copy(user, UserContactSearchResultVO.class);
            }

            case GROUP -> {
                UserGroup userGroup = userGroupMapper.selectById(contactId);
                if (userGroup == null) {
                    return null;
                }
                resultVO.setNickName(userGroup.getGroupName());
            }
        }

        resultVO.setContactType(userContactTypeEnum.getDesc());
        resultVO.setContactId(contactId);

        // 是自己
        if (userId.equals(contactId)) {
            resultVO.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            return resultVO;
        }

        // 查询是否是好友
        UserContact userContact = userContactMapper.selectByUserIdAndContactId(userId, contactId);
        resultVO.setStatus(userContact == null ? UserContactStatusEnum.NOT_FRIEND.getStatus() : userContact.getStatus());

        return resultVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeContactUser(String userId, String contactId, UserContactStatusEnum statusEnum) {
        // 移除好友 自己视角
        UserContact userContact = new UserContact();
        userContact.setStatus(statusEnum.getStatus());
        userContactMapper.updateByUserIdAndContactId(userContact, userId, contactId);

        // 将好友中自己移除  对方视角
        UserContact friendContact = new UserContact();
        if (UserContactStatusEnum.DEL == statusEnum) {
            friendContact.setStatus(UserContactStatusEnum.DEL_BE.getStatus());
        } else if (UserContactStatusEnum.BLACKLIST == statusEnum) {
            friendContact.setStatus(UserContactStatusEnum.BLACKLIST_BE.getStatus());
        } else {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        userContactMapper.updateByUserIdAndContactId(friendContact, contactId, userId);

        // 从我的好友列表缓存中删除好友
        redisComponent.removeUserContact(contactId, userId);
        // 从好友列表缓存中删除我
        redisComponent.removeUserContact(userId, contactId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addContact(String applyUserId, String receiveUserId, String contactId, Integer contactType, String applyInfo) {
        Date date = new Date();
        // 群聊人数
        if (UserContactTypeEnum.GROUP.getType().equals(contactType)) {
            UserContactDto userContactDto = new UserContactDto();
            userContactDto.setContactId(contactId);
            userContactDto.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            Integer count = userContactMapper.selectCount(userContactDto);
            Integer maxGroupMemberCount = redisComponent.getSetting().getMaxGroupMemberCount();
            if (count >= maxGroupMemberCount) {
                throw new BusinessException("成员已满，无法加入");
            }
        }

        // 同意 双方添加好友吧
        ArrayList<UserContact> userContacts = new ArrayList<>();
        // 申请人添加对方   群组直接就是添加一次就行(你添加群组 群组自动加你)
        UserContact userContact = new UserContact();
        userContact.setUserId(applyUserId);
        userContact.setContactId(contactId);
        userContact.setContactType(contactType);
        userContact.setCreateTime(date);
        userContact.setLastUpdateTime(date);
        userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
        userContacts.add(userContact);

        // 如果是申请好友 接受人添加申请人，  群组不用添加对方为好友
        // 接受人添加申请人
        if (UserContactTypeEnum.USER.getType().equals(contactType)) {
            UserContact userContact1 = new UserContact();
            userContact1.setUserId(receiveUserId);
            userContact1.setContactId(applyUserId);
            userContact1.setContactType(contactType);
            userContact1.setCreateTime(date);
            userContact1.setLastUpdateTime(date);
            userContact1.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            userContacts.add(userContact1);
        }

        // userId 和 contactId 都相同才触发更新 否则就是插入数据
        // 批量插入数据
        userContactMapper.insertOrUpdateBatch(userContacts);

        // 如果是好友 接收人也添加申请人为好友  添加缓存
        if (UserContactTypeEnum.USER.getType().equals(contactType)) {
            redisComponent.addSingleUserContact(receiveUserId, applyUserId);
        }
        redisComponent.addSingleUserContact(applyUserId, contactId);

        // 创建会话 发送消息
        String sessionId = null;
        if (UserContactTypeEnum.USER.getType().equals(contactType)) {
            sessionId = StringTools.generatorChatSessionId4User(new String[]{applyUserId, contactId});
        } else {
            sessionId = StringTools.generatorChatSessionId4Group(contactId);
        }

        ArrayList<ChatSessionUser> chatSessionUserList = new ArrayList<>();
        if (UserContactTypeEnum.USER.getType().equals(contactType)) {
            // 创建会话
            ChatSession chatSession = new ChatSession();
            chatSession.setId(sessionId);
            chatSession.setLastMessage(applyInfo);
            chatSession.setLastReceiveTime(date.getTime());
            chatSessionMapper.insertOrUpdate(chatSession);

            // 申请人 session
            ChatSessionUser applySessionUser = new ChatSessionUser();
            applySessionUser.setUserId(applyUserId);
            applySessionUser.setContactId(contactId);
            applySessionUser.setSessionId(sessionId);
            User applyUser = userMapper.selectById(contactId);
            applySessionUser.setContactName(applyUser.getNickName());
            chatSessionUserList.add(applySessionUser);
            // 接收人 session
            ChatSessionUser contactSessionUser = new ChatSessionUser();
            contactSessionUser.setUserId(contactId);
            contactSessionUser.setContactId(applyUserId);
            contactSessionUser.setSessionId(sessionId);
            User contactUser = userMapper.selectById(applyUserId);
            contactSessionUser.setContactName(contactUser.getNickName());
            chatSessionUserList.add(contactSessionUser);
            chatSessionUserMapper.insertOrUpdateBatch(chatSessionUserList);

            // 记录消息
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSessionId(sessionId);
            chatMessage.setMessageType(SendMessageTypeEnum.ADD_FRIEND.getType());
            chatMessage.setMessageContent(applyInfo);
            chatMessage.setSendUserId(applyUserId);
            chatMessage.setSendUserNickName(applyUser.getNickName());
            chatMessage.setSendTime(date.getTime());
            chatMessage.setContactId(contactId);
            chatMessage.setContactType(UserContactTypeEnum.USER.getType());
            chatMessageMapper.insert(chatMessage);
            // 发送 ws 消息
            MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
            messageHandler.sendMessage(messageSendDto);

            messageSendDto.setMessageType(SendMessageTypeEnum.ADD_FRIEND_SELF.getType());
            messageSendDto.setContactId(applyUserId);
            messageSendDto.setExtendData(contactUser);
            messageHandler.sendMessage(messageSendDto);
        } else {
            // 加入群组
            ChatSessionUser chatSessionUser = new ChatSessionUser();
            chatSessionUser.setUserId(applyUserId);
            chatSessionUser.setContactId(contactId);
            UserGroup group = userGroupMapper.selectById(contactId);
            chatSessionUser.setContactName(group.getGroupName());
            chatSessionUser.setSessionId(sessionId);
            chatSessionUserMapper.insertOrUpdate(chatSessionUser);

            User applyUser = userMapper.selectById(applyUserId);
            String sendMessage = String.format(SendMessageTypeEnum.ADD_GROUP.getInitMessage(), applyUser.getNickName());

            // 增加 session 信息
            ChatSession chatSession = new ChatSession();
            chatSession.setId(sessionId);
            chatSession.setLastReceiveTime(date.getTime());
            chatSession.setLastMessage(sendMessage);
            chatSessionMapper.insertOrUpdate(chatSession);

            // 增加 聊天 信息
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSessionId(sessionId);
            chatMessage.setMessageType(SendMessageTypeEnum.ADD_GROUP.getType());
            chatMessage.setMessageContent(sendMessage);
            chatMessage.setSendTime(date.getTime());
            chatMessage.setContactId(contactId);
            chatMessage.setContactType(UserContactTypeEnum.GROUP.getType());
            chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus());
            chatMessageMapper.insert(chatMessage);

            // 将群组添加到联系人
            redisComponent.addSingleUserContact(applyUserId, group.getId());
            // 将新创建的群组添加到群组通道
            channelContextUtils.addUser2Group(applyUserId, group.getId());

            // 发送 ws 消息
            MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
            messageSendDto.setContactId(contactId);

            // 获取群人数量
            UserContactDto userContactDto = new UserContactDto();
            userContactDto.setContactId(contactId);
            userContactDto.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            Integer memeberCount = userContactMapper.selectCount(userContactDto);
            messageSendDto.setMemberCount(memeberCount);
            messageSendDto.setContactName(group.getGroupName());

            messageHandler.sendMessage(messageSendDto);
        }
    }

    /**
     * 用户注册时 添加机器人为好友
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addContact4Robot(String userId) {
        Date date = new Date();
        SettingDto setting = redisComponent.getSetting();
        String contactId = setting.getRobotUid();
        String contactName = setting.getRobotNickName();
        String sendMessage = setting.getRobotWelcome();
        sendMessage = StringTools.cleanHtmlTag(sendMessage);

        // 添加机器人为好友
        UserContact userContact = new UserContact();
        userContact.setUserId(userId);
        userContact.setContactId(contactId);
        userContact.setContactType(UserContactTypeEnum.USER.getType());
        userContact.setCreateTime(date);
        userContact.setLastUpdateTime(date);
        userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
        userContactMapper.insert(userContact);

        // 增加会话信息
        String sessionId = StringTools.generatorChatSessionId4User(new String[]{userId, contactId});
        ChatSession chatSession = new ChatSession();
        chatSession.setId(sessionId);
        chatSession.setLastMessage(sendMessage);
        chatSession.setLastReceiveTime(date.getTime());
        chatSessionMapper.insert(chatSession);

        // 增加会话人物相关信息
        ChatSessionUser chatSessionUser = new ChatSessionUser();
        chatSessionUser.setUserId(userId);
        chatSessionUser.setContactId(contactId);
        chatSessionUser.setSessionId(sessionId);
        chatSessionUser.setContactName(contactName);
        chatSessionUserMapper.insert(chatSessionUser);

        // 增加聊天信息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionId(sessionId);
        chatMessage.setMessageType(SendMessageTypeEnum.CHAT.getType());
        chatMessage.setMessageContent(sendMessage);
        // 发送消息的是机器人
        chatMessage.setSendUserId(contactId);
        chatMessage.setSendUserNickName(contactName);
        chatMessage.setSendTime(date.getTime());
        chatMessage.setContactId(userId);
        chatMessage.setContactType(UserContactTypeEnum.USER.getType());
        chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus());
        chatMessageMapper.insert(chatMessage);
    }
}