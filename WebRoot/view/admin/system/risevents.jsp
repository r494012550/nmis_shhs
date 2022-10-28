<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div class="easyui-panel" data-options="fit:true" style="padding:10px;">
		<header style="color:#8aa4af;">您当前的位置：系统管理  > RIS触发事件管理</header>
		<div class="easyui-panel" style="padding:10px 20px;" data-options="fit:true">
	        <h2>RIS发送至PACS事件消息</h2>
		    <div style="margin-top:20px">
		    	<label class="textbox-label textbox-label-before" style="text-align: right; width: 300px; height: 30px; line-height: 30px;">
		    		患者信息更新（ADT^A08）：</label>
		        <input class="easyui-switchbutton" style="width:100px;"
		        	data-options="onText:'开',offText:'关',disabled:true">
		    </div>
		    <div style="margin-top:20px">
		    	<label class="textbox-label textbox-label-before" style="text-align: right; width: 300px; height: 30px; line-height: 30px;">
		    		患者唯一标识修改（ADT^A18）：</label>
		        <input class="easyui-switchbutton" style="width:100px;"
		        	data-options="onText:'开',offText:'关',disabled:true">
		    </div>
		    <div style="margin-top:20px">
		    	<label class="textbox-label textbox-label-before" style="text-align: right; width: 300px; height: 30px; line-height: 30px;">
		    		患者信息合并（ADT^A40）：</label>
		        <input class="easyui-switchbutton" style="width:100px;"
		        	data-options="onText:'开',offText:'关',disabled:true">
		    </div>
		    <div style="margin-top:20px">
		    	<label class="textbox-label textbox-label-before" style="text-align: right; width: 300px; height: 30px; line-height: 30px;">
		    		新的检查申请（ORM^O01-NW）：</label>
		        <input class="easyui-switchbutton" style="width:100px;"
		        	data-options="onText:'开',offText:'关',disabled:true">
		    </div>
		    <div style="margin-top:20px">
		    	<label class="textbox-label textbox-label-before" style="text-align: right; width: 300px; height: 30px; line-height: 30px;">
		    		检查状态更新（ORM^O01-SC）：</label>
		        <input class="easyui-switchbutton" style="width:100px;"
		        	data-options="onText:'开',offText:'关',disabled:true">
		    </div>
		    <div style="margin-top:20px">
		    	<label class="textbox-label textbox-label-before" style="text-align: right; width: 300px; height: 30px; line-height: 30px;">
		    		检查暂停（ORM^O01-DC）：</label>
		        <input class="easyui-switchbutton" style="width:100px;"
		        	data-options="onText:'开',offText:'关',disabled:true">
		    </div>
		    <div style="margin-top:20px">
		    	<label class="textbox-label textbox-label-before" style="text-align: right; width: 300px; height: 30px; line-height: 30px;">
		    		检查取消（ORM^O01-CA）：</label>
		        <input class="easyui-switchbutton" style="width:100px;"
		        	data-options="onText:'开',offText:'关',disabled:true">
		    </div>
		    <div style="margin-top:20px">
		    	<label class="textbox-label textbox-label-before" style="text-align: right; width: 300px; height: 30px; line-height: 30px;">
		    		报告状态更新（ORU^R01-R）：</label>
		        <input class="easyui-switchbutton" style="width:100px;"
		        	data-options="onText:'开',offText:'关',disabled:true">
		    </div>
		    <div style="margin-top:20px">
		    	<label class="textbox-label textbox-label-before" style="text-align: right; width: 300px; height: 30px; line-height: 30px;">
		    		报告状态更新（ORU^R01-P）：</label>
		        <input class="easyui-switchbutton" style="width:100px;"
		        	data-options="onText:'开',offText:'关',disabled:true">
		    </div>
		    <div style="margin-top:20px">
		    	<label class="textbox-label textbox-label-before" style="text-align: right; width: 300px; height: 30px; line-height: 30px;">
		    		报告状态更新（ORU^R01-A）：</label>
		        <input class="easyui-switchbutton" style="width:100px;"
		        	data-options="onText:'开',offText:'关',disabled:true">
		    </div>
		    <div style="margin-top:20px">
		    	<label class="textbox-label textbox-label-before" style="text-align: right; width: 300px; height: 30px; line-height: 30px;">
		    		报告状态更新（ORU^R01-F）审核报告：</label>
		        <input class="easyui-switchbutton" style="width:100px;"
		        	data-options="onText:'开',offText:'关',checked:${rt.ris_pacs_oru_r01_f eq '1'},onChange:function(checked){risEnvents(checked,'ris_pacs_oru_r01_f')}">
		    </div>
		    <h2>PACS发送至RIS事件消息</h2>
	    </div>
	</div>	
</body>
</html>