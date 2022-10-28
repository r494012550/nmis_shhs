/**
 * 
 */

function findStatistics(value){
	getJSON(window.localStorage.ctx+"/statistics/findStatistics",{value:value}, function(json){
    	$("#statisticsdg").datagrid("loadData",json);
    });
}

function openEditStatisticsDlg(row){
	$('#common_dialog').dialog({
		title : '编辑报表',
		width : 550,height : 650,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/statistics/gotoEditStatistics?id='+(row!=null?row.id:""),
		buttons:[{
			text:'保存',
			width:80,
			handler:function(){saveStatistics();}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
//		,onLoad:function(){
//			var conns=$('#statisticsconditions').val();
//			if(conns){
//				var connarr=conns.split(",");
//				var rows=$("#conditions_list").datalist("getRows");
//				for(var j=0,l=connarr.length;j<l;j++){
//					for(var i=0,len=rows.length;i<len;i++){
//						if(connarr[j]==rows[i].value){
//							$("#conditions_list").datalist("checkRow",i);
//						}
//					}
//				}
//			}
//		}
	});
}


function openModifyStatisticsDlg(index){
	console.log(index)
//	if(index){
		$('#statisticsdg').datagrid('selectRow',index);
//	}
	var row=$('#statisticsdg').datagrid('getSelected');
	if(row){
		openEditStatisticsDlg(row);
	}
	else{
		$.messager.show({
            title:'提醒',
            msg:"请选择一个报表！",
            timeout:3000,
            border: 'thin',
            showType:'slide'
        });
	}
}

function saveStatistics(){
	$('#statisticsform').form('submit', {
		url: window.localStorage.ctx+"/statistics/saveStatistics",
		onSubmit: function(param){
			
			if(!$('#statisticsid').val()){
				if($('#generation_type_template').radiobutton('options').checked){
					if($('#jrxml_id').filebox('getValue')==""||$('#jasper_id').filebox('getValue')==""){
						$.messager.show({
				            title:'提醒',
				            msg:"请上传模板设计文件！",
				            timeout:3000,
				            border: 'thin',
				            showType:'slide'
				        });
						return false;
					}
				} else{
					if($('#sql').textbox('getValue')==""){
						_message("请输入SQL语句！",'提醒');
						return false;
					}
				}
			}
			
			var conditions="";
			$('#conditions_panel').find(".easyui-checkbox").each(function(index,obj){
				if($(obj).checkbox('options').checked){
					conditions+=$(obj).val()+";";
				}
			});	
			console.log(conditions);
			
			if($('#enable_regoperator').checkbox('options').checked){
				var val=$('#regoperator').combobox("getValues");
				if(val){
					conditions="regoperator("+val+");"+conditions
				}
				else{
					return false;
				}
			}
			if($('#enable_technologists').checkbox('options').checked){
				var val=$('#technologists').combobox("getValues");
				if(val){
					conditions="technologists("+val+");"+conditions
				}
				else{
					return false;
				}
			}
			if($('#enable_reportphysician').checkbox('options').checked){
				var val=$('#reportphysician').combobox("getValues");
				if(val){
					conditions="reportphysician("+val+");"+conditions
				}
				else{
					return false;
				}
			}
			if($('#enable_auditphysician').checkbox('options').checked){
				var val=$('#auditphysician').combobox("getValues");
				if(val){
					conditions="auditphysician("+val+");"+conditions
				}
				else{
					return false;
				}
			}
			if($('#enable_datetime').checkbox('options').checked){
				var datetype=$('#date_type').combobox("getValue");
				if(datetype){
					conditions="datetime("+datetype+");"+conditions
				}
				else{
					return false;
				}
			}
			if(conditions!=""){
				conditions=conditions.substr(0,conditions.length-1);
			}

			$('#statisticsconditions').val(conditions);
			$('#statisticsclassifyname').val($('#statisticstype').combobox('getText'));
			
			endEditing_statistics();
			var rows = $('#custom_conditions').datagrid('getChanges');
			if(rows!=null&&rows.length>0){
				param.custom_conditions=JSON.stringify(rows);
			}
	
			if($('#statisticsname').textbox('getValue')==""||$('#statisticsconditions').val()==""
				||$('#statisticstype').combobox('getValue')==""){
				return false;
			}
			
			if (editIndex_statistics){
				if(!$('#custom_conditions').datagrid('validateRow', editIndex_statistics)){
					return false;
				}
			}
		},
		success: function(data){
			var json=validationDataAll(data);
			console.log(json)
			if(json.code==0){
				findStatistics('');
				$('#common_dialog').dialog('close');
			}
			else{
				$.messager.show({
		            title:'错误提醒',
		            msg:'保存失败请重试，如果问题依然存在，请联系系统管理员！',
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
				
			}
		}
	});
}

function deleteStatistics(index){
	if(index){
		$('#statisticsdg').datagrid('selectRow',index);
	}
	var row=$('#statisticsdg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除选中的报表吗？',
			fn: function(r){
				if (r){
					 getJSON(window.localStorage.ctx+"/statistics/deleteStatistics",{id:row.id},function(data){
					    	//alert("JSON Data: " + json.patientname);
						 	var json=validationData(data);
					    	if(json.code==0){
					    		var index=$("#statisticsdg").datagrid('getRowIndex',row);
					    		$("#statisticsdg").datagrid('deleteRow',index);
					    	}
					    	else{
					    		$.messager.show({
					                title:'错误',
					                msg:"删除失败，请重试，如果问题依然存在，请联系系统管理员！",
					                timeout:3000,
					                border:'thin',
					                showType:'slide'
					            });
					    	}
					 });
				}
			}
		});
	}
	else{
		$.messager.show({
            title:'提醒',
            msg:"请选择一个报表！",
            timeout:3000,
            border: 'thin',
            showType:'slide'
        });
	}
}

function addStatisticalClassify(location){
	$.messager.prompt({
		title: '添加报表分类',
		msg: '请输入分类名称：',
		border: 'thin',
		fn: function(r){
			if (r){
					getJSON(window.localStorage.ctx+"/statistics/addStatisticalClassify",{name:r},function(data){
						var json=validationDataAll(data);
						//console.log(json)
				    	if(json.code==0){
				    		if(json.data=="1"){
				    			if(location==1){
				    				$("#statisticstype").combobox('reload');
				    			}
				    			else if(location==2){
				    				$("#classify_list").datagrid('reload');
				    			}
				    		}
				    		else if(json.data=="2"){
				    			$.messager.show({
					                title:'错误',
					                msg:"保存失败，请重试，如果问题依然存在，请联系系统管理员！",
					                timeout:3000,
					                border:'thin',
					                showType:'slide'
					            });
				    		}
				    		else if(json.data=="3"){
				    			$.messager.show({
					                title:'错误',
					                msg:"分类已经存在，请输入其他分类名称！",
					                timeout:3000,
					                border:'thin',
					                showType:'slide'
					            });
				    		}
				    	}
				    	else{
				    		$.messager.show({
				                title:'错误',
				                msg:"保存失败，请重试，如果问题依然存在，请联系系统管理员！",
				                timeout:3000,
				                border:'thin',
				                showType:'slide'
				            });
				    	}
					});
				
			}
		}
	});
}

function fileformatter(value,row,index){
	if(value){
		return "已上传";
	}
	else{
		return "";
	}
}


function operatecolumn_Statistical(value, row, index){
	console.log(index);
	return '<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="openModifyStatisticsDlg('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-edit"></i></span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteStatistics('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-delete"></i></span></span></a>';
}

function openManageClassifyDlg(){
	$('#common_dialog').dialog({
		title : '报告分类管理',
		width : 450,height : 350,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/statistics/gotoManageClassify',
		buttons:[{
			text:'新增',
			width:80,
			handler:function(){
				addStatisticalClassify(2);
			}
		}
		,{
			text:'修改',
			width:80,
			handler:function(){
				var row=$('#classify_list').datagrid('getSelected');
				if(row){
					$.messager.prompt({
						title: '修改报表分类',
						msg: '请输入新的分类名称：',
						border: 'thin',
						fn: function(r){
							if (r){
								
								getJSON(window.localStorage.ctx+"/statistics/addStatisticalClassify",{id:row.id,name:r},function(data){
									var json=validationDataAll(data);
							    	if(json.code==0){
							    		if(json.data=="1"){
							    			$("#classify_list").datagrid('reload');
							    		}
							    		else if(json.data=="2"){
							    			$.messager.show({
								                title:'错误',
								                msg:"保存失败，请重试，如果问题依然存在，请联系系统管理员！",
								                timeout:3000,
								                border:'thin',
								                showType:'slide'
								            });
							    		}
							    		else if(json.data=="3"){
							    			$.messager.show({
								                title:'错误',
								                msg:"分类已经存在，请输入其他分类名称！",
								                timeout:3000,
								                border:'thin',
								                showType:'slide'
								            });
							    		}
							    	}
							    	else{
							    		$.messager.show({
							                title:'错误',
							                msg:"保存失败，请重试，如果问题依然存在，请联系系统管理员！",
							                timeout:3000,
							                border:'thin',
							                showType:'slide'
							            });
							    	}
								});
								
							}
						}
					});
				}
				else{
					$.messager.show({
			            title:'提醒',
			            msg:"请选择一个分类！",
			            timeout:3000,
			            border: 'thin',
			            showType:'slide'
			        });
				}
			}
		}
		,{
			text:'删除',
			width:80,
			handler:function(){
				var row=$('#classify_list').datagrid('getSelected');
				if(row){
					$.messager.confirm({
						title: '确认删除',
						ok: '是',
						cancel: '否',
						border:'thin',
						msg: '该操作无法撤销，确认删除选中的报表分类以及相关的报表吗？',
						fn: function(r){
							if (r){
								getJSON(window.localStorage.ctx+'/statistics/deleteClassify',{id:row.id},function(data){
									var json=validationDataAll(data);
									if(json.code==0){
										$("#classify_list").datagrid('reload');
							    	}
							    	else{
							    		$.messager.show({
							                title:'错误',
							                msg:"删除失败，请重试，如果问题依然存在，请联系系统管理员！",
							                timeout:3000,
							                border:'thin',
							                showType:'slide'
							            });
							    	}
								});
							}
						}
					});
				}
				else{
					$.messager.show({
			            title:'提醒',
			            msg:"请选择一个分类！",
			            timeout:3000,
			            border: 'thin',
			            showType:'slide'
			        });
				}
			}
		}
		,{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

function switchGenerationType(checked,type){
	console.log(checked)
	console.log(type)
	if(checked){
		if(type=='template'){
			$('#jrxml_div').show();
			$('#jasper_div').show();
			$('#sql').textbox('setValue','');
		} else{
			$('#sql_div').show();
			$('#jrxml_id').filebox('setValue','');
			$('#jasper_id').filebox('setValue','');
		}
	} else{
		if(type=='template'){
			$('#jrxml_div').hide();
			$('#jasper_div').hide();
		} else{
			$('#sql_div').hide();
		}
	}
}

