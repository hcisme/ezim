package org.chc.ezim.service.impl;

import java.util.List;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.dto.ChatSessionUserDto;
import org.chc.ezim.entity.dto.MessageSendDto;
import org.chc.ezim.entity.dto.SimplePage;
import org.chc.ezim.entity.dto.UserContactDto;
import org.chc.ezim.entity.enums.SendMessageTypeEnum;
import org.chc.ezim.entity.enums.UserContactTypeEnum;
import org.chc.ezim.entity.model.UserContact;
import org.chc.ezim.mapper.ChatSessionUserMapper;
import org.chc.ezim.mapper.UserContactMapper;
import org.chc.ezim.websocket.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.chc.ezim.entity.enums.PageSize;
import org.chc.ezim.entity.model.ChatSessionUser;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.service.ChatSessionUserService;
import org.chc.ezim.utils.StringTools;


/**
 * 会话用户 业务接口实现
 */
@Service("chatSessionUserService")
public class ChatSessionUserServiceImpl implements ChatSessionUserService {

    @Resource
    private ChatSessionUserMapper<ChatSessionUser, ChatSessionUserDto> chatSessionUserMapper;

    @Resource
    private MessageHandler messageHandler;

    @Resource
    private UserContactMapper<UserContact, UserContactDto> userContactMapper;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<ChatSessionUser> findListByParam(ChatSessionUserDto param) {
        return this.chatSessionUserMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(ChatSessionUserDto param) {
        return this.chatSessionUserMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<ChatSessionUser> findListByPage(ChatSessionUserDto param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPage(), count, pageSize);
        param.setSimplePage(page);
        List<ChatSessionUser> list = this.findListByParam(param);
        PaginationResultVO<ChatSessionUser> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(ChatSessionUser bean) {
        return this.chatSessionUserMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<ChatSessionUser> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.chatSessionUserMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<ChatSessionUser> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.chatSessionUserMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(ChatSessionUser bean, ChatSessionUserDto param) {
        StringTools.checkParam(param);
        return this.chatSessionUserMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(ChatSessionUserDto param) {
        StringTools.checkParam(param);
        return this.chatSessionUserMapper.deleteByParam(param);
    }

    /**
     * 根据UserIdAndContactId获取对象
     */
    @Override
    public ChatSessionUser getChatSessionUserByUserIdAndContactId(String userId, String contactId) {
        return this.chatSessionUserMapper.selectByUserIdAndContactId(userId, contactId);
    }

    /**
     * 根据UserIdAndContactId修改
     */
    @Override
    public Integer updateChatSessionUserByUserIdAndContactId(ChatSessionUser bean, String userId, String contactId) {
        return this.chatSessionUserMapper.updateByUserIdAndContactId(bean, userId, contactId);
    }

    /**
     * 根据UserIdAndContactId删除
     */
    @Override
    public Integer deleteChatSessionUserByUserIdAndContactId(String userId, String contactId) {
        return this.chatSessionUserMapper.deleteByUserIdAndContactId(userId, contactId);
    }

    /**
     * 更新冗余信息
     */
    @Override
    public void updateRedundancyInfo(String contactName, String contactId) {
        ChatSessionUser updateInfo = new ChatSessionUser();
        updateInfo.setContactName(contactName);
        ChatSessionUserDto chatSessionUserDto = new ChatSessionUserDto();
        chatSessionUserDto.setContactId(contactId);
        chatSessionUserMapper.updateByParam(updateInfo, chatSessionUserDto);

        UserContactTypeEnum contactTypeEnum = UserContactTypeEnum.getByPrefix(contactId);
        if (contactTypeEnum == UserContactTypeEnum.GROUP) {
            // 修改群昵称后 发送 ws 消息
            MessageSendDto<Object> messageSendDto = new MessageSendDto<>();
            messageSendDto.setContactType(contactTypeEnum.getType());
            messageSendDto.setContactId(contactId);
            messageSendDto.setExtendData(contactName);
            messageSendDto.setMessageType(SendMessageTypeEnum.CONTACT_NAME_UPDATE.getType());
            messageHandler.sendMessage(messageSendDto);
        } else {
            UserContactDto userContactDto = new UserContactDto();
            userContactDto.setContactId(contactId);
            userContactDto.setContactType(UserContactTypeEnum.USER.getType());
            List<UserContact> contactList = userContactMapper.selectList(userContactDto);
            for (var userContact : contactList) {
                MessageSendDto<Object> messageSendDto = new MessageSendDto<>();
                messageSendDto.setContactType(contactTypeEnum.getType());
                messageSendDto.setContactId(userContact.getUserId());
                messageSendDto.setExtendData(contactName);
                messageSendDto.setMessageType(SendMessageTypeEnum.CONTACT_NAME_UPDATE.getType());
                messageSendDto.setSendUserId(contactId);
                messageSendDto.setSendUserNickName(contactName);
                messageHandler.sendMessage(messageSendDto);
            }
        }
    }
}