<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>Insert title here</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false" style="padding:0px;margin:auto;">
    <div data-options="region:'west', title:'检查申请', href:'${ctx}/schedule/goToApplicationView', onLoad:initApplicationView_sch, hideCollapsedContent:false,headerCls:'panelHeaderCss_top'" style="width:350px;">
    
    </div>       
    <div data-options="region:'center',border:false">
        <div class="easyui-layout" data-options="fit:true,border:false" style="padding:0px;margin:auto;">
            <div data-options="region:'north',border:false" style="height:40px;">
                <div style="padding:5px;text-align:right;">
                    <shiro:hasPermission name="save_schedule">
                    <a class="easyui-linkbutton" style="width:125px;height:28px" onclick="beforeSaveSchedule(false)">保存</a>
                    </shiro:hasPermission>
                    <shiro:hasPermission name="saveAndprint_schedule">
                    <a class="easyui-linkbutton" style="width:150px;height:28px" onclick="beforeSaveSchedule(true)">保存并打印预约单</a>
                    </shiro:hasPermission>
                    <a id="print_sch" href="printApp:-1" type="hidden" ></a>
                    <c:if test="${enable_scan_module}">
                        <%-- <a href='reporttool:-c scan -n ${username} -i ${ip} -s ${scanurl}' id="launch_scaner" class="easyui-linkbutton" 
                            style="width:120px;height:28px" autoLaunch="${auto_launch_scaner}">启动扫描仪</a> --%>
                        <a class="easyui-linkbutton" style="width:120px;height:28px" onclick="triggerScan_sch();">扫描</a>
                    </c:if>
                    <a id="print_res" href="tool:-1" type="hidden" ></a>
                    <a id="scan_sch" href="tool:-1" type="hidden" ></a>
                    <a class="easyui-linkbutton" style="width:125px;height:28px" onClick="cancelSave_sch()">取消</a>
                </div>
            </div>
            <div data-options="region:'center',border:false">
                <div style="margin-left:auto;margin-right:auto;width:1010px;">
                    <form name="scheduleForm" id="scheduleForm" method="POST">
                        <%@ include file="/view/front/register/reg_patient_info.jsp"%>
                        <%@ include file="/view/front/register/reg_admission_info.jsp"%>
                        <%@ include file="/view/front/register/reg_study_info.jsp"%>
                        <div class="easyui-panel" title='预约时间'  halign='left' style="height:185px;padding:2px;width:990px;" data-options="headerCls:'panelHeaderCss_top',bodyCls:'panelBodyCss_top'">
                                <table class="mytablelaout">
                                    
                                    <tr>
                                        <!-- <td align="right">预约日期：</td> -->
                                        <td >
                                            <div id="calendar_sch" class="easyui-calendar" style="width:250px;height:170px;" title="预约日期：" data-options="onSelect:handleSelect_gettime,
                                                validator: function(date){
                                                    if (date.getTime() >((new Date()).getTime()-86400000)){
                                                        return true;
                                                    } else {
                                                        return false;
                                                    }
                                                }
                                                ">
                                            </div>
                                        </td>
                                        <td align="right">可选时间：</td>
                                        <td>
                                            <div id="schedule_time_panel" class="easyui-panel" style="width:608px;height:100%">
                                            无可选时间，请选择其他日期/切换检查设备。
                                            </div>                              
                                        </td>
                                    </tr>
                
                                </table>
                        </div>
                        <!-- </div> -->
                        
                        <input id="appointmenttime" type="hidden" name="appointmenttime"/>                   
                    </form>
                    
                    <input id="user_institution" name="user_institution" value="${user_institution}" type="hidden">

                </div>
            </div>
        </div>
    </div>
</div>

<div id="notesDlg_reg" class="easyui-dialog" style="width:300px;height:150px"
    data-options="closed:true">
    <input id="notes_reg" name="notes" class="easyui-textbox" multiline="true" 
        style="width:298px;height:100%;border:none;" prompt="可以在此输入备注信息">
</div>

<div id="printDlg_res" class="easyui-dialog" style="width:300px;height:180px;padding:10px;" closed="true">
	<div style="margin-top: 2px;">
	<div class="messager-icon messager-question"></div>
	<span>请选择打印张数：</span>
	</div>
	<div style="margin-top: 10px;">
	<select class="easyui-combobox" id="copies_reservation" style="width:90%;height:30px;"
		data-options="editable:false,panelHeight:'auto'">
	          <option value="1">1</option>
	          <option value="2">2</option>
	          <option value="3">3</option>
	          <option value="4">4</option>
	          <option value="5">5</option>
	</select>&nbsp;张
	</div>
</div>

<input id="scanClientIp_reg" value="${ip}" type="hidden">
<input id="scanClientUserid_reg" value="${user_id+990000}" type="hidden">
</body>
</html>