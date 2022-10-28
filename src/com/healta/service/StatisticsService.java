package com.healta.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.healta.config.MainConfig;
import com.healta.constant.CacheName;
import com.healta.constant.DbConfigName;
import com.healta.controller.StatisticsController;
import com.healta.model.DicModality;
import com.healta.model.Statisticalclassify;
import com.healta.model.Statisticalcustomconditions;
import com.healta.model.Statisticalreport;
import com.healta.model.Syscode;
import com.healta.util.MyConverterProperties;
import com.healta.util.ParaKit;
import com.healta.util.SyscodeKit;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.base.JRBaseReport;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.export.WriterExporterOutput;

public class StatisticsService {
	private final static Logger log = Logger.getLogger(StatisticsService.class);
	private final static String filePath = PropKit.use("system.properties").get("tempdir")+System.getProperty("file.separator");

	public List<Statisticalreport> findStatistics(String value){
		String sql="select * from statisticalreport";
		if(StringUtils.isNotEmpty(value)){
			sql+=" where name like '%?%'";
			return Statisticalreport.dao.find(sql,value);
		}
		else{
			return Statisticalreport.dao.find(sql);
		}


	}

	public JSONArray findStatistics_Json(String value){
		String sql="select * from statisticalreport";
		if(StringUtils.isNotEmpty(value)){
			sql+=" where name like '%?%'";
		}
		JSONArray array=new JSONArray();
		Statisticalreport.dao.find(sql,value).stream().forEach(sr->{

			JSONObject json=new JSONObject();

			json.put("text", StrKit.isBlank(sr.getName())?"":sr.getName());

			array.add(json);
		});

		return array;
	}

	public List<Statisticalcustomconditions> findStatisticalcustomconditions(Integer srfk){
		return Statisticalcustomconditions.dao.find("select * from statisticalcustomconditions where statisticalreportfk=?", srfk);
	}

	public boolean saveStatistics(Statisticalreport st,ArrayList<Statisticalcustomconditions> list, String statisticsPath){
		return Db.tx(()->{
			boolean ret=false;
			if(st.getId()!=null){
				Statisticalreport tmp=Statisticalreport.dao.findById(st.getId());
				if(StrKit.notBlank(tmp.getJasperFilename())&&StrKit.notBlank(st.getJasperFilename())) {
					FileUtils.deleteQuietly(new File(statisticsPath + System.getProperty("file.separator") + tmp.getJasperFilename()));
				}
				if(StrKit.notBlank(tmp.getJrxmlFilename())&&StrKit.notBlank(st.getJrxmlFilename())) {
					FileUtils.deleteQuietly(new File(statisticsPath + System.getProperty("file.separator") + tmp.getJrxmlFilename()));
				}
				if(StrKit.notBlank(st.getSql())){
					st.setJasperFilename(null);
					st.setJrxmlFilename(null);
				}
				ret=st.update();
			}
			else{
				ret=st.remove("id").save();
			}

			for(Statisticalcustomconditions scc:list){
				if(scc.getId()!=null){
					if(StrKit.equals("deleted", scc.getType())){
						ret=ret&scc.delete();
					}
					else{
						ret=ret&scc.update();
					}
				}
				else{
					scc.setStatisticalreportfk(st.getId());
					ret=ret&scc.remove("id").save();
				}
			}

			return ret;
		});
	}

	/*
	 * return String
	 * 1 : save success
	 * 2 : save failed
	 * 3 : name conflicts
	 * */

	public String saveStatisticalclassify(Statisticalclassify sc){
		String ret="1";
		Statisticalclassify tmp=Statisticalclassify.dao.findFirst("select * from statisticalclassify where name=?" ,sc.getName());
		if(sc.getId()!=null){
			if(tmp!=null&&tmp.getId().intValue()!=sc.getId().intValue()){
				ret="3";
			}
			else{
				ret=sc.update()?"1":"2";
			}
		}
		else{
			if(tmp==null){
				ret=sc.remove("id").save()?"1":"2";
			}
			else{
				ret="3";
			}
		}

		return ret;
	}

	public boolean deleteClassify(Integer id){
		return Db.tx(() -> {
			boolean ret=true;
			ret=ret&&Statisticalclassify.dao.deleteById(id);
			Db.delete("delete from statisticalreport where classifyid =?" , id);
			return ret;
		});
	}


