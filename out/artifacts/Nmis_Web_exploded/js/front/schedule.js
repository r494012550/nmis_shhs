var nowPageNumber_sch = 1;//查询结果当前页码
var nowPageSize_sch = 50;//查询结果当前结果数量
var nowflag_sch = false;//标识查询结果是否使用当前页码，nowflag_sch = true时使用当前页码
var searchForm_flag_sch = "searchForm";//标识查询方式，searchForm_flag_sch == "quicksearch"为快速全文查询

var saveScheduleFlag = false;//预约表单提交标识，saveScheduleFlag = true时不提交，避免重复提交

var schedule_date=new Date();
function handleSelect(date){
	schedule_date=date;
	showSchedule(date,null);
}

function handleClickRow(index,row){
	showSchedule(null,row);
}

function handleLoadData(data){
	if(data.rows.length>0){
		$("#modality_list").datagrid("selectRow",0);
		showSchedule(null,data.rows[0]);
	}
}

function showSchedule(date,row){
	if(!date){
		date=schedule_date;
	}
	if(!row){
		row=$("#modality_list").datagrid("getSelected");
		
	}
	$('#layout_schedule').layout('panel', 'center').panel('refresh',window.localStorage.ctx+'/schedule/showScheduleInfo?date='+date.format("yyyy-MM-dd")+'&modalityid='+row.id+"&modality="+row.type);
}


function validator_sch (value) { 
    //格式yyyy-MM-dd或yyyy-M-d
       return/^(?:(?!0000)[0-9]{4}([-]?)(?:(?:0?[1-9]|1[0-2])\1(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])\1(?:29|30)|(?:0?[13578]|1[02])\1(?:31))|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([-]?)0?2\2(?:29))$/i.test(value); 
}

function setSelectableAppointmentDate(modalityid) {
	var select = null;
	$.each($("#modality_dic_reg").combobox("getData"),function (index,data){
		select = data;
		if (data.id == modalityid) {
			return false;
		}
	});
	var now=new Date();
    if(select!=null){
	//设置可预约的日期
	$("#calendar_sch").calendar({validator: function(date) {
		var tmp = new Date();
		if (tmp.getTime()>date.getTime()||(date.getDay()==6&&!select.saturday_of_worktime)||(date.getDay()==0&&!select.sunday_of_worktime)){
			return false;
		} else {
			return true;
		}
	}});
	//获取一个可预约的日期，并赋值给calendar_sch
	while(true){
		now.setDate(now.getDate()+1);
		if(now.getDay()!=0&&now.getDay()!=6){
			break;
		}
		if(now.getDay()==6&&select.saturday_of_worktime){
			break;
		}
		if(now.getDay()==0&&select.sunday_of_worktime){
			break;
		}
	}
    }
	$('#calendar_sch').calendar('moveTo',now);
	handleSelect_gettime();	
}


function handleSelect_gettime(date){
	if(!date){
		date=$('#calendar_sch').calendar('options').current;
	}else{
		date = new Date(date);
	}
//	var modality=$('#modality_reg').combobox('getValue');
	var modalityid=$('#modality_dic_reg').combobox('getValue');
	var orderid=$("#studyorderid_reg").val();
	if(modalityid){
		$('#schedule_time_panel').panel('refresh',window.localStorage.ctx+'/schedule/showScheduleTime?date='+date.format("yyyy-MM-dd")+'&modalityid='+modalityid+"&orderid="+orderid);
	}
	else{
		_message('请选择检查设备！' , '提醒');
	}

}

function handleSelect_gettime2(date){
	if(!date){
		date=$('#calendar_sch').calendar('options').current;
	}
	var modality=$('#modality_sch').combobox('getValue');
	var modalityid=$('#modality_dic_sch').combobox('getValue');
	var orderid=$("#studyorderidpk").val();
	
	if(modalityid){
		$('#schedule_time_panel').panel('refresh',window.localStorage.ctx+'/schedule/showScheduleTime2?date='+date.format("yyyy-MM-dd")+'&modalityid='+modalityid+"&modality="+modality+'&orderid='+orderid);
		
	}
	else{
		_message('请选择检查设备！' , '提醒');
	}

}

function setValue_schtime(time){
	var date=$('#calendar_sch').calendar('options').current;
	$('#appointmenttime').val(date.format("yyyy-MM-dd")+" "+time+":00");
}

