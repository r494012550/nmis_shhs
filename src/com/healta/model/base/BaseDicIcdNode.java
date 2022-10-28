package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseDicIcdNode<M extends BaseDicIcdNode<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setIcdIndex(java.lang.Integer icdIndex) {
		set("icd_index", icdIndex);
	}
	
	public java.lang.Integer getIcdIndex() {
		return getInt("icd_index");
	}

	public void setParentid(java.lang.Integer parentid) {
		set("parentid", parentid);
	}
	
	public java.lang.Integer getParentid() {
		return getInt("parentid");
	}

	public void setIcdName(java.lang.String icdName) {
		set("icd_name", icdName);
	}
	
	public java.lang.String getIcdName() {
		return getStr("icd_name");
	}

	public void setIcdMnemonicCode(java.lang.String icdMnemonicCode) {
		set("icd_mnemonic_code", icdMnemonicCode);
	}
	
	public java.lang.String getIcdMnemonicCode() {
		return getStr("icd_mnemonic_code");
	}

	public void setIcdText(java.lang.String icdText) {
		set("icd_text", icdText);
	}
	
	public java.lang.String getIcdText() {
		return getStr("icd_text");
	}

}