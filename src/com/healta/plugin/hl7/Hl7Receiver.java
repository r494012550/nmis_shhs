package com.healta.plugin.hl7;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.apache.activemq.ScheduledMessage;
import org.apache.log4j.Logger;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.hl7.HL7DeviceExtension;
import org.dom4j.Document;
import org.dom4j.io.SAXContentHandler;
import org.regenstrief.xhl7.HL7XMLReader;
import org.regenstrief.xhl7.MLLPDriver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.healta.model.Ae;
import com.healta.plugin.activemq.ActiveMQ;
import com.healta.plugin.activemq.Destination;
import com.healta.plugin.activemq.JmsReceiver;
import com.healta.plugin.activemq.JmsSender;
import com.healta.plugin.activemq.MQSubject;
import com.healta.plugin.hl7.message.ACK;
import com.healta.plugin.hl7.message.MSH;
import com.jfinal.kit.PropKit;

public class Hl7Receiver extends JmsReceiver {
	private final static Logger log = Logger.getLogger(Hl7Receiver.class);
  
    private final Integer acTimeout=10000;
    private final Integer soCloseDelay=50;
    
    private final Device device = new Device("hl7receive");
    private final HL7DeviceExtension hl7Ext = new HL7DeviceExtension();
    
    private final org.dcm4che3.net.Connection conn = new org.dcm4che3.net.Connection();
    
