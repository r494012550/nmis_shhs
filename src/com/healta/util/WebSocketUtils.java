package com.healta.util;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.JMSException;
import javax.websocket.Session;

import org.apache.activemq.jms.pool.PooledConnection;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.healta.license.VerifyLicense;
import com.healta.model.ChatGroup;
import com.healta.model.User;
import com.healta.plugin.activemq.ActiveMQ;
import com.healta.plugin.activemq.ChatSendOrder;
import com.healta.plugin.activemq.ChatSendReceiver;
import com.healta.plugin.activemq.Destination;
import com.healta.plugin.activemq.JmsReceiver;
import com.healta.plugin.activemq.MQSubject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

public class WebSocketUtils {
	private final static Logger log = Logger.getLogger(WebSocketUtils.class);
	private static Map<Integer, Session> clients = new ConcurrentHashMap<Integer, Session>();
	private static Map<Integer, JmsReceiver> topicreceivers = new ConcurrentHashMap<Integer, JmsReceiver>();
	private static Map<Integer, Date> clients_lastaccess_time = new ConcurrentHashMap<Integer, Date>();
	/*
    Add Session
     */
    public static void add(Integer userId, Session session) {
        clients.put(userId,session);
//        log.info("clients_size="+clients.size());
        PooledConnection connection=(PooledConnection)ActiveMQ.pooledConnectionMap.get(ActiveMQ.ChatName);
//        log.info("getNumActiveSessions="+connection.getNumActiveSessions());
//        log.info("getNumSessions="+connection.getNumSessions());
//        log.info("getNumtIdleSessions="+connection.getNumtIdleSessions());
//        try {
//        	if(VerifyLicense.hasModule("chat")&&topicreceivers.get(userId)==null){
//        		topicreceivers.put(userId, new ChatSendReceiver(MQSubject.CHATTOPICSEND.getReceiveName(), ActiveMQ.getConnection(ActiveMQ.ChatName), Destination.Topic, MQSubject.CHATTOPICSEND.getSubject(),javax.jms.Session.CLIENT_ACKNOWLEDGE).setReceive_userid(userId));
//        	}
//        	log.info("topicreceivers_size="+topicreceivers.size());
//		} catch (JMSException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        log.info("当前连接数 = " + clients.size());

    }

    /*
    Receive Message
     */
    public static void receive(Integer userId, String message) {
        log.info("收到消息 : UserId = " + userId + " , Message = " + message);
        log.info("当前连接数 = " + clients.size());
    }

