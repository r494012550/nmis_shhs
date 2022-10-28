$(function(){
	//console.log("=======report_request_reportid====:"+request_reportid);
//	init(function () {});
	$("#imageShow_" + request_reportid).attr('src',$("#imageShow").attr('src'));
	//启动报告自动保存功能
	enbaleAutoSaveReport();
	//测试
})

function enbaleAutoSaveReport(){
	var autosavereport_interval= $('#autosavereportint').val();
	console.log('autosavereport_Interval='+autosavereport_interval);
	if(autosavereport_interval!=null&&parseInt(autosavereport_interval)>0){
		console.log('enable auto save report.Interval is:'+autosavereport_interval+' minute');
		autosavereport_interval=parseInt(autosavereport_interval)*60000;
		autosavereport_int=self.setInterval("autoSaveReport()",autosavereport_interval);
	}
}
function autoSaveReport(){
	console.log('autoSaveReport.'+new Date());
	$('#autosavereport_tip').html("");
	let tabs=$('#tab').tabs('tabs')
	for(let i=0,len=tabs.length;i<len;i++){
		let tabid=tabs[i].panel('options').id;
		console.log(tabid);
		if(tabid){
			let reportid=tabid.substr(tabid.lastIndexOf("_")+1);
			//暂时只实现常规报告的自动保存
			if(!$('#srtemplateid_'+reportid)[0]){
				if($('#orderStatus_'+reportid).val()==myCache.ReportStatus.Noresult||//未写
						$('#orderStatus_'+reportid).val()==myCache.ReportStatus.Created||//已创建
						$('#orderStatus_'+reportid).val()==myCache.ReportStatus.Preliminary||//初步报告
						$('#orderStatus_'+reportid).val()==myCache.ReportStatus.Preliminary_reject||//初步报告驳回
						$('#orderStatus_'+reportid).val()==myCache.ReportStatus.Preliminary_review||//初审
						$('#orderStatus_'+reportid).val()==myCache.ReportStatus.Preliminary_review_reject){//初审驳回
					var ue_desc = UM.getEditor('desc_' + reportid + '_html');
					var desc_txt = UM.utils.html(ue_desc.getPlainTxt(true));
					var desc_html=ue_desc.getContent();
					var ue_result = UM.getEditor('result_' + reportid + '_html');
					var result_txt = UM.utils.html(ue_result.getPlainTxt(true));
					var result_html=ue_result.getContent();
					
					if(desc_txt!=""||result_txt!=""){
						$.post(window.localStorage.ctx+'/report/autoSaveReport',
						    {
								'id':reportid,
						    	'checkdesc_txt' : desc_txt,
						    	'checkdesc_html' : desc_html,
						    	'checkresult_txt' : result_txt,
						    	'checkresult_html' : result_html
						     },
						     function (res) {
					        	var json = validationDataAll(res);
					        	if(json.code==0){
					        		console.log("auto save report success.")
					        		let now=new Date();
					        		$('#autosavereport_tip').html("自动保存成功！"+now.format('yyyy-MM-dd hh:mm:ss'));
					        	}
					        }
						);
					}
				}
			}
		}
	}
	
}

/**
 * 历史检查记录
 * 
 * @returns
 */
