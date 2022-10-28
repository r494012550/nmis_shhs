package com.healta.service;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jfree.util.Log;

import com.alibaba.druid.util.StringUtils;
import com.healta.constant.ReportStatus;
import com.healta.constant.StudyOrderStatus;
import com.healta.constant.StudyprocessStatus;
import com.healta.model.Admission;
import com.healta.model.Inquiry;
import com.healta.model.Patient;
import com.healta.model.PreviousHistory;
import com.healta.model.Reportremark;
import com.healta.model.StudyImage;
import com.healta.model.Studyorder;
import com.healta.model.Studyprocess;
import com.healta.model.User;
import com.healta.plugin.activemq.ActiveMQ;
import com.healta.plugin.activemq.MQSubject;
import com.healta.plugin.activemq.StudyprocessOrder;
import com.healta.plugin.queueup.Node;
import com.healta.plugin.queueup.QueueMethod;
import com.healta.plugin.queueup.QueueupSendOrder;
import com.healta.util.ParaKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

import ca.uhn.hl7v2.util.StringUtil;

public class InquiringService {
	private final static Logger log = Logger.getLogger(InquiringService.class);
	
	private final static String _SQL="SELECT " + Patient.dao.toSelectStr("id,createtime,creator,modifytime")
			+ " , " + Admission.dao.toSelectStr("id,createtime,creator,modifytime")
			+ " , " + Studyorder.dao.toSelectStr("id,patientidfk,patientid,createtime,creator,modifytime,accessionnumber")
			+ " , " + Inquiry.dao.toSelectStr("id,studyorderfk,studyid")
			+ " , " + PreviousHistory.dao.toSelectStr("id,studyorderfk,studyid,interrogation_time")
			+ ", patient.id as patientpkid, studyorder.id as studyorderpkid, admission.id as admissionpkid"
			+ ", inquiry.id AS interrogation_id, previous_history.id AS previous_history_id"
			+ ", (select _codenamedisplay_ from syscode where syscode.code=studyorder.status and syscode.type='0005') as orderstatus"
			+ ", (select _codenamedisplay_ from syscode where syscode.code=admission.patientsource and syscode.type='0002') as patientsourcedisplay"
			+ ", (select _codenamedisplay_ from syscode where syscode.code=patient.sex and syscode.type='0001') as sexdisplay"
			+ ", (select _codenamedisplay_ from syscode where syscode.code=admission.ageunit and syscode.type='0008') as ageunitdisplay"
			+ ", (select modality_name from dic_modality where dic_modality.id=studyorder.modalityid) as modalityname"
			+ ", (CASE WHEN report.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE report.reportstatus END) AS reportstatusdisplaycode"
			+ ", (SELECT  _codenamedisplay_  FROM syscode  WHERE syscode.code = (CASE WHEN report.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE report.reportstatus END) AND syscode.type = '0007') AS reportstatusdisplay"
	        + ", studyorder.nuclide,studyorder.arrivedtime,studyorder.id as orderid";
	
	private final static String _SQLExceptSelect =  " FROM patient,admission,studyorder"
			+ " LEFT JOIN report ON studyorder.id = report.studyorderfk"
			+ " LEFT JOIN inquiry ON studyorder.id = inquiry.studyorderfk"
			+ " LEFT JOIN previous_history ON studyorder.id = previous_history.studyorderfk"
			+ " WHERE patient.id = admission.patientidfk AND studyorder.admissionidfk = admission.id ";

