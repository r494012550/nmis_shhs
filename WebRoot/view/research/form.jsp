<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<c:set var="input_width" value="180"/>
<c:set var="label_width" value="80"/>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body width="100%" height="100%">
<link rel="stylesheet" type="text/css" href="${ctx}/css/structreport.css?v=${vs}">
<script type="text/javascript" src="${ctx}/js/front/srCompHandle.js">
</script>
	<!-- 结构化模板填充位置 -->
	<div class="easyui-layout" id="" data-options="fit:true">
		<div data-options="region:'north',border:false" style="height:${not empty data.error_import?120:70}px;padding:2px;text-align:center;">
			<c:if test="${not empty data.error_import}">
			<h3 id="importerror_${dataid}" style="text-align:left;padding:0px 5px;color:#f3331f;">错误：${data.error_import}</h3>
			</c:if>
			<div style="text-align:left;padding:0px 5px;">
				<span style="font-size:14px;font-weight:bold;">患者信息</span>
			</div>
			<div style="width:100%;">
				<div class="project_content_div">
		    		<input class="easyui-textbox" id="patientid_form_${dataid}" name="patientid" label="病人编号:" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
		    			prompt="自动编号" data-options="value:'${data.patientid}'">
		    	</div>
		    	<div class="project_content_div">
		    		<input class="easyui-textbox" id="patientname_form_${dataid}" name="patientname" label="病人姓名:" labelWidth="${label_width+1}" labelAlign="right" style="height:25px;width:${input_width}px;" 
		    			required="true" data-options="value:'${data.patientname}',validType:'isSpace'">
		    	</div>
		    	<div class="project_content_div">
		    		<%-- <input class="easyui-combobox" id="sex_form_${dataid}" name="sex"  label="性别:" labelWidth="${label_width-30}" labelAlign="right" style="height:25px;width:${input_width-50}px;"
		    			data-options="value:'${data.sex}',valueField:'code',panelHeight:'auto',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0001',editable:false,onLoadSuccess:function(){$(this).combobox('select', 'M')}" required="true">
		    		 --%>
					|<a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'g1_sex',plain:true,selected:${data==null||data.sex==null||data.sex=='M'?true:false}" onclick="$('#sex_form_${dataid}').val('M');" style="height:25px;">男</a>
     				<a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'g1_sex',plain:true,selected:${data.sex=='F'?true:false}" onclick="$('#sex_form_${dataid}').val('F');" style="height:25px;">女</a>
     				<a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'g1_sex',plain:true,selected:${data.sex=='O'?true:false}" onclick="$('#sex_form_${dataid}').val('O');" style="height:25px;">其他</a>
     				<input id="sex_form_${dataid}" name="sex" type="hidden" value='${data.sex}'/>|
		    	
		    	</div>
		    	<div class="project_content_div">
		    		<div style="float: left;">
		    		<input class="easyui-numberbox" id="age_form_${dataid}" name="age" type="text"  label="年龄:" labelWidth="${label_width-30}" labelAlign="right" style="width:${(input_width-label_width)/2+label_width-20}px;height:25px;" 
		    			data-options="value:'${data.age}',min:1,max:200,onChange:function(newv,oldv){calculate_age1(newv,'${dataid}')}" required="true"/>
		    		</div>
		    		<div style="float: left;">
					<input class="easyui-combobox" id="age_unit_form_${dataid}" name="ageunit" style="width:${(input_width-label_width)/2}px;height:25px;" 
						data-options="value:'${data.age_unit}',valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0008',editable:false,panelHeight:'auto',
							onLoadSuccess:function(){$(this).combobox('select', 'Y')},onChange:function(newv,oldv){calculate_age2(newv,'${dataid}')}" required="true"/>
					</div>
		    	</div>
		    	<div class="project_content_div">
		    		<input class="easyui-datebox" id="birthdate_form_${dataid}" name="birthdate" label="出生日期:" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width+10}px;" 
		    			required="required" data-options="value:'${data.birthdate}',editable:false,onSelect:function(value){getAgeFromBirthdate(value,'${dataid}')}">
		    	</div>
		    	<div class="project_content_div">
		    		<input class="easyui-textbox" id="studyid_form_${dataid}" name="studyid" label="检查编号:" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width+20}px;" 
		    			prompt="检查编号" data-options="value:'${data.studyid}'">
		    	</div>
		    	<div class="project_content_div">
		    		<input class="easyui-datetimebox" id="studydatetime_form_${dataid}" name="study_datetime" label="检查时间:" labelWidth="${label_width}" labelAlign="right" 
		    			style="height:25px;width:250px;" data-options="value:'${data.study_datetime}'">
		    	</div>
			</div>
		</div>
		<div data-options="region:'center'" title="表单内容"  id="sr_div_${dataid}" style="border-top-width: 0px;border-bottom-width: 0px;border-right-width: 0px;">
			<div class="mydiv" style="width:90%;height:100%;margin-left:auto;margin-right:auto;">
				<%-- <form name="formdataform" id="formdataform_${dataid}" method="POST"> --%>
				<div id="sr_container_${dataid}" class="easyui-panel sr_container" data-options="border:false" 
					style="background-color:#FFFFFF;padding:5px;">
				</div>
				<!-- </form> -->
			</div>
		</div>
		<div data-options="region:'south',border:false" style="height:32px;padding:2px;text-align:center;">
			<shiro:hasPermission name="submit_form_data">
			<a class="easyui-linkbutton" id="submitbtn_${dataid}"  data-options="disabled:${status eq 31 ?true:false}" style="width:80px;height:28px" 
				onclick="submitFormData('${dataid}');">提交</a>
			</shiro:hasPermission>
			<shiro:hasPermission name="audit_form_data">
			<a class="easyui-linkbutton"  id="audibtn_${dataid}" style="width:80px;height:28px"  data-options="disabled:${status eq 31 ?true:false}"
				onclick="auditFormData('${dataid}');">${sessionScope.locale.get("report.audit")}</a>
			</shiro:hasPermission>
			<shiro:hasPermission name="cancel_audit_form_data">
			<a class="easyui-linkbutton" id="cancebtn_${dataid}" style="width:80px;height:28px" data-options="disabled:${status eq 31 ?false:true}"
				onclick="cancelAuditFormData('${dataid}');">取消审核</a>
			</shiro:hasPermission>	
			<%-- <a class="easyui-linkbutton" style="width:80px;height:28px" onClick="printFormData('${dataid}',-1);">打印预览</a> --%>
			<a class="easyui-linkbutton" style="width:80px;height:28px" onclick="closeTab('${dataid}');">${sessionScope.locale.get("report.close")}</a>
		</div>
	</div>
	<input id="form_id_${dataid}" name="id" type="hidden" value="${dataid}"/>
	<input id="form_formid_${dataid}" name="formid" type="hidden" value="${formid}"/>
	<input id="form_groupid_${dataid}" name="groupid" type="hidden" value="${groupid}"/>
	<input id="form_projectid_${dataid}" name="projectid" type="hidden" value="${projectid}"/>
	<input id='contentChangeflag_${dataid}' type='hidden'/>
	<input id="type_${dataid}" name="type" type="hidden" value="${type}"/>
</body>
</html>
