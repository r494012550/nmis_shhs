<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@page import="com.healta.constant.ReportType"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<c:set var="span_width" value="70"/>
<c:set var="span1_width" value="150"/>
<style type="text/css">
	._fontsize12 p , _fontsize12 span {
		font-size:12px !important;
	}
	._fontsize16 p,._fontsize16 span {
		font-size:16px !important;
	}
	._fontsize18 p,._fontsize18 span {
		font-size:18px !important;
	}
	._fontsize24 p,._fontsize24 span {
		font-size:24px !important;
	}
	._fontsize32 p,._fontsize32 span {
		font-size:32px !important;
	}
	._fontsize48 p,._fontsize48 span {
		font-size:48px !important;
	}
	.report_desc_div ol {
		padding-inline-start: 10px;
	}
	.report_result_div ol {
		padding-inline-start: 10px;
	}
	.edui-editor-body {
		color:${empty editor_color?'#000000':editor_color};
	}
</style>
<div style="display: none;" ><input type="text" id="value_${reportid}" ></div>

	<div id="report_layout_${reportid}" class="easyui-layout" data-options="fit:true,border:false">
		<div data-options="region:'south',border:false" style="height:34px;padding:2px;text-align:center;">
			<shiro:hasPermission name="save_report">
			<a class="easyui-linkbutton" id="savebtn_${reportid}" style="width:70px;height:28px" 
				onclick="beforesaveReport(${reportid},'${report.reportStatus}','${report.studyitems}','${report.sexdisplay}','saveReport');">${sessionScope.locale.get("save")}</a>
			</shiro:hasPermission>
			<shiro:hasPermission name="submit_report">
			<a class="easyui-linkbutton" id="submitbtn_${reportid}" style="width:70px;height:28px" 
				onclick="beforesaveReport(${reportid},'${report.reportStatus}','${report.studyitems}','${report.sexdisplay}','submitReport');">提交</a>
			</shiro:hasPermission>
			<c:if test="${preAudit}">
			<shiro:hasPermission name="preaudit_report">
				<a class="easyui-linkbutton" id="preaudit_${reportid}" style="width:70px;height:28px"
				 onclick="beforesaveReport(${reportid},'${report.reportStatus}','${report.studyitems}','${report.sexdisplay}','auditPreReport');">初审</a>
			</shiro:hasPermission>	 
			</c:if>
			<shiro:hasPermission name="audit_report">
			<a class="easyui-linkbutton"  id="audibtn_${reportid}" style="width:70px;height:28px" 
				onclick="beforesaveReport(${reportid},'${report.reportStatus}','${report.studyitems}','${report.sexdisplay}','auditReport');">${sessionScope.locale.get("report.audit")}</a>
			</shiro:hasPermission>
			<c:if test="${enable_urgent}">
			<a class="easyui-linkbutton" style="width:70px;height:28px" onclick="goToUrgent(${studyorderfk},'worklist')">危急值</a>
			</c:if>
			<shiro:hasPermission name="open_Image">
				<c:if test="${enable_plaza_callup==1}">
					<a class="easyui-linkbutton" href="${plaza_loaddata}" style="width:70px;height:28px" onclick="closeReportFlag=false;urlFlag = true;">${sessionScope.locale.get("wl.image")}(p)</a>
				</c:if>
				<c:if test="${enable_via_callup==1}">
					<a class="easyui-linkbutton" href="${callviapara}" style="width:70px;height:28px" onclick="closeReportFlag=false;urlFlag = true;">${sessionScope.locale.get("wl.image")}(v)</a>
				</c:if>
			</shiro:hasPermission>
			<shiro:hasPermission name="print_report">
			<a class="easyui-menubutton" style="width:80px;height:28px" onClick="printReport('${projecturl}',${reportid},'${printername}',0,-1)";
				data-options="plain:false,menu:'#printmenu_${reportid}'">${sessionScope.locale.get("report.print")}</a>
			<div id="printmenu_${reportid}" style="width:100px;">
			    <div><a class="easyui-linkbutton" plain="true" onClick="printReport('${projecturl}',${reportid},'${printername}',0,-1)">${sessionScope.locale.get("report.print")}</a></div>
			    <div><a class="easyui-linkbutton" plain="true" onClick="previewReport(${reportid},true);">打印预览</a></div>
			    <div><a class="easyui-linkbutton" plain="true" onClick="openPrintImagesDlg(${reportid},${studyorderfk},'${studyid}');">打印图片</a></div>
			</div>
			<a id="printApp_${reportid}" href="printApp:-1" type="hidden" ></a>
			</shiro:hasPermission>
			<a class="easyui-linkbutton" style="width:70px;height:28px" onclick="apply(${studyorderfk},'worklist')">申请单</a>
			<shiro:hasPermission name="switch_template">
				<c:if test="${sr_support}">
					<a class="easyui-linkbutton" id="changetemplatebtn_${reportid}" style="width:80px;height:28px" 
						onclick="openSwitchTemplateDialog(${reportid},${studyorderfk},'${studyid}');">${sessionScope.locale.get("report.switchtemplate")}</a>
				</c:if>
			</shiro:hasPermission>
			<a class="easyui-linkbutton" style="width:80px;height:28px" onclick="goToComparetrace(${reportid});">痕迹对比</a>
			<a class="easyui-menubutton" id="moremenu_${reportid}" style="width:80px;height:28px" data-options="plain:false,menu:'#moremenuitems_${reportid}'">更多...</a>
			<div id="moremenuitems_${reportid}" style="width:150px;">
				<div onclick="process(${studyorderfk},'worklist');">检查流程</div>
				
				<shiro:hasPermission name="favorites_report">
				<div id="showRe_${reportid}" onclick="openFavoritesDialog(${report.reportid},${studyorderfk})">${sessionScope.locale.get("wl.favoritesreport")}</div>
				</shiro:hasPermission>
				
				<shiro:hasPermission name="reject_report">
				<div id="reject_${reportid}" onclick="beforeRejectReport(${reportid})">驳回</div>
				</shiro:hasPermission>
				<div id="printimage_btn_${reportid}" onclick="openPrintImagesDlg(${reportid},${studyorderfk},'${studyid}')">打印图片</div>
				<div onclick="openOtherSystem(${reportid});">其他系统</div>
				<div class="menu-sep"></div>
				<%
					ReportType[] types=ReportType.values();
					for(int i=0,len=types.length;i<len;i++){
						if(types[i]!=ReportType.STRUCTURED&&types[i]!=ReportType.NORMAL){
				%>
				<div onclick="normalReport(${reportid},${studyorderfk},'${studyid}',<%=types[i].getName()%>,'<%=types[i].getDisplayName()%>');"><%=types[i].getDisplayName()%></div>
				<%
						}
					}
				%>
			</div>
			<a class="easyui-linkbutton" ${plaza_closeexam} style="width:70px;height:28px" onclick="closeTab(${reportid});">${sessionScope.locale.get("report.close")}</a>
		</div>
      	<!--  工具窗口 -->
        <div id='tools' data-options="region:'west',split:true,hideCollapsedContent:false,href:'${ctx}/report/report_template?reportid=${reportid}&orderid=${studyorderfk}&studyid=${studyid}&modality=${modality}&patientid=${report.patientid}'" 
        	title="${sessionScope.locale.get("report.reporttemplate")}" style="width:245px;">
        </div>
     <!--    默认模板位置 -->
        <form name="reportform_${reportid}" id="reportform_${reportid}" method="POST">
        	<div data-options="region:'center'" style="padding:3px 3px;border-top-width: 0px;">
	        	<div id='report_panel_${reportid}' style="padding:1px 5px;margin-left:auto;margin-right:auto;width:95%;height:100%;" class="report_panel">
		        	<div id='flag_${reportid}' style="width:100%;" class="report_content_item">
		        		
		        		<img alt='' id="audi_image_${reportid}" src='${ctx}/image/flag.png' style='position:absolute;left:400px;top:10px;display:none;z-index:100000;'>
		        		<%-- <div style="margin-bottom: 10px;">
			        		<h2 style="text-align:center;"><b>${reporttitle}</b></h2>
			        		<!--<h3 style="text-align:center;"></h3>-->
		        		</div> --%>
		        		<div style="margin-bottom: 1px;width:230px;float:left;">
		        			<span style="display:inline-block;width:${span_width}px;"><b>${sessionScope.locale.get("report.patientname")}：</b></span>
		        			<span style="display:inline-block;width:155px;">${report.patientname}</span>
		        		</div>
		        		<div style="margin-bottom: 1px;width:230px;float:left;">
		        			<span style="display:inline-block;width:${span_width}px;"><b>性别：</b></span>
		        			<span style="display:inline-block;width:120px;">${report.sexdisplay}</span>
		        		</div>
		        		<div style="margin-bottom: 1px;width:230px;float:left;">
		        			<span style="display:inline-block;width:${span_width}px;"><b>${sessionScope.locale.get("report.birthdate")}：</b></span>
		        			<span style="display:inline-block;width:${span1_width}px;">${report.birthdate}</span>
		        		</div>
		        		<div style="margin-bottom: 1px;width:230px;float:left;">
		        			<span style="display:inline-block;width:${span_width}px;"><b>${sessionScope.locale.get("report.studyid")}：</b></span>
		        			<span style="display:inline-block;width:120px;">${report.studyid}</span>
		        		</div>
		        		<div style="margin-bottom: 1px;width:230px;float:left;">
		        			<span style="display:inline-block;width:${span_width}px;"><b>年龄：</b></span>
		        			<span style="display:inline-block;width:120px;">${age}</span>
		        		</div>
		        		<div style="margin-bottom: 1px;width:230px;float:left;">
		        			<span style="display:inline-block;width:${span_width}px;"><b>${sessionScope.locale.get("report.patientid")}：</b></span>
		        			<span style="display:inline-block;width:${span1_width}px;">${report.patientid}</span>
		        		</div>
		        		<div style="margin-bottom: 1px;width:230px;float:left;">
		        			<span style="display:inline-block;width:${span_width}px;"><b>门诊号：</b></span>
		        			<span style="display:inline-block;width:120px;">${report.outno}</span>
		        		</div>
		        		<div style="margin-bottom: 1px;width:230px;float:left;">
		        			<span style="display:inline-block;width:${span_width}px;"><b>住院号：</b></span>
		        			<span style="display:inline-block;width:120px;">${report.inno}</span>
		        		</div>
		        		<div style="margin-bottom: 1px;width:230px;float:left;">
		        			<span style="display:inline-block;width:${span_width}px;"><b>检查时间：</b></span>
		        			<span style="display:inline-block;width:${span1_width}px;">${report.studytime}</span>
		        		</div>
		        		<div style="margin-bottom: 1px;width:230px;float:left;">
		        			<span style="display:inline-block;width:${span_width}px;"><b>检查设备：</b></span>
		        			<span style="display:inline-block;width:${span1_width}px;">${report.modality_name}</span>
		        		</div>
		        		<div style="margin-bottom: 1px;height:19px;width:230px;float:left;">
		        			<span style="display:inline-block;width:${span_width}px;"><b>申请科室：</b></span>
		        			<span title="${report.appdeptname}" class="easyui-tooltip" 
		        				style="display:inline-block;line-height:1;width:155px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">${report.appdeptname}</span>
		        		</div>
		        		<div style="margin-bottom: 1px;width:230px;float:left;">
		        			<span style="display:inline-block;width:${span_width}px;"><b>核素：</b></span>
		        			<span style="display:inline-block;width:120px;">${report.nuclidename}</span>
		        		</div>
		        		<div style="margin-bottom: 1px;height:19px;width:690px;float:left;">
		        			<span style="display:inline-block;width:${span_width}px;"><b>地址：</b></span>
		        			<span title="${report.address}" class="easyui-tooltip" 
		        				style="display:inline-block;line-height:1;width:350px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">${report.address}</span>
		        			<%-- <span style="display:inline-block;width:${span_width}px;"><b></b></span>
		        			<span style="display:inline-block;width:${span1_width}px;"></span> --%>
		        		</div>
		        		<div style="margin-bottom: 1px;width:100%;float:left;">
		        			<span class="easyui-tooltip" title ="${report.referencevalue}"><b>${sessionScope.locale.get("report.studyitem")}：</b></span>
	        				<input id="reportstudyitem_${reportid}" title="${sessionScope.locale.get("report.studyitem")}：" class="easyui-textbox easyui-tooltip" 
	        					name="studyitem" style="width:100%;height:26px;" maxlength="100" 
	        					value="${report.reportstudyitem}"/>
		        		</div>
		        	</div>
		        	<div style="margin-bottom: 1px;width:100%;float:left;">
						<b class="report_content_item">${sessionScope.locale.get("report.studymethod")}：</b>
						<!-- <input type="text" id="method_${reportid}" class="easyui-textbox" name="studymethod" style="width:100%;height:26px;"
		        			value="${report.studymethod}" data-options="validType:'length[0,100]',onChange:function(){$('#contentChangeflag_${reportid}').val(true);}"/> -->
		        		<input id="method_${reportid}" class="easyui-combobox" name="studymethod" style="width:100%;height:26px;" value="${report.studymethod}" 
							data-options="valueField: 'id',textField: 'name',panelHeight:'200px',url:'${ctx}/dic/findDicCommonFromCache?group=studymethod&modality=${modalityType}',
								onChange:function(){$('#contentChangeflag_${reportid}').val(true);}">
					</div>
		        	<div style="margin-bottom: 2px;width:100%;float:left;" class="report_desc_div" onclick="$('#report_select_${reportid}').val('desc');">
			        	<b class="report_content_item">核医学所见：</b>
					    <script id="desc_${reportid}_html" type="text/plain" name="checkdesc_html" class="_fontsize${desc_fontsize}" style="width:100%;height:${desc_h}px;"></script>
				   	</div>
				   	<div style="margin-bottom: 2px;width:100%;float:left;" class="report_result_div" onclick="$('#report_select_${reportid}').val('result');">
				   		<b class="report_content_item">核医学诊断：</b>
		           		<script id="result_${reportid}_html" type="text/plain" name="checkresult_html" class="_fontsize${result_fontsize}" style="width:100%;height:${res_h}px;"></script>
					</div>
					<div style="width:100%;height:28px;float:left;" class="report_content_item">
						<div style="width:180px;float : left;">
							<input class="easyui-radiobutton" id="neg_${reportid}" label="阴阳性： 阴性" labelWidth="95" labelAlign="left" name="pos_or_neg" data-options="checked:${pos_or_neg eq 'n' or empty pos_or_neg}" value="n">
							<input class="easyui-radiobutton" id="pos_${reportid}" label="阳性" labelWidth="40" labelAlign="right" name="pos_or_neg" data-options="checked:${pos_or_neg eq 'p'}" value="p">
						</div>
						<shiro:hasPermission name="audit_report">
						<div style="width:170px;float : right;">
							<input class="easyui-combobox" id="reportquality_${reportid}"  name="reportquality" style="width:170px;height:25px;" label="报告质量:" labelWidth="75px" labelAlign="right"
								data-options="editable:false,panelHeight:'auto',
								valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0027',
								onLoadSuccess:function(){$(this).combobox('select', '${report.reportquality}')}"/>
						</div>
						<div style="width:170px;float : right;">
							<input class="easyui-combobox" id="diagnosis_coincidence_${reportid}"  name="diagnosis_coincidence" style="width:170px;height:25px;" label="诊断符合:" labelWidth="75px" labelAlign="right"
								data-options="editable:false,panelHeight:'auto',
								valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0028',
								onLoadSuccess:function(){$(this).combobox('select', '${report.diagnosis_coincidence}')}"/>
						</div>
						</shiro:hasPermission>
						<div style="width:170px;float : right;">
							<input class="easyui-combobox" id="imagequality_${reportid}"  name="imagequality" style="width:170px;height:25px;" label="图像评级:" labelWidth="75px" labelAlign="right"
								data-options="editable:false,panelHeight:'auto',
								valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0025',
								onLoadSuccess:function(){$(this).combobox('select', '${report.imagequality}')}"/>
						</div>
					</div>
					<c:if test="${enable_urgent}">
					<div style="width:100%;height:28px;float:left;" class="report_content_item">
						<div style="width:150px;float : left;padding:3px 0px 0px 0px;">
                            <input class="easyui-checkbox" id="urgent_${reportid}" label="是否危急：" name="urgent" value="1" data-options="checked:${report.urgent eq '1' || report.urgent_status eq 'submitted' || report.urgent_status eq 'handled'},
                                    onChange:function(checked){
                                        checked?$('#urgent_explain_${reportid}').textbox('setText','${report.urgentexplain eq null ? report.urgent_txt: report.urgentexplain}').textbox('enable'):$('#urgent_explain_${reportid}').textbox('setText','').textbox('disable');
                                    }">
                        </div>
                        <div style="width:550px;float : right;">
                            <input type="text" id="urgent_explain_${reportid}" label="情况说明：" labelPosition="before" class="easyui-textbox" name="urgentexplain" style="width:100%;height:25px;" maxlength="100" 
                                        value="${report.urgentexplain eq null ? report.urgent_txt: report.urgentexplain}"  data-options="disabled:${report.urgent ne '1'}"/>
                        </div>
					</div>
					</c:if>
					
					<div style="width:100%;height:30px;float:left;" class="report_content_item">
						<div style="margin-bottom: 2px;">
		        			<span style="display:inline-block;width:83px;"><b>${sessionScope.locale.get("report.reportphysician")}：</b></span>
		        			<span id="reportphysician_${reportid}" style="display:inline-block;width:138px;">${report.reportphysician_name}</span>
		        			<span style="display:inline-block;width:83px;"><c:if test="${preAudit}"><b>初审医生：</b></c:if></span>
		        			<c:if test="${preAudit}">
		        			  <span id="pre_auditphysician_name_${reportid}" style="display:inline-block;width:138px;">${report.pre_auditphysician_name}</span>
		        			</c:if>
		        			<span style="display:inline-block;width:83px;"><b>${sessionScope.locale.get("report.auditphysician")}：</b></span>
		        			<span id="auditphysician_${reportid}" style="display:inline-block;width:138px;">${report.auditphysician_name}</span>
		        		</div>
						<div style="margin-bottom: 2px;">
		        			<span style="display:inline-block;width:83px;"><b>${sessionScope.locale.get("report.reportdatetime")}：</b></span>
		        			<span id="reporttime_${reportid}" style="display:inline-block;width:138px;"><fmt:formatDate value='${report.reporttime}' pattern='yyyy-MM-dd HH:mm:ss'/></span>
		        			<span style="display:inline-block;width:83px;"><c:if test="${preAudit}"><b>初审时间${report.auditphysician}：</b></c:if></span>
		        			<c:if test="${preAudit}"><span id="pre_audittime_${reportid}" style="display:inline-block;width:138px;"><fmt:formatDate value='${report.pre_audittime}' pattern='yyyy-MM-dd HH:mm:ss'/></span></c:if>
		        			<span style="display:inline-block;width:83px;"><b>${sessionScope.locale.get("report.auditdatetime")}：</b></span>
		        			<span id="audittime_${reportid}" style="display:inline-block;width:138px;"><fmt:formatDate value='${report.audittime}' pattern='yyyy-MM-dd HH:mm:ss'/></span>
		        		</div>
		        	</div>
		        	<div style="width:100%;height:10px;">
		        	</div>
		        	<input id="id_${reportid}" name="id" type="hidden" value="${report.reportid}">
		        	<input id="auditphysicianid_${reportid}" type="hidden" value="${report.auditphysician}">
		        	<input id="reportphysician_name_${reportid}" type="hidden" value="${report.reportphysician_name}">
		        	<input id="studyid_${reportid}" name="studyid" type="hidden" value="${studyid}">
		        	<input id="orderid_${reportid}" name="studyorderfk" type="hidden" value="${studyorderfk}"> 
		        	<input id="desc_${reportid}_txt" name="checkdesc_txt" type="hidden">
		        	<input id="result_${reportid}_txt" name="checkresult_txt" type="hidden">
		        	<input id="orderStatus_${reportid}" type="hidden" name="reportstatus" value="${report.reportStatus}">
		        	<input id="publiclabel_${reportid}" name="publiclabel" type="hidden" value="">
		        	<input id="privatelabel_${reportid}" name="privatelabel" type="hidden" value="">
		        	<input id="icd10label_${reportid}" name="icd10label" type="hidden" value="">
		        	<input id="savelabel_flag_${reportid}" name="savelabel_flag" type="hidden" value="">
		        	<input name="studyitem_${reportid}" type="hidden" value="${report.studyitems}">
		        	<input id="modality_${reportid}" type="hidden" value="${modality}">
		        	<input id="patientid_${reportid}" type="hidden" value="${report.patientid}">
		        	<input id="desc_fontsize_${reportid}" name="desc_fontsize" type="hidden" value="${desc_fontsize}">
		        	<input id="result_fontsize_${reportid}" name="result_fontsize" type="hidden" value="${result_fontsize}">
		        	<input id="sexdisplay_${reportid}" type="hidden" value="${report.sexdisplay}">
		        	<input id="enable_refresh_${reportid}" type="hidden">
		        </div>
        	</div>
        </form>
        
        <div data-options="region:'east',split:true,hideCollapsedContent:false,href:'${ctx}/report/report_Assistant_Panel?studyid=${studyid}&orderid=${studyorderfk}&modality=${modality}&patientidfk=${report.patientidfk}&reportid=${reportid}',
        		collapsed:${report_assistant_collapsed==1?true:false},
          		onExpand:function(){saveUserProfiles_value('0','report_assistant_collapsed')},
           		onCollapse:function(){saveUserProfiles_value('1','report_assistant_collapsed')}" 
        	style="width:260px;" title="${sessionScope.locale.get("report.reportassistantpanel")}"> 
        </div>
	</div>
        <!-- 申请单查看窗口 -->
        <div class="gallerys" style='display:none'>
			<img id="imageShow_${reportid}" src="" class="gallery-pic" onclick="$.openPhotoGallery(this)"/>
		</div>
		<input id='contentChangeflag_${reportid}' type='hidden'/>
		<input id='report_printmore' type='hidden' value='${report_printmore}'>
		<input id='report_select_${reportid}' type='hidden' value="desc">
		
