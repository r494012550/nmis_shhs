package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSyngoviareport<M extends BaseSyngoviareport<M>> extends Model<M> implements IBean {

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
	
	public void setStudyinsuid(java.lang.String studyinsuid) {
		set("studyinsuid", studyinsuid);
	}
	
	public java.lang.String getStudyinsuid() {
		return getStr("studyinsuid");
	}

	public void setCdafile(java.lang.String cdafile) {
		set("cdafile", cdafile);
	}
	
	public java.lang.String getCdafile() {
		return getStr("cdafile");
	}

	public void setPdffile(java.lang.String pdffile) {
		set("pdffile", pdffile);
	}
	
	public java.lang.String getPdffile() {
		return getStr("pdffile");
	}

	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}
	
	public java.util.Date getCreatetime() {
		return get("createtime");
	}
	
	public void setDelflag(java.lang.Integer delflag) {
		set("delflag", delflag);
	}
	
	public java.lang.Integer getDelflag() {
		return getInt("delflag");
	}
	
	public void setReportname(java.lang.String reportname) {
		set("reportname", reportname);
	}
	
	public java.lang.String getReportname() {
		return getStr("reportname");
	}

}
