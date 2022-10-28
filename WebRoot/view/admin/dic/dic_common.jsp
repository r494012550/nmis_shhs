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
	    <header style="color:#8aa4af;">您当前的位置：字典管理  > 通用字典 </header>
		<table id="dic_common_dg" class="easyui-datagrid" style="width:100%;"
			data-options="singleSelect:true,fit:true,toolbar:'#toolbar_div',
				autoRowHeight:true,fitColumns:true,onClickCell: onClickCell_DicCommon,scrollbarSize:0">
			<thead>
					<tr>
						<th data-options="field:'id',hidden:true">ID</th>
						<th data-options="field:'code',width:50,editor:'textbox',required:true">代码</th>
						<th data-options="field:'type',width:40,
							formatter:function(value,row,index){
								return row.type_name;
							},
	                        editor:{type:'combobox',options:{valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0004'}}">设备类型</th>
						<th data-options="field:'name',width:100,editor:'textbox',required:true">名称</th>
						<th data-options="field:'name_en',width:100,editor:'textbox'">英文名称</th>
						<th data-options="field:'sort',width:40,editor:'numberbox',validType:['number']">显示顺序</th>
						<th data-options="field:'operate',width:40,align:'center',formatter:operation_diccommon">操作</th>
					</tr>
			</thead>
        </table>
		 <div id="toolbar_div" style="padding:2px 5px;text-align:right;">
		 	<input class="easyui-combobox" id="dicgroup_cb" style="width:250px;" label="分组：" labelAlign="right" data-options="valueField:'code',textField:'name_zh',editable:false,panelHeight:'auto',
                    url:'${ctx}/syscode/getCode?type=0042',onChange:function(value){getDicCommon()}">
		 	<shiro:hasPermission name="edit_examItem">
				<a class="easyui-linkbutton easyui-tooltip" title="新建" onClick="addDicCommon();">新建</a>
				<a class="easyui-linkbutton easyui-tooltip" title="保存" onClick="saveDicCommon();">保存</a>
			</shiro:hasPermission>
			<input class="easyui-searchbox" id="itemName_search"
				data-options="prompt:'请输入编码或名称' ,searcher:getDicCommon" style="width:200px;">
		</div>
	</div>
	
<script type="text/javascript">
function operation_diccommon(value, row, index){
	return '<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteDicCommon('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>';//"<a href='#' class='easyui-linkbutton' name='operate' onclick='openModifyUserDialog()' ><i class='icon icon-edit1'></i>修改</a>&nbsp;&nbsp;"+
}
var tableName="DicInspectMethod"
var editIndex = undefined;//定义编辑列的索引
function endEditing_Diccom() {//判断是否处于编辑状态
    if (editIndex == undefined) { return true }
    if ($('#dic_common_dg').datagrid('validateRow', editIndex)) {
    	
    	 var ed = $('#dic_common_dg').datagrid('getEditor', { index: editIndex, field: 'type' });  //editIndex编辑时记录下的行号
    	     if (ed != null) {
                    var name_zh = $(ed.target).combobox('getText');
                    $('#dic_common_dg').datagrid('getRows')[editIndex]['type_name'] = name_zh;
    	      }
        $('#dic_common_dg').datagrid('endEdit', editIndex);
        editIndex = undefined;
        return true;
    } else {
        return false;
    }
}
//单击单元格的时候触发
function onClickCell_DicCommon(index, field) {
    if (endEditing_Diccom()) {
        $('#dic_common_dg').datagrid('selectRow', index).datagrid('editCell', { index: index, field: field });
        editIndex = index;
    }
}

</script>
</body>
</html>