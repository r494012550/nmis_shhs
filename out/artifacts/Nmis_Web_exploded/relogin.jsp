<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<link rel="shortcut icon" href="${ctx}/themes/head.ico?v=${vs}">
	<%@ include file="/common/basecss.jsp"%>
	<%@ include file="/common/basejs.jsp"%>
</head>
<body >
<style>
	.infoItem{
		margin-top: 10px;
		font-size: 12px;
		color: #6a6a6a;
	}
	.infoItem div{
		margin-top: 5px;
		width: 100%;
	}
	
	.error-message{
         margin: 4px 0 0 0;
         padding: 2;
         font-size: 12px;
         color: red;
     }
     
</style>
<script type="text/javascript" src='${ctx}/js/login.js?v=${vs}'></script>
<script type="text/javascript" src='${ctx}/js/language.js?v=${vs}'></script>
	<div style="margin-left:auto;margin-right:auto;width:250px;">
		<form name="loginform" id="loginform" action="${pageContext.request.contextPath}/dologin" method="POST">    
		    
		    <div class="infoItem" style="width:100%;margin-top: 20px;">
		        <span>用户名</span>
		    	<div>
		    		<input id='username' type="text" name="username" class="easyui-textbox" data-options="prompt:'请输入用户名',iconCls:'icon-man',iconWidth:38,err:error" style="width:100%;height:35px;">
		    		<label class="error-message">&nbsp;</label>
		    	</div>
		    </div>		    
		    <div class="infoItem" style="width:100%;">
		        <span>密码</span>
		    	<div>
		    		<input id='password' class="easyui-passwordbox" type="password" name="password" data-options="showEye:false,iconWidth:38,err:error" style="width:100%;height:35px;">
		    		<label class="error-message">&nbsp;</label>
		    	</div>
		    </div>
		    
			<div style="margin-top:5px;width:100%;">
	    		<a class="easyui-linkbutton" style="width:100%;height:35px"  onClick="dologin(false);">登录</a>
	   		</div>
	   		<div style="margin-top:5px;width:100%;">
	    		<a class="easyui-linkbutton c2" style="width:100%;height:35px"  onClick="$('#relogin_dialog').dialog('close');">关闭</a>
	   		</div>
	   		<div id="progressbarDiv" style="margin-top:10px;background:orange;display:none">
   				<div id="progressbar" class="easyui-progressbar" data-options="value:0" style="width:230px;"></div>
   		 	</div>
		    <div>
		    	<label class="error-message" id="errormessage">&nbsp;</label>
			</div>
		</form>
	</div>
	<input type="hidden" id="language" value="${language}">
</body>
</html>