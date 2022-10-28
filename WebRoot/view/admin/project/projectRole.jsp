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
		<header style="color:#8aa4af;">您当前的位置：项目管理  > 项目角色</header>
		<table id="taskRoledg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_statistics',
				url:'${ctx}/research/searchProjectRole',autoRowHeight:true,fitColumns:true,scrollbarSize:0,
				loadMsg:'加载中...',emptyMsg:'没有查找到角色...'">
			<thead>
				<tr>
					<th data-options="field:'rolename',width:100">角色名称</th>
					<th data-options="field:'description',width:100">描述</th>
					<th data-options="field:'user_level',width:100">用户级别</th>
					<th data-options="field:'createtime',width:100">创建时间</th>
					<th data-options="field:'operate',width:60,align:'center',formatter:projectRole_formatter">${sessionScope.locale.get("admin.operation")}</th>
				</tr>
			</thead>
		</table>
		<div id="toolbar_div_statistics" style="padding:2px 2px;text-align:right;">
			<a class="easyui-linkbutton easyui-tooltip" title="新建角色"  onClick="openProjectRoleDlg(null);">新建</a>
			<input class="easyui-searchbox" data-options="prompt:'请输入角色名称',searcher:searchTaskRole" style="width:300px;">	
		</div>
	</div>
</body>
</html>