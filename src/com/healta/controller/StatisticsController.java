package com.healta.controller;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.healta.config.MainConfig;
import com.healta.constant.DbConfigName;
import com.healta.constant.SessionKey;
import com.healta.constant.SysTheme;
import com.healta.model.Statisticalclassify;
import com.healta.model.Statisticalcustomconditions;
import com.healta.model.Statisticalreport;
import com.healta.model.User;
import com.healta.model.UserDac;
import com.healta.model.UserProfiles;
import com.healta.service.StatisticsService;
import com.healta.util.MyConverterProperties;
import com.healta.util.ParaKit;
import com.healta.util.ResultUtil;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.upload.UploadFile;

import net.sf.jasperreports.engine.JasperRunManager;

public class StatisticsController extends Controller {

	private final static Logger log = Logger.getLogger(StatisticsController.class);
	private final static StatisticsService sv=new StatisticsService();

	public void index(){
		User user = (User) getSessionAttr(SessionKey.USER);
		setAttr("username", user.getUsername());
//		setAttr("rolename", "管理员");
		setAttr("name", user.getName());
		setAttr("user_id", user.getId());
		UserDac ud=(UserDac)getSessionAttr(SessionKey.USER_DAC);
		setAttr("hasUserdac", ud!=null&&StrKit.notBlank(ud.getInstitutionIds())?true:false);
		UserProfiles profiles=(UserProfiles)getSessionAttr(SessionKey.USER_PROFILES);
		String chart_theme="";
		if(profiles!=null) {
			if(StrKit.equals(SysTheme.VIA, profiles.getTheme())) {
				chart_theme="dark";
			}
		}
		setAttr("chart_theme", chart_theme);
		renderJsp("/view/statistics/statistics.jsp");
	}

