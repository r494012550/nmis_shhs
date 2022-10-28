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
	<header style="color:#8aa4af;">您当前的位置：系统管理   > 客户端</header>
		<table id="clientdg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_Client',
				url:'${ctx}/system/getClient',
				loadMsg:'加载中...',emptyMsg:'没有查找到数据...',
				autoRowHeight:true,fitColumns:true,
				onRowContextMenu: function(e,index ,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',index);
		            $('#cmenu_Client').menu('show', {
		                left:e.pageX,
		                top:e.pageY
		            });
		        }">
			<thead>
					<tr>
						<th data-options="field:'hostname',width:40">客户端名称</th>
						<th data-options="field:'hostip',width:40">IP地址</th>
						<th data-options="field:'zone',width:40">区域</th>						
						<th data-options="field:'type',width:40">功能类型</th>
						<th data-options="field:'modalityname',width:40">检查设备</th>
						<th data-options="field:'operate',width:55,align:'center',formatter:operatecolumn_Client">操作</th>
					</tr>
			</thead>
        </table>
        <div id="cmenu_Client" class="easyui-menu"  style="padding:2px 2px;">	
        	<shiro:hasPermission name="edit_client">		
		        <div onclick="new_Client();">新建</div>
		        <div onclick="modifyClient();">修改</div>	   
		        <div onclick="deleteClient();">删除</div>	
		     </shiro:hasPermission>	  
		 </div>
		 <div id="toolbar_div_Client" style="padding:2px 5px;text-align:right;">					
			<shiro:hasPermission name="edit_client">
				<a class="easyui-linkbutton easyui-tooltip" title="新建客户端" onClick="new_Client();">新建</a>
		    </shiro:hasPermission>
		    <input class="easyui-combobox" id="modalityid_search" label="检查设备:" labelWidth="75px" style="width:200px;height:30px;"
	        	data-options="valueField:'id',textField:'modality_name',url:'${ctx}/dic/findDic?deleted=0',
	        	panelHeight:'auto',editable:false,onChange:doSearchClientByModality,
	        	icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]"> 
			<input class="easyui-searchbox" id="clientName_search" style="width:200px;"
				data-options="prompt:'请输入客户端名称' ,searcher:doSearchClient">
		</div>
	</div>
</body>
</html>
