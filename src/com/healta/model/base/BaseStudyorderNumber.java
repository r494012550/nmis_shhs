package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseStudyorderNumber<M extends BaseStudyorderNumber<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setModalityid(java.lang.Integer modalityid) {
		set("modalityid", modalityid);
	}
	
	public java.lang.Integer getModalityid() {
		return getInt("modalityid");
	}

	public void setSequencenumber(java.lang.Integer sequencenumber) {
		set("sequencenumber", sequencenumber);
	}
	
	public java.lang.Integer getSequencenumber() {
		return getInt("sequencenumber");
	}

	public void setScopeoftime(java.lang.Integer scopeoftime) {
		set("scopeoftime", scopeoftime);
	}
	
	public java.lang.Integer getScopeoftime() {
		return getInt("scopeoftime");
	}

	public void setStatus(java.lang.String status) {
		set("status", status);
	}
	
	public java.lang.String getStatus() {
		return getStr("status");
	}

	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}
	
	public java.util.Date getCreatetime() {
		return get("createtime");
	}

}
