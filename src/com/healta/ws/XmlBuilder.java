package com.healta.ws;


import java.util.Date;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.util.Base64;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.healta.model.Admission;
import com.healta.model.DicEmployee;
import com.healta.model.Eorder;
import com.healta.model.Patient;
import com.healta.model.Report;
import com.healta.model.Study;
import com.healta.model.Studyorder;

import com.healta.util.DateUtil;

import com.healta.util.FileUtil;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

public class XmlBuilder {

	private static Logger log = Logger.getLogger(XmlBuilder.class);
	/**
	 *  医院原先的确费接口
	 * @param requestNo 申请单号
	 * @param patientid 住院号 --不带ZY
	 * @param inOrOut 确费类别  --1门诊 2住院
	 * @param reMrak 操作类别  --1人工操作 ，自助确费不填
	 * @return
	 */
	public static String feeConfirm(Studyorder studyorder,Admission adm, String reMrak) {
		
		String patientno=studyorder.getPatientid();
		String type="1";
		if("I".equals(adm.getPatientsource())) {
			patientno=adm.getInno();
			type="2";
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"utf-16\"?><Request>");
		buffer.append("<CombNo>").append(studyorder.getEorderid()).append("</CombNo>");
		buffer.append("<PatientNo>").append(patientno).append("</PatientNo>");
		buffer.append("<Type>").append(type).append("</Type>");
		if (StrKit.notBlank(reMrak)) {
			buffer.append("<ReMrak>").append(reMrak).append("</ReMrak>");
		}
		buffer.append("</Request>");
		return buffer.toString();
	}

	
	/**
	 * 叫号报到
	 * @param studyorder
	 * @return
	 */
	public static String callNumber(Studyorder studyorder) {
		StringBuffer xml = new StringBuffer();
//		if (StringUtils.isNotBlank(studyorder.getCallNumberId())) {
//			xml.append("<data><queue_id>").append(DateUtil.toShortStr(new Date()) + "-" + studyorder.getCallNumberId()).append("</queue_id></data>");
//		} else {
//			log.info(studyorder.getStudyid() + "叫号唯一号为空");
//		}
		xml.append("<data><queue_id>").append(DateUtil.toShortStr(new Date()) + "-" + studyorder.getSequencenumber()).append("</queue_id></data>");
		return xml.toString();
	}
	

	
	/**
	 *  注册检查报告
	 * @param patient
	 * @param admission
	 * @param studyorder
	 * @param report
	 * @param record
	 */
	public static String registerReport(Patient patient, Admission admission, Studyorder studyorder, Report report, Record record, Eorder eorder) {
		Document inputDoc = null;
		try {
			inputDoc = new SAXReader().read(new File(JFinal.me().getServletContext().getRealPath("upload/webservice/registerReport.xml")));
			Element root = inputDoc.getRootElement();
			root.element("creationTime").attribute("value").setText(DateUtil.dtsHL7Date(new Date()));
			Element clinicalDocument = root.element("controlActProcess").element("subject").element("clinicalDocument");
			// 文档、报告流水号
			clinicalDocument.element("id").element("item").attribute("extension").setText(report.getId() + "");
			// 文档生成日期时间
			clinicalDocument.element("effectiveTime").attribute("value").setText(DateUtil.toShortStr(new Date()));
			// 经base64编码的文档原始内容
			String path = PropKit.use("system.properties").get("sysdir") + "\\" + "SDA" + "\\" + "SDA.xml";
			clinicalDocument.element("storageCode").element("originalText").setText(encodeBase64File(getSDA(patient, admission, studyorder, report, record, eorder), path));
			
			Element patientElement = clinicalDocument.element("recordTarget").element("patient");
			List<Element> items = patientElement.element("id").elements();
			for (Element item : items) {
				String code = item.attributeValue("root");
				if ("2.16.156.10011.2.5.1.4".equals(code)) {
					// PatientID
					item.attribute("extension").setText(patient.getPatientid());
				} else if ("2.16.156.10011.1.12".equals(code)) {
					// 住院号标识
					item.attribute("extension").setText(admission.getInno());
				} else if ("2.16.156.10011.1.11".equals(code)) {
					// 门诊号标识
					item.attribute("extension").setText(admission.getOutno());
				} else if ("2.16.156.10011.0.9.1.55".equals(code)) {
					// 患者诊疗号
					item.attribute("extension").setText(admission.getAdmissionid());
				}
			}
			// 患者就诊日期时间
			patientElement.element("effectiveTime").element("low").attribute("value").setText(DateUtil.toShortStr(studyorder.getRegdatetime()));
			// 身份证号
			patientElement.element("patientPerson").element("id").element("item").attribute("extension").setText(patient.getIdnumber());
			// 姓名
			patientElement.element("patientPerson").element("name").element("item").element("part").attribute("value").setText(patient.getPatientname());
			// 出生日期
			patientElement.element("patientPerson").element("birthTime").attribute("value").setText(patient.getBirthdate().replaceAll("-", ""));
			// 科室代码
			patientElement.element("providerOrganization").element("organizationContains").element("id").element("item").attribute("extension").setText(eorder.getAppdeptcode() + "");
			
			// 文档创建者 TODO 自动登记怎么算？
			clinicalDocument.element("author").element("assignedAuthor").element("id").element("item").attribute("extension").setText("");
			clinicalDocument.element("author").element("assignedAuthor").element("assignedPerson").element("name").element("item").element("part").attribute("value").setText(studyorder.getCreatorname());
			
			// 检查申请单编号
			clinicalDocument.element("inFulfillmentOf").element("order").element("id").element("item").attribute("extension").setText(studyorder.getEorderid());
			// 检查流水号
			clinicalDocument.element("documentationOf").element("serviceEvent").element("id").element("item").attribute("extension").setText(studyorder.getStudyid());
			// 就诊流水号
			clinicalDocument.element("componentOf").element("encompassingEncounter").element("id").element("item").attribute("extension").setText(admission.getAdmissionid());
			// 就诊类别名称
			if ("O".equals(admission.getPatientsource())) {
				// 门诊
				clinicalDocument.element("componentOf").element("encompassingEncounter").element("code").attribute("code").setText("01");
				clinicalDocument.element("componentOf").element("encompassingEncounter").element("code").element("displayName").attribute("value").setText("门诊");
			} else if ("I".equals(admission.getPatientsource())) {
				// 住院
				clinicalDocument.element("componentOf").element("encompassingEncounter").element("code").attribute("code").setText("03");
				clinicalDocument.element("componentOf").element("encompassingEncounter").element("code").element("displayName").attribute("value").setText("住院");
			} else if ("E".equals(admission.getPatientsource())) {
				// 急诊
				clinicalDocument.element("componentOf").element("encompassingEncounter").element("code").attribute("code").setText("02");
				clinicalDocument.element("componentOf").element("encompassingEncounter").element("code").element("displayName").attribute("value").setText("急诊");
			} else if ("P".equals(admission.getPatientsource())) {
				// 体检
				clinicalDocument.element("componentOf").element("encompassingEncounter").element("code").attribute("code").setText("04");
				clinicalDocument.element("componentOf").element("encompassingEncounter").element("code").element("displayName").attribute("value").setText("体检");
			} else {
				clinicalDocument.element("componentOf").element("encompassingEncounter").element("code").attribute("code").setText("09");
				clinicalDocument.element("componentOf").element("encompassingEncounter").element("code").element("displayName").attribute("value").setText("其他");
			}
			
			// 查看生成的xml
			encodeBase64File(inputDoc.asXML(), PropKit.use("system.properties").get("sysdir") + "\\" + "SDA" + "\\" + "createReport.xml");
		} catch (DocumentException e) {
			e.printStackTrace();
			log.info(e.getMessage());
		} catch (ParseException e) {
			e.printStackTrace();
			log.info(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}	
		return inputDoc.asXML();
	}
	/**
	 *  注册报告事件
	 * @param patient
	 * @param admission
	 * @param studyorder
	 * @param report
	 * @param record
	 */
	public static String registerReportEvent(Patient patient, Admission admission, Studyorder studyorder, Report report, Record record, Eorder eorder) {
		Document inputDoc = null;
		try {
			inputDoc = new SAXReader().read(new File(JFinal.me().getServletContext().getRealPath("upload/webservice/registerReportEvent.xml")));
//			inputDoc = new SAXReader().read(new File(PropKit.use("system.properties").get("sysdir") + "\\" + "webservice" + "\\" + "registerReportEvent.xml"));
			Element root = inputDoc.getRootElement();
			root.element("creationTime").attribute("value").setText(DateUtil.dtsHL7Date(new Date()));
			Element obs = root.element("controlActProcess").element("subject").element("placerGroup").element("component2").element("observationRequest");
			List<Element> items = obs.element("id").elements();
			for (Element item : items) {
				String code = item.attributeValue("root");
				if ("2.16.156.10011.1.24".equals(code)) {
					// 申请单号
					item.attribute("extension").setText(studyorder.getEorderid());
				} else if ("2.16.156.10011.0.9.1.62".equals(code)) {
					// 条码号 (条码号是打出来的条码)
					item.attribute("extension").setText("");
				} else if ("2.16.156.10011.0.9.1.65".equals(code)) {
					// 业务号 (业务号门诊传医嘱号，住院传执行单号)
					item.attribute("extension").setText("");
				}
			}
			// 操作日期
			obs.element("performer").element("time").element("low").attribute("value").setText(DateUtil.dtsyyyyMMddHHmm(report.getAudittime()));
			// 操作人工号 
			DicEmployee employee = DicEmployee.dao.findFirst("select dic_employee.* from dic_employee where id = (select top 1 users.employeefk from users where users.id = ?)", report.getAuditphysician());
			obs.element("performer").element("assignedEntity").element("id").element("item").attribute("extension").setText(employee.getJobnumber());
			// 操作人姓名
			obs.element("performer").element("assignedEntity").element("assignedPerson").element("name").element("item").element("part").attribute("value").setText(report.getAuditphysicianName());
			// 执行科室编码 
			obs.element("location").element("serviceDeliveryLocation").element("serviceProviderOrganization").element("id").element("item").attribute("extension").setText(eorder.getAppdeptcode() + "");
			// 执行科室名称
			obs.element("location").element("serviceDeliveryLocation").element("serviceProviderOrganization").element("name").element("item").element("part").attribute("value").setText(eorder.getAppdeptname() + "");
			
			List<Element> elements = obs.elements();
			for (Element component1:elements) {
//				log.info(component1.getName());
				if ("component1".equals(component1.getName())) {
					String code = component1.element("processStep").element("code").attributeValue("codeSystem");
					if ("2.16.156.10011.0.9.2.3.2.115".equals(code)) {
						// 报告文档
						// 报告编号
						component1.element("processStep").element("id").element("item").attribute("extension").setText(report.getId() + "");
					} else if ("2.16.156.10011.0.9.2.3.2.105".equals(code)) {
						// 事件状态 (确费)
						// 事件时间 (确费时间)
						component1.element("processStep").element("effectiveTime").element("any").attribute("value").setText(DateUtil.dtsyyyyMMddHHmm(studyorder.getRegdatetime()));
					}
				}
			}
			
			// 就诊信息
			Element encounter = root.element("controlActProcess").element("subject").element("placerGroup").element("componentOf1").element("encounter");
			List<Element> ids = encounter.element("id").elements();
			for (Element id : ids) {
				String code = id.attributeValue("root");
				if ("2.16.156.10011.2.5.1.8".equals(code)) {
					// 就诊次数
					//id.attribute("extension").setText(hisApply.getVisitTimes() + "");
				} else if ("2.16.156.10011.2.5.1.9".equals(code)) {
					// 就诊流水号
					id.attribute("extension").setText(admission.getAdmissionid());
				}
			}
			// 就诊类别编码（门诊02，急诊01，住院06，体检，04，其他09）
			if ("O".equals(admission.getPatientsource())) {
				encounter.element("code").attribute("code").setText("01");
				encounter.element("code").element("displayName").attribute("value").setText("门诊");
			} else if ("E".equals(admission.getPatientsource())) {
				encounter.element("code").attribute("code").setText("02");
				encounter.element("code").element("displayName").attribute("value").setText("急诊");
			} else if ("I".equals(admission.getPatientsource())) {
				encounter.element("code").attribute("code").setText("03");
				encounter.element("code").element("displayName").attribute("value").setText("住院");
			} else if ("P".equals(admission.getPatientsource())) {
				// 体检
				encounter.element("code").attribute("code").setText("04");
				encounter.element("code").element("displayName").attribute("value").setText("体检");
			} else {
				encounter.element("code").attribute("code").setText("09");
				encounter.element("code").element("displayName").attribute("value").setText("其他");
			}
			
			// 病人信息
			List<Element> lists = encounter.element("subject").element("patient").element("id").elements();
			for (Element list : lists) {
				String code = list.attributeValue("root");
				if ("2.16.156.10011.2.5.1.5".equals(code)) {
					// 域ID
					//list.attribute("extension").setText(hisApply.getEmpiId() + "");
				} else if ("2.16.156.10011.2.5.1.5".equals(code)) {
					// 患者ID
					list.attribute("extension").setText(patient.getPatientid());
				} else if ("2.16.156.10011.2.5.1.5".equals(code)) {
					// 门诊号
					list.attribute("extension").setText(admission.getOutno());
				}  else if ("2.16.156.10011.2.5.1.5".equals(code)) {
					// 住院号
					list.attribute("extension").setText(admission.getInno());
				}  else if ("2.16.156.10011.2.5.1.5".equals(code)) {
					// 诊疗号
					list.attribute("extension").setText(admission.getAdmissionid());
				}
			}
			// 患者姓名 
			encounter.element("subject").element("patient").element("patientPerson").element("name").element("item").element("part").attribute("value").setText(patient.getPatientname());
			
			Element sdl = encounter.element("location").element("serviceDeliveryLocation");
			// 病床编码
			sdl.element("location").element("id").element("item").attribute("extension").setText(eorder.getBedno() + "");
			// 病床号
			sdl.element("location").element("name").element("item").element("part").attribute("value").setText(admission.getBedno());
			// 病房编码
			//sdl.element("location").element("asLocatedEntityPartOf").element("location").element("id").element("item").attribute("extension").setText(hisApply.getRoomNoCode() + "");
			// 病房号
			//sdl.element("location").element("asLocatedEntityPartOf").element("location").element("name").element("item").element("part").attribute("value").setText(hisApply.getRoomNo() + "");
			// 科室编码
			sdl.element("serviceProviderOrganization").element("id").element("item").attribute("extension").setText(eorder.getAppdeptcode() + "");
			// 科室名称
			sdl.element("serviceProviderOrganization").element("name").element("item").element("part").attribute("value").setText(eorder.getAppdeptname()+ "");
			// 病区编码
			sdl.element("serviceProviderOrganization").element("asOrganizationPartOf").element("wholeOrganization").element("id").element("item").attribute("extension").setText(eorder.getWardno() + "");
			// 病区名称
			sdl.element("serviceProviderOrganization").element("asOrganizationPartOf").element("wholeOrganization").element("name").element("item").element("part").attribute("value").setText(admission.getWardno() + "");
			
			// 查看生成的xml
			encodeBase64File(inputDoc.asXML(), PropKit.use("system.properties").get("sysdir") + "\\" + "SDA" + "\\" + "createReportEvent.xml");
		} catch (DocumentException e) {
			e.printStackTrace();
			log.info(e.getMessage());
		} catch (ParseException e) {
			e.printStackTrace();
			log.info(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return inputDoc.asXML();
	}
	
	
	
	
	
	
	
	/**
	 *  获取SDA报告
	 * @param patient
	 * @param admission
	 * @param studyorder
	 * @param report
	 * @param record
	 * @return
	 */
	public static String getSDA(Patient patient, Admission admission, Studyorder studyorder, Report report, Record record, Eorder eorder) {
		Document inputDoc = null;
		try {
			inputDoc = new SAXReader().read(new File(JFinal.me().getServletContext().getRealPath("upload/webservice/SDA.xml")));
//			inputDoc = new SAXReader().read(new File(PropKit.use("system.properties").get("sysdir") + "\\" + "webservice" + "\\" + "SDA.xml"));
			Element root = inputDoc.getRootElement();
			log.info(root.getName());
			// 文档机器生成时间
			root.element("effectiveTime").attribute("value").setText(DateUtil.dtsHL7Date(new Date()));
			
			Element patientRole = root.element("recordTarget").element("patientRole");
			List<Element> ids = patientRole.element("id").elements();
			for (Element element : ids) {
				String code = element.attributeValue("root");
				if ("2.16.156.10011.1.11".equals(code)) {
					// 门（急）诊号标识
					element.attribute("extension").setText(admission.getOutno());
				} else if ("2.16.156.10011.1.12".equals(code)) {
					// 住院号标识
					element.attribute("extension").setText(admission.getInno());
				} else if ("2.16.156.10011.1.32".equals(code)) {
					// 检查报告单号标识
					element.attribute("extension").setText(report.getId() + "");
				} else if ("2.16.156.10011.1.24".equals(code)) {
					// 电子申请单编号
					element.attribute("extension").setText(studyorder.getEorderid());
				} else if ("2.16.156.10011.1.14".equals(code)) {
					// 标本编号标识
					element.attribute("extension").setText("");
				}
			}
			// 患者类别代码 （门诊01，急诊02，住院03，体检04，其他09）
			if ("O".equals(admission.getPatientsource())) {
				patientRole.element("patientType").element("patienttypeCode").attribute("code").setText("01");
				patientRole.element("patientType").element("patienttypeCode").attribute("displayName").setText("门诊");
			} else if ("E".equals(admission.getPatientsource())) {
				patientRole.element("patientType").element("patienttypeCode").attribute("code").setText("02");
				patientRole.element("patientType").element("patienttypeCode").attribute("displayName").setText("急诊");
			} else if ("I".equals(admission.getPatientsource())) {
				patientRole.element("patientType").element("patienttypeCode").attribute("code").setText("03");
				patientRole.element("patientType").element("patienttypeCode").attribute("displayName").setText("住院");
			} else if ("P".equals(admission.getPatientsource())) {
				patientRole.element("patientType").element("patienttypeCode").attribute("code").setText("04");
				patientRole.element("patientType").element("patienttypeCode").attribute("displayName").setText("体检");
			} else {
				patientRole.element("patientType").element("patienttypeCode").attribute("code").setText("9");
				patientRole.element("patientType").element("patienttypeCode").attribute("displayName").setText("其他");
			}
			
			// 手机号
			patientRole.element("telecom").attribute("value").setText(patient.getTelephone());
			// 患者身份证号标识
			patientRole.element("patient").element("id").attribute("extension").setText(patient.getIdnumber());
			patientRole.element("patient").element("name").setText(patient.getPatientname());
			// 性别 男1，女2，未知0
			if ("M".equals(patient.getSex())) {
				patientRole.element("patient").element("administrativeGenderCode").attribute("code").setText("1");
				patientRole.element("patient").element("administrativeGenderCode").attribute("displayName").setText("男性");
			} else if ("F".equals(patient.getSex())) {
				patientRole.element("patient").element("administrativeGenderCode").attribute("code").setText("2");
				patientRole.element("patient").element("administrativeGenderCode").attribute("displayName").setText("女性");
			} else {
				patientRole.element("patient").element("administrativeGenderCode").attribute("code").setText("0");
				patientRole.element("patient").element("administrativeGenderCode").attribute("displayName").setText("其他");
			}
			//  患者出生日期
			patientRole.element("patient").element("birthTime").attribute("value").setText(patient.getBirthdate().replaceAll("-", ""));
			// 年龄
			if ("Y".equals(admission.getAgeunit())) {
				patientRole.element("patient").element("age").attribute("unit").setText("岁");
			} else if ("M".equals(admission.getAgeunit())) {
				patientRole.element("patient").element("age").attribute("unit").setText("月");
			} else if ("D".equals(admission.getAgeunit())) {
				patientRole.element("patient").element("age").attribute("unit").setText("天");
			} else if ("MIN".equals(admission.getAgeunit())) {
				patientRole.element("patient").element("age").attribute("unit").setText("分钟");
			}
			patientRole.element("patient").element("age").attribute("value").setText(admission.getAge() + "");
			
			// 检查报告医师（文档创作者）
			// 检查报告日期
			root.element("author").element("time").attribute("value").setText(DateUtil.toShortStr(report.getReporttime()));
			// 报告医师姓名
			root.element("author").element("assignedAuthor").element("assignedPerson").element("name").setText(report.getReportphysicianName());
			// 审核医生姓名
			root.element("legalAuthenticator").element("assignedEntity").element("assignedPerson").element("name").setText(report.getAuditphysicianName());
			
			Study study = Study.dao.findFirst("select * from study where studyorderfk = ?", studyorder.getId());
			if (study != null) {
				//  检查技师 
				root.element("authenticator").element("assignedEntity").element("assignedPerson").element("name").setText(study.getTechnician() + "");
				//  检查医师
				root.element("authenticator").element("assignedEntity").element("assignedPerson").element("name").setText(study.getPhysician() + "");
			} else {
				// 检查医师，检查技师 没有
				root.element("authenticator").element("assignedEntity").element("assignedPerson").element("name").setText(report.getReportphysicianName());
				root.element("authenticator").element("assignedEntity").element("assignedPerson").element("name").setText("");
			}
			
			// 申请科室
			String appdeptcode = "";
			if (StringUtils.isNotBlank(studyorder.getAppdeptcode())) {
				appdeptcode = studyorder.getAppdeptcode();
			}
			root.element("participant").element("associatedEntity").element("scopingOrganization").element("id").attribute("extension").setText(appdeptcode + "");
			String appdeptname = "";
			if (StringUtils.isNotBlank(studyorder.getAppdeptname())) {
				appdeptname = studyorder.getAppdeptname();
			}
			root.element("participant").element("associatedEntity").element("scopingOrganization").element("name").setText(appdeptname + "");
			// 申请机构
//			root.element("participant").element("associatedEntity").element("scopingOrganization").element("asOrganizationPartOf").element("wholeOrganization").element("name").setText(studyorder.getAppdeptname());
			// 检查流水号
			root.element("documentationOf").element("serviceEvent").element("id").attribute("extension").setText(studyorder.getStudyid());
			
			// 检查报告pdf地址
			String reportPdfPath = PropKit.use("system.properties").get("report_pdf_address") + report.getRelativePath();
			log.info("报告图像的地址：" + reportPdfPath);
			root.element("relatedDocument").element("parentDocument").element("text").element("reference").attribute("value").setText(reportPdfPath);
			// 病床号、病房、病区、科室和医院的关联
			Element wo = root.element("componentOf").element("encompassingEncounter").element("location").element("healthCareFacility").element("serviceProviderOrganization").element("asOrganizationPartOf").element("wholeOrganization");
			// 病床号
			wo.element("id").attribute("extension").setText(admission.getBedno() + "");
			// 病房号
			wo.element("asOrganizationPartOf").element("wholeOrganization").element("id").attribute("extension").setText(eorder.getWardno() + "");
			DicEmployee employee = DicEmployee.dao.findFirst("select dic_employee.* from dic_employee where id = (select top 1 users.employeefk from users where users.id = ?)", report.getAuditphysician());
			// 科室名称 （执行科室）
			wo.element("asOrganizationPartOf").element("wholeOrganization").element("asOrganizationPartOf").element("wholeOrganization").element("name").setText(employee.getDeptname() + "");
			// 病区名称
			wo.element("asOrganizationPartOf").element("wholeOrganization").element("asOrganizationPartOf").element("wholeOrganization").element("asOrganizationPartOf").element("wholeOrganization").element("name").setText(admission.getWardno() + "");
			
			// 文档体Body
			List<Element> components = root.element("component").element("structuredBody").elements();
			for (Element component : components) {
				String displayName = component.element("section").element("code").attributeValue("displayName");
				if ("Diagnosis".equals(displayName)) {
					// 诊断记录章节
					// icd-10 code
					//component.element("section").element("entry").element("observation").element("value").attribute("code").setText(hisApply.getIcd10());
					//component.element("section").element("entry").element("observation").element("value").attribute("displayName").setText(hisApply.getDiseaseName());
				} else if ("PROBLEM LIST".equals(displayName)) {
					// 症状章节
				} else if ("其他处置章节".equals(displayName)) {
					// 其他处置章节
				} else if ("检查报告".equals(displayName)) {
					// 检查报告章节
					List<Element> entrys = component.element("section").elements();
					for (int i = 1; i < entrys.size(); i++) {
						Element entry = entrys.get(i);
						String code = entry.element("observation").element("code").attributeValue("code");
						if ("DE04.50.131.00".equals(code)) {
							// 检查所见
							entry.element("observation").element("value").setText("<![CDATA[" + report.getCheckdescTxt()  + "]]");
						} else if ("DE04.50.132.00".equals(code)) {
							// 检查结果
							entry.element("observation").element("value").setText("<![CDATA[" + report.getCheckresultTxt()  + "]]");
						} else if ("DE08.10.026.00".equals(code)) {
							// 检查报告科室
							entry.element("observation").element("value").setText(eorder.getAppdeptname() + "");
						} else if ("DE06.00.179.00".equals(code)) {
							// 报告备注
							entry.element("observation").element("value").setText(record.getStr("note")+"");
						}
					}
				} else if ("报告内容关联章节".equals(displayName)) {
					// 报告内容关联章节
					// 申请项目
					component.element("section").element("text").element("content").setText(studyorder.getStudyitems());
					// 报告图像位置 url
					component.element("section").element("text").element("linkHtml").attribute("href").setText(reportPdfPath);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			log.info(e.getMessage());
		} catch (ParseException e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return inputDoc.asXML();
	}
	/**
     * 将文件转成base64 字符串
     *
     * @param xml SDA报告
     * @return *
     * @throws Exception
     */
    public static String encodeBase64File(String xml, String path) throws Exception {
    	FileUtil.createXMLFile(xml, path);
    	return new String(Base64.encodeBase64(FileUtils.readFileToByteArray(new File(path))), "utf-8");
    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Document doc=DocumentFactory.getInstance().createDocument();
		Element root=doc.addElement("root");
		Element t=root.addElement("test");
		t.addText("d<=＝你好你好>d");
		
		System.out.print(doc.asXML());
		
	}

}
