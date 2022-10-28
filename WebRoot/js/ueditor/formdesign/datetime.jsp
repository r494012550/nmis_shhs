<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>${sessionScope.locale.get('admin.textbox')}</title>
    <%@ include file="/common/meta.jsp"%>
    <!--<link rel="stylesheet" type="text/css" href="/report/themes/metro-blue/easyui.css">
    <link rel="stylesheet" href="bootstrap/css/bootstrap.css">
    [if lte IE 6]>
    <link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap-ie6.css">
    <![endif]-->
    <!--[if lte IE 7]>
    <link rel="stylesheet" type="text/css" href="bootstrap/css/ie.css"><link rel="stylesheet" href="leipi.style.css">
    <![endif]
    
    <script type="text/javascript" src="../dialogs/internal.js"></script>-->
    <script type="text/javascript">
/*
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
}*/
    </script>
</head>
<body>
<form id="datetime_form" method="post">
<div style="padding:10px 10px 5px 10px;margin-left:auto;margin-right:auto;width:350px;height:240px;">
	<div style="margin-bottom:5px">
				<input id="orgname" class='easyui-textbox tb' label="${sessionScope.locale.get('report.name')}:" labelWidth='110' labelAlign="right" style="width:280px;height:30px;" 
					data-options="prompt:'${sessionScope.locale.get('admin.entername')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" name="name" value=""/>
	</div>
	<div style="margin-bottom:5px">
       	<input id="datetime_code" class='easyui-textbox tb' label="${sessionScope.locale.get('admin.code')}:" labelWidth='110' labelAlign="right" name="code" style="width:280px;height:30px;"  
       		data-options="prompt:'${sessionScope.locale.get('admin.choosecode')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" readonly='readonly' value=""/>
       	<a class="easyui-linkbutton"  style="width:60px;height:28px;margin-left:3px;" onclick="openStandardCodWindow_('datetime_code')">${sessionScope.locale.get('admin.code')}</a>
    </div>
    <div style="margin-bottom:5px">
		<input class="easyui-radiobutton" id="com_type_date" name="com_type" value="date" data-options="checked:true" label="类型:" labelWidth='110'  labelAlign="right">&nbsp;日期&nbsp;&nbsp;
		<input class="easyui-radiobutton" id="com_type_datetime" name="com_type" value="datetime" labelWidth='110'>&nbsp;日期时间
	</div>
	<%-- <div style="margin-bottom:5px">
		<input class="easyui-textbox" label="${sessionScope.locale.get('admin.defaultvalue')}:" labelWidth='100' id="orgvalue" 
			data-options="prompt:'${sessionScope.locale.get('admin.enterdefaultvalue')}...'" value="" style="width:280px;height:30px;">
	</div> --%>
	<div style="margin-bottom:5px">
		<input id="orgwidth" class="easyui-numberbox tb" label="${sessionScope.locale.get('admin.width')}:" labelWidth='110' labelAlign="right" value="100" style="width:280px;height:30px;"  data-options="prompt:'${sessionScope.locale.get('admin.entercomponentwidth')}...'"/> px
	</div>
	<div style="margin-bottom:5px">
		<input id="orgheight" class="easyui-numberbox tb" label="${sessionScope.locale.get('admin.height')}:" labelWidth='110' labelAlign="right" value="20" style="width:280px;height:30px;"  data-options="prompt:'${sessionScope.locale.get('admin.entercomponentheight')}...'"/> px
	</div>

    <div style="margin-bottom:5px">
    	<input class="easyui-checkbox" id="textinput_required" name="required" label=" ${sessionScope.locale.get('required')}:" labelWidth="110" labelAlign="right">
    </div>
    <div style="margin-bottom:5px">
    	<input class="easyui-checkbox" id="datetime_fixedwidth" name="fixedwidth" label="打印时固定宽度:" labelWidth="110" labelAlign="right">
    </div>
    <!-- <div style="margin-bottom:2px">
		<input id="datetime_sumtxt" class="easyui-textbox tb" label="总结文本:" labelWidth='100' multiline="true" style="width:345px;height:55px;"  name="summary_text"/>
	</div> -->
	<div style="margin-bottom:5px">
		<textarea id="datetime_sumtxt" class="easyui-textbox tb" label="总结文本-所见:" labelWidth='110' labelAlign="right" multiline="true" style="width:315px;height:50px;"  name="summary_text"></textarea>
		<a href="#" title="在总结文本中添加@{value}，提取总结文本时将被组件的值替换。" class="easyui-tooltip">帮助</a>
	</div>
	<div style="margin-bottom:2px">
		<textarea id="datetime_sumtxt_result" class="easyui-textbox tb" label="总结文本-结论:" labelWidth='110' labelAlign="right" multiline="true" style="width:315px;height:50px;"  name="summary_text_result"></textarea>
	</div>
	<!-- <div style="margin-bottom:5px;">
		<span style="font-size:5px;">(在总结文本中添加@{value}，提取总结文本时将被组件的值替换)</span>
	</div> -->
    
    <input type="hidden" name="id"  id="datetimeid"/>
    <input type="hidden" name="uid" id="datetimeuid"/>
    <input type="hidden" name="html" id="datetimehtml"/>
    <input type="hidden" id="component_type" name="type" value="4"/>
    <input type="hidden" id="datetime_path" name="path"/>
