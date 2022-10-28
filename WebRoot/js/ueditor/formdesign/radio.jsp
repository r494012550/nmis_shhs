<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>单选框</title>
   <%@ include file="/common/meta.jsp"%>
    <script type="text/javascript">
function fnHTMLDecode( text ) {
    if ( !text ) {
        return '' ;
    }
    text = text.replace( /&gt;/g, '>' ) ;
    text = text.replace( /&lt;/g, '<' ) ;
    text = text.replace( /&amp;/g, '&' ) ;
    return text ;
}

function fnSetAttribute( element, attName, attValue ) {
    if ( attValue == null || attValue.length == 0 ){
        element.removeAttribute( attName, 0 ) ;        
    } else {
        element.setAttribute( attName, attValue, 0 ) ;    
    }
}
    </script>
</head>
<body>
<div style="padding:0px;margin-left:auto;margin-right:auto;width:660px;height:500px;">
		<form id="select_form" method="post">
			<div style="margin-bottom:5px;width:100%;">
			 	<div style="float:left;width:50%;">
			 		<input id="radio_name" class='easyui-textbox tb' label="${sessionScope.locale.get('report.name')}:" labelPosition="top" style="width:250px;height: 60px"  
			 			data-options="prompt:'${sessionScope.locale.get('admin.entername')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" name="name" value=""/>
			 	</div>
			 	<div style="float:right;width:50%;">
			 		<input id="radio_code" class='easyui-textbox tb' label="${sessionScope.locale.get('admin.code')}:" labelPosition="top" name="code" style="width:250px;height:60px;"  
	            		data-options="prompt:'${sessionScope.locale.get('admin.choosecode')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" readonly='readonly' value=""/>
	            	<a class="easyui-linkbutton"  style="width:50px;height:28px;margin-left:3px;" onclick="openStandardCodWindow_('radio_code')">${sessionScope.locale.get('admin.code')}</a>
			 		
			 	</div>
		 	</div>
		 	<div style="margin-bottom:5px;width:100%;">
		 		<div style="float:left;width:50%;">
	            	<input id="radio_width" class='easyui-textbox tb' label="${sessionScope.locale.get('admin.width')}:" labelPosition="top" style="width:250px;height: 60px"  
			 			data-options="prompt:'${sessionScope.locale.get('admin.entercomponentwidth')}...',value:'100'" name="gwidth" value=""/> px
	            </div>
			 	<div style="float:right;width:50%;">
					<input id="radio_height" class="easyui-numberbox tb" label="${sessionScope.locale.get('admin.height')}:" labelPosition="top"  value="" style="width:250px;height:60px;"  
						data-options="prompt:'${sessionScope.locale.get('admin.entercomponentheight')}...',value:'23'"/> px
				</div>
            </div>
            <div style="margin-bottom:5px;width:100%;">
	            <div style="float:left;width:50%;">
	            	<select id="radio_operation" name="operation" class="easyui-combobox" label="操作:" labelPosition="top" style="width:250px;height:60px;" 
						data-options="prompt:'请选择选中选项的操作...',editable:false,onSelect:selectOperation_handle" >
						<option>无操作</option>
						<option value="showOptiopn_section">显示章节</option>
						<option value="copyOption_Nsection">复制(N)个章节</option>
					</select>
	            </div>
	            <div style="float:right;width:50%;">
		            <select id="radio_section" name="operation" class="easyui-combobox" label="章节:" labelPosition="top" style="width:250px;height:60px;" 
						data-options="prompt:'请选择章节...',editable:false,textField:'name',valueField:'uid',disabled:true,onLoadSuccess:sectionLoadSuccess" >
						
					</select>
	            </div>
            </div>
             
            <div style="margin-bottom:5px;width:100%;">
	            <div style="float:left;width:50%;">
	            	<input class="easyui-checkbox" id="radio_required" name="required" label="${sessionScope.locale.get('required')}:" labelPosition="top">
	            </div>
	            <div style="float:right;width:50%;">
					
	            </div>
            </div>
            <div style="margin-bottom:5px;width:100%;">
	            <div style="float:left;width:100%;">
	            	<div style="float:left;width:90%;">
		            	<textarea id="radio_sumtxt" class="easyui-textbox tb" label="总结文本-所见(@{value},@{displayname}):" labelPosition="top" style="width:600px;height: 80px" multiline="true"
				 			name="summary_text" value=""></textarea>
			 		</div>
			 		<div style="float:right;width:10%;padding-top:5px;">
				 		<a href="#" title="测试提取结果" class="easyui-tooltip" onclick="openTestComponentDlg('radio_sumtxt','summary')">测试</a>
				 		<a href="#" title="在总结文本中添加@{value}，提取总结文本时将被组件的值替换。@{displayname}：只供选择框使用，将会用选择项的显示名称替换。
				 			支持JavaScript脚本，脚本使用方法@{{JavaScript}}，脚本中支持使用@{value}表示该组件的值，示例如下：
				 			@{{'@{value}'=='无'?'未见明显钙化':'@{value}'=='有'?'发现钙化':'@{value}'}}" class="easyui-tooltip">帮助</a>
	            	</div>
	            </div>
            </div>
            <div style="margin-bottom:5px;width:100%;">
	            <div style="float:left;width:100%;">
	            	<div style="float:left;width:90%;">
		            	<textarea id="radio_sumtxt_result" class="easyui-textbox tb" label="总结文本-结论(@{value},@{displayname}):" labelPosition="top" style="width:600px;height: 80px" multiline="true"
				 			name="summary_text_result" value=""></textarea>
	            	</div>
	            	<div style="float:right;width:10%;padding-top:5px;">
				 		<a href="#" title="测试提取结果" class="easyui-tooltip" onclick="openTestComponentDlg('radio_sumtxt_result','summary_result')">测试</a>
	            	</div>
	            </div>
            </div>
            
            <input type="hidden" id="component_type" name="type" value="6"/>
            <input type="hidden" name="id" value="" id="radioComponentid"/>
            <input type="hidden" name="uid" value="" id="radioComponentuid"/>
	        <input type="hidden" id="radio_html" name="html"/>
	        <input type="hidden" id="radio_option" name="select_option"/>
	        <input type="hidden" id="radio_path" name="path"/>
        </form>
            
		 	<div class="easyui-panel" style="margin-top:3px;width:660px;height:260px;" border="0">
		 		<table id="dg_select" class="easyui-datagrid" 
		            data-options="fit:true,
		                singleSelect: true,
		                toolbar: '#tb_radio',
		                checkOnSelect:false,
		                onClickCell: onClickCell_handle,
		                onEndEdit: onEndEdit_handle,
		                onLoadSuccess:onLoadSuccess_handle
		            ">
			        <thead>
			            <tr>
			            	<th data-options="field:'ck',checkbox:true">${sessionScope.locale.get('admin.defaultoption')}</th>
			                <th data-options="field:'value',width:140,editor:'textbox',options:{required:true,fit:true}">选择项值</th>
			                <th data-options="field:'displayname',width:100,editor:'textbox',options:{fit:true}">显示名称</th>
			                <th data-options="field:'code',width:80">${sessionScope.locale.get('admin.code')}</th>
			                <th data-options="field:'sectionname',width:80">章节</th>
			                <th data-options="field:'color',width:50" formatter="columeStyler_optioncolor">颜色</th>
			                <th data-options="field:'status',width:180,align:'center',formatter:columnformatter">${sessionScope.locale.get('admin.operation')}</th>
			                <!-- <th data-options="field:'status',width:100,align:'center'">章节</th> -->
			            </tr>
			        </thead>
			    </table>
			    <div id="tb_radio" style="text-align:right;">
			    	<input class="easyui-checkbox" id="wrap_options" name="wrap_options" value="wrap_options" label="选项间换行" labelPosition="after">
			        <a class="easyui-linkbutton" onclick="appendOption()">${sessionScope.locale.get('admin.addoption')}</a>
			        <a class="easyui-linkbutton c2" onclick="clearDefault()">${sessionScope.locale.get('admin.removedefaultoption')}</a>
			    </div>
			 	
			</div>
	</div>
