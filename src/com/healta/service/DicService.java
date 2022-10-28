package com.healta.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.healta.constant.CacheName;
import com.healta.model.Client;
import com.healta.model.DeptPostShift;
import com.healta.model.DicCalling;
import com.healta.model.DicCommon;
import com.healta.model.DicDepartment;
import com.healta.model.DicEmployee;
import com.healta.model.DicEquipGroup;
import com.healta.model.DicExamEquip;
import com.healta.model.DicExamitem;
import com.healta.model.DicInstitution;
import com.healta.model.DicInstitutionRole;
import com.healta.model.DicModality;
import com.healta.model.DicOrgan;
import com.healta.model.Role;
import com.healta.model.User;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;


public class DicService {
	private final static Logger log = Logger.getLogger(DicService.class);
	
	/**
	 * 查询设备
	 * @param value
	 * @param type
	 * @param deleted
	 * @return
	 */
	public List<DicModality> findDic(String value,String type,Integer institutionid,Integer departmentid,Integer examId,String deleted) {
		String where="";
		List<Object> list=new ArrayList<Object>();
		if(StringUtils.isNotBlank(value)){
			where += " and modality_name like CONCAT('%',?,'%')";
			list.add(value);
		}
		if(StringUtils.isNotBlank(type)) {
			where += " and type =?" ;
			list.add(type);
		}
		if(institutionid!=null){
			where += " and institutionid = ?";
			list.add(institutionid);
		}
		if(departmentid!=null){
			where += " and departmentid = ?";
			list.add(departmentid);
		}
		if(examId!=null){
			where += " and id not in(select dic_exam_equip.equip_id from dic_exam_equip where dic_exam_equip.exam_id = ?)";
			list.add(examId);
		}
		if(StringUtils.isNotBlank(deleted)) {
			where += " and deleted =?";
			list.add(deleted);
		}
		String sql="select * ,(select name_zh from syscode where syscode.code=dic_modality.role and type='0017') as rolename,"
				+ "(select gpname from dic_equip_group where dic_equip_group.id= dic_modality.groupid)as groupname"
				+ " from dic_modality where 0=0"+where;
		return DicModality.dao.find(sql,list.toArray());
	}
	
	/**
	 * 查询组中设备
	 * @param value
	 * @return
	 */
	public List<DicModality> findDicByGroup(Integer groupid ,String syscode_lan) {
		String sql="select * ,(select gpname from dic_equip_group where dic_equip_group.id= dic_modality.groupid) as groupname,"
				+ "(select "+syscode_lan+" from syscode where syscode.code=dic_modality.role and syscode.type='0017') as roledisplayname from dic_modality where groupid =? and deleted=0";
		return DicModality.dao.find(sql,groupid);
	}
	
	/**
	 * 查询检查项目对应的设备
	 * @param value
	 * @return
	 */
	public List<DicModality> findModalityByExamItem(Integer examid,String syscode_lan) {
		return DicModality.dao.find("select dic_modality.*,"
				+ "(select "+syscode_lan+" from syscode where syscode.code=dic_modality.role and syscode.type='0017') as roledisplayname "
						+ "from dic_modality ,dic_exam_equip where dic_modality.id=dic_exam_equip.equip_id and "
						+ "dic_exam_equip.exam_id=? and dic_modality.deleted=0",examid);
	}
	
