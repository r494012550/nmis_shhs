package com.healta.plugin.hl7;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.healta.model.Patient;
import com.healta.model.Studyorder;
import com.healta.util.DateUtil;
import com.jfinal.plugin.activerecord.Record;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v23.message.ADT_A08;
import ca.uhn.hl7v2.model.v23.message.ADT_A40;
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.model.v23.segment.DG1;
import ca.uhn.hl7v2.model.v23.segment.DSC;
import ca.uhn.hl7v2.model.v23.segment.MRG;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.model.v23.segment.NTE;
import ca.uhn.hl7v2.model.v23.segment.OBR;
import ca.uhn.hl7v2.model.v23.segment.ORC;
import ca.uhn.hl7v2.model.v23.segment.PID;
import ca.uhn.hl7v2.model.v23.segment.PV1;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

public class HL7SendServices {
	
	private static String defDtPtn = "yyyy-MM-dd HH:mm:ss";
	private static String defDtPtn1 = "yyyy-MM-dd HH:mm:ss.SSS";
	private static String defDtPtn2 = "yyyyMMddHHmmss";
	private static String defDtPtn3 = "yyyyMMddHHmmssSSS";
	private static String defDtPtn4 = "yyyyMMddHHmm";
	private static SimpleDateFormat DATEFORMAT = new SimpleDateFormat(defDtPtn);
	private static SimpleDateFormat DATEFORMAT1 = new SimpleDateFormat(defDtPtn1);
	private static SimpleDateFormat DATEFORMAT2 = new SimpleDateFormat(defDtPtn2);
	private static SimpleDateFormat DATEFORMAT3 = new SimpleDateFormat(defDtPtn3);
	private static SimpleDateFormat DATEFORMAT4 = new SimpleDateFormat(defDtPtn4);
	
	public static void sendTo(byte[] message, String receiver) throws Exception {
		HL7SendPlugin.sendTo(message, receiver);
	}
	
