<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
<c:set var="input_width" value="300"/>
<c:set var="label_width" value="100"/>
</head>
<body>
	<div class="easyui-layout" fit="true" style="width:100%;height:100%;">
		<div data-options="region:'north',title:'查询条件',split:false,collapsible:false" style="height:145px;padding:5px;">
			<form id="search_sreport_form" method="post">
			<div style="margin-bottom:5px;width:360px;float:left;">
				<input id="templateName" name="templateName" class="easyui-combobox" data-options="valueField:'name',textField:'name',url:'${ctx}/research/searchSrtemplate'" 
					style="width:350px;"label="模板名称:" labelWidth="80px">
			</div>
			<div style="margin-bottom:5px;width:360px;float:left;">
				<select id="dateType" name="dateType" class="easyui-combobox" style="width:100px;" value="reportTime">
				    <option value="audittime">审核时间</option>
				    <option value="reporttime">报告时间</option>
				    <option value="registertime">登记时间</option>
				</select>
				<input id="strTime" name="strTime" class="easyui-datebox" style="width:120px;">
				<input id="endTime" name="endTime" class="easyui-datebox" style="width:120px;">
			</div>
			<div style="margin-bottom:5px;width:360px;float:left;">
				<input id="modality_type" name="modality_type" class="easyui-combobox" data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0004',
					editable:false,onLoadSuccess:function(data){if(data){$(this).combobox('select', data[0].code)}},onChange:loadStudyItem" 
					style="width:350px;"label="设备类型:" labelWidth="80px">
			</div>
			<div style="margin-bottom:5px;width:360px;float:left;">
				<select id="peopleType" name="doctorType" class="easyui-combobox" style="width:100px;" value="reportDoctor">
				    <option value="reportDoctor">报告医生</option>
				    <option value="examineDoctor">审核医生</option>
				</select>	
				<input id="people" name="doctorName" class="easyui-textbox" style="width:245px;">
			</div>
			<div style="margin-bottom:0px;width:360px;float:left;">
				<input id="examitemName" name="examitemName" class="easyui-combobox" data-options="valueField:'id',textField:'item_name'" 
					style="width:350px;"label="检查项目:" labelWidth="80px">
			</div>
			</form>
			<div style="margin-bottom:0px;width:360px;float:left;">
				<a class="easyui-linkbutton easyui-tooltip" title="查询" style="width:100px" onClick="doSearchSreport();">查询</a>
				<a class="easyui-linkbutton c2 easyui-tooltip" title="清除" style="width:100px;" onClick="cleanSearchSreport_Condition();">清除</a>
			</div>
		</div>
		<div data-options="region:'center'" title="报告查询结果">
			<div class="easyui-layout" data-options="fit:true,border:false">
				<div data-options="region:'center',border:false">
					<table id="sreport_dg" class="easyui-datagrid" style=""
						data-options="singleSelect:false,pagination:true,fit:true,remoteSort:false,autoRowHeight:false,border:false,scrollbarSize:0,
						onDblClickRow:previewSreport,loadMsg:'加载中 ...',emptyMsg:'${sessionScope.locale.get('nosearchresults')}',footer:'#footer_div_for_sreport_dg'">
						<thead data-options="frozen:true">
				            <tr>
				                <th data-options="field:'ck',width:'20'" checkbox="true" ></th>
								<th data-options="field:'template_name',width:'100',align:'center',fixed: false" >模板名称</th>
								<th data-options="field:'patientname',width:'100',align:'center',fixed: false" >姓名</th>
				            </tr>
				        </thead>
						<thead>
							<tr>
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
							</tr>
						</thead>
					</table>
					<div id="footer_div_for_sreport_dg" style="padding:2px;border-right:0px;border-left:0px;text-align:center;">
				       	<a class="easyui-linkbutton easyui-tooltip" title="添加" style="width:80px" onClick="addToBeList();"><i class="icon iconfont icon-arrow-o-d"></i></a>
						<!-- <a class="easyui-linkbutton easyui-tooltip" title="左移" style="width:80px" onClick="leftShift();"><==</a> -->
				    </div>
				</div>
				<div data-options="region:'south',title:'已选择的报告',split:true,border:false,collapsible:false,tools:'#footer_div_for_selected_sreport_dg'" style="height:350px;">
					<table id="selected_sreport_dg" class="easyui-datagrid" data-options="scrollbarSize:0,border:false,fit:true,onDblClickRow:previewSreport">
						<thead data-options="frozen:true">
				            <tr>
				                <th data-options="field:'ck',width:'20'" checkbox="true" ></th>
								<th data-options="field:'template_name',width:'100',align:'center',fixed: false" >模板名称</th>
								<th data-options="field:'patientname',width:'100',align:'center',fixed: false" >姓名</th>
				            </tr>
				        </thead>
						<thead>
							<tr>
								<th data-options="field:'patientid',width:'100',align:'center',fixed: false" >病人编号</th>
								<th data-options="field:'sex',width:'100',align:'center',fixed: false" >性别</th>
								<th data-options="field:'modality_type',width:'100',align:'center',fixed: false" >设备类型</th>
								<th data-options="field:'studyitems',width:'100',align:'center',fixed: false" >检查项目</th>
								<th data-options="field:'auditphysician_name',width:'100',align:'center'">审核医生</th>
								<th data-options="field:'reporttime',width:'85',align:'center'">审核时间</th>
								<th data-options="field:'reportphysician_name',width:'100',align:'center'">报告医生</th>
								<th data-options="field:'reporttime',width:'85',align:'center'">报告时间</th>
								<th data-options="field:'regdatetime',width:'85',align:'center'">登记时间</th>
								<th data-options="field:'audittime',width:'85',align:'center'">检查时间</th>
							</tr>
						</thead>
			    	</table>
			    	<div id="footer_div_for_selected_sreport_dg">
				       	<a class="easyui-tooltip" title="移除" onclick="removeFromToBeList();">
			                  <i class="icon iconfont icon-delete" style="text-align: center;vertical-align:2px;"></i></a>
				    </div>
				</div>
			</div>	
		</div>
		<div data-options="region:'south',border:false" style="text-align:right;padding:5px 0 0;">
			<a class="easyui-linkbutton" onclick="openSumbitDlg();" style="width:80px">申请</a>
			<a class="easyui-linkbutton" onclick="$('#common_right_window').dialog('close');" style="width:80px">关闭</a>
		</div>
	</div>
	<div id="reportPrescriptionDlg" class="easyui-dialog" style="width:300px;height:150px;" data-options="modal:true,closed:true,title:'选择时效',border : 'thin',
		buttons:[{
		    text:'提交',
		    width: 80,
		    handler:function(){applySreport2Group();}
		}]">
		<div style="padding-top:20px;">
			<input class="easyui-datebox" id="reportPrescriptionTime" name="reportPrescriptionTime" label="报告时效："  editable="false" labelWidth="${label_width-20}" labelAlign="right" 
				style="width:${input_width-30}px;">
		</div>
	</div>
</body>
</html>