	/**
	 * 保存设备		ret: 1保存成功； 2设备名或AET被占用
	 * @param dic
	 * @return
	 */
	public String saveDic(DicModality dic) {
		HashMap<String, String> map=new HashMap<String, String>();
		map.put("ret", "1");
		boolean ret=false;
		List<Object> list=new ArrayList<Object>();
		list.add(dic.getModalityName());
		list.add(dic.getStr("storagescu"));
		list.add(dic.getStr("storagescp"));
		list.add(dic.getStr("storagecmtscu"));
		list.add(dic.getStr("storagecmtscp"));
		list.add(dic.getStr("worklistscu"));
		list.add(dic.getStr("worklistscp"));
		list.add(dic.getStr("qrscu"));
		list.add(dic.getStr("qrscp"));
		list.add(dic.getStr("printscu"));
		list.add(dic.getStr("printscp"));
		String sql="select * from dic_modality where (modality_name=?"
		+" or storagescu=? or storagescp=?"
		+" or storagecmtscu=? or storagecmtscp=?"
		+" or worklistscu=? or worklistscp=?"
		+" or qrscu=? or qrscp=?"
		+" or printscu=? or printscp=?) and deleted=0";
		if(dic.getId()!=null) {
			sql+=" and id !="+dic.getId();
			if(DicModality.dao.find(sql,list.toArray()).size()!=0) {
				map.put("ret", "2");
				return map.get("ret");
			}
			else {
				ret=dic.update();
			}
		}
		else {
			if(DicModality.dao.find(sql,list.toArray()).size()!=0) {
				map.put("ret", "2");
				return map.get("ret");
			}else {
				dic.setDeleted("0");
				ret=dic.remove("id").save();
			}
		}
		if(ret) {
			CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_MODALITY_KEY);
		}
		return map.get("ret");
	}
	
	/**
	 * 删除设备前判断
	 * @param id
	 * @return
	 */
	public HashMap<String, Object> beforeDeleteDic(Integer id) {
		HashMap<String, Object> map=new HashMap<String, Object>();
		List<Client> client = Client.dao.find("SELECT * FROM client where modalityid=?",id);
		map.put("client", client.size());
		List<DicExamEquip> dicExamEquip = DicExamEquip.dao.find("SELECT * FROM dic_exam_equip WHERE equip_id=?",id);
		map.put("dic_exam_equip",dicExamEquip.size());
		DicModality dicModality = DicModality.dao.findById(id);
		String groupname = "";
		if(dicModality!=null) {
			DicEquipGroup group = DicEquipGroup.dao.findFirst("SELECT TOP 1 * FROM dic_equip_group WHERE id=? AND deleted=0",dicModality.getGroupid());
			if(group!=null) {
				groupname = group.getGpname();
			}
		}
		map.put("dic_modality",groupname);
		return map;
	}
	
	/**
	 * 删除设备
	 * @param id
	 * @return
	 */
	public boolean deleteDicModality(Integer id) {
		boolean succeed=Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			try {
/*					Db.update("DELETE FROM dic_exam_equip WHERE equip_id = ?",id);
					Db.update("DELETE FROM client WHERE modalityid = ?",id);*/		
				Db.update("UPDATE dic_modality SET deleted=1 WHERE id = ?",id);
			}catch(Exception e) {
				e.printStackTrace();
				return false;
			}
			return ret;
		});
		if(succeed) {
			CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_MODALITY_KEY);
		}
		return succeed;
	}
	
	public List<Record> getExamitemEquipInfo(String type,String deleted,Integer modalityId) {
		String where = "";
		List<Object> list=new ArrayList<Object>();
	
		if(StringUtils.isNotBlank(type)) {
			where += " and dic_examitem.type = ?";
			list.add(type);
		}
		if(StringUtils.isNotBlank(deleted)) {
			where += " and dic_examitem.deleted =?";
			list.add(deleted);
		}
		String sql1="select dic_examitem.*,(select treename_zh from dic_organ where dic_organ.id = dic_examitem.organfk ) as organ,"
				+ " (select treename_zh from dic_organ where dic_organ.id=dic_examitem.suborganfk )as suborgan,"
				+ " (case when dic_exam_equip.id is NULL then '0' else '1' end) as ck"
				+ " from dic_examitem"
				+ " left join dic_exam_equip on dic_examitem.id=dic_exam_equip.exam_id and dic_exam_equip.equip_id="+modalityId+" where 0=0"+where;
		
		List<Record> result = Db.find(sql1,list.toArray());

		return result;
	}
	
	public boolean saveExamitemEquipInfo(Integer modalityid, String examIds) {
		boolean succeed=Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			Db.update("delete from dic_exam_equip where equip_id="+modalityid);
			if(StringUtils.isNotBlank(examIds)) {
				String examitems = examIds.substring(0, examIds.length()-1);
				String ids[]=examitems.split(",");
				for(String id:ids){
					if(StringUtils.isNotBlank(id)){
						DicExamEquip dic=new DicExamEquip();
						dic.set("exam_id",id);
						dic.set("equip_id", modalityid);
						dic.save();
					}
				}
			}	
			return ret;
		});
		
		if(succeed) {
			CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_EXAMEQUIPMAP_KEY);
		}
		return succeed;
	}
	
	/**
	 * 查找检查项目
	 * @param value
	 * @param modalityid
	 * @param organfk
	 * @param suborganfk
	 * @param deleted
	 * @return
	 */
	public List<DicExamitem> findExamitemdic(String value,String type,Integer modalityid,Integer organfk,Integer suborganfk,String deleted) {
		String where = "";
		List<Object> list=new ArrayList<Object>();
	
		if(StringUtils.isNotBlank(value)){
			where += " and item_name like CONCAT('%',?,'%')";
			list.add(value);			
		}
		if(StringUtils.isNotBlank(type)) {
			where += " and type = ?";
			list.add(type);
		}
		if(modalityid!=null) {
			List<DicExamEquip> dicExamEquips = DicExamEquip.dao.find("SELECT * FROM dic_exam_equip WHERE equip_id = ?",modalityid);
			
			String ids="";
			for(DicExamEquip dic:dicExamEquips) {
				ids += dic.getExamId()+",";
			}
			
			if(StringUtils.isBlank(ids)) {
				where += " and id = 0";
			}else {
				where += " and id in ("+ids.substring(0,ids.length()-1)+")";
			}
			
		}
		if(organfk!=null) {
			where += " and organfk =?";
			list.add(organfk);
		}
		if(suborganfk!=null) {
			where += " and suborganfk =?";
			list.add(suborganfk);
		}
		if(StringUtils.isNotBlank(deleted)) {
			where += " and deleted =?";
			list.add(deleted);
		}
		String sql="select *,(select treename_zh from dic_organ where dic_organ.id= dic_examitem.organfk ) as organ,"
				+ " (select treename_zh from dic_organ where dic_organ.id=dic_examitem.suborganfk )as suborgan"
				+ " from dic_examitem where 0=0"+where;
		
		return DicExamitem.dao.find(sql,list.toArray());
		
	}
	
	/**
	 * 保存检查项目
	 * @param examitemdic
	 * @param equipments
	 * @return
	 */
	public boolean saveExamitem(DicExamitem examitemdic,String equipments) {
		boolean succeed=Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			String sql="select * from dic_examitem";
			if(examitemdic.getId()!=null) {
				ret=examitemdic.update();
			}
			else {
				examitemdic.setDeleted("0");
				ret=examitemdic.remove("id").save();
			}
			Db.update("delete from dic_exam_equip where exam_id = ?",examitemdic.getId());
			
			if(StringUtils.isNotBlank(equipments)) {
				String[] equip= equipments.split(",");
				for(String s:equip) {
//						System.out.println(s);
					new DicExamEquip().set("exam_id", examitemdic.getId()).set("equip_id", s).save();
				}
			}
			return ret;
		});
		
		if(succeed) {
			CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_EXAMITEM_KEY);
			CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_EXAMEQUIPMAP_KEY);
		}
		return succeed;
	}
	
	/**
	 * 删除检查项目
	 * @param examitemid
	 * @return
	 */
	public boolean deleteExamitem(Integer examitemid) {
		boolean succeed=Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			try {
				Db.update("UPDATE dic_examitem SET deleted=1 WHERE id = ?",examitemid);
				Db.update("DELETE FROM dic_exam_equip WHERE exam_id = ?",examitemid);
			}catch(Exception e) {
				e.printStackTrace();
				return false;
			}
			return ret;
		});
		
		if(succeed) {
			CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_EXAMITEM_KEY);
			CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_EXAMEQUIPMAP_KEY);
		}
		return succeed;	
	}
	
	/**
	 * 查询设备组
	 * @param value
	 * @param modalityid
	 * @param deleted
	 * @return
	 */
	public List<DicEquipGroup> findGroup(String value,Integer modalityid,String deleted) {
		String where = "";
		List<Object> list=new ArrayList<Object>();
	
		if(StringUtils.isNotBlank(value)){
			where += " and gpname like CONCAT('%',?,'%')";
			list.add(value);			
		}
		if(modalityid!=null) {
			DicModality dicModality = DicModality.dao.findById(modalityid);
			where += " and id = ?";
			list.add(dicModality.getGroupid());
		}
		if(StringUtils.isNotBlank(deleted)) {
			where += " and deleted =?";
			list.add(deleted);
		}
		String sql="select * from dic_equip_group where 0=0"+where;
		return DicEquipGroup.dao.find(sql,list.toArray());
	}
	
	/**
	 * 保存设备组
	 * @param group
	 * @param modality
	 * @return
	 */
	public boolean saveGroup(DicEquipGroup group,Integer[] modality) {
		Boolean succeed=Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=false;
			if(group.getId()!=null) {
				ret= group.update();
			}
			else{
				group.setDeleted("0");
				ret=group.remove("id").save();
			}
				
			if(modality!=null) {
				String ids="";
				for(Integer modalityid:modality) {
					ids+=modalityid+",";
				}
				ids=ids.substring(0,ids.length()-1);
				
				Db.update("UPDATE dic_modality SET groupid=? WHERE id IN ("+ids+")",group.getId());
				Db.update("UPDATE dic_modality SET groupid=null WHERE id NOT IN ("+ids+") AND groupid=?",group.getId());
			}else {
				Db.update("UPDATE dic_modality SET groupid=null WHERE groupid=?",group.getId());
			}
		
			return ret;
		});
		return succeed;
	}
	
	/**
	 * 删除设备组
	 * @param id
	 * @return
	 */
	public boolean deleteGroup(Integer id) {
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret = true;
			Db.update("UPDATE dic_equip_group SET deleted=1 WHERE id = ?",id);
			Db.update("UPDATE dic_modality SET groupid=null WHERE groupid=?",id);
			return ret;
		});
	}
	
	public boolean checkGroupName(String name,Integer id){		
		boolean ret = true;
		String sql="select * from dic_equip_group where gpname = ? and deleted = 0";
		if(id!=null) {
			sql+=" and id != "+id;
			if(DicEquipGroup.dao.find(sql,name).size()!=0){
				return false;
			}						
		}
		else {
			if(DicEquipGroup.dao.find(sql,name).size()!=0){
				return false;
			}	
		}
		return ret;
	}
	
