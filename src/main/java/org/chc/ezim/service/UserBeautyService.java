package org.chc.ezim.service;

import java.util.List;

import org.chc.ezim.entity.dto.UserBeautyDto;
import org.chc.ezim.entity.model.UserBeauty;
import org.chc.ezim.entity.vo.PaginationResultVO;


/**
 * 靓号表 业务接口
 */
public interface UserBeautyService {

	/**
	 * 根据条件查询列表
	 */
	List<UserBeauty> findListByParam(UserBeautyDto param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(UserBeautyDto param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserBeauty> findListByPage(UserBeautyDto param);

	/**
	 * 新增
	 */
	Integer add(UserBeauty bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserBeauty> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserBeauty> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(UserBeauty bean, UserBeautyDto param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(UserBeautyDto param);

	/**
	 * 根据Id查询对象
	 */
	UserBeauty getUserBeautyById(Integer id);


	/**
	 * 根据Id修改
	 */
	Integer updateUserBeautyById(UserBeauty bean,Integer id);


	/**
	 * 根据Id删除
	 */
	Integer deleteUserBeautyById(Integer id);


	/**
	 * 根据UserId查询对象
	 */
	UserBeauty getUserBeautyByUserId(String userId);


	/**
	 * 根据UserId修改
	 */
	Integer updateUserBeautyByUserId(UserBeauty bean,String userId);


	/**
	 * 根据UserId删除
	 */
	Integer deleteUserBeautyByUserId(String userId);


	/**
	 * 根据Email查询对象
	 */
	UserBeauty getUserBeautyByEmail(String email);


	/**
	 * 根据Email修改
	 */
	Integer updateUserBeautyByEmail(UserBeauty bean,String email);


	/**
	 * 根据Email删除
	 */
	Integer deleteUserBeautyByEmail(String email);

}