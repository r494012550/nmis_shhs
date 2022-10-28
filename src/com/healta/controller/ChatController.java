package com.healta.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.healta.listener.MySessionContext;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.healta.constant.ChatConstant;
import com.healta.constant.TableNameConstant;
import com.healta.model.ChatGroup;
import com.healta.model.ChatMessage;
import com.healta.model.ChatMessageImg;
import com.healta.model.User;
import com.healta.service.ChatService;
import com.healta.util.ResultUtil;
import com.healta.util.WebSocketUtils;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

public class ChatController extends Controller{
	private final static Logger log = Logger.getLogger(ChatController.class);
	private final static ChatService sv=new ChatService();
	/**
	 * layim聊天窗口初始化数据获取接口
	 */
	public void queryBaseInformation(){
		User user = (User) getSession().getAttribute("user");
		JSONObject mine=new JSONObject();
		mine.put("username", user.getName());
		mine.put("id", user.getId());
		mine.put("status", "online");
		mine.put("sign", user.getSign());
		mine.put("avatar", StrKit.isBlank(user.getAvatar())?"themes/head.ico":getSession().getServletContext().getContextPath()+"/image/getAvatarImg?path="+user.getAvatar());
		JSONArray friends=new JSONArray();
		JSONObject friend_arr=new JSONObject();
		friend_arr.put("groupname", ChatConstant.GROUP_NAME);
		friend_arr.put("id", 1);
		List<Record> friendRecord = sv.queryFriend(user.getId());
		JSONArray friend_list=new JSONArray();
		for(Record record : friendRecord){
			JSONObject friend=new JSONObject();
			
			friend.put("username", record.getStr("username"));
			friend.put("id", record.getInt("id"));
			if(WebSocketUtils.isOnline(record.getInt("id"))||record.getInt("id").intValue()==user.getId().intValue()){
				friend.put("status", "online");
			}
			else{
				friend.put("status", "offline");
			}
			friend.put("sign", record.getStr("sign"));
			friend.put("avatar", StrKit.isBlank(record.getStr("avatar"))?"themes/head.ico":getSession().getServletContext().getContextPath()+"/image/getAvatarImg?path="+record.getStr("avatar"));
			friend_list.add(friend);
		}
		friend_list.sort(Comparator.comparing(obj -> ((JSONObject) obj).getString("status")).reversed());
		friend_arr.put("list", friend_list);
		friends.add(friend_arr);
		
		JSONArray groups=new JSONArray();
		List<Record> groupRecord = sv.queryGroupByUserId(user.getId());
		for (Record record : groupRecord) {
			
			JSONObject group=new JSONObject();
			group.put("groupname", record.getStr("groupname"));
			group.put("id", record.getInt("id"));
			group.put("avatar", StrKit.isBlank(record.getStr("avatar"))?"themes/head.ico":getSession().getServletContext().getContextPath()+"/image/getGroupAvatarImg?path="+record.getStr("avatar"));
			groups.add(group);
		}
		
		JSONObject basedata=new JSONObject();
		basedata.put("mine", mine);
		basedata.put("friend", friends);
		basedata.put("group", groups);
		
		JSONObject base=new JSONObject();
		
		base.put("code", 0);
		base.put("msg", "");
		base.put("data", basedata);
		
		renderJson(base);
	}
	
	
	/**
	 * 获取群成员接口
	 */
	public void queryFriendsByGroupId(){
		JSONObject ret=new JSONObject();
		ret.put("code", 0);
		ret.put("msg", "");
		List<Record> record = sv.queryFriendsByGroupId(getParaToInt("id"));
		for (Record i : record) {
			
			if(StrKit.notBlank(i.getStr("avatar"))){
				i.set("avatar", "image/getAvatarImg?path="+i.getStr("avatar"));
			}
			else{
				i.set("avatar", "themes/head.ico");
			}
		}
		JSONObject list=new JSONObject();
		list.put("list", record);
		ret.put("data", list);
		renderJson(ret);
	}
	
	/**
	 * 跳转至聊天记录页面
	 */
	
	public void toChatRecord(){
		renderJsp("/view/chat/chatRecord.jsp");
	}