//查询部位字典	
	public JSONArray findOrgandic(String modalitytype,String deleted){
		String sql1="select * from dic_organ where modality=?";
		String sql2="select * from dic_organ where modality=? and parentid=?";
		if(StringUtils.isNotBlank(deleted)) {
			sql1+=" and deleted = "+deleted;
			sql2+=" and deleted = "+deleted;
		}
		JSONArray array=new JSONArray();
		List<DicOrgan> organdics=DicOrgan.dao.find(sql1,modalitytype);
		DicOrgan.dao.find(sql2,modalitytype,0).stream().forEach(x->array.add(setJSON(x)));
		recursionOrgandic(organdics,array);
		return array;	
	}
//递归查询部位
	public void recursionOrgandic(List<DicOrgan> organdics,JSONArray parent){
		for (int i=0,len=parent.size();i<len;i++) {
			JSONObject object=parent.getJSONObject(i);
			JSONArray array=new JSONArray();
			for(DicOrgan organdic:organdics) {
				if(object.getIntValue("id")==organdic.getParentid()) {
					array.add(setJSON(organdic));
				}
			}
			if(array.size()>0) {
				recursionOrgandic(organdics,array);
				object.put("children", array);
				object.put("state","closed");
			}
		}
	}
//设置JSON格式部位
	public JSONObject setJSON(DicOrgan organdic) {
		return (JSONObject)JSON.toJSON(organdic);
//		JSONObject jo=new JSONObject();
//		jo.put("id", organdic.getId());
//	    jo.put("treename_zh", organdic.getTreenameZh());
//	    jo.put("treename_en", organdic.getTreenameEn());
//	    jo.put("typecode", organdic.getTypecode());
//	    jo.put("parentid",organdic.getParentid());
//	    jo.put("levelnum",organdic.getLevelnum());
//		return jo;
	}
	
	/**
	 * 删除设备前判断
	 * @param id
	 * @return
	 */
	public HashMap<String, Object> beforeDeleteOrgan(Integer id) {
		HashMap<String, Object> map=new HashMap<String, Object>();
		List<DicExamitem> dicExamitem = DicExamitem.dao.find("SELECT * FROM dic_examitem WHERE organfk=? OR suborganfk =?",id,id);
		map.put("dic_examitem",dicExamitem.size());
		return map;
	}
	
	/**
	 * 删除部位字典
	 * @param id
	 * @return
	 */
	public boolean deleteOrgandic(Integer id) {
		boolean succeed=Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			try {
				List<DicOrgan> organdics=DicOrgan.dao.find("select * from dic_organ"  );
				List<DicOrgan> parentList=DicOrgan.dao.find("select * from dic_organ where id=?",id);
				recursionDelete(organdics,parentList);
			}catch(Exception e) {
				e.printStackTrace();
				return false;
			}
			int count = Db.update("UPDATE dic_organ SET deleted=1 WHERE id = ?",id);
			return ret&&count==1;
		});
		
		if(succeed) {
			CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_ORGAN_KEY);
		}
		return succeed;
	}
