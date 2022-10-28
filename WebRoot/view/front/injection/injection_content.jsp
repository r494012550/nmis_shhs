<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<c:set var="label_width" value="110"/>
<c:set var="label_width_short" value="50"/>

<c:set var="input_width" value="230"/>
<c:set var="input_width_short" value="100"/>


</head>
<body>
<div class="easyui-layout" data-options="fit:true" style="padding:0px;margin:auto;">
	<div data-options="region:'west',href:'${ctx}/injection/westSearch', onLoad:initWestsearch, hideCollapsedContent:false,headerCls:'panelHeaderCss_top'" style="width:700px;">
	
	</div>
	
	<div data-options="region:'center',border:false">
		<div class="easyui-layout" data-options="fit:true" style="padding:0px;margin:auto;">
		
			<div data-options="region:'south',border:false" style="height:80px;">
				<div style="padding:5px;margin-right:auto;margin-left: auto;width:520px;">
					<a class="easyui-linkbutton" style="width:120px;" id="printSave"  data-options="disabled:true" onclick="saveInjection()">保存</a>
					<a class="easyui-linkbutton c2" style="width:120px;" id="printEmpty"  data-options="disabled:true" onclick="clearInjectionForm()">清空</a>
					<a class="easyui-linkbutton" style="width:120px;" id="printInspection"  data-options="disabled:true" onclick="toProcess()">检查流程</a>
					<a class="easyui-linkbutton" style="width:120px;" id="printExamine"  data-options="disabled:true" onclick="openRemarkDialog()">查看病人备注</a>
	<!-- 				<a class="easyui-linkbutton" style="width:80px;"  onclick="injectioncalling();">叫号</a> -->
					<!-- <a class="easyui-linkbutton" style="width:120px;" onclick="">电子病历</a> -->
				</div>
			</div>
				        
			<div data-options="region:'center',border:false">
			<form id="injectionForm" method="POST">
			<div style="padding:3px;margin-left:auto;margin-right:auto;border:0px;width:550px;">
				
				<div style="margin-top: 10px;text-align:center;">
					<font size="5">注射记录</font>
				</div>
				
				<div style="width: 100%;margin-top: 10px;" class="mylabel">
					<input class="easyui-textbox" label="姓名：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
						name="patientname" readonly="readonly"/>
    				
					<input class="easyui-textbox" label="性别：" labelWidth="${label_width+25}" labelAlign="right" style="height:25px;width:${input_width+25}px;"
						name="sexdisplay" readonly="readonly"/>
				</div>
				<div style="width: 100%;margin-top: 10px;" class="mylabel">
					<input class="easyui-textbox" label="年龄：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
						id="agedisplay" readonly="readonly"/>
					<input class="easyui-textbox" label="身高：" labelWidth="${label_width+25}" labelAlign="right" style="height:25px;width:${input_width+25}px;"
						name="height" readonly="readonly"/>
				</div>
				<div style="width: 100%;margin-top: 10px;" class="mylabel">
					<input class="easyui-textbox" label="体重：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
						name="weight" readonly="readonly"/>
					<input class="easyui-textbox" label="核素：" labelWidth="${label_width+25}" labelAlign="right" style="height:25px;width:${input_width+25}px;"
						name="nuclide_name" readonly="readonly"/>
				</div>
				 
				<div style="width: 100%;margin-top: 10px;" class="mylabel">
					<input class="easyui-textbox" label="检查药物：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
						name="medicine_name" readonly="readonly"/>
					<input class="easyui-textbox" label="检查部位：" labelWidth="${label_width+25}" labelAlign="right" style="height:25px;width:${input_width+25}px;"
						name="examination_position_name" readonly="readonly"/>
				</div>
				 
				<div style="width: 100%;margin-top: 10px;" class="mylabel">
					<input class="easyui-textbox" label="检查方法：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
						name="examination_method_name" readonly="readonly"/>
					<input class="easyui-textbox" label="问诊医生：" labelWidth="${label_width+25}" labelAlign="right" style="height:25px;width:${input_width+25}px;"
						name="interrogation_doctor_name" readonly="readonly"/>
				</div>
				
				<div class="easyui-panel" style="width: 100%;margin-top: 10px;border-left:0px;border-bottom:0px;border-right:0px;">
					<div style="width: 100%;margin-top: 10px;" class="mylabel">
						<input class="easyui-combobox" label="给药方式：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
							name="administration_method"
							data-options="url:'${ctx}/dic/findDicCommonFromCache?group=administrationmethod',valueField:'id',textField:'name', editable:false, panelHeight:'200px'"/>
	
						<input class="easyui-combobox" label="注射部位：" labelWidth="${label_width+25}" labelAlign="right" style="height:25px;width:${input_width+25}px;"
							name="injection_site"
							data-options="url:'${ctx}/dic/findDicCommonFromCache?group=injectionsite',valueField:'id',textField:'name', editable:false, panelHeight:'200px'"/>
					</div>
					 
					<div style="width: 100%;margin-top: 10px;" class="mylabel">
						<input class="easyui-combobox" label="注射人：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
							name="injecter_id"
							data-options="url:'${ctx}/dic/getEmployeeFromCache?profession=T',valueField:'id',textField:'name', editable:false, panelHeight:'200px'"/>
	
						<input class="easyui-combobox" label="污染：" labelWidth="${label_width+25}" labelAlign="right" style="height:25px;width:${input_width+25}px;"
							name="pollution"
							data-options="url:'${ctx}/dic/findDicCommonFromCache?group=pollution',valueField:'id',textField:'name', editable:false, panelHeight:'200px'"/>
					</div>
					 
					<div style="width: 100%;margin-top: 10px;" class="mylabel">
						<input class="easyui-combobox" label="接待护士：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
							name="reception_nurse"
							data-options="url:'${ctx}/dic/getEmployeeFromCache?profession=N',valueField:'id',textField:'name', editable:false, panelHeight:'200px'"/>
							
						<input class="easyui-combobox" label="药物来源：" labelWidth="${label_width+25}" labelAlign="right" style="height:25px;width:${input_width+25}px;"
							name="drug_source"
							data-options="url:'${ctx}/dic/findDicCommonFromCache?group=drugsource',valueField:'id',textField:'name', editable:false, panelHeight:'200px'"/>
					</div>
					 
					<div style="width: 100%;margin-top: 10px;" class="mylabel">
						<input id="injection_dose_pre" class="easyui-numberspinner" label="注射前剂量：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
							name="injection_dose_pre"
							data-options="min:0,precision:3,onChange:countInjectionDoseReal"/>mCi
	
						<input class="easyui-numberspinner" label="处方剂量：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
							name="prescription_dose"
							data-options="min:0,precision:3"/>mCi
					</div>
					 
					<div style="width: 100%;margin-top: 10px;" class="mylabel">
						<input id="injection_dose_after" class="easyui-numberspinner" label="注射后剂量：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
							name="injection_dose_after"
							data-options="min:0,precision:3,onChange:countInjectionDoseReal"/>mCi
	
						<input id = "injection_dose_real" class="easyui-numberspinner" label="实际注射剂量：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;background-color:red;"
							name="injection_dose_real"
							data-options="min:0,precision:3" required="required"/>mCi
					</div>
					<div style="width: 100%;margin-top: 10px;" class="mylabel">
						<input class="easyui-datetimebox" label="注射时间：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:490px;"
							name="injection_datetime" id="injection_datetime" editable="false"
							data-options="showSeconds: true"/ required="required">
					</div>
					<div style="width: 100%;margin-top: 10px;" class="mylabel">
					 	<input class="easyui-textbox" label="备注：" labelWidth="${label_width}" labelAlign="right" style="width:490px;height:100px;" data-options="multiline:true"
					 		name="injection_remark"/>
					</div>
					
					<input id="orderid" name="orderid" type="hidden">
					<input id="studyid" name="studyid" type="hidden">
					<input id="patientid" name="patientid" type="hidden">
					<input id="admissionid" name="admissionid" type="hidden">
	     			<input id="orderstatus" name="orderstatus" type="hidden">
	     			<input id="injectionid" name="id" type="hidden">
				</div>
				
			</div>
			</form>
			</div>
		</div>
	
	</div><!-- region:center -->
</div>
</body>
</html>