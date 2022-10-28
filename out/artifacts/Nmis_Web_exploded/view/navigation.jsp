<%@page import="com.jfinal.kit.PropKit"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<%@page import="com.healta.license.VerifyLicense"%>
<script type="text/javascript" src="${ctx}/js/websocket.js?v=${vs}" ></script>
<script type="text/javascript" src="${ctx}/js/admin/notice.js"></script>
<%
	if(VerifyLicense.hasModule("chat")){
%>

<link rel="stylesheet" type="text/css" href="${ctx}/layui/css/layui.css?v=${vs}"/>
<script type="text/javascript" src="${ctx}/layui/layui.js?v=${vs}" ></script>
<script type="text/javascript" src="${ctx}/js/chat.js?v=${vs}" ></script>

<%
	}
%>
<title>Insert title here</title>
</head>
<body>
<%@page import="java.util.List"%> 
<%@page import="com.jfinal.plugin.activerecord.Record"%>
<%@page import="org.apache.commons.lang.StringUtils"%> 
<%@page import="com.healta.license.VerifyLicense"%> 
<%@page import="com.healta.util.SyscodeKit"%> 
<%@page import="org.apache.shiro.SecurityUtils"%> 
<%@page import="org.apache.shiro.subject.Subject"%>
<%@page import="com.jfinal.kit.PropKit"%>
<td width="80px"><img src="${ctx}/image/logo.png?v=${vs}"  alt="Healta" width="70px" height="22px"/></td><td>
<%
	Subject subject = SecurityUtils.getSubject();
	boolean existSch = PropKit.use("system.properties").getBoolean("exist_appointment_process",true);
	List<Record> list= (List<Record>)request.getSession().getAttribute("modules");
	String modulesstr="";
	for(Record r:list){
		modulesstr+=r.getStr("module")+",";
	}
		
		if(StringUtils.contains(VerifyLicense.getSiteInfo().getModulesStr(), "schedule")&&StringUtils.contains(modulesstr, "schedule")&&existSch){
			String displayname=SyscodeKit.INSTANCE.getCodeDisplay("0018", "schedule", request.getSession().getAttribute("language").toString());
			%>
			<a class="easyui-linkbutton" style="height:26px;width:90px;font-weight:bold;font-size:20px;color:#b8c7ce;" 
				data-options="toggle:true,group:'nav_group',plain:true" onClick="redirect('${ctx}/schedule','${user.username}');"><i class="icon iconfont icon-yuyue1"></i>&nbsp;<%=displayname%></a>
			<%
		} 
		if(StringUtils.contains(VerifyLicense.getSiteInfo().getModulesStr(), "register")&&StringUtils.contains(modulesstr, "register")&&!existSch){
			String displayname=SyscodeKit.INSTANCE.getCodeDisplay("0018", "register", request.getSession().getAttribute("language").toString());
			%>
			<a class="easyui-linkbutton" style="height:26px;width:90px;font-weight:bold;font-size:20px;color:#b8c7ce;" 
				data-options="toggle:true,group:'nav_group',plain:true" onClick="redirect('${ctx}/register','${user.username}');"><i class="icon iconfont icon-dengjijiuzhenqia" style="font-size:18px;"></i>&nbsp;<%=displayname%></a>
			<%
		}
		if(StringUtils.contains(VerifyLicense.getSiteInfo().getModulesStr(), "inquiring")&&StringUtils.contains(modulesstr, "inquiring")){
            String displayname=SyscodeKit.INSTANCE.getCodeDisplay("0018", "inquiring", request.getSession().getAttribute("language").toString());
            %>
            <a class="easyui-linkbutton" style="height:26px;width:90px;font-weight:bold;font-size:20px;color:#b8c7ce;" 
                data-options="toggle:true,group:'nav_group',plain:true" onClick="redirect('${ctx}/inquiring','${user.username}');"><i class="icon iconfont icon-wenzhen1"></i>&nbsp;<%=displayname%></a>
            <%
        }
        if(StringUtils.contains(VerifyLicense.getSiteInfo().getModulesStr(), "injection")&&StringUtils.contains(modulesstr, "injection")){
            String displayname=SyscodeKit.INSTANCE.getCodeDisplay("0018", "injection", request.getSession().getAttribute("language").toString());
            %>
            <a class="easyui-linkbutton" style="height:26px;width:90px;font-weight:bold;font-size:20px;color:#b8c7ce;" 
                data-options="toggle:true,group:'nav_group',plain:true" onClick="redirect('${ctx}/injection','${user.username}');"><i class="icon iconfont icon-zhushe1"></i>&nbsp;<%=displayname%></a>
            <%
        }
		if(StringUtils.contains(VerifyLicense.getSiteInfo().getModulesStr(), "examine")&&StringUtils.contains(modulesstr, "examine")){
			String displayname=SyscodeKit.INSTANCE.getCodeDisplay("0018", "examine", request.getSession().getAttribute("language").toString());
			%>
			<a class="easyui-linkbutton" style="height:26px;width:90px;font-weight:bold;font-size:20px;color:#b8c7ce;" 
				data-options="toggle:true,group:'nav_group',plain:true" onClick="redirect('${ctx}/examine','${user.username}');"><i class="icon iconfont icon-fangsheke1" style="font-size:15px;"></i>&nbsp;<%=displayname%></a>
			<%
		}
		if(StringUtils.contains(VerifyLicense.getSiteInfo().getModulesStr(), "worklist")&&StringUtils.contains(modulesstr, "worklist")){
			String displayname=SyscodeKit.INSTANCE.getCodeDisplay("0018", "worklist", request.getSession().getAttribute("language").toString());
			%>
			<a class="easyui-linkbutton" style="height:26px;width:90px;font-weight:bold;font-size:20px;color:#b8c7ce;" 
				data-options="toggle:true,group:'nav_group',plain:true" onClick="redirect('${ctx}/worklist','${user.username}');"><i class="icon iconfont icon-baogao"></i>&nbsp;<%=displayname%></a>
			<%
		}
		if(StringUtils.contains(VerifyLicense.getSiteInfo().getModulesStr(), "statistics")&&StringUtils.contains(modulesstr, "statistics")){
            String displayname=SyscodeKit.INSTANCE.getCodeDisplay("0018", "statistics", request.getSession().getAttribute("language").toString());
            %>
            <a class="easyui-linkbutton" style="height:26px;width:90px;font-weight:bold;font-size:20px;color:#b8c7ce;" 
                data-options="toggle:true,group:'nav_group',plain:true" onClick="redirect('${ctx}/statistics','${user.username}');"><i class="icon iconfont icon-tongji"></i>&nbsp;<%=displayname%></a>
            <%
        }
		if(StringUtils.contains(VerifyLicense.getSiteInfo().getModulesStr(), "distribution")&&StringUtils.contains(modulesstr, "distribution")){
            String displayname=SyscodeKit.INSTANCE.getCodeDisplay("0018", "distribution", request.getSession().getAttribute("language").toString());
            %>
            <%-- <a class="easyui-linkbutton" style="height:26px;width:90px;font-weight:bold;font-size:20px;color:#b8c7ce;" 
                data-options="toggle:true,group:'nav_group',plain:true" onClick="redirect('${ctx}/distribution','${user.username}');"><i class="icon iconfont icon-fafang"></i>&nbsp;<%=displayname%></a> --%>
            <%
        }
		if(StringUtils.contains(VerifyLicense.getSiteInfo().getModulesStr(), "followup")&&StringUtils.contains(modulesstr, "followup")){
            String displayname=SyscodeKit.INSTANCE.getCodeDisplay("0018", "followup", request.getSession().getAttribute("language").toString());
            %>
            <a class="easyui-linkbutton" style="height:26px;width:90px;font-weight:bold;font-size:20px;color:#b8c7ce;" 
                data-options="toggle:true,group:'nav_group',plain:true" onClick="redirect('${ctx}/followup','${user.username}');"><i class="icon iconfont icon-suifang"></i>&nbsp;<%=displayname%></a>
            <%
        }
		if(StringUtils.contains(VerifyLicense.getSiteInfo().getModulesStr(), "admin")&&StringUtils.contains(modulesstr, "admin")){
            String displayname=SyscodeKit.INSTANCE.getCodeDisplay("0018", "admin", request.getSession().getAttribute("language").toString());
            %>
            <a class="easyui-linkbutton" style="height:26px;width:90px;font-weight:bold;font-size:20px;color:#b8c7ce;" 
                data-options="toggle:true,group:'nav_group',plain:true" onClick="redirect('${ctx}/admin','${user.username}');"><i class="icon iconfont icon-set2"></i>&nbsp;<%=displayname%></a>
            <%
        }
		if(false){
			String displayname=SyscodeKit.INSTANCE.getCodeDisplay("0018", "purchase", request.getSession().getAttribute("language").toString());
			%>
			<a class="easyui-linkbutton" style="height:26px;width:90px;font-weight:bold;font-size:20px;color:#b8c7ce;" 
				data-options="toggle:true,group:'nav_group',plain:true" onClick="redirect('${ctx}/purchase','${user.username}');"><i class="icon iconfont icon-set2"></i>&nbsp;<%=displayname%></a>
			<%
		}
		if(false){
			String displayname=SyscodeKit.INSTANCE.getCodeDisplay("0018", "stock", request.getSession().getAttribute("language").toString());
			%>
			<a class="easyui-linkbutton" style="height:26px;width:90px;font-weight:bold;font-size:20px;color:#b8c7ce;" 
				data-options="toggle:true,group:'nav_group',plain:true" onClick="redirect('${ctx}/stock','${user.username}');"><i class="icon iconfont icon-set2"></i>&nbsp;<%=displayname%></a>
			<%
		}
		if(false){
			String displayname=SyscodeKit.INSTANCE.getCodeDisplay("0018", "sale", request.getSession().getAttribute("language").toString());
			%>
			<a class="easyui-linkbutton" style="height:26px;width:90px;font-weight:bold;font-size:20px;color:#b8c7ce;" 
				data-options="toggle:true,group:'nav_group',plain:true" onClick="redirect('${ctx}/sale','${user.username}');"><i class="icon iconfont icon-set2"></i>&nbsp;<%=displayname%></a>
			<%
		}
		
		%>
		<a class="easyui-menubutton" id="moremenu" style="height:26px;width:110px;font-weight:bold;font-size:20px;color:#b8c7ce;" 
		    	data-options="toggle:true,group:'nav_group',plain:true,menu:'#moremenuitems'"><i class="icon iconfont icon-ketizu"></i>&nbsp;选择项目</a>
		<div id="moremenuitems"  class="easyui-menu" data-options="onClick:menuHandler"  style="width:200px;">
		</div>
