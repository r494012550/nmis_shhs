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
		<header style="color:#8aa4af;">您当前的位置：系统管理  > 密码策略</header>
		<div class="easyui-panel" style="padding:10px 20px;" data-options="fit:true">
	        <h2>密码策略</h2>
		    <div style="margin-top:20px">
		    	<label class="textbox-label textbox-label-before" style="text-align: right; width: 300px; height: 30px; line-height: 30px;">
		    		是否启用复杂密码：</label>
		        <input class="easyui-switchbutton" style="width:100px;"
		        	data-options="onText:'开',offText:'关'
		        	,checked:${policy.enable_complex_pwd eq '1'},
		        	onChange:function(checked){passwordPolicyConfig(checked,'enable_complex_pwd')}">
		    </div>
		    <div style="margin-top:20px">
		    	<label class="textbox-label textbox-label-before" style="text-align: right; width: 300px; height: 30px; line-height: 30px;">
		    		复杂密码长度：</label>
		        <input class="easyui-numberbox" id="password_length" style="width:100px;"
		        	data-options="min:1,disabled:${policy.enable_complex_pwd ne '1'},
		        	onChange:function(newValue, oldvalue){passwordPolicyConfig(newValue,'password_length')}"
		        	value="${policy.password_length}">
		    </div>
		    <div style="margin-top:20px">
		    	<label class="textbox-label textbox-label-before" style="text-align: right; width: 300px; height: 30px; line-height: 30px;">
		    		是否需要包含数字：</label>
		        <input class="easyui-switchbutton" id="contain_digit_pwd" style="width:100px;"
		        	data-options="onText:'开',offText:'关',disabled:${policy.enable_complex_pwd ne '1'},
		        	checked:${policy.contain_digit_pwd eq '1'},
		        	onChange:function(checked){passwordPolicyConfig(checked,'contain_digit_pwd')}">
		    </div>
		    <div style="margin-top:20px">
		    	<label class="textbox-label textbox-label-before" style="text-align: right; width: 300px; height: 30px; line-height: 30px;">
		    		是否需要包含字母：</label>
		        <input class="easyui-switchbutton" id="contain_letter_pwd"  style="width:100px;"
		        	data-options="onText:'开',offText:'关',disabled:${policy.enable_complex_pwd ne '1'},
		        	checked:${policy.contain_letter_pwd eq '1'},
		        	onChange:function(checked){passwordPolicyConfig(checked,'contain_letter_pwd')}">
		    </div>
		    <div style="margin-top:20px">
		    	<label class="textbox-label textbox-label-before" style="text-align: right; width: 300px; height: 30px; line-height: 30px;">
		    		是否需要包含字母大小写：</label>
		        <input class="easyui-switchbutton" id="contain_case_pwd" style="width:100px;"
		        	data-options="onText:'开',offText:'关',disabled:${policy.enable_complex_pwd ne '1'},
		        	checked:${policy.contain_case_pwd eq '1'},
		        	onChange:function(checked){passwordPolicyConfig(checked,'contain_case_pwd')}">
		    </div>
	    </div>
	</div>	
</body>
</html>