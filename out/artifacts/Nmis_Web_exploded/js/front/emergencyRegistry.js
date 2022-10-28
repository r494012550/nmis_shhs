//配置急诊默认信息
function setEmergDefInfo(){
	$('#common_dialog').dialog({
		title:'配置急诊信息',
		width:820,
		height:420,
		closed : false,
		cache : false,
		resizable: false,
		minimizable: false,
		maximizable: false,
		border: 'thin',
		href: window.localStorage.ctx+'/register/setEmergDefInfo',
		modal : true,
		buttons:[{
			text: $.i18n.prop('save'),
			width:80,
			handler:function(){saveEmergDefInfo()}
		},{
			text:'删除',
			width:80,
			handler:function(){deleteEmergDefInfo()}
		},{
			text:$.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}],
		onLoad:function(){
			getJSON(window.localStorage.ctx +"/register/findEmergDefInfo",null,function(json) {
				if(json.length>0){
					$('#emergency_menubtn_emerg').menubutton({
					    menu: '#emergency_menu_emerg'
					});
					  
					$.each(json,function(index,temp){
						$('#emergency_menu_emerg').menu('appendItem',{
							text : temp.configname,
							onclick : function(){
								fillEmergencyPara(temp.configname);
//								var item = $('#emergency_menu_emerg').menu('findItem', temp.configname);
//								if(item){
//									$('#emergency_menu_emerg').menu('removeItem', item.target);
//								}
							}
						});
					});
				}
			});
			
			$('#birthdate_emerg').datebox('calendar').calendar({
				validator:function(date){
					var now=new Date();
					var d1=new Date(now.getFullYear(),now.getMonth(),now.getDate());
					return date<=d1;
				}
			});
			$('#searchItemtree_emerg').textbox('textbox').keydown(function(e){
				if(e.keyCode == 13){
		        	var rows1=$("#itemtree_emerg").datalist("getRows");
		        	getJSON(window.localStorage.ctx+"/dic/getExamItemDicFromCache",
						{
							searchtext : $('#searchItemtree_emerg').textbox('getValue'),
							modality : $("#modality_emerg").combobox("getValue"),
//							organ : $("#organ_emerg").textbox("getValue"),
							equipment : 19
						},
						function(data) {
							var json=validationData(data);
							$("#itemtree_emerg").datalist({
								onLoadSuccess:function(){
									var rows2=$("#itemtree_emerg").datalist("getRows");
									if(rows1.length==1&&rows2.length==1&&rows1[0].item_name==rows2[0].item_name){
											treeclick_emerg(0,rows2[0]);
									}
								}
							});
							$("#itemtree_emerg").datalist('loadData',json);
						}
					);
				}
			});
		}
	});
}

function fillEmergencyPara(configname){
	clearEmergencyPara();
	getJSON(window.localStorage.ctx+"/register/emergencyRegister",
			{
				configname : configname
			},function(data){
				var result=validationDataAll(data);
				if(result.code==0){
					if(result.data.flag){
						var defaultInfo = result.data.defaultInfo;
						$("#defaultInfoid").val(defaultInfo.id);
						$("#configname_emerg").textbox("setValue", defaultInfo.configname);
						$("#patientname_emerg").textbox("setValue",defaultInfo.patientname);
					    $("#pinyin_emerg").textbox("setValue",defaultInfo.py);
					    $("#sex_emerg").combobox('select', defaultInfo.sex);
					    $("#birthdate_emerg").datebox("setValue",defaultInfo.birthdate);
						var str=$("#birthdate_emerg").datebox("getValue");
						var arr=str.split("-");
						var birthdate=new Date(arr[0],arr[1]-1,arr[2]);
						getAgeFromBirthdate_emerg(birthdate);
						
						$("#modality_emerg").combobox('select', defaultInfo.modality_type);
					    $("#modality_dic_emerg").combobox("select", defaultInfo.modalityid);
					    if(result.data.studyitem.length>0){
					    	$('#modality_emerg').combobox('disable');
							$('#modality_dic_emerg').combobox('disable');
							$("#studydg_item_emerg").datagrid("loadData",result.data.studyitem);
					    }
					}else{
						$.messager.show({
			                title:'提醒',
			                msg:"未配置急诊信息",
			                timeout:5000,
			                border:'thin',
			                showType:'slide'
			            });
					}
				}
	});
}

function clearEmergencyPara(){
	$("#configname_emerg").textbox("setValue","");
	$("#patientname_emerg").textbox("setValue","");
    $("#pinyin_emerg").textbox("setValue","");
    $("#age_emerg").textbox("setValue","");
    $("#age_unit_emerg").combobox('select', 'Y');
    $("#birthdate_emerg").datebox("setValue","");
    $('#studydg_item_emerg').datagrid('loadData',{total:0,rows:[]});
    $("#itemsstr_emerg").val("");
    $("#defaultInfoid").val("");
    $("#modality_emerg").combobox('enable');
    $("#modality_dic_emerg").combobox('enable');
}

