<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div style="padding:5px;width:385px;">
	
		<div class="easyui-layout" style="width:538px;height:410px;">
	        <div data-options="region:'north',border:false" style="height:65px;">
	        	<input class="easyui-textbox" data-options="label:'${sessionScope.locale.get("report.name")}',labelPosition:'top'" value="${studyitems}" 
	        		style="width:100%;" id="reportdesc"> 
	        </div>
	       
	        <div data-options="region:'center'">
	        	<ul id="favorites_tree" class="easyui-tree" 
			    	data-options="url:'${ctx}/report/getFavoritesNodes',method:'get',fit:true,dnd:true,
			    	onAfterEdit:function(node){
		                	modifyFavoritesnode(node);
		            },
		            onClick:function(node){
		            	$('#favoritesreport_list').datalist('reload',{nodeid:node.id});
		            },
		            onDblClick:function(node){
		            	$(this).tree('beginEdit', node.target);
		            },
		            onContextMenu:function(e,node){
	                    e.preventDefault();
						$(this).tree('select', node.target);
						$('#mm_fa_tree').menu('show', {
							left: e.pageX,
							top: e.pageY
						});
                	}"></ul>
		            
		    	<div id="mm_fa_tree" class="easyui-menu" style="width:120px;">
		    		<div onClick="addFavoritesNode()">${sessionScope.locale.get("wl.newfolder")}</div>
		    		<div class="menu-sep"></div>
			        <div onClick="deleteFavoritesnode()">${sessionScope.locale.get("wl.delete")}</div>
			    </div>
	        </div>
	        <div data-options="region:'east'" style="width:60%;">
	        	<div class="easyui-datalist" id="favoritesreport_list" 
	        		data-options="url: '${ctx}/report/findFavoritesreport',fit:true,border:false,textField:'report_desc',
	        			emptyMsg:'请收藏报告...',
	        			onRowContextMenu:function(e,index ,row){
                    	e.preventDefault();
	                    $(this).datagrid('selectRow',index);
	                    $('#mm_fa_datalist').menu('show', {
	                        left:e.pageX,
	                        top:e.pageY
	                    });
	                }">
			    </div>
			    
			    <div id="mm_fa_datalist" class="easyui-menu" style="width:120px;">
			        <div onClick="deleteFavoritesReport()">${sessionScope.locale.get("wl.delete")}</div>
			    </div>
	        </div>
	    </div>
		
	 
	  	<input type="hidden" id="reportId" value='${reportId}'>
	</div>
	 <%--  <div id="mydialog_<%=request.getParameter("studyid")%>" title="添加文件夹" style="display:none;padding:5px;width:300px;height:200px;">  
	    <label class="lbInfo">节点名称：</label>   
	    <input id="txRolename_<%=request.getParameter("studyid")%>" type="text" value="" /><br />   
	  </div>  
	</div> --%>

	

 <script type="text/javascript">
 
 //添加文件夹
 function addFavoritesNode(ischild){
	 
	 var node =$('#favorites_tree').tree('getSelected');
	 if(!node){
		 node=$('#favorites_tree').tree('getRoot');
	 }
	 $.getJSON("${ctx}/report/addFavoritesNode?name="+$.i18n.prop('report.folder')+"&parent_id="+node.id, function(json){
		 //$.messager.progress();
		 if(json.success=="success"){
			 
			 
			/* if(ischild=='0'){
				$('#favorites_tree').tree('insert', {
					after: node.target,
					data: {
						id: json.data.id,						
						text: $.i18n.prop('report.folder')
					}
				});
			}
			else{ */
				$('#favorites_tree').tree('append', {
					parent: node.target,
					data: {
						id: json.data.id,
						//state: 'closed',
						text: $.i18n.prop('report.folder')
					}
				});
			//}
			
			var node1 = $('#favorites_tree').tree('find',  json.data.id);
			$("#favorites_tree").tree('beginEdit', node1.target);
		 }
		 else{
			 $.messager.show({
	            title: $.i18n.prop('error'),
	            msg: $.i18n.prop('savefailed'),
	            timeout:3000,
	            border:'thin',
	            showType:'slide'
	        });
		 }
		 
	 });
}
 
