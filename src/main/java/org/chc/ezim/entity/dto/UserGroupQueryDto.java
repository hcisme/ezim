package org.chc.ezim.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

/**
 * 参数
 */
public class UserGroupQueryDto extends BaseParam {

    public interface create {
    }

    public interface update {
    }


    /**
     * 群ID
     */
    @NotEmpty(groups = update.class)
    @Length(min = 12, max = 12, groups = update.class)
    private String id;

    /**
     * 群组名
     */
    @NotEmpty(groups = {create.class, update.class})
    @Size(max = 20, groups = {create.class, update.class})
    private String groupName;

    /**
     * 群公告
     */
    @Size(max = 500, groups = {create.class, update.class})
    private String groupNotice;

    /**
     * 0:直接加入 1:管理员同意后加入
     */
    @NotNull(groups = {create.class, update.class})
    @Range(min = 0, max = 1, groups = {create.class, update.class})
    private Integer joinType;

    // @NotNull(groups = {create.class})
    // private MultipartFile avatarFile;
    //
    // @NotNull(groups = {create.class})
    // private MultipartFile avatarCover;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupNotice() {
        return groupNotice;
    }

    public void setGroupNotice(String groupNotice) {
        this.groupNotice = groupNotice;
    }

    public Integer getJoinType() {
        return joinType;
    }

    public void setJoinType(Integer joinType) {
        this.joinType = joinType;
    }

    @Override
    public String toString() {
        return "UserGroupQueryDto{" +
                "id='" + id + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupNotice='" + groupNotice + '\'' +
                ", joinType=" + joinType +
                '}';
    }
}
