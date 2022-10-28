var formula_evt;
$(function(){
	//console.log("=======srreport_request_reportid====:"+request_reportid);
	$("#imageShow_" + request_reportid).attr('src',$("#imageShow").attr('src'));
	
	// 创建
	formula_evt = document.createEvent("HTMLEvents");
	// 初始化
	formula_evt.initEvent("formula_calculate", false, false);
})

/*
 * 重置报告目录,默认为true。
 * 在如下条件下重置目录：
 * 打开报告，切换模板
 * 在如下条件下不重置目录：
 * 保存、提交、初审、审核报告
 * */

function generatedCatalog(container,reportStatus){
	if(container.attr("resetCatalog")=="true"){
		$('#progress_dlg').dialog('open');
		var datas=new Array(0);
		container.find("article[name='srsection'][is_catalog='1'],article[name='srsection'][is_header='1']").each(function(index,obj){
			//隐藏标题章节
			if($(obj).attr('is_header')=='1'){
				$(obj).hide();
			} else{
				var data=new Object();
				var title=$(obj).attr('displayname');
				if(!title){
					title=$(obj).attr('title');
				}
				data.title=title;
				data.id=$(obj).attr('id');
				data.hasshow=$(obj).attr('hasshow');
				datas.push(data);
				//审核报告全部显示
				if(reportStatus==myCache.ReportStatus.FinalResults){
					$(obj).show();
				}
			}
		});
		var reportid=container.attr('reportid');
		var westpanelopt=$('#sr_content_'+reportid).layout('panel','west').panel('options');
		if(datas.length>0){
			if(westpanelopt.collapsed){
				$('#sr_content_'+reportid).layout('expand','west');
			}
			setTimeout(function (){
				$('#catalog_'+reportid).datalist('loadData',datas);
				//审核报告全部显示
				if(reportStatus!=myCache.ReportStatus.FinalResults){
					$('#catalog_'+reportid).datalist('selectRow',0);
				}
				$('#progress_dlg').dialog('close');
			},500);
		} else{
			if(!westpanelopt.collapsed){
				$('#sr_content_'+reportid).layout('collapse','west');
			}
			setTimeout(function (){
				if($('#catalog_'+reportid)[0]){
					$('#catalog_'+reportid).datagrid('loadData',datas);
				}
				$('#progress_dlg').dialog('close');
			},500);
		}
	}
}

function catalog_select_handle(index,row,reportid){
	var datas=$('#catalog_'+reportid).datalist('getData');
	for(var i=0,len=datas.total;i<len;i++){
		if(datas.rows[i].id==row.id){
			$('#'+datas.rows[i].id).show();
			var hasshow=$('#'+datas.rows[i].id).attr("hasshow");
			if(hasshow!='1'){
				$('#'+datas.rows[i].id).attr("hasshow","1");
				row.hasshow="1";
				$('#catalog_'+reportid).datagrid('updateRow',{
					index: index,
					row: row
				});
				$('#catalog_'+reportid).datagrid('refreshRow',index);
			}
		} else{
			$('#'+datas.rows[i].id).hide();
		}
	}
}

function catalogStyler(index,row) {
	if(row.hasshow=="1"){
		return 'color:#000000;height:45px;text-align: center;';//'background-color:#2C3B41;color:#B8C7CE;height:30px;';
	} else{
		return 'color:#B8C7CE;height:45px;text-align: center';
	}
}

function selectAllCatalog(reportid){
	$('#catalog_'+reportid).datalist('unselectAll');
	var datas=$('#catalog_'+reportid).datalist('getData');
	for(var i=0,len=datas.total;i<len;i++){
		$('#'+datas.rows[i].id).show();
	}
}

function showHeaderSection(reportid){
	$('#sr_container_'+reportid).find("article[name='srsection'][is_header='1']").each(function(index,obj){
		$(obj).toggle();
	});
}

function reg_formula_dom(container,selector){
	console.log('reg_formula_dom')
	if(selector){
		selector+=' ';
	}
	
	var formulaData= container.data('formulaData');
	if(!formulaData){
		formulaData=new Map();
	}
	container.find((selector||'')+"input[formula]").each(function(index,obj){
		//console.log(obj)
		var formula =$(obj).attr('formula');
		if(formula){
			var match = formula.match(/{.+?}/g);
			if(match){
				for(var i=0,len=match.length;i<len;i++){
					var id=match[i].substring(1,match[i].length-1);
					//console.log(id);
					var array=formulaData.get(id);
					if(!array){
						array=new Array();
					}
					array.push($(obj).attr('id'));
					formulaData.set(id,array);
				}
			}
		}
		
		obj.addEventListener("formula_calculate", function(e) {
			calculateFormula(e.target||e.currentTarget)
		});
		
	});
	console.log(formulaData)
	container.data('formulaData',formulaData);
}

function calculateFormula(obj){
	console.log(obj)
	var formula=$(obj).attr('formula');
	if(formula){
		var match = formula.match(/{.+?}/g);
		if(match){
			for(var i=0,len=match.length;i<len;i++){
				var id=match[i].substring(1,match[i].length-1);
				formula=formula.replace(new RegExp(match[i],"g"),"parseFloat($('#"+id+"').val())");
			}
			console.log(formula);
			try{
				var n=eval(formula);
				console.log(n);
				if(typeof n === 'number' &&!isNaN(n)){//数值
					n=Math.round(n*10000)/10000;
					$(obj).val(n);
					obj.setAttribute('value',n);
					$(obj).change();
				}
			} catch(e){
				console.log(formula+"---"+e);
				//_message("表达式语法错误！错误信息："+e);
				return false;
			}
		}
	}
}

function deleteFindingFromReport(){
	if(pluginHandle.editdom&&$(pluginHandle.editdom).attr('name')==pluginHandle.component_name_snapshot){
		$.messager.confirm({
			title: $.i18n.prop('confirm'),
			border:'thin',
			msg: $.i18n.prop('report.confirmdeletefinding'),
			fn: function(r){
				if (r){
					$(pluginHandle.editdom).remove();
					delete pluginHandle.editdom;
				}
			}
		});
	}
}

function clearFindingContent(){
	if(pluginHandle.editdom&&$(pluginHandle.editdom).attr('name')==pluginHandle.component_name_snapshot){
		$.messager.confirm({
			title: $.i18n.prop('confirm'),
			border:'thin',
			msg: $.i18n.prop('report.confirmclearfindingcontent'),
			fn: function(r){
				if (r){
					$(pluginHandle.editdom).empty();
					delete pluginHandle.editdom;
				}
			}
		});
	}
}

