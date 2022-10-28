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
<form id="textinput_form" method="post">
<div style="padding:10px 10px 5px 10px;margin-left:auto;margin-right:auto;width:350px;height:240px;">
	<div style="margin-bottom:5px">
				<input id="orgname" class='easyui-textbox tb' label="${sessionScope.locale.get('report.name')}:" labelWidth='110' labelAlign="right" style="width:280px;height:30px;" 
					data-options="prompt:'${sessionScope.locale.get('admin.entername')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" name="name" value=""/>
	</div>
	<div style="margin-bottom:5px">
       	<input id="textinput_code" class='easyui-textbox tb' label="${sessionScope.locale.get('admin.code')}:" labelWidth='110' labelAlign="right" name="code" style="width:280px;height:30px;"  
       		data-options="prompt:'${sessionScope.locale.get('admin.choosecode')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" readonly='readonly' value=""/>
       	<a class="easyui-linkbutton"  style="width:60px;height:28px;margin-left:3px;" onclick="openStandardCodWindow_('textinput_code')">${sessionScope.locale.get('admin.code')}</a>
    </div>
	<div style="margin-bottom:5px">
		<input class="easyui-textbox" label="${sessionScope.locale.get('admin.defaultvalue')}:" labelWidth='110' labelAlign="right" id="orgvalue" data-options="prompt:'${sessionScope.locale.get('admin.enterdefaultvalue')}...'" value="" style="width:280px;height:30px;">
	</div>
	<div style="margin-bottom:5px">
		<input id="orgwidth" class="easyui-numberbox tb" label="${sessionScope.locale.get('admin.width')}:" labelWidth='110' labelAlign="right" value="100" style="width:280px;height:30px;"  data-options="prompt:'${sessionScope.locale.get('admin.entercomponentwidth')}...'"/> px
	</div>
	<div style="margin-bottom:5px">
		<input id="orgheight" class="easyui-numberbox tb" label="${sessionScope.locale.get('admin.height')}:" labelWidth='110' labelAlign="right" value="20" style="width:280px;height:30px;"  data-options="prompt:'${sessionScope.locale.get('admin.entercomponentheight')}...'"/> px
	</div>
	<!-- 
	<div style="margin-bottom:10px">
				<label class="checkbox"><input id="orgrich" type="checkbox"  /> 富文本形式 </label>
	</div> -->
    <div style="margin-bottom:5px">
    	<!-- <input class="easyui-switchbutton tb" label="必填：" style="width:110px;" id="textinput_required" gchecked="" name="required"
    		data-options="onText:'开',offText:'关',handleText:'必填项',handleWidth:70" /> -->
    	<input class="easyui-checkbox" id="textinput_required" name="required" label=" ${sessionScope.locale.get('required')}:" labelWidth="110" labelAlign="right">
    </div>
    <div style="margin-bottom:5px">
    	<!-- <input class="easyui-switchbutton tb" label="必填：" style="width:110px;" id="textinput_required" gchecked="" name="required"
    		data-options="onText:'开',offText:'关',handleText:'必填项',handleWidth:70" /> -->
    	<input class="easyui-checkbox" id="textinput_fixedwidth" name="fixedwidth" label="打印时固定宽度:" labelWidth="110" labelAlign="right">
    </div>
    <!-- <div style="margin-bottom:2px">
		<input id="textinput_sumtxt" class="easyui-textbox tb" label="总结文本:" labelWidth='100' multiline="true" style="width:345px;height:55px;"  name="summary_text"/>
	</div> -->
	<div style="margin-bottom:5px;width:100%;height:40px;">
		<div style="float:left;width:100%;">
			<div style="float:left;width:90%;">
				<textarea id="textinput_sumtxt" class="easyui-textbox tb" label="总结文本-所见:" labelWidth='110' labelAlign="right" multiline="true" style="width:315px;height:50px;"  name="summary_text"></textarea>
			</div>
			<div style="float:right;width:10%;padding-top:5px;">
				<a href="#" title="测试提取结果" class="easyui-tooltip" onclick="openTestComponentDlg('textinput_sumtxt','summary')">测试</a>
				<a href="#" title="在总结文本中添加@{value}，提取总结文本时将被组件的值替换。" class="easyui-tooltip">帮助</a>
			</div>
		</div>
	</div>
	<div style="margin-bottom:5px;height:5px;"></div>
	<div style="margin-bottom:2px;width:100%;">
		<div style="float:left;width:90%;">
		<textarea id="textinput_sumtxt_result" class="easyui-textbox tb" label="总结文本-结论:" labelWidth='110' labelAlign="right" multiline="true" style="width:315px;height:50px;"  name="summary_text_result"></textarea>
		</div>
		<div style="float:right;width:10%;padding-top:5px;">
			<a href="#" title="测试提取结果" class="easyui-tooltip" onclick="openTestComponentDlg('textinput_sumtxt_result','summary_result')">测试</a>
		</div>
	</div>
	<!-- <div style="margin-bottom:5px;">
		<span style="font-size:5px;">(在总结文本中添加@{value}，提取总结文本时将被组件的值替换)</span>
	</div> -->
    
    <input type="hidden" name="id"  id="textinputid"/>
    <input type="hidden" name="uid" id="textinputuid"/>
    <input type="hidden" name="html" id="textinputhtml"/>
    <input type="hidden" id="component_type" name="type" value="3"/>
    <input type="hidden" id="textinput_path" name="path"/>
