package com.healta.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseDicExamitem<M extends BaseDicExamitem<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}
	
	public void setCoefficient(java.lang.Integer coefficient) {
		set("coefficient", coefficient);
	}
	
	public java.lang.Integer getCoefficient() {
		return getInt("coefficient");
	}

	public void setItemCode(java.lang.String itemCode) {
		set("item_code", itemCode);
	}
	
	public java.lang.String getItemCode() {
		return getStr("item_code");
	}

	public void setItemName(java.lang.String itemName) {
		set("item_name", itemName);
	}
	
	public java.lang.String getItemName() {
		return getStr("item_name");
	}

	public void setPy(java.lang.String py) {
		set("py", py);
	}
	
	public java.lang.String getPy() {
		return getStr("py");
	}

	public void setType(java.lang.String type) {
		set("type", type);
	}
	
	public java.lang.String getType() {
		return getStr("type");
	}

	public void setOrganfk(java.lang.Integer organfk) {
		set("organfk", organfk);
	}
	
	public java.lang.Integer getOrganfk() {
		return getInt("organfk");
	}

	public void setSuborganfk(java.lang.Integer suborganfk) {
		set("suborganfk", suborganfk);
	}
	
	public java.lang.Integer getSuborganfk() {
		return getInt("suborganfk");
	}

	public void setPrice(java.math.BigDecimal price) {
		set("price", price);
	}
	
	public java.math.BigDecimal getPrice() {
		return get("price");
	}

	public void setReportAlertHour(java.lang.Integer reportAlertHour) {
		set("report_alert_hour", reportAlertHour);
	}
	
	public java.lang.Integer getReportAlertHour() {
		return getInt("report_alert_hour");
	}

	public void setReportAlertMinute(java.lang.Integer reportAlertMinute) {
		set("report_alert_minute", reportAlertMinute);
	}
	
	public java.lang.Integer getReportAlertMinute() {
		return getInt("report_alert_minute");
	}

	public void setDuration(java.lang.Integer duration) {
		set("duration", duration);
	}
	
	public java.lang.Integer getDuration() {
		return getInt("duration");
	}

	public void setFulldescription(java.lang.String fulldescription) {
		set("fulldescription", fulldescription);
	}
	
	public java.lang.String getFulldescription() {
		return getStr("fulldescription");
	}
	
	public void setDeleted(java.lang.String deleted) {
		set("deleted", deleted);
	}

	public java.lang.String getDeleted() {
		return get("deleted");
	}

}