    /*
    Remove Session
     */
    public static void remove(Integer userId) {
    	try {
			Session session=clients.get(userId);
			if(session!=null){
				session.close();
				clients.remove(userId);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	JmsReceiver receiver=topicreceivers.get(userId);
		if(receiver!=null){
			receiver.close();
			topicreceivers.remove(userId);
		}
        log.info("当前连接数 = " + clients.size());
    }
    
    /*
    Remove Session
     */
    public static boolean isOnline(Integer userId) {
    	if(userId!=null&&clients.keySet().contains(userId)) {
    		log.info("[UserId]="+userId+" is online.");
    		return true;
    	}
    	else {
    		log.info("[UserId]="+userId+" is offline.");
    		return false;
    	}
        //log.info(userId+"是否在线 = " + clients.size());
    }

    /*
    Get Session
     */
    public static boolean sendMessage(Integer userId , String message) {
        log.info("当前连接数 = " + clients.size()+";用户ID = "+userId+";消息内容 = "+message);
        Session session=clients.get(userId);
        log.info("当前用户连接的session为" + session);
        if(session != null&&session.isOpen()){
        	synchronized (session) {
        		try {
        			session.getAsyncRemote().sendText(message);
        		}
        		catch (Exception e) {
					log.error("Error sending message!"+e.getMessage());
					return false;
				}
        	}
            return true;
            
        }else{
        	return false;
        }
    }
    
    public static boolean sendMessage(Session session , String message) {
        log.info("当前用户连接的session为" + session);
        if(session != null&&session.isOpen()){
        	synchronized (session) {
        		try {
        			session.getAsyncRemote().sendText(message);
        		}
        		catch (Exception e) {
					log.error("Error sending message!"+e.getMessage());
					return false;
				}
        	}
            return true;
            
        }else{
        	return false;
        }
    }
    
    public static void sendQueueMessage(Integer userId , String message) {
    	log.info("当前连接数 = " + clients.size()+";用户ID = "+userId+";消息内容 = "+message);
    	Session session=clients.get(userId);
    	try{
	    	if(session!=null){
	    		synchronized (session) {
	        		session.getAsyncRemote().sendText(message);
	        	}
	    	}
	    	else{
	    		log.warn("The user is offline,Scheduling retry.");
//	    		ActiveMQ.sendObjectMessage(new ChatSendOrder(userId, message),MQSubject.CHATQUEUESEND.getSendName(), StrKit.getRandomUUID(), 4);
	    	}
    	}
    	catch (Exception e) {
    		log.warn("Failed to send message,Scheduling retry.", e);
//    		ActiveMQ.sendObjectMessage(new ChatSendOrder(userId, message),MQSubject.CHATQUEUESEND.getSendName(), StrKit.getRandomUUID(), 4);
		}
    }
    
    /**
     * 
     * @param userId 用户id
     * @param message json消息对象
     */
    public static void sendTopicMessage(Integer userId , String message) {
    	log.info("当前连接数 = " + clients.size()+";用户ID = "+userId+";消息内容 = "+message);
    	log.info("VerifyLicense.hasModule.chat:" + VerifyLicense.hasModule("chat"));
	    ActiveMQ.sendObjectMessage(new ChatSendOrder(userId, message),MQSubject.CHATTOPICSEND.getSendName(), StrKit.getRandomUUID(), 4);
    }
    
    public static boolean broadcastMessage(String message) {
        log.info("广播消息，当前连接数 = " + clients.size()+";消息内容 = "+message);
        clients.forEach((k, v) -> {
	   		synchronized (v) {
	   			v.getAsyncRemote().sendText(message);
	        }
        });
        return true;
    }
    public static boolean broadcastMessageExceptMe(Integer meUserId,String message) {
    	 log.info("广播消息，当前连接数 = " + clients.size()+";消息内容 = "+message);
    	 clients.forEach((k, v) -> {
    		 if(k.intValue()!=meUserId.intValue()){
    			 synchronized (v) {
          			v.getAsyncRemote().sendText(message);
    			 }
    		 }
    	 });
        return true;
    }
    
    public static boolean broadcastQueueMessageExceptMe(Integer meUserId,String message) {
   	 log.info("广播消息，当前连接数 = " + clients.size()+";消息内容 = "+message);
   	 clients.forEach((k, v) -> {
   		 if(k.intValue()!=meUserId.intValue()){
   			 synchronized (v) {
         			v.getAsyncRemote().sendText(message);
   			 }
   		 }
   	 });
       return true;
   }
   
   public static Record returnUserRecord(Object userId) {
	   User user = User.dao.findById(userId);
		Record record = new Record();
		record.set("avatar", "image/getAvatarImg?path=" + user.getAvatar());
		record.set("username", user.getUsername());
		record.set("groupid", 1);
		record.set("id", userId);
		record.set("sign", user.getSign());
		record.set("type", "friend");
		return record;
   }
   
   public static Record returnGroupRecord(Integer groupId) {
	   ChatGroup chatGroup = ChatGroup.dao.findById(groupId);
		Record record = new Record();
		record.set("type", "group");
		record.set("avatar", "/report/image/getGroupAvatarImg?path="+chatGroup.getAvatar());
		record.set("groupname", chatGroup.getGroupname());
		record.set("id", chatGroup.getId());
		return record;
   }
   
   public static JSONObject returnJAONObj(Integer fromid, Integer userId, Integer checkMessagetype, Integer toType, Integer handelResult, String messageType, Integer groupId) {
	   JSONObject obj = new JSONObject();
	   obj.put("checkMessagetype", checkMessagetype);
	   if(handelResult == 1) {
		   if(toType == 1) {//代表返回的是申请方
			   if(messageType == "friend") {
//				   obj.put("checkMessageCount", ChatService.queryNoHandelChatCheckMessage(fromid));
				   obj.put("fromUsername",User.dao.findById(userId).getUsername());
				   obj.put("handelResult",handelResult);
				   obj.put("friend", returnUserRecord(userId));
			   }else {
				   obj.put("checkMessagetype", checkMessagetype);
//				   obj.put("checkMessageCount", ChatService.queryNoHandelChatCheckMessage(fromid));
				   obj.put("fromUsername",User.dao.findById(userId).getUsername());
				   obj.put("handelResult",handelResult);
			   }
		   }else {
			   if(messageType == "friend") {
							   
			   }else {
				   
			   }
		   }
	   }else {
		   obj.put("selfResult", "处理失败请重试！");
	   }
//	   if(handelResult == 1) {
//		   if(messageType == "friend") {
//			   if(toType == 1) {
//				   obj.put("friend", returnUserRecord(toid));
//			   }else {
//				   obj.put("friend", returnUserRecord(fromid));
//			   }
//		   }else {
//			   if(toType == 1) {
//				   obj.put("group", returnGroupRecord(groupId));
//			   }
//		   }
//	   }
//	   if(toType == 1) {//请求方
//		   obj.put("checkMessageCount", ChatService.queryNoHandelChatCheckMessage(fromid));
//		   obj.put("handelUsername",User.dao.findById(toid).getUsername());
//		   obj.put("handelResult",handelResult);
//	   }else {
//		   obj.put("selfResult", "处理成功！");
//	   }
	   return obj;
   }
   
   public static Integer getUseridByIP(String[] ips) {
		for (Map.Entry<Integer, Session> entry : clients.entrySet()) {
			String uri=entry.getValue().getRequestURI().toString();
			String query=entry.getValue().getQueryString();
			if(StrKit.isBlank(query))query="";
			log.info("Key = " + entry.getKey() + " Value = "+ uri+" QueryString="+query);
			for(String ip:ips){
				if(uri.indexOf(ip)>=0||query.indexOf(ip)>=0){
					return entry.getKey();
				}
			}
		}
		return null;
	}
   
   public static void setAccessTime(Integer userId){
	   clients_lastaccess_time.put(userId,new Date());
   }
   
   public static Date getLastAccessTime(Integer userId){
	   return clients_lastaccess_time.get(userId);
   }
 
}
