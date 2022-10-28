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
	<header style="color:#8aa4af;">您当前的位置：字典管理  > 科室管理</header>
	<table id="departmentdg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter:true,singleSelect:true,fit:true,toolbar:'#toolbar_div_department',
				url:'${ctx}/dic/findDepartment?deleted=0',autoRowHeight:true,fitColumns:true,border:true,
				onRowContextMenu: function(e,index ,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',index);
		            $('#cmenu_department').menu('show',{
		                left:e.pageX,
		                top:e.pageY
		            });
		        },
		        view:groupview,
                groupField:'institutionname',
                groupFormatter:function(value,rows){
                    return value + ' - ' + rows.length + ' ${sessionScope.locale.get('admin.items')}';
                }">
			<thead>
					<tr>
						<!-- <th data-options="field:'institutionname',width:140">机构名称</th> -->
						<th data-options="field:'deptcode',width:140">科室代码</th>
						<th data-options="field:'deptname',width:140">科室名称</th>						
						<th data-options="field:'type',width:140">科室类型</th>
						<th data-options="field:'shifts',width:150">班次</th>
						<th data-options="field:'operate',width:140,align:'center',formatter:operatecolumn_department">操作</th>
					</tr>
			</thead>
        </table>
        <div id="cmenu_department" class="easyui-menu"  style="padding:2px 2px;">
			<shiro:hasPermission name="edit_departmentdic">
		        <div onclick="new_department();">新建</div>
		        <div onclick="Modifydepartment();">修改</div>
		   
		        <div onclick="deleteDepartment();">删除</div>
		  	</shiro:hasPermission>
		 </div>
		 <div id="toolbar_div_department" style="padding:2px 5px;text-align:right;">	
			<shiro:hasPermission name="edit_departmentdic">
				<a class="easyui-linkbutton easyui-tooltip" title="新建科室" onClick="new_department();">新建</a>
		    </shiro:hasPermission>
			    <input class="easyui-combobox" id="institutionid_search" label="机构：" labelWidth="50px" labelAlign="right" style="width:250px;height:30px;"
	                data-options="valueField: 'id',textField: 'name',url:'${ctx}/dic/getInstitution?deleted=0',
	                panelHeight:'200px',editable:false,onChange:doSearchDicDepartment,
	                icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]">
				<input id="departmentName" class="easyui-searchbox" data-options="prompt:'请输入科室名' ,searcher:doSearchdepartment" style="width:300px;">
		</div>
	</div>

</body>
</html>