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
		<header style="color:#8aa4af;">您当前的位置：模板  > 组件库</header>
			<div  class="easyui-layout" data-options="fit:true,border:false">
			    <div data-options="region:'north'" style="height:45px;padding:5px;">
				    <form id="importfm" method="post" enctype="multipart/form-data">
						<input class="easyui-filebox" label="导入模板：" labelPosition="left" name="srtemplatexml" id="importsrtempfilebox"
							data-options="prompt:'选择模板文件...',buttonText: '选择文件...',accept:'.xml',labelWidth:80,onChange:importSRTemplate" style="width:350px">
					</form>
			    </div>
			    <div data-options="region:'center',border:false">
			    	<table id="srtempandcompdg" class="easyui-datagrid" style="width:100%;"
						data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div',
						url:'${ctx}/srtemplate/findSRTemplateAndComponent',autoRowHeight:true,fitColumns:true,singleSelect:false,
						loadMsg:'加载中...',emptyMsg:'没有查找到模板...',
						onRowContextMenu: function(e,index ,row){
				            e.preventDefault();
				            $(this).datagrid('selectRow',index);
				            $('#cmenu').menu('show', {
				                left:e.pageX,
				                top:e.pageY
				            });
				        },
				        view:groupview,
				        groupField:'group',
		                groupFormatter:function(value,rows){
		                    return value + ' - ' + rows.length + ' 个';
		                }">
						<thead>
							<tr>
								<th data-options="field:'ck',checkbox:true"></th>
								<th data-options="field:'name',width:100">组件名称</th>
								<th data-options="field:'creatorname',width:100">创建人</th>
								<th data-options="field:'createtime',width:100">创建时间</th>
							</tr>
						</thead>
					</table>
					
					<div id="cmenu" class="easyui-menu" style="width:120px;">
				        <div onclick="exportSRTemplate();">导出</div>
				        <shiro:hasPermission name="delete_srtemp">
				        <div class="menu-sep"></div>
				        <div onclick="delSRTempAndComponent();">删除</div>
				        </shiro:hasPermission>
				    </div>
					<div id="toolbar_div" style="padding:2px 2px;text-align:right;">
						
				        <a class="easyui-linkbutton easyui-tooltip" title="导出" onClick="exportSRTemplate();"><i class="icon iconfont icon-export1"></i>&nbsp;导出</a>
				        <shiro:hasPermission name="delete_srtemp">
						<a class="easyui-linkbutton easyui-tooltip c5" title="删除" onClick="delSRTempAndComponent();"><i class="icon iconfont icon-delete"></i>&nbsp;删除</a>
						</shiro:hasPermission>
						<input class="easyui-searchbox" id="srlibsearchbox" data-options="prompt:'请输入模板或组件名称...',searcher:doSearchSRTempAndComponent" style="width:300px;">	        
					</div>
			    </div>
			</div>
		</div>
</body>
</html>