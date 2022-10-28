<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Insert title here</title>
</head>
<body>
<form id="studyinfo_form" method="post">
<div class="easyui-panel" data-options="fit:true,border:false" style="padding:3px 3px 0px 3px;margin:auto;">
	<table cellspacing="0" class="mytablelaout">
		<tr>
			<td>
			<input class="easyui-textbox" label="临床诊断：" labelWidth="${label_width}" labelPosition="top" style="width:100%;height:${input_width/2}px;"
   				name="admittingdiagnosis" data-options="multiline:true,editable:false">
			</td>
		</tr>
		<tr>
			<td>
			<input class="easyui-textbox" label="临床资料/病情摘要：" labelWidth="${label_width*2}" labelPosition="top" style="width:100%;height:${input_width/2}px;"
   				name="briefcondition" data-options="multiline:true,editable:false">
			</td>
		</tr>
		<tr>
			<td>
			<input class="easyui-textbox" label="检查所见：" labelWidth="${label_width}" labelPosition="top" style="width:100%;height:${input_width/2}px;"
   				name="checkdesc_txt" data-options="multiline:true,editable:false">
			</td>
		</tr>
		<tr>
			<td>
			<input class="easyui-textbox" label="检查诊断：" labelWidth="${label_width}" labelPosition="top" style="width:100%;height:${input_width/2}px;"
   				name="checkresult_txt" data-options="multiline:true,editable:false">
			</td>
		</tr>
	</table>
</div>
</form>
			
<script type="text/javascript">

</script>
</body>
</html>