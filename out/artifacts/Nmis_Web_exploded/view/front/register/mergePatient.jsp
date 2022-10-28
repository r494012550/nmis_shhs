<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>登记</title>
</head>
<body>
<div style="padding:10px;">
	<div style="margin-bottom: 5px;">
		<table id="patientdg1_reg" class="easyui-datagrid" title="" style="width:630px;height:150px;"
			data-options="showFooter: true,
			loadMsg:'loading',
			toolbar:'#toolbar_merge1',
			singleSelect: true,
			fitColumns:true">
	        <thead>
	            <tr>
	            	<th data-options="field:'patientid',width:80">编号</th>
	            	<th data-options="field:'patientname',width:80">姓名</th>
	                <th data-options="field:'sexdisplay',width:50">性别</th>
	                <th data-options="field:'birthdate',width:70">出生日期</th>
	                <th data-options="field:'telephone',width:80">电话</th>
	            </tr>
	        </thead>
	    </table>
	    <div id="toolbar_merge1">
	    	患者编号：<input class="easyui-textbox" id="patientid1_reg" style="width:150px;">
			姓名：<input class="easyui-textbox" id="patientname1_reg" style="width:150px;">
			<a class="easyui-linkbutton" style="width:80px;height:28px" onclick="findSamePatient('1_reg')">查找</a>
	    </div>
	</div>
	<div style="margin:0 auto;width:630px;height:35px;">
		<a class="easyui-linkbutton" title="向下合并" onClick="mergePatient(true)"><i class="icon iconfont icon-arrow-o-d"></i></a>
		&nbsp
		<a class="easyui-linkbutton" title="向上合并" onClick="mergePatient(false)"><i class="icon iconfont icon-arrow-o-u"></i></a>	
	</div>
	<div>
		<table id="patientdg2_reg" class="easyui-datagrid" title="" style="width:630px;height:300px;"
			data-options="showFooter: true,
			loadMsg:'loading',
			toolbar:'#toolbar_merge2',
			singleSelect: true,
			fitColumns:true">
	        <thead>
	            <tr>
	            	<th data-options="field:'patientid',width:80">编号</th>
	            	<th data-options="field:'patientname',width:80">姓名</th>
	                <th data-options="field:'sexdisplay',width:50">性别</th>
	                <th data-options="field:'birthdate',width:70">出生日期</th>
	                <th data-options="field:'telephone',width:80">电话</th>
	            </tr>
	        </thead>
	    </table>
	    <div id="toolbar_merge2">
	    	患者编号：<input class="easyui-textbox" id="patientid2_reg" style="width:150px;">
			姓名：<input class="easyui-textbox" id="patientname2_reg" style="width:150px;">
			<a class="easyui-linkbutton" style="width:80px;height:28px" onclick="findSamePatient('2_reg')">查找</a>
	    </div>
	</div>
</div>
</body>
</html>