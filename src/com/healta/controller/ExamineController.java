package com.healta.controller;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.healta.model.*;
import org.apache.log4j.Logger;


import com.healta.constant.CacheName;
import com.healta.constant.StudyOrderStatus;
import com.healta.license.VerifyLicense;
import com.healta.plugin.activemq.ActiveMQ;
import com.healta.plugin.activemq.MQSubject;
import com.healta.plugin.queueup.Node;
import com.healta.plugin.queueup.QueueMethod;
import com.healta.plugin.queueup.QueueupSendOrder;
import com.healta.service.ExamineService;
import com.healta.service.FrontCommonService;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

public class ExamineController extends Controller {
	private static final Logger log = Logger.getLogger(ExamineController.class);
	private static ExamineService sv=new ExamineService();
	private static FrontCommonService frontCommonsv = new FrontCommonService();
	
	public void index() {
		User user = (User) getSession().getAttribute("user");
		
		DicEmployee employee = DicEmployee.dao.findById(user.getEmployeefk());
		setAttr("user_institution", employee.getInstitutionid());
		
		setAttr("username", user.getUsername());
		//setAttr("rolename", "管理员");
		setAttr("name", user.getName());
		setAttr("user_id", user.getId());

		UserProfiles profiles= UserProfiles.dao.findFirst("select * from userprofiles where userid = ?", user.getId());
		if(profiles!=null){
			setAttr("up", profiles);
			setAttr("pagination_examine_searchdg_exam", profiles.getPaginationExamineSearchdgExam());
		}

		String ip=getRequest().getRemoteAddr();
		log.info("IP:"+ip);
		Client client = Client.dao.findByHostip(ip);
		if(client!=null) {
			setAttr("bindingip", "true");
		}
		else{
			setAttr("bindingip", "false");
		}
		setAttr("enable_workforce", VerifyLicense.hasFunction("workforce"));
		renderJsp("/view/front/examine/examine.jsp");
	}
	
	/**
	 *  跳转检查工作站页面
	 */
	public void gotoExamination() {
		User user = (User) getSessionAttr("user");
		DicEmployee employee = DicEmployee.dao.findById(user.getEmployeefk());

		setAttr("deptcode", employee.getDeptcode());//取该账号对应的科室,通过科室代码和岗位分别获取上机医生和上机护士
		
		Integer modalityId=getParaToInt("modalityid");
		//modalityId为空首先通过排班表找到该技师对应的设备，然后再查找ip绑定的设备，任然找不到设备，跳转至选择设备界面
		/*
		if(modalityId == null){
			if(VerifyLicense.hasFunction("workforce")){
				modalityId=sv.getModalitByShift(user.getId(),employee.getDeptfk(),employee.getProfession());
			}
		}
	
		if(modalityId == null){
			Client client = Client.dao.findFirst("SELECT TOP 1 * FROM client WHERE hostip = ?", getRequest().getRemoteAddr());
			modalityId = client!=null? client.getModalityid() : null;
		}
		
		//modalityId为空则获取默认设备id
		 if(modalityId==null) {
			List<UserProfiles> list=UserProfiles.dao.find("select * from userprofiles where userid="+user.getId());
			if(list!=null&&list.size()>0) {
				UserProfiles userprofiles=list.get(0);
				modalityId=Integer.valueOf(userprofiles.getDefaultDicModality());
			}
		}*/
		
		
		
		//根据当前机器ip 获取所对应的设备
		try {
			InetAddress ip4 = Inet4Address.getLocalHost();
			Record record=Db.findFirst("select* from client  where hostip='"+ip4.getHostAddress()+"'");
			if(record!=null) {
				modalityId=record.getInt("modalityid");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		
		if(modalityId != null) {
			DicModality modality=DicModality.dao.findById(modalityId);
			setSessionAttr("tech_modality", modality);
			setAttr("modality", modality);

			/*Record so = sv.getInProcessStudy(modalityId+"", getSessionAttr("syscode_lan"), user);
			if (so != null) {
				setAttr("so", so);
			}*/
			
			LocalDate today = LocalDate.now();
			setAttr("today", today.format(DateTimeFormatter.ISO_LOCAL_DATE));
			setAttr("injected", StudyOrderStatus.injected);
			setAttr("re_examine", StudyOrderStatus.re_examine);
			setAttr("in_process", StudyOrderStatus.in_process);
			
			StudyAcquisitionModeDefault studyacquisitionmodedefault=StudyAcquisitionModeDefault.dao.findFirst("select * from study_acquisition_mode_default where creator=?",user.getId());
			if(studyacquisitionmodedefault!=null) {
				setAttr("studyacquisitionmodedefault", studyacquisitionmodedefault);
			}
			if(getBoolean("istablet",false)) {
				renderJsp("/view/front/examine/examination_tablet.jsp");
			} else {
				renderJsp("/view/front/examine/examination.jsp");
			}
			ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(modality.getType(),modalityId,user.getName()),QueueMethod.switchUser), 
					MQSubject.QUEUEUP.getSendName(), StrKit.getRandomUUID(), 4);
		} else {
			//List<Record> list=Db.find("select * from dic_modality");
			setAttr("map", sv.getEquipment_Map(employee.getInstitutionid()));
			//setAttr("r", new Random());
			render("/view/front/examine/selectEquipment.html");
		}
		
	}
	
