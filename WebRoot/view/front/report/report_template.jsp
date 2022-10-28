<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div id="report_tt" class="easyui-tabs" data-options="fit:true,plain:true,justified:true,narrow:true,border:false">
		<div title="树型模板" style="display:none;">
	       	<div class="easyui-layout" data-options="fit:true" border="0">
		       <div data-options="region:'center'" style="height:100%;" border="0">
			       
			        <div id="tree_tool_${reportid}" style="padding:2px 2px;">
					   <!--a class="easyui-linkbutton easyui-tooltip" data-options="plain:true" onclick="addnode('<%=request.getParameter("studyid")%>');" title="添加节点"><i class="icon iconfont icon-plus4"></i></a-->
					   <a id="addchildnode1_${reportid}" class="easyui-linkbutton easyui-tooltip" data-options="plain:true" disabled="true" onclick="addchildnode(${reportid});" 
					   		title="${sessionScope.locale.get("report.addchildnode")}"><i class="icon iconfont icon-tianjia"></i></a>
					   <a id="addTemplateContent1_${reportid}" class="easyui-linkbutton easyui-tooltip" data-options="plain:true" disabled="true" onclick="addTemplateContent(${reportid});" 
							   		title="${sessionScope.locale.get("report.addtemplate")}"><i class="icon iconfont icon-plus2"></i></a>
					   <a id="modifynode1_${reportid}" class="easyui-linkbutton easyui-tooltip" data-options="plain:true" disabled="true" onclick="modifynode(${reportid});" 
					   		title="${sessionScope.locale.get("report.modifynode")}"><i class="icon iconfont icon-edit"></i></a>
					   <%-- <a class="easyui-linkbutton easyui-tooltip" data-options="plain:true" onclick="modifyTemplateContent('<%=request.getParameter("studyid")%>');" 
							   		title="${sessionScope.locale.get("report.modifytemplate")}"><i class="icon iconfont icon-edit"></i></a> --%>
					   <a id="deletenode1_${reportid}" class="easyui-linkbutton easyui-tooltip" data-options="plain:true" disabled="true" onclick="deletenode(${reportid});" 
					   		title="${sessionScope.locale.get("delete")}"><i class="icon iconfont icon-delete"></i></a>
					   <a id="previewTemplateNew1_${reportid}" class="easyui-linkbutton easyui-tooltip" data-options="plain:true" disabled="true" onclick="previewTemplateNew(${reportid});" 
							   	title="${sessionScope.locale.get("report.previewtemplate")}"><i class="icon iconfont icon-info"></i></a>
				    </div>
				    <div class="easyui-datalist" style="width:100%;height:30px;" border="0" data-options="toolbar:'#tree_tool_${reportid}'"></div>
	       			<div>
	       			     <input class="easyui-searchbox" name="eee" style="width:99%" data-options="prompt:'请输入你需要查询的模板名称...',
	       			     searcher:function(value,name){
	       			         searchTemplate(value,name,'${modality}','${reportid}');
	       			     }" >
	       			</div>
	       			<ul id="nodetree_${reportid}" class="easyui-tree" data-options="url:'${ctx}/template/getTemplateNodeByModality?modality=${modality}&creator=1',method:'get',
	       			    dnd:'true',
		       			onContextMenu: function(e,node){
		                    e.preventDefault();
		                    $(this).tree('select',node.target);
		                    enableTemplateMenu(node,${reportid});
		                    $('#mm_${reportid}').menu('show',{
		                        left: e.pageX,
		                        top: e.pageY
		                    });
		                },
		                onAfterEdit:function(node){
		                	submitMidifynode(node,${reportid});
		                },
		                onClick:function(node){
		                	$(this).tree('toggle',node.target);
		                },
		                onDblClick:function(node){
		                	applyTemplate_new(node,${reportid});
		                },
		                onSelect:function(node){
		                	selectTemplateNode(node,${reportid});
		                },
		                onBeforeDrop:function(target, source, point){
		                   var targetNode = $(this).tree('getNode', target);
	                       if(targetNode.type == 'template'){
						       return false;
						   }
						   if(targetNode.ispublic == '1' && !$('#editpublicnode').val()){
						       return false;
						   }
						   var flag=onBeforeDropNode(target,source,point,'${reportid}');
						   if(flag=='1'){
						   	   return false;
						   }
		                },
		                onBeforeDrag:function(node){
		                   return onBeforeDragNode(node,'${reportid}');
		                },
		                onDrop:function(target, source, point){
		                },
		                formatter:function(node){
	                        var s = node.text;
	                        if (node.ispublic=='0'){
	                            s = '<span style=\'color:blue\'>' + s + '</span>';
	                        }
	                        return s;
	                    }">
		         	</ul>
		         	
		         	<input type="hidden" id="parentNode_${reportid}">
		         	<input type="hidden" id="dialog_${reportid}" value="0">
					<input type="hidden" id="userid" value="${userid}">
		       		
		       		<div id="mm_${reportid}" class="easyui-menu" style="width:120px;">
				        <div data-options="name:'addchildnode2_${reportid}'" onclick="addchildnode(${reportid});">${sessionScope.locale.get("report.addchildnode")}</div>
				        <div data-options="name:'addTemplateContent2_${reportid}'" onclick="addTemplateContent(${reportid});">${sessionScope.locale.get("report.addtemplate")}</div>
				        <div class="menu-sep"></div>
				        <div data-options="name:'modifynode2_${reportid}'" onclick="modifynode(${reportid});">修改模板</div>
				        <div data-options="name:'deletenode2_${reportid}'" onclick="deletenode(${reportid});">${sessionScope.locale.get("delete")}</div>
				        <div class="menu-sep"></div>
				        <div data-options="name:'previewTemplateNew2_${reportid}'" onclick="previewTemplateNew(${reportid});">${sessionScope.locale.get("report.previewtemplate")}</div>
				    </div>
					    
		       </div>
		       <%-- <div data-options="region:'center',border:false" style="height:20%;" border="0" title="${sessionScope.locale.get('report.template')}">
		       		<div class="easyui-layout" data-options="fit:true" border="0">
			       		<div data-options="region:'north',hideCollapsedContent:false" style="height:100%;" border="0"  title="${sessionScope.locale.get('report.template')}">
			       			<div id="templist_${studyid}" class="easyui-datalist" border="0"
			       				data-options="fit:true,textField:'name',valueField:'id',toolbar:'#templist_tool_${studyid}',
			       								onDblClickRow:function(index,row){applyTemplate(row,'${studyid}')}">
			       				
						    </div>
						    
						    <div id="templist_tool_<%=request.getParameter("studyid")%>" style="padding:2px 2px;">
							   <!--a class="easyui-linkbutton easyui-tooltip" data-options="plain:true" onclick="addnode('<%=request.getParameter("studyid")%>');" title="添加节点"><i class="icon iconfont icon-plus4"></i></a-->
							   <a class="easyui-linkbutton easyui-tooltip" data-options="plain:true" onclick="addTemplateContent('<%=request.getParameter("studyid")%>');" 
							   		title="${sessionScope.locale.get("report.addtemplate")}"><i class="icon iconfont icon-plus2"></i></a>
							   <a class="easyui-linkbutton easyui-tooltip" data-options="plain:true" onclick="modifyTemplateContent('<%=request.getParameter("studyid")%>');" 
							   		title="${sessionScope.locale.get("report.modifytemplate")}"><i class="icon iconfont icon-edit"></i></a>
							   <a class="easyui-linkbutton easyui-tooltip" data-options="plain:true" onclick="delTemplate('<%=request.getParameter("studyid")%>');" 
							   		title="${sessionScope.locale.get("report.deletetemplate")}"><i class="icon iconfont icon-delete"></i></a>
							   
						    </div>
						    
						    
			       		<!--<div data-options="region:'center',border:false" style="height:50%;" border="0" title="模板预览" >
			       			<div id="tempcontent_<%=request.getParameter("studyid")%>">
							    	
							</div>
			       		</div-->
		       		<!-- </div> -->
		       </div> --%>
		       
		       <%-- <a id="preview<%=request.getParameter("studyid")%>" href="javascript:void(0)" hidden="true">Click here</a> --%>
		       <a id="preview${reportid}" href="javascript:void(0)" hidden="true">Click here</a>
		
	       		<div id="model_windows_${reportid}" class="easyui-window" title="${sessionScope.locale.get('report.addchildnode')}" style="width:300px;height:170px"
					        data-options="modal:true,closed:true,collapsible:false,minimizable:false,maximizable:false,footer:'#classify_footer_${reportid}',border:'thin'">
					        
			        <form name="classifyForm_${reportid}" id="classifyForm_${reportid}" method="POST">
						<div style="padding:5px 5px 5px;margin-left:auto;margin-right:auto;width:250px;">
				            <div style="margin-bottom:5px">
				                <input id="classifyName_${reportid}" class="easyui-textbox" label="${sessionScope.locale.get('report.nodename')}：" labelPosition="top" 
				                data-options="prompt:'${sessionScope.locale.get('report.inputnodename')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}',
											validType:{
												length:[1,50]
											}" name="nodename" style="width:100%;height:60px;">
				            </div>
						</div>
						
					</form>
				
				</div>
	       		<div id="classify_footer_${reportid}" style="text-align:center;padding:5px;">
		            <a class="easyui-linkbutton" style="width:80px;height:30px" onclick="saveclassifynode(${reportid});">${sessionScope.locale.get("save")}</a>
					<a class="easyui-linkbutton" style="width:80px;height:30px" onclick="closeclassifynode(${reportid});">${sessionScope.locale.get("cancel")}</a>
	   		 	</div>
		      
			</div>
				<div id="common_dialog_template_${reportid}"></div>
			</div>
			<%-- <div title="人体图 ">
				<div class="easyui-layout"  data-options="fit:true" border="0">
					<div data-options="region:'north',collapsible:false,border:false" style="height:17%;padding:2px;border-right:0px;">
						<div style="padding-left:3px;padding-top:3px;">
								<input id="ycbw_${reportid}" class="easyui-textbox" label="异常部位:" labelWidth="80px" labelPosition="left" style="width:280px;height:45px;"
									data-options="multiline:true, editable:false"/>
						</div>
						<div style="padding-left:3px;padding-top:5px;">
							<a class="easyui-linkbutton" style="width:80px;height:20px"  onclick="extract(${reportid},${examination_position},'${sex}');">提取</a>
							<a class="easyui-linkbutton" style="width:80px;height:20px"  onclick="clearycbw(${reportid});">清空</a>
						</div>
					</div>
					<div data-options="region:'south',title:'',collapsible:false" style="height:83%;padding:2px;border-right:0px;border-bottom-width: 0px;">
						<div style="margin-left:auto;margin-right:auto;width: 230px;height:620px;">
							<%@ include file="/view/front/examine/mannequin.jsp"%>
						</div>
					</div>
				</div>
				</div> --%>
			<div title="片语" style="border:0;">
	<div id="phrasenode_tool_${reportid}" style="padding:2px 2px;">
		   <a id="addPhrasenode_${reportid}" class="easyui-linkbutton easyui-tooltip" data-options="plain:true" onclick="goToAddPhrasenode(${reportid});" 
		   		title="${sessionScope.locale.get("report.addchildnode")}"><i class="icon iconfont icon-tianjia"></i></a>
		   <a id="addPhraseContent_${reportid}" class="easyui-linkbutton easyui-tooltip" data-options="plain:true" onclick="goToAddPhraseContent(${reportid});" 
				   		title="添加片语"><i class="icon iconfont icon-plus2"></i></a>
		   <a id="modifynode1_${reportid}" class="easyui-linkbutton easyui-tooltip" data-options="plain:true"   onclick="modifynodePhrase(${reportid});" 
		   		title="${sessionScope.locale.get("report.modifynode")}"><i class="icon iconfont icon-edit"></i></a>
		   <a id="deletePhrase_${reportid}" class="easyui-linkbutton easyui-tooltip" data-options="plain:true"   onclick="deletePhrase(${reportid});" 
		   		title="${sessionScope.locale.get("delete")}"><i class="icon iconfont icon-delete"></i></a>
		   <a id="previewPhrase_${reportid}" class="easyui-linkbutton easyui-tooltip" data-options="plain:true" onclick="previewPhrase(null, ${reportid});" 
				   	title="${sessionScope.locale.get("report.previewtemplate")}"><i class="icon iconfont icon-info"></i></a>
		</div>
		<div class="easyui-datalist" style="width:100%;height:30px;" border="0" data-options="toolbar:'#phrasenode_tool_${reportid}'"></div>
		<div>
		<input class="easyui-searchbox" name="eee" style="width:99%" 
			data-options="prompt:'请输入你需要查询的片语名称...',
	       			     searcher:function(value,name){searchPhrase(value,name,'${reportid}');}" >
	 	</div>
		<ul id="phrase_node_${reportid}" class="easyui-tree" 
			data-options="url:'${ctx}/report/getPersonalPhrase',
		                onClick:function(node){$(this).tree('toggle',node.target);},
		                onDblClick:function(node){previewPhrase(node,${reportid});}">
		</ul>
	</div>	
		</div>
</body>
</html>