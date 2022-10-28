<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
    <div style="padding:10px;margin-left:auto;margin-right:auto;width:450px;">
        <form name="checkerrorform" id="checkerrorform" method="POST">
	       <input id="id" name="id" type="hidden" value="${rule.id}"/>
	       <div style="margin-bottom:10px">
	       		<div style="float: left;">
			       	<select class="easyui-combobox" name="type" label="关键词：" labelPosition="top" 
			       		data-options="required:true,editable:false,panelHeight:'auto',value:'${rule.type}'" style="width:100px;height:60px;">
		                <option value="sex">性别</option>
		                <option value="examitem">检查项目</option>
		                <option value="modality_type">设备类型</option>
		            </select>
		      	</div>
		      	<div style="float: left;margin-left:-1px">
		            <input class="easyui-textbox"  id="keyword" label=" " labelPosition="top"
			               data-options="prompt:'请输入关键词...',validType:'checkKeyword',required:true,missingMessage:'必填'" name="keyword" 
			               style="width:350px;height:60px;" value="${rule.keyword}">
	           </div>
	       </div>
	       <div style="margin-bottom:10px">
	           <input class="easyui-textbox" label="规则：" labelPosition="top" id="rules" 
	               data-options="prompt:'请输入规则如 a|b|c形式',validType:'checkRules',required:true,missingMessage:'必填'" name="rules" 
	               multiline="true" style="width:450px;height:100px;" value="${rule.rules}">
	       </div>
	       <div style="margin-bottom:10px">
	           <input class="easyui-textbox" label="提示信息：" labelPosition="top" id="rules" 
	               data-options="prompt:'请输入提示信息',required:true,missingMessage:'必填'" name="warning" 
	               multiline="true" style="width:450px;height:100px;" value="${rule.warning}">
	       </div>
	       <script>
		       $.extend($.fn.validatebox.defaults.rules,{
		    	   checkRules:{
		               validator:function(value){
		                   var reg = /^([a-zA-Z0-9\u4E00-\u9FA5]+$)|([a-zA-Z0-9\u4E00-\u9FA5]+\|+[a-zA-Z0-9\u4E00-\u9FA5]+$)/;
		                   return reg.test(value);
		               },
		               message:'规则输入格式不正确'
		           },
		           checkKeyword:{
		        	   validator:function(value){
                           var reg = /\|/;
                           return !reg.test(value);
                       },
                       message:'关键词输入格式不正确'
		           },
		       });
	       </script>
        </form>
    </div>
    
</body>
</html>