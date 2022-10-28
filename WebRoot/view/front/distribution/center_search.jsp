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

		<div data-options="region:'west',hideCollapsedContent:false,href:'${ctx}/distribution/west_search',onLoad:initFieldEvent" style="width:280px;">

		</div>
		
		<div data-options="region:'center',border:false">
		<!-- 修改datagrid的singleSelect属性为false使其可以多选 -->
			<table id="dg" class="easyui-datagrid" style="width:100%;"
				data-options="rownumbers: true,showFooter: true,singleSelect:false,pagination:true,rownumbers:true, toolbar:'#toolbar_div',fit:true,
				remoteSort:false,autoRowHeight:false,border:false,footer:'#footer_div',
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
                onLoadError:function(){
                	alert('error');
                },
                view: detailview,
                detailFormatter:detailFormatter_wl_previewreport,
                onExpandRow:onExpandRow_wl_previewreport
                ">
                
                <thead data-options="frozen:true">
		            <tr>
		                <th data-options="field:'ck',checkbox:true"></th>
		            	<th data-options="field:'priority',width:30,formatter:priorityFormat,align:'center'" sortable="true"></th>
		            	<th data-options="field:'studyorderstudyid',width:140,styler:columeStyler_studyid" sortable="true">${sessionScope.locale.get("wl.studyid")}</th>
		            </tr>
		        </thead>
                
				<thead>
					<tr>
						<th data-options="field:'patientname',width:100" sortable="true">${sessionScope.locale.get("wl.patientname")}</th>
						<th data-options="field:'patientid',width:80" sortable="true">${sessionScope.locale.get("wl.patientid")}</th>
						<th data-options="field:'sexdisplay',width:50" sortable="true">${sessionScope.locale.get("wl.sex")}</th>
						<th data-options="field:'reportprintcount',width:80,formatter:printcount_formatter_wl" sortable="true">打印状态</th>
		                <th data-options="field:'orderstatus',width:80,styler:columeStyler_orderstatus_wl" sortable="true">${sessionScope.locale.get("wl.orderstatus")}</th>
						<th data-options="field:'reportstatusdisplay',width:80,styler:columeStyler_reportstatus_wl" sortable="true">${sessionScope.locale.get("wl.reportstatus")}</th>
						<th data-options="field:'studyitems',width:200,align:'center'" sortable="true">${sessionScope.locale.get("wl.orderitem")}</th>
						<th data-options="field:'modality_type',width:80" sortable="true">${sessionScope.locale.get("wl.modality")}</th>
						<th data-options="field:'modalityname',width:120" sortable="true">${sessionScope.locale.get("wl.modalityname")}</th>
						<th data-options="field:'audittime',width:150,align:'center'" sortable="true">审核时间</th>
						<th data-options="field:'regdatetime',width:150,align:'center'" sortable="true">${sessionScope.locale.get("wl.registertime")}</th>
						<th data-options="field:'studydatetime',width:150,align:'center'" sortable="true">${sessionScope.locale.get("wl.studytime")}</th>
						<th data-options="field:'reporttime',width:150,align:'center'" sortable="true">${sessionScope.locale.get("wl.reporttime")}</th>

						<%-- <th data-options="field:'outno',width:80,align:'center'" sortable="true">${sessionScope.locale.get("wl.outno")}</th>
						<th data-options="field:'inno',width:80,align:'center'" sortable="true">${sessionScope.locale.get("wl.inno")}</th> --%>
                        <th data-options="field:'filmnum',width:80" sortable="true">${sessionScope.locale.get("wl.filmnum")}</th>
                        <th data-options="field:'numberofstudyrelatedinstances',width:80" sortable="true">${sessionScope.locale.get("wl.imagenumber")}</th>
						<th data-options="field:'studydescription',width:200,align:'center'" sortable="true">${sessionScope.locale.get("wl.studydesc")}</th>
						
					</tr>
				</thead>
			</table>
			
			<div id="cmenu_wl" class="easyui-menu">
			 	<div data-options="name:'report'" onClick="batchPrintReport();">打印报告</div>
			 	<div onClick="print('0',-1);">打印预览</div>
			 	<div onclick="showprogressbar()">显示进度条</div>
			 	<a id="printReport_worklist" href="tool:-1" type="hidden" ></a>
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
			    <a class="easyui-linkbutton" plain="true" onClick="batchPrintReport();" style="margin-left: 10px;" id="reportprint_linkbutton" ><i class="iconfont icon-print1"></i>&nbsp;打印</a>
		    	<input type="hidden" id="dbflag"> 
		    	<input type="hidden" id="page"> 
		    	<input type="hidden" id="pageSize">
		    </div>
		    <div id="footer_div" style="padding:2px 5px;">
		    	<jsp:include page="footer.jsp"/>
		    </div>
		    <div id="paramsManage"></div>
		</div>
	</div>
<input id="checkreportuserid_dis" value="${user_id+990000}" type="hidden">
</body>
</html>