function openEditImageSnapshotDialog(){
	console.log(pluginHandle.editdom);
	if(pluginHandle.editdom&&$(pluginHandle.editdom).html()&&$(pluginHandle.editdom).attr('name')==pluginHandle.component_name_snapshot){
		
		$('#common_dialog').dialog(
				{
					title : $.i18n.prop('report.sortimage'),
					width : 800,height : 720,
					cache : false,modal : true,
					border: 'thin',
					href : window.localStorage.ctx +'/srreport/goToRearrageSnaphsot',
					buttons:[{
						text:$.i18n.prop('ok'),
						width:80,
						handler:function(){
							var editor = UM.getEditor('snapshot_container');
							console.log($(editor.getContent()))
							
							var imgstr="";
							$(editor.getContent()).find("img").each(function(index,element){
								//过滤掉图片src中的‘http://0.0.0.0:8080/report/’,保留相对的url
								if($(this).attr("src")&&$(this).attr("src").indexOf("http")>=0){
									$(this).attr("src",$(this).attr("src").substr($(this).attr("src").indexOf(window.localStorage.ctx)+8));
								}
								
								
								var imgwidth=$(this).width();
								if(imgwidth){
									imgwidth="width:"+imgwidth+"px;";
								}
								else{
									imgwidth="width:300px;";
								}
								var imgheight=$(this).height();
								if(imgheight){
									imgheight="height:"+imgheight+"px;";
								}
								else{
									imgheight="";
								}
								imgstr+="<div class='mydiv-inline' style='"+imgwidth+imgheight+"'>"+this.outerHTML+"</div>";
								
							});
							
							$(pluginHandle.editdom).html(imgstr);  //$(editor.getContent()).find("img")
							addSN2Snapshot($('#sr_container_'+getReportid_Open()));
							$('#common_dialog').dialog('close');
							UM.delEditor('snapshot_container');
							delete pluginHandle.editdom;
						}
					},{
						text:$.i18n.prop('cancel'),
						width:80,
						handler:function(){delete pluginHandle.editdom;UM.delEditor('snapshot_container');$('#common_dialog').dialog('close');}
					}]
//					,onLoad:function(){
//						var editor = UE.getEditor('snapshot_container',{wordCount:false,elementPathEnabled:false,autoHeightEnabled: true,initialFrameHeight:612});
//						editor.ready(function() {
//							//editor.setHeight(612);
//							editor.setContent($(pluginHandle.editdom).html());
//						});
						
//					},
				//onBeforeClose
					,onClose:function(){
						//var editor = 
							UM.delEditor('snapshot_container');
							delete pluginHandle.editdom;
						//if(editor)editor.destroy();
					}
		});
			
		
	}
}


//function insertFindingToSRReport(index,row,studyid){
//	
////	if(UE.getEditor('desc_' + studyid + '_html').isFocus()){
//		UE.getEditor('structTemplet_' + studyid).execCommand('insertHtml', 
//				'<hr style="border:0px;"/><article gvalue="'+row.findingname+'" report="'+row.belongreport+'" gtype="finding" style="border:1px solid #EEEEEE;padding:5px;">'+row.htmlsr+
//				'</article><br/>');
////	}
////	if(UE.getEditor('result_' + studyid + '_html').isFocus()){
////		UE.getEditor('result_' + studyid + '_html').execCommand('insertHtml', row.html);
////	}
//}
function beforeSaveOrAuditSR(reportid,content,data,PhysicianName,DateTime,type){
	//保存，提交，初审，审核报告时，不需要重置报告目录
	$('#sr_container_' + reportid).attr('resetCatalog',false);
	//$('#temp_' + reportid).html(content);
	var clone=$('<div>'+content+'</div>');
	
	//设置可复制章节中组件的组号
	clone.find("article[name='srsection'][clone='1']").each(function(index,obj){
//		console.log(obj);
		var g1=uuid();
		$(obj).find("input:not(.textbox-text,.textbox-value),select,textarea").each(function(ind,o){
//			console.log(o);
			var ___group=$(o).attr("___group");
			if(___group){
				___group=g1+'.'+___group;
			} else{
				___group=g1;
			}
			$(o).attr("___group",___group);
		});
	});
	
	clone.find("img[esign='"+PhysicianName+"']").replaceWith(function(){
		if($(enableesign).val()=="1"){
			return "<img src='image/image_GetSignImg?path="+$('#signfilepath').val()+"' esign='"+PhysicianName+"' align='middle' style='width:100px;float:center' alt='img'/>";
		}
		else{
			return "<input type='button' gtype='clinicalcode' code='"+PhysicianName+"' scheme='SYSTEM' gvalue='"+PhysicianName+"' value='["+PhysicianName+"]' style='border:0px;background: #FFFFFF;'/>";
		}
	});
	
//	console.log("----"+pluginHandle.component_name_clinicalcode);
	clone.find('input:not(.textbox-text,.textbox-value),select,textarea').each(function(index,obj){
		$(obj).removeClass('valuechange');//去除修改样式
		//console.log(obj);
		if($(obj).attr('name')==pluginHandle.component_name_clinicalcode){
			if($(obj).attr('scheme')=='SYSTEM'){
				if(type!="saveStructReport"){
				if($(obj).attr('code')==PhysicianName){
					if($(enableesign).val()=="1"){
						$(obj).after("<img src='image/image_GetSignImg?path="+$('#signfilepath').val()+"' esign='"+PhysicianName+"' align='middle' style='width:100px;float:center' alt='img'/>");
		    			$(obj).remove();
					}
					else{
						$(obj).val("["+PhysicianName+"]");
					}
				}
				if(PhysicianName=="AuditPhysicianName"){
					if($(obj).attr('code')=='ReportPhysicianName'){
						if($(enableesign).val()=="1"){
							$(obj).after("<img src='image/image_GetSignImg?path="+$('#signfilepath').val()+"' esign='ReportPhysicianName' align='middle' style='width:100px;float:center' alt='img'/>");
			    			$(obj).remove();
						}
						else{
							$(obj).val("[ReportPhysicianName]");
						}
					}
					else if($(obj).attr('code')==DateTime){
						$(obj).val("["+DateTime+"]");
					}
				}
			  }
			}
		}
		else if(!$(obj).attr('for')){
			//checkbox 和radio 跳过未选中的
			if($(obj).attr('gname')==pluginHandle.component_name_checkbox){	
				if($(obj).attr('checked')!='checked'){
					return true;
				}
				//合并一组复选框的值
				var groupname=$(obj).attr('name');
				var pre_field=null;
				if(data.length>0){
					pre_field=data[data.length-1];
				}
				if(pre_field&&pre_field.cbgroupname==groupname){
					pre_field.value=pre_field.value+','+$(obj).val();
				} else{
					var field = new Object();
					field.code = $(obj).attr('code');
					field.value = $(obj).val();
					field.path = $(obj).attr('_path')||'';
					field.cbgroupname=groupname;
					data.push(field);
				}
				return true;
			} else if($(obj).attr('gname')==pluginHandle.component_name_radio){
				if($(obj).attr('checked')!='checked'){
					return true;
				}
			}
			
			var field =new Object();
			field.code = $(obj).attr('code');
			field.unit = $(obj).attr('unit');
			field.value = $(obj).val();
			field.path = $(obj).attr('_path');
			field.name = $(obj).attr('name');
			if(field.path){//如果path不为空，并且组件是在可复制章节中，获取group
				field.group=$(obj).attr('___group');
				$(obj).attr('___group','');//获取___group值后，立刻清空___group
			}
			if('SELECT'==obj.tagName){
				field.value=$(obj).attr('gvalue');
				if(!field.value){//获取默认值，某些模板中设置了默认值，但无gvalue属性
					var _objid=$(obj).attr("id");
					field.value=$('#'+_objid).combobox('getValue');
				}
			}
			field.optioncode=$(obj).attr('gvaluecode')
			data.push(field);
			
		}
	});

	clone.find('.textbox.combo,.textbox.combo.datebox').replaceWith("");
	clone.find('label.valuechange').removeClass('valuechange');//去除修改样式
	content=clone.html();
	clone.remove();
	return content;
//	console.log(data);
}
/**
 * 保存结构化报告数据
 * @param reportid    closeAfterSave:true-关闭，false-不关闭
 * @returns
 */
