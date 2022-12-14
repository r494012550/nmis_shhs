package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseStatisticalreport<M extends BaseStatisticalreport<M>> extends Model<M> implements IBean {

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

	public void setClassifyid(java.lang.Integer classifyid) {
		set("classifyid", classifyid);
	}
	
	public java.lang.Integer getClassifyid() {
		return getInt("classifyid");
	}

	public void setClassifyname(java.lang.String classifyname) {
		set("classifyname", classifyname);
	}
	
	public java.lang.String getClassifyname() {
		return getStr("classifyname");
	}

	public void setJrxmlFilename(java.lang.String jrxmlFilename) {
		set("jrxml_filename", jrxmlFilename);
	}
	
	public java.lang.String getJrxmlFilename() {
		return getStr("jrxml_filename");
	}

	public void setJasperFilename(java.lang.String jasperFilename) {
		set("jasper_filename", jasperFilename);
	}
	
	public java.lang.String getJasperFilename() {
		return getStr("jasper_filename");
	}

	public void setSql(java.lang.String sql) {
		set("sql", sql);
	}
	
	public java.lang.String getSql() {
		return getStr("sql");
	}
	
	public void setConditions(java.lang.String conditions) {
		set("conditions", conditions);
	}
	
	public java.lang.String getConditions() {
		return getStr("conditions");
	}

	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}
	
	public java.util.Date getCreatetime() {
		return get("createtime");
	}

}
