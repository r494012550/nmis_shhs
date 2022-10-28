/**
 *  已登记中的重新关联检查
 * @returns
 */
function openReassignStudyDlg(){
	var row=$("#dg_search_sch").datagrid("getSelected");
	if(row){
		$('#common_dialog').dialog({
			title:'重新关联检查'+'——当前患者:'+row.patientname,
			width:858,height:660,
			resizable: false,minimizable: false,maximizable: false,modal : true,
			border: 'thin',
			href: window.localStorage.ctx+"/register/goReassignStudy?patientpkid="+row.patientpkid
			 +"&admissionpkid="+row.admissionpkid+"&studyorderpkid="+row.studyorderpkid,
			buttons:[{
				text:"确定",
				width:80,
				handler:function(){reassignStudy();}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}],
			onLoad:function(){}
		});
	}else{
		_message('请选择一条数据' , '提醒');
	}
	
}

//取消关联病人
function disassociationAdmission(){
	var row=$("#dg_search_sch").datagrid("getSelected");
	if(row){
		$.messager.confirm({title:$.i18n.prop('confirm'),ok: '是',cancel: '否',msg: '是否取消关联病人？',border:'thin',
			fn: function(r){
				if(r){
					getJSON(window.localStorage.ctx+"/register/disassociationAdmission?orderid="+row.studyorderpkid,null, function(data){
						var json=validationData(data);
						if(json.code==0){
							//searchstudy();
							searchSchstudy();
				    	}else{
				    		_message('取消关联失败请重试，如果问题依然存在，请联系系统管理员！' , '提醒');
				    	}
				    });
				}
			}
		});
	}else{
		_message('请选择一条数据' , '提醒');
	}
	
}

//重新关联检查
function reassignStudy(){
	$("#splitPatientForm").form('submit',{
		url: window.localStorage.ctx+"/register/reassignStudy",
		onSubmit: function(){
	        if($("#patientname_split").textbox("getValue")==""||
		        $("#pinyin_split").textbox("getValue")==""||
		        $("#sex_split").combobox("getValue")==""||
		        $("#age_split").textbox("getValue")==""||
		        $("#birthdate_split").datebox("getValue")==""||
		        $("#patientsource_split").textbox("getValue")==""){
	        	_message('请填入数据' , '提醒');
		        return false;
		    }	
	    },
	    success:function(data){
			var result=validationDataAll(data);
			if(result.code==0){
				_message('保存成功' , '保存成功');
				searchSchstudy();
				$('#common_dialog').dialog('close');
		        
			}else{
				_message(result.message , '保存失败');
			}
	    }
	});
}
/**
 * 选择已有病人信息
 * @returns
 */
function goChoosePatient(){
	$('<div></div>').dialog({
		id:'newDialog',
		title:'选择患者',
		width:900,height:660,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href: window.localStorage.ctx+"/register/goChoosePatient",
		buttons:[{
			text:'确定',
			width:80,
			handler:function(){
				var row=$("#patientdg1_split").datagrid("getSelected");
				if(row){
					enableEditPatient(false);
					enableEditAdmission(false);
					clearSplitPatient();
					clearSplitAdmission();
					console.log(row);
					fillSplitPatient(row);
					fillSplitAdmission(row);
				}else{
					_message('请选择一条数据' , '提醒');
				}
			}
		},{
			text:'关闭',
			width:80,
			handler:function(){ $('#newDialog').dialog('destroy');}
		}],
		onClose:function(){
			$('#newDialog').dialog('destroy');
		}
	});
}
/**
 * 新建病人信息
 * @returns
 */
function newPatient(){
	if($("#patientname_split").val()!=""){
		$.messager.confirm({
			title:$.i18n.prop('confirm'),
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '正在录入中，是否放弃当前的患者信息和入院信息？',
			fn: function(r){
				if (r){
					clearSplitPatient();
					clearSplitAdmission();
					enableEditPatient(true);
					$('#chooseAdmission_btn').linkbutton('disable');
					$('#newAdmission_btn').linkbutton('enable');
				}
			}
		});
	}else{
		enableEditPatient(true);
		$('#chooseAdmission_btn').linkbutton('disable');
		$('#newAdmission_btn').linkbutton('enable');
	}

}
/**
 * 选择已有入院信息
 * @returns
 */
