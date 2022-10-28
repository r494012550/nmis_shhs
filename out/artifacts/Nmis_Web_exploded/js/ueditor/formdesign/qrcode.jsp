<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>${sessionScope.locale.get('admin.barcode')}</title>
    <%@ include file="/common/meta.jsp"%>
    <!--<link rel="stylesheet" type="text/css" href="themes/metro-blue/easyui.css">
    <link rel="stylesheet" href="bootstrap/css/bootstrap.css">
    [if lte IE 6]>
    <link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap-ie6.css">
    <![endif]-->
    <!--[if lte IE 7]>
    <link rel="stylesheet" type="text/css" href="bootstrap/css/ie.css"><link rel="stylesheet" href="leipi.style.css">
    <![endif]
    
    <script type="text/javascript" src="../dialogs/internal.js"></script>-->

</head>
<body>
<form id="number_form" method="post">
<div style="padding:20px;margin-left:auto;margin-right:auto;width:310px;height:240px;">
	<div style="margin-bottom:10px">
				<input id="orgname" class='easyui-textbox tb' label="${sessionScope.locale.get('report.name')}：" labelWidth="110" style="width:280px;height:30px;" 
					data-options="prompt:'${sessionScope.locale.get('admin.entername')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" name="name" value=""/>
	</div>
	<div style="margin-bottom:10px">
		<select id="code" class="easyui-combobox" name="code" labelWidth="110" label="${sessionScope.locale.get('admin.code')}：" style="width:280px;height:30px;" data-options="editable:false">
		    <option value="00100020">${sessionScope.locale.get('admin.patientid')}</option>
		    <option value="00200010">${sessionScope.locale.get('admin.studyid')}</option>
		    <option value="00080050">访问编号</option>
		</select>
	</div>
	<div style="margin-bottom:10px">
		<select id="barcodetype" class="easyui-combobox" name="barcodetype" labelWidth="110" label="${sessionScope.locale.get('admin.type')}：" style="width:280px;height:30px;" data-options="editable:false">
		    <option value="text">文本</option>
		</select>
	</div>
	<div style="margin-bottom:10px">
		<select id="outputtype" class="easyui-combobox" name="outputtype" labelWidth="110" label="${sessionScope.locale.get('admin.output')}：" style="width:280px;height:30px;" data-options="editable:false">
		    <option value="css">css(${sessionScope.locale.get('admin.recommendation')})</option>
		    <option value="svg">svg</option>
		    <option value="bmp">bmp</option>
		    <option value="canvas">canvas</option>
		</select>
	</div>
	<div style="margin-bottom:10px">
		<input id="barwidth" class="easyui-numberbox tb" label="${sessionScope.locale.get('admin.barwidth')}：" labelWidth="110" value="70" style="width:280px;height:30px;"  data-options="prompt:'${sessionScope.locale.get('admin.entercomponentheight')}...'"/> px
	</div>
	<div style="margin-bottom:10px">
		<input id="barheight" class="easyui-numberbox tb" label="${sessionScope.locale.get('admin.barheight')}：" labelWidth="110" value="70" style="width:280px;height:30px;"  data-options="prompt:'${sessionScope.locale.get('admin.entercomponentheight')}...'"/> px
	</div>
    
    <input type="hidden" name="type" value="4"/>
</div>
</form>

<script type="text/javascript">
var oNode = null,thePlugins = 'qrcode';
$(document).ready(function(){
    if( UE.plugins[thePlugins].editdom ){
        oNode = UE.plugins[thePlugins].editdom;

		/* var gValue = '';
		if(oNode.getAttribute('value'))
			gValue = oNode.getAttribute('value').replace(/&quot;/g,"\""); */
		var gTitle=oNode.getAttribute('title').replace(/&quot;/g,"\""),
		gWidth=oNode.getAttribute('width'),
		gHeight=oNode.getAttribute('height'),
		barcodetype=oNode.getAttribute('barcodetype'),
		outputtype=oNode.getAttribute('outputtype'),
		barwidth=oNode.getAttribute('barwidth'),
		barheight=oNode.getAttribute('barheight'),
		code=oNode.getAttribute('code');

		//gValue = gValue==null ? '' : gValue;
        gTitle = gTitle==null ? '' : gTitle;
        $('#orgname').val(gTitle);
        $('#width').val(gWidth);
        $('#height').val(gHeight);
        $('#barcodetype').val(barcodetype);
        $('#outputtype').val(outputtype);
        $('#barwidth').val(barwidth);
        $('#barheight').val(barheight);
        $('#code').val(code);

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

	if(!$('#orgname').val()){
        return false;
    }
	
	//console.log($('#orgvalue').val())
    //var gValue=$('#orgvalue').val().replace(/\"/g,"&quot;"),
    var gTitle=$('#orgname').val().replace(/\"/g,"&quot;"),
    //gWidth=oNode.getAttribute('width'),
	//gHeight=oNode.getAttribute('height'),
	barcodetype=$('#barcodetype').combobox('getValue'),
	outputtype=$('#outputtype').combobox('getValue'),
	barwidth=$('#barwidth').numberbox('getValue'),
	barheight=$('#barheight').numberbox('getValue'),
	code=$('#code').combobox('getValue');
	var flag=false;
    if( !oNode ) {
     	oNode=pluginHandle.createElement("img","qrcodeComponent");
     	flag=true;
    }
    oNode.setAttribute('title', gTitle);
    oNode.setAttribute("code",code);
    oNode.setAttribute("barcodetype",barcodetype);
    oNode.setAttribute("outputtype",outputtype);
    oNode.setAttribute("barwidth",barwidth);
    oNode.setAttribute("barheight",barheight);
  	//默认图片显示
    oNode.setAttribute("src","image/image/img/qrcode_blank.png");
    oNode.setAttribute("alt","img");
    if(flag){
    	editor.execCommand('insertHtml',oNode.outerHTML);
    }
    else{
    	delete UE.plugins[thePlugins].editdom;
    }
    $('#common_dialog').dialog('close');
};
</script>
</body>
</html>