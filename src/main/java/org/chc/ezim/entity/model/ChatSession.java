package org.chc.ezim.entity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;


/**
 * 会话信息
 */
public class ChatSession implements Serializable {


	/**
	 * 会话ID
	 */
	private String id;

	/**
	 * 最后接受的消息
	 */
	private String lastMessage;

	/**
	 * 最后接受消息时间毫秒
	 */
	private Long lastReceiveTime;


	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return this.id;
	}

	public void setLastMessage(String lastMessage){
		this.lastMessage = lastMessage;
	}

	public String getLastMessage(){
		return this.lastMessage;
	}

	public void setLastReceiveTime(Long lastReceiveTime){
		this.lastReceiveTime = lastReceiveTime;
	}

	public Long getLastReceiveTime(){
		return this.lastReceiveTime;
	}

	@Override
	public String toString (){
		return "会话ID:"+(id == null ? "空" : id)+"，最后接受的消息:"+(lastMessage == null ? "空" : lastMessage)+"，最后接受消息时间毫秒:"+(lastReceiveTime == null ? "空" : lastReceiveTime);
	}
}
