package org.chc.ezim.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ChatSendMessageDto {
    @NotEmpty
    private String contactId;

    @NotEmpty
    @Size(max = 500)
    private String messageContent;

    @NotNull
    private Integer messageType;

    private Long fileSize;

    private String fileName;

    private Integer fileType;

    public @NotEmpty String getContactId() {
        return contactId;
    }

    public void setContactId(@NotEmpty String contactId) {
        this.contactId = contactId;
    }

    public @NotEmpty @Size(max = 500) String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(@NotEmpty @Size(max = 500) String messageContent) {
        this.messageContent = messageContent;
    }

    public @NotNull Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(@NotNull Integer messageType) {
        this.messageType = messageType;
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

    @Override
    public String toString() {
        return "ChatSendMessageDto{" +
                "contactId='" + contactId + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", messageType=" + messageType +
                ", fileSize=" + fileSize +
                ", fileName='" + fileName + '\'' +
                ", fileType=" + fileType +
                '}';
    }
}
