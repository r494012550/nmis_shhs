function goToAddPhrasenode(reportid){
	    
	var node = $('#phrase_node_' +reportid).tree('getSelected');
	if(node && node.type!="node"){
		_message($.i18n.prop('report.selectchildnode') , $.i18n.prop('alert'));
		return;
	}else {
		$('<div></div>').dialog({
			id : 'newDialog',
			title:'添加节点',
			width:320,height:160,
			resizable: false,minimizable: false,maximizable: false,modal : true,
			border: 'thin',
			href: window.localStorage.ctx + '/report/goToAddPhrasenode',
			buttons:[{
				text:'保存',
				width:80,
				handler:function(){addPhrasenode(reportid);}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#newDialog').dialog('destroy');}
			}],
			onLoad:function(){

			},
			onClose:function(){
				$('#newDialog').dialog('destroy');
			}
		});
	}
}

function addPhrasenode(reportid){
	var node = $('#phrase_node_' +reportid).tree('getSelected');
	console.log(node);
	$('#phraseClassifyForm').form('submit', {
		url: window.localStorage.ctx+"/report/addPhrasenode",
		onSubmit: function(param){
			if(node){
				param.parent = node.id;
			}else{
				param.parent = 0;
			}
			if($('#phraseClassifyName').textbox('getText')==''){
				return false;
			}
		},
		success: function(data){
			closeclassifynode(reportid);
			var json = validationDataAll(data);
			
			if(json.code == 0){
				var parent = $('#phrase_node_' + reportid).tree('getParent', node.target);
				if(node&&parent){
					$('#phrase_node_' + reportid).tree('reload', parent.target);
				}else{
					$('#phrase_node_' + reportid).tree('reload');
				}
				$('#newDialog').dialog('close');
			} else {
				_message( $.i18n.prop('savefailed') , $.i18n.prop('error'));
			}
		}
	});
}

function goToAddPhraseContent(reportid) {
	var node = $('#phrase_node_' + reportid).tree('getSelected');
	console.log(node);
	if (node && node.parent && node.type=="node") {
		$('<div></div>').dialog({
			id : 'newDialog',
			title:'添加词条',
			width:520,height:200,
			resizable: false,minimizable: false,maximizable: false,modal : true,
			border: 'thin',
			href: window.localStorage.ctx + '/report/goToAddPhraseContent?reportid='+reportid,
			buttons:[{
				text:'保存',
				width:80,
				handler:function(){savePhrase(reportid, node);}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#newDialog').dialog('destroy');}
			}],
			onLoad:function(){

			},
			onClose:function(){
				$('#newDialog').dialog('destroy');
			}
		});	
	} else {
		_message($.i18n.prop('report.selectchildnode') , $.i18n.prop('alert'));
	}
}

function savePhrase(reportid, node){
	$('#phraseForm_' + reportid).form('submit', {
		url: window.localStorage.ctx+'/report/savePhrase',
		onSubmit: function(param){
			param.nodeid = node.id;
			return $(this).form('validate');
		},
		success: function(data){
		 	var json=validationDataAll(data);
	    	if(json.code==0){
	    		var parent = $('#phrase_node_' + reportid).tree('getParent', node.target);
	    		if(node&&parent){
					$('#phrase_node_' + reportid).tree('reload', parent.target);
				}else{
					$('#phrase_node_' + reportid).tree('reload');
				}
				$('#newDialog').dialog('close');
	        }
	        else{
	        	_message( $.i18n.prop('savefailed') , $.i18n.prop('error'));
	        }
		}
	});

}

