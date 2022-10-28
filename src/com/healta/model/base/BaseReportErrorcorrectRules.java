package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseReportErrorcorrectRules<M extends BaseReportErrorcorrectRules<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setKeyword(java.lang.String keyword) {
		set("keyword", keyword);
	}
	
	public java.lang.String getKeyword() {
		return getStr("keyword");
	}

	public void setRules(java.lang.String rules) {
		set("rules", rules);
	}
	
	public java.lang.String getRules() {
		return getStr("rules");
	}

	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}
	
	public java.util.Date getCreatetime() {
		return get("createtime");
	}

}