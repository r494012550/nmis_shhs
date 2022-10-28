<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body oncontextmenu="return false;">
<c:choose>
	<c:when test="${report.template_id > 0}">
		<div style="width: 710px;border: none;">
			${report.checkdesc_html}
		</div>
	</c:when>
	<c:otherwise>
		<div style="width: 800px;border: none;">
			<div >
				<div style="padding:5px;">
					<label style="font-weight: bold;">	${sessionScope.locale.get("report.studymethod")}：</label> 
					${report.studymethod}
				</div>
			</div>
			
			<div>
				<div style="padding:5px;">
					<label style="font-weight: bold;">${sessionScope.locale.get("wl.reportdesc")}：</label>
				</div>
				<div style="padding-left: 10px; padding-right: 10px;">
					${report.checkdesc_html}
				</div>
			</div>
			
			<div >
				<div style="padding:5px;">
					<label style="font-weight: bold;">${sessionScope.locale.get("wl.reportresult")}：</label>
				</div>
				<div style="padding-left: 10px; padding-right: 10px;">
					${report.checkresult_html}
				</div>
			</div>
			<div style="padding:5px;">
				<label style="font-weight: bold;">阴阳性：</label>
				<c:choose>
					<c:when test="${report.pos_or_neg eq 'p'}">
						<label>阳性</label>
					</c:when>
					<c:otherwise>
						<label>阴性</label>
					</c:otherwise>
				</c:choose>
			</div>
			<div style="padding:5px;">
				<label style="font-weight: bold;">图像评级：</label>
				<label>${report.imagequality}</label>	
			</div>
			<div style="height: 100%;margin-top: 1px;">
					<div style="padding:5px; ">
						<label style="font-weight: bold;">是否危急：</label>
						<label>
							<c:choose>
									<c:when test="${report.urgent eq '1'}">
										是
									</c:when>
									<c:otherwise>
										否
									</c:otherwise>
							</c:choose>
						</label>
						<label style="margin-left: 100px;font-weight: bold;">情况说明：</label>
						<label>${report.urgentexplain}</label>
					</div>
			</div>
			
			<div style="width: 100%;height:25px;margin-top: 1px;padding: 5px;">
				<div style="width: 250px;float: left;">
					<b>${sessionScope.locale.get("report.reportphysician")}：</b>${report.reportphysician_name}
				</div>
				<c:if test="${preAudit}">
					<div style="width: 250px;float: left;">
						<b>初审医生：</b>${report.pre_auditphysician_name}
					</div>
				</c:if>
				<div style="width: 250px;float: right;">
					<b>${sessionScope.locale.get("report.auditphysician")}：</b>${report.auditphysician_name}
				</div>
			</div>
			<div style="width: 100%;height:25px;margin-top: 1px;padding: 5px;">
				<div style="width: 250px;float: left;">
					<b>${sessionScope.locale.get("report.reportdatetime")}：</b><span id="reporttime_${studyorderfk}"><fmt:formatDate value='${report.reporttime}' pattern='yyyy-MM-dd HH:mm:ss'/></span>
				</div>
				<c:if test="${preAudit}">
					<div style="width: 250px;float: left;">
						<b>初审时间：</b><span id="pre_audittime_${studyorderfk}"><fmt:formatDate value='${report.pre_audittime}' pattern='yyyy-MM-dd HH:mm:ss'/></span>
					</div>
				</c:if>
				<div style="width: 250px;float: right;">
					<b>${sessionScope.locale.get("report.auditdatetime")}：</b><span id="audittime_${studyorderfk}"><fmt:formatDate value='${report.audittime}' pattern='yyyy-MM-dd HH:mm:ss'/></span>
				</div>
			</div>
	
	</div>
	</c:otherwise>
</c:choose>
	
</body>
</html>