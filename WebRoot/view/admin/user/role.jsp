<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
		<div class="easyui-panel" data-options="fit:true" style="padding:10px;">
			<header style="color:#8aa4af;">${sessionScope.locale.get('admin.currentlocation.role')}</header>
			<table id="roledg" class="easyui-datagrid" style="width:100%;"
				data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_role',
				url:'${ctx}/getAllRole',autoRowHeight:true,fitColumns:true,
				loadMsg:'${sessionScope.locale.get('loading')}...',emptyMsg:'${sessionScope.locale.get('nosearchresults')}...',
				onRowContextMenu: function(e,index ,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',index);
		            $('#cmenu_role').menu('show', {
		                left:e.pageX,
		                top:e.pageY
		            });
		        }">
				<thead>
					<tr>
						<th data-options="field:'rolename',width:200">${sessionScope.locale.get('admin.rolename')}</th>
						<th data-options="field:'createtime',width:100">${sessionScope.locale.get('admin.createtime')}</th>
						<th data-options="field:'operate',width:60,align:'center',formatter:operatecolumn_role">${sessionScope.locale.get('admin.operation')}</th>
					</tr>
				</thead>
			</table>
			
			<div id="cmenu_role" class="easyui-menu" style="width:120px;">
		        <shiro:hasPermission name="edit_role">
		        <div onclick="openNewRoleDialog();">${sessionScope.locale.get('admin.createrole')}</div>
		        <div onclick="openModifyRoleDialog();">${sessionScope.locale.get('admin.modifyrole')}</div>
		        <div class="menu-sep"></div>
		        <div onclick="deleteRole();">${sessionScope.locale.get('admin.deleterole')}</div>
		        </shiro:hasPermission>
		    </div>
			<div id="toolbar_div_role" style="padding:2px 5px;text-align:right;">
				<shiro:hasPermission name="edit_role">
				<a class="easyui-linkbutton easyui-tooltip" title="${sessionScope.locale.get('admin.createrole')}" onClick="openNewRoleDialog();">${sessionScope.locale.get('admin.create')}</a>
				</shiro:hasPermission>
				<input class="easyui-searchbox" data-options="prompt:'${sessionScope.locale.get('admin.inputrolename')}',searcher:doSearchRole" style="width:300px">	        
			</div>
		</div>
</body>
</html>