<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<c:set var="input_width" value="230"/>
<c:set var="label_width" value="100"/>
<c:set var="input_width_long" value="500"/>
<c:set var="label_width_long" value="120"/>
<title>Insert title here</title>
</head>
<body>	
     <div class="easyui-panel" style="padding:3px 3px 0px 3px;border:0px">
     	<input id="orderid" name="orderid" type="hidden">
     	<input id="studyid" name="studyid" type="hidden">
     	<input id="orderstatus" name="orderstatus" type="hidden">
     	<input id="patientid" name="patientid" type="hidden">
     	<input id="patient_id" name="patient_id" type="hidden"/>
     	<input id="admission_id" name="admission_id" type="hidden"/>
     	<input id="interrogation_id" name="interrogation_id" type="hidden"/>
     	<input id="previous_history_id" name="previous_history_id" type="hidden"/>
     	<input id="modality_type" name="modality_type" type="hidden"/>
    	
      	<div class="interrogation_content_div">
      		<input class="easyui-textbox"  label="检查编号：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
    			name="studyid"
    			readonly="readonly" />
    	</div>
    	<div class="interrogation_content_div">
      		<input class="easyui-combobox" label="患者来源：" readonly="readonly" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
				name="patientsource"
				data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0002',editable:false,panelHeight:'auto',
				onLoadSuccess:function(){$(this).combobox('select', 'O')}"  required="true" />
		</div>
    	<div class="interrogation_content_div">
			<input class="easyui-textbox" label="姓名：" readonly="readonly"  labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
    			name="patientname"
    			data-options="validType:'isSpace'"/>
    	</div>
    	<div class="interrogation_content_div">
    		<input class="easyui-combobox" label="性别：" readonly="readonly"  labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
    			name="sex" id="sexdisplay"
    			data-options="valueField:'code',panelHeight:'auto',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0001',editable:false,
    			onChange:changePatientSex,
    			onLoadSuccess:function(){$(this).combobox('select', 'M')}"/>
    	</div>
    	<div class="interrogation_content_div">
    		<div style="float: left;">
    			<input class="easyui-numberbox" label="年龄：" readonly="readonly"  labelWidth="${label_width}" labelAlign="right" style="width:${(input_width-label_width)/2+label_width}px;height:25px;" 
    				name="age" id="age"
    				data-options="min:1,max:200,onChange:calculateAge"/>
    		</div>
			<div style="float: left;">
				<input class="easyui-combobox" style="width:${(input_width-label_width)/2}px;height:25px;"  readonly="readonly" 
					name="ageunit" id="ageunit"
					data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0008',editable:false,panelHeight:'auto',
					onChange:calculateAge,
					onLoadSuccess:function(){$(this).combobox('select', 'Y')}"/>
			</div>
		</div>
    	<div class="interrogation_content_div">
			<input class="easyui-datebox" label="出生日期：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"  readonly="readonly" 
    			name="birthdate" id="birthdate"
    			data-options="editable:false,onSelect:getAgeFromBirthdate"/>
      	</div>
		<div class="interrogation_content_div">
			<input id="appdeptcode" name="appdeptcode" class="easyui-combobox" label="申请科室：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
				data-options="valueField:'deptcode',textField:'deptname', editable:false, panelHeight:'120px',
      				url:'${ctx}/dic/getDeptFromCache?type=P',onChange:getappdoctor,
      				icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]"/>
		</div>
    	<div class="interrogation_content_div">
			<input  id="appdoctorcode" class="easyui-combobox" label="申请医生：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
				data-options="valueField: 'appdoctorcode',textField: 'appdoctorname', editable:false, panelHeight:'120px'"/>
		</div>
    	<div class="interrogation_content_div">
			<input class="easyui-textbox" label="门诊/住院号：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
    			id="outnoOrinno" readonly="readonly"
    			data-options=""/>
    	</div>
    	<div class="interrogation_content_div">
    		<input class="easyui-textbox" label="病区：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
      			name="wardno"/>
      	</div>
    	<div class="interrogation_content_div">
      		<input class="easyui-textbox" label="病床：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
      			name="bedno"/>
      	</div>
    	<div class="interrogation_content_div">
      		<input class="easyui-numberbox" label="电话：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
    			name="telephone"
    			data-options=""/>
		</div>
		
		<div class="interrogation_content_div">
			<input class="easyui-combobox" label="核素：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
      			name="nuclide"
      			data-options="url:'${ctx}/dic/findDicCommonFromCache?group=nuclide',valueField: 'id',textField: 'name',editable:false,panelHeight:'auto'"/>
      	</div>
    	<div class="interrogation_content_div">
      		<input class="easyui-combobox" label="药物：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
      			name="medicine"
      			data-options="url:'${ctx}/comGoods/getDicComGoodsCache',valueField: 'id',textField: 'goods_name',editable:false,panelHeight:'auto'"/>
      	</div>
    	<div class="interrogation_content_div">	
			<input class="easyui-combobox" label="检查部位：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
      			name="organ" id="organ_inq"
      			data-options="editable:false,panelHeight:'120px',
						valueField: 'id',textField: 'treename_zh',panelHeight:'120px',
						icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]"/>
		</div>
    	<div class="interrogation_content_div">
			<input id="examination_method" class="easyui-combobox" label="检查方法：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
      			name="examination_method"
      			data-options="valueField: 'id',textField: 'name',editable:false,panelHeight:'120px'"/>
      	</div>
    	<div class="interrogation_content_div">			
 			<input class="easyui-numberspinner" label="身高：" precision="2" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width-20}px;" 
      			name="height" required="true"
      			data-options=""/>cm
      	</div>
    	<div class="interrogation_content_div">
      		<input class="easyui-numberspinner" label="体重：" precision="2" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width-17}px;" 
      			name="weight" required="true"
      			data-options=""/>kg	
    	</div>
	 	<div class="interrogation_content_div">
      		<input id="fasting_glucose" class="easyui-numberspinner" precision="2" label="空腹血糖：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width-52}px;" 
      			name="fasting_glucose" required="true"
      			data-options=""/>mmol/L
      	</div>
    	<div class="interrogation_content_div">
      		<input class="easyui-numberspinner" label="处方剂量：" precision="2" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width-25}px;" 
      			name="prescribed_dose" required="true"
      			data-options=""/>mCi
      	</div>
    	<div class="interrogation_content_div">
      		<input class="easyui-combobox" label="给药方式：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
				name="administration_method"
				data-options="url:'${ctx}/dic/findDicCommonFromCache?group=administrationmethod',valueField: 'id',textField: 'name',editable:false,panelHeight:'auto'"/>
		</div>
    	<div class="interrogation_content_div">	
			<input class="easyui-combobox" label="特殊注射要求：" labelWidth="${label_width_long}" labelAlign="right" style="height:25px;width:${input_width_long-155}px;" 
				name="sp_injection_req"
				data-options="url:'${ctx}/dic/findDicCommonFromCache?group=injectionspecialrequest',valueField:'id',textField:'name', editable:false, panelHeight:'auto'"/>
		</div>
    	<div class="interrogation_content_div">
			<input class="easyui-combobox" label="特殊检查要求：" labelWidth="${label_width_long}" labelAlign="right" style="height:25px;width:${input_width_long-155}px;" 
				name="sp_inspection_req"
				data-options="url:'${ctx}/dic/findDicCommonFromCache?group=studyspecialrequest',valueField:'id',textField:'name', editable:false, panelHeight:'250px'"/>	
      	</div>
    	
    	<%-- <div class="interrogation_content_div">
      		<input class="easyui-combobox" label="问诊医生：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
      			id="interrogation_doctor_id" name="interrogation_doctor_id"
      			data-options="url:'${ctx}/dic/getEmployeeFromCache?profession=ConsultDoctor',valueField: 'id',textField: 'name',
      			editable:false,panelHeight:'120px',
      			onLoadSuccess:setInterrogationDoctor">
      	</div> --%>
		<%-- <div class="interrogation_content_div">
    		<input class="easyui-datebox" label="问诊日期：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
    			id="interrogation_time" name="interrogation_time"
    			data-options="editable:false">
    	</div> --%>
	</div>
</body>
</html>