package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseDeptShiftWork<M extends BaseDeptShiftWork<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setUserid(java.lang.Integer userid) {
		set("userid", userid);
	}
	
	public java.lang.Integer getUserid() {
		return getInt("userid");
	}

	public void setEmployeeid(java.lang.Integer employeeid) {
		set("employeeid", employeeid);
	}
	
	public java.lang.Integer getEmployeeid() {
		return getInt("employeeid");
	}

	public void setPostcode(java.lang.String postcode) {
		set("postcode", postcode);
	}
	
	public java.lang.String getPostcode() {
		return getStr("postcode");
	}

	public void setDeptid(java.lang.Integer deptid) {
		set("deptid", deptid);
	}
	
	public java.lang.Integer getDeptid() {
		return getInt("deptid");
	}

	public void setInstitutionid(java.lang.Integer institutionid) {
		set("institutionid", institutionid);
	}
	
	public java.lang.Integer getInstitutionid() {
		return getInt("institutionid");
	}

	public void setShiftid(java.lang.Integer shiftid) {
		set("shiftid", shiftid);
	}
	
	public java.lang.Integer getShiftid() {
		return getInt("shiftid");
	}

	public void setShiftName(java.lang.String shiftName) {
		set("shift_name", shiftName);
	}
	
	public java.lang.String getShiftName() {
		return getStr("shift_name");
	}

	public void setWorktimeid(java.lang.Integer worktimeid) {
		set("worktimeid", worktimeid);
	}
	
	public java.lang.Integer getWorktimeid() {
		return getInt("worktimeid");
	}

	public void setWorktimeName(java.lang.String worktimeName) {
		set("worktime_name", worktimeName);
	}
	
	public java.lang.String getWorktimeName() {
		return getStr("worktime_name");
	}
	
	public void setModalityid(java.lang.Integer modalityid) {
		set("modalityid", modalityid);
	}
	
	public java.lang.Integer getModalityid() {
		return getInt("modalityid");
	}

	public void setWorkdate(java.util.Date workdate) {
		set("workdate", workdate);
	}
	
	public java.util.Date getWorkdate() {
		return get("workdate");
	}

	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}
	
	public java.util.Date getCreatetime() {
		return get("createtime");
	}

}
