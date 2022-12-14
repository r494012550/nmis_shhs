package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseClient<M extends BaseClient<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setHostname(java.lang.String hostname) {
		set("hostname", hostname);
	}
	
	public java.lang.String getHostname() {
		return getStr("hostname");
	}

	public void setHostip(java.lang.String hostip) {
		set("hostip", hostip);
	}
	
	public java.lang.String getHostip() {
		return getStr("hostip");
	}

	public void setType(java.lang.String type) {
		set("type", type);
	}
	
	public java.lang.String getType() {
		return getStr("type");
	}

	public void setModalityid(java.lang.Integer modalityid) {
		set("modalityid", modalityid);
	}
	
	public java.lang.Integer getModalityid() {
		return getInt("modalityid");
	}
	
	public void setZone(java.lang.String zone) {
		set("zone", zone);
	}
	
	public java.lang.String getZone() {
		return getStr("zone");
	}

}
