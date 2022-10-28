<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title></title>
	<style type="text/css">
		.icon-selection{
			background: url('${ctx}/icons/tip3.png?v=${vs}')center center;
		}
		.clear_btn {
			position:absolute;
			background:#fff;
			top:0;
			cursor:pointer;
			right:0px;
			height:40px;
			width:30px;
			text-align:center;
			border-top-right-radius: 20px;
			border-bottom-right-radius: 20px;
			margin-top:1px;
		}
		.clear_btn img {
			height:15px;
			margin-top:0px;
		}
	    
	</style>
</head>
<body>
<script type="text/javascript">
</script>
			<div class="easyui-panel" data-options="fit:true" border="0">
				<header style="color:#8aa4af;">您当前的位置：  项目管理> 表单管理</header>
				<div class="easyui-tabs" data-options="fit:true,plain:true,narrow:true" id="srtemplate_tab">
					<div title="表单" border="0">
						<div class="easyui-layout" data-options="fit:true,border:false">
							<div data-options="region:'east',border:false,href:'srtemplate/goSrtemplate_Right?showsection=false'" style="width: 260px;padding:0px;">
							</div>
							<div data-options="region:'center',border:false" style="padding:0px;" id="center_div">
								<div class="easyui-layout" data-options="fit:true">
									<div data-options="region:'north',border:false" style="height:34px;padding:2px;">
										<div>
											<div style="float:left;padding:3px;">
												<span>当前打开表单：</span><span id="srtemplatename_opened"></span>
											</div>
											<div style="float:right">										
												<shiro:hasPermission name="add_form_template">
												<a class="easyui-linkbutton" onclick="newFormTmp(true);" style="width:80px">${sessionScope.locale.get('admin.new')}...</a>
												<a class="easyui-linkbutton" onclick="newFormTmp(false);" style="width:80px">${sessionScope.locale.get('admin.new')}</a>
												</shiro:hasPermission>
												<!--<shiro:hasPermission name="open_srtemp">-->
												<a class="easyui-linkbutton" onclick="openFormListDlg();" style="width:80px">${sessionScope.locale.get('admin.open')}</a>
												<!--</shiro:hasPermission>-->
												<shiro:hasPermission name="save_form_template">
												<a class="easyui-linkbutton" id="savebtn" data-options="disabled:true" onclick="openSaveFormDialog();" style="width:80px">${sessionScope.locale.get('save')}</a>
												</shiro:hasPermission>
												<shiro:hasPermission name="saveas_form_template">
												<a class="easyui-linkbutton" id="saveasbtn" data-options="disabled:true" onclick="openSaveAsFormDialog();" style="width:80px">${sessionScope.locale.get('saveas')}</a>
										    	</shiro:hasPermission>
											</div>
										</div>
								
									</div>
		    						<div data-options="region:'center',onResize:resizeContainer" id="ueditor_container_sr" style="overflow-y:auto;margin-left:auto;margin-right:auto;padding:2px;">
		    							<!-- <div style="padding:5px;margin-left:auto;margin-right:auto;width:800px;background-color:#FFFFFF;" id="scrollBox"> </div>-->
		    							<script id="ue_template" type="text/plain" name="template_html" style="width:95%;height:100%;margin-left:auto;margin-right:auto;"></script>
		    							
		    						</div>
		    						<div data-options="region:'west',split:true,hideCollapsedContent:false,collapsed:true" title="数据结构" style="width:250px;">
		    							<table class="easyui-treegrid" id="srtemp_datamodels" 
									            data-options="fit:true,border:false,autoRowHeight:false,onSelect:selectDataModel,
									                idField: 'id',
									                treeField: 'display'
									            ">
									        <thead>
									            <tr>
									            	<th data-options="field:'display'" width="250">显示名称</th>
									                <th data-options="field:'code'" width="150">编码</th>
									                <th data-options="field:'type'" width="150">类型</th>
									            </tr>
									        </thead>
									    </table>
		    						</div>
								</div>
								
					    		<input id='templet_id' type="hidden">
					    		<input id='model_id' type="hidden">
					    		<input id='model_name' type="hidden">
							</div>
						</div>
					</div>
					
				</div>
				
			
			<div id = "testdiv" class="easyui-dialog" title="${sessionScope.locale.get('admin.assigncode')}" data-options="closed:true,resizable:true,border:'thin',doSize:true" 
				style="width:780px;height:700px;padding:10px;background-color:#FFFFFF;">
				<div id="testcon" style="width:100%;height:100%;background-color:#FFFFFF;">
				
				</div>
			</div>
			
			
			<!-- <div id="sectionId"></div> -->
			
			<div id = "standardCodeWin" class="easyui-dialog" title="${sessionScope.locale.get('admin.assigncode')}" data-options="zIndex:999,modal:true,closed:true,resizable:true,border:'thin',doSize:true" 
				style="width:570px;height:550px;">
				
				<div class="easyui-tabs" data-options="fit:true,plain:true,narrow:true,border:false">
			        <div title="数据模型" style="padding:10px" data-options="href:'srtemplate/toDomains'">
			           
			        </div>
			        <div title="编码" style="padding:10px">
			        	<div style="margin-bottom:10px">
							<input class="easyui-searchbox"  data-options="prompt:'${sessionScope.locale.get('admin.enterquerycond')}',searcher:searchClinialCode" style="width:550px;height:30px;">
		        		</div>
		         		<div style="margin-bottom:10px">
					    	<table id="conDataGrid_c" class="easyui-datagrid" align="center" style="width:550px;height:320px;margin-bottom:10px;"
							    		data-options="singleSelect:true, rownumbers:true,onDblClickRow:function(index,row){selectStandardcode_(index);}">
							    <thead>
									<tr>
										<th data-options="field:'scheme',width:110" sortable="true">${sessionScope.locale.get('admin.classify')}</th>
										<th data-options="field:'code',width:110" sortable="true">${sessionScope.locale.get('admin.code')}</th>
										<th data-options="field:'meaning',width:210" sortable="true">${sessionScope.locale.get('admin.content')}</th>
										<th data-options="field:'operate',width:80,formatter:codecolumnformatter">${sessionScope.locale.get('admin.operation')}</th>
										
									</tr>
								</thead>
							</table>
						</div>
						<div style="margin-bottom:2px">
							${sessionScope.locale.get('admin.createcode')}
		         		</div>
						<div class="easyui-panel" style="width:550px;height:55px;padding:10px 5px 0px 5px;">
							<select id="standardCodeType2" class="easyui-combobox" style="margin-left:20px;width:80px;height:28px;">
				                <option value="private" >${sessionScope.locale.get('admin.private')}</option>
		           			</select>
							<input  id ="stan_cod_" class='easyui-textbox tb' label="${sessionScope.locale.get('admin.code')}:" labelPosition="before" labelWidth="65" labelAlign="right" style="width:190px;height:28px" >
							<input  id= "stan_cont_" class='easyui-textbox tb' label="${sessionScope.locale.get('report.name')}:" labelPosition="before" labelWidth="65" labelAlign="right" style="width:190px;height:28px">
							<shiro:hasPermission name="add_StandardCode">
							<a class="easyui-linkbutton" style="margin-left: 10px;width:50px;height:25px" onclick="addPrivateCode_()">${sessionScope.locale.get('admin.add')}</a>
							</shiro:hasPermission>
						</div>
			        </div>
			    </div>
				<input type="hidden" id="open_id"/>
			</div>
			<div id="select_dialog"></div>
			<div id="formula_dialog"></div>
		<div style="display: none;" id="temp_div"></div>
    </div>
