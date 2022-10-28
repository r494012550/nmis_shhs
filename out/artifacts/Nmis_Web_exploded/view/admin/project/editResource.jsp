<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<c:set var="input_width" value="300"/>
<c:set var="label_width" value="100"/>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div style="width:100%;height:100%">
		<div class="easyui-panel"  title='资源编辑' border='false' halign='top'  style="height:100%;width:100%;">	
				<form id="resourceform" method="POST">
					<div class="reg_content_div" style="float:left;padding-right:3px;padding-top:10px;">
						<input id="resource_name" name="resource_name" data-options="prompt:'请输入资源名称...',required:true,missingMessage:'必填'"  value='${rr.resource_name}' class="easyui-textbox" label="资源名称:" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;">
					</div>
					<div class="reg_content_div" style="float:left;padding-right:3px;padding-top:10px;">
						<input id="resource_value" name="resource_value" data-options="prompt:'请输入资源值...',required:true,missingMessage:'必填'" value='${rr.resource_value}' class="easyui-textbox" label="资源值:" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;">
					</div>
					<input id="id" name="id" value='${rr.id}' type="hidden"/>
				</form>
		</div>
   	</div>
   	<script type="text/javascript">
	   	function saveResource(){
	   		var resourceName=$("#resource_name").textbox("getValue");
	   		var resourceValue=$("#resource_value").textbox("getValue");
	   		var id=$("#id").val();
	   		$('#resourceform').form('submit', {
	   			url: window.localStorage.ctx+"/research/saveResource",
	   			onSubmit: function(param){
	   				param.resourceName=resourceName;
	   				param.resourceValue=resourceValue;
	   				param.id=id;
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
	   					findResource("");
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
	   			}
	   		});
		}
    </script>
</body>
</html>