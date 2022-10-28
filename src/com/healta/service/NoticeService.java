package com.healta.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.healta.model.Notice;
import com.healta.model.NoticeRecord;
import com.healta.model.User;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class NoticeService {
	
	public List<User> getSendOrNoSendUserBySelectedUser(Integer type, String selectedUserIds){
		String sql = "select * from users where deleted = 0";
		if(StrKit.isBlank(selectedUserIds)) {
			return type == 1 ? User.dao.find(sql) : null;
		}else {
			sql += (type == 1 ? " and id not in (" : " and id in (");
			List<Object> list = new ArrayList<Object>(); 
			String[] userIdArray = selectedUserIds.split(",");
			for (int i = 0; i < userIdArray.length; i++) {
				if(i == userIdArray.length - 1) {
					sql += "?)";
				}else {
					sql += "?,";
				}
				list.add(userIdArray[i]);
			}
			return User.dao.find(sql, list.toArray());
		}
	}
	
	public Page<Record> queryNoticeByCondition(Integer pageNumber, Integer pageSize, String timeType, String type, String title, Integer userId, String beginTime, String endTime ) {
		String selectSql = "select * ";
		String sql = "from notice where creator = ? ";
		if(StrKit.isBlank(timeType) || timeType.equals("createtime")) {
			if(StrKit.notBlank(beginTime)) {
				sql += "and createtime >= '" + beginTime + "' ";
			}
			if(StrKit.notBlank(endTime)) {
				sql += "and createtime <= '" + endTime + " 23:59:59' ";
			}
		}else {
			if(StrKit.notBlank(beginTime)) {
				sql += "and releasetime >= '" + beginTime + "' ";
			}
			if(StrKit.notBlank(endTime)) {
				sql += "and releasetime <= '" + endTime + " 23:59:59' ";
			}
		}
		if(StrKit.notBlank(type)) {
			if(type.equals("issend")) {
				sql += "and releasedflag = 1 ";
			}else {
				sql += "and releasedflag = 0 ";
			}
		}
		if(StrKit.notBlank(title)) {
			sql += " and title like '%" + title + "%'";
		}
		return Db.paginate(pageNumber, pageSize, selectSql, sql, userId);
	}
	
	public Page<Record> queryUserNoticeByCondition(Integer pageNumber, Integer pageSize, String timeType, Integer type, String title, Integer userId, String beginTime, String endTime ) {
		String selectSql = "select * ";
		String sql = "from notice where releasedflag = 1 and id in(SELECT noticeid from notice_record where userid=?";
		if(type != null) {
			sql += " and isread = " + type + "GROUP BY noticeid) ";
		}else {
			sql += " GROUP BY noticeid) ";
		}
		
		if(StrKit.isBlank(timeType) || timeType.equals("createtime")) {
			if(StrKit.notBlank(beginTime)) {
				sql += "and createtime >= '" + beginTime + " 23:59:59' ";
			}
			if(StrKit.notBlank(endTime)) {
				sql += "and createtime <= '" + endTime + " 23:59:59' ";
			}
		}else {
			if(StrKit.notBlank(beginTime)) {
				sql += "and releasetime >= '" + beginTime + " 23:59:59' ";
			}
			if(StrKit.notBlank(endTime)) {
				sql += "and releasetime <= '" + endTime + " 23:59:59' ";
			}
		}
		if(StrKit.notBlank(title)) {
			sql += " and title like '%" + title + "%'";
		}
		System.out.println(sql);
		return Db.paginate(pageNumber, pageSize, selectSql, sql, userId);
	}
	
	
	public List<Integer> queryUserIdNotIn(List<User> userIdList1, List<User> userIdList2){
		List<Integer> userIdList = new ArrayList<Integer>();
		boolean state = false;
		for (User user1 : userIdList1) {
			for (User user2 : userIdList2) {
				if(user1.getId().equals(user2.getId())) {
					state = true;
					continue;
				}
			}
			if(!state) {
				userIdList.add(user1.getId());
			}
			state = false;
		}
		return userIdList;
	}
	
	public boolean delNoticeRecord(List<Integer> list, Integer noticeId) {
		for (Integer toid : list) {
			if(Db.delete("delete from notice_record where noticeid = ? and userid = ?", noticeId, toid) < 1) {
				return false;
			}
		}
		return true;
	}
	
	public List<NoticeRecord> getNoticeRecord(List<Integer> toidList, Integer noticeId, Integer fromId){
		List<NoticeRecord> list = new ArrayList<NoticeRecord>();
		for (Integer toid : toidList) {
			NoticeRecord noticeRecord = new NoticeRecord();
			noticeRecord.setNoticeid(noticeId);
			noticeRecord.setUserid(toid);
			list.add(noticeRecord);
		}
		return list;
	}
	
	
	public boolean updateNoticeReocrd(String selectedUserIds, Integer id) {
		List<User> newUserIdList = User.dao.find("select id from users where id in (" + selectedUserIds.substring(0, selectedUserIds.lastIndexOf(",")) + ")");
		List<User> oldUserIdList = User.dao.find("select id from users where id in (select userid from notice_record where noticeid = ?)", id);
		NoticeService sv = new NoticeService();
		List<Integer> saveUserId = sv.queryUserIdNotIn(newUserIdList, oldUserIdList);
		List<Integer> delUserId = sv.queryUserIdNotIn(oldUserIdList, newUserIdList);
		List<NoticeRecord> recordList = sv.getNoticeRecord(saveUserId, id, Notice.dao.findById(id).getCreator());
		if(!recordList.isEmpty() && recordList.size() > 0) {
			for (int i : Db.batchSave(recordList, recordList.size())) {
				if(i == 0) {
					System.out.println("save noticeRecord fail");
					return false;
				}
			}
		}
		if(!sv.delNoticeRecord(delUserId, id)) {
			System.out.println("delNoticeRecord fail");
			return false;
		}
		return true;
	}
	
	public static List<Record> queryNoSendNoticeByUserId(Integer userId){
		return Db.find("SELECT notice.id, notice.mustread from notice, notice_record where notice.id = notice_record.noticeid and userid = ?  and notice.releasedflag = 1 and notice_record.reminded is null", userId);
	}
	
	public static List<Integer> queryNoSendNoticeByNoticeId(Integer noticeId){
		return Db.query("select userid from notice_record where notice_record.noticeid = ? and notice_record.reminded is null ", noticeId);
	}
	
	public NoticeRecord queryNoticeRecord(Integer userId, Integer noticeId) {
		return NoticeRecord.dao.findFirst("select * from notice_record where userid = ? and noticeid = ?", userId, noticeId);
	}
	
	public NoticeRecord queryNoticeRecordWhereNoRead(Integer userId, Integer noticeId) {
		return NoticeRecord.dao.findFirst("select * from notice_record where noticeid = ? and userid = ? and isread = 0", noticeId, userId);
	}
}
