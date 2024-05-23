package org.chc.ezim.service.impl;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.config.AppConfigProperties;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.entity.dto.*;
import org.chc.ezim.entity.enums.*;
import org.chc.ezim.entity.model.*;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.mapper.*;
import org.chc.ezim.redis.RedisComponent;
import org.chc.ezim.service.UserContactService;
import org.chc.ezim.service.UserGroupService;
import org.chc.ezim.utils.CopyTools;
import org.chc.ezim.utils.StringTools;
import org.chc.ezim.websocket.ChannelContextUtils;
import org.chc.ezim.websocket.MessageHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;


/**
 * 业务接口实现
 */
@Service("userGroupService")
public class UserGroupServiceImpl implements UserGroupService {

    @Resource
    private UserGroupMapper<UserGroup, UserGroupDto> userGroupMapper;

    @Resource
    private UserContactMapper<UserContact, UserContactDto> userContactMapper;

    @Resource
    private UserContactService userContactService;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AppConfigProperties appConfigProperties;

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

    @Resource
    private ChatSessionUserServiceImpl chatSessionUserService;

    @Resource
    private UserMapper<User, UserDto> userMapper;

    @Resource
    @Lazy
    private UserGroupService userGroupService;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<UserGroup> findListByParam(UserGroupDto param) {
        return this.userGroupMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(UserGroupDto param) {
        return this.userGroupMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<UserGroup> findListByPage(UserGroupDto param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPage(), count, pageSize);
        param.setSimplePage(page);
        List<UserGroup> list = this.findListByParam(param);
        PaginationResultVO<UserGroup> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(),
                page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(UserGroup bean) {
        return this.userGroupMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<UserGroup> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userGroupMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<UserGroup> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userGroupMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(UserGroup bean, UserGroupDto param) {
        StringTools.checkParam(param);
        return this.userGroupMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(UserGroupDto param) {
        StringTools.checkParam(param);
        return this.userGroupMapper.deleteByParam(param);
    }

    /**
     * 根据Id获取对象
     */
    @Override
    public UserGroup getUserGroupById(String id) {
        return this.userGroupMapper.selectById(id);
    }

    /**
     * 根据Id修改
     */
    @Override
    public Integer updateUserGroupById(UserGroup bean, String id) {
        return this.userGroupMapper.updateById(bean, id);
    }

    /**
     * 根据Id删除
     */
    @Override
    public Integer deleteUserGroupById(String id) {
        return this.userGroupMapper.deleteById(id);
    }

    /**
     * 创建群组
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createOrUpdateGroup(UserGroup userGroup, MultipartFile avatarFile, MultipartFile avatarCover) throws IOException {
        Date date = new Date();
        // 新增
        if (StringTools.isEmpty(userGroup.getId())) {
            Integer groupCount = userGroupMapper.selectCount(new UserGroupDto(userGroup.getGroupOwnerId()));
            SettingDto setting = redisComponent.getSetting();
            if (groupCount >= setting.getMaxGroupCount()) {
                throw new BusinessException("最多只能创建" + setting.getMaxGroupCount() + "个群组");
            }

            userGroup.setCreateTime(date);
            userGroup.setId(StringTools.getGroupId());
            userGroupMapper.insert(userGroup);

            // 将群组设置为联系人
            UserContact userContact = new UserContact();
            userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            userContact.setContactType(UserContactTypeEnum.GROUP.getType());
            userContact.setContactId(userGroup.getId());
            userContact.setUserId(userGroup.getGroupOwnerId());
            userContact.setCreateTime(date);
            userContact.setLastUpdateTime(date);
            userContactMapper.insert(userContact);

            // 上传图片逻辑
            String baseFileFolder = appConfigProperties.getProjectFolder() + Constants.FILE_FOLDER;
            File targetFileFolder = new File(baseFileFolder + Constants.FILE_FOLDER_AVATAR_NAME);
            if (!targetFileFolder.exists()) {
                targetFileFolder.mkdirs();
            }
            String filePath = targetFileFolder.getPath() + "/" + userGroup.getId() + Constants.IMAGE_SUFFIX;
            avatarFile.transferTo(new File(filePath));
            avatarCover.transferTo(new File(filePath + Constants.COVER_IMAGE_SUFFIX));

            // 创建会话
            String sessionId = StringTools.generatorChatSessionId4Group(userGroup.getId());
            ChatSession chatSession = new ChatSession();
            chatSession.setId(sessionId);
            chatSession.setLastMessage(SendMessageTypeEnum.GROUP_CREATE.getInitMessage());
            chatSession.setLastReceiveTime(date.getTime());
            chatSessionMapper.insert(chatSession);

            ChatSessionUser chatSessionUser = new ChatSessionUser();
            chatSessionUser.setUserId(userGroup.getGroupOwnerId());
            chatSessionUser.setContactId(userGroup.getId());
            chatSessionUser.setContactName(userGroup.getGroupName());
            chatSessionUser.setSessionId(sessionId);
            chatSessionUserMapper.insert(chatSessionUser);

            // 创建消息
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSessionId(sessionId);
            chatMessage.setMessageType(SendMessageTypeEnum.GROUP_CREATE.getType());
            chatMessage.setMessageContent(SendMessageTypeEnum.GROUP_CREATE.getInitMessage());
            chatMessage.setSendTime(date.getTime());
            chatMessage.setContactId(userGroup.getId());
            chatMessage.setContactType(UserContactTypeEnum.GROUP.getType());
            chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus());
            chatMessageMapper.insert(chatMessage);
            // 将群组添加到联系人
            redisComponent.addSingleUserContact(userGroup.getGroupOwnerId(), userGroup.getId());
            // 将新创建的群组添加到群组通道
            channelContextUtils.addUser2Group(userGroup.getGroupOwnerId(), userGroup.getId());

            // 发 ws 消息
            chatSessionUser.setLastMessage(SendMessageTypeEnum.GROUP_CREATE.getInitMessage());
            chatSessionUser.setLastReceiveTime(String.valueOf(date.getTime()));
            chatSessionUser.setMemberCount(1);

            MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
            messageSendDto.setExtendData(chatSessionUser);
            messageSendDto.setLastMessage(chatSessionUser.getLastMessage());
            messageHandler.sendMessage(messageSendDto);
        } else {
            // 修改
            UserGroup dbInfo = userGroupMapper.selectById(userGroup.getId());
            if (!dbInfo.getGroupOwnerId().equals(userGroup.getGroupOwnerId())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            userGroupMapper.updateById(userGroup, userGroup.getId());

            // 上传图片逻辑
            String baseFileFolder = appConfigProperties.getProjectFolder() + Constants.FILE_FOLDER;
            File targetFileFolder = new File(baseFileFolder + Constants.FILE_FOLDER_AVATAR_NAME);
            if (!targetFileFolder.exists()) {
                targetFileFolder.mkdirs();
            }
            String filePath = targetFileFolder.getPath() + "/" + userGroup.getId() + Constants.IMAGE_SUFFIX;
            if (avatarFile != null) {
                avatarFile.transferTo(new File(filePath));
            }
            if (avatarCover != null) {
                avatarCover.transferTo(new File(filePath + Constants.COVER_IMAGE_SUFFIX));
            }

            // 更新相关表冗余信息
            String contactNameUpdate = null;
            if (!dbInfo.getGroupName().equals(userGroup.getGroupName())) {
                contactNameUpdate = userGroup.getGroupName();
            }
            if (contactNameUpdate == null) {
                return;
            }
            chatSessionUserService.updateRedundancyInfo(contactNameUpdate, userGroup.getId());
        }
    }

    /**
     * 解散群
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void dissolve(String groupOwnerId, String groupId) {
        UserGroup dbGroupInfo = userGroupMapper.selectById(groupId);
        if (dbGroupInfo == null || !dbGroupInfo.getGroupOwnerId().equals(groupOwnerId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 删除  改变群组状态 为 解散
        UserGroup group = new UserGroup();
        group.setStatus(GroupStatusEnum.DISSOLUTION.getStatus());
        userGroupMapper.updateById(group, groupId);

        // 更新联系人信息
        UserContactDto userContactDto = new UserContactDto();
        // 更新的条件   是群组 且 群组id相同
        userContactDto.setContactId(groupId);
        userContactDto.setContactType(UserContactTypeEnum.GROUP.getType());
        // 更新的内容
        UserContact updateUserContact = new UserContact();
        updateUserContact.setStatus(UserContactStatusEnum.DEL.getStatus());

        userContactMapper.updateByParam(updateUserContact, userContactDto);

        // 移除相关群员的联系人缓存
        List<UserContact> userContactList = userContactMapper.selectList(userContactDto);
        for (UserContact userContact : userContactList) {
            redisComponent.removeUserContact(userContact.getUserId(), userContact.getContactId());
        }
        String sessionId = StringTools.generatorChatSessionId4Group(groupId);
        Date date = new Date();
        String messageContent = SendMessageTypeEnum.DISSOLUTION_GROUP.getInitMessage();

        // 1.更新会话信息
        ChatSession chatSession = new ChatSession();
        chatSession.setLastMessage(messageContent);
        chatSession.setLastReceiveTime(date.getTime());
        chatSessionMapper.updateById(chatSession, sessionId);

        // 2.记录群消息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionId(sessionId);
        chatMessage.setSendTime(date.getTime());
        chatMessage.setContactType(UserContactTypeEnum.GROUP.getType());
        chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus());
        chatMessage.setMessageType(SendMessageTypeEnum.DISSOLUTION_GROUP.getType());
        chatMessage.setContactId(groupId);
        chatMessage.setMessageContent(messageContent);
        chatMessageMapper.insert(chatMessage);

        // 3.发送群解散通知
        MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
        messageHandler.sendMessage(messageSendDto);
    }

    @Override
    public void addOrRemoveGroupUser(TokenUserInfoDto tokenUserInfoDto, String groupId, String selectContacts, Integer opType) {
        UserGroup userGroup = userGroupMapper.selectById(groupId);
        if (userGroup == null || !userGroup.getGroupOwnerId().equals(groupId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        String[] contactIdList = selectContacts.split(",");
        for (String contactId : contactIdList) {
            if (Constants.ZERO.equals(opType)) {
                // 移除
                userGroupService.leaveGroup(contactId, groupId, SendMessageTypeEnum.REMOVE_GROUP);
            }
            if (Constants.ONE.equals(opType)) {
                // 添加
                userContactService.addContact(contactId, null, groupId, UserContactTypeEnum.GROUP.getType(), null);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void leaveGroup(String userId, String groupId, SendMessageTypeEnum messageTypeEnum) {
        UserGroup userGroup = userGroupMapper.selectById(groupId);
        if (userGroup == null || !userGroup.getGroupOwnerId().equals(groupId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        Integer i = userContactMapper.deleteByUserIdAndContactId(userGroup.getId(), groupId);
        if (i == 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        User user = userMapper.selectById(userId);

        String sessionId = StringTools.generatorChatSessionId4Group(groupId);
        Date date = new Date();
        String messageContent = String.format(messageTypeEnum.getInitMessage(), user.getNickName());

        ChatSession chatSession = new ChatSession();
        chatSession.setLastMessage(messageContent);
        chatSession.setLastReceiveTime(date.getTime());
        chatSessionMapper.updateById(chatSession, sessionId);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionId(sessionId);
        chatMessage.setSendTime(date.getTime());
        chatMessage.setContactType(UserContactTypeEnum.GROUP.getType());
        chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus());
        chatMessage.setMessageType(messageTypeEnum.getType());
        chatMessage.setContactId(groupId);
        chatMessage.setMessageContent(messageContent);
        chatMessageMapper.insert(chatMessage);

        UserContactDto userContactDto = new UserContactDto();
        userContactDto.setContactId(groupId);
        userContactDto.setStatus(UserContactStatusEnum.FRIEND.getStatus());
        Integer memberCount = userContactMapper.selectCount(userContactDto);

        MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
        messageSendDto.setExtendData(userId);
        messageSendDto.setMemberCount(memberCount);
        messageHandler.sendMessage(messageSendDto);
    }
}