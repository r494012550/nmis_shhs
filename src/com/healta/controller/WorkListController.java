package com.healta.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import com.healta.constant.ReportStatus;
import com.healta.constant.StudyOrderStatus;
import com.healta.license.VerifyLicense;
import com.healta.model.DicEmployee;
import com.healta.model.Filter;
import com.healta.model.Report;
import com.healta.model.Studyorder;
import com.healta.model.User;
import com.healta.model.UserProfiles;
import com.healta.model.UserShortcutConfig;
import com.healta.render.ImageRender;
import com.healta.server.dicom.Echo;
import com.healta.server.dicom.PatientInfo;
import com.healta.service.FrontCommonService;
import com.healta.service.ReportService;
import com.healta.service.WorkListService;
import com.healta.service.WorkforceService;
import com.healta.util.CallupKit;
import com.healta.util.DateUtil;
import com.healta.util.ExcelUtil;
import com.healta.util.ParaKit;
//import com.healta.service.impl.WorkListServiceImpl;
import com.healta.util.ResultUtil;
import com.healta.util.SerializeRes;
import com.healta.util.SyscodeKit;
import com.jfinal.core.Controller;
import com.jfinal.i18n.Res;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;


/**
 * 工作列表
 * 
 * @author Administrator
 *
 */
public class WorkListController extends Controller {
	private final static Logger log = Logger.getLogger(WorkListController.class);
	/**
	 * 业务层
	 */
	private static WorkListService sv=new WorkListService();
	private static ReportService reportService = new ReportService();
	private static FrontCommonService frontCommonsv = new FrontCommonService();
	private static WorkforceService shiftsv = new WorkforceService();

    /**
           * 跳转工作列表页面
     */
	public void index() {
		User user=(User)getSession().getAttribute("user");
		setAttr("up", UserProfiles.dao.findFirst("select * from userprofiles where userid = ?", user.getId()));
		String imagesize=PropKit.use("system.properties").get("syngoviafindingimagesize");
		if(StringUtils.isNotEmpty(imagesize)){
			String sizes[]=imagesize.split("-");
			setAttr("imgwidth", sizes[0]);
			if(sizes.length>1) {
				setAttr("imgheight", sizes[1]);
			}
		}
		setAttr("vip_flag", user.getVipFlag());
		setAttr("openGrade", getSession().getAttribute("openGrade"));
		setAttr("sr_support",VerifyLicense.hasFunction("srreport"));
		setAttr("enable_workforce", VerifyLicense.hasFunction("workforce"));
		renderJsp("/view/front/worklist/worklist.jsp");
	}
		
	public void centerSearch() {
		User user=(User)getSession().getAttribute("user");
		UserProfiles profiles=(UserProfiles)getSession().getAttribute("myprofiles");
		if(profiles!=null){
			setAttr("openimageatonce", profiles.getOpenimageatonce());
			setAttr("report_historyreport_collapsed", profiles.getReportHistoryreportCollapsed());
			
		}
		setAttr("user_id", user.getId());
		setAttr("enable_via_callup", PropKit.use("system.properties").get("enable_via_callup"));
		setAttr("callviapara", PropKit.use("system.properties").get("launcherviapara").replace("?", " "));
		setAttr("enable_plaza_callup", PropKit.use("system.properties").get("enable_plaza_callup"));
		setAttr("plaza_loaddata", CallupKit.callUpPlaza_loadDataCmd(user));
		setAttr("sr_support",VerifyLicense.hasFunction("srreport"));
		setAttr("quicksearch",VerifyLicense.hasFunction("quicksearch"));
		renderJsp("/view/front/worklist/center_search.jsp");
	}
	
	public void west_search() {
		setAttr("sr_support",VerifyLicense.hasFunction("srreport"));
		setAttr("workforce_support",VerifyLicense.hasFunction("workforce"));
		UserProfiles profiles=(UserProfiles)getSession().getAttribute("myprofiles");
		if(profiles!=null){
			setAttr("myreport_collapsed", profiles.getMyreportCollapsed());
			setAttr("myfilter_collapsed", profiles.getMyfilterCollapsed());
		}
		setAttr("quicksearch",VerifyLicense.hasFunction("quicksearch"));
		renderJsp("/view/front/worklist/west_search.jsp");
	}

