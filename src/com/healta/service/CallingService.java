package com.healta.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class CallingService {
	private final static Logger log = Logger.getLogger(ChatService.class);
	
	public List<Record> getModalityName() {
		List<Record> modalityname = Db.find("select id,modality_name from dic_modality where role = 'modality'");
		return modalityname;
	}
}
