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

    public static final String REDIS_KEY_SYS_SETTING = "ezim:sys:setting";

    public static final Integer REDIS_TIME_1MIN = 60;

    public static final Integer REDIS_KEY_EXPIRES_HEART_BEAT = 6 * 600;

    public static final Integer REDIS_KEY_EXPIRES_DAY = REDIS_TIME_1MIN * 60 * 24;

    /**
     * token 失效时间 12天
     */
    public static final Integer REDIS_KEY_TOKEN_EXPIRES = REDIS_KEY_EXPIRES_DAY * 12;


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

    public static final String FILE_FOLDER_UPLOAD_FILE_NAME = "uploadFile/";

    public static final String APP_UPDATE_FOLDER = "app/";

    public static final String APP_APK_SUFFIX = ".apk";

    public static final String FILE_FOLDER_AVATAR_NAME = "avatar/";

    public static final String IMAGE_SUFFIX = ".png";

    public static final String COVER_IMAGE_SUFFIX = "_cover.png";

    /**
     * 申请默认验证消息
     */
    public static final String APPLY_INFO_TEMPLATE = "我是 %s";

    /**
     * 密码正则
     * <p> 密码必须是8到18个字符，至少包含一个数字，至少包含一个字母，可以包含特殊字符 ~!@#$%^&*_
     */
    public static final String REGEX_PASSWORD = "^(?=.*\\d)(?=.*[a-zA-Z])[\\da-zA-Z~!@#$%^&*_]{8,18}$";

    public static final String APP_NAME = "Ezim";

    /**
     * 用户联系人列表的key
     */
    public static final String REDIS_KEY_USER_CONTACT = "ezim:ws:user:contact:";

    /**
     *
     */
    public static final Long MillisSECONDS_3DAYS_ago = 3 * 24 * 60 * 60 * 1000L;

    public static final String[] IMAGE_SUFFIX_LIST = new String[]{".jpeg", ".jpg", ".png", ".gif", ".bmp", ".webp"};

    public static final String[] VIDEO_SUFFIX_LIST = new String[]{".mp4", ".avi", ".rmvb", ".mkv", ".mov"};

    public static final Long FILE_SIZE_MB = 1024 * 1024L;

    /**
     * 从群聊中移除人
     */
    public static final Integer ZERO = 0;

    /**
     * 添加人到群聊
     */
    public static final Integer ONE = 1;
}
