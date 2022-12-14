package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUserAuditLogs<M extends BaseUserAuditLogs<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}
	
	public void setUserfk(java.lang.Integer userfk) {
		set("userfk", userfk);
	}
	
	public java.lang.Integer getUserfk() {
		return getInt("userfk");
	}
	
	public void setAuditTime(java.util.Date auditTime) {
		set("audit_time", auditTime);
	}
	
	public java.util.Date getAuditTime() {
		return get("audit_time");
	}
	
	public void setAuditType(java.lang.String auditType) {
		set("audit_type", auditType);
	}
	
	public java.lang.String getAuditType() {
		return getStr("audit_type");
	}
	
	public void setIp(java.lang.String ip) {
		set("ip", ip);
	}
	
	public java.lang.String getIp() {
		return getStr("ip");
	}
	
	public void setDuration(java.lang.Long duration) {
		set("duration", duration);
	}
	
	public java.lang.Long getDuration() {
		return getLong("duration");
	}
	
	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}
	
	public java.util.Date getCreatetime() {
		return get("createtime");
	}
	
}
