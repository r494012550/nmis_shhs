<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
		<div style="width:400px;height:60px;padding:5px 15px;" id="modules_div">
		    
		   	<c:forEach var="arr" items="${list}">
		    	<input class="easyui-checkbox" name="role_module_cb" value="${arr[1]}" label="${arr[0]}:" 
		    	data-options="checked:${arr[2] eq '1'?true:false},onChange:function(checked){onchange_handler_role(${roleid},'${arr[1]}',checked)}">&nbsp;&nbsp;&nbsp;&nbsp;
			</c:forEach>
		    
		</div>
</body>
</html>