<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<c:set var="input_width" value="200"/>
<c:set var="input_height" value="25"/>
<c:set var="label_width" value="80"/>
<c:set var="input_width_long" value="400"/>
<c:set var="number_width" value="120"/>
<title>Insert title here</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true" style="padding:0px;margin:auto;">
	<div data-options="region:'west', href:'${ctx}/inquiring/westSearch', onLoad:initWestsearch, hideCollapsedContent:false,headerCls:'panelHeaderCss_top'" style="width:410px;"></div>
	<div data-options="region:'center',border:false">
		<div class="easyui-layout" data-options="fit:true" style="padding:0px;margin:auto;">
			<div data-options="region:'south'" style="height:45px;">
				<div style="padding:5px 20px 5px 5px;text-align:left;margin:auto;">
					<a class="easyui-linkbutton inq_op_btn" style="width:80px;" id="printSave"  data-options="disabled:true" onclick="saveInterrogation()">保存</a>
					<a class="easyui-linkbutton inq_op_btn" style="width:80px;" id="printCall"  data-options="disabled:true" onclick="interrogationcalling();">叫号</a>
					<a class="easyui-linkbutton inq_op_btn" style="width:80px;"  id="printBagStickers"  data-options="disabled:true" onclick="printBagStickers()">打印袋贴</a>
					<a class="easyui-linkbutton inq_op_btn" style="width:100px;" id="printChecklist" data-options="disabled:true" onclick="printChecklist()">打印检查单</a>
					<a class="easyui-linkbutton inq_op_btn" style="width:80px;"  id="printMedicalHistory" data-options="disabled:true" onclick="printMedicalHistory()">打印病史</a>
					<a class="easyui-linkbutton inq_op_btn" style="width:80px;"  id="printAuthorization" data-options="disabled:true" onclick="printAuthorization()">同意书</a>
					<a class="easyui-linkbutton inq_op_btn" style="width:100px;" id="printScanning" data-options="disabled:true" onclick="triggerScan()">扫描申请单</a>
				<!-- 	<a class="easyui-linkbutton" style="width:100px;" onclick="printEMR()">电子病历</a> -->
					<a class="easyui-linkbutton inq_op_btn" id="process_btn" id="printInspection" data-options="disabled:true" onclick="toProcess();" style="width:80px;">检查流程</a>
					<a class="easyui-linkbutton inq_op_btn" id="remark_btn" id="printRemark" data-options="disabled:true" onclick="openRemarkDialog();" style="width:80px;">患者备注</a>
					<a class="easyui-linkbutton c2 inq_op_btn" style="width:80px;" id="printEmpty" data-options="disabled:true" onclick="clearPanelInfo()">清空</a>
					<a id="print_res" href="tool:-1" type="hidden" ></a>
				</div>
			</div>
			<form id="interrogationForm" method="post">
			<div data-options="region:'center',border:false">
				<div class="easyui-tabs" id="tabs_div" data-options="plain:true,narrow:true,tabHeight:30,tabWidth:140,tabPosition:'top',fit:true,border:false">
					<div title="简要病史" data-options="selected:true,border:false">
						<%@ include file="/view/front/inquiring/brief_history.jsp" %>	
					</div>
					<div title="既往病史" data-options="border:false">
						<%@ include file="/view/front/inquiring/past_medical_history.jsp"  %>	
					</div>
					<div title="历史报告" data-options="href:'${ctx}/inquiring/reporthistory', onLoad:initReportHistory, border:false">
						<%-- <%@ include file="/view/front/interrogation/report_history.jsp"  %> --%>
					</div>
				<%-- 	<div title="扫描附件" data-options="border:false">
						<%@ include file="/view/front/inquiring/scan_attachments.jsp"  %>
					</div> --%>
				</div>
			</div>
			<div data-options="region:'north',border:false,height:'auto'" style="padding:0px 0px 5px 0px;">
				<div style="margin-left:auto;margin-right:auto;">
					<%@ include file="/view/front/inquiring/patient_info.jsp"%>
				</div>
			</div>
			</form>
		</div>
	</div><!-- region:center -->
</div>
<script type="text/javascript">
</script>
</body>
</html>