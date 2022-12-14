package com.healta.controller;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;

import com.healta.config.MainConfig;
import com.healta.constant.ReportStatus;
import com.healta.constant.ReportType;
import com.healta.license.VerifyLicense;
import com.healta.model.Favorites;
import com.healta.model.Favoritesreport;
import com.healta.model.Inquiry;
import com.healta.model.Label;
import com.healta.model.Labelfolder;
import com.healta.model.Phrase;
import com.healta.model.PhraseNode;
import com.healta.model.Rejectopinion;
import com.healta.model.Report;
import com.healta.model.Reportrace;
import com.healta.model.Reportremark;
import com.healta.model.Retrialviewlist;
import com.healta.model.SRTemplate;
import com.healta.model.Studyorder;
import com.healta.model.User;
import com.healta.model.UserProfiles;
import com.healta.service.ReportService;
import com.healta.util.CallupKit;
import com.healta.util.IPKit;
import com.healta.util.ResultUtil;
import com.healta.util.SerializeRes;
import com.healta.util.SyscodeKit;
import com.healta.util.TextCompareKit;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;

/**
 * ???????????????????????????
 * 
 * @author Administrator
 *
 */
public class ReportController extends Controller {

	
	/**
	 * ?????????
	 */
	
	private final static Logger log = Logger.getLogger(ReportController.class);
	//private ReportService_del reportService = new ReportServiceImpl();
	
	private static ReportService sv=new ReportService();
	
	public void report_template(){
		/*Interrogation interrogation = Interrogation.dao.getFirstByorderid(getParaToInt("orderid"));
		setAttr("briefHistory", interrogation);*/
		User user=(User)getSession().getAttribute("user");
		setAttr("reportid",getParaToInt("reportid"));
		setAttr("orderid",getParaToInt("orderid"));
		setAttr("studyid", getPara("studyid"));
		setAttr("modality", getPara("modality"));
		setAttr("patientid", getPara("patientid"));
		setAttr("examination_position", getPara("examination_position"));
		setAttr("sex", getPara("sex"));
		setAttr("userid", user.getId());
		
		renderJsp("/view/front/report/report_template.jsp");
	 	//renderJsp("/view/front/examine/mannequin.jsp"); 
	}
	
	/**
	 * ????????????????????????????????????
	 */
	public void report_Assistant_Panel() {
		User user=(User)getSession().getAttribute("user");
		
		Integer reportid=getParaToInt("reportid");
		setAttr("reportid", reportid);
		
		List<String> values=sv.getReportLabel(reportid,user);
		setAttr("publiclabelvalue", values.get(0));
		setAttr("privatelabelvalue", values.get(1));
		Studyorder so = Studyorder.dao.findById(getParaToInt("orderid"));
		setAttr("studyid", getPara("studyid"));
		setAttr("orderid", getParaToInt("orderid"));
		setAttr("modality", getPara("modality"));
		setAttr("patientidfk", getParaToInt("patientidfk"));
		setAttr("enable_plaza_callup", PropKit.use("system.properties").get("enable_plaza_callup"));
		setAttr("icd10s", sv.getIcd10ByReportId(reportid));
		String getStudyid=so.getStudyid();
		Inquiry interrogation = Inquiry.dao.getFirstByorderid(getParaToInt("orderid"));
		setAttr("briefHistory", interrogation);
		setAttr("plaza_loaddata", CallupKit.callUpPlaza_loadDataCmd(user)+" "+getStudyid);
		
		//??????????????????
		Record inquiry=sv.findInquiryByStudyorderid(getParaToInt("orderid"));
		if(inquiry!=null) {
			setAttr("inquiry", inquiry);
		}
		renderJsp("/view/front/report/report_assistant_panel.jsp");
	}
	
	
	
	public void reportTrack(){
		renderJsp("/view/front/report/report_track.jsp");
	}

