<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
	<div style="width:700px;">
		<b>【${sessionScope.locale.get("wl.reportdesc")}】:</b><br/>
		${checkdesc_txt}
		<br/><b>【${sessionScope.locale.get("wl.reportresult")}】:</b><br/>
		${checkresult_txt}
	</div>
</body>
</html>