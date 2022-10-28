<%@page language="java"  contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title>采购</title>
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
	
<c:set var="input_width" value="300"/>
<c:set var="label_width" value="100"/>
</head>
<body style="" class="easyui-layout">
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
    <input class="easyui-searchbox" id="itemName_search" data-options="prompt:'请输入单号!!' ,searcher:doSearchComGoodsVariety" style="width:100%;">
    <ul id="tt" class="easyui-tree" data-options="url:'${ctx}/purchase/findOrdertree',method:'get',animate:true,dnd:true,
           onClick:function(node){
           		findPurchaseOrderById(node.id);
           },
            onLoadSuccess:selectLastTree"></ul>
    </div>
    <div data-options="region:'east',title:'采购信息',split:false" style="width:85%;">
    	<div id="cc1" class="easyui-layout" style="width:100%;height:100%;border:0px;">
		    <div data-options="region:'north',split:false" style="height:25%;">
		    		<div id="toolbar_div_sscPurchase" style="text-align:right;padding:0px 0px 0px 50px;">
						<a class="easyui-linkbutton easyui-tooltip" title="新建订单" onClick="add();">新建订单</a>
						<a class="easyui-linkbutton easyui-tooltip" title="新建订单" onClick="addRow();">新增行</a>
						<a class="easyui-linkbutton easyui-tooltip" title="保存订单" onClick="savePurchase();">保存</a>
						<a class="easyui-linkbutton easyui-tooltip" title="提交订单" onClick="submitPurchase();">提交</a>
						<a class="easyui-linkbutton easyui-tooltip" title="删除订单" onClick="deletePurchase();">删除</a>
					</div>
				<form id="purchaseForm" method="post">
		    		<div style="padding:5px 10px 0px 0px;">
						<input id="purchase_no" name="purchase_no" class="easyui-textbox" label="订单号:" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
    						   readonly="readonly">
    					<input id="id"  name="id" class="easyui-textbox" label="订单ID:" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
    						   readonly="readonly">
    					<input id="order_start_date" name="order_start_date" class="easyui-datebox" label="开始日期:" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;background-color:red;" 
    						   readonly="readonly" data-options="value:'Today'">
    					<input id="order_end_date" name="order_end_date" class="easyui-datebox" label="结束日期:" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
    						    required="required" data-options="onHidePanel:endDateSelect,editable:false">		   
    				</div>
    				<div style="padding:10px 10px 0px 0px;">
						<input id="creator" name="creator" class="easyui-textbox" label="制单人:" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
    						   readonly="readonly" value="${name}">
    					<input id="vender"  name="vender" class="easyui-textbox" label="供应商:" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
    						   required="required">
    					<input id="contacts" name="contacts" class="easyui-textbox" label="联系人:" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
    						    >
    					<input id="contacts_number" name="contacts_number" class="easyui-numberbox" label="联系电话:"  labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
    						    data-options="validType:['number']">		   
    				</div>
    				<div style="padding:10px 10px 0px 0px;">
						<input id="vender_address" name="vender_address" class="easyui-textbox" label="供应商地址:" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
    						   required="required">
    					<input id="receivi_goods_address" name="receivi_goods_address" class="easyui-textbox" label="收货地址:" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
    						   required="required">
    					<input id="note" name="note" class="easyui-textbox" label="备注:" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:605px;">	   
    				</div>
    				<input id="status" name="status"  class="easyui-textbox"  type="hidden">	 
		    	</form>
		    </div>
		    <div data-options="region:'south',split:false" style="height:80%;">
		    	<table  id="sscPurchaseLines" class="easyui-datagrid" style="width:100%;height:100%"
				        data-options="fitColumns:true,singleSelect:true,fit:true,autoRowHeight:true,scrollbarSize:0,onDblClickCell: onDblClickCell,onClickCell:onClickCell">
				    <thead>
				        <tr>
				            <th data-options="field:'goods_id',width:100">商品ID</th>
				            <th data-options="field:'goods_name',width:100">商品名称</th>
				            <th data-options="field:'com_lot',width:200">批号</th>
				            <th data-options="field:'efficiency_date',width:200">有效日期</th>
				            <th data-options="field:'expire_date',width:200">失效日期</th>
				            <th data-options="field:'calibration_dose',width:200">标定剂量</th>
				            <th data-options="field:'capacity',width:200">容量</th>
				            <th data-options="field:'pur_qty',width:200,editor:'numberbox',validType:['number']"><i class="icon iconfont icon-fafang"> 订单数量</th>
				        </tr>
				    </thead>
				</table>
		    </div>
		    
		    <div id="ReceiveFeedBackDialog" class="easyui-dialog" closed="true" buttons="#dlg-buttons" title="商品信息" style="width:700px;height:550px;">
    				<table id="comGoodsdg" class="easyui-datagrid" style="width:100%;"
						data-options="singleSelect:true,fit:true,toolbar:'#toolbar_div_comGoods',onDblClickCell: onComGoodsdg,autoRowHeight:true,fitColumns:true,scrollbarSize:0,url:'${ctx}/comGoods/findComGoods?deleted=0'">
						<thead>
								<tr>
									<th data-options="field:'id',width:40">商品ID</th>
									<th data-options="field:'goods_name',width:150">商品名称</th>
									<th data-options="field:'general_name',width:180">商品通用名</th>
									<th data-options="field:'com_goods_variety_id',width:150">商品品种</th>
									<th data-options="field:'com_lot',width:100">批号</th>
									<th data-options="field:'efficiency_date',width:200">有效日期</th>
									<th data-options="field:'expire_date',width:200">失效日期</th>
									<th data-options="field:'calibration_dose',width:150">标定剂量</th>
									<th data-options="field:'capacity',width:50">容量</th>					
								</tr>
						</thead>
			        </table>
			</div> 
    		<div id="dlg-buttons">
        			<a href="#" class="easyui-linkbutton" onclick="onComGoodsdg();">保存</a> 
        			<a href="#" class="easyui-linkbutton" onclick="closeDialog();">关闭</a>
    		</div>
    		 <div id="toolbar_div_comGoods" style="padding:2px 5px;text-align:right;">
				<a class="easyui-linkbutton easyui-tooltip" title="查询商品" onClick="doSearchComGoods();">查询</a>
				<input id="comGoodsId" class="easyui-textbox" label="商品ID:" labelWidth="55px" style="width:200px;height:25px;">
				<input id="comGoodsName" class="easyui-textbox" label="商品名称:" labelWidth="65px" style="width:200px;height:25px;">
			</div>
		</div>
    </div>
    
    
 <script type="text/javascript">
 var childData;
 var editIndex = undefined;//定义编辑列的索引
 function endEditing() {//判断是否处于编辑状态
     if (editIndex == undefined) { return true }
     if ($('#sscPurchaseLines').datagrid('validateRow', editIndex)) {
         $('#sscPurchaseLines').datagrid('endEdit', editIndex);
         editIndex = undefined;
         return true;
     } else {
         return false;
     }
 }
 //单击单元格的时候触发
 function onClickCell(index, field) {
	 if(field=="pur_qty"){
	     if (endEditing()) {
	         $('#sscPurchaseLines').datagrid('selectRow', index).datagrid('editCell', { index: index, field: field });
	         editIndex = index;
	     }
	 }else{
		 endEditing();
	 }
 }
 function doSearchComGoodsVariety(value){
	 var node = $('#tt').tree('getRoot'); 
	 var childData1 = $("#tt").tree('getChildren',node.target);
	//删除所有子节点
	for(var i=0;i<childData1.length;i++){
		$("#tt").tree('remove',childData1[i].target);
	}
 
	for (i = 0; i < childData.length; i++) {
        var treeId = childData[i].id;
        var treeName = childData[i].text;
        if (treeName.indexOf(value) >= 0) {
        	$("#tt").tree('append',{parent:(node ? node.target : null),data : {id:treeId,text:treeName}}); 
        }
    }
 }
  
 var type;
 function addRow(){
	 type=1;
	 $('#ReceiveFeedBackDialog').dialog('open');
 }
 function onDblClickCell(index,field,value){
	 if(field=="goods_name"){
		 type=2;
	    $('#ReceiveFeedBackDialog').dialog('open');
	 }
 }
