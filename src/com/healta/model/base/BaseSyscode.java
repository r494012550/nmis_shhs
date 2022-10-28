package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSyscode<M extends BaseSyscode<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setCode(java.lang.String code) {
		set("code", code);
	}

	public java.lang.String getCode() {
		return get("code");
	}

	public void setNameZh(java.lang.String nameZh) {
		set("name_zh", nameZh);
	}

	public java.lang.String getNameZh() {
		return get("name_zh");
	}

	public void setNameEn(java.lang.String nameEn) {
		set("name_en", nameEn);
	}

	public java.lang.String getNameEn() {
		return get("name_en");
	}

	public void setType(java.lang.String type) {
		set("type", type);
	}

	public java.lang.String getType() {
		return get("type");
	}

	public void setParent(java.lang.Integer parent) {
		set("parent", parent);
	}

	public java.lang.Integer getParent() {
		return get("parent");
	}

}
