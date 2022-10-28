package com.healta.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.druid.util.StringUtils;
import com.healta.constant.ReportStatus;
import com.healta.constant.StudyOrderStatus;
import com.healta.constant.StudyprocessStatus;
import com.healta.model.Admission;
import com.healta.model.Injection;
import com.healta.model.Inquiry;
import com.healta.model.Patient;
import com.healta.model.Reportremark;
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
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

import ca.uhn.hl7v2.util.StringUtil;

public class InjectionService {
	private final static Logger log = Logger.getLogger(InjectionService.class);
	
	private final static String _SQL="SELECT top 2000 " + Patient.dao.toSelectStr("id,createtime,creator,modifytime")
			+ " , " + Admission.dao.toSelectStr("id,createtime,creator,modifytime")
			+ " , " + Studyorder.dao.toSelectStr("id,patientidfk,patientid,createtime,creator,modifytime,accessionnumber")
			+ " , " + Inquiry.dao.toSelectStr("id,studyorderfk,studyid,createtime,modifytime")
			+ " , " + Injection.dao.toSelectStr("id,studyorderfk,studyid,administration_method,createtime,modifytime")
			+ ", patient.id as patientpkid, studyorder.id as studyorderpkid, admission.id as admissionpkid"
			+ ", injection.id as injectionid"
			+ ", (select _codenamedisplay_ from syscode where syscode.code=studyorder.status and syscode.type='0005') as orderstatus"
			+ ", (select _codenamedisplay_ from syscode where syscode.code=admission.patientsource and syscode.type='0002') as patientsourcedisplay"
			+ ", (select _codenamedisplay_ from syscode where syscode.code=patient.sex and syscode.type='0001') as sexdisplay"
			+ ", (select _codenamedisplay_ from syscode where syscode.code=admission.ageunit and syscode.type='0008') as ageunitdisplay"
			+ ", (select modality_name from dic_modality where dic_modality.id=studyorder.modalityid) as modalityname"
			+ ", (select name from dic_common where dic_common.id=studyorder.nuclide) as nuclide_name"
			+ ", (SELECT name FROM dic_common WHERE dic_common.id = studyorder.examination_method) AS examination_method_name"
			+ ", (SELECT name FROM users WHERE users.id = inquiry.interrogation_doctor_id) AS interrogation_doctor_name"  //苗超风说从USERS表中查询姓名
			+ ", (SELECT name FROM dic_common WHERE dic_common.id = inquiry.administration_method) AS administration_method_name"
			+ ", (SELECT goods_name FROM com_goods WHERE com_goods.id = inquiry.medicine) AS medicine_name"
			+ ", (CASE WHEN report.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE report.reportstatus END) AS reportstatusdisplaycode"
			+ ", (SELECT  _codenamedisplay_  FROM syscode  WHERE syscode.code = (CASE WHEN report.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE report.reportstatus END) AND syscode.type = '0007') AS reportstatusdisplay,dic_organ.treename_zh as examination_position_name";
	
	private final static String _SQLExceptSelect =  " FROM patient,admission,studyorder"
			+ " LEFT JOIN inquiry ON studyorder.id = inquiry.studyorderfk"
			+ " LEFT JOIN injection ON studyorder.id = injection.studyorderfk"
			+ " LEFT JOIN report ON studyorder.id = report.studyorderfk"
			+ " LEFT JOIN dic_organ  ON dic_organ.id = studyorder.organ"
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
		} else if (StrKit.equals("arrivedtime", paraMap.get("datetype")[0])) { //到检时间
			timecon = "studyorder.arrivedtime";
		}
		if (StrKit.notBlank(from)) {
			where.append( " and " + timecon + " >='" + from + "'" );
		}
		if (StrKit.notBlank(to)) {
			where.append( " and " + timecon + " <='" + to + "'" );
		}
		
		//签到状态
		if(StrKit.notBlank(paraMap.get("isArrived")) && StrKit.notBlank(paraMap.get("isArrived")[0])){
			if(StringUtils.equals(StudyOrderStatus.ARRIVED, paraMap.get("isArrived")[0])) {
				where.append( " and CONVERT(int,studyorder.status) >= ?" );
				list.add(Integer.parseInt(StudyOrderStatus.ARRIVED));
			}else {
				where.append( " and CONVERT(int,studyorder.status) < ?" );
				list.add(Integer.parseInt(StudyOrderStatus.ARRIVED));
			}
		}
		
		//检查状态
//		if(StrKit.notBlank(paraMap.get("orderstatus"))){
//			String[] orderstatus = paraMap.get("orderstatus");
//			if(orderstatus.length == 1 && StrKit.notBlank(orderstatus[0])) {
//				where.append(" and studyorder.status = ?");
//				list.add(paraMap.get("orderstatus")[0]);
//			}else if(orderstatus.length > 1) {
//				String in = "(";
//				for(String str : orderstatus) {
//					in += "?,";
//					list.add(str);
//				}
//				where.append(" and studyorder.status in " + in.substring(0, in.length()-1) + ")");
//			}
//		}
		
