<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true" style="padding:0px;margin:auto;">
	<div data-options="region:'west',title:'检查申请',hideCollapsedContent:false,href:'${ctx}/register/studyOrders',onLoad:initStudyOrders,headerCls:'panelHeaderCss_top'" style="width:350px;">
	
	</div>
	<div data-options="region:'center',border:false">
		<div class="easyui-layout" data-options="fit:true" style="padding:0px;margin:auto;">
			 <div data-options="region:'north',border:false" style="height:40px;">
				<div style="padding:5px 20px 5px 5px;text-align:right;margin:auto;">
				
					<!-- <a id="emergency_menubtn" class="easyui-menubutton" style="width:120px;" data-options="plain:false" onclick="emergencyRegister()">急诊登记</a> -->
						
			       	<shiro:hasPermission name="save_register">
			       	<a class="easyui-linkbutton" style="width:120px;" onclick="beforeSaveApply(false)">保存</a>
			       	</shiro:hasPermission>
			       	<shiro:hasPermission name="saveAndprint_register">
			       	<a class="easyui-linkbutton" style="width:150px;" onclick="beforeSaveApply(true)">保存并打印检查单</a>
			       	</shiro:hasPermission>
			       	<c:if test="${enable_scan_module}">
				       	<%-- <a href='reporttool:-c scan -n ${user_id} -i ${ip} -s ${scanurl}' id="launch_scaner" class="easyui-linkbutton" 
				       		style="width:120px;" autoLaunch="${auto_launch_scaner}">启动扫描仪</a> --%>
				       	<a class="easyui-linkbutton" style="width:120px;" onclick="triggerScan();">扫描</a>
			       	</c:if>
			       	
			       	<a id="print_reg" href="tool:-1" type="hidden" ></a>
			       	<a id="scan_reg" href="tool:-1" type="hidden" ></a>
			       	<!-- <a class="easyui-linkbutton" style="width:120px;height:28px" >打印检查单</a> -->
			       	<a class="easyui-linkbutton c2" style="width:120px;" onClick="cancelSave()">取消</a>
				</div>
			</div>
				        
			<div data-options="region:'center',border:false">
				<div style="margin-left:auto;margin-right:auto;width:990px;">
				<form name="registerForm" id="registerForm" method="POST">
					<%@ include file="/view/front/register/reg_patient_info.jsp"%>
					<%@ include file="/view/front/register/reg_admission_info.jsp"%>
					<%@ include file="/view/front/register/reg_study_info.jsp"%>
				</form>
				</div>
			</div><!-- region:center -->
			<!-- <div data-options="region:'west',title:'扫描申请单',split:true" style="width:100px;">
			
			</div> -->
		
		</div>
	
	</div>
	

   
</div>
			
	<div id="notesDlg_reg" class="easyui-dialog" style="width:300px;height:150px;border:0;"
				data-options="closed:true">
		<input id="notes_reg" name="notes" class="easyui-textbox" multiline="true" 
			style="width:298px;height:100%;border:none;" prompt="可以在此输入备注信息">
	</div>
	<input id="user_institution" name="user_institution" value="${user_institution}" type="hidden">
	
	<div id="printDlg_reg" class="easyui-dialog" style="width:300px;height:180px;padding:10px;" closed="true">
		<div style="margin-top: 2px;">
			<div class="messager-icon messager-question"></div>
			<span>请选择打印张数：</span>
		</div>
		<div style="margin-top: 10px;">
			<select class="easyui-combobox" id="copies_reg" style="width:90%;height:30px;"
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
	<div id="emergency_menu" style="width:100px;" type="hidden"></div>

<script type="text/javascript">
</script>
</body>
</html>