/**
 * 
 */
$(function(){
	
	
	
});


var sameGroupName=false;
function checkGroupName(newValue,oldValue){
	if($("#gpname").val()){
		getJSON(window.localStorage.ctx+"/dic/checkGroupName",
			{
				id:$("#groupid").val(),
				gpname:$("#gpname").val()
			},
			function(data){
			 	var json=validationData(data);
		    	if(json.code==0){
		    		sameGroupName=false;
		    		
		    	}else{
		    		sameGroupName=true;
		    		$("#gpname").textbox("setValue","");
		    		_alert('提示信息！','提示信息');
		    	} 	
		  });
	}	
}
function setstation(checked){
	var swb=$("#swb").val();
	var a;
	if (checked){
		a=0;
	}
	else{
		a=1;
	}
	$("#working_state").val(a);
	console.log($("#working_state").val());
}

//var correctTime=true;
function getWorktime(){
	var v1=$("#workday_of_worktime1").val(),
	v2=$("#workday_of_worktime2").val(),
	v3=$("#workday_of_worktime3").val(),
	v4=$("#workday_of_worktime4").val(),
	sa1=$("#saturday_of_worktime1").val(),
	sa2=$("#saturday_of_worktime2").val(),
	sa3=$("#saturday_of_worktime3").val(),
	sa4=$("#saturday_of_worktime4").val(),
	su1=$("#sunday_of_worktime1").val(),
	su2=$("#sunday_of_worktime2").val(),
	su3=$("#sunday_of_worktime3").val(),
	su4=$("#sunday_of_worktime4").val();
	if(v1&&v2&&v3&&v4){
		if(v2 > v1&&v4>v3){
			$("#workday_of_worktime").val(v1+'-'+v2+','+v3+'-'+v4);
		}else{
			_message('开始时间大于结束时间！','提示信息');
			return false;
		}	
	}
	else{
		_message('请选择工作时间！','提醒');
		return false;
	}
	if(sa1&&sa2&&sa3&&sa4){
		if(sa2 > sa1&&sa4>sa3){
			$("#saturday_of_worktime").val(sa1+'-'+sa2+','+sa3+'-'+sa4);
		}else{
			_message('周六工作时间中开始时间大于结束时间！','提示信息');
			return false;
		}
	}
	else if(!(!sa1&&!sa2&&!sa3&&!sa4)){
		_message('请填写完整的周六工作时间！','提醒');
		return false;
	}
	
	if(su1&&su2&&su3&&su4){
		if(su2 > su1&& su4 > su3){
			$("#sunday_of_worktime").val(su1+'-'+su2+','+su3+'-'+su4);
		}else{
			_message('周日工作时间中开始时间大于结束时间！','提示信息');
			return false;
		}
	}
	else if(!(!su1&&!su2&&!su3&&!su4)){
		_message('请填写完整的周日工作时间！','提示');
		return false;
	}
	
	return true;
}
/*
 * 
 * dic检查设备增删改查
 * 
 */
function operatecolumn_dicdg(value, row, index){
/*	var rolestr = row.role&&row.role=='modality'?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="goSetExamItem_Equip('+index+');"><span class="l-btn-left"><span class="l-btn-text">相关检查</span></span></a>&nbsp;&nbsp;' : '';
	return $('#cmenu_dic').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="openModifyDicDlg('+index+');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;&nbsp;'+
			rolestr +
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteDic('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>':'';
	*/
	return $('#cmenu_dic').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="openModifyDicDlg('+index+');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="goSetExamItem_Equip('+index+');"><span class="l-btn-left"><span class="l-btn-text">相关检查</span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteDic('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>':'';
}
function openEditDicDig(){
	var op=$('#modality_dic_panel').panel('options');
	var width=710,height=500;
	if(op.width-50>width){
		width=op.width-50;
	}
	if(op.height-50>height){
		height=op.height-50;
	}
	
	$('#common_dialog').dialog(
		{
			title : '新建设备',
			left: 150,
			top: 40,
			width : width,
			height : height,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/dic/goEditDic',
			buttons:[{
				text:'保存',
				width:80,
				handler:function(){saveModality();}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}]
	});
}

function openModifyDicDlg(para){
//	console.log(para);
	$('#dicdg').treegrid('selectRow',para);
	var row=$('#dicdg').datagrid('getSelected');
	if(row){
		var op=$('#modality_dic_panel').panel('options');
		var width=710,height=500;
		if(op.width-50>width){
			width=op.width-50;
		}
		if(op.height-50>height){
			height=op.height-50;
		}
		
		$('#common_dialog').dialog({
			title : '编辑设备',
			left: 150,
			top: 40,
			width : width,
			height : height,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/dic/goEditDic?id='+row.id,
			buttons:[{
				text:'保存',
				width:80,
				handler:function(){saveModality();}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}]
		});
	}
	else{
		_message('请选择一份数据！','提醒');
	}
}

function showDicomSetting(newValue,oldValue){
	
	$(".storage").textbox("enable");
	$(".stgcmt").textbox("enable");
	$(".queryretrieve").textbox("enable");
	$(".print").textbox("enable");
	$(".worklist").textbox("enable");
	$(".modality_attr").textbox("enable");
	
	 
	
	if(newValue=="modality"){
		$(".print").textbox("disable");
		$(".worklist").textbox("enable");
		$(".modality_attr").textbox("enable");
	}
	else if(newValue=="printer"){
		$(".storage").textbox("disable");
		$(".stgcmt").textbox("disable");
		$(".queryretrieve").textbox("disable");
		$(".worklist").textbox("disable");
		$(".modality_attr").textbox("disable");
	}
	else if(newValue=="PACS"){
		$(".print").textbox("disable");
		$(".worklist").textbox("disable");
		$(".modality_attr").textbox("disable");
	}
	else if(newValue=="RIS"){
		$(".storage").textbox("disable");
		$(".stgcmt").textbox("disable");
		$(".queryretrieve").textbox("disable");
		$(".print").textbox("disable");
		$(".modality_attr").textbox("disable");
	}
	else if(newValue=="workstation"){
		$(".print").textbox("disable");
		$(".worklist").textbox("disable");
		$(".modality_attr").textbox("disable");
	}
	else if(newValue=="other"){
		$(".modality_attr").textbox("disable");
	}
//	$(".storage").textbox("disable");
//	$(".stgcmt").textbox("disable");
//	$(".queryretrieve").textbox("disable");
//	$(".print").textbox("disable");
//	$(".worklist").textbox("disable");

}

function setDepartment_dic(newValue,departmentid){
	if(newValue){
		$("#departmentid").combobox({
			url:window.localStorage.ctx+'/dic/findDepartment?institutionid='+newValue+"&deleted=0",
			onLoadSuccess:function(data){
				$('#departmentid').combobox('clear');
				for(var i=0;i<data.length;i++){
					if(data[i].id==departmentid){
						$('#departmentid').combobox('select',departmentid);
					}
				}
			}
		});
	}else{
		$('#departmentid').combobox('clear');
		$('#departmentid').combobox('loadData',[]);
	}
	
}

