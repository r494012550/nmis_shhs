package com.healta.service;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.healta.model.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.healta.util.SyscodeKit;
import com.healta.license.VerifyLicense;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;


public class UserService {
	private final static Logger log = Logger.getLogger(UserService.class);
	
	/**
	 * 获取所有用户信息
	 * @param value
	 * @param deleted
	 * @return
	 */
	public List<User> findUsers(String value, String deleted) {
		String where = "";
		List<Object> list=new ArrayList<Object>();
		if(StringUtils.isNotBlank(value)){
		    where+=" and username like CONCAT('%',?,'%') or name like CONCAT('%',?,'%') ";
			list.add(value);
			list.add(value);
		}
		if (StringUtils.isNotBlank(deleted)) {
		    where+=" and deleted = ? ";
            list.add(deleted);
        }
		String sql = "select * from users where 0=0" + where;
		return User.dao.find(sql, list.toArray());
	}
	
    public List<Role> findRole(Integer userid,String value) {
		
		String select ="select * from role";
		if (userid!=null) {
		    select+=" where id not in(select roleid from userrole where userid="+userid+")";
		}
		if (!"".equals(value)&&value!=null) {
			select+=" where rolename like '%"+value+"%'";
		}
		
		List<Role> roles = Role.dao.find(select);
		return roles;
	}
    
    /**
     * 根据员工的id查出相应的角色
     * @param employeeid
     * @return
     */
    public List<Role> findRoleByEmployee(Integer employeeid, Integer userid) {
        List<Role> roles = new ArrayList<Role>();
        if (employeeid != null) {
            DicEmployee dicEmployee = DicEmployee.dao.findById(employeeid);
         // 此员工绑定了机构，查询的角色为只有此机构下的角色
//            if (dicEmployee.getInstitutionid() != null) {
                // 此员工绑定了机构，查询的角色为只有此机构下的角色
//                List<DicInstitutionRole> institutionRoles = DicInstitutionRole.dao.find("select * from dic_institution_role where institutionid = " + dicEmployee.getInstitutionid());
//                if (institutionRoles.size() > 0 ) {
//                    for (DicInstitutionRole institutionRole: institutionRoles) {
//                        // 查询此角色是否与该用户关联
//                        Role role = Role.dao.findById(institutionRole.getRoleid());
//                        if (userid != null) {
//                            // 编辑用户
//                            List<Userrole> userrole = Userrole.dao.find("select * from userrole where userid = " + userid + " and roleid = " + role.getId());
//                            if (userrole.size() == 0) {
//                                // 此用户未与该角色关联
//                                roles.add(role);
//                            }
//                        } else {
//                            // 添加用户
//                            roles.add(role);
//                        }
//                    }
//                }
//            	
//            } else {
//                // 此员工未绑定机构，查询所有的角色
//                roles = Role.dao.find("select * from role");
//            }
            
            roles=Role.dao.find("select role.* from role ,dic_institution_role where role.id=dic_institution_role.roleid and "
        			+ "dic_institution_role.institutionid=? and not exists (select null from userrole where role.id=userrole.roleid and userrole.userid=?)",
        			dicEmployee.getInstitutionid(),userid);
        }
        return roles;
    }
	
