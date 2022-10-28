package com.healta.controller;

import java.util.List;

import org.apache.log4j.Logger;

import com.healta.service.CallingService;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;

public class CallingController extends Controller {
	private final static Logger log = Logger.getLogger(DicController.class);
	CallingService sv = new CallingService();
	
	public void index() {
		renderJsp("/view/front/examine/calling.jsp");
	}
	
	
	public void getModalityName() {
		try{
			renderJson(sv.getModalityName());
		}
		catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
		
	}
	
	public void getcalling() {
		set("patientname", getPara("callingpatientname"));
		set("modalityname", getPara("modalityname"));
		set("examinecallingorcomplete",getPara("type"));
		renderJsp("/view/front/examine/calling.jsp");
	}

}
