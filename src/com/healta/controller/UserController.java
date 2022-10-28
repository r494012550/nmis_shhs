package com.healta.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.healta.constant.SessionKey;
import com.healta.constant.UserAuditType;
import com.healta.plugin.UserAuditLogsKit;
import com.healta.util.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.healta.constant.TableNameConstant;
import com.healta.interceptor.AuthInterceptor;
import com.healta.license.VerifyLicense;
import com.healta.listener.MySessionContext;
import com.healta.model.Authority;
import com.healta.model.AuthorityResource;
import com.healta.model.DicModality;
import com.healta.model.PasswordPolicy;
import com.healta.model.Resource;
import com.healta.model.Role;
import com.healta.model.User;
import com.healta.model.UserProfiles;
import com.healta.plugin.shiro.ShiroPrincipal;
import com.healta.service.UserService;
import com.healta.vo.WebsocketVO;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 用户相关控制层
 * 
 * @author Administrator
 *
 */
public class UserController extends Controller {
	private final static Logger log = Logger.getLogger(UserController.class);
	/**
	 * 业务层
	 */
	private static UserService sv=new UserService();
	

	/**
	 * 跳转登录页面
	 */
	@Clear(AuthInterceptor.class)
	public void index() {
		String language=PropKit.use("system.properties").get("language");
		Res res = I18n.use(language);
		getSession().setAttribute("language", language);
		getSession().setAttribute("locale", new SerializeRes(res));	
		setAttr("language", language);
		setAttr("port", getRequest().getServerPort());
		renderJsp("login.jsp");
	}
	
	@ActionKey("/login")
	@Clear(AuthInterceptor.class)
	public void login(){

		String language= getPara("language");
		log.info("language="+language+";"+getCookie("userLanguage"));
		Res res=null;
		if(StrKit.notBlank(language)){
			res = I18n.use(language);
		}
		else if(StrKit.notBlank(getCookie("userLanguage"))){
			language=getCookie("userLanguage");
			res = I18n.use(language);
		}
		else{
			language=PropKit.use("system.properties").get("language");//Locale.getDefault().getLanguage()+"_"+Locale.getDefault().getCountry();
			res = I18n.use(language);
		}
		getSession().setAttribute("language", language);
		getSession().setAttribute("locale", new SerializeRes(res));
		if(StrKit.equals(language, "zh_CN")){
			getSession().setAttribute("syscode_lan", "name_zh");
		}
		else{
			getSession().setAttribute("syscode_lan", "name_en");
		}
		setAttr("language", language);
		setAttr("port", getRequest().getServerPort());
		renderJsp("login.jsp");
	}
	
	@ActionKey("/signout")
	public void signout(){
	    
	    // 退出时将session，sessionId，userId之间的关联清除
//        User user =  (User) getSession().getAttribute("user");
//        if (user != null) {
//            SessionMapUtils.removeSession(SessionMapUtils.getSessionId(user.getId()));
//            SessionMapUtils.removeUser(user.getId());
//        }
        Subject subject = SecurityUtils.getSubject();
		//用户审计日志，注销
		UserAuditLogsKit.addLog(((ShiroPrincipal)subject.getPrincipal()).getUser().getId(), UserAuditType.LOGOUT, IPKit.getIP(getRequest()));
        subject.logout();
		
		String language= getPara("language");
		log.info("language="+language);
		Res res=null;
		if(language!=null){
			res = I18n.use(language);
		}
		else if(StrKit.notBlank(getCookie("userLanguage"))){
			language=getCookie("userLanguage");
			res = I18n.use(language);
		}
		else{
			language=PropKit.use("system.properties").get("language");//Locale.getDefault().getLanguage();
			res = I18n.use(language);
		}
		getSession().setAttribute("language", language);
		getSession().setAttribute("locale", new SerializeRes(res));
		if(StrKit.equals(language, "zh_CN")){
			getSession().setAttribute("syscode_lan", "name_zh");
		}
		else{
			getSession().setAttribute("syscode_lan", "name_en");
		}
		setAttr("language", language);
		setAttr("port", getRequest().getServerPort());
		renderJsp("login.jsp");
	}

