package com.healta.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.healta.constant.ReportStatus;
import com.healta.constant.StudyOrderStatus;
import com.healta.constant.StudyprocessStatus;
import com.healta.license.VerifyLicense;
import com.healta.model.DicIcd10;
import com.healta.model.Favorites;
import com.healta.model.Favoritesreport;
import com.healta.model.Injection;
import com.healta.model.Inquiry;
import com.healta.model.Label;
import com.healta.model.Labelfolder;
import com.healta.model.Patient;
import com.healta.model.Phrase;
import com.healta.model.PhraseNode;
import com.healta.model.PreviousHistory;
import com.healta.model.Report;
import com.healta.model.Reporticd;
import com.healta.model.Reportlabel;
import com.healta.model.Reportremark;
import com.healta.model.SRTemplate;
import com.healta.model.Structreport;
import com.healta.model.Studyorder;
import com.healta.model.Studyprocess;
import com.healta.model.Syngoviafindingfile;
import com.healta.model.Syngoviaimage;
import com.healta.model.Syngoviareport;
import com.healta.model.Syngoviasrdata;
import com.healta.model.User;
import com.healta.model.Xslttemplate;
import com.healta.plugin.activemq.ActiveMQ;
import com.healta.plugin.activemq.MQSubject;
import com.healta.plugin.activemq.StudyprocessOrder;
import com.healta.plugin.workforce.WorkforceServer;
import com.healta.util.CallupKit;
import com.healta.util.DateUtil;
import com.healta.util.ReportCorrectKit;
import com.healta.util.SerializeRes;
import com.healta.util.SyscodeKit;
import com.jfinal.i18n.Res;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;


public class ReportService {
	private final static Logger log = Logger.getLogger(ReportService.class);
	
	/**
	 * 打开报告
	 * @throws Exception 
	 */
	public synchronized  Record openReport(Integer orderid,Integer reportid,User user,int srtemplateid,SerializeRes r) throws Exception {

		Record record = null;
		Record studyorder =Db.findFirst("SELECT TOP 1 studyorder.*,report.id as reportid,study.studymethod as study_studymethod from studyorder "
				+ "left join report on studyorder.id=report.studyorderfk "
				+ "left join study on studyorder.id = study.studyorderfk where studyorder.id=?",orderid);//  Studyorder.dao.findById(orderid);
		
		Report report;
		if(reportid==null&&studyorder.getInt("reportid")!=null){
			throw new Exception("报告状态已经改变，请刷新列表！");
		}
		else if(reportid!=null){
			report = Report.dao.findFirst("select top 1 *,(select name from users where users.id=report.lockingpeople) as lockingpeoplename from report where id=?" , reportid);
		}
		else{
			report = new Report();
			report.setStudyorderfk(orderid);
			report.setStudyid(studyorder.getStr("studyid"));
			report.setReportstatus(ReportStatus.Noresult);
			report.setStudymethod(studyorder.getStr("study_studymethod"));
			report.setIslocking(0);
			report.remove("id").save();
		}

		if(report.getIslocking()!=null&&report.getIslocking().intValue()==1&&!user.getId().equals(report.getLockingpeople())){//报告被锁定
			record = new Record();
			record.set("islocking", 1);
			record.set("lockingpeople", report.get("lockingpeoplename"));
			return record;
		}else{
			if(StrKit.equals(report.getReportstatus(), ReportStatus.FinalResults)&&srtemplateid>0){
				throw new Exception(r.get("report.err.reporthasaudicanotopen"));
			}
			int count = Db.update("update report set islocking=1,lockingpeople=? where id=? ",user.getId(),report.getId());
			if (count != 1) {
				throw new Exception(r.get("report.err.openreportfailed"));
			}
			record = Db.findById("report", report.getId());
			
			if(VerifyLicense.hasFunction("srreport")){//有结构化报告的license
				if(srtemplateid>0){
					SRTemplate temp=SRTemplate.dao.findById(srtemplateid);
					record.set("template_id", temp.getId());
					record.set("srtemplatecontent", temp.getTemplatecontent());
				}else{
					if(record.getInt("template_id")==null){//新建的报告				
						String sql3="select * from srtemplate";
						List<SRTemplate> reco = SRTemplate.dao.find(sql3);
						for(SRTemplate re:reco){
							String studyitems = studyorder.getStr("studyitems");
							String studyDescription = studyorder.getStr("studydescription");
							String maprule = re.getMaprule();
							
							boolean match1=false;
							boolean match2=false;
							
							try{
								if(studyitems!=null){
									match1 = Pattern.matches(maprule, studyitems);
								}
								if(studyDescription!=null){
									match2 = Pattern.matches(maprule, studyDescription);
								}
							}
							catch(Exception ex){
								log.error("PatternSyntaxException:"+ex.getMessage()+". The rule is :"+maprule);
								//ex.printStackTrace();
							}
							
							if(match1||match2){
								record.set("template_id", re.getId());
								record.set("checkresult_html", re.getTemplatecontent());
								record.set("need_format", "1");//新建的报告，内容需要格式化
								break;
							}
							
						}
					}
					
				}
			}else{//无结构化报告的license
				if(record.getInt("template_id")!=null) {//不是新建的报告
					Integer template_id=record.getInt("template_id");
					if(template_id!=null&&template_id>0){
						record.set("template_id", null);
						record.set("checkdesc_html",null);
						record.set("checkresult_html",null);
						record.set("checkdesc_txt",null);
						record.set("checkresult_txt",null);
					}
				}

			}
			log.info("template_id="+record.getStr("template_id"));
			record.set("islocking", 0);

		}
		
		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
				.set("studyorderfk", report.getStudyorderfk())
				.set("reportfk", report.getId())
				.set("status", StudyprocessStatus.openreport)
				.set("operator", user.getUsername())
				.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
		
		return record;
	}
	
	/**
	 * 打开报告
	 */
//	public Record openReportWithSRTemp(String studyid, String orderid,int srtemplateid) {
//
//		Studyorder studyorder = Studyorder.dao.findById(orderid);
//		Record record = null;
//		
//		if(studyorder.getIslocking()==1){
//			record = new Record();
//			record.set("islocking", 1);
//			record.set("lockingpeople", studyorder.getLockingpeople());
//			return record;
//		}
//		else{
//			
//			String sql = "SELECT * FROM report WHERE studyid= ? ORDER BY createtime  DESC";
//			record  = Db.findFirst(sql, studyid);
//			if(record==null){
//				record = new Record();
//			}
//			SRTemplate temp=SRTemplate.dao.findById(srtemplateid);
//			record.set("template_id", temp.getId());
//			record.set("srtemplatecontent", temp.getTemplatecontent());
//			
//			record.set("islocking", 0);
//		}
//		
//		record.set("reportstatus", studyorder.getReportstatus());
//		
//		
//		return record;
//	}
	
