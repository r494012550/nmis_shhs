$(document).ready(function(){
})

function doSearchFindingXslt_tmp(value){
	var params=null;
	if(value.code){
		var name_tmp=$('#sb_xslt').searchbox("getValue");
		params = {
				name: name_tmp,
				displayname:name_tmp,
				viaversion:value.code
			};
	}
	else{
		var via=$('#viavsn_xslt').combobox("getValue");
		params = {
				name: value,
				displayname:value,
				viaversion:via
			};
	}
	
	$('#xsltdg_sr').datagrid({queryParams: params});
}

function doSearchFindingXslt_sec(value){
	var params=null;
	if(value.code){
		var name_tmp=$('#sb_xslt_sec').searchbox("getValue");
		params = {
				name: name_tmp,
				displayname:name_tmp,
				viaversion:value.code
			};
	}
	else{
		var via=$('#viavsn_xslt_sec').combobox("getValue");
		params = {
				name: value,
				displayname:value,
				viaversion:via
			};
	}
	
	$('#xsltdg_sr_section').datagrid({queryParams: params});
}

//function doClinicalcodeSearch(value){
//	//alert(value);
//		var params = {
//				meaning: value
//			};
//		$('#clinicalcodes_dg').datagrid("options").queryParams = params;
//		$('#clinicalcodes_dg').datagrid('reload');
//}
//function closeSaveSRtempDialog(){
//	clearFieldValue_SaveSRtempDlg();
//	$('#saveSRTempDialog').dialog('close');
//	
//}
//
//function clearFieldValue_SaveSRtempDlg(){
//	$('#templetName1').textbox('setValue',"");
//	$('#templetMapruleName1').textbox('setValue',"");
//}

//var isSaveAs=false;

