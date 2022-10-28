package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseReport<M extends BaseReport<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}
	
	public void setStudyorderfk(java.lang.Integer studyorderfk) {
		set("studyorderfk", studyorderfk);
	}
	
	public java.lang.Integer getStudyorderfk() {
		return getInt("studyorderfk");
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
	
	public void setDescFontsize(java.lang.Integer descFontsize) {
		set("desc_fontsize", descFontsize);
	}
	
	public java.lang.Integer getDescFontsize() {
		return getInt("desc_fontsize");
	}
	
	public void setResultFontsize(java.lang.Integer resultFontsize) {
		set("result_fontsize", resultFontsize);
	}
	
	public java.lang.Integer getResultFontsize() {
		return getInt("result_fontsize");
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
	
	public void setReportphysician(java.lang.Integer reportphysician) {
		set("reportphysician", reportphysician);
	}
	
	public java.lang.Integer getReportphysician() {
		return getInt("reportphysician");
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
	
	public void setPreAuditphysician(java.lang.Integer preAuditphysician) {
		set("pre_auditphysician", preAuditphysician);
	}
	
	public java.lang.Integer getPreAuditphysician() {
		return getInt("pre_auditphysician");
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
	
	public void setAuditphysician(java.lang.Integer auditphysician) {
		set("auditphysician", auditphysician);
	}
	
	public java.lang.Integer getAuditphysician() {
		return getInt("auditphysician");
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
	
	public void setViareportid(java.lang.Integer viareportid) {
		set("viareportid", viareportid);
	}
	
	public java.lang.Integer getViareportid() {
		return getInt("viareportid");
	}
	
	public void setUrgent(java.lang.String urgent) {
		set("urgent", urgent);
	}
	
	public java.lang.String getUrgent() {
		return getStr("urgent");
	}
	
	public void setUrgentexplain(java.lang.String urgentexplain) {
		set("urgentexplain", urgentexplain);
	}
	
	public java.lang.String getUrgentexplain() {
		return getStr("urgentexplain");
	}
	
	public void setPosOrNeg(java.lang.String posOrNeg) {
		set("pos_or_neg", posOrNeg);
	}
	
	public java.lang.String getPosOrNeg() {
		return getStr("pos_or_neg");
	}
	
	public void setPrintcount(java.lang.Integer printcount) {
		set("printcount", printcount);
	}
	
	public java.lang.Integer getPrintcount() {
		return getInt("printcount");
	}
	
	public void setImagequality(java.lang.String imagequality) {
		set("imagequality", imagequality);
	}
	
	public java.lang.String getImagequality() {
		return getStr("imagequality");
	}
	
	public void setReportquality(java.lang.String reportquality) {
		set("reportquality", reportquality);
	}
	
	public java.lang.String getReportquality() {
		return getStr("reportquality");
	}
	
	public void setDiagnosisCoincidence(java.lang.String diagnosisCoincidence) {
		set("diagnosis_coincidence", diagnosisCoincidence);
	}
	
	public java.lang.String getDiagnosisCoincidence() {
		return getStr("diagnosis_coincidence");
	}
	
	public void setIslocking(java.lang.Integer islocking) {
		set("islocking", islocking);
	}
	
	public java.lang.Integer getIslocking() {
		return getInt("islocking");
	}
	
	public void setLockingpeople(java.lang.Integer lockingpeople) {
		set("lockingpeople", lockingpeople);
	}
	
	public java.lang.Integer getLockingpeople() {
		return getInt("lockingpeople");
	}
	
	public void setRelativePath(java.lang.String relativePath) {
		set("relativePath", relativePath);
	}
	
	public java.lang.String getRelativePath() {
		return getStr("relativePath");
	}
	
	public void setIstakenaway(java.lang.Integer istakenaway) {
		set("istakenaway", istakenaway);
	}
	
	public java.lang.Integer getIstakenaway() {
		return getInt("istakenaway");
	}
		
	public void setPrintReportCount(java.lang.Integer printReportCount) {
		set("print_report_count", printReportCount);
	}
	
	public java.lang.Integer getPrintReportCount() {
		return getInt("print_report_count");
	}
	
	public void setPrintReportTime(java.util.Date printReportTime) {
		set("print_report_time", printReportTime);
	}
	
	public java.util.Date getPrintReportTime() {
		return get("print_report_time");
	}
	
	public void setPrintFilmCount(java.lang.Integer printFilmCount) {
		set("print_film_count", printFilmCount);
	}
	
	public java.lang.Integer getPrintFilmCount() {
		return getInt("print_film_count");
	}
	
	public void setPrintFilmTime(java.util.Date printFilmTime) {
		set("print_film_time", printFilmTime);
	}
	
	public java.util.Date getPrintFilmTime() {
		return get("print_film_time");
	}
	
	public void setPrintImagesHtml(java.lang.String printImagesHtml) {
		set("print_images_html", printImagesHtml);
	}
	
	public java.lang.String getPrintImagesHtml() {
		return getStr("print_images_html");
	}
	
	public void setCreatephysician(java.lang.Integer createphysician) {
		set("createphysician", createphysician);
	}
	
	public java.lang.Integer getCreatephysician() {
		return getInt("createphysician");
	}
	
	public void setCreatephysicianName(java.lang.String createphysicianName) {
		set("createphysician_name", createphysicianName);
	}
	
	public java.lang.String getCreatephysicianName() {
		return getStr("createphysician_name");
	}
	
	public void setCreatephysiciantime(java.util.Date createphysiciantime) {
		set("createphysiciantime", createphysiciantime);
	}
	
	public java.util.Date getCreatephysiciantime() {
		return get("createphysiciantime");
	}
	
	public void setReportType(java.lang.String report_type) {
		set("report_type", report_type);
	}
	
	public java.lang.String getReportType() {
		return getStr("report_type");
	}

	
}