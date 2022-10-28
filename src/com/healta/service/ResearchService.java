package com.healta.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.healta.constant.*;
import com.healta.model.*;
import com.healta.plugin.activemq.ActiveMQ;
import com.healta.plugin.activemq.ESIndexDocOrder;
import com.healta.plugin.activemq.MQSubject;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.healta.util.DateUtil;
import com.healta.util.ExcelUtil;
import com.healta.util.IDUtil;
import com.healta.util.ParaKit;
import com.healta.util.ResultUtil;
import com.healta.util.SerializeRes;
import com.healta.vo.ResultVO;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class ResearchService {
	private static final Logger log = Logger.getLogger(ExamineService.class);
	
	public boolean deleteProject(Integer Id) {
		return  Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			try {
				Date now=new Date();
				//删除课题下所有的实验组信息
				List<ResearchTestGroup> eg=ResearchTestGroup.dao.find("select * from research_test_group where project_id="+Id+"");
				for (ResearchTestGroup experienceGroup : eg) {
					//删除实验组下所有表单信息
					List<ResearchTestgroupData> rtfs=ResearchTestgroupData.dao.find("select * from exgroup_taskform_relation where exGroupId="+experienceGroup.getId()+"");
					for (ResearchTestgroupData rtf : rtfs) {
						rtf.deleteById(rtf.getId());
					}
					experienceGroup.setDeleted("1");
					experienceGroup.setUpdatetime(now);
					experienceGroup.update();
				}
				
				//删除课题下相关成员
				Db.delete("delete research_user_role where project_id="+Id);
				
				//删除课题下的入组申请
				Db.delete("delete research_joingroup_apply where project_id="+Id);
				ResearchProject tm=ResearchProject.dao.findById(Id);
				tm.setDeleted("1");
				tm.setUpdatetime(now);
				tm.update();
			} catch (Exception e) {
				e.printStackTrace();
				ret=false;
			}
			return ret;
 		});
	}
	
	public List<Record> findTestGroups(Integer projectid){
		return Db.find("select tg.*,u.name as creatorname,(select count(*) from research_testgroup_data where research_testgroup_data.group_id=tg.id) as all_count,"
				+ "(select count(*) from research_testgroup_data where research_testgroup_data.group_id=tg.id and research_testgroup_data.status=?) as submit_count, "
				+ "(select count(*) from research_testgroup_data where research_testgroup_data.group_id=tg.id and research_testgroup_data.status=?) as final_count "
				+ " from research_test_group tg left join users u on tg.creator=u.id "
    	 		+ "where tg.deleted!='1' and tg.project_id=?",FormStatus.Preliminary,FormStatus.FinalResults,projectid);
	}
	
	public Record getTestGroups(Integer groupid){
		return Db.findFirst("select tg.*,u.name as creatorname,(select count(*) from research_testgroup_data where research_testgroup_data.group_id=tg.id) as all_count,"
				+ "(select count(*) from research_testgroup_data where research_testgroup_data.group_id=tg.id and research_testgroup_data.status=?) as submit_count, "
				+ "(select count(*) from research_testgroup_data where research_testgroup_data.group_id=tg.id and research_testgroup_data.status=?) as final_count "
				+ " from research_test_group tg left join users u on tg.creator=u.id "
    	 		+ "where tg.deleted!='1' and tg.id=?",FormStatus.Preliminary,FormStatus.FinalResults,groupid);
	}

	public boolean deleteTestGroup(Integer id) {
		return  Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->  {
			boolean ret=true;
			try {
				Db.delete("delete from research_testgroup_data where group_id=?",id);
				Db.delete("delete from research_test_group where id=?",id);
			} catch(Exception e) {
				ret=false;
				e.printStackTrace();
			}
			return ret;
 		});
	}

	public boolean saveProjectRole(String rows, String name, String describe, Integer taskRoleId,int level) {
		boolean succeed=Db.tx(new IAtom() {
			public boolean  run() {
				boolean ret=true;
				Date now=new Date();
		 		ResearchRole tr=new ResearchRole();
		 		if(taskRoleId==null) {
		 			tr.setRolename(name);
		 			tr.setDescription(describe);
		 			tr.setCreatetime(now);
					 tr.setUserLevel(level);
		 			tr.remove("id").save();
		 			
		 			JSONArray jsonArray = JSONArray.parseArray(rows);
		 			for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject obj=jsonArray.getJSONObject(i);
						ResearchRoleAuthority trjr=new ResearchRoleAuthority();
						trjr.setRoleId(tr.getId());
						trjr.setAuthorityId(obj.getInteger("id"));
						trjr.remove("id").save();
					}
		 		}else {
		 			tr.setId(taskRoleId);
		 			tr.setRolename(name);
		 			tr.setDescription(describe);
					tr.setUserLevel(level);
		 			tr.update();
		 			
		 			Db.delete("delete research_role_authority where role_id="+taskRoleId);
		 			JSONArray jsonArray = JSONArray.parseArray(rows);	
		 			for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject obj=jsonArray.getJSONObject(i);	
						ResearchRoleAuthority trjr=new ResearchRoleAuthority();
						trjr.setRoleId(tr.getId());
						trjr.setAuthorityId(obj.getInteger("id"));
						trjr.remove("id").save();
					}
		 		}
				return ret;
			}
		});
		return succeed;
	}

	public boolean deleteProjectRole(Integer id) {
		boolean succeed=Db.tx(new IAtom() {
			public boolean  run() {
				boolean ret=true;
				ResearchRole tr=new ResearchRole();
				tr.deleteById(id);
				Db.delete("delete task_role_personnel where roleId="+id);
				return ret;
			}
		});
		return succeed;
	}

	public int newResearchForm(ResearchForm st,SerializeRes res){
		if(st.dao.queryByName(st.getName()) != null) {
			throw new RuntimeException(res.get("admin.templatenameexist"));
		}
		st.setUid(StrKit.getRandomUUID());
		st.save();
		return st.getId();
	}

	public boolean updateResearchForm(ResearchForm templet, SerializeRes res) {
		if(templet.dao.queryByNameAndNotEquSelf(templet.getName(),templet.getId()) != null) {
			throw new RuntimeException(res.get("admin.templatenameexist"));
		}
		
		return templet.update();
	}

	public List<Record> findResearchForm(String name,boolean withContent){
		List<Record> ret=null;
		String columns=ResearchForm.dao.toSelectStr(withContent?"":"formcontent,datamodel");
		if(StrKit.notBlank(name)){
			ret = Db.find("select top 200 "+columns+" from research_form where name like CONCAT('%',?,'%') order by createtime desc",name);
		}
		else{
			ret = Db.find("select top 200 "+columns+" from research_form order by createtime desc");
		}
		ret.stream().forEach(x->{
			try {
				x.set("formname", Base64.encodeBase64String(x.getStr("name").getBytes("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		return ret;
		
	}

//	public ResearchTestgroupData submitTaskForm(String params, String html,Integer experience_group_id,Integer formId, String formName, Integer type,User user,Integer id) {
//		ResearchTestgroupData forminfo=new ResearchTestgroupData();
//		if(id!=null) {
//			forminfo=ResearchTestgroupData.dao.findById(id);
//			forminfo.setHtml(html);
//			forminfo.update();
//		}else {
//			forminfo.setType(type);
//			forminfo.setFormId(formId);
//			forminfo.setFormName(formName);
//			forminfo.setGroupId(experience_group_id);
//			forminfo.setCreatetime(new Date());
//			forminfo.setCreator(user.getId());
//			forminfo.setHtml(html);
//			forminfo.remove("id").save();
//		}
//		Db.update("delete from structform where formid=?",formId);
//		List<JSONObject> paramList = JSON.parseArray(params, JSONObject.class);
//		List<ResearchFormData> srs=new ArrayList<ResearchFormData>();
//		for(JSONObject obj:paramList){
//			ResearchFormData sr=new ResearchFormData();
//			sr.setDataid(formId);
//			sr.setCode(obj.getString("code"));
//			sr.setOptioncode( obj.getString("optioncode"));
//			sr.setValue( obj.getString("value"));
//			sr.setUnit( obj.getString("unit"));
//			sr.setGrp(obj.getString("group"));
//			srs.add(sr);
//		}
//		Db.batchSave(srs,srs.size());
//		
//		return forminfo;
//		
//	}
	/*
	 * 检查病人id相同，但姓名不同的情况
	 * */
	public ResultVO checkPatientInfo(String info,Integer dataid) {
		JSONObject object=JSONObject.parseObject(info);
		String patientid=object.getString("patientid");
		String patientname=object.getString("patientname");
		return checkPatientInfo(patientid,patientname,dataid);
	}
	
	public ResultVO checkPatientInfo(String patientid,String patientname,Integer dataid) {
		if(StrKit.notBlank(patientid)) {
			List<Object> paras = new ArrayList<>();
			paras.add(patientid);
			String where="";
			if(dataid!=null) {
				where+=" and id!=?";
				paras.add(dataid);
			}
			List<ResearchTestgroupData> list= ResearchTestgroupData.dao.find("select * from research_testgroup_data where patientid=? "+where,paras.toArray());
			for(ResearchTestgroupData data:list) {
				if(!StrKit.equals(patientname, data.getPatientname())) {
					return ResultUtil.fail(-2,"病人编号相同，但病人姓名不同，请检查！");
				}
			}
		}
		return ResultUtil.success();
	}
	
	public boolean submitFormData(ResearchTestgroupData data,String structdata,String html,User user,String ctx,String info){
		JSONObject object=JSONObject.parseObject(info);
		String patientid=object.getString("patientid");
		if(StrKit.isBlank(patientid)) {
			patientid=IDUtil.getPatientID_Research();
			object.put("patientid",patientid);
		}
		
		Date now=new Date();
		html=html.replaceAll("\\[ReportPhysicianName\\]", user.getName()).replaceAll("\\[ReportDateTime\\]", DateUtil.dtsWithTime(now));
		String temp_html=html.replaceAll(ctx+"/image/image", "/image/image");
		data.setHtml(temp_html);
		
		boolean succeed=Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
				boolean re=true;
				if(data.getId()==null){
					if(data.getFormId()!=null){
						ResearchForm form=ResearchForm.dao.findById(data.getFormId());
						data.setFormName(form.getName());
					}
					data.setStatus(FormStatus.Preliminary);
					data.setType(1);
					data.setCreator(user.getId());
					data.setPatientname(object.getString("patientname"));
					data.setPatientid(object.getString("patientid"));
					data.setSex(object.getString("sex"));
					data.setStudyid(object.getString("studyid"));
					data.setModalityType(object.getString("modality_type"));
					data.setStudyitems(object.getString("studyitems"));
					data.setAge(object.getInteger("age"));
					data.setAgeUnit(object.getString("age_unit"));
					data.setBirthdate(object.getString("birthdate"));
					data.setStudyDatetime(object.getDate("study_datetime"));
					data.remove("id").save();
				} else{
					data.setPatientname(object.getString("patientname"));
					data.setPatientid(object.getString("patientid"));
					data.setSex(object.getString("sex"));
					data.setStudyid(object.getString("studyid"));
					data.setModalityType(object.getString("modality_type"));
					data.setStudyitems(object.getString("studyitems"));
					data.setAge(object.getInteger("age"));
					data.setAgeUnit(object.getString("age_unit"));
					data.setBirthdate(object.getString("birthdate"));
					data.setStudyDatetime(object.getDate("study_datetime"));
					data.update();
				}
//				Report report2 = Report.dao.findById(data.getId());
//				if(StringUtils.equals(report2.getReportstatus(), ReportStatus.Noresult)) {
//					//报告状态是未写时，更新为已创建
//					data.setStatus(ReportStatus.Created);
//				}
				
				//Db.update("INSERT INTO reportrace SELECT *, operator = "+user.getId()+" FROM report where report.id=?", data.getId());
				Db.update("delete from research_form_data where dataid=?", data.getId());
				List<JSONObject> paramList = JSON.parseArray(structdata, JSONObject.class);
				List<ResearchFormData> srs=new ArrayList<ResearchFormData>();
				for(JSONObject obj:paramList){
					ResearchFormData sr=new ResearchFormData();
					sr.setDataid(data.getId());
					sr.setCode(obj.getString("code"));
					sr.setOptioncode( obj.getString("optioncode"));
					sr.setValue( obj.getString("value"));
					sr.setUnit( obj.getString("unit"));
					sr.setGrp(obj.getString("group"));
					sr.setPath(obj.getString("path"));
					srs.add(sr);
				}
				Db.batchSave(srs,srs.size());
				return re;
		});
		data.setHtml(html);
		data.setStatus(ResearchTestgroupData.dao.findById(data.getId()).getStatus());
		return succeed;
	}

//	public void saveFormIfon_html(Integer id, String printhtml) {
//		ResearchTestgroupData fi=new ResearchTestgroupData();
//		fi.setId(id);
//		fi.setHtml(printhtml);
//		fi.update();
//		
//	}
	
	public void saveForm_printhtml(Integer id,String printhtml){
		ResearchTestgroupData data=new ResearchTestgroupData();
		data.setId(id);
		data.setPrintHtml(printhtml);
		data.update();
	}
	
	public boolean deleteFormData(Integer dataid){
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			boolean ret=true;
			Db.delete("delete from research_form_data where dataid=?",dataid);
			ret=ret&&Db.deleteById("research_testgroup_data", dataid);
			return ret;
		});	
	}
	
	public boolean deleteFormDataBatch(String ids){
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			boolean ret=true;
			Db.delete("delete from research_form_data where dataid in("+ids+")");
			Db.delete("delete from research_testgroup_data where id in("+ids+")");
//			ret=ret&&ResearchTestgroupData.dao.deleteById(dataid);
			return ret;
		});
	}

	public Page<Record> searchReport(ParaKit kit) {
		List<Object> list = new ArrayList<>();
		String where="";
		if(kit.hasPara("strTime")) {
			//报告开始时间
			if("reporttime".equals(kit.getPara("dateType"))) {
				where+=" and report.reporttime>?";
				list.add(kit.getPara("strTime"));
			}
			//登记开始时间
			if("registertime".equals(kit.getPara("dateType"))) {
				where+=" and studyorder.regdatetime>?";
				list.add(kit.getPara("strTime"));
			}
			//检查开始时间
			if("audittime".equals(kit.getPara("dateType"))) {
				where+=" and report.audittime>?";
				list.add(kit.getPara("strTime"));
			}
		}
		if(kit.hasPara("endTime")) {
			//报告结束时间
			if("reporttime".equals(kit.getPara("dateType"))) {
				where+=" and report.reporttime<?";
				list.add(kit.getPara("endTime"));
			}
			//登记结束时间
			if("registertime".equals(kit.getPara("dateType"))) {
				where+=" and studyorder.regdatetime<?";
				list.add(kit.getPara("endTime"));
			}
			//检查结束时间
			if("audittime".equals(kit.getPara("dateType"))) {
				where+=" and report.audittime<?";
				list.add(kit.getPara("endTime"));
			}
		}
		
		//模板名称
		if(kit.hasPara("templateName")) {
			where+=" and srtemplate.name LIKE CONCAT('%',?,'%')";
			list.add(kit.getPara("templateName"));
		}
		//检查项目
		if(kit.hasPara("examitemName")) {
			where+=" and report.studyitem LIKE CONCAT('%',?,'%')";
			list.add(kit.getPara("examitemName"));
		}
		
		if(kit.hasPara("doctorName")) {
			//报告医生
			if("reportDoctor".equals(kit.getPara("doctorType"))) {
				where+=" and report.reportphysician_name =?";
				list.add(kit.getPara("people"));
			}
			//审核医生
			if("examineDoctor".equals(kit.getPara("doctorType"))) {
				where+=" and report.auditphysician_name =?";
				list.add(kit.getPara("people"));  
			}
		}
		//设备类型
		if(kit.hasPara("modality_type")) {
			where+=" and studyorder.modality_type =?";
			list.add(kit.getPara("modality_type"));
		}
		StringBuffer select=new StringBuffer();
		select.append(" select");
		select.append("  report.id as report_id,"); //报告Id
		select.append("  report.studyid,	");//检查号
		select.append("  report.audittime,");//检查时间
		select.append("  report.reportphysician,");//报告医生ID
		select.append("  report.reportphysician_name,");//报告医生
		select.append("  report.auditphysician,");//审核医生ID
		select.append("  report.auditphysician_name,");//审核医生
		select.append("  report.reporttime,");//审核时间
		select.append("  syscode.name_zh as sex, ");//性别
		select.append("  srtemplate.name as template_name,");//模板名称
		select.append("  srtemplate.id as template_id,");//模板ID
		select.append("  studyorder.studyitems,");//检查项目
		select.append("  studyorder.studydescription, ");//检查描述
		select.append("  admission.age,");//年龄
		select.append("  studyorder.regdatetime,");//登记时间
		select.append("  patient.patientname,");//病人姓名
		select.append("  patient.patientid,");//病人编号
		select.append("  studyorder.modality_type");//设备类型
		StringBuffer sqlExceptSelect=new StringBuffer();
		sqlExceptSelect.append(" from report");
		sqlExceptSelect.append(" INNER JOIN studyorder on studyorder.id = report.studyorderfk");
		sqlExceptSelect.append(" INNER JOIN admission on admission.id = studyorder.admissionidfk");
		sqlExceptSelect.append(" INNER JOIN patient on patient.id = studyorder.patientidfk");
		sqlExceptSelect.append(" INNER JOIN syscode on syscode.code=patient.sex and syscode.type=0001");
		sqlExceptSelect.append(" INNER JOIN srtemplate on srtemplate.id=report.template_id");
		sqlExceptSelect.append(" where report.reportstatus=31  and report.template_id>0");
		sqlExceptSelect.append(where);
		return Db.paginate(Integer.valueOf(kit.getPara("page")), Integer.valueOf(kit.getPara("rows")), select.toString(), sqlExceptSelect.toString(), list.toArray());
	}
	
	
	public boolean applySreport2Group(Integer projectid,Integer groupid,String reports,Date validDate,User user){
		return Db.tx(() ->{
			boolean ret=true;
			if(StrKit.notBlank(reports)) {
				ResearchJoingroupApply apply=new ResearchJoingroupApply();
				apply.setProjectId(projectid);
				apply.setGroupId(groupid);
				apply.setApplicant(user.getId());
				apply.setAppDatetime(new Date());
				apply.setValidPeriod(validDate);
				apply.setStatus(Sreport2GroupStatus.apply);
				apply.remove("id").save();
				
				JSONArray array=JSONArray.parseArray(reports);
				for (int i = 0; i < array.size(); i++) {
					JSONObject object=array.getJSONObject(i);
					Report report=Report.dao.findById(object.getInteger("report_id"));
					ResearchJoingroupApplyItems applyitems=new ResearchJoingroupApplyItems();
					applyitems.setAppId(apply.getId());
					applyitems.setReportId(object.getInteger("report_id"));
					applyitems.setTemplateId(object.getInteger("template_id"));
					applyitems.remove("id").save();
				}
			}
			return ret;
		});
	}
 
	public boolean ruleCheck(String reportId,User user) {
		try {
			//根据报告ID 查询出报告信息
			Report report=Report.dao.findById(reportId);
			//只有结构化报告需要进行校验
			if(report.getTemplateId()==0) {
				return true;
			}
			//根据reportID查询出匹配所需要的信息
			StringBuffer sb=new StringBuffer();
			sb.append("   select studyorder.studyitems,studyorder.studydescription, admission.age, report.audittime, syscode.name_zh as sex, srtemplate.name from report");
			sb.append("   INNER JOIN studyorder on studyorder.id = report.studyorderfk");
			sb.append("   INNER JOIN admission on admission.id = studyorder.admissionidfk");
			sb.append("   INNER JOIN patient on patient.id = studyorder.patientidfk");
			sb.append("   INNER JOIN syscode on syscode.code=patient.sex and syscode.type=0001");
			sb.append("   INNER JOIN srtemplate on srtemplate.id=report.template_id");
			sb.append("   where report.id="+reportId);
			Record record=Db.findFirst(sb.toString());
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			HashMap<String,String> map=new HashMap<String,String>();
			//查询出所有的规则
			List<Record> list=Db.find(" select id,group_id,project_id from research_joingroup_rule");
			for (int i = 0; i < list.size(); i++) {
				//查询出每个规则下的条件进行校验
				boolean flag=false;
				List<ResearchJoingroupRuleItem> researchjoingroupruleitem=ResearchJoingroupRuleItem.dao.find("select * from research_joingroup_rule_item where rule_id="+list.get(i).getStr("id"));
				for (ResearchJoingroupRuleItem item : researchjoingroupruleitem) {
					// 001:检查项目  002：检查描述  003：审核时间  004:年龄  005：结构化报告模板  006：性别
					switch(item.getMatchingField())
				      {
				         case "001" :
				        	 if(record.getStr("studyitems")!=null&&record.getStr("studyitems").matches(item.getMatchingValue())) {
								  flag=true;
							  }else {
								  flag=false;
							  }
				        	 break;
				         case "002" :
				        	 if(record.getStr("studydescription")!=null&&record.getStr("studydescription").matches(item.getMatchingValue())) {
									flag=true;
								}else {
									flag=false;
								}
				        	 break;
				         case "003" :
				        	 if(record.getStr("audittime")!=null) {
				        		 String[] str=(item.getMatchingValue()).split(",");
								 String audittime=String.valueOf(record.getStr("audittime"));
								 Date time=df.parse(audittime);
								 Date ctime= df.parse(str[0]);
								 Date etime= df.parse(str[1]);
								 if(time.after(ctime)&&time.before(etime)) {
									 flag=true;
								 }else {
									 flag=false;
								 }
				        	 }
					        break;
					     case "004" :
					    	 if(record.getStr("age")!=null) {
				        		 String[] ageStr=(item.getMatchingValue()).split(",");
								 Integer age=record.getInt("age");
								 Integer bage= Integer.valueOf(ageStr[0]);
								 Integer eage= Integer.valueOf(ageStr[1]);
								 if(bage<=age&&eage>=age) {
									 flag=true;
								 }else {
									 flag=false;
								 }
				        	 }
					        break;
					     case "005" :
						   	if(record.getStr("name")!=null) {
						   	  if(record.getStr("name").equals(item.getMatchingValue())) {
						   		 flag=true;
						   	  }else {
						   		 flag=false;
						   	  }
						   	}
						    break;
						 case "006" :
							if(record.getStr("sex")!=null) {
							  if(record.getStr("sex").equals(item.getMatchingValue())) {
						   		  flag=true;
							  	}else {
						   		  flag=false;
						   	  	}
						   	  }
							break;
				      }
					 //同一个规则下有一个条件不满足则校验不通过
					  if(!flag) {
						  break;
					  }
					}
					//校验通过且该课题下的实验组没有被匹配过
					if(flag&&!map.containsKey(list.get(i).getInt("project_id")+"-"+list.get(i).getInt("group_id"))) {
						map.put(list.get(i).getInt("project_id")+"-"+list.get(i).getInt("group_id"), list.get(i).getInt("project_id")+"-"+list.get(i).getInt("group_id"));
						ResearchJoingroupApply researchjoingroupapply=new ResearchJoingroupApply();
						researchjoingroupapply.setProjectId(list.get(i).getInt("project_id"));
						researchjoingroupapply.setGroupId(list.get(i).getInt("group_id"));
						researchjoingroupapply.setCreatetime(new Date());
						researchjoingroupapply.setAppDatetime(new Date());
						researchjoingroupapply.setApplicant(user.getId());
						researchjoingroupapply.setValidPeriod(null);
						researchjoingroupapply.setStatus(1);
						researchjoingroupapply.remove("id").save();
						
						ResearchJoingroupApplyItems researchjoingroupapplyitems=new ResearchJoingroupApplyItems();
						researchjoingroupapplyitems.setAppId(researchjoingroupapply.getId());
						researchjoingroupapplyitems.setReportId(Integer.parseInt(reportId));
						researchjoingroupapplyitems.setTemplateId(report.getTemplateId());
						researchjoingroupapplyitems.setCreatetime(new Date());
						researchjoingroupapplyitems.remove("id").save();
					}
			}
		}  catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public List<Record> searchTaskFormSv(String taskName, String experience_group_id) {
		 String sql="select * from research_form where 1=1";
		 List<Object> params=new ArrayList<Object>();
    	 if(taskName!=null&&!"".equals(taskName)) {
    		 sql+=" and r.studyitem LIKE CONCAT('%',?,'%')";
    		 params.add(taskName);
    	 }
    	 return Db.find(sql,params.toArray());
	}

	public List<Record> searchProjectSv(String projectname) {
		 String sql="select * from research_project where deleted!='1'";
		 List<Object> params=new ArrayList<Object>();
    	 if(projectname!=null&&!"".equals(projectname)) {
    		 sql+=" and name LIKE CONCAT('%',?,'%')";
    		 params.add(projectname);
    	 }
		return  Db.find(sql,params.toArray());
	}

	public List<Record> findResearchUserRoleSv(User user) {
		 StringBuffer sb=new StringBuffer();
		 List<Object> params=new ArrayList<Object>();
		 sb.append(" select * from research_user_role tpr,research_role_authority trjr,research_authority tj");
		 sb.append(" where tpr.role_id=trjr.role_id and trjr.authority_id=tj.id");
		 sb.append(" and tpr.user_id=?");
		 params.add(user.getId());
		return Db.find(sb.toString(),params.toArray());
	}

//	public List<Record> searchExgroupTaskformSv(Integer experienceId, String taskName) {
//		 StringBuffer sbstr=new StringBuffer();
//		 List<Object> params=new ArrayList<Object>();
//    	 sbstr.append(" select etr.id as etr_id,etr.exGroupId,etr.taskFromId,tf.id as task_formId,tf.name,tf.formcontent,tf.createtime from exgroup_taskform_relation etr,research_form tf where  etr.taskFromId=tf.id");
//    	 if(experienceId!=null&&experienceId!=0) {
//    		 sbstr.append(" and etr.exGroupId=?");
//    		 params.add(experienceId);
//    	 }
//    	 if(taskName!=null&&!"".equals(taskName)) {
//    		 sbstr.append(" and tf.name LIKE CONCAT('%',?,'%')");
//    		 params.add(taskName);
//    	 }
//		return Db.find(sbstr.toString(),params.toArray());
//	}
	
	public List<Record> searchTestGroupformSv(Integer groupid, String formname,boolean withContent) {
		String columns=ResearchForm.dao.toSelectStr(withContent?"":"formcontent,datamodel");
		StringBuffer sbstr = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		sbstr.append("select "+columns+",research_testgroup_form.id as groupformid from research_form,research_testgroup_form where research_form.id=research_testgroup_form.formid ");
		if (groupid != null) {
			sbstr.append(" and research_testgroup_form.groupid=?");
			params.add(groupid);
		}
		if (StrKit.notBlank(formname)) {
			sbstr.append(" and research_form.name LIKE CONCAT('%',?,'%')");
			params.add(formname);
		}
		return Db.find(sbstr.toString(), params.toArray());
	}
	/*
	 * param:
	 * groupid：实验组id
	 * formid：表单id
	 * 
	 * return：String
	 * 1 ：保存添加成功
	 * 2 ：添加失败
	 * 3 ：添加失败，已经存在
	 */
	public String addFormToGroup(ResearchTestgroupForm tf){
		String ret="1";
		ResearchTestgroupForm rtf=ResearchTestgroupForm.dao.findFirst("select top 1 * from research_testgroup_form where groupid=? and formid=?",tf.getGroupid(),tf.getFormid());
		if(rtf!=null){
			ret="3";
		} else{
			if(!tf.remove("id").save()){
				ret="2";
			}
		}
		return ret;
	}
	
	/*
	 * 从实验组移除表单，并且删除和表单相关的数据
	 * param:
	 * id：research_testgroup_form表的id
	 * 
	 * return：boolean
	 * true ：删除成功
	 * false ：删除失败
	 */
	public boolean removeFormFromGroup(Integer id){
		return Db.tx(()->{
			boolean ret=true;
			try{
				ResearchTestgroupForm tf= ResearchTestgroupForm.dao.findById(id);
				if(tf!=null){
					Db.delete("delete from research_testgroup_data where group_id=? and form_id=?", tf.getGroupid(),tf.getFormid());
					ret=ret&&tf.delete();
				}
			} catch(Exception e){
				ret=false;
			}
			return ret;
		});
	}

	public List<Record> searchProjectMembersSv(Integer taskId) {
		return Db.find("select tpr.id,u.id as personnel_id,u.name,tr.rolename,tr.description from research_user_role tpr,users u,research_role tr  where tpr.user_id=u.id    and tr.id=tpr.role_id and tpr.project_id=?",taskId);
	}

	public List<Record> searchTaskPersonnelSv(Integer roleId) {
		 StringBuffer sbstr=new StringBuffer();
		 List<Object> params=new ArrayList<Object>();
		 sbstr.append("select trp.id,u.id,u.name from task_role_personnel trp,users u  where trp.personnelId=u.id and trp.roleId=?");
		 params.add(roleId);
		return Db.find(sbstr.toString(),params.toArray());
	}

	public List<Record> searchProjectRoleSv(String role_name) {
		Subject subject = SecurityUtils.getSubject();
		List<Object> params=new ArrayList<Object>();
		String sql="select * from research_role where 1=1";
 		if(role_name!=null&&!"".equals(role_name)) {
 			sql+=" and rolename=?";
 			params.add(role_name);
 		}
		List<Record> list = Db.find(sql, params.toArray());

		for (Record record : list) {
			if(record.get("user_level")!=null){
				String user_level = ProjectUserLevel.getValue(record.get("user_level"));
				if(StringUtils.isNotBlank(user_level)) {
					record.set("user_level", user_level);
				}
			}
			if (subject.isPermitted("edit_project_role")){
				record.set("edit_project_role", "true");
			}
			if (subject.isPermitted("delete_project_role")){
				record.set("delete_project_role", "true");
			}
		}
		return list;
	}

	public List<Record> findProjectByUserId(User user) {
		return Db.find("select tm.id,tm.name from research_project tm,research_user_role tpr where tm.id=tpr.project_id and deleted=0 and tpr.user_id=?",user.getId());
	}

	public List<Record> searchJoingroupApply(User user, String status, String name, String strTime, String endTime) {
		StringBuffer sb= new StringBuffer();
		List<Object> params=new ArrayList<Object>();
    	sb.append(" select t.name as project_name,e.name as group_name,u.name as user_name,r.* ");
    	sb.append(" from research_joingroup_apply r");
    	sb.append(" left join research_project t on r.project_id=t.id");
    	sb.append(" left join research_test_group e on r.group_id=e.id");
    	sb.append(" left join users u on r.applicant=u.id");
    	sb.append(" where 1=1 ");
    	if(name!=null&&!"".equals(name)) {
    		sb.append(" and u.name=?");
    		params.add(name);
    	}
    	if(status!=null&&!"".equals(status)) {
    		sb.append(" and r.status=?");
    		params.add(status);
    	}
    	if(strTime!=null&&!"".equals(strTime)) {
    		sb.append(" and r.app_datetime>=?");
    		params.add(strTime);
    	}
    	if(endTime!=null&&!"".equals(endTime)) {
    		sb.append(" and r.app_datetime<=?");
    		params.add(endTime);
    	}
		return Db.find(sb.toString(),params.toArray());
	}

	public List<Record> searchRuleItemsSv(Integer task_rule_id) {
		return Db.find("select * from research_joingroup_rule_item where rule_id=?",task_rule_id);
	}

	public List<Record> searchJoinGroupApplyItemsSv(Integer report_order_id, String templateId, String syscode_lan) {
		StringBuffer sb=new StringBuffer();
		List<Object> params=new ArrayList<Object>();
		sb.append(" select research_joingroup_apply_items.id,");
		sb.append(" 	   report.id as report_id,");
		sb.append(" 	   report.studyid,");
		sb.append(" 	   report.audittime,");
		sb.append(" 	   report.reportphysician,");
		sb.append(" 	   report.reportphysician_name,");
		sb.append(" 	   report.auditphysician,");
		sb.append(" 	   report.auditphysician_name,");
		sb.append(" 	   report.reporttime,");
		sb.append(" 	   syscode.name_zh as sex,");
		sb.append(" 	   srtemplate.name as template_name,");
		sb.append(" 	   srtemplate.id as template_id,");
		sb.append(" 	   studyorder.studyitems,");
		sb.append(" 	   studyorder.studydescription,");
		sb.append(" 	   admission.age,");
		sb.append(" 	   studyorder.regdatetime,");
		sb.append(" 	   patient.patientname,");
		sb.append(" 	   patient.patientid,");
		sb.append(" 	   studyorder.modality_type");
		sb.append(" from research_joingroup_apply_items ");
		sb.append("      INNER JOIN report on research_joingroup_apply_items.report_id=report.id  ");
		sb.append("      INNER JOIN studyorder on studyorder.id = report.studyorderfk");
		sb.append(" 	 INNER JOIN patient on patient.id = studyorder.patientidfk ");
		sb.append(" 	 INNER JOIN srtemplate on srtemplate.id=report.template_id  ");
		sb.append(" 	 INNER JOIN syscode on syscode.code=patient.sex and syscode.type=0001");
		sb.append(" 	 INNER JOIN admission on admission.id = studyorder.admissionidfk ");
		sb.append(" 	 where research_joingroup_apply_items.app_id=?");
        params.add(report_order_id);
    	if(templateId!=null&&!"".equals(templateId)) {
    		sb.append(" and srtemplate.id=?");
    		params.add(templateId);
    	}
    	return Db.find(sb.toString(),params.toArray());
	}

	public  List<Record> searchAuthoritySv(String jurisdictionName) {
		Subject subject = SecurityUtils.getSubject();
		StringBuffer sb =new StringBuffer();
		List<Object> params=new ArrayList<Object>();
		sb.append("SELECT tj.*,u.name as creation_name from research_authority tj,users u where tj.creator=u.id");
		if(jurisdictionName!=null&&!"".equals(jurisdictionName)) {
			sb.append(" and tj.name LIKE CONCAT('%',?,'%')");
			params.add(jurisdictionName);
		}
		List<Record> list = Db.find(sb.toString(),params.toArray());
		for (Record record : list) {
			if (subject.isPermitted("edit_project_authority")){
				record.set("edit_project_authority", "true");
			}
			if (subject.isPermitted("delete_project_authority")){
				record.set("delete_project_authority", "true");
			}
		}
		return list;
	}

	public List<Record>  serachRoleAuthoritySv(String roleId) {
		StringBuffer sb =new StringBuffer();
		List<Object> params=new ArrayList<Object>();
    	if(roleId!=null&&!"".equals(roleId)) {
    	    sb.append(" select tj.*,(case when trjr.id is NULL then '0' else '1' end) as ck  from research_authority tj left join research_role_authority trjr on tj.id=trjr.authority_id and trjr.role_id=?");
    	    params.add(roleId);
    	}else {
    		sb.append(" select tj.*,(case when trjr.id is NULL then '0' else '1' end) as ck  from research_authority tj left join research_role_authority trjr on tj.id=trjr.authority_id and 1=2");
    	}
    	return Db.find(sb.toString(),params.toArray());
	}

	public List<Record> searchResourceSv(String resourceName, Integer authorityId) {
		StringBuffer sb =new StringBuffer();
		List<Object> params=new ArrayList<Object>();
		if(authorityId!=null) {
			sb.append(" select rr.*,(case when rar.id is NULL then '0' else '1' end) as ck from research_resource rr left join research_authority_resource rar on rr.id=rar.resource and rar.authority=?");
			params.add(authorityId);
		}else {
			sb.append(" select *from research_resource where 1=1");
			if(resourceName!=null&&!"".equals(resourceName)) {
				sb.append(" and resource_name=?");
				params.add(resourceName);
			}
		}
		return Db.find(sb.toString(),params.toArray());
	}

	public boolean checkPersonnel(ResearchUserRole tpr) {
		boolean flag=true;
		List<Record> list=Db.find("select rur.*,u.name from research_user_role  rur left join users u on rur.user_id=u.id where project_id="+tpr.getProjectId()+" and user_id="+tpr.getUserId());
		if(list.size()>0){
			flag=false;
		}
		return flag;
	}

	public boolean saveResearchAuthoritySv(ResearchAuthority auth,String rows,User user) {
		return Db.tx(()->  {
			 boolean ret=true;
			 try {
				 if(auth.getId()!=null) {
					 Db.update("research_authority", auth.toRecord());
					 Db.delete("delete research_authority_resource where authority=?",auth.getId());
				 } else {
					 auth.setCreator(user.getId());
					 Record r=auth.remove("id").toRecord();
					 Db.save("research_authority", r);
					 auth.setId(r.getInt("id"));
				 }
				 
				 JSONArray jsonArray = JSONArray.parseArray(rows);
				 for (int i = 0; i < jsonArray.size(); i++) {
					 JSONObject obj=jsonArray.getJSONObject(i);
					 ResearchAuthorityResource rar=new ResearchAuthorityResource();
					 rar.setAuthority(auth.getId());
					 rar.setResource(obj.getInteger("rid"));
					 Db.save("research_authority_resource", rar.toRecord());
				}
			} catch (Exception e) {
				ret=false;
				e.printStackTrace();
			}
			return ret;
 		});
		
	}
	public boolean saveTestGroup(ResearchTestGroup tg,User user){
		boolean ret=true;
		Date now=new Date();
		if(tg.getId()!=null) {
			tg.setUpdatetime(now);
			ret=ret&&tg.update();
		}else {
			tg.setCreatetime(now);
			tg.setCreator(user.getId());
			tg.setDeleted("0");
			ret=ret&&tg.remove("id").save();
		}
		return ret;
	}
	
	public List<Record> searchTestGroupDatas(Integer groupid,Integer formid,Integer type){
		StringBuffer sb =new StringBuffer();
		List<Object> params=new ArrayList<Object>();
		sb.append(" select research_testgroup_data.*,s1.name_zh,users.name,patient.patientname,s2.name_zh as sex,patient.patientid,report.studyid,studyorder.modality_type,studyorder.studyitems");
		sb.append(" from  research_testgroup_data  ");
		sb.append(" left join syscode s1 on research_testgroup_data.status=s1.code and s1.type=0028");
		sb.append(" left join users on research_testgroup_data.creator=users.id  ");
		sb.append(" left JOIN report on research_testgroup_data.reportid=report.id  ");
		sb.append(" left JOIN studyorder on studyorder.id = report.studyorderfk");
		sb.append(" left JOIN patient on patient.id = studyorder.patientidfk");
		sb.append(" left JOIN syscode s2 on patient.sex=s2.code and s2.type=0001  ");
		sb.append(" where research_testgroup_data.group_id=?");
		params.add(groupid);
		if(formid!=null) {
			sb.append(" and research_testgroup_data.form_id=?");
			params.add(formid);
		}
		if(type!=null) {
			sb.append(" and research_testgroup_data.type=?");
			params.add(type);
		}
		return Db.find(sb.toString(),params.toArray());
		
	}
	
	public Record openForm(Integer dataid,Integer formid){
		Record ret=null;
		//打开已经录入的表单
		if(dataid!=null){
			ret=Db.findFirst("select * from research_testgroup_data where id=?",dataid);
			//通过excel导入的数据（type=3），状态为导入或审核，需要加载模板的内容。批量审核的表单，html为空，再打开时需要模板内容
			if(StrKit.isBlank(ret.getStr("html"))&&ret.getInt("type")==3&&(FormStatus.Imported.equals(ret.getStr("status"))||FormStatus.FinalResults.equals(ret.getStr("status")))) {
				ResearchForm form=ResearchForm.dao.findById(ret.getInt("form_id"));
				ret.set("html", form.getFormcontent());
				ret.set("filldata", "1");//加载新的模板内容，前端需要填充数据至模板中
			}
			
		} else {//打开新的表单
			ResearchForm form=ResearchForm.dao.findById(formid);
			ret=new Record();
			ret.set("id", "new-"+StrKit.getRandomUUID()).set("name",form.getName()).set("html", form.getFormcontent());
		}
		return ret;
	}

	public boolean refuseApplyForJoinGroupSv(String datas) {
		return Db.tx(() ->  {
		    boolean ret = true;
			JSONArray array=JSONArray.parseArray(datas);
			for (int i = 0; i < array.size(); i++) {
				JSONObject object=array.getJSONObject(i);
				ResearchJoingroupApply reportorder=ResearchJoingroupApply.dao.findById(object.get("id"));
				if(object.getInteger("status")==1) {
					reportorder.setStatus(3);
					Db.update("research_joingroup_apply", reportorder.toRecord());
				}
			}
			return ret;
		});
	}

	public String checkapplySreport2Group(Integer groupid, String reports) {
		String str="";
		JSONArray array=JSONArray.parseArray(reports);
		for (int i = 0; i < array.size(); i++) {
			JSONObject object=array.getJSONObject(i);
			List<Record> list=Db.find("select * from research_testgroup_data where reportid="+object.getInteger("report_id")+" and group_id="+groupid);
			if(list.size()>0) {
				str=list.get(i).getStr("form_name");
				break;
			}
		}
		return str;
	}

	public boolean auditFormData(ResearchTestgroupData data,String structdata,String html,User user,String ctx,String info) {
		JSONObject object=JSONObject.parseObject(info);
		String patientid=object.getString("patientid");
		if(StrKit.isBlank(patientid)) {
			patientid=IDUtil.getPatientID_Research();
			object.put("patientid",patientid);
		}
		
//		Date now=new Date();
//		html=html.replaceAll("\\[ReportPhysicianName\\]", user.getName()).replaceAll("\\[ReportDateTime\\]", DateUtil.dtsWithTime(now));
//		String temp_html=html.replaceAll(ctx+"/image/image", "/image/image");
		data.setHtml(html);
		
		boolean succeed=Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			boolean re=true;
			if(data.getId()==null){
				if(data.getFormId()!=null){
					ResearchForm form=ResearchForm.dao.findById(data.getFormId());
					data.setFormName(form.getName());
				}
				data.setStatus(FormStatus.FinalResults);
				data.setType(1);
				data.setCreator(user.getId());
				data.setPatientname(object.getString("patientname"));
				data.setPatientid(object.getString("patientid"));
				data.setSex(object.getString("sex"));
				data.setStudyid(object.getString("studyid"));
				data.setModalityType(object.getString("modality_type"));
				data.setStudyitems(object.getString("studyitems"));
				data.setAge(object.getInteger("age"));
				data.setAgeUnit(object.getString("age_unit"));
				data.setBirthdate(object.getString("birthdate"));
				data.setStudyDatetime(object.getDate("study_datetime"));
				data.setErrorImport(null);
				data.setAuditor(user.getId());
            	data.setAudittime(new Date());
				data.remove("id");//.save()
				re=re&Db.save("research_testgroup_data", data.toRecord());
			} else{
				data.setStatus(FormStatus.FinalResults);
				data.setPatientname(object.getString("patientname"));
				data.setPatientid(object.getString("patientid"));
				data.setSex(object.getString("sex"));
				data.setStudyid(object.getString("studyid"));
				data.setModalityType(object.getString("modality_type"));
				data.setStudyitems(object.getString("studyitems"));
				data.setAge(object.getInteger("age"));
				data.setAgeUnit(object.getString("age_unit"));
				data.setBirthdate(object.getString("birthdate"));
				data.setStudyDatetime(object.getDate("study_datetime"));
				data.setAuditor(user.getId());
            	data.setAudittime(new Date());
            	if(data.getCreator()==null) {
            		data.setCreator(user.getId());
            	}
				data.setErrorImport(null);
//				data.update();
				re=re&Db.update("research_testgroup_data", data.toRecord());
			}
			
			Db.update("delete from research_form_data where dataid=?", data.getId());
			List<JSONObject> paramList = JSON.parseArray(structdata, JSONObject.class);
			List<ResearchFormData> srs=new ArrayList<ResearchFormData>();
			for(JSONObject obj:paramList){
				ResearchFormData sr=new ResearchFormData();
				sr.setDataid(data.getId());
				sr.setCode(obj.getString("code"));
				sr.setOptioncode( obj.getString("optioncode"));
				sr.setValue( obj.getString("value"));
				sr.setUnit( obj.getString("unit"));
				sr.setGrp(obj.getString("group"));
				sr.setPath(obj.getString("path"));
				srs.add(sr);
			}
			Db.batchSave(srs,srs.size());
			ActiveMQ.sendObjectMessage(new ESIndexDocOrder(data.getId()), MQSubject.ESINDEXDOC.getSendName(), StrKit.getRandomUUID(), 4);
			return re;
		});
		data.setHtml(html);
		data.setStatus(ResearchTestgroupData.dao.findById(data.getId()).getStatus());
		return succeed;
		
	}
	
	public void auditFormDataBatch(String ids,User user,ResultVO vo) {
//		Db.tx(Connection.TRANSACTION_READ_UNCOMMITTED,()->{
			boolean re=true;
			String idsarr[]= ids.split(",");
			for(int i=0;i<idsarr.length;i++) {
				ResearchTestgroupData data=ResearchTestgroupData.dao.findById(idsarr[i]);
				if(data!=null&&FormStatus.Imported.equals(data.getStatus())) {
					if(StrKit.isBlank(data.getErrorImport())&&checkPatientInfo(data.getPatientid(),data.getPatientname(),data.getId()).getCode()==0) {
						data.setStatus(FormStatus.FinalResults);
						data.setAuditor(user.getId());
		            	data.setAudittime(new Date());
						data.setErrorImport(null);
						re=re&Db.update("research_testgroup_data", data.toRecord());
						ActiveMQ.sendObjectMessage(new ESIndexDocOrder(data.getId()), MQSubject.ESINDEXDOC.getSendName(), StrKit.getRandomUUID(), 4);
					} else {
						vo.setCode(-2);//部分数据存在错误，无法审核
					}
				}
			}
			if(!re) {
				vo.setCode(-1);
			}
//			return re;
//		});	
	}
	
	 /**
	  * 判断字符串是否符合正则表达式
     *
     * @param
     * @param regex
     * @return
     */
    public  boolean isRegex(String regex) {
    	boolean flag=true;
        try {
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher("校验正则表达式是否正确");
			m.find();
		} catch (Exception e) {
			flag=false;
		}
        return flag;
    }
    public boolean saveRuleSv(ResearchJoingroupRule taskrule, User user, String datas) {
	    return  Db.tx(()->  {
			 boolean ret=true;
				if(taskrule.getId()!=null) {
					Db.update("research_joingroup_rule", taskrule.toRecord());
				}else {
					taskrule.setCreator(user.getId());
					Db.save("research_joingroup_rule", taskrule.toRecord());
				}
				
				Db.delete("delete research_joingroup_rule_item where rule_id=?",taskrule.getId());
				JSONArray jsonarray=JSONArray.parseArray(datas);
				for (int i = 0; i < jsonarray.size(); i++) {
					JSONObject object=jsonarray.getJSONObject(i);
					ResearchJoingroupRuleItem taskrulelines=new ResearchJoingroupRuleItem();
					taskrulelines.setRuleId(taskrule.getId());
					taskrulelines.setMatchingField(object.getString("matching_field"));
					if("001".equals(object.getString("matching_field"))||"002".equals(object.getString("matching_field"))){
						//校验正则表达式是否合规
						if(!isRegex(object.getString("matching_value"))) {
							 ret=false;
						}
					}
					taskrulelines.setMatchingName(object.getString("matching_name"));
					taskrulelines.setMatchingValue(object.getString("matching_value"));
					Db.save("research_joingroup_rule_item", taskrulelines.toRecord());
				}	
			return ret;
		});
    }

	public  Page<Record> searchStudyInfo(ParaKit paraKit) {
		List<Object> list = new ArrayList<>();
		String where="";
		if(paraKit.hasPara("reportstatus")) {
			where+=" and studyorder.reportstatus=?";
			list.add(paraKit.getPara("reportstatus"));
		}
		if(paraKit.hasPara("orderstatus")) {
			where+=" and studyorder.status=?";
			list.add(paraKit.getPara("orderstatus"));
		}
		String from = null;
		String to = null;
		// 时间选择 'T'今天(today) 'Y'昨天(yesterday) 'TD'三天(ThreeDay) 'FD'五天(FiveDay) 'W'一周  'M'一月 '
		LocalDate now=LocalDate.now();
		if (StrKit.equals(SearchTimeConstant.Today, paraKit.getPara("appdate"))) {
			from = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate()));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() + 1));
		}else if (StrKit.equals(SearchTimeConstant.Yesterday, paraKit.getPara("appdate"))) {
			from = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals(SearchTimeConstant.ThreeDay, paraKit.getPara("appdate"))){
			from = now.minusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals(SearchTimeConstant.FiveDay, paraKit.getPara("appdate"))){
			from = now.minusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals(SearchTimeConstant.Week, paraKit.getPara("appdate"))){
			from = now.minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals(SearchTimeConstant.ThreeMonth, paraKit.getPara("appdate"))){
			from = now.minusMonths(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}

		if (StrKit.notBlank(paraKit.getPara("datefrom"))) {
			from = paraKit.getPara("datefrom")+" 00:00:00";
		}
		if (StrKit.notBlank(paraKit.getPara("dateto"))) {
			to = paraKit.getPara("dateto")+" 23:59:59";
		}
		String timecon = "";
		if (StrKit.equals("registertime", paraKit.getPara("datetype"))) {
			timecon = "studyorder.regdatetime";
		} else if (StrKit.equals("studytime", paraKit.getPara("datetype"))) {
			timecon = "studyorder.studydatetime";
		} else if (StrKit.equals("reporttime", paraKit.getPara("datetype"))) {
			timecon = "report.reporttime";
		} else if (StrKit.equals("audittime", paraKit.getPara("datetype"))) {
			timecon = "report.audittime";
		} else if (StrKit.equals("appointmenttime", paraKit.getPara("datetype"))) {
			timecon = "studyorder.appointmenttime";
		}

		if (StrKit.notBlank(from)) {
			where += " and " + timecon + " >='" + from + "'";
		}
		if (StrKit.notBlank(to)) {
			where += " and " + timecon + " <='" + to + "'";
		}
		
		
		if (StrKit.notBlank(paraKit.getPara("studyid"))) {
			where += " and studyorder.studyid =?";
			list.add(paraKit.getPara("studyid"));
		}
		if (StrKit.notBlank(paraKit.getPara("patientname"))) {
			where += " and patient.patientname LIKE CONCAT('%',?,'%')";
			list.add(paraKit.getPara("patientname"));
		}
		
		if (StrKit.notBlank(paraKit.getPara("patientid"))) {
			where += " and patient.patientid = ?";
			list.add(paraKit.getPara("patientid"));
		}
		if (StrKit.notBlank(paraKit.getPara("patientsource"))) {
			where += " and admission.patientsource = ?";
			list.add(paraKit.getPara("patientsource"));
		}

		StringBuffer sb=new StringBuffer();
		sb.append(" select patient.patientname,syscode.name_zh as sex,patient.patientid,studyorder.id as orderid,studyorder.studyid,studyorder.modality_type,studyorder.studyitems ");
		StringBuffer sqlexcept=new StringBuffer();
		sqlexcept.append(" from studyorder,patient,admission,syscode,report");
		sqlexcept.append(" where patient.id=admission.patientidfk  ");
		sqlexcept.append(" and admission.id=studyorder.admissionidfk");
		sqlexcept.append(" and report.studyorderfk=studyorder.id");
		sqlexcept.append(" and syscode.code=patient.sex");
		sqlexcept.append(" and syscode.type=0001");
		sqlexcept.append(where);
		return Db.paginate(Integer.valueOf(paraKit.getPara("page")), Integer.valueOf(paraKit.getPara("rows")), sb.toString(), sqlexcept.toString(),list.toArray());
	}
	
	
	public List<Record> searchTestGroupDatas1(Integer groupid,Integer formid,Integer type,String patientid,ParaKit kit){
		StringBuffer sb =new StringBuffer();
		List<Object> params=new ArrayList<Object>();
//		sb.append(" select * from (select research_testgroup_data.id,research_testgroup_data.type,research_testgroup_data.form_id,research_testgroup_data.form_name,");
//		sb.append(" research_testgroup_data.group_id,research_testgroup_data.reportid,research_testgroup_data.html,research_testgroup_data.print_html,research_testgroup_data.status,");
//		sb.append(" s1.name_zh,");
//		sb.append(" users.name as creator,");
//		sb.append(" patient.patientname,");
//		sb.append(" s2.name_zh as sex,");
//		sb.append(" patient.patientid,");
//		sb.append(" report.studyid,");
//		sb.append(" studyorder.modality_type,");
//		sb.append(" research_testgroup_data.createtime,");
//		sb.append(" studyorder.studyitems,");
//		sb.append(" admission.age,admission.ageunit as age_unit,");
//		sb.append(" (select top 1 name_zh from syscode where admission.ageunit=syscode.code and syscode.type='0008') as ageunitdisplay,patient.birthdate ");
//		sb.append(" from research_testgroup_data ");
//		sb.append(" left join syscode s1 on research_testgroup_data.status=s1.code and s1.type='0043' ");
//		sb.append(" left join users on research_testgroup_data.creator=users.id   ");
//		sb.append(" left JOIN report on research_testgroup_data.reportid=report.id   ");
//		sb.append(" left JOIN studyorder on studyorder.id = report.studyorderfk ");
//		sb.append(" left JOIN patient on patient.id = studyorder.patientidfk ");
//		sb.append(" left JOIN admission on admission.id = studyorder.admissionidfk ");
//		sb.append(" left JOIN syscode s2 on patient.sex=s2.code and s2.type='0001'   ");
//		sb.append(" where research_testgroup_data.type=2");
//		sb.append(" union");
//		sb.append(" select research_testgroup_data.id,research_testgroup_data.type,research_testgroup_data.form_id,research_testgroup_data.form_name,");
//		sb.append(" research_testgroup_data.group_id,research_testgroup_data.reportid,research_testgroup_data.html,research_testgroup_data.print_html,research_testgroup_data.status,");
//		sb.append(" s1.name_zh,");
//		sb.append(" users.name as creator,");
//		sb.append(" research_testgroup_data.patientname,(select top 1 name_zh from syscode where research_testgroup_data.sex=syscode.code and syscode.type='0001') as sex,research_testgroup_data.patientid,research_testgroup_data.studyid,");
//		sb.append(" research_testgroup_data.modality_type,research_testgroup_data.createtime,research_testgroup_data.studyitems,research_testgroup_data.age,research_testgroup_data.age_unit,");
//		sb.append(" (select top 1 name_zh from syscode where research_testgroup_data.age_unit=syscode.code and syscode.type='0008') as ageunitdisplay,research_testgroup_data.birthdate ");
//		sb.append(" from  research_testgroup_data ");
//		sb.append(" left join syscode s1 on research_testgroup_data.status=s1.code and s1.type='0043' ");
//		sb.append(" left join users on research_testgroup_data.creator=users.id   ");
//		sb.append(" where research_testgroup_data.type=1) a");
//		sb.append(" where a.group_id=?");

		sb.append(" select research_testgroup_data.id,research_testgroup_data.type,research_testgroup_data.form_id,research_testgroup_data.form_name,");
		sb.append(" research_testgroup_data.group_id,research_testgroup_data.reportid,research_testgroup_data.html,research_testgroup_data.print_html,research_testgroup_data.status,");
		sb.append(" (select name_zh from syscode where syscode.code=research_testgroup_data.status and syscode.type='0043') statusdisplay,");
		sb.append(" (select name from users where research_testgroup_data.creator=users.id ) as creator_name, ");
		sb.append(" (select name from users where research_testgroup_data.auditor=users.id ) as auditor_name,audittime, ");
		sb.append(" research_testgroup_data.patientname,(select top 1 name_zh from syscode where research_testgroup_data.sex=syscode.code and syscode.type='0001') as sexdisplay,research_testgroup_data.patientid,research_testgroup_data.studyid,");
		sb.append(" research_testgroup_data.modality_type,research_testgroup_data.createtime,research_testgroup_data.studyitems,research_testgroup_data.age,research_testgroup_data.age_unit,");
		sb.append(" (select top 1 name_zh from syscode where research_testgroup_data.age_unit=syscode.code and syscode.type='0008') as ageunitdisplay,research_testgroup_data.birthdate,research_testgroup_data.study_datetime, ");
		sb.append(" research_testgroup_data.error_import ");
		sb.append(" from research_testgroup_data ");
		sb.append(" where group_id=? ");
		params.add(groupid);
		if(formid!=null) {
			sb.append(" and research_testgroup_data.form_id=?");
			params.add(formid);
		}
		if(type!=null) {
			sb.append(" and research_testgroup_data.type=?");
			params.add(type);
		}
		if(StrKit.notBlank(patientid)) {
			sb.append(" and research_testgroup_data.patientid=?");
			params.add(patientid);
		}
		if(StrKit.notBlank(kit.getPara("patientname"))) {
			sb.append(" and research_testgroup_data.patientname=?");
			params.add(kit.getPara("patientname"));
		}
		if(StrKit.notBlank(kit.getPara("sex"))) {
			sb.append(" and research_testgroup_data.sex=?");
			params.add(kit.getPara("sex"));
		}
		if(StrKit.notBlank(kit.getPara("status"))) {
			sb.append(" and research_testgroup_data.status=?");
			params.add(kit.getPara("status"));
		}
		sb.append(" order by form_id,createtime desc");
		return Db.find(sb.toString(),params.toArray());
		
	}
	
	public Record getStudyInfoByOrderid(Integer orderid) {
		return Db.findFirst("select top 1 patient.patientid,patient.patientname,patient.sex,patient.birthdate,admission.age,admission.ageunit as age_unit,"
				+ " studyorder.studyid,studyorder.studydatetime as study_datetime "
				+ " from patient,admission,studyorder where patient.id=admission.patientidfk and admission.id=studyorder.admissionidfk and studyorder.id=?",orderid);
	}
	
	public Record getDicomInfoByStudypk(Integer studypk) {
		return Db.use(DbConfigName.MSSQL_MATRIX_DICOM).findFirst("select top 1 dcm_patient.patientid,dcm_patient.patientname,dcm_patient.patientsex as sex,"//dcm_patient.birthday as birthdate,
				+ " dcm_study.acc_no as studyid,dcm_study.study_datetime "
				+ " from dcm_patient,dcm_study where dcm_patient.id=dcm_study.patient_fk and dcm_study.id=?",studypk);
	}
	
	public List<Record> findGroupPatient(Integer groupid){
		return Db.find("select distinct case when RTRIM(patientname)='' then NULL else patientname end as patientname,case when RTRIM(patientid)='' then NULL else patientid end as patientid,"
				+ "age,case when RTRIM(age_unit)='' then NULL else age_unit end as age_unit,"
				+ "(select top 1 name_zh from syscode where research_testgroup_data.age_unit=syscode.code and syscode.type='0008') as ageunitdisplay, "
				+ "case when RTRIM(sex)='' then NULL else sex end as sex,"
				+ "(select top 1 name_zh from syscode where research_testgroup_data.sex=syscode.code and syscode.type='0001') as sexdisplay,"
				+ "case when RTRIM(birthdate)='' then NULL else birthdate end as birthdate "
				+ "from research_testgroup_data where research_testgroup_data.group_id=?", groupid);
	}
	
	public List<Record> findStudys(String patientid){
		return Db.use(DbConfigName.MSSQL_MATRIX_DICOM).find("select dcm_patient.patientid,dcm_study.study_iuid,dcm_study.mods_in_study,dcm_study.acc_no,dcm_study.bodysite,dcm_study.study_desc,dcm_study.study_datetime,dcm_study.series_num,dcm_study.image_num "
				+ " from dcm_patient,dcm_study where dcm_patient.id=dcm_study.patient_fk and dcm_patient.patientid=?",patientid);
	}
	
	public List<Record> findDicomInfo(ParaKit kit){
		List<Object> list = new ArrayList<>();
		String where="";

		String from = null;
		String to = null;
		// 时间选择 'T'今天(today) 'Y'昨天(yesterday) 'TD'三天(ThreeDay) 'FD'五天(FiveDay) 'W'一周  'M'一月 '
		LocalDate now=LocalDate.now();
		if (StrKit.equals(SearchTimeConstant.Today, kit.getPara("appdate"))) {
			from = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate()));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() + 1));
		}else if (StrKit.equals(SearchTimeConstant.Yesterday, kit.getPara("appdate"))) {
			from = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals(SearchTimeConstant.ThreeDay, kit.getPara("appdate"))){
			from = now.minusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals(SearchTimeConstant.FiveDay, kit.getPara("appdate"))){
			from = now.minusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals(SearchTimeConstant.Week, kit.getPara("appdate"))){
			from = now.minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals(SearchTimeConstant.ThreeMonth, kit.getPara("appdate"))){
			from = now.minusMonths(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}

		if (StrKit.notBlank(kit.getPara("datefrom"))) {
			from = kit.getPara("datefrom")+" 00:00:00";
		}
		if (StrKit.notBlank(kit.getPara("dateto"))) {
			to = kit.getPara("dateto")+" 23:59:59";
		}
		
		String timecon = "dcm_study.study_datetime";
		

		if (StrKit.notBlank(from)) {
			where += " and " + timecon + " >='" + from + "'";
		}
		if (StrKit.notBlank(to)) {
			where += " and " + timecon + " <='" + to + "'";
		}

		if (StrKit.notBlank(kit.getPara("studyid"))) {
			where += " and dcm_study.acc_no =?";
			list.add(kit.getPara("studyid"));
		}
		if (StrKit.notBlank(kit.getPara("patientname"))) {
			where += " and dcm_patient.patientname LIKE CONCAT('%',?,'%')";
			list.add(kit.getPara("patientname"));
		}
		
		if (StrKit.notBlank(kit.getPara("patientid"))) {
			where += " and dcm_patient.patientid = ?";
			list.add(kit.getPara("patientid"));
		}
		if (StrKit.notBlank(kit.getPara("modality"))) {
			where += " and dcm_study.mods_in_study = ?";
			list.add(kit.getPara("modality"));
		}

		StringBuffer sb=new StringBuffer();
		sb.append(" select dcm_patient.patientname,dcm_patient.patientid,dcm_study.acc_no as studyid,dcm_study.mods_in_study as modality_type,dcm_study.study_desc as studyitems, ");
		sb.append(" dcm_patient.patientsex as sex,dcm_study.study_datetime,dcm_study.id as dcmstudypk ");
		sb.append(" from dcm_patient,dcm_study");
		sb.append(" where dcm_patient.id=dcm_study.patient_fk ");
		sb.append(where);
		return Db.use(DbConfigName.MSSQL_MATRIX_DICOM).find(sb.toString(),list.toArray());
	}
	
	public Record getGroupGenderRatio(Integer groupid) {
		Record ret=new Record();
		List<Record> list=new ArrayList<Record>();
		Db.use(DbConfigName.MSSQL).find("select sex,count(*) as value from research_testgroup_data where group_id=? group by sex",groupid).stream().forEach(x->{
			if("F".equals(x.getStr("sex"))) {
				Record r= new Record();
				r.set("name", "男");
				r.set("value", x.getInt("value")!=null?x.getInt("value"):0);
				list.add(r);
			} else if("M".equals(x.getStr("sex"))) {
				Record r= new Record();
				r.set("name", "女");
				r.set("value", x.getInt("value")!=null?x.getInt("value"):0);
				list.add(r);
			}
		});
		ret.set("data", list.toArray());
		return ret;
	}
	
	public Record getGroupAgeDistribution(Integer groupid) {
		Record ret=new Record();
		
		Record r=Db.use(DbConfigName.MSSQL).findFirst(" select sum(l_20) as l_20,sum(r20_40) as r20_40,sum(r40_60) as r40_60,sum(g_60) as g_60 from ( SELECT age " + 
				"   ,SUM( CASE WHEN age <20 THEN 1 ELSE 0 END ) AS l_20" + 
				"   ,SUM( CASE WHEN (age>=20 and age<=40) THEN 1 ELSE 0 END ) AS r20_40" + 
				"   ,SUM( CASE WHEN (age>40 and age<=60) THEN 1 ELSE 0 END ) AS r40_60" + 
				"   ,SUM( CASE WHEN age>60 THEN 1 ELSE 0 END ) AS g_60" + 
				"  from research_testgroup_data where group_id=? and age is not null GROUP BY age) as tmp",groupid);
		
		List<Integer> list=new ArrayList<Integer>();
		if(r!=null) {
			list.add(r.getInt("l_20")!=null?r.getInt("l_20"):0);
			list.add(r.getInt("r20_40")!=null?r.getInt("r20_40"):0);
			list.add(r.getInt("r40_60")!=null?r.getInt("r40_60"):0);
			list.add(r.getInt("g_60")!=null?r.getInt("g_60"):0);
		} else {
			list.add(0);
			list.add(0);
			list.add(0);
			list.add(0);
		}
		ret.set("data", list.toArray());
		return ret;
	}
	
	
	public SXSSFWorkbook downloadFormTemp(JSONObject obj) {
//		JSONArray arr=JSONArray.parseArray(jsonstr);
//		for(int i=0;i<arr.size();i++) {
//			JSONObject obj= JSONObject.parseObject(jsonstr);
			String sheetname= obj.getString("sheetname");
			String uid= obj.getString("uid");
			JSONArray columns= obj.getJSONArray("columns");
			String[] allcols=ResearchTestgroupData.dao.getGeneralColumnNames();
			String[] dataformat=ResearchTestgroupData.dao.getGeneralDataFormat();
			Map<Integer,String[]> opts=ResearchTestgroupData.dao.getGeneralColumnOptions();//获取一般列的下拉选项
			if(columns!=null&&columns.size()>0) {
				String[] tmparr=new String[columns.size()];
				String[] tmpformat=new String[columns.size()];
				for(int j=0;j<columns.size();j++) {
					JSONObject col = columns.getJSONObject(j);
					tmparr[j]=col.getString("name")+"["+col.getString("code")+"]"+(StrKit.notBlank(col.getString("unit"))?("<"+col.getString("unit")+">"):"");
					//下拉选项
					JSONArray options=col.getJSONArray("options");
					if(options!=null) {
						String[] strarr=new String[options.size()];
						for(int k=0;k<options.size();k++) {
							strarr[k]=options.getString(k);
						}
						opts.put((allcols.length+j), strarr);
					}
					tmpformat[j]="";
					String type=col.getString("type");
					if(StrKit.notBlank(type)) {
						if("date".equals(type)) {
							tmpformat[j]="yyyy-mm-dd";
						} else if("datetime".equals(type)) {
							tmpformat[j]="yyyy-mm-dd hh:mm:ss";
						}
					}
				}
				dataformat=(String[]) ArrayUtils.addAll(dataformat, tmpformat);
				allcols=(String[]) ArrayUtils.addAll(allcols, tmparr);
			}
			return ExcelUtil.getXSSFWorkbook(sheetname, allcols, null,opts,dataformat);
//		}
	}
	
	public SXSSFWorkbook downloadFormData(Integer groupid,Integer formid,JSONObject obj) {

		String sheetname= obj.getString("sheetname");
		String uid= obj.getString("uid");
		JSONArray columns= obj.getJSONArray("columns");
		String[] allcols=ResearchTestgroupData.dao.getGeneralColumnNames();
		String[] dataformat=ResearchTestgroupData.dao.getGeneralDataFormat();
		Map<Integer,String[]> opts=ResearchTestgroupData.dao.getGeneralColumnOptions();//获取一般列的下拉选项
		if(columns!=null&&columns.size()>0) {
			String[] tmparr=new String[columns.size()];
			String[] tmpformat=new String[columns.size()];
			for(int j=0;j<columns.size();j++) {
				JSONObject col = columns.getJSONObject(j);
				tmparr[j]=col.getString("name")+"["+col.getString("code")+"]"+(StrKit.notBlank(col.getString("unit"))?("<"+col.getString("unit")+">"):"");
				//下拉选项
				JSONArray options=col.getJSONArray("options");
				if(options!=null) {
					String[] strarr=new String[options.size()];
					for(int k=0;k<options.size();k++) {
						strarr[k]=options.getString(k);
					}
					opts.put((allcols.length+j), strarr);
				}
				tmpformat[j]="";
				String type=col.getString("type");
				if(StrKit.notBlank(type)) {
					if("date".equals(type)) {
						tmpformat[j]="yyyy-mm-dd";
					} else if("datetime".equals(type)) {
						tmpformat[j]="yyyy-mm-dd hh:mm:ss";
					}
				}
			}
			dataformat=(String[]) ArrayUtils.addAll(dataformat, tmpformat);
			allcols=(String[]) ArrayUtils.addAll(allcols, tmparr);
		}
		
		Map<Integer, List<Record>> datamap=Db.find("select a.*,b.code,b.optioncode,b.value,b.unit from research_testgroup_data a,research_form_data b where a.id=b.dataid and a.group_id=? and a.form_id=? and a.status=? order by a.id,b.id",
				groupid,formid,FormStatus.FinalResults).stream().collect(Collectors.groupingBy(x->x.getInt("id")));
		
		String [][] values=null;
		if(datamap!=null&&datamap.size()>0) {
			values=new String[datamap.size()][allcols.length];
			int n=0;
			for (Integer key : datamap.keySet()) {
	            List<Record> list=removeDuplicateByCode(datamap.get(key));
	            Record record=list.get(0);
	            values[n][0]=record.getStr("patientid");
	            values[n][1]=record.getStr("patientname");
	            values[n][2]=record.getStr("sex");
	            values[n][3]=record.getStr("age");
	            values[n][4]=record.getStr("age_unit");
	            values[n][5]=record.getStr("birthdate");
	            values[n][6]=record.getStr("studyid");
	            Date study_datetime =record.getDate("study_datetime");
	            if(study_datetime!=null) {
	            	values[n][7]=DateKit.toStr(study_datetime, "yyyy-MM-dd HH:mm:ss");
	            } else {
	            	values[n][7]=null;
	            }
	            values[n][8]=record.getStr("modality_type");
	            for(int i=0,len=list.size();i<len;i++) {
	            	Record re=list.get(i);
	            	int index=getCodeIndex(allcols,re.getStr("code"));
	            	log.info("row="+n+";col="+i+";index="+index+";val="+re.getStr("value")+";code="+re.getStr("code"));
	            	if(index==(i+9)) {
	            		values[n][i+9]=re.getStr("value");
	            		log.info("row="+n+";col="+(i+9)+";val="+re.getStr("value"));
	            	}
	            }
	            n++;
	        }
		}
		return ExcelUtil.getXSSFWorkbook(sheetname, allcols, values,opts,null);
	}
	
