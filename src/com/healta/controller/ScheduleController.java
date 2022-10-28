package com.healta.controller;

import java.io.UnsupportedEncodingException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.license.VerifyLicense;
import com.healta.model.Admission;
import com.healta.model.CheckWorkAttendance;
import com.healta.model.DicEmployee;
import com.healta.model.DicModality;
import com.healta.model.Filter;
import com.healta.model.Patient;
import com.healta.model.Report;
import com.healta.model.Studyitem;
import com.healta.model.Studyorder;
import com.healta.model.User;
import com.healta.model.UserProfiles;
import com.healta.plugin.sequence.SequenceService;
import com.healta.service.FrontCommonService;
import com.healta.service.ScheduleService;
import com.healta.util.IPKit;
import com.healta.util.ParaKit;
import com.healta.util.ResultUtil;
import com.healta.util.ScheduleTempUtil_del;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;


public class ScheduleController extends Controller {
	
	private static final Logger log = Logger.getLogger(ScheduleController.class);
	private static final ScheduleService sv=new ScheduleService();
	private static FrontCommonService frontCommonsv = new FrontCommonService();
//	private static FilterService filterService = new FilterService();
	
	public void index(){
		User user = (User) getSession().getAttribute("user");
		setAttr("username", user.getUsername());
//		setAttr("rolename", "管理员");
		setAttr("name", user.getName());
		setAttr("user_id", user.getId());
//		UserProfiles up=(UserProfiles)getSession().getAttribute("myprofiles");
		setAttr("projecturlschedule", getRequest().getScheme()+"://"+IPKit.getServerIP(getRequest())+":"+getRequest().getServerPort()+getRequest().getContextPath());
		try {
			setAttr("reservationname1",Base64.encodeBase64String("预约单".getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UserProfiles profiles=(UserProfiles)getSession().getAttribute("myprofiles");
		if(profiles!=null){
			setAttr("up", profiles);
			setAttr("beforeSaveScanSch", StringUtils.equals("1", profiles.getBeforeSaveScan()));
			setAttr("afterSavePrintSch", StringUtils.equals("1", profiles.getAfterSavePrint()));
			setAttr("copiesch", profiles.getStudyformPrintCopies()==null?1:profiles.getStudyformPrintCopies());
		}
		renderJsp("/view/front/schedule/schedule.jsp");
	}
	
	public void goScheduleDetailView(){
		renderJsp("/view/front/schedule/scheduleDetails.jsp");
	}
	//打开保存过滤器对话框
	public void openFilterSaveDialog() {
		renderJsp("/view/front/schedule/filter.jsp");
	}
	//打开过滤器管理框
	public void openFilterManageDialog() {
		renderJsp("/view/front/schedule/filterManage.jsp");
	}
	
	/**
	 *  跳预约录入页面
	 */
	public void goSchedulecenter() {
		User user = (User) getSession().getAttribute("user");
		DicEmployee employee = DicEmployee.dao.findById(user.getEmployeefk());
		setAttr("user_institution", employee.getInstitutionid());
		setAttr("username", user.getUsername());
		setAttr("user_id", user.getId());
		setAttr("ip", IPKit.getIP(getRequest()));
		setAttr("enable_scan_module", StrKit.equals("1", PropKit.use("system.properties").get("enable_scan_module")));
		setAttr("auto_launch_scaner", StrKit.equals("1", PropKit.use("system.properties").get("auto_launch_scaner")));
		setAttr("scanurl", getRequest().getScheme()+"://"+IPKit.getIP(getRequest())+":"+getRequest().getServerPort()+getRequest().getContextPath());
		renderJsp("/view/front/schedule/schedule_center.jsp");
	}
	
	/**
	 * 跳预约查询页面
	 */
	public void goSearchSchedule() {
	    setAttr("quicksearch",VerifyLicense.hasFunction("quicksearch"));
		renderJsp("/view/front/schedule/schedule_search.jsp");
	}
	
	
	public void goToApplicationView() {
		LocalDate today=LocalDate.now();
		setAttr("today", today.format(DateTimeFormatter.ISO_LOCAL_DATE));
		setAttr("yesterday", today.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
		renderJsp("/view/front/schedule/application_view.jsp");
	}
	
	/**
	  *  查看预约时间页面
	 */
	public void showScheduleInfo() {
	    DicModality dm = DicModality.dao.findById(getParaToInt("modalityid"));
        LocalDate date = LocalDate.parse(getPara("date"));
        String worktime = "";
        if (DayOfWeek.SATURDAY.equals(date.getDayOfWeek())) {
            worktime = dm.getSaturdayOfWorktime();
            if (StrKit.isBlank(worktime)) {
                worktime = PropKit.use("system.properties").get("saturday_of_worktime",worktime);
            }
        } else if (DayOfWeek.SUNDAY.equals(date.getDayOfWeek())) {
            worktime = dm.getSundayOfWorktime();
            if (StrKit.isBlank(worktime)) {
                worktime = PropKit.use("system.properties").get("sunday_of_worktime",worktime);
            }
        } else {
            worktime=dm.getWorkdayOfWorktime();
            if (StrKit.isBlank(worktime)) {
                worktime=PropKit.use("system.properties").get("workday_of_worktime");
            }
        }
	    List<Integer> times = SequenceService.getScheduleTime(worktime, dm.getAppointmentTime());
        setAttr("times", times);
        setAttr("dicModality", dm);
		setAttr("map", sv.showSchedulesDetail(getPara("date"), getParaToInt("modalityid")));
		render("/template/schedule_6.html");
//		setAttr("map", sv.showSchedules(getPara("date"), getParaToInt("modalityid")));
//		render("/template/"+ScheduleTempUtil_del.getTemplate(getPara("modality"))+".html");
	}
	
	/**
	  *  获取预约的设备类型能否使用的时间
	 */
	public void showScheduleTime() {
		Integer modalityid = getParaToInt("modalityid");
		DicModality dm = DicModality.dao.findById(modalityid);
		LocalDate date = LocalDate.parse(getPara("date"));
		String worktime = "";//dm.getWorkdayOfWorktime();
		if (DayOfWeek.SATURDAY.equals(date.getDayOfWeek())) {
			worktime = dm.getSaturdayOfWorktime();
			if (StrKit.isBlank(worktime)) {
				worktime = PropKit.use("system.properties").get("saturday_of_worktime",worktime);
			}
		} else if (DayOfWeek.SUNDAY.equals(date.getDayOfWeek())) {
			worktime=dm.getSundayOfWorktime();
			if (StrKit.isBlank(worktime)) {
				worktime=PropKit.use("system.properties").get("sunday_of_worktime",worktime);
			}
		} else {
			worktime=dm.getWorkdayOfWorktime();
			if (StrKit.isBlank(worktime)) {
				worktime=PropKit.use("system.properties").get("workday_of_worktime");
			}
		}
		
		List<Integer> times = SequenceService.getScheduleTime(worktime, dm.getAppointmentTime());
		System.out.println("times.size():"+times.size());
		setAttr("worktime", worktime);
		setAttr("times", times);
		setAttr("appointment_time", dm.getAppointmentTime());
		setAttr("appointment_number", dm.getAppointmentNumber());
		setAttr("interval", dm.getAveragetime());
		setAttr("orderid", getParaToInt("orderid"));
		
//		if(getParaToInt("modalityid")==null) {
//			render("/template/null.html");
//		}
//		else {
			setAttr("map", sv.getScheduleTime_new(date, modalityid, getParaToInt("orderid")));
			render("/template/schedule_time.html");
//		}
	
	}
	

	
	/**
	  *  保存和编辑预约信息
	 */
	public void saveSchedule() {
		try {
			User user = (User) getSession().getAttribute("user");

			Patient patient = getModel(Patient.class, "", true);
			patient.setId(getParaToInt("patient_id"));

			Admission admission = getModel(Admission.class, "", true);
			admission.setId(getParaToInt("admission_id"));
			
			Studyorder so = getModel(Studyorder.class, "", true);
			so.setId(getParaToInt("studyorder_id"));

			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("itemstr", getPara("itemsvalue"));//检查项目
			paramMap.put("patientremark", getPara("patientremark"));
			paramMap.put("admissionremark", getPara("admissionremark"));
			paramMap.put("studyorderremark", getPara("studyorderremark"));
			paramMap.put("scanimgs", getParaValues("app_scan_img"));//申请单

			
			setAttr("appointmenttime", getPara("studyorder.appointmenttime"));
			
			if (patient.getId() != null&&admission.getId() != null&&so.getId() != null) {
			    //修改预约信息
			    if (sv.checkScheduleTime(get("studyorder.appointmenttime"), so.getModalityid(), so.getId())) {
			        if(sv.modifySchedule(patient, so, admission, paramMap, user)) {
	                    renderJson(ResultUtil.success
	                            (new Record().set("patientid", patient.getPatientid()).set("studyid", so.getStudyid())
	                                    .set("accession_no", admission.getAdmissionid()).set("orderid", so.getId())
	                                    .set("sequencenumber", so.getSequencenumber())));
	                } else {
	                    renderJson(ResultUtil.fail(-1,"修改失败请重试，如果问题依然存在请联系系统管理员！"));
	                }
                } else {
                    renderJson(ResultUtil.fail(-1,"此时间段已经被预约，请重新选择预约时间！"));
                }
			} else {
			    //保存预约信息
			    if (sv.checkScheduleTime(get("studyorder.appointmenttime"), so.getModalityid(), null)) {
			        if(sv.saveSchedule(patient, so, admission, paramMap, user)) {
	                    renderJson(ResultUtil.success
	                            (new Record().set("patientid", patient.getPatientid())
	                                    .set("studyid", so.getStudyid())
	                                    .set("accession_no", admission.getAdmissionid())
	                                    .set("orderid", so.getId())
	                                    .set("sequencenumber", so.getSequencenumber())));
	                } else {
	                    renderJson(ResultUtil.fail(-1,"保存失败请重试，如果问题依然存在请联系系统管理员！"));
	                }
                } else {
                    renderJson(ResultUtil.fail(-1,"此时间段已经被预约，请重新选择预约时间！"));
                }
			}
		} catch(Exception e){
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	//查询预约信息
	public void getScheduleorder(){
		try {
			Page<Record> page = sv.getSchelderorder(getParaMap(), getSessionAttr("syscode_lan"));
			Map<String, Object> jsonMap = new HashMap<String, Object>();//定义map  
	        jsonMap.put("total", page.getTotalRow());//total键 存放总记录数，必须的  
	        jsonMap.put("rows", page.getList());//rows键 存放每页记录 list  
			renderJson("data", jsonMap);
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	//获取检查项目
	public void getStudyItem(){
		try {
			renderJson(Studyitem.dao.find("select * from studyitem where orderid=?",getParaToInt("orderid")));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//取消预约
	public void cancelScheduleOrder() {
		try {
			User user = (User) getSession().getAttribute("user");
			int orderid=getParaToInt("orderid");
			if(sv.cancelScheduleOrder(orderid,user)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
		
	//删除预约
	public void deleteScheduleOrder(){
		try{
			if(sv.deleteScheduleOrder(getParaToInt("orderid"))) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		}
		catch(Exception e){
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//删除病人信息
	public void deletePatient() {
		try {
			User user = (User) getSession().getAttribute("user");
			renderJson(ResultUtil.success(sv.deletePatient(getParaToInt("patientpkid"), user)));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 修改病人信息
	 */
	public void modifyPatAndAdm() {
		try {
			Patient patient=getModel(Patient.class,true);
			Admission admission=getModel(Admission.class,true);
			if(sv.modifyPatAndAdm(patient,admission)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(-1,"保存失败请重试，如果问题依然存在请联系系统管理员！"));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	/**
	 * 修改检查信息
	 */
	public void modifyStudy() {
		try {
			Studyorder so=getModel(Studyorder.class,true);
			
			String itemstr=getPara("itemsvalue");
			so.set("appointmenttime", getPara("appointmenttime1"));
			
			String studydatetime=getPara("appointmenttime1");
			
			if(sv.modifyStudy(so, itemstr,studydatetime)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(-1,"保存失败请重试，如果问题依然存在请联系系统管理员！"));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
		
	/**
	 *  预约转登记
	 */
	public void sch_to_reg() {
		try {
			User user = (User) getSession().getAttribute("user");
			if(sv.sch_to_regs(get("orderids"),user)) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1,"保存失败请重试，如果问题依然存在请联系系统管理员！"));
			}
		}catch(Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	
	/**
	 * 获取查询条件
	 */
	public void getFilters() {
		try {
			User user = (User) getSession().getAttribute("user");
			renderJson(sv.findFilters(getParaToInt("_id"),user.getId(),getPara("filterType"),getSessionAttr("language")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
		
	/**
	 * 保存查询条件
	 */
	public void saveFilter() {
		try {
			Filter record = getModel(Filter.class,"",true);
			User user = (User) getSession().getAttribute("user");
			if (frontCommonsv.saveFilter(record,user,getParaMap())) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1, "保存失败"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
		
	/**
	 * 删除保存的查询条件
	 */
	public void deleteFilter() {
		try {
			if (frontCommonsv.deleteFilters(getPara("id"))) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail("删除失败！"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}
	
	/**
	  *  校验 我的条件 保存的命名是否已经存在
	 */
	public void checkMyConditionName() {
	    User user = (User) getSession().getAttribute("user");
	    log.info("我的条件保存的模块为：" + get("filterType") + ",命名为：" + get("name"));
	    Boolean res = frontCommonsv.checkMyConditionName(user.getId(), get("filterType"), get("name"));
	    log.info("此名称是否已经存在：" + res);
	    renderJson(ResultUtil.success(res));
	}

	//预约签到
	public void signin() {
		try {
			User user = (User) getSession().getAttribute("user");
			renderJson(ResultUtil.success(sv.signin(getInt("orderid"), user)));
		}catch(Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//提前预约签到  不需要做时间控制
	public void advanceSignin() {
		try {
			User user = (User) getSession().getAttribute("user");
			renderJson(ResultUtil.success(sv.advanceSignin(getInt("orderid"), user)));
		}catch(Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

//******************************规避权限检查重复冲突********************************************
		
	//申请单
	public void getImage(){
		try {
			renderJson(ResultUtil.success(frontCommonsv.getApplyImage(getParaToInt("orderid"))));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}	
	}
	
	//检查流程
	public void gotoStudyProcess(){
		Record record=frontCommonsv.findStudyInfo(getParaToInt("orderid"));
		setAttr("record", record);
		renderJsp("/view/front/studyprocess.jsp");
	}
		
//******************************规避权限检查重复冲突********************************************
	//取走报告
	public void takeAwayReport() {
		try {
			Report report = Report.dao.findById(getParaToInt("reportid"));
			if(report != null) {
				report.setIstakenaway(1);
				report.update();
			}
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	//取走胶片
	public void takeAwayFilm() {
		try {
			Studyorder so = Studyorder.dao.findById(getParaToInt("orderid"));
			if(so != null) {
				so.setFilmtakenaway(1);
				so.update();
			}
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
}
