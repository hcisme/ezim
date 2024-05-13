package org.chc.ezim.service;

import java.util.List;

import org.chc.ezim.entity.dto.UserGroupDto;
import org.chc.ezim.entity.model.UserGroup;
import org.chc.ezim.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface UserGroupService {

	/**
	 * 根据条件查询列表
	 */
	List<UserGroup> findListByParam(UserGroupDto param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(UserGroupDto param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserGroup> findListByPage(UserGroupDto param);

	/**
	 * 新增
	 */
	Integer add(UserGroup bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserGroup> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserGroup> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(UserGroup bean,UserGroupDto param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(UserGroupDto param);

	/**
	 * 根据Id查询对象
	 */
	UserGroup getUserGroupById(String id);


	/**
	 * 根据Id修改
	 */
	Integer updateUserGroupById(UserGroup bean,String id);


	/**
	 * 根据Id删除
	 */
	Integer deleteUserGroupById(String id);

}