<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div class="easyui-panel" data-options="fit:true" style="padding:10px;">
		<header style="color:#8aa4af;">您当前的位置：统计  > 统计管理</header>
		<table id="statisticsdg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_statistics',
				url:'${ctx}/statistics/findStatistics',autoRowHeight:true,fitColumns:true,
				loadMsg:'加载中...',emptyMsg:'没有查找到报表...',
				onRowContextMenu: function(e,index ,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',index);
		            $('#cmenu_statistics').menu('show', {
		                left:e.pageX,
		                top:e.pageY
		            });
		        },
		        view:groupview,
		        groupField:'classifyname',
                groupFormatter:function(value,rows){
                    return value + ' - ' + rows.length + ' 个';
                }">
			<thead>
				<tr>
					<th data-options="field:'name',width:100">名称</th>
					<th data-options="field:'jrxml_filename',formatter:fileformatter,width:100">模板设计文件</th>
					<th data-options="field:'jasper_filename',formatter:fileformatter,width:100">模板运行文件</th>
					<th data-options="field:'sql',width:100">SQL语句</th>
					<th data-options="field:'createtime',width:100">创建时间</th>
					<th data-options="field:'operate',width:60,align:'center',formatter:operatecolumn_Statistical">${sessionScope.locale.get("admin.operation")}</th>
				</tr>
			</thead>
		</table>
   		
		
		<div id="cmenu_statistics" class="easyui-menu" style="width:120px;">
			<shiro:hasPermission name="edit_statistics">
	        <div onclick="openEditStatisticsDlg(null);">新建</div>
	        <div onclick="openModifyStatisticsDlg();">修改</div>
	        <div onclick="deleteStatistics();">删除</div>
	        </shiro:hasPermission>
	    </div>
		<div id="toolbar_div_statistics" style="padding:2px 2px;text-align:right;">
			<shiro:hasPermission name="edit_statistics">
			<a class="easyui-linkbutton easyui-tooltip" title="新建报表"  onClick="openEditStatisticsDlg(null);">新建</a>
			</shiro:hasPermission>
			<shiro:hasPermission name="manageStatisticsClassify">
			<a class="easyui-linkbutton easyui-tooltip" title="分类管理"  onClick="openManageClassifyDlg();">分类管理</a>
			</shiro:hasPermission>
			<%-- <shiro:hasPermission name="xsltdel">
			<a class="easyui-linkbutton easyui-tooltip" title="删除报表"  onClick="deleteStatistics();">删除</a>
			<a class="easyui-linkbutton easyui-tooltip" title="修改报表"  onClick="openModifyStatisticsDlg();">修改</a>
			</shiro:hasPermission> --%>
			
			<input class="easyui-searchbox" data-options="prompt:'请输入报告名称',searcher:findStatistics" style="width:300px;">	
		</div>
	</div>
</body>
</html>