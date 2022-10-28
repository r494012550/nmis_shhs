<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<c:set var="input_width" value="300"/>
<c:set var="label_width" value="100"/>
<title>Insert title here</title>
</head>
<body>
	<input id="_project_id" name="project_id" type="hidden" value='${project.id}'/>
	<input id="selected_group_id" name="groupid" type="hidden" value=''/>
	<input id="selected_group_name" name="groupname" type="hidden" value=''/>
	<div id="project_tabs" class="easyui-tabs" data-options="plain:true,narrow:true,justified:false,fit:true,border:false,onClose:function(title,index){refreshGroupInfo_Charts();}">
		<div title="项目：${project.name}" style="padding:10px;">
			<div id="testGroupLayout" class="easyui-layout" data-options="fit:true,border:false">
				<div data-options="region:'north',split:true,collapsible:false,href:'${ctx}/research/toTestGroups?projectid=${project.id}',tools:'#testgroup_tools'" 
					title="实验组列表" style="height:280px;">
			    	<div id="testgroup_tools" style="padding:2px 5px;">
			              <a class="easyui-tooltip" title="刷新" onclick="searchTestGroup();">
			                  <i class="icon iconfont icon-shuaxin2" style="text-align: center;vertical-align:2px;"></i></a>
			    	</div>
				</div>
				<div data-options="region:'center'" title="实验组数据">
					<div id="group_data_layout" class="easyui-layout" data-options="fit:true,border:false">
						<div data-options="region:'west',border:false,href:'${ctx}/research/toGroupInfo?groupid=',footer:'#groupinfo_footer',onLoad:groupInfoOnload" style="width:200px;">
						</div>
						<shiro:hasPermission name="research_advanced_analysis">
						<div id="groupinfo_footer" style="padding:5px;">
					        <a href="#" class="easyui-linkbutton" data-options="plain:true" style="width:100%;height:20px;" onclick="goAdvAnalysis();">高级分析>></a>
					    </div>
					    </shiro:hasPermission>
						<div data-options="region:'center'" style="border-top-width: 0px;border-right-width: 0px;border-bottom-width: 0px;">
							<table id="form_datas_dg" class="easyui-datagrid"
								data-options="singleSelect:false,fit:true,autoRowHeight:true,fitColumns:true,toolbar:'#toolbar_form_info',border:false,
									scrollbarSize:0,loadMsg:'加载中...',emptyMsg:'没有查找到表单数据...',ctrlSelect:true,
									onRowContextMenu:function(e,index ,row){
							            e.preventDefault();
							            $(this).datagrid('selectRow',index);
							            $('#cmenu_form_datas').menu('show', {
							                left:e.pageX,
							                top:e.pageY
							            });
							        },
									view:groupview,
						            groupField:'form_name',
						            groupFormatter:groupdata_group_formatter">
								<thead>
									<tr>
										<th data-options="field:'cb',checkbox:true" ></th>
										<th data-options="field:'form_name',width:100">表单名称</th>
										<th data-options="field:'statusdisplay',width:50,align:'center',styler:columeStyler_formstatus">状态</th>
										<th data-options="field:'patientname',width:'100',align:'center',fixed: false" >病人姓名</th>
										<th data-options="field:'sexdisplay',width:'50',align:'center',fixed: false" >性别</th>
										<th data-options="field:'patientid',width:'100',align:'center',fixed: false" >病人编号</th>
										<th data-options="field:'age',width:'50',align:'center',fixed: false,formatter:age_formatter" >年龄</th>
										<th data-options="field:'birthdate',width:'80',align:'center',fixed: false" >出生日期</th>
										<th data-options="field:'studyid',width:'100',align:'center',fixed: false" >检查号</th>
										<th data-options="field:'study_datetime',width:'100',align:'center',fixed: false" >检查时间</th>
										<th data-options="field:'modality_type',width:'60',align:'center',fixed: false" >设备类型</th>
										<th data-options="field:'studyitems',width:'100',align:'center',fixed: false" >检查项目</th>
										<th data-options="field:'createtime',align:'center',width:100">创建时间</th>
										<th data-options="field:'creator_name',align:'center',width:50">创建人</th>
										<th data-options="field:'audittime',align:'center',width:100">审核时间</th>
										<th data-options="field:'auditor_name',align:'center',width:50">审核人</th>
										<th data-options="field:'operate',width:80,align:'center',formatter:form_data_formatter">${sessionScope.locale.get("admin.operation")}</th>
									</tr>
								</thead>
							</table>
							<div id="toolbar_form_info" style="padding:2px 2px;text-align:right;">
								<form id="groupdata_form" method="post">
								<input class="easyui-searchbox" id="filter_pname" data-options="searcher:searchGroupDatas" name="patientname" label="姓名：" labelWidth="50" labelAlign="right" style="width:150px">
								<input class="easyui-searchbox" id="filter_patientid" data-options="searcher:searchGroupDatas" name="patientid" label="病人编号：" labelWidth="80" labelAlign="right" style="width:180px">
								<!-- <select  class="easyui-combobox" id="filter_sex" name="sex" label="性别：" labelAlign="right" labelWidth="50" style="width:120px;" 
									data-options="editable:false,panelHeight:100,onChange:searchGroupDatas">
									<option value="">全部</option>
									<option value="M">男</option>
									<option value="F">女</option>
								</select> -->
								|
								<a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'g_sex',plain:true,selected:true" onclick="$('#filter_sex').val('');searchGroupDatas();">全部</a>
								<a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'g_sex',plain:true" onclick="$('#filter_sex').val('M');searchGroupDatas();">男</a>
        						<a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'g_sex',plain:true" onclick="$('#filter_sex').val('F');searchGroupDatas();">女</a>
        						<input id="filter_sex" name="sex" type="hidden" value=''/>|
								<a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'g_status',plain:true,selected:true" onclick="$('#filter_status').val('');searchGroupDatas();">全部</a>
								<a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'g_status',plain:true" onclick="$('#filter_status').val('22');searchGroupDatas();">导入</a>
        						<a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'g_status',plain:true" onclick="$('#filter_status').val('23');searchGroupDatas();">提交</a>
        						<a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'g_status',plain:true" onclick="$('#filter_status').val('31');searchGroupDatas();">审核</a>
        						<input id="filter_status" name="status" type="hidden" value=''/>
								<!-- <select  class="easyui-combobox" id="filter_status" name="status" label="状态：" labelAlign="right" labelWidth="50" style="width:120px;" 
									data-options="editable:false,panelHeight:100,onChange:searchGroupDatas">
									<option value="">全部</option>
									<option value="M">男</option>
									<option value="F">女</option>
								</select> -->
								
					    		<shiro:hasPermission name="input_form_data">
					     		    <a class="easyui-menubutton" id="select_inspect_menubtn" style="width:90px;" data-options="disabled:true,plain:false,menu:'#select_inspect_items'">选择检查</a>
									<div id="select_inspect_items" class="easyui-menu" data-options="onClick:function(item){selectStudyDg(item.value,item.text);}" style="width:200px;">
		
									</div>
						     		<!-- <a class="easyui-linkbutton easyui-tooltip" title="填写模板" data-options="disabled:true" onClick="openWriteTemplate();">录入</a> -->
					     			<a class="easyui-menubutton" id="entry_form_menubtn" style="width:70px;" data-options="disabled:true,plain:false,menu:'#entry_form_menu_items'">录入</a>
										<div id="entry_form_menu_items" class="easyui-menu" data-options="onClick:function(item){entryForm(null,item.value,null,2);}" style="width:200px;">
		
									</div>
					     		</shiro:hasPermission>
					     		<%-- <shiro:hasPermission name="select_report">
					     			<a class="easyui-linkbutton easyui-tooltip" id="select_report_lb" title="选择报告" data-options="disabled:true" style="width:80px;" onClick="addSreport2Group();">选择报告</a>
					     		</shiro:hasPermission> --%>
					     		<shiro:hasPermission name="export_testgroup_data_excel">
					     		<a class="easyui-menubutton" id="export_excel_menubtn" title="导出" data-options="disabled:true,plain:false,menu:'#export_data_menu_items'" style="width:70px;">导出</a>
					     		<div id="export_data_menu_items" class="easyui-menu" data-options="" style="width:120px;">
									<div onClick="exportFormData('data');">数据</div>
									<div onClick="exportFormData('temp');">Excel模板</div>
								</div>
								</shiro:hasPermission>
								<shiro:hasPermission name="import_form_data_excel">
					     		<a class="easyui-linkbutton easyui-tooltip" id="upload_excel_lb" title="上传" data-options="disabled:true" style="width:70px;" onClick="$('#upload_excel_dlg').dialog('open');">导入</a>
					     		</shiro:hasPermission>
					     		<a class="easyui-menubutton" id="viewtype_menubtn" style="width:80px;" data-options="menu:'#change_view_menu_items'" selectitem="form">表单</a>
								<div id="change_view_menu_items" class="easyui-menu" data-options="" style="width:100px;">
									<div onClick="changeView('patient','患者');">患者</div>
									<div onClick="changeView('form','表单');">表单</div>
								</div>
								</form>
							</div>
							<div id="cmenu_form_datas" class="easyui-menu">
								<shiro:hasPermission name="audit_form_data_batch">
						    	<div data-options="name:'auditFormDataBatch'" onClick="auditFormDataBatch();">批量审核</div>
						    	</shiro:hasPermission>
						    	<div class="menu-sep"></div>
						    	<shiro:hasPermission name="delete_form_data_batch">
						    	<div data-options="name:'deleteFormDataBatch'" onClick="deleteFormDataBatch();">批量删除</div>
						    	</shiro:hasPermission>
						    </div>
						</div>
					</div>
			     </div>
			</div>
		</div>
	</div>
	<div id="upload_excel_dlg" class="easyui-dialog" title="导入" style="width:450px;height:300px;padding:10px"
            data-options="modal:false,closed:true,resizable:true,border:'thin',
                buttons: [{
                    text:'导入',
                    width : 80,
                    handler:function(){
                        uploadFormData();
                    }
                },{
                    text:'取消',
                    width : 80,
                    handler:function(){
                    	$('#formdata_excel').filebox('reset');
                    	$('#import_error_message').textbox('setValue','');
                        $('#upload_excel_dlg').dialog('close');
                    }
                }]
            ">
    	<form id="uploadexcelfm" method="post" enctype="multipart/form-data">
        	<input class="easyui-filebox" id="formdata_excel" name="excel_file" label="Excel文件:" labelPosition="top" data-options="prompt:'选择文件...',buttonText: '选择文件...',accept:'.xlsx'" style="width:100%">
        </form>
        <input class="easyui-textbox" id="import_error_message" label="错误信息:" labelPosition="top" multiline="true" style="width:100%;height:140px">
        
    </div>
	<div id="select_inspect_dialog"></div>
	<shiro:hasPermission name="delete_form_data">
	<input id="delete_form_data_flag" type="hidden" value="1">
	</shiro:hasPermission>
</body>
</html>