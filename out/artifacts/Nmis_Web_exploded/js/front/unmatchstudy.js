
var default_page_match=1;
var default_pageSize_match=20;

var nowPageNumber_match = 1;
var nowPageSize_match = 50;
var nowflag_match = false;

//页面初始化
function initUnmatchstudy(){
	var p = $('#unmatchstudydg_match').datagrid('getPager'); 
    $(p).pagination({ 
        pageSize: default_pageSize_match,//每页显示的记录条数，默认为20 
        pageNumber:default_page_match,
        pageList: [10,20,30,50],//可以设置每页记录条数的列表 
        //showRefresh:false,
        beforePageText: $.i18n.prop('wl.beforepagetext'),//页数文本框前显示的汉字 
        afterPageText: $.i18n.prop('wl.afterpagetext'),
        displayMsg: $.i18n.prop('wl.displaymsg'),
        onSelectPage:function(pageNumber, pageSize){
        	$(this).pagination('loading');
    		$('#progress_dlg').dialog('open');
    		searchStudy_match(pageNumber, pageSize);
    		$(this).pagination('loaded');
    		$('#progress_dlg').dialog('close');
    	}
    });
	$('#searchForm_match').form({
		url: window.localStorage.ctx+"/register/findUnmatchstudy?page="+ default_page +"&rows="+ default_pageSize,
		onSubmit : function() {
			var datefrom=	$('#datefrom_match').datebox('getValue');  
		    var dateto=	$('#dateto_match').datebox('getValue');  
			if(datefrom != '' && !validator_reg(datefrom)){
				_message($.i18n.prop('filter.starttimeformaterror') , $.i18n.prop('error'));
				return false;
			}
			if(dateto != '' && !validator_reg(dateto)){
				_message($.i18n.prop('filter.endtimeformaterror') , $.i18n.prop('error'));
				return false;
			}
			$('#progress_dlg').dialog('open');
		},
		success:function(data){
			$('#progress_dlg').dialog('close');
		    if(!data.code){
	    		$('#unmatchstudydg_match').datagrid('loadData', validationData(data));
	    		if(nowflag_match){
	 				var p = $('#unmatchstudydg_match').datagrid('getPager'); 
	 				 $(p).pagination({ 
	 					 pageNumber:nowPageNumber_match,
	 				 });
	 				 nowflag_match = false;
	 			} 
	    	}
	    }
		
	});
	searchStudy_match();
}


//查询
function searchStudy_match(pageNumber, pageSize){
	if (!pageNumber) {
		pageNumber = default_page_match;
		$('#unmatchstudydg_match').datagrid('getPager').pagination({ 
			pageSize : default_pageSize_match,//每页显示的记录条数，默认为20 
			pageNumber : default_page_match
		});
	}
	if (!pageSize) {
		pageSize = default_pageSize_match;
	}
	$('#searchForm_match').form('submit', {url:window.localStorage.ctx+"/register/findUnmatchstudy?page="+ pageNumber +"&rows="+ pageSize});
}

//刷新查询结果，并指定页码
function refreshsearchStudy_match(){
	var options = $("#unmatchstudydg_match" ).datagrid("getPager" ).data("pagination" ).options;
	nowPageNumber_match = options.pageNumber;
	nowPageSize_match = options.pageSize;
	nowflag_match = true;
	searchStudy_match(nowPageNumber_match, nowPageSize_match, "", "");//即时更新查询到的数据
}

/**
 *  查询时，先进行校验
 * @returns
 */
function checkSearch_match() {
	var checkResult = true;
	// T：今天；Y：昨天；TD：近三天；FD：近五天， W：近一周， TM：近三个月
	var appdate = $('#appdate_match').val();
	var datefrom = $('#datefrom_match').datebox('getValue'); // 开始时间
	var dateto = $('#dateto_match').datebox('getValue');  // 结束时间
	var appstudyid = $("#accessionnumber_match").textbox("getValue");  // 检查号
	var apppatientid = $('#patientid_match').textbox('getValue'); // 病人编号
	var apppatientname = $("#patientname_match").textbox("getValue"); // 姓名
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
	return checkResult;
}

//清空查询条件
function clearSearch_match(){
	$('#searchForm_match').form('clear');
	$('#datetype_match').combobox('setValue', $('#datetype_match').combobox('getData')[0].value);//时间类型默认选中第一个
	$('#today_match').linkbutton({selected:false});
	$('#yesterday_match').linkbutton({selected:false});
	$('#threeday_match').linkbutton({selected:false});
	$('#fiveday_match').linkbutton({selected:false});
	$('#week_match').linkbutton({selected:false});
	$('#month_match').linkbutton({selected:false});
}