	/**
	 * 获取工作列表页面所有数据
	 */
	public void getDataAll() {

		User user = (User) getSession().getAttribute("user");
		try {
			Map<String, Object> jsonMap = new HashMap<String, Object>();//定义map
			String datasource=get("datasource");
			if(StrKit.isBlank(datasource)||StrKit.equals("localdatabase", datasource)){
			    log.info("Quick Search Content:" + getPara("quicksearchcontent")+";Quick Search Name:"+getPara("quicksearchname"));
			    Page<Record> page = sv.findStudyInfo(getParaMap(),null,getSessionAttr("syscode_lan"),user);
			    jsonMap.put("total", page.getTotalRow());
	            jsonMap.put("rows", page.getList());//rows键 存放每页记录 list  
			} else {
				List<Record> list=sv.querySyngovia(new ParaKit(getParaMap()));
				int count = list.get(0).getInt("totalsize");//workListService.getDataNumber(record);
				list.remove(0);
				//total键 存放总记录数，必须的  
				jsonMap.put("total", count);
	            jsonMap.put("rows", list);//rows键 存放每页记录 list  
			}
			renderJson("data", jsonMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void findStudyByFavoritesId() {
        Integer page = getParaToInt("page");  
        Integer rows = getParaToInt("rows"); 
        if(page == null){
       	 page = 1;
        }
        if(rows == null){
       	 rows = 20;
        }
        int start = (page-1) * rows;
		List<Record> list = null;
		try {
			
			list = sv.findStudyByFavoritesId(getParaToInt("favorites_id"),start,rows,getSessionAttr("syscode_lan"));
			int count = list.get(0).getInt("totalsize");//workListService.getDataNumber(record);
			list.remove(0);
			Map<String, Object> jsonMap = new HashMap<String, Object>();//定义map  
			jsonMap.put("total", count);//total键 存放总记录数，必须的  
			jsonMap.put("rows", list);//rows键 存放每页记录 list  
			renderJson("data", jsonMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}

	/**
	 * echo
	 */
	public void echo() {
		Echo echo = new Echo();
		//DICOM://PLAZA:REPORT@10.10.2.60:104
		String dcmurl="DICOM://"+getPara("aet")+":"+PropKit.use("system.properties").get("localaet")+"@"+getPara("hostname")+":"+getPara("port");
		log.info("Echo scu, dcmurl="+dcmurl);
		long time = echo.doEcho(dcmurl);
		if(time==-1){
			renderJson(ResultUtil.fail(1,""));
		}
		else{
			renderJson(ResultUtil.success());
		}
	}

	/**
	 * 加载列表管理界面
	 */
	public void openFilterManageDialog() {
		//setAttr("filterType", "worklist");
		renderJsp("/view/front/worklist/filterManage.jsp");
	}

	/**
	 * 加载详情信息弹窗
	 */
	public void detailsView() {
		renderJsp("/view/front/details.jsp");
	}

	/**
	 * 加载检查流程弹窗
	 */
	public void checkProcess() {
		renderJsp("/view/front/checkProcess.jsp");
	}

	/**
	 * 申请单弹窗
	 */
	public void applyView() {
		renderJsp("/view/front/image.jsp");
	}
	
	
	/**
	 * 保存查询条件界面
	 */
	public void openFilterSaveDialog() {
		renderJsp("/view/front/worklist/filter.jsp");
	}
	
	public void previewReport(){
		Report report=Report.dao.findById(getParaToInt("reportid"));
		String imagequality = SyscodeKit.INSTANCE.getCodeDisplay("0025",report.getImagequality(),getSessionAttr("language"));
		report.setImagequality(imagequality);
		setAttr("report", report);
		setAttr("preAudit", PropKit.use("system.properties").getBoolean("PreAudit", true));
//		renderJsp("/view/front/worklist/previewReport.jsp");
		renderJsp("/view/webView/previewReport.jsp");
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
	 * 获取所有保存的查询条件
	 */
	public void getAllSearch() {
		try {
//			workListService.getAllSearch();
			renderJson();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}

	/**
	 * 删除保存的查询条件
	 */
	public void deleteFilter() {
		try {
			if(StrKit.isBlank(getPara("id"))||"null".equals(getPara("id"))) {
				renderJson(ResultUtil.fail(2,"该条件不能删除！！"));
			}else if (frontCommonsv.deleteFilters(getPara("id"))) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail("删除失败！"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}

	/**
//	 * 获取检查类型
//	 */
//	public void getCode() {
//		try {
//			Record record = new Record();
//			record.set("type", getPara("type"));
//			renderJson(workListService.getCode(record));
//		} catch (Exception e) {
//			// TODO Auto-generated catch b/			renderJson(ResultUtil.fail(1, e.getMessage()));
//		}
//	}

	/**
	 * 获取常用条件列表
	 */
	public void getFilters() {
		try {
			User user = (User) getSession().getAttribute("user");
			renderJson(sv.findFilters(getParaToInt("_id"),user.getId(),getPara("filterType"),VerifyLicense.hasFunction("workforce"),getSessionAttr("language")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}

	/**
	 * 删除报告信息
	 */
	public void deleteSR() {
		try {
			Report report=Report.dao.findById(getParaToInt("reportid"));
			if(report.getIslocking()==1||ReportStatus.FinalResults.equals(report.getReportstatus())) {
				renderJson(ResultUtil.fail(1,"报告状态已经改变，请刷新列表！"));
			}else {
				if (sv.deleteReport(getParaToInt("reportid"),getPara("studyid"))) {
					setAttr("success", "success");
					renderJson(ResultUtil.success());
				} else {
					renderJson(ResultUtil.fail(""));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}

	}

	/**
	 * 查看详情信息
	 */
	public void viewDetails() {
		try {
			Record record = new Record();
			record.set("patientid", getPara("patientid"));
//			renderJson(workListService.viewDetails(record));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}

//	/**
//	 * 检查流程
//	 */
//	public void checkFlow() {
//		try {
//			Record record = new Record();
//			record.set("studyid", getPara("studyid"));
//			renderJson(workListService.checkFlow(record));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			renderJson(ResultUtil.fail(1, e.getMessage()));
//		}
//	}
	
	
	/**
	 * 检查session
	 */
	public void checkSession(){
		User user = (User) getSession().getAttribute("user");
		if(user==null){
			setAttr("data", "");
		}else{
			setAttr("data", 0);
		}
		renderJson();
	}
	
	public void getFindings(){
		String studyid =getPara("studyid");
		int viaid=getParaToInt("viaid");
		try {
			
			List list=sv.getFindings(studyid,viaid);
			JSONObject json=new JSONObject();
			if(list.size()>0){
				json.put("findings", (JSONArray)list.get(0));
				json.put("images", (JSONArray)list.get(1));
				json.put("pdffile", (String)list.get(2));
			}
			
			renderJson(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	
	/**
	 * 打开锁定
	 */
	public void lockReport(){
		User user = (User) getSession().getAttribute("user");
		try{
			sv.lockReport(getPara("studyid"), user.getUsername());
			renderJson(ResultUtil.success());
		}catch(Exception e){
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//将via数据 保存至本地
	public void retrieveData(){
		try{
//			sv.lockReport(getPara("studyid"), user.getUsername());
			Record re=new Record();
			re.set("patientname", getPara("patientname"));
			re.set("numberofstudyrelatedinstances", getPara("numberofstudyrelatedinstances"));
			re.set("sex", getPara("sexdisplay"));
			re.set("patientid", getPara("patientid"));
			re.set("studydatetime", getPara("studydatetime"));
			re.set("studydescription", getPara("studydescription"));
			re.set("modalitiesinstudy", getPara("modality_type"));
			re.set("studyid", getPara("studyorderstudyid"));
			re.set("studyinstanceuid", getPara("studyinstanceuid"));
			re.set("birthdate", getPara("birthdate"));
			re.set("accessionnumber", getPara("accessionnumber"));
			sv.retrieveData(re,(User) getSession().getAttribute("user"));
			
			/*Record re=new Record();
			re.set("patientname", "KuaiZhengDong");
			re.set("numberofstudyrelatedinstances", "2521");
			re.set("sex", "M");
			re.set("patientid", "601141356");
			re.set("studydatetime", "20211110093543.124000");
			re.set("studydescription", "SH RJ RJ-examination");
			re.set("modalitiesinstudy", "PT\\MR\\SR\\RWV\\OTSEG\\REG");
			re.set("studyid","P0211110208418");
			re.set("studyinstanceuid", "1.2.840.113820.862101.1.107.2021111008232");
			re.set("birthdate", "19680611");
			re.set("accessionnumber", "P0211110208418");*/
			sv.retrieveData(re,(User) getSession().getAttribute("user"));

			renderJson(ResultUtil.success());
		}catch(Exception e){
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 获取病人备注
	 */
	public void getRemarks() {
		try {
			renderJson(ResultUtil.success(sv.getRemarks(getParaToInt("patientidfk"))));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 标签查询
	 */
	public void findStudyByLabel() {
		try {
			List<Record> list = sv.findStudyByLabel(new ParaKit(getParaMap()),getSessionAttr("syscode_lan"));
			int count = list.get(0).getInt("totalsize");//workListService.getDataNumber(record);
			list.remove(0);
			Map<String, Object> jsonMap = new HashMap<String, Object>();//定义map  
            jsonMap.put("total", count);//total键 存放总记录数，必须的  
            jsonMap.put("rows", list);//rows键 存放每页记录 list  
			renderJson("data", jsonMap);
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 全文检索
	 */
	public void findStudyByFulltext() {
		try {
			List<Record> list = sv.findStudyByFulltext(getParaMap(),getSessionAttr("syscode_lan"));
			int count = list.get(0).getInt("totalsize");//workListService.getDataNumber(record);
			list.remove(0);
			Map<String, Object> jsonMap = new HashMap<String, Object>();//定义map  
            jsonMap.put("total", count);//total键 存放总记录数，必须的  
            jsonMap.put("rows", list);//rows键 存放每页记录 list  
			renderJson("data", jsonMap);
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 根据patientid查询历史检查
	 */
	public void findHistoryStudyorder() {
		try {
			renderJson(sv.findHistoryStudyorder(getPara("patientid"), getParaToInt("orderid"), getSessionAttr("syscode_lan")));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 *  pdf报表导出
	 */
	public void exportReport() {
        try {
        	 Record re = reportService.getReportInfo(getParaToInt("reportid"));
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
            +"export"+System.getProperty("file.separator");
	        File dir=new File(zipFilePath);
	        if(!dir.exists()) {
	        	dir.mkdir();
	        }
	        zipFilePath = zipFilePath + "report.zip";
	        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath));
	        BufferedOutputStream bos = new BufferedOutputStream(zos);
	        // 将生成的pdf写入zip文件中
	        for (int i = 0; i < ids.length; i++) {
	            if (StringUtils.isNotEmpty(ids[i])) {
	                Integer id = Integer.valueOf(ids[i]);
	                // 获取导出模板的类型和名称
	                Record re = reportService.getReportInfo(id);
	                File file = sv.exportReport( id, request);
	                if(file==null||!file.exists()) {
	                    log.error("Error generating report file. The order id:"+id);
	                    continue;
	                }
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
            }
            bos.close();
            log.info("下载文件的地址:" + zipFilePath);
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
	
	/**
	 * 将查询出来的已审核报告，导出excel文件
	 */
	public void exportExcel() {
		User user = (User) getSession().getAttribute("user");
        HttpServletResponse response = getResponse();
        List<Record> list = null;
        if (!StrKit.equals("localdatabase", getPara("datasource"))) {
            list = sv.querySyngovia(new ParaKit(getParaMap()));
        } else {
            list = new ArrayList<Record>();
            Page<Record> page =sv.findStudyInfo(getParaMap(),null,getSessionAttr("syscode_lan"),user);
            list.add(new Record().set("totalsize", page.getTotalRow()));
            list.addAll(page.getList());
        }
        // excel的标题
        String titles = get("titles");
        String[] title = titles.split(",");
        // excel对应的列
        String fields = get("fields");
        String[] field = fields.split(",");
        // 下载后文件的名称
        String fileName = "查询报告记录表.xls";
        //sheet名
        String sheetName = "查询报告";
        // 建一个二维数组，前面放行 （每行数据），后面放列 （每列标题）
        list.remove(0);
        log.info("报告数据量为：" + list.size());
        String[][] content = sv.excelContent(list, title, field);
        // 建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, get("widths").split(","));
        // 响应到客户端
        try {
            sv.setResponseHeader(response, fileName);
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            renderNull();
        }
    }
    
  //*******************************************************修改检查时间*********************************************************
  //打开修改检查时间窗口
  	public void openStudydatetimeDlg() {
  		setAttr("studyid", getPara("studyid"));
  		renderJsp("/view/front/worklist/studydatetime.jsp");
  	}
  	
  	//获取plaza中完成时间
  	public void getPlazatime() {
  		try {
  			Echo echo = new Echo();
  			long time = echo.doEcho(PropKit.use("system.properties").get("syncstudydatetimedcmURL"));
  			if(time!=-1){
  				Date studydatetime = null;
  				PatientInfo patientInfo=new PatientInfo();
  				patientInfo.callFindWithQuery("", "", "", "", "","",getPara("studyid") , "", "",
  						PropKit.use("system.properties").get("syncstudydatetimedcmURL"), "");
  				
  				ArrayList<Record> studyList = patientInfo.getStudyList();
  				for(Record re : studyList) {
  					if(StringUtils.isNotBlank(re.getStr("studydatetime"))) {
  						try {
  							log.info(re.getStr("studydatetime"));
  							studydatetime = DateUtil.getHL7Date(re.getStr("studydatetime"));
  						} catch (ParseException e) {
  							log.error("studydatetime invalid format:"+re.getStr("studydatetime"));
  						}
  					}	
  				}
  				renderJson(ResultUtil.success(studydatetime));
  				
  			}else {
  				throw new Exception();
  			}
  		}catch(Exception e) {
  			e.printStackTrace();
  			renderJson(ResultUtil.fail(-1,e.getMessage()));
  		}
  	}
  	
  	//获取登记时间
  	public void getRegistertime() {
  		try {
  			Studyorder studyorder = Studyorder.dao.getStudyorderByStudyid(getPara("studyid"));
  			if(studyorder != null) {
  				renderJson(ResultUtil.success(studyorder.getAppointmenttime()));
  			}else {
  				renderJson(ResultUtil.fail(""));
  			}
  		}catch(Exception e) {
  			e.printStackTrace();
  			renderJson(ResultUtil.fail(-1,e.getMessage()));
  		}
  	}
  	
  	//保存检查时间
  	public void saveStudydatetime() {
  		try {
  			Studyorder studyorder = Studyorder.dao.getStudyorderByStudyid(getPara("studyid"));
  			if(studyorder != null) {
  				if(StringUtils.isNotBlank(getPara("studydatetime"))) {
  					if(Integer.parseInt(studyorder.getStatus()) < Integer.parseInt(StudyOrderStatus.re_examine)) {
  						studyorder.setStatus(StudyOrderStatus.completed);
  					}
  					studyorder.set("studydatetime",getPara("studydatetime"));
  				}
  				studyorder.update();
  			}
  			renderJson(ResultUtil.success());
  		}catch(Exception e) {
  			e.printStackTrace();
  			renderJson(ResultUtil.fail(-1,e.getMessage()));
  		}
  	}
  //*******************************************************修改检查时间*********************************************************
   
//***********************快捷键操作*********************************************************************************************
    /**
	 * 打开快捷键配置
	 */
	public void openShortcutDialog() {
		User user=(User)getSession().getAttribute("user");
		renderJsp("/view/front/worklist/ShortcutDialog.jsp");
	}
	
	//根据检查类型，快捷键获取当前配置的报告模板
	public void getTemplate_shortcuts() {
		User user=(User)getSession().getAttribute("user");
		try {
			String sql = "SELECT TOP 1 *,reporttemplate.name FROM user_shortcut_config "
					+ "LEFT JOIN reporttemplate ON user_shortcut_config.template_id = reporttemplate.id"
					+ " WHERE modality=? AND keyCode = ? AND user_shortcut_config.creator = ?";
			Record record = Db.findFirst(sql, getPara("modality"), getPara("keyCode"), user.getId());
			renderJson(ResultUtil.success(record));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void saveshortcuts() {
		User user=(User)getSession().getAttribute("user");
		try {
			String sql = "SELECT TOP 1 * FROM user_shortcut_config WHERE modality=? AND keyCode = ? AND creator = ?";
			UserShortcutConfig config = UserShortcutConfig.dao.findFirst(sql, getPara("modality"), getPara("keyCode"), user.getId());
			if(config == null) {
				config = new UserShortcutConfig();
				config.setCreator(user.getId());
				config.setModality(getPara("modality"));
				config.setKeyCode(getPara("keyCode"));
				config.setTemplateId(getParaToInt("template_id"));
				config.remove("id").save();
			}else {
				config.setTemplateId(getParaToInt("template_id"));
				config.update();
			}
           renderJson(ResultUtil.success());
       } catch (Exception e) {
       	e.printStackTrace();
           renderJson(ResultUtil.fail(-1, e.getMessage()));
       }	
	}
	
	//使用快捷键填入报告模板
	public void applyTemplate_shortcuts() {
		User user=(User)getSession().getAttribute("user");
		try {
			String sql = "SELECT TOP 1 * FROM user_shortcut_config LEFT JOIN reporttemplate ON user_shortcut_config.template_id = reporttemplate.id WHERE modality=? AND keyCode = ? AND user_shortcut_config.creator = ?";
			Record record = Db.findFirst(sql, getPara("modality"), getPara("keyCode"), user.getId());
			renderJson(ResultUtil.success(record));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
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
	public void getDataSource(){
		renderJson(sv.getDateSource((SerializeRes)getSessionAttr("locale")));
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
	
	public void showDutyRoster() {
		renderJsp("/view/dutyRoster.jsp");
	}
	
	public void showDutyRosterDetails() {
		User user = (User) getSession().getAttribute("user");
		Integer deptid=DicEmployee.dao.findById(user.getEmployeefk()).getDeptfk();
		String postcode=get("postcode");
		LocalDate now=LocalDate.now();
		int dayofweek=now.getDayOfWeek().getValue();
		LocalDate monday=now.minusDays(dayofweek-1);
		Integer offset=0;//getParaToInt("offset");
		monday=monday.plusDays(offset*7);

		String list[]=new String[7];
		for(int i=0;i<7;i++){
			list[i]=monday.plusDays(i).format(DateTimeFormatter.ISO_DATE);
		}
		setAttr("weeklist", list);
		setAttr("shifts", shiftsv.getDeptShiftAndWorktimes(deptid,postcode));
//		setAttr("employeelist", shiftsv.getEmployeesByDept(deptid,postcode));
//		setAttr("postname", getPara("postname"));
		setAttr("today", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
		
		//"ss".compareTo(anotherString)
		switch (get("postcode")) {
		case "T":
			set("post_shift_count", shiftsv.getPostShiftCount(deptid, postcode));
			set("modalitys", shiftsv.getModalityByDept(deptid));
			log.info(getAttr("modalitys"));
			setAttr("map", shiftsv.getShiftWorkDetails(deptid, postcode, monday));
			render("/view/onduty_technician.html");
			break;
		case "D":
			setAttr("map", shiftsv.getShiftWorkDetails(deptid, postcode, monday));
			render("/view/onduty_physician.html");
			break;
		case "N":
			setAttr("map", shiftsv.getShiftWorkDetails(deptid, postcode, monday));
			render("/view/onduty_physician.html");
			break;
		case "REG":
			setAttr("map", shiftsv.getShiftWorkDetails(deptid, postcode, monday));
			render("/view/onduty_physician.html");
			break;
		default:
			setAttr("map", shiftsv.getShiftWorkDetails(deptid, postcode, monday));
			render("/view/onduty_physician.html");
		}
	}
	
	public void returnReport(){
		try {
  			if(sv.returnReport(getInt("orderid"), get("report_status"))) {
  				renderJson(ResultUtil.success());
  			}else {
  				renderJson(ResultUtil.fail(""));
  			}
  		}catch(Exception e) {
  			e.printStackTrace();
  			renderJson(ResultUtil.fail(-1,e.getMessage()));
  		}
	}
	
	public void findReportInfoById() {
		try { 
			Record record=sv.findReportInfoById(getInt("reportid"),getSessionAttr("syscode_lan"));
			renderJson(ResultUtil.success(record));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
}
