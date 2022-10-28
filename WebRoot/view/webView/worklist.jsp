<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title>报告浏览</title>
	<link rel="shortcut icon" href="${ctx}/themes/head.ico">
	<%@ include file="/common/basecss.jsp"%>
	<%@ include file="/common/basejs.jsp"%>
	<script type="text/javascript" src="${ctx}/js/websocket.js?v=${vs}" ></script>
	<script type="text/javascript" src="${ctx}/js/front/cache.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/jquery-barcode.min.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/webView/worklist.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/gallery/jquery.photo.gallery.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/pdfobject.min.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/filter.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/front/frontcommon.js?v=${vs}"></script>

	<script type="text/javascript" language="javascript">        
	    //页面离开时会触发此方法
		$(window).unload(function(){
	    }); 
    </script>
</head>
<body style="background:#fafafa;" class="easyui-layout">
	<div data-options="region:'north',border:false" style="height:35px;" class="title_background">
		<table style="width:100%;height:100%;" cellspacing="0">
			<tr>
				<td width="80px"><img src="${ctx}/image/logo.png?v=${vs}"  alt="Healta" width="70px" height="22px"/></td>
				<td>
					<input type="hidden" id="studyidList"/>
					<input type="hidden" id="imgwidth" value="${imgwidth}"/>
					<input type="hidden" id="imgheight" value="${imgheight}"/></td>
				
				<td align="right" style="font-weight:bold; padding:0px 5px 0px 0px;">
					<a class="easyui-menubutton" data-options="menu:'#head_mm2',iconCls:'iconfont icon-user'" style="color:#b8c7ce;"> ${user.name}</a>
				</td>
			</tr>
		</table>
		<div id="head_mm2">
		    <div onclick="batchExportReport()">批量导出PDF</div>
		    <div onclick="openMyConfigDialog()">${sessionScope.locale.get("wl.myprofile")}</div>
	        <div onclick="logout();">${sessionScope.locale.get("logout")}</div>
	    </div>
	</div>
	<div data-options="region:'center',border:false">
		<div class="easyui-tabs" id="tab" data-options="plain:true,narrow:true,justified:false,tabHeight:30,fit:true,border:false,tabPosition:'top'">
			<div title="${sessionScope.locale.get("wl.worklist")}" data-options="href:'${ctx}/webview/centerSearch',onLoad:initWorklist">
			</div>
		</div>
	</div>
	<!-- 数据加载页 -->
	<div id="progress_dlg" class="easyui-dialog" data-options="noheader:true,modal:true,shadow:false,border:false,closed:true" 
		style="width:120px;height:42px;padding:1px;">
        <table style="width:100%;height:100%;" cellspacing="0" border="0">
			<tr>
				<td><span align="center"><img src="${ctx}/themes/icons/loading.gif"  alt="${sessionScope.locale.get("loading")}..." /></span></td>
				<td><span align="center">${sessionScope.locale.get("loading")}</span></td>
			</tr>
		</table>
    </div>
	<div id="common_dialog"></div>
	<input type="hidden" id="username_hidden" value="${user.username}"/>
	<input type="hidden" id="userid_hidden" value="${user.id}"/>
	<input type="hidden" id="name_hidden" value="${user.name}"/>
</body>
</html>