/**
 * 
 */

function openNewUserDialog(){
	var row=$('#employeedg').datagrid('getSelected');
	if(row){
		getJSON(window.localStorage.ctx+'/existAccount',
				{
					employeefk:row.id
				},
				function(data){
					var json=validationDataAll(data);
					if(json.code==0){
						if(json.data){
							$.messager.show({
				                title:'提示',
				                msg: "员工【"+json.data.name+"】已存在用户账号【"+json.data.username+"】",
				                timeout:5000,
				                border:'thin',
				                showType:'slide'
				            });	
						}else{
							$('#common_dialog').dialog(
									{
										title : $.i18n.prop('admin.edituser'),
										width : 600,
										height : 650,
										closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
										border: 'thin',
										href : window.localStorage.ctx+'/goEditUser?employeefk='+row.id+'&name='+row.name,
										buttons:[{
											text: $.i18n.prop('save'),
											width:80,
											handler:function(){saveUser();}
										},{
											text: $.i18n.prop('cancel'),
											width:80,
											handler:function(){$('#common_dialog').dialog('close');}
										}]
								});
						}
					}
				});
	}else{
		$.messager.show({
            title:'提醒',
            msg:"请选择一份数据！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}

//	
//	$.getJSON("/getAllRole", function(json){
//    	//alert("JSON Data: " + json.patientname);
//    	//if(json!=null){
//    		$("#rolelist1").datagrid("loadData",json);//validationData(json)
//    	//}
//    });
}


function goEditUserFromEmployee(){
	var row=$('#employeedg').datagrid('getSelected');
	if(row){
		getJSON(window.localStorage.ctx+'/existAccount',
				{
					employeefk:row.id
				},
				function(data){
					var json=validationDataAll(data);
					if(json.code==0){
						if(json.data&&json.data.id){
							$('#common_dialog').dialog({
								title : $.i18n.prop('admin.edituser'),
								width : 600,height : 650,
								closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
								border: 'thin',
								href : window.localStorage.ctx+'/goEditUser?id='+json.data.id,
								buttons:[{
									text: $.i18n.prop('save'),
									width:80,
									handler:function(){saveUser();}
								},{
									text: $.i18n.prop('cancel'),
									width:80,
									handler:function(){$('#common_dialog').dialog('close');}
								}]
							});
						}else{
							$.messager.show({
					            title: $.i18n.prop('alert'),
					            msg: "当前员工没有账号",
					            timeout:3000,
					            border:'thin',
					            showType:'slide'
					        });
						}
					}
				});
	}else{
		$.messager.show({
            title: $.i18n.prop('alert'),
            msg: $.i18n.prop('admin.selectoneuser'),
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
}

function openModifyUserDialog(index){
	console.log(index)
	$('#userdg').datagrid('selectRow',index);
	var row=$('#userdg').datagrid('getSelected');
	if(row){
		$('#common_dialog').dialog({
			title : $.i18n.prop('admin.edituser'),
			width : 600,height : 650,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/goEditUser?id='+row.id,
			buttons:[{
				text: $.i18n.prop('save'),
				width:80,
				handler:function(){saveUser();}
			},{
				text: $.i18n.prop('cancel'),
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}],
			onLoad:function(){
				if($('.passwordtext')[0] && $('#userid').val()){
					$('.passwordtext').textbox('disable');
				}
			}
		});
		
		
		
//		$('#userdialog').dialog('open');
//		$('#userid').val(row.id);
//		$('#username').textbox('setValue',row.username);
//		$('#name').textbox('setValue',row.name);
//		$('#description').textbox('setValue',row.description);
		//$('#roles').val("");
		//$('#rolenames').val(row.role);
		
//		$.getJSON("/getUserrole?userid="+row.id, function(json){
//	    	//alert("JSON Data: " + json.patientname);
//	    	//if(json!=null){
//	    		$("#rolelist").datagrid("loadData",json);//validationData(json)
//	    	//}
//	    });
//		
//		$.getJSON("/getAllRole?userid="+row.id, function(json){
//	    	//alert("JSON Data: " + json.patientname);
//	    	//if(json!=null){
//	    		$("#rolelist1").datagrid("loadData",json);//validationData(json)
//	    	//}
//	    });
		
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

//function loadData(){
	//url:'/admin/getAllRole'
	
//	$.getJSON("/admin/getUserrole?userid="+userid, function(json){
//    	//alert("JSON Data: " + json.patientname);
//    	//if(json!=null){
//    		$("#rolelist").datagrid("loadData",json);//validationData(json)
//    	//}
//    });
//	
//	
//	$.getJSON("/admin/getAllRole", function(json){
//    	//alert("JSON Data: " + json.patientname);
//    	//if(json!=null){
//    		$("#rolelist1").datagrid("loadData",json);//validationData(json)
//    	//}
//    });
//	
//}

//function closeUserDialog(){
//	$('#userdialog').dialog('close');//destroy
//	clearField();
//}
//function user_operate_formatter(value,row,index){
//	
//	
//	var str = 
//					"<input type='image' onclick='selectStandardcode_("+index+")' src='"+window.localStorage.ctx+"/themes/icons/pencil.png'>&nbsp;&nbsp;&nbsp;&nbsp;" +
//					"<input type='image' onclick='selectStandardcode_("+index+")' src='"+window.localStorage.ctx+"/themes/icons/delete.png'>";  
//    return str;
//}

function addRoleToList(){
	var rows=$('#rolelist1').datagrid('getSelections');
	if(rows.length>0){
		//alert(rows.length)
		for(var i=0;i<rows.length;i++){
			$('#rolelist').datagrid('appendRow',{id:rows[i].id,rolename:rows[i].rolename});
			
			var index=$('#rolelist1').datagrid('getRowIndex',rows[i]);
			$('#rolelist1').datagrid('deleteRow',index);
		}
	}
	else{
		$.messager.show({
            title: $.i18n.prop('alert'),
            msg: $.i18n.prop('admin.selectoneormorerole'),
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
}

function removeRoleToList(){
	var rows=$('#rolelist').datagrid('getSelections');
	if(rows.length>0){
		//alert(rows.length)
		for(var i=0;i<rows.length;i++){
			$('#rolelist1').datagrid('appendRow',{id:rows[i].id,rolename:rows[i].rolename});
			
			var index=$('#rolelist').datagrid('getRowIndex',rows[i]);
			$('#rolelist').datagrid('deleteRow',index);
		}
	}
	else{
		$.messager.show({
            title:$.i18n.prop('alert'),
            msg: $.i18n.prop('admin.selectoneormorerole'),
            timeout:3000,border:'thin',
            showType:'slide'
        });
	}
}




function checkUsername(newvalue,oldvalue){
	console.log("checkUsername:"+newvalue);
	getJSON(window.localStorage.ctx+"/checkUsername",
			{
				username : newvalue,
				userid : $('#userid').val()
			},
			function(data){
				var json=validationData(data);
				if(json.exist){
					$('#checkexist').val('true');
					console.log("exist:"+json.exist);
					$('#username').validatebox({
						validType: "exist['true']"
						
					})
				}
				else{
					$('#checkexist').val('');
					console.log("exist:"+json.exist);
					$('#username').validatebox({
						validType: "exist['false']"
					})
				}
    });
}

function saveUser(){

	var moredata = $("#userform_1").serialize();
	console.log(moredata)
	$('#userform').form('submit', {
		url: window.localStorage.ctx+"/saveUser?"+moredata,
		onSubmit: function(){
			var rows=$('#rolelist').datagrid('getRows');
			if(rows.length>0){
				var roleids="";
				for(var i=0;i<rows.length;i++){
					roleids+=rows[i].id+',';
				}
				
				if(roleids!=""){
					$('#roles').val(roleids.substr(0,roleids.length-1));
				}
				
				var rolenames="";
				for(var i=0;i<rows.length;i++){
					rolenames+=rows[i].rolename+',';
				}
				
				if(roleids!=""){
					$('#rolenames').val(rolenames.substr(0,rolenames.length-1));
				}
				
				var src=$("#avatar_img").attr("src");
				if(src&&src.indexOf("?")>0){
					var path=src.substr(src.indexOf("path=")+5);
					console.log(path);
					$('#avatar').val(path);
				}
				
			}
			
			if($('#username').textbox('getValue')==""||$('#name').textbox('getValue')==""){
				_message("用户名不能为空", "提醒");
				return false;
			}
			
			if(!$('.passwordtext').textbox('options').disabled && ($('#password').textbox('getValue')==""||$('#password').textbox('getValue')!=$('#password1').textbox('getValue'))){
				_message("密码不能为空或密码不一致", "提醒");
				return false;
			}
			
			if($('#checkexist').val()=='true'){
				$.messager.show({
					title : $.i18n.prop('error'),
					msg : '用户名已被占用',
					timeout : 1000,
					border:'thin',
					showType : 'slide'
				});
				return false;
			}
			if($('#roles').val()==""){
				$.messager.show({
					title : $.i18n.prop('error'),
					msg : '请选择角色',
					timeout : 1000,
					border:'thin',
					showType : 'slide'
				});
				return false;
			}
			return $(this).form('validate');
		},
		success: function(data){
			var json=validationDataAll(data);
			if(json.code==0){
				$('#common_dialog').dialog('close');
				
				getJSON(window.localStorage.ctx+"/getAllUsers",
						{
							deleted : "0"
						},
						function(data){
							var json = validationData(data);
							if($("#userdg")[0]){
								$("#userdg").datagrid("loadData",json);
							}
							
						});
				
				if($("#userid").val()){
					$.messager.show({
		                title:'保存成功',
		                msg: "用户账号：【"+json.data.username+"】修改成功",
		                timeout:5000,
		                border:'thin',
		                showType:'slide'
		            });
				}else{
					$.messager.show({
		                title:'保存成功',
		                msg: "【"+json.data.name+"】的用户账号：【"+json.data.username+"】创建成功",
		                timeout:5000,
		                border:'thin',
		                showType:'slide'
		            });	
				}
				
			}
			else{
				$.messager.show({
		            title: $.i18n.prop('error'),
		            msg: $.i18n.prop('savefailed'),
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
			}
		}
	});
}

function deleteUser(index){
	console.log(index)
	$('#userdg').datagrid('selectRow',index);
	var row=$('#userdg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: $.i18n.prop('confirm'),
			border:'thin',
			msg: $.i18n.prop('admin.confrimdeleteuser'),
			fn: function(r){
				if (r){
					 $.getJSON(window.localStorage.ctx+"/deleteUser?userid="+row.id, function(data){
						 	var json=validationData(data);
					    	if(json.code==0){
					    		doSearchUser();
					    	}
					    	else{
					    		$.messager.show({
					                title: $.i18n.prop('error'),
					                msg: $.i18n.prop('deletefailed'),
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
            title: $.i18n.prop('alert'),
            msg: $.i18n.prop('admin.selectoneuser'),
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
}

function doSearchUser(value){
	getJSON(window.localStorage.ctx+"/getAllUsers",
			{
				deleted : "0",
				value : value
			},
			function(data){
				var json = validationData(data);
				$("#userdg").datagrid("loadData",json);
			});
}

function doSearchRes(value){
	getJSON(window.localStorage.ctx+"/getAllResource",
			{
				value : value
			},
			function(data){
				var json = validationData(data);
				$("#resdg").datagrid("loadData",json);
			});
}


function openNewResDialog(){
	$('#resdialog').dialog('open');
}

function clearField_Res(){
	$('#resname').textbox('setValue',"");
	$('#resource').textbox('setValue',"");
	$('#resid').val("");
}

function closeResDialog(){
	$('#resdialog').dialog('close');//destroy
	clearField_Res();
}

function saveResource(){
	
	$('#resform').form('submit', {
		url: window.localStorage.ctx+"/saveResource",
		onSubmit: function(){
			
		},
		success: function(data){
			var json=validationData(data);
			if(json.code== 0){
				closeResDialog();
				
				$.getJSON(window.localStorage.ctx+"/getAllResource", function(json){
			    	$("#resdg").datagrid("loadData",json);
			    });
			}
			else{
				$.messager.show({
		            title: $.i18n.prop('error'),
		            msg: $.i18n.prop('savefailed'),
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
			}
		}
	});
}

function openModifyResDialog(index){
	console.log(index)
	$('#resdg').datagrid('selectRow',index);
	var row=$('#resdg').datagrid('getSelected');
	if(row){
		
		$('#resdialog').dialog('open');
		$('#resid').val(row.id);
		$('#resname').textbox('setValue',row.name);
		$('#resource').textbox('setValue',row.resource);
		
	}
	else{
		$.messager.show({
            title: $.i18n.prop('alert'),
            msg: $.i18n.prop('admin.selectoneresource'),
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
}

function deleteRes(index){
	console.log(index)
	$('#resdg').datagrid('selectRow',index);
	var row=$('#resdg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: $.i18n.prop('confirm'),
			msg:  $.i18n.prop('admin.confiemtodeleteresource'),border:'thin',
			fn: function(r){
				if (r){
					 $.getJSON(window.localStorage.ctx+"/deleteResource?resid="+row.id, function(data){
						 	var json=validationData(data);
					    	if(json.code== 0){
					    		var index=$("#resdg").datagrid('getRowIndex',row);
					    		$("#resdg").datagrid('deleteRow',index);
					    	}
					    	else{
					    		$.messager.show({
					                title: $.i18n.prop('error'),
					                msg: $.i18n.prop('deletefailed'),
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
            title: $.i18n.prop('alert'),
            msg: $.i18n.prop('admin.selectoneresource'),
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
}

function openNewAuDialog(){
	
	
	$('#common_dialog').dialog({
		title : $.i18n.prop('admin.editauthority'),
		width : 450,
		height : 590,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/goEditAuthority',
		buttons:[{
			text: $.i18n.prop('save'),
			width:80,
			handler:function(){checkAuthName();}
		},{
			text: $.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}],
		onLoad:function(){
			loadResource($("#aumodule").val(),null);
		}
	});
	
}

function loadResource(newValue,oldValue){
	getJSON(window.localStorage.ctx+'/getAllResource',
			{
				module : newValue
			},
			function(data){
				var json=validationData(data);
				console.log(data);
				$("#resedg").treegrid("loadData",data);
			}
	);
}

function checkAuthName(){
	if($('#auname').textbox('getValue')!=""){
		getJSON(window.localStorage.ctx+"/checkAuthName", 
				{
					autid: $('#auid').val(),
					auname: $('#auname').textbox('getValue')
				}
		,function(json){
	    	if(json.data){
	    		$.messager.show({
		            title: $.i18n.prop('error'),
		            msg: $.i18n.prop('admin.authoritynameexistindatabase'),
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
	    	}
	    	else{
	    		saveAuth();
	    	}
	    });
	}
}

function saveAuth(){
		$('#auids').val("");
	
		$('#auform').form('submit', {
		url: window.localStorage.ctx+"/saveAuth",
		onSubmit: function(){
			var rows=$('#resedg').datagrid('getChecked');
			var idsstr="";
			for(var i=0;i<rows.length;i++){
				idsstr+=rows[i].rid+",";
			}
			
			if(idsstr==""||$('#auname').textbox('getValue')==""){
				$.messager.show({
	                title: $.i18n.prop('error'),
	                msg: $.i18n.prop('admin.enterauthoritynameorselectresource'),
	                timeout:3000,
	                border:'thin',
	                showType:'slide'
	            });
				return false;
			}
			else{
				$('#auids').val(idsstr);
			}
		},
		success: function(data){
			var json=validationData(data);
			if(json.code==0){
				$('#common_dialog').dialog('close');
				$.getJSON(window.localStorage.ctx+"/getAllAuth", function(json){
			    	$("#audg").datagrid("loadData",json);
			    });
			}
			else{
				$.messager.show({
	                title: $.i18n.prop('error'),
	                msg: $.i18n.prop('savefailed'),
	                timeout:3000,
	                border:'thin',
	                showType:'slide'
	            });
			}
		}
	});
}

function openModifyAuDialog(index){
	$('#audg').datagrid('selectRow',index);
	var row=$('#audg').datagrid('getSelected');
	if(row){
		
		$('#common_dialog').dialog({
			title : $.i18n.prop('admin.editauthority'),
			width : 460,
			height : 560,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/goEditAuthority?id='+row.id,
			buttons:[{
				text:$.i18n.prop('save'),
				width:80,
				handler:function(){checkAuthName();}
			},{
				text:$.i18n.prop('cancel'),
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}],
			onLoad:function(){
				loadResource($("#aumodule").val(),null);
			}
		});

	}
	else{
		$.messager.show({
            title: $.i18n.prop('alert'),
            msg: $.i18n.prop('admin.selectoneauthority'),
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
}

function initresedg(row,data){
	var auids=$("#auids").val()+"";
	if(auids){
		var array = auids.split(",");
		
		for(var index=0; index<array.length; index++){
			var i = array[index];
			$('#resedg').datagrid('checkRow',parseInt(i));
		}
	}
	
}


function deleteAuth(index){
	$('#audg').datagrid('selectRow',index);
	var row=$('#audg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: $.i18n.prop('confirm'),
			msg: $.i18n.prop('admin.confirmdeleteauthority'),
			border:'thin',
			fn: function(r){
				if (r){
					 $.getJSON(window.localStorage.ctx+"/deleteAuthority?auid="+row.id, function(data){
						 	var json=validationData(data);
					    	if(json.code==0){
					    		$("#audg").datagrid('reload');
					    	}
					    	else{
					    		$.messager.show({
					                title: $.i18n.prop('error'),
					                msg: $.i18n.prop('deletefailed'),
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
            title: $.i18n.prop('alert'),
            msg: $.i18n.prop('admin.selectoneauthority'),
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
}

function doSearchAuth(value){
	$.getJSON(window.localStorage.ctx+"/getAllAuth?value="+value, function(data){
	 	var json=validationData(data);
	 	
	 	$("#audg").datagrid("loadData",json);
 	});
}


function openNewRoleDialog(){
	
	$('#common_dialog').dialog({
		title : $.i18n.prop('admin.editrole'),
		width : 480,
		height : 650,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/goEditRole',
		buttons:[{
			text:$.i18n.prop('save'),
			width:80,
			handler:function(){checkRoleName();}
		},{
			text:$.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
	
}


function checkRoleName(){
	if ($('#rolename').textbox('getValue')!="") {
		getJSON(window.localStorage.ctx+"/checkRoleName", 
				{
					roleid: $('#roleid').val(),
					rolename: $('#rolename').textbox('getValue')
				}
		,function(json){
	    	if(json.data){
	    		$.messager.show({
		            title:$.i18n.prop('error'),
		            msg: $.i18n.prop('admin.rolenameexistindatabase'),
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
	    	}
	    	else{
	    		saveRole();
	    	}
	    });
	}
}

function saveRole(){
	
	$('#roleform').form('submit', {
		url: window.localStorage.ctx+"/saveRole",
		onSubmit: function(){
			var rows=$('#audg_role').datagrid('getChecked');
			var idsstr="";
			for(var i=0;i<rows.length;i++){
				idsstr+=rows[i].id+",";
			}
			
			var idsstr1="";
			
			$('#modules_div').find("input[checkboxname='role_module_cb']").each(function(index,obj){
				if($(obj).checkbox('options').checked){
					idsstr1+=$(obj).val()+",";
				}
			});
			
			// 关联机构
			var idsstr2 = "";
			$('#institution_div').find("input[checkboxname='role_module_in']").each(function(index,obj){
				if($(obj).checkbox('options').checked){
					idsstr2+=$(obj).val()+",";
				}
			});
			
			if(idsstr==""||idsstr1==""||idsstr2==""||$('#rolename').textbox('getValue')==""){
				$.messager.show({
	                title: $.i18n.prop('error'),
	                msg: $.i18n.prop('admin.enterrolenameorselectmoduleorselectauthority'),
	                timeout:3000,
	                border:'thin',
	                showType:'slide'
	            });
				return false;
			}
			else{
				$('#roleids').val(idsstr);
				$('#modulesids').val(idsstr1);
				$('#institutionids').val(idsstr2);
			}
			
		},
		success: function(data){
			var json=validationData(data);
			if(json.code==0){
				$('#common_dialog').dialog('close');
				$.getJSON(window.localStorage.ctx+"/getAllRole", function(json){
			    	$("#roledg").datagrid("loadData",json);
			    });
			}
			else{
				$.messager.show({
		            title: $.i18n.prop('error'),
		            msg: $.i18n.prop('savefailed'),
		            timeout:3000,border:'thin',
		            showType:'slide'
		        });
			}
		}
	});
}


function openModifyRoleDialog(index){
	console.log(index)
	$('#roledg').datagrid('selectRow',index);
	var row=$('#roledg').datagrid('getSelected');
	if(row){

		$('#common_dialog').dialog({
			title : $.i18n.prop('admin.editrole'),
			width : 480,
			height : 650,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/goEditRole?id='+row.id,
			buttons:[{
				text: $.i18n.prop('save'),
				width:80,
				handler:function(){checkRoleName();}
			},{
				text: $.i18n.prop('cancel'),
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}]
		});
		
	}
	else{
		$.messager.show({
            title: $.i18n.prop('alert'),
            msg: $.i18n.prop('admin.selectonerole'),
            timeout:3000,border:'thin',
            showType:'slide'
        });
	}
}


function initaudg_role(data){
	for(var i=0;i<data.rows.length;i++){
		if(data.rows[i].ck=="1"){
			$("#audg_role").datagrid("checkRow",i);
		}
	}
	
}

function initdg_modules(data){
	for(var i=0;i<data.rows.length;i++){
		if(data.rows[i].ck=="1"){
			console.log(data.rows[i].ck)
			$("#dg_modules").datagrid("checkRow",i);
		}
	}
}


function deleteRole(index){
	console.log(index)
	$('#roledg').datagrid('selectRow',index);
	var row=$('#roledg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: $.i18n.prop('confirm'),
			msg: $.i18n.prop('admin.confirmdeleterole'),border:'thin',
			fn: function(r){
				if (r){
					 $.getJSON(window.localStorage.ctx+"/deleteRole?roleid="+row.id, function(data){
						 	var json=validationData(data);
					    	if(json.code==0){
					    		var index=$("#roledg").datagrid('getRowIndex',row);
					    		$("#roledg").datagrid('deleteRow',index);
					    	}
					    	else{
					    		$.messager.show({
					                title:$.i18n.prop('error'),
					                msg: $.i18n.prop('deletefailed'),
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
            title: $.i18n.prop('alert'),
            msg: $.i18n.prop('admin.selectonerole'),
            timeout:3000,border:'thin',
            showType:'slide'
        });
	}
}


function doSearchRole(value){
	$.getJSON(window.localStorage.ctx+"/getAllRole?value="+value, function(data){
	 	var json=validationData(data);
	 	
	 	$("#roledg").datagrid("loadData",json);
 	});
}

function operatecolumn_user(value, row, index){
	return $('#cmenu_user').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="openModifyUserDialog('+index+');"><span class="l-btn-left"><span class="l-btn-text"><span class="l-btn-text"><i class="icon iconfont icon-edit"></i></span></span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteUser('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-delete"></i></span></span></a>':'';//"<a href='#' class='easyui-linkbutton' name='operate' onclick='openModifyUserDialog()' ><i class='icon icon-edit1'></i>修改</a>&nbsp;&nbsp;"+
}

function operatecolumn_role(value, row, index){
	console.log();
	return $('#cmenu_role').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="openModifyRoleDialog('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-edit"></i></span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteRole('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-delete"></i></span></span></a>':'';//"<a href='#' class='easyui-linkbutton' name='operate' onclick='openModifyUserDialog()' ><i class='icon icon-edit1'></i>修改</a>&nbsp;&nbsp;"+
}
function operatecolumn_auth(value, row, index){
	console.log();
	return $('#cmenu_au').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="openModifyAuDialog('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-edit"></i></span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteAuth('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-delete"></i></span></span></a>':'';//"<a href='#' class='easyui-linkbutton' name='operate' onclick='openModifyUserDialog()' ><i class='icon icon-edit1'></i>修改</a>&nbsp;&nbsp;"+
}
function operatecolumn_res(value, row, index){
	console.log();
	return $('#cmenu_resource').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="openModifyResDialog('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-edit"></i></span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteRes('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-delete"></i></span></span></a>':'';//"<a href='#' class='easyui-linkbutton' name='operate' onclick='openModifyUserDialog()' ><i class='icon icon-edit1'></i>修改</a>&nbsp;&nbsp;"+
}
function onchange_handler_role(roleid,module,checked){
	if(checked){
		$.getJSON(window.localStorage.ctx+"/getAuthorityByRoleId?roleid="+roleid+"&module="+module, function(data){
		 	var json=validationData(data);

		 	$.each(json,function(index,row){
		 		$('#audg_role').datagrid("appendRow",row);
		 	});
	 	});
		
	}
	else{
		var rows=$('#audg_role').datagrid("getRows");
		var len=rows.length;
		for(var i=0;i<len;i++){
			if(rows[i]&&rows[i].module==module){
				
				$('#audg_role').datagrid("deleteRow",i);
				i--;
			}
		}
	}
}


function goModifyUserPassword(){
	var row=$('#userdg').datagrid('getSelected');
	if(row){
		$('<div></div>').dialog({
			id : 'userPasswordDialog',
			title : '修改密码',
			width : 500,height : 250,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/goModifyUserPassword?userid='+row.id,
			buttons:[{
				text: $.i18n.prop('save'),
				width:80,
				handler:function(){modifyUserPassword();}
			},{
				text: $.i18n.prop('cancel'),
				width:80,
				handler:function(){$('#userPasswordDialog').dialog('destroy');}
			}],
			onLoad:function(){

			},
			onClose:function(){
				$('#userPasswordDialog').dialog('destroy');
			}
		});
	}
}


function modifyUserPassword(){
	if(!checkUserComplexPassword($('#newpassword').textbox('getValue'))){
		return;
	}
	
	$('#userPasswordForm').form('submit', {
		url: window.localStorage.ctx+"/modifyUserPassword",
		onSubmit: function(data){
			if($('#newpassword').textbox('getValue')==""||$('#newpassword').textbox('getValue')!=$('#newpassword1').textbox('getValue')){
				_message("密码不一致！", "提醒");
				return false;
			}
		},
		success: function(data){
			var json=validationDataAll(data);
			if(json.code==0){
				_message("密码修改成功！", "成功");
				$('#userPasswordDialog').dialog('close');
			}else{
				_message("密码修改失败！", "失败");
			}
		}
	});
	
}

//验证密码复杂性
function checkUserComplexPassword(password){
	var ret = true;
	var flag = 0;
	var regex = /^(\s|\u00A0)+|(\s|\u00A0)+$/;
	if(password == "" || regex.test(password)){
		flag = -1;
		ret = false;
	}else{
		$.ajax({
		    url : window.localStorage.ctx+'/checkComplexPassword',
		    contentType : "application/x-www-form-urlencoded; charset=UTF-8",
		    async : false,
		    type : 'POST',
		    data : {password : password},
		    dataType : 'json',
		    success : function(data){
		    	flag = data;
		    }
		});
	}
	
	if(flag == -1){
		_message("密码不能为空,或包含空格！", "提醒");
	}else if(flag == 1){
		_message("密码长度不足！", "提醒");
		ret = false;
	}else if(flag == 2){
		_message("密码需要包含大小写！", "提醒");
		ret = false;
	}else if(flag == 3){
		_message("密码需要包含数字！", "提醒");
		ret = false;
	}else if(flag == 4){
		_message("密码需要包含字母！", "提醒");
		ret = false;
	}
	
	return ret;
}

