<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
	<div style="padding:10px;margin-left:auto;margin-right:auto;width:720px;height:500px;">
			<table id="examItem_Equip_dg" class="easyui-datagrid"
				data-options="rownumbers: true,singleSelect:false,fit:true,border:false,
				loadMsg:'加载中...',emptyMsg:'没有查找到登记信息...',
				url:'${ctx}/dic/getExamitemEquipInfo?type=${type}&modalityId=${modalityId}&deleted=0',
				onLoadSuccess:initExamItemdg">
				<thead>
					<tr>
						<th data-options="field:'ck',checkbox:true"></th>
						<th data-options="field:'item_name',width:160">项目名</th>
						<th data-options="field:'organ',width:60">部位</th>
						<th data-options="field:'suborgan',width:60">子部位</th>
						<th data-options="field:'price',width:30">价格</th>
						<th data-options="field:'report_alert_hour',width:40">报警时间（时）</th>
						<th data-options="field:'report_alert_minute',width:40">报警时间（分）</th>
						<th data-options="field:'duration',width:40">持续时间</th>					
						<th data-options="field:'fulldescription',width:200">具体描述</th>
					</tr>
				</thead>
			</table>
	</div>
	
	<input id="modalityId_set" name="modalityId" type="hidden" value="${modalityId}">
</body>
</html>