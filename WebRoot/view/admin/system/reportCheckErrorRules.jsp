<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
    <div class="easyui-panel" data-options="fit:true" style="padding:10px;">
    <header style="color:#8aa4af;">您当前的位置：系统管理   > 报告纠错规则</header>
        <table id="checkerrordg" class="easyui-datagrid" style="width:100%;"
            data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_Checkerror',
                url:'${ctx}/system/getReportCheckErrorRules',
                loadMsg:'加载中...',emptyMsg:'没有查找到数据...',
                autoRowHeight:true,fitColumns:true,
                onRowContextMenu: function(e,index ,row){
                    e.preventDefault();
                    $(this).datagrid('selectRow',index);
                    $('#menu_Checkerror').menu('show', {
                        left:e.pageX,
                        top:e.pageY
                    });
                }">
            <thead>
                    <tr>
                        <th data-options="field:'keyword',width:30">关键词</th>
                        <th data-options="field:'rules',width:100">规则</th>
                        <th data-options="field:'operate',width:10,align:'center',formatter:operatecolumn_CheckError">操作</th>
                    </tr>
            </thead>
        </table>
        <div id="menu_CheckError" class="easyui-menu"  style="padding:2px 2px;"> 
             <shiro:hasPermission name="edit_checkerrorrule">        
                <div onclick="new_CheckErrorRule();">新建</div>
                <div onclick="modifyCorrectRule();">修改</div>    
                <div onclick="deleteCheckErrorRule();">删除</div>  
             </shiro:hasPermission>
         </div>
         <div id="toolbar_div_Checkerror" style="padding:2px 5px;text-align:right;">                    
<%--             <shiro:hasPermission name="edit_checkerrorrule"> --%>
               <a class="easyui-linkbutton easyui-tooltip" title="新建规则" onClick="new_CheckErrorRule();">新建</a>
<%--             </shiro:hasPermission> --%>
            <input class="easyui-searchbox" id="keywordName_search" style="width:200px;"
                data-options="prompt:'请输入关键词名称' ,searcher:doSearchCheckRule">
        </div>
    </div>
</body>
</html>