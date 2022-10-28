package com.healta.plugin.sequence;

import org.apache.log4j.Logger;
import com.jfinal.plugin.IPlugin;

public class SequencePlugin implements IPlugin {
	private final static Logger log = Logger.getLogger(SequencePlugin.class);
	
	@Override
	public boolean start() {

		try	{
			log.info("Starting SequencePlugin......");
			
			SequenceService.start();
	        
	        log.info("Starting SequencePlugin Complete.");  
	        
	    } 
		catch(Exception e) {
			System.out.println("Error in SequencePlugin : "+e);
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
