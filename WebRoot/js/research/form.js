/**
 * 
 */
var birthdate_Flag = false;


/*
type=1 选择一个检查后录入表单数据
type=2 直接选择表单然后录入数据
type=3 从excel导入
*/
function entryForm(dataid,formid,row,type){
	console.log(row)
	var status;
	var projectid=$('#_project_id').val(),groupid=$('#selected_group_id').val();
	getJSON(window.localStorage.ctx + "/research/openForm", {
		dataid : dataid,
		formid : formid
	}, function(data) {
		console.log(data)
		if(!dataid){
			dataid=data.id;
		}
		if(!data.status){
			status="";
		}else{
			status=data.status;
		}
		
		if(!formid){
			formid=data.form_id;
		}
		console.log(dataid)
		$('#project_tabs').tabs('add',{
	        id: "tab_" + dataid,  
	        title: '实验组：'+$('#selected_group_name').val()+'--表单：'+(data.name||data.form_name),
	        closable: true ,
	        onLoad : function(){

				// 设置编辑器的内容 'openwithsrtemp' =true:以结构化报告模板直接打开，不用判断报告状态
	    			$('#sr_container_'+dataid).panel({content:pluginHandle.formatComponent(data.html)});
					//类型为excel导入且状态为导入且赋值标记为1，赋值到模板中的组件，
					if(data.type==3&&data.filldata=="1"){
						getJSON(window.localStorage.ctx + "/research/getFormData", {
							dataid : dataid
						}, function(data) {
							console.log(data)
							if(data.code==0&&data.data!=null){
								
								$('#sr_container_'+dataid).find('input:not(.textbox-text,.textbox-value),select,textarea').each(function(index,obj){
									var code=$(obj).attr("code");
									var value=data.data[code];
									console.log("code="+code+";value="+value);
									var comp_name=$(obj).attr('name')||'';
									if(value!=null){
										if(comp_name==pluginHandle.component_name_clinicalcode){
											$(obj).val(value);
											$(obj).html(value);
											$(obj).attr('data-clipboard-text',value);
										} else if(comp_name==pluginHandle.component_name_text){
											$(obj).val(value);
											$(obj).attr('value',value);
										} else if(comp_name==pluginHandle.component_name_textarea){
											$(obj).val(value);
											$(obj).attr('value',value);
											$(obj).attr('gvalue',value);
											$(obj).html(value);
											$(obj).attr('gvaluecode',code);
										} else if($(obj).hasClass('easyui-datebox')){//==pluginHandle.datetimeComponent
											$(obj).datebox('setValue',value);
											$(obj).attr('value',value);
											$(obj).attr('gvalue',value);
											$(obj).attr('gvaluecode',code);
										} else if($(obj).hasClass('easyui-datetimebox')){//==pluginHandle.datetimeComponent
											$(obj).datetimebox('setValue',value);
											$(obj).attr('value',value);
											$(obj).attr('gvalue',value);
											$(obj).attr('gvaluecode',code);
										} else if(comp_name==pluginHandle.component_name_number){
											$(obj).val(value);
											$(obj).attr('value',value);
										} else if($(obj).hasClass('easyui-combobox')){//==pluginHandle.selectComponent
											$(obj).combobox('setValue',value);
											$(obj).attr('gvalue',value);
											$(obj).attr('gvaluecode',code);
											//$(obj).attr('selected_index',0);
										} else if(comp_name.indexOf(pluginHandle.component_name_radio)>=0){
											if(value==$(obj).attr('value')){
												$(obj).attr('checked',true);
											}
										} else if(comp_name.indexOf(pluginHandle.component_name_checkbox)>=0){
											var vals=value.split(',');
											for(var i=0,len=vals.length; i<len; i++){
												if(vals[i]==$(obj).attr('value')){
													$(obj).attr('checked',true);
													break;
												}
											}
										}
									}
									
									
								});
							}
						});
					}
	        },
	        
			href : window.localStorage.ctx+'/research/formContent?formid='+formid+'&projectid='+projectid+'&groupid='+groupid+'&dataid='+dataid+"&status="+status
					+"&type="+(type||'')+"&orderid="+(row?(row.orderid||''):'')+"&dcmstudypk="+(row?(row.dcmstudypk||''):''),
	        onBeforeDestroy : function(title, index) {
//	        	UE.delEditor('structTemplet_'+ row.studyorderstudyid);
	        	$('#sr_container_' + dataid).empty();
			},
			onClose:function(title,index){
				refreshGroupInfo_Charts();
			}
	      
	    });
	});
}