function modifyFavoritesnode(node){
	$.getJSON("${ctx}/report/modifyFavoritesnode?name="+node.text+"&id="+node.id, function(json){
		 //$.messager.progress();
		 if(json.success!="success"){
			 $.messager.show({
		            title: $.i18n.prop('error'),
		            msg: $.i18n.prop('savefailed'),
		            timeout:3000,
		            border:'thin',
		            showType:'slide'
		        });
		 }
	 });
}

function deleteFavoritesnode(){
	
	var node = $('#favorites_tree').tree('getSelected');
	if(node){
		
		$.messager.confirm({
			title: $.i18n.prop('confirm'),
			border:'thin',
			msg: $.i18n.prop('report.confirmdeletefolder'),
			fn: function(r){
				if (r){
					$.getJSON("${ctx}/report/deleteFavoritesnode?id="+node.id, function(json){
						 //$.messager.progress();
						 if(json.success=="success"){
							 $("#favorites_tree").tree('remove', node.target);
							 
							 $('#favoritesreport_list').datalist('loadData',[]);
							 
							 
						 }
						 else{
							 $.messager.show({
						            title: $.i18n.prop('error'),
						            msg: $.i18n.prop('deletefailed'),
						            timeout:3000,
						            border:'thin',
						            showType:'slide'
						        });
						 }
					 });
				}
			}
		});
		
	 }
	else{
		$.messager.show({title : $.i18n.prop('alert'),msg : $.i18n.prop('report.selectfolder'),timeout : 3000,border:'thin',showType : 'slide'});
	}
}


function deleteFavoritesReport(){
	var node = $('#favoritesreport_list').datalist('getSelected');
	if(node){
		$.messager.confirm({
			title: $.i18n.prop('confirm'),
			border:'thin',
			msg: '确认删除选中的收藏报告吗？',
			fn: function(r){
				if (r){
					$.getJSON("${ctx}/report/deleteFavoritesReport?id="+node.id, function(json){
						 if(json.success=="success"){
							 var index=$('#favoritesreport_list').datalist('getRowIndex',node);
							 $('#favoritesreport_list').datalist('deleteRow',index);
						 }
						 else{
							 $.messager.show({
						            title: $.i18n.prop('error'),
						            msg: $.i18n.prop('deletefailed'),
						            timeout:3000,
						            border:'thin',
						            showType:'slide'
						        });
						 }
					 });
				}
			}
		});
		
	 }
	else{
		$.messager.show({title : $.i18n.prop('alert'),msg : '请选择收藏的报告！',timeout : 3000,border:'thin',showType : 'slide'});
	}
}

 
 //收藏报告
function saveFavorites(reportId)	{
    
	var desc=$("#reportdesc").textbox("getValue");
	if(desc.trim()==""){
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : '请输入名称',
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
		return;
	}
	var node = $('#favorites_tree').tree('getSelected');
	if(node){
		
		$.post("${ctx}/report/saveFavorites",{reportId : reportId,favoritesId : node.id,reportDesc : desc},function(json){
			if(json.success="success"){
				//$('#common_dialog').dialog('close');
				$('#favoritesreport_list').datalist('reload',{nodeid:node.id});
			}
			else{
				$.messager.show({
					title : $.i18n.prop('error'),
					msg : $.i18n.prop('savefailed'),
					timeout : 3000,
					border:'thin',
					showType : 'slide'
				});
			}
			
		});
		
	} else{
		$.messager.show({
			title : $.i18n.prop('alert'),
			msg : $.i18n.prop('report.selectfolder'),
			timeout : 3000,
			border:'thin',
			showType : 'slide'
		});
	}
	
}

 </script>
</body>
</html>