function openSaveSRtempDialog(){
	if(!healtaEditor.hasContents()&&!/textareaComponent/.test(healtaEditor.body.innerHTML)){
		$.messager.show({
            title: $.i18n.prop('alert'),
            msg: $.i18n.prop('admin.templatecontentcannotempty'),
            timeout:3000,
            border :'thin',
            showType:'slide'
        });
		return;
	}
	
	$('#common_dialog').dialog({
		title : $.i18n.prop('admin.savetemplate'),
		width : 490,height : 650,border: 'thin',
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		href : window.localStorage.ctx+'/srtemplate/openSaveSRTemplateDialog?id='+$('#templet_id').val(),
		buttons:[{
			text: $.i18n.prop('save'),
			width:80,
			handler:function(){saveSRTemplet(false);}
		},{
			text: $.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

function openSaveSRsectionDialog(){

	if(!healtaEditor_section.hasContents()&&!/textareaComponent/.test(healtaEditor_section.body.innerHTML)){
		$.messager.show({
            title: $.i18n.prop('alert'),
            msg: '章节内容不能为空',
            timeout:3000,
            border :'thin',
            showType:'slide'
        });
		return;
	}
	
	$('#common_dialog').dialog({
		title : '保存章节',
		width : 450,height : 320,border: 'thin',
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		href : window.localStorage.ctx+'/srtemplate/openSaveSRSectionDialog?id='+$('#section_id').val(),
		buttons:[{
			text: $.i18n.prop('save'),
			width:80,
			handler:function(){saveSRSection(false);}
		},{
			text: $.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
}

function openSaveAsSRtempDialog(){
	if(!healtaEditor.hasContents()&&!/textareaComponent/.test(healtaEditor.body.innerHTML)){
		$.messager.show({
            title:$.i18n.prop('alert'),
            msg: $.i18n.prop('admin.templatecontentcannotempty'),
            timeout:3000,
            border :'thin',
            showType:'slide'
        });
		return;
	}
	
	$('#common_dialog').dialog({
		title : $.i18n.prop('admin.templatesaveas'),
		width : 490,height : 650,border: 'thin',
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		href : window.localStorage.ctx+'/srtemplate/openSaveSRTemplateDialog',
		buttons:[{
			text: $.i18n.prop('save'),
			width:80,
			handler:function(){saveSRTemplet(true);}
		},{
			text: $.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
	
}

function openSaveAsSRsectopnDialog(){
	
	if(!healtaEditor_section.hasContents()&&!/textareaComponent/.test(healtaEditor_section.body.innerHTML)){
		$.messager.show({
            title:$.i18n.prop('alert'),
            msg: '章节内容不能为空',
            timeout:3000,border :'thin', showType:'slide'
        });
		return;
	}
	
	$('#common_dialog').dialog({
		title : '章节另存为',
		width : 450,height : 320,border: 'thin',
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		href : window.localStorage.ctx+'/srtemplate/openSaveSRSectionDialog',
		buttons:[{
			text: $.i18n.prop('save'),
			width:80,
			handler:function(){saveSRSection(true);}
		},{
			text: $.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#common_dialog').dialog('close');}
		}]
	});
	
}

//var b;
//打开国际标准码窗口
function openStandardCodWindow_(open_id){

	$("#open_id").val(open_id);
	$("#standardCodeWin").dialog("open");

}

function openEditFormulaDialog(open_id){
	$("#open_id").val(open_id);
	$('#formula_dialog').dialog({
		title : '编辑表达式',
		width : 800,height : 800,border: 'thin',
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		href : window.localStorage.ctx+'/srtemplate/openEditFromulaDialog',
		buttons:[{
			text: $.i18n.prop('ok'),
			width:80,
			handler:function(){
				var formulaEditor = UE.getEditor('ue_formula');
				var content=formulaEditor.getContent();
				$(formulaEditor.body).find("input[formula='formula']").each(function(index,e){
					$(e).replaceWith($(e).val());
				});
				var formulastr=tmp=trim(formulaEditor.getContentTxt());
				var match = tmp.match(/{.+?}/g);
				if(match){
					for(var i=0,len=match.length;i<len;i++){
						tmp=tmp.replace(new RegExp(match[i],"g"),1);
					}
				}
				console.log(tmp);
				try{
					var n=eval(tmp);
					console.log(n);
					console.log(typeof  n);
					if(typeof n != 'number' || isNaN(n)){//非数值
						_message("表达式语法错误！");
						formulaEditor.setContent(content);
						return false;
					}
				} catch(e){
					console.log(e);
					_message("表达式语法错误！错误信息："+e);
					formulaEditor.setContent(content);
					return false;
				}
				
				$('#'+open_id).textbox('setValue',formulastr);
				$('#formula_dialog').dialog('close');
			}
		},{
			text: $.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#formula_dialog').dialog('close');}
		}],
		onLoad:function(){
			//$('#formula_id').textbox('setValue',trim($('#'+open_id).val()));
			var oNode = UE.plugins['numberinput'].editdom;
			if(oNode){
				var body=$(oNode).parents("body.view");
				if(body[0]){
					//console.log($(body[0]).html());
					var clone=$(body[0]).clone();
					clone.find("input[type!='number'],select,textarea,img,#"+oNode.id).remove();
					clone.find("input[type='number']").removeAttr('onchange').attr('onclick','addToFormula(this)');
					$('#formula_content').panel({
						content:clone.html()
					});
					clone.remove();
				}
			}
			
			UE.delEditor('ue_formula');
			var formulaEditor = UE.getEditor('ue_formula',{  
	           //这里可以选择自己需要的工具按钮名称,此处仅选择如下五个  [ 'undo', 'redo', '|', 'removeformat', '|','source']
	           toolbars:[['undo', 'redo', '|', 'removeformat', '|','source']]
	            ,lang:$.cookie("userLanguage").toLowerCase().replace('_','-')//"zh-cn"
	        	,langPath:"${ctx}/js/ueditor/lang/"
	            ,wordCount:false
	            ,elementPathEnabled:false
	            ,autoHeightEnabled: false
	        });
			
			formulaEditor.ready(function() {
				var formula=$('#'+open_id).val();
				if(formula){
					var match = formula.match(/{.+?}/g);
					if(match){
						for(var i=0,len=match.length;i<len;i++){
							var id=match[i].substring(1,match[i].length-1);
							console.log(match[i]);
							formula=formula.replace(new RegExp(match[i],"g"),'<input type="button" tmpid="'+id+'" formula="formula" style="border:1px;background: #FFFFFF;" value="{'+id+'}"/>');
						}
						this.setContent(formula);
					}
				}
				$('#north_formula .edui-editor-toolbarbox').hide();
			});
			
			formulaEditor.addListener( 'contentChange', function() {
				var editor = UE.getEditor('ue_formula');
				$('#formula_content').find("input[type='number']").css('background-color','white');
				$(editor.getContent()).find("input[formula='formula']").each(function(index,e){
					$('#'+e.getAttribute('tmpid')).css('background-color','green');
				});
			});
		},
		onBeforeClose:function(){
			$('#formula_content').panel('clear');
			return true;
		}
	});
	
}

function addToFormula(obj){
	//$('#formula_id').textbox('setValue','{'+$(obj).attr('id')+'}');
	var formulaEditor = UE.getEditor('ue_formula');
	formulaEditor.execCommand('insertHtml', '<input type="button" tmpid="'+$(obj).attr('id')+'" formula="formula" style="border:1px;background: #FFFFFF;" value="{'+$(obj).attr('id')+'}"/>');
	
	$(obj).css('background-color','green');
}

function openSectionsDialog(open_id){
	$("#open_id").val(open_id);
	var sectionid=$("#section_id").val();
	var tab = $('#srtemplate_tab').tabs('getSelected');
	var index = $('#srtemplate_tab').tabs('getTabIndex',tab);
	if(index==0){
		sectionid="";
	}
	$('#select_dialog').dialog(
			{
				title : '选择章节',
				width : 575,height : 500,
				border: 'thin',
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				href : window.localStorage.ctx+'/srtemplate/gotoSRSectionList?sectionid='+sectionid,
				buttons:[{
					text:$.i18n.prop('admin.open'),
					width:80,
					handler:function(){assignSectionToOption(null,null);}
				},{
					text: $.i18n.prop('cancel'),
					width:80,
					handler:function(){$('#select_dialog').dialog('close');}
				}],
				onLoad:function(){
					$('#section_dg').datagrid({onDblClickRow:assignSectionToOption})
				}
	});
}

function assignSectionToOption(index,row){
	
	if(!row){
		row=$('#section_dg').datagrid('getSelected');
	}
	var open_id=$('#open_id').val();
	var rowo=$('#dg_select').datagrid('getSelected');
	$('#dg_select').datagrid('updateRow',{
		index: open_id,
		row: {
			sectionuid: row.uid,
			sectionname: row.name
		}
	});
	$('#dg_select').datagrid('beginEdit', open_id);
	$('#select_dialog').dialog('close');
}


function searchClinialCode(value){
	if(value!=""){
		
		$.getJSON(window.localStorage.ctx+"/srtemplate/findClinicalCode?meaning="+value, function(json){
			$("#conDataGrid_c").datagrid("loadData",validationData(json));
	    });
	}
}

function searchSRTemp(value){
	$('#tmp_dg').datagrid({
		queryParams: {
			name: value
		}
	});
}

function searchSRSection(value){	
	$('#section_dg').datagrid({
		queryParams: {
			name: value
		}
	});
}

function openSRTemp(index,row){
	if(row==null){
		row=$('#tmp_dg').datagrid('getSelected');
	}
	if(!row){
		_message("请选择一个模板！");
		return ;
	}
	healtaEditor.setEnabled();
	$('#savebtn').linkbutton('enable');
	$('#saveasbtn').linkbutton('enable');
	$('#common_dialog').dialog('close');
	$.getJSON(window.localStorage.ctx+"/srtemplate/getSRTemplateById?id="+row.id, function(json){
		if(healtaEditor.getContent()!=""){
			$.messager.confirm({
				title : $.i18n.prop('confirm'),
				border:'thin',
				msg : $.i18n.prop('admin.giveupsavetemplate'),
				fn : function(r) {
					if (r) {
						$('#templet_id').val(row.id);
						$('#srtemplatename_opened').html(row.name);
						console.log(row)
						healtaEditor.setContent(initImgSrc(json.templatecontent));
						setTimeout(function (){
							healtaEditor.setContent("",true);
							showDataModel(json.templatecontent);
			            },500);
					}
				}
			});
		}
		else{
			$('#templet_id').val(row.id);
			$('#srtemplatename_opened').html(row.name);
			console.log(row)
			healtaEditor.setContent(initImgSrc(json.templatecontent));
			setTimeout(function (){
				healtaEditor.setContent("",true);
				showDataModel(json.templatecontent);
	        },500);
		}
    });
}

var datamodel_obj;

function showDataModel(content){
	datamodel_obj=null;
	if($('#open_only_temp')[0] && $('#open_only_temp').radiobutton('options').checked)
		return;
	var select ="input:not(.textbox-text,.textbox-value,[gtype='snapshot'],[gtype='finding']),select,textarea,article[name='srsection']";
	datamodel_obj=getDataModel(content,select);
	$('#srtemp_datamodels').treegrid('loadData',datamodel_obj);
}

function getDataModel(content,select){
	var arr=[];
	var map=new Object();
	$("<div>"+content+"</div>").find(select).each(function(index,e){  
		var name=$(e).attr("name");
		if(!name)return;//跳过数字输入框的单位input
		var obj={};
		obj.id=uuid();
		var parent_srsection=$(e).parents("article[name='srsection']:first");
		if(name==pluginHandle.component_name_srsection){
			obj.name=name;
			obj.display=$(e).attr("title");
			obj.code=$(e).attr("code");
			obj.type=name;
			obj.uid=$(e).attr("uid");
			obj.oid=$(e).attr("id");
			map[$(e).attr("id")]=obj;
		} else if(name==pluginHandle.component_name_clinicalcode){
			obj.name=$(e).attr("gvalue");
			obj.display=$(e).attr("gvalue");
			obj.code=$(e).attr("code");
			obj.type=name;
			obj.uid=$(e).attr("uid");
			obj.oid=$(e).attr("id");
		} else{
			obj.name=$(e).attr("title");
			obj.display=$(e).attr("title");
			obj.code=$(e).attr("code");
			obj.type=name;
			obj.uid=$(e).attr("uid");
			obj.oid=$(e).attr("id");
			
			if(name==pluginHandle.component_name_select){
				var options=e.options,sectionuids="";
				for(var i=0,l=options.length;i<l;i++){
					var sectionuid= options[i].getAttribute('sectionuid')
					if(sectionuid){
						sectionuids+=sectionuid+",";
					}
				}
				console.log(sectionuids);
				if(sectionuids){
					$('#progress_dlg').dialog('open');
					getJSON(window.localStorage.ctx+"/srtemplate/getSRSectionByUids",{sectionuids:sectionuids}, function(json){
						$('#progress_dlg').dialog('close');
						for(var i=0,l=json.length;i<l;i++){
							console.log(json[i].name)
							obj.children=getDataModel(json[i].sectioncontent,select);
							$('#srtemp_datamodels').treegrid('loadData',datamodel_obj);
						}
					});
				}
			}
			
		}
		
		if(parent_srsection[0]){
			var section=map[parent_srsection.attr("id")];
			//console.log(section);
			if(section){
				var children_arr=section.children;
				if(!children_arr){
					children_arr=[];
					section.children=children_arr;
				}
				children_arr.push(obj);
			} else{
				arr.push(obj);
			}
		} else{
			arr.push(obj);
		}
	});
//	console.log(arr);
	return arr;
}


function selectDataModel(row){
	console.log(row)
	$(healtaEditor.body).find("#"+row.oid).each(function(index,e){
//		console.log(e)
//		$(e).focus();
		var range=healtaEditor.selection.getRange();
    	range.selectNodeContents(e);
    	range.select(true);
	});
}


function openSRSection(index,row){
	
	if(row==null){
		row=$('#section_dg').datagrid('getSelected');
	}
	
	healtaEditor_section.setEnabled();
	$('#savebtn_section').linkbutton('enable');
	$('#saveasbtn_section').linkbutton('enable');
	$('#common_dialog').dialog('close');
	$.getJSON(window.localStorage.ctx+"/srtemplate/getSRSectionById?id="+row.id, function(json){
		if(healtaEditor_section.getContent()!=""){
			$.messager.confirm({
				title : $.i18n.prop('confirm'),
				border:'thin',
				msg : '是否放弃保存当前打开的章节？',
				fn : function(r) {
					if (r) {
						$('#section_id').val(row.id);
						$('#srsectionname_opened').html(row.name);
						console.log(row)
						healtaEditor_section.setContent(initImgSrc(json.sectioncontent));
						setTimeout(function (){
							healtaEditor_section.setContent("",true);
			            },1000);
					}
				}
			});
		}
		else{
			$('#section_id').val(row.id);
			$('#srsectionname_opened').html(row.name);
			console.log(row)
			healtaEditor_section.setContent(initImgSrc(json.sectioncontent));
			setTimeout(function (){
				healtaEditor_section.setContent("",true);
	        },1000);
		}
	});
}


function initImgSrc(content){
//	$('#temp_div').html(content);
//	$('#temp_div').find('img').each(function(index,obj){
//		var src=$(obj).attr('src');
//		console.log("src="+src);
//		var index=src.indexOf(window.localStorage.ctx);
//		if(index<0){
//			$(obj).attr('src',window.localStorage.ctx+src);
//			$(obj).attr('alt',"img");
//			
//			console.log("src----="+$(obj).attr('src'));
//		}
//	});
//	var ret=$('#temp_div').html();
//	$('#temp_div').empty();
	
	return content;
}

//搜索刷新国际标准码
//function openStandardCodWindow_b(index){
//	$("#standardCodeWin").dialog("open");
//	// 国际标准码信息

//	$.ajax({
//		  url: "/field/findStandardcodeByValue",
//		  contentType:"application/x-www-form-urlencoded; charset=UTF-8",
//		  data: {value:$("#search2").val()},
//		  dataType: 'json',
//		  success: function(data){
//			  $('#conDataGrid_c').datagrid('loadData',[]);
//			  var json=validationData(data);
//					// 循环遍历
//					 for(var i = 0 ; i < json.length ; i++){
//					  // 动态插入产品数据行
//					      $('#conDataGrid_c').datagrid('insertRow',{
//					              index:i ,  // 索引从0开始
//					              row: {
//					            classify: json[i].classify,
//					              code: json[i].code,
//					              cont: json[i].name,
//					              }
//					      }); 
//					  }
//		}
//	});
//}
//选择国际标准码
function selectStandardcode_(index){
	$('#conDataGrid_c').datagrid('selectRow',index);
	var row = $('#conDataGrid_c').datagrid('getSelected');
//	if( window.localStorage.getItem("index")!= "null"){
//		var index2 = window.localStorage.getItem("index");
//		 document.getElementById(index2).value=row.code;
//	}else{
//		document.getElementById("selStandardCode2").value=row.code;
//		document.getElementById("standardCode2").value=row.code;
//	}
	
	
	var open_id=$('#open_id').val();
	
	if($("#"+open_id)&&$("#"+open_id).attr("class")){
		$('#'+open_id).textbox('setValue',row.code);
	}
	else{
		//$('#'+open_id).val(row.code);
		var rowo=$('#dg_select').datagrid('getSelected');
		$('#dg_select').datagrid('updateRow',{
			index: open_id,
			row: {
				code: row.code
			}
		});
		$('#dg_select').datagrid('beginEdit', open_id);
	}
	
	
//	$('#standardCode2').textbox('setValue',row.code);
//	$('#selStandardCode2').textbox('setValue',row.code);
	$("#standardCodeWin").dialog("close");
}
//添加国际标准码
function addPrivateCode_(){
//	if($("#standardCodeType2").textbox('getText') == "" ){
//		$.messager.show({
//			title : '提示',
//			msg : "国际标准码类别不能为空！",
//			timeout : 3000,
//			showType : 'slide'
//		});
//		return;
//	}
	if($("#stan_cod_").textbox('getValue') == "" ){
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : $.i18n.prop('admin.codenotempty'),
			timeout : 3000,border:'thin',
			showType : 'slide'
		});
		return;
	}
	if($("#stan_cont_").textbox('getValue') == ""){
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : $.i18n.prop('admin.contentnotempty'),
			timeout : 3000,border:'thin',
			showType : 'slide',
		});
		return;
	}
	$.post(window.localStorage.ctx+'/srtemplate/addPrivateCode',
		{
			scheme:$("#standardCodeType2").textbox('getValue'),
			code:$("#stan_cod_").textbox('getValue'),
			meaning:$("#stan_cont_").textbox('getValue'),
		},
		function(result){
//			alert(result.code)
			if(result.code == 0){
				$.messager.show({
					title : $.i18n.prop('tips'),
					msg : $.i18n.prop('savesuccess'),
					timeout : 3000,border:'thin',
					showType : 'slide'
				});
//				if(b != null){
//					var index2 = window.localStorage.getItem("index");
//					document.getElementById(index2).value=$("#stan_cod_").val();
//				}else{
//					document.getElementById("selStandardCode2").value=$("#stan_cod_").val();
//					document.getElementById("standardCode2").value=$("#stan_cod_").val();
//				}
				$("#stan_cod_").textbox('setValue','');
				$("#stan_cont_").textbox('setValue','');
//				 $("#standardCodeWin").dialog("close");
			}else{
				if(result.message=="hasexist"){
					$.messager.show({
						title : $.i18n.prop('alert'),
						msg : $.i18n.prop('admin.codehasexist'),
						timeout : 3000,border:'thin',
						showType : 'slide'
					});
				}
				else{
					$.messager.show({
						title : $.i18n.prop('error'),
						msg : $.i18n.prop('savefailed'),
						timeout : 3000,border:'thin',
						showType : 'slide'
					});
				}
			}
		}
	);    
}



//打开插入下拉选窗口
//function openCreateInsertSelectWind(){
	
//	$("#create_select_win").dialog("open");
//	$("#fieldName2").val('');
//	$("#fieldCode2").val('');
//	$("#selStandardCode2").val('');
//	$("#parentdiv2").html('');
//	addDiv_();
//}
//关闭插入下拉选窗口
//function closeCreateInserSelectWind(){
//	$('.edui-cancelbutton .edui-button-body').trigger('click');
//	$("#standardCodeWin").dialog("close");
//	$("#create_select_win").dialog("close");
//}
//
//
//function clearCreateInserSelectWind(){
//	$("#fieldName2").textbox('setValue','');
////	$("#fieldCode2").val('');
//	$("#selStandardCode2").textbox('setValue','');
//	$("#parentdiv2").html('');
//}
/**
 * 保存结构化模板
 * @returns
 */
function saveSRTemplet(isSaveAs){
	
	var data = new Object();
	data.name = $("#srtemplatename_save").val();
	if(!$("#srtemplatename_save").val()){
		return;
	}
	data.maprule = $("#maprule_save").val();
	if(!$("#maprule_save").val()){
		return;
	}
	
	data.enablefilter=$("#enablefilter_save").attr('gvalue');
	data.filter_width=$("#filter_width_save").textbox('getValue');
	
	var content=healtaEditor.getContent();
	if(!content){//ueditor 会过滤textarea标签，如果模板中只有textarea组件，则通过editor.body.innerHTML 获得content
		content=healtaEditor.body.innerHTML;
	}
	
	//保存前判断是否存在嵌套章节情况
	if(checkNesting(content)){
		_message('存在嵌套情况！');
		return ;
	}
	
	$('#temp_div').html(content);
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
	});

	var content_new=$('#temp_div').html();
	$('#temp_div').empty();
	//因为所有组件的id重新生成，需要将表达式中的id替换为新的id
	for(var i=0,len=arr.length;i<len;i++){
		content_new=content_new.replace(new RegExp("{"+arr[i].id+"}","g"),"{"+arr[i].newid+"}");
	}
	healtaEditor.setContent(content_new);
	data.templatecontent = content_new;
	data.footer_left='';
	data.footer_middle='';
	data.footer_right='';
	$('td.drop').each(function(index,obj){
		console.log(obj)
		var rtype=$(obj).children('div').attr('rtype');
		if(rtype){
			if($(obj).attr('rposition')=='left'){
				data.footer_left=rtype;
			} else if($(obj).attr('rposition')=='middle'){
				data.footer_middle=rtype;
			} else if($(obj).attr('rposition')=='right'){
				data.footer_right=rtype;
			}
		}
	});
	
	if(datamodel_obj){
		data.datamodel=JSON.stringify(datamodel_obj[0]);
	}
//	if(!isSaveAs){
//		$('#templet_name').val(data.name);
//		$('#templet_maprule').val(data.maprule);
//	}
	
	if(!$('#templet_id').val()||isSaveAs){
		$.post(window.localStorage.ctx+"/srtemplate/newSRtemplate",data,function(result){
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
		$.post(window.localStorage.ctx+"/srtemplate/updateSRtemplate",data,function(result){
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


function saveSRSection(isSaveAs){
	
	var data = new Object();
	if(!$("#srsectionname_save").val()){
		return;
	}
	data.name = $("#srsectionname_save").val();
	data.displayname = $("#srsectiondisplayname_save").val();
	data.is_qc=$('#is_qc').checkbox('options').checked?1:0;
	data.clone=$('#canclone').checkbox('options').checked?1:0;
	data.catalog=$('#is_catalog').checkbox('options').checked?1:0;
	data.header=$('#is_header').checkbox('options').checked?1:0;
	var content=healtaEditor_section.getContent();
	if(!content){//ueditor 会过滤textarea标签，如果模板中只有textarea组件，则通过editor.body.innerHTML 获得content
		content=healtaEditor_section.body.innerHTML;
	}
	//保存前判断是否存在嵌套章节情况
	if(checkNesting(content)){
		_message('存在嵌套情况！');
		return ;
	}
	//需要判断，如果是可复制章节，则章节中不能再包含其他章节。
	if(data.clone==1){
		var art=$('<div>'+content+'</div>').find("article[name='srsection']").first();
		console.log(art)
		if(art[0]!=null){
			_message('可复制章节中，不能包含其他章节！');
			return;
		}
	}
	
	$('#temp_div').html(content);
	$('#temp_div').find('select,img').each(function(index,obj){
		if($(obj).attr('name')==pluginHandle.component_name_select){
			//$(obj).attr('gvalue',"");
			//$(obj).attr('gvaluecode',"");
		}
		else{
			$(obj).attr('alt',"img");
		}
		
	});
	var content_new=$('#temp_div').html();
	$('#temp_div').empty();
	healtaEditor_section.setContent(content_new);
	data.sectioncontent = content_new;
	if(!$('#section_id').val()||isSaveAs){
		$.post(window.localStorage.ctx+"/srtemplate/newSRSection",data,function(result){
			if(result.id>= 0){
	        	$('#section_id').val(result.id);
	        	$('#srsectionname_opened').html(data.name);
	        	$('#common_dialog').dialog('close');
	        	$.messager.show({
	                title: $.i18n.prop('tips'),
	                msg: $.i18n.prop('savesuccess'),
	                timeout:3000,border:'thin',showType:'slide'
	            });
	
	        }else{
	        	$.messager.show({
	                title: $.i18n.prop('error'),
	                msg: result.message,
	                timeout:3000,border:'thin',showType:'slide'
	            });
	        }
	    });
	}
	else{
		data.id=$('#section_id').val();
		$.post(window.localStorage.ctx+"/srtemplate/updateSRSection",data,function(result){
			if(result.code == 0){
				$('#srsectionname_opened').html(data.name);
            	$.messager.show({
                    title: $.i18n.prop('tips'),
                    msg: $.i18n.prop('savesuccess'),
                    timeout:3000,border:'thin',showType:'slide'
                });
            	$('#common_dialog').dialog('close');
            }else{
            	$.messager.show({
                    title:$.i18n.prop('error'),
                    msg: result.message,
                    timeout:3000,border:'thin',showType:'slide'
                });
            }
		});
		
	}
}

/**
 * 判断修改方法
 */
//function checkSavedCont(templateId){
//	var ue_result = UE.getEditor('ue_savedTemplate');
//		if(st.id == null || st.id == ''){
//			$('#main').panel('open').panel('refresh','/view/admin/savedTemplatemanage.jsp?savedTemplateId='+templateId);
//		}else{
//			if($("#savedTempletName").val()!= st.name || $("#savedTempletMapruleName").val()!=st.maprule || ue_result.getContent() != st.content){
//				$.messager.confirm('提示','是否保存修改？',function(r){
//				    if (r){
//				    	updateSavedTemplet();
//				    }else{
//				    	$('#main').panel('open').panel('refresh','/view/admin/savedTemplatemanage.jsp?savedTemplateId='+templateId);
//				    }
//				})
//			}else{
//				$('#main').panel('open').panel('refresh','/view/admin/savedTemplatemanage.jsp?savedTemplateId='+templateId);
//			}
//		}   
//}







/**
 * 模板另存为
 * @returns
 */
//function saveTempletToAnother(){
//	
//	var data = new Object();
//	if(st.id == null || st.id == ''){
//		$.messager.show({
//            title:'提示',
//            msg: '请选择一个模版！',
//            timeout:3000,
//            showType:'slide'
//        });
//		return;
//	}else{
//		$('#savedTempletName').validatebox({
//			required : true,
//			validType : 'string',
//			missingMessage : '请填写模板名称',
//		});
//		$('#savedTempletMapruleName').validatebox({
//			required : true,
//			validType : 'string',
//			missingMessage : '请填写映射规则',
//		});
//		if($("#savedTempletName").val() == '' || $("#savedTempletName").val() == null ){
//			$.messager.show({
//	            title:'提示',
//	            msg: '模板名称不能为空',
//	            timeout:3000,
//	            showType:'slide'
//	        });
//			return;
//		}
//		data.name = $("#savedTempletName").val();
//		if($("#savedTempletMapruleName").val()==''||$("#savedTempletMapruleName").val()==null ){
//			$.messager.show({
//	            title:'提示',
//	            msg: '模板映射规则不能为空',
//	            timeout:3000,
//	            showType:'slide'
//	        });
//			return;
//		}
//		data.maprule = $("#savedTempletMapruleName").val();
//		var ue_result = UE.getEditor('ue_savedTemplate');
//		if(ue_result.getContent() == null || ue_result.getContent() ==''){
//			$.messager.show({
//	            title:'提示',
//	            msg: '模板内容不能为空',
//	            timeout:3000,
//	            showType:'slide'
//	        });
//			return;
//		}
//		data.content = ue_result.getContent();
//		sendAjax({
//	        'url' : '/srtemplate/findStructTempName',
//	        'type' : 'POST',
//	        'data' : {name:$("#savedTempletName").val()},
//	        'dataType' : "JSON",
//	        'success' : function (result) {
//	        	if(result.code == 0){
//	        		sendAjax({
//	        	        'url' : '/srtemplate/findStructTempMaprule',
//	        	        'type' : 'POST',
//	        	        'data' : {maprule:$("#savedTempletMapruleName").val()},
//	        	        'dataType' : "JSON",
//	        	        'success' : function (result) {
//	        	        	if(result.code == 0){
//	        	        		$.ajax({
//	        	        	        'url' : '/srtemplate/save',
//	        	        	        'type' : 'POST',
//	        	        	        'data' : data,
//	        	        	        'dataType' : "JSON",
//	        	        	        'success' : function (result) {
//	        	        	            if(result.id != 0 && result.id != null){
//	        	        	            	$.messager.show({
//	        	        	                    title:'提示',
//	        	        	                    msg: '模板保存成功',
//	        	        	                    timeout:3000,
//	        	        	                    showType:'slide'
//	        	        	                });
//	        	        	           $('#main').panel('open').panel('refresh','/view/admin/savedTemplatemanage.jsp?savedTemplateId='+result.id);
//	        	        	            }else{
//	        	        	            	$.messager.show({
//	        	        	                    title:'提示',
//	        	        	                    msg: result.message,
//	        	        	                    timeout:3000,
//	        	        	                    showType:'slide'
//	        	        	                });
//	        	        	            }
//	        	        	        }
//	        	        	    })
//	       				 	}else{
//	       				 	 $.messager.show({
//	    							title : '提示',
//	    							msg : "映射规则已存在！",
//	    							timeout : 3000,
//	    							showType : 'slide'
//	    					});
//	    					$("#savedTempletMapruleName").val('');
//	    					return;
//	       				 	}
//	        	        }
//	        	    });
//				 	}else{
//				 	 $.messager.show({
//						title : '提示',
//						msg : "模板名已存在！",
//						timeout : 3000,
//						showType : 'slide'
//				});
//				 $("#savedTempletName").val('');
//				 return;
//				}
//	        }
//	    });
//    		
//	}
//}

/**
 * 修改模板
 * @returns
 */
//function updateSavedTemplet(){
//	   
//	var data = new Object();
//	if($('#savedTempletContent').val()!="null" && $('#savedTempletContent').val()!= ''){
//		data.id = $('#savedTempletContent').val();
//	}else{
//		data.id = 0 ;
//	}
//	st.id = $('#savedTempletContent').val();
//	
//	if(!$("#savedTempletName").val()){
//		$.messager.show({
//            title:'提示',
//            msg: '模板名称不能为空',
//            timeout:3000,
//            showType:'slide'
//        });
//		return;
//	}
//	data.name = $("#savedTempletName").val();
//	st.name = $("#savedTempletName").val();
//	
//	if(!$("#savedTempletMapruleName").val()){
//		$.messager.show({
//            title:'提示',
//            msg: '模板映射规则不能为空',
//            timeout:3000,
//            showType:'slide'
//        });
//		return;
//	}
//	data.maprule = $("#savedTempletMapruleName").val();
//	st.maprule = $("#savedTempletMapruleName").val();
//	var ue_result = UE.getEditor('ue_savedTemplate');
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
//	st.content = ue_result.getContent();
//	$.ajax({
//        'url' : '/srtemplate/update',
//        'type' : 'POST',
//        'data' : data,
//        'dataType' : "JSON",
//        'success' : function (result) {
//            if(result.code == 0 || result.id != null){
//            	$.messager.show({
//                    title:'提示',
//                    msg: '模板保存成功',
//                    timeout:3000,
//                    showType:'slide'
//                });
//            	if(result.id){
//            		openSavedTeample(result.id);
//            		return;
//            	}
//            	openSavedTeample(data.id);
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
//删除结构化模板
function delStructTemp(){
	if(st.id == null || st.id == ''){
		$.messager.show({
            title: $.i18n.prop('alert'),
            msg: $.i18n.prop('admin.choosetemplate'),
            timeout:3000,border:'thin',
            showType:'slide'
        });
		return;
	}else{
		$.ajax({
	        'url' : window.localStorage.ctx+'/srtemplate/delStructTemp',
	        'type' : 'POST',
	        'data' : {'id':st.id},
	        'dataType' : "JSON",
	        'success' : function (result) {
	            if(result.code == 0){
	            	$.messager.show({
	                    title: $.i18n.prop('tips'),
	                    msg: $.i18n.prop('deletesuccess'),
	                    timeout:3000,border:'thin',
	                    showType:'slide'
	                });
	            	st = new Object();
	            	openSavedTeample('');
	            }else{
	            	$.messager.show({
	                    title: $.i18n.prop('error'),
	                    msg: result.message,
	                    timeout:3000,border:'thin',
	                    showType:'slide'
	                });
	            }
	        }
	    })
	}
}


//打开插入文本框窗口
function openInsertTextWind(){
	$("#text_win").window("open");
	$("#orgname1").val('');
	$("#standardCode1").val('');
	$("#orgvalue1").val('');
	findAllUnit();
}

//关闭插入文本框窗口
function closeInserTextWind(){
	//$('.edui-cancelbutton .edui-button-body').trigger('click');
	 $('.edui-cancelbutton .edui-button-body').each(function () {
		  if ($(this).parents('.cs-dialog-box').eq(1).css('display') != 'none') {
			  $(this).trigger('click');
		  }
	  });
	$("#standard_code_win").window("close");
	$("#text_win").window("close");
}

//打开插入文本框窗口
function openInsertSelectWind(){
	
	$("#select_win").window("open");
	$("#fieldName").val('');
	$("#fieldCode").val('');
	$("#sel_standard_code").val('');
	$("#parentdiv").html('');
}
//关闭插入下拉选窗口
function closeInserSelectWind(){
	//$('.edui-cancelbutton .edui-button-body').trigger('click');
	 $('.edui-cancelbutton .edui-button-body').each(function () {
		  if ($(this).parents('.cs-dialog-box').eq(1).css('display') != 'none') {
			  $(this).trigger('click');
		  }
	  });
	$("#standard_code_win").window("close");
	$("#select_win").window("close");
}
//var j = 0;
///**
// * 动态添加div
// */
//function addDiv(){
//	sendAjax({
//		url:'/worklist/checkSession',
//		type:'post',
//		success:function(data){
//		}
//	})
//	
//	var boarddiv = "<div>" +
//	"<input  class='select_name' style='width:120px;height:20px' value = >" +
//	"<input  id= 'index"+j+"' class='select_code' readonly='readonly' style='margin-left:7px;margin-top: 10px;width:120px;height:20px' value = >" +
//	"<button id='sc' style=\"margin-left: 7px;width:80px;height:25px\" onclick=\"openStandardCodWindow(index"+j+");\" >选择编码</button>"+
//	"<button id='a' style=\"margin-left: 7px;width:50px;height:25px\" onclick=\"delDiv($(this));\">删除</button>" +
//	"</div>"; 
//	$("#parentdiv").append(boarddiv);
//	j++;
//}

/**
 * 动态删除div
 */
function delDiv(obj){
	sendAjax({
		url:window.localStorage.ctx+'/worklist/checkSession',
		type:'post',
		success:function(data){
		}
	})     
	obj.parent().remove();
}
var a;
//打开国际标准码窗口
//function openStandardCodWindow(index){
//	$("#search").val('');
//	$('#del').hide();
//	if(index){
//		a = index;
//		window.localStorage.setItem("index",index.id);
//	}else{
//		a = null;
//		window.localStorage.setItem("index",null);
//	}
//	$("#standard_code_win").window("open");
//	// 国际标准码信息

//	$.ajax({
//		  url: "/field/findAllStandardcode",
//		  contentType:"application/x-www-form-urlencoded; charset=UTF-8",
//		  data: {},
//		  dataType: 'json',
//		  success: function(data){
//			  $('#conDataGrid_').datagrid('loadData',[]);
//			  var json=validationData(data);
//					// 循环遍历
//					 for(var i = 0 ; i < json.length ; i++){
//					  // 动态插入产品数据行
//					      $('#conDataGrid_').datagrid('insertRow',{
//					              index:i ,  // 索引从0开始
//					              row: {
//					           classify: json[i].classify, 
//					              code: json[i].code,
//					              cont: json[i].name,
//					              }
//					      }); 
//					  }
//		}
//	});
//}

//选择国际标准码
//function selectStandardcode(index){
//	$('#conDataGrid_').datagrid('selectRow',index);
//	var row = $('#conDataGrid_').datagrid('getSelected');
//	if( window.localStorage.getItem("index")!= "null"){
//		var index2 = window.localStorage.getItem("index");
//		document.getElementById(index2).value=row.code;
//	}else{
//		document.getElementById("sel_standard_code").value=row.code;
//		document.getElementById("standardCode1").value=row.code;
//	}
//	 $("#standard_code_win").window("close");
//}
//添加国际标准码
function addStandCode(){
	if($("#standardCodeType").val() == "" ||$("#standardCodeType").val()==null ){
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : $.i18n.prop('codenotempty'),
			timeout : 3000,border:'thin',
			showType : 'slide'
		});
		return;
	}
	if($("#stan_cod").val() == "" ||$("#stan_cod").val()==null ){
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : $.i18n.prop('admin.codenotempty'),
			timeout : 3000,border:'thin',
			showType : 'slide'
		});
		return;
	}
	if($("#stan_cont").val() == "" ||$("#stan_cont").val()==null ){
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : $.i18n.prop('admin.contentnotempty'),
			timeout : 3000,border:'thin',
			showType : 'slide',
		});
		return;
	}
	sendAjax({
		url:window.localStorage.ctx+'/field/addStandardCode',
		type:'post',
		contentType:"application/x-www-form-urlencoded; charset=UTF-8",
		data: {
			classify:$("#standardCodeType").val(),
			code:$("#stan_cod").val(),
			name:$("#stan_cont").val(),
		},
		success:function(result){
			if(result.code == 0){
				$.messager.show({
					title : $.i18n.prop('tips'),
					msg : $.i18n.prop('savesuccess'),
					timeout : 3000,border:'thin',
					showType : 'slide'
				});
				if(a != null){
					var index2 = window.localStorage.getItem("index");
					document.getElementById(index2).value=$("#stan_cod").val();
				}else{
					document.getElementById("sel_standard_code").value=$("#stan_cod").val();
					document.getElementById("standardCode1").value=$("#stan_cod").val();
				}
				$("#stan_cod").val('');
				$("#stan_cont").val('');
				 $("#standard_code_win").window("close");
			}else{
				$.messager.show({
					title : $.i18n.prop('error'),
					msg : $.i18n.prop('savefailed'),
					timeout : 3000,border:'thin',
					showType : 'slide'
				});
			}
		}
	})    
}


var j = 0;
/**
 * 动态添加div
 */
function addDiv_(code,value){
	
	var boarddiv = "<div>" +
	"<input  class='select_name' style='width:130px;height:20px' value='"+value+"'/>" +
	"<input  id= 'index"+j+"' class='select_code' readonly='readonly' value='"+code+"' style='margin-left:7px;margin-bottom: 7px;width:130px;height:20px'/>" +
	"<input type='button'  id='sc' style=\"margin-left: 7px;width:80px;height:25px\" onclick=\"openStandardCodWindow_(\'index"+j+"\');\" value='编码'/>"+
	"<button id='a' style=\"margin-left: 7px;width:50px;height:25px\" onclick=\"delDiv_($(this));\">删除</button>" +
	"</div>"; 
	$("#parentdiv").append(boarddiv);
	j++;
}

/**
 * 动态删除div
 */
function delDiv_(obj){    
	obj.parent().remove();
}


function newSRtmp(){
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
					$('#templet_id').val("");
					$('#srtemplatename_opened').html("新建模板");
//					$('#templet_name').val("");
//					$('#templet_maprule').val("");
					healtaEditor.setContent("");
				}
			}
		});
	}
	else{
		$('#srtemplatename_opened').html("新建模板");
	}
}

function newSRSection(){
	healtaEditor_section.setEnabled();
	$('#savebtn_section').linkbutton('enable');
	$('#saveasbtn_section').linkbutton('enable');
	if(healtaEditor_section.getContent()!=""){
		$.messager.confirm({
			title : $.i18n.prop('confirm'),
			border:'thin',
			msg : '是否放弃保存当前打开的章节？',
			fn : function(r) {
				if (r) {
					$('#section_id').val("");
					$('#srsectionname_opened').html("新建章节");
//					$('#templet_name').val("");
//					$('#templet_maprule').val("");
					healtaEditor_section.setContent("");
				}
			}
		});
	}
	else{
		$('#srsectionname_opened').html("新建章节");
	}
}

function openTmp(){
	
	$('#common_dialog').dialog(
			{
				title : $.i18n.prop('admin.opentemplate'),
				width : 575,height : 535,
				border: 'thin',
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				href : window.localStorage.ctx+'/srtemplate/openSRTemplateList',
				buttons:[{
					text:$.i18n.prop('admin.open'),
					width:80,
					handler:function(){openSRTemp(null,null);}
				},{
					text: $.i18n.prop('cancel'),
					width:80,
					handler:function(){$('#common_dialog').dialog('close');}
				}]
	});
}

function openSectionListDialog(){
	
	$('#common_dialog').dialog(
			{
				title : '打开章节',
				width : 575,height : 500,
				border: 'thin',
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				href : window.localStorage.ctx+'/srtemplate/gotoSRSectionList',
				buttons:[{
					text:$.i18n.prop('admin.open'),
					width:80,
					handler:function(){openSRSection(null,null);}
				},{
					text: $.i18n.prop('cancel'),
					width:80,
					handler:function(){$('#common_dialog').dialog('close');}
				}],
				onLoad:function(){
					$('#section_dg').datagrid({onDblClickRow:openSRSection})
				}
	});
}

function doSearchSRTempAndComponent(value){
	//if(value!=""){
		
	$.getJSON(window.localStorage.ctx+"/srtemplate/findSRTemplateAndComponent?name="+value, function(json){
		$("#srtempandcompdg").datagrid("loadData",validationData(json));
    });
	//}
}

function delSRTempAndComponent(){
	
	var rows=$("#srtempandcompdg").datagrid("getSelections");
	
	var tempids="";
	var compids="";
	var secids="";
	$.each( rows, function( index, row ){
		
		if(row.groupid=="srtemplate"){
			tempids+=row.id+",";
		} else if(row.groupid=="srsection"){
			secids+=row.id+",";
		} else if(row.groupid=="srcomponent"){
			compids+=row.id+",";
		}
	});
	
	//alert(tempids+"--"+compids);
	
	if(tempids==""&&compids==""&&secids==""){
		 $.messager.show({title : $.i18n.prop('alert'),msg : $.i18n.prop('admin.selectstemporcomponent'),timeout : 3000,showType : 'slide'});
	}
	else{
		$.messager.confirm({
			title : $.i18n.prop('confirm'),
			border: 'thin',
			msg : $.i18n.prop('admin.confirmdeletetemporcomponent'),
			fn : function(r) {
				if (r) {
					$.getJSON(window.localStorage.ctx+"/srtemplate/delSRTemplateAndComponent"
						,{
							'tempids':tempids,
							'secids':secids,
							'compids':compids
						}
						,function(json){
							var result=validationData(json);
							$.each( rows, function( index, row ){
								$("#srtempandcompdg").datagrid("deleteRow",$("#srtempandcompdg").datagrid("getRowIndex",row));
							});
				    });
				}
			}
		});
	}
	
}

function insertClinicalCodeToTmp(row,editor){
	pluginHandle.insertClinicalCodeComponent(editor,row);
}

function insertFindingToTmp(row,editor){
	pluginHandle.insertFindingComponent(editor,row);
}


function modifyComponent(id,editor){
	var row = $("#"+id).datagrid("getSelected");
	
	if(row.type=="0"){
		//console.log($(row.html)[0])
		UE.plugins["numberinput"].editdom=$(row.html)[0];
		UE.plugins["numberinput"].editway=true;
		$('#common_dialog').dialog(
			{
				title : $.i18n.prop('admin.editnumberbox'),
				width : 500,
				height : 620,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : false,
				border: 'thin',
				href : 'js/ueditor/formdesign/number.jsp',
				buttons:[{
					text: $.i18n.prop('ok'),
					width:80,
					handler:function(){onok(editor);}
				},{
					text: $.i18n.prop('cancel'),
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}],
				onClose:function(){$('#componentsandsections_dg').datagrid('reload');$('#components_dg').datagrid('reload');}
		});
	}
	else if(row.type=="1"){
		//console.log($(row.html)[0])
		UE.plugins["select"].editdom=$(row.html)[0];
		UE.plugins["select"].editway=true;
		$('#common_dialog').dialog(
			{
				title : $.i18n.prop('admin.editcombobox'),
				width : 680,
				height : 750,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : false,
				border: 'thin',
				href : 'js/ueditor/formdesign/select.jsp',
				buttons:[{
					text: $.i18n.prop('ok'),
					width:80,
					handler:function(){onok(editor);}
				},{
					text: $.i18n.prop('cancel'),
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}],
				onClose:function(){$('#componentsandsections_dg').datagrid('reload');$('#components_dg').datagrid('reload');}
		});
	}
	else if(row.type=="2"){
		//console.log($(row.html)[0])
		UE.plugins["textarea"].editdom=$(row.html)[0];
		UE.plugins["textarea"].editway=true;
		$('#common_dialog').dialog(
			{
				title : $.i18n.prop('admin.edittextarea'),
				width : 450,
				height : 510,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : false,
				border: 'thin',
				href : 'js/ueditor/formdesign/textarea.jsp',
				buttons:[{
					text: $.i18n.prop('ok'),
					width:80,
					handler:function(){onok(editor);}
				},{
					text: $.i18n.prop('cancel'),
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}],
				onClose:function(){$('#componentsandsections_dg').datagrid('reload');$('#components_dg').datagrid('reload');}
		});
	}
	else if(row.type=="3"){
		//console.log($(row.html)[0])
		UE.plugins["textinput"].editdom=$(row.html)[0];
		UE.plugins["textinput"].editway=true;
		$('#common_dialog').dialog(
			{
				title : $.i18n.prop('admin.edittextbox'),
				width : 400,
				height : 430,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : false,
				border: 'thin',
				href : 'js/ueditor/formdesign/text.jsp',
				buttons:[{
					text: $.i18n.prop('ok'),
					width:80,
					handler:function(){onok(editor);}
				},{
					text: $.i18n.prop('cancel'),
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}],
				onClose:function(){$('#componentsandsections_dg').datagrid('reload');$('#components_dg').datagrid('reload');}
		});
	} else if(row.type=="4"){
		//console.log($(row.html)[0])
		UE.plugins["datetime"].editdom=$(row.html)[0];
		UE.plugins["datetime"].editway=true;
		$('#common_dialog').dialog(
			{
				title : $.i18n.prop('admin.editdatetime'),
				width : 400,
				height : 430,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : false,
				border: 'thin',
				href : 'js/ueditor/formdesign/datetime.jsp',
				buttons:[{
					text: $.i18n.prop('ok'),
					width:80,
					handler:function(){onok(editor);}
				},{
					text: $.i18n.prop('cancel'),
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}],
				onClose:function(){$('#componentsandsections_dg').datagrid('reload');$('#components_dg').datagrid('reload');}
		});
		
	} else if(row.type=="5"){
		//console.log($(row.html)[0])
		UE.plugins["checkbox"].editdom=$(row.html)[0];
		UE.plugins["checkbox"].editway=true;
		$('#common_dialog').dialog(
			{
				title : '编辑复选框',
				width : 680,
				height : 740,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : false,
				border: 'thin',
				href : 'js/ueditor/formdesign/checkbox.jsp',
				buttons:[{
					text: $.i18n.prop('ok'),
					width:80,
					handler:function(){onok(editor);}
				},{
					text: $.i18n.prop('cancel'),
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}],
				onClose:function(){$('#componentsandsections_dg').datagrid('reload');$('#components_dg').datagrid('reload');}
		});
	} else if(row.type=="6"){
		//console.log($(row.html)[0])
		UE.plugins["radio"].editdom=$(row.html)[0];
		UE.plugins["radio"].editway=true;
		$('#common_dialog').dialog(
			{
				title : '编辑单选框',
				width : 680,
				height : 740,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : false,
				border: 'thin',
				href : 'js/ueditor/formdesign/radio.jsp',
				buttons:[{
					text: $.i18n.prop('ok'),
					width:80,
					handler:function(){onok(editor);}
				},{
					text: $.i18n.prop('cancel'),
					width:80,
					handler:function(){oncancel();$('#common_dialog').dialog('close');}
				}],
				onClose:function(){$('#componentsandsections_dg').datagrid('reload');$('#components_dg').datagrid('reload');}
		});
	}
	
}


function insertComponent(row,editor,objuidflag){
	pluginHandle.insertCustomComponent(editor,row,objuidflag);
}

function insertSection(row,editor,isEditSection){
	if(isEditSection&&row.id==$('#section_id').val()){
		_message('存在嵌套情况，已经撤销操作！');
		return;
	}
	pluginHandle.insertSection(editor,row.uid,row.sectioncontent,row.is_qc,row.clone,row.name,row.displayname||"",row.catalog,row.header);
	if(checkNesting(editor.getContent())){
		editor.execCommand('undo');
		_message('存在嵌套情况，已经撤销操作！');
	}
}

//检查是否存在嵌套章节清空,return true:存在嵌套，false:不存在嵌套
function checkNesting(content){
	var ret=false;
	$("<div>"+content+"</div>").find("article[name='srsection']").each(function(index,obj){
		var sectionuid=$(obj).attr('sectionuid');
		console.log(sectionuid)
		$(obj).parents("article[name='srsection']").each(function(index,pobj){
			var psectionuid=$(pobj).attr('sectionuid');
			console.log(psectionuid)
			if(psectionuid==sectionuid){
				ret=true;
				return;
			}
		});
	});
	
	return ret;
}

function escape2Html(str) {
	 var arrEntities={'lt':'<','gt':'>','nbsp':' ','amp':'&','quot':'"'};
	 return str.replace(/&(lt|gt|nbsp|amp|quot);/ig,function(all,t){return arrEntities[t];});
}

function updateComponentInSRTemplate(data,editor){

//	console.log(escape2Html(data.html))
//	console.log($(escape2Html(data.html))[0])
	$('#temp_div').html(editor.getContent());
	
	if(data.type==0){
		$("#temp_div input[uid='"+data.uid+"']").replaceWith("");
		var n=0;
		$("#temp_div input[uid='"+data.uid+"']").replaceWith(function(){
			var html=escape2Html(data.html)
			if(data.unit){
				var iuid=data.uid+new Date().format('yyyyMMddhhmmssS')+(n++);
				var unitstr="<input type='button' for='"+iuid+"' uid='"+data.uid+"' style='border:0px;background: #EFEFEF;' value='"+data.unit+"'/>&nbsp;";
				var o=$(html);
				o.attr("iuid",iuid);
				html=o[0].outerHTML+unitstr;
				o.remove();
			}
			return html;
	    });
	}
	else{
		$("#temp_div input[uid='"+data.uid+"'],select[uid='"+data.uid+"'],textarea[uid='"+data.uid+"']").replaceWith(escape2Html(data.html));
	}

	var content_new=$('#temp_div').html();
	$('#temp_div').empty();
	editor.setContent(content_new);
}


function deleteComponent(id){
	var row=$('#'+id).datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title : $.i18n.prop('confirm'),
			msg : $.i18n.prop('admin.confirmdeletecomponent'),
			border: 'thin',
			fn : function(r) {
				if (r) {
					$.getJSON(window.localStorage.ctx+"/srtemplate/delSRTemplateAndComponent",{'tempids':"",'compids':row.id} ,function(json){
						var result=validationData(json);
//						$.each( rows, function( index, row ){
//							$('#'+id).datagrid("deleteRow",$('#'+id).datagrid("getRowIndex",row));
//						});
						
						if(result.code==0){
							$('#componentsandsections_dg').datagrid('reload');
							$('#components_dg').datagrid('reload');
						}
				    });
				}
			}
		});
	}
	else{
		$.messager.show({
			title : $.i18n.prop('alert'),msg : $.i18n.prop('admin.choosecomponent'),border: 'thin',timeout : 3000,showType : 'slide'
		});
	}
}

