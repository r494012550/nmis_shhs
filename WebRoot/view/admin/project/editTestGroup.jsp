<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<c:set var="input_width" value="250"/>
<c:set var="label_width" value="100"/>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div style="width:100%;height:100%">
		<form name="experienceform" id="experienceform" method="POST">
			<div class="easyui-panel" data-options="border:false" style="padding:10px;margin-bottom:5px;">
				<div style="margin-bottom:10px">
			        <input id="name" name="name" value='${eg.name}' class="easyui-textbox" label="实验组名称：" labelWidth="${label_width}" labelAlign="right" 
			        	required="true" style="width:${input_width}px;">
			    </div>
			    <div style="margin-bottom:0px">
			    	<input id="description" name="description"  value='${eg.description}' class="easyui-textbox" label="实验组说明：" labelWidth="${label_width}" labelAlign="right" 
			    		style="width:${input_width}px;">
			    </div>
			</div>
			<input id="id" name="id" type="hidden" value='${eg.id}'/>
			<input id="project_id" name="project_id" type="hidden" value='${project_id}'/>
		</form>
   	</div>
</body>
</html>