function goChooseAdmission(){
	$('<div></div>').dialog({
		id:'newDialog',
		title:'选择患者',
		width:858,height:660,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href: window.localStorage.ctx+"/register/goChooseAdmission?patientidfk="+$("#patientkid_split").val(),
		buttons:[{
			text:'确定',
			width:80,
			handler:function(){
				var row=$("#admission1_split").datagrid("getSelected");
				if(row){
					enableEditAdmission(false);
					clearSplitAdmission();
					fillSplitAdmission(row);
				}else{
					_message('请选择一条数据' , '提醒');
				}
			}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#newDialog').dialog('destroy');}
		}],
		onClose:function(){
			$('#newDialog').dialog('destroy');
		}
	});
}
/**
 * 选择已有入院信息
 * @returns
 */
function newAdmission(){
	console.log($("#patientsource_split").val());
	if($("#patientsource_split").val()!=""){
		$.messager.confirm({
			title:$.i18n.prop('confirm'),
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '正在录入中，是否放弃当前的入院信息？',
			fn: function(r){
				if (r){
					clearSplitAdmission();
					enableEditAdmission(true);
				}
			}
		});
	}else{
		enableEditAdmission(true);
	}	
}
/**
 * 填充病人信息
 * @returns
 */
function fillSplitPatient(row){
	
	$("#patientkid_split").val(row.patientkey);
	$("#patientid_split").textbox("setValue",row.patientid);
	$('#patientname_split').textbox("setValue",row.patientname);
	$('#pinyin_split').textbox("setValue",row.py);
	$('#sex_split').combobox('select', row.sex);

	$('#birthdate_split').datebox("setValue",row.birthdate);
	$('#height_split').textbox("setValue",row.height);
	$('#weight_split').textbox("setValue",row.weight);
	$('#title_split').textbox("setValue",row.title);
	$('#telephone_split').textbox("setValue",row.telephone);
	$('#address_split').textbox("setValue",row.address);
	
//	var typeId=row.patientkey;
//	if(row.patientidfk){
//		typeId=row.patientidfk;
//	}
	
	getJSON(window.localStorage.ctx+"/register/getRemark",
		{
			typeId : row.patientkey,
			type : "patient"
		},
		function(data){
			$("#patientremark_split").textbox('setValue',validationDataAll(data).data);
		}
	);
	
	var str=$("#birthdate_split").datebox("getValue");
	var arr=str.split("-");
	var birthdate=new Date(arr[0],arr[1]-1,arr[2]);
	getAgeFromBirthdate_split(birthdate);
	
	$('#chooseAdmission_btn').linkbutton('enable');
	$('#newAdmission_btn').linkbutton('enable');
	$('#newDialog').dialog('destroy');
}
/**
 * 填充入院信息
 * @returns
 */
function fillSplitAdmission(row){
	$("#admissionkid_split").val(row.admissionkey);
	$("#admissionid_split").textbox("setValue",row.admissionid);
	$('#patientsource_split').combobox("select",row.patientsource);
	$('#cardno_split').textbox("setValue",row.cardno);
	$('#inno_split').textbox("setValue",row.inno);
	$('#outno_split').textbox("setValue",row.outno);
	$('#wardno_split').combobox('select', row.wardno);
	$('#bedno_split').combobox("select",row.bedno);
	$('#insurance_split').textbox("setValue",row.insurance);
	$('#institutionid_split').combobox("select",row.institutionid);
	$('#admission_date_split').datebox("setValue",row.adm_date);
	$('#discharge_date_split').datebox("setValue",row.dis_date);
	$('#admittingDiagnosis_split').textbox('setValue',row.admittingdiagnosis);
	$('#subjective_split').textbox('setValue',row.subjective);
	
	getJSON(window.localStorage.ctx+"/register/getRemark",
		{
			typeId : row.admissionkey,
			type : "admission"
		},
		function(data){
			$("#admissionremark_split").textbox('setValue',validationDataAll(data).data);
		}
	);
	$('#newDialog').dialog('destroy');
}
/**
 * 清空填充的病人信息
 * @returns
 */
