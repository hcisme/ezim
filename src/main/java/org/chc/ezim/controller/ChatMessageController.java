package org.chc.ezim.controller;

import java.util.List;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.dto.ChatMessageDto;
import org.chc.ezim.entity.model.ChatMessage;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.service.ChatMessageService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 聊天消息表 Controller
 */
@RestController("chatMessageController")
@RequestMapping("/chatMessage")
public class ChatMessageController extends ABaseController{

	@Resource
	private ChatMessageService chatMessageService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(ChatMessageDto query){
		return getSuccessResponseVO(chatMessageService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(ChatMessage bean) {
		chatMessageService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<ChatMessage> listBean) {
		chatMessageService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<ChatMessage> listBean) {
		chatMessageService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Id查询对象
	 */
	@RequestMapping("/getChatMessageById")
	public ResponseVO getChatMessageById(Long id) {
		return getSuccessResponseVO(chatMessageService.getChatMessageById(id));
	}

	/**
	 * 根据Id修改对象
	 */
	@RequestMapping("/updateChatMessageById")
	public ResponseVO updateChatMessageById(ChatMessage bean,Long id) {
		chatMessageService.updateChatMessageById(bean,id);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Id删除
	 */
	@RequestMapping("/deleteChatMessageById")
	public ResponseVO deleteChatMessageById(Long id) {
		chatMessageService.deleteChatMessageById(id);
		return getSuccessResponseVO(null);
	}
}