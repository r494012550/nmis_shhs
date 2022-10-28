package com.healta.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.healta.model.DicDepartment;
import com.healta.model.DicInstitution;
import com.healta.model.DicModality;
import com.healta.model.UserDac;
import com.jfinal.kit.StrKit;

/*
 * 用户数据访问控制工具类
 * 
 * 可控制用户访问机构，科室，检查设备,三者之间没有内在联系，单独进行数据访问控制。
 * 
 * */
public class DACKit {
	
	public static List<DicModality> modalityDAC(UserDac dac,List<DicModality> mods){
		if(mods==null||mods.size()==0||dac==null||StrKit.isBlank(dac.getModIds())) {
			return mods;
		}
		List<String> ids= Arrays.asList(dac.getModIds().split(","));
		return mods.stream().filter(i ->ids.contains(i.getId().toString())).collect(Collectors.toList());
	}
	
	
	public static List<DicInstitution> institutionDAC(UserDac dac,List<DicInstitution> insts){
		if(insts==null||insts.size()==0||dac==null||StrKit.isBlank(dac.getInstitutionIds())) {
			return insts;
		}
		List<String> ids= Arrays.asList(dac.getInstitutionIds().split(","));
		return insts.stream().filter(i ->ids.contains(i.getId().toString())).collect(Collectors.toList());
	}
	
	public static List<DicDepartment> deptDAC(UserDac dac,List<DicDepartment> depts){
		if(depts==null||depts.size()==0||dac==null||StrKit.isBlank(dac.getDeptIds())) {
			return depts;
		}
		List<String> ids= Arrays.asList(dac.getDeptIds().split(","));
		return depts.stream().filter(i ->ids.contains(i.getId().toString())).collect(Collectors.toList());
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
