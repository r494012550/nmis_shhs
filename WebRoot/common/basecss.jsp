<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.healta.model.UserProfiles"%>
<%@page import="com.jfinal.kit.PropKit"%>
<%
	String theme=PropKit.use("system.properties").get("default_theme", "metro-blue");
	if(request.getSession().getAttribute("myprofiles")!=null){
		UserProfiles up=(UserProfiles)request.getSession().getAttribute("myprofiles");
		if(up.getTheme()!=null&&!"".equals(up.getTheme())){
			theme=up.getTheme();
		}
	}
%>
<link rel="stylesheet" type="text/css" href="${ctx}/themes/<%=theme%>/easyui.css?v=${vs}">
<link rel="stylesheet" type="text/css" href="${ctx}/themes/<%=theme%>/custom.css?v=${vs}">
<link rel="stylesheet" type="text/css" href="${ctx}/themes/color.css?v=${vs}">
<link rel="stylesheet" type="text/css" href="${ctx}/themes/icon.css?v=${vs}">
<link rel="stylesheet" type="text/css" href="${ctx}/themes/iconfont/iconfont.css?v=${vs}">

<style type="text/css">

		/* @font-face {
	    	font-family: 'font_SourceHan';
		    src: url('${ctx}/fonts/SourceHanSerifSC-Regular.otf');
		    font-weight: normal;
		} */
	
		/* @font-face {
	    	font-family: 'font_SourceHan';
		    src: url('${ctx}/fonts/SourceHanSerifSC-Bold.otf'),
		    url('${ctx}/fonts/SourceHanSerifSC-ExtraLight.otf'),
		    url('${ctx}/fonts/SourceHanSerifSC-Heavy.otf'),
		    url('${ctx}/fonts/SourceHanSerifSC-Light.otf'),
		    url('${ctx}/fonts/SourceHanSerifSC-Medium.otf'),
		    url('${ctx}/fonts/SourceHanSerifSC-Regular.otf'),
		    url('${ctx}/fonts/SourceHanSerifSC-SemiBold.otf');
		    font-weight: normal;
		} */
		
		/*@font-face {
	    	font-family: 'font_SourceHan';
		    src: url('${ctx}/fonts/KaiGenGothicSC-Regular.ttf');
		    font-weight: normal;
		}*/
		
		/* @font-face {
	    	font-family: 'font_SourceHan';
		    src: url('${ctx}/fonts/KaiGenGothicSC-Bold.ttf'),
		    url('${ctx}/fonts/KaiGenGothicSC-ExtraLight.ttf'),
		    url('${ctx}/fonts/KaiGenGothicSC-Heavy.ttf'),
		    url('${ctx}/fonts/KaiGenGothicSC-Light.ttf'),
		    url('${ctx}/fonts/KaiGenGothicSC-Medium.ttf'),
		    url('${ctx}/fonts/KaiGenGothicSC-Normal.ttf'),
		    url('${ctx}/fonts/KaiGenGothicSC-Regular.ttf');
		    font-weight: normal;
		} */
	
		/* font-family: font_SourceHan, 思源宋体, Source Han Serif,微软雅黑, Microsoft YaHei; 思源宋体, Source Han Serif*/
		body{
			
			/* font-family:"${res.get("systemfont")}"; */
			font-family: 微软雅黑, Microsoft YaHei,宋体,SimSun,SimSun-18030,SimSun-ExtB !important;
		} 
		
		/* .title_background {
		    height: 30px;
		    background: -webkit-linear-gradient(left, #4580F4 , #3CA1FD); 
		    background: -o-linear-gradient(right, #4580F4 , #3CA1FD);  
		    background: -moz-linear-gradient(right, #4580F4 , #3CA1FD);  
		    background: linear-gradient(to right, #4580F4 , #3CA1FD);  
		} */   
		.title_background {
		    height: 30px;
		    background: #15162C;
		    color: #FFFFFF !important;
		}
		.panelHeaderCss{
			/* font-weight: normal !important; */
			border-bottom-width: 0px;
		}
		.panelBodyCss{
			border-bottom-width: 0px;
		}
		
		.panelHeaderCss_top{
			/* font-weight: normal !important; */
			border-top-width: 0px;
		}
		.panelBodyCss_top{
			border-top-width: 0px !important;
		}
		
</style>

