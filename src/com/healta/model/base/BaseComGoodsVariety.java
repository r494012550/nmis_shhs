package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseComGoodsVariety<M extends BaseComGoodsVariety<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setComGoodsVarietyCode(java.lang.String comGoodsVarietyCode) {
		set("com_goods_variety_code", comGoodsVarietyCode);
	}
	
	public java.lang.String getComGoodsVarietyCode() {
		return getStr("com_goods_variety_code");
	}

	public void setComGoodsVarietyName(java.lang.String comGoodsVarietyName) {
		set("com_goods_variety_name", comGoodsVarietyName);
	}
	
	public java.lang.String getComGoodsVarietyName() {
		return getStr("com_goods_variety_name");
	}

	public void setDeleted(java.lang.String deleted) {
		set("deleted", deleted);
	}
	
	public java.lang.String getDeleted() {
		return getStr("deleted");
	}

}
