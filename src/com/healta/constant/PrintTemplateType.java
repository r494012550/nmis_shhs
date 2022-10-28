package com.healta.constant;

import com.jfinal.kit.StrKit;

public enum PrintTemplateType {

	REPORT_TEMPLATE("1","报告模板"),
	REG_TEMPLATE("2","检查单模板"),
	SCH_TEMPLATE("3","预约单模板"),
	BAGSTICKER_TEMPLATE("4","袋贴模板"),
	INFORMED_CONSENT_TEMPLATE("5","知情同意书模板"),
	MEDICALHISTORY_TEMPLATE("6","病史模板"),
	ALLIMAGE_TEMPLATE("7","全部图片模板"),
	ONEIMAGE_TEMPLATE("8","单幅图像模板");
	
	private String name;
	private String displayname;
	
	private PrintTemplateType(String name,String displayname){
		this.name=name;
		this.displayname=displayname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	
	public static String getDisplayName(String name){
		String displayname="";
		for(PrintTemplateType type:PrintTemplateType.values()){
			if(StrKit.equals(name, type.getName())){
				displayname=type.getDisplayname();
				break;
			}
		}
		return displayname;
	}
	
	public static PrintTemplateType valOf(String name){
		for(PrintTemplateType type:PrintTemplateType.values()){
			if(StrKit.equals(name, type.getName())){
				return type;
			}
		}
		return REPORT_TEMPLATE;
	}
	
}
