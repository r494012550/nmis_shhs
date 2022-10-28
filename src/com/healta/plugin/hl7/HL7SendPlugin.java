package com.healta.plugin.hl7;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

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

import com.healta.plugin.hl7.message.ACK;
import com.healta.plugin.hl7.message.MSH;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.IPlugin;

public class HL7SendPlugin implements IPlugin {
	private final static Logger log = Logger.getLogger(HL7ServerPlugin.class);
	
	private final static Integer acTimeout=10000;
    private final static Integer soCloseDelay=50;
    private final Device device = new Device("hl7SendService");
    private final HL7DeviceExtension hl7Ext = new HL7DeviceExtension();
    private final static String PACSHOSTNAME = PropKit.use("system.properties").get("pacsServerIP");//PACS hostname
    private final static Integer PACSPORT = PropKit.use("system.properties").getInt("pacsPort");//PACS port
    
    private final static org.dcm4che3.net.Connection conn = new org.dcm4che3.net.Connection();
	
	public HL7SendPlugin(){
	}
	
	@Override
	public boolean start() {
		log.info("Starting Hl7SendPlugin......");
		device.addDeviceExtension(hl7Ext);
        device.addConnection(conn);
		log.info("Starting Hl7SendPlugin Complete");
		return true;
	}

	@Override
	public boolean stop() {

		return true;
	}
	
	
	public static void sendTo(byte[] message, String receiver) throws Exception {
    	System.out.println("message: " + message);
    	System.out.println("receiver: " + receiver);
        Document rsp = invoke(message, receiver);
        checkResponse(rsp);
    }
    
    public static Document invoke(byte[] message, String receiver) throws Exception {
    	log.info("receiver： " + receiver);
    	log.info("hostname： " + PACSHOSTNAME + " && port： "+PACSPORT);
    	org.dcm4che3.net.Connection remote = new org.dcm4che3.net.Connection();	
    	
    	remote.setHostname(PACSHOSTNAME);
    	remote.setPort(PACSPORT);
    	remote.setResponseTimeout(60000);
        remote.setTlsProtocols(conn.getTlsProtocols());
        remote.setTlsCipherSuites(conn.getTlsCipherSuites());
        
        //conn.setHostname("127.0.0.1");
        conn.setAcceptTimeout(6000000);
        conn.setConnectTimeout(6000000);
        conn.setRequestTimeout(6000000);
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
    
    private static void writeMessage(byte[] message, String receiving, OutputStream out)
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
    
    private static int writePartTo(OutputStream out, byte[] ba, char b, int offs, int count) throws IOException {
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
    
    private static void checkResponse(Document rsp) throws Hl7Exception {
        MSH msh = new MSH(rsp);
        if ("ACK".equals(msh.messageType)) {
            ACK ack = new ACK(rsp);
            //AA:Application Accept || CA:Commit Accept
            if (!("AA".equals(ack.acknowledgmentCode) || "CA".equals(ack.acknowledgmentCode))) {
            	throw new Hl7Exception(ack.acknowledgmentCode, ack.textMessage);
            }else {
            	log.info("Response AcknowledgmentCode:"+ack.acknowledgmentCode);
            }
        } else {
            log.warn("Unsupport response message type: " + msh.messageType
                    + '^' + msh.triggerEvent
                    + ". Assume successful message forward.");
        }
    }
    
    private static Document readMessage(InputStream mllpIn) throws IOException,
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