function openHistoryReport(mainReportid, row) {
	$('#common_dialog').dialog({
		title : $.i18n.prop('report.historyreport'),
		width : 680,height : 680,modal : false,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,
		border: 'thin',
		href : window.localStorage.ctx+'/report/historyView?orderid='+row.id+'&mainReportid='+mainReportid+'&reportid='+row.reportid,
		buttons:[{
			text:$.i18n.prop('report.close'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}


/**
 * 关闭痕迹对比
 * @param studyid
 * @returns
 */
//function closeTraceCompare(){
//	//关闭痕迹对比窗口   
//	$("#win").window("close");
//}

// 痕迹对比
function traceCompare(studyid){
   
	reportid1=null;
	reportid2=null;
	
	$.getJSON( window.localStorage.ctx+"/report/getAllReport?studyid="+studyid,function(json){
		
		var data=validationData(json);
		if(data){
			$('#common_dialog').dialog({
				title : '报告修改痕迹对比',
				width : 1000,
				height : 700,
				closed : false,
				cache : false,
				href : window.localStorage.ctx+'/report/reportTrack',
				border: 'thin',
				modal : true,
				buttons:[{
					text:'关闭',
					width:80,
					handler:function(){$('#common_dialog').dialog('close');}
				}],
				onLoad:function(){
					$('#conDataGrid').datagrid('loadData',data);
				}
			});
				 
		}else{
			  //$.messager.alert('提示','该报告没有痕迹记录!');
//			$.messager.alert({ 
//		　　　　　　　　title:'提示', 
//		　　　　　　　　msg:'该报告没有痕迹记录!', 
//		　　　　　　　　icon:'确定', 
//			　　　　});
		}
	});

	$("#reporttabs").tabs('hideHeader');
		// 报告信息
		defaultReportCompare();
}
//获取默认报告信息
function defaultReportCompare(){
	  
//	document.getElementById("pname").value=(reportRow.patientname || '');
//	document.getElementById("pgender").value=(reportRow.sexdisplay || '');
//	document.getElementById("page").value=(reportRow.age + reportRow.ageunitdisplay|| '');
//	document.getElementById("poutno").value=(reportRow.outno || '');
//	document.getElementById("pinno").value=(reportRow.inno || '');
//	document.getElementById("pstudyorderstudyid").value=(reportRow.studyorderstudyid || '');
//	document.getElementById("patientid_").value=(reportRow.patientid || '');
//	document.getElementById("pwardno").value=(reportRow.wardno || '');
//	document.getElementById("pbedno").value=(reportRow.bedno || '');
//	document.getElementById("pstudyitems").value=(reportRow.studyitems || '');
}

// 获取指定id的报告
function findReportById(studyid) {
	sendAjax({
		url:window.localStorage.ctx+'/worklist/checkSession',
		type:'post',
		success:function(data){
		}
	})   
	// 获取选中行的id
	var row = $('#conDataGrid').datagrid('getSelected');
	if (row) {
		 $.ajax({
					url : window.localStorage.ctx+"/report/getReportById",
					contentType : "application/x-www-form-urlencoded; charset=UTF-8",
					data : {
						'id' : row.reportid
					},
					dataType : 'json',
					success : function(data) {
						// 校验后台返回的数据是否为空，数量是否大于0
						var json = validationData(data);
						document.getElementById("checktime").value = json.createtime;
						document.getElementById("checkMethod").value = json.studymethod;
						$('#apperance').html(json.checkdesc_txt);
						$('#diagnose').html(json.checkresult_txt);
					}
				});
	}
}
var reportid1;
// 添加初始报告
function initialReport(studyid) {
	sendAjax({
		url:window.localStorage.ctx+'/worklist/checkSession',
		type:'post',
		success:function(data){
		}
	})   
	// 获取选中行的id
	var row = $('#conDataGrid').datagrid('getSelected');
	if (row) {
		if(reportid2 != row.reportid){
		
			$.ajax({
					url : window.localStorage.ctx+"/report/getReportById",
					contentType : "application/x-www-form-urlencoded; charset=UTF-8",
					data : {
						'id' : row.reportid
					},
					dataType : 'json',
					success : function(data) {
						// 校验后台返回的数据是否为空，数量是否大于0
						reportid1 = row.reportid;
					
						var json = validationData(data);// (row.studyitems ||
						// "")
						document.getElementById("initial").value = (json.reportphysician_name || "      ")
								+ " "
								+ json.createtime
								+ " "
								+ (json.reportstatus || "");
					}
				});
		
		}else{
			$.messager.show({
				title : '提醒',
				msg : "初始报告与参照报告不能是同一报告!",
				timeout : 3000,
				border:'thin',
				showType : 'slide'
			});
		}
	}else{
		$.messager.show({
			title : '提醒',
			msg : "请选择一条报告!",
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
	}
	$('#conDataGrid').datagrid('clearSelections');  
}
var reportid2;
// 添加参照报告
function referenceReport(studyid) {
	sendAjax({
		url:window.localStorage.ctx+'/worklist/checkSession',
		type:'post',
		success:function(data){
		}
	})   
	// 获取选中行的id
	var row = $('#conDataGrid').datagrid('getSelected');
	if (row ) {
		if(reportid1 != row.reportid){
			 $.ajax({
				url : window.localStorage.ctx+"/report/getReportById",
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					'id' : row.reportid
				},
				dataType : 'json',
				success : function(data) {
					// 校验后台返回的数据是否为空，数量是否大于0
					reportid2 = row.reportid;
					var json = validationData(data);
					document.getElementById("reference").value = (json.reportphysician_name || "      ")
							+ " "
							+ json.createtime
							+ " "
							+ (json.reportstatus || "");
					;
				}
			});
		}else{
			$.messager.show({
				title : '提醒',
				msg : "初始报告与参照报告不能是同一报告!",
				timeout : 3000,
				border:'thin',
				showType : 'slide'
			});
		}
		
	}else{
		$.messager.show({
			title : '提醒',
			msg : "请选择一条报告!",
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
	}
	$('#conDataGrid').datagrid('clearSelections');  
}

// 对比报告
function compareReport(studyid) {
	sendAjax({
		url: window.localStorage.ctx+'/worklist/checkSession',
		type:'post',
		success:function(data){
		}
	})       
	$.extend($.messager.defaults,{
          ok:"确定",
          cancel:"取消"
      });
	if (reportid1 == null) {
		$.messager.show({
			title : '提醒',
			msg : "请选择初始报告!",
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
		return;
	} else if (reportid2 == null) {
		$.messager.show({
			title : '提醒',
			msg : "请选择参照报告!",
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
		return;
	} else {
		$.ajax({
			url : window.localStorage.ctx+"/report/compareReport",
			contentType : "application/x-www-form-urlencoded; charset=UTF-8",
			data : {
				'reportid1' : reportid1,
				'reportid2' : reportid2
			},
			dataType : 'json',
			success : function(data) {
				var json = validationData(data);
				var contDesc = new Array();
				var contResult = new Array();
				var checkMethod = new Array();
				for (var i = 0; i < json.conData.length; i++) {
					contDesc[i] = json.conData[i].checkdesc_txt;
					contResult[i] = json.conData[i].checkresult_txt;
					checkMethod[i] = json.conData[i].studymethod;
				}
				var contDesc1 = contDesc[0];
				var contDesc2 = contDesc[1];
				var contResult1 = contResult[0];
				var contResult2 = contResult[1];
				var checkMethod1 = checkMethod[0];
				var checkMethod2 = checkMethod[1];
				if (contDesc1 !== contDesc2 || contResult1 !== contResult2 || checkMethod1 !=checkMethod2) {
//					$.messager.alert({ 
//				　　　　　　　　title:'提示', 
//				　　　　　　　　msg:'报告内容已改变!', 
//				　　　　});
				} else {
					$.messager.alert('提示', '报告内容未改变!');
				}

			}
		});
	}
}




function insertFindingToReport(index,row,reportid){
	
//	if(UE.getEditor('desc_' + studyid + '_html').isFocus()){
		UM.getEditor('desc_' + reportid + '_html').execCommand('insertHtml', row.html);
//	}
//	if(UE.getEditor('result_' + studyid + '_html').isFocus()){
//		UE.getEditor('result_' + studyid + '_html').execCommand('insertHtml', row.html);
//	}
}


function insertImages(imageid,studyid){
	var tabid=$('#tab').tabs('getSelected').panel('options').id;
	var reportid=tabid.substr(tabid.lastIndexOf("_")+1);
	
	var imgwidth=$('#imgwidth').val();
	if(imgwidth){
		imgwidth="width:"+imgwidth+"px;";
	}
	else{
		imgwidth="width:300px;";
	}
	
	var imgheight=$('#imgheight').val();
	if(imgheight){
		imgheight="height:"+imgheight+"px;";
	}
	else{
		imgheight="";
	}
	
	var html="<img src='"+window.localStorage.ctx +"/image/image_GetViaImage?id=" + imageid + "' align='middle' style='"+imgwidth+imgheight+"float:center' alt='img'/>";
	
	//alert($('#srtemplateid_'+studyid).val())
	if($('#srtemplateid_'+reportid).val()){
		UM.getEditor('structTemplet_'+reportid).execCommand('insertHtml', html);
	}
	else{
		UM.getEditor('desc_' + reportid + '_html').execCommand('insertHtml', html);
	}
}


/*function getStudyid_Open(title){
	if(!title){
		title=$('#tab').tabs('getSelected').panel('options').title;
	}	
	return title.substr(title.lastIndexOf("_")+1);
}*/

/**
 * 报告关闭事件
 * @param title
 * @param index
 * @returns
 */
function report_close(title,index){
	
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

//解除锁定
function closeReport(reportid){
	 getJSON(window.localStorage.ctx+"/report/closeReport?reportid="+ reportid,null,function(json) {
			if (json.code == 1) {
				$.messager.show({
							title : $.i18n.prop('error'),
							msg : $.i18n.prop('report.closereportfailed'),
							timeout : 3000,
							showType : 'slide'
						});
			}else{
				refreshRow(reportid);
			}
		});
	 //refreshReportInfo(reportid);
	 // refresh();
}

//判断是否保存
function beforeCloseReport(title,index){
	 var tabid=$('#tab').tabs('getTab',index).panel('options').id;
	 var reportid=tabid.substr(tabid.lastIndexOf("_")+1);

//	 var studyid=getStudyid_Open(title);
	 var reportFlag=$("#orderStatus_"+reportid).val();
	 var studyid=$("#studyid_"+reportid).val();
	 var srtemplateid=$("#srtemplateid_"+reportid).val();
	 var reportStatus=$("#orderStatus_"+reportid).val();
	 /* 如果是最终报告直接返回true*/
	 if(reportFlag==myCache.ReportStatus.FinalResults){
		 closeReport(reportid);
		 return true;
	 }
	 
	 var ccf=$("#contentChangeflag_"+reportid).val();
//	 console.log("contentChangeflag========="+ccf);
	 if(ccf){
		 $.messager.confirm({
				title: "是否保存报告？",
				ok: $.i18n.prop('report.yes'),
				cancel: $.i18n.prop('report.no'),
				border:'thin',
				msg: "选择‘是’保存，‘否’不保存！",
				fn: function(r){
					if(r){
						//根据模板id判断保存那种类型的报告
						if(srtemplateid==null||srtemplateid==0){
							var orderStatus = $("#orderStatus_" + reportid).val();
							var studyitem = $("#studyitem_" + reportid).val();
							var sexdisplay = $("#sexdisplay_" + reportid).val();
							// 保存后关闭
							beforesaveReport(reportid, orderStatus, studyitem, sexdisplay, 'saveReport', true);
						}else{
							//设置参数close，表示保存后自动关闭报告
							saveStructReport(reportid,studyid,"",true);
						}
					}else{
						console.log($('#sr_container_'+reportid));
						closeReportDirectly(reportid);
					}
				}
			});
		 console.log(" before close return false");
		 return false;
	 }
	 else{
		 closeReport(reportid);
		 console.log(" before close return true");
		 return true;
	 }
}
//直接关闭报告
function closeReportDirectly(reportid){
	$('#sr_container_'+reportid).empty();
	$('#sr_container_'+reportid).remove();
	var opts = $("#tab").tabs('options');
	var bc = opts.onBeforeClose;
	opts.onBeforeClose = function(){};  // allowed to close now
//	$("#tab").tabs('close',index);
	closeReport(reportid);
	closeTab(reportid);
	opts.onBeforeClose = bc;  // restore the event function
}

//保存报告
//function saveReportInfo(studyid,patientname,reportStatus){
//	//alert($("#srtemplateid_"+studyid).val());
//	if($("#srtemplateid_"+studyid).val()){
//		saveStructReport(studyid,patientname,reportStatus);
//	}else{
//		saveReport(studyid,patientname,reportStatus);
//	}
//}

/**
 *  保存报告之前做的操作
 * @param reportid
 * @returns
 */
function beforeSaveOrAuditReport(reportid) {
	var ue_desc = UM.getEditor('desc_' + reportid + '_html');
	//获取检查描述的字体大小，只取第一个span标签的字体大小
	$(ue_desc.getContent()).find('span').each(function(index , obj){
		var fontsize=UM.dom.domUtils.getStyle(obj,'font-size');
		if(fontsize){
			$('#desc_fontsize_'+reportid).val(fontsize.replace('px',''));
			return false;
		}
	});
	if(!$('#desc_fontsize_'+reportid).val()){
		$('#desc_fontsize_'+reportid).val(14);
	}
	//ue_desc.execCommand('selectall');
	//ue_desc.execCommand('removeformat');
	var desc_txt = UM.utils.html(ue_desc.getPlainTxt(true));
	$('#desc_' + reportid + '_txt').val(removeTxtspace(desc_txt));
	var ue_result = UM.getEditor('result_' + reportid + '_html');
	//获取检查结论的字体大小，只取第一个span标签的字体大小
	$(ue_result.getContent()).find('span').each(function(index , obj){
		var fontsize=UM.dom.domUtils.getStyle(obj,'font-size');
		if(fontsize){
			$('#result_fontsize_'+reportid).val(fontsize.replace('px',''));
			return false;
		}
	});
	if(!$('#result_fontsize_'+reportid).val()){
		$('#result_fontsize_'+reportid).val(14);
	}
//	ue_result.execCommand('selectall');
//	ue_result.execCommand('removeformat');
	var result_txt = UM.utils.html(ue_result.getPlainTxt());
	$('#result_' + reportid + '_txt').val(removeTxtspace(result_txt));
	
	//-----------当报告助手面板默认是收缩状态时，不保存标签等数据---------------//
	var savelabel_flag=false;
	if($('#publiclabelct_' + reportid)[0]){
		$('#publiclabel_' + reportid).val($('#publiclabelct_' + reportid).combotree('getValues'));
		$('#privatelabel_' + reportid).val($('#privatelabelct_' + reportid).combotree('getValues'));
		$('#icd10label_' + reportid).val($('#icd10labelct_' + reportid).combobox('getValues'));
		savelabel_flag=true;
	}
	$('#savelabel_flag_' + reportid).val(savelabel_flag);
	//----------------------------------------------------------------------//
	
	//-----------保存图文报告中的图片---------------//
	var imgids='';
	$('#img_ul_' + reportid).find('.item').sort(sortImage).each(function(index , obj){
		console.log($(obj).attr('imgid'));
		imgids+=$(obj).attr('imgid')+',';
	});
	if(imgids!=''){
		imgids=imgids.substring(0,imgids.length-1);
	}
	$('#imageids_' + reportid).val(imgids);
	//----------------------------------------------------------------------//
}

//1.去除开始换行，包括空格换行
//2.去除结尾所有空白
//3.去除中间只有空白的段落
function removeTxtspace(text){
	return text.replace(/^[ ]{0,}[\r\n]+/g, '').replace(/(\s|\u00A0)+$/g, '').replace(/([\r\n]\s*[\r\n])/g, '\r\n');
}

/*
 * closeAfterSave:true-关闭，false-不关闭
 * */
function beforesaveReport(reportid,reportStatus,studyitem,patientsex,type,closeAfterSave){
	beforeSaveOrAuditReport(reportid);
	//所见
	var desc = $('#desc_' + reportid + '_txt').val();
	//所得
	var result = $('#result_' + reportid + '_txt').val();
	var modality_type = $('#modality_type_' + reportid).val();
	
	$.post(window.localStorage.ctx+"/report/getReportCorrect",
		{
			sex : patientsex,
			examitem : studyitem,
			desc : desc,
			result : result,
			modality_type:modality_type
		},
		function(json){
			if(json.code==-1){
				$.messager.confirm({
					title:'智能纠错',
					ok:'是',
					cancel:'否',
					border:'thin',
					msg:'<div style="margin-left:40px"><div>'+json.message+'</div><div style="margin-top:5px"><div style="margin-top:10px">是否继续?<div><div>',
					fn: function(r){
						if (r){
							judgeSaveorSubmitorAudit(reportid,reportStatus,type,closeAfterSave);
						}
					}
				});
			} else {
				judgeSaveorSubmitorAudit(reportid,reportStatus,type,closeAfterSave);
			}		
			
	});
	
}
/**
 * 去除报告检查纠错的重复项
 * @param array
 * @returns
 */
//function deleteSameElement(array){
//	var hash = [];
//	for (var i =0 ; i < array.length; i++){
//		//若hash数组不存在则加到hash数组中
//		if(hash.indexOf(array[i]) == -1){
//			hash.push(array[i]);
//		}
//	}
//	return hash;
//}

/**
 * 判断是保存/提交/初审/审核操作
 * @param reportid
 * @param reportStatus
 * @returns
 */
function judgeSaveorSubmitorAudit(reportid,reportStatus,type,closeAfterSave){
	if (type == 'saveReport'){
		//保存报告
		saveReport(reportid,reportStatus,closeAfterSave);
	} else if (type == 'submitReport'){
		//提交报告
		submitReport(reportid,reportStatus);
	} else if (type == 'auditPreReport'){
		//初审报告
		auditPreReport(reportid,reportStatus);
	} else if (type == 'auditReport'){
		//审核报告
		auditReport(reportid,reportStatus);
	}
}

//保存默认报告  closeAfterSave:true-关闭，false-不关闭
function saveReport(reportid,reportStatus,closeAfterSave) {
	$('#progress_dlg').dialog('open');
	$('#reportform_' + reportid).form('submit',{
		url : window.localStorage.ctx+'/report/saveReport',
		onSubmit : function() {
			beforeSaveOrAuditReport(reportid);
		},
		success : function(data) {
			var json = validationDataAll(data);
			console.log(json);
			if (json.code == 0) {
//				if(!$('#id_' + studyid).val()){
//					$('#reporttime_' + studyid).html(json.data.reporttime);
//					$('#reportphysician_' + studyid).html(json.data.reportphysician_name);
//				}
				$('#id_' + reportid).val(json.data.id);
				$('#orderStatus_' + reportid).val(json.data.reportstatus);
				$("#contentChangeflag_"+reportid).val("");
				changeReportBtnEnable(json.data.reportstatus,reportid);
				$.messager.show({
					title : $.i18n.prop('tips'),
					msg : $.i18n.prop('savesuccess'),
					timeout : 3000,border : 'thin',showType : 'slide'
				});
				//refreshReportInfo(reportid, json.data.reportstatus);
				// 关闭时，点击保存，保存后自动关闭
				if (closeAfterSave) {
					closeReportDirectly(reportid);
				}
				
				refreshRow(reportid);
				//refresh();
			} else {
				 $.messager.show({
		            title: $.i18n.prop('error'),
		            msg: $.i18n.prop('savefailed'),
		            timeout:3000,border:'thin',showType:'slide'
		        });
			}
			$('#progress_dlg').dialog('close');
			
		}
	});
}

/**
 * 提交常规初步报告
 * @param reportid
 * @param patientname
 * @param reportStatus
 * @returns
 */
function submitReport(reportid,reportStatus) {

	var caFlag=$("#caFlag_"+reportid).val();
	//判断是否开启ca
	if(caFlag=="1"){
		//获取Cakey
		GetAllDeviceSN(call_back);
		setTimeout(function(){
			var devSN = $("#devSN").val();
			if(devSN!=null&&devSN!=""){
				    console.log(devSN);
				    $('#progress_dlg').dialog('open');
				    $('#reportform_' + reportid).form('submit',{
					url : window.localStorage.ctx+'/report/submitReport',
					onSubmit : function() {
						$("#devSN1").val(devSN);
	//					beforeSaveOrAuditReport(reportid);
					},
					success : function(data) {
						var json = validationDataAll(data);
						if (json.code == 0) {
							// 提交成功后是否自动关闭
							var report_submit = $("#report_submit").val();
							console.log("提交后是否关闭：" + report_submit);
							if ("1" == report_submit) {
								$("#enable_refresh_" + reportid).val("1");
								closeTab(reportid);
							} else {
							$("#devSN1").val("");
							//if(!$('#id_' + studyid).val()){
								$('#reporttime_' + reportid).html(json.data.reporttime);
								//$('#reportphysician_' + reportid).html(json.data.reportphysician_name);
								$('#reportphysician_' + reportid).empty();
								$('#reportphysician_' + reportid).append("<img src='/pt"+json.data.b_autograph+"' width=140 height=45 />");
							//}
							$('#id_' + reportid).val(json.data.id);
							$('#orderStatus_' + reportid).val(json.data.reportstatus);
							$("#contentChangeflag_" + reportid).val("");
							changeReportBtnEnable(json.data.reportstatus,reportid);
							if($("#image_container_"+reportid)[0]){
								$("#image_container_"+reportid).panel('refresh');
							}
							$.messager.show({
								title : $.i18n.prop('tips'),
								msg : $.i18n.prop('savesuccess'),
								timeout : 3000,border : 'thin',showType : 'slide'
							});
							
							//refreshReportInfo(reportid, json.data.reportstatus);
							//refresh();
							refreshRow(reportid);
							}
						} else if(json.code == -2) {
							_message(json.message);
						}else{
							 $.messager.show({
						            title: $.i18n.prop('error'),
						            msg: $.i18n.prop('savefailed'),
						            timeout:3000,border:'thin',showType:'slide'
						        });
						}
						$('#progress_dlg').dialog('close');
						afterSaveOrAuditReport(reportid);
						submitbtn = true;
					}
				});
			}else{
				_message("没有插入key");
				submitbtn = true;
			}
		},800);
	}else{
		 $('#progress_dlg').dialog('open');
		    $('#reportform_' + reportid).form('submit',{
			url : window.localStorage.ctx+'/report/submitReport',
			onSubmit : function() {
//				beforeSaveOrAuditReport(reportid);
			},
			success : function(data) {
				var json = validationDataAll(data);
				if (json.code == 0) {
					// 提交成功后是否自动关闭
					var report_submit = $("#report_submit").val();
					console.log("提交后是否关闭：" + report_submit);
					if ("1" == report_submit) {
						$("#enable_refresh_" + reportid).val("1");
						closeTab(reportid);
					} else {
					//if(!$('#id_' + studyid).val()){
						$('#reporttime_' + reportid).html(json.data.reporttime);
						$('#reportphysician_' + reportid).html(json.data.reportphysician_name);
						//$('#reportphysician_' + reportid).empty();
						//$('#reportphysician_' + reportid).append("<img src='/pt"+json.data.b_autograph+"' width=140 height=45 />");
					//}
					$('#id_' + reportid).val(json.data.id);
					$('#orderStatus_' + reportid).val(json.data.reportstatus);
					$("#contentChangeflag_" + reportid).val("");
					changeReportBtnEnable(json.data.reportstatus,reportid);
					if($("#image_container_"+reportid)[0]){
						$("#image_container_"+reportid).panel('refresh');
					}
					$.messager.show({
						title : $.i18n.prop('tips'),
						msg : $.i18n.prop('savesuccess'),
						timeout : 3000,border : 'thin',showType : 'slide'
					});
					
					//refreshReportInfo(reportid, json.data.reportstatus);
					//refresh();
					refreshRow(reportid);
					}
				} else {
					 $.messager.show({
			            title: $.i18n.prop('error'),
			            msg: $.i18n.prop('savefailed'),
			            timeout:3000,border:'thin',showType:'slide'
			        });
				}
				$('#progress_dlg').dialog('close');
				afterSaveOrAuditReport(reportid);
				submitbtn = true;
			}
		});
	}
}

////初审报告
//function auditPreReportInfo(studyid,patientname,reportStatus){
//	//alert($("#srtemplateid_"+studyid).val());
//	if($("#srtemplateid_"+studyid).val()){
//		//saveStructReport(studyid,patientname,reportStatus);
//	}else{
//		auditPreReport(studyid,patientname,reportStatus);
//	}
//}

//初审常规报告
function auditPreReport(reportid,reportStatus) {
	//获取Cakey
	GetAllDeviceSN(call_back);
	setTimeout(function(){
		var devSN = $("#devSN").val();
		console.log(devSN);
	$('#progress_dlg').dialog('open');
	$('#reportform_' + reportid).form('submit',{
		url : window.localStorage.ctx+'/report/auditPreReport?devSN='+devSN,
		onSubmit : function() {
			beforeSaveOrAuditReport(reportid);
		},
		success : function(data) {
			var json = validationDataAll(data);
			console.log(json)
			if (json.code == 0) {
				$('#pre_audittime_' + reportid).html(json.data.pre_audittime);
				//$('#pre_auditphysician_name_' + reportid).html(json.data.pre_auditphysician_name);
				$('#pre_auditphysician_name_' + reportid).empty();
				$('#pre_auditphysician_name_' + reportid).append("<img src='/pt"+json.data.c_autograph+"' width=140 height=45 />");
				
				//document.getElementById('#pre_auditphysician_name_' + reportid).innerHTML('<img src="/pt/2021/7/16/9115-S.bmp" width=140 height=45 />');
				if(!$('#reportphysician_' + reportid).html()){
					$('#reporttime_' + reportid).html(json.data.reporttime);
					//$('#reportphysician_' + reportid).html(json.data.reportphysician_name);
					$('#reportphysician_' + reportid).empty();
					$('#reportphysician_' + reportid).append("<img src='/pt"+json.data.b_autograph+"' width=140 height=45 />");
				}
				$('#id_' + reportid).val(json.data.id);
				$('#orderStatus_' + reportid).val(json.data.reportstatus);
				$("#contentChangeflag_"+ reportid).val("");
				
				changeReportBtnEnable(json.data.reportstatus,reportid);
				$.messager.show({
					title : $.i18n.prop('tips'),
					msg : $.i18n.prop('savesuccess'),
					timeout : 3000,border : 'thin',showType : 'slide'
				});
				//refreshReportInfo(reportid, json.data.reportstatus);
				//refresh();
				refreshRow(reportid);
			} else {
				 $.messager.show({
		            title: $.i18n.prop('error'),
		            msg: $.i18n.prop('savefailed'),
		            timeout:3000,border:'thin',showType:'slide'
		        });
			}
			$('#progress_dlg').dialog('close');
		}
	});
	},800);
}





//关闭保存初审
//function preAuditSaveReport(studyid,patientname,reportStatus,obj){
//	$(obj).removeAttr('onclick');
//	var templetId = $("#templetId_"+studyid).val();
//	$('#progress_dlg').dialog('open');
//	if(templetId==0){
//		$('#reportform_' + studyid)
//		.form(
//				'submit',
//				{
//					url : window.localStorage.ctx+'/report/preAuditReport',
//					onSubmit : function() {
//						var ue_desc = UM.getEditor('desc_' + studyid
//								+ '_html');
//						$('#desc_' + studyid + '_txt').val(
//								ue_desc.getPlainTxt());
//						var ue_result = UM.getEditor('result_'
//								+ studyid + '_html');
//						$('#result_' + studyid + '_txt').val(
//								ue_result.getPlainTxt());
//					},
//					success : function(data) {
//						var json = validationData(data);
//						if (json.success == "success") {
//
//							$('#id_' + studyid).val(json.report.id);
//							$('#reporttime_' + studyid).html(
//									json.report.reporttime);
//							$('#reportphysician_' + studyid).html(
//									json.report.reportphysician_name);
//							$('#auditphysician_' + studyid)
//									.html(
//											json.report.pre_auditphysician_name);
//							$('#audittime_' + studyid).html(
//									json.report.pre_audittime);
//							$('#orderStatus_' + studyid).val(
//									json.report.reportstatus);
//							$("#saveFlag_"+studyid).val("1");
//							var title=patientname+"_"+studyid;
//							$("#templet_"+studyid).hide();
//							/*$('#tab').tabs('close', title);*/
//							$.messager.show({
//								title : $.i18n.prop('tips'),
//								msg : $.i18n.prop('savesuccess'),
//								timeout : 3000,
//								border : 'thin',
//								showType : 'slide'
//							});
//							
//						} else {
//							$.messager.show({
//					            title: $.i18n.prop('error'),
//					            msg: $.i18n.prop('savefailed'),
//					            timeout:3000,
//					            border:'thin',
//					            showType:'slide'
//					        });
//						}
//						$('#progress_dlg').dialog('close');
//						refresh();
//					}
//				});
//	} else{
//		//数据封装数组
//		var data = new Array(0);
//		//拿到所有的字段表单id
//		$("#structTemplet_"+studyid).find("label").each(function(index,obj){
//			var $fieldId= $(obj).attr("for");
//			var fieldCode = $("#" + $fieldId).attr("name");
//			var fieldValue = $("#" + $fieldId).val();
//			var field = new Object();
//			field.fieldCode = fieldCode;
//			field.fieldValue = fieldValue;
//			data.push(field);
//		});
//		$.ajax({
//				'url' : window.localStorage.ctx+'/report/preAuditStructReport',
//		        'type' : 'POST',
//		        'data' : {
//		        	's' : JSON.stringify(data),
//		        	'studyid' : studyid,
//		        	'templetId' : $("#templetId_"+studyid).val()
//		         },
//		        'dataType' : "JSON",
//		        'success' : function (res) {
//		        	$("#saveFlag_"+studyid).val("1");
//		        	var title=patientname+"_"+studyid;
//					/*$('#tab').tabs('close', title);*/
//		        	$("#orderStatus_"+res.data.studyid).val(res.data.reportstatus);
//		        	$("#templet_"+studyid).hide();
//		        	$.messager.show({
//						title : $.i18n.prop('tips'),
//						msg : $.i18n.prop('savesuccess'),
//						timeout : 3000,
//						border : 'thin',
//						showType : 'slide'
//					});
//		        	 $('#progress_dlg').dialog('close');
//			     	 refresh();
//		        }
//			});
//	}
//}
// 初审报告
//function preAuditReport(studyid,patientname,reportStatus,obj) {
//
//	var status = $("#orderStatus_"+studyid).val();
//	if(status=="F"){
//		$.messager.show({
//			title : $.i18n.prop('alert'),
//			msg : "报告已是最终审核无法进行初审",
//			timeout : 3000,
//			showType : 'slide'
//		});
//	}else{
//		$(obj).removeAttr('onclick');
//		var templetId = $("#templetId_"+studyid).val();
//		if (status == "" ||status == "P" || status == 'O') {
//			$('#progress_dlg').dialog('open');
//			if(templetId==0){
//				$('#reportform_' + studyid)
//				.form(
//						'submit',
//						{
//							url : window.localStorage.ctx+'/report/preAuditReport',
//							onSubmit : function() {
//								var ue_desc = UM.getEditor('desc_' + studyid
//										+ '_html');
//								$('#desc_' + studyid + '_txt').val(
//										ue_desc.getPlainTxt());
//								var ue_result = UM.getEditor('result_'
//										+ studyid + '_html');
//								$('#result_' + studyid + '_txt').val(
//										ue_result.getPlainTxt());
//							},
//							success : function(data) {
//								var json = validationData(data);
//								if (json.success == "success") {
//
//									$('#id_' + studyid).val(json.report.id);
//									$('#reporttime_' + studyid).html(
//											json.report.reporttime);
//									$('#reportphysician_' + studyid).html(
//											json.report.reportphysician_name);
//									$('#auditphysician_' + studyid)
//											.html(
//													json.report.pre_auditphysician_name);
//									$('#audittime_' + studyid).html(
//											json.report.pre_audittime);
//									$('#orderStatus_' + studyid).val(
//											json.report.reportstatus);
//									$("#saveFlag_"+studyid).val("1");
//									var title=patientname+"_"+studyid;
//									$("#templet_"+studyid).hide();
//									/*$('#tab').tabs('close', title);*/
//									var flag=$("#saveFlag_"+studyid).val();
//									if(flag==1){
//										$.messager.show({
//											title : '提醒',
//											msg : "初审成功！",
//											timeout : 3000,
//											showType : 'slide'
//										});
//									}else{
//										$.messager.show({
//											title : $.i18n.prop('tips'),
//											msg : $.i18n.prop('savesuccess'),
//											timeout : 3000,
//											border : 'thin',
//											showType : 'slide'
//										});
//									}
//									
//								} else {
//									if(flag==1){
//										$.messager.show({
//											title : '提醒',
//											msg : "初审失败请重试，如果问题依然存在，请联系系统管理员！",
//											timeout : 3000,
//											showType : 'slide'
//										});
//									}else{
//										$.messager.show({
//								            title: $.i18n.prop('error'),
//								            msg: $.i18n.prop('savefailed'),
//								            timeout:3000,
//								            border:'thin',
//								            showType:'slide'
//								        });
//									}
//								}
//								$('#progress_dlg').dialog('close');
//								refresh();
//							}
//						});
//			} else{
//				//数据封装数组
//				var data = new Array(0);
//				//拿到所有的字段表单id
//				$("#structTemplet_"+studyid).find("label").each(function(index,obj){
//					var $fieldId= $(obj).attr("for");
//					var fieldCode = $("#" + $fieldId).attr("name");
//					var fieldValue = $("#" + $fieldId).val();
//					var field = new Object();
//					field.fieldCode = fieldCode;
//					field.fieldValue = fieldValue;
//					data.push(field);
//				});
//				$.ajax({
//						'url' : window.localStorage.ctx+'/report/preAuditStructReport',
//				        'type' : 'POST',
//				        'data' : {
//				        	's' : JSON.stringify(data),
//				        	'studyid' : studyid,
//				        	'templetId' : $("#templetId_"+studyid).val()
//				         },
//				        'dataType' : "JSON",
//				        'success' : function (res) {
//				        	$("#saveFlag_"+studyid).val("1");
//				        	var title=patientname+"_"+studyid;
//							/*$('#tab').tabs('close', title);*/
//				        	$("#orderStatus_"+res.data.studyid).val(res.data.reportstatus);
//				        	$("#templet_"+studyid).hide();
//				        	$.messager.show({
//								title : '提醒',
//								msg : "初审成功!",
//								timeout : 3000,
//								showType : 'slide'
//							});
//				        	 $('#progress_dlg').dialog('close');
//					     	 refresh();
//				        }
//					});
//			}
//			
//
//		} else {
//			$.messager.show({
//				title : '提醒',
//				msg : "报告已经被初审，无法再次初审！",
//				timeout : 3000,
//				showType : 'slide'
//			});
//		}
//	}
//	
//}


//function auditReportInfo(studyid,patientname,reportStatus){
//	if($("#srtemplateid_"+studyid).val()){
//		auditStructReport(studyid,patientname,reportStatus);
//	}
//	else{
//		auditReport(studyid,patientname,reportStatus);
//	}
//}

// 审核报告
function auditReport(reportid,reportStatus) {
	var userid = $("#userid_hidden").val();
	var openGrade = $("#openGrade").val();  // 1，报告医生；2，审核医生；3，主任
	var auditphysician = $("#auditphysicianid_" + reportid).val();
	console.log("审核：" + userid + "," + openGrade + "," + auditphysician);
	
	if (auditphysician != "" && auditphysician != undefined && "2" == openGrade && userid != auditphysician) {
		_message("您没有审核别人审核过报告的权限！");
		auditbtn = true;
		return;
	}
	
	
	var caFlag=$("#caFlag_"+reportid).val();
	//判断是否开启ca
	if(caFlag=="1"){
		//获取Cakey
		GetAllDeviceSN(call_back);
		setTimeout(function(){
			var devSN = $("#devSN").val();
			if(devSN!=null&&devSN!=""){
				console.log(devSN);
				$('#progress_dlg').dialog('open');
					$('#reportform_' + reportid).form('submit',
							{
								url : window.localStorage.ctx+'/report/auditReport',
								onSubmit : function() {
									$("#devSN1").val(devSN);
//									beforeSaveOrAuditReport(reportid);
								},
								success : function(data) {
									var json = validationDataAll(data);
									// 提交后清空sn
									$("#devSN1").val("");
									if (json.code==0) {
										// 审核成功后是否自动关闭
										var report_audit = $("#report_audit").val();
										console.log("审核后是否关闭：" + report_audit);
										if ("1" == report_audit) {
											$("#enable_refresh_" + reportid).val("1");
											closeTab(reportid);
										}else {
										$('#id_' + reportid).val(json.data.id);
										//$('#auditphysician_' + reportid).html(json.data.auditphysician_name);
										$('#audittime_' + reportid).html(json.data.audittime);
										
										$('#auditphysician_' + reportid).empty();
										$('#auditphysician_' + reportid).append("<img src='/pt"+json.data.s_autograph+"' width=140 height=45 />");
										
										//$('#reportphysician_' + reportid).html(json.data.reportphysician_name);
										$('#reportphysician_' + reportid).empty();
										$('#reportphysician_' + reportid).append("<img src='/pt"+json.data.b_autograph+"' width=140 height=45 />");
										
										$('#reporttime_' + reportid).html(json.data.reporttime);
										if($('#pre_auditphysician_name_' + reportid)[0]){
											if(!$('#pre_auditphysician_name_' + reportid).html()){
												$('#pre_audittime_' + reportid).html(json.data.pre_audittime);
												//$('#pre_auditphysician_name_' + reportid).html(json.data.pre_auditphysician_name);
												$('#pre_auditphysician_name_' + reportid).empty();
												$('#pre_auditphysician_name_' + reportid).append("<img src='/pt"+json.data.c_autograph+"' width=140 height=45 />");
											}
										}
										
										$('#orderStatus_' + reportid).val(json.data.reportstatus);
										$("#contentChangeflag_"+ reportid).val("");
										
										disableEditReport(reportid);
										
										changeReportBtnEnable(json.data.reportstatus,reportid);
										if($("#image_container_"+reportid)[0]){
											$("#image_container_"+reportid).panel('refresh');
										}
										$.messager.show({
											title : $.i18n.prop('tips'),
											msg : $.i18n.prop('report.auditreportsuccess'),
											timeout : 3000,
											border:'thin',
											showType : 'slide'
										});
										//refreshReportInfo(reportid, json.data.reportstatus);
										//refresh();
										refreshRow(reportid);
									}
									} else if(json.code==-2) {
										_message(json.message);
									}else{
										$.messager.show({
											title : $.i18n.prop('error'),
											msg : $.i18n.prop('report.auditreportfailed'),
											timeout : 3000,
											border:'thin',
											showType : 'slide'
										});
									}
									afterSaveOrAuditReport(reportid);
									$('#progress_dlg').dialog('close');
									auditbtn = true;
								}
							});
				
			}else{
				auditbtn = true;
				_message("没有插入key");
			}
		},800);
	}else{
		$('#progress_dlg').dialog('open');
		$('#reportform_' + reportid).form('submit',
				{
					url : window.localStorage.ctx+'/report/auditReport',
					onSubmit : function() {
//						beforeSaveOrAuditReport(reportid);
					},
					success : function(data) {
						var json = validationDataAll(data);
						if (json.code==0) {
							// 审核成功后是否自动关闭
							var report_audit = $("#report_audit").val();
							console.log("审核后是否关闭：" + report_audit);
							if ("1" == report_audit) {
								$("#enable_refresh_" + reportid).val("1");
								closeTab(reportid);
							} else {
							$('#id_' + reportid).val(json.data.id);
							$('#auditphysician_' + reportid).html(json.data.auditphysician_name);
							$('#audittime_' + reportid).html(json.data.audittime);
							
							var reportphysician_name = $("#reportphysician_name_" + reportid).val();
							if (reportphysician_name == undefined || reportphysician_name == "" || reportphysician_name == null) {
								$('#reportphysician_' + reportid).html(json.data.auditphysician_name);
								$('#reporttime_' + reportid).html(json.data.audittime);
							}
							
							if(!$('#reportphysician_' + reportid).html()){
								$('#reportphysician_' + reportid).html(json.data.reportphysician_name);
								$('#reporttime_' + reportid).html(json.data.reporttime);
							}
							
							if($('#pre_auditphysician_name_' + reportid)[0]){
								if(!$('#pre_auditphysician_name_' + reportid).html()){
									$('#pre_audittime_' + reportid).html(json.data.pre_audittime);
									$('#pre_auditphysician_name_' + reportid).html(json.data.pre_auditphysician_name);
									//$('#pre_auditphysician_name_' + reportid).empty();
									//$('#pre_auditphysician_name_' + reportid).append("<img src='/pt"+json.data.c_autograph+"' width=140 height=45 />");
								}
							}
							
							$('#orderStatus_' + reportid).val(json.data.reportstatus);
							$("#contentChangeflag_"+ reportid).val("");
							
							disableEditReport(reportid);
							
							changeReportBtnEnable(json.data.reportstatus,reportid);
							if($("#image_container_"+reportid)[0]){
								$("#image_container_"+reportid).panel('refresh');
							}
							$.messager.show({
								title : $.i18n.prop('tips'),
								msg : $.i18n.prop('report.auditreportsuccess'),
								timeout : 3000,
								border:'thin',
								showType : 'slide'
							});
							//refreshReportInfo(reportid, json.data.reportstatus);
							//refresh();
							refreshRow(reportid);
							}
						} else if(json.code==-2) {
							_message(json.message);
						} else {
							$.messager.show({
								title : $.i18n.prop('error'),
								msg : $.i18n.prop('report.auditreportfailed'),
								timeout : 3000,
								border:'thin',
								showType : 'slide'
							});
						}
						afterSaveOrAuditReport(reportid);
						$('#progress_dlg').dialog('close');
						auditbtn = true;
					}
				});
	}

//	} else {
//		$.messager.show({
//			title : '提醒',
//			msg : "报告已经审核，无法再次审核！",
//			timeout : 3000,
//			showType : 'slide'
//		});
//	}

}

function afterSaveOrAuditReport(reportid) {
	console.log('afterSaveOrAuditReport');
	enbaleAutoSaveReport();
}

function enableEditReport(reportstatus,reportid){
	$('#orderStatus_' + reportid).val(reportstatus);
	console.log("srtemplateid_"+reportid+":"+$("#srtemplateid_"+reportid).val());
	if($("#srtemplateid_" + reportid).val()){
//		var data=$("#via_findings_" + reportid).datagrid("getData");
//		if(data && data.rows && data.rows.length>0){
//			$('#importbtn_' + reportid).linkbutton('enable');
//		}
	}
	else{
		$("#audi_image_"+reportid).hide();
		
		var index=-1;
		var tabid="tab_"+reportid;
		for(var i=0; i<$('#tab').tabs('tabs').length; i++){
			var id=$('#tab').tabs('getTab',i).panel('options').id;
			if(id==tabid){
				index=i;
			}
		}
		
		if($('#tab').tabs('getTab',index)){
			UM.getEditor('desc_' + reportid + '_html').setEnabled();
			UM.getEditor('result_' + reportid + '_html').setEnabled();
			$("#method_" + reportid).textbox('enable');
			
			/*$("#urgent_" + reportid).checkbox('enable');
			if($('#urgent_' + reportid).checkbox('options').checked){
				$("#urgent_explain_" + reportid).textbox('enable');
			}*/
			$("#pos_" + reportid).radiobutton('enable');
			$("#neg_" + reportid).radiobutton('enable');
			$("#imagequality_"+reportid).combobox('enable');
			$("#reportquality_"+reportid).combobox('enable');
			$("#diagnosis_coincidence_"+reportid).combobox('enable');
		}
	}
}
function disableEditReport(reportid){
	
	if(!$("#srtemplateid_" + reportid).val()){
		//$("#flag_"+studyid).prepend("<img alt='' src='/img/flag.png' style='position:absolute;left:550px;top:50px;'>");
		$("#audi_image_" + reportid).show();
		$("#reportstudyitem_" + reportid).textbox('disable');
		$("#method_" + reportid).textbox('disable');
		try{
			UM.getEditor('desc_' + reportid + '_html').setDisabled();
			UM.getEditor('result_' + reportid + '_html').setDisabled();
		}catch(e){
			console.log(e);
		}
		if($("#urgent_" + reportid)[0]){
			$("#urgent_" + reportid).checkbox('disable');
			$("#urgent_explain_" + reportid).textbox('disable');
		}
		$("#pos_" + reportid).radiobutton('disable');
		$("#neg_" + reportid).radiobutton('disable');
		$("#imagequality_" + reportid).combobox('disable');
		$("#reportquality_"+reportid).combobox('disable');
		$("#diagnosis_coincidence_"+reportid).combobox('disable');

		//设置报告图像容器editable=false,禁止删除，拖动图像
		var container=ReportImageContainer.getCatch(reportid,'img_ul');
		console.log(container)
		if(container){
			container.setEditable(false);
		}
	}
}

function beforeRejectReport(reportid){
	openRejectOpinionDialog(reportid);
	// rejectReport(reportid);
}

// 驳回报告
function rejectReport(reportid) {
	$('#progress_dlg').dialog('open');
	var obj={
			id : $("#id_" + reportid).val(),
			orderid : $('#orderid_' + reportid).val(),
			status : $('#orderStatus_' + reportid).val()
		};
	$.post(window.localStorage.ctx+'/report/rejectReport',obj, function(data) {
		var json = validationDataAll(data);
		if (json.code==0) {
			$('#orderStatus_' + reportid).val(json.data);
			changeReportBtnEnable(json.data,reportid);
			$.messager.show({
				title : '提醒',
				msg : "驳回成功!",
				timeout : 3000,
				border:'thin',
				showType : 'slide'
			});
			//refreshReportInfo(reportid, json.data);
			//refresh();
			refreshRow(reportid);
		}
		else {
			$.messager.show({
				title : $.i18n.prop('error'),
				msg : '驳回报告失败，请重试。如果问题依然存在，请联系系统管理员！',
				timeout : 3000,
				border:'thin',
				showType : 'slide'
			});
		}
		
		$('#progress_dlg').dialog('close');
	});
}



var cardview_history = $.extend({}, $.fn.datagrid.defaults.view, {
    renderRow: function(target, fields, frozen, rowIndex, rowData){
        var cc = [];
        if(rowData.studyid){
            cc.push('<td colspan=' + fields.length + ' style="padding:2px 2px;border:0px;">');
            	
                cc.push('<div style="margin-top:2px;">');
                
                cc.push('<div style="margin-left:0px;width:100%; heigh:200px; border: 0px;">');
                cc.push('<span style="text-align:left;color:#1A7BC9">【'+$.i18n.prop('report.studyitem')+'】</span>');
                var imgbtns='<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
                if(rowData.plazapara){
                	imgbtns+='<a href="'+rowData.plazapara +'" style="width:80px;height:28px" onclick="closeReportFlag=false;urlFlag = true;">'+$.i18n.prop('wl.image')+'(p)</a>';
                }
                if(rowData.viapara){
                	imgbtns+='<a href="'+rowData.viapara +'" style="width:80px;height:28px" onclick="closeReportFlag=false;urlFlag = true;">'+$.i18n.prop('wl.image')+'(v)</a>';
                }
                cc.push(imgbtns+'</span>');
                cc.push('<div style="margin-left:2px;">'+(rowData.studyitems||"") +'</div>');
                cc.push('</div>');
                cc.push('<div style="margin-left:2px;width:100%; heigh:20px; border: 0px;">');
                cc.push('<span style="text-align:left;">【'+$.i18n.prop('report.studyid')+'】</span>');
                cc.push('<span>'+rowData.studyid+'</span>');
                cc.push('</div>');
                cc.push('<div style="margin-left:2px;width:100%; heigh:20px; border: 0px;">');
                cc.push('<span style="text-align:left;">【'+$.i18n.prop('wl.studytime')+'】</span>');
                cc.push('<div style="margin-left:2px;">'+(rowData.studydatetime==null?rowData.regdatetime:rowData.studydatetime)+'</div>');
                cc.push('</div>');

                cc.push('</div>');
            cc.push('</td>');
        }
        return cc.join('');
    }
});
	
//function setCardview_history(patientid,reportid){

//	if(index==1){
//	//历史
//		$.getJSON(window.localStorage.ctx+"/report/historyReport?patientid="+patientid,function(json) {
//			var reporthistory = validationData(json);
//			
			
//			$('#history_report_'+reportid).datagrid({
//	            view: cardview_history
//			});
			
//			$('#history_report_'+studyid).datagrid("loadData",reporthistory);
			
//		});
//	}
//}

function contrastImage(index,row,reportid){
	if($('#contrastimg_btn_'+reportid)[0]){
		var basehref=$('#contrastimg_btn_'+reportid).attr('href1');
		var href='';
		var rows=$('#history_report_'+reportid).datagrid('getSelections');
		for(var i=0;i<rows.length;i++){
			var s = rows[i].studyid;
			href+=","+s;
		}
		if(href){
			$('#contrastimg_btn_'+reportid).attr('href',basehref+href);
		}
		else{
			$('#contrastimg_btn_'+reportid).removeAttr("href");
		}
		console.log(basehref+href);
	}
}


/**
 * 历史记录检索
 * 
 * @returns
 */
function doHistoryReportSearch(value,patientidfk,thisOrderid,reportid) {
	if(value.trim()==""){
		$.getJSON(window.localStorage.ctx+"/report/historyReport?patientidfk="+patientidfk+"&thisOrderid="+thisOrderid,function(json) {
			var reporthistory = validationData(json);
			$('#history_report_'+reportid).datagrid("loadData",reporthistory);
		});
	}else{
		$.getJSON(window.localStorage.ctx+"/report/historyReport?patientidfk="+patientidfk+"&thisOrderid="+thisOrderid+"&studyid="+value,function(json) {
			var reporthistory = validationData(json);
			$('#history_report_'+reportid).datagrid("loadData",reporthistory);
		});
	}
	
}


function print_sr(reportid){
	UM.getEditor('structTemplet_'+reportid).execCommand( 'print' );
	
//	var html=UE.getEditor('structTemplet_'+studyid).getContent();
//	alert(html)
//	$("#temp_"+studyid).html(html);
//	$("#temp_"+studyid).print({ 
//		globalStyles : false,
//        //Add link with attrbute media=print
//        mediaPrint : false,
//        iframe:true,prepend:'<br/>'}); 
//	$("#temp_"+studyid).html("");
	
//	$("#structTemplet_"+studyid).print({iframe:true,prepend:'<br/>'}); 
	
//	$("#printFrame").attr("srcdoc",html);
//	$("#printdlg_"+studyid).dialog('open');
}

//function cleanReportRemarkFiled(studyid){
//	$('#reportremarkid_'+studyid).val("");
//	$('#remark_'+studyid).textbox('setValue',"");
//	$('#remark_'+studyid).textbox('readonly',false);
//	$('#reportremarkreadonly_'+studyid).val(false);
//}

function openRemarkDialog(reportid,orderid){
	
	$('#common_dialog').dialog({
		title:'添加备注',
		width : 460,height : 450,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,
		border: 'thin',
		href : window.localStorage.ctx+'/report/goToEditremark?reportid='+reportid+'&orderid='+orderid,
		modal : true,
		buttons:[{
			text:'关闭',
			width:60,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}



function delReportRemark(reportid){
	var row=$('#reportremark_dg_'+reportid).datagrid("getSelected");
	if(row!=null){
		if(row.id){
			$.messager.confirm({
				title: $.i18n.prop('confirm'),
				msg: '确认删除选择的备注！',
				border:'thin',
				fn: function(r){
					if (r){
						 $.getJSON(window.localStorage.ctx+"/report/delReportRemark?id="+row.id, function(json){
							 	var js=validationData(json);
						    	if(js.code==0){
						    		/*var index=$('#reportremark_dg_'+orderid).datagrid("getRowIndex");
						    		$('#reportremark_dg_'+orderid).datagrid("deleteRow",index);*/
						    		$('#reportremark_dg_'+reportid).datagrid("reload");
						    	}
						    	else{
						    		$.messager.show({
						                title:$.i18n.prop('error'),
						                msg: js.message,
						                timeout:3000,
						                showType:'slide'
						            });
						    	}
						 });
					}
				}
			});
		}
		else{
			$.messager.show({
				title : $.i18n.prop('alert'),
				msg : "无法删除！",
				timeout : 3000,
				border : 'thin',
				showType : 'slide'
			});
		}
	}
	else{
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : "请选择一个备注！",
			timeout : 3000,
			border : 'thin',
			showType : 'slide'
		});
	}
}

function formatProgress(value){
    if (value){
        var s = '<div style="width:100%;border:1px solid #ccc">' +
                '<div style="width:' + value + '%;background:#cc0000;color:#fff">' + value + '%' + '</div>'
                '</div>';
        return s;
    } else {
        return '';
    }
}


function savereportremark(studyid,record){
	console.log(studyid);
	var orderid=record.orderid;
	var reportid=record.reportid;
	if(reportid){
		var remark=$('#remark_content').textbox('getValue');
		
		$.post(window.localStorage.ctx+'/report/saveRemark',
		    {
				'id':$('#reportremarkid_'+reportid).val(),
		    	'studyid' : studyid,
		    	'patientidfk' : record.patientidfk,
		    	'admissionidfk' : record.admissionidfk,
		    	'orderid' : orderid,
		    	'remarkcontent' : remark,
		    	'type' : 'report'  	
		     },
		     function (res) {
	        	var json = validationDataAll(res);
	        	if(json.code==0){
	        		$('#reportremark_dg_'+reportid).datagrid("reload");
	        		$('#common_dialog').dialog('refresh', window.localStorage.ctx+'/report/goToEditremark?reportid='+reportid+'&orderid='+orderid);
	        	}
	        	else{
	        		$.messager.show({
						title : $.i18n.prop('error'),
						msg : $.i18n.prop('savefailed'),
						timeout : 3000,
						border : 'thin',
						showType : 'slide'
					});
	        	}
	        }
		);
		
	}
	else{
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : "请先保存报告",
			timeout : 3000,
			border : 'thin',
			showType : 'slide'
		});
	}

}

function openLabelDialog(){
	
	$('#common_dialog').dialog({
		title:'管理个人标签',
		width : 460,height : 450,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,
		border: 'thin',
		href : window.localStorage.ctx+'/report/gotoManageLabel',
		modal : true,
		buttons:[{
			text:'新建根文件夹',
			width:120,
			handler:function(){addRootNode_label(null);}
		},{
			text:'关闭',
			width:60,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}


/**
 * 收藏夹
 * 
 * @param studyid
 * @returns
 */
function openFavoritesDialog(reportid, orderid){
	$('#common_dialog').dialog(
			{
				title : $.i18n.prop('wl.favoritesreport'),
				width : 550,height : 500,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,
				border: 'thin',
				href : window.localStorage.ctx+'/report/goFavorites?reportId='+reportid+'&orderid='+orderid,
				modal : true,
				buttons:[
//					{
//					text: $.i18n.prop('wl.newfolder'),
//					width:100,
//					handler:function(){addFavoritesNode('0');}
//				},
				{
					text: $.i18n.prop('wl.newfolder'),
					width:120,
					handler:function(){addFavoritesNode('1');}
				},
//				{
//					text: $.i18n.prop('wl.delete'),
//					width:60,
//					handler:function(){deleteFavoritesnode();}
//				},
				{
					text: $.i18n.prop('save'),
					width:60,
					handler:function(){saveFavorites(reportid);}
				},{
					text: $.i18n.prop('cancel'),
					width:60,
					handler:function(){$('#common_dialog').dialog('close');}
				}]
	});
}

/**
 * 添加文件夹
 * @param ischild
 * @returns
 */
function addFavoritesNode(ischild) {
	 var node = $('#favorites_tree').tree('getSelected');
	 if (!node) {
		 node = $('#favorites_tree').tree('getRoot');
	 }
	 $.getJSON(window.localStorage.ctx+"/report/addFavoritesNode?name="+$.i18n.prop('report.folder')+"&parent_id="+node.id, function(json){
		 if (json.success=="success") {
			 	$('#favorites_tree').tree('append', {
					parent: node.target,
					data: {
						id: json.data.id,
						text: $.i18n.prop('report.folder')
					}
				});
			var node1 = $('#favorites_tree').tree('find',  json.data.id);
			$("#favorites_tree").tree('beginEdit', node1.target);
			$('#myfavorites_tree_search').tree('reload');
		 } else {
			 $.messager.show({
	            title: $.i18n.prop('error'),
	            msg: $.i18n.prop('savefailed'),
	            timeout:3000,
	            border:'thin',
	            showType:'slide'
	        });
		 }
	 });
}

/**
 * 收藏报告
 * @param reportId
 * @returns
 */
function saveFavorites(reportId) {
	var desc = $("#reportdesc").textbox("getValue");
	if (desc.trim() == "") {
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : '请输入名称',
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
		return;
	}
	var node = $('#favorites_tree').tree('getSelected');
	if (node) {
		$.post(window.localStorage.ctx+"/report/saveFavorites",{reportId : reportId,favoritesId : node.id,reportDesc : desc},function(json){
			if (json.success="success") {
				$('#favoritesreport_list').datalist('reload',{nodeid:node.id});
				$('#myfavorites_tree_search').tree('reload');
			} else {
				$.messager.show({
					title : $.i18n.prop('error'),
					msg : $.i18n.prop('savefailed'),
					timeout : 3000,
					border:'thin',
					showType : 'slide'
				});
			}
		});
	} else {
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : $.i18n.prop('report.selectfolder'),
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
	}
}

//*************************痕迹对比***********************************************
//打开痕迹对比窗口
function goToComparetrace(reportid){
	getJSON(window.localStorage.ctx+"/report/getAllReporttrace",
			{
				reportid : reportid
			},function(json){
				var data=validationData(json);
				if(data && data.length > 1){
					$('#common_dialog').dialog({
						title:'报告痕迹对比',
						width:1300,height:700,
						resizable: false,minimizable: false,maximizable: false,modal : true,
						border: 'thin',
						href: window.localStorage.ctx+"/report/goToComparetrace?reportid="+reportid,
						buttons:[{
							text:'关闭',
							width:80,
							handler:function(){$('#common_dialog').dialog('close');}
						}],
						onLoad:function(){
							$("#reporttracedg_").datagrid("loadData",json);
						}
					});
				}else{
					$.messager.alert({title:'提示',msg:'该报告没有痕迹记录!',border:'thin'});
				}
			});
}


function selectReporttraceDg(index,row){
	var rows=$('#reporttracedg_').datagrid('getSelections');
	if(rows.length==2){
		$('#report_comparison').linkbutton('enable');
	}
	else{
		$('#report_comparison').linkbutton('disable');
	}
}

//设置按钮可用
function clickReporttraceDg(index,row){
	$('#initial_report').linkbutton('enable');
	$('#reference_report').linkbutton('enable');
}
//初始报告
function initial_report(){
	var row=$('#reporttracedg_').datagrid('getSelected');
	if(row){
		var index = $('#reporttracedg_').datagrid('getRowIndex',row);
		console.log("1:"+index);
		console.log("2:"+ $("#reference_report_index").val());
		console.log(index+"" === $("#reference_report_index").val());
		if(index+"" === $("#reference_report_index").val()){
			return;
		}
		$("#initial_report_index").val(index);
		$("#initial_report_txt").textbox('setValue',(row.operatorname || "")+" "+row.reportstatusdisplay+" "+row.modifytime);
		if($("#initial_report_txt").val()!="" && $("#reference_report_txt").val()!=""){
			$('#report_comparison').linkbutton('enable');
		}
	}else{
		$.messager.show({
			title:'提醒',
            msg:"请选择一份数据！",
            timeout:3000,
            border:'thin',
            showType:'slide'
		});
	}
}
//参照报告
function reference_report(){
	var row=$('#reporttracedg_').datagrid('getSelected');
	if(row){
		var index = $('#reporttracedg_').datagrid('getRowIndex',row);
		console.log("1:"+index);
		console.log("2:"+ $("#initial_report_index").val());
		console.log(index+"" === $("#initial_report_index").val());
		if(index+"" === $("#initial_report_index").val()){
			return;
		}
		$("#reference_report_index").val(index);
		$("#reference_report_txt").textbox('setValue',(row.operatorname || "")+" "+row.reportstatusdisplay+" "+row.modifytime);
		if($("#initial_report_txt").val()!="" && $("#reference_report_txt").val()!=""){
			$('#report_comparison').linkbutton('enable');
		}
	}else{
		$.messager.show({
			title:'提醒',
            msg:"请选择一份数据！",
            timeout:3000,
            border:'thin',
            showType:'slide'
		});
	}
}
//进行报告痕迹对比
function report_comparison(){
	/*if($('#initial_report_index').val() === "" && $('#reference_report_index').val() === ""){
		return;
	}
	var rows = $('#reporttracedg_').datagrid('getRows');*/
	var rows=$('#reporttracedg_').datagrid('getSelections');
	var initial_report = rows[0];
	var reference_report = rows[1];
	var center=$('#reportCompare_layout').layout('panel','center');
	center.panel('setTitle',(rows[0].operatorname || "")+" "+rows[0].reportstatusdisplay+" "+rows[0].modifytime);
	
	var east=$('#reportCompare_layout').layout('panel','east');
	east.panel('setTitle',(rows[1].operatorname || "")+" "+rows[1].reportstatusdisplay+" "+rows[1].modifytime);
	
	getJSON(window.localStorage.ctx+"/report/reportComparing",
			{
				before_desc : initial_report.checkdesc_txt,
				before_result : initial_report.checkresult_txt,
				after_desc : reference_report.checkdesc_txt,
				after_result : reference_report.checkresult_txt,
				reportid : $('#reportid_compare').val()
			},
			function(json){
				var data=validationData(json);
				console.log(data);
				var content1 = data.checkdesc[0];
				var content2 = data.checkresult[0];
				var content3 = data.checkdesc[1];
				var content4 = data.checkresult[1];
				$('#mypanel_compare1').panel({
			        content : content1
			    });
				$('#mypanel_compare2').panel({
			        content : content2
			    });
				$('#mypanel_compare3').panel({
			        content : content3
			    });
				$('#mypanel_compare4').panel({
			        content : content4
			    });
				
				$("#studymethod_compare1").textbox('setValue',initial_report.studymethod);
				$("#studymethod_compare2").textbox('setValue',reference_report.studymethod);
				
				$("#reportphysician_name_compare1").textbox('setValue',initial_report.reportphysician_name);
				$("#reporttime_compare1").textbox('setValue',initial_report.reporttime);
				$("#auditphysician_name_compare1").textbox('setValue',initial_report.auditphysician_name);
				$("#audittime_compare1").textbox('setValue',initial_report.audittime);
				
				$("#reportphysician_name_compare2").textbox('setValue',reference_report.reportphysician_name);
				$("#reporttime_compare2").textbox('setValue',reference_report.reporttime);
				$("#auditphysician_name_compare2").textbox('setValue',reference_report.auditphysician_name);
				$("#audittime_compare2").textbox('setValue',reference_report.audittime);
			});
}
//报告预览
function reportpreview_compare(row){
	var content5 = row.checkdesc_txt;
	var content6 = row.checkresult_txt;
	$('#mypanel_compare5').panel({
        content : content5
    });
	$('#mypanel_compare6').panel({
        content : content6
    });
	$("#studymethod_compare3").textbox('setValue',row.studymethod);
	
	$("#reportphysician_name_compare3").textbox('setValue',row.reportphysician_name);
	$("#reporttime_compare3").textbox('setValue',row.reporttime);
	$("#auditphysician_name_compare3").textbox('setValue',row.auditphysician_name);
	$("#audittime_compare3").textbox('setValue',row.audittime);
}
//*************************痕迹对比***********************************************

// 用于存需要保存查询到的icd10
var saveSearchICD10List = new Array();

/**
 * 查找icd10的页面
 * @param reportid  打开报告的id
 * @returns
 */
function searchIcd10(reportid) {
	saveSearchICD10List = null;
	$('#common_dialog').dialog({
		title: 'ICD查询',
		width : 900,
		height : 450,
		closed : false,
		cache : false,
		border: 'thin',
		href: window.localStorage.ctx+'/report/goToSearchIcd10?reportid=' + reportid,
		modal : true,
		buttons:[{
			text:"确定",
			width:80,
			handler:function(){addSelectIcd10(reportid, false);}
		},{
			text:"保存查询",
			width:80,
			handler:function(){saveSearchICD10();}
		},{
			text:"添加",
			width:80,
			handler:function(){addSelectIcd10(reportid, true);}
		},{
			text:$.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

/**
 * 添加新的icd10标签
 * @param reportid  报告的id
 * @param type 判断是添加（true），还是替换（false）
 * @returns
 */
function addSelectIcd10(reportid, type) {
	// 下拉框的所有选项
	var data = [];
	// 获取已选中的icd10  
	var icd10s = $('#icd10dg').datagrid('getSelections');
	if (icd10s.length > 0) {
		var text = $('#icd10labelct_' + reportid).combobox('getText');
		if (type && text != "") {
			// 将选中的值添加到下拉框里
			var values = $('#icd10labelct_' + reportid).combobox('getValues');
			var tests = text.split(",");
			// 先添加原先存在的
			for (var i = 0; i < tests.length; i++) {
				data.push({"id": values[i], "text": tests[i], "selected": true});
			}
			// 再添加后加的
			for (var i = 0; i < icd10s.length; i++) {
				if (icd10s[i].id) {
					var count = 0;
					// 判断添加的数据，原先存不存在
					for (var j = 0; j < values.length; j++) {
						if (icd10s[i].id == values[j]) {
							count = count + 1;
						}
					}
					// 不存在，在进行添加
					if (count == 0) {
						data.push({"id": icd10s[i].id, "text": icd10s[i].icd_code + icd10s[i].icd_name, "selected": true});
					}
				}
			}
		} else {
			// 直接将选中的值替换掉下拉框里的值
			for (var i = 0; i < icd10s.length; i++) {
				if (icd10s[i].id) {
					data.push({"id": icd10s[i].id, "text": icd10s[i].icd_code + icd10s[i].icd_name, "selected": true});
				}
			}
		}
		$('#icd10labelct_' + reportid).combobox('clear');
		$('#icd10labelct_' + reportid).combobox('loadData',validationData(data));
		$('#common_dialog').dialog('close');
	} else {
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : "请选择需要添加的疾病",
			timeout : 3000,
			border : 'thin',
			showType : 'slide'
		});
	}
}

/**
 * ICD10 -- 保存查询
 * @param reportid
 * @returns
 */
function saveSearchICD10() {
	var icd10s = $('#icd10dg').datagrid('getSelections');
	if (icd10s.length != 0) {
		if (saveSearchICD10List == null) {
			saveSearchICD10List = icd10s;
			saveSearchICD10List.push("");
		} else {
			saveSearchICD10List.pop("");
			integratedCollection(saveSearchICD10List, icd10s);
			saveSearchICD10List.push("");
		}
	} else {
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : "请选择需要保存查询的数据",
			timeout : 3000,
			border : 'thin',
			showType : 'slide'
		});
	}
}

/**
 *  将两个集合整合为一个集合
 * @param list 原集合数据
 * @param data 需要添加到list（原集合数据）中的 集合
 * @returns
 */
function integratedCollection(list ,data) {
	for (var i = 0; i < data.length; i++) {
		var count = 0;
		for (var j = 0; j < list.length; j++) {
			// 判断此数据是否已经存在list集合里
			if (data[i].id == list[j].id) {
				count++;
			}
		}
		if (count == 0) {
			// 此数据不存在集合里，对此数据进行添加
			list.push(data[i]);
		}
	}
	// 返回添加好的集合
	return list;
}

/**
 *  根据条件查找dic10
 * @returns
 */
function findDic10(reportid) {
	$('#progress_dlg').dialog('open');
	var icdCode = $("#icdCode_" + reportid).val();
	var icdName = $("#icdName_" + reportid).val();
	getJSON(window.localStorage.ctx+"/report/findDic10",
			{
				icdCode: icdCode,
				icdName: icdName
			}
	,function(data) {
		$('#progress_dlg').dialog('close');
		if (saveSearchICD10List != null) {
			data = integratedCollection(JSON.parse(JSON.stringify(saveSearchICD10List)), data);
		}
		$("#icd10dg").datagrid("loadData", validationData(data));
		if (saveSearchICD10List != null) {
			// 将 保存查询 的数据设置为选中状态
			for (var i = 0; i < saveSearchICD10List.length; i++) {
				$("#icd10dg").datagrid("selectRow", i);
			}
		}
	});
}

/**
 *  动态改变选中的icd10
 * @param node
 * @param checked
 * @param reportid
 * @returns
 */
function checkDic10(node, reportid) {
	// 获取选中的节点
	getJSON(window.localStorage.ctx+"/report/findDic10ByNode?icdIndex=" + node.id,{},function(data) {
		if (saveSearchICD10List != null) {
			data = integratedCollection(JSON.parse(JSON.stringify(saveSearchICD10List)), data);
		}
		$("#icd10dg").datagrid("loadData", validationData(data));
		if (saveSearchICD10List != null) {
			// 将 保存查询 的数据设置为选中状态
			for (var i = 0; i < saveSearchICD10List.length; i++) {
				$("#icd10dg").datagrid("selectRow", i);
			}
		}
	});
}

/**
 * 打开复审意见
 * @param reportid
 * @returns
 */
function openRetrialviewDialog(reportid) {
	$('#common_dialog').dialog({
		title:'添加复审',
		width : 460,height : 450,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/report/goToRetrialview?reportid='+reportid,
		modal : true,
		buttons:[{
			text:'关闭',
			width:60,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

/**
 *  删除复审意见
 * @param reportid
 * @returns
 */
function delRetrialview(reportid) {
	var row=$('#retrialview_dg_'+reportid).datagrid("getSelected");
	if (row!=null) {
		if (row.id) {
			$.messager.confirm({
				title: $.i18n.prop('confirm'),
				msg: '确认删除选择的复审意见！',
				border:'thin',
				fn: function(r){
					if (r){
						 $.getJSON(window.localStorage.ctx+"/report/delRetrialview?id="+row.id, function(json){
							 	var js=validationData(json);
						    	if (js.code==0) {
						    		$('#retrialview_dg_'+reportid).datagrid("reload");
						    	} else {
						    		$.messager.show({
						                title:$.i18n.prop('error'),
						                msg: js.message,
						                timeout:3000,
						                showType:'slide'
						            });
						    	}
						 });
					}
				}
			});
		} else {
			$.messager.show({
				title : $.i18n.prop('alert'),
				msg : "无法删除！",
				timeout : 3000,
				border : 'thin',
				showType : 'slide'
			});
		}
	} else {
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : "请选择一个备注！",
			timeout : 3000,
			border : 'thin',
			showType : 'slide'
		});
	}
}

/**
 *  保存复审意见
 * @param reportid
 * @returns
 */
function saveRetrialview(reportid) {
	if (reportid) {
		var retrialviewcontent=$('#retrialview_content').textbox('getValue');
		if (retrialviewcontent.trim() == "" || !$('#retrialview_content').textbox('isValid')) {
			return;
		}
		$.post(window.localStorage.ctx+'/report/saveRetrialview',
		    {
		    	'reportid' : reportid,
		    	'retrialviewcontent' : retrialviewcontent,	
		     },
		     function (res) {
	        	var json = validationDataAll(res);
	        	if (json.code==0) {
	        		$('#retrialview_dg_'+reportid).datagrid("reload");
	        		$('#common_dialog').dialog('refresh', window.localStorage.ctx+'/report/goToRetrialview?reportid='+reportid);
	        	} else {
	        		$.messager.show({
						title : $.i18n.prop('error'),
						msg : $.i18n.prop('savefailed'),
						timeout : 3000,
						border : 'thin',
						showType : 'slide'
					});
	        	}
	        }
		);
	} else {
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : "请先保存报告",
			timeout : 3000,
			border : 'thin',
			showType : 'slide'
		});
	}
}

//打开驳回意见
function openRejectOpinionDialog(reportid) {
	$('#common_dialog').dialog({
		title:'驳回意见',
		width : 460,height : 450,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/report/goToRejectOpinion?reportid='+reportid,
		modal : true,
		buttons:[{
			text:'关闭',
			width:60,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

/**
 * 取消报告
 */
 function cancelRejectOpinion() {
	$('#common_dialog').dialog('close');
}

//保存驳回意见
function saveRejectOpinion(reportid) {
	if (reportid) {
		var rejectopinioncontent=$('#rejectopinioncontent').textbox('getValue');
		// 驳回报告
		rejectReport(reportid,rejectopinioncontent);
		if (rejectopinioncontent.trim() == "" || !$('#rejectopinioncontent').textbox('isValid')) {
			cancelRejectOpinion();
			return;
		}
		$.post(window.localStorage.ctx+'/report/saveRejectOpinion',
		    {
		    	'reportid' : reportid,
		    	'rejectopinioncontent' : rejectopinioncontent,	
		     },
		     function (res) {
	        	var json = validationDataAll(res);
	        	cancelRejectOpinion();
				if (json.code==0) {
	        		$('#rejectOpinion_dg_'+reportid).datagrid("reload");
	        		$('#common_dialog').dialog('refresh', window.localStorage.ctx+'/report/goToRejectOpinion?reportid='+reportid);
	        	} else {
	        		$.messager.show({
						title : $.i18n.prop('error'),
						msg : $.i18n.prop('savefailed'),
						timeout : 3000,
						border : 'thin',
						showType : 'slide'
					});
	        	}
	        }
		);
	} else {
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : "请先保存报告",
			timeout : 3000,
			border : 'thin',
			showType : 'slide'
		});
	}
}
//删除驳回意见
function delRejectOpinion(reportid) {
	var row=$('#rejectOpinion_dg_'+reportid).datagrid("getSelected");
	if (row!=null) {
		if (row.id) {
			$.messager.confirm({
				title: $.i18n.prop('confirm'),
				msg: '确认删除选择的驳回意见！',
				border:'thin',
				fn: function(r){
					if (r){
						 $.getJSON(window.localStorage.ctx+"/report/delRejectOpinion?id="+row.id, function(json){
							 	var js=validationData(json);
						    	if (js.code==0) {
						    		$('#rejectOpinion_dg_'+reportid).datagrid("reload");
						    	} else {
						    		$.messager.show({
						                title:$.i18n.prop('error'),
						                msg: js.message,
						                timeout:3000,
						                showType:'slide'
						            });
						    	}
						 });
					}
				}
			});
		} else {
			$.messager.show({
				title : $.i18n.prop('alert'),
				msg : "无法删除！",
				timeout : 3000,
				border : 'thin',
				showType : 'slide'
			});
		}
	} else {
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : "请选择一个驳回意见！",
			timeout : 3000,
			border : 'thin',
			showType : 'slide'
		});
	}
}

/**
 * 搜索报告模板
 * @param value  搜索的内容
 * @param name   
 * @returns
 */
function searchTemplate(value,name) {
	console.log(value);
	console.log(name);
}

/**
 *  移动节点
 * @param target
 * @param source
 * @param point
 * @returns
 */
function moveNode(target,source,point) {
	console.log(target);
	console.log(source);
	console.log(point);
}

/**
 * 搜索报告模板
 * @param value  搜索的内容
 * @param name   
 * @returns
 */
function searchTemplate(value,name) {
	console.log(value);
	console.log(name);
}

/**
 *  移动节点
 * @param target
 * @param source
 * @param point
 * @returns
 */
function moveNode(target,source,point) {
	console.log(target);
	console.log(source);
	console.log(point);
}

function saveReportLabel(reportid){
	var publ_arr=$('#publiclabelct_' + reportid).combotree('getValues'),
	pril_arr=$('#privatelabelct_' + reportid).combotree('getValues'),
	icd_arr=$('#icd10labelct_' + reportid).combobox('getValues');
	$.post(window.localStorage.ctx+'/report/saveReportLabel',
	    {
	    	reportid : reportid,
	    	publiclabel : publ_arr?publ_arr.toString():'',
	    	privatelabel : pril_arr?pril_arr.toString():'',
			icd10label : icd_arr?icd_arr.toString():''
	     },
	     function (res) {
        	var json = validationDataAll(res);
        	if (json.code==0) {
        		_message($.i18n.prop('savesuccess'));
        	} else {
        		_message($.i18n.prop('savefailed'));
        	}
        }
	);
}

/*
 * type: 'one'单幅图像打印预览，'all'全部打印预览，'selected'选中打印预览
 * 
 */
function previewImages(reportid,orderid,studyid,type){
	if(reportid){
		var container=ReportImageContainer.getCatch(reportid,'img_ul');
		console.log(container)
		if(container){
			let selects=container.getSelected();
			var imgid=null;
			var imgids="";
			var printTempType=7;//默认全部打印类型
			var printtempname="";
			if(type=='one'){
				if(selects&&selects.length==1){
					imgid=selects[0].imgid;
					printTempType=8;//单页打印类型
				} else{
					_message('请选择一幅图像！');
					return;
				}
				printtempname=$('#one_temp_name_cb').combobox('getValues');
				
			} else {
				if(type=='selected'){
					if(selects&&selects.length>0){
						for(let i=0,len=selects.length;i<len;i++){
							imgids+=selects[i].imgid+",";
						}
						imgids=imgids.substr(0,imgids.length-1);
					} else{
						_message('请选择图像！');
						return;
					}
				} else{
					container.selecteAll();
					selects=container.getSelected();
					$('#selected_images_span').html(selects.length);
					console.log(selects)
					if(selects&&selects.length>0){
						for(let i=0,len=selects.length;i<len;i++){
							imgids+=selects[i].imgid+",";
						}
						imgids=imgids.substr(0,imgids.length-1);
					} else{
						_message('没有图像可以打印！');
						return;
					}
				}
				printtempname=$('#all_temp_name_cb').combobox('getValues');
			}
			window.open(window.localStorage.ctx+"/print/toPreviewImages?printTempType="+printTempType+"&orderid="+orderid+"&reportid="+reportid
					+"&studyid="+studyid+"&imgid="+(imgid||'')
					+"&imgids="+imgids
					+"&printtempname="+printtempname);
		}
	}
	else{
		_message('尚未创建报告，无法打印！', $.i18n.prop('error'));
	}
}

/**
 * 打印报告
 * @param projecturl
 * @param reportid
 * @returns
 */
function printImages(reportid,orderid,studyid,type){
	
	if(reportid){
		var container=ReportImageContainer.getCatch(reportid,'img_ul');
		console.log(container)
		if(container){
			let selects=container.getSelected();
			var imgid=null;
			var imgids="";
			var printTempType=7;//默认全部打印类型
			var printtempname="";
			if(type=='one'){
					if(selects&&selects.length==1){
						imgid=selects[0].imgid;
						printTempType=8;//单页打印类型
					} else{
						_message('请选择一幅图像！');
						return;
					}
					printtempname=$('#one_temp_name_cb').combobox('getValues');
				
			} else{
				if(type=='selected'){
					if(selects&&selects.length>0){
						for(let i=0,len=selects.length;i<len;i++){
							imgids+=selects[i].imgid+",";
						}
						imgids=imgids.substr(0,imgids.length-1);
					} else{
						_message('请选择图像！');
						return;
					}
				} else{
					container.selecteAll();
					selects=container.getSelected();
					$('#selected_images_span').html(selects.length);
					console.log(selects)
					if(selects&&selects.length>0){
						for(let i=0,len=selects.length;i<len;i++){
							imgids+=selects[i].imgid+",";
						}
						imgids=imgids.substr(0,imgids.length-1);
					} else{
						_message('没有图像可以打印！');
						return;
					}
				}
				printtempname=$('#all_temp_name_cb').combobox('getValues');
			}
//		getJSON( window.localStorage.ctx+'/report/getPrintReportInfo',
//			{
//				reportid : row.reportid
//			},
//			function(json){
				closeReportFlag=false;
				urlFlag = true;
				var href="reporttool:-c print -l "+$('#print_img_projecturl').val()+" -n "+$('#reportprintUserid_wl').val()+" -m orderid="+orderid+"&studyid="+studyid
					+"&imgid="+(imgid||"")+"&printTempType="+printTempType+"&printtempname="+printtempname+"&imgids="+imgids;
		    	$('#printReport_worklist')[0].setAttribute('href',href);
		    	$('#printReport_worklist')[0].click();
//			});
		}
	} else{
		_message('尚未创建报告，无法打印！', $.i18n.prop('error'));
	}
}

let sortImage = function(a,b){
	return $(a).attr('item') - $(b).attr('item');
}

function savePrintImagesHtml(reportid){	
	var status=$('#print_img_reportstatus').val();
	if(status==myCache.ReportStatus.FinalResults){
		_message("报告已经审核，不能保存！");
		return ;
	}
	
	let html="";
	let items= $('#img_ul .item');
	let orders=new Object();
	if(items&&items.length>0){
		html="<div class='imglist' style='width:660px;'>";
//		let item_w=$(items[0]).width(),item_h=$(items[0]).height();
//		let n=6;//每页6张图
//		let page=Math.ceil(items.length/n);//页数
//		console.log(page)
		$('#img_ul').find('.item').sort(sortImage).each(function(i,obj){
			orders[$(obj).attr('imgid')]=i;
//			let img=$(obj).find('a img:first');
//			if(img!=null&&img[0]){
//				if(i>0&&i%n==0){//页末添加分页符
//					html+="<div class='pageNext'></div>";
//				}
//				let style="";
//				/*if(Math.ceil((i+1)/n)==page){//最后一页，在item上添加display:inline;样式
//					style="style='display:inline;'";
//				}*/
//				html+="<div class='item' "+style+">";
//				html+="<img src='"+img.attr('src')+"' style='width: "+img.width()+"px; height: "+
//					img.height()+"px;margin:"+img.css('margin-top')+" "+img.css('margin-right')+" "+
//					img.css('margin-bottom')+" "+img.css('margin-left')+";'/>";
//				html+="</div>";
//			}
		});
		html+="</div>";
	}

	console.log(html);
	//console.log(orders)
	if(html){
		getJSON(window.localStorage.ctx+'/report/savePrintImagesHtml',
			{
				reportid:reportid,
				html:html,
				orders:JSON.stringify(orders)
			},
			function(data){
				if(data.code==0){
					_message("保存成功！");
				} else{
					_message("保存失败，请重试！");
				}
			});
	} else{
		_message("没有打印图片，无法保存！");
	}
}

function openPrintImagesDlg(reportid,orderid,studyid){
	$('#common_dialog').dialog({
		title:'打印图片',
		width : 1170,height : 830,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/report/goPrintImages?reportid='+reportid+'&orderid='+orderid+'&studyid='+studyid,
		modal : true,
		buttons:[{
			text:'保存排序',
			width:80,
			handler:function(){savePrintImagesHtml(reportid);}
		},{
//			text:'批量打印',
//			width:80,
//			handler:function(){previewImages(reportid,studyid);}
//		},{
			text:'关闭',
			width:80,
			handler:function(){ReportImageContainer.removeCatch(reportid,'img_ul');$('#common_dialog').dialog('close');}
		}]
	});
}

function printImagePanel_onLoad(reportid,studyid){
	console.log(reportid)
	var status=$('#print_img_reportstatus').val();
	var editable=true;//!(status==myCache.ReportStatus.FinalResults);
	var imgulp = new ReportImageContainer({
		listid:"img_ul",//页面图片列表ID
		thumbherf:"",//列表图片前缀
		bigherf:"",//原图前缀[同列表图相同时，省略]
		jsondata:true,
		showtitle:false,
		imgsrcarr:[],//图片数据数组
		editable:editable,
		item_w:320,
		item_h:320,
		callback_del_func:delReportImages,
		callback_select_func:selectOneImage
	});
	
	imgulp.putCatch(reportid,'img_ul');

	getJSON(window.localStorage.ctx+'/report/getReportImages',{
		reportid:reportid,
		studyid:studyid
	},function(data){
		$('#total_images_span').html(data.length);
		for(var i=0,len=data.length;i<len;i++){
			imgulp.addimgarr([{
					src:'image/image_GetViaImage?id='+data[i].id,
					id:data[i].id
				}
			]);
		}
	});
}

function refreshImages(reportid,studyid){
	var container=ReportImageContainer.getCatch(reportid,'img_ul');
	if(container){
		var arr= container.getnewarr();
		getJSON(window.localStorage.ctx+'/report/getReportImages',{
			reportid:reportid,
			studyid:studyid
		},function(data){
			//去掉重复的图像，至添加新图像至最后
			var newarr=[];
			for(var i=0,len=data.length;i<len;i++){
				let contain=false;
				for(var j=0,len1=arr.length;j<len1;j++){
					if(arr[j].imgid==data[i].id){
						contain=true;
						break;
					}
				}
				if(!contain){
					newarr.push(data[i])
				}
			}
			for(var i=0,len=newarr.length;i<len;i++){
				container.addimgarr([{
						src:'image/image_GetViaImage?id='+newarr[i].id,
						id:newarr[i].id
					}
				]);
			}
			$('#total_images_span').html(container.getImagesCount());
		});
	}
}

function delReportImages(arr,container){
	var ids=arr;
	if(arr instanceof Array){
		ids='';
		$.each(arr, function(i,o) {   
			ids += o.imgid+',';
		});
		if(ids.indexOf(',')>=0){
			ids=ids.substring(0,ids.length-1);
		}
	}
	console.log(ids)
	getJSON(window.localStorage.ctx+'/report/delReportImages',{
		ids:ids
	},function(data){
		if(data.code==0){
			$('#total_images_span').html(container.getImagesCount());
			$('#selected_images_span').html(container.getSelectedImagesCount());
			
			var c_id=container.box.attr("id");
			if(c_id.indexOf("img_ul_")>=0){
				var reportid=c_id.substring(7,c_id.lenght);
				console.log(reportid)
				if($('#image_container_' + reportid)[0]){
					var title=$('#image_container_' + reportid).panel('options').title;
					$('#image_container_' + reportid).panel('setTitle',title.substring(0,title.indexOf("--")+2)+"    现有图像："+container.getnewarr().length+"张");
				}
			}
			
		} else{
			_message("删除操作发生错误，请重试！");
		}
	});
}

function delSelectedImgs(reportid){
	var container=ReportImageContainer.getCatch(reportid,'img_ul');
	console.log(container)
	if(container){
		container.delSelectedImgs();
	}
}

function changeImageLayout(record,reportid){
	var container_w=$('#img_ul').width();//容器的宽度
	var col_num=record.value;//列数
	var item_h=item_w=Math.floor(container_w/col_num)-6;//图像宽度
	if(item_h>650){
		item_h=644;
	}
	var container=ReportImageContainer.getCatch(reportid,'img_ul');
	console.log(container)
	if(container){
		container.resizeAll(item_w,item_h);
	}
}

function selectAllImage(reportid,btn){
	var container=ReportImageContainer.getCatch(reportid,'img_ul');
	if(container){
		if(!$(btn).linkbutton('options').selected){
			container.selecteAll();
			$('#selected_images_span').html(container.getSelectedImagesCount());
		} else{
			container.unselecteAll();
			$('#selected_images_span').html(container.getSelectedImagesCount());
		}
		enableImageDesc(false);
	}
}

function selectOneImage(container){
	//console.log(container)
	$('#imgDescTxtbox').textbox('setValue','');
	var selects=container.getSelected();
	console.log(selects)
	$('#selected_images_span').html(container.getSelectedImagesCount());
	if(selects&&selects.length==1){
		var status=$('#print_img_reportstatus').val();
//		var editable=!(status==myCache.ReportStatus.FinalResults);
//		if(editable){
			enableImageDesc(true);
//		}
		getJSON(window.localStorage.ctx+'/report/getImgDesc',{
			id:selects[0].imgid
		},function(data){
			console.log(data)
			if(data.code==0){
				$('#imgDescTxtbox').textbox('setValue',data.data||'');
			} else{
				_message("获取图像描述失败！请重试。错误信息："+data.message);
			}
		});
		
	} else{
		enableImageDesc(false);
	}
}

function enableImageDesc(enable){
	if(enable){
		$('#saveImgDescBtn').linkbutton('enable');
		$('#imgDescTxtbox').textbox('enable');
	} else{
		$('#saveImgDescBtn').linkbutton('disable');
		$('#imgDescTxtbox').textbox('disable');
		$('#imgDescTxtbox').textbox('setValue','');
	}
}

function saveImgDesc(reportid){
	var container=ReportImageContainer.getCatch(reportid,'img_ul');
	if(container){
		var selects=container.getSelected();
		console.log(selects)
		if(selects&&selects.length==1){
			let desc=$('#imgDescTxtbox').textbox('getValue');
			if(desc){
				getJSON(window.localStorage.ctx+'/report/saveImgDesc',{
					id:selects[0].imgid,
					desc:desc
				},function(data){
					if(data.code==0){
						_message('保存成功！');
					} else{
						_message('保存失败，请重试！错误信息：'+data.message);
					}
				});
			} else{
				_message('请填写图像描述！');
			}
		}
	}
}

function printImagePanel_history_onLoad(reportid,studyid,patientid){
	console.log(reportid)
	var imgulp = new ReportImageContainer({
		listid:"img_ul_history",//页面图片列表ID
		thumbherf:"",//列表图片前缀
		bigherf:"",//原图前缀[同列表图相同时，省略]
		jsondata:true,
		showtitle:false,
		imgsrcarr:[],//图片数据数组
		editable:true,
		item_w:160,
		item_h:155,
		callback_select_func:selectOneImageHistory
	});
	imgulp.putCatch(reportid,'img_ul_history');
	getJSON(window.localStorage.ctx+'/report/getReportImagesHistory',{
		reportid:reportid,
		studyid:studyid,
		patientid:patientid
	},function(data){
		for(var i=0,len=data.length;i<len;i++){
			imgulp.addimgarr([{
					src:'image/image_GetViaImage?id='+data[i].id,
					id:data[i].id
				}
			]);
		}
		var container_w=$('#img_ul_history').width();//容器的宽度
		var col_num=1;//列数
		var item_h=item_w=Math.floor(container_w/col_num)-6;//图像宽度
		if(item_h>650){
			item_h=644;
		}
		var container=ReportImageContainer.getCatch(reportid,'img_ul_history');
		console.log(container)
		if(container){
			container.resizeAll(item_w,item_h);
		}
	});
}

function selectOneImageHistory(container){
	//console.log(container)
	var reportid=$("#reportid").val();
	var container=ReportImageContainer.getCatch(reportid,'img_ul');
	if(container){
		container.unselecteAll();
		$('#selected_images_span').html(0);
	}
}
function addImage(reportid,studyid){
	var containerhistory=ReportImageContainer.getCatch(reportid,'img_ul_history');
	if(containerhistory){
		let selects=containerhistory.getSelected();
		if(selects&&selects.length>=1){
			var ids=""
			for (var i = 0; i < selects.length; i++) {
				if(i==0){
					ids=selects[i].imgid;
				}else{
					ids=ids+","+selects[i].imgid;
				}
			}
			getJSON(window.localStorage.ctx+'/report/addImage',{
				ids:ids,
				studyid:studyid
			},function(data){
				if(data.code==0){
					_message('添加成功！');
					refreshImages(reportid,studyid);
				} else{
					_message('添加失败，请重试！错误信息：'+data.message);
				}
			});
		}else{
			_message('请选择要添加的图片！！');
		}
	}

}

function reportImagePanel_onLoad(reportid,studyid,report_type){
	console.log(reportid)
	var status=$('#orderStatus_' + reportid).val();
	var editable=!(status==myCache.ReportStatus.FinalResults);
	var imgulp = new ReportImageContainer({
		listid:"img_ul_"+reportid,//页面图片列表ID
		thumbherf:"",//列表图片前缀
		bigherf:"",//原图前缀[同列表图相同时，省略]
		jsondata:true,
		showtitle:false,
		imgsrcarr:[],//图片数据数组
		editable:editable,
		item_w:150,
		item_h:150,
		callback_select_func:selectOneImage_report,
		callback_del_func:delReportImages
	});
	imgulp.putCatch(reportid,'img_ul_'+reportid);
//	console.log(imgulp.getCatch(reportid))
//	ReportImageContainer.getCatch(reportid)

	getJSON(window.localStorage.ctx+'/report/getReportImages',{
		reportid:reportid,
		studyid:studyid//,
		//report_type:report_type
	},function(data){
		for(var i=0,len=data.length;i<len;i++){
			imgulp.addimgarr([{
					src:'image/image_GetViaImage?id='+data[i].id,
					id:data[i].id
				}
			]);
		}
		if(data.length>0){
			$('#image_container_' + reportid).panel('setTitle',$('#image_container_' + reportid).panel('options').title+"--现有图像："+data.length+"张");
			var hei=$('#image_container_' + reportid).height();
			if(imgulp.box.height()>(hei+10)){
				$('#image_container_' + reportid).panel('resize',{
					height: (hei+10)*2
				});
			}
		}
	});
}

function selectOneImage_report(container){
	//console.log(container)
}

function addImagesToReport(reportid,studyid){
	var container=ReportImageContainer.getCatch(reportid,'img_ul_'+reportid);
	if(container){
		var arr= container.getnewarr();
		getJSON(window.localStorage.ctx+'/report/getReportImages',{
			reportid:reportid,
			studyid:studyid
		},function(data){
			//去掉重复的图像，至添加新图像至最后
			var newarr=[];
			for(var i=0,len=data.length;i<len;i++){
				let contain=false;
				for(var j=0,len1=arr.length;j<len1;j++){
					if(arr[j].imgid==data[i].id){
						contain=true;
						break;
					}
				}
				if(!contain){
					newarr.push(data[i])
				}
			}
			for(var i=0,len=newarr.length;i<len;i++){
				container.addimgarr([{
						src:'image/image_GetViaImage?id='+newarr[i].id,
						id:newarr[i].id
					}
				]);
			}
			var title=$('#image_container_' + reportid).panel('options').title;
			$('#image_container_' + reportid).panel('setTitle',title.substring(0,title.indexOf("--")+2)+"    现有图像："+container.getnewarr().length+"张");
		});
	}
}


