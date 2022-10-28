package com.healta.plugin.hl7;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import com.jfinal.log.Log4jLogFactory;
import com.jfinal.plugin.IPlugin;


public class HL7ServerPlugin implements IPlugin {
	private final static Logger log = Logger.getLogger(HL7ServerPlugin.class);
	public static Hl7ServerService hl7Server;
	
	public  String hl7command;
	
	public HL7ServerPlugin(String command){
		this.hl7command=command; 
	}
	
	@Override
	public boolean start() {

		try	{
			log.info("Starting Hl7Service......");
			
	        CommandLine cl = Hl7ServerService.parseComandLine(hl7command.split(" "));
	        hl7Server = new Hl7ServerService();
	        Hl7ServerService.configure(hl7Server, cl);
	        ExecutorService executorService = Executors.newCachedThreadPool();
	        ScheduledExecutorService scheduledExecutorService = 
	                Executors.newSingleThreadScheduledExecutor();
	        hl7Server.getDevice().setScheduledExecutor(scheduledExecutorService);
	        hl7Server.getDevice().setExecutor(executorService);
	        hl7Server.getDevice().bindConnections();
	        hl7Server.registerService(ORUService.messageTypes, new ORUService());
	        log.info("Starting Hl7Service Complete.");  
	        
	    } catch (ParseException e) {
	    	System.out.println("Error in Hl7Service : "+e);
			e.printStackTrace();
		} catch(Exception e) {
			System.out.println("Error in Hl7Service : "+e);
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean stop() {
		try {
//			hl7Server.getDevice().
			
			hl7Server.getDevice().unbindConnections();
		} catch (Exception e) {
			 //TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
