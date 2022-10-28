//var wsurl="";
//var websocket = null;  
//var lockReconnect = false;  //避免ws重复连接
var myCache;
var closeReportFlag = true;
$(function() {
	myCache=JSON.parse(window.localStorage.myCache);
    //判断当前浏览器是否支持WebSocket
//    if ('WebSocket' in window) {
//    	try{
//    		websocket = new WebSocket(wsurl);
//    	}
//    	catch(err){
//    		$.messager.show({
//    			title : '错误提醒',msg : "与服务器的连接失败，请重试，如果问题依然存在请联系系统管理员！",timeout : 3000,border: 'thin',showType : 'fade',style:{
//                    right:'',
//                    bottom:''
//                }
//    		});
//    	}
//        
//    }
//    else{
//        console.log('当前浏览器 Not support websocket')
//        $.messager.show({
//			title : '错误提醒',msg : "当前浏览器 Not support websocket！",timeout : 3000,border: 'thin',showType : 'fade',style:{
//                right:'',
//                bottom:''
//            }
//		});
//    }    
//    
//	wsurl="ws://"+window.localStorage.serverName+":"+window.localStorage.port+window.localStorage.ctx+"/websocket/"+$("#userid_hidden").val();
//    createWebSocket(wsurl);
	
	setTimeout(function () {
		var via_hl7={
				type : "viareport",
				exec : function(data){
					console.log("viareport exec "+data);
					var json=JSON.parse(data)
					
					var tabs=$('#tab').tabs('tabs');
					for(var i=1;i<tabs.length;i++){
						var tabid = tabs[i].panel('options').id;
						var reportid=getReportid_Open(tabid);
						if($('#orderid_'+reportid).val() == json.orderid && $('#via_images_' + reportid)[0]){
							$('#via_images_' + reportid).datagrid('reload');
		    				$('#via_findings_' + reportid).datagrid('reload');
						}
					}
					
					/*var index=getTabIndexByOrderid(json.orderid);
					if(index){
	    				$('#via_images_'+json.orderid).datagrid('reload');
	    				$('#via_findings_'+json.orderid).datagrid('reload');
					}*/
				}
			}
		var reportprintstatus={
				type: 'printstatus',
				exec: function(data){
					console.log("printstatus:"+data);
					if (data == "True") {
						$.messager.show({
							title:'提醒',
					        msg: '打印成功！',
					        timeout:3000,
					        border:'thin',
					        showType:'slide'
						});
					} else {
						$.messager.show({
							title:'提醒',
					        msg: '打印失败！',
					        timeout:3000,
					        border:'thin',
					        showType:'slide'
						});
					}
				}
				
		}
			//console.log(websocket)
			if(websocket){
				initService_WS(via_hl7);
				initService_WS(reportprintstatus);
			}

	}, 1000);
});

function refreshReportInfo(reportid,reportstatus){
	var rows = $('#dg').datagrid('getRows');
	var index = -1;
	for(var i=0; i<rows.length; i++){
		if(reportid && reportid==rows[i].reportid){
			console.log(i);
			console.log(rows[i]);
			index = $('#dg').datagrid('getRowIndex',rows[i]);
			break;
		}
	}
	if(index != -1 && reportstatus){
		var reportstatusdisplay = "";
		if(reportstatus == myCache.ReportStatus.Noresult){
			reportstatusdisplay = "未写"
		}else if(reportstatus == myCache.ReportStatus.Preliminary){
			reportstatusdisplay = "初步报告";
		}else if(reportstatus == myCache.ReportStatus.Preliminary_reject){
			reportstatusdisplay = "初步报告驳回";
		}else if(reportstatus == myCache.ReportStatus.FinalResults){
			reportstatusdisplay = "已审核";
		}
		$('#dg').datagrid('updateRow',{
			index : index,
			row:{
				reportstatusdisplaycode: reportstatus,
				reportstatusdisplay: reportstatusdisplay
			}
		});
	}else if(index != -1 && !reportstatus){
		$('#dg').datagrid('updateRow',{
			index : index,
			row:{
				islocking : 0,
				lockingpeople : "",
				locking : "",
			}
		});
	}
	
}

/**
 *  打开图像
 * @param para 用来判断打开的是哪一类图像
 * @returns
 */
function openStudyImage(para) {
	var row = $("#dg").datagrid("getSelected");
	if (row != null) {
		closeReportFlag=false;
		urlFlag = true;
		var str = row.studyorderstudyid;
		var href = $('#'+para).val() + str;
		console.log(href);
		if(electron_enable()){//通过electron调用reporttool
			if('viapara_worklist'==para){
				electron_via_open(null,href);
			} else{
				electron_plaza_loaddata(null,null,null,href);
			}
		} else{//通过url protocal调用reporttool
			$("#image_worklist")[0].setAttribute('href',href);
			$("#image_worklist")[0].click();
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
 * 获取reportid
 * @param tabid
 * @returns
 */
function getReportid_Open(tabid){
	if(!tabid){
		tabid=$('#tab').tabs('getSelected').panel('options').id;
	}	
	return tabid.substr(tabid.lastIndexOf("_")+1);
}

/**
 * 获取Tab的index
 * @param reportid
 * @returns
 */
function getTabIndexByReportid(reportid){
	var tabid="tab_"+reportid;
	var tabs=$('#tab').tabs('tabs');
	for(var i=1;i<tabs.length;i++){
		if(tabs[i].panel('options').id==tabid){
			return i;
		}
	}
	return null;
}

function validator (value) { 
    //格式yyyy-MM-dd或yyyy-M-d
       return/^(?:(?!0000)[0-9]{4}([-]?)(?:(?:0?[1-9]|1[0-2])\1(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])\1(?:29|30)|(?:0?[13578]|1[02])\1(?:31))|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([-]?)0?2\2(?:29))$/i.test(value); 
}
var default_page=1;
var default_pageSize=50;
var searchForm_flag="searchForm";
function initWorklist(){
//	$("#page").val(page);
//	$("#pageSize").val(pageSize);
	//设置分页控件 
    var p = $('#dg').datagrid('getPager'); 
    $(p).pagination({ 
        pageSize: default_pageSize,//每页显示的记录条数，默认为20 
        pageNumber:default_page,
        pageList: [20,30,50,100],//可以设置每页记录条数的列表 
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
    		}else if(searchForm_flag=="labelSearchForm"){
    			labelSearch(pageNumber, pageSize);
    		}else if(searchForm_flag=="fulltextSearchForm"){
    			fulltextSearch(pageNumber, pageSize);
    		}else if(searchForm_flag=="Favorites"){
    			var node = $('#myfavorites_tree_search').tree('getSelected');
    			queryReport(pageNumber,pageSize,node.id);
    		}
    		else if(searchForm_flag=="quicksearch"){
    			sumbitSearchForm(pageNumber,pageSize,$('#quicksearch-input').searchbox('getValue'),$('#quicksearch-input').searchbox('getName'));
    		}
    		
    		$(this).pagination('loaded');
    	}
    });
    
    //设置可移动列
    startReDatagridColumn($('#dg')[0], 'worklist');
    
    var item = $('#cmenu_wl').menu('findItem', {name:'open'});
    if(item){
	    getJSON(window.localStorage.ctx +"/srtemplate/findSRTemplate?withContent=false",null,function(json) {
			  // find 'Open' item
			$.each(json,function(index,temp){
				$('#cmenu_wl').menu('appendItem', {
					parent: item.target,  // the parent item element
					text: temp.name,
					onclick: function(){openReportWithSRTemp(temp.id);}
				});
			});
	
		});
    }
    
    var content = '<b>【'+$.i18n.prop('report.studymethod')+'】:</b>'
	+ '<br/><b>【核医学所见】:</b>'
	+ '<br/><b>【核医学诊断】:</b>'
	$('#mypanel_wl').panel({
        content:content
    });
    
    
    getJSON(window.localStorage.ctx +"/report/getRejectReportCount",null,function(json) {
//		console.log(json)
		if(json.code==0){
			if (parseInt(json.data) > parseInt(0)) {
				$.messager.confirm({
					title : $.i18n.prop('confirm'),ok:'我已知晓',closable:false,cancel:'否', border: 'thin',
					msg : '你有'+json.data+'份报告被驳回，是否现在处理？',
					fn : function(r){
					    if (r){
					    	searchForm_flag='searchForm';
					    	var row={'reportstatus':'25','reportphysician_name':$('#name_hidden').val(),'appdate':'W','datetype':'reporttime'};
//					    	console.log(row)
			            	fillParams(row);
					    }
				    }
				});
			}
		}
	});
    
}

function sumbitSearchForm(pageNum, pageSi, quicksearchcontent, quicksearchname, timingTaskFlag) {
	if (!pageNum) {
		pageNum = default_page;
		$('#dg').datagrid('getPager').pagination({ 
			pageSize: default_pageSize,//每页显示的记录条数，默认为20 
			pageNumber: default_page
		});
	}
	if (!pageSi) {
		pageSi = default_pageSize;
	}
	$("#searchForm_worklist").form('submit', {
		url:window.localStorage.ctx+"/worklist/getDataAll?page="+ pageNum +"&rows="+ pageSi +
		"&quicksearchname="+(quicksearchname||"")+
		"&aet="+(aet||'')+"&hostname="+(hostname||'')+"&port="+(port||''),
		onSubmit : function(param) {
			param.quicksearchcontent = quicksearchcontent
			if(!timingTaskFlag){
				$('#progress_dlg').dialog('open');
			}
			if(checkSearch()){
				$('#progress_dlg').dialog('open');
				return true;
			} else {
				$('#progress_dlg').dialog('close');
				return false;
			}
		}
	});
}

