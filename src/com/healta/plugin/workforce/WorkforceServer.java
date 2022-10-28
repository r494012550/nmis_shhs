package com.healta.plugin.workforce;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.healta.model.DeptReportTaskSetting;
import com.healta.model.DeptSubscriptionExamitem;
import com.healta.model.DeptSubscriptionModality;
import com.healta.model.DeptSubscriptionRule;
import com.healta.model.DicModality;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

public enum WorkforceServer {
	INSTANCE;
	private final static Logger log = Logger.getLogger(WorkforceServer.class);
	//	public static ConcurrentHashMap<Integer, Physician> reportphysician=new ConcurrentHashMap<Integer, Physician>();
//	public static ConcurrentHashMap<Integer, Physician> pre_auditphysician=new ConcurrentHashMap<Integer, Physician>();
//	public static ConcurrentHashMap<Integer, Physician> auditphysician=new ConcurrentHashMap<Integer, Physician>();
	private static List<Physician> reportphysician=null;
	private static List<Physician> pre_auditphysician=null;
	private static List<Physician> auditphysician=null;
	
//	private static Map<String,String> configmap=new HashMap<String ,String>();
	private static DeptReportTaskSetting setting=null;
	
	private static Map<Integer, List<DicModality>> modalitymap=null;
	
	private static Map<Integer, String> subsRuleMap=null;
	private static Map<Integer, List<DeptSubscriptionModality>> subsRuleModalityMap=null;
	private static Map<Integer, List<DeptSubscriptionExamitem>> subsRuleExamItemMap=null;
	
	public void init(boolean reset) {
		
		loadConfiguration();
		if(enbaleFunc("enable_report_task")) {
			if(enbaleFunc("enable_ADARD")) {
				initADARD(reset);
			} else if(enbaleFunc("enable_ADQ")){
				initADQ(reset);
			}
		}
//		Collections.synchronizedList(new ArrayList<>());
	}
	
	public void clear(){
		CacheKit.removeAll("workforce_data");
	}
	
	public void loadConfiguration() {
		setting=DeptReportTaskSetting.dao.findFirst("select top 1 * from dept_report_task_setting order by createtime desc");
		if(setting==null) {
			setting=new DeptReportTaskSetting();
		}
	}
	
	/*
	 *是否启用功能
	 *fun_name ： 功能名称，对应数据库字段名称 
	 * */
	public boolean enbaleFunc(String fun_name){
		log.info(fun_name+"---------"+StrKit.equals(setting.getStr(fun_name), "1"));
		return StrKit.equals(setting.getStr(fun_name), "1");
	}
	
	public DeptReportTaskSetting getConfiguration() {
		return setting;
	}
	
	public List<Physician> getPhysicianList(){
		return reportphysician!=null?reportphysician:new ArrayList<Physician>();
	}
	
	public List<Physician> getPreAuditPhysicianList(){
		return pre_auditphysician!=null?pre_auditphysician:new ArrayList<Physician>();
	}
	
	public List<Physician> getAuditPhysicianList(){
		return auditphysician!=null?auditphysician:new ArrayList<Physician>();
	}
	
