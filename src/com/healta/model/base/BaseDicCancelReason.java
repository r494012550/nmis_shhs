package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseDicCancelReason<M extends BaseDicCancelReason<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}
	
	public void setMessage(java.lang.String message) {
		set("message", message);
	}
	
	public java.lang.String getMessage() {
		return getStr("message");
	}
	
	public void setPriority(java.lang.Integer priority) {
		set("priority", priority);
	}
	
	public java.lang.Integer getPriority() {
		return getInt("priority");
	}
	
	public void setCreatetime(java.lang.String createtime) {
		set("createtime", createtime);
	}
	
	public java.lang.String getCreatetime() {
		return getStr("createtime");
	}
	
}