function saveStructReport(reportid,studyid,patientname,closeAfterSave){
   
	$('#progress_dlg').dialog('open');
	var content=$('#sr_container_' + reportid).html();
	var data = new Array(0);
	var html=beforeSaveOrAuditSR(reportid,content,data,"ReportPhysicianName","ReportDateTime","saveStructReport");
	//-----------当报告助手面板默认是收缩状态时，不保存标签等数据---------------//
	var publiclabel,privatelabel,icd10label='';
	var savelabel_flag=false;
	if($('#publiclabelct_' + reportid)[0]){
		publiclabel=$('#publiclabelct_' + reportid).combotree('getValues').join(",");
		privatelabel=$('#privatelabelct_' + reportid).combotree('getValues').join(",");
		icd10label=$('#icd10labelct_' + reportid).combobox('getValues').join(",");
		savelabel_flag=true;
	}
	//----------------------------------------------------------------------//
	$.post(window.localStorage.ctx+'/srreport/saveStructReport',
		    {
		    	'id' : $("#id_"+reportid).val(),
		    	's' : JSON.stringify(data),
		    	'studyid' : studyid,
		    	'template_id' : $("#srtemplateid_" + reportid).val(),
		    	'html' : html,
		    	'viareportid' : $("#viareportid_" + reportid).val(),
		    	'checkresult_txt' : "",//srreport.getPlainTxt(),
		    	'studyitem' : $("#studyitem_" + reportid).val(),
		    	'studyorderfk' : $("#orderid_" + reportid).val(),
		    	'publiclabel' : publiclabel,
		    	'privatelabel'  :privatelabel,
		    	'icd10label':icd10label,
		    	'savelabel_flag':savelabel_flag
		     },
		     function (res) {
	        	var json = validationDataAll(res);
	        	if(json.code==0){
//		        	if(!$('#id_' + studyid).val()){
//						$('#reporttime_' + studyid).html(json.data.reporttime);
//						$('#reportphysician_' + studyid).html(json.data.reportphysician_name);
//					}
	        		
	        		if($('#orderStatus_' + reportid).val() == myCache.ReportStatus.Noresult){
	        			$('#orderStatus_' + reportid).val(myCache.ReportStatus.Created);
	        		}
	        		var reportStatus = $('#orderStatus_' + reportid).val();
	        		afterSaveOrAuditSR(json,reportid,reportStatus,closeAfterSave);
	        	}
	        	else{
	        		$.messager.show({
						title : $.i18n.prop('error'),
						msg : $.i18n.prop('savefailed'),
						timeout : 3000,border : 'thin',showType : 'slide'
					});
		        	$('#progress_dlg').dialog('close');
	        	}
	        }
		);
	
}


/**
 * 提交结构化报告数据
 * @param reportid
 * @returns
 */
function submitStructReport(reportid,studyid,patientname){
   
	$('#progress_dlg').dialog('open');
	var content=$('#sr_container_' + reportid).html();
	var data = new Array(0);
	var html=beforeSaveOrAuditSR(reportid,content,data,"ReportPhysicianName","ReportDateTime","submitStructReport");
	//-----------当报告助手面板默认是收缩状态时，不保存标签等数据---------------//
	var publiclabel,privatelabel,icd10label='';
	var savelabel_flag=false;
	if($('#publiclabelct_' + reportid)[0]){
		publiclabel=$('#publiclabelct_' + reportid).combotree('getValues').join(",");
		privatelabel=$('#privatelabelct_' + reportid).combotree('getValues').join(",");
		icd10label=$('#icd10labelct_' + reportid).combobox('getValues').join(",");
		savelabel_flag=true;
	}
	//----------------------------------------------------------------------//
	$.post(window.localStorage.ctx+'/srreport/submitStructReport',
		    {
		    	'id':$("#id_"+reportid).val(),
		    	's' : JSON.stringify(data),
		    	'studyid' : studyid,
		    	'template_id' : $("#srtemplateid_" + reportid).val(),
		    	'html':html,
		    	'viareportid':$("#viareportid_" + reportid).val(),
		    	'checkresult_txt':"",//srreport.getPlainTxt(),
		    	'studyitem':$("#studyitem_" + reportid).val(),
		    	'studyorderfk':$("#orderid_" + reportid).val(),
		    	'publiclabel' : publiclabel,
		    	'privatelabel'  :privatelabel,
		    	'icd10label':icd10label,
		    	'savelabel_flag':savelabel_flag
		     },
		     function (res) {
	        	var json = validationDataAll(res);
	        	
	        	if(json.code==0){
//		        	if(!$('#id_' + studyid).val()){
//						$('#reporttime_' + studyid).html(json.data.reporttime);
//						$('#reportphysician_' + studyid).html(json.data.reportphysician_name);
//					}
	        		var reportStatus = json.data.reportstatus;
	        		afterSaveOrAuditSR(json,reportid,reportStatus,false);
	        	}
	        	else{
	        		$.messager.show({
						title : $.i18n.prop('error'),
						msg : $.i18n.prop('savefailed'),
						timeout : 3000,
						border : 'thin',
						showType : 'slide'
					});
		        	$('#progress_dlg').dialog('close');
	        	}
	        }
		);
	
}

/**
 * 初审结构化报告数据
 * @param reportid
 * @returns
 */
