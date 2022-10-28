//var wsurl="";
//var websocket = null;  
//var lockReconnect = false;  //避免ws重复连接
var myCache;
var closeReportFlag = true;
$(function() {
	myCache=JSON.parse(window.localStorage.myCache);
	setTimeout(function () {
		var session_over={
			type : "sessionover",
			exec : function(data){
				console.log("sessionover exec "+data);
				$.messager.alert({
	        		title: '请重新登录',
	        		border: 'thin',closable:false,
	        		icon: 'warning',zIndex:99999,
	        		msg: '你的会话已过期，请重新登录！',
	        		fn: function(){
	        			//if (r){
	        				logout();
	        			//}
	        		}
	        	});
			}
		}
		//console.log(websocket)
		if(websocket){
			initService_WS(session_over.type,session_over);
		}
	}, 1000);
});

function validator_dis (value) { 
    //格式yyyy-MM-dd或yyyy-M-d
       return/^(?:(?!0000)[0-9]{4}([-]?)(?:(?:0?[1-9]|1[0-2])\1(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])\1(?:29|30)|(?:0?[13578]|1[02])\1(?:31))|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([-]?)0?2\2(?:29))$/i.test(value); 
}
var default_page=1;
var default_pageSize=200;
var searchForm_flag="searchForm";
function initWorklist(){
//	$("#page").val(page);
//	$("#pageSize").val(pageSize);
	//设置分页控件 
    var p = $('#dg').datagrid('getPager'); 
    $(p).pagination({ 
        pageSize: default_pageSize,//每页显示的记录条数
        pageNumber:default_page,
        pageList: [50,100,200],//可以设置每页记录条数的列表 
        //showRefresh:false,
        beforePageText: $.i18n.prop('wl.beforepagetext'),//页数文本框前显示的汉字 
        afterPageText: $.i18n.prop('wl.afterpagetext'),
        displayMsg: $.i18n.prop('wl.displaymsg'),
        onSelectPage:function(pageNumber, pageSize){
//        	page=pageNumber;
//        	pageSize=pageSize;
        	$("#page").val(pageNumber);
        	$("#pageSize").val(pageSize);
        	$(this).pagination('loading');
    		$('#progress_dlg').dialog('open');
    		if(searchForm_flag=="searchForm"){
    			sumbitSearchForm(pageNumber, pageSize, null,null);
    		} else if(searchForm_flag=="quicksearch"){
    			sumbitSearchForm(pageNumber,pageSize,$('#quicksearch-input').searchbox('getValue'),$('#quicksearch-input').searchbox('getName'));
    		}
    		$(this).pagination('loaded');
    	}
    });
}

function sumbitSearchForm(pageNum, pageSi, quicksearchcontent,quicksearchname) {
	if (!pageNum) {
		pageNum = default_page;
		$('#dg').datagrid('getPager').pagination({ 
			pageSize: default_pageSize,//每页显示的记录条数
			pageNumber: default_page
		});
	}
	if (!pageSi) {
		pageSi = default_pageSize;
	}
	$("#searchForm_distribution").form('submit', {
		url:window.localStorage.ctx+"/distribution/getDataAll?page="+ pageNum +"&rows="+ pageSi +
		"&quicksearchcontent=" + (quicksearchcontent||"")+"&quicksearchname="+(quicksearchname||"")
	});
}


