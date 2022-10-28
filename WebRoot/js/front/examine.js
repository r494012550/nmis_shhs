/**
 * 
 */
var myCache;

//function validator_exam (value) { 
//    //格式yyyy-MM-dd或yyyy-M-d
//       return/^(?:(?!0000)[0-9]{4}([-]?)(?:(?:0?[1-9]|1[0-2])\1(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])\1(?:29|30)|(?:0?[13578]|1[02])\1(?:31))|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([-]?)0?2\2(?:29))$/i.test(value); 
//}

$(function() {	
	myCache=JSON.parse(window.localStorage.myCache);
	console.log(navigator.userAgent);
});

function getOS () {
	var ua = navigator.userAgent.toLowerCase();
	if (ua.indexOf('window') > 0) {
		return 'windows';
	} else if (ua.indexOf('mac os x') > 0) {
		return 'mac';
	} else if (ua.indexOf('ipad') > 0) {
		return 'ipad';
	} else if (ua.indexOf('linux') > 0) {
		return 'linux';
	} else if (ua.indexOf('android') > 0) {
		return 'android';
	} else {
		return 'windows';
	}
}
//判断是否平板
function isTablet(){
//	return true;
	var ua = navigator.userAgent.toLowerCase();
	if(ua.indexOf('ipad') >= 0||ua.indexOf('linux') >=0||ua.indexOf('android') > 0){
		return true;
	} else{
		return false;
	}
}

function validator_exam (value) { 
    //格式yyyy-MM-dd或yyyy-M-d
       return/^(?:(?!0000)[0-9]{4}([-]?)(?:(?:0?[1-9]|1[0-2])\1(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])\1(?:29|30)|(?:0?[13578]|1[02])\1(?:31))|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([-]?)0?2\2(?:29))$/i.test(value); 
}

/**
 *  根据所选的设备id，跳转检查工作站
 * @param modalityid
 * @returns
 */
function gotoExamination(modalityid) {
	var tab = $('#tabs_div_exam').tabs('getTab',0);
	tab.panel('refresh', window.localStorage.ctx+'/examine/gotoExamination?modalityid='+modalityid+"&istablet="+isTablet());
	$('#tabs_div_exam').tabs('select',0);
}

function selectEquipment(){
//	var tab = $('#tabs_div_exam').tabs('getSelected');  // get selected panel
	var tab = $('#tabs_div_exam').tabs('getTab',0);  // get selected panel
	tab.panel('refresh', window.localStorage.ctx+'/examine/selectEquipment?user_institution='+$('#user_institution').val());
	$('#tabs_div_exam').tabs('select',0);
}

/**
 * 初始化检查页面
 */
function initExamine() {
	var tab=$('#tabs_div_exam').tabs('getTab',0);
	if($('#modality_name_exam').val()){
		$('#tabs_div_exam').tabs('update', {
			tab: tab,
			options: {
				title: '检查工作站:'+$('#modality_name_exam').val()
			}
		});
		
	} else{
		$('#tabs_div_exam').tabs('update', {
			tab: tab,
			options: {
				title: '检查工作站'
			}
		});
	}
	
	if(!$("#orderstatus_exam")[0]){
		return;
	}
	
	$("#orderstatus_exam").change(function(){
		var status=$("#orderstatus_exam").val();
		if(status==myCache.StudyOrderStatus.canceled){
			$("#exmaPanel").panel('setTitle',"检查已被取消...");
			enableButton(true);
			$('#save_btn').linkbutton('disable');
			$('#remark_btn').linkbutton('disable');
			$('#process_btn').linkbutton('disable');
			$('#appform_btn').linkbutton('disable');
		}
		else if(status==myCache.StudyOrderStatus.in_process){
			$("#exmaPanel").panel('setTitle',"正在检查中...");
			enableButton(false);
			$('#save_btn').linkbutton('enable');
			$('#remark_btn').linkbutton('enable');
			$('#process_btn').linkbutton('enable');
			$('#appform_btn').linkbutton('enable');
			if($("#hasRemark").val()=="1"){
		    	openRemarkDialog();
		    }
		}
		else if(status==myCache.StudyOrderStatus.completed){
			$("#exmaPanel").panel('setTitle',"检查完成...");
			enableButton(true);
			$('#save_btn').linkbutton('enable');
			$('#remark_btn').linkbutton('enable');
			$('#process_btn').linkbutton('enable');
			$('#appform_btn').linkbutton('enable');
		}
		else{
			$("#exmaPanel").panel('setTitle',"等待新的检查...");
			enableButton(true);
			$('#save_btn').linkbutton('disable');
			$('#remark_btn').linkbutton('disable');
			$('#process_btn').linkbutton('disable');
			$('#appform_btn').linkbutton('disable');
		}
	});
	
	if($("#orderstatus_exam").val()){
		$("#orderstatus_exam").val($("#orderstatus_exam").val()).change();
	}
	else{
		$("#orderstatus_exam").val(myCache.StudyOrderStatus.registered).change();
	}
	
	//初始化查询条件-检查设备
	if($('#appmodalityid_exam')[0] && $('#modalityId_exam')[0]){
		$('#appmodalityid_exam').combobox('select', $('#modalityId_exam').val());
		searchForm_flag = "searchForm";
		searchExamine();
	}
	
	$("#waitinglist_form_exam").form({
		url: window.localStorage.ctx+"/examine/findStudyorder?page="+ default_page +"&rows="+ default_pageSize,
		onSubmit : function(param) {
			param.searchFormType = 'waitinglist';
			//param.datetype = "arrivedtime";
		},
		success:function(data){
			var json=validationData(data);
			if(json&&!json.code){
	    		$("#waitinglistdg_exam").datagrid("loadData", json);
	    		//表格每次刷新 默认选择第一行数据
	    		var rows = $('#waitinglistdg_exam').datagrid('getRows');
	    		if(rows.length>0){
	    			var row = rows[0];
	    			dgDbclick_exam(0,row,1);
	    		}
	    	}
	    }
	}); 
	
	refreshWaitinglist();
	
	//定时刷新
	$('body').stopTime("examine_waitinglist_timer");
	$('body').everyTime("30s","examine_waitinglist_timer",function(){
		refreshWaitinglist();
	});
}

/**
 * 
 * @param node
 * @returns
 */
