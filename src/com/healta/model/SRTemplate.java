package com.healta.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.healta.model.base.BaseSRTemplate;
import com.jfinal.kit.StrKit;

@SuppressWarnings("serial")
public class SRTemplate extends BaseSRTemplate<SRTemplate> {
	public static final SRTemplate dao = new SRTemplate().dao();
	
	
	public SRTemplate queryByName(String name){
		return dao.findFirst("select top 1 * from srtemplate where name=?",name);
	}
	
	public SRTemplate queryByNameAndNotEquSelf(String name,int id){
		return dao.findFirst("select top 1 * from srtemplate where name=? and id!=?",name,id);
	}
	
	/*
	 * 返回model的select字符串。
	 * exceptAttrName: 排除一个属性,多个属性中间以','相隔
	 * */
	public String toSelectStr(String excludAttrName){
		String ret="";
		List<String> excludlist=new ArrayList<String>();
		if(StrKit.notBlank(excludAttrName)) {
			excludlist=Arrays.asList(excludAttrName.split(","));
		}
		String tableName=this._getTable().getName();
		for(String attrname:this._getTable().getColumnNameSet()) {
			if(excludlist.contains(attrname))continue;
			ret+=tableName+"."+attrname+",";
		}
		return ret.substring(0, ret.length()-1);
	}
}
