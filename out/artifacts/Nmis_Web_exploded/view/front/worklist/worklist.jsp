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
	<link rel="stylesheet" type="text/css" href="${ctx}/css/structreport.css?v=${vs}"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/js/umeditor/themes/default/css/umeditor.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/js/front/report/report_image_container.css?v=${vs}">
	<%@ include file="/common/basejs.jsp"%>
	<%-- <script type="text/javascript" src="${ctx}/js/jquery.timer.js"></script> --%>
	<script type="text/javascript" src="${ctx}/js/clipboard.min.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/cache.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/jquery-barcode.min.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/umeditor/umeditor.config.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/umeditor/umeditor.js?v=${vs}"></script>
	<%-- <script type="text/javascript" src="${ctx}/js/ueditor/lang/zh-cn/zh-cn.js"></script> --%>
	<script type="text/javascript" src="${ctx}/js/easyui/columns-ext.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jquery.texteditor.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jQuery.Hz2Py-min.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/jquery.timers-1.2.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/worklist.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/report.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/gallery/jquery.photo.gallery.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/pdfobject.min.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/filter.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/structreport.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/template.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/frontcommon.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/replacerule.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/close.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/pinyin_dict_notone.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/pinyin_dict_withtone.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/pinyinUtil.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/phrase.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/report/report_image_container.js?v=${vs}"></script>
	<c:if test="${sr_support}">
   	<script type="text/javascript" src="${ctx}/js/ueditor/rebound.plugin.v1.js?v=${vs}"></script>
   	</c:if>
   	<script type="text/javascript" src="${ctx}/js/front/srAdditionalFunc.js?v=${vs}"></script>
    <style>
		.mylabel label{
			height: 25px !important;
    		line-height: 25px !important;
		}
	</style>
	<script type="text/javascript" language="javascript">  
	
	//unload点击报告重新进入界面时，closeReports请求无法触发
	    /* $(window).unload(function(){
	    	console.log("==unload==");
	    	closeUI();
	    });
	    $(window).bind('beforeunload', function (e) {
	    	console.log("==beforeunload==");
	    	closeUI();
	    }) 
	    function closeUI(){
	    	var ts=$('#tab').tabs('tabs');
	    	if(ts.length>1){
	    		var ids="";
		    	for(var i=1;i<ts.length;i++){
		    		var tabid=ts[i].panel('options').id;
		    		ids+=tabid.substr(tabid.lastIndexOf("_")+1)+",";
		    	}
		    	$.getJSON("${ctx}/report/closeReports?reportids="+ids,function(json){
		    		
		    	});
		    	console.log("==closeReports==");
	    	}
	    }*/
	    
    </script>
