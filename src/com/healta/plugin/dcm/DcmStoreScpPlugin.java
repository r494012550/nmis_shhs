package com.healta.plugin.dcm;

import org.apache.log4j.Logger;
import com.jfinal.plugin.IPlugin;

public class DcmStoreScpPlugin implements IPlugin {
	private final static Logger log = Logger.getLogger(DcmStoreScpPlugin.class);
	public static StoreSCP storeServer;
	public  String storecommand;
	
	
	public DcmStoreScpPlugin(String storecommand){
		this.storecommand=storecommand; 
	}

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		
		try {
			log.info("Starting DcmStoreScpService......");
			storeServer = new StoreSCP();
			storeServer.configAll(storecommand.split(" "));
			storeServer.start();
			log.info("Starting DcmStoreScpService Complete.");
            
        } catch (Exception e) {
        	log.error("dcmstorescp: " + e.getMessage());
            e.printStackTrace();
        }
		return true;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		storeServer.stop();
		return true;
	}

}
