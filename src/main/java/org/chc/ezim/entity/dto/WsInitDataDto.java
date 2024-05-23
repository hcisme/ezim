package org.chc.ezim.entity.dto;

import org.chc.ezim.entity.model.ChatMessage;
import org.chc.ezim.entity.model.ChatSessionUser;

import java.util.List;

public class WsInitDataDto {
    private List<ChatSessionUser> chatSessionList;

    private List<ChatMessage> chatMessageList;

    private Integer applyCount;

    public List<ChatSessionUser> getChatSessionList() {
        return chatSessionList;
    }

    public void setChatSessionList(List<ChatSessionUser> chatSessionList) {
        this.chatSessionList = chatSessionList;
    }

    public List<ChatMessage> getChatMessageList() {
        return chatMessageList;
    }

    public void setChatMessageList(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
    }

    public Integer getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(Integer applyCount) {
        this.applyCount = applyCount;
    }

    @Override
    public String toString() {
        return "WsInitDataDto{" +
                "chatSessionList=" + chatSessionList +
                ", chatMessageList=" + chatMessageList +
                ", applyCount=" + applyCount +
                '}';
    }
}
