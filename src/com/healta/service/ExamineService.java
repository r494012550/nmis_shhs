package com.healta.service;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.healta.constant.ReportStatus;
import com.healta.constant.SearchTimeConstant;
import com.healta.constant.StudyOrderPriority;
import com.healta.constant.StudyOrderStatus;
import com.healta.constant.StudyprocessStatus;
import com.healta.license.VerifyLicense;
import com.healta.model.Admission;
import com.healta.model.Client;
import com.healta.model.DeptShiftWork;
import com.healta.model.DicEmployee;
import com.healta.model.Mwlitem;
import com.healta.model.DicModality;
import com.healta.model.Injection;
import com.healta.model.Inquiry;
import com.healta.model.ModalityRecord;
import com.healta.model.StudyMaterialDefault;
import com.healta.model.Patient;
import com.healta.model.Patientcallingrecord;
import com.healta.model.PreviousHistory;
import com.healta.model.Reportremark;
import com.healta.model.Study;
import com.healta.model.StudyAcquisitionModeDefault;
import com.healta.model.StudyMethodTemplate;
import com.healta.model.Studyitem;
import com.healta.model.Studyorder;
import com.healta.model.Studyprocess;
import com.healta.model.User;
import com.healta.plugin.activemq.ActiveMQ;
import com.healta.plugin.activemq.MQSubject;
import com.healta.plugin.activemq.StudyprocessOrder;
import com.healta.plugin.dcm.SPSStatus;
import com.healta.plugin.queueup.Node;
import com.healta.plugin.queueup.QueueMethod;
import com.healta.plugin.queueup.QueueupSendOrder;
import com.healta.plugin.workforce.WorkforceServer;
import com.healta.util.ParaKit;
import com.healta.util.SequenceNo_Generator;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class ExamineService {
	private static final Logger log = Logger.getLogger(ExamineService.class);
	
	private final static String _SQL="SELECT " + Patient.dao.toSelectStr("id,createtime,creator,modifytime")
			+ " , " + Admission.dao.toSelectStr("id,createtime,creator,modifytime")
			+ " , " + Studyorder.dao.toSelectStr("id,patientidfk,patientid,createtime,creator,modifytime,accessionnumber")
			+ " , " + Study.dao.toSelectStr("id,createtime,creator")
			+ " , " + Inquiry.dao.toSelectStr("id,studyorderfk,studyid,nuclide,administration_method,createtime,modifytime,tumor_removal")
			+ " , " + Injection.dao.toSelectStr("id,studyorderfk,studyid,createtime,modifytime")
//			+ " , " + PreviousHistory.dao.toSelectStr("id,studyorderfk,studyid,interrogation_time")
			+ ", patient.id as patientpkid, studyorder.id as studyorderpkid, admission.id as admissionpkid, study.id as studypk, patientcallingrecord.callinghistory"
			+ ", (select _codenamedisplay_ from syscode where syscode.code=studyorder.status and syscode.type='0005') as orderstatus"
			+ ", (select _codenamedisplay_ from syscode where syscode.code=admission.patientsource and syscode.type='0002') as psource"
			+ ", (select _codenamedisplay_ from syscode where syscode.code=patient.sex and syscode.type='0001') as sexdisplay"
			+ ", (select _codenamedisplay_ from syscode where syscode.code=admission.ageunit and syscode.type='0008') as ageunitdisplay"
			+ ", (select modality_name from dic_modality where dic_modality.id=studyorder.modalityid) as modalityname"
			+ ", (select modality_name from dic_modality where dic_modality.id=studyorder.triagemodalityid) as triagemodalityname"
			+ ", (select name from dic_common where dic_common.id=studyorder.nuclide) as nuclide_name"
			+ ", (SELECT name FROM dic_common WHERE dic_common.id = studyorder.examination_method) AS examination_method_name"
			+ ", (select treename_zh from dic_organ WHERE dic_organ.id = studyorder.organ) AS examination_position_name"
			+ ", (SELECT name FROM users WHERE users.id = inquiry.interrogation_doctor_id) AS interrogation_doctor_name"
			+ ", (SELECT name FROM dic_common WHERE dic_common.id = inquiry.administration_method) AS administration_method_name"
			+ ", (SELECT name FROM dic_common WHERE dic_common.id = inquiry.sp_inspection_req) AS sp_inspection_req_name"
			+ ", (SELECT name FROM dic_common WHERE dic_common.id = injection.injection_site) AS injection_site_name"
			+ ", (SELECT name FROM dic_common WHERE dic_common.id = injection.pollution) AS pollution_name"
			+ ", (SELECT goods_name FROM com_goods WHERE com_goods.id = inquiry.medicine) AS medicine_name"
			+ ", (CASE WHEN report.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE report.reportstatus END) AS reportstatusdisplaycode"
			+ ", (SELECT  _codenamedisplay_  FROM syscode  WHERE syscode.code = (CASE WHEN report.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE report.reportstatus END) AND syscode.type = '0007') AS reportstatusdisplay"
			+ ", (select name_zh from syscode where type='0039' and code=inquiry.operationmethod) AS operationmethodname"
			+ ", (select name_zh from syscode where type='0038' and code=inquiry.menstruation_conditions) AS menstruation_conditionsname";
	private final static String _SQLExceptSelect =  " FROM patient,admission,studyorder"
			+ " LEFT JOIN inquiry ON studyorder.id = inquiry.studyorderfk"
			+ " LEFT JOIN injection ON studyorder.id = injection.studyorderfk"	
			+ " LEFT JOIN study ON studyorder.id=study.studyorderfk"
			+ " LEFT JOIN report ON studyorder.id = report.studyorderfk"
			+ " LEFT JOIN previous_history ON studyorder.id = previous_history.studyorderfk"
			+ " LEFT JOIN patientcallingrecord ON studyorder.studyid = patientcallingrecord.studyid  @quicksearchTable@"
			+ " WHERE patient.id = admission.patientidfk AND studyorder.admissionidfk = admission.id ";
	
	public ExamineService(){
		
	}
	
	public Studyorder getStatus(Integer orderid) throws Exception {
		return Studyorder.dao.findById(orderid);
	}
	
	/**
	 * 获取检查列表
	 */
	public Page<Record> findStudyorder(Map<String, String[]> map, String syscode_lan){
		
		List<Record> ret=new ArrayList<Record>();
		
		String where="";
		List<Object> list=new ArrayList<Object>();

		String from=null;
		String to=null;
		String orderby = " order by studyorder.pri,studyorder.sequencenumber";
		LocalDate now=LocalDate.now();
		// 时间选择 'T'今天(today) 'Y'昨天(yesterday) 'TD'三天(ThreeDay) 'FD'五天(FiveDay) ' 'W'一周 'M'一月 '
		if (StrKit.notBlank(map.get("appdate")) && StrKit.equals(SearchTimeConstant.Today, map.get("appdate")[0])) {
			from = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate()));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() + 1));
		} else if (StrKit.notBlank(map.get("appdate")) && StrKit.equals(SearchTimeConstant.Yesterday, map.get("appdate")[0])) {
			from = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} else if (StrKit.notBlank(map.get("appdate")) && StrKit.equals(SearchTimeConstant.ThreeDay, map.get("appdate")[0])){
			from = now.minusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} else if (StrKit.notBlank(map.get("appdate")) && StrKit.equals(SearchTimeConstant.FiveDay, map.get("appdate")[0])){
			from = now.minusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} else if (StrKit.notBlank(map.get("appdate")) && StrKit.equals(SearchTimeConstant.Week, map.get("appdate")[0])){
			from = now.minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} else if (StrKit.notBlank(map.get("appdate")) && StrKit.equals(SearchTimeConstant.Month, map.get("appdate")[0])){
			from = now.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} else if (StrKit.notBlank(map.get("appdate")) && StrKit.equals(SearchTimeConstant.Tomorrow, map.get("appdate")[0])) {// 明天
			from = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            to = now.plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}

		if (StrKit.notBlank(map.get("datefrom")) && StrKit.notBlank(map.get("datefrom")[0])) {
			from = map.get("datefrom")[0]+" 00:00:00";
		}
		if (StrKit.notBlank(map.get("dateto")) && StrKit.notBlank(map.get("dateto")[0])) {
			to = map.get("dateto")[0]+" 23:59:59";
		}

		//该方法有可能有个地方使用，timeframe参数等于1表示 使用配置时间
		if(map.get("timeframe")!=null&&map.get("timeframe")[0].equals("1")) {
			DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			Integer num=PropKit.use("system.properties").getInt("advanceTime",0);
			LocalDateTime today_start = LocalDateTime.of(LocalDate.now(),LocalTime.MIN);
			String startDate= String.valueOf(df.format(today_start.minusHours(num)));
			String endDate =String.valueOf(df.format(LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59))));
			from = startDate;
			to = endDate;
		}
		
		String timecon = "";
		boolean exist_sch=PropKit.use("system.properties").getBoolean("exist_appointment_process",true);
		if(exist_sch) {
			timecon = "studyorder.arrivedtime";
		} else {
			timecon = "studyorder.regdatetime";
		}
		if(map.containsKey("datetype")){
			if (StrKit.equals("registertime", map.get("datetype")[0])) {
				timecon = "studyorder.regdatetime";
			} else if (StrKit.equals("studytime", map.get("datetype")[0])) {
				timecon = "studyorder.studydatetime";
//			} else if (StrKit.equals("reporttime", map.get("datetype")[0])) {
//				timecon = "studyorder.reporttime";
			} else if (StrKit.equals("appointmenttime", map.get("datetype")[0])) {
				timecon = "studyorder.appointmenttime";
			} else if (StrKit.equals("arrivedtime", map.get("datetype")[0])) { //检查工作站 查询时间用 签到时间  --2020 08 27 hx
				timecon = "studyorder.arrivedtime";
			}
		}
		if (StrKit.notBlank(from)) {
			where += " and " + timecon + " >='" + from + "'";
		}
		if (StrKit.notBlank(to)) {
			where += " and " + timecon + " <='" + to + "'";
		}
		
		//检查设备
		if(StrKit.notBlank(map.get("appmodalityid"))) {
			String[] modalitys = map.get("appmodalityid");
			if(modalitys.length == 1 && StrKit.notBlank(modalitys[0])) {
				where += " and studyorder.modalityid  = ?";
				list.add(modalitys[0]);
			}else if(modalitys.length > 1) {
				String in = "(";
				for(String str : modalitys) {
					in += "?,";
					list.add(str);
				}
				where += " and studyorder.modalityid in " + in.substring(0, in.length()-1) + ")";
			}
		}
		
		//检查状态
		if(StrKit.notBlank(map.get("apporderStatus"))){
			String[] orderstatus = map.get("apporderStatus");
			if(orderstatus.length == 1 && StrKit.notBlank(orderstatus[0])) {
				where += " and studyorder.status = ?";
				list.add(map.get("apporderStatus")[0]);
			}else if(orderstatus.length > 1) {
				String in = "(";
				for(String str : orderstatus) {
					in += "?,";
					list.add(str);
				}
				where += " and studyorder.status in " + in.substring(0, in.length()-1) + ")";
			}
		}
		
		if(StrKit.notBlank(map.get("appstudyid")) && StrKit.notBlank(map.get("appstudyid")[0])){
			where += " and studyorder.studyid = ?";
			list.add(map.get("appstudyid")[0]);
		}
		if(StrKit.notBlank(map.get("apppatientname")) && StrKit.notBlank(map.get("apppatientname")[0])){
			where += " and patient.patientname LIKE CONCAT('%',?,'%')";
			list.add(map.get("apppatientname")[0]);
		}
		if(StrKit.notBlank(map.get("apppatientid")) && StrKit.notBlank(map.get("apppatientid")[0])){
			where += " and patient.patientid = ?";
			list.add(map.get("apppatientid")[0]);
		}
		if(StrKit.notBlank(map.get("apppatientsex")) && StrKit.notBlank(map.get("apppatientsex")[0])){
			where += " and patient.sex = ?";
			list.add(map.get("apppatientsex")[0]);
		}
		if(StrKit.notBlank(map.get("apppatientsource")) && StrKit.notBlank(map.get("apppatientsource")[0])){
			where += " and admission.patientsource = ?";
			list.add(map.get("apppatientsource")[0]);
		}
		if(StrKit.notBlank(map.get("appaccession_no")) && StrKit.notBlank(map.get("appaccession_no")[0])) {
			where += " and admission.accession_no = ?";
			list.add(map.get("appaccession_no")[0]);
		}
		if(StrKit.notBlank(map.get("appcardno")) && StrKit.notBlank(map.get("appcardno")[0])) {
			where += " and admission.cardno = ?";
			list.add(map.get("appcardno")[0]);
		}
		if(StrKit.notBlank(map.get("appinno")) && StrKit.notBlank(map.get("appinno")[0])) {
			where += " and admission.inno = ?";
			list.add(map.get("appinno")[0]);
		}
		if(StrKit.notBlank(map.get("appoutno")) && StrKit.notBlank(map.get("appoutno")[0])) {
			where += " and admission.outno = ?";
			list.add(map.get("appoutno")[0]);
		}
		
		// 快速检索//////////////////////////////////////////////////////
		String quicksearchTable="";
        if (StrKit.notBlank(map.get("quicksearchcontent")) && StrKit.notBlank(map.get("quicksearchcontent")[0])
        		&&StrKit.notBlank(map.get("quicksearchname"))) {
        	quicksearchTable=",quicksearch ";
			if(StrKit.equals("all", map.get("quicksearchname")[0])) {
				where = " and studyorder.id=quicksearch.studyorderkey and CONTAINS(quicksearch.content, ?)";
				list=new ArrayList<Object>();
				list.add("\"" + map.get("quicksearchcontent")[0] + "*\"");
			}
			else {
				where += " and studyorder.id=quicksearch.studyorderkey and CONTAINS(quicksearch.content, ?)";
				list.add("\"" + map.get("quicksearchcontent")[0] + "*\"");
			} 
        }
	        
        //检查工作站和查询选择不同的排序方式
        if (StrKit.notBlank(map.get("searchFormType")) && StringUtils.equals("waitinglist", map.get("searchFormType")[0])){//待检查列表
        	orderby = " order by studyorder.pri,studyorder.sequencenumber";
		} else {
			orderby = " order by studyorder.regdatetime,studyorder.pri";
		}
        
	    ///////////////////////////////////////////////////
		String select = _SQL.replaceAll("_codenamedisplay_", syscode_lan);
		String sqlExceptSelect = _SQLExceptSelect.replaceAll("@quicksearchTable@", quicksearchTable) + where + orderby;
		return Db.paginate(Integer.valueOf(map.get("page")[0]), Integer.valueOf(map.get("rows")[0]), select, sqlExceptSelect, list.toArray());
	}
	
	//获取正在检查的项目
	public Record getInProcessStudy(String modalityid, String syscode_lan, User user){
//		HashMap<String, String[]> map=new HashMap<String, String[]>();
//		map.put("appdate", new String[]{"T"});
//		map.put("apporderStatus", new String[]{StudyOrderStatus.in_process});
//		map.put("datetype", new String[]{"registertime"});
//		map.put("appmodalityid", new String[]{modalityid});
//		map.put("page", new String[]{"1"});
//		map.put("rows", new String[]{"20"});
//		List<Record> list=findStudyorder(new ParaKit(map),ip,syscode_lan);
		LocalDate now = LocalDate.now();
		List<Record> list = Db.find(_SQL.replaceAll("_codenamedisplay_", syscode_lan)
				+ _SQLExceptSelect.replaceAll("@quicksearchTable@", "")
				+ " AND studyorder.appointmenttime>=? AND studyorder.appointmenttime<? AND studyorder.modalityid=? AND studyorder.status=?",
				now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
				modalityid, StudyOrderStatus.in_process);
		
		if(list != null && list.size() >= 1){
			Record so = list.get(0);
			List<Reportremark> remarks = Reportremark.dao.find("SELECT top 1 * FROM reportremark"
					+ " WHERE (patientidfk=? OR admissionidfk=? OR orderid=?)"
					+ " AND (remarkcontent IS NOT NULL OR remarkcontent = '') ",
					so.getInt("patientidfk"), so.getInt("admissionidfk"), so.getInt("studyorderpkid"));
			if(remarks != null && remarks.size() > 0){
				so.set("hasRemark", "1");
			} else {
				so.set("hasRemark", "0");
			}
			
			Record record=Db.findFirst(Db.getSql("exam.queueup_node"),so.getInt("studyorderpkid"));
			if(record!=null){
			//发送叫号消息
				ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.repeatCall), MQSubject.QUEUEUP.getSendName(), StrKit.getRandomUUID(), 4);
			}
			return so;
		}
		
		return null;
	}
	
	public Map<String, List<DicModality>> getEquipment_Map(Integer institutionid){
		String where="";
		List<Object> list=new ArrayList<Object>();
		if(institutionid!=null) {
			where += " and institutionid =?";
			list.add(institutionid);
		}
		String sql = "select * from dic_modality where deleted=0"+where+" and role='modality' order by type";
		return DicModality.dao.find(sql,list.toArray()).stream().collect(Collectors.groupingBy(DicModality::getType));
	}
	
	public List<List<Record>> getEquipment_List(String modality,Integer modalityid){
		List<List<Record>> ret=new ArrayList<List<Record>>();
		ret.add(Db.find("select * from dic_modality where dic_modality.groupid =(select groupid from  dic_modality where id=?) and id!=?",modalityid,modalityid));//same group equipment
		ret.add(Db.find("select * from dic_modality where type=? and id!=? and working_state=0",modality,modalityid));//same modalitytype equipment
		return ret;
	}
	
	public List<Record> getModality(String modality,Integer modalityid){
		List<Record> ret=new ArrayList<Record>();
		List<DicModality> sameGroup = DicModality.dao.find("select * from dic_modality where dic_modality.groupid =(select groupid from  dic_modality where id=?) and id!=?",modalityid,modalityid);//same group equipment
		List<DicModality> sameType = DicModality.dao.find("select * from dic_modality where type=? and id!=?",modality,modalityid);//same modalitytype equipment
		for(DicModality dicModality:sameGroup) {
			Record record=new Record();
			record.set("id", dicModality.getId());
			record.set("modality_name", dicModality.getModalityName());
			record.set("group", "同组设备");
			ret.add(record);
		}
		for(DicModality dicModality:sameType) {
			Record record=new Record();
			record.set("id", dicModality.getId());
			record.set("modality_name", dicModality.getModalityName());
			record.set("group", "同类型设备");
			ret.add(record);
		}
		
		return ret;
	}
	
	public List<Record> getTriageList(Integer modalityid,String syscode_lan){

		LocalDate now=LocalDate.now();

		String sql="select *,patient.id as patientpkid,studyorder.id as studyorderpkid,admission.id as admissionpkid,study.id as studypk"
				+ ",(select _codenamedisplay_ from syscode where syscode.code=studyorder.status and syscode.type='0005') as orderstatus"
				+ ",(select _codenamedisplay_ from syscode where syscode.code=admission.patientsource and syscode.type='0002') as psource"
				+ ",(select _codenamedisplay_ from syscode where syscode.code=patient.sex and syscode.type='0001') as sexdisplay"
				+ ",(select _codenamedisplay_ from syscode where syscode.code=admission.ageunit and syscode.type='0008') as ageunitdisplay"
				+ ",(select modality_name from dic_modality where dic_modality.id=studyorder.modalityid) as modalityname"
				+ ",(select modality_name from dic_modality where dic_modality.id=studyorder.triagemodalityid) as triagemodalityname"
				+ ",injection.injection_datetime"
				+ " from patient,admission,injection,studyorder left join study on studyorder.id=study.studyorderfk"
				+ " where patient.id=admission.patientidfk and studyorder.id=injection.studyorderfk"
				//+ " and studyorder.admissionidfk=admission.id and studyorder.triagemodalityid=? and studyorder.regdatetime>=? and studyorder.regdatetime<?"
				+ " and studyorder.admissionidfk=admission.id and studyorder.triagemodalityid=? and studyorder.arrivedtime>=? and studyorder.arrivedtime<?"  //因核医学没有登陆模块，所以把登记时间改为签到时间
				+ " and studyorder.status in (?,?,?)"   
				+" order by studyorder.pri,studyorder.sequencenumber";
		sql =sql.replaceAll("_codenamedisplay_", syscode_lan);
		return Db.find(sql,modalityid,now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),StudyOrderStatus.injected,StudyOrderStatus.in_process,StudyOrderStatus.re_examine);
		
	}
	
	public boolean applyTriage(Integer orderid,Integer modid){
		int n=Db.update("update studyorder set triagemodalityid=? where id=?",modid,orderid);
		if (n==1) {
			return true;
		}
		else{
			return false;
		}
	
	}
	
	public boolean applyTriageAll(String orderid,Integer modid){
		int n=0;
		if(orderid.contains(",")) {
//			studyorder=Studyorder.dao.find("SELECT * FROM studyorder WHERE id in ("+orderid+")");
			n=Db.update("update studyorder set triagemodalityid="+modid+" where id in ("+orderid+")");
		}else {
			n=Db.update("update studyorder set triagemodalityid=? where id=?",modid,orderid);
		}
		if (n>0) {
			return true;
		}
		else{
			return false;
		}
	
	}
	
	/**
	 * 取消分诊申请
	 * @param orderid 检查id
	 * @param triagemodalityid 分诊申请设备id
	 * @return
	 */
	public boolean cancelApplyTriage(Integer orderid,Integer triagemodalityid){
		int count = Db.update("UPDATE studyorder SET triagemodalityid=null WHERE id=? AND triagemodalityid=?", orderid, triagemodalityid);
		if (count == 1) {
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * 开始检查
	 */
	public boolean startStudyOrder(int orderid, User user, String studystatus) {
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			Record record=Db.findFirst(Db.getSql("exam.queueup_node"),orderid);
			if(record!=null) {
				int count=Db.update("UPDATE studyorder SET status=?, triagemodalityid=null, studydatetime=? WHERE id = ? AND status = ?",
						StudyOrderStatus.in_process, new Date() ,orderid, studystatus);
				if(count==1) {
					Db.update("UPDATE mwlitem SET sps_status = ? WHERE studyorderidfk = ?", SPSStatus.ARRIVED , orderid);
					
					//发送检查进程-开始检查
			  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
							.set("studyorderfk", orderid)
							.set("status", StudyprocessStatus.inprocess)
							.set("operator", user.getUsername())
							.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
				}
				else{
					ret=false;
				}
			}
			return ret;
		});
	}
	
	/**
	 * 完成检查
	 */
	public boolean completeStudyOrder(int orderid, String completetime, User user,Study st) {
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			int count=0;
			
			Studyorder studyorder=Studyorder.dao.findById(orderid);
			Study study=Study.dao.findFirst("select * from study where studyorderfk=?",orderid);
			if(study==null) {
				study=new Study();
				study.setPatientfk(studyorder.getPatientidfk());
				study.setStudyorderfk(orderid);
				study.setModality(studyorder.getModalityType());
			}
			study.setOnEquipmentTime(st.getOnEquipmentTime());
			study.setExamPollution(st.getExamPollution());
			study.setDelayedAcquisition(st.getDelayedAcquisition());
			study.setDelayedAcquisition1(st.getDelayedAcquisition1());
			study.setDelayedAcquisitionTime(st.getDelayedAcquisitionTime());
			study.setDelayedAcquisitionTime1(st.getDelayedAcquisitionTime1());
			study.setRemarks(st.getRemarks());
			if(study.getId()==null) {
				ret=ret&study.remove("id").save();
			} else {
				ret=ret&study.update();
			}
			
			if(StringUtils.isNotBlank(completetime)) {
				count=Db.update("update studyorder set status=?, studycompletetime=? where id=? and status=?" ,StudyOrderStatus.completed, completetime, orderid,StudyOrderStatus.in_process);
			}else {
				count=Db.update("update studyorder set status=?, studycompletetime=? where id=? and status=?" ,StudyOrderStatus.completed, new Date(), orderid,StudyOrderStatus.in_process);	
			}
			if(count==1) {
				Db.update("UPDATE mwlitem SET sps_status = ? WHERE studyorderidfk = ?", SPSStatus.COMPLETED , orderid);
				
				//发送检查进程-完成检查
		  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
						.set("studyorderfk", orderid)
						.set("status", StudyprocessStatus.completed)
						.set("operator", user.getUsername())
						.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
		  		
		  		//检查完成,根据排版情况获取报告医生
		  		if(VerifyLicense.hasFunction("workforce")&&StrKit.equals(WorkforceServer.INSTANCE.getConfiguration().getEnableReportTask(), "1")) {
			  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
							.set("studyorderfk", orderid)
							.set("status", StudyprocessStatus.completed)
							.set("operator", user.getUsername())
							.set("operatorname", user.getName())), MQSubject.WORKFORCE.getSendName(), StrKit.getRandomUUID(), 4);
		  		}
			}
			else{
				ret=false;
			}
			return ret;	
		});
	}
	
	/**
	 * 取消检查
	 * @param integer 
	 */
	public Record cancelStudyOrder(int orderid, User user, String message) {
		Record re=new Record().set("res", true);
		Studyorder so= Studyorder.dao.findById(orderid);
		//检查取消，检查完成，检查重拍，取消预约状态下，无法取消检查
		if(StrKit.equals(so.getStatus(), StudyOrderStatus.canceled)||StrKit.equals(so.getStatus(), StudyOrderStatus.completed)
				||StrKit.equals(so.getStatus(), StudyOrderStatus.re_examine)||StrKit.equals(so.getStatus(), StudyOrderStatus.cancel_the_appointment)) {
			return re.set("res", false).set("error", "当前检查状态下无法取消！");
		}
		return re.set("res", Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			int count=Db.update("update studyorder set status=?,cancelreason=? where id=?",
					StudyOrderStatus.canceled,message,orderid);
			if(count==1) {
				Db.update("UPDATE mwlitem SET sps_status = ? WHERE studyorderidfk = ?", SPSStatus.DISCONTINUED , orderid);
				
				//发送检查流程-取消检查
		  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
						.set("studyorderfk", orderid)
						.set("status", StudyprocessStatus.canceled)
						.set("operator", user.getUsername())
						.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
			}
			else{
				ret=false;
			}
			return ret;	
		}));
	}
	
	/**
	 * 重新检查
	 */
	public boolean cancelStudyChecked(int orderid, User user){
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			int count=Db.update("update studyorder set status=?,studyinstanceuid=null,studydatetime=null,studycompletetime=null where id=? and status=?" ,StudyOrderStatus.re_examine ,orderid,StudyOrderStatus.completed);

			if(count==1) {
				Db.update("UPDATE mwlitem SET sps_status = ? WHERE studyorderidfk = ?", SPSStatus.SCHEDULED , orderid);
				//发送检查流程-重新检查
		  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
						.set("studyorderfk", orderid)
						.set("status", StudyprocessStatus.re_examine)
						.set("operator", user.getUsername())
						.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
			}
			else{
				ret=false;
			}
			return ret;
		});
	}

	
	/**
	 * 过号
	 */
	public boolean skipStudyOrder(int orderid) {
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret = true;
			int count = Db.update("UPDATE studyorder SET status=?,pri=? WHERE id=? AND status=?",
					StudyOrderStatus.injected, StudyOrderPriority.skipExam, orderid, StudyOrderStatus.in_process);
			
			if(count ==1 ){
				Db.update("UPDATE mwlitem SET sps_status = ? WHERE studyorderidfk = ?", SPSStatus.SCHEDULED , orderid);
			}
			else{
				ret=false;
			}
			return ret;
		});
	}
	
	/**
	 * 重复叫号
	 * @param orderid
	 * @return
	 */
