<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div class="easyui-panel" border="false" data-options="fit:true" style="height:100%;width:100%;padding:5px 30px;">
		<form id="editprojectform" method="POST">
			<div style="margin-bottom:5px;">
				<input id="task_name" name="name"  value="${tm.name}" class="easyui-textbox" label="项目名称:" labelPosition="top"
					style="width:100%;" required="true">
			</div>
			<div style="margin-bottom:5px;">
				<input id="task_type" name="type"  value="${tm.type}" class="easyui-textbox" label="项目类别:" labelPosition="top"
					style="width:100%;" required="true">
			</div>
			<div style="margin-bottom:0px;">
				<input id="task_content" name="description" value="${tm.description}" class="easyui-textbox" label="项目描述:" labelPosition="top"
					style="width:100%;height:90px;" multiline="true">
			</div>
			<input id="id" name="id" value='${tm.id}' type="hidden"/>
		</form>
	</div>
   	
</body>
</html>