package com.healta.plugin.activemq;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.activemq.ScheduledMessage;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.healta.model.ChatMessage;
import com.healta.plugin.activemq.Destination;
import com.healta.plugin.activemq.JmsReceiver;
import com.healta.util.WebSocketUtils;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;

public class ChatSendReceiver extends JmsReceiver {
	private final static Logger log = Logger.getLogger(ChatSendReceiver.class);
	
	private Integer receive_userid;
  
    public ChatSendReceiver(String name,
                       Connection connection,
                       Destination type,
                       String subject) throws JMSException {
    	super(name, connection, type, subject);
    }
    
    public ChatSendReceiver(String name,
            Connection connection,
            Destination type,
            String subject,int ack) throws JMSException {
    	super(name, connection, type, subject,ack);
    }

    public Integer getReceive_userid() {
		return receive_userid;
	}

	public ChatSendReceiver setReceive_userid(Integer receive_userid) {
		this.receive_userid = receive_userid;
		return this;
	}

	@Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage msg = (ObjectMessage) message;
                try {
                	ChatSendOrder order = (ChatSendOrder)msg.getObject();
                	if(this.getName().equals(MQSubject.CHATQUEUESEND.getReceiveName())){
		                try {
		                    log.info("Start processing " + order);
		                    
		                    if(WebSocketUtils.isOnline(order.getUserId())){
		                    	WebSocketUtils.sendMessage(order.getUserId(), order.getMessage());
		                    }
		                    else{
		                    	log.warn("The user is offline, userid:"+order.getUserId());
		                    	throw new RuntimeException("The user is offline");
		                    }
		                    log.info("Finished processing " + order);
		                } 
		                catch (Exception e) {
		                	order.setThrowable(e);
		                	final int failureCount = order.getFailureCount()+1;
		                	order.setFailureCount(failureCount);
		                	log.warn("Failed to process " + order+ ". Scheduling retry.", e);
		                	
		                	JmsSender sender = ActiveMQ.getSender(MQSubject.CHATQUEUESEND.getSendName());
	                        ObjectMessage objmes=sender.getSession().createObjectMessage(order);
	                        objmes.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, PropKit.use("system.properties").getLong("chat_scheduled_delay"));
	                        //objmes.setStringProperty(ScheduledMessage.AMQ_SCHEDULED_CRON, "* * * * *");
		                    if(failureCount>PropKit.use("system.properties").getInt("chat_max_failure_count")){
		                    	log.error("Give up to process " + order);
		                    	try{
		                    		JSONObject jsonObject=JSON.parseObject(order.getMessage());
		                    		Integer cid=jsonObject.getInteger("cid");
		                    		if(cid!=null){
		                    			new ChatMessage().set("id", cid).set("isoffline", 1).update();
		                    		}
		                    	}
		                    	catch (Exception ex) {
		                    		log.error("Set message offline failed .",ex);
		                    	}
		                    }
		                    else{
		                    	sender.sendMessage(objmes,4);
		                    }
		                }
                	}
                	else{
                		log.info("Start processing topic " + order+"receiveuserid="+getReceive_userid()+";userid="+order.getUserId()+";message:"+order.getMessage());
                		if(getReceive_userid().intValue()!=order.getUserId().intValue()){
                			if(WebSocketUtils.sendMessage(getReceive_userid(), order.getMessage())){
                				message.acknowledge();
                			}
                			else{
                				getSession().recover();
                			}
                		}
                		else{
                			message.acknowledge();
                		}
                	}
                }
                catch (JMSException e) {
	                log.error("jms error during processing message: " + message, e);
	            }
                catch (Throwable e) {
	                log.error("unexpected error during processing message: " + message,e);
	            }
            } 
            else {
                System.out.println(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