	public void selectEquipment() {
		setAttr("map", sv.getEquipment_Map(getParaToInt("user_institution")));
		//setAttr("r", new Random());
		render("/view/front/examine/selectEquipment.html");
	}
	
	/**
	 *  查找所有机房（获取所有的检查设备）
	 */
	public void findModality() {
	    try {
	        renderJson(DicModality.dao.find("select id,modality_name from dic_modality where deleted = 0"));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(ResultUtil.fail(-1, e.getMessage()));
        }
    }
	
	public void gotoSearchExamination() {
		setAttr("modalityId", getPara("modalityId"));
		setAttr("modalityType", getPara("modalityType"));
		setAttr("quicksearch",VerifyLicense.hasFunction("quicksearch"));
		renderJsp("/view/front/examine/examine_search.jsp");
	}
	
	public void gotoTriageDlg() {
		List<List<Record>> list=sv.getEquipment_List(getPara("modalityType"), getParaToInt("modalityId"));
		setAttr("samegroup", list.get(0));
		setAttr("samemodality", list.get(1));
		render("/view/front/examine/triageEquipment.html");
	}
	
	public void gotoTriageListDlg() {
		setAttr("modalityId", getParaToInt("modalityId"));
		setAttr("ctx", getRequest().getContextPath());
		render("/view/front/examine/triageList.html");
	}
	
	public void gotoexamineCancel() {
		renderJsp("/view/front/examine/examinecancel.jsp");
	}
	
	public void getTriageList() {
		renderJson(sv.getTriageList(getParaToInt("modalityId"),getSessionAttr("syscode_lan")));
	}
	
