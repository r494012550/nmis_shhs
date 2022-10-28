<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>${sessionScope.locale.get('admin.textarea')}</title>
    <%@ include file="/common/meta.jsp"%>
    <!--<link rel="stylesheet" type="text/css" href="/report/themes/metro-blue/easyui.css">
    [if lte IE 6]><link rel="stylesheet" href="bootstrap/css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap-ie6.css">
    <![endif]-->
    <!--[if lte IE 7]>
    <link rel="stylesheet" type="text/css" href="bootstrap/css/ie.css"><link rel="stylesheet" href="leipi.style.css">
    <![endif]
    
    <script type="text/javascript" src="../dialogs/internal.js"></script>-->
</head>
<body>
<form id="textarea_form" method="post">
<div style="padding:10px 10px 5px 10px;margin-left:auto;margin-right:auto;width:380px;height:300px;">
	<div style="margin-bottom:10px">
				<input id="orgname" class='easyui-textbox tb' label="${sessionScope.locale.get('report.name')}:" labelWidth='100' labelAlign="right" style="width:280px;height:30px;" 
					data-options="prompt:'${sessionScope.locale.get('admin.entername')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" name="name" value=""/>
	</div>
	 <div style="margin-bottom:5px">
       	<input id="textarea_code" class='easyui-textbox tb' label="${sessionScope.locale.get('admin.code')}:" labelWidth='100' labelAlign="right" name="code" style="width:280px;height:30px;"  
       		data-options="prompt:'${sessionScope.locale.get('admin.choosecode')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" readonly='readonly' value=""/>
       	<a class="easyui-linkbutton"  style="width:60px;height:28px;margin-left:3px;" onclick="openStandardCodWindow_('textarea_code')">${sessionScope.locale.get('admin.code')}</a>
    </div>
	<div style="margin-bottom:5px">
		<input id="orgwidth" class="easyui-numberbox tb" label="${sessionScope.locale.get('admin.width')}:" labelWidth='100' labelAlign="right" value="300" style="width:280px;height:30px;"  data-options="prompt:'${sessionScope.locale.get('admin.entercomponentwidth')}...'"/> px
	</div>
	<div style="margin-bottom:5px">
		<input id="orgheight" class="easyui-numberbox tb" label="${sessionScope.locale.get('admin.height')}:" labelWidth='100' labelAlign="right" value="80" style="width:280px;height:30px;"  data-options="prompt:'${sessionScope.locale.get('admin.entercomponentheight')}...'"/> px
	</div>
	<!-- 
	<div style="margin-bottom:10px">
				<label class="checkbox"><input id="orgrich" type="checkbox"  /> 富文本形式 </label>
	</div> -->
	<div style="margin-bottom:5px">
		<textarea class="easyui-textbox tb" label="${sessionScope.locale.get('admin.defaultvalue')}:" labelWidth='100' labelAlign="right" id="orgvalue" multiline="true" 
			data-options="prompt:'${sessionScope.locale.get('admin.enterdefaultvalue')}...'" value="" style="width:280px;height:100px"></textarea>
	</div>
    <div style="margin-bottom:5px">
    	<!-- <input class="easyui-switchbutton tb" label="必填：" style="width:110px;" id="textarea_required" gchecked="" name="required"
    		data-options="onText:'开',offText:'关',handleText:'必填项',handleWidth:70" /> -->
    	<input class="easyui-checkbox" id="textarea_required" name="required" label=" ${sessionScope.locale.get('required')}:" labelWidth="100" labelAlign="right">
    </div>
    <div style="margin-bottom:5px">
    	<!-- <input class="easyui-switchbutton tb" label="必填：" style="width:110px;" id="textarea_required" gchecked="" name="required"
    		data-options="onText:'开',offText:'关',handleText:'必填项',handleWidth:70" /> -->
    	<input class="easyui-checkbox" id="textarea_summary" name="summaryarea" label=" 总结区:" labelWidth="100" labelAlign="right"
    		data-options="onChange:function(checked){
    			if(checked){
    				$('#textarea_sumtxt').textbox('disable');
    				$('#textarea_sumtxt_result').textbox('disable');
    				$('#test_desc_btn').linkbutton('disable');
    				$('#test_result_btn').linkbutton('disable');
    			}else{
    				$('#textarea_sumtxt').textbox('enable');
    				$('#textarea_sumtxt_result').textbox('enable');
    				$('#test_desc_btn').linkbutton('enable');
    				$('#test_result_btn').linkbutton('enable');
    			}}">
    </div>
    <!-- <div style="margin-bottom:2px">
		<input id="textarea_sumtxt" class="easyui-textbox tb" label="总结文本:" labelWidth='100' multiline="true" style="width:345px;height:55px;"  name="summary_text"
			data-options="disabled:false"/>
	</div> -->
	<div style="margin-bottom:5px;height:40px;">
		<div style="float:left;width:315px;">
			<textarea id="textarea_sumtxt" class="easyui-textbox tb" label="总结文本-所见:" labelWidth='100' labelAlign="right" multiline="true" style="width:315px;height:50px;"  name="summary_text"
				data-options="disabled:false"></textarea>
		</div>
		<div style="float:right;width:50px;padding-top:5px;">
			<a id="test_desc_btn" title="测试提取结果" class="easyui-linkbutton" data-options="disabled:true" style="width:50px;" onclick="openTestComponentDlg('textarea_sumtxt','summary')">测试</a>
			<a href="#" title="在总结文本中添加@{value}，提取总结文本时将被组件的值替换。" class="easyui-tooltip">帮助</a>
		</div>
	</div>
	<div style="margin-bottom:5px;height:5px;"></div>
	<div style="margin-bottom:2px">
		<div style="float:left;width:315px;">
			<textarea id="textarea_sumtxt_result" class="easyui-textbox tb" label="总结文本-结论:" labelWidth='100' labelAlign="right" multiline="true" style="width:315px;height:50px;"  name="summary_text_result"
				data-options="disabled:false"></textarea>
		</div>
		<div style="float:right;width:50px;padding-top:5px;">
			<a id="test_result_btn" title="测试提取结果" class="easyui-linkbutton" data-options="disabled:true" style="width:50px;" onclick="openTestComponentDlg('textarea_sumtxt_result','summary_result')">测试</a>
		</div>
	</div>
	<!-- <div style="margin-bottom:5px;">
		<span style="font-size:5px;">(在总结文本中添加@{value}，提取总结文本时将被组件的值替换)</span>
	</div> -->
    
    <input type="hidden" name="id"  id="textareaid"/>
    <input type="hidden" name="uid" id="textareauid"/>
    <input type="hidden" name="html" id="textareahtml"/>
    <input type="hidden" name="type" value="2"/>
    <input type="hidden" id="textarea_path" name="path"/>
