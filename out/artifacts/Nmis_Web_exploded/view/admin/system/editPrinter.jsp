<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>	
<body>
<form name="printerform" id="printerform" method="POST">
     
     <div style="padding:10px 10px 10px 10px;margin-left:auto;margin-right:auto;width:280px;">
		<div style="margin-bottom:10px">
			<input class="easyui-textbox"   id="printername" label="打印机名称:" labelWidth="100"
	        	data-options="prompt:'请输入打印机名称...',required:true,missingMessage:'必填',"
				 name="printername" style="width:270px;height:30px;" value="${printer.printername}">
	    </div>
	    <div style="margin-bottom:10px">
            <input class="easyui-textbox"   id="printername" label="打印机描述:" labelWidth="100"
                data-options="prompt:'请输入打印机描述...',"
                 name="printerdescription" style="width:270px;height:30px;" value="${printer.printerdescription}">
        </div>
        <div style="margin-bottom:10px">
            <input class="easyui-textbox"   id="printername" label="模板名称:" labelWidth="100"
                data-options="prompt:'请输入模板名称...',required:true,missingMessage:'必填',"
                 name="templatename" style="width:270px;height:30px;" value="${printer.templatename}">
        </div>
        <div style="margin-bottom:10px">
            <input class="easyui-textbox"   id="printername" label="模板描述:" labelWidth="100"
                data-options="prompt:'请输入模板描述...',"
                 name="templatedescription" style="width:270px;height:30px;" value="${printer.templatedescription}">
        </div>

		<div><input id="id" name="id" type="hidden" value="${printer.id}"/></div>
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