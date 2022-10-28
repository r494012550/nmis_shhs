package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSscSaleOrderLines<M extends BaseSscSaleOrderLines<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setSscSaleOrderId(java.lang.Integer sscSaleOrderId) {
		set("ssc_sale_order_id", sscSaleOrderId);
	}
	
	public java.lang.Integer getSscSaleOrderId() {
		return getInt("ssc_sale_order_id");
	}

	public void setGoodsId(java.lang.Integer goodsId) {
		set("goods_id", goodsId);
	}
	
	public java.lang.Integer getGoodsId() {
		return getInt("goods_id");
	}

	public void setGoodsName(java.lang.String goodsName) {
		set("goods_name", goodsName);
	}
	
	public java.lang.String getGoodsName() {
		return getStr("goods_name");
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

	public void setSaleQty(java.lang.Integer saleQty) {
		set("sale_qty", saleQty);
	}
	
	public java.lang.Integer getSaleQty() {
		return getInt("sale_qty");
	}

}