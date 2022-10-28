

/**
 * 保存查询条件，关闭弹窗
 * 
 * @returns
 */
function saveFilter(filterType) {
	var condition= $('#condition').val();
	if ( trim(condition)== '') {
		return;
	}
	getJSON(window.localStorage.ctx +"/"+filterType+"/checkMyConditionName",
		{
			filterType: filterType,
			name: condition
		},
		function(data) {
			// 校验 我的条件 保存的命名是否已经存在
			if (data.data) {
				$("#searchForm_"+filterType).form('submit', {
					url:window.localStorage.ctx+"/"+filterType+"/saveFilter?description="+ condition +"&filter_type="+ filterType,
					success : function(data) {
						var json=validationData(data);
						if(json.code==0){
							$('#progress_dlg').dialog('close');
							// 成功
							$('#common_dialog').dialog('close');
							// 刷新页面
							$('#myfilterlist_'+filterType).datalist('reload');
						} else if(json.code==-1){
							_message("123"+$.i18n.prop('savefailed'),$.i18n.prop('error'));
						} else {
							$('#progress_dlg').dialog('close');
						}
					}
				});
			} else {
				_message('此名称已存在！',$.i18n.prop('error'));
			}
		});
}

//删除查询数据
function removefilter(filterType) {
	var ids = "";
	var idArry = $("#dg_filter_"+filterType).datagrid("getSelections");
	if (idArry.length==0) {
		_message($.i18n.prop('filter.selectfilter'),$.i18n.prop('alert'));
		return;
	}
	for (var i = 0,len=idArry.length; i < len; i++) {
		if (i + 1 == len){
			ids += idArry[i].id
		} else {
			ids += idArry[i].id + ",";
		}
	}
	$.messager.confirm({
		title:$.i18n.prop('confirm'),
		msg:'确定要删除选中部分常用条件吗？',
		border:'thin',
		fn:function(r){
		    if (r){
		    	$.post(window.localStorage.ctx +"/"+filterType+"/deleteFilter",
		    		{
		    			id : ids
		    		},
		    		function(data) {
		    			var json=validationData(data);
		    			if(json.code==0){
		    				// 成功
		    				$.getJSON( window.localStorage.ctx +"/"+filterType+"/getFilters?filterType="+filterType,function(data){
		    					var json=validationData(data);
		    					$('#dg_filter_'+filterType).datagrid("loadData", json);
		    					$('#myfilterlist_'+filterType).datalist("loadData", json);
		    				});
		    			}else if(json.code==2){
		    				$.messager.show({
		        				title:$.i18n.prop('error'),
		        				msg:json.message,
		        				timeout:1000,border:'thin',
		        				showType:'slide'
		        			});
		    			}else{
		    				$.messager.show({
		        				title:$.i18n.prop('error'),
		        				msg:$.i18n.prop('deletefailed'),
		        				timeout:1000,border:'thin',
		        				showType:'slide'
		        			});
		    			}
			    	});
			    }else{
			    	return;
			    }
			}
	});
}

/**
 * 打开 保存查询条件 弹窗
 * 
 * @returns
 */
function openFilterSaveDialog(filterType){
	$('#common_dialog').dialog({
		title: $.i18n.prop('filter.savefilter'),
		width : 320,
		height : 150,
		closed : false,
		cache : false,
		border: 'thin',
		href: window.localStorage.ctx+'/'+filterType+'/openFilterSaveDialog',
		modal : true,
		buttons:[{
			text:$.i18n.prop('save'),
			width:80,
			handler:function(){saveFilter(filterType);}
		},{
			text:$.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

/**
 * 查询条件管理
 * 
 * @returns
 */
function openFilterManageDialog(filterType){
	$('#common_dialog').dialog({
		title : $.i18n.prop('filter.filtermanage'),
		width : 850,
		height : 400,
		closed : false,
		cache : false,
		href : window.localStorage.ctx +'/'+filterType+'/openFilterManageDialog',
		border: 'thin',
		modal : true,
		buttons:[{
			text:$.i18n.prop('wl.delete'),
			width:80,
			handler:function(){removefilter(filterType);}
		},{
			text:$.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}],
		onLoad:function(){
			$.getJSON( window.localStorage.ctx +"/"+filterType+"/getFilters?filterType="+filterType,function(json){
				$('#dg_filter_'+filterType).datagrid("loadData", validationData(json));
			});
		}
	});
}

