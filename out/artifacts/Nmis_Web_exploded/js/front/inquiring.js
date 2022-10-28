var myCache;
var imgulp;
var saveInterrogationFlag = false;//保存表单提交标识，saveInterrogationFlag = true时不提交，避免重复提交

$(function(){
	myCache=JSON.parse(window.localStorage.myCache);
	setTimeout(function () {
		var scan={
			type : "scan",
			exec : function(data){
				console.log("scan exec "+data);
				if(imgulp.getnewarr().length==6){
					var flag = 0;
					for(var i=5; i>=0; i--){
						if(!imgulp.isSelected(i)){
							flag = i;
							break;
						}
					}
					imgulp.deleteimg(flag);
				}
				imgulp.addimgarr([{
					src:'image/image_GetApplyImg?path=/apply/tmp/'+data,
					title:data
					}
				]);
				imgulp.selecteAll();
			}
		}
		
		if(websocket){
			initService_WS(scan);
		}
	}, 1000);
});

//初始化问诊页面
function initInterrogation(){
	$('#interrogationForm').find('.easyui-datebox').each(function(index,element){
		$(element).datebox('calendar').calendar({
			validator:function(date){
				var start = new Date(new Date(new Date().toLocaleDateString()).getTime());
				return date <= start;
			}
		});
	});
	
	//问诊日期
	$('#interrogation_time').datebox('setValue', formatterDate(new Date()));
	//问诊医生
	setInterrogationDoctor();
	
	//申请单图片
	imgulp = new Jsequencing({
		listid:"img_ul",//页面图片列表ID
		thumbherf:"",//列表图片前缀
		bigherf:"",//原图前缀[同列表图相同时，省略]
		jsondata:true,
		imgsrcarr:[]//图片数据数组
	});
}

//初始化查询页面
function initWestsearch(){
	$('#westsearch_form').form({
		url : window.localStorage.ctx + "/inquiring/findStudyorder",
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
			}
		}
	});
	
	
	$('#isArrived_west').combobox('select', myCache.StudyOrderStatus.ARRIVED);
	$('#isConsulted_west').combobox('select', 'NO');
	
	setTimeout(function(){ searchStudyorder(); }, 500);
	
}

//查询检查信息
function searchStudyorder(){
	if(!$("#datefrom_west").datebox("getValue")){
		_message('请选择开始时间！！' , '提醒');
		return;
	}
	if(!$("#dateto_west").datebox("getValue")){
		_message('请选择结束时间！！' , '提醒');
		return;
	}
	
	$('#westsearch_form').form('submit');
}

//扫描申请单
function triggerScan(){
	var scanurl = $("#projecturl").val();
	var a = "";
	//登记保存之前扫描
	var a="reporttool:-c scan -s "+scanurl+" -n userid="+$('#scanClientUserid').val()+"&launchip="+$('#scanClientIp').val();

	urlFlag = true;
	if(electron_enable()){
		electron_scan(a);
	} else{
		$("#scan")[0].setAttribute('href', a);
		$("#scan")[0].click();
	}
}

