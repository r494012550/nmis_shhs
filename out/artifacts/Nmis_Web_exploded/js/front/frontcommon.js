/**
 * 
 */

//申请单
function apply(orderid){
	
	if(!orderid){
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : '请选择检查！',
			timeout : 3000,border:'thin',showType : 'slide'
		});
		return;
	}
	getJSON(window.localStorage.ctx+'/frontcommon/getImage?orderid='+orderid,null,function(data){
		var json = validationDataAll(data);
		console.log(json);
		if(json.code==0){
			if(json.data){
//				$("#imageShow1").attr("src","image/image_GetApplyImg?path="+json.data.img1);
//				$("#imageShow2").attr("src","image/image_GetApplyImg?path="+json.data.img2);
				$(".gallerys").empty();
				
				var imageShow=null;
				
				var imgstrs="";
				for(var i=1;i<11;i++){
					if(json.data["img"+i]){
						imgstrs+="<img id='imageShow"+i+"' src='image/image_GetApplyImg?path="+
								json.data["img"+i]+"' class='gallery-pic' onclick='$.openPhotoGallery(this)' orderid='"+
								json.data["orderid"] +"'/>";
						if(!imageShow)imageShow="imageShow"+i;
					}
				}

				if(imageShow){
					$('#common_dialog').dialog({
						title : '申请单',
						width : 800,height: 600,
						modal : true,maximizable:false,minimizable:false,draggable:true,resizable:false,collapsible:false,border:'thin',
						href: window.localStorage.ctx+'/frontcommon/toAppInfo',
						onLoad: function(){
							//console.log(imageShow);
							console.log($("#"+imageShow));
							$(imgstrs).appendTo(".gallerys");
							//$.openPhotoGallery($("#"+imageShow)[0])
							
							$("#"+imageShow).click();
						},
						onClose:function(){
							$(".gallerys").empty();
							if(window.parent.document.getElementById("J_pg"))
							window.parent.document.getElementById("J_pg").remove();
						},
						buttons:[]
					});
				}
				else{
					$.messager.show({
						title : $.i18n.prop('alert'),
						msg : '无申请单！',
						timeout : 3000,border:'thin',showType : 'slide'
					});
				}
				
			}
			else{
				$.messager.show({
					title : $.i18n.prop('alert'),
					msg : '无申请单！',
					timeout : 3000,border:'thin',showType : 'slide'
				});
			}
		}
		else{
			$.messager.show({
				title : $.i18n.prop('error'),
				msg : '打开申请单失败，请重试。如果问题依然存在，请联系系统管理员！',
				timeout : 3000,border:'thin',showType : 'slide'
			});
		}
	});
	
}

