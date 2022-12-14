package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSyngoviafindingfile<M extends BaseSyngoviafindingfile<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}
	
	public void setStudyinsuid(java.lang.String studyinsuid) {
		set("studyinsuid", studyinsuid);
	}
	
	public java.lang.String getStudyinsuid() {
		return getStr("studyinsuid");
	}

	public void setStudyid(java.lang.String studyid) {
		set("studyid", studyid);
	}
	
	public java.lang.String getStudyid() {
		return getStr("studyid");
	}

	public void setFindingfile(java.lang.String findingfile) {
		set("findingfile", findingfile);
	}
	
	public java.lang.String getFindingfile() {
		return getStr("findingfile");
	}

	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}
	
	public java.util.Date getCreatetime() {
		return get("createtime");
	}
	
	public void setFindingindex(java.lang.Integer findingindex) {
		set("findingindex", findingindex);
	}
	
	public java.lang.Integer getFindingindex() {
		return getInt("findingindex");
	}
	
	public void setFindingname(java.lang.String findingname) {
		set("findingname", findingname);
	}
	
	public java.lang.String getFindingname() {
		return getStr("findingname");
	}
	public void setSource(java.lang.Integer source) {
		set("source", source);
	}
	
	public java.lang.Integer getSource() {
		return getInt("source");
	}
	
	public void setPatientid(java.lang.String patientid) {
		set("patientid", patientid);
	}
	
	public java.lang.String getPatientid() {
		return getStr("patientid");
	}
	
	public void setStudydatetime(java.util.Date studydatetime) {
		set("studydatetime", studydatetime);
	}
	
	public java.util.Date getStudydatetime() {
		return get("studydatetime");
	}
}
