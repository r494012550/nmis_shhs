package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseReportlabel<M extends BaseReportlabel<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setLabelfk(java.lang.Integer labelfk) {
		set("labelfk", labelfk);
	}
	
	public java.lang.Integer getLabelfk() {
		return getInt("labelfk");
	}

	public void setReportfk(java.lang.Integer reportfk) {
		set("reportfk", reportfk);
	}
	
	public java.lang.Integer getReportfk() {
		return getInt("reportfk");
	}
	
	public void setIspublic(java.lang.String ispublic) {
		set("ispublic", ispublic);
	}
	
	public java.lang.String getIspublic() {
		return getStr("ispublic");
	}

	public void setCreator(java.lang.Integer creator) {
		set("creator", creator);
	}
	
	public java.lang.Integer getCreator() {
		return getInt("creator");
	}

	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}
	
	public java.util.Date getCreatetime() {
		return get("createtime");
	}

}
