package com.healta.plugin.dcm;

import org.apache.log4j.Logger;
import com.jfinal.plugin.IPlugin;

public class DcmQRPlugin implements IPlugin {
	private final static Logger log = Logger.getLogger(DcmQRPlugin.class);
	public static DcmQRSCP qrServer;
	public  String qrcommand;
	
	
	public DcmQRPlugin(String qrcommand){
		this.qrcommand=qrcommand; 
	}

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		
		try {
			log.info("Starting DcmQRService......");
			qrServer = new DcmQRSCP();
			qrServer.configAll(qrcommand.split(" "));
			qrServer.start();
			log.info("Starting DcmQRService Complete.");
            
        } catch (Exception e) {
           log.error("dcmqrscp: " + e.getMessage());
            e.printStackTrace();
        }
		return true;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		qrServer.stop();
		return true;
	}

}
