package com.healta.ws;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.healta.model.Admission;
import com.healta.model.Eorder;
import com.healta.model.Patient;
import com.healta.model.Report;
import com.healta.model.Studyorder;
import com.healta.model.Studyprocess;
import com.healta.plugin.activemq.Destination;
import com.healta.plugin.activemq.JmsReceiver;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class WsReceiver extends JmsReceiver {
private final static Logger log = Logger.getLogger(WsReceiver.class);
	
	public WsReceiver(String name, Connection connection, Destination type, String subject)
			throws JMSException {
		super(name, connection, type, subject);
	}


	@Override
	public void onMessage(Message message) {
		try {
			if(message instanceof ObjectMessage) {
				ObjectMessage msg = (ObjectMessage)message;
				try {
					WsOrder order = (WsOrder) msg.getObject();
					log.info("Start processing " + order);
					Studyorder studyorder=order.getStudyorder();
					Admission adm=order.getAdmission();
					String xml=null;
					WsOrder.Method method=order.getMethod();
					Patient patient=null;
					Report report=null;
					Eorder eorder=null;
					Record record = new Record();
        		    Studyprocess sp=order.getSp();
        		    if(sp!=null) {
        		    	studyorder=Studyorder.dao.findById(sp.getStudyorderfk());
            			patient = Patient.dao.findById(studyorder.getPatientidfk());
            			adm = Admission.dao.findById(studyorder.getAdmissionidfk());
            			report=Report.dao.findById(sp.getReportfk());
            			eorder = Eorder.dao.findFirst("select * from eorder where eorderid = ?", studyorder.getEorderid());
            			
            			String enableUrgent = PropKit.use("wsconf.properties").get("enableUrgent");
            			//判断是否从视图获取数据
            			if("1".equals(enableUrgent)) {
            				eorder = (Eorder) Db.use("oracle_his2").find("select * from v_eorder where eorderid = ?",studyorder.getEorderid());
            			}
        		    }
					switch(method) {
					case PACSFEE_CONFIRM_OLD:
						xml=XmlBuilder.feeConfirm(studyorder, adm, order.getReMrak());
						WsClient.sendFeeConfirmOld(studyorder.getId(),xml);
						break;
					case PACSFEE_CONFIRM:
						xml=XmlBuilder.feeConfirm(studyorder, adm, order.getReMrak());
						WsClient.sendFeeConfirm(studyorder.getId(), xml, studyorder.getRegdatetime());
						break;
					case CALLING:
						xml=XmlBuilder.callNumber(studyorder);						
						WsClient.sendCallNum(studyorder.getId(), xml, studyorder.getRegdatetime());
						break;
					case CREATE_EXAMINATION_CRISIS:
						break;
					case RECIPTER_PACSFEE_CANCEL:
						break;
					case RECIPTER_PACSFEE_CONFIRM:
						break;
					case RECOPTER_PACSFEE_CANCEL:
						break;
					case RECOPTER_PACSFEE_CONFIRM:
						break;
					case REGISTER_REPORT:
						xml=XmlBuilder.registerReport(patient, adm, studyorder, report, record, eorder);
						WsClient.sendRegisterReport(studyorder.getId(), xml, report.getAudittime());
						break;
					case REGISTER_REPORT_EVENT:
						xml=XmlBuilder.registerReportEvent(patient, adm, studyorder, report, record, eorder);
						WsClient.sendRegisterReport(studyorder.getId(), xml, report.getAudittime());
						break;
					default:
						break;
					}
					log.info("Finished processing " + order);
				} catch (JMSException e) {
					log.error("jms error during processing message: " + message, e);
				} catch (Throwable e) {
					log.error("unexpected error during processing message: " + message, e);
				} 
			}else {
				log.info(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
