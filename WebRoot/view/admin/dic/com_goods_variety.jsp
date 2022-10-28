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
	    <header style="color:#8aa4af;">您当前的位置：字典管理  > 品种管理</header>
		<table id="comGoodsVarietydg" class="easyui-datagrid" style="width:100%;"
			data-options="singleSelect:true,fit:true,toolbar:'#toolbar_div_comGoods',
				autoRowHeight:true,fitColumns:true,onClickCell: onClickCell,scrollbarSize:0,url:'${ctx}/goodsVariety/findComGoodsVariety?deleted=0'">
			<thead>
					<tr>
						<th data-options="field:'com_goods_variety_code',width:40,editor:'textbox'">品种编码</th>
						<th data-options="field:'com_goods_variety_name',width:100,editor:'textbox'">品种名称</th>
						<th data-options="field:'operate',width:60,align:'center',formatter:aaa">操作</th>
					</tr>
			</thead>
        </table>
		 <div id="toolbar_div_comGoods" style="padding:2px 5px;text-align:right;">
		 	<shiro:hasPermission name="edit_examItem">
				<a class="easyui-linkbutton easyui-tooltip" title="新建品种" onClick="addGoodsVerietydlg();">新建</a>
				<a class="easyui-linkbutton easyui-tooltip" title="保存品种" onClick="saveGoodsVeriety();">保存</a>
			</shiro:hasPermission>
			<input class="easyui-searchbox" id="itemName_search"
				data-options="prompt:'请输入品种编码或名称' ,searcher:doSearchComGoodsVariety" style="width:200px;">
		</div>
	</div>
	
<script type="text/javascript">
</script>
</body>
</html>