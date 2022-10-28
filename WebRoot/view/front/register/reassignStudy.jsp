<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>登记</title>
</head>
<body>

<c:set var="input_width" value="220px"/>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false" style="height:70px;padding:5px;">
		<table class="mytablelaout" style="width:840px;">
  			<tr>
	  			<td style="width:280px;">
	  				<input class="easyui-textbox" id="studyid_sc" label="患者编号：" labelWidth="80" labelAlign="right" style="width:${input_width};height:25px;"
	  				 readonly="readonly" value="${studyorder.patientid}">
	  			</td>
	  			<td style="width:280px;">
	  				<input class="easyui-textbox" id="studyid_sc" label="检查编号：" labelWidth="80" labelAlign="right" style="width:${input_width};height:25px;"
	  				 readonly="readonly" value="${studyorder.studyid}">
	  			</td>
	  			<td style="width:280px;">
	  				<input class="easyui-textbox" id="" label="患者姓名：" labelWidth="80" labelAlign="right" style="width:${input_width};height:25px;"
	  				 readonly="readonly" value="${patient.patientname}">
	  			</td>
  			</tr>
  			<tr>
  				<td colspan="2">
	  				<input class="easyui-textbox" id="" label="检查项目：" labelWidth="80" labelAlign="right" style="width:499px;height:25px;"
	  				 readonly="readonly" value="${studyorder.studyitems}">
	  			</td>
	  			
	  			<td>
	  				<a class="easyui-linkbutton" onclick="goChoosePatient()" style="width:${input_width};height:28px">选择已有患者</a>
	  			</td>
  			</tr>
		</table>
	</div>
	
	<form name="splitPatientForm" id="splitPatientForm" method="POST">
		<input id="orderid_split" name="orderid" type="hidden" value="${studyorder.id}"/>
	<div data-options="region:'center',border:false" title="指定目标患者" style="padding:5px;">
		<div style="margin-top: 0px;padding:0px 0px 0px 10px;">
			<a class="easyui-linkbutton" onclick="newPatient()" style="width:200px;height:28px">录入新患者信息</a>
		</div>
		<div style="margin-top: 5px;padding:0px 0px 0px 30px;">
			<input id="patientkid_split" name="patient.id" type="hidden"/>
			<table class="mytablelaout" style="width:810px;">
				<tr>
					<td style="width:270px;">
						<input class="easyui-textbox" id="patientid_split" name="patient.patientid" label="患者编号：" labelWidth="80" labelAlign="right" 
							style="height:25px;width:${input_width};" readonly="readonly" prompt="自动编号">
					</td>
					<td style="width:270px;">
						<input class="easyui-textbox" id="patientname_split" name="patient.patientname" label="患者姓名：" labelWidth="80" labelAlign="right"
							style="height:25px;width:${input_width};" required="true" readonly="readonly"
							data-options="onChange:function(){$('#pinyin_split').textbox('setValue',pinyinUtil.getPinyin($(this).val(), '', false, false, true));}">
					</td>
					<td style="width:270px;">
						<input class="easyui-textbox" id="pinyin_split" name="patient.py" label="拼音：" labelWidth="80" labelAlign="right"
							style="height:25px;width:${input_width};" required="true" readonly="readonly">
					</td>
				</tr>
				<tr>
					<td style="width:130px;">
						<input class="easyui-combobox" id="sex_split" name="patient.sex" label="性别：" labelWidth="80" labelAlign="right"
							style="height:25px;width:${input_width};" required="true" readonly="readonly"
							data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0001',editable:false,panelHeight:'auto'">
					</td>
					<td>
						<input id="age_split" name="admission.age" type="text" class="easyui-numberbox" label="年龄：" labelWidth="80" labelAlign="right"
							style="width:150px;height:25px;"  required="true" readonly="readonly"
							data-options="min:0,max:200,onChange:calculate_age1_split">
						<input id="age_unit_split" name="admission.ageunit" class="easyui-combobox" style="width:66px;height:25px;" required="true" readonly="readonly"
							data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0008',editable:false,panelHeight:'auto',
								onLoadSuccess:function(){$(this).combobox('select', 'Y')},onChange:calculate_age2_split">
					</td>
					<td>
						<input id="birthdate_split" name="patient.birthdate" class="easyui-datebox" label="出生日期：" labelWidth="80" labelAlign="right"
							style="height:25px;width:${input_width};" readonly="readonly"
							data-options="onSelect:getAgeFromBirthdate_split">
					</td>
				</tr>
				<tr>
					<td>
						<input id="height_split" name="patient.height" class="easyui-numberbox" label="身高(cm)：" labelWidth="80" labelAlign="right"
							style="height:25px;width:${input_width};" readonly="readonly"
							data-options="min:0,precision:2">
					</td>
					<td>
						<input id="weight_split" name="patient.weight" class="easyui-numberbox" label="体重(Kg)：" labelWidth="80" labelAlign="right"
							style="height:25px;width:${input_width};" readonly="readonly"
							data-options="min:0,precision:2">
					</td>
					<td>
						<input id="title_split" name="patient.title" class="easyui-textbox" label="头衔：" labelWidth="80" labelAlign="right"
							style="height:25px;width:${input_width};" readonly="readonly">
					</td>
				</tr>
				<tr>
					<td>
						<input id="telephone_split" name="patient.telephone" class="easyui-textbox" label="联系电话：" labelWidth="80" labelAlign="right"
							style="height:25px;width:${input_width};" readonly="readonly">
					</td>
					<td colspan="2">
						<input id="address_split" name="patient.address" class="easyui-textbox" label="联系地址：" labelWidth="80" labelAlign="right"
							style="height:25px;width:490px;" readonly="readonly">
					</td>
				</tr>
				<tr>
					<td colspan="3">
						<input id="patientremark_split" name="patientremark" class="easyui-textbox" label="患者备注：" labelWidth="80" labelAlign="right"
							multiline="true" style="height:25px;width:760px;" readonly="readonly">
					</td>
				</tr>
		    </table>
		</div>
	</div>
	
	<div data-options="region:'south',border:false,collapsible:false" title="指定入院信息" style="height:290px;padding:5px;">
		<div style="margin-top: 0px;padding:0px 0px 0px 10px;">
			<!-- <a class="easyui-linkbutton" id="chooseAdmission_btn" onclick="goChooseAdmission()" disabled="true" style="width:268px;height:28px">选择已有入院信息</a> -->
			<a class="easyui-linkbutton" id="newAdmission_btn" onclick="newAdmission()" disabled="true" style="width:200px;height:28px">录入新入院信息</a>
		</div>
		<div style="margin-top: 5px;padding:0px 0px 0px 30px;">
			<input id="admissionkid_split" name="admission.id" type="hidden"/>
			<table class="mytablelaout" style="width:810px;">
				<tr>
					<td style="width:270px;">
						<input id="admissionid_split" name="admission.admissionid" class="easyui-textbox" label="入院编号：" labelWidth="80" labelAlign="right"
							style="height:25px;width:${input_width};" readonly="readonly" prompt="自动编号">
					</td>
					<td style="width:270px;">
						<input id="patientsource_split" name="admission.patientsource" class="easyui-combobox" label="患者来源：" labelWidth="80" labelAlign="right"
							style="height:25px;width:${input_width};"  required="true" readonly="readonly"
							data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0002',editable:false,panelHeight:'auto',onChange:setDept_split" >
					</td>
					<td style="width:270px;">
						<input id="cardno_split" name="admission.cardno" class="easyui-textbox" label="卡号：" labelWidth="80" labelAlign="right"
							style="height:25px;width:${input_width};" readonly="readonly">
					</td>
				</tr>
				<tr>
					<td>
						<input id="inno_split" name="admission.inno" class="easyui-textbox" label="住院号：" labelWidth="80" labelAlign="right"
							style="height:25px;width:${input_width};" readonly="readonly">
					</td>
					<td>
						<input id="outno_split" name="admission.outno" class="easyui-textbox" label="门诊号：" labelWidth="80" labelAlign="right"
							style="height:25px;width:${input_width};" readonly="readonly">
					</td>
					<td>
						<input id="wardno_split" name="admission.wardno" class="easyui-combobox" label="病区：" labelWidth="80" labelAlign="right"
							style="height:25px;width:${input_width};" readonly="readonly"
							 data-options="valueField: 'id',textField: 'deptname',panelHeight:'120px',editable:false">
		            </td>
		         </tr>
		         <tr>
					<td>
						<input id="bedno_split" name="admission.bedno" class="easyui-combobox" label="病床：" labelWidth="80" labelAlign="right"
							style="height:25px;width:${input_width};" readonly="readonly"
							 data-options="valueField: 'id',textField: 'text',panelHeight:'120px',editable:false">
		            </td>
					<td>
						<input id="insurance_split" name="admission.insurance" class="easyui-textbox" label="医保号：" labelWidth="80" labelAlign="right"
							style="height:25px;width:${input_width};" readonly="readonly">
					</td>
					<td>
						 <input id="institutionid_split" name="admission.institutionid" class="easyui-combobox" label="送检医院：" labelWidth="80" labelAlign="right"
						 	style="height:25px;width:${input_width};" readonly="readonly"
						 	data-options="valueField:'id',textField:'name',url:'${ctx}/dic/getInstitution',editable:false,panelHeight:'auto',
                        	icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]" >
		            </td>
				</tr>
				<tr>
					<td>
		             	<input id="admission_date_split" name="admission.adm_date" class="easyui-datebox" label="入院日期：" labelWidth="80" labelAlign="right"
		             		style="height:25px;width:${input_width};" readonly="readonly">
		            </td>
					<td colspan="2">
		             	<input id="discharge_date_split" name="admission.dis_date" class="easyui-datebox" label="出院日期：" labelWidth="80" labelAlign="right"
		             		style="height:25px;width:${input_width};" readonly="readonly">
		            </td>
		            
		        <tr>
					<td colspan="3">
						<input id="admittingDiagnosis_split" name="admission.admittingdiagnosis" class="easyui-textbox" label="入院诊断：" labelWidth="80" labelAlign="right"
							style="width:760px;height:25px;"
							data-options="multiline:true" readonly="readonly">
					</td>
					
	            </tr>
	            <tr>
					<td colspan="3">
						<input id="subjective_split" name="admission.subjective" class="easyui-textbox" label="患者主诉：" labelWidth="80" labelAlign="right"
							style="width:760px;height:25px;"
							data-options="multiline:true" readonly="readonly">
	                </td>   
	            </tr>
	            <tr>
					<td colspan="3">
						 <input id="admissionremark_split" name="admissionremark" class="easyui-textbox" label="入院备注：" labelWidth="80" labelAlign="right"
						 	multiline="true" style="width:760px;height:25px;" readonly="readonly"> 
	                </td>
				</tr> 
		    </table>
		</div>
	</div>
	</form>
</div>
</body>
</html>