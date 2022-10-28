<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div style="padding:10px 10px 5px 10px;margin-left:auto;margin-right:auto;">
		<div style="margin-bottom:10px">
		 	<input id='srsectionname_save'  class='easyui-textbox tb' label="章节名称：" data-options="labelPosition:'before',prompt:'请输入章节名称...',required:true,missingMessage:'必填'"
				style="width:400px;" value="${sec.name}"/>
        </div>
        <div style="margin-bottom:20px">
		 	<input id='srsectiondisplayname_save'  class='easyui-textbox tb' label="显示名称：" data-options="labelPosition:'before',prompt:'请输入显示名称...'"
				style="width:400px;" value="${sec.displayname}"/>
        </div>
        <div style="margin-bottom:10px">
           <input class="easyui-checkbox" id="is_qc" name="is_qc" value="is_qc" data-options="checked:${sec.is_qc eq 1},onChange:function(checked){
           		if(checked){
           			$('#is_header').checkbox('uncheck');
           		}
           }">
           		&nbsp;&nbsp;<span style="font-size:13px">质控章节在打印预览时不显示，且不打印。</span>
        </div>
        <div style="margin-bottom:10px">
           <input class="easyui-checkbox" id="canclone" name="clone" value="clone" data-options="checked:${sec.clone eq 1},onChange:function(checked){
           		if(checked){
           			$('#is_catalog').checkbox('uncheck');
           			$('#is_header').checkbox('uncheck');
           		}
           }">
           		&nbsp;&nbsp;<span style="font-size:13px">可以在结构化报告中复制整个章节内容。</span>
        </div>
        <div style="margin-bottom:10px">
           <input class="easyui-checkbox" id="is_catalog" name="catalog" value="catalog" data-options="checked:${sec.catalog eq 1},onChange:function(checked){
           		if(checked){
           			$('#canclone').checkbox('uncheck');
           			$('#is_header').checkbox('uncheck');
           		}
           }">
           		&nbsp;&nbsp;<span style="font-size:13px">目录章节在写报告时名称会显示在报告目录中。</span>
        </div>
        <div style="margin-bottom:10px">
           <input class="easyui-checkbox" id="is_header" name="header" value="header" data-options="checked:${sec.header eq 1},onChange:function(checked){
           		if(checked){
           			$('#is_qc').checkbox('uncheck');
           			$('#canclone').checkbox('uncheck');
           			$('#is_catalog').checkbox('uncheck');
           		}
           }">
           		&nbsp;&nbsp;<span style="font-size:13px">标题章节在写报告时不显示，打印和打印预览时显示。</span>
        </div>
	</div>
</body>
</html>