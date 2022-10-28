var myCache;
$(function() {
	myCache=JSON.parse(window.localStorage.myCache);
});
function initFollowup(){
	
}

//初始化west查询界面
function initWestsearch(){
	$('#searchform').form({
		url: window.localStorage.ctx+'/followup/searchFollowup',
		onSubmit: function(){
			$('#progress_dlg').dialog('open');
			$('#followup_label').val($('#labelct').combotree('getValues'));
		},
		success: function(data){
			console.log(666);
			$('#progress_dlg').dialog('close');
			$('#westsearchDg').datagrid('loadData', validationData(data));
		}
	});
}

function searchFollowup(){
	var datefrom=$("#datefrom").datebox("getValue");
	var dateto=$("#dateto").datebox("getValue");
	if(!datefrom){
		_message('请输入开始时间！' , '提示');
	}else if(!dateto){
		_message('请输入结束时间！' , '提示');
	}else{
		$('#searchform').form('submit');
	}
	
}

function clearSearch(){
	$("#datefrom").datebox("setValue","");
	$("#dateto").datebox("setValue","");
	$("#labelct").combotree("setValue","");
	$("#reportdoctor").combobox("setValue","");
}

//双击west查询结果
function dbClikdg_west(row){
	console.log(row);
	$('#patientinfo_form').form('clear');
	$('#studyinfo_form').form('clear');
//	var followup_doctorname=$("#followup_doctorname").textbox("getValue");
	$('#followup_form').form('clear');
//	$("#followup_doctorname").textbox("setValue",followup_doctorname);
	$('#patientinfo_form').form('load', row);
	$('#orderid').val(row.orderid);
	loadFollowupData();
	$('.followup_btn').linkbutton('enable');
	
}

function addFollowup(){
	$('#followup_la').layout('expand','south');//.panel('expand');
	clearFollowup();
	$('.editfollowup_btn').linkbutton('enable');
//	$('#editfollowup_btn').linkbutton('enable');
}


function cancelAddFollowup(){
	$('#followup_la').layout('collapse','south');
}

//清空随访
function clearFollowup(){
	var orderid = $('#orderid').val();
	$("#followup_form").form('clear');
	$('#orderid').val(orderid);
}

/**
 * 保存随访
 * @returns
 */
function saveFollowup(){
	var patientid=$("#patientid").textbox("getValue");
	if(patientid){
		$('#followup_form').form('submit',{
			url: window.localStorage.ctx+'/followup/saveFollowup',
			onSubmit: function(){
				var isValid = $(this).form('validate');
				if (!isValid){
					_message('请输入必填信息！' , '提示');
					return isValid;
		        }
			},
			success: function(data){
				$('#progress_dlg').dialog('close');
				var json = validationDataAll(data);
				if(json.code == 0){
					clearFollowup();
					_message('保存成功！' , '成功');
					$('#followup_la').layout('collapse','south');
					loadFollowupData();
				}else{
					_message('保存失败' , '提示');
				}
			}
		});
	}else{
		_message('请先选择要随访病人！！！' , '提示');
	}
}

//删除随访数据
function deleteFollowup(){
	var row = $('#followupDg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title : $.i18n.prop('confirm'),msg : '确认删除选中的随访记录吗?', border: 'thin',
			fn : function(r){
		    if (r){
		    	getJSON(window.localStorage.ctx+'/followup/deleteFollowup',
					{
						followupid : row.id
					},
					function(data){
						var json = validationDataAll(data);
						if(json.code == 0){
							if(row.id == $('#followupid').val()){
								clearFollowup();
							}
							_message('删除成功！' , '成功');
							loadFollowupData();
						}
					});
		    }
		}});
	}else{
		_message('请选择要删除的随访记录！' , '提示');
	}
}

function loadFollowupData(){
	getJSON(window.localStorage.ctx+'/followup/loadFollowupData',
		{
			orderid : $('#orderid').val()
		},
		function(data){
			console.log(data);
			$('#followupDg').datagrid('loadData', data);
		});
}

//双击随访
function onClickRow_followupDg(row){
	console.log(row);
	clearFollowup();
	$('#followup_la').layout('expand','south');
	$('.editfollowup_btn').linkbutton('enable');
	$('#followup_form').form('load', row);
}

function age_formatter(value,row,index){
	if(value){
		return value+row.ageunitdisplay;
	}
	else{
		return "";
	}
}

function columeStyler_reportstatus_fl(value,row,index){
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