function enableButton(can_next){
	if (can_next){
		//$('#next_btn').linkbutton('enable');
		$('#repeat_btn').linkbutton('disable');
		$('#complete_btn').linkbutton('disable');
		$('#cancel_btn').linkbutton('disable');
		$('#skip_btn').linkbutton('disable');
		$('#time_btn').linkbutton('disable');
		$('#diccall_btn').linkbutton('disable');
	}
	else{
		//$('#next_btn').linkbutton('disable');
		$('#repeat_btn').linkbutton('enable');
		$('#complete_btn').linkbutton('enable');
		$('#cancel_btn').linkbutton('enable');
		$('#skip_btn').linkbutton('enable');
		$('#time_btn').linkbutton('enable');
		$('#diccall_btn').linkbutton('enable');
	}	
}
var default_page=1;
var default_pageSize=20;
var searchForm_flag = "searchForm";
/**
 * 初始化查询条件
 */
function initSearch(){
	  var p = $('#searchdg_exam').datagrid('getPager'); 
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
	    		
	    		if(searchForm_flag=="searchForm"){
	    			searchExamine(pageNumber, pageSize, null,null);
	    		}
	    		else if(searchForm_flag=="quicksearch"){
	    			searchExamine(pageNumber,pageSize,$('#quicksearch-input').searchbox('getValue'),$('#quicksearch-input').searchbox('getName'));
	    		}
	    		
	    		$(this).pagination('loaded');
	    	}
	  });
	
	$("#searchForm_exam").form({
		url: window.localStorage.ctx+"/examine/findStudyorder",
		onSubmit: function(){
			if (checkSearch()) {
				$('#progress_dlg').dialog('open');
				return true;
			} else {
				return false;
			}
			
	    },
	    success:function(data){
	    	$('#progress_dlg').dialog('close');
	    	if(!data.code){
	    		var json=validationData(data);
			    $("#searchdg_exam").datagrid("loadData", json);
	    	}
	    }
	});
	
	//输入框绑定回车事件
	$('#searchForm_exam').find('.easyui-textbox').each(function(index,element){
		$(element).textbox('textbox').bind('keydown', function(e){
			if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
				searchExamine();
			}
		});
	})
	
	//初始化查询条件-检查设备
	if($('#appmodalityid_exam')[0] && $('#modalityId_exam')[0]){
		$('#appmodalityid_exam').combobox('select', $('#modalityId_exam').val());
	}
	
	searchExamine();
}

/**
 * 刷新待检查列表
 */
function refreshWaitinglist(){
	$("#waitinglist_form_exam").form('submit',
		{url: window.localStorage.ctx+"/examine/findStudyorder?page="+ default_page +"&rows="+ default_pageSize});
}


/**
 * 设备停用按钮
 */
function setModalityState(checked){
	var note=$("#note").textbox("getValue");
	if(checked){
		$("#workingState").val("0");
	}else{
		$("#workingState").val("1");
	}
	getJSON(window.localStorage.ctx+"/examine/setModalityState",
			 {
				 checked:checked,
				 modalityid:$('#modalityId_exam').val(),
				 note:note
			 },
			 function(json){
				 var data=validationData(json);
				 if(json.code!=0){
					 	_message('保存失败请重试，如果问题依然存在，请联系系统管理员！','错误提醒');
				 }else{
					  $("#note").textbox("setValue","");
				 }
			 }
		);
}

/**
 * 下一个检查
 */
function nextStudy(){
	var rows = $('#waitinglistdg_exam').datagrid('getRows');
	if (rows&&rows.length>0) {
        var rowData = rows[0];
        if (rowData.status==myCache.StudyOrderStatus.injected || rowData.status==myCache.StudyOrderStatus.re_examine) {
        	//判断当前状态
    		getJSON(window.localStorage.ctx+"/examine/getStatus",
    				{
    					modalityId:$("#modalityId_exam").val(),
    					orderid : rowData.studyorderpkid
    				},
    				function(json){
		    			var data = validationDataAll(json);
		    			if(data.code==0){
		    				// 如果是登记状态
		    				if (data.data.status==myCache.StudyOrderStatus.injected || data.data.status==myCache.StudyOrderStatus.re_examine) {
		    		            // 改为开始检查状态
		    					rowData.status=myCache.StudyOrderStatus.in_process;
		    		    		startApp(rowData,data.data.status);
		    				} else {
		    					_message('请刷新检查列表后再开始检查！','提醒');
		    					refreshWaitinglist();
		    				}
		    			} else {
		    				_message('请求数据失败！','提醒');
		    				
		    			}	
		    		}
    		);	

        }else {
        	_message('当前状态无法开始！','提醒');
        }
        
	}
	else {
		_message('无新的检查！','提醒');
	}
		
}

/**
 * 开始检查
 */
function startApp(row,status){
	$('#completetime').val("");
	getJSON(window.localStorage.ctx+"/examine/startStudyOrder",
			{
				orderid : row.studyorderpkid,
				patientidfk : row.patientidfk,
				admissionidfk : row.admissionidfk,
				studyid : row.studyid,
				studystatus: status
			},
			function(json){
				if(json.code==0){
					var hasRemark=json.data;
		    		loadInfo(row,hasRemark);
		    		refreshWaitinglist();
				}else{
					_message('开始失败请重试，如果问题依然存在，请联系系统管理员！','提醒')
				}
			});
}

/**
 * 完成检查
 */
function completeApp(modalityname){
	var completetime=$("#completetime").val();
	var studyorderpkid=$("#studyorderid_exam").val();
	var status=$("#orderstatus_exam").val();
	if(status==myCache.StudyOrderStatus.in_process){
		//判断当前状态
		getJSON(window.localStorage.ctx+"/examine/getStatus",
				{
					modalityId:$("#modalityId_exam").val(),
					orderid : studyorderpkid
				},
				function(json){
	    			var data = validationDataAll(json);
	    			if(data.code==0){
	    				if(data.data.status==myCache.StudyOrderStatus.in_process){
	    					CompletecallingSetting(modalityname);
	    		            //canCompleteApp(studyorderpkid,completetime);
	    				}else{
	    					$("#orderstatus_exam").val(data.data.status).change();
	    					_message('当前病人已完成检查或状态出错！','提醒');
	    				}
	    			}else if(data.code==1){
	    				_message(data.message,'提醒');
	    			}else{
	    				_message('请求数据失败！','提醒');
	    			}	
	    		}
			);	
	}else{
		_message('当前状态无法完成！','提醒');
	}

}

