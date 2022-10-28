/**
 * 
 */

$(document).ready(function(){
	var clipboard = new ClipboardJS("input[name='clinicalcode']");
});

function select_onchange_multiple_handle(obj){
//	console.log('select_onchange_multiple_handle')
	var text=obj.combobox('getValues');
	var values=[];var codes=[];
	for(var i=0;i<obj[0].options.length;i++){
	    if(obj[0].options[i].value&&text.indexOf(obj[0].options[i].value)>=0){
	        values.push(obj[0].options[i].value);
	        codes.push(obj[0].options[i].getAttribute('code'));
	    }
	}

	obj.attr('gvalue',values);
	obj.attr('gvaluecode',codes);
	$('#contentChangeflag_' + getReportid_Open(null)).val(true);
}


function select_onchange_handle(obj){
//	console.log('select_onchange_handle')
	if(obj[0].options.selectedIndex>-1){
		obj.attr('gvalue',obj[0].options[obj[0].options.selectedIndex].value);
		obj.attr('gvaluecode',obj[0].options[obj[0].options.selectedIndex].getAttribute('code'));
		
		var color=obj[0].options[obj[0].options.selectedIndex].getAttribute('color');
		if(color){
			$(obj).css('color',color);
			$(obj).next('span').find('input.textbox-text').css('color',color);
		} else{
			$(obj).css('color','');
			$(obj).next('span').find('input.textbox-text').css('color','');
		}
		
	} else{
		obj.attr('gvalue','');
		obj.attr('gvaluecode','');
	}
	obj.attr('selected_index',obj[0].options.selectedIndex);
	var reportid = getReportid_Open(null);
	$('#contentChangeflag_'+reportid).val(true)
	
	console.log(obj.attr('isselect'))
	if(obj.attr('isselect')=='true'){
		var operation=obj.attr("operation");
		//console.log(operation);
		if(operation&&'无操作'!=operation){
			var sectionuid=obj[0].options[obj[0].options.selectedIndex].getAttribute('sectionuid');
			var n=obj[0].options[obj[0].options.selectedIndex].value;
			_operation_handle(obj,n,operation,sectionuid,reportid);
		}
		obj.attr('isselect',false);
		obj.next('span').addClass('valuechange');
	}
}

