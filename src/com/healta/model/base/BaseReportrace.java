package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseReportrace<M extends BaseReportrace<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setStudyid(java.lang.String studyid) {
		set("studyid", studyid);
	}
	
	public java.lang.String getStudyid() {
		return getStr("studyid");
	}

	public void setCheckdescHtml(java.lang.String checkdescHtml) {
		set("checkdesc_html", checkdescHtml);
	}
	
	public java.lang.String getCheckdescHtml() {
		return getStr("checkdesc_html");
	}

	public void setCheckresultHtml(java.lang.String checkresultHtml) {
		set("checkresult_html", checkresultHtml);
	}
	
	public java.lang.String getCheckresultHtml() {
		return getStr("checkresult_html");
	}

	public void setCheckdescTxt(java.lang.String checkdescTxt) {
		set("checkdesc_txt", checkdescTxt);
	}
	
	public java.lang.String getCheckdescTxt() {
		return getStr("checkdesc_txt");
	}

	public void setCheckresultTxt(java.lang.String checkresultTxt) {
		set("checkresult_txt", checkresultTxt);
	}
	
	public java.lang.String getCheckresultTxt() {
		return getStr("checkresult_txt");
	}

	public void setReportstatus(java.lang.String reportstatus) {
		set("reportstatus", reportstatus);
	}
	
	public java.lang.String getReportstatus() {
		return getStr("reportstatus");
	}

	public void setReporttime(java.util.Date reporttime) {
		set("reporttime", reporttime);
	}
	
	public java.util.Date getReporttime() {
		return get("reporttime");
	}

	public void setReportphysician(java.lang.String reportphysician) {
		set("reportphysician", reportphysician);
	}
	
	public java.lang.String getReportphysician() {
		return getStr("reportphysician");
	}

	public void setReportphysicianName(java.lang.String reportphysicianName) {
		set("reportphysician_name", reportphysicianName);
	}
	
	public java.lang.String getReportphysicianName() {
		return getStr("reportphysician_name");
	}

	public void setPreAudittime(java.util.Date preAudittime) {
		set("pre_audittime", preAudittime);
	}
	
	public java.util.Date getPreAudittime() {
		return get("pre_audittime");
	}

	public void setPreAuditphysician(java.lang.String preAuditphysician) {
		set("pre_auditphysician", preAuditphysician);
	}
	
	public java.lang.String getPreAuditphysician() {
		return getStr("pre_auditphysician");
	}

	public void setPreAuditphysicianName(java.lang.String preAuditphysicianName) {
		set("pre_auditphysician_name", preAuditphysicianName);
	}
	
	public java.lang.String getPreAuditphysicianName() {
		return getStr("pre_auditphysician_name");
	}

	public void setAudittime(java.util.Date audittime) {
		set("audittime", audittime);
	}
	
	public java.util.Date getAudittime() {
		return get("audittime");
	}

	public void setAuditphysician(java.lang.String auditphysician) {
		set("auditphysician", auditphysician);
	}
	
	public java.lang.String getAuditphysician() {
		return getStr("auditphysician");
	}

	public void setAuditphysicianName(java.lang.String auditphysicianName) {
		set("auditphysician_name", auditphysicianName);
	}
	
	public java.lang.String getAuditphysicianName() {
		return getStr("auditphysician_name");
	}

	public void setStudyitem(java.lang.String studyitem) {
		set("studyitem", studyitem);
	}
	
	public java.lang.String getStudyitem() {
		return getStr("studyitem");
	}

	public void setStudymethod(java.lang.String studymethod) {
		set("studymethod", studymethod);
	}
	
	public java.lang.String getStudymethod() {
		return getStr("studymethod");
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

	public void setTemplateId(java.lang.Integer templateId) {
		set("template_id", templateId);
	}
	
	public java.lang.Integer getTemplateId() {
		return getInt("template_id");
	}
	
	public void setImagequality(java.lang.String imagequality) {
		set("imagequality", imagequality);
	}

	public java.lang.String getImagequality() {
		return get("imagequality");
	}
	
	public void setReportquality(java.lang.String reportquality) {
		set("reportquality", reportquality);
	}

	public java.lang.String getReportquality() {
		return get("reportquality");
	}
	
	public void setDiagnosisCoincidence(java.lang.String diagnosis_coincidence) {
		set("diagnosis_coincidence", diagnosis_coincidence);
	}

	public java.lang.String getDiagnosisCoincidence() {
		return get("diagnosis_coincidence");
	}
	
	public void setIslocking(java.lang.Integer islocking) {
		set("islocking", islocking);
	}
	
	public java.lang.Integer getIslocking() {
		return getInt("islocking");
	}

	public void setLockingpeople(java.lang.String lockingpeople) {
		set("lockingpeople", lockingpeople);
	}
	
	public java.lang.String getLockingpeople() {
		return getStr("lockingpeople");
	}
	
	public void setRelativePath(java.lang.String relativePath) {
		set("relativePath", relativePath);
	}
	
	public java.lang.String getRelativePath() {
		return getStr("relativePath");
	}

}
