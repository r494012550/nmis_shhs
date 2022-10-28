<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
		<div class="easyui-panel" data-options="fit:true" style="padding:5px;">
			<header style="color:#8aa4af;">您当前的位置：系统管理  > 模块</header>
			<table id="modulesdg" class="easyui-datagrid" style="width:100%;"
				data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_modules',
				url:'${ctx}/system/getModules',autoRowHeight:true,fitColumns:true,
				loadMsg:'加载中...',emptyMsg:'没有查找到模块...',
				onRowContextMenu: function(e,index ,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',index);
		            $('#cmenu_modules').menu('show', {
		                left:e.pageX,
		                top:e.pageY
		            });
		        }">
				<thead>
					<tr>
						<th data-options="field:'name',width:100">模块名称</th>
						<th data-options="field:'url',width:300">URL</th>
						
					</tr>
				</thead>
			</table>
			
			<div id="cmenu_modules" class="easyui-menu" style="width:120px;">
				<shiro:hasPermission name="modulesedit">
		        <div onclick="$('#modulesdialog').dialog('open');">新建模块</div>
		        <div onclick="openModifyModuleDialog();">修改模块</div>
		        </shiro:hasPermission>
		        <shiro:hasPermission name="modulesdelete">
		        <div class="menu-sep"></div>
		        <div onclick="deleteModule();">删除模块</div>
		        </shiro:hasPermission>
		    </div>
			<div id="toolbar_div_modules" style="padding:2px 5px;">
				<shiro:hasPermission name="modulesedit">
					<a class="easyui-linkbutton easyui-tooltip" title="新建模块 " plain="true" onClick="$('#modulesdialog').dialog('open');">新建模块</a>
			        <a class="easyui-linkbutton easyui-tooltip" title="修改模块 " plain="true" onClick="openModifyModuleDialog();">修改模块</a>
			    </shiro:hasPermission>
			    <shiro:hasPermission name="modulesdelete">
			        <a class="easyui-linkbutton easyui-tooltip" title="删除模块 " plain="true" onClick="deleteModule()">删除模块</a>
		        </shiro:hasPermission>
				<input class="easyui-searchbox" data-options="prompt:'请输入模块名称',searcher:doSearchModules" style="width:300px">	        
			</div>
		    
		    <div id="modulesdialog" class="easyui-dialog" title="编辑资源" style="width:370px;height:240px;"
		        data-options="modal:true,closed:true,onClose:function(){clearField_Res();},buttons: '#dlg-buttons-modules',border:'thin'">
			    <form name="moduleform" id="moduleform" method="POST">
					<div style="padding:10px 10px 5px 28px;margin-left:auto;margin-right:auto;width:330px;">
						
						<div style="margin-bottom:10px">
							
			                <input class="easyui-textbox tb" label="模块：" labelPosition="top" id="modulename" data-options="prompt:'请输入模块名称...',required:true,missingMessage:'必填'" name="name" style="width:300px;height:50px;">
			               
			            </div>
			            <div style="margin-bottom:10px">
			                <input class="easyui-textbox" id="moduleurl" label="URL：" labelPosition="top" data-options="prompt:'请输入URL...',required:true,missingMessage:'必填'" name="url" style="width:300px;height:50px;">
			            </div>
			            <input id="moduleid" name="id" type="hidden" value="">
						
					</div>
				</form>
				
			</div>
			<div style="text-align:center;padding:5px 0" id="dlg-buttons-modules">
	            <a class="easyui-linkbutton" style="width:80px;height:32px" onclick="saveModule();">保存</a>
				<a class="easyui-linkbutton" style="width:80px;height:32px" onclick="closeModulesDialog();">取消</a>
	        </div>
		</div>
</body>
</html>