package org.chc.ezim.service;

import java.util.List;

import org.chc.ezim.entity.dto.ChatSessionDto;
import org.chc.ezim.entity.model.ChatSession;
import org.chc.ezim.entity.vo.PaginationResultVO;


/**
 * 会话信息 业务接口
 */
public interface ChatSessionService {

	/**
	 * 根据条件查询列表
	 */
	List<ChatSession> findListByParam(ChatSessionDto param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(ChatSessionDto param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<ChatSession> findListByPage(ChatSessionDto param);

	/**
	 * 新增
	 */
	Integer add(ChatSession bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<ChatSession> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<ChatSession> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(ChatSession bean, ChatSessionDto param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(ChatSessionDto param);

	/**
	 * 根据Id查询对象
	 */
	ChatSession getChatSessionById(String id);


	/**
	 * 根据Id修改
	 */
	Integer updateChatSessionById(ChatSession bean,String id);


	/**
	 * 根据Id删除
	 */
	Integer deleteChatSessionById(String id);

}