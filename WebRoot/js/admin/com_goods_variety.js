/**
 * 
 */
$(function(){
	 
});

function aaa(value, row, index){
	return '<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteGoodsVarietydg('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>';//"<a href='#' class='easyui-linkbutton' name='operate' onclick='openModifyUserDialog()' ><i class='icon icon-edit1'></i>修改</a>&nbsp;&nbsp;"+
}

var editIndex = undefined;//定义编辑列的索引
function endEditing() {//判断是否处于编辑状态
    if (editIndex == undefined) { return true }
    if ($('#comGoodsVarietydg').datagrid('validateRow', editIndex)) {
        $('#comGoodsVarietydg').datagrid('endEdit', editIndex);
        editIndex = undefined;
        return true;
    } else {
        return false;
    }
}
//单击单元格的时候触发
function onClickCell(index, field) {
    if (endEditing()) {
        $('#comGoodsVarietydg').datagrid('selectRow', index).datagrid('editCell', { index: index, field: field });
        editIndex = index;
    }
}
//保存新增或变更的品种
function saveGoodsVeriety(){
	//保存前结束表格编辑状态
	endEditing();
	var rows=$('#comGoodsVarietydg').datagrid('getChanges');
	getJSON(window.localStorage.ctx+"/goodsVariety/saveComGoodsVariety",
			{
			str : JSON.stringify(rows)
			},
			function(data){
				var json=validationData(data);
			 	if(json.code==0){
			 		_message("保存成功","提示");
			 		doSearchComGoodsVariety();
			 	}else if(json.code==1){
			 		_message(json.message,"提示");
			 	}else{
		    		_message("保存失败，请重试，如果问题依然存在，请联系系统管理员！","错误");
		    	}
			});
}
//新增行
function addGoodsVerietydlg(){
	$('#comGoodsVarietydg').datagrid('insertRow',{index:0,row:{id:"",goods_name:""}});
}

//查询商品
function doSearchComGoodsVariety(value){
	getJSON(window.localStorage.ctx+"/goodsVariety/findComGoodsVariety",
			{
				value : value
			},
			function(data){
				$("#comGoodsVarietydg").datagrid("loadData",validationData(data));
			});
}
//删除商品品种
function deleteGoodsVarietydg(index){
	$('#comGoodsVarietydg').datagrid('selectRow',index);
	var row=$('#comGoodsVarietydg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除选中的数据吗？',
			fn: function(r){
				if (r){
					 getJSON(window.localStorage.ctx+"/goodsVariety/deleteComGoodsVariety",{
						 id:row.id
					 } ,function(data){
						 	var json=validationData(data);
						 	if(json.code==0){
						 		_message("删除成功","提示");
						 		doSearchComGoodsVariety();
						 	}
					    	else{
					    		_message("删除失败，请重试，如果问题依然存在，请联系系统管理员！","错误");
					    	}
					 });
				}
			}
		});
	}
	else{
		_message("请选择一个商品品种！","提醒");
	}
	
}