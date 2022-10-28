<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body width="100%" height="100%">
<link rel="stylesheet" type="text/css" href="${ctx}/css/structreport.css?v=${vs}"/>
<script type="text/javascript" src="${ctx}/js/front/srCompHandle.js"/>
	 <!-- 结构化模板填充位置 -->
	<div class="mydiv" style="width:730px;height:100%;margin-left:auto;margin-right:auto;">
		<div id="task_report" class="easyui-panel sr_container" data-options="border:false" 
			style="background-color:#FFFFFF;padding:5px;">
		</div>
		 <input id="formId" name="formId" type="hidden" value='${formId}'/>
		<input id="fi_Id" name="fi_Id" type="hidden" value='${fi_Id}'/>
	</div>
</body>
</html>