package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseEorder<M extends BaseEorder<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}
	
	public void setEorderid(java.lang.String eorderid) {
		set("eorderid", eorderid);
	}
	
	public java.lang.String getEorderid() {
		return getStr("eorderid");
	}
	
	public void setEorderidIssuer(java.lang.String eorderidIssuer) {
		set("eorderid_issuer", eorderidIssuer);
	}
	
	public java.lang.String getEorderidIssuer() {
		return getStr("eorderid_issuer");
	}
	
	public void setPatientid(java.lang.String patientid) {
		set("patientid", patientid);
	}
	
	public java.lang.String getPatientid() {
		return getStr("patientid");
	}
	
	public void setPatientname(java.lang.String patientname) {
		set("patientname", patientname);
	}
	
	public java.lang.String getPatientname() {
		return getStr("patientname");
	}
	
	public void setSex(java.lang.String sex) {
		set("sex", sex);
	}
	
	public java.lang.String getSex() {
		return getStr("sex");
	}
	
	public void setBirthdate(java.lang.String birthdate) {
		set("birthdate", birthdate);
	}
	
	public java.lang.String getBirthdate() {
		return getStr("birthdate");
	}
	
	public void setHeight(java.lang.String height) {
		set("height", height);
	}
	
	public java.lang.String getHeight() {
		return getStr("height");
	}
	
	public void setWeight(java.lang.String weight) {
		set("weight", weight);
	}
	
	public java.lang.String getWeight() {
		return getStr("weight");
	}
	
	public void setTelephone(java.lang.String telephone) {
		set("telephone", telephone);
	}
	
	public java.lang.String getTelephone() {
		return getStr("telephone");
	}
	
	public void setAddress(java.lang.String address) {
		set("address", address);
	}
	
	public java.lang.String getAddress() {
		return getStr("address");
	}
	
	public void setPatientsource(java.lang.String patientsource) {
		set("patientsource", patientsource);
	}
	
	public java.lang.String getPatientsource() {
		return getStr("patientsource");
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
	
	public void setIdnumber(java.lang.String idnumber) {
		set("idnumber", idnumber);
	}
	
	public java.lang.String getIdnumber() {
		return getStr("idnumber");
	}
	
	public void setCardno(java.lang.String cardno) {
		set("cardno", cardno);
	}
	
	public java.lang.String getCardno() {
		return getStr("cardno");
	}
	
	public void setWardno(java.lang.String wardno) {
		set("wardno", wardno);
	}
	
	public java.lang.String getWardno() {
		return getStr("wardno");
	}
	
	public void setBedno(java.lang.String bedno) {
		set("bedno", bedno);
	}
	
	public java.lang.String getBedno() {
		return getStr("bedno");
	}
	
	public void setStudyitems(java.lang.String studyitems) {
		set("studyitems", studyitems);
	}
	
	public java.lang.String getStudyitems() {
		return getStr("studyitems");
	}
	
	public void setAppdatetime(java.util.Date appdatetime) {
		set("appdatetime", appdatetime);
	}
	
	public java.util.Date getAppdatetime() {
		return get("appdatetime");
	}
	
	public void setAppdeptname(java.lang.String appdeptname) {
		set("appdeptname", appdeptname);
	}
	
	public java.lang.String getAppdeptname() {
		return getStr("appdeptname");
	}
	
	public void setAppdeptcode(java.lang.String appdeptcode) {
		set("appdeptcode", appdeptcode);
	}
	
	public java.lang.String getAppdeptcode() {
		return getStr("appdeptcode");
	}
	
	public void setAppdoctorname(java.lang.String appdoctorname) {
		set("appdoctorname", appdoctorname);
	}
	
	public java.lang.String getAppdoctorname() {
		return getStr("appdoctorname");
	}
	
	public void setAppdoctorcode(java.lang.String appdoctorcode) {
		set("appdoctorcode", appdoctorcode);
	}
	
	public java.lang.String getAppdoctorcode() {
		return getStr("appdoctorcode");
	}
	
	public void setAppointmenttime(java.util.Date appointmenttime) {
		set("appointmenttime", appointmenttime);
	}
	
	public java.util.Date getAppointmenttime() {
		return get("appointmenttime");
	}
	
	public void setPriority(java.lang.String priority) {
		set("priority", priority);
	}
	
	public java.lang.String getPriority() {
		return getStr("priority");
	}
	
	public void setModalityType(java.lang.String modalityType) {
		set("modality_type", modalityType);
	}
	
	public java.lang.String getModalityType() {
		return getStr("modality_type");
	}
	
	public void setRemark(java.lang.String remark) {
		set("remark", remark);
	}
	
	public java.lang.String getRemark() {
		return getStr("remark");
	}
	
	public void setStatus(java.lang.String status) {
		set("status", status);
	}
	
	public java.lang.String getStatus() {
		return getStr("status");
	}
	
	public void setAdmittingdiagnosis(java.lang.String admittingdiagnosis) {
		set("admittingdiagnosis", admittingdiagnosis);
	}
	
	public java.lang.String getAdmittingdiagnosis() {
		return getStr("admittingdiagnosis");
	}
	
	public void setReasonforexamination(java.lang.String reasonforexamination) {
		set("reasonforexamination", reasonforexamination);
	}
	
	public java.lang.String getReasonforexamination() {
		return getStr("reasonforexamination");
	}
	
	public void setAdmDate(java.util.Date admDate) {
		set("adm_date", admDate);
	}
	
	public java.util.Date getAdmDate() {
		return get("adm_date");
	}
	
	public void setDisDate(java.util.Date disDate) {
		set("dis_date", disDate);
	}
	
	public java.util.Date getDisDate() {
		return get("dis_date");
	}
	
	public void setChargeStatus(java.lang.String chargeStatus) {
		set("charge_status", chargeStatus);
	}
	
	public java.lang.String getChargeStatus() {
		return getStr("charge_status");
	}
	
	public void setPrice(java.math.BigDecimal price) {
		set("price", price);
	}
	
	public java.math.BigDecimal getPrice() {
		return get("price");
	}
	
	public void setRealprice(java.math.BigDecimal realprice) {
		set("realprice", realprice);
	}
	
	public java.math.BigDecimal getRealprice() {
		return get("realprice");
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
