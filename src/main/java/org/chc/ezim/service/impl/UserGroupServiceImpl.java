package org.chc.ezim.service.impl;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.config.AppConfigProperties;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.entity.dto.SettingDto;
import org.chc.ezim.entity.dto.SimplePage;
import org.chc.ezim.entity.dto.UserContactDto;
import org.chc.ezim.entity.dto.UserGroupDto;
import org.chc.ezim.entity.enums.PageSize;
import org.chc.ezim.entity.enums.ResponseCodeEnum;
import org.chc.ezim.entity.enums.UserContactStatusEnum;
import org.chc.ezim.entity.enums.UserContactTypeEnum;
import org.chc.ezim.entity.model.UserContact;
import org.chc.ezim.entity.model.UserGroup;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.mapper.UserContactMapper;
import org.chc.ezim.mapper.UserGroupMapper;
import org.chc.ezim.redis.RedisComponent;
import org.chc.ezim.service.UserGroupService;
import org.chc.ezim.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;


/**
 * 业务接口实现
 */
@Service("userGroupService")
public class UserGroupServiceImpl implements UserGroupService {

    @Resource
    private UserGroupMapper<UserGroup, UserGroupDto> userGroupMapper;

    @Resource
    private UserContactMapper<UserContact, UserContactDto> userContactMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AppConfigProperties appConfigProperties;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<UserGroup> findListByParam(UserGroupDto param) {
        return this.userGroupMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(UserGroupDto param) {
        return this.userGroupMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<UserGroup> findListByPage(UserGroupDto param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPage(), count, pageSize);
        param.setSimplePage(page);
        List<UserGroup> list = this.findListByParam(param);
        PaginationResultVO<UserGroup> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(),
                page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(UserGroup bean) {
        return this.userGroupMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<UserGroup> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userGroupMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<UserGroup> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userGroupMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(UserGroup bean, UserGroupDto param) {
        StringTools.checkParam(param);
        return this.userGroupMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(UserGroupDto param) {
        StringTools.checkParam(param);
        return this.userGroupMapper.deleteByParam(param);
    }

    /**
     * 根据Id获取对象
     */
    @Override
    public UserGroup getUserGroupById(String id) {
        return this.userGroupMapper.selectById(id);
    }

    /**
     * 根据Id修改
     */
    @Override
    public Integer updateUserGroupById(UserGroup bean, String id) {
        return this.userGroupMapper.updateById(bean, id);
    }

    /**
     * 根据Id删除
     */
    @Override
    public Integer deleteUserGroupById(String id) {
        return this.userGroupMapper.deleteById(id);
    }

    /**
     * 创建群组
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createOrUpdateGroup(UserGroup userGroup, MultipartFile avatarFile, MultipartFile avatarCover) throws IOException {
        Date date = new Date();
        // 新增
        if (StringTools.isEmpty(userGroup.getId())) {
            Integer groupCount = userGroupMapper.selectCount(new UserGroupDto(userGroup.getGroupOwnerId()));
            SettingDto setting = redisComponent.getSetting();
            if (groupCount >= setting.getMaxGroupCount()) {
                throw new BusinessException("最多只能创建" + setting.getMaxGroupCount() + "个群组");
            }

            userGroup.setCreateTime(date);
            userGroup.setId(StringTools.getGroupId());
            userGroupMapper.insert(userGroup);

            // 将群组设置为联系人
            UserContact userContact = new UserContact();
            userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            userContact.setContactType(UserContactTypeEnum.GROUP.getType());
            userContact.setContactId(userGroup.getId());
            userContact.setUserId(userGroup.getGroupOwnerId());
            userContact.setCreateTime(date);
            userContact.setLastUpdateTime(date);
            userContactMapper.insert(userContact);

            // 上传图片逻辑
            String baseFileFolder = appConfigProperties.getProjectFolder() + Constants.FILE_FOLDER;
            File targetFileFolder = new File(baseFileFolder + Constants.FILE_FOLDER_AVATAR_NAME);
            if (!targetFileFolder.exists()) {
                targetFileFolder.mkdirs();
            }
            String filePath = targetFileFolder.getPath() + "/" + userGroup.getId() + Constants.IMAGE_SUFFIX;
            avatarFile.transferTo(new File(filePath));
            avatarCover.transferTo(new File(filePath + Constants.COVER_IMAGE_SUFFIX));

            // TODO 创建会话
            // TODO 发一个消息
        } else {
            // 修改
            UserGroup group = userGroupMapper.selectById(userGroup.getId());
            if (!group.getGroupOwnerId().equals(userGroup.getGroupOwnerId())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            userGroupMapper.updateById(userGroup, userGroup.getId());

            // 上传图片逻辑
            String baseFileFolder = appConfigProperties.getProjectFolder() + Constants.FILE_FOLDER;
            File targetFileFolder = new File(baseFileFolder + Constants.FILE_FOLDER_AVATAR_NAME);
            if (!targetFileFolder.exists()) {
                targetFileFolder.mkdirs();
            }
            String filePath = targetFileFolder.getPath() + "/" + userGroup.getId() + Constants.IMAGE_SUFFIX;
            if (avatarFile != null) {
                avatarFile.transferTo(new File(filePath));
            }
            if (avatarCover != null) {
                avatarCover.transferTo(new File(filePath + Constants.COVER_IMAGE_SUFFIX));
            }

            // TODO 更新相关表冗余信息
            // TODO 修改群昵称后 发送 ws 消息
        }
    }
}