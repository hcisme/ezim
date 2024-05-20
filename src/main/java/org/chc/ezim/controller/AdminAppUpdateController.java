package org.chc.ezim.controller;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.chc.ezim.annotation.GlobalAccessInterceptor;
import org.chc.ezim.entity.dto.AppUpdateDto;
import org.chc.ezim.entity.enums.AppUpdateStatusEnum;
import org.chc.ezim.entity.model.AppUpdate;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.service.AppUpdateService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * app发布 Controller
 */
@RestController
@RequestMapping("/adminAppUpdate")
public class AdminAppUpdateController extends ABaseController {

    @Resource
    private AppUpdateService appUpdateService;

    /**
     * 获取发布列表
     */
    @GlobalAccessInterceptor(checkAdminAccess = true)
    @GetMapping("/list")
    public ResponseVO list(AppUpdateDto appUpdateDto) {
        appUpdateDto.setOrderBy("version desc");

        return getSuccessResponseVO(appUpdateService.findListByPage(appUpdateDto));
    }

    /**
     * 创建发布信息
     */
    @GlobalAccessInterceptor(checkAdminAccess = true)
    @PostMapping("/create")
    public ResponseVO create(
            @NotEmpty String version,
            @NotEmpty String updateDesc,
            @NotNull Integer fileType,
            String outerLink,
            MultipartFile file
    ) throws IOException {
        AppUpdate appUpdate = new AppUpdate();
        appUpdate.setVersion(version);
        appUpdate.setUpdateDesc(updateDesc);
        appUpdate.setFileType(fileType);
        appUpdate.setOuterLink(outerLink);

        appUpdateService.saveAppUpdateInfo(appUpdate, file);

        return getSuccessResponseVO(null);
    }

    /**
     * 更新发布信息
     */
    @GlobalAccessInterceptor(checkAdminAccess = true)
    @PutMapping("/update")
    public ResponseVO update(
            @NotNull Integer id,
            @NotEmpty String version,
            @NotEmpty String updateDesc,
            @NotNull Integer fileType,
            String outerLink,
            MultipartFile file
    ) throws IOException {
        AppUpdate appUpdate = new AppUpdate();
        appUpdate.setId(id);
        appUpdate.setVersion(version);
        appUpdate.setUpdateDesc(updateDesc);
        appUpdate.setFileType(fileType);
        appUpdate.setOuterLink(outerLink);

        appUpdateService.saveAppUpdateInfo(appUpdate, file);

        return getSuccessResponseVO(null);
    }

    /**
     * 删除更新
     */
    @GlobalAccessInterceptor(checkAdminAccess = true)
    @DeleteMapping("/delete/{id}")
    public ResponseVO delete(@PathVariable @NotNull Integer id) {
        appUpdateService.deleteAppUpdateById(id);
        return getSuccessResponseVO(null);
    }

    /**
     * 发布
     */
    @GlobalAccessInterceptor(checkAdminAccess = true)
    @PostMapping("/public/{id}/{status}")
    public ResponseVO publicUpdate(
            @PathVariable @NotNull Integer id,
            @PathVariable @NotNull Integer status,
            String grayscaleUid
    ) {
        appUpdateService.publicUpdate(id, status, grayscaleUid);

        return getSuccessResponseVO(null);
    }
}