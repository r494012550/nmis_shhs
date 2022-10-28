package com.healta.controller;

import com.healta.license.VerifyLicense;
import com.healta.model.PasswordPolicy;
import com.healta.model.RisTrigger;
import com.healta.model.User;
import com.healta.model.UserProfiles;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

public class AdminController extends Controller {
	
	public void index(){
		User user = (User) getSession().getAttribute("user");
		setAttr("username", user.getUsername());
		setAttr("name", user.getStr("name"));
		setAttr("user_id", user.getId());
		setAttr("sr_support",VerifyLicense.hasFunction("srtemplate"));
		setAttr("up", UserProfiles.dao.findFirst("select * from userprofiles where userid = ?", user.getId()));
		renderJsp("/view/admin/admin.jsp");
	}
	
	public void user(){
		renderJsp("user/user.jsp");
	}
	
	public void role(){
		renderJsp("user/role.jsp");
	}
	
	public void authority(){
		renderJsp("user/authority.jsp");
	}
	
	public void resource(){
		renderJsp("user/resource.jsp");
	}
	
	public void srLibrary(){
		renderJsp("template/srLibrary.jsp");
	}
	public void xslLibrary(){
		renderJsp("template/xslLibrary.jsp");
	}
	
	public void srtemplatemanage(){
		renderJsp("template/srtemplateManage.jsp");
	}
	public void templatemanage(){
		renderJsp("template/templatemanage.jsp");
	}
	
	public void modules(){
		renderJsp("system/modules.jsp");
	}
	
	public void statisticsmanage(){
		renderJsp("statistics/statisticsManage.jsp");
	}
	
	public void syscode() {
		renderJsp("system/syscode.jsp");
	}
	public void dicmanage() {
		renderJsp("dic/modality.jsp");
	}
	
	public void examitemdic() {
		renderJsp("dic/examitemdic.jsp");
	}
	
	/**
	 * 课题管理
	 */	
	//课题维护
	public void projectManagement(){
		renderJsp("project/projectManagement.jsp");
	}
	//课题权限
	public void toAuthority(){
		renderJsp("project/projectAuthority.jsp");
	}
	//课题角色
	public void projectRole(){
		renderJsp("project/projectRole.jsp");
	}
	//表单管理
	public void formManage() {
		renderJsp("project/formManage.jsp");
	}
	//表单库
	public void formLibrary() {
		renderJsp("project/formLibrary.jsp");
	}
	//表单审批
	public void applyForJoinGroup() {
		renderJsp("project/joinGroupApplication.jsp");
	}
	//课题资源
	public void projectResource() {
		renderJsp("project/projectResource.jsp");
	}
	//规则维护
	public void joinGroupRule() {
		setAttr("enable_sr_joingroup_rules", PropKit.use("system.properties").getBoolean("enable_sr_joingroup_rules",false));
		renderJsp("project/joinGroupRules.jsp");
	}
		
	//商品管理
	public void comGoods() {
		renderJsp("dic/com_goods.jsp");
	}
	//商品品种
	public void comGoodsVariety() {
		renderJsp("dic/com_goods_variety.jsp");
	}
	
	//通用字典
	public void toCommonDic(){
		renderJsp("dic/dic_common.jsp"); 
	}

	/**
	  *  机构管理
	 */
	public void institution() {
        renderJsp("dic/institution.jsp");
    }
	public void equipgroup() {
		renderJsp("dic/group.jsp");
	}
	public void organdic() {
		renderJsp("dic/organdic.jsp");
	}	
	
	public void label() {
		renderJsp("system/label.jsp");
	}
	public void colormanage() {
		renderJsp("system/colormanage.jsp");
	}
	public void department() {
		renderJsp("dic/department.jsp");
	}
	public void employee() {
		renderJsp("dic/employee.jsp");
	}
	public void printer() {
		renderJsp("system/printer.jsp");
	}
	public void client() {
		renderJsp("system/client.jsp");
	}
	
	public void risevents(){
		
		setAttr("rt", RisTrigger.dao.getRisTriggers());
//		setAttr("triggername",);
		renderJsp("system/risevents.jsp");
	}
	public void task() {
		renderJsp("/view/admin/system/task.jsp");
	}
	
	public void printtemplate() {
		renderJsp("system/printtemplate.jsp");
	}
	
	public void urgentexplain() {
		renderJsp("system/urgentexplain.jsp");
	}
	
	public void reportcheckerror(){
		renderJsp("system/reportCheckErrorRules.jsp");
	}
	
	public void diccalling() {
		renderJsp("dic/dic_callinginfo.jsp");
	}
	
	// 密码策略
	public void passwordPolicy() {
		PasswordPolicy passwordpolicy=PasswordPolicy.dao.findFirst("SELECT  * FROM password_policy");
		setAttr("policy",passwordpolicy);
		renderJsp("/view/admin/system/passwordPolicy.jsp");
	}
}
