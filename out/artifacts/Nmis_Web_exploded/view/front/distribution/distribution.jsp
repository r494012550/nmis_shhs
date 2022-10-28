<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title>${sessionScope.locale.get("wl.worklist")}</title>
	<link rel="shortcut icon" href="${ctx}/themes/head.ico?v=${vs}">
	<%@ include file="/common/basecss.jsp"%>
	<link rel="stylesheet" type="text/css" href="${ctx}/js/cropper/main.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/js/cropper/cropper.min.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/js/umeditor/themes/default/css/umeditor.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/js/imgcontainer/image_container.css?v=${vs}">
	<%@ include file="/common/basejs.jsp"%>
	<script type="text/javascript" src="${ctx}/js/front/cache.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/jquery-barcode.min.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/distribution.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/filter.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/frontcommon.js?v=${vs}"></script>
    <style>
		.mylabel label{
			height: 25px !important;
    		line-height: 25px !important;
		}
	</style>
</head>
<body style="background:#fafafa;" class="easyui-layout">
	<div data-options="region:'north',border:false" style="height:35px;" class="title_background">
		<table style="width:100%;height:100%;" cellspacing="0">
			<tr>
				<jsp:include page="/view/navigation.jsp"/>
				<td>
					<input type="hidden" id="imgwidth" value="${imgwidth}"/>
					<input type="hidden" id="imgheight" value="${imgheight}"/></td>
				
				<td align="right" style="font-weight:bold; padding:0px 5px 0px 0px;">
					<a class="easyui-menubutton" data-options="menu:'#head_mm2',iconCls:'icon iconfont icon-user'" style="color:#b8c7ce;"> ${user.name}</a>
				</td>
			</tr>
		</table>
		<div id="head_mm2">
	        <div onclick="batchExportReport()">批量导出PDF</div>
	        <div class="menu-sep"></div>
            <div onclick="exportExcel()">导出Excel</div>
            <div class="menu-sep"></div>
	        <div onclick="openMyConfigDialog()">${sessionScope.locale.get("wl.myprofile")}</div>
	        <div class="menu-sep"></div>
	        <div onclick="openDevTools()">开发工具</div>
	        <div class="menu-sep"></div>
	        <div onclick="closeWebSocket();logout();">${sessionScope.locale.get("logout")}</div>
	    </div>
	</div>
	<div data-options="region:'center',border:false,href:'${ctx}/distribution/centerSearch',onLoad:initWorklist">
		
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
	<div id="common_dialog"></div>
	<div id="relogin_dialog"></div>
	<input type="hidden" id="username_hidden" value="${user.username}"/>
	<input type="hidden" id="userid_hidden" value="${user.id}"/>
	<input type="hidden" id="name_hidden" value="${user.name}"/>
	<input type="hidden" id="chat_login_reminder" value="${up.chat_login_reminder}"/>
</body>
</html>