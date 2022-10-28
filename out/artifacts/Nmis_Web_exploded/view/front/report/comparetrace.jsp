<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title></title>
</head>
<body>
<div class="easyui-layout" id="reportCompare_layout" style="width:100%;height:100%;">
	<div data-options="region:'west',collapsible:false,split:false" title="报告记录" style="width:300px;">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'north',border:false" style="height:30px;padding:1px 5px 1px 210px;">
				<!-- <a href="#" class="easyui-linkbutton" id="initial_report"  onClick="initial_report();" disabled="true" style="width:80px;height:28px;margin-left:1px;">初始报告</a>
				<a href="#" class="easyui-linkbutton" id="reference_report" onClick="reference_report();" disabled="true" style="width:80px;height:28px;margin-left:1px;">参照报告</a> -->
				<a href="#" class="easyui-linkbutton" id="report_comparison" onClick="report_comparison();" disabled="true" style="width:80px;height:28px;margin-left:1px;">报告对比</a>
			</div>
			<div data-options="region:'center',border:false">
				<table id="reporttracedg_" class="easyui-datagrid" data-options="singleSelect:false,fit:true,footer:'#reporttracedg_ft',border:false,
				loadMsg:'加载中...',emptyMsg:'没有查找到报告信息...',onClickRow:function(index,row){clickReporttraceDg(index,row);},onSelect:selectReporttraceDg,onUnselect:selectReporttraceDg">
					<thead>
						<tr>
						<th data-options="field:'operatorname',width:70">操作员</th>
						<th data-options="field:'reportstatusdisplay',width:70">报告状态</th>
						<th data-options="field:'modifytime',width:150">保存时间</th>
						</tr>
					</thead>
				</table>
				<div id="reporttracedg_ft" style="padding:2px 5px;border:0px;">
					提示：请选择两个报告进行对比
				</div>
				<!-- 
				<input id="initial_report_index" type="hidden">
				<input id="reference_report_index" type="hidden"> -->
			</div>
			<!-- <div data-options="region:'south',border:false" style="height:60px">
				<div>
					<input class="easyui-textbox" id="initial_report_txt" label="初始：" labelWidth="60px" labelPosition="left" editable="false" style="width:290px;height:28px;">
					<input id="initial_report_index" type="hidden">
				</div>
				
				<div>
					<input class="easyui-textbox" id="reference_report_txt" label="参照：" labelWidth="60px" labelPosition="left" editable="false" style="width:290px;height:28px;">
					<input id="reference_report_index" type="hidden">
				</div>
			</div> -->
		</div>
	</div><!-- west -->
	
	
	<div data-options="region:'center',title:'报告内容'" style="width:500px;padding:10px">
		<div>
			<input class="easyui-textbox" id="studymethod_compare1" label="检查方法：" labelPosition="left" editable="false" style="width:478px;height:28px;">
		</div>
		<div style="margin-top: 5px;">
			【所见】
		</div>
		<div style="margin-top: 5px;">
			<div id="mypanel_compare1" class="easyui-panel"  style="width:100%;height:270px;padding:10px;"></div>
		</div>
		<div style="margin-top: 5px;">
			【诊断结果】
		</div>
		<div style="margin-top: 5px;">
			<div id="mypanel_compare2" class="easyui-panel"  style="width:100%;height:140px;padding:10px;"></div>
		</div>
		<div style="margin-top: 5px;">
			<div style="width:180px;float:left;">
			<input class="easyui-textbox" id="reportphysician_name_compare1" label="报告医生" labelWidth="70px" labelPosition="left" editable="false" style="width:180px;height:28px;">
			</div>
			<div style="width:220px;float:right;">
			<input class="easyui-textbox" id="reporttime_compare1" label="报告时间" labelWidth="70px" labelPosition="left" editable="false" style="width:220px;height:28px;">
			</div>
		</div>
		<div>
			<div style="width:180px;float:left;margin-top: 5px;">
			<input class="easyui-textbox" id="auditphysician_name_compare1" label="审核医生" labelWidth="70px" labelPosition="left" editable="false" style="width:180px;height:28px;">
			</div>
			<div style="width:220px;float:right;margin-top: 5px;">
			<input class="easyui-textbox" id="audittime_compare1" label="审核时间" labelWidth="70px" labelPosition="left" editable="false" style="width:220px;height:28px;">
			</div>
		</div>
	</div>
	<div  data-options="region:'east',title:'报告内容',collapsible:false" style="width:500px;padding:10px">
		<div>
			<input class="easyui-textbox" id="studymethod_compare2" label="检查方法：" labelWidth="80px" labelPosition="left" editable="false" style="width:478px;height:28px;">
		</div>
		<div style="margin-top: 5px;">
			【所见】
		</div>
		<div style="margin-top: 5px;">
			<div id="mypanel_compare3" class="easyui-panel"  style="width:100%;height:270px;padding:10px;"></div>
		</div>
		<div style="margin-top: 5px;">
			【诊断结果】
		</div>
		<div style="margin-top: 5px;">
			<div id="mypanel_compare4" class="easyui-panel"  style="width:100%;height:140px;padding:10px;"></div>
		</div>
		<div style="margin-top: 5px;">
			<div style="width:180px;float:left;">
				<input class="easyui-textbox" id="reportphysician_name_compare2" label="报告医生" labelWidth="70px" labelPosition="left" editable="false" style="width:180px;height:28px;">
			</div>
			<div style="width:220px;float:right;">
				<input class="easyui-textbox" id="reporttime_compare2" label="报告时间" labelWidth="70px" labelPosition="left" editable="false" style="width:220px;height:28px;">
			</div>
		</div>
		<div>
			<div style="width:180px;float:left;margin-top: 5px;">
				<input class="easyui-textbox" id="auditphysician_name_compare2" label="审核医生" labelWidth="70px" labelPosition="left" editable="false" style="width:180px;height:28px;">
			</div>
			<div style="width:220px;float:right;margin-top: 5px;">
				<input class="easyui-textbox" id="audittime_compare2" label="审核时间" labelWidth="70px" labelPosition="left" editable="false" style="width:220px;height:28px;">
			</div>
		</div>
	</div>
	<input id="reportid_compare" type="hidden" value="${reportid}">
</div>
</body>
</html>