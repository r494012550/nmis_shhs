package com.healta.service;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.model.Admission;
import com.healta.model.DicExamitem;
import com.healta.model.DicModality;
import com.healta.model.EmergencyDefaultInfo;
import com.healta.model.EmergencyStudyitem;
import com.healta.model.Mwlitem;
import com.healta.model.Patient;
import com.healta.model.PatientRecycle;
import com.healta.model.Reportremark;
import com.healta.model.StudyImage;
import com.healta.model.Studyitem;
import com.healta.model.Studyorder;
import com.healta.model.Studyprocess;
import com.healta.model.Unmatchstudy;
import com.healta.model.User;
import com.healta.plugin.activemq.ActiveMQ;
import com.healta.plugin.activemq.MQSubject;
import com.healta.plugin.activemq.PacsUpdateOrder;
import com.healta.plugin.activemq.StudyprocessOrder;
import com.healta.plugin.dcm.SPSStatus;
import com.healta.plugin.queueup.Node;
import com.healta.plugin.queueup.QueueMethod;
import com.healta.plugin.queueup.QueueupSendOrder;
import com.healta.plugin.queueup.QueueupSender;
import com.alibaba.fastjson.JSON;
import com.healta.constant.PatientSource;
import com.healta.constant.ReportStatus;
import com.healta.constant.SearchTimeConstant;
import com.healta.constant.StudyImageStatus;
import com.healta.constant.StudyOrderPriority;
import com.healta.constant.StudyOrderStatus;
import com.healta.constant.StudyprocessStatus;
import com.healta.util.IDUtil;
import com.healta.util.ParaKit;
import com.healta.util.PinyinUtils;
import com.healta.util.SequenceNo_Generator;
import com.healta.util.SyscodeKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class RegisterService {
	
	private static final Logger log = Logger.getLogger(RegisterService.class);
	public RegisterService(){
		
	}
	
	public Studyorder getStatus(Integer orderid) throws Exception {
		return Studyorder.dao.findById(orderid);
	}
	
	//@Before(Tx.class)
	public boolean addRegister(Patient patient,Studyorder so,Admission admission,String itemstr,
			String patientremark,String admissionremark,String studyorderremark,User user,Integer unmatchkid,String[] scanimgs) {
		boolean succeed = Db.tx(Connection.TRANSACTION_READ_COMMITTED,() -> {
					boolean ret = true;
					Date now = new Date();
					String patientid = patient.getPatientid();
			  		if (patientid == null) {
			  			patientid = IDUtil.getPatientID();
			  			patient.setPatientid(patientid);
			  		} else if (patient.getId() == null) {
			  			Patient p = Patient.dao.getPatientById(patientid);
			  			if (p != null) {
			  				patient.setId(p.getId());
			  			}
			  		}
			  		patient.setCreator(user.getUsername());
		  			patient.setCreatetime(now);
		  			patient.setModifytime(now);
		  			// 对拼音进行特殊字符过滤
		  			patient.setPy(PinyinUtils.toPinyin(patient.getPy(),true));
		  			if(patient.getId()!=null) {
		  				ret=ret&&patient.update();
		  			}
		  			else {
		  				ret=ret&&patient.remove("id").save();
		  			}

			  		if(admission.getAdmissionid()==null) {
			  			admission.setAdmissionid(IDUtil.getAdmissionID());
			  		}
			  		admission.setPatientidfk(patient.getId());
		  			admission.setCreator(user.getUsername());
		  			admission.setCreatetime(now);
		  			admission.setModifytime(now);
		  			if(admission.getId()!=null) {
		  				ret=ret&&admission.update();
		  			} else {
		  				ret=ret&&admission.remove("id").save();
		  			}

			  		so.setPatientid(patientid);
			  		so.setPatientidfk(patient.getId());
			  		so.setAdmissionidfk(admission.getId());
			  		so.setCreator(user.getUsername());
			  		so.setCreatorname(user.getName());
			  		
			  		if(PatientSource.emergency.equals(admission.getPatientsource())) {
			  			so.setPri(StudyOrderPriority.emergency);
			  		}else {
			  			so.setPri(StudyOrderPriority.normal);
			  		}

			  		if(StringUtils.isBlank(so.getStudyid())){
			  			String tempstudyid = IDUtil.getStudyID(so.getModalityType(), so.getAppdeptcode());
			  			so.setStudyid(tempstudyid);
			  			so.setAccessionnumber(tempstudyid);
			  			so.setStatus(StudyOrderStatus.registered);
			  		}else {
			  			so.setAccessionnumber(so.getStudyid());
			  			so.setStatus(StudyOrderStatus.registered);
			  		}
			  		
			  		Record other = null;//未匹配登记标识
			  		if(unmatchkid !=null){
			  			so.setStatus(StudyOrderStatus.completed);
			  			Unmatchstudy unmatchstudy = Unmatchstudy.dao.findById(unmatchkid);
			  			if(unmatchstudy != null) {
			  				unmatchstudy.setMatchflag(1);
			  				unmatchstudy.update();
			  				other = unmatchstudy.toRecord();
			  			}
			  			
			  		}
			  		if(so.getAppdatetime()==null) {
			  			so.setAppdatetime(now);
			  		}
			  		so.setRegdatetime(now);
			  		so.setImagestatus(StudyImageStatus.NOMATCH);
			  		//每台机器生成序列号
			  		if(StrKit.isBlank(so.getSequencenumber())) {
			  			String sequencenumber=SequenceNo_Generator.getRegNumber_new(so);
				  		if("".equals(sequencenumber)) {
				  			return false;
				  		}
				  		so.setSequencenumber(sequencenumber);
			  		}
			  		ret=ret&&so.remove("id").save();
					
			  	//保存remark
					if(StringUtils.isNotBlank(patientremark)) {
						Reportremark remark=Reportremark.dao.findFirst("SELECT TOP 1 * FROM reportremark WHERE patientidfk=? AND type=?",patient.getId(),"patient");
						if(remark!=null) {
							remark.set("remarkcontent", patientremark).set("modifytime", now).update();
						}else {
							new Reportremark().set("patientidfk", patient.getId())
				  			.set("remarkcontent", patientremark).set("type", "patient")
				  			.set("creator", user.getName()).set("modifytime", now).save();
						}
		  			}else {
		  				Db.delete("DELETE FROM reportremark WHERE patientidfk=? AND type=?",patient.getId(),"patient");
		  			}	
					
					if(StringUtils.isNotBlank(admissionremark)) {
						Reportremark remark=Reportremark.dao.findFirst("SELECT TOP 1 * FROM reportremark WHERE admissionidfk=? AND type=?",admission.getId(),"admission");		
						if(remark!=null) {
							remark.set("remarkcontent", admissionremark).set("modifytime", now).update();		
						}else {
							new Reportremark().set("patientidfk", patient.getId()).set("admissionidfk", admission.getId())
				  			.set("remarkcontent", admissionremark).set("type", "admission")
				  			.set("creator", user.getName()).set("modifytime", now).save();
						}
			  			
		  			}else {
		  				Db.delete("DELETE FROM reportremark WHERE admissionidfk=? AND type=?",admission.getId(),"admission");
		  			}	
					
					if(StringUtils.isNotBlank(studyorderremark)) {
						Reportremark remark=Reportremark.dao.findFirst("SELECT TOP 1 * FROM reportremark WHERE studyid=? AND type=?",so.getStudyid(),"studyorder");
						if(remark!=null) {
							remark.set("remarkcontent", studyorderremark).set("modifytime", now).update();
						}else {
							new Reportremark().set("patientidfk", patient.getId()).set("admissionidfk", admission.getId())
							.set("orderid", so.getId()).set("studyid", so.getStudyid())
				  			.set("remarkcontent", studyorderremark).set("type", "studyorder")
				  			.set("creator", user.getName()).set("modifytime", now).save();
						}	
			  		}else {
		  				Db.delete("DELETE FROM reportremark WHERE studyid=? AND type=?",so.getStudyid(),"studyorder");
		  			}

			  		StringBuilder studyitems=new StringBuilder();
			  		try {
			  			updateStudyitem(itemstr, so, studyitems,now);
			  		//将检查项目保存进studyorder中
				  		String str=studyitems.toString();
				  		if(str.length()>0){
				  			so.setStudyitems(str.substring(0,str.length()-1));
				  			so.update();
				  		}
		  			}catch(Exception e) {
						e.printStackTrace();
						return false;
					}		  		

			  		DicModality dicmodality = DicModality.dao.findById(so.getModalityid());
			  		// worklist信息表
			  		Mwlitem mwl=new Mwlitem();
			  		mwl.setPatientidfk(patient.getId());
			  		mwl.setAdmissionidfk(admission.getId());
			  		mwl.setStudyorderidfk(so.getId());
			  		mwl.setSpsStatus(SPSStatus.SCHEDULED);
			  		
			  		mwl.setStationAet(dicmodality.getWorklistscu());
			  		mwl.setStationName(dicmodality.getModalityName());
			  		mwl.setCharacter(dicmodality.getCharacter());
			  		mwl.setModality(so.getModalityType());
//			  		mwl.setIssuerPatientid("");
//			  		mwl.setReqPhysician("");
//			  		mwl.setRefePhysician("");
//			  		mwl.setPerfPhysician("");
//			  		mwl.setReqProcId("");
//			  		mwl.setReqProcDesc("");
			  		
			  		mwl.setStudyIuid(IDUtil.getStudyInsUid(dicmodality.getType()));
			  		mwl.setAccessionNo(so.getStudyid());
			  		mwl.setSpsId("1");
			  		mwl.setSpsDesc(so.getStudyitems());
			  		
			  		mwl.setStartDatetime(now);
			  		mwl.setUpdatedTime(now);
			  		mwl.setCreatedTime(now);
			  		ret=ret&&mwl.remove("id").save();
			  		
			  		if(scanimgs!=null){
			  			StudyImage si=new StudyImage();
  						si.setOrderId(so.getId());
  						si.setStudyid(so.getStudyid());
			  			for(int i=0;i<scanimgs.length;i++){
			  				try{
			  					File file=new File(PropKit.get("base_upload_path")+"\\apply\\tmp\\"+scanimgs[i]);
			  					if(file.exists()){
		  							String filename=file.getName();
		  							String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
		  							String dirname=LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		  							FileUtils.moveFileToDirectory(file, new File(PropKit.get("base_upload_path")+"\\apply\\"+year+"\\"+dirname), true);
		  							si.set("img"+(i+1), "/apply/"+year+"/"+dirname+"/"+filename);
			  					}
				  			}
					  		catch(Exception ex){
					  			ex.printStackTrace();
					  		}
			  			}
			  			si.remove("id").save();
			  		}

			  		if(ret==true) {
			  		    //发送消息到队列
//				  		Record record=Db.findFirst(Db.getSql("exam.queueup_node"),so.getId());
//				  		ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.Offer), MQSubject.QUEUEUP.getSendName(), StrKit.getRandomUUID(), 4);
				  		
				  		//发送检查进程-登记
				  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
				  				.set("patientfk", patient.getId())
				  				.set("admissionfk", admission.getId())
								.set("studyorderfk", so.getId())
								.set("status", StudyprocessStatus.registered)
								.set("operator", user.getUsername())
								.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
//				  		try {
//				  			String patientsource=admission.getPatientsource();
//				  			
//					  		boolean state=false;
//					  		if("E".equals(patientsource)) state=true;
//					  		new QueueupSender(so,patient.getPatientname(),dicmodality.getModalityName(),dicmodality.getLocation(),user.getName(),state).send(QueueMethod.Offer);
//			  			}catch(Exception e) {
//							e.printStackTrace();
//							return false;
//						}	
				  		
				  		//未匹配转登记，更新PACS中信息
//				  		if(other != null) {
//				  			ActiveMQ.sendObjectMessage(new PacsUpdateOrder(patient, admission, so, other, "patientMerge"), MQSubject.PACSUPDATE.getSendName(), StrKit.getRandomUUID(), 4);
//				  		}
			  		}
			  		
					return ret;     
				});
		return succeed;
	}
	
	//修改
	public boolean modifyRegister(Patient patient,Studyorder studyorder,Admission admission,String itemstr,
			String patientremark,String admissionremark,String studyorderremark,User user,String[] scanimgs) {
		boolean succeed=Db.tx(Connection.TRANSACTION_READ_COMMITTED,() -> {
					boolean ret=true,changeQue=false;
					Date now=new Date();
					//Patient beforePatient = Patient.dao.findById(patient.getId());
					// 对拼音进行特殊字符过滤
		  			patient.setPy(PinyinUtils.toPinyin(patient.getPy(),true));
					patient.setModifytime(now);
					ret=ret&&patient.update();
					
					admission.setModifytime(now);
					ret=ret&&admission.update();
					
					Studyorder so = Studyorder.dao.findById(studyorder.getId());
					
					if(StringUtils.isNotBlank(studyorder.getStudyid())) {
						//更换设备，删除原有序号
						if(so.getModalityid().intValue()!=studyorder.getModalityid().intValue()) {
					  		String sequencenumber=SequenceNo_Generator.getRegNumber_new(studyorder);
					  		if("".equals(sequencenumber)) {
					  			return false;
					  		}
					  		studyorder.setSequencenumber(sequencenumber);
					  		changeQue=true;
						}
						studyorder.setModifytime(now);
						try {
							Db.update("DELETE FROM studyitem WHERE orderid = ?", studyorder.getId());
							StringBuilder studyitems=new StringBuilder();
							updateStudyitem(itemstr, studyorder, studyitems,now);
							String str=studyitems.toString();
							if(str.length()>0){
								studyorder.setStudyitems(str.substring(0,str.length()-1));
							}
						}catch(Exception e) {
							e.printStackTrace();
							return false;
						}
						ret=ret&&studyorder.update();
						
						if(changeQue) {
							DicModality dicmodality = DicModality.dao.findById(studyorder.getModalityid());
					  		Mwlitem mwl=Mwlitem.dao.findFirst("select top 1 * from mwlitem where studyorderidfk=?",studyorder.getId());
					  		mwl.setStationAet(dicmodality.getWorklistscu());
					  		mwl.setStationName(dicmodality.getModalityName());
					  		mwl.setModality(studyorder.getModalityType());
					  		mwl.setStartDatetime(now);
					  		mwl.setUpdatedTime(now);
					  		ret=ret&&mwl.update();
						}
					}
					
					
					//保存remark
					if(StringUtils.isNotBlank(patientremark)) {
						Reportremark remark=Reportremark.dao.findFirst("SELECT TOP 1 * FROM reportremark WHERE patientidfk=? AND type=?",patient.getId(),"patient");
						if(remark!=null) {
							remark.set("remarkcontent", patientremark).set("modifytime", now).update();
						}else {
							new Reportremark().set("patientidfk", patient.getId())
				  			.set("remarkcontent", patientremark).set("type", "patient")
				  			.set("creator", user.getName()).set("modifytime", now).save();
						}
		  			}else {
		  				Db.delete("DELETE FROM reportremark WHERE patientidfk=? AND type=?",patient.getId(),"patient");
		  			}	
					
					if(StringUtils.isNotBlank(admissionremark)) {
						Reportremark remark=Reportremark.dao.findFirst("SELECT TOP 1 * FROM reportremark WHERE admissionidfk=? AND type=?",admission.getId(),"admission");		
						if(remark!=null) {
							remark.set("remarkcontent", admissionremark).set("modifytime", now).update();		
						}else {
							new Reportremark().set("patientidfk", patient.getId()).set("admissionidfk", admission.getId())
				  			.set("remarkcontent", admissionremark).set("type", "admission")
				  			.set("creator", user.getName()).set("modifytime", now).save();
						}
			  			
		  			}else {
		  				Db.delete("DELETE FROM reportremark WHERE admissionidfk=? AND type=?",admission.getId(),"admission");
		  			}	
					
					if(StringUtils.isNotBlank(studyorderremark)) {
						Reportremark remark=Reportremark.dao.findFirst("SELECT TOP 1 * FROM reportremark WHERE studyid=? AND type=?",studyorder.getStudyid(),"studyorder");
						if(remark!=null) {
							remark.set("remarkcontent", studyorderremark).set("modifytime", now).update();
						}else {
							new Reportremark().set("patientidfk", patient.getId()).set("admissionidfk", admission.getId())
							.set("orderid", studyorder.getId()).set("studyid", studyorder.getStudyid())
				  			.set("remarkcontent", studyorderremark).set("type", "studyorder")
				  			.set("creator", user.getName()).set("modifytime", now).save();
						}	
			  		}else {
		  				Db.delete("DELETE FROM reportremark WHERE studyid=? AND type=?",studyorder.getStudyid(),"studyorder");
		  			}
						

					if(scanimgs!=null){
			  			StudyImage si=StudyImage.dao.findFirst("select top 1 * from study_image where orderid=?",so.getId());
			  			if(si==null){
			  				si=new StudyImage();
			  				si.setOrderId(so.getId());
			  				si.setStudyid(so.getStudyid());
			  			}
			  			for(int i=0;i<scanimgs.length;i++){
			  				try{
			  					System.out.println("scanimgs:"+scanimgs[i]);
			  					File file=new File(PropKit.get("base_upload_path")+"\\apply\\tmp\\"+scanimgs[i]);
			  					if(file.exists()){
		  							String filename=file.getName();
		  							String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
		  							String dirname=LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		  							FileUtils.moveFileToDirectory(file, new File(PropKit.get("base_upload_path")+"\\apply\\"+year+"\\"+dirname), true);
		  							for(int j=1; j<=10; j++) {
		  								if(si.get("img"+j) == null) {
		  									si.set("img"+j, "/apply/"+year+"/"+dirname+"/"+filename);
		  									break;
		  								}
		  							}
		  							//si.set("img"+(i+1), "/apply/"+dirname+"/"+filename);
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
						Db.update("delete from study_image where orderid=?",so.getId());
					}
					
					
					if(ret) {
//						if(changeQue) {
//							Record record=Db.findFirst(Db.getSql("exam.queueup_node"),studyorder.getId());
//					  		ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.delete), MQSubject.QUEUEUP.getSendName(), StrKit.getRandomUUID(), 4);
//					  		ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.Offer), MQSubject.QUEUEUP.getSendName(), StrKit.getRandomUUID(), 4);
//						}
						//发送检查进程-修改登记信息
				  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
				  				.set("patientfk", patient.getId())
				  				.set("admissionfk", admission.getId())
								.set("studyorderfk", studyorder.getId())
								.set("status", StudyprocessStatus.modifyregistered)
								.set("operator", user.getUsername())
								.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
				  		
//				  		if(!StringUtils.equals(beforePatient.getPatientname(), patient.getPatientname())) {
//				  		//更新图像信息
//					  		Record other = new Record();
//							other.set("patientid", beforePatient.getPatientid());
//							other.set("patientname", beforePatient.getPy());
//					  		ActiveMQ.sendObjectMessage(new PacsUpdateOrder(patient, admission, studyorder, other, "patientMerge"), MQSubject.PACSUPDATE.getSendName(), StrKit.getRandomUUID(), 4);
//				  		}
				  		
					}
					return ret;
				});
		return succeed;
	}
	
	/**
	 * 更新studyitem
	 */
	public void updateStudyitem(String itemstr, Studyorder studyorder,StringBuilder studyitems,Date now) {
		//if(StringUtils.isNotBlank(itemstr)){
		
		com.alibaba.fastjson.JSONArray items=JSON.parseArray(itemstr);
//			JSONArray items=new JSONArray(itemstr);
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
		//}
	}
	
	//查询并返回同名病人信息
		public List<Record> checkSameName(String patientname, String patientid, String hospitalizeNo) {
			List<Record> result = new ArrayList<Record>();
			String sql = "SELECT DISTINCT TOP 1000 patient.*,"
					+ " patient.id AS patientpkid,"
					+ " (SELECT name_zh FROM syscode WHERE syscode.code=patient.sex AND syscode.type='0001') AS sexdisplay"
					+ " FROM patient WHERE 0=0 ";
			String where = "";
			List<Object> list = new ArrayList<Object>();
			if(StringUtils.isNotBlank(patientname)) {
				where +=" AND patient.patientname LIKE ?";
				list.add(patientname+"%");
			}
			if(StringUtils.isNotBlank(patientid)) {
				where +=" AND patient.patientid = ?";
				list.add(patientid);
			}
			if(StringUtils.isNotBlank(hospitalizeNo)) {
				where +=" and hospitalizeNo =?";
				list.add(hospitalizeNo);
			}
			Object[] array=list.toArray();	
			result = Db.find(sql + where, array);
			return result;
		}

		//查询patient信息
		public List<Record> findPatient(String patientid, String patientname) {
			List<Record> result = new ArrayList<Record>();
			String sql = "SELECT DISTINCT patient.id,patient.patientid, patient.patientname, patient.idnumber, patient.sex,"
					+ " patient.birthdate, patient.telephone, patient.address,"
					+ " (SELECT name_zh FROM syscode WHERE syscode.code=patient.sex AND syscode.type='0001') AS sexdisplay"
					+ " FROM patient WHERE 0=0 ";
			String where="";
			List<Object> list=new ArrayList<Object>();
			if(StringUtils.isNotBlank(patientid)) {
				where +=" and patientid =?";
				list.add(patientid);
			}
			if(StringUtils.isNotBlank(patientname)) {
				where +=" and patientname LIKE ?";
				list.add(patientname+"%");
			}
			
			Object[] array=list.toArray();	
			result = Db.find(sql + where, array);
			return result;
		}
	
	//根据病人编号和姓名查找患者和入院信息
	public List<Record> findPatAndAdmission(String patientid,String patientname,String studyid,String syscode_lan){

		String where="";
		List<Object> list=new ArrayList<Object>();
		if(StringUtils.isNotBlank(patientid)) {
			where +=" and studyorder.patientid =?";
			list.add(patientid);
		}
		if(StringUtils.isNotBlank(patientname)) {
			where +=" and patientname LIKE CONCAT('%',?,'%')";
			list.add(patientname);
		}
		if(StringUtils.isNotBlank(studyid)) {
			where +=" and studyorder.studyid =?";
			list.add(studyid);
		}

		String sql="select *,(select "+syscode_lan+" from syscode where syscode.code=patient.sex and syscode.type='0001') as sexdisplay,"
				+ "(select "+syscode_lan+" from syscode where syscode.code=admission.patientsource and syscode.type='0002') as psource,"
						+ "admission.createtime as regtime,patient.id as patientkey,admission.id as admissionkey,studyorder.id as studyorderkey "
						+ "from patient,admission,studyorder "
				+ "where patient.id=admission.patientidfk and admission.id=studyorder.admissionidfk "+where +" order by regdatetime desc";
		Object[] array=list.toArray();	
		return Db.find(sql, array);
	}
	
	
	public List<Record> findRecycledPat(String patientid,String patientname){
		String where="";
		List<Object> list=new ArrayList<Object>();
		if(StringUtils.isNotBlank(patientid)) {
			where +=" and patientid =?";
			list.add(patientid);
		}
		if(StringUtils.isNotBlank(patientname)) {
			where +=" and patientname LIKE CONCAT('%',?,'%')";
			list.add(patientname+"%");
		}

		String sql="select top 50 *,(select name_zh from syscode where syscode.code=patient_recycle.sex and syscode.type='0001') as sexdisplay"
				+ " from patient_recycle where 0=0 "+where+" order by mergetime desc";
		Object[] array=list.toArray();	
		
		return Db.find(sql, array);
	}
	
	//同住院号或门诊号
	public Record checkSameNO(String inno, String outno){
		List<Object> list = new ArrayList<>();
		String sql = "SELECT TOP 1 "+Patient.dao.toSelectStr("id,createtime,creator,modifytime")
				+ ", "+Admission.dao.toSelectStr("id,createtime,creator,modifytime")
				+ ", patient.id AS patientpkid, admission.id AS admissionpkid"
				+ " FROM patient,admission"
				+ " WHERE patient.id=admission.patientidfk";
		String where="";

		if(StrKit.notBlank(inno)&&StrKit.isBlank(outno)) {
			where += " AND inno = ?";
			list.add(inno);
		}
		else if(StrKit.notBlank(outno)&&StrKit.isBlank(inno)) {
			where += " AND outno = ?";
			list.add(outno);
		}
		return Db.findFirst(sql+where, list.toArray());
	}
	
	//同名病人的检查详细信息
	public List<Record> getStudyDetail(Integer patientkid){
		
		return Db.find("SELECT *,patient.id as patientpkid,studyorder.id as studyorderpkid,admission.id as admissionpkid"
				+ " FROM studyorder,admission,patient"
				+ " WHERE patient.id=studyorder.patientidfk"
				+ " AND patient.id=admission.patientidfk"
				+ " AND studyorder.admissionidfk=admission.id"
				+ " AND patient.id = ?"
				+ " order by studyorder.studyid desc", patientkid);
	}
	
	//根据病人主键查询病人的Admission信息
	public List<Admission> getAdmission(Integer patientkid,String syscode_lan){
		String sql = "SELECT *, admission.id AS admissionpkid,"
				+ " (select _codenamedisplay_ from syscode where syscode.code=admission.patientsource and syscode.type='0002') as patientsourcedisplay"
				+ " FROM admission WHERE patientidfk = ?";
		return Admission.dao.find(sql.replaceAll("_codenamedisplay_", syscode_lan), patientkid);
	}
	
	//合并病人
	public boolean mergePat(int fromid,int toid,String topatientid,User user) {
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,() -> {
			boolean ret=true;
			Record other = new Record();
			
			List<Admission> list = Admission.dao.find("select * from admission where patientidfk=?",fromid);
			String admissionidfk="";
			for(Admission admission:list) {
				admissionidfk+=admission.getId()+",";
			}
			List<Studyorder> list2 = Studyorder.dao.find("select * from studyorder where patientidfk=?",fromid);
			String studyorderidfk="";
			for(Studyorder studyorder:list2) {
				studyorderidfk+=studyorder.getId()+",";
			}
//				try {
				Db.update("update admission set patientidfk=? where patientidfk=?",toid,fromid);
				Db.update("update studyorder set patientidfk=?, patientid=? where patientidfk =?",toid,topatientid,fromid);
				Db.update("update mwlitem set patientidfk=? where patientidfk=?",toid,fromid);
				
//				}catch(Exception e) {
//					e.printStackTrace();
//					return false;
//				}	
			
			//备份病人
			Patient patient = Patient.dao.findById(fromid);
			other.set("patientid", patient.getPatientid());
			other.set("patientname", patient.getPy());
			PatientRecycle patientRecycle = new PatientRecycle();
			try {
				BeanUtils.copyProperties(patientRecycle, patient);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//				patientRecycle.setPatientname(patient.getPatientname());
//				patientRecycle.setPy(patient.getPy());
//				patientRecycle.setPatientid(patient.getPatientid());
//				patientRecycle.setSex(patient.getSex());
//				patientRecycle.setBirthdate(patient.getBirthdate());
//				patientRecycle.setHeight(patient.getHeight());
//				patientRecycle.setWeight(patient.getWeight());
//				patientRecycle.setTitle(patient.getTitle());
//				patientRecycle.setTelephone(patient.getTelephone());
//				patientRecycle.setAddress(patient.getAddress());
//				patientRecycle.setCreator(patient.getCreator());
//				patientRecycle.setCreatetime(patient.getCreatetime());
//				patientRecycle.setModifytime(patient.getModifytime());
			patientRecycle.setPatientidfk(patient.getId());
			if(StringUtils.isNotBlank(admissionidfk)) {
				patientRecycle.setAdmissionidfk(admissionidfk.substring(0, admissionidfk.length()-1));
			}
			if(StringUtils.isNotBlank(studyorderidfk)) {
				patientRecycle.setStudyorderidfk(studyorderidfk.substring(0, studyorderidfk.length()-1));
			}
			ret=ret&&patientRecycle.remove("id").save();
			ret=ret&&patient.delete();
			
			
			//发送检查进程-取消检查
	  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
					.set("patientfk", toid)
					.set("description", fromid+"")
					.set("status", StudyprocessStatus.mergepatient)
					.set("operator", user.getUsername())
					.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
	  		
	  		
		  	Patient correctPatient = Patient.dao.findById(toid);
	  		ActiveMQ.sendObjectMessage(new PacsUpdateOrder(correctPatient, null, null, other, "patientMerge"), MQSubject.PACSUPDATE.getSendName(), StrKit.getRandomUUID(), 4);
			
			return ret;
		});
	}
	
	/**
	 * 取消合并
	 * @param id
	 * @return
	 */
	public boolean cancelMerge(Integer id,User user) {
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,() -> {
			boolean ret=true;
			Date now=new Date();
			PatientRecycle patientRecycle = PatientRecycle.dao.findById(id);
			Patient patient = new Patient();
			try {
				BeanUtils.copyProperties(patient, patientRecycle);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
//				patient.setPatientname(patientRecycle.getPatientname());
//				patient.setPy(patientRecycle.getPy());
//				patient.setPatientid(patientRecycle.getPatientid());
//				patient.setSex(patientRecycle.getSex());
//				patient.setBirthdate(patientRecycle.getBirthdate());
//				patient.setHeight(patientRecycle.getHeight());
//				patient.setWeight(patientRecycle.getWeight());
//				patient.setTitle(patientRecycle.getTitle());
//				patient.setTelephone(patientRecycle.getTelephone());
//				patient.setAddress(patientRecycle.getAddress());
//				patient.setCreator(patientRecycle.getCreator());
//				patient.setCreatetime(patientRecycle.getCreatetime());
			patient.setModifytime(now);
			ret=ret&&patient.remove("id").save();
			
			
			Db.update("update admission set patientidfk=? where id in ("+patientRecycle.getAdmissionidfk()+")",patient.getId());
			Db.update("update studyorder set patientidfk=?, patientid=? where id in ("+patientRecycle.getStudyorderidfk()+")",patient.getId(),patient.getPatientid());
			Db.update("update mwlitem set patientidfk=? where studyorderidfk in ("+patientRecycle.getStudyorderidfk()+")",patient.getId());
			Db.update("update reportremark set patientidfk=? where patientidfk=?",patient.getId(),patientRecycle.getPatientidfk());
			ret=ret&&patientRecycle.delete();
			
			//发送检查进程-取消检查
	  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
					.set("patientfk", patient.getId())
					.set("description", patientRecycle.getPatientidfk()+"")
					.set("status", StudyprocessStatus.cancelmergepatient)
					.set("operator", user.getUsername())
					.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
			
			
			return ret;
		});
	}
	
	/**
	 * 取消检查
	 */
	public boolean cancelStudyOrder(Integer orderid,User user) {
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,() -> {
			boolean ret=false;
			Studyorder so=Studyorder.dao.findById(orderid);
			
			String status=StudyOrderStatus.canceled;
			if(so.getStatus().equals(StudyOrderStatus.scheduled)){
				status=StudyOrderStatus.cancel_the_appointment;
			}
			int n=Db.update("update studyorder set status=? where id=? and (status=? or status=?)",status,orderid,StudyOrderStatus.registered,StudyOrderStatus.scheduled);
			if(n==1) {
				if(so.getStatus().equals(StudyOrderStatus.registered)){
					Db.update("delete from mwlitem where studyorderidfk=?",orderid);
					Record record=Db.findFirst(Db.getSql("exam.queueup_node"),orderid);
			  		ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.delete), MQSubject.QUEUEUP.getSendName(), StrKit.getRandomUUID(), 4);
				}
				//发送检查进程-取消检查
		  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
						.set("studyorderfk", orderid)
						.set("status", StudyprocessStatus.canceled)
						.set("operator", user.getUsername())
						.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
		  		ret=true;
			}	
			return ret;	
		});

	}

	/**
	 * 删除检查
	 */
	public boolean deleteStudyOrder(int orderid,User user){
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,() -> {
			boolean ret=true;
			try {
				
				Studyorder so=Studyorder.dao.findById(orderid);
				Db.update("delete from studyitem where orderid=?",orderid);
				Db.update("DELETE FROM reportremark WHERE orderid=?",orderid);
				if(so.getStatus().equals(StudyOrderStatus.registered)){
					Db.update("delete from mwlitem where studyorderidfk=?",orderid);
					//从叫号对列中删除
					Record record=Db.findFirst(Db.getSql("exam.queueup_node"),orderid);
			  		ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.delete), MQSubject.QUEUEUP.getSendName(), StrKit.getRandomUUID(), 4);
				}
				ret=ret&&Studyorder.dao.deleteById(orderid);
				if(ret==true) {
					//发送检查进程-删除检查
			  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
							.set("studyorderfk", orderid)
							.set("status", StudyprocessStatus.deletestudyinfo)
							.set("operator", user.getUsername())
							.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
					
				}	
			}catch(Exception e) {
				e.printStackTrace();
				return false;
			}
			return ret;
		});
	}
	
	/**
	 * 删除病人
	 * 
	 * return 1:success  2:failed  3 has can not delete order
	 * 
	 */
	public String deletePatient(int patientidfk, User user) {
		
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("ret", "1");
		Db.tx(Connection.TRANSACTION_READ_COMMITTED,() -> {

			List<Studyorder> orders = 
					Studyorder.dao.find("SELECT * FROM studyorder WHERE patientidfk = ?", patientidfk);
			List<Record> records = new ArrayList<Record>();
			try {
	
				for(Studyorder so:orders) {
					//患者存在正在检查或检查完成状态的检查记录，不能删除
					if(StudyOrderStatus.in_process.equals(so.getStatus())||
							StudyOrderStatus.completed.equals(so.getStatus())||
							StudyOrderStatus.re_examine.equals(so.getStatus())) {
						map.put("ret", "3");
						return false;
					}
					else if(StudyOrderStatus.registered.equals(so.getStatus())){
						records.add(Db.findFirst(Db.getSql("exam.queueup_node"),so.getId()));
					}
				}
				
				Db.delete("delete from admission where patientidfk=?",patientidfk);
				Db.delete("delete from mwlitem where patientidfk=?",patientidfk);
				//外键约束，先删除studyitem
				Db.delete("delete from studyitem where exists (select studyorder.id from studyorder where studyorder.id=studyitem.orderid and"
						+ " studyorder.patientidfk=?)",patientidfk);
				Db.delete("DELETE FROM reportremark WHERE patientidfk = ?",patientidfk);
				Db.delete("DELETE FROM studyorder WHERE patientidfk = ?",patientidfk);
				Patient.dao.deleteById(patientidfk);
				
			}catch(Exception e) {
				map.put("ret", "2");
				e.printStackTrace();
				return false;
			}
			
			for(Record record:records) {
			  	ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.delete), MQSubject.QUEUEUP.getSendName(), StrKit.getRandomUUID(), 4);	
			}
			//发送检查进程-删除病人信息
	  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
					.set("patientfk", patientidfk)
					.set("status", StudyprocessStatus.deletePatientinfo)
					.set("operator", user.getUsername())
					.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
			
			return true;
		});
		return map.get("ret");
	}
	
	
	/**
	 * 获取检查列表
	 */
	public Page<Record> findStudyorder(Map<String, String[]> map, String syscode_lan){
		log.info(com.alibaba.fastjson.JSONObject.toJSONString(map));
		String where="";
		List<Object> list=new ArrayList<Object>();

		String from = null;
		String to = null;
		// 时间选择 'T'今天(today) 'Y'昨天(yesterday) 'TD'三天(ThreeDay) 'FD'五天(FiveDay) ' 'W'一周 'M'一月 '不限(all)
		LocalDate now=LocalDate.now();
		if(StrKit.equals(SearchTimeConstant.Today, map.get("appdate")[0])){
			from = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate()));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() + 1));
		}else if(StrKit.equals(SearchTimeConstant.Yesterday, map.get("appdate")[0])){
			from = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals(SearchTimeConstant.ThreeDay, map.get("appdate")[0])){
			from = now.minusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals(SearchTimeConstant.FiveDay, map.get("appdate")[0])){
			from = now.minusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals(SearchTimeConstant.Week, map.get("appdate")[0])){
			from = now.minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals(SearchTimeConstant.Month, map.get("appdate")[0])){
			from = now.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
		
		
		if (StrKit.notBlank(map.get("datefrom"))) {
			from = map.get("datefrom")[0]+" 00:00:00";
		}
		if (StrKit.notBlank(map.get("dateto"))) {
			to = map.get("dateto")[0]+" 23:59:59";
		}
		
		String timecon = "";
		if (StrKit.equals("registertime", map.get("datetype")[0])) {
			timecon = "studyorder.regdatetime";
		} else if (StrKit.equals("studytime", map.get("datetype")[0])) {
			timecon = "studyorder.studydatetime";
		} else if (StrKit.equals("reporttime", map.get("datetype")[0])) {
			timecon = "studyorder.reporttime";
		} else if (StrKit.equals("appointmenttime", map.get("datetype")[0])) {
			timecon = "studyorder.appointmenttime";
		}

		if (StrKit.notBlank(from)) {
			where += " and " + timecon + " >='" + from + "'";
		}
		if (StrKit.notBlank(to)) {
			where += " and " + timecon + " <='" + to + "'";
		}

		if(map.get("orderstatus")!=null&&StrKit.notBlank(map.get("orderstatus")[0])){
			where += " and studyorder.status = ?";
			list.add(map.get("orderstatus")[0]);
		}
		if (map.get("reportstatus")!=null&&StrKit.notBlank(map.get("reportstatus")[0])) {
		    if (ReportStatus.Noresult.equals(map.get("reportstatus")[0])) {
		        where += " and report.reportstatus is null";
		    } else {
		        where += " and report.reportstatus = ?";
	            list.add(map.get("reportstatus")[0]);  
		    }
		}
		if (map.get("patientsource") != null && StringUtils.isNotBlank(map.get("patientsource")[0])){
			where += " and admission.patientsource = ?";
			list.add(map.get("patientsource")[0]);
		}
		if(map.get("studyid") != null && StringUtils.isNotBlank(map.get("studyid")[0])){
			where += " and studyorder.studyid = ?";
			list.add(map.get("studyid")[0]);
		}
		if(map.get("patientname") != null && StringUtils.isNotBlank(map.get("patientname")[0])){
			where += " and patient.patientname LIKE CONCAT('%',?,'%')";
			list.add(map.get("patientname")[0]);
		}
		if(map.get("patientid") != null && StringUtils.isNotBlank(map.get("patientid")[0])){
			where += " and patient.patientid = ?";
			list.add(map.get("patientid")[0]);
		}
		if(map.get("admissionid") != null && StringUtils.isNotBlank(map.get("admissionid")[0])) {
			where += " and admission.admissionid = ?";
			list.add(map.get("admissionid")[0]);
		}
		if(map.get("cardno") != null && StringUtils.isNotBlank(map.get("cardno")[0])) {
			where += " and admission.cardno = ?";
			list.add(map.get("cardno")[0]);
		}
		if(map.get("inno") != null && StringUtils.isNotBlank(map.get("inno")[0])) {
			where += " and admission.inno = ?";
			list.add(map.get("inno")[0]);
		}
		if(map.get("outno") != null && StringUtils.isNotBlank(map.get("outno")[0])) {
			where += " and admission.outno = ?";
			list.add(map.get("outno")[0]);
		}
		if (StrKit.notBlank(map.get("idnumber")) && StrKit.notBlank(map.get("idnumber")[0])) {
			where += " and patient.idnumber = ?";
			list.add(map.get("idnumber")[0].replaceAll(" ", ""));
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
		
		// 快速检索//////////////////////////////////////////////////////
		String quicksearchTable="";
        if (StrKit.notBlank(map.get("quicksearchcontent")) && StrKit.notBlank(map.get("quicksearchcontent")[0]) 
        		&& StrKit.notBlank(map.get("quicksearchname"))) {
        	quicksearchTable=",quicksearch ";
			if(StrKit.equals("all", map.get("quicksearchname")[0])) {
				where = " and studyorder.id=quicksearch.studyorderkey and CONTAINS(quicksearch.content, ?)";
				list=new ArrayList<Object>();
				list.add("\"" + map.get("quicksearchcontent")[0] + "\"");
			}
			else {
				where += " and studyorder.id=quicksearch.studyorderkey and CONTAINS(quicksearch.content, ?)";
				list.add("\"" + map.get("quicksearchcontent")[0] + "\"");
			} 
        }
        ///////////////////////////////////////////////////
		String select="select "+Patient.dao.toSelectStr("id,createtime,creator,modifytime")+","+Admission.dao.toSelectStr("id,createtime,creator,modifytime")
				+","+Studyorder.dao.toSelectStr("id,patientidfk,patientid,createtime,creator,modifytime,accessionnumber")
				+ ",patient.id as patientpkid,studyorder.id as studyorderpkid,admission.id as admissionpkid"
				+ ",(select _codenamedisplay_ from syscode where syscode.code=studyorder.status and syscode.type='0005') as orderstatus"
				+ ",(select _codenamedisplay_ from syscode where syscode.code=admission.patientsource and syscode.type='0002') as psource"
				+ ",(select _codenamedisplay_ from syscode where syscode.code=patient.sex and syscode.type='0001') as sexdisplay"
				+ ",(select _codenamedisplay_ from syscode where syscode.code=admission.ageunit and syscode.type='0008') as ageunitdisplay"
				+ ",(select modality_name from dic_modality where dic_modality.id=studyorder.modalityid) as modalityname"
				+ ",report.id AS reportid,report.template_id AS templateId"
				+ ",(CASE WHEN report.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE report.reportstatus END) AS reportstatusdisplaycode"
				+ ",(SELECT  _codenamedisplay_  FROM syscode  WHERE syscode.code = (CASE WHEN report.reportstatus IS NULL THEN "
				+ReportStatus.Noresult+" ELSE report.reportstatus END) AND syscode.type = '0007') AS reportstatusdisplay"
				+ ",(CASE WHEN report.printcount IS NULL THEN 0 ELSE report.printcount END) AS reportprintcount ";
		String sqlExceptSelect = " from patient,admission,studyorder"
				+ " LEFT JOIN report ON studyorder.id = report.studyorderfk" + quicksearchTable
				+ " where 0=0 "
				+ " and patient.id=admission.patientidfk"
				+ " and studyorder.admissionidfk=admission.id"
				+where+" order by studyorder.regdatetime desc";
		
		select =select.replaceAll("_codenamedisplay_", syscode_lan);
		return Db.paginate(Integer.valueOf(map.get("page")[0]), Integer.valueOf(map.get("rows")[0]), select, sqlExceptSelect, list.toArray());
	}
	
	public List<Record> findApplication(Record record,String syscode_lan){
		List<Record> applys=null;
		String where="";
		List<Object> list=new ArrayList<Object>();
		String status = record.get("status");
		String patientname = record.get("patientname");
		String patientid = record.get("patientid");
		String inno = record.get("inno");
		String outno = record.get("outno");
		String studyid = record.get("studyid");
		String modality_type = record.get("modality_type");
		String appdept = record.get("appdept");
		String appdoctor = record.get("appdoctor");
		
		String appdate = record.get("appdate");
		String datefrom = record.get("datefrom");
		String dateto = record.get("dateto");
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		String from=null;
		String to=null;
		
		LocalDate now=LocalDate.now();
		if(StrKit.equals("T", appdate)){
			from = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate()));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() + 1));
		}else if(StrKit.equals("Y", appdate)){
			from = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals("TD", appdate)){
			from = now.minusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals("FD", appdate)){
			from = now.minusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals("W", appdate)){
			from = now.minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals("M", appdate)){
			from = now.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
	
		if (StringUtils.isNotBlank(datefrom)) {
			from = datefrom+" 00:00:00";
		}
		if (StringUtils.isNotBlank(dateto)) {
			to = dateto+" 23:59:59";
		}
		
		if (StringUtils.isNotBlank(from)) {
			where += " and eorder.createtime >='" + from + "'";
		}
		if (StringUtils.isNotBlank(to)) {
			where += " and eorder.createtime <='" + to + "'";
		}
		if(StringUtils.isNotBlank(status)) {
			where += " and eorder.status = ?";
			list.add(status);
		}
		if(StringUtils.isNotBlank(patientname)){
			where += " and eorder.patientname LIKE CONCAT('%',?,'%')";
			list.add(patientname);
		}
		if(StringUtils.isNotBlank(patientid)){
			where += " and eorder.patientid = ?";
			list.add(patientid);
		}
		if(StringUtils.isNotBlank(inno)) {
			where += " and eorder.inno = ?";
			list.add(inno);
		}
		if(StringUtils.isNotBlank(outno)) {
			where += " and eorder.outno = ?";
			list.add(outno);
		}
		if(StringUtils.isNotBlank(studyid)){
			where += " and eorder.studyid = ?";
			list.add(studyid);
		}
		if(StringUtils.isNotBlank(modality_type)){
			where += " and eorder.modality_type = ?";
			list.add(modality_type);
		}
		if(StringUtils.isNotBlank(appdept)){
			where += " and eorder.appdept = ?";
			list.add(appdept);
		}
		if(StringUtils.isNotBlank(appdoctor)){
			where += " and eorder.appdoctor = ?";
			list.add(appdoctor);
		}
		String sql="select *"
				+ ",(select _codenamedisplay_ from syscode where syscode.code=eorder.status and syscode.type='0005') as orderstatus"
				+ ",(select _codenamedisplay_ from syscode where syscode.code=eorder.sex and syscode.type='0001') as sexdisplay"
				+ ",(select _codenamedisplay_ from syscode where syscode.code=eorder.ageunit and syscode.type='0008') as ageunitdisplay"
				+ " from eorder"
				+ " where 0=0 "+where;
		
		String sqlPara =sql.replaceAll("_codenamedisplay_", syscode_lan);
		Object[] array=list.toArray();		
		applys=Db.find(sqlPara, array);
		return applys;
	}
	
	/**
	 * 根据StudyOrder获取病人的详细信息
	 * @param StudyOrder
	 */
	public Record getPatientByOrder(Studyorder StudyOrder) {
		Record record=new Record();
		Patient patient=Patient.dao.findById(StudyOrder.getPatientidfk());
		Admission admission=Admission.dao.findById(StudyOrder.getAdmissionidfk());
		DicModality dicmodality=DicModality.dao.findById(StudyOrder.getModalityid());
//		String doctorname="";
//		if(StudyOrder.getAppdoctor()!=null) {
//			DicEmployee doctor=DicEmployee.dao.findById(StudyOrder.getAppdoctor());
//			doctorname = doctor.getName();
//		}
		
		
		record.set("patientname", patient.getPatientname());
		record.set("modalityname", dicmodality.getModalityName());
		record.set("location", dicmodality.getLocation());
		record.set("patientsource", admission.getPatientsource());
		record.set("doctor", StudyOrder.getAppdoctorname());
		
		record.set("patientidfk", patient.getId());
		record.set("admissionidfk", admission.getId());
		
		return record;
	}
	
	/**
	 * 发送消息到队列中
	 * @param so
	 * @param method
	 */
	public void sendQueue(Studyorder so,Record record,Enum method) {
//		Record record = getPatientByOrder(so);
  		boolean state=false;
  		if("E".equals(record.get("patientsource"))) state=true;
  		new QueueupSender(so,record.get("patientname"),record.get("modalityname")
  				,record.get("location"),record.get("doctor"),state).send(method);
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
			where += " and (creator= "+creator+" or creator = 0)";
		}
		if (StringUtils.isNotBlank(filterType)) {
			where += " and filter_type= '"+filterType+"'";
		}
		String sql = "SELECT *,case when modality is null then '' else modality end as modality FROM  filter WHERE 0 = 0 " + where +" ORDER BY createtime DESC ";
		List<Record> ret= Db.find(sql);
		ret.forEach(record->{
			record.set("orderstatus_display", SyscodeKit.INSTANCE.getCodeDisplay("0023",record.getStr("orderstatus"), locale));
			record.set("reportstatus_display", SyscodeKit.INSTANCE.getCodeDisplay("0007",record.getStr("reportstatus"), locale));
			record.set("appdate_display", SyscodeKit.INSTANCE.getCodeDisplay("0022",record.get("appdate"), locale));
			record.set("source", SyscodeKit.INSTANCE.getCodeDisplay("0002",record.get("patientsource"), locale));
			record.set("datetype_display", SyscodeKit.INSTANCE.getCodeDisplay("0021",record.get("datetype"), locale));
		});	
		return ret;
	}
	
	/*
	 * return 1: success  2: failed 3: has been used
	 */
	public synchronized String toSchedule(Integer orderid,Integer modalityid,String modalityType,String appointmenttime,User user) {
		Map<String,String> map = new HashMap<String, String>();
		map.put("toschret", "2");
		Db.tx(()->{
			boolean ret=false;
			//判断预约时间是否已经被占
			List<Record> list=Db.find("select id from studyorder where appointmenttime=? and modalityid=?", appointmenttime,modalityid);
			if(list==null||list.size()==0){
				
				Studyorder so=Studyorder.dao.findById(orderid);
				so.setAppointmenttime(Date.from(LocalDateTime.parse(appointmenttime,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toInstant()));
//				log.info(so.getAppdatetime());
				String sequencenumber=SequenceNo_Generator.getSchNumber_new(so);//生成预约序号
//				log.info("sequencenumber=="+sequencenumber);
		  		if("".equals(sequencenumber)) {
		  			return false;
		  		}
				int n=Db.update("update studyorder set status=?,appointmenttime=?,modifytime=?,sequencenumber=? where id=? and (status=? or status=?)",
						StudyOrderStatus.scheduled,appointmenttime,new Date(),sequencenumber,orderid,StudyOrderStatus.registered,StudyOrderStatus.scheduled);
				if(n==1) {
					
					//修改之前的状态为登记，执行下面
					if(so.getStatus().equals(StudyOrderStatus.registered)){
						Db.delete("delete from mwlitem where studyorderidfk=?", orderid);
						//从叫号对列中删除
						Record record=Db.findFirst(Db.getSql("exam.queueup_node"),orderid);
				  		ActiveMQ.sendObjectMessage(new QueueupSendOrder(new Node(record,user.getName()),QueueMethod.delete), MQSubject.QUEUEUP.getSendName(), StrKit.getRandomUUID(), 4);
					}
					
					//发送检查进程-登记转预约
			  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
							.set("studyorderfk", orderid)
							.set("status", StudyprocessStatus.registerturntoschedule)
							.set("operator", user.getUsername())
							.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
			  		
			  		map.put("toschret", "1");
			  		ret=true;
				}
			}
			else{
				map.put("toschret", "3");
			}
			return ret;
		});
		
		return map.get("toschret");
	}
	
	public Record getStudyOrderById(Integer id) {
		return Db.findFirst("select top 1 patient.patientname,patient.patientid,studyorder.studyid,studyorder.modality_type,studyorder.modalityid, "
				+ "(select modality_name from dic_modality where dic_modality.id=studyorder.modalityid) as modality_name,studyorder.id as orderid "
				+ "from patient,studyorder where patient.id=studyorder.patientidfk and studyorder.id=?",id);
	}
	
	//重新关联检查
	public boolean reassignStudy(Patient patient,Admission admission,Integer orderid,String patientremark,String admissionremark,User user) {
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,() -> {
				boolean ret=true;
				Record other = new Record();
				Date now=new Date();
				String patientid=patient.getPatientid();
		  		if(StringUtils.isBlank(patientid)){
		  			patientid=IDUtil.getPatientID();
		  			patient.setPatientid(patientid);
		  			patient.setCreator(user.getUsername());
		  			patient.setCreatetime(now);
		  			patient.setModifytime(now);
		  			ret=ret&&patient.remove("id").save();
		  		}
	
		  		String admissionid=admission.getAdmissionid();
		  		if(StringUtils.isBlank(admissionid)) {
		  			admission.setAdmissionid(IDUtil.getAdmissionID());
		  			admission.setPatientidfk(patient.getId());
		  			admission.setCreator(user.getUsername());
		  			admission.setCreatetime(now);
		  			admission.setModifytime(now);
		  			ret=ret&&admission.remove("id").save();
		  		}
		  		
		  		Studyorder studyorder = Studyorder.dao.findById(orderid);
		  		
		  		Patient wrongPatient = Patient.dao.findById(studyorder.getPatientidfk());
		  		other.set("patientid", wrongPatient.getPatientid());
				other.set("patientname", wrongPatient.getPy());
		  		
		  		studyorder.setPatientidfk(patient.getId());
		  		studyorder.setPatientid(patient.getPatientid());
		  		studyorder.setAdmissionidfk(admission.getId());
		  		studyorder.setModifytime(now);
		  		ret=ret&&studyorder.update();
		  		
		  		Mwlitem mwlitem = Mwlitem.dao.findFirst("select top 1 * from mwlitem where studyorderidfk=?",orderid);
		  		if(mwlitem != null) {
		  			mwlitem.setPatientidfk(patient.getId());
			  		mwlitem.setAdmissionidfk(admission.getId());
			  		ret=ret&&mwlitem.update();
		  		}

		  	//保存remark
				if(StringUtils.isNotBlank(patientremark)) {
					Reportremark remark=Reportremark.dao.findFirst("SELECT TOP 1 * FROM reportremark WHERE patientidfk=? AND type=?",patient.getId(),"patient");
					if(remark!=null) {
						remark.set("remarkcontent", patientremark).set("modifytime", now).update();
					}else {
						new Reportremark().set("patientidfk", patient.getId())
			  			.set("remarkcontent", patientremark).set("type", "patient")
			  			.set("creator", user.getName()).set("modifytime", now).save();
					}
	  			}else {
	  				Db.delete("DELETE FROM reportremark WHERE patientidfk=? AND type=?",patient.getId(),"patient");
	  			}	
				
				if(StringUtils.isNotBlank(admissionremark)) {
					Reportremark remark=Reportremark.dao.findFirst("SELECT TOP 1 * FROM reportremark WHERE admissionidfk=? AND type=?",admission.getId(),"admission");		
					if(remark!=null) {
						remark.set("remarkcontent", admissionremark).set("modifytime", now).update();		
					}else {
						new Reportremark().set("patientidfk", patient.getId()).set("admissionidfk", admission.getId())
			  			.set("remarkcontent", admissionremark).set("type", "admission")
			  			.set("creator", user.getName()).set("modifytime", now).save();
					}
		  			
	  			}else {
	  				Db.delete("DELETE FROM reportremark WHERE admissionidfk=? AND type=?",admission.getId(),"admission");
	  			}
				
				//发送检查进程-重新关联检查
		  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
						.set("studyorderfk", orderid)
						.set("status", StudyprocessStatus.reassignStudy)
						.set("operator", user.getUsername())
						.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
		  		
		  		ActiveMQ.sendObjectMessage(new PacsUpdateOrder(patient, null, null, other, "patientMerge"), MQSubject.PACSUPDATE.getSendName(), StrKit.getRandomUUID(), 4);
		  		
				return ret;
		});
	}
	
	public String getRemark(Integer typeId,String type) {
		String remarkcontent="";
		Reportremark reportremark=null;
		if(StrKit.equals("patient", type)) {
			reportremark=Reportremark.dao.findFirst("SELECT TOP 1 * FROM reportremark WHERE patientidfk=? AND type=?",typeId,type);
		}else if(StrKit.equals("admission", type)) {
			reportremark=Reportremark.dao.findFirst("SELECT TOP 1 * FROM reportremark WHERE admissionidfk=? AND type=?",typeId,type);
		}else if(StrKit.equals("studyorder", type)) {
			reportremark=Reportremark.dao.findFirst("SELECT TOP 1 * FROM reportremark WHERE orderid=? AND type=?",typeId,type);
		}
		if(reportremark!=null) {
			remarkcontent=reportremark.getRemarkcontent();
		}
		return remarkcontent;
	}
	
	/**
	 *  登记录入中，检查申请查询
	 * @param kit 前端的查询条件
	 * @return
	 */
	public List<Record> findStudyorder_FromThridIS(ParaKit kit) {
		List<Object> list = new ArrayList<Object>();
		String where = " where 1=1";
		if(StrKit.notBlank(kit.getPara("patientname"))){
			where+=" and patientname like ?";
			list.add(kit.getPara("patientname")+"%");
		}
		if(StrKit.notBlank(kit.getPara("appno"))){
			where+=" and eorderid = ?";
			list.add(kit.getPara("appno"));
		}
		if(StrKit.notBlank(kit.getPara("datefrom"))){
			where+=" and appdatetime >= ? ";
			list.add(kit.getPara("datefrom") +" 00:00:00");
		}
		if(StrKit.notBlank(kit.getPara("dateto"))){
			where+=" and appdatetime < ? ";
			list.add(kit.getPara("dateto") +" 23:59:59");
		}
		if(StrKit.notBlank(kit.getPara("patient_source"))){
			where+=" and eorderid_issuer = ? ";
			list.add(kit.getPara("patient_source"));
		}
		//List<Record> ret=Db.use("oracle_his2").find(Db.getSql("reg.his_orders")+where,list.toArray());
		List<Record> ret=Db.find(Db.getSql("reg.his_orders")+where,list.toArray());
		return ret;
	}
	
	public List<DicExamitem> getExamItemByHisItemCode(String hisitemcode){
		return DicExamitem.dao.find("select dic_examitem.*,dic_exam_equip.equip_id from dic_examitem left join dic_examitem_map on dic_examitem.item_code=dic_examitem_map.ris_item_code "
				+ "left join dic_exam_equip on dic_examitem.id=dic_exam_equip.exam_id "
				+ "where dic_examitem_map.his_item_code=? and deleted='0'",hisitemcode);
	}
	
	public HashMap<String, Object> getExamItemByRequestNo(String requestNo, String modality){
		List<Record> hisList = Db.use("mssql_his").find(Db.getSql("reg.his_orders_requestNo")+" where RequestNo = ? AND RequestMode = ?", requestNo, modality);
		HashMap<String, Object> map=new HashMap<String, Object>();
		String hisitemcode = "";
		Float price = 0.00f;
		Float realprice = 0.00f;
		for(Record re:hisList) {
			price += re.getFloat("price");
			realprice += re.getFloat("realprice");
			hisitemcode +=re.getStr("item_code") + ",";
		}
		List<DicExamitem> examitem = DicExamitem.dao.find("select dic_examitem.*,dic_exam_equip.equip_id from dic_examitem left join dic_examitem_map on dic_examitem.item_code=dic_examitem_map.ris_item_code "
				+ "left join dic_exam_equip on dic_examitem.id=dic_exam_equip.exam_id "
				+ "where dic_examitem_map.his_item_code in ("+ hisitemcode.substring(0, hisitemcode.length()-1) +") and deleted='0'");
		map.put("examitem", examitem);
		map.put("price", price);
		map.put("realprice", realprice);
		return map;
	}
	
	//保存急诊配置
	public HashMap<String, Object> saveEmergDefInfo(EmergencyDefaultInfo defaultInfo, String itemstr, User user) {
		HashMap<String, Object> map=new HashMap<String, Object>();
		map.put("ret", "1");
		
		Date now = new Date();
		
		defaultInfo.setCreator(user.getId());
		defaultInfo.setModifytime(now);
		if(defaultInfo.getId() != null) {
			List<EmergencyDefaultInfo> list = EmergencyDefaultInfo.dao.find("SELECT * FROM emergency_default_info WHERE configname = ? AND creator = ? AND id != ?", defaultInfo.getConfigname() , user.getId(), defaultInfo.getId());
			if(list.size() > 0) {
				map.put("ret", "2");
				return map;
			}
			Db.delete("DELETE FROM emergency_studyitem WHERE emergencyid = ?", defaultInfo.getId());
			defaultInfo.update();
		}else {
			List<EmergencyDefaultInfo> list = EmergencyDefaultInfo.dao.find("SELECT * FROM emergency_default_info WHERE configname = ? AND creator = ?", defaultInfo.getConfigname() , user.getId());
			if(list.size() > 0) {
				map.put("ret", "2");
				return map;
			}
			defaultInfo.remove("id").save();
		}
		
		if(StringUtils.isNotBlank(itemstr)) {
			com.alibaba.fastjson.JSONArray items=JSON.parseArray(itemstr);
			for(int i=0;i<items.size();i++){
				com.alibaba.fastjson.JSONObject item=items.getJSONObject(i);
				EmergencyStudyitem si=new EmergencyStudyitem();
				si.setEmergencyid(defaultInfo.getId());
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
			}
		}
		map.put("defaultInfo", defaultInfo);
		return map;
	}
	
	//删除急诊配置
	public boolean deleteEmergDefInfo(Integer id) {
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,() -> {
			boolean ret=true;
			try {
				EmergencyDefaultInfo.dao.deleteById(id);
				Db.delete("DELETE FROM emergency_studyitem WHERE emergencyid = ?", id);
			}catch(Exception e) {
				e.printStackTrace();
				return false;
			}
			return ret;
		});
	}
	
