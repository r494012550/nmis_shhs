<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	
	<form name="dicform" id="dicform" method="POST">
	<div style="padding:10px;margin-left:auto;margin-right:auto;width:800px;">
		<div style="margin-bottom:3px;padding:5px;background-color:#ffffff;">
			<div>
				<input class="easyui-combobox" id="role" label="设备角色：" labelWidth="80" style="width:250px;height:30px;"
		        	data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0017',
		        	editable:false,panelHeight:'auto',prompt:'请选择设备角色...',required:true,missingMessage:'必填',
		        	onLoadSuccess:function(){$(this).combobox('select', '${empty dicmodality.role?'modality':dicmodality.role}')},onChange:function(newValue,oldValue){showDicomSetting(newValue,oldValue);}" 
		        	name="role">
		        <input class="easyui-textbox"  id="manufacturer" label="制造商：" labelWidth="80" style="width:250px;height:30px;"
		        	data-options="prompt:'请输入制造商...'"
		        	name="manufacturer" value="${dicmodality.manufacturer}">           
				<input class="easyui-textbox"  id="model" label="型号：" labelWidth="80" style="width:250px;height:30px;"
		        	data-options="prompt:'请输入型号...'"
		        	name="model" value="${dicmodality.model}">
			</div>
		</div>
		<div style="margin-bottom:3px;padding:5px;background-color:#ffffff;">
			<div style="margin-bottom:3px">
				
		        <input class="easyui-combobox modality_attr" id="type" label="类型：" labelWidth="80" style="width:250px;height:30px;"
		        	data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0004',
		        	editable:false,panelHeight:'auto',prompt:'请选择类型...',panelHeight:'120px',required:true,missingMessage:'必填',
		        	onLoadSuccess:function(){$(this).combobox('select', '${dicmodality.type}')}" 
		        	name="type" >
		        <input class="easyui-textbox"  id="modality_name" label="设备名：" labelWidth="80" style="width:250px;height:30px;" 
		        	data-options="prompt:'请输入设备名...',required:true,missingMessage:'必填'"
		        	name="modality_name" value="${dicmodality.modality_name}">
		        <input class="easyui-textbox" id="hostname" label="主机名/IP：" labelWidth="80" style="width:200px;height:30px;"
		        	data-options="prompt:'请输入主机名/IP...',validType:'checkIP',required:true,missingMessage:'必填'"
		        	name="hostname" value="${dicmodality.hostname}">
		        	<a class="easyui-linkbutton" title="ping" onClick="pingHostname();">ping</a>
			</div>
			<div style="margin-bottom:3px">
		        <input class="easyui-textbox modality_attr"  id="location" label="机房：" labelWidth="80" style="width:250px;height:30px;"
		        	data-options="prompt:'请输入机房...'"
		        	name="location" value="${dicmodality.location}">
		        <input class="easyui-combobox" id="institution" label="所在机构：" labelWidth="80" style="width:250px;height:30px;"
	                data-options="valueField:'id',textField:'name',url:'${ctx}/dic/getInstitution?deleted=0',
	                editable:false,prompt:'请选择机构...',required:true,missingMessage:'必填',panelHeight:'120px',
	                onChange:function(newValue,oldValue){setDepartment_dic(newValue,${empty dicmodality.departmentid?-1:dicmodality.departmentid})},
	                onLoadSuccess:function(){$(this).combobox('select', '${dicmodality.institutionid}')}"
	                name="institutionid"> 	
	            <input class="easyui-combobox" id="departmentid"  label="所在科室：" labelWidth="80" style="width:250px;height:30px;"
		            data-options="valueField:'id',textField:'deptname',
		            editable:false,prompt:'请选择科室...',panelHeight:'120px',required:true,missingMessage:'必填'"
		            name="departmentid"> 
		    </div>
		    <div style="margin-bottom:3px">
		        <input class="easyui-combobox modality_attr" id="gpname" label="分组：" labelWidth="80"  style="width:250px;height:30px;"
		        	data-options="valueField:'id',textField:'gpname',url: '${ctx}/dic/findGroup?deleted=0',
					editable:false,prompt:'请选择组...',panelHeight:'120px'"
					name="groupid" value="${dicmodality.groupid}">
				<input class="easyui-combobox modality_attr" id="character" label="字符编码：" labelWidth="80" style="width:250px;height:30px;"
		        	data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0026',
		        	editable:false,panelHeight:'auto',prompt:'请选择字符编码...',
		        	onLoadSuccess:function(){$(this).combobox('select', '${dicmodality.character}')}" 
		        	name="character" >
				<input class="easyui-textbox"  id="description" label="描述：" labelWidth="80" style="width:250px;height:30px;"
		        	data-options="prompt:'请输入描述...'"
		        	name="description"  value="${dicmodality.description}">
		   </div>
		</div>
		<div style="margin-bottom:3px;padding:5px;background-color:#ffffff;">
		   	<div style="margin-bottom:3px"> 
				状态设置：
				<input id="swb" class="easyui-switchbutton"  data-options="onText:'工作中',offText:'已停用' ,onChange:setstation,checked:${swb}" style="width:80px;height:30px;">
				<input class="easyui-numberspinner modality_attr"  id="averagetime" label="扫描一个患者的平均时间：" labelWidth="180" labelAlign="right" style="width:280px;height:30px;"
		        	data-options="required:true,prompt:'平均时间...',min:'1',max:'100',increment:1,suffix:'分钟'"
		        	name="averagetime" value="${dicmodality.averagetime}"> 
		        <input class="easyui-numberbox" id="report_alert_hour" label="报警时间(时)：" labelAlign="right" labelWidth="100" style="width:150px;height:30px;"
		  			data-options="min:1,max:24,prompt:'小时...'"  
			        name="report_alert_hour" value="${dicmodality.report_alert_hour}">
			    <input class="easyui-numberbox"  id="report_alert_minute" label="报警时间(分)：" labelAlign="right" labelWidth="100" style="width:150px;height:30px;"
			        data-options="min:1,max:59,prompt:'分钟...'"
			        name="report_alert_minute" value="${dicmodality.report_alert_minute}">
			</div>
		</div>
		<div style="margin-bottom:3px;padding:5px;background-color:#ffffff;">
		   	<div style="margin-bottom:3px"> 
				<input class="easyui-numberspinner modality_attr"  id="appointment_time" label="单位预约时长：" labelWidth="120" labelAlign="left" style="width:224px;height:30px;"
		        	data-options="required:true,prompt:'平均时间...',min:1,max:360,increment:1,suffix:'分钟'"
		        	name="appointment_time" value="${dicmodality.appointment_time}">
		        <input class="easyui-numberbox modality_attr"  id="appointment_number" label="时长人数：" labelWidth="110" labelAlign="right" style="width:210px;height:30px;"
		        	data-options="required:true,prompt:'时长人数...',min:1"
		        	name="appointment_number" value="${dicmodality.appointment_number}"> 
		        <input class="easyui-numberbox" id="advance_hour" label="到检时间(时)：" labelAlign="right" labelWidth="100" style="width:150px;height:30px;"
		  			data-options="min:1,max:24,prompt:'小时...'"  
			        name="advance_hour" value="${dicmodality.advance_hour}">
			    <input class="easyui-numberbox"  id="advance_minute" label="到检时间(分)：" labelAlign="right" labelWidth="100" style="width:150px;height:30px;"
			        data-options="min:1,max:59,prompt:'分钟...'"
			        name="advance_minute" value="${dicmodality.advance_minute}">
			</div>
		</div>
		<div style="margin-bottom:3px;padding:5px;background-color:#ffffff;">
			<div style="margin-bottom:3px;height:30px;font-weight:bold;">
				设备工作时间：
			</div>
			<div style="margin-bottom:5px">
		         <input class="easyui-timespinner modality_attr"  id="workday_of_worktime1" label="常规工时AM：" labelWidth="90"data-options="min:'07:00',max:'12:00',prompt:'开始时间...',required:true," style="width:200px;height:30px;"value="${workday_of_worktime1}">
		        -&nbsp;<input class="easyui-timespinner modality_attr"  id="workday_of_worktime2" data-options="min:'07:00',max:'12:00',prompt:'截止时间...',required:true"  style="width:108px;height:30px;"value="${workday_of_worktime2}"> 
	        	 &nbsp;<input class="easyui-timespinner modality_attr"  id="workday_of_worktime3" label="常规工时PM：" labelWidth="90"data-options="min:'12:00',prompt:'开始时间...',required:true,"  style="width:200px;height:30px;"value="${workday_of_worktime3}">
		        -&nbsp;<input class="easyui-timespinner modality_attr"  id="workday_of_worktime4" data-options="min:'12:00',prompt:'截止时间...',required:true, "   style="width:108px;height:30px;" value="${workday_of_worktime4}" >     
			</div>
			<div style="margin-bottom:5px">
		         <input class="easyui-timespinner modality_attr"  id="saturday_of_worktime1" label="周六工时AM：" labelWidth="90" data-options="min:'07:00',max:'12:00',prompt:'开始时间...'"  style="width:200px;height:30px;" value="${saturday_of_worktime1}">
		        -&nbsp;<input class="easyui-timespinner modality_attr"  id="saturday_of_worktime2" data-options="min:'07:00',max:'12:00',prompt:'截止时间...'"  style="width:108px;height:30px;" value="${saturday_of_worktime2}"> 
	        	 &nbsp;<input class="easyui-timespinner modality_attr"  id="saturday_of_worktime3" label="周六工时PM：" labelWidth="90" data-options="min:'12:00',prompt:'开始时间...'" style="width:200px;height:30px;"value="${saturday_of_worktime3}" >
		        -&nbsp;<input class="easyui-timespinner modality_attr"  id="saturday_of_worktime4"data-options="min:'12:00',prompt:'截止时间...'" style="width:108px;height:30px;"value="${saturday_of_worktime4}">     
			</div>
			<div style="margin-bottom:3px">
		         <input class="easyui-timespinner modality_attr"  id="sunday_of_worktime1" label="周日工时AM：" labelWidth="90" data-options="min:'07:00',max:'12:00',prompt:'开始时间...'"  style="width:200px;height:30px;"value="${sunday_of_worktime1}" >
		        -&nbsp;<input class="easyui-timespinner modality_attr"  id="sunday_of_worktime2" data-options="min:'07:00',max:'12:00',prompt:'截止时间...'"  style="width:108px;height:30px;" value="${sunday_of_worktime2}"> 
	        	 &nbsp;<input class="easyui-timespinner modality_attr"  id="sunday_of_worktime3" label="周日工时PM：" labelWidth="90"data-options="min:'12:00',prompt:'开始时间...'"  style="width:200px;height:30px;"value="${sunday_of_worktime3}" >
		        -&nbsp;<input class="easyui-timespinner modality_attr"  id="sunday_of_worktime4" data-options="min:'12:00',prompt:'截止时间...'"  style="width:108px;height:30px;" value="${sunday_of_worktime4}">     
			</div>
			<input  id="workday_of_worktime" name="workday_of_worktime" type="hidden" >
			<input  id="saturday_of_worktime" name="saturday_of_worktime" type="hidden">
			<input  id="sunday_of_worktime" name="sunday_of_worktime"  type="hidden" >
		</div>
		<div style="padding:5px;background-color:#ffffff;">
			<div style="margin-bottom:3px;height:30px;font-weight:bold;">
				DICOM服务设置:
			</div>
			<div style="margin-bottom:3px" id="storage_div">
				<div class="easyui-panel" title="Storage" data-options="halign:'left'" style="width:750px;height:75px;padding:5px 20px;">
					<div style="margin-bottom:3px">
						<input class="easyui-textbox storage" id="storagescu" label="SCU:" labelWidth="40"
			        		data-options="prompt:'请输入Storage AET...'"
			        		name="storagescu" style="width:300px;" value="${dicmodality.storagescu}"> 
					</div>
					<div>
						<input class="easyui-textbox storage" id="storagescp" label="SCP:" labelWidth="40"
			        		data-options="prompt:'请输入Storage AET...'"
			        		name="storagescp" style="width:300px;height:30px;" value="${dicmodality.storagescp}"> 
			        	<input class="easyui-numberbox storage" id="storagescpport" label="Port:" labelWidth="40"
			    			data-options="prompt:'请输入端口号...',min:0,max:65536,validType:'integer'"
			    			name="storagescpport" style="width:300px;" value="${dicmodality.storagescpport}"/>
					</div>
		    	</div>
	    	</div>
	    	<div style="margin-bottom:3px">
	    		<div class="easyui-panel" title="StgCmt" data-options="halign:'left'" style="width:750px;height:75px;padding:5px 20px;">
					<div style="margin-bottom:3px">
			        	<input class="easyui-textbox stgcmt"  id="storagecmtscu" label="SCU:" labelWidth="40"
			        		data-options="prompt:'请输入Storage Commitment AET...'"
			        		name="storagecmtscu" style="width:300px;height:30px;" value="${dicmodality.storagecmtscu}">
					</div>
					<div>
			    		<input class="easyui-textbox stgcmt" id="storagecmtscp" label="SCP:" labelWidth="40"
			        		data-options="prompt:'请输入Storage Commitment AET...'"
			        		name="storagecmtscp" style="width:300px;height:30px;" value="${dicmodality.storagecmtscp}"> 
			        	<input class="easyui-numberbox stgcmt" id="storagecmtscpport" label="Port:" labelWidth="40"
			    			data-options="prompt:'请输入端口号...',validType:'integer'"
			    			name="storagecmtscpport" style="width:300px;height:30px;" value="${dicmodality.storagecmtscpport}"/>
					</div>
		    	</div>
	    	</div>
	    	
	    	<div style="margin-bottom:3px">
				<div class="easyui-panel" title="Q/R" data-options="halign:'left'" style="width:750px;height:75px;padding:5px 20px;">
					<div style="margin-bottom:3px">
			        	<input class="easyui-textbox queryretrieve" id="qrscu" label="SCU:" labelWidth="40"
			        		data-options="prompt:'请输入Query/Retrieve AET...'" 
			        		name="qrscu" style="width:300px;height:30px;" value="${dicmodality.qrscu}"> 
					</div>
					<div>
			    		<input class="easyui-textbox queryretrieve" id="qrscp" label="SCP:" labelWidth="40"
			        		data-options="prompt:'请输入Query/Retrieve AET...'" 
			        		name="qrscp" style="width:300px;height:30px;" value="${dicmodality.qrscp}"> 
			        	<input class="easyui-numberbox queryretrieve" id="qrscpport" label="Port:" labelWidth="40"
			    			data-options="prompt:'请输入端口号...',validType:'integer'" 
			    			name="qrscpport" style="width:300px;height:30px;" value="${dicmodality.qrscpport}"/>
					</div>
		    	</div>
			</div>
			<div style="margin-bottom:3px">
				<div class="easyui-panel" title="Print" data-options="halign:'left'" style="width:750px;height:75px;padding:5px 20px;">
					<div style="margin-bottom:3px">
			        	<input class="easyui-textbox print" id="printscu" label="SCU:" labelWidth="40"
			        		data-options="prompt:'请输入AET...'" 
			        		name="printscu" style="width:300px;height:30px;" value="${dicmodality.printscu}"> 
					</div>
					<div>
						<input class="easyui-textbox print" id="printscp" label="SCP:" labelWidth="40"
			        		data-options="prompt:'请输入AET...'" 
			        		name="printscp" style="width:300px;height:30px;" value="${dicmodality.printscp}"> 
			        	<input class="easyui-numberbox print" id="printport" label="Port:" labelWidth="40"
			    			data-options="prompt:'请输入端口号...',validType:'integer'" 
			    			name="printport" style="width:300px;height:30px;" value="${dicmodality.printport}"/>
					</div>
		    	</div>
				
	    	</div>
	    	
	    	<div>
				<div class="easyui-panel" title="worklist" data-options="halign:'left'" style="width:750px;height:75px;padding:5px 20px;">
					<div style="margin-bottom:3px">
			        	<input class="easyui-textbox worklist" id="orklistscu" label="SCU:" labelWidth="40"
			        		data-options="prompt:'请输入AET...'" 
			        		name="worklistscu" style="width:300px;height:30px;" value="${dicmodality.worklistscu}"> 
					</div>
					<div>
			    		<input class="easyui-textbox worklist" id="worklistscp" label="SCP:" labelWidth="40"
			        		data-options="prompt:'请输入AET...'" 
			        		name="worklistscp" style="width:300px;height:30px;" value="${dicmodality.worklistscp}"> 
			        	<input class="easyui-numberbox worklist" id="worklistport" label="Port:" labelWidth="40"
			    			data-options="prompt:'请输入端口号...',validType:'integer'" 
			    			name="worklistport" style="width:300px;height:30px;" value="${dicmodality.worklistport}"/>
					</div>
		    	</div>
	    	</div>
    	</div>
	</div>
	<div><input id="id" name="id" type="hidden" value="${id}" />
	<input id="working_state" name="working_state" type="hidden" value="${dicmodality.working_state}" /></div>
	</form>
   	<script>
   $.extend($.fn.validatebox.defaults.rules,{
	   checkIP:{
		   validator:function(value){
			   var reg=/^((1?\d?\d|(2([0-4]\d|5[0-5])))\.){3}(1?\d?\d|(2([0-4]\d|5[0-5])))$/;
			   return reg.test(value);
		   },
		   message:'IP地址格式不正确'
	   },
	   integer:{
		   validator:function(value){
			   var reg=/^[1-9]+\d*$/;
			   return reg.test(value);
		   },
		   message:'请输入正确的端口号'
	   }
   });
   	</script>
</body>
</html>