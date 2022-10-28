<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title>检查</title>
	<link rel="shortcut icon" href="${ctx}/themes/head.ico?v=${vs}">
	<%@ include file="/common/basecss.jsp"%>
	<link rel="stylesheet" type="text/css" href="${ctx}/js/cropper/main.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/js/cropper/cropper.min.css?v=${vs}">
	<%@ include file="/common/basejs.jsp"%>
	<%-- <script type="text/javascript" src="${ctx}/js/front/cache.js"></script> --%>
	<script type="text/javascript" src="${ctx}/js/easyui/jQuery.Hz2Py-min.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/jquery.timers-1.2.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/examine.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/frontcommon.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/gallery/jquery.photo.gallery.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/pinyin_dict_notone.js?v=${vs}"></script>
    <script type="text/javascript" src="${ctx}/js/pinyin_dict_withtone.js?v=${vs}"></script>
    <script type="text/javascript" src="${ctx}/js/pinyinUtil.js?v=${vs}"></script>
    <script type="text/javascript" src="${ctx}/js/admin/calling.js?v=${vs}"></script>
	<style>
		.panelHeaderCss{
			/* font-weight: normal !important; */
			border-bottom-width: 0px;
		}
		.panelBodyCss{
			border-bottom-width: 0px;
		}
		
		.panelHeaderCss_top{
			/* font-weight: normal !important; */
			border-top-width: 0px !important;
		}
		.panelBodyCss_top{
			border-top-width: 0px !important;
		}
		
		.firstrow{
			text-align:center;
			font-size:200%;
		}
		
		.firstcol{
			text-align:center;
			width:80px;
		}
		.mytablelaout{
			border-collapse: separate;
			border-spacing: 2px 2px;
		}
		.panelHeaderCss_height{
			/* font-weight: normal !important; 
			height: 20px !important;
			padding:2px !important;*/
			padding: 1px !important;
    		position: relative !important;
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
					<input type="hidden" id="name_hidden" value="${name}"/>
					<input type="hidden" id="userid_hidden" value="${user_id}"/>
					<input type="hidden" id="username_hidden" value="${username}"/>
				</td>
				<td align="right" style="font-weight:bold; padding:0px 5px 0px 0px;">
					<a href="#" class="easyui-menubutton" data-options="menu:'#head_mm_exam',iconCls:'icon iconfont icon-user'" style="color:#b8c7ce;"> ${name}</a>
				</td>
			</tr>
		</table>
		<div id="head_mm_exam" style="width:150px;">
			<c:if test="${enable_workforce}">
	        	<div onclick="openDutyRoster()">排班表</div>
	        	<div class="menu-sep"></div>
	        </c:if>
	        <div onclick="openMyConfigDialog('common_dialog')">我的配置</div>
	        <div class="menu-sep"></div>
	        <div onclick="openDevTools()">开发工具</div>
	        <div class="menu-sep"></div>
	        <div onclick="checkSessionAnRelogin(${user.id})">重新登录</div>
	        <div onclick="logout();">注销</div>
	    </div>
	</div>
	
	<div data-options="region:'center'">
		<div class="easyui-tabs" id="tabs_div_exam" data-options="plain:true,narrow:true,tabPosition:'top',tabWidth:280,tabHeight:30,fit:true,border:false${bindingip=='false'?",tools:'#tab-tools_exam'":""}">
			<div title="检查工作站" data-options="href:'${ctx}/examine/gotoExamination',selected:true,border:false,onLoad:initExamine"></div>
			<div title="查询" data-options="href:'${ctx}/examine/gotoSearchExamination?modalityType=${modalityType}&modalityId=${modalityId}',onLoad:initSearch,border:false"></div>
		</div>
		<div id="tab-tools_exam">
	        <a class="easyui-linkbutton easyui-tooltip" title="切换设备" data-options="plain:true" onclick="selectEquipment()">&nbsp;<i class="iconfont icon-qiehuanzhanghao"></i>&nbsp;</a>
	    </div>
	</div>
	<div id="common_dialog"></div>
	<div id="relogin_dialog"></div>
	<div id="remarkDlg_exam" class="easyui-dialog" data-options="closed:true,border:false"></div>
	<div class="gallerys"></div>
	<div id="progress_dlg" class="easyui-dialog" data-options="noheader:true,modal:true,shadow:false,border:false,closed:true" 
		style="width:120px;height:42px;padding:1px;">
        <table style="width:100%;height:100%;" cellspacing="0" border="0">
			<tr>
				<td><span align="center"><img src="${ctx}/themes/icons/loading.gif?v=${vs}"  alt="${sessionScope.locale.get("loading")}..." /></span></td>
				<td><span align="center">${sessionScope.locale.get("loading")}</span></td>
			</tr>
		</table>
    </div>
	<input id="user_institution" name="user_institution" value="${user_institution}" type="hidden">
	<input type="hidden" id="chat_login_reminder" value="${up.chat_login_reminder}"/>

</body>
</html>