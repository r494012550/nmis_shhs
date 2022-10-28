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
		<header style="color:#8aa4af;">您当前的位置：系统管理  > 系统代码</header>
		<table id="syscodedg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_syscode',
				url:'${ctx}/syscode/findSyscode',autoRowHeight:true,fitColumns:true,
				loadMsg:'加载中...',emptyMsg:'没有查找到报表...',
				onRowContextMenu: function(e,index ,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',index);
		            $('#cmenu_syscode').menu('show', {
		                left:e.pageX,
		                top:e.pageY
		            });
		        },
		        view:groupview,
		        groupField:'group',
                groupFormatter:function(value,rows){
                    return value + ' - ' + rows.length + ' 个';
                }">
			<thead>
				<tr>
					
					<th data-options="field:'code',width:100">代码</th>
					<th data-options="field:'name_zh',width:100">中文名</th>
					<th data-options="field:'name_en',width:100">英文名</th>
					<th data-options="field:'type',width:100">模块</th>
					<th data-options="field:'operate',width:60,align:'center',formatter:operatecolumn_syscode">操作</th>
				</tr>
			</thead>
        </table>
        
        <div id="cmenu_syscode" class="easyui-menu" style="width:120px;">
        	<shiro:hasPermission name="edit_syscode">
	        <div onclick="openEditSyscodeDlg(null);">新建</div>
	        <div onclick="openEditSyscodeGroupDlg(null);">新建分组</div>
	        <div class="menu-sep"></div>
	        <div onclick="openModifySyscodeDlg();">修改</div>
	        <div onclick="deleteSyscode();">删除</div>
	    	</shiro:hasPermission>
	    </div>
		<div id="toolbar_div_syscode" style="padding:2px 2px;text-align:right;">
			
			<shiro:hasPermission name="edit_syscode">
			<a class="easyui-linkbutton easyui-tooltip" style="width:90px;" title="新建" onClick="openEditSyscodeDlg(null);">新建</a>
			<a class="easyui-linkbutton easyui-tooltip" style="width:90px;" title="新建分组" onClick="openEditSyscodeGroupDlg(null);">新建分组</a>
			</shiro:hasPermission>
			
			<input class="easyui-searchbox" data-options="prompt:'请输入名称...',searcher:doSearchSyscode" style="width:300px;">	
		</div>
	</div>

</body>
</html>