package org.chc.ezim.controller;

import io.springboot.captcha.ArithmeticCaptcha;
import jakarta.annotation.Resource;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.entity.model.User;
import org.chc.ezim.entity.dto.UserDto;
import org.chc.ezim.entity.vo.ResponseVO;
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
@Validated
public class UserController extends ABaseController {

    @Resource
    private UserService userService;

    @Resource
    private RedisUtils<String> redisUtils;

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
        redisUtils.setValueAndExpire(Constants.REDIS_KEY_CAPTCHA, code, Constants.REDIS_TIME_1MIN * 10);

        var captchaKey = UUID.randomUUID().toString();
        map.put("captcha", captcha.toBase64());
        map.put("captchaKey", captchaKey);

        return getSuccessResponseVO(map);
    }

    /**
     * 验证码
     */
    @PostMapping("/register")
    public ResponseVO register(String captchaKey, String email, String nickName, String password, String captcha) {


        return getSuccessResponseVO(null);
    }
}