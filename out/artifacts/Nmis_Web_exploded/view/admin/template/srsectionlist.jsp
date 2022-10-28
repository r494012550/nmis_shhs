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
			<input class="easyui-searchbox"  data-options="prompt:'请输入章节名称...',searcher:searchSRSection" style="width:553px;height:30px;">
	    </div>
	    <div style="margin-bottom:3px">
	    	<table id="section_dg" class="easyui-datagrid" align="center" style="width:553px;height:358px;"
			    		data-options="singleSelect:true,url:'${ctx}/srtemplate/findSRSections?sectionid=${sectionid}&withContent=false'">
			    		
			    <thead>
					<tr>
						<th data-options="field:'name',width:230" sortable="true">名称</th>
						<th data-options="field:'is_qc',width:80,formatter:qc_formatter" sortable="true">质控章节</th>
						<th data-options="field:'clone',width:80,formatter:clone_formatter" sortable="true">可复制</th>
						<th data-options="field:'catalog',width:80,formatter:clone_formatter" sortable="true">目录章节</th>
						<th data-options="field:'header',width:80,formatter:clone_formatter" sortable="true">标题章节</th>
						<th data-options="field:'creatorname',width:80" sortable="true">创建人</th>
						<th data-options="field:'createtime',width:200" sortable="true">创建时间</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
</body>
</html>