//	public boolean repeatCall(int orderid,User user) {
//		boolean succeed=Db.tx(new IAtom() {
//			public boolean  run() {
//				boolean ret=true;
//				Record record=Db.findFirst(Db.getSql("exam.queueup_node"),orderid);
//				if(record!=null){
//				//发送叫号消息
//					ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.repeatCall), MQSubject.QUEUEUP.getSendName(), StrKit.getRandomUUID(), 4);
//				}
//				Studyorder so=Studyorder.dao.findById(orderid);
//				Record re = getPatientByOrder(so,user.getName());
//				try {
//					sendQueue(so,re,QueueMethod.repeatCall);
//					}catch(Exception e) {
//					e.printStackTrace();
//					return false;
//				}
//				return ret;	
//			}
//		});
//		return succeed;
//	}
	
	public List<Studyitem> getStudyItem(Integer orderid){
		List<Studyitem> items=new ArrayList<Studyitem>();
		if(orderid!=null){
			items=Studyitem.dao.find("select * from studyitem where orderid=?",orderid);
		}
		return items;
	}
	
	/**
	 * 保存检查耗材
	 * @throws Exception 
	 */
	public Study saveApp(Study study,User user) throws Exception {
		if(study.getInt("technicianid")!=null) {
			study.set("technician", DicEmployee.dao.findById(study.getInt("technicianid")).getName());
		}
		if(study.getInt("nurseid")!=null) {
			study.set("nurse", DicEmployee.dao.findById(study.getInt("nurseid")).getName());
		}
		if(study.getInt("physicianid")!=null) {
			study.set("physician", DicEmployee.dao.findById(study.getInt("physicianid")).getName());
		}
		
		study.set("creator", user.getId());
		if(study.getId()!=null) {
			study.update();
		}else {
			study.remove("id").save();
		}
		
		return study;
	}
	
	public boolean acceptTriageApply(String orderids,Integer modalityid){
		
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			String ids="";
			if(StringUtils.endsWith(orderids, ",")){
				ids=StringUtils.substring(orderids, 0, orderids.length()-1);
			}
			DicModality mod = DicModality.dao.findById(modalityid);
			for(String id:ids.split(",")){

				Studyorder so=new Studyorder();
				so.setModalityid(modalityid);
				so.setModalityType(mod.getType());
				
				String sequencenumber=SequenceNo_Generator.getRegNumber_new(so);
				int n=Db.update("update studyorder set modalityid=?,triagemodalityid=null,sequencenumber=? where id=? and status in(?,?,?)",modalityid,sequencenumber,id,StudyOrderStatus.injected,StudyOrderStatus.in_process,StudyOrderStatus.re_examine);
				ret=ret&(n==1);
				
				n=Db.update("update mwlitem set station_aet=?,station_name=? where studyorderidfk=?",mod.getWorklistscu(),mod.getModalityName(),id);
				ret=ret&(n==1);
			}
			
			return ret;
		});
		
	}
	
	public boolean modifyStudyorder(Integer orderid, String completetime, User user) {
		boolean ret=false;
		Studyorder so=Studyorder.dao.findById(orderid);
		if(StrKit.equals(StudyOrderStatus.in_process, so.getStatus())) {
			ret=completeStudyOrder(orderid, completetime, user,null);
		}else if(StrKit.equals(StudyOrderStatus.completed, so.getStatus())) {
			if(StringUtils.isNotBlank(completetime)) {
				so.set("studycompletetime", completetime);
			}else {
				so.set("studycompletetime", new Date());
			}
			ret=so.update();
		}
		return ret;
	}
	
	//打开备注
	public List<Record> getRemarks(Integer patientidfk, Integer admissionidfk, Integer orderid){
		List<Record> list=Db.find("SELECT reportremark.*, CONVERT(varchar(100), reportremark.createtime, 120) AS formatcreatetime,"
				+ " creator as creator_name"
				+ " FROM reportremark"
				+ " WHERE ((patientidfk=? AND type='patient') OR (admissionidfk=? AND type='admission') OR orderid = ?)"
				+ " AND (remarkcontent IS NOT NULL OR remarkcontent = '') "
				+ " ORDER BY modifytime DESC", patientidfk, admissionidfk, orderid);
		return list;
	}

	//保存检查备注
	public boolean saveReportRemark(Reportremark remark,User user) {
		boolean ret=true;
		remark.setModifytime(new Date());
		if(remark.getId()!=null) {
			ret=remark.update();
		}
		else {
			remark.setCreator(user.getName());
			ret=remark.remove("id").save();
		}
		
		return ret;
		
	}

	/**
	 * 设置设备是否可用
	 * @param checked
	 * @param modalityId
	 * @return
	 */
	public boolean setModalityState(boolean checked,Integer modalityId,String note,User user) {
		boolean ret=false;
		DicModality modality=DicModality.dao.findById(modalityId);
		if(modality!=null) {
			if(checked) {
				modality.setWorkingState(0);
			}else {
				List<Studyorder> list=Studyorder.dao.find("select * from studyorder where status='13' and modalityid="+modalityId);
				if(list.size()>0) {
					for (int i = 0; i < list.size(); i++) {
						Studyorder studyorder=list.get(i);
						studyorder.setStatus(StudyOrderStatus.injected);
						studyorder.update();
					}
				}
				modality.setWorkingState(1);
			}
			ret=modality.update();
			
			ModalityRecord modalityrecord=new ModalityRecord();
			if(checked) {
				modalityrecord.setType("开启");
			}else {
				modalityrecord.setType("关闭");
			}
			modalityrecord.setModalityId(String.valueOf(modalityId));
			modalityrecord.setNote(note);
			modalityrecord.setCreateid(user.getId());
			modalityrecord.setCreatetime(new Date());
			ret=modalityrecord.remove("id").save();
		}
		return ret;
	}
	
	/**
	 * 更新当前病人队列
	 */
	public boolean updateQueue(int orderid, User user) {
		boolean ret=true;
		Record record=Db.findFirst(Db.getSql("exam.queueup_node"),orderid);
		if(record!=null){
		//发送叫号消息
			ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.repeatCall), MQSubject.QUEUEUP.getSendName(), StrKit.getRandomUUID(), 4);
		}
		return ret;
	}
	
	/**
	 * 根据StudyOrder获取病人的详细信息
	 * @param StudyOrder
	 */
	public Record getPatientByOrder(Studyorder StudyOrder,String doctorname) {
		Record record=new Record();
		Patient patient=Patient.dao.findById(StudyOrder.getPatientidfk());
		Admission admission=Admission.dao.findById(StudyOrder.getAdmissionidfk());
		DicModality dicmodality=DicModality.dao.findById(StudyOrder.getModalityid());
		
		record.set("patientname", patient.getPatientname());
		record.set("modalityname", dicmodality.getModalityName());
		record.set("location", dicmodality.getLocation());
		record.set("patientsource", admission.getPatientsource());
		record.set("doctor", doctorname);
		
		record.set("patientidfk", patient.getId());
		record.set("admissionidfk", admission.getId());
		
		return record;
	}
	
	
	public Integer getModalitByShift(Integer userid,Integer deptid,String postcode){
		Integer ret=null;
		String now=LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
		log.info("now="+now+";userid="+userid+";deptid="+deptid+";postcode="+postcode);
		List<Record> list=Db.find("select dept_shift_work.modalityid,dept_worktime.starttime,dept_worktime.endtime from dept_shift_work left join dept_worktime on dept_shift_work.worktimeid=dept_worktime.id "
				+ "where userid=? and deptid=? and postcode=? and workdate=?",
				userid,deptid,postcode,LocalDate.now().format(DateTimeFormatter.ISO_DATE));
		for(Record record:list){
			String start= record.getStr("starttime");
			String end=record.getStr("endtime");
			if(start.compareTo(end)>0){//时间段跨天
				if(now.compareTo(start)>=0||now.compareTo(end)<=0){
					ret=record.getInt("modalityid");
					break;
				}
			} else if(now.compareTo(start)>=0&&now.compareTo(end)<=0){
				ret=record.getInt("modalityid");
				break;
			}
		}
		return ret;
	}
	
	/**
	 * 检查叫号-发送信息
	 */
	public boolean diccallingsendmessage(int orderid,String sendmessage,User user) {
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			Record record=Db.findFirst(Db.getSql("exam.queueup_node"),orderid);
			if(record!=null){
			  //发送叫号消息
			  record.set("dicmessage", sendmessage);
			  ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.Remove), MQSubject.QUEUEUP.getSendName(), StrKit.getRandomUUID(), 4);

			}
			return ret;
		});
	}
	
	public boolean savePatientCallingRecord(String studyid, int index) {
		boolean ret = true;
		Patientcallingrecord patientcallingrecord = Patientcallingrecord.dao.findFirst("select * from patientcallingrecord where studyid=?",studyid);
		if(patientcallingrecord == null){
			patientcallingrecord = new Patientcallingrecord();
			patientcallingrecord.setStudyid(studyid);
			patientcallingrecord.setCallinghistory(index);
			ret = patientcallingrecord.remove("id").save();
		}else{
			if(index > patientcallingrecord.getCallinghistory()){
				patientcallingrecord.setCallinghistory(index);
				ret = patientcallingrecord.update();
			}
		}
		
		return ret;
	}
	
	public int getPatientCallingHistory(String studyid) {
		Patientcallingrecord patientcallingrecord = Patientcallingrecord.dao.findFirst("select * from patientcallingrecord where studyid=?",studyid);
		if(patientcallingrecord != null){
			return patientcallingrecord.getCallinghistory();
		}else {
			return -1;
		}
	}
	/**
	 * 获取此用户今天设置的 默认检查耗材
	 * @param userid 登入的用户id
	 * @param modalityid   设备的id
	 * @return
	 */
	public Record getDefaultSetting(Integer userid, Integer modalityid) {
		return Db.findFirst("SELECT top 1 * from study_material_default where creator = ? and modalityid = ? ORDER BY createtime desc", userid, modalityid);
	}

	public StudyAcquisitionModeDefault saveStudyAcquisitionModeDefault(StudyAcquisitionModeDefault studyacquisitionmodedefault, User user) {
		StudyAcquisitionModeDefault model=StudyAcquisitionModeDefault.dao.findFirst("select * from study_acquisition_mode_default where creator=?",user.getId());
		if(model!=null) {
			studyacquisitionmodedefault.setId(model.getId());
			studyacquisitionmodedefault.update();
		} else {
			studyacquisitionmodedefault.setCreator(user.getId());
			studyacquisitionmodedefault.remove("id").save();
		}
		return studyacquisitionmodedefault;
	}
}
