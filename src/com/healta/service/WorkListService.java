package com.healta.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONObject;

import com.healta.constant.ReportStatus;
import com.healta.constant.SearchTimeConstant;
import com.healta.constant.StudyOrderStatus;
import com.healta.constant.StudyprocessStatus;
import com.healta.controller.PrintController;
import com.healta.model.Admission;
import com.healta.model.Filter;
import com.healta.model.Patient;
import com.healta.model.Report;
import com.healta.model.Reportremark;
import com.healta.model.Studyorder;
import com.healta.model.Studyprocess;
import com.healta.model.Syngoviafindingfile;
import com.healta.model.Syngoviaimage;
import com.healta.model.Syngoviareport;
import com.healta.model.User;
import com.healta.model.Xslttemplate;
import com.healta.plugin.activemq.ActiveMQ;
import com.healta.plugin.activemq.MQSubject;
import com.healta.plugin.activemq.StudyprocessOrder;
import com.healta.plugin.workforce.WorkforceServer;
import com.healta.server.dicom.PatientInfo;
import com.healta.util.IPKit;
import com.healta.util.ParaKit;
import com.healta.util.SerializeRes;
import com.healta.util.SyscodeKit;
import com.jfinal.aop.Before;
import com.jfinal.i18n.Res;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import net.sf.jasperreports.engine.JRException;

public class WorkListService {
	private final static Logger log = Logger.getLogger(WorkListService.class);

	private final static String _SQL="SELECT studyorder.id AS orderid,studyorder.studyid AS studyorderstudyid, studyorder.patientidfk,"
			+ " patient.patientname,patient.address, patient.patientid,patient.vipflag,patient.height,patient.weight,patient.idnumber,admission.patientsource, studyorder.pri,studyorder.report_assignee,"	
			+ " studyorder.modality_type,studyorder.studyitems,studyorder.status, studyorder.precedence,"//patient.bedno,"
			+ " studyorder.studydescription,studyorder.appdeptname,studyorder.accessionnumber,studyorder.numberofstudyrelatedinstances,studyorder.studyinstanceuid,"
			+ " (SELECT _codenamedisplay_ FROM  syscode  WHERE syscode.code = patient.sex   AND syscode.type = '0001') AS sexdisplay,"	
			+ " admission.age, (select _codenamedisplay_ from syscode where syscode.code=admission.ageunit and syscode.type='0008') as ageunitdisplay,"
			//+ " patient.age, (SELECT   _codenamedisplay_ FROM  syscode  WHERE syscode.code = patient.ageunit  AND syscode.type = '0006') AS ageunitdisplay,patient.inno, patient.outno,  patient.wardno,"
			+ " studyorder.appointmenttime, studyorder.regdatetime, studyorder.studydatetime, studyorder.priority, "
			+ " (SELECT  modality_name FROM  dic_modality c WHERE c.id = studyorder.modalityid) AS modalityname,"
			+ " report.id AS reportid, report.template_id AS templateId,report.checkdesc_txt,report.checkresult_txt,"
			+ " (CASE WHEN report.islocking IS NULL THEN 0 ELSE report.islocking END) AS islocking,"
			+ " (select name from users where users.id = report.lockingpeople) as lockingpeople, "
			+ " report.reporttime, report.audittime,"
			+ " (CASE WHEN report.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE report.reportstatus END) AS reportstatusdisplaycode,"
			+ " (CASE WHEN report.printcount IS NULL THEN 0 ELSE report.printcount END) AS reportprintcount,"
			+ " (SELECT  _codenamedisplay_  FROM syscode  WHERE syscode.code = (CASE WHEN report.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE report.reportstatus END) AND syscode.type = '0007') AS reportstatusdisplay,"
			+ " (SELECT  _codenamedisplay_  FROM syscode  WHERE syscode.code = studyorder.status AND syscode.type = '0005') AS orderstatus,"
			+ " (SELECT  _codenamedisplay_  FROM syscode  WHERE syscode.code = (CASE WHEN report.islocking IS NULL THEN 0 ELSE report.islocking END) AND syscode.type = '0011') AS locking,"
			+ " (select top 1 retrialviewcontent from retrialviewlist where reportid = report.id ORDER BY createtime desc) AS retrialviewcontent,"
	        + " report.auditphysician_name AS auditphysicianname, "
	        + " report.reportphysician_name AS reportphysicianname, "
	        + " report.createphysician,report.createphysician_name,report.createphysiciantime ";
			

	private final static String SEPARATOR = System.getProperty("line.separator");
	