//保存急诊配置
function saveEmergDefInfo(){
	var defaultInfoid = $('#defaultInfoid').val();
	$('#emergencyForm').form('submit', {
		url: window.localStorage.ctx+"/register/saveEmergDefInfo",
		onSubmit: function(param){
			param.modality_type = $('#modality_emerg').combobox('getValue');
			param.modalityid = $('#modality_dic_emerg').combobox('getValue');
			var rows=$('#studydg_item_emerg').datagrid('getRows');
			if (rows!=null&&rows.length>0) {
				$("#itemsstr_emerg").val(JSON.stringify(rows));
			} else {
				$("#itemsstr_emerg").val("");
			}
			return $(this).form('validate');
		},
		success: function(data){
			var json=validationDataAll(data);
			
			if(json.code==0){
				if(json.data.ret == "2"){
					$.messager.show({
			            title:'提醒',
			            msg:'该名称已被占用！',
			            timeout:3000,
			            border: 'thin',
			            showType:'slide'
			        });
				}else if(defaultInfoid != json.data.defaultInfo.id){
					/*$('#emergency_menu_emerg').menu('appendItem',{
						text : json.data.configname,
						onclick : function(){
							fillEmergencyPara(json.data.configname);
						}
					});*/
					$('#emergency_menubtn').menubutton({
					    menu: '#emergency_menu'
					});
					$('#emergency_menu').menu('appendItem',{
						text : json.data.defaultInfo.configname,
						onclick : function(){
							emergencyRegister(json.data.defaultInfo.configname);
						}
					});
				}
				
				
			}else{
				$.messager.show({
		            title:'错误提醒',
		            msg:json.message,
		            timeout:3000,
		            border: 'thin',
		            showType:'slide'
		        });
			}
			$('#common_dialog').dialog('close');
		}
	});
}

function deleteEmergDefInfo(){
	if($('#defaultInfoid').val()){
		var configname = $('#configname_emerg').textbox('getValue');
		getJSON(window.localStorage.ctx+"/register/deleteEmergDefInfo",
				{
					id : $('#defaultInfoid').val()
				},function(data){
					var result=validationDataAll(data);
					if(result.code==0){
						clearEmergencyPara();
						var item_emerg = $('#emergency_menu_emerg').menu('findItem', configname);
						if(item_emerg){
							$('#emergency_menu_emerg').menu('removeItem', item_emerg.target);
						}
						var item = $('#emergency_menu').menu('findItem', configname);
						if(item){
							$('#emergency_menu').menu('removeItem', item.target);
						}
					}
		});
	}else{
		$.messager.show({
            title:'提醒',
            msg:'当前急诊配置未保存，无法删除',
            timeout:3000,
            border: 'thin',
            showType:'slide'
        });
	}
}

function emergencyRegister(configname){
	if(!configname){
		$.messager.show({
            title:'提醒',
            msg:"未配置急诊信息；如果已经配置请点击配置名称",
            timeout:5000,
            border:'thin',
            showType:'slide'
        });
		return;
	}
	cancelSave();
	getJSON(window.localStorage.ctx+"/register/emergencyRegister",
			{
				configname : configname
			},function(data){
				var result=validationDataAll(data);
				if(result.code==0){
					if(result.data.flag){
						extractFlag = true;
						var defaultInfo = result.data.defaultInfo;
						$("#patientname_reg").textbox("setValue",defaultInfo.patientname);
					    $("#pinyin_reg").textbox("setValue",defaultInfo.py);
					    $("#sex_reg").combobox('select', defaultInfo.sex);
					    $("#birthdate_reg").datebox("setValue",defaultInfo.birthdate);
						var str=$("#birthdate_reg").datebox("getValue");
						var arr=str.split("-");
						var birthdate=new Date(arr[0],arr[1]-1,arr[2]);
						getAgeFromBirthdate(birthdate);
						$("#patientsource_reg").combobox("select",defaultInfo.patientsource);
						
						$("#modality_reg").combobox('select', defaultInfo.modality_type);
					    $("#modality_dic_reg").combobox("select", defaultInfo.modalityid);
					    if(result.data.studyitem.length>0){
					    	setStudy(true);
					    	$("#studydg_item_reg").datagrid("loadData",result.data.studyitem);
					    }
						extractFlag = false;
					}else{
						$.messager.show({
			                title:'提醒',
			                msg:"未配置急诊信息",
			                timeout:5000,
			                border:'thin',
			                showType:'slide'
			            });
					}
				}
	});
}

