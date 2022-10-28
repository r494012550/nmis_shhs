<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>启动扫描设备</title>
<link rel="shortcut icon" href="${ctx}/themes/head.ico">
<script type="text/javascript" src="${ctx}/js/easyui/jquery.min.js"></script>
<script language="javascript" type="text/javascript">
<%@page import="com.healta.util.IPKit"%>
<%
	String userid=request.getParameter("userid");
	String launchip=request.getParameter("ip");
	String ip=IPKit.getIP(request);
	
	//out.println("launchip="+launchip+";ip="+ip);
	//out.flush();
	
	if(userid!=null&&!"".equals(userid)){
	
		if(ip.equals(launchip)){
		%>
		
		window.localStorage.port='${pageContext.request.serverPort}';
		window.localStorage.ctx='${ctx}';
		window.localStorage.serverName='${pageContext.request.serverName}';
		var win=window.open("${ctx}/image/toScanView?userid=<%=userid%>&launchip=<%=launchip%>","扫描申请单","width=760,height=800,location=no,menubar=no,resizable=no,scrollbars=no,status=no,toolbar=no",true);

		window.opener=null;
		window.open('','_self');
		window.close();
		
		<%
		}
		else{
			%>
			document.write("ip 地址不匹配");
			//setTimeout("alert('5 seconds!')",5000)
			<%
		}
	}
	else{
		%>
		document.write("用户名为空，请重新点击申请单按钮！");
		//setTimeout("alert('5 seconds!')",5000)
		<%
	}
%>

</script>

</head>
<body>
<%=launchip%>
<%=ip%>

<input id="userid_hidden" type="hidden" value="<%=userid%>"/>
</body>
</html>