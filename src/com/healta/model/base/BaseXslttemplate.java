package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseXslttemplate<M extends BaseXslttemplate<M>> extends Model<M> implements IBean {

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

	public void setXslt(java.lang.String xslt) {
		set("xslt", xslt);
	}
	
	public java.lang.String getXslt() {
		return getStr("xslt");
	}

	public void setCreator(java.lang.Integer creator) {
		set("creator", creator);
	}
	
	public java.lang.Integer getCreator() {
		return getInt("creator");
	}

	public void setCreatorName(java.lang.String creatorName) {
		set("creator_name", creatorName);
	}
	
	public java.lang.String getCreatorName() {
		return getStr("creator_name");
	}

	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}
	
	public java.util.Date getCreatetime() {
		return get("createtime");
	}
	
	public void setXsltSr(java.lang.String xsltsr) {
		set("xslt_sr", xsltsr);
	}
	
	public java.lang.String getXsltSr() {
		return getStr("xslt_sr");
	}
	
	public void setDisplayvalue(java.lang.String displayvalue) {
		set("displayvalue", displayvalue);
	}
	
	public java.lang.String getDisplayvalue() {
		return getStr("displayvalue");
	}
	
	public void setSource(java.lang.String source) {
		set("source", source);
	}
	
	public java.lang.String getSource() {
		return getStr("source");
	}
	
	public void setDisplayname(java.lang.String displayname) {
		set("displayname", displayname);
	}
	
	public java.lang.String getDisplayname() {
		return getStr("displayname");
	}
	
	public void setBelongreport(java.lang.String belongreport) {
		set("belongreport", belongreport);
	}
	
	public java.lang.String getBelongreport() {
		return getStr("belongreport");
	}
	
	public void setViaversion(java.lang.String viaversion) {
		set("viaversion", viaversion);
	}
	
	public java.lang.String getViaversion() {
		return getStr("viaversion");
	}

}
