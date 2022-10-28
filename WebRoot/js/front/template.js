/**
 * 
 */

//function addnode(studyid) {
//	sendAjax({
//		url:window.localStorage.ctx+'/worklist/checkSession',
//		type:'post',
//		success:function(data){
//		}
//	})     
//	var root = $("#nodetree_" + studyid).tree('getRoot');
//	var node = $("#nodetree_" + studyid).tree('getSelected');
//	if (node) {
//
//		var parent = $("#nodetree_" + studyid).tree('getParent', node.target);
//
//		$.messager.prompt(
//						'添加节点',
//						'请输入节点名称',
//						function(r) {
//							if (r) {
//								// alert('Your name is:' + r);
//
//								$.ajax({
//											url : window.localStorage.ctx+"/templatenode/addTemplateNode",
//											contentType : "application/x-www-form-urlencoded; charset=UTF-8",
//											data : {
//												nodename : r,
//												modality : node.modality,
//												parent : (parent ? parent.id
//														: root.id),
//												username : $("#userid_hidden")
//														.val(),
//												ispublic : '1'
//											},
//											success : function(da) {
//												var d = validationData(da);
//												var dd = JSON.parse(d);
//												$("#nodetree_" + studyid)
//														.tree(
//																'append',
//																{
//																	parent : (parent ? parent.target
//																			: root.target),
//																	data : dd
//																});
//											},
//											dataType : 'json'
//										});
//							}
//						});
//	} 
//}


	
	
	/**
 * 关闭添加子分类窗口
 * @returns
 */
function closeclassifynode(reportid){
	
	$('#classifyName_' + reportid).textbox("setValue","");
	$('#model_windows_' + reportid).window('close');
}

function generateCode(obj,reportid){
	var str = pinyinUtil.getFirstLetter($(obj).val()).trim();
	$("#templateCode_"+reportid).textbox('setValue',str);
	/*var str=$.trim($(obj).toPinyin());
	var pinyin="";
	if(str==""){
		$("#templateCode_"+reportid).textbox('setValue',str);
		return;
	}else{
		var attr=str.split(" ");
		for(var i=0;i<attr.length;i++){
			pinyin+=attr[i].charAt(0);
		}
		$("#templateCode_"+reportid).textbox('setValue',pinyin.trim());
	}*/
}

/**
 * 添加子分类的方法
 * 
 * @returns
 */
function addchildnode(reportid){
	    
	var node=$("#nodetree_" +reportid).tree('getSelected');
	console.log("type:"+node.type);
	if(node==null||node.type!="node"){
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : $.i18n.prop('report.selectchildnode'),
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
		return;
	}
	else{
		if(node.ispublic=="1" && !$('#editpublicnode').val()){
			$.messager.show({
				title : $.i18n.prop('alert'),
				msg : $.i18n.prop('report.no_editpublictemplate_permission'),
				timeout : 3000,
				border:'thin',
				showType : 'slide'
			});
		}
		else{
			$('#model_windows_' + reportid).window('open');
		}
		
	}
}
/**
 * 添加子分类
 * @param reportid
 * @returns
 */
