<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div style="padding:5px;margin-left:auto;margin-right:auto;width:400px;">
		 <div class="easyui-panel" data-options="border:false" style="padding:5px;margin-bottom:5px;">
		 	<div style="margin-bottom:10px">
		        <input class="easyui-textbox" label="名称：" id="role_name" labelWidth="60"
		        	data-options="prompt:'请输入角色名称...',required:true,missingMessage:'必填'" name="role_name" style="width:300px;" value="${tr.rolename}">
		    </div>
		    <div style="margin-bottom:10px">
		        <input class="easyui-textbox" label="描述：" id="describe" labelWidth="60" name="describe" style="width:300px;" value="${tr.description}">
		    </div>
			 <div style="margin-bottom:0px">
				 <input class="easyui-combobox" label="用户等级：" id="user_level" labelWidth="60" style="width:300px;" name="user_level" value="${tr.user_level}"
						data-options="url:'${ctx}/research/findUserLevel',valueField:'level',textField:'name', editable:false,panelHeight:'auto'"
				 />
			 </div>

		    <input id="taskRoleId" name="taskRoleId"  type="hidden" value='${tr.id}'/>
		 </div>
		 <div class="easyui-panel" data-options="border:false" title="请选择角色所有权限"  style="height:300px;width: 100%,padding:10px;margin-bottom:5px;">
      	  	<table id="task_jurisdiction_dg" class="easyui-datagrid"
						data-options="url:'${ctx}/research/serachRoleAuthority?roleId=${tr.id}',onLoadSuccess:jurisdiction_role,
							singleSelect:false,border:true,fit:true,scrollbarSize:0,
							loadMsg:'${sessionScope.locale.get('loading')}...',
							emptyMsg:'${sessionScope.locale.get('nosearchresults')}...'">
						<thead>
							<tr>
								<th data-options="field:'ck',checkbox:true"></th>
								<th data-options="field:'code'">权限编码</th>
								<th data-options="field:'name'">权限名称</th>
								<th data-options="field:'description',width:200">权限描述</th>
							</tr>
						</thead>
			</table>
         </div>
   	</div>
   	
 	<script type="text/javascript">
 		function jurisdiction_role(data){
 			for(var i=0;i<data.rows.length;i++){
 				if(data.rows[i].ck=="1"){
 					$("#task_jurisdiction_dg").datagrid("checkRow",i);
 				}
 			}
 		}
 	</script>
</body>
</html>