	/**
	 *  获取在线用户
	 */
	public void getGroupMemberStatus() {
		try{
			String ids=get("ids");
			List<Record> ret=new ArrayList<Record>();
			if(StrKit.notBlank(ids)) {
				for(String id:ids.split(",")) {
					Record r=new Record();
					r.set("id", id);
					if(MySessionContext.isOnline(Integer.valueOf(id))) {
						r.set("online", true);
					} else {
						r.set("online", false);
					}
					ret.add(r);
				}
			}
			renderJson(ResultUtil.success(ret));
		} catch (Exception e) {
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}


	/**
	 * 返回用户消息数量
	 */
	public void queryMsgCountByType(){
		Integer count = 0;
		String type = getPara("type");
		if(type.equals("friend")){//好友消息数量，else群消息数量
			count = sv.queryFriendMsgCount(getParaToInt("sendUserId"), getParaToInt("collectUserId"));
		}else{
			count = sv.queryGroupMsgCount(getPara("collectUserId"));
		}
		renderJson(JsonKit.toJson(count));
	}
	
	/**
	 * 查看聊天记录时通过此方法返回聊天记录
	 */
	public void queryMsgByType(){
		Integer limit = getParaToInt("limit");
		Integer page = getParaToInt("page");
		String type = getPara("type");
		JSONObject data = new JSONObject();
		if(type.equals("friend")){//查看好友消息
			Page<ChatMessage> friendMsg = sv.queryFriendMsg(page, limit, getParaToInt("sendUserId"), getParaToInt("collectUserId"));
			List<ChatMessage> friendmsgList = friendMsg.getList();
			for (ChatMessage chatMessage : friendmsgList) {
				if(chatMessage.getMsgtype() == 1) {
					chatMessage.setMsgcontent("image/getChatImg?id=" + chatMessage.getMsgcontent());
				}
			}
			data.put("data", friendMsg.getList());
		}else{
			Page<ChatMessage> groupMsg = sv.queryGroupMsg(page, limit, getPara("collectUserId"));
			List<ChatMessage> groupMsgList = groupMsg.getList();
			for (ChatMessage chatMessage : groupMsgList) {
				if(chatMessage.getMsgtype() == 1) {
					chatMessage.setMsgcontent("image/getChatImg?id=" + chatMessage.getMsgcontent());
				}
			}
			data.put("data", groupMsg.getList());
		}
		data.put("msg", "");
		data.put("code", 0);
		renderJson(JsonKit.toJson(data));
	}
		
	/**
	 * 通过userid返回用户名称。
	 */
	public void queryUserNameByUserId(){
		renderJson(JsonKit.toJson(sv.queryUserNameByUserId(getParaToInt("id"))));
	}
	
	/**
	 * 修改签名
	 */
	public void updateSign() {
		User user = (User) getSession().getAttribute("user");
		user.setSign(getPara("sign"));
		renderJson(sv.updateSignByUserId(getParaToInt("userId"), getPara("sign")));
	}
	
	/**
	 * 聊天图片上传
	 */
	public void uploadImg(){
		UploadFile uploadFile = getFile("file","/chat/img/tmp");
		File file = uploadFile.getFile();
		String newfilename=LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)+System.getProperty("file.separator")+UUID.randomUUID().toString()+"_"+file.getName();
		try {
			FileUtils.copyFile(uploadFile.getFile(), new File(file.getParentFile().getParent()+System.getProperty("file.separator")+newfilename));
			FileUtils.deleteQuietly(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ChatMessageImg cmimg=new ChatMessageImg();
		cmimg.setImgpath(newfilename);
		cmimg.save();
		renderJson(new Record().set("code", 0).set("msg", "").set("data", new Record().set("src", "image/getChatImg?id="+cmimg.getId())));
	}
	
	/**
	 * 跳转至查找好友界面
	 */
	public void toSearchFriendOrGroup() {
		renderJsp("/view/chat/searchFriendOrGroup.jsp");
	}

	public void findFriend() {
		renderJson(sv.findFriend(getPara("para"), getParaToInt("page"), getParaToInt("limit")));
	}
	
	public void getMyfriend() {
		User user = (User) getSession().getAttribute("user");
		renderJson(sv.getMyFriend(user.getId(), getParaToInt("page"), getParaToInt("limit")));
	}
	
	public void toAddFriendOrGroup() {
		setAttr("user", User.dao.findById(getParaToInt("userId")));
		renderJsp("/view/chat/addFriendOrGroup.jsp");
	}
	
	public void applyToAddFriend(){
		User user = (User) getSession().getAttribute("user");
		try{
			renderJson(ResultUtil.success(sv.applyToAddFriend(user,getParaToInt("toId"),getPara("remark"))));
		}
		catch (Exception e) {
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}
	
	public void applyToAddGroup() {
		User user = (User) getSession().getAttribute("user");
		try{
			renderJson(ResultUtil.success(sv.applyToAddGroup(user,getParaToInt("groupid"),getPara("remark"))));
		}
		catch (Exception e) {
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}
	
	
	public void toMessageBox() {
		renderJsp("/view/chat/messageBox.jsp");
	}
	
	public void getMessageBoxValue() {
		User user = (User) getSession().getAttribute("user");
		renderJson(sv.getMessageBoxValue(user, getParaToInt("page"), getParaToInt("limit")));
	}
	
	public void handelApply(){
		if(sv.handelApply(getParaToInt("result"), getParaToInt("id"))){
			renderJson(ResultUtil.success());
		}
		else{
			renderJson(ResultUtil.fail("error"));
		}
	}
	
	public void checkIsFriend() {
		User user = (User) getSession().getAttribute("user");
		renderJson(sv.checkIsFriend(user.getId(), getParaToInt("toId")));
	}
	
	public void checkInGroup() {
		User user = (User) getSession().getAttribute("user");
		renderJson(sv.checkInGroup(user.getId(), getParaToInt("groupid")));
	}
	
	public void openGroupManger() {
		renderJsp("/view/chat/allGroup.jsp");
	}
	
	public void queryGroupByUserId() {
		JSONObject obj = new JSONObject();
		Page<Record> pageRecord = Db.paginate(getParaToInt("page"), getParaToInt("rows"), "select * ", "from " + TableNameConstant.CHAT_GROUP + " where state = 1 and creatorid = " + getParaToInt("userId"));
		obj.put("total", pageRecord.getTotalRow());
		obj.put("rows", pageRecord.getList());
		renderJson(JsonKit.toJson(obj));
	}
	
	public void getMyGroups(){
		User user = (User) getSession().getAttribute("user");
		renderJson(sv.getMyGroups(user.getId()));
	}
	
	public void findGroupByName() {
		renderJson(sv.findGroupByName(getParaToInt("page"), getParaToInt("limit"), getPara("name")));
	}
	
	public void toAddOrUpdateGroup() {
		Integer groupId = getParaToInt("id");
		ChatGroup group = ChatGroup.dao.findById(groupId);
		if(groupId != 0) {
			setAttr("id", groupId);
			setAttr("group", group);
			setAttr("groupAvatar", "/report/image/getGroupAvatarImg?path=" + group.getAvatar());
		}else {
			setAttr("id", groupId);
			setAttr("groupAvatar", "/report/image/getGroupAvatarImg?path=e5daae3f38fe4195b72af06740a47dcf_1560409357688.jpg");
		}
		renderJsp("/view/chat/addOrUpdateGroup.jsp");
	}
	
	/**
	 * 群头像上传
	 */
	public void uploadGroupAvatar(){
		try {
			User user=(User)getSession().getAttribute("user");
			String img=getPara("imgBase64");
			String header ="data:image";  
	        String[] imageArr=img.split(",");  
	        if(imageArr[0].contains(header)){//是img的  
	            // 去掉头部  
	        	img=imageArr[1];  
	        }
			String imagefile=StrKit.getRandomUUID()+"_"+new Date().getTime()+".jpg";
			File file = new File(PropKit.use("system.properties").get("tempdir")+System.getProperty("file.separator")+"groupAvatar" + System.getProperty("file.separator")+imagefile);
			try {
				FileUtils.writeByteArrayToFile(file, Base64.decodeBase64(img));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			renderJson(ResultUtil.success(getSession().getServletContext().getContextPath()+"/image/getGroupAvatarImg?tmp=1&path=" + imagefile));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}
	
	public void toAllGroupUser() {
		setAttr("groupId", getPara("groupId"));
		renderJsp("/view/chat/allGroupUser.jsp");
	}
	
	public void queryGroupUserByGroupId() {
		JSONObject obj = new JSONObject();
		Page<Record> pageRecord = Db.paginate(getParaToInt("page"), getParaToInt("rows"), "select u.* ", "from " + TableNameConstant.CHAT_GROUP_USER + " c left join " + TableNameConstant.CHAT_USERS + " u on c.userid = u.id where c.groupid = " + getParaToInt("groupid"));
		obj.put("total", pageRecord.getTotalRow());
		obj.put("rows", pageRecord.getList());
		renderJson(JsonKit.toJson(obj));
	}
	
	public void getGroupUserByGroupId() {
		User user=(User)getSession().getAttribute("user");
		renderJson(sv.getGroupUserByGroupId(getParaToInt("groupid"),user.getId()));
	}
	
	public void getFriendNotInGroup(){
		User user=(User)getSession().getAttribute("user");
		renderJson(sv.getFriendNotInGroup(getParaToInt("groupid"),user.getId()));
	}
	
	public void delGroupUserByUserId() {
		renderJson(Db.delete("delete from " + TableNameConstant.CHAT_GROUP_USER + " where userid = " + getParaToInt("userId")));
	}
	
	public void toInvitationUser() {
		setAttr("groupId", getPara("groupId"));
		renderJsp("/view/chat/invitationUser.jsp");
	}
	
	public void allInvitationUser() {
		JSONObject obj = new JSONObject();
		Page<Record> pageRecord = Db.paginate(getParaToInt("page"), getParaToInt("rows"), "select u.* ", "from " + TableNameConstant.CHAT_USERS + " u where u.deleted = 0 and  u.id  not in (select userid from " + TableNameConstant.CHAT_GROUP_USER + " where groupid = " + getParaToInt("groupId") + ")");
		obj.put("total", pageRecord.getTotalRow());
		obj.put("rows", pageRecord.getList());
		renderJson(JsonKit.toJson(obj));
	}
	
	public void createGroup(){
		User user=(User)getSession().getAttribute("user");
		try{
			renderJson(ResultUtil.success(sv.createGroup(user, getPara("groupname"), getPara("friendids"))));
		}
		catch (Exception e) {
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}
	
	public void saveOrUpdateGroup() {
		boolean state = false;
		User user=(User)getSession().getAttribute("user");
		Integer groupId = getParaToInt("groupId");
		System.out.println(groupId);
		ChatGroup group = new ChatGroup();
		if(groupId != null && groupId != 0) {
			group = ChatGroup.dao.findById(groupId);
			if(!StrKit.isBlank(getPara("avatar"))) {
				group.setAvatar(getPara("avatar"));
			}
			group.setGroupname(getPara("name"));
			state = group.update();
		}else {
			group.setAvatar(StrKit.isBlank(getPara("avatar")) ? "e5daae3f38fe4195b72af06740a47dcf_1560409357688.jpg" : getPara("avatar"));
			group.setGroupname(getPara("name"));
			group.setCreatorid(user.getId());
			group.setState(1);
			state = group.save();
		}
		
		File delFile = new File(PropKit.use("system.properties").get("tempdir") + System.getProperty("file.separator")+"groupAvatar\\" + user.getId());
		if(delFile.exists()) {
			//文件拷贝
			File file = new File(PropKit.use("system.properties").get("tempdir")+System.getProperty("file.separator")+"groupAvatar\\"+ user.getId() + "\\" + System.getProperty("file.separator")+ getPara("avatar"));
			File toFile = new File(PropKit.use("system.properties").get("sysdir") + "\\groupAvatar\\" + getPara("avatar") );
			try {
				FileUtils.copyFile(file, toFile);
				FileUtils.deleteDirectory(new File(PropKit.use("system.properties").get("tempdir") + System.getProperty("file.separator")+"groupAvatar\\" + user.getId()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		renderJson(state);
	}
	
	public void delGroup() {
//		ChatGroup group = ChatGroup.dao.findById(getParaToInt("groupId"));
//		group.setState(0);
//		renderJson(group.update());
		
		try{
			renderJson(ResultUtil.success(sv.delGroup(getParaToInt("groupid"), getPara("groupname"))));
		}
		catch (Exception e) {
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}
	
	public void saveGroup(){
		ChatGroup cg=getModel(ChatGroup.class,"",true);
		try{
			renderJson(ResultUtil.success(sv.saveGroup(cg)));
		}
		catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}
	public void addOrRemoveMember(){
		try{
			renderJson(ResultUtil.success(sv.addOrRemoveMember(getParaToInt("groupid"),getPara("groupname"),getParaToInt("userid"),getBoolean("checked"))));
		}
		catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}
}
