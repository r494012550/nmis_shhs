/**
 * 预加载
 * 
 * @returns
 */
// 初始化放置区域
var ids = new Array(0);
$(function() {
	// 初始化字段分类
	$("#classifys").tree({
		url : '/classify',
		method : 'GET',
		onClick : function() {
			var node = $(this).tree("getSelected");
			$("#fields").tree({
				url : '/field?classifyId=' + node.id,
				dnd : true,
				onBeforeDrop : function() {
					return false;
				}
			})
		},
		onBeforeDrop : function() {
			return false;
		}
	});

	var temp = new Object();
	var count = 0;
	$("#section_edit").droppable({
		onDragOver : function(e, source) {
			$(source).click();
			var node = $("#fields").tree("getSelected");
			var id = node.id;
			// 判断是否重复添加字段
			if ($.inArray(id, ids) != -1) {
				return false;
			}
			ids.push(id);
			$("#section_home").append(getNode(node));
		},
		onDragLeave : function(e, source) {
		}
	});
	/**
	 * 初始化已存章节
	 */
	$("#sections").tree({
		url : '/section/getTree',
		method : 'GET',
		onClick : function() {
			var node = $(this).tree("getSelected");
			var sectionId = node.id;
			var name = node.text;
			$("#sectionName").val(name);
			$("#section_home").html(node.attributes.content);
			$("#delSection").css('display','inline-block');
			$("#updateSection").css('display','inline-block');
			$("#saveSection").hide();
			ids = new Array(0);
			$("#section_home>div").each(function(index,obj){
				ids.push(obj.id-0);
			})
		},
		onBeforeDrop : function() {
			return false;
		},
		onBeforeLoad : function() {
			$("#delSection").hide();
			$("#updateSection").hide();
			$("#saveSection").css('display','inline-block');
		}
	});
});
/**
 * 根据type 获取字段类型
 */
function getNode(node) {
	sendAjax({
		url:'/worklist/checkSession',
		type:'post',
		success:function(data){
		}
	})     
	var id = node.id;
	var name = node.name;
	var type = node.attributes.type;
	var unit = node.attributes.unit;
	var code = node.attributes.code;
	var values = node.attributes.values;
	var $node = $("<div id='"
			+ id
			+ "' draggable='true' class='form-control' ondblclick='remove(this);' ondrop='drop(event);' ondragover='allowDrop(event);' ondragstart='drag(event);'></div>");
	$node.append($("<label for='field" + id
			+ "'  class='control-label'>" + name
			+ "： </label>"));
	if (type == 0) {
		if (unit) {
			$node
					.append($("<div class='col-sm-10'><div class='input-append'><input name='"
							+ code
							+ "' type='text' disabled='disabled' class='input-medium' id='field"
							+ id
							+ "'/><span class='add-on'>"
							+ unit
							+ "</span></div></div>"));
		} else {
			$node
					.append($("<div class='col-sm-10'><input type='text' name='" + code + "' disabled='disabled' class='input-medium' id='field"
							+ id + "'/></div>"));
		}
	} else if (type == 1) {
		var $select = $("<div class='col-sm-10'><select id='field" + id
				+ "' draggable='false'  name='" + code
				+ "' disabled='disabled' class='input-large'></select></div>");
		$($select).find('select').append(
				$("<option value=''>请选择</option>"));
		for (var i = 0; i < values.length; i++) {
			$($select).find('select').append(
					$("<option value='" + values[i].code + "'>"
							+ values[i].value + "</option>"));
		}
		$node.append($select);
	} else if (type == 2) {
		$node.append($("<div class='col-sm-10'><textarea disabled='disabled' name='" + code + "' id='field" + id
				+ "' class='form-control' style='width:800px'></textarea></div>"));
	}
	return $node;
}

/**
 * 保存章节
 * 
 * @returns
 */
