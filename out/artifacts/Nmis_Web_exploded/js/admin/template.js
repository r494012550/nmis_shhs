
$(document).ready(function(){
	
})

//function getTemplate(nodeid){
//	 
//	var ue_desc = UE.getEditor('desc_ad');
//	var ue_result = UE.getEditor('result_ad');
//	if(rt==null){
//		if($("#templateName").val()!="" ||trim(ue_result.getPlainTxt())!="" ||  trim(ue_desc.getPlainTxt()) !=""){
//			$.messager.confirm({
//				title: '提醒',
//				ok: '是',
//				cancel: '否',
//				msg: '是否保存新增？',
//				fn: function(r){
//				if(!r){
//					getTemplate_(nodeid);
//					return;
//					}else{
//						saveAdminTemplate();
//						return;
//					}
//				}
//			 });
//		}else{
//			getTemplate_(nodeid);
//			return;
//		}
//	}else{
//		if(rt.name == $("#templateName").val() 
//				&& (rt.resultcontent_html || '') == ue_result.getContent()
//				&& (rt.desccontent_html || '') == ue_desc.getContent() ){
//			getTemplate_(nodeid);
//		}else{
//			$.messager.confirm({
//				title: '提醒',
//				ok: '是',
//				cancel: '否',
//				msg: '是否保存修改？',
//				fn: function(r){
//				if(!r){
//					getTemplate_(nodeid);
//					}else{
//						modifyTemplate(rt);
//						//rt = null;
//					}
//				}
//			 });
//	       }
//	}
//}
/**
 * 展开树节点
 * @param node
 * @returns
 */
function expandTree(node){
	$("#nodetree").tree('options').url=window.localStorage.ctx+"/template/getTemplateNodeByModality?modality="
		+$('#topModality').textbox('getValue')+"&type=node&ispublic=1";
}

/**
 * 获取模板
 * @returns
 */
function getTemplate_(node){
//	document.getElementById("templateUpdate").style.display = "none";
//	document.getElementById("templateSave").style.display = "";
//	document.getElementById("templateConsole").style.display = "";
	if(node.parent!=0){
		$.getJSON(window.localStorage.ctx+"/template/getTemplate?nodeid="+node.id, function(json){
	    	$('#templist').datagrid('loadData',validationData(json));
	    	$('#previewtemplate').html("");
	    	$('#new_linkbutton').linkbutton({disabled:false});
	        $('#del_linkbutton').linkbutton({disabled:false});
	        $('#modify_linkbutton').linkbutton({disabled:false});
	    });
	}else{
		$('#templist').datagrid('loadData',{ total: 0, rows: [] });
	}
//		$('#templateName').val('');
//		var ue_result = UE.getEditor('result_ad');
//		ue_result.setContent('');
//		var ue_desc = UE.getEditor('desc_ad');
//		ue_desc.setContent('');
//		rt = null;
}

function addnode(){
	
	var node=$("#nodetree").tree('getSelected');
	
	if(node!=null&&node.parent!=0){
		$('#node_windows').window('open');
//		$('#modality').combobox('select','');
		
//		$('#nodename').textbox('textbox').attr('maxlength', 50);
	}
}

//去除空格
//function trim(str){   
//    return str.replace(/^(\s|\u00A0)+/,'').replace(/(\s|\u00A0)+$/,'');   
//}

/**
 * 添加根节点
 * @returns
 */
