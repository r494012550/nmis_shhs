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
	<header style="color:#8aa4af;">您当前的位置：系统管理   > 定时任务</header>
		<table id="taskdg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_task',
				url:'${ctx}/system/getAllTask',
				loadMsg:'加载中...',emptyMsg:'没有查找到数据...',
				autoRowHeight:true,fitColumns:true">
			<thead>
					<tr>
						<th data-options="field:'task',width:80">任务名称</th>
						<th data-options="field:'cron',width:60">任务执行计划</th>
						<th data-options="field:'isstarted',width:40,formatter:startcolumn_task">启动</th>						
						<th data-options="field:'isdaemon',width:40,formatter:daemoncolumn_task">后台运行</th>
						<th data-options="field:'lastruntime',width:60">最近执行时间</th>
						<th data-options="field:'errormessage',width:80">错误信息</th>
						<th data-options="field:'operate',width:70,align:'center',formatter:operatecolumn_task">操作</th>
					</tr>
			</thead>
        </table>
		<div id="toolbar_div_task" style="padding:2px 5px;text-align:right;">
		 	<a class="easyui-linkbutton easyui-tooltip" title="刷新" onClick="refreshTask();">刷新</a>				
			<!-- <input class="easyui-searchbox" id="taskName_search" style="width:200px;"
				data-options="prompt:'请输入定时任务名称' ,searcher:doSearchClient"> -->
		</div>
	</div>
</body>
</html>
