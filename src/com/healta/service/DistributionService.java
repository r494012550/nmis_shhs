package com.healta.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
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
import com.healta.constant.StudyOrderStatus;
import com.healta.controller.PrintController;
import com.healta.model.Admission;
import com.healta.model.Filter;
import com.healta.model.Patient;
import com.healta.model.Report;
import com.healta.model.Reportremark;
import com.healta.model.Studyorder;
import com.healta.model.Syngoviafindingfile;
import com.healta.model.Syngoviaimage;
import com.healta.model.Syngoviareport;
import com.healta.model.User;
import com.healta.model.Xslttemplate;
import com.healta.server.dicom.PatientInfo;
import com.healta.util.ParaKit;
import com.healta.util.ResultUtil;
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

public class DistributionService {

	private final static Logger log = Logger.getLogger(WorkListService.class);

	private final static String _SQL="SELECT studyorder.id AS orderid,studyorder.studyid AS studyorderstudyid, studyorder.patientidfk,"
			+ " patient.patientname, patient.patientid,admission.patientsource, studyorder.pri,"	
			+ " studyorder.modality_type,studyorder.studyitems,studyorder.status, studyorder.precedence,"//patient.bedno,"
			+ " studyorder.studydescription,studyorder.accessionnumber,studyorder.numberofstudyrelatedinstances,studyorder.studyinstanceuid,"
			+ " (SELECT _codenamedisplay_ FROM  syscode  WHERE syscode.code = patient.sex   AND syscode.type = '0001') AS sexdisplay,"	
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
			+ " (select top 1 retrialviewcontent from retrialviewlist where reportid = report.id ORDER BY createtime desc) AS retrialviewcontent ";
			
	private final static String SEPARATOR = System.getProperty("line.separator");
	
	/**
	 *  报告搜索
	 * @param map  搜索的条件
	 * @param institutionid  机构的id
	 * @param syscode_lan  中英文
	 * @return
	 */
	public Page<Record> findStudyInfo(Map<String, String[]> map,Integer institutionid,String syscode_lan) {
		List<Object> list = new ArrayList<>();
		String from = null;
		String to = null;
		// 时间选择 'T'今天(today) 'Y'昨天(yesterday) 'TD'三天(ThreeDay) 'FD'五天(FiveDay) ' 'W'一周 'M'一月 '不限(all)
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
		}
		if (StrKit.notBlank(map.get("datefrom")[0])) {
			from = map.get("datefrom")[0]+" 00:00:00";
		}
		if (StrKit.notBlank(map.get("dateto")[0])) {
			to = map.get("dateto")[0]+" 23:59:59";
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
		}

