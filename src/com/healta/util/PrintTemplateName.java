package com.healta.util;

import java.util.List;
import java.io.File;
import com.healta.constant.CacheName;
import com.healta.constant.PrintTemplateType;
import com.healta.constant.ReportType;
import com.healta.model.Printtemplate;
import com.healta.model.Report;
import com.healta.model.Studyitem;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;

public class PrintTemplateName {
	public static final String REPORT_DEFAULT_PRINT_FONTSIZE = PropKit.use("system.properties").get("REPORT_DEFAULT_PRINT_FONTSIZE","14");
	public static final String DEFAULT_REPORTNAME = PropKit.use("system.properties").get("default_reportName");
	public static final String DEFAULT_REG_REPORTNAME = PropKit.use("system.properties").get("default_reg_reportName");
	public static final String DEFAULT_SCH_REPORTNAME = PropKit.use("system.properties").get("default_sch_reportName");
	public static final String DEFAULT_BAGSTICKER_TEMPLATENAME = PropKit.use("system.properties").get("default_bagsticker_templateName");
	public static final String DEFAULT_INFORMED_CONSENT_TEMPLATENAME = PropKit.use("system.properties").get("default_informed_consent_templateName");
	public static final String DEFAULT_MEDICALHISTORY_TEMPLATENAME = PropKit.use("system.properties").get("default_medicalhistory_templateName");
	public static final String DEFAULT_ALLIMAGE_TEMPLATENAME = PropKit.use("system.properties").get("default_allimage_templateName","allImages");
	public static final String DEFAULT_ONEIMAGE_TEMPLATENAME = PropKit.use("system.properties").get("default_oneimage_templateName","oneImage");
	
	/**
	 * 获取报告模板名 
	 * @param report
	 * 匹配优先级 报告中保存的reportType>检查项目>设备类型>默认模板
	 * 
	 * @return printtempname
	 */
	public static String getPrintTemplate_Report(Report report) {
		String printtempname = DEFAULT_REPORTNAME;
		List<Printtemplate> temps = 
				Printtemplate.dao.findByCache(CacheName.PRINTTEMPLATE, CacheName.PRINTTEMPLATE_KEY, "select * from printtemplate order by examitem_id"); //order by examitem_id 当examitem_id为空时，排在前面
		if(StrKit.notBlank(report.getReportType())&&!StrKit.equals(ReportType.NORMAL.getName()+"", report.getReportType())
				&&!StrKit.equals(ReportType.STRUCTURED.getName()+"", report.getReportType())){
			printtempname="image_report_"+report.getReportType();
			if(new File(JFinal.me().getServletContext().getRealPath("upload/print/"+printtempname+".jasper")).exists()){
				return printtempname;
			} else {
				printtempname = DEFAULT_REPORTNAME;
			}
		}
		if(temps!=null&&temps.size()>0) {
			List<Studyitem> items= Studyitem.dao.find("select * from studyitem where orderid=?",report.getStudyorderfk());
			for(Printtemplate p:temps) {
				if(StrKit.equals(p.getType(), PrintTemplateType.REPORT_TEMPLATE.getName())){
					for (Studyitem item: items) {
						//先匹配设备类型，因为以 order by examitem_id排序， 当examitem_id为空时，排在前面
						if(StrKit.equals(p.getModalityType(), item.getModality())&&p.getExamitemId()==null){
							printtempname = p.getTemplateName();
						}
						//继续匹配检查项目，匹配成功，直接返回
						if(p.getExamitemId()!=null&&p.getExamitemId().intValue()==item.getItemId().intValue()){
							return p.getTemplateName();
						}
					}
				}
			}	
		}
		return printtempname;
	}
	
	/**
	 * 获取检查单模板名
	 * @param orderid
	 * @return
	 */
	public static String getPrintTemplate_Reg(int orderid){
		String printtempname = DEFAULT_REG_REPORTNAME;
		List<Printtemplate> temps = 
				Printtemplate.dao.findByCache(CacheName.PRINTTEMPLATE, CacheName.PRINTTEMPLATE_KEY, "select * from printtemplate order by examitem_id"); //order by examitem_id 当examitem_id为空时，排在前面
		
		if(temps!=null&&temps.size()>0) {
			List<Studyitem> items= Studyitem.dao.find("select * from studyitem where orderid=?",orderid);
			for(Printtemplate p:temps) {
				if(StrKit.equals(p.getType(), PrintTemplateType.REG_TEMPLATE.getName())){
					for (Studyitem item: items) {
						//先匹配设备类型，因为以 order by examitem_id排序， 当examitem_id为空时，排在前面
						if(StrKit.equals(p.getModalityType(), item.getModality())&&p.getExamitemId()==null){
							printtempname = p.getTemplateName();
						}
						//继续匹配检查项目，匹配成功，直接返回
						if(p.getExamitemId()!=null&&p.getExamitemId().intValue()==item.getItemId().intValue()){
							return p.getTemplateName();
						}
					}
				}
			}	
		}
		return printtempname;
	}
	
