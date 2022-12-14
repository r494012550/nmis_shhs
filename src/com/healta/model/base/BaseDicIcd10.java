package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseDicIcd10<M extends BaseDicIcd10<M>> extends Model<M> implements IBean {

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

	public void setIcdCode(java.lang.String icdCode) {
		set("icd_code", icdCode);
	}
	
	public java.lang.String getIcdCode() {
		return getStr("icd_code");
	}

	public void setIcdSubcode(java.lang.String icdSubcode) {
		set("icd_subcode", icdSubcode);
	}
	
	public java.lang.String getIcdSubcode() {
		return getStr("icd_subcode");
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

	public void setIcdDesc(java.lang.String icdDesc) {
		set("icd_desc", icdDesc);
	}
	
	public java.lang.String getIcdDesc() {
		return getStr("icd_desc");
	}

	public void setIcdXbxz(java.lang.String icdXbxz) {
		set("icd_xbxz", icdXbxz);
	}
	
	public java.lang.String getIcdXbxz() {
		return getStr("icd_xbxz");
	}

	public void setIcdLxxz(java.lang.String icdLxxz) {
		set("icd_lxxz", icdLxxz);
	}
	
	public java.lang.String getIcdLxxz() {
		return getStr("icd_lxxz");
	}

}
