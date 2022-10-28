var saveInjectionFlag = false;//保存表单提交标识，saveInjectionFlag = true时不提交，避免重复提交

$(function(){
	myCache=JSON.parse(window.localStorage.myCache);
});

//初始化查询页面
function initWestsearch(){
	$('#westsearch_form').form({
		url : window.localStorage.ctx + "/injection/findStudyorder",
		onSubmit : function() {
			var datefrom = $('#datefrom_west').datebox('getValue');  
		    var dateto = $('#dateto_west').datebox('getValue');  
			if(datefrom!='' && !validator_date(datefrom)){
				_message($.i18n.prop('filter.starttimeformaterror') , $.i18n.prop('error'));
				return false;
			}
			if(dateto!='' && !validator_date(dateto)){
				_message($.i18n.prop('filter.endtimeformaterror') , $.i18n.prop('error'));
				return false;
			}
			
			$('#progress_dlg').dialog('open');
		},
		success : function(data) {
			$('#progress_dlg').dialog('close');
			if(data.code != -1){
				$("#dg_west").datagrid("loadData", validationData(data));
				$('#count_injection').html($("#dg_west").datagrid("getRows").length);
				if($("#dg_west").datagrid("getRows").length>2000){
					_message('查询结果超过系统显示上限，无法全部显示。请重新设置查询条件，缩小检查范围！');
				}
			}
		}
	});
	
	
//	$('#isConsulted_west').combobox('select', myCache.StudyOrderStatus.CONSULTED);
	$('#isInjected_west').combobox('select', 'NO');
	setTimeout(function(){ searchStudyorder(); }, 500);
}

//查询检查信息
function searchStudyorder(){
	$('#westsearch_form').form('submit');
}

//双击检查信息
function dbClikdg_west(row){
	if(parseInt(row.status) < parseInt(myCache.StudyOrderStatus.CONSULTED)){
		_message('当前病人未问诊，请先问诊再注射！' , '提醒');
		return;
	}
	console.log(row);
	$('#injectionForm').form('load', row);
	$('#orderid').val(row.studyorderpkid);
	$('#studyid').val(row.studyid);
	$('#patientid').val(row.patientpkid);
	$('#admissionid').val(row.admissionidfk);
	$('#orderstatus').val(row.status);
	
	$('#injectionid').val(row.injectionid);
	$('#agedisplay').textbox('setValue', row.age + row.ageunitdisplay);
	
	$('#printSave').linkbutton('enable');
	$('#printEmpty').linkbutton('enable');
	$('#printInspection').linkbutton('enable');
	$('#printExamine').linkbutton('enable');
}

//根据部门获取设备
function setModality(newValue, oldValue){
	if(newValue){
		getJSON(window.localStorage.ctx+'/dic/getModalityDic',
				{
					departmentid : newValue,
					role:'modality'
				},
				function(data){
		
					$('#modality_west').combobox('loadData',data);
					if(data.length>0){
						$("#modality_west").combobox('select',data[0].id);
					}else{
						$('#modality_west').combobox('clear');
					}
				});
	}
}

/**
 * 状态颜色
 */
function columeStyler_orderstatus_injection(val,row,index) {
	var color = myCache.status_color['0005_'+row.status];
	if (color && color == "#ffffff") {
		return "<span style='color: "+color+"'>" + val + "</span>"
	} else if (color) {
		return 'background-color:'+color+';';//color:red;
	} else {
		return '';
	}
}

//保存注射信息
function saveInjection(){
	$('#injectionForm').form('submit', {
		url : window.localStorage.ctx + "/injection/saveInjection",
		onSubmit : function(){
			if(saveInjectionFlag){
				return false;
			}
			
			if(!$('#orderid').val()){
				_message('请选择一个检查！' , '提醒');
				return false;
			}
			if(!$(this).form('validate')){
				_message('请填入完整信息！' , '提醒');
				return false;
			}
//			if($('#injection_datetime').datetimebox('getValue') == ""){
//				$('#injection_datetime').datetimebox('setValue', formatterDateTime(new Date()));
//			}
			
			saveInjectionFlag = true;
			return true;
		},
		success : function(data){
			saveInjectionFlag = false;
			var result = validationDataAll(data);
			if(result.code == 0) {
				$('#printSave').linkbutton('disable');
				$('#printEmpty').linkbutton('disable');
				$('#printInspection').linkbutton('disable');
				$('#printExamine').linkbutton('disable');
				clearInjectionForm();
				searchStudyorder();
				_message('保存成功', '成功');
			}else {
				_message('保存失败请重试，如果问题依然存在，请联系系统管理员！', '失败');
			}

		}
	});
}

