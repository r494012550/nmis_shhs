package com.healta.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.healta.license.VerifyLicense;
import com.healta.model.Admission;
import com.healta.model.Client;
import com.healta.model.DicEmployee;
import com.healta.model.EmergencyDefaultInfo;
import com.healta.model.EmergencyStudyitem;
import com.healta.model.Eorderitem;
import com.healta.model.Filter;
import com.healta.model.Inquiry;
import com.healta.model.Patient;
import com.healta.model.PreviousHistory;
import com.healta.model.Report;
import com.healta.model.StudyImage;
import com.healta.model.Studyitem;
import com.healta.model.Studyorder;
import com.healta.model.Unmatchstudy;
import com.healta.model.User;
import com.healta.model.UserProfiles;
import com.healta.plugin.activemq.ActiveMQ;
import com.healta.plugin.activemq.MQSubject;
import com.healta.plugin.activemq.PacsUpdateOrder;
import com.healta.service.FrontCommonService;
import com.healta.service.RegisterService;
import com.healta.util.IDUtil;
import com.healta.util.IPKit;
import com.healta.util.ParaKit;
import com.healta.util.ResultUtil;
import com.healta.util.WebSocketUtils;
import com.healta.vo.WebsocketVO;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.MultipartRequest;
import com.jfinal.upload.UploadFile;

public class RegisterController extends Controller {
	private static final Logger log = Logger.getLogger(RegisterController.class);
	private static RegisterService sv=new RegisterService();
	private static FrontCommonService frontCommonsv = new FrontCommonService();
	