	public List<Record> findStudyorder(Map<String, String[]> paraMap, String syscode_lan) {
		StringBuffer where = new StringBuffer();
		List<Object> list = new ArrayList<Object>();
		//时间范围
		String from = "";
		String to = "";
		if (StrKit.notBlank(paraMap.get("datefrom")) && StrKit.notBlank(paraMap.get("datefrom")[0])) {
			from = paraMap.get("datefrom")[0]+" 00:00:00";
		}
		if (StrKit.notBlank(paraMap.get("dateto")) && StrKit.notBlank(paraMap.get("dateto")[0])) {
			to = paraMap.get("dateto")[0]+" 23:59:59";
		}
		String timecon = "";
		if (StrKit.equals("registertime", paraMap.get("datetype")[0])) {
			timecon = "studyorder.regdatetime";
		} else if (StrKit.equals("studytime", paraMap.get("datetype")[0])) {
			timecon = "studyorder.studydatetime";
		} else if (StrKit.equals("reporttime", paraMap.get("datetype")[0])) {
			timecon = "studyorder.reporttime";
		} else if (StrKit.equals("appointmenttime", paraMap.get("datetype")[0])) {
			timecon = "studyorder.appointmenttime";
		} else if (StrKit.equals("arrivedtime", paraMap.get("datetype")[0])) {
			timecon = "studyorder.arrivedtime";
		} 
		if (StrKit.notBlank(from)) {
			where.append( " and " + timecon + " >='" + from + "'" );
		}
		if (StrKit.notBlank(to)) {
			where.append( " and " + timecon + " <='" + to + "'" );
		}
		
		boolean exist_sch = PropKit.use("system.properties").getBoolean("exist_appointment_process",true);
//		if(exist_sch) {//存在预约流程，检查状态从签到开始
//			if(StrKit.notBlank(paraMap.get("isArrived")) && StrKit.notBlank(paraMap.get("isArrived")[0])){
//				if(StringUtils.equals(StudyOrderStatus.ARRIVED, paraMap.get("isArrived")[0])) {
//					where.append( " and CONVERT(int,studyorder.status) >= ?" );
//					list.add(Integer.parseInt(StudyOrderStatus.ARRIVED));
//				}else {
//					where.append( " and CONVERT(int,studyorder.status) < ?" );
//					list.add(Integer.parseInt(StudyOrderStatus.ARRIVED));
//				}
//			}
//		} else {//不存在预约流程，检查状态从登记开始
//			where.append( " and CONVERT(int,studyorder.status) = ?" );
//			list.add(Integer.parseInt(StudyOrderStatus.registered));
//		}
		//问诊
		if(StrKit.notBlank(paraMap.get("isConsulted")) && StrKit.notBlank(paraMap.get("isConsulted")[0])){
			if(StringUtils.equals(StudyOrderStatus.CONSULTED, paraMap.get("isConsulted")[0])) {
				where.append( " and CONVERT(int,studyorder.status) >= ?" );
				list.add(Integer.parseInt(StudyOrderStatus.CONSULTED));
			}else {
				where.append( " and CONVERT(int,studyorder.status) < ?" );
				list.add(Integer.parseInt(StudyOrderStatus.CONSULTED));
			}
		}
		
		if(StrKit.notBlank(paraMap.get("patientname")) && StrKit.notBlank(paraMap.get("patientname")[0])){
			where.append( " and patient.patientname LIKE CONCAT('%',?,'%')");
			list.add(paraMap.get("patientname")[0]);
		}
		
		//检查设备
//		if(StrKit.notBlank(paraMap.get("modalityid")) && StrKit.notBlank(paraMap.get("modalityid")[0])) {
//			where.append( " and studyorder.modalityid = ?");
//			list.add(paraMap.get("modalityid")[0]);
//		}
		if(StrKit.notBlank(paraMap.get("modalityid"))){
			String[] modalityid = paraMap.get("modalityid");
			if(modalityid.length == 1) {
				where.append(" and studyorder.modalityid = ?");
				list.add(paraMap.get("modalityid")[0]);
			}else if(modalityid.length > 1) {
				String in = "(";
				for(String str : modalityid) {
					in += "?,";
					list.add(str);
				}
				where.append(" and studyorder.modalityid in " + in.substring(0, in.length()-1) + ")");
			}
		}
		
		if(exist_sch) {
			//根据签到时间正序 排序
			where.append(" order by studyorder.arrivedtime asc");
		}else {
			//根据登记时间正序 排序
			where.append(" order by studyorder.regdatetime asc");
		}
		
		String sqlPara = _SQL.replaceAll("_codenamedisplay_", syscode_lan) + _SQLExceptSelect + where;
		return Db.find(sqlPara, list.toArray());
	}

	public boolean saveInterrogation(ParaKit paraKit, Patient patient, Studyorder studyorder, Inquiry interrogation, PreviousHistory previousHistory, User user, String[] scanimgs) {
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,() -> {
				boolean flag = false;
				Date now=new Date();
				Integer orderid = StringUtil.isBlank(paraKit.getPara("orderid")) ? null : Integer.valueOf(paraKit.getPara("orderid"));
				if(orderid == null) {
					return false;
				}
				studyorder.setId(orderid);
				//检查状态  < 问诊StudyOrderStatus.CONSULTED
				if(StringUtil.isNotBlank(paraKit.getPara("orderstatus")) && Integer.parseInt(paraKit.getPara("orderstatus")) < Integer.parseInt(StudyOrderStatus.CONSULTED)) {
					studyorder.setStatus(StudyOrderStatus.CONSULTED);
				}
				studyorder.update();
				log.info("studyorder model:" + studyorder);
				
				//=====病人信息=====
				Integer patient_id = StringUtil.isBlank(paraKit.getPara("patient_id")) ? null : Integer.valueOf(paraKit.getPara("patient_id"));
				if(patient_id != null) {
					patient.setId(patient_id);
					patient.update();
				}
				//=====入院信息=====
				Integer admission_id = StringUtil.isBlank(paraKit.getPara("admission_id")) ? null : Integer.valueOf(paraKit.getPara("admission_id"));
				if(admission_id != null) {
					Admission admission = Admission.dao.findById(admission_id);
					admission.setAge(Integer.parseInt(paraKit.getPara("age")));
					admission.setAgeunit(paraKit.getPara("ageunit"));
					admission.update();
				}
					
				//=====问诊=====
				Integer interrogation_id = StringUtil.isBlank(paraKit.getPara("interrogation_id")) ? null : Integer.valueOf(paraKit.getPara("interrogation_id"));
				interrogation.setStudyorderfk(studyorder.getId());
				interrogation.setStudyid(studyorder.getStudyid());
				if(interrogation_id != null) {
					interrogation.setId(interrogation_id);
					interrogation.update();
				}else {
					flag = true;
					interrogation.remove("id").save();
				}
				
				//=====既往病史=====
				Integer previous_history_id = StringUtil.isBlank(paraKit.getPara("previous_history_id")) ? null : Integer.valueOf(paraKit.getPara("previous_history_id"));
				previousHistory.setStudyorderfk(studyorder.getId());
				previousHistory.setStudyid(studyorder.getStudyid());
				if(previous_history_id != null) {
					previousHistory.setId(previous_history_id);
					previousHistory.update();
				}else {
					previousHistory.remove("id").save();
				}
				

				saveScanimgs(scanimgs, studyorder);//保存申请单图片
				
				//保存问诊备注
				if(org.apache.commons.lang.StringUtils.isNotBlank(interrogation.getOtherInformation())) {
					Reportremark remark=Reportremark.dao.findFirst("SELECT TOP 1 * FROM reportremark WHERE orderid=? AND type=?",orderid,"interrogation");
					if (remark!=null) {
						remark.set("remarkcontent", interrogation.getOtherInformation()).set("modifytime", now).update();
					} else {
						new Reportremark().set("orderid", orderid).set("remarkcontent", interrogation.getOtherInformation()).set("type", "interrogation")
			  			.set("creator", user.getUsername()).set("modifytime", now).save();
					}	
		  		} else {
					Db.delete("DELETE FROM reportremark WHERE orderid=? AND type=?",orderid,"interrogation");
				}
				//问诊保存，说明该患者问诊完成
				if(flag) {
			  		Record record = Db.findFirst(Db.getSql("exam.queueup_node"), studyorder.getId());
			  		ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.InterrogationCompleted), MQSubject.QUEUEUPINTERROGATION.getSendName(), StrKit.getRandomUUID(), 4);
				}
				
