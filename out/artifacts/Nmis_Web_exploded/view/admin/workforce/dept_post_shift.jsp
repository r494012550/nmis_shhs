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
	<header style="color:#8aa4af;">您当前的位置：排班管理  > 班次管理</header>
	
		<div class="easyui-layout" id="dept_post_shift_layout" data-options="fit:true">
	        <div data-options="region:'north'" style="height:45px;padding:5px;">
	        	<input class="easyui-combobox" id="institutionid_search_deptworktime" label="请选择机构：" labelWidth="100px" labelAlign="right" style="width:280px;height:30px;"
		                data-options="valueField: 'id',textField: 'name',url:'${ctx}/dic/getInstitution?deleted=0',
		                panelHeight:'250px',editable:false
		                ,onChange:function(newvalue,oldvalue){
		                	$('#dept_post_shift_dept_cb').combobox('setValue','');
		                	$('#dept_post_shift_dept_cb').combobox('reload','${ctx}/dic/findDepartment?institutionid='+newvalue+'&value=&deleted=0');
		                	$('#dept_post_shift_dg').datagrid('clearSelections');
		                	$('#dept_post_shift_post_dg').datagrid('clearSelections');
		                }
		                ,onLoadSuccess:function(){
		                	var data=$(this).combobox('getData');
		                	if(data&&data[0]){
		                		$(this).combobox('select',data[0].id);
		                	}
		                }">
	        	<input class="easyui-combobox" id="dept_post_shift_dept_cb" label="请选择科室：" labelWidth="100px" labelAlign="right" style="width:250px;height:30px;"
	                data-options="valueField: 'id',textField: 'deptname',
	                panelHeight:'300px',editable:false
	                ,onLoadSuccess:function(){
	                	var data=$(this).combobox('getData');
	                	if(data&&data[0]){
	                		$(this).combobox('select',data[0].id);
	                	}
	                }
	                ,onChange:function(newvalue,oldvalue){
	                	$('#dept_post_shift_dg').datagrid('clearSelections');
	                	$('#dept_post_shift_post_dg').datagrid('clearSelections');
	                }">
        
	        </div>
	        	
	        <!-- <div data-options="region:'west'" style="width:150px;"></div> -->
	        <div data-options="region:'center'" title="请选择岗位：" >
	        	
		        <div class="easyui-datalist" id="dept_post_shift_post_dg"
					data-options="singleSelect:true,fit:true,valueField:'code',textField:'name_zh',checkbox:true,
						url:'${ctx}/syscode/getCode?type=0019',autoRowHeight:true,border:false
						,onSelect:getDeptPostShifts
						">
		        </div >
	        </div>
	        
	        <div data-options="region:'south',collapsible:false" style="height:45%;" title="班次：">
	        	<table id="dept_post_shift_dg" class="easyui-datagrid"
					data-options="singleSelect:false,fit:true,fitColumns:true,
						url:'${ctx}/workforce/getDeptShift',autoRowHeight:true,border:false,footer:'#dept_post_shift_ft',
						">
					<thead>
							<tr>
								<th data-options="field:'ck',checkbox:true"></th>
								<th data-options="field:'name',width:80">班次名称</th>						
								<th data-options="field:'worktimes',width:500">时间段</th>
							</tr>
					</thead>
		        </table>
		        <div id="dept_post_shift_ft" style="padding:2px 5px;text-align:left;">
			        <a class="easyui-linkbutton" style="width:80px;" onclick="saveDeptPostShift();">保存</a>
			    </div>
	        </div>
	    </div>
		
	</div>

</body>
</html>