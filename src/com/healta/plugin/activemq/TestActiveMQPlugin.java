package com.healta.plugin.activemq;

import java.io.File;
import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.commons.io.FileUtils;

import com.healta.plugin.hl7.HL7SendOrder;
import com.healta.plugin.hl7.Hl7Receiver;

public class TestActiveMQPlugin {

	public static void main(String[] args) throws JMSException {
        ActiveMQPlugin p = new ActiveMQPlugin("failover:(tcp://localhost:61616)");
        p.start();
        String subject = "test";
        ActiveMQ.addSender(new JmsSender("Hl7Send", ActiveMQ.getConnection(), Destination.Queue, subject));//定义发送者
//        ActiveMQ.addReceiver(new JmsReceiver("testReceiver1", ActiveMQ.getConnection(), Destination.Queue, subject));//定义接受者
        ActiveMQ.addReceiver(new Hl7Receiver("Hl7Receive", ActiveMQ.getConnection(), Destination.Queue, subject));
        for (int i = 0; i < 1; i++) {
            new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        JmsSender sender = ActiveMQ.getSender("Hl7Send");

                        HL7SendOrder order = new HL7SendOrder(FileUtils.readFileToByteArray(new File("c:\\h17.txt")), "REPORT_HL7^REPORT_HL7","","");



                        ObjectMessage objmes=sender.getSession().createObjectMessage(order);
//                        objmes.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 30*1000);
//                        objmes.setStringProperty(ScheduledMessage.AMQ_SCHEDULED_CRON, "* * * * *");
                        objmes.setJMSMessageID("1");
                        sender.sendMessage(objmes,4);
                    } catch (JMSException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }.run();
        }
    }
}
