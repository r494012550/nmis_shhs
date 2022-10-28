function saveColor(index,value){

	var row = $('#colordg').datagrid("getRows")[index];
	row.color=value;
	$.post(window.localStorage.ctx+"/color/saveColor",row,function(data){

		 	var json=validationDataAll(data);
	    	if(json.code==0){
	    		$('#colordg').datagrid("updateRow",
	    			{        
	    				index:index,
	    				row:{id:json.data.id},    
	    			}
	    		);
	    	}
	    	else{
	    		_message('保存失败，请重试，如果问题依然存在，请联系系统管理员！','错误');
	    	}
	 });
}

function columeStyler_color(value, rowData, rowIndex){
	return '<input type="color" id="'+rowIndex.toString()+'" name="color" onchange="saveColor('+rowIndex+', this.value);" value="'+value+'">';
}

//function setColor(index, color){
//	var oldData = $('#colordg').datagrid("getRows")[parseInt(index)];
//	oldData.color = color;    
//	$('#colordg').datagrid("updateRow",
//			{        index:index,        row:oldData,    }
//	);
//	saveColor(oldData);
//}