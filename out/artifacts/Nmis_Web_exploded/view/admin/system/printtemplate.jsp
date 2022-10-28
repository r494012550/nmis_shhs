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
		<header style="color:#8aa4af;">您当前的位置：系统管理  > 打印模板</header>
		<table id="printtemplatedg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_printtemplate',
				url:'${ctx}/system/findPrinttemplate',autoRowHeight:true,fitColumns:true,
				loadMsg:'加载中...',emptyMsg:'没有查找到打印模板...',
				onRowContextMenu: function(e,index,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',index);
		            $('#cmenu_printtemplate').menu('show', {
		                left:e.pageX,
		                top:e.pageY
		            });
                },
                view:groupview,
                groupField:'temptype',
                groupFormatter:function(value,rows){
                    return value + ' - ' + rows.length + ' 个';
		        }">
			<thead>
				<tr>
					
					<th data-options="field:'template_name',width:100">模板名</th>
					<th data-options="field:'description',width:100">描述</th>
					<th data-options="field:'temptype',width:80">类型</th>
					<th data-options="field:'modality_type',width:100">检查类型</th>
					<th data-options="field:'item_name',width:100">检查项目</th>
					<!-- <th data-options="field:'desc_maxlength',width:100">所见最大字符长度</th>
					<th data-options="field:'result_maxlength',width:100">所得最大字符长度</th>
					<th data-options="field:'fontsize',width:100">字体大小</th>
					<th data-options="field:'description',width:100">描述</th> -->
					<th data-options="field:'operate',width:60,align:'center',formatter:operatecolumn_printtemplate">操作</th>
				</tr>
			</thead>
        </table>
        
        <div id="cmenu_printtemplate" class="easyui-menu" style="width:120px;">
            <!-- <div onclick="goToEditReservation(null,'new');">新建预约单模板</div>
            <div onclick="goToEditChecklist(null,'new');">新建检查单模板</div> -->
	        <div onclick="goToEditPrinttemplate(null, 'new');">新建打印模板</div>
	        <div class="menu-sep"></div>
	        <!-- <div onclick="rightClickJudgeCheckOrPrint();">修改</div> -->
	        <div onclick="deletePrinttemplate(null);">删除</div>
	    </div>
		<div id="toolbar_div_printtemplate" style="padding:2px 2px;text-align:right;">
		    <!-- <a class="easyui-linkbutton easyui-tooltip" style="width:120px" title="新建预约单模板" onclick="goToEditReservation(null,'new')">新建预约单模板</a>
			<a class="easyui-linkbutton easyui-tooltip" style="width:120px" title="新建检查单模板" onclick="goToEditChecklist(null,'new')">新建检查单模板</a> -->
			<a class="easyui-linkbutton easyui-tooltip" style="width:110px;" title="新建报告模板" onClick="goToEditPrinttemplate(null,'new');">新建打印模板</a>
			
			<input class="easyui-searchbox" data-options="prompt:'请输入模板名称...',searcher:doSearchPrinttemplate" style="width:300px;">	
		</div>
	</div>
</body>
</html>