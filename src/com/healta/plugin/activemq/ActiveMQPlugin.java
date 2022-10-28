package com.healta.plugin.activemq;

import java.util.Properties;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnection;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
//import org.apache.activemq.pool.PooledConnection;
//import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.log4j.Logger;

import com.healta.license.VerifyLicense;
import com.healta.plugin.hl7.Hl7Receiver;
import com.healta.plugin.queueup.QueueupReceiver;
import com.healta.ws.WsReceiver;
import com.jfinal.plugin.IPlugin;

public class ActiveMQPlugin implements IPlugin {
	private final static Logger log = Logger.getLogger(ActiveMQPlugin.class);
	private String url;
    private String name;
    
    @Resource
    private ConnectionFactory connectionFactory;
    
    public ActiveMQPlugin(String url,String name) {
        this.url = url;
        this.name = name;
    }
    public ActiveMQPlugin(String url) {
        this.url = url;
        this.name = ActiveMQ.defaultName;
    }
    
	@Override
	public boolean start() {
		log.info("初始化activeMQ配置");
		boolean ret=true;
		try {
	        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
	        activeMQConnectionFactory.setTrustAllPackages(true);
	        activeMQConnectionFactory.setUserName(ActiveMQConnection.DEFAULT_USER);
	        activeMQConnectionFactory.setPassword(ActiveMQConnection.DEFAULT_PASSWORD);
	        activeMQConnectionFactory.setBrokerURL(url);
	        activeMQConnectionFactory.setDispatchAsync(true);//异步发送消息
	        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
	        pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory);
	        pooledConnectionFactory.setMaximumActiveSessionPerConnection(100);
	        pooledConnectionFactory.setIdleTimeout(120);
	        pooledConnectionFactory.setMaxConnections(10);
	        pooledConnectionFactory.setBlockIfSessionPoolIsFull(true);
        	PooledConnection connection = (PooledConnection) pooledConnectionFactory.createConnection();
//			Connection connection = connectionFactory.createConnection();
        	PooledConnection connection_char = (PooledConnection) pooledConnectionFactory.createConnection();
        	connection.start();
            ActiveMQ.pooledConnectionMap.put(name, connection);
            ActiveMQ.pooledConnectionMap.put(ActiveMQ.ChatName, connection_char);
            ActiveMQ.addSender(new JmsSender(MQSubject.HL7.getSendName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.HL7.getSubject()));//定义发送者
            ActiveMQ.addReceiver(new Hl7Receiver(MQSubject.HL7.getReceiveName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.HL7.getSubject()));
            
            ActiveMQ.addSender(new JmsSender(MQSubject.QUEUEUP.getSendName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.QUEUEUP.getSubject()));
            ActiveMQ.addReceiver(new QueueupReceiver(MQSubject.QUEUEUP.getReceiveName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.QUEUEUP.getSubject()));
            
            ActiveMQ.addSender(new JmsSender(MQSubject.QUEUEUPINTERROGATION.getSendName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.QUEUEUPINTERROGATION.getSubject()));
            ActiveMQ.addReceiver(new QueueupReceiver(MQSubject.QUEUEUPINTERROGATION.getReceiveName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.QUEUEUPINTERROGATION.getSubject()));
            
            
            ActiveMQ.addSender(new JmsSender(MQSubject.STUDYPROCESS.getSendName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.STUDYPROCESS.getSubject()));
            ActiveMQ.addReceiver(new StudyprocessReceiver(MQSubject.STUDYPROCESS.getReceiveName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.STUDYPROCESS.getSubject()));
            
            ActiveMQ.addSender(new JmsSender(MQSubject.PACSUPDATE.getSendName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.PACSUPDATE.getSubject()));
            ActiveMQ.addReceiver(new PacsUpdateReceiver(MQSubject.PACSUPDATE.getReceiveName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.PACSUPDATE.getSubject()));
            
            ActiveMQ.addSender(new JmsSender(MQSubject.CHATQUEUESEND.getSendName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.CHATQUEUESEND.getSubject()));
            ActiveMQ.addReceiver(new ChatSendReceiver(MQSubject.CHATQUEUESEND.getReceiveName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.CHATQUEUESEND.getSubject()));
            
            ActiveMQ.addSender(new JmsSender(MQSubject.CHATTOPICSEND.getSendName(), ActiveMQ.getConnection(), Destination.Topic, MQSubject.CHATTOPICSEND.getSubject()));
            //ActiveMQ.addReceiver(new ChatSendReceiver(MQSubject.CHATTOPICSEND.getReceiveName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.CHATTOPICSEND.getSubject()));
            
            //问诊sender发送到本地
            ActiveMQ.addSender(new JmsSender(MQSubject.TOPICINTERROGATION.getSendName(), ActiveMQ.getConnection(), Destination.Topic, MQSubject.TOPICINTERROGATION.getSubject()));

            //检查sender发送到本地
            ActiveMQ.addSender(new JmsSender(MQSubject.TOPICEXAMINE.getSendName(), ActiveMQ.getConnection(), Destination.Topic, MQSubject.TOPICEXAMINE.getSubject()));
            
            if(VerifyLicense.hasModule("chat")) {
	            ActiveMQ.addSender(new JmsSender(MQSubject.CHATQUEUESEND.getSendName(), ActiveMQ.getConnection(ActiveMQ.ChatName), Destination.Queue, MQSubject.CHATQUEUESEND.getSubject()));
	            ActiveMQ.addReceiver(new ChatSendReceiver(MQSubject.CHATQUEUESEND.getReceiveName(), ActiveMQ.getConnection(ActiveMQ.ChatName), Destination.Queue, MQSubject.CHATQUEUESEND.getSubject()));
	            ActiveMQ.addSender(new JmsSender(MQSubject.CHATTOPICSEND.getSendName(), ActiveMQ.getConnection(ActiveMQ.ChatName), Destination.Topic, MQSubject.CHATTOPICSEND.getSubject()));
	            //ActiveMQ.addReceiver(new ChatSendReceiver(MQSubject.CHATTOPICSEND.getReceiveName(), ActiveMQ.getConnection(name_chat), Destination.Queue, MQSubject.CHATTOPICSEND.getSubject()));
            }
            if(VerifyLicense.hasFunction("workforce")) {
	            ActiveMQ.addSender(new JmsSender(MQSubject.WORKFORCE.getSendName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.WORKFORCE.getSubject()));
	            ActiveMQ.addReceiver(new WorkforceReceiver(MQSubject.WORKFORCE.getReceiveName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.WORKFORCE.getSubject()));
            }
//          webService
            ActiveMQ.addSender(new JmsSender(MQSubject.WEBSERVICECLIENT.getSendName(), ActiveMQ.getConnection(), Destination.Queue,MQSubject.WEBSERVICECLIENT.getSubject()));
            ActiveMQ.addReceiver(new WsReceiver(MQSubject.WEBSERVICECLIENT.getSendName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.WEBSERVICECLIENT.getSubject()));
            
            ActiveMQ.addSender(new JmsSender(MQSubject.ESINDEXDOC.getSendName(), ActiveMQ.getConnection(), Destination.Queue,MQSubject.ESINDEXDOC.getSubject()));
            ActiveMQ.addReceiver(new ESIndexDocReceiver(MQSubject.ESINDEXDOC.getSendName(), ActiveMQ.getConnection(), Destination.Queue, MQSubject.ESINDEXDOC.getSubject()));
          
        } catch (JMSException e) {
            e.printStackTrace();
            ret=false;
        }
		return ret;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		ActiveMQ.closePooledConnection();
		return true;
	}

}
