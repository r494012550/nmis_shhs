<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	
<div class="easyui-layout" data-options="fit:true" id="worklist_layout_id">
		<!-- <div data-options="region:'east',hideCollapsedContent:false" title="预览报告" style="width:200px;padding:5px;">
			<div id="reportcontent"></div>
		</div> -->
	<div data-options="region:'west',hideCollapsedContent:false,href:'${ctx}/worklist/west_search',onLoad:initFieldEvent" style="width:280px;">

	</div>
		
	<div data-options="region:'center',border:false">	
		<div class="easyui-layout" style="width:100%;height:100%;">
		<div data-options="region:'center',border:false">
			<table id="dg" class="easyui-datagrid" style="width:100%;"
				data-options="rownumbers: true,showFooter: true,singleSelect:true,pagination:true,rownumbers:true, toolbar:'#toolbar_div',fit:true,
				remoteSort:false,autoRowHeight:false,border:false,
				loadMsg:'${sessionScope.locale.get('loading')}...',emptyMsg:'${sessionScope.locale.get('nosearchresults')}',
				rowStyler: function(index,row){
	                   
	               },
	            onRowContextMenu:function(e,index ,row){
                    e.preventDefault();
                    $(this).datagrid('selectRow',index);
                    $('#cmenu_wl').menu('show', {
                        left:e.pageX,
                        top:e.pageY
                    });
                },
                onHeaderContextMenu: function (e, field) {
          			e.preventDefault();
          			$(this).datagrid('columnMenu').menu('show', {
            		left: e.pageX,
            		top: e.pageY
          			});
        		},
        		onClickRow:function(index,row){
	            	
	            	clickDgRow(index,row);
	            },
                onDblClickRow:function(index,row){
                	$(this).datagrid('selectRow',index);
                	openReport(null,'${openimageatonce}');
					
                },
                onLoadError:function(){
                	alert('error');
                },
                view: detailview,
                detailFormatter:detailFormatter_wl_previewreport,
                onExpandRow:onExpandRow_wl_previewreport
                ">
                
             <%--    <thead data-options="frozen:true">
		            <tr>
		            	<th data-options="field:'priority',width:30,formatter:priorityFormat,align:'center'" sortable="true"></th>
		            	<th data-options="field:'studyorderstudyid',width:140,styler:columeStyler_studyid" sortable="true">${sessionScope.locale.get("wl.studyid")}</th>
		                <th data-options="field:'orderstatus',width:80,styler:columeStyler_orderstatus_wl" sortable="true">${sessionScope.locale.get("wl.orderstatus")}</th>
						<th data-options="field:'reportstatusdisplay',width:80,styler:columeStyler_reportstatus_wl" sortable="true">${sessionScope.locale.get("wl.reportstatus")}</th>
						<!-- <th data-options="field:'locking',width:80" sortable="true">锁定状态</th> -->
		            </tr>
		        </thead> --%>
                
				<thead>
					<tr>
						<th data-options="field:'priority',width:30,formatter:priorityFormat,align:'center'" sortable="true"></th>
		            	<th data-options="field:'studyorderstudyid',width:140,styler:columeStyler_studyid" sortable="true">${sessionScope.locale.get("wl.studyid")}</th>
		                <th data-options="field:'orderstatus',width:80,styler:columeStyler_orderstatus_wl" sortable="true">${sessionScope.locale.get("wl.orderstatus")}</th>
						<th data-options="field:'reportstatusdisplay',width:80,styler:columeStyler_reportstatus_wl" sortable="true">${sessionScope.locale.get("wl.reportstatus")}</th>
						<th data-options="field:'lockingpeople',width:70,styler:rowStyler_lockingpeople_wl" sortable="true">${sessionScope.locale.get("wl.locked")}</th>
						<th data-options="field:'patientname',width:100,formatter:columeFormatter_patientname_reg" sortable="true">${sessionScope.locale.get("wl.patientname")}</th>
						<th data-options="field:'patientid',width:80" sortable="true">${sessionScope.locale.get("wl.patientid")}</th>
						<th data-options="field:'sexdisplay',width:50" sortable="true">${sessionScope.locale.get("wl.sex")}</th>
						<th data-options="field:'idnumber',width:120" sortable="true">身份证号</th>
						<th data-options="field:'appdeptname',align:'center'" sortable="true">申请科室</th>
						<th data-options="field:'address',align:'center'" sortable="true">地址</th>
						<th data-options="field:'age',width:50,formatter:age_formatter" sortable="true">年龄</th>
						<th data-options="field:'height',width:60" sortable="true">身高(cm)</th>
						<th data-options="field:'weight',width:60" sortable="true">体重(kg)</th>
						<th data-options="field:'studyitems',width:200,align:'center'" sortable="true">${sessionScope.locale.get("wl.orderitem")}</th>
						<th data-options="field:'modality_type',width:80" sortable="true">${sessionScope.locale.get("wl.modality")}</th>
						<th data-options="field:'modalityname',width:120" sortable="true">${sessionScope.locale.get("wl.modalityname")}</th>
						<th data-options="field:'numberofstudyrelatedinstances',width:80" sortable="true">${sessionScope.locale.get("wl.imagenumber")}</th>
						<th data-options="field:'regdatetime',width:150,align:'center'" sortable="true">${sessionScope.locale.get("wl.registertime")}</th>
						<th data-options="field:'studydatetime',width:150,align:'center'" sortable="true">${sessionScope.locale.get("wl.studytime")}</th>
						<th data-options="field:'reporttime',width:150,align:'center'" sortable="true">${sessionScope.locale.get("wl.reporttime")}</th>
						<th data-options="field:'reportphysicianname',width:150,align:'center'" sortable="true">报告医生</th>
						<th data-options="field:'auditphysicianname',width:150,align:'center'" sortable="true">审核医生</th>
						<th data-options="field:'audittime',width:150,align:'center'" sortable="true">审核时间</th>
						<%-- <th data-options="field:'outno',width:80,align:'center'" sortable="true">${sessionScope.locale.get("wl.outno")}</th>
						<th data-options="field:'inno',width:80,align:'center'" sortable="true">${sessionScope.locale.get("wl.inno")}</th> --%>
						<th data-options="field:'reportprintcount',width:80,formatter:printcount_formatter_wl" sortable="true">报告打印</th>
						<th data-options="field:'studydescription',width:200,align:'center'" sortable="true">${sessionScope.locale.get("wl.studydesc")}</th>
						<th data-options="field:'retrialviewcontent',width:200,align:'center'" sortable="true">复审意见</th>
						
						<!--th data-options="field:'age',width:50" sortable="true">年龄</th>
						<th data-options="field:'ageunitdisplay',width:65" sortable="true">年龄单位</th>
						
						<th data-options="field:'appointmenttime',width:120,align:'center'" sortable="true">预约日期</th>
						
						<th data-options="field:'wardno',align:'center',width:50" sortable="true">病区</th>
						<th data-options="field:'bedno',align:'center',width:50, hidden:true"" sortable="true">床号</th-->
					</tr>
				</thead>
			</table>
			
			<div id="cmenu_wl" class="easyui-menu">
				<shiro:hasPermission name="open_report">
			 	<div data-options="name:'report'" onClick="openReport(null,0);">${sessionScope.locale.get("wl.report")}</div>
			 	</shiro:hasPermission>
			 	<div data-options="name:'exportReport'" onClick="exportReport();">导出PDF</div>
			 	<div data-options="name:'printReport'" onClick="printReport();">打印报告</div>
			 	<div data-options="name:'returnReport'" onClick="returnReport();">退还报告</div>
			 	<a id="printReport_worklist" href="tool:-1" type="hidden" ></a>
			 	<c:if test="${sr_support}">
			 		<div data-options="name:'open'"><a>${sessionScope.locale.get("wl.open")}</a></div>
			 	</c:if>
			 	<shiro:hasPermission name="open_Image">
				 	<c:if test="${enable_plaza_callup==1}">
				 		<div data-options="name:'image'"> <a id="callupmenu_plaza" plain="true" onClick="openStudyImage('plazapara_worklist')">${sessionScope.locale.get("wl.image")}(p)</a></div>
				 	</c:if>
				 	<c:if test="${enable_via_callup==1}">
			        	<div data-options="name:'image'"> <a id="callupmenu_via" plain="true" onClick="openStudyImage('viapara_worklist')">${sessionScope.locale.get("wl.image")}(v)</a></div>
			        </c:if>
		        </shiro:hasPermission>
		        <div data-options="name:'refresh'" onClick="refresh()">${sessionScope.locale.get("wl.refresh")}</div>
		        <shiro:hasPermission name="delete_report">
		        <div data-options="name:'delete'" onClick="deleteSR()">${sessionScope.locale.get("wl.delete")}</div>
		        </shiro:hasPermission>
		         <div data-options="name:'studydatetime'" onClick="openStudydatetimeDlg()">修改检查完成时间</div>
				<div data-options="name:'apply'" onClick="openApplyForm()">申请单</div>
				<div data-options="name:'process'" onClick="var row=$('#dg').datagrid('getSelected');row?process(row.orderid,'worklist'):process(null);">检查流程</div>					        
		        <!-- <div > <a class="easyui-linkbutton" plain="true" onClick="process();"><i class="icon iconfont icon-plus4"></i>  检查流程</a></div>
		        <div > <a class="easyui-linkbutton" plain="true" onClick="details();"><i class="icon iconfont icon-plus4"></i>  详情信息</a> </div-->
		        <shiro:hasPermission name="cancel_audireport">
		        <div data-options="name:'cancelaudireport'" onClick="cancelAudiReport();">${sessionScope.locale.get("wl.cancelaudit")}</div>
		        </shiro:hasPermission>
		        <shiro:hasPermission name="cancel_blockreport">
		        <div data-options="name:'cancellock'" onClick="cancelLock();">${sessionScope.locale.get("wl.unlock")}</div>
		        </shiro:hasPermission>
		      	<div data-options="name:'restore_defaults'" onClick="restoreDefaults('worklist');">恢复初始设置</div>
		    </div>
		    <div id="toolbar_div" style="padding:1px 5px;border:0px;">
		    
		    	<c:if test="${quicksearch}">
			        <input type="text" id="quicksearch-input" name="search" class="easyui-searchbox" title="搜索内" 
			        	data-options="prompt:'请输入您要查询的信息',searcher:quickSearch,width:300,menu:'#quicksearch_mm'">
	                <div id="quicksearch_mm">
				        <div data-options="name:'all'">全部</div>
				        <div data-options="name:'join'">关联</div>
				    </div>
	                <i class="icon iconfont icon-info easyui-tooltip" style="font-size:13px;" title="搜索内容后加  * 查询，搜索出的内容为包含 此内容的全部数据。如：搜索 '李*'，
	                                        搜索出的数据将是所有开头为 '李' 的数据"></i>
                </c:if>
			    	<shiro:hasPermission name="open_report">
			    	<a class="easyui-linkbutton" plain="true" onClick="openReport(null,0);" id="openreport_linkbutton"><i class="icon iconfont icon-baogao"></i>  ${sessionScope.locale.get("wl.report")}</a>
			    	</shiro:hasPermission>
			    	<shiro:hasPermission name="open_Image">
				    	<c:if test="${enable_plaza_callup==1}">
							<a id="callupbtn_plaza" class="easyui-linkbutton" plain="true" onClick="openStudyImage('plazapara_worklist')" style="margin-left: 10px;">
			    				<i class="icon iconfont icon-fangshe1"></i>  ${sessionScope.locale.get("wl.image")}(p)</a>
						</c:if>
			    		<c:if test="${enable_via_callup==1}">
				    		<a id="callupbtn_via" class="easyui-linkbutton" plain="true" onClick="openStudyImage('viapara_worklist')" style="margin-left: 10px;">
				    			<i class="icon iconfont icon-fangshe1"></i>  ${sessionScope.locale.get("wl.image")}(v)</a>
			    		</c:if>
			    		<input id="plazapara_worklist" value="${plaza_loaddata} " type="hidden"/>
			    		<input id="viapara_worklist" value="${callviapara}" type="hidden"/>
			    		<a id="image_worklist" href="tool:-1" type="hidden" ></a>
			    	</shiro:hasPermission>
			    	<%-- <a class="easyui-linkbutton" plain="true" onClick="refresh()" style="margin-left: 10px;"><i class="icon iconfont icon-shuaxin2"></i>  ${sessionScope.locale.get("wl.refresh")}</a> --%>
			    	<shiro:hasPermission name="delete_report">
			    	<a class="easyui-linkbutton" plain="true" onClick="deleteSR()" title="删除报告"  style="margin-left: 10px;" id="delete_linkbutton"><i class="icon iconfont icon-delete"></i>  ${sessionScope.locale.get("wl.delete")}</a>
			    	</shiro:hasPermission>
			    	<a class="easyui-linkbutton" plain="true" onclick="openApplyForm();" style="margin-left: 10px;"><i class="icon iconfont icon-icon-test"></i>  申请单</a>
			    	<a class="easyui-linkbutton" plain="true" onClick="var row=$('#dg').datagrid('getSelected');row?process(row.orderid,'worklist'):process(null);" style="margin-left: 10px;"><span class="iconfont icon-liuchengshuoming"></span>  检查流程</a>
			    	<a class="easyui-linkbutton" plain="true" onClick="printReport(null);" 
			    		style="margin-left: 10px;"><span class="iconfont icon-print1"></span>  打印报告</a>
			    	<a class="easyui-linkbutton" plain="true" onClick="openPrintImgDlg();" 
			    		style="margin-left: 10px;"><span class="iconfont icon-print1"></span>  打印图像</a>
			    	<!-- <a class="easyui-linkbutton" plain="true" onClick="printApp()"><i class="iconfont icon-print1"></i>打印报告</a> -->
			    	<c:if test="${sr_support}">
			    		<a class="easyui-linkbutton" plain="true" onClick="retrieveData();" style="margin-left: 10px;" id="retrieve_linkbutton" data-options="disabled:true"><i class="icon iconfont icon-import1"></i>  ${sessionScope.locale.get("wl.retrieve")}</a>
			    	</c:if>
			    	<!--td></td>
			    	<td><a class="easyui-linkbutton" plain="true" onClick="details();" style="margin-left: 10px;"><i class="icon iconfont icon-plus4"></i>  详情信息</a></td-->
		    	
		    	<input type="hidden" id="dbflag"> 
		    	<input type="hidden" id="page"> 
		    	<input type="hidden" id="pageSize">
		    </div>
		    <div id="footer_div" style="padding:2px 5px;">
		    	<jsp:include page="footer.jsp"/>
		    </div>
		    <div id="saveSearch"></div>
		    <div id="paramsManage"></div>
		    
		    <div id="history"></div>
			<div class="gallerys">
				<img id="imageShow" src="" class="gallery-pic" onclick="$.openPhotoGallery(this)"/>
			</div>
		</div>
		<div data-options="region:'south',split:false,collapsible:true,title:'历史检查' ,hideCollapsedContent:false,
		        collapsed:${report_historyreport_collapsed==1?true:false},
          		onExpand:function(){saveUserProfiles_value('0','report_historyreport_collapsed')},
           		onCollapse:function(){saveUserProfiles_value('1','report_historyreport_collapsed')}" style="height:300px;border:0">
			<div class="easyui-layout" style="width:100%;height:100%;">
				<div data-options="region:'center'" style="height:400px;border:0">
					<table id="historydg_wl" class="easyui-datagrid" style="width:100%;height:100%;border:0" data-options="fit:true,scrollbarSize:0,border:false,
						 singleSelect:true,onDblClickRow:function(index,row){$(this).datagrid('selectRow',index);showHistoryReport(row)}">
						<thead>
							<tr>
								<th data-options="field:'studydatetime',width:150,align:'center'" sortable="true">${sessionScope.locale.get("wl.studytime")}</th>
								<th data-options="field:'studyitems',width:150,align:'center'" sortable="true">${sessionScope.locale.get("wl.orderitem")}</th>
								<th data-options="field:'modality_type',width:80,align:'center'" sortable="true">${sessionScope.locale.get("wl.modality")}</th>
								<th data-options="field:'studyid',width:140,align:'center'">${sessionScope.locale.get("wl.studyid")}</th>
								<th data-options="field:'patientname',width:100,align:'center'">${sessionScope.locale.get("wl.patientname")}</th>
								<%-- <th data-options="field:'sexdisplay',width:50,align:'center'">${sessionScope.locale.get("wl.sex")}</th> --%>
								<th data-options="field:'patientsourcedisplay',width:100,align:'center'">${sessionScope.locale.get("wl.patientsource")}</th>
								<th data-options="field:'modalityname',width:120,align:'center'" sortable="true">${sessionScope.locale.get("wl.modalityname")}</th>
								<th data-options="field:'orderstatus',width:80">${sessionScope.locale.get("wl.orderstatus")}</th>
								<%-- <th data-options="field:'regdatetime',width:150,align:'center'" sortable="true">${sessionScope.locale.get("wl.registertime")}</th> --%>
							</tr>
						</thead>
					</table>
				</div>
				<div data-options="region:'east',split:false,collapsible:true" style="width:700px;height:100%;border:0">
					<div id="mypanel_wl" class="easyui-panel"  style="width:100%;height:100%;padding:10px;border-top-width:0px;">
		
					</div>
				</div>
			</div>	
		</div>
		</div>
	</div>
</div>
<input id="reportprintUserid_wl" value="${user_id + 990000}" type="hidden">
</body>
</html>