function deleteSection(id){
	var row=$('#'+id).datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title : $.i18n.prop('confirm'),
			msg : '确认删除选中的章节吗？',
			border: 'thin',
			fn : function(r) {
				if (r) {
					$.getJSON(window.localStorage.ctx+"/srtemplate/delSRTemplateAndComponent",{'secids':row.id} ,function(json){
						var result=validationData(json);
						if(result.code==0){
							$('#'+id).datagrid('reload');
						}
				    });
				}
			}
		});
	}
	else{
		$.messager.show({
			title : $.i18n.prop('alert'),msg : $.i18n.prop('admin.choosecomponent'),border: 'thin',timeout : 3000,showType : 'slide'
		});
	}
}

function exportSRTemplate(){
	
	var rows=$('#srtempandcompdg').datagrid('getSelections');
	//console.log(rows);
	if(rows&&rows.length>0){
		var rids="",cids="",sids="";
		$(rows).each(function(index,row){
			if(row.groupid=="srtemplate"){
				rids+=row.id+",";
			} else if(row.groupid=="srsection"){
				sids+=row.id+",";
			} else{
				cids+=row.id+",";
			}
		});
		
		$('<form method="post" target="" role="form" action="'+window.localStorage.ctx+'/srtemplate/downloadSRTemplate?rids='+rids+'&cids='+cids+'&sids='+sids+'" hidden="hidden"></form>').appendTo('body').submit().remove();
	}
	else{
		$.messager.show({
	          title:$.i18n.prop('alert'),msg: $.i18n.prop('admin.choosetemplate'),timeout:3000,border:'thin',showType:'slide'
	    });
	}
	
}

