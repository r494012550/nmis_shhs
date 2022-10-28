<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<c:set var="input_width" value="240"/>
<c:set var="label_width" value="100"/>
<title>Insert title here</title>
</head>
<body>
<form id="followup_form" method="post">
<div class="easyui-panel" data-options="border:false,footer:'#followup_info_footer'" style="width:100%;height:390px;padding:5px;">
	<input type="hidden" id="orderid" name="orderid"/>
	<input type="hidden" id="followupid" name="id"/>
	<div class="followup_content_div">
		<input class="easyui-datetimebox" label="随访日期：" required="true" labelWidth="${label_width}"  labelAlign="right" style="width:${input_width+30}px;height:25px;" 
				name="followup_datetime" data-options="editable:false" value="${followup_date}" />
	</div>
	<%-- <div class="followup_content_div">
		<input id="followup_doctorname" class="easyui-textbox" label="随访人：" readonly="readonly"  required="true" labelWidth="${label_width}" labelAlign="right" style="width:${input_width}px;height:25px;"
				name="followup_doctorname" value="${name}">
	</div> --%>
	<div class="followup_content_div">
		<input class="easyui-combobox" label="随访方式：" required="true" labelWidth="${label_width}" labelAlign="right" style="width:${input_width}px;height:25px;"
				name="followup_way" data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0040',editable:false">
	</div>
	<div class="followup_content_div">
		<input class="easyui-combobox" label="随访符合：" required="true" labelWidth="${label_width}" labelAlign="right" style="width:${input_width}px;height:25px;"
				name="followup_consistent" data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0041',editable:false">
	</div>
	<div class="followup_content_div">
		<input id="followup_reason" class="easyui-textbox" label="随访原因：" required="true" labelWidth="${label_width}" labelAlign="right" style="width:${input_width*2+33}px;height:25px;"
			 	name="followup_reason">
	</div>
	<div style="width:100%;float:left;">
		<input class="easyui-textbox" label="治疗情况：" required="true" labelWidth="${label_width*2}" labelPosition="top" style="width:100%;height:100px;"
   				name="treatment" data-options="multiline:true">
	</div>
	<div style="width:100%;float:left;">
		<input class="easyui-textbox" label="随访结果：" required="true" labelWidth="${label_width*2}" labelPosition="top" style="width:100%;height:180px;"
   				id="followup_result" name="followup_result" data-options="multiline:true">
	</div>
</div>
<div id="followup_info_footer" style="padding:5px;text-align:right;"> <!-- data-options="disabled:true" -->
	<a class="easyui-linkbutton editfollowup_btn" data-options="disabled:true" style="width:80px;" onclick="saveFollowup()">保存</a>
	<a class="easyui-linkbutton c2 editfollowup_btn" id="editfollowup_btn" data-options="disabled:true" style="width:80px;" onclick="clearFollowup()">清空</a>
	<a class="easyui-linkbutton c6 editfollowup_btn" data-options="disabled:true" style="width:80px;" onclick="cancelAddFollowup()">取消</a>
</div>
</form>
</body>
</html>