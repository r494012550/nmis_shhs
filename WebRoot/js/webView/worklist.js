//var wsurl="";
//var websocket = null;  
//var lockReconnect = false;  //避免ws重复连接
var myCache;
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

function validator (value) { 
    //格式yyyy-MM-dd或yyyy-M-d
       return/^(?:(?!0000)[0-9]{4}([-]?)(?:(?:0?[1-9]|1[0-2])\1(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])\1(?:29|30)|(?:0?[13578]|1[02])\1(?:31))|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([-]?)0?2\2(?:29))$/i.test(value); 
}
var default_page=1;
var default_pageSize=20;
function initWorklist(){
//	$("#page").val(page);
//	$("#pageSize").val(pageSize);
	//设置分页控件 
    var p = $('#dg').datagrid('getPager'); 
    $(p).pagination({ 
        pageSize: default_pageSize,//每页显示的记录条数，默认为20 
        pageNumber:default_page,
        pageList: [10,20,30,50],//可以设置每页记录条数的列表 
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
    		sumbitSearchForm(pageNumber, pageSize);
    		$(this).pagination('loaded');
    	}
    });
    
}

function sumbitSearchForm(pageNum, pageSi) {
	if(!pageNum){
		pageNum=default_page;
		$('#dg').datagrid('getPager').pagination({ 
			pageSize: default_pageSize,//每页显示的记录条数，默认为20 
			pageNumber:default_page
		});
	}
	if(!pageSi){
		pageSi=default_pageSize;
	}
	$("#searchForm_webview").form('submit', {url:window.localStorage.ctx+"/webview/getDataAll?page="+ pageNum +"&rows="+ pageSi});
}


