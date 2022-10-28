<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
		<div class="easyui-panel" data-options="fit:true" style="padding:10px;">
			<header style="color:#8aa4af;">${sessionScope.locale.get('admin.currentlocation.authority')}</header>
			<table id="audg" class="easyui-datagrid" style="width:100%;" 
				data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_au',
				url:'${ctx}/getAllAuth',autoRowHeight:true,fitColumns:true,
				loadMsg:'${sessionScope.locale.get('loading')}...',emptyMsg:'${sessionScope.locale.get('nosearchresults')}...',
				onRowContextMenu: function(e,index ,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',index);
		            $('#cmenu_au').menu('show', {
		                left:e.pageX,
		                top:e.pageY
		            });
		        },
		        view:groupview,
				groupField:'modulename',
		        groupFormatter:function(value,rows){
                    return value + ' - ' + rows.length + ' ${sessionScope.locale.get('admin.items')}';
                }">
				<thead>
					<tr>
						<th data-options="field:'name',width:100">${sessionScope.locale.get('admin.authorityname')}</th>
						<th data-options="field:'description',width:300">${sessionScope.locale.get('admin.authoritydesc')}</th>
						<th data-options="field:'operate',width:60,align:'center',formatter:operatecolumn_auth">${sessionScope.locale.get('admin.operation')}</th>
					</tr>
				</thead>
			</table>
			
			<div id="cmenu_au" class="easyui-menu" style="width:120px;">
				<shiro:hasPermission name="edit_authority">
		        <div onclick="openNewAuDialog();">${sessionScope.locale.get('admin.createauthority')}</div>
		        <div onclick="openModifyAuDialog();">${sessionScope.locale.get('admin.modifyauthority')}</div>
		        <div class="menu-sep"></div>
		        <div onclick="deleteAuth();">${sessionScope.locale.get('admin.deleteauthority')}</div>
		        </shiro:hasPermission>
		    </div>
			<div id="toolbar_div_au" style="padding:2px 5px;text-align:right;">	
				<shiro:hasPermission name="edit_authority">
				<a class="easyui-linkbutton easyui-tooltip" title="${sessionScope.locale.get('admin.createauthority')}" onClick="openNewAuDialog();">${sessionScope.locale.get('admin.add')}</a>
		        </shiro:hasPermission>
		        <input class="easyui-searchbox" data-options="prompt:'${sessionScope.locale.get('admin.enterauthoritynameordesc')}',searcher:doSearchAuth" style="width:300px">       
			</div>
		</div>
</body>
</html>