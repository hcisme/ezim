package org.chc.ezim.entity.constants;

import org.chc.ezim.entity.enums.UserContactTypeEnum;

public class Constants {
    /**
     * redis 相关
     */
    public static final String REDIS_KEY_CAPTCHA = "ezim:captcha-";

    public static final String REDIS_KEY_WS_USER_HEART_BEAT = "ezim:ws:user:heartbeart:";

    public static final String REDIS_KEY_WS_TOKEN = "ezim:ws:token:";

    public static final String REDIS_KEY_WS_TOKEN_USERID = "ezim:ws:token:userid:";

    public static final String REDIS_KEY_SYS_SETTING = "ezim:sys:setting:";

    public static final Integer REDIS_TIME_1MIN = 60;

    public static final Integer REDIS_KEY_EXPIRES_DAY = REDIS_TIME_1MIN * 60 * 24;


    /**
     * 数字相关
     */
    public static final Integer LENGTH_11 = 11;

    public static final Integer LENGTH_20 = 20;

    /**
     * robot
     */
    public static final String ROBOT_UID = UserContactTypeEnum.USER.getPrefix() + "robot";

    /**
     * 路径相关
     */
    public static final String FILE_FOLDER = "file/";

    public static final String FILE_FOLDER_AVATAR_NAME = "avatar/";

    public static final String IMAGE_SUFFIX = ".png";

    public static final String COVER_IMAGE_SUFFIX = "_cover.png";

    /**
     * 申请默认验证消息
     */
    public static final String APPLY_INFO_TEMPLATE = "我是 %s";
}
