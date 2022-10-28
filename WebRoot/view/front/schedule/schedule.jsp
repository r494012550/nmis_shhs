<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title>预约</title>
	<link rel="shortcut icon" href="${ctx}/themes/head.ico?v=${vs}">
	<%@ include file="/common/basecss.jsp"%>
	<link rel="stylesheet" type="text/css" href="${ctx}/js/cropper/main.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/js/cropper/cropper.min.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/js/umeditor/themes/default/css/umeditor.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/js/imgcontainer/image_container.css?v=${vs}">
	<%@ include file="/common/basejs.jsp"%>
	<%-- <script type="text/javascript" src="${ctx}/js/easyui/jQuery.Hz2Py-min.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jQuery.Hz2Py-min1.js?v=${vs}"></script> --%>
	<script type="text/javascript" src="${ctx}/js/pinyin_dict_notone.js?v=${vs}"></script>
    <script type="text/javascript" src="${ctx}/js/pinyin_dict_withtone.js?v=${vs}"></script>
    <script type="text/javascript" src="${ctx}/js/pinyinUtil.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/cache.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/register.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/reassignstudy.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/schedule.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/gallery/jquery.photo.gallery.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/filter.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/frontcommon.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/imgcontainer/image_container.js?v=${vs}"></script>
	<style>
	   .panelHeaderCss{
            /* font-weight: normal !important; */
            border-bottom-width: 0px;
        }
        .panelBodyCss{
            border-bottom-width: 0px;
        }
        
        .mytablelaout{
            width: 950px;
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
	<div data-options="region:'north',border:false" style="height:35px;" class="title_background">
		<table style="width:100%;height:100%;" cellspacing="0">
			<tr>
				<jsp:include page="/view/navigation.jsp" />
				<td>
					<input type="hidden" id="userid_hidden" value="${user_id}"/>
					<input type="hidden" id="username_hidden" value="${username}"/>
				</td>
				<td align="right" style="font-weight:bold; padding:0px 5px 0px 0px;">
					<a class="easyui-menubutton" data-options="menu:'#head_mm_sch',iconCls:'icon iconfont icon-user'"style="color:#b8c7ce;"> ${name}</a>
				</td>
			</tr>
		</table>
		<div id="head_mm_sch" style="width:150px;">
	        <div onclick="openMyConfigDialog();">我的配置</div>
	        <div onclick="gotoWork();">上班打卡</div>
	        <div onclick="gooffWork();">下班打卡</div>
	        <div onclick="openCheckWorkAttendance();">考勤记录</div>
	        <div class="menu-sep"></div>
	        <div onclick="openDevTools()">开发工具</div>
	        <div class="menu-sep"></div>
	        <div onclick="logout();">注销</div>
	    </div>
	</div>
	
	<div data-options="region:'center'">
		<div class="easyui-tabs" id="tabs_div_sch" data-options="plain:true,narrow:true,tabWidth:200,tabHeight:30,fit:true,border:false,tabPosition:'top'">
			<div title="预约表" data-options="href:'${ctx}/schedule/goScheduleDetailView',tabWidth:350"></div>
			<div title="预约录入" data-options="href:'${ctx}/schedule/goSchedulecenter',onLoad:initRegister,selected:true,tabWidth:350"></div>
			<div title="预约查询" data-options="href:'${ctx}/schedule/goSearchSchedule',onLoad:initSearchSch,tabWidth:350"></div>
		</div>			
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
	<div id="common_dialog_applyimage"></div>
	<div id="relogin_dialog"></div>
	<input id="projecturl_res"  value="${projecturlschedule}" type="hidden">
    <input id="printername1_res"  value="${reservationname1}" type="hidden">
    <input id="beforeSaveScansch"  value="${beforeSaveScanSch}" type="hidden">
    <input id="afterSavePrintsch"  value="${afterSavePrintSch}" type="hidden">
    <input id="copiesres"  value="${copiesch}" type="hidden">
	<input type="hidden" id="chat_login_reminder" value="${up.chat_login_reminder}"/>
</body>
</html>