	/**
	 * 获取预约单模板名
	 * @param orderid
	 * @return
	 */
	public static String getPrintTemplate_Sch(int orderid){
		String printtempname = DEFAULT_SCH_REPORTNAME;
		List<Printtemplate> temps = 
				Printtemplate.dao.findByCache(CacheName.PRINTTEMPLATE, CacheName.PRINTTEMPLATE_KEY, "select * from printtemplate order by examitem_id"); //order by examitem_id 当examitem_id为空时，排在前面
		
		if(temps!=null&&temps.size()>0) {
			List<Studyitem> items= Studyitem.dao.find("select * from studyitem where orderid=?",orderid);
			for(Printtemplate p:temps) {
				if(StrKit.equals(p.getType(), PrintTemplateType.SCH_TEMPLATE.getName())){
					for (Studyitem item: items) {
						//先匹配设备类型，因为以 order by examitem_id排序， 当examitem_id为空时，排在前面
						if(StrKit.equals(p.getModalityType(), item.getModality())&&p.getExamitemId()==null){
							printtempname = p.getTemplateName();
						}
						//继续匹配检查项目，匹配成功，直接返回
						if(p.getExamitemId()!=null&&p.getExamitemId().intValue()==item.getItemId().intValue()){
							return p.getTemplateName();
						}
					}
				}
			}	
		}
		return printtempname;
	}
	
	/**
	 * 获取所有图像模板
	 * @param orderid
	 * @return
	 */
	public static String getPrintTemplate_AllImages(int orderid){
		String printtempname = DEFAULT_ALLIMAGE_TEMPLATENAME;
		List<Printtemplate> temps = 
				Printtemplate.dao.findByCache(CacheName.PRINTTEMPLATE, CacheName.PRINTTEMPLATE_KEY, "select * from printtemplate order by examitem_id"); //order by examitem_id 当examitem_id为空时，排在前面
		
		if(temps!=null&&temps.size()>0) {
			List<Studyitem> items= Studyitem.dao.find("select * from studyitem where orderid=?",orderid);
			for(Printtemplate p:temps) {
				if(StrKit.equals(p.getType(), PrintTemplateType.REG_TEMPLATE.getName())){
					for (Studyitem item: items) {
						//先匹配设备类型，因为以 order by examitem_id排序， 当examitem_id为空时，排在前面
						if(StrKit.equals(p.getModalityType(), item.getModality())&&p.getExamitemId()==null){
							printtempname = p.getTemplateName();
						}
						//继续匹配检查项目，匹配成功，直接返回
						if(p.getExamitemId()!=null&&p.getExamitemId().intValue()==item.getItemId().intValue()){
							return p.getTemplateName();
						}
					}
				}
			}	
		}
		return printtempname;
	}
	
	/**
	 * 获取所有图像模板
	 * @param orderid
	 * @return
	 */
	public static String getPrintTemplate_OneImages(int orderid){
		String printtempname = DEFAULT_ONEIMAGE_TEMPLATENAME;
		List<Printtemplate> temps = 
				Printtemplate.dao.findByCache(CacheName.PRINTTEMPLATE, CacheName.PRINTTEMPLATE_KEY, "select * from printtemplate order by examitem_id"); //order by examitem_id 当examitem_id为空时，排在前面
		
		if(temps!=null&&temps.size()>0) {
			List<Studyitem> items= Studyitem.dao.find("select * from studyitem where orderid=?",orderid);
			for(Printtemplate p:temps) {
				if(StrKit.equals(p.getType(), PrintTemplateType.REG_TEMPLATE.getName())){
					for (Studyitem item: items) {
						//先匹配设备类型，因为以 order by examitem_id排序， 当examitem_id为空时，排在前面
						if(StrKit.equals(p.getModalityType(), item.getModality())&&p.getExamitemId()==null){
							printtempname = p.getTemplateName();
						}
						//继续匹配检查项目，匹配成功，直接返回
						if(p.getExamitemId()!=null&&p.getExamitemId().intValue()==item.getItemId().intValue()){
							return p.getTemplateName();
						}
					}
				}
			}	
		}
		return printtempname;
	}

}
