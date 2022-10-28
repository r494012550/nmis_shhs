package com.healta.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.healta.listener.MySessionContext;
import com.healta.model.DicDepartment;
import com.healta.model.DicInstitution;
import com.healta.model.Notice;
import com.healta.model.NoticeRecord;
import com.healta.model.User;
import com.healta.service.NoticeService;
import com.healta.util.FileUtil;
import com.healta.util.ResultUtil;
import com.healta.util.WebSocketUtils;
import com.healta.vo.WebsocketVO;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class NoticeController extends Controller{
	
	private final static Logger log = Logger.getLogger(NoticeController.class);
	public static NoticeService sv = new NoticeService();
	public static Integer id = null;
	
	public void toNoticeCenter() {
		renderJsp("/view/admin/notice/noticeCenter.jsp");
	}
	
	public void toEditNotice() {
		Integer noticeId = getParaToInt("noticeId");
		if(noticeId != null) {
			Notice notice = Notice.dao.findById(noticeId);
			setAttr("notice", notice);
			String[] fileNameArray = FileUtil.getFileNameByFolderPath(PropKit.get("base_upload_path") + PropKit.get("notice_file_upload_path") + notice.getFilepath());
			if(fileNameArray != null && fileNameArray.length > 0) {
				Record file = new Record();
				for(int i = 0 ; i < fileNameArray.length && i < 4; i++) {
					file.set("file" + (i+1), fileNameArray[i]);
				}
				setAttr("file", file);
			}
		}
		renderJsp("/view/admin/notice/editNotice.jsp");
	}
	
	public void saveNotice() {
		User user = (User) getSession().getAttribute("user");
		String path = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\\" + new Date().getTime();
		boolean succeed = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try {
					Notice notice = new Notice();
					getFile("file", PropKit.get("notice_file_upload_path") + path);
					File file = new File(PropKit.get("base_upload_path") + PropKit.get("notice_file_upload_path") + path);
					if(file.listFiles().length > 0) {
						notice.setFilepath(path);
					}else {
						file.delete();
					}
					notice.setContenttxt(getPara("contenttxt"));
					notice.setCreator(user.getId());
					notice.setTitle(getPara("title"));
					notice.setContenthtml(getPara("content"));
					notice.setMustread("on".equals(getPara("type")) ? 1 : 0);
					if(StrKit.notBlank(getPara("sendtime"))) {
						notice.setReleasetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(getPara("sendtime")+".000"));
					}
					if(notice.save()) {
						id = notice.getId();
						String userIdStr = getPara("selectedUserIds");
						if(!StrKit.isBlank(userIdStr)) {
							List<User> userList = User.dao.find("select id from users where id in (" + userIdStr.substring(0, userIdStr.lastIndexOf(",")) + ")");
							List<NoticeRecord> noticeRecordList = new ArrayList<NoticeRecord>();
							for (int i = 0; i < userList.size(); i++) {
								NoticeRecord noticeRecord = new NoticeRecord();
								noticeRecord.setNoticeid(notice.getId());
								noticeRecord.setUserid(userList.get(i).getId());
								noticeRecordList.add(noticeRecord);
							}
							int[] result  = Db.batchSave(noticeRecordList, noticeRecordList.size());
							for (int i : result) {
								if (i < 0 && i != Statement.SUCCESS_NO_INFO) {
									return false;
								}
							}
							return true;
						}
					}
					return false;
				}catch (Exception e) {
					e.printStackTrace();
					log.info("保存公告失败！");
					return false;
				}
			}
		});
		if(succeed) {
			renderJson(ResultUtil.success(id));
		}else {
			try {
				FileUtils.deleteDirectory(new File(PropKit.get("base_upload_path") + PropKit.get("notice_file_upload_path") + path));
			} catch (IOException e) {
				e.printStackTrace();
			}
			renderJson(ResultUtil.fail("失败！"));
		}
	}
	
	public void updateNotice() {
		String path = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\\" + new Date().getTime();
		boolean succeed = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try {
					getFile("file", PropKit.get("notice_file_upload_path") + path);
					Notice notice = Notice.dao.findById(getParaToInt("noticeId"));
					if(notice == null) {return false;}
					notice.setContenttxt(getPara("contenttxt"));
					notice.setTitle(getPara("title"));
					notice.setContenthtml(getPara("content"));
					notice.setMustread("on".equals(getPara("type")) ? 1 : 0);
					if(StrKit.notBlank(getPara("sendtime"))) {
						notice.setReleasetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(getPara("sendtime")+".000"));
					}
					if(notice.update() && StrKit.notBlank(getPara("selectedUserIds"))) {
						if(sv.updateNoticeReocrd(getPara("selectedUserIds"), notice.getId())) {
							if(StrKit.notBlank(notice.getFilepath())) {//修改前带有附件
								String delFile = getPara("delFile");
								if(StrKit.notBlank(delFile)) {	
									String[] delFileName = delFile.split(",");
									for (String fileName : delFileName) {
										if(!new File(PropKit.get("base_upload_path") + PropKit.get("notice_file_upload_path") + notice.getFilepath() + "\\"+ fileName).delete()) {
											return false;
										}
									}
								}
								
								FileUtils.copyDirectory(new File(PropKit.get("base_upload_path") + PropKit.get("notice_file_upload_path") + notice.getFilepath()),
										new File(PropKit.get("base_upload_path") + PropKit.get("notice_file_upload_path") + path));
								FileUtils.deleteDirectory(new File(PropKit.get("base_upload_path") + PropKit.get("notice_file_upload_path") + notice.getFilepath()));
							}
							notice.setFilepath(path);
							return notice.update();
						}
					}else {
						return false;
					}
					return false;
				} catch (Exception e) {
					e.printStackTrace();
					log.info("修改公告失败！");
					return false;
				}
			}
		});
		if(succeed) {
			renderJson(ResultUtil.success(getParaToInt("noticeId")));
		}else {
			try {
				FileUtils.deleteDirectory(new File(PropKit.get("base_upload_path") + PropKit.get("notice_file_upload_path") + path));
			} catch (IOException e) {
				e.printStackTrace();
			}
			renderJson(ResultUtil.fail("失败！"));
		}
	}
	
	public void getSendOrNoSendUserBySelectedUser() {
		Integer type = getParaToInt("type");
		String sql = "select * from users where deleted = 0";
		String selectedUserIds = getPara("selectedUserIds");
		if(StrKit.isBlank(selectedUserIds)) {
			renderJson(type == 1 ? User.dao.find(sql) : null);
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
			renderJson(User.dao.find(sql, list.toArray()));
		}
	}
	
	
