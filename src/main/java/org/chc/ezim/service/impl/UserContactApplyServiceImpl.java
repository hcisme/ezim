package org.chc.ezim.service.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.ArrayUtils;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.entity.dto.*;
import org.chc.ezim.entity.enums.*;
import org.chc.ezim.entity.model.User;
import org.chc.ezim.entity.model.UserContact;
import org.chc.ezim.entity.model.UserContactApply;
import org.chc.ezim.entity.model.UserGroup;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.mapper.UserContactApplyMapper;
import org.chc.ezim.mapper.UserContactMapper;
import org.chc.ezim.mapper.UserGroupMapper;
import org.chc.ezim.mapper.UserMapper;
import org.chc.ezim.service.UserContactApplyService;
import org.chc.ezim.service.UserContactService;
import org.chc.ezim.utils.StringTools;
import org.chc.ezim.websocket.MessageHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * 联系人申请 业务接口实现
 */
@Service("userContactApplyService")
public class UserContactApplyServiceImpl implements UserContactApplyService {

    @Resource
    private UserContactApplyMapper<UserContactApply, UserContactApplyDto> userContactApplyMapper;

    @Resource
    private UserContactMapper<UserContact, UserContactDto> userContactMapper;

    @Resource
    private UserContactService userContactService;

    @Resource
    private UserMapper<User, UserDto> userMapper;

    @Resource
    private UserGroupMapper<UserGroup, UserGroupDto> userGroupMapper;