function auditPreStructReport(reportid,studyid,patientname){
   
	$('#progress_dlg').dialog('open');
	var content=$('#sr_container_' + reportid).html();
	var data = new Array(0);
	var html=beforeSaveOrAuditSR(reportid,content,data,"ReportPhysicianName","ReportDateTime","auditPreStructReport");
	//-----------当报告助手面板默认是收缩状态时，不保存标签等数据---------------//
	var publiclabel,privatelabel,icd10label='';
	var savelabel_flag=false;
	if($('#publiclabelct_' + reportid)[0]){
		publiclabel=$('#publiclabelct_' + reportid).combotree('getValues').join(",");
		privatelabel=$('#privatelabelct_' + reportid).combotree('getValues').join(",");
		icd10label=$('#icd10labelct_' + reportid).combobox('getValues').join(",");
		savelabel_flag=true;
	}
	//----------------------------------------------------------------------//
	$.post(window.localStorage.ctx+'/srreport/auditPreStructReport',
		    {
		    	'id':$("#id_"+reportid).val(),
		    	's' : JSON.stringify(data),
		    	'studyid' : studyid,
		    	'template_id' : $("#srtemplateid_" + reportid).val(),
		    	'html':html,
		    	'viareportid':$("#viareportid_" + reportid).val(),
		    	'checkresult_txt':"",//srreport.getPlainTxt(),
		    	'studyitem':$("#studyitem_" + reportid).val(),
		    	'studyorderfk':$("#orderid_" + reportid).val(),
		    	'publiclabel' : publiclabel,
		    	'privatelabel'  :privatelabel,
		    	'icd10label':icd10label,
		    	'savelabel_flag':savelabel_flag
		     },
		     function (res) {
	        	var json = validationDataAll(res);
	        	
	        	if(json.code==0){
//		        	if(!$('#id_' + studyid).val()){
//						$('#reporttime_' + studyid).html(json.data.reporttime);
//						$('#reportphysician_' + studyid).html(json.data.reportphysician_name);
//					}
	        		var reportStatus = json.data.reportstatus;
	        		afterSaveOrAuditSR(json,reportid,reportStatus,false);
	        	}
	        	else{
	        		$.messager.show({
						title : $.i18n.prop('error'),
						msg : $.i18n.prop('savefailed'),
						timeout : 3000,
						border : 'thin',
						showType : 'slide'
					});
		        	$('#progress_dlg').dialog('close');
	        	}
	        }
		);
	
}

//审核结构化报告
function auditStructReport(reportid,studyid,patientname) {
	
	$('#progress_dlg').dialog('open');
	var content=$('#sr_container_' + reportid).html();
	var data = new Array(0);
	var html=beforeSaveOrAuditSR(reportid,content,data,"AuditPhysicianName","AuditDatetime","auditStructReport");
	//-----------当报告助手面板默认是收缩状态时，不保存标签等数据---------------//
	var publiclabel,privatelabel,icd10label='';
	var savelabel_flag=false;
	if($('#publiclabelct_' + reportid)[0]){
		publiclabel=$('#publiclabelct_' + reportid).combotree('getValues').join(",");
		privatelabel=$('#privatelabelct_' + reportid).combotree('getValues').join(",");
		icd10label=$('#icd10labelct_' + reportid).combobox('getValues').join(",");
		savelabel_flag=true;
	}
	//----------------------------------------------------------------------//
	$.post(
		window.localStorage.ctx+'/srreport/auditStructReport',
        {
        	'id':$("#id_"+reportid).val(),
        	's' : JSON.stringify(data),
        	'studyid' : studyid,
        	'template_id' : $("#srtemplateid_" + reportid).val(),
        	'html':html,
        	'viareportid':$("#viareportid_" + reportid).val(),
        	'checkresult_txt':"",//srreport.getPlainTxt(),
        	'studyitem':$("#studyitem_" + reportid).val(),
        	'studyorderfk':$('#orderid_' + reportid).val(),
        	'publiclabel' : publiclabel,
	    	'privatelabel'  :privatelabel,
	    	'icd10label':icd10label,
	    	'savelabel_flag':savelabel_flag
        },
        function (res) {
        	var json = validationDataAll(res);
        	disableEditReport(reportid);
        	var reportStatus = json.data.reportstatus;
        	afterSaveOrAuditSR(json,reportid,reportStatus,false);
        }
	);

}

var svgarr=null;
/*
 * closeAfterSave :true -关闭报告,false-不关闭报告
 * */
function afterSaveOrAuditSR(json,reportid,reportstatus,closeAfterSave){
	
	/********************处理解剖结构示意图 begin**************************/
	svgarr=null;
	var embeds=document.embeds;
	if(embeds.length>0){
		svgarr=new Array();
		for(var i=0,len=embeds.length;i<len;i++){
			var svgdoc=document.embeds[i].getSVGDocument(),
			svg=svgdoc.querySelector("svg");
			svgarr.push(svg.outerHTML);
		}
		//console.log(svgarr);
	}
	/********************处理解剖结构示意图 end**************************/
	
	$('#id_' + reportid).val(json.data.id);
	$('#orderStatus_' + reportid).val(reportstatus);
	$('#div_toolbar_' + reportid).html(tiptoolbarhtml);
	$('#sr_container_' + reportid).panel({content:pluginHandle.addTooltipContentForSection(json.data.checkresult_html)});
	$("#contentChangeflag_" + reportid).val('');
	
//	changeReportBtnEnable(json.data.reportstatus,reportid);
	changeReportBtnEnable(reportstatus,reportid);
	$.messager.show({
		title : $.i18n.prop('tips'),
		msg : $.i18n.prop('savesuccess'),
		timeout : 3000,border : 'thin',showType : 'slide'
	});
	$('#progress_dlg').dialog('close');
	/* 
	 ******************************************************************
	 * start generate print html 
	 *******************************************************************
	 * */
	generatePrintHtml(reportid,json,closeAfterSave);
	//refreshReportInfo(reportid, reportstatus);
//	refresh();
	refreshRow(reportid);
}

/*
 * closeAfterSave :true -关闭报告,false-不关闭报告
 * */