//扫描申请单
function triggerScan_sch(orderid, studyid){
	var scanurl = $("#projecturl_res").val();
	var a = "";
	if(orderid && studyid){//登记保存之后扫描，有studyid
		console.log("triggerScan:"+" orderid:"+orderid+" studyid:"+studyid);
		var a="reporttool:-c scan -s "+scanurl+" -n orderid="+orderid+"&studyid="+studyid;
		
	}else{//预约保存之前扫描
		var a="reporttool:-c scan -s "+scanurl+" -n userid="+$('#scanClientUserid_reg').val()+"&launchip="+$('#scanClientIp_reg').val();
	}
	urlFlag = true;
	if(electron_enable()){
		electron_scan(a);
	} else{
		$("#scan_sch")[0].setAttribute('href',a);
		$("#scan_sch")[0].click();
	}
}

//预约信息 保存前判断设备是否可用
function beforeSaveSchedule(print) {
	if(saveScheduleFlag){//默认false，避免重复提交
		return;
	}
	getJSON(window.localStorage.ctx+"/dic/getModalityState",
			 {
				 modalityid:$("#modality_dic_reg").combobox("getValue")
			 },
			 function(json) {
				 if (json.code==0&&json.data!=null) {
					 var succeed=json.data.working_state==0?true:false;
					 if (succeed) {
						 saveSchedule(print);
					 } else {
						 _message('当前设备不可用，请重新选择！' , '保存失败');
					 }
				 }
			 }
		);
}

//保存预约
function saveSchedule(print) {
	$("#scheduleForm").form('submit',{
		url: window.localStorage.ctx+"/schedule/saveSchedule?modality_type="+$("#modality_reg").combobox("getValue")
				+'&modalityid='+$("#modality_dic_reg").combobox("getValue"),
		onSubmit: function() {
	        if (!$(this).form('validate')) {
	        	_message('请正确填入完整信息！' , '提醒');
		        return false;
		    }
	        
	        var rows = $('#studydg_item_reg').datagrid('getRows');
	        if (rows != null && rows.length > 0) {
				$("#itemsstr_reg").val(JSON.stringify(rows));
			} else {
				 $("#itemsstr_reg").val("");
				 _message('请选择检查项目！' , '提醒');
			     return false;
			}
	        
	        if ($('#appointmenttime').val()=="") {
	        	_message('请选择预约时间！' , '提醒');
	        	return false;
	        }
	        
	        saveScheduleFlag = true;
	        
			return true;
	    },
	    success:function(data) {
	    	saveScheduleFlag = false;
			var result = validationDataAll(data);
			if (result.code==0) {
				_message('病人编号：'+result.data.patientid+ '检查号：'+result.data.studyid
						+ '预约时间：'+$('#appointmenttime').val()+'  序号：'+result.data.sequencenumber 
						, '保存成功');
				
				cancelSave_sch();
				
				if(print){
					copiesAndPrintReservation(result.data.orderid);	
				}
				
				if($('#dg_search_sch')[0]){
					refreshSearchSchstudy();
				}
			} else {
				handleSelect_gettime();
				_message(result.message, '保存失败');
			}
	    }
	});
}

function cancelSave_sch(){
    cancelSave();
//    handleSelect_gettime();
}

