package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseDeptPostShift<M extends BaseDeptPostShift<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setDeptid(java.lang.Integer deptid) {
		set("deptid", deptid);
	}
	
	public java.lang.Integer getDeptid() {
		return getInt("deptid");
	}

	public void setShiftid(java.lang.Integer shiftid) {
		set("shiftid", shiftid);
	}
	
	public java.lang.Integer getShiftid() {
		return getInt("shiftid");
	}
	
	public void setPostcode(java.lang.String postcode) {
		set("postcode", postcode);
	}
	
	public java.lang.String getPostcode() {
		return getStr("postcode");
	}

}