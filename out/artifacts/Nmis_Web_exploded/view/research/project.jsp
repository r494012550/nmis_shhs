<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title>科研项目</title>
	<link rel="shortcut icon" href="${ctx}/themes/head.ico?v=${vs}">
	<%@ include file="/common/basecss.jsp"%>
	<link rel="stylesheet" type="text/css" href="${ctx}/js/umeditor/themes/default/css/umeditor.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/themes/layui-grid.css">
	<%@ include file="/common/basejs.jsp"%>
	<script type="text/javascript" src="${ctx}/js/front/cache.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/jquery-barcode.min.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/umeditor/umeditor.config.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/umeditor/umeditor.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jquery.texteditor.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/ueditor/rebound.plugin.v1.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/research/research.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/research/form.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/clipboard.min.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/echarts.min.js?v=${vs}"></script>
	<style>
		.header_panel{
			/* font-weight: normal !important; 
			border-bottom-width: 0px;*/
			background-color: #FFFFFF !important;
			border-color: #f2f2f2;
		}
		.body_panel{
			/* border-bottom-width: 0px; */
			background-color: #FFFFFF;
			border-color: #f2f2f2;
		}
		
		.mytablelaout{
			width: 960px;
			border-collapse: separate;
			border-spacing: 2px 2px;
		}
		
		.project_content_div {
			float : left;
			padding-right:3px;
			padding-top:5px;
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
					<input type="hidden" id="userid_hidden" value="${user.id}"/>
					<input type="hidden" id="name_hidden" value="${user.name}"/>
					<input type="hidden" id="username_hidden" value="${user.username}"/>
				</td>
				<td align="right" style="font-weight:bold; padding:0px 5px 0px 0px;">
					<a href="#" class="easyui-menubutton" data-options="menu:'#head_mm_task',iconCls:'icon iconfont icon-user'" style="color:#b8c7ce;">${user.name}</a>
				</td>
			</tr>
		</table>
		
		<div id="head_mm_task" style="width:150px;">
	        <div onclick="logout()">${sessionScope.locale.get("logout")}</div>
	    </div>
	    
	</div>
	
	<div data-options="region:'center',href:'${ctx}/research/centerProject?projectid=${projectid}'">
		
	</div>
	<div id="common_dialog"></div>
	<div id="relogin_dialog"></div>
<!-- 	<div id="writeTemplate_dialog"></div>
	<div id="experience_group_dialog"></div> -->
	<div id="common_right_window" data-options="resizable:false,maximizable:false,collapsible:false,minimizable:false,draggable:false,
		shadow:false,openAnimation:'slide',closeAnimation:'slide',border: 'thin',headerCls:'header_panel',
	    modal:true,left:'',right:0" style="width:40%;height:100%;padding:20px;border-top:0px;border-right:0px;" headerCls="header_panel"></div>
<!-- 	<div id="report_dialog"></div> -->
	
	<div id="progress_dlg" class="easyui-dialog" data-options="noheader:true,modal:true,shadow:false,border:false,closed:true" 
		style="width:120px;height:42px;padding:1px;">
        <table style="width:100%;height:100%;" cellspacing="0" border="0">
			<tr>
				<td><span align="center"><img src="${ctx}/themes/icons/loading.gif?v=${vs}"  alt="${sessionScope.locale.get("loading")}..." /></span></td>
				<td><span align="center">${sessionScope.locale.get("loading")}</span></td>
			</tr>
		</table>
    </div>
    <input id="matrix_view_url" type="hidden" value="${matrix_view_url}">
    <input id="matrix_view_servername" type="hidden" value="${matrix_view_servername}">
    <input id="matrix_url" type="hidden" value="${matrix_url}">
    <input id="kibana_url" type="hidden" value="${kibana_url}">
</body>
</html>