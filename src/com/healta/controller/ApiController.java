package com.healta.controller;

import org.apache.log4j.Logger;

import com.healta.service.ApiService;
import com.healta.util.ParaKit;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;

public class ApiController extends Controller {
	private static final Logger log = Logger.getLogger(ApiController.class);
	public static ApiService sv=new ApiService();
	
	public void register() {
		try {
			sv.addRegister(new ParaKit(getParaMap()));
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
}