	/**
	 *   初始化配置，按照检查报告难度平均分配
	 * Average distribution according to report difficulty
	 */
	private void initADARD(boolean reset) {
		String today=LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		if(!reset){
			reportphysician=CacheKit.get("workforce_data", "reportphysician"+today);
			pre_auditphysician=CacheKit.get("workforce_data", "pre_auditphysician"+today);
			auditphysician=CacheKit.get("workforce_data", "auditphysician"+today);
		}
		boolean flag=false;
		if(reportphysician==null){
			flag=true;
			reportphysician=new ArrayList<Physician>();//Collections.synchronizedList(new ArrayList<Physician>());
		}
		if(pre_auditphysician==null){
			pre_auditphysician=new ArrayList<Physician>();//Collections.synchronizedList(new ArrayList<Physician>());
		}
		if(auditphysician==null){
			auditphysician=new ArrayList<Physician>();//Collections.synchronizedList(new ArrayList<Physician>());
		}
		
//		LocalDate now=LocalDate.now();
//		
//		String reportphysician_sql="select a.report_assignee,sum(dic_examitem.coefficient) as coefficient, "
//				+ "STUFF((select ',''' + cast(id as varchar)+'''' from  studyorder b where b.report_assignee = a.report_assignee and b.studydatetime >=? for xml path('')),1,1,'') AS orderids "
//				+ "from studyorder a left join report on a.id=report.studyorderfk,studyitem left join dic_examitem on studyitem.item_id=dic_examitem.id  "+
//				"where a.id=studyitem.orderid  and a.studydatetime >=? and (report.reportstatus is null or report.reportstatus in('20','21')) and a.report_assignee is not null "+
//				"group by a.report_assignee";//计算每个报告医生的检查报告分值之和。
				
				
//				"select report.reportphysician,sum(dic_examitem.coefficient) as coefficient from studyorder,studyitem left join dic_examitem on studyitem.item_id=dic_examitem.id,report  "
//				+ "where studyorder.id=studyitem.orderid and studyorder.id=report.studyorderfk "
//				+ "and report.reporttime >=? group by report.reportphysician";//计算每个报告医生的检查报告分值之和。
		
//		String reportphysician_sql="select report.reportphysician,sum(dic_examitem.coefficient) as coefficient from studyorder,studyitem left join dic_examitem on studyitem.item_id=dic_examitem.id,report  "
//				+ "where studyorder.id=studyitem.orderid and studyorder.id=report.studyorderfk "
//				+ "and report.reporttime >=? group by report.reportphysician";//计算每个报告医生的检查报告分值之和。
//		String pre_auditphysician_sql="select report.pre_auditphysician,sum(dic_examitem.coefficient) as coefficient from studyorder,studyitem left join dic_examitem on studyitem.item_id=dic_examitem.id,report "
//				+ "where studyorder.id=studyitem.orderid and studyorder.id=report.studyorderfk "
//				+ "and report.pre_audittime>=? group by report.pre_auditphysician";//计算每个初审医生的检查报告分值之和。
//		String auditphysician_sql="select report.auditphysician,sum(dic_examitem.coefficient) as coefficient from studyorder,studyitem left join dic_examitem on studyitem.item_id=dic_examitem.id,report "
//				+ "where studyorder.id=studyitem.orderid and studyorder.id=report.studyorderfk "
//				+ "and report.audittime>=? group by report.auditphysician";//计算每个审核医生的检查报告分值之和。
		
//		Map<Integer,Integer> reportphysician_map= Db.find(reportphysician_sql,now.format(DateTimeFormatter.ISO_LOCAL_DATE),now.format(DateTimeFormatter.ISO_LOCAL_DATE)).stream().collect(Collectors.toMap(x->x.getInt("report_assignee"),x->x.getInt("coefficient")));
//		Map<Integer,Integer> pre_auditphysician_map= Db.find(pre_auditphysician_sql,now.format(DateTimeFormatter.ISO_LOCAL_DATE)).stream().collect(Collectors.toMap(x->x.getInt("pre_auditphysician"),x->x.getInt("coefficient")));
//		Map<Integer,Integer> auditphysician_map= Db.find(auditphysician_sql,now.format(DateTimeFormatter.ISO_LOCAL_DATE)).stream().collect(Collectors.toMap(x->x.getInt("auditphysician"),x->x.getInt("coefficient")));
		if(flag){
			if(enbaleFunc("enable_ADMQ")) {
				modalitymap= DicModality.dao.find("select distinct departmentid ,type from dic_modality where role='modality' and  type is not null and deleted=0")
					.stream().collect(Collectors.groupingBy(x->x.getInt("departmentid")));
			}
			if(enbaleFunc("enable_SD")) {
				subsRuleMap=DeptSubscriptionRule.dao.find("select * from dept_subscription_rule").stream().collect(Collectors.toMap(DeptSubscriptionRule::getUserid, DeptSubscriptionRule::getModalities));
				subsRuleModalityMap=DeptSubscriptionModality.dao.find("select * from dept_subscription_modality").stream().collect(Collectors.groupingBy(x->x.getUserid()));
				subsRuleExamItemMap=DeptSubscriptionExamitem.dao.find("select * from dept_subscription_examitem").stream().collect(Collectors.groupingBy(x->x.getUserid()));
			}
			getOndutyPhysician().forEach((k,v)->{
				v.forEach(x->{
					Integer userid=x.getInt("userid");
					Physician p=new Physician();
					p.setDept_id(k);
					p.setPhysician_id(userid);
					p.setPhysician_name(x.getStr("employeename"));
					p.setStarttime(x.getStr("starttime"));
					p.setEndtime(x.getStr("endtime"));
					p.setShiftid(x.getInt("shiftid"));
					p.setWorktimeid(x.getInt("worktimeid"));
					p.setOrderids(x.getStr("orderids"));
					if(modalitymap!=null){
						List<DicModality> modalitylist=modalitymap.get(k);
						if(modalitylist!=null){
							p.setModality_map(modalitylist.stream().collect(Collectors.toMap(DicModality::getType, DicModality::getId)));
						}
					}
					if(enbaleFunc("enable_SD")) {
						p.setSubcriptionRule(subsRuleMap, subsRuleModalityMap, subsRuleExamItemMap);
					}
					String type=x.getStr("type");
					switch(type) {
						case "23"://报告医生
	//						Integer reportphysician_coefficient= reportphysician_map.get(userid);
	//						if(reportphysician_coefficient!=null) {
	//							p.plusScore(reportphysician_coefficient,null);
	//						}
							p.setType("23");
							reportphysician.add(p);
							break;
						case "27"://初审医生
	//						Integer pre_auditphysician_coefficient= pre_auditphysician_map.get(userid);
	//						if(pre_auditphysician_coefficient!=null) {
	//							p.plusScore(pre_auditphysician_coefficient,null);
	//						}
							p.setType("27");
							pre_auditphysician.add(p);
							break;
						case "31"://审核医生
	//						Integer auditphysician_coefficient= auditphysician_map.get(userid);
	//						if(auditphysician_coefficient!=null) {
	//							p.plusScore(auditphysician_coefficient,null);
	//						}
							p.setType("31");
							auditphysician.add(p);
							break;
					}
					
				});
			});
			
			CacheKit.put("workforce_data", "reportphysician"+today, reportphysician);
			CacheKit.put("workforce_data", "pre_auditphysician"+today, pre_auditphysician);
			CacheKit.put("workforce_data", "auditphysician"+today, auditphysician);
			CacheKit.getCacheManager().getCache("workforce_data").flush();
		}
	}
	