//	public void getNoticeByCondition() {
//		User user = (User) getSession().getAttribute("user");
//		JSONObject json = new JSONObject();
//		Page<Record> pageRecordList = sv.queryNoticeByCondition(getParaToInt("page"), getParaToInt("rows"), getPara("type"),user.getId(), getPara("beginTime"), getPara("endTime"));
//		json.put("total",pageRecordList.getTotalRow());
//		json.put("rows", pageRecordList.getList());
//		renderJson(json);
//	}
	
	public void getNoticeByCondition() {
		User user = (User) getSession().getAttribute("user");
		JSONObject json = new JSONObject();
		Page<Record> pageRecordList = sv.queryNoticeByCondition(getParaToInt("page"), getParaToInt("rows"), getPara("timeType"), getPara("type"), getPara("title"), user.getId(), getPara("beginTime"),  getPara("endTime"));
		json.put("total",pageRecordList.getTotalRow());
		json.put("rows", pageRecordList.getList());
		renderJson(json);
	}
	
	public void getUserNoticeByCondition() {
		User user = (User) getSession().getAttribute("user");
		JSONObject json = new JSONObject();
		Page<Record> pageRecordList = sv.queryUserNoticeByCondition(getParaToInt("page"), getParaToInt("rows"), getPara("timeType"), getParaToInt("type"), getPara("title"), user.getId(), getPara("beginTime"),  getPara("endTime"));
		json.put("total",pageRecordList.getTotalRow());
		json.put("rows", pageRecordList.getList());
		renderJson(json);
	}
	
	public void getSendEmployeeJson() {
		JSONArray jsonArray = new JSONArray();
		JSONObject insJsonObj = null;
		JSONObject deptJsonObj = null;
		JSONObject empJsonObj = null;
		List<Integer> sendUserIdList = Db.query("select userid from notice_record where noticeid = ?", getParaToInt("noticeId"));
		String sendUserIdStr = "";
		for (Integer userid : sendUserIdList) {
			sendUserIdStr += userid + ",";
		}
		Map<Integer, List<DicDepartment>> dicDepartmentsMap = new HashMap<Integer, List<DicDepartment>>();
		List<DicDepartment> dicDepartmentList = DicDepartment.dao.find("select * from dic_department d  where  d.id in (select deptfk from dic_employee GROUP BY dic_employee.deptfk)");
		List<DicDepartment> dicDepartmentTempList = null;
		for (DicDepartment dicDepartment : dicDepartmentList) {
			if(dicDepartmentsMap.get(dicDepartment.getInstitutionId()) != null) {
				dicDepartmentTempList = dicDepartmentsMap.get(dicDepartment.getInstitutionId());
				dicDepartmentTempList.add(dicDepartment);
			}else {
				dicDepartmentTempList = new ArrayList<DicDepartment>();
				dicDepartmentTempList.add(dicDepartment);
			}
			dicDepartmentsMap.put(dicDepartment.getInstitutionId(), dicDepartmentTempList);
		}
		
		Map<Integer, List<Record>> recordMap = new HashMap<Integer, List<Record>>();
		List<Record> recordList = Db.find("select u.*, e.deptfk from users u, dic_employee e where u.employeefk = e.id and  u.employeefk in (select id from dic_employee)");
		List<Record> recordTempList = null;
		for (Record record : recordList) {
			if(recordMap.get(record.getInt("deptfk")) != null) {
				recordTempList = recordMap.get(record.getInt("deptfk"));
				recordTempList.add(record);
			}else {
				recordTempList = new ArrayList<Record>();
				recordTempList.add(record);
			}
			recordMap.put(record.getInt("deptfk"), recordTempList);
		}
		
		List<DicInstitution> dicInstitutionList = DicInstitution.dao.find("select * from dic_institution di  where  di.id in (select institutionid from dic_employee GROUP BY dic_employee.institutionid)");
		for (int i = 0; i < dicInstitutionList.size(); i++) {
			insJsonObj = new JSONObject();
			insJsonObj.put("id", "ins_" + dicInstitutionList.get(i).getId());
			insJsonObj.put("text", dicInstitutionList.get(i).getName());
			List<DicDepartment> dicDepartmentsList = dicDepartmentsMap.get(dicInstitutionList.get(i).getId());
			if(dicDepartmentsList.size() > 0) {
				JSONArray deptJsonArray = new JSONArray();
				for (int k = 0; k < dicDepartmentsList.size(); k++) {
					deptJsonObj = new JSONObject();
					deptJsonObj.put("id", "dept_" + dicDepartmentsList.get(k).getId());
					deptJsonObj.put("text", dicDepartmentsList.get(k).getDeptname());
					List<Record> list = recordMap.get(dicDepartmentsList.get(k).getId());
					if(list!=null&&list.size() > 0) {
						JSONArray empJsonArray = new JSONArray();
						for (int j = 0; j < list.size(); j++) {
							empJsonObj = new JSONObject();
							empJsonObj.put("id",+ list.get(j).getInt("id"));
							empJsonObj.put("text", list.get(j).get("name"));
							if(!StrKit.isBlank(sendUserIdStr) && sendUserIdStr.indexOf(String.valueOf(list.get(j).getInt("id"))) != -1) {
								empJsonObj.put("checked", true);
							}
							empJsonArray.set(j, empJsonObj);
						}
						deptJsonObj.put("children", empJsonArray);
					}
					deptJsonArray.set(k, deptJsonObj);
				}
				insJsonObj.put("children", deptJsonArray);
			}
			jsonArray.set(i, insJsonObj);
		}
		Object [] a = new Object[1];
		JSONObject all = new JSONObject();
		all.put("id", "all");
		all.put("text", "所有");
		all.put("children", jsonArray);
		a[0] = all;
		renderJson(a);
	}
	
	public void delNotice() {
		boolean succeed = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				Notice notice = Notice.dao.findById(getParaToInt("id"));
				String filePath = notice.getFilepath();
				if(Notice.dao.deleteById(getParaToInt("id"))) {
					try {
						FileUtils.deleteDirectory(new File(PropKit.get("base_upload_path") + PropKit.get("notice_file_upload_path") + filePath));
					} catch (IOException e) {
						e.printStackTrace();
					}
//					FileUtil.delFolder(PropKit.get("base_upload_path") + PropKit.get("notice_file_upload_path") + filePath);
					Integer a = Db.delete("delete from notice_record where noticeid = ? " , getParaToInt("id"));
					return a > 0 ? true : false;
				}
				return false;
			}
		});
		System.out.println(succeed);
		if(succeed) {
			renderJson(ResultUtil.success(succeed));
		}else {
			renderJson(ResultUtil.fail("失败！"));
		}
	}
	
	public void userNoticeCenter() {
		setAttr("noticeId", getPara("noticeId"));
		renderJsp("/view/admin/notice/userNoticeCenter.jsp");
	}

	public void showNotice() {
		if(!getPara("noticeId").equals("undefined")) {
			Notice notice = Notice.dao.findById(getPara("noticeId"));
			if(notice.getReleasedflag() == 1) {
				User user = (User) getSession().getAttribute("user");
				NoticeRecord noticeRecord = sv.queryNoticeRecordWhereNoRead(user.getId(), notice.getId());
				if(noticeRecord != null) {
					noticeRecord.setIsread(1);
					noticeRecord.update();
				}
			} 
			setAttr("notice", notice);
			String[] fileNameArray = FileUtil.getFileNameByFolderPath(PropKit.get("base_upload_path") + PropKit.get("notice_file_upload_path") + notice.getFilepath());
			if(fileNameArray != null && fileNameArray.length > 0) {
				String files = "";
				for(int i = 0 ; i < fileNameArray.length && i < 4; i++) {
					if(i == fileNameArray.length-1) {
						files += fileNameArray[i];
					}else {
						files += (fileNameArray[i] + ",");
					}
				}
				setAttr("files", files);
			}
			renderJsp("/view/admin/notice/showNotice.jsp");
		}else {
			renderJsp("/view/admin/notice/noNotice.jsp");
		}
		
		
	}
	
	public void sendNotice() {
		Notice notice = Notice.dao.findById(getPara("noticeId"));
		notice.setReleasedflag(1);
		if(notice.update()) {
			Integer noticeId = getParaToInt("noticeId");
				List<Integer> sendUserID = NoticeService.queryNoSendNoticeByNoticeId(noticeId);
				for (Integer id : sendUserID) {
					if(StrKit.notBlank(MySessionContext.getSessionId(id))) {
						WebSocketUtils.sendMessage(id, new WebsocketVO(WebsocketVO.NOTICE,noticeId + "," + notice.getMustread() + ",1").toJson());
					}
				}
			renderJson(ResultUtil.success());
		}else{
			renderJson(ResultUtil.fail("失败！"));
		}
	}
	
	public void downloadFile() {
		Notice notice = Notice.dao.findById(getParaToInt("noticeId"));
		renderFile(new File(PropKit.get("base_upload_path") + PropKit.get("notice_file_upload_path") + notice.getFilepath() +"\\" + getPara("fileName")));
	}
	
	public void remindNotice() {
		User user = (User) getSession().getAttribute("user");
		NoticeRecord noticeRecord = sv.queryNoticeRecord(user.getId(), getParaToInt("noticeId"));
		noticeRecord.setReminded(1);
		noticeRecord.update();
		renderNull();
	}
}