var default_page=1;
var default_pageSize=20;
function initSearchSch() {
	//设置分页控件 
    var p = $('#dg_search_sch').datagrid('getPager'); 
    $(p).pagination({ 
        pageSize: default_pageSize,//每页显示的记录条数，默认为10 
        pageNumber:default_page,
        pageList: [10,20,30,50],//可以设置每页记录条数的列表 
        beforePageText: $.i18n.prop('wl.beforepagetext'),//页数文本框前显示的汉字 
        afterPageText: $.i18n.prop('wl.afterpagetext'),
        displayMsg: $.i18n.prop('wl.displaymsg'),
        onSelectPage:function(pageNumber, pageSize){
        	$("#page").val(pageNumber);
        	$("#pageSize").val(pageSize);
        	$(this).pagination('loading');
    		$('#progress_dlg').dialog('open');
    		// 分页搜索时，判断是左边点击的搜索，还是搜索框中的搜索
    		if (searchForm_flag_sch == "searchForm") {
    			searchSchstudy(pageNumber, pageSize, null,null);
    		} else if (searchForm_flag_sch == "quicksearch") {
    			searchSchstudy(pageNumber,pageSize,$('#quicksearch-input_sch').searchbox('getValue'),$('#quicksearch-input_sch').searchbox('getName'));
    		}
    		$(this).pagination('loaded');
    	}
    });
	
	
	$("#searchForm_schedule").form({
		url: window.localStorage.ctx+"/schedule/getScheduleorder?page="+ default_page +"&rows="+ default_pageSize,
		onSubmit: function(){
			if(checkSearchSchedule()){
				$('#progress_dlg').dialog('open');
				return true;
			} else {
				return false;
			}
	    },
	    success:function(data){
	    	if(!data.code){
	    		$("#dg_search_sch").datagrid("loadData",validationData(data));
	    		if(nowflag_sch){
	 				var p = $('#dg_search_sch').datagrid('getPager'); 
	 				$(p).pagination({ 
	 					 pageNumber:nowPageNumber_sch,
	 				});
	 				nowflag_sch = false;
	 			} 
	    	}
		    
		    $('#progress_dlg').dialog('close');
	    }
		
	});
	
	$('#searchForm_schedule').find('.easyui-textbox').each(function(index,element){
		$(element).textbox('textbox').bind('keydown', function(e){
			if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
				searchSchstudy();
			}
		});
	})
	
	searchSchstudy();
}

/**
 *  查询前校验
 * @returns
 */
function checkSearchSchedule() {
	var checkResult = true;
	if (searchForm_flag_sch == "quicksearch") {
		return checkResult;
	}
	// T：今天；Y：昨天；TD：近三天；FD：近五天， W：近一周， TM：近三个月
	var appdate = $('#appdate').val();
	var datefrom = $('#datefrom').datebox('getValue'); // 开始时间
	var dateto = $('#dateto').datebox('getValue');  // 结束时间 
	var apppatientid = $('#apppatientid').textbox('getValue'); // 病人编号
	var appstudyid = $("#appstudyid").textbox("getValue");  // 检查号
	var appoutno = $('#appoutno').textbox('getValue'); // 门诊号
	var appinno = $('#appinno').textbox('getValue'); // 住院号
	var apppatientname = $("#apppatientname").textbox("getValue"); // 姓名
	var appcardno = $("#appcardno").textbox("getValue"); // 卡号
	if (appstudyid == "" && apppatientid == "" && apppatientname == "" && appoutno == ""
		&& appinno == "" && datefrom == "" && appcardno == "" && dateto == "" && appdate == "") {
		checkResult = false;
		_message('请选择需要查询的具体一个时间段！' , '提醒');
	}
	if (datefrom != "" && dateto == "") {
		checkResult = false;
		_message('结束时间不能为空！' , '提醒');
	}
	if (datefrom == "" && dateto != "") {
		checkResult = false;
		_message('开始时间不能为空！' , '提醒');
	}
	if (datefrom != "" && dateto != "") {
		var date1 = new Date(datefrom);
	    var date2 = new Date(dateto);
	    if (date1.getTime() > date2.getTime()) {
	    	checkResult = false;
	    	_message('开始时间不能大于结束时间！' , '提醒');
	    }
	}
	return checkResult;
}

//刷新查询结果，nowflag = true指定当前页码
function refreshSearchSchstudy(){
	var options = $("#dg_search_sch" ).datagrid("getPager" ).data("pagination" ).options;
	nowPageNumber_sch = options.pageNumber;
	nowPageSize_sch = options.pageSize;
	nowflag_sch = true;
	if(searchForm_flag_sch == "quicksearch"){
		quickSearch_sch($('#quicksearch-input_sch').searchbox('getValue'), $('#quicksearch-input_sch').searchbox('getName'));
	}else{
		searchSchstudy(nowPageNumber_sch, nowPageSize_sch, "", "");//即时更新查询到的数据
	}
}

/**
 *  查询预约
 * @param pageNumber  当前页数
 * @param pageSize    每页个数
 * @param quicksearchcontent   需要搜索的内容
 * @param quicksearchname      搜索是否关联左边的查询条件
 * @returns
 */
