package org.chc.ezim.entity.vo;

import org.chc.ezim.entity.enums.UserContactStatusEnum;

public class UserContactSearchResultVO {
    private String contactId;
    private String contactType;
    private String nickName;
    private Integer status;
    private String statusName;
    private Integer sex;
    private String areaName;

    public UserContactSearchResultVO() {
    }

    public UserContactSearchResultVO(String contactId, String contactType, String nickName, Integer status, String statusName, Integer sex, String areaName) {
        this.contactId = contactId;
        this.contactType = contactType;
        this.nickName = nickName;
        this.status = status;
        this.statusName = statusName;
        this.sex = sex;
        this.areaName = areaName;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        UserContactStatusEnum byStatus = UserContactStatusEnum.getByStatus(status);
        return byStatus == null ? null : byStatus.getDesc();
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    @Override
    public String toString() {
        return "UserContactSearchResultVO{" +
                "contactId='" + contactId + '\'' +
                ", contactType='" + contactType + '\'' +
                ", nickName='" + nickName + '\'' +
                ", status=" + status +
                ", statusName='" + statusName + '\'' +
                ", sex=" + sex +
                ", areaName='" + areaName + '\'' +
                '}';
    }
}
