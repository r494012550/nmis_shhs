<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title>登记</title>
</head>
<body>
<div>
	<table id="admission1_split" class="easyui-datagrid" title="" style="width:853px;height:540px;"
			data-options="showFooter: false,loadMsg:'loading',emptyMsg:'无入院记录...',singleSelect: true,
			checkbox: true,fitColumns:true,border:false,nowrap:false,
			url:'${ctx}/register/getAdmission?patientkid=${patientidfk}'">
		<thead>
		    <tr>
		    	<th data-options="field:'ck',checkbox:true"></th>
		    	<th data-options="field:'admissionid',width:80">入院编号</th>
		    	<th data-options="field:'psource',width:80">病人来源</th>
		        <th data-options="field:'cardno',width:50">卡号</th>
		        <th data-options="field:'outno',width:70">门诊号</th>
		        <th data-options="field:'inno',width:80">住院号</th>
		        <th data-options="field:'subjective',width:80">病人主诉</th>
		    	<th data-options="field:'admittingdiagnosis',width:80">入院诊断</th>
		        <th data-options="field:'createtime',width:80">创建时间</th>
		    </tr>
		</thead>
	</table>
</div>
</body>
</html>