</div>
</form>

<script type="text/javascript">
var oNode = null,thePlugins = 'datetime';
$(document).ready(function(){
    if( UE.plugins[thePlugins].editdom ){
        oNode = UE.plugins[thePlugins].editdom;

		var gValue = '';
		if(oNode.getAttribute('value'))
			gValue = oNode.getAttribute('value').replace(/&quot;/g,"\"");
		var gTitle=oNode.getAttribute('title').replace(/&quot;/g,"\""),
		//gHidden=oNode.getAttribute('orghide'),
		//gFontSize=oNode.getAttribute('orgfontsize'),
		//gAlign=oNode.getAttribute('orgalign'),
		gWidth=oNode.getAttribute('orgwidth'),
		gHeight=oNode.getAttribute('orgheight'),
		//gType=oNode.getAttribute('orgtype');
		code=oNode.getAttribute('code'),
        ret=oNode.getAttribute('required'),
        id=oNode.getAttribute('id'),
        uid=oNode.getAttribute('uid'),
		fixedwidth=oNode.getAttribute('fixedwidth');

		//console.log("init");
		
		gValue = gValue==null ? '' : gValue;
        gTitle = gTitle==null ? '' : gTitle;
		//$('#orgvalue').val(gValue);
        $('#orgname').val(gTitle);
        //if (gHidden == '1')
        //{
        //    $('orghide').checked = true;
        //}
        //$('orgfontsize').value = gFontSize;
        $('#orgwidth').val(gWidth);
        $('#orgheight').val(gHeight);
        //$('orgalign').value = gAlign;
        //$('orgtype').value = gType;
        $('#datetime_code').val(code);
        $('#datetime_code').attr('_path',oNode.getAttribute('_path'));
        $('#datetime_sumtxt').val(oNode.getAttribute('summary'));
        $('#datetime_sumtxt_result').val(oNode.getAttribute('summary_result'));
        if(ret=="required"){
        	$('#textinput_required').attr("data-options","checked:true");
        }
        if(!fixedwidth||fixedwidth=='1'){
        	$('#datetime_fixedwidth').attr("data-options","checked:true");
        }
        
        if(oNode.getAttribute("datetype")=="date"){
        	$('#com_type_date').attr("data-options","checked:true");
        } else{
        	$('#com_type_datetime').attr("data-options","checked:true");
        }

        $('#datetimeid').val(id);
        $('#datetimeuid').val(uid);
    }
});
oncancel = function () {
    if( UE.plugins[thePlugins].editdom ) {
        delete UE.plugins[thePlugins].editdom;
    }
    if( UE.plugins[thePlugins].editway) {
    	delete UE.plugins[thePlugins].editway;
    }
};
onok = function (editor){

	if(!$('#orgname').val()||!$('#datetime_code').val()){
        return false;
    }
	
	//console.log($('#orgvalue').val())
    //var gValue=$('#orgvalue').val().replace(/\"/g,"&quot;"),
    var gTitle=$('#orgname').val().replace(/\"/g,"&quot;"),
    //gFontSize=$('orgfontsize').value,
    //gAlign=$('orgalign').value,
    gWidth=$('#orgwidth').val(),
    gHeight=$('#orgheight').val(),
    code=$('#datetime_code').val(),
    path=$('#datetime_code').attr('_path'),
    //req=$('#textinput_required').switchbutton('options').checked,
    req=$('#textinput_required').checkbox('options').checked,
    fixedwidth=$('#datetime_fixedwidth').checkbox('options').checked,
    id=$('#datetimeid').val(),
    uid=$('#datetimeuid').val();
    $.getJSON( window.localStorage.ctx+"/srtemplate/checkComponentName?name="+gTitle+"&uid="+$('#datetimeuid').val(), function(json){

 	   if(json.code == 0){
 			 
 			 if(json.data){
 				 $.messager.show({
 						title : $.i18n.prop('error'),
						msg : $.i18n.prop('admin.componentnameexists'),
 						timeout : 3000,
 						border:'thin',
 						showType : 'slide'
 				 });
 				 $('#orgname').textbox("setValue",'');
 			 }
 			 else{

 				 $('#datetime_form').form('submit', {
 					    url:window.localStorage.ctx+"/srtemplate/saveComponent_Input",
 					    onSubmit: function(){
 					    	if( !oNode ) {
 					        	oNode=pluginHandle.createElement("input","datetimeComponent");
 					        	oNode.setAttribute('id','@id@');
 					            oNode.setAttribute('uid','@uid@');
 					            //oNode.setAttribute("name","textinputComponent");
 					        }
 					    	if($('#com_type_date').radiobutton("options").checked){
 					    		oNode.setAttribute('class','easyui-datebox');
 					    		oNode.setAttribute('datetype','date');
 					        } else{
 					        	oNode.setAttribute('class','easyui-datetimebox');
 					        	oNode.setAttribute('datetype','datetime');
 					        }
 					    	oNode.setAttribute('summary',$('#datetime_sumtxt').val());
 					    	oNode.setAttribute('summary_result',$('#datetime_sumtxt_result').val());
 					        oNode.setAttribute('title', gTitle);
 					        oNode.setAttribute('value','');
 					        oNode.setAttribute('code',code);
 					       	oNode.setAttribute('_path',path);
 					        oNode.setAttribute('autocomplete',"off");
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

 					        if( gWidth != '' ) {
 					            oNode.style.width = gWidth+ 'px';
 					            oNode.setAttribute('orgwidth',gWidth );
 					        }else{
 					            oNode.setAttribute('orgwidth', '');
 					        }
 					        if( gHeight != '' ) {
 					            oNode.style.height = gHeight+ 'px';
 					            oNode.setAttribute('orgheight',gHeight );
 					        }else{
 					            oNode.setAttribute('orgheight', '');
 					        }
 					        
					        oNode.setAttribute('data-options',"onChange:function(){datetime_onchange_handle(this)}");
 					        //console.log(oNode.outerHTML)
 					        $('#datetimehtml').val(oNode.outerHTML);
 					       	$('#datetime_path').val(path);
 					    },
 					    success:function(data){
 					    	var json = validationDataAll(data);
 					        if(json.code==0){
 					        	if(!UE.plugins[thePlugins].editway){
	 					        	if(id!=""){
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
</script>
</body>
</html>