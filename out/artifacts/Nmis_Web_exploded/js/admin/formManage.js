$(document).ready(function(){

});

//withModel:
//true:先选择模型，再新建表单
//false:直接新建表单
function newFormTmp(withModel){
	healtaEditor.setEnabled();
	$('#savebtn').linkbutton('enable');
	$('#saveasbtn').linkbutton('enable');
	if(healtaEditor.getContent()!=""){
		$.messager.confirm({
			title : $.i18n.prop('confirm'),
			border:'thin',
			msg : $.i18n.prop('admin.giveupsavetemplate'),
			fn : function(r) {
				if (r) {
					if(withModel){
						openModelDialog();
					} else{
						$('#templet_id').val("");
						$('#model_id').val("");
						$('#model_name').val("");
						$('#srtemplatename_opened').html("新建表单");
	//					$('#templet_name').val("");
	//					$('#templet_maprule').val("");
						healtaEditor.setContent("");
					}
				}
			}
		});
	} else{
		if(withModel){
			openModelDialog();
		} else{
			$('#srtemplatename_opened').html('新建表单');
		}
	}
}

function openModelDialog(){
	$('#common_dialog').dialog({
		title : '选择模型',
		width : 490,height : 400,border: 'thin',
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		href : window.localStorage.ctx+'/research/openModelDialog',
		buttons:[{
			text: $.i18n.prop('ok'),
			width:80,
			handler:function(){
				var row=$('#newform_models_dg').datagrid('getSelected');
				if(row!=null){
					console.log(row)
					$('#templet_id').val("");
					$('#model_id').val(row.id);
					$('#model_name').val(row.name);
					$('#srtemplatename_opened').html("新建表单");
					healtaEditor.setContent("");
					$('#common_dialog').dialog('close');
				} else{
					_message('请选择一个模型！');
				}
			}
		},{
			text: $.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

function on_select_domain(index,row){
	getJSON(window.localStorage.ctx+"/srtemplate/getModels",
		{
			domainid:row.id
		},
		function(json){
			$('#newform_models_dg').datagrid('loadData',json);
    });
}

function openSaveFormDialog(){
	if(!healtaEditor.hasContents()&&!/textareaComponent/.test(healtaEditor.body.innerHTML)){
		$.messager.show({
            title: $.i18n.prop('alert'),
            msg: '表单内容不能为空',
            timeout:3000,
            border :'thin',
            showType:'slide'
        });
		return;
	}
	
	$('#common_dialog').dialog({
		title : '保存表单',
		width : 490,height : 165,border: 'thin',
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		href : window.localStorage.ctx+'/research/openSaveFormDialog?id='+$('#templet_id').val()+'&modelname='+$('#model_name').val(),
		buttons:[{
			text: $.i18n.prop('save'),
			width:80,
			handler:function(){saveFrom(false);}
		},{
			text: $.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

function openSaveAsFormDialog(){
	if(!healtaEditor.hasContents()&&!/textareaComponent/.test(healtaEditor.body.innerHTML)){
		$.messager.show({
            title:$.i18n.prop('alert'),
            msg: '表单内容不能为空',
            timeout:3000,
            border :'thin',
            showType:'slide'
        });
		return;
	}
	
	$('#common_dialog').dialog({
		title : '表单另存为',
		width : 490,height : 165,border: 'thin',
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		href : window.localStorage.ctx+'/research/openSaveFormDialog?modelname='+$('#model_name').val(),
		buttons:[{
			text: $.i18n.prop('save'),
			width:80,
			handler:function(){saveFrom(true);}
		},{
			text: $.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

/**
 * 保存结构化模板
 * @returns
 */
function saveFrom(isSaveAs){
	
	var data = new Object();
	data.name = $("#srtemplatename_save").val();
	if(!$("#srtemplatename_save").val()){
		return;
	}
	data.model_id=$("#model_id").val();
	data.model_name=$("#model_name").val();
	
	var content=healtaEditor.getContent();
	if(!content){//ueditor 会过滤textarea标签，如果模板中只有textarea组件，则通过editor.body.innerHTML 获得content
		content=healtaEditor.body.innerHTML;
	}
	$('#temp_div').html(content);
	var map=new Object();
	var exist_repeatcode=false;
	var repeatcode;
	var arr=new Array(0);
	$('#temp_div').find('img,input[type!=button],select,textarea').each(function(index,obj){//select,
		if(obj.tagName=='IMG'){
			$(obj).attr('alt',"img");
		} else if(obj.getAttribute('name')==pluginHandle.component_name_number){
			var id=$(obj).attr('id'),newid=uuid();
			var tmp = new Object();
			tmp.id=id;
			tmp.newid=newid;
			arr.push(tmp);
			$(obj).attr('id',newid);
			$(obj).attr('objuid',uuid());
		} else{
			$(obj).attr('id',uuid());
			$(obj).attr('objuid',uuid());
		}
		
		var code=$(obj).attr('code');
		if(code!=null){
			if(map[code]==null){
				map[code]=1;
			} else{
				exist_repeatcode=true;
				repeatcode=code;
			}
		}
	});

	if(exist_repeatcode){
		_message('表单模板中存在相同编码的组件！重复的编码：'+repeatcode);
		return;
	}
	
	var content_new=$('#temp_div').html();
	$('#temp_div').empty();
	//因为所有组件的id重新生成，需要将表达式中的id替换为新的id
	for(var i=0,len=arr.length;i<len;i++){
		content_new=content_new.replace(new RegExp("{"+arr[i].id+"}","g"),"{"+arr[i].newid+"}");
	}
	healtaEditor.setContent(content_new);
	
	data.formcontent = content_new;
	
//	if(datamodel_obj){
//		data.datamodel=JSON.stringify(datamodel_obj[0]);
//	}
	if(!$('#templet_id').val()||isSaveAs){
		$.post(window.localStorage.ctx+"/research/newForm",data,function(result){
			if(result.id>= 0){
	        	$('#templet_id').val(result.id);
	        	$('#srtemplatename_opened').html(data.name);
	        	$('#common_dialog').dialog('close');
	        	$.messager.show({
	                title: $.i18n.prop('tips'),
	                msg: $.i18n.prop('savesuccess'),
	                timeout:3000,border:'thin',
	                showType:'slide'
	            });
	
	        }else{
	        	$.messager.show({
	                title: $.i18n.prop('error'),
	                msg: result.message,
	                timeout:3000,border:'thin',
	                showType:'slide'
	            });
	        }
	    });
	}
	else{
		data.id=$('#templet_id').val();
		$.post(window.localStorage.ctx+"/research/updateForm",data,function(result){
			if(result.code == 0){
				$('#srtemplatename_opened').html(data.name);
            	$.messager.show({
                    title: $.i18n.prop('tips'),
                    msg: $.i18n.prop('savesuccess'),
                    timeout:3000,border:'thin',
                    showType:'slide'
                });
            	//closeSaveSRtempDialog();
            	$('#common_dialog').dialog('close');
            }else{
            	$.messager.show({
                    title:$.i18n.prop('error'),
                    msg: result.message,
                    timeout:3000,border:'thin',
                    showType:'slide'
                });
            }
		});
		
	}
}

function openFormListDlg(){
	$('#common_dialog').dialog(
			{
				title : "打开表单",
				width : 575,height : 535,
				border: 'thin',
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				href : window.localStorage.ctx+'/research/openFormList',
				buttons:[{
					text:$.i18n.prop('admin.open'),
					width:80,
					handler:function(){openResearchForm(null,null);}
				},{
					text: $.i18n.prop('cancel'),
					width:80,
					handler:function(){$('#common_dialog').dialog('close');}
				}]
	});
}

function openResearchForm(index,row){
	if(row==null){
		row=$('#tmp_dg').datagrid('getSelected');
	}
	if(!row){
		_message("请选择一个表单！");
		return ;
	}
	healtaEditor.setEnabled();
	$('#savebtn').linkbutton('enable');
	$('#saveasbtn').linkbutton('enable');
	$('#common_dialog').dialog('close');
	$.getJSON(window.localStorage.ctx+"/research/getResearchFormById?id="+row.id, function(json){
		if(healtaEditor.getContent()!=""){
			$.messager.confirm({
				title : $.i18n.prop('confirm'),
				border:'thin',
				msg : $.i18n.prop('admin.giveupsavetemplate'),
				fn : function(r) {
					if (r) {
						$('#templet_id').val(row.id);
						$('#srtemplatename_opened').html(row.name);
						$('#model_id').val(row.model_id);
						$('#model_name').val(row.model_name);
						console.log(row)
						healtaEditor.setContent(initImgSrc(json.formcontent));
						setTimeout(function (){
							healtaEditor.setContent("",true);
							showDataModel(json.formcontent);
			            },500);
					}
				}
			});
		}
		else{
			$('#templet_id').val(row.id);
			$('#srtemplatename_opened').html(row.name);
			$('#model_id').val(row.model_id);
			$('#model_name').val(row.model_name);
			console.log(row)
			healtaEditor.setContent(initImgSrc(json.formcontent));
			setTimeout(function (){
				healtaEditor.setContent("",true);
				showDataModel(json.formcontent);
	        },500);
		}
    });
}

function delResearchFormAndComponent(){

	var rows=$("#resFandcompdg").datagrid("getSelections");

	var resepids="";
	var compids="";

	$.each( rows, function( index, row ){

		if(row.groupid=="researchForm"){
			resepids+=row.id+",";
		} else if(row.groupid=="srcomponent"){
			compids+=row.id+",";
		}
	});

	// console.log(resepids+"--"+compids);

	if(resepids==""&&compids==""){
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg :"请选择结构化表单或者组件？",
			timeout : 3000,showType : 'slide'
		});
	}
	else{
		$.messager.confirm({
			title : $.i18n.prop('confirm'),
			border: 'thin',
			msg : $.i18n.prop('admin.confirmdeletetemporcomponent'),
			fn : function(r) {
				if (r) {
					$.getJSON(window.localStorage.ctx+"/research/delFormAndComponent"
						,{
							'resepids':resepids,
							'compids':compids
						}
						,function(json){
							var result=validationData(json);
							$.each( rows, function( index, row ){
								$("#resFandcompdg").datagrid("deleteRow",$("#resFandcompdg").datagrid("getRowIndex",row));
							});
						});
				}
			}
		});
	}
}

function exportResearchForm(){

	var rows=$('#resFandcompdg').datagrid('getSelections');
	//console.log(rows);
	if(rows&&rows.length>0){
		var rids="",cids="",sids="";
		$(rows).each(function(index,row){
			if(row.groupid=="researchForm"){
				rids+=row.id+",";
			} else{
				cids+=row.id+",";
			}
		});

		$('<form method="post" target="" role="form" action="'+window.localStorage.ctx+'/research/exportFormTemplate?rids='+rids+'&cids='+cids+'" hidden="hidden"></form>').appendTo('body').submit().remove();
	}
	else{
		$.messager.show({
			title:$.i18n.prop('alert'),msg: $.i18n.prop('admin.choosetemplate'),timeout:3000,border:'thin',showType:'slide'
		});
	}
}

function importResearchForm(newValue,oldValue){
	if(newValue!=""){
		$('#importfm1').form('submit', {
			url:window.localStorage.ctx+'/research/importFormTemplate',
			success:function(result){
				var json=validationData(result);
				if(json.code == 0){
					$('#importresFormfilebox').textbox('setValue','');
					doSearchResearchFormAndComponent($('#formsearchbox').searchbox("getValue"));
					$.messager.show({
						title: $.i18n.prop('tips'),msg: $.i18n.prop('admin.uploadsuccess'),timeout:3000,border:'thin',showType:'slide'
					});
				}else{
					$.messager.show({
						title: $.i18n.prop('error'),msg:json.message,timeout:3000,border:'thin',showType:'slide'
					});
				}
			}
		});
	}
}

function doSearchResearchFormAndComponent(value){
	//if(value!=""){

	$.getJSON(window.localStorage.ctx+"/research/findSRTemplateAndComponent?name="+value, function(json){
		$("#resFandcompdg").datagrid("loadData",validationData(json));
	});
	//}
}

