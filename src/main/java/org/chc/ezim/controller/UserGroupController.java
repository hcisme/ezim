package org.chc.ezim.controller;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.chc.ezim.annotation.GlobalAccessInterceptor;
import org.chc.ezim.entity.dto.TokenUserInfoDto;
import org.chc.ezim.entity.dto.UserGroupDto;
import org.chc.ezim.entity.dto.UserGroupQueryDto;
import org.chc.ezim.entity.model.UserGroup;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.service.UserGroupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 群组 Controller
 */
@RestController("userGroupController")
@RequestMapping("/group")
public class UserGroupController extends ABaseController {

    @Resource
    private UserGroupService userGroupService;

    /**
     * 获取创建的群组
     */
    @GlobalAccessInterceptor
    @GetMapping("/select")
    public ResponseVO select(@RequestHeader("token") String token) {
        TokenUserInfoDto userInfo = getTokenInfo(token);
        UserGroupDto userGroupDto = new UserGroupDto();
        userGroupDto.setGroupOwnerId(userInfo.getId());
        userGroupDto.setOrderBy("create_time desc");
        List<UserGroup> list = userGroupService.findListByParam(userGroupDto);
        return getSuccessResponseVO(list);
    }

    /**
     * 创建群组
     */
    @GlobalAccessInterceptor
    @PostMapping("/create")
    public ResponseVO create(
            @RequestHeader("token") String token,
            @Validated(UserGroupQueryDto.create.class) @ModelAttribute UserGroupQueryDto userGroupQuery,
            @RequestPart("avatarFile") @NotNull MultipartFile avatarFile,
            @RequestPart("avatarCover") @NotNull MultipartFile avatarCover
    ) throws IOException {
        TokenUserInfoDto userInfo = getTokenInfo(token);
        UserGroup userGroup = new UserGroup(userGroupQuery.getId(), userGroupQuery.getGroupName(), userInfo.getId(),
                userGroupQuery.getGroupNotice(), userGroupQuery.getJoinType());

        userGroupService.createOrUpdateGroup(userGroup, avatarFile, avatarCover);

        return getSuccessResponseVO(null);
    }

    /**
     * 更新群组
     */
    @GlobalAccessInterceptor
    @PutMapping("/update")
    public ResponseVO update(
            @RequestHeader("token") String token,
            @Validated(UserGroupQueryDto.update.class) @ModelAttribute UserGroupQueryDto userGroupQuery,
            @RequestPart(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestPart(value = "avatarCover", required = false) MultipartFile avatarCover
    ) throws IOException {
        TokenUserInfoDto userInfo = getTokenInfo(token);
        UserGroup userGroup = new UserGroup(userGroupQuery.getId(), userGroupQuery.getGroupName(), userInfo.getId(),
                userGroupQuery.getGroupNotice(), userGroupQuery.getJoinType());

        userGroupService.createOrUpdateGroup(userGroup, avatarFile, avatarCover);

        return getSuccessResponseVO(null);
    }
}