package com.healta.util;

import com.healta.model.User;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;

public class UserUtil {

	
	public static User getUser(Controller controller){
		User user=CacheKit.get("userCache", controller.getPara("username"));  
		return user;
	}
}
