package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseComGoods<M extends BaseComGoods<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setGoodsName(java.lang.String goodsName) {
		set("goods_name", goodsName);
	}
	
	public java.lang.String getGoodsName() {
		return getStr("goods_name");
	}

	public void setGeneralName(java.lang.String generalName) {
		set("general_name", generalName);
	}
	
	public java.lang.String getGeneralName() {
		return getStr("general_name");
	}

	public void setComGoodsVarietyId(java.lang.Integer comGoodsVarietyId) {
		set("com_goods_variety_id", comGoodsVarietyId);
	}
	
	public java.lang.Integer getComGoodsVarietyId() {
		return getInt("com_goods_variety_id");
	}

	public void setProductLocation(java.lang.String productLocation) {
		set("product_location", productLocation);
	}
	
	public java.lang.String getProductLocation() {
		return getStr("product_location");
	}

	public void setStatus(java.lang.String status) {
		set("status", status);
	}
	
	public java.lang.String getStatus() {
		return getStr("status");
	}

	public void setComLot(java.lang.String comLot) {
		set("com_lot", comLot);
	}
	
	public java.lang.String getComLot() {
		return getStr("com_lot");
	}

	public void setEfficiencyDate(java.lang.String efficiencyDate) {
		set("efficiency_date", efficiencyDate);
	}
	
	public java.lang.String getEfficiencyDate() {
		return getStr("efficiency_date");
	}

	public void setExpireDate(java.lang.String expireDate) {
		set("expire_date", expireDate);
	}
	
	public java.lang.String getExpireDate() {
		return getStr("expire_date");
	}

	public void setCalibrationDose(java.lang.String calibrationDose) {
		set("calibration_dose", calibrationDose);
	}
	
	public java.lang.String getCalibrationDose() {
		return getStr("calibration_dose");
	}

	public void setCapacity(java.lang.String capacity) {
		set("capacity", capacity);
	}
	
	public java.lang.String getCapacity() {
		return getStr("capacity");
	}

	public void setInvOwner(java.lang.Integer invOwner) {
		set("inv_owner", invOwner);
	}
	
	public java.lang.Integer getInvOwner() {
		return getInt("inv_owner");
	}

	public void setNote(java.lang.String note) {
		set("note", note);
	}
	
	public java.lang.String getNote() {
		return getStr("note");
	}

	public void setCreatedBy(java.lang.Integer createdBy) {
		set("created_by", createdBy);
	}
	
	public java.lang.Integer getCreatedBy() {
		return getInt("created_by");
	}

	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}
	
	public java.util.Date getCreatetime() {
		return get("createtime");
	}

	public void setLastUpdatedBy(java.lang.Integer lastUpdatedBy) {
		set("last_updated_by", lastUpdatedBy);
	}
	
	public java.lang.Integer getLastUpdatedBy() {
		return getInt("last_updated_by");
	}

	public void setModifytime(java.util.Date modifytime) {
		set("modifytime", modifytime);
	}
	
	public java.util.Date getModifytime() {
		return get("modifytime");
	}
	public void setDeleted(java.lang.String deleted) {
		set("deleted", deleted);
	}
	
	public java.lang.String getDeleted() {
		return getStr("deleted");
	}
}