</td>
<div id="read_notice_dig"></div>
<script>
$(function() {
	setTimeout(function () {
		var session_over={
			type : "sessionover",
			exec : function(data){
				console.log("sessionover exec "+data);
				<%
					if(VerifyLicense.hasModule("chat")&&subject.isPermitted("init_chat")){
				%>
				layer.closeAll();
				layui.use('layim', function(layim){
					layim.config({
			            brief: false, //是否简约模式（如果true则不显示主面板）
			            min: true,
			            title: 'IM'
					});
				});
				<%
					}
				%>
				//强行关闭Websocket连接
				forceTocloseWebSocket();
				openReloginDialog();
			}
		}
		var account_else_login={
	            type : "accountelselogin",
	            exec : function(data){
	                console.log("sessionover exec "+data);
	                <%
						if(VerifyLicense.hasModule("chat")){
					%>
					layer.closeAll();
					layui.use('layim', function(layim){
						layim.config({
				            brief: false, //是否简约模式（如果true则不显示主面板）
				            min: true,
				            title: 'IM'
						});
					});
					<%
						}
					%>
	              	//强行关闭Websocket连接
					forceTocloseWebSocket();
	                $.messager.confirm({
						title:'请重新登录',
						ok:'重新登录',
						cancel:'退出系统',
						border:'thin',
						msg:'您的账号已在其他设备登录！',
						fn:function(r){
							if(r){
								openReloginDialog();
							} else {
								 logout();
							}
						}
					});
	            }
	        }
	        if(websocket){
	            initService_WS(session_over);
	            initService_WS(account_else_login);
	        }
	}, 1000);
	getJSON(window.localStorage.ctx+"/research/findProjectByUserId",{},function(data){
		console.log("data:");
		console.log(data);
		for (var i = 0; i < data.length; i++) {
			$('#moremenuitems').menu('appendItem', {
			    text: data[i].name,
			    value:data[i].id
			});
		}
 	});
});
function menuHandler(item){
    redirect(window.localStorage.ctx+'/research','',item.value);
}
</script>

</body>
</html>