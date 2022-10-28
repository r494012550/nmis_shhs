package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseAuthorityResource<M extends BaseAuthorityResource<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setAuthorityId(java.lang.Integer authorityId) {
		set("authority_id", authorityId);
	}

	public java.lang.Integer getAuthorityId() {
		return get("authority_id");
	}

	public void setResourceId(java.lang.Integer resourceId) {
		set("resource_id", resourceId);
	}

	public java.lang.Integer getResourceId() {
		return get("resource_id");
	}

}