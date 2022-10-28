<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<c:set var="input_width" value="280"/>
<c:set var="label_width" value="50"/>
<title>Insert title here</title>
</head>
<body>
	  <div style="width:100%;height:100%">
		<table id="reportOrderLinesDg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_formInfoDg',border:false,
				url:'${ctx}/research/searchJoinGroupApplyItems?report_order_id=${report_order_id}',autoRowHeight:true,scrollbarSize:0,
				loadMsg:'加载中...',emptyMsg:'没有查找到数据...'">
			<thead>
				<tr>
					<th data-options="field:'template_name',width:'100',align:'center',fixed: false" >模板名称</th>
					<th data-options="field:'patientname',width:'100',align:'center',fixed: false" >姓名</th>
					<th data-options="field:'patientid',width:'100',align:'center',fixed: false" >病人编号</th>
				    <th data-options="field:'sex',width:'50',align:'center',fixed: false" >性别</th>
				    <th data-options="field:'modality_type',width:'100',align:'center',fixed: false" >设备类型</th>
				    <th data-options="field:'studyitems',width:'120',align:'center',fixed: false" >检查项目</th>
				    <th data-options="field:'auditphysician_name',width:'80',align:'center'">审核医生</th>
				    <th data-options="field:'reporttime',width:'85',align:'center'">审核时间</th>
				    <th data-options="field:'reportphysician_name',width:'80',align:'center'">报告医生</th>
				    <th data-options="field:'reporttime',width:'85',align:'center'">报告时间</th>
				    <th data-options="field:'regdatetime',width:'85',align:'center'">登记时间</th>
				    <th data-options="field:'audittime',width:'85',align:'center'">检查时间</th>
					<th data-options="field:'ck',width:60,align:'center',formatter:joingroupItems_formatter" >操作</th>
				</tr>
			</thead>
		</table>
		<div id="toolbar_formInfoDg" style="padding:2px 2px;text-align:right;">
			<input id="templateName" class="easyui-combobox" data-options="valueField:'id',textField:'name',url:'${ctx}/research/searchSrtemplate',prompt:'请选择表单名称'" style="width:250px;">
			<a class="easyui-linkbutton easyui-tooltip" title="查询表单"  onClick="searchJoinGroupApplyItems();">查询</a>	
		</div>
		<input id="report_order_id" name="report_order_id" type="hidden" value='${report_order_id}'/>
   	</div>
   	<script type="text/javascript">
   	 function searchJoinGroupApplyItems(value){
   		 var report_order_id=$("#report_order_id").val();
   		 var templateId=$("#templateName").searchbox("getValue");
   		 var r = /^\+?[1-9][0-9]*$/;
   		 if(templateId&&!r.test(templateId)){
   			_message("请选择正确的模板！");
   			return;
   		 }
   		getJSON(window.localStorage.ctx+"/research/searchJoinGroupApplyItems",{templateId:templateId,report_order_id:report_order_id},function(data){
		 	$("#reportOrderLinesDg").datagrid('loadData',data);
	 	});
   	 }
    </script>
</body>
</html>