<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div style="padding:10px 30px 5px 30px;margin-left:auto;margin-right:auto;">
		<div style="margin-bottom:5px">
		 	<input  class='easyui-textbox' label="模型：" data-options="labelPosition:'before',editable:false"
				style="width:400px;height:30px;" value="${modelname}"/>
        </div>
		<div style="margin-bottom:5px">
		 	<input id='srtemplatename_save'  class='easyui-textbox tb' label="表单名称：" data-options="labelPosition:'before',prompt:'请输入表单名称...',required:true,missingMessage:'必填'"
				style="width:400px;height:30px;" value="${form.name}"/>
        </div>
	</div>
</body>
</html>