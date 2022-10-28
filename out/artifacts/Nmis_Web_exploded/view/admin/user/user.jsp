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
			<header style="color:#8aa4af;">${sessionScope.locale.get("admin.currentlocation.user")}</header>
			<table id="userdg" class="easyui-datagrid" style="width:100%;"
				data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_user',
				url:'${ctx}/getAllUsers?deleted=0',autoRowHeight:true,fitColumns:true,
				loadMsg:'${sessionScope.locale.get('loading')}',emptyMsg:'${sessionScope.locale.get('nosearchresults')}...',
				onRowContextMenu: function(e,index ,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',index);
		            $('#cmenu_user').menu('show', {
		                left:e.pageX,
		                top:e.pageY
		            });
		        }">
				<thead>
					<tr>
						<th data-options="field:'username',width:100">${sessionScope.locale.get("admin.username")}</th>
						<th data-options="field:'name',width:100">${sessionScope.locale.get("admin.name")}</th>
						<th data-options="field:'role',width:100">${sessionScope.locale.get("admin.role")}</th>
						<th data-options="field:'description',width:100">${sessionScope.locale.get("admin.desc")}</th>
						<th data-options="field:'createtime',width:100">${sessionScope.locale.get("admin.createtime")}</th>
						<th data-options="field:'operate',width:60,align:'center',formatter:operatecolumn_user">${sessionScope.locale.get("admin.operation")}</th>
						
					</tr>
				</thead>
			</table>
			
			<div id="cmenu_user" class="easyui-menu" style="width:120px;">
				<shiro:hasPermission name="edit_user">
		        <%-- <div onclick="openNewUserDialog();">${sessionScope.locale.get('admin.createuser')}</div> --%>
		        <div onclick="openModifyUserDialog();">${sessionScope.locale.get('admin.modifyuser')}</div>
		        <div class="menu-sep"></div>
		        <div onclick="deleteUser();">${sessionScope.locale.get('admin.deleteuser')}</div>
		        </shiro:hasPermission>
		        <shiro:hasPermission name="modify_userpassword">
                <div onclick="goModifyUserPassword();">修改密码</div>
                </shiro:hasPermission>
		    </div>
			<div id="toolbar_div_user" style="padding:2px 5px;text-align:right;">
				<input class="easyui-searchbox" data-options="prompt:'${sessionScope.locale.get('admin.inputusernameorname')}',searcher:doSearchUser" style="width:300px;">
			</div>
			
		</div>
	
</body>
</html>