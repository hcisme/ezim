package org.chc.ezim.entity.enums;

import java.util.Arrays;

public enum MessageStatusEnum {
    SENDING(0, "发送中"),
    SENDED(1, "已发送");

    private final Integer status;
    private final String desc;

    MessageStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static MessageStatusEnum getByStatus(Integer status) {
        return Arrays.stream(MessageStatusEnum.values()).filter(item -> item.getStatus().equals(status)).findFirst().orElse(null);
    }
}
