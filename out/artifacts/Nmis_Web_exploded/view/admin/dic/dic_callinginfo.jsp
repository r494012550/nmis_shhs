<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript" src="http://www.w3cschool.cc/try/jeasyui/datagrid-detailview.js"></script>
<title>Insert title here</title>

</head>
	
<body>
<div class="easyui-panel" data-options="fit:true" style="padding:10px;">
	<header style="color:#8aa4af;">您当前的位置：字典管理 > 叫号信息</header>

	<table id="diccallingdlg"  class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,rownumbers: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_diccalling',			
				autoRowHeight:true,fitColumns:true,
				url:'${ctx}/dic/getDicCalling',
				onContextMenu: function(e,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',row.id);
		            $('#cmenu_diccalling').menu('show', {
		                left:e.pageX,
		                top:e.pageY
		            });
		        },
		        view:groupview,
                groupField:'modalityname',
                groupFormatter:function(value,rows){
                    return value + ' - ' + rows.length + ' ${sessionScope.locale.get('admin.items')}';
                }"
				>
		        
		<thead>
				<tr>
					<th data-options="field:'modalityname',width:100">设备名称</th>
					<th data-options="field:'message',width:100">提示信息</th>
					<th data-options="field:'priority',width:80">优先级</th>
					<th data-options="field:'createtime',width:100">创建时间</th>
					<th data-options="field:'operate',width:60,align:'center',formatter:operatecolumn_diccalling">操作</th>
				</tr>
			</thead>
		        
	</table>
	
		 <div id="toolbar_div_diccalling" style="padding:2px 5px;text-align:right;">
		 		<shiro:hasPermission name="edit_organdic">
				<a class="easyui-linkbutton easyui-tooltip" title="新建设备叫号提示信息" onClick="new_callingdic();">新建</a>
		        </shiro:hasPermission>
				
				<input class="easyui-combobox easyui-tooltip" id="modality_diccalling" label="设备名称:" labelWidth="80"
	        	data-options="valueField:'id',textField:'modality_name',url:'${ctx}/calling/getModalityName',editable:false,
	        	onChange:selectDicCalling,icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]" 
	        	name="type" style="width:250px;height:30px;"> 		 		
		</div>
	 	<div id="cmenu_diccalling" class="easyui-menu"  style="padding:2px 2px;">
	 		<shiro:hasPermission name="edit_organdic">	        
		        <div onclick="ModifyDicCalling();">修改</div>		   
		        <div onclick="deleteDicCalling();">删除</div>
		  	</shiro:hasPermission>
		 </div>
	

<script type="text/javascript">

</script>
</div>	
</body>
</html>