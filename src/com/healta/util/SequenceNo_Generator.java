package com.healta.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.constant.SequenceNoStatus;
import com.healta.constant.StudyOrderStatus;
import com.healta.model.DicModality;
import com.healta.model.Studyorder;
import com.healta.model.StudyorderNumber;
import com.healta.plugin.sequence.SequenceService;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;


public class SequenceNo_Generator {
	
	private final static Logger log = Logger.getLogger(SequenceNo_Generator.class);
	private static final String regEx="[^0-9]";
	private static String STR="0000";
	private static int DEFAULT_LEN=4;
	private static int APPOINTMENT_TIME = 60;
	private static int APPOINTMENT_NUMBER = 10;
	
	/**
	 * 为每台设备所分配的病人生成当天序号
	 */
	public synchronized static String getRegNumber(Studyorder studyorder){
		String number="";
		Integer modalityid = studyorder.getModalityid();
		LocalDateTime now=LocalDateTime.now();
		Date now_date=Date.from(now.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
		//获取第一个可用的序号
		Record record=Db.findFirst("select top 1 * from studyorder_number where modalityid=? and status=? and createtime=? order by sequencenumber,scopeoftime",
				modalityid,SequenceNoStatus.usable,now_date);
		//获取成功后，将序号状态改为被使用'4'
		if(record!=null){
			number=genrateNumber(studyorder.getModalityType(),record.getInt("sequencenumber"));
			int ret=Db.update("update studyorder_number set status=? where id=? and status=?",SequenceNoStatus.used,record.getInt("id"),SequenceNoStatus.usable);
			if(ret!=1){
				throw new RuntimeException("get Reg sequencenumber error");
			}
		}
		else{
			//正常产生的序号不够用，需要产生额外的序号
			Record record_add=Db.findFirst("select max(sequencenumber)+1 as sequencenumber_add from studyorder_number where modalityid=? and createtime=?",
					modalityid,now_date);
			if(record_add!=null){
				StudyorderNumber sn=new StudyorderNumber();
				sn.set("modalityid", modalityid).set("sequencenumber", record_add.getInt("sequencenumber_add"))
				.set("scopeoftime", 0)
				.set("status", SequenceNoStatus.used)
				.set("createtime", now_date).save();
				number=genrateNumber(studyorder.getModalityType(),record_add.getInt("sequencenumber_add"));
			}
		}
		return number;
	}
	
	/*
	 * 预约转登记后需要更新序号状态
	 * 由原先的占用状态'2'，改为被使用'4'
	 * 
	 * */
	public synchronized static boolean updateStatus(Integer modalityid,Date appointmenttime){
		LocalDateTime apptime = LocalDateTime.ofInstant(appointmenttime.toInstant(), ZoneId.systemDefault());
		int app=(apptime.getHour()*60+apptime.getMinute());
		Date date=Date.from(apptime.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
		log.info("时间：" + date + ", app:" + app);
		int count=Db.update("update studyorder_number set status=? where modalityid=? and status=? and scopeoftime=? and createtime=?",
				SequenceNoStatus.used,modalityid,SequenceNoStatus.sch_reg_used,app,date);
		log.info("count="+count);
		if (count==1) {
			return true;
		} else {
			log.error("update studyorder_number status error:modalityid="+modalityid+";appointmenttime="+appointmenttime);
			return false;
		}
	}
	
	/**
	 * 为每台设备所分配的病人生成当天序号
	 */
	public synchronized static String getSchNumber(Studyorder studyorder){
		String number="";
		Integer modalityid = studyorder.getModalityid();
		LocalDateTime apptime=LocalDateTime.ofInstant(studyorder.getAppointmenttime().toInstant(), ZoneId.systemDefault());
		
		DicModality dic=DicModality.dao.findById(modalityid);
		String worktime=StringUtils.isNotBlank(dic.getWorkdayOfWorktime())?dic.getWorkdayOfWorktime():PropKit.use("system.properties").get("workday_of_worktime");
		if(DayOfWeek.SATURDAY.equals(apptime.getDayOfWeek())){
			worktime=StringUtils.isNotBlank(dic.getSaturdayOfWorktime())?dic.getSaturdayOfWorktime():PropKit.use("system.properties").get("saturday_of_worktime",worktime);
		}
		else if(DayOfWeek.SUNDAY.equals(apptime.getDayOfWeek())){
			worktime=StringUtils.isNotBlank(dic.getSundayOfWorktime())?dic.getSundayOfWorktime():PropKit.use("system.properties").get("sunday_of_worktime",worktime);
		}
		List<Integer> minutes=SequenceService.getWorkTime(worktime, dic.getAveragetime());
		int app=(apptime.getHour()*60+apptime.getMinute());
		for(int i=0;i<minutes.size();i++){
			if(minutes.get(i).intValue()==app){
				number=genrateNumber(studyorder.getModalityType(),i+1);
				break;
			}
		}
		
		return number;
	}
	
	public static String  genrateNumber(String type, int newSnint) {
		int len=DEFAULT_LEN;
		return type+STR.substring(0, len-(newSnint+"").length())+newSnint;
	}

	public static String timeFormat(Integer minutes){
		Integer hours=minutes/60;
		Integer minute=minutes%60;
		
		return (hours<10?("0"+hours):hours)+":"+(minute<10?("0"+minute):minute);
	}
	
	/**
	 * 为每台设备所分配的病人生成当天序号
	 */
	public synchronized static String getSchNumber_new(Studyorder studyorder){
		String number="";
		int count = 0;
		LocalDateTime apptime=LocalDateTime.ofInstant(studyorder.getAppointmenttime().toInstant(), ZoneId.systemDefault());
		DicModality dic=DicModality.dao.findById(studyorder.getModalityid());
		int appointment_time = dic.getAppointmentTime()==null?APPOINTMENT_TIME:dic.getAppointmentTime();
		int appointment_number = dic.getAppointmentTime()==null?APPOINTMENT_NUMBER:dic.getAppointmentNumber();
		String worktime=StringUtils.isNotBlank(dic.getWorkdayOfWorktime())?dic.getWorkdayOfWorktime():PropKit.use("system.properties").get("workday_of_worktime");
		if(DayOfWeek.SATURDAY.equals(apptime.getDayOfWeek())){
			worktime=StringUtils.isNotBlank(dic.getSaturdayOfWorktime())?dic.getSaturdayOfWorktime():PropKit.use("system.properties").get("saturday_of_worktime",worktime);
		}
		else if(DayOfWeek.SUNDAY.equals(apptime.getDayOfWeek())){
			worktime=StringUtils.isNotBlank(dic.getSundayOfWorktime())?dic.getSundayOfWorktime():PropKit.use("system.properties").get("sunday_of_worktime",worktime);
		}
		Record record = Db.findFirst("SELECT COUNT(*) AS number FROM studyorder WHERE sequencenumber IS NOT null AND modalityid=? AND appointmenttime>=? AND appointmenttime<? AND status<?",
				studyorder.getModalityid(), Date.from(apptime.atZone(ZoneId.systemDefault()).toInstant()), Date.from(apptime.plusMinutes(appointment_time).atZone(ZoneId.systemDefault()).toInstant()),StudyOrderStatus.registered);
		List<Integer> minutes=SequenceService.getWorkTime(worktime, appointment_time);
		int app=(apptime.getHour()*60+apptime.getMinute());
		for(int i=0;i<minutes.size();i++){
			if(minutes.get(i).intValue()==app){
				count = i;
				break;
			}
		}
		if(record==null) {
			number = genrateNumber(studyorder.getModalityType(), count*appointment_number+1);
		}else {
			number = genrateNumber(studyorder.getModalityType(), count*appointment_number+record.getInt("number")+1);
		}
		
		return number;
	}
	
	public synchronized static String getRegNumber_new(Studyorder studyorder){
		LocalDate today=LocalDate.now();
		String number="";
		Record record = Db.findFirst("SELECT COUNT(*) AS number FROM studyorder WHERE sequencenumber IS NOT null AND modalityid=? AND createtime>=? AND createtime<? AND status>=?",
				studyorder.getModalityid(), Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()), StudyOrderStatus.registered);
		if(record==null) {
			number = genrateNumber(studyorder.getModalityType(), 1);
		}else {
			number = genrateNumber(studyorder.getModalityType(), record.getInt("number")+1);
		}
		
		return number;
	}
	
}
