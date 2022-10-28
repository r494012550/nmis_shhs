/**
 * 
 */

function clearField_Modules(){
	$('#modulename').textbox('setValue',"");
	$('#moduleurl').textbox('setValue',"");
	$('#moduleid').val("");
}

function closeModulesDialog(){
	$('#modulesdialog').dialog('close');//destroy
	clearField_Modules();
}

function doSearchModules(value){
	$.getJSON(window.localStorage.ctx+"/system/getModules?value="+value, function(data){
	 	var json=validationData(data);
	 	$("#modulesdg").datagrid("loadData",json);
 	});
}

function saveModule(){
	$('#moduleform').form('submit', {
		url: window.localStorage.ctx+"/system/saveModule",
		onSubmit: function(){
			if(!$('#modulename').textbox('getValue')||!$('#moduleurl').textbox('getValue')){
				return false;
			}
			
		},
		success: function(data){
			var json=validationData(data);
			if(json.code== 0){
				closeModulesDialog();
				
				$.getJSON(window.localStorage.ctx+"/system/getModules", function(json){
			    	$("#modulesdg").datagrid("loadData",json);
			    });
			}
			else{
				$.messager.show({
		            title:'错误提醒',
		            msg:"保存失败请重试，如果问题依然存在，请联系系统管理员！",
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
			}
		}
	});
}

function openModifyModuleDialog(){
	var row=$('#modulesdg').datagrid('getSelected');
	if(row){
		
		$('#modulesdialog').dialog('open');
		$('#moduleid').val(row.id);
		$('#modulename').textbox('setValue',row.name);
		$('#moduleurl').textbox('setValue',row.url);
		
	}
	else{
		$.messager.show({
            title:'提醒',
            msg:"请选择一个模块！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
}


function deleteModule(){
	var row=$('#modulesdg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			border:'thin',
			cancel: '否',
			msg: '确认删除选中的模块吗？',
			fn: function(r){
				if (r){
					 $.getJSON(window.localStorage.ctx+"/system/deleteModule?modulesid="+row.id, function(data){
					    	//alert("JSON Data: " + json.patientname);
						 	var json=validationData(data);
					    	if(json.code== 0){
					    		var index=$("#modulesdg").datagrid('getRowIndex',row);
					    		$("#modulesdg").datagrid('deleteRow',index);
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
            msg:"请选择一个模块！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
}

function doSearchSyscode(value){		
	$.getJSON(window.localStorage.ctx+"/syscode/findSyscode?value="+value, function(json){
		$("#syscodedg").datagrid("loadData",validationData(json));
    });
}

function openEditSyscodeDlg(para){
	$('#common_dialog').dialog({
		title : '编辑系统代码',
		width : 380,
		height : 380,
		resizable: false,
		minimizable: false,
		maximizable: false,
		border: 'thin',
		href : window.localStorage.ctx+'/syscode/openEditSysCode'+(para||''),
		modal : true,
		buttons:[{
			text:'保存',
			width:80,
			handler:function(){saveEditSysCode();}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

function openEditSyscodeGroupDlg(para){
	$('#common_dialog').dialog({
		title : '编辑系统代码组',
		width : 350,
		height : 280,
		resizable: false,
		minimizable: false,
		maximizable: false,
		border: 'thin',
		href : window.localStorage.ctx+'/syscode/openEditSysCodeGroup'+(para||''),
		modal : true,
		buttons:[{
			text:'保存',
			width:80,
			handler:function(){saveEditSysCodeGroup();}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

function openModifySyscodeDlg(index){
	console.log(index)
	$('#syscodedg').datagrid('selectRow',index);
	var row=$('#syscodedg').datagrid('getSelected');
	if(row){
		
		openEditSyscodeDlg('?code='+row.code+'&name_zh='+row.name_zh+'&name_en='+row.name_en+'&type='+row.type+'&parent='+row.parent+'&id='+row.id);
	}
	else{
		$.messager.show({
            title:'提醒',
            msg:"请选择一份数据！",
            timeout:3000,
            border: 'thin',
            showType:'slide'
        });
	}
}

function saveEditSysCode(){
	$('#syscodeform').form('submit', {
		url: window.localStorage.ctx+"/syscode/saveEditSysCode",
		onSubmit: function(){
			if($('#code').textbox('getValue')==""||$('#name_zh').textbox('getValue')==""||$('#name_en').textbox('getValue')==""||$('#parent').combobox('getValue')==""){
				return false;
			}

		},
		success: function(data){
			var json=validationData(data);
			if(json.code!=0){
				$.messager.show({
		            title:'错误提醒',
		            msg:'保存失败请重试，如果问题依然存在，请联系系统管理员！',
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
			}
			else{
				doSearchSyscode('');
				$('#common_dialog').dialog('close');
			}
		}
	});
}

function saveEditSysCodeGroup(){
	$('#syscodeGroupform').form('submit', {
		url: window.localStorage.ctx+"/syscode/saveEditSysCodeGroup",
		onSubmit: function(){
			if($('#name_zh').textbox('getValue')==""){
				return false;
			}

		},
		success: function(data){
			var json=validationData(data);
			if(json.code!=0){
				$.messager.show({
		            title:'错误提醒',
		            msg:'保存失败请重试，如果问题依然存在，请联系系统管理员！',
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
			}
			else{
				doSearchSyscode('');
				$('#common_dialog').dialog('close');
			}
		}
	});
}

function deleteSyscode(index){
	console.log(index)
	$('#syscodedg').datagrid('selectRow',index);
	var row=$('#syscodedg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除选中的代码吗？',
			fn: function(r){
				if (r){
					 $.getJSON(window.localStorage.ctx+"/syscode/deleteSysCode?id="+row.id, function(data){
					    	//alert("JSON Data: " + json.patientname);
						 	var json=validationData(data);
					    	if(json.code==0){
					    		$("#syscodedg").datagrid('reload');
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
            msg:"请选择一份数据！",
            timeout:3000,
            border: 'thin',
            showType:'slide'
        });
	}
}

function operatecolumn_syscode(value, row, index){
	console.log();
	return $('#cmenu_syscode').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="openModifySyscodeDlg('+index+');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteSyscode('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>':'';//"<a href='#' class='easyui-linkbutton' name='operate' onclick='openModifyUserDialog()' ><i class='icon icon-edit1'></i>修改</a>&nbsp;&nbsp;"+
	//"<a name='operate' href='#' class='easyui-linkbutton' onclick='deleteUser()' ><i class='icon icon-remove'></i>删除</a>";
}

/*
 * 
 * 标签增删改查
 * 
 * */
function clickdg(node){
	if (node.foldername){
		$('#btn5').linkbutton('disable');
		$('#btn6').linkbutton('disable');
		$('#btn1').linkbutton('enable');
		$('#btn2').linkbutton('enable');
		$('#btn3').linkbutton('enable');
		$('#btn4').linkbutton('enable');
	}
	else{
		$('#btn5').linkbutton('enable');
		$('#btn6').linkbutton('enable');
		$('#btn1').linkbutton('disable');
		$('#btn2').linkbutton('disable');
		$('#btn3').linkbutton('disable');
		$('#btn4').linkbutton('disable');
	}
	
	
}
function enableMenu(node){
	if(node.foldername){
		var item = $('#mm_label').menu('findItem', {name:'newfolder'});
		$('#mm_label').menu('enableItem', item.target);
		item = $('#mm_label').menu('findItem', {name:'modifyfolder'});
		$('#mm_label').menu('enableItem', item.target);
		item = $('#mm_label').menu('findItem', {name:'delfolder'});
		$('#mm_label').menu('enableItem', item.target);
		item = $('#mm_label').menu('findItem', {name:'newlabel'});
		$('#mm_label').menu('enableItem', item.target);
		item = $('#mm_label').menu('findItem', {name:'modifylabel'});
		$('#mm_label').menu('disableItem', item.target);
		item = $('#mm_label').menu('findItem', {name:'dellabel'});
		$('#mm_label').menu('disableItem', item.target);

	}
	else{
		var item = $('#mm_label').menu('findItem', {name:'newfolder'});
		$('#mm_label').menu('disableItem', item.target);
		item = $('#mm_label').menu('findItem', {name:'modifyfolder'});
		$('#mm_label').menu('disableItem', item.target);
		item = $('#mm_label').menu('findItem', {name:'delfolder'});
		$('#mm_label').menu('disableItem', item.target);
		item = $('#mm_label').menu('findItem', {name:'newlabel'});
		$('#mm_label').menu('disableItem', item.target);
		item = $('#mm_label').menu('findItem', {name:'modifylabel'});
		$('#mm_label').menu('enableItem', item.target);
		item = $('#mm_label').menu('findItem', {name:'dellabel'});
		$('#mm_label').menu('enableItem', item.target);
	}
}

function addRootNode_label(node){
	$.messager.prompt({
		title: '编辑文件夹',
		msg: '请输入文件夹名称',
		border:'thin',
		fn: function(r){
			if (r){
				console.log(r);
				saveLabelFolder(node,r);
			}
		}
	});
	
}

function saveLabelFolder(node,foldername){
	$.post(window.localStorage.ctx+'/system/saveLabelFolder',
		    {
				//'id': node?node.id:null,
		    	'foldername' : foldername,
		    	'parent' : node?node.id:0
		     },
		     function (res) {
	        	var json = validationDataAll(res);
	        	
	        	if(json.code==0){
	        		$('#labels_tree').tree('append', {
	        			parent: node?node.target:null,
						data: {
							id: json.data.id,
							//state: 'closed',
							foldername:foldername,
							text: foldername
						}
					});
	        		$('#labels_tree').tree('reload');
	        	}
	        	else{
	        		$.messager.show({
						title : $.i18n.prop('error'),
						msg : $.i18n.prop('savefailed'),
						timeout : 3000,
						border : 'thin',
						showType : 'slide'
					});
	        	}
	        }
		);
}

function addLabelFolder(){
	var selected = $('#labels_tree').tree('getSelected');
	addRootNode_label(selected);
}

function modifyLabelFolder(){
	var selected = $('#labels_tree').tree('getSelected');
	$('#labels_tree').tree('beginEdit',selected.target);
}

function modifyLabel(){
	var selected = $('#labels_tree').tree('getSelected');
	$('#labels_tree').tree('beginEdit',selected.target);
	
}

function submitMidifyFolder(node){
	if(node.foldername){
		$.post(window.localStorage.ctx+'/system/saveLabelFolder',
			    {
					'id': node.id,
			    	'foldername' : node.text,
			    	'parent' : node.parent
			     },
			     function (res) {
		        	var json = validationDataAll(res);
		        	$('#labels_tree').tree('reload');
		        	if(json.code==0){
		        		//$('#labels_tree').tree('reload');
		        	}
		        	else{
		        		$.messager.show({
							title : $.i18n.prop('error'),
							msg : '保存失败，请检查是否重名。重新保存后如果问题仍存在，请联系系统管理员！',
							timeout : 3000,
							border : 'thin',
							showType : 'slide'
						});
		        	}
		        }
		);
	}
	else{
		$.post(window.localStorage.ctx+'/system/saveLabel',
			    {
					'id': node.labelid,
			    	'label' : node.text,
			    	'folderfk' : node.folderfk
			     },
			     function (res) {
		        	var json = validationDataAll(res);
		        	$('#labels_tree').tree('reload');
		        	if(json.code==0){
		        		//$('#labels_tree').tree('reload');
		        	}
		        	else{
		        		$.messager.show({
							title : $.i18n.prop('error'),
							msg : '保存失败，请检查是否重名。重新保存后如果问题仍存在，请联系系统管理员！',
							timeout : 3000,
							border : 'thin',
							showType : 'slide'
						});
		        	}
		        }
		);
	}
}

function deleteLabelFolder(){
	var selected = $('#labels_tree').tree('getSelected');
		$.messager.confirm({
			title: $.i18n.prop('confirm'),
			msg: '确认删除文件夹！',
			border:'thin',
			fn: function(r){
				if (r){
					$.post(window.localStorage.ctx+'/system/delLabelFolder',
						    {
								'id': selected.id
						     },
						     function (res) {
					        	var json = validationDataAll(res);
					        	if(json.code==0){
					        		$('#labels_tree').tree('remove',selected.target);
					        		$('#labels_tree').tree('reload');
					        	}
					        	else{
					        		$.messager.show({
										title : $.i18n.prop('error'),
										msg : $.i18n.prop('savefailed'),
										timeout : 3000,
										border : 'thin',
										showType : 'slide'
									});
					        	}
					        }
					);
				}
			}
		});
	
}

function deleteLabel(){
	var selected = $('#labels_tree').tree('getSelected');
		$.messager.confirm({
			title: $.i18n.prop('confirm'),
			msg: '确认删除标签吗？',
			border:'thin',
			fn: function(r){
				if (r){
					$.post(window.localStorage.ctx+'/system/delLabel',
						    {
								'id': selected.labelid
						     },
						     function (res) {
					        	var json = validationDataAll(res);
					        	if(json.code==0){
					        		$('#labels_tree').tree('remove',selected.target);
					        		$('#labels_tree').tree('reload');
					        	}
					        	else{
					        		$.messager.show({
										title : $.i18n.prop('error'),
										msg : $.i18n.prop('savefailed'),
										timeout : 3000,
										border : 'thin',
										showType : 'slide'
									});
					        	}
					        }
					);
				}
			}
		});
	
}

function addLabel(){
	var selected = $('#labels_tree').tree('getSelected');
		$.messager.prompt({
			title: '编辑标签',
			msg: '请输入标签名称',
			border:'thin',
			fn: function(r){
				if (r){
					$.post(window.localStorage.ctx+'/system/saveLabel',
						    {
								//'id': node?node.id:null,
						    	'folderfk' : selected.id,
						    	'label' : r
						     },
						     function (res) {
					        	var json = validationDataAll(res);
					        	
					        	if(json.code==0){
					        		$('#labels_tree').tree('append', {
					        			parent: selected.target,
										data: {
											id: json.data.id,
											//state: 'closed',
											text: r
										}
									});
					        		$('#labels_tree').tree('reload');
					        	}
					        	else{
					        		$.messager.show({
										title : $.i18n.prop('error'),
										msg : $.i18n.prop('savefailed'),
										timeout : 3000,
										border : 'thin',
										showType : 'slide'
									});
					        	}
					        }
						);
				}
			}
		});
	
}


/*
 * printer 增删改查
 * 
 */

function operatecolumn_Printer(value, row, index){	
	return $('#cmenu_Printer').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="ModifyPrinter('+index+');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deletePrinter('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>':'';//"<a href='#' class='easyui-linkbutton' name='operate' onclick='openModifyUserDialog()' ><i class='icon icon-edit1'></i>修改</a>&nbsp;&nbsp;"+
	//"<a name='operate' href='#' class='easyui-linkbutton' onclick='deleteUser()' ><i class='icon icon-remove'></i>删除</a>";
}

//增加
function new_Printer(){
	$('#common_dialog').dialog(
		{
			title : '新建打印机',
			width : 350,
			height : 260,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/system/ModifyPrinter',
			buttons:[{
				text:'保存',
				width:80,
				handler:function(){savePrinter();}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}]
	});
}

//查找
function doSearchPrinter(value){
	getJSON(window.localStorage.ctx+"/system/getPrinter",
			{
				value : value
			},
			function(data){
				$("#printerdg").datagrid("loadData",validationData(data));
			});
	/*$.getJSON(window.localStorage.ctx+"/system/getprinter?value="+value, function(json){
		$("#printerdg").datagrid("loadData",validationData(json));
    });*/
}

//删除
function deletePrinter(index){
//	console.log(index);
	$('#printerdg').datagrid('selectRow',index);
	var row=$('#printerdg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除选中的数据吗？',
			fn: function(r){
				if (r){
					 $.getJSON(window.localStorage.ctx+"/system/deletePrinter?id="+row.id, function(data){
						 	var json=validationData(data);
					    	if(json.code==0){
					    		var index=$("#printerdg").datagrid('getRowIndex',row);
					    		$("#printerdg").datagrid('deleteRow',index);
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
            msg:"请选择一份数据！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
}
		
function ModifyPrinter(para){
		$('#printerdg').datagrid('selectRow',para);
		var row=$('#printerdg').datagrid('getSelected');
		if(row){
			$('#common_dialog').dialog({
				title : '编辑打印机',
				width : 350,
				height : 260,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				border: 'thin',
				href : window.localStorage.ctx+'/system/ModifyPrinter?id='+row.id,
				buttons:[{
					text:'保存',
					width:80,
					handler:function(){savePrinter();}
				},{
					text:'关闭',
					width:80,
					handler:function(){$('#common_dialog').dialog('close');}
				}]
			});
		}
		else{
		$.messager.show({
	        title:'提醒',
	        msg:"请选择一个数据！",
	        border:'thin',
	        timeout:3000,
	        showType:'slide'
	    });
	}
}

/**
 * 保存
 * @returns
 */
function savePrinter(){
	$('#printerform').form('submit', {
		url: window.localStorage.ctx+"/system/savePrinter",
		onSubmit: function(){
			return $(this).form('validate');
		},
		success: function(data){
			var json=validationData(data);
			if(json.code==0){
				$("#printerdg").datagrid("reload");
				$('#common_dialog').dialog('close');
//				getJSON(window.localStorage.ctx+"/system/getPrinter",null,
//						function(data){
//							$("#printerdg").datagrid("loadData",validationData(data));
//						});
			}
			else{
				$.messager.show({
		            title:'错误提醒',
		            msg:"保存失败请重试，如果问题依然存在，请联系系统管理员！",
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
			}
		}
	});
}

/*
 * Client 增删改查
 * 
 */
function operatecolumn_Client(value, row, index){	
	return $('#cmenu_Client').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="modifyClient('+index+');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteClient('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>':'';//"<a href='#' class='easyui-linkbutton' name='operate' onclick='openModifyUserDialog()' ><i class='icon icon-edit1'></i>修改</a>&nbsp;&nbsp;"+
	//"<a name='operate' href='#' class='easyui-linkbutton' onclick='deleteUser()' ><i class='icon icon-remove'></i>删除</a>";
}

var sameIP=false
function checkIP(){
	if($('#hostip').textbox('isValid')){
		getJSON(window.localStorage.ctx+"/system/checkIP",
				{
					id : $("#id").val(),
					hostip : $("#hostip").val()
				},
				function(data){
					var json=validationDataAll(data);
			    	if(json.code==0){
			    		sameIP=false;
			    		
			    	}else{
			    		sameIP=true;
			    		$.messager.alert({title:'提示信息',msg:'IP已被占用！',border:'thin'});
			    	}	
				});
	}	
}	

//客户端注册——新建
function new_Client(){
	$('#common_dialog').dialog({
		title : '新建客户端',
		width : 360,
		height : 300,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/system/modifyClient',
		buttons:[{
			text:'保存',
			width:80,
			handler:function(){saveClient();}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

//根据设备查找客户端
function doSearchClientByModality(newValue,oldValue){
	getJSON(window.localStorage.ctx+"/system/getClient",
			{
				value : $('#clientName_search').val(),
				modalityid : newValue
			},
			function(data){
				$("#clientdg").datagrid("loadData",validationData(data));
			});
}
//根据名称查找客户端
function doSearchClient(value){
	getJSON(window.localStorage.ctx+"/system/getClient",
			{
				value : value,
				modalityid : $('#modalityid_search').combobox('getValue')
			},
			function(data){
				$("#clientdg").datagrid("loadData",validationData(data));
			});
	/*$.getJSON(window.localStorage.ctx+"/system/getClient?value="+value, function(json){
		$("#clientdg").datagrid("loadData",validationData(json));
    });*/
}

//删除客户端
function deleteClient(index){
	$('#clientdg').datagrid('selectRow',index);
	var row=$('#clientdg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除选中的数据吗？',
			fn: function(r){
				if (r){
					getJSON(window.localStorage.ctx+"/system/deleteClient",
						{
							id : row.id
						},
						function(data){
							var json=validationData(data);
					    	if(json.code==0){
					    		var index=$("#clientdg").datagrid('getRowIndex',row);
					    		$("#clientdg").datagrid('deleteRow',index);
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
						}
					);
				}
			}
		});
	}
	else{
		$.messager.show({
            title:'提醒',
            msg:"请选择一份数据！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
}
		
function modifyClient(para){
		$('#clientdg').datagrid('selectRow',para);
		var row=$('#clientdg').datagrid('getSelected');
		if(row){
			$('#common_dialog').dialog({
				title : '编辑客户端',
				width : 360,
				height : 260,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				border: 'thin',
				href : window.localStorage.ctx+'/system/modifyClient?id='+row.id,
				buttons:[{
					text:'保存',
					width:80,
					handler:function(){saveClient();}
				},{
					text:'关闭',
					width:80,
					handler:function(){$('#common_dialog').dialog('close');}
				}]
			});
		}
		else{
		$.messager.show({
	        title:'提醒',
	        msg:"请选择一份数据！",
	        timeout:3000,
	        border:'thin',
	        showType:'slide'
	    });
	}
}

//保存
function saveClient(){
	$('#clientform').form('submit', {
		url: window.localStorage.ctx+"/system/saveClient",
		onSubmit: function(){
			if(sameIP){
				$.messager.show({
		            title:'错误提醒',
		            msg:'该IP已被占用',
		            timeout:3000,
		            border: 'thin',
		            showType:'slide'
		        });
				return false;
			}
			return $(this).form('validate');
		},
		success: function(data){
			var json=validationData(data);
			if(json.code==0){
				$('#common_dialog').dialog('close');
				getJSON(window.localStorage.ctx+"/system/getClient",null,
						function(data){
							var json=validationData(data);
							$("#clientdg").datagrid("loadData",json);
						});
			}
			else{
				$.messager.show({
		            title:'错误提醒',
		            msg:"保存失败请重试，如果问题依然存在，请联系系统管理员！",
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
			}
		}
	});
}
function modalityid(record){
	 
	   if (record.code =="Exam"){
		   $('#modalityid').combobox('enable');
	   }else{
		   
		   $('#modalityid').combobox('disable');   
	   }
}

function risEnvents(checked,name){
	
	console.log(checked)
	console.log(name)
	
	getJSON(window.localStorage.ctx+"/system/risEnvents",
			{
				name : name,
				on_off:checked
			},
			function(data){
				var json=validationData(data);
		    	if(json.code==0){
		    		
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
			}
		);
}

function operatecolumn_task(value, row, index){
	var operate=row.isstarted?'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="startTask(\''+row.schedulerid+'\',false);"><span class="l-btn-left"><span class="l-btn-text">停止</span></span></a>&nbsp;&nbsp;':
		'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="startTask(\''+row.schedulerid+'\',true);"><span class="l-btn-left"><span class="l-btn-text">启动</span></span></a>&nbsp;&nbsp;';
	
	operate+='<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="modifyTask(\''+row.schedulerid+'\',\''+row.cron+'\');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;&nbsp;';
	
	if(row.copy){
		operate+='<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="copyTask(\''+row.schedulerid+'\');"><span class="l-btn-left"><span class="l-btn-text">复制</span></span></a>&nbsp;&nbsp;'
	}
	return operate;
}

function startcolumn_task(value, row, index){	
	return value?'<span style="color:#0f0">启动</span>':'<span style="color:#f00">停止</span>';
}

function daemoncolumn_task(value, row, index){
	return value?'<span style="color:#0f0">是</span>':'<span style="color:#f00">否</span>';
}

function startTask(schedulerid,start){
	getJSON(window.localStorage.ctx+"/system/startTask",
		{
			schedulerid : schedulerid,
			start : start
		},
		function(data){
			var json=validationData(data);  
	    	if(json.code==0){
	    		refreshTask();
	    	}
	    	else{
	    		$.messager.show({
	                title:'错误',
	                msg:"操作失败，请重试，如果问题依然存在，请联系系统管理员！",
	                timeout:3000,
	                border:'thin',
	                showType:'slide'
	            });
	    	}
		}
	);
}

function refreshTask(){
	$('#taskdg').datagrid('reload');
	
}

function copyTask(schedulerid){
	$.messager.prompt({
		title: '输入',
		msg: '请输入拷贝分数:',
		border: 'thin',
		fn: function(r){
			if (r){
				if(!isNaN(r)&&r>0){
					getJSON(window.localStorage.ctx+"/system/copyTask",
						{
							schedulerid : schedulerid,
							num : r
						},
						function(data){
							var json=validationData(data);
					    	if(json.code==0){
					    		refreshTask();
					    	}
					    	else{
					    		$.messager.show({
					                title:'错误',
					                msg:"拷贝失败，请重试，如果问题依然存在，请联系系统管理员！",
					                timeout:3000,
					                border:'thin',
					                showType:'slide'
					            });
					    	}
						}
					);
				}
			}
		}
	});
}

function modifyTask(schedulerid,cron){
	$('#common_dialog').dialog({
		title : '编辑定时任务',
		width : 360,
		height : 180,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/system/goEditTask?cron='+cron,
		buttons:[{
			text:'保存',
			width:80,
			handler:function(){saveModifyTask(schedulerid);}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

function saveModifyTask(schedulerid){
	$('#taskform').form('submit', {
		url: window.localStorage.ctx+"/system/saveTask?schedulerid="+schedulerid,
		onSubmit: function(){
			return $(this).form('validate');
		},
		success: function(data){
			var json=validationData(data);
			if(json.code==0){
				$('#common_dialog').dialog('close');
				refreshTask();
			}
			else{
				$.messager.show({
		            title:'错误提醒',
		            msg:"保存失败请重试，如果问题依然存在，请联系系统管理员！",
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
			}
		}
	});
}


//打印模板最后一列操作按钮（修改和删除）
function operatecolumn_printtemplate(value, row, index){
	return $('#cmenu_printtemplate').html()?
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deletePrinttemplate('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>':'';
}

function doSearchPrinttemplate(value){		
	getJSON(window.localStorage.ctx+"/system/findPrinttemplate?",
			{
				template_name : value
			},
			function(json){$("#printtemplatedg").datagrid("loadData",validationData(json));}
			);
}
//操作里面修改，判断选中的是修改检查单or打印模板or预约单
/*function judgeCheckOrPrint(getindex){
	var rows = $('#printtemplatedg').datagrid("getRows");
	var row = rows[getindex];
	var type = row.type;
	if(type == 'reporttemplate'){
		goToEditPrinttemplate(getindex,'modify');
	}
	else if(type == 'checklisttemplate'){
		goToEditChecklist(getindex,'modify');
	}
	else if(type == 'reservationtemplate'){
		goToEditReservation(getindex,'modify');
	}
}*/

//右键修改，判断是修改检查预约单or打印模板or预约单
/*function rightClickJudgeCheckOrPrint(){
	var row = $('#printtemplatedg').datagrid("getSelected");
	var type = row.type;
	var rightindex = $('#printtemplatedg').datagrid("getRowIndex",row);
	if(type == 'reporttemplate'){
		goToEditPrinttemplate(rightindex,'modify');
	}
	else if(type == 'checklisttemplate'){
		goToEditChecklist(rightindex,'modify');
	}
	else if(type == 'reservationtemplate'){
		goToEditReservation(rightindex,'modify');
	}
}*/
//打开编辑预约单
/*function goToEditReservation(index, operate){
	var href = window.localStorage.ctx+'/system/goToEditReservation';
	if(index || index == 0 || operate == "modify" ){
		if(index || index == 0){
			$('#printtemplatedg').datagrid('selectRow',index);
		}
		var row=$('#printtemplatedg').datagrid('getSelected');
		if(row){
			href += "?id="+row.id; 
		}else{
			$.messager.show({
	            title:'提醒',
	            msg:"请选择一份数据！",
	            timeout:3000,
	            border: 'thin',
	            showType:'slide'
	        });
			return;
		}
	}
	$('#common_dialog').dialog({
		title : '编辑预约单模板',
		width : 680,
		height : 500,
		resizable: false,
		minimizable: false,
		maximizable: false,
		border: 'thin',
		href : href,
		modal : true,
		buttons:[{
			text:'保存',
			width:80,
			handler:function(){savePrinttemplate();}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}*/

//打开编辑检查单
/*function goToEditChecklist(index, operate){
	var href = window.localStorage.ctx+'/system/goToEditChecklist';
	if(index || index == 0 || operate == "modify" ){
		if(index || index == 0){
			$('#printtemplatedg').datagrid('selectRow',index);
		}
		var row=$('#printtemplatedg').datagrid('getSelected');
		if(row){
			href += "?id="+row.id; 
		}else{
			$.messager.show({
	            title:'提醒',
	            msg:"请选择一份数据！",
	            timeout:3000,
	            border: 'thin',
	            showType:'slide'
	        });
			return;
		}
	}
	$('#common_dialog').dialog({
		title : '编辑检查单模板',
		width : 680,
		height : 500,
		resizable: false,
		minimizable: false,
		maximizable: false,
		border: 'thin',
		href : href,
		modal : true,
		buttons:[{
			text:'保存',
			width:80,
			handler:function(){savePrinttemplate();}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}*/
//打开编辑打印模板界面
function goToEditPrinttemplate(index, operate){
	var href = window.localStorage.ctx+'/system/goToEditPrinttemplate';
	if(index || index == 0 || operate == "modify" ){
		if(index || index == 0){
			$('#printtemplatedg').datagrid('selectRow',index);
		}
		var row=$('#printtemplatedg').datagrid('getSelected');
		if(row){
			href += "?id="+row.id; 
		}else{
			$.messager.show({
	            title:'提醒',
	            msg:"请选择一份数据！",
	            timeout:3000,
	            border: 'thin',
	            showType:'slide'
	        });
			return;
		}
	}
	$('#common_dialog').dialog({
		title : '编辑报告模板',
		width : 680,
		height : 500,
		resizable: false,
		minimizable: false,
		maximizable: false,
		border: 'thin',
		href : href,
		modal : true,
		buttons:[{
			text:'保存',
			width:80,
			handler:function(){savePrinttemplate();}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

//保存打印模板
function savePrinttemplate(){
	$('#printtemplateform').form('submit', {
		url: window.localStorage.ctx+"/system/savePrinttemplate",
		onSubmit: function(param){
			var rows=$('#examitemdg').datagrid('getChecked');
			var idsstr="";
			for(var i=0;i<rows.length;i++){
				idsstr+=rows[i].id+",";
			}
			if(idsstr!="")
				idsstr=idsstr.substring(0,idsstr.length-1);
			param.idsstr = idsstr;
			return $(this).form('validate');
		},
		success: function(data){
			var json=validationData(data);
			if(json.code==0){
				$('#common_dialog').dialog('close');
				$('#printtemplatedg').datagrid('reload');
			}
			else{
				$.messager.show({
		            title:'错误提醒',
		            msg:"保存失败请重试，如果问题依然存在，请联系系统管理员！",
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
			}
		}
	});
}
//删除打印模板
function deletePrinttemplate(index){
	var ids='';
	if(index || index==0){
		$('#printtemplatedg').datagrid('selectRow',index);
		var row=$('#printtemplatedg').datagrid('getSelected');
		ids=(row.id+'');
	} else{
		var rows=$('#printtemplatedg').datagrid('getChecked');
		for(var i=0;i<rows.length;i++){
			ids+=rows[i].id+",";
		}
		if(ids!=''){
			ids=ids.substring(0,ids.length-1);
		}
	}
	if(ids.length==0){
		_message('请选择项目！');
		return ;
	}
	$.messager.confirm({
		title: '确认删除',
		ok: '是',
		cancel: '否',
		border:'thin',
		msg: '确认删除选中的项目吗？',
		fn: function(r){
			if (r){
				 getJSON(window.localStorage.ctx+"/system/deletePrinttemplate",
						 {
					 		ids : ids
						 },
						 function(data){
						 	var json=validationData(data);
					    	if(json.code==0){
					    		$('#printtemplatedg').datagrid('reload');
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
//获取打印模板对应的检查项目
function getExamitemdg(modality_type,itemname){
	if(!modality_type){
		modality_type = $('#modality_type').combobox('getValue');
	}
	if(!modality_type){
		return;
	}
	getJSON(window.localStorage.ctx+"/system/getExamitemByPrinttemplate?",
		{
			modality_type : modality_type,
			itemname : itemname
		},
		function(json){
			//console.log(json)
			$("#examitemdg").datagrid("loadData",validationData(json));
		}
	);
}

function operatecolumn_urgentexplain(value, row, index){
	return '<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="judgeCheckOrPrint('+index+');"><span class="l-btn-left"><span class="l-btn-text">危急值上报</span></span></a>&nbsp;&nbsp;'+
	'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deletePrinttemplate('+index+');"><span class="l-btn-left"><span class="l-btn-text">置为已处理</span></span></a>';
}

function refreshTask1(){
	var data = {total:"2",rows:[{a:'张三',b:'00001009',c:'CT00002010',d:'',e:'否',f:''},{a:'李四',b:'00001010',c:'CT00002011',d:'危急值情况说明',e:'是',f:'2018-12-12 16:43:53',g:'未处理'}]};
	$('#taskdg').datagrid('loadData', data);
}


//报告纠错操作(编辑和删除)
function operatecolumn_CheckError(value, row, index){	
	return $('#menu_CheckError').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="modifyCorrectRule('+row.id+');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteCheckErrorRule('+row.id+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>':'';
}

//新建规则
function new_CheckErrorRule(){
	$('#common_dialog').dialog({
				title : '新建报告纠错规则',
				width : 500,
				height : 450,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				border: 'thin',
				href : window.localStorage.ctx+'/system/toEditReportCorrectRules',
				buttons:[{
					text:'保存',
					width:80,
					handler:function(){saveReportCorrectRule();}
				},{
					text:'关闭',
					width:80,
					handler:function(){$('#common_dialog').dialog('close');}
				}]
		});
}

function saveReportCorrectRule(){
	$('#checkerrorform').form('submit', {
		url: window.localStorage.ctx+"/system/saveReportCorrectRule",
		onSubmit: function(){
			return $(this).form('validate');
		},
		success: function(data){
			var json = validationData(data);
			if(json.code==0){
				$("#checkerrordg").datagrid("reload");
				$('#common_dialog').dialog('close');
			}
			else{
				_message('保存失败请重试，如果问题依然存在，请联系系统管理员！','错误提醒');
			}
		}
	});
}


//修改报告纠错规则
function modifyCorrectRule(id){
		$('#common_dialog').dialog({
			title : '编辑纠错规则',
			width : 500,
			height : 450,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/system/toEditReportCorrectRules?id='+id,
			buttons:[{
				text:'保存',
				width:80,
				handler:function(){saveReportCorrectRule();}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}]
		});
}

//删除报告纠错规则
function deleteCheckErrorRule(id,index){
		$.messager.confirm({
			title: '确认删除',
			border:'thin',
			msg: '确认删除选中的数据吗？',
			fn: function(r){
				if (r){
					getJSON(window.localStorage.ctx+"/system/deleteCorrectRule",
						{
							id : id
						},
						function(data){
							var json=validationData(data);
					    	if(json.code==0){
//					    		var index=$("#checkerrordg").datagrid('getRowIndex',row);
					    		$("#checkerrordg").datagrid('deleteRow',index);
					    	}
					    	else{
					    		_message('删除失败，请重试，如果问题依然存在，请联系系统管理员！','错误');
					    	}
						}
					);
				}
			}
		});
	
}


function doSearchCheckRule(value){
	getJSON(window.localStorage.ctx+"/system/getReportCheckErrorRules",
		{
			keyword : value
		},
		function(data){
			$("#checkerrordg").datagrid("loadData",validationData(data));
		});
}


function passwordPolicyConfig(checked, name){
	if(name == "enable_complex_pwd"){
		enablePolicySwitchbutton(checked);
	}
	var policy = "0";
	if(name == "password_length"){
		policy = checked;
	}else if(checked){
		policy = "1";
	}

	getJSON(window.localStorage.ctx+"/syscode/passwordPolicyConfig",
			{
				name : name,
				policy: policy
			},
			function(data){
				var json=validationData(data);
		    	if(json.code==0){
		    		
		    	}
		    	else{
		    		$.messager.show({
		                title:'错误',
		                msg:"失败，请重试，如果问题依然存在，请联系系统管理员！",
		                timeout:3000,
		                border:'thin',
		                showType:'slide'
		            });
		    	}
			}
		);
}

function enablePolicySwitchbutton(checked){
	if(checked){
		$('#password_length').numberbox('enable');
		$('#contain_letter_pwd').switchbutton('enable');
		$('#contain_case_pwd').switchbutton('enable');
		$('#contain_digit_pwd').switchbutton('enable');
	}else{
		$('#password_length').numberbox('disable');
		$('#contain_letter_pwd').switchbutton('disable');
		$('#contain_case_pwd').switchbutton('disable');
		$('#contain_digit_pwd').switchbutton('disable');
	}
}