	/**
	 *  报告搜索
	 * @param map  搜索的条件
	 * @param institutionid  机构的id
	 * @param syscode_lan  中英文
	 * @return
	 */
	public Page<Record> findStudyInfo(Map<String, String[]> map,Integer institutionid,String syscode_lan,User user) {
		List<Object> list = new ArrayList<>();

		String from = null;
		String to = null;
		// 时间选择 'T'今天(today) 'Y'昨天(yesterday) 'TD'三天(ThreeDay) 'FD'五天(FiveDay) 'W'一周  'M'一月 '
		LocalDate now=LocalDate.now();
		if (StrKit.equals(SearchTimeConstant.Today, map.get("appdate")[0])) {
			from = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate()));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() + 1));
		}else if (StrKit.equals(SearchTimeConstant.Yesterday, map.get("appdate")[0])) {
			from = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals(SearchTimeConstant.ThreeDay, map.get("appdate")[0])){
			from = now.minusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals(SearchTimeConstant.FiveDay, map.get("appdate")[0])){
			from = now.minusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals(SearchTimeConstant.Week, map.get("appdate")[0])){
			from = now.minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals(SearchTimeConstant.ThreeMonth, map.get("appdate")[0])){
			from = now.minusMonths(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}

		if (StrKit.notBlank(map.get("datefrom")[0])) {
			from = map.get("datefrom")[0]+" 00:00:00";
			if(map.get("startfromtime")!=null && StrKit.notBlank(map.get("startfromtime")[0])) {
				from = from.replace("00:00:00", map.get("startfromtime")[0]+":00");
			}
		}
		if (StrKit.notBlank(map.get("dateto")[0])) {
			to = map.get("dateto")[0]+" 23:59:59";
			if(map.get("endtotime")!=null && StrKit.notBlank(map.get("endtotime")[0])) {
				to = to.replace("23:59:59", map.get("endtotime")[0]+":00");
			}
		}
		String where = "";
		String timecon = "";
		if (StrKit.equals("registertime", map.get("datetype")[0])) {
			timecon = "studyorder.regdatetime";
		} else if (StrKit.equals("studytime", map.get("datetype")[0])) {
			timecon = "studyorder.studydatetime";
		} else if (StrKit.equals("reporttime", map.get("datetype")[0])) {
			timecon = "report.reporttime";
		} else if (StrKit.equals("audittime", map.get("datetype")[0])) {
			timecon = "report.audittime";
		} else if (StrKit.equals("appointmenttime", map.get("datetype")[0])) {
			timecon = "studyorder.appointmenttime";
		} else if (StrKit.equals("registertime", map.get("datetype")[0])) {
			timecon = "studyorder.registertime";
		}

		if (StrKit.notBlank(from)) {
			where += " and " + timecon + " >='" + from + "'";
		}
		if (StrKit.notBlank(to)) {
			where += " and " + timecon + " <='" + to + "'";
		}
		
		if(map.get("startfromtime")!=null && StrKit.notBlank(map.get("startfromtime")[0])) {
			where += " and DATEPART(hh,"+timecon+" )*60+DATEPART(mi,"+timecon+")> " +  map.get("startfromtime")[0].substring(0, 2)+"*60";
		}
		if(map.get("endtotime")!=null && StrKit.notBlank(map.get("endtotime")[0])) {
			where += " and DATEPART(hh,"+timecon+") *60+DATEPART(mi,"+timecon+")< " + map.get("endtotime")[0].substring(0, 2)+"*60";
		}
		
		if (StrKit.notBlank(map.get("studyid")) && StrKit.notBlank(map.get("studyid")[0])) {
			where += " and studyorder.studyid =?";
			list.add(map.get("studyid")[0].replaceAll(" ", ""));
		}
		if (StrKit.notBlank(map.get("patientname")) && StrKit.notBlank(map.get("patientname")[0])) {
			where += " and patient.patientname LIKE CONCAT('%',?,'%')";
			list.add(map.get("patientname")[0].replaceAll(" ", ""));
		}
		if (StrKit.notBlank(map.get("modality"))) {
			String[] modalitys = map.get("modality");
			if(modalitys.length == 1) {
				where += " and studyorder.modality_type = ?";
				list.add(map.get("modality")[0]);
			}else if(modalitys.length > 1){
				String modality = "";
				for(String str : modalitys) {
					modality += "'" + str + "',";
				}
				where += " and studyorder.modality_type in ("+modality.substring(0, modality.length()-1)+")";
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
		
		if (StrKit.notBlank(map.get("patientid")) && StrKit.notBlank(map.get("patientid")[0])) {
			where += " and patient.patientid = ?";
			list.add(map.get("patientid")[0].replaceAll(" ", ""));
		}
		if (StrKit.notBlank(map.get("patientsource")) && StrKit.notBlank(map.get("patientsource")[0])) {
			where += " and admission.patientsource = ?";
			list.add(map.get("patientsource")[0]);
		}
		if (StrKit.notBlank(map.get("outno")) && StrKit.notBlank(map.get("outno")[0])) {
			where += " and admission.outno = ?";
			list.add(map.get("outno")[0]);
		}
		if (StrKit.notBlank(map.get("inno")) && StrKit.notBlank(map.get("inno")[0])) {
			where += " and admission.inno = ?";
			list.add(map.get("inno")[0]);
		}
		if (StrKit.notBlank(map.get("reportphysician_name")) && StrKit.notBlank(map.get("reportphysician_name")[0])) {
            where += " and report.reportphysician_name LIKE CONCAT('%',?,'%')";
            list.add(map.get("reportphysician_name")[0].replaceAll(" ", ""));
        }
		if (StrKit.notBlank(map.get("auditphysician_name")) && StrKit.notBlank(map.get("auditphysician_name")[0])) {
            where += " and report.auditphysician_name LIKE CONCAT('%',?,'%')";
            list.add(map.get("auditphysician_name")[0].replaceAll(" ", ""));
        }
		
		if (StrKit.notBlank(map.get("modality_dic_reg_id")) && StrKit.notBlank(map.get("modality_dic_reg_id")[0])) {
			String modality_dic_reg_id=map.get("modality_dic_reg_id")[0].toString().replaceAll(",", "','");
			where += " and studyorder.modalityid in ('"+modality_dic_reg_id+"')";
		}
		if (StrKit.notBlank(map.get("idnumber")) && StrKit.notBlank(map.get("idnumber")[0])) {
			where += " and patient.idnumber = ?";
			list.add(map.get("idnumber")[0].replaceAll(" ", ""));
		}
		
		if (institutionid != null) {
			where += " and admission.institutionid = ?";
			list.add(institutionid);
		} else if (map.get("institutionid")!=null && StrKit.notBlank(map.get("institutionid")[0])) {
		    where += " and admission.institutionid = ?";
            list.add(map.get("institutionid")[0]);
        }
		//排班自动分配报告////////////////////////////////////////////////////////////////
		/*if (map.get("workforce")!=null && StrKit.notBlank(map.get("workforce")[0])) {
			where += " and studyorder.report_assignee = ?";
            list.add(user.getId());
		}*/
		
		//////////////////////////////////////////////////////////////////
		
		// 快速检索//////////////////////////////////////////////////////
		
		String quicksearchTable="";
		if (StrKit.notBlank(map.get("quicksearchcontent")) && StrKit.notBlank(map.get("quicksearchcontent")[0])&&
				StrKit.notBlank(map.get("quicksearchname")) && StrKit.notBlank(map.get("quicksearchname")[0])) {
			quicksearchTable=",quicksearch ";
			if(StrKit.equals("all", map.get("quicksearchname")[0])) {
				where = " and studyorder.id=quicksearch.studyorderkey and CONTAINS(quicksearch.content, ?)";
				list=new ArrayList<Object>();
				list.add("\"" + map.get("quicksearchcontent")[0].replaceAll(" ", "") + "\"");
			}
			else {
				where += " and studyorder.id=quicksearch.studyorderkey and CONTAINS(quicksearch.content, ?)";
		        list.add("\"" + map.get("quicksearchcontent")[0].replaceAll(" ", "") + "\"");
			} 
		}
		//根据用户自定义的设备类型过滤
		if(user.getModality()!=null&&StrKit.notBlank(user.getModality())) {
			where += " and studyorder.modality_type in ("+user.getModality()+")";
		}
		
		///////////////////////////////////////////////////
//		List<Record> ret=new ArrayList<Record>();
		String select =_SQL.replaceAll("_codenamedisplay_", syscode_lan);
		String sqlExceptSelect= " FROM patient,admission,studyorder" 
				+ " LEFT JOIN report ON studyorder.id = report.studyorderfk "
			//	+ " LEFT JOIN reporticd ON reporticd.reportid = report.id "
				+ " LEFT JOIN study ON study.studyorderfk = studyorder.id "+quicksearchTable
				+ " WHERE patient.id=admission.patientidfk and admission.id=studyorder.admissionidfk"+ where
				+ " ORDER BY reportstatusdisplaycode, studyorder.pri, studyorder.precedence DESC, studyorder.studydatetime, studyorder.regdatetime";
		
		return Db.paginate(Integer.valueOf(map.get("page")[0]), Integer.valueOf(map.get("rows")[0]), select, sqlExceptSelect, list.toArray());
	}
	
	public List<Record> findStudyByFavoritesId(int favorites_id,int start,int rows,String syscode_lan){
		List<Record> ret=new ArrayList<Record>();
		
//		String sql="select count(*) as size FROM  report,favoritesreport where "
//				+ "report.id=favoritesreport.report_id and favoritesreport.favorites_id=?";
//		ret.add(new Record().set("totalsize", Db.queryInt(sql, favorites_id)));
		
		String sql1 = _SQL.replaceAll("_codenamedisplay_", syscode_lan)
				+ " FROM patient,admission,studyorder"
				+ " LEFT JOIN study ON study.studyorderfk = studyorder.id"
				+ " ,report"
				+ " LEFT JOIN favoritesreport ON report.id=favoritesreport.report_id"
				+ " WHERE patient.id=admission.patientidfk and admission.id=studyorder.admissionidfk"
				+ " and studyorder.id = report.studyorderfk"
				+ " and favoritesreport.favorites_id=?" 	
				+ " ORDER BY reportstatusdisplaycode, studyorder.studydatetime, studyorder.regdatetime";
		//ret.addAll( Db.find(sql1, favorites_id,start,rows));
		
		List<Record> res=Db.find(sql1, favorites_id);
		ret.add(new Record().set("totalsize", res.size()));
		int len=start+rows;
		if(len>res.size()) {
			len=res.size();
		}
		for(int i=start;i<(len);i++) {
			ret.add(res.get(i));
		}
		return ret;
	}
	
	
	public List<Record> querySyngovia(ParaKit kit){
		List<Object> list = new ArrayList<>();

//		int start = record.get("start");
//		int rows = record.get("rows");
//		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String from = "";
		String to = "";
		String searchDates = "";
		LocalDate now=LocalDate.now();
		if (StrKit.equals("T", kit.getPara("appdate"))) {
			from = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate()));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() + 1));
		} else if (StrKit.equals("Y", kit.getPara("appdate"))) {
			from = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		} else if (StrKit.equals("TD", kit.getPara("appdate"))) {
			from = now.minusDays(3).format(DateTimeFormatter.ofPattern("yyyyMMdd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		} else if (StrKit.equals("FD", kit.getPara("appdate"))) {
			from = now.minusDays(5).format(DateTimeFormatter.ofPattern("yyyyMMdd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		} else if (StrKit.equals("W", kit.getPara("appdate"))) {
			from = now.minusDays(7).format(DateTimeFormatter.ofPattern("yyyyMMdd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		} else if (StrKit.equals("TM", kit.getPara("appdate"))) {
			from = now.minusDays(90).format(DateTimeFormatter.ofPattern("yyyyMMdd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		} else if(StrKit.notBlank(kit.getPara("datefrom"))||StrKit.notBlank(kit.getPara("dateto"))){
			from=kit.getPara("datefrom").replaceAll("-", "");
			to=kit.getPara("dateto").replaceAll("-", "");
		}
		searchDates=from+"-"+to;
		log.info("searchDates="+searchDates);
		
//		log.info("patientid="+map.get("patientid")[0]+"apppatientname="+map.get("patientname")[0]+";searchDates="+searchDates+";appmodality="+map.get("modality")[0]);
		
		String dcmurl="DICOM://"+kit.getPara("aet")+":"+PropKit.use("system.properties").get("localaet")+"@"+kit.getPara("hostname")+":"+kit.getPara("port");
		log.info("Query scu,dcmurl="+dcmurl);
		PatientInfo patientInfo=new PatientInfo();
		patientInfo.callFindWithQuery(kit.getPara("patientid"), kit.getPara("patientname"), "", searchDates, "",kit.getPara("modality"), kit.getPara("studyid"), "", "", 
				dcmurl, "");
		ArrayList<Record> records=patientInfo.getStudyList();
		List<Record> ret=new ArrayList<Record>();
		ret.add(new Record().set("totalsize", records.size()));
		ret.addAll(records);
		
		return ret;
	}
	
	/**
	 * 获取常用条件列表
	 */
	
	public List<Record> findFilters(Integer id,Integer creator,String filterType,boolean workforce_support,String locale) {
		List<Record> ret=new ArrayList<Record>();
		if(workforce_support) { 
		Set<String> attrs=(new Filter()).getAttrNames();
			SyscodeKit.INSTANCE.getCodes("0007").forEach(x->{
				Record re=new Record();
				for(String attr:attrs) {
					re.set(attr, null);
				}
				re.set("reportstatus", x.getCode());
				re.set("description", SyscodeKit.INSTANCE.getCodeDisplayname(x, locale));
				re.set("appdate", "T");
				re.set("datetype", "studytime");
				re.set("modality", "");
				re.set("patientsource", "");
				re.set("datasource", "localdatabase");
				re.set("workforce", "1");
				re.set("_group", "我的报告");
				ret.add(re);
			});
		}
		
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

		String sql = "SELECT  *,case when modality is null then '' else modality end as modality,"
				+ "(SELECT dic_institution.name FROM dic_institution WHERE dic_institution.id=filter.institutionid) as institutionname"
				+ "  FROM  filter WHERE 0 = 0 " + where +" ORDER BY createtime DESC ";
		List<Record> list= Db.find(sql);
		list.forEach(record->{
			record.set("reportstatus_display", SyscodeKit.INSTANCE.getCodeDisplay("0007",record.getStr("reportstatus"), locale));
			record.set("appdate_display", SyscodeKit.INSTANCE.getCodeDisplay("0022",record.get("appdate"), locale));
			record.set("patient", SyscodeKit.INSTANCE.getCodeDisplay("0002",record.get("patientsource"), locale));
			record.set("datetype_display", SyscodeKit.INSTANCE.getCodeDisplay("0021",record.get("datetype"), locale));
			if(workforce_support) {
				record.set("_group", "自定义");
				record.set("workforce", "0");
			}
		});
		ret.addAll(list);
		return ret;
	}
	
	public List getFindings(String studyid,int viaid) throws IOException, DocumentException, TransformerException{
		
		//FileUtils.getFile("D:\\image\\"+studyid+".xml");
		
		List ret=new ArrayList();
		boolean update=true;
//		List<Record> records=new ArrayList<Record>();
		
		JSONArray records =new JSONArray();
		Syngoviareport svr=Syngoviareport.dao.findById(viaid);
		
		if(svr!=null&&svr.getDelflag()==0){
			update=false;
		}
		if(update){
			Syngoviareport via=Syngoviareport.dao.findFirst("select * from syngoviareport where studyid='"+studyid+"' and delflag=0");
			
			if(via!=null){
				
				SAXReader reader = new SAXReader();
				
//				File cdafile=new File(via.getCdafile());
//				String cdastr=FileUtils.readFileToString(cdafile);
//				Db.delete("delete from syngoviaimage where studyid=?",studyid);
//				Db.delete("delete from syngoviafindingfile where studyid=?",studyid);
//				FileUtils.deleteQuietly(new File(cdafile.getParent()+"//image"));
//				FileUtils.deleteQuietly(new File(cdafile.getParent()+"//finding"));
//				List list=parseCDA(cdastr,studyid,cdafile.getParent());
				List<Syngoviafindingfile> findings=Syngoviafindingfile.dao.find("select * from syngoviafindingfile where studyid=?", studyid);
				String reportname=via.getReportname();
				
				for(int i=0;i<findings.size();i++){
					
	//				Record record=new Record();
					try{
						
					
						Syngoviafindingfile svff=findings.get(i);
						Document doc=reader.read(new File(PropKit.use("system.properties").get("sysdir")+"\\"+svff.getFindingfile()));
						JSONObject record=new JSONObject();
						record.put("viaid", via.getId());
						record.put("finding", doc.asXML());
						String findingname=svff.getFindingname();
						record.put("findingname", findingname);
						Xslttemplate xslt=Xslttemplate.dao.getXsltByName(findingname,reportname,PropKit.use("system.properties").get("via_version"));
						if(xslt!=null&&xslt.getXslt()!=null){
							String html=transform(doc.asXML(),xslt.getXslt());
	//						System.out.println("html=="+html);
							record.put("html", html);
						}
						else{
							record.put("html", doc.asXML());
						}
						
						if(xslt!=null&&xslt.getXsltSr()!=null){
							String htmlsr=transform(doc.asXML(),xslt.getXsltSr());
	//						System.out.println("htmlsr=="+htmlsr);
							record.put("htmlsr", htmlsr);
						}
						else{
							record.put("htmlsr", doc.asXML());
						}
						
						String str[]=getFindingValueAndSource(doc,xslt);
						record.put("displayvalue", str[0]);
						record.put("source", str[1]);
		//				records.add(record);
						records.put(record);
					}
					catch(Exception ex){
						
					}
				}
				
				
				List<Syngoviaimage> images=Syngoviaimage.dao.find("select * from syngoviaimage where studyid=? and delflag=0", studyid);
				JSONArray imagesjson=new JSONArray();
				for(Syngoviaimage image:images){
					
					BufferedImage sourceImg =ImageIO.read(new File(PropKit.use("system.properties").get("sysdir")+"\\"+image.getImagefile()));
					
					JSONObject im=new JSONObject();
	            	im.put("imageid", image.getId());
	            	im.put("studyid", image.getStudyid());
	            	im.put("width", sourceImg.getWidth());
	            	im.put("height", sourceImg.getHeight());
	            	imagesjson.put(im);
				}
				
				ret.add(records);
				ret.add(imagesjson);
				ret.add(via.getPdffile());
			}
		
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
		return content;
}
	
	/*
     * FindingType example:
     * <entry>
            <observation classCode="OBS" moodCode="EVN">
              <templateId root="2.16.840.1.113883.10.20.6.2.12"/>
              <code code="FindingType" codeSystemName="99SMS_SY" displayName="FindingType"/>
              <value xsi:type="ED">Cardiac_Stenosis</value>
            </observation>
        </entry>
     * 
     * 
     */
//    public String getFindingName(Document doc){
//    	String name=null;
//    	try{
//	    	Element entry=(Element)doc.getRootElement().elements("entry").get(0);
//	    	Element obs=entry.element("observation");
//	    	if("FindingType".equals(obs.element("code").attributeValue("code"))){
//	    		name=obs.element("value").getText();
//	    	}
//    	}
//    	catch(Exception ex){
//    		
//    	}
//    	return name;
//    }
    
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
	
	public List parseCDA(String cdastr,String studyid,String path) throws DocumentException, IOException{
		SAXReader reader = new SAXReader();
		List<Document> docs=new ArrayList<Document>();
//		List<Integer> imageids=new ArrayList<Integer>();
		
		JSONArray images=new JSONArray();
		
			Document doc=reader.read(new StringReader(cdastr));
			Element root = doc.getRootElement();
			Element com=(Element)root.element("component").element("structuredBody").elements("component").get(1);

			Element sec=com.element("section");
			String title=sec.elementText("title");
			List entries=sec.elements("entry");
			Document finding=null;
			
			for(int i=0;i<entries.size();i++){
                Element en=(Element)entries.get(i);
                //提取图片
                Element e_image=en.element("observationMedia");
                if(e_image!=null){
//                	System.out.println(e_image.attributeValue("ID"));
                	
                	String imagefile=path+"\\image\\"+studyid+"_"+e_image.attributeValue("ID")+".png";
					FileUtils.writeByteArrayToFile(new File(imagefile), Base64.decodeBase64(e_image.element("value").getText()));
					
					Syngoviaimage image=new Syngoviaimage();
                	image.setStudyid(studyid);
                	image.setImagefile(imagefile);
                	image.save();
                	
                	
                	JSONObject im=new JSONObject();
                	im.put("imageid", image.getId());
                	im.put("studyid", image.getStudyid());
                	images.put(im);
                }
                
                //提取finding
                Element obs=en.element("observation");
                if(obs!=null){
                	
                	if("FindingType".equals(obs.element("code").attributeValue("code"))){
                		System.out.println(obs.element("value").getText());
                		if(finding!=null){
//                			System.out.println(finding.asXML());
                			docs.add(finding);
                		}
                		
                		finding=DocumentHelper.createDocument();
                    	Element roote=finding.addElement("entries","urn:hl7-org:v3").addNamespace("voc", "urn:hl7-org:v3/voc").addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
//                    	System.out.println(en.declaredNamespaces());
                    	roote.add((Element)en.clone());
                    	
                    	
                	}
                	else{
	                	if(finding!=null){
	                		finding.getRootElement().add((Element)en.clone());
	                	}
                	}
                }
                
                
			}
			if(finding!=null){
//    			System.out.println(finding.asXML());
    			docs.add(finding);
    		}
//			System.out.println(docs.size());
			
			List ret=new ArrayList();
			ret.add(docs);
			ret.add(images);
			ret.add(title);
		return ret;
	}
	
	
	public boolean lockReport(String studyid,String userid) {
		boolean ret=true;
		try{
			Db.update("update studyorder set islocking=1,lockingpeople=? where studyid=?",userid,studyid);
		}
		catch (Exception e) {
			// TODO: handle exception
			ret=false;
		}
		
		return ret;

	}
	
	@Before(Tx.class)
	public boolean deleteReport(Integer reportid,String studyid){
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret = true;
			try {
				Db.delete("delete from report where id = ?", reportid);
				Db.delete("delete from reportrace where id = ?", reportid);
//				Db.delete("delete from structreport where reportid in(select id from report where studyid=?)", studyid);
				Db.delete("delete from structreport where reportid = ?", reportid);
			}catch(Exception e) {
				ret = false;
				e.printStackTrace();
			}
			
			return ret;
		});
	}
	
	//将via数据 保存至本地
	public void retrieveData(Record re,User user) throws ParseException{
		Patient pa=Patient.dao.getPatientById(re.getStr("patientid"));
		if(pa==null){
			pa=new Patient();
			pa.setPatientid(re.getStr("patientid"));
			pa.setPatientname(re.getStr("patientname"));
			pa.setSex(re.getStr("sex"));
			pa.setBirthdate(re.getStr("birthdate"));
			pa.setCreator(user.getUsername());
			pa.remove("id").save();
		}
		
		
		Admission adm=Admission.dao.getAdmissionByAdmissionid(re.getStr("accessionnumber"));
		if(adm==null){
			adm=new Admission();
			adm.setPatientidfk(pa.getId());
			adm.setAdmissionid(re.getStr("accessionnumber"));
			adm.setCreator(user.getUsername());
//			adm.setAccessionnumber(re.getStr("accessionnumber"));
			adm.remove("id").save();
		}
		
		String studyinstanceuid =re.getStr("studyinstanceuid");
		String studyid=re.getStr("studyid");
		log.info(studyinstanceuid+"====studyinstanceuid=="+studyinstanceuid.replaceAll("\\.", ""));
		Studyorder so=Studyorder.dao.getStudyorderByUid(studyinstanceuid);
		log.info("-------Studyorder-------"+so);
		if(so==null){
//			SimpleDateFormat format=new SimpleDateFormat("yyyyMMddhhmmss.SSSSSS");
			SimpleDateFormat format=new SimpleDateFormat("yyyyMMddhhmmss");
			
			so=new Studyorder();
			so.setPatientidfk(pa.getId());
			so.setAdmissionidfk(adm.getId());
			so.setStudyinstanceuid(studyinstanceuid);
			so.setStudyid(studyid);
			so.setStudydescription(re.getStr("studydescription"));
			so.setModalityType(re.getStr("modalitiesinstudy"));
			so.setAccessionnumber(re.getStr("accessionnumber"));
			log.info("----"+re.getStr("numberofstudyrelatedinstances"));
			so.setNumberofstudyrelatedinstances(re.getStr("numberofstudyrelatedinstances")!=null?Integer.valueOf(re.getStr("numberofstudyrelatedinstances")):null);
			so.setPatientid(re.getStr("patientid"));
			so.setStudydatetime(format.parse(re.getStr("studydatetime")));
			so.setStatus(StudyOrderStatus.completed);
			so.setCreator(user.getName());
	  		so.setCreatorname(user.getUsername());
			so.remove("id").save();
		}
		
		//发送检查进程
  		ActiveMQ.sendObjectMessage(new StudyprocessOrder(new Studyprocess()
  				.set("patientfk", pa.getId())
  				.set("admissionfk", adm.getId())
				.set("studyorderfk", so.getId())
				.set("status", StudyprocessStatus.registered)
				.set("operator", user.getUsername())
				.set("operatorname", user.getName())), MQSubject.STUDYPROCESS.getSendName(), StrKit.getRandomUUID(), 4);
	}
	
	/**
	 * 获取病人备注
	 */
	public String getRemarks(Integer patientidfk) {
		List<Reportremark> remarks=Reportremark.dao.find("SELECT * FROM reportremark WHERE patientidfk = ?", patientidfk);
		String remarkcontent="";
		for(Reportremark re:remarks) {
			if(StringUtils.isNotBlank(re.getRemarkcontent())) {
				remarkcontent += re.getRemarkcontent()+" ";
			}
		}
		return remarkcontent.trim();
	}
	
	/**
	 * 标签查询
	 */
	public List<Record> findStudyByLabel(ParaKit kit,String syscode_lan){
		List<Object> list = new ArrayList<>();
		String reportlabelIds="";
		String reportId="";
		boolean labelflag = false;
		if (StrKit.notBlank(kit.getPara("publiclabel"))) {
			labelflag = true;
			String[] labels=kit.getPara("publiclabel").split(",");
			for(String label:labels){
				if(label.indexOf("_")>=0){
					reportlabelIds+=label.substring(0, label.indexOf("_"))+",";	
				}
			}
		}
		if (StrKit.notBlank(kit.getPara("privatelabel"))) {
			labelflag = true;
			String[] labels=kit.getPara("privatelabel").split(",");
			for(String label:labels){
				if(label.indexOf("_")>=0){
					reportlabelIds+=label.substring(0, label.indexOf("_"))+",";
				}
			}
		}
		
		if(StrKit.notBlank(reportlabelIds)) {
			List<Record> reportlabel=Db.find("SELECT reportfk FROM reportlabel WHERE labelfk in ("+reportlabelIds.substring(0, reportlabelIds.length()-1)+")").stream().distinct().collect(Collectors.toList());
//			System.out.println(reportlabel);
			for(Record re:reportlabel) {
				reportId+=re.getInt("reportfk")+",";
			}
		}
		
		String from = null;
		String to = null;
		// 时间选择 'T'今天(today) 'Y'昨天(yesterday) 'TD'三天(ThreeDay) 'FD'五天(FiveDay) ' 'W'一周 'M'一月 ''不限(all)
		LocalDate now=LocalDate.now();
		if (StrKit.equals("T", kit.getPara("appdate"))) {
			from = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate()));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() + 1));
		}else if (StrKit.equals("Y", kit.getPara("appdate"))) {
			from = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals("TD", kit.getPara("appdate"))){
			from = now.minusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals("FD", kit.getPara("appdate"))){
			from = now.minusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals("W",kit.getPara("appdate"))){
			from = now.minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals("TM", kit.getPara("appdate"))){
			from = now.minusMonths(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}

		if (StrKit.notBlank(kit.getPara("datefrom"))) {
			from = kit.getPara("datefrom")+" 00:00:00";
		}
		if (StrKit.notBlank(kit.getPara("dateto"))) {
			to = kit.getPara("dateto")+" 23:59:59";
		}
		String where = "";
		String timecon = "";
		if (StrKit.equals("registertime", kit.getPara("datetype"))) {
			timecon = "studyorder.regdatetime";
		} else if (StrKit.equals("studytime",kit.getPara("datetype"))) {
			timecon = "studyorder.studydatetime";
		} else if (StrKit.equals("reporttime", kit.getPara("datetype"))) {
			timecon = "report.reporttime";
		} else if (StrKit.equals("appointmenttime", kit.getPara("datetype"))) {
			timecon = "studyorder.appointmenttime";
		}

		if (StrKit.notBlank(from)) {
			where += " and " + timecon + " >='" + from + "'";
		}
		if (StrKit.notBlank(to)) {
			where += " and " + timecon + " <='" + to + "'";
		}
		
		if(StrKit.notBlank(reportId)) {
//			System.out.println("reportId:"+reportId.substring(0, reportId.length()-1));
			where += " and report.id in ("+reportId.substring(0, reportId.length()-1)+")";
		}else if(labelflag) {
			where += " and report.id = 0";
		}
		
		// ICD10
        if (StrKit.notBlank(kit.getPara("icd10"))) {
            String icd10String = kit.getPara("icd10");
            String[] icd10s = icd10String.split(",");
            String icd10 = "";
            for(String str : icd10s) {
                icd10 += "'" + str + "',";
            }
            // 疾病名称
            where += " and reporticd.icd_id in ("+icd10.substring(0, icd10.length()-1)+") ";
        }
		
		List<Record> ret=new ArrayList<Record>();
		
		        
		String sql1 =_SQL.replaceAll("_codenamedisplay_", syscode_lan)
				+ " FROM patient,admission,studyorder" 
				+ " LEFT JOIN report ON studyorder.id = report.studyorderfk "
				+ " LEFT JOIN reporticd ON reporticd.reportid = report.id "
				+ " LEFT JOIN study ON study.studyorderfk = studyorder.id "
				+ " WHERE patient.id=admission.patientidfk and admission.id=studyorder.admissionidfk"+ where
				+ " ORDER BY reportstatusdisplaycode, studyorder.pri, studyorder.precedence DESC, studyorder.studydatetime, studyorder.regdatetime";
		List<Record> res=Db.find(sql1, list.toArray());
		//log.info(res.size());
		ret.add(new Record().set("totalsize", res.size()));
	
		int page=Integer.valueOf(kit.getPara("page"));
		int rows=Integer.valueOf(kit.getPara("rows"));
		int start = (page-1) * rows;
		
		int len=start+rows;
		if(len>res.size()) {
			len=res.size();
		}
		for(int i=start;i<(len);i++) {
			ret.add(res.get(i));
		}
		return ret;
	}
	
	/**
	 * 全文检索
	 */
	public List<Record> findStudyByFulltext(Map<String, String[]> map,String syscode_lan){
		List<Object> list = new ArrayList<>();
		List<Record> ret=new ArrayList<Record>();
		String sqlpara = "";
		String where = "";
		String order = "";
		if (StrKit.notBlank(map.get("plaintext"))) {
			log.info(map.get("plaintext")[0]);
			String plaintext = map.get("plaintext")[0].trim();
			log.info("plaintext:"+plaintext);
			switch(map.get("type_fulltext")[0]){
				case "contians":
					sqlpara = _SQL;
					order = " ORDER BY reportstatusdisplaycode, studyorder.studydatetime, studyorder.regdatetime";
					plaintext = plaintext.replaceAll("\\s*[,，]+\\s*", ",").replaceAll(",$", "")
							.replaceAll("\\s+", " AND ").replaceAll("[,，]+", " OR ");
					String arr[]=plaintext.split(" ");
					for(int i=0,size=arr.length;i<size;i++){
						if(StrKit.notBlank(arr[i])&&!StrKit.equals(arr[i], "AND")&&!StrKit.equals(arr[i], "OR")){
							arr[i]="\""+arr[i]+"*\"";
						}
					}
					plaintext=StringUtils.join(arr, " ");
					log.info("plaintext:"+plaintext);
					where += " and (CONTAINS(report.checkdesc_txt, ?)" + " or CONTAINS(report.checkresult_txt, ?))";
					list.add(plaintext);
					list.add(plaintext);
					break;
				case "freetext":
					sqlpara = "SELECT DISTINCT uniontable1.sumrank, "+_SQL.substring(_SQL.indexOf("distinct")+"distinct".length())+"INNER JOIN (select uniontable.[key],sum(uniontable.rank) as sumrank from"
							+"  (  select [key],[rank] from  FREETEXTTABLE(report, checkdesc_txt, ?) AS KEY_TBL"
							+"   union all"
							+"   select [key],[rank] from  FREETEXTTABLE(report, checkresult_txt, ?) AS KEY_TBL1) as uniontable group by uniontable.[key]) as uniontable1"
							+" ON report.id =uniontable1.[key]";
					order = " ORDER BY uniontable1.sumrank desc,reportstatusdisplaycode, studyorder.studydatetime, studyorder.regdatetime";
//					where += " and (FREETEXT(report.checkdesc_txt, ?)" + " or FREETEXT(report.checkresult_txt, ?))";
					list.add(plaintext);
					list.add(plaintext);
					break;
				case "all":
				
					break;
				default:
					
			}
			
			//where += " and (CONTAINS(report.checkdesc_txt,'"+plaintext+"')" + " or CONTAINS(report.checkresult_txt,'"+plaintext+"'))";
//			where += " and (CONTAINS(report.checkdesc_txt, ?)" + " or CONTAINS(report.checkresult_txt, ?))";
//			list.add(plaintext);
//			list.add(plaintext);
		}
		
		if (StrKit.notBlank(map.get("modality"))) {
			String[] modalitys = map.get("modality");
			if(modalitys.length == 1) {
				where += " and studyorder.modality_type = ?";
				list.add(map.get("modality")[0]);
			}else if(modalitys.length > 1){
				String modality = "";
				for(String str : modalitys) {
					modality += "'" + str + "',";
				}
				where += " and studyorder.modality_type in ("+modality.substring(0, modality.length()-1)+")";
			}	
		}
		
		String from = null;
		String to = null;
		// 时间选择 'T'今天(today) 'Y'昨天(yesterday) 'TD'三天(ThreeDay) 'FD'五天(FiveDay) ' 'W'一周 'M'一月 ''不限(all)
		LocalDate now=LocalDate.now();
		if (StrKit.equals("T", map.get("appdate")[0])) {
			from = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate()));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() + 1));
		}else if (StrKit.equals("Y", map.get("appdate")[0])) {
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
		}else if(StrKit.equals("TM", map.get("appdate")[0])){
			from = now.minusMonths(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}else if(StrKit.equals("YEAR", map.get("appdate")[0])){
			from = now.minusYears(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));//format.format(new Date(today.getYear(), today.getMonth(), today.getDate() - 1));
			to = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}

		if (StrKit.notBlank(map.get("datefrom")[0])) {
			from = map.get("datefrom")[0]+" 00:00:00";
		}
		if (StrKit.notBlank(map.get("dateto")[0])) {
			to = map.get("dateto")[0]+" 23:59:59";
		}
		String timecon = "";
		if (StrKit.equals("registertime", map.get("datetype")[0])) {
			timecon = "studyorder.regdatetime";
		} else if (StrKit.equals("studytime", map.get("datetype")[0])) {
			timecon = "studyorder.studydatetime";
		} else if (StrKit.equals("reporttime", map.get("datetype")[0])) {
			timecon = "report.reporttime";
		} else if (StrKit.equals("appointmenttime", map.get("datetype")[0])) {
			timecon = "studyorder.appointmenttime";
		}

		if (StrKit.notBlank(from)) {
			where += " and " + timecon + " >='" + from + "'";
		}
		if (StrKit.notBlank(to)) {
			where += " and " + timecon + " <='" + to + "'";
		}
		
		String sql1 = sqlpara.replaceAll("_codenamedisplay_", syscode_lan)
				+ " FROM patient,admission,studyorder" 
				+ " LEFT JOIN report ON studyorder.id = report.studyorderfk "
				+ " LEFT JOIN reporticd ON reporticd.reportid = report.id "
				+ " LEFT JOIN study ON study.studyorderfk = studyorder.id "
				+ " WHERE patient.id=admission.patientidfk and admission.id=studyorder.admissionidfk"+ where + order;
		List<Record> res=Db.find(sql1, list.toArray());
		//log.info(res.size());
		ret.add(new Record().set("totalsize", res.size()));
	
		int start = 0;
		int len = res.size();
		
		if(map.get("page")!=null&&map.get("rows")[0]!=null) {
			int page=Integer.valueOf(map.get("page")[0]);
			int rows=Integer.valueOf(map.get("rows")[0]);
			start = (page-1) * rows;
			len=start+rows;
			if(len>res.size()) {
				len=res.size();
			}
		}
	
		for(int i=start;i<(len);i++) {
			ret.add(res.get(i));
		}
		return ret;
	}
	
	/**
	 * 根据patientid查询历史检查
	 * @param patientid
	 * @param orderid
	 * @param syscode_lan
	 * @return
	 */
	public List<Record> findHistoryStudyorder(String patientid, Integer orderid, String syscode_lan){
		List<Record> list = null;
		String sqlParam = "SELECT studyorder.id AS orderid, studyorder.studyid, patient.patientname, patient.patientid, admission.patientsource"
				+ ", studyorder.modality_type, studyorder.studyitems, studyorder.status, studyorder.regdatetime, studyorder.studydatetime"
				+ ", (SELECT _codenamedisplay_ FROM syscode WHERE syscode.code = patient.sex AND syscode.type = '0001') AS sexdisplay"
				+ ", (SELECT _codenamedisplay_ FROM syscode WHERE syscode.code = admission.patientsource AND syscode.type='0002') as patientsourcedisplay"
				+ ", (SELECT _codenamedisplay_ FROM syscode  WHERE syscode.code = studyorder.status AND syscode.type = '0005') AS orderstatus"
				+ ", (SELECT modality_name FROM dic_modality WHERE dic_modality.id = studyorder.modalityid) AS modalityname"
				+ ", report.id AS reportid"
				+ ", (CASE WHEN report.reportstatus IS NULL THEN "+ReportStatus.Noresult+" ELSE report.reportstatus END) AS reportstatusdisplaycode"
				+ " FROM studyorder"
				+ " LEFT JOIN patient ON patient.id = studyorder.patientidfk"
				+ " LEFT JOIN admission ON admission.id = studyorder.admissionidfk"
				+ " LEFT JOIN report ON report.studyorderfk = studyorder.id"
				+ " WHERE studyorder.patientid = ? AND studyorder.id != ?"
				+ " ORDER BY studyorder.status DESC, studyorder.studydatetime";
		String sql = sqlParam.replaceAll("_codenamedisplay_", syscode_lan);
		list = Db.find(sql, patientid, orderid);
		return list;
	}
	
	/**
	 *  批量导出pdf文件
	 * @param issr  默认为 0
	 * @param reportid    reportid
	 * @param reportName    使用报表的名称
	 * @param patientname   病人的姓名
	 * @param request
	 * @return
	 * @throws JRException
	 * @throws SQLException
	 */
	public File exportReport(Integer reportid, HttpServletRequest request) throws JRException, SQLException {
		File file = null;
	    Report report = Report.dao.findById(reportid);
	    if(StrKit.notBlank(report.getRelativePath())){
	    	file=new File(PropKit.use("system.properties").get("sysdir") + "\\"+report.getRelativePath());
	    } else{
	    	byte[] b=PrintController.sv.generatePdfReport(report, request.getServerPort(), 
		    		request.getScheme()+"://"+IPKit.getServerIP(request)+":"+request.getServerPort()+request.getContextPath());
	    	if(b!=null){
	    		LocalDate now=LocalDate.now();
	    		String relativePath = "report\\"+now.getYear()+"\\"+now.getMonthValue()+"\\"+now.getDayOfMonth()+"\\"+report.getId()+".pdf";
	        	String pdffilename=PropKit.use("system.properties").get("sysdir") + "\\" + relativePath;
	        	file=new File(pdffilename);
	    		try {
					FileUtils.writeByteArrayToFile(file, b);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		if(file.exists()){
	    			report.setRelativePath(relativePath);
	    			report.update();
	    		}
	    	}
	    }
        return file;
    }
	
	public List<Record> getDateSource(SerializeRes res){
		List<Record> ret=new ArrayList<Record>();
		ret.add(new Record().set("value", "localdatabase").set("name", res.get("wl.local")));
		Db.find("select * from dic_modality where role=? AND deleted = 0","workstation").stream().forEach(x->{
			ret.add(new Record().set("value",x.getStr("storagescp")).set("name", x.getStr("modality_name")).
					set("aet", x.getStr("storagescp")).
					set("port", x.getStr("storagescpport")).
					set("hostname", x.getStr("hostname")));
		});
		
		return ret;
	}
	
	public boolean returnReport(Integer orderid,String report_status){
		Integer userid=null;
		if(StrKit.equals(WorkforceServer.INSTANCE.getConfiguration().getEnableRrred(), "1")){
			List<Record> list=Db.find("select a.id,a.report_assignee as reassgin_physicianid,a.studydatetime,(select c.coefficient from dic_examitem c where b.item_id=c.id and b.orderid=a.id) as coefficient,"
					+ "b.modality,b.item_id,a.modalityid from studyorder a,studyitem b where a.id=b.orderid and a.id=?",orderid);
			
			int examitem_coefficient= list.stream().mapToInt(x->x.getInt("coefficient")).sum();
    		String examitemids=list.stream().map(x->x.getStr("item_id")).collect(Collectors.joining(","));
    		String modality=list.get(0).getStr("modality");
    		Integer modalityid=list.get(0).getInt("modalityid");
			Record re= new Record();
			re.set("modality", modality);
			re.set("modalityid", modalityid);
			re.set("examitemids", examitemids);
			
			if(StrKit.equals(report_status, ReportStatus.Noresult)||StrKit.equals(report_status, ReportStatus.Created)){
				userid=WorkforceServer.INSTANCE.getReportAssignee(re.getInt("coefficient"), orderid, re);
			} else if(StrKit.equals(report_status, ReportStatus.Preliminary)){
				userid=WorkforceServer.INSTANCE.getPreAuditPhysicianAssignee(re.getInt("coefficient"), orderid, re);
			} else if(StrKit.equals(report_status, ReportStatus.Preliminary_review)){
				userid=WorkforceServer.INSTANCE.getAuditPhysicianAssignee(re.getInt("coefficient"), orderid, re);
			}
		}
		Studyorder order=new Studyorder();
		order.setId(orderid);
		order.setReportAssignee(userid);
		return order.update();
	}
	
	/**
	 * excel写入内容
	 */
	public String[][] excelContent(List<Record> list, String[] title, String[] field) {
	    log.info("excel写入的标题长度为：" + title.length + "，内容为：" + JSONObject.valueToString(title));
	    log.info("excel写入的列名长度为：" + field.length + "，内容为：" + JSONObject.valueToString(field));
	    String[][] content = new String[list.size()][title.length];
        for (int i = 0; i < list.size(); i++) {
            Record record = list.get(i);
            for (int j = 0; j < field.length; j++) {
                content[i][j] = record.get(field[j]) + "";
            }
        }
        return content;
    }
	
	/**
     * 发送响应流方法
     *
     * @param response
     * @param fileName
     */
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(),"ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public Record findReportInfoById(Integer reportid,String syscode_lan) {
    	String select =_SQL.replaceAll("_codenamedisplay_", syscode_lan);
    	select+=" FROM patient,admission,studyorder" 
				+ " LEFT JOIN report ON studyorder.id = report.studyorderfk "
				+ " LEFT JOIN study ON study.studyorderfk = studyorder.id "
				+ " WHERE patient.id=admission.patientidfk and admission.id=studyorder.admissionidfk and report.id=? ";
		return Db.findFirst(select,reportid);
    }

}
