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
import org.chc.ezim.service.UserContactApplyService;
import org.chc.ezim.service.UserContactService;
import org.chc.ezim.utils.CopyTools;
import org.chc.ezim.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private UserGroupMapper<UserGroup, UserGroupDto> userGroupMapper;

    @Resource
    private UserContactApplyMapper<UserContactApply, UserContactApplyDto> userContactApplyMapper;

    @Resource
    private UserContactApplyService userContactApplyService;

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
     * 添加为好友
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
            userContactApplyService.addContact(
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
}