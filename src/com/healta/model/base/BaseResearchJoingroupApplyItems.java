package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseResearchJoingroupApplyItems<M extends BaseResearchJoingroupApplyItems<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}
	
	public void setAppId(java.lang.Integer appId) {
		set("app_id", appId);
	}
	
	public java.lang.Integer getAppId() {
		return getInt("app_id");
	}
	
	public void setReportId(java.lang.Integer reportId) {
		set("report_id", reportId);
	}
	
	public java.lang.Integer getReportId() {
		return getInt("report_id");
	}
	
	public void setTemplateId(java.lang.Integer templateId) {
		set("template_id", templateId);
	}
	
	public java.lang.Integer getTemplateId() {
		return getInt("template_id");
	}
	
	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}
	
	public java.util.Date getCreatetime() {
		return get("createtime");
	}
	
}
