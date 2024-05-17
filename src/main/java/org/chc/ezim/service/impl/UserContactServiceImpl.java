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
import org.chc.ezim.entity.vo.UserContactSearchResultVO;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.mapper.UserContactApplyMapper;
import org.chc.ezim.mapper.UserContactMapper;
import org.chc.ezim.mapper.UserGroupMapper;
import org.chc.ezim.mapper.UserMapper;
import org.chc.ezim.redis.RedisComponent;
import org.chc.ezim.service.UserContactApplyService;
import org.chc.ezim.service.UserContactService;
import org.chc.ezim.utils.CopyTools;
import org.chc.ezim.utils.StringTools;
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
    private UserContactApplyMapper<UserContactApply, UserContactApplyDto> userContactApplyMapper;

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
                throw new BusinessException("非法操作");
            }
            joinType = user.getJoinType();
        }

        // 直接加入不用记录申请
        if (JoinTypeEnum.JOIN.getType().equals(joinType)) {
            addContact(
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
            // TODO 发送 ws 消息
        }

        return null;
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

        // TODO 从我的好友列表缓存中删除好友

        // TODO 从好友列表缓存中删除我
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

        // TODO 如果是好友 接收人也添加申请人为好友  添加缓存

        // TODO 创建会话
    }
}