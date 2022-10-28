<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<div> 
	<div style="margin-left:auto;margin-right:auto;width:730px;">
		<script id="snapshot_container" type="text/plain" style="width:730px;height:612px;"></script>
	<div>

</div>



<script type="text/javascript">

$(document).ready(function(){

	UM.delEditor('snapshot_container');
	var editor = UM.getEditor('snapshot_container',{wordCount:false,elementPathEnabled:false,autoHeightEnabled: true,initialFrameHeight:612});
	editor.ready(function() {
		//editor.setHeight(612);
		editor.setWidth(730);
		var imgstr="";
		$(pluginHandle.editdom).find("img").each(function(index,element){
			imgstr+=this.outerHTML;
		});
		editor.setContent(imgstr);
	});
});

</script>