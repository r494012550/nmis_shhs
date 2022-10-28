<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>${sessionScope.locale.get('admin.combobox')}</title>
   <%@ include file="/common/meta.jsp"%>
     <!--<link rel="stylesheet" type="text/css" href="themes/metro-blue/easyui.css">
    <link rel="stylesheet" href="bootstrap/css/bootstrap.css">
    [if lte IE 6]>
    <link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap-ie6.css">
    <![endif]-->
    <!--[if lte IE 7]>
    <link rel="stylesheet" type="text/css" href="bootstrap/css/ie.css">
    <![endif]
    <link rel="stylesheet" href="leipi.style.css">
    <script type="text/javascript" src="../dialogs/internal.js"></script>-->
    <script type="text/javascript">
function createElement(type, name)
{     
    var element = null;     
    try {        
    	element = document.createElement(type);   
        element.setAttribute("name",name);    
    } catch (e) {}   
    if(element==null) {     
        element = document.createElement(type);     
        element.name = name;     
    } 
    return element;     
}
function fnSelect( combo ) {
    var iIndex = combo.selectedIndex ;
    oListText.selectedIndex    = iIndex ;
    var olistText    = document.getElementById( "orgtext" ) ;
    olistText.value    = oListText.value ;
}

function fnAdd() {
    var olistText    = document.getElementById( "orgtext" ) ;
    fnAddComboOption( oListText, olistText.value, olistText.value ) ;
    oListText.selectedIndex = oListText.options.length - 1 ;
    olistText.value    = '' ;
    olistText.focus() ;
}

function fnModify() {
    var iIndex = oListText.selectedIndex ;
    if ( iIndex < 0 ) return ;
    var olistText    = document.getElementById( "orgtext" ) ;
    oListText.options[ iIndex ].innerHTML    = fnHTMLEncode( olistText.value ) ;
    oListText.options[ iIndex ].value        = olistText.value ;
    olistText.value    = '' ;
    olistText.focus() ;
}

function fnMove( steps ) {
    fnChangeOptionPosition( oListText, steps ) ;
}

function fnDelete() {
    fnRemoveSelectedOptions( oListText ) ;
}

function fnSetSelectedValue() {
    var iIndex = oListText.selectedIndex ;
    if ( iIndex < 0 ) return ;
    var olistText = document.getElementById( "orgvalue" ) ;
    olistText.innerHTML = oListText.options[ iIndex ].value ;
}

// Moves the selected option by a number of steps (also negative)
function fnChangeOptionPosition( combo, steps ) {
    var iActualIndex = combo.selectedIndex ;
    if ( iActualIndex < 0 ){
        return ;
    }
    var iFinalIndex = iActualIndex + steps ;
    if ( iFinalIndex < 0 ){
        iFinalIndex = 0 ;
    }
    if ( iFinalIndex > ( combo.options.length - 1 ) ) {
        iFinalIndex = combo.options.length - 1 ;
    }
    if ( iActualIndex == iFinalIndex ) {
        return ;
    }
    var oOption = combo.options[ iActualIndex ] ;
    if(oOption.value=="") {
        var sText    = fnHTMLDecode( oOption.value ) ;
    } else {
        var sText    = fnHTMLDecode( oOption.innerHTML ) ;
    }
    combo.remove( iActualIndex ) ;
    oOption = fnAddComboOption( combo, sText, sText, null, iFinalIndex ) ;
    oOption.selected = true ;
}

