<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<script type="text/javascript">

</script>
<style type="text/css">

	.mydiv hr{
		border:none;
		border-top:1px dashed;/*#0066CC*/
	}
	
	.mydiv input{
		border-width: thin;
		border-color: rgb(195, 217, 224);
		border-style: solid solid solid solid;
	}
	.mydiv article{
		border: 0;
	}
	/*.mydiv textarea{
		border-width: thin;
		border-color: rgb(195, 217, 224);
		border-style: solid solid solid solid;
	}
	
	.mydiv select{
		border-width: thin;
		border-color: rgb(195, 217, 224);
		border-style: solid solid solid solid;
		background-color: rgb(255, 243, 243);
	}*/
			/*body{font-family: 宋体, SimSun;}
			.pagebreak{display:block;clear:both !important;cursor:default !important;width: 100% !important;margin:0;}
		    .pageNext{page-break-after: always;}*/
	.mydiv table {
		margin-bottom:5px;
		border-collapse:collapse;
		display:table;
	}
	.mydiv td,th {
		padding: 3px 3px;
		border: 1px solid #DDD;
	}
	.mydiv th {
		border-top:1px solid #BBB;
		background-color:#F7F7F7;
	}
	
	.mydiv table tr.firstRow th {
		border-top-width:2px
	}
	
	.mydiv td p{
		margin:0px;
		padding:0px;
	}
	
	.mydiv p{
		margin:3px !important;
	} 
	.mydiv ol{
		margin:3px !important;
	}
	.hlong *{display:inline-block;vertical-align:middle}  
	
	.mydiv [required=required]{
		border-width: thin;
		border-color: rgb(255, 168, 168);
		border-style: solid solid solid solid;
	}
	.mydiv [optional=optional]{
		border-width: thin;
		border-color: rgb(195, 217, 224);
		border-style: solid solid solid solid;
	}
	
	.sectioncontainer{
		background-color:#f2f2f2;
		padding:1px;
		margin:1px;
	}
	
</style>
	<div class="easyui-layout" data-options="fit:true,border:false" >
		<div data-options="region:'north',border:false" style="height:32px;padding:2px;text-align:right;">
			<a class="easyui-linkbutton" style="width:80px;height:28px" onclick="apply_webView('${studyorderfk}')">申请单</a>
			<a class="easyui-linkbutton" style="width:80px;height:28px" onclick="process_webView(${studyorderfk});">检查流程</a>
			<shiro:hasPermission name="open_Image">
				<c:if test="${enable_plaza_callup==1}">
					<a class="easyui-linkbutton" href="${plaza_loaddata}" style="width:80px;height:28px" onclick="">${sessionScope.locale.get("wl.image")}(p)</a>
				</c:if>
				 <c:if test="${enable_via_callup==1}">
					<a class="easyui-linkbutton" href="${callviapara}" style="width:80px;height:28px" onclick="">${sessionScope.locale.get("wl.image")}(v)</a>
				</c:if>
			</shiro:hasPermission>
			<a class="easyui-menubutton" style="width:85px;height:28px" onClick="printReport('${projecturl}',${reportid},'${srtemplatename}',1);" 
				data-options="plain:false,menu:'#printmenu_${reportid}'">${sessionScope.locale.get("report.print")}</a>
			<div id="printmenu_${reportid}" style="width:150px;">
			    <div><a class="easyui-linkbutton" plain="true" onClick="printReport('${projecturl}',${reportid},'${srtemplatename}',1);">${sessionScope.locale.get("report.print")}</a></div>
			    <div><a class="easyui-linkbutton" plain="true" onClick="print(${reportid},'1');">打印预览</a></div>
			</div>
			<a class="easyui-linkbutton" style="width:80px;height:28px" onclick="closeTab(${reportid});">${sessionScope.locale.get("report.close")}</a>
		</div>
 	    <!-- 结构化模板填充位置 -->
 	    	<div data-options="region:'center',border:false">
				<div class="easyui-layout" data-options="fit:true,border:false">
			        <div data-options="region:'center'" id="sr_div_${reportid}">
			        	 <div class="mydiv" style="padding:5px;margin-left:auto;margin-right:auto;width:720px;background-color:#FFFFFF;">
			        	 	<div id="sr_container_${reportid}" class="easyui-panel" data-options="border:false" style="background-color:#FFFFFF;"></div>
			        	 </div>
			        </div>
			    </div>
			</div>
       </div>
        <div class="gallerys" style='display:none'>
			<img id="imageShow_${reportid}" src="" class="gallery-pic" onclick="$.openPhotoGallery(this)"/>
		</div>
		<div id="viapdfdlg_${reportid}" class="easyui-dialog" data-options="resizable:true,border:'thin',doSize:true" title="${sessionScope.locale.get('report.syngoviasr')}" style="width:700px;height:720px;padding:5px;" closed="true">
	        <div id="pdf-container_${reportid}" style="width:100%;height:100%"></div>
	    </div>
		<input id="orderStatus_${reportid}" type="hidden" value="${reportStatus}">
		<input id="id_${reportid}" name="reportid" type="hidden" value="${reportid}">
		<input id="orderid_${reportid}" name="studyorderfk" type="hidden" value="${studyorderfk}">
		<input id="studyitem_${reportid}" type="hidden" value="${studyitem}">
		<input id="viareportid_${reportid}" name="viareportid" type="hidden" value="${viareportid}">
       