<script type="text/javascript">
var oNode = null,oListText='',thePlugins = 'radio';
$(document).ready(function(){
    oListText = $('#orglist');
    
    if( UE.plugins[thePlugins].editdom ){
        oNode = UE.plugins[thePlugins].editdom;
        var gTitle=oNode.getAttribute('title');
        if(gTitle){
            gTitle=gTitle.replace(/&quot;/g,"\"");
        }
        else{
        	gTitle="";
        }
        gWidth=oNode.getAttribute('orgwidth'),
        gHeight=oNode.getAttribute('orgheight'),
        //gSize=oNode.getAttribute('size');
        code=oNode.getAttribute('code'),
        ret=oNode.getAttribute('required'),
        id=oNode.getAttribute('id'),
        uid=oNode.getAttribute('uid'),
        wrap_options=oNode.getAttribute('wrap_options');
        //$('#orgvalue').innerHTML = oNode.value;
        $('#radio_name').val(gTitle);
        //$('#orgsize').value = gSize;
        $('#radio_width').val(gWidth);
        $('#radio_height').val(gHeight);
        $('#radio_code').val(code);
        $('#radio_code').attr('_path',oNode.getAttribute('_path'));
        $('#radio_sumtxt').val(oNode.getAttribute('summary'));
        $('#radio_sumtxt_result').val(oNode.getAttribute('summary_result'));
        
        $('#radio_operation').val(oNode.getAttribute('operation'));
        
        //$('#select_section').val(oNode.getAttribute('defaultsection'));

        if(ret=="required"){
        	$('#radio_required').attr("data-options","checked:true");
        }
        if(wrap_options=="wrap_options"){
        	$('#wrap_options').attr("data-options","checked:true");
        }
		
        $('#radioComponentid').val(id);
        $('#radioComponentuid').val(uid);

        $('#dg_select').datagrid({url:'srtemplate/findComponentSelectOption?uid='+uid});
        
        //$('#dg_select').datagrid("reload");
        
       // for ( var i = 0 ; i < oNode.options.length ; i++ ) {
       //     var sText    = oNode.options[i].value ;
       //     _fnAddComboOption( oListText, sText, sText ,null,null,oNode.options[i].code) ;
       // }
    }
    /*$('#showTips').popover();*/
    
});
var editIndex = undefined;
function endEditing(){
    if (editIndex == undefined){return true}
    if ($('#dg_select').datagrid('validateRow', editIndex)){
        $('#dg_select').datagrid('endEdit', editIndex);
        editIndex = undefined;
        return true;
    } else {
        return false;
    }
}
function onClickCell_handle(index, field){
	console.log("editIndex="+editIndex+";index="+index);
    if (editIndex != index){
        if (endEditing()){
            $('#dg_select').datagrid('selectRow', index)
                    .datagrid('beginEdit', index);
            var ed = $('#dg_select').datagrid('getEditor', {index:index,field:field});
            if (ed){
                ($(ed.target).data('textbox') ? $(ed.target).textbox('textbox') : $(ed.target)).focus();
            }
            editIndex = index;
        } else {
            setTimeout(function(){
                $('#dg_select').datagrid('selectRow', editIndex);
            },0);
        }
    }
}
function onEndEdit_handle(index, row){
    /*var ed = $(this).datagrid('getEditor', {
        index: index,
        field: 'productid'
    });*/
    //row.productname = $(ed.target).combobox('getText');
}

