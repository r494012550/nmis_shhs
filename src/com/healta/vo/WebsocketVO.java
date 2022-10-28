package com.healta.vo;

import com.alibaba.fastjson.JSONObject;

public class WebsocketVO {

	public final static String SCANAPPFORM="scan";
	public final static String TRIGGERSCANAPPFORM="triggerscan";
	public final static String RECEIVEVIAREPORT="viareport";
	public final static String ONLINE_REMINDER="onlinereminder";
	public final static String OFFLINE_REMINDER="offlinereminder";
	public final static String MESSAGE_CONTENT="messagecontent";
	public final static String APPLY_FRIEND_GROUP = "applyfriendgroup";
	public final static String MESSAGE_BOX = "messageBox";
	public final static String SESSIONOVER="sessionover";
	public final static String NOTICE = "notice";
	public final static String REPORTPRINTSTATUS="printstatus";
	public final static String CHECKREPORTPRINTSTATUS="checkprintstatus";
	public final static String RECEIVEVIADATA_CLIPBOARD="viareport_clipboard";
	/**
	 *  此账号在其他地方登入
	 */
	public final static String ACCOUNT_ELSE_LOGIN="accountelselogin";
	
	String op_type;
	Object data;
	
	
	public WebsocketVO(String type,Object data){
		this.op_type=type;
		this.data=data;
	}
	
	public String getOp_type() {
		return op_type;
	}
	public void setOp_type(String op_type) {
		this.op_type = op_type;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	public String toJson(){
		return JSONObject.toJSONString(this);
	}
}
