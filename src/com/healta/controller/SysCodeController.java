package com.healta.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.healta.model.PasswordPolicy;
import com.healta.model.Syscode;
import com.healta.service.SysCodeService;
import com.healta.util.ResultUtil;
import com.healta.util.SyscodeKit;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

public class SysCodeController extends Controller {
	private static final Logger log = Logger.getLogger(SysCodeController.class);
	private final static SysCodeService sv=new SysCodeService();

	public void getCode(){
		log.info("getCode---"+getPara("type"));
		String type=getPara("type");
		String addemptydata=getPara("addempty");
		HashMap<String ,List<Syscode>> map = SyscodeKit.INSTANCE.loadSyscodeFromCache();
		
		List<Syscode> retcodes=new ArrayList<Syscode>();
		if(type!=null&&map!=null){
			retcodes=map.get(type);
		}
		
		if(StrKit.notBlank(addemptydata)){
			ArrayList<Syscode> tmp=new ArrayList<Syscode>(retcodes);
			Syscode empty=new Syscode();
			empty.setCode("");
			empty.setNameZh("全部");
			empty.setNameEn("All");
			tmp.add(0, empty);
			renderJson(tmp);
		}
		else{
			renderJson(retcodes);
		}
		
	}
	
	public void findSyscodeGroup(){
		try{
			renderJson(sv.findSyscodeGroup());
		
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	
	public void findSyscode(){
		try{
			renderJson(sv.findSyscode(getPara("value")));
		
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void openEditSysCode(){
		setAttr("id", getPara("id"));
		setAttr("code", getPara("code"));
		setAttr("name_zh", getPara("name_zh"));
		setAttr("name_en", getPara("name_en"));
		setAttr("parent", getPara("parent"));
		renderJsp("/view/admin/system/editSyscode.jsp");
	}
	
	public void openEditSysCodeGroup(){
		setAttr("id", getPara("id"));
		setAttr("name_zh", getPara("name_zh"));
		setAttr("name_en", getPara("name_en"));
		setAttr("type",getPara("type"));
		setAttr("parent", getPara("parent"));
		renderJsp("/view/admin/system/editSyscodeGroup.jsp");
	}
	
	public void saveEditSysCode(){
		try{
			Syscode sc=getModel(Syscode.class, "", true);
			int parent=getParaToInt("parent");
			if(sv.saveSysCode(sc,parent)){
				renderJson(ResultUtil.success());
			}
			else{
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void saveEditSysCodeGroup(){
		try{
			Syscode sc=getModel(Syscode.class, "", true);
			if(sv.saveSysCodeGroup(sc)){
				renderJson(ResultUtil.success());
			}
			else{
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	public void deleteSysCode(){
		try{
			if(Syscode.dao.deleteById(getParaToInt("id"))){
				renderJson(ResultUtil.success());
			}
			else{
				renderJson(ResultUtil.fail(""));
			}
		} catch (Exception e) {
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
//	
//	public static List<Syscode> loadSysCode(){
//		return Syscode.dao.find("select * from syscode");
//	}
	
	
	public void passwordPolicyConfig() {
		try {
			Date date = new Date();
			PasswordPolicy policy = PasswordPolicy.dao.findFirst("SELECT TOP 1 * FROM password_policy");
			if(policy == null) {
				policy = new PasswordPolicy();
				policy.setModifytime(date);
				policy.remove("id").save();
			}
			policy.set(getPara("name"), getPara("policy"));
			policy.setModifytime(date);
			policy.update();
			
			renderJson(ResultUtil.success());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}

}
