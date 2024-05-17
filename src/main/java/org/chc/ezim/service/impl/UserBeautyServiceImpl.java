package org.chc.ezim.service.impl;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.dto.SimplePage;
import org.chc.ezim.entity.dto.UserBeautyDto;
import org.chc.ezim.entity.dto.UserDto;
import org.chc.ezim.entity.enums.BeautyAccountStatusEnum;
import org.chc.ezim.entity.enums.PageSize;
import org.chc.ezim.entity.enums.ResponseCodeEnum;
import org.chc.ezim.entity.model.User;
import org.chc.ezim.entity.model.UserBeauty;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.mapper.UserBeautyMapper;
import org.chc.ezim.mapper.UserMapper;
import org.chc.ezim.service.UserBeautyService;
import org.chc.ezim.utils.StringTools;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 靓号表 业务接口实现
 */
@Service("userBeautyService")
public class UserBeautyServiceImpl implements UserBeautyService {

    @Resource
    private UserBeautyMapper<UserBeauty, UserBeautyDto> userBeautyMapper;

    @Resource
    private UserMapper<User, UserDto> userMapper;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<UserBeauty> findListByParam(UserBeautyDto param) {
        return this.userBeautyMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(UserBeautyDto param) {
        return this.userBeautyMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<UserBeauty> findListByPage(UserBeautyDto param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPage(), count, pageSize);
        param.setSimplePage(page);
        List<UserBeauty> list = this.findListByParam(param);
        PaginationResultVO<UserBeauty> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(UserBeauty bean) {
        return this.userBeautyMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<UserBeauty> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userBeautyMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<UserBeauty> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userBeautyMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(UserBeauty bean, UserBeautyDto param) {
        StringTools.checkParam(param);
        return this.userBeautyMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(UserBeautyDto param) {
        StringTools.checkParam(param);
        return this.userBeautyMapper.deleteByParam(param);
    }

    /**
     * 根据Id获取对象
     */
    @Override
    public UserBeauty getUserBeautyById(Integer id) {
        return this.userBeautyMapper.selectById(id);
    }

    /**
     * 根据Id修改
     */
    @Override
    public Integer updateUserBeautyById(UserBeauty bean, Integer id) {
        return this.userBeautyMapper.updateById(bean, id);
    }

    /**
     * 根据Id删除
     */
    @Override
    public Integer deleteUserBeautyById(Integer id) {
        return this.userBeautyMapper.deleteById(id);
    }

    /**
     * 根据UserId获取对象
     */
    @Override
    public UserBeauty getUserBeautyByUserId(String userId) {
        return this.userBeautyMapper.selectByUserId(userId);
    }

    /**
     * 根据UserId修改
     */
    @Override
    public Integer updateUserBeautyByUserId(UserBeauty bean, String userId) {
        return this.userBeautyMapper.updateByUserId(bean, userId);
    }

    /**
     * 根据UserId删除
     */
    @Override
    public Integer deleteUserBeautyByUserId(String userId) {
        return this.userBeautyMapper.deleteByUserId(userId);
    }

    /**
     * 根据Email获取对象
     */
    @Override
    public UserBeauty getUserBeautyByEmail(String email) {
        return this.userBeautyMapper.selectByEmail(email);
    }

    /**
     * 根据Email修改
     */
    @Override
    public Integer updateUserBeautyByEmail(UserBeauty bean, String email) {
        return this.userBeautyMapper.updateByEmail(bean, email);
    }

    /**
     * 根据Email删除
     */
    @Override
    public Integer deleteUserBeautyByEmail(String email) {
        return this.userBeautyMapper.deleteByEmail(email);
    }

    @Override
    public void saveUserBeautyAccount(UserBeauty userBeauty) {
        if (userBeauty.getId() != null) {
            UserBeauty dbInfo = userBeautyMapper.selectById(userBeauty.getId());

            // 已使用 不准修改
            if (BeautyAccountStatusEnum.USED.getStatus().equals(dbInfo.getStatus())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }

        UserBeauty dbInfo = userBeautyMapper.selectByEmail(userBeauty.getEmail());
        // 新增的时候判断邮箱是否存在
        if (dbInfo != null && userBeauty.getId() == null) {
            throw new BusinessException("靓号邮箱已存在");
        }
        // 修改的时候判断邮箱是否存在
        if (dbInfo != null && userBeauty.getId() != null && dbInfo.getId() != null && !userBeauty.getId().equals(dbInfo.getId())) {
            throw new BusinessException("靓号邮箱已存在");
        }

        // 判断靓号是否存在
        dbInfo = userBeautyMapper.selectByUserId(userBeauty.getUserId());
        if (dbInfo != null && userBeauty.getId() == null) {
            throw new BusinessException("靓号已存在");
        }
        if (dbInfo != null && userBeauty.getId() != null && dbInfo.getId() != null && !userBeauty.getId().equals(dbInfo.getId())) {
            throw new BusinessException("靓号已存在");
        }

        // 判断邮箱是否已经注册
        User user = userMapper.selectByEmail(userBeauty.getEmail());
        if (user != null) {
            throw new BusinessException("靓号邮箱已经被注册");
        }
        user = userMapper.selectById(userBeauty.getUserId());
        if (user != null) {
            throw new BusinessException("靓号已经被注册");
        }

        if (userBeauty.getId() != null) {
            userBeautyMapper.updateById(userBeauty, userBeauty.getId());
        } else {
            userBeauty.setStatus(BeautyAccountStatusEnum.NO_USE.getStatus());
            userBeautyMapper.insert(userBeauty);
        }
    }
}