package org.chc.ezim.controller;

import java.util.List;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.dto.UserGroupDto;
import org.chc.ezim.entity.model.UserGroup;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.service.UserGroupService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  Controller
 */
@RestController("userGroupController")
@RequestMapping("/userGroup")
public class UserGroupController extends ABaseController{

	@Resource
	private UserGroupService userGroupService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(UserGroupDto query){
		return getSuccessResponseVO(userGroupService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(UserGroup bean) {
		userGroupService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<UserGroup> listBean) {
		userGroupService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<UserGroup> listBean) {
		userGroupService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Id查询对象
	 */
	@RequestMapping("/getUserGroupById")
	public ResponseVO getUserGroupById(String id) {
		return getSuccessResponseVO(userGroupService.getUserGroupById(id));
	}

	/**
	 * 根据Id修改对象
	 */
	@RequestMapping("/updateUserGroupById")
	public ResponseVO updateUserGroupById(UserGroup bean,String id) {
		userGroupService.updateUserGroupById(bean,id);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Id删除
	 */
	@RequestMapping("/deleteUserGroupById")
	public ResponseVO deleteUserGroupById(String id) {
		userGroupService.deleteUserGroupById(id);
		return getSuccessResponseVO(null);
	}
}