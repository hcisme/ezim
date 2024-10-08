package org.chc.ezim.entity.dto;



/**
 * 联系人申请参数
 */
public class UserContactApplyDto extends BaseParam {


	/**
	 * 自增ID
	 */
	private Integer id;

	/**
	 * 申请人id
	 */
	private String applyUserId;

	private String applyUserIdFuzzy;

	/**
	 * 接收人ID
	 */
	private String receiveUserId;

	private String receiveUserIdFuzzy;

	/**
	 * 联系人类型 0:好友 1:群组
	 */
	private Integer contactType;

	/**
	 * 联系人群组ID
	 */
	private String contactId;

	private String contactIdFuzzy;

	/**
	 * 状态 0:待处理 1:已同意 2:已拒绝 3:已拉黑
	 */
	private Integer status;

	/**
	 * 申请信息
	 */
	private String applyInfo;

	private String applyInfoFuzzy;

	/**
	 * 最后申请时间
	 */
	private Long lastApplyTime;

	private Boolean queryContactInfo;

	private Long lastApplyTimeStamp;

	public Long getLastApplyTimeStamp() {
		return lastApplyTimeStamp;
	}

	public void setLastApplyTimeStamp(Long lastApplyTimeStamp) {
		this.lastApplyTimeStamp = lastApplyTimeStamp;
	}

	public Boolean getQueryContactInfo() {
		return queryContactInfo;
	}

	public void setQueryContactInfo(Boolean queryContactInfo) {
		this.queryContactInfo = queryContactInfo;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public Integer getId(){
		return this.id;
	}

	public void setApplyUserId(String applyUserId){
		this.applyUserId = applyUserId;
	}

	public String getApplyUserId(){
		return this.applyUserId;
	}

	public void setApplyUserIdFuzzy(String applyUserIdFuzzy){
		this.applyUserIdFuzzy = applyUserIdFuzzy;
	}

	public String getApplyUserIdFuzzy(){
		return this.applyUserIdFuzzy;
	}

	public void setReceiveUserId(String receiveUserId){
		this.receiveUserId = receiveUserId;
	}

	public String getReceiveUserId(){
		return this.receiveUserId;
	}

	public void setReceiveUserIdFuzzy(String receiveUserIdFuzzy){
		this.receiveUserIdFuzzy = receiveUserIdFuzzy;
	}

	public String getReceiveUserIdFuzzy(){
		return this.receiveUserIdFuzzy;
	}

	public void setContactType(Integer contactType){
		this.contactType = contactType;
	}

	public Integer getContactType(){
		return this.contactType;
	}

	public void setContactId(String contactId){
		this.contactId = contactId;
	}

	public String getContactId(){
		return this.contactId;
	}

	public void setContactIdFuzzy(String contactIdFuzzy){
		this.contactIdFuzzy = contactIdFuzzy;
	}

	public String getContactIdFuzzy(){
		return this.contactIdFuzzy;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return this.status;
	}

	public void setApplyInfo(String applyInfo){
		this.applyInfo = applyInfo;
	}

	public String getApplyInfo(){
		return this.applyInfo;
	}

	public void setApplyInfoFuzzy(String applyInfoFuzzy){
		this.applyInfoFuzzy = applyInfoFuzzy;
	}

	public String getApplyInfoFuzzy(){
		return this.applyInfoFuzzy;
	}

	public void setLastApplyTime(Long lastApplyTime){
		this.lastApplyTime = lastApplyTime;
	}

	public Long getLastApplyTime(){
		return this.lastApplyTime;
	}

	@Override
	public String toString() {
		return "UserContactApplyDto{" +
				"id=" + id +
				", applyUserId='" + applyUserId + '\'' +
				", applyUserIdFuzzy='" + applyUserIdFuzzy + '\'' +
				", receiveUserId='" + receiveUserId + '\'' +
				", receiveUserIdFuzzy='" + receiveUserIdFuzzy + '\'' +
				", contactType=" + contactType +
				", contactId='" + contactId + '\'' +
				", contactIdFuzzy='" + contactIdFuzzy + '\'' +
				", status=" + status +
				", applyInfo='" + applyInfo + '\'' +
				", applyInfoFuzzy='" + applyInfoFuzzy + '\'' +
				", lastApplyTime=" + lastApplyTime +
				", queryContactInfo=" + queryContactInfo +
				", lastApplyTimeStamp=" + lastApplyTimeStamp +
				'}';
	}
}
