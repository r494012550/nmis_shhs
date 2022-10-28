package com.healta.plugin.activemq;

import java.util.List;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

//import task.report.ESFormIndexDoc;

public class ESIndexDocReceiver extends JmsReceiver {
	private final static Logger log = Logger.getLogger(ESIndexDocReceiver.class);
  
    public ESIndexDocReceiver(String name,
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
                	ESIndexDocOrder order = (ESIndexDocOrder)msg.getObject();
                	List<Record> list= Db.find("select * from research_form_data where (indexed is null or indexed =0) and path is not null and dataid=? order by id",order.getDataid());
//                	if(list.size()>0) {
//                		ESFormIndexDoc indexdoc=new ESFormIndexDoc();
//                		indexdoc.indexDoc(order.getDataid(), list);
//                	}
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