function saveModality(){
	$('#dicform').form('submit', {
		url: window.localStorage.ctx+"/dic/saveModality",
		onSubmit: function(){
			if(!$("#working_state").val()){
				$("#working_state").val(0);
			}
			
			var role=$('#role').combobox('getValue')
			if(role=="modality"){
				if(!$('#type').combobox('getValue')||!$('#modality_name').textbox('getValue')||
					!$('#hostname').textbox('getValue')||!$('#institution').combobox('getValue')||
					!$('#departmentid').combobox('getValue')||!$('#averagetime').numberspinner('getValue')){
					return false;
				}
				return getWorktime()
			}else {
				if(!$('#modality_name').textbox('getValue')||
						!$('#hostname').textbox('getValue')||!$('#institution').combobox('getValue')||
						!$('#departmentid').combobox('getValue')){
						return false;
					}
			}
			return $(this).form('validate');
		},
		success: function(data){
			var json=validationDataAll(data);
			if(json.code==0){
				if(json.data=="1"){
					$('#common_dialog').dialog('close');
					getJSON(window.localStorage.ctx+"/dic/findDic",
							{
								institutionid : $('#institutionid_search').val(),
								departmentid : $('#departmentid_search').val(),
								deleted : "0"
							},
							function(data){
								var json=validationData(data);
								$("#dicdg").datagrid("loadData",json);
							});
				}
				else if(json.data=="2"){
					_message('当前设备名或AET已被占用！','提醒');
				}
				
			}
			else{
				_message('保存失败请重试，如果问题依然存在，请联系系统管理员！','错误提醒');
			}
		}
	});
}

function deleteDic(para){
	$('#dicdg').datagrid('selectRow',para);
	var row=$('#dicdg').datagrid('getSelected');
	if(row){
		beforeDeleteDic(row);
	}
	else{
		_message('请选择一份数据！','提醒');
	}
}

function beforeDeleteDic(row){
	getJSON(window.localStorage.ctx+"/dic/beforeDeleteDic",
			{
				id : row.id
			},
			function(data){
				var json = validationDataAll(data);
				if(json.code==0){
					var map = json.data;
					
					var varning1 = map.dic_exam_equip?"检查项目存在"+map.dic_exam_equip+"条相关数据;":"";
					var varning2 = map.client?"客户端存在"+map.client+"条相关数据;":"";
					var varning3 = map.dic_modality?"该设备存在分组【"+map.dic_modality+"】;":"";
					var message = varning1 + varning2 + varning3 + "请先删除相关数据";
					var reg = new RegExp(";","g")
					
					if(map.dic_exam_equip||map.client||map.dic_modality){
						$.messager.show({
			                title  :'提示',
			                msg : message.replace(reg,"<br/>"),
			                timeout : 3000,
			                border : 'thin',
			                showType : 'slide'
			            });
					}else{
						canDeleteDic(row);
					}
				}else{
					_message('请求失败，请重试，如果问题依然存在，请联系系统管理员！','错误');
				}
			});
}

function canDeleteDic(row){
	$.messager.confirm({
		title: '确认删除',
		ok: '是',
		cancel: '否',
		border: 'thin',
		msg: '确认删除选中的数据吗？',
		fn: function(r){
			if (r){
				getJSON(window.localStorage.ctx+"/dic/deleteDic",
						{
							id : row.id
						},
						function(data){
							var json=validationData(data);
					    	if(json.code==0){
					    		var index=$("#dicdg").datagrid('getRowIndex',row);
					    		$("#dicdg").datagrid('deleteRow',index);
					    	}
					    	else{
					    		_message('删除失败，请重试，如果问题依然存在，请联系系统管理员！','错误');
					    	}
						});
			}
		}
	});
}

/**
 * 根据设备分配检查项目
 * @param para
 * @returns
 */
function goSetExamItem_Equip(para) {
	$('#dicdg').datagrid('selectRow', para);
	var row = $('#dicdg').datagrid('getSelected');
	if (row) {
		if(row.role&&row.role=="modality"){
			$('#common_dialog').dialog({
				title : '相关检查项目',
				width : 780,
				height : 590,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				border: 'thin',
				href : window.localStorage.ctx+'/dic/goSetExamItemEquip?type='+row.type+'&modalityId='+row.id,
				buttons:[{
					text: $.i18n.prop('save'),
					width:80,
					handler:function(){saveExamItem_Equip();}
				},{
					text: $.i18n.prop('cancel'),
					width:80,
					handler:function(){$('#common_dialog').dialog('close');}
				}],
				onLoad:function(){

				}
			});
		}else{
			_message('当前不是检查设备！','提醒');
		}
	} else {
		_message('请选择一个设备！','提醒');
	}
}

/**
 * 保存选择的检查项目
 * @returns
 */
function saveExamItem_Equip(){
	var modalityid=$("#appmodalityid").val();
	var rows=$("#examItem_Equip_dg").datagrid("getSelections");
	var examIds="";
	$.each(rows,function(i,row){
		examIds = examIds + row.id + ',';
	});
	
	getJSON(window.localStorage.ctx+"/dic/saveExamitemEquipInfo",
			{	
				modalityid : $('#modalityId_set').val(),
				examIds : examIds,
			},
			function(data){
				var json=validationData(data);
		    	if(json.code==0){
		    		$('#common_dialog').dialog('close');
		    		_message('保存成功！','成功');
		    	}
		    	else{
		    		_message('删除失败，请重试，如果问题依然存在，请联系系统管理员！','错误');
		    	}
				
			});
	
}

/**
 * 加载检查项目初始化默认选中已选
 * @returns
 */
function initExamItemdg(data){
	for(var i=0;i<data.rows.length;i++){
		if(data.rows[i].ck=="1"){
			$("#examItem_Equip_dg").datagrid("checkRow",i);
		}
	}
}

function doSearchDic1(value){
	getJSON(window.localStorage.ctx+"/dic/findDic",
			{	
				institutionid : $('#institutionid_search').val(),
				departmentid : $('#departmentid_search').val(),
				value : value,
				deleted : "0"
			},
			function(data){
				$("#dicdg").datagrid("loadData",validationData(data));
			});
/*	$.getJSON(window.localStorage.ctx+"/dic/findDic?value="+value, function(json){
		
    });*/
}

function doSearchDic2(newValue,oldValue){
	if(newValue){
		$("#departmentid_search").combobox({
			url:window.localStorage.ctx+'/dic/findDepartment?institutionid='+newValue+"&deleted=0",
			onLoadSuccess:function(data){
				$('#departmentid_search').combobox('clear');
			}
		});
	}else{
		$('#departmentid_search').combobox('clear');
		$('#departmentid_search').combobox('loadData',[]);
	}
	getJSON(window.localStorage.ctx+"/dic/findDic",
			{
				institutionid : newValue,
				departmentid : $('#departmentid_search').val(),
				value : $('#dicName_search').val(),
				deleted : "0"
			},
			function(data){
				$("#dicdg").datagrid("loadData",validationData(data));
			});
}

