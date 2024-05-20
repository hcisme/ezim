package org.chc.ezim.controller;

import jakarta.annotation.Resource;
import org.chc.ezim.annotation.GlobalAccessInterceptor;
import org.chc.ezim.entity.config.AppConfigProperties;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.entity.enums.AppUpdateFileTypeEnum;
import org.chc.ezim.entity.model.AppUpdate;
import org.chc.ezim.entity.vo.AppUpdateVO;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.service.AppUpdateService;
import org.chc.ezim.utils.CopyTools;
import org.chc.ezim.utils.StringTools;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Arrays;

/**
 * 检查软件版本 Controller
 */
@RestController
@RequestMapping("/update")
public class UpdateVersionController extends ABaseController {

    @Resource
    private AppUpdateService appUpdateService;

    @Resource
    private AppConfigProperties appConfigProperties;

    /**
     * 检查更新
     */
    @GlobalAccessInterceptor
    @GetMapping("/checkVersion")
    public ResponseVO checkVersion(@RequestParam(required = false) String appVersion, @RequestParam(required = false) String uid) {
        if (StringTools.isEmpty(appVersion)) {
            return getSuccessResponseVO(null);
        }

        AppUpdate latestAppUpdate = appUpdateService.getLatestAppUpdate(appVersion, uid);
        if (latestAppUpdate == null) {
            return getSuccessResponseVO(null);
        }

        AppUpdateVO appUpdateVO = CopyTools.copy(latestAppUpdate, AppUpdateVO.class);
        if (AppUpdateFileTypeEnum.LOCAL.getType().equals(latestAppUpdate.getFileType())) {
            File file = new File(appConfigProperties.getProjectFolder()
                    + Constants.FILE_FOLDER
                    + Constants.APP_UPDATE_FOLDER
                    + latestAppUpdate.getId()
                    + Constants.APP_APK_SUFFIX
            );
            appUpdateVO.setSize(file.length());
        } else {
            appUpdateVO.setSize(0L);
        }

        appUpdateVO.setUpdateList(Arrays.asList(latestAppUpdate.getUpdateDescArray()));
        String fileName = Constants.APP_NAME + "." + latestAppUpdate.getVersion() + Constants.APP_APK_SUFFIX;
        appUpdateVO.setFileName(fileName);

        return getSuccessResponseVO(appUpdateVO);
    }
}