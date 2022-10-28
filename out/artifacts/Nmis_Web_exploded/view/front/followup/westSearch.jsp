<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<c:set var="input_width" value="190"/>
<c:set var="input_height" value="28"/>
<c:set var="label_width" value="80"/>
<title>Insert title here</title>
<body>
	<div class="easyui-layout" data-options="fit:true">
		<div data-options="region:'north',border:false" style="height:230px;padding:5px;">
			<div style="margin-left:auto;margin-right:auto;width:100%;">
				<form id="searchform" method="post">
				<div class="followup_content_div">
					<select class="easyui-combobox" id="datetype" name="datetype" style="width: ${input_width*2+3}px;height:${input_height*2+5}px;" 
						label='${sessionScope.locale.get("wl.datetime")}:' labelWidth="${label_width-30}px" labelAlign="left" labelPosition="top"
						data-options="editable:false,panelHeight:'auto'">
		               	<option value="registertime">登记时间</option>
		                <option value="studytime">${sessionScope.locale.get("wl.studytime")}</option>
		                <option value="reporttime">报告时间</option>
		                <option value="audittime">审核时间</option>
		                <option value="followup_datetime">随访时间</option>
		            </select>
	            </div>
				<div class="followup_content_div">
					<input id="datefrom" name="datefrom" class="easyui-datebox" value="${today}" style="width: ${input_width}px;height:${input_height}px;" 
						data-options="editable:false">
				</div>
				<div class="followup_content_div">
					<input id="dateto" name="dateto" class="easyui-datebox" value="${lastday}" style="width: ${input_width}px;height:${input_height}px;" 
						data-options="editable:false">
				</div>
				<div class="followup_content_div">
					<input class="easyui-combotree" id="labelct" name="label" data-options="url:'${ctx}/followup/getAllLabel',
                      label:'标签:',labelPosition:'top',textField:'display',onlyLeafCheck:true,
                      labelPosition:'top',multiple:true,
		                onBeforeCheck:function(node, checked){
		                	if(checked&&node.labelid==null){
		                		return false;
		                	}
		                }" style="width: ${input_width}px;height:${input_height*2}px;">
		        	<input type="hidden" id="followup_label" name="followup_label"/>
				</div>
				<div class="followup_content_div">
					<input class="easyui-combobox" id="reportdoctor" name="reportdoctor" data-options="url:'${ctx}/followup/findUserByRole',valueField:'id',textField:'name',editable:false,
                      label:'报告医生:',labelPosition:'top',labelPosition:'top'" style="width: ${input_width}px;height:${input_height*2}px;" value="${user_id}">
				</div>
				<div class="followup_content_div">
					<input type="text" id="quicksearch-input" name="quicksearch" class="easyui-searchbox" title="搜索内" style="width: ${input_width*2+3}px;height:${input_height}px;" 
			        	data-options="prompt:'请输入姓名、病人编号、检查号...',searcher:searchFollowup">
				</div>
				<div class="followup_content_div" style="width:99%;text-align:right;">
					<input class="easyui-checkbox" name="followup" value="followup" label="已随访：" labelAlign="right">
					<a class="easyui-linkbutton c2" onclick="clearSearch();" style="width:120px;">清空</a>
					<a class="easyui-linkbutton" onclick="searchFollowup();" style="width:120px;">${sessionScope.locale.get("wl.dosearch")}</a>
				</div>
				</form>
			</div>
		</div>
		<div data-options="region:'center',border:false">
			<table id="westsearchDg" class="easyui-datagrid"
				data-options="fit:true, border:false, singleSelect: true,remoteSort:false,
					loadMsg:'加载中...',emptyMsg:'没有查询结果...',
					onDblClickRow:function(index,row){
	                	$(this).datagrid('selectRow',index);
	                	dbClikdg_west(row);
	                }">
		        <thead>
		            <tr>
		            	<th data-options="field:'reportstatusdisplay',width:80,styler:columeStyler_reportstatus_fl" sortable="true">报告状态</th>
		            	<th data-options="field:'studydatetime',width:150" sortable="true">检查日期</th>
		                <th data-options="field:'patientname',width:80" sortable="true">姓名</th>
		                <th data-options="field:'studyitems',width:260" sortable="true">检查项目</th>
		                <th data-options="field:'patientid',width:80" sortable="true">病人编号</th>
		                <th data-options="field:'studyid',width:80" sortable="true">检查编号</th>
		                <th data-options="field:'psource',width:80" sortable="true">病人来源</th>
		                <th data-options="field:'sexdisplay',width:60" sortable="true">性别</th>
		                <th data-options="field:'age',width:60,formatter:age_formatter" sortable="true">年龄</th>
		                <th data-options="field:'birthdate',width:100" sortable="true">出生日期</th>
		            </tr>
		        </thead>
		    </table>
		</div>
	
	</div>
</body>
</html>