<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>解剖结构示意图</title>
    <%@ include file="/common/meta.jsp"%>
</head>
<body>
<form id="anachart_form" method="post">
<div style="padding:10px;margin-left:auto;margin-right:auto;width:390px;height:240px;">
	<div style="margin-bottom:5px">
				<input id="orgname" class='easyui-textbox tb' label="${sessionScope.locale.get('report.name')}：" labelWidth="80" labelAlign="right" style="width:340px;height:30px;" 
					data-options="prompt:'${sessionScope.locale.get('admin.entername')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" name="name" value=""/>
	</div>
	<div style="margin-bottom:5px">
		<input id="orgwidth" class="easyui-numberbox tb" label="${sessionScope.locale.get('admin.width')}：" labelWidth='80' labelAlign="right" value="100" style="width:340px;height:30px;" 
			data-options="prompt:'${sessionScope.locale.get('admin.entercomponentwidth')}...',onChange:width_change"/> px
	</div>
	<div style="margin-bottom:5px">
		<input id="orgheight" class="easyui-numberbox tb" label="${sessionScope.locale.get('admin.height')}：" labelWidth='80' labelAlign="right" value="20" style="width:340px;height:30px;" 
			data-options="prompt:'${sessionScope.locale.get('admin.entercomponentheight')}...',editable:false"/> px
	</div>
	<div style="margin-bottom:5px">
    	<input class="easyui-checkbox" id="singleselect" name="singleselect" label="部位单选："" labelWidth="80" labelAlign="right">
    </div>
	<div style="margin-bottom:5px">
       	<textarea id="location_ac" class='easyui-textbox tb' label="定位框：" labelWidth='80' labelAlign="right" style="width:340px;height:30px;"  
       		data-options="prompt:'请选择定位组件...'" readonly='readonly' value="">
       	</textarea>
       	<a class="easyui-linkbutton"  style="width:30px;height:28px;margin-left:3px;" onclick="openChoiceLocationDialog('location_ac')">...</a>
    </div>
	<div style="margin-bottom:2px">
		<table id="dg_anachart" class="easyui-datagrid" style="margin-top:5px;width:388px;height:200px;"
            data-options="singleSelect: true,fitColumns:true">
	        <thead>
	            <tr>
	                <th data-options="field:'displayname',width:100">部位名称</th>
	                <th data-options="field:'value',width:100">值</th>
	                <th data-options="field:'code',width:80">${sessionScope.locale.get('admin.code')}</th>
	            </tr>
	        </thead>
	    </table>
	</div>
    
    <input type="hidden" name="type" value="9"/>
</div>
</form>

<script type="text/javascript">
var oNode = null,thePlugins = 'anatomychart_svg';
$(document).ready(function(){
    if( UE.plugins[thePlugins].editdom ){
        oNode = UE.plugins[thePlugins].editdom;
		var gTitle=oNode.getAttribute('title')?oNode.getAttribute('title').replace(/&quot;/g,"\""):"";
		//gValue = gValue==null ? '' : gValue;
        gTitle = gTitle==null ? '' : gTitle;
        $('#orgname').val(gTitle);
        $('#orgwidth').val(oNode.getAttribute('width'));
        $('#orgheight').val(oNode.getAttribute('height'));
        $('#location_ac').val(oNode.getAttribute('locationcom_id'));
        
        var singleselect=oNode.getAttribute('singleselect');
        if(singleselect!='false'){
        	$('#singleselect').attr("data-options","checked:true");
        }

		var svgdoc=oNode.getSVGDocument();
		var paths=svgdoc.querySelectorAll("path");
        var arr=new Array();
        for (var i=0;i<paths.length;i++){
        	var obj=new Object();
        	obj.value=paths[i].getAttribute('value');
        	obj.code=paths[i].getAttribute('code');
        	obj.displayname=paths[i].getAttribute('displayname');
        	arr.push(obj);
        }
        //console.log(arr);
        setTimeout(function () {
        	$('#dg_anachart').datagrid('loadData',arr);
        },300);
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
    var gTitle=$('#orgname').val().replace(/\"/g,"&quot;");
    oNode.setAttribute('title', gTitle);
    oNode.setAttribute('width',$('#orgwidth').numberbox('getValue'));
    oNode.setAttribute('height',$('#orgheight').numberbox('getValue'));
    oNode.setAttribute('locationcom_id',$('#location_ac').textbox('getValue'));
    oNode.setAttribute('singleselect',$('#singleselect').checkbox('options').checked);
    oNode.setAttribute('onload','initSvgCircle(this);');
    //editor.execCommand('insertHtml',oNode.outerHTML);
    delete UE.plugins[thePlugins].editdom;
    $('#common_dialog').dialog('close');
};

function width_change(newValue,oldValue){
	console.log(newValue)
	var svgdoc=oNode.getSVGDocument(),
	svg=svgdoc.querySelector("svg");
	var svgwidth=svg.getAttribute('width'),
	svgheight=svg.getAttribute('height');
	console.log(Math.round((newValue/svgwidth)*svgheight));
	$('#orgheight').numberbox('setValue',Math.round((newValue/svgwidth)*svgheight));
}

</script>
</body>
</html>