	public List<Resource> findResource(String value){
		String query="select * from resource";
		if(!"".equals(value)&&value!=null){
			query+=" where name like '%"+value+"%'";
		}
		return Resource.dao.find(query);
	}
	
	
	public boolean saveResource(Resource res){
		boolean ret=true;
		try{
			if(res.getId()==null){
				res.remove("id").save();
			}
			else{
				res.update();
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			ret=false;
		}
		return ret;
	}
	
	
	public boolean deleteResource(int id){
		boolean ret=true;
		try{
			Resource.dao.deleteById(id);
		}
		catch(Exception ex){
			ex.printStackTrace();
			ret=false;
		}
		return ret;
	}
	
	public List<Record> findAuthority(String value,String language){
		String query="select * from authority";
		if(!"".equals(value)&&value!=null){
			query+=" where name like '%"+value+"%' or description like '%"+value+"%'";
		}
		
		List<Record> ret=Db.find(query);
		for(Record record:ret){
			record.set("modulename", SyscodeKit.INSTANCE.getCodeDisplay("0018", record.getStr("module"), language));
		}
		return ret;
	}
	
	@Before(Tx.class)
	public boolean saveAuthority(Authority au,String auids){
		boolean ret=true;
		try{
			if(au.getId()==null){
				au.remove("id").save();
			}
			else{
				au.update();
				Db.update("delete from authority_resource where authority_id="+au.getId());
			}
			
			if(auids!=null&&auids.length()>0){
				auids=auids.substring(0, auids.length()-1);
				String ids[]=auids.split(",");
				for(String resid:ids){
					if(!"".equals(resid)){
						AuthorityResource ar=new AuthorityResource();
						ar.set("authority_id", au.getId());
						ar.set("resource_id", resid);
						ar.save();
					}
				}
				
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			ret=false;
		}
		return ret;
	}
	
	public JSONArray findResourceByAuthorityId(int auid){
		
		List<Record> records=Db.find("select resource.*,(case when authority_resource.id is NULL then '0' else '1' end) as ck from resource  left join authority_resource on resource.id=authority_resource.resource_id and authority_resource.authority_id="+auid);
		
		JSONArray arr=new JSONArray();
		for(Record record: records){
			JSONObject obj=new JSONObject();
			
			obj.put("id", record.getInt("id"));
			obj.put("name", record.getStr("name"));
			obj.put("resource", record.getStr("resource"));
			obj.put("ck", record.getStr("ck"));
			arr.put(obj);
		}
		
		return arr;
	}
	
	public List<Record> findAuthorityByRoleId(String module,int roleid,String language){
		String where="";
		if(StrKit.notBlank(module)) {
			where=" where authority.module='"+module+"'";
		}
		else {
			where=" where authority.module in (select module from role_module where role_module.roleid="+roleid+")";
		}
		List<Record> records=Db.find("select authority.*,(case when role_authority.id is NULL then '0' else '1' end) as ck from authority "
				+ "left join role_authority on authority.id=role_authority.authority_id and role_authority.role_id="+roleid
				+ where);
		
//		JSONArray arr=new JSONArray();
		for(Record record: records){
//			JSONObject obj=new JSONObject();
//			
//			obj.put("id", record.getInt("id"));
//			obj.put("name", record.getStr("name"));
//			obj.put("description", record.getStr("description"));
//			obj.put("module", value)
//			obj.put("ck", record.getStr("ck"));
//			arr.put(obj);
			
			record.set("modulename", SyscodeKit.INSTANCE.getCodeDisplay("0018", record.getStr("module"), language));
		}
		
		return records;
	}
	
	public ArrayList<String[]> findModulesByRoleId(int roleid,String locale){
//		List<Record> records=Db.find("select modules.*,(case when role_module.id is NULL then '0' else '1' end) as ck from modules "
//				+ "left join role_module on modules.id=role_module.moduleid and role_module.roleid="+roleid);
		
		List<Record> records=Db.find("select * from role_module where roleid="+roleid);
		
		HashMap<String, String> map=new HashMap<String, String>();
		for(Record record: records){
			map.put(record.getStr("module"), record.getStr("module"));
		}
		
		String modulesstr=VerifyLicense.getSiteInfo().getModulesStr();//PropKit.use("system.properties").get("sys_modules");
		String[] modules=modulesstr.split(",");
		ArrayList<String[]> list=new ArrayList<String[]>();
		for(String module: modules){
			String[] arr=new String[3];
//			String[] str=module.split(":");
//			obj.put("id", record.getInt("id"));
			arr[0] =SyscodeKit.INSTANCE.getCodeDisplay("0018", module, locale);
			arr[1] =module;
			arr[2] =map.get(module)!=null?"1":"0";
			list.add(arr);
		}
		
		return list;
	}
	
	@Before(Tx.class)
	public boolean deleteAuthority(int auid){
		boolean succeed=Db.tx(new IAtom() {
			public boolean run() throws SQLException{
				boolean ret=true;
				try{
					Authority.dao.deleteById(auid);
					Db.update("delete from authority_resource where authority_id="+auid);
					Db.update("delete from role_authority where authority_id="+auid);
				}
				catch(Exception ex){
					ex.printStackTrace();
					ret=false;
				}
				
				return ret;
			}
		});
		return succeed;
	}
	
	@Before(Tx.class)
	public boolean saveRole(Role role,String roleids,String modulesids,String institutionids){
		boolean ret=true;
		try{
			if(role.getId()==null){
				role.remove("id").save();
			}
			else{
				role.update();
				Db.update("delete from role_authority where role_id="+role.getId());
				Db.update("delete from role_module where roleid="+role.getId());
				Db.update("delete from dic_institution_role where roleid="+role.getId());
			}
			
			if(roleids!=null&&roleids.length()>0){
				roleids=roleids.substring(0, roleids.length()-1);
				String ids[]=roleids.split(",");
				List<RoleAuthority> roleAuthorityList = new ArrayList<RoleAuthority>();
				for(String resid:ids){
					if(!"".equals(resid)){
						RoleAuthority ar=new RoleAuthority();
						ar.set("role_id", role.getId());
						ar.set("authority_id", resid);
						roleAuthorityList.add(ar);
					}
				}
				Db.batchSave(roleAuthorityList, roleAuthorityList.size());
			}
			if(modulesids!=null&&modulesids.length()>0){
				modulesids=modulesids.substring(0, modulesids.length()-1);
				String ids[]=modulesids.split(",");
				
				//String modulesstr=PropKit.use("system.properties").get("sys_modules_cn");
				
				for(String module:ids){
					if(StrKit.notBlank(module)){
						RoleModule rm=new RoleModule();
						rm.setRoleid(role.getId());
						rm.setModule(module);
						//rm.setModuleNamemodule(modulesstr.substring(modulesstr.lastIndexOf(",", modulesstr.lastIndexOf(":"+url))+1, modulesstr.indexOf(":"+url)));
						rm.save();
					}
				}
			}
			// 角色与机构绑定
			if (StringUtils.isNotBlank(institutionids)) {
			    institutionids = institutionids.substring(0, institutionids.length()-1);
                String ids[] = institutionids.split(",");
                for (String id:ids) {
                    if (StringUtils.isNotBlank(id)) {
                        DicInstitutionRole ir = new DicInstitutionRole();
                        ir.set("roleid", role.getId());
                        ir.set("institutionid", id);
                        ir.save();
                    }
                }
            }
		}
		catch(Exception ex){
			ex.printStackTrace();
			ret=false;
		}
		return ret;
	}
	
//	@Before(Tx.class)
	public boolean deleteRole(int roleid){	
			
		boolean succeed = Db.tx(new IAtom(){
			public boolean run() throws SQLException {
			    boolean ret = true;
			    try {
			        Db.update("delete from role_authority where role_id="+roleid);
	                Db.update("delete from userrole where roleid="+roleid);
	                Db.update("delete from role_module where roleid="+roleid);
	                Db.update("delete from dic_institution_role where roleid="+roleid);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                    return false;
                }
			    
	            ret = ret && Role.dao.deleteById(roleid);
	            return ret;
			}});
		return succeed;
	}
	
	//@Before(Tx.class)
	public boolean saveUser(User user,String rolessstr){
		boolean ret=true;
		try{
			
			User old=null;
			if(user.getId()!=null){
			    // 编辑用户
				if(StrKit.notBlank(user.getAvatar())){
					old=User.dao.findById(user.getId());
				}
				Db.update("delete from userrole where userid="+user.getId());
				user.setId(user.getId());
				user.setExpiredate(user.getExpiredate());
				user.update();
			}
			else{
			    // 添加用户,将用户的状态设为未删除，0代表未删除，1代表删除
			    user.setDeleted("0");
				user.remove("id").save();
			}
			
			String rolearr[]=rolessstr.split(",");
			for(String rolestr:rolearr){
				Userrole ur=new Userrole();
				ur.set("userid", user.getId());
				ur.set("roleid", Integer.valueOf(rolestr));
				ur.save();
			}
			
			if(old!=null){
				if(StrKit.notBlank(old.getAvatar())&&StrKit.equals(user.getAvatar(), old.getAvatar())){
					FileUtils.deleteQuietly(new File(PropKit.use("system.properties").get("sysdir")+System.getProperty("file.separator")+"userAvatar"+System.getProperty("file.separator")+old.getAvatar()));
				}
			}
			
			File av=new File(PropKit.use("system.properties").get("tempdir")+System.getProperty("file.separator")+"userAvatar"+System.getProperty("file.separator")+user.getAvatar());
			if(av.exists()){
				FileUtils.copyFile(av, new File(PropKit.use("system.properties").get("sysdir")+System.getProperty("file.separator")+"userAvatar"+System.getProperty("file.separator")+user.getAvatar()));
				FileUtils.deleteQuietly(av);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			ret=false;
		}
		return ret;
	}
	
	
	public List<Record> getUserrole(int userid){
		return Db.find("select userrole.roleid as id,role.rolename as rolename from userrole,role where userrole.roleid=role.id and userrole.userid="+userid);
	}
	
	//@Before(Tx.class)
	public boolean deleteUser(int userid){
		boolean ret=true;
		try{
			Db.update("delete from userrole where userid="+userid);
			// 将此用户的数据逻辑删除，1代表已删除，0代表未删除，active设置为未激活状态
			User.dao.findById(userid).set("active", "0").set("deleted", "1").update();
		}
		catch(Exception ex){
			ex.printStackTrace();
			ret=false;
		}
		return ret;
	}
	
/*	public List<Record> getUsersByRole(Integer roleid){
		return Db.find("select id,username,name,description from users left join userrole on users.id=userrole.userid where userrole.roleid=?",roleid);
	}
	*/
	
	public List<Record> getUsersByRoles(String roleids){
		return Db.find("select id,username,name,description from users left join userrole on users.id=userrole.userid where userrole.roleid in ("+roleids+")");
	}
	
	public User existAccount(Integer employeefk) {
		return User.dao.findFirst("SELECT * FROM users WHERE employeefk = ? and deleted = 0",employeefk);
	}
	
	/**
	 *  添加/编辑 角色时，对机构的查找
	 * @param roleid
	 * @return
	 */
	public ArrayList<String[]> findInstitutionByRoleId(Integer roleid) {
	    ArrayList<String[]> list=new ArrayList<String[]>();
	    List<Record> institutionList = Db.find("select di.id, di.name, ir.id as institutionroleid from " +
	            "dic_institution as di LEFT JOIN dic_institution_role as ir on ir.institutionid = di.id and ir.roleid = ? where di.deleted=0", roleid);
        if (institutionList.size() > 0) {
            for (Record institution : institutionList) {
                String[] arr = new String[3];
                // 机构的id
                arr[0] = institution.getStr("id");
                // 机构的名称
                arr[1] = institution.getStr("name");
                // 是否选中，1为选中，0为未选中
                arr[2] = institution.getStr("institutionroleid") != null? "1":"0";
                list.add(arr);
            }
        }
        return list;
    }
	
	/**
	 *  检验角色名是否存在
	 *  
	 * @param roleId
	 * @param roleName
	 * @return
	 */
	public Boolean checkRoleName(Integer roleId, String roleName) {
        List<Object> list = new ArrayList<Object>();
        String sql = "select * from role where rolename = ? ";
        list.add(roleName);
        if (roleId != null) {
            // 编辑
            sql += " and id != ?";
            list.add(roleId);
        }
        return Role.dao.find(sql,list.toArray()).size() > 0;
    }
	
	/**
     *  检验权限名是否存在
     *  
     * @param autId
     * @param autName
     * @return
     */
    public Boolean checkAuthorityName(Integer autId, String autName) {
        List<Object> list = new ArrayList<Object>();
        String sql = "select * from authority where name = ? ";
        list.add(autName);
        if (autId != null) {
            // 编辑
            sql += " and id != ?";
            list.add(autId);
        }
        return Authority.dao.find(sql,list.toArray()).size() > 0;
    }
    
    public boolean modifyUserPassword(User user) {
    	boolean ret = true;
    	if(user.getId() != null) {
			user.entryptPassword(user);
			ret = user.update();
		}
    	return ret;
    }

	public void checkWorkAttendance(String type, User user) {
		// TODO Auto-generated method stub
		
	}

	public UserDac getUserDAC(int userid) {
		return UserDac.dao.findFirst("select * from user_dac where userid=?",userid);
	}

	public List<UserAuditLogs> getAuditLogs(Integer userid,String from,String to,String types[]) {
		if(StrKit.isBlank(from)) {
			from= LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		}
		if(StrKit.isBlank(to)) {
			to=LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)+" 23:59:59";
		} else {
			to+=" 23:59:59";
		}
		String type="";
		if(types!=null) {
			for(int i=0;i<types.length;i++) {
				type+="'"+types[i]+"',";
			}
			type=" and audit_type in("+type.substring(0, type.length()-1)+")";
		}
		return UserAuditLogs.dao.find("select a.*,u.username,u.name from user_audit_logs a left join users u on a.userfk=u.id "
				+ "where userfk=? and audit_time>? and audit_time<=? "+type+" order by audit_time desc",userid,from,to);
	}
}
