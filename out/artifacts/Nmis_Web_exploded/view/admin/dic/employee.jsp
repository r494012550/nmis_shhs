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
	<header style="color:#8aa4af;">您当前的位置：字典管理  > 员工管理</header>
	<table id="employeedg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_employee',
				url:'${ctx}/dic/getEmployee?deleted=0',
				autoRowHeight:true,fitColumns:true,scrollbarSize:0,
				onRowContextMenu: function(e,index ,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',index);
		            $('#cmenu_employee').menu('show', {
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
						<th data-options="field:'jobnumber',width:140">工号</th>
						<th data-options="field:'name',width:140">姓名</th>
						<th data-options="field:'deptname',width:140">所在科室</th>						
						<!-- <th data-options="field:'deptcode',width:140">科室代码</th>	
						<th data-options="field:'deptfk',width:140">科室fk</th> -->						
						<th data-options="field:'profession',width:140">职务</th>
				
						<th data-options="field:'operate',width:100,align:'center',formatter:operatecolumn_employee">操作</th>
					</tr>
			</thead>
        </table>
        <div id="cmenu_employee" class="easyui-menu"  style="padding:2px 2px;">
			<shiro:hasPermission name="edit_employeedic">
		        <div onclick="openNewEmployeeDialog();">新建员工</div>
		        <div onclick="openModifyEmployeeDialog();">修改员工</div>
		        <div onclick="deleteEmployee();">删除员工</div>
		    </shiro:hasPermission>
		    <shiro:hasPermission name="edit_user">
		        <div class="menu-sep"></div>
		        <div onclick="openNewUserDialog();">${sessionScope.locale.get('admin.createuser')}</div>
		  	</shiro:hasPermission>
		 </div>
		 <div id="toolbar_div_employee" style="padding:2px 5px;text-align:right;">
					
			<shiro:hasPermission name="edit_employeedic">
				<a class="easyui-linkbutton easyui-tooltip" title="" onClick="openNewEmployeeDialog();">新建员工</a>
			</shiro:hasPermission>
			<shiro:hasPermission name="edit_user">
				<a class="easyui-linkbutton easyui-tooltip" title="" onClick="openNewUserDialog();">${sessionScope.locale.get('admin.createuser')}</a>
				<a class="easyui-linkbutton easyui-tooltip" title="" onClick="goEditUserFromEmployee();">修改用户</a>
			</shiro:hasPermission>
				<input class="easyui-combobox" id="institutionid_search" label="机构：" labelWidth="50px" labelAlign="right" style="width:200px;height:30px;"
	                data-options="valueField: 'id',textField: 'name',url:'${ctx}/dic/getInstitution?deleted=0',
	                panelHeight:'120px',editable:false,onChange:doSearchEmployeeByInstitutionId,
	                icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]">
		        <input class="easyui-combobox" id="deptname" label="所在科室:" labelWidth="75px" style="width:200px;height:30px;"
		        	data-options="valueField:'id',textField:'deptname',
		        	panelHeight:'auto',panelHeight:'auto',editable:false,onChange:doSearchEmployeeBydept,
		        	icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]"/>
		        <input class="easyui-combobox" id="professionName" label="员工职务：" labelWidth="75px" style="width:200px;height:30px;"
		        	data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0019',
		        	panelHeight:'auto',panelHeight:'auto',editable:false,onChange:doSearchEmployeeByprofession,
		        	icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]"/>
				<input class="easyui-searchbox" id="employeeName" style="width:200px;"
					data-options="prompt:'请输入员工姓名' ,searcher:doSearchEmployee" >
		</div>
	</div>

</body>
</html>