//登记前验证未匹配信息是否正确
function beforeToRegister_match(){
	var row=$("#unmatchstudydg_match").datagrid("getSelected");
	if(row!=null){
		if(row.matchflag==0){
			getJSON(window.localStorage.ctx+"/register/checkUnmatchInfo",
					{
						patientid : row.patientid,
						accessionnumber : row.accessionnumber
					},
					function(json){
						var data = validationDataAll(json);
						if(data.code==0){
							if(data.data == "1"){
								toRegister_match(row);
							}else if(data.data == "2"){
								_message('当前数据信息状态已改变，请刷新后重试！' , '提醒');
							}else if(data.data == "3"){
								_message('存在检查号冲突！' , '提醒');
							}
						}else{
							_message('请求数据失败！' , '错误');
						}	
					});
		}else{
			_message('本条数据已登记成功！' , '提醒');
		}
		
	}else{
		_message('请选择一条数据！' , '提醒');
	}
}

//登记
function toRegister_match(row){
	if($("#patientid_reg").textbox("getValue") != "" || $("#studyid_reg").textbox("getValue") != ""
			|| $("#admissionid_reg").textbox("getValue") != "")
	{
		$.messager.confirm({
			title:$.i18n.prop('confirm'),
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '正在登记录入中，是否放弃当前的登记信息？',
			fn: function(r){
				if (r){
					cancelSave();
					setValue_match(row);
				}
			}
		});
	}
	else
	{
		cancelSave();
		setValue_match(row);
	}
}

//登记界面填充选中的数据
function setValue_match(row){
	$("#patientid_reg").textbox("setValue",row.patientid);
	
	var patientname = row.patientname.split('^');
	$("#patientname_reg").textbox("setValue",patientname[0]+patientname[1]);
	
    $("#birthdate_reg").datebox("setValue",row.birthdate);
    $("#sex_reg").combobox('select', row.sex);
	var str=$("#birthdate_reg").datebox("getValue");
	var arr=str.split("-");
	var birthdate=new Date(arr[0],arr[1]-1,arr[2]);
	getAgeFromBirthdate(birthdate);
	
	$("#studyid_reg").textbox("setValue",row.studyid);
	$("#studyorder_status_reg").val(row.status);
    //$("#modality_reg").combobox('select', row.modality);
	$('#unmatchkid_reg').val(row.id);

    $("#tabs_div_reg").tabs("select",0);
    
  //病人id一致
    getJSON(window.localStorage.ctx+"/register/checkSameName",
		{
			patientid : row.patientid
		},
		function(data) {
			var json=validationData(data);
			if(json.length>0){
				openSameNameDlg(json);
			}
		});
}

//更改检查号
function correctAccessionNo_match(){
	var row=$("#unmatchstudydg_match").datagrid("getSelected");
	if(row!=null){
		$('#correctAccessNumDlg_match').dialog({
			title:'更改检查号',
			width:'260px',
			height:'120px',
			border:'thin',
			modal: true,
			buttons:[{
				text:'保存',
				width:63,
				handler:function(){
					if($('#accessNum_match').textbox('getValue')){
						getJSON(window.localStorage.ctx+"/register/correctAccessionNo",
								{
									id : row.id,
									accessionnumber : $('#accessNum_match').textbox('getValue')
								},
								function(data){
									var json = validationDataAll(data);
									if(json.code == 0){
										searchStudy_match(null, null);
									}
								});
								$('#accessNum_match').textbox('setValue',"");
								$('#correctAccessNumDlg_match').dialog('close');
					}else{
						_message('请输入检查号！' , '提醒');
					}
					
				}
			},{
				text:'取消',
				width:63,
				handler:function(){$('#correctAccessNumDlg_match').dialog('close');}
			}]
		});
		$('#correctAccessNumDlg_match').dialog('open');
	}else{
		_message('请选择一条数据！' , '提醒');
	}
}

function deleteUnmatchstudy_match(){
	var row=$("#unmatchstudydg_match").datagrid("getSelected");
	if(row){
		$.messager.confirm({title:$.i18n.prop('confirm'),ok: '是',cancel: '否',msg: '是否删除选中的检查？',border:'thin',
			fn: function(r){
				if(r){
					getJSON(window.localStorage.ctx+"/register/deleteUnmatchstudy",
						{
							id : row.id
						}, function(data){
							var json=validationData(data);
							if(json.code==0){
								searchStudy_match(null, null);
					    	}else{
					    		_message('删除失败请重试，如果问题依然存在，请联系系统管理员！' , '提醒');
					    	}
						});
				}
			}
		});
	}else{
		_message('请选择一条数据！' , '提醒');
	}
}