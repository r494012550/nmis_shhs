package com.healta.controller;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.healta.constant.ChatMessageType;
import com.healta.license.VerifyLicense;
import com.healta.listener.MySessionContext;
import com.healta.model.ChatGroupMsg;
import com.healta.model.ChatMessage;
import com.healta.model.Notice;
import com.healta.model.NoticeRecord;
import com.healta.plugin.activemq.ActiveMQ;
import com.healta.plugin.activemq.ChatSendOrder;
import com.healta.plugin.activemq.MQSubject;
import com.healta.service.NoticeService;
import com.healta.util.ChatKit;
import com.healta.util.WebSocketUtils;
import com.healta.vo.WebsocketVO;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

@ServerEndpoint("/websocket/{userId}")
public class WebSocketController {
	private final static Logger log = Logger.getLogger(WebSocketController.class);
	public static Map<Integer, Instant> userOffLineTimeMap = new HashMap<Integer, Instant>();//记录用户下线时间
	/**
	 * 浏览器连服务器时触发此方法
	 * @param session
	 * @param userId
	 * @throws InterruptedException 
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("userId") Integer userId) throws InterruptedException {
		/**
		 * 判断当前连接用户是否在线
		 */
		log.info("[WebSocketServer] Connected : userId = "+userId+"; sessionid="+session.getId());
//		Thread.sleep(5000);
		if(!MySessionContext.isOnline(userId)){
    		WebSocketUtils.sendMessage(session, new WebsocketVO(WebsocketVO.SESSIONOVER,userId+"").toJson());
    	}else{
    		WebSocketUtils.add(userId , session);
    		WebSocketUtils.setAccessTime(userId);
    		if(VerifyLicense.hasModule("chat")) {
    			ChatKit.pushMessageOnLogin(userId,userOffLineTimeMap);
    		}
//    		List<Record> recordList = NoticeService.queryNoSendNoticeByUserId(userId);
//    		Record sendRecord = recordList.size() > 0 ? recordList.get(0): null;
//    		if(sendRecord != null) {
//    			WebSocketUtils.sendMessage(userId, new WebsocketVO(WebsocketVO.NOTICE,sendRecord.getObject("id") + "," + sendRecord.getObject("mustread") + "," + recordList.size()).toJson());
//    		}
    	}
	}

	/**
	 * 连接关闭会触发此事件
	 * @param session
	 * @param userId
	 */
	@OnClose
	public void onClose(Session session, @PathParam("userId") Integer userId) {
		log.info("[WebSocketServer] Close Connection : userId = " + userId+"; sessionid="+session.getId());
        WebSocketUtils.remove(userId);  
        if(VerifyLicense.hasModule("chat")) {
	        userOffLineTimeMap.put(userId, Instant.now());
//	        WebSocketUtils.sendTopicMessage(userId, new WebsocketVO(WebsocketVO.OFFLINE_REMINDER, userId+"").toJson());
	        WebSocketUtils.broadcastMessageExceptMe(userId,new WebsocketVO(WebsocketVO.OFFLINE_REMINDER, userId+"").toJson());
        }
        try {
			session.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**  
	 * 异常时触发
	 * @param throwable
	 * @param session
	 * @param userId
	*/
	@OnError
	public void onError(Throwable throwable,Session session,@PathParam("userId") Integer userId) {
		log.info("[WebSocketServer] onError : userId = " + userId+"; sessionid="+session.getId());
		WebSocketUtils.remove(userId);
		log.error(throwable.getMessage());
		try {
			session.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 服务器收到消息时触发此方法
	 * @param message
	 * @param session
	 * @param userId
	 */
	@OnMessage
	public void onMessage(String message, Session session, @PathParam("userId") Integer userId) {
		WebSocketUtils.setAccessTime(userId);
		if("ping".equals(message)) {
			log.info("[WebSocketServer] Received Message : userId = "+ userId + " , sessionid = " + MySessionContext.getSessionId(userId));
        	if(!MySessionContext.isOnline(userId)){
        		WebSocketUtils.sendMessage(userId, new WebsocketVO(WebsocketVO.SESSIONOVER,userId+"").toJson());
        	}else{
        		WebSocketUtils.sendMessage(userId, "pong");
        	}
		} else if(StringUtils.startsWith(message, "windowclose")) {
			log.info("[WebSocketServer] Received Message : userId = "+ userId + " , message = " + message);
			log.info(WebSocketUtils.getLastAccessTime(userId));
			ActiveMQ.sendTextMessage(userId.toString(), MQSubject.UNLOCKREPORT.getSendName(), StrKit.getRandomUUID() ,60000l);//延迟60秒
		} else {
        	log.info("[WebSocketServer] Received Message : userId = "+ userId + " , message = " + message);
        	JSONObject messageObject = JSONObject.parseObject(message);
        	JSONObject mine = messageObject.getJSONObject("mine");
        	JSONObject to = messageObject.getJSONObject("to");
        	if(to!=null){
        		String type = to.getString("type");
        		ChatMessage cm = ChatMessage.dao.createByJsonObj(messageObject);
        		if(StrKit.equals(ChatMessageType.FRIEND, type)){
        			ChatKit.getChatService().saveChatMessage(cm);
        			if(WebSocketUtils.isOnline(to.getInteger("id"))){
        				WebSocketUtils.sendQueueMessage(to.getInteger("id"), new WebsocketVO(WebsocketVO.MESSAGE_CONTENT,cm.toJsonObj(false)).toJson());
        			}else{
        				cm.setIsoffline(1);
        				cm.update();
        			}
        		}else if(StrKit.equals(ChatMessageType.GROUP, type)){
        			ChatKit.getChatService().saveChatMessage(cm);
//        			WebSocketUtils.broadcastMessageExceptMe(userId, new WebsocketVO(WebsocketVO.MESSAGE_CONTENT,cm.toJsonObj(false)).toJson());
        			ActiveMQ.sendObjectMessage(new ChatSendOrder(userId, new WebsocketVO(WebsocketVO.MESSAGE_CONTENT,cm.toJsonObj(false)).toJson()), MQSubject.CHATQUEUESEND.getSendName(), StrKit.getRandomUUID(), 4);
        			ChatKit.getChatService().queryUserIdByGroupId(to.getInteger("id"),mine.getInteger("id")).stream().forEach(cgu->{
        				//log.info(cgu.getUserid()+"user ,+"+WebSocketUtils.isOnline(cgu.getUserid()));
        				if(!WebSocketUtils.isOnline(cgu.getUserid())){
        					List<ChatGroupMsg> list=new ArrayList<ChatGroupMsg>();
        					ChatGroupMsg cgm=new ChatGroupMsg();
        					cgm.setMsgid(cm.getId());
        					cgm.setUserid(cgu.getUserid());
        					cgm.setGroupid(to.getInteger("id"));
        					list.add(cgm);
        					ChatKit.getChatService().saveChatGroupMessage(list);
        				}
        			});
        		}
        	}
		}
	}
}

