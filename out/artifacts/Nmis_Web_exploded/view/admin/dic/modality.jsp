<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div class="easyui-panel" id="modality_dic_panel" data-options="fit:true" style="padding:10px;">
        <header style="color:#8aa4af;">您当前的位置：字典管理  > 设备管理</header>
		<table id="dicdg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter:true,singleSelect:true,fit:true,toolbar:'#toolbar_div_dic',
				url:'${ctx}/dic/findDic?deleted=0',autoRowHeight:true,fitColumns:true,view: detailview,
				loadMsg:'加载中...',emptyMsg:'没有查找到内容...',detailFormatter:myDetailFormatter,autoRowHeight:true,
				onRowContextMenu: function(e,index ,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',index);
		            $('#cmenu_dic').menu('show', {
		                left:e.pageX,
		                top:e.pageY
		            });
		        }">
			<thead>
				<tr>
					<th data-options="field:'modality_name',width:60" >设备名</th>
					<th data-options="field:'type',width:30">类型</th>
					<th data-options="field:'hostname',width:60">主机名</th>
					<th data-options="field:'location',width:50">机房</th>
					<th data-options="field:'groupname',width:50">组名</th>
					<th data-options="field:'rolename',width:50">设备角色</th>
					<th data-options="field:'manufacturer',width:50" >制造商</th>
					<th data-options="field:'model',width:50">型号</th>
					<!-- 
					<th data-options="field:'averagetime',width:80" >平均时间(分钟)</th>
					<th data-options="field:'workday_of_worktime',width:80" >工作时间（常规）</th>
					<th data-options="field:'saturday_of_worktime',width:80" >工作时间（周六）</th>
					<th data-options="field:'sunday_of_worktime',width:80" >工作时间（周日）</th> -->
					<th data-options="field:'operate',width:60,align:'center',formatter:operatecolumn_dicdg">操作</th>
				</tr>
			</thead>
        </table>
        
        <div id="cmenu_dic" class="easyui-menu" style="width:120px;">
        	<shiro:hasPermission name="edit_modalitydic">
	        <div onclick="openEditDicDig();">新建</div>
	        <div onclick="openModifyDicDlg();">修改</div>
	        <div onclick="deleteDic();">删除</div>
	        </shiro:hasPermission>
	    
	    </div>
		<div id="toolbar_div_dic"  style="padding:2px 5px;text-align:right;">
			<shiro:hasPermission name="edit_modalitydic">
			<a class="easyui-linkbutton easyui-tooltip" title="新建" onClick="openEditDicDig();">新建</a>
	        </shiro:hasPermission>
	        <input class="easyui-combobox" id="institutionid_search" label="机构：" labelWidth="50px" labelAlign="right" style="width:200px;height:30px;"
				data-options="valueField: 'id',textField: 'name',url:'${ctx}/dic/getInstitution?deleted=0',
				panelHeight:'120px',editable:false,onChange:doSearchDic2,
				icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]">
			<input class="easyui-combobox" id="departmentid_search" label="科室:" labelWidth="55px" labelAlign="right" style="width:200px;height:30px;"
				data-options="valueField: 'id',textField: 'deptname',
				panelHeight:'120px',editable:false,onChange:doSearchDic3,
				icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]">	
			<input class="easyui-searchbox" id="dicName_search" data-options="prompt:'请输入设备名...',searcher:doSearchDic1" style="width:300px;">
		</div>
	</div>
	
	<style type="text/css">
		.details{
			border:0px;
		}
		.details td{
			border:0px;
		}
		
		
		.tableheader{
			background-color: #ECECEC;
			font-weight:bold;
		}
		.tableheader1{
			font-weight:bold;
		}
		.servicename{
			text-align:left;
			color:#1A7BC9;
		}
	</style>

