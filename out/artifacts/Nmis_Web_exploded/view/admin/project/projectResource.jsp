<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
		<div class="easyui-panel" data-options="fit:true" style="padding:10px;">
			<header style="color:#8aa4af;">您当前的位置：项目管理  > 资源管理</header>
			<table id="resdg" class="easyui-treegrid" style="width:100%;"
				data-options="showFooter: true,iconCls: 'icon-ok',singleSelect:true,fit:true,scrollbarSize:0,
				url:'${ctx}/research/searchResource',autoRowHeight:true,fitColumns:true,idField:'rid',treeField:'display_${language}',animate:true,
				">
				<thead>
					<tr>
						<th data-options="field:'display_${language}',width:100">${sessionScope.locale.get('admin.resourcename')}</th>
						<th data-options="field:'resource',width:300">${sessionScope.locale.get('admin.resourcevalue')}</th>
					</tr>
				</thead>
			</table>
			
		    
		   </div>
</body>

</html>