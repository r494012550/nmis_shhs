package com.healta.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.alibaba.fastjson.JSON;
import com.healta.constant.ReportStatus;
import com.healta.constant.SearchTimeConstant;
import com.healta.model.Followup;
import com.healta.model.User;
import com.healta.model.UserProfiles;
import com.healta.util.ParaKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class FollowupService {
	private final static String _SQL = "SELECT distinct patient.patientname, patient.patientid, patient.telephone, patient.birthdate,"
			+ " admission.admittingdiagnosis, admission.briefcondition, admission.age,"
			+ " studyorder.id AS orderid, studyorder.studyid, studyorder.studyitems, studyorder.studydatetime,"
			+ " report.checkdesc_txt, report.checkresult_txt,"
			+ " (SELECT _codenamedisplay_ FROM  syscode  WHERE syscode.code = patient.sex AND syscode.type = '0001') AS sexdisplay,"
			+ " (SELECT _codenamedisplay_ from syscode WHERE syscode.code=admission.ageunit AND syscode.type='0008') AS ageunitdisplay,"
			+ " (SELECT _codenamedisplay_ FROM syscode WHERE syscode.code=admission.patientsource AND syscode.type='0002') AS psource"
			+ ", (CASE WHEN report.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE report.reportstatus END) AS reportstatusdisplaycode "
			+ ", (SELECT  _codenamedisplay_  FROM syscode  WHERE syscode.code = (CASE WHEN report.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE report.reportstatus END) AND syscode.type = '0007') AS reportstatusdisplay"
			+ " FROM studyorder"
			+ " INNER JOIN patient ON patient.id = studyorder.patientidfk"
			+ " INNER JOIN admission ON admission.id = studyorder.admissionidfk"
			+ " INNER JOIN report ON report.studyorderfk = studyorder.id ";
	
	public List<Record> searchStudyAndReport(ParaKit kit, String syscode_lan) {
		String where=" 0=0 ";
		List<Object> list=new ArrayList<Object>();

		String from = null;
		String to = null;
		
		if (StrKit.notBlank(kit.getPara("datefrom"))) {
			from = kit.getPara("datefrom")+" 00:00:00";
		}
		if (StrKit.notBlank(kit.getPara("dateto"))) {
			to = kit.getPara("dateto")+" 23:59:59";
		}
		
		String timecon = "";
		String innerjoin="";
		if (StrKit.equals("registertime", kit.getPara("datetype"))) {
			timecon = "studyorder.regdatetime";
		} else if (StrKit.equals("studytime", kit.getPara("datetype"))) {
			timecon = "studyorder.studydatetime";
		} else if (StrKit.equals("reporttime", kit.getPara("datetype"))) {
			timecon = "report.reporttime";
		} else if (StrKit.equals("audittime", kit.getPara("datetype"))) {
			timecon = "report.audittime";
		} else if (StrKit.equals("appointmenttime", kit.getPara("datetype"))) {
			timecon = "studyorder.appointmenttime";
		} else if (StrKit.equals("followup_datetime", kit.getPara("datetype"))) {
			timecon = "followup.followup_datetime";
			innerjoin+=" INNER JOIN followup ON studyorder.id=followup.orderid ";
		}

		if (StrKit.notBlank(from)) {
			where += " and " + timecon + " >=? ";
			list.add(from);
		}
		if (StrKit.notBlank(to)) {
			where += " and " + timecon + " <=? ";
			list.add(to);
		}
		
		if (StrKit.notBlank(kit.getPara("followup_label"))) {
			String reportlabelIds="";
			String[] labels=kit.getPara("followup_label").split(",");
			for(String label:labels){
				if(label.indexOf("_")>=0){
					reportlabelIds+=label.substring(0, label.indexOf("_"))+",";	
				}
			}
			innerjoin+=" left JOIN reportlabel ON report.id=reportlabel.reportfk  ";
			where += " and labelfk in(" + reportlabelIds.substring(0, reportlabelIds.length()-1) + ") ";
		}
		
		if (StrKit.notBlank(kit.getPara("reportdoctor"))) {
			where += " and reportphysician=? ";
			list.add(kit.getPara("reportdoctor"));
		}
		
		if (StrKit.notBlank(kit.getPara("quicksearch"))) {
			innerjoin+=" INNER JOIN quicksearch ON studyorder.id=quicksearch.studyorderkey ";
			where = " CONTAINS(quicksearch.content, ?)";
			list=new ArrayList<Object>();
			list.add("\"" + kit.getPara("quicksearch").replaceAll(" ", "") + "\"");
		}
		if (StrKit.notBlank(kit.getPara("followup"))&&!StrKit.equals("followup_datetime", kit.getPara("datetype"))) {
			innerjoin+=" INNER JOIN followup ON studyorder.id=followup.orderid ";
		}
		where+=" and report.reportstatus='"+ReportStatus.FinalResults+"' ";
		
		String sql = _SQL.replaceAll("_codenamedisplay_", syscode_lan)+innerjoin+" where ";
		
		return Db.find(sql+where, list.toArray());
	}
	
	public List<Followup> findFollowup(Integer orderid){
		return Followup.dao.find("select *,(SELECT name_zh FROM syscode WHERE syscode.code=followup.followup_way AND syscode.type='0040') "
				+ "AS followup_way_name,(SELECT name_zh FROM syscode "
				+ "WHERE syscode.code=followup.followup_consistent AND syscode.type='0041') AS "
				+ "followup_consistent_name from followup where orderid=? order by followup_datetime desc",orderid);
	}
	
	public boolean saveFollowup(Followup followup, User user) {
		boolean ret = true;
		followup.setFollowupDoctorid(user.getId());
		followup.setFollowupDoctorname(user.getName());
		if(followup.getId() == null) {
			ret = followup.remove("id").save();
		}else {
			ret = followup.update();
		}
		return ret;
	}

	public boolean deleteFollowup(Integer id) {
		boolean ret = true;
		ret = Followup.dao.deleteById(id);
		return ret;
	}
	//保存查询结果列布局
	public boolean saveDatagridColumn(HashMap<String, String> map, User user) {
		boolean ret = true;
		String mapStr = JSON.toJSONString(map);
		UserProfiles profiles = UserProfiles.dao.findFirst("SELECT * FROM userprofiles WHERE userid = ?", user.getId());
		if(profiles == null) {
			profiles = new UserProfiles();
			profiles.setUserid(user.getId());
		}
		String field = "datagrid_" + map.get("moudle") + "_" + map.get("targetid");
		profiles.set(field, mapStr);
		if(profiles.getId() == null) {
			profiles.remove("id").save();
		}else {
			profiles.update();
		}
		return ret;
	}
	public List findUserByRole(){
		//查询角色为报告医生的所有用户
				List<Record> list=Db.find("select users.id,users.name from users,dic_employee where users.employeefk=dic_employee.id and profession = 'D'");
				return list;
	}
	
	public Record findUserProfessionById(User user){
		//查询用户的职务
				Record record=Db.findFirst("select dic_employee.* from users,dic_employee where users.employeefk=dic_employee.id and users.id = ?",user.getId());
				return record;
	}
}
