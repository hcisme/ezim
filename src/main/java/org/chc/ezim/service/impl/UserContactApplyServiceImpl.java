package org.chc.ezim.service.impl;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.dto.SimplePage;
import org.chc.ezim.entity.dto.UserContactApplyDto;
import org.chc.ezim.entity.dto.UserContactDto;
import org.chc.ezim.entity.enums.PageSize;
import org.chc.ezim.entity.enums.ResponseCodeEnum;
import org.chc.ezim.entity.enums.UserContactApplyStatusEnum;
import org.chc.ezim.entity.enums.UserContactStatusEnum;
import org.chc.ezim.entity.model.UserContact;
import org.chc.ezim.entity.model.UserContactApply;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.mapper.UserContactApplyMapper;
import org.chc.ezim.mapper.UserContactMapper;
import org.chc.ezim.service.UserContactApplyService;
import org.chc.ezim.service.UserContactService;
import org.chc.ezim.utils.StringTools;
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