function generatePrintHtml(reportid,json,closeAfterSave){
	
	//$('#temp_' + reportid).html(pluginHandle.component_name_radio);
//	var clone=$('#sr_container_' + reportid).clone();
	var clone=$('<div>'+json.data.checkresult_html+'</div>');
	//删除easyui的隐藏标签以及label标签
	clone.find('input,textarea,select,img,article,div.sectioncontainer,.pagebreak,.checkbox_label,.radio_label,.summary_textarea_toolbar').each(function(index,obj){
		if("IMG"==obj.tagName){
			$(obj).removeAttr('alt');
			$(obj).attr('alt',"img");
		}
		else if("ARTICLE"==obj.tagName||("DIV"==obj.tagName&&$(obj).hasClass("sectioncontainer"))){
			$(obj).attr('style',"");
			if($(obj).attr('qc')=='1'){
				$(obj).remove();
			}
		}
		else if("HR"==obj.tagName){
			$(obj).replaceWith("<div class='pageNext'></div>");
		}
		else if("LABEL"==obj.tagName||$(obj).hasClass("summary_textarea_toolbar")){
			$(obj).remove();
		}
		else{
			var isCbOrRd=false,//是否复选框和单选框
			color='',
			sep='';//复选框的分隔符
			//checkbox,radio 跳过未选中
			if($(obj).attr('gname')==pluginHandle.component_name_checkbox||$(obj).attr('gname')==pluginHandle.component_name_radio){
				if($(obj).attr('checked')!='checked'){
					if($(obj).attr('iswrap')=='1'){
						$(obj).nextAll("br:first").remove();
					}
					$(obj).remove();
				} else{
					isCbOrRd=true;
					if($(obj).attr('_separator')){
						sep=$(obj).attr('_separator');
					}
				}
				color=$(obj).attr('_color');
			}
			var value=$(obj).val(),
			code=$(obj).attr('code'),
			barwidth=0;//提取文本框右侧工具栏的宽度，需要加到span的宽度上，默认23.
			if('SELECT'==obj.tagName){
				value=$(obj).attr('gvalue');
				color=$(obj).css('color');
			} else if('TEXTAREA'==obj.tagName){
				value=value.replace(/\n/g,"<br>");
				value=value.replace(/\s/g,"&nbsp;");
				if($(obj).attr('issummary')=='true'){
					barwidth=23;
				}
			}
			//数字输入框的值超出范围
			if($(obj).hasClass('outof_normal_range_alert')){
				color='rgb(255, 0, 0);';
			}
			if($(obj).attr('fixedwidth')=='0'||$(obj).width()<=0||isCbOrRd||'TEXTAREA'==obj.tagName){
				$(obj).replaceWith('<span code="'+code+'" style="'+(color?('color:'+color+';'):'')+'>'+value+sep+'</span>');//微软雅黑, 
			} else{//组件打印时默认是固定宽度
				$(obj).replaceWith('<span code="'+code+'" style="width:'+($(obj).width()+barwidth)+'px;display:inline-block;'+(color?('color:'+color+';'):'')+'">'+value+'</span>');//微软雅黑, 
			}
		
//			$(obj).replaceWith('<span style="font-family: Microsoft YaHei; font-size: 14px;">'+value+'</span>');//微软雅黑, 
//			$(obj).remove();
		}
	});
	
	var printhtml=clone.html();
	clone.remove();
		//$('#temp_' + reportid).html();
	//$('#temp_' + reportid).empty();
	$.post(window.localStorage.ctx+"/srreport/saveStructReport_printhtml",
		{
			'id':json.data.id,
			'studyid':json.data.studyid,
			'printhtml':printhtml,
			'svgs':svgarr||[]
		},
		function(res){
			if(closeAfterSave){
				closeReportDirectly(reportid);
			}
		}
	);
}


