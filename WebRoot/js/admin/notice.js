var ue;

$(function() {
	var notice={
			type : "notice",
			exec : function(data){
				var type = data.split(',')[1];
				var noticeId = data.split(',')[0];
				var size = data.split(',')[2];
				getJSON(window.localStorage.ctx+"/notice/remindNotice",{noticeId : noticeId},function(data){});
				$.messager.confirm({
					title: '收到公告',
					ok: '是',
					cancel: '否',
					border: 'thin',
					msg: '您收到' + size + '份新的公告，是否立即打开查看？',
					fn: function(r){
						if(r || !r && type == 1){
							if(!r && type == 1){
								layer.msg('此公告是必读公告！');
							}
							userNoticeCenter(noticeId);
						}
					}
				});
			}
	};
	if(websocket){
		initService_WS(notice);
	}
});


function searchNotice(){
	$("#notice_dg").datagrid("load",{
		type : $('#messageType').val(),
		timeType : $('#timeType').val(),
		beginTime: $('#beginTime').val(),
		title: $('#noticeTitle').val(),
		endTime: $('#endTime').val()
	});
}

function usersearchNotice(){
	$("#user_notice_dg").datagrid("load",{
		type : $('#user_messageType').val(),
		timeType : $('#user_timeType').val(),
		beginTime: $('#user_beginTime').val(),
		title: $('#user_noticeTitle').val(),
		endTime: $('#user_endTime').val()
	});
}

function openNotice(id, tableId){
	var row=$('#'+ tableId).datagrid('getSelected');
	$('#'+ id).panel('refresh', window.localStorage.ctx + '/notice/showNotice?noticeId=' + row.id);
}

function toCreateNotice(){
	$('#notice_dialog').dialog({
		title : '新增公告',
		left: 130,
		top: 35,
		width : 1060,
		height : 700,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/notice/toEditNotice',
		onLoad:function(){
			ue = UE.getEditor('container',{autoHeightEnabled: false});
			ue.ready(function() {
				ue.setContent($('#content').val());
			});
		},
		onClose:function(){
			if(UE.delEditor('container'))r.destroy();
		},
		buttons:[{
			text:'保存',
			width:80,
			handler:function(){saveNotice();}
		},{
			text:'立即发布',
			width:130,
			handler:function(){saveNotice(1);}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#notice_dialog').dialog('close');}
		}]
	});
}
	
function saveNotice(type) {
	var selectedData = $('#user_tree').tree('getChecked');
	var idStr = '';
	for(var i = 0; i < selectedData.length; i++){
		var nodeId = selectedData[i].id;
		if(!isNaN(nodeId)){
			idStr += nodeId + ",";
		}
	}
	if(idStr == '' || idStr == null){
		$.messager.show({title:'提示', msg: '发送用户不能为空！', timeout:2000, border:'thin', showType:'slide'});	
		return;
	}else if(!$('#title').val()){
		$.messager.show({title:'提示', msg: '标题不可为空！', timeout:2000, border:'thin', showType:'slide'});	
		return;
	}else if(!ue.getContent()){
		$.messager.show({title:'提示', msg: '内容不可为空！', timeout:2000, border:'thin', showType:'slide'});	
		return;
	}else if($('#sendtime').val()){
		if(checkSendTime($('#sendtime').val(), new Date()) < 5){
			$.messager.show({title:'提示', msg: '发送时间需要大于当前时间！', timeout:2000, border:'thin', showType:'slide'});	
			return;
		}
	}
	var url = $("#noticeId").val() ? '/notice/updateNotice' : '/notice/saveNotice'
	$('#selectedUserIds').val(idStr);
	$('#noticeFrom').form('submit', {
		url: window.localStorage.ctx + url,
		onSubmit: function(param){
			param.content = ue.getContent();
			param.contenttxt = ue.getContentTxt();
		},
		success: function(data){
			var message = '保存成功！'
			console.log(data);
			var json=validationDataAll(data);
			if(json.code == 0){
				if(type){
					commonSendNotice(json.data);
				}
				message = '保存成功！'
				$('#notice_dialog').dialog('close');
				searchNotice();
			}else{
				message = '保存失败，请重试！'
			}
			$.messager.show({title:'提示', msg: message, timeout:2000, border:'thin', showType:'slide'});	
		}
	});
}

function toEditNotice(){
	var row=$('#notice_dg').datagrid('getSelected');
	if(row==null){
		_message("请选择要修改的记录！","提示");
		return;
	}
	if(row.releasedflag == 1){
		$.messager.show({title:'提示', msg: '已发送公告不可修改！', timeout:2000, border:'thin', showType:'slide'});	
		return;
	}
	$('#notice_dialog').dialog({
		title : '编辑公告',
		left: 130,
		top: 35,
		width : 1300,
		height : 700,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/notice/toEditNotice?noticeId=' + row.id,
		onLoad:function(){
			ue = UE.getEditor('container',{autoHeightEnabled: false});
			ue.ready(function() {
				ue.setContent($('#content').html());
			});
		},
		onClose:function(){
			if(UE.delEditor('container'))r.destroy();
		},
		buttons:[{
			text:'保存',
			width:80,
			handler:function(){saveNotice();}
		},{
			text:'保存并发送',
			width:130,
			handler:function(){saveNotice(1);}
		},{
			text:'关闭',
			width:80,
			handler:function(){$('#notice_dialog').dialog('close');}
		}]
	});
}