</head>
<body style="background:#fafafa;" class="easyui-layout">
	<div data-options="region:'north',border:false" style="height:35px;" class="title_background">
		<table style="width:100%;height:100%;" cellspacing="0">
			<tr>
				
				
				<jsp:include page="/view/navigation.jsp"/>
			        <!--<a class="easyui-linkbutton" style="width:70px;height:26px;font-weight:bold;font-size:20px;" data-options="toggle:true,group:'g2',plain:true,selected:true" onClick="redirect('/worklist','${username}');"><i class="icon iconfont icon-baogao"></i>  报告</a>
			        <a class="easyui-linkbutton" style="width:70px;height:26px;font-weight:bold;font-size:20px;"data-options="toggle:true,group:'g2',plain:true"><i class="icon iconfont icon-tongji"></i>  统计</a>-->
			        
			        <!-- <a class="easyui-linkbutton" style="width:110px;height:41px;font-weight:bold;font-size:20px;" data-options="toggle:true,group:'g2',plain:true," onClick="serachReport();"><i class="icon iconfont icon-set2"></i>我收藏的报告</a> -->
			        
				
				<td>
					<input type="hidden" id="studyidList"/>
					<input type="hidden" id="enableesign" value="${up.enableesign}"/>
					<input type="hidden" id="signfilepath" value="${up.signfilepath}"/>
					<input type="hidden" id="worklist_refresh_interval" value="${up.worklist_refresh_interval}"/>
					<input type="hidden" id="imgwidth" value="${imgwidth}"/>
					<input type="hidden" id="imgheight" value="${imgheight}"/></td>
					<a id="printApp_${reportid}" href="printApp:-1" type="hidden" ></a>
				<td align="right" style="font-weight:bold; padding:0px 5px 0px 0px;">
					<a class="easyui-menubutton" data-options="menu:'#head_mm2',iconCls:'icon iconfont icon-user'" style="color:#b8c7ce;"> ${user.name}</a>
				</td>
			</tr>
		</table>
		<div id="head_mm2">
	        <!--<div onclick="openMyFavoritesDialog()">我收藏的报告</div>
	        <div class="menu-sep"></div>-->
	        <c:if test="${enable_workforce}">
	        	<div onclick="openDutyRoster()">排班表</div>
	        	<div class="menu-sep"></div>
	        </c:if>
	        <shiro:hasPermission name="cancel_blockreport">
	        <div onclick="cancelLock()">${sessionScope.locale.get("wl.unlock")}</div>
	        </shiro:hasPermission>
	        <shiro:hasPermission name="cancel_audireport">
	        <div onclick="cancelAudiReport()">${sessionScope.locale.get("wl.cancelaudit")}</div>
	        </shiro:hasPermission>
	        <div onclick="addOtherReport()">新建报告</div>
	        <div class="menu-sep"></div>
	        <div onclick="batchExportReport()">批量导出PDF</div>
	        <div class="menu-sep"></div>
	        <div onclick="exportExcel()">导出Excel</div>
            <div class="menu-sep"></div>
	        <div onclick="openMyConfigDialog()">${sessionScope.locale.get("wl.myprofile")}</div>
	        <div onclick="openShortcutDialog()">配置模板快捷方式</div>
	        <div class="menu-sep"></div>
	        <div onclick="openDevTools()">开发工具</div>
	        <div class="menu-sep"></div>
	        <div onclick="checkSessionAnRelogin(${user.id})">重新登录</div>
	        <div onclick="closeWebSocket();logout();">${sessionScope.locale.get("logout")}</div>
	    </div>
	</div>
	<div data-options="region:'center',border:false">
		<div class="easyui-tabs" id="tab" data-options="plain:true,narrow:true,justified:false,tabHeight:30,fit:true,border:false,tabPosition:'top',tools:'#tab_tools_worklist',
			onBeforeClose:function(title,index){
		     	return beforeCloseReport(title,index);
		     
			},onClose:function(title,index){
				report_close(title,index);
			}">
			<div title="${sessionScope.locale.get("wl.worklist")}" data-options="href:'${ctx}/worklist/centerSearch',onLoad:initWorklist">
			</div>
		</div>
	</div>
	<div id="tab_tools_worklist" style="padding:3px 0px;">
        <span id="autosavereport_tip"></span>
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
	<div id="common_dialog1"></div>
	<div id="common_dialog_applyimage"></div>
	<div id="common_dialog_reporttemp"></div>
	<div id="relogin_dialog"></div>
	<div id="mm_article" class="easyui-menu" style="width:100px;"></div>
	
	<div style="display: none;" id="temp_div_hidden"></div>
	<shiro:hasPermission name="edit_publicTemplate">
		<input name="editpublicnode" id="editpublicnode" type="hidden" value="1">
	</shiro:hasPermission>
	<input type="hidden" id="vip_flag"  value="${vip_flag}"/>
	<input type="hidden" id="openGrade"  value="${openGrade}"/>
	<input type="hidden" id="username_hidden" value="${user.username}"/>
	<input type="hidden" id="userid_hidden" value="${user.id}"/>
	<input type="hidden" id="name_hidden" value="${user.name}"/>
	<input type="hidden" id="devSN" value=""/>
	<input type="hidden" id="autosavereportint" value="${up.auto_save_report_interval}"/>
	<input type="hidden" id="report_audit" value="${up.report_audit}"/>
	<input type="hidden" id="report_submit" value="${up.report_submit}"/>
	<input type="hidden" id="chat_login_reminder" value="${up.chat_login_reminder}"/>
</body>
</html>