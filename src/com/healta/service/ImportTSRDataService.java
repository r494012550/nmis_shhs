package com.healta.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONObject;

import com.csvreader.CsvReader;
import com.healta.constant.CacheName;
import com.healta.constant.FindingSource;
import com.healta.model.Studyorder;
import com.healta.model.Syngoviafindingfile;
import com.healta.model.Syngoviaimage;
import com.healta.model.Syngoviareport;
import com.healta.model.Syngoviasrdata;
import com.healta.util.MyFileUtils;
import com.healta.util.WebSocketUtils;
import com.healta.vo.WebsocketVO;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.upload.UploadFile;

public class ImportTSRDataService {
	private static final Logger log = Logger.getLogger(ImportTSRDataService.class);
	
	public void importViaLung3DData(String content){
		
		//log content
		try {
			FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("tempdir")+"\\clipboard\\VIA\\text_"+System.currentTimeMillis()+".txt"), content);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		
		String codes[]=new String[]{"Vol","Rel.Vol","MLD","SD","FWHM","LAV","HAV"};
		String segmentations[]=new String[]{"Total","Left","Right","Left Lower Lobe","Left Upper Lobe","Right Lower Lobe","Right Middle Lobe","Right Upper Lobe"};
		
		LocalDateTime now=LocalDateTime.now();
		Document finding=DocumentHelper.createDocument();
		Element roote=finding.addElement("entries","urn:hl7-org:v3").addNamespace("voc", "urn:hl7-org:v3/voc").addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		Element entry=roote.addElement("entry");
		Element observation=createObservation(entry, "OBS", "DICOM", "Finding");
		
