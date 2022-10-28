<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title>登记</title>
	<link rel="shortcut icon" href="${ctx}/themes/head.ico?v=${vs}">
	<%@ include file="/common/basecss.jsp"%>
	<link rel="stylesheet" type="text/css" href="${ctx}/js/cropper/main.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/js/cropper/cropper.min.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/js/umeditor/themes/default/css/umeditor.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/js/imgcontainer/image_container.css?v=${vs}">
	
	<%@ include file="/common/basejs.jsp"%>
	<script type="text/javascript" src="${ctx}/js/front/cache.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/jquery-barcode.min.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/umeditor/umeditor.config.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/umeditor/umeditor.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jquery.texteditor.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/ueditor/rebound.plugin.v1.js?v=${vs}"></script>
	<%-- <script type="text/javascript" src="${ctx}/js/easyui/jQuery.Hz2Py-min.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jQuery.Hz2Py-min1.js?v=${vs}"></script> --%>
	<script type="text/javascript" src="${ctx}/js/front/register.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/reassignstudy.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/unmatchstudy.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/emergencyRegistry.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/gallery/jquery.photo.gallery.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/filter.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/frontcommon.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/imgcontainer/image_container.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/pinyin_dict_notone.js?v=${vs}"></script>
    <script type="text/javascript" src="${ctx}/js/pinyin_dict_withtone.js?v=${vs}"></script>
    <script type="text/javascript" src="${ctx}/js/pinyinUtil.js?v=${vs}"></script>
	<script type="text/javascript">

	</script>
	
	<style>
		.panelHeaderCss{
			/* font-weight: normal !important; */
			border-bottom-width: 0px;
		}
		.panelBodyCss{
			border-bottom-width: 0px;
		}
		
		.mytablelaout{
			width: 960px;
			border-collapse: separate;
			border-spacing: 2px 2px;
		}
		
		.reg_content_div {
			float : left;
			padding-right:3px;
			padding-top:3px;
		}
		
		.mylabel label{
			height: 25px !important;
    		line-height: 25px !important;
		}
	</style>
	
</head>
<body style="background:#fafafa;" class="easyui-layout">
	<div data-options="region:'north',border:false" style="height:35px;" id="northdiv" class="title_background">
		<table style="width:100%;height:100%;" cellspacing="0">
			<tr>			
				<jsp:include page="/view/navigation.jsp" />
				<td><div id="favorites_edit"></div></td>
				<td>
					<input type="hidden" id="userid_hidden" value="${user_id}"/>
					<input type="hidden" id="name_hidden" value="${name}"/>
					<input type="hidden" id="username_hidden" value="${username}"/>
				</td>
				<td align="right" style="font-weight:bold; padding:0px 5px 0px 0px;">
					<a href="#" class="easyui-menubutton" data-options="menu:'#head_mm_reg',iconCls:'icon iconfont icon-user'" style="color:#b8c7ce;">${name}</a>
				</td>
			</tr>
		</table>
		
		<div id="head_mm_reg" style="width:150px;">
			<c:if test="${enable_workforce}">
	        	<div onclick="openDutyRoster()">排班表</div>
	        	<div class="menu-sep"></div>
	        </c:if>
	        <div onclick="openMyConfigDialog('common_dialog')">${sessionScope.locale.get("wl.myprofile")}</div>
	        <div onclick="setEmergDefInfo()">配置急诊默认信息</div>
	        <div class="menu-sep"></div>
	        <div onclick="openCancelMergeDlg();">取消合并</div>
	        <div class="menu-sep"></div>
	        <div onclick="openDevTools()">开发工具</div>
	        <div class="menu-sep"></div>
	        <div onclick="checkSessionAnRelogin(${user.id})">重新登录</div>
	        <div onclick="logout()">${sessionScope.locale.get("logout")}</div>
	    </div>
	</div>
	
	<div data-options="region:'center',href:'${ctx}/register/centerRegister',onLoad:initRegister">
		
	</div>
	<div id="progress_dlg" class="easyui-dialog" data-options="noheader:true,modal:true,shadow:false,border:false,closed:true" 
		style="width:120px;height:42px;padding:1px;">
        <table style="width:100%;height:100%;" cellspacing="0" border="0">
			<tr>
				<td><span align="center"><img src="${ctx}/themes/icons/loading.gif?v=${vs}"  alt="${sessionScope.locale.get("loading")}..." /></span></td>
				<td><span align="center">${sessionScope.locale.get("loading")}</span></td>
			</tr>
		</table>
    </div>
	<!-- <div id="common_dialog_reg"></div> -->
	<div id="common_dialog"></div>
	<div id="common_dialog_applyimage"></div>
	<div id="relogin_dialog"></div>
	<input id="projecturl_reg"  value="${projecturl}" type="hidden">
	<input id="printername1_reg"  value="${printername1}" type="hidden">
	<input id="beforeSaveScan"  value="${beforeSaveScan}" type="hidden">
	<input id="afterSavePrint"  value="${afterSavePrint}" type="hidden">
	<input id="copies"  value="${copies}" type="hidden">
	<input type="hidden" id="chat_login_reminder" value="${up.chat_login_reminder}"/>
</body>
</html>