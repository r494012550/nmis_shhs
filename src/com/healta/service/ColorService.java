package com.healta.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.constant.CacheName;
import com.healta.model.StatusColor;
import com.healta.model.Syscode;
import com.healta.util.SyscodeKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

public class ColorService {
	private final static Logger log = Logger.getLogger(ColorService.class);
	
	public List<StatusColor> getColor(String type){
		String sql="select * from status_color";
		if(StringUtils.isNotBlank(type)){
			sql+=" where type = '"+type+"'" ;
		}
		return StatusColor.dao.find(sql);
	}

	public List<Record> findColor(String locale) {
		List<Record> ret=new ArrayList<Record>();
		
		List<Syscode> syscode=new ArrayList<Syscode>();
		syscode.addAll(SyscodeKit.INSTANCE.getCodes("0005"));
		syscode.addAll(SyscodeKit.INSTANCE.getCodes("0007"));
		
		List<StatusColor> statusColor=StatusColor.dao.find("select * from status_color");
		
		for(Syscode sc:syscode) {
			Record record=new Record();
			for(StatusColor color:statusColor) {
				if(sc.getCode().equals(color.getStatusCode())) {
					record.set("id", color.getId());
					record.set("color", color.getColor());
				}
			}
			record.set("status_code", sc.getCode());
			record.set("status", SyscodeKit.INSTANCE.getCodeDisplayname(sc, locale));
			record.set("type", sc.getType());
			record.set("typedesc", SyscodeKit.INSTANCE.getCodeTypeDisplay(sc.getType(), locale));
			
			if(record.getStr("color")==null) {
				record.set("color", "#FFFFFF");
			}
			ret.add(record);
		}
		return ret;
	}
	
	public boolean saveColor(StatusColor sc){
		boolean ret = false; 
		if(sc.getId()!=null){
			ret = sc.update();
		}
		else{
			ret = sc.remove("id").save();
		}
		if(ret) {
			CacheKit.remove("dicCache", "status_color");
		}
		return ret;
	}
	

}
