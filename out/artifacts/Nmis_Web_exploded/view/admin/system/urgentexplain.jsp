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
	<header style="color:#8aa4af;">您当前的位置：系统管理   > 危急值上报</header>
		<table id="taskdg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_task',
				loadMsg:'加载中...',emptyMsg:'没有查找到数据...',
				autoRowHeight:true,fitColumns:true">
			<thead>
					<tr>
						<th data-options="field:'a',width:40">病人姓名</th>
						<th data-options="field:'b',width:40">病人编号</th>
						<th data-options="field:'c',width:40">检查编号</th>						
						<th data-options="field:'d',width:100">情况说明</th>
						<th data-options="field:'e',width:20">是否上报</th>
						<th data-options="field:'f',width:60">上报时间</th>
						<th data-options="field:'g',width:20">是否处理</th>
						<th data-options="field:'operate',width:70,align:'center',formatter:operatecolumn_urgentexplain">操作</th>
					</tr>
			</thead>
        </table>
		<div id="toolbar_div_task" style="padding:2px 5px;text-align:right;">
		 	<a class="easyui-linkbutton easyui-tooltip" title="刷新" onClick="refreshTask1();">刷新</a>				
			<input class="easyui-searchbox" id="taskName_search" style="width:200px;"
				data-options="prompt:'请输入病人姓名'">
		</div>
	</div>
	
<script type="text/javascript">
$(function(){
	//var data = {total:"2",rows:[{a:'张三',b:'00001009',c:'CT00002010',d:'',e:'否',f:''},{a:'张三',b:'00001009',c:'CT00002010',d:'',e:'否',f:''}]};
	
});
</script>
</body>

</html>
