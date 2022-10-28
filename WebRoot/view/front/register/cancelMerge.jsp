<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title>登记</title>
</head>
<body>
	<table id="patientdg3_reg" class="easyui-datagrid" title="被合并的患者列表" 
		data-options="singleSelect:true,fitColumns:true,toolbar:'#toolbar_merge_cancel',fit:true,border:false,
		loadMsg:'加载中...',emptyMsg:'没有查找到登记信息...'">
		<thead>
			<tr>
				<th data-options="field:'ck',checkbox:true"></th>
				<th data-options="field:'patientid',width:80">编号</th>
		        <th data-options="field:'patientname',width:80">姓名</th>
		        <th data-options="field:'sexdisplay',width:50">性别</th>
		        <th data-options="field:'birthdate',width:70">出生日期</th>
		        <th data-options="field:'telephone',width:80">联系电话</th>
		        <th data-options="field:'address',width:100">联系地址</th>
			</tr>
		</thead>
	</table>
	
	<div id="toolbar_merge_cancel">
		<input class="easyui-textbox" id="patientid3_reg" label="病人编号：" labelPosition="left" style="width:220px;height:25px;">
		<input class="easyui-textbox" id="patientname3_reg" label="病人姓名：" labelPosition="left" style="width:220px;height:25px;">
		<a class="easyui-linkbutton" style="width:80px;height:28px" onclick="findRecycledPat()">查找</a>
	</div>
</body>
</html>