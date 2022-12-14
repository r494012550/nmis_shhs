package com.healta.plugin.activemq;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.healta.constant.PatientSource;
import com.healta.constant.StudyprocessStatus;
import com.healta.controller.PrintController;
import com.healta.model.Admission;
import com.healta.model.Eorder;
import com.healta.model.Patient;
import com.healta.model.Quicksearch;
import com.healta.model.Report;
import com.healta.model.Studyorder;
import com.healta.model.Studyprocess;
import com.healta.plugin.activemq.Destination;
import com.healta.plugin.activemq.JmsReceiver;
import com.healta.service.SearchParaService;
import com.healta.ws.WsOrder;
import com.healta.ws.WsReceiver;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

public class StudyprocessReceiver extends JmsReceiver {
	private final static Logger log = Logger.getLogger(StudyprocessReceiver.class);
  
    public StudyprocessReceiver(String name,
                       Connection connection,
                       Destination type,
                       String subject) throws JMSException {
    	super(name, connection, type, subject);
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage msg = (ObjectMessage) message;
                try {
                	StudyprocessOrder order = (StudyprocessOrder)msg.getObject();

                    log.info("Start processing " + order);
                    Studyprocess sp=order.getSp();
                    Studyorder so=null;
                    if(sp.getStudyorderfk()!=null){
                    	so=Studyorder.dao.findById(sp.getStudyorderfk());
                    	if(so!=null) {
                    		if(sp.getPatientfk()==null) {
                    			sp.setPatientfk(so.getPatientidfk());
    	                    	sp.setAdmissionfk(so.getAdmissionidfk());
                    		}
                    		
                    		Patient patient1 = Patient.dao.findById(so.getPatientidfk());
                    		Admission admission1 = Admission.dao.findById(so.getAdmissionidfk());
                    		String messagestr = (patient1==null?"":patient1.toStr()) + "^^"
                    				+ (admission1==null?"":admission1.toStr()) + "^^" + so.toStr();
                    		sp.setLogmessages(messagestr);
                    	}
                    }
                    sp.remove("id").save();
	                
                    Admission admission = Admission.dao.findById(so.getAdmissionidfk());
	                try {  
		                //QuickSearch---------------------beign------------------
	                    
	                    //????????????????????????????????????
	                	if(StrKit.equals(StudyprocessStatus.scheduled, sp.getStatus())||
	                			StrKit.equals(StudyprocessStatus.registered, sp.getStatus())) {
	                    	SearchParaService.getLatestContent(null,sp.getStudyorderfk()).stream().forEach(x->x.save());
	                    }
	                    //??????????????????????????????????????????????????????????????????????????????????????????????????????
	                    else if(StrKit.equals(StudyprocessStatus.modifyschedule, sp.getStatus())||
	                    		StrKit.equals(StudyprocessStatus.modifyregistered, sp.getStatus())||
	                    		StrKit.equals(StudyprocessStatus.reassignStudy, sp.getStatus())) {
	                    	SearchParaService.getLatestContent(null,sp.getStudyorderfk()).stream().forEach(x->{
	                    		Quicksearch tmp=Quicksearch.dao.findByStudyorderkey(x.getStudyorderkey());
	                    		if(tmp!=null) {
	                    			x.setId(tmp.getId());
	                    			x.update();
	                    		}
	                    	});
	                    }
	                    //????????????????????????????????????????????????
	                    else if(StrKit.equals(StudyprocessStatus.mergepatient, sp.getStatus())||
	                    		StrKit.equals(StudyprocessStatus.cancelmergepatient, sp.getStatus())) {
	                    	SearchParaService.getLatestContent(sp.getPatientfk(),null).stream().forEach(x->{
	                    		Quicksearch tmp=Quicksearch.dao.findByStudyorderkey(x.getStudyorderkey());
	                    		if(tmp!=null) {
	                    			x.setId(tmp.getId());
	                    			x.update();
	                    		}
	                    	});
	                    }
	                    //????????????
	                    else if(StrKit.equals(StudyprocessStatus.deletePatientinfo, sp.getStatus())) {
	                    	SearchParaService.delByPatientkey(sp.getPatientfk());
	                    }
	                    //????????????
	                    else if(StrKit.equals(StudyprocessStatus.deletestudyinfo, sp.getStatus())) {
	                    	SearchParaService.delByStudyorderkey(sp.getStudyorderfk());
	                    }
	                }
	                catch(Exception x) {
	                	x.printStackTrace();
	                }
	              //????????????
                    if(StrKit.equals(StudyprocessStatus.finalresults, sp.getStatus())){
                    	//??????pdf????????????
                    	if(PropKit.use("system.properties").getBoolean("enable_generate_pdf_report", true)){
                    		makeReportAsPdf(Report.dao.findById(sp.getReportfk()),order.getPort(),order.getServerurl());
                    	}
                    	
                    	if (so != null && so.getEorderid() != null) {
                    		// ?????????????????????
    	                    ActiveMQ.sendObjectMessage(new WsOrder(sp,WsOrder.Method.REGISTER_REPORT),MQSubject.WEBSERVICECLIENT.getSendName(), StrKit.getRandomUUID(), 4);
    	                    
                    		// ??????????????????
                    		//ActiveMQ.sendObjectMessage(new WsOrder(sp,WsOrder.Method.REGISTER_REPORT_EVENT),MQSubject.WEBSERVICECLIENT.getSendName(), StrKit.getRandomUUID(), 4);
                    	
                    	}
                    }
	                    
	                log.info("Finished processing " + order);
                }
                catch (JMSException e) {
	                log.error("jms error during processing message: " + message, e);
	            }
                catch (Throwable e) {
	                log.error("unexpected error during processing message: " + message,e);
	            }
            } 
            else {
            	log.info(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String makeReportAsPdf(Report report,int port,String serverurl) {
    	LocalDate now=LocalDate.now();
    	String relativePath = "report\\"+now.getYear()+"\\"+now.getMonthValue()+"\\"+now.getDayOfMonth()+"\\"+report.getId()+".pdf";
    	String pdffilename=PropKit.use("system.properties").get("sysdir") + "\\" + relativePath;
    	try {
    		log.info(pdffilename);
    		File pdFile=new File(pdffilename);
    		byte[] b=PrintController.sv.generatePdfReport(report, port, serverurl);
    		if(b!=null){
    			FileUtils.writeByteArrayToFile(pdFile, b);
    		}
	    	if(pdFile.exists()){
	    		String oldpdf=report.getRelativePath();
		    	report.setRelativePath(relativePath);
		    	if(report.update()&&StrKit.notBlank(oldpdf)&&!StrKit.equals(relativePath, oldpdf)){//??????????????????????????????????????????????????????????????????????????????????????????????????????????????????pdf??????
		    		FileUtils.deleteQuietly(new File(PropKit.use("system.properties").get("sysdir") + "\\" +oldpdf));
		    	}
	    	}
    	} catch (IOException e) {
    		pdffilename=null;
			e.printStackTrace();
		}
    	return pdffilename;
    }
    
    //?????????????????????his?????????????????????????????????????????????????????????
    public void afterRegister(Admission admission,Studyorder so , Studyprocess sp) {
    	if ("sa".equals(sp.getOperator())&&"??????".equals(sp.getOperatorname())) {// ?????????????????????????????????
    		//????????????????????????
    		ActiveMQ.sendObjectMessage(new WsOrder(admission,so,WsOrder.Method.PACSFEE_CONFIRM_OLD,""),
    			MQSubject.WEBSERVICECLIENT.getSendName(), StrKit.getRandomUUID(), 4);
    		
    		//?????????????????????
//    		ActiveMQ.sendObjectMessage(new WsOrder(admission,so,WsOrder.Method.PACSFEE_CONFIRM,""),
//        			MQSubject.WEBSERVICECLIENT.getSendName(), StrKit.getRandomUUID(), 4);
    	} else {// ???????????????????????????????????????
    		//????????????????????????
    		ActiveMQ.sendObjectMessage(new WsOrder(admission,so,WsOrder.Method.PACSFEE_CONFIRM_OLD,"1"),
    			MQSubject.WEBSERVICECLIENT.getSendName(), StrKit.getRandomUUID(), 4);
    		
    		//?????????????????????
//    		ActiveMQ.sendObjectMessage(new WsOrder(admission,so,WsOrder.Method.PACSFEE_CONFIRM,"1"),
//        			MQSubject.WEBSERVICECLIENT.getSendName(), StrKit.getRandomUUID(), 4);
    	}
    	
    	// ??????????????????????????????
		if (PatientSource.inpatient.equals(admission.getPatientsource()) || PatientSource.outpatient.equals(admission.getPatientsource())) {
			// ??????????????????????????????????????????????????????????????? ?????????????????????????????????
			ActiveMQ.sendObjectMessage(new WsOrder(so, WsOrder.Method.CALLING),MQSubject.WEBSERVICECLIENT.getSendName(), StrKit.getRandomUUID(), 4);
		}
    }
     
    public static void main(String[] arg) {
    	
    }
   
}