function onLoadSuccess_handle(data){
	var dgrows=$('#dg_select').datagrid('getData').rows;
	for(var n=0;n<dgrows.length;n++){
		if(dgrows[n].defaultoption=="checked"){
			$('#dg_select').datagrid('checkRow',n);
		}
	}
}


function columnformatter(value, row, index){
	return (row.canotedit=='1'?'':'<a href="#" onclick=\'handelClick()\'>'+$.i18n.prop('admin.code')+'</a>&nbsp;<a href="#" onclick=\'removeit()\'>'+$.i18n.prop('delete')+'</a>&nbsp;')+
		'<a href="#" onclick=\'up_option()\'>'+$.i18n.prop('admin.up')+'</a>&nbsp;<a href="#" onclick=\'down_option()\'>'+$.i18n.prop('admin.down')+'</a>&nbsp;'+
		'<a href="#" onclick=\'assginSection()\'>章节</a>';
}

function assginSection(){
	setTimeout(function(){
		console.log(editIndex)
		$("#dg_select").datagrid("endEdit", editIndex);
		openSectionsDialog(editIndex);
		
	},1);
}

function selectOperation_handle(record){
	
	if(record.value=="copyOption_Nsection"){
		var sectionid=$("#section_id").val();
		var tab = $('#srtemplate_tab').tabs('getSelected');
		var index = $('#srtemplate_tab').tabs('getTabIndex',tab);
		if(index==0){
			sectionid="";
		}
		setTimeout(function(){
			$("#radio_section").combobox('enable');
			$("#radio_section").combobox('reload','${ctx}/srtemplate/findSRSections_NoContent?sectionid='+sectionid); 
		},0);
		
	}
	else{
		setTimeout(function(){$("#radio_section").combobox('disable');},0);
	}
}