function previewPhrase(node, reportid){
	if(!node){
		node = $('#phrase_node_' + reportid).tree('getSelected');
	}
	if(node.attributes && node.attributes.phrase_name){
		if($("#orderStatus_"+reportid).val() != myCache.ReportStatus.FinalResults){
			$('<div></div>').dialog({
				id : 'newDialog',
				title:'使用词条',
				width:520,height:300,
				resizable: false,minimizable: false,maximizable: false,modal : true,
				border: 'thin',
				href: window.localStorage.ctx + '/report/previewPhrase?reportid='+reportid+"&id="+node.attributes.id,
				buttons:[{
					text:'复制',
					width:80,
					handler:function(){
						myCopyTxt(node.attributes.phrase_content);
						$('#newDialog').dialog('close');
					}
				},{
					text:'关闭',
					width:80,
					handler:function(){$('#newDialog').dialog('destroy');}
				}],
				onLoad:function(){

				},
				onClose:function(){
					$('#newDialog').dialog('destroy');
				}
			});	
		}else{
			_message( '该报告是最终报告不可编辑！' , '提醒');
		}
	}
	
}

function myCopyTxt(text){
    const input = document.createElement( 'input' );
    document.body.appendChild(input);
    input.setAttribute( 'value' , text );
    input.select();
    if (document.execCommand( 'copy' )) {
        document.execCommand( 'copy' );
        console.log( '复制成功' );
    }
    document.body.removeChild(input);
}

function deletePhrase(reportid){
	var node = $('#phrase_node_' + reportid).tree('getSelected');
}

function searchPhrase(value,name,reportid) {
	if (value == "") {
		// 搜索内容为空时，将加载整棵树
		getJSON(window.localStorage.ctx+'/report/getPersonalPhrase', null, 
				function(data) {
					$('#phrase_node_' + reportid).tree('loadData', data);
				});
	} else {
		// 搜索内容不为空，则搜索相应的模板
		getJSON(window.localStorage.ctx+"/report/searchPhrase",
				{
					searchContent:value
				},function(data) {
					$('#phrase_node_' + reportid).tree('loadData',data);

		});
	}
}

function modifynodePhrase(reportid){
	var node = $('#phrase_node_' + reportid).tree('getSelected');
	if(node.type=="node"){
		$('<div></div>').dialog({
			id : 'newDialog',
			title:'添加节点',
			width:320,height:160,
			resizable: false,minimizable: false,maximizable: false,modal : true,
			border: 'thin',
			href: window.localStorage.ctx + '/report/goToAddPhrasenode?PhrasenodeId='+node.id,
			buttons:[{
				text:'保存',
				width:80,
				handler:function(){addPhrasenode(reportid);}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#newDialog').dialog('destroy');}
			}],
			onLoad:function(){

			},
			onClose:function(){
				$('#newDialog').dialog('destroy');
			}
		});
	}else if(node.type=="content"){
		$('<div></div>').dialog({
			id : 'newDialog',
			title:'添加词条',
			width:520,height:200,
			resizable: false,minimizable: false,maximizable: false,modal : true,
			border: 'thin',
			href: window.localStorage.ctx + '/report/goToAddPhraseContent?reportid='+reportid+'&PhraseContentId='+node.id,
			buttons:[{
				text:'保存',
				width:80,
				handler:function(){savePhrase(reportid, node);}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#newDialog').dialog('destroy');}
			}],
			onLoad:function(){

			},
			onClose:function(){
				$('#newDialog').dialog('destroy');
			}
		});	
	}else{
		_message('选择的数据有问题，请联系管理员！！' , '提醒');
	}
}

/**
 * 双击复制导入鼠标停留的地方
 * @param node
 * @param reportid
 * @returns
 */
function copyImport(node,reportid){
//	console.log(node);
//	console.log(repoxtid);
	if (node.attributes.phrase_content){
		var phrase_content = node.attributes.phrase_content;
		myCopyTxt(phrase_content);
		
		var select =$("#report_select_" + reportid).val();
		if ("desc" == select) {
			var ue_desc = UM.getEditor('desc_' + reportid + '_html');
			try{
				ue_desc.execCommand( 'inserthtml ' , phrase_content);
			}catch(e){
				console.log(e);
			}
		}else{
			var ue_result = UM.getEditor( ' result_' + reportid + '_html');
			try{
				ue_result.execCommand( 'inserthtml ', phrase_content);
			}catch (e){
				console.log(e);
			}
		}
	}
}
