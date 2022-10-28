package com.healta.controller;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.healta.constant.ReportStatus;
import com.healta.constant.StudyOrderStatus;
import com.healta.constant.Version;
import com.healta.model.StatusColor;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

public class FrontCacheController extends Controller {
    
	public void index(){
		
		JSONArray array=new JSONArray();
		
		JSONObject json =new JSONObject();
		
		Field[] fields= ReportStatus.class.getFields();
		HashMap<String, Object> map=new HashMap<String, Object>();
		for(Field field:fields){
			try {
				map.put(field.getName(), field.get(null));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		json.put(ReportStatus.class.getSimpleName(), map);

		fields= StudyOrderStatus.class.getFields();
		map=new HashMap<String, Object>();
		for(Field field:fields){
			try {
				map.put(field.getName(), field.get(null));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		json.put(StudyOrderStatus.class.getSimpleName(), map);
		json.put("report_btn_enable", PropKit.use("reportstatus_btnenable.properties").getProperties());
		
		HashMap<String, String> map2=new HashMap<String, String>();
		List<StatusColor> scs= CacheKit.get("dicCache", "status_color", new IDataLoader(){ 
		    public Object load() { 
		      return StatusColor.dao.find("select * from status_color"); 
		}});
		scs.forEach(sc -> {
            map2.put(sc.getType()+"_"+sc.getStatusCode(), sc.getColor());
        });
		json.put("status_color", map2);
		
		JSONObject cache =new JSONObject();
		cache.put("port", getRequest().getServerPort());
		cache.put("ctx", getRequest().getContextPath());
		cache.put("serverName", getRequest().getServerName());
		cache.put("vs", Version.VERSION);
		cache.put("timeout", PropKit.use("system.properties").getInt("timeout",3000));
		
		array.add(json);
		array.add(cache);
		renderJson(array);
	}
    
}
