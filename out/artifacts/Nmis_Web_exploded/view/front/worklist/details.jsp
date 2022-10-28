<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript" src="js/front/details.js"></script>
<script type="text/javascript" src="/js/common.js"></script>
<div class="content" style="width: 100%;">
        <table style="width: 98%;padding-left: 10px;" >
  <tr>
    <th style="text-align: center;padding-top: 10px;" colspan="6"><font size="5px">患者基本信息</font><hr></th>
  </tr>
  <tr style="text-align: right;">
  <td >患者编号：</td>
    <td><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box;" readOnly="true"  id="_patientid"></td>
    <td>患者姓名：</td>
    <td><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box;"  readOnly="true"   id="_patientname"></td>
    <td>拼音：</td>
    <td ><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box;"  readOnly="true"  id="_py"></td>
  </tr>
   <tr style="text-align: right;">
   	<td>性别：</td>
    <td><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box;"  readOnly="true"  id="_sexdisplay"></td>
    <td >年龄：</td>
    <td  style="text-align: right;"><input style="height: 30px;float:left; width:40px;padding:0 5px;box-sizing: border-box;"  readOnly="true"   id="_age">出生日期：<input style="height: 30px; width:85px;padding:0 5px;box-sizing: border-box;"  readOnly="true"   id="_birthdate"></td>
    <td>卡号：</td>
    <td><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box;"  readOnly="true"   id="_cardno"></td>
  </tr>
  <tr style="text-align: right;">
    <td>住院号：</td>
    <td><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box;"  readOnly="true"   id="_inno"></td>
    <td>病区：</td>
    <td><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box;"  readOnly="true" id="_wardno"></td>
     <td>病床：</td>
    <td><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box;"  readOnly="true"   id="_bedno"></td>
    
  </tr>
  <tr style="text-align: right;">
  	<td>门诊号：</td>
    <td><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box;"  readOnly="true" id="_outno"></td>
  	<td>联系电话：</td>
    <td><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box;"  readOnly="true"   id="_telephone"></td>
 	 <td>联系地址：</td>
    <td ><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box;"  readOnly="true"   id="_address"></td>
  </tr>
  <tr style="text-align: right;">
  
   
  
  </tr>
   <tr>
    <th style="text-align: center;padding-top: 10px;" colspan="6"><font size="5px">患者检查信息</font><hr></th>
  </tr>
  <tr style="text-align: right;">
   <td>检查号：</td>
    <td ><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box;"  readOnly="true" id="_studyid"></td>
     <td>病人来源：</td>
    <td ><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box;"  readOnly="true" id="_patientsource"></td>
     <td>优先级别：</td>
    <td><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box;"  readOnly="true" id="_priority"></td>
  </tr>
   <tr style="text-align: right;">
     <td>设备类型：</td>
    <td><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box; width:100%;"  readOnly="true" id="_modality_type"></td>
      <td>申请科室：</td>
     <td><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box; width:100%;"  readOnly="true" id="_appdept"></td>
       <td>申请医生：</td>
    <td><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box; width:100%;"  readOnly="true" id="_appdoctor"></td>
  </tr>
  <tr style="text-align: right;">
    <td>检查项目：</td>
    <td colspan="3"><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box; width:100%;"  readOnly="true" id="_studyitems"></td>
    <td>检查设备：</td>
    <td><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box; width:100%;"  readOnly="true" id="_modality"></td>
  </tr>
  <tr style="text-align: right;">
    <td>备注信息：</td>
    <td colspan="5"><input style="height: 30px; width:100%;padding:0 5px;box-sizing: border-box; width:100%;"  readOnly="true" id="_remark"></td>
  </tr>
        </table>
        <div style="text-align: right;margin-top: 10px;margin-right: 20px;">
		<a class="easyui-linkbutton" plain="true" onClick="$('#details').dialog('close')"
		style="margin-right: 2px;height: 30px; width:80px; text-align: center;background-color: #3684D9;padding:5px;"> 关闭</a>
		</div>
    </div>
 
