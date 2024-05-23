package org.chc.ezim.entity.dto;

import org.chc.ezim.utils.StringTools;

import java.io.Serializable;

public class MessageSendDto<T> implements Serializable {
    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 消息内容
     */
    private String messageContent;

    /**
     * 发送人ID
     */
    private String sendUserId;

    /**
     * 发送人昵称
     */
    private String sendUserNickName;

    /**
     * 发送时间
     */
    private Long sendTime;

    /**
     * 接收联系人ID
     */
    private String contactId;

    /**
     * 接收联系人昵称
     */
    private String contactName;

    /**
     * 联系人类型  0：单聊  1：群聊
     */
    private Integer contactType;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型
     */
    private Integer fileType;

    /**
     * 状态   0：正在发送   1：已发送
     */
    private Integer status;

    /**
     * 最后的消息
     */
    private String lastMessage;

    /**
     * 扩展信息
     */
    private T extendData;

    /**
     * 群成员数量
     */
    private Integer memberCount;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(String sendUserId) {
        this.sendUserId = sendUserId;
    }

    public String getSendUserNickName() {
        return sendUserNickName;
    }

    public void setSendUserNickName(String sendUserNickName) {
        this.sendUserNickName = sendUserNickName;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Integer getContactType() {
        return contactType;
    }

    public void setContactType(Integer contactType) {
        this.contactType = contactType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLastMessage() {
        if (StringTools.isEmpty(lastMessage)) {
            return messageContent;
        }
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public T getExtendData() {
        return extendData;
    }

    public void setExtendData(T extendData) {
        this.extendData = extendData;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    @Override
    public String toString() {
        return "MessageSendDto{" +
                "messageId=" + messageId +
                ", sessionId='" + sessionId + '\'' +
                ", messageType=" + messageType +
                ", messageContent='" + messageContent + '\'' +
                ", sendUserId='" + sendUserId + '\'' +
                ", sendUserNickName='" + sendUserNickName + '\'' +
                ", sendTime=" + sendTime +
                ", contactId='" + contactId + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactType=" + contactType +
                ", fileSize=" + fileSize +
                ", fileName='" + fileName + '\'' +
                ", fileType=" + fileType +
                ", status=" + status +
                ", lastMessage='" + lastMessage + '\'' +
                ", extendData='" + extendData + '\'' +
                ", memberCount=" + memberCount +
                '}';
    }
}
