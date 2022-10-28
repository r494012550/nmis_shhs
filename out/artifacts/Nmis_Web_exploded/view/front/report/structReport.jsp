<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<script type="text/javascript" src="${ctx}/js/front/srCompHandle.js"></script>
<script type="text/javascript">
</script>

<!--  <style type="text/css">
</style>  -->

	<div class="easyui-layout" data-options="fit:true,border:false" >
		<div data-options="region:'south',border:false" style="height:32px;padding:2px;text-align:center;">
			
			<shiro:hasPermission name="save_report">
			<a class="easyui-linkbutton" id="savebtn_${reportid}" style="width:80px;height:28px" onclick="saveStructReport(${reportid},'${studyid}','${patientname}');">${sessionScope.locale.get("save")}</a>
			</shiro:hasPermission>
			
			<shiro:hasPermission name="submit_report">
			<a class="easyui-linkbutton" id="submitbtn_${reportid}" style="width:80px;height:28px" 
				onclick="submitStructReport(${reportid},'${studyid}','${patientname}');">提交</a>
			</shiro:hasPermission>
				
			<c:if test="${PreAudit}">
			<shiro:hasPermission name="preaudit_report">
			<a class="easyui-linkbutton" id="preaudit_${reportid}" style="width:80px;height:28px" onclick="auditPreStructReport(${reportid},'${studyid}','${patientname}');">初审</a>
			</shiro:hasPermission>
			</c:if>
			
			<shiro:hasPermission name="audit_report">
			<a class="easyui-linkbutton"  id="audibtn_${reportid}" style="width:80px;height:28px" onclick="auditStructReport(${reportid},'${studyid}','${patientname}');">${sessionScope.locale.get("report.audit")}</a>
			</shiro:hasPermission>
			
			
			<!-- 
			<a class="easyui-linkbutton" style="width:80px;height:28px" onclick="process('${studyid}');">检查流程</a> -->
			
			<shiro:hasPermission name="open_Image">
				<c:if test="${enable_plaza_callup==1}">
					<a class="easyui-linkbutton" href="${plaza_loaddata}" style="width:80px;height:28px" onclick="closeReportFlag=false;urlFlag = true;">${sessionScope.locale.get("wl.image")}(p)</a>
				</c:if>
				 <c:if test="${enable_via_callup==1}">
					<a class="easyui-linkbutton" href="${callviapara}" style="width:80px;height:28px" onclick="closeReportFlag=false;urlFlag = true;">${sessionScope.locale.get("wl.image")}(v)</a>
				</c:if>
			</shiro:hasPermission>
			<shiro:hasPermission name="print_report">
			<a class="easyui-menubutton" style="width:85px;height:28px" onClick="printReport('${projecturl}',${reportid},'',1, -1);"
				data-options="plain:false,menu:'#printmenu_${reportid}'">${sessionScope.locale.get("report.print")}</a>
			<div id="printmenu_${reportid}" style="width:150px;">
			    <div><a class="easyui-linkbutton" plain="true" onClick="printReport('${projecturl}',${reportid},'',1, -1);">${sessionScope.locale.get("report.print")}</a></div>
			    <div><a class="easyui-linkbutton" plain="true" onClick="previewReport(${reportid},false);">打印预览</a></div>
			</div>
			<a id="printApp_${reportid}" href="printApp:-1" type="hidden" ></a>
			</shiro:hasPermission>
			<shiro:hasPermission name="viewapply">
				<a class="easyui-linkbutton" style="width:85px;height:28px" onclick="apply(${studyorderfk},'worklist')">申请单</a>
			</shiro:hasPermission>
			<shiro:hasPermission name="switch_template">
			<a class="easyui-linkbutton" id="changetemplatebtn_${reportid}" style="width:125px;height:28px" onclick="openSwitchTemplateDialog(${reportid},${studyorderfk},'${studyid}');">${sessionScope.locale.get("report.switchtemplate")}</a>
			</shiro:hasPermission>

			<a class="easyui-menubutton" id="moremenu_${reportid}" style="width:85px;height:28px" data-options="plain:false,menu:'#moremenuitems_${reportid}'">更多...</a>
			<div id="moremenuitems_${reportid}" style="width:150px;">
				<div onclick="process(${studyorderfk},'worklist')">检查流程</div>
				<shiro:hasPermission name="favorites_report">
					<div id="showRe1_${reportid}" onclick="openFavoritesDialog(${reportid},${studyorderfk})">${sessionScope.locale.get("wl.favoritesreport")}</div>
				</shiro:hasPermission>
				<div id="importbtn_${reportid}" data-options="disabled:true" onclick="manualImportViaDataToReport(${reportid},'${studyid}');">${sessionScope.locale.get("report.import")}</div>
				<div id="normalreportbtn_${reportid}" onclick="normalReport(${reportid},${studyorderfk},'${studyid}');">${sessionScope.locale.get("report.normalreport")}</div>
				<shiro:hasPermission name="reject_report">
					<div id="reject_${reportid}" onclick="rejectStructReport(${reportid},'${patientname}','${reportStatus}')">驳回</div>
				</shiro:hasPermission>
			</div>
			
			<a class="easyui-linkbutton" style="width:80px;height:28px" onclick="closeTab(${reportid});">${sessionScope.locale.get("report.close")}</a>
		</div>
 	    <!-- 结构化模板填充位置 -->
 	    	<div data-options="region:'center',border:false">
				<div class="easyui-layout" data-options="fit:true,border:false">
			        <div data-options="region:'west',hideCollapsedContent:false" title="syngo.via" style="width:250px;">
			        
				        <div class="easyui-layout" data-options="fit:true,border:false">
						    <div data-options="region:'north',title:'${sessionScope.locale.get('report.finding')}',border:false,collapsible:false,split:true,tools:[{
						        iconCls:'icon-pdf1',
						        handler:function(){openDialog_SyngoviaPdf(${reportid},'${studyid}')}
						    }]" style="height:300px;">
						    	<table class="easyui-datagrid" data-options="fit:true,border:false,fitColumns:false,singleSelect:true,url:'${ctx}/srreport/getFinding?studyid=${studyid}&studyinsuid=${studyinsuid}',
							    	<%-- onDblClickRow:function(index,row){insertFindingToSRReport(index,row,'${studyid}')}, --%>
							    	onLoadSuccess:function(data){automaticImportViaDataToReport(data,${reportid},'${studyid}');enableImportBtn(data,${reportid});}" 
							    	id="via_findings_${reportid}">
							    	<thead>
							            <tr>
							                <th data-options="field:'findingname',width:150">${sessionScope.locale.get('report.name')}</th>
							                <th data-options="field:'displayvalue',width:50">${sessionScope.locale.get('report.value')}</th>
							                <th data-options="field:'source',width:50">${sessionScope.locale.get('report.source')}</th>
							            </tr>
							        </thead>
							    </table>
						    
						    </div>
						    <div data-options="region:'center',title:'${sessionScope.locale.get('wl.image')}',border:false,tools:'#syngoviaimgs_tools_${reportid}'">
						    	<table class="easyui-datagrid" id="via_images_${reportid}"
						            data-options="showHeader:false,fit:true,border:false,fitColumns:true,singleSelect:true,view:cardview,
						            url:'${ctx}/srreport/getFindingImages?studyid=${studyid}&studyinsuid=${studyinsuid}'">
							        <thead>
							            <tr>
							                <th data-options="field:'imageid',align:'center'">Item ID</th>
							            </tr>
							        </thead>
			    				</table>
			    				
			    				<div id="syngoviaimgs_tools_${reportid}" style="padding:2px 5px;">
									<a class="easyui-tooltip" title="刷新" onclick="$('#via_images_${reportid}').datagrid('reload');">
										<i class="icon iconfont icon-shuaxin2" style="text-align: center;vertical-align:2px;"></i></a>
							    	<a class="easyui-tooltip" title="导入" onclick="importSnapshot(${reportid});">
							    		<i class="icon iconfont icon-chakandaorujilu" style="font-size:15px;vertical-align:2px;text-align: center;"></i></a>
							    </div>
						    </div>
						</div>
			        </div>
			        <div data-options="region:'east',hideCollapsedContent:false,href:'${ctx}/report/report_Assistant_Panel?studyid=${studyid}&orderid=${studyorderfk}&modality=${modality}&patientidfk=${patientidfk}&reportid=${reportid}',
			        		collapsed:${report_assistant_collapsed==1?true:false},
          					onExpand:function(){saveUserProfiles_value('0','report_assistant_collapsed')},
           					onCollapse:function(){saveUserProfiles_value('1','report_assistant_collapsed')}" 
			        	style="width:310px;" title="${sessionScope.locale.get("report.reportassistantpanel")}">
			        
				        
			        </div>
			        <div data-options="region:'center'" style="border-top-width: 0px;">
			        	<div class="easyui-layout" id="sr_content_${reportid}" data-options="fit:true">
					        <div data-options="region:'west',border:false,tools:'#catalog_tool_${reportid}',hideCollapsedContent:false,collapsed:true" style="width:200px;" title="报告目录">
					        	<div class="easyui-datalist" id="catalog_${reportid}" data-options="fit:true,lines:true,
						        	border:false,textField:'title',rowStyler:catalogStyler
						        	,onSelect:function(index,row){
						        		catalog_select_handle(index,row,${reportid});
						        	}">
								</div>
								 <div id="catalog_tool_${reportid}">
								     <a class="easyui-tooltip" title="全部显示" onclick="selectAllCatalog(${reportid});">
										<i class="icon iconfont icon-quanxuan2" style="text-align: center;vertical-align:2px;"></i></a>
									<a class="easyui-tooltip toggle:true" title="显示/隐藏标题章节" onclick="showHeaderSection(${reportid});">
										<i class="icon iconfont icon-xuanzhong" style="text-align: center;vertical-align:2px;"></i></a>
								 </div>
					        </div>
					        <div data-options="region:'center'"  id="sr_div_${reportid}" style="border-top-width: 0px;border-bottom-width: 0px;border-right-width: 0px;">
					        	<div class="mydiv" style="padding:5px;margin-left:auto;margin-right:auto;width:720px;">
			        				<form id="sr_form_${reportid}" onsubmit="return false;">
			        				<div id="sr_container_${reportid}" class="easyui-panel sr_container" data-options="border:false,
			        					onOpen:function(){
			        						pluginHandle.addListenerOnSRContainer($(this));
			        						generatedCatalog($(this),${reportStatus});
			        						reg_formula_dom($(this));
			        					}" style="background-color:#FFFFFF;" reportid="${reportid}" resetCatalog="true"></div>
			        					<input id="sr_submit_${reportid}" type="submit" style="display:none" />
			        				</form>
			        			</div> 
					        </div>
					    </div>
			        </div>
			    </div>
			</div>
			
			
       </div>
       
       <div style="display:none" id="tips_div_toolbar_${reportid}">
	        <div id="tips_toolbar_${reportid}">
	       		<a href="#" class="easyui-linkbutton easyui-tooltip" title="提取内容" 
	       			data-options="plain:true" onclick='extract_Summary();'><i class='iconfont icon-info'></i>提取内容</a>
	        </div>
	    </div>
        <!-- 切换模板窗口 
        <div id="choose_Templet" class="easyui-window" title="切换模板" style="width:500px;height:350px" data-options="modal:true,closed:true,collapsible:false,minimizable:false,maximizable:false, draggable:false,resizable:false,onBeforeOpen:function(){initTempletsTree();},">
        	<input id='templetName' type='text' placeholder='模糊查询模板名称' style="width:100%;height:28px;" value='' onchange='reloadTree()'/>
        	<div style='width:100%;height:200px;' class='easyui-panel' data-options='title:"结构化模板列表"' >
	        	<ul id='templets'></ul>
				
        	</div>
        	<div align='center' style='margin-top:20px'>
        	<a class="easyui-linkbutton" style="width:180px;height:32px" onclick="useDefault('${patientname}','${studyid}');">使用默认模板</a>
			<a class="easyui-linkbutton" style="width:180px;height:32px" onclick="useStruct('${patientname}','${studyid}');">使用选中模板</a>
        	</div>
        </div>-->
          <!-- 申请单查看窗口 -->
        <div class="gallerys" style='display:none'>
			<img id="imageShow_${reportid}" src="" class="gallery-pic" onclick="$.openPhotoGallery(this)"/>
		</div>
		
		<div id="viapdfdlg_${reportid}" class="easyui-dialog" data-options="resizable:true,border:'thin',doSize:true" title="${sessionScope.locale.get('report.syngoviasr')}" style="width:700px;height:720px;padding:5px;" closed="true">
	        <div id="pdf-container_${reportid}" style="width:100%;height:100%"></div>
	        
	    </div>
       
       <!--<script type="text/javascript" src="/js/front/report.js"></script>
       <script type="text/javascript" src="/js/front/worklist.js"></script>-->
		<input id='srtemplateid_${reportid}' type='hidden' value='${srtemplateid}'/>
		<input id="srtemplatename_${reportid}" type="hidden" value="${srtemplatename}"/>
		<input id='filter_width_${reportid}' type='hidden' value='${filter_width}'/>
		<input id="orderStatus_${reportid}" type="hidden" value="${reportStatus}">
		<input id="id_${reportid}" name="reportid" type="hidden" value="${reportid}">
		<input id="orderid_${reportid}" name="studyorderfk" type="hidden" value="${studyorderfk}">
		<input id="studyid_${reportid}" name="studyid" type="hidden" value="${studyid}">
		<input id="studyinsuid_${reportid}" name="studyinsuid" type="hidden" value="${studyinsuid}">
		<input id="studyitem_${reportid}" type="hidden" value="${studyitem}">
		<input id='contentChangeflag_${reportid}' type='hidden'/>
		<input id="viareportid_${reportid}" name="viareportid" type="hidden" value="${viareportid}">
		<input id="patientid_${reportid}" type="hidden" value="${patientid}">
       	<div style="display: none;" id="temp_${reportid}"></div>
       	<div style="display:none">
	        <div id="toolbar_srreport_for_summary">
	            <a href="#" class="easyui-linkbutton easyui-tooltip" title="提取" data-options="iconCls:'icon-add',plain:true" onclick="extract_Summary();"></a>
	        </div>
        </div>
       