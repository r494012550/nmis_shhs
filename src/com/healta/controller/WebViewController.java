package com.healta.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.constant.ReportStatus;
import com.healta.constant.TableNameConstant;
import com.healta.model.DicEmployee;
import com.healta.model.Filter;
import com.healta.model.Report;
import com.healta.model.SRTemplate;
import com.healta.model.User;
import com.healta.model.UserProfiles;
import com.healta.service.FrontCommonService;
import com.healta.service.ReportService;
import com.healta.service.WebViewService;
import com.healta.service.WorkListService;
import com.healta.util.IPKit;
import com.healta.util.ResultUtil;
import com.healta.util.SyscodeKit;
import com.jfinal.core.Controller;
import com.jfinal.i18n.Res;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class WebViewController extends Controller {
	private final static Logger log = Logger.getLogger(ReportController.class);
	private static WorkListService sv=new WorkListService();
	private static ReportService svr = new ReportService();
	private static WebViewService websv=new WebViewService();
	private static FrontCommonService frontCommonsv = new FrontCommonService();
	/**
	 * 跳转到报告查询页面
	 */
	public void index() {
		UserProfiles profiles=(UserProfiles)getSession().getAttribute("myprofiles");
		if(profiles!=null){
			setAttr("openimageatonce", profiles.getOpenimageatonce());
		}
		String imagesize=PropKit.use("system.properties").get("syngoviafindingimagesize");
		if(StringUtils.isNotEmpty(imagesize)){//给setAttr（图片宽高）
			String sizes[]=imagesize.split("-");
			setAttr("imgwidth", sizes[0]);
			setAttr("imgheight", sizes[1]);
		}
		renderJsp("/view/webView/worklist.jsp");
	}
	
	public void centerSearch() {
		renderJsp("/view/webView/center_search.jsp");
	}
	
	public void west_search(){
		setAttr("finalreport_display",SyscodeKit.INSTANCE.getCodeDisplay("0007", ReportStatus.FinalResults, getSessionAttr("language")));
		setAttr("finalreport", ReportStatus.FinalResults);
		renderJsp("/view/webView/west_search.jsp");
	}
	
	/**
	 * 获取常用条件列表
	 */
	public void getFilters() {
		try {
			User user = (User) getSession().getAttribute("user");
			renderText(JsonKit.toJson(sv.findFilters(null,user.getId(),getPara("filterType"),false,getSessionAttr("language"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderText(JsonKit.toJson(ResultUtil.fail(1, e.getMessage())));
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
	
	/**
	 * 获取工作列表页面所有数据
	 */
	public void getDataAll() {
		User user = (User) getSession().getAttribute("user");
		try {
			DicEmployee employee=DicEmployee.dao.findById(user.getEmployeefk());
			log.info("Institutionid:"+employee.getInstitutionid());
			if(employee.getInstitutionid()==null){
				renderNull();
			}
			else{
				Page<Record> page= sv.findStudyInfo(getParaMap(),employee.getInstitutionid(),getSessionAttr("syscode_lan"),user);
				Map<String, Object> jsonMap = new HashMap<String, Object>();//定义map  
	            jsonMap.put("total", page.getTotalRow());//total键 存放总记录数，必须的  
	            jsonMap.put("rows", page.getList());//rows键 存放每页记录 list  
	            renderText(JsonKit.toJson(jsonMap));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderText(JsonKit.toJson(ResultUtil.fail(1, e.getMessage())));
		}
	}
	
	public void report(){
		try {
			String studyid=getPara("studyid");
			Integer orderid = getParaToInt("orderid");
			Integer reportid = getParaToInt("reportid");
			setAttr("studyid", studyid);
			setAttr("studyorderfk", orderid);
			setAttr("reportid",reportid);
			Record record=svr.getStydyAndReportInfo(reportid);
			if(record!=null){

				if(record.getStr("age")!=null){
					setAttr("age", record.getStr("age")+record.getStr("ageunitdisplay"));
				}
				String modality_type=record.getStr("modality_type");
				log.info(modality_type);
				if(modality_type.indexOf("\\")>0){
					modality_type=modality_type.substring(0, modality_type.indexOf("\\"));
				}
				setAttr("modality", modality_type);
				String studyinstanceuid=record.getStr("studyinstanceuid");
				String para=PropKit.use("system.properties").get("launcherviapara");
				/*if(studyinstanceuid!=null){
					para=para.replace("?", studyinstanceuid);
				}
				else{
					para=para.replace("?", studyid);
				}*/
				para=para.replace("?", studyid);
				setAttr("para", para);
				setAttr("reporttitle", PropKit.use("system.properties").get("reporttitle"));
				setAttr("report", record);
			}
//			setAttr("projecturl", IPKit.getServerIP(getRequest()));
			setAttr("projecturl", getRequest().getScheme()+"://"+IPKit.getServerIP(getRequest())+":"+getRequest().getServerPort()+getRequest().getContextPath());
			setAttr("printername",Base64.encodeBase64String("report".getBytes("UTF-8")));
			renderJsp("/view/webView/report.jsp");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void structReport(){

		try {
			
			String studyid=getPara("studyid");
			Integer orderid = getParaToInt("orderid");
			Integer reportid = getParaToInt("reportid");
			Record record=svr.getStydyAndReportInfo(reportid);
			setAttr("studyorderfk", orderid);
			setAttr("reportid", reportid);
			setAttr("patientidfk", record.getInt("patientidfk"));
			setAttr("studyid", studyid);
			setAttr("patientname", record.getStr("patientname"));
			setAttr("viareportid", record.getStr("viareportid"));
			setAttr("studyinsuid", record.getStr("studyinstanceuid"));
			setAttr("studyitem", record.getStr("studyitems"));
			setAttr("srtemplateid", record.getStr("template_id"));
			SRTemplate srt=SRTemplate.dao.findById(record.getInt("template_id"));
			if(srt!=null) {
				setAttr("srtemplatename",Base64.encodeBase64String(srt.getName().getBytes("UTF-8")));
			}

			String studyinstanceuid=record.getStr("studyinstanceuid");
			String callviapara=PropKit.use("system.properties").get("launcherviapara");
			String plaza_loaddata=PropKit.use("system.properties").get("launcherplazacmd_loaddata");
			if(StrKit.isBlank(studyinstanceuid)){
				studyinstanceuid=studyid;
			}
			/*if(studyinstanceuid!=null){
				callviapara=callviapara.replace("?", studyinstanceuid);
				plaza_loaddata=plaza_loaddata+" -a "+studyinstanceuid;
			}*/
			if(studyid!=null){
				callviapara=callviapara.replace("?", studyid);
				plaza_loaddata=plaza_loaddata+" -a "+studyid;
			}
			
			setAttr("enable_via_callup", PropKit.use("system.properties").get("enable_via_callup"));
			setAttr("callviapara", callviapara);
			setAttr("enable_plaza_callup", PropKit.use("system.properties").get("enable_plaza_callup"));
			setAttr("plaza_loaddata", plaza_loaddata);
			setAttr("projecturl", IPKit.getServerIP(getRequest()));
			renderJsp("/view/webView/structReport.jsp");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 打开报告
	 */
	public void openReport() {
		try {
//			User user = (User) getSession().getAttribute("user");
//			Res r=(Res)getSessionAttr("locale");
			renderJson(ResultUtil.success(Report.dao.findById(getParaToInt("reportid"))));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}

	/**
	 * 保存查询条件界面
	 */
	public void openFilterSaveDialog() {
		renderJsp("/view/webView/filter.jsp");
	}
	/**
	 * 加载列表管理界面
	 */
	public void openFilterManageDialog() {
		//setAttr("filterType", "worklist");
		renderJsp("/view/webView/filterManage.jsp");
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
	 * 报告展开
	 */
	public void previewReport(){
		Report report=Report.dao.findById(getParaToInt("reportid"));
		String imagequality = SyscodeKit.INSTANCE.getCodeDisplay("0025",report.getImagequality(),getSessionAttr("language"));
		report.setImagequality(imagequality);
		setAttr("report", report);
		renderJsp("/view/webView/previewReport.jsp");
	}
	
	
	//*********frontcommon*************
	/**
	 * 申请单
	 */
	public void getImage(){
		try {
			renderJson(ResultUtil.success(frontCommonsv.getApplyImage(getParaToInt("orderid"))));
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
	
	public void gotoStudyProcess(){
		Record record=frontCommonsv.findStudyInfo(getParaToInt("orderid"));
		
		setAttr("record", record);
		renderJsp("/view/front/studyprocess.jsp");
	}
	
	public void getStudyProcess(){
		try {
			renderJson(frontCommonsv.findStudyProcess(getParaToInt("orderid"),getSessionAttr("language")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	/**
     *  pdf报表导出
     */
    public void exportReport() {
        try {
        	 Record re = svr.getReportInfo(getParaToInt("reportid"));
             File file = sv.exportReport( getInt("reportid"), getRequest());
             if (file!=null&&file.exists()) {
                 renderFile(file,re.getStr("patientname")+"_"+re.getStr("studyid")+"_"+getParaToInt("reportid")+".pdf");
             } else{
             	renderNull();
             }
        } catch (Exception e) {
            e.printStackTrace();
            renderHtml("<html><body>"+e.getMessage()+"</body></html>");
        }
    }
    
    /**
     *  pdf 文件批量导出
     */
    public void batchExportReport() {
        try {
        	String idsString = get("ids");
            String[] ids = idsString.split(",");
            // 获取
            HttpServletRequest request = getRequest();
            // zip文件保存目录
            String zipFilePath = PropKit.use("system.properties").get("tempdir")+System.getProperty("file.separator")
            +"export"+System.getProperty("file.separator")+"report.zip";
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath));
            BufferedOutputStream bos = new BufferedOutputStream(zos);
            // 将生成的pdf写入zip文件中
            for (int i = 0; i < ids.length; i++) {
                Integer id = Integer.valueOf(ids[i]);
                // 获取导出模板的类型和名称
                Record re = svr.getReportInfo(id);
                File file = sv.exportReport( id,request);
                if(file==null||!file.exists()) {
                	log.error("Error generating report file. The order id:"+id);
                	continue;
                }
//                log.info("生成pdf的report id 为:" + id + ", 模板类型为：" + map.get("issr") + " ,文件名称为：" + file.getName());
                // 将文件放进zip文件里
                zos.putNextEntry(new ZipEntry(re.getStr("patientname")+"_"+re.getStr("studyid")+"_"+file.getName()));
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                int read;
                while ((read = bis.read()) != -1) {
                    // 将文件已字节流方式写入zip文件中
                    bos.write(read);
                }
                // 等待上一个文件写完后，在开始写入下一个文件
                bos.flush();
                bis.close();
                fis.close();
            }
            bos.close();
            File zipFile = new File(zipFilePath);
            renderFile(zipFile);
        } catch (Exception e) {
            e.printStackTrace();
            renderHtml("<html><body>"+e.getMessage()+"</body></html>");
        }
    }
    
    /**
     *  获取需要导出的数据
     */
    public void getBatchExportReport() {
        User user = (User) getSession().getAttribute("user");
        List<Record> list = new ArrayList<Record>();
        try {
        	DicEmployee employee = DicEmployee.dao.findById(user.getEmployeefk());
            if (employee.getInstitutionid() == null) {
                renderNull();
            }
            Page<Record> page = sv.findStudyInfo(getParaMap(),null,getSessionAttr("syscode_lan"),user);
            int count = page.getTotalRow();
            list.add(new Record().set("totalsize", count));
            if (count<500) {
                // 如果数据量大于500条，则提示用户缩短时间范围
                list.addAll(page.getList());
            }
            renderJson(list);
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(ResultUtil.fail(1, e.getMessage()));
        }
    }
    
}
