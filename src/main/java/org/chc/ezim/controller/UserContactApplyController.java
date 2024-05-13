package org.chc.ezim.controller;

import java.util.List;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.dto.UserContactApplyDto;
import org.chc.ezim.entity.model.UserContactApply;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.service.UserContactApplyService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 联系人申请 Controller
 */
@RestController("userContactApplyController")
@RequestMapping("/userContactApply")
public class UserContactApplyController extends ABaseController{

	@Resource
	private UserContactApplyService userContactApplyService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(UserContactApplyDto query){
		return getSuccessResponseVO(userContactApplyService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(UserContactApply bean) {
		userContactApplyService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<UserContactApply> listBean) {
		userContactApplyService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<UserContactApply> listBean) {
		userContactApplyService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Id查询对象
	 */
	@RequestMapping("/getUserContactApplyById")
	public ResponseVO getUserContactApplyById(Integer id) {
		return getSuccessResponseVO(userContactApplyService.getUserContactApplyById(id));
	}

	/**
	 * 根据Id修改对象
	 */
	@RequestMapping("/updateUserContactApplyById")
	public ResponseVO updateUserContactApplyById(UserContactApply bean,Integer id) {
		userContactApplyService.updateUserContactApplyById(bean,id);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Id删除
	 */
	@RequestMapping("/deleteUserContactApplyById")
	public ResponseVO deleteUserContactApplyById(Integer id) {
		userContactApplyService.deleteUserContactApplyById(id);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId查询对象
	 */
	@RequestMapping("/getUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId")
	public ResponseVO getUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId,String receiveUserId,String contactId) {
		return getSuccessResponseVO(userContactApplyService.getUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(applyUserId,receiveUserId,contactId));
	}

	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId修改对象
	 */
	@RequestMapping("/updateUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId")
	public ResponseVO updateUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(UserContactApply bean,String applyUserId,String receiveUserId,String contactId) {
		userContactApplyService.updateUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(bean,applyUserId,receiveUserId,contactId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId删除
	 */
	@RequestMapping("/deleteUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId")
	public ResponseVO deleteUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId,String receiveUserId,String contactId) {
		userContactApplyService.deleteUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(applyUserId,receiveUserId,contactId);
		return getSuccessResponseVO(null);
	}
}