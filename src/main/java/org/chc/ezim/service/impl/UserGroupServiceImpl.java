package org.chc.ezim.service.impl;

import java.util.List;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.dto.SimplePage;
import org.springframework.stereotype.Service;

import org.chc.ezim.entity.enums.PageSize;
import org.chc.ezim.entity.dto.UserGroupDto;
import org.chc.ezim.entity.model.UserGroup;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.mapper.UserGroupMapper;
import org.chc.ezim.service.UserGroupService;
import org.chc.ezim.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("userGroupService")
public class UserGroupServiceImpl implements UserGroupService {

	@Resource
	private UserGroupMapper<UserGroup, UserGroupDto> userGroupMapper;

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

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<UserGroup> list = this.findListByParam(param);
		PaginationResultVO<UserGroup> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
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
}