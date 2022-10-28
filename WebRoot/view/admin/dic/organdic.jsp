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
	<header style="color:#8aa4af;">您当前的位置：字典管理 > 部位字典</header>

	<table id="organdictg"  class="easyui-treegrid" style="width:100%;"
			data-options="showFooter: true,rownumbers: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_organdic',			
			idField:'id',  treeField:'treenameZh',
				autoRowHeight:true,fitColumns:true,
				onContextMenu: function(e,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',row.id);
		            $('#cmenu_organdic').menu('show', {
		                left:e.pageX,
		                top:e.pageY
		            });
		        },
		        onLoadSuccess:function(row, data){
		        	var root=$(this).treegrid('getRoot');
		        	if(root){
		        		$(this).treegrid('expandAll',root.id);
		        	}
		        }"
				>
		        
		<thead>
				<tr>
				<!-- 	<th data-options="field:'id',width:100">id</th> -->
					<th data-options="field:'treenameZh',width:100">部位名称（中）</th>
					<th data-options="field:'treenameEn',width:100">部位名称（英）</th>
					<th data-options="field:'typecode',width:80">代码</th>
					<th data-options="field:'operate',width:60,align:'center',formatter:operatecolumn_organdic">操作</th>
				</tr>
			</thead>
		        
	</table>
	
		 <div id="toolbar_div_organdic" style="padding:2px 5px;text-align:right;">
		 		<shiro:hasPermission name="edit_organdic">
				<a class="easyui-linkbutton easyui-tooltip" title="新建同级节点" onClick="new_organdiclg();">新建部位</a>
		      
				<a class="easyui-linkbutton easyui-tooltip" title="新建子节点"  onClick="new_organdiclg1();">新建子部位</a>
		        </shiro:hasPermission>
				
				<input class="easyui-combobox easyui-tooltip" id="modality_organ" label="类型:" labelWidth="60"
	        	data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0004',editable:false,
	        	onLoadSuccess:function(data){if(data){$(this).combobox('select', data[0].code)}},
	        	onChange:selectType" 
	        	name="type" style="width:250px;height:30px;"> 
		 		
		</div>
	 	<div id="cmenu_organdic" class="easyui-menu"  style="padding:2px 2px;">
	 		<shiro:hasPermission name="edit_organdic">	        
		        <div onclick="ModifyOrgandicdlg();">修改</div>		   
		        <div onclick="deleteOrgandic();">删除</div>
		  	</shiro:hasPermission>
		 </div>
	

<script type="text/javascript">

</script>
</div>	
</body>
</html>