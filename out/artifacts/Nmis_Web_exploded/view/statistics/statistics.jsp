<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%@ include file="/common/meta.jsp"%>
    <title>统计</title>
    <link rel="shortcut icon" href="${ctx}/themes/head.ico?v=${vs}">
    <%@ include file="/common/basecss.jsp"%>
    <link rel="stylesheet" type="text/css" href="${ctx}/themes/sidemenu_style.css?v=${vs}">
    <%@ include file="/common/basejs.jsp"%>
    <%-- <script type="text/javascript" src="${ctx}/js/easyui/datagrid-groupview.js"></script> --%>
    <%-- <script type="text/javascript" src="${ctx}/js/common.js"></script> --%>
    <script type="text/javascript" src="${ctx}/js/statistics/statistics.js?v=${vs}"></script>
    <script type="text/javascript" src="${ctx}/js/statistics/dashboard.js?v=${vs}"></script>
    <script type="text/javascript" src="${ctx}/js/statistics/reportboard.js?v=${vs}"></script>
    <script type="text/javascript" src="${ctx}/js/echarts.min.js?v=${vs}"></script>
    <style type="text/css">
        /* .tree-icon{
            background:url('${ctx}/themes/icons/mini_add.png') no-repeat center center
		}
		*/
        .flex-container .report_item {
            width:20%
        }

        .condition_div {
            float : left;
            padding-right:5px;
            padding-bottom:5px;
        }
        .condition_div label{
            color: #fafafa;
        }
        .button_div {
            float : right;
            padding-right:5px;
            padding-bottom:5px;
        }

        .mydatalistdiv .datagrid-body {
            background: #15162C;
            color: #FFFFFF;
        }
        .mydatalistdiv .datagrid-group {
            background: #111222;
            color: #b8c7ce;
            font-size: 14px;
            font-weight: bold;
            height: 35px;
            line-height: 35px;/*设置其文字内容垂直居中*/
            border-width: 0px;
        }

        .mydatalistdiv .datagrid-cell {
            color: #FFFFFF;
        }

        .statistics{
            padding: 10px;
        }
        .statistics table {
            margin-bottom:0px;
            border-collapse:collapse;
            display:table;
        }
        .statistics td,th {
            padding: 1px 1px;
            border: 1px solid #DDD;
        }
        .statistics table tr.firstRow {
            border-top-width:2px;
            background-color:#9cc8f7;
        }

    </style>
</head>
<body style="background:#fafafa;" class="easyui-layout">
<div data-options="region:'north',border:false" style="height:35px;" class="title_background">
    <table style="width:100%;height:100%;" cellspacing="0">
        <tr>

            <jsp:include page="/view/navigation.jsp" />

            <td><input type="hidden" id="userid_hidden" value="${user_id}"/>
                <input type="hidden" id="username_hidden" value="${username}"/></td>
            <td><input type="hidden" id="name_hidden" value="${name}"/></td>
            <td align="right" style="font-weight:bold; padding:0px 5px 0px 0px;">
                <a href="#" class="easyui-menubutton" data-options="menu:'#head_mm2',iconCls:'icon iconfont icon-user'" style="color:#b8c7ce;">${name}</a>
            </td>
        </tr>
    </table>

    <div id="head_mm2" style="width:150px;">
        <div onclick="openMyConfigDialog()">我的配置</div>
        <div class="menu-sep"></div>
        <div onclick="openDevTools()">开发工具</div>
        <div class="menu-sep"></div>
        <div onclick="logout()">退出登录</div>
    </div>

</div>
<div data-options="region:'west',hideCollapsedContent:false,border:false"  style="width:170px;">
    <div style="width:100%;height:100%;" class="mydatalistdiv">
        <table class="easyui-datalist" id="statisticsReport_dl" data-options="singleSelect:true,fit:true,url:'${ctx}/statistics/findStatistics',
				loadMsg:'加载中...',emptyMsg:'没有查找到报表...',textField:'name',border:false,
				onClickRow:function(index,row){showStatistics(row);},
				groupField:'classifyname'" border="0">

        </table>
    </div>
</div>
<div data-options="region:'center',border:false,onResize:tabsOnResize">
    <div class="easyui-tabs" id="statistics_tabs" data-options="fit:true,plain:true,narrow:true,border:false,tabWidth:200">
        <div title="主页" data-options="href:'${ctx}/statistics/toDashBoard'${hasUserdac?'':',onLoad:dashboardOnload'}" style="padding:5px;">
        </div>
        <div title="报告" data-options="href:'${ctx}/statistics/toReportBoard'${hasUserdac?'':',onLoad:reportboardOnload'}" style="padding:5px;">
        </div>
        <div title="统计" class="easyui-layout" data-options="fit:true" id="centerarea">
            <div data-options="region:'north',border:false,onLoad:initConditions" style="height:150px;background:#505679;padding:5px;"><%-- 4E5465--%>
            </div>
            <div data-options="region:'center',border:false">
                <div id="report_tabs" class="easyui-tabs" data-options="fit:true,plain:true,narrow:true,border:false,tabWidth:200,onSelect:showHtml">
                    <div title="PDF">
                        <iframe id="reportFrame" width="100%" height="100%" frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="no" allowtransparency="yes">
                        </iframe>
                    </div>
                    <div title="html" id="main_statistics" style="border-right-width: 0px;">
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="progress_dlg" class="easyui-dialog" data-options="noheader:true,modal:true,shadow:false,border:false,closed:true" style="width:120px;height:40px;padding:1px;">
    <table style="width:100%;height:100%;" cellspacing="0" border="0">
        <tr>
            <td><span align="center"><img src="${ctx}/themes/icons/loading.gif?v=${vs}"  alt="${sessionScope.locale.get("loading")}..." /></span></td>
            <td><span align="center">${sessionScope.locale.get("loading")}</span></td>
        </tr>
    </table>
</div>
<div id="common_dialog"></div>
<div id="relogin_dialog"></div>
<input type="hidden" id="statisticalreport_id" value=""/>
<input type="hidden" id="chart_theme" value="${chart_theme}"/>
</body>
</html>