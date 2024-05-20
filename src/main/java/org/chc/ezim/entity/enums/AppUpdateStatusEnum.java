package org.chc.ezim.entity.enums;

import java.util.Arrays;

public enum AppUpdateStatusEnum {

    INIT(0,"未发布"),
    GRAYSCALE(1,"灰度发布"),
    ALL(2,"全网发布");

    private Integer status;
    private String desc;

    AppUpdateStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static AppUpdateStatusEnum getByStatus(Integer status) {
        return Arrays.stream(AppUpdateStatusEnum.values()).filter(item -> item.getStatus().equals(status)).findFirst().orElse(null);
    }
}
