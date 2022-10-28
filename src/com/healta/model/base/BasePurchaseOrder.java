package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BasePurchaseOrder<M extends BasePurchaseOrder<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setPurchaseNo(java.lang.String purchaseNo) {
		set("purchase_no", purchaseNo);
	}
	
	public java.lang.String getPurchaseNo() {
		return getStr("purchase_no");
	}

	public void setVender(java.lang.String vender) {
		set("vender", vender);
	}
	
	public java.lang.String getVender() {
		return getStr("vender");
	}

	public void setVenderAddress(java.lang.String venderAddress) {
		set("vender_address", venderAddress);
	}
	
	public java.lang.String getVenderAddress() {
		return getStr("vender_address");
	}

	public void setReceiviGoodsAddress(java.lang.String receiviGoodsAddress) {
		set("receivi_goods_address", receiviGoodsAddress);
	}
	
	public java.lang.String getReceiviGoodsAddress() {
		return getStr("receivi_goods_address");
	}

	public void setOrderStartDate(java.lang.String orderStartDate) {
		set("order_start_date", orderStartDate);
	}
	
	public java.lang.String getOrderStartDate() {
		return getStr("order_start_date");
	}

	public void setOrderEndDate(java.lang.String orderEndDate) {
		set("order_end_date", orderEndDate);
	}
	
	public java.lang.String getOrderEndDate() {
		return getStr("order_end_date");
	}

	public void setCreator(java.lang.String creator) {
		set("creator", creator);
	}
	
	public java.lang.String getCreator() {
		return getStr("creator");
	}

	public void setContacts(java.lang.String contacts) {
		set("contacts", contacts);
	}
	
	public java.lang.String getContacts() {
		return getStr("contacts");
	}

	public void setContactsNumber(java.lang.Integer contactsNumber) {
		set("contacts_number", contactsNumber);
	}
	
	public java.lang.Integer getContactsNumber() {
		return getInt("contacts_number");
	}

	public void setNote(java.lang.String note) {
		set("note", note);
	}
	
	public java.lang.String getNote() {
		return getStr("note");
	}

	public void setCteatetime(java.util.Date cteatetime) {
		set("cteatetime", cteatetime);
	}
	
	public java.util.Date getCteatetime() {
		return get("cteatetime");
	}

	public void setCreatedBy(java.lang.Integer createdBy) {
		set("created_by", createdBy);
	}
	
	public java.lang.Integer getCreatedBy() {
		return getInt("created_by");
	}

	public void setModifytime(java.util.Date modifytime) {
		set("modifytime", modifytime);
	}
	
	public java.util.Date getModifytime() {
		return get("modifytime");
	}

	public void setModifyBy(java.lang.Integer modifyBy) {
		set("modify_by", modifyBy);
	}
	
	public java.lang.Integer getModifyBy() {
		return getInt("modify_by");
	}

	public void setDeleted(java.lang.String deleted) {
		set("deleted", deleted);
	}
	
	public java.lang.String getDeleted() {
		return getStr("deleted");
	}

	public void setStatus(java.lang.String status) {
		set("status", status);
	}
	
	public java.lang.String getStatus() {
		return getStr("status");
	}

}