//	public String[] getGeneralColumns() {
//		String[] ret= {"病人编码(patientid)","病人姓名(patientname)","性别(sex)","年龄(age)","年龄单位(age_unit)","出生日期(birthdate)","检查编码(studyid)","检查时间(study_datetime)","设备类型(modality_type)"};
//		return ret;
//	}
	
	public int getCodeIndex(String[] allcols,String code) {
		for(int i=0,len=allcols.length;i<len;i++) {
//			log.info("col="+allcols[i]+";code="+code);
			if(allcols[i].indexOf("["+code+"]")>=0) {
				return i;
			}
		}
		return -1;
	}
	
	public List<Record> removeDuplicateByCode(List<Record> list) {
		List<Record> ret=new ArrayList<Record>();
		Map<String,String> map= new HashMap<String,String>();
		for(Record re:list) {
			if(!map.containsKey(re.getStr("code"))) {
				ret.add(re);
				map.put(re.getStr("code"), re.getStr("value"));
			}
		}
		return ret;
	}
	
	public ResultVO importFormData(File file,Integer groupid,User user) throws FileNotFoundException, IOException {
		List<ResultVO> ret=new ArrayList<ResultVO>();
		String filename =file.getName();
		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
		
		if(StrKit.isBlank(filename)) {
			return ResultUtil.fail("文件名不能为空！");
		}
		if(filename.indexOf("(")<0||filename.indexOf(")")<0) {
			return ResultUtil.fail("获取表单模板UID失败！文件名："+filename);
		}
		String uid=filename.substring(filename.indexOf("(")+1, filename.indexOf(")"));
		if(StrKit.isBlank(uid)) {
			return ResultUtil.fail("获取表单模板UID失败！文件名："+filename);
		}
		ResearchForm form= ResearchForm.dao.queryByUID(uid);
		if(form==null) {
			return ResultUtil.fail("通过UID获取表单模板失败！UID："+uid);
		}
		
		Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			try {
				XSSFSheet sheet=null;
				log.info("filename=="+filename);
				log.info("sheets="+workbook.getNumberOfSheets());
				if ( workbook.getNumberOfSheets()>0) {//获取每个Sheet表
		            sheet=workbook.getSheetAt(0);
		            if(sheet.getPhysicalNumberOfRows()>1) {
		            	XSSFRow col_row=sheet.getRow(0);
//		            	log.info("colnum=="+col_row.getPhysicalNumberOfCells());
//		            	log.info("colnum=="+col_row.getLastCellNum());
		            	int col_num=col_row.getLastCellNum();
			            for (int j = 1,row_num=sheet.getPhysicalNumberOfRows(); j < row_num; j++) {//获取每行
			            	XSSFRow row=sheet.getRow(j);
			            	if(row==null) {
			            		continue;
			            	}
			            	
			            	StringBuilder sb=new StringBuilder();
			            	ResearchTestgroupData data=new ResearchTestgroupData();
			            	data.setType(3);
			            	data.setFormId(form.getId());
			            	data.setFormName(form.getName());
			            	data.setGroupId(groupid);
			            	data.setStatus(FormStatus.Imported);
			            	data.setCreator(user.getId());
			            	Record data_re=data.toRecord();
			            	if(Db.save("research_testgroup_data", data_re)) {
			            		data.setId(data_re.getInt("id"));
			            	} else {
			            		ret.add(ResultUtil.fail("保存失败！"));
			            		return false;
			            	}
			            	
			            	
			                for (int k = 0,cell_num=col_num; k < cell_num; k++) {//获取每个单元格
			                	XSSFCell cell = row.getCell(k);
			                    log.info(cell);
			                    String colheadname= getColHeadName(col_row,k);
		                    	if(StrKit.isBlank(colheadname)) {
		                    		ret.add(ResultUtil.fail("列头不能为空！序号："+k));
		                    		return false;
		                    	}
			                    if(isGeneralColumn(col_row,k)) {
			                    	String colname=getColName(colheadname);
			                    	if(StrKit.isBlank(colname)) {
			                    		ret.add(ResultUtil.fail("列名不能为空！列头名称："+colheadname));
			                    		return false;
			                    	}
			                    	if(!ResearchTestgroupData.dao.containsAttr(colname)) {
			                    		ret.add(ResultUtil.fail("数据库表中不包含该列："+colname));
			                    		return false;
			                    	}
			                    	String val=getCellValue(cell);
			                    	ResultVO vo=ResearchTestgroupData.dao.validateValue(colname, val);
			                    	if(vo.getCode()!=0) {
			                    		ret.add(ResultUtil.fail((j+1)+"行,"+(k+1)+"列，错误信息："+vo.getMessage()));
			                    		sb.append((j+1)+"行,"+(k+1)+"列，错误信息："+vo.getMessage());
			                    		continue;
			                    	} else if(vo.getData()!=null){
			                    		val=vo.getData().toString();
			                    	}
			                    	data.set(colname, val);
			                    } else {
			                    	String code=getCode(colheadname);
			                    	if(StrKit.isBlank(code)) {
			                    		ret.add(ResultUtil.fail("编码不能为空！列头名称："+colheadname));
			                    		return false;
			                    	}
			                    	ResearchFormData fdata=new ResearchFormData();
			                    	fdata.setDataid(data.getId());
			                    	fdata.setCode(code);
			                    	fdata.setValue(getCellValue(cell));
			                    	String unit=getUnit(colheadname);
			                    	if(StrKit.notBlank(unit)) {
			                    		fdata.setUnit(unit);
			                    	}
			                    	Db.save("research_form_data", fdata.toRecord());
			                    }
			                }
			                if(StrKit.notBlank(sb.toString())) {
			                	data.setErrorImport(sb.toString());
			                }
			                Db.update("research_testgroup_data", data.toRecord());
			            }
		            }
		        }
			} catch(Exception e) {
				e.printStackTrace();
				ret.add(ResultUtil.fail("未知错误："+e.getMessage()));
				return false;
			}
			return true;
		});
		
		if(ret.size()>0) {
			StringBuilder sb=new StringBuilder();
			for(ResultVO vo:ret) {
				sb.append(vo.getMessage()).append("\r");
			}
			return ResultUtil.fail(sb.toString());
		} else {
			return ResultUtil.success();
		}
	}
	
	public boolean isGeneralColumn(XSSFRow row,int index) {
		String val=row.getCell(index).getStringCellValue();
		if(StrKit.notBlank(val)&&val.indexOf("(")>=0&&val.indexOf(")")>=0) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getColHeadName(XSSFRow row,int index) {
		return row.getCell(index).getStringCellValue();
	}
	
	public String getColName(String headname) {
		return headname.substring(headname.indexOf("(")+1, headname.indexOf(")"));
	}
	
	public String getCode(String headname) {
		return headname.substring(headname.indexOf("[")+1, headname.indexOf("]"));
	}
	
	public String getUnit(String headname) {
		if(headname.indexOf("<")>=0&&headname.indexOf(">")>=0) {
			return headname.substring(headname.indexOf("<")+1, headname.indexOf(">"));
		} else {
			return null;
		}
	}
	
	public String getCellValue(XSSFCell cell) {
		String ret=null;
		if(cell!=null) {
			log.info(cell.getCellStyle().getDataFormatString()+"---celltype:"+cell.getCellType());
			switch(cell.getCellType()) {
				case XSSFCell.CELL_TYPE_NUMERIC:
					if(org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
						Date da=org.apache.poi.ss.usermodel.DateUtil.getJavaDate(cell.getNumericCellValue());
						Calendar cal = Calendar.getInstance();
						cal.setTime(da);
						if(cal.get(Calendar.HOUR)==0&&cal.get(Calendar.MINUTE)==0&&cal.get(Calendar.SECOND)==0) {
							ret=DateKit.toStr(da, "yyyy-MM-dd");
						} else {
							ret=DateKit.toStr(da, "yyyy-MM-dd HH:mm:ss");
						}
					} else {
						cell.setCellType(XSSFCell.CELL_TYPE_STRING);
						ret=cell.getStringCellValue()+"";
					}
					break;
				case XSSFCell.CELL_TYPE_STRING:
					ret=cell.getStringCellValue();
					break;
				case XSSFCell.CELL_TYPE_BOOLEAN:
					ret=cell.getBooleanCellValue()+"";
					break;
				case Cell.CELL_TYPE_BLANK: // 空值
		            ret = null;
		            break;
				default:
					cell.setCellType(XSSFCell.CELL_TYPE_STRING);
					ret=cell.getStringCellValue();	
			}
		}
		log.info("---ret:"+ret);
		return ret;
	}
	
	public Map<String, String> getFormData(Integer dataid) {
		Map<String, String> map=new HashMap<String, String>();
		List<ResearchFormData> list=ResearchFormData.dao.find("select * from research_form_data where dataid=?",dataid);
		
		for(ResearchFormData data:list) {
			if(StrKit.notBlank(data.getCode())) {
				if(!map.containsKey(data.getCode())) {
					map.put(data.getCode(), StrKit.notBlank(data.getValue())?data.getValue():"");
				}
			}
		}
		
		return map;
	}

	public ResearchTestgroupData getResearchTestgroupData(String patientid){
		ResearchTestgroupData researchTestgroupData = ResearchTestgroupData.dao.findFirst("SELECT * ," +
				"(select top 1 name_zh from syscode where research_testgroup_data.sex=syscode.code and syscode.type='0001') as sexdisplay " +
				"FROM research_testgroup_data WHERE patientid=?", patientid);
		return researchTestgroupData;
	}

	public void savePatient(ResearchTestgroupData data,String patientidOld){
		Db.update("UPDATE research_testgroup_data set patientname=?,patientid=?,age=?,sex=?,birthdate=? WHERE patientid=?",
				data.getPatientname(),data.getPatientid(),data.getAge(),data.getSex(),data.getBirthdate(),patientidOld);
	}
	public List<Record> findSRTemplateAndComponent(String name){
		List<Record> ret=new ArrayList<Record>();
		String sql="";
		String sql1="";
		if(name!=null&&!"".equals(name)){
			sql="select top 1000 * from research_form where name like '"+name+"%'";
			sql1="select top 1000 * from srcomponent where name like '"+name+"%'";
		}
		else{
			sql="select top 1000 * from research_form";
			sql1="select top 1000 * from srcomponent";
		}
		List<ResearchForm> researchForms = ResearchForm.dao.find(sql);
		for (ResearchForm researchForm : researchForms) {
			Record record=new Record();
			record.set("id", researchForm.get("id"));
			record.set("name", researchForm.get("name"));
			record.set("creatorname", researchForm.get("creatorname"));
			record.set("createtime", researchForm.get("createtime"));
			record.set("group", "结构化表单");
			record.set("groupid", "researchForm");
			ret.add(record);
		}

		List<SRComponent> components=SRComponent.dao.find(sql1);
		for(SRComponent component:components){
			Record record=new Record();
			record.set("id", component.get("id"));
			record.set("name", component.get("name"));
			record.set("creatorname", component.get("creatorname"));
			record.set("createtime", component.get("createtime"));
			record.set("group", "结构化组件");
			record.set("groupid", "srcomponent");
			ret.add(record);
		}
		return ret;
	}

	public boolean delFormAndComponent(String resepids,String compids){
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			boolean ret=true;
			try{
				if(StrKit.notBlank(resepids)){
					String ids[]=resepids.split(",");
					for(String id:ids){
						ResearchForm.dao.deleteById(id);
					}
				}
				if(StrKit.notBlank(compids)){
					String ids[]=compids.split(",");
					for(String id:ids){
						SRComponent.dao.deleteById(id);
						Db.delete("delete from srcomponentoption where component_id=?", id);
					}
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
				ret=false;
			}
			return ret;
		});
	}

	public List<ResearchForm> findResearchFormByIds(String ids){
		return ResearchForm.dao.find("select * from research_form where id in ("+ids+")");
	}

	public List findSRComponentByIdOrUid(String ids,String uids){
		List ret=new ArrayList();

		String sql1="select * from srcomponent ";
		String where1="";

		if(StrKit.notBlank(ids)){
			where1+=StrKit.notBlank(where1)?" or ":"";
			where1+=" id in("+ids+")";
		}

		if(StrKit.notBlank(uids)){
			where1+=StrKit.notBlank(where1)?" or ":"";
			where1+=" uid in("+uids+")";
		}
		where1=StrKit.notBlank(where1)?" where ("+where1+")":where1;

		List<SRComponent> list1=SRComponent.dao.find(sql1+where1);

		ret.add(list1);
		List<Srcomponentoption> list2=Srcomponentoption.dao.find("select * from srcomponentoption where exists "
				+ "(select null from srcomponent "+where1+" and srcomponentoption.component_id=srcomponent.id)");

		HashMap<String, ArrayList<Srcomponentoption>> map=new HashMap<String, ArrayList<Srcomponentoption>>();
		for(Srcomponentoption op:list2){
			ArrayList<Srcomponentoption> list3=map.get(op.getComponentId()+"");
			if(list3==null){
				list3=new ArrayList<Srcomponentoption>();
				map.put(op.getComponentId()+"", list3);
			}
			list3.add(op);
		}
		ret.add(map);
		return ret;
	}

	public void insertSRComponent(SRComponent src,List<Srcomponentoption> opts){
		Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			boolean ret=true;
			SRComponent sc=findSRComponentByNameOrUid(src.getName(),src.getUid());
			if(sc==null){
				ret=ret&src.save();
			} else{
				src.setId(sc.getId());
				ret=ret&src.update();
				Db.delete("delete from srcomponentoption where component_id=?", src.getId());
			}
			for(Srcomponentoption op:opts){
				op.setComponentId(src.getId());
				ret=ret&op.save();
			}
			return ret;
		});
	}

	@Before(Tx.class)
	public void insertResearchForm(List<ResearchForm> resForms){

		for(ResearchForm resForm:resForms){
			ResearchForm researchForm=findResearchFormByName(resForm.getName());
			if(researchForm==null){
				resForm.save();
			}
			else {
				resForm.setId(researchForm.getId());
				resForm.update();
			}
		}

	}

	public SRComponent findSRComponentByNameOrUid(String name,String uid){
		return SRComponent.dao.findFirst("select top 1 * from srcomponent where name=? or uid=?",name,uid);
	}

	public ResearchForm findResearchFormByName(String name){
		return ResearchForm.dao.findFirst("select * from research_form where name = ?",name);
	}

    public List<Record> findUserLevel() {
		List<Record> recordlist = new ArrayList<>();

		List<ProjectUserLevel> projectUserLevels = Arrays.asList(ProjectUserLevel.values());
		for (ProjectUserLevel projectUserLevel : projectUserLevels) {
			Record record = new Record();
			record.set("level",projectUserLevel.getLevel());
			record.set("name",projectUserLevel.getName());
			recordlist.add(record);
		}
		return recordlist;
	}
}
