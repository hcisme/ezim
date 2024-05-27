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
import org.chc.ezim.entity.model.User;
import org.chc.ezim.entity.model.UserContact;
import org.chc.ezim.entity.model.UserContactApply;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.entity.vo.UserContactSearchResultVO;
import org.chc.ezim.entity.vo.UserVo;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.service.UserContactApplyService;
import org.chc.ezim.service.UserContactService;
import org.chc.ezim.service.UserService;
import org.chc.ezim.utils.CopyTools;
import org.chc.ezim.utils.PinYinUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        userContactApplyService.applyAdd(userInfo, applyAddDto.contactId, applyAddDto.applyInfo);

        return getSuccessResponseVO(null);
    }

    /**
     * 查看申请列表
     */
    @GlobalAccessInterceptor
    @GetMapping("/getApplyList")
    public ResponseVO getApplyList(@RequestHeader("token") String token, @RequestParam(required = false) Integer currentPage, @RequestParam(required = false) Integer pageSize) {
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
     * 处理申请
     */
    @GlobalAccessInterceptor
    @PutMapping("/dealWithApply")
    public ResponseVO dealWithApply(@RequestHeader("token") String token, @Validated @RequestBody DealApplyDto dealApplyDto) {
        TokenUserInfoDto userInfo = getTokenInfo(token);

        userContactApplyService.dealWithApply(userInfo.getId(), dealApplyDto.applyId, dealApplyDto.status);

        return getSuccessResponseVO(null);
    }

    /**
     * 获取联系人列表
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
        } else if (UserContactTypeEnum.GROUP.getType().equals(contactType)) {
            // 我加入的群组
            userContactDto.setQueryGroupInfo(true);
            userContactDto.setExcludeMyGroup(true);
        }
        userContactDto.setOrderBy("last_update_time desc");
        userContactDto.setStatusArray(new Integer[]{
                UserContactStatusEnum.FRIEND.getStatus(),
                UserContactStatusEnum.DEL_BE.getStatus(),
                UserContactStatusEnum.BLACKLIST_BE.getStatus(),
        });

        List<UserContact> userContactList = userContactService.findListByParam(userContactDto);

        // 处理数据根据拼音分组
//        Map<String, List<UserContact>> categorizedContacts = new HashMap<>();
//
//        for (UserContact contact : userContactList) {
//            String initial = PinYinUtil.getPinyinInitial(contact.getContactName());
//            categorizedContacts.computeIfAbsent(initial, k -> new ArrayList<>()).add(contact);
//        }
//        Map<String, List<UserContact>> categoryContactList = categorizedContacts.entrySet()
//                .stream()
//                .sorted(Map.Entry.comparingByKey())
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        Map.Entry::getValue,
//                        (e1, e2) -> e1,
//                        HashMap::new
//                ));

        return getSuccessResponseVO(userContactList);
    }

    /**
     * 获取单个联系人详情（不是好友也可以获取）
     */
    @GlobalAccessInterceptor
    @GetMapping("/getContactUserInfo")
    public ResponseVO getContactUserInfo(@RequestHeader("token") String token, @NotNull String contactId) {
        TokenUserInfoDto userInfo = getTokenInfo(token);

        User user = userService.getUserById(contactId);
        if (user == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        UserVo userVo = CopyTools.copy(user, UserVo.class);
        userVo.setContactStatus(UserContactStatusEnum.NOT_FRIEND.getStatus());

        // 是否为好友
        UserContact userContact = userContactService.getUserContactByUserIdAndContactId(userInfo.getId(), contactId);
        if (userContact != null) {
            userVo.setContactStatus(UserContactStatusEnum.FRIEND.getStatus());
        }

        return getSuccessResponseVO(userVo);
    }

    /**
     * 删除联系人
     */
    @GlobalAccessInterceptor
    @DeleteMapping("/delete/{contactId}")
    public ResponseVO deleteContactUser(@RequestHeader("token") String token, @PathVariable String contactId) {
        TokenUserInfoDto userInfo = getTokenInfo(token);
        userContactService.removeContactUser(userInfo.getId(), contactId, UserContactStatusEnum.DEL);
        return getSuccessResponseVO(null);
    }

    /**
     * 拉黑联系人
     */
    @GlobalAccessInterceptor
    @DeleteMapping("/addBlacklist/{contactId}")
    public ResponseVO addBlacklist(@RequestHeader("token") String token, @PathVariable String contactId) {
        TokenUserInfoDto userInfo = getTokenInfo(token);
        userContactService.removeContactUser(userInfo.getId(), contactId, UserContactStatusEnum.BLACKLIST);
        return getSuccessResponseVO(null);
    }
}