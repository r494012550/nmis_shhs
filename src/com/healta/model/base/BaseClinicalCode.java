package com.healta.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public abstract class BaseClinicalCode<M extends BaseClinicalCode<M>> extends Model<M> implements IBean {

	public Integer getId() {
		return get("id");
	}
	public void setId(Integer id) {
		set("id",id);
	}
	public String getMeaning() {
		return get("meaning");
	}
	public void setMeaning(String meaning) {
		set("meaning",meaning);
	}
	public String getCode() {
		return get("code");
	}
	public void setCode(String code) {
		set("code",code);
	}
	public String getScheme() {
		return get("scheme");
	}
	public void setScheme(String scheme) {
		set("scheme",scheme);
	}
	
	
}