				//发送检查进程-保存问诊
		  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
		  				.set("patientfk", patient_id)
		  				.set("admissionfk", admission_id)
						.set("studyorderfk", orderid)
						.set("status", StudyprocessStatus.Inquiring)
						.set("operator", user.getUsername())
						.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
				
				return true;
		});
	}
	
	public PreviousHistory getPreviousHistory(Integer orderid) {
		return PreviousHistory.dao.findFirst("select top 1 * from previous_history where studyorderfk=?",orderid);
	}

	//根据patientid获取历史报告
	public List<Record> getReportHistory(String patientid, Integer orderid, String syscode_lan) {
		String sql = "SELECT *,"
				+ " (select _codenamedisplay_ from syscode where syscode.code=patient.sex and syscode.type='0001') as sexdisplay"
				+ " FROM studyorder"
				+ " INNER JOIN patient ON patient.id = studyorder.patientidfk"
				+ " LEFT JOIN report ON report.studyorderfk = studyorder.id"
				//+ " LEFT JOIN interrogation ON interrogation.studyorderfk = studyorder.id"
				+ " WHERE studyorder.patientid = ? AND studyorder.id != ? ";
		List<Object> paras = new ArrayList<Object>();
		paras.add(patientid);
		paras.add(orderid);
		return Db.find(sql.replaceAll("_codenamedisplay_", syscode_lan), paras.toArray());
	}
	
	//问诊叫号
	public boolean interrogationcalling(int orderid,User user) {
		boolean ret=true;
		Record record=Db.findFirst(Db.getSql("exam.queueup_node"),orderid);
		if(record!=null){
		    //发送问诊叫号消息
			ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.InterrogationCalling), MQSubject.QUEUEUPINTERROGATION.getSendName(), StrKit.getRandomUUID(), 4);
		}
		return ret;	
	}
	
	/**
	 * 保存申请单图片
	 * @param scanimgs
	 * @param studyorder
	 */
	public void saveScanimgs(String[] scanimgs, Studyorder studyorder) {
		if(scanimgs != null){
  			StudyImage si=StudyImage.dao.findFirst("select top 1 * from study_image where orderid=?", studyorder.getId());
  			if(si==null){
  				si=new StudyImage();
  				si.setOrderId(studyorder.getId());
  				si.setStudyid(studyorder.getStudyid());
  			}
  			for(int i=0;i<scanimgs.length;i++){
  				try{
  					log.info("scanimgs:"+scanimgs[i]);
  					File file=new File(PropKit.get("base_upload_path")+"\\apply\\tmp\\"+scanimgs[i]);
  					if(file.exists()){
						String filename = file.getName();
						String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
						String dirname = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
						FileUtils.moveFileToDirectory(file, new File(PropKit.get("base_upload_path")+"\\apply\\"+year+"\\"+dirname), true);
						for(int j=1; j<=10; j++) {
							if(si.get("img"+j) == null) {
								si.set("img"+j, "/apply/"+year+"/"+dirname+"/"+filename);
								break;
							}
						}
  					}
	  			}
		  		catch(Exception ex){
		  			ex.printStackTrace();
		  		}
  			}
  			if(si.getId()!=null){
  				si.update();
  			}
  			else{
  				si.remove("id").save();
  			}
  		}
		else{
			Db.update("delete from study_image where orderid=?", studyorder.getId());
		}
	}

}
