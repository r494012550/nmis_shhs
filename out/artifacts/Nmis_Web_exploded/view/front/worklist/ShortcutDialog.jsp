<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
	<div style="padding:5px 10px 5px 10px;margin-left:auto;margin-right:auto;width:420px;height:430px;">
		<form name="shortcuts_form" id="auform" method="POST">
		<div style="margin-bottom:5px;">
			<div style="float:left;" >
				<select id="keyCode_shortcuts" class="easyui-combobox" name="keyCode" style="width:200px;height:28px;" label='快捷键：' labelPosition='left'
					data-options="editable:false,panelHeight:'auto'">
	               	<option value='Ctrl+Shift+1'>Ctrl+Shift+1</option>
	               	<option value='Ctrl+Shift+2'>Ctrl+Shift+2</option>
	                <option value='Ctrl+Shift+3'>Ctrl+Shift+3</option>
            	</select>
            </div>
            <div style="float:right;">
               <input id="modality_shortcuts" class="easyui-combobox" name="modality" style="width:200px;height:28px;" label="${sessionScope.locale.get("wl.modality")}:" labelPosition="left" 
					data-options="valueField:'code',textField:'name_zh',editable:false,onChange:getTemplate_shortcuts,
					url:'${ctx}/syscode/getCode?type=0004',panelHeight:'120px'"/>
            </div>
         </div>
         <div style="margin-bottom:5px;">
         	<input id="templatename_shortcuts" class="easyui-textbox" style="width:420px;height:28px;" label="模板名：" labelPosition="left" editable="false" />
         </div>
        </form>
           <div style="margin-bottom:5px;height:360px;">
               <ul id="nodetree_shortcuts" class="easyui-tree" data-options="
	                onClick:function(node){
	                	$(this).tree('toggle',node.target);
	                }">
	         	</ul>
           </div>
	</div>
</body>
</html>