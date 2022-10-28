<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
   <form name="departmentform" id="departmentform" method="POST">
     
     <div style="padding:10px 10px 10px 10px;margin-left:auto;margin-right:auto;width:400px;">
		<%-- <div style="margin-bottom:10px">
			<input class="easyui-textbox"   id="deptcode" label="科室代码:" labelWidth="100"
	        	data-options="prompt:'请输入科室代码...',required:true,missingMessage:'必填'"
				 name="deptcode" style="width:400px;height:30px;" value="${deptcode}">
	    </div>
	    <div  style="margin-bottom:10px">
            <input class="easyui-combobox" id="institution" label="机构：" labelWidth="100"   editable="false"  name="institutionid" 
                data-options="valueField:'id',textField:'name',url:'${ctx}/dic/getInstitution?deleted=0',editable:false,prompt:'机构...',
                required:true,missingMessage:'必填',onLoadSuccess:function(){$(this).combobox('select', '${institutionid}')}"
                style="width:400px;height:30px;"> 
        </div>
		<div style="margin-bottom:10px">
			<input class="easyui-textbox"   id="deptname" label="科室名称:" labelWidth="100"
	        	data-options="prompt:'请输入科室名称...',required:true,missingMessage:'必填',"
				 name="deptname" style="width:400px;height:30px;" value="${deptname}">
	    </div>
		<div style="margin-bottom:10px" > 
	        <input class="easyui-combobox" id="type" label="类型:" labelWidth="100"
	        	data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0002',
	        	editable:false,prompt:'请选择类型...',required:true,missingMessage:'必填',
	        	onLoadSuccess:function(){$(this).combobox('select', '${type}')}" 
	        	name="type" style="width:400px;height:30px;" value="${type}">     
		</div> --%>
		
		<table id="dept_shift_dg" class="easyui-datagrid" style="width:100%;height:180px;" title="请选择班次"
			data-options="singleSelect:false,
				url:'${ctx}/workforce/getDeptPostShift?deleted=0&deptid=${id}',autoRowHeight:true,border:true
				,onLoadSuccess:function(data){
					checkShift(data);
				}">
			<thead>
					<tr>
						<th data-options="field:'ck',checkbox:true"></th>
						<th data-options="field:'name',width:80">班次名称</th>						
						<th data-options="field:'worktimes',width:500">时间段</th>
					</tr>
			</thead>
        </table>
	   
		<input id="id" name="id" type="hidden" value="${id}"/>
		<input id="shiftids_dic" name="shiftids" type="hidden" value=""/>
		<input id="shifts_dic" name="shifts" type="hidden" value=""/>
	</div>

</form>
</body>
</html>