    @Resource
    private MessageHandler messageHandler;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<UserContactApply> findListByParam(UserContactApplyDto param) {
        return this.userContactApplyMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(UserContactApplyDto param) {
        return this.userContactApplyMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<UserContactApply> findListByPage(UserContactApplyDto param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPage(), count, pageSize);
        param.setSimplePage(page);
        List<UserContactApply> list = this.findListByParam(param);
        PaginationResultVO<UserContactApply> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(UserContactApply bean) {
        return this.userContactApplyMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<UserContactApply> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userContactApplyMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<UserContactApply> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userContactApplyMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(UserContactApply bean, UserContactApplyDto param) {
        StringTools.checkParam(param);
        return this.userContactApplyMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(UserContactApplyDto param) {
        StringTools.checkParam(param);
        return this.userContactApplyMapper.deleteByParam(param);
    }

    /**
     * 根据Id获取对象
     */
    @Override
    public UserContactApply getUserContactApplyById(Integer id) {
        return this.userContactApplyMapper.selectById(id);
    }

    /**
     * 根据Id修改
     */
    @Override
    public Integer updateUserContactApplyById(UserContactApply bean, Integer id) {
        return this.userContactApplyMapper.updateById(bean, id);
    }

    /**
     * 根据Id删除
     */
    @Override
    public Integer deleteUserContactApplyById(Integer id) {
        return this.userContactApplyMapper.deleteById(id);
    }

    /**
     * 根据ApplyUserIdAndReceiveUserIdAndContactId获取对象
     */
    @Override
    public UserContactApply getUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId, String receiveUserId, String contactId) {
        return this.userContactApplyMapper.selectByApplyUserIdAndReceiveUserIdAndContactId(applyUserId, receiveUserId, contactId);
    }

    /**
     * 根据ApplyUserIdAndReceiveUserIdAndContactId修改
     */
    @Override
    public Integer updateUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(UserContactApply bean, String applyUserId, String receiveUserId, String contactId) {
        return this.userContactApplyMapper.updateByApplyUserIdAndReceiveUserIdAndContactId(bean, applyUserId, receiveUserId, contactId);
    }

    /**
     * 根据ApplyUserIdAndReceiveUserIdAndContactId删除
     */
    @Override
    public Integer deleteUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId, String receiveUserId, String contactId) {
        return this.userContactApplyMapper.deleteByApplyUserIdAndReceiveUserIdAndContactId(applyUserId, receiveUserId, contactId);
    }

    /**
     * 申请添加为联系人
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer applyAdd(TokenUserInfoDto userInfo, String contactId, String applyInfo) {
        UserContactTypeEnum userContactTypeEnum = UserContactTypeEnum.getByPrefix(contactId);
        if (userContactTypeEnum == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 申请人id
        String applyUserId = userInfo.getId();
        // 申请人验证信息
        applyInfo = StringTools.isEmpty(applyInfo) ? String.format(Constants.APPLY_INFO_TEMPLATE, userInfo.getNickName()) : applyInfo;
        Long currentTime = System.currentTimeMillis();
        Integer joinType = null;
        String receiveUserId = contactId;

        // 查询对方好友是否已经添加 如果拉黑无法添加
        UserContact userContact = userContactMapper.selectByUserIdAndContactId(applyUserId, receiveUserId);
        // 能查到 但是已经被拉黑
        if (userContact != null
                && ArrayUtils.contains(
                new Integer[]{
                        UserContactStatusEnum.BLACKLIST_BE.getStatus(),
                        UserContactStatusEnum.BLACKLIST_BE_FIRST.getStatus()
                },
                userContact.getStatus()
        )) {
            throw new BusinessException("对方已将你拉黑，无法添加");
        }

        // 添加的是群组
        if (UserContactTypeEnum.GROUP == userContactTypeEnum) {
            UserGroup group = userGroupMapper.selectById(contactId);
            if (group == null || GroupStatusEnum.DISSOLUTION.getStatus().equals(group.getStatus())) {
                throw new BusinessException("群聊不存在或已解散");
            }
            if (group.getGroupOwnerId().equals(userInfo.getId())) {
                throw new BusinessException("非法操作");
            }
            receiveUserId = group.getGroupOwnerId();
            joinType = group.getJoinType();
        } else {
            // 添加的是用户
            User user = userMapper.selectById(contactId);
            if (user == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            if (user.getId().equals(userInfo.getId())) {
                throw new BusinessException("添加者与当前登录用户相同");
            }
            joinType = user.getJoinType();
        }

        // 直接加入不用记录申请
        if (JoinTypeEnum.JOIN.getType().equals(joinType)) {
            userContactService.addContact(
                    applyUserId,
                    receiveUserId,
                    contactId,
                    userContactTypeEnum.getType(),
                    applyInfo
            );
            return joinType;
        }

        UserContactApply userContactApply = userContactApplyMapper.selectByApplyUserIdAndReceiveUserIdAndContactId(applyUserId, receiveUserId, contactId);
        if (userContactApply == null) {
            userContactApplyMapper.insert(new UserContactApply(
                    applyUserId,
                    receiveUserId,
                    userContactTypeEnum.getType(),
                    contactId,
                    UserContactApplyStatusEnum.INIT.getStatus(),
                    applyInfo,
                    currentTime
            ));
        } else {
            // 已经存在 更新状态
            userContactApplyMapper.updateById(
                    new UserContactApply(
                            UserContactApplyStatusEnum.INIT.getStatus(),
                            applyInfo,
                            currentTime
                    ),
                    userContactApply.getId()
            );
        }

        // 第一次发申请
        // 或者 状态为 已同意 已拒绝
        if (userContactApply == null || !UserContactApplyStatusEnum.INIT.getStatus().equals(userContactApply.getStatus())) {
            // 发送 ws 消息
            MessageSendDto<Object> messageSendDto = new MessageSendDto<>();
            messageSendDto.setMessageType(SendMessageTypeEnum.CONTACT_APPLY.getType());
            messageSendDto.setMessageContent(applyInfo);
            messageSendDto.setContactId(receiveUserId);
            messageHandler.sendMessage(messageSendDto);
        }

        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void dealWithApply(String userId, Integer applyId, Integer status) {
        UserContactApplyStatusEnum statusEnum = UserContactApplyStatusEnum.getByStatus(status);
        if (statusEnum == null || UserContactApplyStatusEnum.INIT == statusEnum) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        UserContactApply applyInfo = userContactApplyMapper.selectById(applyId);
        // 检验是否存在 或者 操作人id和接受验证消息的人是否是同一个
        if (applyInfo == null || !userId.equals(applyInfo.getReceiveUserId())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        UserContactApply updateInfo = new UserContactApply();
        updateInfo.setStatus(statusEnum.getStatus());
        updateInfo.setLastApplyTime(System.currentTimeMillis());

        UserContactApplyDto userContactApplyDto = new UserContactApplyDto();
        userContactApplyDto.setId(applyId);
        userContactApplyDto.setStatus(UserContactApplyStatusEnum.INIT.getStatus());

        // 接受 或 拒绝
        Integer count = userContactApplyMapper.updateByParam(updateInfo, userContactApplyDto);
        if (count == 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }

        // 接受
        if (UserContactApplyStatusEnum.PASS == statusEnum) {
            userContactService.addContact(
                    applyInfo.getApplyUserId(),
                    applyInfo.getReceiveUserId(),
                    applyInfo.getContactId(),
                    applyInfo.getContactType(),
                    applyInfo.getApplyInfo()
            );
            return;
        }

        // 拉黑
        if (UserContactApplyStatusEnum.BLACKLIST == statusEnum) {
            Date date = new Date();
            UserContact userContact = new UserContact();
            userContact.setUserId(applyInfo.getApplyUserId());
            userContact.setContactId(applyInfo.getContactId());
            userContact.setContactType(applyInfo.getContactType());
            userContact.setCreateTime(date);
            userContact.setStatus(UserContactStatusEnum.BLACKLIST_BE_FIRST.getStatus());
            userContact.setLastUpdateTime(date);

            userContactMapper.insertOrUpdate(userContact);
        }
    }
}