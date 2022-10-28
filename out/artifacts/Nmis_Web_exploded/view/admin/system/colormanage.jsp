<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div class="easyui-panel" data-options="fit:true" style="padding:10px;">
	<header style="color:#8aa4af;">您当前的位置：系统管理  > 状态颜色</header>
	<table id="colordg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,singleSelect:true,fit:true,fitColumns:true,
				url:'${ctx}/color/findColor',autoRowHeight:true,
				loadMsg:'加载中...',emptyMsg:'没有查找到数据...',
				view:groupview,groupField:'typedesc',
		        groupFormatter:function(value,rows){
                    return value + ' - ' + rows.length + ' 个';
                }">
			<thead>
				<tr>
					<th data-options="field:'status',width:100">状态</th>
					<th data-options="field:'status_code',width:100">状态代码</th>
					<th data-options="field:'color',width:200" formatter="columeStyler_color">颜色</th>
				</tr>
			</thead>
        </table>
        
	</div>
<script type="text/javascript">



</script>
</body>
</html>