		createEntryRelationship(observation, "121071", "DICOM", "name", "Finding 1", "ED", "","","");
		createEntryRelationship(observation, "TimeStamp", "99SMS_SY", "TimeStamp", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), "ED", "","","");
		Element observation_1 = createEntryRelationship_Nest(observation, "OBS", "DICOM", "FindingState");
		createEntryRelationship(observation_1, "findingStateUid", "99SMS_SY", "findingStateUid", StrKit.getRandomUUID(), "ED", "","","");
		createEntryRelationship(observation_1, "G-C0E3", "SRT", "Finding Site", "Lung", "ED", "","T280D0","SRT");
		createEntryRelationship(observation_1, "FindingType", "99SMS_SY", "FindingType", "Lung", "ED", "","GENERIC","PRIVATE_CODE");
		Element observation_2 = createEntryRelationship_Nest(observation_1, "OBS", "DICOM", "Measurement");
		
		char separator = '	';
		CsvReader reader = null;
		String patientname="",patientid="";
		List<Syngoviasrdata> list=new ArrayList<Syngoviasrdata>();
        try {
            //如果生产文件乱码，windows下用gbk，linux用UTF-8
            reader = new CsvReader(new StringReader(content), separator);//new CsvReader("d:\\lung3D_1.txt",separator, Charset.forName("UTF-8"));
            
            // 读取表头
            reader.readHeaders();
            String[] headArray = reader.getHeaders();//获取标题
            //System.out.println(headArray[0] + headArray[1] + headArray[2]);
            int index=-1;
            // 逐条读取记录，直至读完
            while (reader.readRecord()) {
            	
            	String raw=reader.getRawRecord();
            	String[] rawRecord=raw.split("\",\"");
            	String rawRecord1=rawRecord[0].replace("\"", "").replace(",", "");
            	String rawRecord2=rawRecord[1].replace("\"", "").replace(",", "");
            	String rawRecord3=rawRecord[2].replace("\"", "").replace(",", "");
            	Syngoviasrdata data=new Syngoviasrdata();
        		data.setCode("00100010");
        		data.setCodesystemname("DICOM");
        		data.setCodedisplayname("Name");
        		data.setValue(rawRecord1);
        		data.setUnit("");
        		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
        		data.setStudyid("PETCT00018645");
        		list.add(data);
        		createEntryRelationship(observation_2, "00100010", "Name", "Name", rawRecord[0].replace("\"", ""), "ED", "","","");
            	
        		data=new Syngoviasrdata();
        		data.setCode("00100020");
        		data.setCodesystemname("DICOM");
        		data.setCodedisplayname("Mean (Uptake)");
        		data.setValue(rawRecord2);
        		data.setUnit("");
        		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
        		data.setStudyid("PETCT00018645");
        		list.add(data);
        		createEntryRelationship(observation_2, "00100020", "Mean (Uptake)", "Mean (Uptake)", rawRecord[1].replace("\"", ""), "PQ", "","","");
            	
        		data=new Syngoviasrdata();
        		data.setCode("00100030");
        		data.setCodesystemname("DICOM");
        		data.setCodedisplayname("# Std.Dev from mean");
        		data.setValue(rawRecord3);
        		data.setUnit("");
        		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
        		data.setStudyid("PETCT00018645");
        		list.add(data);
        		createEntryRelationship(observation_2, "00100030", "# Std.Dev from mean", "# Std.Dev from mean", rawRecord[2].replace("\"", ""), "PQ", "","","");
            	/*
                // 读一整行
                //System.out.println(reader.getRawRecord());
            	if(StrKit.equals("Amygdala (AAL) (L)", reader.get(0))) {
            		log.info(reader.get(0)+reader.get(1));
            		index=0;
            		patientid=reader.get(1);
            		Syngoviasrdata data=new Syngoviasrdata();
            		data.setCode("00100020");
            		data.setCodesystemname("DICOM");
            		data.setCodedisplayname("PatientID");
            		data.setValue(patientid);
            		data.setUnit("");
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, "00100020", "DICOM", "PatientID", patientid, "ED", "","","");
            	}
            	if(StrKit.equals("Anterior cingulate and paracingulate gyri (AAL) (L)", reader.get(0))) {
            		log.info(reader.get(0)+reader.get(1));
            		index=0;
            		patientname=reader.get(1);
            		Syngoviasrdata data=new Syngoviasrdata();
            		data.setCode("00100010");
            		data.setCodesystemname("DICOM");
            		data.setCodedisplayname("PatientsName");
            		data.setValue(patientname);
            		data.setUnit("");
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, "00100010", "DICOM", "PatientsName", patientname, "ED", "","","");
            	}
                // 读这行的第一列
            	if(StrKit.equals("Caudate nucleus (AAL) (L)", reader.get(0))) {
            		System.out.println(reader.get(0));
                    System.out.println(reader.getCurrentRecord());
                    index=0;
                    Syngoviasrdata data=new Syngoviasrdata();
            		data.setCode("FindingType");
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname("FindingType");
            		data.setValue("GENERAL");
            		data.setUnit("");
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
                    continue;
            	}
            	if(index>=0&&index<7) {
            		System.out.println(reader.get(0));
            		String firstname=reader.get(0);
            		//String unit=firstname.substring(firstname.indexOf("[")+1, firstname.indexOf("]"));
            		String unit="";
            		createEntryRelationship(observation_2, "G-C0E3", "SRT", "Finding Site", reader.get(0), "ED", "","","");
            		Syngoviasrdata data=new Syngoviasrdata();
            		data.setCode("G-C0E3");
            		data.setCodesystemname("SRT");
            		data.setCodedisplayname("Finding Site");
            		data.setValue(reader.get(0));
            		data.setUnit("");
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, segmentations[0], "99SMS_SY", segmentations[0], reader.get(1), "PQ", unit,"","");
            		data=new Syngoviasrdata();
            		data.setCode(segmentations[0]);
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname(segmentations[0]);
            		data.setValue(reader.get(1));
            		data.setUnit(unit);
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, segmentations[1], "99SMS_SY", segmentations[1], reader.get(2), "PQ", unit,"","");
            		data=new Syngoviasrdata();
            		data.setCode(segmentations[1]);
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname(segmentations[1]);
            		data.setValue(reader.get(2));
            		data.setUnit(unit);
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, segmentations[2], "99SMS_SY", segmentations[2], reader.get(3), "PQ", unit,"","");
            		data=new Syngoviasrdata();
            		data.setCode(segmentations[2]);
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname(segmentations[2]);
            		data.setValue(reader.get(3));
            		data.setUnit(unit);
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, segmentations[3], "99SMS_SY", segmentations[3], reader.get(14), "PQ", unit,"","");
            		data=new Syngoviasrdata();
            		data.setCode(segmentations[3]);
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname(segmentations[3]);
            		data.setValue(reader.get(14));
            		data.setUnit(unit);
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, segmentations[4], "99SMS_SY", segmentations[4], reader.get(15), "PQ", unit,"","");
            		data=new Syngoviasrdata();
            		data.setCode(segmentations[4]);
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname(segmentations[4]);
            		data.setValue(reader.get(15));
            		data.setUnit(unit);
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, segmentations[5], "99SMS_SY", segmentations[5], reader.get(16), "PQ", unit,"","");
            		data=new Syngoviasrdata();
            		data.setCode(segmentations[5]);
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname(segmentations[5]);
            		data.setValue(reader.get(16));
            		data.setUnit(unit);
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, segmentations[6], "99SMS_SY", segmentations[6], reader.get(17), "PQ", unit,"","");
            		data=new Syngoviasrdata();
            		data.setCode(segmentations[6]);
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname(segmentations[6]);
            		data.setValue(reader.get(17));
            		data.setUnit(unit);
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, segmentations[7], "99SMS_SY", segmentations[7], reader.get(18), "PQ", unit,"","");
            		Syngoviasrdata data1=new Syngoviasrdata();
            		data1.setCode(segmentations[7]);
            		data1.setCodesystemname("99SMS_SY");
            		data1.setCodedisplayname(segmentations[7]);
            		data1.setValue(reader.get(18));
            		data1.setUnit(unit);
            		data1.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data1);
            		index++;
            	}
            	if(index>=7) {
            		break;
            	}*/
            	
//                System.out.println(reader.get(0));
//                System.out.println(reader.getCurrentRecord());
                // 读这行的第二列
                //System.out.println(reader.get(1));
            }
            
            String basePath=PropKit.use("system.properties").get("sysdir")
        			+System.getProperty("file.separator");
	        String filename=now.format(DateTimeFormatter.BASIC_ISO_DATE)
			+System.getProperty("file.separator")+"clipboard"+System.getProperty("file.separator")+"csv测试"+".xml";
	        
	        try {
				FileUtils.writeStringToFile(new File(basePath+filename), finding.asXML(),"UTF-8");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
	        Syngoviareport viareport=new Syngoviareport();
	        viareport.setReportname("PARENCHYMA ANALYSIS REPORT");
	        viareport.setCdafile(filename);
	        viareport.setStudyid("PETCT00018645");
	        viareport.save();
	        
	        Syngoviafindingfile findingfile= new Syngoviafindingfile();
	        findingfile.setFindingname("LUNG 3D GENERAL");
	        findingfile.setFindingfile(filename);
	        findingfile.setFindingindex(0);
	        findingfile.setSource(FindingSource.SYNGOVIA_CLIPBOARD_LUNG3D);
	        findingfile.setStudyid("PETCT00018645");
	        findingfile.save();
	        
	        List<Object> cachelist=new ArrayList<Object>();
	        cachelist.add(viareport);
	        cachelist.add(findingfile);
	        cachelist.add(list);
	 
	        String uuid=StrKit.getRandomUUID();
	        CacheKit.put(CacheName.SYNGOVIA_CLIPBOARD_DATA, uuid, cachelist);
	        Db.batchSave(list, list.size());
	        
	        JSONObject da=new JSONObject();
	    	da.put("patientid", patientid);
	    	da.put("patientname", patientname);
	    	da.put("uuid", uuid);
	    	da.put("source", FindingSource.SYNGOVIA_CLIPBOARD_LUNG3D);
	    	da.put("viareportid", viareport.getId());
	    	da.put("viafindingfileid", findingfile.getId());
	        
	        WebSocketUtils.broadcastMessage(new WebsocketVO(WebsocketVO.RECEIVEVIADATA_CLIPBOARD, da.toString()).toJson());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                reader.close();
            }
        }
	}
	
	public static Element createObservation(Element parent,String code,String codeSystemName,String codeDisplayName) {
		Element observation=parent.addElement("observation").addAttribute("moodCode", "EVN").addAttribute("classCode", "OBS");
		observation.addElement("code").addAttribute("displayName", codeDisplayName).addAttribute("codeSystemName", codeSystemName).addAttribute("code", code);
		
		return observation;
	}
	
	public static Element createEntryRelationship(Element parent,String code,String codeSystemName,String codeDisplayName,String value,String valuetype,String unit,String valuecode,String valueCodeSystemName) {
		Element entryRelationship=parent.addElement("entryRelationship").addAttribute("typeCode", "REFR");
		Element observation = entryRelationship.addElement("observation").addAttribute("moodCode", "EVN").addAttribute("classCode", "OBS");
		observation.addElement("templateId").addAttribute("root", "2.16.840.1.113883.10.20.6.2.12");
		observation.addElement("code").addAttribute("displayName", codeDisplayName).addAttribute("codeSystemName", codeSystemName).addAttribute("code", code);
		Element valueele=observation.addElement("value").addAttribute("xsi:type", valuetype);
		
		if(StrKit.notBlank(valuecode)) {
			valueele.addAttribute("code", valuecode);
		}
		if(StrKit.notBlank(valueCodeSystemName)) {
			valueele.addAttribute("codeSystemName", valueCodeSystemName);
		}
		
		if(StrKit.equals("PQ", valuetype)) {
			valueele.addAttribute("value", value).addAttribute("unit", unit);
		} else {
			valueele.addText(value);
		}
		return entryRelationship;
	}
	
	public static Element createEntryRelationship_Nest(Element parent,String code,String codeSystemName,String codeDisplayName) {
		Element entryRelationship=parent.addElement("entryRelationship").addAttribute("typeCode", "REFR");
		Element observation = entryRelationship.addElement("observation").addAttribute("moodCode", "EVN").addAttribute("classCode", "OBS");
		observation.addElement("code").addAttribute("displayName", codeDisplayName).addAttribute("codeSystemName", codeSystemName).addAttribute("code", code);
		return observation;
	}
	
	public static Element createEntry(Element parent,String imageID) {
		Element entry=parent.addElement("entry").addAttribute("typeCode", "COMP").addAttribute("contextConductionInd", "true");
		Element observationMedia = entry.addElement("observationMedia").addAttribute("classCode", "OBS").addAttribute("moodCode", "EVN").addAttribute("ID", imageID);
		return entry;
	}
	
	public boolean importUnitedAIData(File file){
		
		return Db.tx(()->{
			boolean ret=true;
			try {
				
				FileInputStream fileInputStream = new FileInputStream(file);
				XSSFWorkbook wb=new XSSFWorkbook(fileInputStream);
				XSSFSheet sheet_total =wb.getSheet("全肺");
				XSSFSheet sheet_left =wb.getSheet("左肺");
				XSSFSheet sheet_right =wb.getSheet("右肺");
				for(int i=2,len=sheet_total.getLastRowNum();i<len;i++){
					XSSFRow row = sheet_total.getRow(i);
					if(row!=null&&row.getCell(0)!=null) {
						List<Syngoviasrdata> list=new ArrayList<Syngoviasrdata>();
						String studyid=null,patientname=null,studydate=null,patientid=null;
						patientname=row.getCell(0).getStringCellValue();
						if(row.getCell(3)!=null) {
							studyid=getCellValueStr(row.getCell(3));//"UCT"+
						}
						if(row.getCell(1)!=null) {
							studydate=getCellValueStr(row.getCell(1));
						}
						if(row.getCell(2)!=null) {
							patientid=getCellValueStr(row.getCell(2));//"P"+
						}
						
						LocalDateTime now=LocalDateTime.now();
						Document finding=DocumentHelper.createDocument();
						Element roote=finding.addElement("entries","urn:hl7-org:v3").addNamespace("voc", "urn:hl7-org:v3/voc").addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
						Element entry=roote.addElement("entry");
						Element observation=createObservation(entry, "OBS", "DICOM", "Finding");
						
						createEntryRelationship(observation, "121071", "DICOM", "name", "Finding United AI", "ED", "","","");
						createEntryRelationship(observation, "TimeStamp", "99SMS_SY", "TimeStamp", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), "ED", "","","");
						Element observation_1 = createEntryRelationship_Nest(observation, "OBS", "DICOM", "FindingState");
						createEntryRelationship(observation_1, "findingStateUid", "99SMS_SY", "findingStateUid", StrKit.getRandomUUID(), "ED", "","","");
						createEntryRelationship(observation_1, "G-C0E3", "SRT", "Finding Site", "Lung", "ED", "","T280D0","SRT");
						createEntryRelationship(observation_1, "FindingType", "99SMS_SY", "FindingType", "Lung United AI", "ED", "","GENERIC","PRIVATE_CODE");
						Element observation_2 = createEntryRelationship_Nest(observation_1, "OBS", "DICOM", "Measurement");
						
						if(StrKit.notBlank(patientid)) {
		            		Syngoviasrdata data=new Syngoviasrdata();
		            		data.setCode("00100020");
		            		data.setCodesystemname("DICOM");
		            		data.setCodedisplayname("PatientID");
		            		data.setValue(patientid);
		            		data.setUnit("");
		            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
		            		list.add(data);
		            		createEntryRelationship(observation_2, "00100020", "DICOM", "PatientID", patientid, "ED", "","","");
		            	}
		            	if(StrKit.notBlank(patientname)) {
		            		Syngoviasrdata data=new Syngoviasrdata();
		            		data.setCode("00100010");
		            		data.setCodesystemname("DICOM");
		            		data.setCodedisplayname("PatientsName");
		            		data.setValue(patientname);
		            		data.setUnit("");
		            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
		            		list.add(data);
		            		createEntryRelationship(observation_2, "00100010", "DICOM", "PatientsName", patientname, "ED", "","","");
		            	}
		            	if(StrKit.notBlank(studydate)) {
		            		Syngoviasrdata data=new Syngoviasrdata();
		            		data.setCode("00080020");
		            		data.setCodesystemname("DICOM");
		            		data.setCodedisplayname("StudyDate");
		            		data.setValue(studydate);
		            		data.setUnit("");
		            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
		            		list.add(data);
		            		createEntryRelationship(observation_2, "00080020", "DICOM", "StudyDate", studydate, "ED", "","","");
		            	}
		            	if(StrKit.notBlank(studyid)) {
		            		Syngoviasrdata data=new Syngoviasrdata();
		            		data.setCode("00200010");
		            		data.setCodesystemname("DICOM");
		            		data.setCodedisplayname("StudyID");
		            		data.setValue(studyid);
		            		data.setUnit("");
		            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
		            		list.add(data);
		            		createEntryRelationship(observation_2, "00200010", "DICOM", "StudyID", studyid, "ED", "","","");
		            	}
		            	
		            	//正常解剖结构finding
		            	String average_density="",quality="",volume="";
		            	if(row.getCell(4)!=null){
		            		average_density=String.valueOf(row.getCell(4).getNumericCellValue());
		            	}
		            	if(row.getCell(5)!=null){
		            		quality=String.valueOf(row.getCell(5).getNumericCellValue());
		            	}
		            	if(row.getCell(6)!=null){
		            		volume=String.valueOf(row.getCell(6).getNumericCellValue());
		            	}
		            	
		            	
		            	createEntryRelationship(observation_2, "G-C0E3", "SRT", "Finding Site", "Total", "ED", "","Whole Lung","Whole Lung");
		            	Syngoviasrdata data_fs=new Syngoviasrdata();
		            	data_fs.setCode("G-C0E3");
		            	data_fs.setCodesystemname("SRT");
		            	data_fs.setCodedisplayname("Finding Site");
		            	data_fs.setValue("Total");
		            	data_fs.setValuecode("Whole Lung");
		            	data_fs.setValuecodesystemname("Whole Lung");
		            	data_fs.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data_fs);
		            	
//	            		if(StrKit.notBlank(average_density)){
	            			Syngoviasrdata data=new Syngoviasrdata();
	                		data.setCode("112118");
	                		data.setCodesystemname("DCM");
	                		data.setCodedisplayname("Density");
	                		data.setValue(average_density);
	                		data.setUnit("HU");
	                		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                		list.add(data);
	                		createEntryRelationship(observation_2, "112118", "DCM", "Density", average_density, "PQ", "HU","","");
//	            		}
//		            	if(StrKit.notBlank(quality)){
		            		data=new Syngoviasrdata();
	                		data.setCode("G-C287");
	                		data.setCodesystemname("SRT");
	                		data.setCodedisplayname("Quality");
	                		data.setValue(quality);
	                		data.setUnit("mg");
	                		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                		list.add(data);
	                		createEntryRelationship(observation_2, "G-C287", "SRT", "Quality", quality, "PQ", "mg","","");
//		            	}
//		            	if(StrKit.notBlank(volume)){
		            		data=new Syngoviasrdata();
	                		data.setCode("G-D705");
	                		data.setCodesystemname("SRT");
	                		data.setCodedisplayname("Volume");
	                		data.setValue(volume);
	                		data.setUnit("cm3");
	                		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                		list.add(data);
	                		createEntryRelationship(observation_2, "G-D705", "SRT", "Volume", volume, "PQ", "cm3","","");
//		            	}
		            	
		            	//感染区域finding
		            	String lesion_average_density="",lesion_quality="",lesion_volume="",lesion_volume_ratio="",lesion_ggo_volume="",lesion_solid_volume="";
		            	if(row.getCell(11)!=null){
		            		lesion_average_density=String.valueOf(row.getCell(11).getNumericCellValue());
		            	}
		            	if(row.getCell(12)!=null){
		            		lesion_quality=String.valueOf(row.getCell(12).getNumericCellValue());
		            	}
		            	if(row.getCell(13)!=null){
		            		lesion_volume=String.valueOf(row.getCell(13).getNumericCellValue());
		            	}
		            	if(row.getCell(14)!=null){
		            		lesion_volume_ratio=String.valueOf(row.getCell(14).getNumericCellValue());
		            	}
		            	if(row.getCell(15)!=null&&row.getCell(16)!=null){
		            		lesion_ggo_volume=String.valueOf(Math.round((row.getCell(15).getNumericCellValue()+row.getCell(16).getNumericCellValue())*100)/100.0);
		            	}
		            	if(row.getCell(17)!=null&&row.getCell(18)!=null){
		            		lesion_solid_volume=String.valueOf(Math.round((row.getCell(17).getNumericCellValue()+row.getCell(18).getNumericCellValue())*100)/100.0);
		            	}
		            	
		            	data=new Syngoviasrdata();
	            		data.setCode("112118");
	            		data.setCodesystemname("DCM");
	            		data.setCodedisplayname("Density");
	            		data.setValue(lesion_average_density);
	            		data.setUnit("HU");
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		createEntryRelationship(observation_2, "112118", "DCM", "Density", lesion_average_density, "PQ", "HU","","lesion");
	            		data=new Syngoviasrdata();
	            		data.setCode("G-C287");
	            		data.setCodesystemname("SRT");
	            		data.setCodedisplayname("Quality");
	            		data.setValue(lesion_quality);
	            		data.setUnit("mg");
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		createEntryRelationship(observation_2, "G-C287", "SRT", "Quality", lesion_quality, "PQ", "mg","","lesion");
	            		data=new Syngoviasrdata();
	            		data.setCode("G-D705");
	            		data.setCodesystemname("SRT");
	            		data.setCodedisplayname("Volume");
	            		data.setValue(lesion_volume);
	            		data.setUnit("cm3");
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		createEntryRelationship(observation_2, "G-D705", "SRT", "Volume", lesion_volume, "PQ", "cm3","","lesion");
		            	///////////////////////////////////////
	            		data=new Syngoviasrdata();
	            		data.setCode("101256");
	            		data.setCodesystemname("99_MI");
	            		data.setCodedisplayname("Volume Ratio");
	            		data.setValue(lesion_volume_ratio);
	            		data.setUnit("%");
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		createEntryRelationship(observation_2, "101256", "99_MI", "Volume Ratio", lesion_volume_ratio, "PQ", "%","","lesion");
	            		data=new Syngoviasrdata();
	            		data.setCode("G-D705");
	            		data.setCodesystemname("SRT");
	            		data.setCodedisplayname("Volume");
	            		data.setValue(lesion_ggo_volume);
	            		data.setUnit("cm3");
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		createEntryRelationship(observation_2, "G-D705", "SRT", "Volume", lesion_ggo_volume, "PQ", "cm3","","lesion_ggo");
	            		data=new Syngoviasrdata();
	            		data.setCode("G-D705");
	            		data.setCodesystemname("SRT");
	            		data.setCodedisplayname("Volume");
	            		data.setValue(lesion_solid_volume);
	            		data.setUnit("cm3");
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		createEntryRelationship(observation_2, "G-D705", "SRT", "Volume", lesion_solid_volume, "PQ", "cm3","","lesion_solid");
		            	
	            		//左肺
	            		if(sheet_left.getRow(i)!=null){
	            			XSSFRow row_left = sheet_left.getRow(i);
	            			if(row_left.getCell(3)!=null&&StrKit.equals(getCellValueStr(row_left.getCell(3)), studyid)){//"UCT"+
	            				
	            				//正常解剖结构finding
	        	            	String average_density_left="",quality_left="",volume_left="";
	        	            	if(row_left.getCell(4)!=null){
	        	            		average_density_left=String.valueOf(row_left.getCell(4).getNumericCellValue());
	        	            	}
	        	            	if(row_left.getCell(5)!=null){
	        	            		quality_left=String.valueOf(row_left.getCell(5).getNumericCellValue());
	        	            	}
	        	            	if(row_left.getCell(6)!=null){
	        	            		volume_left=String.valueOf(row_left.getCell(6).getNumericCellValue());
	        	            	}
	        	            	Syngoviasrdata data_fs_left=new Syngoviasrdata();
	        	            	data_fs_left.setCode("G-C0E3");
	        	            	data_fs_left.setCodesystemname("SRT");
	        	            	data_fs_left.setCodedisplayname("Finding Site");
	        	            	data_fs_left.setValue("Left");
	        	            	data_fs_left.setValuecode("Left Lung");
	        	            	data_fs_left.setValuecodesystemname("Left Lung");
	        	            	data_fs_left.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                    		list.add(data_fs_left);
	                    		createEntryRelationship(observation_2, "G-C0E3", "SRT", "Finding Site", "Left", "ED", "","Left Lung","Left Lung");
	                    			data=new Syngoviasrdata();
	                        		data.setCode("112118");
	                        		data.setCodesystemname("DCM");
	                        		data.setCodedisplayname("Density");
	                        		data.setValue(average_density_left);
	                        		data.setUnit("HU");
	                        		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                        		list.add(data);
	                        		createEntryRelationship(observation_2, "112118", "DCM", "Density", average_density_left, "PQ", "HU","","");

	        	            		data=new Syngoviasrdata();
	                        		data.setCode("G-C287");
	                        		data.setCodesystemname("SRT");
	                        		data.setCodedisplayname("Quality");
	                        		data.setValue(quality_left);
	                        		data.setUnit("mg");
	                        		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                        		list.add(data);
	                        		createEntryRelationship(observation_2, "G-C287", "SRT", "Quality", quality_left, "PQ", "mg","","");

	        	            		data=new Syngoviasrdata();
	                        		data.setCode("G-D705");
	                        		data.setCodesystemname("SRT");
	                        		data.setCodedisplayname("Volume");
	                        		data.setValue(volume_left);
	                        		data.setUnit("cm3");
	                        		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                        		list.add(data);
	                        		createEntryRelationship(observation_2, "G-D705", "SRT", "Volume", volume_left, "PQ", "cm3","","");
	        	            	
	        	            	//感染区域finding
	        	            	String lesion_average_density_left="",lesion_quality_left="",lesion_volume_left="",lesion_volume_ratio_left="",lesion_ggo_volume_left="",lesion_solid_volume_left="";
	        	            	if(row_left.getCell(11)!=null){
	        	            		lesion_average_density_left=String.valueOf(row_left.getCell(11).getNumericCellValue());
	        	            	}
	        	            	if(row_left.getCell(12)!=null){
	        	            		lesion_quality_left=String.valueOf(row_left.getCell(12).getNumericCellValue());
	        	            	}
	        	            	if(row_left.getCell(13)!=null){
	        	            		lesion_volume_left=String.valueOf(row_left.getCell(13).getNumericCellValue());
	        	            	}
	        	            	if(row_left.getCell(14)!=null){
	        	            		lesion_volume_ratio_left=String.valueOf(row_left.getCell(14).getNumericCellValue());
	        	            	}
	        	            	if(row_left.getCell(15)!=null&&row_left.getCell(16)!=null){
	        	            		lesion_ggo_volume_left=String.valueOf(Math.round((row_left.getCell(15).getNumericCellValue()+row_left.getCell(16).getNumericCellValue())*100)/100.0);
	        	            	}
	        	            	if(row_left.getCell(17)!=null&&row_left.getCell(18)!=null){
	        	            		lesion_solid_volume_left=String.valueOf(Math.round((row_left.getCell(17).getNumericCellValue()+row_left.getCell(18).getNumericCellValue())*100)/100.0);
	        	            	}
	        	            	
	        	            	data=new Syngoviasrdata();
	                    		data.setCode("112118");
	                    		data.setCodesystemname("DCM");
	                    		data.setCodedisplayname("Density");
	                    		data.setValue(lesion_average_density_left);
	                    		data.setUnit("HU");
	                    		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                    		list.add(data);
	                    		createEntryRelationship(observation_2, "112118", "DCM", "Density", lesion_average_density_left, "PQ", "HU","","lesion");
	                    		data=new Syngoviasrdata();
	                    		data.setCode("G-C287");
	                    		data.setCodesystemname("SRT");
	                    		data.setCodedisplayname("Quality");
	                    		data.setValue(lesion_quality_left);
	                    		data.setUnit("mg");
	                    		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                    		list.add(data);
	                    		createEntryRelationship(observation_2, "G-C287", "SRT", "Quality", lesion_quality_left, "PQ", "mg","","lesion");
	                    		data=new Syngoviasrdata();
	                    		data.setCode("G-D705");
	                    		data.setCodesystemname("SRT");
	                    		data.setCodedisplayname("Volume");
	                    		data.setValue(lesion_volume_left);
	                    		data.setUnit("cm3");
	                    		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                    		list.add(data);
	                    		createEntryRelationship(observation_2, "G-D705", "SRT", "Volume", lesion_volume_left, "PQ", "cm3","","lesion");
	        	            	///////////////////////////////////////
	                    		data=new Syngoviasrdata();
	                    		data.setCode("101256");
	                    		data.setCodesystemname("99_MI");
	                    		data.setCodedisplayname("Volume Ratio");
	                    		data.setValue(lesion_volume_ratio_left);
	                    		data.setUnit("%");
	                    		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                    		list.add(data);
	                    		createEntryRelationship(observation_2, "101256", "99_MI", "Volume Ratio", lesion_volume_ratio_left, "PQ", "%","","lesion");
	                    		data=new Syngoviasrdata();
	                    		data.setCode("G-D705");
	                    		data.setCodesystemname("SRT");
	                    		data.setCodedisplayname("Volume");
	                    		data.setValue(lesion_ggo_volume_left);
	                    		data.setUnit("cm3");
	                    		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                    		list.add(data);
	                    		createEntryRelationship(observation_2, "G-D705", "SRT", "Volume", lesion_ggo_volume_left, "PQ", "cm3","","lesion_ggo");
	                    		data=new Syngoviasrdata();
	                    		data.setCode("G-D705");
	                    		data.setCodesystemname("SRT");
	                    		data.setCodedisplayname("Volume");
	                    		data.setValue(lesion_solid_volume_left);
	                    		data.setUnit("cm3");
	                    		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                    		list.add(data);
	                    		createEntryRelationship(observation_2, "G-D705", "SRT", "Volume", lesion_solid_volume_left, "PQ", "cm3","","lesion_solid");
	            			}
	            		}
	            		//右肺
	            		if(sheet_right.getRow(i)!=null){
	            			XSSFRow row_right = sheet_right.getRow(i);
	            			if(row_right.getCell(3)!=null&&StrKit.equals(getCellValueStr(row_right.getCell(3)), studyid)){//"UCT"+
	            				
	            				//正常解剖结构finding
	        	            	String average_density_right="",quality_right="",volume_right="";
	        	            	if(row_right.getCell(4)!=null){
	        	            		average_density_right=String.valueOf(row_right.getCell(4).getNumericCellValue());
	        	            	}
	        	            	if(row_right.getCell(5)!=null){
	        	            		quality_right=String.valueOf(row_right.getCell(5).getNumericCellValue());
	        	            	}
	        	            	if(row_right.getCell(6)!=null){
	        	            		volume_right=String.valueOf(row_right.getCell(6).getNumericCellValue());
	        	            	}
	        	            	Syngoviasrdata data_fs_right=new Syngoviasrdata();
	        	            	data_fs_right.setCode("G-C0E3");
	        	            	data_fs_right.setCodesystemname("SRT");
	        	            	data_fs_right.setCodedisplayname("Finding Site");
	        	            	data_fs_right.setValue("Right");
	        	            	data_fs_right.setValuecode("Right Lung");
	        	            	data_fs_right.setValuecodesystemname("Right Lung");
	        	            	data_fs_right.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                    		list.add(data_fs_right);
	                    		createEntryRelationship(observation_2, "G-C0E3", "SRT", "Finding Site", "Right", "ED", "","Right Lung","Right Lung");
	                    			data=new Syngoviasrdata();
	                        		data.setCode("112118");
	                        		data.setCodesystemname("DCM");
	                        		data.setCodedisplayname("Density");
	                        		data.setValue(average_density_right);
	                        		data.setUnit("HU");
	                        		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                        		list.add(data);
	                        		createEntryRelationship(observation_2, "112118", "DCM", "Density", average_density_right, "PQ", "HU","","");

	        	            		data=new Syngoviasrdata();
	                        		data.setCode("G-C287");
	                        		data.setCodesystemname("SRT");
	                        		data.setCodedisplayname("Quality");
	                        		data.setValue(quality_right);
	                        		data.setUnit("mg");
	                        		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                        		list.add(data);
	                        		createEntryRelationship(observation_2, "G-C287", "SRT", "Quality", quality_right, "PQ", "mg","","");

	        	            		data=new Syngoviasrdata();
	                        		data.setCode("G-D705");
	                        		data.setCodesystemname("SRT");
	                        		data.setCodedisplayname("Volume");
	                        		data.setValue(volume_right);
	                        		data.setUnit("cm3");
	                        		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                        		list.add(data);
	                        		createEntryRelationship(observation_2, "G-D705", "SRT", "Volume", volume_right, "PQ", "cm3","","");
	        	            	
	        	            	//感染区域finding
	        	            	String lesion_average_density_right="",lesion_quality_right="",lesion_volume_right="",
	        	            			lesion_volume_ratio_right="",lesion_ggo_volume_right="",lesion_solid_volume_right="";
	        	            	if(row_right.getCell(11)!=null){
	        	            		lesion_average_density_right=String.valueOf(row_right.getCell(11).getNumericCellValue());
	        	            	}
	        	            	if(row_right.getCell(12)!=null){
	        	            		lesion_quality_right=String.valueOf(row_right.getCell(12).getNumericCellValue());
	        	            	}
	        	            	if(row_right.getCell(13)!=null){
	        	            		lesion_volume_right=String.valueOf(row_right.getCell(13).getNumericCellValue());
	        	            	}
	        	            	if(row_right.getCell(14)!=null){
	        	            		lesion_volume_ratio_right=String.valueOf(row_right.getCell(14).getNumericCellValue());
	        	            	}
	        	            	if(row_right.getCell(15)!=null&&row_right.getCell(16)!=null){
	        	            		lesion_ggo_volume_right=String.valueOf(Math.round((row_right.getCell(15).getNumericCellValue()+row_right.getCell(16).getNumericCellValue())*100)/100.0);
	        	            	}
	        	            	if(row_right.getCell(17)!=null&&row_right.getCell(18)!=null){
	        	            		lesion_solid_volume_right=String.valueOf(Math.round((row_right.getCell(17).getNumericCellValue()+row_right.getCell(18).getNumericCellValue())*100)/100.0);
	        	            	}
	        	            	
	        	            	data=new Syngoviasrdata();
	                    		data.setCode("112118");
	                    		data.setCodesystemname("DCM");
	                    		data.setCodedisplayname("Density");
	                    		data.setValue(lesion_average_density_right);
	                    		data.setUnit("HU");
	                    		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                    		list.add(data);
	                    		createEntryRelationship(observation_2, "112118", "DCM", "Density", lesion_average_density_right, "PQ", "HU","","lesion");
	                    		data=new Syngoviasrdata();
	                    		data.setCode("G-C287");
	                    		data.setCodesystemname("SRT");
	                    		data.setCodedisplayname("Quality");
	                    		data.setValue(lesion_quality_right);
	                    		data.setUnit("mg");
	                    		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                    		list.add(data);
	                    		createEntryRelationship(observation_2, "G-C287", "SRT", "Quality", lesion_quality_right, "PQ", "mg","","lesion");
	                    		data=new Syngoviasrdata();
	                    		data.setCode("G-D705");
	                    		data.setCodesystemname("SRT");
	                    		data.setCodedisplayname("Volume");
	                    		data.setValue(lesion_volume_right);
	                    		data.setUnit("cm3");
	                    		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                    		list.add(data);
	                    		createEntryRelationship(observation_2, "G-D705", "SRT", "Volume", lesion_volume_right, "PQ", "cm3","","lesion");
	        	            	///////////////////////////////////////
	                    		data=new Syngoviasrdata();
	                    		data.setCode("101256");
	                    		data.setCodesystemname("99_MI");
	                    		data.setCodedisplayname("Volume Ratio");
	                    		data.setValue(lesion_volume_ratio_right);
	                    		data.setUnit("%");
	                    		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                    		list.add(data);
	                    		createEntryRelationship(observation_2, "101256", "99_MI", "Volume Ratio", lesion_volume_ratio_right, "PQ", "%","","lesion");
	                    		data=new Syngoviasrdata();
	                    		data.setCode("G-D705");
	                    		data.setCodesystemname("SRT");
	                    		data.setCodedisplayname("Volume");
	                    		data.setValue(lesion_ggo_volume_right);
	                    		data.setUnit("cm3");
	                    		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                    		list.add(data);
	                    		createEntryRelationship(observation_2, "G-D705", "SRT", "Volume", lesion_ggo_volume_right, "PQ", "cm3","","lesion_ggo");
	                    		data=new Syngoviasrdata();
	                    		data.setCode("G-D705");
	                    		data.setCodesystemname("SRT");
	                    		data.setCodedisplayname("Volume");
	                    		data.setValue(lesion_solid_volume_right);
	                    		data.setUnit("cm3");
	                    		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	                    		list.add(data);
	                    		createEntryRelationship(observation_2, "G-D705", "SRT", "Volume", lesion_solid_volume_right, "PQ", "cm3","","lesion_solid");
	            			}
	            		}
	            		
	            		
	            		String basePath=PropKit.use("system.properties").get("sysdir")
	                			+System.getProperty("file.separator");
	        	        String filename=now.format(DateTimeFormatter.BASIC_ISO_DATE)
	        			+System.getProperty("file.separator")+"import"+System.getProperty("file.separator")+studyid+".xml";
	        	        
	        	        try {
	        				FileUtils.writeStringToFile(new File(basePath+filename), finding.asXML(),"UTF-8");
	        			} catch (IOException e1) {
	        				// TODO Auto-generated catch block
	        				e1.printStackTrace();
	        			}
	        	        

	        	        Syngoviareport report=Syngoviareport.dao.findFirst("select top 1 * from syngoviareport where studyid=? ",studyid);
						if (report==null) {
							report=new Syngoviareport();
							report.setStudyid(studyid);
							report.setReportname("UNITEDAI PARENCHYMA ANALYSIS REPORT");
							report.setCdafile(filename);
							ret=ret&report.save();
						}
						
						Db.delete("delete from syngoviafindingfile where studyid=? and source=?",studyid,FindingSource.UNITEDAI_LUNG);
	        	        Syngoviafindingfile findingfile= new Syngoviafindingfile();
	        	        findingfile.setStudyid(studyid);
	        	        findingfile.setFindingname("UNITEDAI LUNG");
	        	        findingfile.setFindingfile(filename);
	        	        findingfile.setFindingindex(0);
	        	        findingfile.setSource(FindingSource.UNITEDAI_LUNG);
	        	        findingfile.setPatientid(patientid);
	        	        if(StrKit.notBlank(studydate)){
	        	        	LocalDate date=LocalDate.parse(studydate, DateTimeFormatter.ofPattern("yyyyMMdd"));
	        	        	Instant instant = date.atStartOfDay().atZone( ZoneId.systemDefault()).toInstant();
	        	        	findingfile.setStudydatetime(Date.from(instant));
	        	        }
	        	        
	        	        ret=ret&findingfile.save();

	        	        for(Syngoviasrdata da:list){
	        	        	da.setStudyid(studyid);
	        	        	da.setSource(FindingSource.UNITEDAI_LUNG);
	        	        }
	        	        Db.delete("delete from syngoviasrdata where studyid=? and source=?",studyid,FindingSource.UNITEDAI_LUNG);
	            		Db.batchSave(list, list.size());
		            	
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ret;
		});
	}
	
	public String getCellValueStr(XSSFCell cell) {
		switch(cell.getCellType()) {
			case XSSFCell.CELL_TYPE_BOOLEAN:
				return cell.getBooleanCellValue()?"true":"false";
			case XSSFCell.CELL_TYPE_NUMERIC:
				return String.valueOf(cell.getNumericCellValue());
			case XSSFCell.CELL_TYPE_BLANK:
				return "";
			default :
				return cell.getStringCellValue();
		}
	}
	
	public boolean importCTAPData(List<UploadFile> files,String studyid){
		
		return Db.tx(()->{
			boolean ret=true;
			String content=null;
			File csv=null;
			List<File> imgs=new ArrayList<File>();
			for(UploadFile file:files) {
				log.info(file.getFileName());
				if(file.getFileName().endsWith(".csv")) {
					csv=file.getFile();
				} else if(file.getFileName().endsWith(".png")) {
					imgs.add(file.getFile());
				}
			}
			if(csv!=null) {
				try {
					content=FileUtils.readFileToString(csv);
					if(!StringUtils.contains(content, "Siemens Healthineers CT Pneumonia Analysis Prototype")) {
						return false;
					}
					//log content
					FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("tempdir")+"\\upload\\VIA_CTPA\\text_"+System.currentTimeMillis()+".txt"), "");
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				
			} else {
				return false;
			}

			
			String codes[]=new String[]{"Affected","Opacity Score","Lung volume (ml)","Volume of opacity (ml)","Percentage of opacity","Volume of high opacity (ml)","Percentage of high opacity","Mean HU total","Mean HU of opacity","Standard deviation total","Standard deviation of opacity"};
			String segmentations[]=new String[]{"Both lungs","Left lung","Right lung","Left upper lobe","Left lower lobe","Right upper lobe","Right middle lobe","Right lower lobe"};
			
			LocalDateTime now=LocalDateTime.now();
			Document finding=DocumentHelper.createDocument();
			Element roote=finding.addElement("entries","urn:hl7-org:v3").addNamespace("voc", "urn:hl7-org:v3/voc").addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			roote.addElement("studyid").addAttribute("studyid", studyid);
			Element entry=roote.addElement("entry");
			Element observation=createObservation(entry, "OBS", "DICOM", "Finding");
			
			createEntryRelationship(observation, "121071", "DICOM", "name", "Finding 1", "ED", "","","");
			createEntryRelationship(observation, "TimeStamp", "99SMS_SY", "TimeStamp", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), "ED", "","","");
			Element observation_1 = createEntryRelationship_Nest(observation, "OBS", "DICOM", "FindingState");
			createEntryRelationship(observation_1, "findingStateUid", "99SMS_SY", "findingStateUid", StrKit.getRandomUUID(), "ED", "","","");
			createEntryRelationship(observation_1, "G-C0E3", "SRT", "Finding Site", "Lung", "ED", "","T280D0","SRT");
			createEntryRelationship(observation_1, "FindingType", "99SMS_SY", "FindingType", "Lung", "ED", "","CTPA","PRIVATE_CODE");
			Element observation_2 = createEntryRelationship_Nest(observation_1, "OBS", "DICOM", "Measurement");
			
			char separator = ',';
			CsvReader reader = null;
			String patientname="",patientid="",seriesDate="",seriesTime="";
			List<Syngoviasrdata> list=new ArrayList<Syngoviasrdata>();
	        try {
	            //如果生产文件乱码，windows下用gbk，linux用UTF-8
	            reader = new CsvReader(new StringReader(content), separator);//new CsvReader("E:\\OneDrive - Siemens Healthineers\\武汉协和\\CTPA\\CT Pneumonia Analysis_2020-08-24_010000\\result.csv",separator, Charset.forName("UTF-8"));// new CsvReader(new StringReader(content), separator);//
	            
	            // 读取表头
	            reader.readHeaders();
	            String[] headArray = reader.getHeaders();//获取标题
	            //System.out.println(headArray[0] + headArray[1] + headArray[2]);
	            int index=-1;
	            // 逐条读取记录，直至读完
	            while (reader.readRecord()) {
	                // 读一整行
	                //System.out.println(reader.getRawRecord());
	            	if(StrKit.equals("PatientsName:", reader.get(0))) {
	            		log.info(reader.get(0)+reader.get(1));
	            		patientname=reader.get(1);
	            		Syngoviasrdata data=new Syngoviasrdata();
	            		data.setCode("00100010");
	            		data.setCodesystemname("DICOM");
	            		data.setCodedisplayname("PatientsName");
	            		data.setValue(patientname);
	            		data.setUnit("");
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		createEntryRelationship(observation_2, "00100010", "DICOM", "PatientsName", patientname, "ED", "","","");
	            	}
	            	if(StrKit.equals("SeriesDate:", reader.get(0))) {
	            		log.info(reader.get(0)+reader.get(1));
	            		seriesDate=reader.get(1);
	            		Syngoviasrdata data=new Syngoviasrdata();
	            		data.setCode("00080021");
	            		data.setCodesystemname("DICOM");
	            		data.setCodedisplayname("SeriesDate");
	            		data.setValue(seriesDate);
	            		data.setUnit("");
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		createEntryRelationship(observation_2, "00080021", "DICOM", "SeriesDate", seriesDate, "ED", "","","");
	            	}
	            	if(StrKit.equals("SeriesTime:", reader.get(0))) {
	            		log.info(reader.get(0)+reader.get(1));
	            		seriesTime=reader.get(1);
	            		Syngoviasrdata data=new Syngoviasrdata();
	            		data.setCode("00080031");
	            		data.setCodesystemname("DICOM");
	            		data.setCodedisplayname("SeriesTime");
	            		data.setValue(seriesTime);
	            		data.setUnit("");
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		createEntryRelationship(observation_2, "00080031", "DICOM", "SeriesTime", seriesTime, "ED", "","","");
	            	}
	                // 读这行的第一列
	            	if(StrKit.equals("Both lungs", reader.get(1))) {
	            		log.info(reader.get(0)+reader.get(1));
	                    index=0;
	                    Syngoviasrdata data=new Syngoviasrdata();
	            		data.setCode("FindingType");
	            		data.setCodesystemname("99SMS_SY");
	            		data.setCodedisplayname("FindingType");
	            		data.setValue("CTPA");
	            		data.setUnit("");
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	                    continue;
	            	}
	            	if(index>=0&&index<11) {
	            		log.info(reader.get(0));
	            		String firstname=reader.get(0);
	            		String unit="";
	            		if(firstname.indexOf("(")>0){
	            			unit=firstname.substring(firstname.indexOf("(")+1, firstname.indexOf(")"));
	            		}
	            		
	            		String val=val(reader.get(0));
	            		createEntryRelationship(observation_2, "G-C0E3", "SRT", "Finding Site", val, "ED", "","","");
	            		Syngoviasrdata data=new Syngoviasrdata();
	            		data.setCode("G-C0E3");
	            		data.setCodesystemname("SRT");
	            		data.setCodedisplayname("Finding Site");
	            		data.setValue(val);
	            		data.setUnit("");
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		val=val(reader.get(1));
	            		createEntryRelationship(observation_2, segmentations[0], "99SMS_SY", segmentations[0], val, "PQ", unit,"","");
	            		data=new Syngoviasrdata();
	            		data.setCode(segmentations[0]);
	            		data.setCodesystemname("99SMS_SY");
	            		data.setCodedisplayname(segmentations[0]);
	            		data.setValue(val);
	            		data.setUnit(unit);
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		val=val(reader.get(2));
	            		createEntryRelationship(observation_2, segmentations[1], "99SMS_SY", segmentations[1], val, "PQ", unit,"","");
	            		data=new Syngoviasrdata();
	            		data.setCode(segmentations[1]);
	            		data.setCodesystemname("99SMS_SY");
	            		data.setCodedisplayname(segmentations[1]);
	            		data.setValue(val);
	            		data.setUnit(unit);
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		val=val(reader.get(3));
	            		createEntryRelationship(observation_2, segmentations[2], "99SMS_SY", segmentations[2], val, "PQ", unit,"","");
	            		data=new Syngoviasrdata();
	            		data.setCode(segmentations[2]);
	            		data.setCodesystemname("99SMS_SY");
	            		data.setCodedisplayname(segmentations[2]);
	            		data.setValue(val);
	            		data.setUnit(unit);
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		val=val(reader.get(4));
	            		createEntryRelationship(observation_2, segmentations[3], "99SMS_SY", segmentations[3], val, "PQ", unit,"","");
	            		data=new Syngoviasrdata();
	            		data.setCode(segmentations[3]);
	            		data.setCodesystemname("99SMS_SY");
	            		data.setCodedisplayname(segmentations[3]);
	            		data.setValue(val);
	            		data.setUnit(unit);
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		val=val(reader.get(5));
	            		createEntryRelationship(observation_2, segmentations[4], "99SMS_SY", segmentations[4], val, "PQ", unit,"","");
	            		data=new Syngoviasrdata();
	            		data.setCode(segmentations[4]);
	            		data.setCodesystemname("99SMS_SY");
	            		data.setCodedisplayname(segmentations[4]);
	            		data.setValue(val);
	            		data.setUnit(unit);
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		val=val(reader.get(6));
	            		createEntryRelationship(observation_2, segmentations[5], "99SMS_SY", segmentations[5], val, "PQ", unit,"","");
	            		data=new Syngoviasrdata();
	            		data.setCode(segmentations[5]);
	            		data.setCodesystemname("99SMS_SY");
	            		data.setCodedisplayname(segmentations[5]);
	            		data.setValue(val);
	            		data.setUnit(unit);
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		val=val(reader.get(7));
	            		createEntryRelationship(observation_2, segmentations[6], "99SMS_SY", segmentations[6], val, "PQ", unit,"","");
	            		data=new Syngoviasrdata();
	            		data.setCode(segmentations[6]);
	            		data.setCodesystemname("99SMS_SY");
	            		data.setCodedisplayname(segmentations[6]);
	            		data.setValue(val);
	            		data.setUnit(unit);
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		val=val(reader.get(8));
	            		createEntryRelationship(observation_2, segmentations[7], "99SMS_SY", segmentations[7], val, "PQ", unit,"","");
	            		data=new Syngoviasrdata();
	            		data.setCode(segmentations[7]);
	            		data.setCodesystemname("99SMS_SY");
	            		data.setCodedisplayname(segmentations[7]);
	            		data.setValue(val);
	            		data.setUnit(unit);
	            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
	            		list.add(data);
	            		index++;
	            	}
	            	if(index>=11) {
	            		break;
	            	}
	            	
//	                System.out.println(reader.get(0));
//	                System.out.println(reader.getCurrentRecord());
	                // 读这行的第二列
	                //System.out.println(reader.get(1));
	            }
	            
	            String basePath=PropKit.use("system.properties").get("sysdir")
	        			+System.getProperty("file.separator");

		        List<Syngoviaimage> imglist=new ArrayList<Syngoviaimage>();
        		for(File imgfile:imgs) {
        			String imageid=StrKit.getRandomUUID();
        			String imgfilename=now.format(DateTimeFormatter.BASIC_ISO_DATE)
        					+System.getProperty("file.separator")+"ctpa"+System.getProperty("file.separator")+patientname+"_"+seriesDate+seriesTime+System.getProperty("file.separator")+"image"+System.getProperty("file.separator")+imageid+".png";
        			FileUtils.moveFile(imgfile, new File(basePath+imgfilename));
        			createEntry(roote, imageid);
        			Syngoviaimage img=new Syngoviaimage();
        			img.setStudyid(studyid);
        			img.setImagefile(imgfilename);
        			img.setImageid(imageid);
        			img.setSource(FindingSource.CTPA_LUNG);
        			imglist.add(img);
        		}
        		Db.delete("update syngoviaimage set delflag=1 where studyid=? and source=?",studyid,FindingSource.CTPA_LUNG);
        		Db.batchSave(imglist, imglist.size());
		        
        		
        		String filename=now.format(DateTimeFormatter.BASIC_ISO_DATE)
        				+System.getProperty("file.separator")+"ctpa"+System.getProperty("file.separator")+patientname+"_"+seriesDate+seriesTime+System.getProperty("file.separator")+patientname+"_"+seriesDate+seriesTime+".xml";
		        try {
					FileUtils.writeStringToFile(new File(basePath+filename), finding.asXML(),"UTF-8");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        Syngoviareport report=Syngoviareport.dao.findFirst("select top 1 * from syngoviareport where studyid=? ",studyid);
				if (report==null) {
					report=new Syngoviareport();
					report.setStudyid(studyid);
					report.setReportname("Siemens Healthineers CT Pneumonia Analysis Prototype");
					report.setCdafile(filename);
					ret=ret&report.save();
				}

				Db.delete("delete from syngoviafindingfile where studyid=? and source=?",studyid,FindingSource.CTPA_LUNG);
		        Syngoviafindingfile findingfile= new Syngoviafindingfile();
		        findingfile.setFindingname("CTPA");
		        findingfile.setFindingfile(filename);
		        findingfile.setFindingindex(0);
		        findingfile.setSource(FindingSource.CTPA_LUNG);
		        findingfile.setStudyid(studyid);
		        ret=ret&findingfile.save();

    	        for(Syngoviasrdata da:list){
    	        	da.setStudyid(studyid);
    	        	da.setSource(FindingSource.CTPA_LUNG);
    	        }
    	        Db.delete("delete from syngoviasrdata where studyid=? and source=?",studyid,FindingSource.CTPA_LUNG);
        		Db.batchSave(list, list.size());
        		
        		JSONObject da=new JSONObject();
     	    	//da.put("studyinsuid", studyinsuid);
     	    	da.put("studyid", studyid);
     	    	
     	    	Studyorder order=Studyorder.dao.getStudyorderByStudyid(studyid);
     	    	if(order!=null){
     	    		da.put("orderid", order.getId());
     	    		log.info(da.toString());
         	    	WebSocketUtils.broadcastMessage(new WebsocketVO(WebsocketVO.RECEIVEVIAREPORT, da.toString()).toJson());
     	    	}
     	    	
     	    	
//		        findingfile.save();
		        
//		        List<Object> cachelist=new ArrayList<Object>();
//		        cachelist.add(viareport);
//		        cachelist.add(findingfile);
//		        cachelist.add(list);
//		 
//		        String uuid=StrKit.getRandomUUID();
//		        CacheKit.put(CacheName.CTPA_DATA, uuid, cachelist);
//		        Db.batchSave(list, list.size());
		        
//		        JSONObject da=new JSONObject();
//		    	da.put("patientid", patientid);
//		    	da.put("patientname", patientname);
//		    	da.put("uuid", uuid);
//		    	da.put("source", FindingSource.CTPA_LUNG);
//		    	da.put("viareportid", viareport.getId());
//		    	da.put("viafindingfileid", findingfile.getId());
		        
//		        WebSocketUtils.broadcastMessage(new WebsocketVO(WebsocketVO.RECEIVEVIADATA_CLIPBOARD, da.toString()).toJson());
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (null != reader) {
	                reader.close();
	            }
	        }
			
			return ret;
		});
	}
	
	public String val(String val) {
		if(val.indexOf(".")>=0) {
			val=String.valueOf(Math.round(Float.valueOf(val)*100)/100.0);
		}
		return val;
	}
	
	public static void main(String arg[]){
//		try {
//			FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("tempdir")+"\\clipboard\\VIA_CTPA\\text_"+System.currentTimeMillis()+".txt"), content);
//		} catch (IOException e2) {
//			e2.printStackTrace();
//		}
		
		
		String codes[]=new String[]{"Affected","Opacity Score","Lung volume (ml)","Volume of opacity (ml)","Percentage of opacity","Volume of high opacity (ml)","Percentage of high opacity","Mean HU total","Mean HU of opacity","Standard deviation total","Standard deviation of opacity"};
		String segmentations[]=new String[]{"Both lungs","Left lung","Right lung","Left upper lobe","Left lower lobe","Right upper lobe","Right middle lobe","Right lower lobe"};
		
		LocalDateTime now=LocalDateTime.now();
		Document finding=DocumentHelper.createDocument();
		Element roote=finding.addElement("entries","urn:hl7-org:v3").addNamespace("voc", "urn:hl7-org:v3/voc").addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		Element entry=roote.addElement("entry");
		Element observation=createObservation(entry, "OBS", "DICOM", "Finding");
		
		createEntryRelationship(observation, "121071", "DICOM", "name", "Finding 1", "ED", "","","");
		createEntryRelationship(observation, "TimeStamp", "99SMS_SY", "TimeStamp", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), "ED", "","","");
		Element observation_1 = createEntryRelationship_Nest(observation, "OBS", "DICOM", "FindingState");
		createEntryRelationship(observation_1, "findingStateUid", "99SMS_SY", "findingStateUid", StrKit.getRandomUUID(), "ED", "","","");
		createEntryRelationship(observation_1, "G-C0E3", "SRT", "Finding Site", "Lung", "ED", "","T280D0","SRT");
		createEntryRelationship(observation_1, "FindingType", "99SMS_SY", "FindingType", "Lung", "ED", "","CTPA","PRIVATE_CODE");
		Element observation_2 = createEntryRelationship_Nest(observation_1, "OBS", "DICOM", "Measurement");
		
		char separator = ',';
		CsvReader reader = null;
		String patientname="",patientid="",seriesDate="",seriesTime="";
		List<Syngoviasrdata> list=new ArrayList<Syngoviasrdata>();
        try {
            //如果生产文件乱码，windows下用gbk，linux用UTF-8
            reader = new CsvReader("E:\\OneDrive - Siemens Healthineers\\武汉协和\\CTPA\\CT Pneumonia Analysis_2020-08-24_010000\\result.csv",separator, Charset.forName("UTF-8"));// new CsvReader(new StringReader(content), separator);//
            
            // 读取表头
            reader.readHeaders();
            String[] headArray = reader.getHeaders();//获取标题
            //System.out.println(headArray[0] + headArray[1] + headArray[2]);
            int index=-1;
            // 逐条读取记录，直至读完
            while (reader.readRecord()) {
                // 读一整行
                //System.out.println(reader.getRawRecord());
            	if(StrKit.equals("PatientsName:", reader.get(0))) {
            		log.info(reader.get(0)+reader.get(1));
            		patientname=reader.get(1);
            		Syngoviasrdata data=new Syngoviasrdata();
            		data.setCode("00100010");
            		data.setCodesystemname("DICOM");
            		data.setCodedisplayname("PatientsName");
            		data.setValue(patientname);
            		data.setUnit("");
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, "00100010", "DICOM", "PatientsName", patientname, "ED", "","","");
            	}
            	if(StrKit.equals("SeriesDate:", reader.get(0))) {
            		log.info(reader.get(0)+reader.get(1));
            		seriesDate=reader.get(1);
            		Syngoviasrdata data=new Syngoviasrdata();
            		data.setCode("00080021");
            		data.setCodesystemname("DICOM");
            		data.setCodedisplayname("SeriesDate");
            		data.setValue(seriesDate);
            		data.setUnit("");
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, "00080021", "DICOM", "SeriesDate", seriesDate, "ED", "","","");
            	}
            	if(StrKit.equals("SeriesTime:", reader.get(0))) {
            		log.info(reader.get(0)+reader.get(1));
            		seriesTime=reader.get(1);
            		Syngoviasrdata data=new Syngoviasrdata();
            		data.setCode("00080031");
            		data.setCodesystemname("DICOM");
            		data.setCodedisplayname("SeriesTime");
            		data.setValue(seriesTime);
            		data.setUnit("");
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, "00080031", "DICOM", "SeriesTime", seriesTime, "ED", "","","");
            	}
                // 读这行的第一列
            	if(StrKit.equals("Both lungs", reader.get(1))) {
            		log.info(reader.get(0)+reader.get(1));
                    index=0;
                    Syngoviasrdata data=new Syngoviasrdata();
            		data.setCode("FindingType");
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname("FindingType");
            		data.setValue("CTPA");
            		data.setUnit("");
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
                    continue;
            	}
            	if(index>=0&&index<11) {
            		log.info(reader.get(0));
            		String firstname=reader.get(0);
            		String unit="";
            		if(firstname.indexOf("(")>0){
            			unit=firstname.substring(firstname.indexOf("(")+1, firstname.indexOf(")"));
            		}
            		createEntryRelationship(observation_2, "G-C0E3", "SRT", "Finding Site", reader.get(0), "ED", "","","");
            		Syngoviasrdata data=new Syngoviasrdata();
            		data.setCode("G-C0E3");
            		data.setCodesystemname("SRT");
            		data.setCodedisplayname("Finding Site");
            		data.setValue(reader.get(0));
            		data.setUnit("");
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, segmentations[0], "99SMS_SY", segmentations[0], reader.get(1), "PQ", unit,"","");
            		data=new Syngoviasrdata();
            		data.setCode(segmentations[0]);
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname(segmentations[0]);
            		data.setValue(reader.get(1));
            		data.setUnit(unit);
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, segmentations[1], "99SMS_SY", segmentations[1], reader.get(2), "PQ", unit,"","");
            		data=new Syngoviasrdata();
            		data.setCode(segmentations[1]);
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname(segmentations[1]);
            		data.setValue(reader.get(2));
            		data.setUnit(unit);
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, segmentations[2], "99SMS_SY", segmentations[2], reader.get(3), "PQ", unit,"","");
            		data=new Syngoviasrdata();
            		data.setCode(segmentations[2]);
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname(segmentations[2]);
            		data.setValue(reader.get(3));
            		data.setUnit(unit);
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, segmentations[3], "99SMS_SY", segmentations[3], reader.get(4), "PQ", unit,"","");
            		data=new Syngoviasrdata();
            		data.setCode(segmentations[3]);
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname(segmentations[3]);
            		data.setValue(reader.get(4));
            		data.setUnit(unit);
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, segmentations[4], "99SMS_SY", segmentations[4], reader.get(5), "PQ", unit,"","");
            		data=new Syngoviasrdata();
            		data.setCode(segmentations[4]);
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname(segmentations[4]);
            		data.setValue(reader.get(5));
            		data.setUnit(unit);
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, segmentations[5], "99SMS_SY", segmentations[5], reader.get(6), "PQ", unit,"","");
            		data=new Syngoviasrdata();
            		data.setCode(segmentations[5]);
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname(segmentations[5]);
            		data.setValue(reader.get(6));
            		data.setUnit(unit);
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, segmentations[6], "99SMS_SY", segmentations[6], reader.get(7), "PQ", unit,"","");
            		data=new Syngoviasrdata();
            		data.setCode(segmentations[6]);
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname(segmentations[6]);
            		data.setValue(reader.get(7));
            		data.setUnit(unit);
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		createEntryRelationship(observation_2, segmentations[7], "99SMS_SY", segmentations[7], reader.get(8), "PQ", unit,"","");
            		data=new Syngoviasrdata();
            		data.setCode(segmentations[7]);
            		data.setCodesystemname("99SMS_SY");
            		data.setCodedisplayname(segmentations[7]);
            		data.setValue(reader.get(8));
            		data.setUnit(unit);
            		data.setViaVersion(PropKit.use("system.properties").get("via_version"));
            		list.add(data);
            		index++;
            	}
            	if(index>=11) {
            		break;
            	}
            	
