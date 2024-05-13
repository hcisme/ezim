package org.chc.ezim.entity.enums;

import java.util.Arrays;

public enum UserStatusEnum {
    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    private Integer status;
    private String desc;

    UserStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 通过名称获取对应枚举信息
     */
    public static UserStatusEnum getByStatus(Integer status) {
        return Arrays.stream(UserStatusEnum.values()).filter(item -> item.getStatus().equals(status)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "UserStatusEnum{" +
                "status=" + status +
                ", desc='" + desc + '\'' +
                '}';
    }
}