// Remove all selected options from a SELECT object
function fnRemoveSelectedOptions( combo ) {
    // Save the selected index
    var iSelectedIndex = combo.selectedIndex ;
    var oOptions = combo.options ;
    // Remove all selected options
    for ( var i = oOptions.length - 1 ; i >= 0 ; i-- ) {
        if (oOptions[i].selected) combo.remove(i) ;
    }

    // Reset the selection based on the original selected index
    if ( combo.options.length > 0 ) {
        if ( iSelectedIndex >= combo.options.length ) iSelectedIndex = combo.options.length - 1 ;
        combo.selectedIndex = iSelectedIndex ;
    }
}




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
			 		<input id="select_name" class='easyui-textbox tb' label="${sessionScope.locale.get('report.name')}:" labelPosition="top" style="width:250px;height: 60px"  
			 			data-options="prompt:'${sessionScope.locale.get('admin.entername')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" name="name" value=""/>
			 	</div>
			 	<div style="float:right;width:50%;">
			 		<input id="select_code" class='easyui-textbox tb' label="${sessionScope.locale.get('admin.code')}:" labelPosition="top" name="code" style="width:250px;height:60px;"  
	            		data-options="prompt:'${sessionScope.locale.get('admin.choosecode')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" readonly='readonly' value=""/>
	            	<a class="easyui-linkbutton"  style="width:50px;height:28px;margin-left:3px;" onclick="openStandardCodWindow_('select_code')">${sessionScope.locale.get('admin.code')}</a>
			 		
			 	</div>
		 	</div>
		 	<div style="margin-bottom:5px;width:100%;">
		 		<div style="float:left;width:50%;">
	            	<input id="select_width" class='easyui-textbox tb' label="${sessionScope.locale.get('admin.width')}:" labelPosition="top" style="width:250px;height: 60px"  
			 			data-options="prompt:'${sessionScope.locale.get('admin.entercomponentwidth')}...',value:'100'" name="gwidth" value=""/> px
	            </div>
			 	<div style="float:right;width:50%;">
					<input id="select_height" class="easyui-numberbox tb" label="${sessionScope.locale.get('admin.height')}:" labelPosition="top"  value="20" style="width:250px;height:60px;"  
						data-options="prompt:'${sessionScope.locale.get('admin.entercomponentheight')}...',value:'23'"/> px
				</div>
            </div>
            <div style="margin-bottom:5px;width:100%;">
	            <div style="float:left;width:50%;">
	            	<select id="select_operation" name="operation" class="easyui-combobox" label="操作:" labelPosition="top" style="width:250px;height:60px;" 
						data-options="prompt:'请选择选中选项的操作...',editable:false,onSelect:selectOperation_handle" >
						<option value="">无操作</option>
						<option value="showOptiopn_section">显示章节</option>
						<option value="copyOption_Nsection">复制(N)个章节</option>
					</select>
	            </div>
	            <div style="float:right;width:50%;">
		            <select id="select_section" name="operation" class="easyui-combobox" label="章节:" labelPosition="top" style="width:250px;height:60px;" 
						data-options="prompt:'请选择章节...',editable:false,textField:'name',valueField:'uid',disabled:true,onLoadSuccess:sectionLoadSuccess" >
						
					</select>
	            </div>
            </div>
            <!-- <div style="margin-bottom:10px;width:100%;">
	            <div style="float:left;width:100%;">
	            	<input id="select_sumtxt" class='easyui-textbox tb' label="总结文本(@{value}):" labelPosition="top" style="width:480px;height: 60px"  
			 			data-options="prompt:'请输入总结文本...'" name="summary_text" value=""/>
	            	
	            </div>
            </div> -->
             
            <div style="margin-bottom:5px;width:100%;">
	            <div style="float:left;width:25%;">
	            	<input class="easyui-checkbox" id="select_required" name="required" label="${sessionScope.locale.get('required')}:" labelPosition="top">
	            </div>
	            <div style="float:left;width:25%;">
	            	<input class="easyui-checkbox" id="select_fixedwidth" name="fixedwidth" label="打印时固定宽度:" labelPosition="top">
	            </div>
	            <div style="float:right;width:50%;">
					<input class="easyui-checkbox _multipleselect" id="select_multiple" name="multiple" label="${sessionScope.locale.get('admin.allowmultiple')}:" labelPosition="top"
						data-options="onChange:function(checked){
										console.log(checked);
										$('#dg_select').datagrid('clearChecked')
										$('#dg_select').datagrid('options').singleSelect=!checked;
										//$('#dg_select').datagrid({singleSelect:!checked});
									}">
	            </div>
            </div>
            <div style="margin-bottom:5px;width:100%;">
	            <div style="float:left;width:100%;">
	            	<div style="float:left;width:90%;">
		            	<textarea id="select_sumtxt" class="easyui-textbox tb" label="总结文本-所见(@{value},@{displayname}):" labelPosition="top" style="width:600px;height: 80px" multiline="true"
				 			name="summary_text" value=""></textarea>
			 		</div>
			 		<div style="float:right;width:10%;padding-top:5px;">
				 		<a href="#" title="测试提取结果" class="easyui-tooltip" onclick="openTestComponentDlg('select_sumtxt','summary')">测试</a>
				 		<a href="#" title="在总结文本中添加@{value}，提取总结文本时将被组件的值替换。@{displayname}：只供选择框使用，将会用选择项的显示名称替换。
				 			支持JavaScript脚本，脚本使用方法@{{JavaScript}}，脚本中支持使用@{value}表示该组件的值，示例如下：
				 			@{{'@{value}'=='无'?'未见明显钙化':'@{value}'=='有'?'发现钙化':'@{value}'}}" class="easyui-tooltip">帮助</a>
	            	</div>
	            </div>
            </div>
            <div style="margin-bottom:5px;width:100%;">
	            <div style="float:left;width:90%;">
	            	<textarea id="select_sumtxt_result" class="easyui-textbox tb" label="总结文本-诊断结论(@{value},@{displayname}):" labelPosition="top" style="width:600px;height: 80px" multiline="true"
			 			name="summary_result_text" value=""></textarea>
	            </div>
	            <div style="float:right;width:10%;padding-top:5px;">
			 		<a href="#" title="测试提取结果" class="easyui-tooltip" onclick="openTestComponentDlg('select_sumtxt_result','summary_result')">测试</a>
            	</div>
            </div>
            
            <input type="hidden" id="component_type" name="type" value="1"/>
            <input type="hidden" name="id" value="" id="selectComponentid"/>
            <input type="hidden" name="uid" value="" id="selectComponentuid"/>
            <input type="hidden" id="select_path" name="path"/>
	        <input type="hidden" id="select_html" name="html"/>
	        <input type="hidden" id="select_option" name="select_option"/>
        </form>
            
		 	<div class="easyui-panel" style="margin-top:5px;width:660px;height:260px;" border="0">
		 		<table id="dg_select" class="easyui-datagrid" 
		            data-options="fit:true,
		                singleSelect: true,
		                toolbar: '#tb_select',
		                checkOnSelect:false,
		                onClickCell: onClickCell_handle,
		                onEndEdit: onEndEdit_handle,
		                onLoadSuccess:onLoadSuccess_handle
		            ">
			        <thead>
			            <tr>
			            	<th data-options="field:'ck',checkbox:true">${sessionScope.locale.get('admin.defaultoption')}</th>
			                <th data-options="field:'value',width:150,editor:'textbox',options:{required:true,fit:true}">选择项值</th>
			                <th data-options="field:'displayname',width:100,editor:'textbox',options:{fit:true}">显示名称</th>
			                <th data-options="field:'code',width:80">${sessionScope.locale.get('admin.code')}</th>
			                <th data-options="field:'sectionname',width:80">章节</th>
			                <th data-options="field:'color',width:45" formatter="columeStyler_optioncolor">颜色</th>
			                <th data-options="field:'status',width:175,align:'center',formatter:columnformatter">${sessionScope.locale.get('admin.operation')}</th>
			                <!-- <th data-options="field:'status',width:100,align:'center'">章节</th> -->
			            </tr>
			        </thead>
			    </table>
			    <div id="tb_select" style="text-align:right;">
			        <a class="easyui-linkbutton" id="_addoption" onclick="appendOption()">${sessionScope.locale.get('admin.addoption')}</a>
			        <a class="easyui-linkbutton c2" onclick="clearDefault()">${sessionScope.locale.get('admin.removedefaultoption')}</a>
			    </div>
			 	
			</div>
	</div>
