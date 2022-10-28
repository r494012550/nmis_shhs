<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<c:set var="input_width" value="350"/>
<c:set var="label_width" value="80"/>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div style="width:100%;height:100%">
		<div class="easyui-panel" id="task_panel" title='权限编辑' border='false' halign='top'  style="height:100%;width:100%;margin: 0 auto;">
				<form id="jurisdictionform" method="POST">
					<div class="reg_content_div" style="float:left;padding-right:3px;padding-top:3px;padding-left:24px;">
						<input id="jurisdiction_name" name="name" data-options="prompt:'请输入权限名称...',required:true,missingMessage:'必填'"  value='${tj.name}' class="easyui-textbox" label="权限名称:" labelWidth="${label_width}" labelAlign="left" style="width:${input_width}px;">
					</div>
					<%-- <div class="reg_content_div" style="float:left;padding-right:3px;padding-top:3px;padding-left:24px;">
						<input id="jurisdiction_code" name="code" data-options="prompt:'请输入权限编码...',required:true,missingMessage:'必填'" value='${tj.code}' class="easyui-textbox" label="权限编码:" labelWidth="${label_width}" labelAlign="left" style="height:25px;width:${input_width}px;">
					</div> --%>
					<div class="reg_content_div" style="float:left;padding-right:3px;padding-top:5px;padding-left:24px;">
						<input id="jurisdiction_describe" name="description" value='${tj.description}' class="easyui-textbox" label="权限描述:" labelWidth="${label_width}" labelAlign="left" style="width:${input_width}px;">
					</div>
					<input id="id" name="id" value='${tj.id}' type="hidden"/>
					<input id="auids" name="auids" type="hidden" value="${auids}">
				</form>
				
				<div style="float:left;padding-right:3px;padding-top:5px;padding-left:24px;height:300px;width:350px;">
	               <table id="task_resource_dg" class="easyui-treegrid" style="width:320px;"
						data-options="singleSelect:false,selectOnCheck:true,onLoadSuccess:initresedg,idField:'rid',treeField:'display_${language}',
						checkOnSelect:true,border:true,fit:true,loadMsg:'${sessionScope.locale.get('loading')}...',emptyMsg:'${sessionScope.locale.get('nosearchresults')}...'">
					<thead>
						<tr>
							<th data-options="field:'ck',checkbox:true"></th>
							<th data-options="field:'display_${language}',width:150">${sessionScope.locale.get('admin.resourcename')}</th>
							<th data-options="field:'resource',width:170">${sessionScope.locale.get('admin.resource')}</th>
						</tr>
					</thead>
					</table>
          	 </div>
		</div>
   	</div>
   	<script type="text/javascript">
	   	function saveTaskJurisdiction(){
		   	 var rows=$("#task_resource_dg").datagrid("getChecked");
			     rows=JSON.stringify(rows);
	   		$('#jurisdictionform').form('submit', {
	   			url: window.localStorage.ctx+"/research/saveResearchAuthority",
	   			onSubmit: function(param){
	   				param.rows=rows
	   				var isValid=$(this).form("validate");
	 				if(!isValid){
	 					$.messager.alert("提醒","请输入完整信息！！");
	 					return isValid;
	 				}
	   			},
	   			success: function(data){
	   				 var data = eval('(' + data + ')');
	   				 if (data.code==0){
	   					 $.messager.show({
	   			                title:'提示',
	   			                msg:"保存成功！",
	   			                timeout:3000,
	   			                border:'thin',
	   			                showType:'slide'
	   			            });
	   					findTaskjurisdiction("");
	   					 $('#common_dialog').dialog('close'); 
	   			     }else{
	   			    	 $.messager.show({
	   			                title:'错误',	
	   			                msg:"保存失败，"+data.message,
	   			                timeout:3000,
	   			                border:'thin',
	   			                showType:'slide'
	   			            });
	   			    	 
	   			     }
	   				$("#jurisdiction_code").textbox("setValue","");
	   			}
	   		});
		}
	   	function jurisdiction_role(data){
 			for(var i=0;i<data.rows.length;i++){
 				if(data.rows[i].ck=="1"){
 					$("#task_resource_dg").datagrid("checkRow",i);
 				}
 			}
 		}
	   	function initresedg(row,data){
	   		var auids=$("#auids").val()+"";
	   		if(auids){
	   			var array = auids.split(",");
	   			
	   			for(var index=0; index<array.length; index++){
	   				var i = array[index];
	   				$('#task_resource_dg').datagrid('checkRow',parseInt(i));
	   			}
	   		}
	   		
	   	}
    </script>
</body>
</html>