//填写年龄获取出生日期
function calculate_age1_emerg(newValue,oldValue){
	if(!isNaN(newValue)&&newValue>0){
		var ageunit=$("#age_unit_emerg").textbox("getValue");
		var str=$("#birthdate_emerg").datebox("getValue");
		var birth=getBirthday(newValue,ageunit,str);
		$("#birthdate_emerg").datebox('setValue',birth);	
	}
}
//选择单位获取出生日期
function calculate_age2_emerg(newValue,oldValue){
	var value=$('#age_emerg').numberbox('getValue');
	if(value==""){
		return;
	}
	var ageunit=$("#age_unit_emerg").textbox("getValue");
	var str=$("#birthdate_emerg").datebox("getValue");
	var birth=getBirthday(value,ageunit,str);
	$("#birthdate_emerg").datebox('setValue',birth);
}

function setOrgan_emerg(newValue,oldValue){
	if(newValue){
		$("#organ_emerg").combobox({
			url:window.localStorage.ctx+'/dic/getOrganDic?modality='+newValue,
		});
		$("#suborgan_emerg").combobox("clear");
		$("#suborgan_emerg").combobox("loadData",[]);
		
		getJSON(window.localStorage.ctx+'/dic/getModalityDic',
				{
					modality : newValue,
					user_institution : $('#user_institution').val(),
				},
				function(data){
		
					$('#modality_dic_emerg').combobox('loadData',data);
					if(data.length>0){
						$("#modality_dic_emerg").combobox('select',data[0].id);
					}else{
						$('#modality_dic_emerg').combobox('clear');
					}
				});
	}else{
		$("#organ_emerg").combobox("clear");
		$("#organ_emerg").combobox("loadData",[]);
		
		$('#modality_dic_emerg').combobox('clear');
		$('#modality_dic_emerg').combobox('loadData',[]);
	}
}

//选择部位后加载子部位
function setSuborgan_emerg(newValue,oldValue){
	if(newValue){
		$("#suborgan_emerg").combobox({
			url:window.localStorage.ctx+'/dic/getOrganDic?parent='+newValue,
			onLoadSuccess:function(data){
			}
		});
	}else{
		$("#suborgan_emerg").combobox("clear");
		$("#suborgan_emerg").combobox("loadData",[]);
	}
	
	loadItemtree("_emerg");
}

function modalitydic_onChange_emerg(){
	loadItemtree("_emerg");
}

function getAgeFromBirthdate_emerg(birthdate){
	var tmp=new Date();
	var today=new Date(tmp.getFullYear(),tmp.getMonth(),tmp.getDate());
	$("#age_emerg").numberbox({onChange:function(){}});
	if(birthdate.getTime()==today.getTime()){
		$("#age_emerg").numberbox("setValue",0);
		$("#age_unit_emerg").combobox('select', 'D');
		$("#age_emerg").numberbox("setValue",1);
		$("#age_emerg").numberbox({onChange:function(newValue,oldValue){
			calculate_age1_emerg(newValue,oldValue);
		}});
		return;
	}
	var year=today.getFullYear()-birthdate.getFullYear();
	if(year>0){
		var month=today.getMonth()-birthdate.getMonth();
		if(month<0){
			year = year - 1;
		}else if(month == 0 && today.getDate()-birthdate.getDate()<0){
			year = year - 1;
		}
		$("#age_emerg").numberbox("setValue",year);
		$("#age_unit_emerg").combobox('select', 'Y');
	}else if(year==0){
		var month=today.getMonth()-birthdate.getMonth();
		if(month>0){
			$("#age_emerg").numberbox("setValue",month);
			$("#age_unit_emerg").combobox('select', 'M');
		}
		else if(month==0){
			var day=today.getDate()-birthdate.getDate();
			if(day>0){
				$("#age_emerg").numberbox("setValue",day);
				$("#age_unit_emerg").combobox('select', 'D');
			}
			else{
				$("#age_emerg").numberbox("setValue",1);
				$("#age_unit_emerg").combobox('select', 'D');
			}
		}
	}
	$("#age_emerg").numberbox({onChange:function(newValue,oldValue){
		calculate_age1_emerg(newValue,oldValue);
	}});	
}

//选择检查项目
function treeclick_emerg(rowIndex, rowData){
	if(rowData.item_name!=null){
		var rows=$('#studydg_item_emerg').datagrid('getRows');
		if(rows!=null&&rows.length>0){
			for(var i=0;i<rows.length;i++){
				if(rows[i].item_name==rowData.item_name){
					return;
				}
			}
		}
		$('#studydg_item_emerg').datagrid('appendRow',{
			id: rowData.id,
			modality: rowData.type,
			item_name: rowData.item_name,
			item_id: rowData.id,
			organ: rowData.organfk||"",
			suborgan: rowData.suborganfk||"",
			price: rowData.price||"",
			realprice: rowData.price||"",
			charge_status:'已收费'
		});
		
		$("#modality_emerg").combobox('disable');
		$("#modality_dic_emerg").combobox('disable');
	}
}