	public boolean closeReports(String reportids){
		
		boolean ret=true;
		try{
			if(reportids.contains(",")) {
				Db.delete("UPDATE report SET islocking=0,lockingpeople=NULL WHERE id IN ("+reportids+")");
			}
			else {
				Db.delete("UPDATE report SET islocking=0,lockingpeople=NULL WHERE id = ?",reportids);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			ret=false;
		}
		return ret;
	}

	
	public boolean closeReport(Integer reportid){
		
		boolean ret=true;
		try{
				String sql="update report set islocking=0,lockingpeople=null where id=?";
				int count =Db.update(sql, reportid);
		}
		catch(Exception ex){
			ex.printStackTrace();
			ret=false;
		}
		return ret;
	}
	
	public List<Report> getAllReport(String studyid) throws Exception {
		List<Report> list = null;
		
		try{
			list=Report.dao.find("SELECT *,(SELECT name_zh FROM `syscode` WHERE r.`reportstatus` = syscode.`code` AND TYPE='0007') AS reportstatus_zh "
					+ "FROM report r where studyid = ? ORDER BY createtime DESC ",studyid);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}
	
	/**
	 * 获取报告信息
	 * 更改条件studyid为orderid
	 * 更改条件orderid为reportid
	 * @param reportid
	 * @return
	 */
	public Record getStydyAndReportInfo(Integer reportid) {
		Record ret=Db.findFirst("select patient.*,admission.*,studyorder.*,report.*,"
				+ " report.id as reportid,study.studymethod as study_studymethod, report.studyitem AS reportstudyitem,"
				+ " (CASE WHEN report.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE report.reportstatus END) AS reportstatusdisplaycode,"
				+ "(select name_zh from syscode where admission.ageunit =syscode.code and syscode.type='0008') as ageunitdisplay,"
//				+ "(select name_zh from syscode where admission.ageunit =syscode.code and syscode.type='0006') as ageunitdisplay,"
				+ "(select name_zh from syscode where patient.sex =syscode.code and syscode.type='0001') as sexdisplay, "
				+ " CONVERT(varchar(100), studyorder.studydatetime, 120) as studytime,dic_modality.modality_name,"
				+ " (select name from dic_common where dicgroup='nuclide' and id=studyorder.nuclide) as nuclidename"
				//+ ",report.urgent,report.urgentexplain,report.pos_or_neg "
				+ " from patient,admission,studyorder"
				+ " left join report on studyorder.id=report.studyorderfk "
				+ " left join study on studyorder.id = study.studyorderfk "
				+ " left join dic_modality on studyorder.modalityid = dic_modality.id    "
				+ " where studyorder.admissionidfk = admission.id and patient.id =studyorder.patientidfk"
				+ " and report.id=? order by report.modifytime desc",reportid);
		// 检查项目的参考范围
		List<Record> records = Db.find("select studyitem.item_name, dic_examitem.reference_value from studyitem, dic_examitem where studyitem.item_id = dic_examitem.id and studyitem.orderid = ?", ret.getInt("studyorderfk"));
		String referencevalue = "";
		for (int i = 0; i < records.size(); i++) {
		    Record record = records.get(i);
		    String item_name = record.get("item_name");
		    String reference_value = record.get("reference_value");
		    if (StringUtils.isNotBlank(reference_value)) {
		        referencevalue = referencevalue + item_name + "(" + reference_value + "),";
            } else {
                referencevalue = referencevalue + item_name + ",";
            }
        }

		ret.set("referencevalue", referencevalue.length()>0?referencevalue.substring(0, referencevalue.length()-1):"");
		//未写报告时，从studyorder表中取检查方法和检查项目
		if (ReportStatus.Noresult.equals(ret.getStr("reportstatus"))){
			ret.set("studymethod", ret.getStr("examination_method"));	
			ret.set("reportstudyitem", ret.get("studyitems"));
		}
		return ret;
	}
	
	
	/**
	 * 历史报告
	 */
	
	public List<Record> historyReports(Integer patientidfk,Integer orderid,String studyid,Integer thisOrderid,User user) {
		String para_via=PropKit.use("system.properties").get("launcherviapara");
		String para_plaza=CallupKit.callUpPlaza_loadDataCmd(user);
		List<Record> ret=null;
		
		List<Object> params=new ArrayList<Object>();
		String where="";
		
		if(patientidfk!=null){
			where+=" and studyorder.patientidfk = ?";
			params.add(patientidfk);
		}
		if(orderid!=null){
			where +=" and studyorder.id = ?";
			params.add(orderid);
		}
		if(studyid!=null){
			where +=" and studyorder.studyid = ?";
			params.add(studyid);
		}
		if(thisOrderid!=null){
			where +=" and studyorder.id != ?";
			params.add(thisOrderid);
		}
		
		ret = Db.find("select studyorder.*,report.id AS reportid,"
				+ " (CASE WHEN report.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE report.reportstatus END) AS reportstatusdisplaycode,patient.patientname,"
				+ "patient.patientid,patient.birthdate,"
				+ "(select name_zh from syscode where patient.sex =syscode.code and syscode.type='0001') as sex,admission.age"
				+ " from studyorder LEFT JOIN report ON studyorder.id=report.studyorderfk  "
				+ " LEFT JOIN patient ON studyorder.patientidfk=patient.id "
				+ " LEFT JOIN admission ON studyorder.admissionidfk=admission.id "
				+ " WHERE (studyorder.status = '"+StudyOrderStatus.completed+"' or report.id IS NOT NULL) "+where+" ORDER BY studyorder.studydatetime DESC",params.toArray());
		
		if(PropKit.use("system.properties").getBoolean("enable_matrix_callup", false) && StrKit.equals("1", PropKit.get("enable_trunkDB_connetion"))) {
			Patient patient = Patient.dao.findById(patientidfk);
			List<Record> ret2 = getDataFromMatrixByPatientid(patient.getPatientid());
			ret.addAll(ret2);
		}
		
		for(Record order:ret){
			String str=order.getStr("studyid");//order.getStr("studyinstanceuid");
//			if(StrKit.isBlank(str)){
//				str=order.getStr("studyid");
//			}
			
			if(StrKit.equals("1", PropKit.use("system.properties").get("enable_via_callup"))){
				order.set("viapara", para_via.replace("?", str));
			}
			if(StrKit.equals("1", PropKit.use("system.properties").get("enable_plaza_callup"))){
				order.set("plazapara", para_plaza+" "+str);
			}
		}
		
		return ret;
	}
	
	/**
	 * 取消审核
	 * 
	 */
	public void cancelAudiReport(int reportid, User user, boolean preAudit,Record ret){
		Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
				Report report = Report.dao.findById(reportid);
				String reportStatus = "";
				if(preAudit) {
					reportStatus = ReportStatus.Preliminary_review;
				}else {
					reportStatus = ReportStatus.Preliminary;
					report.setPreAuditphysician(null);
					report.setPreAuditphysicianName("");
					report.setPreAudittime(null);
				}
				ret.set("reportstatus", reportStatus);
				try {
//					String sql="update report set reportstatus=? where id=?";			
//					Db.update(sql,reportStatus,reportid);
					report.setReportstatus(reportStatus);
					report.setAuditphysician(null);
					report.setAuditphysicianName(null);
					report.setAudittime(null);
					if(report.update()) {
						ret.set("templateid", report.getTemplateId());
						if(report.getTemplateId()!=null&&report.getTemplateId()>0) {
							ret.set("checkresult", report.getCheckresultHtml());
							ret.set("checkdesc", report.getCheckdescHtml());
						}
						
						ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
							.set("studyorderfk", report.getStudyorderfk())
							.set("reportfk", report.getId())
							.set("status", StudyprocessStatus.cancelAuditReport)
							.set("operator", user.getUsername())
							.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
					}
					
				}catch(Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
		});
	}
	
	public boolean afterCancelAudiReport(int reportid,String result,String desc) {
		Report report = Report.dao.findById(reportid);
		report.setCheckresultHtml(result);
		report.setCheckdescHtml(desc);
		return report.update();
	}
	
	/**
	 *  保存报告
	 * @param report
	 * @param publiclabel
	 * @param privatelabel
	 * @param user
	 * @param icd10 需要与icd10关联的id
	 */
	public void saveReport(Report report,String publiclabel,String privatelabel,User user, String icd10, String imageids) {
		boolean r=Db.tx(Connection.TRANSACTION_READ_COMMITTED,() -> {
			Integer id=report.getId();
			report.setTemplateId(0);
			report.setModifytime(new Date());
			if(StringUtils.equals(report.getReportstatus(), ReportStatus.Noresult)) {
				//报告状态是未写时，更新为已创建
				report.setReportstatus(ReportStatus.Created);
			}
			Report report1 = Report.dao.findById(report.getId());
			String studyprocessStatus = StudyprocessStatus.savereport;
			// 创建报告的医生
			if (report1.getCreatephysician() == null) {
				report.setCreatephysician(user.getId());
				report.setCreatephysicianName(user.getName());
				report.setCreatephysiciantime(new Date());
				studyprocessStatus = StudyprocessStatus.created;
			}
			if(StrKit.notBlank(report.getCheckdescTxt())) {
				report.setCheckdescTxt(report.getCheckdescTxt().replaceAll("  ", " "));
			}
			if(StrKit.notBlank(report.getCheckresultTxt())) {
				report.setCheckresultTxt(report.getCheckresultTxt().replaceAll("  ", " "));
			}
			report.update();
			Db.update("INSERT INTO reportrace SELECT *, operator = "+user.getId()+" FROM report where report.id=?", report.getId());
			
			// 报告关联icd10
            // 先将关联的icd10删除
            Db.delete("delete from reporticd where reportid = ?", id);
            // 在添加新关联的icd10
            if(StrKit.notBlank(icd10)){
	            String[] icd10Ids = icd10.split(",");
	            for (int i = 0; i < icd10Ids.length; i++) {
	                DicIcd10 dicIcd10 = DicIcd10.dao.findById(icd10Ids[i]);
	                if (dicIcd10 != null) {
	                    new Reporticd().set("reportid", id).set("icd_id", dicIcd10.getId()).set("icd_code", dicIcd10.getIcdCode())
	                    .set("icd_subcode", dicIcd10.getIcdSubcode()).set("icd_name", dicIcd10.getIcdName())
	                    .set("createtime", new Date()).save();
	                }
	            }
            }
			
            Db.delete("delete from reportlabel where reportfk=? and ispublic=1", id);
			
			if(StrKit.notBlank(publiclabel)){
				String[] labels=publiclabel.split(",");
				for(String label:labels){
					if(label.indexOf("_")>=0){
						Reportlabel rl=new Reportlabel();
						rl.setReportfk(id);
						rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
						rl.setCreator(user.getId());
						rl.setIspublic("1");
						rl.remove("id").save();
					}
				}
			}
			Db.delete("delete from reportlabel where reportfk=? and creator=? and ispublic=0", id, user.getId()); 
			if(StrKit.notBlank(privatelabel)){
				String[] labels=privatelabel.split(",");
				for(String label:labels){
					if(label.indexOf("_")>=0){
						Reportlabel rl=new Reportlabel();
						rl.setReportfk(id);
						rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
						rl.setCreator(user.getId());
						rl.setIspublic("0");
						rl.remove("id").save();
					}
				}
			}
			
			if(StrKit.notBlank(imageids)){
				saveImages(report,imageids);
			}
			
			ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
					.set("studyorderfk", report.getStudyorderfk())
					.set("reportfk", report.getId())
					.set("status", studyprocessStatus)
					.set("operator", user.getUsername())
					.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
			
			//初步报告,根据排版情况获取初审医生
	  		if(VerifyLicense.hasFunction("workforce")&&StrKit.equals(WorkforceServer.INSTANCE.getConfiguration().getEnableReportTask(), "1")) {
		  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
						.set("studyorderfk", report.getStudyorderfk())
						.set("status", StudyprocessStatus.created)
						.set("operator", user.getUsername())
						.set("operatorname", user.getName())), MQSubject.WORKFORCE.getSendName(), StrKit.getRandomUUID(), 4);
	  		}
			
			return true;
		});
		
	}
	
	/**
	 * 提交初步报告
	 * @param report
	 * @param publiclabel
	 * @param privatelabel
	 * @param user
	 * @param icd10  需要与icd10关联的id
	 */
	public void submitReport(Report report,String publiclabel,String privatelabel,User user, String icd10,String imageids,String devSN,HttpSession session,List<String> error) {
		boolean r=Db.tx(Connection.TRANSACTION_READ_COMMITTED,() -> {
			Date now = new Date();
			Integer id = report.getId();
			Report re=Report.dao.findById(id);
			if(re.getReportphysician()==null){//只有当报告医生为空时，才更新报告医生，报告时间
				report.setReportphysician(user.getId());
				report.setReportphysicianName(user.getName());
				report.setReporttime(now);
			}
			
			report.setReportstatus(ReportStatus.Preliminary);
			report.setTemplateId(0);
			report.setModifytime(now);
			// 创建报告的医生
			if (re.getCreatephysician() == null) {
				report.setCreatephysician(user.getId());
				report.setCreatephysicianName(user.getName());
				report.setCreatephysiciantime(new Date());
			}
			if(StrKit.notBlank(report.getCheckdescTxt())) {
				report.setCheckdescTxt(report.getCheckdescTxt().replaceAll("  ", " "));
			}
			if(StrKit.notBlank(report.getCheckresultTxt())) {
				report.setCheckresultTxt(report.getCheckresultTxt().replaceAll("  ", " "));
			}
			report.update();
			Db.update("INSERT INTO reportrace SELECT *, operator = "+user.getId()+" FROM report where report.id=?", report.getId());

			// 报告关联icd10
			// 先将关联的icd10删除
			Db.delete("delete from reporticd where reportid = ?", id);
			// 在添加新关联的icd10
			if(StrKit.notBlank(icd10)){
				String[] icd10Ids = icd10.split(",");
				for (int i = 0; i < icd10Ids.length; i++) {
				    DicIcd10 dicIcd10 = DicIcd10.dao.findById(icd10Ids[i]);
				    if (dicIcd10 != null) {
				        new Reporticd().set("reportid", id).set("icd_id", dicIcd10.getId()).set("icd_code", dicIcd10.getIcdCode())
				        .set("icd_subcode", dicIcd10.getIcdSubcode()).set("icd_name", dicIcd10.getIcdName())
				        .set("createtime", new Date()).save();
	                }
	            }
			}
			

			Db.delete("delete from reportlabel where reportfk=? and ispublic=1", id);
			
			if(StrKit.notBlank(publiclabel)){
				String[] labels=publiclabel.split(",");
				for(String label:labels){
					if(label.indexOf("_")>=0){
						Reportlabel rl=new Reportlabel();
						rl.setReportfk(id);
						rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
						rl.setCreator(user.getId());
						rl.setIspublic("1");
						rl.remove("id").save();
					}
				}
			}
			Db.delete("delete from reportlabel where reportfk=? and creator=? and ispublic=0", id, user.getId()); 
			if(StrKit.notBlank(privatelabel)){
				String[] labels=privatelabel.split(",");
				for(String label:labels){
					if(label.indexOf("_")>=0){
						Reportlabel rl=new Reportlabel();
						rl.setReportfk(id);
						rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
						rl.setCreator(user.getId());
						rl.setIspublic("0");
						rl.remove("id").save();
					}
				}
			}
			
			if(StrKit.notBlank(imageids)){
				saveImages(report,imageids);
			}
			
			ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
					.set("studyorderfk", report.getStudyorderfk())
					.set("reportfk", report.getId())
					.set("status", StudyprocessStatus.preliminary)
					.set("operator", user.getUsername())
					.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
			//初步报告,根据排版情况获取初审医生
	  		if(VerifyLicense.hasFunction("workforce")&&StrKit.equals(WorkforceServer.INSTANCE.getConfiguration().getEnableReportTask(), "1")) {
		  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
						.set("studyorderfk", report.getStudyorderfk())
						.set("status", StudyprocessStatus.preliminary)
						.set("operator", user.getUsername())
						.set("operatorname", user.getName())), MQSubject.WORKFORCE.getSendName(), StrKit.getRandomUUID(), 4);
	  		}
			return true;
		});
	}
	
	/**
	  * 初审报告
	 * @param report
	 * @param publiclabel
	 * @param privatelabel
	 * @param user
	 * @param icd10
	 */
	public void auditPreReport(Report report,String publiclabel,String privatelabel,User user, String icd10,String imageids) {
		boolean r=Db.tx(Connection.TRANSACTION_READ_COMMITTED,() -> {
			Integer id=report.getId();
			
			Date now=new Date();
			report.setReportstatus(ReportStatus.Preliminary_review);
			report.setPreAuditphysician(user.getId());
			report.setPreAuditphysicianName(user.getName());
			report.setPreAudittime(now);
			report.setTemplateId(0);

			Report re=Report.dao.findById(id);
			if(StringUtils.isBlank(re.getReportphysicianName())) {
				report.setReportphysician(user.getId());
				report.setReportphysicianName(user.getName());
			}
			if(re.getReporttime()==null){
				report.setReporttime(now);
			}
			
			report.setModifytime(now);
			if(StrKit.notBlank(report.getCheckdescTxt())) {
				report.setCheckdescTxt(report.getCheckdescTxt().replaceAll("  ", " "));
			}
			if(StrKit.notBlank(report.getCheckresultTxt())) {
				report.setCheckresultTxt(report.getCheckresultTxt().replaceAll("  ", " "));
			}
			report.update();
			Db.update("INSERT INTO reportrace SELECT *, operator = "+user.getId()+" FROM report where report.id=?", report.getId());
			
			// 报告关联icd10
            // 先将关联的icd10删除
            Db.delete("delete from reporticd where reportid = ?", id);
            // 在添加新关联的icd10
            if(StrKit.notBlank(icd10)){
	            String[] icd10Ids = icd10.split(",");
	            for (int i = 0; i < icd10Ids.length; i++) {
	                DicIcd10 dicIcd10 = DicIcd10.dao.findById(icd10Ids[i]);
	                if (dicIcd10 != null) {
	                    new Reporticd().set("reportid", id).set("icd_id", dicIcd10.getId()).set("icd_code", dicIcd10.getIcdCode())
	                    .set("icd_subcode", dicIcd10.getIcdSubcode()).set("icd_name", dicIcd10.getIcdName())
	                    .set("createtime", new Date()).save();
	                }
	            }
            }
			
            Db.delete("delete from reportlabel where reportfk=? and ispublic=1", id);
			
			if(StrKit.notBlank(publiclabel)){
				String[] labels=publiclabel.split(",");
				for(String label:labels){
					if(label.indexOf("_")>=0){
						Reportlabel rl=new Reportlabel();
						rl.setReportfk(id);
						rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
						rl.setCreator(user.getId());
						rl.setIspublic("1");
						rl.remove("id").save();
					}
				}
			}
			
			if(StrKit.notBlank(privatelabel)){
				String[] labels=privatelabel.split(",");
				for(String label:labels){
					if(label.indexOf("_")>=0){
						Reportlabel rl=new Reportlabel();
						rl.setReportfk(id);
						rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
						rl.setCreator(user.getId());
						rl.setIspublic("0");
						rl.remove("id").save();
					}
				}
			}
			
			if(StrKit.notBlank(imageids)){
				saveImages(report,imageids);
			}
			
			ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
					.set("studyorderfk", report.getStudyorderfk())
					.set("reportfk", report.getId())
					.set("status", StudyprocessStatus.Preliminaryreview)
					.set("operator", user.getUsername())
					.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
			//初审报告,根据排版情况获取审核医生
	  		if(VerifyLicense.hasFunction("workforce")&&StrKit.equals(WorkforceServer.INSTANCE.getConfiguration().getEnableReportTask(), "1")) {
		  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
						.set("studyorderfk", report.getStudyorderfk())
						.set("status", StudyprocessStatus.Preliminaryreview)
						.set("operator", user.getUsername())
						.set("operatorname", user.getName())), MQSubject.WORKFORCE.getSendName(), StrKit.getRandomUUID(), 4);
	  		}
			return true;
		});
	}
	
	/**
	 * 审核报告
	 * @param report
	 * @param publiclabel
	 * @param privatelabel
	 * @param user
	 * @param port
	 * @param icd10
	 */
	public void auditReport(Report report, String publiclabel,String privatelabel,User user,int port, String icd10,String imageids,String serverurl,List<String> error) {
		boolean r=Db.tx(Connection.TRANSACTION_READ_COMMITTED,() -> {
			
			Integer id=report.getId();
			Report re=Report.dao.findById(id);
			//报告未提交或提交人是本人不能审核报告
			if(re.getReportphysician()==null||user.getId().equals(re.getReportphysician())){
				error.add("该报告未提交或同一人不能审核自己提交的报告！");
				return false;
			}
			Date now=new Date();
			report.setReportstatus(ReportStatus.FinalResults);
			report.setAuditphysician(user.getId());
			report.setAuditphysicianName(user.getName());
			report.setAudittime(now);
			report.setTemplateId(0);
			
		
			if(re.getReportphysician()==null){
				report.setReportphysician(user.getId());
				report.setReportphysicianName(user.getName());
			}
			if(re.getReporttime()==null){
				report.setReporttime(now);
			}
			if(re.getPreAuditphysician()==null){
				report.setPreAuditphysician(user.getId());
				report.setPreAuditphysicianName(user.getName());
			}
			if(re.getPreAudittime()==null){
				report.setPreAudittime(now);
			}
			
			report.setModifytime(now);
			if(StrKit.notBlank(report.getCheckdescTxt())) {
				report.setCheckdescTxt(report.getCheckdescTxt().replaceAll("  ", " "));
			}
			if(StrKit.notBlank(report.getCheckresultTxt())) {
				report.setCheckresultTxt(report.getCheckresultTxt().replaceAll("  ", " "));
			}
			report.update();
			Db.update("INSERT INTO reportrace SELECT *, operator = "+user.getId()+" FROM report where report.id=?", report.getId());
			
			Studyorder studyorder = Studyorder.dao.findById(report.getStudyorderfk());
			studyorder.setPrecedence(1);
			studyorder.update();

			// 报告关联icd10
            // 先将关联的icd10删除
            Db.delete("delete from reporticd where reportid = ?", id);
            // 在添加新关联的icd10
            if(StrKit.notBlank(icd10)){
	            String[] icd10Ids = icd10.split(",");
	            for (int i = 0; i < icd10Ids.length; i++) {
	                DicIcd10 dicIcd10 = DicIcd10.dao.findById(icd10Ids[i]);
	                if (dicIcd10 != null) {
	                    new Reporticd().set("reportid", id).set("icd_id", dicIcd10.getId()).set("icd_code", dicIcd10.getIcdCode())
	                    .set("icd_subcode", dicIcd10.getIcdSubcode()).set("icd_name", dicIcd10.getIcdName())
	                    .set("createtime", new Date()).save();
	                }
	            }
            }
			
            Db.delete("delete from reportlabel where reportfk=? and ispublic=1", id);
			
			if(StrKit.notBlank(publiclabel)){
				String[] labels=publiclabel.split(",");
				for(String label:labels){
					if(label.indexOf("_")>=0){
						Reportlabel rl=new Reportlabel();
						rl.setReportfk(id);
						rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
						rl.setCreator(user.getId());
						rl.setIspublic("1");
						rl.remove("id").save();
					}
				}
			}
			Db.delete("delete from reportlabel where reportfk=? and creator=? and ispublic=0", id, user.getId()); 
			if(StrKit.notBlank(privatelabel)){
				String[] labels=privatelabel.split(",");
				for(String label:labels){
					if(label.indexOf("_")>=0){
						Reportlabel rl=new Reportlabel();
						rl.setReportfk(id);
						rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
						rl.setCreator(user.getId());
						rl.setIspublic("0");
						rl.remove("id").save();
					}
				}
			}
			
			if(StrKit.notBlank(imageids)){
				saveImages(report,imageids);
			}
			
			ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
					.set("studyorderfk", report.getStudyorderfk())
					.set("reportfk", report.getId())
					.set("status", StudyprocessStatus.finalresults)
					.set("operator", user.getUsername())
					.set("description", port)
					.set("operatorname", user.getName()),port,serverurl), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
			// 报告回传
			List<Reportremark> lists = Reportremark.dao.find("select remarkcontent,creator,modifytime from reportremark where studyid = ?", studyorder.getStudyid());
			Record record = new Record();
			String note = "";
			for (Reportremark reportremark : lists) {
			    note += reportremark.getCreator() + ":" + reportremark.getRemarkcontent() + " " + reportremark.getModifytime() + ";";
            }
			record.set("remarkcontent", note);
			// 检查项目
			List<Record> checkList = Db.find("SELECT studyitem.item_code,studyitem.item_name,dic_organ.treename_zh,dic_organ.modality,dic_modality.modality_name,"
                    + " dic_modality.type FROM studyitem, dic_organ, studyorder, dic_modality WHERE studyorder.id = studyitem.orderid "
                    + " and dic_modality.id = studyorder.modalityid and studyitem.organ = dic_organ.id AND studyitem.orderid = ?", studyorder.getId());
//			ActiveMQ.sendObjectMessage(new RisDataOrder(Patient.dao.findById(studyorder.getPatientidfk()), 
//			        studyorder, report, record, checkList, Hl7InterfaceConstant.TRANSMIT_OBSERVATION), MQSubject.MATRIX.getSendName(), StrKit.getRandomUUID(), 4);
			return true;
			
		});
		
	}
	
	
	
	/**
	 * 驳回报告
	 */

	public String rejectReport(Integer id,Integer orderid,String status,User user) {
		
		List<String> ret=new ArrayList<String>();
		
		boolean r=Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			
			String sql="update report set reportstatus=? where id=?";
			
			String sps="";
			
			if(StrKit.equals(status, ReportStatus.Preliminary)){
				Db.update(sql, ReportStatus.Preliminary_reject,id);
				ret.add(ReportStatus.Preliminary_reject);
				sps=StudyprocessStatus.preliminaryreject;
			}
			else if(StrKit.equals(status, ReportStatus.Preliminary_review)){
				Db.update(sql, ReportStatus.Preliminary_review_reject,id);
				ret.add(ReportStatus.Preliminary_review_reject);
				sps=StudyprocessStatus.Preliminaryreviewreject;
			}
			
			
			ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
					.set("studyorderfk", orderid)
					.set("reportfk", id)
					.set("status", sps)
					.set("operator", user.getUsername())
					.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
			
			
			return true;
			
		});
		
		return ret.size()==1?ret.get(0):status;
		
	}
	
	public boolean saveData(List<Report> dataList) {
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
            boolean saveDataResult = true;
            for (Report data : dataList) { //循环遍历dataList将所有data插入数据库
                //saveDataResult = saveDataResult && Report.dao.saveData(data); //将所有结果与，只要有一条失败就跳出循环，返回false
                if (!saveDataResult) {
                    break;
                }
            }
            // saveDataResult = saveDataResult && DataSrv.srv.saveDataIds(dataId);在关联表中插入数据
            return saveDataResult;
        });
	}
	
	public boolean saveStructReport(Report report,String structdata,String html,User user,String ctx,String publiclabel,String privatelabel, String icd10,boolean savelabel_flag){
		
		Date now=new Date();
		//html=html.replaceAll("\\[ReportPhysicianName\\]", user.getName()).replaceAll("\\[ReportDateTime\\]", DateUtil.dtsWithTime(now));
		//String temp_html=html.replaceAll(ctx+"/image/image", "/image/image");
		report.setCheckresultHtml(html);
		
		boolean succeed=Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean re=true;
			report.setModifytime(new Date());
			Report report2 = Report.dao.findById(report.getId());
			if(StringUtils.equals(report2.getReportstatus(), ReportStatus.Noresult)) {
				//报告状态是未写时，更新为已创建
				report.setReportstatus(ReportStatus.Created);
			}
			report.update();
			Db.update("INSERT INTO reportrace SELECT *, operator = "+user.getId()+" FROM report where report.id=?", report.getId());
			
			Db.update("delete from structreport where reportid=?", report.getId());
			List<JSONObject> paramList = JSON.parseArray(structdata, JSONObject.class);
			List<Structreport> srs=new ArrayList<Structreport>();
			for(JSONObject obj:paramList){
				Structreport sr=new Structreport();
				sr.setReportid(report.getId());
				sr.setCode(obj.getString("code"));
				sr.setOptioncode( obj.getString("optioncode"));
				sr.setValue( obj.getString("value"));
				sr.setUnit( obj.getString("unit"));
				sr.setGrp(obj.getString("group"));
				srs.add(sr);
			}
			Db.batchSave(srs,srs.size());
	
			if(savelabel_flag){
				// 报告关联icd10
                // 先将关联的icd10删除
                Db.delete("delete from reporticd where reportid = ?", report.getId());
                // 在添加新关联的icd10
                if(StrKit.notBlank(icd10)){
	                String[] icd10Ids = icd10.split(",");
	                for (int i = 0; i < icd10Ids.length; i++) {
	                    DicIcd10 dicIcd10 = DicIcd10.dao.findById(icd10Ids[i]);
	                    if (dicIcd10 != null) {
	                        new Reporticd().set("reportid", report.getId()).set("icd_id", dicIcd10.getId()).set("icd_code", dicIcd10.getIcdCode())
	                        .set("icd_subcode", dicIcd10.getIcdSubcode()).set("icd_name", dicIcd10.getIcdName())
	                        .set("createtime", new Date()).save();
	                    }
	                }
                }
				
				Db.delete("delete from reportlabel where reportfk=? and creator=?", report.getId(), user.getId());
				if(StrKit.notBlank(publiclabel)){
					String[] labels=publiclabel.split(",");
					for(String label:labels){
						if(label.indexOf("_")>=0){
							Reportlabel rl=new Reportlabel();
							rl.setReportfk(report.getId());
							rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
							rl.setCreator(user.getId());
							rl.setIspublic("1");
							rl.remove("id").save();
						}
					}
				}
				
				if(StrKit.notBlank(privatelabel)){
					String[] labels=privatelabel.split(",");
					for(String label:labels){
						if(label.indexOf("_")>=0){
							Reportlabel rl=new Reportlabel();
							rl.setReportfk(report.getId());
							rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
							rl.setCreator(user.getId());
							rl.setIspublic("0");
							rl.remove("id").save();
						}
					}
				}
			}
			ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
					.set("studyorderfk", report.getStudyorderfk())
					.set("reportfk", report.getId())
					.set("status", StudyprocessStatus.created)
					.set("operator", user.getUsername())
					.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
			return re;
		});
		report.setCheckresultHtml(html);
		return succeed;
	}
	
	
	public void saveStructReport_printhtml(Integer id,String printhtml,String ctx){
		Report report=new Report();
		report.setId(id);
//		printhtml=printhtml.replaceAll("alt=\"img\">", "alt=\"img\"/>");
//		log.info("alt=\"img\">"+ "alt=\"img\"\\>");
//		report.setCheckdescHtml(printhtml.replaceAll(ctx+"/image/image", "/image/image"));
		report.setCheckdescHtml(printhtml);
		report.update();
	}
	
	public boolean submitStructReport(Report report,String structdata,String html,User user,String ctx,String publiclabel,String privatelabel, String icd10){
		Date now=new Date();
		Report report2 = Report.dao.findById(report.getId());
		if(report2 == null || report2.getReportphysician() == null) {
			report.setReportphysician(user.getId());
			report.setReportphysicianName(user.getName());
			report.setReporttime(now);
			html=html.replaceAll("\\[ReportPhysicianName\\]", user.getName()).replaceAll("\\[RptDocName\\]", user.getName()).replaceAll("\\[ReportDateTime\\]", DateUtil.dtsWithTime(now));
			String temp_html=html.replaceAll(ctx+"/image/image", "/image/image");
			report.setCheckresultHtml(temp_html);
		}
		else {
			html=html.replaceAll("\\[ReportPhysicianName\\]", report2.getReportphysicianName()).replaceAll("\\[RptDocName\\]", report2.getReportphysicianName()).replaceAll("\\[ReportDateTime\\]", DateUtil.dtsWithTime(report2.getReporttime()));
			String temp_html=html.replaceAll(ctx+"/image/image", "/image/image");
			report.setCheckresultHtml(temp_html);
		}
		
		boolean succeed=Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean re=true;
			report.setReportstatus(ReportStatus.Preliminary);
			report.setModifytime(now);
			re=re&&report.update();
			Db.update("INSERT INTO reportrace SELECT *, operator = "+user.getId()+" FROM report where report.id=?", report.getId());
			
			// 报告关联icd10
            // 先将关联的icd10删除
            Db.delete("delete from reporticd where reportid = ?", report.getId());
            // 在添加新关联的icd10
            if(StrKit.notBlank(icd10)){
	            String[] icd10Ids = icd10.split(",");
	            for (int i = 0; i < icd10Ids.length; i++) {
	                DicIcd10 dicIcd10 = DicIcd10.dao.findById(icd10Ids[i]);
	                if (dicIcd10 != null) {
	                    new Reporticd().set("reportid", report.getId()).set("icd_id", dicIcd10.getId()).set("icd_code", dicIcd10.getIcdCode())
	                    .set("icd_subcode", dicIcd10.getIcdSubcode()).set("icd_name", dicIcd10.getIcdName())
	                    .set("createtime", new Date()).save();
	                }
	            }
            }

			Db.update("delete from structreport where reportid=?", report.getId());
			List<JSONObject> paramList = JSON.parseArray(structdata, JSONObject.class);
			List<Structreport> srs=new ArrayList<Structreport>();
			for(JSONObject obj:paramList){
				Structreport sr=new Structreport();
				sr.setReportid(report.getId());
				sr.setCode(obj.getString("code"));
				sr.setOptioncode( obj.getString("optioncode"));
				sr.setValue( obj.getString("value"));
				sr.setUnit( obj.getString("unit"));
				sr.setGrp(obj.getString("group"));
				srs.add(sr);
			}
			Db.batchSave(srs,srs.size());
	
			Db.delete("delete from reportlabel where reportfk=? and creator=?", report.getId(), user.getId());
			if(StrKit.notBlank(publiclabel)){
				String[] labels=publiclabel.split(",");
				for(String label:labels){
					if(label.indexOf("_")>=0){
						Reportlabel rl=new Reportlabel();
						rl.setReportfk(report.getId());
						rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
						rl.setCreator(user.getId());
						rl.setIspublic("1");
						rl.remove("id").save();
					}
				}
			}
			
			if(StrKit.notBlank(privatelabel)){
				String[] labels=privatelabel.split(",");
				for(String label:labels){
					if(label.indexOf("_")>=0){
						Reportlabel rl=new Reportlabel();
						rl.setReportfk(report.getId());
						rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
						rl.setCreator(user.getId());
						rl.setIspublic("0");
						rl.remove("id").save();
					}
				}
			}
			
			ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
					.set("studyorderfk", report.getStudyorderfk())
					.set("reportfk", report.getId())
					.set("status", StudyprocessStatus.preliminary)
					.set("operator", user.getUsername())
					.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
			//初步报告,根据排版情况获取初审医生
	  		if(VerifyLicense.hasFunction("workforce")&&StrKit.equals(WorkforceServer.INSTANCE.getConfiguration().getEnableReportTask(), "1")) {
		  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
						.set("studyorderfk", report.getStudyorderfk())
						.set("status", StudyprocessStatus.preliminary)
						.set("operator", user.getUsername())
						.set("operatorname", user.getName())), MQSubject.WORKFORCE.getSendName(), StrKit.getRandomUUID(), 4);
	  		}
			return re;
		});
		report.setCheckresultHtml(html);
		return succeed;
		
	}
	
	public boolean auditPreStructReport(Report report,String structdata,String html,User user,String ctx,String publiclabel,String privatelabel, String icd10){
		
		Date now=new Date();
		//html=html.replaceAll("\\[ReportPhysicianName\\]", user.getName()).replaceAll("\\[ReportDateTime\\]", DateUtil.dtsWithTime(now));
		String temp_html=html.replaceAll(ctx+"/image/image", "/image/image");
		report.setCheckresultHtml(temp_html);
		
		boolean succeed=Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean re=true;

			report.setReportstatus(ReportStatus.Preliminary_review);
			report.setPreAuditphysician(user.getId());
			report.setPreAuditphysicianName(user.getName());
			report.setPreAudittime(now);
			
			Report rep=Report.dao.findById(report.getId());
			if(StringUtils.isBlank(rep.getReportphysicianName())) {
				report.setReportphysician(user.getId());
				report.setReportphysicianName(user.getName());
			}
			if(rep.getReporttime()==null){
				report.setReporttime(now);
			}
			
			report.setModifytime(now);
			re=re&&report.update();
			Db.update("INSERT INTO reportrace SELECT *, operator = "+user.getId()+" FROM report where report.id=?", report.getId());
						
			Db.update("delete from structreport where reportid=?", report.getId());
			List<JSONObject> paramList = JSON.parseArray(structdata, JSONObject.class);
			List<Structreport> srs=new ArrayList<Structreport>();
			for(JSONObject obj:paramList){
				Structreport sr=new Structreport();
				sr.setReportid(report.getId());
				sr.setCode(obj.getString("code"));
				sr.setOptioncode( obj.getString("optioncode"));
				sr.setValue( obj.getString("value"));
				sr.setUnit( obj.getString("unit"));
				sr.setGrp(obj.getString("group"));
				srs.add(sr);
			}
			Db.batchSave(srs,srs.size());
	
			// 报告关联icd10
            // 先将关联的icd10删除
            Db.delete("delete from reporticd where reportid = ?", report.getId());
            // 在添加新关联的icd10
            if(StrKit.notBlank(icd10)){
                String[] icd10Ids = icd10.split(",");
                for (int i = 0; i < icd10Ids.length; i++) {
                    DicIcd10 dicIcd10 = DicIcd10.dao.findById(icd10Ids[i]);
                    if (dicIcd10 != null) {
                        new Reporticd().set("reportid", report.getId()).set("icd_id", dicIcd10.getId()).set("icd_code", dicIcd10.getIcdCode())
                        .set("icd_subcode", dicIcd10.getIcdSubcode()).set("icd_name", dicIcd10.getIcdName())
                        .set("createtime", new Date()).save();
                    }
                }
            }
			
			Db.delete("delete from reportlabel where reportfk=? and creator=?", report.getId(), user.getId());
			if(StrKit.notBlank(publiclabel)){
				String[] labels=publiclabel.split(",");
				for(String label:labels){
					if(label.indexOf("_")>=0){
						Reportlabel rl=new Reportlabel();
						rl.setReportfk(report.getId());
						rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
						rl.setCreator(user.getId());
						rl.setIspublic("1");
						rl.remove("id").save();
					}
				}
			}
			
			if(StrKit.notBlank(privatelabel)){
				String[] labels=privatelabel.split(",");
				for(String label:labels){
					if(label.indexOf("_")>=0){
						Reportlabel rl=new Reportlabel();
						rl.setReportfk(report.getId());
						rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
						rl.setCreator(user.getId());
						rl.setIspublic("0");
						rl.remove("id").save();
					}
				}
			}
			ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
					.set("studyorderfk", report.getStudyorderfk())
					.set("reportfk", report.getId())
					.set("status", StudyprocessStatus.Preliminaryreview)
					.set("operator", user.getUsername())
					.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
			//审核报告,根据排版情况获取审核医生
	  		if(VerifyLicense.hasFunction("workforce")&&StrKit.equals(WorkforceServer.INSTANCE.getConfiguration().getEnableReportTask(), "1")) {
		  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
						.set("studyorderfk", report.getStudyorderfk())
						.set("status", StudyprocessStatus.Preliminaryreview)
						.set("operator", user.getUsername())
						.set("operatorname", user.getName())), MQSubject.WORKFORCE.getSendName(), StrKit.getRandomUUID(), 4);
	  		}
			return re;
		});
		report.setCheckresultHtml(html);
		return succeed;
	}
	
	public boolean auditStructReport(Report report,String structdata,String html,User user,String ctx,String publiclabel,String privatelabel,int port, String icd10){
		
		Date now=new Date();
		html=html.replaceAll("\\[AuditPhysicianName\\]", user.getName()).replaceAll("\\[AdtDocName\\]", user.getName()).replaceAll("\\[AuditDatetime\\]", DateUtil.dtsWithTime(now));
		String temp_html=html.replaceAll(ctx+"/image/image", "/image/image");
		
		Report rep=Report.dao.findById(report.getId());
		if(rep.getReportphysician()==null){
			report.setReportphysician(user.getId());
			report.setReportphysicianName(user.getName());
			html=html.replaceAll("\\[ReportPhysicianName\\]", user.getName()).replaceAll("\\[RptDocName\\]", user.getName()).replaceAll("\\[ReportDateTime\\]", DateUtil.dtsWithTime(now));
			temp_html=html.replaceAll(ctx+"/image/image", "/image/image");
		}
		else {
			html=html.replaceAll("\\[ReportPhysicianName\\]", rep.getReportphysicianName()).replaceAll("\\[RptDocName\\]", rep.getReportphysicianName()).replaceAll("\\[ReportDateTime\\]", DateUtil.dtsWithTime(rep.getReporttime()));
			temp_html=html.replaceAll(ctx+"/image/image", "/image/image");
		}
		if(rep.getReporttime()==null){
			report.setReporttime(now);
		}
		if(rep.getPreAuditphysician()==null){	
			report.setPreAuditphysician(user.getId());
			report.setPreAuditphysicianName(user.getName());
		}
		if(rep.getPreAudittime()==null){
			report.setPreAudittime(now);
		}
		report.setCheckresultHtml(temp_html);
		
		boolean succeed=Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean re=true;

			report.setAuditphysician(user.getId());
			report.setAuditphysicianName(user.getName());
			report.setAudittime(now);
			report.setReportstatus(ReportStatus.FinalResults);
			
			Report rep1=Report.dao.findById(report.getId());
			if(rep1.getReportphysician()==null){
				report.setReportphysician(user.getId());
				report.setReportphysicianName(user.getName());
			}
			if(rep1.getReporttime()==null){
				report.setReporttime(now);
			}
			if(rep1.getPreAuditphysician()==null){
				report.setPreAuditphysician(user.getId());
				report.setPreAuditphysicianName(user.getName());
			}
			if(rep1.getPreAudittime()==null){
				report.setPreAudittime(now);
			}
			
			report.setModifytime(now);
			re=re&&report.update();
			Db.update("INSERT INTO reportrace SELECT *, operator = "+user.getId()+" FROM report where report.id=?", report.getId());
			
			Studyorder studyorder = Studyorder.dao.findById(report.getStudyorderfk());
			studyorder.setPrecedence(1);
			studyorder.update();
			
			// 报告关联icd10
            // 先将关联的icd10删除
            Db.delete("delete from reporticd where reportid = ?", report.getId());
            // 在添加新关联的icd10
            if(StrKit.notBlank(icd10)){
                String[] icd10Ids = icd10.split(",");
                for (int i = 0; i < icd10Ids.length; i++) {
                    DicIcd10 dicIcd10 = DicIcd10.dao.findById(icd10Ids[i]);
                    if (dicIcd10 != null) {
                        new Reporticd().set("reportid", report.getId()).set("icd_id", dicIcd10.getId()).set("icd_code", dicIcd10.getIcdCode())
                        .set("icd_subcode", dicIcd10.getIcdSubcode()).set("icd_name", dicIcd10.getIcdName())
                        .set("createtime", new Date()).save();
                    }
                }
            }
			
			Db.update("delete from structreport where reportid=?", report.getId());
			List<JSONObject> paramList = JSON.parseArray(structdata, JSONObject.class);
			List<Structreport> srs=new ArrayList<Structreport>();
			for(JSONObject obj:paramList){
				Structreport sr=new Structreport();
				sr.setReportid(report.getId());
				sr.setCode(obj.getString("code"));
				sr.setOptioncode( obj.getString("optioncode"));
				sr.setValue( obj.getString("value"));
				sr.setUnit( obj.getString("unit"));
				sr.setGrp(obj.getString("group"));
				srs.add(sr);
			}
			Db.batchSave(srs,srs.size());
			
			Db.delete("delete from reportlabel where reportfk=? and creator=?", report.getId(), user.getId());
			if(StrKit.notBlank(publiclabel)){
				String[] labels=publiclabel.split(",");
				for(String label:labels){
					if(label.indexOf("_")>=0){
						Reportlabel rl=new Reportlabel();
						rl.setReportfk(report.getId());
						rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
						rl.setCreator(user.getId());
						rl.setIspublic("1");
						rl.remove("id").save();
					}
				}
			}
			
			if(StrKit.notBlank(privatelabel)){
				String[] labels=privatelabel.split(",");
				for(String label:labels){
					if(label.indexOf("_")>=0){
						Reportlabel rl=new Reportlabel();
						rl.setReportfk(report.getId());
						rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
						rl.setCreator(user.getId());
						rl.setIspublic("0");
						rl.remove("id").save();
					}
				}
			}
			
			ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
					.set("studyorderfk", report.getStudyorderfk())
					.set("reportfk", report.getId())
					.set("status", StudyprocessStatus.finalresults)
					.set("description", port)
					.set("operator", user.getUsername())
					.set("operatorname", user.getName()),port,null), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
			return re;
		});
		report.setCheckresultHtml(html);
		return succeed;
	}

	public List<Record> getSyngoviaSRData(String studyid,String codes,String locale){
		List<Record> ret=new ArrayList<Record>();
		Map<String,List<Syngoviasrdata>> map=Syngoviasrdata.dao.find("select * from syngoviasrdata where studyid=?",studyid).stream().collect(Collectors.groupingBy(Syngoviasrdata::getCode));
		String code[]=codes.split(",");
		for(String co:code){
//			for(Syngoviasrdata data:datas){
			List<Syngoviasrdata> datas=map.get(co);
			if(datas!=null&&datas.size()>0){
				Syngoviasrdata data=datas.get(0);
				Record record=new Record();
				record.set("code", co);
				if(StrKit.equals(co, "00100040")) {
					String valuedisplay=SyscodeKit.INSTANCE.getCodeDisplay("0013", data.getValue(),locale);
					if(StrKit.notBlank(valuedisplay)) {
						record.set("value", valuedisplay);
					}
				} else if(StrKit.equals(co, "00080020")){//检查日期转换格式：2020-07-24
					String value=data.getValue();
					if(StrKit.notBlank(value)&&value.length()==8){
						record.set("value", value.substring(0, 4)+"-"+value.substring(4, 6)+"-"+value.substring(6));
					} else{
						record.set("value", value);
					}
				} else if(StrKit.equals(co, "00080030")){//检查时间转换格式：18:13:14
					String value=data.getValue();
					if(StrKit.notBlank(value)&&value.length()==6){
						record.set("value", value.substring(0, 2)+":"+value.substring(2, 4)+":"+value.substring(4));
					} else{
						record.set("value", value);
					}
				}
				else {
					record.set("value", data.getValue());
				}
				if(StrKit.notBlank(data.getUnit())){
					String display=SyscodeKit.INSTANCE.getCodeDisplay("0013", data.getUnit(),locale);
					if(StrKit.notBlank(display)) {
						data.setUnit(display);
					}
				}
				record.set("unit", data.getUnit());
				ret.add(record);
			}
//			}
		}
		
		if(ret.size()==0){
			Record r=Db.findFirst("select top 1 patient.patientname,patient.patientid,patient.birthdate,"
					+ "(select name_zh from syscode where code=patient.sex and type='0001') as sex,"
					+ "studyorder.studyid,"
					+ "case when admission.age is not null and admission.age !='' then cast(admission.age as nvarchar) end+(select name_zh from syscode where code=admission.ageunit and type='0008') as age,"
					+ "admission.institutionName,studyorder.accessionnumber,studyorder.studydatetime,studyorder.studydescription,studyorder.studyitems "
					+ " from patient,admission,studyorder "
					+ " where patient.id=admission.patientidfk and admission.id=studyorder.admissionidfk and studyid=?",studyid);
			
				Record record=new Record();
				record.set("code", "00100020");
				record.set("value", r.getStr("patientid"));
				ret.add(record);
				
				record=new Record();
				record.set("code", "00100010");
				record.set("value", r.getStr("patientname"));
				ret.add(record);
				
				record=new Record();
				record.set("code", "00100030");
				record.set("value", r.getStr("birthdate"));
				ret.add(record);
				
				record=new Record();
				record.set("code", "PatientsAge");
				record.set("value", StrKit.notBlank(r.getStr("age"))?r.getStr("age"):"");
				ret.add(record);
				
				record=new Record();
				record.set("code", "00100040");
				record.set("value", r.getStr("sex"));
				ret.add(record);
				
				record=new Record();
				record.set("code", "00200010");
				record.set("value", StrKit.notBlank(r.getStr("studyid"))?r.getStr("studyid"):"");
				ret.add(record);
				
				record=new Record();
				record.set("code", "00080020");
				record.set("value", r.getStr("studydatetime")!=null?r.getStr("studydatetime"):"");
				ret.add(record);
				
				record=new Record();
				record.set("code", "00080030");
				record.set("value", "");
				ret.add(record);
				
				record=new Record();
				record.set("code", "00081030");
				record.set("value", r.getStr("studydescription")!=null?r.getStr("studydescription"):"");
				ret.add(record);
				
				record=new Record();
				record.set("code", "00180015");
				record.set("value", r.getStr("studyitems")!=null?r.getStr("studyitems"):"");
				ret.add(record);
				
				record=new Record();
				record.set("code", "00081040");
				record.set("value", r.getStr("institutionName")!=null?r.getStr("institutionName"):"");
				ret.add(record);
			
		}
		return ret;
	}
	
	public List<Favoritesreport> findFavoritesreportByNodeid(Integer nodeid){
		return Favoritesreport.dao.find("select * from favoritesreport where favorites_id=?" ,nodeid);
	}
	
	
	public JSONArray queryFavoritesList(Integer creator) {
		String sql = "select id, name,parent_id from favorites where creator = ?";
		List<Favorites> favoritesList = Favorites.dao.find(sql, creator);
		JSONObject root =null;
		if (favoritesList.size() > 0) {
			for (int i = 0; i < favoritesList.size(); i++) {
				if (favoritesList.get(i).getInt("parent_id") == 0) {
					root = new JSONObject();
					Favorites rootnode = favoritesList.get(0);
					boolean haschild = hasChild(favoritesList, rootnode.getId());
					root.put("id", rootnode.getId());
					root.put("text", rootnode.getName());
					root.put("state", "open");
					root.put("iconCls", "icon-fo");
					if (haschild) {
						favoritesList.remove(0);
						root.put("children", getNodes(favoritesList, rootnode.getId()));
					}
				}
			}

		}
		else{
			Favorites f=new Favorites();
			f.setName("收藏夹");
			f.setParentId(0);
			f.setCreator(creator);
			f.save();
			root=new JSONObject();
			root.put("id", f.getId());
			root.put("text", f.getName());
			root.put("state", "open");
			root.put("iconCls", "icon-fo");
		}

		JSONArray arr = new JSONArray();
		if(root!=null){
			arr.put(root);
		}
		return arr;
	}
	private boolean hasChild(List<Favorites> nodes, int id) {
		boolean ret = false;
		for (Favorites node : nodes) {
			if (node.getParentId() == id) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	/**
	 * 获取节点列表
	 * 
	 * @param nodes
	 * @param rootid
	 * @return
	 */
	private List<JSONObject> getNodes(List<Favorites> nodes, int rootid) {

		List<JSONObject> ret = new ArrayList<JSONObject>();
		
		Iterator<Favorites> iterator=nodes.iterator();
		while(iterator.hasNext()){
			Favorites node=iterator.next();
			if (node.getParentId() == rootid) {
				boolean haschild = hasChild(nodes, node.getInt("id"));
				JSONObject obj = new JSONObject();
				obj.put("id", node.getId());
				obj.put("text", node.getName());
				obj.put("state", haschild ? "closed" : "open");
				obj.put("iconCls", "icon-fo");
				if (haschild) {
					//iterator.remove();
					obj.put("children", getNodes(nodes, node.getId()));
				}
				ret.add(obj);
			}
		}
		
		
		return ret;
	}
	
	public boolean deleteFavoritesReport(int id){
		return Favoritesreport.dao.deleteById(id);
	}
	
	public void deleteFavoritesnode(int id,int creator){
		
		String sql = "select * from favorites where creator = ?";
		
		List<Favorites> favoritesList = Favorites.dao.find(sql, creator);
		
		for (int i = 0; i < favoritesList.size(); i++) {
			if (favoritesList.get(i).getInt("parent_id") == id) {
				getChildNodes(favoritesList,id);
			}
		}
		//log.info("delete from favoritesreport");
		Db.delete("delete from favoritesreport where favorites_id=?",id);
		Favorites.dao.deleteById(id);
	}
	
	
	private void getChildNodes(List<Favorites> nodes, int rootid) {

		Iterator<Favorites> iterator=nodes.iterator();
		while(iterator.hasNext()){
			Favorites node=iterator.next();
			if (node.getParentId() == rootid) {
				
				Db.delete("delete from favoritesreport where favorites_id=?",node.getId());
				node.delete();
				boolean haschild = hasChild(nodes, node.getInt("id"));
				if (haschild) {
					//iterator.remove();
					getChildNodes(nodes, node.getId());
				}
				
			}
		}
		
	}

	public List<Record> getFindings(String studyid,String studyinsuid) throws IOException, DocumentException, TransformerException{

		List<Record> records =new ArrayList<Record>();
		
			Syngoviareport via=null;
			if(StrKit.notBlank(studyinsuid)){
				via=Syngoviareport.dao.getByStudyInsUid(studyinsuid);
			}
			if(via==null&&StrKit.notBlank(studyid)){
				via=Syngoviareport.dao.getByStudyid(studyid);
			}
			
			if(via!=null){
				
				SAXReader reader = new SAXReader();

				List<Syngoviafindingfile> findings=null;//
				if(StrKit.notBlank(studyid)){
					findings=Syngoviafindingfile.dao.find("select * from syngoviafindingfile where studyid=?", studyid);
				}
				if((findings==null||findings.size()==0)&&StrKit.notBlank(studyinsuid)){
					findings=Syngoviafindingfile.dao.find("select * from syngoviafindingfile where studyinsuid=?", studyinsuid);
				}
				
				String reportname=via.getReportname();
				
				for(int i=0;i<findings.size();i++){

					try{
						Syngoviafindingfile svff=findings.get(i);
						Document doc=reader.read(new File(PropKit.use("system.properties").get("sysdir")+"\\"+svff.getFindingfile()));
						Record record=new Record();
						record.set("viaid", via.getId());
						record.set("finding", doc.asXML());
						String findingname=svff.getFindingname();
						record.set("findingname", findingname);
						Xslttemplate xslt=Xslttemplate.dao.getXsltByName(findingname,PropKit.use("system.properties").get("via_version"));
						if(xslt!=null&&xslt.getXslt()!=null){
							String html=transform(doc.asXML(),xslt.getXslt());
	//						System.out.println("html=="+html);
							record.set("html", html);
						}
						else{
							record.set("html", "");
						}
						
						if(xslt!=null&&xslt.getXsltSr()!=null){
							String htmlsr=transform(doc.asXML(),xslt.getXsltSr());
							record.set("htmlsr", htmlsr);
						}
						else{
							record.set("htmlsr", "");
						}
						
						String str[]=getFindingValueAndSource(doc,xslt);
						record.set("displayvalue", str[0]);
						record.set("source", str[1]);
						
						record.set("viareportid", via.getId());
						record.set("pdf", via.getPdffile());
						
						records.add(record);
					}
					catch(Exception ex){
						log.error(ex);
						//ex.printStackTrace();
					}
				}
			}
		
		
		return records;
		
	}
	
	
	public boolean cancleBlock(Integer reportid) {
		int n=Db.update("update report set islocking=0 ,lockingpeople=null where id=? and islocking=1" ,reportid);
		if(n==1) {
			return true;
		} else {
			return false;
		}
	}
	
	public String[] getFindingValueAndSource(Document doc,Xslttemplate xslt){
		String ret[]={null,null};
		try{
			if(xslt!=null){
				
	    		List ens=doc.getRootElement().elements("entry");
	    		
	    		for(int i=0;i<ens.size();i++){
	    			Element entry=(Element)ens.get(i);
	    			Element obs=entry.element("observation");
	    			if(xslt.getDisplayvalue()!=null){
	    				if(xslt.getDisplayvalue().equals(obs.element("code").attributeValue("code"))){
	    					ret[0]=obs.element("value").getText();
		    	    	}
	    			}
	    			if(xslt.getSource()!=null){
	    				if(xslt.getSource().equals(obs.element("code").attributeValue("code"))){
	    					ret[1]=obs.element("value").getText();
		    	    	}
	    			}
	    		}
	    		
			}
		}
		catch(Exception ex){
			
		}
		return ret;
	}
	
	
	public String transform(String xmlcontent, String xslcontent) throws TransformerException, IOException {  
        
        TransformerFactory tFac = TransformerFactory.newInstance();  
        Source xslSource = new StreamSource(new StringReader(xslcontent));  
        Transformer t = tFac.newTransformer(xslSource);  
//        File xmlFile = new File(xmlFileName);  
        File htmlFile = new File(PropKit.use("system.properties").get("tempdir")+"\\temp+"+(new Date()).getTime()+".html");  
//        Source source = new StreamSource(xmlFile);
        
        StringReader in=new StringReader(xmlcontent);
        Source source1 = new StreamSource(in);
        
        Result result = new StreamResult(htmlFile);
        t.transform(source1, result);
        
		String content = FileUtils.readFileToString(htmlFile,"UTF-8");
//		System.out.println(content);
		FileUtils.deleteQuietly(htmlFile);
//		content=content.replaceAll("\\\\T\\\\lt;", "&lt;");
//		content=content.replaceAll("\\\\T\\\\gt;", "&gt;");
		return content;
	}
	
	
	
	public List<Record> getFindingImages(String studyid,String studyinsuid) throws IOException, DocumentException, TransformerException{

		List<Record> ret =new ArrayList<Record>();
		List<Syngoviaimage> images=null;
		String top="";
		if(StrKit.notBlank(PropKit.use("system.properties").get("showimagecount"))) {
			top=" top "+PropKit.use("system.properties").get("showimagecount");
		}
		
		if(StrKit.notBlank(studyinsuid)){
			images=Syngoviaimage.dao.find("select "+top+" * from syngoviaimage where studyinsuid=? and delflag=0 order by createtime desc", studyinsuid);
		}
		else if(StrKit.notBlank(studyid)){
			images=Syngoviaimage.dao.find("select "+top+" * from syngoviaimage where studyid=? and delflag=0 order by createtime desc", studyid);
		}

		for(Syngoviaimage image:images){
			try{
				BufferedImage sourceImg =ImageIO.read(new File(PropKit.use("system.properties").get("sysdir")+"\\"+image.getImagefile()));
				
				Record im=new Record();
	        	im.set("imageid", image.getId());
	        	im.set("studyid", image.getStudyid());
	        	im.set("width", sourceImg.getWidth());
	        	im.set("height", sourceImg.getHeight());
	        	ret.add(im);
			}
			catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return ret;
		
	}
	
	public List<Record> getRemarks(Studyorder so,Integer orderid){
		if(so==null) {
			so=Studyorder.dao.findById(orderid);
		}
		List<Record> ret=new ArrayList<Record>();
		List<Record> oRecords=Db.find("select *,reportremark.creator as creator_name from reportremark"
				+ " where (patientidfk=? and type='patient') or (admissionidfk=? and type='admission')"
				+ " or orderid=? order by modifytime desc",so.getPatientidfk(),so.getAdmissionidfk(),orderid);
		for(Record re:oRecords) {
			if(StringUtils.isNotBlank(re.getStr("remarkcontent"))) {
				ret.add(re);
			}
		}
		return ret;
	}
	
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
	
	public boolean delReportRemark(Integer id) {
		boolean ret=true;
		ret=Reportremark.dao.deleteById(id);
		return ret;
	}
	
	public List<String> getReportLabel(Integer reportid, User user){
		List<Reportlabel> records=Reportlabel.dao.find("select * from reportlabel where reportfk=?",reportid);
		String publiclabel="";
		String privatelabel="";
		for(Reportlabel re:records){
			if(StrKit.equals(re.getIspublic(), "1")){
				publiclabel=publiclabel.concat(re.getLabelfk()+"_label,");
			}
			else{
				if(user.getId().intValue()==re.getCreator().intValue()) {
					privatelabel=privatelabel.concat(re.getLabelfk()+"_label,");
				}
				
			}
		}
		return new ArrayList<String>(Arrays.asList(StringUtils.substringBeforeLast(publiclabel, ","),StringUtils.substringBeforeLast(privatelabel, ",")));
	}
	
	public List<Record> getReportLabel(Integer reportid,String ispublic,Integer userid){
		String sql1=null;
		String sql2=null;
		List<Record> folders=null;
		List<Record> labels=null;
		List<Record> rootfolders=new ArrayList<Record>();
		if(StrKit.equals(ispublic, "1")){
			sql1="select * from labelfolder where ispublic=?";
			sql2="select label.* "
					+ (reportid!=null?",reportlabel.id as reportlabelid ":" ")
					+ " from label  "
					+ (reportid!=null?" left join reportlabel on label.id=reportlabel.labelfk and reportlabel.reportfk="+reportid+" ":" ")
					+ "where folderfk in(select id from labelfolder where ispublic='"+ispublic+"')";
			folders=Db.find(sql1,ispublic);
			labels=Db.find(sql2);
		}
		else{
			sql1="select * from labelfolder where ispublic=? and creator=?";
			sql2="select label.* "
					+ (reportid!=null?",reportlabel.id as reportlabelid ":" ")
					+ " from label "
					+ (reportid!=null?" left join reportlabel on label.id=reportlabel.labelfk and reportlabel.reportfk="+reportid+" ":" ")
					+ "where folderfk in(select id from labelfolder where ispublic='"+ispublic+"' and creator="+userid+")";
			folders=Db.find(sql1,ispublic,userid);
			labels=Db.find(sql2);
		}
		
		for(Record folder:folders){
			if(folder.getInt("parent")==0){
				rootfolders.add(folder);
			}
		}
		rerangeFolder(rootfolders,folders,labels);
		log.info(rootfolders);
		return rootfolders;
	}
	
	public void rerangeFolder(List<Record> rootfolders,List<Record> folders,List<Record> labels){
		for(Record rootfolder:rootfolders){
			if(StrKit.notBlank(rootfolder.getStr("foldername"))){
				rootfolder.set("text", rootfolder.getStr("foldername"));
				rootfolder.set("display", "");
				rootfolder.set("iconCls", "icon-mini-add");
				int parentid=rootfolder.getInt("id");
				List<Record> childs=getChildren_Folder(folders, parentid);
				if(childs.size() > 0) {
					List<Record> labs=getChildren_Label(labels, parentid);
					childs.addAll(labs);
					rootfolder.set("children", childs);
					rerangeFolder(childs,folders,labels);
				}else {
//					parent.set("state", "closed");
					List<Record> labs=getChildren_Label(labels, parentid);
					rootfolder.set("children", labs);
				}
			}
		}
	}
	
	private List<Record> getChildren_Folder(List<Record> nodes,int parent){
		List<Record> ret=new ArrayList<Record>();
		for(Record re:nodes){
			if(re.getInt("parent").intValue()==parent){
				ret.add(re);
			}
		}
		return ret;
	}
	
	private List<Record> getChildren_Label(List<Record> labels,int parent){
		List<Record> ret=new ArrayList<Record>();
		for(Record re:labels){
			if(re.getInt("folderfk").intValue()==parent){
				re.set("text", re.getStr("label"));
				re.set("display", re.getStr("label"));
				re.set("labelid", re.getInt("id"));
				re.set("id", re.getInt("id")+"_label");
				re.set("iconCls", "icon-fo");
				ret.add(re);
			}
		}
		return ret;
	}
	
	public List<Record> getLabels(Integer userid){
		String sql1="select * from labelfolder where ispublic='0' and creator=?";
		String sql2="select * from label where creator=?";
		List<Record> folders=Db.find(sql1,userid);
		List<Record> labels=Db.find(sql2,userid);
		List<Record> parents=new ArrayList<Record>();
		for(Record folder:folders){
			if(folder.getInt("parent")==0){
				parents.add(folder);
			}
		}
		rerangeFolder(parents,folders,labels);
		log.info(parents);
		return parents;
	}
	
	public boolean saveLabelFolder(Labelfolder lf,Integer userid){
		boolean ret=true;
		
		if(lf.getId()!=null) {
			ret=lf.update();
		}
		else {
			lf.setCreator(userid);
			lf.setIspublic("0");
			ret=lf.remove("id").save();
		}
		
		return ret;
	}
	
	public boolean deleteLabelFolder(int id,Integer userid){
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			try{
				String sql1="select * from labelfolder where ispublic='0' and creator=?";
				String sql2="select * from label where creator=?";
				List<Record> folders=Db.find(sql1,userid);
				List<Record> labels=Db.find(sql2,userid);
				List<Record> parents=new ArrayList<Record>();
				
				Record root=null;
				for(Record folder:folders){
					if(folder.getInt("id").intValue()==id){
						root=folder;
					}
				}
				getChildFolder(parents,root,folders);
				for(Record record:parents){
					log.info(record);
					Db.deleteById("labelfolder", record.getInt("id"));
					Db.delete("delete from label where folderfk=?", record.getInt("id"));
					
				}
			}
			catch(Exception ex){
				ret=false;
			}
			return ret;
		});
	}
	
	public void getChildFolder(List<Record> list,Record root,List<Record> folders){

		list.add(root);
		List<Record> childs=getChildren_Folder(folders, root.getInt("id"));
		if(childs.size() > 0) {
			for(Record child:childs){
				getChildFolder(list,child,folders);
			}
		}

	}
	
	public boolean saveLabel(Label l,Integer userid){
		boolean ret=true;
		
		if(l.getId()!=null) {
			ret=l.update();
		}
		else {
			l.setCreator(userid);
			ret=l.remove("id").save();
		}
		
		return ret;
	}
	
	public boolean deleteLabel(int id){
		return Db.deleteById("label", id);
	}
	
	/**
	 * 根据id得到相应的模板名称和类型
	 * @param reportId
	 * @return
	 */
	public Record getReportInfo(Integer reportid) {
        String sql = "SELECT TOP 1 report.*,patient.patientname,report.studyid"
        		+ " FROM report LEFT JOIN studyorder ON report.studyorderfk = studyorder.id"
        		+ " LEFT JOIN patient ON studyorder.patientidfk = patient.id"
        		+ " WHERE report.id = ?";
        return Db.findFirst(sql, reportid);
	}
	
	public Integer doReportCreating(Integer orderid,String studyid,String other,User user) {
		if(StringUtils.isNotBlank(other)) {
			Report report = new Report();
			report.setStudyorderfk(orderid);
			report.setStudyid(studyid);
			report.setReportstatus(ReportStatus.Noresult);
			report.setIslocking(0);
//			report.setLockingpeople(user.getId());
			report.remove("id").save();
			log.info("OpenReport--->CreateOtherReport");
			return report.getId();
		}else {
			List<Report> reportList = Report.dao.find("SELECT * FROM report WHERE studyorderfk = ?",orderid);
			if(reportList.size()==0) {
				Report report = new Report();
				report.setStudyorderfk(orderid);
				report.setStudyid(studyid);
				report.setReportstatus(ReportStatus.Noresult);
				report.setIslocking(0);
//				report.setLockingpeople(user.getId());
				report.remove("id").save();
				log.info("OpenReport--->CreateFirstReport");
				return report.getId();
			}
		}
		
		return 0;
	}
	
	/**
	 * 下拉框 获取icd10的数据
	 * @param reportid
	 * @return
	 */
	public List<Record> getIcd10(Integer reportid) {
        List<Record> lIcd10s = Db.find("select icd_id as id, icd_code + '  ' + icd_name as text, 'true' as selected from reporticd where reportid = ?", reportid);
        return lIcd10s;
    }
	
	/**
     *  下拉框 根据搜索的内容获取icd10的数据
     */
    public List<Record> checkboxFindIcd10(String searchContent) {
        log.info("下拉框搜索的内容为：" + searchContent);
        String where = "";
        if (StringUtils.isNotBlank(searchContent)) {
            // 下拉框搜索匹配
            where += " where icd_mnemonic_code like '%" + searchContent + "%'";
            where += " or icd_name like '%" + searchContent + "%'";
            where += " or icd_code like '%" + searchContent + "%'";
        }
        List<Record> lIcd10s = Db.find("select top 1000 id, icd_code + '  ' + icd_name as text from dic_icd10 " + where);
        return lIcd10s;
    }
	
	/**
	 *  根据报告的id，获取关联的icd10
	 * @param reportId
	 * @return
	 */
	public String getIcd10ByReportId(Integer reportId) {
	    List<Record> icd10Ids = Db.find("select icd_id from reporticd where reportid = ?", reportId);
	    String ids = "";
	    for (Record icd10Id : icd10Ids) {
	        String id = icd10Id.get("icd_id");
            ids = ids + id.trim() + ",";
        }
	    log.info("关联的icd10为：" + ids);
	    return ids;
    }
	
	/**
	 *  获取icd10的树形结构
	 * @return
	 */
	public List<Record> getIcd10Tree(Integer parentid) {
	    log.info("父节点id为：" + parentid);
        List<Record> nodes = new ArrayList<Record>();
        // 查找所有的节点
        List<Record> icd10s = Db.find("select * from dic_icd_node where parentid = ?", parentid);
        for (Record icd10 : icd10s) {
            Record node = new Record();
            node.set("id", icd10.get("icd_index"));
            node.set("text", icd10.get("icd_name"));
            List<Record> childNodes = getIcd10Tree(icd10.getInt("icd_index"));
            if (!childNodes.isEmpty()) {
                // 有子节点
                node.set("children", childNodes);
                node.set("state", "closed");
            } else {
                // 没有子节点
                node.set("state", "open");
            }
            nodes.add(node);
        }
        return nodes;
    }
	
	/**
	 *  根据条件搜索icd10
	 * @param icdCode 疾病编码
	 * @param icdName 疾病名称
	 * @param icdMnemonicCode  疾病简写
	 * @return
	 */
	public List<Record> findDic10(String icdCode, String icdName) {
        List<Record> resList = new ArrayList<Record>();
        String where = "";
        if (StringUtils.isNotBlank(icdCode)) {
            where += " and icd_code like '%" + icdCode + "%'";
        }
        if (StringUtils.isNotBlank(icdName)) {
            where += " and icd_name like '%" + icdName + "%'";
        }
        resList = Db.find("select top 100 * from dic_icd10 where 0=0 " + where);
	    return resList;
    }
	
	/**
	 *  根据icdIndex 树节点查找dic10
	 * @param icdIndex
	 */
	public List<Record> findDic10ByNode(Integer icdIndex) {
        return Db.find("select * from dic_icd10 where icd_index = ?",icdIndex);
    }
	
	/**
	 * 获取报告纠错意见
	 * @param reportinfo
	 * @return
	 */
	public String getReportCorrect(Record r) {
		return ReportCorrectKit.correct(r);
	}

	
	/**
	 * 根据关键词获取规则
	 */
	public String getReportCheckRule(String keyword) {
		return Db.queryStr("select rules from report_errorcorrect_rules where keyword = ?",keyword);
	}
	
	/**
	 * 根据patientid获取Matrix检查报告信息
	 * @param patientid
	 * @return
	 */
	public List<Record> getDataFromMatrixByPatientid(String patientid){
		return Db.use(PropKit.get("trunkDb_configName")).find(Db.getSql("report.matrix_studylist"), patientid);
	}
	
	/**
	 * 根据reportid获取Matrix报告信息
	 * @param reportid
	 * @return
	 */
	public Record getReportFromMatrixByReportid(Integer reportid) {
		Record report = Db.use(PropKit.get("trunkDb_configName")).findFirst(Db.getSql("report.matrix_report"), reportid);
		if (report != null) {
            if (StringUtils.isNotBlank(report.getStr("checkdesc_txt"))) {
                String checkDesc = report.getStr("checkdesc_txt");
                // 2、jsp中对 \r\n 和 %Enter% 换行无效，使已替换为 <br>，jsp页面空格无效，替换为 &nbsp;
            	checkDesc = checkDesc.replaceAll("\\{Enter\\}","<br>").replaceAll("\r\n","<br>").replaceAll("\\s{2}","&nbsp;");
            	checkDesc = checkDesc.replaceAll("%Enter%","<br>").replaceAll("\r\n","<br>").replaceAll("\\s{2}","&nbsp;");
                report.set("checkdesc_txt", checkDesc);
            }
            if (StringUtils.isNotBlank(report.getStr("checkresult_txt"))) {
                String checkResult = report.getStr("checkresult_txt");
            	checkResult = checkResult.replaceAll("\\{Enter\\}","\r\n").replaceAll("\r\n","<br>").replaceAll("\\s{2}","&nbsp;");
            	checkResult = checkResult.replaceAll("%Enter%","\r\n").replaceAll("\r\n","<br>").replaceAll("\\s{2}","&nbsp;");
                report.set("checkresult_txt", checkResult);
            }
        }
		return report;
	}
	
	public boolean saveReportLabel(Integer reportid,String publiclabel,String privatelabel,String icd10label,Integer userid){
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			boolean ret=true;
			Db.delete("delete from reporticd where reportid = ?", reportid);
	        // 在添加新关联的icd10
			if(StrKit.notBlank(icd10label)){
		        String[] icd10Ids = icd10label.split(",");
		        for (int i = 0; i < icd10Ids.length; i++) {
		            DicIcd10 dicIcd10 = DicIcd10.dao.findById(icd10Ids[i]);
		            if (dicIcd10 != null) {
		            	ret=ret& new Reporticd().set("reportid", reportid).set("icd_id", dicIcd10.getId()).set("icd_code", dicIcd10.getIcdCode())
		                .set("icd_subcode", dicIcd10.getIcdSubcode()).set("icd_name", dicIcd10.getIcdName())
		                .set("createtime", new Date()).save();
		            }
		        }
			}
			
			Db.delete("delete from reportlabel where reportfk=?", reportid);
			if(StrKit.notBlank(publiclabel)){
				String[] labels=publiclabel.split(",");
				for(String label:labels){
					if(label.indexOf("_")>=0){
						Reportlabel rl=new Reportlabel();
						rl.setReportfk(reportid);
						rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
						rl.setCreator(userid);
						rl.setIspublic("1");
						ret=ret&rl.remove("id").save();
					}
				}
			}
			Db.delete("delete from reportlabel where reportfk=? and creator=? and ispublic=0", reportid, userid); 
			if(StrKit.notBlank(privatelabel)){
				String[] labels=privatelabel.split(",");
				for(String label:labels){
					if(label.indexOf("_")>=0){
						Reportlabel rl=new Reportlabel();
						rl.setReportfk(reportid);
						rl.setLabelfk(Integer.valueOf(label.substring(0, label.indexOf("_"))));
						rl.setCreator(userid);
						rl.setIspublic("0");
						ret=ret&rl.remove("id").save();
					}
				}
			}
			
			return ret;
		});
	}

	public List<Record> searchPhrase(String phrase_name, User user) {
		List<Record> result = new ArrayList<Record>();
		String phrasePhrase = "SELECT * FROM phrase WHERE phrase_name like CONCAT('%',?,'%') AND creator = ?";
		List<Record> list = Db.find(phrasePhrase, phrase_name, user.getId());
		System.out.println(list);
		for(Record record:list) {
			Record re = new Record();
			re.set("id", record.getInt("id"));
			re.set("text",record.getStr("phrase_name"));
			re.set("type", "content");
			re.set("attributes", record);
			re.set("state","open");
            result.add(re);
		}

		System.out.println(result);
		return result;
	}

	public List<Record> getPersonalPhrase(Integer parent, User user) {
		List<Record> result = new ArrayList<Record>();
		List<Record> list = new ArrayList<Record>();
		String sql = "SELECT * FROM phrase_node WHERE parent = ? AND creator = ?";
		String phrasePhrase = "SELECT * FROM phrase WHERE nodeid = ? AND creator = ?";
		if(parent == null) {
			parent = 0;
		}
		List<Record> nodeList = Db.find(sql, parent, user.getId());
		List<Record> phrase = Db.find(phrasePhrase, parent, user.getId());
		list.addAll(nodeList);
		list.addAll(phrase);
		
		for (Record re:list) {
			System.out.println(re);
			Record record = new Record();
			record.set("id",re.getInt("id"));
			if (re.getInt("nodeid") == null) {
				//节点
				record.set("text",re.getStr("nodename"));
				record.set("parent", re.getInt("parent"));
				record.set("type", "node");
				record.set("attributes", re);
				record.set("state","closed");
			}else {
				//非节点

				record.set("text",re.getStr("phrase_name"));
				record.set("type", "content");
				record.set("attributes", re);
				record.set("state","open");
			}
			
			if(StringUtils.isNotBlank(record.getStr("type"))) {
				result.add(record);
			}
		}
		System.out.println(result);
		return result;
	}

	public Object savePhrase(Phrase phrase, User user) {
		boolean ret = true;
		Date date = new Date();
		if(phrase.getId()!=null) {
			Phrase phrase1=Phrase.dao.findById(phrase.getId());
			phrase1.setPhraseName(phrase.getPhraseName());
			phrase1.setPhraseContent(phrase.getPhraseContent());
			phrase1.update();
		}else {
			phrase.setCreator(user.getId());
			phrase.setModifytime(date);
			phrase.remove("id").save();
		}
		return ret;
	}

	public boolean addPhrasenode(PhraseNode phraseNode, User user) {
		boolean ret = true;
		Date date = new Date();
		if(phraseNode.getId()!=null) {
			PhraseNode phraseNode1=PhraseNode.dao.findById(phraseNode.getId());
			phraseNode1.setNodename(phraseNode.getNodename());
			phraseNode1.update();
		}else {
			phraseNode.setCreator(user.getId());
			phraseNode.setModifytime(date);
			phraseNode.remove("id").save();
		}
		return ret;
	}

	public List<Syngoviaimage> getReportImages(String studyid,Integer top){
		return Syngoviaimage.dao.find("select "+(top!=null?(" top "+top):"")+" * from syngoviaimage where studyid=? and delflag=0 order by orderby",studyid);
	}
	
	public boolean delReportImages(String ids){
		boolean ret=true;
		if(StrKit.notBlank(ids)){
			for(String id:ids.split(",")){
				if(id.indexOf(".")<0){
					Syngoviaimage img = Syngoviaimage.dao.findById(id);
					if(img!=null){
						File imgfile= new File(PropKit.use("system.properties").get("sysdir")+System.getProperty("file.separator")+img.getImagefile());
						if(imgfile.exists()){
							FileUtils.deleteQuietly(imgfile);
						}
						ret=ret&img.delete();
					}
				} else{
					File image=new File(PropKit.use("system.properties").get("tempdir")+"\\"+"clipboard\\image\\"+ id);
					if(image.isFile()&&image.exists()){
						FileUtils.deleteQuietly(image);
					}
				}
			}
		}
		return ret;
	}
	
	public boolean savePrintImagesHtml(int reportid,String html,String orders){
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			List<Syngoviaimage> images =new ArrayList<Syngoviaimage>();
			JSONObject json= JSONObject.parseObject(orders);
			for(String key:json.keySet()){
				Syngoviaimage img =new Syngoviaimage();
				img.setId(Integer.valueOf(key));
				img.setOrderby(json.getInteger(key));
				images.add(img);
			}
			Db.batchUpdate(images, images.size());
//			Report report=new Report();
//			report.setId(reportid);
//			report.setPrintImagesHtml(html);
//			return report.update();
			return true;
		});
	}
	
	public boolean saveImages(Report report,String imageids){
		boolean ret=true;
		String[] imgids=imageids.split(",");
		Map<Integer, Syngoviaimage> images = Syngoviaimage.dao.find("select * from syngoviaimage where studyid=? and delflag=0 ",report.getStudyid())
					.stream().collect(Collectors.toMap(Syngoviaimage::getId, x -> x));;
		LocalDate now=LocalDate.now();
		for(int n=0,len=imgids.length;n<len;n++){
			log.info("-----"+imgids[n]);
			if(imgids[n].indexOf(".")>=0){
				File src =new File(PropKit.use("system.properties").get("tempdir")+"\\"+"clipboard\\image\\"+imgids[n]);
				String destpath =PropKit.use("system.properties").get("sysdir")+"\\"+"dicom\\"+now.getYear()+"\\"+now.getMonthValue()+"\\"+now.getDayOfMonth()+"\\"+report.getId();
				File desc =new File(destpath);
				try {
					log.info("move file:"+src.getAbsolutePath()+" to "+desc.getAbsolutePath());
					FileUtils.moveFileToDirectory(src, desc, true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					ret=false;
				}
				Syngoviaimage img=new Syngoviaimage();
//             	img.setPatientid(order.getPatientid());
             	img.setStudyid(report.getStudyid());
//             	img.setStudyinsuid(order.getStudyinstanceuid());
//             	img.setImageid(format.getString(Tag.SOPInstanceUID));
             	img.setImagefile(destpath.substring(PropKit.use("system.properties").get("sysdir").length()+1)+"\\"+imgids[n]);
             	img.setOrderby(n+1);
//             	img.setSeriesnumber(format.getString(Tag.SeriesNumber));
//             	img.setInstancenumber(format.getString(Tag.InstanceNumber));
             	img.setDelflag(0);
             	img.remove("id").save();
			} else{
				Syngoviaimage img= images.get(Integer.valueOf(imgids[n]));
				img.setOrderby(n+1);
				if(img.update()){
					images.put(img.getId(), null);
				} else{
					ret=false;
				}
			}
		}
		
		List<Syngoviaimage> deList=new ArrayList<Syngoviaimage>();
		for (Map.Entry<Integer, Syngoviaimage> entry : images.entrySet()) {
			if(entry.getValue()!=null){
				log.info(entry.getValue());
				File imgfile=new File(PropKit.use("system.properties").get("sysdir")+System.getProperty("file.separator")+entry.getValue().getImagefile());
				FileUtils.deleteQuietly(imgfile);
				if(!imgfile.exists()){
					deList.add(entry.getValue());
				}
			}
		}
		if(deList.size()>0){
			Object[][] paras= new Object[deList.size()][1];
			for(int i=0,len=deList.size();i<len;i++){
				paras[i][0]=deList.get(i).getId();
			}
			Db.batch("delete from syngoviaimage where id=?", paras, deList.size());
		}
		return ret;
	}
	
	
	public Integer getRejectReportCount(Integer userid){
		LocalDate now = LocalDate.now();
		Record r=Db.findFirst("select count(*) as num from report where reporttime>=? and reportphysician=? and reportstatus=?",
				now.minusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE),userid,ReportStatus.Preliminary_reject);
		if(StrKit.notBlank(r.getStr("num"))){
			return r.getInt("num");
		} else{
			return 0;
		}
	}

	public boolean autoSaveReport(Report report) {
		Report tmp = Report.dao.findById(report.getId());
		if(StrKit.equals(tmp.getReportstatus(), ReportStatus.Noresult)||
				StrKit.equals(tmp.getReportstatus(), ReportStatus.Created)||
				StrKit.equals(tmp.getReportstatus(), ReportStatus.Preliminary)||
				StrKit.equals(tmp.getReportstatus(), ReportStatus.Preliminary_reject)||
				StrKit.equals(tmp.getReportstatus(), ReportStatus.Preliminary_review)||
				StrKit.equals(tmp.getReportstatus(), ReportStatus.Preliminary_review_reject)){
			if(StrKit.notBlank(report.getCheckdescTxt())) {
				report.setCheckdescTxt(report.getCheckdescTxt().replaceAll("  ", " "));
			}
			if(StrKit.notBlank(report.getCheckresultTxt())) {
				report.setCheckresultTxt(report.getCheckresultTxt().replaceAll("  ", " "));
			}
			return report.update();
		} else{
			return true;
		}
	}
	
	public String getImgDesc(Integer id) {
		Syngoviaimage image=Syngoviaimage.dao.findById(id);
		if(image!=null) {
			return image.getImgDesc();
		} else {
			return null;
		}
	}
	
	public boolean saveImgDesc(Integer id ,String desc) {
		Syngoviaimage image=Syngoviaimage.dao.findById(id);
		if(image!=null) {
			image.setImgDesc(desc);
			return image.update();
		} else {
			return false;
		}
	}

	public Record findInquiryByStudyorderid(Integer studyorderid) {
		String SQL="select "+Inquiry.dao.toSelectStr("id,studyorderfk,studyid,nuclide,administration_method,createtime,modifytime,tumor_removal")
		          +","+PreviousHistory.dao.toSelectStr("id,studyorderfk,studyid,interrogation_time")
		          + ", (select name_zh from syscode where type='0039' and code=inquiry.operationmethod) AS operationmethodname"
				  + ", (select name_zh from syscode where type='0038' and code=inquiry.menstruation_conditions) AS menstruation_conditionsname"
				  + ", (select sex from patient,studyorder where patient.id=studyorder.patientidfk and studyorder.id=inquiry.studyorderfk) AS sex"
		          +" from inquiry,previous_history where inquiry.studyid=previous_history.studyid and inquiry.studyorderfk=?";
		Record record=Db.findFirst(SQL.toString(),studyorderid);
		if(record!=null) {
			String[] field=record.getColumnNames();
			ZoneId zoneId = ZoneId.systemDefault();
			for (int i = 0; i < field.length; i++) {
				String name=field[i];
				Object val=record.getObject(name);
				if(val instanceof Date) {
					Date valDate = record.getDate(name);
					Instant instant = valDate.toInstant();
			        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
			        String val_str=DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localDateTime);
			        if(StrKit.notBlank(val_str)&&val_str.indexOf("00:00:00")>0) {
			        	record.set(name, val_str.substring(0,10));
			        }
				}
			}
		}
		return record;
	}
	
	public boolean copyImageToCurrentStudy(String ids,String studyid) {
		boolean ret=true;
		if(StrKit.notBlank(ids)){
			for(String id:ids.split(",")){
				Syngoviaimage img = Syngoviaimage.dao.findById(id);
				File oldfile = new File(PropKit.use("system.properties").get("sysdir")+File.separator+img.getImagefile());
				if(oldfile.exists()) {
					String imagefile=img.getImagefile();
					String newImageFile=imagefile.substring(0, imagefile.lastIndexOf("."))+"_"+StrKit.getRandomUUID()+imagefile.substring(imagefile.lastIndexOf("."));
					Syngoviaimage new_img=new Syngoviaimage();
					new_img.setPatientid(img.getPatientid());
					new_img.setStudyinsuid(img.getStudyinsuid());
					new_img.setStudyid(studyid);
					new_img.setImageid(img.getImageid());
					new_img.setImagefile(newImageFile);
					new_img.setCreatetime(new Date());
					new_img.remove("id").save();
					File newfile = new File(PropKit.use("system.properties").get("sysdir")+File.separator+newImageFile);
					try {
						FileUtils.copyFile(oldfile, newfile);
					} catch (IOException e) {
						ret=false;
						e.printStackTrace();
					}
				}
			}
		}
		return ret;
	}

	public List<Syngoviaimage> getReportImagesHistory(String studyid, String patientid) {
		return Syngoviaimage.dao.find("select * from syngoviaimage a where studyid!=? and studyid is not null and patientid=?",studyid,patientid);
	}
	
	public Record findReportInfo(Integer orderid) {
		Record record=Db.findFirst(" select top 1 report.*,report.id AS reportid, (CASE WHEN report.reportstatus IS NULL THEN 20 ELSE report.reportstatus END) AS reportstatusdisplaycode, patient.patientname,patient.patientid,patient.birthdate,(CASE WHEN patient.sex='M' THEN '男' ELSE '女' END) as sex,admission.age " + 
				" from  studyorder LEFT JOIN report ON studyorder.id=report.studyorderfk" + 
				" LEFT JOIN patient ON studyorder.patientidfk=patient.id " + 
				" LEFT JOIN admission ON studyorder.admissionidfk=admission.id " + 
				"WHERE  studyorder.id = ?",orderid);
		return record;
	}
}
