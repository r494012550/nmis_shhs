/**
 * 
 */

function searchTestGroup(){
	$('#testGroupLayout').layout('panel', 'north').panel('refresh');
}

function refreshTestGroupInfo(){
	var groupid=$('#selected_group_id').val();
	getJSON(window.localStorage.ctx+"/research/getTestGroupsInfo",
		{
			groupid:groupid
		},function(data){
		 	console.log(data);
		if(data){
			$('#test_group_div_'+groupid+' .all_data_count').html(data.all_count);
			$('#test_group_div_'+groupid+' .sumbit_data_count').html(data.submit_count);
			$('#test_group_div_'+groupid+' .final_data_count').html(data.final_count);
		}
	 });
}

function selectTestGroup(groupid,groupname){
	$('._testgroup').removeClass('_select');
	$('#test_group_div_'+groupid).addClass('_select');
	
	$('#selected_group_id').val(groupid);
	$('#selected_group_name').val(groupname);
	
	$('#testGroupLayout').layout('panel','center').panel('setTitle',groupname+'--实验组数据');
	$("#select_report_lb").menubutton('enable');
	$("#select_inspect_menubtn").menubutton('enable');
	$("#export_excel_menubtn").menubutton('enable');
	$("#upload_excel_lb").linkbutton('enable');

	//清空menubutton的内容
	$('#entry_form_menu_items').find('.menu-item').each(function(index,obj){
		$('#entry_form_menu_items').menu('removeItem', obj);
	});
	//清空menubutton的内容
	$('#select_inspect_items').find('.menu-item').each(function(index,obj){
		$('#select_inspect_items').menu('removeItem', obj);
	});
	//重新加载menubutton内容
	getJSON(window.localStorage.ctx + "/research/findTestgroupFrom", {
		groupid : groupid
	}, function(data) {
		$("#entry_form_menubtn").menubutton('enable');
		$("#select_inspect_menubtn").menubutton('enable');
		$("#export_excel_menubtn").menubutton('enable');
		for (var i = 0; i < data.length; i++) {
			$('#entry_form_menu_items').menu('appendItem', {
			    text: data[i].name,
			    value:data[i].id
			});
			$('#select_inspect_items').menu('appendItem', {
			    text: data[i].name,
			    value:data[i].id
			});
		}
	});
	searchTestGroupDatas(groupid);
}


function searchTestGroupDatas(groupid,formid,type,patientid,refreshGroupInfo){
	if(groupid){
		console.log($('#viewtype_menubtn').menubutton('options'))
		var dgid='#form_datas_dg';
		if(patientid){
			dgid='#sub_form_datas_dg_'+patientid;
		}
		//判断是否患者视图
		if($('#viewtype_menubtn').attr('selectitem')=='patient'&&patientid==null){
			$(dgid).datagrid('reload'
				,window.localStorage.ctx + '/research/findGroupPatient?groupid='+groupid);
		} else{
			$(dgid).datagrid('reload'
				,window.localStorage.ctx + '/research/searchTestGroupDatas1?groupid='+groupid+'&patientid='+(patientid||'')+'&formid='+(formid||'')+'&type='+(type||''));
		}
				
		$('#group_data_layout').layout('panel','west').panel('refresh',window.localStorage.ctx + '/research/toGroupInfo?groupid='+groupid);
		if(refreshGroupInfo)refreshTestGroupInfo();
	} else{
		_message('实验组ID为空！');
	}
}

function refreshGroupInfo_Charts(){
	var groupid=$('#selected_group_id').val();
	$('#group_data_layout').layout('panel','west').panel('refresh',window.localStorage.ctx + '/research/toGroupInfo?groupid='+groupid);
}

function searchGroupDatas(){
	var groupid=$('#selected_group_id').val();
	if(!groupid){
		_message("请选择一个实验组！");
		return;
	}
//	$('#form_datas_dg').datagrid('load',{
//		groupid: groupid,
//		patientid: $('#filter_patientid').textbox('getValue'),
//		patientname: $('#filter_pname').textbox('getValue'),
//		sex: $('#filter_sex').combobox('getValue')
//	});
	
	$('#groupdata_form').form('submit', {
	    url:window.localStorage.ctx + '/research/searchTestGroupDatas1',
	    onSubmit: function(param){
	        param.groupid = groupid;
	    },
	    success:function(data){
			$('#form_datas_dg').datagrid('loadData',validationDataAll(data));
	    }
	});
	
	//$('#group_data_layout').layout('panel','west').panel('refresh',window.localStorage.ctx + '/research/toGroupInfo?groupid='+groupid);
}

function columeStyler_formstatus(value,row,index) {
	if(row.status==22){
		if(row.error_import){
			return 'background-color:#f3331f;';
		} else{
			return 'background-color:#FF8040;';
		}
	} else if (row.status==23) {
		return 'background-color:#C1D226;';
	} else if (row.status==31) {
		return 'background-color:#018040;';
	} else{
		return '';
	}
}

