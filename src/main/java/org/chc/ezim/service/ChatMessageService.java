package org.chc.ezim.service;

import java.io.File;
import java.util.List;

import org.chc.ezim.entity.dto.ChatMessageDto;
import org.chc.ezim.entity.dto.MessageSendDto;
import org.chc.ezim.entity.dto.TokenUserInfoDto;
import org.chc.ezim.entity.model.ChatMessage;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.springframework.web.multipart.MultipartFile;


/**
 * 聊天消息表 业务接口
 */
public interface ChatMessageService {

	/**
	 * 根据条件查询列表
	 */
	List<ChatMessage> findListByParam(ChatMessageDto param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(ChatMessageDto param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<ChatMessage> findListByPage(ChatMessageDto param);

	/**
	 * 新增
	 */
	Integer add(ChatMessage bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<ChatMessage> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<ChatMessage> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(ChatMessage bean, ChatMessageDto param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(ChatMessageDto param);

	/**
	 * 根据Id查询对象
	 */
	ChatMessage getChatMessageById(Long id);


	/**
	 * 根据Id修改
	 */
	Integer updateChatMessageById(ChatMessage bean,Long id);


	/**
	 * 根据Id删除
	 */
	Integer deleteChatMessageById(Long id);

	MessageSendDto saveMessage(ChatMessage chatMessage, TokenUserInfoDto tokenUserInfoDto);

	void saveMessageFile(String userId, Long messageId, MultipartFile file, MultipartFile cover);

	File downloadFile(TokenUserInfoDto tokenUserInfoDto, Long fileId, Boolean showCover);
}