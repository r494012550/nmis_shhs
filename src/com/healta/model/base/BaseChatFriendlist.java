package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseChatFriendlist<M extends BaseChatFriendlist<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setMineid(java.lang.Integer mineid) {
		set("mineid", mineid);
	}
	
	public java.lang.Integer getMineid() {
		return getInt("mineid");
	}

	public void setFriendid(java.lang.Integer friendid) {
		set("friendid", friendid);
	}
	
	public java.lang.Integer getFriendid() {
		return getInt("friendid");
	}

	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}
	
	public java.util.Date getCreatetime() {
		return get("createtime");
	}

}
