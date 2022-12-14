<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title>${sessionScope.locale.get("admin.setting")}</title>
	<link rel="shortcut icon" href="${ctx}/themes/head.ico?v=${vs}">
	<%@ include file="/common/basecss.jsp"%>
	<link rel="stylesheet" type="text/css" href="${ctx}/themes/sidemenu_style.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/js/cropper/main.css?v=${vs}">
	<link rel="stylesheet" type="text/css" href="${ctx}/js/cropper/cropper.min.css?v=${vs}">
	<!-- <link rel="stylesheet" type="text/css" href="${ctx}/js/ueditor/themes/iframe.css"/> -->
	<%@ include file="/common/basejs.jsp"%>
	<script src="${ctx}/js/cropper/cropper.min.js" type="text/javascript"></script>
	
	<script type="text/javascript" src="${ctx}/js/easyui/jquery.portal.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jquery.color.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/ueditor/ueditor.config.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/ueditor/ueditor.all.js?v=${vs}"></script>
	<%-- <script type="text/javascript" src="${ctx}/js/ueditor/lang/zh-cn/zh-cn.js"></script> --%>
	<script type="text/javascript" src="${ctx}/js/ueditor/rebound.plugin.v1.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jQuery.Hz2Py-min1.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/admin/admin.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/admin/template.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/admin/srtemplatemanage.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/admin/user.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/admin/system.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/admin/statistics.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/admin/dic.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/admin/colormanage.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/admin/workforce.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/pinyin_dict_notone.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/pinyin_dict_withtone.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/pinyinUtil.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/admin/com_goods.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/admin/com_goods_variety.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/admin/calling.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/admin/project.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/admin/formManage.js?v=${vs}"></script>
	<script type="text/javascript">
        var data = [{
            text: "&nbsp;${sessionScope.locale.get('admin.user')}",
            iconCls: 'icon iconfont icon-user',
            state: 'open',
            children: [{
                text: "${sessionScope.locale.get('admin.user')}",
                href: '/admin/user',
                selected: true
            },{
                text: "${sessionScope.locale.get('admin.role')}",
                href: '/admin/role'
            },{
                text: "${sessionScope.locale.get('admin.authority')}",
                href: '/admin/authority'
            },{
                text: "${sessionScope.locale.get('admin.resources')}",
                href: '/admin/resource'
            }]
        },{
            text: "&nbsp;${sessionScope.locale.get('admin.template')}",
            iconCls: 'icon iconfont icon-template',
            children: [{
                text: "${sessionScope.locale.get('admin.template')}",
                href: '/admin/templatemanage'
            }
            <%
            	if((boolean)request.getAttribute("sr_support")){
            %>
            ,{
                text: "${sessionScope.locale.get('admin.srtemplate')}",
                href: '/admin/srtemplatemanage'
            },{
                text: "${sessionScope.locale.get('admin.componentlibrary')}",
                href: '/admin/srLibrary'
            },{
                text: "${sessionScope.locale.get('admin.findingxsltlibrary')}",
                href: '/admin/xslLibrary'
            }
            <%
        		}
            %>
            ]
        	
        },{
            text: "&nbsp;${sessionScope.locale.get('admin.dic')}",
            iconCls: 'icon iconfont icon-zidian',
            children: [{
                text: '????????????',
                href: '/admin/institution'
            },{
                text: '????????????',
                href: '/admin/department'
            },{
                text: '????????????',
                href: '/admin/employee'
            },{
            	text: "${sessionScope.locale.get('admin.modalitymanagement')}",
            	children: [{
                    text: '????????????',
                    href: '/admin/equipgroup'
                },{
                    text: "${sessionScope.locale.get('admin.modalitymanagement')}",
                    href: '/admin/dicmanage'
                }]
            },{
                text: '????????????',
                href: '/admin/organdic'
            },{
                text: '????????????',
                href: '/admin/examitemdic'
            },{
            	text: '????????????',
            	children: [{
                    text: '????????????',
                    href: '/admin/comGoods'
                },{
                    text: '????????????',
                    href: '/admin/comGoodsVariety'
                }]
            },{
                text: '????????????',
                href: '/admin/diccalling'
            },{
                text: '????????????',
                href: '/admin/toCommonDic'
            }]
        },{
            text: "&nbsp;????????????",
            iconCls: 'icon iconfont icon-paiban',
            children: [{
                text: "??????????????????",
                href: '/workforce/reportTask'
            },{
                text: "??????????????????",
                href: '/workforce/shift'
            },{
                text: "??????????????????",
                href: '/workforce/deptWorktime'
            },{
                text: "????????????",
                href: '/workforce/worktime'
            }]
        },{
            text: "&nbsp;${sessionScope.locale.get('admin.statistical')}",
            iconCls: 'icon iconfont icon-tongjibaobiao',
            children: [{
                text: "${sessionScope.locale.get('admin.statistical')}",
                href: '/admin/statisticsmanage'
            }]
        },{
        	text:"????????????",
        	iconCls: 'icon iconfont icon-keti',
            children: [{
                text: "????????????",
                href: '/admin/projectManagement'
            },{
                text: "????????????",
                href: '/admin/projectRole'
            },{
                text: "????????????",
                href: '/admin/toAuthority'
            },{
                text: "????????????",
                href: '/admin/projectResource'
            },{
                text: "????????????",
                href: '/admin/formManage'
            },{
				text: "?????????",
				href: '/admin/formLibrary'
			},{
                text: "????????????",
                href: '/admin/applyForJoinGroup'
            },{
                text: "????????????",
                href: '/admin/joinGroupRule'
            }]
        },{
            text: "&nbsp;${sessionScope.locale.get('admin.system')}",
            iconCls: 'icon iconfont icon-system',
            children: [{
                text: "${sessionScope.locale.get('admin.systemcode')}",
                href: '/admin/syscode'
            },{
            	text: "????????????",
                href: '/admin/printtemplate'
            },{
                text: "????????????",
                href: '/admin/passwordPolicy'
            },{
            	text: "????????????",
                href: '/admin/label'
            },{
            	text: "????????????",
                href: '/admin/colormanage'
            }/* ,{
            	text: "?????????",
                href: '/admin/printer'
            } */,{
            	text: "???????????????",
                href: '/admin/client'
            },{
            	text: "RIS????????????",
                href: '/admin/risevents'
            },{
            	text: "????????????",
                href: '/admin/task'
            },{
            	text: "????????????",
                href: '/notice/toNoticeCenter'
            },{
            	text: "???????????????",
            	href: '/admin/urgentexplain'
            },{
                text: "??????????????????",
                href: '/admin/reportcheckerror'
            }]
        }]; 
        
        //class="easyui-sidemenu" data-options="data:data,multiple:false,border:false,onSelect:onselect_handel"
        
        $(function () {
	            /* $('#leftMenu').sidemenu({
	                data: data,
	                onSelect: onSideMenuSelect,
	                border: false,
	                multiple:false
	            }); */
	    });
        
        
        function onSideMenuSelect(item){
        	if(item.href.indexOf('srtemplatemanage')>=0){
        		$('#progress_dlg').dialog('open');
        	}
        	$('#main').panel({href:'${ctx}'+item.href});
        }
     </script>
