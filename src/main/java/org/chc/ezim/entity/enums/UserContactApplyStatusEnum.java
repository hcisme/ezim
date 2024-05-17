package org.chc.ezim.entity.enums;

import org.chc.ezim.utils.StringTools;

import java.util.Arrays;

public enum UserContactApplyStatusEnum {
    INIT(0, "待处理"),
    PASS(1, "已同意"),
    REJECT(2, "已拒绝"),
    BLACKLIST(3, "已拉黑");

    private final Integer status;
    private final String desc;

    UserContactApplyStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static UserContactApplyStatusEnum getByStatus(Integer status) {
        return Arrays.stream(UserContactApplyStatusEnum.values())
                .filter(item -> item.getStatus().equals(status))
                .findFirst()
                .orElse(null);
    }

    public static UserContactApplyStatusEnum getByStatus(String status) {
        try {
            if (StringTools.isEmpty(status)) {
                return null;
            }
            return UserContactApplyStatusEnum.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
