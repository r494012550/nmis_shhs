<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div class="easyui-panel" data-options="fit:true" style="padding:10px;">
	<header style="color:#8aa4af;">您当前的位置：排班管理  > 科室排班管理</header>
		<div class="easyui-layout" data-options="fit:true">
	        <div data-options="region:'north'" style="height:45px;padding:5px;">
	        		<div style="float:left;width:78%;">
		        		<input class="easyui-combobox" id="institutionid_wf" label="机构：" labelWidth="50px" labelAlign="right" style="width:200px;height:30px;"
			                data-options="valueField: 'id',textField: 'name',url:'${ctx}/dic/getInstitution?deleted=0',
			                panelHeight:'300px',editable:false
			                ,onChange:function(newvalue,oldvalue){
			                	$('#dept_shift_dept_cb').combobox('setValue','');
			                	$('#dept_shift_dept_cb').combobox('reload','${ctx}/dic/findDepartment?institutionid='+newvalue+'&value=&deleted=0');
			                	$('#dept_shift_position_cb').combobox('setValue', '');
			                }
			                ,onLoadSuccess:function(){
			                	var data=$(this).combobox('getData');
			                	if(data&&data[0]){
			                		$(this).combobox('select',data[0].id);
			                	} else {
			                		clearPanel_wf();
			                	}
			                	
			                }">
			        
			        	<input class="easyui-combobox" id="dept_shift_dept_cb" label="科室：" labelWidth="50px" labelAlign="right" style="width:200px;height:30px;"
			                data-options="valueField: 'id',textField: 'deptname',
			                panelHeight:'300px',editable:false
			                ,onLoadSuccess:function(){
			                	var data=$(this).combobox('getData');
			                	if(data&&data[0]){
			                		$(this).combobox('select',data[0].id);
			                	} else {
			                		clearPanel_wf();
			                	}
			                }
			                ,onChange:function(newvalue,oldvalue){
			                	$('#dept_shift_position_cb').combobox('setValue', '');
			                }">
			        
			        	<input class="easyui-combobox" id="dept_shift_position_cb" label="岗位：" labelWidth="50px" labelAlign="right" style="width:150px;height:30px;"
			                data-options="valueField: 'code',textField: 'name_zh',url:'${ctx}/syscode/getCode?type=0019',
			                panelHeight:'auto',editable:false,onChange:showShiftDetails">
			        
			        	<a class="easyui-linkbutton" id="dept_shift_lastweek" data-options="toggle:true,group:'g1',plain:true" style="width:60px;" 
			        		onclick="showShiftDetails(null,null,'lastweek')">&lt;&nbsp;上周</a>
			        	<a class="easyui-linkbutton" id="dept_shift_nextweek" data-options="toggle:true,group:'g1',plain:true" style="width:60px;" 
			        		onclick="showShiftDetails(null,null,'nextweek')">下周&nbsp;&gt;</a>
				        <a class="easyui-linkbutton" id="dept_shift_thisweek" data-options="toggle:true,group:'g1',selected:true,plain:true" style="width:60px;" 
				        	onclick="showShiftDetails(null,null,'thisweek')" monday="${thismoday}" week_offset="0">本周</a>
				        
	        		</div>
	        		<div style="float:right;width:20%;text-align:right;">
	        			<a class="easyui-linkbutton" id="dept_shift_nextweek" data-options="" style="width:80px;" onclick="copyLastWeekShifts()">引用上周</a>
	        			<a class="easyui-linkbutton" id="dept_shift_submit" data-options="disabled:true" style="width:80px;" onclick="submitShifts()">立即生效</a>
	        		</div>
		        	
		        
		        	
		        <!-- <div style="margin-bottom:10px">
		        	<input class="easyui-combobox" id="worktimes_wf" label="班次：" labelWidth="50px" labelAlign="right" style="width:250px;height:30px;"
		                data-options="valueField: 'id',textField: 'name',
		                panelHeight:'auto',editable:false">
		        </div> -->
		    </div>
	        <!-- <div data-options="region:'west'" style="width:150px;"></div> -->
	        <div data-options="region:'center'" style="padding:0px">
	        	<div id="panel_wf" class="easyui-panel" data-options="fit:true,border:false" style="padding:0px;">
	        		<div style="width:95%;height:250px;padding:20px 20px;float:left;line-height: 250px;text-align: center;">
			    		<span style="font-weight: bold;">请选择机构、科室、岗位，进行排班设置。</span>
			    	</div>
	        	</div>
	        </div>
	        <!-- <div data-options="region:'south'" style="height:100px;padding:20px;">
	        	
	        </div> -->
	    </div>
		 
	</div>

</body>
</html>