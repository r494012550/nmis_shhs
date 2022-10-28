package com.healta.plugin.queueup;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import org.apache.activemq.ScheduledMessage;
import org.apache.log4j.Logger;
import com.healta.plugin.activemq.ActiveMQ;
import com.healta.plugin.activemq.Destination;
import com.healta.plugin.activemq.JmsReceiver;
import com.healta.plugin.activemq.JmsSender;
import com.jfinal.kit.PropKit;

public class QueueupReceiver extends JmsReceiver {
	private final static Logger log = Logger.getLogger(QueueupReceiver.class);
  
    public QueueupReceiver(String name,
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
	                QueueupSendOrder order = (QueueupSendOrder)msg.getObject();
	                try {
	                    log.info("Start processing " + order);
	                    Enum method=order.getMethod();
	                    if(method.equals(QueueMethod.Init)){
	                    	QueuingUpService.initQueue();
	                    }
	                    else if(method.equals(QueueMethod.Offer)){
	                    	QueuingUpService.offer(order.getNode());
	                    }
	                    else if(method.equals(QueueMethod.Poll)){
	                    	QueuingUpService.poll(order.getNode().getModalityid());
	                    }
	                    else if(method.equals(QueueMethod.Remove)){
	                    	QueuingUpService.remove(order.getNode());
	                    }
	                    else if(method.equals(QueueMethod.repeatCall)){
	                    	QueuingUpService.repeatCall(order.getNode());
	                    }
	                    else if(method.equals(QueueMethod.delete)){
	                    	QueuingUpService.delete(order.getNode());
	                    }
	                    else if(method.equals(QueueMethod.InterrogationCompleted)) {
	                    	QueuingUpService.InterroagationCompleted(order.getNode());
	                    }
	                    else if(method.equals(QueueMethod.InterrogationCalling)) {
	                    	QueuingUpService.InterrogationRemove(order.getNode());
	                    }
	                   
	                    
	                    log.info("Finished processing " + order);
	                    
	                } 
	                catch (Exception e) {
//	                    order.setThrowable(e);
//	                    final int failureCount = order.getFailureCount() + 1;
//	                    order.setFailureCount(failureCount);
////	                    final long delay = retryIntervalls.getIntervall(failureCount);
////	                    if (delay == -1L) {
////	                        log.error("Give up to process " + order);
////	                    } else {
//	                    
//	                    log.warn("Failed to process " + order+ ". Scheduling retry.", e);
//                        
//                        JmsSender sender = ActiveMQ.getSender("Hl7Send");
//                        ObjectMessage objmes=sender.getSession().createObjectMessage(order);
//                        objmes.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, PropKit.use("system.properties").getLong("scheduled_delay"));
//                        //objmes.setStringProperty(ScheduledMessage.AMQ_SCHEDULED_CRON, "* * * * *");
//	                    if(failureCount>PropKit.use("system.properties").getInt("max_failure_count")){
////	                    	sender.sendMessage(objmes,4,1000);
//	                    	log.error("Give up to process " + order);
//	                    }
//	                    else{
//	                    	sender.sendMessage(objmes,4);
//	                    }
	                        
	                    
//	                    }
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
                log.info(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