	public File exportStatisticalReport(Integer id,String exporttype,HttpServletRequest request) throws JRException, SQLException, IOException{
		File ret=null;
		Statisticalreport sr=Statisticalreport.dao.findById(id);
		HashMap<String, Object> parameters = new HashMap<String, Object>();// 给报表模板文件传参
		String where=ParaKit.generateWhere(request,parameters);
		log.info(where);
		boolean exist = FileUtil.exist(filePath);
		if(!exist){
			FileUtil.mkdir(filePath);
		}
		String fileNamePrefix = filePath+sr.getName()+"_"+System.currentTimeMillis();
		parameters.put("where", where);
		if(StrKit.notBlank(sr.getSql())){
			if(StrKit.equals(exporttype, "pdf")){
				String pdfname=fileNamePrefix+".pdf";
				PdfWriter writer= new PdfWriter(pdfname);
				HtmlConverter.convertToPdf(generationHtml(sr.getSql(), where), writer, MyConverterProperties.getInstance());
				ret=new File(pdfname);
			} else if(StrKit.equals(exporttype, "excel")){
				ret=generationExcel(sr.getSql(), where,fileNamePrefix);
			}
		} else{
			//mysql
			//			Connection connection = MainConfig.c3p0Plugin.getDataSource().getConnection();
			//sql server
			Connection connection = MainConfig.mainDbPlugin.getDataSource().getConnection();
			String statisticsPath = request.getServletContext().getRealPath("upload/statistics");
			// 据据jasper文件生成JasperPrint对象
			File reportFile = new File(statisticsPath + System.getProperty("file.separator") + sr.getJasperFilename());
			log.info(reportFile.getAbsolutePath());
			if (reportFile.exists() == false) {
				return ret;
			}

			if(StrKit.equals(exporttype, "pdf")){
				JasperPrint print = JasperFillManager.fillReport(reportFile.getPath(), parameters, connection);
				String fileName = fileNamePrefix+".pdf";
				JRAbstractExporter exporter = new JRPdfExporter();
				ExporterInput exporterInput = new SimpleExporterInput(print);
				exporter.setExporterInput(exporterInput);
				OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(fileName);
				exporter.setExporterOutput(exporterOutput);
				exporter.exportReport();
				ret=new File(fileName);
			}
			else if(StrKit.equals(exporttype, "excel")){
				parameters.put("IS_IGNORE_PAGINATION", true);
				JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportFile);
				prepareReport(jasperReport);
				//print文件
				JasperPrint print =  JasperFillManager.fillReport(jasperReport, parameters, connection);
				//如果只注明文件名字，默认会生成在user.dir
				String fileName = fileNamePrefix+".xlsx";
				//设置导出时参数
				SimpleXlsxReportConfiguration conf = new SimpleXlsxReportConfiguration();
				conf.setWhitePageBackground(false);
				conf.setOnePagePerSheet(false);
				conf.setDetectCellType(true);
				conf.setIgnorePageMargins(true);
				JRXlsxExporter exporter = new JRXlsxExporter();
				exporter.setConfiguration(conf);
				//设置输入项
				ExporterInput exporterInput = new SimpleExporterInput(print);
				exporter.setExporterInput(exporterInput);
				//设置输出项
				OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(fileName);
				exporter.setExporterOutput(exporterOutput);
				exporter.exportReport();
				//renderHtml("<html><body>sss</body></html>");
				ret=new File(fileName);
			}
			else if(StrKit.equals(exporttype, "word")){
				parameters.put("IS_IGNORE_PAGINATION", true);
				//print文件
				JasperPrint print =  JasperFillManager.fillReport(reportFile.getPath(), parameters, connection);
				//如果只注明文件名字，默认会生成在user.dir
				String fileName = fileNamePrefix+".doc";
				JRAbstractExporter exporter = new JRDocxExporter();
				//设置输入项
				ExporterInput exporterInput = new SimpleExporterInput(print);
				exporter.setExporterInput(exporterInput);
				//设置输出项
				OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(fileName);
				exporter.setExporterOutput(exporterOutput);
				exporter.exportReport();
				//renderHtml("<html><body>sss</body></html>");
				ret=new File(fileName);
			}
			else if(StrKit.equals(exporttype, "csv")){

				parameters.put("IS_IGNORE_PAGINATION", true);
				//print文件
				JasperPrint print =  JasperFillManager.fillReport(reportFile.getPath(), parameters, connection);
				String fileName = fileNamePrefix+".csv";
				JRAbstractExporter exporter = new JRCsvExporter();
				//设置输入项
				ExporterInput exporterInput = new SimpleExporterInput(print);
				exporter.setExporterInput(exporterInput);
				//设置输出项
				WriterExporterOutput exporterOutput = new SimpleWriterExporterOutput(fileName,"utf-8");
				exporter.setExporterOutput(exporterOutput);
				exporter.exportReport();
				//renderHtml("<html><body>sss</body></html>");
				ret=new File(fileName);
			}
			else if(StrKit.equals(exporttype, "rtf")){
				parameters.put("IS_IGNORE_PAGINATION", true);
				//print文件
				JasperPrint print =  JasperFillManager.fillReport(reportFile.getPath(), parameters, connection);
				//如果只注明文件名字，默认会生成在user.dir
				String fileName = fileNamePrefix+".rtf";
				JRRtfExporter exporter = new JRRtfExporter();

				exporter.setExporterInput(new SimpleExporterInput(print));
				exporter.setExporterOutput(new SimpleWriterExporterOutput(fileName));
				//				SimpleRtfReportConfiguration configuration = new SimpleRtfReportConfiguration();
				//configuration.setProgressMonitor(new SimpleExportProgressMonitor());
				//				exporter.setConfiguration(configuration);

				exporter.exportReport();
				ret=new File(fileName);
			}
		}
		return ret;
	}



	/*
	 * 如果导出的是excel，则需要去掉周围的margin
	 */
	public static void prepareReport(JasperReport jasperReport) {
		try {
			Field margin = JRBaseReport.class
					.getDeclaredField("leftMargin");
			margin.setAccessible(true);
			margin.setInt(jasperReport, 0);
			margin = JRBaseReport.class.getDeclaredField("topMargin");
			margin.setAccessible(true);
			margin.setInt(jasperReport, 0);
			margin = JRBaseReport.class.getDeclaredField("bottomMargin");
			margin.setAccessible(true);
			margin.setInt(jasperReport, 0);
			Field pageHeight = JRBaseReport.class
					.getDeclaredField("pageHeight");
			pageHeight.setAccessible(true);
			pageHeight.setInt(jasperReport, 2147483647);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public String generationHtml(String sql,String where){
		String ret="<html><body><div class='statistics'>";
		try {
			sql=StringUtils.replace(sql, "$P!{where}", where);
			log.info("sql:"+sql);

			List<Record> list=Db.use(DbConfigName.MSSQL_ST).find(sql);
			String[] colnames=null;
			StringBuilder html=new StringBuilder();
			if(list.size()>0){
				colnames=list.get(0).getColumnNames();
				html.append("<table width='"+(colnames.length*100)+"'><tr class='firstRow'>");
				for(String name:colnames){
					html.append("<td width='100'>").append(name).append("</td>");
				}
				html.append("</tr>");
			}
			for(Record r:list){
				html.append("<tr>");
				for(String name:colnames){
					html.append("<td>").append(r.getStr(name)).append("</td>");
				}
				html.append("</tr>");
			}
			if(StrKit.notBlank(html.toString())){
				html.append("</table>");
				ret+=html.toString();
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return ret+"</div></body></html>";
	}

	public File generationExcel(String sql,String where,String fileNamePrefix){
		FileOutputStream out=null;
		String fileName = fileNamePrefix+".xlsx";
		try {
			sql=StringUtils.replace(sql, "$P!{where}", where);
			log.info("sql:"+sql);
			List<Record> list=Db.use(DbConfigName.MSSQL_ST).find(sql);
			String[] colnames=null;
			XSSFWorkbook wb=null;
			XSSFSheet sheet=null;
			if(list.size()>0){
				colnames=list.get(0).getColumnNames();
				wb=new XSSFWorkbook();
				sheet = wb.createSheet();
				XSSFRow row = sheet.createRow(0);
				for(int i=0,len=colnames.length;i<len;i++){
					row.createCell(i).setCellValue(colnames[i]);
				}
			}
			for(int i=0,len=list.size();i<len;i++){
				XSSFRow row = sheet.createRow(i+1);
				for(int j=0,len1=colnames.length;j<len1;j++){
					row.createCell(j).setCellValue(list.get(i).getStr(colnames[j]));
				}
			}

			if(wb!=null){
				out=new FileOutputStream(fileName);
				wb.write(out);
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new File(fileName);
	}

	public Record totalNumOfSchOrReg(String startDate,String endDate,Integer institutionid) {
		Record ret= new Record();
		List<Object> params= new ArrayList<Object>();
		params.add(startDate);
		params.add(endDate);
		if(institutionid!=null) {
			params.add(institutionid);
		}

		LocalDate start = LocalDate.parse(startDate);
		LocalDate end = LocalDate.parse(endDate);
		long daysBetween = Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();

		String category[]=new String[Integer.valueOf(daysBetween+"")];
		String data_sch[]=new String[category.length];
		String data_reg[]=new String[category.length];
		for(int i=0;i<category.length;i++) {
			category[i]=start.plusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE);
		}
		Map<String,String> map_sch=Db.use(DbConfigName.MSSQL).find("select format(appointmenttime,'yyyy-MM-dd') as date,count(*) as num from "+(institutionid!=null?"admission,":"")+"studyorder "
				+ " where appointmenttime>? and appointmenttime<? " +(institutionid!=null?" and admission.id=studyorder.admissionidfk and admission.institutionid = ?":"")
				+ " group by format(appointmenttime,'yyyy-MM-dd') order by date",params.toArray()).stream().collect(Collectors.toMap(a->a.getStr("date"), b -> b.getStr("num")));
		for(int i=0;i<category.length;i++) {
			String data=map_sch.get(category[i]);
			data_sch[i]=data!=null?data:"0";
		}

		Map<String,String> map_reg=Db.use(DbConfigName.MSSQL).find("select format(regdatetime,'yyyy-MM-dd') as date,count(*) as num from "+(institutionid!=null?"admission,":"")+"studyorder "
				+ " where regdatetime>? and regdatetime<? " +(institutionid!=null?" and admission.id=studyorder.admissionidfk and admission.institutionid = ?":"")
				+ " group by format(regdatetime,'yyyy-MM-dd') order by date",params.toArray()).stream().collect(Collectors.toMap(a->a.getStr("date"), b -> b.getStr("num")));
		for(int i=0;i<category.length;i++) {
			String data=map_reg.get(category[i]);
			data_reg[i]=data!=null?data:"0";
		}

		for(int i=0;i<category.length;i++) {
			category[i]=category[i].substring(5, 10).replaceAll("-", ".");
		}
		ret.set("category", category);
		ret.set("data_sch", data_sch);
		ret.set("data_reg", data_reg);
		return ret;
	}

	public Record totalNumOfExam(String startDate,String endDate,Integer institutionid) {
		Record ret= new Record();
		List<Object> params= new ArrayList<Object>();
		params.add(startDate);
		params.add(endDate);
		if(institutionid!=null) {
			params.add(institutionid);
		}
		LocalDate start = LocalDate.parse(startDate);
		LocalDate end = LocalDate.parse(endDate);
		long daysBetween = Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();

		String category[]=new String[Integer.valueOf(daysBetween+"")];
		String data[]=new String[category.length];
		for(int i=0;i<category.length;i++) {
			category[i]=start.plusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE);
		}

		Map<String,String> map_reg=Db.use(DbConfigName.MSSQL).find("select format(studydatetime,'yyyy-MM-dd') as date,count(*) as num from "+(institutionid!=null?"admission,":"")+"studyorder "
				+ " where studydatetime>? and studydatetime<? " +(institutionid!=null?" and admission.id=studyorder.admissionidfk and admission.institutionid = ?":"")
				+ " group by format(studydatetime,'yyyy-MM-dd') order by date",params.toArray()).stream().collect(Collectors.toMap(a->a.getStr("date"), b -> b.getStr("num")));
		for(int i=0;i<category.length;i++) {
			String da=map_reg.get(category[i]);
			data[i]=da!=null?da:"0";
		}

		for(int i=0;i<category.length;i++) {
			category[i]=category[i].substring(5, 10).replaceAll("-", ".");
		}
		ret.set("category", category);
		ret.set("data", data);
		return ret;
	}

	public Record totalNumOfReport(String startDate,String endDate,Integer institutionid) {
		Record ret= new Record();
		List<Object> params= new ArrayList<Object>();
		params.add(startDate);
		params.add(endDate);
		if(institutionid!=null) {
			params.add(institutionid);
		}
		LocalDate start = LocalDate.parse(startDate);
		LocalDate end = LocalDate.parse(endDate);
		long daysBetween = Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();

		String category[]=new String[Integer.valueOf(daysBetween+"")];
		String data_report[]=new String[category.length];
		String data_preaudit[]=new String[category.length];
		String data_audit[]=new String[category.length];
		for(int i=0;i<category.length;i++) {
			category[i]=start.plusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE);
		}
		Map<String,String> map_report=Db.use(DbConfigName.MSSQL).find("select format(reporttime,'yyyy-MM-dd') as date,count(*) as num from "+(institutionid!=null?"admission,studyorder,":"")+"report "
				+ " where reporttime>? and reporttime<? " +(institutionid!=null?" and admission.id=studyorder.admissionidfk and studyorder.id=report.studyorderfk and admission.institutionid = ?":"")
				+ " group by format(reporttime,'yyyy-MM-dd') order by date",params.toArray()).stream().collect(Collectors.toMap(a->a.getStr("date"), b -> b.getStr("num")));
		for(int i=0;i<category.length;i++) {
			String data=map_report.get(category[i]);
			data_report[i]=data!=null?data:"0";
		}

		Map<String,String> map_preaudit=Db.use(DbConfigName.MSSQL).find("select format(pre_audittime,'yyyy-MM-dd') as date,count(*) as num from "+(institutionid!=null?"admission,studyorder,":"")+"report "
				+ " where pre_audittime>? and pre_audittime<? " +(institutionid!=null?" and admission.id=studyorder.admissionidfk and studyorder.id=report.studyorderfk and admission.institutionid = ?":"")
				+ " group by format(pre_audittime,'yyyy-MM-dd') order by date",params.toArray()).stream().collect(Collectors.toMap(a->a.getStr("date"), b -> b.getStr("num")));
		for(int i=0;i<category.length;i++) {
			String data=map_preaudit.get(category[i]);
			data_preaudit[i]=data!=null?data:"0";
		}

		Map<String,String> map_audit=Db.use(DbConfigName.MSSQL).find("select format(audittime,'yyyy-MM-dd') as date,count(*) as num from "+(institutionid!=null?"admission,studyorder,":"")+"report "
				+ " where audittime>? and audittime<? " +(institutionid!=null?" and admission.id=studyorder.admissionidfk and studyorder.id=report.studyorderfk and admission.institutionid = ?":"")
				+ " group by format(audittime,'yyyy-MM-dd') order by date",params.toArray()).stream().collect(Collectors.toMap(a->a.getStr("date"), b -> b.getStr("num")));
		for(int i=0;i<category.length;i++) {
			String data=map_audit.get(category[i]);
			data_audit[i]=data!=null?data:"0";
		}

		for(int i=0;i<category.length;i++) {
			category[i]=category[i].substring(5, 10).replaceAll("-", ".");
		}
		ret.set("category", category);
		ret.set("data_report", data_report);
		ret.set("data_preaudit", data_preaudit);
		ret.set("data_audit", data_audit);
		return ret;
	}

	public Record eachEquipmentRevernue(String startDate,String endDate,Integer institutionid) {
		Record ret= new Record();
		List<Object> params= new ArrayList<Object>();
		if(institutionid!=null) {
			params.add(institutionid);
		}
		params.add(startDate);
		params.add(endDate);

//		LocalDate start = LocalDate.parse(startDate);
//		LocalDate end = LocalDate.parse(endDate);
		List<DicModality> list=CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_MODALITY_KEY);
		Map<Integer,Long> map=Db.use(DbConfigName.MSSQL).find("select s.modalityid,sum(case when i.realprice is null then 0 else i.realprice end) as num from "+(institutionid!=null?"admission a,":"")+"studyorder s,studyitem i "
				+ " where s.id=i.orderid and "+(institutionid!=null?" a.id=s.admissionidfk and a.institutionid = ? and ":"")
				+ " s.studydatetime>? and s.studydatetime<? "
				+ "group by s.modalityid ",params.toArray()).stream().collect(Collectors.toMap(a->a.getInt("modalityid"), b -> b.getLong("num")));

		List<Record> datas=new ArrayList<Record>();
		long total=0l;
		if(list!=null) {
			for(DicModality m:list) {
				Record r=new Record();
				r.set("name", m.getModalityName());
				Long value=map.get(m.getId());
				if(value==null) {
					value=0l;
				}
				total+=value;
				r.set("value", value);
				datas.add(r);
			}
		}
		ret.set("startdate", startDate);
		ret.set("total", total);
//		ret.set("category", category);
		ret.set("datas", datas);
		return ret;
	}

	public Record modalityRevernue(String startDate,String endDate,Integer institutionid) {
		Record ret= new Record();
		List<Object> params= new ArrayList<Object>();
		if(institutionid!=null) {
			params.add(institutionid);
		}
		params.add(startDate);
		params.add(endDate);
		List<Record> list=Db.use(DbConfigName.MSSQL).find("select s.modality_type,sum(case when i.realprice is null then 0 else i.realprice end) as num from "+(institutionid!=null?"admission a,":"")+"studyorder s,studyitem i "
				+ " where s.id=i.orderid and "+(institutionid!=null?" a.id=s.admissionidfk and a.institutionid = ? and ":"")
				+ " s.studydatetime>? and s.studydatetime<? "
				+ "group by s.modality_type ",params.toArray());

		List<Record> datas=new ArrayList<Record>();
		long total=0l;
		if(list!=null) {
			for(Record m:list) {
				Record r=new Record();
				r.set("name", m.getStr("modality_type"));
				Long value=m.getLong("num");
				if(value==null) {
					value=0l;
				}
				total+=value;
				r.set("value", value);
				datas.add(r);
			}
		}
		ret.set("startdate", startDate);
		ret.set("enddate", endDate);
		ret.set("total", total);
//		ret.set("category", category);
		ret.set("datas", datas);
		return ret;
	}

	public Record patientSourceRevernue(String startDate,String endDate,Integer institutionid) {
		Record ret= new Record();
		List<Object> params= new ArrayList<Object>();
		if(institutionid!=null) {
			params.add(institutionid);
		}
		params.add(startDate);
		params.add(endDate);
		List<Record> list=Db.use(DbConfigName.MSSQL).find("select a.patientsource,sum(case when i.realprice is null then 0 else i.realprice end) as num from admission a,studyorder s,studyitem i "
				+ " where s.id=i.orderid and a.id=s.admissionidfk "+(institutionid!=null?" and a.institutionid = ? ":"")
				+ " and s.studydatetime>? and s.studydatetime<? "
				+ "group by a.patientsource ",params.toArray());

		List<Record> datas=new ArrayList<Record>();
		long total=0l;
		if(list!=null) {
			for(Record m:list) {
				Record r=new Record();
				r.set("name", SyscodeKit.INSTANCE.getCodeDisplay("0002",m.getStr("patientsource"), "zh_CN"));
				r.set("code", m.getStr("patientsource"));
				Long value=m.getLong("num");
				if(value==null) {
					value=0l;
				}
				total+=value;
				r.set("value", value);
				datas.add(r);
			}
		}
		ret.set("startdate", startDate);
		ret.set("enddate", endDate);
		ret.set("total", total);
//		ret.set("category", category);
		ret.set("datas", datas);
		return ret;
	}

	public Record eachEquipmentRevernue30Days(String startDate,String endDate,Integer institutionid,String modality,String patientsource) {
		Record ret= new Record();
		List<Object> params= new ArrayList<Object>();
		if(institutionid!=null) {
			params.add(institutionid);
		}
		params.add(startDate);
		params.add(endDate);
		if(StrKit.notBlank(modality)) {
			params.add(modality);
		}
		if(StrKit.notBlank(patientsource)) {
			params.add(patientsource);
		}

		LocalDate start = LocalDate.parse(startDate);
		LocalDate end = LocalDate.parse(endDate);
		long daysBetween = Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();

		String category[]=new String[Integer.valueOf(daysBetween+"")];
		for(int i=0;i<category.length;i++) {
			category[i]=start.plusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE);
		}

		List<DicModality> list=CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_MODALITY_KEY);
		if(institutionid!=null) {
			list=list.stream().filter(x->institutionid.equals(x.getInstitutionid())).collect(Collectors.toList());
		}
		String legend[]=new String[list.size()];
		Map<String,Boolean> legend_selected=new HashMap<String,Boolean>();

		Map<Integer,List<Record>> map=Db.use(DbConfigName.MSSQL).find("select s.modalityid,format(studydatetime,'yyyy-MM-dd') as date,sum(case when i.realprice is null then 0 else i.realprice end) as num from "
				+ (institutionid!=null||StrKit.notBlank(patientsource)?"admission a,":"")+" studyorder s,studyitem i "
				+ " where s.id=i.orderid  "
				+ (institutionid!=null||StrKit.notBlank(patientsource)?" and a.id=s.admissionidfk ":"")
				+ (institutionid!=null?" and a.institutionid = ? ":"")
				+ " and s.studydatetime>? and s.studydatetime<? "
				+ (StrKit.notBlank(modality)?" and s.modality_type=? ":"")
				+ (StrKit.notBlank(patientsource)?" and a.patientsource=? ":"")
				+ " group by s.modalityid,format(s.studydatetime,'yyyy-MM-dd') ",params.toArray()).stream().collect(Collectors.groupingBy(a->a.getInt("modalityid")));
		List<Record> series=new ArrayList<Record>();
		for(int i=0;i<list.size();i++) {
			DicModality m=list.get(i);
			legend[i]=m.getModalityName();
			legend_selected.put(m.getModalityName(), StrKit.notBlank(modality)&&!modality.equals(m.getType())?false:true);
//			{
//		    	name: '西门子MR2',
//		      	data: [2420, 532, 971, 134, 1290, 1330, 720, 832, 601, 1234, 1240, 1230, 1620],
//		      	type: 'line',
//		      	smooth: true
//		    },
			Record ser=new Record();
			ser.set("name", m.getModalityName());
			ser.set("type", "line");
			ser.set("smooth", true);
			List<Record> li= map.get(m.getId());
			Map<String, Long> ma=null;
			if(li!=null) {
				ma=li.stream().collect(Collectors.toMap(a->a.getStr("date"), b -> b.getLong("num")));
			}
			String data[]=new String[category.length];
			for(int j=0;j<category.length;j++) {
				String va="0";
				if(ma!=null&&ma.get(category[j])!=null) {
					va=ma.get(category[j])+"";
				}
				data[j]=va;
			}
			ser.set("data", data);
			series.add(ser);
		}
		for(int i=0;i<category.length;i++) {
			category[i]=category[i].substring(5, 10).replaceAll("-", ".");
		}
		ret.set("category", category);
		ret.set("legend", legend);
		ret.set("legend_selected", legend_selected);
		ret.set("series", series);
		return ret;
	}

	public Record positiveRate(String startDate,String endDate,Integer institutionid) {
		Record ret= new Record();
		List<Object> params= new ArrayList<Object>();
		if(institutionid!=null) {
			params.add(institutionid);
		}
		params.add(startDate);
		params.add(endDate);

		List<Record> categories=Db.use(DbConfigName.MSSQL).find("select distinct (CASE WHEN s.appdeptname IS NULL THEN '无申请科室' ELSE s.appdeptname END )AS appdeptname from "
				+ (institutionid!=null?"admission a,":"")+" studyorder s,report r "
				+ "	where s.id=r.studyorderfk and "+(institutionid!=null?" a.id=s.admissionidfk and s.id=r.studyorderfk and a.institutionid = ? and ":"")
				+ "r.audittime>? and r.audittime<?",params.toArray());
		String category[]=new String[categories.size()];
		for(int i=0;i<category.length;i++) {
			category[i]=categories.get(i).getStr("appdeptname");
		}

//		Map<String,List<Record>> map=Db.use(DbConfigName.MSSQL).find("select s.modality_type,(CASE WHEN s.appdeptname IS NULL THEN '无申请科室' ELSE s.appdeptname END )AS appdeptname,"
//				+ " SUM( CASE WHEN r.pos_or_neg = 'n' THEN 1 ELSE 0 END ) AS neg, "
//				+ " SUM( CASE WHEN r.pos_or_neg = 'p' THEN 1 ELSE 0 END ) AS pos,count(r.id) as total from "
//				+ (institutionid!=null?"admission a,":"")+" studyorder s,report r "
//				+ " where s.id=r.studyorderfk and "+(institutionid!=null?" a.id=s.admissionidfk and s.id=r.studyorderfk and a.institutionid = ? and ":"")
//				+ " r.audittime>? and r.audittime<? "
//				+ " group by s.modality_type,s.appdeptname",params.toArray()).stream().collect(Collectors.groupingBy(a->a.getStr("modality_type")));
//		List<Record> series=new ArrayList<Record>();
//		for(Map.Entry<String,List<Record>> entry : map.entrySet()) {
//			List<Record> list=entry.getValue();
//			Record ser=new Record();
//			ser.set("name", entry.getKey());
//			ser.set("type", "bar");
//			Map<String, Record> ma=list.stream().collect(Collectors.toMap(a->a.getStr("appdeptname"), b -> b));
//
//			String data[]=new String[category.length];
//			for(int j=0;j<category.length;j++) {
//				String va="0";
//				if(ma.get(category[j])!=null) {
//					Record re=ma.get(category[j]);
//					int p=re.getInt("pos");
//					int total=re.getInt("total");
//					va=(p*100/total)+"";
//				}
//				data[j]=va;
//			}
//			ser.set("data", data);
//			series.add(ser);
//		}

		Map<String,Record> map=Db.use(DbConfigName.MSSQL).find("select (CASE WHEN s.appdeptname IS NULL THEN '无申请科室' ELSE s.appdeptname END )AS appdeptname,"
				+ " SUM( CASE WHEN r.pos_or_neg = 'n' or r.pos_or_neg is null THEN 1 ELSE 0 END ) AS neg, "
				+ " SUM( CASE WHEN r.pos_or_neg = 'p' THEN 1 ELSE 0 END ) AS pos,count(r.id) as total from "
				+ (institutionid!=null?"admission a,":"")+" studyorder s,report r "
				+ " where s.id=r.studyorderfk and "+(institutionid!=null?" a.id=s.admissionidfk and s.id=r.studyorderfk and a.institutionid = ? and ":"")
				+ " r.audittime>? and r.audittime<? "
				+ " group by s.appdeptname",params.toArray()).stream().collect(Collectors.toMap(a->a.getStr("appdeptname"),b->b));
		List<Record> series=new ArrayList<Record>();
		Record ser=new Record();
		ser.set("name", "阴性数");
		ser.set("type", "bar");
		ser.set("stack", "total");
		ser.set("emphasis", new Record().set("focus", "series"));
		String data[]=new String[category.length];
		for(int j=0;j<category.length;j++) {
			String va="0";
			if(map.get(category[j])!=null) {
				Record re=map.get(category[j]);
				va=re.getInt("neg")+"";
			}
			data[j]=va;
		}
		ser.set("data", data);
		series.add(ser);

		Record ser1=new Record();
		ser1.set("name", "阳性数");
		ser1.set("type", "bar");
		ser1.set("stack", "total");
		ser1.set("emphasis", new Record().set("focus", "series"));
		String data1[]=new String[category.length];
		for(int j=0;j<category.length;j++) {
			String va="0";
			if(map.get(category[j])!=null) {
				Record re=map.get(category[j]);
				va=re.getInt("pos")+"";
			}
			data1[j]=va;
		}
		ser1.set("data", data1);
		series.add(ser1);
		ret.set("category", category);
		ret.set("series", series);
		return ret;
	}

	/*
	 * 汇总当天数据
	 * */
	public Record summarizeDataOfToday(String startDate,String endDate,Integer institutionid) {
		Record ret= new Record();
		List<Object> params= new ArrayList<Object>();
		if(institutionid!=null) {
			params.add(institutionid);
		}
		params.add(startDate);
		params.add(endDate);

		Double appoint_count= Db.use(DbConfigName.MSSQL).queryDouble("select count(*) as num from "
						+ (institutionid!=null?"admission a,":"")+ "studyorder s where "
						+ (institutionid!=null?" a.id=s.admissionidfk and a.institutionid = ? and ":"")+" s.appointmenttime>? and s.appointmenttime< ?"
				, params.toArray());
		ret.set("appoint_count", appoint_count!=null?appoint_count:0);

		Double ret_count= Db.use(DbConfigName.MSSQL).queryDouble("select count(*) as num from "
						+ (institutionid!=null?"admission a,":"")+"studyorder s where "
						+ (institutionid!=null?" a.id=s.admissionidfk and a.institutionid = ? and ":"")+" s.regdatetime>? and s.regdatetime<?"
				, params.toArray());
		ret.set("reg_count", ret_count!=null?ret_count:0);

		Double exam_count= Db.use(DbConfigName.MSSQL).queryDouble("select count(*) as num from "
						+ (institutionid!=null?"admission a,":"")+"studyorder s where "
						+ (institutionid!=null?" a.id=s.admissionidfk and a.institutionid = ? and ":"")+ " s.studydatetime>? and s.studydatetime<?"
				, params.toArray());
		ret.set("exam_count", exam_count!=null?exam_count:0);

		Double report_count= Db.use(DbConfigName.MSSQL).queryDouble("select count(*) as num from "
						+ (institutionid!=null?"admission a,studyorder s,":"")+"report r where "
						+ (institutionid!=null?" a.id=s.admissionidfk and s.id=r.studyorderfk and a.institutionid=? and ":"")+ "r.reporttime>? and r.reporttime<?",
				params.toArray());
		ret.set("report_count", report_count!=null?report_count:0);

		Double preaudit_count= Db.use(DbConfigName.MSSQL).queryDouble("select count(*) as num from "
						+ (institutionid!=null?"admission a,studyorder s,":"")+"report r where "
						+ (institutionid!=null?" a.id=s.admissionidfk and s.id=r.studyorderfk and a.institutionid=? and ":"")+ "r.pre_audittime>? and r.pre_audittime<?",
				params.toArray());
		ret.set("preaudit_count", preaudit_count!=null?preaudit_count:0);

		Double audit_count= Db.use(DbConfigName.MSSQL).queryDouble("select count(*) as num from "
						+ (institutionid!=null?"admission a,studyorder s,":"")+"report r where "
						+ (institutionid!=null?" a.id=s.admissionidfk and s.id=r.studyorderfk and a.institutionid=? and ":"")+"r.audittime>? and r.audittime<?",
				params.toArray());
		ret.set("audit_count", audit_count!=null?audit_count:0);

		return ret;
	}

	public Record 	onlineTime(String startDate,String endDate,Integer institutionid) {
		Record ret= new Record();
		List<Object> params= new ArrayList<Object>();
		params.add(startDate);
		params.add(endDate);
		if(institutionid!=null) {
			params.add(institutionid);
		}

		List<Record> categories=Db.use(DbConfigName.MSSQL).find("select u.id,max(u.name) as name,sum(ISNULL(a.duration,0)) as times from users u left join user_audit_logs a on u.id=a.userfk"
				+ " and a.audit_time>? and a.audit_time<? and a.audit_type=1 and a.duration is not null "
				+ (institutionid!=null?",dic_employee e where u.employeefk=e.id and e.institutionid=? and ":" where ")
				+ " u.active=1 and u.deleted=0 group by u.id",params.toArray());
		String category[]=new String[categories.size()];
		Integer data[]=new Integer[categories.size()];
		for(int i=0;i<category.length;i++) {
			category[i]=categories.get(i).getStr("name");
			data[i]=categories.get(i).getInt("times")/60;
		}
		ret.set("category", category);
		ret.set("data", data);
		return ret;
	}

