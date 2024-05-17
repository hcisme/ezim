package org.chc.ezim.controller;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.chc.ezim.annotation.GlobalAccessInterceptor;
import org.chc.ezim.entity.dto.TokenUserInfoDto;
import org.chc.ezim.entity.dto.UserContactDto;
import org.chc.ezim.entity.dto.UserGroupDto;
import org.chc.ezim.entity.dto.UserGroupQueryDto;
import org.chc.ezim.entity.enums.GroupStatusEnum;
import org.chc.ezim.entity.enums.UserContactStatusEnum;
import org.chc.ezim.entity.model.UserContact;
import org.chc.ezim.entity.model.UserGroup;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.entity.vo.UserGroupVO;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.service.UserContactService;
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

    @Resource
    private UserContactService userContactService;

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

    /**
     * 获取群组详情
     */
    @GlobalAccessInterceptor
    @GetMapping("/getDetail")
    public ResponseVO getDetail(@RequestHeader("token") String token, @NotEmpty String groupId) {
        UserGroup userGroup = getGroupDetail(token, groupId);

        // 获取成员数
        UserContactDto userContactDto = new UserContactDto();
        userContactDto.setContactId(groupId);
        Integer memberCount = userContactService.findCountByParam(userContactDto);
        userGroup.setMemberCount(memberCount);

        return getSuccessResponseVO(userGroup);
    }

    /**
     * 获取会话中群组的详情
     */
    @GlobalAccessInterceptor
    @GetMapping("/getDetailChat")
    public ResponseVO getDetailChat(@RequestHeader("token") String token, @NotEmpty String groupId) {
        UserGroup userGroup = getGroupDetail(token, groupId);

        // 获取具体联系人
        UserContactDto userContactDto = new UserContactDto();
        userContactDto.setContactId(groupId);
        userContactDto.setQueryUserInfo(true);
        userContactDto.setOrderBy("create_time asc");
        userContactDto.setStatus(UserContactStatusEnum.FRIEND.getStatus());
        List<UserContact> list = userContactService.findListByParam(userContactDto);

        UserGroupVO userGroupVO = new UserGroupVO(userGroup, list);

        return getSuccessResponseVO(userGroupVO);
    }

    private UserGroup getGroupDetail(String token, String groupId) {
        TokenUserInfoDto userInfo = getTokenInfo(token);
        UserContact peopleInCurrentGroup = userContactService.getUserContactByUserIdAndContactId(userInfo.getId(), groupId);
        // 人不在群组中 || 状态不是好友
        if (peopleInCurrentGroup == null || !UserContactStatusEnum.FRIEND.getStatus().equals(peopleInCurrentGroup.getStatus())) {
            throw new BusinessException("你不在群组中或者群聊不存在或者已解散");
        }

        UserGroup userGroup = userGroupService.getUserGroupById(groupId);
        if (userGroup == null || !GroupStatusEnum.NORMAL.getStatus().equals(userGroup.getStatus())) {
            throw new BusinessException("群聊不存在或者已解散");
        }
        return userGroup;
    }
}