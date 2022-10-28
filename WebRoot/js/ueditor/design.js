/**
 * 
 */

function openTestComponentDlg(textareaid,summary_attr){
	console.log(textareaid)
//	console.log(oNode)
	$('#test_component_dialog').dialog(
		{
			title : '测试提取表达式',
			width : 500,height : 620,border: 'thin',
			closed : false,closable : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			href : 'js/ueditor/formdesign/testComponent.jsp',
			buttons:[{
				text: '测试',
				width:80,
				handler:function(){testExpression(summary_attr)}
			},{
				text: $.i18n.prop('save'),
				width:80,
				handler:function(){saveExpression(textareaid);}
			},{
				text: $.i18n.prop('cancel'),
				width:80,
				handler:function(){$('#test_component_dialog').dialog('close');}
			}],
			onLoad:function(){
				$('#summary_attr').val(summary_attr);
//				console.log($('#'+textareaid).val())
				var com=pluginHandle.formatComponent(oNode.outerHTML);
//				console.log(com)
				
				$('#component_container').append(com).find('span.summary_textarea_toolbar').each(function(index,obj){
					$(obj).remove();
				});
				$.parser.parse('#component_container');
				$('#expression_ta').textbox('setValue',$('#'+textareaid).val());
			}
			
	});
}

function testExpression(summary_attr){
	if($('#expression_ta').textbox('getValue')==''){
		$('#test_result').css('color','red').html('请输入表达式！');
		return;
	}
	
	$('#test_result').html('');
	var res="";
	$('#component_container').find('input:not(.textbox-text,.textbox-value,[type="button"]),select,textarea').each(function(index,obj){
		var summary=replaceRule.replace(obj,summary_attr);
		
		res+=(summary||'');
		console.log(summary);
	});
	
	$('#test_result').css('color','green').html(res);
}

function expressionChange(newValue,oldValue){
	console.log(newValue)
	$('#component_container').find('input:not(.textbox-text,.textbox-value,[type="button"]),select,textarea').each(function(index,obj){
		$(obj).attr($('#summary_attr').val(),newValue);
	});
}

function saveExpression(textareaid){
	$('#'+textareaid).textbox('setValue',$('#expression_ta').val());
	$('#test_component_dialog').dialog('close');
}

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
}


function select_onchange_handle(obj){
	console.log(obj[0].options.selectedIndex)
	if(obj[0].options.selectedIndex>-1){
		obj.attr('gvalue',obj[0].options[obj[0].options.selectedIndex].value);
		obj.attr('gvaluecode',obj[0].options[obj[0].options.selectedIndex].getAttribute('code'));
	} else{
		obj.attr('gvalue','');
		obj.attr('gvaluecode','');
	}
	obj.attr('selected_index',obj[0].options.selectedIndex);
	
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
}

function text_onchange_handle(obj){
	$(obj).addClass('valuechange');
	obj.setAttribute('value',obj.value);
}

function datetime_onchange_handle(obj){
	$(obj).next('span').addClass('valuechange');
	obj.setAttribute('value',obj.value);
	obj.setAttribute('gvalue',obj.value);
	obj.setAttribute('gvaluecode',obj.getAttribute('code'));
}

function checkbox_onchange_handle(checked,obj){
	$(obj).next('label').addClass('valuechange');
	if(checked){
		$(obj).attr('checked','checked');
		//有互斥属性，去掉其他选中
		var mutex=$(obj).attr('mutex');
		var name=$(obj).attr('name');
		if(mutex&&mutex=='1'){
			$(obj).nextAll("input[name='"+name+"']").removeAttr('checked');
			$(obj).prevAll("input[name='"+name+"']").removeAttr('checked');
		} else{//没有互斥属性，去掉具有互斥属性选择框的选中
			$(obj).nextAll("input[name='"+name+"'][mutex='1']").removeAttr('checked');
			$(obj).prevAll("input[name='"+name+"'][mutex='1']").removeAttr('checked');
		}
		
	} else{
		$(obj).removeAttr('checked');
	}
}

function radio_onchange_handle(checked,obj){
//	console.log(obj)
	$("input:radio[name='"+$(obj).attr('name')+"']:not(#"+obj.id+")").removeAttr('checked')[0].checked=false;
	if(checked){
		$(obj).attr('checked',true);
	} else{
		$(obj).removeAttr('checked');
	}
}

function number_onchange_handle(obj){
	obj.setAttribute('value',obj.value);
}