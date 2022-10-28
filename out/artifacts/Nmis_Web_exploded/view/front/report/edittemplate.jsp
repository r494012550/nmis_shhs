<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
	<form name="tempForm_${reportid}" id="tempForm_${reportid}" method="POST">
		<div style="padding:5px 5px 5px;margin-left:auto;margin-right:auto;width:705px;">
			<div style="margin-bottom:5px">
			<span><b>${sessionScope.locale.get("report.templatename")}：</b></span>
               	<input id="tempname_${reportid}" name="name" class="easyui-textbox"
               		data-options="prompt:'${sessionScope.locale.get('report.inputtemplatename')}',required:true,missingMessage:'${sessionScope.locale.get('required')}',
               		onChange:function(){generateCode(this,${reportid})}" style="width:100%;height:30px;">
            </div>
            <span><b>模板编码：</b></span>
			<div style="margin-bottom:5px">
               <input id="templateCode_${reportid}" name="code" class="easyui-textbox" data-options="prompt:'请输入模板编码',required:true,missingMessage:'${sessionScope.locale.get("required")}'" 
               		style="width:100%;height:30px;">
            </div>
            <!--div style="margin-bottom:5px">
                <input class="easyui-textbox" id="temp_desc_<%=request.getParameter("studyid")%>" label="所见：" labelPosition="top" multiline="true" name="desccontent_html" style="width:100%;height:150px;">
            </div>
            <div style="margin-bottom:5px">
                <input class="easyui-textbox" id="temp_result_<%=request.getParameter("studyid")%>" label="诊断：" labelPosition="top" multiline="true" name="resultcontent_html" style="width:100%;height:150px;">
            </div-->
            <span><b>${sessionScope.locale.get("report.studymethod")}：</b></span>
            <div style="margin-bottom:5px">
                <input class="easyui-textbox" id="studymethod_temp_${reportid}" name="studymethod" style="width:100%;height:30px;">
            </div-->
            <span><b>${sessionScope.locale.get("wl.reportdesc")}：</b></span>
            <div style="margin-bottom:5px">
			<script id="desc_win_${reportid}" type="text/plain" name="desccontent_html" style="width:700px;height:170px;"></script>
			</div>
			<span><b>${sessionScope.locale.get("wl.reportresult")}：</b></span>
			<div style="margin-bottom:5px">
			<script id="result_win_${reportid}" type="text/plain" name="resultcontent_html" style="width:700px;height:145px;"></script>
            </div>
			<input name="nodeid" type="hidden" value="<%=request.getParameter("nodeid")%>" id="templatenodeid_report">
			<input id="templateid_${reportid}" name="id" type="hidden" value="<%=request.getParameter("tempid")!=null?request.getParameter("tempid"):""%>">
			<input id="desccontent_${reportid}" name="desccontent" type="hidden">
			<input id="resultcontent_${reportid}" name="resultcontent" type="hidden">
		</div>
	</form>
		
	
	
</body>
</html>