<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
	<form name="phraseForm_${reportid}" id="phraseForm_${reportid}" method="POST">
		<div style="padding:5px 5px 5px;margin-left:auto;margin-right:auto;width:500px;">
			<div style="margin-bottom:5px">
			<span><b>词条名称：</b></span>
               	<input id="phrase_name_${reportid}" name="phrase_name" class="easyui-textbox"
               		data-options="prompt:'请输词条名称',required:true,missingMessage:'${sessionScope.locale.get('required')}'" 
               		style="width:100%;height:30px;" value="${phrase.phrase_name}">
            </div>
            <span><b>内容：</b></span>
			<div style="margin-bottom:5px">
               <input id="phrase_content_${reportid}" name="phrase_content" class="easyui-textbox" 
               		data-options="prompt:'请输入词条内容',required:true,missingMessage:'${sessionScope.locale.get("required")}'" 
               		style="width:100%;height:30px;" value="${phrase.phrase_content}">
            </div>
			<input id="phraseid_${reportid}" name="id" type="hidden" value="${phrase.id}">
		</div>
	</form>
</body>
</html>