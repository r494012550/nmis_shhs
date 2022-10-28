package com.healta.plugin.workforce;

import org.apache.log4j.Logger;
import com.jfinal.plugin.IPlugin;

public class WorkforcePlugin implements IPlugin {
	private final static Logger log = Logger.getLogger(WorkforcePlugin.class);
	
	@Override
	public boolean start() {
		try	{
			log.info("Starting WorkforcePlugin......");
			WorkforceServer.INSTANCE.init(false);
	        log.info("Starting WorkforcePlugin Complete.");
	    } 
		catch(Exception e) {
			log.error("Error in WorkforcePlugin : "+e);
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		return false;
	}

}
