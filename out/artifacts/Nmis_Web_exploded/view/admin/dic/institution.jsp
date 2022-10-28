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
	<header style="color:#8aa4af;">您当前的位置：字典管理  > 机构管理</header>
	<table id="Institutiondg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter:true,singleSelect:true,fit:true,toolbar:'#toolbar_div_Institution',
				url:'${ctx}/dic/getInstitution?deleted=0',autoRowHeight:true,fitColumns:true,
				onRowContextMenu: function(e,index ,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',index);
		            $('#cmenu_Institution').menu('show',{
		                left:e.pageX,
		                top:e.pageY
		            });
		        },">
			<thead>
					<tr>
						<th data-options="field:'name',width:140">机构名称</th>
						<th data-options="field:'code',width:140">机构代码</th>
						<th data-options="field:'address',width:140">机构地址</th>						
						<th data-options="field:'note',width:140">备注</th>
						<th data-options="field:'operate',width:140,align:'center',formatter:operatecolumn_Institution">操作</th>
					</tr>
			</thead>
        </table>
        <div id="cmenu_Institution" class="easyui-menu"  style="padding:2px 2px;">
			
		        <div onclick="new_Institution();">新建</div>
		        <div onclick="modifyInstitution();">修改</div>
		        <div onclick="editInstitutionRole();">添加角色</div>
		        <div onclick="deleteInstitution();">删除</div>
		  
		 </div>
		 <div id="toolbar_div_Institution" style="padding:2px 5px;text-align:right;">

				<a class="easyui-linkbutton easyui-tooltip" title="新建医院" onClick="new_Institution();">新建</a>
				<input class="easyui-searchbox" data-options="prompt:'请输入医院名' ,searcher:doSearchInstitution" style="width:300px;">
		</div>
	</div>

</body>
</html>