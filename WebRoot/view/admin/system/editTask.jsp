<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div style="padding:10px;margin-left:auto;margin-right:auto;width:260px;">
		<form name="taskform" id="taskform" method="POST">
		<div style="margin-bottom:10px">
	        <input class="easyui-textbox" label="新的定时任务表达式：" labelPosition="top" id=""cron"" 
	        	data-options="prompt:'新的定时任务表达式...',required:true,missingMessage:'必填'" name="cron" style="width:250px;height:60px;" value="${cron}">
	    </div>
	    </form>
   	</div>
</body>
</html>