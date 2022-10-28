/**
 * 
 */
$(function(){
	 
});

//增加
function comGoodsdlg(){
	$('#common_dialog').dialog(
		{
			title : '新建商品',
			width : 650,
			height : 400,
			closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
			border: 'thin',
			href : window.localStorage.ctx+'/comGoods/editComGoods',
			buttons:[{
				text:'保存',
				width:80,
				handler:function(){saveComGoodsdlg();}
			},{
				text:'关闭',
				width:80,
				handler:function(){$('#common_dialog').dialog('close');}
			}]
	});
}

function saveComGoodsdlg(){
	var id=$('#id').textbox('getValue');
	var efficiency_date=$('#efficiency_date').datebox('getValue');
	var expire_date=$('#expire_date').datebox('getValue');
	//判断有效日期不能大于失效日期
	if(compareDate(efficiency_date,expire_date)){
		 _message("有效日期不能大于失效日期","提示");
		 return;
	}
	var str;
	if(id){
		str="updateComGoods";
	}else{
		str="saveComGoods";
	}
	$('#comGoodsform').form('submit', {
		url: window.localStorage.ctx+"/comGoods/"+str,
		success: function(data){
			var json=validationData(data);
			if(json.code==0){
				_message("保存成功！","提示");
				$('#common_dialog').dialog('close');
				doSearchComGoods();
			}
			else{
				_message("保存失败请重试，如果问题依然存在，请联系系统管理员！","错误提醒");
			}
		}
	});
	
}

//查询商品
function doSearchComGoods(){
	getJSON(window.localStorage.ctx+"/comGoods/findComGoods",
			{
				comGoodsId : $('#comGoodsId').textbox('getValue'),
				comGoodsName : $('#comGoodsName').textbox('getValue'),
				comGoodsVariety : $('#comGoodsVariety').textbox('getValue'),
				comLot : $('#comLot').textbox('getValue'),
				productLocation : $('#productLocation').textbox('getValue')
			},
			function(data){
				$("#comGoodsdg").datagrid("loadData",validationData(data));
			});
}
function operatecolumn_comGoods(value, row, index){
	
	return '<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="ModifyComGoodsdlg('+index+');"><span class="l-btn-left"><span class="l-btn-text">修改</span></span></a>&nbsp;&nbsp;'+
			'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteComgGoods('+index+');"><span class="l-btn-left"><span class="l-btn-text">删除</span></span></a>';//"<a href='#' class='easyui-linkbutton' name='operate' onclick='openModifyUserDialog()' ><i class='icon icon-edit1'></i>修改</a>&nbsp;&nbsp;"+
}

function formatterComgoodsStatus(value,row,index){
	if(value=="1"){
		return "可用";
	}else if(value=="2"){
		return "不可用";
	}else{
		return "";
	}
}

function ModifyComGoodsdlg(index){
	$('#comGoodsdg').datagrid('selectRow',index);
	var row=$('#comGoodsdg').datagrid('getSelected');
	if(row){
	$('#common_dialog').dialog(
			{
				title : '编辑商品',
				width : 650,
				height : 510,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,modal : true,
				border: 'thin',
				href : window.localStorage.ctx+'/comGoods/editComGoods?id='+row.id,
				buttons:[{
					text:'保存',
					width:80,
					handler:function(){saveComGoodsdlg();}
				},{
					text:'关闭',
					width:80,
					handler:function(){$('#common_dialog').dialog('close');}
				}]
		});
	}else{
		_message("请选择一个商品！","提醒");
	}
}
function deleteComgGoods(index){
	$('#comGoodsdg').datagrid('selectRow',index);
	var row=$('#comGoodsdg').datagrid('getSelected');
	if(row){
		$.messager.confirm({
			title: '确认删除',
			ok: '是',
			cancel: '否',
			border:'thin',
			msg: '确认删除选中的数据吗？',
			fn: function(r){
				if (r){
					 getJSON(window.localStorage.ctx+"/comGoods/deleteComGoods",{
						 id:row.id
					 } ,function(data){
						 	var json=validationData(data);
						 	if(json.code==0){
						 		_message("删除成功","提示");
						 		doSearchComGoods();
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
		_message("请选择一个商品！","提醒");
	}
}