function rejectStructReport(reportid,patientname,reportStatus){
	$('#progress_dlg').dialog('open');
	var obj={
			id : $("#id_"+reportid).val(),
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
//			refresh();
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

/**
 * 打开切换模板窗口
 * @returns
 */
function openSwitchTemplateDialog(reportid,orderid,studyid){
	$('#common_dialog').dialog(
			{
				title : $.i18n.prop('report.switchtemplate'),
				width : 500,height : 470,
				cache : false,modal : true,
				border: 'thin',
				href : window.localStorage.ctx +'/srreport/openSwitchTemplateDialog?studyid='+studyid
						+'&reportid='+reportid+'&orderid='+orderid,
				buttons:[{
					text: $.i18n.prop('ok'),
					width:80,
					handler:function(){switchSRTemplate(reportid,orderid,studyid);}
				},{
					text: $.i18n.prop('cancel'),
					width:80,
					handler:function(){$('#common_dialog').dialog('close');}
				}]
	});
	
}

function switchSRTemplate(reportid,orderid,studyid){
	var row=$('#srtemplatetable_switch').datagrid('getSelected');
	if(row!=null){
		$.messager.confirm({
			title: $.i18n.prop('confirm'),
			border:'thin',
			msg: $.i18n.prop('report.confirmswitchtemplate'),
			fn: function(r){
				if (r){
					//切换模板时需要重置报告目录
					$('#sr_container_' + reportid).attr('resetCatalog',true);
					if($('#srtemplateid_' + reportid).val()){
						$('#div_toolbar_' + reportid).html(tiptoolbarhtml);
						$('#sr_container_' + reportid).panel({content:pluginHandle.formatComponent(row.templatecontent)});
						
						$('#srtemplateid_' + reportid).val(row.id);
						$('#srtemplatename_' + reportid).val(row.srtemplatename);
						$('#common_dialog').dialog('close');
						var data=$('#via_findings_' + reportid).datagrid("getData");
						if(data&&data.rows){
							importViaDataToReport(data,reportid,studyid);
						}
					}
					else{
						var tab = $('#tab').tabs('getSelected');
						var index = $('#tab').tabs('getTabIndex',tab);
						$('#tab').tabs('update', {
							tab: tab,
							options: {
//								title: 'New Title',closable: true ,
						        onLoad : function(){
						        	$('#div_toolbar_' + reportid).html(tiptoolbarhtml);
						        	$('#sr_container_' + reportid).panel({content:pluginHandle.formatComponent(row.templatecontent)});
						        	changeReportBtnEnable($('#orderStatus_' + reportid).val(),reportid);
						        	var data=$('#via_findings_' + reportid).datagrid("getData");
									if(data&&data.rows){
										importViaDataToReport(data,reportid,studyid);
									}
						        },
								href: window.localStorage.ctx +'/srreport/structReport?orderid='+orderid+'&reportid='+reportid
									+'&studyid='+studyid+'&template_id='+row.id+"&normalreportswitchToSr=1"  // the new content URL
							}
						});
						tab.panel('refresh');
						$('#common_dialog').dialog('close');
					}
					
					$('#contentChangeflag_'+reportid).val(true)
				}
			}
		});
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

function doSearchSRTemplate(value){
	$.getJSON(window.localStorage.ctx +"/srtemplate/findSRTemplate?name="+value,function(json) {
//		var reporthistory = validationData(json);
		$('#srtemplatetable_switch').datagrid("loadData",json);
	});
}


function openDialog_SyngoviaPdf(reportid,studyid){
	//alert($('#pdf_'+studyid).val());
	var data=$('#via_findings_' + reportid).datagrid("getData");
	if(data&&data.rows&&data.rows.length>0&&data.rows[0].pdf){
		$('#viapdfdlg_' + reportid).dialog('open');
		var options = {pdfOpenParams: { view: 'Fit', page: '1' }};
		PDFObject.embed(window.localStorage.ctx +'/srreport/getPdf?studyid='+studyid, '#pdf-container_'+reportid, options);
	}
	else{
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : $.i18n.prop('report.noviapdfreport'),
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
	}
	
}


function automaticImportViaDataToReport(data,reportid,studyid){

	var importViaClipboardData=$('#sr_container_' + reportid).data('importViaClipboardData');
	var inittemplate=$('#sr_container_' + reportid).data('inittemplate');
	if(importViaClipboardData){
		importViaClipBDataToReport(data,reportid,studyid);
		$('#contentChangeflag_'+reportid).val(true);
		$('#sr_container_' + reportid).data('importViaClipboardData',false);
	} else if(inittemplate){
		importViaDataToReport(data,reportid,studyid);
		$('#contentChangeflag_'+reportid).val(true);
		$('#sr_container_' + reportid).data('inittemplate',false);
	} else{
		var viareportid=$("#viareportid_"+reportid).val();
		if(data.rows.length>0&&data.rows[0].viareportid!=viareportid){
			if($('#orderStatus_'+reportid).val()!="F"){
			
				$.messager.confirm({
					title: $.i18n.prop('confirm'),
					ok: $.i18n.prop('report.yes'),
					border:'thin',
					cancel: $.i18n.prop('report.no'),
					msg: $.i18n.prop('report.receiveviasrimportintoreport'),
					fn: function(r){
						if (r){
							importViaDataToReport(data,reportid,studyid);
							$('#contentChangeflag_'+reportid).val(true)
						}
					}
				});
			
			}
		}
	}
}

function manualImportViaDataToReport(reportid,studyid){
	var data=$('#via_findings_'+reportid).datagrid("getData");
	if(data&&data.rows&&data.rows.length>0){
		$.messager.confirm({
			title: $.i18n.prop('confirm'),
			ok: $.i18n.prop('report.yes'),
			border:'thin',
			cancel: $.i18n.prop('report.no'),
			msg: $.i18n.prop('report.wetherimportviadatatoreport'),
			fn: function(r){
				if (r){
					importViaDataToReport(data,reportid,studyid);
					$('#contentChangeflag_'+reportid).val(true)
				}
			}
		});
	}
	else{
		$.messager.show({title : $.i18n.prop('alert'),msg : $.i18n.prop('report.donotreceiveviadataunableimport'),timeout : 3000,border:'thin',showType : 'slide'});
	}
	

}

function importViaDataToReport(data,reportid,studyid){
	
	console.log(reportid)
	
	$('#progress_dlg').dialog('open');

	if(data.rows.length>0){
		console.log(data.rows[0].viareportid);
		$("#viareportid_"+reportid).val(data.rows[0].viareportid);
	}
	
	var codes="";
	
	$('#sr_container_'+reportid).find("input[name='"+pluginHandle.component_name_clinicalcode+"'],"+
											"[name='"+pluginHandle.component_name_finding+"'],"+
											"[name='"+pluginHandle.component_name_snapshot+"'],article,img[name='"+pluginHandle.component_name_barcode+"']").each(function(index,obj){
		var name=$(obj).attr('name');
		console.log("name="+name);
		
		if(name==pluginHandle.component_name_clinicalcode){
			if($(obj).attr('scheme')!="SYSTEM"){
				codes+=$(obj).attr('code')+",";
			}
		}
		else if(name==pluginHandle.component_name_barcode){
			codes+=$(obj).attr('code')+",";
		}
		else if(name==pluginHandle.component_name_finding){
			var findingname=$(obj).attr('gvalue');
			var belongreport=$(obj).attr('report');
			console.log('findingname='+findingname+';belongreport='+belongreport);
			var arc=null;
			if(obj.tagName=="INPUT"){
				arc=pluginHandle.createFindingComponent(obj);
				$(obj).after(arc);
				$(obj).remove();
			}
			else{
				$(obj).empty();
				arc=obj;
			}
			$.each(data.rows, function (index, row) {
				if(row.findingname==findingname){
					console.log('row.findingname='+row.findingname);
					console.log('row.htmlsr='+row.htmlsr);
					$(arc).append(pluginHandle.formatComponent(row.htmlsr));
				}
		    });
		}
		else if(name==pluginHandle.component_name_snapshot){
			var imgdata=$('#via_images_'+reportid).datagrid('getData');
			if(imgdata&&imgdata.rows){
				var imghtml="";
				for(var i=0;i<imgdata.rows.length;i++) {
					var row=imgdata.rows[i];
					
					console.log("filter_width_=="+$('#filter_width_'+reportid).val()+";row.width="+row.width+";row.height="+row.height);
					if($('#filter_width_'+reportid).val()){
						if(row.width<$('#filter_width_'+reportid).val()){
							continue;
						}
					}
					
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
						
						imghtml+="<div class='mydiv-inline' style='"+imgwidth+imgheight+";'><img src='image/image_GetViaImage?id=" + row.imageid + "' class='via_snapshot' align='middle' style='"+imgwidth+imgheight+
							"float:center;margin-right: 3px;margin-bottom: 3px;' alt='img'/></div>";
				}
				
				var arc=null;
				if(obj.tagName=="INPUT"){
					arc=pluginHandle.createSnapshotComponent(obj);
					$(obj).after(arc);
					$(obj).remove();
				}
				else{
					$(obj).empty();
					arc=obj;
				}
				$(arc).html(imghtml);
			}
		}
	});
	
	
	if(codes){
		$.getJSON(window.localStorage.ctx +"/srreport/getSyngoviaSRData?studyid="+studyid+"&codes="+codes,function(json) {
			$('#sr_container_'+reportid).find("input[name='"+pluginHandle.component_name_clinicalcode+"'][scheme!='SYSTEM'],img[name='"+pluginHandle.component_name_barcode+"']").each(function(index,obj){
				var value=getValueFromJson($(obj).attr('code'),json);
				//console.log(value);
				if(value){
					//console.log(obj.tagName)
					if(obj.tagName=="INPUT"){
						$(obj).val(value);
						$(obj).html(value);
						$(obj).attr('data-clipboard-text',value);
					}
					else{
						console.log($(obj).attr("name"))
						
						var barwidth=$(obj).attr("barwidth"),
						barheight=$(obj).attr("barheight"),
						barcodetype=$(obj).attr('barcodetype'),
						outputtype=$(obj).attr('outputtype'),
						barcodecolor=$(obj).attr('barcodecolor'),
						showhri=$(obj).attr('showhri');
						
						console.log(barwidth)
						var container=$("<div></div>");
						
						$(obj).replaceWith(container);
						container.barcode(value, barcodetype,{
					          output:outputtype,       //渲染方式 css/bmp/svg/canvas
					          //bgColor: '#ff0000', //条码背景颜色
					          color: barcodecolor,   //条码颜色
					          barWidth: barwidth,        //单条条码宽度
					          barHeight: barheight,     //单体条码高度
					          // moduleSize: 5,   //条码大小
					          // posX: 10,        //条码坐标X
					          // posY: 5,         //条码坐标Y
					          showHRI:"true"==showhri?true:false,
					          addQuietZone: false  //是否添加空白区（内边距）
					        })
//						arc=obj;
					}
				}
			});

			$('#progress_dlg').dialog('close');
			
		});
	}
	
	addSN2Snapshot($('#sr_container_'+reportid));
	$('#progress_dlg').dialog('close');
	pluginHandle.addListenerOnSRContainer($('#sr_container_'+reportid),"article[name='snapshot']");
}

function addSN2Snapshot(container){
	//var index = 1;
	 var backgroundColor = "red";
	container.find(".via_snapshot").each(function(index,obj){
		console.log(obj)
		console.log($(obj).position())
		
		 var num = "<span style='position:absolute; width:15px; height:15px; left:0; top:0; text-align:center; line-height:15px; font-size:13px; color:#fff; background-color:" + 
		 backgroundColor + ";'>" + (index+1) + "</span>";
        $(obj).parent("div").css("position", "relative").append(num);
        //index++;
	});
	
	/* $(function () {
    var index = 1;
    var backgroundColor = "red";
    $(".over2f").each(function () {
        backgroundColor = index > 3 ? "blue" : "red";
        var num = "<span style='position:absolute; width:15px; height:15px; left:0; top:0; text-align:center; line-height:15px; font-size:12px; color:#fff; background-color:" + backgroundColor + ";'>" + index + "</span>";
        $(this).children(".over2fpic").css("position", "relative").append(num);
        index++;
    });
});*/
}

function importViaDataToReport_clinicalcode(reportid,studyid){
	console.log(reportid)
	$('#progress_dlg').dialog('open');
	var codes="";
	$('#sr_container_'+reportid).find("input[name='"+pluginHandle.component_name_clinicalcode+"'],"+
											"input[name='"+pluginHandle.component_name_finding+"'],"+
											"input[name='"+pluginHandle.component_name_snapshot+"'],img[name='"+pluginHandle.component_name_barcode+"']").each(function(index,obj){
		var name=$(obj).attr('name');
		console.log("name="+name);
		if(name==pluginHandle.component_name_clinicalcode){
			if($(obj).attr('scheme')!="SYSTEM"){
				codes+=$(obj).attr('code')+",";
			}
		} else if(name==pluginHandle.component_name_finding){
			var arc=pluginHandle.createFindingComponent(obj);
			$(obj).after(arc);
			$(obj).remove();
		} else if(name==pluginHandle.component_name_snapshot){
			var arc=pluginHandle.createSnapshotComponent(obj);
			$(obj).after(arc);
			$(obj).remove();
		} else if(name==pluginHandle.component_name_barcode){
			codes+=$(obj).attr('code')+",";
		}
	});
	if(codes){
		$.getJSON(window.localStorage.ctx +"/srreport/getSyngoviaSRData?studyid="+studyid+"&codes="+codes,function(json) {
			$('#sr_container_'+reportid).find("input[name='"+pluginHandle.component_name_clinicalcode+"'][scheme!='SYSTEM'],img[name='"+pluginHandle.component_name_barcode+"']").each(function(index,obj){
				var value=getValueFromJson($(obj).attr('code'),json);
				//console.log(value);
				if(value){
					//console.log(obj.tagName)
					if(obj.tagName=="INPUT"){
						$(obj).val(value);
						$(obj).html(value);
						$(obj).attr('data-clipboard-text',value);
					}
					else{
						console.log($(obj).attr("name"))
						
						var barwidth=$(obj).attr("barwidth"),
						barheight=$(obj).attr("barheight"),
						barcodetype=$(obj).attr('barcodetype'),
						outputtype=$(obj).attr('outputtype'),
						barcodecolor=$(obj).attr('barcodecolor'),
						showhri=$(obj).attr('showhri');
						
						console.log(barwidth)
						var container=$("<div></div>");
						
						$(obj).replaceWith(container);
						container.barcode(value, barcodetype,{
					          output:outputtype,       //渲染方式 css/bmp/svg/canvas
					          //bgColor: '#ff0000', //条码背景颜色
					          color: barcodecolor,   //条码颜色
					          barWidth: barwidth,        //单条条码宽度
					          barHeight: barheight,     //单体条码高度
					          // moduleSize: 5,   //条码大小
					          // posX: 10,        //条码坐标X
					          // posY: 5,         //条码坐标Y
					          showHRI:"true"==showhri?true:false,
					          addQuietZone: false  //是否添加空白区（内边距）
					        })
//						arc=obj;
					}
				}
			});

			$('#progress_dlg').dialog('close');
		});
	}
	$('#progress_dlg').dialog('close');
}

function getValueFromJson(code,json){
	var ret=null;
	//console.log("json="+json)
	$.each(json, function (index, obj) {
//		alert(code+"---"+obj.code);
        if(code==obj.code){
        	console.log("obj.code="+obj.code+";obj.unit="+obj.unit)
        	ret=obj.value+" "+(obj.unit||"");
        	
        	return false;
        }
    });
	
	return ret;
}
//function test(studyid){
//	var srreport = UE.getEditor('structTemplet_'+ studyid);
//	
//	var html = srreport.getContent();
//	alert(html)
////	alert($('#structTemplet_'+studyid).find('input').size())
////	alert($('#structTemplet_'+studyid).find('select').size())
//	
//	$('#temp_'+studyid).html(html);
//	$('#temp_'+studyid).find('input').each(function(index,obj){
//		var field = new Object();
//		field.fieldCode = $(obj).attr('code');
//		field.fieldValue = obj.value;
//		
//		alert($(obj).attr('code')+"--"+$(obj).val());
//	});
//	
//	$('#temp_'+studyid).find('select').each(function(index,obj){
//		var field = new Object();
//		field.fieldCode = $(obj).attr('code');
//		field.fieldValue = obj.value;
//		
//		alert($(obj).attr('code')+"-88888-"+$(obj).attr('gvalue')+"===="+$(obj).attr('gvaluecode'));
//	});
//}
//
//
//function test1(studyid){
//	var htmlstr=UE.getEditor('structTemplet_'+studyid).getContent();
//
//	var	html=$(html);
//	
//	
//	html.find('select').each(function(index,obj){
//		
//		console.log($(obj).attr('code'));
//	});
//	
//	
//}


function loadImages(data,studyid,reportid){
	
	$('#via_images_'+reportid).datagrid({
        view: cardview
	});
	
	$.getJSON(window.localStorage.ctx+"/srreport/getFindingImages?studyid="+studyid,function(json) {
	
		$('#via_images_'+reportid).datagrid('loadData',json);
	});
}

function enableImportBtn(data,reportid){
//	console.log("data="+data.rows.length);
	
	if(data.rows.length>0 && myCache.ReportStatus.FinalResults!=$('#orderStatus_'+reportid).val()){
		//$('#importbtn_'+studyid).menu('enable');
		$('#moremenu_'+reportid).menu('enableItem', $('#importbtn_'+reportid));
	}
}

function normalReport(reportid,orderid,studyid,reporttype,message){
	$.messager.confirm({
		title: $.i18n.prop('confirm'),
		border:'thin',
		msg: $.i18n.prop('report.confirmchangetonormalreport')+"<br/> "+(message||''),
		fn: function(r){
			if (r){			
					var tab = $('#tab').tabs('getSelected');
					//var index = $('#tab').tabs('getTabIndex',tab);
					$('#tab').tabs('update', {
						tab: tab,
						options: {
//							title: 'New Title',closable: true ,
					        onLoad : function(){
					        	getJSON(window.localStorage.ctx+'/report/findReportByid',{reportid:reportid},function(data){
					        		var desc='',result='';
					        		if(!data.template_id){
					        			desc=data.checkdesc_html || '';
					        			result=data.checkresult_html || '';
					        		}
						        	UM.delEditor('desc_'+ reportid+ '_html');
									var ue_desc = UM.getEditor('desc_'+ reportid+ '_html');
									ue_desc.ready(function() {	
										// 设置编辑器的内容
										ue_desc.setContent(desc);
										ue_desc.addListener('contentChange',function(){
						        			$('#contentChangeflag_' + reportid).val(true);
						            	});
									});
									UM.delEditor('result_'+ reportid+ '_html');
									var ue_result = UM.getEditor('result_'+ reportid+ '_html');
									ue_result.ready(function() {
										// 设置编辑器的内容
										ue_result.setContent(result);
										ue_result.addListener('contentChange',function(){
						        			$('#contentChangeflag_' + reportid).val(true);
						            	});	
									});
					        	});
					        	$('#contentChangeflag_' + reportid).val(true);
					        	changeReportBtnEnable(myCache.ReportStatus.Noresult,reportid);
					        },
							href: window.localStorage.ctx+'/report/report?orderid='+orderid + '&reportid='+reportid
								+'&studyid='+ studyid+'&height='+$('#worklist_layout_id').height()+'&reporttype='+(reporttype||'0')  // the new content URL
						}
					});
					tab.panel('refresh');
					
			}
		}
	});
}


function importSnapshot(reportid){
	
	if($('#orderStatus_' + reportid).val()==myCache.ReportStatus.FinalResults){
		
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : '报告已经审核，无法导入图像！',
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
		return;
	}
	
	var imgdata=$('#via_images_'+reportid).datagrid('getData');
	if(imgdata&&imgdata.rows){
		$('#sr_container_' + reportid).find("article[name='"+pluginHandle.component_name_snapshot+"']").each(function(index,obj){
		
		
			var imghtml="";
			for(var i=0;i<imgdata.rows.length;i++) {
				var row=imgdata.rows[i];
				
				console.log("filter_width_=="+$('#filter_width_'+reportid).val()+";row.width="+row.width)
				if($('#filter_width_'+reportid).val()){
					if(row.width<$('#filter_width_'+reportid).val()){
						continue;
					}
				}
				
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
					
					imghtml+="<img src='image/image_GetViaImage?id=" + row.imageid + "' align='middle' style='"+imgwidth+imgheight+"float:center;margin-right: 3px;margin-bottom: 3px;' alt='img'/>";
				
			}
			
			var arc=null;
			if(obj.tagName=="INPUT"){
				arc=pluginHandle.createSnapshotComponent(obj);
				$(obj).after(arc);
				$(obj).remove();
			}
			else{
				$(obj).empty();
				arc=obj;
			}
			$(arc).html(imghtml);
		
		});
	}
}

function importViaData_FromClipboard_ToReport(uuid,source,studyid,studyinsuid,reportid){

	getJSON(window.localStorage.ctx +"/srreport/importViaDataFromClipboard",
		{
			uuid:uuid,
			source:source,
			studyid : studyid,
			studyinsuid : studyinsuid
		},
		function(json) {
			console.log(json)
			if(json.code==0){
				$('#sr_container_' + reportid).data('importViaClipboardData',true);
				$('#via_findings_' + reportid).datagrid('reload');
			} else{
				_message($.i18n.prop('savefailed'));
			}
	});
	
}
function importViaClipBDataToReport(data,reportid,studyid){
	console.log(reportid)
	$('#progress_dlg').dialog('open');
	if(data.rows.length>0){
		console.log(data.rows[0].viareportid);
		$("#viareportid_"+reportid).val(data.rows[0].viareportid);
	}

	$('#sr_container_'+reportid).find("input[name='"+pluginHandle.component_name_finding+"'],article").each(function(index,obj){
		var name=$(obj).attr('name');
		console.log("name="+name);
		if(name==pluginHandle.component_name_finding){
			var findingname=$(obj).attr('gvalue');
			var belongreport=$(obj).attr('report');
			console.log('findingname='+findingname+';belongreport='+belongreport);
			var arc=null;
			if(obj.tagName=="INPUT"){
				arc=pluginHandle.createFindingComponent(obj);
				$(obj).after(arc);
				$(obj).remove();
			}
			else{
				$(obj).empty();
				arc=obj;
			}
			$.each(data.rows, function (index, row) {
				if(row.findingname==findingname){
					console.log('row.findingname='+row.findingname);
					console.log('row.htmlsr='+row.htmlsr);
					$(arc).append(pluginHandle.formatComponent(row.htmlsr));
				}
		    });
		}
		
	});
	$('#progress_dlg').dialog('close');
}

function importAIDataToReport(reportid,row){
	var pretable,table;
	$('#sr_container_'+reportid).find("article[name='"+pluginHandle.component_name_finding+"'][gvalue='UNITEDAI LUNG PRE']").each(function(index,obj){
		console.log(obj)
		$(obj).empty();
		$(obj).append(pluginHandle.formatComponent(row.htmlsr));
		pretable=$(obj).children("table").first();
	});
	
	$('#sr_container_'+reportid).find("article[name='"+pluginHandle.component_name_finding+"'][gvalue='UNITEDAI LUNG']").each(function(index,obj){
		table=$(obj).children("table").first();
	});
	console.log(pretable)
	console.log(table)
	if(table&&pretable){
		table.find('tr').each(function(i,obj){                   
			console.log(obj)
			if(i>=2){
				var tdcount=$(obj).find('td').length;
				var left = new Number($(obj).find('td:eq('+(tdcount-3)+')').first().html());
				var right = new Number($(obj).find('td:eq('+(tdcount-2)+')').first().html());
				var all = new Number($(obj).find('td:eq('+(tdcount-1)+')').first().html());
//				console.log($(obj).find('td:eq('+(tdcount-3)+')').first().html())
//				console.log($(obj).find('td:eq('+(tdcount-2)+')').first().html())
//				console.log($(obj).find('td:eq('+(tdcount-1)+')').first().html())
				
				var pre_left = new Number(pretable.find('tr:eq('+i+') td:eq(0)').first().html());
				var pre_right = new Number(pretable.find('tr:eq('+i+') td:eq(1)').first().html());
				var pre_all = new Number(pretable.find('tr:eq('+i+') td:eq(2)').first().html());
//				console.log(pretable.find('tr:eq('+i+') td:eq(0)').first().html())
//				console.log(pretable.find('tr:eq('+i+') td:eq(1)').first().html())
//				console.log(pretable.find('tr:eq('+i+') td:eq(2)').first().html())
				
				pretable.find('tr:eq('+i+') td:eq(3)').first().html(((left-pre_left)*100/pre_left).toFixed(1));
				pretable.find('tr:eq('+i+') td:eq(4)').first().html(((right-pre_right)*100/pre_right).toFixed(1));
				pretable.find('tr:eq('+i+') td:eq(5)').first().html(((all-pre_all)*100/pre_all).toFixed(1));
			}
		});
	}
}
