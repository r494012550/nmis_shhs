<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div style="padding:10px;">
		<div style="margin-bottom:10px">
			<input class="easyui-searchbox"  data-options="prompt:'请输入模板名称...',searcher:searchSRTemp" style="width:553px;height:30px;">
	    </div>
	    <div style="margin-bottom:3px">
	    	<table id="tmp_dg" class="easyui-datagrid" align="center" style="width:553px;height:358px;"
			    		data-options="singleSelect:true,onDblClickRow:openSRTemp,url:'${ctx}/srtemplate/findSRTemplate?withContent=false'">
			    		
			    <thead>
					<tr>
						<th data-options="field:'name',width:230" sortable="true">名称</th>
						<th data-options="field:'maprule',width:230" sortable="true">映射规则</th>
						<th data-options="field:'creatorname',width:80" sortable="true">创建人</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
</body>
</html>