function groupdata_group_formatter(value, rows){
	return '表单名:'+value + '--共计:<a style="color:red">'+rows.length+'</a>条 ';
}

function form_data_formatter(value, row, index){
	return '<a class="easyui-linkbutton l-btn l-btn-small l-btn-plain" onclick=" editFormData('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-edit" style="color:#1a7bc9;font-size:16px;"></i></span></span></a>'+
		($('#delete_form_data_flag').val()=='1'?
			('<a class="easyui-linkbutton l-btn l-btn-small l-btn-plain" onclick="deleteFormData('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-delete" style="color:#b52b27;font-size:18px;"></i></span></span></a>'):'');
}

function editFormData(index){
	$('#form_datas_dg').datagrid('selectRow',index);
	var row=$('#form_datas_dg').datagrid('getSelected');
	var index=getTabIndexByDataid(row.id);
	if(index){
		$('#project_tabs').tabs('select',index);
	} else{
		entryForm(row.id,null);
	}
}

function form_data_formatter_sub(value, row, index){
	return '<a class="easyui-linkbutton l-btn l-btn-small l-btn-plain" onclick=" editFormDataSub('+index+',\''+row.patientid+'\');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-edit" style="color:#1a7bc9;font-size:16px;"></i></span></span></a>&nbsp;'+
		($('#delete_form_data_flag').val()=='1'?
			('<a class="easyui-linkbutton l-btn l-btn-small l-btn-plain" onclick="deleteFormDataSub('+index+',\''+row.patientid+'\');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-delete" style="color:#b52b27;font-size:18px;"></i></span></span></a>'):'');
}

function editFormDataSub(index,patientid){
	$('#sub_form_datas_dg_'+patientid).datagrid('selectRow',index);
	var row=$('#sub_form_datas_dg_'+patientid).datagrid('getSelected');
	var index=getTabIndexByDataid(row.id);
	if(index){
		$('#project_tabs').tabs('select',index);
	} else{
		entryForm(row.id,null);
	}
}

/**
 * 获取Tab的index
 * @param dataid
 * @returns
 */
function getTabIndexByDataid(dataid){
	var tabid="tab_"+dataid;
	var tabs=$('#project_tabs').tabs('tabs');
	for(var i=1;i<tabs.length;i++){
		if(tabs[i].panel('options').id==tabid){
			return i;
		}
	}
	return null;
}


function deleteFormData(index){
	$('#form_datas_dg').datagrid('selectRow',index);
	var row=$('#form_datas_dg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: '确认删除',
			border:'thin',
			msg: '确认删除选中的表单数据吗？',
			fn: function(r){
				if (r){
					 getJSON(window.localStorage.ctx+"/research/deleteFormData",{id:row.id},function(data){
						 	var json=validationData(data);
					    	if(json.code==0){
					    		searchTestGroupDatas($('#selected_group_id').val(),null,null,null,true);
					    	}
					    	else{
					    		_message("删除失败，请重试，如果问题依然存在，请联系系统管理员！");
					    	}
					 });
				}
			}
		});
	}
	else{
		_message("请选择一条记录！");
	}
}

function deleteFormDataBatch(){
	var rows=$('#form_datas_dg').datagrid('getChecked');
	console.log(rows)
	if(rows&&rows.length>0){
		$.messager.confirm({
			title: '确认删除',
			border:'thin',
			msg: '确认删除选中的表单数据吗？',
			fn: function(r){
				if (r){
					var ids='';
					for(var i=0,l=rows.length;i<l;i++){
						ids+=rows[i].id+',';
					}
					if(ids!=''){
						ids=ids.substr(0,ids.length-1);
					}
					getJSON(window.localStorage.ctx+"/research/deleteFormDataBatch",{"ids":ids},function(data){
						var json=validationData(data);
						if(json.code==0){
					    	searchTestGroupDatas($('#selected_group_id').val(),null,null,null,true);
						} else{
					    	_message("删除失败，请重试，如果问题依然存在，请联系系统管理员！");
					    }
					});
				}
			}
		});
	} else{
		_message("请选择数据！");
	}
}

function deleteFormDataSub(index,patientid){
	$('#sub_form_datas_dg_'+patientid).datagrid('selectRow',index);
	var row=$('#sub_form_datas_dg_'+patientid).datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: '确认删除',
			border:'thin',
			msg: '确认删除选中的表单数据吗？',
			fn: function(r){
				if (r){
					 getJSON(window.localStorage.ctx+"/research/deleteFormData",{id:row.id},function(data){
						 	var json=validationData(data);
					    	if(json.code==0){
					    		searchTestGroupDatas($('#selected_group_id').val(),null,null,patientid,true);
					    	}
					    	else{
					    		_message("删除失败，请重试，如果问题依然存在，请联系系统管理员！");
					    	}
					 });
				}
			}
		});
	}
	else{
		_message("请选择一条记录！");
	}
}

