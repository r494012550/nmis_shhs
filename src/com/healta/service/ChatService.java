package com.healta.service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.healta.constant.ChatConstant;
import com.healta.constant.TableNameConstant;
import com.healta.controller.ChatController;
import com.healta.model.ChatApplyFriend;
import com.healta.model.ChatGroupMsg;
import com.healta.model.ChatGroupUser;
import com.healta.model.ChatMessage;
import com.healta.model.ChatMessageImg;
import com.healta.model.User;
import com.healta.util.WebSocketUtils;
import com.healta.vo.WebsocketVO;
import com.healta.model.ChatFriendlist;
import com.healta.model.ChatGroup;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class ChatService {
	private final static Logger log = Logger.getLogger(ChatService.class);
	/**
	 * 根据用户id查询用户信息
	 * @param userId 用户id
	 * @return
	 */
	public Record queryUserByUserId(Integer userId) {
		return Db.findFirst("select u.username, u.sign, u.avatar, u.id from " + TableNameConstant.CHAT_USERS + " u where u.id = " + userId);
	}
	
	/**
	 * 查询所有用户自己除外
	 * @param userId 用户id
	 * @return
	 */
	public List<Record> queryFriend(Integer userId) {
		return Db.find("select distinct u.name as username, u.id, u.sign, u.avatar from chat_friendlist "
				+ "left join users u on chat_friendlist.friendid=u.id  where mineid=? and u.id is not null", userId);
	}
	
	public JSONObject getMessageBoxValue(User user,Integer page,Integer limit){
		Page<Record> pageRecord = Db.paginate(page, limit, 
				"select c.*,"
				+ "(select users.name from users where users.id=c.fromid) as fromname,"
				+ "(select users.avatar from users where users.id=c.fromid) as fromavatar,"
				+ "(select users.name from users where users.id=c.toid) as toname,"
				+ "(select users.avatar from users where users.id=c.toid) as toavatar,g.groupname"
				, " from chat_apply_friend c  "
						+ " LEFT JOIN chat_group g on c.groupid = g.id where  c.toid = " + user.getId() + " or c.fromid="+user.getId()+" order by c.createtime desc");
		JSONObject json = new JSONObject();
		json.put("msg", "");
		json.put("code", 0);
		json.put("count", pageRecord.getTotalRow());
		json.put("data", pageRecord.getList());
		return json;
	}
	
	public JSONObject findFriend(String para,Integer page,Integer limit){
		JSONObject data = new JSONObject();
		data.put("msg", "");
		data.put("code", 0);
		if(StrKit.isBlank(para)) {
			data.put("count", 0);
			return data;
		}else {
			String selectSql = " select * ";
			String whereSql = " from users where username like CONCAT('%',?,'%')  or name like CONCAT('%',?,'%')";
			Page<User> users = User.dao.paginate(page, limit, selectSql, whereSql,para,para);
			data.put("count", users.getTotalRow());
			data.put("data", users.getList());
			return data;
		}
	}
	
	public JSONObject getMyFriend(Integer userid,Integer page,Integer limit) {
		JSONObject data = new JSONObject();
		data.put("msg", "");
		data.put("code", 0);
		String selectSql = " select f.*,u.username,u.name,u.avatar,u.sign ";
		String whereSql = " from chat_friendlist f left join users u on u.id=f.friendid where f.mineid =?";
		Page<User> users = User.dao.paginate(page, limit, selectSql, whereSql,userid);
		data.put("count", users.getTotalRow());
		data.put("data", users.getList());
		return data;
		
	}
	
	public boolean handelApply(Integer res,Integer id){
		
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			ChatApplyFriend app=ChatApplyFriend.dao.findById(id);
			app.setHandelresult(res);
			app.setHandeltime(new Date());
			ret=ret&app.update();
			
			if(app.getType().intValue()==1){//好友申请
				if(res.intValue()==1){//同意好友申请，将双方添加至好友列表
					if(!checkIsFriend(app.getFromid(), app.getToid())){
						ChatFriendlist friendlist=new ChatFriendlist();
						friendlist.setMineid(app.getFromid());
						friendlist.setFriendid(app.getToid());
						ret=ret&friendlist.remove("id").save();
						
						if(WebSocketUtils.isOnline(app.getFromid())){
							JSONObject checkMessageObj = new JSONObject();
							checkMessageObj.put("checkMessagetype", ChatConstant.MESSAGETYPE_HANDLEAPPLYFRIEND);
							checkMessageObj.put("handelResult",1);
							Record r=returnUserRecord(app.getToid());
							checkMessageObj.put("friend", r);
							checkMessageObj.put("handelUsername",r.getStr("username"));
							WebSocketUtils.sendQueueMessage(app.getFromid(), JsonKit.toJson(new WebsocketVO(WebsocketVO.APPLY_FRIEND_GROUP,checkMessageObj)));
						}
						
					}
					if(!checkIsFriend(app.getToid(),app.getFromid())){
						ChatFriendlist friendlist=new ChatFriendlist();
						friendlist.setMineid(app.getToid());
						friendlist.setFriendid(app.getFromid());
						ret=ret&friendlist.remove("id").save();
						
						JSONObject checkMessageObj = new JSONObject();
						checkMessageObj.put("checkMessagetype", ChatConstant.MESSAGETYPE_HANDLEAPPLYFRIEND);
						checkMessageObj.put("handelResult",1);
						checkMessageObj.put("friend", returnUserRecord(app.getFromid()));
						WebSocketUtils.sendQueueMessage(app.getToid(), JsonKit.toJson(new WebsocketVO(WebsocketVO.APPLY_FRIEND_GROUP,checkMessageObj)));
						
					}
				}
				else{//拒绝好友申请
					if(WebSocketUtils.isOnline(app.getFromid())){//申请方在线，通知对方申请被拒绝
						JSONObject checkMessageObj = new JSONObject();
						checkMessageObj.put("checkMessagetype", ChatConstant.MESSAGETYPE_HANDLEAPPLYFRIEND);
						checkMessageObj.put("handelResult",0);
						checkMessageObj.put("handelUsername",User.dao.findById(app.getToid()).getName());
						WebSocketUtils.sendQueueMessage(app.getFromid(), JsonKit.toJson(new WebsocketVO(WebsocketVO.APPLY_FRIEND_GROUP,checkMessageObj)));
					}
				}
			}
			else if(app.getType().intValue()==2){//加群申请
				if(res.intValue()==1){//同意加群，将用户添加至群
					if(!checkInGroup(app.getFromid(),app.getGroupid())){
						ChatGroupUser chatGroupUser=new ChatGroupUser();
						chatGroupUser.setUserid(app.getFromid());
						chatGroupUser.setGroupid(app.getGroupid());
						ret=ret&chatGroupUser.remove("id").save();
						
						if(WebSocketUtils.isOnline(app.getFromid())){
							JSONObject checkMessageObj = new JSONObject();
							checkMessageObj.put("checkMessagetype", ChatConstant.MESSAGETYPE_HANDLEAPPLYGROUP);
							checkMessageObj.put("handelResult",1);
							checkMessageObj.put("handelUsername",User.dao.findById(app.getToid()).getName());
							Record g=returnGroupRecord(app.getGroupid());
							checkMessageObj.put("group", g);
							WebSocketUtils.sendQueueMessage(app.getFromid(), JsonKit.toJson(new WebsocketVO(WebsocketVO.APPLY_FRIEND_GROUP,checkMessageObj)));
						}
					}
				}
				else {//拒绝加群
					if(WebSocketUtils.isOnline(app.getFromid())){//申请方在线，通知对方申请被拒绝
						JSONObject checkMessageObj = new JSONObject();
						checkMessageObj.put("checkMessagetype", ChatConstant.MESSAGETYPE_HANDLEAPPLYGROUP);
						checkMessageObj.put("handelResult",0);
						checkMessageObj.put("handelUsername",User.dao.findById(app.getToid()).getName());
						WebSocketUtils.sendQueueMessage(app.getFromid(), JsonKit.toJson(new WebsocketVO(WebsocketVO.APPLY_FRIEND_GROUP,checkMessageObj)));
					}
				}
			}
			
			return ret;
		});
	}
	
	public Record returnUserRecord(Object userId) {
		User user = User.dao.findById(userId);
		Record record = new Record();
		if(StrKit.notBlank(user.getAvatar())) {
			record.set("avatar", "image/getAvatarImg?path=" + user.getAvatar());
		}
		else {
			record.set("avatar", "themes/head.ico");
		}
		record.set("username", user.getName());
		record.set("groupid", 1);
		record.set("id", userId);
		record.set("sign", user.getSign());
		record.set("type", "friend");
		return record;
	}
	   
	public Record returnGroupRecord(Integer groupId) {
		ChatGroup chatGroup = ChatGroup.dao.findById(groupId);
		Record record = new Record();
		record.set("type", "group");
		if(StrKit.notBlank(chatGroup.getAvatar())) {
			record.set("avatar", "image/getGroupAvatarImg?path="+chatGroup.getAvatar());
		}
		else {
			record.set("avatar", "themes/head.ico");
		}
		record.set("groupname", chatGroup.getGroupname());
		record.set("id", chatGroup.getId());
		return record;
	}
	
	
	public JSONObject findGroupByName(Integer page,Integer rows ,String name) {
		JSONObject data = new JSONObject();
		data.put("msg", "");
		data.put("code", 0);
		Page<Record> pageRecord = Db.paginate(page, rows, "select * ", "from " + TableNameConstant.CHAT_GROUP + " where state = 1 and groupname like CONCAT('%',?,'%') ",name);
		data.put("count", pageRecord.getTotalRow());
		data.put("data", pageRecord.getList());
		return data;
	}
	
	
	/**
	 * 查询所有群
	 * @return
	 */
	public List<Record> queryAllGroup(){
		return Db.find("select * from " + TableNameConstant.CHAT_GROUP);
	}
	
	/**
	 * 通过群id查询所有群成员
	 * @param groupId 群id
	 * @return
	 */
	public List<Record> queryFriendsByGroupId(Integer groupId){
		String sql = "select u.name as username, u.id, u.avatar, u.sign from " + TableNameConstant.CHAT_USERS + " u, " + TableNameConstant.CHAT_GROUP_USER + " g where g.groupid = ? and u.id = g.userid";
		return Db.find(sql,groupId);
	}
	
	/**
	  *查询个人聊天信息数量
	 * @param sendUserId 消息发送者id
	 * @param collectUserId 接收消息id
	 * @return
	 */
	public Integer queryFriendMsgCount(Integer sendUserId, Integer collectUserId) {
		return Db.queryInt("select count(id) from " + TableNameConstant.CHAT_MESSAGE + " where (fromid = " + sendUserId +"  and toid = " + collectUserId + ")  or  (fromid = " + collectUserId + " and toid = " + sendUserId + ") and sourcetype = 'friend'");
	}
	/**
	 * 查询群聊信息数量
	 * @param groupId 群id
	 * @return
	 */
	public Integer queryGroupMsgCount(String groupId) {
		return Db.queryInt("select count(id) from " + TableNameConstant.CHAT_MESSAGE + " where toid = '" + groupId + "' and sourcetype = 'group'");
	}
	
	/**
	 * 根据page， limit 查询对应的个人聊天记录
	 * @param page 第几页
	 * @param limit 每页多少条
	 * @param sendUserId 消息发送者id
	 * @param collectUserId 消息接受者id
	 * @return
	 */
	public Page<ChatMessage> queryFriendMsg(Integer page, Integer limit, Integer sendUserId, Integer collectUserId){
		String selectSql = "select u.username, m.fromid, u.avatar, m.createtime, m.msgcontent, m.msgType ";
		String fromAndWhereSql = " from " + TableNameConstant.CHAT_MESSAGE + " m INNER JOIN users u on m.fromid = u.id and m.sourcetype = 'friend' and ((m.fromid = " + sendUserId + "  and m.toid = " + collectUserId + ")  or  (m.fromid = " + collectUserId + " and m.toid = " + sendUserId + "))";
		return ChatMessage.dao.paginate(page, limit, selectSql, fromAndWhereSql);
	}
	
	/**
	 * 根据page， limit查询群消息
	 * @param page 第几页
	 * @param limit 每页多少条
	 * @param groupId 群id
	 * @return
	 */
	public Page<ChatMessage> queryGroupMsg(Integer page, Integer limit, String groupId){
		String selectSql = "select u.username, m.fromid, u.avatar, m.createtime, m.msgcontent, m.msgType ";
		String fromAndWhereSql = " from " + TableNameConstant.CHAT_MESSAGE + " m INNER JOIN users u on m.fromid = u.id and toid = '" + groupId + "' and sourcetype = 'group' ";
		return ChatMessage.dao.paginate(page, limit, selectSql, fromAndWhereSql);
	}
	
	/**
	 * 查询用户名
	 * @param userId 用户id
	 * @return
	 */
	public String queryUserNameByUserId(Integer userId) {
		return Db.queryStr("select username from users where id = " + userId);
	}
	
	/**
	 * 根据用户id修改签名
	 * @param userId 用户id
	 * @param sign 签名
	 * @return
	 */
	public boolean updateSignByUserId(Integer userId, String sign) {
		
		User user=new User();
		user.setId(userId);
		user.setSign(sign);
		return user.update();
	}
	
	/**
	 * 保存消息图片
	 * @param messageImgRecord 消息图片对象
	 * @return
	 */
	public boolean saveMsgImg(Record messageImgRecord) {
		return Db.save(TableNameConstant.CHAT_MESSAGE_IMG, messageImgRecord);
	}
	
	/**
	 * 根据用户id查询好友离线消息
	 * @param userId 用户id
	 * @return
	 */
	public List<ChatMessage> queryOffLineFriendMsgByUserId(Integer userId){
		String sql = "SELECT * from chat_message WHERE toid =? and isoffline = ? and sourcetype='friend'";
		return ChatMessage.dao.find(sql,userId,1);
	}
	
	/**
	 * 根据用户id查询离线群消息
	 * @param userId 用户id
	 * @return
	 */
	public List<ChatMessage> queryOffLineGroupMsgByUserId(Integer userId){
//		String sql = "select m.username,m.msgType, m.avatar, m.fromid, m.groupId as id, m.content, m.timestamp, g.id as gid  from " + TableNameConstant.CHAT_GROUP_MSG + " g, " + TableNameConstant.CHAT_MESSAGE + " m where g.userId = " + userId + " and g.msgId =  m.id";
		return ChatMessage.dao.find("select *,chat_group_msg.id as gid from chat_message inner join chat_group_msg on chat_group_msg.userid=? and chat_message.id=chat_group_msg.msgid",userId);
	}
	
	/**
	 * 根据消息id修改消息发送状态为0，即非离线消息
	 * @param messageId 消息id
	 * @return
	 */
	public Integer setMessageNotOffLine(Long messageId) {
		String updateMessageSql = "update " + TableNameConstant.CHAT_MESSAGE + " set isoffline = 0 where id= " + messageId;
		return Db.update(updateMessageSql);
	}
	
	/**
	 * 根据群消息id删除群消息
	 * @param id 群消息id
	 * @return
	 */
	public boolean delGroupMessageById(Long id) {
		return Db.deleteById(TableNameConstant.CHAT_GROUP_MSG, id);
	}
	
	/**
	 * 保存聊天消息
	 * @param record 消息对象
	 * @return
	 */
	public boolean saveChatMessage(Record record) {
		return Db.save(TableNameConstant.CHAT_MESSAGE, record);//保存消息
	}
	
	/**
	 * 保存聊天消息
	 * @param record 消息对象
	 * @return
	 */
	public boolean saveChatMessage(ChatMessage cm) {
		return cm.save();//保存消息
	}
	 
	/**
	 * 根据群id查询所有在该群的用户id并且自己除外
	 * @param groupNo 群id
	 * @param userId 用户本人id
	 * @return
	 */
	public List<ChatGroupUser> queryUserIdByGroupId(Integer groupNo, Integer userId){
		//String sql = "select userId from " + TableNameConstant.CHAT_GROUP_USER + " where groupId = " + groupNo + " and userId != " + userId  ;
		return ChatGroupUser.dao.find("select * from chat_group_user where groupid=? and userid !=?",groupNo,userId);
	}
	
	/**
	 * 保存群消息
	 * @param record 群消息对象
	 * @return
	 */
	public boolean saveChatGroupMessage(List<ChatGroupMsg> list) {
		Db.batchSave(list, list.size());
		return true;
	}
	
	/**
	 * 根据图片id查询图片信息
	 * @param id 图片id
	 * @return
	 */
	public ChatMessageImg queryMessageImgById(Integer id) {
		return ChatMessageImg.dao.findById(id);
	}

	public boolean checkIsFriend(Integer userId, Integer toId) {
		return Db.find(" select id from "+TableNameConstant.CHAT_FRIENDLIST+" c where c.mineid = ? and c.friendid = ?",userId,toId).size() > 0;
	}
	
	public boolean checkInGroup(Integer userId, Integer groupid) {
		return Db.find(" select id from "+TableNameConstant.CHAT_GROUP_USER+" c where c.userid=? and groupid=?",userId,groupid).size() > 0;
	}
	
	public boolean haveCheckMessageIsNoHandel(Integer userId, Integer toId) {
		return ChatApplyFriend.dao.find("select id from " + TableNameConstant.CHAT_APPLY_FRIEND + " where fromid = " + userId + " and toid = " + toId + " and type = 1 and handelresult is null").size() < 1 ? true : false;
	}
	
	public String applyToAddFriend(User user, Integer toId, String remark) {
		String ret="0";
		if(checkIsFriend(user.getId(),toId)){
			ret="1";//已经是好友
		}
		else{
			ChatApplyFriend checkMessage = new ChatApplyFriend();
			checkMessage.setFromid(user.getId());
			checkMessage.setToid(toId);
			checkMessage.setRemark(remark);
			checkMessage.setType(ChatConstant.APPLY_TYPE_FRIEND);
			checkMessage.save();
			if(WebSocketUtils.isOnline(toId)){
				JSONObject checkMessageObj = new JSONObject();
				checkMessageObj.put("checkMessagetype", ChatConstant.MESSAGETYPE_APPLYFRIEND);
				checkMessageObj.put("fromUsername", user.getName());
				WebSocketUtils.sendQueueMessage(toId, JsonKit.toJson(new WebsocketVO(WebsocketVO.APPLY_FRIEND_GROUP,checkMessageObj)));
			}
			
			ret="2";//保存好友申请
		}
		
		return ret;
	}
	
	public String applyToAddGroup(User user, Integer groupid, String remark) {
		String ret="0";
		if(checkInGroup(user.getId(), groupid)){
			ret="1";//已经是好友
		}
		else{
			ChatGroup cg = ChatGroup.dao.findById(groupid);
			if(cg!=null&&cg.getCreatorid()!=null) {
				ChatApplyFriend checkMessage = new ChatApplyFriend();
				checkMessage.setFromid(user.getId());
				checkMessage.setToid(cg.getCreatorid());
				checkMessage.setGroupid(groupid);
				checkMessage.setRemark(remark);
				checkMessage.setType(ChatConstant.APPLY_TYPE_GROUP);
				checkMessage.save();
				if(WebSocketUtils.isOnline(cg.getCreatorid())){
					JSONObject checkMessageObj = new JSONObject();
					checkMessageObj.put("checkMessagetype", ChatConstant.MESSAGETYPE_APPLYFGROUP);
					checkMessageObj.put("fromUsername", user.getName());
					WebSocketUtils.sendQueueMessage(cg.getCreatorid(), JsonKit.toJson(new WebsocketVO(WebsocketVO.APPLY_FRIEND_GROUP,checkMessageObj)));
				}
			}
			
			ret="2";//保存加群申请
		}
		
		return ret;
	}
	
	public Integer queryNoHandelChatCheckMessage(Integer toId) {
		return ChatApplyFriend.dao.find("select count(id) from " + TableNameConstant.CHAT_APPLY_FRIEND + " where toid = " + toId + " and type = 1 and handelresult is null" ).size();
	}
	
	public boolean updateChatCheckMessage(Integer handelResult, ChatApplyFriend chatCheckMessage) {
		chatCheckMessage.setHandelresult(handelResult);
		chatCheckMessage.setHandeltime(new Date());
		return chatCheckMessage.update();
	}
	
	public boolean saveUserFriend(Integer userId, Integer fromid) {
		ChatFriendlist chatUserFriend = new ChatFriendlist();
		chatUserFriend.setMineid(userId);
		chatUserFriend.setFriendid(fromid);
		return chatUserFriend.save();
	}
	
	public List<Record> queryGroupByUserId(Integer userId){
		return Db.find("select * from "+ TableNameConstant.CHAT_GROUP + " where state = 1 and id in ( select groupid from " + TableNameConstant.CHAT_GROUP_USER + " where userid = " + userId + ")");
	}
	public JSONObject getMyGroups(Integer userId){
		JSONObject obj = new JSONObject();
		obj.put("msg", "");
		obj.put("code", 0);
		obj.put("data", ChatGroup.dao.find("select * from "+ TableNameConstant.CHAT_GROUP +" where state = 1 and creatorid =? ",userId));
		return obj;
	}
	
	public JSONObject getGroupUserByGroupId(Integer groupid,Integer myid){
		JSONObject obj = new JSONObject();
		obj.put("msg", "");
		obj.put("code", 0);
		obj.put("data", Db.find("select a.*,b.avatar,b.username,b.name,b.sign from " + TableNameConstant.CHAT_GROUP_USER + 
				" a left join users b on b.id=a.userid where a.groupid=? and a.userid !=?",groupid,myid));
		return obj;
	}
	
	public JSONObject getFriendNotInGroup(Integer groupid,Integer myid){
		JSONObject obj = new JSONObject();
		obj.put("msg", "");
		obj.put("code", 0);
		obj.put("data", Db.find("select a.*,a.friendid as userid,c.avatar,c.username,c.name,c.sign from chat_friendlist a "
				+ "left join chat_group_user b on a.friendid =b.userid and b.groupid=? "
				+ "left join users c on a.friendid=c.id "
				+ "where a.mineid=? and b.id is null",groupid,myid));
		
		return obj;
	}
	
	public boolean createGroup(User user ,String groupname,String friendids){
		return Db.tx(() -> {
			boolean ret=true;
			ChatGroup cg=new ChatGroup();
			cg.setGroupname(groupname);
			cg.setCreatorid(user.getId());
			cg.setState(1);
			ret=ret&cg.remove("id").save();
			String[] ids=friendids.split(",");
			
			List<ChatGroupUser> list=new ArrayList<ChatGroupUser>();
			for(String id:ids){
				ChatGroupUser u=new ChatGroupUser();
				u.setGroupid(cg.getId());
				u.setUserid(Integer.valueOf(id));
				list.add(u);
			}
			ChatGroupUser u=new ChatGroupUser();
			u.setGroupid(cg.getId());
			u.setUserid(user.getId());
			list.add(u);
			Db.batchSave(list, list.size());
			
			list.forEach((x)->{
				if(WebSocketUtils.isOnline(x.getUserid())){
					JSONObject checkMessageObj = new JSONObject();
					checkMessageObj.put("checkMessagetype", ChatConstant.MESSAGETYPE_CREATEGROUP);
//					checkMessageObj.put("fromUsername", user.getName());
					checkMessageObj.put("groupname", groupname);
					Record g=returnGroupRecord(cg.getId());
					checkMessageObj.put("group", g);
					WebSocketUtils.sendQueueMessage(x.getUserid(), JsonKit.toJson(new WebsocketVO(WebsocketVO.APPLY_FRIEND_GROUP,checkMessageObj)));
				}
			});
			
			return ret;
		});
	}
	
	public boolean delGroup(Integer id,String groupname){
		return Db.tx(() -> {
			boolean ret=true;
			ret=ret&Db.deleteById(TableNameConstant.CHAT_GROUP, id);
			List<ChatGroupUser> list=ChatGroupUser.dao.find("select * from "+TableNameConstant.CHAT_GROUP_USER+" where groupid=?",id);
			int n=Db.delete("delete from "+TableNameConstant.CHAT_GROUP_USER+" where groupid=?", id);
			Db.delete("delete from "+TableNameConstant.CHAT_GROUP_MSG+" where groupid=?",id);
			ret=ret&(n==list.size());
			list.stream().forEach(x->{
				if(WebSocketUtils.isOnline(x.getUserid())){
					JSONObject checkMessageObj = new JSONObject();
					checkMessageObj.put("checkMessagetype", ChatConstant.MESSAGETYPE_DELETEGROUP);
					checkMessageObj.put("groupname", groupname);
					checkMessageObj.put("groupid", id);
					WebSocketUtils.sendQueueMessage(x.getUserid(), JsonKit.toJson(new WebsocketVO(WebsocketVO.APPLY_FRIEND_GROUP,checkMessageObj)));
				}
			});
			
			return ret;
		});
	}
	
	public boolean saveGroup(ChatGroup cg){
		//ChatGroup c=ChatGroup.dao.findById(cg.getId());
		//if(StrKit.equals("1", tmp)){
			File file = new File(PropKit.use("system.properties").get("tempdir")+System.getProperty("file.separator")+"groupAvatar" + System.getProperty("file.separator")+ cg.getAvatar());
			File toFile = new File(PropKit.use("system.properties").get("sysdir") + System.getProperty("file.separator")+"groupAvatar" +System.getProperty("file.separator")+ cg.getAvatar());
			if(file.exists()){
				try {
					FileUtils.copyFile(file, toFile);
					FileUtils.deleteQuietly(file);
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		//}
		return cg.update();
	}
	
	public boolean addOrRemoveMember(Integer groupid,String groupname,Integer userid,boolean add_or_remove){
		boolean ret=true;
		if(add_or_remove){
			ChatGroupUser cgu=new ChatGroupUser();
			cgu.setUserid(userid);
			cgu.setGroupid(groupid);
			ret=ret&cgu.remove("id").save();

			if(WebSocketUtils.isOnline(userid)){
				JSONObject checkMessageObj = new JSONObject();
				checkMessageObj.put("checkMessagetype", ChatConstant.MESSAGETYPE_CREATEGROUP);
				Record g=returnGroupRecord(groupid);
				checkMessageObj.put("group", g);
				WebSocketUtils.sendQueueMessage(userid, JsonKit.toJson(new WebsocketVO(WebsocketVO.APPLY_FRIEND_GROUP,checkMessageObj)));
			}
		}
		else{
			ChatGroupUser cgu=ChatGroupUser.dao.findFirst("select * from "+TableNameConstant.CHAT_GROUP_USER+" where userid=? and groupid=?",userid,groupid);
			if(cgu!=null){
				ret=ret&cgu.delete();
				
				if(WebSocketUtils.isOnline(userid)){
					JSONObject checkMessageObj = new JSONObject();
					checkMessageObj.put("checkMessagetype", ChatConstant.MESSAGETYPE_DELETEGROUP);
					checkMessageObj.put("groupname", groupname);
					checkMessageObj.put("groupid", groupid);
					WebSocketUtils.sendQueueMessage(userid, JsonKit.toJson(new WebsocketVO(WebsocketVO.APPLY_FRIEND_GROUP,checkMessageObj)));
				}
			}
		}
		
		return ret;
	}
}
