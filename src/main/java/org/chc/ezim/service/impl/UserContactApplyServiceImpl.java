package org.chc.ezim.service.impl;

import java.util.List;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.dto.SimplePage;
import org.chc.ezim.entity.dto.UserContactApplyDto;
import org.chc.ezim.mapper.UserContactApplyMapper;
import org.springframework.stereotype.Service;

import org.chc.ezim.entity.enums.PageSize;
import org.chc.ezim.entity.model.UserContactApply;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.service.UserContactApplyService;
import org.chc.ezim.utils.StringTools;


/**
 * 联系人申请 业务接口实现
 */
@Service("userContactApplyService")
public class UserContactApplyServiceImpl implements UserContactApplyService {

	@Resource
	private UserContactApplyMapper<UserContactApply, UserContactApplyDto> userContactApplyMapper;

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

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
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
}