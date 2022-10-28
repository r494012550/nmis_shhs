<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
<div style="width: 100%; height: 100%;">

    <div class="easyui-layout"  data-options="fit:true,border:false">
        <div data-options="region:'north',border:false" style="padding:5px;height:27%;">
            <div style="margin-bottom:5px;">
                <input class="easyui-textbox" id="cancelreason" label="取消原因：" style="width:390px;height:70px" data-options="multiline:true">
            </div>
        </div>
        <div data-options="region:'south',border:false" style="padding:5px;height:75%;">
            <table id="examineCancelReason" class="easyui-datagrid" style="width:100%;"
				data-options="singleSelect:true,fit:true,autoRowHeight:true,
					onDblClickRow:function(index,row){
						examineCancelReasonDblClick(index,row);
					},fitColumns:true,scrollbarSize:0,url:'${ctx}/dic/findDicCommonFromCache?group=studycancelreason'">
			<thead>
					<tr>
						<th data-options="field:'id',hidden:true">ID</th>
						<th data-options="field:'name',width:100">请选择取消原因</th>
					</tr>
			</thead>
        </table>
        </div>
    </div>

</div>
</body>
</html>
    
