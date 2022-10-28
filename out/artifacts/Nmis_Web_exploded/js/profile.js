/**
 * 
 */

function openMyConfigDialog(){
	
//	{
//		text:'保存',
//		width:80,
//		handler:function(){}
//	},
	
	
	$('#common_dialog').dialog({
		title : '我的配置',
		width : 500,
		height : 500,
		resizable: false,
		minimizable: false,
		maximizable: false,
		border: 'thin',
		href : window.localStorage.ctx+'/profile/toMyConfig',
		modal : true,
		buttons:[{
			text:'关闭',
			width:80,
			handler:function(){
				$('#common_dialog').dialog('close');
			}
		}]
	});
}

function modifyPassword(){
	if(!checkProfileComplexPassword($('#newpassword').textbox('getValue'))){
		return;
	}
	$('#passwordform').form('submit', {
		url: window.localStorage.ctx+"/profile/modifyPassword",
		onSubmit: function(){
			if($('#newpassword').textbox('getValue')==""||$('#newpassword1').textbox('getValue')==""
				||$('#oldpassword').textbox('getValue')==""||$('#newpassword').textbox('getValue')!=$('#newpassword1').textbox('getValue')){
				return false;
			}
		},
		success: function(data){
			var json=validationData(data);
			if(json.code==0){
				$('#oldpassword').textbox('setValue','');
				$('#newpassword').textbox('setValue','');
				$('#newpassword1').textbox('setValue','');
				
				$.messager.show({
		            title:'提示',
		            msg:"修改成功！",
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
			}
			else{
				var message="保存失败请重试，如果问题依然存在，请联系系统管理员！";
				if(json.message=="passworderror"){
					message="密码错误！请重新输入当前密码！";
					$('#oldpassword').textbox('setValue','');
				}
				
				$.messager.show({
		            title:'错误提醒',
		            msg:message,
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
			}
		}
	});
}

//验证密码复杂性
function checkProfileComplexPassword(password){
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

function checkModulesList(data){
	
	var rows=$('#moduleslist').datagrid('getRows');
	for(var i=0;i<rows.length;i++){
		if((rows[i].module)==$("#defaultmodule").val()){
			$("#moduleslist").datalist("checkRow",i);
		}
	}
}

function setMyDefaultModule(row){
	$.getJSON(window.localStorage.ctx+"/profile/setMyDefaultModule?defaultmodule="+row.module, function(data){
		var json=validationDataAll(data);
		if(json.code!=0){
			$.messager.show({
	            title:'错误提醒',
	            msg:'保存失败请重试，如果问题依然存在，请联系系统管理员！',
	            timeout:3000,
	            border:'thin',
	            showType:'slide'
	        });
		}
	});
}

function saveUserProfiles(checked,profiles){
	var value=checked?'1':'0';
	$.getJSON(window.localStorage.ctx+"/profile/saveUserProfiles?profiles="+profiles+"&value="+value, function(data){
		var json=validationDataAll(data);
		if(json.code!=0){
			$.messager.show({
	            title:'错误提醒',
	            msg:'保存失败请重试，如果问题依然存在，请联系系统管理员！',
	            timeout:3000,
	            
	            border:'thin',
	            showType:'slide'
	        });
		}
	});
}

function saveUserProfiles_value(value,profiles){
	getJSON(window.localStorage.ctx+'/profile/saveUserProfiles',
			   {
			    profiles: profiles,
			    value: value
			   },
			   function(data){
			    var json=validationDataAll(data);
			    if (json.code!=0) {
			     _message("保存失败请重试，如果问题依然存在，请联系系统管理员！");
			    }
			   });
}
 function modifyModality(){
	 $('#modalityform').form('submit', {
			url: window.localStorage.ctx+"/modifyModality",
			success: function(data){
				var json=validationData(data);
				if(json.code==0){
					$.messager.show({
			            title:'提示',
			            msg:"保存成功！",
			            timeout:3000,
			            border:'thin',
			            showType:'slide'
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
 }
 function saveUserProfiles(checked,profiles){
		var value=checked?'1':'0';
		$.getJSON(window.localStorage.ctx+"/profile/saveUserProfiles?profiles="+profiles+"&value="+value, function(data){
			var json=validationDataAll(data);
			if(json.code!=0){
				$.messager.show({
		            title:'错误提醒',
		            msg:'保存失败请重试，如果问题依然存在，请联系系统管理员！',
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
			}
		});
	}