function CompletecallingSetting(modalityname) {
	var patientname = $('#patientname_exam').textbox('getValue');
	var studyid = $('#studyorderstudyid_exam').textbox('getValue');
	$('#common_dialog').dialog(
	{
		title : '检查完成病人提示信息',
		width : 420,
		height : 400,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : false,
		border: 'thin',
		href : window.localStorage.ctx+'/calling/getcalling?callingpatientname='+patientname+'&modalityname='+modalityname+'&type='+'completecalling',
		onLoad:function(){
			if($('#diccalling_dialogonload').val()){
				getJSON(window.localStorage.ctx+"/examine/getPatientCallingHistory",
						{
							studyid: studyid
						},
						function(data){
							if(data.code == 0){
								$('#dicallinginfo_dg').datagrid({
									rowStyler:function(index,row){
										if(index <= data.data) {
											return 'background-color:#A8A8A8;';
										}
									}
								});
							}
						}
					);
			}
			
		},
		buttons:[{
			text:'叫号',
			width:80,
			handler:function(){diccallinginfo_send();}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

//完成检查更新数据库
function canCompleteApp(studyorderpkid,completetime){
	var collectionForm_exam=$("#collectionForm_exam").val();
	$("#collectionForm_exam").form("submit",{
		url: window.localStorage.ctx+"/examine/completeStudyOrder",
		onSubmit: function(param){
			param.orderid=studyorderpkid,
			param.completetime=completetime
			
		},
	    success:function(data){
	    	var json=validationData(data);
			if(json.code==0){
				$("#orderstatus_exam").val(myCache.StudyOrderStatus.completed).change();
				refreshWaitinglist();
				$('#patient_info_exam').form('clear');
				$("#studyitemlist_exam").datagrid('loadData',[]);
				$('#materialsForm_exam').form('clear'); 
				
				$('#div_history_exam').find('span').html('');
				$('#div_previous_history_exam').find('span').html('');
				$("#on_equipment_time").datetimebox("setValue","");
				$("#delayed_acquisition_time").datetimebox("setValue","");
				$("#delayed_acquisition_time1").datetimebox("setValue","");
				
				var rows = $('#waitinglistdg_exam').datagrid('getRows');
	    		if(rows.length>0){
	    			var row = rows[0];
	    			dgDbclick_exam(0,row,1);
	    		}
			}else{
				_message('完成失败请重试，如果问题依然存在，请联系系统管理员！','提醒');
			}
	    }
		
	});
}

/**
 * 过号
 */
function skipApp(){
	var studyorderpkid=$("#studyorderid_exam").val();
	var status=$("#orderstatus_exam").val();
	if(status==myCache.StudyOrderStatus.in_process){
		//判断当前状态
		getJSON(window.localStorage.ctx+"/examine/getStatus",
				{
					modalityId:$("#modalityId_exam").val(),
					orderid : studyorderpkid
				},
				function(json){
	    			var data = validationDataAll(json);
	    			if(data.code==0){
	    				if(data.data.status==myCache.StudyOrderStatus.in_process){
	    					canSkipApp(studyorderpkid);
	    				}else{
	    					$("#orderstatus_exam").val(data.data.status).change();
	    					_message('当前病人已完成检查或状态出错！','提醒');
	    				}
	    			}else{
	    				_message('请求数据失败！','提醒');
	    			}	
	    		}
			);
	}else{
		_message('当前操作无法完成！','提醒');
	}
}

/**
 * 过号更新数据库
 * @param studyorderpkid
 * @returns
 */
function canSkipApp(studyorderpkid){
	getJSON(window.localStorage.ctx+"/examine/skipStudyOrder",
			{
				orderid : studyorderpkid
			},
			function(data){
				var json=validationData(data);
				if(json.code==0){
					$("#orderstatus_exam").val(myCache.StudyOrderStatus.registered).change();
					refreshWaitinglist();
					$('#patient_info_exam').form('clear');
					$("#studyitemlist_exam").datagrid('loadData',[]);
					$('#materialsForm_exam').form('clear');
				} else {
					_message('跳号失败请重试，如果问题依然存在，请联系系统管理员！','提醒');
				}
			}
		);
}

/**
 * 重复叫号
 */
function repeatCall(){
	var studyorderpkid=$("#studyorderid_exam").val();
	var status=$("#orderstatus_exam").val();
	if(status==myCache.StudyOrderStatus.in_process){
		getJSON(window.localStorage.ctx+"/examine/repeatCall",
				{
					orderid : studyorderpkid
				},
				function(data){
					var json=validationData(data);
					if(json.code==0){
					}else {
						_message('叫号失败请重试，如果问题依然存在，请联系系统管理员！','提醒');
					}
				}
			);
	}else {
		_message('当前操作无法完成！','提醒');
	}
}

/**
 * 保存检查耗材
 */
//function saveApp() {
//	if ($("#studyorderfk_exam").val()) {
//		$("#saveAppForm_exam").form("submit",{
//			url: window.localStorage.ctx+"/examine/saveApp",
//			onSubmit: function(){
//				// 检查方法
//		    	var studymethod = $("#studymethod_exam_combobox").combobox("getText");
//		    	$("#studymethod_exam").val(studymethod);
//				if(!$("#exposurenum_exam").textbox('getValue')&&
//				!$("#filmnum_exam").textbox('getValue')&&
//				!$("#contrastagent_exam").textbox('getValue')&&
//				!$("#contrastagentdose_exam").textbox('getValue')&&
//				!$("#technician_exam").combobox("getValue")&&
//				!$("#nurse_exam").combobox("getValue")&&
//				!$("#physician_exam").combobox("getValue")&&
//				!$("#cost_exam").textbox("getValue")&&
//				!$("#studymethod_exam").val()&&
//				!$("#reexaminereason_exam").textbox("getValue")){
//					return false;
//				}
//		    },
//		    success:function(data){
//		    	var result=validationDataAll(data);
//		    	if(result.code==0){
//		    		$("#id_exam").val(result.data.id);
//		    		_message('保存成功！','保存成功');
//				}else{
//					_message(result.message,'保存失败');
//				}
//		    }
//			
//		});
//
//	}else{
//		_message('请选择一条数据！','保存失败');
//	}
//}

function dgDbclick_exam(rowIndex, rowData,eventType){
	//如果是自动刷新触发该方法，并且 右边表单已有数据则不进行覆盖。
	if(eventType==1&&$("#patientpkid_exam").val()!=null&&$("#patientpkid_exam").val()!=""){
	     return;
	}
	//平板模式下，展开检查面板
	var eastpanel=$("#exam_layout")[0]?$("#exam_layout").layout('panel','east'):null;
	if(eastpanel){
		if(eastpanel.panel('options').collapsed){
			$("#exam_layout").layout('expand','east');
		}
	}
	//判断当前状态
	getJSON(window.localStorage.ctx+"/examine/getStatus",
			{
				modalityId:$("#modalityId_exam").val(),
				orderid : rowData.studyorderpkid
			},
			function(json){
				var data = validationDataAll(json);
				if(data.code==0){
					if(data.data.status != rowData.status){
						//_message('更新当前数据','提醒');
						refreshWaitinglist();
					}else if(data.data.status==myCache.StudyOrderStatus.injected || rowData.status==myCache.StudyOrderStatus.re_examine){
						rowData.status = myCache.StudyOrderStatus.in_process;
			    		startApp(rowData, data.data.status);
					}else{
						loadInfo(rowData);
					}
				}else if(data.code==1){
					if(eventType!=1){
						_message(data.message,'提醒');
					}
				}else{
					_message('请求数据失败','提醒');
				}	
			}
		);
	
}

function startExam_tablet(index){
	$("#waitinglistdg_exam").datagrid('selectRow',index);
	var row=$("#waitinglistdg_exam").datagrid('getSelected');
	if(row!=null){
		dgDbclick_exam(index,row);
	}
}

function operatecolumn_startExam(value, row, index){
	return '<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" style="width: 50px;height: 28px;" onclick="startExam_tablet('+index
		+');"><span class="l-btn-left"><span class="l-btn-text">开始</span></span></a>';
}

function collapsedExamPanel(){
	$("#exam_layout").layout('collapse','east');
}

function maxOrRestoreExam(btn){
	console.log(btn)
	console.log($(btn).linkbutton('options').text)
	if('最大化'==$(btn).linkbutton('options').text){
		$("#exam_layout").layout('panel','east').panel('maximize');
		$(btn).linkbutton({text:'还原'})
	} else{
		$("#exam_layout").layout('panel','east').panel('restore');
		$(btn).linkbutton({text:'最大化'})
	}
}

/**
 * 加载检查信息
 */
function loadInfo(rowData,hasRemark) {
	$('#remarkDlg_exam').dialog('close');
	$("#patientpkid_exam").val(rowData.patientpkid);
	$("#admissionpkid_exam").val(rowData.admissionpkid)
    $("#studyorderid_exam").val(rowData.studyorderpkid);
    $("#hasRemark").val(hasRemark);
    $("#orderstatus_exam").val(myCache.StudyOrderStatus.in_process).change();
    loadPatient(rowData);
    getStudyItem(rowData.studyorderpkid);
    
    //setButton(false);
    if($("#hasRemark").val()=="1"){
    	openRemarkDialog();
    }
  //pri==5 表示已过过号，则过号按钮禁用
	if(rowData.pri==5){
		$("#skip_btn").linkbutton("disable");
	}else{
		$("#skip_btn").linkbutton("enable");
	}
	
	if(rowData.sex=="F"){
		$('#female_info').panel('open');
	}else{
		$('#female_info').panel('close');
	}
	
	$.each(rowData, function(key, val) {
		$('#div_history_exam').find('#'+key).each(function(index,e){
			if((val+"").endsWith('00:00:00')){
				$(e).html(val.substring(0,10));
			} else{
				$(e).html(val);
			}
		});
	});
	
	getJSON(window.localStorage.ctx+"/inquiring/getPreviousHistory",{
		orderid:rowData.studyorderpkid
	},function(data){
		console.log(data)
		if(data!=null){
			$.each(data, function(key, val) {
				$('#div_previous_history_exam').find('#'+key).each(function(index,e){
					if((val+"").endsWith('00:00:00')){
						$(e).html(val.substring(0,10));
					} else{
						$(e).html(val);
					}
				});
			});
		}
	});
	
}

/**
 * 加载病人和入院信息
 */
function loadPatient(rowData){
	$('#patient_info_exam').form('load', rowData);
	$("#age_exam").textbox('setValue',(rowData.age||"")+(rowData.ageunitdisplay||""));//年龄数值+年龄单位  20岁
    loadExamine(rowData);
}

/**
 * 加载选中检查数据
 */
function loadExamine(row) {
	$("#patientfk_exam").val(row.patientpkid);
    $("#studyorderfk_exam").val(row.studyorderpkid);
    $("#studyid_exam").val(row.studyid);
    $("#modality_exam").val(row.modality_type);
	if (row.studypk) {
		// 已经保存过检查耗材
		$("#id_exam").val(row.studypk);
		$('#materialsForm_exam').form('load', row);
	} else {
		// 未保存过检查耗材，查看今天此用户是否已有设置默认值
		getJSON(window.localStorage.ctx+"/examine/getDefaultSetting",{
			modalityid: $("#modalityId_exam").val()
		},function(data) {
			$("#id_exam").val("");
			if (data.code==0) {
				$('#materialsForm_exam').form('load', data.data);
			} else {
				$('#materialsForm_exam').form('clear');
			}
			$("#reexaminereason_exam").textbox("setValue","");
		});
	}
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
	
	var apppatientid_exam = $("#apppatientid_exam").textbox("getValue");  // 病人编号
	var appstudyid_exam = $('#appstudyid_exam').textbox('getValue'); // 检查编号
	var appoutno_exam = $("#appoutno_exam").textbox("getValue"); // 门诊号
	var appinno_exam = $('#appinno_exam').textbox('getValue'); // 住院号
	var apppatientname_exam = $('#apppatientname_exam').textbox('getValue'); // 姓名
	var appcardno_exam = $('#appcardno_exam').textbox('getValue'); // 卡号
	
	if (apppatientid_exam == "" && appstudyid_exam == "" && appoutno_exam == "" && appinno_exam == "" && 
			apppatientname_exam == "" && appcardno_exam == "" && datefrom == "" && dateto == "" && appdate == "") {
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

//查询检查信息
function searchExamine(pageNumber, pageSize, quicksearchcontent,quicksearchname) {
	if (!pageNumber) {
		pageNumber = default_page;
		$('#searchdg_exam').datagrid('getPager').pagination({ 
			pageSize: default_pageSize,//每页显示的记录条数，默认为20 
			pageNumber:default_page
		});
	}
	if (!pageSize) {
		pageSize = default_pageSize;
	}
	$("#searchForm_exam").form('submit',
		{url:window.localStorage.ctx+"/examine/findStudyorder?page="+ pageNumber +"&rows="+ pageSize
		+ "&quicksearchcontent=" + (quicksearchcontent||"")+"&quicksearchname="+(quicksearchname||"")});
}

/**
 * 清空查询条件
 */
function clearSearch(){
	$('#searchForm_exam').form('clear');
	
	$('#datetype').combobox('setValue',$('#datetype').combobox('getData')[0].value);
	
	$('#apptoday').linkbutton({selected:false});
	$('#appyesterday').linkbutton({selected:false});
	$('#appthreeday').linkbutton({selected:false});
	$('#appfiveday').linkbutton({selected:false});
	$('#appweek').linkbutton({selected:false});
	$('#appmonth').linkbutton({selected:false});
}

/**
 * 获取检查项目
 */
function getStudyItem(orderid){
	getJSON(window.localStorage.ctx+"/examine/getStudyItem?orderid="+orderid,null, function(data){
		var json=validationData(data);
		 $("#studyitemlist_exam").datagrid("loadData", json);
    });
}


//申请单
function toApply(){
	var orderid=$("#studyorderid_exam").val();
	if(orderid!=""){
		apply(orderid,"examine");
	}
	else{
		_message('请选择一条数据','提醒');
	}
	
}

//检查流程
function toProcess(){
	var orderid= $("#studyorderid_exam").val();
	if(orderid!=""){
		process(orderid,"examine");
	}
	else{
		_message('请选择一条数据','提醒');
	}
}

/**
 * 打开过滤器
 */
function openFilterDlg(){
	var workingState=$("#workingState").val();
	var modalityId_exam=$("#modalityId_exam").val();
	$('#common_dialog').dialog({
		title:'设备管理',
		width : 550,
		height : 300,
		closed : false,
		cache : false,
		resizable: false,
		minimizable: false,
		maximizable: false,
		border: 'thin',
		href: window.localStorage.ctx+"/examine/openFilterDlg?workingState="+workingState+"&modalityId="+modalityId_exam,
		modal : true,
		buttons:[{
			text:"关闭",
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}],
		onLoad:function(){
			//$('#timefrom').timespinner('setValue', val);
		},
		onClose:function(){
			refreshWaitinglist();
		}
	});
}

/**
 * 打开完成时间窗口
 */
function openCompletetimeDlg(){
	$('#common_dialog').dialog({
		title:'时间选择',
		width : 350,
		height : 200,
		closed : false,
		cache : false,
		resizable: false,
		minimizable: false,
		maximizable: false,
		border: 'thin',
		href: window.localStorage.ctx+"/examine/openCompletetimeDlg?",
		modal : true,
		buttons:[{
			text: $.i18n.prop('save'),
			width:80,
			handler:function(){
				var completetime=$('#sptime').timespinner('getValue');
				if(new Date().getTime()<new Date(completetime).getTime()){
					_message('所选时间大于当前时间','提醒');
				}else{
					$('#completetime').val(completetime);
					$('#common_dialog').dialog('close');
				}
				
			}
		},{
			text:$.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}],
		onLoad:function(){
			var val = $('#completetime').val(); 
			$('#sptime').timespinner('setValue', val==""?(new Date()).format('yyyy-MM-dd hh:mm:ss'):val);
		}
	});
}

var modalityid_exam_apply_triage=null;

function openTriageDlg1(){
	var row = $('#waitinglistdg_exam').datagrid('getSelected');
	if(row){
		modalityid_exam_apply_triage=null;
		$('#common_dialog').dialog({
			title:'申请分诊',
			width : 620,height : 500,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,modal : true,
			border: 'thin',
			href: window.localStorage.ctx+"/examine/gotoTriageDlg?modalityId="+$("#modalityId_exam").val()+"&modalityType="+$("#modalityType_exam").val(),
			buttons:[{
				text: '申请',
				width:80,
				handler:function(){applyTriage(row);}
			},{
				text:$.i18n.prop('cancel'),
				width:80,
				handler:function(){modalityid_exam_apply_triage=null;$('#common_dialog').dialog('close');}
			}]
//			,onLoad:function(){
//				
//			},
//			onClose:function(){
//				//searchExamine();
//				//refreshWaitinglist();
//			}
		});
	}
	else{
		_message('请选择一条数据','提醒');
	}
}

function openTriageDlg2(){
	var rows = $('#waitinglistdg_exam').datagrid('getRows');
	if(rows.length>0){
		modalityid_exam_apply_triage=null;
		$('#common_dialog').dialog({
			title:'申请分诊',
			width : 620,height : 500,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,modal : true,
			border: 'thin',
			href: window.localStorage.ctx+"/examine/gotoTriageDlg?modalityId="+$("#modalityId_exam").val()+"&modalityType="+$("#modalityType_exam").val(),
			buttons:[{
				text: '申请',
				width:80,
				handler:function(){applyTriageAll(rows);}
			},{
				text:$.i18n.prop('cancel'),
				width:80,
				handler:function(){modalityid_exam_apply_triage=null;$('#common_dialog').dialog('close');}
			}]
//			,onLoad:function(){
//				
//			},
//			onClose:function(){
//				//searchExamine();
//				//refreshWaitinglist();
//			}
		});
	}
	else{
		_message('请选择一条数据','提醒');
	}
}

function applyTriage(row){
	//console.log(modalityid_exam_apply_triage)
	if(modalityid_exam_apply_triage){
		getJSON(window.localStorage.ctx+"/examine/applyTriage",
			 {
				 orderid:row.studyorderpkid,
				 modalityid:modalityid_exam_apply_triage
			 },
			 function(json){
				 var data=validationData(json);
					if(data.code==0){
						modalityid_exam_apply_triage=null;
						$('#common_dialog').dialog('close');
						refreshWaitinglist();
						searchExamine();
						_message(data.message,'申请成功');
					}
					else{
						_message(data.message,'申请失败');
					}
			 }
		);
	}
	else{
		_message('请选择转诊设备！','提醒');
	}
}

function applyTriageAll(rows){
	var ss ;
	for(var i=0; i<rows.length; i++){
		var row = rows[i];
		if(i==0){
			ss=row.studyorderpkid;
		}else{
			ss=ss+","+row.studyorderpkid;
		}
	}
	if(modalityid_exam_apply_triage){
		getJSON(window.localStorage.ctx+"/examine/applyTriageAll",
			 {
				 orderid:ss,
				 modalityid:modalityid_exam_apply_triage
			 },
			 function(json){
				 var data=validationData(json);
					if(data.code==0){
						modalityid_exam_apply_triage=null;
						$('#common_dialog').dialog('close');
						refreshWaitinglist();
						searchExamine();
					}
					else{
						_message(data.message,'申请失败');
					}
			 }
		);
	}
	else{
		_message('请选择转诊设备！','提醒');
	}
}

//取消分诊申请
function cancelApplyTriage(element){
	var row = $('#'+element).datagrid('getSelected');
	if(row && row.triagemodalityid){
		getJSON(window.localStorage.ctx+"/examine/cancelApplyTriage",
			 {
				 orderid:row.studyorderpkid,
				 triagemodalityid:row.triagemodalityid
			 },
			 function(json){
				var data=validationData(json);
				if(data.code==0){
					_message('取消分诊成功！' , '成功');
					refreshWaitinglist();
					searchExamine();
				}
				else{
					_message('取消分诊失败，请重试。如果问题依然存在，请联系系统管理员。' , '失败');
				}
			 });
	}
	else{
		_message('请选择一条分诊申请数据！' , '提醒');
	}
}

function triageList(){
	$('#common_dialog').dialog({
		title:'分诊列表',
		width : 750,height : 500,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href: window.localStorage.ctx+"/examine/gotoTriageListDlg?modalityId="+$("#modalityId_exam").val(),
		buttons:[{
			text: '接受',
			width:80,
			handler:function(){acceptTriageApplay();}
		},{
			text:$.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

function acceptTriageApplay(){
	var rows = $('#studyorderdg_exam_apply').datagrid('getSelections');

	if(rows.length!=0){
		var orderis="";
		$.each(rows,function(i,row){
			orderis=orderis+row.studyorderpkid+',';
		});
		getJSON(window.localStorage.ctx+"/examine/acceptTriageApply",
			 {
				orderis:orderis,
				modalityid:$('#modalityId_exam').val()
			 },
			 function(json){
				 var data=validationData(json);
					if(data.code==0){
						_message(data.message,'操作成功！');
						$('#common_dialog').dialog('close');
						refreshWaitinglist();
					}
					else{
						_message(data.message,'提醒');
					}
			 }
		);
	}else{
		_message('请选择分诊申请','提醒');
	}
}

/**
 * 打开请求分诊窗口
 */
function openTriageDlg(){
	var row = $('#searchdg_exam').datagrid('getSelected');
	if(row){
		if(row.status!=myCache.StudyOrderStatus.injected&&row.status==myCache.StudyOrderStatus.re_examine){ //MCF说的只有已注射和重拍可以申请分诊
			$.messager.show({
	            title:'提示',
	            msg: "该记录已注射，无法申请分诊！！",
	            border:'thin',
	            timeout:5000,
	            showType:'slide'
	        });
			return;
		}

		modalityid_exam_apply_triage=null;
		$('#common_dialog').dialog({
			title:'申请分诊',
			width : 620,height : 500,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,modal : true,
			border: 'thin',
			href: window.localStorage.ctx+"/examine/gotoTriageDlg?modalityId="+row.modalityid+"&modalityType="+row.modality_type,
			buttons:[{
				text: '申请',
				width:80,
				handler:function(){applyTriage(row);}
			},{
				text:$.i18n.prop('cancel'),
				width:80,
				handler:function(){modalityid_exam_apply_triage=null;$('#common_dialog').dialog('close');}
			}],
//				,onLoad:function(){
//					
//				},
			onClose:function(){
				searchExamine();
				refreshWaitinglist();
			}
		});
		
	}else{
		_message('请选择一条数据','提醒');
		
	}
}

//获取设备病人
function gotoReceiveDlg(){
	modalityid_exam_apply_triage=null;
	$('#common_dialog').dialog({
		title:'获取设备病人',
		width : 620,height : 500,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,modal : true,
		border: 'thin',
		href: window.localStorage.ctx+"/examine/gotoReceiveDlg?modalityId="+$("#modalityId_exam").val()+"&modalityType="+$("#modalityType_exam").val(),
		buttons:[{
			text: '获取',
			width:80,
			handler:function(){beforeReceive();}
		},{
			text:$.i18n.prop('cancel'),
			width:80,
			handler:function(){modalityid_exam_apply_triage=null;$('#common_dialog').dialog('close');}
		}],
		onClose:function(){
			refreshWaitinglist();
		}
	});
}

//选中设备,获取设备病人
function handleClickRow(index,row){
	$("#yesterdayfrom2").val($('#yesterdayfrom1').val());
	$("#receivewaitinglist_form_exam").form('submit',{
		url: window.localStorage.ctx+"/examine/findStudyorder?page="+ default_page +"&rows="+ default_pageSize,
		onSubmit: function(param) {
			param.searchFormType = 'waitinglist';
			param.appdate = "T";
			param.datetype = "arrivedtime";
			param.appmodalityid = row.id;
			$('#progress_dlg').dialog('open');
		},
	    success:function(data){
	    	$('#progress_dlg').dialog('close');
	    	if(!data.code){
	    		var json=validationData(data);
	    		$("#receivewaitinglistdg_exam").datagrid("loadData", json);
	    	}
	    }
	});
}

function beforeReceive(){
	var modalityid=$("#appmodalityid").val();
	var rows=$("#receivewaitinglistdg_exam").datagrid("getSelections");
	if(rows.length>0){
		$.messager.confirm({
			title:$.i18n.prop('confirm'),
			ok:'是',
			cancel:'否',
			border:'thin',
			msg:'确认获取选中数据？',
			fn:function(r){
				if(r){
					receivePatient(modalityid,rows);		
				}
			}
		});
	}else{
		_message('请选择数据！' , '提醒');
	}
}
//接收病人
function receivePatient(modalityid,rows){
	if(modalityid){
		var orderis="";
		$.each(rows,function(i,row){
			orderis=orderis+row.studyorderpkid+',';
		});
		getJSON(window.localStorage.ctx+"/examine/acceptTriageApply",
			 {
				orderis:orderis,
				modalityid:modalityid
			 },
			 function(json){
				var data=validationData(json);
				if(data.code==0){
					_message('操作成功！' , '成功');
					$('#common_dialog').dialog('close');
					refreshWaitinglist();
				}
				else{
					_message('获取失败，请重试。如果问题依然存在，请联系系统管理员。' , '失败');
				}
			 });
	}
}

/**
 * 打开病人备注
 */
function openRemarkDialog(){
	var patientpkid=$("#patientpkid_exam").val();
	var admissionpkid=$("#admissionpkid_exam").val();
	var orderid=$("#studyorderid_exam").val();
	var studyid=$("#studyorderstudyid_exam").val();
	if(studyid){
		$('#remarkDlg_exam').dialog({
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
				handler:function(){$('#remarkDlg_exam').dialog('close');}
			}]
		});
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
	    	'type' : 'study'
	     },
	     function (res) {
        	var json = validationDataAll(res);
        	
        	if(json.code==0){
        		$('#remarkDlg_exam').dialog('refresh', window.localStorage.ctx+'/examine/goToEditremark?studyid='+studyid
        				+'&patientpkid='+patientidfk+'&admissionpkid='+admissionidfk+'&orderid='+orderid);
        	}
        	else{
        		_message($.i18n.prop('savefailed'),$.i18n.prop('error'));
        	}
        }
	);
}

function openModifyDlg(){
	var row=$("#searchdg_exam").datagrid("getSelected");
	if(row!=null){
		if(row.status==myCache.StudyOrderStatus.in_process||row.status==myCache.StudyOrderStatus.completed){
			$("#common_dialog").dialog({
				title : '修改',
				width : 600,
				height : 400,
				closed : false,
				cache : false,
				resizable: false,
				minimizable: false,
				maximizable: false,
				border: 'thin',
				href: window.localStorage.ctx+"/examine/openModifyDlg?orderid="+row.studyorderpkid,
				modal : true,
				buttons:[{
					text:'关闭',
					width:80,
					handler:function(){$('#common_dialog').dialog('close');}
				}],
//				,onLoad:function(){
//					
//				},
				onClose:function(){
					searchExamine();
				}
				
			});
		}else{
			_message('当前状态不是检查开始或检查完成，无法修改！','提醒');
		}
		
	}else{
		_message('请选择一条数据','提醒');
	}
}

function modifyStudyorder(orderid){
	var completetime=$('#sptime2').timespinner('getValue');
	if(!completetime){
		_message('检查完成时间不能为空','提醒');
		return;
	}
	
	if(new Date().getTime()<new Date(completetime).getTime()){
		_message('所选时间大于当前时间！','提醒');
		return;
	}else{
		getJSON(window.localStorage.ctx+"/examine/modifyStudyorder",
				{
					orderid : orderid,
					completetime : completetime
				},
				function(json){
					var data=validationData(json);
					 if(json.code==0){
						 searchExamine();
						 _message('当前检查信息已修改！','保存成功');
						}else{
							_message('保存失败请重试，如果问题依然存在，请联系系统管理员！','错误提醒');
						}
				});
	}
}

function saveApp_mod(){
	$("#saveAppForm_mod").form("submit",{
		url: window.localStorage.ctx+"/examine/saveApp",
		onSubmit: function(){
			if(!$("#exposurenum_mod").textbox('getValue')&&
			!$("#filmnum_mod").textbox('getValue')&&
			!$("#contrastagent_mod").textbox('getValue')&&
			!$("#contrastagentdose_mod").textbox('getValue')&&
			!$("#Technician_mod").combobox("getValue")&&
			!$("#nurse_mod").combobox("getValue")&&
			!$("#physician_mod").combobox("getValue")&&
			!$("#cost_mod").textbox("getValue")&&
			!$("#studymethod_mod").textbox('getValue')&&
			!$("#reexaminereason_mod").textbox("getValue")){
				return false;
			}
	    },
	    success:function(data){
	    	var result=validationDataAll(data);
	    	if(result.code==0){
	    		$("#id_mod").val(result.data.id);
	    		_message('保存成功！','保存成功');
			}else{
				_message(result.message,'保存失败');
			}
	    }
		
	});
}

/**
 * 状态颜色
 */
function columeStyler_orderstatus_exam(val,row,index) {
	var color = myCache.status_color['0005_'+row.status];
	if (color && color == "#ffffff") {
		return "<span style='color: "+color+"'>" + val + "</span>"
	} else if (color) {
		return 'background-color:'+color+';';//color:red;
	} else {
		return '';
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
 *  查询
 * @param e
 * @returns
 */
function quickSearch(value,name) {
	value = $.trim(value).replace(/[\-\_\,\!\|\~\`\(\)\#\$\%\^\&\{\}\:\;\"\L\<\>\?]/g, '');
	if (value === null || value === "" || value === undefined) {
		_message('请输入要查询的数据！','提醒');
	} else {
		searchForm_flag = "quicksearch";
		searchExamine(null, null, value,name);
	}
};


/**
 * 将现在所填写的 检查耗材 设置为默认
 * @returns
 */
//function saveDefaultSetting() {
//	$('#saveAppForm_exam').form('submit', {
//	    url: window.localStorage.ctx+"/examine/saveDefaultSetting?id="+$("#default_setting_id").val()+"&modalityid="+$("#modalityId_exam").val(),
//	    onSubmit: function(){
//	    	// 检查方法
//	    	var studymethod = $("#studymethod_exam_combobox").combobox("getText");
//	    	$("#studymethod_exam").val(studymethod);
//	    	if ($("#exposurenum_exam").val() == "" && $("#filmnum_exam").val() == "" && $("#contrastagent_exam").val() == "" && $("#contrastagentdose_exam").val() == "" && 
//	    			$("#technician_exam").val() == "" && $("#nurse_exam").val() == "" && $("#physician_exam").val() == "" ) {
//	    		_message("请填写需要设置的数据！")
//	    		return false;
//	    	}
//	    },
//	    success:function(data){
//	    	var json = validationDataAll(data);
//	    	console.log(json)
//	    	if (json.code==0) {
//				$("#default_setting_id").val(json.data.id);
//				_message("设置成功！")
//			} else {
//				_message("设置失败，请重试。如果问题依然存在，请联系系统管理员。","错误提醒")
//			}
//	    }
//	});
//	
//}

/**
 * 重新检查
 */
function cancelChecked(){
	var row = $("#searchdg_exam").datagrid("getSelected");
    if (row==null){
    	_message('请选择一条数据！','提醒');
    } else {
		if (row.status == myCache.StudyOrderStatus.completed && row.reportstatusdisplaycode == myCache.ReportStatus.Noresult){
			getJSON(window.localStorage.ctx+"/examine/cancelStudyChecked",
					{
						orderid : row.studyorderpkid
					},
					function(data){
						var json = validationData(data);
						if (json.code==0){
							_message('操作成功！将重新检查','提醒');
							searchExamine();
						} else {
							_message('取消失败请重试，如果问题依然存在，请联系系统管理员！','提醒');
						}
					});
			
		} else {
			var remind = '';
			if (row.status != myCache.StudyOrderStatus.completed){
				remind = remind + '检查未完成！';
			}
			if (row.reportstatusdisplaycode != myCache.ReportStatus.Noresult){
				remind = remind + '报告不是未写状态！';
			}
			_message('无法取消：<br/>' + remind,'提醒');
		}
    }
}

function openCancelReasonDlg() {
	var row = $("#searchdg_exam").datagrid("getSelected");
	if (row==null){
    	_message('请选择一条数据！','提醒');
    	return;
	}
	if (row.status == myCache.StudyOrderStatus.completed || row.status == myCache.StudyOrderStatus.canceled
			|| row.status == myCache.StudyOrderStatus.re_examine|| row.status == myCache.StudyOrderStatus.cancel_the_appointment){
		_message('当前检查状态下无法取消！','提醒');
    	return;
	}
	$('#common_dialog').dialog(
		{
			title : '检查取消',
			width : 420,
			height : 400,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : false,
			border: 'thin',
			href : window.localStorage.ctx+'/examine/gotoexamineCancel',
			buttons:[{
				text:'确定',
				width:80,
				handler:function(){cancelExamine(row);}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}]
		});
}


//取消检查更新数据库
function cancelExamine(row){
	getJSON(window.localStorage.ctx+"/examine/cancelStudyOrder",
		{
	        cancelreason:$("#cancelreason").textbox("getValue"),
			orderid : row.studyorderpkid
		},
		function(json){
			console.log(json)
			if(json.code==0){
				$('#common_dialog').dialog('close');
				searchExamine();
				_message('检查取消成功！','提醒');
			}else{
				_message('取消失败请重试，如果问题依然存在，请联系系统管理员！失败原因：'+json.message,'提醒');
			}
		}
	);
}

function openDutyRoster(){
	$('#common_dialog').dialog({
		title : '排班表',
		width : 1100,height : 650,
		modal : true,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,
		border: 'thin',
		href : window.localStorage.ctx+'/examine/showDutyRoster',
		buttons:[{
			text:$.i18n.prop('report.close'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}


/**
 * 双击触发
 * 发送一个病人叫号需要添加的信息
 * @returns
 */
function diccallinginfo_send() {
	var studyorderpkid = $("#studyorderid_exam").val();
	var status = $("#orderstatus_exam").val();
	var row=$('#dicallinginfo_dg').datagrid('getSelected');
	if(row==null){
		_message('请选择提示信息','提醒');
		return;
	}
	var sendmessage = row.message;
	var completetime=$("#completetime").val();
	var studyid = $('#studyorderstudyid_exam').textbox('getValue');
	var index = $('#dicallinginfo_dg').datagrid('getRowIndex',$('#dicallinginfo_dg').datagrid('getSelected'));
	if(status==myCache.StudyOrderStatus.in_process){
		getJSON(window.localStorage.ctx+"/examine/diccallingsendmessage",
				{
					orderid : studyorderpkid,
					sendmessage: sendmessage,
					studyid: studyid,
					index: index
				},
				function(data) {
					var json=validationData(data);
					if(json.code==0) {
						//完成检查
						if($("#examinecallingorcomplete").val() == 'completecalling') {
							canCompleteApp(studyorderpkid,completetime);
							
						}
						_message('发送成功！','提醒');
					}else {
						_message(json.message,'提醒');
					}
					$('#common_dialog').dialog('close');
				}
			);
	}else {
		_message('当前操作无法完成！','提醒');
	}
	
}

function examinecallingstatus(val, row, index) {
	if(row.callinghistory != null){
		return '已叫号';
	}else {
		return '未叫号';
	}
}

function examinecancel() {
	var status=$("#orderstatus_exam").val();
	if(status!=myCache.StudyOrderStatus.in_process){
		_message('当前状态无法取消！','提醒');
		return;
	}
	
	$('#common_dialog').dialog(
	{
		title : '检查取消',
		width : 420,
		height : 400,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : false,
		border: 'thin',
		href : window.localStorage.ctx+'/examine/gotoexamineCancel',
		buttons:[{
			text:'确定',
			width:80,
			handler:function(){canCancelApp($("#studyorderid_exam").val());}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

//取消检查更新数据库
function canCancelApp(studyorderpkid){
	getJSON(window.localStorage.ctx+"/examine/cancelStudyOrder",
		{
	        cancelreason:$("#cancelreason").textbox("getValue"),
			orderid : studyorderpkid
		},
		function(json){
			if(json.code==0){
				$("#orderstatus_exam").val(myCache.StudyOrderStatus.canceled).change();
				$('#common_dialog').dialog('close');
				refreshWaitinglist();
				_message('检查取消成功！','提醒');
			}else{
				_message('取消失败请重试，如果问题依然存在，请联系系统管理员！失败原因:'+json.message,'提醒');
			}
		}
	);
}

function examineCancelReasonDblClick(index,row){
	$("#cancelreason").textbox("setValue",row.name);
}

function saveStudyAcquisitionModeDefault(){
	$("#collectionForm_exam").form("submit",{
		url: window.localStorage.ctx+"/examine/saveStudyAcquisitionModeDefault",
		onSubmit: function(){},
	    success:function(data){
	    	var result=validationDataAll(data);
	    	if(result.code==0){
	    		_message('保存成功！','保存成功');
			}else{
				_message(result.message,'保存失败');
			}
	    }
		
	});
}

