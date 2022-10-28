<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<c:set var="input_width" value="200"/>
<c:set var="label_width" value="50"/>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div style="width:100%;height:100%">
			<div class="easyui-panel" id="personnel_panel" style="height:100%;width:100%;">
				<table id="personnel_dg" class="easyui-datagrid" style="width:100%;" data-options="url:'${ctx}/research/searchPersonnel?taskId=${taskId}',showFooter: true,singleSelect:true,fit:true,autoRowHeight:true,fitColumns:true,toolbar:'#personnel_toolbar',scrollbarSize:0,loadMsg:'加载中...',emptyMsg:'没有查找到人员信息...'  ">
					<thead>
						<tr> 
							<th data-options="field:'name',width:100">员工姓名</th>
							<th data-options="field:'rolename',width:100">角色</th>
							<th data-options="field:'description',width:100">描述</th>
							<th data-options="field:'operate',width:60,align:'center',formatter:personnel_formatter">${sessionScope.locale.get("admin.operation")}</th>
						</tr>
					</thead>
				</table>
			</div>
			<div id="personnel_toolbar" style="padding:2px 2px;text-align:right;">
				<input id="personnel" class="easyui-combobox"   label="员工:"  labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" data-options="valueField:'id',textField:'name',url:'${ctx}/getAllUsers?deleted=0'">
				<input id="personnelRole" class="easyui-combobox"   label="所属角色:"  labelWidth="${label_width+30}" labelAlign="right" style="height:25px;width:${input_width}px;" data-options="valueField:'id',textField:'rolename',url:'${ctx}/research/searchTaskRole'">
			</div>
   	</div>
   	<script type="text/javascript">
   	
	
    </script>
</body>1
</html>