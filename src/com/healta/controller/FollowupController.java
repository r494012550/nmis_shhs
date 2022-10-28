package com.healta.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import com.healta.model.Followup;
import com.healta.model.User;
import com.healta.service.FollowupService;
import com.healta.service.ReportService;
import com.healta.util.ParaKit;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

public class FollowupController extends Controller {
	public static FollowupService sv = new FollowupService();
	public static ReportService rsv = new ReportService();
	
	public void index(){
		User user = (User) getSession().getAttribute("user");
		setAttr("username", user.getUsername());
		setAttr("name", user.getName());
		setAttr("user_id", user.getId());
		renderJsp("/view/front/followup/followup.jsp");
	}
	
	public void followupContent() {
		renderJsp("/view/front/followup/followup_content.jsp");
	}
	
	public void westSearch() {
		LocalDate now=LocalDate.now();
		User user = (User) getSession().getAttribute("user");
		setAttr("today", now.minusDays(7).format(DateTimeFormatter.ISO_DATE));
		setAttr("lastday", now.format(DateTimeFormatter.ISO_DATE));
		if("D".equals(sv.findUserProfessionById(user).get("profession"))){
			setAttr("user_id", user.getId());
		}
		
		renderJsp("/view/front/followup/westSearch.jsp");
	}
	
	public void getAllLabel(){
		User user = (User) getSession().getAttribute("user");
		List<Record> list=rsv.getReportLabel(null,"1", user.getId());
//		list.addAll(rsv.getReportLabel(null,"0", user.getId()));
		renderJson(list);
	}
	
	public void searchFollowup() {
		try {
			List<Record> list = sv.searchStudyAndReport(new ParaKit(getParaMap()), getSessionAttr("syscode_lan"));
			renderJson(list);
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void loadFollowupData() {
		try {
			renderJson(sv.findFollowup(getInt("orderid")));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void saveFollowup() {
		try {
			User user = (User) getSession().getAttribute("user");
			Followup followup = getModel(Followup.class, "", true);
			if(sv.saveFollowup(followup, user)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(-1,"保存失败请重试，如果问题依然存在请联系系统管理员！"));
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void deleteFollowup() {
		try {
			if(sv.deleteFollowup(getParaToInt("followupid"))) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(-1,"保存失败请重试，如果问题依然存在请联系系统管理员！"));
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	//保存查询结果列布局
	public void saveDatagridColumn() {
		try {
			User user = (User) getSession().getAttribute("user");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("targetid", getPara("targetid"));
			map.put("frozenOpts", getPara("frozenOpts"));
			map.put("opts", getPara("opts"));
			map.put("moudle", getPara("moudle"));
			if(sv.saveDatagridColumn(map, user)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(-1,"error"));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	public void findUserByRole() {
		renderJson(sv.findUserByRole());
	}
}