function openEditTestGroupDlg(groupid) {
	$('#common_dialog').dialog(
		{
			title : '编辑实验组',
			width : 300,height : 180,
			resizable : false,minimizable : false,maximizable : false,modal : true,
			border : 'thin',
			href : window.localStorage.ctx + '/research/toEditTestGroup?id=' + (groupid || '') + '&projectid=' + $("#_project_id").val(),
			buttons : [ {
				text : '保存',
				width : 80,
				handler : function() {
					saveTestGroup();
				}
			}, {
				text : '关闭',
				width : 80,
				handler : function() {
					$('#common_dialog').dialog("close")
				}
			} ]
		});
}

function manageDicomImages(){
	var matrix_url=$('#matrix_url').val();
	/*var matrix_username=$('#matrix_username').val();*/
	getJSON(window.localStorage.ctx + "/research/getMatrixToken",
		{},function(data) {
			console.log(data);
			if(data.code==0){
				var url= matrix_url+"/cas/login?username="+data.data.username+"&language=zh_CN&sessionid="+data.data.sessionid;
				window.open(url,"matrix",null,true);
			} else{
				_message('错误：'+data.message);
			}
		}
	);
	
	
//	var url=$('#matrix_view_url').val()+"?patientID="
//		window.open(url,"matrixview","height="+ (screen.availHeight-50) +",width="+ (screen.availWidth - 10) +",top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no",true);
//	var url="http://localhost:8083/matrix/cas/token?username=sa1&key=78645sdfsdfafde5201"
//	window.open(url,"matrix",null,true);
}

function saveTestGroup() {
	var projectId = $("#_project_id").val();
	$("#experienceform").form('submit',
		{
			url : window.localStorage.ctx + "/research/saveTestGroup",
			onSubmit : function(param) {
				return $("#experienceform").form('validate');
			},
			success : function(data) {
				var data = eval('(' + data + ')');
				if (data.code == 0) {
					_message("保存成功！");
					searchTestGroup();
					$('#common_dialog').dialog('close');
				} else {
					_message("保存失败，请重试，如果问题依然存在，请联系系统管理员！")
				}
			}
		});
}

function deleteTestGroup(groupid) {
//	var row = $('#experience_group_dg').datagrid('getSelected');
//	if (row) {
		$.messager.confirm({
			title : '确认删除',
			border : 'thin',
			msg : '确认删除选中的实验组吗？',
			fn : function(r) {
				if (r) {
					getJSON(window.localStorage.ctx
							+ "/research/deleteTestGroup", {
						id : groupid
					}, function(data) {
						// alert("JSON Data: " + json.patientname);
						var json = validationData(data);
						if (json.code == 0) {
							searchTestGroup();
							$('#form_datas_dg').datagrid('loadData', {
								rows : []
							});
							
							$('#selected_group_id').val('');
							$('#testGroupLayout').layout('panel','center').panel('setTitle','实验组数据');
							$("#entry_form_menubtn").menubutton('disable');
						} else {
							_message("删除失败，请重试，如果问题依然存在，请联系系统管理员！");
						}
					});
				}
			}
		});
//	} else {
//		_message("请选择一个实验组！");
//	}
}

//function searchFormInfo(index, row) {
//	getJSON(window.localStorage.ctx + "/research/searchFormInfo", {
//		experienceId : row.id
//	}, function(data) {
//		$("#form_info_dg").datagrid('loadData', data);
//	});
//}

function openFormListDlg(groupid){
	$('#common_right_window').window({
		title : '表单信息',
		width : 450,
		href : window.localStorage.ctx+'/research/toGroupFormList?groupid='+groupid,
	    style:{
            left:'',
            right:0,
            bottom:''
        }
	});
}
function addFormToGroup(){
	var row=$("#formlist_available_dg").datagrid("getSelected");
	if(!row){
		_message("请选择一个表单！");
		return;
	}
	getJSON(window.localStorage.ctx + "/research/addFormToGroup", {
		formid : row.id,
		groupid : $('#addForm_group_id').val()
	}, function(data) {
		var data = validationData(data);
		if (data.code == 0) {
			$('#formlist_selected_dg').datagrid('reload');
		} else {
			_message(data.message);
		}
	});
}
function findTaskForm(){
	var taskName=$("#taskName").searchbox("getValue");
	getJSON(window.localStorage.ctx+"/research/searchTaskForm",{'taskName':taskName}, function(json){
   	$("#taskFormDg").datagrid("loadData",json);
   });
}
//function taskFormDg_formatter(value, row, index){
//	return '<a href="#" class="easyui-linkbutton" onclick="openReportTemplate2('+index+');">查看模板</a>';
//}