		if (StrKit.notBlank(from)) {
			where += " and " + timecon + " >='" + from + "'";
		}
		if (StrKit.notBlank(to)) {
			where += " and " + timecon + " <='" + to + "'";
		}
		if (StrKit.notBlank(map.get("studyid")) && StrKit.notBlank(map.get("studyid")[0])) {
			where += " and studyorder.studyid =?";
			list.add(map.get("studyid")[0]);
		}
		if (StrKit.notBlank(map.get("patientname")) && StrKit.notBlank(map.get("patientname")[0])) {
			where += " and patient.patientname LIKE CONCAT('%',?,'%')";
			list.add(map.get("patientname")[0]);
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
		//打印状态
		if (StrKit.notBlank(map.get("reportprintstatus")) && StrKit.notBlank(map.get("reportprintstatus")[0])){
			if (StringUtils.equals("0", map.get("reportprintstatus")[0])){
				//未打印
				where += " and (report.printcount = 0 or report.printcount IS NULL)";
			} else if(StringUtils.equals("1",  map.get("reportprintstatus")[0])){
				//已打印
				where += " and report.printcount > 0";
			}
		}
		if (StrKit.notBlank(map.get("patientid")) && StrKit.notBlank(map.get("patientid")[0])) {
			where += " and patient.patientid = ?";
			list.add(map.get("patientid")[0]);
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
            list.add(map.get("reportphysician_name")[0]);
        }
		if (StrKit.notBlank(map.get("auditphysician_name")) && StrKit.notBlank(map.get("auditphysician_name")[0])) {
            where += " and report.auditphysician_name LIKE CONCAT('%',?,'%')";
            list.add(map.get("auditphysician_name")[0]);
        }
		
		if (institutionid != null) {
			where += " and admission.institutionid = ?";
			list.add(institutionid);
		} else if (map.get("institutionid")!=null && StrKit.notBlank(map.get("institutionid")[0])) {
		    where += " and admission.institutionid = ?";
            list.add(map.get("institutionid")[0]);
        }
		
		// 快速检索//////////////////////////////////////////////////////
		String quicksearchTable="";
		if (StrKit.notBlank(map.get("quicksearchcontent")) && StrKit.notBlank(map.get("quicksearchcontent")[0])&&
				StrKit.notBlank(map.get("quicksearchname")) && StrKit.notBlank(map.get("quicksearchname")[0])) {
			quicksearchTable=",quicksearch ";
			if(StrKit.equals("all", map.get("quicksearchname")[0])) {
				where = " and studyorder.id=quicksearch.studyorderkey and CONTAINS(quicksearch.content, ?)";
				list=new ArrayList<Object>();
				list.add('"' + map.get("quicksearchcontent")[0] + '"');
			}
			else {
				where += " and studyorder.id=quicksearch.studyorderkey and CONTAINS(quicksearch.content, ?)";
		        list.add('"' + map.get("quicksearchcontent")[0] + '"');
			} 
		}
		///////////////////////////////////////////////////
		String select =_SQL.replaceAll("_codenamedisplay_", syscode_lan);
		String sqlExceptSelect=" FROM patient,admission,studyorder" 
				+ " LEFT JOIN report ON studyorder.id = report.studyorderfk "
				+ " LEFT JOIN reporticd ON reporticd.reportid = report.id "
				+ " LEFT JOIN study ON study.studyorderfk = studyorder.id "+quicksearchTable
				+ " WHERE patient.id=admission.patientidfk and admission.id=studyorder.admissionidfk"+ where
				+ " ORDER BY reportstatusdisplaycode, studyorder.pri, studyorder.precedence DESC, studyorder.studydatetime, studyorder.regdatetime";
		
		return Db.paginate(Integer.valueOf(map.get("page")[0]), Integer.valueOf(map.get("rows")[0]), select, sqlExceptSelect, list.toArray());
		
//		List<Record> res=Db.find(sql1, list.toArray());
//		
//		
//		//log.info(res.size());
//		ret.add(new Record().set("totalsize", res.size()));
//		
//		int start = 0;
//		int len = res.size();
//		
//		if(map.get("page")!=null&&map.get("rows")[0]!=null) {
//			int page=Integer.valueOf(map.get("page")[0]);
//			int rows=Integer.valueOf(map.get("rows")[0]);
//			start = (page-1) * rows;
//			len=start+rows;
//			if(len>res.size()) {
//				len=res.size();
//			}
//		}
//	
//		for(int i=start;i<(len);i++) {
//			ret.add(res.get(i));
//		}
//		return ret;
//	
	}
	
	/**
	 * 获取常用条件列表
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

		String sql = "SELECT  *,case when modality is null then '' else modality end as modality,"
				+ "(SELECT dic_institution.name FROM dic_institution WHERE dic_institution.id=filter.institutionid) as institutionname"
				+ "  FROM  filter WHERE 0 = 0 " + where +" ORDER BY createtime DESC ";
		List<Record> ret= Db.find(sql);
		ret.forEach(record->{
			record.set("reportstatus_display", SyscodeKit.INSTANCE.getCodeDisplay("0007",record.getStr("reportstatus"), locale));
			record.set("appdate_display", SyscodeKit.INSTANCE.getCodeDisplay("0022",record.get("appdate"), locale));
			record.set("patient", SyscodeKit.INSTANCE.getCodeDisplay("0002",record.get("patientsource"), locale));
			record.set("datetype_display", SyscodeKit.INSTANCE.getCodeDisplay("0021",record.get("datetype"), locale));
		});	
		return ret;
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
	
}
