package com.healta.util;

import com.healta.model.User;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;

public class CallupKit {
	
	private final static String plaza_user_name="-u";
	private final static String plaza_password="-p";
	private final static String plaza_accession_numbers="-a";
	
	public static String callUpPlaza_loadDataCmd(User user){
		String cmd=PropKit.use("system.properties").get("launcherplazacmd_loaddata").trim();
		if(StrKit.notBlank(user.getPlazausername())){
			cmd+=" "+plaza_user_name+" "+user.getPlazausername();
		}
		if(StrKit.notBlank(user.getPlazapassword())){
			cmd+=" "+plaza_password+" "+user.getPlazapassword();
		}
		cmd+=" "+plaza_accession_numbers;
		return cmd;
	}
	
	public static String callUpPlaza_closeExamCmd(User user){
		String cmd=PropKit.use("system.properties").get("launcherplazacmd_closeexam").trim();
		if(StrKit.notBlank(user.getPlazausername())){
			cmd+=" "+plaza_user_name+" "+user.getPlazausername();
		}
		if(StrKit.notBlank(user.getPlazapassword())){
			cmd+=" "+plaza_password+" "+user.getPlazapassword();
		}
		return cmd;
	}
}
