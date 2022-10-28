/**
 * 
 */

function openTaskDlg(row){
	$('#common_dialog').dialog({
		title : '编辑项目',
		width : 500,height : 320,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/research/editProject?Id='+(row!=null?row.id:""),
		buttons:[{
			text:'保存',
			width:80,
			handler:function(){saveProject();}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});

}

/*function openeformInfo(row){
	 var formrow=$("#experience_group_dg").datagrid("getSelected");
	 if(formrow==null){
		 $.messager.alert('提示','请先选择实验组！！');
		 return;
	 }
	$('#form_info_dialog').dialog({
		title : '表单信息',
		width : 500,height : 600,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/research/editformInfo?Id='+(row!=null?row.fi_id:""),
		buttons:[{
			text:'保存',
			width:80,
			handler:function(){saveFormInfo();}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#form_info_dialog').dialog('close');}
		}]
	});
}*/

function openeReport(){
	 var formrow=$("#experience_group_dg").datagrid("getSelected");
	 if(formrow==null){
		 $.messager.alert('提示','请先选择实验组！！');
		 return;
	 }
	$('#common_dialog').dialog({
		title : '报告信息',
		width : 1100,height : 700,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/research/openReport',
		buttons:[{
			text:'确定',
			width:80,
			handler:function(){choicePrescription();}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}],
		onLoad:function(){
			var pg = $("#dg").datagrid("options");
			$("#dg").datagrid("loading");
			getJSON(window.localStorage.ctx+"/research/searchReport",{'page':pg.pageNumber,'rows':pg.pageSize}, function(json){
		    	 $('#dg').datagrid('loadData',{"total" : json.data.total,"rows" : json.data.rows});
		    	 $("#dg").datagrid("loaded");
		    });
			
			//分页事件
			$('#dg').datagrid('getPager').pagination({
				     onSelectPage:function(pageNumber, pageSize){
				    	    $("#dg").datagrid("loading");
				    	    var dateType=$("#dateType").combobox("getValue");
							var strTime=$("#strTime").datebox("getValue");
							var endTime=$("#endTime").datebox("getValue");
							var examitemName=$("#examitemName").combobox("getText");
							var templateName=$("#templateName").combobox("getText");
							var peopleType=$("#peopleType").combobox("getValue");
							var people=$("#people").textbox("getValue");
							getJSON(window.localStorage.ctx+"/research/searchReport",
									{
								     'page':pageNumber,
								     'rows':pageSize,
								     'dateType':dateType,
								     'strTime':strTime,
								     'endTime':endTime,
								     'examitemName':examitemName,
								     'templateName':templateName,
								     'peopleType':peopleType,
								     'people':people
								    }, function(json){
						    	 $('#dg').datagrid('loadData',{"total" : json.data.total,"rows" : json.data.rows});
						    	 $("#dg").datagrid("loaded");
						    });
				     }
		    });
		}
	});
}




function saveProject(){
	$('#editprojectform').form('submit', {
		url: window.localStorage.ctx+"/research/saveProject",
		onSubmit: function(param){
			return $('#editprojectform').form('validate');
		},
		success: function(data){
			 var data = validationDataAll(data);
			 if (data.code==0){
				 _message('保存成功！');
				 findProject('');
				 $('#common_dialog').dialog('close'); 
		     }else{
		    	 _message('保存失败，请重试，如果问题依然存在，请联系系统管理员！','错误');
		     }
		}
	});
}


function findProject(value){
	getJSON(window.localStorage.ctx+"/research/searchProject",{'projectname':value}, function(json){
    	$("#taskMaintaindg").datagrid("loadData",json);
    });
}

function findTaskForm(){
	var taskName=$("#taskName").searchbox("getValue");
	getJSON(window.localStorage.ctx+"/research/searchTaskForm",{'taskName':taskName}, function(json){
    	$("#taskFormDg").datagrid("loadData",json);
    });
}

function saveFormInfo(){
	var rows=$("#taskFormDg").datagrid("getSelections");
	var form_info_dgRows=$("#form_info_dg").datagrid("getRows");
	var str="";
	for (var i = 0; i < rows.length; i++) {
		for (var j = 0; j < form_info_dgRows.length; j++) {
			if(rows[i].id==form_info_dgRows[j].task_formid){
				if(str==""){
					str="<"+form_info_dgRows[j].name+">";
				}else{
					str+=",<"+form_info_dgRows[j].name+">";
				}
				
			}
		}
	}
	if(str!=""){
		$.messager.alert("提示","表单名为"+str+"已经存在,请勿重复选择！！");
		return true;
	}
	
	var row=$("#experience_group_dg").datagrid("getSelected");
	
	getJSON(window.localStorage.ctx+"/research/saveFormInfo",{rows:JSON.stringify(rows),parentId:row.id},function(data){
		var data=validationData(data);
		 if (data.code==0){
			 $.messager.show({
	                title:'提示',
	                msg:"保存成功！",
	                timeout:3000,
	                border:'thin',
	                showType:'slide'
	            });
			 searchExgroupTaskform(null,row);
			 $('#form_info_dialog').dialog('close'); 
	     }else{
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

function searchExgroupTaskform(index,row){
	 getJSON(window.localStorage.ctx+"/research/searchExgroupTaskform",{experienceId:row.id,project_id:row.project_id},function(data){
		 	$("#form_info_dg").datagrid('loadData',data);
		 	if(row.xjbd!=null){
		 		document.getElementById("addFormBtn").style.display = "";
		 	}else{
		 		document.getElementById("addFormBtn").style.display = "none";
		 	}
		 	if(row.xzbg!=null){
		 		document.getElementById("addReportBtn").style.display = "";
		 	}else{
		 		document.getElementById("addReportBtn").style.display = "none";
		 	}
	 });
}


function taskMaintain_formatter(value, row, index){
	return  (row.bjkt!=null?'<a id="openTaskBtn" class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" type="hidden" onclick=" openModifyTaskDlg('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-edit"></i></span></span></a>&nbsp;&nbsp;':"")+
			(row.sckt!=null?'<a id="deleteTaskBtn" class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteTaskMaintain('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-delete"></i></span></span></a>&nbsp;&nbsp;':"");
}

function form_info_formatter(value, row, index){
	return (row.scbd!=null?'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteExgroupTaskformRelation('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-delete"></i></span></span></a>':"");
}


function taskMaintain_personnel(value, row, index){
	return '<a href="#" class="easyui-linkbutton" onclick="openModifyPersonnel('+index+');">添加</a>';
}

function personnel_formatter(value, row, index){
	return '<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteProjectMember('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-delete"></i></span></span></a>';
}

function role_personnel_formatter(value,row,index){
	return '<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteRolePersonnel('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-delete"></i></span></span></a>';
}

//function report_formatter(value, row, index){
//	return '<a href="#" class="easyui-linkbutton" onclick="openReportTemplate('+index+');">查看模板</a>';
//}
function joingroupItems_formatter(value, row, index){
	return '<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="previewSreport('+index+');"><span class="l-btn-left"><span class="l-btn-text">报告</span></span></a>';
}
//function taskFormDg_formatter(value, row, index){
//	return '<a href="#" class="easyui-linkbutton" onclick="openReportTemplate2('+index+');">查看模板</a>';
//}

//function openReportTemplate(index){
//	var rows = $("#dg").datagrid('getRows');//获得所有行
//	var row = rows[index];
//	$('#report_dialog').dialog({
//		title : '查看报告',
//		width : 730,height : 800,
//		resizable: false,minimizable: false,maximizable: false,modal : true,
//		border: 'thin',
//		href : window.localStorage.ctx+'/research/editTaskReport',
//		onLoad:function(){
//			var content=row.checkdesc_html;
//			content=content.replace(/ghidden/g,"hidden='hidden'");
//			content=content.replace(new RegExp("_ueditor_page_break_tag_","gm"),"<hr class='pagebreak' noshade='noshade' size='5' style='-webkit-user-select: none;'>")
//			$('#task_report').panel({content:content});
//		},
//		onBeforeDestroy : function(title, index) {
//    		$('#task_report').empty();
//		},
//		buttons:[{
//			text:'关闭',
//			width:80,
//			handler:function(){$('#report_dialog').dialog('close');}
//		}]
//	});
//}

function previewSreport(index){
	var rows = $("#reportOrderLinesDg").datagrid('getRows');//获得所有行
	var row = rows[index];
	getJSON(window.localStorage.ctx+"/research/findApplyItemsById",{report_id:row.report_id},function(data){
	 	var json=validationData(data);
		$('#report_dialog').dialog({
			title : '预览报告',
			width : 800,height : 800,
			resizable: false,minimizable: false,maximizable: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/research/viewReport',
			onLoad:function(){
				var content=json.html;
				content=content.replace(/ghidden/g,"hidden='hidden'");
				content=content.replace(new RegExp("_ueditor_page_break_tag_","gm"),"<hr class='pagebreak' noshade='noshade' size='5' style='-webkit-user-select: none;'>")
				$('#task_report').panel({content:content});
			},
			onBeforeDestroy : function(title, index) {
	    		$('#task_report').empty();
			},
			buttons:[{
				text:'关闭',
				width:80,
				handler:function(){$('#report_dialog').dialog('close');}
			}]
		});
	});
}
/*function openReportTemplate2(index){
	var rows = $("#taskFormDg").datagrid('getRows');//获得所有行
	var row = rows[index];
	$('#report_dialog').dialog({
		title : '查看报告',
		width : 730,height : 800,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/research/editTaskReport',
		onLoad:function(){
			var content=row.formcontent;
			content=content.replace(/ghidden/g,"hidden='hidden'");
			content=content.replace(new RegExp("_ueditor_page_break_tag_","gm"),"<hr class='pagebreak' noshade='noshade' size='5' style='-webkit-user-select: none;'>")
			$('#task_report').panel({content:content});
		},
		onBeforeDestroy : function(title, index) {
    		$('#task_report').empty();
		},
		buttons:[{
			text:'关闭',
			width:80,
			handler:function(){$('#report_dialog').dialog('close');}
		}]
	});
}*/

/*function reg_formula_dom(container,selector){
	console.log('reg_formula_dom')
	if(selector){
		selector+=' ';
	}
	
	var formulaData= container.data('formulaData');
	if(!formulaData){
		formulaData=new Map();
	}
	container.find((selector||'')+"input[formula]").each(function(index,obj){
		//console.log(obj)
		var formula =$(obj).attr('formula');
		if(formula){
			var match = formula.match(/{.+?}/g);
			if(match){
				for(var i=0,len=match.length;i<len;i++){
					var id=match[i].substring(1,match[i].length-1);
					//console.log(id);
					var array=formulaData.get(id);
					if(!array){
						array=new Array();
					}
					array.push($(obj).attr('id'));
					formulaData.set(id,array);
				}
			}
		}
		
		obj.addEventListener("formula_calculate", function(e) {
			calculateFormula(e.target||e.currentTarget)
		});
		
	});
	console.log(formulaData)
	container.data('formulaData',formulaData);
}*/

function openModifyPersonnel(index){
	$('#taskMaintaindg').datagrid('selectRow',index);
	var row=$('#taskMaintaindg').datagrid('getSelected');
	if(row){
		openPersonnelDlg(row);
	}
	else{
		$.messager.show({
	        title:'提醒',
	        msg:"请选择一个项目！",
	        timeout:3000,
	        border: 'thin',
	        showType:'slide'
	    });
	}
}

function openPersonnelDlg(row){
	var taskrow=$("#taskMaintaindg").datagrid("getSelected");
	 if(taskrow==null){
		 $.messager.alert('提示','请先选择项目！！');
		 return;
	 }
	$('#personnel_dialog').dialog({
		title : '相关人员',
		width : 450,height : 400,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/research/editProjectMember?taskId='+row.id,
		buttons:[{
			text:'添加',
			width:80,
			handler:function(){saveProjectMember(row);}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#personnel_dialog').dialog('close');}
		}]
	});
}


function saveProjectMember(row){
	var personnel_id=$("#personnel").combobox("getValue");
	var personnelRole=$("#personnelRole").combobox("getValue");
	var rows=$("#personnel_dg").datagrid("getRows");
	if(!personnel_id){
		$.messager.show({
            title:'提示',
            msg:"请先选择要添加的人员！！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
		return;
	}
	if(!personnelRole){
		$.messager.show({
            title:'提示',
            msg:"请先选择要人员所属角色！！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
		return;
	}
	 getJSON(window.localStorage.ctx+"/research/saveProjectMember",{project_id:row.id,user_id:personnel_id,role_id:personnelRole},function(data){
	    	//alert("JSON Data: " + json.patientname);
		 	var json=validationData(data);
	    	if(json.code==0){
	    		$.messager.show({
	                title:'提示',
	                msg:"添加成功！",
	                timeout:3000,
	                border:'thin',
	                showType:'slide'
	            });
	    		$("#personnel").combobox("setValue","");
	    		$("#personnelRole").combobox("setValue","");
	    		searchPersonnel(row);
	    	}else if(json.code==1){
	    		$.messager.show({
	                title:'提示',
	                msg:json.message,
	                timeout:3000,
	                border:'thin',
	                showType:'slide'
	            });
	    		
	    	}
	    	else{
	    		$.messager.show({
	                title:'错误',
	                msg:"添加失败，请重试，如果问题依然存在，请联系系统管理员！",
	                timeout:3000,
	                border:'thin',
	                showType:'slide'
	            });
	    	}
	});
}

function deleteProjectMember(index){
	$('#personnel_dg').datagrid('selectRow',index);
	var row=$('#personnel_dg').datagrid('getSelected');
	var taskRow=$('#taskMaintaindg').datagrid('getSelected');
	 getJSON(window.localStorage.ctx+"/research/deleteProjectMember",{id:row.id},function(data){
	    	//alert("JSON Data: " + json.patientname);
		 	var json=validationData(data);
	    	if(json.code==0){
	    		$.messager.show({
	                title:'提示',
	                msg:"删除成功！",
	                timeout:3000,
	                border:'thin',
	                showType:'slide'
	            });
	    		searchPersonnel(taskRow);
	    	}
	    	else{
	    		$.messager.show({
	                title:'错误',
	                msg:"添加失败，请重试，如果问题依然存在，请联系系统管理员！",
	                timeout:3000,
	                border:'thin',
	                showType:'slide'
	            });
	    	}
	 });
}

function searchPersonnel(row){
	 getJSON(window.localStorage.ctx+"/research/searchProjectMembers",{taskId:row.id},function(data){
	    	//alert("JSON Data: " + json.patientname);
		 	$("#personnel_dg").datagrid('loadData',data);
	 });
}

function openModifyTaskDlg(index){
	$('#taskMaintaindg').datagrid('selectRow',index);
	var row=$('#taskMaintaindg').datagrid('getSelected');
	if(row){
		openTaskDlg(row);
	}
	else{
		$.messager.show({
	        title:'提醒',
	        msg:"请选择一个项目！",
	        timeout:3000,
	        border: 'thin',
	        showType:'slide'
	    });
	}
}

function openFormInfoFormatterDlg(index){
	$('#form_info_dg').datagrid('selectRow',index);
	var row=$('#form_info_dg').datagrid('getSelected');
	if(row){
		openeformInfo(row);
	}
	else{
		$.messager.show({
	        title:'提醒',
	        msg:"请选择一个表单！",
	        timeout:3000,
	        border: 'thin',
	        showType:'slide'
	    });
	}
}


function deleteTaskMaintain(index){
	if(index){
		$('#taskMaintaindg').datagrid('selectRow',index);
	}
	var row=$('#taskMaintaindg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除选中的项目吗？',
			fn: function(r){
				if (r){
					 getJSON(window.localStorage.ctx+"/research/deleteProject",{id:row.id},function(data){
					    	//alert("JSON Data: " + json.patientname);
						 	var json=validationData(data);
					    	if(json.code==0){
					    		var index=$("#taskMaintaindg").datagrid('getRowIndex',row);
					    		$("#taskMaintaindg").datagrid('deleteRow',index);
					    		$('#experience_group_dg').datagrid('loadData',{rows:[]});
					    		$('#form_info_dg').datagrid('loadData',{rows:[]});
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
            msg:"请选择一个项目！",
            timeout:3000,
            border: 'thin',
            showType:'slide'
        });
	}
}

function deleteExperienceGroup(index){
	if(index){
		$('#experience_group_dg').datagrid('selectRow',index);
	}
	var row=$('#experience_group_dg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除选中的实验组吗？',
			fn: function(r){
				if (r){
					 getJSON(window.localStorage.ctx+"/research/deleteExperienceGroup",{id:row.id},function(data){
					    	//alert("JSON Data: " + json.patientname);
						 	var json=validationData(data);
					    	if(json.code==0){
					    		var index=$("#experience_group_dg").datagrid('getRowIndex',row);
					    		$("#experience_group_dg").datagrid('deleteRow',index);
					    		$('#form_info_dg').datagrid('loadData',{rows:[]});
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
            msg:"请选择一个实验组！",
            timeout:3000,
            border: 'thin',
            showType:'slide'
        });
	}
}

function deleteExgroupTaskformRelation(index){
	if(index){
		$('#form_info_dg').datagrid('selectRow',index);
	}
	var row=$('#form_info_dg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除选中的表单吗？',
			fn: function(r){
				if (r){
					 getJSON(window.localStorage.ctx+"/research/deleteExgroupTaskformRelation",{id:row.etr_id},function(data){
					    	//alert("JSON Data: " + json.patientname);
						 	var json=validationData(data);
					    	if(json.code==0){
					    		var index=$("#form_info_dg").datagrid('getRowIndex',row);
					    		$("#form_info_dg").datagrid('deleteRow',index);
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
            msg:"请选择一个表单！",
            timeout:3000,
            border: 'thin',
            showType:'slide'
        });
	}
}


function openProjectRoleDlg(row){
	$('#taskRole_dialog').dialog({
		title : '编辑角色',
		width : 450,height : 490,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/research/editTskRole?id='+(row!=null?row.id:""),
		buttons:[{
			text:'保存',
			width:80,
			handler:function(){saveRolePersonnel();}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#taskRole_dialog').dialog('close');}
		}]
	});
	if(row!=null){
		getJSON(window.localStorage.ctx+"/research/searchTaskPersonnel",{'roleId':row.id}, function(json){
	    	$("#role_personnel_dg").datagrid("loadData",json);
	    });
	}
}

function projectRole_formatter(value, row, index){
	return (row.edit_project_role!=null?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick=" openModifyProjectRole('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-edit"></i></span></span></a>&nbsp;&nbsp;':"")+
		   (row.delete_project_role!=null?'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteProjectRole('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-delete"></i></span></span></a>':"");
}

function deleteProjectRole(index){
	if(index){
		$('#taskRoledg').datagrid('selectRow',index);
	}
	var row=$('#taskRoledg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除选中的角色吗？',
			fn: function(r){
				if (r){
					 getJSON(window.localStorage.ctx+"/research/deleteProjectRole",{id:row.id},function(data){
					    	//alert("JSON Data: " + json.patientname);
						 	var json=validationData(data);
					    	if(json.code==0){
					    		$.messager.show({
					                title:'提示',
					                msg:"删除成功！",
					                timeout:3000,
					                border:'thin',
					                showType:'slide'
					            });
					    		searchTaskRole(null);
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
		_message("请选择一个项目！");
	}
}

function openModifyProjectRole(index){
	$('#taskRoledg').datagrid('selectRow',index);
	var row=$('#taskRoledg').datagrid('getSelected');
	if(row){
		openProjectRoleDlg(row);
	}
	else{
		_message("请选择一个角色！");
	}
}

function addTaskRolePersonnel(){
	var value=$("#RolePersonnel").combobox("getValue");
	var text=$("#RolePersonnel").combobox("getText");
	$('#role_personnel_dg').datagrid('appendRow',{
	    id: value,
	    name: text
	});
}
function deleteRolePersonnel(index){
	$('#role_personnel_dg').datagrid('deleteRow',index);
}
function saveRolePersonnel(){
	 var rows=$("#task_jurisdiction_dg").datagrid("getChecked");
	 rows=JSON.stringify(rows);
	 var role_name=$("#role_name").textbox("getValue");
	 var describe=$("#describe").textbox("getValue");
	 var user_level=$("#user_level").textbox("getValue");
	 var taskRoleId=$("#taskRoleId").val();
	 getJSON(window.localStorage.ctx+"/research/saveProjectRole",{rows:rows,role_name:role_name,describe:describe,taskRoleId:taskRoleId,user_level:user_level},function(data){
	    	//alert("JSON Data: " + json.patientname);
		 	var json=validationData(data);
			 if (data.code==0){
				 $.messager.show({
		                title:'提示',
		                msg:"保存成功！",
		                timeout:3000,
		                border:'thin',
		                showType:'slide'	
		            });
				 searchTaskRole(null);
				 $('#taskRole_dialog').dialog('close'); 
		     }else{
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

function searchTaskRole(value){
	getJSON(window.localStorage.ctx+"/research/searchProjectRole",{'role_name':value}, function(json){
    	$("#taskRoledg").datagrid("loadData",json);
    });
}

function openReportApproval(){
	var row=$("#reportApprovaldg").datagrid("getSelections");
	if(row.length==0){
		_message("请选择要提交的数据！！");
		return;
	}
	for (var i = 0; i < row.length; i++) {
		if(row[i].status==2){
			_message("已通过的数据不能提交！！");
			return;
		}
	}
	$("#reportPrescription").datebox("setValue",row.valid_period);
	$("#note").textbox("setValue","");
	$("#reportApprovalDlg").dialog("open");
}

function approveApplyForJoinGroup(){
	var reportPrescription=$("#reportPrescription").datebox("getValue"); 
	var note=$("#note").textbox("getValue");
	var rows=$("#reportApprovaldg").datagrid("getSelections");
	getJSON(window.localStorage.ctx + "/research/approveApplyForJoinGroup", {
		'reportPrescription' : reportPrescription,
		'datas' : JSON.stringify(rows),
		'note' : note
	}, function(data) {
		var json = validationData(data);
		if (json.code == 0) {
			_message("申请成功！");
			searchReportOrder();
			$("#reportApprovalDlg").dialog("close");
		}
	});
}
function submitReport(){
	var reportPrescription=$("#reportPrescriptionTime").datebox("getValue");
	var taskRow=$("#taskMaintaindg").datagrid("getSelected");
	var exRow=$("#experience_group_dg").datagrid("getSelected");
	if(reportPrescription==null||reportPrescription==""){
		$.messager.alert('提醒','请选择有效期！');
		return;
	}
	var rows=$("#dg1").datagrid("getRows");
	if(rows==null||rows.length==0){
		$.messager.alert('提醒','请选择要申请的报告！');
		return;
	}
	getJSON(window.localStorage.ctx+"/research/submitReport",
	              {'reportPrescription':reportPrescription,
		                        'datas':JSON.stringify(rows),
		                        'status':'1',
		                        'task_id':taskRow.id,
		                        'experience_group_id':exRow.id},
	function(data){
		var json=validationData(data);
		if(json.code==0){
			$.messager.show({
	            title:'提示',
	            msg:"申请成功！",
	            timeout:3000,
	            border:'thin',
	            showType:'slide'
	        });
			$("#reportPrescriptionDlg").dialog("close");
			$('#common_dialog').dialog('close');
		}else{
			 $.messager.show({
	                title:'错误',
	                msg:json.message,
	                timeout:3000,
	                border:'thin',
	                showType:'slide'
	            });
		}
    });
}
function openJoingroupApplyDetailsDlg(index){
	 $("#reportApprovaldg").datagrid("clearChecked");
	 $("#reportApprovaldg").datagrid("selectRow",index);
	 var row=$("#reportApprovaldg").datagrid("getSelected");
	 $('#reportorderlines_dialog').dialog({
		title : '申请明细',
		width : 900,height : 600,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/research/openJoingroupApplyDetailsDlg?report_order_id='+(row!=null?row.id:""),
		buttons:[{
			text:'关闭',
			width:80,
			handler:function(){$('#reportorderlines_dialog').dialog('close');}
		}]
	 });
}