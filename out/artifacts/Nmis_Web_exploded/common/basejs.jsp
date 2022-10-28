<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script type="text/javascript" src='${ctx}/js/websocket_services.js?v=${vs}'></script>
<script type="text/javascript" src="${ctx}/js/easyui/jquery.min.js?v=${vs}"></script>
<script type="text/javascript" src="${ctx}/js/easyui/jquery.easyui.min.js?v=${vs}"></script>
<%
if("zh_CN".equals(session.getAttribute("language"))) {

%>
	<script type="text/javascript" src="${ctx}/js/easyui/easyui-lang-zh_CN.js?v=${vs}"></script>
<%
}
else{
%>
	<script type="text/javascript" src="${ctx}/js/easyui/easyui-lang-en.js?v=${vs}"></script>
<%	
}
%>
<script type="text/javascript" src="${ctx}/js/easyui/jquery.cookie.js?v=${vs}"></script>
<script type="text/javascript" src="${ctx}/js/jquery.i18n.properties.js?v=${vs}"></script>
<script type="text/javascript" src='${ctx}/js/language.js?v=${vs}'></script>
<script type="text/javascript" src="${ctx}/js/easyui/datagrid-detailview.js?v=${vs}"></script>
<script type="text/javascript" src="${ctx}/js/easyui/datagrid-groupview.js?v=${vs}"></script>
<script type="text/javascript" src="${ctx}/js/easyui/datagrid-filter.js?v=${vs}"></script>
<script type="text/javascript" src="${ctx}/js/easyui/datagrid-cellediting.js?v=${vs}"></script>
<script type="text/javascript" src="${ctx}/js/common.js?v=${vs}"></script>
<script type="text/javascript" src="${ctx}/js/profile.js?v=${vs}"></script>
<script type="text/javascript" src='${ctx}/js/renderer.js?v=${vs}'></script>
<script type="text/javascript">
	var websocket = null; 
</script>