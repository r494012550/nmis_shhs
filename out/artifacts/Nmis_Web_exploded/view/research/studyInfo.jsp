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
<div class="easyui-tabs" id="select_study_tabs" style="width:100%;height:100%;" data-options="plain:true,narrow:true,justified:true,border:false">
	<div title="报告系统" style="padding:10px;">
		<div class="easyui-layout" data-options="fit:true,border:false" style="width:100%;height:100%;">
			<div data-options="region:'west',title:'查询条件',collapsible:false,split:false" style="width:30%;padding:2px 5px;">
				<form id="searchForm_inspect" method="post">
		            <div style="width: 100%;margin-top: 2px;" class="mylabel">
						 <div style="width:120px;float:left;">
							<!-- <input id="modality" name="modality" class="easyui-combobox" label="设备类型：" labelPosition="top" style="width:100px;" data-options="valueField:'code',textField:'name_zh',url:'/worklist/getCode?type=0004',editable:false"> --> 
							<input class="easyui-combobox" name="modality" id="modality" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.modality")}:" labelPosition="top" 
								data-options="valueField:'code',textField:'name_zh',multiple:true,editable:false,panelHeight:'120px',
								url:'${ctx}/syscode/getCode?type=0004'"/>
						</div>
						<div style="width:120px;float:right;">
							<select class="easyui-combobox" name="reportstatus" id="reportStatus" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.reportstatus")}:" labelPosition="top"
							    data-options="valueField:'code',textField:'${sessionScope.syscode_lan}',editable:false,panelHeight:'120px',
								url:'${ctx}/syscode/getCode?type=0007&addempty=true'">
				            </select>
			            </div>
		            </div>
			         <div style="width: 100%;margin-top: 2px;" class="mylabel">
						<div style="width:120px;float:left;">
							<input class="easyui-combobox" name="appdeptcode" id="appdeptcode_wl" style="width:119px;height:50px;" label="申请科室:" labelPosition="top" 
								data-options="valueField:'code',textField:'name_zh',multiple:true,editable:false,panelHeight:'120px'"/>
						</div>
						<div style="width:120px;float:right;">
							<select class="easyui-combobox" name="orderstatus" id="orderstatus_wl" style="width:119px;height:50px;" label="检查状态:" labelPosition="top"
							    data-options="valueField:'code',textField:'${sessionScope.syscode_lan}',editable:false,panelHeight:'120px',
								url:'${ctx}/syscode/getCode?type=0005&addempty=true'">
				            </select>
			            </div>
		            </div>
			         
			         <div style="width:100%;margin-top:2px;" class="mylabel">
						<select class="easyui-combobox" name="datetype" id="datetype" style="width:100%;height:50px;" label="${sessionScope.locale.get("wl.datetime")}:" labelPosition="top" 
							data-options="editable:false,panelHeight:'auto'">
			               	<option value="registertime">${sessionScope.locale.get("wl.registertime")}</option>
			                <option value="studytime">${sessionScope.locale.get("wl.studytime")}</option>
			                <option value="reporttime">报告时间</option>
			                <option value="audittime">审核时间</option>
			                <%-- <option value="appointmenttime">${sessionScope.locale.get("wl.appointmenttime")}</option> --%>
			            </select>
		            </div>
					<div class="easyui-panel" style="width:100%;margin-top:2px;padding:1px;">
						<a id="today" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
							onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('T');searchStudyInfo();">${sessionScope.locale.get("wl.today")}</a>
				        <a  id="yesterday" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
				        	onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('Y');searchStudyInfo();">${sessionScope.locale.get("wl.yesterday")}</a>
				        <a id="threeday" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',selected:true,plain:true" 
	        				onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('TD');searchStudyInfo();">近三天</a>
					</div>
					
					<div class="easyui-panel" style="width:100%;margin-top:2px;padding:1px;">
						<a id="fiveday" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
							onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('FD');searchStudyInfo();">近五天</a>
				        <a  id="week" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
				        	onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('W');searchStudyInfo();">近一周</a>
				        <a id="month" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
				        	onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('TM');searchStudyInfo();">近三个月</a>
					</div>
					
					<div style="width:100%;margin-top:2px;">
						<div style="width:120px;float:left;">
							<input id="datefrom" name="datefrom" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
								onSelect:function(){
									$('#today').linkbutton({selected:false});
									$('#yesterday').linkbutton({selected:false});
									$('#threeday').linkbutton({selected:false});
									$('#fiveday').linkbutton({selected:false});
									$('#week').linkbutton({selected:false});
									$('#month').linkbutton({selected:false});
									$('#appdate').val('');
								}">
						</div>
						<div style="width:120px;float:right;">
							<input id="dateto" name="dateto" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
								onSelect:function(){
									$('#today').linkbutton({selected:false});
									$('#yesterday').linkbutton({selected:false});
									$('#threeday').linkbutton({selected:false});
									$('#fiveday').linkbutton({selected:false});
									$('#week').linkbutton({selected:false});
									$('#month').linkbutton({selected:false});
									$('#appdate').val('');
								}">
						</div>
					</div>
					
					<div style="width: 100%;margin-top: 2px;" class="mylabel">
						<div style="width:120px;float:left;">
							 <input id="studyid" name="studyid" class="easyui-textbox" style="width:119px;height:50px;" type="text" label="${sessionScope.locale.get("wl.studyid")}:" labelPosition="top">
						</div>
						
						<div style="width:120px;float:right;">
							<select class="easyui-combobox" name="patientsource" id="patientsource" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.patientsource")}:" 
								labelPosition="top" data-options="valueField:'code',textField:'${sessionScope.syscode_lan}',url:'${ctx}/syscode/getCode?type=0002&addempty=true',editable:false,
								panelHeight:'auto'">
				            </select>
			            </div>
		            </div>
		            
		            <div style="width: 100%;margin-top: 2px;" class="mylabel">
						<div style="width:120px;float:left;">
							<input id="patientid" name="patientid" class="easyui-textbox" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.patientid")}:" labelPosition="top">
						</div>
						
						<div style="width:120px;float:right;">
						    <input id="patientname" name="patientname" class="easyui-textbox" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.patientname")}:" labelPosition="top" >
						</div>
					</div>
					<div class="easyui-panel" style="width: 100%;margin-top: 2px;" data-options="border:false">
						<div style="margin-top: 5px;">
							<a class="easyui-linkbutton" onclick="searchStudyInfo();" style="width:100%;height:28px">${sessionScope.locale.get("wl.dosearch")}</a>
						</div>
			        	<div style="margin-top: 5px;">
			        		<a class="easyui-linkbutton c2" onclick="clearManage();" style="width:100%;height:28px">${sessionScope.locale.get("wl.clearcondition")}</a>
			        	</div>
					</div>
					<input id="appdate" type="hidden" name="appdate" value="T"/>
				</form>
		     </div>
		     <div data-options="region:'center',title:'查询结果'" style="">
				<table id="inspectInfoDg" class="easyui-datagrid" style="width:100%;"
					data-options="showFooter: true,singleSelect:true,fit:true,border:false,autoRowHeight:true,scrollbarSize:0,pagination:true,
						loadMsg:'加载中...',emptyMsg:'没有查找到数据...'">
					<thead>
						<tr>
							<th data-options="field:'patientname',width:'120',align:'center',fixed: false" >姓名</th>
						    <th data-options="field:'sex',width:'50',align:'center',fixed: false" >性别</th>
						    <th data-options="field:'patientid',width:'100',align:'center',fixed: false" >病人编号</th>
						    <th data-options="field:'studyid',width:'100',align:'center',fixed: false" >检查号</th>
						    <th data-options="field:'modality_type',width:'100',align:'center',fixed: false" >设备类型</th>
						    <th data-options="field:'studyitems',width:'200',align:'center',fixed: false" >检查项目</th>
						</tr>
					</thead>
				</table>
		     </div>
		</div>
	</div>
	<div title="影像系统" style="padding:10px;">
		<div class="easyui-layout" data-options="fit:true,border:false" style="width:100%;height:100%;">
			<div data-options="region:'west',title:'查询条件',collapsible:false,split:false" style="width:30%;padding:2px 5px;">
				<form id="searchForm_dicom" method="post">
		            <div style="width: 100%;margin-top: 2px;" class="mylabel">
						 <div style="width:120px;float:left;">
						 	<input class="easyui-combobox" name="modality" id="modality_dicom" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.modality")}:" labelPosition="top" 
								data-options="valueField:'code',textField:'name_zh',multiple:true,editable:false,panelHeight:'120px',
								url:'${ctx}/syscode/getCode?type=0004'"/>
						</div>
						<div style="width:120px;float:right;">
							<input id="studyid_dicom" name="studyid" class="easyui-textbox" style="width:119px;height:50px;" type="text" label="${sessionScope.locale.get("wl.studyid")}:" labelPosition="top">
			            </div>
		            </div>
			         
			         <div style="width:100%;margin-top:2px;" class="mylabel">
						<label style="width:100px;display: block;" class="textbox-label">检查时间：</label>
		            </div>
					<div class="easyui-panel" style="width:100%;margin-top:2px;padding:1px;">
						<a id="today_dicom" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g9',plain:true" 
							onclick="$('#datefrom_dicom').datebox('setValue','');$('#dateto_dicom').datebox('setValue','');$('#appdate_dicom').val('T');searchDicomInfo();">${sessionScope.locale.get("wl.today")}</a>
				        <a  id="yesterday_dicom" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g9',plain:true" 
				        	onclick="$('#datefrom_dicom').datebox('setValue','');$('#dateto_dicom').datebox('setValue','');$('#appdate_dicom').val('Y');searchDicomInfo();">${sessionScope.locale.get("wl.yesterday")}</a>
				        <a id="threeday_dicom" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g9',selected:true,plain:true" 
	        				onclick="$('#datefrom_dicom').datebox('setValue','');$('#dateto_dicom').datebox('setValue','');$('#appdate_dicom').val('TD');searchDicomInfo();">近三天</a>
					</div>
					
					<div class="easyui-panel" style="width:100%;margin-top:2px;padding:1px;">
						<a id="fiveday_dicom" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g9',plain:true" 
							onclick="$('#datefrom_dicom').datebox('setValue','');$('#dateto_dicom').datebox('setValue','');$('#appdate_dicom').val('FD');searchDicomInfo();">近五天</a>
				        <a  id="week_dicom" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g9',plain:true" 
				        	onclick="$('#datefrom_dicom').datebox('setValue','');$('#dateto_dicom').datebox('setValue','');$('#appdate_dicom').val('W');searchDicomInfo();">近一周</a>
				        <a id="month_dicom" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g9',plain:true" 
				        	onclick="$('#datefrom_dicom').datebox('setValue','');$('#dateto_dicom').datebox('setValue','');$('#appdate_dicom').val('TM');searchDicomInfo();">近三个月</a>
					</div>
					
					<div style="width:100%;margin-top:2px;">
						<div style="width:120px;float:left;">
							<input id="datefrom_dicom" name="datefrom" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
								onSelect:function(){
									$('#today').linkbutton({selected:false});
									$('#yesterday').linkbutton({selected:false});
									$('#threeday').linkbutton({selected:false});
									$('#fiveday').linkbutton({selected:false});
									$('#week').linkbutton({selected:false});
									$('#month').linkbutton({selected:false});
									$('#appdate').val('');
								}">
						</div>
						<div style="width:120px;float:right;">
							<input id="dateto_dicom" name="dateto" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
								onSelect:function(){
									$('#today').linkbutton({selected:false});
									$('#yesterday').linkbutton({selected:false});
									$('#threeday').linkbutton({selected:false});
									$('#fiveday').linkbutton({selected:false});
									$('#week').linkbutton({selected:false});
									$('#month').linkbutton({selected:false});
									$('#appdate').val('');
								}">
						</div>
					</div>
		            <div style="width: 100%;margin-top: 2px;" class="mylabel">
						<div style="width:120px;float:left;">
							<input id="patientid_dicom" name="patientid" class="easyui-textbox" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.patientid")}:" labelPosition="top">
						</div>
						
						<div style="width:120px;float:right;">
						    <input id="patientname_dicom" name="patientname" class="easyui-textbox" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.patientname")}:" labelPosition="top" >
						</div>
					</div>
					<div class="easyui-panel" style="width: 100%;margin-top: 2px;" data-options="border:false">
						<div style="margin-top: 5px;">
							<a class="easyui-linkbutton" onclick="searchDicomInfo();" style="width:100%;height:28px">${sessionScope.locale.get("wl.dosearch")}</a>
						</div>
			        	<div style="margin-top: 5px;">
			        		<a class="easyui-linkbutton c2" onclick="clearManageDicom();" style="width:100%;height:28px">${sessionScope.locale.get("wl.clearcondition")}</a>
			        	</div>
					</div>
					<input id="appdate_dicom" type="hidden" name="appdate" value="T"/>
				</form>
		     </div>
		     <div data-options="region:'center',title:'查询结果'" style="">
				<table id="dicomInfoDg" class="easyui-datagrid" style="width:100%;"
					data-options="showFooter: true,singleSelect:true,fit:true,border:false,autoRowHeight:true,scrollbarSize:0,
						loadMsg:'加载中...',emptyMsg:'没有查找到数据...'">
					<thead>
						<tr>
							<th data-options="field:'patientname',width:'120',align:'center',fixed: false" >姓名</th>
						    <th data-options="field:'sex',width:'50',align:'center',fixed: false" >性别</th>
						    <th data-options="field:'patientid',width:'100',align:'center',fixed: false" >病人编号</th>
						    <th data-options="field:'studyid',width:'100',align:'center',fixed: false" >检查号</th>
						    <th data-options="field:'modality_type',width:'100',align:'center',fixed: false" >设备类型</th>
						    <th data-options="field:'study_datetime',width:'200',align:'center',fixed: false" >检查时间</th>
						    <th data-options="field:'study_desc',width:'200',align:'center',fixed: false" >检查描述</th>
						</tr>
					</thead>
				</table>
		     </div>
		</div>
	</div>
