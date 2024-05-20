package org.chc.ezim.controller;

import java.util.List;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.dto.ChatSessionDto;
import org.chc.ezim.entity.model.ChatSession;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.service.ChatSessionService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会话信息 Controller
 */
@RestController("chatSessionController")
@RequestMapping("/chatSession")
public class ChatSessionController extends ABaseController{

	@Resource
	private ChatSessionService chatSessionService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(ChatSessionDto query){
		return getSuccessResponseVO(chatSessionService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(ChatSession bean) {
		chatSessionService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<ChatSession> listBean) {
		chatSessionService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<ChatSession> listBean) {
		chatSessionService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Id查询对象
	 */
	@RequestMapping("/getChatSessionById")
	public ResponseVO getChatSessionById(String id) {
		return getSuccessResponseVO(chatSessionService.getChatSessionById(id));
	}

	/**
	 * 根据Id修改对象
	 */
	@RequestMapping("/updateChatSessionById")
	public ResponseVO updateChatSessionById(ChatSession bean,String id) {
		chatSessionService.updateChatSessionById(bean,id);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Id删除
	 */
	@RequestMapping("/deleteChatSessionById")
	public ResponseVO deleteChatSessionById(String id) {
		chatSessionService.deleteChatSessionById(id);
		return getSuccessResponseVO(null);
	}
}