function formlist_onselect(index,row){
	$("#addFormToGroup_lb").linkbutton('enable');
}

function addFromToGroup_formatter(value, row, index){
	return '<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick=" previewForm('+index+',null,1);"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-shenqing"></i></span></span></a>&nbsp;&nbsp;'+
	'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="removeFormFromGroup('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-delete"></i></span></span></a>';
}
function addFromToGroup_formatter_available(value,row,index){
	return '<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick=" previewForm('+index+',null,2);"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-shenqing"></i></span></span></a>';
}

function removeFormFromGroup(index){
	$("#formlist_selected_dg").datagrid("selectRow",index);
	var row=$("#formlist_selected_dg").datagrid("getSelected");
	$.messager.confirm({
		title : '确认',
		border : 'thin',
		msg : '移除表单同时会删除以该表单录入的数据，此操作无法撤销。确认从实验组中移除该表单吗？',
		fn : function(r) {
			if (r) {
				getJSON(window.localStorage.ctx + "/research/removeFormFromGroup", {
					id : row.groupformid
				}, function(data) {
					var json = validationData(data);
					if (json.code == 0) {
						$('#formlist_selected_dg').datagrid('reload');
					} else {
						_message("删除失败，请重试，如果问题依然存在，请联系系统管理员！");
					}
				});
			}
		}
	});
}

function searchForm(){
	var formname=$("#formname_addtogroup").searchbox("getValue");
	getJSON(window.localStorage.ctx+"/research/findResearchForm",{'name':formname,'withContent':false}, function(json){
    	$("#formlist_available_dg").datagrid("loadData",json);
    	$("#addFormToGroup_lb").linkbutton('disable');
    });
}


function addSreport2Group(){
	$('#common_right_window').window({
		title : '添加报告至实验组',
		width : 950,
		href : window.localStorage.ctx+'/research/toAddSRportToGroup',
	    style:{
           left:'',
           right:0,
           bottom:''
       }
	}); 
}

/**
 * 加载检查项目
 * @param element
 * @returns
 */
function loadStudyItem(newValue,oldValue){
	console.log(newValue)
	getJSON(window.localStorage.ctx+"/dic/getExamItemDicFromCache",
		 {
			modality : newValue
		},
		function(data) {
			var json=validationData(data);
			$("#examitemName").combobox('loadData',json);
		}
	);
}

function initPagination(dg){
	dg.datagrid('getPager').pagination({
		pageSize: 20,
		onSelectPage:function(pageNumber, pageSize){
			doSearchSreport(pageNumber, pageSize);
		}
	});
	dg.datagrid('getPager').attr('init_select',true);
}

function doSearchSreport(pageNumber, pageSize){
	
	if(!$('#sreport_dg').datagrid('getPager').attr('init_select')){
		initPagination($('#sreport_dg'));
	}
	
	if(!pageNumber){
		pageNumber=1;
	}
	if(!pageSize){
		pageSize=20;
	}
	
	$('#progress_dlg').dialog('open');
	$('#search_sreport_form').form('submit', {
	    url: window.localStorage.ctx+"/research/searchReport",
	    onSubmit: function(param){
	        param.page=pageNumber,
	        param.rows=pageSize
	    },
	    success:function(data){
	    	$('#progress_dlg').dialog('close');
			$("#sreport_dg").datagrid("loadData", validationData(data));
	    }
	});
}

function cleanSearchSreport_Condition(){
	$('#search_sreport_form').form('reset');
}

function addToBeList(){
	var rows=$("#sreport_dg").datagrid("getSelections");
	if(!rows||rows.length==0){
		_message('请选择要申请的报告！');
		return;
	}
	var rows_selected=$("#selected_sreport_dg").datagrid("getRows");
	var error=false;
	for (var i = 0; i < rows.length; i++) {
		var row=rows[i];
		var flag=false;
		if(rows_selected.length>0){
			for (var j = 0; j < rows_selected.length; j++) {
				if(row.report_id==rows_selected[j].report_id){
					error=true;
					flag=true;
					break;
				}
			}
		}
		if(flag){
			continue;
		}
		$('#selected_sreport_dg').datagrid('appendRow',row);
	}
	if(error){
		_message('选择的报告中，有报告已经添加，将不会重复添加！');
	}
	$("#sreport_dg").datagrid("clearSelections");
}

function removeFromToBeList(){
	var rows=$("#selected_sreport_dg").datagrid("getSelections");
	if(!rows||rows.length==0){
		_message('请选择要移除的报告！');
		return;
	}
	for (var i = 0; i < rows.length; i++) {
		var row=rows[i];
		var index=$("#selected_sreport_dg").datagrid("getRowIndex",row);
		$("#selected_sreport_dg").datagrid("deleteRow",index);
	}
	$("#selected_sreport_dg").datagrid("clearSelections");
}

