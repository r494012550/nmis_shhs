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
		<header style="color:#8aa4af;">您当前的位置：系统管理  > 标签管理</header>
		<div id="toolbar_div_label" style="padding:2px 5px;text-align:left;">
			<shiro:hasPermission name="edit_label">
				<a class="easyui-linkbutton easyui-tooltip" id="btn1" title="新建文件夹，不可以在标签下新建" onClick="addLabelFolder();">新建文件夹</a>
		        <a class="easyui-linkbutton easyui-tooltip" id="btn2" disabled="true"  title="修改文件夹"  onClick="modifyLabelFolder();">修改文件夹</a>
				<a class="easyui-linkbutton easyui-tooltip" id="btn3" disabled="true"  title="删除标签文件夹" onClick="deleteLabelFolder();">删除文件夹</a>
				<a class="easyui-linkbutton easyui-tooltip" id="btn4" disabled="true"  title="新建文件夹下的标签"  onClick="addLabel();">新建标签</a>
		        <a class="easyui-linkbutton easyui-tooltip"  id="btn5" disabled="true" title="修改文件夹下的标签"  onClick="modifyLabel();">修改标签</a>
				<a class="easyui-linkbutton easyui-tooltip"  id="btn6" disabled="true" title="删除文件夹下的标签"   onClick="deleteLabel();">删除标签</a>
				<!-- <input class="easyui-searchbox" data-options="prompt:'请输入标签名' ,searcher:doSearchLabel" style="width:300px;"> -->
			</shiro:hasPermission>
		</div>	
		<ui id="labels_tree" class="easyui-tree" style="width:50%; float:left"
			data-options="fit:true,collapsible: true,fitColumns: true,toolbar:'#toolbar_div_label',			
			showFooter: true,rownumbers: true,autoRowHeight:true,
			url:'${ctx}/system/getLabels',method: 'get',idField: 'id',
			onClick:function(node){clickdg(node);},
			onContextMenu: function(e,node){				
              e.preventDefault();
              $(this).tree('select',node.target);
              console.log(node);
              enableMenu(node);             
              $('#mm_label').menu('show',{
                  left: e.pageX,
                  top: e.pageY,
              });
          },
           onAfterEdit:function(node){
          	submitMidifyFolder(node);
          },
          formatter:function(node){
              var s = node.text;
              if (node.label){
                  s = '<span style=\'color:blue\'>' + s + '</span>';
              }
              return s;
          }">
	       
		        
	    </ui>

		<div id="mm_label" class="easyui-menu" style="width:120px;">
		<shiro:hasPermission name="edit_label">
	        <div data-options="name:'newfolder'" onclick="addLabelFolder();">新建文件夹</div>
	        <div data-options="name:'modifyfolder'" onclick="modifyLabelFolder();">修改文件</div>
	        <div data-options="name:'delfolder'" onclick="deleteLabelFolder();">删除文件夹</div>
	        <div class="menu-sep"></div>
	        <div data-options="name:'newlabel'" onclick="addLabel();">新建标签</div>
	        <div data-options="name:'modifylabel'" onclick="modifyLabel();">修改标签</div>
	        <div data-options="name:'dellabel'" onclick="deleteLabel();">删除标签</div>
	    </shiro:hasPermission>
   		</div>
</div>	
</body>
</html>