function saveSection() {
	sendAjax({
		url:'/worklist/checkSession',
		type:'post',
		success:function(data){
		}
	})     
	$("#sectionName").validatebox({
		required : true,
		missingMessage : '请输入章节名称'
	});
	var data = new Object();
	data.name = $("#sectionName").val();
	if(!$("#sectionName").val()){
		$.messager.show({
            title:'提示',
            msg: '章节名称不能为空',
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
		return;
	}
	var items = new Array(0);
	$("#section_home>div").each(function(index, obj) {
		var field = new Object();
		field.sort = index;
		field.field_id = obj.id;
		items.push(field);
	})
	if(items.length == 0 ){
		$.messager.show({
            title:'提示',
            msg: '当前章节未保存任何字段',
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
		return;
	}
	data.content = $("#section_home").html();
	data.items = JSON.stringify(items);
	$.ajax({
        'url' : '/section/save',
        'type' : 'POST',
        'data' : data,
        'dataType' : "JSON",
        'success' : function (result) {
            if(result.code == 0){
            	$.messager.show({
                    title:'提示',
                    msg: '保存章节成功',
                    timeout:3000,
                    border:'thin',
                    showType:'slide'
                });
            	$("#sections").tree("reload");
            	newSection();
            }else{
            	$.messager.show({
                    title:'失败',
                    msg: result.message,
                    timeout:3000,
                    border:'thin',
                    showType:'slide'
                });
            }
        }
    })
}
/**
 * 新建章节
 * 
 * @returns
 */
function newSection() {
	sendAjax({
		url:'/worklist/checkSession',
		type:'post',
		success:function(data){
		}
	})     
	data = new Array(0);
	ids = new Array(0);
	$("#sectionName").val("");
	$("#section_home").html("");
	$("#classifys").tree("reload");
	$("#sections").tree("reload");
	$("#fields").html("");
}
/**
 * 移除方法
 * @param obj
 * @returns
 */
function remove(obj) {
	sendAjax({
		url:'/worklist/checkSession',
		type:'post',
		success:function(data){
		}
	})     
	ids.remove(obj.id);
	$(obj).remove();
}
/**
 * 移除数组元素
 */
Array.prototype.indexOf = function(val) {
	for (var i = 0; i < this.length; i++) {
	if (this[i] == val) return i;
	}
	return -1;
};
Array.prototype.remove = function(val) {
	var index = this.indexOf(val);
	if (index > -1) {
	this.splice(index, 1);
	}
};
//h5拖拽事件
function allowDrop(ev) {
	ev.preventDefault();
}
//开始拖拽
function drag(ev) {
	id = ev.target.id;
	ev.dataTransfer.setData("Text", ev.target.id);
}
//拖拽结束
function drop(ev) {
	ev.preventDefault();
	var data = ev.dataTransfer.getData("Text");
	if(ev.target.draggable == true){
		$("#" + data + "").insertAfter(ev.target);
	}else{
		$("#" + data + "").insertAfter($(ev.target).parents("[draggable=true]"));
	}
}

//删除章节
function deleteSection(){
	sendAjax({
		url:'/worklist/checkSession',
		type:'post',
		success:function(data){
		}
	})     
	var node = $("#sections").tree("getSelected");
	if(!node){
		$.messager.show({
            title:'提示',
            msg: '请先选中一个章节',
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
		return;
	}
	$.messager.confirm({
		title : '确认删除',
		ok : '是',
		cancel : '否',
		border:'thin',
		msg : '确认删除该章节吗?',
		fn : function(r) {
			if (r) {
				$.ajax({
			        'url' : '/section/delete?id=' + node.id,
			        'type' : 'POST',
			        'dataType' : "JSON",
			        'success' : function (result) {
			            if(result.code == 0){
			            	$("#sections").tree("reload");
			            	newSection();
			            }else{
			            	$.messager.alert("失败",result.message);
			            }
			        }
			    })
			}
		}
	})
}

//修改章节
function updateSection(){
	sendAjax({
		url:'/worklist/checkSession',
		type:'post',
		success:function(data){
		}
	})     
	var node = $("#sections").tree("getSelected");
	if(!node){
		$.messager.show({
            title:'提示',
            msg: '请先选中一个章节',
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
		return;
	}
	var data = new Object();
	data.id = node.id;
	data.name = $("#sectionName").val();
	if(!$("#sectionName").val()){
		$.messager.show({
            title:'失败',
            msg: '章节名称不能为空',
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
		return;
	}
	var items = new Array(0);
	$("#section_home>div").each(function(index, obj) {
		var field = new Object();
		field.sort = index;
		field.field_id = obj.id;
		items.push(field);
	})
	if(items.length == 0 ){
		$.messager.show({
            title:'提示',
            msg: '当前章节未保存任何字段',
            timeout:3000,
            border:'thin',
            showType:'slide'
        });
		return;
	}
	data.content = $("#section_home").html();
	$.ajax({
        'url' : '/section/update',
        'type' : 'POST',
        'data' : data,
        'dataType' : "JSON",
        'success' : function (result) {
            if(result.code == 0){
            	$.messager.show({
                    title:'提示',
                    msg: '章节修改成功',
                    timeout:3000,
                    border:'thin',
                    showType:'slide'
                });
            	$("#sections").tree("reload");
            	newSection();
            }else{
            	$.messager.show({
                    title:'失败',
                    msg: result.message,
                    timeout:3000,
                    border:'thin',
                    showType:'slide'
                });
            }
        }
    })
}