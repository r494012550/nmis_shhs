<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
	<!-- 配置文件 -->
	<script type="text/javascript" src="${ctx}/js/ueditor/ueditor.config.js"></script>
	<!-- 编辑器源码文件 -->
	<script type="text/javascript" src="${ctx}/js/ueditor/ueditor.all.js"></script>

	<div class="easyui-layout" data-options="fit: true,border:false">
		<div data-options="region:'west',hideCollapsedContent:false,border:true" style="width:20%;">
			<ul class="easyui-tree" id="user_tree" data-options="url:'${ctx}/notice/getSendEmployeeJson?noticeId=${notice.id }',checkbox:true"></ul>
		</div>
		
		<div data-options="region:'center',border:true" style="width: 80%;">
			<form name="noticeFrom" id="noticeFrom" method="POST" enctype="multipart/form-data">
				<div style="margin-left: 20px; margin-top: 15px;">
					<input class="easyui-textbox" id="title" value="${notice.title }" name="title" style="width:800px;height: 28px;" data-options="label:'标题:'">
				</div>
				
				<div style=" margin-left: 20px; margin-top: 15px;">
					<div style="float: left;">
						<input class="easyui-datetimebox" id="sendtime" name="sendtime" value="${notice.releasetime }" style="width:395px;" data-options="label:'发布时间:'">
						<label style="margin-left: 15px;">必读:</label>
					</div>
					<div style="float: left; padding-left: 36px;">
						<input class="easyui-switchbutton" name="type" data-options="onText:'必读', offText:'非必读' , checked:${notice.mustread eq 1}, width:200">
					</div>
					<div style="clear: left;"></div>
				</div>
				<div style="margin-left: 20px; margin-top: 15px;">
					<input class="easyui-filebox" id="file1" name="file1" value="${file.file1 }" data-options="label:'附件1:',prompt:'请选择一个或者多个附件',separator: ',', buttonText: '选择文件', onChange:function(newFile, oldFile){fileOnChange(newFile, '${file.file1 }')}" style="width:335px;">
					<a id="select_user" class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="removeFile('${file.file1 }', 'file1');"><span class="l-btn-left"><span class="l-btn-text">移除</span></span></a>
					<input class="easyui-filebox" id="file2" name="file2" value="${file.file2 }" data-options="label:'&nbsp;附件2:',prompt:'请选择一个或者多个附件',separator: ',', buttonText: '选择文件', onChange:function(newFile, oldFile){fileOnChange(newFile, '${file.file2 }')}" style="width:335px;">
					<a id="select_user" class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="removeFile('${file.file2 }', 'file2');"><span class="l-btn-left"><span class="l-btn-text">移除</span></span></a>
				</div>
				
				<div style="margin-left: 20px; margin-top: 15px;">
					<input class="easyui-filebox" id="file3" name="file3" value="${file.file3 }" data-options="label:'附件3:',prompt:'请选择一个或者多个附件',separator: ',', buttonText: '选择文件', onChange:function(newFile, oldFile){fileOnChange(newFile, '${file.file3 }')}" style="width:335px;">
					<a id="select_user" class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="removeFile('${file.file3 }', 'file3');"><span class="l-btn-left"><span class="l-btn-text">移除</span></span></a>
					<input class="easyui-filebox" id="file4" name="file4" value="${file.file4 }" data-options="label:'&nbsp;附件4:',prompt:'请选择一个或者多个附件',separator: ',', buttonText: '选择文件', onChange:function(newFile, oldFile){fileOnChange(newFile, '${file.file4 }')}" style="width:335px;">
					<a id="select_user" class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="removeFile('${file.file4 }', 'file4');"><span class="l-btn-left"><span class="l-btn-text">移除</span></span></a>
				</div>
				
				<input id="delFile" name="delFile" type="hidden">
				<input id="noticeId" name="noticeId" type="hidden" value="${notice.id }">
				<input id="selectedUserIds" name="selectedUserIds" type="hidden" value="${selectedUserIds }" >
				<div id="content" hidden>${notice.contenthtml }</div>
				<div>&nbsp;</div>
			</form>
				<script id="container" type="text/plain" name="content" style="margin-left: 20px; width: 800px;height: 380px;"></script>
		</div>	
	</div>
</body>
</html>