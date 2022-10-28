/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), available at http://sourceforge.net/projects/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * TIANI Medgraph AG.
 * Portions created by the Initial Developer are Copyright (C) 2003-2005
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * Gunter Zeilinger <gunter.zeilinger@tiani.com>
 * Franz Willer <franz.willer@gwi-ag.com>
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package com.healta.plugin.hl7;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
//import org.dcm4chex.archive.ejb.jdbc.QueryCmd;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONObject;

import com.healta.constant.PatientSource;
import com.healta.constant.StudyImageStatus;
import com.healta.constant.StudyOrderStatus;
import com.healta.model.Admission;
import com.healta.model.Mwlitem;
import com.healta.model.Patient;
import com.healta.model.Report;
import com.healta.model.Studyitem;
import com.healta.model.Studyorder;
import com.healta.model.Syngoviafindingfile;
import com.healta.model.Syngoviaimage;
import com.healta.model.Syngoviareport;
import com.healta.model.Unmatchstudy;
import com.healta.plugin.dcm.SPSStatus;
import com.healta.plugin.hl7.message.HL7Message;
import com.healta.util.DateUtil;
import com.healta.util.WebSocketUtils;
import com.healta.vo.WebsocketVO;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;


/**
 * @author 
 * @version
 * @since 
 *
 */
public class ORUService implements IHL7Service
{
	private final static Logger log = Logger.getLogger(ORUService.class);
    private static final int DEFAULT_STATUS_FIELD_NR = 10;//Default: Field 11 is OBSERV RESULT STATUS
    private static final String NO_RESULT_STATUS = "NO_OBSERV_RESULT_STATUS";
    private static final String NO_OBX = "NO_OBX";
    public static final String messageTypes="ORU^R01";
    
    
    private HashSet obxIgnoreStati = new HashSet();
    private int obxStatusFieldNr = DEFAULT_STATUS_FIELD_NR;
    private int fetchSize;
    
    
    public ORUService(){
    	//setMessageTypes(messageTypes);
    }
    
//    public final String getMessageTypes() {
//        return messageTypes;
//    }
    public void setObxIgnoreStati(String stati) {
        obxIgnoreStati.clear();
        if ( stati.equalsIgnoreCase("NONE")) return;
        int start = 0, end = stati.indexOf(':');
        if ( end != -1 ) {
            obxStatusFieldNr = Integer.parseInt(stati.substring(0,end));
            obxStatusFieldNr--;
            start = ++end;
        } else {
            obxStatusFieldNr = DEFAULT_STATUS_FIELD_NR;
        }
        for ( end = stati.indexOf(',', start) ; end != -1 ; start = ++end, end = stati.indexOf(',', start) ) {
            obxIgnoreStati.add(stati.substring(start, end));
        }
        obxIgnoreStati.add(stati.substring(start));
    }
    
    public String getObxIgnoreStati() {
        if (obxIgnoreStati.isEmpty()) return "NONE";
        StringBuffer sb = new StringBuffer();
        if (obxStatusFieldNr != 10) {
            sb.append((obxStatusFieldNr+1)).append(':');
        }
        Iterator iter = obxIgnoreStati.iterator();
        sb.append(iter.next());
        while ( iter.hasNext() ) {
            sb.append(',').append(iter.next());
        }
        return sb.toString();
    }
    
    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public boolean process(HL7Message message) throws Hl7Exception {
    	boolean ret=true;
    	String sending_application=message.get("MSH[1].3");
    	log.info("The HL7 message sending_application:"+sending_application);
    	if(StrKit.equals(sending_application, "syngo.via")){
    		ret=processSyngovia(message);
    	}
    	else if(StrKit.equals(sending_application, "syngo.plaza")){
    		ret=processSyngoplaza(message);
    	}
    	else if(StrKit.equals(sending_application, "anyis")){
    		ret=processAnyis(message);
    	}
        return ret;
    }
    
    public boolean processAnyis(HL7Message message) {
    	
    	return true;
    }
    
