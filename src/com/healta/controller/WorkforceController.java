package com.healta.controller;

import com.healta.model.User;
import com.healta.plugin.workforce.WorkforceServer;
import com.healta.plugin.workforce.Physician;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;

import com.healta.model.DeptPostShift;
import com.healta.model.DeptShift;
import com.healta.model.DeptShiftWork;
import com.healta.model.DeptWorktime;
import com.healta.service.WorkforceService;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

public class WorkforceController extends Controller {
	private final static Logger log = Logger.getLogger(WorkforceController.class);
	private final static WorkforceService sv=new WorkforceService();
	public void index(){
		User user = (User) getSession().getAttribute("user");
		setAttr("username", user.getUsername());
		setAttr("name", user.getStr("name"));
		setAttr("user_id", user.getId());
//		setAttr("sr_support",VerifyLicense.hasFunction("srtemplate"));
		renderJsp("/view/workforce/dept_worktime.jsp");
	}
	
	public void shift(){
		LocalDate now=LocalDate.now();
		int dayofweek=now.getDayOfWeek().getValue();
		LocalDate monday=now.minusDays(dayofweek-1);
		set("lastmoday", monday.minusDays(7).format(DateTimeFormatter.ISO_DATE));
		set("thismoday", monday.format(DateTimeFormatter.ISO_DATE));
		set("nextmoday", monday.plusDays(7).format(DateTimeFormatter.ISO_DATE));
		renderJsp("/view/admin/workforce/dept_shift.jsp");
	}
	
	public void reportTask(){
		setAttr("setting", sv.getReportTaskSetting());
		renderJsp("/view/admin/workforce/report_task_setting.jsp");
	}
	