</div>
</form>
<script type="text/javascript">
var oNode = null,thePlugins = 'textarea';

$(document).ready(function(){
	
	if( UE.plugins[thePlugins].editdom ){
        oNode = UE.plugins[thePlugins].editdom;
        var gValue = oNode.getAttribute('value').replace(/&quot;/g,"\""),
        gTitle=oNode.getAttribute('title').replace(/&quot;/g,"\""),
        //gFontSize=oNode.getAttribute('orgfontsize'),
        gWidth=oNode.getAttribute('orgwidth'),
        gHeight=oNode.getAttribute('orgheight'),
        code=oNode.getAttribute('code'),
        //gRich=oNode.getAttribute('orgrich');
        ret=oNode.getAttribute('required'),
        id=oNode.getAttribute('id'),
        uid=oNode.getAttribute('uid');
        //console.log(ret);

        
        
        gValue = gValue==null ? '' : gValue;
        gTitle = gTitle==null ? '' : gTitle;
        $('#orgvalue').val(gValue);
        //$G('orgvalue').value = gValue;
        $('#orgname').val(gTitle);
        //$G('orgname').value = gTitle;
       // if ( gRich == '1' ) {
            //$G('orgrich').checked = true ;
        //}
        //$('#orgfontsize').val(gFontSize)
        //$G('orgfontsize').value = gFontSize;
        $('#orgwidth').val(gWidth);
        //$G('orgwidth').value = gWidth;
        $('#orgheight').val(gHeight);
        $('#textarea_code').val(code);
        $('#textarea_code').attr('_path',oNode.getAttribute('_path'));
        if(ret=="required"){
        	//$('#textarea_required').switchbutton({checked:true});
        	$('#textarea_required').attr("data-options","checked:true");
        }
        
        if(oNode.getAttribute('issummary')=="true"){
        	$('#textarea_summary').attr("data-options","checked:true,onChange:function(checked){if(checked){$('#textarea_sumtxt').textbox('disable');$('#textarea_sumtxt_result').textbox('disable');$('#test_desc_btn').linkbutton('disable');$('#test_result_btn').linkbutton('disable');}else{$('#textarea_sumtxt').textbox('enable');$('#textarea_sumtxt_result').textbox('enable');$('#test_desc_btn').linkbutton('enable');$('#test_result_btn').linkbutton('enable');}}");
        	$('#textarea_sumtxt').attr("data-options","disabled:true");
        	$('#textarea_sumtxt_result').attr("data-options","disabled:true");
        }
        else{
        	$('#textarea_sumtxt').val(oNode.getAttribute('summary'));
        	$('#textarea_sumtxt_result').val(oNode.getAttribute('summary_result'));
        }

        $('#textareaid').val(id);
        $('#textareauid').val(uid);
        //$G('orgheight').value = gHeight;
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
    if(!$('#orgname').val()||!$('#textarea_code').val()){
        return false;
    }
    var gValue=$('#orgvalue').val(),//.replace(/\"/g,"&quot;"),
    gTitle=$('#orgname').textbox('getValue').replace(/\"/g,"&quot;"),
    //gFontSize=$('#orgfontsize').val(),
    gWidth=$('#orgwidth').val(),
    gHeight=$('#orgheight').val(),
    code=$('#textarea_code').val(),
    path=$('#textarea_code').attr('_path'),
    //req=$('#textarea_required').switchbutton('options').checked,
    req=$('#textarea_required').checkbox('options').checked,
    id=$('#textareaid').val(),
    uid=$('#textareauid').val();

   $.getJSON( window.localStorage.ctx+"/srtemplate/checkComponentName?name="+gTitle+"&uid="+$('#textareauid').val(), function(json){

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

				 $('#textarea_form').form('submit', {
					    url:window.localStorage.ctx+"/srtemplate/saveComponent_Input",
					    onSubmit: function(){
					    	if( !oNode ) {
					        	oNode=pluginHandle.createElement("textarea","textareaComponent");
					        	oNode.setAttribute('id','@id@');
					            oNode.setAttribute('uid','@uid@');
					        }

					        oNode.setAttribute('title', gTitle);
					        oNode.setAttribute('value',gValue);
					        oNode.setAttribute('code',code);
					        oNode.setAttribute('_path',path);
					        oNode.innerHTML = gValue;
					        
					        oNode.setAttribute('onchange',"textarea_onchange_handle(this);");
					        oNode.removeAttribute('data-options');//为了兼容老版本，需要清空data-options
					        oNode.removeAttribute('class');//为了兼容老版本，需要清空class
					       if($('#textarea_summary').checkbox('options').checked){
					    	   //oNode.setAttribute('class','easyui-tooltip');
					    	   oNode.setAttribute('issummary','true');
					    	   //oNode.setAttribute('data-options',"position:'right',hideEvent: 'none',"+
					    		//	   "content: function(){return getTooltipContent($(this));},"+
					    	   	//		"onShow: function(e){var t = $(this);t.tooltip('tip').focus().unbind().bind('blur',function(){t.tooltip('hide');});}");
					    			   //"onShow: function(e){var t = $(this);t.tooltip('tip').unbind().bind('mouseenter', function(){t.tooltip('show');}).bind('mouseleave', function(){t.tooltip('hide');});}");
					    	   oNode.setAttribute('summary',"");
					    	   oNode.setAttribute('summary_result',"");
					       }
					       else{
					    	   oNode.setAttribute('issummary','false');
					    	   oNode.setAttribute('summary',$('#textarea_sumtxt').val());
					    	   oNode.setAttribute('summary_result',$('#textarea_sumtxt_result').val());
					       }
					        
					        
					        if(req){
					        	oNode.setAttribute('required','required');
					        	oNode.removeAttribute('optional');
					        }
					        else{
					        	oNode.setAttribute('optional','optional');
					        	oNode.removeAttribute('required');
					        }
					        
					        oNode.style.float='';
					        
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

					        $('#textareahtml').val(oNode.outerHTML);
					        $('#textarea_path').val(path);
					    },
					    success:function(data){
					    	var json = validationDataAll(data);
					        if(json.code==0){
					        	
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

/*
    if( !oNode ) {
        try {

        	

        	oNode.setAttribute("title",gTitle);
        	oNode.setAttribute("name","textareaComponent");
        	oNode.setAttribute("code",code);
        	oNode.setAttribute("value",gValue);
        	if(req){
            	oNode.setAttribute("required","required");
            }

        	if( gWidth != '' ) {
                oNode.setAttribute("orgwidth",gWidth);
                oNode.style.width=gWidth+"px";
            } else {
                oNode.setAttribute("orgwidth","");
            }
            if(gHeight != '') {
                oNode.setAttribute("orgheight",gHeight);
                oNode.style.height=gHeight+"px";
            } else {
                oNode.setAttribute("orgheight","");
            }
        	//oNode.setAttribute("style","");
        	
        	oNode.value=gValue;

        	console.log(oNode.outerHTML);*/

            
            /*var html = '<textarea ';
            html += ' title = "' + gTitle + '"';
            html += ' name = "textareaComponent"';
            html += ' code = "' + code + '"';
            //html += ' leipiPlugins = "'+thePlugins+'"';
            html += ' value = "' + gValue + '"';
            //if ( $('#orgrich') ) {
            //    html += ' orgrich = "1"';
            //} else {
            //    html += ' orgrich = "0"';
            //}
            //if( gFontSize != '' ) {
            //    html += ' orgfontsize = "' + gFontSize + '"';
            //} else {
            //    html += ' orgfontsize = ""';
            //}
            
            if(req){
            	html += ' required = "required"';
            }
            
            if( gWidth != '' ) {
                html += ' orgwidth = "' + gWidth + '"';
            } else {
                html += ' orgwidth = ""';
            }
            if(gHeight != '') {
                html += ' orgheight = "' + gHeight + '"';
            } else {
                html += ' orgheight = ""';
            }
            
            html += ' style = "';
            //if( gFontSize != '' ) {
            //    html += 'font-size:' + gFontSize + 'px;';
            //}
            if( gWidth != '' ) {
                html += 'width:' + gWidth + 'px;';
            }
            if( gHeight != '' ) {
                html += 'height:' + gHeight + 'px;';
            }
            html += '">';
            html += gValue + '</textarea>';
            editor.execCommand('insertHtml',html);
        } catch (e) {
            console.log(e)
            try {
                editor.execCommand('error');
            } catch ( e ) {
                alert('控件异常，请到 [雷劈网] 反馈或寻求帮助！');
            }
            return false;
        }
    } else {
        
        delete UE.plugins[thePlugins].editdom;
    }

    $('#common_dialog').dialog('close');*/
};
</script>
</body>
</html>