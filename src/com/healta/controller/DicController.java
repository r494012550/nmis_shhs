package com.healta.controller;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.healta.constant.SessionKey;
import com.healta.model.*;
import com.healta.util.DACKit;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.healta.constant.CacheName;
import com.healta.constant.TableNameConstant;
import com.healta.service.DicService;
import com.healta.util.ResultUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

public class DicController extends Controller {
	private final static Logger log = Logger.getLogger(DicController.class);
	private final static DicService sv=new DicService();
	
	/**
	 * 从缓存中读取机构
	 */
	public void getInstitutionFromCache(){
		List<DicDepartment> institution = CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_INSTITUTION_KEY, new IDataLoader(){ 
			public Object load() { 
				return DicDepartment.dao.find("select * from dic_institution where deleted=0 "); 
			}
		});
	
		renderJson(institution);
	}
	
	public void getDeptFromCache(){
		List<DicDepartment> depts = CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_DEPARTMENT_KEY, new IDataLoader(){ 
			public Object load() { 
				return DicDepartment.dao.find("select * from dic_department where deleted=0"); 
			}
		});
		String type=getPara("type");
		Integer institutionid=getParaToInt("user_institution");
		if(StrKit.notBlank(type)||institutionid!=null){
			List<DicDepartment> ret=depts.stream().filter(dept-> !StrKit.equals(type, dept.getType())
					&&(institutionid==null||dept.getInstitutionId().intValue()==institutionid.intValue())).collect(Collectors.toList());
			renderJson(ret);
		}
		else{
			renderJson(depts);
		}
	}
	
	/**
	 * 获取医生/护士/技师
	 */
	public void getEmployeeFromCache() {
		List<DicEmployee> ems = CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_EMPLOYEE_KEY, new IDataLoader() { 
			public Object load() { 
				return DicEmployee.dao.find("select * from dic_employee where deleted=0"); 
			}
		});
		
		Integer deptfk = getParaToInt("deptfk");
		String deptcode = getPara("deptcode");
		String profession = getPara("profession");
		Integer institutionid = getParaToInt("user_institution");
		log.info("所属科室：" + deptcode + ", 职务：" + profession);
		if(deptfk!=null||StrKit.notBlank(profession)||institutionid!=null){
			List<DicEmployee> ret=ems.stream().filter(em-> (deptfk==null||deptfk.intValue()==em.getDeptfk())&&(profession==null||StrKit.equals(profession, em.getProfession()))
					&&(deptcode==null||StrKit.equals(deptcode, em.getDeptcode()))
					&&(institutionid==null||em.getInstitutionid().intValue()==institutionid.intValue()))
					.collect(Collectors.toList());//em.getDeptfk().intValue()==deptfk&&
			renderJson(ret);
		} else {
			renderJson(ems);
		}
	}
	
	/**
	 * 从缓存中读取检查项目并筛选
	 */
	public void getExamItemDicFromCache(){
		List<Record> items = CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_EXAMITEM_KEY, new IDataLoader(){ 
			public Object load() { 
				return Db.find("select * from dic_examitem where deleted=0"); 
			}
		});
		
		Integer equipment=getParaToInt("equipment");
		
		if (equipment!=null) {
		
			HashMap<Integer, List<Record>> ees = CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_EXAMEQUIPMAP_KEY, new IDataLoader(){ 
				public Object load() {
					HashMap<Integer, List<Record>> map=new HashMap<Integer, List<Record>>();
					List<Record> tmps=Db.find("select dic_examitem.*,dic_exam_equip.equip_id"
							+ " from dic_examitem,dic_exam_equip"
							+ " where dic_examitem.id=dic_exam_equip.exam_id"
							+ " and dic_examitem.deleted=0"); 
					
					for(Record re:tmps){
						List<Record> list=map.get(re.getInt("equip_id"));
						if(list==null){
							list =new ArrayList<Record>();
						}
						list.add(re);
						map.put(re.getInt("equip_id"), list);
					}
					
					return map;
				}
			});
			items=ees.get(equipment);
		}
		if(items!=null){
			String searchtext=getPara("searchtext");
			String modality=getPara("modality");
			Integer organ=getParaToInt("organ");
			Integer suborgan=getParaToInt("suborgan");
			
			if(StrKit.notBlank(searchtext)||StrKit.notBlank(modality)||organ!=null||suborgan!=null){
				List<Record> ret=items.stream().filter(ei-> 
				(searchtext==null||ei.getStr("item_name").startsWith(searchtext)||StringUtils.startsWithIgnoreCase(ei.getStr("py"), searchtext))&&
				(modality==null||StrKit.equals(modality, ei.getStr("type")))&&
				(organ==null||organ==0||(ei.getInt("organfk")!=null&&organ.intValue()==ei.getInt("organfk")))&&
				(suborgan==null||(ei.getInt("suborganfk")!=null&&suborgan.intValue()==ei.getInt("suborganfk")))
						).collect(Collectors.toList());
				renderJson(ret);
			}
			else{
				renderJson(items);
			}
		}else{
//			renderNull();
			renderJson(new ArrayList<String>());
		}
	}
	
	/**
	 * 从缓存中读取检查部位并筛选
	 * modality  设备类型
	 * parentOrgan  上级部位
	 */
	public void getOrganDic() {
		String modality = getPara("modality");
		Integer parentOrgan = getParaToInt("parent");
		List<DicOrgan> items = CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_ORGAN_KEY, new IDataLoader(){ 
		    public Object load() { 
		      return DicOrgan.dao.find("select * from dic_organ where deleted = 0"); 
		}});
		
		DicOrgan parent = new DicOrgan();
		if(StringUtils.isNotBlank(modality)) {//获取类型的id
			DicOrgan organ = DicOrgan.dao.findFirst("select top 1 * from dic_organ where treename_zh = ? and parentid = 0 and deleted = 0",modality);
			parent.setId(organ==null?null:organ.getId());
		}else if(parentOrgan != null){//获取上级部位的id
			DicOrgan organ = DicOrgan.dao.findFirst("select top 1 * from dic_organ where id = ? and deleted = 0",parentOrgan);
			parent.setId(organ==null?null:organ.getId());
		}
		
		List<DicOrgan> list = items.stream().filter(i -> (parent.getId()==null || i.getParentid().intValue() == parent.getId().intValue())).collect(Collectors.toList());
		
		renderJson(list);
		
	}
	
	/**
	 * 从缓存中读取设备并筛选
	 * state:0 工作中;deleted 0 未删除
	 * modality  设备类型
	 * institutionid  机构
	 */
	public void getModalityDic(){
		String modality  =getPara("modality");
		Integer institutionid = getParaToInt("user_institution");
		Integer working_state = getParaToInt("working_state");
		Integer departmentid = getParaToInt("departmentid");
		String role = getPara("role");
		/*List<DicModality> items = CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_MODALITY_KEY, new IDataLoader(){ 
		    public Object load() { 
		      return DicModality.dao.find("select * from dic_modality where working_state = ? and deleted = ?", 0, 0); 
		}});*/

		List<DicModality> items = CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_MODALITY_KEY, new 
			IDataLoader(){ 
			    public Object load() { 
			      return DicModality.dao.find("select * from dic_modality where deleted=0"); 
		}});
		
		if(StringUtils.isNotBlank(modality) || institutionid != null || departmentid != null||role!=null) {
			List<DicModality> list = items.stream().filter(i ->
			(StringUtils.isBlank(modality) || StringUtils.equals(i.getType(), modality))
			&& (StringUtils.isBlank(role) || StringUtils.equals(i.getRole(), role))
			&& (departmentid == null || i.getDepartmentid() == departmentid.intValue())
			&& (working_state == null || i.getWorkingState() == working_state.intValue())
			&& (institutionid == null || i.getInstitutionid() == null || i.getInstitutionid().intValue() == institutionid.intValue()))
			.collect(Collectors.toList());
			renderJson(list);
		}else {
			renderJson(items);
		}
	}
	
	public void getModalityDicByType(){
//		String modality=getPara("modality");
//		
//		List<DicModality> items = CacheKit.get(CacheName.DICCACHE, "dic_modality", new 
//			IDataLoader(){ 
//			    public Object load() { 
//			      return DicModality.dao.find("select * from dic_modality"); 
//			}});
//		
//		List<DicModality> ret=new ArrayList<DicModality>();
//	
//		if(StrKit.notBlank(modality)){
//			for(DicModality dic:items){
//				if(modality.equals(dic.getType())){
//					ret.add(dic);
//				}
//			}
//		}
//		else{
//			ret.addAll(items);
//		}
		renderJson(DicModality.dao.find("select * from dic_modality where deleted=0 order by type,modality_name"));
	}
	
	public void getEquipmentByStudyitem() {
		List<DicModality> modalityitems = CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_MODALITY_KEY, new 
				IDataLoader(){ 
		    public Object load() { 
		      return DicModality.dao.find("select * from dic_modality where deleted=0"); 
		}}); 
		String sql = "SELECT dic_exam_equip.equip_id FROM dic_exam_equip WHERE"
				+ " EXISTS (SELECT * FROM studyitem WHERE studyitem.item_id = dic_exam_equip.exam_id AND orderid = ?)";
		List<Record> items = Db.find(sql,getParaToInt("orderid")).stream().distinct().collect(Collectors.toList());
		
		List<DicModality> resultModality=new ArrayList<DicModality>();
		for(DicModality dic:modalityitems){
			for(Record re:items) {
				if(re.getInt("equip_id").intValue()==dic.getId().intValue()) {
					resultModality.add(dic);
				}
			}
			
		}
		
		renderJson(resultModality);
	}
	
	/*public void getEquipmentByStudyitem() {
		List<DicExamEquip> items = DicExamEquip.dao.find("select * from dic_exam_equip");
		List<DicModality> modalityitems = CacheKit.get(CacheName.DICCACHE, "dic_modality", new 
				IDataLoader(){ 
		    public Object load() { 
		      return DicModality.dao.find("select * from dic_modality where working_state = ? and deleted=0",0); 
		}}); 
		String modality=getPara("modality");
		
		List<Studyitem> studyitems = Studyitem.dao.find("SELECT * FROM studyitem WHERE orderid = ?", getParaToInt("orderid"));
		
		List<Integer> ret = new ArrayList<Integer>();
		Integer examid = null;
		//判断检查项目的id是否存在，并去重
		for(Studyitem s:studyitems) {
			examid = s.getItemId();
			if(examid!=null&&ret.indexOf(examid)==-1) {
				ret.add(examid);
			}
		}
		
		
		
		//获取检查项目的设备
		HashMap<Integer,List<Integer>> map = new HashMap<Integer,List<Integer>>();
		for(Integer id:ret) {
			List<Integer> ret2 = new ArrayList<Integer>();
			for(DicExamEquip item:items) {
				if(id==item.getExamId()) {
					ret2.add(item.getEquipId());
				}
			}
			map.put(id, ret2);
		}
		
		List<Integer> result = new ArrayList<Integer>(); 
		for(Integer id:ret) {
			result = map.get(id);
			for(Integer id2:ret) {
				map.get(id).retainAll(map.get(id2));
			}
			break;
		}
		List<DicModality> resultModality=new ArrayList<DicModality>();
		for(DicModality dic:modalityitems){
			if(StrKit.notBlank(modality)&&!modality.equals(dic.get("type"))){
				continue;
			}
			
			for(Integer id:result) {
				if(id==dic.getId()) {
					resultModality.add(dic);
				}
			}
		}
		renderJson(resultModality);
		
	}*/
	
	/**
	 * 获取绑定设备的部门
	 */
	public void getDeptOfModalityFromCache(){
		List<DicDepartment> depts = CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_DeptOfModality_KEY, new IDataLoader(){ 
			public Object load() { 
				return DicDepartment.dao.find("SELECT DISTINCT dic_department.* FROM dic_department"
						+ " INNER JOIN dic_modality ON dic_modality.departmentid = dic_department.id AND dic_modality.deleted = '0'"
						+ " WHERE dic_department.deleted = '0'"); 
			}
		});
		Integer institutionid=getParaToInt("user_institution");
		if(institutionid != null){
			List<DicDepartment> ret=depts.stream().filter(dept -> (institutionid==null||dept.getInstitutionId().intValue()==institutionid.intValue())).collect(Collectors.toList());
			renderJson(ret);
		}
		else{
			renderJson(depts);
		}
	}
	
	public void getModalityState() {
		try {
			renderJson(ResultUtil.success(DicModality.dao.findById(getParaToInt("modalityid"))));
		}catch(Exception e){
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 查询设备(根据检查id排除已分配检查的设备)
	 */
	public void findDic() {
		try{
			renderJson(sv.findDic(getPara("value"),getPara("type"),
					getParaToInt("institutionid"),getParaToInt("departmentid"),
					getParaToInt("examId"),getPara("deleted")));
		
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 查询组中设备
	 */
	public void findDicByGroup() {
		try{
			renderJson(sv.findDicByGroup(getParaToInt("groupid"),getSessionAttr("syscode_lan")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void goEditDic() {
		if(StringUtils.isNotEmpty(getPara("id"))){
			int id=getParaToInt("id");
			DicModality dic=DicModality.dao.findById(id);
			setAttr("id", id);
			int working_state= dic.getWorkingState();
			if(working_state==1) {
				setAttr("swb",false);
			}else {
				setAttr("swb",true);
			}
			String time1 = dic.getWorkdayOfWorktime();
			String time2 = dic.getSaturdayOfWorktime(); 
		    String time3 = dic.getSundayOfWorktime(); 
		    if(StringUtils.isNotBlank(time1)) {
		    	String [] a=time1.split(","); 
		    	String [] aa=a[0].split("-");
		    	String [] a1=a[1].split("-"); 
			    setAttr("workday_of_worktime1",aa[0]);
			    setAttr("workday_of_worktime2",aa[1]); 
			    setAttr("workday_of_worktime3",a1[0]);
			    setAttr("workday_of_worktime4",a1[1]);
			}
		    if(StringUtils.isNotBlank(time2)) {
			    String [] b=time2.split(","); 
			    String [] bb=b[0].split("-");
			    String [] b1=b[1].split("-"); 
			    setAttr("saturday_of_worktime1",bb[0]);
			    setAttr("saturday_of_worktime2",bb[1]);
			    setAttr("saturday_of_worktime3",b1[0]);
			    setAttr("saturday_of_worktime4",b1[1]); 
		   
		    }
		    if(StringUtils.isNotBlank(time3)) {
		    	String [] c=time3.split(","); 
				String [] cc=c[0].split("-");
				String [] c1=c[1].split("-");
			    setAttr("sunday_of_worktime1",cc[0]);
			    setAttr("sunday_of_worktime2",cc[1]); 
			    setAttr("sunday_of_worktime3",c1[0]);
			    setAttr("sunday_of_worktime4",c1[1]);
		    }
		    
			setAttr("dicmodality", dic);
		}
		else{
			setAttr("swb",true);
		}
		renderJsp("/view/admin/dic/editModality.jsp");
	}
	
	/**
	 * 保存设备
	 */
	public void saveModality() {
		try{
			DicModality dic=getModel(DicModality.class, "", true);

			renderJson(ResultUtil.success(sv.saveDic(dic)));
	
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	} 
	
	public void beforeDeleteDic() {
		try {
			renderJson(ResultUtil.success(sv.beforeDeleteDic(getParaToInt("id"))));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1,e.getMessage()));
		}
	}
	
	public void deleteDic() {
		try {
			if(sv.deleteDicModality(getParaToInt("id"))){
				renderJson(ResultUtil.success());
			}
			else{
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void pingHostname() {
		try {
			if(InetAddress.getByName(getPara("value")).isReachable(3000)) {
				renderJson(ResultUtil.success());
			}
			else {
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 根据设备分配检查项目
	 */
	public void goSetExamItemEquip() {
		setAttr("type", getPara("type"));
		setAttr("modalityId", getParaToInt("modalityId"));
		
		renderJsp("/view/admin/dic/setExamItem_Equip.jsp");
		
	}
	
	/**
	 * 获取检查项目，并将绑定设备的ck字段置1
	 */
	public void getExamitemEquipInfo() {
		try{
			renderJson(sv.getExamitemEquipInfo(getPara("type"),getPara("deleted"),getParaToInt("modalityId")));	
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void saveExamitemEquipInfo() {
		try {

			if (sv.saveExamitemEquipInfo(getParaToInt("modalityid"),getPara("examIds"))) {
				renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
/*
 * 检查项目
 * *
 */
	
	public void findExamitemdic() {
		try{
			renderJson(sv.findExamitemdic(getPara("value"),getPara("type"),getParaToInt("modalityid"),
					getParaToInt("organfk"),getParaToInt("suborganfk"),getPara("deleted")));
			
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void getExamEquip() {
		try{
//			List<DicModality> ret=new ArrayList<DicModality>();
//			Integer examid=getParaToInt("id");
//			if(examid!=null) {
//				String sql = "select * from dic_modality where"
//						+ " exists (select dic_exam_equip.equip_id from dic_exam_equip"
//						+ " where dic_exam_equip.equip_id = dic_modality.id and dic_exam_equip.exam_id = ?)";
//				ret = DicModality.dao.find(sql,examid);
//			}
			renderJson(sv.findModalityByExamItem(getParaToInt("id"), getSessionAttr("syscode_lan")));
			
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void deletExamitemdic() {
		try {
			if(sv.deleteExamitem(getParaToInt("id"))) {
				renderJson(ResultUtil.success());
			}
			else {
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	public void saveExamitem() {
		try{
		 DicExamitem examitemdic=getModel(DicExamitem.class, "", true);
		 String equipments=getPara("equipments");
			if(sv.saveExamitem(examitemdic,equipments)){
				renderJson(ResultUtil.success());
			}
			else{
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
		
	}

	
	public void editExamitemdic() {
		if(StringUtils.isNotBlank(getPara("id"))){
			int id=getParaToInt("id");
			DicExamitem item=DicExamitem.dao.findById(id);
			
			setAttr("id", id);
			setAttr("item_name", item.getItemName());
			setAttr("type", item.getType());
			setAttr("coefficient", item.getCoefficient());
			setAttr("item_code", item.getItemCode());
			setAttr("py", item.getPy());
			setAttr("organfk", item.getOrganfk());
			setAttr("suborganfk", item.getSuborganfk());
			setAttr("price", item.getPrice());
			setAttr("report_alert_hour", item.getReportAlertHour());
			setAttr("report_alert_minute", item.getReportAlertMinute());
			setAttr("duration", item.getDuration());
			setAttr("fulldescription", item.getFulldescription());
			
		}
			renderJsp("/view/admin/dic/editExamitem.jsp");
	}
	

/*
 * 组
 * *
 */
	
	public void findGroup() {
		try{
			renderJson(sv.findGroup(getPara("value"),getParaToInt("modalityid"),getPara("deleted")));
			
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	 
	 /**
	  * 保存设备组
	  */
	 public void saveGroup() {
		 try{
			DicEquipGroup group=getModel(DicEquipGroup.class, "", true);

			if(sv.saveGroup(group,getParaValuesToInt("modalityids"))){						
				renderJson(ResultUtil.success());		
			}
			else{
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	 }
		
	 
	 /**
	  * 删除设备组
	  */
	 public void deleteGroup() {
		 try {
				if(sv.deleteGroup(getParaToInt("id"))) {
					renderJson(ResultUtil.success());
				}
				else {
					renderJson(ResultUtil.fail(""));
				}
			}catch(Exception e) {
				e.printStackTrace();
				renderJson(ResultUtil.fail(-1, e.getMessage()));
			}
	 }

	
 	 public void goEditGroup() {
		if(StringUtils.isNotEmpty(getPara("id"))){
			int id=getParaToInt("id");
			DicEquipGroup m=DicEquipGroup.dao.findById(id);
			
			List<DicModality> modalitys = DicModality.dao.find("SELECT * FROM dic_modality WHERE groupid=? AND deleted=0",id);
			String ids="";
			for(DicModality mo:modalitys) {
				ids += mo.getId()+",";
			}

			ids=StringUtils.isBlank(ids)?"":ids.substring(0, ids.length()-1);
			
			setAttr("id", id);
			setAttr("gpname", m.get("gpname"));
			setAttr("type", m.get("type"));
			setAttr("description", m.get("description"));
			setAttr("modalityids", ids);
			setAttr("default_duration", m.get("default_duration"));
			}
			renderJsp("/view/admin/dic/editGroup.jsp");
	}

	public void checkGroupName(){
		if(sv.checkGroupName(getPara("gpname"),getParaToInt("id"))){
			renderJson(ResultUtil.success());	
		}
		else{
			renderJson(ResultUtil.fail(""));
		}
	}
	
/*
 *  部位字典
 *
 *
 */
	/**
	 * 根据类型查询部位
	 */
	public void findOrgandic(){
		try {
			renderJson(sv.findOrgandic(getPara("modalitytype"),getPara("deleted")));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
//	public void findOrgandic1() {
//		try{
//			Integer parentid=null;
//			List<Organdic> organs=Organdic.dao.find("select * from organdic"  );
//			if(StringUtils.isNotBlank(getPara("modalitytype"))){
//				//System.out.println("==type==");
//				String sql1="select top 1 * from organdic where treename_zh='"+getPara("modalitytype")+"'"; 
//				Organdic organd=Organdic.dao.findFirst(sql1);
//				if(organd!=null){
//					parentid=organd.getId();
//				}
//			}else if(getParaToInt("parentId")!=null){
//				//System.out.println("==expand==");
//				parentid=getParaToInt("parentId");
//			}
//			
//		
//			String sql="select *  from organdic where parentid=? "; 
//			
//			List<Organdic> organdics=Organdic.dao.find(sql, parentid);
//			//System.out.println("****"+parentid+"******");
//			JSONArray array=new JSONArray();
//			for(Organdic organdic:organdics){
//				JSONObject jo=new JSONObject();
//			    jo.put("id", organdic.getId());
//			    jo.put("treename_zh", organdic.getTreenameZh());
//			    jo.put("treename_en", organdic.getTreenameEn());
//			    jo.put("typecode", organdic.getTypecode());
//			    jo.put("parentid",organdic.getParentid());
//			    jo.put("levelnum",organdic.getLevelnum());
//			   
//			    if(hasChild(organdic,organs)){
//			    	jo.put("state","closed");
//			    }
//			    
//			    array.put(jo); 
//				
//			  }	
//			
//			renderJson(array.toString());
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//			renderJson(ResultUtil.fail(-1, e.getMessage()));			
//		   }
//	}
//	
//	public boolean hasChild(Organdic organdic,List<Organdic> organs){
//		boolean ret=false;
//		for(Organdic o:organs){
//			if(o.getParentid()==organdic.getId()){
//				return true;
//			}
//		}
//		return ret;
//	}
	
	//add organ
	public void addOrgandic() {
//		DicOrgan parent=DicOrgan.dao.findFirst("select top 1 * from dic_organ where modality=? and parentid=0 and deleted=0",getPara("modality"));
//		if(parent==null) {
//			parent = new DicOrgan();
//			parent.setTreenameZh(getPara("modality"));
//			parent.setTreenameEn(getPara("modality"));
//			parent.setParentid(0);
//			parent.setTypecode(getPara("modality"));
//			parent.setModality(getPara("modality"));
//			parent.setDeleted("0");
//			parent.save();
//			CacheKit.remove(CacheName.DICCACHE, CacheName.DICCACHE_ORGAN_KEY);
//		}
//		setAttr("organ", new DicOrgan().set("parentid", parent.getId()).set("modality", getPara("modality")));
		setAttr("organ", new DicOrgan().set("parentid", 0).set("modality", getPara("modality")));
		renderJsp("/view/admin/dic/editOrgandic.jsp");
	}
	
	//add sub_organ
	public void addsubOrgandic() {
		setAttr("organ", new DicOrgan().set("parentid", getParaToInt("id")).set("modality", getPara("modality")));
		renderJsp("/view/admin/dic/editOrgandic.jsp");
	}
	
	/**
	 * 保存部位
	 */
	public void saveOrgandic() {
		try{
			DicOrgan organ=getModel(DicOrgan.class, "", true);	
			
			if(sv.saveOrgandic(organ)){
				renderJson(ResultUtil.success());
			}
			else{
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

	/* public void saveOrgandic() {
		 try{
			 	DicOrgan o=getModel(DicOrgan.class, "", true);	
			 	o.remove("id").save();
			 	renderJson(ResultUtil.success());	
			} catch (Exception e) {
				renderJson(ResultUtil.fail(-1, e.getMessage()));
			}
		}*/
	 
	 /*public void savesubOrgandic() {
		 try{
			 	DicOrgan exn=getModel(DicOrgan.class, "", true);
				exn.remove("id").save();
				renderJson(ResultUtil.success());				
			} catch (Exception e) {
				renderJson(ResultUtil.fail(-1, e.getMessage()));
			}
			
		}*/
	//改
	public void editOrgandic() {
		setAttr("organ", DicOrgan.dao.findById(getParaToInt("id")));
		renderJsp("/view/admin/dic/editOrgandic.jsp");
	}
	
	/*public void saveEditOrgandic() {
		 try{
			 DicOrgan o=getModel(DicOrgan.class, "", true);
			 if(o.getId()!=null)	
				 o.update();
				renderJson(ResultUtil.success());
				
			} catch (Exception e) {
				renderJson(ResultUtil.fail(-1, e.getMessage()));
			}
		}*/
	
	/**
	 * 删除部位字典前判断
	 */
	public void beforeDeleteOrgan() {
		try {
			renderJson(ResultUtil.success(sv.beforeDeleteOrgan(getParaToInt("id"))));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1,e.getMessage()));
		}
	}
	
	/**
	 * 删除部位
	 */
	public void deleteOrgandic() {
		 try {
			 if(sv.deleteOrgandic(getParaToInt("id"))) {
				 renderJson(ResultUtil.success());
			}else {
				renderJson(ResultUtil.fail(""));
			}		 
		}catch(Exception e) {
				e.printStackTrace();
				renderJson(ResultUtil.fail(-1, e.getMessage()));
			}
	 }


/*
 * 科室管理
 * *
 */
	
	public void findDepartment() {
		try{
			renderJson(sv.findDepartment(getPara("value"),getParaToInt("institutionid"),getPara("deleted")));			
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void Modifydepartment() {
			if(StringUtils.isNotEmpty(getPara("id"))){
				int id=getParaToInt("id");
				DicDepartment m=DicDepartment.dao.findById(id);				
				setAttr("id", id);
				setAttr("deptname", m.get("deptname"));
				setAttr("type", m.get("type"));
				setAttr("deptcode", m.get("deptcode"));
				setAttr("institutionid", m.get("institutionid"));
				}
				renderJsp("/view/admin/dic/editDepartment.jsp");
		
	 }
	 
	 /**
	  * 保存科室部门
	  */
	 public void saveDepartment() {
		 try{
			 	DicDepartment dic=getModel(DicDepartment.class, "", true);
				if(sv.saveDepartment(dic,getPara("shiftids"))){
					renderJson(ResultUtil.success());
				}
				else{
					renderJson(ResultUtil.fail(""));
				}
			} catch (Exception e) {
				e.printStackTrace();
				renderJson(ResultUtil.fail(-1, e.getMessage()));
			}
	 }
	 
	 /**
	  * 删除科室部门前判断是否有相关数据
	  */
	 public void beforeDeleteDepartment() {
		 try {
				renderJson(ResultUtil.success(sv.beforeDeleteDepartment(getParaToInt("id"))));
			}catch(Exception e) {
				e.printStackTrace();
				renderJson(ResultUtil.fail(-1,e.getMessage()));
			}
	 }
	 
	 /**
	  * 删除科室部门
	  */
	 public void deleteDepartment() {
		 try {
				if(sv.deleteDepartment(getParaToInt("id"))) {
					renderJson(ResultUtil.success());
				}
				else {
					renderJson(ResultUtil.fail(""));
				}
			}catch(Exception e) {
				e.printStackTrace();
				renderJson(ResultUtil.fail(-1, e.getMessage()));
			}
	 }
	 	

/*
 * 员工管理
 * *
 */
	/**
	 * (根据员工姓名或科室)获取员工
	 */
	public void getEmployee() {
		try{
			renderJson(sv.getEmployee(getParaToInt("institutionid"), getPara("name"),getParaToInt("dept"),getPara("profession"),getPara("deleted")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void goEditEmployee() {
		if(StringUtils.isNotEmpty(getPara("id"))){
			int id=getParaToInt("id");				
			DicEmployee employee=DicEmployee.dao.findById(id);
			setAttr("id", id);
			setAttr("jobnumber",employee.get("jobnumber"));
			setAttr("name",employee.get("name"));
			setAttr("deptfk",employee.get("deptfk"));
			setAttr("profession",employee.get("profession"));
			setAttr("institution",employee.get("institutionid"));
		}
		renderJsp("/view/admin/dic/editEmployee.jsp");	
	}
	
	/**
	 * 返回所有送检机构
	 */
	public void returnInstitution() {
		renderJson(JsonKit.toJson(Db.findAll(TableNameConstant.DIC_INSTITUTION)));
	}
	
	public void saveEmployee() {
		 try{
			 	DicEmployee employee=getModel(DicEmployee.class, "", true);		
			 	if(sv.saveEmployee(employee)){
					renderJson(ResultUtil.success());
				}
				else{
					renderJson(ResultUtil.fail(""));
				}					
			} catch (Exception e) {
				e.printStackTrace();
				renderJson(ResultUtil.fail(-1, e.getMessage()));
			}
	}
	 
	 public void deleteEmployee() {
			try {
				if(sv.deleteEmployee(getParaToInt("id"))){
					renderJson(ResultUtil.success());
				}
				else{
					renderJson(ResultUtil.fail(""));
				}
			}catch(Exception e) {
				e.printStackTrace();
				renderJson(ResultUtil.fail(-1, e.getMessage()));
			}
		}
	 
    /**
     *  查询机构管理列表
     */
    public void getInstitution() {
        try {
            renderJson(sv.getInstitution(getPara("value"),getPara("deleted")));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(ResultUtil.fail(-1, e.getMessage()));
        }
    }

    /**
     *  跳更新机构管理页面
     */
    public void modifyInstitution() {
        if (StringUtils.isNotEmpty(getPara("id"))) {
            int id = getParaToInt("id");
            DicInstitution institution = DicInstitution.dao.findById(id);
            setAttr("id", id);
            setAttr("detail", institution);
        }
        renderJsp("/view/admin/dic/editInstitution.jsp");

    }
   
    
    /**
           *  跳编辑机构与角色关联的页面
     */
    public void editInstitutionRole() {
        if (StringUtils.isNotEmpty(getPara("id"))) {
            int id = getParaToInt("id");
            setAttr("id", id);
            setAttr("roleList", sv.findRoleByInstitutionId(id));
        }
        renderJsp("/view/admin/dic/editInstitutionRole.jsp");
    }
    
    /**
     *  保存机构管理
     */
    public void saveInstitution() {
        try {
            DicInstitution institution = getModel(DicInstitution.class, "", true);
            if(sv.saveInstitution(institution)) {
            	renderJson(ResultUtil.success());
            }else {
            	renderJson(ResultUtil.fail(""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(ResultUtil.fail(-1, e.getMessage()));
        }
    }
    
    /**
           *  保存机构与角色的关联
     */
    public void saveInstitutionRole() {
        try {
            String roleids = getPara("roleids"); 
            Integer institutionId = getInt("id");
            if(sv.saveInstitutionRole(institutionId, roleids)) {
                renderJson(ResultUtil.success());
            }else {
                renderJson(ResultUtil.fail(""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(ResultUtil.fail(-1, e.getMessage()));
        }
    }
    
    /**
     * 删除机构前判断是否有相关数据
     */
	  public void beforeDeleteInstitution() {
	  	try {
			renderJson(ResultUtil.success(sv.beforeDeleteInstitution(getParaToInt("id"))));
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1,e.getMessage()));
		}
	  }
 
    /**
     *  删除机构管理
     */
    public void deleteInstitution() {
        try {
            if (sv.deleteInstitution(getParaToInt("id"))) {
                renderJson(ResultUtil.success());
            } else {
                renderJson(ResultUtil.fail(""));
            }
        } catch (Exception e) {
            renderJson(ResultUtil.fail(-1, e.getMessage()));
        }
    }
    
    /**
     * 添加设备叫号分组
     */
    public void addDicCalling() {
    	renderJsp("/view/admin/dic/editDicCalling.jsp");
	}
    
    /**
     * 保存设备--叫号提示信息
     */
    public void saveDicCalling() {
    	try {
			DicCalling dicCalling = getModel(DicCalling.class,"",true);
			if(sv.saveDicCalling(dicCalling)) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
    }
    
    
    /**
     * 获取
     */
    public void getDicCalling() {
    	try {
			renderJson(sv.getDicCalling(getInt("modalityid"),getPara("modalityname")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
    }
    
    /**
     * 修改设备-叫号信息
     */
    public void ModifyDicCalling() {
    	if (getInt("id") != null) {
            int id = getParaToInt("id");
            DicCalling dicCalling = DicCalling.dao.findById(id);
            set("detail", dicCalling);
        }
        renderJsp("/view/admin/dic/editDicCalling.jsp");
    }
    
    /**
     * 删除设备-叫号信息
     */
    public void deleteDicCalling() {
    	try {
    		log.info(getPara("id"));
			if(sv.deleteDicCalling(getInt("id"))){
				renderJson(ResultUtil.success());
			}
			else{
				renderJson(ResultUtil.fail(""));
			}
		}catch(Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
    }
    
    /**
     * 查询设备--叫号信息
     */
    public void findDicCalling() {
    	try{
			renderJson(sv.findDicCalling(getPara("modalityid")));			
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
    }
    
//    /**
//     * 查询人体部位信息 --tree格式
//     */
//    public void findDicBodyParts() {
//    	try{
//			renderJson(sv.findDicBodyParts());			
//		} catch (Exception e) {
//			e.printStackTrace();
//			renderJson(ResultUtil.fail(-1, e.getMessage()));
//		}
//    }
    
    
    /**
     * 查询部位，不包含子部位
     */
    public void findDicOrgan() {
    	try{
    		String modality=getPara("modality");
    		List<Record> list=sv.findDicOrgan(modality);
    		renderJson(list);	
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
    }
    
    /**
     * 查询人体子部位信息
     */
//    public void findDicBodyPartsLinesByParentId() {
//    	try{
//    		Integer parentId= getParaToInt("id");
//    		String sex=getPara("sex");
//			renderJson(sv.findDicBodyPartsLinesByParentId(parentId,sex));			
//		} catch (Exception e) {
//			e.printStackTrace();
//			renderJson(ResultUtil.fail(-1, e.getMessage()));
//		}
//    }
  
    
    /**
     * 编辑人体子部位信息
     */
//    public void modifyBodyParts() {
//    	try{
//    		Integer id= getParaToInt("id");
//    		String sql="select * from dic_body_parts_lines where id="+id+"";
//    		List<Record> datas=Db.find(sql);
//    		renderJson(ResultUtil.success(datas));
//		} catch (Exception e) {
//			e.printStackTrace();
//			renderJson(ResultUtil.fail(-1, e.getMessage()));
//		}
//    }
    
    
    /**
       * 查询人体子部位异常，正常描述
     */
//    public void findDicBodyPartsLinesDescribe() {
//    	try{
//    		String str= getPara("str");
//    		Integer examination_position= getParaToInt("examination_position");
//    		String sex=getPara("sex");
//			renderJson(sv.findDicBodyPartsLinesDescribe(str,examination_position,sex));			
//		} catch (Exception e) {
//			e.printStackTrace();
//			renderJson(ResultUtil.fail(-1, e.getMessage()));
//		}
//    }
    public void findModalityname() {
    	try {
			String modalityId= getPara("modalityId");
			modalityId=modalityId.replaceAll(",", "','");
			renderJson(Db.find("select * from dic_modality where  type in('"+modalityId+"')"));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}	
    }
    
    //查询
  	public void findDicCommon() {
		try{
			renderJson(ResultUtil.success(sv.findDicCommon(get("group"),get("value"))));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
  	}
  	
  	public void findDicCommonFromCache() {
		try{
			renderJson(sv.findDicCommonFromCache(get("group"),get("modality")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
  	}
  	
  	public void delDicCommon() {
  		try{
  			if(sv.delDicCommon(getInt("id"))) {
  				renderJson(ResultUtil.success());
  			} else {
  				renderJson(ResultUtil.fail(""));
  			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
  	}
  	
  	public void saveDicCommon() {
  		try{
  			if(sv.saveDicCommon(get("data"),get("group"))) {
  				renderJson(ResultUtil.success());
  			} else {
  				renderJson(ResultUtil.fail(""));
  			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
  	}

	/**
	 *  查询机构管理列表,返回数据经过数据访问控制配置进行过滤
	 */
	public void getInstitutionDAC() {
		try {
			UserDac dac=getSessionAttr(SessionKey.USER_DAC)!=null?(UserDac)getSessionAttr(SessionKey.USER_DAC):null;
			List<DicInstitution> list= sv.getInstitution(getPara("value"));
			renderJson(DACKit.institutionDAC(dac, list));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
} 
	
				

