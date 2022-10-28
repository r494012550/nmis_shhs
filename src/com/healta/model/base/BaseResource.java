package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseResource<M extends BaseResource<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}
	
	public java.lang.String getName() {
		return getStr("name");
	}

	public void setResource(java.lang.String resource) {
		set("resource", resource);
	}
	
	public java.lang.String getResource() {
		return getStr("resource");
	}

	public void setDisplayZhCn(java.lang.String displayZhCn) {
		set("display_zh_CN", displayZhCn);
	}
	
	public java.lang.String getDisplayZhCn() {
		return getStr("display_zh_CN");
	}

	public void setDisplayEnUs(java.lang.String displayEnUs) {
		set("display_en_US", displayEnUs);
	}
	
	public java.lang.String getDisplayEnUs() {
		return getStr("display_en_US");
	}

	public void setRid(java.lang.Integer rid) {
		set("rid", rid);
	}
	
	public java.lang.Integer getRid() {
		return getInt("rid");
	}

}