//清空注射信息
function clearInjectionForm(){
	$('#injectionForm').form('clear');
}

//注射叫号
function injectioncalling() {
	var studyorderpkid = $("#orderid").val();
	var status = $('#orderstatus').val();
	if(status == myCache.StudyOrderStatus.CONSULTED){
		getJSON(window.localStorage.ctx+"/injection/injectioncalling",
				{
					orderid : studyorderpkid
				},
				function(data){
					var json = validationData(data);
					if(json.code==0){
						_message('叫号成功！','提醒');
					}else {
						_message('叫号失败请重试，如果问题依然存在，请联系系统管理员！','提醒');
					}
				}
			);
	}else {
		_message('当前操作无法完成！','提醒');
	}
}
//检查流程
function toProcess(){
	var orderid= $("#orderid").val();
	if(orderid!=""){
		process(orderid,"injection");
	}
	else{
		_message('请选择一条数据','提醒');
	}
}

/**
 * 打开病人备注
 */
function openRemarkDialog(){
	var patientpkid=$("#patientid").val();
	var admissionpkid=$("#admissionid").val();
	var orderid=$("#orderid").val();
	var studyid=$("#studyid").val();
	if(studyid){
		$('#common_dialog').dialog({
			title:'备注信息',
			width : 460,height : 450,
			left : 200,top : 130,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,
			border: 'thin',
			href : window.localStorage.ctx+'/examine/goToEditremark?studyid='+studyid
				+'&patientpkid='+patientpkid+'&admissionpkid='+admissionpkid+'&orderid='+orderid,
			modal : false,
			buttons:[{
				text:'关闭',
				width:60,
				handler:function(){$('#common_dialog').dialog('close');}
			}]
		});
	}else{
		_message('请选择一条数据','提醒');
	}
}
/**
 * 保存检查备注
 * 
 * @returns
 */
function savereportremark(studyid,patientidfk,admissionidfk,orderid){
	var remark=$('#remark_content').textbox('getValue');
	$.post(window.localStorage.ctx+'/examine/saveRemark',
	    {
	    	'studyid' : studyid,
	    	'patientidfk' : patientidfk,
	    	'admissionidfk' : admissionidfk,
	    	'orderid' : orderid,
	    	'remarkcontent' : remark,
	    	'type' : 'injection'
	     },
	     function (res) {
        	var json = validationDataAll(res);
        	
        	if(json.code==0){
        		$('#common_dialog').dialog('refresh', window.localStorage.ctx+'/examine/goToEditremark?studyid='+studyid
        				+'&patientpkid='+patientidfk+'&admissionpkid='+admissionidfk+'&orderid='+orderid);
        	}
        	else{
        		_message($.i18n.prop('savefailed'),$.i18n.prop('error'));
        	}
        }
	);
}
/**
 * 计算实际注射值
 * @returns
 */
function countInjectionDoseReal(){


	var injection_dose_pre = $('input[name="injection_dose_pre"]').val();
	var injection_dose_after = $('input[name="injection_dose_after"]').val();
	if(injection_dose_pre&&injection_dose_after){
		var injection_dose_real = injection_dose_pre - injection_dose_after;
		if(injection_dose_real<0){
			_message("实际注射量小于0.请重新输入！！","提醒");
			$("#injection_dose_pre").numberspinner('setValue', "");
			$("#injection_dose_after").numberspinner('setValue', "");
			return;			
		}else{
			$("#injection_dose_real").numberspinner('setValue', injection_dose_real);
		}
	}	
}