package com.healta.plugin.activemq;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.activemq.ScheduledMessage;
//import org.apache.activemq.jms.pool.PooledConnection;
import org.apache.log4j.Logger;


public class ActiveMQ {
	public static final ConcurrentHashMap<String, Connection> pooledConnectionMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, JmsSender> senderMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, JmsReceiver> receiverMap = new ConcurrentHashMap<>();
    public static final String defaultName = "main";
    public static final String ChatName = "chat_conn";
    private final static Logger log = Logger.getLogger(ActiveMQ.class);
    public static void addSender(JmsSender sender) {
        senderMap.put(sender.getName(), sender);
    }
    public static JmsSender getSender(String name) {
        return senderMap.get(name);
    }
    public static void addReceiver(JmsReceiver receiver) {
        receiverMap.put(receiver.getName(), receiver);
    }
    public static JmsReceiver getReceiver(String name) {
        return receiverMap.get(name);
    }
    public static void addConnection(String connectionName,
    		Connection connection) {
        pooledConnectionMap.put(connectionName, connection);
    }
    public static Connection getConnection() {
        return pooledConnectionMap.get(defaultName);
    }
    public static Connection getConnection(String connectionName) {
        return pooledConnectionMap.get(connectionName);
    }
    
    public static void closePooledConnection(){
    	for(Entry<String, Connection> e: pooledConnectionMap.entrySet() ){
            try {
				e.getValue().close();
			} catch (JMSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
    }
    public static void sendObjectMessage(BaseJmsOrder order,String sendername,String messageid,Integer priority){
    	JmsSender sender = getSender(sendername);
		try {
			ObjectMessage objmes = sender.getSession().createObjectMessage(order);
			objmes.setJMSMessageID(messageid);
	        sender.sendMessage(objmes,priority);
	        log.info("send object message:"+order);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    public static void sendTextMessage(String textMessage,String sendername,String messageid,Integer priority){
    	JmsSender sender = getSender(sendername);
		try {
			TextMessage objmes = sender.getSession().createTextMessage(textMessage);
			objmes.setJMSMessageID(messageid);
	        sender.sendMessage(objmes,priority);
	        log.info("send text message:"+textMessage);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    public static void sendTextMessage(String textMessage,String sendername,String messageid,Long delay){
    	JmsSender sender = getSender(sendername);
		try {
			TextMessage objmes = sender.getSession().createTextMessage(textMessage);
			objmes.setJMSMessageID(messageid);
			log.info(delay);
			if(delay!=null&&delay>0){
				objmes.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
			}
	        sender.sendMessage(objmes);
	        log.info("send text message:"+textMessage);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
