package com.healta.plugin.activemq;

public enum MQSubject {
	HL7("Hl7Send","Hl7Receive","HL7"),
	QUEUEUP("QueueUpSend","QueueUpReceive","QUEUEUP"),
	STUDYPROCESS("SPSend","SPReceive","STUDYPROCESS"),
	CHATQUEUESEND("ChatQueueSend","ChatQueueReceive","CHATQUEUESEND"),
	CHATTOPICSEND("ChatTopicSend","ChatTopicReceive","CHATTOPICSEND"),
	PACSUPDATE("PacsUpdateSend","PacsUpdateReceive","PACSUPDATE"),
	WORKFORCE("WorkforceSend","WorkforceReceive","WORKFORCE"),
	QUEUEUPINTERROGATION("QueueUpInterrogation","QueueUpInterrogationReceive","QUEUEUPINTERROGATION"),
	QUEUEUPINJE("QueueUpInjection","QueueUpInjeReceive","QUEUEUPINJE"),
	
	TOPICINTERROGATION("TopicInterrgation","TopicInterrgationReceive","TOPICINTERROGATION"),
	TOPICEXAMINE("TopicExamine","TopicExamineReceive","TOPICEXAMINE"),
	
	UNLOCKREPORT("Nmis_UnlockReportSend","Nmis_UnlockReportReceive","NMIS_UnlockReport"),//延迟解锁报告
	WEBSERVICECLIENT("Nmis_WebserviceClientSend","Nmis_WebserviceClientReceive","NMIS_WEBSERVICECLIENT"),//调用webservice
	
	ESINDEXDOC("ES_Index_DocSend","ES_Index_DocReceive","ES_Index_Doc");//索引表单数据
	
	private String subject;
	private String sendName;
	private String receiveName;
	
	private MQSubject(String sendName,String receiveName,String subject){
		this.sendName = sendName;
		this.receiveName=receiveName;
		this.subject = subject;  
	}
	public String getSubject() {
		return subject;
	}
	public String getSendName() {
		return sendName;
	}
	public String getReceiveName() {
		return receiveName;
	} 
	
	
}