function initFieldEvent() {
	$('#searchForm_distribution').form({
		url : window.localStorage.ctx+"/distribution/getDataAll?page="+ default_page +"&rows="+ default_pageSize,
		onSubmit : function() {
			if(checkSearch()){
				$('#progress_dlg').dialog('open');
				return true;
			} else {
				return false;
			}
		},
		success : function(data) {
				$('#progress_dlg').dialog('close');
				$("#dg").datagrid("loadData", validationData(data)); // hide
//				if (row) {
//					var rows = $("#dg").datagrid("getRows");
//					if (rows) {
//						for (var i = 0; i < rows.length; i++) {
//							if (rows[i].id == row.id) {
//								$("#dg").datagrid("selectRow", i);
//								break;
//							}
//						}
//					}
//				}
		}
	});
	

	$('#studyid').textbox('textbox').bind('keydown', function(e){
		if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
			searchStudyWS();
		}
	});
	$('#patientname').textbox('textbox').bind('keydown', function(e){
		if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
			searchStudyWS();
		}
	});
	$('#patientid').textbox('textbox').bind('keydown', function(e){
		if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
			searchStudyWS();
		}
	});
//	$('#outno').textbox('textbox').bind('keydown', function(e){
//		if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
//			searchStudyWS();
//		}
//	});
//	$('#inno').textbox('textbox').bind('keydown', function(e){
//		if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
//			searchStudyWS();
//		}
//	});
	
	$('#reportphysician_name_ws').textbox('textbox').bind('keydown', function(e){
		if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
			searchStudyWS();
		}
	});
	$('#auditphysician_name_ws').textbox('textbox').bind('keydown', function(e){
		if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
			searchStudyWS();
		}
	});
	
	sumbitSearchForm();
}

/**
 * 条件清除
 * @returns
 */
function clearManage(){
	$('#myfilterlist_distribution').datalist('clearSelections');
	$('#searchForm_distribution').form('clear');
	$('#datetype').combobox('setValue',$('#datetype').combobox('getData')[0].value);
	/*// 时间选择 D今天(today) E昨天(yesterday) K不限(all);*/
	$('#today').linkbutton({selected:false});
	$('#yesterday').linkbutton({selected:false});
	$('#threeday').linkbutton({selected:false});
	$('#fiveday').linkbutton({selected:false});
	$('#week').linkbutton({selected:false});
	$('#month').linkbutton({selected:false});
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
	var modality = $('#modality').combobox('getValue'); // 设备类型
	// 报告状态 O未写 P未审 R已审 G我的报告 K 不限
	var reportStatus = $('#reportStatus').combobox('getValue');
	
	// T：今天；Y：昨天；TD：近三天；FD：近五天， W：近一周， TM：近三个月
	var appdate = $('#appdate').val();
	
	var datefrom = $('#datefrom_dis').datebox('getValue'); // 开始时间
	var dateto = $('#dateto_dis').datebox('getValue');  // 结束时间
	var patientsource = $('#patientsource').combobox('getValue'); // 病人来源
	
	var studyid = $("#studyid").textbox("getValue");  // 检查号
	var patientid = $('#patientid').textbox('getValue'); // 病人编号
	var patientname = $("#patientname").textbox("getValue"); // 姓名
	var reportphysician_name_ws = $('#reportphysician_name_ws').textbox('getValue'); // 报告医生
	var auditphysician_name_ws = $('#auditphysician_name_ws').textbox('getValue'); // 审核医生
	
	if (studyid == "" && patientid == "" && patientname == "" && datefrom == "" && dateto == "" && appdate == "") {
		checkResult = false;
		_message('请选择需要查询的具体一个时间段','提醒');
	}
	if (datefrom != "" && dateto == "") {
		checkResult = false;
		_message('结束时间不能为空','提醒');
	}
	if (datefrom == "" && dateto != "") {
		checkResult = false;
		_message('开始时间不能为空','提醒');
	}
	if (datefrom != "" && dateto != "") {
		var date1 = new Date(datefrom);
	    var date2 = new Date(dateto);
	    if (date1.getTime() > date2.getTime()) {
	    	checkResult = false;
	    	_message('开始时间不能大于结束时间','提醒');
	    }
	}
	return checkResult;
}

/**
 * 开始查询
 */

function searchStudyWS(quickSearchContent,quickSearchName) {
	if (checkSearch()) {
		sumbitSearchForm(null, null, quickSearchContent,quickSearchName);
	}
}

