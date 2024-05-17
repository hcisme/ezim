package org.chc.ezim.controller;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.chc.ezim.annotation.GlobalAccessInterceptor;
import org.chc.ezim.entity.dto.*;
import org.chc.ezim.entity.enums.PageSize;
import org.chc.ezim.entity.enums.ResponseCodeEnum;
import org.chc.ezim.entity.enums.UserContactStatusEnum;
import org.chc.ezim.entity.enums.UserContactTypeEnum;
import org.chc.ezim.entity.model.UserContact;
import org.chc.ezim.entity.model.UserContactApply;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.entity.vo.UserContactSearchResultVO;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.service.UserContactApplyService;
import org.chc.ezim.service.UserContactService;
import org.chc.ezim.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 联系人 Controller
 */
@RestController("userContactController")
@RequestMapping("/userContact")
public class UserContactController extends ABaseController {

    @Resource
    private UserContactService userContactService;

    @Resource
    private UserService userService;

    @Resource
    private UserContactApplyService userContactApplyService;

    /**
     * 搜索联系人
     */
    @GlobalAccessInterceptor
    @GetMapping("/search")
    public ResponseVO search(@RequestHeader("token") String token, @NotEmpty String contactId) {
        TokenUserInfoDto userInfo = getTokenInfo(token);

        UserContactSearchResultVO userContactSearchResultVO = userContactService.searchContact(userInfo.getId(), contactId);

        return getSuccessResponseVO(userContactSearchResultVO);
    }

    /**
     * 申请添加为联系人
     */
    @GlobalAccessInterceptor
    @PostMapping("/applyAdd")
    public ResponseVO applyAdd(
            @RequestHeader("token") String token,
            @Validated @RequestBody ApplyAddDto applyAddDto
    ) {
        TokenUserInfoDto userInfo = getTokenInfo(token);

        Integer i = userContactService.applyAdd(userInfo, applyAddDto.contactId, applyAddDto.applyInfo);

        return getSuccessResponseVO(null);
    }

    /**
     * 查看申请列表
     */
    @GlobalAccessInterceptor
    @GetMapping("/getApplyList")
    public ResponseVO getApplyList(@RequestHeader("token") String token, @RequestParam(required = false) Integer currentPage) {
        TokenUserInfoDto userInfo = getTokenInfo(token);

        UserContactApplyDto userContactApplyDto = new UserContactApplyDto();
        userContactApplyDto.setOrderBy("last_apply_time desc");
        userContactApplyDto.setReceiveUserId(userInfo.getId());
        userContactApplyDto.setPage(currentPage);
        userContactApplyDto.setPageSize(PageSize.SIZE15.getSize());
        userContactApplyDto.setQueryContactInfo(true);

        PaginationResultVO<UserContactApply> listByPage = userContactApplyService.findListByPage(userContactApplyDto);

        return getSuccessResponseVO(listByPage);
    }

    /**
     * 查看申请列表
     */
    @GlobalAccessInterceptor
    @PutMapping("/dealWithApply")
    public ResponseVO dealWithApply(@RequestHeader("token") String token, @Validated @RequestBody DealApplyDto dealApplyDto) {
        TokenUserInfoDto userInfo = getTokenInfo(token);

        userContactApplyService.dealWithApply(userInfo.getId(), dealApplyDto.applyId, dealApplyDto.status);

        return getSuccessResponseVO(null);
    }

    /**
     * 获取联系人
     */
    @GlobalAccessInterceptor
    @GetMapping("/getContactList")
    public ResponseVO getContactList(@RequestHeader("token") String token, @NotNull Integer contactType) {
        if (UserContactTypeEnum.getByType(contactType) == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        TokenUserInfoDto userInfo = getTokenInfo(token);
        UserContactDto userContactDto = new UserContactDto();
        userContactDto.setUserId(userInfo.getId());
        userContactDto.setContactType(contactType);
        if (UserContactTypeEnum.USER.getType().equals(contactType)) {
            // 联系人
            userContactDto.setQueryContactUserInfo(true);
        } else if (UserContactTypeEnum.GROUP.getType().equals(contactType)){
            // 我加入的群组
            userContactDto.setQueryGroupInfo(true);
            userContactDto.setExcludeMyGroup(true);
        }
        userContactDto.setOrderBy("last_update_time desc");
        userContactDto.setStatusArray(new Integer[] {
                UserContactStatusEnum.FRIEND.getStatus(),
                UserContactStatusEnum.DEL_BE.getStatus(),
                UserContactStatusEnum.BLACKLIST_BE.getStatus(),
        });

        List<UserContact> listByParam = userContactService.findListByParam(userContactDto);

        return getSuccessResponseVO(listByParam);
    }
}