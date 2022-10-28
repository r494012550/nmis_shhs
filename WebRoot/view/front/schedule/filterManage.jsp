<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div style="width: 100%; height: 100%;">
	<!-- 查询条件管理 -->
	<table id="dg_filter_schedule" class="easyui-datagrid" sortName="createtime" sortOrder="desc" border="0"
		data-options="rownumbers: true,showFooter: true,fit:true,remoteSort:false,singleSelect:false,
		loadMsg:'加载中...',emptyMsg:'没有查找到预约信息...'">
		<thead>
			<tr>
				<th data-options="field:'ck',checkbox:true"></th>
				<th data-options="field:'description',width:100" sortable="false">条件描述</th>
				<th data-options="field:'orderstatus_display',width:100" sortable="false">检查状态</th>
				<th data-options="field:'source',width:100" sortable="false">病人来源</th>
				<th data-options="field:'modality',width:100" sortable="false">设备类型</th>
				<th data-options="field:'equip',width:100" sortable="false">设备名称</th>
				<th data-options="field:'studyid',width:100" sortable="false">检查号</th>
				<th data-options="field:'patientid',width:100" sortable="false">病人编号</th>
				<th data-options="field:'cardno',width:100" sortable="false">病人卡号</th>
				<th data-options="field:'patientname',width:100" sortable="false">姓名</th>
				<th data-options="field:'appdate_display',width:100" sortable="false">时间描述</th>
				<th data-options="field:'datefrom',width:100" sortable="false">检索起始时间</th>
				<th data-options="field:'dateto',width:100" sortable="false">检索截止时间</th>
				<th data-options="field:'createtime',width:160" sortable="false">创建时间</th>
				<td>
			</tr>
		</thead>
	</table>
</div>
