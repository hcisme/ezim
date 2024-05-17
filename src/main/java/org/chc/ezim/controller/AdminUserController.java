package org.chc.ezim.controller;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.chc.ezim.annotation.GlobalAccessInterceptor;
import org.chc.ezim.entity.dto.UserDto;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.service.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员 Controller
 */
@RestController
@RequestMapping("/admin")
public class AdminUserController extends ABaseController {

    @Resource
    private UserService userService;

    /**
     * 获取用户列表
     */
    @GlobalAccessInterceptor(checkAdminAccess = true)
    @GetMapping("/loadUserList")
    public ResponseVO loadUserList(@ModelAttribute UserDto userDto) {
        userDto.setOrderBy("create_time desc");
        PaginationResultVO resultVO = userService.findListByPage(userDto);

        return getSuccessResponseVO(resultVO);
    }

    /**
     * 更新用户状态
     */
    @GlobalAccessInterceptor(checkAdminAccess = true)
    @PutMapping("/updateUserStatus")
    public ResponseVO updateUserStatus(@RequestParam @NotNull Integer status, @RequestParam @NotEmpty String userId) {
        userService.updateUserStatus(status, userId);

        return getSuccessResponseVO(null);
    }

    /**
     * 强制用户下线
     */
    @GlobalAccessInterceptor(checkAdminAccess = true)
    @PostMapping("/forceOffLine")
    public ResponseVO forceOffLine(@RequestParam @NotEmpty String userId) {
        userService.forceOffLine(userId);

        return getSuccessResponseVO(null);
    }
}
