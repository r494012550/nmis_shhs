<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
   	<form name="comGoodsform" id="comGoodsform" method="POST">
   	<div style="padding:10px;margin-left:auto;margin-right:auto;">
		 <div style="margin-bottom:5px">
			<input class="easyui-textbox"  id="goods_name" label="商品名称：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"
	       		data-options="prompt:'请输入商品名称...',required:true,missingMessage:'必填'"
	       		name="goods_name" value="${item.goods_name}">
	       	<input class="easyui-textbox"  id="general_name" label="商品通用名：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"
	       		data-options="prompt:'请输入商品通用名...'"
	       		name="general_name" value="${item.general_name}">
		</div>
		<div style="margin-bottom:5px">
			<input class="easyui-combobox" id="com_goods_variety_id" label="商品品种：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"
	        	data-options="valueField:'id',textField:'com_goods_variety_name',required:true,missingMessage:'必填',
	        	url:'${ctx}/goodsVariety/findComGoodsVariety?deleted=0',editable:false,prompt:'请选择商品品种...',
	        	onLoadSuccess:function(){$(this).combobox('select', '${item.com_goods_variety_id}')}" 
	        	name="com_goods_variety_id" >
	        <input class="easyui-textbox"  id="product_location" label="产地：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"
	       		data-options="prompt:'请输入商品产地...'"
	       		name="product_location" value="${item.product_location}">
		</div>
		<div style="margin-bottom:5px">
			<input class="easyui-combobox" id="status" label="商品状态：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"
	        	data-options="valueField:'code',textField:'name_zh',required:true,missingMessage:'必填',
	        	url:'${ctx}/syscode/getCode?type=0030',editable:false,prompt:'请选择商品状态...',
	        	onLoadSuccess:function(){$(this).combobox('select', '${item.status}')}" 
	        	name="status" >
	        <input class="easyui-textbox"  id="com_lot" label="批号：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"
	       		data-options="prompt:'请输入商品批号...',required:true,missingMessage:'必填'" 
	       		name="com_lot" value="${item.com_lot}">
		</div>
		<div style="margin-bottom:5px">
			<input class ="easyui-datebox"  id="efficiency_date" label="有效日期：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"name="efficiency_date" data-options="prompt:'请输入有效日期...',required:true,editable:false,missingMessage:'必填'"  value="${item.efficiency_date}">
	        <input class ="easyui-datebox"  id="expire_date" label="失效日期：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"name="expire_date"  data-options="prompt:'请输入失效日期...',required:true,editable:false,missingMessage:'必填'"  value="${item.expire_date}">
		</div>
		<div style="margin-bottom:5px">
			<input class ="easyui-textbox"  id="calibration_dose" label="标定剂量：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"name="calibration_dose"  value="${item.calibration_dose}">
	        <input class ="easyui-textbox"  id="capacity" label="容量：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"name="capacity"  value="${item.capacity}">
		</div>
		<div style="margin-bottom:5px">
			<input class="easyui-textbox" id="note" label="备注：" labelAlign="right" labelWidth="100" style="width:603px;height:80px;"
	  			data-options="prompt:'请输入备注...',multiline:true "
	  			name="note" value="${item.note}">
		</div>
   	</div>
   	
	<div>
		<input id="id" class ="easyui-textbox" name="id" type="hidden" value="${item.id}" />
	</div>
</form>
<script type="text/javascript">
 
</script>
</body>
</html>