function initFieldEvent() {
	$('#searchForm_worklist').form({
		url : window.localStorage.ctx+"/worklist/getDataAll?page="+ default_page +"&rows="+ default_pageSize,
		onSubmit : function() {
			$('#progress_dlg').dialog('open');
		},
		success : function(data) {
				$('#progress_dlg').dialog('close');
				$("#dg").datagrid("loadData", validationData(data)); // hide
				if(nowflag){
					var p = $('#dg').datagrid('getPager'); 
					 $(p).pagination({ 
						 pageNumber:nowPageNumber,
					 });
					 nowflag = false;
				} 
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
	
	$('body').everyTime("10000s",function(){
		refresh(true);
	});
}

var cardview = $.extend({}, $.fn.datagrid.defaults.view, {  
    renderRow: function(target, fields, frozen, rowIndex, rowData){  
        var cc = [];  
        cc.push('<td style="padding:2px;border:0;">');  
        //if (!frozen){  
        
        if(rowData.imageid)
        	cc.push('<img src="'+window.localStorage.ctx +'/image/image_GetViaImage?id=' + rowData.imageid + '" align="middle" style="height:180px;float:center" />');  
           /* cc.push('<img src="'+window.localStorage.ctx +'/image/image_GetViaImage?id=' + rowData.imageid + '" align="middle" style="height:150px;float:center" ondblclick="insertImages('+ rowData.imageid + ',\''+rowData.studyid+'\')"/>');  */
//            cc.push('<div style="float:left">');  
//            for(var i=0; i<fields.length; i++){  
//                cc.push('<p>' + fields[i] + ': ' + rowData[fields[i]] + '</p>');  
//            }  
//            cc.push('</div>');  
        //}  
        cc.push('</td>');  
        return cc.join('');  
    }  
});

var TimeFn = null;
function clickDgRow(index, row) {
	clearTimeout(TimeFn);
	if(row.datasource=="via"){
		return;
	}
	
	 TimeFn = setTimeout(function () {
         //....Other Operation
		 //历史检查
		 getJSON(window.localStorage.ctx+"/worklist/findHistoryStudyorder",
				 {
			 		patientid : row.patientid,
			 		orderid : row.orderid
				 },function(data){
					 $("#historydg_wl").datagrid("loadData", validationData(data));
				 });
		 
		 //所见所得
		 var content = "";
		 if (row.vipflag == 1 && row.reportstatusdisplaycode == myCache.ReportStatus.FinalResults
					&& $('#vip_flag')[0] && $('#vip_flag').val() != 1){
			 content = '<b>【'+$.i18n.prop('report.studymethod')+'】:</b>' + ""
				+ '<br/><b>【核医学所见】:</b>' + ""
				+ '<br/><b>【核医学诊断】:</b>' + "";
			 $('#mypanel_wl').panel({
		        content:content
			 });
		 } else {
			 if(row.templateid && row.templateid > 0){
					getJSON(window.localStorage.ctx+"/worklist/goToPreviewSRReport",
							{
								reportid : row.reportid
							},
							function(data){
								if(data){
									content = '<b>【核医学所见】:</b>' + (data.checkdesc || "")
									+ '<br/><b>【核医学诊断】:</b>' + (data.checkresult || "");
									$('#mypanel_wl').panel({
								        content:content
								    });
								}
								
							});
				 }else{
					content = '<b>【'+$.i18n.prop('report.studymethod')+'】:</b>' + (row.studymethod || "")
					+ '<br/><b>【核医学所见】:</b>' + (row.checkdesc_txt || "")
					+ '<br/><b>【核医学诊断】:</b>' + (row.checkresult_txt || "");
					$('#mypanel_wl').panel({
				        content:content
				    });
				 } 
		 }
		/* if(row.patientidfk){
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
			}*/
      }, 100);//延时时长设置           
	
	
}

/**
 * 条件清除
 * @returns
 */
function clearManage(){
	$('#myfilterlist_worklist').datalist('clearSelections');
	var datasource_value=$('#datasource')[0]?$('#datasource').combobox('getValue'):"";
	$('#searchForm_worklist').form('clear');
	$('#datasource')[0]?$('#datasource').combobox('setValue',datasource_value):"";
	$('#datetype').combobox('setValue',$('#datetype').combobox('getData')[0].value);

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

//	var modality = $('#modality').combobox('getValue'); // 设备类型
//	// 报告状态 O未写 P未审 R已审 G我的报告 K 不限
//	var reportStatus = $('#reportStatus').combobox('getValue');
//	var institutionid = $('#institutionid').combobox('getValue'); // 送检机构
	
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

var aet,hostname,port;
function searchStudyWS(quickSearchContent,quickSearchName) {
//	if (checkSearch()) {
		if ($('#datasource')[0]&&$('#datasource').combobox('getValue') != 'localdatabase') {
			aet=null;
			hostname=null;
			port=null;
			var name=$('#datasource').combobox('getText')
			var data=$('#datasource').combobox('getData');
			for(var i=0;i< data.length;i++){
				if(data[i].name==name){
					aet=data[i].aet;
					hostname=data[i].hostname;
					port=data[i].port;
					break;
				}
			}
			
			$('#progress_dlg').dialog('open');
			getJSON(window.localStorage.ctx+"/worklist/echo",
					{
						aet: aet,
						hostname: hostname,
						port: port
					}
					,
					function(json) {
						var data = validationData(json);
						if (data.code !=0) {
							$('#progress_dlg').dialog('close');
							$.messager.show({
								title : $.i18n.prop('error'),msg : $.i18n.prop('wl.connecttoviafailed'),timeout : 3000,border: 'thin',showType : 'slide'
							});

						} else {
							sumbitSearchForm(null, null, quickSearchContent,quickSearchName);
						}

					});
		} else {
			sumbitSearchForm(null, null, quickSearchContent,quickSearchName);
		}
//	}
}

//var page=1;
//var pageSize=20;


/**
 * 查询首页list 
 * @param pageNumber
 * @param pageSize
 * @returns
 */
//function queryList(pageNumber,pageSize,row){
//	fla = false;
	
//	$('#remark').textbox('setValue', '');
	//$("#reportcontent").html('');

//}


function retrieveData(){
	var row = $("#dg").datagrid("getSelected");
//	console.log(row)
	if (row) {
		if(!row.patientid){
			$.messager.show({
				title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.patientidcannotnull'),timeout : 3000,border: 'thin',showType : 'slide'
			});
			return;
		}
		if(!row.patientname){
			$.messager.show({
				title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.patientnamecannotbenull'),timeout : 3000,border: 'thin',showType : 'slide'
			});
			return;
		}
		if(!row.studyorderstudyid){
			$.messager.show({
				title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.studyidcannotbenull'),timeout : 3000,border: 'thin',showType : 'slide'
			});
			return;
		}
		
		$.post( window.localStorage.ctx+"/worklist/retrieveData",row, function( data ) {
			var json = validationData(data);
			if(json.code==0){
				$.messager.show({
					title : $.i18n.prop('tips'),msg : $.i18n.prop('wl.retrievedatasuccess'),timeout : 3000,border: 'thin',showType : 'slide'
				});
			}
			else{
				$.messager.show({
					title : $.i18n.prop('error'),msg : $.i18n.prop('wl.retrievedatafailed'),timeout : 3000,border: 'thin',showType : 'slide'
				});
			}
		});
		
	}
	else{
		$.messager.show({
			title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.selectonestudy'),timeout : 3000,border: 'thin',showType : 'slide'
		});
	}
}

function dataSourceChangeHandler(newValue,oldValue){
	console.log("newValue="+newValue);
	if(!newValue)return;
	if("localdatabase"!=newValue){
		$('#reportStatus').combo('disable');
		$('#patientsource').combo('disable');
		$('#institutionid').combo('disable');
		$('#reportphysician_name_ws').textbox('disable');
		$('#auditphysician_name_ws').textbox('disable');
		$('#openreport_linkbutton').linkbutton('disable');
		$('#delete_linkbutton').linkbutton('disable');
		$('#retrieve_linkbutton').linkbutton('enable');
		var item=$('#cmenu_wl').menu('findItem',{name:'report'});
		$('#cmenu_wl').menu('disableItem',item.target);
		var item=$('#cmenu_wl').menu('findItem',{name:'open'});
		$('#cmenu_wl').menu('disableItem',item.target);
		var item=$('#cmenu_wl').menu('findItem',{name:'delete'});
		$('#cmenu_wl').menu('disableItem',item.target);
		var item=$('#cmenu_wl').menu('findItem',{name:'cancelaudireport'});
		$('#cmenu_wl').menu('disableItem',item.target);
		var item=$('#cmenu_wl').menu('findItem',{name:'cancellock'});
		$('#cmenu_wl').menu('disableItem',item.target);
		$('#datetype').combobox('select','studytime');
	}
	else{
		$('#reportStatus').combo('enable');
		$('#patientsource').combo('enable');
		$('#institutionid').combo('enable');
		$('#reportphysician_name_ws').textbox('enable');
		$('#auditphysician_name_ws').textbox('enable');
		$('#openreport_linkbutton').linkbutton('enable');
		$('#delete_linkbutton').linkbutton('enable');
		$('#retrieve_linkbutton').linkbutton('disable');
		var item=$('#cmenu_wl').menu('findItem',{name:'report'});
		$('#cmenu_wl').menu('enableItem',item.target);
		var item=$('#cmenu_wl').menu('findItem',{name:'open'});
		$('#cmenu_wl').menu('enableItem',item.target);
		var item=$('#cmenu_wl').menu('findItem',{name:'delete'});
		$('#cmenu_wl').menu('enableItem',item.target);
		var item=$('#cmenu_wl').menu('findItem',{name:'cancelaudireport'});
		$('#cmenu_wl').menu('enableItem',item.target);
		var item=$('#cmenu_wl').menu('findItem',{name:'cancellock'});
		$('#cmenu_wl').menu('enableItem',item.target);
	}
	
}

function findStudyByFavoritesId(id) {
	$('#progress_dlg').dialog('open');
	/*if($("#page").val()!="")
		default_page=$("#page").val()
	if($("#pageSize").val()!="")
		default_pageSize=$("#pageSize").val();*/
//	var p = $('#dg').datagrid('getPager'); 
//	$(p).pagination({ 
//			pageSize: pageSize,//每页显示的记录条数，默认为10 
//	        //pageNumber:page,
//	        pageList: [10,20,30,50],//可以设置每页记录条数的列表 
//	        beforePageText: '第',//页数文本框前显示的汉字 
//	       // showRefresh:false,
//	        afterPageText: '页    共 {pages} 页',
//	        displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录', 
//	        onSelectPage:function(pageNumber, pageSize){
//	        	page=pageNumber;
//	        	pageSize=pageSize;
//	        	$("#page").val(pageNumber);
//	        	$("#pageSize").val(pageSize);
//	        	$(this).pagination('loading');
//	    		$('#progress_dlg').dialog('open');
//	    		queryReport(pageNumber,pageSize,id)
//	    		$(this).pagination('loaded');
//	    	}
//	    });
	queryReport(default_page,default_pageSize,id);
}

function queryReport(pageNumber,pageSize,id){
	fla = false;
	searchForm_flag="Favorites";
//	$('#remark').textbox('setValue', '');
	//$("#reportcontent").html('');
	
	$.getJSON(window.localStorage.ctx+"/worklist/findStudyByFavoritesId?favorites_id=" +id+"&page="+ pageNumber +"&rows="+ pageSize ,function(data) {
				$('#progress_dlg').dialog('close');
				$('#remark').textbox('setValue', '');//备注清空
				//$('#reportcontent').html('');//备注清空
				$("#dg").datagrid("loadData", validationData(data)); // hide
		
	});
}
//var reportdatas;
//var Row;
//var reportId;
//var openStatus="";
/*******************$("#studyidList")负责存储当前打开的报告id，当关闭时同时解锁报告*****************************************/
function storeOpenReportid(reportid){
	var reportidInfo=$("#studyidList").val();
	if(reportidInfo==""){
		reportidInfo=reportid;
	}else{
		reportidInfo=reportidInfo +"," + reportid;
	}
	$("#studyidList").val(reportidInfo);
}


function openReportWithSRTemp(srtemplateid){
	var row = $("#dg").datagrid("getSelected");
	if (row) {
		
		if(row.datasource=="via"){
			$.messager.show({title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.cannotopenreport'),timeout : 3000,border:'thin',showType : 'slide'});
			return;
		}
		
		if(row.status != myCache.StudyOrderStatus.completed){//判断检查状态是否为检查完成
			$.messager.show({title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.studywasnotcompleteandcannotopen'),timeout : 3000,border:'thin',showType : 'slide'});
			return;
		}
		
//		if(row.status < myCache.StudyOrderStatus.completed){//判断检查状态
//			$.messager.show({title : $.i18n.prop('alert'),msg : '当前检查状态无法编辑报告',timeout : 3000,border:'thin',showType : 'slide'});
//			return;
//		}

		var index=getTabIndexByReportid(row.reportid);
		
		if (!index) {
			$('#progress_dlg').dialog('open');
			getJSON(window.localStorage.ctx+"/srreport/openReportWithSRTemp",
					{
						studyid : row.studyorderstudyid || '',
						orderid : row.orderid,
						reportid : row.reportid,
						srtemplateid : srtemplateid
					},
					function(json) {
					var reportdata = validationDataAll(json);
					if(reportdata.code==0){
						if(reportdata.data.islocking==1){
							$.messager.show({title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.reportislockedcannotbeopen'),timeout : 3000,border:'thin',showType : 'slide'});
							$('#dg').datagrid('updateRow',{
									index:$('#dg').datagrid('getRowIndex',row),
									row:{
										locking: $.i18n.prop('wl.locked'),
										lockingpeople:reportdata.data.lockingpeople
									}
							});
							
						}else{
							var reportid = row.reportid;
							if(row.reportid == null || row.reportid==""){
								reportid = reportdata.data.id;
							}
							lock_report(row);//打开报告后锁定报告
							/*******************$("#studyidList")负责存储当前打开的报告id，当关闭时同时解锁报告*****************************************/
							storeOpenReportid(reportid);
							/*********************************************************************************************************************/
							
							$('#changeflag_'+reportdata.data.id).val(false);
							addTab_StructReport(row, reportid, reportdata,true);
						}
					
					}
					else{
						$.messager.show({title : $.i18n.prop('error'),msg :reportdata.message ,timeout : 3000,border:'thin',showType : 'slide'});//"打开报告，发生错误，请重试！ 如果问题依然存在，请联系系统管理员。"
					}
				
				$('#progress_dlg').dialog('close');
			});
		}
		else {
			$('#tab').tabs('select',index); 
		}
	}
	else {
		$.messager.show({title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.selectonestudy'),timeout : 3000,border:'thin',showType : 'slide'});
	}
}

function openReport(srtemplateid,openimageflag) {
	clearTimeout(TimeFn);
	var row = $("#dg").datagrid("getSelected");
	if (row) {
		if(row.datasource=="via"){
			$.messager.show({title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.cannotopenreport'),timeout : 3000,border:'thin',showType : 'slide'});
			return;
		}
		
		if(row.status != myCache.StudyOrderStatus.completed){//判断检查状态是否为检查完成
			$.messager.show({title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.studywasnotcompleteandcannotopen'),timeout : 3000,border:'thin',showType : 'slide'});
			return;
		}
		
		var userid = $("#userid_hidden").val();
		var openGrade = $("#openGrade").val();  // 1，报告医生；2，审核医生；3，主任
		console.log("打开报告：" + userid + "," + openGrade + "," + row.createphysician);
		if ("未写" != row.reportstatusdisplay && "1" == openGrade && row.createphysician != null && userid != row.createphysician+"") {
			_message("此报告已被指定！");
			return;
		}
		
		if ("已创建" == row.reportstatusdisplay && row.createphysician != null && userid != row.createphysician+"") {
			_message("只有创建人本人才能打开此份报告！");
			return;
		}
		
//		if(row.status < myCache.StudyOrderStatus.completed){//判断检查状态
//			$.messager.show({title : $.i18n.prop('alert'),msg : '当前检查状态无法编辑报告',border:'thin',timeout : 3000,showType : 'slide'});
//			return;
//		}
		
		if (row.vipflag == 1 && row.reportid && row.reportstatusdisplaycode == myCache.ReportStatus.FinalResults
				&& $('#vip_flag')[0] && $('#vip_flag').val() != 1){
//			console.log($('#username_hidden').val());
//			console.log($('#vip_flag').val());
			_message('无打开vip病人权限' , '提醒');
			return;
		}
		
		beforeOpenReport(row,row.reportid,openimageflag,true);		
		
	} else {
		$.messager.show({title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.selectonestudy'),timeout : 3000,border:'thin',showType : 'slide'});
	}
}

function beforeOpenReport(row,reportid,openimageflag,lock){
	var index=getTabIndexByReportid(reportid);
	
	if (!index) {
		var tab = $('#tab').tabs("tabs");
		if (tab.length >= Number(2)) {
			_message("只能同时打开一个报告！");
			return;
		}
		
		
		$('#progress_dlg').dialog('open');
		getJSON(window.localStorage.ctx+"/report/openReport",
				{
					orderid : row.orderid,
					reportid : reportid 
				},
				function(json) {
					var reportdata = validationDataAll(json);
					if(reportdata.code==0){
						var name=$("#name_hidden").val();
						//如果锁定人为当前用户则不需要解锁
						if(reportdata.data.islocking==1&&name!=reportdata.data.lockingpeople){
							$.messager.show({title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.reportislockedcannotbeopen'),timeout : 3000,border:'thin',showType : 'slide'});
							$('#dg').datagrid('updateRow',{
									index:$('#dg').datagrid('getRowIndex',row),
									row:{
										locking:$.i18n.prop('wl.locked'),
										lockingpeople:reportdata.data.lockingpeople
									}
							});
						}else{
							if(!reportid){
								reportid = reportdata.data.id;
								$('#dg').datagrid('updateRow',{
									index:$('#dg').datagrid('getRowIndex',row),
									row:{
										reportid:reportid
									}
								});
							}
							openImageAtOnce(row,openimageflag);
							if(lock){
								lock_report(row);//打开报告后锁定报告
							}	
							/*******************$("#studyidList")负责存储当前打开的报告id，当关闭时同时解锁报告*****************************************/
							storeOpenReportid(reportid);
							/*********************************************************************************************************************/
							$('#changeflag_'+reportid).val(false);
							if(reportdata.data.template_id==null||reportdata.data.template_id==0){
							/*******************打开普通报告*****************************************/
								addTab_NormalReport(row,reportid,reportdata);
								$("#reportcontent").html('');
							} else{
							/*******************打开结构化报告*****************************************/
								addTab_StructReport(row,reportid,reportdata,false);
							}
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
}

function addTab_StructReport(row,reportid,reportdata,openwithsrtemp){

	$('#tab').tabs('add',{
        id: "tab_" + reportid,  
        title: row.patientname+'('+(row.sexdisplay||'')+')'+(row.age||'')+(row.ageunitdisplay||''),
//									            href: url,      //使用content的iframe方式：将整个页面嵌入到tab页内。  缺点：js重复加载  
        closable: true ,
        onLoad : function(){

			// 设置编辑器的内容 'openwithsrtemp' =true:以结构化报告模板直接打开，不用判断报告状态
    		if(openwithsrtemp){
    			$('#sr_container_' + reportid).panel({content:pluginHandle.formatComponent(reportdata.data.srtemplatecontent)});
    			$('#contentChangeflag_' + reportid).val(true)
    		}
    		else{
    			if(reportdata.data.need_format=="1"){//新建的报告，内容需要格式化
    				$('#sr_container_' + reportid).panel({content:pluginHandle.formatComponent(reportdata.data.checkresult_html)});
    			} else{
    				$('#sr_container_' + reportid).panel({content:initImgSrc(reportdata.data.checkresult_html)});
    			}
    		}
        	changeReportBtnEnable(reportdata.data.reportstatus,reportid);
//			if(reportdata.data.reportstatus==myCache.ReportStatus.FinalResults&&!openwithsrtemp){
//				
//			}
//			else{
				if(openwithsrtemp||reportdata.data.reportstatus==myCache.ReportStatus.Noresult){//新的模板，填充clinicalcode
					//importViaDataToReport_clinicalcode(reportid,row.studyorderstudyid);
					$('#sr_container_' + reportid).data('inittemplate',true);
				}
				//addListenerOnSRContainer($('#contentChangeflag_' + reportid))
//			}
//			initStructReportData(row);
        },
        
		href : window.localStorage.ctx+'/srreport/structReport?orderid='+row.orderid+'&reportid='+reportid
			+'&studyid='+row.studyorderstudyid+'&template_id='+reportdata.data.template_id,
        onBeforeDestroy : function(title, index) {
//        	UE.delEditor('structTemplet_'+ row.studyorderstudyid);
        	
        	$('#sr_container_' + reportid).empty();
		}
      
    });
}

function age_formatter(value,row,index){
	if(value){
		return value+row.ageunitdisplay;
	}
	else{
		return "";
	}
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

function addTab_NormalReport(row,reportid,reportdata){
//	console.log($('#worklist_layout_id').height())
	$('#tab').tabs('add',{
		id: "tab_"+reportid,  
        title: row.patientname+'('+(row.sexdisplay||'')+')'+(row.age||'')+(row.ageunitdisplay||''),
		closable : true,
		onLoad : function(panel) {
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
		/*	// 判断此报告有没有备注
			getJSON(window.localStorage.ctx+"/report/checkRemarks",
					{
						orderid : row.orderid
					},
					function(data){
						if (data) {
							openRemarkDialog(reportid,row.orderid);
						}
					});	*/		
			$('#progress_dlg').dialog('close');
			
		},
		href : window.localStorage.ctx+'/report/report?orderid=' + row.orderid + '&reportid='+ reportid
			+'&studyid='+ row.studyorderstudyid + '&height=' + $('#worklist_layout_id').height()+'&modality_type='+row.modality_type
//		,onBeforeDestroy : function(title, index) {
//			destroyUeditor(row);
//		}
	});
}



function initImgSrc(content){
//	$('#temp_div_hidden').html(content);
//	$('#temp_div_hidden').find('img').each(function(index,obj){
//		var src=$(obj).attr('src');
//		var index=src.indexOf(window.localStorage.ctx);
//		if(index<0){
//			$(obj).attr('src',window.localStorage.ctx+src);
//			$(obj).attr('alt',"img");
//		}
//	});
//	var ret=$('#temp_div_hidden').html();
//	$('#temp_div_hidden').empty();
	if(content==null){
		return '';
	}
	content=content.replace(/ghidden/g,"hidden='hidden'");
	content=content.replace(new RegExp("_ueditor_page_break_tag_","gm"),"<hr class='pagebreak' noshade='noshade' size='5' style='-webkit-user-select: none;'>")
	//console.log("ret="+content);
	return content;
}

//function initStructReportData(row){
//	$.getJSON('/report/getStructReport?stuydid='+row.studyorderstudyid,function(json){
//			if(json!=null){
////									            				$("#structTemplet_"+json.studyid).find("label").each(function(index,obj){
////																	 var fieldId = $(obj).attr("for");
////																	 var fieldCode = $("#" + fieldId).attr("name");
////																	 var value = eval('json.' + fieldCode);
////																	 $("#structTemplet_"+json.studyid + " #" + fieldId).val(value);
////																	 if(row.reportstatusdisplaycode=="R"){
////																		 $("#structTemplet_"+json.studyid + " #" + fieldId).attr('onfocus','this.blur()');
////																		 $("#structTemplet_"+json.studyid + " select").attr('disabled','disabled');
////																	 }
////																 });
//				$("#structTemplet_"+json.studyid).find("input").each(function(index,obj){
//					 var fieldCode = $(obj).attr("code");
//					 var value = eval('json.' + fieldCode);
//					 $(obj).val(value);
//					 if(row.reportstatusdisplaycode=="R"){
//						 $("#structTemplet_"+json.studyid + " #" + fieldId).attr('onfocus','this.blur()');
//						 $("#structTemplet_"+json.studyid + " select").attr('disabled','disabled');
//					 }
//				 });
//				$("#structTemplet_"+json.studyid).find("select").each(function(index,obj){
//					 var fieldCode = $(obj).attr("code");
//					 var value = eval('json.' + fieldCode);
//					 for(var i=0; i<obj.options.length; i++){  
//					        if(obj.options[i].innerHTML == value){  
//					            obj.options[i].selected = true;  
//					            break;  
//					        }  
//					    }  
//					 if(row.reportstatusdisplaycode=="R"){
//						 $("#structTemplet_"+json.studyid + " #" + fieldId).attr('onfocus','this.blur()');
//						 $("#structTemplet_"+json.studyid + " select").attr('disabled','disabled');
//					 }
//				 });
//				
//				if(row.reportstatusdisplaycode=="R"){
//					 $("#structTemplet_"+json.studyid).prepend("<img alt='' src='/img/flag.png' style='position:absolute;left:150px;top:10px;z-index:9999;'>")
//				 }
//			}
//			
//		});
//}

function lock_report(row){
//	$.getJSON(window.localStorage.ctx+'/worklist/lockReport?studyid='+ row.studyorderstudyid,function(data){
		$('#dg').datagrid('updateRow',{
			index:$('#dg').datagrid('getRowIndex',row),
			row:{
				locking:$.i18n.prop('wl.locked'),
				lockingpeople:$('#name_hidden').val(),
				islocking:1
			}
		});
//	});
}



function destroyUeditor(row){
	var ue_desc = UM.getEditor('desc_'+ row.reportid+ '_html');
	var ue_result = UM.getEditor('result_'+ row.reportid+ '_html');
	if (ue_desc){
		ue_desc.destroy();
	    ue_result.destroy();
	}
}


//关闭检查报告弹窗
function closeHistoryRecord(){
	$('#details').window('close');
}
//图像
function loadImages() {
	    
	var row = $("#dg").datagrid("getSelected");
	if (row) {
		alert("图像");
	}else{
		$.messager.show({
			title : '提醒',
			msg : "请选择检查！",
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
	}

}

var nowPageNumber = 1;
var nowPageSize = 50;
var nowflag = false;
//刷新
function refresh(timingTaskFlag) {
	console.log("searchForm_flag="+searchForm_flag);
//	var p = $('#w_s_accordion').accordion('getSelected');
//	var index = $('#w_s_accordion').accordion('getPanelIndex', p);

	
//	if(index==1){
//		var node =$('#myfavorites_tree_search').tree('getSelected');
//		if(node){
//			findStudyByFavoritesId(node.id);
//		}
//	}
//	else{
//		
//	}
	
	var options = $("#dg" ).datagrid("getPager" ).data("pagination" ).options;
	nowPageNumber = options.pageNumber;
	nowPageSize = options.pageSize;
	nowflag = true;
	
	if(searchForm_flag=="searchForm"){
		sumbitSearchForm(nowPageNumber, nowPageSize, "", "", timingTaskFlag);
	}else if(searchForm_flag=="labelSearchForm"){
		labelSearch(nowPageNumber, nowPageSize, timingTaskFlag);
	}else if(searchForm_flag=="fulltextSearchForm"){
		fulltextSearch(nowPageNumber, nowPageSize, timingTaskFlag);
	}
	else if(searchForm_flag=="quicksearch"){
		sumbitSearchForm(nowPageNumber, nowPageSize, $('#quicksearch-input').searchbox('getValue'), $('#quicksearch-input').searchbox('getName'), timingTaskFlag);
	}
	else{
		var node =$('#myfavorites_tree_search').tree('getSelected');
		if(node){
			findStudyByFavoritesId(node.id);
		}
	}
	$('#remark').textbox('setValue', '');
	$('#reportcontent').html('');
}

//删除
function deleteSR(){
     
	var row = $("#dg").datagrid("getSelected");
	if (row!=null) {
		console.log(row.islocking);
		if(row.islocking==1){
			$.messager.show({
				title : $.i18n.prop('alert'),msg : '当前报告已被锁定',timeout : 3000,border: 'thin',showType : 'slide'
			});
			return ;
		}
		if(row.reportstatusdisplaycode==myCache.ReportStatus.FinalResults){
    		$.messager.show({title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.reporthasauditcannotbedelete'),timeout : 2000,border : 'thin',showType : 'slide'});
    		return;
    	}
    	if(!row.reportid){
    		_message("报告未创建，无法删除！", "提醒");
    		return;
    	}
		$.messager.confirm({
			title : $.i18n.prop('confirm'),msg : '是否删除选中的报告信息', border: 'thin',
			fn : function(r){
		    if (r){
				getJSON(window.localStorage.ctx+"/worklist/deleteSR",
					{
						orderid : row.orderid,
						studyid : row.studyorderstudyid,
						reportid : row.reportid
					},
					function(data) {
						var result=validationData(data);
						if(result.code==0){
							$.messager.show({title : $.i18n.prop('tips'),msg : $.i18n.prop('deletesuccess'),timeout : 1000,border : 'thin',showType : 'slide'});
							$("#dg").datagrid("deleteRow",$("#dg").datagrid("getRowIndex",row));
						} else if(result.code==1){
							_message(data.message, "提醒");
						} else{
							_message("删除失败！"+data.message, "提醒");
						}
					}
				);
		    }
		}});
	}else{
		$.messager.show({title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.selectonestudy'),timeout : 3000,border : 'thin',showType : 'slide'});
	}
}

//详情信息
function details(){  
	var row = $("#dg").datagrid("getSelected");
	if (row) {
		$.ajax({
			url : window.localStorage.ctx+"/worklist/viewDetails",
			contentType : "application/x-www-form-urlencoded; charset=UTF-8",
			dataType : 'json',
			data : {
				patientid : row.patientid
			},
			success : function(data) {
				$('#details').dialog({
					title : '详情信息',
					width : 1000,
					height : 500,
					border:'thin',
					closed : false,
					cache : false,
					href : window.localStorage.ctx+'/worklist/detailsView',
					modal : true,
					onLoad:function(){
						document.getElementById("_patientid").value=data[0].patientid;
						document.getElementById('_patientname').value=data[0].patientname;
						document.getElementById('_py').value=data[0].py;
						document.getElementById('_sexdisplay').value=data[0].sexdisplay;
						document.getElementById('_telephone').value=data[0].telephone;
						document.getElementById('_modality_type').value=data[0].modality_type;
						document.getElementById('_age').value=data[0].age;
						document.getElementById('_bedno').value=data[0].bedno;
						document.getElementById('_address').value=data[0].address;
						document.getElementById('_birthdate').value=data[0].birthdate;
						document.getElementById('_cardno').value=data[0].cardno;
						document.getElementById('_inno').value=data[0].inno;
						document.getElementById('_outno').value=data[0].outno;
						document.getElementById('_wardno').value=data[0].wardno;
						document.getElementById('_studyitems').value=data[0].studyitems;
						document.getElementById('_studyid').value=row.studyorderstudyid;
						document.getElementById('_patientsource').value=data[0].patientsource;
						document.getElementById('_priority').value=data[0].priority;
						document.getElementById('_modality').value=data[0].modality;
						document.getElementById('_appdept').value=data[0].appdept;
						document.getElementById('_appdoctor').value=data[0].appdoctor;
						document.getElementById('_modality').value=data[0].modality;
						document.getElementById('_remark').value=data[0].remark;
					}
				});
				$('#details').dialog('refresh',window.localStorage.ctx+'/worklist/detailsView');
			}
			});
	
	}else{
		$.messager.show({
			title : '提醒',
			msg : "请选择检查！",
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
	}
}

/**
 * 给申请单赋值
 * @param row
 * @returns
 */
var request_reportid;
function getImage(row){
	console.log("======getImage======");
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
 * 获取常用列表数据
 * 
 * @returns
 */
//function getFillParamsVal() {
	    
//	$('#fillParams').combobox({
//		url : '/worklist/getFilters',
//		valueField : 'id',
//		textField : 'description',
//		editable : false,
//		icons : [ {
//			iconCls : 'icon-clear'
//		} ],
//		onClickIcon : function() {
//			$('#fillParams').combobox('clear')
//		},
//		onChange : (fillParams)
//	});
//	
//	$.getJSON("/worklist/getFilters",function(json) {
//		$('#fillParams').combobox('reload', json)
//	
//	});
//	
//	
//	$('#fillParams').combobox('reload','/worklist/getFilters');
//	
//}

/**
 * 常用查询条件填充
 * 
 * @returns
 */
function fillParams(row) {


	if(row==null)
	row=$('#myfilterlist_worklist').datalist('getSelected');
	console.log(row);
	if(!row)return;

	//form中存在多选下拉框时，如果对应值为null,在加载前必须置为'',不然会报错。
	$("#searchForm_worklist").form('load',row);

	$('#searchForm_worklist').find('.easyui-combobox').each(function(index,element){
		if($(element).combobox('options').multiple && $(element).combobox('getValues').length == 1
				&& $(element).combobox('getValues')[0] == ""){
			console.log($(element).attr('id'));
			$(element).combobox('clear');
		}

	});
	
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
 * 我的收藏报告
 * @returns
 */
function openMyFavoritesDialog(){
     
	 $('#common_dialog').dialog(
				{
					title : $.i18n.prop('wl.favoritesreport'),
					width : 400,height : 450,
					closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,
					border: 'thin',
					href : window.localStorage.ctx+'/report/goMyFavorites',
					modal : true,
					buttons:[{
						text: $.i18n.prop('wl.openreport'),
						width:80,
						handler:function(){addFavoritesNode('0');}
					},{
						text: $.i18n.prop('cancel'),
						width:60,
						handler:function(){$('#common_dialog').dialog('close');}
					}]
		});
}


/**
 * 使用默认模板
 * @returns
 */
//function useDefault(patientname,studyid){
//   
//	var templetId = $("#templetId_"+studyid).val();
//	if(templetId==0){
//		$.messager.alert("提示","已经是默认模板,请重新选择");
//		return;
//	}
//	var row=validationData($("#value__"+studyid).attr("value"));
//	$("#choose_Templet").window("close");
//	changeTemplet(patientname,studyid,row,'default');
//}

/**
 * 使用自定义模板
 * @returns
 */
//function useStruct(patientname,studyid){
//    
//	var node = $("#templets").tree("getSelected");
//	if(!node){
//		$.messager.alert("提示","请先选中一个模板");
//		return;
//	}
//	var templetId = $("#templetId_"+studyid).val();
//	var row="";
//	
//	if(templetId !=0){
//		//row=JSON.parse($("#value__"+studyid).attr("value"));
//		row = validationData($("#value__"+studyid).attr("value"))
//	} else{
//		row = validationData($("#value_"+studyid).attr("value"));
//	}
//	$("#choose_Templet").window("close");
//	changeTemplet(patientname,studyid,row,node);
//}
/**
 * 初始化模板列表
 * 
 * @returns
 */
//function initTempletsTree(){
//
//	var name = $("#templetName").val();
//	$("#templets").tree({
//		url: window.localStorage.ctx+'/templet/getTree?name='+name,
//	})
//}
/**
 * 切换模板的方法
 * @param obj
 * @returns
 */
//function changeTemplet(patientname,studyid,row,obj){
// 
//	$("#tab").tabs("close",patientname + '_' + studyid);
//	if(obj == 'default'){
//		row.templetid=0;
//		report(row);
//    }else{
//    	var name = patientname + '_' + studyid;
//    	var url = window.localStorage.ctx+"/templet/useStruct?studyid=" + studyid + "&patientname=" + patientname;
//        var dd = $('#tab').tabs('exists',name);  
//        if(dd){  
//            $('#tab').tabs('select',name);  
//        }else{  
//        	var content = '<iframe scrolling="auto" frameborder="0"  src="'+url+'" style="width:100%;height:100%;"></iframe>';  
//	        $('#tab').tabs('add',{  
//	            id: name,  
//	            title: name,  
//	            href: url,      //使用content的iframe方式：将整个页面嵌入到tab页内。  缺点：js重复加载  
//	            closable: true ,
//	            onLoad : function(){
//	            	$("#value__"+row.studyorderstudyid).attr("value",JSON.stringify(row));
//	            	  if(row.reportstatusdisplaycode=="O"){
//							$("#pre1_"+row.studyorderstudyid).hide();
//							$("#audi1_"+row.studyorderstudyid).hide();
//							$("#reR1_"+row.studyorderstudyid).hide();
//							$("#showRe1_"+row.studyorderstudyid).hide();
//						}
//						if(row.reportstatusdisplaycode=="P"){
//							$("#audi1_"+row.studyorderstudyid).hide();
//							$("#reR1_"+row.studyorderstudyid).hide();
//							$("#saveR1_"+row.studyorderstudyid).hide();
//							$("#templet_"+row.studyorderstudyid).hide();
//						}
//						if(row.reportstatusdisplaycode==myCache.ReportStatus.FinalResults){
//							$("#audi1_"+row.studyorderstudyid).show();
//							$("#reR1_"+row.studyorderstudyid).show();
//							$("#pre1_"+row.studyorderstudyid).hide();
//							$("#saveR1_"+row.studyorderstudyid).hide();
//							$("#templet_"+row.studyorderstudyid).hide();
//						}
//						if(row.reportstatusdisplaycode=="RE"){
//							$("#audi1_"+row.studyorderstudyid).hide();
//							$("#reR1_"+row.studyorderstudyid).hide();
//							$("#pre1_"+row.studyorderstudyid).show();
//							$("#saveR1_"+row.studyorderstudyid).hide();
//							$("#templet_"+row.studyorderstudyid).hide();
//						}
//						if(row.reportstatusdisplaycode=="R"){
//							$("#audi1_"+row.studyorderstudyid).hide();
//							$("#reR1_"+row.studyorderstudyid).hide();
//							$("#pre1_"+row.studyorderstudyid).hide();
//							$("#saveR1_"+row.studyorderstudyid).hide();
//							$("#templet_"+row.studyorderstudyid).hide();
//						}
//	            	$("#structTemplet_"+studyid).html(obj.attributes.content);
//	            	$("#structTemplet_"+studyid).find("label").each(function(index,obj){
//						 var fieldId = $(obj).attr("for");
//						 $("#structTemplet_"+studyid + " #" + fieldId).removeAttr("disabled");
//					 });
//	            	$("#structTemplet_"+studyid).children("div").each(function(index,obj){
//						 $(obj).removeAttr("disabled").removeAttr("draggable").removeAttr("ondblclick").removeAttr("ondrop").removeAttr("ondragover").removeAttr("ondragstart");
//					 });
//	            	$("#templetId_"+studyid).val(obj.id);
//	            	$.ajax({
//	            		url:window.localStorage.ctx+'/report/getStructReport',
//	            		type:'Post',
//	            		data:{studyid:row.reportid},
//	            		success:function(json){
//	            			if(json!=null){
//	            				$("#structTemplet_"+studyid).find("label").each(function(index,obj){
//									 var fieldId = $(obj).attr("for");
//									 var fieldCode = $("#" + fieldId).attr("name");
//									 var value = eval('json.' + fieldCode);
//									 $("#structTemplet_"+studyid +"#" + fieldId).val(value);
//								 });
//	            			}
//	            			
//	            			
//	            		}
//	            	})
//	            }
//	        	});  
//	        }  
//    }
//}

/*function reportPreview(){
	var row=$("#dg").datagrid("getSelected");
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

/**
 *  报告查询页面---报告预览
 */
function previewReport(reportid,select_fontsize) {
	var reportstatus=null;
	if(reportid==null){
		var row = $("#dg").datagrid("getSelected");
		if (row==null) {
			_message('请选择一条数据!');
			return;
		}
		reportid=row.reportid;
		reportstatus=row.reportstatusdisplaycode;
	} else{
		reportstatus=$('#orderStatus_'+reportid).val()
	}
	//判断报告是否创建
	if (reportstatus == myCache.ReportStatus.Noresult) {
		_message('尚未创建报告，无法打印预览！' , $.i18n.prop('error'));
		return;
	}
	window.open(window.localStorage.ctx+"/print/printReport?reportid="+reportid+"&select_fontsize="+(select_fontsize||false)
			,"_blank ","width="+$(window).width()+",height="+$(window).height(),true);
}
/**
 * 打印界面
 * 
 * @param reportid
 * @returns
 */
function print(reportid) {
	if($('#id_' + reportid).val()){
		 window.open(window.localStorage.ctx+"/print/printReport?reportid="+$('#id_' + reportid).val(),
				 "_blank ","width=1000,height=700",true);
	}
	else{
		_message('尚未创建报告，无法打印！', $.i18n.prop('error'));
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
function printReport(projecturl,reportid,ptemp,issr,fontSize){
	if(!projecturl && !reportid){
		var row=$("#dg").datagrid("getSelected");
		if (row!=null && row.reportid) {
			if (myCache.ReportStatus.FinalResults == row.reportstatusdisplaycode) {
				// 
				getJSON( window.localStorage.ctx+'/report/getPrintReportInfo',
						{
							reportid : row.reportid
						},
						function(json){
							if(json.code==0){
								closeReportFlag=false;
								urlFlag = true;
								var href="reporttool:-c print -t "+json.data.templatename+" -l "+json.data.projecturl+" -n "+$('#reportprintUserid_wl').val()+" -m reportid="+json.data.reportid+"&issr="+json.data.issr;
								if(electron_enable()){
									electron_print(href);
								} else {
									$('#printReport_worklist')[0].setAttribute('href',href);
							    	$('#printReport_worklist')[0].click();
								}
							} else {
								_message(json.message , '提醒');
							}
						});
			} else {
				_message("只有已审核的报告才能打印！");
			}
		} else {
			_message('请选择一条已有报告的数据' , '提醒');
		}
	} else {
		if ($('#id_' + reportid).val() && $('#orderStatus_'+reportid).val() == myCache.ReportStatus.FinalResults) {
			if($('#report_printmore').val() == 1){
				//判断报告是否打印过，打印过提示
				getJSON(window.localStorage.ctx+"/report/getReportPrintCount",
					{
						reportid:reportid
					},
					function(json){
						var data = validationDataAll(json);
						if(data.code==0){
							if(data.data > 0){
								$.messager.confirm({
									title:'提示',
									ok:'是',
									cancel:'否',
									border:'thin',
									msg:'报告已被打印，是否继续打印？',
									fn: function(r){
										if (r){
											closeReportFlag=false;
											urlFlag = true;
											if(issr==1 && isBlank(ptemp)){
												ptemp = $('#srtemplatename_'+reportid).val();
											}
											var href="reporttool:-c print -t "+ptemp+" -l "+projecturl+" -n "+$('#reportprintUserid_wl').val()+" -m reportid="+$('#id_' + reportid).val()+"&issr="+issr+"&fontSize="+fontSize;
											if(electron_enable()){
												electron_print(href);
											} else {
												$("#printApp_"+reportid)[0].setAttribute('href',href);
										    	$("#printApp_"+reportid)[0].click();
											}
										}
									}
								});
							}else {
								closeReportFlag=false;
								urlFlag = true;
								if(issr==1 && isBlank(ptemp)){
									ptemp = $('#srtemplatename_'+reportid).val();
								}
								var href="reporttool:-c print -t "+ptemp+" -l "+projecturl+" -n "+$('#reportprintUserid_wl').val()+" -m reportid="+$('#id_' + reportid).val()+"&issr="+issr+"&fontSize="+fontSize;
								if(electron_enable()){
									electron_print(href);
								} else {
									$("#printApp_"+reportid)[0].setAttribute('href',href);
							    	$("#printApp_"+reportid)[0].click();
								}
							}
						}
						
				});
			}else {
				closeReportFlag=false;
				urlFlag = true;
				if(issr==1 && isBlank(ptemp)){
					ptemp = $('#srtemplatename_'+reportid).val();
				}
				var href="reporttool:-c print -t "+ptemp+" -l "+projecturl+" -n "+$('#reportprintUserid_wl').val()+" -m reportid="+$('#id_' + reportid).val()+"&issr="+issr+"&fontSize="+fontSize;
				if(electron_enable()){
					electron_print(href);
				} else {
					$("#printApp_"+reportid)[0].setAttribute('href',href);
			    	$("#printApp_"+reportid)[0].click();
				}
			}
		} else {
			_message('只有已审核的报告才能打印！' , $.i18n.prop('error'));
		}
	}	
}

//取消最终审核
function cancelAudiReport(){
	var reportid=null;
	var reportStatus=null;
	var tabid=null;
	var templateid=null;
	var tab = $('#tab').tabs('getSelected');
	var index = $('#tab').tabs('getTabIndex',tab);
	
	if(index==0){
		var row = $("#dg").datagrid("getSelected");
		if(row){
		
			if(row.islocking==1){
				$.messager.show({
					title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.reportislockedandcannotbecanceled'),timeout : 3000,border: 'thin',showType : 'slide'
				});
				return ;
			}
			
			reportid=row.reportid;
			reportStatus=row.reportstatusdisplaycode;
			templateid=row.templateid;
			tabid="tab_"+reportid;
		}
	}
	else{
		tabid=tab.panel('options').id;
		reportid=tabid.substr(tabid.lastIndexOf("_")+1);
		reportStatus=$("#orderStatus_"+ reportid).val();
		templateid=$("#srtemplateid_"+ reportid).val();
	}
		var html=null;
		if(reportid){
			if(reportStatus==myCache.ReportStatus.FinalResults){
				$.messager.confirm({title: $.i18n.prop('confirm'),border: 'thin',msg: $.i18n.prop('wl.confirmcancelaudit'),fn: function(r){
					if(r){
						$('#progress_dlg').dialog('open');
						getJSON(window.localStorage.ctx+'/report/cancelAudiReport?reportid='+reportid,null,function(data){
								var json = validationDataAll(data);
//								console.log(json);
								if(json.code==0){
									var reportstatus=json.data.reportstatus;
									changeReportBtnEnable(reportstatus,reportid);
									
									if($("#auditphysician_" + reportid)[0]){//常规报告
										$("#auditphysician_" + reportid).empty();
										$("#audittime_" + reportid).empty();
									} else{//结构化报告
										$('#sr_container_' + reportid).find("input[code='AuditDatetime'],input[code='AuditPhysicianName']").each(function(index,obj){
											if($(obj).attr('code')=='AuditDatetime'){
												$(obj).attr('value','[AuditDatetime]');
											} else{
												$(obj).attr('value','[AuditPhysicianName]');
											}
										});
										$('#sr_container_' + reportid).find("img[esign='AuditPhysicianName']").attr('src','');
									}
									enableEditReport(reportstatus,reportid);
									if(json.data.templateid>0){
										afterCancelAudiReport(reportid,json.data.checkresult,json.data.checkdesc);
									}
									//_message($.i18n.prop('wl.reportcancelauditsuccess'));
									//refreshReportInfo(reportid, reportstatus);
									//refresh();
									refreshRow(reportid);
								}else if(json.code==1){
									_message(json.message, "提醒");
								}else{
									_message($.i18n.prop('wl.reportcancelauditfailed') , $.i18n.prop('error'));
								}
								$('#progress_dlg').dialog('close');
								
							
						});
					}
				}
				});
			}else{
				_message($.i18n.prop('wl.reportnotbeauditedandcannotbecancel') , $.i18n.prop('alert'));
			}
		}else{
			_message($.i18n.prop('wl.selectonereport') , $.i18n.prop('alert'));
		}
}

//取消审核后，如果是结构化报告，更新报告内容和打印内容
function afterCancelAudiReport(reportid,checkresult,checkdesc){
	if(checkresult==null&&checkdesc==null){
		return;
	}
	var result=$('<div>'+checkresult+'</div>');
	result.find("img[esign='AuditPhysicianName'],input[code='AuditDatetime'],input[code='AuditPhysicianName']").each(function(index,obj){
		if($(obj).attr('code')=='AuditDatetime'){
			$(obj).attr('value','[AuditDatetime]');
			$(obj).attr('data-clipboard-text','[AuditDatetime]');
		} else if($(obj).attr('code')=='AuditPhysicianName'){
			$(obj).attr('value','[AuditPhysicianName]');
			$(obj).attr('data-clipboard-text','[AuditPhysicianName]');
		} else if($(obj).attr('esign')=='AuditPhysicianName'){
			var data={
					"code":"AuditPhysicianName",
					"scheme":"SYSTEM",
					"meaning":"AuditPhysicianName"
			};
			$(obj).replaceWith(pluginHandle.getClinicalCodeComponentHtml(data))
		}
	});
	
	var print=$('<div>'+checkdesc+'</div>');
	print.find("img[esign='AuditPhysicianName'],span[code='AuditDatetime'],span[code='AuditPhysicianName']").each(function(index,obj){
		if($(obj).attr('esign')=='AuditPhysicianName'){
			$(obj).replaceWith('<span style="width:'+($(obj).width())+'px;display:inline-block;"></span>');
		} else{
			$(obj).html("");
		}
	});
	
	getJSON(window.localStorage.ctx+'/report/afterCancelAudiReport',{
		"reportid":reportid,
		"checkresult":result.html(),
		"checkdesc":print.html()
	},function(data){
		result.remove();
		print.remove();
		console.log(data)
		if(data.code!=0){
			_message($.i18n.prop('wl.reportcancelauditfailed') , $.i18n.prop('error'));
		}
	});
}

//取消锁定
function cancelLock(){
	var row = $("#dg").datagrid("getSelected");
	if(row){
//		console.log("locking:"+row.islocking);
		if(row.islocking==1 && row.reportid){
			var tableIndex = getTabIndexByReportid(row.reportid);
//			console.log(tableIndex);
			if(tableIndex){
				return;
			}
			$.messager.confirm({
				title: $.i18n.prop('confirm'),
				border:'thin',
				msg: $.i18n.prop('wl.confirmunlock'),
				fn: function(r){
					if(r){
						$('#progress_dlg').dialog('open');
						$.post(
								window.localStorage.ctx+'/report/cancelBlock',
								{reportid:row.reportid},
								function(data){
									if(data.code==0){
										$.messager.show({
											title : $.i18n.prop('tips'),msg : $.i18n.prop('wl.unlocksuccessful'),timeout : 3000,border: 'thin',showType : 'slide'
										});
									}else if(data.code==1){
										_message(data.message, "提醒");
									}else{
										$.messager.show({
											title : $.i18n.prop('error'),msg : $.i18n.prop('wl.unlockedfailed'),timeout : 3000,border: 'thin',showType : 'slide'
										});
									}
									$('#progress_dlg').dialog('close');
									//refresh();
									refreshRow(row.reportid);
								}
						);
					}
				}
			});
		}
	}else{
		$.messager.show({
			title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.selectonereport'),timeout : 3000,border: 'thin',showType : 'slide'
		});
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

function openImageAtOnce(row,flag){
	if(flag=='1'){
		/*if($('#callupbtn_plaza')[0]){
			//$('#callupbtn_plaza')[0].click();
			openStudyImage("plazapara_worklist");
		}*/
		if($('#callupbtn_via')[0]){
			//$('#callupbtn_via')[0].click();
			openStudyImage("viapara_worklist");
		}
	}
}

function detailFormatter_wl_previewreport(index,row){
	return '<div class="ddv" style="padding:5px 0"></div>';
}

function priorityFormat(val, row, index){
	var style = "";
	/*if(row.priority == 'V'){
		style += '<div style="height: 18px; width: 18px; background-color: red; border-radius: 9px;"><div style="height: 9px; width: 9px; background: white; border-radius: 5px;position: relative;left: 4.5px; top: 4.5px;"></div></div>';
	}*/
	if(row.precedence == 2){
		style += '<img src="image/warning.png" width="16" heigth="16">';	
	}
	return style;
}

function onExpandRow_wl_previewreport(index,row){
	if(row.vipflag == 1 && row.reportstatusdisplaycode == myCache.ReportStatus.FinalResults
			&& $('#vip_flag')[0] && $('#vip_flag').val() != 1){
		return;
	}
	if (row.reportid && row.reportstatusdisplaycode != myCache.ReportStatus.Noresult) {
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
function columeStyler_orderstatus_wl(value,row,index) {
	var color = myCache.status_color['0005_'+row.status];
	if (color && color == "#ffffff") {
		return "<span style='color: "+color+"'>" + value + "</span>"
	} else if (color) {
		return 'background-color:'+color+';';//color:red;
	} else {
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

function columeStyler_studyid(val,row,index) {
	if (row.patientsource == 'E') {
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
 *  打开申请单
 * @returns
 */
function openApplyForm() {
	var row=$("#dg").datagrid("getSelected");
	if (row!=null) {
		apply(row.orderid,'worklist');
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
 *  检查标签查询
 * @returns
 */
function checkSearchLabel() {
	var checkResult = true;
	var publiclabel_tree = $('#publiclabel_tree').combobox('getValue'); // 公共标签
	var privatelabel_tree = $('#privatelabel_tree').combobox('getValue'); // 个人标签
	var datetype2 = $('#datetype2').combobox('getValue'); // 时间
	
	// T：今天；Y：昨天；TD：近三天；FD：近五天， W：近一周， TM：近三个月
	var appdate2 = $('#appdate2').val();
	var datefrom2 = $('#datefrom2').datebox('getValue'); // 开始时间
	var dateto2 = $('#dateto2').datebox('getValue');  // 结束时间
	if (datefrom2 == "" && dateto2 == "" && appdate2 == "") {
		checkResult = false;
		$.messager.show({
	        title:'提醒',
	        msg:'请选择需要查询的具体一个时间段',
	        timeout:3000,
	        border:'thin',
	        showType:'slide'
	    });
	}
	if (datefrom2 != "" && dateto2 == "") {
		checkResult = false;
		$.messager.show({
	        title:'提醒',
	        msg:'结束时间不能为空',
	        timeout:3000,
	        border:'thin',
	        showType:'slide'
	    });
	}
	if (datefrom2 == "" && dateto2 != "") {
		checkResult = false;
		$.messager.show({
	        title:'提醒',
	        msg:'开始时间不能为空',
	        timeout:3000,
	        border:'thin',
	        showType:'slide'
	    });
	}
	return checkResult;
}

/**
 * 标签查询
 * @returns
 */
function labelSearch(pageNum, pageSi, timingTaskFlag) {
	if (checkSearchLabel()) {
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
		$('#labelSearchForm').form({
			url : window.localStorage.ctx+"/worklist/findStudyByLabel?page="+ pageNum +"&rows="+ pageSi,
			onSubmit : function() {
				if(!timingTaskFlag){
					$('#progress_dlg').dialog('open');
				}
				var datefrom = $("#datefrom2").datebox("getValue");  
			    var dateto = $("#dateto2").datebox("getValue");  
				if(datefrom != ''&&!validator(datefrom)){
					$('#progress_dlg').dialog('close');
					$.messager.show({
						title : $.i18n.prop('error'),
						msg : $.i18n.prop('filter.starttimeformaterror'),
						timeout : 1000,
						border:'thin',
						showType : 'slide'
					});
					return false;
				}
				if(dateto!=''&&!validator(dateto)){
					$('#progress_dlg').dialog('close');
					$.messager.show({
						title : $.i18n.prop('error'),
						msg : $.i18n.prop('filter.endtimeformaterror'),
						timeout : 1000,
						border:'thin',
						showType : 'slide'
					});
					return false;
				}
				$('#publiclabel2').val($('#publiclabel_tree').combotree('getValues'));
				$('#privatelabel2').val($('#privatelabel_tree').combotree('getValues'));
				$('#icd10label_').val($('#icd10labelct_').combobox('getValues'));
				searchForm_flag="labelSearchForm";
			},
			success : function(data) {
					$('#progress_dlg').dialog('close');
					$("#dg").datagrid("loadData", validationData(data));
					if(nowflag){
						var p = $('#dg').datagrid('getPager'); 
						 $(p).pagination({ 
							 pageNumber:nowPageNumber,
						 });
						 nowflag = false;
					} 
			}
		});
		$("#labelSearchForm").submit();
	}
}

//清除标签查询的条件
function clearLabelSearch(){
	$('#publiclabel_tree').combotree('clear');
	$('#privatelabel_tree').combotree('clear');
	$('#icd10labelct_').combobox('clear');
	$('#publiclabel2').val('');
	$('#privatelabel2').val('');
	
	$('#datefrom2').datebox('setValue', '');
	$('#dateto2').datebox('setValue', '');
	
	$('#today2').linkbutton({selected:false});
	$('#yesterday2').linkbutton({selected:false});
	$('#threeday2').linkbutton({selected:false});
	$('#fiveday2').linkbutton({selected:false});
	$('#week2').linkbutton({selected:false});
	$('#month2').linkbutton({selected:false});
	$('#appdate2').val('');
//	$('#datetype2').combobox('setValues', 'studytime');
}

/**
 * 全文检索
 * @returns
 */
function fulltextSearch(pageNum, pageSi, timingTaskFlag) {
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
	$('#fulltextSearchForm').form({
		url : window.localStorage.ctx+"/worklist/findStudyByFulltext?page="+ pageNum +"&rows="+ pageSi,
		onSubmit : function() {
			if(!timingTaskFlag){
				$('#progress_dlg').dialog('open');
			}
			var datefrom=	$("#datefrom3").datebox("getValue");  
		    var dateto=	$("#dateto3").datebox("getValue");  
			if(datefrom!=''&&!validator(datefrom)){
				$('#progress_dlg').dialog('close');
				$.messager.show({
					title : $.i18n.prop('error'),
					msg : $.i18n.prop('filter.starttimeformaterror'),
					timeout : 1000,
					border:'thin',
					showType : 'slide'
				});
				return false;
			}
			if(dateto!=''&&!validator(dateto)){
				$('#progress_dlg').dialog('close');
				$.messager.show({
					title : $.i18n.prop('error'),
					msg : $.i18n.prop('filter.endtimeformaterror'),
					timeout : 1000,
					border:'thin',
					showType : 'slide'
				});
				return false;
			}
			
			
			var plaintext = $("#plaintext").textbox('getValue').trim();
			if(!plaintext){
				$('#progress_dlg').dialog('close');
				$.messager.show({
					title : $.i18n.prop('error'),
					msg : "请输入关键字",
					timeout : 1000,
					border:'thin',
					showType : 'slide'
				});
				return false;
			}
			
			// a-zA-Z字母;0-9数字;\u4e00-\u9fa5中文;
			//不能使用逗号开始或结束
			var reg1 = new RegExp("(^[,，])|([,，]$)");
			if(reg1.test(plaintext)){
				$('#progress_dlg').dialog('close');
				$.messager.show({
					title : $.i18n.prop('error'),
					msg : "非法参数，不能使用逗号开始或结束",
					timeout : 3000,
					border:'thin',
					showType : 'slide'
				});
				return false;
			}
			
			//只能使用一种规则
			var reg2 = new RegExp("(\\S+)[,，](\\S+)");
			var reg3 = new RegExp("\\s+");
			var regex = new RegExp("( *[,，]+ *)");
			console.log(regex.test(plaintext));
			var str = plaintext.replace(/( *[,，]+ *)/g,',');
			console.log(str);
			if(reg2.test(str)&&reg3.test(str)){
				$('#progress_dlg').dialog('close');
				$.messager.show({
					title : $.i18n.prop('error'),
					msg : "只能使用一种规则",
					timeout : 1000,
					border:'thin',
					showType : 'slide'
				});
				return false;
			}
			
			//判断是否存在"
			var reg4 = new RegExp("[\"]+");
			if(reg4.test(plaintext)){
				$('#progress_dlg').dialog('close');
				$.messager.show({
					title : $.i18n.prop('error'),
					msg : "非法参数",
					timeout : 1000,
					border:'thin',
					showType : 'slide'
				});
				return false;
			}
			
			searchForm_flag="fulltextSearchForm";
		},
		success : function(data) {
				$('#progress_dlg').dialog('close');
				$("#dg").datagrid("loadData", validationData(data));
				if(nowflag){
					var p = $('#dg').datagrid('getPager'); 
					 $(p).pagination({ 
						 pageNumber:nowPageNumber,
					 });
					 nowflag = false;
				} 
		}
	});
	$("#fulltextSearchForm").submit();
}

//清除全文检索的条件
function clearFulltextSearch(){
	$("#plaintext").textbox('setValue', '');
	$('#modality3').combobox('clear');
	
	$('#datefrom3').datebox('setValue', '');
	$('#dateto3').datebox('setValue', '');
	
	$('#today3').linkbutton({selected:false});
	$('#yesterday3').linkbutton({selected:false});
	$('#threeday3').linkbutton({selected:false});
	$('#fiveday3').linkbutton({selected:false});
	$('#week3').linkbutton({selected:false});
	$('#month3').linkbutton({selected:false});
	$('#appdate3').val('');
//	$('#datetype3').combobox('setValues', 'studytime');
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
	// 1、根据查询条件获取 所有报告状态为 已审核 的数据
	var result = true;
	if (result) {
		result = false;
		$.ajax({
			//不同步的话，它导出的时候会有延迟，如果在延迟中再点击导出，它就会再导出一遍
	        async: false,
	        type: "POST",
	        url: window.localStorage.ctx + "/worklist/getBatchExportReport?reportstatus="+myCache.ReportStatus.FinalResults+'&page=1&rows=500',
	        contentType : "application/x-www-form-urlencoded; charset=utf-8",
	        data:$("#searchForm_worklist").serialize(),
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
	        			$('#progress_dlg').dialog('close');
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
	        		$('#progress_dlg').dialog('close');
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
 * 为当前打开检查，新建报告
 * @returns
 */
function addOtherReport(){
	var tab = $('#tab').tabs('getSelected');
	var index = $('#tab').tabs('getTabIndex',tab);
	if(index==0){
		return;
	}
	var reportid=null;
	var reportStatus=null;
	var tabid=null;
	tabid=tab.panel('options').id;
	reportid=tabid.substr(tabid.lastIndexOf("_")+1);
	reportStatus=$("#orderStatus_"+ reportid).val();
	var patientname=tab.panel("options").title;
	var orderid=$("#orderid_"+reportid).val();
	var studyid=$("#studyid_"+reportid).val();
	
	var row = {
			patientname : patientname,
			reportstatusdisplaycode : myCache.ReportStatus.Noresult,
			orderid : orderid,
			studyorderstudyid : studyid
	}
	
	if(reportid){
		getJSON(window.localStorage.ctx+"/report/doReportCreating",
				{
					orderid : row.orderid,
					studyid : row.studyorderstudyid,
					other : 'other'
				},
				function(json){
					var result = validationDataAll(json);
					if(result.code==0 && result.data!=0){
						beforeOpenReport(row,result.data,0,false);
						refresh();
						
					}else{
						$.messager.show({
					        title:'提醒',
					        msg:'新建失败请重试，如果问题依然存在，请联系系统管理员！',
					        timeout:3000,
					        border:'thin',
					        showType:'slide'
					    });
					}
				});
	}else{
		$.messager.show({
			title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.selectonereport'),timeout : 3000,border: 'thin',showType : 'slide'
		});
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
 * 监听快捷键事件
 * @returns
 */
$(document).ready(
	function() {
		document.onkeydown = function() {
			var oEvent = window.event;
			if (oEvent.ctrlKey && oEvent.keyCode == 81) {
				// 打开申请单, Ctrl + Q
				openApplyForm();
			} else if (oEvent.ctrlKey && oEvent.keyCode == 73) {
				// 打开图像（p）, Ctrl + i;
				openStudyImage('plazapara_worklist');
			} else if (oEvent.ctrlKey && oEvent.altKey && oEvent.keyCode == 51) {
				// 打开图像（v）, Ctrl + Alt + 3
				openStudyImage('viapara_worklist');
			}
		}
	}
);

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
	$("#searchForm_worklist").form('submit', {
		url:window.localStorage.ctx+"/worklist/exportExcel?reportstatus=31&titles=" + titles + "&fields=" + fields + "&widths=" + widths+'&page=1&rows=500'
	});
	$('#progress_dlg').dialog('close');
}

//*****************修改检查时间*****************************
//打开检查时间窗口
function openStudydatetimeDlg(){
	var row = $("#dg").datagrid("getSelected");
	if (row!=null) {
		if(row.status == myCache.StudyOrderStatus.canceled){//判断检查状态
			$.messager.show({title : $.i18n.prop('alert'),msg : '当前检查状态无法更改检查完成时间',border:'thin',timeout : 3000,showType : 'slide'});
			return;
		}
		
		$('#common_dialog').dialog({
			title:'时间选择',
			width : 350,
			height : 260,
			closed : false,
			cache : false,
			resizable: false,
			minimizable: false,
			maximizable: false,
			border: 'thin',
			href: window.localStorage.ctx+'/worklist/openStudydatetimeDlg?studyid='+row.studyorderstudyid,
			modal : true,
			buttons:[{
				text: $.i18n.prop('save'),
				width:80,
				handler:function(){saveStudydatetime();}
			},{
				text:$.i18n.prop('cancel'),
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}],
			onLoad:function(){
				
			}
		});
	}else{
		$.messager.show({title : $.i18n.prop('alert'),msg : $.i18n.prop('wl.selectonestudy'),timeout : 3000,border : 'thin',showType : 'slide'});
	}
}
//获取plaza中的完成日期
function getPlazatime(){
	$('#progress_dlg').dialog('open');
	getJSON(window.localStorage.ctx+'/worklist/getPlazatime',
			{
				studyid : $('#studyid_time').val()
			},
			function(json){
				$('#progress_dlg').dialog('close');
				var data = validationDataAll(json);
				if(data.code == 0 && data.data){
					$('#sptime_time').timespinner('setValue', data.data);
				}else{
					$.messager.show({
						title:'提醒',
				        msg: '获取plaza信息失败！',
				        timeout:3000,
				        border:'thin',
				        showType:'slide'
					});
				}
				
			});
}
//获取登记时间
function getRegistertime(){
	getJSON(window.localStorage.ctx+'/worklist/getRegistertime',
			{
				studyid : $('#studyid_time').val()
			},
			function(json){
				var data = validationDataAll(json);
				if(data.code == 0 && data.data){
					$('#sptime_time').timespinner('setValue', data.data);
				}else{
					$.messager.show({
						title:'提醒',
				        msg: '获取登记日期失败！',
				        timeout:3000,
				        border:'thin',
				        showType:'slide'
					});
				}
				
			});
}

//保存检查时间
function saveStudydatetime(){
	getJSON(window.localStorage.ctx + '/worklist/saveStudydatetime',
			{
				studyid :  $('#studyid_time').val(),
				studydatetime : $('#sptime_time').timespinner('getValue')
			},
			function(json){
				var data = validationDataAll(json);
				console.log(data);
				if(data.code == 0){
					refresh();
					$('#common_dialog').dialog('close');
					$.messager.show({
						title:'提醒',
				        msg: '保存成功',
				        timeout:3000,
				        border:'thin',
				        showType:'slide'
					});
				}
			});
}
//*********************修改检查时间*****************************

//***********************快捷键操作*********************************************************************************************
/**
 * 监听快捷键事件
 * @returns
 */
$(document).ready(
	function() {
		document.onkeydown = function() {
			var oEvent = window.event;
			if(oEvent.ctrlKey && oEvent.shiftKey && oEvent.keyCode == 49){
				console.log("Ctrl+Shift+1");
				applyTemplate_shortcuts("Ctrl+Shift+1");
			}
			if(oEvent.ctrlKey && oEvent.shiftKey && oEvent.keyCode == 50){
				console.log("Ctrl+Shift+2");
				applyTemplate_shortcuts("Ctrl+Shift+2");
			}
			if(oEvent.ctrlKey && oEvent.shiftKey && oEvent.keyCode == 51){
				console.log("Ctrl+Shift+3");
				applyTemplate_shortcuts("Ctrl+Shift+3");
			}
			if (oEvent.ctrlKey && oEvent.keyCode == 81) {
				// 打开申请单, Ctrl + Q
				openApplyForm();
			} else if (oEvent.ctrlKey && oEvent.keyCode == 73) {
				// 打开图像（p）, Ctrl + i;
				openStudyImage('plazapara_worklist');
			} else if (oEvent.ctrlKey && oEvent.altKey && oEvent.keyCode == 51) {
				// 打开图像（v）, Ctrl + Alt + 3
				openStudyImage('viapara_worklist');
			}
		}
	}
);

function openShortcutDialog(){
	$('#common_dialog').dialog({
		title : '编辑快捷键',
		width : 510,
		height : 520,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+"/worklist/openShortcutDialog",
		buttons:[{
			text: $.i18n.prop('save'),
			width:80,
			handler:function(){saveshortcuts();}
		},{
			text: $.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}],
		onLoad:function(){
			
		}
	});
}

function saveshortcuts(){
	var node=$("#nodetree_shortcuts").tree('getSelected');
	if(node && node.type=="template" && node.id){
		getJSON(window.localStorage.ctx+"/worklist/saveshortcuts",
				{
					modality : $('#modality_shortcuts').combobox('getValue'),
					keyCode : $('#keyCode_shortcuts').combobox('getValue'),
					template_id : node.id,
				},
				function(data) {
					var json=validationDataAll(data);
					if(json.code==0){
						$.messager.show({
							title:'成功',
				            msg:"保存成功！",
				            timeout:3000,
				            border:'thin',
				            showType:'slide'
						});
						$('#common_dialog').dialog('close');
					}
					
				}
			);
	}else{
		$.messager.show({
			title:'提醒',
            msg:"请选择一份模板！",
            timeout:3000,
            border:'thin',
            showType:'slide'
		});
	}
}

//使用快捷键填入报告模板
function applyTemplate_shortcuts(shortcuts){
	var index = $('#tab').tabs('getTabIndex', $('#tab').tabs('getSelected'));
	if(index === 0){
		return;
	}
	var reportid = getReportid_Open();
	if($("#srtemplateid_" + reportid)[0]){
		$.messager.show({
			title : '提醒',
			msg : "当前报告不可应用！",
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
		return;
	}
	getJSON(window.localStorage.ctx+"/worklist/applyTemplate_shortcuts",
			{
				modality : $('#modality_'+reportid).val(),
				keyCode : shortcuts
			},
			function(data){
				var json=validationDataAll(data);
				console.log(json);
				if(json.code==0 && json.data){
					if($("#orderStatus_"+reportid).val() != myCache.ReportStatus.FinalResults){
						var ue_result = UM.getEditor('result_' + reportid + '_html');
						var ue_desc = UM.getEditor('desc_' + reportid + '_html');
						ue_result.setContent(json.data.resultcontent_html || "");
						ue_desc.setContent(json.data.desccontent_html || "");
						$('#method_'+reportid).textbox("setValue",json.data.studymethod || "");
					}else{
						$.messager.show({
							title : '提醒',
							msg : "该报告是最终报告不可编辑！",
							timeout : 3000,
							border:'thin',
							showType : 'slide'
						});
					}
				}else{
					$.messager.show({
				        title:'错误',
				        msg:'请求数据失败或当前没有设置快捷模板！',
				        timeout:3000,
				        border:'thin',
				        showType:'slide'
				    });
				}
				
			});
	
}

//根据检查类型，快捷键获取当前配置的报告模板
function getTemplate_shortcuts(newValue, oldValue){
	if(newValue != ""){
		getJSON(window.localStorage.ctx+"/worklist/getTemplate_shortcuts",
				{
					modality : newValue,
					keyCode : $('#keyCode_shortcuts').combobox('getValue')
				},
				function(data){
					var json=validationDataAll(data);
					console.log(json);
					if(json.code==0 && json.data){
						
						$('#templatename_shortcuts').textbox('setValue', json.data.name);
					}else{
						$('#templatename_shortcuts').textbox('setValue', '');
					}
				});
		$('#nodetree_shortcuts').tree({
		    url : window.localStorage.ctx+'/template/getTemplateNodeByModality?modality='+newValue+'&creator=1'
		});
	}
}
//******************************快捷键*******************************************************************************************

//查看历史检查的报告
function showHistoryReport(row){
	if(row.reportid){
		$('#common_dialog').dialog({
			title : $.i18n.prop('report.historyreport'),
			width : 680,height : 580,
			modal : true,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,
			border: 'thin',
			href : window.localStorage.ctx+'/report/historyView?orderid='+row.orderid+'&reportid='+row.reportid,
			buttons:[{
				text:$.i18n.prop('report.close'),
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}]
		});
	}else{
		_message("当前检查无报告！", "提醒");
	}

	
}

function openDutyRoster(){
	$('#common_dialog').dialog({
		title : '排班表',
		width : 1100,height : 650,
		modal : true,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,
		border: 'thin',
		href : window.localStorage.ctx+'/worklist/showDutyRoster',
		buttons:[{
			text:$.i18n.prop('report.close'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

function returnReport(){
	var row = $("#dg").datagrid("getSelected");
	if (row!=null) {
		if(row.report_assignee!=$('#userid_hidden').val()){
			_message("该检查没有指派给您，无法退回！","提示");
			return;
		}
		if(row.reportstatusdisplaycode==myCache.ReportStatus.FinalResults){
			_message("该报告已经审核，无法退回！","提示");
			return;
		}
		
		getJSON(window.localStorage.ctx+"/worklist/returnReport",
			{
				orderid : row.orderid,
				report_status: row.reportstatusdisplaycode
			},
			function(data){
				var json=validationDataAll(data);
				console.log(json);
				if(json.code==0){
					_message("报告退回成功！","提示");
				}else{
					_message("报告退回失败，请稍后重试！如果问题依然存在，请联系系统管理员！","错误");
				}
			});
	}
}

function menuHandler(item){
	  alert(item.id);
}

/**
 * 姓名加密
 * @param value
 * @param row
 * @param index
 * @returns
 */
function columeFormatter_patientname_reg(value,row,index) {
	if (row.vipflag == 1 && row.reportstatusdisplaycode == myCache.ReportStatus.FinalResults
			&& $('#vip_flag')[0] && $('#vip_flag').val() != 1) {
		return value.replace(/^(\S{1})\S+/g, '$1**');
	} else {
		return value;
	}
}
//根据设备类型查询检查设备
function findModalityname(newValue, oldValue){
	if(newValue){
		getJSON(window.localStorage.ctx+'/dic/findModalityname',
				{
					modalityId : newValue.toString()
				}, 
				function(data){
					$('#modality_dic_reg').combobox('loadData',data);
					if(data.length>0){
						$("#modality_dic_reg").combobox('select',data[0].id);
					}else{
						$('#modality_dic_reg').combobox('clear');
					}
				});
	}else{
		$('#modality_dic_reg').combobox('clear');
		$('#modality_dic_reg').combobox('loadData',[]);
	}
}
function setModalityname(newValue, oldValue){
	if(newValue){
		$("#modality_dic_reg_id").val(newValue.toString());
	}else{
		$("#modality_dic_reg_id").val("");
	}
}

function openPrintImgDlg(){
	var row=$('#dg').datagrid('getSelected');
	if(row){
		console.log(row)
		openPrintImagesDlg(row.reportid,row.orderid,row.studyorderstudyid);//report.js中的方法
	} else{
		_message("请选择一条数据！");
	}
}

function refreshRow(reportid){
	getJSON(window.localStorage.ctx+"/worklist/findReportInfoById?reportid="+ reportid,null,function(json) {
		if (json.code == 0) {
			var rows = $('#dg').datagrid('getRows');//返回当前页面所有行
			var index=null;
			if(rows){
				for (var i = 0; i < rows.length; i++) {
					var row = rows[i];
					if(row.reportid==reportid){
					    index=$('#dg').datagrid('getRowIndex',row);
						break;
					}
					
				}
				if(index!=null){
					 $('#dg').datagrid('updateRow',{
							index: index,
							row: json.data
					 });
				}
			}
		} else{
			_message("刷新数据失败！");
		}
	});
}
