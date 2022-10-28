<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
	<form name="tempForm" id="tempForm_ad" method="POST">
		<div style="padding:5px 5px 5px;margin-left:auto;margin-right:auto;width:705px;">
			<span><b>${sessionScope.locale.get("report.templatename")}：</b></span>
			<div style="margin-bottom:5px">
               <input id="templateName" name="name" class="easyui-textbox"
               		data-options="prompt:'${sessionScope.locale.get('report.inputtemplatename')}',required:true,missingMessage:'${sessionScope.locale.get('required')}',
               			onChange:generateCode" 
               		style="width:100%;height:30px;">
            </div>
            <span><b>模板编码：</b></span>
			<div style="margin-bottom:5px">
               <input id="templateCode" name="code" class="easyui-textbox" data-options="prompt:'请输入模板编码',required:true,missingMessage:'${sessionScope.locale.get("required")}'" 
               		style="width:100%;height:30px;">
            </div>
            <span><b>${sessionScope.locale.get("report.studymethod")}：</b></span>
            <div style="margin-bottom:5px">
                <input id="studymethod" name="studymethod" class="easyui-textbox"
                    style="width:100%;height:30px;">
            </div>
            <span><b>${sessionScope.locale.get("wl.reportdesc")}：</b></span>
            <div style="margin-bottom:5px">
			<script id="desc_ad" type="text/plain" name="desccontent_html" style="width:700px;height:170px;"></script>
			</div>
			<span><b>${sessionScope.locale.get("wl.reportresult")}：</b></span>
			<div style="margin-bottom:5px">
			<script id="result_ad" type="text/plain" name="resultcontent_html" style="width:700px;height:135px;"></script>
            </div>
			<input id="templateid" name="id" type="hidden">
			<input id="ispublic" name="ispublic" type="hidden">
			<input id="desccontent_ad" name="desccontent" type="hidden">
			<input id="resultcontent_ad" name="resultcontent" type="hidden">
		</div>
	</form>
		
	
	
</body>
</html>