/**
 * 获取reportid
 * @param tabid
 * @returns
 */
function getReportid_Open(tabid){
	if(!tabid){
		tabid=$('#project_tabs').tabs('getSelected').panel('options').id;
	}	
	return tabid.substr(tabid.lastIndexOf("_")+1);
}

//关闭表单Tab
function closeTab(dataid) {
	var tabid="tab_"+dataid;
	var index=0;
	for(var i=0; i<$('#project_tabs').tabs('tabs').length; i++){
		var id=$('#project_tabs').tabs('getTab',i).panel('options').id;
		if(id==tabid){
			index=i;
		}
	}
	if(index){
		$('#project_tabs').tabs('close', index);
	}
}

function initImgSrc(content){
	content=content.replace(/ghidden/g,"hidden='hidden'");
	content=content.replace(new RegExp("_ueditor_page_break_tag_","gm"),"<hr class='pagebreak' noshade='noshade' size='5' style='-webkit-user-select: none;'>")
	//console.log("ret="+content);
	return content;
}

function beforeSaveOrAuditFormData(dataid,content,data,PhysicianName,DateTime){
	//保存，提交，初审，审核报告时，不需要重置报告目录
	$('#sr_container_' + dataid).attr('resetCatalog',false);
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
	
//	console.log("----"+pluginHandle.component_name_clinicalcode);
	clone.find('input:not(.textbox-text,.textbox-value),select,textarea').each(function(index,obj){
		//console.log(obj);
		$(obj).removeClass('valuechange');//去除修改样式
		if($(obj).attr('name')==pluginHandle.component_name_clinicalcode){
			if($(obj).attr('scheme')=='SYSTEM'){
				if($(obj).attr('code')==PhysicianName){
//					if($(enableesign).val()=="1"){
//						$(obj).after("<img src='image/image_GetSignImg?path="+$('#signfilepath').val()+"' esign='"+PhysicianName+"' align='middle' style='width:100px;float:center' alt='img'/>");
//		    			$(obj).remove();
//					}
//					else{
						$(obj).val("["+PhysicianName+"]");
//					}
				}
				else if($(obj).attr('code')==DateTime){
					$(obj).val("["+DateTime+"]");
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
			var field = new Object();
			field.code = $(obj).attr('code');
			field.unit = $(obj).attr('unit');
			field.value = $(obj).val();
			field.path = $(obj).attr('_path')||'';
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

function getPatientInfo(info,dataid){
	info.patientname=$('#patientname_form_'+dataid).textbox('getValue');
	if(info.patientname==null||''==info.patientname){
		_message('请录入病人姓名！');
		return false;
	}
	info.patientid=$('#patientid_form_'+dataid).textbox('getValue');
	info.sex=$('#sex_form_'+dataid).val();
	info.age=$('#age_form_'+dataid).numberbox('getValue');
	if(info.age==null||''==info.age){
		_message('请录入病人年龄！');
		return false;
	}
	info.age_unit=$('#age_unit_form_'+dataid).combobox('getValue');
	info.birthdate=$('#birthdate_form_'+dataid).datebox('getValue');
	if(info.birthdate==null||''==info.birthdate){
		_message('请录入病人出生日期！');
		return false;
	}
	info.studyid=$('#studyid_form_'+dataid).textbox('getValue');
	info.study_datetime=$('#studydatetime_form_'+dataid).datetimebox('getValue');
	return true;
}

/**
 * 提交表单数据
 * @param dataid
 * @returns
 */
function submitFormData(dataid){
	$('#progress_dlg').dialog('open');
	var content=$('#sr_container_' + dataid).html();
	var type=$('#type_' + dataid).val();
	var data = new Array(0);
	var html=beforeSaveOrAuditFormData(dataid,content,data,"ReportPhysicianName","ReportDateTime");

	var info = new Object();
	if(!getPatientInfo(info,dataid)){
		$('#progress_dlg').dialog('close');
		return;
	}
	console.log(info)
	/*console.log($('#formdataform_'+ dataid).form('validate'))
	if(!$('#formdataform_'+ dataid).form('validate')){
		return;
	}*/
	/*if(type==2){
		$('#sr_container_'+dataid).find("input[name!='"+pluginHandle.component_name_clinicalcode+"'],textarea[name!='"+pluginHandle.component_name_clinicalcode+"']").each(function(index,obj){
					if($(obj).attr('code')=="00100010"){
						info.patientname=$(obj).val();
					}
					if($(obj).attr('code')=="00100040"){
						info.sex=$(obj).val();
					}
					if($(obj).attr('code')=="00100020"){
						info.patientid=$(obj).val();
					}
					if($(obj).attr('code')=="00200010"){
						info.studyid=$(obj).val();
					}
		    }); 
	}else{
		$('#sr_container_'+dataid).find("input[name='"+pluginHandle.component_name_clinicalcode+"']").each(function(index,obj){
			  console.log($(obj).attr('code'));
				if(obj.tagName=="INPUT"){
					if($(obj).attr('code')=="00100010"){
						info.patientname=$(obj).val();
					}
					if($(obj).attr('code')=="00100040"){
						info.sex=$(obj).val();
					}
					if($(obj).attr('code')=="00100020"){
						info.patientid=$(obj).val();
					}
					if($(obj).attr('code')=="00200010"){
						info.studyid=$(obj).val();
					}
				}
		    }); 
	}*/
	
	var id=$("#form_id_"+dataid).val();
	if(id.startsWith("new-")){
		id="";
	}
	$.post(window.localStorage.ctx+'/research/submitFormData',
		    {
		    	'id': id,
		    	's' : JSON.stringify(data),
//		    	'studyid' : studyid,
		    	'form_id' : $("#form_formid_" + dataid).val(),
		    	'group_id' : $("#form_groupid_" + dataid).val(),
		    	'html':html,
		    	'info':JSON.stringify(info)
//		    	'viareportid':$("#viareportid_" + dataid).val(),
//		    	'checkresult_txt':"",//srreport.getPlainTxt(),
//		    	'studyitem':$("#studyitem_" + dataid).val(),
//		    	'studyorderfk':$("#orderid_" + dataid).val()
		     },
		     function (res) {
	        	var json = validationDataAll(res);
	        	if(json.code==0){
	        		$('#form_id_' + dataid).val(json.data.id);
	        		afterSaveOrAuditFormData(json,dataid);
	        	}
	        	else{
	        		_message($.i18n.prop('savefailed')+' 错误原因：'+json.message);
		        	$('#progress_dlg').dialog('close');
	        	}
	        }
		);
	
}

function afterSaveOrAuditFormData(json,dataid){
	//console.log(json);
	$('#form_id_' + dataid).val(json.data.id);
	$('#sr_container_' + dataid).panel({content:json.data.html});
	$("#contentChangeflag_" + dataid).val('');
	_message($.i18n.prop('savesuccess'));
	$('#progress_dlg').dialog('close');
	/* 
	 ******************************************************************
	 * start generate print html 
	 *******************************************************************
	 * */
//	generatePrintHtml_formData(json);
	//refreshReportInfo(reportid, reportstatus);
	
	if(json.data.status=='22'||json.data.status=='23'){
		$('#submitbtn_'+dataid).linkbutton('enable');
		$('#audibtn_'+dataid).linkbutton('enable');
		$('#cancebtn_'+dataid).linkbutton('disable');
	} else if(json.data.status=='29'){
		$('#submitbtn_'+dataid).linkbutton('enable');
		$('#audibtn_'+dataid).linkbutton('enable');
		$('#cancebtn_'+dataid).linkbutton('disable');
	} else if(json.data.status=='31'){
		$('#submitbtn_'+dataid).linkbutton('disable');
		$('#audibtn_'+dataid).linkbutton('disable');
		$('#cancebtn_'+dataid).linkbutton('enable');
	}
	searchTestGroupDatas($('#selected_group_id').val(),null,null,null,true);
}


/*function generatePrintHtml_formData(json){
	console.log(json)
	//$('#temp_' + reportid).html(pluginHandle.component_name_radio);
//	var clone=$('#sr_container_' + reportid).clone();
	var clone=$('<div>'+json.data.html+'</div>');
	//删除easyui的隐藏标签以及label标签
	clone.find('input,textarea,select,img,article,.pagebreak,.checkbox_label,.radio_label').each(function(index,obj){
		if("IMG"==obj.tagName){
			$(obj).removeAttr('alt');
			$(obj).attr('alt',"img");
		}
		else if("ARTICLE"==obj.tagName){
			$(obj).attr('style',"");
			if($(obj).attr('qc')=='1'){
				$(obj).remove();
			}
		}
		else if("HR"==obj.tagName){
			$(obj).replaceWith("<div class='pageNext'></div>");
		}
		else if("LABEL"==obj.tagName){
			$(obj).remove();
		}
		else{
			//checkbox,radio 跳过未选中
			if($(obj).attr('gname')==pluginHandle.component_name_checkbox||$(obj).attr('gname')==pluginHandle.component_name_radio){
				if($(obj).attr('checked')!='checked'){
					$(obj).remove();
				}
			}
			var value=$(obj).val();
			if('SELECT'==obj.tagName){
				value=$(obj).attr('gvalue');
			} else if('TEXTAREA'==obj.tagName){
				value=value.replace(/\n/g,"<br>");
				value=value.replace(/\s/g,"&nbsp;");
			}
			
			if($(obj).attr('fixedwidth')=='0'||$(obj).width()<=0){
				$(obj).replaceWith('<span>'+value+'</span>');//微软雅黑, 
			} else{//组件打印时默认是固定宽度
				$(obj).replaceWith('<span style="width:'+$(obj).width()+'px;display:inline-block;">'+value+'</span>');//微软雅黑, 
			}
		
//			$(obj).replaceWith('<span style="font-family: Microsoft YaHei; font-size: 14px;">'+value+'</span>');//微软雅黑, 
//			$(obj).remove();
		}
	});
	
	var printhtml=clone.html();
	clone.remove();
		//$('#temp_' + reportid).html();
	//$('#temp_' + reportid).empty();
	$.post(window.localStorage.ctx+"/research/saveForm_printhtml",
			{
				'id':json.data.id,
				'printhtml':printhtml
			}
	);
}*/

/*function beforeSaveOrAuditSR(content,data,PhysicianName,DateTime){
	//保存，提交，初审，审核报告时，不需要重置报告目录
	$('#task_report').attr('resetCatalog',false);
	var clone=$('<div>'+content+'</div>');
	
	 clone.find("img[esign='"+PhysicianName+"']").replaceWith(function(){
		if($(enableesign).val()=="1"){
			return "<img src='image/image_GetSignImg?path="+$('#signfilepath').val()+"' esign='"+PhysicianName+"' align='middle' style='width:100px;float:center' alt='img'/>";
		}
		else{
			return '<input type="button" gtype="clinicalcode" code="ReportPhysicianName" scheme="SYSTEM" gvalue="ReportPhysicianName" value="[ReportPhysicianName]" disabled="disabled" style="border:0px;background: #EFEFEF;"/>';
		}
	}); 
	
	clone.find('input:not(.textbox-text,.textbox-value),select,textarea').each(function(index,obj){
		if($(obj).attr('name')==pluginHandle.component_name_clinicalcode){
			if($(obj).attr('scheme')=='SYSTEM'){
				if($(obj).attr('code')==PhysicianName){
					if($(enableesign).val()=="1"){
						$(obj).after("<img src='image/image_GetSignImg?path="+$('#signfilepath').val()+"' esign='"+PhysicianName+"' align='middle' style='width:100px;float:center' alt='img'/>");
		    			$(obj).remove();
					}
					else{
						$(obj).val("["+PhysicianName+"]");
					}
				}
				else if($(obj).attr('code')==DateTime){
					$(obj).val("["+DateTime+"]");
				}
			}
		}
		else if(!$(obj).attr('for')){
			//checkbox 和radio 跳过未选中的
			if($(obj).attr('gname')==pluginHandle.component_name_checkbox){
				if($(obj).attr('checked')!='checked'){
					return true;
				}
			} else if($(obj).attr('gname')==pluginHandle.component_name_radio){
				if($(obj).attr('checked')!='checked'){
					return true;
				}
			}
			var field = new Object();
			field.code = $(obj).attr('code');
			field.unit = $(obj).attr('unit');
			field.value = $(obj).val();
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
	content=clone.html();
	clone.remove();
	return content;
}*/

function print(dataid,issr,fontSize) {
	if($('#form_id_' + dataid).val()){
		 window.open(window.localStorage.ctx+"/research/printReport?dataid="+$('#form_id_' + dataid).val() +"&issr="+issr+"&fontSize="+fontSize);
	}
}
//审核报告
function auditFormData(dataid){
	$('#progress_dlg').dialog('open');
	var content=$('#sr_container_' + dataid).html();
	var data = new Array(0);
	var html=beforeSaveOrAuditFormData(dataid,content,data,"ReportPhysicianName","ReportDateTime");
	//-----------当报告助手面板默认是收缩状态时，不保存标签等数据---------------//
//	var publiclabel,privatelabel,icd10label='';
//	var savelabel_flag=false;
//	if($('#publiclabelct_' + reportid)[0]){
//		publiclabel=$('#publiclabelct_' + reportid).combotree('getValues').join(",");
//		privatelabel=$('#privatelabelct_' + reportid).combotree('getValues').join(",");
//		icd10label=$('#icd10labelct_' + reportid).combobox('getValues').join(",");
//		savelabel_flag=true;
//	}
	//----------------------------------------------------------------------//
	var info = new Object();
	if(!getPatientInfo(info,dataid)){
		$('#progress_dlg').dialog('close');
		return;
	}
	var id=$("#form_id_"+dataid).val();
	if(id.startsWith("new-")){
		id="";
	}
	$.post(window.localStorage.ctx+'/research/auditFormData',
		    {
		    	'id': id,
		    	's' : JSON.stringify(data),
//		    	'studyid' : studyid,
		    	'form_id' : $("#form_formid_" + dataid).val(),
		    	'group_id' : $("#form_groupid_" + dataid).val(),
		    	'html':html,
				'info':JSON.stringify(info)
//		    	'viareportid':$("#viareportid_" + dataid).val(),
//		    	'checkresult_txt':"",//srreport.getPlainTxt(),
//		    	'studyitem':$("#studyitem_" + dataid).val(),
//		    	'studyorderfk':$("#orderid_" + dataid).val()
		     },
		     function (res) {
	        	var json = validationDataAll(res);
	        	if(json.code==0){
	        		$('#form_id_' + dataid).val(json.data.id);
					$('#importerror_' + dataid).remove();
	        		afterSaveOrAuditFormData(json,dataid);
	        	}
	        	else{
	        		_message($.i18n.prop('savefailed')+' 错误原因：'+json.message);
		        	$('#progress_dlg').dialog('close');
	        	}
	        }
		);
}
//取消申请审核
function cancelAuditFormData(dataid){
	$('#progress_dlg').dialog('open');

	var id=$("#form_id_"+dataid).val();
	if(id.startsWith("new-")){
		id="";
	}
	$.post(window.localStorage.ctx+'/research/cancelAuditFormData',
		     {
		    	'id': id
		     },
		     function (res) {
	        	var json = validationDataAll(res);
	        	if(json.code==0){
					_message('取消审核成功！');
	        		searchTestGroupDatas($('#selected_group_id').val());
	        		//closeTab(dataid);
					$('#submitbtn_'+dataid).linkbutton('enable');
					$('#audibtn_'+dataid).linkbutton('enable');
					$('#cancebtn_'+dataid).linkbutton('disable');
	        	}
	        	else{
	        		_message($.i18n.prop('savefailed'));
	        	}
	        	$('#progress_dlg').dialog('close');
	        }
		);
}

//填写年龄获取出生日期
function calculate_age1(newValue,dataid){
	if(birthdate_Flag){
		return;
	}
	if(!isNaN(newValue)&&newValue>0){
		var ageunit=$("#age_unit_form_"+dataid).textbox("getValue");
		var str=$("#birthdate_form_"+dataid).datebox("getValue");
		var birth=getBirthday(newValue,ageunit,str);
		$("#birthdate_form_"+dataid).datebox('setValue',birth);	
	}
}
//选择单位获取出生日期
function calculate_age2(newValue,dataid){
	if(birthdate_Flag){
		return;
	}
	var value=$('#age_form_'+dataid).numberbox('getValue');
	if(value==""){
		return;
	}
	var ageunit=$("#age_unit_form_"+dataid).textbox("getValue");
	var str=$("#birthdate_form_"+dataid).datebox("getValue");
	var birth=getBirthday(value,ageunit,str);
	$("#birthdate_form_"+dataid).datebox('setValue',birth);
}

//计算出生日期
function getBirthday(value,ageunit,str){
	var date = new Date();
	var bdate=null;
	if(ageunit=="Y"){
		bdate=new Date(date.getTime()-value*1000*60*60*24*365);
		if(str!=""){
			var arr=str.split("-");
			return arr[1]+"/"+arr[2]+"/"+bdate.getFullYear();
		}
	}else if(ageunit=="M"){
		bdate=new Date(date.getTime()-value*1000*60*60*24*30);
		if(str!=""){
			var arr=str.split("-");
			return (bdate.getMonth()+1)+"/"+arr[2]+"/"+bdate.getFullYear();
		}
	}else if(ageunit=="D"){//天
		bdate=new Date(date.getTime()-value*1000*60*60*24);	
	}else if(ageunit=="W"){//周
		bdate=new Date(date.getTime()-value*1000*60*60*24*7);	
	}else if(ageunit=="H"){//小时
		if(value<=date.getHours()){
			bdate=new Date(date.getTime());
		}else{
			var count = Math.floor((value-date.getHours()-1)/24)+1;
			bdate=new Date(date.getTime()-count*1000*60*60*24);
		}
	}else{//分
		if(value>date.getHours()*60+date.getMinutes()){
			bdate=new Date(date.getTime()-1*1000*60*60*24);
		}else{
			bdate=new Date(date.getTime());
		}
	}
	return "01/01/"+bdate.getFullYear();
}

function getAgeFromBirthdate(birthdate,dataid){
	birthdate_Flag = true;
	var tmp=new Date();
	var today=new Date(tmp.getFullYear(),tmp.getMonth(),tmp.getDate());
	if(birthdate.getTime()==today.getTime()){
		$("#age_form_"+dataid).numberbox("setValue",0);
		$("#age_unit_form_"+dataid).combobox('select', 'D');
		$("#age_form_"+dataid).numberbox("setValue",1);
		$("#age_form_"+dataid).numberbox({onChange:function(newValue,oldValue){
			calculate_age1(newValue,dataid);
		}});
		return;
	}
	var year=today.getFullYear()-birthdate.getFullYear();
	if(year>0){
		var month=today.getMonth()-birthdate.getMonth();
		if(month<0){
			year = year - 1;
		}else if(month == 0 && today.getDate()-birthdate.getDate()<0){
			year = year - 1;
		}
		$("#age_form_"+dataid).numberbox("setValue",year);
		$("#age_unit_form_"+dataid).combobox('select', 'Y');
	}else if(year==0){
		var month=today.getMonth()-birthdate.getMonth();
		if(month>0){
			$("#age_form_"+dataid).numberbox("setValue",month);
			$("#age_unit_form_"+dataid).combobox('select', 'M');
		}
		else if(month==0){
			var day=today.getDate()-birthdate.getDate();
			if(day>0){
				$("#age_form_"+dataid).numberbox("setValue",day);
				$("#age_unit_form_"+dataid).combobox('select', 'D');
			}
			else{
				$("#age_form_"+dataid).numberbox("setValue",1);
				$("#age_unit_form_"+dataid).combobox('select', 'D');
			}
		}
	}
	birthdate_Flag = false;
}