//查询商品
 function doSearchComGoods(){
 	getJSON(window.localStorage.ctx+"/comGoods/findComGoods",
 			{
 				comGoodsId : $('#comGoodsId').textbox('getValue'),
 				comGoodsName : $('#comGoodsName').textbox('getValue'),
 			},
 			function(data){
 				$("#comGoodsdg").datagrid("loadData",validationData(data));
 			});
 }
 function onComGoodsdg(index,field,value){
	 var row = $('#comGoodsdg').datagrid('getSelected');
	 //type=1 表示新增行 type=2表示修改
	 if(type==1){
		 if(checkRow(row)){
			 return _message("商品:"+row.goods_name+"已添加","提示！！");
		 }
		 $('#sscPurchaseLines').datagrid('appendRow',{goods_id:row.id,goods_name:row.goods_name,com_lot:row.com_lot,efficiency_date:row.efficiency_date,expire_date:row.expire_date,calibration_dose:row.calibration_dose,capacity:row.capacity});
	     $('#ReceiveFeedBackDialog').dialog('close');
	 }else if(type==2){
	     if(checkRow(row)){
	    	 return _message("商品:"+row.goods_name+"已添加","提示！！");
		 }
	     var purRow =   $('#sscPurchaseLines').datagrid('getSelected');
	     var purindex=	$('#sscPurchaseLines').datagrid('getRowIndex',purRow);
	     $('#sscPurchaseLines').datagrid('updateRow',{index:purindex,row:{goods_id:row.id,goods_name:row.goods_name,com_lot:row.com_lot,efficiency_date:row.efficiency_date,expire_date:row.expire_date,calibration_dose:row.calibration_dose,capacity:row.capacity}});
	     $('#ReceiveFeedBackDialog').dialog('close');
	 }
 }
 
 function closeDialog(){
	 $('#ReceiveFeedBackDialog').dialog('close');
 }
 
 function checkRow(comGoodsRow){
	 var rows = $('#sscPurchaseLines').datagrid('getRows');
	 for (var i = 0; i < rows.length; i++) {
		 if(comGoodsRow.id==rows[i].goods_id){
			 var purRow   =   $('#sscPurchaseLines').datagrid('getSelected');
		     var purindex =	 $('#sscPurchaseLines').datagrid('getRowIndex',purRow);
		     var purindex1 =	 $('#sscPurchaseLines').datagrid('getRowIndex',rows[i]);
		     if(purindex==purindex1){
		    	 return false;
		     }
		     return true;
		 }
	}
	 return false;
 }
 
 function add(){
	 $('#purchaseForm').form('clear');
	 var userName="<%=session.getAttribute("userName")%>";
	 $("#creator").textbox("setValue",userName);
	 $("#order_start_date").datebox("setValue","Today");
	 $("#sscPurchaseLines").datagrid("loadData",{"total":0,"rows":[]});
 }
 function endDateSelect(data){
	 var startDate=$("#order_start_date").datebox("getValue");
	 var endDate=$("#order_end_date").datebox("getValue");
	 if(compareDate(startDate,endDate)){
		  $("#order_end_date").datebox("setValue","");
		  _message('结束日期不能小于开始日期！！','提示');
		  document.getElementById('order_end_date').focus();
		  return;
	 }
 }
 function savePurchase(){
	 endEditing();
	 var isValid =$("#purchaseForm").form('validate');
	 var rows = $('#sscPurchaseLines').datagrid('getRows');
	//表单验证
	 if(isValid){
	 var data=$("#purchaseForm").serializeArray();
	 var dataLines=$('#sscPurchaseLines').datagrid('getChanges');
	 for (var i = 0; i < dataLines.length; i++) {
		if(dataLines[i].pur_qty==null||dataLines[i].pur_qty==""){
			return _message('商品ID:'+dataLines[i].goods_id+"的订单数量不能为空！！",'提示');
		}
	}
	 
	 var json={};
	 
	 for(var i=0;i<data.length;i++){
		 json[data[i]['name']]=data[i]['value'];
	 }
	 
	 if(rows.length==0){
		 return $.messager.alert('提示','请在明细行不能为空');
	 }
	 
	 getJSON(window.localStorage.ctx+"/purchase/save",{data : JSON.stringify(json),dataLines:JSON.stringify(dataLines)},
		function(data){
			var json=validationData(data);
		 	if(json.code==0){
		 		_message("保存成功","提示");
		 		$('#tt').tree('reload');
		 		//加载完成后，默认自动选中最后一次节点
		 		selectLastTree();
		 	}else{
	    		_message("保存失败，请重试，如果问题依然存在，请联系系统管理员！","错误");
	    	}
		});   
	}
 }
 var selecdNone; //用来记录最后一次选择的节点
 function findPurchaseOrderById(id){
		selecdNone=$('#tt').tree('getSelected');
	 	if($('#tt').tree('isLeaf',selecdNone.target)){
			$.getJSON(window.localStorage.ctx+"/purchase/findSscPurchase",{id:id},function(data) {
				$("#purchaseForm").form("load", validationData(data)); 
			});
			$.getJSON(window.localStorage.ctx+"/purchase/findSscPurchaseLines",{id:id},function(data) {
				$("#sscPurchaseLines").datagrid("loadData", validationData(data)); 
			});
	 	}
	 	
 }
 function submitPurchase(){
	 var node=$('#tt').tree('getSelected');
	 var status=$("#status").textbox("getValue");
	 var id=$("#id").textbox("getValue");
	 if(node==null){
		 return $.messager.alert('提示','请在左侧选择要提交的采购订单！！！');
	 }
	 if(!$('#tt').tree('isLeaf',node.target)){
		 return $.messager.alert('提示','请选择子节点提交！！！');
	 };
	 if("1"==status){
		 $.messager.confirm('提示','是否提交',function(r){
			    if (r){
			    	 $.getJSON(window.localStorage.ctx+"/purchase/submitSscPurchase",{id:id,status:status},function(data) {
			    		 var json=validationData(data);
			 		 		if(json.code==0){
			 		 			$('#tt').tree('reload');
			 		 			$.messager.alert('提示','提交成功！！');
			 		 			add();
			 		 		}else{
			 		 			_message("提交失败，请重试，如果问题依然存在，请联系系统管理员！","错误");
			 		 		}
						});
			    }
			});
	 }else{
		 return $.messager.alert('提示','该订单已经提交！！');
	 }
 }
 function selectLastTree(){
	 if(selecdNone!=null){
		 $("#tt").tree("expandAll");
		 var node = $("#tt").tree('find',selecdNone.id);
         $("#tt").tree("select",node.target); 
    }
	var node = $('#tt').tree('getRoot'); 
	//获取根节点的子节点
	childData = $("#tt").tree('getChildren',node.target);
 }
 function deletePurchase(){
	 var id=$("#id").textbox("getValue");
	 $.messager.confirm('提示','是否删除',function(r){
		    if (r){
		    	 $.getJSON(window.localStorage.ctx+"/purchase/deleteSscPurchase",{id:id},function(data) {
		    		 var json=validationData(data);
		 		 		if(json.code==0){
		 		 			$('#tt').tree('reload');
		 		 			$.messager.alert('提示','删除成功！！');
		 		 		}else{
		 		 			_message("删除失败，请重试，如果问题依然存在，请联系系统管理员！","错误");
		 		 		}
					});
		    }
		});
 }

</script>   
</body>
</html>