function _operation_handle(obj,intvalue,operation,sectionuid,reportid){
	//if(sectionuid){
		$.getJSON(window.localStorage.ctx+"/srreport/getSectionByUid?uid="+sectionuid, function(json){
			var section=validationData(json);
			//if(section&&section.sectioncontent){
				var sectioncontent=section&&section.sectioncontent?section.sectioncontent:'';
				
				if(sectioncontent){
					//格式化章节内容
					sectioncontent=pluginHandle.formatComponent(sectioncontent);
					var jobj=null;
					if(section.clone==1){
						jobj=$("<article id='"+uuid()+"' name='srsection' class='easyui-tooltip' title='可右键复制删除' position='right' gtype='srsection' sectionuid='"+sectionuid+"' qc='' clone='1' style='border: 1px solid rgb(255, 255, 255); padding: 1px;'>"+sectioncontent+"</article>");
					}
					else{
						jobj=$("<div>"+sectioncontent+"</div>");
					}
					//所有组件重新生成新的id
					sectioncontent=pluginHandle.newComponentId(jobj);
//					var arr=new Array(0)
//					jobj.find("input[type!=button],select,textarea,label").each(function(index,eobj){
//						if(eobj.getAttribute('name')==pluginHandle.component_name_number){
//							var id=$(eobj).attr('id'),newid=uuid();
//							var tmp = new Object();
//							tmp.id=id;
//							tmp.newid=newid;
//							arr.push(tmp);
//							$(eobj).attr('id',newid);
//						} else if("LABEL"==eobj.tagName){//处理单选框和复选框的for属性，设置为处理单选框和复选框新的id
//							var id=$(eobj).prev("input[type='checkbox'],input[type='radio']").attr("id");
//							if(id){
//								$(eobj).attr('for',id);
//							}
//						} else{
//							$(eobj).attr('id',uuid());
//						}
//						$(eobj).attr("objuid",uuid());
//					});
//					sectioncontent=jobj.prop("outerHTML");
//					console.log(arr)
//					//因为所有组件的id重新生成，需要将表达式中的id替换为新的id
//					for(var j=0,len=arr.length;j<len;j++){
//						sectioncontent=sectioncontent.replace(new RegExp("^\{"+arr[j].id+"(?=\})","g"),"{"+arr[j].newid);
//					}
				}

				var article_section =null;
				var article_section_id=null;
				//获得显示章节的位置
				var nextobj=null;
				if('SELECT'==obj[0].tagName){
					nextobj=obj.next('span');
					article_section_id =obj.attr("objuid");
				} else{
					nextobj=obj.parent('span');
					article_section_id =nextobj.attr("objuid");
				}
				article_section =$('#'+article_section_id);
				console.log(article_section);
				
				if(operation=='showOptiopn_section'){
					
					if(article_section[0]){
						article_section.html(sectioncontent||"");
						article_section.attr("qc",section.is_qc||'');
					}
					else{
						nextobj.after("<div id='"+article_section_id+"' name='srsection' gtype='srsection' qc='"+(section.is_qc||'')+"'>"+(sectioncontent||"")+"</div>");
						article_section =$('#'+article_section_id);
					}

					if(sectioncontent){
						article_section.addClass("sectioncontainer");//css("background-color","#f2f2f2");
					}
					else{
						article_section.removeClass("sectioncontainer");//css("background-color","#ffffff");
					}
					
				}
				else if(operation=='copyOption_Nsection'){
					
					if(!isNaN(intvalue)){
						var html="";
						for(var i=0;i<intvalue;i++){
							//复制章节需要重新生成每个组件的objuid
							var jjobj=$(sectioncontent);
							sectioncontent=pluginHandle.newComponentId(jjobj);
//							,arr=new Array(0);
//							jjobj.find("input[type!=button],select,textarea,label").each(function(index,eeobj){
//								//console.log(eeobj)
//								if(eeobj.getAttribute('name')==pluginHandle.component_name_number){
//									var id=$(eeobj).attr('id'),newid=uuid();
//									var tmp = new Object();
//									tmp.id=id;
//									tmp.newid=newid;
//									arr.push(tmp);
//									$(eeobj).attr('id',newid);
//								} else if("LABEL"==eeobj.tagName){//处理单选框和复选框的for属性，设置为处理单选框和复选框新的id
//									var id=$(eeobj).prev("input[type='checkbox'],input[type='radio']").attr("id");
//									if(id){
//										$(eeobj).attr('for',id);
//									}
//								} else{
//									$(eeobj).attr('id',uuid());
//								}
//								$(eeobj).attr("objuid",uuid());
//							});
//							sectioncontent=jjobj.prop("outerHTML");
//							//因为所有组件的id重新生成，需要将表达式中的id替换为新的id
//							for(var j=0,len=arr.length;j<len;j++){
//								sectioncontent=sectioncontent.replace(new RegExp("^\{"+arr[j].id+"(?=\})","g"),"{"+arr[j].newid);
//							}
							html+="<li><div name='srsection'>"+sectioncontent+"</div></li>";
						}
						if(html){
							html="<ol>"+html+"</ol>";
						}
						
						//console.log(html);
						if(article_section[0]){
							article_section.html(html);
						}
						else{
							nextobj.after("<div id='"+article_section_id+"' name='srsection' gtype='srsection'>"+html+"</div>");
							article_section =$('#'+article_section_id);
						}
						
						if(sectioncontent){
							article_section.addClass("sectioncontainer");//css("background-color","#f2f2f2");
						}
						else{
							article_section.removeClass("sectioncontainer");//css("background-color","#ffffff");
						}
				    } else{
				        alert(val +"不是数字");
				    }
				}
				$.parser.parse($('#'+article_section_id));
				reg_formula_dom($('#sr_container_'+reportid),'#'+article_section_id);//注册表达式，已使其生效
				if(section&&section.clone==1){
					pluginHandle.addListenerOnSRContainer($('#sr_container_'+reportid),'#'+article_section_id+" article[name='srsection'][clone='1'],[name='snapshot']");
				}
			//}
	    });
	//}
}

function select_onclick_handle(obj,record){
	console.log(obj)
	obj.attr('isselect',true);
}


function select_onload_multiple_handle(obj){
	
	if(obj.attr('gvalue')){
		obj.combobox('setValues',obj.attr('gvalue'))
	}
}

function select_onload_handle(obj){
	if(obj.attr('gvalue')){
		obj.combobox('setValue',obj.attr('gvalue'))
	}
}

function textarea_onchange_handle(obj){
	$(obj).addClass('valuechange');
	obj.setAttribute('value',obj.value);
	obj.innerHTML=obj.value;
	$('#contentChangeflag_'+getReportid_Open(null)).val(true);
}

