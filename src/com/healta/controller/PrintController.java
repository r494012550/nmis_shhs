package com.healta.controller;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.constant.PrintTemplateParameters;
import com.healta.constant.PrintTemplateType;
import com.healta.model.Report;
import com.healta.model.Studyorder;
import com.healta.render.PdfRender;
import com.healta.service.PrintService;
import com.healta.util.IPKit;
import com.healta.util.PrintTemplateName;
import com.healta.util.ResultUtil;
import com.healta.util.WebSocketUtils;
import com.healta.vo.WebsocketVO;
//import com.itextpdf.text.FontProvider;
//import com.itextpdf.text.PageSize;
//import com.itextpdf.text.pdf.PdfWriter;
//import com.itextpdf.tool.xml.XMLWorkerFontProvider;
//import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

public class PrintController extends Controller {

    private final static Logger log = Logger.getLogger(PrintController.class);
    public static PrintService sv=new PrintService();
    
	/**
	 * 打印报告页面
	 * 
	 * @param studyid
	 */
	public void printReport() {
//		setAttr("studyid", getPara("studyid"));
//		setAttr("orderid", getParaToInt("orderid"));
		setAttr("reportid", getParaToInt("reportid"));
//		setAttr("printtempname", getPara("printtempname"));
//		setAttr("issr", getPara("issr"));
//		setAttr("fontSize", getParaToInt("fontSize"));
		setAttr("printType", getPara("printType"));
		setAttr("select_fontsize", getBoolean("select_fontsize"));
		setAttr("desc_fontsize_name", PrintTemplateParameters.REPORT_DESC_FONTSIZE);
		setAttr("res_fontsize_name", PrintTemplateParameters.REPORT_RESULT_FONTSIZE);
		renderJsp("/view/front/print.jsp");
	}

	/**
	 * 获取打印内容
	 */
	public void preview() {
		try {
			HttpServletRequest request = getRequest();
			ServletContext context = request.getServletContext();
			String printtempname = "";
			Report report = null;
				
			switch(PrintTemplateType.valOf(get("printTempType"))){
				case REG_TEMPLATE: 
					printtempname = PrintTemplateName.getPrintTemplate_Reg(getParaToInt("orderid"));
					break;
				case SCH_TEMPLATE:
					printtempname = PrintTemplateName.getPrintTemplate_Sch(getParaToInt("orderid"));
					break;
				case BAGSTICKER_TEMPLATE:
					printtempname = PrintTemplateName.DEFAULT_BAGSTICKER_TEMPLATENAME;
					break;
				case INFORMED_CONSENT_TEMPLATE:
					printtempname = PrintTemplateName.DEFAULT_INFORMED_CONSENT_TEMPLATENAME;
					break;
				case MEDICALHISTORY_TEMPLATE:
					printtempname = PrintTemplateName.DEFAULT_MEDICALHISTORY_TEMPLATENAME;
					break;
				case ALLIMAGE_TEMPLATE: 
					printtempname=get("printtempname");
					if(StrKit.isBlank(printtempname))
					printtempname = PrintTemplateName.getPrintTemplate_AllImages(getInt("orderid"));
					break;
				case ONEIMAGE_TEMPLATE:
					printtempname=get("printtempname");
					if(StrKit.isBlank(printtempname))
					printtempname = PrintTemplateName.getPrintTemplate_OneImages(getInt("orderid"));
					break;    
				default:
					report = Report.dao.findById(getParaToInt("reportid"));
					if(report.getTemplateId() != null && report.getTemplateId() >0){
		//				printHtml(getParaToInt("reportid"),request.getServerPort());
						render(new PdfRender(sv.makeSrReportAsPdf(getParaToInt("reportid"),request.getServerPort())));
						printtempname=null;
					}
					else {
						printtempname = PrintTemplateName.getPrintTemplate_Report(report);
					}
			}
				
			log.info("printtempname:" + printtempname);
			if(StrKit.notBlank(printtempname)){
				File reportFile = new File(context.getRealPath("upload/print/" + printtempname + ".jasper"));
				log.info(reportFile);
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
				parameters.put(PrintTemplateParameters.SERVER_URL, getRequest().getScheme()+"://"+IPKit.getServerIP(getRequest())+":"+getRequest().getServerPort()+getRequest().getContextPath());
				if(report!=null){
					Integer desc_fontsize=getInt(PrintTemplateParameters.REPORT_DESC_FONTSIZE);
					if(desc_fontsize==null) {
						parameters.put(PrintTemplateParameters.REPORT_DESC_FONTSIZE, 
								//report.getDescFontsize()!=null?report.getDescFontsize()+"":
									PrintTemplateName.REPORT_DEFAULT_PRINT_FONTSIZE);
						log.info("paramName:" + PrintTemplateParameters.REPORT_DESC_FONTSIZE + "   ,paramValue:" + parameters.get(PrintTemplateParameters.REPORT_DESC_FONTSIZE));
					}
					Integer res_fontsize=getInt(PrintTemplateParameters.REPORT_RESULT_FONTSIZE);
					if(res_fontsize==null) {
						parameters.put(PrintTemplateParameters.REPORT_RESULT_FONTSIZE, 
								//report.getResultFontsize()!=null?report.getResultFontsize()+"":
								PrintTemplateName.REPORT_DEFAULT_PRINT_FONTSIZE);
						log.info("paramName:" + PrintTemplateParameters.REPORT_RESULT_FONTSIZE + "   ,paramValue:" + parameters.get(PrintTemplateParameters.REPORT_RESULT_FONTSIZE));
					}
				}
				render(new PdfRender(sv.makeNormalReportAsPdf(reportFile, parameters)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderNull();
		}
	}
    
    public void countPrint(){
    	
    	if(StrKit.notBlank(getPara("reportid"))){
    		Db.update("update report set printcount=printcount+1 where id=?",getParaToInt("reportid"));
    	}
    	renderNull();
    }
    
    //获取报告打印是否成功
    public void getPrintStatus(){
    	System.out.println("--------"+getPara("printstatus"));
    	System.out.println(getPara("userid"));
//    	WebSocketUtils.sendMessage(getParaToInt("userid")-990000, new WebsocketVO(WebsocketVO.REPORTPRINTSTATUS, getPara("printstatus")).toJson() );
    	renderJson(ResultUtil.success());
    }
    
    //批量打印报告，获取打印状态
    public void getCheckReportPrintStatus() {
    	System.out.println("批量打印-----");
    	System.out.println(getPara("printstatus"));
    	System.out.println(getParaToInt("userid"));
//		WebSocketUtils.sendMessage(getParaToInt("userid")-990000, new WebsocketVO(WebsocketVO.CHECKREPORTPRINTSTATUS, getPara("printstatus")).toJson());
		renderJson(ResultUtil.success());
    }
    
    /**
	 * 预览打印图片
	 * 
	 * @param studyid
	 */
	public void toPreviewImages() {
		setAttr("studyid", getPara("studyid"));
		setAttr("orderid", getInt("orderid"));
		setAttr("reportid", getParaToInt("reportid"));
		setAttr("imgid",getInt("imgid"));
		setAttr("imgids",get("imgids"));
		setAttr("printtempname", get("printtempname"));
		setAttr("printTempType", get("printTempType"));
		renderJsp("/view/front/previewImages.jsp");
	}
    
}