function searchSchstudy(pageNumber, pageSize, quicksearchcontent, quicksearchname){
	if(!pageNumber) {
		pageNumber=default_page;
		$('#dg_search_sch').datagrid('getPager').pagination({ 
			pageSize: default_pageSize,//每页显示的记录条数，默认为20 
			pageNumber:default_page
		});
	}
	if (!pageSize) {
		pageSize = default_pageSize;
	}
	$("#searchForm_schedule").form('submit',
		{url:window.localStorage.ctx+"/schedule/getScheduleorder?page="+ pageNumber +"&rows="+ pageSize
		+ "&quicksearchcontent=" + (quicksearchcontent||"") + "&quicksearchname=" + (quicksearchname||"")});
}


// 取消预约
function cancelApp_sch() {
	var rows = $("#dg_search_sch").datagrid("getSelections");
	if (rows != null && rows.length == 1) {
		var row = rows[0];
		if (row.status == myCache.StudyOrderStatus.scheduled) {
			$.messager.confirm({title: '确认',border:'thin',msg: '确认取消选中的预约申请？',
				fn: function(r){
					if(r){
						getJSON(window.localStorage.ctx+"/schedule/cancelScheduleOrder",
							{
								orderid : row.studyorderpkid
							},function(data){
								var json=validationData(data);
								if(json.code==0){
									refreshSearchSchstudy();
								}else{
									_message('取消失败请重试，如果问题依然存在，请联系系统管理员！' , '提醒');
								}
							});
					}
				}
			});
		}else{
			_message('当前不是预约状态，无法取消！' , '提醒');
		}
	}else{
		_message('请选择一条数据！' , '提醒');
	}
}


// 删除预约信息
function deleteApp_sch(){
	var rows = $("#dg_search_sch").datagrid("getSelections");
	if (rows != null && rows.length == 1) {
		var row = rows[0];
		if(row.status == myCache.StudyOrderStatus.scheduled || row.status == myCache.StudyOrderStatus.cancel_the_appointment){
			$.messager.confirm({title: '确认',border:'thin',msg: '是否删除选中的预约申请？',
				fn: function(r){
					if(r){
						getJSON(window.localStorage.ctx+"/schedule/deleteScheduleOrder",
							{
								orderid : row.studyorderpkid
							}, function(data){
								var json=validationData(data);
								if(json.code==0){
									refreshSearchSchstudy();
									_message('删除成功' , '提醒');
						    	}
						    	else{
						    		_message('删除失败请重试，如果问题依然存在，请联系系统管理员！' , '提醒');
						    	}
						    });
					}
				}
			});
		}
		else{
			_message('当前不是预约状态，无法删除！' , '提醒');
		}
	}
	else{
		_message('请选择一条数据！' , '提醒');
	}
}


//删除病人信息
function deletePatient_sch(){
	var rows = $("#dg_search_sch").datagrid("getSelections");
	if (rows != null && rows.length == 1)
	{
		var row = rows[0];
		if(row.status==myCache.StudyOrderStatus.registered||
				row.status==myCache.StudyOrderStatus.scheduled||
				row.status==myCache.StudyOrderStatus.cancel_the_appointment||
				row.status==myCache.StudyOrderStatus.canceled)
		{
			$.messager.confirm({title:'确认',border:'thin',msg:'是否删除选中的病人信息？该操作会删除病人名下所有检查且不可恢复！',
				fn:function(r){
					if(r){
						getJSON(window.localStorage.ctx+"/schedule/deletePatient",
								{
									patientpkid : row.patientpkid
								},function(data){
									var json=validationDataAll(data);
									if(json.code !=0 || json.data == "2"){
										_message('删除失败请重试，如果问题依然存在，请联系系统管理员！' , '错误');
									}
									else if(json.data == "1"){
										_message('删除成功！' , '成功');
										refreshSearchSchstudy();
									}
									else if(json.data == "3"){
										_message('无法删除，病人存在正在检查或检查完成状态的检查记录！' , '提醒');
									}
							    });
					  }
				  }
			});
		}
		else
		{
			_message('当前不是预约或取消预约状态，无法删除！' , '提醒');
		}	
	}
	else
	{
		_message('请选择一条数据！' , '提醒');
	}
}

/**
 * 修改预约信息
 * @returns
 */
