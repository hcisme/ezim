package org.chc.ezim.service.impl;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.enums.PageSize;
import org.chc.ezim.entity.model.User;
import org.chc.ezim.entity.dto.SimplePage;
import org.chc.ezim.entity.dto.UserDto;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.mapper.UserMapper;
import org.chc.ezim.service.UserService;
import org.chc.ezim.utils.StringTools;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 用户信息 业务接口实现
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper<User, UserDto> userMapper;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<User> findListByParam(UserDto param) {
        return this.userMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(UserDto param) {
        return this.userMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<User> findListByPage(UserDto param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<User> list = this.findListByParam(param);
        PaginationResultVO<User> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(User bean) {
        return this.userMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<User> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<User> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(User bean, UserDto param) {
        StringTools.checkParam(param);
        return this.userMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(UserDto param) {
        StringTools.checkParam(param);
        return this.userMapper.deleteByParam(param);
    }

    /**
     * 根据Id获取对象
     */
    @Override
    public User getUserById(String id) {
        return this.userMapper.selectById(id);
    }

    /**
     * 根据Id修改
     */
    @Override
    public Integer updateUserById(User bean, String id) {
        return this.userMapper.updateById(bean, id);
    }

    /**
     * 根据Id删除
     */
    @Override
    public Integer deleteUserById(String id) {
        return this.userMapper.deleteById(id);
    }

    /**
     * 根据Email获取对象
     */
    @Override
    public User getUserByEmail(String email) {
        return this.userMapper.selectByEmail(email);
    }

    /**
     * 根据Email修改
     */
    @Override
    public Integer updateUserByEmail(User bean, String email) {
        return this.userMapper.updateByEmail(bean, email);
    }

    /**
     * 根据Email删除
     */
    @Override
    public Integer deleteUserByEmail(String email) {
        return this.userMapper.deleteByEmail(email);
    }
}