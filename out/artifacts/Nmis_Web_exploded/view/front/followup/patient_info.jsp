<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<c:set var="input_width" value="240"/>
<c:set var="label_width" value="100"/>
<title>Insert title here</title>
</head>
<body>
<form id="patientinfo_form" method="post">
<div class="easyui-tabs" data-options="plain:true,narrow:true,tabHeight:30,tabWidth:140,tabPosition:'top'" style="height:380px;">
	<div title="患者信息" data-options="selected:true,border:false" style="padding:5px;">
		<div class="followup_content_div">
			<input class="easyui-textbox"  label="病人编号：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
		  			id="patientid" name="patientid" readonly="readonly">
		</div>
		<div class="followup_content_div">
			<input class="easyui-textbox"  label="姓名：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
		  			name="patientname" readonly="readonly">
		</div>
		<div class="followup_content_div">
			<input class="easyui-textbox"  label="性别：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
		  			name="sexdisplay" readonly="readonly">
		</div>
		<div class="followup_content_div">
			<input class="easyui-textbox"  label="联系电话：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
		  			name="telephone" readonly="readonly">
		</div>
		<div class="followup_content_div">
			<input class="easyui-textbox"  label="检查编号：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
		  			name="studyid" readonly="readonly">
		</div>
		<div class="followup_content_div">
			<input class="easyui-textbox"  label="检查日期：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
		  			name="studydatetime" readonly="readonly">
		</div>
		<div class="followup_content_div">
			<input class="easyui-textbox"  label="检查项目：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width*2+3}px;" 
		  			name="studyitems" readonly="readonly">
		</div>
		<div style="width:100%;float:left;">
			<input class="easyui-textbox" label="临床诊断：" labelWidth="250" labelPosition="top" style="width:98%;height:90px;"
	   				name="admittingdiagnosis" data-options="multiline:true,editable:false">
		</div>
		<div style="width:100%;float:left;">
			<input class="easyui-textbox" label="临床资料/病情摘要：" labelWidth="${label_width*2}" labelPosition="top" style="width:98%;height:190px;"
	   				name="briefcondition" data-options="multiline:true,editable:false">
		</div>
	</div>
	<div title="影像检查" data-options="border:false" style="padding:0px 5px 5px 5px;">
		<div style="width:100%;">
			<input class="easyui-textbox" label="检查所见：" labelWidth="${label_width*2}" labelPosition="top" style="width:98%;height:220px;"
	   				name="checkdesc_txt" data-options="multiline:true,editable:false">
		</div>
		<div style="width:100%;">
			<input class="easyui-textbox" label="检查诊断：" labelWidth="${label_width*2}" labelPosition="top" style="width:98%;height:120px;"
	   				name="checkresult_txt" data-options="multiline:true,editable:false">
		</div>
	</div>
</div>
</form>
<script type="text/javascript">

</script>
</body>
</html>