function doSearchDic3(newValue,oldValue){
	getJSON(window.localStorage.ctx+"/dic/findDic",
			{
				institutionid : $('#institutionid_search').val(),
				departmentid : newValue,
				value : $('#dicName_search').val(),
				deleted : "0"
			},
			function(data){
				$("#dicdg").datagrid("loadData",validationData(data));
			});
}

function pingHostname(){
	var ip=$('#hostname').val();
	if($('#hostname').textbox('isValid')){
		$('#progress_dlg').dialog('open');
		$.getJSON(window.localStorage.ctx+"/dic/pingHostname?value="+ip, function(data){
			var json=validationData(data);
			if(json.code==0){
				$.messager.show({
		            title:'成功',
		            msg:"Ping ip:"+ip+"成功！",
		            timeout:3000,
		            border: 'thin',
		            showType:'slide'
		        });
	    	}
	    	else{
	    		$.messager.show({
	                title:'错误',
	                msg:"Ping ip:"+ip+"失败，请重试，如果问题依然存在，请联系系统管理员！",
	                timeout:3000,
	                border:'thin',
	                showType:'slide'
	            });
	    	}
			$('#progress_dlg').dialog('close');
	    });
	}else{
		$.messager.show({
            title:'错误',
            msg:"IP地址格式错误！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}

}
//查询设备
function getModalitydic(value){
	$.getJSON(window.localStorage.ctx+"/dic/findDic?value="+value
			+"&type="+$('#type').combobox('getValue'), function(json){
		$("#equipdg2").datalist("loadData",validationData(json));
    });
}
//添加设备
function addEquip(){
	var rows=$('#equipdg2').datagrid('getSelections');
	if(rows.length>0){
		for(var i=0;i<rows.length;i++){
			$('#equipdg1').datagrid('appendRow',{id:rows[i].id,modality_name:rows[i].modality_name});
			
			var index=$('#equipdg2').datagrid('getRowIndex',rows[i]);
			$('#equipdg2').datagrid('deleteRow',index);
		}
	}
	else{
		$.messager.show({
            title: $.i18n.prop('alert'),
            msg: "请至少选择一个设备",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
}
//删除设备
function removeEquip(){
	var rows=$('#equipdg1').datagrid('getSelections');
	if(rows.length>0){
		for(var i=0;i<rows.length;i++){
			$('#equipdg2').datagrid('appendRow',{id:rows[i].id,modality_name:rows[i].modality_name});
			
			var index=$('#equipdg1').datagrid('getRowIndex',rows[i]);
			$('#equipdg1').datagrid('deleteRow',index);
		}
	}
	else{
		$.messager.show({
            title:$.i18n.prop('alert'),
            msg: "请至少选择一个设备",
            timeout:3000,
            border: 'thin',
            showType:'slide'
        });
	}
}

/*
 * examitemdic 增删改查
 * 
 */

function operatecolumn_examitemdic(value, row, index){
	
	return $('#cmenu_examitemdic').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="Modifyexamitemdlg('+index+');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deletExamitemdic('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>':'';//"<a href='#' class='easyui-linkbutton' name='operate' onclick='openModifyUserDialog()' ><i class='icon icon-edit1'></i>修改</a>&nbsp;&nbsp;"+
	//"<a name='operate' href='#' class='easyui-linkbutton' onclick='deleteUser()' ><i class='icon icon-remove'></i>删除</a>";
}

//增加
function new_examitemdlg(){
	$('#common_dialog').dialog(
		{
			title : '新建检查项目',
			width : 650,
			height : 510,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/dic/editExamitemdic',
			buttons:[{
				text:'保存',
				width:80,
				handler:function(){saveExamitemdic();}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}]
	});
}

//保存检查项目
function saveExamitemdic(){

	$('#examitemdicform').form('submit', {
		url: window.localStorage.ctx+"/dic/saveExamitem",
		onSubmit: function(){
			var rows=$('#equipdg1').datagrid('getRows');
			if(rows.length>0){
				var roleids="";
				var equipments="";
				for(var i=0;i<rows.length;i++){
					equipments+=rows[i].id+',';
				}
				
				if(equipments!=""){
					$('#equipments').val(equipments.substr(0,equipments.length-1));
				}
			}
			return $(this).form('validate');
		},
		success: function(data){
			var json=validationData(data);
			if(json.code==0){
				$('#common_dialog').dialog('close');
				getJSON(window.localStorage.ctx+"/dic/findExamitemdic",
					{
						type : $('#type_search').combobox('getValue'),
//页面没找到对应标签							
//							modalityid : $('#modalityid_search').combobox('getValue'),
//							organfk : $('#organfk_search').combobox('getValue'),
//							suborganfk : $('#suborganfk_search').combobox('getValue'),
						deleted : "0"
					},
					function(data){
						var json=validationData(data);
						$("#examitemdicdg").datagrid("loadData",json);
					});
			}
			else{
				$.messager.show({
		            title:'错误提醒',
		            msg:"保存失败请重试，如果问题依然存在，请联系系统管理员！",
		            timeout:3000,
		            border: 'thin',
		            showType:'slide'
		        });
			}
		}
	});
}

function doSearchItemByType(newValue,oldValue){
	/*if(newValue){
		$("#modalityid_search").combobox({
			url:window.localStorage.ctx+'/dic/findDic?type='+newValue+'&deleted=0',
		});
	}else{
		$('#modalityid_search').combobox('clear');
		$('#modalityid_search').combobox('loadData',[]);
	}*/
	
	getJSON(window.localStorage.ctx+"/dic/findExamitemdic",
			{
				value : $('#itemName_search').val(),
				type : newValue,
				deleted : "0"
			},
			function(data){
				$("#examitemdicdg").datagrid("loadData",validationData(data));
			});
}


//根据设备查找检查项目
function doSearchItemByModality(newValue,oldValue){
	getJSON(window.localStorage.ctx+"/dic/findExamitemdic",
			{
				value : $('#itemName_search').val(),
				type : $('#type_search').combobox('getValue'),
				modalityid : newValue,
				deleted : "0"
			},
			function(data){
				$("#examitemdicdg").datagrid("loadData",validationData(data));
			});
}

//查找检查项目
function doSearchExamitemdic(value){
	getJSON(window.localStorage.ctx+"/dic/findExamitemdic",
			{
				value : value,
				type : $('#type_search').combobox('getValue'),
				deleted : "0"
			},
			function(data){
				$("#examitemdicdg").datagrid("loadData",validationData(data));
			});
}
		
//删除
function deletExamitemdic(index){
	$('#examitemdicdg').datagrid('selectRow',index);
	var row=$('#examitemdicdg').datagrid('getSelected');
	if(row){
	/*var row=$('#examitemdicdg').datagrid('getSelected');
	if(row){*/
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border: 'thin',
			msg: '确认删除选中的数据吗？',
			fn: function(r){
				if (r){
					 $.getJSON(window.localStorage.ctx+"/dic/deletExamitemdic?id="+row.id, function(data){
						 	var json=validationData(data);
					    	if(json.code==0){
					    		doSearchExamitemdic($('#itemName_search').val());
					    	}
					    	else{
					    		$.messager.show({
					                title:'错误',
					                msg:"删除失败，请重试，如果问题依然存在，请联系系统管理员！",
					                timeout:3000,
					                border: 'thin',
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
            border: 'thin',
            timeout:3000,
            showType:'slide'
        });
	}
}
		
//改Modifyexamitemdlg
function Modifyexamitemdlg(para){
		$('#examitemdicdg').datagrid('selectRow',para);
		var row=$('#examitemdicdg').datagrid('getSelected');
		if(row){
			$('#common_dialog').dialog({
				title : '编辑检查项目',
				width : 650,
				height : 530,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				border: 'thin',
				href : window.localStorage.ctx+'/dic/editExamitemdic?id='+row.id,
				/*onLoad : function(){
					console.log(row);
					$('#examitemdicform').form('load',row);
				},*/
				buttons:[{
					text:'保存',
					width:80,
					handler:function(){saveExamitemdic();}
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
        msg:"请选择一个项目！",
        timeout:3000,
        border: 'thin',
        showType:'slide'
    });
}
}
/*
 * group 增删改查
 * 
 */

function operatecolumn_group(value, row, index){
	return $('#cmenu_group').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="modifyGroupdlg('+index+');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteGroup('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>':'';//"<a href='#' class='easyui-linkbutton' name='operate' onclick='openModifyUserDialog()' ><i class='icon icon-edit1'></i>修改</a>&nbsp;&nbsp;"+
	//"<a name='operate' href='#' class='easyui-linkbutton' onclick='deleteUser()' ><i class='icon icon-remove'></i>删除</a>";
}

//增加
function new_groupdlg(){
	$('#common_dialog').dialog(
		{
			title : '新建分组',
			width : 440,
			height : 300,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/dic/goEditGroup',
			buttons:[{
				text:'保存',
				width:80,
				handler:function(){saveGroup();}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}]
	});
}


function saveGroup(){
	$('#groupform').form('submit', {
		url: window.localStorage.ctx+"/dic/saveGroup",
		onSubmit: function(){
			if(sameGroupName){
				$.messager.show({
		            title:'错误提醒',
		            msg:'该名称已被占用',
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
				$("#groupdg").datagrid("reload");
				$('#common_dialog').dialog('close');
			}
			else{
				$.messager.show({
		            title:'错误提醒',
		            msg:json.message,
		            timeout:3000,
		            border: 'thin',
		            showType:'slide'
		        });
			}
		}
	});
}

//根据设备查找设备组
function doSearchGroupByModality(newValue,oldValue){
	getJSON(window.localStorage.ctx+"/dic/findGroup",
			{
				value : $('#GroupName_search').val(),
				modalityid : newValue,
				deleted : "0"
			},
			function(data){
				$("#groupdg").datagrid("loadData",validationData(data));
			});
}
//根据名称查找设备组
function doSearchGroup(value){
	getJSON(window.localStorage.ctx+"/dic/findGroup",
			{
				value : value,
				modalityid : $('#modalityid_search').combobox('getValue'),
				deleted : "0"
			},
			function(data){
				$("#groupdg").datagrid("loadData",validationData(data));
			});
	/*$.getJSON(window.localStorage.ctx+"/dic/getGroup?value="+value, function(json){
		$("#groupdg").datagrid("loadData",validationData(json));
    });*/
}
		


//删除
function deleteGroup(index){

	$('#groupdg').datagrid('selectRow',index);
	var row=$('#groupdg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border: 'thin',
			msg: '确认删除选中的数据吗？',
			fn: function(r){
				if (r){
					getJSON(window.localStorage.ctx+"/dic/deleteGroup",
							{
								id : row.id
							},
							function(data){
								var json=validationData(data);
						    	if(json.code==0){
						    		var index=$("#groupdg").datagrid('getRowIndex',row);
						    		$("#groupdg").datagrid('deleteRow',index);
						    	}
						    	else{
						    		$.messager.show({
						                title:'错误',
						                msg:"删除失败，请重试，如果问题依然存在，请联系系统管理员！",
						                timeout:3000,
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
		
//改group
function modifyGroupdlg(para){
		$('#groupdg').datagrid('selectRow',para);
		var row=$('#groupdg').datagrid('getSelected');
		if(row){
			$('#common_dialog').dialog({
				title : '编辑组',
				width : 440,
				height : 300,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				border: 'thin',
				href : window.localStorage.ctx+'/dic/goEditGroup?id='+row.id,
				buttons:[{
					text:'保存',
					width:80,
					handler:function(){saveGroup();}
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
        msg:"请选择一个组！",
        timeout:3000,
        border: 'thin',
        showType:'slide'
        	
    });
}
}

/*
 * Organdic增删改查
 * 
 */
function operatecolumn_organdic(value, row, index){
	return $('#organdictg').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="ModifyOrgandicdlg('+row.id+');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteOrgandic('+row.id+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>':'';//"<a href='#' class='easyui-linkbutton' name='operate' onclick='openModifyUserDialog()' ><i class='icon icon-edit1'></i>修改</a>&nbsp;&nbsp;"+
	//"<a name='operate' href='#' class='easyui-linkbutton' onclick='deleteUser()' ><i class='icon icon-remove'></i>删除</a>";
}

//增加部位
function new_organdiclg(){
	$('#common_dialog').dialog(
			{
				title : '新建部位',
				width : 360,
				height : 220,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				border: 'thin',
				
				href : window.localStorage.ctx+'/dic/addOrgandic?modality='+$("#modality_organ").val(),
				buttons:[{
					text:'保存',
					width:80,
					handler:function(){saveOrgandic();}
				},{
					text:'关闭',
					width:80,
					handler:function(){$('#common_dialog').dialog('close');}
				}]
		});
}


function saveOrgandic(){

	$('#organdicform').form('submit', {
		url: window.localStorage.ctx+"/dic/saveOrgandic",
		onSubmit: function(){
			return $(this).form('validate');
		},
		success: function(data){
			var json=validationData(data);
			if(json.code==0){
				$('#common_dialog').dialog('close');
				$("#organdictg").treegrid("reload");
//				getJSON(window.localStorage.ctx+"/dic/findOrgandic",
//						{
//							modalitytype : $("#modalitytype").combobox("getValue"),
//							deleted : "0"
//						},
//						function(data){
//							var json=validationData(data);
//							$("#organdictg").treegrid("loadData",json);
//						});
				/*$.getJSON(window.localStorage.ctx+"/dic/findOrgandic?modalitytype="+$("#modalitytype").combobox("getValue"),function(json){
			    	
			    });*/
			}
			else{
				$.messager.show({
		            title:'错误提醒',
		            msg:json.message,
		            timeout:3000,
		            border: 'thin',
		            showType:'slide'
		        });
			}
		}
	});
}

//增加子部位
function new_organdiclg1(){
	var row=$('#organdictg').treegrid('getSelected');
	if(row){
		$('#common_dialog').dialog(
				{
					title : '新建子部位',
					width : 360,
					height : 220,
					closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
					border: 'thin',
					href : window.localStorage.ctx+'/dic/addsubOrgandic?id='+row.id+'&modality='+$("#modality_organ").val(),
					buttons:[{
						text:'保存',
						width:80,
						handler:function(){saveOrgandic();}
					},{
						text:'关闭',
						width:80,
						handler:function(){$('#common_dialog').dialog('close');}
					}]
			});
	}else{
		$.messager.show({
            title:'提醒',
            msg:"请选择一份数据！",
            timeout:3000,
            border: 'thin',
            showType:'slide'
        });
	}
	
}

/*
function savesubOrgandic(){

	$('#organdicform').form('submit', {
		url: window.localStorage.ctx+"/dic/savesubOrgandic",
		onSubmit: function(){
			return $(this).form('validate');
		},
		success: function(data){
			var json=validationData(data);
			if(json.code==0){
				$('#common_dialog').dialog('close');
				getJSON(window.localStorage.ctx+"/dic/findOrgandic",
						{
							modalitytype : $("#modalitytype").combobox("getValue"),
							deleted : "0"
						},
						function(data){
							var json=validationData(data);
							$("#organdictg").treegrid("loadData",json);
						});
				$.getJSON(window.localStorage.ctx+"/dic/findOrgandic?modalitytype="+$("#modalitytype").combobox("getValue"),function(json){
			    	$("#organdictg").treegrid("loadData",json);
			    });
			}
			else{
				$.messager.show({
		            title:'错误提醒',
		            msg:json.message,
		            timeout:3000,
		            border: 'thin',
		            showType:'slide'
		        });
			}
		}
	});
}*/


//删除
function deleteOrgandic(para){
	$('#organdictg').treegrid('selectRow',para);
	var row=$('#organdictg').treegrid('getSelected');
	if(row){
		beforeDeleteOrgan(row);
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
/**
 * 删除设备前判断
 * @param row
 * @returns
 */
function beforeDeleteOrgan(row){
	getJSON(window.localStorage.ctx+"/dic/beforeDeleteOrgan",
			{
				id : row.id
			},
			function(data){
				var json = validationDataAll(data);
				if(json.code==0){
					var map = json.data;
					if(map.dic_examitem){
						var message = "检查项目存在"+map.dic_examitem+"条相关数据;请先删除相关数据";
						var reg = new RegExp(";","g")
						$.messager.show({
			                title  :'提示',
			                msg : message.replace(reg,"<br/>"),
			                timeout : 3000,
			                border : 'thin',
			                showType : 'slide'
			            });
					}else{
						canDeleteOrgan(row);
					}
				}else{
					$.messager.show({
						title : '错误',
						msg : '请求失败，请重试，如果问题依然存在，请联系系统管理员！',
						timeout : 3000,
						border : 'thin',
						showType : 'slide'
					});
				}
			});
}

function canDeleteOrgan(row){
	$.messager.confirm({
		title: '确认删除',
		ok: '是',
		cancel: '否',
		border:'thin',
		msg: '确认删除选中的数据吗？',
		fn: function(r){
			if (r){
				 getJSON(window.localStorage.ctx+"/dic/deleteOrgandic",{
					 id:row.id
				 } ,function(data){
					 	var json=validationData(data);
					 	if(json.code==0){
					 		$("#organdictg").treegrid("reload");
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
		
//改
function ModifyOrgandicdlg(para){
		$('#organdictg').treegrid('selectRow',para);
		var row=$('#organdictg').treegrid('getSelected');
		if(row){
			$('#common_dialog').dialog({
				title : '编辑当前部位',
				width : 360,
				height : 220,
				border:'thin',
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				border: 'thin',
				href : window.localStorage.ctx+'/dic/editOrgandic?id='+row.id,
				buttons:[{
					text:'保存',
					width:80,
					handler:function(){saveOrgandic();}
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
	        msg:"请选择一条记录！",
	        border:'thin',
	        timeout:3000,
	        showType:'slide'
	    });
	}
}
/*function saveEditOrgandic(){

	$('#organdicform').form('submit', {
		url: window.localStorage.ctx+"/dic/saveEditOrgandic",
		onSubmit: function(){
			//alert($('#modname').val());
			
			return $(this).form('validate');
		},
		success: function(data){
			var json=validationData(data);
			if(json.code==0){
				$('#common_dialog').dialog('close');
				$.getJSON(window.localStorage.ctx+"/dic/findOrgandic?modalitytype="+$("#modalitytype").combobox("getValue"),function(json){
			    	$("#organdictg").treegrid("loadData",json);
			    });
			}
			else{
				$.messager.show({
		            title:'错误提醒',
		            msg:json.message,
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
			}
		}
	});
}*/

/**
 * 根据类型获取部位字典
 * @param newValue
 * @param oldValue
 * @returns
 */
function selectType(newValue,oldValue){
	 $('#organdictg').datagrid('clearSelections');
	 
	 $('#organdictg').treegrid({  
	      url:window.localStorage.ctx+'/dic/findOrgandic?modalitytype='+newValue+'&deleted=0',   
	 }); 
}

//根据选择类型获得部位
function setOrgan(newValue, organfk){
	if(newValue){
		$("#organfk").combobox({
			url:window.localStorage.ctx+'/dic/findDicOrgan?modality='+newValue,
			onLoadSuccess:function(data){
				$('#organfk').combobox('clear');
//				console.log("organfk:"+organfk);
				for(var i=0;i<data.length;i++){
					if(data[i].id==organfk){
						$('#organfk').combobox('select',organfk);
					}
				}

			}
		});
		getJSON(window.localStorage.ctx+"/dic/findDic",
				{
					type : newValue,
					examId : $('#id').val(),
					deleted : "0"
					
				},
				function(data){
					$("#equipdg2").datagrid("loadData",validationData(data));
				});
	}else{
		$('#organfk').combobox('clear');
		$('#organfk').combobox('loadData',[]);
	}
	
}

function setSuborgan(newValue, suborganfk){
	if(newValue){
		$("#suborganfk").combobox({
			url:window.localStorage.ctx+'/dic/getOrganDic?parent='+newValue,
			onLoadSuccess:function(data){
				$('#suborganfk').combobox('clear');
				for(var i=0;i<data.length;i++){
					if(data[i].id==suborganfk){
						$('#suborganfk').combobox('select',suborganfk);
					}
				}
			}
		});
	}else{
		$('#suborganfk').combobox('clear');
		$('#suborganfk').combobox('loadData',[]);
	}
	
}

// 新建员工时，根据选择的机构来获取 所在的科室
function setDepartment(newValue,deptfk){
	if(newValue){
		$("#dept").combobox({
			url:window.localStorage.ctx+'/dic/findDepartment?institutionid='+newValue+"&deleted=0",
			onLoadSuccess:function(data){
				$('#dept').combobox('clear');
				for(var i=0;i<data.length;i++){
					if(data[i].id==deptfk){
						$('#dept').combobox('select',deptfk);
					}
				}
			}
		});
	}else{
		$('#dept').combobox('clear');
		$('#dept').combobox('loadData',[]);
	}
	
}

function setDeptInfo(record){
	$('#deptcode_edit').val(record.deptcode);
	$('#deptname_edit').val(record.deptname);
}

/*
 * department 增删改查
 * 
 */

function operatecolumn_department(value, row, index){
	
	return $('#cmenu_department').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="Modifydepartment('+index+');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteDepartment('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>':'';//"<a href='#' class='easyui-linkbutton' name='operate' onclick='openModifyUserDialog()' ><i class='icon icon-edit1'></i>修改</a>&nbsp;&nbsp;"+
	//"<a name='operate' href='#' class='easyui-linkbutton' onclick='deleteUser()' ><i class='icon icon-remove'></i>删除</a>";
}

//增加
function new_department(){
	$('#common_dialog').dialog(
		{
			title : '新建科室',
			width : 450,
			height : 450,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/dic/Modifydepartment',
			buttons:[{
				text:'保存',
				width:80,
				handler:function(){saveDepartment();}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}]
	});
}

//保存

function saveDepartment(){

	$('#departmentform').form('submit', {
		url: window.localStorage.ctx+"/dic/saveDepartment",
		onSubmit: function(){
			/*var rows=$('#dept_shift_dg').datagrid('getChecked')
			if(rows&&rows.length>0){
				var shiftids="";
				var shifts="";
				$.each(rows,function(index,row){
					shiftids+=row.id+',';
					shifts+=row.name+',';
				});
				if(shifts)shifts=shifts.substr(0,shifts.length-1);
				$('#shiftids_dic').val(shiftids);
				$('#shifts_dic').val(shifts);
			}*/				
			return $(this).form('validate');
		},
		success: function(data){
			var json=validationData(data);
			if(json.code==0){
				$('#common_dialog').dialog('close');
				getJSON(window.localStorage.ctx+"/dic/findDepartment",
						{
							institutionid: $('#institutionid_search').combobox('getValue'),
							deleted : "0"
						},
						function(data){
							
							$("#departmentdg").datagrid("loadData",validationData(data));
						});
				/*$.getJSON(window.localStorage.ctx+"/dic/getDepartment", function(json){
			    	$("#departmentdg").datagrid("loadData",json);
			    });*/
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

/**
 *  查找科室
 * @param value doSearchDicDepartment
 * @returns
 */
function doSearchdepartment(value) {
	getJSON(window.localStorage.ctx+"/dic/findDepartment",
			{
				institutionid: $('#institutionid_search').combobox('getValue'),
				value : value,
				deleted : "0"
			},
			function(data){
				$("#departmentdg").datagrid("loadData",validationData(data));
			});
}

/**
 *  选中机构查找科室
 * @param newValue
 * @param oldValue
 * @returns
 */
function doSearchDicDepartment(newValue,oldValue) {
	getJSON(window.localStorage.ctx+"/dic/findDepartment",
			{
				institutionid : newValue,
				value : $('#departmentName').val(),
				deleted : "0"
			},
			function(data){
				$("#departmentdg").datagrid("loadData",validationData(data));
			});
}

//删除
function deleteDepartment(index){
	$('#departmentdg').datagrid('selectRow',index);
	var row=$('#departmentdg').datagrid('getSelected');
	if(row){
		beforeDeleteDepartment(row);
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

function beforeDeleteDepartment(row){
	getJSON(window.localStorage.ctx+"/dic/beforeDeleteDepartment",
			{
				id : row.id
			},
			function(data){
				var json = validationDataAll(data);
				if(json.code==0){
					var map = json.data;
					
					var varning1 = map.dic_employee?"员工字典存在"+map.dic_employee+"条相关数据;":"";
					var varning2 = map.dic_modality?"设备字典存在"+map.dic_modality+"条相关数据;":"";
					var message = varning1 + varning2 + "请先删除相关数据";
					var reg = new RegExp(";","g")
					
					if(map.dic_employee||map.dic_modality){
						$.messager.show({
			                title  :'提示',
			                msg : message.replace(reg,"<br/>"),
			                timeout : 3000,
			                border : 'thin',
			                showType : 'slide'
			            });
					}else{
						canDeleteDepartment(row);
					}
				}else{
					$.messager.show({
						title : '错误',
						msg : '请求失败，请重试，如果问题依然存在，请联系系统管理员！',
						timeout : 3000,
						border : 'thin',
						showType : 'slide'
					});
				}
			});
}

function canDeleteDepartment(row){
	$.messager.confirm({
		title: '确认删除',
		ok: '是',
		cancel: '否',
		border:'thin',
		msg: '确认删除选中的数据吗？',
		fn: function(r){
			if (r){
				 $.getJSON(window.localStorage.ctx+"/dic/deleteDepartment?id="+row.id, function(data){
					 	var json=validationData(data);
				    	if(json.code==0){
				    		var index=$("#departmentdg").datagrid('getRowIndex',row);
				    		$("#departmentdg").datagrid('deleteRow',index);
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
		
function Modifydepartment(para){
		$('#departmentdg').datagrid('selectRow',para);
		var row=$('#departmentdg').datagrid('getSelected');
		if(row){
			$('#common_dialog').dialog({
				title : '编辑科室',
				width : 450,
				height : 450,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				border: 'thin',
				href : window.localStorage.ctx+'/dic/Modifydepartment?id='+row.id,
				buttons:[{
					text:'保存',
					width:80,
					handler:function(){saveDepartment();}
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
	        msg:"请选择一个部门！",
	        border:'thin',
	        timeout:3000,
	        showType:'slide'
	    });
	}
}

/*
 * employee 增删改查
 * 
 */

function operatecolumn_employee(value, row, index){
	
	return $('#cmenu_employee').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="openModifyEmployeeDialog('+index+');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteEmployee('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>':'';//"<a href='#' class='easyui-linkbutton' name='operate' onclick='openModifyUserDialog()' ><i class='icon icon-edit1'></i>修改</a>&nbsp;&nbsp;"+
	//"<a name='operate' href='#' class='easyui-linkbutton' onclick='deleteUser()' ><i class='icon icon-remove'></i>删除</a>";
}

//根据科室查找员工
function doSearchEmployeeBydept(newValue, oldValue){
	getJSON(window.localStorage.ctx+"/dic/getEmployee",
			{
				institutionid : $('#institutionid_search').combobox('getValue'),
				name : $("#employeeName").val(),
				dept : newValue,
				profession : $('#professionName').combobox('getValue'),
				deleted : "0"
			},
			function(json){
				$("#employeedg").datagrid("loadData",validationData(json));
			}
		);
}
/**
 *  根据机构id来查找员工
 * @param newValue
 * @param oldValue
 * @returns
 */
function doSearchEmployeeByInstitutionId(newValue, oldValue){
	if (newValue) {
		$("#deptname").combobox({
			url:window.localStorage.ctx+'/dic/findDepartment?institutionid='+newValue+"&deleted=0",
			onLoadSuccess:function(data){
				$('#deptname').combobox('clear');
			}
		});
	} else {
		$('#deptname').combobox('clear');
		$('#deptname').combobox('loadData',[]);
	}
	getJSON(window.localStorage.ctx+"/dic/getEmployee",
			{
				institutionid : newValue,
				name : $("#employeeName").val(),
				dept : $('#deptname').combobox('getValue'),
				profession : $('#professionName').combobox('getValue'),
				deleted : "0"
			},
			function(json){
				$("#employeedg").datagrid("loadData",validationData(json));
			}
		);
}
//根据职务查找员工
function doSearchEmployeeByprofession(newValue, oldValue){
	getJSON(window.localStorage.ctx+"/dic/getEmployee",
			{
				institutionid : $('#institutionid_search').combobox('getValue'),
				name : $("#employeeName").val(),
				dept : $('#deptname').combobox('getValue'),
				profession : newValue,
				deleted : "0"
			},
			function(json){
				$("#employeedg").datagrid("loadData",validationData(json));
			}
		);
}
//查找员工
function doSearchEmployee(value){
	getJSON(window.localStorage.ctx+"/dic/getEmployee",
			{
				institutionid : $('#institutionid_search').combobox('getValue'),
				name : value,
				dept : $('#deptname').combobox('getValue'),
				profession : $('#professionName').combobox('getValue'),
				deleted : "0"
			},
			function(json){
				$("#employeedg").datagrid("loadData",validationData(json));
			}
		);
}

//新增
function openNewEmployeeDialog(){
	$('#common_dialog').dialog(
		{
			title : '新建员工',
			width : 350,
			height : 300,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/dic/goEditEmployee',
			buttons:[{
				text:'保存',
				width:80,
				handler:function(){saveEmployee();}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}]
	});
}

//修改
function openModifyEmployeeDialog(index){
	$('#employeedg').datagrid('selectRow',index);
	var row=$('#employeedg').datagrid('getSelected');
	if(row){
			$('#common_dialog').dialog({
				title : '编辑员工',
				width : 350,
				height : 300,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				border: 'thin',
				href : window.localStorage.ctx+'/dic/goEditEmployee?id='+row.id,
				buttons:[{
					text:'保存',
					width:80,
					handler:function(){saveEmployee();}
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
		        msg:"请选择一个员工！",
		        timeout:3000,
		        border:'thin',
		        showType:'slide'
		    });
		}
}

//保存员工信息
function saveEmployee(){
	$('#employeeform').form('submit', {
		url: window.localStorage.ctx+"/dic/saveEmployee",
		onSubmit: function(){
			return $(this).form('validate');
		},
		success: function(data){
			var json=validationData(data);
			if(json.code==0){
				$('#common_dialog').dialog('close');
				getJSON(window.localStorage.ctx+"/dic/getEmployee",
						{
							institutionid : $('#institutionid_search').combobox('getValue'),
							dept : $('#deptname').combobox('getValue'),
							profession : $('#professionName').combobox('getValue'),
							deleted : "0"
						},
						function(json){
							$("#employeedg").datagrid("loadData",json);
							}
						);
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

//删除员工信息
function deleteEmployee(index){
	$('#employeedg').datagrid('selectRow',index);
	var row=$('#employeedg').datagrid('getSelected');
	if(row){
		$.getJSON(window.localStorage.ctx+"/existAccount?employeefk="+row.id, function(json){
			if (json.data != null) {
				// 判断是否存在此用户	
	    		$.messager.show({
	                title:'提醒',
	                msg:"请先将此员工关联的用户删除！",
	                timeout:3000,
	                border:'thin',
	                showType:'slide'
	            });
			} else {
				$.messager.confirm({
					title: '确认删除',
					ok: '是',
					cancel: '否',
					border:'thin',
					msg: '确认删除选中的数据吗？',
					fn: function(r){
						if (r){
							 $.getJSON(window.localStorage.ctx+"/dic/deleteEmployee?id="+row.id, function(data){
								 	var json=validationData(data);
							    	if(json.code==0){
//							    		var index=$("#employeedg").datagrid('getRowIndex',row);
//							    		$("#employeedg").datagrid('deleteRow',index);
							    		doSearchEmployee($('#employeeName').searchbox('getValue'))
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

/**
 *  显示添加医院页面
 * @returns
 */
function new_Institution() {
	$('#common_dialog').dialog(
		{
			title : '新建医院',
			width : 400,
			height : 300,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/dic/modifyInstitution',
			buttons:[{
				text:'保存',
				width:80,
				handler:function(){saveInstitution();}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}]
	});
}


/**
 *  保存医院
 * @returns
 */
function saveInstitution() {

	$('#Institutionform').form('submit', {
		url: window.localStorage.ctx+"/dic/saveInstitution",
		onSubmit: function() {
			return $(this).form('validate');
		},
		success: function(data) {
			var json = validationData(data);
			if (json.code == 0) {
				$('#common_dialog').dialog('close');
				getJSON(window.localStorage.ctx+"/dic/getInstitution",
						{
							deleted : "0"
						},
						function(data){
							$("#Institutiondg").datagrid("loadData",validationData(data));
						});
				/*$.getJSON(window.localStorage.ctx+"/dic/getInstitution", function(json) {
			    	$("#Institutiondg").datagrid("loadData",json);
			    });*/
			} else {
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

/**
 *  显示编辑医院页面
 * @param para
 * @returns
 */
function modifyInstitution(para) {
	$('#Institutiondg').datagrid('selectRow', para);
	var row = $('#Institutiondg').datagrid('getSelected');
	if (row) {
		$('#common_dialog').dialog(
				{
					title : '编辑医院',
					width : 400,
					height : 300,
					closed : false,
					cache : false,
					resizable : false,
					minimizable : false,
					maximizable : false,
					collapsible : false,
					modal : true,
					border : 'thin',
					href : window.localStorage.ctx
							+ '/dic/modifyInstitution?id=' + row.id,
					buttons : [ {
						text : '保存',
						width : 80,
						handler : function() {
							saveInstitution();
						}
					}, {
						text : '关闭',
						width : 80,
						handler : function() {
							$('#common_dialog').dialog('close');
						}
					} ]
				});
	} else {
		$.messager.show({
			title : '提醒',
			msg : "请选择一个医院！",
			border : 'thin',
			timeout : 3000,
			showType : 'slide'
		});
	}
}

/**
 *  机构添加与角色关联的页面
 * @param para
 * @returns
 */
function editInstitutionRole(para) {
	$('#Institutiondg').datagrid('selectRow', para);
	var row = $('#Institutiondg').datagrid('getSelected');
	if (row) {
		$('#common_dialog').dialog(
				{
					title : '编辑角色',
					width : 350,
					height : 250,
					closed : false,
					cache : false,
					resizable : false,
					minimizable : false,
					maximizable : false,
					collapsible : false,
					modal : true,
					border : 'thin',
					href : window.localStorage.ctx
							+ '/dic/editInstitutionRole?id=' + row.id,
					buttons : [ {
						text : '保存',
						width : 80,
						handler : function() {
							saveInstitutionRole();
						}
					}, {
						text : '关闭',
						width : 80,
						handler : function() {
							$('#common_dialog').dialog('close');
						}
					} ]
				});
	} else {
		$.messager.show({
			title : '提醒',
			msg : "请选择一个医院！",
			border : 'thin',
			timeout : 3000,
			showType : 'slide'
		});
	}
}

/**
 *  保存机构与角色相关联的信息
 * @returns
 */
function saveInstitutionRole() {

	$('#institutionRoleform').form('submit', {
		url: window.localStorage.ctx+"/dic/saveInstitutionRole",
		onSubmit: function() {
			// 关联机构
			var idsstr = "";
			$('#institution_role_div').find("input[checkboxname='role_module_in']").each(function(index,obj){
				if($(obj).checkbox('options').checked){
					idsstr+=$(obj).val()+",";
				}
			});
			
			if (idsstr=="") {
				$.messager.show({
	                title: $.i18n.prop('错误提醒'),
	                msg: $.i18n.prop('请选择需要关联的角色'),
	                timeout:3000,
	                border:'thin',
	                showType:'slide'
	            });
				return false;
			}
			else{
				$('#roleids').val(idsstr);
			}
		},
		success: function(data) {
			var json = validationData(data);
			if (json.code == 0) {
				$('#common_dialog').dialog('close');
				getJSON(window.localStorage.ctx+"/dic/getInstitution",
						{
							deleted : "0"
						},
						function(data){
							$("#Institutiondg").datagrid("loadData",validationData(data));
						});
			} else {
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

/**
 *  列表操作中的编辑和修改
 *  
 * @param value
 * @param row
 * @param index
 * @returns
 */
function operatecolumn_Institution(value, row, index){
	return $('#cmenu_Institution').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="modifyInstitution('+index+');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="editInstitutionRole('+index+');"><span class="l-btn-left"><span class="l-btn-text">添加角色</span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteInstitution('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>':'';
}

/**
 * 查找医院
 * @param value
 * @returns
 */
function doSearchInstitution(value) {
	getJSON(window.localStorage.ctx+"/dic/getInstitution",
			{
				value : value,
				deleted : "0"
			},
			function(data){
				$("#Institutiondg").datagrid("loadData",validationData(data));
			});
	/*$.getJSON(window.localStorage.ctx+"/dic/getInstitution?value="+value, function(json) {
		$("#Institutiondg").datagrid("loadData",validationData(json));
    });*/
}

/**
 * 删除医院
 * @param index
 * @returns
 */
function deleteInstitution(index) {
	$('#Institutiondg').datagrid('selectRow',index);
	var row = $('#Institutiondg').datagrid('getSelected');
	if (row) {
		beforeDeleteInstitution(row);
	} else {
		$.messager.show ({
            title:'提醒',
            msg:"请选择一份数据！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
}

function beforeDeleteInstitution(row){
	getJSON(window.localStorage.ctx+"/dic/beforeDeleteInstitution",
			{
				id : row.id
			},
			function(data){
				var json = validationDataAll(data);
				if(json.code==0){
					var map = json.data;
					
					var varning1 = map.role?"角色存在"+map.role+"条相关数据;":"";
					var varning2 = map.dic_department?"科室部门存在"+map.dic_department+"条相关数据;":"";
					var varning3 = map.dic_employee?"员工存在"+map.dic_employee+"条相关数据;":"";
					var varning4 = map.dic_modality?"设备存在"+map.dic_modality+"条相关数据;":"";
					var message = varning1 + varning2 + varning3 + varning4 + "请先删除相关数据";
					var reg = new RegExp(";","g")
					
					if(map.role||map.dic_department||map.dic_employee||map.dic_modality){
						$.messager.show({
			                title  :'提示',
			                msg : message.replace(reg,"<br/>"),
			                timeout : 3000,
			                border : 'thin',
			                showType : 'slide'
			            });
					}else{
						canDeleteInstitution(row);
					}
				}else{
					$.messager.show({
						title : '错误',
						msg : '请求失败，请重试，如果问题依然存在，请联系系统管理员！',
						timeout : 3000,
						border : 'thin',
						showType : 'slide'
					});
				}
			});
}

function canDeleteInstitution(row){
	$.messager.confirm({
		title: '确认删除',
		ok: '是',
		cancel: '否',
		border:'thin',
		msg: '确认删除选中的数据吗？',
		fn: function(r) {
			if (r) {
				 $.getJSON(window.localStorage.ctx+"/dic/deleteInstitution?id="+row.id, function(data){
					 	var json = validationData(data);
				    	if (json.code == 0) {
				    		var index = $("#Institutiondg").datagrid('getRowIndex',row);
				    		$("#Institutiondg").datagrid('deleteRow',index);
				    	} else {
				    		$.messager.show ({
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

function checkShift(data){
	console.log(data);
	var rows=$("#dept_shift_dg").datagrid('getRows');
	$.each(rows,function(index,row){
		if(row.deptid){
			$("#dept_shift_dg").datagrid('selectRow',index);
		}
	});
	
}

function getDicCommon(value){
	var group=$("#dicgroup_cb").combobox('getValue');
	if(group){
		getJSON(window.localStorage.ctx+"/dic/findDicCommon",
			{
				'group':group,
				'value':value
			},function(json){
				if(json.code==0){
					$("#dic_common_dg").datagrid('loadData',json.data);
				} else{
					_messge('请求数据失败！请重试!'+json.message,'错误');
				}
		});
	} else{
		_message('请先选择分组！');
	}
}

//通用字典新增行
function addDicCommon(){
	if($('#dicgroup_cb').combobox('getValue')){
		$('#dic_common_dg').datagrid('appendRow',{id:""});
	} else{
		_message('请先选择分组！');
	}
}

//删除通用字典
function deleteDicCommon(index){
	$('#dic_common_dg').datagrid('selectRow',index);
	var row=$('#dic_common_dg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除选中的数据吗？',
			fn: function(r){
				if (r){
					if(!row.id){
						$('#dic_common_dg').datagrid('deleteRow',index);
						return;
					}  
					 getJSON(window.localStorage.ctx+"/dic/delDicCommon",{
						 id:row.id
					 } ,function(data){
						 	var json=validationData(data);
						 	if(json.code==0){
						 		_message("删除成功","提示");
						 		$('#dic_common_dg').datagrid('deleteRow',index);
						 	}
					    	else{
					    		_message("删除失败，请重试，如果问题依然存在，请联系系统管理员！","错误");
					    	}
					 });
				}
			}
		});
	}
	else{
		_message("请选择要删除的记录！","提醒");
	}
}

//保存新增或变更的通用字典
function saveDicCommon(){
	if(!$("#dicgroup_cb").combobox('getValue')){
		_message('请先选择分组！')
		return;
	}
	//保存前结束表格编辑状态
	endEditing_Diccom();
	var rows=$('#dic_common_dg').datagrid('getChanges');
	if(rows&&rows.length>0){
		getJSON(window.localStorage.ctx+"/dic/saveDicCommon",
			{
				data : JSON.stringify(rows),
				group : $("#dicgroup_cb").combobox('getValue')
			},
			function(json){
			 	if(json.code==0){
			 		_message("保存成功","提示");
			 		getDicCommon();
			 	} else{
		    		_message("保存失败，请重试，如果问题依然存在，请联系系统管理员！"+json.message,"错误");
		    	}
			}
		);
	}
}
