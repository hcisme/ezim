package org.chc.ezim.entity.dto;

import org.chc.ezim.entity.constants.Constants;

public class SettingDto {
    private Integer maxGroupCount = 5;
    private Integer maxGroupMemberCount = 500;
    private Integer maxImageSize = 2;
    private Integer maxVideoSize = 5;
    private Integer maxFileSize = 5;

    private String robotUid = Constants.ROBOT_UID;
    private String robotNickName = "Ez-Im";
    private String robotWelcome = "欢迎使用 " + robotNickName;

    public Integer getMaxGroupCount() {
        return maxGroupCount;
    }

    public void setMaxGroupCount(Integer maxGroupCount) {
        this.maxGroupCount = maxGroupCount;
    }

    public Integer getMaxGroupMemberCount() {
        return maxGroupMemberCount;
    }

    public void setMaxGroupMemberCount(Integer maxGroupMemberCount) {
        this.maxGroupMemberCount = maxGroupMemberCount;
    }

    public Integer getMaxImageSize() {
        return maxImageSize;
    }

    public void setMaxImageSize(Integer maxImageSize) {
        this.maxImageSize = maxImageSize;
    }

    public Integer getMaxVideoSize() {
        return maxVideoSize;
    }

    public void setMaxVideoSize(Integer maxVideoSize) {
        this.maxVideoSize = maxVideoSize;
    }

    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Integer maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getRobotUid() {
        return robotUid;
    }

    public void setRobotUid(String robotUid) {
        this.robotUid = robotUid;
    }

    public String getRobotNickName() {
        return robotNickName;
    }

    public void setRobotNickName(String robotNickName) {
        this.robotNickName = robotNickName;
    }

    public String getRobotWelcome() {
        return robotWelcome;
    }

    public void setRobotWelcome(String robotWelcome) {
        this.robotWelcome = robotWelcome;
    }
}
