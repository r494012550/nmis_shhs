<!-- 保存查询条件 -->

<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title></title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'west',collapsible:false" style="width:120px;border-top:none">
		<div id="modality_list" class="easyui-datalist" title="设备列表"
			data-options="singleSelect:true,fit:true,border:false,onClickRow:handleClickRow,
			url:'${ctx}/examine/getModality?modalityType=${modalityType}&modalityId=${modalityId}',
			autoRowHeight:true,loadMsg:'加载中...',emptyMsg:'没有查找到设备...',
			groupField:'group',textField:'modality_name'">
		
		</div>
		<form id="receivewaitinglist_form_exam" method="POST">
		    	<input type="hidden" name="apporderStatus" value="${registered}"/>
		    	<input type="hidden" name="apporderStatus" value="${re_examine}"/>
		    	<input id="yesterdayfrom2" type="hidden" name="yesterdayfrom" value="23:40"/>
		 </form>
	</div>
	<div data-options="region:'center',border:false">
		<div style="height:100%">
			<table id="receivewaitinglistdg_exam" class="easyui-datagrid" sortName="studyid" sortOrder="asc"
				data-options="rownumbers: true,singleSelect:false,fit:true,border:false,
				loadMsg:'加载中...',emptyMsg:'没有查找到登记信息...',
				onLoadSuccess:function(){$(this).datagrid('selectAll')},
				rowStyler: function(index,row){
	                  if (row.patientsource == 'E'){
	                      return 'background-color:#F00;font-weight:bold;';
	                  }
		         }
				">
				<thead>
					<tr>
						<th data-options="field:'ck',checkbox:true"></th>
						<th data-options="field:'orderstatus',width:70,styler:columeStyler_orderstatus_exam" sortable="true">检查状态</th>
						<th data-options="field:'sequencenumber',width:80" sortable="true">序号</th>
						<th data-options="field:'studyid',width:80" sortable="true">检查号</th>
						<th data-options="field:'patientid',width:80" sortable="true">病人编号</th>
						<th data-options="field:'patientname',width:80" sortable="true">姓名</th>
						<th data-options="field:'age',width:50,formatter:age_formatter" sortable="true">年龄</th>
						<th data-options="field:'birthdate',width:80" sortable="true">出生日期</th>
						<th data-options="field:'studyitems',width:200,align:'center'" sortable="true">检查项目</th>
						<th data-options="field:'regdatetime',width:200,align:'center'" sortable="true">登记日期</th>
						<th data-options="field:'triagemodalityname',width:200,align:'center'" sortable="true">申请分诊设备</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
</div>
<script type="text/javascript">

</script>
</body>
</html>