//递归删除	
	public void recursionDelete(List<DicOrgan> organdics,List<DicOrgan> parentList) {
		for(DicOrgan parent:parentList) {
			List<DicOrgan> childrenList=new ArrayList<DicOrgan>();
			for(DicOrgan organdic:organdics) {
				if(parent.getId()==organdic.getParentid()) {
					childrenList.add(organdic);
					Db.update("UPDATE dic_organ SET deleted=1 WHERE id = ?",organdic.getId());
//					DicOrgan.dao.deleteById(organdic.getId());
				}
			}
			if(childrenList.size()>0) {
				recursionDelete(organdics,childrenList);
			}	
		}	
	}
	
	/**
	 * 保存部位
	 * @param organ
	 * @return
	 */
	public boolean saveOrgandic(DicOrgan organ) {
		boolean ret=false;
		if(organ.getId()!=null){
			ret=organ.update();	
		}
		else{
			organ.setDeleted("0");
			ret=organ.remove("id").save();		
		}
		if(ret) {
			CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_ORGAN_KEY);
		}
		return ret;
	}
/*
 * 科室管理
 * 
 */	
	public List<DicDepartment> findDepartment(String value,Integer institutionid,String deleted) {
		String where = "";
		List<Object> list=new ArrayList<Object>();
	
		if(StringUtils.isNotBlank(value)){
			where += " and deptname like CONCAT('%',?,'%')";
			list.add(value);			
		}
		if(institutionid!=null) {
		    where += " and institutionid = ?";
		    list.add(institutionid);
		}
		if(StringUtils.isNotBlank(deleted)) {
			where += " and deleted =?";
			list.add(deleted);
		}
		String sql="select *,"
				+ " (select name from dic_institution where dic_institution.id = dic_department.institutionid)as institutionname,"
				+ " (select name_zh from syscode where syscode.code= dic_department.type and syscode.type='0002')as type"
				+ " from dic_department where 0=0"+where;

		return DicDepartment.dao.find(sql,list.toArray());
	}
	
	/**
	 * 保存科室部门
	 * @param department
	 * @return
	 */
	public boolean saveDepartment(DicDepartment department,String shiftids) {
		
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			boolean ret=true;
			if(department.getId()!=null){
				ret=ret&department.update();
				Db.delete("delete from dept_shift where id=?",department.getId());
			}
			else{
				department.setDeleted("0");
				ret=ret&department.remove("id").save();		
			}
			
			if(StrKit.notBlank(shiftids)){
				String ids[]=shiftids.split(",");
				List<DeptPostShift> list=new ArrayList<DeptPostShift>();
				for(String id:ids){
					DeptPostShift dds=new DeptPostShift();
					dds.setDeptid(department.getId());
					dds.setShiftid(Integer.valueOf(id));
					list.add(dds);
				}
				Db.batchSave(list, list.size());
			}
			if(ret) {
				CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_DEPARTMENT_KEY);
			}
			return ret;
		});

	}
	
	/**
	 * 删除科室部门前判断
	 * @param id
	 * @return
	 */
	public HashMap<String, Object> beforeDeleteDepartment(Integer id) {
		HashMap<String, Object> map=new HashMap<String, Object>();
		List<DicModality> dicModality = DicModality.dao.find("SELECT * FROM dic_modality WHERE departmentid=?",id);
		map.put("dic_modality", dicModality.size());
		List<DicEmployee> employee = DicEmployee.dao.find("SELECT * FROM dic_employee WHERE deptfk=?",id);
		map.put("dic_employee",employee.size());
		return map;
	}
	
	/**
	 * 删除科室部门
	 * @param id
	 * @return
	 */
	public boolean deleteDepartment(Integer id) {
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			boolean ret=true;
			int count = Db.update("UPDATE dic_department SET deleted=1 WHERE id = ?",id);
			Db.delete("delete from dept_post_shift where deptid=?",id);
			if(count==1) {
				CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_DEPARTMENT_KEY);
			}
			return ret&&count==1;
		});
	}

	
	/**
	 * (根据员工姓名或科室或职务)获取员工
	 */
	public List<DicEmployee> getEmployee(Integer institutionid,String name,Integer dept,String profession, String deleted) {
		String where = "";
		List<Object> list = new ArrayList<Object>();
		if (institutionid != null) {
            where += " AND institutionid = ?";
            list.add(institutionid);
        }
		if(StringUtils.isNotBlank(name)){
			where += " AND name LIKE CONCAT('%',?,'%')";
			list.add(name);
		}
		if(dept!=null) {
			where += " AND deptfk = ?";
			list.add(dept);
		}
		if(StringUtils.isNotBlank(profession)) {
			where += " AND profession = ?";
			list.add(profession);
		}
		if (StringUtils.isNotBlank(deleted)) {
            where += " AND deleted = ?";
            list.add(deleted);
        }
		where += " order by dic_employee.institutionid,dic_employee.deptfk ";
		String sql="SELECT * ,"
		        + "(select i.name from dic_institution as i where i.id = dic_employee.institutionid) as institutionname,"
		        + "(select name_zh from syscode where syscode.code= dic_employee.profession and syscode.type='0019')as profession"
				+ " FROM dic_employee WHERE 0=0"+where;
		Object[] array = list.toArray();
		return DicEmployee.dao.find(sql,array);
	}
	
	//保存或修改员工信息
	public boolean saveEmployee(DicEmployee employee) {
		boolean ret=false;
		if(employee.getId()!=null){
			ret=employee.update();
			User user=User.dao.findFirst("SELECT * FROM users WHERE employeefk = ?",employee.getId());
			//更新user表中姓名
			if(user!=null) {
				user.setName(employee.getName());
				ret=ret&&user.update();
			}
		}
		else{
		    employee.setDeleted("0");
			ret=employee.remove("id").save();		
		}
		if(ret) {
			CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_EMPLOYEE_KEY);
		}
		return ret;
	}
	
	//删除员工信息
	public boolean deleteEmployee(Integer id) {
		boolean succeed=Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			try {
				Db.update("update users set active = '0', deleted = '1' WHERE employeefk = ?",id);
			}catch(Exception e) {
				e.printStackTrace();
				return false;
			}
			// 逻辑删除，1代表删除，0代表未删除
			ret=ret&&DicEmployee.dao.findById(id).set("deleted","1").update();
			return ret;
		});
		
		if(succeed) {
			CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_EMPLOYEE_KEY);
		}
		return succeed;
	}
	
	/**
	  *  查找机构管理
	 * @param value
	 * @return
	 */
	public List<DicInstitution> getInstitution(String value,String deleted) {
		String where = "";
		List<Object> list = new ArrayList<Object>();
		if(StringUtils.isNotBlank(value)){
			where += " AND name LIKE CONCAT('%',?,'%')";
			list.add(value);
		}
		
		if(StringUtils.isNotBlank(deleted)) {
			where += " AND deleted = ?";
			list.add(deleted);
		}
        String sql = "select * from dic_institution where 0=0"+where;
     
        return DicInstitution.dao.find(sql,list.toArray());
    }

	/**
	 *  查找机构管理
	 * @param value
	 * @return
	 */
	public List<DicInstitution> getInstitution(String value) {
		String where = "";
		List<Object> list = new ArrayList<Object>();
		if (StringUtils.isNotBlank(value)) {
			where += " AND name LIKE CONCAT('%',?,'%')";
			list.add(value);
		}
		String sql = "select * from dic_institution where deleted = 0 "+where;
		return DicInstitution.dao.find(sql,list.toArray());
	}
	
	public boolean saveInstitution(DicInstitution institution) {
		boolean ret = false;
		if (institution.getId() != null) {
			ret = institution.update();
        } 
		else {
        	institution.setDeleted("0");
        	ret = institution.remove("id").save();
        }
		if(ret) {
			CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_INSTITUTION_KEY);
		}
		return ret;
	}
	
	/**
	 * 保存机构与角色的关联信息
	 * @param institutionid
	 * @param roles
	 * @return
	 */
	public boolean saveInstitutionRole(Integer institutionid, String roles) {
	    // 先根据机构的id将角色以前的关联信息删除
	    boolean succeed=Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
            boolean ret=true;
            try {
                Db.update("DELETE FROM dic_institution_role WHERE institutionid = ?",institutionid);
                // 角色与机构绑定
                if (StringUtils.isNotBlank(roles)) {
                    String roleids = roles.substring(0, roles.length()-1);
                    String ids[] = roleids.split(",");
                    for (String id:ids) {
                        if (StringUtils.isNotBlank(id)) {
                            DicInstitutionRole ir = new DicInstitutionRole();
                            ir.set("roleid", id);
                            ir.set("institutionid", institutionid);
                            ir.save();
                        }
                    }
                }
            }catch(Exception e) {
                e.printStackTrace();
                return false;
            }
            return ret;
        });
        return succeed; 
    }
	
	/**
	 * 删除机构前判断是否有相关数据
	 * @param id
	 * @return
	 */
	public HashMap<String, Object> beforeDeleteInstitution(Integer id) {
		HashMap<String, Object> map=new HashMap<String, Object>();
		//角色
		String sql = "SELECT * FROM role WHERE"
				+ " EXISTS (SELECT dic_institution_role.roleid FROM dic_institution_role"
				+ " WHERE dic_institution_role.roleid = role.id AND dic_institution_role.institutionid = ?)";
		List<Role> role = Role.dao.find(sql,id);
		map.put("role", role.size());
		//科室
		List<DicDepartment> department = DicDepartment.dao.find("SELECT * FROM dic_department WHERE institutionid=? and deleted=0",id);
		map.put("dic_department",department.size());
		//员工
		List<DicEmployee> employee = DicEmployee.dao.find("SELECT * FROM dic_employee WHERE institutionid=? and deleted=0",id);
		map.put("dic_employee",employee.size());
		//设备
		List<DicModality> dicModality = DicModality.dao.find("SELECT * FROM dic_modality WHERE institutionid=? and deleted=0",id);
		map.put("dic_modality",dicModality.size());
		return map;
	}
	
	public boolean deleteInstitution(Integer id) {
		boolean ret = true;
		int count = Db.update("UPDATE dic_institution SET deleted=1 WHERE id = ?",id);
		CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_INSTITUTION_KEY);
		return ret && count==1;
	}
	
	/**
     *  添加/编辑 机构与角色关联信息时，对角色的查找
     * @param institutionid
     * @return
     */
    public ArrayList<String[]> findRoleByInstitutionId(Integer institutionid) {
        // 查出所有的角色信息，在查出所有的
        ArrayList<String[]> list=new ArrayList<String[]>();
        List<Record> roleList = Db.find("select r.*,ir.id as institutionroleid from role as r left join  dic_institution_role as ir on r.id=ir.roleid and ir.institutionid=?",institutionid);
        if (roleList.size() > 0) {
            for (Record role : roleList) {
                String[] arr = new String[3];
                // 角色的id
                arr[0] = role.getStr("id");
                // 角色的名称
                arr[1] = role.getStr("rolename");
                // 是否选中，1为选中，0为未选中
                arr[2] = role.getStr("institutionroleid") != null? "1":"0";
                list.add(arr);
            }
        }
        return list;
    }
    
    /**
     * 设备--叫号信息保存
     */
    public boolean saveDicCalling(DicCalling dicCalling) {
    	boolean ret = false;
    	if(dicCalling.getId() != null) {
    		ret = dicCalling.update();
    	} else {
    		dicCalling.setId(0);
        	ret = dicCalling.remove("id").save();
    	}
    	return ret;
    }
    
    
    /**
     * 获取设备-叫号信息
     */
	public List<DicCalling> getDicCalling(Integer modalityid, String modalityname) {
		String where = " deleted = '0'";
		List<Object> list = new ArrayList<Object>(); 
		if(modalityid !=null ){
			where += " and modalityid = ?";
			list.add(modalityid);
		}
		if(StringUtils.isNotBlank(modalityname)) {
			where += " and modalityid = (select id from dic_modality where modality_name = ? )";
			list.add(modalityname);
		}
		where += " order by priority,createtime ";
		log.info(where);
		List<DicCalling> dicCallings = DicCalling.dao.find("select *,(select modality_name from dic_modality where dic_modality.id = dic_calling.modalityid) as modalityname from dic_calling where" +where,list.toArray());
		return dicCallings;
	}
	
	public boolean deleteDicCalling(Integer id) {
		int count = Db.update("update dic_calling set deleted = '1' where id = ?",id);
		if(count == 1) {
			return true;
		} else {
			return false;
		}	
	}
	
	
	/**
	 * @return 
	 * 
	 */
	public List<DicCalling> findDicCalling(String modalityid) {
		String where = "";
		List<Object> list=new ArrayList<Object>();
		if(modalityid!=null) {
		    where += " and modalityid = ?";
		    list.add(modalityid);
		}
		String sql="select * from dic_calling where modalityid = ? and deleted = '0'";
		return DicCalling.dao.find(sql,list.toArray());
	}