	/**
	 * 登录操作
	 */
	@Clear(AuthInterceptor.class)
	public void dologin() {
		log.info("---dologin----");
		if(getSession().getAttribute("language")==null) {
			String language=PropKit.use("system.properties").get("language");
			Res res = I18n.use(language);
			getSession().setAttribute("language", language);
			getSession().setAttribute("locale", new SerializeRes(res));	
			setAttr("language", language);
			setAttr("port", getRequest().getServerPort());
			renderJsp("login.jsp");
		} else {
			User user=getModel(User.class,"",true);
			Res res=I18n.use(getSessionAttr("language"));
			String error="";
			Subject subject = SecurityUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(user.getStr("username"),user.getStr("password"));
			try {
				subject.login(token);
			} catch(UnknownAccountException ue) {
				token.clear();
				error = res.get("login.loginfailednotexistaccount");
			} catch(IncorrectCredentialsException ie) {
				//ie.printStackTrace();
				token.clear();
				error = res.get("login.loginfailedinvalidpassword");
			} catch(LockedAccountException e){
				token.clear();
				error = res.get("login.lockedaccount");
			} catch(ExpiredCredentialsException e){
				token.clear();
				error = res.get("login.expiredaccount");
			} catch(DisabledAccountException e){
				token.clear();
				error = res.get("login.disabledaccount");
			} catch(RuntimeException re) {
				//re.printStackTrace();
				token.clear();
				error = res.get("login.loginfailed");
			}
			//log.info(CacheKit.get("shiro_activeSessionCache", subject.getSession().getId()));
			//log.info("---"+((ShiroPrincipal)subject.getPrincipal()).getUser());
	
			if(StringUtils.isEmpty(error)) {
	
				User user1=((ShiroPrincipal)subject.getPrincipal()).getUser();//User.dao.findFirst("select top 1 * from users where username=?",user.getStr("username"));
			
				String url=null;
				String sql="select distinct role_module.module from userrole,role_module "
						+ "where userrole.roleid=role_module.roleid and userrole.userid=? and role_module.module!='chat'";
					
				List<Record> list=Db.find(sql,user1.getId());
				if(list!=null&&list.size()>0){
					url="/"+list.get(0).getStr("module");
					if(StrKit.notBlank(user1.getDefaultmodule())&&VerifyLicense.getSiteInfo().getFunctions_str().indexOf(user1.getDefaultmodule())>=0){
						for(Record record:list){
							if(StrKit.equals(record.getStr("module"), user1.getDefaultmodule())){
								url="/"+user1.getDefaultmodule();
								break;
							}
						}
					}
					 // 根据用户id查找sessionid是否存在
	                String oldSessionId = MySessionContext.getSessionId(user1.getId());
	                // 获取现在登入的sessionId
	                String newSessionId = (String) subject.getSession().getId();
	                log.info("oldSessionId="+oldSessionId+";newSessionId="+newSessionId);
	                    // 此用户已经登入过了
	                if (StrKit.notBlank(oldSessionId)&&!oldSessionId.equals(newSessionId)) {
	                    // 两个sessionId不一样，删除此账号前面登入的session
	                    // 根据sessionId获取session
	                    Session shiroSession = MySessionContext.getSession(oldSessionId);
	                    log.info("oldSession="+shiroSession);
	                    if (shiroSession != null) {
	                        // 移除放在session的用户信息
	                    	shiroSession.removeAttribute("user");
	                    	shiroSession.setTimeout(1);
	                    	shiroSession.stop();
	                    	Object useragent=shiroSession.getAttribute("clientip_user_agent");
	                    	if(!StrKit.equals(useragent!=null?useragent.toString():"", IPKit.getIP(getRequest())+getRequest().getHeader("user-agent"))) {
	                    	// 提示此用户，他的账号已在其他地方进行登入
	                    		WebSocketUtils.sendQueueMessage(user1.getId(), new WebsocketVO(WebsocketVO.ACCOUNT_ELSE_LOGIN,user1.getId()+"").toJson());
	                    	}
	                    }
	                }
	                MySessionContext.addUserSession(user1.getId(), newSessionId);
					
					subject.getSession().setAttribute("user", user1);
					subject.getSession().setAttribute("modules", list);
					subject.getSession().setAttribute(SessionKey.USER_DAC, sv.getUserDAC(user1.getId()));
					subject.getSession().setAttribute("myprofiles", UserProfiles.dao.findFirst("select top 1 * from userprofiles where userid=?", user1.getId()));
					subject.getSession().setAttribute("clientip_user_agent", IPKit.getIP(getRequest())+getRequest().getHeader("user-agent"));
					//2020 05 06 hx
					subject.getSession().setAttribute("userName", user1.getName());
					
					// 是否有打开报告权限
					Boolean enadleOpenReport = true;
					try {
					    // 查看此用户是否为主任，主任可打开所有报告
						subject.checkPermission("open_report");
					} catch (Exception e) {
						enadleOpenReport = false;
					}
					
					if (enadleOpenReport) {
						// 打开报告等级
						// 1，报告医生 只能打开自己写的报告；
						// 2，审核医生 可以打开报告医生写的报告，但打开不了其他审核医生审核后的报告
						// 3，主任级别 所有报告都能打开
						String openGrade = "1";
						boolean openReport = true;
						try {
							openGrade = "3";
						    // 查看此用户是否为主任，主任可打开所有报告
							subject.checkPermission("open_report_all");
						} catch (Exception e) {
							openGrade = "2";
							openReport = false;
						}
						if (!openReport) {
							try {
								openGrade = "2";
							    // 查看此用户是否为审核医生
								subject.checkPermission("open_report_audit");
							} catch (Exception e) {
								openGrade = "1";
								openReport = false;
							}
						}
						log.info("user open grade：" + openGrade);
						subject.getSession().setAttribute("openGrade", openGrade);
					}
					//用户审计日志，登录
					UserAuditLogsKit.addLog(user1.getId(), UserAuditType.LOGIN, IPKit.getIP(getRequest()));
					//subject.getSession().setTimeout(600000);
					JSONObject ret=new JSONObject();
					ret.put("url", getRequest().getContextPath()+url);
					ret.put("ctx", getRequest().getContextPath());
					renderText(ResultUtil.success(ret).toJsonStr());
				}
				else{
					error=res.get("login.useenomodule");
					renderText(ResultUtil.fail(-1, error).toJsonStr());
				}
				
			} else {
				//renderJson(ResultUtil.fail(-1, error));
				renderText(ResultUtil.fail(-1, error).toJsonStr());
	//			redirect("/login");
			}
		}
	}
	
