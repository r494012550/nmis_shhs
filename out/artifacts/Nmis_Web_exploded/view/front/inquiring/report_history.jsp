<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Insert title here</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false" style="padding:0px;margin:auto;">
	<div data-options="region:'north'" style="height:160px;border-top:0px;border-right:0px;">
		<table class="easyui-datagrid" id="reportHistoryDg" 
				data-options="singleSelect:true, fit:true, fitColumns:true, scrollbarSize:0, loadMsg:'加载中...', emptyMsg:'无历史报告...',border:false,
					onDblClickRow:function(index,row){
						$(this).datagrid('selectRow',index);
                		dbClikReportHistoryDg(row);
					}">
			<thead>
			<tr>
				<th data-options="field:'studydatetime', width:100"> &nbsp; 检查日期</th>
				<th data-options="field:'patientid', width:100">患者编号</th>
				<th data-options="field:'patientname', width:100">姓名</th>
				<th data-options="field:'sexdisplay', width:100">性别</th>
				<!-- <th data-options="field:'name5', width:100">年龄</th> -->
				<th data-options="field:'birthdate', width:100">出生日期</th>
				<th data-options="field:'studyid', width:100">检查号</th>
				<th data-options="field:'checkresult_txt', width:400">诊断结果</th>
			</tr>
			</thead>
		</table>
	</div>
	
	<div data-options="region:'center'" style="width:100%;height:100%;border:0px;">
		<div class="history_content_div">检查所见</div>
		<div class="history_content_div">
			<input class="easyui-textbox" style="width:99%;height:80px;"
				id="checkdesc_txt" readonly="true"
				data-options="multiline:true"/>
		</div>
		
		<div class="history_content_div">检查诊断结果</div>
		<div class="history_content_div">
			<input class="easyui-textbox" style="width:99%;height:120px;"
				id="checkresult_txt" readonly="true"
				data-options="multiline:true"/>
		</div>
		
		<div class="history_content_div">简要病史</div>
		<div class="history_content_div">
			<input class="easyui-textbox" style="width:99%;height:80px;"
				id="briefhistory_history" readonly="true"
				data-options="multiline:true"/>
		</div>
		
		<div class="history_content_div">问诊其他信息</div>
		<div class="history_content_div">
			<input class="easyui-textbox" style="width:99%;height:100px;"
				id="other_information_history" readonly="true"
				data-options="multiline:true"/>
		</div>
	</div>
</div>
<script type="text/javascript">
</script>
</body>
</html>