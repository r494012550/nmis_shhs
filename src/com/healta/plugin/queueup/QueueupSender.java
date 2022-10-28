package com.healta.plugin.queueup;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.healta.model.Studyorder;
import com.healta.plugin.activemq.ActiveMQ;
import com.healta.plugin.activemq.JmsSender;
import com.healta.plugin.activemq.MQSubject;

public class QueueupSender {
	private final static Logger log = Logger.getLogger(QueueupSender.class);
	private Node studyNode;
	
	public QueueupSender(Studyorder studyorder,String patientname,
			String modalityname,String location,String doctor,boolean emergency) {
		studyNode=new Node(emergency);
		studyNode.setStudyid(studyorder.getStudyid());
		studyNode.setPatientname(patientname);
		studyNode.setSn(studyorder.getSequencenumber());
		studyNode.setModality(studyorder.getModalityType());
		studyNode.setModalityid(studyorder.getModalityid());
		studyNode.setModalityname(modalityname);
		studyNode.setLocation(location);
		studyNode.setDoctor(doctor);
	}
	
	public QueueupSender(String modality,Integer modalityid,String doctor) {
		studyNode=new Node(false);
		studyNode.setModality(modality);
		studyNode.setModalityid(modalityid);
		studyNode.setDoctor(doctor);
	}
	
	public Node getNode() {
		return this.studyNode;
	}
	
//	public void send(Node studyNode,Enum method) {
//		log.info("----Begin QueueupSend----");
//		try {
//			JmsSender sender = ActiveMQ.getSender(MQSubject.QUEUEUP.getSendName());
//			QueueupSendOrder order = new QueueupSendOrder(studyNode, method);
//			ObjectMessage objmes=sender.getSession().createObjectMessage(order);
//			objmes.setJMSMessageID("1");
//            sender.sendMessage(objmes,4);
//		} catch (JMSException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public void send(Enum method) {
		log.info("----Begin QueueupSend----");
		try {
			JmsSender sender = ActiveMQ.getSender(MQSubject.QUEUEUP.getSendName());
			QueueupSendOrder order = new QueueupSendOrder(studyNode, method);
			ObjectMessage objmes=sender.getSession().createObjectMessage(order);
			objmes.setJMSMessageID("1");
            sender.sendMessage(objmes,4);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