function sectionLoadSuccess(){
	if(oNode){
		$("#radio_section").combobox('setValue',oNode.getAttribute('defaultsection'));
	}
}
/*
function onEndEdit(index, row){
    var ed = $(this).datagrid('getEditor', {
        index: index,
        field: 'productid'
    });
    //row.productname = $(ed.target).combobox('getText');
}
*/
function appendOption(){
    if (endEditing()){
    	
    	var sectionuidstr="";
    	var sectionnamestr="";
    	if($("#radio_operation").combobox('getValue')=="copyOption_Nsection"){
    		sectionuidstr=$("#radio_section").combobox('getValue');
    		sectionnamestr=$("#radio_section").combobox('getText');
    	}
    	
        $('#dg_select').datagrid('appendRow',{name:'',displayname:'',code:'',sectionuid:sectionuidstr,sectionname:sectionnamestr});
        editIndex = $('#dg_select').datagrid('getRows').length-1;
        $('#dg_select').datagrid('selectRow', editIndex)
                .datagrid('beginEdit', editIndex);
    }
}

function clearDefault(){
	$('#dg_select').datagrid("clearChecked");
}
function removeit(index){
	setTimeout(function(){
		console.log(editIndex)
	    $('#dg_select').datagrid('cancelEdit', editIndex);
	    $('#dg_select').datagrid('deleteRow', editIndex);
	    editIndex = undefined;
	},1);
}

function handelClick(){
	setTimeout(function(){
		console.log(editIndex)
		$("#dg_select").datagrid("endEdit", editIndex);
		openStandardCodWindow_(editIndex);
	},1);
}

function up_option(){
	setTimeout(function(){
		console.log(editIndex)
		if(editIndex>0){
			$("#dg_select").datagrid("endEdit", editIndex);
	
			
			var data=$('#dg_select').datagrid('getData');
			var rows=data.rows;
			var code=rows[editIndex].code;
			var value=rows[editIndex].value;
			var displayname=rows[editIndex].displayname;
			var sectionuid=rows[editIndex].sectionuid;
			var color=rows[editIndex].color;
			var code1=rows[editIndex-1].code;
			var value1=rows[editIndex-1].value;
			var displayname1=rows[editIndex-1].displayname;
			var sectionuid1=rows[editIndex-1].sectionuid;
			var color1=rows[editIndex-1].color;
			rows[editIndex].code=code1;
			rows[editIndex].value=value1;
			rows[editIndex].displayname=displayname1;
			rows[editIndex].sectionuid=sectionuid1;
			rows[editIndex].color=color1;
			rows[editIndex-1].code=code;
			rows[editIndex-1].value=value;
			rows[editIndex-1].displayname=displayname;
			rows[editIndex-1].sectionuid=sectionuid;
			rows[editIndex-1].color=color;
			$('#dg_select').datagrid('refreshRow',editIndex);
			$('#dg_select').datagrid('refreshRow',editIndex-1);
		}

	},1);
}
function down_option(){

	setTimeout(function(){
		console.log(editIndex)
		$("#dg_select").datagrid("endEdit", editIndex);
		var data=$('#dg_select').datagrid('getData');
		var rows=data.rows;
		if((editIndex+1)<rows.length){
			var code=rows[editIndex].code;
			var value=rows[editIndex].value;
			var displayname=rows[editIndex].displayname;
			var sectionuid=rows[editIndex].sectionuid;
			var color=rows[editIndex].color;
			var code1=rows[editIndex+1].code;
			var value1=rows[editIndex+1].value;
			var displayname1=rows[editIndex+1].displayname;
			var sectionuid1=rows[editIndex+1].sectionuid;
			var color1=rows[editIndex+1].color;
			rows[editIndex].code=code1;
			rows[editIndex].value=value1;
			rows[editIndex].displayname=displayname1;
			rows[editIndex].sectionuid=sectionuid1;
			rows[editIndex].color=color1;
			rows[editIndex+1].code=code;
			rows[editIndex+1].value=value;
			rows[editIndex+1].displayname=displayname;
			rows[editIndex+1].sectionuid=sectionuid;
			rows[editIndex+1].color=color;
			$('#dg_select').datagrid('refreshRow',editIndex);
			$('#dg_select').datagrid('refreshRow',editIndex+1);
			
		}
               
    },1);

}