//刷新
function refresh() {
	console.log("searchForm_flag="+searchForm_flag);
	if(searchForm_flag=="searchForm"){
		sumbitSearchForm();
	}else if(searchForm_flag=="quicksearch"){
		sumbitSearchForm(null,null,$('#quicksearch-input').searchbox('getValue'),$('#quicksearch-input').searchbox('getName'));
	}
}

//去除空格
function trim(str){   
    return str.replace(/^(\s|\u00A0)+/,'').replace(/(\s|\u00A0)+$/,'');   
}

/**
 * 常用查询条件填充
 * 
 * @returns
 */
function fillParams() {
	var row=$('#myfilterlist_distribution').datalist('getSelected');
	console.log(row);
	if(!row)return;
	//form中存在多谢下拉框时，如果对应值为null,在加载前必须置为'',不然会报错。
	$("#searchForm_distribution").form('load',row);
	// 时间选择T今天(today) Y昨天(yesterday) 
	if (row.appdate == "T") {
		$('#today').linkbutton('select');
		$("#appdate").val("T");
	} else if (row.appdate == "Y") {
		$('#yesterday').linkbutton('select');
		$("#appdate").val("Y");
	} else if (row.appdate == "TD") {
		$('#threeday').linkbutton('select');
		$("#appdate").val("TD");
	}else if (row.appdate == "FD") {
		$('#fiveday').linkbutton('select');
		$("#appdate").val("FD");
	}else if (row.appdate == "W") {
		$('#week').linkbutton('select');
		$("#appdate").val("W");
	}else if (row.appdate == "TM") {
		$('#month').linkbutton('select');
		$("#appdate").val("TM");
	}else{
		$('#today').linkbutton({selected:false});
		$('#yesterday').linkbutton({selected:false});
		$('#threeday').linkbutton({selected:false});
		$('#fiveday').linkbutton({selected:false});
		$('#week').linkbutton({selected:false});
		$('#month').linkbutton({selected:false});
		$('#appdate').val('');
	}
	searchStudyWS();
}

/**
 * 打印界面
 * 
 * @param reportid
 * @returns
 */
