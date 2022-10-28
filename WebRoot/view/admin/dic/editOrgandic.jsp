<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
 <form name="organdicform" id="organdicform" method="POST">
     
      <div style="padding:10px 10px 10px 10px; margin-left:auto; margin-right:auto; width:300px;">
		<div style="margin-bottom:10px">
			<input class="easyui-textbox"  id="treename_zh" label="部位名称（中）：" labelWidth="120"
	        	data-options="prompt:'请输入部位名称（中）...',required:true,missingMessage:'必填',onChange:getCode" 
	        	name="treename_zh" style="width:300px;height:30px;" value="${organ.treename_zh}">
		</div>
		<div style="margin-bottom:10px">
			<input class="easyui-textbox"  id="treename_en" label="部位名称（英）：" labelWidth="120"
	        	data-options="prompt:'请输入部位名称（英）...',missingMessage:'必填'" 
	        	name="treename_en" style="width:300px;height:30px;" value="${organ.treename_en}">
		</div>

		<div style="margin-bottom:10px">
			<input class="easyui-textbox"  id="typecode" label="代码：" labelWidth="120"
	        	data-options="prompt:'请输入代码...',missingMessage:'必填'" 
	        	name="typecode" style="width:300px;height:30px;" value="${organ.typecode}">
		</div>
	   
		<div>
			<input id="id" name="id" type="hidden" value="${organ.id}"/>
			<input name="modality" type="hidden" value="${organ.modality}"/>
			<input id="parentid" name="parentid" type="hidden" value="${organ.parentid}"/>
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