//                System.out.println(reader.get(0));
//                System.out.println(reader.getCurrentRecord());
                // 读这行的第二列
                //System.out.println(reader.get(1));
            }
            
            String basePath=PropKit.use("system.properties").get("sysdir")
        			+System.getProperty("file.separator");
	        String filename=now.format(DateTimeFormatter.BASIC_ISO_DATE)
			+System.getProperty("file.separator")+"clipboard_ctpa"+System.getProperty("file.separator")+patientname+""+".xml";
	        
	        try {
				FileUtils.writeStringToFile(new File(basePath+filename), finding.asXML(),"UTF-8");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
//	        Syngoviareport viareport=new Syngoviareport();
//	        viareport.setReportname("PARENCHYMA ANALYSIS REPORT");
//	        viareport.setCdafile(filename);
////	        viareport.save();
//	        
//	        Syngoviafindingfile findingfile= new Syngoviafindingfile();
//	        findingfile.setFindingname("LUNG 3D GENERAL");
//	        findingfile.setFindingfile(filename);
//	        findingfile.setFindingindex(0);
//	        findingfile.setSource(FindingSource.SYNGOVIA_CLIPBOARD_LUNG3D);
////	        findingfile.save();
//	        
//	        List<Object> cachelist=new ArrayList<Object>();
//	        cachelist.add(viareport);
//	        cachelist.add(findingfile);
//	        cachelist.add(list);
//	 
//	        String uuid=StrKit.getRandomUUID();
//	        CacheKit.put(CacheName.SYNGOVIA_CLIPBOARD_DATA, uuid, cachelist);
////	        Db.batchSave(list, list.size());
//	        
//	        JSONObject da=new JSONObject();
//	    	da.put("patientid", patientid);
//	    	da.put("patientname", patientname);
//	    	da.put("uuid", uuid);
//	    	da.put("source", FindingSource.SYNGOVIA_CLIPBOARD_LUNG3D);
//	    	da.put("viareportid", viareport.getId());
//	    	da.put("viafindingfileid", findingfile.getId());
//	        
//	        WebSocketUtils.broadcastMessage(new WebsocketVO(WebsocketVO.RECEIVEVIADATA_CLIPBOARD, da.toString()).toJson());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                reader.close();
            }
        }
	}
	
	public boolean batchImportCTPA(UploadFile file){
		
		String csvpath=MyFileUtils.unZipFiles(file.getFile());
		
		List<File> csvfiles =new ArrayList<File>();
		try {
			handleDir(new File(csvpath),csvfiles);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for(File f:csvfiles) {
			char separator = ',';
			CsvReader reader = null;
			FileInputStream in=null;
			String patientname="",seriesDate="",seriesTime="",seriesDescription="";
			
			String datas[][]=new String[12][9];
			
			List<Syngoviasrdata> list=new ArrayList<Syngoviasrdata>();
	        try {
	        	in=new FileInputStream(f);
	            //如果生产文件乱码，windows下用gbk，linux用UTF-8
	            reader = new CsvReader(in,separator,Charset.forName("UTF-8"));// new CsvReader(f,separator, Charset.forName("UTF-8"));// new CsvReader(new StringReader(content), separator);//
	            
	            reader.readHeaders();
	            reader.getHeader(0);
	            log.info(reader.getHeader(0));
	            if(!StrKit.equals("Siemens Healthineers CT Pneumonia Analysis Prototype", reader.getHeader(0))) {
	            	log.info("Skip file:"+f);
	            	continue;
	            }
	            int index=-1;
	            // 逐条读取记录，直至读完
	            while (reader.readRecord()) {
	            	if(StrKit.equals("PatientsName:", reader.get(0))) {
	            		patientname=reader.get(1);
	            	}
	            	if(StrKit.equals("SeriesDate:", reader.get(0))) {
	            		seriesDate=reader.get(1);
	            	}
	            	if(StrKit.equals("SeriesTime:", reader.get(0))) {
	            		seriesTime=reader.get(1);
	            	}
	            	if(StrKit.equals("SeriesDescription:", reader.get(0))) {
	            		seriesDescription=reader.get(1);
	            	}
	                // 读这行的第一列
	            	if(StrKit.equals("Both lungs", reader.get(1))) {
	            		log.info(reader.get(0)+reader.get(1));
	            		
	            		for(int i=1;i<9;i++) {
	            			datas[0][i]=reader.get(i);
	            		}
	                    index=0;
	            	}
	            	
	            	for(int i=1;i<11&&index>=0;i++) {
	            		
	            	}
	            }
	            
	            
	            reader.close();
	            in.close();
	            
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		
		return true;
	}
	
	//递归完成目录文件读取 
    private void handleDir(File dir , List<File> csvfiles)throws IOException{ 
    	File[] files = dir.listFiles();
        for(File csvfile : files){
//            System.out.println(csvfile); 
            if(csvfile.isDirectory()){
                handleDir(csvfile , csvfiles);
            }
            else if(csvfile.getName().indexOf(".csv")>0){
            	csvfiles.add(csvfile);
            } 
        }
    }
}