<script type="text/javascript">


	function myDetailFormatter(rowIndex, rowData){
		
		return "<table class='details'>"+
					"<tr class='tableheader'>"+
						"<td colspan='3'>服务（SCP）</td>"+
						"<td colspan='3'>服务（SCU）</td>"+
					"</tr>"+
					"<tr class='tableheader1'>"+
						"<td width='160px'>服务</td>"+
						"<td width='200px'>AE-Titel</td>"+
						"<td width='70px'>端口</td>"+
						"<td width='160px'>服务</td>"+
						"<td width='200px'>AE-Titel</td>"+
					"</tr>"+
					"<tr>"+
						"<td class='servicename'>Storage</td>"+
						"<td>"+(rowData.storagescp||"")+"</td>"+
						"<td>"+(rowData.storagescpport||"")+"</td>"+
						"<td class='servicename'>Storage</td>"+
						"<td>"+(rowData.storagescu||"")+"</td>"+
					"</tr>"+
					"<tr>"+
						"<td class='servicename'>StorageCommitment</td>"+
						"<td>"+(rowData.storagecmtscp||"")+"</td>"+
						"<td>"+(rowData.storagecmtscpport||"")+"</td>"+
						"<td class='servicename'>StorageCommitment</td>"+
						"<td>"+(rowData.storagecmtscu||"")+"</td>"+
					"</tr>"+
					"<tr>"+
						"<td class='servicename'>Query</td>"+
						"<td>"+(rowData.qrscp||"")+"</td>"+
						"<td>"+(rowData.qrscpport||"")+"</td>"+
						"<td class='servicename'>Query</td>"+
						"<td>"+(rowData.qrscu||"")+"</td>"+
					"</tr>"+
					"<tr>"+
						"<td class='servicename'>Retrieve</td>"+
						"<td>"+(rowData.qrscp||"")+"</td>"+
						"<td>"+(rowData.qrscpport||"")+"</td>"+
						"<td class='servicename'>Retrieve</td>"+
						"<td>"+(rowData.qrscu||"")+"</td>"+
					"</tr>"+
					"<tr>"+
						"<td class='servicename'>Print</td>"+
						"<td>"+(rowData.printscp||"")+"</td>"+
						"<td>"+(rowData.printport||"")+"</td>"+
						"<td class='servicename'>Print</td>"+
						"<td>"+(rowData.printscu||"")+"</td>"+
					"</tr>"+
					"<tr>"+
					"<td class='servicename'>Worklist</td>"+
					"<td>"+(rowData.worklistscp||"")+"</td>"+
					"<td>"+(rowData.worklistport||"")+"</td>"+
					"<td class='servicename'>Worklist</td>"+
					"<td>"+(rowData.worklistscu||"")+"</td>"+
				"</tr>"+
				"</table>"
	}



	/*$('#dicdg').datagrid({
		view: detailview,
		detailFormatter: function(index,row){
			return '<div style="height:100px;border:groove"><table class="tableDetail"></table></div>';
		},
		onExpandRow: function(index,row){
			var ddv=$(this).datagrid('getRowDetail',index).find('table.tableDetail');
			ddv.datagrid({
				fit: true,
				border: false,
				url:'${ctx}/dic/findDetail?id='+row.id,
				singleSelect: true,
				columns:[
					[{title:'storage',colspan:3},
						{title:'storagecmt',colspan:3},
						{title:'qr',colspan:3},
						{title:'print',colspan:3}],
					
					[{field:'storagescu',title:'storagescu',width:100},
					{field:'storagescp',title:'storagescp',width:100},
					{field:'storagescpport',title:'storagescpport',width:100},
					
					{field:'storagecmtscu',title:'storagecmtscu',width:100},
					{field:'storagecmtscp',title:'storagecmtscp',width:100},
					{field:'storagecmtscpport',title:'storagecmtscpport',width:100},
					
					{field:'qrscu',title:'qrscu',width:100},
					{field:'qrscp',title:'qrscp',width:100},
					{field:'qrscpport',title:'qrscpport',width:100},
					
					{field:'printscu',title:'printscu',width:100},
					{field:'printscp',title:'printscp',width:100},
					{field:'printport',title:'printport',width:100}]
					]
			});
			
		}
	});*/
</script>
</body>
</html>