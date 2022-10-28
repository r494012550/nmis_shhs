<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
<div style="width:100%;height:100%">
	<div class="easyui-layout" data-options="fit:true">
        <div data-options="region:'north',border:false" style="height:300px">
        
	        <div class="easyui-layout" data-options="fit:true,border:false">
	        	<div data-options="region:'north',border:false" style="height:60px;padding:0px;">
	        		<h3>实验组：${group.name}</h3>
	        	</div>
	        	<div data-options="region:'center'">
	        		<table id="formlist_selected_dg" class="easyui-datagrid" title="已选表单" style="width:100%;height:100%"
							data-options="singleSelect:true,fit:true,autoRowHeight:true,fitColumns:true,scrollbarSize:0,toolbar:'#toolbar_writeTemplate_dg',
							url:'${ctx}/research/findTestgroupFrom?groupid=${group.id}&withContent=false',loadMsg:'加载中...',emptyMsg:'没有查找到模板信息...',border:false">
						<thead>
							<tr>
								<th data-options="field:'name',width:100">表单模板名称</th>
								<th data-options="field:'operate',width:60,align:'center',formatter:addFromToGroup_formatter">${sessionScope.locale.get("admin.operation")}</th>
							</tr>
						</thead>
					</table>
	        	</div>
	        	<div data-options="region:'south',border:false" style="text-align:center;padding:5px;">
	        		<a class="easyui-linkbutton" id="addFormToGroup_lb" data-options="disabled:true" onclick="addFormToGroup();" style="width:80px">
	        			<i class="icon iconfont icon-arrow-o-u"></i></a>
	        	</div>
	        </div>
        </div>
        <div data-options="region:'center'">
            <table id="formlist_available_dg" class="easyui-datagrid" title="可选表单" style="width:100%;height:100%"
					data-options="singleSelect:true,fit:true,autoRowHeight:true,fitColumns:true,scrollbarSize:0,toolbar:'#toolbar_writeTemplate_dg',
					url:'${ctx}/research/findResearchForm?withContent=false',loadMsg:'加载中...',emptyMsg:'没有查找到表单信息...',border:false,
					onSelect:formlist_onselect">
				<thead>
					<tr>
						<th data-options="field:'name',width:100,">表单模板名称</th>
						<th data-options="field:'creatorname',width:50,align:'center'">创建人</th>
						<th data-options="field:'createtime',width:110,align:'center'">创建时间</th>
						<th data-options="field:'operate',width:50,align:'center',formatter:addFromToGroup_formatter_available">${sessionScope.locale.get("admin.operation")}</th>
					</tr>
				</thead>
			</table>
			<div id="toolbar_writeTemplate_dg" style="padding:2px 2px;text-align:right;">
				<input  id="formname_addtogroup" class="easyui-searchbox" data-options="prompt:'请输入表单名称',searcher:searchForm" style="width:200px;">
				<a class="easyui-linkbutton easyui-tooltip" title="查询表单"  onClick="searchForm();">查询</a>
			</div>
        </div>
        <div data-options="region:'south',border:false" style="text-align:right;padding:5px 0 0;">
            <a class="easyui-linkbutton" onclick="$('#common_right_window').dialog('close');" style="width:80px">关闭</a>
        </div>
    </div>
	<input id="addForm_group_id" name="groupid" value='${group.id}' type="hidden"/>
</div>
</body>
</html>