function clearSplitPatient(){
	$("#patientkid_split").val("");
	$("#patientid_split").textbox("setValue","");
	$('#patientname_split').textbox("setValue","");
	$('#pinyin_split').textbox("setValue","");
	$('#sex_split').combobox('select', "");
	$("#age_split").textbox("setValue","");
    $("#age_unit_split").combobox('select', 'Y');
	$('#birthdate_split').datebox("setValue","");
	$('#height_split').textbox("setValue","");
	$('#weight_split').textbox("setValue","");
	$('#title_split').textbox("setValue","");
	$('#telephone_split').textbox("setValue","");
	$('#address_split').textbox("setValue","");
	$("#patientremark_split").textbox('setValue',"");
}
/**
 * 清空填充的入院信息
 * @returns
 */
function clearSplitAdmission(){
	$("#admissionkid_split").val("");
	$("#admissionid_split").textbox("setValue","");
	$('#patientsource_split').combobox("select","");
	$('#cardno_split').textbox("setValue","");
	$('#inno_split').textbox("setValue","");
	$('#outno_split').textbox("setValue","");
	$('#wardno_split').combobox('select', "");
	$('#bedno_split').combobox("select","");
	$('#insurance_split').textbox("setValue","");
	$('#institutionid_split').combobox("select","");
	$('#admission_date_split').datebox("setValue","");
	$('#discharge_date_split').datebox("setValue","");
	$('#admittingDiagnosis_split').textbox("setValue","");
	$('#subjective_split').textbox("setValue","");
	$("#admissionremark_split").textbox("setValue","");
}
/**
 * 是否可编辑病人信息
 * @param para
 * @returns
 */
function enableEditPatient(para){
	if(para){
		$('#patientname_split').textbox('readonly',false);
		$('#pinyin_split').textbox('readonly',false);
		$('#sex_split').combobox('readonly',false);
		$('#age_split').numberbox('readonly',false);
		$('#age_unit_split').combobox('readonly',false);
		$('#birthdate_split').datebox("readonly",false);
		$('#height_split').numberbox('readonly',false);
		$('#weight_split').numberbox('readonly',false);
		$('#title_split').textbox('readonly',false);
		$('#telephone_split').textbox('readonly',false);
		$('#address_split').textbox('readonly',false);
		$('#patientremark_split').textbox('readonly',false);
		
		$('#birthdate_split').datebox('calendar').calendar({
			validator:function(date){
				var now=new Date();
				var d1=new Date(now.getFullYear(),now.getMonth(),now.getDate());
				return date<=d1;
			}
		});
	}else{
		$('#patientname_split').textbox('readonly',true);
		$('#pinyin_split').textbox('readonly',true);
		$('#sex_split').combobox('readonly',true);
		$('#age_split').numberbox('readonly',true);
		$('#age_unit_split').combobox('readonly',true);
		$('#birthdate_split').datebox("readonly",true);
		$('#height_split').numberbox('readonly',true);
		$('#weight_split').numberbox('readonly',true);
		$('#title_split').textbox('readonly',true);
		$('#telephone_split').textbox('readonly',true);
		$('#address_split').textbox('readonly',true);
		$('#patientremark_split').textbox('readonly',true);
	}
}
/**
 * 是否可编辑入院信息
 * @param para
 * @returns
 */
function enableEditAdmission(para){
	if(para){
		$('#patientsource_split').combobox('readonly',false);
		$('#cardno_split').textbox('readonly',false);
		$('#inno_split').textbox('readonly',false);
		$('#outno_split').textbox('readonly',false);
		$('#wardno_split').combobox('readonly',false);
		$('#bedno_split').combobox('readonly',false);
		$('#insurance_split').textbox('readonly',false);
		$('#institutionid_split').combobox('readonly',false);
		$('#admission_date_split').datebox("readonly",false);
		$('#discharge_date_split').datebox("readonly",false);
		$('#admittingDiagnosis_split').textbox('readonly',false);
		$('#subjective_split').textbox('readonly',false);
		$('#admissionremark_split').textbox('readonly',false);
	}else{
		$('#patientsource_split').combobox('readonly',true);
		$('#cardno_split').textbox('readonly',true);
		$('#inno_split').textbox('readonly',true);
		$('#outno_split').textbox('readonly',true);
		$('#wardno_split').combobox('readonly',true);
		$('#bedno_split').combobox('readonly',true);
		$('#insurance_split').textbox('readonly',true);
		$('#institutionid_split').combobox('readonly',true);
		$('#admission_date_split').datebox("readonly",true);
		$('#discharge_date_split').datebox("readonly",true);
		$('#admittingDiagnosis_split').textbox('readonly',true);
		$('#subjective_split').textbox('readonly',true);
		$('#admissionremark_split').textbox('readonly',true);
	}
}


