<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>登记</title>
</head>
<body>
<div style="margin-left:10px;margin-top:10px">
<form name="scanimgform" id="scanimgform" method="POST" enctype="multipart/form-data">
	<table style="border-collapse:separate; border-spacing:0px 10px;">
	<tr>
	<td>
	<input class="easyui-textbox" id="patientname_scanimg" label="病人姓名：" labelWidth="90" labelAlign="right"
		editable="false" style="width:240px;height:28px;">
	</td>
	<td>
	<input class="easyui-textbox" id="studyid_scanimg" label="检查号：" labelWidth="90" labelAlign="right"
		editable="false" style="width:240px;height:28px;">
	</td>
	</tr>
	<tr>
	<td colspan="2">
       	<input class="easyui-filebox" id="scanimgfile" name="scanimgfile" label="申请单文件：" labelWidth="90" labelAlign="right"
			data-options="prompt:'选择申请单文件...',buttonText: '选择申请单...',accept:'.jpg',multiple:true" style="width:482px;"/>
	</td>
	</tr>
	</table>
	
	<input name="orderid" type="hidden" value="${orderid}"/>
	<input name="studyid" type="hidden" value="${studyid}"/>	
</form>
</div>
</body>
</html>