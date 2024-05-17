package org.chc.ezim.controller;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import org.chc.ezim.annotation.GlobalAccessInterceptor;
import org.chc.ezim.entity.dto.UserGroupDto;
import org.chc.ezim.entity.enums.ResponseCodeEnum;
import org.chc.ezim.entity.model.UserGroup;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.service.UserGroupService;
import org.springframework.web.bind.annotation.*;

/**
 * 群组 Controller
 */
@RestController
@RequestMapping("/adminGroup")
public class AdminUserGroupController extends ABaseController {

    @Resource
    private UserGroupService userGroupService;

    /**
     * 获取群组列表
     */
    @GlobalAccessInterceptor(checkAdminAccess = true)
    @GetMapping("/list")
    public ResponseVO getList(@ModelAttribute UserGroupDto userGroupDto) {
        userGroupDto.setOrderBy("create_time desc");
        userGroupDto.setQueryGroupOwnerName(true);
        userGroupDto.setQueryMemebrCount(true);
        PaginationResultVO resultVO = userGroupService.findListByPage(userGroupDto);

        return getSuccessResponseVO(resultVO);
    }

    /**
     * 解散群
     */
    @GlobalAccessInterceptor(checkAdminAccess = true)
    @PostMapping("/dissolve")
    public ResponseVO dissolveGroup(@RequestParam @NotEmpty String groupId) {
        UserGroup userGroup = userGroupService.getUserGroupById(groupId);
        if (userGroup == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        userGroupService.dissolve(userGroup.getGroupOwnerId(), groupId);

        return getSuccessResponseVO(null);
    }
}