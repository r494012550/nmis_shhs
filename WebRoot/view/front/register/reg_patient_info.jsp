<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<c:set var="input_width" value="220"/>
<c:set var="label_width" value="90"/>
<title>Insert title here</title>
</head>
<body>	
     <div class="easyui-panel" title='病人' halign='left' style="height:90px;padding:3px 3px 0px 3px;width:990px;"
     			data-options="tools:'#patienttools',headerCls:'panelHeaderCss',bodyCls:'panelBodyCss'">
		<div id="patienttools">
			<a class="easyui-tooltip" title="病人备注" onclick="openNotes('patientremark_reg','病人备注');">
					<i class="iconfont icon-info"></i></a>
    	</div>
    	<input id="patientkid_reg" name="patient_id" type="hidden"/>
    	<input id="patientremark_reg"  name="patientremark" type="hidden"> 
    	<input id="vipflag_reg" name="vipflag" type="hidden">
    	<div class="reg_content_div">
    		<input class="easyui-textbox" id="patientid_reg" name="patientid" label="病人编号：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
    			readonly="readonly" prompt="自动编号">
    	</div>
    	<div class="reg_content_div">
    		<input class="easyui-textbox" id="patientname_reg" name="patientname" label="病人姓名：" labelWidth="${label_width+1}" labelAlign="right" style="height:25px;width:${input_width}px;" 
    			required="true" data-options="onChange:checkSameName,validType:'isSpace'">
    	</div>
    	<div class="reg_content_div">
    		<input class="easyui-combobox" data-options="valueField:'py',textField:'py'" id="pinyin_reg" name="py" label="拼音：" labelWidth="${label_width+1}" labelAlign="right" style="height:25px;width:${input_width}px;" 
    			required="true" validType="isSpace">
    	</div>
    	<div class="reg_content_div">
    		<input class="easyui-combobox" id="sex_reg" name="sex"  label="性别：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
    			data-options="valueField:'code',panelHeight:'auto',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0001',editable:false,onLoadSuccess:function(){$(this).combobox('select', 'M')}" required="true">
    	</div>
    	<div class="reg_content_div">
    		<div style="float: left;">
    		<input class="easyui-numberbox" id="age_reg" name="age" type="text"  label="年龄：" labelWidth="${label_width}" labelAlign="right" style="width:${(input_width-label_width)/2+label_width}px;height:25px;" 
    			data-options="min:1,max:200,onChange:calculate_age1" required="true"/>
    		</div>
    		<div style="float: left;">
			<input class="easyui-combobox" id="age_unit_reg" name="ageunit" style="width:${(input_width-label_width)/2}px;height:25px;" 
				data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0008',editable:false,panelHeight:'auto',
					onLoadSuccess:function(){$(this).combobox('select', 'Y')},onChange:calculate_age2" required="true"/>
			</div>
    	</div>
    	<div class="reg_content_div">
    		<input class="easyui-datebox" id="birthdate_reg" name="birthdate" label="出生日期：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
    			required="required" data-options="editable:false,onSelect:getAgeFromBirthdate">
    	</div>
    	<div class="reg_content_div">
    		<input class="easyui-numberbox" id="height_reg" name="height" label="身高(cm)：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
				data-options="min:0,max:300">
    	</div>
    	<div class="reg_content_div">
    		<input class="easyui-numberbox" id="weight_reg" name="weight" label="体重(Kg)：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
				data-options="min:0,max:500">
    	</div>
    	<div class="reg_content_div">
    		<input class="easyui-textbox" id="idnumber_reg" name="idnumber" label="身份证号：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
    			   validType="isCardNo"/>
    	</div>
    	<%-- <div class="reg_content_div">
    		<input id="title_reg" name="title" class="easyui-textbox" label="头衔：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;">
    	</div> --%>
    	<div class="reg_content_div">
    		<input id="telephone_reg" name="telephone" class="easyui-textbox" label="联系电话：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
    			validType="checkPhone"/>
    	</div>
    	<div class="reg_content_div">
    		<input id="address_reg" name="address" class="easyui-textbox" label="联系地址：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;">
    	</div>
    	<div class="reg_content_div">
    		<input id="vipflagcheck_reg" class="easyui-checkbox" label="vip：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:25px;"
	         data-options="checked:false,onChange:function(checked){checked?$('#vipflag_reg').val('1'):$('#vipflag_reg').val('0');}">
    	</div>
    	<%-- <div class="reg_content_div">
    		<input id="externalpatientno_reg" name="externalpatientno" class="easyui-textbox" label="外院病历号：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;">
    	</div> --%>
	</div>
</body>
</html>