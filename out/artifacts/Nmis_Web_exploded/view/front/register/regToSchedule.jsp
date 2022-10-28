<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title></title>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">	
	<!-- <div data-options="region:'south'" style="height:38px;">
		<div data-options="fit:true" style="padding:3px;text-align:center;margin-right:auto;">
        	<a class="easyui-linkbutton" style="width:150px;height:28px" onclick="turnIntoSchedule()">保存</a>
        	<a class="easyui-linkbutton" style="width:150px;height:28px" onClick="closeScheduleDlg()">关闭</a>
        </div>
    </div> -->
	<div data-options="region:'north',border:false" style="height:65px;padding:5px;">
		<input id="studyorderid_sc" name="studyorder.id" type="hidden" value="${order.orderid}"/>
		<input id="appointmenttime_sc" name="studyorder.appointmenttime" type="hidden"/>
		<table style="width:100%; font-size:13px;" cellspacing="0">
  			<tr>
	  			<td style="width:110px;height:25px;">
	  				<label>病人编号：</label>
	  			</td>
	  			<td>
	  				<input class="easyui-textbox" id="studyid_sc" style="width:110px;height:25px;"
	  				 readonly="readonly" value="${order.patientid}">
	  			</td>
	  			<td style="width:110px;height:25px;">
	  				<label>检查编号：</label>
	  			</td>
	  			<td>
	  				<input class="easyui-textbox" id="studyid_sc" style="width:110px;height:25px;"
	  				 readonly="readonly" value="${order.studyid}">
	  			</td>
	  			<td style="width:110px;height:25px;">
	  				<label>病人姓名：</label>
	  			</td>
	  			<td>
	  				<input class="easyui-textbox" id="" style="width:110px;height:25px;"
	  				 readonly="readonly" value="${order.patientname}">
	  			</td>
  			</tr>
  			<tr>
  				<td style="width:110px;height:25px;">
	  				<label>检查类型：</label>
	  			</td>
	  			<td>
	  				<input class="easyui-textbox" id="modalityType_sc" style="width:110px;height:25px;"
	  				 readonly="readonly" value="${order.modality_type}">
	  			</td>
	  			<td style="width:110px;height:25px;">
					<label>检查设备：</label>
				</td>
				<td>
					<input id="modality_dic_sc" name="studyorder.modalityid" class="easyui-combobox" name="language" 
						style="height:25px;width:110px;" required="true"
						data-options="valueField: 'id',textField: 'modality_name',
						method:'get',editable:false,
	                   	onChange:handleOnChange_modality,
	                   	url:'${ctx}/dic/getEquipmentByStudyitem?modality=${order.modality_type}&orderid=${order.orderid}',
	                   	onLoadSuccess:function(){$(this).combobox('select', '${order.modalityid}')}">
	            </td>
  			</tr>
  		</table>
    </div>
	<div data-options="region:'center',border:false" title="检查项目">
		
		<!-- <div class="easyui-panel" title='' style="padding:1px;height:100%;width:100%;border:none;"> -->
		<%-- <form id="scheduleForm_sc" name="scheduleForm" method="POST">
			
  			<table>
	  			<tr>
		  			
		  			<td style="width:110px;height:25px;">
		  				<label>申请科室：</label>
		  			</td>
		  			<td>
		  				<input class="easyui-textbox" id="" style="width:110px;height:25px;"
		  				 readonly="readonly" value="${appdept}">
		  			</td> 
		  			 <td colspan="6">--%>
		  				<table id="studydgitem_sc" name="studyitem" class="easyui-datagrid" style="width:600px;height:150px;" 
							data-options="url:'${ctx}/register/getStudyItem?orderid=${order.orderid}',rownumbers: true,singleSelect: true,fit:true,fitColumns:true,border:false">
					        <thead>
					            <tr>
					                <th data-options="field:'modality',width:80">检查类型</th>
					                <th data-options="field:'item_name',width:200">检查项目</th>
					                <th data-options="field:'price',width:80">计价金额</th>
					                <th data-options="field:'realprice',width:80,align:'right',editor:{type:'numberbox',options:{precision:2}}">实收金额</th>
					                <th data-options="field:'charge_status',width:80,align:'right'">缴费状态</th>
					            </tr>
					        </thead>
					    </table>
		  			<%-- </td>
		  		</tr>
		
		  		<tr>
		  			
		            <td style="width:110px;height:25px;">
		  				<label>申请医生：</label>
		  			</td>
		  			<td>
		  				<input class="easyui-textbox" id="" style="width:110px;height:25px;"
		  				 readonly="readonly" value="${appdoctor}">
		  			</td> 
	  			</tr>
	  			
			</table>
		</form>--%>
		<!-- </div> -->
		
	</div>
	<div data-options="region:'south',border:false" title="请选择预约时间" style="height:250px;">
		<table>
			<tr>
				<td>
					<div id="calendar_sc" class="easyui-calendar" style="width:190px;height:210px;" title="预约日期："
					 data-options="onSelect:handleSelect_gettime,
			            validator: function(date){
			                if (date.getTime() > new Date().getTime()){
			                    return true;
			                } else {
			                    return false;
			                }
			            }
			            ">
			        </div>
	            </td>
				<td colspan="5">
	            	<div id="schedule_time_panel" class="easyui-panel" style="width:600px;height:210px;">
	            		无可选时间，请选择其他日期/切换检查设备。
					</div>	
	            </td>
			</tr>
		</table>
    </div>
</div>
</body>
</html>