//	=================================== 统计 报告标签 ======================================

	/**
	 * 统计当天设备类型的报告数量
	 * @param startDate
	 * @param endDate
	 * @param institutionid
	 * @return
	 */
	public Record reportDataOfToday(String startDate,String endDate,Integer institutionid) {
		Record ret = new Record();
		List<Object> params = new ArrayList<Object>();
		if (institutionid != null) {
			params.add(institutionid);
		}
		params.add(startDate + " 00:00:00");
		params.add(endDate + " 23:59:59");

		// 报告总数
		Double report_count= Db.use(DbConfigName.MSSQL).queryDouble("select count(*) as num from "
						+ (institutionid!=null?"admission a,":"")+ "studyorder s where "
						+ (institutionid!=null?" a.id=s.admissionidfk and a.institutionid = ? and ":"")+" s.regdatetime>=? and s.regdatetime<= ?"
				, params.toArray());
		ret.set("report_count", report_count!=null?report_count:0);

		// 未写报告总数
		Double report_noresult_count= Db.use(DbConfigName.MSSQL).queryDouble("select count(*) as num from "
						+ (institutionid!=null?"admission a,":"")+ "studyorder s left join report r on r.studyorderfk = s.id where "
						+ (institutionid!=null?" a.id=s.admissionidfk and a.institutionid = ? and ":"")+" (r.reportstatus = '20' or r.reportstatus is null) "
						+ " and s.regdatetime>=? and s.regdatetime<= ?"
				, params.toArray());
		ret.set("report_noresult_count", report_noresult_count!=null?report_noresult_count:0);

		// 未审核报告总数
		Double report_no_audit_count= Db.use(DbConfigName.MSSQL).queryDouble("select count(*) as num from "
						+ (institutionid!=null?"admission a,":"")+ "studyorder s left join report r on r.studyorderfk = s.id where "
						+ (institutionid!=null?" a.id=s.admissionidfk and a.institutionid = ? and ":"")+" (r.reportstatus != '31' or r.reportstatus is null) "
						+ " and s.regdatetime>=? and s.regdatetime<= ?"
				, params.toArray());
		ret.set("report_no_audit_count", report_no_audit_count!=null?report_no_audit_count:0);

		// 已审核报告总数
		Double report_audit_count= Db.use(DbConfigName.MSSQL).queryDouble("select count(*) as num from "
						+ (institutionid!=null?"admission a,":"")+ "studyorder s left join report r on r.studyorderfk = s.id where "
						+ (institutionid!=null?" a.id=s.admissionidfk and a.institutionid = ? and ":"")+" r.reportstatus = '31' "
						+ " and s.regdatetime>=? and s.regdatetime<= ?"
				, params.toArray());
		ret.set("report_audit_count", report_audit_count!=null?report_audit_count:0);
		return ret;
	}

	/**
	 *  按设备类型分配的报告
	 * @param startDate
	 * @param endDate
	 * @param institutionid
	 * @return
	 */
	public Record reportModality(String startDate,String endDate,Integer institutionid) {
		Record ret = new Record();
		List<Object> params = new ArrayList<Object>();
		if (institutionid!=null) {
			params.add(institutionid);
		}
		params.add(startDate + " 00:00:00");
		params.add(endDate + " 23:59:59");
		List<Record> list=Db.use(DbConfigName.MSSQL).find("select s.modality_type, r.reportstatus from "+(institutionid!=null?"admission a,":"")+"studyorder s "
				+ " left join report r on r.studyorderfk = s.id where "+(institutionid!=null?" a.id=s.admissionidfk and a.institutionid = ? and ":"")
				+ " s.regdatetime>? and s.regdatetime<? ",params.toArray());

		ret.set("startdate", startDate);
		ret.set("enddate", endDate);
		// 未写报告
		List<Record> noresultLists = list.stream().filter(r -> (StringUtils.isBlank(r.getStr("reportstatus")) || r.getStr("reportstatus").equals("20"))).collect(Collectors.toList());
		String data_noresult[] = new String[4];
		data_noresult[0] = noresultLists.size()==0?"0":noresultLists.stream().filter(r -> (!r.getStr("modality_type").equals("CT") && !r.getStr("modality_type").equals("MR") && !r.getStr("modality_type").equals("DR"))).count() + ""; // 其他
		data_noresult[1] = noresultLists.size()==0?"0":noresultLists.stream().filter(r -> r.getStr("modality_type").equals("DR")).count() + ""; // DR
		data_noresult[2] = noresultLists.size()==0?"0":noresultLists.stream().filter(r -> r.getStr("modality_type").equals("MR")).count() + ""; // MR
		data_noresult[3] = noresultLists.size()==0?"0":noresultLists.stream().filter(r -> r.getStr("modality_type").equals("CT")).count() + ""; // CT
		ret.set("data_noresult", data_noresult);
		// 初步报告
		List<Record> pre_report_Lists = list.stream().filter(r -> (StringUtils.isNotBlank(r.getStr("reportstatus")) && r.getStr("reportstatus").equals("23"))).collect(Collectors.toList());
		String data_pre_report[] = new String[4];
		data_pre_report[0] = pre_report_Lists.size()==0?"0":pre_report_Lists.stream().filter(r -> (!r.getStr("modality_type").equals("CT") && !r.getStr("modality_type").equals("MR") && !r.getStr("modality_type").equals("DR"))).count() + ""; // 其他
		data_pre_report[1] = pre_report_Lists.size()==0?"0":pre_report_Lists.stream().filter(r -> r.getStr("modality_type").equals("DR")).count() + ""; // DR
		data_pre_report[2] = pre_report_Lists.size()==0?"0":pre_report_Lists.stream().filter(r -> r.getStr("modality_type").equals("MR")).count() + ""; // MR
		data_pre_report[3] = pre_report_Lists.size()==0?"0":pre_report_Lists.stream().filter(r -> r.getStr("modality_type").equals("CT")).count() + ""; // CT
		ret.set("data_pre_report", data_pre_report);
		// 未审报告
		List<Record> no_audit_report_Lists = list.stream().filter(r -> (StringUtils.isBlank(r.getStr("reportstatus")) || !r.getStr("reportstatus").equals("31"))).collect(Collectors.toList());
		String data_no_audit[] = new String[4];
		data_no_audit[0] = no_audit_report_Lists.size()==0?"0":no_audit_report_Lists.stream().filter(r -> (!r.getStr("modality_type").equals("CT") && !r.getStr("modality_type").equals("MR") && !r.getStr("modality_type").equals("DR"))).count() + ""; // 其他
		data_no_audit[1] = no_audit_report_Lists.size()==0?"0":no_audit_report_Lists.stream().filter(r -> r.getStr("modality_type").equals("DR")).count() + ""; // DR
		data_no_audit[2] = no_audit_report_Lists.size()==0?"0":no_audit_report_Lists.stream().filter(r -> r.getStr("modality_type").equals("MR")).count() + ""; // MR
		data_no_audit[3] = no_audit_report_Lists.size()==0?"0":no_audit_report_Lists.stream().filter(r -> r.getStr("modality_type").equals("CT")).count() + ""; // CT
		ret.set("data_no_audit", data_no_audit);
		// 已审核报告
		List<Record> audit_report_Lists = list.stream().filter(r -> (StringUtils.isNotBlank(r.getStr("reportstatus")) && r.getStr("reportstatus").equals("31"))).collect(Collectors.toList());
		String data_audit[] = new String[4];
		data_audit[0] = audit_report_Lists.size()==0?"0":audit_report_Lists.stream().filter(r -> (!r.getStr("modality_type").equals("CT") && !r.getStr("modality_type").equals("MR") && !r.getStr("modality_type").equals("DR"))).count() + ""; // 其他
		data_audit[1] = audit_report_Lists.size()==0?"0":audit_report_Lists.stream().filter(r -> r.getStr("modality_type").equals("DR")).count() + ""; // DR
		data_audit[2] = audit_report_Lists.size()==0?"0":audit_report_Lists.stream().filter(r -> r.getStr("modality_type").equals("MR")).count() + ""; // MR
		data_audit[3] = audit_report_Lists.size()==0?"0":audit_report_Lists.stream().filter(r -> r.getStr("modality_type").equals("CT")).count() + ""; // CT
		ret.set("data_audit", data_audit);
		return ret;
	}

	/**
	 *  按病人来源统计报告
	 * @param startDate
	 * @param endDate
	 * @param institutionid
	 * @return
	 */
	public Record reportPatientsource(String startDate,String endDate,Integer institutionid) {
		Record ret = new Record();
		List<Object> params = new ArrayList<Object>();
		if (institutionid!=null) {
			params.add(institutionid);
		}
		params.add(startDate + " 00:00:00");
		params.add(endDate + " 23:59:59");
		List<Record> list=Db.use(DbConfigName.MSSQL).find("select a.patientsource, r.reportstatus from admission a, studyorder s "
				+ " left join report r on r.studyorderfk = s.id where a.id=s.admissionidfk and "+(institutionid!=null?" a.institutionid = ? and ":"")
				+ " s.regdatetime>? and s.regdatetime<? ",params.toArray());
		ret.set("startdate", startDate);
		ret.set("enddate", endDate);
		// 未写报告
		List<Record> noresultLists = list.stream().filter(r -> (StringUtils.isBlank(r.getStr("reportstatus")) || r.getStr("reportstatus").equals("20"))).collect(Collectors.toList());
		String data_noresult[] = new String[4];
		data_noresult[0] = noresultLists.size()==0?"0":noresultLists.stream().filter(r -> r.getStr("patientsource").equals("P")).count() + ""; // 体检
		data_noresult[1] = noresultLists.size()==0?"0":noresultLists.stream().filter(r -> r.getStr("patientsource").equals("E")).count() + ""; // 急诊
		data_noresult[2] = noresultLists.size()==0?"0":noresultLists.stream().filter(r -> r.getStr("patientsource").equals("I")).count() + ""; // 住院
		data_noresult[3] = noresultLists.size()==0?"0":noresultLists.stream().filter(r -> r.getStr("patientsource").equals("O")).count() + ""; // 门诊
		ret.set("data_noresult", data_noresult);
		// 初步报告
		List<Record> pre_report_Lists = list.stream().filter(r -> (StringUtils.isNotBlank(r.getStr("reportstatus")) && r.getStr("reportstatus").equals("23"))).collect(Collectors.toList());
		String data_pre_report[] = new String[4];
		data_pre_report[0] = pre_report_Lists.size()==0?"0":pre_report_Lists.stream().filter(r -> r.getStr("patientsource").equals("P")).count() + ""; // 体检
		data_pre_report[1] = pre_report_Lists.size()==0?"0":pre_report_Lists.stream().filter(r -> r.getStr("patientsource").equals("E")).count() + ""; // 急诊
		data_pre_report[2] = pre_report_Lists.size()==0?"0":pre_report_Lists.stream().filter(r -> r.getStr("patientsource").equals("I")).count() + ""; // 住院
		data_pre_report[3] = pre_report_Lists.size()==0?"0":pre_report_Lists.stream().filter(r -> r.getStr("patientsource").equals("O")).count() + ""; // 门诊
		ret.set("data_pre_report", data_pre_report);
		// 未审报告
		List<Record> no_audit_report_Lists = list.stream().filter(r -> (StringUtils.isBlank(r.getStr("reportstatus")) || !r.getStr("reportstatus").equals("31"))).collect(Collectors.toList());
		String data_no_audit[] = new String[4];
		data_no_audit[0] = no_audit_report_Lists.size()==0?"0":no_audit_report_Lists.stream().filter(r -> r.getStr("patientsource").equals("P")).count() + ""; // 体检
		data_no_audit[1] = no_audit_report_Lists.size()==0?"0":no_audit_report_Lists.stream().filter(r -> r.getStr("patientsource").equals("E")).count() + ""; // 急诊
		data_no_audit[2] = no_audit_report_Lists.size()==0?"0":no_audit_report_Lists.stream().filter(r -> r.getStr("patientsource").equals("I")).count() + ""; // 住院
		data_no_audit[3] = no_audit_report_Lists.size()==0?"0":no_audit_report_Lists.stream().filter(r -> r.getStr("patientsource").equals("O")).count() + ""; // 门诊
		ret.set("data_no_audit", data_no_audit);
		// 已审核报告
		List<Record> audit_report_Lists = list.stream().filter(r -> (StringUtils.isNotBlank(r.getStr("reportstatus")) && r.getStr("reportstatus").equals("31"))).collect(Collectors.toList());
		String data_audit[] = new String[4];
		data_audit[0] = audit_report_Lists.size()==0?"0":audit_report_Lists.stream().filter(r -> r.getStr("patientsource").equals("P")).count() + ""; // 体检
		data_audit[1] = audit_report_Lists.size()==0?"0":audit_report_Lists.stream().filter(r -> r.getStr("patientsource").equals("E")).count() + ""; // 急诊
		data_audit[2] = audit_report_Lists.size()==0?"0":audit_report_Lists.stream().filter(r -> r.getStr("patientsource").equals("I")).count() + ""; // 住院
		data_audit[3] = audit_report_Lists.size()==0?"0":audit_report_Lists.stream().filter(r -> r.getStr("patientsource").equals("O")).count() + ""; // 门诊
		ret.set("data_audit", data_audit);
		return ret;
	}

	/**
	 * 各个设备类型未写的报告
	 * @param startDate
	 * @param endDate
	 * @param institutionid
	 * @return
	 */
	public Record noresultReportModality(String startDate,String endDate,Integer institutionid) {
		Record ret = new Record();
		List<Object> params = new ArrayList<Object>();
		if (institutionid!=null) {
			params.add(institutionid);
		}
		params.add(startDate + " 00:00:00");
		params.add(endDate + " 23:59:59");
		List<Record> list=Db.use(DbConfigName.MSSQL).find("select s.modality_type as name, count(s.modality_type) as value from "+(institutionid!=null?"admission a,":"")+"studyorder s "
				+ " left join report r on r.studyorderfk = s.id where (r.reportstatus = '20' or r.reportstatus is null) and "+(institutionid!=null?" a.id=s.admissionidfk and a.institutionid = ? and ":"")
				+ " s.regdatetime>? and s.regdatetime<? "
				+ "group by s.modality_type ",params.toArray());

		int count = 0;
		for (Record r : list) {
			count += r.getInt("value");
		}
		if (count != 0) {
			for (Record r : list) {
				r.set("name", r.getStr("name") + "（" + new BigDecimal(((float) r.getInt("value") / (float)count) * 100).setScale(2,  BigDecimal.ROUND_HALF_UP).floatValue() + "%）");
			}
		}
		ret.set("startdate", startDate);
		ret.set("enddate", endDate);
		ret.set("datas", list);
		return ret;
	}

	/**
	 *  各个设备类型未审核的报告
	 * @param startDate
	 * @param endDate
	 * @param institutionid
	 * @return
	 */
	public Record noauditReportModality(String startDate,String endDate,Integer institutionid) {
		Record ret = new Record();
		List<Object> params = new ArrayList<Object>();
		if (institutionid!=null) {
			params.add(institutionid);
		}
		params.add(startDate + " 00:00:00");
		params.add(endDate + " 23:59:59");
		List<Record> list=Db.use(DbConfigName.MSSQL).find("select s.modality_type as name, count(s.modality_type) as value from "+(institutionid!=null?"admission a,":"")+"studyorder s "
				+ " left join report r on r.studyorderfk = s.id where r.reportstatus != '31' and "+(institutionid!=null?" a.id=s.admissionidfk and a.institutionid = ? and ":"")
				+ " s.regdatetime>? and s.regdatetime<? "
				+ "group by s.modality_type ",params.toArray());

		int count = 0;
		for (Record r : list) {
			count += r.getInt("value");
		}
		if (count != 0) {
			for (Record r : list) {
				r.set("name", r.getStr("name") + "（" + new BigDecimal(((float) r.getInt("value") / (float)count) * 100).setScale(2,  BigDecimal.ROUND_HALF_UP).floatValue() + "%）");
			}
		}
		ret.set("startdate", startDate);
		ret.set("enddate", endDate);
		ret.set("datas", list);
		return ret;
	}

	/**
	 *  图像评级
	 * @param startDate
	 * @param endDate
	 * @param institutionid
	 * @return
	 */
	public Record imagequality(String startDate,String endDate,Integer institutionid) {
		Record ret = new Record();
		List<Object> params = new ArrayList<Object>();
		if (institutionid!=null) {
			params.add(institutionid);
		}
		LocalDate start = LocalDate.parse(startDate);
		LocalDate end = LocalDate.parse(endDate);
		long daysBetween = Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();
		String category[]=new String[Integer.valueOf(daysBetween+"")];
		for(int i=0;i<category.length;i++) {
			category[i]=start.plusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE);
		}
		params.add(startDate + " 00:00:00");
		params.add(endDate + " 23:59:59");

		List<Record> list=Db.use(DbConfigName.MSSQL).find("select (select syscode.name_zh from syscode where syscode.code = r.imagequality and syscode.type = '0025') as name, "
				+ " format(s.studydatetime,'yyyy-MM-dd') as date, count(r.imagequality) as num from "+(institutionid!=null?"admission a,":"")+"studyorder s "
				+ " left join report r on r.studyorderfk = s.id where r.imagequality is not null and "+(institutionid!=null?" a.id=s.admissionidfk and a.institutionid = ? and ":"")
				+ " s.studydatetime>? and s.studydatetime<? GROUP BY format ( s.studydatetime, 'yyyy-MM-dd' ), r.imagequality",params.toArray());
		List<Syscode> syscodes = Syscode.dao.find("select syscode.name_zh from syscode where syscode.type = '0025' and syscode.code is not null");
		String syscode[] = new String[syscodes.size()];
		Map<String, String[]> map = new HashMap<String, String[]>();
		for (int i=0; i<syscodes.size(); i++) {
			String name = syscodes.get(i).getNameZh();
			syscode[i] = name;
			Map<String,String> map_value = list.stream().filter(r -> r.getStr("name").equals(name)).collect(Collectors.toMap(a->a.getStr("date"), b -> b.getStr("num")));
			String value[] = new String[category.length];
			for (int j=0; j<category.length; j++) {
				String data=map_value.get(category[j]);
				value[j]=data!=null?data:"0";
			}
			map.put(name, value);
		}
		for (int i=0; i<category.length; i++) {
			category[i]=category[i].substring(5, 10).replaceAll("-", ".");
		}
		ret.set("category", category);
		ret.set("startdate", startDate);
		ret.set("enddate", endDate);
		ret.set("map", map);
		ret.set("syscode", syscode);
		return ret;
	}

	/**
	 *  报告质量
	 * @param startDate
	 * @param endDate
	 * @param institutionid
	 * @return
	 */
	public Record reportquality(String startDate,String endDate,Integer institutionid) {
		Record ret = new Record();
		List<Object> params = new ArrayList<Object>();
		if (institutionid!=null) {
			params.add(institutionid);
		}
		LocalDate start = LocalDate.parse(startDate);
		LocalDate end = LocalDate.parse(endDate);
		long daysBetween = Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();
		String category[]=new String[Integer.valueOf(daysBetween+"")];
		for(int i=0;i<category.length;i++) {
			category[i]=start.plusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE);
		}
		params.add(startDate + " 00:00:00");
		params.add(endDate + " 23:59:59");

		List<Record> list=Db.use(DbConfigName.MSSQL).find("select (select syscode.name_zh from syscode where syscode.code = r.reportquality and syscode.type = '0027') as name, "
				+ " format(s.studydatetime,'yyyy-MM-dd') as date, count(r.reportquality) as num from "+(institutionid!=null?"admission a,":"")+"studyorder s "
				+ " left join report r on r.studyorderfk = s.id where r.imagequality is not null and "+(institutionid!=null?" a.id=s.admissionidfk and a.institutionid = ? and ":"")
				+ " s.studydatetime>? and s.studydatetime<? GROUP BY format ( s.studydatetime, 'yyyy-MM-dd' ), r.reportquality",params.toArray());
		List<Syscode> syscodes = Syscode.dao.find("select syscode.name_zh from syscode where syscode.type = '0027' and syscode.code is not null");
		String syscode[] = new String[syscodes.size()];
		Map<String, String[]> map = new HashMap<String, String[]>();
		for (int i=0; i<syscodes.size(); i++) {
			String name = syscodes.get(i).getNameZh();
			syscode[i] = name;
			Map<String,String> map_value = list.stream().filter(r -> r.getStr("name").equals(name)).collect(Collectors.toMap(a->a.getStr("date"), b -> b.getStr("num")));
			String value[] = new String[category.length];
			for (int j=0; j<category.length; j++) {
				String data=map_value.get(category[j]);
				value[j]=data!=null?data:"0";
			}
			map.put(name, value);
		}
		for (int i=0; i<category.length; i++) {
			category[i]=category[i].substring(5, 10).replaceAll("-", ".");
		}
		ret.set("category", category);
		ret.set("startdate", startDate);
		ret.set("enddate", endDate);
		ret.set("map", map);
		ret.set("syscode", syscode);
		return ret;
	}

	/**
	 *  诊断符合
	 * @param startDate
	 * @param endDate
	 * @param institutionid
	 * @return
	 */
	public Record diagnosisCoincidence(String startDate,String endDate,Integer institutionid) {
		Record ret = new Record();
		List<Object> params = new ArrayList<Object>();
		if (institutionid!=null) {
			params.add(institutionid);
		}
		LocalDate start = LocalDate.parse(startDate);
		LocalDate end = LocalDate.parse(endDate);
		long daysBetween = Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();
		String category[]=new String[Integer.valueOf(daysBetween+"")];
		for(int i=0;i<category.length;i++) {
			category[i]=start.plusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE);
		}
		params.add(startDate + " 00:00:00");
		params.add(endDate + " 23:59:59");

		List<Record> list=Db.use(DbConfigName.MSSQL).find("select (select syscode.name_zh from syscode where syscode.code = r.diagnosis_coincidence and syscode.type = '0028') as name, "
				+ " format(s.studydatetime,'yyyy-MM-dd') as date, count(r.diagnosis_coincidence) as num from "+(institutionid!=null?"admission a,":"")+"studyorder s "
				+ " left join report r on r.studyorderfk = s.id where r.imagequality is not null and "+(institutionid!=null?" a.id=s.admissionidfk and a.institutionid = ? and ":"")
				+ " s.studydatetime>? and s.studydatetime<? GROUP BY format ( s.studydatetime, 'yyyy-MM-dd' ), r.diagnosis_coincidence",params.toArray());
		List<Syscode> syscodes = Syscode.dao.find("select syscode.name_zh from syscode where syscode.type = '0028' and syscode.code is not null");
		String syscode[] = new String[syscodes.size()];
		Map<String, String[]> map = new HashMap<String, String[]>();
		for (int i=0; i<syscodes.size(); i++) {
			String name = syscodes.get(i).getNameZh();
			syscode[i] = name;
			Map<String,String> map_value = list.stream().filter(r -> r.getStr("name").equals(name)).collect(Collectors.toMap(a->a.getStr("date"), b -> b.getStr("num")));
			String value[] = new String[category.length];
			for (int j=0; j<category.length; j++) {
				String data=map_value.get(category[j]);
				value[j]=data!=null?data:"0";
			}
			map.put(name, value);
		}
		for (int i=0; i<category.length; i++) {
			category[i]=category[i].substring(5, 10).replaceAll("-", ".");
		}
		ret.set("category", category);
		ret.set("startdate", startDate);
		ret.set("enddate", endDate);
		ret.set("map", map);
		ret.set("syscode", syscode);
		return ret;
	}

	/**
	 *  阴阳性
	 * @param startDate
	 * @param endDate
	 * @param institutionid
	 * @return
	 */
	public Record posOrNeg(String startDate,String endDate,Integer institutionid) {
		Record ret = new Record();
		List<Object> params = new ArrayList<Object>();
		if (institutionid!=null) {
			params.add(institutionid);
		}
		LocalDate start = LocalDate.parse(startDate);
		LocalDate end = LocalDate.parse(endDate);
		long daysBetween = Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();
		String category[]=new String[Integer.valueOf(daysBetween+"")];
		for(int i=0;i<category.length;i++) {
			category[i]=start.plusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE);
		}
		params.add(startDate + " 00:00:00");
		params.add(endDate + " 23:59:59");

		List<Record> list=Db.use(DbConfigName.MSSQL).find("select r.pos_or_neg as name, "
				+ " format(s.studydatetime,'yyyy-MM-dd') as date, count(r.pos_or_neg) as num from "+(institutionid!=null?"admission a,":"")+"studyorder s "
				+ " left join report r on r.studyorderfk = s.id where r.pos_or_neg is not null and "+(institutionid!=null?" a.id=s.admissionidfk and a.institutionid = ? and ":"")
				+ " s.studydatetime>? and s.studydatetime<? GROUP BY format ( s.studydatetime, 'yyyy-MM-dd' ), r.pos_or_neg",params.toArray());
		String syscode[] = {"p", "n"};
		Map<String, String[]> map = new HashMap<String, String[]>();
		for (int i=0; i<syscode.length; i++) {
			String name = syscode[i];
			Map<String,String> map_value = list.stream().filter(r -> r.getStr("name").equals(name)).collect(Collectors.toMap(a->a.getStr("date"), b -> b.getStr("num")));
			String value[] = new String[category.length];
			for (int j=0; j<category.length; j++) {
				String data=map_value.get(category[j]);
				value[j]=data!=null?data:"0";
			}
			map.put(name, value);
		}
		for (int i=0; i<category.length; i++) {
			category[i]=category[i].substring(5, 10).replaceAll("-", ".");
		}
		ret.set("category", category);
		ret.set("startdate", startDate);
		ret.set("enddate", endDate);
		ret.set("map", map);
		ret.set("syscode", syscode);
		return ret;
	}

}
