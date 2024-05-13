package org.chc.ezim.entity.dto;

import java.io.Serializable;

public class TokenUserInfoDto implements Serializable {

    private String token;
    private String id;
    private String nickName;
    private Boolean admin;

    public TokenUserInfoDto() {
    }

    public TokenUserInfoDto(String token, String id, String nickName, Boolean admin) {
        this.token = token;
        this.id = id;
        this.nickName = nickName;
        this.admin = admin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