</div>
<script type="text/javascript">
   	var default_page=1;
   	var default_pageSize=20;
   	function initPagination(){
	   	var p = $('#inspectInfoDg').datagrid('getPager');
	   	$(p).pagination({ 
	        pageSize: default_pageSize,//每页显示的记录条数，默认为20 
	        pageNumber:default_page,
	        pageList: [10,20,30,50],//可以设置每页记录条数的列表 
	        beforePageText: $.i18n.prop('wl.beforepagetext'),//页数文本框前显示的汉字 
	        afterPageText: $.i18n.prop('wl.afterpagetext'),
	        displayMsg: $.i18n.prop('wl.displaymsg'),
	        onSelectPage:function(pageNumber, pageSize){
	        	$("#page").val(pageNumber);
	        	$("#pageSize").val(pageSize);
	        	searchStudyInfo(pageNumber, pageSize);
	    	}
	    });
	   	$('#inspectInfoDg').datagrid('getPager').attr('init_select',true);
   	}
   	
   	function searchStudyInfo(pageNumber, pageSize){
   		if(!$('#inspectInfoDg').datagrid('getPager').attr('init_select')){
   			initPagination($('#sreport_dg'));
   		}
   		
   		if(!pageNumber){
   			pageNumber=1;
   		}
   		if(!pageSize){
   			pageSize=20;
   		}
   		$('#progress_dlg').dialog('open');
   		$('#searchForm_inspect').form('submit', {
   		    url: window.localStorage.ctx+"/research/searchStudyInfo",
   		    onSubmit: function(param){
   		        param.page=pageNumber,
   		        param.rows=pageSize
   		    },
   		    success:function(data){
   		    	$('#progress_dlg').dialog('close');
   				$("#inspectInfoDg").datagrid("loadData", validationData(data));
   		    }
   		});
   	}

   	function clearManage(){
   		$('#searchForm_inspect').form('reset');//reset 会清空组件内容，但是不会清空hidden 组件内容，需要再清空一下hidden组件
   		$('#appdate').val('');
   		$('#datetype').combobox('setValue',$('#datetype').combobox('getData')[0].value);

   		$('#today').linkbutton({selected:false});
   		$('#yesterday').linkbutton({selected:false});
   		$('#threeday').linkbutton({selected:false});
   		$('#fiveday').linkbutton({selected:false});
   		$('#week').linkbutton({selected:false});
   		$('#month').linkbutton({selected:false});
   	}
   	
   	function searchDicomInfo(pageNumber, pageSize){

   		$('#progress_dlg').dialog('open');
   		$('#searchForm_dicom').form('submit', {
   		    url: window.localStorage.ctx+"/research/findDicomInfo",
   		    onSubmit: function(param){

   		    },
   		    success:function(data){
   		    	$('#progress_dlg').dialog('close');
   				$("#dicomInfoDg").datagrid("loadData", validationData(data));
   		    }
   		});
   	}
   	function clearManageDicom(){
   		$('#searchForm_dicom').form('reset');//reset 会清空组件内容，但是不会清空hidden 组件内容，需要再清空一下hidden组件
   		$('#appdate_dicom').val('');
   		$('#today_dicom').linkbutton({selected:false});
   		$('#yesterday_dicom').linkbutton({selected:false});
   		$('#threeday_dicom').linkbutton({selected:false});
   		$('#fiveday_dicom').linkbutton({selected:false});
   		$('#week_dicom').linkbutton({selected:false});
   		$('#month_dicom').linkbutton({selected:false});
   	}
</script>
</body>
</html>