//		//问诊
//		if(StrKit.notBlank(paraMap.get("isConsulted")) && StrKit.notBlank(paraMap.get("isConsulted")[0])){
//			if(StringUtils.equals(StudyOrderStatus.CONSULTED, paraMap.get("isConsulted")[0])) {
//				where.append( " and CONVERT(int,studyorder.status) >= ?" );
//				list.add(Integer.parseInt(StudyOrderStatus.CONSULTED));
//			}else {
//				where.append( " and CONVERT(int,studyorder.status) < ?" );
//				list.add(Integer.parseInt(StudyOrderStatus.CONSULTED));
//			}
//		}
//		
//		//注射
		if(StrKit.notBlank(paraMap.get("isInjected")) && StrKit.notBlank(paraMap.get("isInjected")[0])){
			if(StringUtils.equals(StudyOrderStatus.injected, paraMap.get("isInjected")[0])) {
				where.append( " and CONVERT(int,studyorder.status) >= ?" );
				list.add(Integer.parseInt(StudyOrderStatus.injected));
			}else {
				where.append( " and CONVERT(int,studyorder.status) < ?" );
				list.add(Integer.parseInt(StudyOrderStatus.injected));
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
		
		where.append( " order by studyorder.arrivedtime ");
		String sqlPara = _SQL.replaceAll("_codenamedisplay_", syscode_lan) + _SQLExceptSelect + where;
		return Db.find(sqlPara, list.toArray());
	}

	public boolean saveInjection(ParaKit paraKit, Studyorder studyorder, Injection injection, User user) {
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,() -> {
				boolean flag = false;
				Date now=new Date();
				Integer orderid = StringUtil.isBlank(paraKit.getPara("orderid")) ? null : Integer.valueOf(paraKit.getPara("orderid"));
				if(orderid == null) {
					return false;
				}
				studyorder.setId(orderid);
				//检查状态  < 问诊StudyOrderStatus.CONSULTED
				if(StringUtil.isNotBlank(paraKit.getPara("orderstatus")) && Integer.parseInt(paraKit.getPara("orderstatus")) < Integer.parseInt(StudyOrderStatus.injected)) {
					studyorder.setStatus(StudyOrderStatus.injected);
					studyorder.update();
				}
				log.info("studyorder model:" + studyorder);

//				Integer injection_id = StringUtil.isBlank(paraKit.getPara("injectionid")) ? null : Integer.valueOf(paraKit.getPara("injectionid"));
				Integer injecter_id = StringUtil.isBlank(paraKit.getPara("injecter_id")) ? null : Integer.valueOf(paraKit.getPara("injecter_id"));
				Integer reception_nurse = StringUtil.isBlank(paraKit.getPara("reception_nurse")) ? null : Integer.valueOf(paraKit.getPara("reception_nurse"));
				String injecter_name = Db.queryStr("SELECT TOP 1 name FROM dic_employee WHERE id=?",injecter_id);
				String reception_nurse_name = Db.queryStr("SELECT TOP 1 name FROM dic_employee WHERE id=?",reception_nurse);
				
				injection.setStudyorderfk(studyorder.getId());
				injection.setStudyid(studyorder.getStudyid());
				injection.setInjecterName(injecter_name);
				injection.setReceptionNurseName(reception_nurse_name);
				
				//如果用户为手动选择注射时间，默认为保存时的系统时间
				if(injection.getInjectionDatetime()==null){
					injection.setInjectionDatetime(new Date());
				}
				if(injection.getId()!=null) {
					injection.update();
				}else {
					flag = true;
					injection.remove("id").save();
				}
				
				
				//保存注射备注
				if(org.apache.commons.lang.StringUtils.isNotBlank(injection.getInjectionRemark())) {
					Reportremark remark=Reportremark.dao.findFirst("SELECT TOP 1 * FROM reportremark WHERE orderid=? AND type=?",orderid,"injection");
					if (remark!=null) {
						remark.set("remarkcontent", injection.getInjectionRemark()).set("modifytime", now).update();
					} else {
						new Reportremark().set("orderid", orderid).set("remarkcontent", injection.getInjectionRemark()).set("type", "injection")
			  			.set("creator", user.getUsername()).set("modifytime", now).save();
					}	
		  		} else {
					Db.delete("DELETE FROM reportremark WHERE orderid=? AND type=?",orderid,"injection");
				}
				
//				//注射第一次保存，发送消息到检查队列
//				if(flag) {
//					//发送消息到队列,待检查列表增加等待病人
//			  		Record record = Db.findFirst(Db.getSql("exam.queueup_node"), studyorder.getId());
//			  		ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.Offer), MQSubject.QUEUEUP.getSendName(), StrKit.getRandomUUID(), 4);			  		
//				}
				
				//发送检查进程-保存问诊
		  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
						.set("studyorderfk", orderid)
						.set("status", StudyprocessStatus.injection)
						.set("operator", user.getUsername())
						.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
				
				
				return true;
		});
	}
	
	//注射叫号
	public boolean injectioncalling(int orderid,User user) {
		boolean ret=true;
		Record record=Db.findFirst(Db.getSql("exam.queueup_node"),orderid);
		if(record!=null){
		//发送叫号消息
			ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.Remove), MQSubject.QUEUEUPINJE.getSendName(), StrKit.getRandomUUID(), 4);
		}
		return ret;
	}
}