function initFieldEvent(){
	$('#searchForm_webview').form({
		url : window.localStorage.ctx+"/webview/getDataAll?page="+ default_page +"&rows="+ default_pageSize,
		onSubmit : function() {
//			if($("#page").val()!="")
//				default_page=$("#page").val()
//			if($("#pageSize").val()!="")
//				default_pageSize=$("#pageSize").val();
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
	$('#outno').textbox('textbox').bind('keydown', function(e){
		if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
			searchStudyWS();
		}
	});
	$('#inno').textbox('textbox').bind('keydown', function(e){
		if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
			searchStudyWS();
		}
	});
	
	sumbitSearchForm();
}

var TimeFn = null;
function clickDgRow(index, row) {
	clearTimeout(TimeFn);
	 TimeFn = setTimeout(function () {
         //....Other Operation
		 if(row.patientidfk){
				getJSON(window.localStorage.ctx+"/worklist/getRemarks",
						{
							patientidfk : row.patientidfk
						},
						function(data){
							var json=validationDataAll(data);
							if(json.code==0&&json.data){
								var reg = new RegExp(" ","g")
								var message=json.data.replace(reg,"<br/>");
								$.messager.show({
									title : $.i18n.prop('alert'),
									msg : message,
									border:'thin'
								});
							}
						});
			}
      }, 100);//延时时长设置           
}

/**
 * 条件清除
 * @returns
 */
function clearManage(){
	$('#myfilterlist_webview').datalist('clearSelections');
	$('#searchForm_webview').form('clear');
	$('#datetype').combobox('setValue',$('#datetype').combobox('getData')[0].value);
	
	/*// 时间选择 D今天(today) E昨天(yesterday) K不限(all)*/
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
//	var modality = $('#modality').combobox('getValue'); // 设备类型
	// 报告状态 O未写 P未审 R已审 G我的报告 K 不限
//	var reportStatus = $('#reportStatus').combobox('getValue');
	// T：今天；Y：昨天；TD：近三天；FD：近五天， W：近一周， TM：近三个月
	var appdate = $('#appdate').val();
	var datefrom = $('#datefrom').datebox('getValue'); // 开始时间
	var dateto = $('#dateto').datebox('getValue');  // 结束时间
//	var patientsource = $('#patientsource').combobox('getValue'); // 病人来源
//	var studyid = $("#studyid").textbox("getValue");  // 检查号
//	var patientid = $('#patientid').textbox('getValue'); // 病人编号
//	var patientname = $("#patientname").textbox("getValue"); // 姓名
//	var reportphysician_name_ws = $('#reportphysician_name_ws').textbox('getValue'); // 报告医生
//	var auditphysician_name_ws = $('#auditphysician_name_ws').textbox('getValue'); // 审核医生
	
	if (datefrom == "" && dateto == "" && appdate == "") {
		_message('请选择需要查询的具体一个时间段','提醒');
		return false;
	}
	if (datefrom == "" && dateto != "") {
		_message('开始时间不能为空','提醒');
		return false;
	}
	if (datefrom != "" && dateto == "") {
		_message('结束时间不能为空','提醒');
		return false;
	}
	
	if (datefrom != "" && dateto != "") {
		var date1 = new Date(datefrom);
	    var date2 = new Date(dateto);
	    if (date1.getTime() > date2.getTime()) {
	    	_message('开始时间不能大于结束时间','提醒');
	    	return false;
	    }
	}
	return checkResult;
}

/**
 * 开始查询
 */
function searchStudyWS() {
	sumbitSearchForm();
}

function getTabIndexByOrderid(reportid){
	var tabid="tab_"+reportid;
	var tabs=$('#tab').tabs('tabs');
	for(var i=1;i<tabs.length;i++){
		if(tabs[i].panel('options').id==tabid){
			return i;
		}
	}
	return null;
}

function openReport(srtemplateid,openimageflag) {
	clearTimeout(TimeFn);
	var row = $("#dg").datagrid("getSelected");
	console.log(row);
	if (row) {
		if(row.status!=myCache.StudyOrderStatus.completed){//判断检查状态是否为检查完成
			$.messager.show({title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.studywasnotcompleteandcannotopen'),timeout : 3000,border:'thin',showType : 'slide'});
			return;
		}
		var index=getTabIndexByOrderid(row.reportid);
		if (!index) {
			$('#progress_dlg').dialog('open');
			getJSON(window.localStorage.ctx+"/webview/openReport?reportid="+ row.reportid,null,function(json) {
				var reportdata = validationDataAll(json);
				console.log(reportdata);
				if(reportdata.code==0){
					//openImageAtOnce(row,openimageflag);
					/*********************************************************************************************************************/
					//$('#changeflag_'+row.id).val(false);
					if(reportdata.data.template_id==null||reportdata.data.template_id==0){
					/*******************打开普通报告*****************************************/
						addTab_NormalReport(row,reportdata);
						$("#reportcontent").html('');
					} else{
					/*******************打开结构化报告*****************************************/
						addTab_StructReport(row,reportdata,false);
					}
				}
				else{
					$.messager.show({title : $.i18n.prop('error'),msg :reportdata.message,timeout : 3000,border:'thin',showType : 'slide'});
				}
				$('#progress_dlg').dialog('close');
			});
			
		} else {
			$('#tab').tabs('select',index); 
		}
	} else {
		$.messager.show({title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.selectonestudy'),timeout : 3000,border:'thin',showType : 'slide'});
	}
}

function addTab_StructReport(row,reportdata,openwithsrtemp){

	$('#tab').tabs('add',{
        id: "tab_"+row.reportid,  
        title: row.patientname,
        closable: true ,
        onLoad : function(){
        	UM.delEditor('desc_'+ reportid + '_html');
			var ue_desc = UM.getEditor('desc_'+ reportid + '_html');
			ue_desc.ready(function() {
				// 设置编辑器的内容
				ue_desc.setContent(reportdata.data.checkdesc_html || '');
				if(reportdata.data.reportstatus==myCache.ReportStatus.FinalResults){
					ue_desc.setDisabled();
				}
				if($('#desc_fontsize_' + reportid).val()){
					$(ue_desc.container).find('.edui-body-container:first').addClass('_fontsize'+$('#desc_fontsize_' + reportid).val());
				}
				//ue_desc.setHeight(260);
				ue_desc.addListener('contentChange',function(){
        			$('#contentChangeflag_' + reportid).val(true);
            	});
			});
			UM.delEditor('result_'+ reportid + '_html');
			var ue_result = UM.getEditor('result_'+ reportid + '_html');
			ue_result.ready(function() {
				// 设置编辑器的内容
				ue_result.setContent(reportdata.data.checkresult_html || '');
				if(reportdata.data.reportstatus==myCache.ReportStatus.FinalResults){
					ue_result.setDisabled();
				}
				if($('#result_fontsize_' + reportid).val()){
					$(ue_result.container).find('.edui-body-container:first').addClass('_fontsize'+$('#result_fontsize_' + reportid).val());
				}
				//ue_result.setHeight(160);
				ue_result.addListener('contentChange',function(){
        			$('#contentChangeflag_' + reportid).val(true);
            	});	
			});

			changeReportBtnEnable(reportdata.data.reportstatus,reportid);
			if(reportdata.data.reportstatus==myCache.ReportStatus.FinalResults){
//				$("#audi_image_"+reportid).show();
//				$("#reportstudyitem_"+reportid).textbox('disable');
//				$("#method_"+reportid).textbox('disable');
//				$("#urgent_"+reportid).checkbox('disable');
//				$("#urgent_explain_"+reportid).textbox('disable');
//				$("#pos_"+reportid).radiobutton('disable');
//				$("#neg_"+reportid).radiobutton('disable');
//				$("#imagequality_"+reportid).combobox('disable');
//				$("#reportquality_"+reportid).combobox('disable');
//				$("#diagnosis_coincidence_"+reportid).combobox('disable');
				disableEditReport(reportid);
			}
			setTimeout(() => {
				$("#report_layout_"+reportid).layout('panel','center').panel({onResize:function(width, height){
					setTimeout(() => {
						let wi=$('#report_panel_'+reportid).width();
						alert(1);
						console.log(wi)
						console.log($('#flag_'+reportid).width())
						if(wi>700){
							ue_desc.setWidth(wi-2);
							ue_result.setWidth(wi-2);
							$('#method_'+reportid).textbox('resize',wi-2);
							$('#reportstudyitem_'+reportid).textbox('resize',wi-2);
						}
					}, 300);
				}});
				
			}, 300);
			
			/*// 判断此报告有没有备注
			getJSON(window.localStorage.ctx+"/report/checkRemarks",
					{
						orderid : row.orderid
					},
					function(data){
						if (data) {
							openRemarkDialog(reportid,row.orderid);
						}
					});			
			$('#progress_dlg').dialog('close');*/
			
		},
		href : window.localStorage.ctx+'/webview/structReport?orderid='+row.orderid+'&reportid='+row.reportid
			+'&studyid='+row.studyorderstudyid+'&template_id='+reportdata.data.template_id,
        onBeforeDestroy : function(title, index) {
        	$('#sr_container_'+row.reportid).empty();
		}
    });
}


function changeReportBtnEnable(status,reportid){
//	console.log("===changeReportBtnEnable===");
//	console.log("status:"+status);
	$.each(myCache.report_btn_enable,function(key,value) {
		if(key.startsWith(status+".")){
			var btn=$('#'+key.substring(status.length+1)+"_"+reportid);
			if(btn[0]){
				if(value=="1"){
					if("DIV"==btn[0].tagName){
						$('#moremenu_'+reportid).menu('enableItem', btn);
					}
					else{
						btn.linkbutton('enable');
					}

				}
				else{
					if("DIV"==btn[0].tagName){
						$('#moremenu_'+reportid).menu('disableItem', btn);
					}
					else{
						btn.linkbutton('disable');
					}
					
				}
			}
		}
	});
}
function addTab_NormalReport(row,reportdata){
	
	$('#tab').tabs('add',{
		id: "tab_"+row.reportid,  
        title: row.patientname,
		closable : true,
		href : window.localStorage.ctx+'/webview/report?&orderid='+row.orderid+'&reportid='+row.reportid
		+'&studyid='+ row.studyorderstudyid
	});
}

function initImgSrc(content){
	content=content.replace(/ghidden/g,"hidden='hidden'");
	content=content.replace(new RegExp("_ueditor_page_break_tag_","gm"),"<hr class='pagebreak' noshade='noshade' size='5' style='-webkit-user-select: none;'>")
	//console.log("ret="+content);
	return content;
}

//关闭检查报告弹窗
function closeHistoryRecord(){
	$('#details').window('close');
}

//刷新
function refresh() {
	
	var p = $('#w_s_accordion').accordion('getSelected');
	var index = $('#w_s_accordion').accordion('getPanelIndex', p);
	
	if(index==1){
		var node =$('#myfavorites_tree_search').tree('getSelected');
		if(node){
			findStudyByFavoritesId(node.id);
		}
	}
	else{
		sumbitSearchForm();
	}
	
	$('#remark').textbox('setValue', '');
	$('#reportcontent').html('');
}



//详情信息
//function details(){  
//	var row = $("#dg").datagrid("getSelected");
//	if (row) {
//		$.ajax({
//			url : window.localStorage.ctx+"/worklist/viewDetails",
//			contentType : "application/x-www-form-urlencoded; charset=UTF-8",
//			dataType : 'json',
//			data : {
//				patientid : row.patientid
//			},
//			success : function(data) {
//				$('#details').dialog({
//					title : '详情信息',
//					width : 1000,
//					height : 500,
//					border:'thin',
//					closed : false,
//					cache : false,
//					href : window.localStorage.ctx+'/worklist/detailsView',
//					modal : true,
//					onLoad:function(){
//						document.getElementById("_patientid").value=data[0].patientid;
//						document.getElementById('_patientname').value=data[0].patientname;
//						document.getElementById('_py').value=data[0].py;
//						document.getElementById('_sexdisplay').value=data[0].sexdisplay;
//						document.getElementById('_telephone').value=data[0].telephone;
//						document.getElementById('_modality_type').value=data[0].modality_type;
//						document.getElementById('_age').value=data[0].age;
//						document.getElementById('_bedno').value=data[0].bedno;
//						document.getElementById('_address').value=data[0].address;
//						document.getElementById('_birthdate').value=data[0].birthdate;
//						document.getElementById('_cardno').value=data[0].cardno;
//						document.getElementById('_inno').value=data[0].inno;
//						document.getElementById('_outno').value=data[0].outno;
//						document.getElementById('_wardno').value=data[0].wardno;
//						document.getElementById('_studyitems').value=data[0].studyitems;
//						document.getElementById('_studyid').value=row.studyorderstudyid;
//						document.getElementById('_patientsource').value=data[0].patientsource;
//						document.getElementById('_priority').value=data[0].priority;
//						document.getElementById('_modality').value=data[0].modality;
//						document.getElementById('_appdept').value=data[0].appdept;
//						document.getElementById('_appdoctor').value=data[0].appdoctor;
//						document.getElementById('_modality').value=data[0].modality;
//						document.getElementById('_remark').value=data[0].remark;
//					}
//				});
//				$('#details').dialog('refresh',window.localStorage.ctx+'/worklist/detailsView');
//			}
//			});
//	
//	}else{
//		$.messager.show({
//			title : '提醒',
//			msg : "请选择检查！",
//			timeout : 3000,
//			border:'thin',
//			showType : 'slide'
//		});
//	}
//}

/**
 * 给申请单赋值
 * @param row
 * @returns
 */
var request_reportid;
function getImage(row){
	
	if(row){
		var studyid = row.studyorderstudyid;
		var	reportid = row.reportid;
		request_reportid = reportid;
		$.ajax({
			url : window.localStorage.ctx+"/worklist/getImage",
			dataType : 'JSON',
			data:{studyid:studyid},
			success : function(res) {
				if(res.code == 0){
					$("#imageShow").attr("src",res.data.url);
				}else{
					$("#imageShow").attr("src","");
				}
			}
		});
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
	var row=$('#myfilterlist_webview').datalist('getSelected');
	console.log(row)
	if(!row)return;
	//form中存在多谢下拉框时，如果对应值为null,在加载前必须置为'',不然会报错。
	$("#searchForm_webview").form('load',row);
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
	}else if (row.appdate == "M") {
		$('#month').linkbutton('select');
		$("#appdate").val("M");
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
function print(reportid,issr) {
	if($('#id_' + reportid).val()){
		 window.open(window.localStorage.ctx+"/print/printReport?reportid="+$('#id_' + reportid).val() +"&issr="+issr);
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

function printReport(projecturl,reportid,ptemp,issr){
	
	if($('#id_' + reportid).val()){
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

function openImageAtOnce(row,flag){
	if(flag=='1'){
		$('#callupbtn').attr('href','reporttool:-c via -v '+row.studyorderstudyid);
		$('#callupbtn')[0].click();
	}
}

function detailFormatter_wl_previewreport(index,row){
	return '<div class="ddv" style="padding:5px 0"></div>';
}

function onExpandRow_wl_previewreport(index,row){
	if (row.reportid) {
		var ddv = $('#dg').datagrid('getRowDetail',index).find('div.ddv');
	    ddv.panel({
	        border:false,
	        cache:false,
	        href:window.localStorage.ctx+"/webview/previewReport?reportid="+row.reportid,
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
	if(color){
		return 'background-color:'+color+';';//color:red;
	}
	else{
		return '';
	}
}
function columeStyler_reportstatus_wl(value,row,index){
	var reportstatusdisplay = row.reportstatusdisplaycode;
	if(!reportstatusdisplay){
		reportstatusdisplay = myCache.ReportStatus.Noresult;
	}
	var color=myCache.status_color['0007_'+reportstatusdisplay];
	if(color){
		return 'background-color:'+color+';';//color:red;
	}
	else{
		return '';
	}	
}

function reportstatus_formatter(value,row,index){
	if(value){
		return value;
	}
	else{
		return "未写";
	}
}

function openApplyForm(){
	var row=$("#dg").datagrid("getSelected");
	if(row!=null){
		apply(row.orderid);
	}
	else{
		$.messager.show({
	        title:'提醒',
	        msg:'请选择一条数据',
	        timeout:3000,
	        border:'thin',
	        showType:'slide'
	    });
	}
}

//关闭弹窗
function closeTab(reportid) {
	var tabid="tab_"+reportid;
	var index=0;
	for(var i=0; i<$('#tab').tabs('tabs').length; i++){
		var id=$('#tab').tabs('getTab',i).panel('options').id;
		if(id==tabid){
			index=i;
		}
	}
	if(index){
		console.log("closeTab:"+index);
		$('#tab').tabs('close', index);
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
			window.location.href = window.localStorage.ctx+"/webview/exportReport?reportid=" + row.reportid 
		} else {
			$.messager.show({
		        title:'提醒',
		        msg:'请选择已审核的报告',
		        timeout:3000,
		        border:'thin',
		        showType:'slide'
		    });
		}
	} else {
		$.messager.show({
	        title:'提醒',
	        msg:'请选择一条数据',
	        timeout:3000,
	        border:'thin',
	        showType:'slide'
	    });
	}
}

/**
 *  批量导出pdf报表文件
 * @returns
 */
function batchExportReport() {
	// 1、根据查询条件获取 所有检查状态为 检查完成 的数据
	var result = true;
	if (result) {
		result = false;
		$.ajax({
	        async: false,
	        type: "POST",
	        url: window.localStorage.ctx + "/webview/getBatchExportReport?reportstatus="+myCache.ReportStatus.FinalResults,
	        contentType : "application/x-www-form-urlencoded; charset=utf-8",
	        data:$("#searchForm_webview").serialize(),
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
		            	window.location.href = window.localStorage.ctx+"/webview/batchExportReport?ids=" + ids;
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


