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
	    <header style="color:#8aa4af;">您当前的位置：字典管理  > 检查项目</header>
		<table id="examitemdicdg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_examitemdic',
				autoRowHeight:true,fitColumns:true,
				onRowContextMenu: function(e,index ,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',index);
		            $('#cmenu_examitemdic').menu('show', {
		                left:e.pageX,
		                top:e.pageY
		            });
		        }">
			<thead>
					<tr>
						<th data-options="field:'type',width:40">设备类型</th>
						<th data-options="field:'item_name',width:100">项目名</th>
						<th data-options="field:'item_code',width:100">项目编号</th>
						<th data-options="field:'organ',width:30">部位</th>
						<th data-options="field:'suborgan',width:30">子部位</th>
						<th data-options="field:'price',width:30">价格</th>
						<th data-options="field:'report_alert_hour',width:40">报警时间（时）</th>
						<th data-options="field:'report_alert_minute',width:40">报警时间（分）</th>
						<th data-options="field:'duration',width:40">持续时间</th>					
						<th data-options="field:'fulldescription',width:60">具体描述</th>
						<th data-options="field:'operate',width:60,align:'center',formatter:operatecolumn_examitemdic">操作</th>
					</tr>
			</thead>
        </table>
        <div id="cmenu_examitemdic" class="easyui-menu"  style="padding:2px 2px;">
			<shiro:hasPermission name="edit_examItem">
		        <div onclick="new_examitemdlg();">新建</div>
		        <div onclick="Modifyexamitemdlg();">修改</div>
		        <div onclick="deletExamitemdic();">删除</div>
		     </shiro:hasPermission> 
		 </div>
		 <div id="toolbar_div_examitemdic" style="padding:2px 5px;text-align:right;">
		 	<shiro:hasPermission name="edit_examItem">
				<a class="easyui-linkbutton easyui-tooltip" title="新建检查项目" onClick="new_examitemdlg();">新建</a>
			</shiro:hasPermission>
			<input class="easyui-combobox" id="type_search" label="类型:" labelWidth="60px" style="width:220px;height:30px;"
	        	data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0004',
	        	panelHeight:'120px',editable:false,
	        	onChange:doSearchItemByType,
	        	onLoadSuccess:function(data){
		        	if(data){$(this).combobox('select', data[0].code)}
		        	
		        },
	        	icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]" > 
	        <!-- <input class="easyui-combobox" id="modalityid_search" label="检查设备:" labelWidth="75px" style="width:200px;height:30px;"
	        	data-options="valueField:'id',textField:'modality_name',
	        	panelHeight:'120px',editable:false,onChange:doSearchItemByModality,
	        	icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]"> -->
			<input class="easyui-searchbox" id="itemName_search"
				data-options="prompt:'请输入项目名' ,searcher:doSearchExamitemdic" style="width:150px;">
		</div>
	</div>
	
<script type="text/javascript">

$('#examitemdicdg').datagrid({
	view: detailview,
	detailFormatter: function(index,row){
		return '<div style="height:200px;border:groove"><table class="tableDetail"></table></div>';
	},
	onExpandRow: function(index,row){
		var ddv=$(this).datagrid('getRowDetail',index).find('table.tableDetail');
		ddv.datagrid({
			fit: true,
			border: false,
			url:'${ctx}/dic/getExamEquip?id='+row.id,
			singleSelect: true,
			height:'auto',
			columns:[					
				[{field:'modality_name',title:'设备名',width:150},
				{field:'type',title:'类型',width:60},
				{field:'roledisplayname',title:'角色',width:80},				
				{field:'manufacturer',title:'制造商',width:150},
				{field:'model',title:'型号',width:120},
				{field:'hostname',title:'主机',width:100},					
				{field:'location',title:'机房',width:150},]
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