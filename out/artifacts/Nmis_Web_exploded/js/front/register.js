/**
 * 
 */
var myCache;
var imgulp;

var nowPageNumber = 1;//查询结果当前页码
var nowPageSize = 50;//查询结果当前结果数量
var nowflag = false;//标识查询结果是否使用当前页码，nowflag = true时使用当前页码
var searchForm_flag="searchForm";//标识查询方式，searchForm_flag == "quicksearch"为快速全文查询

var saveRegisterFlag = false;//登记表单提交标识，saveRegisterFlag = true时不提交，避免重复提交
var extractFlag = false//从申请单引入病人标识，extractFlag = true时不进行同名病人检查，避免多次重复搜索
var birthdate_Flag = false;

function validator_reg (value) { 
    //格式yyyy-MM-dd或yyyy-M-d
       return/^(?:(?!0000)[0-9]{4}([-]?)(?:(?:0?[1-9]|1[0-2])\1(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])\1(?:29|30)|(?:0?[13578]|1[02])\1(?:31))|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([-]?)0?2\2(?:29))$/i.test(value); 
}

$(function() {	
	myCache=JSON.parse(window.localStorage.myCache);
	setTimeout(function () {
		var scan={
			type : "scan",
			exec : function(data){
				console.log("scan exec "+data);
				//console.log(imgulp.isSelected(1))
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
//	else{
//		$.getScript(window.localStorage.ctx +"/js/websocket.js",function(){
//			initService_WS(scan.type,scan);
//		});
//	}
});


//扫描申请单
function triggerScan(orderid, studyid){
	var scanurl = $("#projecturl_reg").val();
	var a = "";
	if(orderid && studyid){//登记保存之后扫描，有studyid
		console.log("triggerScan:"+" orderid:"+orderid+" studyid:"+studyid);
		var a="reporttool:-c scan -s "+scanurl+" -n orderid="+orderid+"&studyid="+studyid;
		
	}else{//登记保存之前扫描
		var a="reporttool:-c scan -s "+scanurl+" -n userid="+$('#scanClientUserid_reg').val()+"&launchip="+$('#scanClientIp_reg').val();
	}
	urlFlag = true;
	if(electron_enable()){
		electron_scan(a);
	} else{
		$("#scan_reg")[0].setAttribute('href',a);
		$("#scan_reg")[0].click();
	}
	
	//getJSON(window.localStorage.ctx+"/register/triggerScan",null,function(){});
}
//$(document).ready(
//	function(){
//		document.onkeydown=function(){
//			var oEvent=window.event;
//			if(oEvent.ctrlKey && oEvent.keyCode ==49){
//				saveApply();
//			}
//			else if(oEvent.ctrlKey && oEvent.keyCode ==52){
//				cancelSave();
//			}
//		}
//	}
//);

function initRegister(){
	//var now=new Date();
	$('#birthdate_reg').datebox('calendar').calendar({
		validator:function(date){
			var now=new Date();
			var d1=new Date(now.getFullYear(),now.getMonth(),now.getDate());
			return date<=d1;
		}
	});
	
	$('#studydg_item_reg').datagrid('enableCellEditing');
	
	$('#searchItemtree_reg').textbox('textbox').keydown(function(e){
		if(e.keyCode == 13){
        	var rows1=$("#itemtree_reg").datalist("getRows");
        	getJSON(window.localStorage.ctx+"/dic/getExamItemDicFromCache",
				{
					searchtext : $('#searchItemtree_reg').textbox('getValue'),
					modality : $("#modality_reg").combobox("getValue"),
//					organ : $("#organ_reg").textbox("getValue"),
//					suborgan : $("#suborgan_reg").combobox("getValue"),
					equipment : $("#modality_dic_reg").combobox("getValue"),
				},
				function(data) {
					var json=validationData(data);
					$("#itemtree_reg").datalist({
						onLoadSuccess:function(){
							var rows2=$("#itemtree_reg").datalist("getRows");
							if(rows1.length==1&&rows2.length==1&&rows1[0].item_name==rows2[0].item_name){
									treeclick_reg(0,rows2[0]);
							}
						}
					});
					$("#itemtree_reg").datalist('loadData',json);
				}
			);
		}
	});
	
	
	imgulp = new Jsequencing({
		listid:"img_ul",//页面图片列表ID
		thumbherf:"",//列表图片前缀
		bigherf:"",//原图前缀[同列表图相同时，省略]
		jsondata:true,
		imgsrcarr:[]//图片数据数组
	});
	
//	console.log($("#launch_scaner").attr("autoLaunch"))
//	if($("#launch_scaner").attr("autoLaunch")=="true"){
//		console.log($("#launch_scaner").attr("autoLaunch"))
//		$("#launch_scaner")[0].click();
//	}
	
	getJSON(window.localStorage.ctx +"/register/findEmergDefInfo",null,function(json) {
		if(json.length>0){
			$('#emergency_menubtn').menubutton({
			    menu: '#emergency_menu'
			});
			  
			$.each(json,function(index,temp){
				$('#emergency_menu').menu('appendItem',{
					text : temp.configname,
					onclick : function(){
						emergencyRegister(temp.configname);
					}
				});
			});
		}
	});
}

function initimport(){
	$('#searchForm_im').form({
		url: window.localStorage.ctx+"/register/findApplication",
	    success:function(data){
		    $("#dg1_im").datagrid("loadData",validationData(data));
	    }
		
	});
	$("#searchForm_im").submit();
	
}
var default_page=1;
var default_pageSize=20;
function initSearch(){
	var p = $('#dg1_reg').datagrid('getPager'); 
	  $(p).pagination({ 
	        pageSize: default_pageSize,//每页显示的记录条数，默认为10 
	        pageNumber:default_page,
	        pageList: [10,20,30,50],//可以设置每页记录条数的列表 
	        //showRefresh:false,
	        beforePageText: $.i18n.prop('wl.beforepagetext'),//页数文本框前显示的汉字 
	        afterPageText: $.i18n.prop('wl.afterpagetext'),
	        displayMsg: $.i18n.prop('wl.displaymsg'),
	        onSelectPage:function(pageNumber, pageSize){
//	        	page=pageNumber;
//	        	pageSize=pageSize;
	        	$("#page").val(pageNumber);
	        	$("#pageSize").val(pageSize);
	        	$(this).pagination('loading');
	    		$('#progress_dlg').dialog('open');
	    		// 分页搜索时，判断是左边点击的搜索，还是搜索框中的搜索
	    		if (searchForm_flag == "searchForm") {
	    			searchstudy(pageNumber, pageSize, null,null);
	    		} else if (searchForm_flag == "quicksearch") {
	    			searchstudy(pageNumber,pageSize,$('#quicksearch-input').searchbox('getValue'),$('#quicksearch-input').searchbox('getName'));
	    		}
	    		$(this).pagination('loaded');
	    	}
	  });
	  
	$("#searchForm_register").form({
		url: window.localStorage.ctx+"/register/findStudyorder?page="+ default_page +"&rows="+ default_pageSize,
		onSubmit : function() {
			if(checkSearch()){
				$('#progress_dlg').dialog('open');
				return true;
			} else {
				return false;
			}
		},
		success:function(data){
			$('#progress_dlg').dialog('close');
		    if(!data.code){
	    		$("#dg1_reg").datagrid("loadData", validationData(data));
	    		if(nowflag){
	 				var p = $('#dg1_reg').datagrid('getPager'); 
	 				 $(p).pagination({ 
	 					 pageNumber:nowPageNumber,
	 				 });
	 				 nowflag = false;
	 			} 
	    	}
	    }
		
	});
	
	$('#searchForm_register').find('.easyui-textbox').each(function(index,element){
		$(element).textbox('textbox').bind('keydown', function(e){
			if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
				searchstudy();
			}
		});
	})
	
	searchstudy();
}

/**
 *  查询时，先进行校验
 * @returns
 */
function checkSearch() {
	var checkResult = true;
	if (searchForm_flag == "quicksearch") {
		return checkResult;
	}
	// T：今天；Y：昨天；TD：近三天；FD：近五天， W：近一周， TM：近三个月
	var appdate = $('#appdate').val();
	var datefrom = $('#appdatefrom').datebox('getValue'); // 开始时间
	var dateto = $('#appdateto').datebox('getValue');  // 结束时间
	var appstudyid = $("#appstudyid").textbox("getValue");  // 检查号
	var apppatientid = $('#apppatientid').textbox('getValue'); // 病人编号
	var apppatientname = $("#apppatientname").textbox("getValue"); // 姓名
	if (appstudyid == "" && apppatientid == "" && apppatientname == "" && datefrom == "" && dateto == "" && appdate == "") {
		checkResult = false;
		_message('请选择需要查询的具体一个时间段' , '提醒');
	}
	if (datefrom != "" && dateto == "") {
		checkResult = false;
		_message('结束时间不能为空' , '提醒');
	}
	if (datefrom == "" && dateto != "") {
		checkResult = false;
		_message('开始时间不能为空' , '提醒');
	}
	if (datefrom != "" && dateto != "") {
		var date1 = new Date(datefrom);
	    var date2 = new Date(dateto);
	    if (date1.getTime() > date2.getTime()) {
	    	checkResult = false;
	    	_message('开始时间不能大于结束时间' , '提醒');
	    }
	}
	return checkResult;
}

//刷新查询结果，nowflag = true指定当前页码
function refreshSearchstudy(){
	var options = $("#dg1_reg" ).datagrid("getPager" ).data("pagination" ).options;
	nowPageNumber = options.pageNumber;
	nowPageSize = options.pageSize;
	nowflag = true;
	if(searchForm_flag == "quicksearch"){
		quickSearch($('#quicksearch-input').searchbox('getValue'), $('#quicksearch-input').searchbox('getName'));
	}else{
		searchstudy(nowPageNumber, nowPageSize, "", "");//即时更新查询到的数据
	}
}
//搜索检查项目
function searchstudy(pageNumber, pageSize, quicksearchcontent,quicksearchname){
	if (!pageNumber) {
		pageNumber=default_page;
		$('#dg1_reg').datagrid('getPager').pagination({ 
			pageSize: default_pageSize,//每页显示的记录条数，默认为20 
			pageNumber:default_page
		});
	}
	if (!pageSize) {
		pageSize = default_pageSize;
	}
	$("#searchForm_register").form('submit',
			{url:window.localStorage.ctx+"/register/findStudyorder?page="+ pageNumber +"&rows="+ pageSize
			+ "&quicksearchcontent=" + (quicksearchcontent||"")+"&quicksearchname="+(quicksearchname||"")});
}

//转拼音
function toPinyin(){
	$("#pinyin_md").textbox('setValue',$(this).toPinyin());
}

//同名验证
function checkSameName() {
	var patientname = $("#patientname_reg").val();
	$('#pinyin_reg').combobox('clear');
	$('#pinyin_reg').combobox('loadData', []);
	var pinyinArray = pinyinUtil.getPinyin(patientname, '', false, true, true);
	console.log(pinyinArray);
	if(pinyinArray){
		pinyinArray = uniqueArray(pinyinArray);
		let pinyinData = [];
		for(let i=0; i<pinyinArray.length; i++){
			let temp = {py : ""};
			temp.py = pinyinArray[i];
			pinyinData.push(temp);
		}
		$("#pinyin_reg").combobox("loadData", pinyinData);
		
		if(pinyinArray.length==1){
			$("#pinyin_reg").combobox('setValue',pinyinArray[0]);
		}else if(pinyinArray.length>1){
			$("#pinyin_reg").combobox('setValue',pinyinArray[0]);
			$('#pinyin_reg').combobox('showPanel');
		}
	}
	//!extractFlag不是从申请单导入的病人 &&  病人编号为空时才进行同名验证
	if(!extractFlag && $("#patientid_reg").textbox("getValue")=="" && patientname.trim() != ""){
		if(patientname=="*"){
			patientname="";
		}
		getJSON(window.localStorage.ctx+"/register/checkSameName",
			{
				patientname : patientname.trim()
			},
			function(data) {
				var json=validationData(data);
				if(json.length>0){
					openSameNameDlg(json);
				}
			}
		);
	}
}

//打开同名病人窗口
function openSameNameDlg(json){
	$('#common_dialog').dialog({
		title:'同名病人',
		width:1000,height:400,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href: window.localStorage.ctx+"/register/openSameNameDlg",
		buttons:[{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}],
		onLoad:function(){
			$("#sameNamedg_reg").datagrid("loadData",json);
		}
	});
}

//引入相同病人信息
function samePatient(row){
	
	$.messager.confirm('提醒','是否引入相同的病人信息！',function(r){
	    if (r){
	    	//patient 主键
	    	$("#patientkid_reg").val(row.patientpkid);
	    	
	    	if($("#registerForm")[0]){//登记
	    		$("#registerForm").form('load', row);
	    	}
	    	
	    	if($("#scheduleForm")[0]){//预约
	    		$("#scheduleForm").form('load', row);
	    	}
	    	
//	        $("#pinyin_reg").textbox("setValue",row.py);
	    	
	    	//生日
	        $("#birthdate_reg").datebox("setValue",row.birthdate);
	        var str=$("#birthdate_reg").datebox("getValue");
	    	var arr=str.split("-");
	    	var birthdate=new Date(arr[0],arr[1]-1,arr[2]);
	    	getAgeFromBirthdate(birthdate);
	    	
	    	
	    	//备注信息
	    	getJSON(window.localStorage.ctx+"/register/getRemark",
	    			{
	    				typeId : row.patientpkid,
	    				type : "patient"
	    			},
	    			function(data){
	    				$("#patientremark_reg").val(validationDataAll(data).data);
	    			}
	    			);

	    	$('#common_dialog').dialog('close');
	    	
	    	//获取入院信息
	    	getAdmission();
	    }
	});
}

//获取同名病人的Admission信息
function getAdmission(){
	if($("#patientid_reg").textbox("getValue")!=""){
		getJSON(window.localStorage.ctx+"/register/getAdmission",
		{
			patientkid : $("#patientkid_reg").val()
		}, function(data){
			var json=validationData(data);
			if(json!=null){
				$('#admissiondg_reg').combogrid('grid').datagrid('loadData',validationData(json));
			}
	    });
	}
}

//引入相同Admission信息
function sameAdmission(index,row){
	var str = $("#birthdate_reg").datebox("getValue");
	$("#admissionkid_reg").val(row.admissionpkid);

	if($("#registerForm")[0]){//登记
		$("#registerForm").form('load', row);
	}
	
	if($("#scheduleForm")[0]){//预约
		$("#scheduleForm").form('load', row);
	}
	
	//生日
    $("#birthdate_reg").datebox("setValue", str);
    var str=$("#birthdate_reg").datebox("getValue");
	var arr = str.split("-");
	var birthdate=new Date(arr[0], arr[1]-1, arr[2]);
	getAgeFromBirthdate(birthdate);
	
	//备注
	getJSON(window.localStorage.ctx+"/register/getRemark",
			{
				typeId : row.admissionpkid,
				type : "admission"
			},
			function(data){
				$("#admissionremark_reg").val(validationDataAll(data).data);
			}
		);
}

//验证住院号
function checkSameInNo(newValue,oldValue){
	if($("#patientkid_reg").val()=="" && newValue!=null && newValue.trim() != ""){
		getJSON(window.localStorage.ctx+"/register/checkSameNO",
				{
					inno : newValue
				},
				function(data){
					var json=validationData(data);
					if(json!=null){
						$.messager.confirm({
							title:$.i18n.prop('confirm'),
							ok:'是',
							cancel:'否',
							border:'thin',
							msg:'是否引入病人信息？',
							fn:function(r){
								if(r){
									extractInfo(json);
								}
							}
						});
					}
				}
			);
	}
}
//验证门诊号
function checkSameOutNo(newValue,oldValue){
	if($("#patientkid_reg").val()=="" && newValue!=null && newValue.trim() != ""){
		getJSON(window.localStorage.ctx+"/register/checkSameNO",
				{
					outno : newValue
				},
				function(data){
					var json=validationData(data);
					if(json!=null){
						$.messager.confirm({
							title:$.i18n.prop('confirm'),
							ok:'是',
							cancel:'否',
							border:'thin',
							msg:'是否引入病人信息？',
							fn:function(r){
								if(r){
									extractInfo(json);
								}
							}
						});
					}
				}
			);
	}
}

//清空admission面板
function clearAdmission(){
	$("#admissionkid_reg").val("");
	$("#admissionremark_reg").val("");
	$("#admissiondg_reg").combogrid("clear");
	
	$('#admission_panel').find('.easyui-textbox,.easyui-numberbox,.easyui-datebox').each(function(index,element){
		$(element).textbox('setValue', '');
	});
	$('#admission_panel').find('.easyui-combobox').each(function(index,element){
		$(element).combobox("clear");
	});
	$('#admission_panel').find('.myhidden').each(function(index,element){
		$(element).val("");
	});

    $("#patientsource_reg").combobox('select', 'O');
    
    $("#cardno_reg").textbox("setValue","");
    $("#inno_reg").textbox("setValue","");
    $("#outno_reg").textbox("setValue","");
    $("#wardno_reg").textbox("setValue","");
    $("#bedno_reg").textbox("setValue","");
}

//引入相同Admission信息和相同病人信息
function extractInfo(row){
	//patient 主键
	$("#patientkid_reg").val(row.patientpkid);
	//admission 主键
	$("#admissionkid_reg").val(row.admissionpkid);
	
	if($("#registerForm")[0]){//登记
		$("#registerForm").form('load', row);
	}
	
	if($("#scheduleForm")[0]){//预约
		$("#scheduleForm").form('load', row);
	}

	
//    $("#pinyin_reg").textbox("setValue",row.py);
	
	//生日
    $("#birthdate_reg").datebox("setValue",row.birthdate);
    var str=$("#birthdate_reg").datebox("getValue");
	var arr=str.split("-");
	var birthdate=new Date(arr[0],arr[1]-1,arr[2]);
	getAgeFromBirthdate(birthdate);
	
	getAdmission();
	
//	$('#studyorder_panel').find('.myhidden').each(function(index,element){
//		$(element).val("");
//	});
	
	//备注信息
	getJSON(window.localStorage.ctx+"/register/getRemark",
			{
				typeId : row.patientpkid,
				type : "patient"
			},
			function(data){
				$("#patientremark_reg").val(validationDataAll(data).data);
			}
			);
	
	getJSON(window.localStorage.ctx+"/register/getRemark",
		{
			typeId : row.admissionpkid,
			type : "admission"
		},
		function(data){
			$("#admissionremark_reg").val(validationDataAll(data).data);
		}
	);
}

//填写年龄获取出生日期
function calculate_age1(newValue,oldValue){
	if(birthdate_Flag){
		return;
	}
	if(!isNaN(newValue)&&newValue>0){
		var ageunit=$("#age_unit_reg").textbox("getValue");
		var str=$("#birthdate_reg").datebox("getValue");
		var birth=getBirthday(newValue,ageunit,str);
		$("#birthdate_reg").datebox('setValue',birth);	
	}
}
//选择单位获取出生日期
function calculate_age2(newValue,oldValue){
	if(birthdate_Flag){
		return;
	}
	var value=$('#age_reg').numberbox('getValue');
	if(value==""){
		return;
	}
	var ageunit=$("#age_unit_reg").textbox("getValue");
	var str=$("#birthdate_reg").datebox("getValue");
	var birth=getBirthday(value,ageunit,str);
	$("#birthdate_reg").datebox('setValue',birth);
}

//计算出生日期
function getBirthday(value,ageunit,str){
	var date = new Date();
	var bdate=null;
	if(ageunit=="Y"){
		bdate=new Date(date.getTime()-value*1000*60*60*24*365);
		if(str!=""){
			var arr=str.split("-");
			return arr[1]+"/"+arr[2]+"/"+bdate.getFullYear();
		}
	}else if(ageunit=="M"){
		bdate=new Date(date.getTime()-value*1000*60*60*24*30);
		if(str!=""){
			var arr=str.split("-");
			return (bdate.getMonth()+1)+"/"+arr[2]+"/"+bdate.getFullYear();
		}
	}else if(ageunit=="D"){//天
		bdate=new Date(date.getTime()-value*1000*60*60*24);	
	}else if(ageunit=="W"){//周
		bdate=new Date(date.getTime()-value*1000*60*60*24*7);	
	}else if(ageunit=="H"){//小时
		if(value<=date.getHours()){
			bdate=new Date(date.getTime());
		}else{
			var count = Math.floor((value-date.getHours()-1)/24)+1;
			bdate=new Date(date.getTime()-count*1000*60*60*24);
		}
	}else{//分
		if(value>date.getHours()*60+date.getMinutes()){
			bdate=new Date(date.getTime()-1*1000*60*60*24);
		}else{
			bdate=new Date(date.getTime());
		}
	}
	return "01/01/"+bdate.getFullYear();
}

function getAgeFromBirthdate(birthdate){
	birthdate_Flag = true;
	var tmp=new Date();
	var today=new Date(tmp.getFullYear(),tmp.getMonth(),tmp.getDate());
	if(birthdate.getTime()==today.getTime()){
		$("#age_reg").numberbox("setValue",0);
		$("#age_unit_reg").combobox('select', 'D');
		$("#age_reg").numberbox("setValue",1);
		$("#age_reg").numberbox({onChange:function(newValue,oldValue){
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
		$("#age_reg").numberbox("setValue",year);
		$("#age_unit_reg").combobox('select', 'Y');
	}else if(year==0){
		var month=today.getMonth()-birthdate.getMonth();
		if(month>0){
			$("#age_reg").numberbox("setValue",month);
			$("#age_unit_reg").combobox('select', 'M');
		}
		else if(month==0){
			var day=today.getDate()-birthdate.getDate();
			if(day>0){
				$("#age_reg").numberbox("setValue",day);
				$("#age_unit_reg").combobox('select', 'D');
			}
			else{
				$("#age_reg").numberbox("setValue",1);
				$("#age_unit_reg").combobox('select', 'D');
			}
		}
	}
	birthdate_Flag = false;
}

//设置申请科室和病区
function setDept(newValue,oldValue){
//	if(newValue==""||newValue==null){
//		$("#dept_reg").combobox("clear");
//		$("#dept_reg").combobox("loadData",[]);
//		$('#wardno_reg').combobox('clear');
//		$("#wardno_reg").combobox("loadData",[]);
//	}else{
//		$("#dept_reg").combobox({
//			url:window.localStorage.ctx+'/dic/getDeptFromCache?type='+newValue+'&user_institution='+$('#user_institution').val(),
//		});
//		$("#doctor_reg").combobox("clear");
//		$("#doctor_reg").combobox("loadData",[]);
//		if(newValue=="I"){
//			$("#wardno_reg").combobox({
//				url:window.localStorage.ctx+'/dic/getDeptFromCache?type='+newValue+'&user_institution='+$('#user_institution').val(),
//			});
//		}else{
//			$('#wardno_reg').combobox('clear');
//			$("#wardno_reg").combobox("loadData",[]);
//		}
//		$('#bedno_reg').combobox('clear');
//		$("#bedno_reg").combobox("loadData",[]);
//	}
	
	
	/*getJSON(window.localStorage.ctx+'/dic/getDeptFromCache',
			{
				type : newValue
			},
			function(data){
				var json=validationData(data);
				if(!json.code){
					var clear=true;
					for(var i=0;i<json.length;i++){
						console.log(json);
						if(json[i].id==$('#dept_reg').combobox('getValue')){
							clear=false;
						}
					}
					//判断是否清空选择选中项
					if(clear){
						$('#dept_reg').combobox('clear');
					}
					$("#dept_reg").combobox("loadData",json);
					if(newValue=="I"){//病人来源不是住院时病区为空
						if(clear){
							$('#wardno_reg').combobox('clear');
						}
						$("#wardno_reg").combobox("loadData",json);
					}else{
						$('#wardno_reg').combobox('clear');
						$("#wardno_reg").combobox("loadData",[]);
					}
				}
			}
	);*/
}

//设置申请医生
function setEmployee(newValue,oldValue){
//	if(newValue==""||newValue==null){
//		$("#doctor_reg").combobox("clear");
//		$("#doctor_reg").combobox("loadData",[]);
//	}else{
//		$("#doctor_reg").combobox({
//			valueField: 'id',
//			textField: 'name',
//			url:window.localStorage.ctx+'/dic/getEmployeeFromCache?deptfk='+newValue
//			+"&profession=T",
//		});
//	}
}

//设置申请科室
function setDeptReg(newValue,oldValue){
	if(newValue==""||newValue==null){
 		$("#appdeptcode").combobox("clear");
 		$("#appdeptcode").combobox("loadData",[]);
 	}else{
 		$("#appdeptcode").combobox({
 			valueField: 'deptcode',
 			textField: 'deptname',
 			panelHeight:'120px',
 			url:window.localStorage.ctx+'/dic/getDeptFromCache?type=T&user_institution='+newValue,
 		});
 	}
}

//选择设备类型后加载部位和设备
function setOrgan(newValue,oldValue){
	if(newValue){
		/*$("#organ_reg").combobox({
			url:window.localStorage.ctx+'/dic/getOrganDic?modality='+newValue,
		});*/
		
		if($("#suborgan_reg")[0]){
			$("#suborgan_reg").combobox("clear");
			$("#suborgan_reg").combobox("loadData",[]);
		}
		
		$("#itemtree_reg").datalist('loadData',[]);

		getJSON(window.localStorage.ctx+'/dic/getModalityDic',
				{
					modality : newValue,
					user_institution : $('#user_institution').val(),
					working_state:0                 //只查询可用的设备   20200904 hx
				}, 
				function(data){
		
					$('#modality_dic_reg').combobox('loadData',data);
					if(data.length>0){
						$("#modality_dic_reg").combobox('select',data[0].id);
					}else{
						$('#modality_dic_reg').combobox('clear');
					}
				});
		
		getJSON(window.localStorage.ctx+'/dic/findDicCommonFromCache',
			{
				group : "studymethod",
				modality : newValue             
			}, 
			function(data){
				$('#examination_method').combobox('loadData',data);
				if(data.length>0){
					$("#examination_method").combobox('select',data[0].id);
				}else{
					$('#examination_method').combobox('clear');
				}
			}
		);
		getJSON(window.localStorage.ctx+'/dic/findDicOrgan',
				{
					modality : newValue             
				}, 
				function(data){
		
					$('#organ_reg').combobox('loadData',data);
					if(data.length>0){
						$("#organ_reg").combobox('select',data[0].id);
					}else{
						$('#organ_reg').combobox('clear');
					}
				});
	}else{
		$("#organ_reg").combobox("clear");
		$("#organ_reg").combobox("loadData",[]);
		
		$('#modality_dic_reg').combobox('clear');
		$('#modality_dic_reg').combobox('loadData',[]);
		
		//$("#itemtree_reg").datalist('clear');
		$("#itemtree_reg").datalist('loadData',[]);
	}
}


function modalitydic_onChange(newValue,oldValue) {
	if(newValue){
		doSearchItem();
		if ($("#appointmenttime")[0]) {
			setSelectableAppointmentDate(newValue);
		}
	}
}
	
//选择部位后加载子部位
function setSuborgan(newValue,oldValue){
	if(newValue && $("#suborgan_reg")[0]){
		$("#suborgan_reg").combobox({url:window.localStorage.ctx+'/dic/getOrganDic?parent='+newValue});
	}else if($("#suborgan_reg")[0]){
		$("#suborgan_reg").combobox("clear");
		$("#suborgan_reg").combobox("loadData",[]);
	}
	
	doSearchItem();
}

/**
 * 加载检查项目
 * @param element
 * @returns
 */
function loadItemtree(element,param){
	if(!$("#modality"+element).combobox("getValue") || !$("#modality_dic"+element).combobox("getValue")){
		return;
	}
	getJSON(window.localStorage.ctx+"/dic/getExamItemDicFromCache",
		 {
			searchtext : param,
			modality : $("#modality"+element).combobox("getValue"),
//			organ : $("#organ"+element).textbox("getValue"),
			equipment : $("#modality_dic"+element).combobox("getValue")
		},
		function(data) {
			var json=validationData(data);
			$("#itemtree"+element).datalist({
				onLoadSuccess:function(){}
			});
			$("#itemtree"+element).datalist('loadData',json);
		}
	);
}

//根据条件搜索检查项目
function doSearchItem(param){
	loadItemtree("_reg",param);
}

//选择检查项目
function treeclick_reg(rowIndex, rowData){
	if(rowData.item_name!=null){
		var rows=$('#studydg_item_reg').datagrid('getRows');
		if(rows!=null&&rows.length>0){
			for(var i=0;i<rows.length;i++){
				if(rows[i].item_name==rowData.item_name){
					return;
				}
			}
		}
		$('#studydg_item_reg').datagrid('appendRow',{
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
		
		//reloadDataFooter($('#studydg_item_reg'));
		$("#modality_reg").combobox('disable');
		$("#modality_dic_reg").combobox('disable');
	}
}

//删除已选检查项目
function deleteItem(param){
	var row=$('#'+param).datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title:$.i18n.prop('confirm'),
			ok:'是',
			cancel:'否',
			border:'thin',
			msg:'确认删除选中数据？',
			fn:function(r){
				if(r){
					var index=$('#'+param).datagrid('getRowIndex',row);
					$('#'+param).datagrid('deleteRow',index);
					reloadDataFooter($('#'+param));
					if(param=="studydg_item_reg" && $('#'+param).datagrid('getRows').length==0){
						setStudy(true);
						$('#modality_reg').combobox('enable');
						$('#modality_dic_reg').combobox('enable');
					}
					
					if(param=="studydg_item_emerg" && $('#'+param).datagrid('getRows').length==0){
						$('#modality_emerg').combobox('enable');
						$('#modality_dic_emerg').combobox('enable');
					}
					
				}
			}
		});
	}else{
		_message('请选择一份数据！' , '提醒');
	}
}

function deleteItemAll(param){
	$.messager.confirm({
		title:$.i18n.prop('confirm'),
		ok:'是',
		cancel:'否',
		border:'thin',
		msg:'确认删除所有数据？',
		fn:function(r){
			if(r){
				$('#'+param).datagrid('loadData',{total:0,rows:[]});
			    $('#'+param).datagrid('reloadFooter',{total:0,rows:[]});
			    if(param=="studydg_item_reg"){
					setStudy(true);
					$('#modality_reg').combobox('enable');
					$('#modality_dic_reg').combobox('enable');
				}
				
				if(param=="studydg_item_emerg"){
					$('#modality_emerg').combobox('enable');
					$('#modality_dic_emerg').combobox('enable');
				}			
			}
		}
	});
}


function afterChangePrice(index,row,changes){
	var dg=$(this);
	reloadDataFooter(dg);
}

function reloadDataFooter(dg){
	var sum_price=0.0;
	var sum_realprice=0.0;
	var	rows=dg.datagrid('getRows');
	if(rows!=null&&rows.length>0){
		for(var i=0;i<rows.length;i++){
			if(rows[i].price){
				sum_price+=parseFloat(rows[i].price);
			}
			if(rows[i].realprice){
				sum_realprice+=parseFloat(rows[i].realprice);
			}
		}
	}
	dg.datagrid('reloadFooter',[
		{item_name: '合计', price: sum_price,realprice:sum_realprice.toFixed(2)}
	]);
}

/**
 * 常用查询条件填充
 */
function fillSearchParams() {

	var row=$('#myfilterlist_register').datalist('getSelected');
	console.log(row)
	$("#searchForm_register").form('load',row);
	if (row.appdate == "T") {
		$('#apptoday').linkbutton('select');
		$("#appdate").val("T");
	}else if (row.appdate == "Y") {
		$('#appyesterday').linkbutton('select');
		$("#appdate").val("Y");
	}else if (row.appdate == "TD") {
		$('#appthreeday').linkbutton('select');
		$("#appdate").val("TD");
	}else if (row.appdate == "FD") {
		$('#appfiveday').linkbutton('select');
		$("#appdate").val("FD");
	}else if (row.appdate == "W") {
		$('#appweek').linkbutton('select');
		$("#appdate").val("W");
	}else if (row.appdate == "M") {
		$('#appmonth').linkbutton('select');
		$("#appdate").val("M");
	}else{
		$('#apptoday').linkbutton({selected:false});
		$('#appyesterday').linkbutton({selected:false});
		$('#appthreeday').linkbutton({selected:false});
		$('#appfiveday').linkbutton({selected:false});
		$('#appweek').linkbutton({selected:false});
		$('#appmonth').linkbutton({selected:false});
		$('#appdate').val('');
	}
	searchstudy();
}
//清空查询条件
function clearSearch(){
	$('#myfilterlist_register').datalist('clearSelections');
	$('#searchForm_register').form('clear');
	$('#datetype').combobox('setValue',$('#datetype').combobox('getData')[0].value);
	
	$('#apptoday').linkbutton({selected:false});
	$('#appyesterday').linkbutton({selected:false});
	$('#appthreeday').linkbutton({selected:false});
	$('#appfiveday').linkbutton({selected:false});
	$('#appweek').linkbutton({selected:false});
	$('#appmonth').linkbutton({selected:false});
//	$('#appdate').val('');
}


//取消检查
function cancelApp(){
	var row=$("#dg1_reg").datagrid("getSelected");
	if(row!=null){
		if((row.status==myCache.StudyOrderStatus.registered||row.status==myCache.StudyOrderStatus.scheduled) && row.reportstatusdisplaycode==myCache.ReportStatus.Noresult){
			//判断当前状态
			getJSON(window.localStorage.ctx+"/register/getStatus?orderid="+row.studyorderpkid,null, function(json){
				var data = validationDataAll(json);
				if(data.code==0){
					if(data.data.status==myCache.StudyOrderStatus.registered||data.data.status==myCache.StudyOrderStatus.scheduled){
						canCancelApp(row);
					}else{
						_message('更新当前数据' , '提醒');
						refreshSearchstudy();
					}
				}else{
					_message('请求数据失败' , '提醒');
				}	
			});	
		}else{
			_message('当前状态无法取消！' , '提醒');
		}
	}else{
		_message('请选择一条数据' , '提醒');
	}
}
function canCancelApp(row){
	
	$.messager.confirm({title:$.i18n.prop('confirm'),ok: '是',cancel: '否',msg: '是否取消选中的检查？',border:'thin',
		fn: function(r){
			if(r){
				getJSON(window.localStorage.ctx+"/register/cancelStudyOrder?orderid="+row.studyorderpkid,null,function(data){
					var json=validationData(data);
					if(json.code==0){
						refreshSearchstudy();
					}else{
						_message('取消失败请重试，如果问题依然存在，请联系系统管理员！' , '提醒');
					}
				});
			}
		}
	});
}

//取消已检查，将检查完成状态变为重拍状态
function cancelChecked(){
	var row = $("#dg1_reg").datagrid("getSelected");
    if(row==null){
    	_message('请选择一条数据！' , '提醒');
    }else{
    	//new Date(row.regdatetime).toDateString() == new Date().toDateString() 判断是否是当天
		if(row.status == myCache.StudyOrderStatus.completed && row.reportstatusdisplaycode == myCache.ReportStatus.Noresult ){
			getJSON(window.localStorage.ctx+"/examine/cancelStudyChecked",
					{
						orderid : row.studyorderpkid	
					},
					function(data){
						var json = validationData(data);
						if(json.code==0){
							_message('取消已检查成功！' , '提醒');
							refreshSearchstudy();
						}else{
							_message('取消失败请重试，如果问题依然存在，请联系系统管理员！' , '提醒');
						}
					});
			
		}else{
			var remind = '';
			if(row.status != myCache.StudyOrderStatus.completed){
				remind = remind + '检查未完成！';
			}
			if(row.reportstatusdisplaycode != myCache.ReportStatus.Noresult){
				remind = remind + '报告不是未写状态！';
			}
			_message('无法取消：<br/>' + remind , '提醒');
		}
    }
}

//删除检查
function deleteApp(){
	var row=$("#dg1_reg").datagrid("getSelected");
	if(row!=null){
		if((row.status==myCache.StudyOrderStatus.registered||row.status==myCache.StudyOrderStatus.scheduled) && row.reportstatusdisplaycode==myCache.ReportStatus.Noresult){
			//判断当前状态
			getJSON(window.localStorage.ctx+"/register/getStatus?orderid="+row.studyorderpkid,null, function(json){
				var data = validationDataAll(json);
				if(data.code==0){
					if(data.data.status==myCache.StudyOrderStatus.registered||data.data.status==myCache.StudyOrderStatus.scheduled){
						canDeleteApp(row);
					}else{
						_message('更新当前数据', '提醒');
						refreshSearchstudy();
					}
				}else{
					_message('请求数据失败', '提醒');
				}	
			});	
		}
		else{
			_message('当前状态无法删除！', '提醒');
		}
	}
	else{
		_message('请选择一条数据', '提醒');
	}
}
function canDeleteApp(row){
	$.messager.confirm({title:$.i18n.prop('confirm'),ok: '是',cancel: '否',msg: '是否删除选中的检查？',border:'thin',
		fn: function(r){
			if(r){
				getJSON(window.localStorage.ctx+"/register/deleteStudyOrder?orderid="+row.studyorderpkid,null, function(data){
					var json=validationData(data);
					if(json.code==0){
						refreshSearchstudy();
			    	}
			    	else{
			    		_message('删除失败请重试，如果问题依然存在，请联系系统管理员！', '提醒');
			    	}
			    });
			}
		}
	});
}

//删除病人
function deletePatient(){
	var row=$("#dg1_reg").datagrid("getSelected");
	if(row!=null){
		if(row.reportstatusdisplaycode==myCache.ReportStatus.Noresult 
				&& (row.status==myCache.StudyOrderStatus.registered||
				row.status==myCache.StudyOrderStatus.scheduled||
				row.status==myCache.StudyOrderStatus.cancel_the_appointment||
				row.status==myCache.StudyOrderStatus.canceled)){
			$.messager.confirm({title:$.i18n.prop('confirm'),ok:'是',cancel:'否',msg:'该操作无法撤销，是否删除选中的病人？',border:'thin',
				fn:function(r){
					if(r){
						getJSON(window.localStorage.ctx+"/register/deletePatient?patientidfk="+row.patientidfk, null, function(data){
							var json=validationData(data);
							if(json=="1"){
								_message('删除成功！' , '成功');
								refreshSearchstudy();
							}
							else if(json=="2"){
								_message('删除失败请重试，如果问题依然存在，请联系系统管理员！' , '错误');
							}
							else if(json=="3"){
								_message('无法删除，病人存在正在检查或检查完成状态的检查记录！' , '提醒');
							}
					    });
					}
				}
			});
		}
		else{
			_message('当前状态无法删除！' , '提醒');
		}	
	}else{
		_message('请选择一条数据' , '提醒');
	}
}

//修改登记信息
function modifyApp(){
	var row=$("#dg1_reg").datagrid("getSelected");
	if(row!=null){
		if(row.status >= myCache.StudyOrderStatus.registered){
			//判断当前状态
			getJSON(window.localStorage.ctx+"/register/getStatus?orderid="+row.studyorderpkid, null ,function(json){
				var data = validationDataAll(json);
				if(data.code==0){
					if(data.data.status==row.status){
						brforeModify(row);
					}else{
						_message('检查状态已经被更新，正在刷新查询结果。请重试修改操作。' , '提醒');
						refreshSearchstudy();
					}
				}else{
					_message('请求数据失败' , '提醒');
				}	
			});	
		}else{
			_message('当前状态，无法修改！' , '提醒');
		}
		
	}else{
		_message('请选择一条数据' , '提醒');
	}
}

//判断登记面板是否有信息
function brforeModify(row){
	if($("#patientid_reg").textbox("getValue")!=""||$("#studyid_reg").textbox("getValue")!=""||$("#admissionid_reg").textbox("getValue")!=""){
		$.messager.confirm({
			title:$.i18n.prop('confirm'),
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '正在登记录入中，是否放弃当前的登记信息？',
			fn: function(r){
				if (r){
					cancelSave();
					setValue_Modify(row);
				}
			}
		});
	}else{
		cancelSave();
		setValue_Modify(row);
	}
}

//转到登记界面
function setValue_Modify(row) {
	console.log(row);
	//patient 主键
	$('#patientkid_reg').val(row.patientpkid);
	//admission 主键
	$('#admissionkid_reg').val(row.admissionpkid);
	//studyorder 主键
	$('#studyorderid_reg').val(row.studyorderpkid);
	
	$("#registerForm").form('load', row);
    
    $("#appdeptcode").combobox("loadData",[{
		deptcode: row.appdeptcode,
		deptname: row.appdeptname
	}]);
	if(row.appdeptcode){
		$("#appdeptcode").combobox("setValue",row.appdeptcode);
	}else{
		$("#appdeptcode").combobox("setText",row.appdeptname);
	}
	$("#appdeptname_reg").val(row.appdeptname);
	$("#doctor_reg").combobox("loadData",[{
		appdoctorcode: row.appdoctorcode,
		appdoctorname: row.appdoctorname
	}]);
	if(row.appdoctorcode){
		$("#doctor_reg").combobox("setValue",row.appdoctorcode);
	}else{
		$("#doctor_reg").combobox("setText",row.appdoctorname);
	}
	$("#appdoctorname_reg").val(row.appdoctorname);
	
	
	//生日
    $("#birthdate_reg").datebox("setValue",row.birthdate);
    var str=$("#birthdate_reg").datebox("getValue");
	var arr=str.split("-");
	var birthdate=new Date(arr[0],arr[1]-1,arr[2]);
	getAgeFromBirthdate(birthdate);
	
	getAdmission();
	
	//备注信息
	getJSON(window.localStorage.ctx+"/register/getRemark",
			{
				typeId : row.patientpkid,
				type : "patient"
			},
			function(data){
				$("#patientremark_reg").val(validationDataAll(data).data);
			}
		);
	
	getJSON(window.localStorage.ctx+"/register/getRemark",
			{
				typeId : row.admissionpkid,
				type : "admission"
			},
			function(data){
				$("#admissionremark_reg").val(validationDataAll(data).data);
			}
		);

    getJSON(window.localStorage.ctx+"/register/getRemark",
			{
				typeId : row.studyorderpkid,
				type : "studyorder"
			},
			function(data){
				$("#so_remark_reg").val(validationDataAll(data).data);
			}
		);
    
    
    getJSON(window.localStorage.ctx+"/register/getStudyItem?orderid="+row.studyorderpkid, null ,function(json){
    		$("#studydg_item_reg").datagrid("loadData",validationData(json));
    		reloadDataFooter($("#studydg_item_reg"));
    });
	
    getJSON(window.localStorage.ctx+'/frontcommon/getImage?orderid='+row.studyorderpkid,null,function(data){
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
		}
    });

    if(row.status!=myCache.StudyOrderStatus.registered){
    	setStudy(false);
    }else{
    	setStudy(true);
    }
   
    $("#tabs_div_reg").tabs("select",0);
}

//设置检查是否可更改
function setStudy(ret){
	if(ret){
		$("#studyid_reg").textbox('enable');
	    $("#priority_com_reg").combobox('enable');
	    $("#modality_reg").combobox('disable');
	    $("#modality_dic_reg").combobox('disable');
	    $("#appdeptcode").combobox('enable');
	    $("#doctor_reg").combobox('enable');
	    $("#organ_reg").combobox('enable');
	    $("#suborgan_reg").combobox('enable');
	    $('#deleteItem').linkbutton('enable');
	    $("#searchItemtree_reg").searchbox('enable');
	    loadItemtree("_reg");
	}else{
		$("#studyid_reg").textbox('disable');
	    $("#priority_com_reg").combobox('disable');
	    $("#modality_reg").combobox('disable');
	    $("#modality_dic_reg").combobox('disable');
	    $("#appdeptcode").combobox('disable');
	    $("#doctor_reg").combobox('disable');
	    $("#organ_reg").combobox('disable');
	    $("#suborgan_reg").combobox('disable');
	    $('#deleteItem').linkbutton('disable');
	    $("#searchItemtree_reg").searchbox('disable');
	    $("#itemtree_reg").datalist('loadData',{total:0,rows:[]});
	}
}

//打印申请单
function printApp(){
	var row=$("#dg1_reg").datagrid("getSelected");
	console.log(row);
	if(row!=null){
		copiesAndPrint(row.studyorderpkid);
		
	/*	$.messager.confirm({title:$.i18n.prop('confirm'),ok:'是',cancel:'否',border:'thin',msg:'是否打印',
			fn:function(r){
				if(r){
					
					var projecturl = $("#projecturl_reg").val();
					var printername = $("#printername1_reg").val();
					
					var a="reporttool:-c print -t "+printername+" -w barwidth -h barheight -l "+projecturl+" -m orderid="+row.studyorderpkid+"&printtempname="+printtempname+"&printType=checklist&issr=0";
					$("#print_reg")[0].setAttribute('href',a);
					$("#print_reg")[0].click();
				}
			}
		});*/

	}else{
		_message('请选择一条数据' , '提醒');
	}
}

function copiesAndPrint(orderid){
	$('#copies_reg').combobox('setValue',$("#copies").val()?$("#copies").val():1);
	$('#printDlg_reg').dialog({
		title:'打印张数选择',
		border:'thin',
		modal: true,
		buttons:[{
			text:'打印',
			width:63,
			handler:function(){
				var projecturl = $("#projecturl_reg").val();
				var printername = $("#printername1_reg").val();
				var copies = $("#copies_reg").val()?$("#copies_reg").val():1;
				var a="reporttool:-c print -t "+printername+" -w barwidth -h barheight -b "+copies+" -l "+projecturl+" -m orderid="+orderid+"&printTempType=2";//printTempType的取值从后端枚举类PrintTemplateType.REG_TEMPLATE
				urlFlag = true;
				if(electron_enable()){
					electron_print(a);
				} else{
					$("#print_reg")[0].setAttribute('href',a);
					$("#print_reg")[0].click();
				}
				$("#copies").val(copies);
				$('#printDlg_reg').dialog('close');
			}
		},{
			text:'取消',
			width:63,
			handler:function(){$('#printDlg_reg').dialog('close');}
		}]
	});
	$('#printDlg_reg').dialog('open');
}

//申请单
function toApply() {
	var row = $("#dg1_reg").datagrid("getSelected");
	if (row != null) {
		apply(row.studyorderpkid,"register");
	} else {
		_message('请选择一条数据' , '提醒');
	}
	
}

function reTriggerScan(){
	var row=$("#dg1_reg").datagrid("getSelected");
	if(row!=null){
		triggerScan(row.studyorderpkid, row.studyid);
	}
	else{
		_message('请选择一条数据' , '提醒');
	}
}

//检查流程
function toProcess(){
	var row=$("#dg1_reg").datagrid("getSelected");
	if(row!=null){
		process(row.studyorderpkid, "register");
	}
	else{
		_message('请选择一条数据' , '提醒');
	}
}


//打开上传申请单窗口
function goToUploadScanimg(){
	var row=$("#dg1_reg").datagrid("getSelected");
	if(row!=null){
		$('<div></div>').dialog({
			id : 'newDialog',
			title:'上传申请单图片',
			width:520,height:200,
			resizable: false,minimizable: false,maximizable: false,modal : true,
			border: 'thin',
			href: window.localStorage.ctx+'/register/goToUploadScanimg?orderid='+row.studyorderpkid+'&studyid='+row.studyid,
			buttons:[{
				text:'保存',
				width:80,
				handler:function(){uploadScanimg();}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#newDialog').dialog('destroy');}
			}],
			onLoad:function(){
				$('#patientname_scanimg').textbox('setValue', row.patientname);
				$('#studyid_scanimg').textbox('setValue', row.studyid);
			},
			onClose:function(){
				$('#newDialog').dialog('destroy');
			}
		});
	}else{
		_message("请选择一条数据", "提醒");
	}
}


//上传申请单
function uploadScanimg(){
	$('#scanimgform').form('submit', {
		url: window.localStorage.ctx+"/register/uploadScanimg",
		onSubmit: function(param){
			if($('#scanimgfile').filebox('getValue')==""){
				_message("请选择申请单！", "提醒");
				return false;
			}
		},
		success: function(data){
			var json=validationDataAll(data);
			if(json.code==0){
				_message("申请单图片已上传成功！", "成功");
				$('#newDialog').dialog('close');
			}
			else{
				_message("保存失败请重试，如果问题依然存在，请联系系统管理员！", "错误提醒");
			}
		}
	});
}

//编辑备注信息
function openNotes(str,ti){
	$('#notes_reg').textbox('setValue',$('#'+str).val());
	$('#notesDlg_reg').dialog({
		title:ti,
		border:'thin',
		modal: true,
		buttons:[{
			text:'保存',
			width:60,
			handler:function(){
				$('#'+str).val($('#notes_reg').textbox('getValue'));
				$('#notesDlg_reg').dialog('close');
			}
		},{
			text:'关闭',
			width:60,
			handler:function(){$('#notesDlg_reg').dialog('close');}
		}]
	});
	$('#notesDlg_reg').dialog('open');
}

//打开合并病人窗口
function openMergePatientDlg(){
	$('#common_dialog').dialog({
		title:'合并病人',
		width:650,height:590,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href: window.localStorage.ctx+"/register/goMergePatient",
		buttons:[{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
		,onLoad:function(){
			var row=$('#dg_search_sch').datagrid('getSelected');
			if(row){
				$('#patientid1_reg').textbox('setValue',row.patientid);
				$('#patientname1_reg').textbox('setValue',row.patientname);
				findSamePatient('1_reg');
			}
		}
	});
}

/**
 *  合并病人中的查找
 * @param element  判断是哪个查找
 * @returns
 */
function findSamePatient(element) {
	var patientid = $('#patientid'+element).val();
	var patientname = $('#patientname'+element).val();
	if (patientid.trim() == "" && patientname.trim() == "") {
		 _alert('请输入需要查找的患者编号或姓名！' , '提醒');
	} else {
		getJSON(window.localStorage.ctx+"/register/findPatient",
				{
					patientid:$('#patientid'+element).val(),
					patientname:$('#patientname'+element).val()
				}	
				,function(data){
					var json=validationData(data);
					$("#patientdg"+element).datagrid("loadData",json);
				}
			);	
	}
	
}

//进行合并操作
function mergePatient(left_right){
	var row1=$('#patientdg1_reg').datagrid('getSelected');
	if(!row1){
		var rows=$('#patientdg1_reg').datagrid('getRows');
		if(rows&&rows.length==1){
			row1=rows[0];
		}
	}
	
	var row2=$('#patientdg2_reg').datagrid('getSelected');
	if(!row2){
		var rows=$('#patientdg2_reg').datagrid('getRows');
		if(rows&&rows.length==1){
			row2=rows[0];
		}
	}
	if(row1 != null && row2 != null){
		if(row1.patientid == row2.patientid){
			 _alert('请选择不同的病人！' , '提醒');
		}else{
			var fromid=row2.id;
			toid=row1.id;
			topatientid=row1.patientid;
			if(left_right){
				fromid=row1.id;
				toid=row2.id;
				topatientid=row2.patientid
			}
			$.messager.confirm({
				title:$.i18n.prop('confirm'),
				msg:'是否合并所选病人？',
				ok:'是',
				border:'thin',
				cancel:'否',
				fn:function(r){
					if(r){
						getJSON(window.localStorage.ctx+"/register/mergePat",
							{
								fromid:fromid,
								toid:toid,
								topatientid:topatientid
							}
							,function(data){
								var json=validationData(data);
								if(json.code==0){
									findSamePatient('1_reg');
									findSamePatient('2_reg');
									searchSchstudy();
						    	}
						    	else{
						    		_message('合并失败请重试，如果问题依然存在，请联系系统管理员！' , '提醒');
						    	}
						});
					}
				}
			});
		}
		
	}else{
		_alert('请选择数据！' , '提醒');
	}
}

//打开取消合并窗口
function openCancelMergeDlg(){
	$('<div></div>').dialog({
		id:'newDialog',
		title:'取消合并',
		width:700,height:360,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href: window.localStorage.ctx+"/register/goCancelMerge",
		buttons:[{
			text:'取消合并',
			width:80,
			handler:function(){cancelMerge();}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#newDialog').dialog('destroy');}
		}],
		onLoad:function(){
			findRecycledPat();
		},
		onClose:function(){
			$('#newDialog').dialog('destroy');
		}
	});
}

//取消合并
function cancelMerge(){
	var row=$("#patientdg3_reg").datagrid("getSelected");
	if(row){
		$.messager.confirm({
			title:$.i18n.prop('confirm'),
			msg:'是否取消合并？',
			ok:'是',
			border:'thin',
			cancel:'否',
			fn:function(r){
				if(r){
					getJSON(window.localStorage.ctx+"/register/cancelMerge",
							{
								id:row.id
							},	
							function(data){
								var json=validationDataAll(data);
								if(json.code==0){
									findRecycledPat();
									searchSchstudy();
									_message('取消合并成功' , '提醒');
								}else{
									_message('取消合并失败请重试，如果问题依然存在，请联系系统管理员！' , '提醒');
								}
							}
						);
				}
			}
		});
		
	}else{
		_message('请选择一条数据' , '提醒');
	}
}

//查找被合并的病人信息
function findRecycledPat() {
	getJSON(window.localStorage.ctx+"/register/findRecycledPat",
			{
				patientid:$('#patientid3_reg').val(),
				patientname:$('#patientname3_reg').val(),
			},	
			function(data){
				var json=validationData(data);
				$("#patientdg3_reg").datagrid("loadData",json);
			}
		);	
}


//保存前判断设备是否可用
function beforeSaveApply(print){
	console.log("saveRegisterFlag:"+saveRegisterFlag);
	if(saveRegisterFlag){//默认false
		return;
	}
	getJSON(window.localStorage.ctx+"/dic/getModalityState",
			 {
				 modalityid:$("#modality_dic_reg").combobox("getValue")
			 },
			 function(json){
				 if(json.code==0&&json.data!=null){
					 var succeed=json.data.working_state==0?true:false;
					 if(succeed){
						 saveApply(print);
					 }else{
						 _message('当前设备不可用，请重新选择！' , '保存失败');
					 }
				 }
			 }
		);
}

/**
 * 保存登记，modality_type被锁定所以通过url传递
 * @param print
 * @returns
 */
function saveApply(print) { 
    $("#registerForm").form('submit',{
		url: window.localStorage.ctx+"/register/register?modality_type="+$("#modality_reg").combobox("getValue")
			+'&modalityid='+$("#modality_dic_reg").combobox("getValue"),
		onSubmit: function() {
			if($('#institutionid_reg')[0] && $('#institutionName_reg')[0]){
				$('#institutionName_reg').val($('#institutionid_reg').combobox('getText'));
			}
			
			
			if($('#appdeptcode')[0] && $('#appdeptname_reg')[0] && !$('#appdeptname_reg').val()){
				$('#appdeptname_reg').val($('#appdeptcode').combobox('getText'));
			}
			
	        if(!$(this).form('validate') || $("#modality_reg").combobox("getValue")==""
	        	|| $("#modality_dic_reg").combobox("getValue")=="") {
	        	_message('请填入完整信息！' , '提醒');
		        return false;
		    }
	        
	        // 病人身份证号
	        var idnumber_reg = $("#idnumber_reg").textbox("getValue");
	        if (idnumber_reg != "" && !isCardNo(idnumber_reg)) {
	        	_message('身份证号填写有误！' , '提醒');
	        	return false;
			}
	        
	        // 联系电话
	        var telephone_reg = $("#telephone_reg").textbox("getValue");
	        if (telephone_reg != "" && !checkPhone(telephone_reg)) {
	        	_message('手机号填写有误！' , '提醒');
	        	return false;
			}
	        
	        var rows = $('#studydg_item_reg').datagrid('getRows');
			if (rows!=null&&rows.length>0) {
				$("#itemsstr_reg").val(JSON.stringify(rows));
			} else {
				$("#itemsstr_reg").val("");
				_message('请选择检查项目！' , '提醒');
			    return false;
			}
			
			//判断入院日期和出院日期
			if($("#admission_date_reg")[0]&&$("#discharge_date_reg")[0]){
				var datefrom=	$("#admission_date_reg").datebox("getValue");  
			    var dateto=	$("#discharge_date_reg").datebox("getValue");  
				if(datefrom!=''&&!validator_reg(datefrom)){
					_message($.i18n.prop('filter.starttimeformaterror') , $.i18n.prop('error'));
					return false;
				}
				if(dateto!=''&&!validator_reg(dateto)){
					_message($.i18n.prop('filter.endtimeformaterror') , $.i18n.prop('error'));
					return false;
				}
				if(datefrom&&dateto&&validator_reg(datefrom)&&validator_reg(dateto)
						&&new Date(datefrom.replace(/-/g, "/")) > new Date(dateto.replace(/-/g, "/"))){
					_message('出院日期大于入院日期' , $.i18n.prop('error'));
					return false;
				}
			}

			saveRegisterFlag = true;

			return true;
	    },
	    success:function(data){
	    	saveRegisterFlag = false;
			var result=validationDataAll(data);
			var resultStudyid=result.data.studyid!=null?result.data.studyid:$("#studyid_reg").val();
			if(result.code==0){
				_message('病人编号：'+result.data.patientid+'      检查号：'+resultStudyid
						+'      入院号：'+result.data.admissionid 
						, '保存成功');
				if(print){
					copiesAndPrint(result.data.orderid);
				}
				
				cancelSave();
				if($('#dg1_reg')[0]){
					refreshSearchstudy();
				}
			}else{
				_message(result.message, '保存失败');
			}
	    }
	});
}

//取消保存，清空面板
function cancelSave(){  
	if($("#registerForm")[0]){//登记
		$("#registerForm").form('clear');
	}
	
	if($("#scheduleForm")[0]){//预约
		$("#scheduleForm").form('clear');
	}
	$("#sex_reg").combobox('select', 'M');
    $("#age_unit_reg").combobox('select', 'Y');
    $('#patientsource_reg').combobox('select', 'O');

    $('#admissiondg_reg').combogrid('grid').datagrid('loadData',{total:0,rows:[]});

    $("#priority_com_reg").combobox('select', 'N');
    
    var data = $("#modality_reg").combobox('getData');
    console.log(data);
    if(data){
    	$("#modality_reg").combobox('select', data[0].code);
    }
    
    
    $("#searchItemtree_reg").searchbox('setValue', null);
 
    $('#studydg_item_reg').datagrid('loadData',{total:0,rows:[]});
    $('#studydg_item_reg').datagrid('reloadFooter',{total:0,rows:[]});
//    $('#itemtree_reg').datalist('loadData',{total:0,rows:[]});
    
    setStudy(true);
    $("#modality_reg").combobox('enable');
    $("#modality_dic_reg").combobox('enable');
//    $("#modality_dic_reg").combobox({
//    	disabled:false,
//    	onLoadSuccess:function(data){
//    		
//		}
//    });
    
    imgulp.dataempty();
    /*imgulp = new Jsequencing({
		listid:"img_ul",//页面图片列表ID
		thumbherf:"",//列表图片前缀
		bigherf:"",//原图前缀[同列表图相同时，省略]
		jsondata:true,
		imgsrcarr:[]//图片数据数组
	});*/
}

function search_im(){
	$("#searchForm_im").submit();
}

function clear_im(){
	$('#patientid_im').textbox("setValue","");
	$('#patientname_im').textbox("setValue","");
	$('#cardno_im').textbox("setValue","");
	$('#inno_im').textbox("setValue","");
	$('#outno_im').textbox("setValue","");
	$('#studyid_im').textbox("setValue","");
	
	$('#dept_im').combobox('clear');
	$('#doctor_im').combobox('clear');
	$('#appmodality_im').combobox('clear');
	
	$('#appdatefrom_im').datebox('setValue', '');
	$('#appdateto_im').datebox('setValue', '');
	$('#apptoday_im').linkbutton({selected:false});
	$('#appyesterday_im').linkbutton({selected:false});
	$('#appthreeday_im').linkbutton({selected:false});
	$('#appfiveday_im').linkbutton({selected:false});
	$('#appweek_im').linkbutton({selected:false});
	$('#appmonth_im').linkbutton({selected:false});
	$('#appdate_im').val('');
}

function turnIntoRegister_im(){
	var row=$('#dg1_im').datagrid('getSelected');
	if(row){
		if(row.status!="toIP"){
			_message('当前数据已处理' , '提醒');
			return;
		}
		$("#patientname_reg").textbox("setValue",row.patientname);
	    $("#age_reg").textbox("setValue",row.age);
	    $("#age_unit_reg").combobox('select', row.ageunit);
	    $("#birthdate_reg").datebox("setValue",row.birthdate);
	    $("#sex_reg").combobox('select', row.sex);
	    $("#height_reg").textbox("setValue",row.height);
	    $("#weight_reg").textbox("setValue",row.weight);
	    $("#title_reg").textbox("setValue",row.title);
	    $("#telephone_reg").textbox("setValue",row.telephone);
	    $("#address_reg").textbox("setValue",row.address);
	    $("#idnumber_reg").textbox("setValue",row.idnumber);
	    
	    $("#patientsource_reg").combobox('select', row.patientsource);
	    $("#cardno_reg").textbox("setValue",row.cardno);
	    $("#inno_reg").textbox("setValue",row.inno);
	    $("#outno_reg").textbox("setValue",row.outno);
	    $("#wardno_reg").combobox('select', row.wardno);
	    $("#bedno_reg").combobox("select",row.bedno);
	    $("#insurance_reg").textbox("setValue",row.insurance);
	    $("#admission_date_reg").datebox("setValue",row.adm_date);
		$("#discharge_date_reg").datebox("setValue",row.dis_date);

	    $("#priority_com_reg").combobox('select', row.priority);
	    $("#modality_reg").combobox('select', row.modality_type);
	    $("#modality_reg").combobox('disable');
	    $("#modality_dic_reg").combobox("select",row.modalityid);
	    $("#modality_dic_reg").combobox('disable');
	    $("#appdeptcode").combobox("select",row.appdeptcode);
	    $("#doctor_reg").combobox("select",row.appdoctorcode);
	    
	    $("#so_remark_reg").val(row.remark);
	    getJSON(window.localStorage.ctx+"/register/getOrderItem",
	    		{
	    			orderid : row.id
	    		},
	    		function(json){
		    		$("#studydg_item_reg").datagrid("loadData",validationData(json));
		    		reloadDataFooter($("#studydg_item_reg"));
	    		}
	    );
		$('#tabs_div_reg').tabs('select',1);
	}else{
		_message('请选择一条数据' , '提醒');
	}
}

function selectimport(title,index){
	if(index==0){
		search_im();
	}

}

function toSchedule(){
	var row=$("#dg1_reg").datagrid("getSelected");
	if(row!=null){
		if(row.status==myCache.StudyOrderStatus.registered||row.status==myCache.StudyOrderStatus.scheduled){
			//判断当前状态
			getJSON(window.localStorage.ctx+"/register/getStatus?orderid="+row.studyorderpkid, null ,function(json){
				var data = validationDataAll(json);
				if(data.code==0){
					if(data.data.status==myCache.StudyOrderStatus.registered||data.data.status==myCache.StudyOrderStatus.scheduled){
						canToSchedule(row);
					}else{
						_message('检查状态已经被更新，正在刷新查询结果。请重试转预约操作。' , '提醒');
						refreshSearchstudy();
					}
				}else{
					_message('请求数据失败', '提醒');
				}	
			});	
		}
		else{
			_message('当前不是登记状态，无法转预约！', '提醒');
		}			
	}else{
		_message('请选择一条数据', '提醒');
	}
}

function canToSchedule(row){
	$('<div></div>').dialog({
		id : 'newDialog',
		title:'转预约',
		width:810,height:550,
		resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href:window.localStorage.ctx+"/register/openScheduleDlg?patientpkid="+row.patientpkid+
			"&admissionpkid="+row.admissionpkid+"&studyorderpkid="+row.studyorderpkid,
		buttons:[{
			text:'保存',
			width:80,
			handler:function(){regToSchedule();}
		},
		{
			text:'关闭',
			width:80,
			handler:function(){$('#newDialog').dialog('destroy');}
		}]
		,onClose:function(){
			refreshSearchstudy();
			$('#newDialog').dialog('destroy');
		}
	});
}

//function closeScheduleDlg(){
//	$('#common_dialog').dialog('close');
//}

function handleOnChange_modality(newValue,oldValue){
	handleSelect_gettime(null);	
	var select=null;
	$.each($("#modality_dic_sc").combobox("getData"),function (index,data){
		if(data.id==newValue){
			select=data;
			return false;
		}
	});
	
	//设置可预约的日期
	if(select){
		$("#calendar_sc").calendar({validator: function(date){
			var tmp=new Date();
			if (tmp.getTime()>date.getTime()||(date.getDay()==6&&!select.saturday_of_worktime)||(date.getDay()==0&&!select.sunday_of_worktime)){
				//console.log("false=");
				return false;
			}
			else{
				return true;
			}
		}});
	}
	
}

function handleSelect_gettime(date){
	if(!date){
		date=$('#calendar_sc').calendar('options').current;
	}
	//calendar('options').current可能为空
	if(!date){
		return;
	}
//	var modality=$('#modalityType_sc').textbox('getValue');
	var modalityid=$('#modality_dic_sc').combobox('getValue');
	var orderid=$("#studyorderid_sc").val();
	if(modalityid){
//		$('#schedule_time_panel').panel('refresh',window.localStorage.ctx+'/schedule/showScheduleTime?date='+date.format("yyyy-MM-dd")+'&modalityid='+modalityid+"&modality="+modality);	
		$('#schedule_time_panel').panel('refresh',window.localStorage.ctx+'/schedule/showScheduleTime?date='+date.format("yyyy-MM-dd")+'&modalityid='+modalityid+"&orderid="+orderid);
	}
	else{
		_message('请选择检查设备！', '提醒');
	}
}

function setValue_schtime(time){
	console.log(time);
	var date=$('#calendar_sc').calendar('options').current;
	$('#appointmenttime_sc').val(date.format("yyyy-MM-dd")+" "+time+":00");
}

function regToSchedule(){
	
	if($('#appointmenttime_sc').val()==""){
		_message('请选择预约时间！', '提醒');
		return false;
    }
	
	getJSON(window.localStorage.ctx+"/register/toSchedule",
			{
		        orderid: $('#studyorderid_sc').val(),
		        modalityid: $('#modality_dic_sc').combobox('getValue'),
		        modalityType: $('#modalityType_sc').textbox('getValue'),
		        appointmenttime: $('#appointmenttime_sc').val()
		    },
		    function(data){
				var result=validationDataAll(data);
				if(result.code==0){
					if(result.data=="1"){
						refreshSearchstudy();
						$('#newDialog').dialog('close');
						_message('检查号：'+$('#studyid_sc').val()+ '预约时间：'+$('#appointmenttime_sc').val(), '预约成功');
					}
					else if(result.data=="2"){
						_message('预约失败请重试，如果问题依然存在请联系系统管理员！', '预约失败');
					}
					else if(result.data=="3"){
						_message('预约时间已经被占用，请选择其他时间！', '预约失败');
					}
				}
				else{
					_message('保存失败请重试，如果问题依然存在请联系系统管理员！', '保存失败');
				}
		    }
		);
}

function priorityFormat(val, row, index){
	var style = "";
	if(row.precedence == 2){
		style += '<img src="image/warning.png" width="16" heigth="16">'
	}
	/*if(row.priority == 'V'){
		style += '<div style="height: 18px; width: 18px; background-color: red; border-radius: 9px;"><div style="height: 9px; width: 9px; background: white; border-radius: 5px;position: relative;left: 4.5px; top: 4.5px;"></div></div>';
	}*/
	return style;
	
}

/**
 * 状态颜色
 */
function columeStyler_orderstatus_reg(value,row,index){
	var color=myCache.status_color['0005_'+row.status];
	if(color && color == "#ffffff"){
		return "<span style='color: "+color+"'>" + value + "</span>"
	}
	else if(color){
		return 'background-color:'+color+';';
	}
	else{
		return '';
	}
}

function columeStyler_reportstatus_reg(value,row,index){
	var reportstatusdisplay = row.reportstatusdisplaycode;
	var color=myCache.status_color['0007_'+reportstatusdisplay];
	if(color && color == "#ffffff"){
		return "<span style='color: "+color+"'>" + value + "</span>"
	}
	else if(color){
		return 'background-color:'+color+';';
	}
	else{
		return '';
	}
//	if(color){
//		return "<span style='color: "+color+"'>" + value + "</span>"
//	}
//	else{
//		return '';
//	}
}

function columeStyler_studyid(val,row,index){
	if (row.patientsource == 'E'){
        return 'background-color:#F00;font-weight:bold;';
    }
}

function printcount_formatter_reg(value,row,index){
	if(value==0){
		return "未打印";
	}
	else{
		return "已打印";
	}
}

function age_formatter(value,row,index){
	if(value){
		return value+row.ageunitdisplay;
	}
	else{
		return "";
	}
}

/**
 * 报告预览
 */
function detailFormatter_wl_previewreport(index,row){
	return '<div class="ddv" style="padding:5px 0"></div>';
}
/**
 * 报告预览展开
 */
function onExpandRow_wl_previewreport(index,row){
	if (row.reportid && row.reportstatusdisplaycode == myCache.ReportStatus.FinalResults) {
		var ddv = $('#dg1_reg').datagrid('getRowDetail',index).find('div.ddv');
	    ddv.panel({
	        border:false,
	        cache:false,
	        href:window.localStorage.ctx+"/worklist/previewReport?reportid="+row.reportid,
	        onLoad:function(){
	            $('#dg1_reg').datagrid('fixDetailRowHeight',index);
	        }
	    });
	    $('#dg1_reg').datagrid('fixDetailRowHeight',index);
    
	}
}

/*function reportPreview(){
	var row=$("#dg1_reg").datagrid("getSelected");
	if(!row){
		$.messager.show({
	        title:'提醒',
	        msg:'请选择一条数据',
	        timeout:3000,
	        border:'thin',
	        showType:'slide'
	    });
	}else if(!row.reportid || row.reportstatusdisplaycode!=myCache.ReportStatus.FinalResults){
		$.messager.show({
	        title:'提醒',
	        msg:'当前报告未审核',
	        timeout:3000,
	        border:'thin',
	        showType:'slide'
	    });
	}else{
		var issr = 0;
		if(row.templateId!=null && row.templateId>0){
			issr = 1;
		}
		 window.open(window.localStorage.ctx+"/print/printReport?reportid="+ row.reportid +"&issr="+issr);
	}
}*/

function initStudyOrders(){
	$('#appno_orders').textbox('textbox').bind('keydown', function(e){
		if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
			getJSON(window.localStorage.ctx+"/register/findStudyorder_FromThridIS",
				{
					appno:$('#appno_orders').textbox('getValue'),
					patient_source : $('#patientsource_orders').combobox('getValue')
				},
				function(data){
					$("#studyorders_reg").datagrid("loadData", validationData(data));
				}
			);
		}
	});
}

/**
 * 登记录入中，检查申请信息查询
 * @returns
 */
function searchStudyOrders(){
	
	$('#progress_dlg').dialog('open');
	$("#studyorders_Form").form('submit',{
		url: window.localStorage.ctx+"/register/findStudyorder_FromThridIS",
		onSubmit : function() {
			var datefrom=$("#datefrom_stdord").datebox("getValue");  
		    var dateto=	$("#dateto_stdord").datebox("getValue");
			if(datefrom!=''&&!validator_reg(datefrom)){
				_message($.i18n.prop('filter.starttimeformaterror') , $.i18n.prop('error'));
				return false;
			}
			if(dateto!=''&&!validator_reg(dateto)){
				_message($.i18n.prop('filter.endtimeformaterror') , $.i18n.prop('error'));
				return false;
			}
		},
		success:function(data){
	    	$("#studyorders_reg").datagrid("loadData", validationData(data));
	    	$('#progress_dlg').dialog('close');
	    }
	});
}

function extractStudyOrders(row){
	console.log(row)
	cancelSave();
	extractFlag = true;
/*	$("#patientkid_reg").val(row.patientid); //病人编号
	$("#patientname_reg").textbox("setValue",row.patientname);//病人姓名
	
//    $("#pinyin_reg").textbox("setValue",row.py);
	
	if(row.birthdate){
		try{
			var date = new Date(row.birthdate)
			$("#birthdate_reg").datebox("setValue",date.format('yyyy-MM-dd'));
		}
		catch(e){
			console.log(e);
		}
	}
	$("#sex_reg").combobox('select', row.sex);
//	if(row.sex=="男"){
//		$("#sex_reg").combobox('select', 'M');
//	}
//	else if(row.sex=="女"){
//		$("#sex_reg").combobox('select', 'F');
//	}
//	else{
//		$("#sex_reg").combobox('select', 'O');
//	}
//    $("#height_reg").textbox("setValue",row.height);
//    $("#weight_reg").textbox("setValue",row.weight);
//    $("#title_reg").textbox("setValue",row.title);
	$("#address_reg").textbox("setValue",row.address);
	$("#telephone_reg").textbox("setValue",row.telephone);
	$("#idnumber_reg").textbox("setValue",row.idnumber);

	var str=$("#birthdate_reg").datebox("getValue");
	var arr=str.split("-");
	var birthdate=new Date(arr[0],arr[1]-1,arr[2]);

	getAgeFromBirthdate(birthdate);
	

//	$("#admissionid_reg").textbox("setValue",row.admissionid);
//	$("#patientsource_reg").combobox("select",row.patientsource);
	$("#cardno_reg").textbox("setValue",row.cardno);
	$("#inno_reg").textbox("setValue",row.inno);
	$("#outno_reg").textbox("setValue",row.outno);
//	$("#wardno_reg").combobox('select', row.wardno);
//	$("#bedno_reg").combobox("select",row.bedno);
//	$("#insurance_reg").textbox("setValue",row.insurance);
//	$("#admission_date_reg").datebox("setValue",row.adm_date);
//	$("#discharge_date_reg").datebox("setValue",row.dis_date);
	$("#appdatetime_reg").val(row.appdatetime);
	
	var obj=new Object();
	obj.appdeptcode=row.appdeptcode;
	obj.appdeptname=row.appdeptname;
	
	$("#appdeptcode").combobox("loadData",[{
		deptcode: row.appdeptcode,
		deptname: row.appdeptname
	}]);
	if(row.appdeptcode){
		$("#appdeptcode").combobox("setValue",row.appdeptcode);
	}
	else{
		$("#appdeptcode").combobox("setText",row.appdeptname);
	}
	$("#appdeptname_reg").val(row.appdeptname);
	$("#doctor_reg").combobox("loadData",[{
		appdoctorcode: row.appdoctorcode,
		appdoctorname: row.appdoctorname
	}]);
	if(row.appdoctorcode){
		$("#doctor_reg").combobox("setValue",row.appdoctorcode);
	}
	else{
		$("#doctor_reg").combobox("setText",row.appdoctorname);
	}
	$("#appdoctorname_reg").val(row.appdoctorname);
	
	$("#workunitcode_reg").val(row.workunitcode);
	$("#workunit_reg").textbox("setValue",row.workunit);
	
	var data=$("#institutionid_reg").combobox("getData");
	if(data!=null){
		for(var k=0,len=data.length;k<len;k++){
			if(data[k].name==row.institution_name){
				$("#institutionid_reg").combobox("setValue",data[k].id);
				continue;
			}
		}
	}
	//console.log(data);
	
	
	$('#admittingDiagnosis_reg').textbox('setValue',row.admittingdiagnosis);*/
//	$('#subjective_reg').textbox('setValue',row.subjective);
	
	
	
//	$("#studyorderid_reg").val(row.studyorderpkid);
//	$("#studyorder_status_reg").val(row.status);
//	$("#studyid_reg").textbox("setValue",row.studyid);
//    $("#priority_com_reg").combobox('select', row.priority);
//    $("#modality_reg").combobox('select', row.modality_type);
//    $("#modality_dic_reg").combobox("select",row.modalityid);
//    $("#dept_reg").combobox("select",row.appdept);
//    $("#doctor_reg").combobox("select",row.appdoctor);

	/*$("#eorderid").val(row.his_orderid);
	$("#systemid_reg").val(row.systemid);*/
	
	$("#registerForm").form('load',row);
	//检查项目
	if(row.studyitems){
		getJSON(window.localStorage.ctx+"/register/getStudyItem_his",
				{
					id : row.id
				},
				function(json){
					console.log(json);
					if(json && json.length>0){
						for(var i=0;i<json.length;i++){
				    		var rowData=json[i];
				    		$('#studydg_item_reg').datagrid('appendRow',{
				    			id: rowData.id,
				    			modality: rowData.modality,
				    			item_name: rowData.item_name,
				    			item_id: rowData.itemid,
				    			organ: rowData.organ || "",
				    			suborgan: rowData.suborgan || "",
				    			price: rowData.price || "",
				    			realprice: rowData.realprice || "",
				    			charge_status: rowData.charge_status
				    		});
				    	}
						
						/*$("#studydg_item_reg").datagrid('reloadFooter',[
							{item_name: '合计', price: row.price.toFixed(2), realprice:row.realprice.toFixed(2)}
						]);*/
					}
				})
	}
	/*if(row.item_code){
	    getJSON(window.localStorage.ctx+"/register/getExamItemByHisItemCode",
	    		{
	    			hisitemcode : row.item_code
	    		},
	    		function(json){
		    	//console.log(json)
		    	if(json&&json.length>0){
		    		var firstrow=json[0];
			    	for(var i=0;i<json.length;i++){
			    		var rowData=json[i];
			    		
			    		if(!rowData.price){
			    			rowData.realprice = row.realprice;
			    			rowData.price = row.price;
			    		}
			    		$('#studydg_item_reg').datagrid('appendRow',{
			    			id: rowData.id,
			    			modality: rowData.type,
			    			item_name: rowData.item_name,
			    			item_id: rowData.id,
			    			organ: rowData.organfk||"",
			    			suborgan: rowData.suborganfk||"",
			    			price: rowData.price||"",
			    			realprice: rowData.realprice || "",
			    			charge_status:'已收费'
			    		});
			    	}
			    	reloadDataFooter($("#studydg_item_reg"));
			    	$('#modality_reg').combobox('setValue', firstrow.type);
			    	$("#modality_dic_reg").combobox("setValue",firstrow.equip_id);
			    	$("#organ_reg").combobox("setValue",firstrow.organfk);
			    	//setStudy(false);
		    	}
		    	else{
		    		$('#modality_reg').combobox('setValue', row.modality);
		    	}
	
	    });
	}*/
    
    
   /* //病人一致性严重
    getJSON(window.localStorage.ctx+"/register/checkSameName",
		{
    		hospitalizeNo : row.hospitalizeNo,
    		patientname : row.patientname
		},
		function(data) {
			extractFlag = false;
			console.log(data);
			if(data && data.hospitalizeno==1){
				$("#patientkid_reg").val(data.patient[0].id);
				$("#patientid_reg").textbox("setValue",data.patient[0].patientid);
			}else if(data && data.hospitalizeno==2){
				openSameNameDlg(data.patient);
			}
		}
	);*/
    
}

function clearStudyOrders(){
	$("#patientname_orders").textbox("setValue","");
	$("#appno_orders").textbox("setValue","");
	var now=new Date();
	$("#datefrom_stdord").datebox("setValue",now.format("yyyy-MM-dd"));
	$("#dateto_stdord").datebox("setValue",now.format("yyyy-MM-dd"));
}

/**
 *  查询
 * @param e
 * @returns
 */
function quickSearch(value,name) {
	value = $.trim(value).replace(/[\-\_\,\!\|\~\`\(\)\#\$\%\^\&\{\}\:\;\"\L\<\>\?]/g, '');
	if (value === null || value === "" || value === undefined) {
		_message('请输入要查询的数据！' , '提醒');
	} else {
		searchForm_flag = "quicksearch";
		if(nowflag){
			searchstudy(nowPageNumber, nowPageSize, value, name);
		}else{
			searchstudy(null, null, value,name);
		}
	}
}

/**
 *  报告导出pdf
 * @param id
 * @returns
 */
function exportReport() {
	var row = $("#dg1_reg").datagrid("getSelected");
	if (row) {
		if (row.reportstatusdisplaycode+"" === myCache.ReportStatus.FinalResults) {
			// 导出pdf
			window.location.href = window.localStorage.ctx+"/worklist/exportReport?reportid=" + row.reportid 
		} else {
			_message('请选择已审核的报告' , '提醒');
		}
	} else {
		_message('请选择一条数据' , '提醒');
	}
}

function openDutyRoster(){
	$('#common_dialog').dialog({
		title : '排班表',
		width : 1100,height : 650,
		modal : true,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,
		border: 'thin',
		href : window.localStorage.ctx+'/register/showDutyRoster',
		buttons:[{
			text:$.i18n.prop('report.close'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}],
		onLoad:function(){}
	});
}
