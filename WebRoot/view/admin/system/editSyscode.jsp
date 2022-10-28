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
		<form name="syscodeform" id="syscodeform" method="POST">
		<div style="margin-bottom:10px">
	        <input class="easyui-textbox" label="代码：" labelPosition="top" id="code" 
	        	data-options="prompt:'请输入代码...',required:true,missingMessage:'必填'" name="code" style="width:250px;height:60px;" value="${code}">
	    </div>
	    <div style="margin-bottom:10px">
	        <input class="easyui-textbox" label="中文名称：" labelPosition="top" id="name_zh" 
	        	data-options="prompt:'请输入中文名称...',required:true,missingMessage:'必填'" name="name_zh" style="width:250px;height:60px;" value="${name_zh}">
	    </div>
	    <div style="margin-bottom:10px">
	        <input class="easyui-textbox" label="英文名称：" labelPosition="top" id="name_en" 
	        	data-options="prompt:'请输入英文名称...',required:true,missingMessage:'必填'" name="name_en" style="width:250px;height:60px;" value="${name_en}">
	    </div>
	    
	    <div style="margin-bottom:10px">
	        <input class="easyui-combobox" id="parent" label="所属组：" labelPosition="top" 
	        	data-options="valueField:'id',textField:'name_zh',url:'${ctx}/syscode/findSyscodeGroup',editable:false,prompt:'请选择组...',required:true,missingMessage:'必填',
	        	onLoadSuccess:function(){$(this).combobox('select', '${parent}')}" 
	        	name="parent" style="width:250px;height:60px;">
	    </div>
	    
	    <input id="syscodeid" name="id" type="hidden" value="${id}">
	    </form>
   	</div>
</body>
</html>