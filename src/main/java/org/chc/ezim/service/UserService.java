package org.chc.ezim.service;

import java.util.List;

import org.chc.ezim.entity.dto.UserDto;
import org.chc.ezim.entity.model.User;
import org.chc.ezim.entity.vo.PaginationResultVO;


/**
 * 用户信息 业务接口
 */
public interface UserService {

	/**
	 * 根据条件查询列表
	 */
	List<User> findListByParam(UserDto param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(UserDto param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<User> findListByPage(UserDto param);

	/**
	 * 新增
	 */
	Integer add(User bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<User> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<User> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(User bean, UserDto param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(UserDto param);

	/**
	 * 根据Id查询对象
	 */
	User getUserById(String id);


	/**
	 * 根据Id修改
	 */
	Integer updateUserById(User bean,String id);


	/**
	 * 根据Id删除
	 */
	Integer deleteUserById(String id);


	/**
	 * 根据Email查询对象
	 */
	User getUserByEmail(String email);


	/**
	 * 根据Email修改
	 */
	Integer updateUserByEmail(User bean,String email);


	/**
	 * 根据Email删除
	 */
	Integer deleteUserByEmail(String email);

}