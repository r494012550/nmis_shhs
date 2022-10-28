package com.healta.plugin.sequence;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.healta.constant.CacheName;
import com.healta.constant.SequenceNoStatus;
import com.healta.constant.StudyOrderStatus;
import com.healta.model.DicModality;
import com.healta.model.Studyorder;
import com.healta.model.StudyorderNumber;
import com.healta.util.SequenceNo_Generator;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class SequenceService {
	private final static Logger log = Logger.getLogger(SequenceService.class);
	
	private final static int defaultaveragetime=5;
	//private final static int starttime=481;//minute   08:00
	
	public static void start(){
		
		LocalDate today=LocalDate.now();
		//删除今天之前的数据
		Db.delete("delete from studyorder_number where createtime < ?" ,Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		//获取工作时间
		String worktime=PropKit.use("system.properties").get("workday_of_worktime");
		if(DayOfWeek.SATURDAY.equals(today.getDayOfWeek())){
			worktime=PropKit.use("system.properties").get("saturday_of_worktime",worktime);
		}
		else if(DayOfWeek.SUNDAY.equals(today.getDayOfWeek())){
			worktime=PropKit.use("system.properties").get("sunday_of_worktime",worktime);
		}

//		List<DicModality> dics=DicModality.dao.findByCache(CacheName.DICCACHE, "dic_modality", "select * from dic_modality");
		List<DicModality> dics=DicModality.dao.find("select * from dic_modality where role='modality'");
		for(DicModality dic:dics){
			List<StudyorderNumber> sns=StudyorderNumber.dao.find("select * from studyorder_number where modalityid=? and createtime=?",
					dic.getId(),Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));

			if(sns==null||sns.size()==0){
				
				//获取当天的该设备的预约信息
				List<Studyorder> orders=Studyorder.dao.find("select id,modality_type,appointmenttime,sequencenumber from studyorder "
						+ "where modalityid=? and appointmenttime>=? and appointmenttime<? and status=?",dic.getId(),
						Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()),Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),StudyOrderStatus.scheduled);
				Map<String,Integer> map = orders.stream().collect(Collectors.toMap(Studyorder::getSequencenumber, Studyorder::getId));

				//设备的工作时间不为空时
				if(DayOfWeek.SATURDAY.equals(today.getDayOfWeek())){
					worktime=StringUtils.isNotBlank(dic.getSaturdayOfWorktime())?dic.getSaturdayOfWorktime():worktime;
				}else if(DayOfWeek.SUNDAY.equals(today.getDayOfWeek())){
					worktime=StringUtils.isNotBlank(dic.getSundayOfWorktime())?dic.getSundayOfWorktime():worktime;
				}else {
					worktime=StringUtils.isNotBlank(dic.getWorkdayOfWorktime())?dic.getWorkdayOfWorktime():worktime;
				}
				
				Integer avatime=dic.getAveragetime();
				if(avatime==null){
					avatime=defaultaveragetime;
				}
				
				int n=1;
				//根据设备工作时间生成序号，并且将预约的需要状态改成已预约'2'
				List<Integer> minutes=getWorkTime(worktime,dic.getAveragetime());
				for(Integer min:minutes){
					StudyorderNumber sn=new StudyorderNumber();
					sn.set("modalityid", dic.getId()).set("sequencenumber", n)
					.set("scopeoftime", min)
					.set("status", map.get(SequenceNo_Generator.genrateNumber(dic.getType(), n))!=null?SequenceNoStatus.sch_reg_used:SequenceNoStatus.usable)
					.set("createtime", Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant())).save();
					n++;				
				}
			}
		}
		
		
	}
	
	
	public static List<Integer> getWorkTime(String worktime,Integer interval){
		//08:00-12:00,14:00-16:00
		
		List<Integer> ret=new ArrayList<Integer>();
		String[] wts=worktime.split(",");
		for(String wt:wts){
			String[] tmps=wt.split("-");
			LocalTime am=LocalTime.parse(tmps[0]);//LocalTime.of(Integer.valueOf(tmps[0].substring(1, tmps[0].indexOf(":"))), Integer.valueOf(tmps[0].substring(tmps[0].indexOf(":")+1)));
			LocalTime pm=LocalTime.parse(tmps[1]);//LocalTime.of(Integer.valueOf(tmps[1].substring(1, tmps[1].indexOf(":"))), Integer.valueOf(tmps[1].substring(tmps[1].indexOf(":")+1)));
			
			while(am.compareTo(pm)<0){
				ret.add(am.getHour()*60+am.getMinute());
				am=am.plusMinutes(interval);
			}
		}
		
		return ret;
	}
	
	public static List<Integer> getScheduleTime(String worktime,Integer appointment_time){
		//08:00-12:00,14:00-16:00    60min
		
		List<Integer> ret=new ArrayList<Integer>();
		String[] wts=worktime.split(",");
		for(String wt:wts){
			String[] tmps=wt.split("-");
			LocalTime am=LocalTime.parse(tmps[0]);//LocalTime.of(Integer.valueOf(tmps[0].substring(1, tmps[0].indexOf(":"))), Integer.valueOf(tmps[0].substring(tmps[0].indexOf(":")+1)));
			LocalTime pm=LocalTime.parse(tmps[1]);//LocalTime.of(Integer.valueOf(tmps[1].substring(1, tmps[1].indexOf(":"))), Integer.valueOf(tmps[1].substring(tmps[1].indexOf(":")+1)));
			
			while(am.compareTo(pm)<0){
				ret.add(am.getHour()*60+am.getMinute());
				am=am.plusMinutes(appointment_time);
			}
		}
		
		return ret;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