//检查流程
function process(orderid){
//	$.getJSON( window.localStorage.ctx +"/dic/getExamItemDicFromCache?organ=40&modality=MR",function(json){
//		console.log(validationData(json));
//	});
	
	if(orderid){
		openCheckProcess(orderid);
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
 * 打开检查流程
 */
function openCheckProcess(orderid){
	$('#common_dialog').dialog({
		title:'检查流程',
		width : 550,height : 450,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,
		border: 'thin',
		href : window.localStorage.ctx+'/frontcommon/gotoStudyProcess?orderid='+orderid,
		modal : true,
		buttons:[{
			text:'关闭',
			width:60,
			handler:function(){$('#common_dialog').dialog('close');}
		}],
		onLoad:function(){
			$.getJSON( window.localStorage.ctx +"/frontcommon/getStudyProcess?orderid="+orderid,function(json){
				$('#studyprocess_dg').datagrid("loadData", validationData(json));
			});
		}
	});
}

//********************webView*****************

/**
 * 
 */

//申请单
function apply_webView(orderid){
	
	if(!orderid){
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : '请选择检查！',
			timeout : 3000,border:'thin',showType : 'slide'
		});
		return;
	}
	
	getJSON(window.localStorage.ctx+'/webview/getImage?orderid='+orderid,null,function(data){
		var json = validationDataAll(data);
		console.log(json);
		if(json.code==0){
			if(json.data){
//				$("#imageShow1").attr("src","image/image_GetApplyImg?path="+json.data.img1);
//				$("#imageShow2").attr("src","image/image_GetApplyImg?path="+json.data.img2);
				$(".gallerys").empty();
				
				var imageShow=null;
				
				var imgstrs="";
				for(var i=1;i<11;i++){
					if(json.data["img"+i]){
						imgstrs+="<img id='imageShow"+i+"' src='image/image_GetApplyImg?path="+
								json.data["img"+i]+"' class='gallery-pic' onclick='$.openPhotoGallery(this)' orderid='"+
								json.data["orderid"] +"'/>";
						if(!imageShow)imageShow="imageShow"+i;
					}
				}

				if(imageShow){
					$('#common_dialog_applyimage').dialog({
						title : '申请单',
						width : document.body.clientWidth-240,height: document.body.clientHeight-140,
						modal : true,maximizable:false,minimizable:false,draggable:true,resizable:false,collapsible:false,border:'thin',
						href: window.localStorage.ctx+'/webview/toAppInfo',
						onLoad: function(){
							//console.log(imageShow);
							console.log($("#"+imageShow));
							$(imgstrs).appendTo(".gallerys");
							//$.openPhotoGallery($("#"+imageShow)[0])
							
							$("#"+imageShow).click();
						},
						onClose:function(){
							$(".gallerys").empty();
							if(window.parent.document.getElementById("J_pg"))
							window.parent.document.getElementById("J_pg").remove();
						}
					});
				}
				else{
					$.messager.show({
						title : $.i18n.prop('alert'),
						msg : '无申请单！',
						timeout : 3000,border:'thin',showType : 'slide'
					});
				}
				
			}
			else{
				$.messager.show({
					title : $.i18n.prop('alert'),
					msg : '无申请单！',
					timeout : 3000,border:'thin',showType : 'slide'
				});
			}
		}
		else{
			$.messager.show({
				title : $.i18n.prop('error'),
				msg : '打开申请单失败，请重试。如果问题依然存在，请联系系统管理员！',
				timeout : 3000,border:'thin',showType : 'slide'
			});
		}
	});
	
}

/**
 * 检查流程
 * @param orderid
 * @returns
 */
