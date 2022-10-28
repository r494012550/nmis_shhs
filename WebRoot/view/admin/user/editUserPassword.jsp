<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>

	        
<div class="easyui-panel" data-options="border:false,fit:true" style="padding:10px 10px;margin-bottom:5px;width:100%;">
	<form name="userPasswordForm" id="userPasswordForm" method="POST">
	<div style="margin-bottom:5px">
		<input class="easyui-textbox"  label="用户名" labelAlign="right" labelWidth="130" style="width:400px;" 
			readonly="readonly" value="${user.username}">
	</div>
	<div style="margin-bottom:5px">
		<input class="easyui-textbox"  label="姓名" labelAlign="right" labelWidth="130" style="width:400px;"
			readonly="readonly" value="${user.name}">
	</div>
	<div style="margin-bottom:5px">
		<input class="easyui-passwordbox passwordtext" id="newpassword" label="${sessionScope.locale.get('login.password')}" labelAlign="right" labelWidth="130"
		data-options="prompt:'请输入密码...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" 
		name="password" style="width:400px;" validType="isSpace">
	</div>
	<div style="margin-bottom:5px">
		<input class="easyui-passwordbox passwordtext" id="newpassword1" label="${sessionScope.locale.get('admin.confirmpassword')}" labelAlign="right" labelWidth="130"
		data-options="prompt:'请再次输入密码...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" 
		name="password1" style="width:400px;" validType="equals['#newpassword']" >
	</div>	        
	      <input name="id" type="hidden" value="${user.id}">
	</form>
</div>
		


</body>
</html>