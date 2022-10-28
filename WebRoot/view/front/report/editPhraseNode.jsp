<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
<form name="phraseClassifyForm" id="phraseClassifyForm" method="POST">
	<div style="padding:5px 5px 5px;margin-left:auto;margin-right:auto;width:250px;">
           <div style="margin-bottom:5px">
               <input id="phraseClassifyName" class="easyui-textbox" label="${sessionScope.locale.get('report.nodename')}：" labelPosition="top" 
               data-options="prompt:'${sessionScope.locale.get('report.inputnodename')}...',required:true,
               		missingMessage:'${sessionScope.locale.get('required')}',
					validType:{length:[1,50]}"
				name="nodename" style="width:100%;height:60px;" value="${phrasenode.nodename}">
           </div>
           <input id="id" name="id" type="hidden" value="${phrasenode.id}">
	</div>
</form>
</body>
</html>