/* function fnHTMLEncode( text ) {
    if ( !text ) {
        return '' ;
    }
    text = text.replace( /&/g, '&amp;' ) ;
    text = text.replace( /</g, '&lt;' ) ;
    text = text.replace( />/g, '&gt;' ) ;
    return text ;
} */ 

oncancel = function () {
    if( UE.plugins[thePlugins].editdom ) {
        delete UE.plugins[thePlugins].editdom;
    }
    if( UE.plugins[thePlugins].editway) {
    	delete UE.plugins[thePlugins].editway;
    }
};
onok = function (editor){

	if(!endEditing()){
		return false;
	}
	
    if(!$('#radio_name').val()||!$('#radio_code').val()){
        return false;
    }

    var gWidth=$('#radio_width').val(),
    gHeight=$('#radio_height').val(),
    gTitle=$('#radio_name').val(),
    code=$('#radio_code').val(),
    path=$('#radio_code').attr('_path'),
    req=$('#radio_required').checkbox('options').checked,
    wrap_options=$('#wrap_options').checkbox('options').checked,
    id=$('#radioComponentid').val(),
    uid=$('#radioComponentuid').val(),
    operation=$('#radio_operation').textbox("getValue"),
    defaultsection=$('#radio_section').textbox("getValue");
	//console.log($('#dg_select').datagrid('getData').rows);
	if($('#dg_select').datagrid('getData').rows==0){
		$.messager.show({
				title : $.i18n.prop('alert'),msg : $.i18n.prop('admin.addoption'),timeout : 3000,border:'thin',showType : 'slide'
		 });
		return;
	}

    $.getJSON( window.localStorage.ctx+"/srtemplate/checkComponentName?name="+gTitle+"&uid="+uid, function(json){

   	   if(json.code == 0){
   			 
   			 if(json.data){
   				 $.messager.show({
   						title : $.i18n.prop('error'),msg : $.i18n.prop('admin.componentnameexists'),timeout : 3000,border:'thin',showType : 'slide'
   				 });
   				 $('#radio_name').textbox("setValue",'');
   			 }
   			 else{

   				
   				 $('#select_form').form('submit', {
   					    url:window.localStorage.ctx+"/srtemplate/saveComponent_Select",
   					    onSubmit: function(){
   					    	if( !oNode ) {
   					        	oNode=pluginHandle.createElement("input",pluginHandle.component_name_radio);
   					        	oNode.setAttribute('id','@id@');
   					            oNode.setAttribute('uid','@uid@');
   					            oNode.setAttribute("class","radiobutton_Component");
   					         	oNode.setAttribute("disabled", "disabled");
   					        }
   					        oNode.setAttribute('title', gTitle);
   					        oNode.setAttribute('code',code);
   					    	oNode.setAttribute('_path',path);
   					 		oNode.setAttribute('operation',operation);
   					 		oNode.setAttribute('defaultsection',defaultsection);
   					 		oNode.setAttribute('summary',$('#radio_sumtxt').val());
   					 		oNode.setAttribute('summary_result',$('#radio_sumtxt_result').val());
   					        //oNode.innerHTML = gValue;
   					        
   					        if(req){
   					        	oNode.setAttribute('required','required');
   					        }
   					        else{
   					        	oNode.removeAttribute('required');
   					        }
   					        if(wrap_options){
   					        	oNode.setAttribute('wrap_options','wrap_options');
   					        } else{
   					        	oNode.removeAttribute('wrap_options');
   					        }

   					     	oNode.removeAttribute('onchange');
   					     	
   					     	var rows=$('#dg_select').datagrid('getChecked');
   					     	var defaultvalue="",defaultvaluecode="";

   					     	for(var i=0;i<rows.length;i++){
   					     		defaultvalue+=rows[i].value+",";
   					     		defaultvaluecode+=rows[i].code+",";
   					     	}
   					     	if(defaultvalue){
   					     		defaultvalue=defaultvalue.substr(0,defaultvalue.length-1);
   					     		defaultvaluecode=defaultvaluecode.substr(0,defaultvaluecode.length-1);
   					     	}
   					     	
				        	/* oNode.setAttribute('data-options',"value:'"+defaultvalue+"',onChange:function(){select_onchange_multiple_handle($(this))},"+
						        	"onLoadSuccess:function(){select_onload_multiple_handle($(this))}"); */

   					     	oNode.setAttribute('gvalue',defaultvalue);
					  		oNode.setAttribute('gvaluecode',defaultvaluecode);
   					        
   					        if( gWidth != '' ) {
   					            oNode.style.width = gWidth+ 'px';
   					            oNode.setAttribute('orgwidth',gWidth );
   					        }else{
   					        	oNode.style.width = gWidth+ '100px';
   					            oNode.setAttribute('orgwidth', '100');
   					        }
   					        if( gHeight != '' ) {
   					            oNode.style.height = gHeight+ 'px';
   					            oNode.setAttribute('orgheight',gHeight );
   					        }else{
   					            oNode.setAttribute('orgheight', '23');
   					        }

							var optionjson=[];

							var checkrows=$('#dg_select').datagrid('getChecked');
							/* console.log(checkrows);
							var checkindex=-1;
							if(checkrows.length>0){
								checkindex=$('#dg_select').datagrid('getRowIndex',checkrows[0]);
							} */
							
							var values="";
							
							var dgrows=$('#dg_select').datagrid('getData').rows;
							for(var n=0;n<dgrows.length;n++){
								//console.log(dgrows[n]);
								var sText    = dgrows[n].value||"";
								var defaultoption=false;
								for(var j=0;j<checkrows.length;j++){
									var checkindex=$('#dg_select').datagrid('getRowIndex',checkrows[j]);
									if(n==checkindex){
										defaultoption=true;
										//continue;
									}
								}
								
								
								var checkbox=pluginHandle.createElement("input",pluginHandle.component_name_checkbox);
								checkbox.setAttribute('type','checkbox');
								checkbox.setAttribute('id','@id@');
								checkbox.setAttribute('uid','@uid@');
								if(defaultoption){
									checkbox.setAttribute('checked','checked');
								}
								checkbox.setAttribute('value',sText);
								values+=sText+"     ";
		                        var json={};
		                        json.code=dgrows[n].code||"";
		                        json.value=dgrows[n].value||"";
		                        json.displayname=dgrows[n].displayname||"";
		                        json.defaultoption=defaultoption?"checked":"";
		                        json.sectionuid=dgrows[n].sectionuid||"";
		                        json.color=dgrows[n].color||"";
		                        optionjson.push(json);
							}
							oNode.setAttribute("value",values);
							oNode.setAttribute("options",JSON.stringify(optionjson));
   					     	$("#radio_option").val(JSON.stringify(optionjson));
   					        $('#radio_html').val(oNode.outerHTML);
   					     	$('#radio_path').val(path);
   					    },
   					    success:function(data){
   					    	var json = validationDataAll(data);
   					        if(json.code==0){
   					        	
   					        	//console.log(id)
   					        	if(!UE.plugins[thePlugins].editway){
	   					        	if(uid!=""){
	   					        		delete UE.plugins[thePlugins].editdom;
	   					        	}
	   					        	else{
	   					        		oNode.setAttribute('id',json.data.uid);
	   						        	oNode.setAttribute('uid',json.data.uid);
	   						        	oNode.setAttribute('objuid',uuid());
	   						        	editor.execCommand('insertHtml',oNode.outerHTML);
	   					        	}
   					        	}
 					        	else{
 					        		updateComponentInSRTemplate(json.data,editor);
 					        		delete UE.plugins[thePlugins].editdom;
 	 	 					    }
 					        	delete UE.plugins[thePlugins].editway;
   					        	//$("#templets").datagrid('reload');
   					        	$('#common_dialog').dialog('close');
   					        }
   					    }
   					});
   				 
   			 }
   	   }
   	   else{
   			$.messager.show({
   				title : $.i18n.prop('error'),msg : $.i18n.prop('savefailed'),timeout : 3000,border:'thin',showType : 'slide'
   		 	});
   		}

   	   
      });

};

function columeStyler_optioncolor(value, rowData, rowIndex){
	return '<input type="color" id="'+rowIndex.toString()+'" name="color" style="width:35px;" onchange="colorChange(this.value);" value="'+(value||'')+'">';
}

function colorChange(color){
	var row=$('#dg_select').datagrid('getSelected');
	console.log(row)
	row.color=color;
}
</script>
</body>
</html>