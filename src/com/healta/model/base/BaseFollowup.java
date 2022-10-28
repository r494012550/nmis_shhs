package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseFollowup<M extends BaseFollowup<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}
	
	public void setOrderid(java.lang.Integer orderid) {
		set("orderid", orderid);
	}
	
	public java.lang.Integer getOrderid() {
		return getInt("orderid");
	}
	
	public void setFollowupDatetime(java.util.Date followup_datetime) {
		set("followup_datetime", followup_datetime);
	}
	
	public java.lang.String getFollowupDatetime() {
		return getStr("followup_datetime");
	}
	
	public void setFollowupDoctorid(java.lang.Integer followupDoctorid) {
		set("followup_doctorid", followupDoctorid);
	}
	
	public java.lang.Integer getFollowupDoctorid() {
		return getInt("followup_doctorid");
	}
	
	public void setFollowupDoctorname(java.lang.String followupDoctorname) {
		set("followup_doctorname", followupDoctorname);
	}
	
	public java.lang.String getFollowupDoctorname() {
		return getStr("followup_doctorname");
	}
	
	public void setFollowupWay(java.lang.String followupWay) {
		set("followup_way", followupWay);
	}
	
	public java.lang.String getFollowupWay() {
		return getStr("followup_way");
	}
	
	public void setFollowupReason(java.lang.String followupReason) {
		set("followup_reason", followupReason);
	}
	
	public java.lang.String getFollowupReason() {
		return getStr("followup_reason");
	}
	
	public void setFollowupConsistent(java.lang.String followupConsistent) {
		set("followup_consistent", followupConsistent);
	}
	
	public java.lang.String getFollowupConsistent() {
		return getStr("followup_consistent");
	}
	
	public void setFollowupResult(java.lang.String followupResult) {
		set("followup_result", followupResult);
	}
	
	public java.lang.String getFollowupResult() {
		return getStr("followup_result");
	}
	
	public void setTreatment(java.lang.String treatment) {
		set("treatment", treatment);
	}
	
	public java.lang.String getTreatment() {
		return getStr("treatment");
	}
	
	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}
	
	public java.util.Date getCreatetime() {
		return get("createtime");
	}
	
	public void setModifytime(java.util.Date modifytime) {
		set("modifytime", modifytime);
	}
	
	public java.util.Date getModifytime() {
		return get("modifytime");
	}
	
}
