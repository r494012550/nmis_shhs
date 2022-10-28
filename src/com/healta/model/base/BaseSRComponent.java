package com.healta.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public abstract class BaseSRComponent <M extends BaseSRComponent<M>> extends Model<M> implements IBean{

	public Integer getId() {
		return get("id");
	}
	public void setId(Integer id) {
		set("id",id);
	}
	public String getName() {
		return get("name");
	}
	public void setName(String name) {
		set("name",name);
	}
	public String getCode() {
		return get("code");
	}
	public void setCode(String code) {
		set("code",code);
	}
	public String getStandardcode() {
		return get("standardcode");
	}
	public void setStandardcode(String standardcode) {
		set("standardcode",standardcode);
	}
	public Integer getType() {
		return get("type");
	}
	public void setType(Integer type) {
		set("type",type);
	}
	public Integer getClassifyId() {
		return get("classifyId");
	}
	public void setClassifyId(Integer classifyId) {
		set("classifyId",classifyId);
	}
	public String getUnit() {
		return get("unit");
	}
	public void setUnit(String unit) {
		set("unit",unit);
	}
	public void setCreator(int creator) {
		set("creator",creator);
	}
	public int getCreator() {
		return get("creator");
	}
	public void setCreatorname(String creatorname) {
		set("creatorname",creatorname);
	}
	public String getCreatorname() {
		return get("creatorname");
	}
	public void setHtml(String html) {
		set("html",html);
	}
	public String getHtml() {
		return get("html");
	}
	public void setUid(String uid) {
		set("uid",uid);
	}
	public String getUid() {
		return get("uid");
	}
	public void setDefaultvalue(String defaultvalue) {
		set("defaultvalue",defaultvalue);
	}
	public String getDefaultvalue() {
		return get("defaultvalue");
	}
	public void setRequired(String required) {
		set("required",required);
	}
	public String getRequired() {
		return get("required");
	}
	public void setSummaryText(String summary_text) {
		set("summary_text",summary_text);
	}
	public String getSummaryText() {
		return get("summary_text");
	}
	
}