    public Hl7Receiver(String name,
                       Connection connection,
                       Destination type,
                       String subject) throws JMSException {
    	super(name, connection, type, subject);
    	
    	device.addDeviceExtension(hl7Ext);
        device.addConnection(conn);
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage msg = (TextMessage) message;
                System.out.println(msg.getText());
            } else if (message instanceof MapMessage) {
//                MapMessage msg = (MapMessage) message;
//                Enumeration enumer = msg.getMapNames();
//                while (enumer.hasMoreElements()) {
//                    Object obj = enumer.nextElement();
//                    System.out.println(msg.getObject(obj.toString()));
//                }
            } else if (message instanceof StreamMessage) {
//                StreamMessage msg = (StreamMessage) message;
//                System.out.println(msg.readString());
//                System.out.println(msg.readBoolean());
//                System.out.println(msg.readLong());
            } else if (message instanceof ObjectMessage) {
                ObjectMessage msg = (ObjectMessage) message;
                try {
	                HL7SendOrder order = (HL7SendOrder)msg.getObject();
	                try {
	                    log.info("Start processing " + order);
	                    sendTo(order.getHL7Message(), order.getReceiving());
	                    log.info("Finished processing " + order);
	                    
	                } 
	                catch (Exception e) {
	                    order.setThrowable(e);
	                    final int failureCount = order.getFailureCount() + 1;
	                    order.setFailureCount(failureCount);
//	                    final long delay = retryIntervalls.getIntervall(failureCount);
//	                    if (delay == -1L) {
//	                        log.error("Give up to process " + order);
//	                    } else {
	                    
	                    log.warn("Failed to process " + order+ ". Scheduling retry.", e);
                        
                        JmsSender sender = ActiveMQ.getSender(MQSubject.HL7.getSendName());
                        ObjectMessage objmes=sender.getSession().createObjectMessage(order);
                        objmes.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, PropKit.use("system.properties").getLong("scheduled_delay"));
                        //objmes.setStringProperty(ScheduledMessage.AMQ_SCHEDULED_CRON, "* * * * *");
	                    if(failureCount>PropKit.use("system.properties").getInt("max_failure_count")){
//	                    	sender.sendMessage(objmes,4,1000);
	                    	log.error("Give up to process " + order);
	                    }
	                    else{
	                    	sender.sendMessage(objmes,4);
	                    }
	                        
	                    
//	                    }
	                }
                }
                catch (JMSException e) {
	                log.error("jms error during processing message: " + message, e);
	            }
                catch (Throwable e) {
	                log.error("unexpected error during processing message: " + message,e);
	            }
            } else if (message instanceof BytesMessage) {
//                BytesMessage msg = (BytesMessage) message;
//                byte[] byteContent = new byte[1024];
//                int length = -1;
//                StringBuffer content = new StringBuffer();
//                while ((length = msg.readBytes(byteContent)) != -1) {
//                    content.append(new String(byteContent, 0, length));
//                }
//                System.out.println(content.toString());
            } else {
                System.out.println(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void sendTo(byte[] message, String receiver) throws Exception {
        Document rsp = invoke(message, receiver);
        checkResponse(rsp);
    }
    
    public Document invoke(byte[] message, String receiver) throws Exception {
    	
//    	org.dcm4che3.net.Connection remote = new org.dcm4che3.net.Connection();
//    	remote.setHostname("localhost");
//    	remote.setPort(2576);
//    	remote.setResponseTimeout(600000);
    	Ae ae=null;
    	try{
    		ae=Ae.dao.getAeByAET(receiver.substring(0, receiver.indexOf("^")));
    	}
    	catch(Exception e){
    		ae=new Ae();
    		ae.setHostname("localhost");
    		ae.setPort(2575);
    	}

    	org.dcm4che3.net.Connection remote = new org.dcm4che3.net.Connection();
    	remote.setHostname(ae.getHostname());
    	remote.setPort(ae.getPort());
    	remote.setResponseTimeout(600000);

//        conn.setHttpProxy(cl.getOptionValue("proxy"));
        conn.setHostname("127.0.0.1");
        remote.setTlsProtocols(conn.getTlsProtocols());
        remote.setTlsCipherSuites(conn.getTlsCipherSuites());
        Socket sock = conn.connect(remote);
        sock.setSoTimeout(conn.getResponseTimeout());
    	try {

	        MLLPDriver mllpDriver = new MLLPDriver(sock.getInputStream(), sock.getOutputStream(), true);
	        writeMessage(message, receiver, mllpDriver.getOutputStream());
            mllpDriver.turn();
            if (acTimeout > 0) {
            	sock.setSoTimeout(acTimeout);
            }
            if (!mllpDriver.hasMoreInput()) {
                throw new IOException("Receiver " + receiver
                        + " closed socket " + sock
                        + " during waiting on response.");
            }
            return readMessage(mllpDriver.getInputStream());
	        
    	} finally {
            if (soCloseDelay > 0)
                try {
                    Thread.sleep(soCloseDelay);
                } catch (InterruptedException ignore) {
                }
            conn.close(sock);
        }
    }
    
    private void writeMessage(byte[] message, String receiving, OutputStream out)
            throws UnsupportedEncodingException, IOException {
        final String charsetName = "ISO-8859-1";
        int offs = writePartTo(out, message, '|', 0, 4); //write MSH 1-4
        final int delim = receiving.indexOf('^');
        out.write(receiving.substring(0, delim).getBytes(charsetName));
        out.write('|');
        out.write(receiving.substring(delim + 1).getBytes(charsetName));
        while (message[++offs] != '|') {} //skip MSH-5 receiving application
        while (message[++offs] != '|') {} //skip MSH-6 receiving facility
        // write remaining message
        out.write(message, offs, message.length - offs);
    }
    
    private int writePartTo(OutputStream out, byte[] ba, char b, int offs, int count) throws IOException {
        for ( int i = offs ; i < ba.length ; i++) {
            if (ba[i]==b) {
                count--;
                if (count == 0) {
                    out.write(ba, offs, i-offs+1);
                    return i;
                }
            }
        }
        return -1;
    }
    
    private void checkResponse(Document rsp) throws Hl7Exception {
        MSH msh = new MSH(rsp);
        if ("ACK".equals(msh.messageType)) {
            ACK ack = new ACK(rsp);
            if (!("AA".equals(ack.acknowledgmentCode)
                    || "CA".equals(ack.acknowledgmentCode)))
                throw new Hl7Exception(ack.acknowledgmentCode, ack.textMessage);
        } else {
            log.warn("Unsupport response message type: " + msh.messageType
                    + '^' + msh.triggerEvent
                    + ". Assume successful message forward.");
        }
    }
    
    private Document readMessage(InputStream mllpIn) throws IOException,
		    SAXException {
		InputSource in = new InputSource(mllpIn);
		in.setEncoding("ISO-8859-1");
		XMLReader xmlReader = new HL7XMLReader();
		SAXContentHandler hl7in = new SAXContentHandler();
		xmlReader.setContentHandler(hl7in);
		xmlReader.parse(in);
		Document msg = hl7in.getDocument();
		return msg;
	}

}