<script type="text/javascript">
var oNode = null,oListText='',thePlugins = 'select';
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
        multiple=oNode.getAttribute('orgmultiple'),
        id=oNode.getAttribute('id'),
        uid=oNode.getAttribute('uid'),
        fixedwidth=oNode.getAttribute('fixedwidth');
        //$('#orgvalue').innerHTML = oNode.value;
        $('#select_name').val(gTitle);
        //$('#orgsize').value = gSize;
        $('#select_width').val(gWidth);
        $('#select_height').val(gHeight);
        $('#select_code').val(code);
        $('#select_code').attr('_path',oNode.getAttribute('_path'));
        $('#select_path').val(oNode.getAttribute('_path'));
        $('#select_sumtxt').val(oNode.getAttribute('summary'));
        $('#select_sumtxt_result').val(oNode.getAttribute('summary_result'));
        
        $('#select_operation').val(oNode.getAttribute('operation'));
        
        //$('#select_section').val(oNode.getAttribute('defaultsection'));

        if(ret=="required"){
        	$('#select_required').attr("data-options","checked:true");
        }
        if(!fixedwidth||fixedwidth=='1'){
        	$('#select_fixedwidth').attr("data-options","checked:true");
        }

        if(multiple){
        	//$('#select_multiple').attr("data-options","checked:true");
        	setTimeout(function(){
        		$('#select_multiple').checkbox("check");
            },0);
        	
        	$('#dg_select').datagrid({singleSelect:false});
        }
		
        $('#selectComponentid').val(id);
        $('#selectComponentuid').val(uid);

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
			$("#select_section").combobox('enable');
			$("#select_section").combobox('reload','${ctx}/srtemplate/findSRSections_NoContent?sectionid='+sectionid); 
		},0);
		
	}
	else{
		setTimeout(function(){$("#select_section").combobox('disable');},0);
	}
}

