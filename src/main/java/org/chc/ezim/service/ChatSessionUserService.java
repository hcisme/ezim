package org.chc.ezim.service;

import java.util.List;

import org.chc.ezim.entity.dto.ChatSessionUserDto;
import org.chc.ezim.entity.model.ChatSessionUser;
import org.chc.ezim.entity.vo.PaginationResultVO;


/**
 * 会话用户 业务接口
 */
public interface ChatSessionUserService {

	/**
	 * 根据条件查询列表
	 */
	List<ChatSessionUser> findListByParam(ChatSessionUserDto param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(ChatSessionUserDto param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<ChatSessionUser> findListByPage(ChatSessionUserDto param);

	/**
	 * 新增
	 */
	Integer add(ChatSessionUser bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<ChatSessionUser> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<ChatSessionUser> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(ChatSessionUser bean, ChatSessionUserDto param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(ChatSessionUserDto param);

	/**
	 * 根据UserIdAndContactId查询对象
	 */
	ChatSessionUser getChatSessionUserByUserIdAndContactId(String userId,String contactId);


	/**
	 * 根据UserIdAndContactId修改
	 */
	Integer updateChatSessionUserByUserIdAndContactId(ChatSessionUser bean,String userId,String contactId);


	/**
	 * 根据UserIdAndContactId删除
	 */
	Integer deleteChatSessionUserByUserIdAndContactId(String userId,String contactId);

}