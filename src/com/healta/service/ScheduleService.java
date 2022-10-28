package com.healta.service;

import java.io.File;
import java.sql.SQLException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.healta.constant.PatientSource;
import com.healta.constant.ReportStatus;
import com.healta.constant.SearchTimeConstant;
import com.healta.constant.StudyOrderPriority;
import com.healta.constant.StudyOrderStatus;
import com.healta.constant.StudyprocessStatus;
import com.healta.model.Admission;
import com.healta.model.DicEmployee;
import com.healta.model.DicModality;
import com.healta.model.Filter;
import com.healta.model.Mwlitem;
import com.healta.model.Patient;
import com.healta.model.Reportremark;
import com.healta.model.StudyImage;
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
import com.healta.util.IDUtil;
import com.healta.util.ParaKit;
import com.healta.util.SequenceNo_Generator;
import com.healta.util.SyscodeKit;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class ScheduleService {
	private static final Logger log = Logger.getLogger(ScheduleService.class);
	
	/**
	  *  预约显示
	 * @param schedule_date  预约时间
	 * @param modalityid  设备类型
	 * @return
	 */
	public HashMap<String,Record> showSchedules(String schedule_date, int modalityid) {
		Date da = DateKit.toDate(schedule_date);
		Calendar calendar = new GregorianCalendar(); 
		calendar.setTime(da); 
		calendar.add(calendar.DATE,1); //把日期往后增加一天,整数  往后推,负数往前移动 
		da=calendar.getTime(); //这个时间就是日期往后推一天的结果 
		
		log.info("nextday="+da.toLocaleString());
		
		List<Record> list = Db.find("select patientname,appointmenttime,admission.modifytime,studyorder.id as orderid from patient,admission,studyorder "
				+ " where patient.id=studyorder.patientidfk AND patient.id = admission.patientidfk AND studyorder.admissionidfk = admission.id "
				+ " and status='1' and studyorder.modalityid=? and appointmenttime>=? and appointmenttime<?",modalityid,schedule_date,da);
		log.info("此时间段，预约的人数有" + list.size() + "人");
		HashMap<String,Record> map = new HashMap<String,Record>();
		for (Record so:list) {
			Date date = so.getDate("appointmenttime");
			map.put(date.getHours()+":"+date.getMinutes(), so);
		}
		return map;
	}
	
	/**
	  * 预约显示
	 * @param schedule_date 预约时间
	 * @param modalityid 设备类型
	 * @return
	 */
	public HashMap<String, List<Record>> showSchedulesDetail(String schedule_date, int modalityid) {
	    Date da = DateKit.toDate(schedule_date);
        Calendar calendar = new GregorianCalendar(); 
        calendar.setTime(da); 
        calendar.add(calendar.DATE,1); //把日期往后增加一天,整数  往后推,负数往前移动 
        da=calendar.getTime(); //这个时间就是日期往后推一天的结果 
        
        log.info("nextday="+da.toLocaleString());
        
        List<Record> list = Db.find("select patientname,appointmenttime,admission.modifytime,studyorder.id as orderid from patient,admission,studyorder "
                + " where patient.id=studyorder.patientidfk AND patient.id = admission.patientidfk AND studyorder.admissionidfk = admission.id "
                + "  and studyorder.modalityid=? and appointmenttime>=? and appointmenttime<?",modalityid,schedule_date,da);
        //预约时间界面 去除 and status='1' 条件    ---2020-08-28 hx
        log.info("此时间段，预约的人数有" + list.size() + "人");
        HashMap<String, List<Record>> map = new HashMap<String, List<Record>>();
        for (Record so:list) {
            Date date = so.getDate("appointmenttime");
            List<Record> lists = map.get(date.getHours()*60+"");
            if (CollectionUtils.isEmpty(lists)) {
                lists = new ArrayList<Record>();
            }
            lists.add(so);
            map.put(date.getHours()*60 + "", lists);
        }
        log.info("map:" + JSONObject.valueToString(map));
        return map;
    }
	
	/**
	 *  获取该时间的设备有哪些被预约
	 * @param schedule_date  预约的时间 （eg：2019-11-14）
	 * @param modalityid  预约的设备类型的id
	 * @return
	 */
	public HashMap<Integer,Record> getScheduleTime(LocalDate schedule_date,int modalityid) {
		List<Record> list=Db.find("select distinct appointmenttime,studyorder.id from patient,studyorder "
				+ "where patient.id=studyorder.patientidfk and status='1' and studyorder.modalityid=? and appointmenttime>=? and appointmenttime<?",modalityid,
				Date.from(schedule_date.atStartOfDay(ZoneId.systemDefault()).toInstant()),Date.from(schedule_date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		log.info("开始时间：" + Date.from(schedule_date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		log.info("结束时间：" + Date.from(schedule_date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		HashMap<Integer,Record> map = new HashMap<Integer,Record>();
		for (Record so:list) {
			LocalTime lt = so.getDate("appointmenttime").toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
			map.put((lt.getHour()*60+lt.getMinute()), so);
		}
		return map;
	}
	
	public HashMap<Integer,Record> getScheduleTime_new(LocalDate schedule_date, int modalityid,Integer orderid) {
		List<Record> list=Db.find("SELECT DISTINCT appointmenttime,studyorder.id FROM studyorder WHERE status=? AND studyorder.modalityid=? and appointmenttime>=? and appointmenttime<?",
				StudyOrderStatus.scheduled, modalityid, Date.from(schedule_date.atStartOfDay(ZoneId.systemDefault()).toInstant()),Date.from(schedule_date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		log.info("开始时间：" + Date.from(schedule_date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		log.info("结束时间：" + Date.from(schedule_date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		HashMap<Integer,Record> map = new HashMap<Integer,Record>();
		for (Record so:list) {
			LocalTime lt = so.getDate("appointmenttime").toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
			Record record = map.get(lt.getHour()*60+lt.getMinute());
			if(record == null) {
				record = new Record();
				record.set("num", 0);
				record.set("thisorder", false);
			}
			record.set("num", record.getInt("num")+1);
			if(orderid!=null && so.getInt("id").intValue() == orderid.intValue()) {
				record.set("thisorder", true);
			}
			map.put((lt.getHour()*60+lt.getMinute()), record);
		}
		return map;
	}
	
//	public HashMap<String,Record> getScheduleTime2(String schedule_date,int modalityid,int orderid){
//		
//		Date da=DateKit.toDate(schedule_date);
//		Calendar calendar = new GregorianCalendar(); 
//		calendar.setTime(da); 
//		calendar.add(calendar.DATE,1); //把日期往后增加一天,整数  往后推,负数往前移动 
//		da=calendar.getTime(); //这个时间就是日期往后推一天的结果 
//		
//		log.info("nextday="+da.toLocaleString());
//		
//		List<Record> list=Db.find("select distinct appointmenttime from patient,studyorder "
//				+ "where patient.id=studyorder.patientidfk and status='1' and studyorder.modalityid=? and appointmenttime>=? and appointmenttime<? and studyorder.id <> ?",modalityid,schedule_date,da,orderid);
//		HashMap<String,Record> map=new HashMap<String,Record>();
//		for(Record so:list){
//			Date date=so.getDate("appointmenttime");
//			map.put(date.getHours()+":"+date.getMinutes(), so);
//		}
//		
//		return map;
//	}
//	
//	public HashMap<String,Record> getScheduleTime3(String schedule_date,int modalityid,int orderid){
//		
//		Date da=DateKit.toDate(schedule_date);
//		Calendar calendar = new GregorianCalendar(); 
//		calendar.setTime(da); 
//		calendar.add(calendar.DATE,1); //把日期往后增加一天,整数  往后推,负数往前移动 
//		da=calendar.getTime(); //这个时间就是日期往后推一天的结果 
//		
//		log.info("nextday="+da.toLocaleString());
//		
//		List<Record> list=Db.find("select distinct appointmenttime from patient,studyorder "
//				+ "where patient.id=studyorder.patientidfk and status='1' and studyorder.modalityid=? and appointmenttime>=? and appointmenttime<? and studyorder.id = ?",modalityid,schedule_date,da,orderid);
//		HashMap<String,Record> map=new HashMap<String,Record>();
//		for(Record so:list){
//			Date date=so.getDate("appointmenttime");
//			System.out.println("*************"+date.getHours()+":"+date.getMinutes());
//			map.put(date.getHours()+":"+date.getMinutes(), so);
//		}
//		
//		return map;
//	}
	
	/**
	  * 检查此时间段是否可预约
	 * @param appointmenttime  预约的时间
	 * @param modalityid 检查的设备id
	 * @param studyorderid 检查预约信息表id
	 */
	public Boolean checkScheduleTime(String appointmenttime, Integer modalityid, Integer studyorderid) {
	    log.info("预约的时间:" + appointmenttime + ",检查的设备id:" + modalityid + ",预约信息表id:" + studyorderid);
	    Boolean ret = true;
        List<Studyorder> list = Studyorder.dao.find("SELECT DISTINCT * FROM studyorder WHERE status = ? AND modalityid=? AND appointmenttime = ?",
                StudyOrderStatus.scheduled, modalityid, appointmenttime);
        DicModality modality = DicModality.dao.findById(modalityid);
	    if(studyorderid == null && list.size() >= modality.getAppointmentNumber()){
	    	// studyorderid == null 预约信息初次保存
            // 此时间段人数已经超过
            ret = false;
	       
        }else if(studyorderid != null && list.size() >= modality.getAppointmentNumber()){
        	// studyorderid ！= null 预约信息修改
        	ret = false;
            for(Studyorder so : list) {
            	if(so.getId().intValue() == studyorderid.intValue()) {//修改的信息的时间段是原来的时间段
            		ret = true;
            		break;
            	}
            } 
        }
	    return ret;
    }
	
	/**
	  *  保存预约信息
	 * @param patient 病人信息
	 * @param so  检查预约信息
	 * @param admission
	 * @param itemstr  检查项目
	 * @param patientremark
	 * @param admissionremark
	 * @param studyorderremark
	 * @param user
	 * @param scanimgs
	 * @return
	 */
	public boolean saveSchedule(Patient patient,Studyorder so,Admission admission, HashMap<String, Object> parammap, User user){
		boolean succeed=Db.tx(new IAtom() {
			public boolean run() throws SQLException{
					boolean ret=true;
					Date now = new Date();
					String patientid=patient.getPatientid();
			  		if(patientid==null){
			  			patientid=IDUtil.getPatientID();
			  			patient.setPatientid(patientid);
			  			patient.setCreator(user.getUsername());
			  			ret=ret&&patient.remove("id").save();
			  		}else {
			  			patient.update();
			  		}

			  		if(admission.getAdmissionid()==null) {
			  			admission.setAdmissionid(IDUtil.getAdmissionID());
			  			admission.setPatientidfk(patient.getId());
			  			admission.setCreator(user.getUsername());
			  			ret=ret&&admission.remove("id").save();
			  		}else{
			  			admission.update();
			  		}

			  		so.setPatientid(patientid);
			  		so.setPatientidfk(patient.getId());
			  		so.setAdmissionidfk(admission.getId());
			  		so.setAccessionnumber(admission.getAdmissionid());
			  		
			  		if(PatientSource.emergency.equals(admission.getPatientsource())) {
			  			so.setPri(StudyOrderPriority.emergency);
			  		}else {
			  			so.setPri(StudyOrderPriority.normal);
			  		}
			  		if(so.getStudyid()==null){
			  			so.setStudyid(IDUtil.getStudyID(so.getModalityType(), so.getAppdeptcode()));
			  			so.setStatus(StudyOrderStatus.scheduled);
			  		}
			  		so.setCreator(user.getUsername());
			  		so.setCreatorname(user.getName());
			  		so.setAppdatetime(now);
			  		if(PropKit.use("system.properties").getBoolean("generate_sn_or_not", true)) {
			  			so.setSequencenumber(SequenceNo_Generator.getSchNumber_new(so));//生成预约序号
			  		}
			  		ret=ret&&so.remove("id").save();
			  		
			  		//保存备注
			  		saveRemark((String) parammap.get("patientremark"), (String) parammap.get("admissionremark"),
			  				(String) parammap.get("studyorderremark"),
			  				patient, so, admission, now, user);
					StringBuilder studyitems=new StringBuilder();
			  		try {
			  			updateStudyitem((String) parammap.get("itemstr"), so, studyitems, now);
			  			//将检查项目保存进studyorder中
				  		String str = studyitems.toString();
				  		if(str.length()>0){
				  			so.setStudyitems(str.substring(0,str.length()-1));
				  		}
		  			}catch(Exception e) {
						e.printStackTrace();
						return false;
					}	
			  		
			  		updateMwlitem(patient, admission, so, now);
				
			  		String[] scanimgs = (String[]) parammap.get("scanimgs");
			  		if (scanimgs!=null) {
			  			StudyImage si=new StudyImage();
  						si.setOrderId(so.getId());
  						si.setStudyid(so.getStudyid());
			  			for(int i=0;i<scanimgs.length;i++){
			  				try{
			  					File file=new File(PropKit.get("base_upload_path")+"\\apply\\tmp\\"+scanimgs[i]);
			  					if(file.exists()){
		  							String filename=file.getName();
		  							String dirname=LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		  							FileUtils.moveFileToDirectory(file, new File(PropKit.get("base_upload_path")+"\\apply\\"+dirname), true);
		  							si.set("img"+(i+1), "/apply/"+dirname+"/"+filename);
			  					}
				  			}
					  		catch(Exception ex){
					  			ex.printStackTrace();
					  		}
			  			}
			  			si.remove("id").save();
			  		}

			  		ret = ret&&so.update();//更新studyorder
			  		
			  		if(ret) {
			  			//发送检查进程-预约
				  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
				  				.set("patientfk", patient.getId())
				  				.set("admissionfk", admission.getId())
								.set("studyorderfk", so.getId())
								.set("status", StudyOrderStatus.scheduled)
								.set("operator", user.getUsername())
								.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
			  		}

			  		
					return ret;
				      
				}
			});
		return succeed;
		
	}
	
	/**
	  *  预约信息修改
	 * @param patient
	 * @param so
	 * @param admission
	 * @param itemstr  预约检查的项目
	 * @param patientremark
	 * @param admissionremark
	 * @param studyorderremark
	 * @param user
	 * @param scanimgs
	 * @return
	 */
	public boolean modifySchedule(Patient patient,Studyorder so,Admission admission, HashMap<String, Object> parammap, User user){
		boolean succeed=Db.tx(new IAtom() {
			public boolean run() throws SQLException{
				boolean ret = true;
				boolean changeQue = false;
				Date now = new Date();
				patient.setModifytime(now);
				ret = ret&&patient.update();

				admission.setModifytime(now);
				ret = ret&&admission.update();
				
				Studyorder beforeso = Studyorder.dao.findById(so.getId());
				if(beforeso.getModalityid().intValue() != so.getModalityid().intValue()) {
			  		changeQue=true;
				}
				so.setModifytime(now);
				if(PropKit.use("system.properties").getBoolean("generate_sn_or_not", true)) {
					so.setSequencenumber(SequenceNo_Generator.getSchNumber_new(so));//生成预约序号
				}

				StringBuilder studyitems = new StringBuilder();
				Db.update("DELETE FROM studyitem WHERE orderid = ?", so.getId());
				try {
		  			updateStudyitem((String) parammap.get("itemstr"), so, studyitems, now);
		  			//将检查项目保存进studyorder中
			  		String str = studyitems.toString();
			  		if(str.length()>0){
			  			so.setStudyitems(str.substring(0,str.length()-1));
			  		}
	  			}catch(Exception e) {
					e.printStackTrace();
					return false;
				}
				
				ret=ret&&so.update();
				
				if(changeQue) {
					updateMwlitem(patient, admission, so, now);
				}
				
				//保存备注
		  		saveRemark((String) parammap.get("patientremark"), (String) parammap.get("admissionremark"),
		  				(String) parammap.get("studyorderremark"),
		  				patient, so, admission, now, user);
				
		  		String[] scanimgs = (String[]) parammap.get("scanimgs");
				if(scanimgs!=null){
		  			StudyImage si=StudyImage.dao.findFirst("select top 1 * from study_image where orderid=?",so.getId());
		  			
		  			if(si==null){
		  				si=new StudyImage();
		  				si.setOrderId(so.getId());
		  				si.setStudyid(so.getStudyid());
		  			}
					
		  			for(int i=0;i<scanimgs.length;i++){
		  				try{
		  					File file=new File(PropKit.get("base_upload_path")+"\\apply\\tmp\\"+scanimgs[i]);
		  					if(file.exists()){
	  							String filename=file.getName();
	  							String dirname=LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	  							FileUtils.moveFileToDirectory(file, new File(PropKit.get("base_upload_path")+"\\apply\\"+dirname), true);
	  							si.set("img"+(i+1), "/apply/"+dirname+"/"+filename);
		  					}
			  			}
				  		catch(Exception ex){
				  			ex.printStackTrace();
				  		}
		  			}
		  			if(si.getId()!=null){
		  				for(int i=(scanimgs.length+1);i<11;i++){
		  					si.set("img"+i, null);
		  				}
		  				si.update();
		  			}
		  			else{
		  				si.remove("id").save();
		  			}
		  		}
				else{
					Db.update("delete from study_image where orderid=?",so.getId());
				}
				
				//发送检查进程-修改预约信息
		  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
		  				.set("patientfk", patient.getId())
		  				.set("admissionfk", admission.getId())
						.set("studyorderfk", so.getId())
						.set("status", StudyprocessStatus.modifyschedule)
						.set("operator", user.getUsername())
						.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
				return ret;
			}
		});
		return succeed;
	}
	
	/**
	 *  查询预约信息
	 * @param map 需要查询的内容
	 * @param syscode_lan 中英文
	 * @return
	 */
	public Page<Record> getSchelderorder(Map<String, String[]> map, String syscode_lan){

			List<Object> list = new ArrayList<>();
			String from=null;
			String to=null;
			LocalDate now=LocalDate.now();
			// 时间选择 'T'今天(today) 'Y'昨天(yesterday) 'TD'三天(ThreeDay) 'FD'五天(FiveDay) ' 'W'一周 'M'一月 '
			
			//         'T1'今天 ，'TO'明天  ，'FT'后天，'LF'后五天，'，'LW'后一周，'LM' 后一月 
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
	            
			}  else if (StrKit.notBlank(map.get("appdate")) && StrKit.equals("T1", map.get("appdate")[0])) {  
				from = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			} else if (StrKit.notBlank(map.get("appdate")) && StrKit.equals("TM", map.get("appdate")[0])){
				from = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				to = now.plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			} else if (StrKit.notBlank(map.get("appdate")) && StrKit.equals("FT", map.get("appdate")[0])){
				from = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				to = now.plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			} else if (StrKit.notBlank(map.get("appdate")) && StrKit.equals("LF", map.get("appdate")[0])){
				from = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				to = now.plusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			} else if (StrKit.notBlank(map.get("appdate")) && StrKit.equals("LW", map.get("appdate")[0])){
				from = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				to = now.plusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			} else if (StrKit.notBlank(map.get("appdate")) && StrKit.equals("LM", map.get("appdate")[0])) {// 明天
				from = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	            to = now.plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			}
			
			if (StrKit.notBlank(map.get("datefrom")) && StrKit.notBlank(map.get("datefrom")[0])) {
				from = map.get("datefrom")[0] +" 00:00:00";
			}
			if (StrKit.notBlank(map.get("dateto")) && StrKit.notBlank(map.get("dateto")[0])) {
				to = map.get("dateto")[0] +" 23:59:59";
			}
			
			String where="";
			
			String timecon = "";
			if (StrKit.equals("registertime",map.get("datetype")[0])) {
				timecon = "studyorder.regdatetime";
			} else if (StrKit.equals("studytime",map.get("datetype")[0])) {
				timecon = "studyorder.studydatetime";
			} else if (StrKit.equals("reporttime",map.get("datetype")[0])) {
				timecon = "studyorder.reporttime";
			} else if (StrKit.equals("appointmenttime",map.get("datetype")[0])) {
				timecon = "studyorder.appointmenttime";
			} else if (StrKit.equals("arrivedtime",map.get("datetype")[0])) {
				timecon = "studyorder.arrivedtime";
			}
			
			if(map.get("startfromtime")!=null && StrKit.notBlank(map.get("startfromtime")[0])) {
				where += " and DATEPART(hh,"+timecon+" )*60+DATEPART(mi,"+timecon+")> " +  map.get("startfromtime")[0].substring(0, 2)+"*60";
			}
			if(map.get("endtotime")!=null && StrKit.notBlank(map.get("endtotime")[0])) {
				where += " and DATEPART(hh,"+timecon+") *60+DATEPART(mi,"+timecon+")< " + map.get("endtotime")[0].substring(0, 2)+"*60";
			}
			
			if(StrKit.notBlank(from)){
				where+=" and "+timecon+" >='"+from+"'";
			}
			if(StrKit.notBlank(to)){
				where+=" and "+timecon+" <'"+to+"'";
			}
			
			//检查状态
			if(StrKit.notBlank(map.get("orderstatus"))){
				String[] orderstatus = map.get("orderstatus");
				if(orderstatus.length == 1 && StrKit.notBlank(orderstatus[0])) {
					where += " and studyorder.status = ?";
					list.add(map.get("orderstatus")[0]);
				}else if(orderstatus.length > 1) {
					String in = "(";
					for(String str : orderstatus) {
						in += "?,";
						list.add(str);
					}
					where += " and studyorder.status in " + in.substring(0, in.length()-1) + ")";
				}
			}
			
			//检查类型
			if(StrKit.notBlank(map.get("modality"))) {
				String[] modalitys = map.get("modality");
				if(modalitys.length == 1 && StrKit.notBlank(modalitys[0])) {
					where += " and studyorder.modality_type  = ?";
					list.add(modalitys[0]);
				}else if(modalitys.length > 1) {
					String in = "(";
					for(String str : modalitys) {
						in += "?,";
						list.add(str);
					}
					where += " and studyorder.modality_type in " + in.substring(0, in.length()-1) + ")";
				}
			}
			// 报告状态 O未写 P未审 F已审 G我的报告 
			if (map.get("reportstatus") != null && StrKit.notBlank(map.get("reportstatus")[0])) {
			    if (ReportStatus.Noresult.equals(map.get("reportstatus")[0])) {
			        where += " and (report.reportstatus is null or report.reportstatus = ?)";
			    } else {
			        where += " and report.reportstatus = ?";
			    }
			    list.add(map.get("reportstatus")[0]);  
			}
			if(StrKit.notBlank(map.get("studyid")) && StrKit.notBlank(map.get("studyid")[0])){
				where+=" and studyorder.studyid =?";
				list.add(map.get("studyid")[0].replace(" ", ""));
			}
			if(StrKit.notBlank(map.get("patientname")) && StrKit.notBlank(map.get("patientname")[0])){
				where+=" and patient.patientname LIKE CONCAT('%',?,'%')";
				list.add(map.get("patientname")[0].replace(" ", ""));
			}
			if(StrKit.notBlank(map.get("patientid")) && StrKit.notBlank(map.get("patientid")[0])){
				where+=" and patient.patientid =?";
				list.add(map.get("patientid")[0].replace(" ", ""));
			}
			if(StrKit.notBlank(map.get("cardno")) && StrKit.notBlank(map.get("cardno")[0])) {
				where+=" and admission.cardno =?";
				list.add(map.get("cardno")[0].replace(" ", ""));
			}
			if(StrKit.notBlank(map.get("inno")) && StrKit.notBlank(map.get("inno")[0])) {
				where+=" and admission.inno =?";
				list.add(map.get("inno")[0].replace(" ", ""));
			}
			if(StrKit.notBlank(map.get("outno")) && StrKit.notBlank(map.get("outno")[0])) {
				where+=" and admission.outno =?";
				list.add(map.get("outno")[0].replace(" ", ""));
			}
			
			// 快速检索
			String quicksearchTable="";
	        if (StrKit.notBlank(map.get("quicksearchcontent")) && StrKit.notBlank(map.get("quicksearchcontent")[0]) 
	        		&& StrKit.notBlank(map.get("quicksearchname"))) {
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
	        
	        String select = "select "+Patient.dao.toSelectStr("id,createtime,creator,modifytime")+","+Admission.dao.toSelectStr("id,createtime,creator,modifytime")
	        		+ ","+Studyorder.dao.toSelectStr("id,patientidfk,patientid,createtime,creator,modifytime,accessionnumber")
	        		+ ",(select name_zh from syscode where syscode.code=studyorder.status and syscode.type='0005') as orderstatus,"
	                + "studyorder.id as studyorderpkid,"
	                + "patient.id as patientpkid,"
	                + "admission.id as admissionpkid,"
	                + "studyorder.createtime as studyordercreatetime, studyorder.modifytime as studyordermodifytime,"
	                + "(select name_zh from syscode where syscode.code=admission.ageunit and syscode.type='0008') as ageunitdisplay,"
	                + "report.id AS reportid,studyorder.createtime as registertime,(select modality_name from dic_modality where dic_modality.id=studyorder.modalityid) as modalityname,"
	                + "(select name_zh from syscode where syscode.code=patient.sex and syscode.type='0001') as sexdisplay "
	                + ",(CASE WHEN report.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE report.reportstatus END) AS reportstatusdisplaycode"
	                + ",(CASE WHEN report.istakenaway = 1 THEN '已取报告' ELSE '未取报告' END) AS report_takenaway "
	                + ",(SELECT  _codenamedisplay_  FROM syscode  WHERE syscode.code = (CASE WHEN report.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE report.reportstatus END) AND syscode.type = '0007') AS reportstatusdisplay"
					+ ",(CASE WHEN studyorder.filmtakenaway = 1 THEN '已取片' ELSE '未取片' END) AS film_takenaway ";
	        select=select.replaceAll("_codenamedisplay_", syscode_lan);
			String sqlExceptSelect = " from patient,admission,studyorder"
					+ " LEFT JOIN report ON studyorder.id = report.studyorderfk" + quicksearchTable
					+ " where 0=0"
					+ " and patient.id=admission.patientidfk"
					+ " and admission.id=studyorder.admissionidfk "
					+ where + " ORDER BY studyorder.regdatetime desc";
			return Db.paginate(Integer.valueOf(map.get("page")[0]), Integer.valueOf(map.get("rows")[0]), select, sqlExceptSelect, list.toArray());
//			List<Record> res=Db.find(sql1, list.toArray());
//			ret.add(new Record().set("totalsize", res.size()));
//			
//			int page=Integer.valueOf(kit.getPara("page"));
//			int rows=Integer.valueOf(kit.getPara("rows"));
//			int start = (page-1) * rows;
//			
//			int len=start+rows;
//			if(len>res.size()) {
//				len=res.size();
//			}
//			for(int i=start;i<(len);i++) {
//				ret.add(res.get(i));
//			}
////		}
////		catch(Exception ex){
////			ex.printStackTrace();
////		}
//		return ret;
	}
	/**
	 * 取消预约
	 */
	public boolean cancelScheduleOrder(int orderid,User user) {
		boolean succeed=Db.tx(new IAtom() {
			public boolean  run() {
				boolean ret=true;
				Studyorder so=Studyorder.dao.findById(orderid);
				so.setStatus(StudyOrderStatus.cancel_the_appointment);
				String sequencenumber=so.getSequencenumber();
				String appointmenttime=DateKit.toStr(so.getAppointmenttime());
				so.setSequencenumber(null);
				ret=ret&&so.update();
				//发送检查进程-取消预约
		  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
						.set("studyorderfk", so.getId())
						.set("status", StudyprocessStatus.canceltheappointment)
						.set("operator", user.getUsername())
						.set("operatorname", user.getName()).set("description", "oldSequencenumber:"+sequencenumber+";oldAppointmenttime"+appointmenttime)), 
		  				MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
				return ret;	
			}
		});
	
		return succeed;
	}

	/**
	 * 删除预约
	 */
	public boolean deleteScheduleOrder(Integer orderid){
		boolean succeed=Db.tx(new IAtom() {
			public boolean run() throws SQLException{
				boolean ret=false;
				Db.update("delete from studyitem where orderid=?",orderid);
				Db.update("DELETE FROM reportremark WHERE orderid=?",orderid);
				ret=Studyorder.dao.deleteById(orderid);
				return ret;
			}
		});
		return succeed;
	}
	
	/**
	 * 删除病人
	 * @param patientpkid
	 * @param user
	 * @return 1:success  2:failed  3: has can not delete order
	 */
	public String deletePatient(Integer patientpkid, User user) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("ret", "1");
		List<Studyorder> studyorders = 
				Studyorder.dao.find("SELECT * FROM studyorder WHERE patientidfk = ? AND status IN"
						+ " ("+StudyOrderStatus.in_process+","+StudyOrderStatus.completed+","+StudyOrderStatus.re_examine+")", patientpkid);
		if(studyorders != null && studyorders.size() > 0) {
			map.put("ret", "3");
			return map.get("ret");
		}
		boolean succeed=Db.tx(new IAtom() {
			public boolean run() throws SQLException{
				boolean ret = true;
				
				Db.delete("DELETE FROM admission WHERE patientidfk = ?", patientpkid);
				Db.delete("DELETE FROM mwlitem WHERE patientidfk = ?", patientpkid);
				//外键约束，先删除studyitem
				Db.delete("DELETE FROM studyitem WHERE exists ("
						+ "select studyorder.id from studyorder where studyorder.id=studyitem.orderid and studyorder.patientidfk=?)", patientpkid);
				Db.delete("DELETE FROM reportremark WHERE patientidfk = ?", patientpkid);
				Db.delete("DELETE FROM studyorder WHERE patientidfk = ?", patientpkid);
				Patient.dao.deleteById(patientpkid);
				
				if(ret) {
					//发送检查进程-删除病人信息
			  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
							.set("patientfk", patientpkid)
							.set("status", StudyprocessStatus.deletePatientinfo)
							.set("operator", user.getUsername())
							.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
				}
				
				return ret;
			}
		});
		
		if(!succeed) {
			map.put("ret", "2");
		}

		return map.get("ret");
	}
	/**
	 * 根据StudyOrder获取病人的详细信息
	 * @param StudyOrder
	 */
	public Record getPatientByOrder(Studyorder studyOrder) {
		Record record=new Record();
		Patient patient=Patient.dao.findById(studyOrder.getPatientidfk());
		Admission admission=Admission.dao.findById(studyOrder.getAdmissionidfk());
		DicModality dicmodality=DicModality.dao.findById(studyOrder.getModalityid());
//		String doctorname="";
//		if(StudyOrder.getAppdoctor()!=null) {
//			DicEmployee doctor=DicEmployee.dao.findById(StudyOrder.getAppdoctor());
//			doctorname = doctor.getName();
//		}
		
		record.set("patientname", patient.getPatientname());
		record.set("modalityname", dicmodality.getModalityName());
		record.set("location", dicmodality.getLocation());
		record.set("patientsource", admission.getPatientsource());
		record.set("doctor", studyOrder.getAppdoctorname());
		
		return record;
	}
//	/**
//	 * 发送消息到队列中
//	 * @param so
//	 * @param method
//	 */
//	public void sendQueue(Studyorder so,Enum method) {
//		Record record = getPatientByOrder(so);
//  		boolean state=false;
//  		if("E".equals(record.get("patientsource"))) state=true;
//  		new QueueupSender(so,record.get("patientname"),record.get("modalityname")
//  				,record.get("location"),record.get("doctor"),state).send(method);
//	}

	/**
	 * 修改病人信息
	 */
	public boolean modifyPatAndAdm(Patient patient,Admission admission) {
		boolean succeed=Db.tx(new IAtom() {
			public boolean run() throws SQLException{
				boolean ret=true;
				Patient pat=Patient.dao.findFirst("select top 1 * from patient where patientid='"+patient.getPatientid()+"'");
				patient.setId(pat.getId());
				patient.setModifytime(new Date());
				ret=ret&&patient.update();
				
				Admission adm=Admission.dao.findFirst("select top 1 * from admission where accession_no='"+admission.getAdmissionid()+"'");
				admission.setId(adm.getId());
				admission.setModifytime(new Date());
				ret=ret&&admission.update();
				
				return ret;
			}
		});
		return succeed;
	}
	/**
	 * 修改检查信息
	 */
	public boolean modifyStudy(Studyorder studyorder,String itemstr,String studydatetime) {
		boolean succeed=Db.tx(new IAtom() {
			public boolean run() throws SQLException{
				boolean ret=true;
				Studyorder so=Studyorder.dao.findFirst("select top 1 * from studyorder where studyid='"+studyorder.getStudyid()+"'");
				studyorder.setId(so.getId());
				studyorder.setStatus("1");
				studyorder.setModifytime(new Date());
				if(StringUtils.isNotBlank(studydatetime)) {
					System.out.println(studydatetime);
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
					ParsePosition pos = new ParsePosition(0);  
					Date strtodate = formatter.parse(studydatetime, pos);
					studyorder.setStudydatetime(strtodate);
				
				}
				try {
					Db.update("delete from studyitem where studyid='"+studyorder.getStudyid()+"'");
					StringBuilder studyitems=new StringBuilder();
					if(itemstr!=null&&!"".equals(itemstr)){
						JSONArray items=new JSONArray(itemstr);
						
						for(int i=0;i<items.length();i++){
							JSONObject item=items.getJSONObject(i);
							log.info(item.getInt("id"));
							
							Studyitem si=new Studyitem();
							si.setOrderid(studyorder.getId());
							si.setStudyid(studyorder.getStudyid());
							si.setModality(item.getString("type"));
							si.setOrgan(item.getInt("organ"));
			  				si.setSuborgan(item.getInt("suborgan"));
							si.setItemCode(item.getInt("id")+"");
							si.setItemName(item.getString("item_name"));
							
							studyitems.append(item.getString("item_name")+",");
//							si.setPrice(item.getBigDecimal("price"));
//							si.setRealprice(item.getBigDecimal("realprice"));
							si.setChargeStatus(item.getString("status"));
							si.remove("id").save();
						}
					}
					
					String str=studyitems.toString();
					if(str.length()>0){
						studyorder.setStudyitems(str.substring(0,str.length()-1));
					}
				}catch(Exception e) {
					e.printStackTrace();
					return false;
				}

				ret=ret&&studyorder.update();
				return ret;
			}
		});
		return succeed;
	}
	/**
	 * 根据病人编号和姓名查找病人
	 */
//	public List<Record> findPat(String patientid,String patientname){
//		List<Record> ret=null;
//		String where="";
//		List<Object> list=new ArrayList<Object>();
//		if(StringUtils.isNotBlank(patientid)) {
//			if(!"".equals(where)) {
//				where +=" and";
//			}
//			where +=" patientid =?";
//			list.add(patientid);
//		}
//		if(StringUtils.isNotBlank(patientname)) {
//			if(!"".equals(where)) {
//				where +=" and";
//			}
//			where +=" patientname LIKE CONCAT('%',?,'%')";
//			list.add(patientname+"%");
//		}
//		if(!"".equals(where)) {
//			where =" where"+where;
//		}
//		String sql="select *,(select name_zh from syscode where syscode.code=patient.sex and syscode.type='0001') as sexdisplay"
//				+ " from patient"+where;
//		Object[] array=list.toArray();	
//		ret=Db.find(sql, array);
//		return ret;
//	}
	
	//同住院号或门诊号
//	public List<Record> checkSameNO(String inno, String outno){
//		List<Record> ret=null;
//		String where="";
//		Admission ad=null;
//		if(StrKit.notBlank(inno)&&StrKit.isBlank(outno)) {
//			ad=Admission.dao.findFirst("select top 1 * from admission where inno = ?", inno);
//		}
//		else if(StrKit.notBlank(outno)&&StrKit.isBlank(inno)) {
//			ad=Admission.dao.findFirst("select top 1 * from admission where outno = ?", outno);
//		}
//		if(ad!=null) {
//			String sql="SELECT *,patient.id as patientpkid,admission.id as admissionpkid"
//					+ " FROM patient,admission WHERE patient.id=admission.patientidfk"
//					+ " AND patient.id = ? AND admission.id = ?";
//			ret=Db.find(sql, ad.getPatientidfk(),ad.getId());
//		}
//		return ret;
//	}
	//查询病人的Admission信息
//		public List<Admission> getAdmission(Integer patientkid){
//			List<Admission> ret=null;
//			Patient pa=null;
//			if(patientkid!=null) {
//				ret=Admission.dao.find("select * from admission where patientidfk = ?", patientkid);
//			}
//			return ret;
//		}
	
	/**
	  *  批量预约转登记
	 * @param orderids
	 * @param user
	 * @return
	 */
	public boolean sch_to_regs(String orderids, User user) {
        String[] strings = orderids.split(",");
        Boolean res = false; 
        for (int i = 0; i < strings.length; i++) {
            res = sch_to_reg(Integer.valueOf(strings[i]), user);
        }
        return res;
    }
	
	/**
	  * 预约转登记
	 * @param orderid
	 * @param user
	 * @return
	 */
	public boolean sch_to_reg(int orderid, User user) {
	    log.info("转登记的orderid为：" + orderid);
		boolean succeed = Db.tx(new IAtom() {
			public boolean run() {
				boolean ret = true;
				Date now = new Date();
				Record record=Db.findFirst("select top 1 patient.id as patientkey,patient.patientname as patientname,admission.id as admissionkey,studyorder.modalityid as modalitykey,"
						+ "admission.patientsource as patientsource,studyorder.studyid as studyid,studyorder.sequencenumber as sequencenumber,studyorder.appointmenttime as appointmenttime"
						+ " from patient,admission,studyorder where patient.id=admission.patientidfk and admission.id=studyorder.admissionidfk and studyorder.id=?",orderid);
				
				log.info(record.getInt("modalitykey"));
				
				DicModality dicmodality = DicModality.dao.findById(record.getInt("modalitykey"));
				Studyorder so = Studyorder.dao.findById(orderid);
				so.setId(orderid);
				so.set("status", StudyOrderStatus.registered);
				so.setRegdatetime(now);
				so.setModifytime(now);
				if(StringUtils.isBlank(so.getSequencenumber())) {
					so.setSequencenumber(SequenceNo_Generator.getRegNumber_new(so));//生成序号
				}

		  		Mwlitem mwl = Mwlitem.dao.findFirst("select top 1 * from mwlitem where studyorderidfk=?", orderid);
		  		if(mwl != null) {
		  			mwl.setSpsStatus(SPSStatus.SCHEDULED);
			  		mwl.setStartDatetime(now);
			  		mwl.setUpdatedTime(now);
			  		ret=ret&&mwl.update();
		  		}

		  		ret=ret&&so.update();
		  		
		  		//ret=ret&&SequenceNo_Generator.updateStatus(dicmodality.getId(), record.getDate("appointmenttime"));
		  		if(ret){
			  		//发送叫号消息
			  		Node node=new Node(StrKit.equals(record.getStr("patientsource"), PatientSource.emergency));
			  		node.setStudyid(record.getStr("studyid"));
			  		node.setPatientname(record.getStr("patientname"));
			  		node.setSn(record.getStr("sequencenumber"));
			  		node.setModality(dicmodality.getType());
			  		node.setModalityid(dicmodality.getId());
			  		node.setModalityname(dicmodality.getModalityName());
			  		node.setLocation(dicmodality.getLocation());
	
			  		ActiveMQ.sendObjectMessage(new QueueupSendOrder(node, QueueMethod.Offer), MQSubject.QUEUEUP.getSendName(), StrKit.getRandomUUID(), 4);
			  		
			  		//发送检查进程-预约转登记
			  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
							.set("studyorderfk", orderid)
							.set("status", StudyprocessStatus.scheduleturntoregister)
							.set("operator", user.getUsername())
							.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
		  		}
				return ret;	
			}
		});
		return succeed;
	}
	
	/**
	 *  病人到检，提供给C#到检所用
	 * @param studyid 检查号
	 * @return
	 */
	public Record patientCheckIn(String studyid) {
	    log.info("病人到检的studyid为：" + studyid);
	    Record record = Db.findFirst("select top 1 patient.id as patientkey,patient.patientname as patientname,admission.id as admissionkey,studyorder.modalityid as modalitykey,"
                + "admission.patientsource as patientsource,studyorder.studyid as studyid,studyorder.sequencenumber as sequencenumber,studyorder.appointmenttime as appointmenttime"
                + " from patient,admission,studyorder where patient.id=admission.patientidfk and admission.id=studyorder.admissionidfk and studyorder.studyid=?",studyid);

	    boolean succeed = false;
	    boolean flagschtoreg = true;
	    Record res = new Record();
	    Studyorder studyorder = Studyorder.dao.getStudyorderByStudyid(studyid);
	    if(studyorder != null) {
	    	res.set("status", studyorder.getStatus());
	    } else {
	    	res.set("status", "nostatus");
	    }
	    if (record != null && res.get("status").equals("1")) {
	        res.set("patientname", record.get("patientname"));
	        DicModality dicmodality = DicModality.dao.findById(record.getInt("modalitykey"));
	        if(dicmodality.getAdvanceHour() != null || dicmodality.getAdvanceMinute() != null) {
		        //获取预约时间
		        Date datescheduletime = record.getDate("appointmenttime");
		        Instant instant = datescheduletime.toInstant();
		        ZoneId zoneId = ZoneId.systemDefault();
		        LocalDateTime scheduletime = instant.atZone(zoneId).toLocalDateTime();
		        //获取当前时间
		        LocalDateTime nowtime = LocalDateTime.now();
		        
		        if(scheduletime.getYear() == nowtime.getYear() && scheduletime.getMonth() == nowtime.getMonth() && scheduletime.getDayOfMonth() == nowtime.getDayOfMonth()) {
		        	int minute = (scheduletime.getHour()-nowtime.getHour())*60 + scheduletime.getMinute()-nowtime.getMinute();
		        	int checkpreminute = 0;
		        	if(dicmodality.getAdvanceHour()!=null)
		        		checkpreminute += dicmodality.getAdvanceHour()*60;
		        	if(dicmodality.getAdvanceMinute()!=null)
		        		checkpreminute += dicmodality.getAdvanceMinute();
		        	if(minute < 0) {
		        		//过了预约时间，无法自助到检
		        		res.set("time", "已过到检时间，请到护士台咨询");
		        		log.info("已过到检时间");
		        		flagschtoreg = false;
		        	} else {
		        		if(minute > checkpreminute) {
		        			//到检太早
		        			res.set("time", "未到到检时间，请稍后");
		        			log.info("未到到检时间");
		        			flagschtoreg = false;
		        		} else {
		        			//可以到检
		        			flagschtoreg = true;
		        		}
		        	}
		        }
	        }
	        if(flagschtoreg) {
	            succeed = Db.tx(new IAtom() {
	                public boolean run() {
	                    boolean ret = true;
	                    Date now = new Date();

	                    DicModality dicmodality = DicModality.dao.findById(record.getInt("modalitykey"));
	                    log.info("预约时间："+record.get("appointmenttime"));
	                    log.info("小时："+dicmodality.getAdvanceHour());
	                    
	                    Studyorder so = (Studyorder) Studyorder.dao.findFirst("select * from studyorder where studyid = ?", studyid);
	                    so.set("status", StudyOrderStatus.registered);
	                    so.setRegdatetime(now);
	                    so.setModifytime(now);
	                    //若没有序号生成序号
	                    if(StringUtils.isBlank(so.getSequencenumber())){
	                    	so.setSequencenumber(SequenceNo_Generator.getRegNumber_new(so));
	                    }
	                    
	                    Mwlitem mwl = Mwlitem.dao.findFirst("select top 1 * from mwlitem where studyorderidfk=?", studyorder.getId());
	                    if(mwl != null) {
	                    	mwl.setSpsStatus(SPSStatus.SCHEDULED);
		                    mwl.setStartDatetime(now);
		                    mwl.setUpdatedTime(now);
		                    ret=ret&&mwl.update();  
	                    }
	                    ret=ret&&so.update();
	                    
	                    //ret=ret&&SequenceNo_Generator.updateStatus(dicmodality.getId(), record.getDate("appointmenttime"));
	                    if(ret){
	                        //发送叫号消息
	                        Node node=new Node(StrKit.equals(record.getStr("patientsource"), PatientSource.emergency));
	                        node.setStudyid(record.getStr("studyid"));
	                        node.setPatientname(record.getStr("patientname"));
	                        node.setSn(record.getStr("sequencenumber"));
	                        node.setModality(dicmodality.getType());
	                        node.setModalityid(dicmodality.getId());
	                        node.setModalityname(dicmodality.getModalityName());
	                        node.setLocation(dicmodality.getLocation());
	        
	                        ActiveMQ.sendObjectMessage(new QueueupSendOrder(node, QueueMethod.Offer), MQSubject.QUEUEUP.getSendName(), StrKit.getRandomUUID(), 4);
	                        
	                        //发送检查进程-预约转登记
	                        ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
	                                .set("studyorderfk", so.getId())
	                                .set("status", StudyprocessStatus.scheduleturntoregister)
	                                .set("operator", "病人到检")
	                                .set("operatorname", "病人到检")), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
	                    }
	                    return ret;
	                }
	            });
		        
	        }
        }
	    res.set("result", succeed);
        return res;
    }
	
	/**
	 * 获取查询条件
	 */
	public List<Record> findFilters(Integer id,Integer creator,String filterType,String locale) {
		String where = "";
		if (id != null) {
			where = "and id= "+id;
		}
		if (creator!= null) {
			where += " and creator= "+creator;
		}
		if (StringUtils.isNotBlank(filterType)) {
			where += " and filter_type= '"+filterType+"'";
		}
		String sql = "SELECT *,case when modality is null then '' else modality end as modality FROM  filter WHERE 0 = 0 " + where +" ORDER BY createtime DESC ";
		List<Record> ret= Db.find(sql);
		ret.forEach(record->{
			record.set("orderstatus_display", SyscodeKit.INSTANCE.getCodeDisplay("0005",record.getStr("orderstatus"), locale));
			record.set("reportstatus_display", SyscodeKit.INSTANCE.getCodeDisplay("0007",record.getStr("reportstatus"), locale));
			record.set("appdate_display", SyscodeKit.INSTANCE.getCodeDisplay("0022",record.get("appdate"), locale));
			record.set("source", SyscodeKit.INSTANCE.getCodeDisplay("0002",record.get("patientsource"), locale));
			record.set("datetype_display", SyscodeKit.INSTANCE.getCodeDisplay("0021",record.get("datetype"), locale));
		});	
		return ret;
	}
	
	/**
	 * 
	 * @param orderid
	 * @param user
	 * @return 1:success  2:failed  3:not correct time
	 */
	public String signin(Integer orderid, User user) {
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("ret", "1");
		LocalDate localDate = LocalDate.now();
		LocalDateTime localDateTime = LocalDateTime.now();
        Studyorder studyorder = Studyorder.dao.findById(orderid);
        LocalDate appointmentDate = studyorder.getAppointmenttime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if(localDate.isEqual(appointmentDate)) {
        	 studyorder.setStatus(StudyOrderStatus.ARRIVED);
        	 studyorder.setArrivedtime(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        	 studyorder.setModifytime(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        	 studyorder.update();
        }else {
        	map.put("ret", "3");
        }
        
        
//        if(StringUtils.equals("1", map.get("ret"))) {
//        	//签到，叫号等待队列更新
//        	Record record = Db.findFirst(Db.getSql("exam.queueup_node"),orderid);
//	  		ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.Offer), MQSubject.QUEUEUPSIGN.getSendName(), StrKit.getRandomUUID(), 4);
//        }
        
      //发送检查进程-签到
  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
  				.set("patientfk", null)
  				.set("admissionfk", null)
				.set("studyorderfk", orderid)
				.set("status", StudyprocessStatus.signin)
				.set("operator", user.getUsername())
				.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
		
        return map.get("ret");
    }
	
	/**
	 * 保存备注
	 * @param patientremark
	 * @param admissionremark
	 * @param studyorderremark
	 * @param patient
	 * @param so
	 * @param admission
	 * @param now
	 * @param user
	 */
	public void saveRemark(String patientremark, String admissionremark, String studyorderremark, 
			Patient patient,Studyorder so,Admission admission, Date now, User user) {
		if(StringUtils.isNotBlank(patientremark)) {
			Reportremark remark=Reportremark.dao.findFirst("SELECT TOP 1 * FROM reportremark WHERE patientidfk=? AND type=?",patient.getId(),"patient");
			if(remark!=null) {
				remark.set("remarkcontent", patientremark).set("modifytime", now).update();
			}else {
				new Reportremark().set("patientidfk", patient.getId())
	  			.set("remarkcontent", patientremark).set("type", "patient")
	  			.set("creator", user.getUsername()).set("modifytime", now).save();
			}
			
		} else {
			Db.delete("DELETE FROM reportremark WHERE patientidfk=? AND type=?",patient.getId(),"patient");
		}	
		
		if(StringUtils.isNotBlank(admissionremark)) {
			Reportremark remark=Reportremark.dao.findFirst("SELECT TOP 1 * FROM reportremark WHERE admissionidfk=? AND type=?",admission.getId(),"admission");		
			if(remark!=null) {
				remark.set("remarkcontent", admissionremark).set("modifytime", now).update();		
			}else {
				new Reportremark().set("patientidfk", patient.getId()).set("admissionidfk", admission.getId())
	  			.set("remarkcontent", admissionremark).set("type", "admission")
	  			.set("creator", user.getUsername()).set("modifytime", now).save();
			}
  			
		} else {
			Db.delete("DELETE FROM reportremark WHERE admissionidfk=? AND type=?",admission.getId(),"admission");
		}	
		
		if(StringUtils.isNotBlank(studyorderremark)) {
			Reportremark remark=Reportremark.dao.findFirst("SELECT TOP 1 * FROM reportremark WHERE studyid=? AND type=?",so.getStudyid(),"studyorder");
			if (remark!=null) {
				remark.set("remarkcontent", studyorderremark).set("modifytime", now).update();
			} else {
				new Reportremark().set("patientidfk", patient.getId()).set("admissionidfk", admission.getId())
				.set("orderid", so.getId()).set("studyid", so.getStudyid())
	  			.set("remarkcontent", studyorderremark).set("type", "studyorder")
	  			.set("creator", user.getUsername()).set("modifytime", now).save();
			}	
  		} else {
			Db.delete("DELETE FROM reportremark WHERE studyid=? AND type=?",so.getStudyid(),"studyorder");
		}
	}
	
	/**
	 * 更新检查项目表
	 * @param itemstr
	 * @param studyorder
	 * @param studyitems
	 * @param now
	 */
	public void updateStudyitem(String itemstr, Studyorder studyorder, StringBuilder studyitems, Date now) {
		com.alibaba.fastjson.JSONArray items = JSON.parseArray(itemstr);
		for(int i=0;i<items.size();i++){
			com.alibaba.fastjson.JSONObject item=items.getJSONObject(i);
			Studyitem si=new Studyitem();
			si.setOrderid(studyorder.getId());
			si.setStudyid(studyorder.getStudyid());
			si.setModality(item.getString("modality"));
			si.setItemCode(item.getInteger("id")+"");
			si.setItemName(item.getString("item_name"));
			si.setItemId(item.getInteger("item_id"));
			if(StrKit.notBlank(item.getString("organ"))) {
				si.setOrgan(item.getInteger("organ"));
			}
			if(StrKit.notBlank(item.getString("suborgan"))) {
				si.setSuborgan(item.getInteger("suborgan"));
			}
			if(StrKit.notBlank(item.getString("price"))) {
				si.setPrice(item.getBigDecimal("price"));
			}
			if(StrKit.notBlank(item.getString("realprice"))) {
				si.setRealprice(item.getBigDecimal("realprice"));
			}
			si.setChargeStatus(item.getString("charge_status"));
			si.setCreatetime(now);
			si.remove("id").save();
			studyitems.append(item.getString("item_name")+",");
		}
	}
	
	/**
	 * 更新Mwlitem数据
	 * @param patient
	 * @param admission
	 * @param studyorder
	 * @param now
	 */
	public void updateMwlitem(Patient patient, Admission admission, Studyorder studyorder, Date now) {
		DicModality dicmodality = DicModality.dao.findById(studyorder.getModalityid());
  		Mwlitem mwl = Mwlitem.dao.findFirst("select top 1 * from mwlitem where studyorderidfk=?", studyorder.getId());
  		if(mwl == null) {
  			mwl = new Mwlitem();
	  		mwl.setPatientidfk(patient.getId());
	  		mwl.setAdmissionidfk(admission.getId());
	  		mwl.setStudyorderidfk(studyorder.getId());
	  		mwl.setSpsStatus(-1);
	  		
//	  		mwl.setIssuerPatientid("");
//	  		mwl.setReqPhysician("");
//	  		mwl.setRefePhysician("");
//	  		mwl.setPerfPhysician("");
//	  		mwl.setReqProcId("");
//	  		mwl.setReqProcDesc("");

	  		mwl.setAccessionNo(studyorder.getStudyid());
	  		mwl.setSpsId("1");
	  		mwl.setCreatedTime(now);
  		}
  		mwl.setStationAet(dicmodality.getWorklistscu());
  		mwl.setStationName(dicmodality.getModalityName());
  		mwl.setCharacter(dicmodality.getCharacter());
  		mwl.setModality(studyorder.getModalityType());
  		mwl.setStudyIuid(IDUtil.getStudyInsUid(dicmodality.getType()));
  		mwl.setSpsDesc(studyorder.getStudyitems());
  		mwl.setStartDatetime(now);
  		mwl.setUpdatedTime(now);
  		
  		
  		if(mwl.getId() == null) {
  			mwl.remove("id").save();
  		}else {
  			mwl.update();
  		}
	}

		/**
	 * 
	 * @param orderid
	 * @param user
	 * @return 1:success  2:failed  3:not correct time
	 */
	public String advanceSignin(Integer orderid, User user) {
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("ret", "1");
		LocalDateTime localDateTime = LocalDateTime.now();
        Studyorder studyorder = Studyorder.dao.findById(orderid);
		studyorder.setStatus(StudyOrderStatus.ARRIVED);
		studyorder.setArrivedtime(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
		studyorder.setModifytime(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
		studyorder.update();
        
//        if(StringUtils.equals("1", map.get("ret"))) {
//        	//签到，叫号等待队列更新
//        	Record record = Db.findFirst(Db.getSql("exam.queueup_node"),orderid);
//	  		ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.Offer), MQSubject.QUEUEUPSIGN.getSendName(), StrKit.getRandomUUID(), 4);
//        }
		//发送检查进程-签到
  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
				.set("studyorderfk", orderid)
				.set("status", StudyprocessStatus.signin)
				.set("operator", user.getUsername())
				.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
		
        return map.get("ret");
    }
		
}


