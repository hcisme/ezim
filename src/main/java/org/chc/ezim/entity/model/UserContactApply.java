package org.chc.ezim.entity.model;

import org.chc.ezim.entity.enums.UserContactApplyStatusEnum;

import java.io.Serializable;


/**
 * 联系人申请
 */
public class UserContactApply implements Serializable {


    /**
     * 自增ID
     */
    private Integer id;

    /**
     * 申请人id
     */
    private String applyUserId;

    /**
     * 接收人ID
     */
    private String receiveUserId;

    /**
     * 联系人类型 0:好友 1:群组
     */
    private Integer contactType;

    /**
     * 联系人群组ID
     */
    private String contactId;

    /**
     * 状态 0:待处理 1:已同意 2:已拒绝 3:已拉黑
     */
    private Integer status;

    /**
     * 申请信息
     */
    private String applyInfo;

    /**
     * 最后申请时间
     */
    private Long lastApplyTime;

    private String contactName;

    private String statusName;

    public UserContactApply() {
    }

    public UserContactApply(String applyUserId, String receiveUserId, Integer contactType, String contactId, Integer status, String applyInfo, Long lastApplyTime) {
        this.applyUserId = applyUserId;
        this.receiveUserId = receiveUserId;
        this.contactType = contactType;
        this.contactId = contactId;
        this.status = status;
        this.applyInfo = applyInfo;
        this.lastApplyTime = lastApplyTime;
    }

    public UserContactApply(Integer status, String applyInfo, Long lastApplyTime) {
        this.status = status;
        this.applyInfo = applyInfo;
        this.lastApplyTime = lastApplyTime;
    }

    public String getStatusName() {
        UserContactApplyStatusEnum byStatus = UserContactApplyStatusEnum.getByStatus(status);
        return byStatus == null ? null : byStatus.getDesc();
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getApplyUserId() {
        return this.applyUserId;
    }

    public void setReceiveUserId(String receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public String getReceiveUserId() {
        return this.receiveUserId;
    }

    public void setContactType(Integer contactType) {
        this.contactType = contactType;
    }

    public Integer getContactType() {
        return this.contactType;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactId() {
        return this.contactId;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setApplyInfo(String applyInfo) {
        this.applyInfo = applyInfo;
    }

    public String getApplyInfo() {
        return this.applyInfo;
    }

    public void setLastApplyTime(Long lastApplyTime) {
        this.lastApplyTime = lastApplyTime;
    }

    public Long getLastApplyTime() {
        return this.lastApplyTime;
    }

    @Override
    public String toString() {
        return "UserContactApply{" +
                "id=" + id +
                ", applyUserId='" + applyUserId + '\'' +
                ", receiveUserId='" + receiveUserId + '\'' +
                ", contactType=" + contactType +
                ", contactId='" + contactId + '\'' +
                ", status=" + status +
                ", applyInfo='" + applyInfo + '\'' +
                ", lastApplyTime=" + lastApplyTime +
                '}';
    }
}
