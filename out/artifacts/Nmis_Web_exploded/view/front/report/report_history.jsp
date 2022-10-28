<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
<div style="padding: 10px;">
 	<div style="margin-bottom:5px">
 		<table style="width:100%;" cellspacing="0" border="0">
			<tr>
	 				<td style="width:80px;"><b>患者姓名:</b></td>
	 				<td ><span align="left">${report.patientname}</span></td>
	 				<td style="width:80px;"><b>性别:</b></td>
	 				<td ><span align="left">${report.sex}</span></td>
	 				<td style="width:80px;"><b>出生日期:</b></td>
	 				<td ><span align="left">${report.birthdate}</span></td>
	 				<td >
						<c:if test="${enable_plaza_callup==1}">
							<a class="easyui-linkbutton" href="${plaza_loaddata}" style="width:80px;height:28px" onclick="closeReportFlag=false;urlFlag = true;">${sessionScope.locale.get("wl.image")}(p)</a>
						</c:if>
						<c:if test="${enable_via_callup==1}">
							<a class="easyui-linkbutton" href="${callviapara}" style="width:80px;height:28px" onclick="closeReportFlag=false;urlFlag = true;">${sessionScope.locale.get('wl.image')}(v)</a>
						</c:if>
					</td>
	 			</tr>
	 			<tr>
	 				<td style="width:80px;"><b>患者编号:</b></td>
	 				<td ><span align="left">${report.patientid}</span></td>
	 				<td style="width:80px;"><b>年龄:</b></td>
	 				<td ><span align="left">${report.age}</span></td>
	 				<td style="width:80px;"><b>${sessionScope.locale.get('report.studyitem')}:</b></td>
					<td ><span align="left">${studyitem}</span></td>
					<td><a class="easyui-linkbutton" style="width:80px;height:28px" onclick="apply(${studyorderfk},'worklist')">申请单</a></td>
	 			</tr>
		</table>
    	
    </div>
	

<c:choose>
	<c:when test="${report.template_id > 0}">
		<div style="width: 710px;border: none;">
			${report.checkdesc_html}
		</div>
	</c:when>
	<c:otherwise>
    <div style="margin-bottom:5px">
    	<div  class="easyui-panel" style="width:100%;height:450px;padding:5px;" data-options="">
		    <p><b>【${sessionScope.locale.get('report.studymethod')}】</b></p>
		    <p>${report.studymethod}</p>
		    <p><b>【核医学所见】</b></p>
		    <p>${report.checkdesc_txt}</p>
		    <p><b>【核医学诊断】</b></p>
		    <p>${report.checkresult_txt}</p>
		</div>
    
    </div>
    <div style="margin-bottom:5px">
    	<table style="width:100%;height:55px;" cellspacing="2" border="0">
			<tr>
				<td style="width:90px;"><b>${sessionScope.locale.get('report.reportphysician')}：</b></td>
				<td ><span align="left">${report.reportphysician_name}</span></td>
				<td style="width:90px;"><b>${sessionScope.locale.get('report.auditphysician')}：</b></td>
				<td ><span align="left">${report.auditphysician_name}</span></td>
				
			</tr>
			<tr>
				<td style="width:90px;"><b>${sessionScope.locale.get('report.reportdatetime')}：</b></td>
				<td ><span>${report.reporttime}</span></td>
				<td style="width:90px;"><b>${sessionScope.locale.get('report.auditdatetime')}：</b></td>
				<td ><span>${report.audittime}</span></td>
			</tr>
		</table>
    </div>
    </c:otherwise>
</c:choose>
     <input id="template_id_history_${mainReportid}" type="hidden" value="${report.template_id}">
</div>
</body>
</html>