</div>
</form>

<script type="text/javascript">
var oNode = null,thePlugins = 'textinput';
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
		$('#orgvalue').val(gValue);
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
        $('#textinput_code').val(code);
        $('#textinput_code').attr('_path',oNode.getAttribute('_path'));
        $('#textinput_sumtxt').val(oNode.getAttribute('summary'));
        $('#textinput_sumtxt_result').val(oNode.getAttribute('summary_result'));
        if(ret=="required"){
        	$('#textinput_required').attr("data-options","checked:true");
        }
        if(!fixedwidth||fixedwidth=='1'){
        	$('#textinput_fixedwidth').attr("data-options","checked:true");
        }

        $('#textinputid').val(id);
        $('#textinputuid').val(uid);
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

	if(!$('#orgname').val()||!$('#textinput_code').val()){
        return false;
    }
	
	console.log($('#orgvalue').val())
    var gValue=$('#orgvalue').val().replace(/\"/g,"&quot;"),
    gTitle=$('#orgname').val().replace(/\"/g,"&quot;"),
    //gFontSize=$('orgfontsize').value,
    //gAlign=$('orgalign').value,
    gWidth=$('#orgwidth').val(),
    gHeight=$('#orgheight').val(),
    code=$('#textinput_code').val(),
    path=$('#textinput_code').attr('_path'),
    //req=$('#textinput_required').switchbutton('options').checked,
    req=$('#textinput_required').checkbox('options').checked,
    fixedwidth=$('#textinput_fixedwidth').checkbox('options').checked,
    id=$('#textinputid').val(),
    uid=$('#textinputuid').val();

    $.getJSON( window.localStorage.ctx+"/srtemplate/checkComponentName?name="+gTitle+"&uid="+$('#textinputuid').val(), function(json){

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

 				 $('#textinput_form').form('submit', {
 					    url:window.localStorage.ctx+"/srtemplate/saveComponent_Input",
 					    onSubmit: function(){
 					    	if( !oNode ) {
 					        	oNode=pluginHandle.createElement("input","textinputComponent");
 					        	oNode.setAttribute('type','text');
 					        	oNode.setAttribute('id','@id@');
 					            oNode.setAttribute('uid','@uid@');
 					            //oNode.setAttribute("name","textinputComponent");
 					        }
 					    	oNode.setAttribute('summary',$('#textinput_sumtxt').val());
 					    	oNode.setAttribute('summary_result',$('#textinput_sumtxt_result').val());
 					        oNode.setAttribute('title', gTitle);
 					        oNode.setAttribute('value',gValue);
 					        oNode.setAttribute('code',code);
 					       	oNode.setAttribute('_path',path);
 					        oNode.setAttribute('autocomplete',"off");
 					        oNode.innerHTML = gValue;
 					        oNode.setAttribute('onchange',"text_onchange_handle(this);");
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

 					        $('#textinputhtml').val(oNode.outerHTML);
 					       	$('#textinput_path').val(path);
 					        
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






/*
    
    
    if( !oNode ) {
        try {
            oNode = createElement('input','textinputComponent');
            oNode.setAttribute('type','text');
            oNode.setAttribute('title',gTitle);
            oNode.setAttribute('id',window.localStorage.getItem("id"));
            oNode.setAttribute('code',window.localStorage.getItem("code"));
            oNode.setAttribute('value','');
            oNode.setAttribute('name','textinputComponent');
            oNode.onchange=function(e){
                alert(e);
            	oNode.setAttribute('value','');
            };

            //onchange="this.setAttribute(&#39;value&#39;,this.value);
            
            if ( $G('orghide').checked ) {
                oNode.setAttribute('orghide',1);
            } else {
                oNode.setAttribute('orghide',0);
            }
            if( gFontSize != '' ) {
                oNode.style.fontSize = gFontSize + 'px';
                //style += 'font-size:' + gFontSize + 'px;';
                oNode.setAttribute('orgfontsize',gFontSize );
            }
            if( gAlign != '' ) {
                //style += 'text-align:' + gAlign + ';';
                oNode.style.textAlign = gAlign;
                oNode.setAttribute('orgalign',gAlign );
            }
            if( gWidth != '' ) {
                oNode.style.width = gWidth+ 'px';
                //style += 'width:' + gWidth + 'px;';
                //oNode.setAttribute( 'disabled','disabled' );
                oNode.setAttribute('orgwidth',gWidth );
            }
            if( gHeight != '' ) {
                oNode.style.height = gHeight+ 'px';
                //style += 'height:' + gHeight + 'px;';
                oNode.setAttribute('orgheight',gHeight );
            }
            if( gType != '' ) {
                oNode.setAttribute('orgtype',gType );
            }
            //oNode.setAttribute('style',style );
            //oNode.style.cssText=style;//ie7
            if(window.localStorage.getItem("text_flag") == 0){
            	 editor.execCommand('insertHtml','');
            }else{
            	 editor.execCommand('insertHtml',oNode.outerHTML+"<span>"+window.localStorage.getItem('unit')+"</span>");
            }
           
        } catch (e) {
            try {
                editor.execCommand('error');
            } catch ( e ) {
                alert('控件异常，请到 [雷劈网] 反馈或寻求帮助！');
            }
            return false;
        }
    } else {
        oNode.setAttribute('title', gTitle);
        oNode.setAttribute('value', $G('orgvalue').value);
        if( $G('orghide').checked ) {
            oNode.setAttribute('orghide', 1);
        } else {
            oNode.setAttribute('orghide', 0);
        }
        if( gFontSize != '' ) {
            oNode.style.fontSize = gFontSize+ 'px';
            oNode.setAttribute('orgfontsize',gFontSize );
        }else{
            oNode.style.fontSize = '';
            oNode.setAttribute('orgfontsize', '');
        }
        if( gAlign != '' ) {
            oNode.style.textAlign = gAlign;
            oNode.setAttribute('orgalign',gAlign );
        }else{
            oNode.setAttribute('orgalign', '');
        }
        if( gWidth != '' ) {
            oNode.style.width = gWidth+ 'px';
            oNode.setAttribute('orgwidth',gWidth );
        }else{
            oNode.style.width = '';
            oNode.setAttribute('orgwidth', '');
        }
        if( gHeight != '' ) {
            oNode.style.height = gHeight+ 'px';
            oNode.setAttribute('orgheight',gHeight );
        }else{
            oNode.style.height = '';
            oNode.setAttribute('orgheight', '');
        }
        if( gType != '' ) {
            oNode.setAttribute('orgtype',gType );
        }else{
            oNode.setAttribute('orgtype', '');
        }
        delete UE.plugins[thePlugins].editdom;
    }

    */
};
</script>
</body>
</html>