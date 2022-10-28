<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
	<div style="margin: 5px 10px 5px 10px; ">
		<div style="text-align: center;"><h1>${notice.title }</h1></div>
		
		<div style="margin-top: 20px;">${notice.contenthtml }</div>
		<c:if test="${notice.filepath != null}">
		<!--  -->
			<div style="text-align: right;">
				<a id="select_user" class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="downloadFile();"><span class="l-btn-left"><span class="l-btn-text">下载附件</span></span></a>
			</div>
		</c:if>
	</div>
	<input id="files" type="hidden" value="${files }">
	<input id="noticeId1" type="hidden" value="${notice.id }">
	<div id="download"></div>
	<script type="text/javascript">
	function downloadFile(){
		var files = $('#files').val();
		var fileArray = files.split(',');
		var download = $("#download");
		var noticeId = $('#noticeId1').val();
		for (var i = 0; i < fileArray.length; i++) {
			download.append('<a id="file'+ i + '" href="notice/downloadFile?fileName=' + fileArray[i] + '&noticeId=' + noticeId + '" target="_blank" download="' + fileArray[i] + '" style="display:none">下载该文件</a>');
			console.log(download);
			$("#file" + i)[0].click();
		}
	}
	</script> 
</body>
</html>