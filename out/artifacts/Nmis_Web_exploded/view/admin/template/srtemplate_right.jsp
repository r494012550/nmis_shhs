<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<div class="easyui-tabs" data-options="fit:true,plain:true,narrow:true" border="0">
    <div title="${showsection?'章节和组件':'组件'}" style="padding:0px">
    	<c:if test="${showsection}">
        <div id="toolbar_div_sr_temp" style="padding:2px;">
			<input class="easyui-searchbox"  data-options="prompt:'章节名称',
				searcher:function(value){$('#sections_dg_temp').datagrid({queryParams: {name: value}});}" style="width:100%;">
		</div>
		<table id="sections_dg_temp" style="width: 100%;height:50%;" class="easyui-datagrid" data-options="border:false,queryParams: {name:'' } ,
			url:'${ctx}/srtemplate/findSRSections',method:'get',fitColumns:true,striped:false,loadMsg:'${sessionScope.locale.get('loading')}...',toolbar:'#toolbar_div_sr_temp',
			  singleSelect:true,scrollbarSize:0,
			  emptyMsg:'${sessionScope.locale.get('nosearchresults')}...',
			  onRowContextMenu:function(e,index ,row){
                   e.preventDefault();
                   $(this).datagrid('selectRow',index);
                   $('#cmenu_sections_').menu('show', {
                       left:e.pageX,
                       top:e.pageY
                   });
	          },
			  onDblClickRow:function(index,row){
     			 insertSection(row,healtaEditor);
			  }">
						
			<thead>
	            <tr>
	                <th data-options="field:'name',width:200">章节名称</th>
	            </tr>
	        </thead>
		</table>
		<div id="cmenu_sections_" class="easyui-menu" style="width:260px;">
		 	<div onClick="deleteSection('sections_dg_temp');">${sessionScope.locale.get("delete")}</div>
	    </div>
	    </c:if>
        <div id="toolbar_div_sr" style="padding:2px;">
			<input class="easyui-searchbox"  data-options="prompt:'${sessionScope.locale.get('admin.componentname')}',
				searcher:function(value){$('#componentsandsections_dg').datagrid({queryParams: {name: value}});}" style="width:100%;">
		</div>
		<table id="componentsandsections_dg" style="width: 100%;height:${showsection?50:100}%;" class="easyui-datagrid" data-options="border:false,queryParams: {name:'' } ,
			url:'${ctx}/srtemplate/findSRComponent',method:'get',fitColumns:true,striped:false,loadMsg:'${sessionScope.locale.get('loading')}...',toolbar:'#toolbar_div_sr',
			  singleSelect:true,scrollbarSize:0,
			  emptyMsg:'${sessionScope.locale.get('nosearchresults')}...',
			  onDblClickRow:function(index,row){
     			 insertComponent(row,healtaEditor,true);
			  },
			  onRowContextMenu:function(e,index ,row){
                   e.preventDefault();
                   $(this).datagrid('selectRow',index);
                   $('#cmenu_templets').menu('show', {
                       left:e.pageX,
                       top:e.pageY
                   });
	          },
	          view:groupview,
              groupField:'group',
              groupFormatter:function(value,rows){
                return value + ' - ' + rows.length + ' ${sessionScope.locale.get('admin.items')}';
              }">
						
			<thead>
	            <tr>
	                <th data-options="field:'name',width:200">${sessionScope.locale.get('admin.componentname')}</th>
	            </tr>
	        </thead>
		</table>
		<div id="cmenu_templets" class="easyui-menu" style="width:260px;">
		 	<div onClick="modifyComponent('componentsandsections_dg',healtaEditor);">${sessionScope.locale.get("edit")}</div>
		 	<div onClick="updateComponentInSRTemplate($('#componentsandsections_dg').datagrid('getSelected'),healtaEditor);">${sessionScope.locale.get("admin.synctocurrenttemplate")}</div>
		 	<div onClick="deleteComponent('componentsandsections_dg');">${sessionScope.locale.get("delete")}</div>
	    </div>
    </div>
    <div title="DICOM" style="padding:0px">
        <div id="toolbar_div_sr_past" style="padding:2px;">
			<input class="easyui-searchbox"  data-options="prompt:'${sessionScope.locale.get('report.name')}',
				searcher:function(value){$('#clinicalcodes_dg_temp').datagrid({queryParams: {meaning: value}});}" style="width:100%;">
		</div>
	   	<table id="clinicalcodes_dg_temp" style="width: 100%;height:100%;" class="easyui-datagrid" data-options="border:false,queryParams: {meaning:'' } ,showHeader:false,
			url:'${ctx}/srtemplate/findClinicalCode?scheme=DICOM,SYSTEM,99SMS_SY',method:'get',fitColumns:true,striped:false,loadMsg:'${sessionScope.locale.get('loading')}...',toolbar:'#toolbar_div_sr_past',
			  singleSelect:true,
			  emptyMsg:'${sessionScope.locale.get('nosearchresults')}...',
			  onDblClickRow:function(index,row){
	     			insertClinicalCodeToTmp(row,healtaEditor);
				}">
			
			<thead>
	            <tr>
	                <th data-options="field:'meaning'">${sessionScope.locale.get("report.name")}</th>
	            </tr>
	        </thead>
		</table>
    </div>

    <div title="${sessionScope.locale.get('report.finding')}" style="padding:0px">
        <table id="xsltdg_sr" class="easyui-datagrid" style="width:100%;" border="0"
			data-options="singleSelect:true,fit:true,toolbar:'#toolbar_div_xslt_sr',queryParams: {name:'',displayname:'' },
			url:'${ctx}/template/getXsltTemplates',autoRowHeight:true,
			loadMsg:'${sessionScope.locale.get('loading')}...',emptyMsg:'${sessionScope.locale.get('nosearchresults')}...',
			  onDblClickRow:function(index,row){
	        					insertFindingToTmp(row,healtaEditor);
							}">
			<thead>
				<tr>
					<th data-options="field:'name',width:150">${sessionScope.locale.get("report.name")}</th>
					<th data-options="field:'displayname',width:150">${sessionScope.locale.get("admin.displayname")}</th>
					<th data-options="field:'belongreport',width:150">${sessionScope.locale.get("admin.belongtoreport")}</th>
					<th data-options="field:'viaversion',width:100">${sessionScope.locale.get("admin.viaversion")}</th>
				</tr>
			</thead>
		</table>
		<div id="toolbar_div_xslt_sr" style="padding:2px 2px;">
			<select class="easyui-combobox" id="viavsn_xslt" name="viaversion" labelPosition="top" style="width:40%;"
						data-options="prompt:'${sessionScope.locale.get('admin.viaversion')}',valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0014',editable:false,
						onSelect:function(item){doSearchFindingXslt_tmp(item)}"/>
			<input id="sb_xslt" class="easyui-searchbox" data-options="prompt:'${sessionScope.locale.get('admin.templatename')}',
				searcher:function(value){doSearchFindingXslt_tmp(value)}" style="width:58%;">
	    </div>
    </div>
</div>