function fileOnChange (newFile, oldFile){
	if(newFile != oldFile && $('#delFile').val().indexOf(oldFile) == -1){
		$('#delFile').val(oldFile + "," + $('#delFile').val());				
	}
}

function removeFile(fimeName, inputId){
	if(fimeName){
		if($('#delFile').val().indexOf(fimeName) == -1){
			$('#delFile').val(fimeName + "," + $('#delFile').val());
		}
	}
	$('#' + inputId).filebox('clear');
}

function delNotice(){
	var row=$('#notice_dg').datagrid('getSelected');
	if(row==null){
		_message("请选择要删除的记录！","提示");
		return;
	}
	$.messager.confirm({
		title: '确认删除',
		ok: '是',
		cancel: '否',
		border: 'thin',
		msg: '确认删除选中的数据吗？',
		fn: function(r){
			if (r){
				getJSON(window.localStorage.ctx+"/notice/delNotice", {id : row.id},
					function(data){
						if(data.code==0){
							searchNotice();
							$.messager.show({title:'提示', msg: '删除成功' , timeout:2000, border:'thin', showType:'slide'});	
						}else{
							$.messager.show({title:'错误提醒', msg:"保存失败请重试，如果问题依然存在，请联系系统管理员！", timeout:3000, border:'thin', showType:'slide'});
						}
					}
				);
			}
		}
	});
}
function sendNotice(){
	var row=$('#notice_dg').datagrid('getSelected');
	if(row==null){
		_message("请选择要发布的公告！","提示");
		return;
	}
	commonSendNotice(row.id);
}

function commonSendNotice(noticeId){
	getJSON(window.localStorage.ctx+"/notice/sendNotice", {'noticeId' : noticeId},
			function(data){
				if(data.code==0){
					searchNotice();
					$.messager.show({title:'提示', msg: '发送成功' , timeout:2000, border:'thin', showType:'slide'});
				}else{
					$.messager.show({title:'错误提醒', msg:"发送失败请重试，如果问题依然存在，请联系系统管理员！", timeout:3000, border:'thin', showType:'slide'});
				}
			}
		);
}

function checkSendTime(selectDate, nowDate){
	var syear = selectDate.substr(0,4);
	var smounth = selectDate.substr(5,2);
	var sday = selectDate.substr(8,2);
	var shours = selectDate.substr(11,2);
	var sminutes = selectDate.substr(14,2);
	var sseconds = selectDate.substr(17,2);
	
	var nyear = nowDate.getFullYear();
	var nmounth = (nowDate.getMonth() + 1);
	nmounth = nmounth.toString().length == 1 ? '0' + nmounth : nmounth;
	var nday = nowDate.getDate();
	nday = nday.toString().length == 1 ? '0' + nday : nday;
	var nhours = nowDate.getHours();
	nhours = nhours.toString().length == 1 ? '0' + nhours : nhours;
	var nminutes = nowDate.getMinutes();
	nminutes = nminutes.toString().length == 1 ? '0' + nminutes : nminutes;
	var nseconds = nowDate.getSeconds();
	nseconds = nseconds.toString().length == 1 ? '0' + nseconds : nseconds;
	
	var sTime = syear + smounth + sday + shours + sminutes + sseconds;
	var nTime = nyear + nmounth + nday + nhours + nminutes + nseconds;
	return sTime -nTime;
}

function send_state_formater(value, row, index){
	return row.releasedflag == 1 ? '已发布':'未发布';
}

function mustreade_formater(value, row, index){
	return row.mustread == 1 ? '是':'否';
}

function noticeDefaultSelect(noticeId,data){
	for (var i = 0,len=data.rows.length; i < len; i++) {
		if (noticeId == data.rows[i].id) {
			$("#user_notice_dg").datagrid('selectRow',i);
			$('#user_center').panel('refresh', window.localStorage.ctx + '/notice/showNotice?noticeId=' + row.id);
			return;
		}
	}
}

function userNoticeCenter(noticeId){
	$('#read_notice_dig').dialog({
		title : '阅读公告',
		left: 130,
		top: 35,
		width : 1000,
		height : 700,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		border: 'thin',
		href : window.localStorage.ctx+'/notice/userNoticeCenter?noticeId=' + noticeId,
		onLoad:function(){
			if(noticeId){
				$('#user_center').panel('refresh', window.localStorage.ctx + '/notice/showNotice?noticeId=' + noticeId);
			}
		},
		buttons:[{
			text:'关闭',
			width:80,
			handler:function(){$('#read_notice_dig').dialog('close');}
		}]
	});
}


