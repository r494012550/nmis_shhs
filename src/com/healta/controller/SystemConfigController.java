package com.healta.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.healta.config.MainConfig;
import com.healta.constant.PrintTemplateType;
import com.healta.model.Client;
import com.healta.model.DicReportcorrectRules;
import com.healta.model.Label;
import com.healta.model.Labelfolder;
import com.healta.model.Modules;
import com.healta.model.Printer;
import com.healta.model.Printtemplate;
import com.healta.model.User;
import com.healta.plugin.cron4j.MyCron4jPlugin.TaskInfo;
import com.healta.service.SystemConfigService;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

public class SystemConfigController extends Controller {

	private static final SystemConfigService sv = new SystemConfigService();
	
	public void getModules(){
		renderJson(sv.findModules(getPara("value")));
	}
	
	public void saveModule(){
		try {
			Modules res=getModel(Modules.class,"",true);
			if (sv.saveModule(res)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(1, "保存失败！"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	
	public void deleteModule(){
		try {
			Modules.dao.deleteById(getParaToInt("modulesid"));
			renderJson(ResultUtil.success());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
/*
 * label增删改查
 */
	public void getLabels(){
		User user = (User) getSession().getAttribute("user");
		renderJson(sv.getLabels(user.getUsername()));
	}
	
	public void saveLabelFolder(){
		try {
			Labelfolder lf = getModel(Labelfolder.class, "", true);
			User user = (User) getSession().getAttribute("user");
			if(sv.saveLabelFolder(lf, user.getId())) {
				renderJson(ResultUtil.success(lf));
			}
			else {
				renderJson(ResultUtil.fail(-1, "error"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	public void delLabelFolder(){
		try {
			User user = (User) getSession().getAttribute("user");
			
			if(sv.deleteLabelFolder(getParaToInt("id"), user.getId())) {
				renderJson(ResultUtil.success());
			}
			else {
				renderJson(ResultUtil.fail(-1, "error"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void saveLabel(){
		try {
			Label l = getModel(Label.class, "", true);
			User user = (User) getSession().getAttribute("user");
			if(sv.saveLabel(l, user.getId())) {
				renderJson(ResultUtil.success(l));
			}
			else {
				renderJson(ResultUtil.fail(-1, "error"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	public void delLabel(){
		try {
			
			if(sv.deleteLabel(getParaToInt("id"))) {
				renderJson(ResultUtil.success());
			}
			else {
				renderJson(ResultUtil.fail(-1, "error"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
/*
 * 打印机
 * *
 */
	
	public void getPrinter() {
		try{
			renderJson(sv.getPrinter(getPara("value")));			
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void ModifyPrinter() {
			if(StringUtils.isNotEmpty(getPara("id"))){
				int id=getParaToInt("id");
				Printer p=Printer.dao.findById(id);
				setAttr("printer", p);
				}
				renderJsp("/view/admin/system/editPrinter.jsp");
		
	 }
	 
	 public void savePrinter() {
		 try{
			 Printer printer=getModel(Printer.class, "", true);
			 if(sv.savePrinter(printer)){
					renderJson(ResultUtil.success());
				}
				else{
					renderJson(ResultUtil.fail(""));
				}
			} catch (Exception e) {
				e.printStackTrace();
				renderJson(ResultUtil.fail(-1, e.getMessage()));
			}
	 }
	 
	 public void deletePrinter() {
		 try {
				if(sv.deletePrinter(getParaToInt("id"))) {				
					renderJson(ResultUtil.success());
				}
				else {
					renderJson(ResultUtil.fail(""));
				}
			}catch(Exception e) {
				renderJson(ResultUtil.fail(-1, e.getMessage()));
			}
	 }
 /*
  * 客户端
  * *
  */
	 public void checkIP(){
		 if(sv.checkIP(getPara("hostip"),getParaToInt("id"))){
			renderJson(ResultUtil.success());	
		}
		else{
			renderJson(ResultUtil.fail(""));
		}
		 
	 }
	 
	 /**
	  * 获取注册的客户端
	  */
	 public void getClient() {
		try{
			renderJson(sv.getClient(getPara("value"),getParaToInt("modalityid")));			
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	 }
	
	 public void modifyClient() {
		if(StringUtils.isNotEmpty(getPara("id"))){
			int id=getParaToInt("id");
			Client c=Client.dao.findById(id);				
			setAttr("id", id);
			setAttr("type", c.get("type"));
			setAttr("hostip", c.get("hostip"));
			setAttr("zone", c.get("zone"));
			setAttr("hostname", c.get("hostname"));
			setAttr("modalityid", c.get("modalityid"));
			}
			renderJsp("/view/admin/system/editClient.jsp");
	 }
	 
	 /**
	 * 保存客户端
	 */
	 public void saveClient() {
		 try{
			 Client c=getModel(Client.class, "", true);
			 if(sv.saveClient(c)){
					renderJson(ResultUtil.success());
				}
				else{
					renderJson(ResultUtil.fail(""));
				}
			} catch (Exception e) {
				e.printStackTrace();
				renderJson(ResultUtil.fail(-1, e.getMessage()));
			}
	 }
	 
	 /**
	  * 删除客户端
	  */
	public void deleteClient() {
		try {
			if(sv.deleteClient(getParaToInt("id"))) {				
				renderJson(ResultUtil.success());
			}
			else {
				renderJson(ResultUtil.fail(""));
			}
		 }catch(Exception e) {
			 renderJson(ResultUtil.fail(-1, e.getMessage()));
		 }
	 }
	 
	 public void risEnvents() {
		 try {
			if(sv.saveRisTriggers(getPara("name"),getParaToBoolean("on_off"))) {				
				renderJson(ResultUtil.success());
			}
			else {
				renderJson(ResultUtil.fail(""));
			}
		 }catch(Exception e) {
			 renderJson(ResultUtil.fail(-1, e.getMessage()));
		 }
	 }
	 
	public void getAllTask() {
		List<TaskInfo> list=MainConfig.cron4jPlugin.getTaskInfoList();
		List<Record> re=new ArrayList<Record>();
		for(TaskInfo ti:list){
			Record record = new Record();
			record.set("isstarted", ti.getScheduler().isStarted());
			record.set("isdaemon", ti.getScheduler().isDaemon());
			record.set("taskname", ti.getTask().getClass().getName()+ti.toString());
			record.set("schedulerid", ti.getSchedulerId());
			
			
			try {
				Method method=ti.getTask().getClass().getMethod("getTaskName");
				if(method!=null){
					record.set("task", method.invoke(ti.getTask()));
				}
				
				method=ti.getTask().getClass().getMethod("getLastRunTime");
				if(method!=null){
					record.set("lastruntime", method.invoke(ti.getTask()));
				}
				
				method=ti.getTask().getClass().getMethod("getErrorMessage");
				if(method!=null){
					record.set("errormessage", method.invoke(ti.getTask()));
				}
				
				method=ti.getTask().getClass().getMethod("canCopy");
				if(method!=null){
					record.set("copy", method.invoke(ti.getTask()));
				}
				
			 } catch (NoSuchMethodException e) {
				e.printStackTrace();
			 } catch (SecurityException e) {
				e.printStackTrace();
			 } catch (IllegalAccessException e) {
				e.printStackTrace();
			 } catch (IllegalArgumentException e) {
				e.printStackTrace();
			 } catch (InvocationTargetException e) {
				e.printStackTrace();
			 }
			 record.set("cron", ti.getCron());
			 re.add(record);
		 }
		 renderJson(re);
	 }
	
	public void startTask(){
		try {
			String schedulerid=getPara("schedulerid");
			boolean start=getParaToBoolean("start");
			List<TaskInfo> list=MainConfig.cron4jPlugin.getTaskInfoList();
			for(TaskInfo ti:list){
				if(StrKit.equals(ti.getSchedulerId(), schedulerid)){
					if(start){
						if(!ti.getScheduler().isStarted()){
							ti.schedule();
							ti.start();
						}
					}
					else{
						if(ti.getScheduler().isStarted()){
							ti.stop();
						}
					}
					break;
				}
			}
			renderJson(ResultUtil.success());
		}catch(Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	public void copyTask(){
		try {
			String schedulerid=getPara("schedulerid");
			int num=getParaToInt("num");
			TaskInfo taskInfo=null;					
			List<TaskInfo> list=MainConfig.cron4jPlugin.getTaskInfoList();
			for(TaskInfo ti:list){
				if(StrKit.equals(ti.getSchedulerId(), schedulerid)){
					taskInfo=ti;
					break;
				}
			}
			if(taskInfo!=null){
				for(int i=0;i<num;i++){
					MainConfig.cron4jPlugin.addTask(taskInfo.getCron(), BeanUtils.cloneBean(taskInfo.getTask()), true, true);
				}
			}
			renderJson(ResultUtil.success());
		}catch(Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void goEditTask() {
		setAttr("cron", getPara("cron"));
		renderJsp("/view/admin/system/editTask.jsp");
	}
	
	public void saveTask(){
		try {
			String schedulerid=getPara("schedulerid");
			String cron=getPara("cron");
			List<TaskInfo> list=MainConfig.cron4jPlugin.getTaskInfoList();
			for(TaskInfo ti:list){
				if(StrKit.equals(ti.getSchedulerId(), schedulerid)){
					ti.reSchedule(schedulerid, cron);
					break;
				}
			}
			renderJson(ResultUtil.success());
		}catch(Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
    //查询打印模板
	public void findPrinttemplate(){
		try{
			renderJson(sv.findPrinttemplate(getPara("template_name")));
		
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
		/*//打开编辑预约单模板
		public void goToEditReservation(){
			if(getParaToInt("id") != null) {
				Printtemplate printtemplate = Printtemplate.dao.findById(getParaToInt("id"));
				setAttr("printtemplate", printtemplate);
			}
			renderJsp("/view/admin/system/editReservation.jsp");
		}
		//打开编辑检查单界面
		public void goToEditChecklist(){
			if(getParaToInt("id") != null) {
				Printtemplate printtemplate = Printtemplate.dao.findById(getParaToInt("id"));
				setAttr("printtemplate", printtemplate);
			}
			renderJsp("/view/admin/system/editChecklist.jsp");
		}*/
		//打开编辑报告模板界面
	public void goToEditPrinttemplate(){
//			if(getParaToInt("id") != null) {
//				Printtemplate printtemplate = Printtemplate.dao.findById(getParaToInt("id"));
//				setAttr("printtemplate", printtemplate);
//			}
		setAttr("types", PrintTemplateType.values());
		setAttr("ctx", getRequest().getContextPath());
		render("/view/admin/system/editPrinttemplate.html");
	}
		
	//保存打印模板
	public void savePrinttemplate() {
		Printtemplate printtemplate = getModel(Printtemplate.class, "", true);
		try {
			if(sv.savePrinttemplate(printtemplate, getPara("idsstr"))) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//删除打印模板
	public void deletePrinttemplate() {
		try {
			if(sv.deletePrinttemplate(get("ids"))) {				
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(""));
			}
		 }catch(Exception e) {
			 renderJson(ResultUtil.fail(-1, e.getMessage()));
		 }
	}
	
	//获取打印模板对应的检查项目
	public void getExamitemByPrinttemplate() {
		try {
			renderJson(sv.getExamitemByPrinttemplate(getPara("modality_type"),get("itemname")));
		 }catch(Exception e) {
			 renderJson(ResultUtil.fail(-1, e.getMessage()));
		 }
	}
	
	
	/**
	 * 报告检查规则
	 */
	public void getReportCheckErrorRules(){
		try{
			renderJson(sv.getReportCheckErrorRules(getPara("keyword")));			
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void toEditReportCorrectRules(){
		if(StringUtils.isNotBlank(getPara("id"))){
			setAttr("rule", DicReportcorrectRules.dao.findById(getParaToInt("id")));
		}
		renderJsp("/view/admin/system/editReportCorrectRules.jsp");
	}
	
	/**
	 * 报告检查规则保存
	 */
	public void saveReportCorrectRule(){
		try{
			if(sv.saveReportCorrectRule(getModel(DicReportcorrectRules.class, "", true))){
				renderJson(ResultUtil.success());
			}
			else{
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			//e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	
	/**
	 * 删除报告纠错规则
	 */
	public void deleteCorrectRule(){
		try {
			if(sv.deleteCorrectRule(getParaToInt("id"))) {				
				renderJson(ResultUtil.success());
			}
			else {
				renderJson(ResultUtil.fail(""));
			}
		 }catch(Exception e) {
			 renderJson(ResultUtil.fail(-1, e.getMessage()));
		 }
	}
	
	public void checkKeyword(){
		 if(sv.checkKeyword(getPara("keyword"))){
			renderJson(ResultUtil.success());	
		}
		else{
			renderJson(ResultUtil.fail(""));
		}
	}
	
	public void getPrintTemps() {
		renderJson(sv.getPrintTemps(get("type")));
	}
}
