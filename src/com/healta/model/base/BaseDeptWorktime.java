package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseDeptWorktime<M extends BaseDeptWorktime<M>> extends Model<M> implements IBean {

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

	public void setStarttime(java.lang.String starttime) {
		set("starttime", starttime);
	}
	
	public java.lang.String getStarttime() {
		return getStr("starttime");
	}

	public void setEndtime(java.lang.String endtime) {
		set("endtime", endtime);
	}
	
	public java.lang.String getEndtime() {
		return getStr("endtime");
	}

}
