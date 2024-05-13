package org.chc.ezim.service;

import java.util.List;

import org.chc.ezim.entity.dto.UserContactApplyDto;
import org.chc.ezim.entity.model.UserContactApply;
import org.chc.ezim.entity.vo.PaginationResultVO;


/**
 * 联系人申请 业务接口
 */
public interface UserContactApplyService {

	/**
	 * 根据条件查询列表
	 */
	List<UserContactApply> findListByParam(UserContactApplyDto param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(UserContactApplyDto param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserContactApply> findListByPage(UserContactApplyDto param);

	/**
	 * 新增
	 */
	Integer add(UserContactApply bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserContactApply> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserContactApply> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(UserContactApply bean,UserContactApplyDto param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(UserContactApplyDto param);

	/**
	 * 根据Id查询对象
	 */
	UserContactApply getUserContactApplyById(Integer id);


	/**
	 * 根据Id修改
	 */
	Integer updateUserContactApplyById(UserContactApply bean,Integer id);


	/**
	 * 根据Id删除
	 */
	Integer deleteUserContactApplyById(Integer id);


	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId查询对象
	 */
	UserContactApply getUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId,String receiveUserId,String contactId);


	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId修改
	 */
	Integer updateUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(UserContactApply bean,String applyUserId,String receiveUserId,String contactId);


	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId删除
	 */
	Integer deleteUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId,String receiveUserId,String contactId);

}