</head>
<body style="background:#fafafa;" class="easyui-layout">
	<div data-options="region:'north',border:false" style="height:35px;" class="title_background">
		<table style="width:100%;height:100%;" cellspacing="0">
			<tr>
				
				<jsp:include page="/view/navigation.jsp" />
				<td><div id="favorites_edit"></div></td>
				<td>
					<input type="hidden" id="name_hidden" value="${name}"/>
					<input type="hidden" id="userid_hidden" value="${user_id}"/>
					<input type="hidden" id="username_hidden" value="${username}"/>
				</td>
				<td align="right" style="font-weight:bold; padding:0px 5px 0px 0px;">
				
					<a href="#" class="easyui-menubutton" data-options="menu:'#head_mm1',iconCls:'icon iconfont icon-user'" style="color:#b8c7ce;">${name}</a>

				</td>
			</tr>
		</table>
		
		<div id="head_mm1" style="width:150px;">
	        <div onclick="openMyConfigDialog()">${sessionScope.locale.get("wl.myprofile")}</div>
	        <div onclick="userNoticeCenter()">????????????</div>
	        <div class="menu-sep"></div>
	        <div onclick="openDevTools()">????????????</div>
	        <div class="menu-sep"></div>
	        <div onclick="checkSessionAnRelogin(${user.id})">????????????</div>
	        <div onclick="logout()">${sessionScope.locale.get("logout")}</div>
	    </div>
		
	</div>
	<div data-options="region:'west',hideCollapsedContent:false,border:false"  style="width:175px;background: #15162C;">
		<div id="leftMenu" class="easyui-sidemenu" style="width:175px;" data-options="data:data,border: false,onSelect: onSideMenuSelect,multiple:false" border="0"></div>
	</div>
	<%-- <div data-options="region:'east',hideCollapsedContent:false"  style="width:170px;">
		
		<div id="aa" class="easyui-accordion" data-options="fit:true" border="0">
			
			<div title="&nbsp;${sessionScope.locale.get('admin.user')}" data-options="selected:true,iconCls:'icon iconfont icon-user'" style="overflow:auto;">
		        <div>
	            	<a id="userbtn" class="easyui-linkbutton" style="width:100%;height:35px;" data-options="toggle:true,group:'g10',plain:true,selected:true" onClick="$('#main').panel('open').panel('refresh','${ctx}/admin/user');">${sessionScope.locale.get("admin.user")}</a>
	            </div>
	            <div>
			        <a class="easyui-linkbutton" style="width:100%;height:35px;" data-options="toggle:true,group:'g10',plain:true" onClick="$('#main').panel('open').panel('refresh','${ctx}/admin/role');">${sessionScope.locale.get("admin.role")}</a>
	            </div>
	            <div>
			        <a class="easyui-linkbutton" style="width:100%;height:35px;" data-options="toggle:true,group:'g10',plain:true" onClick="$('#main').panel('open').panel('refresh','${ctx}/admin/authority');">${sessionScope.locale.get("admin.authority")}</a>
	            </div>
	            <div>
			        <a class="easyui-linkbutton" style="width:100%;height:35px;" data-options="toggle:true,group:'g10',plain:true" onClick="$('#main').panel('open').panel('refresh','${ctx}/admin/resource');">${sessionScope.locale.get("admin.resources")}</a>
	            </div>
		    </div> 
		    <div title="&nbsp;${sessionScope.locale.get('admin.template')}" style="overflow:auto;" data-options="iconCls:'icon iconfont icon-template'">
		        <div>
	            	<a id='templateView' class="easyui-linkbutton" style="width:100%;height:35px;" data-options="toggle:true,group:'g10',plain:true" onClick="$('#main').panel('open').panel('refresh','${ctx}/admin/templatemanage');">${sessionScope.locale.get("admin.template")}</a>
	            </div>
	            <div>
	            	<a class="easyui-linkbutton" style="width:100%;height:35px;" data-options="toggle:true,group:'g10',plain:true" onClick="$('#main').panel('open').panel('refresh','${ctx}/admin/srtemplatemanage');">${sessionScope.locale.get("admin.srtemplate")}</a>
	            </div>
	            <div>
	            	<a class="easyui-linkbutton" style="width:100%;height:35px;" data-options="toggle:true,group:'g10',plain:true" onClick="$('#main').panel('open').panel('refresh','${ctx}/admin/srLibrary');">${sessionScope.locale.get("admin.componentlibrary")}</a>
	            </div>
	            <div>
	            	<a class="easyui-linkbutton" style="width:100%;height:35px;" data-options="toggle:true,group:'g10',plain:true" onClick="$('#main').panel('open').panel('refresh','${ctx}/admin/xslLibrary');">${sessionScope.locale.get("admin.findingxsltlibrary")}</a>
	            </div>
	            
		    </div>
		    <div title="&nbsp;${sessionScope.locale.get('admin.dic')}" style="overflow:auto;" data-options="iconCls:'icon iconfont icon-zidian'">
		        <div>
	            	<a class="easyui-linkbutton" style="width:100%;height:35px;" data-options="toggle:true,group:'g10',plain:true" onClick="$('#main').panel('open').panel('refresh','${ctx}/admin/dicmanage');">${sessionScope.locale.get("admin.modalitymanagement")}</a>
	            </div>
	            <div>
	            	<a class="easyui-linkbutton" style="width:100%;height:35px;" data-options="toggle:true,group:'g10',plain:true" onClick="$('#main').panel('open').panel('refresh','${ctx}/admin/examitemdic');">????????????</a>
	            </div>
	            <div>
	            	<a class="easyui-linkbutton" style="width:100%;height:35px;" data-options="toggle:true,group:'g10',plain:true" onClick="$('#main').panel('open').panel('refresh','${ctx}/admin/equipgroup');">???</a>
	            </div>
	            <div>
	            	<a class="easyui-linkbutton" style="width:100%;height:35px;" data-options="toggle:true,group:'g10',plain:true" onClick="$('#main').panel('open').panel('refresh','${ctx}/admin/organdic');">????????????</a>
	            </div>
		    </div>
		    <div title="&nbsp;${sessionScope.locale.get('admin.statistical')}" style="overflow:auto;" data-options="iconCls:'icon iconfont icon-tongjibaobiao'">
		        <div>
	            	<a class="easyui-linkbutton" style="width:100%;height:35px;" data-options="toggle:true,group:'g10',plain:true" onClick="$('#main').panel('open').panel('refresh','${ctx}/admin/statisticsmanage');">${sessionScope.locale.get("admin.statistical")}</a>
	            </div>
		    </div>
		    <div title="&nbsp;${sessionScope.locale.get('admin.system')}" style="overflow:auto;" data-options="iconCls:'icon iconfont icon-system'">
		        <div>
	            	<a class="easyui-linkbutton" style="width:100%;height:35px;" data-options="toggle:true,group:'g10',plain:true" onClick="$('#main').panel('open').panel('refresh','${ctx}/admin/modules');">????????????</a>
	            </div>
	            <div>
	            	<a class="easyui-linkbutton" style="width:100%;height:35px;" data-options="toggle:true,group:'g10',plain:true" onClick="$('#main').panel('open').panel('refresh','${ctx}/admin/syscode');">${sessionScope.locale.get("admin.systemcode")}</a>
	            </div>
	            
	            <div>
	            	<a class="easyui-linkbutton" style="width:100%;height:35px;" data-options="toggle:true,group:'g10',plain:true" onClick="$('#main').panel('open').panel('refresh','${ctx}/admin/label');">????????????</a>
	            </div>
		    </div>
		     --%>
		    
		  
		    <!-- <div title="&nbsp;&nbsp;DICOM????????????" style="overflow:auto;">
		        
		    </div>
			<div title="&nbsp;&nbsp;HL7??????" style="overflow:auto;">
		        
		    </div> -->
		  <!-- </div>
		</div> -->
		<div data-options="region:'center',border:false">
			<div id="main" class="easyui-panel" data-options="fit:true" border="0">
				
			</div>

		</div>
	<div id="progress_dlg" class="easyui-dialog" data-options="noheader:true,modal:true,shadow:false,border:false,closed:true" style="width:120px;height:40px;padding:1px;">
        <table style="width:100%;height:100%;" cellspacing="0" border="0">
			<tr>
				<td><span align="center"><img src="${ctx}/themes/icons/loading.gif"  alt="${sessionScope.locale.get("loading")}..." /></span></td>
				<td><span align="center">${sessionScope.locale.get("loading")}</span></td>
			</tr>
		</table>
    </div>
	<div id="common_dialog"></div>
	<div id="relogin_dialog"></div>
	<div id="send_notice_dialog"></div>
	<div id="notice_center_dialog"></div>
	<div id="select_user_dialog"></div>
	<div id="notice_dialog"></div>
	<div id="test_component_dialog"></div>
	<div id="personnel_dialog"></div>
	<div id="taskRole_dialog"></div>
	<div id="reportorderlines_dialog"></div>
	<div id="report_dialog"></div>
	<div id="reportorderlines_dialog"></div>
</body>
</html>