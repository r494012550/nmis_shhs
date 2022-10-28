package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BasePasswordPolicy<M extends BasePasswordPolicy<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}
	
	public void setEnableComplexPwd(java.lang.String enableComplexPwd) {
		set("enable_complex_pwd", enableComplexPwd);
	}
	
	public java.lang.String getEnableComplexPwd() {
		return getStr("enable_complex_pwd");
	}
	
	public void setPasswordLength(java.lang.String passwordLength) {
		set("password_length", passwordLength);
	}
	
	public java.lang.String getPasswordLength() {
		return getStr("password_length");
	}
	
	public void setContainCasePwd(java.lang.String containCasePwd) {
		set("contain_case_pwd", containCasePwd);
	}
	
	public java.lang.String getContainCasePwd() {
		return getStr("contain_case_pwd");
	}
	
	public void setContainDigitPwd(java.lang.String containDigitPwd) {
		set("contain_digit_pwd", containDigitPwd);
	}
	
	public java.lang.String getContainDigitPwd() {
		return getStr("contain_digit_pwd");
	}
	
	public void setContainLetterPwd(java.lang.String containLetterPwd) {
		set("contain_letter_pwd", containLetterPwd);
	}
	
	public java.lang.String getContainLetterPwd() {
		return getStr("contain_letter_pwd");
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