function text_onchange_handle(obj){
	$(obj).addClass('valuechange');
	obj.setAttribute('value',obj.value);
	$('#contentChangeflag_' + getReportid_Open(null)).val(true);
}

function datetime_onchange_handle(obj){
	$(obj).next('span').addClass('valuechange');
	obj.setAttribute('value',obj.value);
	obj.setAttribute('gvalue',obj.value);
	obj.setAttribute('gvaluecode',obj.getAttribute('code'));
	$('#contentChangeflag_' + getReportid_Open(null)).val(true);
}

function checkbox_onchange_handle(checked,obj){
	$(obj).next('label').addClass('valuechange');
	if(checked){
		$(obj).attr('checked','checked');
		//有互斥属性，去掉其他选中
		var mutex=$(obj).attr('mutex');
		var name=$(obj).attr('name');
		if(mutex&&mutex=='1'){
			$(obj).nextAll("input[name='"+name+"']").removeAttr('checked').next('label').css('color','');
			$(obj).prevAll("input[name='"+name+"']").removeAttr('checked').next('label').css('color','');
		} else{//没有互斥属性，去掉具有互斥属性选择框的选中
			$(obj).nextAll("input[name='"+name+"'][mutex='1']").removeAttr('checked').next('label').css('color','');
			$(obj).prevAll("input[name='"+name+"'][mutex='1']").removeAttr('checked').next('label').css('color','');
		}
		
		var color=$(obj).attr('_color');
		if(color){
			$(obj).next('label').css('color',color);
		}
	} else{
		$(obj).removeAttr('checked');
		$(obj).next('label').css('color','');
	}
	$('#contentChangeflag_' + getReportid_Open(null)).val(true);
}

function radio_onchange_handle(checked,obj){
	$(obj).next('label').addClass('valuechange');
	console.log(checked)
	var reportid = getReportid_Open(null);
	$('#contentChangeflag_'+reportid).val(true);
	
	$("input:radio[name='"+$(obj).attr('name')+"']:not(#"+obj.id+")").removeAttr('checked')[0].checked=false;
	$("input:radio[name='"+$(obj).attr('name')+"']:not(#"+obj.id+")").next('label').css('color','');
	if(checked){
		$(obj).attr('checked',true);
		var color=$(obj).attr('_color');
		if(color){
			$(obj).next('label').css('color',color);
		}
		var operation=$(obj).attr('operation');
		var sectionuid=$(obj).attr('sectionuid');
		if(operation){
			_operation_handle($(obj),$(obj).val(),operation,sectionuid,reportid);
		}
	} else{
		$(obj).removeAttr('checked');
		$(obj).next('label').css('color','');
	}
}

function number_onchange_handle(obj){
	$(obj).addClass('valuechange');
	obj.setAttribute('value',obj.value);
	//判断是否超出正常值范围
	var min=$(obj).attr("min"),max=$(obj).attr("max");
	if((min&&parseInt(obj.value)<parseInt(min))||(max&&parseInt(obj.value)>parseInt(max))){
		$(obj).addClass('outof_normal_range_alert');
	} else{
		$(obj).removeClass('outof_normal_range_alert');
	}
	
	var reportid = getReportid_Open(null);
	$('#contentChangeflag_' + reportid).val(true);
	try{
		//$('#sr_submit_' + reportid).click();
	} catch(e){
		console.log(e);
	}
	if(!$(obj).attr('formula')||trim($(obj).attr('formula'))==''){
		var id=$(obj).attr('id');
		var formulaData=$('#sr_container_' + reportid).data('formulaData');
		if(formulaData){
			var array=formulaData.get(id);
			if(array){
				for(var i=0,len=array.length;i<len;i++){
					$('#'+array[i])[0].dispatchEvent(formula_evt);//触发相应组件的计算事件
				}
			}
		}
	}
}

var textarea=null;
var tiptoolbarhtml="";
function getTooltipContent(obj){
	return '';
//	textarea=obj;
//	var reportid = getReportid_Open(null);
//	tiptoolbarhtml=$('#tips_toolbar_'+reportid).prop("outerHTML");
//	console.log($('#tips_toolbar_'+reportid))
	//return $('#tips_toolbar_${reportid}');
//	return "<div><img src='image/tiqu.png' style='CURSOR: pointer' onclick='extract_Summary();'></div>"
	//return "<div><a href='#' onclick='extract_Summary();'>提取内容</a></div>";
	//return "<div><a class='easyui-linkbutton easyui-tooltip' data-options=\"iconCls:'icon-add',plain:true\" onclick='extract_Summary();'></div>";//<i class='iconfont icon-info'></i>提取内容</a>
	
	//return $('#toolbar_srreport_for_summary');
}