function sectionLoadSuccess(){
	if(oNode){
		$("#select_section").combobox('setValue',oNode.getAttribute('defaultsection'));
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
    	if($("#select_operation").combobox('getValue')=="copyOption_Nsection"){
    		sectionuidstr=$("#select_section").combobox('getValue');
    		sectionnamestr=$("#select_section").combobox('getText');
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

//Add a new option to a SELECT object (combo or list)
function _fnAddComboOption( combo, optionText, optionValue, documentObject, index ,code,defaultoption,sectionuid,displayname,color) {
    var oOption ;
    if ( documentObject ) {
        oOption = documentObject.createElement("option") ;
    } else {
        oOption = document.createElement("option") ;
    }
    if ( index != null ) {
        combo.options.add( oOption, index ) ;
    } else {
        combo.options.add( oOption ) ;
    }
    oOption.innerHTML = optionText.length > 0 ? fnHTMLEncode( optionText ) : '&nbsp;' ;
    oOption.value     = optionValue ;

    if(defaultoption){
    	oOption.setAttribute('selected','selected');
    }

    if(code){
    	oOption.setAttribute('code',code);
    }
    if(sectionuid){
    	oOption.setAttribute('sectionuid',sectionuid);
    }
    if(displayname){
    	oOption.setAttribute('displayname',displayname);
    }
    if(color){
    	oOption.setAttribute('color',color);
    }
    return oOption ;
}

function fnHTMLEncode( text ) {
    if ( !text ) {
        return '' ;
    }
    text = text.replace( /&/g, '&amp;' ) ;
    text = text.replace( /</g, '&lt;' ) ;
    text = text.replace( />/g, '&gt;' ) ;
    return text ;
}

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
	
    if(!$('#select_name').val()||!$('#select_code').val()){
        return false;
    }

    var gWidth=$('#select_width').val(),
    gHeight=$('#select_height').val(),
    gTitle=$('#select_name').val(),
    code=$('#select_code').val(),
    path=$('#select_code').attr('_path'),
    req=$('#select_required').checkbox('options').checked,
    fixedwidth=$('#select_fixedwidth').checkbox('options').checked,
    multiple=$('#select_multiple').checkbox('options').checked,
    id=$('#selectComponentid').val(),
    uid=$('#selectComponentuid').val(),
    operation=$('#select_operation').textbox("getValue"),
    defaultsection=$('#select_section').textbox("getValue");
    //console.log(req+"--"+multiple);
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
   				 $('#select_name').textbox("setValue",'');
   			 }
   			 else{

   				
   				 $('#select_form').form('submit', {
   					    url:window.localStorage.ctx+"/srtemplate/saveComponent_Select",
   					    onSubmit: function(){
   					    	if( !oNode ) {
   					        	oNode=pluginHandle.createElement("select","selectComponent");
   					        	
   					        	oNode.setAttribute('id','@id@');
   					            oNode.setAttribute('uid','@uid@');
   					            //oNode.setAttribute("name","textinputComponent");
   					        }
   					    	oNode.setAttribute('class','easyui-combobox');
   					        oNode.setAttribute('title', gTitle);
   					        oNode.setAttribute('code',code);
   					     	oNode.setAttribute('_path',path);
   					     	
   					 		oNode.setAttribute('operation',operation);
   					 		oNode.setAttribute('defaultsection',defaultsection);
   					 		oNode.setAttribute('summary',$('#select_sumtxt').val());
   					 		oNode.setAttribute('summary_result',$('#select_sumtxt_result').val());
   					 		
   					        //oNode.innerHTML = gValue;
   					        
   					        if(req){
   					        	oNode.setAttribute('required','required');
   					        }
   					        else{
   					        	oNode.removeAttribute('required');
   					        }
   					     	if(fixedwidth){
					        	oNode.setAttribute('fixedwidth','1');
					        } else{
					        	oNode.setAttribute('fixedwidth','0');
					        }

   					     	oNode.removeAttribute('onchange');
   					     	
   					     	var rows=$('#dg_select').datagrid('getChecked');
   					     	var defaultvalue="",defaultvaluecode="",defaultindex;
   					     	if(multiple){
	   					     	
	   					     	for(var i=0;i<rows.length;i++){
	   					     		defaultvalue+=rows[i].value+",";
	   					     		defaultvaluecode+=rows[i].code+",";
	   					     	}
	   					     	if(defaultvalue){
	   					     		defaultvalue=defaultvalue.substr(0,defaultvalue.length-1);
	   					     		defaultvaluecode=defaultvaluecode.substr(0,defaultvaluecode.length-1);
	   					     	}
	   					     	
					        	oNode.setAttribute('orgmultiple',multiple);
					        	oNode.setAttribute('data-options',"value:'"+defaultvalue+"',editable:false,panelHeight:'auto',panelMaxHeight:300,multiple:"+multiple+",onChange:function(){select_onchange_multiple_handle($(this))},"+
							        	"onLoadSuccess:function(){select_onload_multiple_handle($(this))}");

					        	//oNode.setAttribute('onchange','select_onchange_multiple_handle(this)');
					        }
					        else{
					        	
					        	var defaultvalue="";
	   					     	if(rows[0]){
	   					     		defaultvalue=rows[0].value;
	   					     		defaultvaluecode=rows[0].code;
	   					     		defaultindex=$('#dg_select').datagrid('getRowIndex',rows[0]);
	   					     	}
					        	oNode.setAttribute('orgmultiple','');
					        	//oNode.removeAttribute('data-options');
					        	oNode.setAttribute('data-options',"value:'"+defaultvalue+"',editable:false,panelHeight:'auto',panelMaxHeight:300,multiple:"+multiple+",onChange:function(){select_onchange_handle($(this))},"+
							        	"onClick:function(record){select_onclick_handle($(this),record)},onLoadSuccess:function(){select_onload_handle($(this))}");
					        	//oNode.data-options.multiple=!multiple;
					        	//oNode.setAttribute('onchange','select_onchange_handle(this)');
					        }
   					     	
   					     	
   					     	oNode.setAttribute('gvalue',defaultvalue);
					  		oNode.setAttribute('gvaluecode',defaultvaluecode);
					  		if(defaultindex!=null){
					  			oNode.setAttribute('selected_index',defaultindex);
					  		} else{
					  			oNode.setAttribute('selected_index','');
					  		}
   					        
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
   					     	oNode.options.length=0;

							var optionjson=[];

							var checkrows=$('#dg_select').datagrid('getChecked');
							/* console.log(checkrows);
							var checkindex=-1;
							if(checkrows.length>0){
								checkindex=$('#dg_select').datagrid('getRowIndex',checkrows[0]);
							} */

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
									
		                        var oOption = _fnAddComboOption( oNode, sText, sText ,null,null,dgrows[n].code,defaultoption,dgrows[n].sectionuid,dgrows[n].displayname,dgrows[n].color) ;

		                        var json={};
		                        json.code=dgrows[n].code||"";
		                        json.value=dgrows[n].value||"";
		                        json.displayname=dgrows[n].displayname||"";
		                        json.defaultoption=defaultoption?"checked":"";
		                        json.sectionuid=dgrows[n].sectionuid||"";
		                        json.color=dgrows[n].color||"";
		                        optionjson.push(json);
							}
							
   					     	$("#select_option").val(JSON.stringify(optionjson));
   					        $('#select_html').val(oNode.outerHTML);
   					     	$('#select_path').val(path);
   					        
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

     

  /*  if( !oNode ) {
        try {
            //oNode = document.createElement("select"); 
            oNode = createElement('select','leipiNewField');
            oNode.setAttribute('title',$G('orgname').value);
            oNode.setAttribute('code',window.localStorage.getItem("code"));
            oNode.setAttribute('leipiPlugins',thePlugins );
            oNode.setAttribute('size',gSize);
            oNode.setAttribute('unit',window.localStorage.getItem('unit'));
            oNode.setAttribute('onchange',"this.setAttribute('value',this.value)");
            if ( $G('orgwidth').value!= '' ) {
                oNode.style.width =  $G('orgwidth').value+ 'px';
                //oNode.setAttribute('style','width:'+ $G('orgwidth').value + 'px;');
            }

            oNode.style.height ='22px';
            if( gWidth != '' ) {
                oNode.style.width = gWidth + 'px';
                oNode.setAttribute('orgwidth',gWidth );
            }

            $(oNode).change(function(){
            	  $(this).css("background-color","#FFFFCC");
            });
            
            // Add all available options.
            	var data = window.localStorage.getItem('data');
                if(data != null && data != ''){
                	var jsonObj=JSON.parse(data);
                	for ( var i = 0 ; i < jsonObj.length ; i++ ) {
                    var sText    = jsonObj[i].value;
                    if ( sText.length == 0 ) {
                        sText = sText ;
                    }
                    var oOption = fnAddComboOption( oNode, sText, sText ) ;
                    if ( sText == $G('orgvalue').innerHTML ) {
                        fnSetAttribute( oOption, 'selected', 'selected' ) ;
                        oOption.selected = true ;
                    }
               	 }
                }
            //firefox要利用span
                if(window.localStorage.getItem("s_flag") == 0){
                	 editor.execCommand('insertHtml','');
               }else{
            	   editor.execCommand('insertHtml','<span leipiplugins="select">'+oNode.outerHTML+'</span>');
               }
           
            return true ;
        } catch ( e ) {
            try {
                editor.execCommand('error');
            } catch ( e ) {
                alert('控件异常，请到 [雷劈网] 反馈或寻求帮助！');
            }
            return false;
        }
    } else {
        oNode.setAttribute('title', $G('orgname').value); 
        oNode.setAttribute('size',gSize);
        if( gWidth != '' ) {
			oNode.style.width = gWidth + 'px';
			oNode.setAttribute('orgwidth',gWidth );
		}
        // Remove all options.
        while ( oNode.options.length > 0 ){
            oNode.remove(0) ;
        }
        for ( var i = 0 ; i < $G('orglist').options.length ; i++ ) {
            var sText    = $G('orglist').options[i].value ;
            if ( sText.length == 0 ) {
                sText = sText ;
            }
            var oOption = fnAddComboOption( oNode, sText, sText ) ;
            if ( sText == $G('orgvalue').innerHTML ) {
                fnSetAttribute( oOption, 'selected', 'selected' ) ;
                oOption.selected = true ;
            }
        }
        delete UE.plugins[thePlugins].editdom; 
    }*/
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