function saveRootNode(){
	    
	var node=$("#nodetree").tree('getSelected');
	
//	alert(node.modality)
		
	$('#nodeForm').form('submit', {
		url: window.localStorage.ctx+"/template/addTemplateNode?parent="+node.parent+"&modality="+node.modality+"&ispublic=1",
		onSubmit: function(){
			if($('#nodename').textbox('getText')==''){
				return false;
			}
		},
		success: function(data){
			closeNodeDialog();
			var res=validationDataAll(data);
			
			if(res.code == 0){
				$.messager.show({
		            title:'成功',
		            msg:"添加节点成功！",
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
				 $("#nodetree").tree("insert",{
					 after: node.target,
					 data : res.data
				 });
//				  $("#nodetree").tree("reload");
			} else {
				$.messager.show({
		            title:'失败',
		            msg:"添加节点失败！请重试!",
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
			}
		}
	});
	
}

/**
 * 关闭窗口
 * @returns
 */
function closeNodeDialog(){
	$('#nodename').textbox('setValue','');
	$('#node_windows').window('close');
}
/**
 * 关闭子节点窗口
 * @returns
 */
function closeChildNodeDialog(){
	$('#childnodename').textbox('setValue','');
	$('#childNode_windows').window('close');
}
/**
 * 打开添加子节点窗口
 * @returns
 */
function addChildNode(){
	    
	var node=$("#nodetree").tree('getSelected');
	if(node==null){
		$.messager.show({
            title:'提醒',
            msg:"请选择一个节点！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
		return;
		
	}else{
		$('#childNode_windows').window('open');
	}
	
}

/**
 * 添加子节点
 * @returns
 */
function saveChildNode(){
	     
	var node=$("#nodetree").tree('getSelected');
	
	console.log(node);
	$('#childNodeForm').form('submit', {
		url: window.localStorage.ctx+"/template/addTemplateNode?parent="+node.id+"&modality="+node.attributes.modality+"&ispublic=1",
		onSubmit: function(){
			if($('#childnodename').textbox('getText')==''){
				return false;
			}
		},
		success: function(data){
			closeChildNodeDialog();
			var res=validationDataAll(data);
			
			if(res.code == 0){
				$.messager.show({
		            title:'成功',
		            msg:"添加节点成功！",
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
				 $("#nodetree").tree("append",{
					 parent: node.target,
					 data : res.data
				 });
			} else {
				$.messager.show({
		            title:'失败',
		            msg:"添加节点失败！请重试!",
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
			}
		}
	});
						
}

function midifynode(){
	    
	var node=$("#nodetree").tree('getSelected');
	if(node){
		$("#nodetree").tree('beginEdit',node.target);
	}
	else{
		$.messager.show({
            title:'提醒',
            msg:"请选择一个节点！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
	
}

function submitMidifynode_(node){   
	
	if(trim(node.text)!=""){
		if(node.text.length<50){
			$.getJSON(window.localStorage.ctx+"/template/modifyTemplateNode?nodeid="+node.id+"&nodename="+node.text, function(json){
				var js=validationDataAll(json);
		    	if(js.code==0){
		    		$.messager.show({
		                title:'提醒',
		                msg:"修改成功！",
		                timeout:3000,
		                border:'thin',
		                showType:'slide'
		            });
		    	}
		    	else{
		    		$("#nodetree").tree('beginEdit',node.target);
		    		$.messager.show({
		                title:'错误',
		                msg:"保存失败，请重试，如果问题依然存在，请联系系统管理员！",
		                timeout:3000,
		                border:'thin',
		                showType:'slide'
		            });
		    	}
			});
		}else{
			$("#nodetree").tree('beginEdit',node.target);
			$.messager.show({
	            title:'提示',
	            msg:"节点名称过长！保存失败！",
	            timeout:3000,
	            border:'thin',
	            showType:'slide'
	        });
		}
	} else{
		$("#nodetree").tree('beginEdit',node.target);
		$.messager.show({
            title:'错误',
            msg:"节点名称不能为空！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
	
}

function deletenode(){

	var node=$("#nodetree").tree('getSelected');
	
	if(node&&node.parent!=0){

		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除选中的节点吗？',
			fn: function(r){

				if (r){
					 $.getJSON(window.localStorage.ctx+"/template/delTemplateNode?nodeid="+node.id, function(json){
						 	var js=validationData(json);
					    	if(js.code==0){
					    		$.messager.show({
					                title:'提醒',
					                msg:"删除成功！",
					                timeout:3000,
					                border:'thin',
					                showType:'slide'
					            });
					    		$("#nodetree").tree("remove",node.target);
					    		
					    		$('#templist').datagrid('loadData', { total: 0, rows: [] });
					    		$('#previewtemplate').html("");
					    		
					    	}
					    	else{
					    		$.messager.show({
					                title:'错误',
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
		
	}else{
		$.messager.show({
            title:'提醒',
            msg:"请选择一个子节点！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
}
//var rt ;
//function showTemplate(row){
//	sendAjax({
//		url:'/worklist/checkSession',
//		type:'post',
//		success:function(data){
//		}
//	})     
//	var ue_desc = UE.getEditor('desc_ad');
//	var ue_result = UE.getEditor('result_ad');
//	if(rt==null){
//		if($("#templateName").val()!="" ||trim(ue_result.getPlainTxt())!="" ||  trim(ue_desc.getPlainTxt()) !=""){
//			$.messager.confirm({
//				title: '提醒',
//				ok: '是',
//				cancel: '否',
//				msg: '是否保存新增？',
//				fn: function(r){
//				if(!r){
//					show(row);
//					return;
//					}else{
//						saveAdminTemplate();
//						return;
//					}
//				}
//			 });
//		}else{
//			show(row);
//			return;
//		}
//	}
//	if(rt.name == $("#templateName").val() 
//			&& (rt.resultcontent_html || '') == ue_result.getContent()
//			&& (rt.desccontent_html || '') == ue_desc.getContent() ){
//		show(row);
//	}else{
//		$.messager.confirm({
//			title: '提醒',
//			ok: '是',
//			cancel: '否',
//			msg: '是否保存修改？',
//			fn: function(r){
//			if(!r){
//				show(row);
//				   
//				}else{
//					modifyTemplate(rt);
//					//rt = null;
//				}
//			}
//		 });
//       }
//}

//function show(row){
//	sendAjax({
//		url:'/worklist/checkSession',
//		type:'post',
//		success:function(data){
//		}
//	})     
//	rt = row;
//	document.getElementById("templateUpdate").style.display = "";
//	document.getElementById("templateSave").style.display = "none";
//	document.getElementById("templateConsole").style.display = "none";
//	$("#templateName").val(row.name);
//	var ue_result = UE.getEditor('result_ad');
//	ue_result.setContent('');
//	var ue_desc = UE.getEditor('desc_ad');
//	ue_desc.setContent('');
//	if (row) {
//		$.getJSON("/template/findTemplateById?id="+ row.id,
//				function(json) {
//					var reportdata = validationData(json);
//					if(row.resultcontent_html){
//						ue_result = UE.getEditor('result_ad');
//						ue_result.setContent(row.resultcontent_html);
//					}
//					 if(row.desccontent_html){
//						 ue_desc = UE.getEditor('desc_ad');
//						 ue_desc.setContent(row.desccontent_html); 
//					 }
//					})
//	}
//}
/**
 * 修改
 * @returns
 */
//function modifyTemplate(r){
//	sendAjax({
//		url:'/worklist/checkSession',
//		type:'post',
//		success:function(data){
//		}
//	})     
//	var row=$("#templist").datagrid('getSelected');
//	var node=$("#nodetree").tree('getSelected');
//	var ue_desc = UE.getEditor('desc_ad');
//	var ue_result = UE.getEditor('result_ad');
//	if(r){
//		row = r;
//	}
//	if(row){
//		$('#tempForm_ad').form('submit', {
//			url: "/template/updateTemplate?id="+row.id,
//			onSubmit: function(){
//				if(!$("#templateName").val()){
//					$.messager.show({
//			            title:'提醒',
//			            msg:"请输入模板名称！",
//			            timeout:3000,
//			            showType:'slide'
//			        });
//					return false;
//				}
//				$('#desccontent_ad').val(ue_desc.getPlainTxt());
//				$('#resultcontent_ad').val(ue_result.getPlainTxt());
//			},
//			success: function(data){
//				var json=validationDataAll(data);
//				 if(json.code==0){
//					 //rt = null;
//		        	$.messager.show({
//		                title:'提醒',
//		                msg:"修改成功！",
//		                timeout:3000,
//		                showType:'slide'
//		            });
//		        	rt.name = $("#templateName").val() ;
//					rt.resultcontent_html = ue_result.getContent();
//					rt.desccontent_html  = ue_desc.getContent() ;
//		        	//getTemplate_(node.id);
//					show(row);
//		        	
//				 	} else {
//		        	$.messager.show({
//		                title:'提醒',
//		                msg:"修改失败请重试，如果问题依然存在，请联系系统管理员！",
//		                timeout:3000,
//		                showType:'slide'
//		            });
//		        }
//			}
//		});
//	} else {
//		$.messager.show({
//            title:'提醒',
//            msg:"请选择一个模板！",
//            timeout:3000,
//            showType:'slide'
//        });
//	}
//}

function generateCode(){
	var str = pinyinUtil.getFirstLetter($(this).val()).trim();
	$("#templateCode").textbox('setValue',str);
}

var ue_options_temp={
		toolbars:[['source', '|', 'undo','redo','|', 'superscript','subscript','|','removeformat','pasteplain','|',
            'insertorderedlist','|','selectall','cleardoc' ,
            '|','justifyleft','justifycenter','justifyright','justifyjustify','|','formula']]
		,lang:$.cookie("userLanguage").toLowerCase().replace('_','-')//"zh-cn"
    	,langPath:window.localStorage.ctx+"/js/ueditor/lang/"
    	,wordCount:false
    	,elementPathEnabled:false
    	,autoHeightEnabled: false
    	,iframeCssUrl:window.localStorage.ctx+"/js/ueditor/themes/iframe.css" //引入自身 css使编辑器兼容你网站css
	};

function showNewTemplate(){
	var node=$("#nodetree").tree('getSelected');
	if(node && node.parent != 0){
		$('#common_dialog').dialog(
				{
					title : $.i18n.prop('report.edittemplate'),
					width : 760,height : 670,
					top : 100,
					cache : false,modal : true,
					border: 'thin',
					href : window.localStorage.ctx+'/template/showTemplate',
					buttons:[{
						text:$.i18n.prop('save'),
						width:80,
						handler:function(){saveAdminTemplate();}
					},
					{
						text:$.i18n.prop('cancel'),
						width:80,
						handler:function(){$('#common_dialog').dialog('close');}
					}]
					,onLoad:function(){
						try {
							UE.delEditor('result_ad');
							UE.delEditor('desc_ad');
						} catch(e) {
							console.log(e);
						}
						UE.getEditor('result_ad',ue_options_temp);
						UE.getEditor('desc_ad',ue_options_temp);
					}
		});
	}
	/*$('#main_panel').panel({title:""});
	$('#temp_panel').panel({title:"报告模板->新建模板"});
	$('#main_panel').hide("fast");
	$('#temp_panel').show("fast");
	
	clearFiled_Template();*/
	
}

function showModifyTemplate(){
	var row=$("#templist").datagrid('getSelected');
	if(row!=null){
		/*$('#main_panel').panel({title:""});
		$('#temp_panel').panel({title:"报告模板->新建模板"});
		$('#main_panel').hide("fast");
		$('#temp_panel').show("fast");*/
		$('#common_dialog').dialog(
				{
					title : $.i18n.prop('report.edittemplate'),
					width : 760,height : 670,
					top : 100,
					cache : false,modal : true,
					border: 'thin',
					href : window.localStorage.ctx+'/template/showTemplate',
					buttons:[{
						text:$.i18n.prop('save'),
						width:80,
						handler:function(){saveAdminTemplate();}
					},
					{
						text:$.i18n.prop('cancel'),
						width:80,
						handler:function(){$('#common_dialog').dialog('close');}
					}]
					,onLoad:function(){
						try {
							UE.delEditor('result_ad');
						} catch(e) {
							console.log(e);
						}
						try {
							UE.delEditor('desc_ad');
						} catch(e) {
							console.log(e);
						}
						$('#templateName').textbox('setValue',row.name);
						$('#templateCode').textbox('setValue',row.code);
						$('#studymethod').textbox('setValue',row.studymethod||"");
						$('#templateid').val(row.id);
						$('#desccontent_ad').val("");
						$('#resultcontent_ad').val("");
						var ue_result = UE.getEditor('result_ad',ue_options_temp);
						if(row.resultcontent_html){
							ue_result.ready(function(){
								ue_result.setContent(row.resultcontent_html);
							});
						}
						
						var ue_desc = UE.getEditor('desc_ad',ue_options_temp);
						if(row.desccontent_html){
							ue_desc.ready(function(){
								ue_desc.setContent(row.desccontent_html);
							});
						}
	
					}
		});
		
		
	}
	else{
		$.messager.show({title:'提醒',msg:"请选择一个模板！",timeout:3000,border:'thin',showType:'slide'
      });
	}
	
	
	
}


/**
 *	添加子模板
 * @returns
 */
//function newTemplate(){
//	     
//	var row=$("#templist").datagrid('getSelected');
//	var ue_desc = UE.getEditor('desc_ad');
//	var ue_result = UE.getEditor('result_ad');
//	if(row!=null){
//		if(rt!=null){
//			if(rt.name == $("#templateName").val() 
//					&& (rt.resultcontent_html || '') == ue_result.getContent()
//					&& (rt.desccontent_html || '') == ue_desc.getContent() ){
//				clearTemplate();
//				return;
//			}else{
//				$.messager.confirm({
//					title: '提醒',
//					ok: '是',
//					cancel: '否',
//					msg: '是否保存修改？',
//					fn: function(r){
//					if(!r){
//						clearTemplate();
//						   return;
//						}else{
//							modifyTemplate(rt);
//							return;
//						}
//					}
//				 });
//		       }
//		}
//		
//	}else{
//		if($("#templateName").val()!="" ||trim(ue_result.getPlainTxt())!="" ||  trim(ue_desc.getPlainTxt()) !=""){
//			$.messager.confirm({
//				title: '提醒',
//				ok: '是',
//				cancel: '否',
//				msg: '是否保存新增？',
//				fn: function(r){
//				if(!r){
//					clearTemplate();
//					}else{
//						saveAdminTemplate();
//					}
//				}
//			 });
//		}
//	}
//}
///**
// * 清空模板
// */
//function clearTemplate(){
//	sendAjax({
//		url:'/worklist/checkSession',
//		type:'post',
//		success:function(data){
//		}
//	})     
//	document.getElementById("templateUpdate").style.display = "none";
//	document.getElementById("templateSave").style.display = "";
//	document.getElementById("templateConsole").style.display = "";
//	$('#templateName').val('');
//	var ue_result = UE.getEditor('result_ad');
//	ue_result.setContent('');
//	var ue_desc = UE.getEditor('desc_ad');
//	ue_desc.setContent('');
//	rt = null;
//}
/**
 * 取消添加模板
 * @param str
 * @returns
 */

function consoleAddTemplate(){
	
	clearFiled_Template();
	
	$('#temp_panel').panel({title:""});
	$('#main_panel').panel({title:"报告模板"});
	$('#main_panel').show("fast");
	$('#temp_panel').hide("fast");
	
}

function clearFiled_Template(){
	$('#templateName').textbox('setValue',"");
	$('#templateCode').textbox('setValue',"");
	$('#studymethod').textbox('setValue',"");
	var ue_result = UE.getEditor('result_ad');
	ue_result.ready(function(){
		ue_result.setContent('');
	});
	
	var ue_desc = UE.getEditor('desc_ad');
	ue_desc.ready(function(){
		ue_desc.setContent('');
	});
	$('#desccontent_ad').val("");
	$('#resultcontent_ad').val("");
	$('#templateid').val("");
}

function onSelect_templist(index,row){
	var content='<b>【所见】:</b><br/>' + row.desccontent;
		content += '<br/><b>【诊断】:</b><br/>' + row.resultcontent;
			
		$('#previewtemplate').html(content);
}

//去除空格
function trim(str){   
  return str.replace(/^(\s|\u00A0)+/,'').replace(/(\s|\u00A0)+$/,'');   
}


function saveAdminTemplate(){
	var node=$("#nodetree").tree('getSelected');
	if(node && node.parent != 0){
		$('#tempForm_ad').form('submit', {
			url: window.localStorage.ctx+"/template/saveTemplate?nodeid="+node.id+"&ispublic=1",
			onSubmit: function(){
				if(!trim($("#templateName").textbox('getValue'))){
					return false;
				}
				$("#ispublic").val(node.ispublic);
				
				var ue_desc = UE.getEditor('desc_ad');
				//alert(ue_desc.getPlainTxt())
				$('#desccontent_ad').val(ue_desc.getPlainTxt());
				
				var ue_result = UE.getEditor('result_ad');
				$('#resultcontent_ad').val(ue_result.getPlainTxt());
			},
			success: function(data){
			 	var json=validationDataAll(data);
		    	if(json.code==0){
		    		
//		    		$('#templateName').val('');
//		    		var ue_result = UE.getEditor('result_ad');
//		    		ue_result.setContent('');
//		    		var ue_desc = UE.getEditor('desc_ad');
//		    		ue_desc.setContent('');
		        	$.messager.show({
		                title:'成功',
		                msg:"保存成功！",
		                timeout:3000,
		                border:'thin',
		                showType:'slide'
		            });
		        	getTemplate_(node);
		        	$('#common_dialog').dialog('close');
		        }
		        else{
		        	$.messager.show({
		                title:'提醒',
		                msg:"保存失败请重试，如果问题依然存在，请联系系统管理员！",
		                timeout:3000,
		                border:'thin',
		                showType:'slide'
		            });
		        }
			}
		});
	}else{
		$.messager.show({
            title:'提示',
            msg:"请选择一个子节点！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
	
}


function deleteAdminTemplate(){
    
	var row=$("#templist").datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除选中的模板吗？',
			fn: function(r){
				if (r){
					 $.getJSON(window.localStorage.ctx+"/template/delTemplate?tempid="+row.id, function(data){
					    	//alert("JSON Data: " + json.patientname);
						 	var json=validationData(data);
					    	if(json.code== 0){
					    		$.messager.show({
					                title:'成功',
					                msg:"删除成功！",
					                timeout:3000,
					                border:'thin',
					                showType:'slide'
					            });
					    		var index=$("#templist").datagrid('getRowIndex',row);
					    		$("#templist").datagrid('deleteRow',index);
					    		
					    	}
					    	else{
					    		$.messager.show({
					                title:'错误',
					                msg:"删除失败，请重试，如果问题依然存在，请联系系统管理员！",
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
		$.messager.show({
            title:'提醒',
            msg:"请选择一个模板！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
	}
}
/**
 * 设备类型改变事件
 * @returns
 */
function modalityChange(newValue,oldValue){
	$('#progressDlg').dialog('open');
	//var modality = newValue;//$('#topModality').combobox('getValue');
//	 $("#nodetree").tree({  
//         url:'/template/getTemplateNodeByModality?modality='+ newValue,//请求路径，id为根节点的id  
//         onLoadSuccess:function(node,data){  
//              var tree = $(this);  
//              if(data){  
//                  $(data).each(function(index,d) {  
//                      if (this.state=='closed') {  
//                          tree.tree('collapseAll');  
//                      }  
//                  });
//                  $('#progressDlg').dialog('close');
//              } 
//         	}
//         }) 
     $.getJSON(window.localStorage.ctx+"/template/getTemplateNodeByModality?modality="+ newValue+"&ispublic=1", function(json){

     	$('#nodetree').tree('loadData',validationData(json));
     	$('#nodetree').tree('expandAll');
     	$('#progressDlg').dialog('close');	
     	
     });
     
     $('#templist').datagrid('loadData', { total: 0, rows: [] });
     $('#previewtemplate').html("");
     $('#new_linkbutton').linkbutton({disabled:true});
     $('#del_linkbutton').linkbutton({disabled:true});
     $('#modify_linkbutton').linkbutton({disabled:true});
     
}
//查询模板
//function serachTemplate(name){
//	
//	var keyWord=$("#key").val();
//	
//	if(keyWord && keyWord != ''){
//		
//		$('#templateInfo div').each(function () {
//			var tmpName = $(this).find('.l-btn-text').text();
//			if (tmpName.indexOf(keyWord) == -1) {
//				$(this).css('display', 'none');
//			} else {
//				$(this).css('display', 'block');
//			}
//		});
//	} else {
//		$('#templateInfo div').css('display', 'block');
//	}
//}  
//结构化字段查询
//function serachTemplateSelection(value){
//	alert(value);
//	var searchCon = $("#selectionTem_c").val();
//	var params = {
//			selection: searchCon
//		};
//	$('#templets').tree("options").queryParams = params;
//	$('#templets').tree('reload');
//}
//结构化模板查询
//function serachStructTemplate(keyword){
//	var searchCon = keyword;
//	var params = {
//			keyWord: searchCon
//		};
//	$('#structtemplets').tree("options").queryParams = params;
//	$('#structtemplets').tree('reload');
//}
////打开结构化模板
//function openTeample(templateId){
//		if(i>0){
//			checkCont(templateId);
//			return;
//		}
//		i++;
//	$('#main').panel('open').panel('refresh','/view/admin/createFieldmanage.jsp?templateId='+templateId);
//	
//}
///**
// * 判断修改方法
// */
//function checkCont(templateId){
//	var ue_result = UE.getEditor('ue_template');
//	if(temp.id == null ||temp.id == ''){
//		if($("#templetName1").val() != '' || $("#templetMapruleName1").val()!= '' || ue_result.getContent() !='' ){
//			$.messager.confirm('提示','是否保存新增？',function(r){
//			    if (r){
//			    	saveTemplet();
//			    }else{
//			    	$('#main').panel('open').panel('refresh','/view/admin/createFieldmanage.jsp?templateId='+templateId);
//			    	return;
//			    }
//			})
//		}else{
//			$('#main').panel('open').panel('refresh','/view/admin/createFieldmanage.jsp?templateId='+templateId);
//		}
//	}else{
//		if($("#templetName1").val()!= temp.name || $("#templetMapruleName1").val()!=temp.maprule || ue_result.getContent() != temp.content){
//			$.messager.confirm('提示','是否保存修改？',function(r){
//			    if (r){
//			    	updateTemplet();
//			    }else{
//			    	$('#main').panel('open').panel('refresh','/view/admin/createFieldmanage.jsp?templateId='+templateId);
//			    }
//			})
//		}else{
//			$('#main').panel('open').panel('refresh','/view/admin/createFieldmanage.jsp?templateId='+templateId);
//		}
//	}
//}

/**
 * 判断新增方法
 */
//function checkContNew(){
//	var ue_result = UE.getEditor('ue_template');
//	if(temp.id == null ||temp.id == ''){
//		if($("#templetName1").val() != '' || $("#templetMapruleName1").val()!= '' || ue_result.getContent() !='' ){
//			$.messager.confirm('提示','是否保存新增？',function(r){
//			    if (r){
//			    	saveTemplet();
//			    }else{
//			    	temp.id='';
//			    	$('#main').panel('open').panel('refresh','/view/admin/createFieldmanage.jsp?templateId=');
//			    	return;
//			    }
//			})
//		}else{
//			temp.id='';
//			$('#main').panel('open').panel('refresh','/view/admin/createFieldmanage.jsp?templateId=');
//			
//		}
//	}else{
//		if($("#templetName1").val()!= temp.name || $("#templetMapruleName1").val()!=temp.maprule || ue_result.getContent() != temp.content){
//			$.messager.confirm('提示','是否保存修改？',function(r){
//			    if (r){
//			    	updateTemplet();
//			    }else{
//			    	temp.id='';
//			    	$('#main').panel('open').panel('refresh','/view/admin/createFieldmanage.jsp?templateId=');
//			    }
//			})
//		}else{
//			temp.id='';
//			$('#main').panel('open').panel('refresh','/view/admin/createFieldmanage.jsp?templateId=');
//		}
//	}
//}

///**
// * 修改模板
// * @returns
// */
//function updateTemplet(){
//	sendAjax({
//		url:'/worklist/checkSession',
//		type:'post',
//		success:function(data){
//		}
//	})     
//	var data = new Object();
//	data.id = $('#templetContent').val();
//	temp.id = $('#templetContent').val();
//	
//	if(!$("#templetName1").val()){
//		$.messager.show({
//            title:'提示',
//            msg: '模板名称不能为空',
//            timeout:3000,
//            showType:'slide'
//        });
//		return;
//	}
//	data.name = $("#templetName1").val();
//	temp.name = $("#templetName1").val();
//	
//	if(!$("#templetMapruleName1").val()){
//		$.messager.show({
//            title:'提示',
//            msg: '模板映射规则不能为空',
//            timeout:3000,
//            showType:'slide'
//        });
//		return;
//	}
//	data.maprule = $("#templetMapruleName1").val();
//	temp.maprule = $("#templetMapruleName1").val();
//	var ue_result = UE.getEditor('ue_template');
//	if(ue_result.getContent() == null || ue_result.getContent() ==''){
//		$.messager.show({
//            title:'提示',
//            msg: '模板内容不能为空',
//            timeout:3000,
//            showType:'slide'
//        });
//		return;
//	}
//	data.content = ue_result.getContent();
//	temp.content = ue_result.getContent();
//	$.ajax({
//        'url' : '/templet/update',
//        'type' : 'POST',
//        'data' : data,
//        'dataType' : "JSON",
//        'success' : function (result) {
//            if(result.code == 0){
//            	$.messager.show({
//                    title:'提示',
//                    msg: '模板修改成功',
//                    timeout:3000,
//                    showType:'slide'
//                });
//            	openTeample(data.id);
//            }else{
//            	$.messager.show({
//                    title:'失败',
//                    msg: result.message,
//                    timeout:3000,
//                    showType:'slide'
//                });
//            }
//        }
//    })
//}


//查询所有单位
//function  findAllUnit_(){
//	$.ajax({
//        'url' : '/field/getUnit',
//        'type' : 'POST',
//        'data' : {},
//        'dataType' : "JSON",
//        'success' : function (result) {
//        	 var data = validationData(result);
//        	 $("#orgtype2").html("");
//        	 $("#orgtype2").append("<option value= 0>请选择</option>");
//        	for(var i = 0 ; i < result.length; i++){
//        		$("#orgtype2").append("<option value="+data[i].name_zh+">"+data[i].name_zh+"</option>")
//        	}
//        }
//	});
//}

function saveXslt(){
	var findingname=$('#findingname').textbox('getText');
	var belongreport=$('#belongreport').textbox('getText');
	var version=$('#viaversion').combobox('getValue');
	
	if(!findingname||!belongreport||!version)return;
	
	var filetxt=$('#xslt_text').filebox("getText");
	var filesr=$('#xslt_sr').filebox("getText");

		
	$.getJSON(window.localStorage.ctx+"/template/isExistXsltName?name="+ findingname+"&id="+$('#xsltid').val()+"&belongreport="+belongreport+"&version="+version, function(json){
		
		if(json){
			$.messager.show({
	            title:'提醒',
	            msg:'系统中已经存在'+findingname+'的病症所见样式文件！',
	            timeout:3000,
	            border:'thin',
	            showType:'slide'
	        });
		}
		else{
			daSaveXslt();
		}
    });
}
function daSaveXslt(){
	
	$('#editorXslt_fm').form('submit', {
	    url:window.localStorage.ctx+'/template/saveXslt',
	    success:function(result){
	    	var json=validationData(result);
	    	if(json.code == 0){
	    		closeEditXsltDialog();
	    		doSearchXsltTemplate($("#sb_xslt_lib").searchbox("getValue"));
            }else{
            	$.messager.show({
                    title:'失败',
                    msg: json.message,
                    timeout:3000,
                    border:'thin',
                    showType:'slide'
                });
            }
	    }
	});
}


function uploadXsltFiles(){

	
	var filetxt=$('#xslt_text').filebox("getText");
	var filesr=$('#xslt_sr').filebox("getText");
	
	if(!filetxt&&!filesr){
		$.messager.show({
            title:'提醒',
            msg:"请选取Xslt文件！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
		return;
	}
		
	if(filetxt&&filetxt.indexOf(".xslt")<0){
		$.messager.show({
            title:'提醒',
            msg:"请选择Xslt文件！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
		return;
	}
	if(filesr&&filesr.indexOf(".xslt")<0){
		$.messager.show({
            title:'提醒',
            msg:"请选择Xslt文件！",
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
		return;
	}
	
	$('#uploadfm').form('submit', {
	    url:window.localStorage.ctx+'/template/uploadXslt',
	    success:function(result){
	    	var json=validationData(result);
	    	if(json.code == 0){
	    		closeUploadDialog();
	    		doSearchXsltTemplate($("#sb_xslt_lib").searchbox("getValue"));
            }else{
            	$.messager.show({
                    title:'失败',
                    msg: json.message,
                    timeout:3000,
                    border:'thin',
                    showType:'slide'
                });
            }
	    }
	});
		

}

function operatecolumn_xsltdg(value, row, index){
	
	var str="";
	
	if($('#xslt_modify_btn')){
		str+='<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="openModifyXsltDialog('+index+');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;';
	}
	if($('#xslt_del_btn')){
		str+='<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="delXslt('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>&nbsp;';
	}
	if($('#uploadxslt')){
		str+='<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="openUploadXsltDialog('+index+');"><span class="l-btn-left"><span class="l-btn-text">上传</span></span></a>&nbsp;';
	}
//	if($('#xslt_download_btn')){
//		str+='<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="downloadXslt('+index+');"><span class="l-btn-left"><span class="l-btn-text">下载</span></span></a>&nbsp;';
//	}
	
	return str;//$('#dg_toolbar_div_user').html()?'<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="openModifyUserDialog('+index+');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;&nbsp;'+
			//'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteUser('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>':'';//"<a href='#' class='easyui-linkbutton' name='operate' onclick='openModifyUserDialog()' ><i class='icon icon-edit1'></i>修改</a>&nbsp;&nbsp;"+
	//"<a name='operate' href='#' class='easyui-linkbutton' onclick='deleteUser()' ><i class='icon icon-remove'></i>删除</a>";
}

function doSearchXsltTemplate(value){

	var params=null;
	if(value.code){
		var name_tmp=$('#sb_xslt_lib').searchbox("getValue");
		params = {
				name: name_tmp,
				displayname:name_tmp,
				viaversion:value.code
			};
	}
	else{
		var via=$('#viavsn_xslt_lib').combobox("getValue");
		params = {
				name: value,
				displayname:value,
				viaversion:via
			};
	}
	$('#xsldg').datagrid("options").queryParams = params;
	$('#xsldg').datagrid('reload');	
}

function delXslt(index){
	console.log(index)
	$('#xsldg').datagrid('selectRow',index);
	var xslt=$("#xsldg").datagrid("getSelected");
	
	if(xslt!=null){
		$.messager.confirm({
			title: '提醒',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '是否删除选择的样式文件？',
			fn: function(r){
				if(r){
					$.getJSON(window.localStorage.ctx+"/template/delXsltTemplate?id="+xslt.id, function(data){
					 	var json=validationData(data);
					 	if(json.code==0){
					 		var index=$("#xsldg").datagrid('getRowIndex',xslt);
				    		$("#xsldg").datagrid('deleteRow',index);
					 	}
					 	else{
					 		$.messager.show({
				                title:'错误',
				                msg: "删除失败请重试，如果问题依然存在，请联系系统管理员！错误信息："+json.message,
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
	
}

function downloadXslt(index){
	console.log(index)
	$('#xsldg').datagrid('selectRow',index);
	var xslt=$("#xsldg").datagrid("getSelected");
	if(xslt){
		$('<form method="post" target="" role="form" action="'+window.localStorage.ctx+'/template/downloadXslt?id='+xslt.id+'" hidden="hidden"></form>').appendTo('body').submit().remove();
	}
}

function openModifyXsltDialog(index){
	
	console.log(index)
	$('#xsldg').datagrid('selectRow',index);
	var xslt=$("#xsldg").datagrid("getSelected");
	if(xslt){
		$('#findingname').textbox("setValue",xslt.name);
		$('#displayname').textbox("setValue",xslt.displayname);
		$('#belongreport').textbox("setValue",xslt.belongreport);
		$('#displayvalue').textbox("setValue",xslt.displayvalue);
		$('#source').textbox("setValue",xslt.source);
		console.log(xslt.viaversion)
		$('#viaversion').combobox('setValue',xslt.viaversion);
		$('#xsltid').val(xslt.id);
		$('#editxsltDlg').dialog('open');
	}
	else{
		$.messager.show({
	          title:'提醒',
	          msg:"请选择模板！",
	          timeout:3000,
	          border:'thin',
	          showType:'slide'
	      });
	}
}

function openUploadXsltDialog(index){
	console.log(index)
	$('#xsldg').datagrid('selectRow',index);
	var xslt=$("#xsldg").datagrid("getSelected");
	if(xslt){
		$('#xsltid1').val(xslt.id);
		$('#uploadXsltDlg').dialog('open');
	}
	else{
		$.messager.show({
          title:'提醒',
          msg:"请选择模板！",
          timeout:3000,
          border:'thin',
          showType:'slide'
      });
	}
}


function closeEditXsltDialog(){
	$('#xsltid').val("");
	$('#findingname').textbox("setValue","");
	$('#displayname').textbox("setValue","");
	$('#belongreport').textbox("setValue","");
	$('#displayvalue').textbox("setValue","");
	$('#source').textbox("setValue","");
	$('#viaversion').textbox("setValue","");
	$('#editxsltDlg').dialog('close');
}

function closeUploadDialog(){
	$('#xsltid1').val('');
	$('#xslt_text').textbox("setValue","");
	$('#xslt_sr').textbox("setValue","");
	$('#uploadXsltDlg').dialog('close');
}


/* 中山六使用
* 将_txt转为_html
*/
function generateHtml(){
	getJSON(window.localStorage.ctx+"/template/generateHtml",null, function(json){
		var data = validationDataAll(json);
		if(data.code==0){
			$.messager.show({
		        title:'成功',
		        msg:'更新当前数据',
		        timeout:3000,
		        border:'thin',
		        showType:'slide'
		    });
		}
	})
}
