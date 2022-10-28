<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<c:set var="input_width" value="230"/>
<c:set var="label_width" value="100"/>
<title>Insert title here</title>
</head>
<body>

	<div class="easyui-panel" title='就诊' halign='left' style="height:90px;padding:3px 3px 0px 3px;width:990px;"
     			data-options="tools:'#admissiontools',headerCls:'panelHeaderCss',bodyCls:'panelBodyCss'">
			<div id="admissiontools">
      			<a class="easyui-tooltip" title="就诊备注" onclick="openNotes('admissionremark_reg','入院备注');">
					<i class="iconfont icon-info"></i></a>
      			<a class="easyui-tooltip" title="清空就诊信息" onclick="clearAdmission();">
					<i class="iconfont icon-qingkong" style="font-size:15px;"></i></a>
      		</div>
      		<input id="admissionkid_reg" name="admission_id" type="hidden"/>
      		<input id="admissionremark_reg" name="admissionremark" type="hidden"> 
      		<div class="reg_content_div">
      			<input id="admissionid_reg" name="admissionid" class="easyui-textbox" label="就诊编号：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
      				readonly="readonly" prompt="自动编号">
      		</div>
      		<div class="reg_content_div">
      			<input id="patientsource_reg" name="patientsource" class="easyui-combobox" label="病人来源：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
					data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0002',editable:false,panelHeight:'auto',
						onLoadSuccess:function(){$(this).combobox('select', 'O')},onChange:setDept"  required="true" >
      		</div>
      		<div class="reg_content_div">
      			<input id="cardno_reg" name="cardno" class="easyui-textbox" label="卡号：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;">
      		</div>
      		<div class="reg_content_div">
      			<input id="inno_reg" name="inno" class="easyui-textbox" label="住院号：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
					data-options="onChange:checkSameInNo">
      		</div>
      		<div class="reg_content_div">
      			<input id="outno_reg" name="outno" class="easyui-textbox" label="门诊号：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
					data-options="onChange:checkSameOutNo">
      		</div>
      		<div class="reg_content_div">
      			<input id="wardno_reg" name="wardno" class="easyui-textbox" label="病区：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
      				 >
      		</div>
      		<div class="reg_content_div">
      			<input id="bedno_reg" name="bedno" class="easyui-textbox" label="病床：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
      				 >
      		</div>
      		<%-- <div class="reg_content_div">
      			<input id="institutionName_reg" name="institutionName" type="hidden"/>
      			<input id="institutionid_reg" name="institutionid" class="easyui-combobox" label="送检机构：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
                	data-options="valueField:'id',textField:'name',url:'${ctx}/dic/getInstitutionFromCache',editable:false,panelHeight:'120px',
                        icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]" >
      		</div> --%>
      		<%-- 
      		<div class="reg_content_div">
      			<input id="insurance_reg" name="insurance" class="easyui-textbox" label="医保号：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;">
      		</div>
      		<div class="reg_content_div">
      			<input id="admission_date_reg" name="adm_date" class="easyui-datebox" label="入院日期：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;">
      		</div>
      		<div class="reg_content_div">
      			<input id="discharge_date_reg" name="dis_date" class="easyui-datebox" label="出院日期：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;">
      		</div> --%>
      		
      		<%-- <div class="reg_content_div">
      			<input id="admittingDiagnosis_reg" name="admittingdiagnosis" class="easyui-textbox" label="临床诊断：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width*2+3}px;"
					data-options="multiline:true">
      		</div> --%>
      		<%-- <div class="reg_content_div">
      			<input id="subjective_reg" name="subjective" class="easyui-textbox" label="病人主诉：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width*2+3}px;"
					data-options="multiline:true">
      		</div> --%>
      		
      		<%-- <div class="reg_content_div">
      			<input id="workunitcode_reg" name="workunitcode" type="hidden"/>
      			<input id="workunit_reg" name="admission.workunit" class="easyui-textbox" label="单位：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width*2+3}px;"
					data-options="multiline:true">
      		</div> --%>
      		<div class="reg_content_div">
      			<select id="admissiondg_reg" class="easyui-combogrid" name="admrecord_sch" label="就诊记录：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width*2+3}px;"
			        data-options="
			            panelWidth:400,editable:false,
			            idField:'admissionid',
			            textField:'admissionid',
			            onSelect:function(index,row){sameAdmission(index,row)},
			            columns:[[
			                {field:'admissionid',title:'入院编号',width:80},
			                {field:'psource',title:'病人来源',width:60},
			                {field:'cardno',title:'卡号',width:60},
			                {field:'outno',title:'门诊号',width:60},
			                {field:'inno',title:'住院号',width:60},
			                {field:'subjective',title:'病人主诉',width:120},
			                {field:'admittingdiagnosis',title:'入院诊断',width:120},
			                {field:'createtime',title:'创建时间',width:120}
			            ]]
			        "></select>
      		</div>
	</div>
  
</body>
</html>