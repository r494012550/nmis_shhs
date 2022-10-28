<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
<div data-options="region:'center',border:false" style="padding:2px 5px;">
<form id="labelSearchForm" method="post">
    <div style="width:100%;margin-top:3px;">
        <input class="easyui-combotree" id="publiclabel_tree" data-options="url:'${ctx}/report/getReportLabel?ispublic=1',
            label:'公共标签:',labelPosition:'top',textField:'display',multiple:true" style="width:100%;height:56px;">
    </div>
    
    <div style="width:100%;margin-top:3px;">               
        <input class="easyui-combotree" id="privatelabel_tree" data-options="url:'${ctx}/report/getReportLabel?ispublic=0',
            label:'个人标签:',labelPosition:'top',textField:'text',multiple:true" style="width:100%;height:56px;">
    </div>
    
    <div style="width:100%;margin-top:3px;">
        <input class="easyui-combobox" id="icd10labelct_" style="width:90%;"
                        data-options="label:'ICD10:',editable:false,textField:'text',valueField:'id',
                        labelPosition:'top',multiple:true,">
        <a class="easyui-tooltip" title="ICD查询" onclick="searchIcd10('');">
            <i class="icon iconfont icon-plus7" style="font-size:15px;vertical-align:2px;text-align: 2px;"></i>
        </a>
    </div>
    
    <div style="width:100%;margin-top:3px;">
        <select class="easyui-combobox" id="datetype2" name="datetype" style="width:100%;height:56px;" label="${sessionScope.locale.get("wl.datetime")}:" labelPosition="top" 
            data-options="editable:false,panelHeight:'auto'">
                 <option value="reporttime">报告日期</option>
                 <option value="audittime">审核时间</option>
                 <option value="studytime">${sessionScope.locale.get("wl.studytime")}</option>
                 <option value="registertime">${sessionScope.locale.get("wl.registertime")}</option>
                 <%-- <option value="appointmenttime">${sessionScope.locale.get("wl.appointmenttime")}</option> --%>
        </select>
    </div>
    
    <div class="easyui-panel" style="width:100%;margin-top:3px;padding:2px;">
            <a id="today2" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g2',plain:true" 
                onclick="$('#datefrom2').datebox('setValue','');$('#dateto2').datebox('setValue','');$('#appdate2').val('T');labelSearch();">${sessionScope.locale.get("wl.today")}</a>
            <a  id="yesterday2" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g2',plain:true" 
                onclick="$('#datefrom2').datebox('setValue','');$('#dateto2').datebox('setValue','');$('#appdate2').val('Y');labelSearch();">${sessionScope.locale.get("wl.yesterday")}</a>
            <a id="threeday2" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g2',selected:true,plain:true" 
                onclick="$('#datefrom2').datebox('setValue','');$('#dateto2').datebox('setValue','');$('#appdate2').val('TD');labelSearch();">近三天</a>
    </div>
                    
    <div class="easyui-panel" style="width:100%;margin-top:3px;padding:2px;">
        <a id="fiveday2" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g2',plain:true" 
            onclick="$('#datefrom2').datebox('setValue','');$('#dateto2').datebox('setValue','');$('#appdate2').val('FD');labelSearch();">近五天</a>
        <a  id="week2" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g2',plain:true" 
            onclick="$('#datefrom2').datebox('setValue','');$('#dateto2').datebox('setValue','');$('#appdate2').val('W');labelSearch();">近一周</a>
        <a id="month2" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g2',plain:true" 
            onclick="$('#datefrom2').datebox('setValue','');$('#dateto2').datebox('setValue','');$('#appdate2').val('TM');labelSearch();">近三个月</a>
    </div>
                    
    <div style="width:100%;margin-top:3px;">
        <div style="width:120px;float:left;">
            <input id="datefrom2" name="datefrom" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
            onSelect:function(){
                $('#today2').linkbutton({selected:false});
                $('#yesterday2').linkbutton({selected:false});
                $('#threeday2').linkbutton({selected:false});
                $('#fiveday2').linkbutton({selected:false});
                $('#week2').linkbutton({selected:false});
                $('#month2').linkbutton({selected:false});
                $('#appdate2').val('');
            }">
        </div>
        <div style="width:120px;float:right;">
            <input id="dateto2" name="dateto" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
            onSelect:function(){
                $('#today2').linkbutton({selected:false});
                $('#yesterday2').linkbutton({selected:false});
                $('#threeday2').linkbutton({selected:false});
                $('#fiveday2').linkbutton({selected:false});
                $('#week2').linkbutton({selected:false});
                $('#month2').linkbutton({selected:false});
                $('#appdate2').val('');
            }">
        </div>
    </div>
    <input id="appdate2" name="appdate" type="hidden" value="TD"/>
    <input id="publiclabel2" name="publiclabel" type="hidden" value="">
    <input id="privatelabel2" name="privatelabel" type="hidden" value="">
    <input id="icd10label_" name="icd10" type="hidden" value="">
</form>
</div>
<div data-options="region:'south',hideCollapsedContent:false,border:false" style="height:70px;padding:2px 5px;">
    <div style="margin-top: 5px;">
        <a class="easyui-linkbutton" onclick="labelSearch();" style="width:100%;height:28px;margin-top: 5px;">${sessionScope.locale.get("wl.dosearch")}</a>
    </div>
    <div style="margin-top: 5px;">
        <a class="easyui-linkbutton c2" onclick="clearLabelSearch();" style="width:100%;height:28px">${sessionScope.locale.get("wl.clearcondition")}</a>
    </div>
</div>
</body>
</html>