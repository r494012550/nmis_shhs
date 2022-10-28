<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title></title>
</head>
<body>
<div style="margin-top: 20px;margin-left: 26px;">
		<a class="easyui-linkbutton" id="time_btn" onclick="getPlazatime();" style="width:120px;height:25px">获取plaza时间</a>
		<a class="easyui-linkbutton" id="time_btn" onclick="getRegistertime();" style="width:120px;height:25px;margin-left: 40px;">获取预约时间</a>
</div>
<div style="margin-top: 20px;margin-left: 20px;">
	<input class="easyui-datetimespinner"  id="sptime_time" style="width:300px;" label="检查完成时间：" labelWidth="120px" labelPosition="left"
	 data-options="showSeconds: true,
		icons:[{iconCls:'icon-clear',handler: function(e){$(e.data.target).datetimespinner('clear');}}]
		">
</div>
<input type="hidden" id="studyid_time" value="${studyid}"> 
<script type="text/javascript">

</script>
</body>
</html>