package com.healta.controller;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.model.Report;
import com.healta.model.SRTemplate;
import com.healta.model.Srsection;
import com.healta.model.Syngoviaimage;
import com.healta.model.Syngoviareport;
import com.healta.model.User;
import com.healta.model.UserProfiles;
import com.healta.render.ImageRender;
import com.healta.render.PdfRender;
import com.healta.service.ReportService;
import com.healta.util.CallupKit;
import com.healta.util.IPKit;
import com.healta.util.ResultUtil;
import com.healta.util.SerializeRes;
import com.jfinal.core.Controller;
import com.jfinal.i18n.Res;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

/**
 * 检查报告相关控制层
 * 
 * @author Administrator
 *
 */
public class SRReportController extends Controller {
	
	/**
	 * 业务层
	 */
	
	private final static Logger log = Logger.getLogger(SRReportController.class);
	private static ReportService sv=new ReportService();
	
	public void openReportWithSRTemp() {
		try {
			User user = (User) getSession().getAttribute("user");
			SerializeRes r=(SerializeRes)getSessionAttr("locale");
			
			renderJson(ResultUtil.success(sv.openReport(getParaToInt("orderid"),getParaToInt("reportid"),user,getParaToInt("srtemplateid"),r)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void structReport(){
		try {
			User user=(User)getSession().getAttribute("user");
			
			String nrsts=getPara("normalreportswitchToSr");
			String studyid=getPara("studyid");
			Integer orderid = getParaToInt("orderid");
			Integer reportid = getParaToInt("reportid");
			Record record=sv.getStydyAndReportInfo(reportid);
			setAttr("studyorderfk", orderid);
			setAttr("reportid", reportid);
			
			setAttr("patientidfk", record.getInt("patientidfk"));
			setAttr("patientid", record.getStr("patientid"));
			setAttr("studyid", studyid);
			setAttr("patientname", record.getStr("patientname"));
			
			setAttr("viareportid", record.getStr("viareportid"));
			setAttr("studyinsuid", record.getStr("studyinstanceuid"));
			setAttr("PreAudit", PropKit.use("system.properties").getBoolean("PreAudit", true));
			setAttr("reportStatus", record.getStr("reportstatusdisplaycode"));
			setAttr("studyitem", record.getStr("studyitems"));
		
			UserProfiles profiles=(UserProfiles)getSession().getAttribute("myprofiles");
			if(profiles!=null){
				setAttr("report_assistant_collapsed", profiles.getReportAssistantCollapsed());
			}
			
			if("1".equals(nrsts)){
				setAttr("srtemplateid", getPara("template_id"));
			}
			else{
				if(StringUtils.isBlank(record.getStr("template_id"))&&!StringUtils.isBlank(record.getStr("template_id"))){
					setAttr("srtemplateid", record.getStr("template_id"));
				}
				else{
					setAttr("srtemplateid", getPara("template_id"));
				}
			}
			
			String srtempid=getPara("template_id");
			SRTemplate srt=SRTemplate.dao.findById(srtempid);
			if(srt!=null){
				setAttr("srtemplatename",Base64.encodeBase64String(srt.getName().getBytes("UTF-8")));
			}
			if(srt!=null&&srt.getFilterWidth()!=null){
				setAttr("filter_width", srt.getFilterWidth());
			}
			
//			String studyinstanceuid=record.getStr("studyinstanceuid");
//			String para=PropKit.use("system.properties").get("launcherviapara");
//			//log.info(para+"--"+studyinstanceuid);
//			if(studyinstanceuid!=null){
//				para=para.replace("?", studyinstanceuid);
//			}
//			else{
//				para=para.replace("?", studyid);
//			}
//			setAttr("para", para);
			String callviapara=PropKit.use("system.properties").get("launcherviapara");
			String plaza_loaddata=CallupKit.callUpPlaza_loadDataCmd(user);
			if(StrKit.notBlank(studyid)){
				callviapara=callviapara.replace("?", studyid);
				plaza_loaddata=plaza_loaddata+" "+studyid;
			}
			
			setAttr("enable_via_callup", PropKit.use("system.properties").get("enable_via_callup"));
			setAttr("callviapara", callviapara);
			setAttr("enable_plaza_callup", PropKit.use("system.properties").get("enable_plaza_callup"));
			setAttr("plaza_loaddata", plaza_loaddata);
			setAttr("reporttitle", PropKit.use("system.properties").get("reporttitle"));
//			setAttr("projecturl", IPKit.getServerIP(getRequest()));
			setAttr("projecturl", getRequest().getScheme()+"://"+IPKit.getServerIP(getRequest())+":"+getRequest().getServerPort()+getRequest().getContextPath());
			
			renderJsp("/view/front/report/structReport.jsp");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存结构化报告数据
	 */
	public void saveStructReport() {
		String params = getPara("s");
		String html=getPara("html");
		try {
			Report report=getModel(Report.class, "", true);
			User user = (User) getSession().getAttribute("user");
			String publiclabel=getPara("publiclabel");
			String privatelabel=getPara("privatelabel");
			String icd10 = get("icd10label"); 
            //log.info("icd10为：" + icd10);
			sv.saveStructReport(report, params, html, user,getRequest().getContextPath(),publiclabel,privatelabel,icd10,getParaToBoolean("savelabel_flag"));
			renderJson(ResultUtil.success(report));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1,e.getMessage()));
		}
	}
	
	/**
	 * 提交结构化报告数据
	 */
	public void submitStructReport() {

		String params = getPara("s");
		String html=getPara("html");
		try {
			Report report=getModel(Report.class, "", true);
			User user = (User) getSession().getAttribute("user");
			String publiclabel=getPara("publiclabel");
			String privatelabel=getPara("privatelabel");
			String icd10 = get("icd10label"); 
            log.info("icd10为：" + icd10);
			sv.submitStructReport(report, params, html, user,getRequest().getContextPath(),publiclabel,privatelabel,icd10);
			renderJson(ResultUtil.success(report));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1,e.getMessage()));
		}
	}
	
	/**
	 * 初审结构化报告数据
	 */
	public void auditPreStructReport() {

		String params = getPara("s");
		String html=getPara("html");
		try {
			Report report=getModel(Report.class, "", true);
			User user = (User) getSession().getAttribute("user");
			String publiclabel=getPara("publiclabel");
			String privatelabel=getPara("privatelabel");
			String icd10 = get("icd10label"); 
            log.info("icd10为：" + icd10);
			sv.auditPreStructReport(report, params, html, user,getRequest().getContextPath(),publiclabel,privatelabel,icd10);
			renderJson(ResultUtil.success(report));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1,e.getMessage()));
		}
	}
	
