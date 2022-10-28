<%@page language="java"  contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title>库存管理</title>
	<link rel="shortcut icon" href="${ctx}/themes/head.ico?v=${vs}">
	<%@ include file="/common/basecss.jsp"%>
	<link rel="stylesheet" type="text/css" href="${ctx}/js/cropper/main.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/js/cropper/cropper.min.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/js/umeditor/themes/default/css/umeditor.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/js/imgcontainer/image_container.css?v=${vs}">
	
	<%@ include file="/common/basejs.jsp"%>
	<script type="text/javascript" src="${ctx}/js/front/cache.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/jquery-barcode.min.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/umeditor/umeditor.config.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/umeditor/umeditor.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jquery.texteditor.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/common.js?v=${vs}"></script>
	
<c:set var="input_width" value="300"/>
<c:set var="label_width" value="100"/>
</head>
<body class="easyui-layout" style="width:100%;height:100%;border:0px;">
	<div data-options="region:'north',border:false" style="height:35px;" class="title_background">
		<table style="width:100%;height:100%;" cellspacing="0">
			<tr>
				<jsp:include page="/view/navigation.jsp" />
				<td>
					<input type="hidden" id="userid_hidden" value="${user_id}"/>
					<input type="hidden" id="username_hidden" value="${username}"/>
				</td>
				<td align="right" style="font-weight:bold; padding:0px 5px 0px 0px;">
					<a class="easyui-menubutton" data-options="menu:'#head_mm_sch',iconCls:'icon iconfont icon-user'"style="color:#b8c7ce;"> ${name}</a>
				</td>
			</tr>
		</table>
		<div id="head_mm_sch" style="width:150px;">
	        <div onclick="openMyConfigDialog();">我的配置</div>
	        <div class="menu-sep"></div>
	        <div onclick="logout();">注销</div>
	    </div>
	</div>
		    <div data-options="region:'west',title:'订单信息',split:false" style="width:15%;">
					<ul id="tt" class="easyui-tree" data-options="url:'${ctx}/stock/findOrder',method:'get',animate:true,dnd:true,
			           onClick:function(node){
			           		findPurchaseOrderById(node.id);
			           }">
			       </ul>
		    </div>
		    <div data-options="region:'east',title:'订单明细',split:false" style="width:85%;">
		    	<table  id="orderLines" class="easyui-datagrid" style="width:100%;height:100%"
				        data-options="fitColumns:true,singleSelect:true,toolbar:toolbar_div_sscPurchase,fit:true,autoRowHeight:true,scrollbarSize:0,onClickCell:onClickCell">
				    <thead>
				        <tr>
				            <th data-options="field:'goods_id',width:100">商品ID</th>
				            <th data-options="field:'goods_name',width:100">商品名称</th>
				            <th data-options="field:'com_lot',width:200">批号</th>
				            <th data-options="field:'efficiency_date',width:200">有效日期</th>
				            <th data-options="field:'expire_date',width:200">失效日期</th>
				            <th data-options="field:'calibration_dose',width:200">标定剂量</th>
				            <th data-options="field:'capacity',width:200">容量</th>
				            <th data-options="field:'order_qty',width:200"> 订单数量</th>
				            <th data-options="field:'qty',width:200,editor:'numberbox',validType:['number']"><i class="icon iconfont icon-fafang"> 实际数量</th>
				        </tr>
				    </thead>
				</table>
		    </div>
		    <div id="toolbar_div_sscPurchase" style="text-align:right;padding:0px 0px 0px 50px;">
				<a class="easyui-linkbutton easyui-tooltip" title="审核" onClick="approval();">审核</a>
				<a class="easyui-linkbutton easyui-tooltip" title="驳回" onClick="reject();">驳回</a>
			</div>
 <script type="text/javascript">
 var editIndex = undefined;//定义编辑列的索引
 function endEditing() {//判断是否处于编辑状态
     if (editIndex == undefined) { return true }
     if ($('#orderLines').datagrid('validateRow', editIndex)) {
         $('#orderLines').datagrid('endEdit', editIndex);
         editIndex = undefined;
         return true;
     } else {
         return false;
     }
 }
 //单击单元格的时候触发
 function onClickCell(index, field) {
	 if(field=="qty"){
	     if (endEditing()) {
	    	 var rows=$("#orderLines").datagrid("getRows");
	    	 var row=rows[index];
	         $('#orderLines').datagrid('selectRow', index).datagrid('editCell', { index: index, field: field});
	         var editors = $('#orderLines').datagrid('getEditors', index); 
	         $(editors[0].target).numberbox('setValue', row.order_qty); 
	         editIndex = index;
	     }
	 }else{
		 endEditing();
	 }
 }
 
 function findPurchaseOrderById(id){
		var node=$('#tt').tree('getSelected');
		if("false"==node.isPrent){
			if("1"==node.type){
				$.getJSON(window.localStorage.ctx+"/purchase/findSscPurchaseLines",{id:id},function(data) {
					$("#orderLines").datagrid("loadData", validationData(data)); 
				});
			}
			if("2"==node.type){
				$.getJSON(window.localStorage.ctx+"/sale/findSscSaleOrderLines",{id:id},function(data) {
					$("#orderLines").datagrid("loadData", validationData(data)); 
				});
			}
		 }
}
 function approval(){
	 endEditing();
	 var node=$('#tt').tree('getSelected');
	 if(node==null){
		 return $.messager.alert('提示','请在左侧选择要审核的订单！！！');
	 }
	 if("false"!=node.isPrent){
		 return $.messager.alert('提示','请选择子节点提交！！！');
	 };
	 var rows =$("#orderLines").datagrid("getRows");
	 for (var i = 0; i < rows.length; i++) {
		if(isEmpty(rows[i].qty)){
			return $.messager.alert("提醒","商品ID:"+rows[i].goods_id+"的实际数量不能为空!!");
		}
		if(rows[i].qty>rows[i].order_qty){
			return $.messager.alert("提醒","商品ID:"+rows[i].goods_id+"的实际数量不能大于订单数量!!");
		}
	}
	 getJSON(window.localStorage.ctx+"/stock/approvalOrder",{id:node.id,data:JSON.stringify(rows),type:node.type},
				function(data){
					var json=validationData(data);
				 	if(json.code==0){
				 		_message("审核成功","提示");
				 		$('#tt').tree('reload');
				 		$("#orderLines").datagrid("loadData",{"total":0,"rows":[]});
				 	}else{
			    		_message("审核失败，请重试，如果问题依然存在，请联系系统管理员！","错误");
			    	}
				});
 }
</script>   
</body>
</html>