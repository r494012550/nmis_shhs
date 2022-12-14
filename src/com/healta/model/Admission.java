package com.healta.model;

import com.healta.model.base.BaseAdmission;
import com.jfinal.kit.StrKit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Admission extends BaseAdmission<Admission> {
	public static final Admission dao = new Admission().dao();
	
	public Admission getAdmissionByAccessionNo(String accession_no){
		return dao.findFirst("select top 1 * from admission where accessionnumber=?",accession_no);
	}
	
	public Admission getAdmissionByAdmissionid(String admissionid){
		return dao.findFirst("select top 1 * from admission where admissionid=?",admissionid);
	}
	
	/*
	 * 返回model的所有属性值字符串。
	 * 请自定义方法不用以get开头
	 * 
	 * */
	public String toStr(){
		String ret="";
		for (Entry<String, Object> entry : this._getAttrsEntrySet()) {
            ret=ret.concat(entry.getKey()).concat(":").concat(entry.getValue()!=null?entry.getValue().toString():"").concat("\r\n");
        }
		return ret;
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
