<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	
<div style="display: none;" ><input type="text" id="value_${reportid}" ></div>

	<div class="easyui-layout" data-options="fit:true,border:false">
		<div data-options="region:'north',border:false" style="height:34px;padding:2px;text-align:right;">
		
	
			<a class="easyui-linkbutton" style="width:80px;height:28px" onclick="apply_webView('${studyorderfk}')">申请单</a>
			
			<a class="easyui-linkbutton" style="width:80px;height:28px" onclick="process_webView(${studyorderfk});">检查流程</a>
			
			<a class="easyui-linkbutton" href="${para}" style="width:80px;height:28px" onclick="">${sessionScope.locale.get("wl.image")}</a>
			
			<a class="easyui-menubutton" style="width:85px;height:28px" onClick="printReport('${projecturl}',${reportid},'${printername}',0)";
				data-options="plain:false,menu:'#printmenu_${reportid}'">${sessionScope.locale.get("report.print")}</a>
			
			<div id="printmenu_${reportid}" style="width:150px;">
			    <div><a class="easyui-linkbutton" plain="true" onClick="printReport('${projecturl}',${reportid},'${printername}',0)">${sessionScope.locale.get("report.print")}</a></div>
			    <div><a class="easyui-linkbutton" plain="true" onClick="print(${reportid},'0');">打印预览</a></div>
			</div>
			
			<a id="printApp_${reportid}" href="printApp:-1" type="hidden" ></a>
 	
			<a class="easyui-linkbutton" style="width:80px;height:28px" onclick="closeTab(${reportid});">${sessionScope.locale.get("report.close")}</a>
		</div>

     <!--    默认模板位置 -->
        <form name="reportform_${reportid}" id="reportform_${reportid}" method="POST">
        	<div data-options="region:'center'" style="padding:5px 5px;">
	        	<div id='defaultTemplet' style="padding:1px 20px 10px;margin-left:auto;margin-right:auto;width:700px;height:98%;background-color:#FFFFFF;">
		        	<div id='flag_${reportid}' style="width:100%;">
		        		
		        		<img alt='' id="audi_image_${reportid}" src='${ctx}/image/flag.png' style='position:absolute;left:400px;top:10px;display:none;'>
		        		 <div>
			        		<h2 style="text-align:center;">${reporttitle}</h3>
			        		<!--<h3 style="text-align:center;"></h3>-->
		        		</div> 
		           		<table style="width:100%;height:65px;" cellspacing="0" border="0">
							<tr>
								<td><b>${sessionScope.locale.get("report.studyid")}：</b></td>
								<td align="left">${studyid}</td>
								<td></td>
								<td></td>
								<td><b>${sessionScope.locale.get("report.patientid")}：</b></td>
								<td>${report.patientid}</td>
								<!-- <td style="width:80px;"><b>门 诊 号：</b></td>
								<td><span id="_outno" >${outno}</span></td>
								<td style="width:80px;"><b>住 院 号：</b></td>
								<td><span id="_inno" >${inno}</span></td> -->
							</tr>
							<tr>
								<td><b>${sessionScope.locale.get("report.patientname")}：</b></td>
								<td>${report.patientname}</td>
								<td><b>${sessionScope.locale.get("report.sex")}：</b></td>
								<td align="left">${report.sexdisplay}</td>
								<td><b>${sessionScope.locale.get("report.birthdate")}：</b></td>
								<td>${report.birthdate}</td>
							</tr>
							<tr>
								<td><b>${sessionScope.locale.get("report.studyitem")}：</b></td>
								<td colspan="5"  ><span id="_studyitem" >${report.studyitems}</span></td>
								<!--<td><b>床        号：</b></td>
								<td colspan="3">${bedno}</td>-->
							</tr>
						</table>
		        	</div>
		        	<!-- p><hr /></p-->
		        	<div style="margin-bottom: 5px;">
						<b>${sessionScope.locale.get("report.studymethod")}：</b><br>
						<span style="width:100%;height:28px;">${report.studymethod}</span>
					</div>
		        	<div style="margin-bottom: 5px;">
			        	<b>${sessionScope.locale.get("wl.reportdesc")}：</b>
			        	<article style="width:width:700px;height:260px;">${report.checkdesc_html}</article>
				   	</div>
				   	<div style="margin-bottom: 2px;">
				   		<b>${sessionScope.locale.get("wl.reportresult")}：</b>
				   		<article style="width:width:700px;height:160px;">${report.checkresult_html}</article>
					</div>
					
					<div title="West" style="width:100%;height:60px;">
						
						<table style="width:705px;padding:0px;" border="0">
							<tr style="width:705px;padding:0px;">
								
								<td><b>阴阳性：</b></td>
								<c:choose>
									<c:when test="${report.pos_or_neg eq 'p'}">
										<td>阳性</td>
									</c:when>
									<c:otherwise>
										<td>阴性</td>
									</c:otherwise>
								</c:choose>
								
								<td style="width:90px;"><b>是否危急：</b></td>
								<td style="width:20px;">
									<c:choose>
										<c:when test="${report.urgent eq '1'}">
											是
										</c:when>
										<c:otherwise>
											否
										</c:otherwise>
									</c:choose>
								</td>
								<td style="width:90px;text-align:right;"><b>情况说明：</b></td>
								<td style="width:350px;" align="right">
									<span style="width:100%;height:25px;">${report.urgentexplain}</span>
								</td>
							</tr> 
							
						</table>
						
						<div style="margin-bottom: 2px;">
						<b>图像评级：</b><span style="width:100%;height:25px;">${report.imagequality}</span>
						</div>
						
		           		<table style="width:100%;" border="0">
							<tr>
								<td style="height:22px;"><b>${sessionScope.locale.get("report.reportphysician")}：</b></td>
								<td><span id="reportphysician_${reportid}" align="left">${report.reportphysician_name}</span></td>
								<td style="height:22px;"><b>初审医生：</b></td>
								<td><span id="pre_auditphysician_name_${reportid}" align="left">${report.pre_auditphysician_name}</span></td>
								<td><b>${sessionScope.locale.get("report.auditphysician")}：</b></td>
								<td><span id="auditphysician_${reportid}" align="left">${report.auditphysician_name}</span></td>
								
							</tr>
							<tr>
								<td><b>${sessionScope.locale.get("report.reportdatetime")}：</b></td>
								<td style="width:140px;"><span id="reporttime_${reportid}"><fmt:formatDate value='${report.reporttime}' pattern='yyyy-MM-dd HH:mm:ss'/></span></td>
								<td><b>初审时间：</b></td>
								<td style="width:140px;"><span id="pre_audittime_${reportid}"><fmt:formatDate value='${report.pre_audittime}' pattern='yyyy-MM-dd HH:mm:ss'/></span></td>
								<td><b>${sessionScope.locale.get("report.auditdatetime")}：</b></td>
								<td style="width:140px;"><span id="audittime_${reportid}"><fmt:formatDate value='${report.audittime}' pattern='yyyy-MM-dd HH:mm:ss'/></span></td>
							</tr>
						</table>
		        	</div>
		        	<input id="id_${reportid}" name="id" type="hidden" value="${report.reportid}">
		        	<input name="studyid" type="hidden" value="${studyid}">
		        	<input id="orderid_${reportid}" name="studyorderfk" type="hidden" value="${studyorderfk}">
		        	<input id="orderStatus_${reportid}" type="hidden" name="reportstatus" value="${report.reportStatus}">
		        	<input name="studyitem" type="hidden" value="${report.studyitems}">
		        	<!--  <input id="re_${studyid}" type="hidden" value="">
		        	<input id="de_${studyid}" type="hidden" value="">-->
		        	
		        </div>
        	</div>
        </form>
        

	</div>
        <!-- 申请单查看窗口 -->
        <div class="gallerys" style='display:none'>
			<img id="imageShow_${reportid}" src="" class="gallery-pic" onclick="$.openPhotoGallery(this)"/>
		</div>
