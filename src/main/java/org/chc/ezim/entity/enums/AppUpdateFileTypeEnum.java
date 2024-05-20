package org.chc.ezim.entity.enums;

import java.util.Arrays;

public enum AppUpdateFileTypeEnum {

    LOCAL(0,"本地"),
    OUTER_LINK(1,"外链");
    
    private Integer type;
    private String desc;

    AppUpdateFileTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static AppUpdateFileTypeEnum getByType(Integer type) {
        return Arrays.stream(AppUpdateFileTypeEnum.values()).filter(item -> item.getType().equals(type)).findFirst().orElse(null);
    }
}
