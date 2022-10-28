package com.healta.server.dicom;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.dcm4che.util.DcmURL;

public class Echo {
	
	private static Logger log = Logger.getLogger(Echo.class);
	
	public long doEcho(String dcmURL){
		
		long ret=-1;
		
		ConfigProperties cfgProperties;
		CDimseService cDimseService;
        DcmURL url = new DcmURL(dcmURL);

        //Load configuration properties of the server
        try {
            cfgProperties = new ConfigProperties(StorageService.class.getResource("../resources/CDimseService.cfg"));
        } catch(IOException ioe) {
            log.error("Unable to create ConfigProperties instance ", ioe);
            return ret;
        }
		
		
        try {
            cDimseService = new CDimseService(cfgProperties, url);
        } catch(ParseException pe) {
            log.error("Unable to create CDimseService instance ", pe);
            return ret;
        }

        //Open association
        try {
            cDimseService.aASSOCIATE();
            
        } catch(IOException e) {
            log.error("Error while opening association ", e);
            return ret;
        } catch(GeneralSecurityException gse) {
            log.error("Error while opeing association ", gse);
            return ret;
        }

        //cECHO 
        try {
        	ret=cDimseService.cECHO();
        } catch(Exception e) {
            log.error("Error while calling cFIND() ", e);
            return ret;
        }
        
        
      //Release association
        try {
            cDimseService.aRELEASE(true);
        } catch(IOException e) {
            log.equals(e.getMessage());
            return ret;
        } catch(InterruptedException ie) {
            log.error(ie.getMessage());
            return ret;
        } 
        
        return 1;
		
	}

}
