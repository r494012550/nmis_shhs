package com.healta.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.healta.model.Filter;
import com.healta.model.StudyImage;
import com.healta.model.Studyprocess;
import com.healta.model.User;
import com.healta.model.UserProfiles;
import com.healta.util.SyscodeKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class FrontCommonService {
	private static final Logger log = Logger.getLogger(FrontCommonService.class);
	
	public StudyImage getApplyImage(Integer orderid){
		return StudyImage.dao.findFirst("select top 1 * from study_image where orderid=?",orderid);
	}
	
	public Record findStudyInfo(Integer orderid){
		return Db.findFirst("select top 1 patient.patientname as patientname,studyorder.studyid as studyid,studyorder.studyitems as studyitems from patient,studyorder where patient.id=studyorder.patientidfk and studyorder.id=?",orderid);
	}
	
	public List<Record> findStudyProcess(Integer orderid,String locale){
		List<Record> list=Db.find("select * from studyprocess where studyorderfk=? order by operatetime asc",orderid);
		list.forEach(sp->{
			sp.set("statusdisplay", SyscodeKit.INSTANCE.getCodeDisplay("0023", sp.getStr("status"), locale));
		});
		return list;
	}
	
	public boolean deleteAppScanImg(Integer orderid ,Integer index){
		
		StudyImage si=StudyImage.dao.findFirst("select top 1 * from study_image where orderid=?",orderid);

		int n=Db.update("update study_image set img"+index+" = null where orderid=?",orderid);
		if(n==1){
			boolean flag=true;
			for(int i=1;i<11;i++){
				if(StrKit.notBlank(si.getStr("img"+i))&&i!=index){
					flag=false;
					break;
				}
			}
			
			if(flag){
				si.delete();
			}
			
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	*  校验 我的条件 保存的命名是否已经存在
	* @param userId 登入用户的id
	* @param filterType 保存我的条件的所属模板
	* @param name 保存条件的命名
	* @return
	*/
	public Boolean checkMyConditionName(Integer userId, String filterType, String name) {
		Boolean res = true;
		Record filter = Db.findFirst("select id from filter where creator = ? and filter_type = ? and description = ? ",
	          userId, filterType, name);
		if (filter != null) {
	      // 此名称已存在
	      res = false;
		}
		return res;
	}
	
	/**
	 * 保存查询条件
	 */
	public boolean saveFilter(Filter record,User user,Map<String , String[]> map) throws Exception {
		map.forEach((k, v) -> {
			if(v!=null&&v.length>1) {
				/*if(k=="studyid") {
					record.setStudyId(StringUtils.join(v, ","));
				}else if(k=="patientname") {
					record.setPatientName(StringUtils.join(v, ","));
				}else if(k=="datetype") {
					record.setTimeType(StringUtils.join(v, ","));
				}else if(k=="datefrom") {
					record.setInno(StringUtils.join(v, ","));
				}else if(k=="dateto") {
					record.setOutno(StringUtils.join(v, ","));
				}else if(k=="startfromtime") {
					record.setStartTime(StringUtils.join(v, ","));
				}else if(k=="endtotime") {
					record.setStopTime(StringUtils.join(v, ","));
				}*/
				record.set(k, StringUtils.join(v, ","));
			}
		});
		record.setCreator(String.valueOf(user.getId()));// 创建人id
		return record.save();
	}
	
	/**
	 * 删除保存的查询条件
	 */
	public boolean deleteFilters(String ids) {
		boolean ret=true;
		try{
			String[] idArry = ids.split(",");
			for (int i = 0; i < idArry.length; i++) {
				Filter.dao.deleteById(idArry[i]);
			}
		}
		catch (Exception e) {
			ret=false;
		}
		return ret;
	}
	
	//保存查询结果列布局
		public boolean saveDatagridColumn(HashMap<String, String> map, User user) {
			boolean ret = true;
			String mapStr = JSON.toJSONString(map);
			UserProfiles profiles = UserProfiles.dao.findFirst("SELECT * FROM userprofiles WHERE userid = ?", user.getId());
			if(profiles == null) {
				profiles = new UserProfiles();
				profiles.setUserid(user.getId());
			}
			String field = "datagrid_" + map.get("moudle") + "_" + map.get("targetid");
			profiles.set(field, mapStr);
			if(profiles.getId() == null) {
				profiles.remove("id").save();
			}else {
				profiles.update();
			}
			return ret;
		}
		
		//保存datagrid页面尺寸
		public boolean saveDatagridPagination(HashMap<String, String> map, User user) {
			boolean ret = true;
			UserProfiles profiles = UserProfiles.dao.findFirst("SELECT * FROM userprofiles WHERE userid = ?", user.getId());
			if(profiles == null) {
				profiles = new UserProfiles();
				profiles.setUserid(user.getId());
			}
			String field = "pagination_" + map.get("moudle") + "_" + map.get("targetid");
			profiles.set(field, map.get("thisPageSize"));
			if(profiles.getId() == null) {
				profiles.remove("id").save();
			}else {
				profiles.update();
			}
			return ret;
		}

		/**
		 *  初始化默认的排序列表
		 * @param moudle
		 * @param userid
		 * @return
		 */
		public Boolean restoreDefaults(String moudle, Integer userid) {
			Boolean res = false;
			UserProfiles userProfiles = UserProfiles.dao.findFirst("select * from userprofiles where userid = ?", userid);
			if (userProfiles != null) {
				if ("worklist".equals(moudle)) {
					userProfiles.setDatagridWorklistDg("");
				} else if ("register".equals(moudle)) {
					userProfiles.setDatagridRegisterDg1Reg("");
				} else if ("schedule".equals(moudle)) {
					userProfiles.setDatagridScheduleDgSearchSch("");
				} else if ("examine".equals(moudle)) {
					userProfiles.setDatagridExamineSearchdgExam(moudle);
				}
				res = userProfiles.update();
			}
			return res;
		}
}
