package org.chc.ezim.entity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import org.chc.ezim.entity.enums.DateTimePatternEnum;
import org.chc.ezim.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 
 */
public class UserGroup implements Serializable {


	/**
	 * 群ID
	 */
	private String id;

	/**
	 * 群组名
	 */
	private String groupName;

	/**
	 * 群主id
	 */
	private String groupOwnerId;

	/**
	 * 群公告
	 */
	private String groupNotice;

	/**
	 * 0:直接加入 1:管理员同意后加入
	 */
	private Integer joinType;

	/**
	 * 1:正常 0:解散
	 */
	private Integer status;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	public UserGroup(String id, String groupName, String groupOwnerId, String groupNotice, Integer joinType) {
		this.id = id;
		this.groupName = groupName;
		this.groupOwnerId = groupOwnerId;
		this.groupNotice = groupNotice;
		this.joinType = joinType;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return this.id;
	}

	public void setGroupName(String groupName){
		this.groupName = groupName;
	}

	public String getGroupName(){
		return this.groupName;
	}

	public void setGroupOwnerId(String groupOwnerId){
		this.groupOwnerId = groupOwnerId;
	}

	public String getGroupOwnerId(){
		return this.groupOwnerId;
	}

	public void setGroupNotice(String groupNotice){
		this.groupNotice = groupNotice;
	}

	public String getGroupNotice(){
		return this.groupNotice;
	}

	public void setJoinType(Integer joinType){
		this.joinType = joinType;
	}

	public Integer getJoinType(){
		return this.joinType;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return this.status;
	}

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}

	public Date getCreateTime(){
		return this.createTime;
	}

	@Override
	public String toString (){
		return "群ID:"+(id == null ? "空" : id)+"，群组名:"+(groupName == null ? "空" : groupName)+"，群主id:"+(groupOwnerId == null ? "空" : groupOwnerId)+"，群公告:"+(groupNotice == null ? "空" : groupNotice)+"，0:直接加入 1:管理员同意后加入:"+(joinType == null ? "空" : joinType)+"，1:正常 0:解散:"+(status == null ? "空" : status)+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}
