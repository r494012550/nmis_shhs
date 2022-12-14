package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseTaskRolePersonnel<M extends BaseTaskRolePersonnel<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}
	
	public void setPersonnelId(java.lang.Integer personnelId) {
		set("personnelId", personnelId);
	}
	
	public java.lang.Integer getPersonnelId() {
		return getInt("personnelId");
	}
	
	public void setRoleId(java.lang.Integer roleId) {
		set("roleId", roleId);
	}
	
	public java.lang.Integer getRoleId() {
		return getInt("roleId");
	}
	
	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}
	
	public java.util.Date getCreatetime() {
		return get("createtime");
	}
	
}