	/**
	 * ????????????
	 */
	public void saveReport() {
		try {
			Report report = getModel(Report.class, "", true);
			String publiclabel=getPara("publiclabel");
			String privatelabel=getPara("privatelabel");
			String icd10 = get("icd10label"); 
            log.info("icd10??????" + icd10);
			User user = (User) getSession().getAttribute("user");
			sv.saveReport(report,publiclabel,privatelabel, user, icd10,get("imageids"));
			renderJson(ResultUtil.success(report));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * ??????????????????
	 */
	public void submitReport() {
		try {
			Report report = getModel(Report.class, "", true);
			String publiclabel = getPara("publiclabel");
			String privatelabel = getPara("privatelabel");
			String devSN = getPara("devSN");
			User user = (User) getSession().getAttribute("user");
			String icd10 = get("icd10label"); 
			log.info("icd10??????" + icd10);
			List<String> error=new ArrayList<String>();
			sv.submitReport(report,publiclabel,privatelabel, user, icd10,get("imageids"),devSN,getSession(),error);
			if(error.size()==0){
				report=Report.dao.findById(report.getId());
				renderJson(ResultUtil.success(report));
			} else{
				renderJson(ResultUtil.fail(-2, error.get(0)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * ??????????????????
	 */
	public void auditPreReport() {
		try {
			Report report = getModel(Report.class, "", true);
			String publiclabel=getPara("publiclabel");
			String privatelabel=getPara("privatelabel");
			String devSN = getPara("devSN");
			User user = (User) getSession().getAttribute("user");
			String icd10 = get("icd10label"); 
            log.info("icd10??????" + icd10);
			sv.auditPreReport(report,publiclabel,privatelabel, user, icd10,get("imageids"));
			
			/*//??????????????????
			if(StringUtils.isNotBlank(devSN)) {
				CaUtil.saveSignature(devSN,String.valueOf(report.getId()),"2");
			}*/
			report=Report.dao.findById(report.getId());
			renderJson(ResultUtil.success(report));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	/**
	 * ????????????
	 */
	public void getReport() {
		try {
			Record record = new Record();
			record.set("studyid", getPara("studyid"));
			//renderJson("data", reportService.getReport(record));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ????????????id?????????
	 */
	public void getReportById() {
		try {
			Report report = new Report();
			report.set("id", getParaToInt("id"));
			//renderJson("data", reportService.getReportById(report));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ????????????????????????
	 */
	public void getAllReport() {
		try {
//			Report report = new Report();
//			report.set("studyid", getPara("studyid"));
			renderJson(sv.getAllReport(getPara("studyid")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ????????????
	 */
	public void compareReport() {
		try {
			Record record = new Record();
			record.set("reportid1", getParaToInt("reportid1"));
			record.set("reportid2", getParaToInt("reportid2"));
			//renderJson("conData", reportService.compareReport(record));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ????????????
	 */
	public void openReport() {
		try {
			User user = (User) getSession().getAttribute("user");
			SerializeRes r=(SerializeRes)getSessionAttr("locale");
			renderJson(ResultUtil.success(sv.openReport(getParaToInt("orderid"),getParaToInt("reportid"),user,-1,r)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void report(){
		try {
			User user=(User)getSession().getAttribute("user");
			String studyid=getPara("studyid");
			Integer orderid = getParaToInt("orderid");
			Integer reportid = getParaToInt("reportid");
			setAttr("studyid", studyid);
			setAttr("studyorderfk", orderid);
			setAttr("reportid", reportid);
			setAttr("preAudit", PropKit.use("system.properties").get("PreAudit"));

			UserProfiles profiles=(UserProfiles)getSession().getAttribute("myprofiles");
			if(profiles!=null){
				setAttr("report_assistant_collapsed", profiles.getReportAssistantCollapsed());
				setAttr("up", profiles);
				if(StrKit.notBlank(profiles.getReportDescFontsize())){
					setAttr("desc_fontsize",profiles.getReportDescFontsize());
				}
				if(StrKit.notBlank(profiles.getReportResultFontsize())){
					setAttr("result_fontsize",profiles.getReportResultFontsize());
				}
				setAttr("editor_color", profiles.getReportEditorColor());
			}
			
			/*
			 * ?????????????????????????????????????????????????????????
			 * ???????????????????????????????????????????????????????????????????????????????????????????????????
			 * ????????????????????????????????????285?????????????????????????????????????????????????????????????????????????????????
			 * 
			 * */
			int height=getParaToInt("height")-360;//-440;
			
			String reportname="report";//???????????????????????????????????????
			Record record=sv.getStydyAndReportInfo(reportid);
			if(record!=null){
				setAttr("report", record);

				if(record.getStr("age")!=null){
					setAttr("age", record.getStr("age") + record.getStr("ageunitdisplay"));
				}
				
				String modality_type=record.getStr("modality_type");
				log.info(modality_type);
				if(modality_type.indexOf("\\")>0){
					String[] ms=modality_type.split("\\\\");
					for(String m : ms){
						if(SyscodeKit.INSTANCE.getSysCode("0004", m)!=null){
							modality_type=m;
							break;
						}
					}
				}
				setAttr("modality", modality_type);
				int image_container_height=160;
				Integer reporttype=getInt("reporttype");
				if(reporttype!=null){//??????????????????
					ReportType type= ReportType.getReportType(reporttype);
					if(type!=null&&type!=ReportType.NORMAL&&type!=ReportType.STRUCTURED){
						reportname=type.getReportfilename();
						int imagecolumn=type.getColumn();
						int imageheight=type.getHeight();
						if(imagecolumn>0){
							int row=(type.getName()/imagecolumn);
							image_container_height=row*imageheight+5+row*5;
							height=height-image_container_height;
						}
					}
					setAttr("report_type", reporttype);
				} else if(StrKit.notBlank(record.getStr("report_type"))){//???????????????????????????
					ReportType type= ReportType.getReportType(Integer.valueOf(record.getStr("report_type")));
					if(type!=null&&type!=ReportType.NORMAL&&type!=ReportType.STRUCTURED){
						reportname=type.getReportfilename();
						int imagecolumn=type.getColumn();
						int imageheight=type.getHeight();
						if(imagecolumn>0){
							int row=(type.getName()/imagecolumn);
							image_container_height=row*imageheight+5+row*5;
							height=height-image_container_height;
						}
					}
					setAttr("report_type", record.getStr("report_type"));
				} else if(StrKit.notBlank(modality_type)){//????????????????????????????????????
					reportname=PropKit.use("report_conf.properties").get(modality_type,reportname);
					int imagenumber=PropKit.use("report_conf.properties").getInt(modality_type+"_image_number",0);
					int imagecolumn=PropKit.use("report_conf.properties").getInt(modality_type+"_image_column",0);
					int imageheight=PropKit.use("report_conf.properties").getInt(modality_type+"_image_height",0);
					if(imagecolumn>0){
						int row=(imagenumber/imagecolumn);
						image_container_height=row*imageheight+5+row*5;
						height=height-image_container_height;
					}
					setAttr("report_type", imagenumber);
				}
				setAttr("image_container_height", image_container_height);
				
				
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
				if(PropKit.use("system.properties").getInt("enable_plaza_closeexam").intValue()==1){
					setAttr("plaza_closeexam", PropKit.use("system.properties").getInt("enable_plaza_callup").intValue()==1?"href=\""+CallupKit.callUpPlaza_closeExamCmd(user)+"\"":"");
				}
				setAttr("reporttitle", PropKit.use("system.properties").get("reporttitle"));
			}

			if(height>285) {
				setAttr("desc_h", height*0.62);
				setAttr("res_h", height*0.34);
			}
			else {
				setAttr("desc_h", 170);
				setAttr("res_h", 110);
			}
			setAttr("projecturl", getRequest().getScheme()+"://"+IPKit.getServerIP(getRequest())+":"+getRequest().getServerPort()+getRequest().getContextPath());
			setAttr("sr_support",VerifyLicense.hasFunction("srreport"));
			
			setAttr("printername",Base64.encodeBase64String("????????????".getBytes("UTF-8")));
			renderJsp("/view/front/report/"+reportname+".jsp");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void toReportImages(){
		setAttr("reportid", get("reportid"));
		setAttr("orderid", get("orderid"));
		setAttr("studyid", get("studyid"));
		renderJsp("/view/front/report/images.jsp");
	}
	
	
	/**
	 * ??????????????????
	 */
	public void defaultReport() {
		try {
			Record record = new Record();
			record.set("study_id", getPara("study_id"));
			record.set("studyid", getPara("studyid"));
			record.set("username", getPara("username"));
//			Record data = reportService.openReport(record);
//			setAttr("studyid", data.get("studyid"));
//			setAttr("patientname", data.get("patientname"));
			renderJsp("/view/front/reportSR.jsp");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ????????????
	 */
	public void closeReport() {
		try { 
//			Record record = new Record();
//			record.set("studyid", getPara("studyid"));
			if (sv.closeReport(getParaToInt("reportid"))) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(1, "false"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	
	/**
	 * ????????????
	 */
	public void closeReports() {
		try {
			
			String ids=getPara("reportids");
			if(ids.endsWith(",")){
				ids=ids.substring(0, ids.length()-1);
			}

			if (sv.closeReports(ids)) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(1, "false"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}

	/**
	 * ????????????
	 */
	public void auditReport() {
		try {
			Report report = getModel(Report.class, "", true);
			User user = (User) getSession().getAttribute("user");
			String publiclabel=getPara("publiclabel");
			String privatelabel=getPara("privatelabel");
			String icd10 = get("icd10label"); 
            log.info("icd10??????" + icd10);
            String url=getRequest().getScheme()+"://"+IPKit.getServerIP(getRequest())+":"+getRequest().getServerPort()+getRequest().getContextPath();
			List<String> error=new ArrayList<>();
			sv.auditReport(report,publiclabel,privatelabel, user,getRequest().getServerPort(), icd10,get("imageids"),url,error);
			if(error.size()>0){
				renderJson(ResultUtil.fail(-2, error.get(0)));
			} else{
				// ?????????????????????????????????????????????????????????????????????
				Report rep = Report.dao.findById(report.getId());
				renderJson(ResultUtil.success(rep));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	/**
	 * ????????????????????????
	 */
	public void preAuditReport() {
		User user = (User) getSession().getAttribute("user");
		Report report = getModel(Report.class, "", true);
		Record record = new Record();
		record.set("username", user.getUsername());
		record.set("name", user.getName());
		record.set("report", report);
		record.set("reportid", getParaToInt("reportid"));
		try {
			//Report re  =reportService.preAuditReport(record);
			//Report re = Report.dao.findById(getParaToInt("reportid"));
			setAttr("success", "success");
			//setAttr("report", re);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setAttr("success", "failure");
		}
		renderJson();
	}
	
	
	/**
	 * ????????????
	 */
	public void historyReport() {
		try {
			User user = (User) getSession().getAttribute("user");
//			String paid=getPara("patientidfk");
//			String ordid=getPara("orderid");
//			//String reid=getPara("reportid");
//			Integer thisOrderid = getParaToInt("thisOrderid");
			renderJson(sv.historyReports(getInt("patientidfk"),getInt("orderid"),get("studyid"), getInt("thisOrderid"), user));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	
	/**
	 * ????????????????????????
	 */
//	public void historyReportInfo() {
//		Record record = new Record();
//		try {
//			record.set("studyid",getPara("studyid"));
//			renderJson("data", reportService.historyReportInfo(record));
//		} catch (Exception e) {
//			e.printStackTrace();
//			renderJson(ResultUtil.fail(1, e.getMessage()));
//		}
//	}

	/**

	 * ????????????
	 */
	public void rejectReport(){
		try{
			User user = (User) getSession().getAttribute("user");
			String status=sv.rejectReport(getParaToInt("id"), getParaToInt("orderid"), getPara("status"), user);
			renderJson(ResultUtil.success(status));
		}catch(Exception e){
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	

	/**
	 * ?????????????????????,????????????,??????
	 */
	public void printer() {
		try {
			Connection connection = MainConfig.c3p0Plugin.getDataSource().getConnection();
			// ??????jasper????????????JasperPrint??????
			HttpServletRequest request = getRequest();
			HttpServletResponse response = getResponse();
			ServletContext context = request.getServletContext();
			File reportFile = new File(context.getRealPath("/report/" + getPara("s") + ".jasper"));
			if (reportFile.exists() == false) {
				return;
			}
			HashMap<String, Object> parameters = new HashMap<String, Object>();// ???????????????????????????

			// ????????????????????????????????????????????????????????????????????????????????????--???????????????????????????????????????????????????sql????????????????????????
			Enumeration<?> temp = request.getParameterNames();
			while (temp.hasMoreElements()) {
				String paramName = (String) temp.nextElement();
				String paramValue = request.getParameter(paramName);
				parameters.put(paramName, paramValue);
			}
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, connection);
			JasperPrintManager.printReport(jasperPrint, false);
			// ??????
			response.setContentType("application/octet-stream");
			ServletOutputStream ouputStream = response.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(ouputStream);
			oos.writeObject(jasperPrint);// ???JasperPrint??????????????????????????????
			oos.flush();
			oos.close();
			ouputStream.close();
			connection.close();
			renderNull();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * ??????????????????????????????
	 */
	public void historyView() {
		User user=(User)getSession().getAttribute("user");
		Integer orderid=getParaToInt("orderid");
		Record report= sv.findReportInfo(orderid);
		Studyorder so=Studyorder.dao.findById(orderid);
		setAttr("mainReportid", getParaToInt("mainReportid"));
		setAttr("studyorderfk", getParaToInt("orderid"));
		
		if(report!=null){
			setAttr("report",report);
//			setAttr("template_id",report.getTemplateId());
//			setAttr("studymethod", report.getStudymethod());
//			setAttr("reporttime", report.getReporttime());
//			setAttr("studyitem", report.getStudyitem());
//			setAttr("checkdesc_txt", report.getCheckdescTxt());
//			setAttr("checkresult_txt", report.getCheckresultTxt());
//			setAttr("reportphysician_name", report.getReportphysicianName());
//			setAttr("audittime", report.getAudittime());
//			setAttr("auditphysician_name", report.getAuditphysicianName());
			
			String studyid = so.getStudyid();
			String callviapara=PropKit.use("system.properties").get("launcherviapara");
			String plaza_loaddata=CallupKit.callUpPlaza_loadDataCmd(user);
			if(studyid!=null){
				callviapara=callviapara.replace("?", studyid);
				plaza_loaddata=plaza_loaddata+" "+studyid;
			}
			
			setAttr("enable_via_callup", PropKit.use("system.properties").get("enable_via_callup"));
			setAttr("callviapara", callviapara);
			setAttr("enable_plaza_callup", PropKit.use("system.properties").get("enable_plaza_callup"));
			setAttr("plaza_loaddata", plaza_loaddata);
		}
		renderJsp("/view/front/report/report_history.jsp");
	}
	
	/**
	 *?????????????????? 
	 */
	public void goFavorites(){
		setAttr("reportId", getPara("reportId"));
		Studyorder studyorder = Studyorder.dao.findById(getParaToInt("orderid"));
		String studyitems = "?????????:"+studyorder.getStudyid()+"	????????????:"+studyorder.getStudyitems();
		setAttr("studyitems", studyitems);
		renderJsp("/view/front/worklist/favorites.jsp");
	}
	
	
	/**
	 *??????????????????
	 */
	public void goTemplateCompare(){
		renderJsp("/view/front/templateCompare.jsp");
	}
	
	/**
	 * ????????????
	 */
	public void getFavoritesNodes(){
		try{
			User user = (User) getSession().getAttribute("user");
			Integer creator = user.getId();
			JSONArray favoritesList = sv.queryFavoritesList(creator);//reportService.queryFavoritesList(creator);
			renderJson(favoritesList.toString());
		}catch(Exception e){
			 e.printStackTrace();
        
		}
	}
	
	
	
	/**
	 * ???????????????
	 */
	public void addFavoritesNode(){
		
		try{
			User user = (User) getSession().getAttribute("user");
			
			Favorites f=new Favorites();
			f.setName(getPara("name"));
			f.setParentId(getParaToInt("parent_id"));
			f.setCreator(user.getId());
			f.save();
			setAttr("success", "success");
			setAttr("data", f);
		}catch(Exception e){
			e.printStackTrace();
			setAttr("success", "failure");
		}
		renderJson();
	}
	/**
	 * ???????????????
	 */
	public void modifyFavoritesnode(){
		
		try{
			Favorites f=new Favorites();
			f.setName(getPara("name"));
			f.setId(getParaToInt("id"));
			f.update();
			setAttr("success", "success");
			setAttr("data", f);
		}catch(Exception e){
			e.printStackTrace();
			setAttr("success", "failure");
		}
		renderJson();
	}
	
	public void deleteFavoritesnode(){
		
		try{
			User user = (User) getSession().getAttribute("user");
			sv.deleteFavoritesnode(getParaToInt("id"), user.getId());
			setAttr("success", "success");
		}catch(Exception e){
			e.printStackTrace();
			setAttr("success", "failure");
		}
		renderJson();
	}
	
	public void deleteFavoritesReport(){
		try{
			sv.deleteFavoritesReport(getParaToInt("id"));
			setAttr("success", "success");
		}catch(Exception e){
			e.printStackTrace();
			setAttr("success", "failure");
		}
		renderJson();
	}
	
	
	public void findFavoritesreport(){
		if(StrKit.notBlank(getPara("nodeid"))){
			renderJson(sv.findFavoritesreportByNodeid(getParaToInt("nodeid")));
		}
		else{
			renderNull();
		}
	}
	
	/**
	 * ????????????
	 */
	public void saveFavorites(){
		User user = (User) getSession().getAttribute("user");
		String sql = "SELECT TOP 1 * FROM favoritesreport WHERE report_id = ? AND favorites_id = ? AND creator = ?";
		try{
			Favoritesreport fr = Favoritesreport.dao.findFirst(sql,getParaToInt("reportId"),getParaToInt("favoritesId"),user.getId());
			if(fr==null) {
				fr = new Favoritesreport();
				fr.set("report_id", getParaToInt("reportId"));
				fr.set("favorites_id", getParaToInt("favoritesId"));
				fr.set("creator", user.getId());
				fr.set("report_desc", getPara("reportDesc"));
				fr.save();
			}else {
				fr.set("report_desc", getPara("reportDesc"));
				fr.update();
			}
			setAttr("success", "success");
		}catch(Exception e){
			e.printStackTrace();
			setAttr("success", "failure");
		}
		renderJson();
	}
	
	/**
	 * ?????????????????????????????????
	 */
	public void checkNode(){
		Record record = new Record();
		record.set("id", getParaToInt("id"));
		try{
//			Integer flag=reportService.checkNode(record);
//			setAttr("success", "success");
//			setAttr("flag", flag);
		}catch(Exception e){
			e.printStackTrace();
			setAttr("success", "failure");
		}
		renderJson();
	}
	
	/**
	 * ????????????
	 */
	public void deleteNode(){
		Record record = new Record();
		record.set("id", getParaToInt("id"));
		try{
			//reportService.deleteNode(record);
			setAttr("success", "success");
		}catch(Exception e){
			e.printStackTrace();
			setAttr("success", "failure");
		}
		renderJson();
	}
	
	/**
	 * ????????????????????????????????????????????????
	 */
	public void checkUserNode(){
		Record record = new Record();
		User user = (User) getSession().getAttribute("user");
		record.set("creator", user.getId());
		try{
//			Integer flag = reportService.checkUserNode(record);
//			setAttr("success", "success");
//			setAttr("flag", flag);
		}catch(Exception e){
			e.printStackTrace();
			setAttr("success", "failure");
		}
		renderJson();
	}
	
	/**
	 * ??????????????????
	 */
	public void goMyFavorites(){
		renderJsp("/view/front/worklist/myfavorites.jsp");
	}
	
	/**
	 * ??????????????????
	 */
	public void getReportName(){
		Record record = new Record();
		User user = (User) getSession().getAttribute("user");
		record.set("creator", user.getId());
		record.set("favoritesId", getParaToInt("favoritesId"));
		record.set("name", getPara("name"));
		try {
//			JSONArray reportInfo = reportService.queryFavoritesReport(record);
//			renderJson(reportInfo.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ??????????????????????????????????????????
	 */
	public void checkReportInfo(){
		Record record = new Record();
		User user = (User) getSession().getAttribute("user");
		record.set("creator", user.getId());
		record.set("favoritesId", getParaToInt("favoritesId"));
		try {
//			Integer existFlag  = reportService.checkReportInfo(record);
//			setAttr("success", "success");
//			setAttr("existFlag", existFlag);
		} catch (Exception e) {
			e.printStackTrace();
			setAttr("success", "failure");
		}
		renderJson();
	}
	
	/**
	 * ??????????????????
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@SuppressWarnings("deprecation")
	public void exportReport() throws SQLException, IOException{
		
		HSSFWorkbook wb = new HSSFWorkbook();
        //???????????????????????????
        HSSFSheet sheet = wb.createSheet("?????????");
        //???????????????????????????
        sheet.setDefaultColumnWidth((short)15);
        // ??????????????????  
        HSSFCellStyle style = wb.createCellStyle();
        //??????????????????????????????????????????
        HSSFRow row = sheet.createRow(0);
        //??????????????????
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //???????????????????????????????????????
        HSSFCell cell = row.createCell((short) 0);
        String [] title = {"??????","???????????????","??? ??? ???","????????????","??? ??? ???","?????????","????????????","??? ???","??? ???","??? ???","????????????","??? ???","????????????","??????","??????","????????????","????????????","????????????","????????????"};
        for(int i=0;i<title.length;i++){
        	cell=row.createCell(i);  
            cell.setCellValue(title[i]);  
            cell.setCellStyle(style);  
        }
     
       //??????????????????
       Record record = new Record();
       record.set("repordIds", getPara("repordIds"));
       record.set("favoritesId", getParaToInt("favoritesId"));
       List<Record> list;
       List<Record> reportInfo = new ArrayList<Record>();	//????????????????????????
       List<Record> reporIds = new ArrayList<Record>();		//???????????????????????????id
       try {
//		   	list = reportService.queryFavoritesReportInfo(record);
//		   	for(int k=0;k<list.size();k++){
//		   		if(list.get(k).getInt("templet_id")==0){
//		   			reportInfo.add(list.get(k));
//		   		}else{
//		   			reporIds.add(list.get(k));
//		   		}
//		   	}
		    //???????????????????????????
		   	for (short i = 0; i <reportInfo.size(); i++) {
		   		row = sheet.createRow(i + 1);
		        row.createCell(0).setCellValue(i+1);
		        row.createCell(1).setCellValue(reportInfo.get(i).getStr("folder"));
		        row.createCell(2).setCellValue(reportInfo.get(i).getStr("studyid"));
		        row.createCell(3).setCellValue(reportInfo.get(i).getStr("patientid"));
		        row.createCell(4).setCellValue(reportInfo.get(i).getStr("outno"));
		        row.createCell(5).setCellValue(reportInfo.get(i).getStr("inno"));
		        row.createCell(6).setCellValue(reportInfo.get(i).getStr("patientname"));
		        String sex="";
		        if(reportInfo.get(i).getStr("sex").equals("M")){
		        	sex="???";
		        }
		        if(reportInfo.get(i).getStr("sex").equals("F")){
		        	sex="???";
		        }
		        row.createCell(7).setCellValue(sex);
		        row.createCell(8).setCellValue(reportInfo.get(i).getInt("age"));
		        row.createCell(9).setCellValue(reportInfo.get(i).getStr("wardno"));
		        row.createCell(10).setCellValue(reportInfo.get(i).getStr("studyitems"));
		        row.createCell(11).setCellValue(reportInfo.get(i).getStr("bedno"));
		        row.createCell(12).setCellValue(reportInfo.get(i).getStr("studymethod"));
		        row.createCell(13).setCellValue(reportInfo.get(i).getStr("checkdesc_txt"));
		        row.createCell(14).setCellValue(reportInfo.get(i).getStr("checkresult_txt"));
		        row.createCell(15).setCellValue(reportInfo.get(i).getStr("reportphysician_name"));
		        row.createCell(16).setCellValue(reportInfo.get(i).getStr("auditphysician_name"));
		        SimpleDateFormat formatter; 
		        formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss"); 
		        String reporttime = "";
		        String audittime = "";
		        if(reportInfo.get(i).getDate("reporttime")!=null){
		            Date time = reportInfo.get(i).getDate("reporttime");
		            reporttime = formatter.format(time); 
		        }
		        if(reportInfo.get(i).getDate("audittime")!=null){
		        	Date date = reportInfo.get(i).getDate("audittime");
		        	audittime = formatter.format(date);
		        }
		        row.createCell(17).setCellValue(reporttime);
		        row.createCell(18).setCellValue(audittime);
	        }
		   	if(reporIds.size()>0){
		   		int count=0;
		   		int sort=1;	//???????????????
		   		if(reportInfo.size()>0){
		   			count=count+reportInfo.size()+1;
		   			sort=reportInfo.size()+1;
		   		}
		   		
		   		for(short z=0;z<reporIds.size();z++){
//		   			List<Record> structtempletreportInfo = reportService.querystructtempletreportInfo(reporIds.get(z));
//		   			List<Record> structtempletreportTitle = reportService.querystructtempletreportTitle(reporIds.get(z));
//		   		    row = sheet.createRow(count);
//		   			cell=row.createCell(0);  
//	   	            cell.setCellValue("??????");  
//	   	            cell.setCellStyle(style);  
//	   	            cell=row.createCell(1);  
//	   	            cell.setCellValue("???????????????");  
//	   	            cell.setCellStyle(style);  
//		   			for(short j=0;j<structtempletreportTitle.size();j++){
//		   				int b=2+j;
//		   				cell=row.createCell(b);  
//		   	            cell.setCellValue(structtempletreportTitle.get(j).getStr("name"));  
//		   	            cell.setCellStyle(style);  
//			   		}
//	   			    row=sheet.createRow(count+1);
//	   			    row.createCell(0).setCellValue(sort);
//	        	    row.createCell(1).setCellValue(structtempletreportInfo.get(0).getStr("folder"));
//		        	for(short k=0;k<structtempletreportInfo.size();k++){
//		        	    row.createCell(k+2).setCellValue(structtempletreportInfo.get(k).getStr("field_value"));
//		   				
//		   			}
		        	sort=sort+1;
			   		count=count+2;
		   		}
		   		
		   	}
	        String fileName = "Excel-" + String.valueOf(System.currentTimeMillis()).substring(4, 13) + ".xls";
	        String headStr = "attachment; filename=\"" + fileName + "\"";
	    	HttpServletResponse response = getResponse();
	        response.setContentType("APPLICATION/OCTET-STREAM");
	        response.setHeader("Content-Disposition", headStr);
	        OutputStream out = response.getOutputStream();
	        wb.write(out);
			renderNull(); 
	   } catch (Exception e) {
		 e.printStackTrace();
	    }
    }
	
	
	
	/**
	 * ????????????????????????????????????????????????
	 */
	public void checkReportTemplate() {
		try{
//			Integer flag = reportService.checkReportTemplate(getPara("studyid"));
//			setAttr("success", "success");
//			setAttr("reportFlag", flag);
		}catch(Exception e){
			setAttr("success", "failure");
		}
		renderJson();
	}
	
	/**
	 * ?????????????????????????????????
	 */
	public void updatefavrivoteName(){
		String favoritesId = getPara("favoritesId");
		String reportId = getPara("reportId");
		String desc = getPara("desc");
		try{
//			boolean flag = reportService.updatefavrivoteName(favoritesId, reportId, desc,getSession(false));
//			if(flag) {
//				renderJson(ResultUtil.success());
//			}else {
//				renderJson(ResultUtil.fail(-1,"??????????????????????????????"));
//			}
		}catch(Exception e){
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1,e.getMessage()));
		}
	}
	
//	/**
//	 * ??????????????????????????????
//	 */
//	public void hasReport() {
//		try {
//			HashMap<String, String> temp=sv.hasReport(getParaToInt("reportid"),getPara("printtempname"),getPara("issr"), getRequest());
//			renderJson(ResultUtil.success(temp.get("printtempname")));
//		} catch (Exception e) {
//			e.printStackTrace();
//			renderJson(ResultUtil.fail(-1, e.getMessage()));
//		}
//	}
	
	/**
	 * ????????????
	 */
	public void cancelBlock(){
		Integer reportid=getParaToInt("reportid");
		try{
			Report report=Report.dao.findById(reportid);
			if(report.getIslocking()!=1) {
				renderJson(ResultUtil.fail(1,"?????????????????????????????????????????????"));
			}else {
				if(sv.cancleBlock(reportid)) {
					renderJson(ResultUtil.success());
				}else {
					renderJson(ResultUtil.fail("????????????????????????"));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * ????????????
	 */
	public void cancelAudiReport(){
		try{
			Report report = Report.dao.findById(getParaToInt("reportid")); 
			if(!ReportStatus.FinalResults.equals(report.getReportstatus())) {
				renderJson(ResultUtil.fail(1, "?????????????????????????????????????????????"));
			}else {
				User user = (User)getSession().getAttribute("user");
				boolean preAudit = PropKit.use("system.properties").getBoolean("PreAudit", true);
				Record ret=new Record();
				sv.cancelAudiReport(getParaToInt("reportid"),user,preAudit,ret);
				renderJson(ResultUtil.success(ret));
			}
		}catch(Exception e){
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * ??????????????????????????????????????????????????????????????????
	 */
	public void afterCancelAudiReport() {
		try{
			renderJson(ResultUtil.success(sv.afterCancelAudiReport(getInt("reportid"), get("checkresult"), get("checkdesc"))));
		}catch(Exception e){
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * ????????????????????????
	 */
    public void getRejectReportCount(){
    	User user=(User)getSession().getAttribute("user");
    	renderJson(ResultUtil.success(sv.getRejectReportCount(user.getId())));
    }
	
	
	public void getFindings(){
		String studyid=getPara("studyid");
		
		SAXReader reader = new SAXReader();
		
		try {
			Document doc=reader.read(new File("D:\\cda.xml"));
			Element root = doc.getRootElement();
			root.element("component");
			
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void goToRemarks() {
		setAttr("studyid", getPara("studyid"));
		renderJsp("/view/front/report/remarks.jsp");
	}
	
	public void goToEditremark() {
		Studyorder so =Studyorder.dao.findById(getParaToInt("orderid"));
		Record re=new Record();
		
		re.set("patientidfk",so.getPatientidfk());
		re.set("admissionidfk",so.getAdmissionidfk());
		re.set("orderid",so.getId());
		re.set("reportid", getParaToInt("reportid"));
		setAttr("record",re);
		setAttr("studyid", so.getStudyid());
//		renderJsp("/view/front/report/edit_remark.jsp");
		setAttr("remarks",sv.getRemarks(so,getParaToInt("orderid")));
		render("/view/front/report/remarks.html");
		
	}
	
	public void getRemarks() {
		renderJson(sv.getRemarks(null,getParaToInt("orderid")));
	}
	
	public void saveRemark() {
		Reportremark remark = getModel(Reportremark.class, "", true);
		
		try {
			User user = (User) getSession().getAttribute("user");
			if(sv.saveReportRemark(remark,user)) {
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
	
	public void delReportRemark() {
		try {
			
			if(sv.delReportRemark(getParaToInt("id"))) {
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
	
	public void getReportLabel(){
		User user = (User) getSession().getAttribute("user");
		Integer reportid=StrKit.notBlank(getPara("reportid"))?getParaToInt("reportid"):null;
		renderJson(sv.getReportLabel(reportid,getPara("ispublic"), user.getId()));
	}
	
	public void gotoManageLabel(){
		setAttr("ctx", getRequest().getContextPath());
		render("/view/front/report/manage_label.html");
	}
	
	public void getLabels(){
		User user = (User) getSession().getAttribute("user");
		renderJson(sv.getLabels(user.getId()));
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
			System.out.println("id:"+getParaToInt("id"));
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
	
	//??????????????????????????????
	public void isStructreport() {
		try {
			Report report = Report.dao.findFirst("SELECT top 1 * FROM report WHERE studyorderfk= ?", getParaToInt("orderid"));
			if(report.getTemplateId()>0) {
				renderJson(ResultUtil.success(true));
			}else {
				renderJson(ResultUtil.success(false));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	//????????????????????????????????????
	public void doReportCreating() {
		try {
			User user = (User) getSession().getAttribute("user");
			renderJson(ResultUtil.success(sv.doReportCreating(getParaToInt("orderid"),getPara("studyid"),getPara("other"),user)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
//*************************????????????***********************************************
	//????????????id????????????????????????
	public void getAllReporttrace() {
		String sqlPara = "SELECT reportrace.*,"
				+ " (SELECT name FROM users WHERE users.id = reportrace.operator) AS operatorname,"
				+ " (SELECT name_zh FROM syscode  WHERE syscode.code = (CASE WHEN reportrace.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE reportrace.reportstatus END) AND syscode.type = '0007') AS reportstatusdisplay"
				+ " FROM reportrace WHERE id = ? AND template_id = 0 ORDER BY modifytime";
		List<Reportrace> reporttrace = Reportrace.dao.find(sqlPara, getParaToInt("reportid"));
		renderJson(reporttrace);
	}
	
	//????????????????????????
	public void goToComparetrace() {
		setAttr("reportid", getParaToInt("reportid"));
		renderJsp("/view/front/report/comparetrace.jsp");
	}
	
	//????????????????????????
	public void reportComparing() {
		HashMap<String,Object> map = new HashMap<String,Object>();
		String[] checkdesc = TextCompareKit.compare(getPara("before_desc"), getPara("after_desc"));
		String[] checkresult = TextCompareKit.compare(getPara("before_result"), getPara("after_result"));
		map.put("checkdesc", checkdesc);
		map.put("checkresult", checkresult);
		
		renderJson(map);
	}
	//*************************????????????***********************************************
	
	/**
	 *  ??????icd10????????? checkboxFindIcd10
	 */
	public void getIcd10() {
        renderJson(sv.getIcd10(getInt("reportid")));
    }
	
	/**
     *  ????????? ???????????????????????????icd10?????????
     */
    public void checkboxFindIcd10() {
        renderJson(sv.checkboxFindIcd10(get("searchContent")));
    }
	
	/**
	 *  ?????????icd10????????????
	 */
	public void goToSearchIcd10() {
	    setAttr("reportid", get("reportid"));
        renderJsp("/view/front/report/search_icd10.jsp");
    }
	
	/**
	 *  ??????icd10???????????????
	 */
	public void getIcd10Tree() {
	    List<Record> records = sv.getIcd10Tree(1);
        renderJson(records);
    }
	
	/**
	 * ??????????????????dic10
	 */
	public void findDic10() {
	    renderJson(sv.findDic10(get("icdCode"), get("icdName")));
    }
	
	/**
	 *  ?????????????????????dic10
	 */
	public void findDic10ByNode() {
	    List<Record> list = sv.findDic10ByNode(getInt("icdIndex"));
	    log.info("?????????????????????dic10????????????" + list.size());
	    renderJson(list);
    }

    public void getAllPrintReportInfo() {
		try {
			HashMap<String,Object> map = new HashMap<String,Object>();
			String[] reportid = getPara("reportid").split(",");
			String[] alltemplatename = new String[reportid.length];
			int[] allissr = new int[reportid.length];
			System.out.println(reportid.length);
			for(int i = 0; i < reportid.length; i++){
				Report report = Report.dao.findById(Integer.parseInt(reportid[i]));
				String templatename = "????????????";
				int issr = 0;
				if(report.getTemplateId() !=null &&report.getTemplateId() > 0) {
					SRTemplate srt=SRTemplate.dao.findById(report.getTemplateId());
					templatename = (srt==null?templatename:srt.getName());
					issr = 1;
				}
				alltemplatename[i] = Base64.encodeBase64String(templatename.getBytes("UTF-8"));
				allissr[i] = issr;
			}
			
			map.put("templatename", alltemplatename);
			map.put("projecturl", getRequest().getScheme()+"://"+IPKit.getServerIP(getRequest())+":"+getRequest().getServerPort()+getRequest().getContextPath() );
			map.put("reportid", reportid);
			map.put("issr", allissr);
			renderJson(ResultUtil.success(map));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1,e.getMessage()));
		}
	}
	
	public void getPrintReportInfo() {
		try {
			Report report = Report.dao.findById(getInt("reportid"));
			if(!ReportStatus.FinalResults.equals(report.getReportstatus())) {
				renderJson(ResultUtil.fail(1,"??????????????????????????????????????????"));
			} else {
				User user = (User) getSession().getAttribute("user");
				HashMap<String,Object> map = new HashMap<String,Object>();
				String templatename = "????????????";
				int issr = 0;
				if(report.getTemplateId() !=null &&report.getTemplateId() > 0) {
					SRTemplate srt=SRTemplate.dao.findById(report.getTemplateId());
					templatename = (srt==null?templatename:srt.getName());
					issr = 1;
				}
				map.put("userid", user.getId());
				map.put("templatename", Base64.encodeBase64String(templatename.getBytes("UTF-8")) );
				map.put("projecturl", getRequest().getScheme()+"://"+IPKit.getServerIP(getRequest())+":"+getRequest().getServerPort()+getRequest().getContextPath() );
				map.put("reportid", report.getId());
				map.put("issr", issr);
				renderJson(ResultUtil.success(map));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1,e.getMessage()));
		}
	}
	
	/**
	 * ?????????????????????
	 */
    public void goToRetrialview() {
        List<Retrialviewlist> list = new ArrayList<Retrialviewlist>();
        if(getParaToInt("reportid") != null) {
            String sql = "SELECT *,(SELECT name FROM users WHERE users.id = retrialviewlist.creator) as creator_name FROM retrialviewlist WHERE reportid = ?";
            list = Retrialviewlist.dao.find(sql, getParaToInt("reportid"));
        }
        setAttr("reportid", getParaToInt("reportid"));
        setAttr("list", list);
        render("/view/front/report/retrialview.html");
    }
    
    /**
     *  ??????????????????
     */
    public void delRetrialview() {
        try {
            if (Retrialviewlist.dao.deleteById(getParaToInt("id"))) {
                renderJson(ResultUtil.success());
            } else {
                renderJson(ResultUtil.fail(-1, "error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(ResultUtil.fail(1, e.getMessage()));
        }
    }
    
    /**
     *  ??????????????????
     */
    public void saveRetrialview() {
        Retrialviewlist retrialviewlist = getModel(Retrialviewlist.class, "", true);
        try {
            User user = (User) getSession().getAttribute("user");
            retrialviewlist.setCreator(user.getId());
            if (retrialviewlist.remove("id").save()) {
                renderJson(ResultUtil.success());
            } else {
                renderJson(ResultUtil.fail(-1, "error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(ResultUtil.fail(1, e.getMessage()));
        }
    }
    
    /**
     *  ???????????????????????????
     */
    public void getRetrialviewlist() {
        List<Retrialviewlist> list = new ArrayList<Retrialviewlist>();
        if (getParaToInt("reportid") != null) {
            String sql = "SELECT *,(SELECT name FROM users WHERE users.id = retrialviewlist.creator) as creator_name FROM retrialviewlist WHERE reportid = ?";
            list = Retrialviewlist.dao.find(sql, getParaToInt("reportid"));
        }
        renderJson(list);
    }
   

    /**
	 * ????????????????????????
	 */
	public void getReportCorrect(){
		Record r=new Record()
				.set("sex", get("sex", ""))
				.set("examitem", get("examitem", ""))
				.set("modality_type", get("modality_type", ""))
				.set("desc", get("desc", ""))
				.set("result", get("result", ""));
		String result = sv.getReportCorrect(r);
		if("".equals(result)) {
			renderJson(ResultUtil.success());
		}else {
			renderJson(ResultUtil.fail(-1, result));
		}
	}
	
	
    //???????????????????????????
    public void getRejectOpinionlist() {
        List<Rejectopinion> list = new ArrayList<Rejectopinion>();
        if (getParaToInt("reportid") != null) {
            String sql = "SELECT *,(SELECT name FROM users WHERE users.id = rejectopinion.creator) as creator_name FROM rejectopinion WHERE reportid = ?";
            list = Rejectopinion.dao.find(sql, getParaToInt("reportid"));
        }
        renderJson(list);
    }
    
    //??????????????????
    public void goToRejectOpinion() {
        List<Rejectopinion> list = new ArrayList<Rejectopinion>();
        if(getParaToInt("reportid") != null) {
            String sql = "SELECT *,(SELECT name FROM users WHERE users.id = rejectopinion.creator) as creator_name FROM rejectopinion WHERE reportid = ?";
            list = Rejectopinion.dao.find(sql, getParaToInt("reportid"));
        }
        Subject subject = SecurityUtils.getSubject();
        setAttr("reportid", getParaToInt("reportid"));
        setAttr("list", list);
        setAttr("reject_report", subject.isPermitted("reject_report"));
        render("/view/front/report/RejectOpinion.html");
    }
    
    //??????????????????
    public void delRejectOpinion() {
        try {
            if (Rejectopinion.dao.deleteById(getParaToInt("id"))) {
                renderJson(ResultUtil.success());
            } else {
                renderJson(ResultUtil.fail(-1, "error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(ResultUtil.fail(1, e.getMessage()));
        }
    }
    
    //??????????????????
    public void saveRejectOpinion() {
    	Rejectopinion Rejectopinion = getModel(Rejectopinion.class, "", true);
        try {
            User user = (User) getSession().getAttribute("user");
            Rejectopinion.setCreator(user.getId());
            if (Rejectopinion.remove("id").save()) {
                renderJson(ResultUtil.success());
            } else {
                renderJson(ResultUtil.fail(-1, "error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(ResultUtil.fail(1, e.getMessage()));
        }
    }
    
    public void saveReportLabel() {
   	 try {
   		 User user = (User) getSession().getAttribute("user");
            if (sv.saveReportLabel(getParaToInt("reportid"), get("publiclabel"), get("privatelabel"), get("icd10label"), user.getId())) {
                renderJson(ResultUtil.success());
            } else {
                renderJson(ResultUtil.fail(-1, "error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(ResultUtil.fail(1, e.getMessage()));
        }
   }
    
    public void getPersonalPhrase() {
    	try {
    		User user=(User)getSession().getAttribute("user");
			renderJson(sv.getPersonalPhrase(getParaToInt("id"),user));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1,e.getMessage()));
		}
    }
    
    public void goToAddPhrasenode() {
    	Integer PhrasenodeId=getParaToInt("PhrasenodeId");
    	if(PhrasenodeId!=null) {
    		PhraseNode phrasenode=PhraseNode.dao.findById(PhrasenodeId);
        	setAttr("phrasenode", phrasenode);
    	}
    	renderJsp("/view/front/report/editPhraseNode.jsp");
    }
    
    public void goToAddPhraseContent() {
    	setAttr("reportid", getParaToInt("reportid"));
    	Integer PhraseContentId=getParaToInt("PhraseContentId");
    	if(PhraseContentId!=null) {
    		Phrase phrase=Phrase.dao.findById(PhraseContentId);
        	setAttr("phrase",phrase);
    	}
    	renderJsp("/view/front/report/editPhrase.jsp");
    }
    
    public void addPhrasenode() {
    	try {
    		User user=(User)getSession().getAttribute("user");
    		PhraseNode phraseNode = getModel(PhraseNode.class, "", true);
			renderJson(ResultUtil.success(sv.addPhrasenode(phraseNode, user)));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1,e.getMessage()));
		}
    }
    
    public void savePhrase() {
    	try {
    		User user=(User)getSession().getAttribute("user");
    		Phrase phrase = getModel(Phrase.class, "", true);
			renderJson(ResultUtil.success(sv.savePhrase(phrase, user)));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1,e.getMessage()));
		}
    }
    
    public void previewPhrase() {
    	Phrase phrase = Phrase.dao.findById(getParaToInt("id"));
    	setAttr("reportid", getParaToInt("reportid"));
    	setAttr("phrase", phrase);
    	renderJsp("/view/front/report/previewPhrase.jsp");
    }
    
    public void searchPhrase() {
    	try {
    		User user=(User)getSession().getAttribute("user");
			renderJson(sv.searchPhrase(getPara("searchContent"), user));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1,e.getMessage()));
		}
    }
    
    public void goPrintImages(){
    	setAttr("reportid", get("reportid"));
    	setAttr("orderid", get("orderid"));
    	setAttr("studyid", get("studyid"));
    	Report report=Report.dao.findById(get("reportid"));
    	setAttr("report",report);
    	Studyorder studyorder=Studyorder.dao.findById(report.getStudyorderfk());
		if(studyorder!=null) {
			setAttr("modalityType", studyorder.getModalityType());
			setAttr("patientid", studyorder.getPatientid());
		}
    	setAttr("projecturl", getRequest().getScheme()+"://"+IPKit.getServerIP(getRequest())+":"+getRequest().getServerPort()+getRequest().getContextPath());
    	render("/view/front/report/printImages.html");
    }
    
    public void getReportImages(){
    	Integer top=null;
    	Integer reporttype=getInt("report_type");
    	if(reporttype!=null) {
    		ReportType type= ReportType.getReportType(reporttype);
    		top=type.getName();
    	}
    	renderJson(sv.getReportImages(get("studyid"),top));
    }
    
    public void delReportImages(){
    	try {
    		if(sv.delReportImages(get("ids"))){
    			renderJson(ResultUtil.success());
    		} else{
    			renderJson(ResultUtil.fail(""));
    		}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1,e.getMessage()));
		}
    }
    
    public void savePrintImagesHtml(){
    	try {
    		if(sv.savePrintImagesHtml(getInt("reportid"),get("html"),get("orders"))){
    			renderJson(ResultUtil.success());
    		} else{
    			renderJson(ResultUtil.fail(""));
    		}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1,e.getMessage()));
		}
    }
    /**
	 * ????????????????????????????????????????????????????????????????????????????????????
	 */
	public void autoSaveReport(){
		try {
			Report report = getModel(Report.class, "", true);
			sv.autoSaveReport(report);
			renderJson(ResultUtil.success(report));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/*
	 *  ??????????????????
	 */
	public void getImgDesc() {
		try {
			renderJson(ResultUtil.success(sv.getImgDesc(getInt("id"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void saveImgDesc() {
		try {
			if(sv.saveImgDesc(getInt("id"),get("desc"))) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1, "???????????????"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void addImage() {
    	try {
    		if(sv.copyImageToCurrentStudy(get("ids"),get("studyid"))){
    			renderJson(ResultUtil.success());
    		} else{
    			renderJson(ResultUtil.fail(""));
    		}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1,e.getMessage()));
		}
	}
	public void getReportImagesHistory() {
		renderJson(sv.getReportImagesHistory(get("studyid"),get("patientid")));
	}
	
	public void findReportByid() {
		Report report=Report.dao.findById(getInt("reportid"));
		renderJson(report);
	}

}
