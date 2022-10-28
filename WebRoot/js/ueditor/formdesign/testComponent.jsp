<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>${sessionScope.locale.get('admin.barcode')}</title>
    <%@ include file="/common/meta.jsp"%>

</head>
<body>
<div style="padding:10px;margin-left:auto;margin-right:auto;width:440px;height:240px;">
	<form id="testComp_form" method="post">
	<div style="margin-bottom:5px">
		<span style="font-weight:bold;">组件：</span>
	</div>
	<div style="margin-bottom:10px;width:440px;overflow:auto;" id="component_container">
		
	</div>
	<div style="margin-bottom:5px">
		<span style="font-weight:bold;">表达式：</span>
	</div>
	<div style="margin-bottom:10px;">
		<textarea id="expression_ta" class="easyui-textbox tb" data-options="onChange:expressionChange" style="width:440px;height: 100px" multiline="true"
			 name="expression_text" value=""></textarea>
	</div>
	<div style="margin-bottom:5px">
		<span style="font-weight:bold;">测试结果：</span>
	</div>
	<div style="margin-bottom:10px;height:30px;" id="component_container">
		<span id="test_result"></span>
	</div>
    <div style="margin-bottom:5px">
		<span style="font-weight:bold;">语法：</span>
	</div>
	<div style="margin-bottom:5px">
		<span>在总结文本中添加@{value}，提取总结文本时将被组件的值替换。@{displayname}：只供选择框使用，将会用选择项的显示名称替换。
				 			</span>
				 			
		<p>
			支持JavaScript脚本，脚本使用方法@{{JavaScript}}，脚本中支持使用@{value}表示该组件的值，示例如下：
				 			@{{'@{value}'=='无'?'未见明显钙化':'@{value}'=='有'?'发现钙化':'@{value}'}}
		</p>
	</div>
	<input type="hidden" id="summary_attr" name="summary_attr"/>
	</form>
</div>
<script type="text/javascript" src="${ctx}/js/front/replacerule.js?v=${vs}"></script>
<script type="text/javascript">

$(document).ready(function(){
	
});

</script>
</body>
</html>