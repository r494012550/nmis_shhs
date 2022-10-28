package com.healta.plugin.workforce;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.healta.model.DeptSubscriptionExamitem;
import com.healta.model.DeptSubscriptionModality;
import com.healta.model.DicExamitem;
import com.healta.model.DicModality;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

public class Physician implements Comparable<Physician> , Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8193781666280852746L;
	//orderids 被分配的orderid字符串 
	public String orderids;
	//医生id
	public Integer physician_id;
	//医生姓名
	public String physician_name;
	//写完报告的总分值
	public Integer report_score=0;
	//科室id
	public Integer dept_id;
	//班次id
	public Integer shiftid;
	//时间段id
	public Integer worktimeid;
	//时间段名称
	public String worktimename;
	//医生排版的开始时间
	public String starttime;
	//医生排版的结束时间
	public String endtime;
	//医生类型 "23":报告医生，"27":初审医生，"31":审核医生
	public String type;
	//按设备类型的报告数量 key：设备类型（CT/MR...）,Integer：报告数量，初始值为0
	public Map<String ,Integer> modality_map;
	//报告数量
	public Integer report_count=0;
	//订阅设备类型
	public String subscription_modality_type;
	//订阅的设备
	public List<DeptSubscriptionModality> subscription_modality;
	//订阅的检查项目
	public List<DeptSubscriptionExamitem> subscription_examitem;
	
	public String getSubscription_modality_type() {
		return subscription_modality_type;
	}

	public void setSubscription_modality_type(String subscription_modality_type) {
		this.subscription_modality_type = subscription_modality_type;
	}

	public List<DeptSubscriptionModality> getSubscription_modality() {
		return subscription_modality;
	}

	public void setSubscription_modality(List<DeptSubscriptionModality> subscription_modality) {
		this.subscription_modality = subscription_modality;
	}

	public List<DeptSubscriptionExamitem> getSubscription_examitem() {
		return subscription_examitem;
	}

	public void setSubscription_examitem(List<DeptSubscriptionExamitem> subscription_examitem) {
		this.subscription_examitem = subscription_examitem;
	}

	public String getWorktimename() {
		return worktimename;
	}

	public void setWorktimename(String worktimename) {
		this.worktimename = worktimename;
	}

	public Map<String,Integer> getModality_map() {
		return modality_map;
	}

	public void setModality_map(Map<String,Integer> modality_map) {
		this.modality_map = modality_map;
	}

	public Integer getReport_count() {
		return report_count;
	}

	public void setReport_count(Integer report_count) {
		this.report_count = report_count;
	}

	public String getOrderids() {
		return orderids;
	}

	public void setOrderids(String orderids) {
		this.orderids = orderids;
	}

	public void setReport_score(Integer report_score) {
		this.report_score = report_score;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getShiftid() {
		return shiftid;
	}

	public void setShiftid(Integer shiftid) {
		this.shiftid = shiftid;
	}

	public Integer getWorktimeid() {
		return worktimeid;
	}

	public void setWorktimeid(Integer worktimeid) {
		this.worktimeid = worktimeid;
	}

	public String getPhysician_name() {
		return physician_name;
	}

	public void setPhysician_name(String physician_name) {
		this.physician_name = physician_name;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public Integer getDept_id() {
		return dept_id;
	}

	public void setDept_id(Integer dept_id) {
		this.dept_id = dept_id;
	}

	public Integer getPhysician_id() {
		return physician_id;
	}

	public void setPhysician_id(Integer physician_id) {
		this.physician_id = physician_id;
	}

	public Integer getReport_score() {
		return report_score;
	}

//	public void setReport_score(Integer report_score) {
//		this.report_score = report_score;
//	}
	
	public void plusScore(Integer report_score,Integer orderid){
		if(StrKit.notBlank(getOrderids())&&orderid!=null){
			if(getOrderids().indexOf("'"+orderid+"'")<0){
				this.report_score=this.report_score+report_score;
				plusOrderids(orderid);
			}
		}
		else{
			this.report_score=this.report_score+report_score;
			plusOrderids(orderid);
		}
	}
	
	public void plusCount(Integer report_count,Integer orderid){
		if(StrKit.notBlank(getOrderids())&&orderid!=null){
			if(getOrderids().indexOf("'"+orderid+"'")<0){
				this.report_count=this.report_count+report_count;
				plusOrderids(orderid);
			}
		}
		else{
			this.report_count=this.report_count+report_count;
			plusOrderids(orderid);
		}
	}
	
	public void plusCount(Integer report_count,Integer orderid,String modality){
		if(StrKit.notBlank(getOrderids())&&orderid!=null){
			if(getOrderids().indexOf("'"+orderid+"'")<0){
				this.getModality_map().put(modality, this.getModality_map().get(modality)+report_count);
				plusOrderids(orderid);
			}
		}
		else{
			this.getModality_map().put(modality, this.getModality_map().get(modality)+report_count);
			plusOrderids(orderid);
		}
	}
	
	private void plusOrderids(Integer orderid){
		setOrderids(getOrderids()+"'"+orderid+"',");
	}
	
	public void minusScore(Integer report_score,Integer orderid){
		if(this.report_score.compareTo(report_score)>=0&&StrKit.notBlank(getOrderids())&&getOrderids().indexOf("'"+orderid+"'")>=0){
			this.report_score=this.report_score-report_score;
			setOrderids(getOrderids().replaceAll("'"+orderid+"'", ""));
		}
	}
	
	public void minusCount(Integer report_count,Integer orderid){
		if(this.report_count.compareTo(report_count)>=0&&StrKit.notBlank(getOrderids())&&getOrderids().indexOf("'"+orderid+"'")>=0){
			this.report_count=this.report_count-report_count;
			setOrderids(getOrderids().replaceAll("'"+orderid+"'", ""));
		}
	}
	
	public void minusCount(Integer report_count,Integer orderid,String modality){
		Integer c=this.getModality_map().get(modality);
		if(c!=null&&c.compareTo(report_count)>=0&&StrKit.notBlank(getOrderids())&&getOrderids().indexOf("'"+orderid+"'")>=0){
			this.getModality_map().put(modality, c-report_count);
			setOrderids(getOrderids().replaceAll("'"+orderid+"'", ""));
		}
	}
	
	/*
	 * 判断医生当前时间是否上班
	 * */
	public Boolean onDuty(){
		String now_str=LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
		if(getStarttime().compareTo(getEndtime())<0) {
			if(now_str.compareTo(starttime)<0||now_str.compareTo(endtime)>0) {
				return false;
			} else {
				return true;
			}
		} else {
			if(now_str.compareTo(starttime)>=0||now_str.compareTo(endtime)<=0) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public int compareTo(Physician p) {
		// TODO Auto-generated method stub
		return this.report_score-p.getReport_score();
	}
	
	public Boolean hasSubscribe(){
		if(StrKit.notBlank(getSubscription_modality_type())||getSubscription_modality()!=null||getSubscription_examitem()!=null){
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean matchSubscriptionRule(Record re){
		String modality=re.getStr("modality");
		Integer modalityid=re.getInt("modalityid");
		String examitemids=re.getStr("examitemids");
		if(StrKit.notBlank("modality")&&StrKit.notBlank(getSubscription_modality_type())){
			if(Arrays.asList(getSubscription_modality_type().split(",")).contains(modality)){
				return true;
			}
		}
		
		if(modalityid!=null&&getSubscription_modality()!=null){
			if(getSubscription_modality().stream().allMatch(x->x.getModalityid().intValue()==modalityid.intValue())){
				return true;
			}
		}
		
		if(StrKit.notBlank("examitemids")&&getSubscription_examitem()!=null){
			Integer itemid=Integer.valueOf(examitemids.split(",")[0]);
			if(getSubscription_examitem().stream().allMatch(x->x.getItemid().intValue()==itemid.intValue())){
				return true;
			}
		} 
		
		return false;
	}
	
	public void setSubcriptionRule(Map<Integer, String> rule_map,Map<Integer, List<DeptSubscriptionModality>> modality_map,Map<Integer, List<DeptSubscriptionExamitem>> examitem_map){
		if(physician_id!=null&&rule_map!=null){
			setSubscription_modality_type(rule_map.get(physician_id));
		}
		
		if(physician_id!=null&&modality_map!=null){
			setSubscription_modality(modality_map.get(physician_id));
		}
		
		if(physician_id!=null&&examitem_map!=null){
			setSubscription_examitem(examitem_map.get(physician_id));
		}
	}
	
}
