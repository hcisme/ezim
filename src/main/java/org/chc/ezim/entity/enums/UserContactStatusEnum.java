package org.chc.ezim.entity.enums;

import org.chc.ezim.utils.StringTools;

import java.util.Arrays;

public enum UserContactStatusEnum {
    NOT_FRIEND(0, "非好友"),
    FRIEND(1, "好友"),
    DEL(2, "已删除好友"),
    DEL_BE(3, "被好友删除"),
    BLACKLIST(4, "已拉黑好友"),
    BLACKLIST_BE(5, "被好友拉黑");

    private final Integer status;

    private final String desc;

    UserContactStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static UserContactStatusEnum getByStatus(Integer status) {
        return Arrays.stream(UserContactStatusEnum.values())
                .filter(userContactStatusEnum -> userContactStatusEnum.getStatus().equals(status))
                .findFirst()
                .orElse(null);
    }

    public static UserContactStatusEnum getByName(String name) {
        if (StringTools.isEmpty(name) || name.trim().isEmpty()) {
            return null;
        }
        return Arrays.stream(UserContactStatusEnum.values())
                .filter(userContactStatusEnum -> userContactStatusEnum.getDesc().equals(name))
                .findFirst()
                .orElse(null);
    }
}
