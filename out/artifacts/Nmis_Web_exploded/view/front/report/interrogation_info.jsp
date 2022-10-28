<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
<div class="easyui-panel" data-options="fit:true" style="padding:0px;margin:auto;border:0">
	<div style="padding-left:3px;padding-top:3px;">
		<input class="easyui-textbox" label="临床诊断：" labelWidth="80px" labelPosition="top" style="width:100%;height:70px;"
			value="${briefHistory.clinicaldiagnosis}"
			data-options="multiline:true, editable:false"/>
	</div>
	
	<div style="padding-left:3px;padding-top:3px;">
		<input class="easyui-textbox" label="简要病史：" labelWidth="80px" labelPosition="top" style="width:100%;height:90px;"
			value="${briefHistory.briefhistory}"
			data-options="multiline:true, editable:false"/>
	</div>
	
	<div style="padding-left:3px;padding-top:3px;">
		<input class="easyui-textbox" label="问诊其他信息：" labelWidth="80px" labelPosition="top" style="width:100%;height:160px;"
			value="${briefHistory.other_information}"
			data-options="multiline:true, editable:false"/>
	</div>
</div>
</body>
</html>