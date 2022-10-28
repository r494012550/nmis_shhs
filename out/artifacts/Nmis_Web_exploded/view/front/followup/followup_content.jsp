<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<c:set var="input_width" value="200"/>
<c:set var="input_height" value="25"/>
<c:set var="label_width" value="80"/>
<c:set var="input_width_long" value="400"/>
<title>Insert title here</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true" style="padding:0px;margin:auto;">
	<div data-options="region:'west',title:'查询', href:'${ctx}/followup/westSearch', onLoad:initWestsearch, hideCollapsedContent:false,headerCls:'panelHeaderCss_top'" style="width:400px;">
	</div>
	<div data-options="region:'center',border:false">
		<div class="easyui-layout" id="followup_la" data-options="fit:true" style="padding:0px;">
			<div data-options="region:'north',border:false" style="height：350px;">
				<%@ include file="/view/front/followup/patient_info.jsp" %>
			</div>
			<div data-options="region:'center',border:false" title="随访记录">
				<table id="followupDg" class="easyui-datagrid"
					data-options="fit:true,singleSelect: true,border:false,toolbar:'#followups_toolbar',
						loadMsg:'加载中...',emptyMsg:'没有查找到随访信息...',
						onDblClickRow:function(index,row){
		                	$(this).datagrid('selectRow',index);
		                	onClickRow_followupDg(row);
		                }">
			        <thead>
			            <tr>
			            	<th data-options="field:'followup_datetime',width:150" sortable="true">随访时间</th>
			                <th data-options="field:'followup_doctorname',width:100" sortable="true">随访人</th>
			                <th data-options="field:'followup_way_name',width:100" sortable="true">随访方式</th>
			                <th data-options="field:'followup_consistent_name',width:100" sortable="true">随访符合</th>
			                <th data-options="field:'followup_reason',width:300">随访原因</th>
			                <th data-options="field:'treatment',width:300">治疗情况</th>
			                <th data-options="field:'followup_result',width:500">随访结果</th>
			            </tr>
			        </thead>
				</table>
				<div id="followups_toolbar" style="text-align:right;">
					<a class="easyui-linkbutton followup_btn" data-options="disabled:true" style="width:80px;" onclick="addFollowup()">新建</a>
					<a class="easyui-linkbutton c5 followup_btn" data-options="disabled:true" style="width:80px;" onclick="deleteFollowup()">删除</a>
				</div>
			</div>
			<div data-options="region:'south',border:false,hideCollapsedContent:false,collapsed:true,collapsible:false,hideExpandTool:true" title="随访信息" style="height:425px;">
				<%@ include file="/view/front/followup/followup_info.jsp" %>
			</div>
		</div>
	</div>
</div>
</body>
</html>