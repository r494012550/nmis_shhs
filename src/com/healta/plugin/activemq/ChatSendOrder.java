package com.healta.plugin.activemq;

import java.io.Serializable;

public class ChatSendOrder extends BaseJmsOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4049114411726548719L;
	
	private Integer userId;
	private String message;
	
//	private int sendCount;

	/**
	 * 
	 * @param userId 用户id
	 * @param message json下次对象
	 */
	public ChatSendOrder(Integer userId, String message) {
		this.userId = userId;
		this.message = message;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