	public void findStatistics(){
		try{
			renderJson(sv.findStatistics(getPara("value")));

		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void findStatistics_Json(){
		try{
			renderJson(sv.findStatistics(getPara("value")));

		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void gotoEditStatistics(){

		Integer id=getParaToInt("id");
		if(id!=null){
			Statisticalreport sr=Statisticalreport.dao.findById(id);
			String con=sr.getConditions();
			if(StrKit.notBlank(con)){
				if(con.indexOf("datetime")>=0){
					setAttr("enable_datetime", "true");
					setAttr("datetype", StringUtils.substringBetween(con, "datetime(", ")"));
				}

				if(con.indexOf("regoperator")>=0){
					setAttr("enable_regoperator", "true");
					setAttr("regoperator", StringUtils.substringBetween(con, "regoperator(", ")"));
				}

				if(con.indexOf("technologists")>=0){
					setAttr("enable_technologists", "true");
					setAttr("technologists", StringUtils.substringBetween(con, "technologists(", ")"));
				}
				if(con.indexOf("reportphysician")>=0){
					setAttr("enable_reportphysician", "true");
					setAttr("reportphysician", StringUtils.substringBetween(con, "reportphysician(", ")"));
				}
				if(con.indexOf("auditphysician")>=0){
					setAttr("enable_auditphysician", "true");
					setAttr("auditphysician", StringUtils.substringBetween(con, "auditphysician(", ")"));
				}
			}
			setAttr("sr", sr);
		}

		renderJsp("/view/admin/statistics/editStatistics.jsp");
	}

	public void gotoManageClassify(){
		renderJsp("/view/admin/statistics/manageClassify.jsp");
	}

	public void toDashBoard() {
		setAttr("userdac", getSessionAttr(SessionKey.USER_DAC));
		Subject subject = SecurityUtils.getSubject();
		setAttr("revenueDashboard", subject.isPermitted("revenueDashboard"));
		Integer usercount=Db.use(DbConfigName.MSSQL).queryInt("select count(*) from users where active=1 and deleted=0");
		setAttr("usercount", usercount!=null?usercount:3);
		render("/view/statistics/dashboard.html");
	}


	public void findStatisticalcustomconditions(){
		try{
			renderJson(sv.findStatisticalcustomconditions(getParaToInt("srfk")));

		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void saveStatistics(){
		try{

			UploadFile jrxmlfile=getFile("jrxml","/jasper/tmp");
			UploadFile jasperfile=getFile("jasper","/jasper/tmp");
			Statisticalreport st=getModel(Statisticalreport.class, "", true);
			String[] ccs=getParaValues("custom_conditions");
			ArrayList<Statisticalcustomconditions> cclist=new ArrayList<Statisticalcustomconditions>();
			if(ccs!=null){
				for(String cc:ccs){
					log.info(cc);
					cclist.addAll(JSON.parseObject(cc,new TypeReference<ArrayList<Statisticalcustomconditions>>() {}));
				}
			}
			log.info(cclist.size());

			String statisticsPath = getRequest().getServletContext().getRealPath("upload/statistics");

			String uuid=UUID.randomUUID().toString();
			if(jrxmlfile!=null&&jrxmlfile.getFile().exists()){
				File file=jrxmlfile.getFile();
				String newfilename=uuid+".jrxml";
//				file.renameTo(new File(file.getParentFile().getParent()+System.getProperty("file.separator")+newfilename));
//				FileUtils.moveFile(file, new File(file.getParentFile().getParent()+System.getProperty("file.separator")+newfilename));
				System.out.println(statisticsPath+System.getProperty("file.separator")+newfilename);
				FileUtils.moveFile(file, new File(statisticsPath+System.getProperty("file.separator")+newfilename));
				st.setJrxmlFilename(newfilename);
			}

			if(jasperfile!=null&&jasperfile.getFile().exists()){
				File file=jasperfile.getFile();
				String newfilename=uuid+".jasper";
				//FileUtils.moveFile(file, new File(file.getParentFile().getParent()+System.getProperty("file.separator")+newfilename));
				FileUtils.moveFile(file, new File(statisticsPath+System.getProperty("file.separator")+newfilename));
				st.setJasperFilename(newfilename);
			}

			if(sv.saveStatistics(st, cclist, statisticsPath)){
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

	public void deleteStatistics(){
		try{
			if(Statisticalreport.dao.deleteById(getParaToInt("id"))){
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

	public void showStatisticsConditions() {
		Statisticalreport sr=Statisticalreport.dao.findById(getParaToInt("id"));
		if(sr!=null&&StrKit.notBlank(sr.getConditions())) {
			setAttr("list", sr.getConditions().split(";"));
			setAttr("id", sr.getId());
			setAttr("cclist", sv.findStatisticalcustomconditions(getParaToInt("id")));
			setAttr("notsql", StrKit.isBlank(sr.getSql())?true:false);
		}
		setAttr("ctx", getRequest().getContextPath());
		render("/view/statistics/conditions.html");
	}


	public void showStatisticalReport(){
		try {
			Statisticalreport sr=Statisticalreport.dao.findById(getParaToInt("id"));
			HttpServletRequest request = getRequest();
//			HttpServletResponse response = getResponse();
//			ServletContext context = request.getServletContext();
			HashMap<String, Object> parameters = new HashMap<String, Object>();// 给报表模板文件传参
			String where=ParaKit.generateWhere(request,parameters);
			log.info(where);
			parameters.put("where", where);
			parameters.put("IS_IGNORE_PAGINATION", true);
			if(StrKit.notBlank(sr.getSql())){
				renderHtml(sv.generationHtml(sr.getSql(), where));
			} else{
				//mysql
				//			Connection connection = MainConfig.c3p0Plugin.getDataSource().getConnection();
				//sql server
				Connection connection = MainConfig.mainDbPlugin.getDataSource().getConnection();
				// 据据jasper文件生成JasperPrint对象
				String statisticsPath = getRequest().getServletContext().getRealPath("upload/statistics");
//				File reportFile = new File(PropKit.get("base_upload_path")+System.getProperty("file.separator")+"jasper"+System.getProperty("file.separator")+sr.getJasperFilename());
				File reportFile = new File(statisticsPath + System.getProperty("file.separator") + sr.getJasperFilename());
				log.info(reportFile.getAbsolutePath());
				if (reportFile.exists() == false) {
					renderNull();
				}
				String html=JasperRunManager.runReportToHtmlFile(reportFile.getPath(), parameters, connection);
				renderHtml(FileUtils.readFileToString(new File(html),"UTF-8"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			renderHtml("<html><body>"+e.getMessage()+"</body></html>");
		}
	}
	public void showStatisticalReport_Pdf(){
		try {
			Statisticalreport sr=Statisticalreport.dao.findById(getParaToInt("id"));

			// 据据jasper文件生成JasperPrint对象
			HttpServletRequest request = getRequest();
			HttpServletResponse response = getResponse();
			//ServletContext context = request.getServletContext();
			HashMap<String, Object> parameters = new HashMap<String, Object>();// 给报表模板文件传参
			String where=ParaKit.generateWhere(request,parameters);
			log.info(where);
			parameters.put("where", where);
			log.info(parameters);
			byte[] bytes=null;
			if(StrKit.notBlank(sr.getSql())){
				String pdfname=PropKit.use("system.properties").get("tempdir")+"\\"+StrKit.getRandomUUID()+".pdf";
				PdfWriter writer= new PdfWriter(pdfname);
				HtmlConverter.convertToPdf(sv.generationHtml(sr.getSql(), where), writer, MyConverterProperties.getInstance());
				File pdffile=new File(pdfname);
				bytes=FileUtils.readFileToByteArray(pdffile);
				FileUtils.deleteQuietly(pdffile);
			} else{
				String statisticsPath = getRequest().getServletContext().getRealPath("upload/statistics");
				File reportFile = new File(statisticsPath + System.getProperty("file.separator") + sr.getJasperFilename());
				log.info(reportFile.getAbsolutePath());
				if (reportFile.exists() == false) {
					renderNull();
				}
				//mysql
				//			Connection connection = MainConfig.c3p0Plugin.getDataSource().getConnection();
				//sql server
				Connection connection = MainConfig.mainDbPlugin.getDataSource().getConnection();
				bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, connection);
				connection.close();
			}

			response.setContentType("application/pdf");
			response.setContentLength(bytes.length);
			ServletOutputStream out = response.getOutputStream();
			out.write(bytes, 0, bytes.length);
			out.flush();
			out.close();
			renderNull();

		} catch (Exception e) {
			e.printStackTrace();
			//renderNull();
			renderHtml("<html><body>"+e.getMessage()+"</body></html>");
		}
	}


	public void exportStatisticalReport(){
		try {
			File file=sv.exportStatisticalReport(getParaToInt("id"), getPara("type"), getRequest());
			if(file!=null){
				renderFile(file);
			}
			else{
				renderNull();
			}

		} catch (Exception e) {
			e.printStackTrace();
			renderHtml("<html><body>"+e.getMessage()+"</body></html>");
		}
	}



	public void addStatisticalClassify(){
		try{
			renderJson(ResultUtil.success(sv.saveStatisticalclassify(getModel(Statisticalclassify.class, "", true))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void deleteClassify(){
		try{
			if(sv.deleteClassify(getParaToInt("id"))) {
				renderJson(ResultUtil.success());
			}
			else {
				renderJson(ResultUtil.fail(-1, "unknown error"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void getStatisticalClassify(){
		try{
			renderJson(Statisticalclassify.dao.find("select * from statisticalclassify"));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void totalNumOfSchOrReg() {
		try{
			renderJson(ResultUtil.success(sv.totalNumOfSchOrReg(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void totalNumOfExam() {
		try{
			renderJson(ResultUtil.success(sv.totalNumOfExam(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void totalNumOfReport() {
		try{
			renderJson(ResultUtil.success(sv.totalNumOfReport(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void eachEquipmentRevernue() {
		try{
			renderJson(ResultUtil.success(sv.eachEquipmentRevernue(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void modalityRevernue() {
		try{
			renderJson(ResultUtil.success(sv.modalityRevernue(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void patientSourceRevernue() {
		try{
			renderJson(ResultUtil.success(sv.patientSourceRevernue(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void eachEquipmentRevernue30Days() {
		try{
			renderJson(ResultUtil.success(sv.eachEquipmentRevernue30Days(get("start"), get("end"),getInt("institutionid"),get("modality"),get("patientsource"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void positiveRate() {
		try{
			renderJson(ResultUtil.success(sv.positiveRate(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void summarizeDataOfToday() {
		try{
			renderJson(ResultUtil.success(sv.summarizeDataOfToday(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void onlineTime() {
		try{
			renderJson(ResultUtil.success(sv.onlineTime(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

//	================================= 统计 报告标签 =========================================

	/**
	 *  跳报告统计标签页
	 */
	public void toReportBoard() {
		setAttr("userdac", getSessionAttr(SessionKey.USER_DAC));
//		Subject subject = SecurityUtils.getSubject();
//		setAttr("revenueDashboard", subject.isPermitted("revenueDashboard"));
//		Integer usercount=Db.use(DbConfigName.MSSQL).queryInt("select count(*) from users where active=1 and deleted=0");
//		setAttr("usercount", usercount!=null?usercount:3);
		render("/view/statistics/reportboard.html");
	}

	/**
	 *  统计当天设备类型的报告数量
	 */
	public void reportDataOfToday() {
		try{
			renderJson(ResultUtil.success(sv.reportDataOfToday(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	/**
	 *  按设备类型统计报告
	 */
	public void reportModality() {
		try{
			renderJson(ResultUtil.success(sv.reportModality(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	/**
	 *  按病人来源统计报告
	 */
	public void reportPatientsource() {
		try{
			renderJson(ResultUtil.success(sv.reportPatientsource(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	/**
	 *  各个设备类型未写的报告
	 */
	public void noresultReportModality() {
		try{
			renderJson(ResultUtil.success(sv.noresultReportModality(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	/**
	 *  各个设备类型未审核的报告
	 */
	public void noauditReportModality() {
		try{
			renderJson(ResultUtil.success(sv.noauditReportModality(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	/**
	 *  图像评级
	 */
	public void imagequality() {
		try{
			renderJson(ResultUtil.success(sv.imagequality(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	/**
	 *  报告质量
	 */
	public void reportquality() {
		try{
			renderJson(ResultUtil.success(sv.reportquality(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	/**
	 *  诊断符合
	 * @param startDate
	 * @param endDate
	 * @param institutionid
	 * @return
	 */
	public void diagnosisCoincidence() {
		try{
			renderJson(ResultUtil.success(sv.diagnosisCoincidence(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	/**
	 *  阴阳性
	 * @param startDate
	 * @param endDate
	 * @param institutionid
	 * @return
	 */
	public void posOrNeg() {
		try{
			renderJson(ResultUtil.success(sv.posOrNeg(get("start"), get("end"),getInt("institutionid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

}
