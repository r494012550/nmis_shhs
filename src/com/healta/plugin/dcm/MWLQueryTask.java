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
 * Java(TM), hosted at https://github.com/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
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

package com.healta.plugin.dcm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.DateRange;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.media.DicomDirReader;
import org.dcm4che3.media.RecordFactory;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.Commands;
import org.dcm4che3.net.Status;
import org.dcm4che3.net.pdu.PresentationContext;
import org.dcm4che3.net.service.BasicQueryTask;
import org.dcm4che3.net.service.DicomServiceException;
//import org.dcm4che3.tool.common.SPSStatus;
//import org.dcm4che3.tool.data.Tags;
//import org.dcm4che3.tool.jdbc.SqlBuilder;
import org.dcm4che3.util.StringUtils;

import com.healta.util.DateUtil;
import com.healta.util.OrderedProperties;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.template.ext.directive.RandomDirective;
//import com.mysql.jdbc.log.Log;


class MWLQueryTask extends BasicQueryTask {
	private static final Logger log = Logger.getLogger(MWLQueryTask.class);
	private static final String[] FROM = { "Patient", "MWLItem" };
    private static final String[] SELECT = { "*" };
    private static final String[] RELATIONS = { "Patient.pk",
            "MWLItem.patient_fk" };
    protected final String[] studyIUIDs;
    protected Attributes wmlRec;
//    protected ResultSet rs = null;
    protected List<Record> mwlitems=null;
    protected int index=0;
    private int sps_id=0;
//    Connection conn=null;
    private String callingAET;
    private HashMap<String,String> queryFields;
    private String regex=".+\\^\\*";

    public MWLQueryTask(Association as, PresentationContext pc, Attributes rq, Attributes keys, DcmQRSCP qrscp)
            throws DicomServiceException {
    	super(as, pc, rq, keys);
    	
    	callingAET=as.getAAssociateAC().getCallingAET();
    	
    	log.info("callingAET:"+callingAET);
    	
    	//选择properties文件,根据callingAET读取choice.properties中的value
    	Properties prop = chooseProperties(); 
    	//读取查询条件的key
    	queryFields = getQueryKey(prop);
    	
    	SqlBuilder sqlBuilder = new SqlBuilder();
    	
    	sqlBuilder.setSelect(SELECT);
        sqlBuilder.setFrom(FROM);
        sqlBuilder.setRelations(RELATIONS);
        setSqlBuilder(sqlBuilder, keys);
            studyIUIDs = StringUtils.maskNull(keys.getStrings(Tag.StudyInstanceUID));
    	
            mwlitems=Db.find(sqlBuilder.getSql());
    }

    @Override
    public boolean hasMoreMatches() throws DicomServiceException {
        boolean ret=false;
    	
    		ret=index<mwlitems.size();//rs.next();
			
			System.out.println("hasMoreMatches=="+ret);

        return ret;
    }

    @Override
    public Attributes nextMatch() throws DicomServiceException {
        
        wrappedFindNextStudy();
        return wmlRec;
    }

    private void wrappedFindNextStudy() throws DicomServiceException {
        try {
        	
            findNextStudy();
        } 
        catch (IOException e) {
            throw new DicomServiceException(Status.UnableToProcess, e);
        }
    }

