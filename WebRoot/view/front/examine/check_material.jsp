<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
    <div class="easyui-layout" style="width:100%;height:100%;">
        <div data-options="region:'west',split:true" style="width:240px;">
                <div id="tree_tool" style="padding:2px 2px;">
                   <a id="addchildnode_cm" class="easyui-linkbutton easyui-tooltip" data-options="plain:true" disabled="true" onclick="addchildnode();" 
                        title="${sessionScope.locale.get("report.addchildnode")}"><i class="icon iconfont icon-tianjia"></i></a>
                   <a id="addTemplateContent_cm" class="easyui-linkbutton easyui-tooltip" data-options="plain:true" disabled="true" onclick="addTemplate();" 
                                title="${sessionScope.locale.get("report.addtemplate")}"><i class="icon iconfont icon-plus2"></i></a>
                   <a id="modifynode_cm" class="easyui-linkbutton easyui-tooltip" data-options="plain:true" disabled="true" onclick="modifynode();" 
                        title="${sessionScope.locale.get("report.modifynode")}"><i class="icon iconfont icon-edit"></i></a>
                   <a id="deletenode_cm" class="easyui-linkbutton easyui-tooltip" data-options="plain:true" disabled="true" onclick="deletenode();" 
                        title="${sessionScope.locale.get("delete")}"><i class="icon iconfont icon-delete"></i></a>
                   <a id="previewTemplateNew_cm" class="easyui-linkbutton easyui-tooltip" data-options="plain:true" disabled="true" onclick="previewTemplate();" 
                            title="${sessionScope.locale.get("report.previewtemplate")}"><i class="icon iconfont icon-info"></i></a>
                </div>
                <div class="easyui-datalist" style="width:100%;height:30px;" border="0" data-options="toolbar:'#tree_tool'"></div>
                <div>
                     <input class="easyui-searchbox" style="width:99%" data-options="prompt:'请输入你需要查询的模板名称...',
                     searcher:function(value,name){
                         searchTemplate(value,'${modalityType}');
                     }" >
                </div>
                <ul id="nodetree_cm" class="easyui-tree" data-options="url:'${ctx}/examine/getCheckMaterialTree?modalityType=${modalityType}',method:'get',
                    onSelect:function(node){
                        selectTemplateNode(node);
                    },
                    onContextMenu: function(e,node){
                        e.preventDefault();
                        $(this).tree('select',node.target);
                        enableTemplateMenu(node);
                        $('#mm').menu('show',{
                            left: e.pageX,
                            top: e.pageY
                        });
                    },
                    onClick:function(node){
                        $(this).tree('toggle',node.target);
                    },
                    onAfterEdit:function(node){
                        updateNodeCm(node);
                    },
                    onDblClick:function(node){
                       if (node.type == 1) {
                        previewTemplate();
                       }
                    }">
                </ul>
                
                <div id="mm" class="easyui-menu" style="width:120px;">
                    <div data-options="name:'addchildnode_cm'" onclick="addchildnode();">${sessionScope.locale.get("report.addchildnode")}</div>
                    <div data-options="name:'addTemplateContent_cm'" onclick="addTemplate();">${sessionScope.locale.get("report.addtemplate")}</div>
                    <div class="menu-sep"></div>
                    <div data-options="name:'modifynode_cm'" onclick="modifynode();">${sessionScope.locale.get("report.modifynode")}</div>
                    <div data-options="name:'deletenode_cm'" onclick="deletenode();">${sessionScope.locale.get("delete")}</div>
                    <div class="menu-sep"></div>
                    <div data-options="name:'previewTemplateNew_cm'" onclick="previewTemplate();">${sessionScope.locale.get("report.previewtemplate")}</div>
                </div>
        </div>
        
        <div data-options="region:'center',iconCls:'icon-ok'">
            <div id="node_cm" class="easyui-panel" title="模板管理"
                style="width:100%;height:100%;padding:10px;background:#fafafa;"
                data-options="noheader:false,border:false">
            </div>
        </div>
    </div>
</body>
</html>