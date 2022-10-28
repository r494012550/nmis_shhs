package com.healta.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;

import com.healta.constant.StudyOrderStatus;
import com.healta.model.Injection;
import com.healta.model.Inquiry;
import com.healta.model.Studyorder;
import com.healta.model.User;
import com.healta.service.FrontCommonService;
import com.healta.service.InjectionService;
import com.healta.util.ParaKit;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Record;

public class InjectionController extends Controller {
	private final static Logger log = Logger.getLogger(InjectionController.class);
	private static InjectionService sv = new InjectionService();
	private static FrontCommonService frontCommonsv = new FrontCommonService();
	public void index() {
		User user = (User) getSession().getAttribute("user");
		setAttr("username", user.getUsername());
		setAttr("name", user.getStr("name"));
		setAttr("user_id", user.getId());
		renderJsp("/view/front/injection/injection.jsp");
	}
	
	public void injectioncontent() {
		User user = (User) getSession().getAttribute("user");
		renderJsp("/view/front/injection/injection_content.jsp");
	}
	
	public void westSearch() {
		LocalDate today = LocalDate.now();
		setAttr("today", today.format(DateTimeFormatter.ISO_LOCAL_DATE));
		setAttr("consulted", StudyOrderStatus.CONSULTED);
		setAttr("injected", StudyOrderStatus.injected);
		setAttr("exist_sch", PropKit.use("system.properties").getBoolean("exist_appointment_process",true));
		renderJsp("/view/front/injection/west_search.jsp");
	}
	
	//查询检查
	public void findStudyorder() {
		try {
			renderJson(sv.findStudyorder(getParaMap(), getSessionAttr("syscode_lan")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//保存问诊信息
	public void saveInjection() {
		try {
			User user = (User) getSession().getAttribute("user");
			Studyorder studyorder = getModel(Studyorder.class, "", true);
			Injection injection = getModel(Injection.class, "", true);
			log.info(studyorder);
			log.info(injection);
			sv.saveInjection(new ParaKit(getParaMap()), studyorder, injection, user);
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//注射叫号
	public void injectioncalling() {
		try {
			User user = (User) getSession().getAttribute("user");
			if(sv.injectioncalling(getParaToInt("orderid"),user)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	//检查流程
	public void gotoStudyProcess(){
		Record record=frontCommonsv.findStudyInfo(getParaToInt("orderid"));
		setAttr("record", record);
		renderJsp("/view/front/studyprocess.jsp");
	}
}
