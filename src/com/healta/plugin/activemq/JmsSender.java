package com.healta.plugin.activemq;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

public class JmsSender {
	private String name;
    private Session session;
    private MessageProducer producer;
    public JmsSender(String name, Connection connection, Destination type, String subject) throws JMSException {
        this.name = name;
        // 事务性会话，自动确认消息
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // 消息的目的地（Queue/Topic）
        if (type.equals(Destination.Topic)) {
            Topic destination = session.createTopic(subject);
            producer = session.createProducer(destination);
        } else {
            Queue destination = session.createQueue(subject);
            producer = session.createProducer(destination);
        }
        // 持久化消息
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
    }
    public String getName() {
        return name;
    }
    public Session getSession() {
        return session;
    }
    public void sendMessage(Message message) throws JMSException {
        producer.send(message);
    }
    /*
     * 消息优先级从 0-9 十个级别，0-4 是普通消息，5-9 是加急消息。如果不指定优先级，则默认为 4。JMS 不要求严格按照这十个优先级发送消息，但必须保证加急消息要先于普通消息到达。**
     */
    public void sendMessage(Message message,int priority) throws JMSException {
    	producer.setPriority(priority);
        producer.send(message);
//        producer.send(arg0, arg1, arg2, arg3);
    }
    
    public void sendMessage(Message message,int priority,long timeToLive) throws JMSException {
//    	producer.setPriority(priority);
//    	producer.setTimeToLive(timeToLive);
//        producer.send(message);
        
        producer.send(message, DeliveryMode.PERSISTENT, priority, timeToLive);
//        producer.send(arg0, arg1, arg2, arg3);
    }
}
