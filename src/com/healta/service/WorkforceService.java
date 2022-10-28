package com.healta.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.healta.constant.CacheName;
import com.healta.model.DeptPostShift;
import com.healta.model.DeptReportTaskSetting;
import com.healta.model.DeptShift;
import com.healta.model.DeptShiftWork;
import com.healta.model.DeptShiftWorktime;
import com.healta.model.DeptSubscriptionExamitem;
import com.healta.model.DeptSubscriptionModality;
import com.healta.model.DeptSubscriptionRule;
import com.healta.model.DeptWorktime;
import com.healta.model.DicExamitem;
import com.healta.model.DicModality;
import com.healta.plugin.workforce.WorkforceServer;
import com.healta.plugin.workforce.Physician;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

public class WorkforceService {
	private final static Logger log = Logger.getLogger(WorkforceService.class);
	
	public List<DeptShift> getDeptShifts(){
		return DeptShift.dao.find("select * from dept_shift");
	}
	
	public List<Record> getDeptShiftWorktime(Integer shiftid){
		return Db.find("select worktimeid from dept_shift_worktime where shiftid=?",shiftid);
	}
	
	public List<DeptWorktime> getWorktimes(){
		return DeptWorktime.dao.find("select * from dept_worktime");
	}
	
	public boolean saveWorktime(DeptWorktime wt) {
		if(wt.getId()!=null){
			return wt.update();
		}else{
			return wt.remove("id").save();
		}
	}
	
	public boolean deleteWorktime(int id){
		return DeptWorktime.dao.deleteById(id);
	}
	
