package com.healta.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.healta.model.CheckWorkAttendance;
import com.healta.model.User;
import com.healta.model.UserProfiles;
import com.healta.render.ImageRender;
import com.healta.util.ResultUtil;
import com.healta.util.SyscodeKit;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class ProfileController extends Controller {
	private final static Logger log = Logger.getLogger(ProfileController.class);
	
	public void toMyConfig(){
		//User user=(User)getSession().getAttribute("user");
		UserProfiles up=(UserProfiles)getSession().getAttribute("myprofiles");
		if(up!=null){
//			setAttr("openimageatonce", up.getOpenimageatonce());
//			setAttr("beforeSaveScan", up.getBeforeSaveScan());
//			setAttr("afterSavePrint", up.getAfterSavePrint());
//			setAttr("studyform_print_copies", up.getStudyformPrintCopies());
//			setAttr("dicModality", up.getDicModality());
//			setAttr("defaultDicModality", up.getDefaultDicModality());
			setAttr("up",up);
		}
		//setAttr("userRole", user.getRole());
		//setAttr("defaultmodule", user.getDefaultmodule());
		renderJsp("/view/myConfig.jsp");
	}
	
	public void toCheckWorkAttendance(){
		LocalDate today=LocalDate.now();
		setAttr("today", today.format(DateTimeFormatter.ISO_LOCAL_DATE));
		renderJsp("/view/checkWorkAttendance.jsp");
	}
	
	public void toesign(){
		UserProfiles up=(UserProfiles)getSession().getAttribute("myprofiles");
		if(up!=null){
			setAttr("enableesign", up.getEnableesign());
			setAttr("esignpath", up.getSignfilepath());
		}
		renderJsp("/view/e_signature.jsp");
	}
	
	public void modifyPassword(){
		try {
			User user=(User)getSession().getAttribute("user");
			String oldpassword=getPara("oldpassword");
			String newpassword=getPara("newpassword");
//			String newpassword1=getPara("newpassword1");
			String error="";
			Subject subject = SecurityUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(user.getStr("username"),oldpassword);
			try {
				subject.login(token);
			} catch(UnknownAccountException ue) {
				token.clear();
				error = "登录失败，您输入的账号不存在";
			} catch(IncorrectCredentialsException ie) {
				ie.printStackTrace();
				token.clear();
				error = "passworderror";
			} catch(RuntimeException re) {
				re.printStackTrace();
				token.clear();
				error = "登录失败";
			}
			if(StringUtils.isEmpty(error)){
				user.setPassword(newpassword);
				user.entryptPassword(user);
				user.update();
				renderJson(ResultUtil.success());
			}
			else{
				renderJson(ResultUtil.fail(1, error));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
		
	}
	
	public void getMyModules(){
		try {
			List<Record> ret=new ArrayList<Record>();
			List<Record> list=(List<Record>)getSession().getAttribute("modules");
			for(Record re:list){
				re.set("module_name", SyscodeKit.INSTANCE.getCodeDisplay("0018", re.getStr("module"), getSession().getAttribute("language").toString()));
				ret.add(re);
			}
			
			renderJson(ret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void setMyDefaultModule(){
		try {
			User user=(User)getSession().getAttribute("user");
			user.setDefaultmodule(getPara("defaultmodule"));
			user.update();
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void saveUserProfiles(){
		try {
			User user=(User)getSession().getAttribute("user");
			String profiles=getPara("profiles");
			String value=getPara("value");
			UserProfiles up=(UserProfiles)getSession().getAttribute("myprofiles");
			if(up==null){
				up=new UserProfiles();
				up.setUserid(user.getId());
				up.save();
			}
			if(profiles.contains("copies")) {
				up.set(profiles, getParaToInt("value"));
			}else if(StringUtils.equals(profiles, "pos_or_neg")){
				up.set(profiles, StringUtils.equals("1", value)?"n":"p");
			}else {
				up.set(profiles, value);
			}
			up.set(profiles, value);
			up.update();
			getSession().setAttribute("myprofiles",up);
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void updateSign(){
		try {
			User user=(User)getSession().getAttribute("user");

			String img=getPara("imgBase64");
			
//			if(StringUtils.isNotEmpty(img)){
			
				String header ="data:image";
		        String[] imageArr=img.split(",");  
		        if(imageArr[0].contains(header)){//是img的  
		            // 去掉头部  
		        	img=imageArr[1];  
		        }
				
				String imagefile=user.getUsername()+"_"+new Date().getTime()+".png";
				try {
					FileUtils.writeByteArrayToFile(new File(PropKit.use("system.properties").get("e-signpath")+"\\"+imagefile), Base64.decodeBase64(img));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
	         			}
				
				UserProfiles up=(UserProfiles)getSession().getAttribute("myprofiles");
				
				if(up==null){
					up=new UserProfiles();
					up.setUserid(user.getId());
					up.save();
				}
				
				if(up.getSignfilepath()!=null){
					File oldfile=new File(up.getSignfilepath());
					if(oldfile.exists()){
						FileUtils.deleteQuietly(oldfile);
					}
				}
				
				up.setSignfilepath(imagefile);
				up.update();
				getSession().setAttribute("myprofiles",up);
//			}
			
			renderJson(ResultUtil.success(getSession().getServletContext().getContextPath()+"/profile/getSignImg?path="+imagefile));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	public void getSignImg(){
		String path=getPara("path");
		if(StringUtils.isNotEmpty(path)){
			render(new ImageRender(PropKit.use("system.properties").get("e-signpath")+"\\"+path));
		}
		else{
			renderNull();
		}
	}
	
	//上班打卡
		public void gotoWork() {
			try {
				User user = (User) getSession().getAttribute("user");
				String format = "YYYY-MM-dd";
				String formatDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
				CheckWorkAttendance cwa=CheckWorkAttendance.dao.findFirst("select * from check_work_attendance where convert(char(10),create_time,120)=? and userid=?",formatDateTime,user.getId());
				if(cwa==null) {
					cwa=new CheckWorkAttendance();
					cwa.setUserid(user.getId());
					cwa.setStartWorkTime(new Date());
					cwa.setCreateTime(new Date());
					cwa.remove("id").save();
				}
				renderJson(ResultUtil.success());
			} catch (Exception e) {
				e.printStackTrace();
				renderJson(ResultUtil.fail(1, e.getMessage()));
			}
			
		}
		//下班打卡
		public void gooffWork() {
			try {
				User user = (User) getSession().getAttribute("user");
				String format = "YYYY-MM-dd";
				String formatDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
				CheckWorkAttendance cwa=CheckWorkAttendance.dao.findFirst("select * from check_work_attendance where convert(char(10),create_time,120)=? and userid=?",formatDateTime,user.getId());
				if(cwa==null) {
					cwa=new CheckWorkAttendance();
					cwa.setUserid(user.getId());
					cwa.setEndWorkTime(new Date());
					cwa.setCreateTime(new Date());
					cwa.remove("id").save();
				}else {
				    cwa.setEndWorkTime(new Date());
				    cwa.update();

				}
				renderJson(ResultUtil.success());
			} catch (Exception e) {
				e.printStackTrace();
				renderJson(ResultUtil.fail(1, e.getMessage()));
			
		}
	}
		
	//考勤查询
	public void findCheckWorkAttendance() {
		String name=getPara("name");
		String sdate=getPara("sdate");
		String edate=getPara("edate");
		String sql="select c.start_work_time,c.end_work_time,u.name,convert(char(10),create_time,120) as create_time from check_work_attendance c,users u where c.userid=u.id";
		List<Object> list = new ArrayList<Object>();
		if(StringUtils.isNotBlank(name)) {
			sql+=" and u.name=?";
			list.add(name);
		}
		if(StringUtils.isNotBlank(sdate)) {
			sql+=" and convert(char(10),create_time,120)>=?";
			list.add(sdate);
		}
		if(StringUtils.isNotBlank(edate)) {
			sql+=" and convert(char(10),create_time,120)<=?";
			list.add(edate);
		}
		List<Record> datas=Db.find(sql,list.toArray());
		renderJson(datas);
	}
}
