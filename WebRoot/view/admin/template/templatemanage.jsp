<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title>jQuery EasyUI Demo</title>
</head>
<body>
	<div id="main_panel" class="easyui-panel" data-options="fit:true" style="padding:0px 0px 0px 10px;">
	<!-- <a class="easyui-linkbutton" style="width:120px;" onclick="generateHtml()">生成html</a> -->
	<header style="color:#8aa4af;">您当前的位置：模板  > 报告模板</header>
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'west',split:true" style="width:225px;padding:10px;" border="0">
				<div class="easyui-layout" data-options="fit:true">
					<div data-options="region:'north',split:true" style="height:80px;" border="0">
						<div style="margin-bottom:5px">
						请选择设备类型：
			               <select id="topModality" name="topModality" class="easyui-combobox" style="width:100%;height:28px;" 
			               		data-options="prompt:'请选择设备类型...',valueField:'code',textField:'name_zh',
			               		url:'${ctx}/syscode/getCode?type=0004',editable:false,onChange:modalityChange,
			               		onLoadSuccess:function(){
			               			$(this).combobox('select','CT');

			               		}">
			               </select>
		               </div>
		               	 模板分类：
					</div>
					<div data-options="region:'center',split:true,border:true">
						
						<div class="easyui-panel" data-options="fit:true,border:false" style="height:99%;">
							 <div class="easyui-datalist" style="width:100%;height:30px;" border="0" data-options="toolbar:'#tree_tool'"></div>
							 <ul id="nodetree" class="easyui-tree" data-options="
				       			onContextMenu: function(e,node){
				                    e.preventDefault();
				                    $(this).tree('select',node.target);
				                    $('#mm_tree').menu('show',{
				                        left: e.pageX,
				                        top: e.pageY
				                    });
				                },
				                onAfterEdit:function(node){
				                	submitMidifynode_(node);
				                },
				                onBeforeExpand:function(node){
				                	expandTree(node);
				                },
				                onSelect:function(node){
				                	getTemplate_(node);
				                }">
				         	</ul>
						</div>
						<div id="tree_tool" style="padding:2px 2px;">
						   <shiro:hasPermission name="edit_tempNode">
						   <!-- <a class="easyui-linkbutton easyui-tooltip" data-options="plain:true" onclick="addnode();" title="添加根节点"><i class="icon iconfont icon-plus4"></i></a> -->
						   <a class="easyui-linkbutton easyui-tooltip" data-options="plain:true" onclick="addChildNode();" title="添加子节点"><i class="icon iconfont icon-tianjia"></i></a>
						   <a class="easyui-linkbutton easyui-tooltip" data-options="plain:true" onclick="midifynode();" title="修改节点"><i class="icon iconfont icon-edit"></i></a>
						   </shiro:hasPermission>
						   <shiro:hasPermission name="delete_tempNnode">
						   <a class="easyui-linkbutton easyui-tooltip" data-options="plain:true" onclick="deletenode();" title="删除节点"><i class="icon iconfont icon-delete"></i></a>
						   </shiro:hasPermission>
					    </div>
			       		<div id="mm_tree" class="easyui-menu" style="width:120px;">
			       		 	<shiro:hasPermission name="edit_tempNode">
					        <!-- <div onclick="addnode();">添加根节点</div> -->
					        <div onclick="addChildNode();">添加子节点</div>
					        <div class="menu-sep"></div>
					        <div onclick="midifynode();">修改节点</div>
					        </shiro:hasPermission>
					        <shiro:hasPermission name="delete_tempNnode">
					        <div onclick="deletenode();">删除节点</div>
					        </shiro:hasPermission>
					        <shiro:hasPermission name="edit_temp">
					        <div class="menu-sep"></div>
					        <div onclick="showNewTemplate();">添加模板</div>
					        </shiro:hasPermission>
					    </div>
					    <div id="progressDlg" class="easyui-dialog" data-options="noheader:true,modal:true,border:false,closed:true" style="width:38px;height:38px;margin-left:auto;margin-right:auto;">
					        <img src="${ctx}/themes/icons/loading.gif"  alt="正在加载中..." />
					    </div>
					    <div id="node_windows" class="easyui-window" title="添加根节点" style="width:300px;height:170px"
								        data-options="modal:true,closed:true,collapsible:false,minimizable:false,maximizable:false,footer:'#footer1',border:'thin'">
								        
					        <form name="nodeForm" id="nodeForm" method="POST">
								<div style="padding:5px 5px 5px;margin-left:auto;margin-right:auto;width:250px;">
									<!-- div style="margin-bottom:5px">
						               <input id="modality" name="modality" class="easyui-combobox" style="width:100%;height:50px;" label="设备类型：" labelPosition="top" data-options="prompt:'请选择设备类型...',valueField:'name_zh',textField:'name_zh',url:'template/getModalityType?',editable:false" required="true">
						            </div-->
						            <div style="margin-bottom:5px;">
						                <input class="easyui-textbox" id="nodename" label="节点名称：" labelPosition="top" data-options="prompt:'请输入节点名称...',required:true,missingMessage:'必填',
												validType:{
													length:[1,50]
												}" name="nodename" style="width:100%;height:50px;">
						            </div>
								</div>
							</form>
							
						</div>
					    <div id="footer1" style="text-align:center;padding:5px 0" border="0">
					            <a class="easyui-linkbutton" style="width:80px;height:30px" onclick="saveRootNode();">保存</a>
								<a class="easyui-linkbutton" style="width:80px;height:30px" onclick="closeNodeDialog();">取消</a>
					    </div>
					     <div id="childNode_windows" class="easyui-window" title="添加子节点" style="width:300px;height:170px"
								        data-options="modal:true,closed:true,collapsible:false,minimizable:false,maximizable:false,footer:'#footer2',border:'thin'">
								        
					        <form name="childNodeForm" id="childNodeForm" method="POST">
								<div style="padding:5px 5px 5px;margin-left:auto;margin-right:auto;width:250px;">
									
						            <div style="margin-bottom:5px;">
						                <input class="easyui-textbox" id="childnodename" label="节点名称：" labelPosition="top" data-options="prompt:'请输入节点名称...',required:true,missingMessage:'必填',
												validType:{
													length:[1,50]
												}" name="nodename" style="width:100%;height:60px;">
						            </div>
								</div>
								
							</form>
							
						</div>
						<div id="footer2" style="text-align:center;padding:5px;">
				            <a class="easyui-linkbutton" style="width:80px;height:30px" onclick="saveChildNode();">保存</a>
							<a class="easyui-linkbutton" style="width:80px;height:30px" onclick="closeChildNodeDialog();">取消</a>
			    		</div>
					</div>
				</div>    
			
			</div>
			<div data-options="region:'center'" border="0" style="padding:10px;">
				<div class="easyui-layout" data-options="fit:true">
					<div data-options="region:'north',split:true" style="height:80px;padding:25px 0px 2px 0px;" border="0">
						<div style="margin-bottom:5px">
						请选择左侧模板分类，可显示相关的模板。
						</div>
						<div style="margin-bottom:5px">
						模板列表：
						</div>
					</div>
					<div data-options="region:'center',split:true" border="0">
					
						<table id="templist" class="easyui-datagrid"
		       				data-options="fit:true,textField:'name',valueField:'id',toolbar:'#toolbar_div',emptyMsg:'没有找到模板，请添加模板',singleSelect:true,
		       								fitColumns:true,onSelect:onSelect_templist">
		       								
		       				<thead>
								<tr>
									<th data-options="field:'name',width:200">模板名称</th>
									<th data-options="field:'creator_name',width:100">创建人</th>
									<th data-options="field:'createtime',width:150">创建时间</th>
								</tr>
							</thead>
		       				
					    </table>
					    <div id="toolbar_div" style="padding:2px 5px;">
					    	<shiro:hasPermission name="edit_temp">
					        <a id="new_linkbutton" class="easyui-linkbutton easyui-tooltip" title="添加模板" plain="true" onClick="showNewTemplate();"><i class="icon iconfont icon-plus2"></i></a>
					        <a id="modify_linkbutton" class="easyui-linkbutton easyui-tooltip" title="修改模板" plain="true" onClick="showModifyTemplate()"><i class="icon iconfont icon-edit"></i></a>
					        </shiro:hasPermission>
					        <shiro:hasPermission name="delete_temp">
					        <a id="del_linkbutton" class="easyui-linkbutton easyui-tooltip" title="删除模板" plain="true" onClick="deleteAdminTemplate()"><i class="icon iconfont icon-delete"></i></a>
					        </shiro:hasPermission>
					    </div>
					  </div>
					  <div data-options="region:'south',split:true"  style="height:200px;" border="1">
					  	请选择列表中的模板，可以预览模板内容：
					  	<div id="previewtemplate" class="easyui-panel" data-options="border:true" style="height:175px;width:100%;padding:5px;">
					    	
					    </div>
					  </div>
				</div>    
						
			</div>
		</div>
	</div>
	
	<!-- <div id="temp_panel" title="报告模板->新建模板" class="easyui-panel" data-options="fit:true,footer:'#footer_panel1'" style="display:none;padding:5px;">
		
		<div style="padding:1px 30px 20px;margin-left:auto;margin-right:auto;width:700px;background-color:#FFFFFF;">
	
			<form name="tempForm" id="tempForm_ad" method="POST">
				
			    <p><b>模板名称：</b></p>
			    <div><input class="easyui-textbox" name='name' id='templateName' maxlength='50' style='width:100%;height:30px'
			    			data-options="prompt:'请输入模板名称',required:true,missingMessage:'必填',
			    			onChange:generateCode"/>
			    </div>
			    <p><b>模板编码：</b></p>
			    <div><input class="easyui-textbox" name='code' id='templateCode' maxlength='50' style='width:100%;height:30px'
			    			data-options="prompt:'请输入模板编码',required:true,missingMessage:'必填'"/>
			    </div>
			    <p><b>检查方法：</b></p>
			    <div><input class="easyui-textbox" name="studymethod" id="studymethod" maxlength="100" style="width:100%;height:28px;"  
		        			type="text" data-options="prompt:'请输入检查方法'"/>
			    </div>
			    
				<p><b>所见：</b></p>
				<script id="desc_ad" type="text/plain" name="desccontent_html" style="width:100%;height:250px"></script>
				
				<p><b>诊断：</b></p>
				<script id="result_ad" type="text/plain" name="resultcontent_html" style="width:100%;height:250px"></script>
				
				<input id="templateid" name="id" type="hidden">
				<input id="ispublic" name="ispublic" type="hidden">
				<input id="desccontent_ad" name="desccontent" type="hidden">
				<input id="resultcontent_ad" name="resultcontent" type="hidden">
			</form>
		</div>
		
		<div id="footer_panel1" style="padding:2px 5px;">
			<a class="easyui-linkbutton" style="width:80px;height:30px" onclick="saveAdminTemplate();">保存</a>
			<a class="easyui-linkbutton" style="width:80px;height:30px" onclick="consoleAddTemplate();">取消</a>
		</div>
	</div> -->
			
</body>
</html>