//	public JSONArray findDicBodyParts() {
//		JSONObject root =null;
//		JSONArray arr = new JSONArray();
//		root = new JSONObject();
//		List<JSONObject> list= getNodes();
//		root.put("id", "");
//		root.put("text", "选择");
//		root.put("state", "open");
//		root.put("iconCls", "icon-fo");
//		//判断是否有子节点
//		if(list!=null&&list.size()>0) {
//			root.put("children", list);
//		}
//		arr.add(root);
//		return arr;
//	}
	
//	private List<JSONObject> getNodes() {
//		List<JSONObject> jsonobjectList=new ArrayList<JSONObject>();
//		String sql="select * from dic_body_parts";
//		List<Record> recordList=Db.find(sql);
//		for (int i = 0; i < recordList.size(); i++) {
//			Record record=recordList.get(i);
//			JSONObject obj = new JSONObject();
//			obj.put("id", record.get("id"));
//			obj.put("text", record.get("name"));
//			obj.put("state", "undefined");
//			obj.put("iconCls", "icon-fo");
//			jsonobjectList.add(obj);
//		}
//		return jsonobjectList;
//	}

//
//
//	public List<Record> findDicBodyPartsLinesByParentId(Integer parentId,String sex) {
//		String sql="select * from dic_body_parts_lines where parentId="+parentId+"  and deleted=0";
//		if(sex!=null&&!"".equals(sex)) {
//			if("M".equals(sex)) {
//				sql+=" and sex not in ('F')";
//			}
//			if("F".equals(sex)) {
//				sql+=" and sex not in ('M')";
//			}
//		}
//		return Db.find(sql);
//	}


	public List<Record> findDicOrgan(String modality) {
		String sql="select * from dic_organ where modality=? and deleted=0 and parentid=0";
		return Db.find(sql,modality);
	}
      /**
        * 提取部位正、异常描述
       * @param str
       * @param examination_position
       * @return
       */
