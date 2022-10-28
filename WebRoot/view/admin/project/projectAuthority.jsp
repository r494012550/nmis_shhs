<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div class="easyui-panel" data-options="fit:true" style="padding:10px;">
		<header style="color:#8aa4af;">您当前的位置：项目管理  > 权限管理</header>
		<table id="taskjurisdictionDg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_jurisdiction',
				url:'${ctx}/research/searchAuthority',autoRowHeight:true,fitColumns:true,scrollbarSize:0,
				loadMsg:'加载中...',emptyMsg:'没有查找到数据...'">
			<thead>
				<tr>
					<th data-options="field:'name',width:100">权限名称</th>
					<!-- <th data-options="field:'code',width:100">权限编码</th> -->
					<th data-options="field:'description',width:100">权限描述</th>
					<th data-options="field:'createtime',width:100">创建时间</th>
					<th data-options="field:'creation_name',width:100">创建人</th>
					<th data-options="field:'operate',width:60,align:'center',formatter:formatter_project_authority">${sessionScope.locale.get("admin.operation")}</th>
				</tr>
			</thead>
		</table>
		<div id="toolbar_div_jurisdiction" style="padding:2px 2px;text-align:right;">
			<a class="easyui-linkbutton easyui-tooltip" title="新建权限"  onClick="openProjectAuthorityDlg(null);">新建</a>
			<input class="easyui-searchbox" data-options="prompt:'请输入权限名称',searcher:findTaskjurisdiction" style="width:300px;">	
		</div>
	</div>
		<script type="text/javascript">
		function openProjectAuthorityDlg(row){
			$('#common_dialog').dialog({
				title : '',
				width : 400,height : 460,
				resizable: false,minimizable: false,maximizable: false,modal : true,
				border: 'thin',
				href : window.localStorage.ctx+'/research/openEditAuthorityDlg?id='+(row!=null?row.id:""),
				buttons:[{
					text:'保存',
					width:80,
					handler:function(){saveTaskJurisdiction();}
				},{
					text:'关闭',
					width:80,
					handler:function(){$('#common_dialog').dialog('close');}
				}],
				onLoad:function(){
					loadResource();
				}
			});

		}
		function loadResource(){
			getJSON(window.localStorage.ctx+'/research/searchResource',{},
				function(data){
					var json=validationData(data);
					console.log(data);
					$("#task_resource_dg").treegrid("loadData",data);
				}
			);
		}
		function formatter_project_authority(value, row, index){
			return (row.edit_project_authority!=null?'<a id="openTaskBtn" class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="editProjectAuthority('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-edit"></i></span></span></a>&nbsp;&nbsp;':"")+
					(row.delete_project_authority!=null?'<a id="deleteTaskBtn" class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteProjectAuthority('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-delete"></i></span></span></a>':"");
		}
		function editProjectAuthority(index){
			$('#taskjurisdictionDg').datagrid('selectRow',index);
			var row=$('#taskjurisdictionDg').datagrid('getSelected');
			openProjectAuthorityDlg(row);
		}
		
		function findTaskjurisdiction(value){
			getJSON(window.localStorage.ctx+"/research/searchAuthority",{'jurisdictionName':value}, function(json){
		    	$("#taskjurisdictionDg").datagrid("loadData",json);
		    });
		}
		function deleteProjectAuthority(index){
			$('#taskjurisdictionDg').datagrid('selectRow',index);
			var row=$('#taskjurisdictionDg').datagrid('getSelected');
			$.messager.confirm({
				title: '确认删除',
				border:'thin',
				msg: '确认删除选中的权限吗？',
				fn: function(r){
					if (r){
						getJSON(window.localStorage.ctx+"/research/deleteProjectAuthority",{id:row.id},function(data){
						 	var json=validationData(data);
					    	if(json.code==0){
					    		$.messager.show({
					                title:'提示',
					                msg:"删除成功！",
					                timeout:3000,
					                border:'thin',
					                showType:'slide'
					            });
					    		findTaskjurisdiction("");
					    	}
					    	else{
					    		$.messager.show({
					                title:'错误',
					                msg:"添加失败，请重试，如果问题依然存在，请联系系统管理员！",
					                timeout:3000,
					                border:'thin',
					                showType:'slide'
					            });
					    	}
					 	});
					}
				}
			});
		}
    </script>
</body>
</html>