	public void getDeptShift() {
		try{
			renderJson(sv.getDeptShifts());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void getDeptShiftWorktime() {
		try{
			renderJson(sv.getDeptShiftWorktime(getParaToInt("shiftid")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void saveDeptShift(){
		try{
			
			String worktimeids=getPara("worktimeids");
			worktimeids=worktimeids.endsWith(",")?worktimeids.substring(0, worktimeids.length()-1):worktimeids;
			String result=sv.saveDeptShift(getModel(DeptShift.class, "", true),worktimeids);
			if(StrKit.equals(result, "1")){
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(result));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	
	public void worktime(){
		renderJsp("/view/admin/workforce/worktime.jsp");
	}
	
	public void deptWorktime(){
		renderJsp("/view/admin/workforce/dept_post_shift.jsp");
	}
	
	public void getWorktimes() {
		try{
			renderJson(sv.getWorktimes());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void saveWorktime(){
		try{
			if(sv.saveWorktime(getModel(DeptWorktime.class, "", true))){
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void deleteWorktime(){
		try{
			if(sv.deleteWorktime(getParaToInt("id"))){
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void deleteDeptShift(){
		try{
			if(sv.deleteDeptShift(getParaToInt("id"))){
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void getDeptPostShift() {
		try{
			renderJson(sv.getDeptPostShift(StrKit.notBlank(getPara("deptid"))?getParaToInt("deptid"):null,getPara("postcode")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void showShiftDetails(){
		if(StrKit.notBlank(get("deptid"))&&StrKit.notBlank(getPara("postcode"))){
			Integer deptid=getParaToInt("deptid");
			String postcode=get("postcode");
			LocalDate monday=LocalDate.parse(get("monday"), DateTimeFormatter.ISO_DATE);
			Integer offset=getParaToInt("offset");
			monday=monday.plusDays(offset*7);

			String list[]=new String[7];
			for(int i=0;i<7;i++){
				list[i]=monday.plusDays(i).format(DateTimeFormatter.ISO_DATE);
			}
			setAttr("weeklist", list);
			setAttr("shifts", sv.getDeptShiftAndWorktimes(deptid,postcode));
			setAttr("employeelist", sv.getEmployeesByDept(deptid,postcode));
			setAttr("postname", getPara("postname"));
			setAttr("today", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
			
			//"ss".compareTo(anotherString)
			switch (get("postcode")) {
			case "T":
				set("post_shift_count", sv.getPostShiftCount(deptid, postcode));
				set("modalitys", sv.getModalityByDept(deptid));
				log.info(getAttr("modalitys"));
				setAttr("map", sv.getShiftWorkDetails(deptid, postcode, monday));
				render("/view/admin/workforce/shift_work_technician.html");
				break;
			case "D":
				setAttr("map", sv.getShiftWorkDetails(deptid, postcode, monday));
				render("/view/admin/workforce/shift_work_physician.html");
				break;
			case "N":
				setAttr("map", sv.getShiftWorkDetails(deptid, postcode, monday));
				render("/view/admin/workforce/shift_work_physician.html");
				break;
			case "REG":
				setAttr("map", sv.getShiftWorkDetails(deptid, postcode, monday));
				render("/view/admin/workforce/shift_work_physician.html");
				break;
			default:
				setAttr("map", sv.getShiftWorkDetails(deptid, postcode, monday));
				render("/view/admin/workforce/shift_work_physician.html");
			}
			
		} else {
			renderHtml("<div style='width:95%;height:250px;padding:20px 20px;float:left;line-height: 250px;text-align: center;'>"
					+ "<span style='font-weight: bold;'>请选择机构、科室、岗位，进行排班设置。</span>"
					+ "</div>");
		}
	}
	
	public void copyLastWeekShifts(){
		try{
			Map<String, String> map=new HashMap<String, String>();
			if(sv.copyLastWeekShifts(getParaToInt("deptid"), get("postcode"), get("monday"),getParaToInt("offset"),map)&&StrKit.isBlank(map.get("reslut"))){
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(StrKit.equals(map.get("reslut"),"0")?"上周没有排版数据！":""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
		
	}
	
	public void saveDeptPostShift(){
		try{
			if(sv.saveDeptPostShift(getParaToInt("deptid"),getPara("postcode"),getPara("shiftids"))){
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void saveDeptShiftWork(){
		try{
			List<Integer> ids=sv.saveDeptShiftWork(getModel(DeptShiftWork.class, "", true),getPara("userids"),getPara("employeeids"));
			if(ids.size()>0){
				renderJson(ResultUtil.success(ids));
			} else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void removeEmployeeFromShift(){
		try{
			if(sv.removeEmployeeFromShift(getPara("ids"))){
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void goEmployeeShift(){
		renderJsp("/view/admin/workforce/employeeShift.jsp");
	}
	
	public void saveReportTaskSetting() {
		try{
			Integer id=sv.saveReportTaskSetting(getParaToInt("id"),get("attr"),get("value"));
			if(id!=null){
				renderJson(ResultUtil.success(id));
			} else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void showPhysicianScore() {
		Integer deptid=getParaToInt("deptid");
		if(deptid!=null){
			List<Physician> physicianlist =WorkforceServer.INSTANCE.getPhysicianList().stream().filter(x->x.getDept_id().intValue()==deptid.intValue()).collect(Collectors.toList());
			List<Physician> preauditphysicianlist =WorkforceServer.INSTANCE.getPreAuditPhysicianList().stream().filter(x->x.getDept_id().intValue()==deptid.intValue()).collect(Collectors.toList());
			List<Physician> auditphysicianlist =WorkforceServer.INSTANCE.getAuditPhysicianList().stream().filter(x->x.getDept_id().intValue()==deptid.intValue()).collect(Collectors.toList());
			Map<String,List<Record>> map=sv.showPhysicianScore(deptid, physicianlist, preauditphysicianlist, auditphysicianlist);
			if(map!=null) {
				setAttr("physicianlist", physicianlist);
				setAttr("preauditphysicianlist", preauditphysicianlist);
				setAttr("auditphysicianlist", auditphysicianlist);
				setAttr("shiftmap", map);
			}
			render("/view/admin/workforce/physician_score.html");
		} else {
			renderHtml("<div style='width:95%;height:250px;padding:20px 20px;float:left;line-height: 250px;text-align: center;'>"
					+ "<span style='font-weight: bold;'>请选择机构、科室。</span>"
					+ "</div>");
		}
	}
	
	public void showPhysicianCount() {
		Integer deptid=getParaToInt("deptid");
		boolean bymodality=getParaToBoolean("bymodality");
		if(deptid!=null){
			List<Physician> physicianlist =WorkforceServer.INSTANCE.getPhysicianList().stream().filter(x->x.getDept_id().intValue()==deptid.intValue()).collect(Collectors.toList());
			List<Physician> preauditphysicianlist =WorkforceServer.INSTANCE.getPreAuditPhysicianList().stream().filter(x->x.getDept_id().intValue()==deptid.intValue()).collect(Collectors.toList());
			List<Physician> auditphysicianlist =WorkforceServer.INSTANCE.getAuditPhysicianList().stream().filter(x->x.getDept_id().intValue()==deptid.intValue()).collect(Collectors.toList());
			
			setAttr("physicianlist", physicianlist);
			setAttr("preauditphysicianlist", preauditphysicianlist);
			setAttr("auditphysicianlist", auditphysicianlist);
			
			if(bymodality) {
				Map<String,Integer> map=sv.showPhysicianCountByModality(physicianlist, preauditphysicianlist, auditphysicianlist);
				setAttr("shiftmap", map);
				render("/view/admin/workforce/physician_count_modality.html");
			} else {
				Map<String,List<Record>> map=sv.showPhysicianCount(deptid, physicianlist, preauditphysicianlist, auditphysicianlist);
				setAttr("shiftmap", map);
				render("/view/admin/workforce/physician_count.html");
			}
		} else {
			renderHtml("<div style='width:95%;height:250px;padding:20px 20px;float:left;line-height: 250px;text-align: center;'>"
					+ "<span style='font-weight: bold;'>请选择机构、科室。</span>"
					+ "</div>");
		}
	}
	
	public void shangesTakeEffectImmediately() {
		try{
			WorkforceServer.INSTANCE.init(true);
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void getPhysicians(){
		try{
			renderJson(sv.getPhysicians(getParaToInt("deptid"), get("name"),getSessionAttr("syscode_lan")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void getModalitys(){
		try{
			renderJson(sv.getModalites());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void getExamItems(){
		try{
			renderJson(sv.getExamItems());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void getSubscriptionRules(){
		try{
			renderJson(sv.getSubscriptionRules(getParaToInt("userid")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void saveSubscriptionRule(){
		try{
			if(sv.saveSubscriptionRule(
					getParaToInt("userid"), 
					getParaToInt("employeeid"), 
					get("modality"), 
					get("modalityids"), 
					get("itemids"))){
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void getPhysicianDetails(){
		renderJson(WorkforceServer.INSTANCE.getPhysicianList().stream().filter(x->x.getPhysician_id().intValue()==getParaToInt("id")
				&&x.getDept_id().intValue()==getParaToInt("deptid")
				&&x.getShiftid().intValue()==getParaToInt("shiftid")
				&&x.getWorktimeid().intValue()==getParaToInt("worktimeid")).collect(Collectors.toList()));
	}
}
