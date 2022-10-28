package com.healta.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.constant.PatientSource;
import com.healta.constant.StudyOrderStatus;
import com.healta.constant.StudyprocessStatus;
import com.healta.model.DicModality;
import com.healta.model.Mwlitem;
import com.healta.model.Studyorder;
import com.healta.model.Studyprocess;
import com.healta.plugin.activemq.ActiveMQ;
import com.healta.plugin.activemq.MQSubject;
import com.healta.plugin.activemq.StudyprocessOrder;
import com.healta.plugin.dcm.SPSStatus;
import com.healta.plugin.queueup.Node;
import com.healta.plugin.queueup.QueueMethod;
import com.healta.plugin.queueup.QueueupSendOrder;
import com.healta.util.SequenceNo_Generator;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class OpenActionService {
	public static final Logger log = Logger.getLogger(OpenActionService.class);
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
	                    
	                    Mwlitem mwl=new Mwlitem();
	                    mwl.setPatientidfk(record.getInt("patientkey"));
	                    mwl.setAdmissionidfk(record.getInt("admissionkey"));
	                    mwl.setStudyorderidfk(so.getId());
	                    mwl.setSpsStatus(SPSStatus.SCHEDULED);
	                    mwl.setStartDatetime(now);
	                    mwl.setStationAet(dicmodality.getWorklistscu());
	                    mwl.setStationName(dicmodality.getModalityName());
	                    mwl.setModality(dicmodality.getType());
	                    mwl.setCharacter(dicmodality.getCharacter());
	                    mwl.setAccessionNo(record.getStr("studyid"));
	                    mwl.setUpdatedTime(now);
	                    mwl.setCreatedTime(now);
	                    ret=ret&&mwl.remove("id").save();  
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
	 * 获取orderid
	 * @param para
	 * @return
	 */
	public int getorderid(String para) {
		return Db.queryInt("select id from studyorder where studyid = ?",para);
	}
}
