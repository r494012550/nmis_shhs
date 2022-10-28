<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
<script type="text/javascript"
	src="${ctx}/js/easyui/datagrid-groupview.js"></script>
</head>
<body>
	<div class="easyui-panel" data-options="fit:true"
		style="padding: 10px;">
		<header style="color:#8aa4af;">您当前的位置：模板 > 病症所见样式库</header>
		<table id="xsldg" class="easyui-datagrid"
			data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_xsl',
				url:'${ctx}/template/getXsltTemplates',autoRowHeight:true,
				loadMsg:'加载中...',emptyMsg:'没有查找到模板...',fitColumns:true,
				onRowContextMenu: function(e,index ,row){
		            e.preventDefault();
		            $(this).datagrid('selectRow',index);
		            $('#cmenu_xsl').menu('show', {
		                left:e.pageX,
		                top:e.pageY
		            });
		        },
		        view:groupview,
	            groupField:'belongreport',
	            groupFormatter:function(value,rows){
	                return value + ' - ' + rows.length + ' 个';
	            },
	            rowStyler: function(index,row){
                    if (!row.xslt_sr&&row.name!='snapshot'){
                        return 'background-color:#FFA116;';
                    }
                }">
			<thead>
				<tr>
					<th data-options="field:'name',width:200">名称</th>
					<th data-options="field:'displayname',width:200">显示名称</th>
					<th data-options="field:'belongreport',width:200">所属报告</th>
					<th data-options="field:'viaversion',width:60">via版本</th>
					<th data-options="field:'creator_name',width:100">创建人</th>
					<th data-options="field:'createtime',width:120">创建时间</th>
					<th data-options="field:'operate',width:180,align:'center',formatter:operatecolumn_xsltdg">操作</th>
					<!-- 
					<th data-options="field:'xslt',width:200">文本样式文件所属报告</th>
						<th data-options="field:'xslt_sr',width:200">结构化样式文件</th>
					
					<th data-options="field:'displayvalue',width:60">病症所见显示值</th>
						<th data-options="field:'source',width:60">病症所见来源</th> -->
				</tr>
			</thead>
			<thead data-options="frozen:true">
				<tr>

				</tr>
			</thead>
		</table>


		<div id="cmenu_xsl" class="easyui-menu" style="width: 120px;">

			<shiro:hasPermission name="edit_XsltTemplate">
				<div onclick="openModifyXsltDialog();" id="xslt_modify_btn">修改</div>
			</shiro:hasPermission>
			<shiro:hasPermission name="delete_XsltTemplate">
				<div onclick="delXslt();" id="xslt_del_btn">删除</div>
			</shiro:hasPermission>
			<shiro:hasPermission name="upload_XsltTemplate">
				<div onclick="openUploadXsltDialog();" id="xslt_upload_btn">上传</div>
			</shiro:hasPermission>
			<shiro:hasPermission name="download_XsltTemplate">
				<div onclick="downloadXslt();" id="xslt_download_btn">下载</div>
			</shiro:hasPermission>
		</div>
		<div id="toolbar_div_xsl" style="padding: 2px 2px; text-align: right;">
			<shiro:hasPermission name="edit_XsltTemplate">
				<a class="easyui-linkbutton easyui-tooltip" id="xslt_new_btn"
					title="新建" onClick="$('#editxsltDlg').dialog('open');">新建</a>
			</shiro:hasPermission>
			<shiro:hasPermission name="download_XsltTemplate">
				<a class="easyui-linkbutton easyui-tooltip" id="xslt_new_btn"
					title="下载" onClick="downloadXslt();">下载</a>
			</shiro:hasPermission>

			<select class="easyui-combobox" id="viavsn_xslt_lib"
				name="viaversion" labelPosition="top" style="width: 120px;"
				data-options="prompt:'via版本',valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0014',editable:false,onSelect:doSearchXsltTemplate,icons: [{
						iconCls:'icon-clear',
						handler: function(e){
							$('#viavsn_xslt_lib').combobox('setValue', '');
							doSearchXsltTemplate('');
						}
					}]" />
			<input id="sb_xslt_lib" class="easyui-searchbox"
				data-options="prompt:'请输入模板的名称',searcher:doSearchXsltTemplate"
				style="width: 300px;">
		</div>
	</div>

	<div id="editxsltDlg" class="easyui-dialog" title="上传文件"
		data-options="modal:true,closed:true,border:'thin',buttons:'#dlg-buttons_xslt_edit',shadow:false"
		style="width: 380px; height: 490px; padding: 5px">
		<form id="editorXslt_fm" method="post">
			<div
				style="padding: 5px 5px 5px 5px; margin-left: auto; margin-right: auto; width: 310px;">

				<div style="margin-bottom: 5px">
					<input class="easyui-textbox" label="病症所见名称：" labelPosition="top"
						id="findingname"
						data-options="prompt:'请输入病症所见名称...',required:true,missingMessage:'必填'"
						name="name" style="width: 300px; height: 60px;">
				</div>
				<div style="margin-bottom: 5px">
					<input class="easyui-textbox" label="病症所见显示名称:" labelPosition="top"
						id="displayname" data-options="prompt:'请输入病症所见显示名称...'"
						name="displayname" style="width: 300px; height: 60px;">
				</div>
				<div style="margin-bottom: 5px">
					<input class="easyui-textbox" label="病症所见所属报告：" labelPosition="top"
						id="belongreport"
						data-options="prompt:'请输入病症所见所属报告...',required:true,missingMessage:'必填'"
						name="belongreport" style="width: 300px; height: 60px;">
				</div>
				<div style="margin-bottom: 5px">
					<input class="easyui-textbox" label="病症所见显示值:" labelPosition="top"
						id="displayvalue" data-options="prompt:'请输入病症所见显示值...'"
						name="displayvalue" style="width: 300px; height: 60px;">
				</div>
				<div style="margin-bottom: 5px">
					<input class="easyui-textbox" label="病症所见来源：" labelPosition="top"
						id="source" data-options="prompt:'请输入病症所见来源...'" name="source"
						style="width: 300px; height: 60px;">
				</div>
				<div style="margin-bottom: 5px">
					<select class="easyui-combobox" id="viaversion" name="viaversion"
						label="syngo.via版本：" labelPosition="top" style="width: 300px;"
						data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0014',required:true,editable:false" />
				</div>
			</div>

			<input id="xsltid" name="id" type="hidden">
		</form>
	</div>
	<div style="text-align: center; padding: 5px 0"
		id="dlg-buttons_xslt_edit">
		<a class="easyui-linkbutton" style="width: 60px; height: 30px;"
			onclick="saveXslt();">保存</a> <a class="easyui-linkbutton"
			style="width: 60px; height: 30px;" onclick="closeEditXsltDialog();">取消</a>
	</div>

	<div id="uploadXsltDlg" class="easyui-dialog" title="上传文件"
		data-options="modal:true,closed:true,border:'thin',buttons:'#dlg-buttons_xslt'"
		style="width: 380px; height: 240px; padding: 5px">
		<form id="uploadfm" method="post" enctype="multipart/form-data">
			<div
				style="padding: 5px 5px 5px 5px; margin-left: auto; margin-right: auto; width: 310px;">
				<div style="margin-bottom: 5px">
					<input id="xslt_text" class="easyui-filebox" name="xslt_text"
						label="文本样式文件：" labelPosition="top"
						data-options="prompt:'选择样式文件...',buttonText: '选择文件...',accept:'.xslt'"
						style="width: 300px; height: 60px;" />
				</div>
				<div style="margin-bottom: 5px">
					<input id="xslt_sr" class="easyui-filebox" name="xslt_sr"
						label="结构化样式文件：" labelPosition="top"
						data-options="prompt:'选择样式文件...',buttonText: '选择文件...',accept:'.xslt'"
						style="width: 300px; height: 60px;" />
				</div>
			</div>

			<input id="xsltid1" name="id" type="hidden">
		</form>
	</div>

	<div style="text-align: center; padding: 5px 0" id="dlg-buttons_xslt">
		<a class="easyui-linkbutton" style="width: 60px; height: 30px;"
			onclick="uploadXsltFiles();">上传</a> <a class="easyui-linkbutton"
			style="width: 60px; height: 30px;" onclick="closeUploadDialog();">取消</a>
	</div>

</body>
</html>