//	public List<Record> findDicBodyPartsLinesDescribe(String str,Integer examination_position,String sex) {
//		str=str.replaceAll(",", "','");
//		StringBuffer sb=new StringBuffer();
//		sb.append(" select *from (");
//		sb.append(" select parentid,normal_description as miaoshu ,sort from dic_body_parts_lines  where name not in('"+str+"') and deleted=0  ");
//		if(0!=examination_position) {
//			sb.append(" and parentId="+examination_position+"");
//		}
//		if("M".equals(sex)) {
//			sb.append(" and sex not in ('F')");
//		}
//		if("F".equals(sex)) {
//			sb.append(" and sex not in ('M')");
//		}
//		sb.append(" union");
//		sb.append(" select parentid,exception_description,sort from dic_body_parts_lines  where name  in('"+str+"') and deleted=0 ");
//		if(0!=examination_position) {
//			sb.append(" and parentId="+examination_position+"");
//		}
//		if("M".equals(sex)) {
//			sb.append(" and sex not in ('F')");
//		}
//		if("F".equals(sex)) {
//			sb.append(" and sex not in ('M')");
//		}
//		sb.append(" ) a order by parentId,sort");
//		return Db.find(sb.toString());
//	}
	
	public List<DicCommon> findDicCommon(String group,String value){
		List<String> list =new ArrayList<>();
		String where =" where 1=1";
		if(StrKit.notBlank(group)) {
			where+=" and dicgroup=?";
			list.add(group);
		}

		if(StrKit.notBlank(value)) {
			where+=" and (code like CONCAT(?,'%') or name like CONCAT(?,'%') or name_en like CONCAT(?,'%')) ";
			list.add(value);
			list.add(value);
			list.add(value);
		}
		return DicCommon.dao.find("select (select name_zh from syscode where code=dic_common.type and type='0004') as type_name,* from dic_common "+where+" order by sort",list.toArray());
	}
	
	public List<DicCommon> findDicCommonFromCache(String group,String type){
		List<DicCommon> ret = CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_DICCOMMON_KEY, new IDataLoader(){ 
			public Object load() {
					return DicCommon.dao.find("select (select name_zh from syscode where code=t.type and type='0004') as type_name,* from dic_common t "
							+ " where t.deleted='0' order by t.sort"); 
			}
		});
		
		if(StrKit.notBlank(group)||StrKit.notBlank(type)){
			List<DicCommon> re=ret.stream().filter(dc-> (group==null||StrKit.equals(group, dc.getDicgroup()))&&(type==null||StrKit.equals(type, dc.getType())))
					.collect(Collectors.toList());
			return re;
		} else {
			return ret;
		}
		
	}
	
	public boolean delDicCommon(Integer id) {
		if(DicCommon.dao.deleteById(id)) {
			CacheKit.remove(CacheName.DICCACHE,CacheName.DICCACHE_DICCOMMON_KEY);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean saveDicCommon(String jsonStr,String group) {
		if(Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			boolean ret=true;
			JSONArray items=JSON.parseArray(jsonStr);
			for(int i=0;i<items.size();i++) {
				JSONObject item=items.getJSONObject(i);
				DicCommon common=new DicCommon();
				for(String key:item.keySet()) {
					if(DicCommon.dao.containColumn(key)) {
						common.set(key, item.get(key));
					}
				}
				if(StrKit.notBlank(common.getStr("id"))) {
					ret=ret&common.update();
				} else {
					common.setDicgroup(group);
					ret=ret&common.remove("id").save();
				}
			}
			return ret;
		})) {
			CacheKit.remove(CacheName.DICCACHE,CacheName.DICCACHE_DICCOMMON_KEY);
			return true;
		} else {
			return false;
		}
	}
}	
	

