<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<link rel="shortcut icon" href="${ctx}/themes/head.ico?v=${vs}">
	<%@ include file="/common/basecss.jsp"%>
	<%@ include file="/common/basejs.jsp"%>
	<script type="text/javascript">
	
	//$(function() {
		window.localStorage.port='${port}';
		window.localStorage.ctx='${ctx}';
		window.localStorage.serverName='${pageContext.request.serverName}';
		window.localStorage.vs='${vs}';
	//});
	</script>
	<script type="text/javascript" src='${ctx}/js/login.js?v=${vs}'></script>
	<script type="text/javascript" src='${ctx}/js/language.js?v=${vs}'></script>
	<title>${sessionScope.locale.get("login.login")}</title>
	<style>
	    html,body{
	    	width:100%;
	    	height: 100%;
	    }
		*{
			margin:0;
			padding:0;
			list-style: none;
		}
		.login_cont{
			width: 100%;
			height: 100%;
			position: relative;
			background: url(image/login_bg.jpg) no-repeat center center;
			-webkit-background-size: cover;
			background-size: cover;
		}
		.login_ingo{
			height: 330px;
			width: 324px;
			box-sizing: border-box;
			padding:25px 47px;
			background-color: #fff;
			position: absolute;
			top: 50%;
			left:50%;
			margin-top: -187px;
			margin-left: -172px;
			box-shadow: 0 0 15px 0 rgba(57,6,27,0.42);
		}
		.tittle1{
			color: #0A6BB8;
			font-size: 20px;
			text-align: center;
			height: 50px;
		}
		.tittle2{
			color: #0c8dd9;
			font-size: 12px;
			margin-top: 5px;
			margin-bottom: 5px;
			text-align: center;
		}
		.infoItem{
			margin-top: 10px;
			font-size: 12px;
			color: #6a6a6a;
		}
		.infoItem div{
			margin-top: 5px;
			width: 100%;
		}
		/*.infoItem input{
			width: 100%;
			height: 46px;
			border-radius: 4px;
			outline: none;
			box-sizing: border-box;
			padding:0 15px;
			border:1px solid rgb(102,102,102);
			font-size: 16px;
		}
		.login_btn{
			cursor: pointer;
			height: 50px;
			width: 100%;
			border-radius: 4px;
			margin-top: 31px;
			background: url(img/login_btn.png) no-repeat center center;
			-webkit-background-size: cover;
		}*/
		.copyright{
			text-align: center;
			margin-top: 10px;
			font-size: 10px;
			color: #6a6a6a;
		}
		
		.error-message{
	         margin: 4px 0 0 0;
	         padding: 2;
	         font-size: 12px;
	         color: red;
	     }
	     
	</style>
