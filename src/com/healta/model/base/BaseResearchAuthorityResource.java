package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseResearchAuthorityResource<M extends BaseResearchAuthorityResource<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}
	
	public void setAuthority(java.lang.Integer authority) {
		set("authority", authority);
	}
	
	public java.lang.Integer getAuthority() {
		return getInt("authority");
	}
	
	public void setResource(java.lang.Integer resource) {
		set("resource", resource);
	}
	
	public java.lang.Integer getResource() {
		return getInt("resource");
	}
	
}
