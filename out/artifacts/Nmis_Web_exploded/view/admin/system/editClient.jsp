<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>	
<body>
   <form name="clientform" id="clientform" method="POST">
     
     <div style="padding:10px 10px 10px 10px;margin-left:auto;margin-right:auto;width:280px;">
		<div style="margin-bottom:10px">
			<input class="easyui-textbox"   id="hostname" label="客户端名称:" labelWidth="100"
	        	data-options="prompt:'请输入客户端名称...',required:true,missingMessage:'必填'"
				 name="hostname" style="width:270px;height:30px;" value="${hostname}">
	    </div>
	    <div style="margin-bottom:10px">
		<input class="easyui-textbox" id="hostip" label="IP:" labelWidth="100"
	        	data-options="prompt:'请输入IP...',validType:'checkIP',onChange:checkIP,required:true,missingMessage:'必填'" name="hostip" style="width:270px;height:30px;" value="${hostip}">
		</div>
		<div style="margin-bottom:10px">
			<input class="easyui-textbox"   id="zone" label="客户端区域:" labelWidth="100"
	        	data-options="prompt:'请输入客户端区域...'"
				 name="zone" style="width:270px;height:30px;" value="${zone}">
	    </div>
		<%-- <div style="margin-bottom:10px" > 
	        <input class="easyui-combobox" id="type" label="类型:" labelWidth="100"
	        	data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0020',prompt:'请选择类型...',editable:false,required:true,missingMessage:'必填',
	        	onLoadSuccess:function(){$(this).combobox('select', '${type}')},onSelect:modalityid" 
	        	name="type" style="width:270px;height:30px;">     
		</div> --%>
		
		<div style="margin-bottom:10px" > 
	        <input class="easyui-combobox" id="modalityid" label="检查设备:" labelWidth="100"
	        	data-options="valueField:'id',textField:'modality_name',url:'${ctx}/dic/findDic?deleted=0',disabled:false,editable:false,prompt:'请选择检查设备...',required:true,missingMessage:'必填',
	        	onLoadSuccess:function(){$(this).combobox('select', '${modalityid}')}" 
	        	name="modalityid" style="width:270px;height:30px;" value="${modalityid}">     
		</div>
		
		<div><input id="id" name="id" type="hidden" value="${id}"/></div>
		<div><input id="type" name="type" type="hidden" value="Exam"/></div>
	 </div>
<script>
   $.extend($.fn.validatebox.defaults.rules,{
	   checkIP:{
		   validator:function(value){
			   var reg=/^((1?\d?\d|(2([0-4]\d|5[0-5])))\.){3}(1?\d?\d|(2([0-4]\d|5[0-5])))$/;
			   return reg.test(value);
		   },
		   message:'IP地址格式不正确'
	   },
   });
   
</script>
</form>
</body>
</html>
