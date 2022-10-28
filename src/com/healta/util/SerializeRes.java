package com.healta.util;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;

import com.jfinal.i18n.Res;

public class SerializeRes implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9071356846744327288L;
	public HashMap<String, String> map;
	
	public SerializeRes(Res res){
		map=new HashMap<String, String>();
		Enumeration<String> keys=res.getResourceBundle().getKeys();
		while(keys.hasMoreElements()){
            String key = keys.nextElement();
            map.put(key, res.getResourceBundle().getString(key));
        }
	}

	public HashMap<String, String> getMap() {
		return map;
	}
	
	public String get(String key) {
		return map.get(key);
	}

//	public void setMap(HashMap<String, String> map) {
//		this.map = map;
//	}

}
