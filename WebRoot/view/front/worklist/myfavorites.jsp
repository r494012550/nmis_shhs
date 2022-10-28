<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@page import="java.util.Date"%> 
<%@page import="java.text.SimpleDateFormat"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- <script type="text/javascript" src="/js/front/favorites.js"></script> -->
<script type="text/javascript" src="/js/common.js"></script>
<title>Insert title here</title>

</head>
<body>
	<div class="easyui-layout" style="width:100%;height:100%;border:false;">
	    <div data-options="region:'west',split:true" style="width:180px;">
	    	<ul id="myfavorites_tree" class="easyui-tree" data-options="url:'/report/getFavoritesNodes',method:'get',animate:true,dnd:true"></ul>
	    
	    </div>
	    <div data-options="region:'center'" style="width:180px;padding:5px;">
	    	<ul id="book"></ul>
	    </div>
	</div>
	<script type="text/javascript">
		/*
		 <a class="easyui-linkbutton" onclick="exportReport()" style="float:right;margin-right:-120px; margin-top:20px;">导出excel</a>
		
		$(function(){
			 $('#favotite').tree({
				 url : '/report/showReport',
				 onlyLeafCheck : true,
				 onClick : function (node){
					$.ajax({
						 url:'/report/checkReportInfo',
						 type:'Post',
						 data:{favoritesId:node.id},
						 success:function(data){
							 var json = validationData(data);
							 if(json.success="success"){
								 if(json.existFlag==1){
									 $('#book').tree({
										 url : '/report/getReportName?favoritesId='+node.id+'&name='+node.text,
										 onlyLeafCheck : true,
										 checkbox:true,
										 onClick: function (node) {
											  $("#book").tree('beginEdit',node.target);
										},
										onAfterEdit:function(node){
							                	var collNode = $("#book").tree('getSelected');
							                	var favoNode = $("#favotite").tree('getSelected');
							                	sendAjax({
							                		url:'/worklist/checkSession',
							                		type:'post',
							                		success:function(data){
							                		}
							                	})     
							                	$.ajax({
							                		url:'/report/updatefavrivoteName',
							                		type:'Post',
							                		data:{favoritesId:favoNode.id, reportId:collNode.id, desc:collNode.text},
							                		success:function(data){
							                			if(data.code==0){
							                				 $.messager.show({
																	title : '提醒',
																	msg : "修改名称成功 ！",
																	timeout : 300,
																	showType : 'slide'
																});
							                			}
							                		}
							                	})
							             },
									 });
								 } else{
									 $.messager.show({
											title : '提醒',
											msg : "该文件夹里面没有收藏文件 ",
											timeout : 3000,
											showType : 'slide'
										});
									 $('#book').html("");
								 }
								 
							 }
							  
							 
						 }
					 }) 
				 }
			})
			
		})
		//导出Excel
		function exportReport(){
			sendAjax({
				url:'/worklist/checkSession',
				type:'post',
				success:function(data){
				}
			})     
			var nodes = $('#book').tree('getChecked');
			 var favorite =$('#favotite').tree('getSelected');
		     var s = '';
             for (var i = 0; i < nodes.length; i++) {
                   if (s != '') 
                       s += ',';
                   s += nodes[i].id;
               }
			 if(nodes){
				 window.location.href="/report/exportReport?s=default"+"&repordIds="+s+"&favoritesId="+favorite.id;
			 }else{
				 $.messager.show({
						title : '提醒',
						msg : "请选择节点 ",
						timeout : 3000,
						showType : 'slide'
					});
			 }
			
		}*/
	</script>
</body>
</html>