	public boolean patientRename(Patient patient) {
        boolean ret = false;
        ADT_A08 adt = new ADT_A08();     
        try {
            
          //MSH消息段(Segment)  
            MSH msh;
            msh = initMSH(adt.getMSH());
            msh.getMessageType().getMessageType().setValue("ADT");
    	    msh.getMessageType().getTriggerEvent().setValue("A08");
          //PID
            PID pid = adt.getPID();
            pid.getSetIDPatientID().setValue("1");//PID-1 	Set ID - PID
            pid.getPatientIDExternalID().getID().setValue(patient.getPatientid());//PID-2 	Patient ID
            pid.getPatientIDInternalID(0).getID().setValue(patient.getPatientid());//PID-3 	Patient Identifier List
            pid.getPatientName(0).getFamilyName().setValue(patient.getPy());//PID-5 	Patient Name
            //pid.getPatientName(0).getGivenName().setValue("");
            pid.getDateOfBirth().getTimeOfAnEvent().setValue(DateUtil.hl7BirthdateFormat(patient.getBirthdate()));//PID-7 	Date/Time of Birth
            pid.getSex().setValue(patient.getSex());//PID-8 	Administrative Sex
            //PID-11 	Patient Address
            
            Parser parser = new PipeParser();  
            String encodedMessage = parser.encode(adt);
            
            System.out.println(encodedMessage);

            sendTo(encodedMessage.getBytes(), "SYNGO^PACS");
            ret = true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
       
        return ret;
    }
	
	/**
	 * 
	 * @param patient (correct patient record)
	 * @param record (wrong patient record)
	 * @return
	 */
	public boolean patientMerge(Patient patient, Record record) {
		boolean ret = false;
        ADT_A40 adt = new ADT_A40();     
        try {
            
          //MSH消息段(Segment)  
            MSH msh;
            msh = initMSH(adt.getMSH());
            msh.getMessageType().getMessageType().setValue("ADT");
    	    msh.getMessageType().getTriggerEvent().setValue("A40");
          //PID 
            PID pid = adt.getPATIENT().getPID();
            pid.getSetIDPatientID().setValue("1");//PID-1 	Set ID - PID
            pid.getPatientIDExternalID().getID().setValue(patient.getPatientid());//PID-2 	Patient ID
            pid.getPatientIDInternalID(0).getID().setValue(patient.getPatientid());//PID-3 	Patient Identifier List
            pid.getPatientName(0).getFamilyName().setValue(patient.getPy());//PID-5 	Patient Name
            pid.getDateOfBirth().getTimeOfAnEvent().setValue(DateUtil.hl7BirthdateFormat(patient.getBirthdate()));//PID-7 	Date/Time of Birth
            pid.getSex().setValue(patient.getSex());//PID-8 	Administrative Sex
            //PID-11 	Patient Address
          
          //MRG 
            MRG mrg = adt.getPATIENT().getMRG();
            mrg.getMrg1_PriorPatientIDInternal(0).getCx1_ID().setValue(record.getStr("patientid"));
            String patientname = record.getStr("patientname");
            if(patientname.contains("^")) {
            	mrg.getMrg7_PriorPatientName().getFamilyName().setValue(patientname.substring(0, patientname.indexOf("^")));
            	mrg.getMrg7_PriorPatientName().getGivenName().setValue(patientname.substring(patientname.indexOf("^")+1));
            }else {
            	mrg.getMrg7_PriorPatientName().getFamilyName().setValue(patientname);
            }
            
            Parser parser = new PipeParser();  
            String encodedMessage = parser.encode(adt);
            
            System.out.println(encodedMessage);

            sendTo(encodedMessage.getBytes(), "SYNGO^PACS");
            ret = true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
       
        return ret;
	}
	
	public boolean procedureUpdate_ORU_R01(Record record) {
		boolean ret = false;
		ORU_R01 oru = new ORU_R01();
		try { 
          //MSH消息段(Segment)  
            MSH msh;
            msh = initMSH(oru.getMSH());
            msh.getMessageType().getMessageType().setValue("ORU");
    	    msh.getMessageType().getTriggerEvent().setValue("R01"); 
          //PID
            PID pid = oru.getRESPONSE().getPATIENT().getPID();
            pid.getSetIDPatientID().setValue("1");//PID-1 	Set ID - PID
            pid.getPatientIDExternalID().getID().setValue(record.getStr("patientid"));//PID-2 	Patient ID
            pid.getPatientIDInternalID(0).getID().setValue(record.getStr("patientid"));//PID-3 	Patient Identifier List
            String patientname = record.getStr("patientname");
            if(patientname.contains("^")) {
            	pid.getPatientName(0).getFamilyName().setValue(patientname.substring(0, patientname.indexOf("^")));//PID-5 	Patient Name
                pid.getPatientName(0).getGivenName().setValue(patientname.substring(patientname.indexOf("^")+1));
            }else {
            	pid.getPatientName(0).getFamilyName().setValue(patientname);
            }
            
            pid.getDateOfBirth().getTimeOfAnEvent().setValue(DateUtil.hl7BirthdateFormat(record.getStr("birthdate")));//PID-7 	Date/Time of Birth
            pid.getSex().setValue(record.getStr("sex"));//PID-8 	Administrative Sex
          //ORC - Common Order Segment
            ORC orc = oru.getRESPONSE().getORDER_OBSERVATION().getORC();
            orc.getOrderControl().setValue("NW");//ORC-1 	Order Control
            //ORC-2 	Placer Order Number
            //ORC-3 	Filler Order Number
            //ORC-7 	Quantity/Timing
            //ORC-12 	Ordering Provider
            orc.getOrderEffectiveDateTime().getTimeOfAnEvent().setValue(DATEFORMAT4.format(new Date()));;//ORC-15 	Order Effective Date/Time
            orc.getEnteringOrganization().getIdentifier().setValue("NOZ");//ORC-17 	Entering Organization
          //OBR – Observation Request
            OBR obr = oru.getRESPONSE().getORDER_OBSERVATION().getOBR();
            obr.getUniversalServiceIdentifier().getText().setValue(record.getStr("modality"));;//OBR-4 	Universal Service Identifier
            obr.getPlacerField1().setValue(record.getStr("accessionnumber"));//OBR-18 Placer Field 1(Used for:DICOM accession number)
            obr.getPlacerField2().setValue(record.getStr("studyid"));//OBR-19 Placer Field 2(Used for:DICOM study ID)
            obr.getFillerField1().setValue(record.getStr("accessionnumber"));//OBR-20 Filler Field 1 +
            obr.getDiagnosticServiceSectionID().setValue(record.getStr("modality"));//OBR-24 Diagnostic Service Section ID(Used for:DICOM modality-code)
            //obr.getReasonForStudy(0).getCe1_Identifier().setValue("Lorem");//OBR-31 Reason for Study(Used for:DICOM reason of requested procedure)
            //obr.getPrincipalResultInterpreter().getCm_ndl1_OPName().getCn1_IDNumber().setValue("Schmidt");//OBR-32 Principal Result Interpreter +(Used for:DICOM reporting physician)
         //ZDS
            DSC dsc = oru.getDSC();
            dsc.getContinuationPointer().setValue(record.getStr("studyinstanceuid"));
            
            
            Parser parser = new PipeParser();  
            String encodedMessage = parser.encode(oru);
            
            System.out.println(encodedMessage);

            sendTo(encodedMessage.getBytes(), "SYNGO^PACS");
            ret = true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
		return ret;
	}
	
	public static MSH initMSH(MSH msh) throws DataTypeException{  
	    msh.getFieldSeparator().setValue("|");//MSH-1 Field Separator            
	    msh.getEncodingCharacters().setValue("^~\\&");//MSH-2 Encoding Characters
	    msh.getSendingApplication().getNamespaceID().setValue("REPORT");//MSH-3 Sending Application
	    msh.getSendingFacility().getNamespaceID().setValue("RIS");//MSH-4 Sending Facility
	    msh.getReceivingApplication().getNamespaceID().setValue("Application");//MSH-5 Receiving Application
	    msh.getReceivingFacility().getNamespaceID().setValue("Facility");//MSH-6 Receiving Facility
	    msh.getDateTimeOfMessage().getTimeOfAnEvent().setValue(getCurrentTime_hl7());//MSH-7 Date/Time Of Message
	    //MSH-9 Message Type
	    //msh.getMessageType().getMessageType().setValue("ADT");
	    //msh.getMessageType().getTriggerEvent().setValue("A08");
	    msh.getMessageControlID().setValue("MWS"+getSequenceNumber());//MSH-10 Message Control ID 
	    msh.getProcessingID().getProcessingID().setValue("P");//MSH-11 Processing ID
	    msh.getVersionID().setValue("2.4");//MSH-12 Version ID
	    msh.getSequenceNumber().setValue(getSequenceNumber());//MSH-13 Sequence Number
	    
	    //msh.getAcceptAcknowledgementType().setValue("NE");//MSH-15 Accept Acknowledgment Type
	    //msh.getApplicationAcknowledgementType().setValue("AL");//MSH-16 Application Acknowledgment Type
	    return msh;
	}
	
	public static String getCurrentTime_hl7(){
        String ret=null;
        ret=DATEFORMAT2.format(new Date());
        return ret;
    }
	
	public static String getSequenceNumber(){
        String ret=null;
        ret=DATEFORMAT3.format(new Date());
        return ret;
    }
	
	public static void main(String[] args) {
		Patient patient = new Patient();
		patient.setPatientid("P00256992");
		patient.setPatientname("XuZhangQuan");
		patient.setPy("XuZhangQuan");
		patient.setBirthdate("1937-01-20");
		patient.setSex("M");
		Studyorder studyorder = new Studyorder();
		studyorder.setModalityType("CT");
		
		Record record = new Record();
		record.set("patientid", "0906044652");
		record.set("patientname", "TEST DR");
		record.set("birthdate","1977-11-27");
		record.set("sex", "M");
		record.set("modality","DX");
		record.set("accessionnumber","DX152544");
		record.set("studyinstanceuid","1.2.840.1424321.23532.20090701.081356.152544");
		new HL7SendPlugin().start();
		//new HL7SendServices().patientRename(patient);
		new HL7SendServices().patientMerge(patient, record);
		
		//new HL7SendServices().procedureUpdate_ORU_R01(record);
	}
}
