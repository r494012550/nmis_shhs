package com.healta.listener;

import com.healta.constant.CacheName;
import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;

import java.util.List;

public class 	MySessionContext {
	private final static Logger log = Logger.getLogger(MySessionContext.class);
	//private static HashMap mymap = new HashMap();

//    public static synchronized void addSession(HttpSession session) {
//        if (session != null) {
////        	CacheKit.put("sessionCache", session.getId(), session);
//        }
//    }

//    public static synchronized void delSession(HttpSession session) {
//        if (session != null) {
////        	CacheKit.remove("sessionCache", session.getId());
//        }
//    }

//    public static synchronized HttpSession getSession(String session_id) {
//        if (session_id == null)
//        return null;
//        return (HttpSession) CacheKit.get("sessionCache", session_id);
//    }
	public static boolean isOnline(Integer userid){
		boolean ret=false;
		String sessionid=getSessionId(userid);
		if(StrKit.notBlank(sessionid)){
			try{
				Session session = getSession(sessionid);
				if(session!=null){
					SimpleSession ss=(SimpleSession)session;
					ss.validate();
					ret= true;
				}
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		log.info("[MySessionContext] userid is online:"+ret);
		return ret;
	}
    public static synchronized String getSessionId(Integer userid) {
    	return CacheKit.get("user_sessionCache", userid);
    }
    
    public static synchronized Session getSession(String sessionid) {
    	return (Session)CacheKit.get("shiro_activeSessionCache", sessionid);
    }
    
    public static synchronized void addUserSession(Integer userid, String sessionid) {
        CacheKit.put("user_sessionCache", userid, sessionid);
    }
    
    public static synchronized void removeUserSession(Integer userid) {
        CacheKit.remove("user_sessionCache", userid);
    }

	public static synchronized List<Integer> getAllUserId() {
		return CacheKit.getKeys(CacheName.USER_SESSION);
	}
}
