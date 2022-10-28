<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>Insert title here</title>
<body>
	<div class="easyui-layout" data-options="fit:true">
		<div data-options="region:'north',border:false" style="height:150px;">		
			<div style="margin-left:auto;margin-right:auto;width:330px;">
				<form id="studyorders_Form" method="post">
				<div style="margin-top: 3px;">
					<input class="easyui-textbox" id="patientname_orders" name="patientname" label="病人姓名：" labelWidth="80" labelAlign="right" 
						style="height:25px;width:330px;">
	    		</div>
	    		<div style="margin-top: 3px;">
					<input class="easyui-textbox" id="appno_orders" name="appno" label="申请编号：" labelWidth="80" labelAlign="right" 
						style="height:25px;width:330px;">
	    		</div>
	    		
	    		<div style="width: 100%;margin-top:3px;margin-bottom:3px;">
					<div style="float:left;">
						<input id="datefrom_stdord" name="datefrom" class="easyui-datebox" label="申请日期：" labelWidth="80" labelAlign="right" style="width: 195px;height:25px;" 
							data-options="value:'${yesterday}'">
					</div>
					<div style="float:right;">
						<input id="dateto_stdord" name="dateto" class="easyui-datebox" label="-" labelWidth="12" labelAlign="right" style="width: 125px;height:25px;" 
							data-options="value:'${today}'">
					</div>
				</div>
				<div style="margin-top: 24px;height:5px;">
				</div>
				<div style="margin-top: 5px;">
					<a class="easyui-linkbutton c2" onclick="cleanStudyordersForm()" style="width:160px;">清空</a>
					<a class="easyui-linkbutton" onclick="" style="width:160px;">${sessionScope.locale.get("wl.dosearch")}</a>
				</div>
				</form>
			</div>
		</div>
		<div data-options="region:'center',border:false">
			<table id="applicationdg_sch" class="easyui-datagrid"
				data-options="fit:true,singleSelect: true,border:false,loadMsg:'加载中...',emptyMsg:'没有查找到申请信息...',
				onDblClickRow:function(index,row){
                	$(this).datagrid('selectRow',index);
                	extractApplication_sch(row);
                }">
		        <thead>
		            <tr>
		                <th data-options="field:'patientname',width:60">姓名</th>
		                <th data-options="field:'sexdisplay'">性别</th>
		                <th data-options="field:'studyitems',width:130">检查项目</th>
		                <th data-options="field:'modality'">检查类型</th>
		                <th data-options="field:'his_orderid'">申请单号</th>
		                <th data-options="field:'patientid'">病人编号</th>
		                <th data-options="field:'birthdate'">出生日期</th>
		                <th data-options="field:'address',width:150">地址</th>
		                <th data-options="field:'telephone'">电话</th>
		                <th data-options="field:'appdatetime'">申请时间</th>
		            </tr>
		        </thead>
		    </table>
		</div>
	
	</div>
</body>
</html>