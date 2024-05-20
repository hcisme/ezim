package org.chc.ezim.service.impl;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.config.AppConfigProperties;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.entity.dto.AppUpdateDto;
import org.chc.ezim.entity.dto.SimplePage;
import org.chc.ezim.entity.enums.AppUpdateFileTypeEnum;
import org.chc.ezim.entity.enums.AppUpdateStatusEnum;
import org.chc.ezim.entity.enums.PageSize;
import org.chc.ezim.entity.enums.ResponseCodeEnum;
import org.chc.ezim.entity.model.AppUpdate;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.mapper.AppUpdateMapper;
import org.chc.ezim.service.AppUpdateService;
import org.chc.ezim.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;


/**
 * app发布 业务接口实现
 */
@Service("appUpdateService")
public class AppUpdateServiceImpl implements AppUpdateService {

    @Resource
    private AppUpdateMapper<AppUpdate, AppUpdateDto> appUpdateMapper;

    @Resource
    private AppConfigProperties appConfigProperties;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<AppUpdate> findListByParam(AppUpdateDto param) {
        return this.appUpdateMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(AppUpdateDto param) {
        return this.appUpdateMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<AppUpdate> findListByPage(AppUpdateDto param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPage(), count, pageSize);
        param.setSimplePage(page);
        List<AppUpdate> list = this.findListByParam(param);
        PaginationResultVO<AppUpdate> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(AppUpdate bean) {
        return this.appUpdateMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<AppUpdate> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.appUpdateMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<AppUpdate> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.appUpdateMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(AppUpdate bean, AppUpdateDto param) {
        StringTools.checkParam(param);
        return this.appUpdateMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(AppUpdateDto param) {
        StringTools.checkParam(param);
        return this.appUpdateMapper.deleteByParam(param);
    }

    /**
     * 根据Id获取对象
     */
    @Override
    public AppUpdate getAppUpdateById(Integer id) {
        return this.appUpdateMapper.selectById(id);
    }

    /**
     * 根据Id修改
     */
    @Override
    public Integer updateAppUpdateById(AppUpdate bean, Integer id) {
        return this.appUpdateMapper.updateById(bean, id);
    }

    /**
     * 根据Id删除
     */
    @Override
    public Integer deleteAppUpdateById(Integer id) {
        AppUpdate dbInfo = getAppUpdateById(id);
        if (!AppUpdateStatusEnum.INIT.getStatus().equals(dbInfo.getStatus())) {
            throw new BusinessException("不能删除已发布数据");
        }

        return this.appUpdateMapper.deleteById(id);
    }

    /**
     * 根据Version获取对象
     */
    @Override
    public AppUpdate getAppUpdateByVersion(String version) {
        return this.appUpdateMapper.selectByVersion(version);
    }

    /**
     * 根据Version修改
     */
    @Override
    public Integer updateAppUpdateByVersion(AppUpdate bean, String version) {
        return this.appUpdateMapper.updateByVersion(bean, version);
    }

    /**
     * 根据Version删除
     */
    @Override
    public Integer deleteAppUpdateByVersion(String version) {
        return this.appUpdateMapper.deleteByVersion(version);
    }

    @Override
    public void saveAppUpdateInfo(AppUpdate appUpdate, MultipartFile file) throws IOException {
        AppUpdateStatusEnum statusEnum = AppUpdateStatusEnum.getByStatus(appUpdate.getStatus());
        AppUpdateFileTypeEnum fileTypeEnum = AppUpdateFileTypeEnum.getByType(appUpdate.getFileType());
        if (fileTypeEnum == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        if (appUpdate.getId() != null) {
            AppUpdate dbInfo = getAppUpdateById(appUpdate.getId());
            if (!AppUpdateStatusEnum.INIT.getStatus().equals(dbInfo.getStatus())) {
                throw new BusinessException("不能修改已发布数据");
            }
        }

        AppUpdateDto appUpdateDto = new AppUpdateDto();
        appUpdateDto.setOrderBy("id desc");
        appUpdateDto.setSimplePage(new SimplePage(0, 1));
        List<AppUpdate> appUpdateList = appUpdateMapper.selectList(appUpdateDto);
        if (!appUpdateList.isEmpty()) {
            AppUpdate lastest = appUpdateList.get(0);
            long dbVersion = Long.parseLong(lastest.getVersion().replace(".", ""));
            long currentVersion = Long.parseLong(appUpdate.getVersion().replace(".", ""));
            // 新增数据 且 当前版本 < 数据库最新版本
            if (appUpdate.getId() == null && currentVersion <= dbVersion) {
                throw new BusinessException("当前版本 必须大于 历史版本");
            }

            // 更新数据 且 当前版本 < 数据库最新版本 且 更新版本 ！= 数据库最新版本
            if (appUpdate.getId() != null &&
                    currentVersion <= dbVersion &&
                    !appUpdate.getId().equals(lastest.getId())

            ) {
                throw new BusinessException("当前版本 必须大于 历史版本");
            }

            AppUpdate versionDb = appUpdateMapper.selectByVersion(appUpdate.getVersion());
            if (appUpdate.getId() != null && versionDb != null && !versionDb.getId().equals(appUpdate.getId())) {
                throw new BusinessException("版本号已存在");
            }
        }

        if (appUpdate.getId() == null) {
            // 新增
            appUpdate.setCreateTime(new Date());
            appUpdate.setStatus(AppUpdateStatusEnum.INIT.getStatus());
            appUpdateMapper.insert(appUpdate);
        } else {
            // 修改
            appUpdate.setStatus(null);
            appUpdate.setGrayscaleUid(null);
            appUpdateMapper.updateById(appUpdate, appUpdate.getId());
        }

        if (appUpdate.getFileType().equals(AppUpdateFileTypeEnum.LOCAL.getType()) && file != null) {
            File baseFolder = new File(appConfigProperties.getProjectFolder() + Constants.FILE_FOLDER + Constants.APP_UPDATE_FOLDER);
            if (!baseFolder.exists()) {
                baseFolder.mkdirs();
            }
            file.transferTo(new File(baseFolder.getAbsolutePath() + "/" + appUpdate.getId() + Constants.APP_APK_SUFFIX));
        }
    }

    @Override
    public void publicUpdate(Integer id, Integer status, String grayscaleUid) {
        AppUpdateStatusEnum statusEnum = AppUpdateStatusEnum.getByStatus(status);
        if (statusEnum == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        if (AppUpdateStatusEnum.GRAYSCALE == statusEnum && StringTools.isEmpty(grayscaleUid)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        if (AppUpdateStatusEnum.GRAYSCALE != statusEnum) {
            grayscaleUid = "";
        }

        AppUpdate appUpdate = new AppUpdate();
        appUpdate.setStatus(status);
        appUpdate.setGrayscaleUid(grayscaleUid);
        appUpdateMapper.updateById(appUpdate, id);
    }

    @Override
    public AppUpdate getLatestAppUpdate(String appVersion, String uid) {
        return appUpdateMapper.selectLatestUpdate(appVersion, uid);
    }
}