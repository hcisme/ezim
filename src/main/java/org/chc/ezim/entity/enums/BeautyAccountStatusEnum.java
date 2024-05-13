package org.chc.ezim.entity.enums;

import java.util.Arrays;

/**
 * 靓号状态 是否能启用
 */
public enum BeautyAccountStatusEnum {
    NO_USE(0, "未使用"),
    USED(1, "已使用");

    private final Integer status;
    private final String desc;

    BeautyAccountStatusEnum(Integer status, String desc) {
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
    public static BeautyAccountStatusEnum getByStatus(Integer status) {
        return Arrays.stream(BeautyAccountStatusEnum.values()).filter(item -> item.getStatus().equals(status)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "BeautyAccountStatusEnum{" +
                "status=" + status +
                ", desc='" + desc + '\'' +
                '}';
    }
}
