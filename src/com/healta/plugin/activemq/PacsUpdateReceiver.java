package com.healta.plugin.activemq;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import org.apache.log4j.Logger;

import com.alibaba.druid.util.StringUtils;
import com.healta.plugin.hl7.HL7SendServices;

public class PacsUpdateReceiver extends JmsReceiver {
	private final static Logger log = Logger.getLogger(PacsUpdateReceiver.class);
	
	public PacsUpdateReceiver(String name, Connection connection, Destination type, String subject)
			throws JMSException {
		super(name, connection, type, subject);
		// TODO Auto-generated constructor stub
	}
	
	 @Override
	 public void onMessage(Message message) {
		 try {
			 if(message instanceof ObjectMessage) {
				 ObjectMessage msg = (ObjectMessage)message;
				 try {
					 PacsUpdateOrder pacsUpdateOrder = (PacsUpdateOrder) msg.getObject();
					 
					 if(StringUtils.equals("patientMerge", pacsUpdateOrder.getType()) && pacsUpdateOrder.getPatient()!=null) {
						 new HL7SendServices().patientMerge(pacsUpdateOrder.getPatient(), pacsUpdateOrder.getRecord());
					 }else if(StringUtils.equals("procedureUpdate_ORU_R01", pacsUpdateOrder.getType()) && pacsUpdateOrder.getStudyorder()==null) {
						 new HL7SendServices().procedureUpdate_ORU_R01(pacsUpdateOrder.getRecord());
					 }
				 } catch (JMSException e) {
					 log.error("jms error during PACSupdate message: " + message, e);
				 } catch (Throwable e) {
		             log.error("unexpected error during PACSupdate message: " + message, e);
		         } 
			 }else {
				 log.info(message);
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	 }
	        
}
