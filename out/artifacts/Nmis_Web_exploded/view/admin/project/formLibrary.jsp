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
    <header style="color:#8aa4af;">您当前的位置：项目管理  > 表单库</header>
    <div  class="easyui-layout" data-options="fit:true,border:false">
        <div data-options="region:'north'" style="height:45px;padding:5px;">
            <form id="importfm1" method="post" enctype="multipart/form-data">
            <shiro:hasPermission name="import_form_template">
                <input class="easyui-filebox" label="导入表单：" labelPosition="left" name="researchFormxml" id="importresFormfilebox"
                       data-options="prompt:'选择表单文件...',buttonText: '选择文件...',accept:'.xml',labelWidth:80,onChange:importResearchForm" style="width:350px">
            </shiro:hasPermission>       
            </form>
        </div>
        <div data-options="region:'center',border:false">
            <table id="resFandcompdg" class="easyui-datagrid" style="width:100%;"
                   data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div',
						url:'${ctx}/research/findSRTemplateAndComponent',autoRowHeight:true,fitColumns:true,singleSelect:false,
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
            
            <shiro:hasPermission name="export_form_template">
                <div onclick="exportResearchForm();">导出</div>
            </shiro:hasPermission>
            
         	<shiro:hasPermission name="delete_form_template">
                    <div class="menu-sep"></div>
                    <div onclick="delResearchFormAndComponent();">删除</div>
			</shiro:hasPermission>
			
            </div>
            <div id="toolbar_div" style="padding:2px 2px;text-align:right;">
				<shiro:hasPermission name="export_form_template">
                <a class="easyui-linkbutton easyui-tooltip" title="导出" onClick="exportResearchForm();"><i class="icon iconfont icon-export1"></i>&nbsp;导出</a>
                </shiro:hasPermission>
                <shiro:hasPermission name="delete_form_template">
                    <a class="easyui-linkbutton easyui-tooltip c5" title="删除" onClick="delResearchFormAndComponent();"><i class="icon iconfont icon-delete"></i>&nbsp;删除</a>
                </shiro:hasPermission>
                <input class="easyui-searchbox" id="formsearchbox" data-options="prompt:'请输入表单或组件名称...',searcher:doSearchResearchFormAndComponent" style="width:300px;">
            </div>
        </div>
    </div>
</div>
</body>
</html>