	/**
	 * 退出登录
	 * @throws IOException 
	 */
	public void log() throws IOException{
		removeSessionAttr("user"); 
	    redirect("login.html");
	}
	
	public void getAllUsers(){
		try {
			renderJson(sv.findUsers(getPara("value"),getPara("deleted")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
		
	}
	
	public void getModules(){
		try {
			ArrayList<Record> list=new ArrayList<Record>();
			//String modulesstr=PropKit.use("system.properties").get("sys_modules_cn");
			for(String module:VerifyLicense.getSiteInfo().getModules()){
				Record record=new Record();
				record.set("module", module);
				record.set("displayname", SyscodeKit.INSTANCE.getCodeDisplay("0018", module, getSessionAttr("language")));
				list.add(record);
			}
			renderJson(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void getAllRole(){
		try {
			Integer userid=getParaToInt("userid");
			String value=getPara("value");
			
			renderJson(sv.findRole(userid, value));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	/**
	 *  根据员工的id查出相应的角色
	 */
	public void getRoleByEmployee() {
	    try {
	        Integer employeeid = getParaToInt("employeeid");
	        Integer userid = getParaToInt("userid");
            renderJson(sv.findRoleByEmployee(employeeid, userid));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(ResultUtil.fail(1, e.getMessage()));
        }
    }
	
	/**
	 * (根据module)获取系统资源
	 */
	 public void getAllResource(){ 
		  File file = new File(PathKit.getWebRootPath()+"\\WEB-INF\\classes\\"+"system_source.json");
		  String module=getPara("module");
		  String str = "";
		  try {
			  String input = FileUtils.readFileToString(file, "UTF-8");//UTF-8 GBK

			  if(StringUtils.isNotBlank(module)) {
				  JSONArray jsonArray = JSONObject.parseArray(input);
				  JSONArray returnArray = new JSONArray();
				  for(Object json:jsonArray) {
					  JSONObject jsonObject = (JSONObject) json;
					  //jsonObject.getString("module") module不存在时返回null所以可以不判断
					  if(jsonObject.containsKey("module")&&StringUtils.equals(jsonObject.getString("module"), module)) {
						  returnArray.add(jsonObject);
					  } 
				  }
				  //import org.json.JSONArray;
				  //import org.json.JSONObject;
				  /*JSONArray jsonArray = new JSONArray(input);
				  JSONArray returnArray = new JSONArray();
				  for(int i=0;i<jsonArray.length();i++) {
					  JSONObject json = jsonArray.getJSONObject(i);
					  if(json.has("module")&&StringUtils.equals(json.getString("module"), module)) {
						  returnArray.put(json);
					  }  
				  }*/
				  str = returnArray.toString();
			  }else {
				  str = input;
			  }
			  
		  } 
		  catch (Exception e) { 
			  e.printStackTrace(); 
		  } 
		  
		  renderJson(str); 
	  }
	 
	public void saveResource(){
		try {
			Resource res=getModel(Resource.class,"",true);
//			res.setId(getParaToInt("resid"));
			if (sv.saveResource(res)) {
				renderJson(ResultUtil.success());
			}else {
				SerializeRes r=(SerializeRes)getSessionAttr("locale");
				renderJson(ResultUtil.fail(1, r.get("savefailed")));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void deleteResource(){
		try {
			if (sv.deleteResource(getParaToInt("resid"))) {
				renderJson(ResultUtil.success());
			}else {
				SerializeRes r=(SerializeRes)getSessionAttr("locale");
				renderJson(ResultUtil.fail(1, r.get("deletefailed")));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void getAllAuth(){
		try {
			String value=getPara("value");
			
			renderJson(sv.findAuthority(value,getSessionAttr("language")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void saveAuth(){
		try {
			String auids=getPara("auids");
			Authority au=getModel(Authority.class,"",true);
			if (sv.saveAuthority(au,auids)) {
				renderJson(ResultUtil.success());
			}else {
				SerializeRes r=(SerializeRes)getSessionAttr("locale");
				renderJson(ResultUtil.fail(1, r.get("savefailed")));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	/**
	 *  校验权限名称是否相同
	 */
	public void checkAuthName(){
		renderJson(ResultUtil.success(sv.checkAuthorityName(getInt("autid"), getPara("auname"))));
	}
	
	public void getResourceByAuthorityId(){
		try {
			Integer auid=getParaToInt("auid");
			
			renderJson(sv.findResourceByAuthorityId(auid).toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void deleteAuthority(){
		try {
			if (sv.deleteAuthority(getParaToInt("auid"))) {
				renderJson(ResultUtil.success());
			}else {
				SerializeRes r=(SerializeRes)getSessionAttr("locale");
				renderJson(ResultUtil.fail(1, r.get("deletefailed")));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	/**
	 * 校验角色名是否相同
	 */
	public void checkRoleName(){
		renderJson(ResultUtil.success(sv.checkRoleName(getInt("roleid"), getPara("rolename"))));
	}
	
	public void saveRole(){
		try {
			SerializeRes r=(SerializeRes)getSessionAttr("locale");
			if (validateToken("saveroleToken")) {

				String roleids=getPara("roleids");
				String modulesids=getPara("modulesids");
				String institutionids=getPara("institutionids");
				
				Role role=getModel(Role.class,"",true);				
				if (sv.saveRole(role, roleids,modulesids, institutionids)) {
					renderJson(ResultUtil.success());
				}else {
					renderJson(ResultUtil.fail(1, r.get("savefailed")));
				}
			}
			else{
				renderJson(ResultUtil.success(r.get("donotrepeatsubmit")));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void getAuthorityByRoleId(){
		try {
			Integer roleid=getParaToInt("roleid");
			String module=getPara("module");
			log.info("roleid="+roleid);
			
			if(roleid>0) {
				renderJson(sv.findAuthorityByRoleId(module,roleid,getSessionAttr("language")));
			}
			else{
				if(StrKit.notBlank(module)) {
					renderJson(sv.findAuthorityByRoleId(module,roleid,getSessionAttr("language")));
				}
				else {
					renderNull();
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void getModulesByRoleId(){
		try {
			Integer roleid=getParaToInt("roleid");
			setAttr("roleid",roleid);
			setAttr("list", sv.findModulesByRoleId(roleid,getSessionAttr("language")));
			renderJsp("/view/admin/user/role_module.jsp");
			
			//renderJson(sv.findModulesByRoleId(roleid).toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void deleteRole(){
		try {
			
//			UserService us=enhance(UserService.class);
			
			
			if (sv.deleteRole(getParaToInt("roleid"))) {
				renderJson(ResultUtil.success());
			}else {
				SerializeRes r=(SerializeRes)getSessionAttr("locale");
				renderJson(ResultUtil.fail(1, r.get("deletefailed")));
			}
		} catch (Exception e) {	
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void saveUser(){
		try {
			SerializeRes r=(SerializeRes)getSessionAttr("locale");
			if (validateToken("saveuserToken")) {

				User user=getModel(User.class,"",true);
				String rolenames=getPara("rolenames");
				
//				log.info(user.getPassword());
				user.set("role", rolenames);
				user.entryptPassword(user);
			
				String rolessstr=getPara("roles");
				
				String[] modalitys = getParaValues("modality");
				String modalitystr = "";
				if(modalitys != null && modalitys.length > 0) {
					for(String str : modalitys) {
						if(StringUtils.isNotBlank(str)) {
							modalitystr += "'" + str + "',";
						}
					}
				}
				
				if(StringUtils.isNotBlank(modalitystr)) {
					modalitystr = modalitystr.substring(0, modalitystr.length()-1);
				}
				
				user.setModality(modalitystr);
				
				if (sv.saveUser(user, rolessstr)) {
					renderJson(ResultUtil.success(user));
				}else {
					renderJson(ResultUtil.fail(1, r.get("savefailed")));
				}
			}
			else{
				renderJson(ResultUtil.success(r.get("donotrepeatsubmit")));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void getUserrole(){
		try {
			
			if(StringUtils.isNotEmpty(getPara("userid"))){
				renderJson(sv.getUserrole(getParaToInt("userid")));
			}
			else {
				renderNull();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void deleteUser(){
		try {
			if (sv.deleteUser(getParaToInt("userid"))) {
				renderJson(ResultUtil.success());
			}else {
				SerializeRes r=(SerializeRes)getSessionAttr("locale");
				renderJson(ResultUtil.fail(1, r.get("deletefailed")));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	
	public void checkUsername(){
		setAttr("exist",User.dao.checkName(getPara("username"),getParaToInt("userid")));
		renderJson();
	}
	
	public void goEditUser(){
		createToken("saveuserToken");
		if(StringUtils.isNotEmpty(getPara("id"))){
		    // 跳用户编辑所需要的数据
			int id=getParaToInt("id");
			User user=User.dao.findById(id);
			setAttr("user", user);
			
			String modalitystr = user.getModality();
			if(StringUtils.isNotBlank(modalitystr)) {
				log.info("before modalitystr:"+modalitystr);
				modalitystr = modalitystr.replaceAll("'", "");
				log.info("after modalitystr:"+modalitystr);
				setAttr("modalitystr", modalitystr);
			}
			
			if(StrKit.isBlank(user.getAvatar())){
				user.setAvatar("themes/head.ico");
			}
			else{
				user.setAvatar(getSession().getServletContext().getContextPath()+"/image/getAvatarImg?path="+user.getAvatar());
			}
			setAttr("hasExpiredate", user.getExpiredate()!=null?"1":"0");
		}else {
		    // 跳用户添加所需要的数据
			User user=new User();
			user.setEmployeefk(getParaToInt("employeefk"));
			user.setName(getPara("name"));
			user.setActive("1");
			user.setLock("0");
			user.setAvatar("themes/head.ico");
			setAttr("user", user);
		}
			renderJsp("/view/admin/user/editUser.jsp");
	}
	
	/**
	 *  跳转到添加/编辑角色页面
	 */
	public void goEditRole(){
		createToken("saveroleToken");
		if(StringUtils.isNotEmpty(getPara("id"))){
		    // 编辑
			int id=getParaToInt("id");
			Role user=Role.dao.findById(id);
			setAttr("id", id);
			setAttr("roleid", id);
			setAttr("rolename", user.getRolename());
			setAttr("institutionList", sv.findInstitutionByRoleId(id));
		} else {
		    // 添加
		    setAttr("institutionList", sv.findInstitutionByRoleId(null));
			setAttr("roleid", -1);
		}
		renderJsp("/view/admin/user/editRole.jsp");
	}
	
	public void goEditAuthority(){
		createToken("saveauthorityToken");
		if(StringUtils.isNotEmpty(getPara("id"))){
			int id=getParaToInt("id");
			Authority au=Authority.dao.findById(id);
			
			setAttr("id", id);
			setAttr("auid", id);
			setAttr("module", au.getModule());
			setAttr("name", au.getName());
			setAttr("description", au.getDescription());
			
			List<AuthorityResource> list=AuthorityResource.dao.find("SELECT * FROM authority_resource WHERE authority_id=?",id);
			String auids="";
			for(AuthorityResource li:list) {
				auids+=li.getResourceId()+",";
			}
			setAttr("auids", auids.substring(0, auids.length()-1));
		}
		else{
			setAttr("auid", -1);
		}
		renderJsp("/view/admin/user/editAuthority.jsp");
	}
	
	/*public void getUsersByRole(){
		try {
			renderJson(sv.getUsersByRole(getParaToInt("roleid")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}*/
	
	public void getUsersByRoles(){
		try {
			renderJson(sv.getUsersByRoles(getPara("roleid")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void existAccount() {
		try {
			renderJson(ResultUtil.success(sv.existAccount(getParaToInt("employeefk"))));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	
	/**
	 * 跳转至选择头像页面
	 */
	public void toSelectAvatar() {
		renderJsp("/view/admin/user/selectAvatar.jsp");
	}
	
	/**
	 * 用户头像上传
	 */
	public void avatarUpload(){
		try {
			//User user=(User)getSession().getAttribute("user");
			String img=getPara("imgBase64");
			String header ="data:image";  
	        String[] imageArr=img.split(",");  
	        if(imageArr[0].contains(header)){//是img的  
	            // 去掉头部  
	        	img=imageArr[1];  
	        }
			String imagefile=StrKit.getRandomUUID()+"_"+new Date().getTime()+".jpg";
			File file = new File(PropKit.use("system.properties").get("tempdir")+System.getProperty("file.separator")+"userAvatar"+ System.getProperty("file.separator")+imagefile);
			try {
				FileUtils.writeByteArrayToFile(file, Base64.decodeBase64(img));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			renderJson(ResultUtil.success(getSession().getServletContext().getContextPath()+"/image/getAvatarImg?tmp=1&path=" + imagefile));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}
	
	/**
	 * 跳转至修改头像页面
	 */
	public void toUpdateUserAvatar(){
		renderJsp("/view/admin/user/updateUserAvatar.jsp");
	}
	
	/**
	 * 修改用户头像
	 */
	public void updateUserAvatar() {
		try {
			String img=getPara("imgBase64");
			String header ="data:image";  
	        String[] imageArr=img.split(",");  
	        if(imageArr[0].contains(header)){//是img的  
	            // 去掉头部  
	        	img=imageArr[1];  
	        }
			String imagefile=StrKit.getRandomUUID()+"_"+new Date().getTime()+".png";
			File file = new File(PropKit.use("system.properties").get("tempdir")+System.getProperty("file.separator")+"userAvatar"+ System.getProperty("file.separator")+imagefile);
			String userAvatar = file.getName();
			try {
				FileUtils.writeByteArrayToFile(file, Base64.decodeBase64(img));
				FileUtils.copyFile(file, new File(PropKit.use("system.properties").get("sysdir")+"//userAvatar//"+ userAvatar));
				FileUtils.deleteQuietly(file);
				User user=(User)getSession().getAttribute("user");
				Db.update("update "+ TableNameConstant.CHAT_USERS + " set avatar = ? WHERE id = ?",userAvatar, user.getId());
				user.setAvatar(userAvatar);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			renderJson(ResultUtil.success(getSession().getServletContext().getContextPath()+"/image/getAvatarImg?path=" + imagefile));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}
	/**
	 * 查询设备信息
	 */
	public void findDicModality() {
		renderJson(DicModality.dao.find("select * from dic_modality where deleted=0"));
	}
	
	public void modifyModality() {
		User user=(User)getSession().getAttribute("user");
		String[] dicModality=getParaValues("dicModality");
		String defaultDicModality=getPara("defaultDicModality");
		Integer userId=user.getId();
		String str="";
		List<UserProfiles> userprofiless=UserProfiles.dao.find("select * from userprofiles where userid="+userId);
		if(userprofiless!=null&&userprofiless.size()==1) {
			UserProfiles userprofiles=userprofiless.get(0);
			for (int i = 0; i < dicModality.length; i++) {
				 if(i==0) {
					 str=dicModality[i];
				 }else {
					 str+=","+dicModality[i];
				 }
			}
			userprofiles.setDicModality(str);
			userprofiles.setDefaultDicModality(defaultDicModality);
			userprofiles.update();
			renderJson(ResultUtil.success());
		}else {
			renderJson(ResultUtil.fail("用户信息不存在或有多条记录！"));
		}
	}
	
	
	public void goModifyUserPassword() {
		User user = User.dao.findById(getParaToInt("userid"));
		setAttr("user", user);

		renderJsp("/view/admin/user/editUserPassword.jsp");
	}
	
	public void modifyUserPassword() {
		try {
			User user = getModel(User.class,"",true);
			sv.modifyUserPassword(user);
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	
	/**
	 * 验证密码复杂性
	 */
	public void checkComplexPassword() {
		String password = getPara("password");
		Integer flag = 0;
		PasswordPolicy policy = PasswordPolicy.dao.findFirst("SELECT TOP 1 * FROM password_policy");
		if(StringUtils.equals("1", policy.getEnableComplexPwd())) {
			//密码长度
			Integer passwordLength = Integer.parseInt(StringUtils.isBlank(policy.getPasswordLength())?"12":policy.getPasswordLength());
			if(password.length() < passwordLength) {
				flag = 1;
			}
			//是否包含大小写
			if(StringUtils.equals("1", policy.getContainCasePwd())) {
				boolean hasUpperCase = password.chars().anyMatch((int ch) -> Character.isUpperCase((char) ch));
				boolean hasLowerCase = password.chars().anyMatch((int ch) -> Character.isLowerCase((char) ch));
				flag = (hasUpperCase && hasLowerCase) ? flag : 2;
			}
			//是否包含数字
			if(StringUtils.equals("1", policy.getContainDigitPwd())) {
				String containDigitRegex = ".*[0-9].*";
				flag = password.matches(containDigitRegex) ? flag : 3;
			}
			//是否包含字母
			if(StringUtils.equals("1", policy.getContainLetterPwd())) {
				String containLetterRegex = ".*[a-zA-z].*";
				flag = password.matches(containLetterRegex) ? flag : 4;
			}
		}
		renderJson(flag);
	}
	/**
	  *上下班打卡
	  *type 1 上班，2 下班
	 */
	public void checkWorkAttendance() {
		try {
			String type = getPara("type");
			User user=(User)getSession().getAttribute("user");
			sv.checkWorkAttendance(type,user);
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//	public void updateUserCa() {
//		String username=getPara("username");
//		String oldCaPasswrod=getPara("oldCaPasswrod");
//		String CaPasswrod=getPara("CaPasswrod");
//		User user=User.dao.findFirst("select * from users where username='"+username+"' and key_password='"+oldCaPasswrod+"'");
//		if(user!=null) {
//			user.setKeyPassword(CaPasswrod);
//			user.update();
//			renderJson(ResultUtil.success());
//		}else {
//			renderJson(ResultUtil.fail(1, "原密码错误！！"));
//		}
//	}
	
//	public void findCakeypasswordByDevSn() {
//		String username = getPara("username");
//		String CAflag = PropKit.use("system.properties").get("CAflag");
//		User user=User.dao.findFirst("select * from users where username=? and deleted=0",username);
//		if (user!=null) {
//			if ("true".equals(CAflag) && user.getCaFlag() != null && 1 == user.getCaFlag()) {
//				renderJson(ResultUtil.success(user));
//			} else {
//				renderJson(ResultUtil.fail(1,"没开启CA登录"));
//			}
//		} else {
//			renderJson(ResultUtil.fail(-1, "用户名不存在！！"));
//		}
//	}
	public void toRelogin() {
		if(StrKit.notBlank(get("userid"))){
			if(StrKit.notBlank(MySessionContext.getSessionId(getInt("userid")))){
				renderJson(ResultUtil.success());//userid对应的session还存在，无需重新登录
			} else{
				renderJson(ResultUtil.fail("需要重新登录"));
			}
		} else {
			String language= getPara("language");
			log.info("language="+language+";"+getCookie("userLanguage"));
			Res res=null;
			if(StrKit.notBlank(language)){
				res = I18n.use(language);
			}
			else if(StrKit.notBlank(getCookie("userLanguage"))){
				language=getCookie("userLanguage");
				res = I18n.use(language);
			}
			else{
				language=PropKit.use("system.properties").get("language");//Locale.getDefault().getLanguage()+"_"+Locale.getDefault().getCountry();
				res = I18n.use(language);
			}
			getSession().setAttribute("language", language);
			getSession().setAttribute("locale", new SerializeRes(res));
			if(StrKit.equals(language, "zh_CN")){
				getSession().setAttribute("syscode_lan", "name_zh");
			}
			else{
				getSession().setAttribute("syscode_lan", "name_en");
			}
			setAttr("language", language);
			setAttr("port", getRequest().getServerPort());
			createToken("dologin");
			renderJsp("relogin.jsp");
		}
	}
}