	public void saveStructReport_printhtml(){
		
		Integer id=getParaToInt("id");
		String printhtml=getPara("printhtml");
		try {
			sv.saveStructReport_printhtml(id, printhtml,getRequest().getContextPath());
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1,e.getMessage()));
		}
	}
	
	/**
	 * 医疗初次审核 结构化报告
	 */
	public void preAuditStructReport(){
		String params = getPara("s");
		String studyId = getPara("studyid");
		String templetId = getPara("templetId");
		try {
//			boolean flag = reportService.preAuditStructReport(studyId, params, templetId,getSession(false));
//			if(flag) {
//				Record re =reportService.queryReportInfo(studyId);
//				renderJson(ResultUtil.success(re));
//			}else {
//				renderJson(ResultUtil.fail(-1,"保存失败请联系管理员"));
//			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1,e.getMessage()));
		}
	}
	/**
	 * 审核结构化报告
	 */
	public void auditStructReport(){

		try {
			String params = getPara("s");
			String html=getPara("html");
			Report report=getModel(Report.class, "", true);
			User user = (User) getSession().getAttribute("user");
			String publiclabel=getPara("publiclabel");
			String privatelabel=getPara("privatelabel");
			String icd10 = get("icd10label"); 
            log.info("icd10为：" + icd10);
			sv.auditStructReport(report, params, html, user,getRequest().getContextPath(),publiclabel,privatelabel,getRequest().getServerPort(),icd10);
			renderJson(ResultUtil.success(report));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1,e.getMessage()));
		}
	}
	/**
	 * 医疗驳回 结构化报告
	 */
	public void rebutStructReport(){
		String params = getPara("s");
		String studyId = getPara("studyid");
		String templetId = getPara("templetId");
		try {
//			boolean flag = reportService.rebutStructReport(studyId, params, templetId,getSession(false));
//			if(flag) {
//				Record re =reportService.queryReportInfo(studyId);
//				renderJson(ResultUtil.success(re));
//			}else {
//				renderJson(ResultUtil.fail(-1,"保存失败请联系管理员"));
//			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1,e.getMessage()));
		}
	}
	/**
	 * 获取结构化报告内容
	 */
	public void getStructReport() {
//		renderJson(reportService.getStructReport(getPara("studyid")));
	}
	
	public void openSwitchTemplateDialog(){
		try {

			setAttr("studyid", getPara("studyid"));
			setAttr("orderid", getParaToInt("orderid"));
			setAttr("reportid", getParaToInt("reportid"));
			setAttr("ctx", getRequest().getContextPath());
			setAttr("locale", getSessionAttr("locale"));
			//renderJsp("/view/front/report/switchtemplate.jsp");
			render("/view/front/report/switchtemplate.html");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void goToRearrageSnaphsot(){
		//setAttr("id", getPara("id"));
		renderJsp("/view/front/report/rearrange_snapshot.jsp");
	}
	
	public void getSyngoviaSRData(){
		
		String studyid=getPara("studyid");
		String codes=getPara("codes");
		renderJson(sv.getSyngoviaSRData(studyid, codes,getSessionAttr("language")));
	}
	
	
	public void getFinding(){
		try {
			renderJson(sv.getFindings(getPara("studyid"),getPara("studyinsuid")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void getFindingImages(){
		try {
			renderJson(sv.getFindingImages(getPara("studyid"),getPara("studyinsuid")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void getSectionByUid(){
		renderJson(Srsection.dao.findFirst("select top 1 * from srsection where uid=?",getPara("uid")));
	}
	
	public void getPdf(){
		Syngoviareport report=Syngoviareport.dao.getByStudyInsUid(getPara("studyinsuid"));
		if(report!=null&&report.getPdffile()!=null){
			render(new PdfRender(PropKit.use("system.properties").get("sysdir")+"\\"+report.getPdffile()));
		}
		else{
			renderJson();
		}
	}
	
	public void getViaImage(){
		
		Integer id=getParaToInt("id");
		if(id!=null){
			Syngoviaimage image=Syngoviaimage.dao.findById(id);
			
			if(image!=null&&image.getImagefile()!=null){
				render(new ImageRender(image.getImagefile()));
			}
			else{
				renderJson();
			}
		}
		else{
			renderJson();
		}
//		renderFile(Syngoviaimage.dao.findById(getParaToInt("id")).getImagefile());
	}
	
//	public void getBase64Srtemplatename() {
//		try {
//			SRTemplate srt=SRTemplate.dao.findById(getParaToInt("srtemplateid"));
//            String srtemplatename = "";
//            if(srt != null) {
//            	srtemplatename = Base64.encodeBase64String(srt.getName().getBytes("UTF-8"));
//            }
//			renderJson(ResultUtil.success(srtemplatename));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			renderJson(ResultUtil.fail(1, e.getMessage()));
//		}
//	}

	public void importViaDataFromClipboard(){
//		try {
//			if(sv.importViaDataFromClipboard(get("uuid"),getParaToInt("source"), get("studyid"), get("studyinsuid"))){
//				renderJson(ResultUtil.success());
//			} else{
//				renderJson(ResultUtil.fail("error"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			renderJson(ResultUtil.fail(-1,e.getMessage()));
//		}
	}
}