function modifyInfo() {
	var rows = $("#dg_search_sch").datagrid("getSelections");
	if (rows != null && rows.length == 1) {
		var row = rows[0];
		if (row.status==myCache.StudyOrderStatus.scheduled||row.status==myCache.StudyOrderStatus.cancel_the_appointment) {
			if($("#patientid_reg").textbox("getValue")!=""||$("#studyid_reg").textbox("getValue")!=""||$("#admissionid_reg").textbox("getValue")!=""){
				$.messager.confirm({
	    			title: '提示',
	    			ok: '是',
	    			cancel: '否',
	    			border:'thin',
	    			msg: '当前有数据正在预约录入中，是否放弃当前的预约信息？',
	    			fn: function(r) {
	    				if (r) {
	    					setValue_Modify_sch(row);
	    				}
	    			}
	    		});
			} else {
				setValue_Modify_sch(row);
			}
		} else {
			_message('当前不是预约状态，无法修改！' , '提醒');
		}
	} else {
		_message('请选择一条数据！' , '提醒');
	}
}

/**
 *  预约修改-将预约的信息填充到from表单中
 * @param row  
 * @returns
 */
function setValue_Modify_sch(row) {
	console.log(row);
	//patient 主键
	$('#patientkid_reg').val(row.patientpkid);
	//admission 主键
	$('#admissionkid_reg').val(row.admissionpkid);
	//studyorder 主键
	$('#studyorderid_reg').val(row.studyorderpkid);
	
	$("#scheduleForm").form('load', row);
    
    $("#dept_reg").combobox("loadData",[{
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

    handleSelect_gettime(row.appointmenttime);
    if(row.appointmenttime){
	    var temp1=row.appointmenttime.split(" ");
	    var temp2=temp1[0].split("-");
	    var time=temp1[1].split(":");
		$('#calendar_sch').calendar('moveTo',new Date(temp2[0],temp2[1]-1,temp2[2]));
    }
	$('#tabs_div_sch').tabs('select',1);
}

/**
 * 常用查询条件填充
 */
function fillSearchParams_sch() {
	var row=$('#myfilterlist_schedule').datalist('getSelected');
	console.log(row)
	$("#searchForm_schedule").form('load',row);
//	$('#appmodality').combobox('setValue',row.modality); // 检查类型
//	$("#appstudyid").textbox("setValue",row.study_id); // 检查编号
//	$("#apppatientid").textbox("setValue",row.patientid); // 病人编号
//	$("#appcardno").textbox("setValue",row.cardno); // 卡号
//	$("#apppatientname").textbox("setValue",row.patient_name); // 姓名 
//	$("#appinno").textbox("setValue",row.inno); // 住院号
//	$("#appoutno").textbox("setValue",row.outno); // 门诊号
//	$("#apporderStatus").textbox("setValue",row.orderstatus); // 检查状态
//	
//	$('#appdate').val("");
//	$('#datefrom').datebox('setValue', row.start_time);
//	$('#dateto').datebox('setValue', row.stop_time);
	if (row.appdate == "T") {
		$('#today').linkbutton('select');
		$("#appdate").val("T");
	} else if (row.appdate == "TO") {
		$('#tomorrow').linkbutton('select');
		$("#appdate").val("TO");
	} else if (row.appdate == "TDAT") {
		$('#aftertomorrow').linkbutton('select');
		$("#appdate").val("TDAT");
	} else if (row.appdate == "Y") {
		$('#aftertomorrow').linkbutton('select');
		$("#appdate").val("Y");
	}  else if (row.appdate == "FD") {
		$('#aftertomorrow').linkbutton('select');
		$("#appdate").val("FD");
	}  else if (row.appdate == "W") {
		$('#aftertomorrow').linkbutton('select');
		$("#appdate").val("W");
	}  else if (row.appdate == "M") {
		$('#aftertomorrow').linkbutton('select');
		$("#appdate").val("M");
	}  else if (row.appdate == "T1") {
		$('#aftertomorrow').linkbutton('select');
		$("#appdate").val("T1");
	}  else if (row.appdate == "TM") {
		$('#aftertomorrow').linkbutton('select');
		$("#appdate").val("TM");
	}  else if (row.appdate == "FT") {
		$('#aftertomorrow').linkbutton('select');
		$("#appdate").val("FT");
	}  else if (row.appdate == "LF") {
		$('#aftertomorrow').linkbutton('select');
		$("#appdate").val("LF");
	}  else if (row.appdate == "LW") {
		$('#aftertomorrow').linkbutton('select');
		$("#appdate").val("LW");
	}   else if (row.appdate == "LM") {
		$('#aftertomorrow').linkbutton('select');
		$("#appdate").val("LM");
	}   else {
		$('#today').linkbutton({selected:false});
		$('#tomorrow').linkbutton({selected:false});
		$('#aftertomorrow').linkbutton({selected:false});
		$('#appdate').val('');
	}
	searchSchstudy();
}

//清空查询条件
function clearSearch(){
	$('#myfilterlist_schedule').datalist('clearSelections');
	$('#searchForm_schedule').form('clear');
	$('#datetype').combobox('setValue',$('#datetype').combobox('getData')[0].value);
	$('#today').linkbutton({selected:false});
	$('#tomorrow').linkbutton({selected:false});
	$('#aftertomorrow').linkbutton({selected:false});
}

//预约转登记
function sch_to_reg() {
	var row = $("#dg_search_sch").datagrid("getSelections");
	if (row != null) {
		var orderids = "";
		for (var i = 0; i < row.length; i++) {
			if (row[i].status != myCache.StudyOrderStatus.scheduled) {
				_message('检查编号为 ' + row[i].studyid + ' 的病人检查状态不是预约状态，无法转登记！' , '提醒');
			} else {
				orderids = orderids + row[i].studyorderpkid + ",";
			}
		}
		if (orderids != "") {
			$.messager.confirm({title: '确认',border:'thin',msg: '是否将选中的预约申请转为登记？',
				fn: function(r) {
					if (r) {
						getJSON(window.localStorage.ctx+"/schedule/sch_to_reg",
							{
								orderids : orderids
							},function(data){
								var json=validationData(data);
								if (json.code==0) {
									refreshSearchSchstudy();
									_message('转登记成功！' , '成功');
						    	} else {
						    		_message('转登记失败，请检查预约时间是否为今天，只有当天时间才能转预约。如果问题依然存在，请联系系统管理员！' , '提醒');
						    	}
							});
					}
				}
			});
		}
	} else {
		_message('请选择需要转登记的数据！' , '提醒');
	}
}

//检查流程
function toProcess_sch() {
	var rows = $("#dg_search_sch").datagrid("getSelections");
	if (rows != null && rows.length == 1) {
		var row = rows[0];
		process(row.studyorderpkid, "schedule");
	} else {
		_message('请选择一条数据！' , '提醒');
	}
}

//申请单
function toApply_sch() {
	var rows = $("#dg_search_sch").datagrid("getSelections");
	if (rows != null && rows.length == 1) {
		var row = rows[0];
		apply(row.studyorderpkid, 'schedule');
	} else {
		_message('请选择一条数据' , '提醒');
	}
}

function appointtime_formatter(value,row,index) {
	if (value) {
		return (new Date(value)).format("yyyy-MM-dd hh:mm");
	} else {
		return "";
	}
}

/**
 *  预约列表中，序号列和预约日期 的颜色显示
 * @param val
 * @param row
 * @param index
 * @returns
 */
function appointtime_styler(value, row, index) {
	var color = myCache.status_color['0005_'+myCache.StudyOrderStatus.scheduled];
	if (color && color == "#ffffff") {
		return "<span style='color: "+color+"'>" + value + "</span>"
	} else if (color) {
		return 'background-color:'+color+';';//color:red;
	} else {
		return "";
	}
}

/**
 *  预约列表中，检查状态列 的颜色显示
 * @param val
 * @param row
 * @param index
 * @returns
 */
function columeStyler_orderstatus_sch(value, row, index) {
	var color = myCache.status_color['0005_'+row.status];
	if (color && color == "#ffffff") {
		return "<span style='color: "+color+"'>" + value + "</span>"
	} else if (color) {
		return 'background-color:'+color+';';//color:red;
	} else {
		return '';
	}
}

/**
 * 查询
 * @param value 搜索的值
 * @param name  搜索是否需要关联左边的查询
 * @returns
 */
function quickSearch_sch(value, name) {
	value = $.trim(value).replace(/[\-\_\,\!\|\~\`\(\)\#\$\%\^\&\{\}\:\;\"\L\<\>\?]/g, '');
	if (value === null || value === "" || value === undefined) {
		_message('请输入要查询的数据！' , '提醒');
	} else {
		searchForm_flag_sch = "quicksearch";
		if(nowflag_sch){
			searchSchstudy(nowPageNumber_sch, nowPageSize_sch, value, name);
		}else{
			searchSchstudy(null, null, value,name);
		}
	}
};


//打印预约单
function printReservation(){
	var rows = $("#dg_search_sch").datagrid("getSelections");
	if (rows != null && rows.length ==1) {
		var row = rows[0];
		copiesAndPrintReservation(row.studyorderpkid);
	} else {
		_message('请选择一条数据' , '提醒');
	}
}

function copiesAndPrintReservation(orderid){
	$('#copies_reservation').combobox('setValue',$("#copiesres").val()?$("#copiesres").val():1);
	$('#printDlg_res').dialog({
		title:'打印张数选择',
		border:'thin',
		modal: true,
		buttons:[{
			text:'打印',
			width:63,
			handler:function(){
				var projecturl = $("#projecturl_res").val();
				var printername = $("#printername1_res").val();
				var copies = $("#copies_reservation").val()?$("#copies_reservation").val():1;
				var a="reporttool:-c print -t "+printername+" -w barwidth -h barheight -b "+copies+" -l "+projecturl+" -m orderid="+orderid+"&printTempType=3";//printTempType的取值从后端枚举类PrintTemplateType.SCH_TEMPLATE
				urlFlag = true;
				if(electron_enable()){
					electron_print(a);
				} else{
					$("#print_res")[0].setAttribute('href',a);
					$("#print_res")[0].click();
				}
				$("#copiesres").val(copies);
				$('#printDlg_res').dialog('close');
			}
		},{
			text:'取消',
			width:63,
			handler:function(){$('#printDlg_res').dialog('close');}
		}]
	});
	$('#printDlg_res').dialog('open');
}

//预约病人签到
function signin(){
	var rows = $("#dg_search_sch").datagrid("getSelections");
	if (rows != null && rows.length == 1) {
		var row = rows[0];
		if (row.status != myCache.StudyOrderStatus.scheduled) {
			_message('检查编号为 ' + row.studyid + ' 的病人检查状态不是预约状态，无法签到！' , '提醒');
			return;
		}
		$.messager.confirm({title: '确认',border:'thin',msg: '是否将选中的预约数据签到？',
			fn: function(r) {
				if (r) {
					getJSON(window.localStorage.ctx+"/schedule/signin",
						{
							orderid : row.studyorderpkid
						},function(data){
							var json=validationDataAll(data);
							if (json.code==0 && json.data=="1") {
								refreshSearchSchstudy();
								_message('签到成功！' , '成功');
					    	} else if(json.code==0 && json.data=="3"){
					    		_message('签到失败，请检查预约时间是否为今天，只有当天时间才能签到。' , '提醒');
					    	} else {
					    		_message('签到失败,请重试。如果问题依然存在，请联系系统管理员！' , '失败');
					    	}
						});
				}
			}
		});
	} else {
		_message('请选择一条需要签到的数据！' , '提醒');
	}
}

//预约病人提前签到
function advanceSignin(){
	var rows = $("#dg_search_sch").datagrid("getSelections");
	if (rows != null && rows.length == 1) {
		var row = rows[0];
		if (row.status != myCache.StudyOrderStatus.scheduled) {
			_message('检查编号为 ' + row.studyid + ' 的病人检查状态不是预约状态，无法签到！' , '提醒');
			return;
		}
		$.messager.confirm({title: '确认',border:'thin',msg: '是否将选中的预约数据签到？',
			fn: function(r) {
				if (r) {
					getJSON(window.localStorage.ctx+"/schedule/advanceSignin",
						{
							orderid : row.studyorderpkid
						},function(data){
							var json=validationDataAll(data);
							if (json.code==0 && json.data=="1") {
								refreshSearchSchstudy();
								_message('签到成功！' , '成功');
					    	} else {
					    		_message('签到失败,请重试。如果问题依然存在，请联系系统管理员！' , '失败');
					    	}
						});
				}
			}
		});
	} else {
		_message('请选择一条需要签到的数据！' , '提醒');
	}
}


//初始化申请页面
function initApplicationView_sch(){
	
}

//提取申请单信息
function extractApplication_sch(){
	
}

function timeSelect(){
	var datetype=$("#datetype").combobox("getValue");
	if(datetype=="appointmenttime"){
		$("#panel1").panel("close");
		$("#panel2").panel("close");
		$("#panel3").panel("open");
		$("#panel4").panel("open");
	} 
	if(datetype=="arrivedtime"){
		$("#panel1").panel("open");
		$("#panel2").panel("open");
		$("#panel3").panel("close");
		$("#panel4").panel("close");
	}
	if(datetype=="studytime"){
		$("#panel1").panel("open");
		$("#panel2").panel("open");
		$("#panel3").panel("close");
		$("#panel4").panel("close");
	}
}

//取走报告
function takeAwayReport(){
	var row = $("#dg_search_sch").datagrid("getSelected");
	if (row) {
		if (row.reportid && row.reportstatusdisplaycode == myCache.ReportStatus.FinalResults) {
			getJSON(window.localStorage.ctx+"/schedule/takeAwayReport",
					{
				        reportid : row.reportid
				    },
				    function(data){
						var result=validationDataAll(data);
						if(result.code==0){
							searchSchstudy();
							_message('取走报告成功！', '成功');
						}
						else{
							_message('保存失败请重试，如果问题依然存在请联系系统管理员！', '保存失败');
						}
				    }
				);
		} else {
			_message('请选择已审核报告的数据' , '提醒');
		}
	} else {
		_message('请选择一条数据' , '提醒');
	}
}

//取走胶片
function takeAwayFilm(){
	var row = $("#dg_search_sch").datagrid("getSelected");
	if (row) {
		if (row.studyorderpkid && row.status==myCache.StudyOrderStatus.completed) {
			getJSON(window.localStorage.ctx+"/schedule/takeAwayFilm",
					{
				        orderid : row.studyorderpkid
				    },
				    function(data){
						var result=validationDataAll(data);
						if(result.code==0){
							searchSchstudy();
							_message('取走胶片成功！', '成功');
						}
						else{
							_message('保存失败请重试，如果问题依然存在请联系系统管理员！', '保存失败');
						}
				    }
				);
		} else {
			_message('请选择已完成检查的数据' , '提醒');
		}
	} else {
		_message('请选择一条数据' , '提醒');
	}
}
function setColumnForeColor_reg(value,row,index){
	if(value && (value=="已取报告" || value=="已取片")){
		return "<span style='color: #47B347'>" + value + "</span>";
	}else{
		return value;
	}
}

function gotoWork(){
	getJSON(window.localStorage.ctx+"/profile/gotoWork",
			{},
		    function(data){
				var result=validationDataAll(data);
				if(result.code==0){
					_message('上班打卡成功！！！', '提示');
				}
				else{
					_message('打卡失败请重试，如果问题依然存在请联系系统管理员！', '保存失败');
				}
		    }
		);
}
function gooffWork(){
	getJSON(window.localStorage.ctx+"/profile/gooffWork",
			{},
		    function(data){
				var result=validationDataAll(data);
				if(result.code==0){
					_message('下班打卡成功！！！', '提示');
				}
				else{
					_message('打卡失败请重试，如果问题依然存在请联系系统管理员！', '保存失败');
				}
		    }
		);
}

function openCheckWorkAttendance(){
	$('#common_dialog').dialog({
		title : '考勤记录',
		width : 700,
		height : 500,
		resizable: false,
		minimizable: false,
		maximizable: false,
		border: 'thin',
		href : window.localStorage.ctx+'/profile/toCheckWorkAttendance',
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
function findCheckWorkAttendance(){
	var name=$("#name").textbox("getValue");
	var sdate=$("#sdate").datebox("getValue");
	var edate=$("#edate").datebox("getValue");
	if(!name&&!sdate&&!edate){
		_message('请选择要查询的人员或时间范围！', '提示');
		return;
	}
	getJSON(window.localStorage.ctx+"/profile/findCheckWorkAttendance",
			{
				name:name,
				sdate:sdate,
				edate:edate
			},
		    function(data){
				var json=validationData(data);
				 $("#checkWorkAttendancedg").datagrid("loadData", json);	
		    }
		);
}

function cleanStudyordersForm(){
	$("#studyorders_Form").form("clear");
}

function columeStyler_reportstatus_wl(value,row,index){
//	console.log("reportstatus:"+row.reportstatusdisplaycode);
	var reportstatusdisplay = row.reportstatusdisplaycode;
	var color=myCache.status_color['0007_'+reportstatusdisplay];
	if(color && color == "#ffffff"){
		return "<span style='color: "+color+"'>" + value + "</span>"
	}
	else if(color){
		return 'background-color:'+color+';';//color:red;
	}
	else{
		return '';
	}
}