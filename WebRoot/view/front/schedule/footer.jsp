<%@page import="com.healta.model.Syscode"%>
<%@page import="com.jfinal.kit.StrKit"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Insert title here</title>
</head>
<body>
<%@page import="java.util.List"%> 
<%@page import="org.apache.commons.lang.StringUtils"%> 
<%@page import="com.healta.util.SyscodeKit"%>
<%@page import="com.jfinal.plugin.ehcache.CacheKit"%>
<%@page import="com.jfinal.plugin.ehcache.IDataLoader"%> 
<%@page import="com.healta.model.StatusColor"%> 

<%
	List<StatusColor> scs= CacheKit.get("dicCache", "status_color", new IDataLoader(){ 
	    public Object load() { 
	      return StatusColor.dao.find("select * from status_color"); 
		}
	});
	List<Syscode> codes= SyscodeKit.INSTANCE.getCodes("0005");
	String typedisplayname=SyscodeKit.INSTANCE.getCodeTypeDisplay("0005", request.getSession().getAttribute("language").toString());
	%>
	<b><%=typedisplayname%>:</b>&nbsp;
	<%
	for(Syscode scode:codes) {
		String color="#FFFFFF";
		String displayname=SyscodeKit.INSTANCE.getCodeDisplay("0005",scode.getCode(), request.getSession().getAttribute("language").toString());
		for(StatusColor sc:scs){
			if(StrKit.equals(sc.getStatusCode(), scode.getCode())&&StrKit.equals(sc.getType(),scode.getType())){
				color=sc.getColor();
				break;
			};
		}; 
		
		%>
		    <a style="width:28px;height:20px;color:<%=color%>;"><%=displayname%></a>&nbsp;
		<%
	};
		
	codes= SyscodeKit.INSTANCE.getCodes("0007");
	//codes.sort((Syscode code1,Syscode code2)-> Integer.valueOf(code1.getCode()).compareTo(Integer.valueOf(code2.getCode())));
	typedisplayname=SyscodeKit.INSTANCE.getCodeTypeDisplay("0007", request.getSession().getAttribute("language").toString());
	%>
	<b><%=typedisplayname%>:</b>&nbsp;
	<%
	for(Syscode scode:codes) {
		String color="#FFFFFF";
		String displayname=SyscodeKit.INSTANCE.getCodeDisplay("0007",scode.getCode(), request.getSession().getAttribute("language").toString());
		for(StatusColor sc:scs){
			if(StrKit.equals(sc.getStatusCode(), scode.getCode())&&StrKit.equals(sc.getType(),scode.getType())){
				color=sc.getColor();
				break;
			};
		}; 
		
		%>
		    <a style="width:28px;height:20px;color:<%=color%>;"><%=displayname%></a>&nbsp;
		<%
	};
%>
		        
</body>
</html>