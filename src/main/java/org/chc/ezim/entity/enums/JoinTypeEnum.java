package org.chc.ezim.entity.enums;

import org.chc.ezim.utils.StringTools;

import java.util.Arrays;

public enum JoinTypeEnum {
    JOIN(0, "直接加入"),
    APPLY(1, "需要审核");

    private final Integer type;
    private final String desc;

    JoinTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 通过 type 获取对应枚举信息
     */
    public static JoinTypeEnum getByType(Integer type) {
        return Arrays.stream(JoinTypeEnum.values()).filter(item -> item.getType().equals(type)).findFirst().orElse(null);
    }

    public static JoinTypeEnum getByName(String name) {
        try {
            if (StringTools.isEmpty(name)) {
                return null;
            }
            return JoinTypeEnum.valueOf(name);
        } catch (Exception e) {
            return null;
        }
    }
}