//填写年龄获取出生日期
function calculate_age1_split(newValue,oldValue){
	if(!isNaN(newValue)&&newValue>0){
		var ageunit=$("#age_unit_split").textbox("getValue");
		var str=$("#birthdate_split").datebox("getValue");
		var birth=getBirthday(newValue,ageunit,str);
		$("#birthdate_split").datebox('setValue',birth);	
	}
}

//选择单位获取出生日期
function calculate_age2_split(newValue,oldValue){
	var value=$('#age_split').numberbox('getValue');
	if(value==""){
		return;
	}
	var ageunit=$("#age_unit_split").textbox("getValue");
	var str=$("#birthdate_split").datebox("getValue");
	var birth=getBirthday(value,ageunit,str);
	$("#birthdate_split").datebox('setValue',birth);
}

function getAgeFromBirthdate_split(birthdate){
	var tmp=new Date();
	var today=new Date(tmp.getFullYear(),tmp.getMonth(),tmp.getDate());
	$("#age_split").numberbox({onChange:function(){}});
	if(birthdate.getTime()==today.getTime()){
		$("#age_split").numberbox("setValue",0);
		$("#age_unit_split").combobox('select', 'D');
		$("#age_split").numberbox("setValue",1);
		$("#age_split").numberbox({onChange:function(newValue,oldValue){
			calculate_age1_split(newValue,oldValue);
		}});
		return;
	}
	var year=today.getFullYear()-birthdate.getFullYear();
	if(year>0){
		$("#age_split").numberbox("setValue",year);
		$("#age_unit_split").combobox('select', 'Y');
	}else if(year==0){
		var month=today.getMonth()-birthdate.getMonth();
		if(month>0){
			$("#age_split").numberbox("setValue",month);
			$("#age_unit_split").combobox('select', 'M');
		}
		else if(month==0){
			var day=today.getDate()-birthdate.getDate();
			if(day>0){
				$("#age_split").numberbox("setValue",day);
				$("#age_unit_split").combobox('select', 'D');
			}
			else{
				$("#age_split").numberbox("setValue",1);
				$("#age_unit_split").combobox('select', 'D');
			}
		}
	}
	$("#age_split").numberbox({onChange:function(newValue,oldValue){
		calculate_age1_split(newValue,oldValue);
	}});
	
}

//设置申请科室和病区
function setDept_split(newValue,oldValue){
	getJSON(window.localStorage.ctx+'/dic/getDeptFromCache',
			{
				type : newValue
			},
			function(data){
				var json=validationData(data);
				if(!json.code){
					if(newValue=="I"){//病人来源不是住院时病区为空
						var clear=true;
						for(var i=0;i<json.length;i++){
							if(json[i].id==$('#wardno_split').combobox('getValue')){
								clear=false;
							}
						}
						//判断是否清空选择选中项
						if(clear){
							$('#wardno_split').combobox('clear');
						}
						$("#wardno_split").combobox("loadData",json);
					}else{
						$('#wardno_split').combobox('clear');
						$("#wardno_split").combobox("loadData",[]);
					}
				}
			}
	);
}

/**
 *  重新关联检查中，选择已有患者中的查询操作
 * @returns
 */
function findPat_split() {
	if ($('#patientid1_split').val() == "" && $('#studyid1_split').val() == "" && $('#patientname1_split').val() == "") {
		_alert('请输入需要查询的患者编号、检查编号或患者姓名！' , '提醒');
	} else {
		getJSON(window.localStorage.ctx+"/register/findPatAndAdmission",
				{
					patientid:$('#patientid1_split').val(),
					studyid:$('#studyid1_split').val(),
					patientname:$('#patientname1_split').val(),
				},	
				function(data){
					var json=validationData(data);
					$("#patientdg1_split").datagrid("loadData",json);
				}
			);	
	}
}
