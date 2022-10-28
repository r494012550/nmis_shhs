<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
	<table id="checkWorkAttendancedg" class="easyui-datagrid" style="width:100%;"
		data-options="singleSelect:true,fit:true,toolbar:'#toolbar_div_checkWorkAttendance',
			autoRowHeight:true,fitColumns:true,scrollbarSize:0">
		<thead>
				<tr>
					<th data-options="field:'name',width:40">姓名</th>
					<th data-options="field:'start_work_time',width:100">上班打卡时间</th>
					<th data-options="field:'end_work_time',width:100">下班打卡时间</th>
					<th data-options="field:'create_time',width:50">日期</th>
				</tr>
		</thead>
       </table>
	 <div id="toolbar_div_checkWorkAttendance" style="padding:2px 5px;text-align:right;">
		<input id="name" class="easyui-combobox" data-options="valueField:'name',textField:'name',panelHeight:'auto',
			url:'${ctx}/getAllUsers'" label="员工姓名:" labelWidth="80px" style="width:200px;height:25px;">
		<input id="sdate" class="easyui-datebox" label="开始日期:"   labelWidth="80px" style="width:200px;height:25px;">
		<input id="edate" class="easyui-datebox" label="结束日期:"   labelWidth="80px" style="width:200px;height:25px;">
		<a class="easyui-linkbutton easyui-tooltip" title="查询" onClick="findCheckWorkAttendance();">查询</a>
	</div>
	
<script type="text/javascript">
	

</script>
</body>
</html>