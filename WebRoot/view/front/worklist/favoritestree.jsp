<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
		
    <ul id="myfavorites_tree_search" class="easyui-tree" 
    	data-options="url:'${ctx}/report/getFavoritesNodes',method:'get',animate:true,dnd:true,
    	
           onDblClick:function(node){
           		findStudyByFavoritesId(node.id);
           }"></ul>
	
</body>
</html>