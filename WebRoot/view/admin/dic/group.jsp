<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript" src="http://www.w3cschool.cc/try/jeasyui/datagrid-detailview.js"></script>
	<%-- <script type="text/javascript" src="${ctx}/js/front/schedule.js"></script>
	<script type="text/javascript"></script> --%>
<title>Insert title here</title>

</head>
<body>
	<div class="easyui-panel" data-options="fit:true" style="padding:10px;">
	<header style="color:#8aa4af;">您当前的位置：字典管理  > 设备分组</header>
		<table id="groupdg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_group',
				url:'${ctx}/dic/findGroup?deleted=0',autoRowHeight:true,fitColumns:true,
				onRowContextMenu: function(e,index ,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',index);
		            $('#cmenu_group').menu('show', {
		                left:e.pageX,
		                top:e.pageY
		            });
		        }">
			<thead>
				<tr>
					<th data-options="field:'gpname',width:40">组名</th>
					<th data-options="field:'type',width:40">类型</th>						
					<th data-options="field:'description',width:80">具体描述</th>
					<th data-options="field:'default_duration',width:60">默认持续时间</th>
					<th data-options="field:'operate',width:55,align:'center',formatter:operatecolumn_group">操作</th>
				</tr>
			</thead>
        </table>
        <div id="cmenu_group" class="easyui-menu"  style="padding:2px 2px;">
			<shiro:hasPermission name="edit_equipGroup">
		        <div onclick="new_groupdlg();">新建</div>
		        <div onclick="modifyGroupdlg();">修改</div>
		        <div onclick="deleteGroup();">删除</div>
		  	</shiro:hasPermission>
		 </div>
		 <div id="toolbar_div_group" style="padding:2px 5px;text-align:right;">	
			<shiro:hasPermission name="edit_equipGroup">
				<a class="easyui-linkbutton easyui-tooltip" title="新建组" onClick="new_groupdlg();">新建</a>
		    </shiro:hasPermission>
		    <input class="easyui-combobox" id="modalityid_search" label="检查设备:" labelWidth="75px" style="width:200px;height:30px;"
	        	data-options="valueField:'id',textField:'modality_name',url:'${ctx}/dic/findDic?deleted=0',
	        	panelHeight:'auto',editable:false,onChange:doSearchGroupByModality,
	        	icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]"> 
			<input class="easyui-searchbox" id="GroupName_search" style="width:300px;"
				data-options="prompt:'请输入组名' ,searcher:doSearchGroup">
		</div>
	</div>
	
	<script type="text/javascript">

	$('#groupdg').datagrid({
		view: detailview,
		detailFormatter: function(index,row){
			return '<div style="height:200px;border:groove"><table class="tableDetail"></table></div>';
		},
		onExpandRow: function(index,row){
			var ddv=$(this).datagrid('getRowDetail',index).find('table.tableDetail');
			ddv.datagrid({
				fit: true,
				border: false,
				url:'${ctx}/dic/findDicByGroup?groupid='+row.id,
				singleSelect: true,
				height:'auto',
				columns:[					
					[{field:'modality_name',title:'设备名',width:150},
					{field:'type',title:'类型',width:60},
					{field:'roledisplayname',title:'角色',width:80},				
					{field:'manufacturer',title:'制造商',width:150},
					{field:'model',title:'型号',width:100},
					{field:'hostname',title:'主机',width:100},					
					{field:'location',title:'机房',width:120},]
					],
		
			onResize:function(){
				$('#groupdg').datagrid('fixDetailRowHeight',index);
			},
			onLoadSuccess:function(){
				setTimeout(function(){
					$('#groupdg').datagrid('fixDetailRowHeight',index);
				},0);
			}
		});
		$('#groupdg').datagrid('fixDetailRowHeight',index);
	}
});
</script>

</body>
</html>