//******************************待匹配列表**************************************************
	
	//查询待匹配数据
	public List<Record> findUnmatchstudy(Map<String, String[]> map, String syscode_lan){
		String where="";
		List<Object> list=new ArrayList<Object>();

		String from = null;
		String to = null;
		// 时间选择 'T'今天(today) 'Y'昨天(yesterday) 'TD'三天(ThreeDay) 'FD'五天(FiveDay) ' 'W'一周 'M'一月 '不限(all)
		LocalDate now=LocalDate.now();
		if(StrKit.equals("T", map.get("appdate")[0])){
			from = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate()));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() + 1));
		}else if(StrKit.equals("Y", map.get("appdate")[0])){
			from = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals("TD", map.get("appdate")[0])){
			from = now.minusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals("FD", map.get("appdate")[0])){
			from = now.minusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals("W", map.get("appdate")[0])){
			from = now.minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals("M", map.get("appdate")[0])){
			from = now.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
		
		
		if (StrKit.notBlank(map.get("datefrom"))) {
			from = map.get("datefrom")[0]+" 00:00:00";
		}
		if (StrKit.notBlank(map.get("dateto"))) {
			to = map.get("dateto")[0]+" 23:59:59";
		}
		
		String timecon = "";
		if (StrKit.equals("studytime", map.get("datetype")[0])) {
			timecon = "unmatchstudy.studydatetime";
		}

		if (StrKit.notBlank(from)) {
			where += " and " + timecon + " >='" + from + "'";
		}
		if (StrKit.notBlank(to)) {
			where += " and " + timecon + " <='" + to + "'";
		}

		if(map.get("patientid") != null && StringUtils.isNotBlank(map.get("patientid")[0])){
			where += " and unmatchstudy.patientid = ?";
			list.add(map.get("patientid")[0]);
		}
		if(map.get("patientname") != null && StringUtils.isNotBlank(map.get("patientname")[0])){
			where += " and unmatchstudy.patientname LIKE CONCAT('%',?,'%')";
			list.add(map.get("patientname")[0]);
		}
		if(map.get("accessionnumber") != null && StringUtils.isNotBlank(map.get("accessionnumber")[0])){
			where += " and unmatchstudy.accessionnumber = ?";
			list.add(map.get("accessionnumber")[0]);
		}
		if(map.get("studyid") != null && StringUtils.isNotBlank(map.get("studyid")[0])){
			where += " and unmatchstudy.studyid = ?";
			list.add(map.get("studyid")[0]);
		}
		if (map.get("modality") != null && StringUtils.isNotBlank(map.get("modality")[0])) {
		    String[] strings = map.get("modality");
            if (strings.length == 1) {
                where += " and unmatchstudy.modality = ?";
                list.add(strings[0]);
            } else if (strings.length > 1) {
                String modality = "";
                for (String str : strings) {
                    modality += "'" + str + "',";
                }
                where += " and  unmatchstudy.modality in ("+modality.substring(0, modality.length()-1)+")";
            }
		}
		if(map.get("matchflag") != null && StringUtils.isNotBlank(map.get("matchflag")[0])){
			where += " and unmatchstudy.matchflag = ?";
			list.add(map.get("matchflag")[0]);
		}
		
		String sql = "SELECT unmatchstudy.*"
				+ " ,(SELECT _codenamedisplay_ FROM syscode WHERE syscode.code=unmatchstudy.sex and syscode.type='0001') as sexdisplay"
				+ " ,(SELECT _codenamedisplay_ FROM syscode WHERE syscode.code=unmatchstudy.status and syscode.type='0005') as statusdisplay"
				+ " ,(CASE WHEN matchflag=0 THEN '未匹配' WHEN matchflag=1 THEN '已匹配' END)  AS matchflagdisplay"
				+ " FROM unmatchstudy WHERE 0=0 " + where;
		String sqlPara =sql.replaceAll("_codenamedisplay_", syscode_lan);
		Object[] array=list.toArray();	
				
		List<Record> applys=Db.find(sqlPara, array);
		
		List<Record> ret=new ArrayList<Record>();
		ret.add(new Record().set("totalsize", applys.size()));
		
		int page=Integer.valueOf(map.get("page")[0]);
		int rows=Integer.valueOf(map.get("rows")[0]);
		int start = (page-1) * rows;
		int len=start+rows;
		if(len>applys.size()) {
			len=applys.size();
		}
		for(int i=start;i<(len);i++) {
			ret.add(applys.get(i));
		}

		return ret;
	}
	
	//更改检查号
	public boolean correctAccessionNo(Integer id, String accessionnumber) {
		boolean ret = false;
		Unmatchstudy unmatchstudy = Unmatchstudy.dao.findById(id);
		unmatchstudy.setAccessionnumber(accessionnumber);
		ret = unmatchstudy.update();
		if(ret) {
			Record record = unmatchstudy.toRecord();
	  		ActiveMQ.sendObjectMessage(new PacsUpdateOrder(null, null, null, record, "procedureUpdate_ORU_R01"), MQSubject.PACSUPDATE.getSendName(), StrKit.getRandomUUID(), 4);
		}
		return ret;
	}
	
	/**
	 * 登记前验证未匹配信息是否正确
	 * @param patientid
	 * @param accessionnumber
	 * @return 1:success  2:matched  3:has same accessionnumber
	 */
	public String checkUnmatchInfo(String patientid, String accessionnumber) {
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("ret", "1");
		Unmatchstudy unmatchstudy = Unmatchstudy.dao.findFirst("SELECT TOP 1 * FROM unmatchstudy WHERE accessionnumber = ? AND matchflag = 0", accessionnumber);
		if(unmatchstudy == null) {
			map.put("ret", "2");
		}
		Studyorder studyorder = Studyorder.dao.getStudyorderByStudyid(accessionnumber);
		if(studyorder != null) {
			map.put("ret", "3");
		}
		return map.get("ret");
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
	  		mwl.setSpsStatus(SPSStatus.SCHEDULED);
	  		mwl.setAccessionNo(studyorder.getStudyid());
	  		
//	  		mwl.setIssuerPatientid("");
//	  		mwl.setReqPhysician("");
//	  		mwl.setRefePhysician("");
//	  		mwl.setPerfPhysician("");
//	  		mwl.setReqProcId("");
//	  		mwl.setReqProcDesc("");

	  		mwl.setSpsId("1");
	  		mwl.setCreatedTime(now);
  		}
  		mwl.setStationAet(dicmodality.getWorklistscu());
  		if ("CT5号".equals(dicmodality.getModalityName())) {
  			mwl.setStationName(dicmodality.getModalityName().replaceAll("号", ""));
  			if (StringUtils.isNotBlank(studyorder.getStudyitems())) {
  				mwl.setSpsDesc(PinyinUtils.toPinyin(studyorder.getStudyitems()));
  	  	  		mwl.setSpsDescPinyin(PinyinUtils.toPinyin(studyorder.getStudyitems()));
  			}
  		} else {
  			mwl.setStationName(dicmodality.getModalityName());
  			mwl.setSpsDesc(studyorder.getStudyitems());
  			if (StringUtils.isNotBlank(studyorder.getStudyitems())) {
  				mwl.setSpsDescPinyin(PinyinUtils.toPinyin(studyorder.getStudyitems()));
  			}
  		}
  		mwl.setCharacter(dicmodality.getCharacter());
  		mwl.setModality(studyorder.getModalityType());
  		mwl.setStudyIuid(IDUtil.getStudyInsUid(dicmodality.getType()));
  		mwl.setStartDatetime(now);
  		mwl.setUpdatedTime(now);
  		
  		
  		if(mwl.getId() == null) {
  			mwl.remove("id").save();
  		}else {
  			mwl.update();
  		}
	}
//******************************待匹配列表**************************************************
}