    protected boolean findNextStudy() throws IOException {
    	
    	Record item=mwlitems.get(index++);
//        if (wmlRec == null)
        	wmlRec = new Attributes(keys.size());
//        wmlRec.addAll(keys);
        	
        
        /*	wmlRec.setString(Tags.PatientName, VR.PN , item.getStr("patientname"));
        	wmlRec.setString(Tags.PatientID, VR.LO, item.getStr("patientid"));
        	if(item.getStr("birthdate")!=null)
        	wmlRec.setDate(Tags.PatientBirthDate,VR.DA, DateUtil.stdJustDate(item.getStr("birthdate")));
        	wmlRec.setString(Tags.PatientSex, VR.CS, item.getStr("sex"));
        	wmlRec.setString(Tags.AdmissionID, VR.LO, item.getStr("admission_id"));
        	
        	wmlRec.setString(Tags.StudyInstanceUID, VR.UI, item.getStr("study_iuid"));
        	wmlRec.setString(Tags.RequestingPhysician, VR.PN, item.getStr("req_physician"));
        	wmlRec.setString(Tags.RequestedProcedureDescription, VR.LO, item.getStr("req_proc_desc"));
        	wmlRec.setString(Tags.AccessionNumber, VR.LO, item.getStr("accession_no"));
        	wmlRec.setString(Tags.ReferringPhysicianName, VR.PN, item.getStr("refe_physician"));
        	wmlRec.setString(Tags.RequestedProcedureID, VR.SH, item.getStr("req_proc_id"));
        	wmlRec.setString(Tags.IssuerOfPatientID, VR.LO, item.getStr("issuer_patientid"));*/
//        	wmlRec.setString(Tags.PatientID, VR.SH, rs.getString("patientid"));
        	
        	Attributes spssRec = new Attributes();
        	/*spssRec.setString(Tags.Modality, VR.CS, item.getStr("modality"));
        	spssRec.setString(Tags.ScheduledStationAET, VR.AE, item.getStr("station_aet"));
        	spssRec.setString(Tags.ScheduledStationName, VR.SH, item.getStr("station_name"));
        	spssRec.setDate(Tags.ScheduledProcedureStepStartDate, VR.DA, item.getDate("updated_time"));
        	spssRec.setDate(Tags.ScheduledProcedureStepStartTime, VR.TM ,item.getTimestamp("updated_time"));
        	spssRec.setString(Tags.ScheduledProcedureStepDescription, VR.LO, item.getStr("sps_desc"));
        	spssRec.setString(Tags.ScheduledProcedureStepID, VR.SH, item.getStr("sps_id"));*/
        	
        	setAttr(wmlRec,spssRec,item,++sps_id);
        	Sequence spss=wmlRec.ensureSequence(Tags.ScheduledProcedureStepSequence, 30);
        	spss.add(spssRec);
        	//wmlRec.addAll(spss);
        	
        
        	
//        else if (studyIUIDs.length == 1)
//            studyRec = null;
//        else
//            studyRec = ddr.findNextStudyRecord(studyRec, keys, recFact, ignoreCaseOfPN, matchNoValue);
//
//        while (studyRec == null && super.findNextPatient())
//            studyRec = ddr.findStudyRecord(patRec, keys, recFact, ignoreCaseOfPN, matchNoValue);

        return wmlRec != null;
    }
    
//    @Override
//    public void run() {
//        try {
//            int msgId = rq.getInt(Tag.MessageID, -1);
//            as.addCancelRQHandler(msgId, this);
//            try {
//                while (!canceled && hasMoreMatches()) {
//                    Attributes match = adjust(nextMatch());
//                    if (match != null) {
//                        int status = optionalKeysNotSupported
//                                ? Status.PendingWarning
//                                : Status.Pending;
//                        as.writeDimseRSP(pc, Commands.mkCFindRSP(rq, status), match);
//                    }
//                }
//                int status = canceled ? Status.Cancel : Status.Success;
//                as.writeDimseRSP(pc, Commands.mkCFindRSP(rq, status));
//            } catch (DicomServiceException e) {
//                Attributes rsp = e.mkRSP(0x8020, msgId);
//                as.writeDimseRSP(pc, rsp, e.getDataset());
//            } finally {
//                as.removeCancelRQHandler(msgId);
//                close();
//            }
//        } catch (IOException e) {
//            // handled by Association
//        }
//    }
    public void setAttr(Attributes attr, Attributes attr1, Record item, int sps_id) {
    	Properties prop = chooseProperties(); 
    	List<String> scuKeys = getSCUKey(prop);
    	 try {
			 for (String key : scuKeys) { 
				 
				 if(key.contains("ITEM")) {
					String tag=key.substring(4);
					String[] str=tag.split("_");
					String name=VRMapping.getInstance().getProperty(str[1]);
					if("DA".equals(name)) {
						if(item.getStr(prop.getProperty(key))!=null) {
							attr1.setDate((int)Tags.class.getField(str[1]).get(null), VR.valueOf(name) ,DateUtil.stdJustDate(item.getStr(prop.getProperty(key))));
						}
						
					 }else if("TM".equals(name)){
						if(item.getTimestamp(prop.getProperty(key))!=null) {
							attr1.setDate((int)Tags.class.getField(str[1]).get(null), VR.valueOf(name) , item.getTimestamp(prop.getProperty(key)));
						}
					 }else {
						attr1.setString((int)Tags.class.getField(str[1]).get(null), VR.valueOf(name) , item.getStr(prop.getProperty(key)));
					 } 
				 }else {
					 String tag=key.substring(4);
					 String name=VRMapping.getInstance().getProperty(tag);
					 /*if(StrKit.equals(tag, "SpecificCharacterSet")) {
						 attr.setString(Tags.SpecificCharacterSet, VR.valueOf("LO") , "GB18030");
					 }*/
					 if("DA".equals(name)) {
						 if(item.getStr(prop.getProperty(key))!=null)
						 attr.setDate((int)Tags.class.getField(tag).get(null), VR.valueOf(name) ,DateUtil.stdJustDate(item.getStr(prop.getProperty(key))));
					 }else if("TM".equals(name)){
						 attr.setDate((int)Tags.class.getField(tag).get(null), VR.valueOf(name) , item.getTimestamp(prop.getProperty(key)));
					 }else {
						 attr.setString((int)Tags.class.getField(tag).get(null), VR.valueOf(name) , item.getStr(prop.getProperty(key)));
					 } 
				 }
//				 attr.setString(Tags.SpecificCharacterSet, VR.valueOf("LO") , "GB18030");
//				 log.info(RandomUtils.nextInt());
//				 attr.setString((int)Tags.class.getField("StudyInstanceUID").get(null), VR.valueOf("UI") , "1.3.12.2.1107.5.2.40.64040.3000001907100017240112"+RandomUtils.nextInt());
				 attr1.setString((int)Tags.class.getField("ScheduledProcedureStepID").get(null), VR.valueOf("SH") , String.valueOf(sps_id));
//				 attr.setString(Tags.StudyDescription, VR.valueOf("LO") , "test");
//				 attr.setString(Tags.RequestedProcedureID, VR.valueOf("SH") , String.valueOf(sps_id));
//				 attr.setString(Tags.RequestedProcedureDescription, VR.valueOf("LO") , "照片");
//				 attr1.setString(Tags.ScheduledProcedureStepDescription, VR.valueOf("LO") , "中文");
			 } 
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    
    //选择properties文件,根据callingAET读取choice.properties中的value
    private  Properties chooseProperties() {
//    	String urlstr="attributes.properties";
    	Properties prop=readOrderedPropertiesFile("choice.properties");
    	String urlstr=prop.getProperty(callingAET);
    	return readOrderedPropertiesFile(urlstr+".properties");
    }
    
    //读取properties文件
    private  Properties readOrderedPropertiesFile(String urlstr) { 
	    Properties properties = new OrderedProperties(); 
	    InputStreamReader inputStreamReader = null; 
	    try { 
	      InputStream inputStream = new BufferedInputStream(new FileInputStream(PathKit.getWebRootPath()+"\\WEB-INF\\classes\\conf\\"+urlstr)); 
	      //prop.load(in);//直接这么写，如果properties文件中有汉子，则汉字会乱码。因为未设置编码格式。 
	      inputStreamReader = new InputStreamReader(inputStream, "utf-8"); 
	      properties.load(inputStreamReader); 
	    } catch (Exception e) { 
	    	e.printStackTrace();
	      System.out.println(e.getMessage()); 
	    } finally { 
	      if (inputStreamReader != null) { 
	        try { 
	          inputStreamReader.close(); 
	        } catch (IOException e) { 
	          System.out.println(e.getMessage()); 
	        } 
	      } 
	    } 
	    return properties; 
	  } 
    
    //读取查询条件的key
    private HashMap<String,String> getQueryKey(Properties prop){
    	HashMap<String,String> queryMap = new HashMap<String,String>();
    	for (String key : prop.stringPropertyNames()) { 
    		if(key.startsWith("query.")) {
    			queryMap.put(key.substring(6), prop.getProperty(key));
    		}
    	}
    	return queryMap;
    }
    
    //读取设备字段的key
    private List<String> getSCUKey(Properties prop){
    	List<String> scuList = new ArrayList<String>();
    	for (String key : prop.stringPropertyNames()) { 
    		if(key.startsWith("scu.")) {
    			scuList.add(key);
    		}
    	}
    	return scuList;
    }
    
    private void setSqlBuilder(SqlBuilder sqlBuilder, Attributes keys) {
    	Attributes spsItem = keys.getNestedDataset(Tags.SPSSeq);//.getItem(Tags.SPSSeq);
        if (spsItem != null) {
            
            sqlBuilder.addWildCardMatch(null, "MWLItem.spsId",
                    SqlBuilder.TYPE1,
                    spsItem.getStrings(Tags.SPSID));
            
            DateRange dr=spsItem.getDateRange(Tags.SPSStartDate);
            if(dr!=null)
            sqlBuilder.addRangeMatch(null, "MWLItem.spsStartDateTime",
                    SqlBuilder.TYPE1,new Date[]{dr.getStartDate(),dr.getEndDate()});
                    //spsItem.getDateTimeRange(Tags.SPSStartDate,Tags.SPSStartTime));
            
            //SPSStatus条件
            String spsStatus=queryFields.get("SPSStatus");
            if(StrKit.notBlank(spsStatus)) {
            	if(!"*".equals(spsStatus)) {
            		sqlBuilder.addListOfIntMatch(null, "MWLItem.spsStatusAsInt",
                            SqlBuilder.TYPE1, 
                            SPSStatus.toInts(spsStatus.split(",")));
            	}
            }else {
            	sqlBuilder.addListOfIntMatch(null, "MWLItem.spsStatusAsInt",
                        SqlBuilder.TYPE1, 
                        SPSStatus.toInts(spsItem.getStrings(Tags.SPSStatus)));
            }
            
            //date条件-updated_time
            String updated_time=queryFields.get("updated_time");
            if(StrKit.notBlank(updated_time)) {
            	Calendar calendar = new GregorianCalendar();
            	calendar.setTime(new Date());
            	calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            	calendar.add(calendar.DATE,1);
            	if("T".equals(updated_time)) {
            		Date tomorrow = calendar.getTime();
            		calendar.add(calendar.DATE,-1);
            		Date currentDate = calendar.getTime();
            		sqlBuilder.addRangeMatch(null, "MWLItem.updatedTime",
                            SqlBuilder.TYPE1,new Date[]{currentDate,tomorrow});

            	}else if("T,Y".equals(updated_time)||"Y,T".equals(updated_time)) {
            		Date tomorrow = calendar.getTime();
            		calendar.add(calendar.DATE,-2);
            		Date yesterday = calendar.getTime();
            		sqlBuilder.addRangeMatch(null, "MWLItem.updatedTime",
                            SqlBuilder.TYPE1,new Date[]{yesterday,tomorrow});
            	}
            }
            
            
            //aet条件
            String station_aet=queryFields.get("AET");
            if(StrKit.notBlank(station_aet)) {
            	if(!"*".equals(station_aet)) {
            		sqlBuilder.addWildCardMatch(null, "MWLItem.scheduledStationAET",
                            SqlBuilder.TYPE1,
                            station_aet.split(","));
            	}
            }else {
            	sqlBuilder.addWildCardMatch(null, "MWLItem.scheduledStationAET",
                        SqlBuilder.TYPE1,
                        callingAET);
            }
            
            //modality条件
            String modality=queryFields.get("modality");
            if(StrKit.notBlank(modality)) {
            	if(!"*".equals(modality)) {
            		sqlBuilder.addWildCardMatch(null, "MWLItem.modality",
                            SqlBuilder.TYPE1,
                            modality.split(","));
            	}
            }else {
                sqlBuilder.addWildCardMatch(null, "MWLItem.modality",
                        SqlBuilder.TYPE1,
                        spsItem.getStrings(Tags.Modality));
            }

            
           /* sqlBuilder.addWildCardMatch(null, "MWLItem.scheduledStationAET",
                    SqlBuilder.TYPE1,
                    spsItem.getStrings(Tags.ScheduledStationAET));
            sqlBuilder.addWildCardMatch(null, "MWLItem.scheduledStationName",
            		SqlBuilder.TYPE1,
                    spsItem.getStrings(Tags.ScheduledStationName));*/
            
//            if (fuzzyMatchingOfPN)
//                try {
//                    sqlBuilder.addPNFuzzyMatch(
//                            new String[] {
//                                "MWLItem.performingPhysicianFamilyNameSoundex",
//                                "MWLItem.performingPhysicianGivenNameSoundex" },
//                            SqlBuilder.TYPE1,
//                            keys.getString(Tags.ScheduledPerformingPhysicianName));
//                } catch (IllegalArgumentException ex) {
//                    throw new DcmServiceException(
//                            Status.IdentifierDoesNotMatchSOPClass,
//                            ex.getMessage() + ": " + keys.get(Tags.ScheduledPerformingPhysicianName));
//                }
//            else
                sqlBuilder.addPNMatch(
                        new String[] {
                            "MWLItem.performingPhysicianName",
                            "MWLItem.performingPhysicianIdeographicName",
                            "MWLItem.performingPhysicianPhoneticName"},
                        true, // TODO make ICASE configurable
                        SqlBuilder.TYPE1,
                        spsItem.getString(Tags.ScheduledPerformingPhysicianName));
        }
        sqlBuilder.addWildCardMatch(null, "MWLItem.requestedProcedureId",
                SqlBuilder.TYPE1,
                keys.getStrings(Tags.RequestedProcedureID));
        sqlBuilder.addWildCardMatch(null, "MWLItem.accessionNumber",
        		SqlBuilder.TYPE1,
                keys.getStrings(Tags.AccessionNumber));
        sqlBuilder.addListOfStringMatch(null, "MWLItem.studyIuid",
                SqlBuilder.TYPE1,
                keys.getStrings(Tags.StudyInstanceUID));
        if (sqlBuilder.addWildCardMatch(null, "Patient.patientId",
                SqlBuilder.TYPE1,keys.getStrings(Tags.PatientID)) != null)
            sqlBuilder.addSingleValueMatch(null, "Patient.issuerOfPatientId",
            		SqlBuilder.TYPE1,keys.getString(Tags.IssuerOfPatientID));
//        if (fuzzyMatchingOfPN)
//            try {
//                sqlBuilder.addPNFuzzyMatch(
//                        new String[] {
//                            "Patient.patientFamilyNameSoundex",
//                            "Patient.patientGivenNameSoundex" },
//                        SqlBuilder.TYPE1,
//                        keys.getString(Tags.PatientName));
//            } catch (IllegalArgumentException ex) {
//                throw new DcmServiceException(
//                        Status.IdentifierDoesNotMatchSOPClass,
//                        ex.getMessage() + ": " + keys.get(Tags.PatientName));
//            }
//        else
       
        //替换LastName后缀 ^* 为 *
        String patientName = keys.getString(Tags.PatientName);
        log.info("PatientName:"+ patientName);
        if(StrKit.notBlank(keys.getString(Tags.PatientName))&&patientName.matches(regex)) {
        	patientName = patientName.substring(0, patientName.length()-2)+"*";
        }
        sqlBuilder.addPNMatch(
                new String[] {
                    "Patient.patientName",
                    "Patient.patientIdeographicName",
                    "Patient.patientPhoneticName"},
                SqlBuilder.TYPE1,
                false,
                patientName);
            
    }
}