function previewSreport(index,row){
	console.log(row)
	$('#common_dialog').dialog({
		title : '查看报告',
		width : 800,height : 800,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/research/viewReport',
		onLoad:function(){
			//var content=row.checkdesc_html;
			//content=content.replace(/ghidden/g,"hidden='hidden'");
			//content=content.replace(new RegExp("_ueditor_page_break_tag_","gm"),"<hr class='pagebreak' noshade='noshade' size='5' style='-webkit-user-select: none;'>")
			getJSON(window.localStorage.ctx + "/research/findReportByFormId", {
				'id' : row.report_id
			}, function(data) {
				var json = validationData(data);
				$('#task_report').panel({content:pluginHandle.formatComponent(json.checkdesc_html)});
			});
			
			
		},
		onBeforeDestroy : function(title, index) {
    		$('#task_report').empty();
		},
		buttons:[{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

function openSumbitDlg(){
	var rows=$("#selected_sreport_dg").datagrid("getRows");
	if(!rows||rows.length==0){
		_message('请添加报告！');
		return;
	}
	
	$("#reportPrescriptionDlg").dialog("open");
}

function applySreport2Group(){
	var reportPrescription=$("#reportPrescriptionTime").datebox("getValue");
	if(!reportPrescription){
		_message('请选择有效期！');
		return;
	}
	var rows=$("#selected_sreport_dg").datagrid("getRows");
	getJSON(window.localStorage.ctx + "/research/applySreport2Group", {
		'validDate' : reportPrescription,
		'reports' : JSON.stringify(rows),
		'projectid' : $('#_project_id').val(),
		'groupid' : $('#selected_group_id').val()
	}, function(data) {
		var json = validationData(data);
		if (json.code == 0) {
			_message("申请成功！");
			$("#reportPrescriptionDlg").dialog("close");
			$('#common_right_window').dialog('close');
		}else if(json.code==2){
			_message("申请添加至实验组的报告，已经存在申请中，请勿重复申请！");
		}
	});
}

function previewForm(index,row,type){
	var dg;
	if(type==1){
		dg="formlist_selected_dg";
	}else if(type==2){
		dg="formlist_available_dg";
	}else{
		dg="";
	}
	$('#common_dialog').dialog({
		title : '预览表单',
		width : 800,height : 800,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/research/viewReport?fi_Id='+(row!=null?row.fi_id:"")+'&formId='+(row!=null?row.form_id:""),
		onLoad:function(){
			$('#'+dg).datagrid('selectRow',index);
			var formRow=$('#'+dg).datagrid('getSelected');
			getJSON(window.localStorage.ctx + "/research/getFormById", {
				'id' : formRow.id
			}, function(data) {
				var json = validationData(data);
				$('#task_report').panel({content:pluginHandle.formatComponent(json.formcontent)});
			});
		},
		onBeforeDestroy : function(title, index) {
    		$('#task_report').empty();
		},
		buttons:[{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
	
}

function selectStudyDg(value,text){
	 $('#select_inspect_dialog').dialog({
			title : '表单名:'+text,
			width : 950,height : 600,
			resizable: false,minimizable: false,maximizable: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/research/toStudyInfo',
			buttons:[{
					text:'确定',
					width:80,
					handler:function(){
						var tab=$("#select_study_tabs").tabs('getSelected');
						var index = $('#select_study_tabs').tabs('getTabIndex',tab);
						var row;
						if(index==0){
							row=$("#inspectInfoDg").datagrid("getSelected");
						} else{
							row=$("#dicomInfoDg").datagrid("getSelected");
						}
						
						if(row){
							entryForm(null,value,row,1);
							$('#select_inspect_dialog').dialog('close');
						}else{
							_message("请选择检查信息！！");
						}
					}
				},{
					text:'关闭',
					width:80,
					handler:function(){$('#select_inspect_dialog').dialog('close');}
			}]
		 });
}

function age_formatter(value,row,index){
	if(value){
		return value+row.ageunitdisplay;
	}
	else{
		return "";
	}
}
function patientview_operate_formatter(value,row,index){
	return '<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick=" openModifyPatientDlg('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-edit"></i></span></span></a>';
}

function openModifyPatientDlg(index){
	console.log(index)
	$('#form_datas_dg').datagrid('selectRow',index);
	var row=$('#form_datas_dg').datagrid('getSelected');
	console.log(row)
	if(row){
		$('#common_dialog').dialog({
			title : "编辑病人",
			width : 350,height : 280,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/research/goEditPatient?patientid='+row.patientid,
			buttons:[{
				text: $.i18n.prop('save'),
				width:80,
				handler:function(){savePatient();}
			},{
				text: $.i18n.prop('cancel'),
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}],
		});
	}
	else{
		$.messager.show({
			title: $.i18n.prop('alert'),
			msg: $.i18n.prop('admin.selectoneuser'),
			timeout:3000,
			border:'thin',
			showType:'slide'
		});
	}
}

function checkPatientid(newvalue,oldvalue){
	console.log("checkPatientid:"+newvalue);
	getJSON(window.localStorage.ctx+"/research/checkPatientid",
		{
			patientid : newvalue,
			patientname:$('#patientname').val(),
		},
		function(data){
			var json=validationData(data);
			if(json.exist){
				$('#checkexist').val('true');
				console.log("exist:"+json.exist);
				$('#patientid').validatebox({
					validType: "exist['true']"
				})
			}
			else{
				$('#checkexist').val('');
				console.log("exist:"+json.exist);
				$('#patientid').validatebox({
					validType: "exist['false']"
				})
			}
		});
}

function savePatient(){
	$('#patientform').form('submit', {
		url: window.localStorage.ctx+'/research/savePatient',
		success: function(data){
			var json=validationDataAll(data);
			if(json.code==0){
				$('#common_dialog').dialog('close');
				$('#form_datas_dg').datagrid('reload');
				$.messager.show({
					title:'保存成功',
					msg: "修改成功",
					timeout:3000,
					border:'thin',
					showType:'slide'
				});
			}else{
				$.messager.show({
					title:'错误提醒',
					msg:json.message,
					timeout:3000,
					border: 'thin',
					showType:'slide'
				});
			}
		},
	})
}

function groupInfoOnload(){
	var groupid=$('#select_groupid_hidden').val();
	if(groupid){
		genderDistribution(groupid);
		ageDistribution(groupid);
	}
}

//性别分布
function　genderDistribution(groupid){
	var container= document.getElementById("container_sex");
	if(container==null){
		return;
	}
//	if(myChart_sex==null){
		var myChart_sex = echarts.init(container,'');
//	}

	getJSON(window.localStorage.ctx+'/research/getGroupGenderRatio',
		{
			groupid:groupid
		},
		function(json){
			console.log(json)
			var option = {
				title: {
					text: '性别比',
	// 				subtext: json.data.startdate+' 总共：'+json.data.total,
					left: 'left',
					padding: [10, 5, 5, 5],
					textStyle: {
						fontSize: 14
					}
				},
				grid: {
					top: 50,
					bottom: 10
				},
				legend: {
				    top: '3%',
				    left: 'right'
				},
				tooltip: {
					trigger: 'item'
				},
				
				series: [
					{
						name: '性别',
						type: 'pie',
						radius: ['0%', '65%'],
						label: {
					        //show: false,
					        position: 'inner',
							formatter: '{d}%'
					    },
						emphasis: {
							itemStyle: {
								shadowBlur: 10,
								shadowOffsetX: 0,
								shadowColor: 'rgba(0, 0, 0, 0.5)'
							}
						},
						data: json.data.data
//				      		[
//					        { value: 1048, name: '男' },
//					        { value: 735, name: '女' }
//					        
//				      	] 
					}
				]
			};

			myChart_sex.setOption(option);
			myChart_sex.on('click', function(params) {
				console.log(params)
			});
		}
	);
	
}

function ageDistribution(groupid){
	var  container= document.getElementById("container_age");
	if(container==null){
		return;
	}
	//if(myChart_age==null){
		var myChart_age = echarts.init(container,'');
	//}
	getJSON(window.localStorage.ctx+'/research/getGroupAgeDistribution',
		{
			groupid:groupid
		},
		function(json){

			var option = {
				title: {
					text: '年龄分布',
					left: 'left',
					padding: [10, 5, 5, 5],
					textStyle: {
						fontSize: 14
					}
				},
				grid: {
					top: 40,
					bottom: 55,
					left:30,
					right:10
				},
				tooltip: {
					trigger: 'axis'
				},
				toolbox: {
					orient: 'horizontal',
					showTitle: false,
					tooltip: {
						show: false
					},
					feature: {
						magicType: {
							type: ["line", "bar"]
						}
					}
				},
				xAxis: {
					type: 'category',
					axisLabel: { rotate: 60 },
					data: ['小于20岁', '20-40岁', '40-60岁', '大于60岁']
				},
				yAxis: {
					type: 'value'
				},
				series: [
					{
						data: json.data.data,//[80, 70, 110, 130],
						type: 'bar',
						label: {
							show: true
						}
					}
				]
			};
			myChart_age.setOption(option);
		}
	);
}

function changeView(view,viewname){
	console.log(view)
	console.log(viewname)
	$('#viewtype_menubtn').attr('selectitem',view);
	$('#viewtype_menubtn').linkbutton({
		text: viewname,
		iconCls: 'm-btn-downarrow',
		iconAlign: 'right'
	});
	var op= $('#viewtype_menubtn').linkbutton('options');
	
	console.log(op)
	op.text=viewname;
	var groupid=$('#selected_group_id').val();
	
	if(view=='patient'){
		$('#form_datas_dg').datagrid({
		    url: window.localStorage.ctx+'/research/findGroupPatient?groupid='+groupid,
			singleSelect:true,
			ctrlSelect:false,
			fit:true,
			autoRowHeight:true,
			fitColumns:true,
			toolbar:'#toolbar_form_info',
			border:false,
			scrollbarSize:0,
			loadMsg:'加载中...',
			emptyMsg:'没有查找到数据...',
		    columns:[[
		        {field:'patientname',title:'病人姓名',width:100},
				{field:'patientid',title:'病人编号',width:100},
		        {field:'sexdisplay',title:'性别',width:100},
		        {field:'age',title:'年龄',width:100,formatter:age_formatter},
				{field:'birthdate',title:'出生日期',width:100},
				{field:'operate',title:'操作',align:'center',formatter:patientview_operate_formatter,width:50}
		    ]],
			onRowContextMenu:function(e,index ,row){
	            e.preventDefault();
	            $(this).datagrid('selectRow',index);
	        },
			view: detailview,
	        detailFormatter:function(index,row){
	            return '<div class="ddv" style="padding:5px 10px 5px 0px;"></div>';
	        },
	        onExpandRow: function(index,row){
	            var ddv = $(this).datagrid('getRowDetail',index).find('div.ddv');
	            ddv.panel({
	                height:300,
	                border:false,
	                cache:false,
	                href:window.localStorage.ctx+'/research/showPatientStudyAndFormData?groupid='+groupid+'&patientid='+row.patientid,
	                onLoad:function(){
	                    $('#form_datas_dg').datagrid('fixDetailRowHeight',index);
	                }
	            });
	            $('#form_datas_dg').datagrid('fixDetailRowHeight',index);
	        }
		});
	} else{
		$('#form_datas_dg').datagrid({
		    url: window.localStorage.ctx+'/research/searchTestGroupDatas1?groupid='+groupid,
			singleSelect:false,
			ctrlSelect:true,
			fit:true,
			autoRowHeight:true,
			fitColumns:true,
			toolbar:'#toolbar_form_info',
			border:false,
			scrollbarSize:0,
			loadMsg:'加载中...',
			emptyMsg:'没有查找到数据...',
		    columns:[[
				{field:'cb',checkbox:true},
		        {field:'form_name',title:'表单名称',width:100},
				{field:'statusdisplay',title:'状态',width:50,align:'center',styler:columeStyler_formstatus},
		        {field:'patientname',title:'病人姓名',width:100,align:'center'},
				{field:'sexdisplay',title:'性别',width:50,align:'center'},
				{field:'patientid',title:'病人编号',width:100,align:'center'},
		        {field:'age',title:'年龄',width:50,formatter:age_formatter},
				{field:'birthdate',title:'出生日期',width:80,align:'center'},
				{field:'studyid',title:'检查号',width:100,align:'center'},
				{field:'study_datetime',title:'检查时间',width:100,align:'center'},
				{field:'modality_type',title:'设备类型',width:60,align:'center'},
				{field:'studyitems',title:'检查项目',width:100,align:'center'},
				{field:'createtime',title:'创建时间',width:100,align:'center'},
				{field:'creator_name',title:'创建人',width:50,align:'center'},
				{field:'audittime',title:'审核时间',width:100,align:'center'},
				{field:'auditor_name',title:'审核人',width:50,align:'center'},
				{field:'operate',title:'操作',width:80,align:'center',formatter:form_data_formatter},
		    ]],
			onRowContextMenu:function(e,index ,row){
	            e.preventDefault();
	            $(this).datagrid('selectRow',index);
	            $('#cmenu_form_datas').menu('show', {
	                left:e.pageX,
	                top:e.pageY
	            });
	        },
			view: groupview,
	        groupField:'form_name',
			groupFormatter:groupdata_group_formatter
		});
	}
}

function studys_formatter(value, row, index){
	return '<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="openDicomImage(\''+row.patientid+'\',\''+row.acc_no+'\',\''+row.study_iuid
		+'\');"><span class="l-btn-left"><span class="l-btn-text">图像</span></span></a>';
}

function openDicomImage(patientid,accno,studyinsuid){
//	console.log(patientid);
//	console.log(accno);
//	console.log(studyinsuid);
	
	var url=$('#matrix_view_url').val()+"?patientID="+patientid+"&studyUID="+studyinsuid+"&serverName="+$('#matrix_view_servername').val();
//		window.open(url,"matrixview","height="+ (screen.availHeight-50) +",width="+ (screen.availWidth - 10) +",top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no",true);
	window.open(url,"matrixview",null,true);
	
}

/*
参数 ：type
	data：导出表单数据
	temp：导出excel模板
 */
function exportFormData(type){
	
	var groupid=$('#selected_group_id').val();
	getJSON(window.localStorage.ctx+'/research/findTestgroupFrom',
		{
			'groupid': groupid,
			'withContent': true
		},
		function(data){
			console.log(data);
//			if('temp'==type){
				for(var i=0,len=data.length;i<len;i++){
					var temp = data[i];
					var sheet=new Object();
					sheet.sheetname=temp.name;
					sheet.uid=temp.uid;
					var columns=new Array();
	//				console.log('----------------------------------------------'+name)
					if(temp.formcontent){
						$("<div>"+temp.formcontent+"</div>").find("input:not(.textbox-text,.textbox-value,[gtype='snapshot'],[gtype='finding']),select,textarea").each(function(index,e){
	//						console.log(e)
							var col=new Object();
							if($(e).attr('name')=='clinicalcode'){
	//							console.log($(e).attr('gvalue')+"---"+$(e).attr('code'));
								col.name=$(e).attr('gvalue');
								col.code=$(e).attr('code');
							} else{
	//							console.log($(e).attr('title')+"---"+$(e).attr('code'));
								col.name=$(e).attr('title');
								col.name=col.name.replace("[","{").replace("]","}");//替换掉组件名称中的[]，数字输入框组件的title中有[正常值]
								col.code=$(e).attr('code');
								col.unit=$(e).attr('unit')||'';
								if(/select/ig.test( e.tagName )){//获取选择框选项值
									var opts=new Array();
									for(var j=0,l=e.options.length;j<l;j++){
										opts.push(e.options[j].value);
									}
									col.options=opts;
								} else if($(e).hasClass('easyui-datebox')){
									col.type='date';
								} else if($(e).hasClass('easyui-datetimebox')){
									col.type='datetime';
								}
							}
							columns.push(col);
						});
					}
					sheet.columns=columns
					$('<form method="post" id="tempform_'+i+'" class="tempform__" action="'+window.localStorage.ctx+'/research/exportFormData?type='
						+type+'&groupid='+groupid+'&formid='+data[i].id+'" hidden="hidden"></form>')
					.append($('<input type="hidden" name="sheets"></input>').attr("value", JSON.stringify(sheet))).appendTo('body');//.submit().remove();
	
				}
//			} else{
//				for(var i=0,len=data.length;i<len;i++){
//					$('<form method="post" id="tempform_'+i+'" class="tempform__" action="'+window.localStorage.ctx+'/research/downloadFormData?type='
//						+type+'&groupid='+groupid+'&formid='+data[i].id+'" hidden="hidden"></form>')
//						.appendTo('body');//.submit().remove();
//				}
//			}
			for(var i=0,len=data.length;i<len;i++){
				window.setTimeout(function(){
					console.log($('.tempform__').first())
					$('.tempform__').first().submit().remove();
				},(i)*1000);
			}
	});

}

function uploadFormData(){
	$('#import_error_message').textbox('setValue','');
	var groupid=$('#selected_group_id').val();
	if(!groupid){
		_message("请选择一个实验组！");
		return;
	}
	var filetxt=$('#formdata_excel').filebox("getText");
	if(!filetxt){
		_message('请选择Excel文件！');
		return;
	}
	
	$('#uploadexcelfm').form('submit', {
	    url:window.localStorage.ctx+'/research/importFormData',
		onSubmit: function(param){
	        param.groupid = groupid;
	    },
	    success:function(result){
	    	var json=validationData(result);
	    	if(json.code == 0){
				$('#uploadexcelfm').form('clear');
				$('#upload_excel_dlg').dialog('close');
				searchTestGroupDatas($('#selected_group_id').val(),null,null,null,true);
            }else{
				searchTestGroupDatas($('#selected_group_id').val(),null,null,null,true);
//				_message();
				$('#import_error_message').textbox('setValue',json.message);
            }
	    }
	});
}

function auditFormDataBatch(){
	var rows=$('#form_datas_dg').datagrid('getChecked');
	console.log(rows)
	if(rows&&rows.length>0){
		
		var ids='';
		for(var i=0,l=rows.length;i<l;i++){
			ids+=rows[i].id+',';
		}
		if(ids!=''){
			ids=ids.substr(0,ids.length-1);
		}
		getJSON(window.localStorage.ctx+"/research/auditFormDataBatch",{"ids":ids},function(json){
			console.log(json)
			if(json.code==0){
		    	searchTestGroupDatas($('#selected_group_id').val(),null,null,null,true);
			} else if(json.code==-2){
				searchTestGroupDatas($('#selected_group_id').val(),null,null,null,true);
		    	_message("部分数据审核失败，请先解决问题，再手动审核！");
		    } else{
		    	_message("批量审核失败，请重试，如果问题依然存在，请联系系统管理员！");
		    }
		});
				
	} else{
		_message("请选择数据！");
	}
}

function goAdvAnalysis(){
	window.open($('#kibana_url').val(),"matrix",null,true);		
}