    public boolean processSyngoplaza(HL7Message message){
    	
    	String control_code=message.get("ZSC[1].2");
    	String notification_type=message.get("ZSC[1].11");
    	String imagenum=message.get("ZSC[1].10");
    	String studyinsuid=message.get("ZSC[1].1.1");
    	String accessNum=message.get("OBR[1].18");
    	String studydatetime=message.get("OBR[1].7");
    	
    	try {
			FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("tempdir")+"\\HL7\\PLAZA\\text_"+accessNum+"_"+System.currentTimeMillis()+".txt"), message.toHL7String());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    	log.info("control_code="+control_code);
    	log.info("notification_type="+notification_type);
    	log.info("studyinsuid="+studyinsuid);
    	log.info("accessNum="+accessNum);
    	log.info("imagenum="+imagenum);
    	log.info("studydatetime="+studydatetime);
    	//plaza 收到新的检查
    	if(StrKit.equals(control_code, "COMPLETED")&&StrKit.equals(notification_type, "FINAL")){
    		if(StrKit.notBlank(accessNum)) {
	    		Studyorder so=Studyorder.dao.getStudyorderByStudyid(accessNum);
	    		if(so!=null){
	    			Studyorder tmp=new Studyorder();
	    			tmp.setId(so.getId());
	    			tmp.setStudyinstanceuid(studyinsuid);
	    			try {
						tmp.setStudydatetime(DateUtil.getHL7Date(studydatetime));
					} catch (ParseException e) {
						log.error("studydatetime invalid format:"+studydatetime);
					}
	    			if(StringUtils.isNumeric(imagenum)){
	    				tmp.setNumberofstudyrelatedinstances(Integer.valueOf(imagenum));
	    			}
	    			if(StrKit.equals(so.getStatus(), StudyOrderStatus.registered)) {
	    				tmp.setStatus(StudyOrderStatus.completed);
	    			}
	    			tmp.setImagestatus(StudyImageStatus.MATCH);
	    			tmp.update();
	    			updateMWlitem(accessNum, SPSStatus.COMPLETED);
	    		}else {
	    			insertUnmatchstudy(message);
	    		}
    		}else {
    			insertUnmatchstudy(message);
    		}
    	}
    	else if(StrKit.equals(control_code, "DELETE")||StrKit.equals(control_code, "MOVED")){
    		if(StrKit.notBlank(studyinsuid)) {
    			Studyorder so=Studyorder.dao.getStudyorderByUid(studyinsuid);
        		if(so!=null){
        			Studyorder tmp=new Studyorder();
        			tmp.setId(so.getId());
        			//tmp.setStudyinstanceuid(studyinsuid);
        			tmp.setImagestatus(StudyImageStatus.DELETE);
        			tmp.update();
        		}
    		}
    	}
    	
    	return true;
    }
    
    public boolean processSyngovia(HL7Message message) throws Hl7Exception{
    	 try {
 	    	FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("tempdir")+"\\HL7\\VIA\\text_"+System.currentTimeMillis()+".txt"), message.toHL7String());
 	    	
 	    	String studyid=message.get("OBR[1].3.1");
 	    	String studyinsuid=message.get("OBR[1].4.1");
 	    	log.info("study_ins_uid="+studyinsuid);
 	    	
 	    	if(StrKit.isBlank(studyid)&&StrKit.isBlank(studyinsuid)){
 	    		throw new Hl7Exception("AE", "studyid and study StudyInstanceUid can't be empty.");
 	    	}

 	    	if(StrKit.isBlank(studyinsuid)){
 	    		studyinsuid=studyid;
 	    	} else if(StrKit.isBlank(studyid)){
 	    		studyid=studyinsuid;
 	    	}

     		Syngoviareport srt=Syngoviareport.dao.getByStudyInsUid(studyinsuid);//.findFirst("select * from syngoviareport where studyinsuid=? and delflag=0",study_ins_uid);
     		if(srt!=null){
     			srt.setDelflag(1);
     			srt.update();
     		}
     		
     		Syngoviareport via=new Syngoviareport();
     		via.setStudyid(studyid);
     		via.setStudyinsuid(studyinsuid);
     		String pdfstr=message.get("OBX[3].5");
     		Date current=new Date();
 	    	
 	    	if(pdfstr!=null&&!"".equals(pdfstr)){
 	    		String pdffilename=DateUtil.toShortStr(current)+System.getProperty("file.separator")+studyinsuid+System.getProperty("file.separator")+studyinsuid+".pdf";
 	    		FileUtils.writeByteArrayToFile(new File(PropKit.use("system.properties").get("sysdir")+System.getProperty("file.separator")+pdffilename), Base64.decodeBase64(pdfstr));
 	    		via.setPdffile(pdffilename);
 	    		log.info("create pdf file :"+pdffilename);
 	    	}
 	    	
 	    	String cdastr=message.get("OBX[4].5");
