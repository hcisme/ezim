package org.chc.ezim.redis;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.entity.dto.SettingDto;
import org.chc.ezim.entity.dto.TokenUserInfoDto;
import org.springframework.stereotype.Component;

@Component
public class RedisComponent {

    @Resource
    private RedisUtils redisUtils;

    /**
     * 获取心跳
     */
    public Long getUserHeartBeat(String userId) {
        return (Long) redisUtils.getValue(Constants.REDIS_KEY_WS_USER_HEART_BEAT + userId);
    }

    /**
     * 存 用户信息 到 redis
     */
    public void saveTokenUserInfoDto(TokenUserInfoDto tokenUserInfoDto) {
        redisUtils.setValueAndExpire(
                Constants.REDIS_KEY_WS_TOKEN + tokenUserInfoDto.getToken(),
                tokenUserInfoDto,
                Constants.REDIS_KEY_EXPIRES_DAY * 2
        );
        redisUtils.setValueAndExpire(
                Constants.REDIS_KEY_WS_TOKEN_USERID + tokenUserInfoDto.getId(),
                tokenUserInfoDto,
                Constants.REDIS_KEY_EXPIRES_DAY * 2
        );
    }

    public SettingDto getSetting() {
        SettingDto settingDto = (SettingDto) redisUtils.getValue(Constants.REDIS_KEY_SYS_SETTING);
        return settingDto == null ? new SettingDto() : settingDto;
    }
}
