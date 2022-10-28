package com.healta.controller;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.alibaba.fastjson.JSONObject;
import com.healta.constant.PrintTemplateParameters;
import com.healta.license.VerifyLicense;
import com.healta.listener.MySessionContext;
import com.healta.model.Report;
import com.healta.model.Studyorder;
import com.healta.model.User;
import com.healta.model.UserProfiles;
import com.healta.plugin.shiro.ShiroPrincipal;
import com.healta.render.PdfRender;
import com.healta.service.OpenActionService;
import com.healta.service.PrintService;
import com.healta.util.IPKit;
import com.healta.util.PrintTemplateName;
import com.healta.util.ResultUtil;
import com.healta.util.SerializeRes;
import com.healta.util.WebSocketUtils;
import com.healta.vo.WebsocketVO;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class OpenActionController extends Controller {
	private static final Logger log = Logger.getLogger(ScheduleController.class);
	private static final OpenActionService sv = new OpenActionService();
	private static final PrintService printsv = new PrintService();
	/**
	  * 病人到检，提供给C#到检所用
	 */
	public void patientCheckIn() {
	    try {
	        Record record = sv.patientCheckIn(get("studyid"));
	        log.info("==result:" + record.getBoolean("result"));
	        log.info("==patientname:" + record.get("patientname"));
	        if(record.get("status").equals("1")) {
	            if (record.getBoolean("result")) {
	                renderJson(ResultUtil.success(record.get("patientname")));
	            } else {
	            	if(record.get("time") != null) {
	            		renderJson(ResultUtil.fail(-1, "非规定时间", record.get("time")));
	            	} else {
	            		renderJson(ResultUtil.fail(-1,"失败"));
	            	} 
	            }
	        } else {
	        	log.info("状态不是预约状态");
	        	
	        	if(!record.get("status").equals("nostatus") ) {
	        		if(record.get("status").equals("2"))
	        			renderJson(ResultUtil.fail(-1,"预约取消"));
	        		else {
						renderJson(ResultUtil.fail(-1, "已登记"));
					}
	        	} else {
	        		renderJson(ResultUtil.fail(-1,"未预约"));
	        	}	
	        }
       } catch (Exception e) {
           renderJson(ResultUtil.fail(-1, e.getMessage()));
       }
	}
	
    //获取orderid，给c#使用
    public void getOrderid() {
    	try {
    		if(getPara("studyid") != null){
    			int getorderid = sv.getorderid(getPara("studyid"));
    			renderJson(getorderid);
    		} else {
    			renderNull();
    		}
			
		} catch (Exception e) {
			renderNull();
		}
    }
    
    /**
     * 更新打印次数
     */
    public void countPrint() {
    	if(StrKit.notBlank(getPara("reportid"))){
    		Db.update("update report set printcount=printcount+1 where id=?",getParaToInt("reportid"));
    	}
    	renderNull();
    }
    
    /**
	 * 获取打印内容
	 */
	public void preview() {
		try {
			HttpServletRequest request = getRequest();
			ServletContext context = request.getServletContext();
			if("1".equals(getPara("issr"))){
				render(new PdfRender(printsv.makeSrReportAsPdf(getParaToInt("reportid"),request.getServerPort())));
			}
			else{
				Report report = Report.dao.findById(getParaToInt("reportid"));
//				Studyorder so = null;
//				String reportFontSize = "14";
//				if(report!=null && report.getStudyorderfk() != null) {
//					so = Studyorder.dao.findById(report.getStudyorderfk());
//				}
				String printtempname = "";
				if(StringUtils.equals("checklist", getPara("printType"))) {
					printtempname = PrintTemplateName.getPrintTemplate_Reg(getParaToInt("orderid"));
					System.out.println("checklist=========="+printtempname);
				}
				else if(StringUtils.equals("reservation", getPara("printType"))){
					printtempname = PrintTemplateName.getPrintTemplate_Sch(getParaToInt("orderid"));
					System.out.println("reservation=========="+printtempname);
				}
				else{
					printtempname = PrintTemplateName.getPrintTemplate_Report(report);
//					reportFontSize = result.get("fontSize");
				}
				
				log.info("printtempname:" + printtempname);
				File reportFile = new File(context.getRealPath("upload/print/" + printtempname + ".jasper"));
				if (reportFile.exists() == false) {
					renderNull();
					return;
				}
				HashMap<String, Object> parameters = new HashMap<String, Object>();// 给报表模板文件传参
				// 得到枚举类型的参数名称，参数名称若有重复的只能得到第一个--获取页面传来的参数，和模板中文件的sql参数名称一一对应
				Enumeration<?> temp = request.getParameterNames();
				while (temp.hasMoreElements()) {
					String paramName = (String) temp.nextElement();
					String paramValue = request.getParameter(paramName);
					log.info("paramName:" + paramName + "   ,paramValue:" + paramValue);
					parameters.put(paramName, paramValue);
				}
				//parameters.put("reportFontSize", reportFontSize);
				parameters.put(PrintTemplateParameters.REPORT_DESC_FONTSIZE, report.getDescFontsize()!=null?report.getDescFontsize()+"":PrintTemplateName.REPORT_DEFAULT_PRINT_FONTSIZE);
				parameters.put(PrintTemplateParameters.REPORT_RESULT_FONTSIZE, report.getResultFontsize()!=null?report.getResultFontsize()+"":PrintTemplateName.REPORT_DEFAULT_PRINT_FONTSIZE);
				parameters.put(PrintTemplateParameters.SERVER_URL, getRequest().getScheme()+"://"+IPKit.getServerIP(getRequest())+":"+getRequest().getServerPort()+getRequest().getContextPath());
//				log.info("paramName:reportFontSize" + "   ,paramValue:" + reportFontSize);
				byte[] bytes = printsv.makeNormalReportAsPdf(reportFile, parameters);
				render(new PdfRender(bytes));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
//	public void printHtml(Integer reportid,int port) {
//		render(new PdfRender(printsv.makeSrReportAsPdf(reportid, port, "")));
//	}
	
	/*
	 * 活度仪用户登录
	 */
	public void dologin() {

		log.info("---活度仪dologin----");
	    String username = getPara("username");
	    String userpassword = getPara("password");
	    log.info("username"+username);
	    log.info("password"+userpassword);
	    SerializeRes res=(SerializeRes)getSessionAttr("locale");
		String error = "";
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(username,userpassword);
		try {
			subject.login(token);
			User user = User.dao.findFirst("select * from users where username=?",username);
			renderJson(ResultUtil.success(user.getName()));
		} catch(UnknownAccountException ue) {
			token.clear();
			error = "账号不存在";
		} catch(IncorrectCredentialsException ie) {
			//ie.printStackTrace();
			token.clear();
			error = "密码错误";
		} catch(LockedAccountException e){
			token.clear();
			error = "账号锁定";
		} catch(ExpiredCredentialsException e){
			token.clear();
			error = "账号已过期";
		} catch(DisabledAccountException e){
			token.clear();
			error = "账号禁用";
		} catch(RuntimeException re) {
			//re.printStackTrace();
			token.clear();
			error = "登录失败";
		}
		finally {
			log.info("error:"+error);
			if(error !=""){
				renderJson(ResultUtil.fail(-1, error));
			}
			
		}
	
	}
}
