package com.healta.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.healta.model.User;
import com.healta.model.UserProfiles;
import com.healta.service.FrontCommonService;
import com.healta.util.IPKit;
import com.healta.util.ResultUtil;
import com.healta.util.TextCompareKit;
import com.healta.util.WebSocketUtils;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

public class FrontCommonController extends Controller {
	private final static Logger log = Logger.getLogger(FrontCommonController.class);
	private static FrontCommonService sv = new FrontCommonService();
	
	
	/**
	 * 申请单
	 */
	public void getImage(){
		try {
			renderJson(ResultUtil.success(sv.getApplyImage(getParaToInt("orderid"))));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
		
		
//		String studyid = getPara("studyid");
//		Record request = workListService.getImage(studyid);
//		if(request == null) {
//			renderJson(ResultUtil.fail(1, "该检查未查到任何申请单信息"));
//		}else {
//			renderJson(ResultUtil.success(request));
//		}
	}
	
	public void gotoStudyProcess(){
		
		Record record=sv.findStudyInfo(getParaToInt("orderid"));
		
		setAttr("record", record);
		renderJsp("/view/front/studyprocess.jsp");
	}
	
	public void getStudyProcess(){
		try {
			renderJson(sv.findStudyProcess(getParaToInt("orderid"),getSessionAttr("language")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	
	public void toAppInfo(){
		log.info(getRequest().getHeader("User-Agent"));
		
		render("/view/front/appInfo.html");
	}
	
	public void deleteAppScanImg(){
		try {
			renderJson(sv.deleteAppScanImg(getParaToInt("orderid"),getParaToInt("img")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void compareStudyProcess() {
		String[] compare = TextCompareKit.compare(getPara("before"), getPara("after"));
		String[] result = new String[2];
		result[0] = compare[0].replaceAll("\r\n", "<br/>");
		result[1] = compare[1].replaceAll("\r\n", "<br/>");
		renderJson(ResultUtil.success(result));
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
		
		//保存datagrid页面尺寸
		public void saveDatagridPagination() {
			try {
				User user = (User) getSession().getAttribute("user");
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("targetid", getPara("targetid"));
				map.put("moudle", getPara("moudle"));
				map.put("thisPageSize", getPara("thisPageSize"));
				if(sv.saveDatagridPagination(map, user)) {
					renderJson(ResultUtil.success());
				}else {
					renderJson(ResultUtil.fail(-1,"error"));
				}
			}catch(Exception e) {
				e.printStackTrace();
				renderJson(ResultUtil.fail(-1, e.getMessage()));
			}
		}
	//启用列移动，加载用户列配置
	public void getDatagridColumn() {
		try {
			User user = (User) getSession().getAttribute("user");
			List<String> list = new ArrayList<String>();
			String field = "datagrid_" + getPara("moudle") + "_" + getPara("targetid");
			UserProfiles profiles = UserProfiles.dao.findFirst("SELECT * FROM userprofiles WHERE userid = ?", user.getId());
			if(profiles != null && StringUtils.isNotBlank(profiles.getStr(field))) {
				Map<String, Object> mapType = JSON.parseObject(profiles.getStr(field));
				String frozenOpts = "";
				String opts = "";
				for(String obj : mapType.keySet()) {
					if(StringUtils.equals("frozenOpts", obj)) {
						frozenOpts = (String) mapType.get(obj);
					}
					if(StringUtils.equals("opts", obj)) {
						opts = (String) mapType.get(obj);
					}
				}
				list.add(frozenOpts);
				list.add(opts);
			}

			renderJson(ResultUtil.success(list));
			
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 *  初始化默认的排序列表
	 */
	public void restoreDefaults() {
		try {
			User user = (User) getSession().getAttribute("user");
			if (sv.restoreDefaults(get("moudle"), user.getId())) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail("初始化失败！"));
			}
		} catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
}