function saveclassifynode(reportid) {
	console.log("==save classifynode==");
	var node = $("#nodetree_" + reportid).tree('getSelected');
	console.log(node);
	$('#classifyForm_'+reportid).form('submit', {
		url: window.localStorage.ctx+"/template/addTemplateNode_wl?parent="+node.id+"&modality="+node.attributes.modality+"&ispublic="+node.ispublic,
		onSubmit: function(){
			if($('#classifyName_'+reportid).textbox('getText')==''){
				return false;
			}
		},
		success: function(data){
			closeclassifynode(reportid);
			var res=validationDataAll(data);
			
			if(res.code == 0){
//				$.messager.show({
//		            title:'成功',
//		            msg:"添加节点成功！",
//		            timeout:3000,
//		            showType:'slide'
//		        });
//				$("#nodetree_" + studyid).tree("append",{
//					 parent: node.target,
//					 data : res.data
//				 });
				
				$("#nodetree_" + reportid).tree('reload',node.target);
			} else {
				$.messager.show({
		            title: $.i18n.prop('error'),
		            msg: $.i18n.prop('savefailed'),
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
			}
		}
	});
	

	
//	var r = $("#classifyName_" + orderid).val();
//	if (node) {
//		if(!trim(r)){
//			$.messager.show({
//	            title:'提醒',
//	            msg:"子分类节点名称不能为空！",
//	            timeout:3000,
//	            showType:'slide'
//	        });
//			return false;
//			}
//			$("#model_windows_" + studyid).window("close");
//			$.ajax({
//				url : "/templatenode/addTemplateNode",
//				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
//				data : {
//				nodename : r,
//				modality : 'CT',
//				parent : node.id,
//				username : $("#userid_hidden").val(),
//				ispublic : '1'
//				},
//				success : function(res) {
//					if(res.code == 0){
//						$.messager.show({
//							title : '成功',
//							msg : "添加子节点成功！",
//							timeout : 3000,
//							showType : 'slide'
//						});
//						/*添加新增节点到树中*/
//						 $("#nodetree_" + studyid).tree("append",{
//						  parent : node.target,
//						  data : res.data
//					    });
//					}else{
//						$.messager.show({
//							title : '提醒',
//							msg : res.message,
//							timeout : 3000,
//							showType : 'slide'
//						});
//					}
//				},
//				dataType : 'json'
//			});
//	} else {
//		$.messager.show({
//			title : '提醒',
//			msg : "请选择一个节点！",
//			timeout : 3000,
//			showType : 'slide'
//		});
//	}
}
/**
 * 修改节点
 * @param reportid
 * @returns
 */
function modifynode(reportid) {
	
	var node = $("#nodetree_" + reportid).tree('getSelected');
	if (node&&node.parent!=0) {
		
		if (node.ispublic=="1" && !$('#editpublicnode').val()) {
			$.messager.show({
				title : $.i18n.prop('alert'),
				msg : $.i18n.prop('report.no_editpublictemplate_permission'),
				timeout : 3000,
				border:'thin',
				showType : 'slide'
			});
		} else {
			if(node.type=="template"){
				$('#common_dialog_reporttemp').dialog(
						{
							title : $.i18n.prop('report.edittemplate'),
							width : 760,height : 650,
							cache : false,modal : true,
							border: 'thin',
							href : window.localStorage.ctx+'/template/addTemplate_wl?tempid=' + node.id
							+ '&nodeid=' + node.attributes.nodeid + '&reportid=' + reportid,
							buttons:[{
								text:$.i18n.prop('report.getcurrentreport'),
								width:200,
								handler:function(){
									getCurrReport(reportid);
								}
							},
							{
								text:$.i18n.prop('save'),
								width:80,
								handler:function(){saveTemplate(reportid,node);}
							},
							{
								text:$.i18n.prop('cancel'),
								width:80,
								handler:function(){$('#common_dialog_reporttemp').dialog('close');}
							}]
							,onLoad:function(){
								$('#tempname_'+reportid).textbox('setValue',node.attributes.name);
								$('#templateCode_'+reportid).textbox('setValue',node.attributes.code);
								$('#studymethod_temp_'+reportid).textbox('setValue',node.attributes.studymethod||"");
								try{
									UM.delEditor('result_win_'+reportid);
									UM.delEditor('desc_win_'+reportid);
								} catch(ex){
									console.log(ex);
								}
								if(node.attributes.resultcontent_html){
									UM.getEditor('result_win_'+reportid).ready(function() {
										this.setContent(node.attributes.resultcontent_html);
									});
								}else{
									UM.getEditor('result_win_'+reportid);
								}
								
								
								if(node.attributes.desccontent_html){
									UM.getEditor('desc_win_'+reportid).ready(function() {
										this.setContent(node.attributes.desccontent_html);
									});
								}else{
									UM.getEditor('desc_win_'+reportid);
								}	
							},
							onClose: function () {
								console.log('onClose')
//								try{
//									var r=UM.delEditor('result_win_'+reportid);
//									if(r)r.destroy();
//									var d=UM.delEditor('desc_win_'+reportid);
//									if(d)d.destroy();
//								} catch(ex){
//									console.log(ex);
//								}
					        }
				});
			} else {
				$("#nodetree_" + reportid).tree('beginEdit', node.target);
			}
		
		}
	}
	else {
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : $.i18n.prop('report.selectchildnode'),
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
	}
}

function submitMidifynode(node,reportid) {
	    
	if(trim(node.text)!=""){
		if(node.text.length<50){
			$.getJSON(window.localStorage.ctx+"/template/modifyTemplateNode_wl?nodeid=" + node.id
					+ "&nodename=" + node.text, function(json) {
				var js = validationDataAll(json);
				console.log(js);
				if (js.code == 0) {
//					$.messager.show({
//						title : '成功',
//						msg : "保存成功！",
//						timeout : 3000,
//						showType : 'slide'
//					});
				} else {
					$("#nodetree_" + reportid).tree('beginEdit',node.target);
					$.messager.show({
			            title: $.i18n.prop('error'),
			            msg: js.message,
			            timeout:3000,
			            border:'thin',
			            showType:'slide'
			        });
				}
			});
		}else{
			$.messager.show({
	            title: $.i18n.prop('error'),
	            msg: $.i18n.prop('report.nodenametoolong'),
	            timeout:3000,
	            border:'thin',
	            showType:'slide'
	        });
			$("#nodetree_" + reportid).tree('beginEdit',node.target);
		}
	}else{
		$.messager.show({
            title: $.i18n.prop('alert'),
            msg: $.i18n.prop('report.nodenamecannotempty'),
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
		$("#nodetree_" + reportid).tree('beginEdit',node.target);
	}
	
}


function deletenode(reportid) {
	 
	var node = $("#nodetree_" + reportid).tree('getSelected');

	if(node&&node.parent!=0){
		if(node.ispublic=="1" && !$('#editpublicnode').val()){
			$.messager.show({
				title : $.i18n.prop('alert'),
				msg : $.i18n.prop('report.no_editpublictemplate_permission'),
				timeout : 3000,
				border:'thin',
				showType : 'slide'
			});
		} else {
			if(node.type=="node"){
				$.messager.confirm({
					title: $.i18n.prop('confirm'),
					msg: $.i18n.prop('report.confirmdeletenode'),
					border:'thin',
					fn: function(r){
						if (r){
							 $.getJSON(window.localStorage.ctx+"/template/delTemplateNode_wl?nodeid="+node.id, function(json){
								 	var js=validationData(json);
							    	if (js.code==0) {
							    		$("#nodetree_" + reportid).tree('remove',node.target);
//							    		_message("删除成功！");
							    	} else {
							    		$.messager.show({
							                title:$.i18n.prop('error'),
							                msg: js.message,
							                timeout:3000,
							                border:'thin',
							                showType:'slide'
							            });
							    	}
							 });
						}
					}
				});
			}
			else{
				$.messager.confirm({
					title : $.i18n.prop('confirm'),
					border:'thin',
					msg : $.i18n.prop('report.confrimdeletetemplate'),
					fn : function(r) {
						if (r) {
							$.getJSON(window.localStorage.ctx+"/template/delTemplate_wl?tempid=" + node.attributes.id,
									function(data) {
										// alert("JSON Data: " + json.patientname);
										var json = validationData(data);
										if (json.code == 0) {
											var parent=$("#nodetree_" + reportid).tree('getParent',node.target);
	//										$("#nodetree_" + studyid).tree('remove',node.target);
											console.log(parent);
											
											
	//										$('#tt').tree('update', {
	//											target: node.target,
	//											text: 'new text'
	//										});
	//										parent.state="close";
	//										console.log(parent);
	//										$("#nodetree_" + studyid).tree('update',{
	//											target: parent.target,
	//											data: parent
	//										});
											
											$("#nodetree_" + reportid).tree('reload',parent.target);
	//										var index = $("#templist_" + studyid)
	//												.datagrid('getRowIndex', row);
	//										$("#templist_" + studyid).datagrid(
	//												'deleteRow', index);
										} else {
											$.messager.show({
												title : $.i18n.prop('error'),
												msg : $.i18n.prop('deletefailed'),
												timeout : 3000,
												border:'thin',
												showType : 'slide'
											});
										}
									});
						}
					}
				});
			}
		
		}
		
	} else {
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : $.i18n.prop('report.selectchildnode'),
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
	}
	
}



/**
 * 修改模板
 * @param id
 * @param studyid
 * @returns
 */
function updateTemplate(id,reportid) {
	  
	$('#tempForm_' + reportid).form('submit', {
		url : window.localStorage.ctx+"/template/updateTemplate?id=" + id,
		onSubmit : function() {
			var name = $('#tempname_' + reportid).val();
			if(!name){
				$.messager.show({
					title : '提醒',
					msg : "请输入模板名称！",
					timeout : 3000,
					border:'thin',
					showType : 'slide'
				});
				return false;
			}
			if (name.trim() == '') {
				$.messager.show({
					title : '提醒',
					msg : "模板名称不能为空！",
					timeout : 3000,
					border:'thin',
					showType : 'slide'
				});
				return false;
			}

			var ue_desc = UM.getEditor('temp_desc_' + reportid);
			$('#desccontent_' + reportid).val(ue_desc.getPlainTxt());

			var ue_result = UM.getEditor('temp_result_' + reportid);
			$('#resultcontent_' + reportid).val(ue_result.getPlainTxt());

		},
		success : function(data) {
			var json = validationData(data);
			if (json.code == 0) {
				$('#temp_windows_' + reportid).window('close');
				var node = $("#nodetree_" + reportid).tree('getSelected');
				getTemplate(node.id, reportid);
				$.messager.show({
					title : '提醒',
					msg : "修改模板信息成功!",
					timeout : 3000,
					border:'thin',
					showType : 'slide'
				});
			} else {
				$.messager.show({
					title : '提醒',
					msg : "保存失败请重试，如果问题依然存在，请联系系统管理员！",
					timeout : 3000,
					border:'thin',
					showType : 'slide'
				});
			}
		}
	});
}

/**
 * 增加模板
 * @param id
 * @param reportid
 * @returns
 */
function addTemplate(reportid) {
  
	var node = $("#nodetree_" + reportid).tree('getSelected');
	$('#tempForm_' + reportid).form('submit', {
		url : window.localStorage.ctx+"/template/saveTemplate_wl?nodeid=" + node.id,
		onSubmit : function() {
			var name = $('#tempname_' + reportid).val();
			if(!name){
				$.messager.show({
					title : '提醒',
					msg : "请输入模板名称！",
					timeout : 3000,
					border:'thin',
					showType : 'slide'
				});
				return false;
			}
			if (name.trim() == '') {
				$.messager.show({
					title : '提醒',
					msg : "模板名称不能为空！",
					timeout : 3000,
					border:'thin',
					showType : 'slide'
				});
				return false;
			}

			var ue_desc = UM.getEditor('temp_desc_' + reportid);
			$('#desccontent_' + reportid).val(ue_desc.getPlainTxt());

			var ue_result = UM.getEditor('temp_result_' + reportid);
			$('#resultcontent_' + reportid).val(ue_result.getPlainTxt());

		},
		success : function(data) {
			var json = validationData(data);
			if (json.code == 0) {
				$('#temp_windows_' + reportid).window('close');
				
				getTemplate(node.id, reportid);
				$.messager.show({
					title : '提醒',
					msg : "添加模板信息成功!",
					timeout : 3000,
					border:'thin',
					showType : 'slide'
				});
			} else {
				$.messager.show({
					title : '提醒',
					msg : "保存失败请重试，如果问题依然存在，请联系系统管理员！",
					timeout : 3000,
					border:'thin',
					showType : 'slide'
				});
			}
		}
	});
}

function getTemplate(nodeid, reportid) {

//	$('#tempcontent_' + studyid).html('');
	 $.getJSON(window.localStorage.ctx+"/template/getTemplate_wl?nodeid=" + nodeid, function(json) {

		$('#templist_' + reportid).datagrid('loadData', validationData(json));

	});
}

function previewTemplate(row, reportid) {
   
	var content = '<b>【所见】:</b><br/>' + row.desccontent
			+ '<br/><b>【诊断】:</b><br/>' + row.resultcontent;
	$('#tempcontent_' + reportid).html(content);
}

var time = null;

  //模板单击
 function previewTemplateNew(reportid) {
	 console.log('previewTemplateNew');

	var node = $("#nodetree_" + reportid).tree('getSelected');
	if(node&&node.parent!=0){ 
		
		if(node.attributes&&node.attributes.name){
	    	 var content = '<b>【'+$.i18n.prop('report.studymethod')+'】:</b><br/>' + (node.attributes.studymethod||"")
	    		+ '<br/><b>【'+$.i18n.prop('wl.reportdesc')+'】:</b><br/>' + (node.attributes.desccontent_html||"")
	    		+ '<br/><b>【'+$.i18n.prop('wl.reportresult')+'】:</b><br/>' + (node.attributes.resultcontent_html||"");
	 		$.messager.alert({
	 			title : node.attributes.name,
	 			msg : content,
	 			width : 450,
	 			height : 400,
	 			border : 'thin'
	
	 		});
		}

	}
	else{
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : $.i18n.prop('report.selecttemplate'),
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
	}
 }

// 模板双击
function applyTemplate(row, reportid) {
	console.log(row);
    // 取消上次延时未执行的方法
//    clearTimeout(time);
    //执行延时
//    time = setTimeout(function(){
        //do function在此处写shuang击事件要执行的代码
    	// alert(row.name);
//    	var rowInfo = JSON.parse($("#orderStatus_"+studyid).val());
	
	//alert($("#orderStatus_"+studyid).val())
	if($("#orderStatus_"+reportid).val() != myCache.ReportStatus.FinalResults){
    	$.messager.confirm({
    		title : '使用模板',
    		ok : '追加',
    		cancel : '替换',
    		border:'thin',
    		msg : '是否替换原报告内容？',
    		fn : function(r) {
    			var ue_result = UM.getEditor('result_' + reportid + '_html');
    			var ue_desc = UM.getEditor('desc_' + reportid + '_html');
				if (r) {
					ue_result.setContent(row.resultcontent_html || "", true);
					ue_desc.setContent(row.desccontent_html || "", true);
				} else {
					ue_result.setContent(row.resultcontent_html || "");
					ue_desc.setContent(row.desccontent_html || "");
				}
    		}
    	});
	}else{
		$.messager.show({
			title : '提醒',
			msg : "该报告是最终报告不可编辑！",
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
	}
//    },200);
}

function applyTemplate_new(node,reportid){
	console.log(node);
	if(node.attributes&&node.attributes.name){
		if($("#orderStatus_"+reportid).val() != myCache.ReportStatus.FinalResults){
			var content = '<b>【'+$.i18n.prop('report.studymethod')+'】:</b><br/>' + (node.attributes.studymethod||"")
    		+ '<br/><b>【'+$.i18n.prop('wl.reportdesc')+'】:</b><br/>' + (node.attributes.desccontent_html||"")
    		+ '<br/><b>【'+$.i18n.prop('wl.reportresult')+'】:</b><br/>' + (node.attributes.resultcontent_html||"");
			var ue_result = UM.getEditor('result_' + reportid + '_html');
			var ue_desc = UM.getEditor('desc_' + reportid + '_html');
			
			// 标记dialog已打开
			$('#dialog_' + reportid).val("1");
			
			//去除模板中的字体和字体大小
//			let res_html=node.attributes.resultcontent_html;
//			if(res_html){
//				var res = $('<div>'+res_html+'</div>');
//				res.find("[style*='font-size']").each(function(index , obj){
//					$(obj).removeAttr('style');
//				});
//				res_html=res.html();
//				res.remove();
//			}
//			let desc_html=node.attributes.desccontent_html;
//			if(desc_html){
//				var des=$('<div>'+desc_html+'</div>');
//				des.find("[style*='font-size']").each(function(index , obj){
//					$(obj).removeAttr('style');
//				});
//				desc_html=des.html();
//				des.remove();
//			}
			$('#common_dialog').dialog({
	    		title : '使用模板',
	    		border:'thin',
	    		width : 500,
	 			height : 470,
	 			href : window.localStorage.ctx+"/template/reporttemplatepreview?id="+node.id,
	    		buttons:[{
	    			text:'追加',
	    			width:63,
	    			handler:function(){
//		    				ue_result.setContent(res_html || "", true);
//							ue_desc.setContent(desc_html || "", true);
		    				var resultcontent = node.attributes.resultcontent;
		    				var desccontent = node.attributes.desccontent;
		    				resultcontent = resultcontent.replace(/\r\n/g, '<br/>');
		    				desccontent = desccontent.replace(/\r\n/g, '<br/>');
							ue_result.setContent(resultcontent || "", true);
							ue_desc.setContent(desccontent || "", true);
							
							var oldValue=$('#method_'+reportid).textbox("getValue");
							var newValue=node.attributes.studymethod||"";
							$('#method_'+reportid).textbox("setValue",oldValue+newValue);
							$('#common_dialog').dialog('close');
						}
		    		},{
		    			text:'替换',
		    			width:63,
		    			handler:function(){
//		    				ue_result.setContent(res_html || "");
//							ue_desc.setContent(desc_html || "");
		    				var resultcontent = node.attributes.resultcontent;
		    				var desccontent = node.attributes.desccontent;
		    				resultcontent = resultcontent.replace(/\r\n/g, '<br/>');
		    				desccontent = desccontent.replace(/\r\n/g, '<br/>');
							ue_result.setContent(resultcontent || "");
							ue_desc.setContent(desccontent || "");
							$('#method_'+reportid).textbox("setValue",node.attributes.studymethod || "");
							$('#common_dialog').dialog('close');
		    			}
		    		}]
	    	});
			
		}else{
			$.messager.show({
				title : '提醒',
				msg : "该报告是最终报告不可编辑！",
				timeout : 3000,
				border:'thin',
				showType : 'slide'
			});
		}
	}
	
}



/**
 * 打开添加模板页面
 * @param reportid
 * @returns
 */
function addTemplateContent(reportid) {
	var node = $("#nodetree_" + reportid).tree('getSelected');
	if (node && node.parent && node.parent != 0) {	
		
		if(node.ispublic=="1" && !$('#editpublicnode').val()){
			$.messager.show({
				title : $.i18n.prop('alert'),
				msg : $.i18n.prop('report.no_editpublictemplate_permission'),
				timeout : 3000,
				border:'thin',
				showType : 'slide'
			});
		}
		else{
			$('#common_dialog_reporttemp').dialog(
					{
						title : $.i18n.prop('report.edittemplate'),
						width : 760,height : 670,
						top : 100,
						cache : false,modal : true,
						border: 'thin',
						href : window.localStorage.ctx+'/template/addTemplate_wl?reportid=' + reportid+ '&nodeid=' + node.id,
						buttons:[{
							text:$.i18n.prop('report.getcurrentreport'),
							width:200,
							handler:function(){
								getCurrReport(reportid);
							}
						},
						{
							text:$.i18n.prop('save'),
							width:80,
							handler:function(){saveTemplate(reportid,node);}
						},
						{
							text:$.i18n.prop('cancel'),
							width:80,
							handler:function(){$('#common_dialog_reporttemp').dialog('close');}
						}]
						,onLoad:function(){
							try {
								UM.delEditor('result_win_'+reportid);
								UM.delEditor('desc_win_'+reportid);
							} catch(e) {
								console.log(e);
							}
							UM.getEditor('result_win_'+reportid);
							UM.getEditor('desc_win_'+reportid);

							$('#tempForm_' + reportid).form('load', {
								tempid : '',
								name : '',
								desccontent : '',
								resultcontent : '',
								desccontent_html : '',
								resultcontent_html : ''
							});
						},
						onClose: function () {
							console.log('onClose')
//							try {
//								var r=UM.delEditor('result_win_'+reportid);
//								if(r)r.destroy();
//								var d=UM.delEditor('desc_win_'+reportid);
//								if(d)d.destroy();
//							} catch(e) {
//								console.log(e);
//							}
				        }
			});
		}
		
	} else {
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : $.i18n.prop('report.selectchildnode'),
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
	}
}
/**
 * 保存报告
 * @param reportid
 * @returns
 */
function saveTemplate(reportid,node){
		//var nodeid=node.id;
		$('#tempForm_' + reportid).form('submit', {
			url: window.localStorage.ctx+"/template/saveTemplate_wl?ispublic="+node.ispublic,
			onSubmit: function(){
				if(!trim($("#tempname_"+reportid).textbox('getValue'))){
					return false;
				}
				var ue_desc = UM.getEditor('desc_win_'+reportid);
//				ue_desc.execCommand('selectall');
				//去除样式
//				ue_desc.execCommand('removeformat');
				var desc_txt =  UM.utils.html(ue_desc.getPlainTxt());
				$('#desccontent_'+reportid).val(desc_txt);
				
				var ue_result = UM.getEditor('result_win_'+reportid);
//				ue_result.execCommand('selectall');
//				ue_result.execCommand('removeformat');
				var result_txt = UM.utils.html(ue_result.getPlainTxt());
				$('#resultcontent_'+reportid).val(result_txt);
			},
			success: function(data){
			 	var json=validationDataAll(data);
		    	if (json.code==0) {
		    		var tempid=$('#templateid_'+reportid).val();
		    		if (tempid) {
		    			var parentnode=$("#nodetree_" + reportid).tree('getParent',node.target);
		    			console.log(parentnode)
		    			$("#nodetree_" + reportid).tree('reload',parentnode.target);
		    		} else {
		    			$("#nodetree_" + reportid).tree('reload',node.target);
		    		}
//		        	getTemplate(nodeid,studyid);
		        	$('#common_dialog_reporttemp').dialog('close');
		        } else if(json.code==2) {
		        	$.messager.show({
		                title:$.i18n.prop('error'),
		                msg:json.message,
		                timeout:3000,
		                border:'thin',
		                showType:'slide'
		            });
		        }else{
		        	$.messager.show({
		                title:$.i18n.prop('error'),
		                msg:$.i18n.prop('savefailed'),
		                timeout:3000,
		                border:'thin',
		                showType:'slide'
		            });
		        }
			}
		});

}

//function modifyTemplateContent(studyid) {
//
//	var node = $("#nodetree_" + studyid).tree('getSelected');
//	console.log(node);
//
//	if(node&&node.parent!=0){
//		
//		if(node.type=="template"){
//			$('#common_dialog').dialog(
//					{
//						title : $.i18n.prop('report.edittemplate'),
//						width : 800,height : 720,
//						cache : false,modal : true,
//						border: 'thin',
//						href : window.localStorage.ctx+'/template/addTemplate?tempid=' + node.id
//						+ '&nodeid=' + node.attributes.nodeid + '&studyid=' + studyid,
//						buttons:[{
//							text:$.i18n.prop('report.getcurrentreport'),
//							width:200,
//							handler:function(){
//								getCurrReport(studyid);
//							}
//						},
//						{
//							text:$.i18n.prop('save'),
//							width:80,
//							handler:function(){saveTemplate(studyid,node);}
//						},
//						{
//							text:$.i18n.prop('cancel'),
//							width:80,
//							handler:function(){$('#common_dialog').dialog('close');}
//						}]
//						,onLoad:function(){
//							$('#tempname' + studyid).textbox('setValue',node.attributes.name);
//							
//
//							UE.delEditor('result_win_' + studyid);
//							UE.getEditor('result_win_' + studyid).ready(
//									function() {
//										this.setContent(node.attributes.resultcontent_html);
//									});
//
//							UE.delEditor('desc_win_' + studyid);
//							UE.getEditor('desc_win_' + studyid).ready(function() {
//								this.setContent(node.attributes.desccontent_html);
//							});
//						}
//			});
//		}
//	}
//}

function delTemplate(reportid) {
	 
	var row = $("#templist_" + reportid).datagrid('getSelected');
	if (row) {
		$.messager.confirm({
			title : $.i18n.prop('confirm'),

			msg : $.i18n.prop('report.confrimdeletetemplate'),
			fn : function(r) {
				if (r) {
					$.getJSON(window.localStorage.ctx+"/template/delTemplate_wl?tempid=" + row.id,
							function(data) {
								// alert("JSON Data: " + json.patientname);
								var json = validationData(data);
								if (json.code == 0) {

									var index = $("#templist_" + reportid)
											.datagrid('getRowIndex', row);
									$("#templist_" + reportid).datagrid(
											'deleteRow', index);
								} else {
									$.messager.show({
										title : $.i18n.prop('error'),
										msg : $.i18n.prop('deletefailed'),
										timeout : 3000,
										border:'thin',
										showType : 'slide'
									});
								}
							});
				}
			}
		});
	} else {
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : $.i18n.prop('report.selecttemplate'),
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
	}
}

function closeTempDialog(reportid) {
	  
	$('#temp_windows_' + reportid).window('close');
	// $('#temp_windows_'+studyid).panel('destroy');

}

function getCurrReport(reportid) {
	  
	var ue_res = UM.getEditor('result_' + reportid + '_html');
	var ue_res_temp = UM.getEditor('result_win_'+reportid);
	ue_res_temp.setContent(ue_res.getContent());
	//ue_res_temp.setHeight(180);
	var ue_desc = UM.getEditor('desc_' + reportid + '_html');
	var ue_desc_temp = UM.getEditor('desc_win_'+reportid);
	ue_desc_temp.setContent(ue_desc.getContent());
	//ue_desc_temp.setHeight(190);
	$('#studymethod_temp_'+reportid).textbox("setValue",$('#method_'+reportid).textbox("getValue"));
}


///**
// * 收藏夹
// * 
// * @param studyid
// * @returns
// */
//function showReport(reportId){  
//	    
//	$('#dlg_edit').window('open');
//}

/**
 * 初始化模板列表
 * 
 * @returns
 */
//function initTempletsTree(){  
//	var name = $("#templetName").val();
//	$("#templets").tree({
//		url : window.localStorage.ctx+'/templet/getTree?name=' + name
//	})
//}


function selectTemplateNode(node,reportid){
//	console.log(node.attributes.creator);
	var userid=$("#userid").val();
	if(node.ispublic=="1" && !$('#editpublicnode').val()){
		$('#addchildnode1_'+reportid).linkbutton('disable');
		$('#addTemplateContent1_'+reportid).linkbutton('disable');
		$('#modifynode1_'+reportid).linkbutton('disable');
		$('#deletenode1_'+reportid).linkbutton('disable');
		
		if(node.type=="node"){
			$('#previewTemplateNew1_'+reportid).linkbutton('disable');
		}else{
			$('#previewTemplateNew1_'+reportid).linkbutton('enable');
		}
		
	}else{
		if(node.type=="node"){
			$('#addchildnode1_'+reportid).linkbutton('enable');
			$('#addTemplateContent1_'+reportid).linkbutton('enable');
			$('#previewTemplateNew1_'+reportid).linkbutton('disable');
		}else{
			$('#addchildnode1_'+reportid).linkbutton('disable');
			$('#addTemplateContent1_'+reportid).linkbutton('disable');
			$('#previewTemplateNew1_'+reportid).linkbutton('enable');
		}
		if ("个人模板" == node.text&&userid!=node.attributes.creator) {
			$('#modifynode1_'+reportid).linkbutton('disable');
			$('#deletenode1_'+reportid).linkbutton('disable');
		} else {
			$('#modifynode1_'+reportid).linkbutton('enable');
			$('#deletenode1_'+reportid).linkbutton('enable');
		}
	}
	
	if(!$('#editpublicnode').val()&&node.type=="node"&&node.attributes.creator=="system"){
		$('#modifynode1_'+reportid).linkbutton('disable');
		$('#deletenode1_'+reportid).linkbutton('disable');
	}
	//公共模板根节点禁止删除
	if(node.parent==0){
		$('#deletenode1_'+reportid).linkbutton('disable');
	}
	
}

/**
 *  模板右键菜单栏
 */
function enableTemplateMenu(node,reportid) {
	var userid=$("#userid").val();
	if (node.ispublic=="1" && !$('#editpublicnode').val()) {
		var item = $('#mm_'+reportid).menu('findItem', {name:'addchildnode2_'+reportid});
		$('#mm_'+reportid).menu('disableItem', item.target);
		item = $('#mm_'+reportid).menu('findItem', {name:'addTemplateContent2_'+reportid});
		$('#mm_'+reportid).menu('disableItem', item.target);
		item = $('#mm_'+reportid).menu('findItem', {name:'modifynode2_'+reportid});
		$('#mm_'+reportid).menu('disableItem', item.target);
		item = $('#mm_'+reportid).menu('findItem', {name:'deletenode2_'+reportid});
		$('#mm_'+reportid).menu('disableItem', item.target);
	} else {
		// 添加子节点
		var item = $('#mm_'+reportid).menu('findItem', {name:'addchildnode2_'+reportid});
		if(node.type=="node"){
			$('#mm_'+reportid).menu('enableItem', item.target);
		}else{
			$('#mm_'+reportid).menu('disableItem', item.target)
		}
		// 添加模板
		item = $('#mm_'+reportid).menu('findItem', {name:'addTemplateContent2_'+reportid});
		if (node.type=="node") {
			$('#mm_'+reportid).menu('enableItem', item.target);
		} else {
			$('#mm_'+reportid).menu('disableItem', item.target);
		}
		// 修改节点
		item = $('#mm_'+reportid).menu('findItem', {name:'modifynode2_'+reportid});
		if (node.text == "个人模板"&&userid!=node.attributes.creator) {
			$('#mm_'+reportid).menu('disableItem', item.target);
		} else {
			$('#mm_'+reportid).menu('enableItem', item.target);
		}
		// 删除
		item = $('#mm_'+reportid).menu('findItem', {name:'deletenode2_'+reportid});
		if (node.text == "个人模板"&&userid!=node.attributes.creator) {
			$('#mm_'+reportid).menu('disableItem', item.target);
		} else {
			$('#mm_'+reportid).menu('enableItem', item.target);
		}
		
		
		item = $('#mm_'+reportid).menu('findItem', {name:'previewTemplateNew2_'+reportid});
		if (node.type=="node") {
			$('#mm_'+reportid).menu('disableItem', item.target);
		} else {
			$('#mm_'+reportid).menu('enableItem', item.target);
		}
	}
	
	if (!$('#editpublicnode').val()&&node.type=="node"&&node.attributes.creator=="system") {
		item = $('#mm_'+reportid).menu('findItem', {name:'modifynode2_'+reportid});
		$('#mm_'+reportid).menu('disableItem', item.target);
		item = $('#mm_'+reportid).menu('findItem', {name:'deletenode2_'+reportid});
		$('#mm_'+reportid).menu('disableItem', item.target);
	}
	// 删除
	item = $('#mm_'+reportid).menu('findItem', {name:'deletenode2_'+reportid});
	//公共模板根节点禁止删除
	if(node.parent==0){
		$('#mm_'+reportid).menu('disableItem', item.target);
	}
}

/**
 * 实时输入事件
 * 
 * @returns
 */
$('#templetName').bind('input propertychange', function() {  
	initTempletsTree();
});

/**
 * 搜索报告模板
 * @param value  搜索的内容
 * @param name  
 * @param modality 设备类型
 * @param reportid 报告的id
 * @returns
 */
function searchTemplate(value,name,modality,reportid) {
	$('#addchildnode1_'+reportid).linkbutton('disable');
	$('#addTemplateContent1_'+reportid).linkbutton('disable');
	$('#modifynode1_'+reportid).linkbutton('disable');
	$('#deletenode1_'+reportid).linkbutton('disable');
	$('#previewTemplateNew1_'+reportid).linkbutton('disable');
	if (value == "") {
		// 搜索内容为空时，将加载整棵树
		getJSON(window.localStorage.ctx+"/template/getTemplateNodeByModality",{
			modality:modality,
			creator:"1"
		},function(date) {
			$('#nodetree_' + reportid).tree('loadData',date);
			var res = $('#dialog_' + reportid).val();
			console.log(res);
			if (res == "1") {
				$('#common_dialog').dialog('close');
				$('#dialog_' + reportid).val("0");
			}
		});
	} else {
		// 搜索内容不为空，则搜索相应的模板
		getJSON(window.localStorage.ctx+"/template/searchTemplate",{
			modality:modality,
			searchContent:value
		},function(date) {
			$('#nodetree_' + reportid).tree('loadData',date);
			if (date.length == 1) {
				// 但搜索结果为一个时，将默认打开此模板
				applyTemplate_new(date[0],reportid);
			} else {
				var res = $('#dialog_' + reportid).val();
				// 判断dialog是否已打开
				if (res == "1") {
					$('#common_dialog').dialog('close');
					$('#dialog_' + reportid).val("0");
				}
			}
		});
	}
}

/**
 *  当节点被放置时触发
 * @param target DOM 对象，放置的目标节点。
 * @param source 源节点。
 * @param point 表示放置操作，可能的值是：'append'、'top' 或 'bottom'。
 * @returns
 */
function onBeforeDropNode(target,source,point,reportid) {
	var targetNode = $("#nodetree_" + reportid).tree('getNode', target);
//	console.log(targetNode);
//	console.log(source);
	// method用来 判断用户是否是对公共模板进行操作；moveNode（普通模板），copyNode（公共模板）
	var method = "moveNode";
	// 判断移动的模板是公共模板还是个人模板
	if (source.ispublic == '1') {
		// 公共模板
		if (targetNode.ispublic != '1') {
			// 目标节点为个人节点
			method = "copyNode";
		}
	} else {
		// 个人模板
		if (targetNode.ispublic == '1') {
			// 目标节点为公共节点
			method = "copyNode";
		}
	}
	getJSON(window.localStorage.ctx+"/template/" + method,{
		templateid:source.id,
		nodeid:targetNode.id,
		ispublic:targetNode.ispublic
	},function(date) {
		var parentNode = $("#parentNode_" + reportid).val();
		parentNode = JSON.parse(parentNode);
		var node = $("#nodetree_" + reportid).tree('find', parentNode.id);
		console.log(parentNode);
		if (method == "copyNode") {
			$("#nodetree_" + reportid).tree('reload', node.target);
		} else {
			if (parentNode.children.length == 1) {
//				console.log(node);
//				node.state = "closed";
//				$("#nodetree_" + reportid).tree('reload', node.target);
//				$("#nodetree_" + reportid).tree('collapse', node.target);
//				$("#nodetree_" + reportid).tree('update', node);
			}
		}
		
	});
}

/**
 * 当节点的拖拽开始时触发，返回 false 则禁止拖拽。
 * @param node
 * @param reportid
 * @returns
 */
function onBeforeDragNode(node, reportid) {
	if (node.type == 'node') {
		// 节点不能被拖动
        return false;
    } else {
    	// 判断该角色是否有拖动公共模板的权限
    	if (node.ispublic=="1" && !$('#editpublicnode').val()) {
    		return false;
    	} else {
    		var parentNode = $("#nodetree_" + reportid).tree('getParent', node.target);
    		$("#parentNode_" + reportid).val(JSON.stringify(parentNode));
    		return true;
    	}
    }
}

