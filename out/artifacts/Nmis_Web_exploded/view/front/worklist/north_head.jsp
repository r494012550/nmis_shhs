<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<table style="width:100%;height:100%;" cellspacing="0">
			<tr>
				
				
				<jsp:include page="/view/navigation.jsp"/>
			        <!--<a class="easyui-linkbutton" style="width:70px;height:26px;font-weight:bold;font-size:20px;" data-options="toggle:true,group:'g2',plain:true,selected:true" onClick="redirect('/worklist','${username}');"><i class="icon iconfont icon-baogao"></i>  报告</a>
			        <a class="easyui-linkbutton" style="width:70px;height:26px;font-weight:bold;font-size:20px;"data-options="toggle:true,group:'g2',plain:true"><i class="icon iconfont icon-tongji"></i>  统计</a>-->
			        
			        <!-- <a class="easyui-linkbutton" style="width:110px;height:41px;font-weight:bold;font-size:20px;" data-options="toggle:true,group:'g2',plain:true," onClick="serachReport();"><i class="icon iconfont icon-set2"></i>我收藏的报告</a> -->
			        
				
				<td>
					<input type="hidden" id="studyidList"/>
					<input type="hidden" id="enableesign" value="${enableesign}"/>
					<input type="hidden" id="signfilepath" value="${signfilepath}"/>
					<input type="hidden" id="imgwidth" value="${imgwidth}"/>
					<input type="hidden" id="imgheight" value="${imgheight}"/></td>
				
				<td align="right" style="font-weight:bold; padding:0px 5px 0px 0px;">
					<a class="easyui-menubutton" data-options="menu:'#head_mm2',iconCls:'icon iconfont icon-user'" style="color:#b8c7ce;"> ${name}</a>
				</td>
			</tr>
		</table>
		<div id="head_mm2">
	        <!--<div onclick="openMyFavoritesDialog()">我收藏的报告</div>
	        <div class="menu-sep"></div>-->
	        <shiro:hasPermission name="cancel_blockreport">
	        <div onclick="cancelLock()">${sessionScope.locale.get("wl.unlock")}</div>
	        </shiro:hasPermission>
	        <shiro:hasPermission name="cancel_audireport">
	        <div onclick="cancelAudiReport()">${sessionScope.locale.get("wl.cancelaudit")}</div>
	        </shiro:hasPermission>
	        <div class="menu-sep"></div>
	        <div onclick="openMyConfigDialog()">${sessionScope.locale.get("wl.myprofile")}</div>
	        <div class="menu-sep"></div>
	        <div onclick="closeWebSocket();logout();">${sessionScope.locale.get("logout")}</div>
	    </div>
</body>
</html>