/**
 * 
 */

function operatecolumn_worktime(value, row, index){
	return '<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteWorktime('+index+','+row.id+',\''+row.name+'\');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>';
}

function deleteWorktime(index,id,name){
	$('#worktimedg').datagrid('selectRow',index);
	if(id){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除 '+name+' 吗？',
			fn: function(r){
				if (r){
					 $.getJSON(window.localStorage.ctx+"/workforce/deleteWorktime?id="+id, function(data){
						 	var json=validationData(data);
					    	if(json.code==0){
					    		newWorktime();
								$("#worktimedg").datagrid("reload");
								$("#shiftworktimedg").datagrid("reload");
								_message("删除成功！","提示");
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
}

function selectOneWorktime(row){
	$('#worktimename').textbox('setValue',row.name);
	$('#starttime').timespinner('setValue',row.starttime);
	$('#endtime').timespinner('setValue',row.endtime);
	$('#worktimeid').val(row.id);
}

function newWorktime(){
	$('#worktimename').textbox('setValue','');
	$('#starttime').timespinner('setValue','08:00');
	$('#endtime').timespinner('setValue','11:59');
	$('#worktimeid').val('');
}

function saveWorktime(){
	$('#worktimeform').form('submit', {
		url: window.localStorage.ctx+"/workforce/saveWorktime",
		onSubmit: function(){
			if(!$(this).form('validate')){
				return false;
			}
//			if($('#starttime').timespinner('getValue')>=$('#endtime').timespinner('getValue')){
//				_message("开始时间必须小于结束时间！");
//				return false
//			}
		},
		success: function(data){
			var json=validationData(data);
			if(json.code==0){
				newWorktime();
				$("#worktimedg").datagrid("reload");
				$("#shiftworktimedg").datagrid("reload");
			}
			else{
				_message("保存失败请重试，如果问题依然存在，请联系系统管理员！","错误提醒");
			}
		}
	});
}


function operatecolumn_deptshift(value, row, index){
	return '<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteDeptShift('+index+','+row.id+',\''+row.name+'\');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>';
}

function deleteDeptShift(index,id,name){
	$('#worktimedg').datagrid('selectRow',index);
	if(id){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除 '+name+' 吗？',
			fn: function(r){
				if (r){
					 getJSON(window.localStorage.ctx+"/workforce/deleteDeptShift",{id:id}, function(data){
						 	var json=validationData(data);
					    	if(json.code==0){
					    		newShift();
								$("#shiftdg").datagrid("reload");
					    	}
					    	else{
					    		_message("删除失败，请重试，如果问题依然存在，请联系系统管理员！","错误提醒");
					    	}
					 });
				}
			}
		});
	}
}

function newShift(){
	$('#shiftid').val("");
	$('#shiftworktimeids').val("");
	$('#shiftworktimes').val("");
	$('#shiftname').textbox("setValue","");
	$('#shift_type').combobox("setValue","");
	$('#shiftworktimedg').datagrid('clearChecked');
}

function selectOneShift(row){
	$('#shiftid').val(row.id);
	$('#shiftname').textbox("setValue",row.name);
	$('#shift_type').combobox("setValue",row.type);
	$('#shiftworktimedg').datagrid('clearChecked');
	getJSON(window.localStorage.ctx+"/workforce/getDeptShiftWorktime",{shiftid:row.id},function(json){
		$.each(json,function(index,value){
			var rows=$('#shiftworktimedg').datagrid('getRows');
			$.each(rows,function(index,r){
				if(r.id==value.worktimeid){
					$('#shiftworktimedg').datagrid('selectRow',index);
				}
			});
		});
	});
}

function saveShift(){
	$('#shiftform').form('submit', {
		url: window.localStorage.ctx+"/workforce/saveDeptShift",
		onSubmit: function(){
			if(!$(this).form('validate')){
				return false;
			}
			var rows=$('#shiftworktimedg').datagrid('getChecked')
			if(rows&&rows.length>0){
				var worktimeids="";
				var worktimes="";
				$.each(rows,function(index,row){
					worktimeids+=row.id+',';
					worktimes+=row.name+'('+row.starttime+' - '+row.endtime+'),';
				});
				if(worktimes)worktimes=worktimes.substr(0,worktimes.length-1);
				$('#shiftworktimeids').val(worktimeids);
				$('#shiftworktimes').val(worktimes);
			}
			else{
				_message("请选择时间段！");
				return false;
			}
		},
		success: function(data){
			var json=validationData(data);
			if(json.code==0){
				newShift();
				$("#shiftdg").datagrid("reload");
			}
			else{
				if(json.message=="-2"){
					_message("时间段重叠！","错误提醒");
				} else{
					_message("保存失败请重试，如果问题依然存在，请联系系统管理员！","错误提醒");
				}
			}
		}
	});
}


/**
 *  选中机构查找科室
 * @param newValue
 * @param oldValue
 * @returns
 */
//function doSearchDicDepartment_wf(newValue,oldValue) {
//	$('#dept_wf').combobox('setValue', '');
//	$("#dept_wf").combobox("reload",window.localStorage.ctx+"/dic/findDepartment?institutionid="+newValue+"&deleted=0");
//	
//	$('#worktimes_wf').combobox('setValue', '');
//	$('#worktimes_wf').combobox('loadData', '{}');
//}

function showShiftDetails(newValue,oldValue,week){
	if($('#dept_shift_dept_cb').combobox('getValue')&&$('#dept_shift_position_cb').combobox('getValue')){
		if(week){
			var offset=$('#dept_shift_thisweek').attr("week_offset");
			if(week=="lastweek"){
				offset=parseInt(offset)-1;
			} else if(week=="nextweek"){
				offset=parseInt(offset)+1;
			} else {
				offset="0";
			}
			
			$('#dept_shift_thisweek').attr("week_offset",offset);
			
		} else {
			$('#dept_shift_thisweek').linkbutton('select');
			$('#dept_shift_thisweek').attr('week_offset','0');
		}
		$('#panel_wf').panel('refresh',window.localStorage.ctx+'/workforce/showShiftDetails?deptid='+$('#dept_shift_dept_cb').combobox('getValue')
				+'&postcode='+$('#dept_shift_position_cb').combobox('getValue')+'&postname='+$('#dept_shift_position_cb').combobox('getText')
				+'&monday='+$('#dept_shift_thisweek').attr("monday")+'&offset='+$('#dept_shift_thisweek').attr("week_offset"));
	}
	else{
		clearPanel_wf();
	}
}

function clearPanel_wf(){
	$('#panel_wf').panel('refresh',window.localStorage.ctx+'/workforce/showShiftDetails');
}

/**
 *  查找科室
 * @param value doSearchdepartment_shift
 * @returns
 */
function doSearchdepartment_shift() {
	getJSON(window.localStorage.ctx+"/dic/findDepartment",
		{
			institutionid: $('#institutionid_search_shift').combobox('getValue'),
			value : $('#departmentName_shift').val(),
			deleted : "0"
		},
		function(data){
			$("#shift_dept_worktime_dg").datagrid("loadData",validationData(data));
		});
}

function getDeptPostShifts(index , row){
	$('#dept_post_shift_layout').layout('panel','south').panel('setTitle','班次：'+row.name_zh);
	$("#dept_post_shift_dg").datagrid('clearSelections');
	getJSON(window.localStorage.ctx+"/workforce/getDeptPostShift",
		{
			deptid: $('#dept_post_shift_dept_cb').combobox('getValue'),
			postcode : row.code
		},
		function(data){
			var rows=$("#dept_post_shift_dg").datagrid('getRows');
			$.each(data,function(index,da){
				if(da.shiftid){
					for(var i=0;i<rows.length;i++){
						if(rows[i].id==da.shiftid){
							$("#dept_post_shift_dg").datagrid('selectRow',i);
							break;
						}
					}
				}
			});
		});
}

function saveDeptPostShift(){
	if(!$('#dept_post_shift_dept_cb').combobox('getValue')){
		_message("请选择科室！")
		return;
	}
	var postrow=$('#dept_post_shift_post_dg').datagrid('getSelected');
	if(!postrow){
		_message("请选择岗位！")
		return;
	}
	var shiftrows=$('#dept_post_shift_dg').datagrid('getSelections');
	if(!shiftrows){
		_message("请选择班次！")
		return;
	}
	var shiftids="";
	$.each(shiftrows,function(index,row){
		shiftids+=row.id+",";
	});
	getJSON(window.localStorage.ctx+"/workforce/saveDeptPostShift",
		{
			deptid: $('#dept_post_shift_dept_cb').combobox('getValue'),
			postcode : postrow.code,
			shiftids: shiftids
		},
		function(data){
			var json=validationData(data);
			if(json.code==0){
				_message("保存成功！");
			}
			else{
				_message("保存失败请重试，如果问题依然存在，请联系系统管理员！","错误提醒");
			}
		});
}

function selectAllEmployee(checked){
	$("#employees_list_div .easyui-checkbox").each(function(){
		if(checked){
			$(this).checkbox('check');
		} else {
			$(this).checkbox('uncheck');
		}
	});
}

function checkOverlaps_Physician(emid,td){
	var overlaps=false;
	var starttime1=td.attr("starttime");
	var endtime1=td.attr("endtime");

	$("td.drop[workdate='"+td.attr("workdate")+"'][shiftid!='"+td.attr("shiftid")+"'] ").each(function(index,obj){
		//console.log(obj);
		var hasemp=false;
		$(obj).find("a[emid="+emid+"]").each(function(index,a){
			hasemp=true;
		});
		if(hasemp){
			var starttime2=$(obj).attr("starttime");
			var endtime2=$(obj).attr("endtime");
			if(!((starttime1<starttime2&&endtime1<=starttime2)||(starttime2<starttime1&&endtime2<=starttime1))){
				overlaps=true
				return;
			}
		}
	});
	return overlaps;
}

function checkOverlaps_Technician(emid,td){
	var overlaps=false;
	$("td.drop[workdate='"+td.attr("workdate")+"'][shiftid='"+td.attr("shiftid")+"'][worktimeid='"+td.attr("worktimeid")+"'][modalityid!='"+td.attr("modalityid")+"'] ").each(function(index,obj){
		//console.log(obj);
		$(obj).find("a[emid="+emid+"]").each(function(index,a){
			overlaps=true;
		});
	});
	return overlaps;
}

function clearAllEmpl(){
	if(edititem_shift){
		$.messager.confirm({
			title: '确认清空',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认清空吗？',
			fn: function(r){
				if (r){
					var ids="";
					$(edititem_shift).find('.easyui-menubutton').each(function(index,a){
						ids+=$(a).attr("id")+",";
					});
					if(ids){
						getJSON(window.localStorage.ctx+"/workforce/removeEmployeeFromShift",
							{
								ids : ids
							},
							function(data){
								if(data.code==0){
									$(edititem_shift).empty();
									edititem_shift=null;
									$('#dept_shift_submit').linkbutton('enable');
								}
								else{
									_message("保存失败请重试，如果问题依然存在，请联系系统管理员！","错误提醒");
								}
							}
						);
					}
				}
			}
		});
	}
}

function removeEmployeeFromShift(){
	if(editemp_shift){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除吗？',
			fn: function(r){
				if (r){
					getJSON(window.localStorage.ctx+"/workforce/removeEmployeeFromShift",
						{
							ids : $(editemp_shift).attr("id")
						},
						function(data){
							if(data.code==0){
								$(editemp_shift).remove();
								//$(editemp_shift).menubutton('destroy');
								editemp_shift=null;
								$('#dept_shift_submit').linkbutton('enable');
							}
							else{
								_message("保存失败请重试，如果问题依然存在，请联系系统管理员！","错误提醒");
							}
						}
					);
				}
			}
		});
	}
	
}

function saveDeptShiftWork(userid,employeeid,td,str){
	getJSON(window.localStorage.ctx+"/workforce/saveDeptShiftWork",
		{
			userids : userid,
			employeeids : employeeid,
			postcode : $('#dept_shift_position_cb').combobox('getValue'),
			institutionid: $('#institutionid_wf').combobox('getValue'),
			deptid: $('#dept_shift_dept_cb').combobox('getValue'),
			shiftid : td.attr("shiftid"),
			shift_name : td.attr("shiftname"),
			worktimeid : td.attr("worktimeid"),
			worktime_name : td.attr("worktime_name"),
			workdate : td.attr("workdate"),
			modalityid : td.attr("modalityid")
		},
		function(data){
			if(data.code==0){
				var newstr="";
				for(var i=0,len=data.data.length;i<len;i++){
					var dom=$(str)[i];
					dom.setAttribute("id",data.data[i]);
					newstr+=dom.outerHTML;
				}
				td.append(newstr);
            	$.parser.parse(td);
            	$('#dept_shift_submit').linkbutton('enable');
			}
			else{
				_message("保存失败请重试，如果问题依然存在，请联系系统管理员！","错误提醒");
			}
		}
	);
}

function copyLastWeekShifts(){
	if(!$('#dept_shift_dept_cb').combobox('getValue')){
		_message("请选择科室！")
		return;
	}
	if(!$('#dept_shift_position_cb').combobox('getValue')){
		_message("请选择岗位！")
		return;
	}
	
	
	var offset=$('#dept_shift_thisweek').attr('week_offset');
	offset=parseInt(offset);
	
	if(offset<0){
		_message("请选择本周或之后的时间！")
		return;
	}
	
	$.messager.confirm({
		title: '确认',
		ok: '是',
		cancel: '否',
		border:'thin',
		msg: '确认引用上周的排班吗？',
		fn: function(r){
			if (r){
				getJSON(window.localStorage.ctx+"/workforce/copyLastWeekShifts",
					{
						postcode : $('#dept_shift_position_cb').combobox('getValue'),
						institutionid: $('#institutionid_wf').combobox('getValue'),
						deptid: $('#dept_shift_dept_cb').combobox('getValue'),
						monday: $('#dept_shift_thisweek').attr('monday'),
						offset: $('#dept_shift_thisweek').attr('week_offset')
					},
					function(data){
						if(data.code==0){
							var week="thisweek";
							if($('#dept_shift_lastweek').linkbutton('options').selected){
								week="lastweek";
							} else if($('#dept_shift_nextweek').linkbutton('options').selected){
								week="nextweek";
							}
							showShiftDetails(null,null,week);
							$('#dept_shift_submit').linkbutton('enable');
						}
						else{
							_message(data.message,"错误提醒");
						}
					}
				);
			}
		}
	});
}

function employeeShift(){
	console.log(edititem_shift);
	console.log(editemp_shift);
	
	$('#common_dialog').dialog(
			{
				title : '排班',
				width : 300,height : 300,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				border: 'thin',
				href : window.localStorage.ctx+'/workforce/goEmployeeShift?'
					+'deptid='+$('#dept_shift_dept_cb').combobox('getValue')
					+'&postcode='+$('#dept_shift_position_cb').combobox('getValue')
					+'&shiftid='+$(edititem_shift).attr("shiftid")
					+'&worktimeid='+$(edititem_shift).attr("worktimeid")
					+'&workdate='+$(edititem_shift).attr("workdate")
					+'&modalityid='+$(edititem_shift).attr("modalityid"),
				buttons:[{
					text:'保存',
					width:80,
					handler:function(){}
				},{
					text:'关闭',
					width:80,
					handler:function(){$('#common_dialog').dialog('close');}
				}]
		});
}

function clearShiftFiled(){
	$('#shiftform').form('clear');
}

function saveReportTaskSetting(attr,checked){
	getJSON(window.localStorage.ctx+"/workforce/saveReportTaskSetting",
			{
				id : $('#reporttasksettingid').val(),
				attr: attr,
				value: checked?"1":"0"
			},
			function(data){
				if(data.code==0){
					$('#reporttasksettingid').val(data.data);
				}
				else{
					_message(data.message,"错误提醒");
				}
			}
		);
}

function refreshPhysicianScore(){
	if(!$('#dept_rts').combobox('options').disabled){
		$('#panel_physician_score').panel('refresh',window.localStorage.ctx+'/workforce/showPhysicianScore?deptid='+$('#dept_rts').combobox('getValue'));
	}
}

function refreshPhysicianCount(){
	if(!$('#dept_rts_count').combobox('options').disabled){
		$('#panel_physician_count').panel('refresh',window.localStorage.ctx+'/workforce/showPhysicianCount?deptid='
			+$('#dept_rts_count').combobox('getValue')
			+'&bymodality='+$('#modality_adq').checkbox('options').checked);
	}
}

function submitShifts(){
	getJSON(window.localStorage.ctx+"/workforce/shangesTakeEffectImmediately",null,
		function(data){
			if(data.code==0){
				$('#dept_shift_submit').linkbutton('disable');
			}
			else{
				_message(data.message,"错误提醒");
			}
		}
	);
}

function doSearch_Physicians(){
	var name=$('#name_searcher').searchbox('getValue');
	var deptid=$('#dept_subscription_rule').combobox('getValue');
	$('#physicians_subscription_rule').datagrid('load',{
		deptid: deptid,
		name: name
	});
}

function showSubscriptionRule(index,row){
	$('#subscription_rule_modalitytype').datalist('unselectAll');
	$('#subscription_rule_modality').datalist('unselectAll');
	var root=$('#subscription_rule_examitem').tree('getRoot');
	if(root){
		$('#subscription_rule_examitem').tree('uncheck',root.target);
	}
	getJSON(window.localStorage.ctx+"/workforce/getSubscriptionRules",{userid:row.userid},
		function(data){
			console.log(data)
			if(data[0]){
				var modalities=data[0].modalities;
				if(modalities){
					var arr=modalities.split(',');
					var rows=$('#subscription_rule_modalitytype').datalist('getRows');
					for(var i=0;i<arr.length;i++){
						for(var j=0;j<rows.length;j++){
							if(arr[i]==rows[j].code){
								var index=$('#subscription_rule_modalitytype').datalist('getRowIndex',rows[j]);
								$('#subscription_rule_modalitytype').datalist('selectRow',index);
								continue;
							}
						}
					}
				}
			}
			if(data[1]){
				var eqs=data[1];
				var rows=$('#subscription_rule_modality').datalist('getRows');
				for(var i=0;i<eqs.length;i++){
					for(var j=0;j<rows.length;j++){
						if(eqs[i].modalityid==rows[j].id){
							var index=$('#subscription_rule_modality').datalist('getRowIndex',rows[j]);
							$('#subscription_rule_modality').datalist('selectRow',index);
							continue;
						}
					}
				}
			}
			if(data[2]){
				var items=data[2];
				for(var i=0;i<items.length;i++){
					console.log(items[i].itemid)
					var node = $('#subscription_rule_examitem').tree('find', items[i].itemid);
					if(node){
						$('#subscription_rule_examitem').tree('check', node.target);
					}
				}
			}
		}
	);
}

function saveSubscriptionRule(){
	var prow=$('#physicians_subscription_rule').datalist('getSelected');
	if(prow){
		var modality="",modalityid="",itemid="";
		var rows=$('#subscription_rule_modalitytype').datalist('getSelections');
		if(rows.length>0){
			for(var j=0;j<rows.length-1;j++){
				modality+=rows[j].code+",";
			}
			modality+=rows[rows.length-1].code;
		}
		
		rows=$('#subscription_rule_modality').datalist('getSelections');
		if(rows.length>0){
			for(var j=0;j<rows.length-1;j++){
				modalityid+=rows[j].id+",";
			}
			modalityid+=rows[rows.length-1].id;
		}
		
		var nodes = $('#subscription_rule_examitem').tree('getChecked');
		if(nodes.length>0){
			for(var j=0;j<nodes.length-1;j++){
				if(nodes[j].leaf=="1"){
					itemid+=nodes[j].id+",";
				}
			}
			itemid+=nodes[nodes.length-1].id;
		}
		
		getJSON(window.localStorage.ctx+"/workforce/saveSubscriptionRule",
			{
				userid:prow.userid,
				employeeid:prow.employeeid,
				modality:modality,
				modalityids:modalityid,
				itemids:itemid
			},
			function(data){
				if(data.code==0){
					_message("保存成功！");
				}
				else{
					_message("保存失败！","错误提醒");
				}
			}
		);
	} else{
		_message("请选择医生！","提示");
	}
}