</head>
<body onload="loadUser();">
	<div class="login_cont">
		<div style="width: 100%; position: absolute; top: 20px; right:30px">
			<table style="width:100%">
				<tr>
				
					<td style="text-align: right">
						<!-- <a href="#" class="easyui-linkbutton" data-options="plain:true" onclick="opencaKeydlg()" 
							style="color: #6a6a6a;">&nbsp;修改ca密码</a> -->
						<a href="#" class="easyui-menubutton" data-options="plain:true,menu:'#mm_lan'" style="color: #6a6a6a;"><i class="icon iconfont icon-guojihua"></i>&nbsp;Language</a>
						<a href="#" class="easyui-linkbutton" data-options="plain:true" onclick="$('#dlplugindlg').dialog('open');" 
							style="color: #6a6a6a;">&nbsp;${sessionScope.locale.get("login.download")}</a>
					</td>
				</tr>
			</table>
			<div id="mm_lan" style="width:80px;">
		        <div onclick="switchLanguage('zh_CN')">中文</div>
		        <div onclick="switchLanguage('en_US')">English</div>
		    </div>
		</div>
		<div class="login_ingo">
			<form name="loginform" id="loginform" action="${pageContext.request.contextPath}/dologin" method="POST">
			    <div class="tittle1">
			    	${sessionScope.locale.get("login.systemname")}
			    </div>		    
			    
			    <div class="infoItem">
			        <span>${sessionScope.locale.get("login.username")}</span>
			    	<div>
			    		<input id='username' type="text" name="username" class="easyui-textbox" data-options="prompt:'${sessionScope.locale.get("login.inputusername")}',iconCls:'icon-man',iconWidth:38,err:error" style="width:100%;height:35px;">
			    		<label class="error-message">&nbsp;</label>
			    	</div>
			    </div>		    
			    <div class="infoItem">
			        <span>${sessionScope.locale.get("login.password")}</span>
			    	<div>
			    		<input id='password' class="easyui-passwordbox" type="password" name="password" data-options="checkInterval:0,lastDelay:0,showEye:false,iconWidth:38,err:error" style="width:100%;height:35px;">
			    		<label class="error-message">&nbsp;</label>
			    	</div>
			    </div>
				<div style="margin-top:5px">
		    		<a class="easyui-linkbutton" style="width:100%;height:35px" onClick="dologin();">${sessionScope.locale.get("login.login")}</a>
		    	</div>
		   		<div id="progressbarDiv" style="margin-top:10px;background:orange;display:none">
		   			<div id="progressbar" class="easyui-progressbar" data-options="value:0" style="width:230px;"></div>
		   		</div>
			    <div>
			    	<label class="error-message" id="errormessage">&nbsp;</label>
				</div>
			 
				<!-- <div class="copyright">
					COPYRIGHT@2017 上海锐邦数据系统有限公司 ALL RIGHTS RESERVED
				</div> -->
			</form>
		</div>
	</div>
	<input type="hidden" id="language" value="${language}">
	<div id="dlplugindlg" class="easyui-dialog" data-options="resizable:true,border:'thin',doSize:true,shadow:false" title="下载" style="width:600px;height:550px;padding:5px;" closed="true">
        <table style="width:100%">
				<tr>
					<td>&gt;<a href="${ctx}/download/Tool.zip" download="${ctx}/download/Tool.zip" data-options="plain:true">Tool.zip</a></td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;1&nbsp;下载Tool.zip到本地，并解压缩，运行setup.exe。</td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;2&nbsp;解压缩后目录结构如下图：</td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;<img src="download/tool.jpg" style="width:300px;height:160px;"/></td>
				</tr>
				<tr>
					<td>&gt;<a href="${ctx}/download/setup.exe" download="${ctx}/download/setup.exe" data-options="plain:true">setup.exe</a></td>
				</tr>
				<%-- <tr>
					<td>&gt;<a href="${ctx}\download\ialauncher.bat" download="${ctx}\download\ialauncher.bat" data-options="plain:true">ialauncher.bat</a></td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;1&nbsp;下载到本地。</td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;2&nbsp;修改文件名称为：ialauncher.bat。</td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;3&nbsp;然后拷贝至：%syngo.via%/bin目录下。</td>
				</tr>
				<tr>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;其中%syngo.via%指syngo.via客户端的安装根目录。一般情况32位系统的默认安装路径为：C:\Program Files (x86)\Siemens\syngo.via\，64位系统的安装路径为：C:\Program Files\Siemens\syngo.via\。</td>
				</tr> --%>
			</table>
    </div>
    <div id="caKeydlg" class="easyui-dialog" data-options="resizable:true,border:'thin',doSize:true,shadow:false,buttons:'#caButton'" title="修改密码" style="width:340px;height:200px;padding:5px;" closed="true">
        <table style="width:100%">
			 <tr>
			 	<td><input class="easyui-textbox" id="causername" name="causername" style="width:298px;height:30px;" 
								label="用户名：" labelPosition="left"></td>
			 </tr>
			 <tr>
			 	<td><input class="easyui-passwordbox" id="oldCaPasswrod" name="oldCaPasswrod" style="width:298px;height:30px;" 
								label="原CA密码：" labelPosition="left"></td>
			 </tr>
			  <tr>
			 	<td><input class="easyui-passwordbox" id="CaPasswrod" name="CaPasswrod" style="width:298px;height:30px;" 
								label="新CA密码：" labelPosition="left"></td>
			 </tr>
		</table>
    </div>
    <div id="caButton">
   	 	 <a href="#" class="easyui-linkbutton" onclick="updateUserCa()" >保存</a>
   		 <a href="#" class="easyui-linkbutton" onclick="$('#caKeydlg').dialog('close');">关闭</a>
	</div>
    <div id="progress_dlg" class="easyui-dialog" data-options="noheader:true,modal:true,shadow:false,border:false,closed:true" style="width:120px;height:40px;padding:1px;">
        <table style="width:100%;height:100%;" cellspacing="0" border="0">
			<tr>
				<td><span align="center"><img src="${ctx}/themes/icons/loading.gif?v=${vs}"  alt="${sessionScope.locale.get("loading")}..." /></span></td>
				<td><span align="center">${sessionScope.locale.get("loading")}</span></td>
			</tr>
		</table>
    </div>
	<script>
	</script>
</body>
</html>