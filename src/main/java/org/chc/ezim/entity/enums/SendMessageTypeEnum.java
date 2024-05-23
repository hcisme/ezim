package org.chc.ezim.entity.enums;

import java.util.Arrays;

public enum SendMessageTypeEnum {
    INIT(0, "", "连接ws获取信息"),

    ADD_FRIEND(1, "", "添加好友打招呼消息"),

    CHAT(2, "", "普通聊天消息"),

    GROUP_CREATE(3, "群组已经创建好,可以和好友一起畅聊了", "群创建成功"),

    CONTACT_APPLY(4, "", "好友申请"),

    MEDIA_CHAT(5, "", "媒体文件"),

    FILE_UPLOAD(6, "", "文件上传完成"),

    FORCE_OFF_LINE(7, "", "强制下线"),

    DISSOLUTION_GROUP(8, "群聊已解散", "解散群聊"),

    ADD_GROUP(9, "%s 加入了群组", "加入群聊"),

    CONTACT_NAME_UPDATE(10, "", "更新昵称"),

    LEAVE_GROUP(11, "%s 退出了群聊", "退出群聊"),

    REMOVE_GROUP(12, "%s 被管理员移出了群聊", "被管理员移出了群聊"),

    ADD_FRIEND_SELF(13, "", "添加好友打招呼消息");

    private final Integer type;
    private final String initMessage;
    private final String desc;

    SendMessageTypeEnum(Integer type, String initMessage, String desc) {
        this.type = type;
        this.initMessage = initMessage;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getInitMessage() {
        return initMessage;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 通过 type 获取对应枚举信息
     */
    public static SendMessageTypeEnum getByType(Integer type) {
        return Arrays.stream(SendMessageTypeEnum.values()).filter(item -> item.getType().equals(type)).findFirst().orElse(null);
    }
}