//双击检查信息
function dbClikdg_west(row){
	if($("#exist_sch_inq").val()=='true'){
		if(parseInt(row.status) < parseInt(myCache.StudyOrderStatus.ARRIVED)){
			_message('当前病人未签到，请先签到再问诊！' , '提醒');
			return;
		}
	}
	clearPanelInfo();
	console.log(row);
	$('#sexdisplay').combobox('setValue', row.sex);
	if(row.ctandmr){
		row["ctAndMr"]=row.ctandmr;
	}
	if(row.dischargesummary){
		row["dischargeSummary"]=row.dischargesummary;
	}
	if(row.in_pet_report){
		row["in_PET_report"]=row.in_pet_report;
	}
	if(row.out_pet_report){
		row["out_PET_report"]=row.out_pet_report;
	}
	
	$('#interrogationForm').form('load', row);
	$('#orderid').val(row.studyorderpkid);
	$('#studyid').val(row.studyid);
	$('#orderstatus').val(row.status);
	$('#modality_type').val(row.modality_type);
	
	
	$('#patient_id').val(row.patientpkid);
	$('#admission_id').val(row.admissionpkid);
	$('#interrogation_id').val(row.interrogation_id);
	$('#previous_history_id').val(row.previous_history_id);
	//启用保存等按钮
	$('.inq_op_btn').linkbutton('enable');
	getJSON(window.localStorage.ctx+'/dic/findDicCommonFromCache',
		{
			group:'studymethod',
			modality : row.modality_type
		},function(data){
			var json = validationDataAll(data);
			$("#examination_method").combobox("loadData",json);
		});
	getJSON(window.localStorage.ctx+'/dic/findDicOrgan',
		{
			modality : row.modality_type             
		}, 
		function(data){
			var json = validationDataAll(data);
			$("#organ_inq").combobox("loadData",json);
		});
	
	//住院号/门诊号
	if(row.patientsource = "O"){
		$('#outnoOrinno').textbox('setValue', row.outno);
	}else if(row.patientsource = "I"){
		$('#outnoOrinno').textbox('setValue', row.inno);
	}
	
	//问诊时间
	if(!row.interrogation_time){
		$('#interrogation_time').datebox('setValue', formatterDate(new Date()));
	}
	//问诊医生
	if(!row.interrogation_doctor_id){
		setInterrogationDoctor();
	}
	
	//加载申请单图片
	getJSON(window.localStorage.ctx+'/frontcommon/getImage',
		 {
	 		orderid : row.studyorderpkid
		 },function(data){
			var json = validationDataAll(data);
			if(json.code==0){
				if(json.data){
					for(var i=1;i<11;i++){
						var img=json.data["img"+i];
						if(img){
							imgulp.addimgarr([{
								src : "image/image_GetApplyImg?path="+img,
								title: img.substr(img.lastIndexOf("/")+1)
							}]);
						}
					}
					imgulp.selecteAll();
				}
			}});
	
	
	if($('#reportHistoryDg')[0]){
		initReportHistory();
	}
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

//设置问诊医生
function setInterrogationDoctor(){
	var value = $('#employee_id_hidden').val();
	/*var data = $('#interrogation_doctor_id').combobox('getData');
	for(var i=0; i < data.length; i++){
		if(data[i].id == value){
			$('#interrogation_doctor_id').combobox('select', value);
		}
	}*/
}

//根据病人性别加载简要病史内容
function changePatientSex(newValue, oldValue){
	$('#female_info').find('.easyui-textbox,.easyui-numberbox').each(function(index,element){
		$(element).textbox('setValue', '');
	});
	$('#female_info').find('.easyui-combobox').each(function(index,element){
		$(element).combobox("clear");
	});
	if("F" == newValue) {//女性
		$('#female_info').panel('open');	
	}else {
		$('#female_info').panel('close');
	}
}

//计算出生日期
function calculateAge(newValue,oldValue){
	var age = $('#age').numberbox('getValue');
	var ageunit = $('#ageunit').combobox('getValue');
	var birthdate = $('#birthdate').datebox('getValue');
	
	if(!isNaN(age) && age>0){
		var birth = getBirthday(age, ageunit, birthdate);
		$('#birthdate').datebox('setValue',birth);
	}
}

//计算出生日期
function getBirthday(age, ageunit, str){
	var date = new Date();
	var bdate=null;
	if(ageunit=="Y"){
		bdate=new Date(date.getTime()-age*1000*60*60*24*365);
		if(str!=""){
			var arr=str.split("-");
			return arr[1]+"/"+arr[2]+"/"+bdate.getFullYear();
		}
	}else if(ageunit=="M"){
		bdate=new Date(date.getTime()-age*1000*60*60*24*30);
		if(str!=""){
			var arr=str.split("-");
			return (bdate.getMonth()+1)+"/"+arr[2]+"/"+bdate.getFullYear();
		}
	}else if(ageunit=="D"){//天
		bdate=new Date(date.getTime()-age*1000*60*60*24);	
	}else if(ageunit=="W"){//周
		bdate=new Date(date.getTime()-age*1000*60*60*24*7);	
	}else if(ageunit=="H"){//小时
		if(age <= date.getHours()){
			bdate=new Date(date.getTime());
		}else{
			var count = Math.floor((age-date.getHours()-1)/24)+1;
			bdate=new Date(date.getTime()-count*1000*60*60*24);
		}
	}else{//分
		if(age > date.getHours()*60+date.getMinutes()){
			bdate=new Date(date.getTime()-1*1000*60*60*24);
		}else{
			bdate=new Date(date.getTime());
		}
	}
	return (bdate.getMonth()+1)+"/"+bdate.getDate()+"/"+bdate.getFullYear();
}

//根据出生日期计算年龄
function getAgeFromBirthdate(birthdate){
	var tmp=new Date();
	var today=new Date(tmp.getFullYear(),tmp.getMonth(),tmp.getDate());
	$('#age').numberbox({onChange:function(){}});
	if(birthdate.getTime()==today.getTime()){
		$('#age').numberbox("setValue",0);
		$('#ageunit').combobox('select', 'D');
		$('#age').numberbox("setValue",1);
		$('#age').numberbox({onChange:function(newValue,oldValue){
			calculate_age1(newValue,oldValue);
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
		$('#age').numberbox("setValue",year);
		$('#ageunit').combobox('select', 'Y');
	}else if(year==0){
		var month=today.getMonth()-birthdate.getMonth();
		if(month>0){
			$('#age').numberbox("setValue",month);
			$('#ageunit').combobox('select', 'M');
		}
		else if(month==0){
			var day=today.getDate()-birthdate.getDate();
			if(day>0){
				$('#age').numberbox("setValue",day);
				$('#ageunit').combobox('select', 'D');
			}
			else{
				$('#age').numberbox("setValue",1);
				$('#ageunit').combobox('select', 'D');
			}
		}
	}
	$('#age').numberbox({onChange:function(newValue,oldValue){
		calculateAge(newValue,oldValue);
	}});
}

//既往病史
function changePreviousHistory(checked){
	if(checked){//没有既往病史
		$('#previousHistoryPanel').find('.easyui-textbox,.easyui-numberspinner,.easyui-numberbox').each(function(index,element){
			$(element).textbox('setValue', '');
			$(element).textbox('readonly', true);
		});
		$('#previousHistoryPanel').find('.easyui-datebox').each(function(index,element){
			$(element).datebox('setValue', '');
			$(element).datebox('readonly', true);
		});
		$('#previousHistoryPanel').find('.easyui-combobox').each(function(index,element){
			$(element).combobox("clear");
			$(element).combobox('readonly', true);
		});
	}else{
		$('#previousHistoryPanel').find('.easyui-textbox,.easyui-numberspinner,.easyui-numberbox').each(function(index,element){
			$(element).textbox('setValue', '');
			$(element).textbox('readonly', false);
		});
		$('#previousHistoryPanel').find('.easyui-datebox').each(function(index,element){
			$(element).datebox('setValue', '');
			$(element).datebox('readonly', false);
		});
		$('#previousHistoryPanel').find('.easyui-combobox').each(function(index,element){
			$(element).combobox("clear");
			$(element).combobox('readonly', true);
		});
	}
}

//初始化历史报告
function initReportHistory(){
	if(!$('#patientid').val() || !$('#patientid').val()){
		return;
	}
	getJSON(window.localStorage.ctx + "/inquiring/getReportHistory",
		{
			patientid : $('#patientid').val(),
			orderid : $('#orderid').val()
		},
		function(data){
			var json = validationDataAll(data);
			if(!json.code && json.length > 0){
				$('#reportHistoryDg').datagrid('loadData', json);
				$('#reportHistoryDg').datagrid('selectRow', 0);
				dbClikReportHistoryDg($('#reportHistoryDg').datagrid('getSelected'));
			}else{
				$('#reportHistoryDg').datagrid('loadData', []);
			}
			
		});
}

//保存问诊信息
function saveInterrogation(){
	//如果设备类型为pet，需要判断是否填写血糖信息
	if($("#modality_type").val()=="PETCT"&!$("#fasting_glucose").textbox("getValue")){
		_message('请填入血糖信息！' , '提醒');
		return true;
	}
	
	$('#interrogationForm').form('submit', {
		url : window.localStorage.ctx + "/inquiring/saveInterrogation",
		onSubmit : function(){
			if(saveInterrogationFlag){
				return false;
			}
			
			if(!$(this).form('validate') || !$('#orderid').val()){
				_message('请填入完整信息！' , '提醒');
				return false;
			}
			
			saveInterrogationFlag = true;
			return true;
		},
		success : function(data){
			saveInterrogationFlag = false;
			var result = validationDataAll(data);
			if(result.code == 0){
				//禁用保存等按钮
				$('.inq_op_btn').linkbutton('disable');
				clearPanelInfo();
				searchStudyorder();
				_message('保存成功', '成功');
			}else{
				_message('保存失败请重试，如果问题依然存在，请联系系统管理员！', '失败');
			}
		}
	});
}

//清空问诊面板
function clearPanelInfo(){
	$('#interrogationForm').form('clear');
	
	//问诊日期
	$('#interrogation_time').datebox('setValue', formatterDate(new Date()));
	//问诊医生
	setInterrogationDoctor();
	imgulp.dataempty();//清空申请单图片
	
	if($('#reportHistoryDg')[0]){
		$('#checkdesc_txt').textbox('setValue', '');
		$('#checkresult_txt').textbox('setValue', '');
		$('#briefhistory_history').textbox('setValue', '');
		$('#other_information_history').textbox('setValue', '');
	}
}

//双击历史检查获取报告信息
function dbClikReportHistoryDg(row){
	if(row){
		$('#checkdesc_txt').textbox('setValue', row.checkresult_txt);
		$('#checkresult_txt').textbox('setValue', row.checkresult_txt);
		$('#briefhistory_history').textbox('setValue', row.briefhistory);
		$('#other_information_history').textbox('setValue', row.other_information);
	}
}

//问诊叫号
function interrogationcalling() {
	var studyorderpkid = $("#orderid").val();
	var status = $('#orderstatus').val();
	if(status == myCache.StudyOrderStatus.ARRIVED){
		getJSON(window.localStorage.ctx+"/inquiring/interrogationcalling",
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
function onclickClear(){
	$("#westsearch_form").form("clear");
	$("#datetype").val("arrivedtime");
}

function getappdoctor(newValue,oldValue){
	if(newValue==""||newValue==null){
		$("#appdoctorcode").combobox("clear");
		$("#appdoctorcode").combobox("loadData",[]);
	}else{
		$("#appdoctorcode").combobox({
			valueField: 'id',
			textField: 'name',
			url:window.localStorage.ctx+'/dic/getEmployee?dept='+newValue
			+"&profession=D",
		});
}
}
function printChecklist(){
	var projecturl = $("#projecturl").val();
	var printername=$("#printername1_res").val();
	var orderid=$("#orderid").val();
	var copies=1;
	var a="reporttool:-c print -t "+printername+" -b  "+copies+" -l "+projecturl+" -m orderid="+orderid+"&printTempType=2";//printTempType的取值从后端枚举类PrintTemplateType.REG_TEMPLATE
	urlFlag = true;
	if(electron_enable()){
		electron_print(a);
	} else{
		$("#print_res")[0].setAttribute('href',a);
		$("#print_res")[0].click();
	}
}

function printMedicalHistory(){
	var projecturl = $("#projecturl").val();
	var printername=$("#printername1_res").val();
	var patient_id=$("#patient_id").val();
	var copies=1;
	var a="reporttool:-c print -t "+printername+" -b  "+copies+" -l "+projecturl+" -m patientid="+patient_id+"&printTempType=6";//printTempType的取值从后端枚举类PrintTemplateType.MEDICALHISTORY_TEMPLATE
	urlFlag = true;
	if(electron_enable()){
		electron_print(a);
	} else{
		$("#print_res")[0].setAttribute('href',a);
		$("#print_res")[0].click();
	}
}

function printAuthorization(){
	var projecturl = $("#projecturl").val();
	var printername=$("#printername1_res").val();
	var patient_id=$("#patient_id").val();
	var copies=1;
	var a="reporttool:-c print -t "+printername+" -b  "+copies+" -l "+projecturl+" -m patientid="+patient_id+"&printTempType=5";//printTempType的取值从后端枚举类PrintTemplateType.INFORMED_CONSENT_TEMPLATE
	urlFlag = true;
	if(electron_enable()){
		electron_print(a);
	} else{
		$("#print_res")[0].setAttribute('href',a);
		$("#print_res")[0].click();
	}
}
function printBagStickers(){
	var projecturl = $("#projecturl").val();
	var printername=$("#printername1_res").val();
	var orderid=$("#orderid").val();
	var copies=1;
	var a="reporttool:-c print -t "+printername+" -w barwidth -h barheight -b "+copies+" -l "+projecturl+" -m orderid="+orderid+"&printTempType=4";//printTempType的取值从后端枚举类PrintTemplateType.BAGSTICKER_TEMPLATE
	urlFlag = true;
	if(electron_enable()){
		electron_print(a);
	} else{
		$("#print_res")[0].setAttribute('href',a);
		$("#print_res")[0].click();
	}
}


/**
 * 状态颜色
 */
function columeStyler_orderstatus_inquiring(val,row,index) { 
	var color = myCache.status_color['0005_'+row.status];
	if (color && color == "#ffffff") {
		return "<span style='color: "+color+"'>" + val + "</span>"
	} else if (color) {
		return 'background-color:'+color+';';//color:red;
	} else {
		return '';
	}
}
//检查流程
function toProcess(){
	var orderid= $("#orderid").val();
	if(orderid!=""){
		process(orderid,"inquiring");
	}
	else{
		_message('请选择一条数据','提醒');
	}
}

/**
 * 打开病人备注
 */
function openRemarkDialog(){
	var patientpkid=$("#patient_id").val();
	var admissionpkid=$("#admission_id").val();
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
	    	'type' : 'inquiring'
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