package org.chc.ezim.service;

import java.io.IOException;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import org.chc.ezim.entity.dto.AppUpdateDto;
import org.chc.ezim.entity.model.AppUpdate;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;


/**
 * app发布 业务接口
 */
public interface AppUpdateService {

	/**
	 * 根据条件查询列表
	 */
	List<AppUpdate> findListByParam(AppUpdateDto param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(AppUpdateDto param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<AppUpdate> findListByPage(AppUpdateDto param);

	/**
	 * 新增
	 */
	Integer add(AppUpdate bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<AppUpdate> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<AppUpdate> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(AppUpdate bean,AppUpdateDto param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(AppUpdateDto param);

	/**
	 * 根据Id查询对象
	 */
	AppUpdate getAppUpdateById(Integer id);


	/**
	 * 根据Id修改
	 */
	Integer updateAppUpdateById(AppUpdate bean,Integer id);


	/**
	 * 根据Id删除
	 */
	Integer deleteAppUpdateById(Integer id);


	/**
	 * 根据Version查询对象
	 */
	AppUpdate getAppUpdateByVersion(String version);


	/**
	 * 根据Version修改
	 */
	Integer updateAppUpdateByVersion(AppUpdate bean,String version);


	/**
	 * 根据Version删除
	 */
	Integer deleteAppUpdateByVersion(String version);

	/**
	 * 创建 或 修改
	 */
	void saveAppUpdateInfo(AppUpdate appUpdate, MultipartFile file) throws IOException;

	/**
	 * 发布更新
	 */
	void publicUpdate(Integer id,Integer status, String grayscaleUid);

	/**
	 *
	 */
	AppUpdate getLatestAppUpdate(String appVersion, String uid);
}