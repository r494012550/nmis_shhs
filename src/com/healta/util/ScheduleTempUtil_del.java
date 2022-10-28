package com.healta.util;

import java.util.HashMap;

import com.jfinal.kit.PropKit;

public class ScheduleTempUtil_del {
	private static final String DEFAULT_TEMPLATE_KEY="default";
	private static final String DEFAULT_TEMPLATE_VALUE="schedule_5";
	private static HashMap<String, String> map=null;
	
	public static String getTemplate(String modality){
		
		String ret=null;
		if(map==null){
			String temps[]=PropKit.use("system.properties").get("schedule_template").split(",");
			map=new HashMap<String, String>();
			for(String temp:temps){
				map.put(temp.split(":")[0], temp.split(":")[1]);
			}
		}
		ret=map.get(modality);
		if(ret==null){
			ret=map.get("DEFAULT_TEMPLATE_KEY");
		}
		if(ret==null){
			ret=DEFAULT_TEMPLATE_VALUE;
		}
		return ret;
	}
}