	/*
	 * return 1 成功
	 * 		  -1 失败
	 * 		  -2 失败，时间段重叠
	 * 
	 * */
	public String saveDeptShift(DeptShift ds,String worktimeids) {
		
		List<DeptWorktime> lt=DeptWorktime.dao.find("select * from dept_worktime where id in("+worktimeids+") order by starttime asc");
		
		boolean overlaps=false;
		
		for(int i=0,len=lt.size();i<len-1;i++){
			if(lt.get(i).getStarttime().compareTo(lt.get(i).getEndtime())>0){
				overlaps=true;
				break;
//				int newtime=Integer.valueOf(lt.get(i).getEndtime().substring(0, 2)).intValue()+24;
//				lt.get(i).setEndtime(newtime+lt.get(i).getEndtime().substring(3));
			}
			for(int j=i+1;j<len&&i!=j;j++){
				if(lt.get(j).getStarttime().compareTo(lt.get(j).getEndtime())>0){
//					int newtime=Integer.valueOf(lt.get(j).getEndtime().substring(0, 2)).intValue()+24;
//					lt.get(j).setEndtime(newtime+lt.get(j).getEndtime().substring(3));
					if(lt.get(i).getStarttime().compareTo(lt.get(j).getEndtime())<0){
						overlaps=true;
						break;
					}
				}
				
				if(lt.get(i).getEndtime().compareTo(lt.get(j).getStarttime())>0){
					overlaps=true;
					break;
				}
			}
			if(overlaps)break;
		}
		if(overlaps){
			return "-2";
		} else {
			return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
				boolean ret=true;
				if(ds.getId()!=null){
					ret=ret&ds.update();
					Db.delete("delete from dept_shift_worktime where shiftid=?",ds.getId());
					
				}else{
					ret=ret&ds.remove("id").save();
				}
	
				if(StrKit.notBlank(worktimeids)){
					String ids[]=worktimeids.split(",");
					List<DeptShiftWorktime> list=new ArrayList<DeptShiftWorktime>();
					for(String id:ids){
						DeptShiftWorktime sw=new DeptShiftWorktime();
						sw.setShiftid(ds.getId());
						sw.setWorktimeid(Integer.valueOf(id));
						list.add(sw);
					}
					
					Db.batchSave(list, list.size());
				}
				return ret;
			})?"1":"-1";
		}
	}

	public boolean deleteDeptShift(int id){
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			boolean ret=true;
			ret=ret&DeptShift.dao.deleteById(id);
			Db.delete("delete from dept_shift_worktime where shiftid=?",id);
			return ret;
		});
	}
	
	public List<Record> getDeptPostShift(Integer deptid,String postcode){
		if(deptid!=null&&StrKit.notBlank(postcode)){
			return Db.find("select dept_shift.*,dept_post_shift.shiftid from dept_shift "
					+ "left join dept_post_shift on dept_shift.id=dept_post_shift.shiftid and dept_post_shift.deptid=? and dept_post_shift.postcode=?",deptid,postcode);
		} else {
			return Db.find("select * from dept_shift");
		}
	}
	
	public List<Record> getDeptShiftAndWorktimes(Integer deptid ,String postcode){
		 List<Record> shiftlist=Db.find("select dept_shift.id as shiftid,dept_shift.name as shiftname,dept_worktime.id as worktimeid,dept_worktime.name,dept_worktime.starttime,dept_worktime.endtime from "
				+ "dept_post_shift ,dept_shift,dept_shift_worktime,dept_worktime "
				+ "where dept_post_shift.shiftid=dept_shift.id and dept_shift.id=dept_shift_worktime.shiftid "
				+ "and dept_shift_worktime.worktimeid=dept_worktime.id and dept_post_shift.deptid=? and dept_post_shift.postcode=? "
				+ "order by dept_shift.id,dept_worktime.starttime",deptid,postcode);
		 
		 Map<Integer,Integer> map= Db.find("select dept_shift.id,count(dept_shift_worktime.worktimeid) as count from dept_post_shift,dept_shift,dept_shift_worktime "
		 		+ "where dept_post_shift.shiftid=dept_shift.id and dept_shift.id=dept_shift_worktime.shiftid and dept_post_shift.deptid=? and dept_post_shift.postcode=? "
		 		+ "group by dept_shift.id",deptid,postcode)
				 .stream().collect(Collectors.toMap(r->r.getInt("id"),r->r.getInt("count")));
		 
		 shiftlist.stream().forEach(x->{
			 Integer count=map.get(x.getInt("shiftid"));
			 if(count!=null&&count>0){
				 x.set("rowspan", count);
				 map.put(x.getInt("shiftid"), 0);
			 } else if(count==0){
				 x.set("rowspan", 0);
			 } else{
				 x.set("rowspan", 1);
			 }
		 });
		 return shiftlist;
	}
	
	public Integer getPostShiftCount(Integer deptid ,String postcode){
		return DeptPostShift.dao.find("select null from dept_post_shift where deptid=? and postcode=?" ,deptid,postcode).size();
	}
	
	public Map<String, List<Record>> getShiftWorkDetails(Integer deptid ,String postcode,LocalDate monday){
		return Db.find("select id,userid,employeeid,(select dic_employee.name from dic_employee where dic_employee.id=dept_shift_work.employeeid) as employeename"
				+ ",institutionid,deptid,postcode,shiftid,worktimeid,workdate,modalityid from dept_shift_work  "
				+ "where deptid=? and postcode=? and workdate>=? and workdate <?  "
				+ "order by shiftid ,worktimeid,workdate",deptid,postcode,monday.format(DateTimeFormatter.ISO_DATE),monday.plusDays(7).format(DateTimeFormatter.ISO_DATE))
				.stream().collect(Collectors.groupingBy(x->(x.getStr("shiftid")+"-"+x.getStr("worktimeid")+"-"+DateKit.toStr(x.getDate("workdate"), "yyyy-MM-dd")
				+(x.getInt("modalityid")!=null?"-"+x.getInt("modalityid"):""))));
	}
	
	public List<Record> getEmployeesByDept(Integer deptid,String postcode){
//		List<DicEmployee> ems = CacheKit.get(CacheName.DICCACHE, "dic_employee", new IDataLoader() { 
//			public Object load() { 
//				return DicEmployee.dao.find("select * from dic_employee where deleted=0"); 
//			}
//		});
//		if(deptid!=null){
//			List<DicEmployee> ret=ems.stream().filter(em-> (deptid.intValue()==em.getDeptfk())&&(StrKit.equals(postcode, em.getProfession())))
//					.collect(Collectors.toList());
//			return ret;
//		} else {
//			return ems;
//		}
		
		return Db.find("select dic_employee.*,users.id as userid from users, dic_employee "
				+ "where users.employeefk=dic_employee.id and dic_employee.deptfk=? and dic_employee.profession=? and dic_employee.deleted=0",deptid,postcode);
	}
	
	public List<DicModality> getModalityByDept(Integer deptid){
		
		Map<String, Integer> map=Db.find("select type ,count(id) as count from dic_modality where departmentid=? and role='modality' and deleted=0 group by type",deptid)
		.stream().collect(Collectors.toMap(x->x.getStr("type"), x->x.getInt("count")));
		
		List<DicModality> ms = CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_MODALITY_KEY, new 
			IDataLoader(){ 
			    public Object load() { 
			      return DicModality.dao.find("select * from dic_modality where deleted=0"); 
			    }
			}
		);
		
		return ms.stream().filter(m->{
			if(deptid.intValue()==m.getDepartmentid().intValue()&&StrKit.equals("modality", m.getRole())){
				Integer count=map.get(m.getType());
				log.info(m.getType());
				if(count!=null&&count>0){
					m.setGroupid(count);// use groupid instead rowspan
					map.put(m.getType(), 0);
				} else if(count==0){
					m.setGroupid(0);
				} else{
					m.setGroupid(1);
				}
				return true;
			} else{
				return false;
			}
		}).collect(Collectors.toList()).stream().sorted(Comparator.comparing(DicModality::getType)).collect(Collectors.toList());
	}
	
	public boolean saveDeptPostShift(Integer deptid,String postcode,String shiftids){
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			boolean ret=true;
			Db.delete("delete from dept_post_shift where deptid=? and postcode=?", deptid,postcode);
			if(StrKit.notBlank(shiftids)){
				String ids[]=shiftids.split(",");
				for(String id : ids){
					DeptPostShift dds = new DeptPostShift();
					dds.setDeptid(deptid);
					dds.setPostcode(postcode);
					dds.setShiftid(Integer.valueOf(id));
					ret=ret&dds.remove("id").save();
				}
			}
			return ret;
		});
	}
	
	public List<Integer> saveDeptShiftWork(DeptShiftWork sw ,String userids,String employeeids) {
		List<Integer> ids=new ArrayList<Integer>();
		Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			boolean ret=true;
			if(userids.indexOf(",")<0){
				sw.setUserid(Integer.valueOf(userids));
				sw.setEmployeeid(Integer.valueOf(employeeids));
				ret=ret&sw.remove("id").save();
				if(ret)ids.add(sw.getId());
			} else {
//				List<DeptShiftWork> list=new ArrayList<DeptShiftWork>();
				String useridarr[]=userids.split(",");
				String employeeidarr[]=employeeids.split(",");
				for(int i=0;i<useridarr.length;i++){
					if(StrKit.notBlank(useridarr[i])){
						DeptShiftWork tmp=new DeptShiftWork();
						tmp.setUserid(Integer.valueOf(useridarr[i]));
						tmp.setEmployeeid(Integer.valueOf(employeeidarr[i]));
						tmp.setDeptid(sw.getDeptid());
						tmp.setInstitutionid(sw.getInstitutionid());
						tmp.setPostcode(sw.getPostcode());
						tmp.setShiftid(sw.getShiftid());
						tmp.setShiftName(sw.getShiftName());
						tmp.setWorkdate(sw.getWorkdate());
						tmp.setWorktimeid(sw.getWorktimeid());
						tmp.setWorktimeName(sw.getWorktimeName());
						ret=ret&tmp.save();
						if(ret)ids.add(tmp.getId());
//						list.add(tmp);
					}
				}
//				Db.batchSave(list, list.size());
			}
			return ret;
		});
		
		return ids;
	}
	
	public boolean removeEmployeeFromShift(String ids){
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			boolean ret=true;
			if(ids.indexOf(",")<0){
				ret=ret&DeptShiftWork.dao.deleteById(ids);
			} else {
				String idarr[]=ids.split(",");
				for(int i=0,len=idarr.length;i<len;i++){
					if(StrKit.notBlank(idarr[i])){
						ret=ret&DeptShiftWork.dao.deleteById(idarr[i]);
					}
				}
			}
			return ret;
		});
	}
	
	public Boolean copyLastWeekShifts(Integer deptid,String postcode,String mondayStr,Integer offset,Map<String, String> retmap){
		
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()->{
			boolean ret=true;
			LocalDate monday=LocalDate.parse(mondayStr, DateTimeFormatter.ISO_DATE);
			monday=monday.plusDays(offset*7);

			List<DeptShiftWork> list =DeptShiftWork.dao.find("select * from dept_shift_work where deptid=? and postcode=? and workdate>=? and workdate <=?",
					deptid, postcode, monday.minusDays(7).format(DateTimeFormatter.ISO_DATE),monday.minusDays(1).format(DateTimeFormatter.ISO_DATE));
			if(list.size()>0){
				Db.delete("delete from dept_shift_work where deptid=? and postcode=? and workdate>=? and workdate <=?",
						deptid, postcode, monday.format(DateTimeFormatter.ISO_DATE),monday.plusDays(6).format(DateTimeFormatter.ISO_DATE));
			}
			else{
				retmap.put("reslut", "0");
			}
			List<DeptShiftWork> tmplist =new ArrayList<DeptShiftWork>();
			for(DeptShiftWork dsw:list){
				DeptShiftWork tmp=new DeptShiftWork();
				try {
					BeanUtils.copyProperties(tmp, dsw);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tmp.remove("id");
				tmp.remove("createtime");
				Calendar c = Calendar.getInstance();
				c.setTime(tmp.getWorkdate());
				c.add(Calendar.DAY_OF_MONTH, 7);
				tmp.setWorkdate(c.getTime());
				tmplist.add(tmp);
			}
			Db.batchSave(tmplist, tmplist.size());
			return ret;
		});
	}
	
	public DeptReportTaskSetting getReportTaskSetting() {
		return DeptReportTaskSetting.dao.findFirst("select top 1 * from dept_report_task_setting order by createtime desc");
	}
	
	public Integer saveReportTaskSetting(Integer id,String attr,String value){
		boolean ret=true;
		DeptReportTaskSetting setting=new DeptReportTaskSetting();
		setting.set(attr, value);
		if(id!=null) {
			setting.setId(id);
			ret=ret & setting.update();
		} else {
			ret=ret & setting.save();
		}
		if(ret) {
			WorkforceServer.INSTANCE.loadConfiguration();
			return setting.getId();
		} else {
			return null;
		}
	}
	
	public Map<String,List<Record>> showPhysicianScore(Integer deptid,List<Physician> physicianlist,List<Physician> preauditphysicianlist,List<Physician> auditphysicianlist){
		List<Record> list=Db.find("select dept_shift.id as shiftid,dept_shift.name as shiftname,dept_shift.type,dept_worktime.id as worktimeid,dept_worktime.name as worktimename "
				+ "from dept_post_shift,dept_shift,dept_shift_worktime,dept_worktime "
				+ "where dept_post_shift.shiftid=dept_shift.id and dept_shift.id=dept_shift_worktime.shiftid "
				+ "and dept_worktime.id=dept_shift_worktime.worktimeid and dept_post_shift.deptid=? and dept_post_shift.postcode='D' "
				+ "order by dept_shift.id,dept_shift.type,dept_worktime.starttime",deptid);
		if(list!=null&&list.size()>0) {
			
			Map<String,List<Physician>> physicianmap=physicianlist.stream().collect(Collectors.groupingBy(x->x.getShiftid()+"-"+x.getWorktimeid()));
			Map<String,List<Physician>> preauditphysicianmap=preauditphysicianlist.stream().collect(Collectors.groupingBy(x->x.getShiftid()+"-"+x.getWorktimeid()));
			Map<String,List<Physician>> auditphysicianmap=auditphysicianlist.stream().collect(Collectors.groupingBy(x->x.getShiftid()+"-"+x.getWorktimeid()));
			
			list.stream().forEach(x->{
				String type=x.getStr("type");
				if(StrKit.notBlank(type)){
					int colspan=0;
					switch(type) {
						case "23"://报告医生
							
							if(physicianmap!=null) {
								List<Physician> li=physicianmap.get(x.getInt("shiftid")+"-"+x.getInt("worktimeid"));
								if(li!=null) {
									colspan=li.size();
								}
							}
							x.set("colspan", colspan);
							break;
						case "27"://初审医生
							if(physicianmap!=null) {
								List<Physician> li=preauditphysicianmap.get(x.getInt("shiftid")+"-"+x.getInt("worktimeid"));
								if(li!=null) {
									colspan=li.size();
								}
							}
							x.set("colspan", colspan);
							break;
						case "31"://审核医生
							if(physicianmap!=null) {
								List<Physician> li=auditphysicianmap.get(x.getInt("shiftid")+"-"+x.getInt("worktimeid"));
								if(li!=null) {
									colspan=li.size();
								}
							}
							x.set("colspan", colspan);
							break;
					}
				}
			});
			
			return list.stream().collect(Collectors.groupingBy(x->x.getStr("type")));
		} else {
			return null;
		}
	}
	
	public Map<String,List<Record>> showPhysicianCount(Integer deptid,List<Physician> physicianlist,List<Physician> preauditphysicianlist,List<Physician> auditphysicianlist){
		List<Record> list=Db.find("select dept_shift.id as shiftid,dept_shift.name as shiftname,dept_shift.type,dept_worktime.id as worktimeid,dept_worktime.name as worktimename "
				+ "from dept_post_shift,dept_shift,dept_shift_worktime,dept_worktime "
				+ "where dept_post_shift.shiftid=dept_shift.id and dept_shift.id=dept_shift_worktime.shiftid "
				+ "and dept_worktime.id=dept_shift_worktime.worktimeid and dept_post_shift.deptid=? and dept_post_shift.postcode='D' "
				+ "order by dept_shift.id,dept_shift.type,dept_worktime.starttime",deptid);
		if(list!=null&&list.size()>0) {
			
			Map<String,List<Physician>> physicianmap=physicianlist.stream().collect(Collectors.groupingBy(x->x.getShiftid()+"-"+x.getWorktimeid()));
			Map<String,List<Physician>> preauditphysicianmap=preauditphysicianlist.stream().collect(Collectors.groupingBy(x->x.getShiftid()+"-"+x.getWorktimeid()));
			Map<String,List<Physician>> auditphysicianmap=auditphysicianlist.stream().collect(Collectors.groupingBy(x->x.getShiftid()+"-"+x.getWorktimeid()));
			
			list.stream().forEach(x->{
				String type=x.getStr("type");
				if(StrKit.notBlank(type)){
					int colspan=0;
					switch(type) {
						case "23"://报告医生
							
							if(physicianmap!=null) {
								List<Physician> li=physicianmap.get(x.getInt("shiftid")+"-"+x.getInt("worktimeid"));
								if(li!=null) {
									colspan=li.size();
								}
							}
							x.set("colspan", colspan);
							x.set("rowspan", colspan);
							break;
						case "27"://初审医生
							if(physicianmap!=null) {
								List<Physician> li=preauditphysicianmap.get(x.getInt("shiftid")+"-"+x.getInt("worktimeid"));
								if(li!=null) {
									colspan=li.size();
								}
							}
							x.set("colspan", colspan);
							x.set("rowspan", colspan);
							break;
						case "31"://审核医生
							if(physicianmap!=null) {
								List<Physician> li=auditphysicianmap.get(x.getInt("shiftid")+"-"+x.getInt("worktimeid"));
								if(li!=null) {
									colspan=li.size();
								}
							}
							x.set("colspan", colspan);
							x.set("rowspan", colspan);
							break;
					}
				}
			});
			
			return list.stream().collect(Collectors.groupingBy(x->x.getStr("type")));
		} else {
			return null;
		}
	}
	
	public Map<String,Integer> showPhysicianCountByModality(List<Physician> physicianlist,List<Physician> preauditphysicianlist,List<Physician> auditphysicianlist){
		Map<String,List<Physician>> physicianmap=physicianlist.stream().collect(Collectors.groupingBy(x->x.getShiftid()+"-"+x.getWorktimeid()));
		Map<String,List<Physician>> preauditphysicianmap=preauditphysicianlist.stream().collect(Collectors.groupingBy(x->x.getShiftid()+"-"+x.getWorktimeid()));
		Map<String,List<Physician>> auditphysicianmap=auditphysicianlist.stream().collect(Collectors.groupingBy(x->x.getShiftid()+"-"+x.getWorktimeid()));
		Map<String,Integer> map=new HashMap<String,Integer>();
		physicianlist.stream().forEach(x->{
			String key=x.getShiftid()+"-"+x.getWorktimeid();
			List<Physician> val=physicianmap.get(key);
			if(val!=null&&val.size()>0){
				map.put(x.getDept_id()+"-23-"+x.getShiftid()+"-"+x.getWorktimeid()+"-"+x.getPhysician_id(), val.size());
				physicianmap.put(key, null);
			} else{
				map.put(x.getDept_id()+"-23-"+x.getShiftid()+"-"+x.getWorktimeid()+"-"+x.getPhysician_id(), 0);
			}
		});
		preauditphysicianlist.stream().forEach(x->{
			String key=x.getShiftid()+"-"+x.getWorktimeid();
			List<Physician> val=preauditphysicianmap.get(key);
			if(val!=null&&val.size()>0){
				map.put(x.getDept_id()+"-27-"+x.getShiftid()+"-"+x.getWorktimeid()+"-"+x.getPhysician_id(), val.size());
				preauditphysicianmap.put(key, null);
			} else{
				map.put(x.getDept_id()+"-27-"+x.getShiftid()+"-"+x.getWorktimeid()+"-"+x.getPhysician_id(), 0);
			}
		});
		auditphysicianlist.stream().forEach(x->{
			String key=x.getShiftid()+"-"+x.getWorktimeid();
			List<Physician> val=auditphysicianmap.get(key);
			if(val!=null&&val.size()>0){
				map.put(x.getDept_id()+"-31-"+x.getShiftid()+"-"+x.getWorktimeid()+"-"+x.getPhysician_id(), val.size());
				auditphysicianmap.put(key, null);
			} else{
				map.put(x.getDept_id()+"-31-"+x.getShiftid()+"-"+x.getWorktimeid()+"-"+x.getPhysician_id(), 0);
			}
		});
		return map;
	}
	
	public List<Record> getPhysicians(Integer deptid,String name,String syscode_lan){
		if(deptid!=null){
			List<Object> para=new ArrayList<>();
			para.add(deptid);
			String where="";
			if(StrKit.notBlank(name)){
				where+=" and (users.username like CONCAT('%',?,'%') or dic_employee.name like CONCAT('%',?,'%'))";
				para.add(name);
				para.add(name);
			}
			return Db.find("select users.id as userid,dic_employee.id as employeeid,dic_employee.name "
					//+ ",(SELECT "+syscode_lan+" FROM syscode WHERE syscode.code = dic_employee.profession AND syscode.type = '0019') AS profession_display "
					+ "from users,dic_employee "
					+ "where users.employeefk=dic_employee.id and dic_employee.deptfk=? and users.active=1 and dic_employee.profession='D' "
					+ where,para.toArray());
		} else {
			return new ArrayList<Record>();
		}
	}
	
	public List<DicModality> getModalites(){
		List<DicModality> list= (List<DicModality>)CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_MODALITY_KEY);
		if(list!=null){
			return list.stream().filter(x->StrKit.equals(x.getRole(), "modality")).collect(Collectors.toList());
		} else {
			return new ArrayList<DicModality>();
		}
	}
	
	public List<Record> getExamItems(){
		List<Record> ret = new ArrayList<Record>();
		List<DicExamitem> list=(List<DicExamitem>)CacheKit.get(CacheName.DICCACHE, CacheName.DICCACHE_EXAMITEM_KEY);
		Record root = new Record();
		if(list!=null){
			Map<String, List<DicExamitem>> map=list.stream().collect(Collectors.groupingBy(x->x.getType()));
			root.set("id", -1);
			root.set("text","全部");
			root.set("state","open");
			root.set("leaf", "0");
			List<Record> modalitys=new ArrayList<Record>();
			map.forEach((k, v) -> {
				Record node = new Record();
				node.set("id", k);
				node.set("text",k);
				node.set("state","closed");
				node.set("leaf", "0");
				List<Record> childrens=new ArrayList<Record>();
				v.forEach(x->{
					Record subnode = new Record();
					subnode.set("id", x.getId());
					subnode.set("text",x.getItemName());
					subnode.set("state","open");
					subnode.set("leaf", "1");
					childrens.add(subnode);
				});
				node.set("children",childrens);
				modalitys.add(node);
			});
			root.set("children",modalitys);
		}
		ret.add(root);
		//log.info(ret);
		return ret;
	}
	
	public List<Object> getSubscriptionRules(Integer userid){
		List<Object> ret=new ArrayList<>();
		DeptSubscriptionRule rule=DeptSubscriptionRule.dao.findFirst("select top 1 * from dept_subscription_rule where userid=?",userid);
		if(rule!=null){
			ret.add(rule);
			ret.add(DeptSubscriptionModality.dao.find("select * from dept_subscription_modality where userid=?",userid));
			ret.add(DeptSubscriptionExamitem.dao.find("select * from dept_subscription_examitem where userid=?",userid));
		}
		return ret;
	}
	
	public boolean saveSubscriptionRule(Integer userid,Integer employeeid,String modality,String modalityids,String itemids){
		return Db.tx(Connection.TRANSACTION_READ_COMMITTED,()-> {
			boolean ret=true;
			DeptSubscriptionRule rule=DeptSubscriptionRule.dao.findFirst("select top 1 * from dept_subscription_rule where userid=? order by createtime desc",userid);
			
			if(StrKit.isBlank(modality)&&StrKit.isBlank(modalityids)&&StrKit.isBlank(itemids)){
				if(rule!=null){
					ret=ret&rule.delete();
				}
			}
			else{
				if(rule==null){
					rule=new DeptSubscriptionRule();
					rule.setUserid(userid);
					rule.setEmployeeid(employeeid);
					ret=ret&rule.save();
				}
			}
			
			if(StrKit.notBlank(modality)){
				rule.setModalities(modality);
				ret=ret&rule.update();
			}
			Db.delete("delete from dept_subscription_modality where ruleid=?", rule.getId());
			if(StrKit.notBlank(modalityids)){
				String ids[]=modalityids.split(",");
				List<DeptSubscriptionModality> list=new ArrayList<DeptSubscriptionModality>();
				for(int i=0,len=ids.length;i<len;i++){
					if(StrKit.notBlank(ids[i])){
						DeptSubscriptionModality mod=new DeptSubscriptionModality();
						mod.setUserid(userid);
						mod.setEmployeeid(employeeid);
						mod.setRuleid(rule.getId());
						mod.setModalityid(Integer.valueOf(ids[i]));
						list.add(mod);
					}
				}
				Db.batchSave(list, list.size());
			}
			Db.delete("delete from dept_subscription_examitem where ruleid=?", rule.getId());
			if(StrKit.notBlank(itemids)){
				String ids[]=itemids.split(",");
				List<DeptSubscriptionExamitem> list=new ArrayList<DeptSubscriptionExamitem>();
				for(int i=0,len=ids.length;i<len;i++){
					if(StrKit.notBlank(ids[i])){
						DeptSubscriptionExamitem item= new DeptSubscriptionExamitem();
						item.setUserid(userid);
						item.setEmployeeid(employeeid);
						item.setRuleid(rule.getId());
						item.setItemid(Integer.valueOf(ids[i]));
						list.add(item);
					}
				}
				Db.batchSave(list, list.size());
			}
			
			return ret;
		});
		
	}
}
