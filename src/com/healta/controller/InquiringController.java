package com.healta.controller;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.healta.constant.StudyOrderStatus;
import com.healta.model.Inquiry;
import com.healta.model.Patient;
import com.healta.model.PreviousHistory;
import com.healta.model.Studyorder;
import com.healta.model.User;
import com.healta.service.FrontCommonService;
import com.healta.service.InquiringService;
import com.healta.util.IPKit;
import com.healta.util.ParaKit;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Record;

public class InquiringController extends Controller {
	
	private final static Logger log = Logger.getLogger(InquiringController.class);
	private static InquiringService sv = new InquiringService();
	private static FrontCommonService frontCommonsv = new FrontCommonService();
	public void index(){
		User user = (User) getSession().getAttribute("user");
		setAttr("username", user.getUsername());
		setAttr("name", user.getStr("name"));
		setAttr("user_id", user.getId());
		setAttr("projecturl", getRequest().getScheme()+"://"+IPKit.getServerIP(getRequest())+":"+getRequest().getServerPort()+getRequest().getContextPath());
		try {
			setAttr("reservationname1",Base64.encodeBase64String("检查报告单".getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		setAttr("ip", IPKit.getIP(getRequest()));
		setAttr("employee_id", user.getEmployeefk());
		renderJsp("/view/front/inquiring/inquiring.jsp");
	}
	
	//问诊内容界面
	public void interrogationContent() {
		renderJsp("/view/front/inquiring/inquiring_content.jsp");
	}
	
	//查询界面
	public void westSearch() {
		LocalDate today = LocalDate.now();
		setAttr("today", today.format(DateTimeFormatter.ISO_LOCAL_DATE));
		setAttr("arrived", StudyOrderStatus.ARRIVED);
		setAttr("consulted", StudyOrderStatus.CONSULTED);
		setAttr("exist_sch", PropKit.use("system.properties").getBoolean("exist_appointment_process",true));
		renderJsp("/view/front/inquiring/west_search.jsp");
	}
	
	public void reporthistory() {
		renderJsp("/view/front/inquiring/report_history.jsp");
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
	
	public void getPreviousHistory() {
		try {
			renderJson(sv.getPreviousHistory(getInt("orderid")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//保存问诊信息
	public void saveInterrogation() {
		try {
			User user = (User) getSession().getAttribute("user");
			Patient patient = getModel(Patient.class, "", true);
			Studyorder studyorder = getModel(Studyorder.class, "", true);
			Inquiry interrogation = getModel(Inquiry.class, "", true); 
			//问诊医生默认为当前登录用户 -2020-08-28  hx
			interrogation.setInterrogationDoctorId(user.getId());
			
			interrogation.setInterrogationTime(new Date());
			PreviousHistory previousHistory = getModel(PreviousHistory.class, "", true);
			String[] scanimgs = getParaValues("app_scan_img");
			sv.saveInterrogation(new ParaKit(getParaMap()),patient, studyorder, interrogation, previousHistory, user, scanimgs);
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//根据patientid获取历史报告
	public void getReportHistory() {
		try {
			renderJson(sv.getReportHistory(getPara("patientid"), getParaToInt("orderid"), getSessionAttr("syscode_lan")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//问诊叫号
	public void interrogationcalling() {
		try {
			User user = (User) getSession().getAttribute("user");
			if(sv.interrogationcalling(getParaToInt("orderid"),user)) {
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
