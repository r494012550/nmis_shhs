<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
<div style="margin-left:30px">
<select class="easyui-combobox" style="width:200px;" label="报告组" labelWidth="80px" labelPosition="top" panelHeight="120px">
	<option>全部</option>
	<option>报告医生</option>
	<option>审核医生</option>
</select>
</div>
<div style="margin-left:30px">
<select class="easyui-combobox" style="width:200px;" label="规则" labelWidth="80px" labelPosition="top" panelHeight="120px"
	data-options="onChange:qqq">
	<option value="平均">平均</option>
	<option value="绩效">绩效</option>
	<option value="订阅">订阅</option>
</select>
</div>
<script>
function qqq(newValue, oldValue){
	console.log(newValue);
}
</script>
</body>
</html>