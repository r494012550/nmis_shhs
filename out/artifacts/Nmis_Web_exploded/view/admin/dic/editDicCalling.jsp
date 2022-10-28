<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
 <form name="diccallingform" id="diccallingform" method="POST">
     
      <div style="padding:10px 10px 10px 10px; margin-left:auto; margin-right:auto; width:300px;">
		<div style="margin-bottom:10px">
			<input class="easyui-combobox"  id="treename_zh" label="设备名称：" labelWidth="120"
	        	data-options="valueField:'id',textField:'modality_name',required:'true',url:'${ctx}/calling/getModalityName',editable:false" 
                name="modalityid" style="width:300px;height:30px;" value="${detail.modalityid}">
		</div>
		<div style="margin-bottom:10px">
			<input class="easyui-textbox"  id="dicmessage" label="提示：" labelWidth="120"
	        	data-options="prompt:'请输入提示信息...',missingMessage:'必填',required:'true'" 
	        	name="message" style="width:300px;height:30px;" value="${detail.message}">
		</div>

		<div style="margin-bottom:10px">
			<input class="easyui-numberbox"  id="priority" label="优先级：" labelWidth="120"
	        	data-options="prompt:'请输入提示排序优先级...',missingMessage:'必填',required:'true'" 
	        	name="priority" style="width:300px;height:30px;" value="${detail.priority}">
		</div>
	   
		<div>
			<input id="id" name="id" type="hidden" value="${detail.id}"/>
		</div>
</div>
</form>
<script type="text/javascript">
function getCode(){
	$("#typecode").textbox('setValue',$("#treename_zh").toPinyinFirst());
}
</script>
</body>
</html>