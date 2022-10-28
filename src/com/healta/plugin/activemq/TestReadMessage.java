package com.healta.plugin.activemq;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;

public class TestReadMessage {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = "service:jmx:rmi:///jndi/rmi://localhost:11099/jmxrmi";
		JMXServiceURL urls;
		try {
			urls = new JMXServiceURL(url);
		
			JMXConnector connector = JMXConnectorFactory.connect(urls,null);
			connector.connect();
			MBeanServerConnection conn = connector.getMBeanServerConnection();
	
			//这里brokerName的b要小些，大写会报错
			ObjectName name = new ObjectName("myDomain:brokerName=broker,type=Broker");
			BrokerViewMBean mBean = (BrokerViewMBean)MBeanServerInvocationHandler.newProxyInstance
			(conn, name, BrokerViewMBean.class, true);
			for(ObjectName na : mBean.getQueues()){
				QueueViewMBean queueBean = (QueueViewMBean) 
				MBeanServerInvocationHandler.newProxyInstance(conn, na, QueueViewMBean.class, true);
				System.out.println("******************************");
				System.out.println("队列的名称："+queueBean.getName());
				System.out.println("队列中剩余的消息数："+queueBean.getQueueSize());
				System.out.println("消费者数："+queueBean.getConsumerCount());
				System.out.println("出队列的数量："+queueBean.getDequeueCount());
//				queueBean.resume();
//				queueBean.pause();
//				queueBean.purge();
//				System.out.println(queueBean.getMessage(arg0));
//				Map<String, String> map=queueBean.getMessageGroups();
//				for(Map.Entry<String, String> e: map.entrySet() ){
//			        System.out.println("键:"+e.getKey()+", 值:"+e.getValue());
//				}
				System.out.println("all ObjectName：---------------");
				Set<ObjectInstance> set = conn.queryMBeans(null, null);
				for (Iterator<ObjectInstance> it = set.iterator(); it.hasNext();) {
					ObjectInstance oi = (ObjectInstance) it.next();
					System.out.println("\t" + oi.getObjectName());
				}
				
				
				MBeanInfo info = conn.getMBeanInfo(name);
				System.out.println("Class: " + info.getClassName());
				if (info.getAttributes().length > 0){
					for(MBeanAttributeInfo m : info.getAttributes())
						System.out.println("\t ==> Attriber：" + m.getName());
				}
				if (info.getOperations().length > 0){
					for(MBeanOperationInfo m : info.getOperations())
						System.out.println("\t ==> Operation：" + m.getName());
				}


			}
			connector.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