	public void index(){
		User user = (User) getSession().getAttribute("user");
		setAttr("username", user.getUsername());
		//setAttr("rolename", "?????????");
		setAttr("name", user.getName());
		setAttr("user_id", user.getId());
//		setAttr("projecturl", IPKit.getServerIP(getRequest()));
		setAttr("projecturl", getRequest().getScheme()+"://"+IPKit.getServerIP(getRequest())+":"+getRequest().getServerPort()+getRequest().getContextPath());
		try {
			setAttr("printername1",Base64.encodeBase64String("?????????".getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UserProfiles profiles=(UserProfiles)getSession().getAttribute("myprofiles");
		if(profiles!=null){
			setAttr("up", profiles);
			setAttr("beforeSaveScan", StringUtils.equals("1", profiles.getBeforeSaveScan()));
			setAttr("afterSavePrint", StringUtils.equals("1", profiles.getAfterSavePrint()));
			setAttr("copies", profiles.getStudyformPrintCopies()==null?1:profiles.getStudyformPrintCopies());
/*			setAttr("reg_third_panel_collapsed", "1".equals(up.getStr("reg_third_panel_collapsed"))?"true":"false");
			setAttr("reg_samename_panel_collapsed", "1".equals(up.getStr("reg_samename_panel_collapsed"))?"true":"false");*/
		}
		setAttr("enable_workforce", VerifyLicense.hasFunction("workforce"));
		renderJsp("/view/front/register/register.jsp");
	}
	
	public void centerRegister() {
		User user = (User) getSession().getAttribute("user");
		DicEmployee employee = DicEmployee.dao.findById(user.getEmployeefk());
		setAttr("user_institution", employee.getInstitutionid());
		setAttr("username", user.getUsername());
		setAttr("user_id", user.getId());
		setAttr("ip", IPKit.getIP(getRequest()));
		setAttr("enable_scan_module", StrKit.equals("1", PropKit.use("system.properties").get("enable_scan_module")));
		setAttr("auto_launch_scaner", StrKit.equals("1", PropKit.use("system.properties").get("auto_launch_scaner")));
		setAttr("scanurl", getRequest().getScheme()+"://"+IPKit.getServerIP(getRequest())+":"+getRequest().getServerPort()+getRequest().getContextPath());
		renderJsp("/view/front/register/center_register.jsp");
	}
	
	public void importPatient(){
		renderJsp("/view/front/register/importPatient.jsp");
	}
	
	/**
	 *  ??????????????????????????????????????????
	 */
	public void registerSearch() {
		setAttr("hasSchedule",VerifyLicense.hasModule("schedule"));
		setAttr("quicksearch",VerifyLicense.hasFunction("quicksearch"));
		renderJsp("/view/front/register/registerSearch.jsp");
	}
	
	public void studyOrders() {
		LocalDate today=LocalDate.now();
		setAttr("today", today.format(DateTimeFormatter.ISO_LOCAL_DATE));
		setAttr("yesterday", today.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
		renderJsp("/view/front/register/studyOrders.jsp");
	}
	
	//?????????????????????
	public void openScheduleDlg() {
//		if(StringUtils.isNotEmpty(getPara("studyorderpkid"))) {
//			Studyorder studyorder=Studyorder.dao.findById(getParaToInt("studyorderpkid"));
//			String priority="";
//			String appdept = "";
//			String appdoctor="";
//			if(StringUtils.isNotBlank(studyorder.getPriority())) {
//				priority=SyscodeKit.getInstance().getCodeDisplay("0003",studyorder.getPriority(),getSessionAttr("language"));
//			}
//			if(StringUtils.isNotBlank(studyorder.getAppdept())) {
//				appdept=DicDepartment.dao.findById(studyorder.getAppdept()).getDeptname();
//			}
//			if(StringUtils.isNotBlank(studyorder.getAppdoctor())) {
//				appdoctor=DicEmployee.dao.findById(studyorder.getAppdoctor()).getName();
//			}
//			setAttr("studyorderkid",studyorder.getId());
//			setAttr("studyid",studyorder.getStudyid());
//			setAttr("priority",priority);
//			setAttr("modality_type",studyorder.getModalityType());
//			setAttr("modalityid",studyorder.getModalityid());
//			setAttr("appdept",appdept);
//			setAttr("appdoctor",appdoctor);
//			setAttr("remark",studyorder.getRemark());
//			setAttr("status",studyorder.getStatus());
//		}	
		
		setAttr("order", sv.getStudyOrderById(getParaToInt("studyorderpkid")));
		
		renderJsp("/view/front/register/regToSchedule.jsp");
	}
	
	
	//??????????????????????????????
	public void openFilterSaveDialog() {
		renderJsp("/view/front/register/filter.jsp");
	}
	//????????????????????????
	public void openFilterManageDialog() {
		renderJsp("/view/front/register/filterManage.jsp");
	}
	
	//????????????????????????
	public void openSameNameDlg() {
		renderJsp("/view/front/register/sameName.jsp");
	}
	
	//???????????????????????????
	public void goToUploadScanimg() {
		setAttr("orderid", getPara("orderid"));
		setAttr("studyid", getPara("studyid"));
		renderJsp("/view/front/register/uploadScanimg.jsp");
	}
	
	/**
	 * ???????????????????????????
	 */
	public void getStatus() {
		try {
			renderJson(ResultUtil.success(sv.getStatus( getParaToInt("orderid"))));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	/**
	 *  ???????????????????????????
	 */
	public void register() {
		try {
			User user = (User) getSession().getAttribute("user");
			
			Patient patient = getModel(Patient.class, "", true);
			patient.setId(getParaToInt("patient_id"));

			Admission admission = getModel(Admission.class, "", true);
			admission.setId(getParaToInt("admission_id"));

			Studyorder so = getModel(Studyorder.class, "", true);
			so.setId(getParaToInt("studyorder_id"));
			
			String itemstr = getPara("itemsvalue");//????????????
			String patientremark = getPara("patientremark");
			String admissionremark = getPara("admissionremark");
			String studyorderremark = getPara("studyorderremark");
			Integer unmatchkid = getParaToInt("unmatchkid"); //???????????????id
			
			if (patient.getId() != null && admission.getId() != null && so.getId() != null) {
			    // ??????
				if (sv.modifyRegister(patient,so,admission,itemstr,
						patientremark,admissionremark,studyorderremark,user,getParaValues("app_scan_img"))) {
					renderJson(ResultUtil.success
							(new Record().set("patientid", patient.getPatientid())
									.set("studyid", so.getStudyid())
									.set("admissionid", admission.getAdmissionid())
									.set("orderid", so.getId())));
				} else {
					renderJson(ResultUtil.fail(-1,"???????????????????????????????????????????????????????????????????????????"));
				}
			} else {
			    // ??????
				if (sv.addRegister(patient,so,admission,itemstr,
						patientremark,admissionremark,studyorderremark,user,unmatchkid,getParaValues("app_scan_img"))) {
					renderJson(ResultUtil.success
							(new Record().set("patientid", patient.getPatientid())
									.set("studyid", so.getStudyid())
									.set("admissionid", admission.getAdmissionid())
									.set("orderid", so.getId())));
				} else {
					renderJson(ResultUtil.fail(-1,"???????????????????????????????????????????????????????????????????????????"));
				}	
			}	
			
		} catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
		
	}
	
	/**
	 * ?????????????????????????????????  
	 * ???????????? patient ?????????????????????form(load, row)??????????????????patient???????????????
	 */
	public void checkSameName() {
		try{
			renderJson(sv.checkSameName(getPara("patientname"), getPara("patientid"), getPara("hospitalizeNo")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//??????patient??????
	public void findPatient() {
		try{
			renderJson(sv.findPatient(getPara("patientid"), getPara("patientname")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//??????????????????????????????????????????????????????
	public void findPatAndAdmission() {
		String patientid=getPara("patientid");
		String patientname=getPara("patientname");
		try{
			renderJson(sv.findPatAndAdmission(patientid,patientname,getPara("studyid"),getSessionAttr("syscode_lan")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//??????????????????????????????
	public void findRecycledPat() {
		String patientid = getPara("patientid");
		String patientname = getPara("patientname");
		try{
			renderJson(sv.findRecycledPat(patientid, patientname));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//????????????????????????
	public void checkSameNO() {
		try {
			renderJson(sv.checkSameNO(getPara("inno"),getPara("outno")));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//?????????????????????????????????
	public void getStudyDetail(){
		try {
			renderJson(sv.getStudyDetail(getParaToInt("patientkid")));
		}catch(Exception e){
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//???????????????Admission??????
	public void getAdmission() {
		try {
			renderJson(sv.getAdmission(getParaToInt("patientkid"),getSessionAttr("syscode_lan")));
		}catch(Exception e){
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//??????????????????
	public void findStudyorder(){
		try {
			log.info("Quick Search Content:" + getPara("quicksearchcontent")+";Quick Search Name:"+getPara("quicksearchname"));
			Page<Record> page = sv.findStudyorder(getParaMap(), getSessionAttr("syscode_lan"));
			Map<String, Object> jsonMap = new HashMap<String, Object>();//??????map  
            jsonMap.put("total", page.getTotalRow());//total??? ??????????????????????????????  
            jsonMap.put("rows", page.getList());//rows??? ?????????????????? list  
			renderJson("data", jsonMap);
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 *  ????????????????????????????????????
	 */
	public void findStudyorder_FromThridIS() {
		try {
			renderJson(sv.findStudyorder_FromThridIS(new ParaKit(getParaMap())));
		}catch(Exception e){
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	public void getStudyItem_his(){
		try {
			renderJson(Db.find(Db.getSql("reg.his_orders_item"),getParaToInt("id")));
			//renderJson(Db.use("oracle_his2").find(Db.getSql("reg.his_orders_item"),getParaToInt("id")));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	
	//??????????????????
	public void getStudyItem(){
		try {
			renderJson(Studyitem.dao.find("select * from studyitem where orderid=?",getParaToInt("orderid")));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//????????????
	public void cancelStudyOrder() {
		try {
			User user = (User) getSession().getAttribute("user");
			int orderid=getParaToInt("orderid");
			if(sv.cancelStudyOrder(orderid,user)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//????????????
	public void deleteStudyOrder(){
		try{
			User user = (User) getSession().getAttribute("user");
			int orderid=getParaToInt("orderid");
			if(sv.deleteStudyOrder(orderid,user)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		}
		catch(Exception e){
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//??????????????????
	public void disassociationAdmission() {
		boolean result = Db.tx(() -> {
			boolean state = true;
			Record other = new Record();
			String patientId = IDUtil.getPatientID();
			String admissionId = IDUtil.getAdmissionID();
			Studyorder studyOrder = Studyorder.dao.findById(getParaToInt("orderid"));
			
			Patient patient = Patient.dao.findById(studyOrder.getPatientidfk());
			other.set("patientid", patient.getPatientid());
			other.set("patientname", patient.getPy());
			patient.setPatientid(patientId);
			state = state && patient.remove("id").save();
			
			Admission admission = Admission.dao.findById(studyOrder.getAdmissionidfk());
			admission.setAdmissionid(admissionId);
			admission.setPatientidfk(patient.getId());
			state = state && admission.remove("id").save();
			
			studyOrder.setAdmissionidfk(admission.getId());
			studyOrder.setPatientidfk(patient.getId());
			studyOrder.setPatientid(patientId);
			state = state && studyOrder.update();
			
			if(state) {
				ActiveMQ.sendObjectMessage(new PacsUpdateOrder(patient, null, null, other, "patientMerge"), MQSubject.PACSUPDATE.getSendName(), StrKit.getRandomUUID(), 4);
			}
			return state;
		});
		renderJson(result ? ResultUtil.success() : ResultUtil.fail(""));
	}
	
	//????????????
	public void deletePatient() {
		try {
			User user = (User) getSession().getAttribute("user");
			renderJson(ResultUtil.success(sv.deletePatient(getParaToInt("patientidfk"),user)));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	

	
	//????????????
	public void mergePat() {
		
		try{
			User user = (User) getSession().getAttribute("user");
			if(sv.mergePat(getParaToInt("fromid"),getParaToInt("toid"),getPara("topatientid"),user)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * ????????????
	 */
	public void cancelMerge() {
		try{
			User user = (User) getSession().getAttribute("user");
			if(sv.cancelMerge(getParaToInt("id"),user)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	
	
	//????????????????????????
	public void goMergePatient() {
		renderJsp("/view/front/register/mergePatient.jsp");
	}
	
	/**
	 *  ???????????????????????????
	 */
	public void goCancelMerge() {
		renderJsp("/view/front/register/cancelMerge.jsp");
	}
	
	//????????????????????????
	public void goReassignStudy() {
		setAttr("patient",Patient.dao.findById(getParaToInt("patientpkid")));
		setAttr("admission",Admission.dao.findById(getParaToInt("admissionpkid")));
		setAttr("studyorder", Studyorder.dao.findById(getParaToInt("studyorderpkid")));
		renderJsp("/view/front/register/reassignStudy.jsp");
	}
	
	//????????????????????????
	public void goChoosePatient() {
		renderJsp("/view/front/register/choosePatient.jsp");
	}
	
	public void goChooseAdmission() {
		setAttr("patientidfk",getParaToInt("patientidfk"));
		renderJsp("/view/front/register/chooseAdmission.jsp");
	}
	
	public void findApplication() {
		try {
			Record record = new Record();
			record.set("status", getPara("status"));
//			record.set("apppatientsource", getPara("apppatientsource")); 
			record.set("patientname", getPara("patientname"));
			record.set("patientid", getPara("patientid"));
//			record.set("appaccession_no", getPara("appaccession_no"));
//			record.set("cardno", getPara("cardno"));
			record.set("inno", getPara("inno"));
			record.set("outno", getPara("outno"));	
			record.set("studyid", getPara("studyid"));
			record.set("modality_type", getPara("modality_type"));
			record.set("appdept", getPara("appdept"));
			record.set("appdoctor", getPara("appdoctor"));
			
			record.set("appdate", getPara("appdate"));
			record.set("datefrom", getPara("appdatefrom"));
			record.set("dateto", getPara("appdateto"));	
			renderJson(sv.findApplication(record,getSessionAttr("syscode_lan")));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getOrderItem(){
		try {
			List<Eorderitem> items=
					Eorderitem.dao.find("select * from eorderitem where orderid='"+getParaToInt("orderid")+"'");
			renderJson(items);
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * ??????????????????
	 */
	public void getFilters() {
		try {
			User user = (User) getSession().getAttribute("user");
			renderJson(sv.findFilters(getParaToInt("_id"),user.getId(),getPara("filterType"),getSessionAttr("language")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	/**
	 * ??????????????????
	 */
	public void saveFilter() {
		try {
			Filter record = getModel(Filter.class,"",true);
			User user = (User) getSession().getAttribute("user");
			if (frontCommonsv.saveFilter(record,user,getParaMap())) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1, "????????????"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * ???????????????????????????
	 */
	public void deleteFilters() {
		try {
			if (frontCommonsv.deleteFilters(getPara("id"))) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail("???????????????"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}
	
	public void toSchedule() {
		try{
			User user = (User) getSession().getAttribute("user");
			renderJson(ResultUtil.success(sv.toSchedule(getParaToInt("orderid"),getParaToInt("modalityid"),getPara("modalityType"),getPara("appointmenttime"),user)));
			
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void triggerScan(){
		User user = (User) getSession().getAttribute("user");
		WebSocketUtils.sendMessage(990000+user.getId(), new WebsocketVO(WebsocketVO.TRIGGERSCANAPPFORM,"triggerscan").toJson());
		renderJson(ResultUtil.success());
	}
	
	/**
	 * ??????????????????
	 */
	public void reassignStudy() {
		try {
			User user = (User) getSession().getAttribute("user");
			Patient patient=getModel(Patient.class,true);
			Admission admission=getModel(Admission.class,true);
			Integer orderid=getParaToInt("orderid");
			String patientremark=getPara("patientremark");
			String admissionremark=getPara("admissionremark");
			if(sv.reassignStudy(patient, admission, orderid, patientremark, admissionremark, user)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(-1,"???????????????????????????????????????????????????????????????????????????"));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * ??????????????????
	 */
	public void getRemark() {
		try {
			renderJson(ResultUtil.success(sv.getRemark(getParaToInt("typeId"),getPara("type"))));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void getExamItemByHisItemCode() {
		try {

			renderJson(sv.getExamItemByHisItemCode(getPara("hisitemcode")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void getExamItemByRequestNo() {
		try {
			renderJson(sv.getExamItemByRequestNo(getPara("requestNo"), getPara("modality")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	
	//???????????????
	public void uploadScanimg() {
		try {
			List<UploadFile> list = getFiles("/apply/tmp");
			Integer orderid = getParaToInt("orderid");
			String studyid = getPara("studyid");
			for(UploadFile uploadfile : list) {
				if(uploadfile!=null&&uploadfile.getFile().exists()){
					File file=uploadfile.getFile();
					String uuid=UUID.randomUUID().toString();
					String newfilename=uuid+".jpg";
					String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
					String dirname=LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					String url = PropKit.get("base_upload_path")+System.getProperty("file.separator")+"apply"+System.getProperty("file.separator")+year+System.getProperty("file.separator")+dirname+System.getProperty("file.separator")+newfilename;
					FileUtils.moveFile(file, new File(url));
					
					StudyImage si=StudyImage.dao.findByOrderid(orderid);
		  			if(si==null){
		  				si=new StudyImage();
		  				si.setOrderId(orderid);
		  				si.setStudyid(studyid);
		  			}
		  			
		  			for(int j=1; j<=10; j++) {
						if(si.get("img"+j) == null) {
							si.set("img"+j, "/apply/"+year+"/"+dirname+"/"+newfilename);
							break;
						}
					}
		  			
		  			if(si.getId()!=null){
		  				si.update();
		  			}else{
		  				si.remove("id").save();
		  			}
				}
			}
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
//**************************************************************????????????***********************************************************************************************
	//????????????????????????????????????
	public void setEmergDefInfo() {
		renderJsp("/view/front/register/emergDefInfo.jsp");
	}
	
	public void findEmergDefInfo() {
		User user = (User) getSession().getAttribute("user");
		List<EmergencyDefaultInfo> defaultInfo = EmergencyDefaultInfo.dao.find("SELECT * FROM emergency_default_info where creator = ?", user.getId());
		renderJson(defaultInfo);
	}
	
	//??????????????????
	public void saveEmergDefInfo() {
		User user = (User) getSession().getAttribute("user");
		try {
			EmergencyDefaultInfo defaultInfo = getModel(EmergencyDefaultInfo.class,"",true);
			String itemstr = getPara("itemsvalue");
			renderJson(ResultUtil.success(sv.saveEmergDefInfo(defaultInfo, itemstr ,user)));
			
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//??????????????????????????????
	public void deleteEmergDefInfo() {
		try {
			if(sv.deleteEmergDefInfo(getParaToInt("id"))){
				renderJson(ResultUtil.success());
			}
			else{
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//????????????
	public void emergencyRegister() {
		HashMap<String,Object> map = new HashMap<String,Object>();
		User user = (User) getSession().getAttribute("user");
		try {
			EmergencyDefaultInfo defaultInfo = EmergencyDefaultInfo.dao.findFirst("SELECT TOP 1 * FROM emergency_default_info WHERE configname = ? AND creator = ?", getPara("configname"), user.getId());
			if(defaultInfo == null) {
				map.put("flag", 0);
			}else {
				List<EmergencyStudyitem> studyitem = EmergencyStudyitem.dao.find("SELECT * FROM emergency_studyitem WHERE emergencyid=?", defaultInfo.getId());
				map.put("flag", 1);
				map.put("defaultInfo", defaultInfo);
				map.put("studyitem", studyitem);
			}
			renderJson(ResultUtil.success(map));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
//**************************************************************????????????***********************************************************************************************
	
//******************************???????????????**************************************************
	//???????????????????????????
	public void unmatchstudy_view() {
		renderJsp("/view/front/register/unmatchstudy_view.jsp");
	}
	//?????????????????????
	public void findUnmatchstudy() {
		try {
			List<Record> list = sv.findUnmatchstudy(getParaMap(), getSessionAttr("syscode_lan"));
			int count = list.get(0).getInt("totalsize");//workListService.getDataNumber(record);
			list.remove(0);
			Map<String, Object> jsonMap = new HashMap<String, Object>();//??????map  
            jsonMap.put("total", count);//total??? ??????????????????????????????  
            jsonMap.put("rows", list);//rows??? ?????????????????? list  
			renderJson("data", jsonMap);
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//???????????????
	public void correctAccessionNo() {
		try {
			if(sv.correctAccessionNo(getParaToInt("id"), getPara("accessionnumber"))){
				renderJson(ResultUtil.success());
			}
			else{
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//??????
	public void deleteUnmatchstudy() {
		try {
			if(Unmatchstudy.dao.deleteById(getParaToInt("id"))) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//??????????????????????????????????????????
	public void checkUnmatchInfo() {
		try {
			//User user = (User) getSession().getAttribute("user");
			renderJson(ResultUtil.success(sv.checkUnmatchInfo(getPara("patientid"), getPara("accessionnumber"))));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
//******************************???????????????**************************************************

//******************************??????????????????????????????********************************************
	
	//?????????
	public void getImage(){
		try {
			renderJson(ResultUtil.success(frontCommonsv.getApplyImage(getParaToInt("orderid"))));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}	
	}
	
	//????????????
	public void gotoStudyProcess(){
		Record record=frontCommonsv.findStudyInfo(getParaToInt("orderid"));
		setAttr("record", record);
		renderJsp("/view/front/studyprocess.jsp");
	}
	
//******************************??????????????????????????????********************************************

	/**
	  *  ?????? ???????????? ?????????????????????????????????
	 */
	public void checkMyConditionName() {
	    User user = (User) getSession().getAttribute("user");
	    log.info("?????????????????????????????????" + get("filterType") + ",????????????" + get("name"));
	    Boolean res = frontCommonsv.checkMyConditionName(user.getId(), get("filterType"), get("name"));
	    log.info("??????????????????????????????" + res);
	    renderJson(ResultUtil.success(res));
	}
	
	public void showDutyRoster() {
		renderJsp("/view/dutyRoster.jsp");
	}
}
