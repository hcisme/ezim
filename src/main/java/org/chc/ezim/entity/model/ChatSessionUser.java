package org.chc.ezim.entity.model;

import org.chc.ezim.entity.enums.UserContactTypeEnum;
import org.chc.ezim.utils.StringTools;

import java.io.Serializable;


/**
 * 会话用户
 */
public class ChatSessionUser implements Serializable {


    /**
     * 用户ID
     */
    private String userId;

    /**
     * 联系人ID
     */
    private String contactId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 联系人名称
     */
    private String contactName;

    /**
     * 关联 chat_session 表后
     */
    private String lastMessage;

    /**
     * 关联 chat_session 表后
     */
    private String lastReceiveTime;

    private Integer contactType;

    /**
     * 群成员人数
     */
    private Integer memberCount;


    public Integer getContactType() {
        if (StringTools.isEmpty(contactId)) {
            return null;
        }
        return UserContactTypeEnum.getByPrefix(contactId).getType();
    }

    public void setContactType(Integer contactType) {
        this.contactType = contactType;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactId() {
        return this.contactId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactName() {
        return this.contactName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastReceiveTime() {
        return lastReceiveTime;
    }

    public void setLastReceiveTime(String lastReceiveTime) {
        this.lastReceiveTime = lastReceiveTime;
    }

    @Override
    public String toString() {
        return "ChatSessionUser{" +
                "userId='" + userId + '\'' +
                ", contactId='" + contactId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", contactName='" + contactName + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastReceiveTime='" + lastReceiveTime + '\'' +
                ", contactType=" + contactType +
                ", memberCount=" + memberCount +
                '}';
    }
}
