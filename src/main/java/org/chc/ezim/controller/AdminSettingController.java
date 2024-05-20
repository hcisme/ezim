package org.chc.ezim.controller;

import jakarta.annotation.Resource;
import org.chc.ezim.annotation.GlobalAccessInterceptor;
import org.chc.ezim.entity.config.AppConfigProperties;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.entity.dto.SettingDto;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.redis.RedisComponent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 管理员设置 Controller
 */
@RestController
@RequestMapping("/adminSetting")
public class AdminSettingController extends ABaseController {

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AppConfigProperties appConfigProperties;

    /**
     * 获取设置
     */
    @GlobalAccessInterceptor(checkAdminAccess = true)
    @GetMapping("/get")
    public ResponseVO getSetting() {
        SettingDto settingDto = redisComponent.getSetting();

        return getSuccessResponseVO(settingDto);
    }

    /**
     * 保存设置
     */
    @GlobalAccessInterceptor(checkAdminAccess = true)
    @PutMapping("/save")
    public ResponseVO saveSetting(
            @ModelAttribute SettingDto settingDto,
            @RequestPart(required = false) MultipartFile robotFile,
            @RequestPart(required = false) MultipartFile robotCover
    ) throws IOException {
        if (robotFile != null) {
            String baseFolder = appConfigProperties.getProjectFolder() + Constants.FILE_FOLDER;
            File targetFileFolder = new File(baseFolder + Constants.FILE_FOLDER_AVATAR_NAME);
            if (!targetFileFolder.exists()) {
                targetFileFolder.mkdirs();
            }
            String filePath = targetFileFolder.getPath() + "/" + Constants.ROBOT_UID + Constants.IMAGE_SUFFIX;
            String coverPath = targetFileFolder.getPath() + "/" + Constants.ROBOT_UID + Constants.COVER_IMAGE_SUFFIX;
            robotFile.transferTo(new File(filePath));
            robotCover.transferTo(new File(coverPath));
        }

        redisComponent.saveSetting(settingDto);

        return getSuccessResponseVO(null);
    }
}