// 		    	log.info(cdastr);
 	    	String cdafilename=null;
 	    	String cdafilepath="";
 	    	if(StrKit.notBlank(cdastr)){
 	    		cdastr=cdastr.replaceAll("\\\\T\\\\gt;", "&gt;");
 	    		cdafilepath=DateUtil.toShortStr(current)+System.getProperty("file.separator")+studyinsuid;
 	    		cdafilename=cdafilepath+System.getProperty("file.separator")+studyinsuid+".xml";
 	    		FileUtils.writeStringToFile(new File(PropKit.use("system.properties").get("sysdir")+System.getProperty("file.separator")+cdafilename), cdastr,"UTF-8");
// 		    		FileUtils.writeStringToFile(new File(cdafilename+"_1"), cdastr, "GB18030");
// 		    		FileUtils.writeStringToFile(new File(cdafilename+"_2"), cdastr, "UTF-8");
 	    		via.setCdafile(cdafilename);
 	    		log.info("create cda file :"+cdafilename);
 	    	}
 	    	
 	    	if(pdfstr!=null||cdastr!=null){

 	    		List list=parseCDA(cdastr,studyid,studyinsuid,cdafilepath);
 	    		via.setReportname((String)list.get(0));
 	    		via.save();
 	    		List<Record> records=(ArrayList<Record>)list.get(1);
 	    		Db.delete("delete from syngoviasrdata where studyinsuid=?", studyinsuid);
 	    		Db.batchSave("syngoviasrdata", records, records.size());
 	    	}
 	    	
 	    	JSONObject da=new JSONObject();
 	    	da.put("studyinsuid", studyinsuid);
 	    	da.put("studyid", studyid);
 	    	
 	    	Studyorder order=Studyorder.dao.getStudyorderByUid(studyinsuid);
 	    	if(order!=null){
 	    		da.put("orderid", order.getId());
 	    	}
 	    	
 	    	log.info(da.toString());
 	    	WebSocketUtils.broadcastMessage(new WebsocketVO(WebsocketVO.RECEIVEVIAREPORT, da.toString()).toJson());

 	    } catch (Exception e) {
 	    	e.printStackTrace();
 	    	throw new Hl7Exception("AE", e.getMessage());
 	    }
    	 return true;
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
    
    
    
    public List parseCDA(String cdastr,String studyid,String studyinsuid,String path) throws DocumentException, IOException{
		SAXReader reader = new SAXReader();
//		reader.setEncoding("UTF-8");
//		log.info(cdastr);
		Document doc=reader.read(new StringReader(cdastr));
		Element root = doc.getRootElement();
		
		List components=root.element("component").element("structuredBody").elements("component");
		
		List ret=null;
		
		if("VB10".equals(PropKit.use("system.properties").get("via_version"))){
			ret=getSRData_VB10(components, studyid,studyinsuid,path);
		}
		else if("VB20".equals(PropKit.use("system.properties").get("via_version"))||"VB30".equals(PropKit.use("system.properties").get("via_version"))){
			ret=getSRData_VB30(components, studyid,studyinsuid,path);
		}
		
		
		if(ret!=null){
			List temlist=(List)ret.get(1);
			temlist.addAll(getPatientData(root.element("recordTarget").element("patientRole"),studyid,studyinsuid));
		}
		
		return ret;
	}
    
    public List getPatientData(Element patientrole,String studyid,String studyinsuid){
    	List<Record> ret=new ArrayList<Record>();
    	
    	String patientid=patientrole.element("id").attributeValue("extension");
    	String patientname=patientrole.element("patient").element("name").elementText("family");
    	String patientsex=patientrole.element("patient").element("administrativeGenderCode").attributeValue("code");
    	String patientbirthdate=patientrole.element("patient").element("birthTime").attributeValue("value");
    	
    	Record data=new Record();
		data.set("studyid", studyid);
		data.set("studyinsuid", studyinsuid);
		data.set("code", "00100010");
		data.set("codesystemname", "DICOM");
		data.set("codedisplayname", "PatientsName");
		data.set("value", patientname);
		data.set("unit", null);
		data.set("valuecode", null);
		data.set("valuecodesystemname", null);
		data.set("valuedisplayname", null);
		ret.add(data);
		data=new Record();
		data.set("studyid", studyid);
		data.set("studyinsuid", studyinsuid);
		data.set("code", "00100020");
		data.set("codesystemname", "DICOM");
		data.set("codedisplayname", "PatientID");
		data.set("value", patientid);
		data.set("unit", null);
		data.set("valuecode", null);
		data.set("valuecodesystemname", null);
		data.set("valuedisplayname", null);
		ret.add(data);
		data=new Record();
		data.set("studyid", studyid);
		data.set("studyinsuid", studyinsuid);
		data.set("code", "00100040");
		data.set("codesystemname", "DICOM");
		data.set("codedisplayname", "PatientsSex");
		data.set("value", patientsex);
		data.set("unit", null);
		data.set("valuecode", null);
		data.set("valuecodesystemname", null);
		data.set("valuedisplayname", null);
		ret.add(data);
		data=new Record();
		data.set("studyid", studyid);
		data.set("studyinsuid", studyinsuid);
		data.set("code", "00100030");
		data.set("codesystemname", "DICOM");
		data.set("codedisplayname", "PatientsBirthDate");
		data.set("value", patientbirthdate);
		data.set("unit", null);
		data.set("valuecode", null);
		data.set("valuecodesystemname", null);
		data.set("valuedisplayname", null);
		ret.add(data);
    	return ret;
    }
    
    
    public List getSRData_VB10(List components,String studyid,String studyinsuid,String path){
    	String reportname=null;
    	List<Record> ret=new ArrayList<Record>();
    	
    	List<Document> docs=new ArrayList<Document>();
    	List<String> findingnames=new ArrayList<String>();
    	Document finding=null;
    	String findingname=null;
    	Element textele=null;
    	
    	
    	for(int i=0;i<components.size();i++){
    		Element com=(Element)components.get(i);
    		Element sec=com.element("section");
			List entries=sec.elements("entry");
			
			
			if(i==1){
				reportname=sec.elementText("title");
				
				log.info("----------------------------------"+reportname);
				
				textele=sec.element("text");
				Db.delete("update syngoviaimage set delflag=1 where studyinsuid=?",studyinsuid);
			}
			
			for(int j=0;j<entries.size();j++){
				Element entry=(Element)entries.get(j);
				
				Element e_image=entry.element("observationMedia");
                if(e_image!=null){
//                	System.out.println(e_image.attributeValue("ID"));
                	
                	String imagefile=path+System.getProperty("file.separator")+"image"+System.getProperty("file.separator")+studyinsuid+"_"+e_image.attributeValue("ID")+".png";
					try {
						FileUtils.writeByteArrayToFile(new File(PropKit.use("system.properties").get("sysdir")+System.getProperty("file.separator")+imagefile), Base64.decodeBase64(e_image.element("value").getText()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Syngoviaimage image=new Syngoviaimage();
                	image.setStudyid(studyid);
                	image.setStudyinsuid(studyinsuid);
                	image.setImageid(e_image.attributeValue("ID"));
                	image.setImagefile(imagefile);
                	image.save();

                }
				
				
				
				Element obs=entry.element("observation");
				if(obs!=null){
					Record data=new Record();
					data.set("studyid", studyid);
					data.set("studyinsuid", studyinsuid);
					data.set("code", obs.element("code").attributeValue("code"));
					data.set("codesystemname", obs.element("code").attributeValue("codeSystemName"));
					data.set("codedisplayname", obs.element("code").attributeValue("displayName"));
					
					String value=obs.element("value").attributeValue("value");
					if(value==null||"".equals(value)){
						value=obs.element("value").getText();
					}
					
					String type=obs.element("value").attributeValue("type");
					if(StringUtils.isNotEmpty(type)&&"PQ".equals(type)){
						if(value.endsWith(".0000")){
							value=value.substring(0, value.length()-5);
							obs.element("value").addAttribute("value", value);
						}
						else if(value.endsWith("00")){
							value=value.substring(0, value.length()-2);
							obs.element("value").addAttribute("value", value);
						}
					}
					
					
					data.set("value", value);
					data.set("unit", obs.element("value").attributeValue("unit"));
					data.set("valuecode", null);
					data.set("valuecodesystemname", null);
					data.set("valuedisplayname", null);
					ret.add(data);
					
					
					
					if(i==1){
						String codestr=obs.element("code").attributeValue("code");
						if("FindingType".equals(codestr)||"CodedContent".equals(codestr)||"NonFindingData".equals(codestr)){
							
							if(finding!=null){
								//finding.getRootElement().add((Element)entry.clone());
		            			docs.add(finding);
		            			findingnames.add(findingname);
		            			
		            			//finding=null;
		            			
		            			log.info("add finding to list="+findingname);
							}
							//else{
								findingname=obs.element("value").getText();
		                		log.info("findingname="+findingname);
		                		finding=DocumentHelper.createDocument();
		                    	Element roote=finding.addElement("entries","urn:hl7-org:v3").addNamespace("voc", "urn:hl7-org:v3/voc").addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		                    	roote.add((Element)entry.clone());
	                    	
							//}
	                	}
	                	else{
		                	if(finding!=null){
		                		finding.getRootElement().add((Element)entry.clone());
		                	}
	                	}
					}
				}
				Element act=entry.element("act");
				
				if(act!=null){
					if(finding!=null){
            			finding.getRootElement().add((Element)entry.clone());
//            			docs.add(finding);
//            			findingnames.add(findingname);
//            			
//            			finding=null;
            		}
				}
				
			}
			
			if(i==1){
				if(finding!=null){
//	    			System.out.println(finding.asXML());
	    			docs.add(finding);
	    			findingnames.add(findingname);
	    		}
				Db.delete("delete from syngoviafindingfile where studyinsuid=?",studyinsuid);
				FileUtils.deleteQuietly(new File(PropKit.use("system.properties").get("sysdir")+System.getProperty("file.separator")+path+System.getProperty("file.separator")+"finding"));
				log.info(docs.size());
				for(int k=0;k<docs.size();k++){
					Document doc=docs.get(k);
					if(textele!=null){
						doc.getRootElement().add((Element)textele.clone());
					}
					
					doc.getRootElement().addElement("studyid").addAttribute("studyid", studyid);
					doc.getRootElement().addElement("studyinsuid").addAttribute("studyinsuid", studyinsuid);
					doc.getRootElement().addElement("findingindex").addAttribute("index", getFindingIndex(findingnames,findingnames.get(k),k)+"");
					
					String findingfilename=path+System.getProperty("file.separator")+"finding"+System.getProperty("file.separator")+findingnames.get(k)+".xml";
					File findingfile=new File(PropKit.use("system.properties").get("sysdir")+System.getProperty("file.separator")+findingfilename);
					
					if(findingfile.exists()){
						int count=Syngoviafindingfile.dao.getCountByFindingname(studyinsuid, findingnames.get(k));
						findingfilename=path+System.getProperty("file.separator")+"finding"+System.getProperty("file.separator")+findingnames.get(k)+"_"+(count++)+".xml";
						findingfile=new File(PropKit.use("system.properties").get("sysdir")+System.getProperty("file.separator")+findingfilename);
					}
					
					try {
						FileUtils.writeStringToFile(findingfile, doc.asXML(),"UTF-8");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Syngoviafindingfile ff=new Syngoviafindingfile();
					ff.setStudyid(studyid);
					ff.setStudyinsuid(studyinsuid);
					ff.setFindingname(findingnames.get(k));
					
					log.info("findingfilename="+findingfilename);
					ff.setFindingfile(findingfilename);
					ff.setFindingindex(k);
					ff.save();
				}
			}
    	}
    	
    	
    	List list=new ArrayList<>();
    	list.add(reportname);
    	list.add(ret);
    	return list;
    	
    }
    
    public List getSRData_VB30(List components,String studyid,String studyinsuid,String path){
    	String reportname=null;
    	List<Record> ret=new ArrayList<Record>();
    	
    	List<Document> docs=new ArrayList<Document>();
    	List<String> findingnames=new ArrayList<String>();
    	Document finding=null;
    	String findingname=null;
    	
    	Element textele=null;
    	
    	if(components.size()==2){
    		Element com0=(Element)components.get(0);
    		Element sec0=com0.element("section");
			List entries0=sec0.elements("entry");
			for(int i=0;i<entries0.size();i++){
				Element entry=(Element)entries0.get(i);
				Element obs=entry.element("observation");
				if(obs!=null){
					Record data=new Record();
					data.set("studyid", studyid);
					data.set("studyinsuid",studyinsuid);
					data.set("code", obs.element("code").attributeValue("code"));
					data.set("codesystemname", obs.element("code").attributeValue("codeSystemName"));
					data.set("codedisplayname", obs.element("code").attributeValue("displayName"));
					
					String value=obs.element("value").attributeValue("value");
					if(value==null||"".equals(value)){
						value=obs.element("value").getText();
					}
					
					String type=obs.element("value").attributeValue("type");
					if(StringUtils.isNotEmpty(type)&&"PQ".equals(type)){
						if(value.endsWith(".0000")){
							value=value.substring(0, value.length()-5);
							obs.element("value").addAttribute("value", value);
						}
						else if(value.endsWith("00")){
							value=value.substring(0, value.length()-2);
							obs.element("value").addAttribute("value", value);
						}
					}

					data.set("value", value);
					data.set("unit", obs.element("value").attributeValue("unit"));
					data.set("valuecode", null);
					data.set("valuecodesystemname", null);
					data.set("valuedisplayname", null);
					ret.add(data);
				
				}
			}
			
			
			Element com1=(Element)components.get(1);
    		Element sec1=com1.element("section");
    		
    		reportname=sec1.elementText("title");
    		textele=sec1.element("text");
    		
			Db.delete("update syngoviaimage set delflag=1 where studyinsuid=?",studyinsuid);
    		
    		
    		List entries1=sec1.elements("entry");
			for(int i=0;i<entries1.size();i++){
				Element entry=(Element)entries1.get(i);
				
				Element e_image=entry.element("observationMedia");
                if(e_image!=null){
//                	System.out.println(e_image.attributeValue("ID"));
                	
                	String imagefile=path+System.getProperty("file.separator")+"image"+System.getProperty("file.separator")+studyinsuid+"_"+e_image.attributeValue("ID")+".png";
					try {
						FileUtils.writeByteArrayToFile(new File(PropKit.use("system.properties").get("sysdir")+System.getProperty("file.separator")+imagefile), Base64.decodeBase64(e_image.element("value").getText()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Syngoviaimage image=new Syngoviaimage();
                	image.setStudyid(studyid);
                	image.setImageid(e_image.attributeValue("ID"));
                	image.setImagefile(imagefile);
                	image.setStudyinsuid(studyinsuid);
                	image.save();

                }
                
                Element ele_obs=entry.element("observation");
                if(ele_obs!=null){
                	List<String> findingtype=new ArrayList<String>();
                	toExtractSRData(ele_obs,studyid,studyinsuid,ret,findingtype);
                	
                	List<Element> ele_er_list=ele_obs.elements("entryRelationship");
                	if(ele_er_list!=null&&ele_er_list.size()>0){
//                		findingname=ele_er_list.get(0).element("observation").elementText("value");
                		finding=DocumentHelper.createDocument();
                    	Element roote=finding.addElement("entries","urn:hl7-org:v3").addNamespace("voc", "urn:hl7-org:v3/voc").addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
                    	roote.add((Element)entry.clone());
                    	docs.add(finding);
    	    			findingnames.add(findingtype.size()>0?findingtype.get(findingtype.size()-1):null);
                	}
                	
                	
                }
				
			}
			
			Db.delete("delete from syngoviafindingfile where studyinsuid=?",studyinsuid);
			FileUtils.deleteQuietly(new File(PropKit.use("system.properties").get("sysdir")+System.getProperty("file.separator")+path+System.getProperty("file.separator")+"finding"));
			log.info(docs.size());
			for(int k=0;k<docs.size();k++){
				Document doc=docs.get(k);
				if(textele!=null){
					doc.getRootElement().add((Element)textele.clone());
				}
				
				doc.getRootElement().addElement("studyid").addAttribute("studyid", studyid);
				doc.getRootElement().addElement("studyinsuid").addAttribute("studyinsuid", studyinsuid);
				String findingfilename=path+System.getProperty("file.separator")+"finding"+System.getProperty("file.separator")+findingnames.get(k)+".xml";
				File findingfile=new File(PropKit.use("system.properties").get("sysdir")+System.getProperty("file.separator")+findingfilename);
				
				if(findingfile.exists()){
					int count=Syngoviafindingfile.dao.getCountByFindingname(studyinsuid, findingnames.get(k));
					findingfilename=path+System.getProperty("file.separator")+"finding"+System.getProperty("file.separator")+findingnames.get(k)+"_"+(count++)+".xml";
					findingfile=new File(PropKit.use("system.properties").get("sysdir")+System.getProperty("file.separator")+findingfilename);
				}
				
				try {
					FileUtils.writeStringToFile(findingfile, doc.asXML(),"UTF-8");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Syngoviafindingfile ff=new Syngoviafindingfile();
				ff.setStudyid(studyid);
				ff.setStudyinsuid(studyinsuid);
				ff.setFindingname(findingnames.get(k));
				
				log.info("findingfilename="+findingfilename);
				ff.setFindingfile(findingfilename);
				ff.setFindingindex(k);
				ff.save();
			}
    	}

    	
    	List list=new ArrayList<>();
    	list.add(reportname);
    	list.add(ret);
    	return list;
    	
    }
    
    public int getFindingIndex(List<String> list ,String name,int k) {
    	int ret=0;
    	for(int i=0;i<k;i++) {
    		if(StrKit.equals(name, list.get(i))) {
    			ret++;
    		}
    	}
    	
    	return ret+1;
    }
    
    
    public void toExtractSRData(Element obs,String studyid,String studyinsuid,List<Record> list,List<String> findingtype){
    	
//    	log.info(obs.element("value")+"----"+obs.element("entryRelationship"));
    	
    	if(obs.element("value")!=null){
    		Record data=new Record();
			data.set("studyid", studyid);
			data.set("studyinsuid", studyinsuid);
			data.set("code", obs.element("code").attributeValue("code"));
			data.set("codesystemname", obs.element("code").attributeValue("codeSystemName"));
			data.set("codedisplayname", obs.element("code").attributeValue("displayName"));
			
			String value=obs.element("value").attributeValue("value");
			if(value==null||"".equals(value)){
				value=obs.element("value").getText();
			}
			
			String type=obs.element("value").attributeValue("type");
			
			if(StringUtils.isNotEmpty(type)){
				if("PQ".equals(type)){
					if(value.endsWith(".0000")){
						value=value.substring(0, value.length()-5);
						obs.element("value").addAttribute("value", value);
					}
					else if(value.endsWith("00")){
						value=value.substring(0, value.length()-2);
						obs.element("value").addAttribute("value", value);
					}
				}
				else if("CD".equals(type)){
					
					data.set("valuecode", obs.element("value").attributeValue("code"));
					data.set("valuecodesystemname", obs.element("value").attributeValue("codeSystemName"));
					data.set("valuedisplayname", obs.element("value").attributeValue("displayName"));
//					log.info("type=="+type+"--"+data.getStr("valuecode"));
				}
			}
			
			if("FindingType".equals(data.getStr("code"))){
				System.out.println("value======"+value);
				if(StrKit.notBlank(value)){
					findingtype.add(value);
				}
				else{
					findingtype.add(data.getStr("valuedisplayname"));
				}
				
			}
			
			data.set("value", value);
			data.set("unit", obs.element("value").attributeValue("unit"));
			list.add(data);
    	}
    	else if(obs.element("entryRelationship")!=null){
    		List<Element> ele_er_list= obs.elements("entryRelationship");
    		for(Element ele_er:ele_er_list){
    			if(ele_er.element("act")==null){
    				toExtractSRData(ele_er.element("observation"),studyid,studyinsuid,list,findingtype);
    			}
    		}
    	}
    	
    }
    
    
    
//    public void Transform(String xmlcontent, String xslcontent, String htmlFileName) throws TransformerException, IOException {  
//        
//            TransformerFactory tFac = TransformerFactory.newInstance();  
//            Source xslSource = new StreamSource(new StringReader(xslcontent));  
//            Transformer t = tFac.newTransformer(xslSource);  
////            File xmlFile = new File(xmlFileName);  
//            File htmlFile = new File(htmlFileName);  
////            Source source = new StreamSource(xmlFile);
//            
//            StringReader in=new StringReader(xmlcontent);
//            Source source1 = new StreamSource(in);
//            
//            Result result = new StreamResult(htmlFile);
//            t.transform(source1, result);
//            
//			String content = FileUtils.readFileToString(htmlFile);
//			System.out.println(content);
////			FileUtils.deleteQuietly(htmlFile);
//      
//    }
    

//    public void process(HL7Message message) throws HL7Exception {
//        try {
////            Dataset doc = xslt(msg, xslPath, xslSubdirs);
////            addIUIDs(doc);
////            storeSR(doc);
//            MSH msh = new MSH(msg);
//            
////            IIPORUService serv=new IIPORUService();
////            if("syngo.via".equals(msh.sendingApplication)){
////                serv.receiveStructureReport(hl7str,msg);
////            }
////            else{
////                serv.receiveReport(hl7str,msg);
////            }
//            
//        } catch (Exception e) {
//            throw new HL7Exception("AE", e.getMessage(), e);
//        }
//    }

//    private String getOBXStatus(Document msg) {
//        Element rootElement = msg.getRootElement();
////        log.info("rootElement:"+rootElement);
//        List obxs = rootElement.elements("OBX");
////        log.info("obxs:"+obxs);
//        if ( obxs.isEmpty()) return NO_OBX;
//        List obxFields = ((Element) obxs.get(0)).elements("field");
////        log.info("obxFields:"+obxFields);
//        if ( obxFields.size() <= obxStatusFieldNr) 
//            return NO_RESULT_STATUS;
////        log.info("obxFields.get(10)).getText():"+((Element) obxFields.get(obxStatusFieldNr)).getText());
//        return ((Element) obxFields.get(obxStatusFieldNr)).getText();
//    }
    
//    private void addIUIDs(Dataset sr) {
//        UIDGenerator uidgen = UIDGenerator.getInstance();
//        if (!sr.containsValue(Tags.StudyInstanceUID)) {
//            if (!addSUIDs(sr)) {
//                sr.putUI(Tags.StudyInstanceUID, uidgen.createUID());
//            }
//        }
//        if (!sr.containsValue(Tags.SeriesInstanceUID)) {
//            sr.putUI(Tags.SeriesInstanceUID, uidgen.createUID());
//        }
//        if (!sr.containsValue(Tags.SOPInstanceUID)) {
//            sr.putUI(Tags.SOPInstanceUID, uidgen.createUID());
//        }
//        String cuid = sr.getString(Tags.SOPClassUID);
//        DcmElement identicalDocumentsSeq = sr.get(Tags.IdenticalDocumentsSeq);
//        if (identicalDocumentsSeq != null) {
//            for (int i = 0, n = identicalDocumentsSeq.countItems(); i < n; i++)
//            {
//                Dataset studyItem = identicalDocumentsSeq.getItem(i);
//                Dataset seriesItem = studyItem.putSQ(Tags.RefSeriesSeq).addNewItem();
//                seriesItem.putUI(Tags.SeriesInstanceUID, uidgen.createUID());
//                Dataset sopItem = seriesItem.putSQ(Tags.RefSOPSeq).addNewItem();
//                sopItem.putUI(Tags.RefSOPInstanceUID, uidgen.createUID());
//                sopItem.putUI(Tags.RefSOPClassUID, cuid);
//            }
//        }
//    }

//    private boolean addSUIDs(Dataset sr) {
//        String accno = sr.getString(Tags.AccessionNumber);
//        if (accno == null) {
//            log.warn("Missing Accession Number in ORU - store report in new Study");
//            return false;
//        }
//        Dataset keys = DcmObjectFactory.getInstance().newDataset();
//        keys.putSH(Tags.AccessionNumber, accno);
//        keys.putUI(Tags.StudyInstanceUID);
//        QueryCmd query = null;
//        try {
//            query = QueryCmd.createStudyQuery(keys, null, false, false, true, false, null);
//            query.setFetchSize(fetchSize).execute();
//            if (!query.next()) {
//                log.warn("No Study with given Accession Number: " 
//                        + accno + " - store report in new Study");
//                return false;
//            }
//            copyStudyInstanceUID(query, sr);
//            if (query.next()) {
//                DcmElement sq = sr.putSQ(Tags.IdenticalDocumentsSeq);
//                do {
//                    copyStudyInstanceUID(query, sq.addNewItem());
//                } while (query.next());
//            }
//            return true;
//        } catch (Exception e) {
//            log.error("Query DB for Studies with Accession Number " + accno
//                    + " failed - store report in new Study", e);
//            sr.putSQ(Tags.IdenticalDocumentsSeq);
//            return false;
//        } finally {
//            if (query != null)
//                query.close();
//        }
//    }

//    private void copyStudyInstanceUID(QueryCmd query, Dataset sr) 
//        throws SQLException {
//        sr.putUI(Tags.StudyInstanceUID, 
//                query.getDataset().getString(Tags.StudyInstanceUID));
//    }
    
    
    public Patient insertPatient(HL7Message message) {
    	Patient patient = new Patient();
    	String patientid = message.get("PID[1].3.1");
    	String patientname = message.get("PID[1].5.1");
    	String py = message.get("PID[1].5.2");
    	String sex = message.get("PID[1].8.1");
    	String birthdate = message.get("PID[1].7.1");
//    	String idnumber = "";
    	
    	patient.setPatientname(patientname);
    	patient.setPy(py);
    	patient.setPatientid(patientid);
    	patient.setSex(sex);
    	patient.setBirthdate(birthdate);
    	
    	patient.remove("id").save();
    	
    	return patient;
    }
    
    public Admission insertAdmission(HL7Message message, Integer patientidfk) {
    	Admission admission = null;
    	String admissionid = message.get("PV1[1].19");
    	String patientsource = message.get("PV1[1].2");
    	Integer age = null;
    	String ageunit = "";
    	
    	admission.setPatientidfk(patientidfk);
    	admission.setAdmissionid(admissionid);
    	admission.setPatientsource(patientsource);
    	admission.setAge(age);
    	admission.setAgeunit(ageunit);
    	
    	admission.remove("id").save();
    	return admission;
    }
    
    public Studyorder insertStudyorderAndStudyitem(HL7Message message, Patient patient, Admission admission) {
    	Studyorder studyorder = null;
    	String studyid = message.get("ORC[1].2.1");
    	String modality_type = message.get("ORC[1].2.2");
    	String studyitems = "";
    	
    	Integer modalityid = null;//检查设备
    	Integer status = null;//检查状态
    	String priority = message.get("OBR[1].5");
    	Date appdatetime = null;//申请时间
    	Date studydatetime = null;//检查时间
    	Date regdatetime = null;//登记时间
    	
    	if(PatientSource.emergency.equals(admission.getPatientsource())) {
    		studyorder.setPri(1);
  		}else {
  			studyorder.setPri(3);
  		}
    	studyorder.remove("id").save();
    	
    	Studyitem si=new Studyitem();
		si.setOrderid(studyorder.getId());
		si.setStudyid(studyorder.getStudyid());
		si.setModality(studyorder.getModalityType());

    	return studyorder;
    }
    
    public boolean insertReport(HL7Message message, Studyorder studyorder) {
    	boolean ret = true;
    	Report report = new Report();
    	String checkdesc_txt = message.get("OBX[2].5");//诊断所见
    	String checkresult_txt = message.get("OBX[1].5");//诊断建议
    	String reportstatus = message.get("OBR[1].25");//
    	String reporttime = message.get("OBR[1].22.1");
    	String reportphysician = "";
    	String reportphysician_name = message.get("OBR[1].32[1].2"); 	
    	String audittime= message.get("OBR[1].22.2");
    	String auditphysician = "";
    	String auditphysician_name = "";
    	String pos_or_neg = message.get("OBX[2].8");
//    	String urgent = "";
//    	String urgentexplain = "";
    	
    	report.setStudyorderfk(studyorder.getId());
    	report.setStudyid(studyorder.getStudyid());
    	report.setTemplateId(0);
    	report.setIslocking(0);
    	report.setPrintcount(0);
    	
    	try {
    		report.setReporttime(DateUtil.getHL7Date(reporttime));
    		report.setAudittime(DateUtil.getHL7Date(audittime));
		} catch (ParseException e) {
			log.error("reporttime or audittime invalid format:"+reporttime+" $ "+audittime);
		}
    	return ret;
    }
    
    /**
     * 将未匹配的数据放入unmatchstudy表中
     * @param message
     * @return
     */
    public boolean insertUnmatchstudy(HL7Message message) {
    	boolean ret = false;
    	
    	String patientid = message.get("PID[1].2");
    	String patientname1 = message.get("PID[1].5.1");
    	String patientname2 = message.get("PID[1].5.2");
    	String birthdate = message.get("PID[1].7");
    	String sex = message.get("PID[1].8");
    	String accessionnumber=message.get("OBR[1].18");
    	String studyid=message.get("OBR[1].19");
    	String studydatetime=message.get("OBR[1].7");
    	String modality=message.get("OBR[1].24");
    	String studyinstanceuid=message.get("ZSC[1].1.1");
    	String studydescription=message.get("ZSC[1].3");
    	String imagenum=message.get("ZSC[1].9");
    	Unmatchstudy unmatchstudy = Unmatchstudy.dao.findFirst("SELECT TOP 1 * FROM unmatchstudy WHERE accessionnumber = ?", accessionnumber);
    	if(unmatchstudy == null) {
    		unmatchstudy = new Unmatchstudy();
    	}
    	
    	if(StringUtils.isNumeric(birthdate)&&birthdate.length()>4) {
    		String birth = DateUtil.hl7BirthdateFormatToString(birthdate);
    		System.out.println(birth);
    		unmatchstudy.setBirthdate(birth);
    	}else {
    		unmatchstudy.setBirthdate(birthdate);
    		log.error("birthdate invalid format:"+birthdate);
    	}
    	try {
    		unmatchstudy.setStudydatetime(DateUtil.getHL7Date(studydatetime));
    	}catch (ParseException e) {
    		log.error("studydatetime invalid format:"+studydatetime);
    	}
    	if(StringUtils.isNumeric(imagenum)){
    		unmatchstudy.setNumberofimages(Integer.valueOf(imagenum));
		}
    	unmatchstudy.setStatus(StudyOrderStatus.completed);
    	
    	unmatchstudy.setPatientid(patientid);
    	unmatchstudy.setPatientname(patientname1+"^"+patientname2);
    	
    	unmatchstudy.setSex(sex);
    	unmatchstudy.setAccessionnumber(accessionnumber);
    	unmatchstudy.setStudyid(studyid);
    	unmatchstudy.setStudyinstanceuid(studyinstanceuid);
    	unmatchstudy.setModality(modality);
    	unmatchstudy.setStudydescription(studydescription);
    	
    	if(unmatchstudy.getId() != null) {
    		ret = unmatchstudy.update();
    	}else {
    		unmatchstudy.setMatchflag(0);
    		ret = unmatchstudy.remove("id").save();
    	}
    	
    	return ret;
    }

    /**
     * 更新MWlitem状态
     * @param accession_no 检查号
     * @param sps_status 检查状态
     * @return
     */
    public boolean updateMWlitem(String accession_no, int sps_status) {
    	boolean ret = true;
    	Date now = new Date();
    	Mwlitem mwlitem = Mwlitem.dao.findByAccessionNo(accession_no);
    	if(mwlitem!=null) {
	    	mwlitem.setSpsStatus(sps_status);
	    	mwlitem.setUpdatedTime(now);
	    	ret = mwlitem.update();
	    	log.info("UpdateMWlitem Done: accession_no="+accession_no+"; SpsStatus="+sps_status);
    	}
    	else {
    		log.info("UpdateMWlitem faile, can not find MWLitem by accession_no="+accession_no+"; SpsStatus="+sps_status);
    	}
    	return ret;
    }
    
    /**
     * 更新studyorder检查时间
     * @param accession_no 检查号
     * @param studydatetime 检查日期
     * @return
     */
    public boolean updateStudyorder(String accessNum, String studydatetime) {
    	boolean ret = true;
    	Studyorder studyorder = Studyorder.dao.getStudyorderByStudyid(accessNum);
    	try {
    		studyorder.setStudydatetime(DateUtil.getHL7Date(studydatetime));
		} catch (ParseException e) {
			log.error("studydatetime invalid format:"+studydatetime);
		}
    	ret = studyorder.update();
    	log.info("UpdateStudyorder: Studydatetime="+studydatetime);
    	return ret;
    }
}