function process_webView(orderid){
	if(orderid){
		openCheckProcess_webView(orderid);
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
 * 打开检查流程
 */
function openCheckProcess_webView(orderid){
	$('#common_dialog').dialog({
		title:'检查流程',
		width : 550,height : 450,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,
		border: 'thin',
		href : window.localStorage.ctx+'/webview/gotoStudyProcess?orderid='+orderid,
		modal : true,
		buttons:[{
			text:'关闭',
			width:60,
			handler:function(){$('#common_dialog').dialog('close');}
		}],
		onLoad:function(){
			$.getJSON( window.localStorage.ctx +"/webview/getStudyProcess?orderid="+orderid,function(json){
				$('#studyprocess_dg').datagrid("loadData", validationData(json));
			});
		}
	});
}




//*****************************为满足权限管理，区分模块实现功能********************************************

//申请单
function apply(orderid,module){
	if(!orderid){
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : '请选择检查！',
			timeout : 3000,border:'thin',showType : 'slide'
		});
		return;
	}
	
	getJSON(window.localStorage.ctx+'/'+module+'/getImage?orderid='+orderid,null,function(data){
		var json = validationDataAll(data);
		if(json.code==0){
			if(json.data){
				$(".gallerys").empty();
				
				var imageShow=null;
				
				var imgstrs="";
				for(var i=1;i<11;i++){
					if(json.data["img"+i]){
						imgstrs+="<img id='imageShow"+i+"' src='image/image_GetApplyImg?path="+
								json.data["img"+i]+"' class='gallery-pic' onclick='$.openPhotoGallery(this)' orderid='"+
								json.data["orderid"] + "'/>";
						if(!imageShow)imageShow="imageShow"+i;
					}
				}

				if(imageShow){
					$('#common_dialog_applyimage').dialog({
						title : '申请单',
						width : document.body.clientWidth-200,height: document.body.clientHeight-100,//900,height: 670,
						modal : true,maximizable:false,minimizable:false,draggable:true,resizable:false,collapsible:false,border:'thin',
						href: window.localStorage.ctx+'/frontcommon/toAppInfo',
						onLoad: function(){
							$(imgstrs).appendTo(".gallerys");
							$("#"+imageShow).click();
						},
						onClose:function(){
							$(".gallerys").empty();
							if(window.parent.document.getElementById("J_pg"))
							window.parent.document.getElementById("J_pg").remove();
						}
					});
				}
				else{
					$.messager.show({
						title : $.i18n.prop('alert'),
						msg : '无申请单！',
						timeout : 3000,border:'thin',showType : 'slide'
					});
				}
				
			}
			else{
				$.messager.show({
					title : $.i18n.prop('alert'),
					msg : '无申请单！',
					timeout : 3000,border:'thin',showType : 'slide'
				});
			}
		}
		else{
			$.messager.show({
				title : $.i18n.prop('error'),
				msg : '打开申请单失败，请重试。如果问题依然存在，请联系系统管理员！',
				timeout : 3000,border:'thin',showType : 'slide'
			});
		}
	});
	
}
//检查流程
function process(orderid,module){
	if(orderid){
		openCheckProcess(orderid,module);
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
//打开检查流程
function openCheckProcess(orderid,module){
	$('#common_dialog').dialog({
		title:'检查流程',
		width : 550,height : 450,
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,
		border: 'thin',
		href : window.localStorage.ctx+'/'+module+'/gotoStudyProcess?orderid='+orderid,
		modal : true,
		buttons:[{
			text:'关闭',
			width:60,
			handler:function(){$('#common_dialog').dialog('close');}
		}],
		onLoad:function(){
			getJSON( window.localStorage.ctx +'/frontcommon/getStudyProcess',
					{
						orderid : orderid
					},
					function(json){
						$('#studyprocess_dg').datagrid("loadData", validationData(json));
					});
		}
	});
}

//*****************************为满足权限管理，区分模块实现功能********************************************
function studyprocess_compare(dg,index,row){
	var after = row.logmessages || "";
	var before = "";
	if(index){
		before = dg.datagrid("getRows")[index-1].logmessages || "";
	}
	
	$('#studyprocess_dialog').dialog('open').dialog('setTitle','痕迹对比');;
	getJSON(window.localStorage.ctx +'/frontcommon/compareStudyProcess',
			{
				before : before,
				after : after,
			},
			function(json){
				var data=validationData(json);
				//console.log(data);
				var content1 = data[0];
				var content2 = data[1];
				$('#mypanel_studyprocesscompare').panel({
			        content : content2
			    });
				
			});
}

//保存查询结果列布局
function saveDatagridColumn(target){
//	console.log($(target).datagrid('options').columns);
//	console.log($(target).datagrid('options').frozenColumns);
	var targetid = $(target).attr("id");
	var moudle = $(target).data('this-moudle');
	var frozenFields = $(target).datagrid('getColumnFields', true);
	var fields = $(target).datagrid('getColumnFields');
	//冻结
	var frozenOpts = [];
	for (let i = 0; i < frozenFields.length; i++) {
		var frozenfield = frozenFields[i];
		var frozencol = $(target).datagrid('getColumnOption', frozenfield);
		frozencol.original_index = i;
		if(frozencol.styler){
			frozencol.styler_name = frozencol.styler.toString().match(/function\s*([^(]*)\(/)[1];
		}
		if(frozencol.formatter){
			frozencol.formatter_name = frozencol.formatter.toString().match(/function\s*([^(]*)\(/)[1];
		}
		frozenOpts.push(frozencol);
	}
	//未冻结
	var opts = [];
	for (let i = 0; i < fields.length; i++) {
		var field = fields[i];
		var col = $(target).datagrid('getColumnOption', field);
		col.original_index = i;
		if(col.styler){
			col.styler_name = col.styler.toString().match(/function\s*([^(]*)\(/)[1];
		}
		if(col.formatter){
			col.formatter_name = col.formatter.toString().match(/function\s*([^(]*)\(/)[1];
		}
		opts.push(col);
	}
	getJSON(window.localStorage.ctx + '/frontcommon/saveDatagridColumn',
			{
				targetid : targetid,
				frozenOpts : JSON.stringify(frozenOpts),
				opts : JSON.stringify(opts),
				moudle : moudle
			},
			function(data){
				console.log(validationDataAll(data));
			});
}

//保存datagrid页面尺寸
function saveDatagridPagination(target){
	var targetid = $(target).attr("id");
	var moudle = $(target).data('this-moudle');
	var thisPageSize = $(target).datagrid('getPager').data('pagination').options.pageSize;
	getJSON(window.localStorage.ctx + '/frontcommon/saveDatagridPagination',
			{
				targetid : targetid,
				moudle : moudle,
				thisPageSize : thisPageSize
			},
			function(data){
				console.log(validationDataAll(data));
			});
}

//启用列移动，加载用户列配置
function startReDatagridColumn(target, moudle){
	$(target).data('this-moudle', moudle);
	//启用列移动
	$(target).datagrid('columnMoving');
	//加载用户列配置
	getJSON(window.localStorage.ctx + '/frontcommon/getDatagridColumn',
			{
				targetid : $(target).attr("id"),
				moudle : moudle
			},
			function(data){
				var jsonData = validationDataAll(data);
				console.log(jsonData);
				//分页属性
				const paginationOptions = $(target).datagrid('getPager').data("pagination").options;
				
				if(jsonData.code == 0 && jsonData.data.length > 0){
					$(target).data('this-userCols', jsonData.data);
					$(target).datagrid('resetColumns');
				}else{
					var frozenfields = $(target).datagrid('getColumnFields', true);
					var fields = $(target).datagrid('getColumnFields', false);
					let frozenSortcolumns = [];
					for(let i = 0; i < frozenfields.length; i++){
						let col = $(target).datagrid('getColumnOption', frozenfields[i]);
						if(col){
		    				col.hidden = false;
		    				frozenSortcolumns.push(col);
		    			}
					}
					let sortcolumns = [];
					for(let i = 0; i < fields.length; i++){
						let col = $(target).datagrid('getColumnOption', fields[i]);
						if(col){
		    				col.hidden = false;
		    				sortcolumns.push(col);
		    			}
					}
					$(target).datagrid({
		    			frozenColumns: [frozenSortcolumns],
	    				columns: [sortcolumns]
	    			}).datagrid('columnMoving');
				}	
				
				//初始化分页属性
	    		initPagination(target, paginationOptions);
	    		
			});
}

//初始化分页
function initPagination(target, paginationOptions){
	var p = $(target).datagrid('getPager'); 
    $(p).pagination({ 
        pageSize: paginationOptions.pageSize,//每页显示的记录条数，默认为20 
        pageNumber: paginationOptions.pageNumber,
        pageList: paginationOptions.pageList,//可以设置每页记录条数的列表 
        //showRefresh:false,
        beforePageText: paginationOptions.beforePageText,//页数文本框前显示的汉字 
        afterPageText: paginationOptions.afterPageText,
        displayMsg: paginationOptions.displayMsg,
        onChangePageSize: paginationOptions.onChangePageSize,
        onSelectPage: paginationOptions.onSelectPage
    });
}

/**
 *  初始化默认的排序列表
 */
function restoreDefaults(moudle) {
	if (moudle != "") {
		$.messager.confirm({
			title : $.i18n.prop('confirm'),msg : '确认恢复初始配置！', border: 'thin',
			fn : function(r){
			    if (r){
			    	getJSON(window.localStorage.ctx + '/frontcommon/restoreDefaults',
		    			{
		    				moudle : moudle
		    			},
		    			function(data){
		    				var jsonData = validationDataAll(data);
		    				if (jsonData) {
		    					_message("初始化成功，请重新刷新页面！");
		    				} else {
		    					_message("初始化失败！");
		    				}
		    			}
		    		);
			    }
			}
		});
	}
}