	/**
	 *   初始化配置，按照检查报告难度平均分配
	 * Average distribution according to report difficulty
	 */
	private void initADQ(boolean reset) {
		String today=LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		if(!reset){
			reportphysician=CacheKit.get("workforce_data", "reportphysician"+today);
			pre_auditphysician=CacheKit.get("workforce_data", "pre_auditphysician"+today);
			auditphysician=CacheKit.get("workforce_data", "auditphysician"+today);
		}
		log.info(reportphysician);
		boolean flag=false;
		if(reportphysician==null){
			flag=true;
			reportphysician=new ArrayList<Physician>();//Collections.synchronizedList(new ArrayList<Physician>());
		}
		if(pre_auditphysician==null){
			pre_auditphysician=new ArrayList<Physician>();//Collections.synchronizedList(new ArrayList<Physician>());
		}
		if(auditphysician==null){
			auditphysician=new ArrayList<Physician>();//Collections.synchronizedList(new ArrayList<Physician>());
		}
		if(flag){
			if(enbaleFunc("enable_ADMQ")) {
				modalitymap= DicModality.dao.find("select distinct 0 as id, departmentid ,type from dic_modality where role='modality' and  type is not null and deleted=0")
					.stream().collect(Collectors.groupingBy(x->x.getInt("departmentid")));
			}
			if(enbaleFunc("enable_SD")) {
				subsRuleMap=DeptSubscriptionRule.dao.find("select * from dept_subscription_rule").stream().collect(Collectors.toMap(DeptSubscriptionRule::getUserid, DeptSubscriptionRule::getModalities));
				subsRuleModalityMap=DeptSubscriptionModality.dao.find("select * from dept_subscription_modality").stream().collect(Collectors.groupingBy(x->x.getUserid()));
				subsRuleExamItemMap=DeptSubscriptionExamitem.dao.find("select * from dept_subscription_examitem").stream().collect(Collectors.groupingBy(x->x.getUserid()));
			}
			getOndutyPhysician().forEach((k,v)->{
				v.forEach(x->{
					Integer userid=x.getInt("userid");
					Physician p=new Physician();
					p.setDept_id(k);
					p.setPhysician_id(userid);
					p.setPhysician_name(x.getStr("employeename"));
					p.setStarttime(x.getStr("starttime"));
					p.setEndtime(x.getStr("endtime"));
					p.setShiftid(x.getInt("shiftid"));
					p.setWorktimeid(x.getInt("worktimeid"));
					p.setWorktimename(x.getStr("worktimename"));
					p.setOrderids(x.getStr("orderids"));
					if(modalitymap!=null){
						List<DicModality> modalitylist=modalitymap.get(k);
						if(modalitylist!=null){
							p.setModality_map(modalitylist.stream().collect(Collectors.toMap(DicModality::getType, DicModality::getId)));
						}
					}
					if(enbaleFunc("enable_SD")) {
						p.setSubcriptionRule(subsRuleMap, subsRuleModalityMap, subsRuleExamItemMap);
					}
					String type=x.getStr("type");
					switch(type) {
						case "23"://报告医生
							p.setType("23");
							reportphysician.add(p);
							break;
						case "27"://初审医生
							p.setType("27");
							pre_auditphysician.add(p);
							break;
						case "31"://审核医生
							p.setType("31");
							auditphysician.add(p);
							break;
					}
					
				});
			});
			
			CacheKit.put("workforce_data", "reportphysician"+today, reportphysician);
			CacheKit.put("workforce_data", "pre_auditphysician"+today, pre_auditphysician);
			CacheKit.put("workforce_data", "auditphysician"+today, auditphysician);
			CacheKit.getCacheManager().getCache("workforce_data").flush();
		}
	}
	/*
	 * 获取当天的所有排版医生
	 * */
	private Map<Integer , List<Record>> getOndutyPhysician() {
//		List ret=null;
		String sql="select dept_shift_work.userid,(select name from users where users.id=dept_shift_work.userid) as employeename,dept_shift_work.employeeid,dept_shift_work.postcode,dept_shift_work.institutionid"
				+ ",dept_shift_work.deptid,dept_shift_work.shiftid,dept_shift_work.shift_name,dept_worktime.id as worktimeid,dept_worktime.name as worktimename,dept_worktime.starttime,dept_worktime.endtime,dept_shift.type "
				+ "from dept_shift_work left join dept_worktime on dept_shift_work.worktimeid= dept_worktime.id "
				+ "left join dept_shift on dept_shift.id = dept_shift_work.shiftid "
				+ "where postcode='D' and dept_shift_work.workdate=? order by institutionid ,deptid,shiftid,worktimeid";

		return Db.find(sql,LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)).stream()
				.collect(Collectors.groupingBy(x->x.getInt("deptid")));
	}
	
	public synchronized Integer getReportAssignee(Integer examitem_coefficient,Integer orderid,Record re) {
		Integer ret=null;
		if(enbaleFunc("enable_report_task")){
			if(enbaleFunc("enable_ADARD")){
				ret=getReportAssignee_ADARD(examitem_coefficient,orderid,re);
			} else if(enbaleFunc("enable_ADQ")){
				ret=getReportAssignee_ADQ(examitem_coefficient,orderid,re);
			}
			
		}
		return ret;
	}
	
	public synchronized Integer getPreAuditPhysicianAssignee(Integer examitem_coefficient,Integer orderid,Record re) {
		Integer ret=null;
		if(enbaleFunc("enable_report_task")){
			if(enbaleFunc("enable_ADARD")){
				ret=getPreAuditPhysicianAssignee_ADARD(examitem_coefficient,orderid,re);
			} else if(enbaleFunc("enable_ADQ")){
				ret=getPreAuditPhysicianAssignee_ADQ(examitem_coefficient,orderid,re);
			}
			
		}
		return ret;
	}
	
	public synchronized Integer getAuditPhysicianAssignee(Integer examitem_coefficient,Integer orderid,Record re) {
		Integer ret=null;
		if(enbaleFunc("enable_report_task")){
			if(enbaleFunc("enable_ADARD")){
				ret=getAuditPhysicianAssignee_ADARD(examitem_coefficient,orderid,re);
			} else if(enbaleFunc("enable_ADQ")){
				ret=getAuditPhysicianAssignee_ADQ(examitem_coefficient,orderid,re);
			}
			
		}
		return ret;
	}

	/*
	 * 根据检查报告难度平均分配规则获取指定的报告医生，有订阅规则的医生优先于无订阅规则的医生
	 * examitem_coefficient :检查项目的难度系数
	 * orderid: 通过orderid获得对应患者历史检查，进而获得最近的一次历史检查时的报告医生id
	 * re: 存储参数
	 * 				1 reassgin_physicianid：重新指派报告医生时，标识原来的报告医生id，会跳过次id。非重新指派时请置为null。重新指派报告医生将忽略“将同一患者的多个检查分配给同一诊断人员”规则
	 * 				2 studydatetime : 检查时间
	 * 				3 modality : orderid对应的检查的设备类型，如：CT、MR...
	 * 				4 modalityid : orderid对应的检查的检查设备，如：西门子force
	 * 				5 examitemids : orderid对应的检查项目id，多个检查项目时以“,”分隔。
	 * */
	public synchronized Integer getReportAssignee_ADARD(Integer examitem_coefficient,Integer orderid,Record re) {
		Integer ret=null;
		if(enbaleFunc("enable_AMEFSPTSP")&&(re==null||re.getInt("reassgin_physicianid")==null)){//将同一患者的多个检查分配给同一诊断人员
			Record record=Db.findFirst("select top 1 report.reportphysician from studyorder,report "
					+ "where studyorder.id=report.studyorderfk and patientidfk=(select patientidfk from studyorder where studyorder.id=?) "
					+ "order by report.reporttime desc",orderid);
			if(record!=null&&record.getInt("reportphysician")!=null){
				ret=record.getInt("reportphysician");
				Optional<Physician> o = reportphysician.stream().filter(x->x.onDuty()&&x.getPhysician_id().intValue()==record.getInt("reportphysician").intValue()).findFirst();
				if(o.isPresent()){
					Physician p=o.get();
					p.plusScore(examitem_coefficient,orderid);
				}
			}
		}
		if(ret==null){
			Optional<Physician> o=null;
			if(enbaleFunc("enable_SD")) {//先根据订阅规则匹配医生
				o=reportphysician.stream().filter(
						x->x.onDuty()//判断医生当班
						&&(re==null||re.getInt("reassgin_physicianid")==null||x.getPhysician_id().intValue()!=re.getInt("reassgin_physicianid").intValue())//重新指定时，跳过原来的医生
						&&x.matchSubscriptionRule(re)) //匹配订阅规则
						.min((p1,p2)->p1.getReport_score().compareTo(p2.getReport_score()));
			} 
			if(o==null){//其次再匹配无订阅的医生
				o=reportphysician.stream().filter(
						x->x.onDuty()//判断医生当班
						&&(re==null||re.getInt("reassgin_physicianid")==null||x.getPhysician_id().intValue()!=re.getInt("reassgin_physicianid").intValue())//重新指定时，跳过原来的医生
						&&!x.hasSubscribe()) //无订阅规则的医生
						.min((p1,p2)->p1.getReport_score().compareTo(p2.getReport_score()));
			}
			if (o!=null&&o.isPresent()) {
				o.get().plusScore(examitem_coefficient,orderid);
				ret=o.get().getPhysician_id();
			}
		}
		//重新指派报告医生，减去原来医生系数
		if(ret!=null&&re!=null){
			Integer reassgin_physicianid=re.getInt("reassgin_physicianid");
			Date studydatetime=re.getDate("studydatetime");
			if(reassgin_physicianid!=null&&ret.intValue()!=reassgin_physicianid.intValue()&&studydatetime!=null){
				LocalDate ld_studydatetime= studydatetime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				if(ld_studydatetime.isEqual(LocalDate.now())){//是否当天，只有当天才减系数
					Optional<Physician> o = reportphysician.stream().filter(x->x.onDuty()
							&&StrKit.notBlank(x.getOrderids())
							&&x.getOrderids().indexOf("'"+orderid+"'")>=0
							&&x.getPhysician_id().intValue()==reassgin_physicianid.intValue()).findFirst();
					if(o.isPresent()){
						Physician p=o.get();
						p.minusScore(examitem_coefficient,orderid);
					}
				}
			}
		}
		String today=LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		CacheKit.put("workforce_data", "reportphysician"+today, reportphysician);
		CacheKit.getCacheManager().getCache("workforce_data").flush();
		return ret;
	}
	
	/*
	 * 根据检查数量平均分配规则获取指定的报告医生
	 * count :检查项目的数量
	 * orderid: 通过orderid获得对应患者历史检查，进而获得最近的一次历史检查时的报告医生id
	 * re: 存储参数
	 * 				1 reassgin_physicianid：重新指派报告医生时，标识原来的报告医生id，会跳过次id。非重新指派时请置为null。重新指派报告医生将忽略“将同一患者的多个检查分配给同一诊断人员”规则
	 * 				2 studydatetime : 检查时间
	 * 				3 modality: 启用按照设备类型平均分配，存储orderid对应的设备类型
	 * 				4 modalityid : orderid对应的检查的检查设备，如：西门子force
	 * 				5 examitemids : orderid对应的检查项目id，多个检查项目时以“,”分隔。
	 * */
	public synchronized Integer getReportAssignee_ADQ(Integer count,Integer orderid,Record re) {
		Integer ret=null;
		if(enbaleFunc("enable_AMEFSPTSP")&&(re==null||re.getInt("reassgin_physicianid")==null)){//将同一患者的多个检查分配给同一诊断人员
			Record record=Db.findFirst("select top 1 report.reportphysician from studyorder,report "
					+ "where studyorder.id=report.studyorderfk and patientidfk=(select patientidfk from studyorder where studyorder.id=?) "
					+ "order by report.reporttime desc",orderid);
			if(record!=null&&record.getInt("reportphysician")!=null){
				ret=record.getInt("reportphysician");
				Optional<Physician> o = reportphysician.stream().filter(x->x.onDuty()&&x.getPhysician_id().intValue()==record.getInt("reportphysician").intValue()).findFirst();
				if(o.isPresent()){
					Physician p=o.get();
					p.plusCount(count,orderid);
				}
			}
		}
		if(ret==null){
			List<Physician> list_filter=null;
			if(enbaleFunc("enable_SD")) {//先根据订阅规则匹配医生
				list_filter=reportphysician.stream().filter(
						 	x->x.onDuty()//判断医生是否当班
							&&(re==null||re.getInt("reassgin_physicianid")==null||x.getPhysician_id().intValue()!=re.getInt("reassgin_physicianid").intValue())//重新指定时，跳过原来的医生
							&&x.matchSubscriptionRule(re))//匹配订阅规则
							.collect(Collectors.toList());
			}
			if(list_filter==null||list_filter.size()==0){//根据订阅规则没有匹配到医生，再匹配无订阅的医生
				list_filter=reportphysician.stream().filter(
							x->x.onDuty()//判断医生是否当班
							&&(re==null||re.getInt("reassgin_physicianid")==null||x.getPhysician_id().intValue()!=re.getInt("reassgin_physicianid").intValue())//重新指定时，跳过原来的医生
							&&!x.hasSubscribe())//无订阅规则的医生
							.collect(Collectors.toList());
			}
			if(enbaleFunc("enable_ADMQ")) {//按照设备类型平均分配
				Optional<Physician> o=list_filter.stream().min((p1,p2)->p1.getModality_map().get(re.getStr("modality")).compareTo(p2.getModality_map().get(re.getStr("modality"))));
				if (o.isPresent()) {
					o.get().plusCount(count,orderid,re.getStr("modality"));
					ret=o.get().getPhysician_id();
				}
			} else {
				Optional<Physician> o=list_filter.stream().min((p1,p2)->p1.getReport_count().compareTo(p2.getReport_count()));
				if (o.isPresent()) {
					o.get().plusCount(count,orderid);
					ret=o.get().getPhysician_id();
				}
			}
		}
		//重新指派报告医生，减去原来医生系数
		if(ret!=null&&re!=null){
			Integer reassgin_physicianid=re.getInt("reassgin_physicianid");
			Date studydatetime=re.getDate("studydatetime");
			if(reassgin_physicianid!=null&&ret.intValue()!=reassgin_physicianid.intValue()&&studydatetime!=null){
				LocalDate ld_studydatetime= studydatetime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				if(ld_studydatetime.isEqual(LocalDate.now())){//是否当天，只有当天才减系数
					Optional<Physician> o = reportphysician.stream().filter(x->x.onDuty()
							&&StrKit.notBlank(x.getOrderids())
							&&x.getOrderids().indexOf("'"+orderid+"'")>=0
							&&x.getPhysician_id().intValue()==reassgin_physicianid.intValue()).findFirst();
					if(o.isPresent()){
						Physician p=o.get();
						if(enbaleFunc("enable_ADMQ")) {//按照设备类型平均分配
							p.minusCount(count,orderid,re.getStr("modality"));
						} else {
							p.minusCount(count,orderid);
						}
					}
				}
			}
		}
		String today=LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		CacheKit.put("workforce_data", "reportphysician"+today, reportphysician);
		CacheKit.getCacheManager().getCache("workforce_data").flush();
		return ret;
	}
	
	/*
	 * 根据检查报告难度平均分配规则获取指定的报告初审医生
	 * examitem_coefficient :检查项目的难度系数
	 * re: 存储参数
	 * 				1 reassgin_physicianid：重新指派初审医生时，标识原来的初审医生id，会跳过次id。非重新指派时请置为null。
	 * 				2 studydatetime : 检查时间
	 * 				3 modality : orderid对应的检查的设备类型，如：CT、MR...
	 * 				4 modalityid : orderid对应的检查的检查设备，如：西门子force
	 * 				5 examitemids : orderid对应的检查项目id，多个检查项目时以“,”分隔。
	 * 
	 * */
	public synchronized Integer getPreAuditPhysicianAssignee_ADARD(Integer examitem_coefficient,Integer orderid,Record re) {
		Integer ret=null;
		Optional<Physician> op=null;
		if(enbaleFunc("enable_SD")) {//先根据订阅规则匹配医生
			op=pre_auditphysician.stream().filter(
					x->x.onDuty()//判断医生当班
					&&(re==null||re.getInt("reassgin_physicianid")==null||x.getPhysician_id().intValue()!=re.getInt("reassgin_physicianid").intValue())//重新指定时，跳过原来的医生
					&&x.matchSubscriptionRule(re)) //匹配订阅规则
					.min((p1,p2)->p1.getReport_score().compareTo(p2.getReport_score()));
		} 
		if(op==null){//其次再匹配无订阅的医生
			op=reportphysician.stream().filter(
					x->x.onDuty()//判断医生当班
					&&(re==null||re.getInt("reassgin_physicianid")==null||x.getPhysician_id().intValue()!=re.getInt("reassgin_physicianid").intValue())//重新指定时，跳过原来的医生
					&&!x.hasSubscribe()) //无订阅规则的医生
					.min((p1,p2)->p1.getReport_score().compareTo(p2.getReport_score()));
		}
		if (op!=null&&op.isPresent()) {
			op.get().plusScore(examitem_coefficient,orderid);
			ret=op.get().getPhysician_id();
		}
		
		//重新指派初审医生，减去原来医生系数
		if(ret!=null&&re!=null){
			Integer reassgin_physicianid=re.getInt("reassgin_physicianid");
			Date studydatetime=re.getDate("studydatetime");
			if(reassgin_physicianid!=null&&ret.intValue()!=reassgin_physicianid.intValue()&&studydatetime!=null){
				LocalDate ld_studydatetime= studydatetime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				if(ld_studydatetime.isEqual(LocalDate.now())){//是否当天，只有当天才减系数
					Optional<Physician> o = pre_auditphysician.stream().filter(x->x.onDuty()
							&&StrKit.notBlank(x.getOrderids())
							&&x.getOrderids().indexOf("'"+orderid+"'")>=0
							&&x.getPhysician_id().intValue()==reassgin_physicianid.intValue()).findFirst();
					if(o.isPresent()){
						Physician ph=o.get();
						ph.minusScore(examitem_coefficient,orderid);
					}
				}
			}
		}
		String today=LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		CacheKit.put("workforce_data", "pre_auditphysician"+today, pre_auditphysician);
		CacheKit.getCacheManager().getCache("workforce_data").flush();
		return ret;
	}
	
	/*
	 * 根据检查数量平均分配规则获取指定的初审医生
	 * examitem_coefficient :检查项目的难度系数
	 * re: 存储参数
	 * 				1 reassgin_physicianid：重新指派初审医生时，标识原来的初审医生id，会跳过次id。非重新指派时请置为null。
	 * 				2 studydatetime : 检查时间
	 * 				3 modality: 启用按照设备类型平均分配，存储orderid对应的设备类型
	 * 				4 modalityid : orderid对应的检查的检查设备，如：西门子force
	 * 				5 examitemids : orderid对应的检查项目id，多个检查项目时以“,”分隔。
	 * */
	public synchronized Integer getPreAuditPhysicianAssignee_ADQ(Integer count,Integer orderid,Record re) {
		Integer ret=null;
		List<Physician> list_filter=null;
		if(enbaleFunc("enable_SD")) {//先根据订阅规则匹配医生
			list_filter=pre_auditphysician.stream().filter(
					 	x->x.onDuty()//判断医生是否当班
						&&(re==null||re.getInt("reassgin_physicianid")==null||x.getPhysician_id().intValue()!=re.getInt("reassgin_physicianid").intValue())//重新指定时，跳过原来的医生
						&&x.matchSubscriptionRule(re))//匹配订阅规则
						.collect(Collectors.toList());
		}
		if(list_filter==null||list_filter.size()==0){//根据订阅规则没有匹配到医生，再匹配无订阅的医生
			list_filter=pre_auditphysician.stream().filter(
						x->x.onDuty()//判断医生是否当班
						&&(re==null||re.getInt("reassgin_physicianid")==null||x.getPhysician_id().intValue()!=re.getInt("reassgin_physicianid").intValue())//重新指定时，跳过原来的医生
						&&!x.hasSubscribe())//无订阅规则的医生
						.collect(Collectors.toList());
		}
		
		if(StrKit.equals(getConfiguration().getEnableAdmq(), "1")) {//按照设备类型平均分配
			Optional<Physician> o=list_filter.stream().min((p1,p2)->p1.getModality_map().get(re.getStr("modality")).compareTo(p2.getModality_map().get(re.getStr("modality"))));
			if (o.isPresent()) {
				o.get().plusCount(count,orderid,re.getStr("modality"));
				ret=o.get().getPhysician_id();
			}
		} else {
			Optional<Physician> op=list_filter.stream().min((p1,p2)->p1.getReport_count().compareTo(p2.getReport_count()));
			if(op.isPresent()){
				op.get().plusCount(count,orderid);
				ret=op.get().getPhysician_id();
			}
		}
		//重新指派初审医生，减去原来医生系数
		if(ret!=null&&re!=null){
			Integer reassgin_physicianid=re.getInt("reassgin_physicianid");
			Date studydatetime=re.getDate("studydatetime");
			if(reassgin_physicianid!=null&&ret.intValue()!=reassgin_physicianid.intValue()&&studydatetime!=null){
				LocalDate ld_studydatetime= studydatetime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				if(ld_studydatetime.isEqual(LocalDate.now())){//是否当天，只有当天才减系数
					Optional<Physician> o = pre_auditphysician.stream().filter(x->x.onDuty()
							&&StrKit.notBlank(x.getOrderids())
							&&x.getOrderids().indexOf("'"+orderid+"'")>=0
							&&x.getPhysician_id().intValue()==reassgin_physicianid.intValue()).findFirst();
					if(o.isPresent()){
						Physician ph=o.get();
						if(enbaleFunc("enable_ADMQ")) {//按照设备类型平均分配
							ph.minusCount(count,orderid,re.getStr("modality"));
						} else {
							ph.minusCount(count,orderid);
						}
					}
				}
			}
		}
		String today=LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		CacheKit.put("workforce_data", "pre_auditphysician"+today, pre_auditphysician);
		CacheKit.getCacheManager().getCache("workforce_data").flush();
		return ret;
	}
	
	/*
	 * 根据检查报告难度平均分配规则获取指定的报告审核医生
	 * examitem_coefficient :检查项目的难度系数
	 * re: 存储参数
	 * 				1 reassgin_physicianid：重新指派审核医生时，标识原来的审核医生id，会跳过次id。非重新指派时请置为null。
	 * 				2 studydatetime : 检查时间
	 * 				3 modality: 启用按照设备类型平均分配，存储orderid对应的设备类型
	 * 				4 modalityid : orderid对应的检查的检查设备，如：西门子force
	 * 				5 examitemids : orderid对应的检查项目id，多个检查项目时以“,”分隔。
	 * */
	public synchronized Integer getAuditPhysicianAssignee_ADARD(Integer examitem_coefficient,Integer orderid,Record re) {
		Integer ret=null;
		Optional<Physician> op=null;
		if(enbaleFunc("enable_ADMQ")) {//先根据订阅规则匹配医生
			op=auditphysician.stream().filter(
					x->x.onDuty()//判断医生当班
					&&(re==null||re.getInt("reassgin_physicianid")==null||x.getPhysician_id().intValue()!=re.getInt("reassgin_physicianid").intValue())//重新指定时，跳过原来的医生
					&&x.matchSubscriptionRule(re)) //匹配订阅规则
					.min((p1,p2)->p1.getReport_score().compareTo(p2.getReport_score()));
		} 
		if(op==null){//其次再匹配无订阅的医生
			op=auditphysician.stream().filter(
					x->x.onDuty()//判断医生当班
					&&(re==null||re.getInt("reassgin_physicianid")==null||x.getPhysician_id().intValue()!=re.getInt("reassgin_physicianid").intValue())//重新指定时，跳过原来的医生
					&&!x.hasSubscribe()) //无订阅规则的医生
					.min((p1,p2)->p1.getReport_score().compareTo(p2.getReport_score()));
		}
		if (op!=null&&op.isPresent()) {
			op.get().plusScore(examitem_coefficient,orderid);
			ret=op.get().getPhysician_id();
		}

		//重新指派审核医生，减去原来医生系数
		if(ret!=null&&re!=null){
			Integer reassgin_physicianid=re.getInt("reassgin_physicianid");
			Date studydatetime=re.getDate("studydatetime");
			if(reassgin_physicianid!=null&&ret.intValue()!=reassgin_physicianid.intValue()&&studydatetime!=null){
				LocalDate ld_studydatetime= studydatetime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				if(ld_studydatetime.isEqual(LocalDate.now())){//是否当天，只有当天才减系数
					Optional<Physician> o = auditphysician.stream().filter(x->x.onDuty()
							&&StrKit.notBlank(x.getOrderids())
							&&x.getOrderids().indexOf("'"+orderid+"'")>=0
							&&x.getPhysician_id().intValue()==reassgin_physicianid.intValue()).findFirst();
					if(o.isPresent()){
						Physician ph=o.get();
						ph.minusScore(examitem_coefficient,orderid);
					}
				}
			}
		}
		String today=LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		CacheKit.put("workforce_data", "auditphysician"+today, auditphysician);
		CacheKit.getCacheManager().getCache("workforce_data").flush();
		return ret;
	}
	
	/*
	 * 根据检查数量平均分配规则获取指定的审核医生
	 * examitem_coefficient :检查项目的难度系数
	 * re: 存储参数
	 * 				1 reassgin_physicianid：重新指派审核医生时，标识原来的审核医生id，会跳过次id。非重新指派时请置为null。
	 * 				2 studydatetime : 检查时间
	 * 				3 modality: 启用按照设备类型平均分配，存储orderid对应的设备类型
	 * 				4 modalityid : orderid对应的检查的检查设备，如：西门子force
	 * 				5 examitemids : orderid对应的检查项目id，多个检查项目时以“,”分隔。
	 * */
	public synchronized Integer getAuditPhysicianAssignee_ADQ(Integer count,Integer orderid,Record re) {
		Integer ret=null;
		List<Physician> list_filter=null;
		if(enbaleFunc("enable_SD")) {//先根据订阅规则匹配医生
			list_filter=auditphysician.stream().filter(
					 	x->x.onDuty()//判断医生是否当班
						&&(re==null||re.getInt("reassgin_physicianid")==null||x.getPhysician_id().intValue()!=re.getInt("reassgin_physicianid").intValue())//重新指定时，跳过原来的医生
						&&x.matchSubscriptionRule(re))//匹配订阅规则
					 	.collect(Collectors.toList());
		}
		if(list_filter==null||list_filter.size()==0){//根据订阅规则没有匹配到医生，再匹配无订阅的医生
			list_filter=auditphysician.stream().filter(
						x->x.onDuty()//判断医生是否当班
						&&(re==null||re.getInt("reassgin_physicianid")==null||x.getPhysician_id().intValue()!=re.getInt("reassgin_physicianid").intValue())//重新指定时，跳过原来的医生
						&&!x.hasSubscribe())//无订阅规则的医生
						.collect(Collectors.toList());
		}
		if(StrKit.equals(getConfiguration().getEnableAdmq(), "1")) {//按照设备类型平均分配
			Optional<Physician> o=list_filter.stream().min((p1,p2)->p1.getModality_map().get(re.getStr("modality")).compareTo(p2.getModality_map().get(re.getStr("modality"))));
			if (o.isPresent()) {
				o.get().plusCount(count,orderid,re.getStr("modality"));
				ret=o.get().getPhysician_id();
			}
		} else {
			Optional<Physician> op=list_filter.stream().min((p1,p2)->p1.getReport_score().compareTo(p2.getReport_score()));
			if(op.isPresent()){
				op.get().plusCount(count,orderid);
				ret=op.get().getPhysician_id();
			}
		}
		//重新指派审核医生，减去原来医生系数
		if(ret!=null&&re!=null){
			Integer reassgin_physicianid=re.getInt("reassgin_physicianid");
			Date studydatetime=re.getDate("studydatetime");
			if(reassgin_physicianid!=null&&ret.intValue()!=reassgin_physicianid.intValue()&&studydatetime!=null){
				LocalDate ld_studydatetime= studydatetime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				if(ld_studydatetime.isEqual(LocalDate.now())){//是否当天，只有当天才减系数
					Optional<Physician> o = auditphysician.stream().filter(x->x.onDuty()
							&&StrKit.notBlank(x.getOrderids())
							&&x.getOrderids().indexOf("'"+orderid+"'")>=0
							&&x.getPhysician_id().intValue()==reassgin_physicianid.intValue()).findFirst();
					if(o.isPresent()){
						Physician ph=o.get();
						if(enbaleFunc("enable_ADMQ")) {//按照设备类型平均分配
							ph.minusCount(count,orderid,re.getStr("modality"));
						} else {
							ph.minusCount(count,orderid);
						}
					}
				}
			}
		}
		String today=LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		CacheKit.put("workforce_data", "auditphysician"+today, auditphysician);
		CacheKit.getCacheManager().getCache("workforce_data").flush();
		return ret;
	}

	public static void main(String[] args) {
		
	}
	
	//public class Physician implements Comparable<Physician> , Serializable {}

}
