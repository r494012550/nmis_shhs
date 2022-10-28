<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title>登记</title>
</head>
<body>
	<table id="patientdg1_split" class="easyui-datagrid"
		data-options="fit:true,showFooter: false,loadMsg:'loading',emptyMsg:'${sessionScope.locale.get('nosearchresults')}',
			singleSelect: true,border:false,toolbar:'#toolbar_choosepatient'">
        <thead>
            <tr>
            	<th data-options="field:'ck',checkbox:true"></th>
            	<th data-options="field:'patientid',width:80">编号</th>
            	<th data-options="field:'studyid',width:130">检查编号</th>
            	<th data-options="field:'patientname',width:80">姓名</th>
                <th data-options="field:'sexdisplay',width:50">性别</th>
                <th data-options="field:'birthdate',width:100">出生日期</th>
                <th data-options="field:'telephone',width:80">电话</th>
                <th data-options="field:'admissionid',width:80">入院编号</th>
		    	<th data-options="field:'psource',width:80">患者来源</th>
		    	<th data-options="field:'regdatetime',width:150">登记日期</th>
		    	<th data-options="field:'studyitems',width:150">检查项目</th>
		    	<th data-options="field:'adm_date',width:150">入院日期</th>
		        <th data-options="field:'cardno',width:100">卡号</th>
		        <th data-options="field:'outno',width:100">门诊号</th>
		        <th data-options="field:'inno',width:100">住院号</th>
		        <!-- <th data-options="field:'subjective',width:80">患者主诉</th>
		    	<th data-options="field:'admittingdiagnosis',width:80">入院诊断</th> -->
            </tr>
        </thead>
    </table>
    
    <div id="toolbar_choosepatient">
		<input class="easyui-textbox" id="patientid1_split" label="患者编号：" labelAlign="right" style="width:200px;">
		<input class="easyui-textbox" id="studyid1_split" label="检查编号：" labelAlign="right" style="width:230px;">
		<input class="easyui-textbox" id="patientname1_split" label="患者姓名：" labelAlign="right" style="width:200px;">
		<a class="easyui-linkbutton" style="width:80px;" onclick="findPat_split()">查找</a>
	</div>
</body>
</html>