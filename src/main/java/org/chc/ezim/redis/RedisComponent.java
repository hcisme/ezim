package org.chc.ezim.redis;

import jakarta.annotation.Resource;
import org.chc.ezim.entity.constants.Constants;
import org.chc.ezim.entity.dto.SettingDto;
import org.chc.ezim.entity.dto.TokenUserInfoDto;
import org.chc.ezim.utils.StringTools;
import org.springframework.stereotype.Component;

import java.util.List;

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
     * 保存心跳
     */
    public void saveUserHeartBeat(String userId) {
        redisUtils.setValueAndExpire(
                Constants.REDIS_KEY_WS_USER_HEART_BEAT + userId,
                System.currentTimeMillis(),
                Constants.REDIS_KEY_EXPIRES_HEART_BEAT
        );
    }

    /**
     * 移除心跳
     */
    public void removeUserHeartBeat(String userId) {
        redisUtils.delete(Constants.REDIS_KEY_WS_USER_HEART_BEAT + userId);
    }

    /**
     * 通过 token 获取用户信息
     */
    public TokenUserInfoDto getUserInfoByToken(String token) {
        return (TokenUserInfoDto) redisUtils.getValue(Constants.REDIS_KEY_WS_TOKEN + token);
    }

    /**
     * 通过 token 获取用户信息
     */
    public TokenUserInfoDto getUserInfoByUserId(String userId) {
        return (TokenUserInfoDto) redisUtils.getValue(Constants.REDIS_KEY_WS_TOKEN_USERID + userId);
    }

    /**
     * 清空指定用户的 token
     */
    public void cleanTokenByUserId(String userId) {
        String token = ((TokenUserInfoDto) redisUtils.getValue(Constants.REDIS_KEY_WS_TOKEN_USERID + userId)).getToken();
        if (!StringTools.isEmpty(token)) {
            redisUtils.delete(Constants.REDIS_KEY_WS_TOKEN + token);
        }
        redisUtils.delete(Constants.REDIS_KEY_WS_TOKEN_USERID + userId);
    }

    /**
     * 存 用户信息 到 redis
     */
    public void saveTokenUserInfoDto(TokenUserInfoDto tokenUserInfoDto) {
        redisUtils.setValueAndExpire(
                Constants.REDIS_KEY_WS_TOKEN + tokenUserInfoDto.getToken(),
                tokenUserInfoDto,
                Constants.REDIS_KEY_TOKEN_EXPIRES
        );
        redisUtils.setValueAndExpire(
                Constants.REDIS_KEY_WS_TOKEN_USERID + tokenUserInfoDto.getId(),
                tokenUserInfoDto,
                Constants.REDIS_KEY_TOKEN_EXPIRES
        );
    }

    public SettingDto getSetting() {
        SettingDto settingDto = (SettingDto) redisUtils.getValue(Constants.REDIS_KEY_SYS_SETTING);
        return settingDto == null ? new SettingDto() : settingDto;
    }

    public void saveSetting(SettingDto settingDto) {
        redisUtils.setValue(Constants.REDIS_KEY_SYS_SETTING, settingDto);
    }

    /**
     * 清空 redis 里的联系人
     */
    public void cleanUserContact(String userId) {
        redisUtils.delete(Constants.REDIS_KEY_USER_CONTACT + userId);
    }

    /**
     * 批量添加联系人到 redis
     */
    public void addUserContactBatch(String userId, List<String> contactIdList) {
        redisUtils.IPush(Constants.REDIS_KEY_USER_CONTACT + userId, contactIdList, Constants.REDIS_KEY_TOKEN_EXPIRES);
    }

    /**
     * 单独添加联系人到 redis
     */
    public void addSingleUserContact(String userId, String contactId) {
        List<String> userContactIdList = getUserContactIdList(userId);
        if (userContactIdList.contains(contactId)) {
            return;
        }

        redisUtils.IPush(Constants.REDIS_KEY_USER_CONTACT + userId, contactId, Constants.REDIS_KEY_TOKEN_EXPIRES);
    }

    /**
     * 获取当前用户的联系人id列表
     */
    public List<String> getUserContactIdList(String userId) {
        return (List<String>) redisUtils.getQueueList(Constants.REDIS_KEY_USER_CONTACT + userId);
    }

    /**
     * 删除联系人
     */
    public void removeUserContact(String userId, String contactId) {
        redisUtils.remove(Constants.REDIS_KEY_USER_CONTACT + userId, contactId);
    }
}
