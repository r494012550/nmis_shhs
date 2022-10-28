package com.healta.service;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.healta.model.Admission;
import com.healta.model.Patient;
import com.healta.model.Studyorder;
import com.healta.model.User;
import com.healta.util.AgeUtils;
import com.healta.util.AgeUtils.Age;
import com.healta.util.ParaKit;
import com.healta.util.PinyinUtils;
import com.jfinal.kit.StrKit;

public class ApiService {
	private static final Logger log = Logger.getLogger(ApiService.class);
	private static RegisterService regsv=new RegisterService();
	
	public synchronized boolean addRegister(ParaKit kit) {
		
		String patient_jsonstr=kit.getPara("patient");
		String admission_jsonstr=kit.getPara("admission");
		String studyorder_jsonstr=kit.getPara("studyorder"); 
		String itemstr = kit.getPara("itemsvalue");//检查项目
		String patientremark = kit.getPara("patientremark");
		String admissionremark = kit.getPara("admissionremark");
		String studyorderremark = kit.getPara("studyorderremark");
//		Integer unmatchkid = kit("unmatchkid"); //待匹配列表id
		
		JSONObject patient_json=JSON.parseObject(patient_jsonstr);
		JSONObject admission_json=JSON.parseObject(admission_jsonstr);
		JSONObject studyorder_json=JSON.parseObject(studyorder_jsonstr);
		
		Patient patient=new Patient();
		if(StrKit.isBlank(patient.getPy())) {
			patient.setPy(PinyinUtils.toPinyin(patient.getStr("patientname"),true));
			
		}
		Admission admission=new Admission();
		if(StrKit.notBlank(patient.getBirthdate())&&admission.getAge()==null) {
			Age age = AgeUtils.ins.birthdayToAge(patient.getBirthdate());
	    	if(age!=null) {
	    		admission.setAge(age.getAge());
	    		admission.setAgeunit(age.getAgeunit());
	    	}
		}
		Studyorder studyorder=new Studyorder();
		for(String key:patient_json.keySet()) {
			patient.set(key, patient_json.get(key));
		}
		
		for(String key:admission_json.keySet()) {
			admission.set(key, admission_json.get(key));
		}
		
		for(String key:studyorder_json.keySet()) {
			studyorder.set(key, studyorder_json.get(key));
		}
		
		User user=new User();
		user.setUsername("sa");
		user.setName("系统");
		return regsv.addRegister(patient, studyorder, admission, itemstr, patientremark, admissionremark, studyorderremark, user, null, null);
	}
}