<!--script type="text/javascript" charset="utf-8" src="../../js/ueditor/ueditor.config.js"></script>
<script type="text/javascript" charset="utf-8" src="../../js/ueditor/ueditor.all.js"> </script>
<script type="text/javascript" charset="utf-8" src="../../js/ueditor/lang/zh-cn/zh-cn.js"></script
<script type="text/javascript" charset="utf-8" src="/js/ueditor/formdesign/leipi.formdesign.v4.js"></script>-->

<shiro:hasPermission name="save_Component">
<script type="text/javascript" src="${ctx}/js/ueditor/rebound.formdesign.v1.js?v=${vs}"></script>
</shiro:hasPermission>

<!-- script start-->  
<script type="text/javascript">
var healtaEditor;
$(document).ready(function(){

	$('#progress_dlg').dialog('open');
	UE.delEditor('ue_template');
	healtaEditor = UE.getEditor('ue_template',{
	            //allowDivTransToP: false,//阻止转换div 为p
	           // toolleipi:true,//是否显示，设计器的 toolbars   
	            //这里可以选择自己需要的工具按钮名称,此处仅选择如下五个
	           toolbars:[[
	        	   '|', 'undo', 'redo', '|','bold', 'italic', 'underline', 'fontborder', 'strikethrough',  'removeformat', '|', 'forecolor', 'backcolor', 
	        	   'insertorderedlist', 'insertunorderedlist','|', 'fontfamily', 'fontsize', '|', 'indent', '|', 'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify', '|',  
	        	   'horizontal',  'spechars',  'simpleupload', '|', 'inserttable', 'deletetable','insertparagraphbeforetable', 'insertrow', 'deleterow', 'insertcol', 'deletecol', 'mergecells', 'mergeright', 'mergedown', 'splittocells', 'splittorows', 'splittocols','|','source']]
	            //focus时自动清空初始化时的内容
	            //autoClearinitialContent:true,
	            ,lang:$.cookie("userLanguage").toLowerCase().replace('_','-')//"zh-cn"
	        	,langPath:"${ctx}/js/ueditor/lang/"
	            //关闭字数统计
	            ,wordCount:false
	            //关闭elementPath
	            ,elementPathEnabled:false
	            //默认的编辑区域高度
	            //,initialFrameHeight:1500
	            //,initialFrameWidth:800
	            //,minFrameWidth:800
	            ,autoHeightEnabled: false
	            //,autoFloatEnabled:true
	        	//浮动时工具栏距离浏览器顶部的高度，用于某些具有固定头部的页面
	        	//,topOffset:100
	        	//编辑器底部距离工具栏高度(如果参数大于等于编辑器高度，则设置无效)
	        	//,toolbarTopOffset:400
	            ,iframeCssUrl:"${ctx}/js/ueditor/themes/iframe.css" //引入自身 css使编辑器兼容你网站css
	            //更多其他参数，请参考ueditor.config.js中的配置项
	            //pageBreakTag
		        //分页标识符,默认是_ueditor_page_break_tag_
		        //,pageBreakTag:'_ueditor_page_break_tag_'
	        });
	$('#progress_dlg').dialog('open');
	healtaEditor.ready(function() {
		healtaEditor.setDisabled();
		//healtaEditor.setWidth(1024);
		healtaEditor.setHeight($('#ueditor_container_sr').height()-65);
		$('#progress_dlg').dialog('close');
	});

});


function resizeContainer(width,height){
	console.log("width:"+width+";height="+height);
	try{
		healtaEditor.setHeight(height-70);
	}
	catch(e){
		
	}
}

function codecolumnformatter(value, row, index){
	return "<a name='operate' href='#' class='easyui-linkbutton' onclick='selectStandardcode_("+index+")' >"+$.i18n.prop('admin.choose')+"</a>";
}

</script>

</body>
</html>