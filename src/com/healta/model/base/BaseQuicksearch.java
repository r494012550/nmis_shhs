package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseQuicksearch<M extends BaseQuicksearch<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setAdmissionkey(java.lang.Integer admissionkey) {
		set("admissionkey", admissionkey);
	}
	
	public java.lang.Integer getAdmissionkey() {
		return getInt("admissionkey");
	}

	public void setPatientkey(java.lang.Integer patientkey) {
		set("patientkey", patientkey);
	}
	
	public java.lang.Integer getPatientkey() {
		return getInt("patientkey");
	}

	public void setStudyorderkey(java.lang.Integer studyorderkey) {
		set("studyorderkey", studyorderkey);
	}
	
	public java.lang.Integer getStudyorderkey() {
		return getInt("studyorderkey");
	}

	public void setReportkey(java.lang.Integer reportkey) {
		set("reportkey", reportkey);
	}
	
	public java.lang.Integer getReportkey() {
		return getInt("reportkey");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}
	
	public java.lang.String getContent() {
		return getStr("content");
	}

	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}

	public java.util.Date getCreatetime() {
		return get("createtime");
	}
	public void setStudyid(java.lang.String studyid) {
		set("studyid", studyid);
	}
	
	public java.lang.String getStudyid() {
		return getStr("studyid");
	}
}
