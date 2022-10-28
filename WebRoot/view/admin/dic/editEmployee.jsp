<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
   <form name="employeeform" id="employeeform" method="POST">
     
     <div style="padding:10px 10px 10px 10px;margin-left:auto;margin-right:auto;width:300px;">
     	<div style="margin-bottom:10px">
			<input class="easyui-textbox"   id="jobnumber" label="工号:" labelWidth="100"
	        	data-options="prompt:'请输入工号...',"
				name="jobnumber" style="width:270px;height:30px;" value="${jobnumber}">
	    </div>
		<div style="margin-bottom:10px">
			<input class="easyui-textbox"   id="name" label="姓名:" labelWidth="100"
	        	data-options="prompt:'请输入姓名...',required:true,missingMessage:'必填'"
				name="name" style="width:270px;height:30px;" value="${name}">
	    </div>
		
		<div  style="margin-bottom:10px">
            <input class="easyui-combobox" id="institution" label="机构：" labelWidth="100"  
                data-options="valueField:'id',textField:'name',url:'${ctx}/dic/getInstitution?deleted=0',
                editable:false,prompt:'机构...',required:true,missingMessage:'必填',
                onChange:function(newValue,oldValue){setDepartment(newValue,${empty deptfk?-1:deptfk})},
                onLoadSuccess:function(){$(this).combobox('select', '${institution}')}"
                name="institutionid" style="width:270px;height:30px;"> 
        </div>
		
		<div style="margin-bottom:10px">
			<input class="easyui-combobox" id="dept" name="deptfk" label="所在科室:" labelWidth="100" 
                data-options="valueField:'id',textField:'deptname',
                editable:false,prompt:'请选择科室...',required:true,missingMessage:'必填',
                onSelect:setDeptInfo"
                style="width:270px;height:30px;">    
		</div>
		
		<div  style="margin-bottom:10px">
	        <input class="easyui-combobox" id="profession" name="profession"  label="岗位：" labelWidth="100" required="true"  editable="false" 
	        	data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0019',editable:false,prompt:'请选择岗位...',required:true,missingMessage:'必填',
	        	onLoadSuccess:function(){$(this).combobox('select', '${profession}')}" 
	        	style="width:270px;height:30px;"> 
		</div>
		
		<div>
			<input id="id" name="id" type="hidden" value="${id}"/>	
			<input id="deptcode_edit" name="deptcode" type="hidden" value="${deptcode}"/>
			<input id="deptname_edit" name="deptname" type="hidden" value="${deptname}"/>
		</div>
	</div>
	
<script>

</script>

</form>
</body>
</html>