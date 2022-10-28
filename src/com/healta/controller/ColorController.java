package com.healta.controller;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.healta.model.StatusColor;
import com.healta.service.ColorService;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;

public class ColorController extends Controller{
	private static final Logger log = Logger.getLogger(ColorController.class);
	private final static ColorService sv=new ColorService();
	
	public void getColor() {
		try{
			renderJson(sv.getColor(getPara("type")));
		
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void findColor() {
		try{
			renderJson(sv.findColor(getSessionAttr("language")));
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void saveColor(){
		try{
			StatusColor sc=getModel(StatusColor.class,"",true);
			if(sv.saveColor(sc)){
				renderJson(ResultUtil.success(sc));
			}
			else{
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
}
