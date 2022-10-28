<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div class="easyui-layout" data-options="fit:true">
		<div data-options="region:'west',hideCollapsedContent:false,href:'${ctx}/webview/west_search',onLoad:initFieldEvent" style="width:280px;">

		</div>
		
		<div data-options="region:'center',title:'${sessionScope.locale.get("wl.searchresult")}'">
			<table id="dg" class="easyui-datagrid" style="width:100%;" sortName="studyorderstudyid" sortOrder="asc" border="0"
				data-options="rownumbers: true,showFooter: true,singleSelect:true,pagination:true,rownumbers:true, toolbar:'#toolbar_div',fit:true,
				remoteSort:false,autoRowHeight:false,
				loadMsg:'${sessionScope.locale.get('loading')}...',emptyMsg:'${sessionScope.locale.get('nosearchresults')}',
				rowStyler: function(index,row){
	                
	            },
	            onClickRow:function(index,row){
	            	clickDgRow(index,row);
	            },
	            onRowContextMenu:function(e,index ,row){
                    e.preventDefault();
                    if(row){
                        $('#callupmenu_plaza').attr('href','${plaza_loaddata} '+(row.studyorderstudyid));
                        $('#callupmenu_via').attr('href','${callviapara}'+(row.studyorderstudyid));
                    }
                    $(this).datagrid('selectRow',index);
                    $('#cmenu_wl').menu('show', {
                        left:e.pageX,
                        top:e.pageY
                    });
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
                
                <thead data-options="frozen:true">
		            <tr>
		                <th data-options="field:'orderstatus',width:80,styler:columeStyler_orderstatus_wl" sortable="true">${sessionScope.locale.get("wl.orderstatus")}</th>
						<th data-options="field:'reportstatusdisplay',width:80,formatter:reportstatus_formatter,styler:columeStyler_reportstatus_wl" sortable="true">${sessionScope.locale.get("wl.reportstatus")}</th>
		            </tr>
		        </thead>
                
				<thead>
					<tr>
						
						<th data-options="field:'studyorderstudyid',width:80" sortable="true">${sessionScope.locale.get("wl.studyid")}</th>
						<th data-options="field:'patientname',width:100" sortable="true">${sessionScope.locale.get("wl.patientname")}</th>
						<th data-options="field:'patientid',width:80" sortable="true">${sessionScope.locale.get("wl.patientid")}</th>
						<th data-options="field:'sexdisplay',width:50" sortable="true">${sessionScope.locale.get("wl.sex")}</th>
						<th data-options="field:'studydescription',width:200,align:'center'" sortable="true">${sessionScope.locale.get("wl.studydesc")}</th>
						<th data-options="field:'modality_type',width:80" sortable="true">${sessionScope.locale.get("wl.modality")}</th>
						<th data-options="field:'modalityname',width:120" sortable="true">${sessionScope.locale.get("wl.modalityname")}</th>
						<th data-options="field:'studyitems',width:200,align:'center'" sortable="true">${sessionScope.locale.get("wl.orderitem")}</th>
						<th data-options="field:'numberofstudyrelatedinstances',width:80" sortable="true">${sessionScope.locale.get("wl.imagenumber")}</th>
						<th data-options="field:'registertime',width:150,align:'center'" sortable="true">${sessionScope.locale.get("wl.registertime")}</th>
						<th data-options="field:'studydatetime',width:150,align:'center'" sortable="true">${sessionScope.locale.get("wl.studytime")}</th>
						<th data-options="field:'reporttime',width:150,align:'center'" sortable="true">${sessionScope.locale.get("wl.reporttime")}</th>
						<th data-options="field:'outno',width:80,align:'center'" sortable="true">${sessionScope.locale.get("wl.outno")}</th>
						<th data-options="field:'inno',width:80,align:'center'" sortable="true">${sessionScope.locale.get("wl.inno")}</th>
					</tr>
				</thead>
			</table>
			
			<div id="cmenu_wl" class="easyui-menu">
			    <div data-options="name:'report'" onClick="exportReport();">导出PDF</div>
		    </div>
		    <div id="toolbar_div" style="padding:2px 5px;">
		    	<shiro:hasPermission name="openreport">
		    	<a class="easyui-linkbutton" plain="true" onClick="openReport(null,0);" id="openreport_linkbutton"><i class="icon iconfont icon-baogao"></i>  ${sessionScope.locale.get("wl.report")}</a>
		    	</shiro:hasPermission>
		    	<shiro:hasPermission name="openimage">
		    	<a href='' id="callupbtn" class="easyui-linkbutton" plain="true" onClick="syngovia:123" style="margin-left: 10px;"><i class="icon iconfont icon-fangshe1"></i>  ${sessionScope.locale.get("wl.image")}</a>
		    	</shiro:hasPermission>
		    	<a class="easyui-linkbutton" plain="true" onClick="refresh()" style="margin-left: 10px;"><i class="icon iconfont icon-shuaxin2"></i>  ${sessionScope.locale.get("wl.refresh")}</a>
		    	<shiro:hasPermission name="deletereport">
		    	</shiro:hasPermission>
		    	<a class="easyui-linkbutton" plain="true" onclick="openApplyForm();" style="margin-left: 10px;"><i class="icon iconfont icon-icon-test"></i>  申请单</a>
		    	<a class="easyui-linkbutton" plain="true" onClick="var row=$('#dg').datagrid('getSelected');row?process(row.id):process(null);" style="margin-left: 10px;"><span class="iconfont icon-liuchengshuoming"></span>  检查流程</a>
		    	
		    	<input type="hidden" id="dbflag"> 
		    	<input type="hidden" id="page"> 
		    	<input type="hidden" id="pageSize"> 
		    </div>
		    <%-- <div id="footer_div" style="padding:2px 5px;">
		    	<jsp:include page="footer.jsp"/>
		    </div> --%>
		    <div id="saveSearch"></div>
		    <div id="paramsManage"></div>
		    
		    <div id="history"></div>
			    <div class="gallerys">
						<img id="imageShow" src="" class="gallery-pic" onclick="$.openPhotoGallery(this)"/>
				</div>
		</div>
	</div>

</body>
</html>