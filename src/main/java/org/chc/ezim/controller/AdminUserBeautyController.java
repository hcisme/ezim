package org.chc.ezim.controller;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.chc.ezim.annotation.GlobalAccessInterceptor;
import org.chc.ezim.entity.dto.UserBeautyDto;
import org.chc.ezim.entity.model.UserBeauty;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.service.UserBeautyService;
import org.chc.ezim.service.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * 靓号表 Controller
 */
@RestController
@RequestMapping("/admin/userBeauty")
public class AdminUserBeautyController extends ABaseController {

    @Resource
    private UserBeautyService userBeautyService;

    @Resource
    private UserService userService;

    /**
     * 获取靓号列表
     */
    @GlobalAccessInterceptor(checkAdminAccess = true)
    @GetMapping("/list")
    public ResponseVO getList(@ModelAttribute UserBeautyDto userBeautyDto) {
        userBeautyDto.setOrderBy("create_time desc");
        PaginationResultVO resultVO = userBeautyService.findListByPage(userBeautyDto);

        return getSuccessResponseVO(resultVO);
    }

    /**
     * 创建靓号
     */
    @GlobalAccessInterceptor(checkAdminAccess = true)
    @PostMapping("/create")
    public ResponseVO create(@RequestBody UserBeauty userBeauty) {
        userBeautyService.saveUserBeautyAccount(userBeauty);

        return getSuccessResponseVO(null);
    }

    /**
     * 修改靓号信息
     */
    @GlobalAccessInterceptor(checkAdminAccess = true)
    @PutMapping("/update")
    public ResponseVO update(@RequestBody UserBeauty userBeauty) {
        userBeautyService.saveUserBeautyAccount(userBeauty);

        return getSuccessResponseVO(null);
    }

    /**
     * 修改靓号信息
     */
    @GlobalAccessInterceptor(checkAdminAccess = true)
    @DeleteMapping("/delete/{id}")
    public ResponseVO delete(@PathVariable @NotNull Integer id) {
        userBeautyService.deleteUserBeautyById(id);

        return getSuccessResponseVO(null);
    }
}