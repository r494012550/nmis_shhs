<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div style="width: 100%; height: 100%;">
	<!-- 查询条件管理 -->
	<table id="dg_filter_worklist" class="easyui-datagrid" sortName="createtime"
		sortOrder="desc" border="0"
		data-options="rownumbers: true,showFooter: true,singleSelect:true,fit:true,
							remoteSort:false,singleSelect:false,
							loadMsg:'加载中...',emptyMsg:'没有查找到检查信息...'">
		<thead>
			<tr>
				<th data-options="field:'ck',checkbox:true"></th>
				<th data-options="field:'description',width:100" sortable="false">条件描述</th>
				<th data-options="field:'reportstatus_display',width:100" sortable="false">报告状态</th>
				<th data-options="field:'modality',width:100" sortable="false">设备类型</th>
				<th data-options="field:'studyid',width:100" sortable="false">检查号</th>
				<th data-options="field:'patientname',width:100" sortable="false">姓名</th>
				<th data-options="field:'patientid',width:100" sortable="false">编号</th>
				<th data-options="field:'inno',width:100" sortable="false">住院号</th>
				<th data-options="field:'outno',width:100" sortable="false">门诊号</th>
				<th data-options="field:'patient',width:100" sortable="false">病人来源</th>
				<th data-options="field:'institutionname',width:200" sortable="false">送检医院</th>
				<th data-options="field:'datetype_display',width:100" sortable="false">时间类型</th>
				<th data-options="field:'appdate_display',width:100" sortable="false">时间描述</th>
				<th data-options="field:'datefrom',width:100" sortable="false">检索起始时间</th>
				<th data-options="field:'dateto',width:100" sortable="false">检索截止时间</th>
				<th data-options="field:'createtime',width:160" sortable="false">创建时间</th>
				<td>
			</tr>
		</thead>
	</table>
</div>
<!--div style="margin-top: 10px;margin-left: 10px;width: 100%">


		<a class="easyui-linkbutton" onclick="removeParam();"
		style="width: 80px; margin-left:50px;height: 30px;  float:left;">删除选中</a>

	
		<a class="easyui-linkbutton" plain="true" onClick="$('#paramsManage').dialog('close')"
		style="text-align: center;margin-right:70px;background-color: #3684D9;padding:5px;width: 80px; height: 30px;  float:right;"> 关闭</a>
	
</div-->