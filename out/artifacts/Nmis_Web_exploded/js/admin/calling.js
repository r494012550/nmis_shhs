function callingSetting(modalityname) {
	var patientname = $('#patientname_exam').textbox('getValue');
	var studyid = $('#studyorderstudyid_exam').textbox('getValue');
	
	$('#common_dialog').dialog(
	{
		title : '叫号病人提示信息',
		width : 420,
		height : 400,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : false,
		border: 'thin',
		href : window.localStorage.ctx+'/calling/getcalling?callingpatientname='+patientname+'&modalityname='+modalityname+'&type='+'examinecalling',
		onLoad:function(){
			if($('#diccalling_dialogonload').val()){
				getJSON(window.localStorage.ctx+"/examine/getPatientCallingHistory",
						{
							studyid: studyid
						},
						function(data){
							if(data.code == 0){
								$('#dicallinginfo_dg').datagrid({
									rowStyler:function(index,row){
										if(index <= data.data) {
											return 'background-color:#A8A8A8;';
										}
									}
								});
							}
						}
					);
			}
			
		},
		buttons:[{
			text:'叫号',
			width:80,
			handler:function(){patientCallinginfo_send();}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

function patientCallinginfo_send() {
	var studyorderpkid = $("#studyorderid_exam").val();
	var status = $("#orderstatus_exam").val();
	var sendmessage = $('#dicallinginfo_dg').datagrid('getSelected').message;
	var completetime=$("#completetime").val();
	var studyid = $('#studyorderstudyid_exam').textbox('getValue');
	var index = $('#dicallinginfo_dg').datagrid('getRowIndex',$('#dicallinginfo_dg').datagrid('getSelected'));
	if(status==myCache.StudyOrderStatus.in_process){
		getJSON(window.localStorage.ctx+"/examine/diccallingsendmessage",
				{
					orderid : studyorderpkid,
					sendmessage: sendmessage,
					studyid: studyid,
					index: index
				},
				function(data) {
					var json=validationData(data);
					if(json.code==0) {
						_message('发送成功！','提醒');
						
					}else {
						_message(json.message,'提醒');
					}
					$('#common_dialog').dialog('close');
				}
			);
	}else {
		_message('当前操作无法完成！','提醒');
	}
}


function new_callingdic() {
	$('#common_dialog').dialog({
				title : '新建提示',
				width : 360,
				height : 220,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				border: 'thin',
				href : window.localStorage.ctx+'/dic/addDicCalling?modality='+$("#modality_organ").val(),
				buttons:[{
					text:'保存',
					width:80,
					handler:function(){saveDicCalling();}
				},{
					text:'关闭',
					width:80,
					handler:function(){$('#common_dialog').dialog('close');}
				}]
		});
}



/**
 * 保存设备-叫号信息
 * @returns
 */
function saveDicCalling() {
	$('#diccallingform').form('submit',{
		url: window.localStorage.ctx+"/dic/saveDicCalling",
		onSubmit: function() {
			return $(this).form('validate');
		},
		success: function(data) {
			var json = validationData(data);
			if(json.code==0) {
				_message("保存成功");
				$('#diccallingdlg').datagrid('reload');
				$('#common_dialog').dialog('close');
			}
			
		}
	});
}


function operatecolumn_diccalling(value, row, index) {
	return $('#cmenu_diccalling').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="ModifyDicCalling('+index+');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteDicCalling('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>':'';
}

/**
 * 修改设备-叫号信息
 * @returns
 */
function ModifyDicCalling(para) {
	$('#diccallingdlg').datagrid('selectRow',para);
	var row = $('#diccallingdlg').datagrid('getSelected');
	if(row){
		$('#common_dialog').dialog({
			title : '编辑叫号信息',
			width : 330,
			height : 230,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/dic/ModifyDicCalling?id='+row.id,
			buttons:[{
				text:'保存',
				width:80,
				handler:function(){saveDicCalling();}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}]
		});
	} else {
		_message("请选择一行");
	}
}

/**
 * 删除设备-叫号信息
 * @returns
 */
function deleteDicCalling(index) {
	$('#diccallingdlg').datagrid('selectRow',index);
	var row = $('#diccallingdlg').datagrid('getSelected');
	if (row) {
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除选中的数据吗？',
			fn: function(r) {
				if (r) {
					$.getJSON(window.localStorage.ctx+"/dic/deleteDicCalling?id="+row.id, function(data){
						var json = validationData(data);
				    	if (json.code == 0) {
				    		_message("删除成功");
				    		var index = $("#diccallingdlg").datagrid('getRowIndex',row);
				    		$("#diccallingdlg").datagrid('deleteRow',index);
				    	} else {
				    		_message("删除失败");
				    	}
					});
				}
			}
		});
	} else {
		_message("请选择一行");
	}
}


function selectDicCalling(newValue,oldValue){
	 getJSON(window.localStorage.ctx+"/dic/getDicCalling",
			{
				value : $('#modality_diccalling').val(),
				modalityid : newValue
			},
			function(data){
				$("#diccallingdlg").datagrid("loadData",validationData(data));
			});
}








