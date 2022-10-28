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
	    <header style="color:#8aa4af;">您当前的位置：字典管理  > 商品管理</header>
		<table id="comGoodsdg" class="easyui-datagrid" style="width:100%;"
			data-options="singleSelect:true,fit:true,toolbar:'#toolbar_div_comGoods',
				autoRowHeight:true,fitColumns:true,scrollbarSize:0,url:'${ctx}/comGoods/findComGoods?deleted=0'">
			<thead>
					<tr>
						<th data-options="field:'id',width:40">商品ID</th>
						<th data-options="field:'goods_name',width:100">商品名称</th>
						<th data-options="field:'general_name',width:100">商品通用名</th>
						<th data-options="field:'com_goods_variety_id',width:50">商品品种</th>
						<th data-options="field:'product_location',width:130">产地</th>
						<th data-options="field:'status',width:50,formatter:formatterComgoodsStatus">商品状态</th>
						<th data-options="field:'com_lot',width:40">批号</th>
						<th data-options="field:'efficiency_date',width:60">有效日期</th>
						<th data-options="field:'expire_date',width:60">失效日期</th>
						<th data-options="field:'calibration_dose',width:50">标定剂量</th>
						<th data-options="field:'capacity',width:50">容量</th>					
						<!-- <th data-options="field:'inv_owner',width:60">拥有者</th> -->
						<th data-options="field:'note',width:60">备注</th>
						<th data-options="field:'operate',width:60,align:'center',formatter:operatecolumn_comGoods">操作</th>
					</tr>
			</thead>
        </table>
		 <div id="toolbar_div_comGoods" style="padding:2px 5px;text-align:right;">
		 	<shiro:hasPermission name="edit_examItem">
				<a class="easyui-linkbutton easyui-tooltip" title="新建商品" onClick="comGoodsdlg();">新建</a>
			</shiro:hasPermission>
			<a class="easyui-linkbutton easyui-tooltip" title="查询商品" onClick="doSearchComGoods();">查询</a>
			<input id="comGoodsId" class="easyui-textbox" label="商品ID:" labelWidth="55px" style="width:200px;height:25px;">
			<input id="comGoodsName" class="easyui-textbox" label="商品名称:" labelWidth="65px" style="width:200px;height:25px;">
			<input id="comGoodsVariety" class="easyui-textbox" label="品种:" labelWidth="45px" style="width:200px;height:25px;">
			<input id="comLot" class="easyui-textbox" label="批号:" labelWidth="45px" style="width:200px;height:25px;">
			<input id="productLocation" class="easyui-textbox" label="产地:" labelWidth="45px" style="width:200px;height:25px;">
		</div>
	</div>
	
<script type="text/javascript">
	

</script>
</body>
</html>