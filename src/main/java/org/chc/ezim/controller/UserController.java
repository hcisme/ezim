package org.chc.ezim.controller;

import io.springboot.captcha.ArithmeticCaptcha;
import jakarta.annotation.Resource;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.entity.dto.LoginDto;
import org.chc.ezim.entity.dto.RegisterDto;
import org.chc.ezim.entity.dto.SettingDto;
import org.chc.ezim.entity.dto.UserDto;
import org.chc.ezim.entity.model.User;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.entity.vo.UserVo;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.redis.RedisComponent;
import org.chc.ezim.redis.RedisUtils;
import org.chc.ezim.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * 用户信息 Controller
 */
@RestController("userController")
@RequestMapping("/user")
public class UserController extends ABaseController {

    @Resource
    private UserService userService;

    @Resource
    private RedisUtils<String> redisUtils;

    @Resource
    private RedisComponent redisComponent;

    /**
     * 根据条件分页查询
     */
    @GetMapping("/loadDataList")
    public ResponseVO loadDataList(UserDto query) {
        return getSuccessResponseVO(userService.findListByPage(query));
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    public ResponseVO add(@RequestBody User bean) {
        userService.add(bean);
        return getSuccessResponseVO(null);
    }

    /**
     * 批量新增
     */
    @PostMapping("/addBatch")
    public ResponseVO addBatch(@RequestBody List<User> listBean) {
        userService.addBatch(listBean);
        return getSuccessResponseVO(null);
    }

    /**
     * 批量新增/修改
     */
    @PutMapping("/addOrUpdateBatch")
    public ResponseVO addOrUpdateBatch(@RequestBody List<User> listBean) {
        userService.addBatch(listBean);
        return getSuccessResponseVO(null);
    }

    /**
     * 根据Id查询对象
     */
    @GetMapping("/getUserById")
    public ResponseVO getUserById(String id) {
        return getSuccessResponseVO(userService.getUserById(id));
    }

    /**
     * 根据Id修改对象
     */
    @PutMapping("/updateUserById")
    public ResponseVO updateUserById(@RequestBody User bean, String id) {
        userService.updateUserById(bean, id);
        return getSuccessResponseVO(null);
    }

    /**
     * 根据Id删除
     */
    @DeleteMapping("/deleteUserById")
    public ResponseVO deleteUserById(String id) {
        userService.deleteUserById(id);
        return getSuccessResponseVO(null);
    }

    /**
     * 根据Email查询对象
     */
    @GetMapping("/getUserByEmail")
    public ResponseVO getUserByEmail(String email) {
        return getSuccessResponseVO(userService.getUserByEmail(email));
    }

    /**
     * 根据Email修改对象
     */
    @PutMapping("/updateUserByEmail")
    public ResponseVO updateUserByEmail(@RequestBody User bean, String email) {
        userService.updateUserByEmail(bean, email);
        return getSuccessResponseVO(null);
    }

    /**
     * 根据Email删除
     */
    @DeleteMapping("/deleteUserByEmail")
    public ResponseVO deleteUserByEmail(String email) {
        userService.deleteUserByEmail(email);
        return getSuccessResponseVO(null);
    }

    /**
     * 验证码
     */
    @GetMapping("/captcha")
    public ResponseVO checkCode() {
        var map = new HashMap<String, Object>();
        var captcha = new ArithmeticCaptcha(100, 42);
        var code = captcha.text();
        var captchaKey = UUID.randomUUID().toString();

        redisUtils.setValueAndExpire(Constants.REDIS_KEY_CAPTCHA + captchaKey, code, Constants.REDIS_TIME_1MIN * 10);
        map.put("captcha", captcha.toBase64());
        map.put("captchaKey", captchaKey);

        return getSuccessResponseVO(map);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public ResponseVO register(@Validated @RequestBody RegisterDto registerDto) {
        var k = Constants.REDIS_KEY_CAPTCHA + registerDto.getCaptchaKey();

        try {
            if (redisUtils.getValue(k) == null) {
                throw new BusinessException("验证码过期 请重新获取");
            }
            if (!registerDto.getCaptcha().equalsIgnoreCase(redisUtils.getValue(k))) {
                throw new BusinessException("图片验证码不正确");
            }

            userService.register(registerDto.getEmail(), registerDto.getNickName(), registerDto.getPassword());
            return getSuccessResponseVO(null);
        } finally {
            redisUtils.delete(k);
        }
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public ResponseVO login(@Validated @RequestBody LoginDto loginDto) {
        var k = Constants.REDIS_KEY_CAPTCHA + loginDto.getCaptchaKey();

        try {
            if (redisUtils.getValue(k) == null) {
                throw new BusinessException("验证码过期 请重新获取");
            }
            if (!loginDto.getCaptcha().equalsIgnoreCase(redisUtils.getValue(k))) {
                throw new BusinessException("图片验证码不正确");
            }
            UserVo userVo = userService.login(loginDto.getEmail(), loginDto.getPassword());

            return getSuccessResponseVO(userVo);
        } finally {
            redisUtils.delete(k);
        }
    }

    /**
     * 得到用户设置
     */
    @GetMapping("/getSetting")
    public ResponseVO getSetting() {
        SettingDto setting = redisComponent.getSetting();

        return getSuccessResponseVO(setting);
    }
}
