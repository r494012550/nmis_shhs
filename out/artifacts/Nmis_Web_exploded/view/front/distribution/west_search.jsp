<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
    <div id="w_s_accordion" class="easyui-accordion"  data-options="fit:true" border="0" >
    
        <div title="${sessionScope.locale.get("wl.search")}" data-options="selected:true">
        
            <div class="easyui-layout" data-options="fit:true,border:false">
                <div data-options="region:'center',border:false" style="padding:2px 5px;">
                        <div style="margin-top: 3px;">
                            <div class="easyui-datalist" id="myfilterlist_distribution" title="${sessionScope.locale.get("wl.myfilter")}" style="width:100%;height:220px" data-options="
                                    url: '${ctx}/distribution/getFilters?filterType=distribution',valueField:'id',textField:'description',tools:'#filter_tools',
                                    onSelect:function(index,row){
                                        searchForm_flag='searchForm';
                                        fillParams();
                                    }">
                            </div>
                            <div id="filter_tools">
                                <shiro:hasPermission name="save_WLfilter">
                                    <a class="easyui-tooltip" title="${sessionScope.locale.get("wl.savesearchcondition")}" onclick="openFilterSaveDialog('distribution');">
                                        <i class="icon iconfont icon-plus7" style="font-size:15px;vertical-align:2px;text-align: 2px;"></i>
                                    </a>
                                </shiro:hasPermission>
                                <shiro:hasPermission name="manage_WLfilter">
                                    <a class="easyui-tooltip" title="${sessionScope.locale.get("wl.managesearchcondition")}" onclick="openFilterManageDialog('distribution');">
                                        <i class="icon iconfont icon-set2" style="font-size:15px;vertical-align:2px;text-align: center;"></i>
                                    </a>
                                </shiro:hasPermission>
                                <a class="easyui-tooltip" title="${sessionScope.locale.get("wl.clear")}" onclick="$('#myfilterlist_distribution').datalist('clearSelections');">
                                    <i class="icon iconfont icon-qingkong" style="font-size:15px;vertical-align:2px;text-align: center;"></i>
                                </a>
                            </div>
                        </div>
                    <form id="searchForm_distribution" method="post">
                        <div style="width: 100%;margin-top: 3px;" class="mylabel">
                             <div style="width:120px;float:left;">
                                <select class="easyui-combobox" name="modality" id="modality" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.modality")}:" labelPosition="top" 
                                    data-options="valueField:'code',textField:'name_zh',multiple:true,editable:false,panelHeight:'auto',
                                    url:'${ctx}/syscode/getCode?type=0004'">
                                    
                                </select>
                            </div>
                            <div style="width:120px;float:right;">
                                <select class="easyui-combobox" name="reportstatus" id="reportStatus" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.reportstatus")}:" labelPosition="top"
                                    data-options="valueField:'code',textField:'${sessionScope.syscode_lan}',editable:false,panelHeight:'auto',value:'31',
                                    url:'${ctx}/syscode/getCode?type=0007&addempty=true'">
                                </select>
                            </div>
                        </div>
                        <div style="width: 100%;margin-top: 3px;" class="mylabel">
                             <select class="easyui-combobox" name="reportprintstatus" id="reportprintstatus" style="width:100%;height:50px;" label="打印状态：" labelPosition="top"
                                 data-options="panelHeight:'auto',editable:false">
                                 <option value="0">未打印</option>
                                 <option value="1">已打印</option>
                                 <option value="2">全部</option>
                             </select>
                        </div>
                         <div style="width:100%;margin-top:3px;" class="mylabel">
                            <select class="easyui-combobox" name="datetype" id="datetype" style="width:100%;height:50px;" label="${sessionScope.locale.get("wl.datetime")}:" labelPosition="top" 
                                data-options="editable:false,panelHeight:'auto'">
                                <option value="studytime">${sessionScope.locale.get("wl.studytime")}</option>
                                <option value="registertime">${sessionScope.locale.get("wl.registertime")}</option>
                                <option value="reporttime">报告时间</option>
                                <option value="audittime">审核时间</option>
                            </select>
                        </div>
                        <div class="easyui-panel" style="width:100%;margin-top:3px;padding:1px;">
                            <a id="today" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true,selected:true" 
                                onclick="$('#datefrom_dis').datebox('setValue','');$('#dateto_dis').datebox('setValue','');$('#appdate').val('T');searchForm_flag='searchForm';searchStudyWS();">${sessionScope.locale.get("wl.today")}</a>
                            <a  id="yesterday" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
                                onclick="$('#datefrom_dis').datebox('setValue','');$('#dateto_dis').datebox('setValue','');$('#appdate').val('Y');searchForm_flag='searchForm';searchStudyWS();">${sessionScope.locale.get("wl.yesterday")}</a>
                            <a id="threeday" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
                                onclick="$('#datefrom_dis').datebox('setValue','');$('#dateto_dis').datebox('setValue','');$('#appdate').val('TD');searchForm_flag='searchForm';searchStudyWS();">近三天</a>
                        </div>
                        
                        
                        <div style="width:100%;margin-top:3px;">
                            <div style="width:120px;float:left;">
                                <input id="datefrom_dis" name="datefrom" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
                                    onSelect:function(){
                                        $('#today').linkbutton({selected:false});
                                        $('#yesterday').linkbutton({selected:false});
                                        $('#threeday').linkbutton({selected:false});
                                        $('#fiveday').linkbutton({selected:false});
                                        $('#week').linkbutton({selected:false});
                                        $('#month').linkbutton({selected:false});
                                        $('#appdate').val('');
                                    }">
                            </div>
                            <div style="width:120px;float:right;">
                                <input id="dateto_dis" name="dateto" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
                                    onSelect:function(){
                                        $('#today').linkbutton({selected:false});
                                        $('#yesterday').linkbutton({selected:false});
                                        $('#threeday').linkbutton({selected:false});
                                        $('#fiveday').linkbutton({selected:false});
                                        $('#week').linkbutton({selected:false});
                                        $('#month').linkbutton({selected:false});
                                        $('#appdate').val('');
                                    }">
                            </div>
                        </div>
                        
                        <div style="width: 100%;margin-top: 3px;" class="mylabel">
                            <div style="width:120px;float:left;">
                                 <input id="studyid" name="studyid" class="easyui-textbox" style="width:119px;height:50px;" type="text" label="${sessionScope.locale.get("wl.studyid")}:" labelPosition="top">
                            </div>
                            
                            <div style="width:120px;float:right;">
                                <select class="easyui-combobox" name="patientsource" id="patientsource" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.patientsource")}:" 
                                    labelPosition="top" data-options="valueField:'code',textField:'${sessionScope.syscode_lan}',url:'${ctx}/syscode/getCode?type=0002&addempty=true',editable:false,
                                    panelHeight:'auto'">
                                </select>
                            </div>
                        </div>
                        
                        <div style="width: 100%;margin-top: 3px;" class="mylabel">
                            <div style="width:120px;float:left;">
                                <input id="patientid" name="patientid" class="easyui-textbox" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.patientid")}:" labelPosition="top">
                            </div>
                            
                            <div style="width:120px;float:right;">
                                <input id="patientname" name="patientname" class="easyui-textbox" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.patientname")}:" labelPosition="top" >
                            </div>
                        </div>
                        
                         
                         <div style="width: 100%;margin-top: 3px;" class="mylabel">
                            <div style="width:120px;float:left;">
                                <input id="reportphysician_name_ws" name="reportphysician_name" class="easyui-textbox" style="width:119px;height:50px;" label='${sessionScope.locale.get("report.reportphysician")}:' labelPosition="top">
                            </div>
                            
                            <div style="width:120px;float:right;">
                                <input id="auditphysician_name_ws" name="auditphysician_name" class="easyui-textbox" style="width:119px;height:50px;" label='${sessionScope.locale.get("report.auditphysician")}:' labelPosition="top" >
                            </div>
                        </div>
                        
                        
                        <input id="appdate" type="hidden" name="appdate" value="T"/>
                    </form>
                </div>
                <div data-options="region:'south',hideCollapsedContent:false,border:false" style="height:70px;padding:2px 5px;">
                    <div style="margin-top: 5px;">
                        <a class="easyui-linkbutton" onclick="searchForm_flag='searchForm';searchStudyWS();" style="width:100%;height:28px">${sessionScope.locale.get("wl.dosearch")}</a>
                    </div>
                    
        
                    <div style="margin-top: 5px;">
                        <a class="easyui-linkbutton c2" onclick="clearManage();" style="width:100%;height:28px">${sessionScope.locale.get("wl.clearcondition")}</a>
                    </div>
                </div>
            </div>
        </div>

        
        
    </div>
    
</body>
</html>