package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseFilter<M extends BaseFilter<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}
	
	public void setReportstatus(java.lang.String reportstatus) {
		set("reportstatus", reportstatus);
	}
	
	public java.lang.String getReportstatus() {
		return getStr("reportstatus");
	}
	
	public void setDatasource(java.lang.String datasource) {
		set("datasource", datasource);
	}
	
	public java.lang.String getDatasource() {
		return getStr("datasource");
	}
	
	public void setWorkforce(java.lang.String workforce) {
		set("workforce", workforce);
	}
	
	public java.lang.String getWorkforce() {
		return getStr("workforce");
	}
	
	public void setDatetype(java.lang.String datetype) {
		set("datetype", datetype);
	}
	
	public java.lang.String getDatetype() {
		return getStr("datetype");
	}
	
	public void setDatefrom(java.lang.String datefrom) {
		set("datefrom", datefrom);
	}
	
	public java.lang.String getDatefrom() {
		return getStr("datefrom");
	}
	
	public void setDateto(java.lang.String dateto) {
		set("dateto", dateto);
	}
	
	public java.lang.String getDateto() {
		return getStr("dateto");
	}
	
	public void setStudyid(java.lang.String studyid) {
		set("studyid", studyid);
	}
	
	public java.lang.String getStudyid() {
		return getStr("studyid");
	}
	
	public void setPatientname(java.lang.String patientname) {
		set("patientname", patientname);
	}
	
	public java.lang.String getPatientname() {
		return getStr("patientname");
	}
	
	public void setPatientsource(java.lang.String patientsource) {
		set("patientsource", patientsource);
	}
	
	public java.lang.String getPatientsource() {
		return getStr("patientsource");
	}
	
	public void setPatientid(java.lang.String patientid) {
		set("patientid", patientid);
	}
	
	public java.lang.String getPatientid() {
		return getStr("patientid");
	}
	
	public void setStartfromtime(java.lang.String startfromtime) {
		set("startfromtime", startfromtime);
	}
	
	public java.lang.String getStartfromtime() {
		return getStr("startfromtime");
	}
	
	public void setEndtotime(java.lang.String endtotime) {
		set("endtotime", endtotime);
	}
	
	public java.lang.String getEndtotime() {
		return getStr("endtotime");
	}
	
	public void setAppdate(java.lang.String appdate) {
		set("appdate", appdate);
	}
	
	public java.lang.String getAppdate() {
		return getStr("appdate");
	}
	
	public void setCreator(java.lang.String creator) {
		set("creator", creator);
	}
	
	public java.lang.String getCreator() {
		return getStr("creator");
	}
	
	public void setDescription(java.lang.String description) {
		set("description", description);
	}
	
	public java.lang.String getDescription() {
		return getStr("description");
	}
	
	public void setFilterType(java.lang.String filterType) {
		set("filter_type", filterType);
	}
	
	public java.lang.String getFilterType() {
		return getStr("filter_type");
	}
	
	public void setReportphysicianName(java.lang.String reportphysicianName) {
		set("reportphysician_name", reportphysicianName);
	}
	
	public java.lang.String getReportphysicianName() {
		return getStr("reportphysician_name");
	}
	
	public void setAuditphysicianName(java.lang.String auditphysicianName) {
		set("auditphysician_name", auditphysicianName);
	}
	
	public java.lang.String getAuditphysicianName() {
		return getStr("auditphysician_name");
	}
	
	public void setModality(java.lang.String modality) {
		set("modality", modality);
	}
	
	public java.lang.String getModality() {
		return getStr("modality");
	}
	
	public void setInstitutionid(java.lang.String institutionid) {
		set("institutionid", institutionid);
	}
	
	public java.lang.String getInstitutionid() {
		return getStr("institutionid");
	}
	
	public void setOutno(java.lang.String outno) {
		set("outno", outno);
	}
	
	public java.lang.String getOutno() {
		return getStr("outno");
	}
	
	public void setInno(java.lang.String inno) {
		set("inno", inno);
	}
	
	public java.lang.String getInno() {
		return getStr("inno");
	}
	
	public void setCardno(java.lang.String cardno) {
		set("cardno", cardno);
	}
	
	public java.lang.String getCardno() {
		return getStr("cardno");
	}
	
	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}
	
	public java.util.Date getCreatetime() {
		return get("createtime");
	}
	
}
