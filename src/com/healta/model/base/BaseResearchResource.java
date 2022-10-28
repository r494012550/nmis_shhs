package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseResearchResource<M extends BaseResearchResource<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}
	
	public void setResourceName(java.lang.String resourceName) {
		set("resource_name", resourceName);
	}
	
	public java.lang.String getResourceName() {
		return getStr("resource_name");
	}
	
	public void setResourceValue(java.lang.String resourceValue) {
		set("resource_value", resourceValue);
	}
	
	public java.lang.String getResourceValue() {
		return getStr("resource_value");
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
	
	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}
	
	public java.util.Date getCreatetime() {
		return get("createtime");
	}
	
	public void setCreator(java.lang.String creator) {
		set("creator", creator);
	}
	
	public java.lang.String getCreator() {
		return getStr("creator");
	}
	
}
