package com.healta.model.base;

import java.util.Date;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public abstract class BaseSRTemplate<M extends BaseSRTemplate<M>> extends Model<M> implements IBean {

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
	public String getTemplatecontent() {
		return get("templatecontent");
	}
	public void setTemplatecontent(String templatecontent) {
		set("templatecontent",templatecontent);
	}
	public String getMaprule() {
		return get("maprule");
	}
	public void setMaprule(String maprule) {
		set("maprule",maprule);
	}
	public void setCreator(int creator) {
		set("creator",creator);
	}
	public int getCreator() {
		return get("creator");
	}
	public void setCreatorName(String creatorName) {
		set("creatorname",creatorName);
	}
	public String getCreatorName() {
		return get("creatorname");
	}
	public void setCreatetime(Date createtime) {
		set("createtime",createtime);
	}
	public Date getCreatetime() {
		return get("createtime");
	}
	public void setEnablefilter(String enablefilter) {
		set("enablefilter",enablefilter);
	}
	public String getEnablefilter() {
		return get("enablefilter");
	}
	public void setFilterWidth(String filter_width) {
		set("filter_width",filter_width);
	}
	public String getFilterWidth() {
		return get("filter_width");
	}
	public void setFooterLeft(String footer_left) {
		set("footer_left",footer_left);
	}
	public String getFooterLeft() {
		return get("footer_left");
	}
	public void setFooterMiddle(String footer_middle) {
		set("footer_middle",footer_middle);
	}
	public String getFooterMiddle() {
		return get("footer_middle");
	}
	public void setFooterRight(String footer_right) {
		set("footer_right",footer_right);
	}
	public String getFooterRight() {
		return get("footer_right");
	}
	public void setFooterImg(String footer_img) {
		set("footer_img",footer_img);
	}
	public String getFooterImg() {
		return get("footer_img");
	}
}