function importSRTemplate(newValue,oldValue){
	if(newValue!=""){
		$('#importfm').form('submit', {
		    url:window.localStorage.ctx+'/srtemplate/importSRTemplate',
		    success:function(result){
		    	var json=validationData(result);
		    	if(json.code == 0){
		    		$('#importsrtempfilebox').textbox('setValue','');
		    		doSearchSRTempAndComponent($('#srlibsearchbox').searchbox("getValue"));
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

function qc_formatter(value,row,index){
	if(value==1){
		return "是";
	}
	else{
		return "";
	}
}

function clone_formatter(value,row,index){
	if(value==1){
		return "是";
	}
	else{
		return "";
	}
}

function select_domain(index,row){
	getJSON(window.localStorage.ctx+"/srtemplate/getModels",
		{
			domainid:row.id
		},
		function(json){
//			var result=validationData(json);
			$('#models_dg').datagrid('loadData',json);
			$('#model_tree').treegrid('loadData',{total: 0,rows: []});
    });
}

function select_model(index,row){
	getJSON(window.localStorage.ctx+"/srtemplate/getAttrs",
		{
			modelid:row.id
		},
		function(json){
//			var result=validationData(json);
			$('#model_tree').treegrid('loadData',json);
    });
}

function select_attr(node){
	console.log(node)
	if(!node.attributes.code){
		_message('属性编码为空，无法选择！');
		return;
	}
	var domain=$('#domains_dg').datalist('getSelected');
	var model=$('#models_dg').datalist('getSelected');
	
	var path=domain.name+'.'+model.name+'.'+getNodePath(node,'');
	console.log(path);
	var open_id=$('#open_id').val();
	if($("#"+open_id)&&$("#"+open_id).attr("class")){
		var component_type=$('#component_type').val();
		//console.log('component_type='+component_type);
		if(checkDaTypeAndCompType(node.attributes.datatype,component_type)){
			$('#'+open_id).textbox('setValue',node.attributes.code);
			$('#'+open_id).attr('_path',path);
			if(node.attributes.defaultvalue){//设置默认值
				$('#orgvalue').textbox('setValue',node.attributes.defaultvalue);
			}
			
			if(component_type=='1'||component_type=='5'||component_type=='6'){//选择框/复选框/单选框自动设置选项
				if(node.attributes.items){
					var rows=JSON.parse(node.attributes.items);
					for(var i = 0;i<rows.length;i++){
						rows[i].canotedit='1';
					}
					//console.log(rows)
					$('#dg_select').datagrid('loadData', rows);
				}
					
				$('#_addoption').linkbutton("disable");//设置模型的选项，无法再手动添加选项
				if(component_type=='1'&&node.attributes.datatype=='array_simple'){//数据类型为'array_simple',设置为多谢
					$('#select_multiple').checkbox("check");
					$('#select_multiple').checkbox("disable");
				}
			} else if(component_type=='0'){
				$('#orgmin').numberbox('setValue',(node.attributes.minvalue||''));
		        $('#orgmax').numberbox('setValue',(node.attributes.maxvalue||''));
			}
		} else{
			_message('组件与所选数据类型不匹配！');
		}
	}
//	else{
//		//$('#'+open_id).val(row.code);
//		var rowo=$('#dg_select').datagrid('getSelected');
//		$('#dg_select').datagrid('updateRow',{
//			index: open_id,
//			row: {
//				code: node.attributes.code,
//				_path: path
//			}
//		});
//		$('#dg_select').datagrid('beginEdit', open_id);
//	}
	
	$("#standardCodeWin").dialog("close");
}

function getNodePath(node,path){
	if(path){
		path=node.name+'.'+path;
	} else{
		path=node.name;
	}
	var pa=$('#model_tree').treegrid('getParent',node.id);
	if(pa){
		return path=getNodePath(pa,path);
	} else{
		return path;
	}
}
/*
 * 检查组件类型和数据类型是否匹配
 * */
function checkDaTypeAndCompType(datatype,component_type){
	console.log('component_type='+component_type+';datatype='+datatype);
	
	if(component_type=='1'&&(datatype=='string'||datatype=='array_simple'||datatype=='integer')){//选择框
		return true;
	} else if(component_type=='5'&&(datatype=='string'||datatype=='array_simple'||datatype=='integer')){//复选框
		return true;
	} else if(component_type=='6'&&(datatype=='string'||datatype=='integer')){//单选框   ||datatype=='array_simple'
		return true;
	} else if(component_type=='3'&&(datatype=='string')){//单行文本框
		return true;
	} else if(component_type=='2'&&(datatype=='text')){//多行文本框
		return true;
	} else if(component_type=='4'&&(datatype=='date'||datatype=='datetime')){//日期时间输入框
		return true;
	} else if(component_type=='0'&&(datatype=='float'||datatype=='integer')){//数字输入框
		return true;
	}
	
	return false;
}


function nanChartDetailFormatter(index,row){
	return '<div style="padding:2px;position:relative;"><div class="ddiv"></div></div>';
}

function nanChartOnExpandRow(index,row){
	var ddv = $(this).datagrid('getRowDetail',index).find('div.ddiv');
	ddv.html('<embed src="image/anatomychart/'+row.name+'" width="230" height="330" type="image/svg+xml" pluginspage="http://www.adobe.com/svg/viewer/install/"/>');
}


function openChoiceLocationDialog(open_id){
	$("#open_id").val(open_id);
	$('#formula_dialog').dialog({
		title : '请选择定位组件',
		width : 800,height : 800,border: 'thin',
		closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
		href : window.localStorage.ctx+'/srtemplate/toLocationComponent',
		buttons:[{
			text: $.i18n.prop('ok'),
			width:80,
			handler:function(){
				var select= $('#location_content').find('select.selected_com:first');
				console.log(select)
				if(select[0]){
					$('#'+open_id).textbox('setValue',select.attr("id"));
					$('#formula_dialog').dialog('close');
				} else{
					_message('请选择一个组件！');
				}
			}
		},{
			text: $.i18n.prop('cancel'),
			width:80,
			handler:function(){$('#formula_dialog').dialog('close');}
		}],
		onLoad:function(){
			//$('#formula_id').textbox('setValue',trim($('#'+open_id).val()));
			var oNode = UE.plugins['anatomychart_svg'].editdom;
			if(oNode){
				var body=$(oNode).parents("body.view");
				if(body[0]){
					//console.log($(body[0]).html());
					var clone=$(body[0]).clone();
					clone.find("input,textarea,img,embed").remove();
					clone.find("select").each(function(index,e){
						$(e).removeClass('easyui-combobox').addClass('tmpselect').removeAttr('data-options').attr('onclick','selectLocation(this)');
						var id=$('#'+open_id).textbox('getValue');
						if($(e).attr('id')==id){
							$(e).css('background-color','#1a7bc9');
							$(e).addClass('selected_com');
						}
					});
					$('#location_content').panel({
						content:clone.html()
					});
					clone.remove();
				}
			}
		},
		onBeforeClose:function(){
			$('#location_content').panel('clear');
			return true;
		}
	});
	
}

function selectLocation(obj){
	console.log($(obj))
	$('#location_content').find('select').each(function(index,e){
		$(e).css('background-color','');
		$(e).removeClass('selected_com');
	});
	$(obj).css('background-color','#1a7bc9');
	$(obj).addClass('selected_com');
}
























