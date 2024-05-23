package org.chc.ezim.controller;

import io.springboot.captcha.ArithmeticCaptcha;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.apache.commons.lang3.ArrayUtils;
import org.chc.ezim.annotation.GlobalAccessInterceptor;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.entity.dto.LoginDto;
import org.chc.ezim.entity.dto.RegisterDto;
import org.chc.ezim.entity.dto.SettingDto;
import org.chc.ezim.entity.dto.TokenUserInfoDto;
import org.chc.ezim.entity.enums.ResponseCodeEnum;
import org.chc.ezim.entity.enums.UserContactStatusEnum;
import org.chc.ezim.entity.model.User;
import org.chc.ezim.entity.model.UserContact;
import org.chc.ezim.entity.vo.ResponseVO;
import org.chc.ezim.entity.vo.UserVo;
import org.chc.ezim.exception.BusinessException;
import org.chc.ezim.redis.RedisComponent;
import org.chc.ezim.redis.RedisUtils;
import org.chc.ezim.service.UserContactService;
import org.chc.ezim.service.UserService;
import org.chc.ezim.utils.CopyTools;
import org.chc.ezim.utils.StringTools;
import org.chc.ezim.websocket.ChannelContextUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
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
    private UserContactService userContactService;

    @Resource
    private RedisUtils<String> redisUtils;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private ChannelContextUtils channelContextUtils;

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
    @GlobalAccessInterceptor
    @GetMapping("/getSetting")
    public ResponseVO getSetting() {
        SettingDto setting = redisComponent.getSetting();

        return getSuccessResponseVO(setting);
    }

    /**
     * 获取单个联系人详情（必须是好友）
     */
    @GlobalAccessInterceptor
    @GetMapping("/getContactUserInfo")
    public ResponseVO getContactUserInfo(@RequestHeader("token") String token, @NotNull String contactId) {
        TokenUserInfoDto userInfo = getTokenInfo(token);

        // 是否为好友
        UserContact userContact = userContactService.getUserContactByUserIdAndContactId(userInfo.getId(), contactId);
        if (userContact == null || ArrayUtils.contains(
                new Integer[]{
                        UserContactStatusEnum.FRIEND.getStatus(),
                        UserContactStatusEnum.DEL_BE.getStatus(),
                        UserContactStatusEnum.BLACKLIST_BE.getStatus(),
                }, userContact.getStatus())
        ) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        User user = userService.getUserById(contactId);
        UserVo userVo = CopyTools.copy(user, UserVo.class);
        userVo.setContactStatus(UserContactStatusEnum.FRIEND.getStatus());

        return getSuccessResponseVO(userVo);
    }

    /**
     * 获取登录用户信息
     */
    @GlobalAccessInterceptor
    @GetMapping("/userInfo")
    public ResponseVO getUserInfo(@RequestHeader("token") String token) {
        TokenUserInfoDto userInfo = getTokenInfo(token);
        User user = userService.getUserById(userInfo.getId());

        UserVo userVo = CopyTools.copy(user, UserVo.class);
        userVo.setAdmin(userInfo.getAdmin());

        return getSuccessResponseVO(userVo);
    }

    /**
     * 更改用户信息
     */
    @GlobalAccessInterceptor
    @PutMapping("/userInfo")
    public ResponseVO putUserInfo(
            @RequestHeader("token") String token,
            @ModelAttribute User user,
            @RequestPart(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestPart(value = "avatarCover", required = false) MultipartFile avatarCover
    ) throws IOException {
        TokenUserInfoDto userInfo = getTokenInfo(token);
        user.setId(userInfo.getId());
        user.setPassword(null);
        user.setStatus(null);
        user.setCreateTime(null);
        user.setLastLoginTime(null);

        userService.updateUserInfo(user, avatarFile, avatarCover);

        return getUserInfo(token);
    }

    /**
     * 更改用户登陆密码
     */
    @GlobalAccessInterceptor
    @PutMapping("/changePwd")
    public ResponseVO changePwd(
            @RequestHeader("token") String token,
            @RequestParam("password") @NotEmpty @Pattern(regexp = Constants.REGEX_PASSWORD) String password
    ) {
        TokenUserInfoDto userInfo = getTokenInfo(token);

        User user = new User();
        user.setPassword(StringTools.encodeMd5(password));
        userService.updateUserById(user, userInfo.getId());

        // 强制退出 重新登录
        channelContextUtils.closeContext(userInfo.getId());
        return getSuccessResponseVO(null);
    }

    /**
     * 退出登录
     */
    @GlobalAccessInterceptor
    @PostMapping("/logout")
    public ResponseVO logout(@RequestHeader("token") String token) {
        TokenUserInfoDto userInfo = getTokenInfo(token);

        // 退出登录 关闭 ws 连接
        channelContextUtils.closeContext(userInfo.getId());
        return getSuccessResponseVO(null);
    }
}
