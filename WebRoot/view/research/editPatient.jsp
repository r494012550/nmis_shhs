<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%@ include file="/common/meta.jsp"%>
    <title>Insert title here</title>
</head>
<body>

<div style="padding:5px;margin-left:auto;margin-right:auto;">
    <form name="patientform" id="patientform" method="POST">
        <div>
            <div style="margin-top:10px;margin-bottom:5px;">
                <input class="easyui-textbox" label="姓名" labelAlign="right"  id="patientname"
                       data-options="prompt:'请输入病人姓名...',required:true," name="patientname" style="width:300px;" value="${patient.patientname}">
            </div>
        </div>
        <div>
            <div style="margin-bottom:5px;">
                <input class="easyui-textbox" label="编号" labelAlign="right" id="patientid"
                       data-options="prompt:'请输入病人编号...',required:true,onChange:checkPatientid" name="patientid" style="width:300px;"  value="${patient.patientid}">
            </div>
        </div>
        <div>
            <div style="margin-bottom:5px;">
                <input class="easyui-combobox" label="性别" labelAlign="right"  id="sex"
                       data-options="valueField:'code',textField:'name_zh',editable:false,panelHeight:'auto',
						url:'${ctx}/syscode/getCode?type=0001'" name="sex" style="width:300px;" value="${patient.sex}">
            </div>
        </div>
        <div>
            <div style="margin-bottom:5px;">
                <input class="easyui-textbox" label="年龄" labelAlign="right" id="age"
                       data-options="prompt:'请输入病人年龄...',required:true," name="age" style="width:300px;" value="${patient.age}">
            </div>
        </div>
        <div>
            <div style="margin-bottom:5px;">
                <input class="easyui-datebox" label="出生日期" labelAlign="right" id="birthdate"
                       data-options="prompt:'请输入病人生日...',required:true," name="birthdate" style="width:300px;" value="${patient.birthdate}">
            </div>
        </div>
        <input id="id" name="id" type="hidden" value="${patient.id}">
        <input id="patientidOld" name="patientidOld" type="hidden" value="${patient.patientid}">
    </form>
    <input id="checkexist" type="hidden">
</div>
</body>
</html>