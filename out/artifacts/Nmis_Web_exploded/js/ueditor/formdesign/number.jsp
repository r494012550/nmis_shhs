<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>${sessionScope.locale.get('admin.numberbox')}</title>
    <%@ include file="/common/meta.jsp"%>
</head>
<body>
<form id="number_form" method="post">
<div style="padding:10px 10px 5px 10px;margin-left:auto;margin-right:auto;width:450px;height:240px;">
	<div style="margin-bottom:5px">
				<input id="orgname" class='easyui-textbox tb' label="${sessionScope.locale.get('report.name')}:" labelWidth='110' labelAlign="right" style="width:380px;height:30px;" 
					data-options="prompt:'${sessionScope.locale.get('admin.entername')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" name="name" value=""/>
	</div>
	<div style="margin-bottom:5px">
       	<input id="number_code" class='easyui-textbox tb' label="${sessionScope.locale.get('admin.code')}:" labelWidth='110' labelAlign="right" name="code" style="width:380px;height:30px;"  
       		data-options="prompt:'${sessionScope.locale.get('admin.choosecode')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" readonly='readonly' value=""/>
       	<a class="easyui-linkbutton"  style="width:60px;height:28px;margin-left:3px;" onclick="openStandardCodWindow_('number_code')">${sessionScope.locale.get('admin.code')}</a>
    </div>
	<div style="margin-bottom:5px">
		<input class="easyui-numberbox" label="${sessionScope.locale.get('admin.defaultvalue')}:" labelWidth='110' labelAlign="right" id="orgvalue" 
			data-options="prompt:'${sessionScope.locale.get('admin.enterdefaultvalue')}...'" value="" style="width:380px;height:30px;">
	</div>
	<div style="margin-bottom:5px">
		<input id="orgunit" name="unit" class="easyui-combobox" label="${sessionScope.locale.get('admin.unit')}:" labelWidth='110' labelAlign="right" style="width:380px;height:30px;" 
			data-options="prompt:'${sessionScope.locale.get('admin.chooseunit')}...',valueField:'name_zh',textField:'name_zh',url:'syscode/getCode?type=0006',editable:false" >
	</div>
	<div style="margin-bottom:5px">
		<input id="orgmin" class="easyui-numberbox tb" label="最小值:" labelWidth='110' labelAlign="right" style="width:380px;height:30px;"  
			data-options="prompt:'请输入最小值...'"/>
	</div>
	<div style="margin-bottom:5px">
		<input id="orgmax" class="easyui-numberbox tb" label="最大值:" labelWidth='110' labelAlign="right" style="width:380px;height:30px;"  
			data-options="prompt:'请输入最大值...'"/>
	</div>
	<div style="margin-bottom:5px">
		<input id="orgwidth" class="easyui-numberbox tb" label="${sessionScope.locale.get('admin.width')}:" labelWidth='110' labelAlign="right" value="100" style="width:380px;height:30px;"  data-options="prompt:'${sessionScope.locale.get('admin.entercomponentwidth')}...'"/> px
	</div>
	<div style="margin-bottom:5px">
		<input id="orgheight" class="easyui-numberbox tb" label="${sessionScope.locale.get('admin.height')}:" labelWidth='110' labelAlign="right" value="20" style="width:380px;height:30px;"  data-options="prompt:'${sessionScope.locale.get('admin.entercomponentheight')}...'"/> px
	</div>
	<div style="margin-bottom:5px">
    	<input class="easyui-checkbox" id="number_required" name="required" label=" ${sessionScope.locale.get('required')}:" labelWidth="110" labelAlign="right">
    	<!-- <input class="easyui-switchbutton tb" label="必填：" style="width:110px;" id="number_required" gchecked="" name="required"
    		data-options="onText:'开',offText:'关',handleText:'必填项',handleWidth:70" /> -->
    </div>
    <div style="margin-bottom:5px">
    	<input class="easyui-checkbox" id="number_fixedwidth" name="fixedwidth" label="打印时固定宽度:"" labelWidth="110" labelAlign="right">
    </div>
    <!-- <div style="margin-bottom:2px">
		<input id="number_sumtxt" class="easyui-textbox tb" label="总结文本:" labelWidth='100' multiline="true" style="width:345px;height:55px;"  name="summary_text"/>
	</div> -->
	<div style="margin-bottom:5px">
		<textarea id="number_sumtxt" class="easyui-textbox tb" label="总结文本-所见:" labelWidth='110' labelAlign="right" multiline="true" style="width:410px;height:50px;"  name="summary_text"></textarea>
		<a href="#" title="测试提取结果" class="easyui-tooltip" onclick="openTestComponentDlg('number_sumtxt','summary')">测试</a>
		<!-- <a href="#" title="在总结文本中添加@{value}，提取总结文本时将被组件的值替换。" class="easyui-tooltip">帮助</a> -->
	</div>
	<div style="margin-bottom:5px">
		<textarea id="number_sumtxt_result" class="easyui-textbox tb" label="总结文本-结论:" labelWidth='110' labelAlign="right" multiline="true" style="width:410px;height:50px;"  name="summary_text_result"></textarea>
		<a href="#" title="测试提取结果" class="easyui-tooltip" onclick="openTestComponentDlg('number_sumtxt_result','summary_result')">测试</a>
	</div>
	<div style="margin-bottom:2px">
       	<textarea id="formula" class='easyui-textbox tb' label="表达式:" labelWidth='110' labelAlign="right" multiline="true" style="width:410px;height:75px;"  
       		data-options="prompt:'${sessionScope.locale.get('admin.choosecode')}...'" readonly='readonly' value="">
       	</textarea>
       	<a class="easyui-linkbutton"  style="width:30px;height:28px;margin-left:3px;" onclick="openEditFormulaDialog('formula')">...</a>
    </div>
	<!-- <div style="margin-bottom:5px;">
		<a href="#" title="在总结文本中添加@{value}，提取总结文本时将被组件的值替换。" class="easyui-tooltip">帮助</a>
	</div> -->
	<!-- 
	<div style="margin-bottom:10px">
				<label class="checkbox"><input id="orgrich" type="checkbox"  /> 富文本形式 </label>
	</div> -->
   
    
    <input type="hidden" name="id"  id="numberid"/>
    <input type="hidden" name="uid" id="numberuid"/>
    <input type="hidden" name="html" id="numberhtml"/>
    <input type="hidden" id="component_type" name="type" value="0"/>
    <input type="hidden" id="number_path" name="path"/>
