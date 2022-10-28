<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
   <form name="groupform" id="groupform" method="POST">
     
     <div style="padding:10px 10px 10px 10px;margin-left:auto;margin-right:auto;width:400px;">
		<div style="margin-bottom:10px">
			<input class="easyui-textbox" id="gpname" label="组名称:" labelWidth="100"
	        	data-options="prompt:'请输入组名称...',required:true,missingMessage:'必填',onChange:checkGroupName"
				 name="gpname" style="width:400px;height:30px;" value="${gpname}">
	    </div>
		
		<div style="margin-bottom:10px" > 
	        <input class="easyui-combobox" id="type" label="类型:" labelWidth="100"
	        	data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0004',editable:false,prompt:'请选择类型...',required:true,missingMessage:'必填',
	        	onLoadSuccess:function(){$(this).combobox('select', '${type}')}" 
	        	name="type" style="width:400px;height:30px;">     
		</div>
		
		<div  style="margin-bottom:10px">
	        <select  class="easyui-combobox"   id="description" label="具体描述：" labelWidth="100"  required="true"  editable="false"  name="description" style="width:400px;height:30px;" value="${description}">
				<option value="常规">常规</option>
				<option value="急诊">急诊</option>
				<option value="高端">高端</option>
				<option value="研究">研究</option>
			</select> 
		</div>
		
		<div style="margin-bottom:10px" > 
	        <input class="easyui-numberbox"  id="default_duration" label="持续时间(分):" labelWidth="100"
	        	data-options="prompt:'请输入时间...'" name="default_duration" style="width:400px;height:30px;" value="${default_duration}">           		     
		</div>
		
		<div style="margin-bottom:10px" > 
			<input class="easyui-combogrid " id="modalityids" name="modalityids" label="设备：" labelWidth="100"  editable="false" style="width:400px;height:30px;"
	        	data-options="prompt:'请选择需要添加的设备...', 
				multiple: true,
				idField: 'id',
				textField: 'modality_name',
				valueField:'id',
				url: '${ctx}/dic/findDic?deleted=0',
				method: 'get',
				columns: [[
					{field:'ck',checkbox:true},
					{field:'modality_name',title:'设备名',width:50,align:'center'},
					{field:'type',title:'类型',width:30,align:'center'},
					{field:'model',title:'型号',width:30,align:'center'}
					<!-- ,{field:'role',title:'设备角色',width:35,align:'center'} -->
				]],
			fitColumns: true"  value="${modalityids}"> 
	     </div>
	   
		<div><input id="groupid" name="id" type="hidden" value="${id}"/></div>
	</div>
<script type="text/javascript">

/* function checkname(){
	//alert(123);
	 $('#groupform').form('gpname',{
		 url: '${ctx}/dic/checkname',
		oncheck: function(){
			// do some check
			// return false to prevent submit;
	    },
	    success:function(data){
	    	window.location.href=data;
	    }
	});
} */
	
</script>
</form>
</body>
</html>