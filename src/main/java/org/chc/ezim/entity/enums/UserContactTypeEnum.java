package org.chc.ezim.entity.enums;

import org.chc.ezim.utils.StringTools;

import java.util.Arrays;

/**
 * 用户之间关系
 */
public enum UserContactTypeEnum {
    USER(0, "U", "好友"),
    GROUP(1, "G", "群聊");

    private final Integer type;
    private final String prefix;
    private final String desc;

    UserContactTypeEnum(Integer type, String prefix, String desc) {
        this.type = type;
        this.prefix = prefix;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "UserContactTypeEnum{" +
                "type=" + type +
                ", prefix='" + prefix + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    /**
     * 通过名称获取对应枚举信息
     * 
     */
    public static UserContactTypeEnum getByName(String prefix) {
        try {
            if (StringTools.isEmpty(prefix) || prefix.trim().isEmpty()) {
                return null;
            }
            String c = prefix.substring(0, 1);
            return Arrays.stream(UserContactTypeEnum.values())
                    .filter(userContactType -> userContactType.getPrefix().equals(c))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
