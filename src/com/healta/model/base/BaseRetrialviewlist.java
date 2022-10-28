package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseRetrialviewlist<M extends BaseRetrialviewlist<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setReportid(java.lang.Integer reportid) {
		set("reportid", reportid);
	}
	
	public java.lang.Integer getReportid() {
		return getInt("reportid");
	}

	public void setRetrialviewcontent(java.lang.String retrialviewcontent) {
		set("retrialviewcontent", retrialviewcontent);
	}
	
	public java.lang.String getRetrialviewcontent() {
		return getStr("retrialviewcontent");
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