</div>
</form>

<script type="text/javascript">
var oNode = null,thePlugins = 'numberinput';
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
        unit=oNode.getAttribute('unit'),
		fixedwidth=oNode.getAttribute('fixedwidth');
		if(id&&!isNaN(id)){//将老版本中整数的id替换为uuid
        	id=uuid();
        	oNode.setAttribute('id',id);
        }
		gValue = gValue==null ? '' : gValue;
        gTitle = gTitle==null ? '' : gTitle;
        gTitle=gTitle.replace(/\[\d*-\d*\]/g,'');

		$('#orgvalue').val(gValue);
        $('#orgname').val(gTitle);
        $('#orgunit').val(unit);//combobox('select', unit);
        //if (gHidden == '1')
        //{
        //    $('orghide').checked = true;
        //}
        //$('orgfontsize').value = gFontSize;
        $('#orgmin').val(oNode.getAttribute('min'));
        $('#orgmax').val(oNode.getAttribute('max'));
        $('#orgwidth').val(gWidth);
        $('#orgheight').val(gHeight);
        //$('orgalign').value = gAlign;
        //$('orgtype').value = gType;
        $('#number_code').val(code);
        $('#number_code').attr('_path',oNode.getAttribute('_path'));
        $('#number_sumtxt').val(oNode.getAttribute('summary'));
        $('#number_sumtxt_result').val(oNode.getAttribute('summary_result'));
        $('#formula').val(oNode.getAttribute('formula'));
        if(ret=="required"){
        	//$('#number_required').switchbutton({checked:true});
        	$('#number_required').attr("data-options","checked:true");
        }
        if(!fixedwidth||fixedwidth=='1'){
        	$('#number_fixedwidth').attr("data-options","checked:true");
        }
        $('#numberid').val(id);
        $('#numberuid').val(uid);
    }
});
oncancel = function () {
    if( UE.plugins[thePlugins].editdom) {
        delete UE.plugins[thePlugins].editdom;
    }
    if( UE.plugins[thePlugins].editway) {
    	delete UE.plugins[thePlugins].editway;
    }
};
onok = function (editor){

	if(!$('#orgname').val()||!$('#number_code').val()){
        return false;
    }
	
	//console.log($('#orgvalue').val())
    var gValue=$('#orgvalue').val().replace(/\"/g,"&quot;"),
    gTitle=$('#orgname').val().replace(/\"/g,"&quot;"),
    //gFontSize=$('orgfontsize').value,
    //gAlign=$('orgalign').value,
    gWidth=$('#orgwidth').val(),
    gHeight=$('#orgheight').val(),
    code=$('#number_code').val(),
    path=$('#number_code').attr('_path'),
    //req=$('#number_required').switchbutton('options').checked,
    req=$('#number_required').checkbox('options').checked,
    fixedwidth=$('#number_fixedwidth').checkbox('options').checked,
    id=$('#numberid').val(),
    uid=$('#numberuid').val(),
    unit=$("#orgunit").textbox("getText"),
    min=$('#orgmin').val(),
    max=$('#orgmax').val();

    $.getJSON( window.localStorage.ctx+"/srtemplate/checkComponentName?name="+gTitle+"&uid="+uid, function(json){

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

 				 $('#number_form').form('submit', {
 					    url:window.localStorage.ctx+"/srtemplate/saveComponent_Input",
 					    onSubmit: function(){
 					    	if( !oNode ) {
 					        	oNode=pluginHandle.createElement("input","numberinputComponent");
 					        	oNode.setAttribute('type','number');
 					        	oNode.setAttribute('id','@id@');
 					            oNode.setAttribute('uid','@uid@');
 					            //oNode.setAttribute("name","textinputComponent");
 					        }

 					        oNode.setAttribute('title', gTitle+'['+min+'-'+max+']');
 					        oNode.setAttribute('value',gValue);
 					        oNode.setAttribute('unit',unit);
 					        oNode.setAttribute('min',min);
 					        oNode.setAttribute('max',max);
 					        oNode.setAttribute('code',code);
 					       	oNode.setAttribute('_path',path);
 					        oNode.setAttribute('summary',$('#number_sumtxt').val());
 					        oNode.setAttribute('summary_result',$('#number_sumtxt_result').val());
 					        oNode.setAttribute('formula',$.trim($('#formula').val()));
 					        oNode.innerHTML = gValue;
 					        oNode.setAttribute('onchange',"number_onchange_handle(this);");
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

 					        $('#numberhtml').val(oNode.outerHTML);
 					       	$('#number_path').val(path);
 					    },
 					    success:function(data){
 					    	var json = validationDataAll(data);
 					        if(json.code==0){
 					        	
 					        	if(!UE.plugins[thePlugins].editway){
	 					        	if(uid!=""){
		 					        	var iuid=oNode.getAttribute('iuid');
	 					        		console.log(iuid)
		 					        	if(iuid){
											var nextlabel=$(oNode).next("input[for='"+iuid+"']");
											if(nextlabel){
												nextlabel.attr("value",unit);
												nextlabel.html(unit);
											}
											else{
												$(oNode).after("<input type='button' for='"+iuid+"' uid='"+uid+"' disabled='disabled' style='border:0px;background: #FFFFFF;' value='"+unit+"'/>&nbsp;");
											}
		 					        	}
	 	 					        	
	 					        		delete UE.plugins[thePlugins].editdom;
	 					        	}
	 					        	else{
	 					        		oNode.setAttribute('id',json.data.uid);
	 						        	oNode.setAttribute('uid',json.data.uid);

	 						        	var unithtml="";
	 						        	if(unit){
		 						        	var iuid=json.data.uid+new Date().format('yyyyMMddhhmmssS')
		 						        	console.log("iuid="+iuid);
		 						        	oNode.setAttribute('iuid',iuid);
		 						        	unithtml="<input type='button' for='"+iuid+"' uid='"+json.data.uid+"' disabled='disabled' style='border:0px;background: #FFFFFF;' value='"+unit+"'/>&nbsp;";
	 						        	}
	 						        	oNode.setAttribute('objuid',uuid());
	 						        	editor.execCommand('insertHtml',oNode.outerHTML+unithtml);
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