function extract_Summary(reportid){
	if(!reportid){
		reportid = getReportid_Open(null);
	}
	console.log("reportid="+reportid)
	//if(!textarea){
		$('#sr_container_' + reportid).find('textarea[issummary="true"]').each(function(index,obj){
			textarea=$(obj);
			var content=$('#sr_container_' + reportid).html();
			//$('#temp_' + reportid).html(content);
			var contentj=$("<div>"+content+"</div>");
			var summary="";
			var summary_result="";
			contentj.find('input:not(.textbox-text,.textbox-value,[type="button"]),select,textarea').each(function(index,obj){
				//console.log("----"+pluginHandle.component_name_clinicalcode)
				//console.log("name="+$(obj).attr('name')+"---"+$(obj).attr('summary'));
				if($(obj).attr('summary')){
					summary+=replaceRule.replace(obj,'summary');
					/* var value=$(obj).val();
					if('SELECT'==obj.tagName){
						value=$(obj).attr('gvalue');
					}
					if(value){
						summary+=$(obj).attr('summary').replace('@{value}',value);
					} */
				}
				if($(obj).attr('summary_result')){
					summary_result+=replaceRule.replace(obj,'summary_result');
				}
			});
			var separator="";
			if(summary&&summary_result){
				separator='\r\n\r\n\r\n';
			}
			summary=summary+separator+summary_result;
			console.log(summary);
			//textarea[0].value=summary;
			textarea.attr('value',summary);
			textarea.html(summary);
			textarea.val(summary);
			textarea.text(summary);
			console.log(textarea);
			$('#contentChangeflag_' + reportid).val(true)
			//$('#temp_' + reportid).empty();
			contentj.remove();
		});
	//}
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

function clear_Summary(reportid){
	if(!reportid){
		reportid = getReportid_Open(null);
	}
	$('#sr_container_' + reportid).find('textarea[issummary="true"]').each(function(index,obj){
		$(obj).attr('value','');
		$(obj).html('');
		$(obj).val('');
		$(obj).text('');
		$('#contentChangeflag_' + reportid).val(true)
	});
}

function setLocationComValue(com,value,code,singleselect){
	if(/select/ig.test( com.tagName )){
		if(singleselect=="true"){
			$(com).attr('isselect',true);
			$(com).combobox('setValue',value);
			$(com).attr('gvalue',value);
			$(com).attr('gvaluecode',code);
		} else{
			var values=$(com).combobox('getValues');
			if(!values.includes(value)){
				values.push(value);
				$(com).attr('isselect',true);
				$(com).combobox('setValues',values);
				$(com).attr('gvalue',values);
				var codes=$(com).attr('gvaluecode');
				var codearr=(codes?codes:'').split(',');
				if(!codearr.includes(code)){
					codearr.push(code);
					$(com).attr('gvaluecode',codearr);
				}
			}
		}
	}
}

function removeLocationComValue(com,value,code,singleselect,countcircle){
	if(/select/ig.test( com.tagName )){
		if(singleselect=="true"){
			$(com).combobox('clear');
			$(com).attr('gvalue','');
			$(com).attr('gvaluecode','');
		} else{
			if(countcircle==0){
				var values=$(com).combobox('getValues');
				var ind=values.indexOf(value);
				if(ind>=0){
					values.splice(ind,1);
					$(com).combobox('setValues',values);
					$(com).attr('gvalue',values);
					var codes=$(com).attr('gvaluecode');
					var codearr=(codes?codes:'').split(',');
					var codeind=codearr.indexOf(code);
					if(codeind>=0){
						codearr.splice(codeind,1);
						$(com).attr('gvaluecode',codearr);
					}
				}
			}
		}
	}
}

function initSvgCircle(embed){
	if(embed){
		var svgcircle=embed.getAttribute('svgcircle');
		var svgdoc=embed.getSVGDocument();
		var svg=svgdoc.querySelector("svg");
		svg.setAttribute('svgcircle',svgcircle||'');
		//console.log(svgcircle)
		if(svgcircle){
			svg.onload();
		}
	}
}

