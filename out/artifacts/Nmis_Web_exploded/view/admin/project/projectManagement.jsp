<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div class="easyui-panel" data-options="fit:true" style="padding:10px;">
		<header style="color:#8aa4af;">您当前的位置：项目管理  &gt; 项目管理</header>
	 	<table id="taskMaintaindg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_task_maintain',scrollbarSize:0,
				url:'${ctx}/research/searchProject',autoRowHeight:true,fitColumns:true,
				loadMsg:'加载中...',emptyMsg:'没有查找到项目...'">
			<thead>
				<tr>
					<th data-options="field:'name',width:100">项目名称</th>
					<th data-options="field:'type',width:100">项目类别</th>
					<th data-options="field:'description',width:200">项目描述</th>
					<th data-options="field:'createtime',width:80">创建时间</th>
					<th data-options="field:'gxry',width:100,align:'center',formatter:taskMaintain_personnel">相关人员</th>
					<th data-options="field:'operate',width:60,align:'center',formatter:taskMaintain_formatter">${sessionScope.locale.get("admin.operation")}</th>
				</tr>
			</thead>
		</table>
		<div id="toolbar_div_task_maintain" style="padding:2px 2px;text-align:right;">
			<a id="addTaskBtn" class="easyui-linkbutton easyui-tooltip" title="新建项目" onClick="openTaskDlg(null);">新建</a>
			<input class="easyui-searchbox" data-options="prompt:'请输入项目名称',searcher:findProject" style="width:300px;">
		</div>
	</div>
</body>
</html>