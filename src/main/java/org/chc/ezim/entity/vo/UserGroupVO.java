package org.chc.ezim.entity.vo;

import org.chc.ezim.entity.model.UserContact;
import org.chc.ezim.entity.model.UserGroup;

import java.util.List;

public class UserGroupVO {
    private UserGroup userGroup;
    private List<UserContact> userContactList;

    public UserGroupVO() {
    }

    public UserGroupVO(UserGroup userGroup, List<UserContact> userContactList) {
        this.userGroup = userGroup;
        this.userContactList = userContactList;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public List<UserContact> getUserContactList() {
        return userContactList;
    }

    public void setUserContactList(List<UserContact> userContactList) {
        this.userContactList = userContactList;
    }
}
