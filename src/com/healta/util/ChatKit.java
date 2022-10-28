package com.healta.util;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.healta.constant.TableNameConstant;
import com.healta.model.ChatApplyFriend;
import com.healta.model.ChatMessage;
import com.healta.service.ChatService;
import com.healta.vo.WebsocketVO;

public class ChatKit {
	private final static Logger log = Logger.getLogger(ChatKit.class);
	private final static ChatService sv=new ChatService();
	public static final Integer USER_COLLECTION_SERVER_CHECK_TIME = 2;//用户连接服务器时判断用户距离上次断开连接的时间常量。单位毫秒。大于5000毫秒才会重新推送给该用户好友上线提醒。
	
	public static ChatService getChatService() {
		return sv;
	}
	
	public static void pushMessageOnLogin(Integer userId,Map<Integer, Instant> userOffLineTimeMap) {
		//判断该用户是否有个人离线消息
		List<ChatMessage> offLineFriendMessage = sv.queryOffLineFriendMsgByUserId(userId);
		if(offLineFriendMessage.size() > 0){
			TimerTask task=new TimerTask(){
				public void run(){
					for (ChatMessage msg : offLineFriendMessage) {
						WebSocketUtils.sendQueueMessage(userId, new WebsocketVO(WebsocketVO.MESSAGE_CONTENT,msg.toJsonObj(false)).toJson());
						sv.setMessageNotOffLine(msg.getId());
					} 
				}
			};
			new Timer().schedule(task,2000);  
		}
		
		//判断用户是否有离线群消息
		List<ChatMessage> offLineGroupMessage = sv.queryOffLineGroupMsgByUserId(userId);
		if(offLineGroupMessage.size() > 0){
				TimerTask task=new TimerTask(){
					public void run(){
						for ( ChatMessage msg : offLineGroupMessage) {
							WebSocketUtils.sendQueueMessage(userId, new WebsocketVO(WebsocketVO.MESSAGE_CONTENT,msg.toJsonObj(false)).toJson());
							sv.delGroupMessageById(msg.getLong("gid"));
						}
					}
				};
				new Timer().schedule(task,2000);  
		}
		Instant userOffLineTime = userOffLineTimeMap.get(userId);
		log.info(userOffLineTime);
		Instant now = Instant.now();
		if(userOffLineTime == null||Duration.between(userOffLineTime, now).getSeconds() > USER_COLLECTION_SERVER_CHECK_TIME) {
			//推送给所有在线好友
			WebSocketUtils.sendTopicMessage(userId, new WebsocketVO(WebsocketVO.ONLINE_REMINDER, userId+"").toJson());
		}
		
		//用户验证消息推送
		Integer checkMessageSize = ChatApplyFriend.dao.find("select id from " + TableNameConstant.CHAT_APPLY_FRIEND + " where toid = " + userId + " and handelresult is null" ).size();
		if(checkMessageSize > 0 && (userOffLineTime == null || Duration.between(userOffLineTime, now).getSeconds() > USER_COLLECTION_SERVER_CHECK_TIME)) {
			TimerTask task = new TimerTask(){
				public void run(){
					WebSocketUtils.sendQueueMessage(userId, new WebsocketVO(WebsocketVO.MESSAGE_BOX, checkMessageSize).toJson());
				}
			};
			new Timer().schedule(task,2000);  
		}
	}
}
