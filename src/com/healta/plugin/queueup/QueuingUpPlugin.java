package com.healta.plugin.queueup;

import org.apache.log4j.Logger;
import com.jfinal.plugin.IPlugin;

public class QueuingUpPlugin implements IPlugin {
	private final static Logger log = Logger.getLogger(QueuingUpPlugin.class);
	
	@Override
	public boolean start() {

		try	{
			log.info("Starting QueuingUpService......");
			
			QueuingUpService.initQueue();
	        
	        log.info("Starting QueuingUpService Complete.");  
	        
	    } 
		catch(Exception e) {
			System.out.println("Error in QueuingUpService : "+e);
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean stop() {
		try {

			
		} catch (Exception e) {
			 //TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
