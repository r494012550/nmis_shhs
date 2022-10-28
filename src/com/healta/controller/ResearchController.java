package com.healta.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.healta.model.*;
import com.healta.plugin.shiro.ShiroPrincipal;
import com.healta.plugin.shiro.ShiroUtils;
import com.itextpdf.html2pdf.jsoup.Jsoup;
import com.itextpdf.html2pdf.jsoup.select.Elements;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.healta.constant.FormStatus;
import com.healta.constant.Sreport2GroupStatus;
import com.healta.render.PdfRender;
import com.healta.service.PrintService;
import com.healta.service.ResearchService;
import com.healta.util.ParaKit;
import com.healta.util.ResultUtil;
import com.healta.util.SerializeRes;
import com.healta.vo.ResultVO;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class ResearchController extends Controller{
	private static final Logger log = Logger.getLogger(ResearchController.class);
	private static ResearchService sv=new ResearchService();
	private static PrintService psv=new PrintService();
	 
	public void index() {
		User user = (User) getSession().getAttribute("user");
		Integer projectid=getParaToInt("keyWord");
		setAttr("user", user);
		setAttr("projectid", projectid);
		//重置当前用户在课题中的权限
		ShiroUtils.resetProjectPermission(projectid);
		
		String matrix_view_url=PropKit.use("system.properties").get("matrix_view_url");
		setAttr("matrix_view_url", matrix_view_url);
		setAttr("has_viewurl", StrKit.notBlank(matrix_view_url));
		setAttr("matrix_view_servername", PropKit.use("system.properties").get("matrix_view_servername"));
		setAttr("matrix_url", PropKit.use("system.properties").get("matrix_url"));
		setAttr("kibana_url", PropKit.use("system.properties").get("kibana_url"));
//		setAttr("secretkey", PropKit.use("system.properties").get("secretkey"));
		renderJsp("/view/research/project.jsp");
	}
	public void centerProject() {
		setAttr("project", ResearchProject.dao.findById(getInt("projectid")));
		renderJsp("/view/research/center_project.jsp");
	}
	public void goEditPatient(){
		if(StringUtils.isNotEmpty(getPara("patientid"))) {
			// 跳用户编辑所需要的数据
			String patientid = getPara("patientid");
			ResearchTestgroupData patient = sv.getResearchTestgroupData(patientid);
			setAttr("patient", patient);
		}
		renderJsp("/view/research/editPatient.jsp");
	}

	public void savePatient(){
		try {
			ResearchTestgroupData data = getModel(ResearchTestgroupData.class, "", true);
			if(ResearchTestgroupData.dao.checkPatientid(data.getPatientid(), data.getPatientname())){
				renderJson(ResultUtil.fail(1, "病人编号已被占用"));
				return;
			}
			String patientidOld = getPara("patientidOld");
			sv.savePatient(data,patientidOld);
			renderJson(ResultUtil.success());
		}catch (Exception e){
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	public void toTestGroups(){
		Subject subject = SecurityUtils.getSubject();
		setAttr("edit_testgroup",subject.isPermitted("edit_testgroup"));
		setAttr("delete_testgroup",subject.isPermitted("delete_testgroup"));
		setAttr("add_form_to_testgroup",subject.isPermitted("add_form_to_testgroup"));
		setAttr("manage_project_dicom_images",subject.isPermitted("manage_project_dicom_images"));
		List<Record> recordList = sv.findTestGroups(getInt("projectid"));
		setAttr("groups",recordList);
		render("/view/research/testGroup.html");
	}
	
	public void getTestGroupsInfo(){
		renderJson(sv.getTestGroups(getInt("groupid")));
	}
	
	public void editProject() {
		Integer Id=getParaToInt("Id");
    	if(Id!=null) {
    		ResearchProject tm=ResearchProject.dao.findById(Id);
    		setAttr("tm", tm);
    	}
    	renderJsp("/view/admin/project/editProject.jsp");
	}
    
	public void toEditTestGroup() {
		Integer id=getInt("id");
		if(id!=null) {
			ResearchTestGroup eg=ResearchTestGroup.dao.findById(id);
			setAttr("eg", eg);
		}
		setAttr("project_id", getInt("projectid"));
		renderJsp("/view/admin/project/editTestGroup.jsp");
	}
     
	public void viewReport() {
		Integer fi_Id=getParaToInt("fi_Id");
		Integer formId=getParaToInt("formId");
		if(fi_Id!=null) {
			setAttr("fi_Id", fi_Id);
		}
		if(formId!=null) {
			setAttr("formId", formId);
		}
		renderJsp("/view/admin/project/view_report.jsp");
	}
     
     
	public void toGroupFormList() {
		setAttr("group", ResearchTestGroup.dao.findById(getInt("groupid")));
		renderJsp("/view/research/addFormToGroup.jsp");
	}
     
//     public void toFormListForGroup() {
//    	 Integer Id=getInt("groupid");
//    	 if(Id!=null) {
//    		 ResearchTestgroupData fi=ResearchTestgroupData.dao.findById(Id);
//    		 setAttr("fi", fi);
//    	 }
//    	 renderJsp("/view/research/formList.jsp");
//     }
     
	public void editProjectMember() {
		Integer taskId=getParaToInt("taskId");
		setAttr("taskId", taskId);
		renderJsp("/view/admin/project/projectMember.jsp");
	}
     
	public void editTskRole() {
		Integer id=getParaToInt("id");
		if(id!=null) {
			ResearchRole tr=ResearchRole.dao.findById(id);
			setAttr("tr", tr);
		}
		renderJsp("/view/admin/project/editProjectRole.jsp");
	}
     
	public void openSaveFormDialog(){
		
		if(StringUtils.isNotEmpty(getPara("id"))){
			ResearchForm temp=ResearchForm.dao.findById(getPara("id"));
			setAttr("form", temp);
		}
		setAttr("modelname", get("modelname"));
		renderJsp("/view/admin/project/saveForm.jsp");
	}
	
	public void openFormList(){
		renderJsp("/view/admin/project/formList.jsp");
	}
	
	
	public void toAddSRportToGroup(){
		renderJsp("/view/research/addSReportToGroup.jsp");
	}
	
    public void openWriteTemplate() {
		 Integer experience_group_id=getParaToInt("experience_group_id");
		 setAttr("experience_group_id", experience_group_id);
		 renderJsp("/view/research/writeTemplate.jsp");
	}
    public void openJoingroupApplyDetailsDlg() {
		 Integer report_order_id=getParaToInt("report_order_id");
		 setAttr("report_order_id", report_order_id);
		 renderJsp("/view/admin/project/joinGroupApplyDetails.jsp");
	}
    public void openEditAuthorityDlg() {
    	Integer id=getParaToInt("id");
    	if(id!=null) {
    		ResearchAuthority tj=ResearchAuthority.dao.findById(id);
    		
    		List<ResearchAuthorityResource> list=ResearchAuthorityResource.dao.find("SELECT * FROM research_authority_resource WHERE authority=?",id);
			String auids="";
			for (int i = 0; i < list.size(); i++) {
				if(i==0) {
					auids=String.valueOf(list.get(i).getResource());
				}else {
					auids+=","+list.get(i).getResource();
				}
			}
			setAttr("auids", auids);
    		setAttr("tj", tj);
    	}
    	renderJsp("/view/admin/project/editAuthority.jsp");
	}
    
    public void openProjectResource() {
    	Integer id=getParaToInt("id");
    	if(id!=null) {
    		ResearchResource rr=ResearchResource.dao.findById(id);
    		setAttr("rr", rr);
    	}
    	renderJsp("/view/admin/project/editResource.jsp");
	}
	
	public void toStudyInfo() {
		renderJsp("/view/research/studyInfo.jsp");
	} 
    
     //获取表单
	public void searchTaskForm() {
		String taskName=getPara("taskName");
		String experience_group_id=getPara("experience_group_id");
		List<Record> list=sv.searchTaskFormSv(taskName, experience_group_id);
		renderJson(list);
	}
   
     //保存课题信息
	public void saveProject(){
		try {
			ResearchProject tm=getModel(ResearchProject.class,"",true);
			Date now=new Date();
			if(tm.getId()!=null) {
				tm.setUpdatetime(now);
				tm.update();
				renderJson(ResultUtil.success());
			}else {
				tm.setCreatetime(now);
				tm.setDeleted("0");
				tm.remove("id").save();
				renderJson(ResultUtil.success());
			}
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
			e.printStackTrace();
		}
     }
     //查询课题信息
     public void searchProject() {
    	 String projectname=getPara("projectname");
    	 User user=(User) getSessionAttr("user");
    	 List<Record> list=sv.searchProjectSv(projectname);  
    	 List<Record> jurisdictionList=sv.findResearchUserRoleSv(user);
    	 for (int i = 0; i < list.size(); i++) {
    		 Record record=list.get(i);
			 for (int j = 0; j < jurisdictionList.size(); j++) {
				 if(record.get("id").equals(jurisdictionList.get(j).get("project_id"))&&"edit_project".equals(jurisdictionList.get(j).get("name"))) {
					 record.set("bjkt", "true");
				 }
				 if(record.get("id").equals(jurisdictionList.get(j).get("project_id"))&&"delete_project".equals(jurisdictionList.get(j).get("name"))) {
					 record.set("sckt", "true");
				 }
			}
		 }	 
    	 renderJson(list);
     }
     
     //查询实验组信息
     public void searchTestGroups() {
    	 renderJson(Db.find("select tg.*,u.name as creatorname from research_test_group tg left join users u on tg.creator=u.id "
    	 		+ "where tg.deleted!='1' and tg.project_id=?",getInt("projectId")));
     }
     
     //保存实验组信息
     public void saveTestGroup() {
    	 try {
			 ResearchTestGroup tg=getModel(ResearchTestGroup.class,"",true);
			 User user=(User) getSessionAttr("user");
			 if(sv.saveTestGroup(tg, user)){
				 renderJson(ResultUtil.success());
			 } else{
				 renderJson(ResultUtil.fail(""));
			 }
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
			e.printStackTrace();
		}
     }

	// 添加表单至实验组
	public void addFormToGroup() {
		try {
			ResearchTestgroupForm rtf = getModel(ResearchTestgroupForm.class, "", true);
			String res=sv.addFormToGroup(rtf);
			if(StrKit.equals(res, "1")){
				renderJson(ResultUtil.success());
			} else if(StrKit.equals(res, "2")){
				renderJson(ResultUtil.fail("保存失败，请重试，如果问题依然存在，请联系系统管理员！"));
			} else{
				renderJson(ResultUtil.fail("表单已经存在，请勿重复添加！"));
			}
		} catch (Exception e) {
			renderJson(ResultUtil.fail(e.getMessage()));
			e.printStackTrace();
		}
	}
	//从实验组移除表单
	public void removeFormFromGroup(){
		try {
			if(sv.removeFormFromGroup(getInt("id"))){
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(""));
			} 
		} catch (Exception e) {
			renderJson(ResultUtil.fail(e.getMessage()));
			e.printStackTrace();
		}
	}
   
//	public void searchExgroupTaskform() {
//		Integer experienceId = getParaToInt("experienceId");
//		Integer project_id = getParaToInt("project_id");
//		String taskName = getPara("taskName");
//		User user = (User) getSession().getAttribute("user");
//		List<Record> list = sv.searchExgroupTaskformSv(experienceId, taskName);
//
//		renderJson(list);
//	}
	
	public void findTestgroupFrom(){
		renderJson(sv.searchTestGroupformSv(getInt("groupid"), get("formname"),getBoolean("withContent",true)));
	}
	
//	public void getCompletedFrom(){
//		renderJson(ResearchForm.dao.findById(getInt("formid")));
//	}
	
	public void openForm(){
//		setAttr("formid", get("formid"));
//		setAttr("projectid", get("projectid"));
//		setAttr("dataid", "new_"+StrKit.getRandomUUID());
		renderJson(sv.openForm(getInt("dataid"), getInt("formid")));
	}
	
	public void formContent(){
		Integer orderid=getInt("orderid");
		Integer dcmstudypk=getInt("dcmstudypk"); 
		setAttr("groupid", get("groupid"));
		setAttr("formid", get("formid"));
		setAttr("projectid", get("projectid"));
		String dataid=get("dataid");
		setAttr("dataid", dataid);
		setAttr("status", get("status"));
		setAttr("type", get("type"));
		if(StrKit.notBlank(dataid)) {
			if(!dataid.startsWith("new-")) {//修改表单，获取病人信息，显示在表单中
				setAttr("data", ResearchTestgroupData.dao.findById(get("dataid")));
			} else if(orderid!=null){//选择检查，然后录入表单
				setAttr("data", sv.getStudyInfoByOrderid(orderid));
			} else if(dcmstudypk!=null) {//选择影像数据，然后录入表单
				setAttr("data", sv.getDicomInfoByStudypk(dcmstudypk));
			}
		}
		
		renderJsp("/view/research/form.jsp");
	}
     
     //删除课题
 	public void deleteProject(){
		try {
			Integer Id=getParaToInt("id");
			if(sv.deleteProject(Id)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
 	
 	//删除实验组
 	public void deleteTestGroup(){
		try {
			Integer Id=getParaToInt("id");
			if(sv.deleteTestGroup(Id)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
 	//删除表单与模板关联关系
// 	public void deleteExgroupTaskformRelation(){
//		try {
//			Integer Id=getParaToInt("id");
//			ResearchTestgroupForm etr=ResearchTestgroupForm.dao.findById(Id);
//			etr.deleteById(Id);
//			renderJson(ResultUtil.success());
//		} catch (Exception e) {
//			renderJson(ResultUtil.fail(-1, e.getMessage()));
//		}
//	}
 	
 	//删除表单数据
 	public void deleteFormData(){
		try {
			if(sv.deleteFormData(getInt("id"))){
				renderJson(ResultUtil.success());
			} else{
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}
 	
 	//批量删除表单数据
 	public void deleteFormDataBatch(){
		try {
			if(sv.deleteFormDataBatch(get("ids"))){
				renderJson(ResultUtil.success());
			} else{
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}
 	//给课题添加相关人员
 	public void saveProjectMember() {
 		try {
			ResearchUserRole tpr=getModel(ResearchUserRole.class,"",true);
			//校验添加的人员是否已经存在该课题下
			if(sv.checkPersonnel(tpr)) {
				tpr.remove("id").save();
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(1, "人员已经存在，请勿重复添加！！"));
			}
			
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
 	}
 	//删除人员
 	public void deleteProjectMember() {
 		try {
			Integer id=getParaToInt("id");
			ResearchUserRole.dao.deleteById(id);
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
 	}
 	//查询课题下相关人员
 	public  void searchProjectMembers() {
 		Integer taskId=getParaToInt("taskId");
 		renderJson(sv.searchProjectMembersSv(taskId));
 	}
 	
 	//保存角色下的人员
 	public void saveProjectRole() {
 		try {
			String rows = get("rows");
			String name=get("role_name");
			String describe=get("describe");
			Integer level=getParaToInt("user_level");
			Integer taskRoleId=getInt("taskRoleId");
			if(sv.saveProjectRole(rows,name,describe,taskRoleId,level)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
 	}
 	//查询角色信息
 	 public void searchProjectRole() {
		 String role_name=get("role_name");
		 User user=(User) getSessionAttr("user");
		 List<Record> list=sv.searchProjectRoleSv(role_name);
		 renderJson(list);
     }
 	 //查询角色下相关人员
 	 public void searchTaskPersonnel() {
 		 Integer roleId=getInt("roleId");
 		 renderJson(sv.searchTaskPersonnelSv(roleId));
 	 }
 	 //删除角色以及角色下相关人员
 	 public void deleteProjectRole() {
 		try {
			Integer id=getInt("id");
			if(sv.deleteProjectRole(id)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
 	 }
 	 //查询当前登录用户所属课题
 	 public void findProjectByUserId() {
 		User user = (User) getSession().getAttribute("user");
 		renderJson(sv.findProjectByUserId(user));
 	 }
 	 
 	 //保存课题表单
 	public void newForm() {
 		ResearchForm templet = getModel(ResearchForm.class,"", true);
 		SerializeRes res=(SerializeRes)getSessionAttr("locale");
 		String content=templet.getFormcontent();
 		content=content.replaceAll(getRequest().getContextPath()+"/image/image/", "image/image/");
 		templet.setFormcontent(content);
 		User user = (User) getSession().getAttribute("user");
 		templet.set("creator", user.getId());
 		templet.set("creatorname", user.getName());
 		
 		try {
 			int id =sv.newResearchForm(templet,res);
 			if (templet.getId()!=null) {
 				renderJson("id",id);
 			} else {
 				renderJson(ResultUtil.fail(-1, "添加模板失败！"));
 			}
 		} catch (Exception e) {
 			e.printStackTrace();
 			renderJson(ResultUtil.fail(-1, e.getMessage()));
 		}
 	}
 	
 	//更新课题表单
	public void updateForm() {
		ResearchForm templet = getModel(ResearchForm.class,"", true);
		SerializeRes res=(SerializeRes)getSessionAttr("locale");
		String content=templet.getFormcontent();
		content=content.replaceAll(getRequest().getContextPath()+"/image/image/", "image/image/");
		templet.setFormcontent(content);
		try {
			if (sv.updateResearchForm(templet,res)) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1, "模板保存失败!"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	//查询课题表单
	public void findResearchForm(){
		renderJson(sv.findResearchForm(get("name"),getBoolean("withContent", true)));
	}
	
	public void getResearchFormById(){
		renderJson(ResearchForm.dao.findById(getInt("id")));
	}
	
	/**
	 * 提交表单数据
	 */
	public void submitFormData() {
		String params = getPara("s");
		String info = getPara("info");
		String html=getPara("html");
		try {
			ResearchTestgroupData data=getModel(ResearchTestgroupData.class, "", true);
			ResultVO vo=sv.checkPatientInfo(info,data.getId());
			if(vo.getCode()!=0) {
				renderJson(vo);
			} else {
				User user = (User) getSession().getAttribute("user");
				sv.submitFormData(data, params, html, user,getRequest().getContextPath(),info);
				renderJson(ResultUtil.success(data));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1,e.getMessage()));
		}
	}
	
	//审核表单数据
	public void auditFormData() {
		String params = getPara("s");
		String info = getPara("info");
		String html=getPara("html");
		try {
			ResearchTestgroupData data=getModel(ResearchTestgroupData.class, "", true);
			ResultVO vo=sv.checkPatientInfo(info,data.getId());
			if(vo.getCode()!=0) {
				renderJson(vo);
			} else {
				User user = (User) getSession().getAttribute("user");
				sv.auditFormData(data, params, html, user,getRequest().getContextPath(),info);
				renderJson(ResultUtil.success(data));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1,e.getMessage()));
		}
	}
	
	//批量审核表单数据
	public void auditFormDataBatch() {
		String ids=get("ids");
		try {
			User user = (User) getSession().getAttribute("user");
			ResultVO vo=ResultUtil.success();
			sv.auditFormDataBatch(ids, user,vo);
			renderJson(vo);
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1,e.getMessage()));
		}
	}
	
	public void saveForm_printhtml(){
		Integer id=getParaToInt("id");
		String printhtml=getPara("printhtml");
		try {
			sv.saveForm_printhtml(id, printhtml);
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}
 	 
	//查询已审核通过的报告
    public void searchReport() {
	     Map<String,Object> hashmap=new HashMap<String,Object>();
	     Page<Record> page = sv.searchReport(new ParaKit(getParaMap()));
	     hashmap.put("total", page.getTotalRow());
	     hashmap.put("rows", page.getList());//rows键 存放每页记录 list  
	     renderJson("data", hashmap);
    }
    
    public void applySreport2Group() {
		try {
			//校验表单是否已经存在实验组下
			String str=sv.checkapplySreport2Group(getInt("groupid"),get("reports"));
			if(!str.equals("")) {
				renderJson(ResultUtil.fail(2, str));
			}else {
				sv.applySreport2Group(getInt("projectid"), getInt("groupid"),get("reports"),getDate("validDate"),getSessionAttr("user"));
				renderJson(ResultUtil.success());
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(e.getMessage()));
		}
    }
    
    //查询入组申请
    public void searchJoingroupApply() {
    	User user=(User) getSession().getAttribute("user");
    	String status = getPara("status");
    	String name = getPara("name");
    	String strTime = getPara("strTime");
    	String endTime = getPara("endTime");
    	renderJson(sv.searchJoingroupApply(user,status,name,strTime,endTime));
    }
    
    //审批报告申请
    public void approveApplyForJoinGroup() {
    	try {
			Date reportPrescription = getDate("reportPrescription");
			String datas = getPara("datas");
			Integer status=null;
			String note = getPara("note");
			if(datas!=null) {
				JSONArray array=JSONArray.parseArray(datas);
				for (int i = 0; i < array.size(); i++) {
					JSONObject object=array.getJSONObject(i);	
					ResearchJoingroupApply researchjoingroupapply=ResearchJoingroupApply.dao.findById(object.get("id"));
					researchjoingroupapply.setValidPeriod(reportPrescription);
					if(object.getInteger("status")==Sreport2GroupStatus.apply||object.getInteger("status")==Sreport2GroupStatus.reject) {
						status=2;
						researchjoingroupapply.setStatus(status);
						researchjoingroupapply.setReason(note);
						researchjoingroupapply.update();
					}
					
					if(status==Sreport2GroupStatus.pass) {
						//审批通过后，把该报告的结构化目标添加到对应的实验组下
						List<ResearchJoingroupApplyItems> reportorderlines =ResearchJoingroupApplyItems.dao.find("select * from research_joingroup_apply_items where app_id=?",researchjoingroupapply.getId());
						if(reportorderlines!=null&&reportorderlines.size()>0) {
							for (int j = 0; j < reportorderlines.size(); j++) {
								ResearchTestgroupData info = new ResearchTestgroupData();
								Report report=Report.dao.findById(reportorderlines.get(j).getReportId());
								info.setFormId(reportorderlines.get(j).getTemplateId());
								if(reportorderlines.get(j).getTemplateId()!=null) {
									SRTemplate srtemplate=SRTemplate.dao.findById(reportorderlines.get(j).getTemplateId());
									info.setFormName(srtemplate.getName());
								}
								info.setGroupId(researchjoingroupapply.getGroupId());
								info.setCreatetime(new Date());
								info.setDeleted("0");
								info.setType(2);
								info.setCreator(researchjoingroupapply.getApplicant());
								info.setHtml(report.getCheckresultHtml());
								info.setPrintHtml(report.getCheckdescHtml());
								info.setStatus(FormStatus.Preliminary);
								info.setReportid(reportorderlines.get(j).getReportId());
								info.remove("id").save();
							}
						}
					}
				}
			}
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
			e.printStackTrace();
		}
    }
    
    //保存规则
    public void saveRule(){
    	try {
			ResearchJoingroupRule taskrule=getModel(ResearchJoingroupRule.class, "", true);
			User user = (User) getSession().getAttribute("user");
			String datas=getPara("datas");
			if(sv.saveRuleSv(taskrule,user,datas)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail("请输入正确的正则表达式!!"));
			}
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
			e.printStackTrace();
		}
    }
    
    //查询规则
    public void searchRuleList() {
    	StringBuffer sb =new StringBuffer();
    	sb.append(" select tr.*,tm.name as project_name,eg.name as group_name,u.name as creator_name from research_joingroup_rule tr ");
    	sb.append("   left join research_project tm on tr.project_id=tm.id");
    	sb.append("   left join research_test_group eg on tr.group_id=eg.id");
    	sb.append("   left join users u on tr.creator=u.id");
    	renderJson(Db.find(sb.toString()));
    }
    
    //查询规则明细
    public void searchRuleItems() {
    	Integer task_rule_id=getInt("task_rule_id");
    	renderJson(sv.searchRuleItemsSv(task_rule_id));
    }
    
    public void findJoinGroupRuleById() {
    	Integer id = getParaToInt("id");
    	renderJson(ResearchJoingroupRule.dao.findById(id));
    }
    
    public void deleteRule() {
    	 Integer id = getParaToInt("id");	
    	 ResearchJoingroupRule.dao.deleteById(id);
    	 renderJson(ResultUtil.success());
    }
    
    public void deleteRuleItem() {
   	 Integer id = getParaToInt("id");	
   	 ResearchJoingroupRuleItem.dao.deleteById(id);
   	 renderJson(ResultUtil.success());
    }
    
    public void searchTestGroupDatas() {	
    	renderJson(sv.searchTestGroupDatas(getInt("groupid"),getInt("formid"),getInt("type")));
    }
    
    public void searchTestGroupDatas1() {	
    	renderJson(sv.searchTestGroupDatas1(getInt("groupid"),getInt("formid"),getInt("type"),get("patientid"),new ParaKit(getParaMap())));
    }
    
    public void searchSrtemplate() {
    	renderJson(Db.find(" select id,name,templatecontent from srtemplate"));
    }
    
//    public void searchDicExamitem() {
//    	renderJson(Db.find(" select * from dic_examitem"));
//    }
    
    public  void searchJoinGroupApplyItems() {
    	Integer report_order_id = getParaToInt("report_order_id");
    	String templateId = getPara("templateId");
    	
    	renderJson(sv.searchJoinGroupApplyItemsSv(report_order_id,templateId,getSessionAttr("syscode_lan")));
    }
    
    public void searchAuthority() {
		String jurisdictionName = getPara("jurisdictionName");
		User user=(User) getSessionAttr("user");
		List<Record> list=sv.searchAuthoritySv(jurisdictionName);
		renderJson(list);
    }
    
    public void saveResearchAuthority() {
    	 try {
			 ResearchAuthority tj=getModel(ResearchAuthority.class,"",true);
			 String rows = get("rows");
			 User user=(User) getSession().getAttribute("user");
			 if(sv.saveResearchAuthoritySv(tj,rows,user)) {
				 renderJson(ResultUtil.success());
			 }else {
				 renderJson(ResultUtil.fail("操作失败，请联系管理员！！"));
			 }
			  
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
			e.printStackTrace();
		}
    }
    
    public void deleteProjectAuthority() {
    	 Integer id = getParaToInt("id");	
    	 ResearchAuthority.dao.deleteById(id);
	   	 renderJson(ResultUtil.success());
    }
    
    public void serachRoleAuthority() {
    	String roleId = getPara("roleId");
    	renderJson(sv.serachRoleAuthoritySv(roleId));
    }
    
    public void roleJurisdictionBtn() {
    	User user=(User) getSession().getAttribute("user");
    	
    }
    
    //保存资源
    public void saveResource() {
    	ResearchResource researchresource=getModel(ResearchResource.class,"",true);
    	User user=(User) getSession().getAttribute("user");
		Date now=new Date();
		if(researchresource.getId()!=null&&!"".equals(String.valueOf(researchresource.getId()))) {
			researchresource.update();
			renderJson(ResultUtil.success());
		}else {
			researchresource.setCreatetime(now);
			researchresource.setCreator(String.valueOf(user.getId()));
			researchresource.remove("id").save();
			renderJson(ResultUtil.success());
		}
    }
    
    // 查询资源
    public void searchResource() {
    	File file = new File(PathKit.getWebRootPath()+"\\WEB-INF\\classes\\"+"research_project_source.json");
		String module=getPara("module");
		String str = "";
		try {
			String input = FileUtils.readFileToString(file, "GBK");//UTF-8 GBK
			if(StringUtils.isNotBlank(module)) {
				JSONArray jsonArray = JSONObject.parseArray(input);
				JSONArray returnArray = new JSONArray();
				for(Object json:jsonArray) {
					JSONObject jsonObject = (JSONObject) json;
					if(jsonObject.containsKey("module")&&StringUtils.equals(jsonObject.getString("module"), module)) {
						returnArray.add(jsonObject);
					} 
				}
				str = returnArray.toString();
			} else {
				str = input;
			}
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
		renderJson(str); 
    }
    
    //删除资源
    public void deleteResource() {
    	Integer id = getParaToInt("id");	
   	    ResearchResource.dao.deleteById(id);
	   	renderJson(ResultUtil.success());
    }
    
    //拒绝入组申请
    public void refuseApplyForJoinGroup() {
    	try {
			String datas = getPara("datas");
			if(datas!=null) {
				sv.refuseApplyForJoinGroupSv(datas);
				renderJson(ResultUtil.success());
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(e.getMessage()));
		}
    }
    
    //根据id查询入组申请明显
    public void findApplyItemsById() {
    	Integer report_id = getParaToInt("report_id");
    	Report report=Report.dao.findById(report_id);
    	HashMap<String,String> hashmap=new HashMap<String,String>();
    	hashmap.put("html", report.getCheckdescHtml());
    	renderJson(ResultUtil.success(hashmap));
    }
    
    //根据表单ID查询表单内容
    public void getFormById() {
//    	Integer id = getParaToInt("id");
//    	ResearchForm researchform=ResearchForm.dao.findById(id);
//    	HashMap<String,String> hashmap=new HashMap<String,String>();
//    	hashmap.put("print_html", researchform.getFormcontent());
    	renderJson(ResearchForm.dao.findById(getInt("id")));
    }
    
    //根据报告ID查询报告信息
    public  void findReportByFormId() {
    	Integer id = getParaToInt("id");
    	Report report=Report.dao.findById(id);
    	HashMap<String,String> hashmap=new HashMap<String,String>();
    	hashmap.put("checkdesc_html", report.getCheckdescHtml());
    	renderJson(ResultUtil.success(hashmap));
    }
    
	//打印表单页面
	public void printFormData() {
		setAttr("dataid", getParaToInt("dataid"));
//		setAttr("issr", getPara("issr"));
		setAttr("fontSize", getParaToInt("fontSize"));
		renderJsp("/view/research/print.jsp");
	}
	
	public void preview() {
		try {
			HttpServletRequest request = getRequest();
			render(new PdfRender(psv.makeFormDataAsPdf(getInt("dataid"), request.getServerPort())));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void cancelAuditFormData() {
		Integer id = getParaToInt("id");
		ResearchTestgroupData data=ResearchTestgroupData.dao.findById(id);
		data.setStatus(FormStatus.Preliminary_review_reject);
		data.update();
		renderJson(ResultUtil.success());
	}
	
    public void searchStudyInfo() {
    	Map<String,Object> hashmap=new HashMap<String,Object>();
    	Page<Record> page =  sv.searchStudyInfo(new ParaKit(getParaMap()));
    	hashmap.put("total", page.getTotalRow());
    	hashmap.put("rows", page.getList());//rows键 存放每页记录 list  
    	renderJson("data", hashmap);
    }
    
    public void toGroupInfo() {
    	Integer groupid= getInt("groupid");
    	setAttr("groupid", groupid);
//    	if(groupid!=null) {
//    		
//    	}
    	render("/view/research/groupInfo.html");
    }
    
    public void findGroupPatient() {
    	renderJson(sv.findGroupPatient(getInt("groupid")));
    }
    
    public void showPatientStudyAndFormData() {
    	setAttr("groupid", get("groupid"));
    	setAttr("patientid", get("patientid"));
    	setAttr("ctx", getRequest().getContextPath());
    	render("/view/research/patientStudyAndFormData.html");
    }
    
    public void findStudys() {
    	renderJson(sv.findStudys(get("patientid")));
    }
    
    public void findDicomInfo() {
    	renderJson(sv.findDicomInfo(new ParaKit(getParaMap())));
    }
    
    public void getGroupGenderRatio() {
    	try {
			renderJson(ResultUtil.success(sv.getGroupGenderRatio(getInt("groupid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(e.getMessage()));
		}
    }
    
    public void getGroupAgeDistribution() {
    	try {
			renderJson(ResultUtil.success(sv.getGroupAgeDistribution(getInt("groupid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(e.getMessage()));
		}
    }
    
    public void openModelDialog(){
		render("/view/admin/project/models.html");
	}
    /*
    	参数 ：type
    	data：导出表单数据
    	temp：导出excel模板
     */
    
    
    public void exportFormData() {
		JSONObject obj= JSONObject.parseObject(get("sheets"));
		String sheetname= obj.getString("sheetname");
		String uid= obj.getString("uid");
		String filename=sheetname+(uid!=null?("("+uid+")"):"")+".xlsx";
		try {
			filename = new String(filename.replace(" ", "_").getBytes("UTF-8"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SXSSFWorkbook wb = null;
		if("temp".equals(get("type"))) {
			wb=sv.downloadFormTemp(obj);
		} else {
			wb=sv.downloadFormData(getInt("groupid"),getInt("formid"),obj);
		}
		HttpServletResponse response = getResponse();
        response.setContentType("application/octet-stream;charset=UTF-8");//ISO8859-1
        response.setHeader("Content-Disposition", "attachment;filename="+ filename);
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        try {
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
    
    public void importFormData(){
		try {
			User user = (User) getSession().getAttribute("user");
			UploadFile file=getFile("excel_file","/xlsx");
			ResultVO vo=sv.importFormData(file.getFile(),getInt("groupid"),user);
//			FileUtils.deleteQuietly(file.getFile());
			renderJson(vo);
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
    
    public void getFormData() {
    	try {
			renderJson(ResultUtil.success(sv.getFormData(getInt("dataid"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
    }

    public void getMatrixToken() {
    	try {
    		ResultVO vo=ResultUtil.fail(-1,"未知错误！");
    		User user = (User) getSession().getAttribute("user");

    		Map<String, String> heads=new HashMap<String, String>();
			heads.put("content-type", "application/json");

			JSONObject data=new JSONObject();
			data.put("username", user.getMatrixUsername());
			data.put("key", PropKit.use("system.properties").get("secretkey"));
			log.info(data.toJSONString());

			String result=HttpKit.post(PropKit.use("system.properties").get("matrix_url")+"/cas/token", data.toJSONString(),heads);
			if(StrKit.notBlank(result)) {
				JSONObject json=JSONObject.parseObject(result);
				if("0".equals(json.getString("code"))) {
					data.put("sessionid", json.getString("data"));
					vo=ResultUtil.success(data);
				} else {
					vo=ResultUtil.fail(-1,json.getString("message"));
				}
			} else {
				vo=ResultUtil.fail(-1,"请求Matrix错误！");
			}
			renderJson(vo);
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
    }


	public void findSRTemplateAndComponent(){
		String name=getPara("name");
		renderJson(sv.findSRTemplateAndComponent(name));
	}

	public void delFormAndComponent(){
		try {
			if (sv.delFormAndComponent(getPara("resepids"), getPara("compids"))) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1, "删除失败"));
			}
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void exportFormTemplate(){
		try {

			String rids=getPara("rids");
			String cids=getPara("cids");
			String componentuids="";
			String section_uids="";
			ArrayList<String> images=new ArrayList<String>();

			Document doc= DocumentHelper.createDocument();
			Element roote=doc.addElement("rootResearchForm","xsd:http://www.w3.org/2001/XMLSchema").addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

			if(StringUtils.isNotEmpty(rids)){

				Element srtemplatesele=roote.addElement("ResearchForm");

				rids=rids.substring(0, rids.length()-1);
				List<ResearchForm> researchForms=sv.findResearchFormByIds(rids);

				for(ResearchForm form: researchForms){

					Element researchForm=srtemplatesele.addElement("ResearchForm");

					researchForm.addElement("name").addText(form.getName());
					researchForm.addElement("maprule").addText(form.getMaprule());
					researchForm.addElement("content").addText(Base64.encodeBase64String(form.getFormcontent().getBytes("UTF-8")));

					com.itextpdf.html2pdf.jsoup.nodes.Document jsoupdoc= Jsoup.parse(form.getFormcontent());
					//获取模板中的组件
					Elements elements=jsoupdoc.select("[name$=Component][uid]");
					for(com.itextpdf.html2pdf.jsoup.nodes.Element element : elements){
						componentuids+="'"+element.attr("uid")+"',";
					}
					//获取模板中的图片，排除条形码图片
					elements=jsoupdoc.select("img:not([barcodetype])");
					for(com.itextpdf.html2pdf.jsoup.nodes.Element element : elements){
						images.add(element.attr("src"));
					}
					//获取模板中的章节id
//					elements=jsoupdoc.select("[sectionuid]");
//					for(com.itextpdf.html2pdf.jsoup.nodes.Element element : elements){
//						section_uids+="'"+element.attr("sectionuid")+"',";
//					}
				}
				if(StrKit.notBlank(section_uids)){
					section_uids=section_uids.substring(0, section_uids.length()-1);
				}
			}


			if(StringUtils.isNotEmpty(componentuids)){
				componentuids=componentuids.substring(0, componentuids.length()-1);
			}

			if(StrKit.notBlank(cids)||StrKit.notBlank(componentuids)){
				Element componentsele=roote.addElement("components");
				cids=StrKit.notBlank(cids)?cids.substring(0, cids.length()-1):null;
				List list=sv.findSRComponentByIdOrUid(cids, componentuids);
				List<SRComponent> components=(ArrayList<SRComponent>)list.get(0);
				HashMap<String, ArrayList<Srcomponentoption>> map=(HashMap<String, ArrayList<Srcomponentoption>>)list.get(1);

				for(SRComponent component:components){
					Element componentele=componentsele.addElement("component");
					componentele.addElement("uid").addText(component.getUid());
					componentele.addElement("name").addText(component.getName());
					componentele.addElement("code").addText(StringUtils.trimToEmpty(component.getCode()));
					componentele.addElement("standardcode").addText(StringUtils.trimToEmpty(component.getStandardcode()));
					componentele.addElement("type").addText(component.getType()+"");
					componentele.addElement("unit").addText(StringUtils.trimToEmpty(component.getUnit()));
					componentele.addElement("defaultvalue").addText(StringUtils.trimToEmpty(component.getDefaultvalue()));
					componentele.addElement("html").addText(org.apache.commons.codec.binary.Base64.encodeBase64String(component.getHtml().getBytes("UTF-8")));
					componentele.addElement("classifyid").addText(component.getClassifyId()!=null?component.getClassifyId()+"":"");
					componentele.addElement("required").addText(StringUtils.trimToEmpty(component.getRequired()));
					componentele.addElement("summary_text").addText(StrKit.notBlank(component.getSummaryText())? Base64.encodeBase64String(component.getSummaryText().getBytes("UTF-8")):"");
					Element optionsele=componentele.addElement("options");
					ArrayList<Srcomponentoption> srcomponentoptions=map.get(component.getId()+"");

					if(srcomponentoptions!=null){
						for(Srcomponentoption option:srcomponentoptions){
							Element optionele=optionsele.addElement("option");
							optionele.addElement("code").addText(StringUtils.trimToEmpty(option.getCode()));
							optionele.addElement("value").addText(StringUtils.trimToEmpty(option.getValue()));
							optionele.addElement("displayname").addText(StringUtils.trimToEmpty(option.getDisplayname()));
							optionele.addElement("standard_code").addText(StringUtils.trimToEmpty(option.getStandardCode()));
							optionele.addElement("opindex").addText(option.getOpindex()!=null?option.getOpindex()+"":"");
							optionele.addElement("sectionuid").addText(StringUtils.trimToEmpty(option.getSectionuid()));
							optionele.addElement("defaultoption").addText(StringUtils.trimToEmpty(option.getDefaultoption()));
							optionele.addElement("mutex").addText(StringUtils.trimToEmpty(option.getMutex()));
							optionele.addElement("color").addText(StringUtils.trimToEmpty(option.getColor()));
						}
					}
				}

			}

			String filename=PropKit.use("system.properties").get("tempdir")+"\\"+UUID.randomUUID().toString()+".xml";
			log.info("filename2="+filename);
			File xml=new File(filename);
			FileUtils.writeStringToFile(xml, doc.asXML(), "UTF-8");
			renderFile(xml);
//			FileUtils.deleteQuietly(xml);


		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}

	public void importFormTemplate(){
		try {
			User user = (User) getSession().getAttribute("user");

			UploadFile file=getFile("researchFormxml","UTF-8");
			if(file!=null){

				SAXReader reader = new SAXReader();
				Document doc=reader.read(new FileInputStream(file.getFile()));
				Element root = doc.getRootElement();
				Element components =root.element("components");
				if(components!=null){
					List componentlist=components.elements("component");
					for(int i=0;i<componentlist.size();i++){
						Element componentele=(Element)componentlist.get(i);
						SRComponent src=new SRComponent();
						src.setUid(componentele.elementText("uid"));
						log.info(componentele.elementText("name"));
						src.setName(componentele.elementText("name"));
						src.setCode(componentele.elementText("code"));
						src.setStandardcode(componentele.elementText("standardcode"));
						src.setType(Integer.valueOf(componentele.elementText("type")));
						if(StringUtils.isNotEmpty(componentele.elementText("unit"))){
							src.setUnit(componentele.elementText("unit"));
						}
						if(StringUtils.isNotEmpty(componentele.elementText("defaultvalue"))){
							src.setDefaultvalue(componentele.elementText("defaultvalue"));
						}
						src.setHtml(new String(Base64.decodeBase64(componentele.elementText("html")),"UTF-8"));
						if(StringUtils.isNotEmpty(componentele.elementText("classifyid"))){
							src.setClassifyId(Integer.valueOf(componentele.elementText("classifyid")));
						}
						if(StringUtils.isNotEmpty(componentele.elementText("required"))){
							src.setRequired(componentele.elementText("required"));
						}
						if(StringUtils.isNotEmpty(componentele.elementText("summary_text"))){
							src.setSummaryText(new String(Base64.decodeBase64(componentele.elementText("summary_text")),"UTF-8"));
						}

						src.setCreator(user.getId());
						src.setCreatorname(user.getName());

						List optionlist=componentele.element("options").elements("option");
						List<Srcomponentoption> srcomponentoptions=new ArrayList<Srcomponentoption>();
						for(int j=0;j<optionlist.size();j++){
							Element optionele=(Element)optionlist.get(j);
							Srcomponentoption srco=new Srcomponentoption();
							srco.setUid(UUID.randomUUID().toString());
							srco.setComponentUid(src.getUid());
							if(StringUtils.isNotEmpty(optionele.elementText("code"))){
								srco.setCode(optionele.elementText("code"));
							}
							if(StringUtils.isNotEmpty(optionele.elementText("value"))){
								log.info(optionele.elementText("value"));
								srco.setValue(optionele.elementText("value"));
							}
							if(StringUtils.isNotEmpty(optionele.elementText("displayname"))){
								srco.setDisplayname(optionele.elementText("displayname"));
							}
							if(StringUtils.isNotEmpty(optionele.elementText("standard_code"))){
								srco.setStandardCode(optionele.elementText("standard_code"));
							}
							srco.setOpindex(Integer.valueOf(optionele.elementText("opindex")));
							if(StringUtils.isNotEmpty(optionele.elementText("sectionuid"))){
								srco.setSectionuid(optionele.elementText("sectionuid"));
							}
							if(StringUtils.isNotEmpty(optionele.elementText("defaultoption"))){
								srco.setDefaultoption(optionele.elementText("defaultoption"));
							}
							if(StringUtils.isNotEmpty(optionele.elementText("mutex"))){
								srco.setMutex(optionele.elementText("mutex"));
							}
							if(StringUtils.isNotEmpty(optionele.elementText("color"))){
								srco.setColor(optionele.elementText("color"));
							}
							srcomponentoptions.add(srco);
						}

						sv.insertSRComponent(src,srcomponentoptions);
					}
				}

				Element srtemplates =root.element("ResearchForm");
				if(srtemplates!=null){
					List srtemplatelist=srtemplates.elements("ResearchForm");
					List<ResearchForm> researchFormlist=new ArrayList<ResearchForm>();
					for(int i=0;i<srtemplatelist.size();i++){
						//ResearchForm和模板表相似的字段比较少
						Element srtemplateele=(Element)srtemplatelist.get(i);
						ResearchForm resForm=new ResearchForm();
						resForm.setName(srtemplateele.elementText("name"));
						resForm.setMaprule(srtemplateele.elementText("maprule"));
						resForm.setFormcontent(new String(Base64.decodeBase64(srtemplateele.elementText("content")),"UTF-8"));
						resForm.setCreator(user.getId());
						resForm.setCreatorname(user.getName());
						researchFormlist.add(resForm);
					}

					sv.insertResearchForm(researchFormlist);
				}

				Element imgsele =root.element("images");
				if(imgsele!=null){
					String basedir=getSession().getServletContext().getRealPath("");
					List imagelist=imgsele.elements("image");
					for(int i=0;i<imagelist.size();i++){
						Element imgele=(Element)imagelist.get(i);
						File img=new File(basedir+"\\"+imgele.elementText("path"));
						if(!img.exists()){
							FileUtils.writeByteArrayToFile(img, Base64.decodeBase64(imgele.elementText("content")));
						}
					}
				}

			}
			if(file!=null){
				FileUtils.deleteQuietly(file.getFile());
			}
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}

	public void findUserLevel(){
		try{
			renderJson(sv.findUserLevel());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
}
