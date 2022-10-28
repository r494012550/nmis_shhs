package com.healta.listener;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

import com.healta.model.User;
import com.healta.util.WebSocketUtils;
import com.healta.vo.WebsocketVO;

public class ShiroSessionListener implements SessionListener {
	private final static Logger log = Logger.getLogger(ShiroSessionListener.class);
	@Override  
	public void onStart(Session session) {//会话创建时触发  
		log.info("会话创建：" + session.getId());
	}
	@Override  
	public void onExpiration(Session session) {//会话过期时触发  
		log.info("会话过期：" + session.getId());
		Object object=session.getAttribute("user");
		if(object!=null){
			User user=(User)object;
			log.info("移除用户会话：" + user.getId());
			MySessionContext.removeUserSession(user.getId());
			WebSocketUtils.sendMessage(user.getId(), new WebsocketVO(WebsocketVO.SESSIONOVER,user.getId()+"").toJson());
		}
	}  
	@Override  
	public void onStop(Session session) {//退出/会话过期时触发 
		log.info("会话停止：" + session.getId());
		Object object=session.getAttribute("user");
		if(object!=null){
			User user=(User)object;
			log.info("移除用户会话：" + user.getId());
			MySessionContext.removeUserSession(user.getId());
			WebSocketUtils.sendMessage(user.getId(), new WebsocketVO(WebsocketVO.SESSIONOVER,user.getId()+"").toJson());
		}
	} 
}