function print(issr,fontSize) {
	var rows = $('#dg').datagrid('getSelections');
	if(rows.length == 1){
		if(rows[0].reportstatusdisplaycode != 20){		
			 window.open(window.localStorage.ctx+"/print/printReport?reportid="+rows[0].reportid +"&issr="+issr+"&fontSize="+fontSize);
		}
		else{
			$.messager.show({
				title : $.i18n.prop('error'),
				msg : "尚未创建报告，无法预览！",
				timeout : 3000,
				border:'thin',
				showType : 'slide'
			});
		}
	} else{
		$.messager.show({
			title : '提醒',
			msg : "请选择一份报告预览！",
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
	}
}


/**
 * 打印界面
 * 
 * @param reportid
 * @returns
 */
function printHtml(reportid) {

	$.getJSON( window.localStorage.ctx+'/print/printHtml?reportid=' + reportid,function (res) {
//         if(res.code == 0){
//        	 window.open("/report/print?studyid=" + studyid+"&printtempname=" + printtempname);
//         }else{
//        	 $.messager.show({
//					title : '提示信息',
//					msg : res.message,
//					timeout : 3000,
//					border:'thin',
//					showType : 'slide'
//				});
//         }
     });
}

/**
 * 打印报告
 * @param projecturl
 * @param reportid
 * @returns
 */
function printReport(projecturl,reportid,ptemp,issr){
	if(!projecturl && !reportid){
		var row=$("#dg").datagrid("getSelected");
		if(row!=null && row.reportid){
			getJSON( window.localStorage.ctx+'/report/getPrintReportInfo',
					{
						reportid : row.reportid
					},
					function(json){
						closeReportFlag=false;
						urlFlag = true;
						var href="reporttool:-c print -t "+json.data.templatename+" -l "+json.data.projecturl+" -m reportid="+json.data.reportid+"&issr="+json.data.issr;
				    	$('#printReport_worklist')[0].setAttribute('href',href);
				    	$('#printReport_worklist')[0].click();
					});
		}else{
			$.messager.show({
		        title:'提醒',
		        msg:'请选择一条已有报告的数据',
		        timeout:3000,
		        border:'thin',
		        showType:'slide'
		    });
		}	
	}else{
		if($('#id_' + reportid).val()){
			closeReportFlag=false;
			urlFlag = true;
			var href="reporttool:-c print -t "+ptemp+" -l "+projecturl+" -m reportid="+$('#id_' + reportid).val()+"&issr="+issr;
	    	$("#printApp_"+reportid)[0].setAttribute('href',href);
	    	$("#printApp_"+reportid)[0].click();
		}
		else{
			$.messager.show({
				title : $.i18n.prop('error'),
				msg : "尚未创建报告，无法打印！",
				timeout : 3000,
				border:'thin',
				showType : 'slide'
			});
		}
		
	 }
}


//设置处理
function setUp(name){
//	var studyList =$("#studyidList").val();
//	if(studyList!==""){
//		var studyInfo = studyList.split(","); 
//		for(var i=0;i<studyInfo.length;i++){
//			sendAjax({
//				url:'report/cancelBlock',
//				type:'Post',
//				data:{studyid:studyInfo[i]},
//				success:function(data){
//					if(data.code==0){
//						$('#dg').datagrid('updateRow',{index:$('#dg').datagrid('getRowIndex',$("#dg").datagrid('getSelected')),row:{locking:'未锁定'}});
//						$('#dg').datagrid('updateRow',{index:$('#dg').datagrid('getRowIndex',$("#dg").datagrid('getSelected')),row:{lockingpeople:''}});
//					}else{
//						$.messager.show({
//							title : '提示信息',
//							msg : "取消锁定失败,请联系系统管理员",
//							timeout : 3000,
//							showType : 'slide'
//						});
//					}
//				}
//			})
//		}
//	}
	redirect(window.localStorage.ctx+'/admin',name,null);
	
}

function detailFormatter_wl_previewreport(index,row){
	return '<div class="ddv" style="padding:5px 0"></div>';
}

function priorityFormat(val, row, index){
	if(row.priority == 'F'){
		return '<div style="height: 18px; width: 18px; background-color: red; border-radius: 9px;"><div style="height: 9px; width: 9px; background: white; border-radius: 5px;position: relative;left: 4.5px; top: 4.5px;"></div></div>';
	}
}

function onExpandRow_wl_previewreport(index,row){
	
	if (row.reportid) {
		var ddv = $('#dg').datagrid('getRowDetail',index).find('div.ddv');
	    ddv.panel({
	        border:false,
	        cache:false,
	        href:window.localStorage.ctx+"/worklist/previewReport?reportid="+row.reportid,
	        onLoad:function(){
	            $('#dg').datagrid('fixDetailRowHeight',index);
	        }
	    });
	    $('#dg').datagrid('fixDetailRowHeight',index);
    
	}
}

function rowStyler_lockingpeople_wl(value,row,index){
	if (row.lockingpeople){
        return 'background-color:#6293BB;color:#fff;font-weight:bold;';
    }
}
function columeStyler_orderstatus_wl(value,row,index){
	var color=myCache.status_color['0005_'+row.status];
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

function columeStyler_studyid(val,row,index){
	if (row.patientsource == 'E'){
        return 'background-color:#F00;font-weight:bold;';
    }
}

function printcount_formatter_wl(value,row,index){
	if(value==0){
		return "未打印";
	}
	else{
		return "已打印";
	}
}



/**
 *  报告导出pdf
 * @param id
 * @returns
 */
function exportReport() {
	var row = $("#dg").datagrid("getSelected");
	if (row) {
		if (row.reportstatusdisplaycode+"" === myCache.ReportStatus.FinalResults) {
			// 导出pdf
			window.location.href = window.localStorage.ctx+"/worklist/exportReport?reportid=" + row.reportid 
		} else {
			_message('请选择已审核的报告','提醒');
		}
	} else {
		_message('请选择一条数据','提醒');
	}
}

/**
 *  批量导出pdf报表文件
 * @returns
 */
function batchExportReport() {
	// 1、根据查询条件获取 所有报告状态为 已审核 的数据
	var result = true;
	if (result) {
		result = false;
		$.ajax({
			//不同步的话，它导出的时候会有延迟，如果在延迟中再点击导出，它就会再导出一遍
	        async: false,
	        type: "POST",
	        url: window.localStorage.ctx + "/worklist/getBatchExportReport?reportstatus="+myCache.ReportStatus.FinalResults,
	        contentType : "application/x-www-form-urlencoded; charset=utf-8",
	        data:$("#searchForm_distribution").serialize(),
	        dataType: "json",
	        beforeSend: function() {
	        	$('#progress_dlg').dialog('open');
			},
	        success: function (res) {
	        	if (res[0].totalsize > 0) {
	        		if (res[0].totalsize < 500) {
	        			var ids = "";
		        		for (var i = 1; i <= res[0].totalsize; i++) {
							ids = ids + res[i].reportid + ",";
						}
		        		// 有可以导出的数据
		            	// 2、将数据返回给后台，后台进行导出pdf 操作
		            	window.location.href = window.localStorage.ctx+"/worklist/batchExportReport?ids=" + ids;
		            	result = true;
		            	var time = res[0].totalsize * 60;
		            	setTimeout(() => {
		            		$('#progress_dlg').dialog('close');
						}, time);
	        		} else {
	        			$.messager.show({
		        	        title:'提醒',
		        	        msg:'需要导出的数据量太大，请缩小需要导出pdf的时间段',
		        	        timeout:3000,
		        	        border:'thin',
		        	        showType:'slide'
		        	    });
		        		result = true;
	        		}
	        	} else {
	        		$.messager.show({
	        	        title:'提醒',
	        	        msg:'没有符合查询条件中可以导出的已审核数据',
	        	        timeout:3000,
	        	        border:'thin',
	        	        showType:'slide'
	        	    });
	        		result = true;
	        	}
	        },
	        error: function () {
	        	$.messager.show({
        	        title:'提醒',
        	        msg:'导出失败，请重试！！！',
        	        timeout:3000,
        	        border:'thin',
        	        showType:'slide'
        	    });
	        	result = true;
	        }
		})
	}
}


/**
 * 点击搜索
 * @param value  搜索的内容
 * @param name   全部/关联
 * @returns
 */
function quickSearch(value,name) {

	value = $.trim(value).replace(/[\-\_\,\!\|\~\`\(\)\#\$\%\^\&\{\}\:\;\"\L\<\>\?]/g, '');
	if (value === null || value === "" || value === undefined) {
		$.messager.show({
	        title:'提醒',
	        msg:'请输入要查询的数据！',
	        timeout:3000,
	        border:'thin',
	        showType:'slide'
	    });
	} else {
		searchForm_flag = "quicksearch";
		searchStudyWS(value,name);
	}
};

/**
 * 已审核报告批量打印
 */
function batchPrintReport(){
	var rows = $('#dg').datagrid("getSelections");
	if(rows.length == 0){
		_message('请选择要打印的已审核的报告！','提醒');
	} else{
		var uncheck = new Array();//存储已选的未审核报告的检查号
		var checkrid = new Array();
		var geturl;
		var checkallreportid ="";//所有已审核的reportid
		var alltemname ="";//所有已审核报告的模板名
		var allissr = "";
		//reportstatusdisplaycode报告状态字段     20为未写 31为已审核
		for(var i = 0; i < rows.length; i++){
			if(rows[i].reportstatusdisplaycode != 31){
				uncheck[i] = rows[i].studyorderstudyid;
			}	
		}
		if(uncheck.length>=1){
			var stringuncheck = '';
			for(var i = 0; i < uncheck.length; i++){
				stringuncheck = stringuncheck + uncheck[i] +',';
			}
			$.messager.show({
		        title:'提醒',
		        msg: stringuncheck+'报告未审核！',
		        timeout:4000,
		        border:'thin',
		        showType:'slide'
		    });
		}

		var count = 0;
		for(var i = 0; i<rows.length; i++){
			if(rows[i].reportstatusdisplaycode==31){
				console.log("i:"+i);
				checkrid[count++] = rows[i].reportid;
			}
		}
		for(var i = 0; i < checkrid.length; i++){
			checkallreportid = checkallreportid+','+checkrid[i];
		}
		
		if(checkrid.length>0){
			getJSON( window.localStorage.ctx+'/report/getAllPrintReportInfo?reportid='+checkrid,
					{},
					function(json){
						var templatename = json.data.templatename;
						var issr = json.data.issr;
						geturl = json.data.projecturl;
						for(var i = 0; i < templatename.length; i++){
							alltemname = alltemname +','+ templatename[i];
						}
						for(var j = 0; j < issr.length; j++){
							allissr = allissr +','+ issr[j];
						}
						
						closeReportFlag=false;
						urlFlag = true;
						var href="reporttool:-c checkReportPrint -d "+checkallreportid+" -l "+geturl+" -n "+$('#checkreportuserid_dis').val()+" -f "+alltemname+" -r "+allissr;
				    	$('#printReport_worklist')[0].setAttribute('href',href);
				    	$('#printReport_worklist')[0].click();
					});
		}
	}
	
};

/**
 * 监听快捷键事件
 * @returns
 */
//$(document).ready(
//	function() {
//		document.onkeydown = function() {
//			var oEvent = window.event;
//			if (oEvent.ctrlKey && oEvent.keyCode == 81) {
//				// 打开申请单, Ctrl + Q
//				openApplyForm();
//			} else if (oEvent.ctrlKey && oEvent.keyCode == 73) {
//				// 打开图像（p）, Ctrl + i;
//				openStudyImage('plazapara_worklist');
//			} else if (oEvent.ctrlKey && oEvent.altKey && oEvent.keyCode == 51) {
//				// 打开图像（v）, Ctrl + Alt + 3
//				openStudyImage('viapara_worklist');
//			}
//		}
//	}
//);

/**
 *  导出Excel
 * @returns
 */
function exportExcel() {
	// 固定的列
	var filesFrozen = $('#dg').datagrid('getColumnFields', true);
	// 截取掉前面两个多余的
	filesFrozen = filesFrozen.splice(2,filesFrozen.length);
	// 未固定的列
	var files = $('#dg').datagrid('getColumnFields');
	// 导出excel的标题
	var titles = [];
	var fields = filesFrozen.concat(files);
	fields.push("checkdesc_txt");
	fields.push("checkresult_txt");
	// 每列的宽度
	var widths = [];
	for (var i = 0; i < filesFrozen.length; i++) {
		var option = $("#dg").datagrid('getColumnOption', filesFrozen[i]);
		titles.push(option.title);
		widths.push(option.width);
	}
	for (var j = 0; j < files.length; j++) {
		var option = $("#dg").datagrid('getColumnOption', files[j]);
		titles.push(option.title);
		widths.push(option.width);
	}
	titles.push("检查所见");
	titles.push("检查结果");
	widths.push("100");
	widths.push("100");
	$("#searchForm_distribution").form('submit', {
		url:window.localStorage.ctx+"/distribution/exportExcel?reportstatus=31&titles=" + titles + "&fields=" + fields + "&widths=" + widths
	});
	$('#progress_dlg').dialog('close');
}

