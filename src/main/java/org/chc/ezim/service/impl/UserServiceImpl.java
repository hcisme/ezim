package org.chc.ezim.service.impl;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.config.AppConfigProperties;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.entity.dto.SimplePage;
import org.chc.ezim.entity.dto.TokenUserInfoDto;
import org.chc.ezim.entity.dto.UserBeautyDto;
import org.chc.ezim.entity.dto.UserDto;
import org.chc.ezim.entity.enums.*;
import org.chc.ezim.entity.model.User;
import org.chc.ezim.entity.model.UserBeauty;
import org.chc.ezim.entity.vo.PaginationResultVO;
import org.chc.ezim.entity.vo.UserVo;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.mapper.UserBeautyMapper;
import org.chc.ezim.mapper.UserMapper;
import org.chc.ezim.redis.RedisComponent;
import org.chc.ezim.service.UserService;
import org.chc.ezim.utils.CopyTools;
import org.chc.ezim.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * 用户信息 业务接口实现
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private AppConfigProperties appConfigProperties;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserMapper<User, UserDto> userMapper;

    @Resource
    private UserBeautyMapper<UserBeauty, UserBeautyDto> userBeautyMapper;

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

    /**
     * 用户注册
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(String email, String nickName, String password) {
        User user = userMapper.selectByEmail(email);

        if (user != null) {
            throw new BusinessException("邮箱账号已存在");
        }

        Date date = new Date();
        String id = StringTools.getUserId();

        UserBeauty userBeauty = userBeautyMapper.selectByEmail(email);

        // 靓号表里面有 且 可用状态
        boolean isCanUse = userBeauty != null && BeautyAccountStatusEnum.NO_USE.getStatus().equals(userBeauty.getStatus());
        if (isCanUse) {
            id = UserContactTypeEnum.USER.getPrefix() + userBeauty.getUserId();
        }

        var newUser = new User(
                id,
                email,
                nickName,
                StringTools.encodeMd5(password),
                JoinTypeEnum.APPLY.getType(),
                UserStatusEnum.ENABLE.getStatus(),
                date,
                date.getTime()
        );
        userMapper.insert(newUser);

        if (isCanUse) {
            userBeautyMapper.updateByUserId(new UserBeauty(BeautyAccountStatusEnum.USED.getStatus()), id);
        }

        // TODO 用户注册时就应该添加一个机器人好友
    }

    /**
     * 用户登录
     */
    @Override
    public UserVo login(String email, String password) {
        User user = userMapper.selectByEmail(email);

        if (user == null) {
            throw new BusinessException("邮箱账号不存在");
        }
        if (!Objects.equals(user.getPassword(), StringTools.encodeMd5(password))) {
            throw new BusinessException("密码错误");
        }
        if (user.getStatus().equals(UserStatusEnum.DISABLE.getStatus())) {
            throw new BusinessException("账号已禁用");
        }
        Long userHeartBeat = redisComponent.getUserHeartBeat(user.getId());
        if (userHeartBeat != null) {
            throw new BusinessException("此账号已在别处登录，请退出后重试");
        }
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(user);
        String token = StringTools.encodeMd5(tokenUserInfoDto.getId() + StringTools.getRandomString(Constants.LENGTH_20));
        tokenUserInfoDto.setToken(token);
        // 保存登录信息到 redis 中
        redisComponent.saveTokenUserInfoDto(tokenUserInfoDto);

        // 返回完整的 user 信息
        UserVo userVo = CopyTools.copy(user, UserVo.class);
        userVo.setToken(tokenUserInfoDto.getToken());
        userVo.setAdmin(tokenUserInfoDto.getAdmin());


        // TODO 查询我的群组
        // TODO 查询我的联系人
        return userVo;
    }

    private TokenUserInfoDto getTokenUserInfoDto(User user) {
        TokenUserInfoDto tokenUserInfoDto = new TokenUserInfoDto(null, user.getId(), user.getNickName(), null);

        String adminEmails = appConfigProperties.getAdminEmails();
        // 是否是管理员
        boolean isAdmin = !StringTools.isEmpty(adminEmails) && Arrays.asList(adminEmails.split(",")).contains(user.getEmail());
        tokenUserInfoDto.setAdmin(isAdmin);

        return tokenUserInfoDto;
    }
}