	public void applyTriage() {
		try {
			if(sv.applyTriage(getParaToInt("orderid"), getParaToInt("modalityid"))){
				renderJson(ResultUtil.success());
			}
			else{
				renderJson(ResultUtil.fail(-1,"error"));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void applyTriageAll() {
		try {
			if(sv.applyTriageAll(getPara("orderid"), getParaToInt("modalityid"))){
				renderJson(ResultUtil.success());
			}
			else{
				renderJson(ResultUtil.fail(-1,"error"));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//取消分诊申请
	public void cancelApplyTriage() {
		try {
			if(sv.cancelApplyTriage(getParaToInt("orderid"), getParaToInt("triagemodalityid"))){
				renderJson(ResultUtil.success());
			}
			else{
				renderJson(ResultUtil.fail(-1,"error"));
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 *  检查工作站---检查列表---设置
	 */
	public void openFilterDlg() {
		if (StrKit.equals("1",getPara("workingState"))) {
			setAttr("workingState", false);
		} else {
			setAttr("workingState", true);
		}
		log.info("today:" + new Date());
		setAttr("today", new Date());
		setAttr("modalityId", getPara("modalityId"));
		renderJsp("/view/front/examine/changeFilter.jsp");
	}
	
	public void gotoReceiveDlg() {
		setAttr("modalityType", getPara("modalityType"));
		setAttr("modalityId", getParaToInt("modalityId"));
		setAttr("registered", StudyOrderStatus.injected);
		setAttr("re_examine", StudyOrderStatus.re_examine);
		renderJsp("/view/front/examine/receivePatient.jsp");
	}
	
	public void openCompletetimeDlg() {
		renderJsp("/view/front/examine/completetime.jsp");
	}
	
	public void openModifyDlg() {
		Studyorder so=Studyorder.dao.findById(getParaToInt("orderid"));
		if(so!=null) {
			setAttr("orderid", so.getId());
			String state=so.getStatus();
			Date completetime=so.getStudycompletetime();
			String str="";
			String time="";
			switch(state) {
			case StudyOrderStatus.in_process:
				str="检查开始";
				break;
			case StudyOrderStatus.completed:
				str="检查完成";
				break;
			default:break;
			}
			setAttr("state", str);
			if(completetime!=null) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				time=formatter.format(completetime);
			}
			setAttr("completeTime", time);
			Study study = Study.dao.findFirst("SELECT TOP 1 * FROM study WHERE studyorderfk = ?",so.getId());
			setAttr("study",study);
		}
		
		renderJsp("/view/front/examine/modifyStudyorder.jsp");
	}
	
	public void getModality() {
		try{
			renderJson(sv.getModality(getPara("modalityType"), getParaToInt("modalityId")));
		
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 获取当前数据的状态
	 */
	public void getStatus() {
		try {
			DicModality modality=DicModality.dao.findById(getParaToInt("modalityId"));
			if(modality.getWorkingState()==0) {
				renderJson(ResultUtil.success(sv.getStatus( getParaToInt("orderid"))));
			}else {
				renderJson(ResultUtil.fail(1, "该设备已停用！"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	/**
	 * 获取检查列表
	 */
	@SuppressWarnings("unlikely-arg-type")
	public void findStudyorder(){	
		try {
			String ip=getRequest().getRemoteAddr();
			log.info("Quick Search Content:" + getPara("quicksearchcontent")+";Quick Search Name:"+getPara("quicksearchname"));
			Page<Record> page = sv.findStudyorder(getParaMap(), getSessionAttr("syscode_lan"));
			Map<String, Object> jsonMap = new HashMap<String, Object>();//定义map  
            jsonMap.put("total", page.getTotalRow());//total键 存放总记录数，必须的  
            jsonMap.put("rows", page.getList());//rows键 存放每页记录 list  
			renderJson("data", jsonMap);
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 开始检查
	 */
	public void startStudyOrder() {
		try {
			User user = (User) getSession().getAttribute("user");
			int orderid=getParaToInt("orderid");
			String studystatus = getPara("studystatus");
			if(sv.startStudyOrder(orderid,user,studystatus)) {
				List<Reportremark> remarks = 
						Reportremark.dao.find("SELECT * FROM reportremark"
						+ " WHERE ((patientidfk=? and type='patient') OR (admissionidfk=? and type='admission') OR studyid=?)"
						+ " AND (remarkcontent IS NOT NULL OR remarkcontent = '') ",
						getParaToInt("patientidfk"), getParaToInt("admissionidfk"), getPara("studyid"));
				String remark = "0";
				if(remarks != null && remarks.size() > 0) {
					remark = "1";
				}
				renderJson(ResultUtil.success(remark));
			}else {
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	
	
	/**
	 * 完成检查
	 */
	public void completeStudyOrder() {
		try {
			User user = (User) getSession().getAttribute("user");
			int orderid = getParaToInt("orderid");
			String completetime = getPara("completetime");
			Study study=getModel(Study.class, "",true);
			if(sv.completeStudyOrder(orderid,completetime,user,study)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 取消检查
	 */
	public void cancelStudyOrder() {
		try {
			User user = (User) getSession().getAttribute("user");
			Record re=sv.cancelStudyOrder(getParaToInt("orderid"),user,getPara("cancelreason"));
			if(re.getBoolean("res")) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(re.getStr("error")));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 重新检查
	 */
	public void cancelStudyChecked(){
		try {
			User user = (User) getSession().getAttribute("user");
			if(sv.cancelStudyChecked(getParaToInt("orderid"),user)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 过号
	 */
	public void skipStudyOrder() {
		try {
			if(sv.skipStudyOrder(getParaToInt("orderid"))) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/*
	public void repeatCall() {
		try {
			User user = (User) getSession().getAttribute("user");
			if(sv.repeatCall(getParaToInt("orderid"),user)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	*/
	public void getStudyItem() {
		try {
			renderJson(sv.getStudyItem(getParaToInt("orderid")));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 保存检查耗材
	 */
	public void saveApp() {
		try {
			Study study=getModel(Study.class, "",true);
			
			User user = (User) getSession().getAttribute("user");
			renderJson(ResultUtil.success(sv.saveApp(study,user)));

		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void acceptTriageApply(){
		try {
			if (sv.acceptTriageApply(getPara("orderis"),getParaToInt("modalityid"))) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1, "保存失败"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void modifyStudyorder() {
		try {
			User user = (User) getSession().getAttribute("user");
			if(sv.modifyStudyorder(getParaToInt("orderid"),getPara("completetime"),user)) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	//打开备注
	public void goToEditremark() {
		setAttr("studyid", getPara("studyid"));
		setAttr("patientidfk",getParaToInt("patientpkid"));
		setAttr("admissionidfk",getParaToInt("admissionpkid"));
		setAttr("orderid",getParaToInt("orderid"));
		setAttr("remarks",sv.getRemarks(getParaToInt("patientpkid"), getParaToInt("admissionpkid"), getParaToInt("orderid")));
		render("/view/front/examine/remarks.html");
		
	}
	
	//保存检查备注
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
	
	
	/**
	 * 设置设备是否可用
	 */
	public void setModalityState() {
		try {
			User user = (User) getSession().getAttribute("user");
			if(sv.setModalityState(getParaToBoolean("checked"),getParaToInt("modalityid"),getPara("note"),user)) {
				CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_MODALITY_KEY);
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 更新当前病人队列
	 */
	public void updateQueue() {
		User user = (User) getSession().getAttribute("user");
		try {
			if (sv.updateQueue(getParaToInt("orderid"),user)) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1, "保存失败"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
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
	
	public void showDutyRoster() {
		renderJsp("/view/dutyRoster.jsp");
	}
	
	//检查-发送叫号消息
	public void diccallingsendmessage() {
		try {
			User user = (User) getSession().getAttribute("user");
			if(sv.diccallingsendmessage(getParaToInt("orderid"),getPara("sendmessage"),user)) {
				if(sv.savePatientCallingRecord(getPara("studyid"),getInt("index"))) {
					renderJson(ResultUtil.success());
				}else{
					renderJson(ResultUtil.fail("叫号成功，保存叫号记录失败"));
				}
			}else {
				renderJson(ResultUtil.fail("叫号失败请重试，如果问题依然存在，请联系系统管理员！"));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void getPatientCallingHistory() {
		try {
			renderJson(ResultUtil.success(sv.getPatientCallingHistory(getPara("studyid"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void findModalityRecord() {
		renderJson(ModalityRecord.dao.find("select modality_record.*,users.name from modality_record,users where modality_record.createid=users.id and modality_id='"+getPara("modalityid")+"'"));
	}
	public void getDefaultSetting() {
        User user = (User) getSession().getAttribute("user");
        if (user != null) {
            renderJson(ResultUtil.success(sv.getDefaultSetting(user.getId(), getParaToInt("modalityid"))));
        } else {
            renderJson(ResultUtil.fail(-1, "登入过期，请重新登入！"));
        }
	}
	
	/**
	 * 保存采集方式
	 */
	public void saveStudyAcquisitionModeDefault() {
		try {
			StudyAcquisitionModeDefault studyacquisitionmodedefault=getModel(StudyAcquisitionModeDefault.class, "",true);
			
			User user = (User) getSession().getAttribute("user");
			renderJson(ResultUtil.success(sv.saveStudyAcquisitionModeDefault(studyacquisitionmodedefault,user)));

		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
}
