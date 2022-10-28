<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>Insert title here</title>
<body>
	<div class="easyui-layout" data-options="fit:true">
		<div data-options="region:'north',border:false" style="height:70px;">
			<div style="margin-left:auto;margin-right:auto;width:100%">
				<form id="westsearch_form" method="post">
	    		
					<div class="visit_content_div">
						<input class="easyui-datebox" label="开始：" labelWidth="80px" labelAlign="right" style="width: 200px;height:28px;" 
								id="datefrom_west" name="datefrom"
								data-options="value:'${today}'">
					</div>
					<div class="visit_content_div">
						<input class="easyui-datebox" label="结束：" labelWidth="80px" labelAlign="right" style="width: 200px;height:28px;" 
								id="dateto_west" name="dateto"
								data-options="value:'${today}'">
					</div>
					<div class="visit_content_div">
					
						<%-- <input id="apporderStatus_exam" name="orderstatus" class="easyui-combobox" style="width:280px;height:28px;"
							data-options="valueField:'code',textField:'${sessionScope.syscode_lan}',panelHeight:'200px',
							url:'${ctx}/syscode/getCode?type=0005',value:'7',editable:false,multiple:true,icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); }}]"
							label="检查状态："  labelWidth="80px" labelAlign="right" > --%>
					
			            <!-- <select class="easyui-combobox" label="注射状态：" labelWidth="80px" labelAlign="right"  style="width:200px;height:28px;"
							id="isConsulted_west" name="isConsulted"
							data-options="editable:false,panelHeight:'auto'">
			                <option value="">&nbsp;</option>
			                <option value="NO">未注射</option>
			                <option value="9">已注射</option>
			            </select> -->
			            <select class="easyui-combobox" label="注射状态：" labelWidth="80px" labelAlign="right"  style="width:200px;height:28px;"
							id="isInjected_west" name="isInjected"
							data-options="editable:false,panelHeight:'auto'">
			                <option value="">&nbsp;</option>
			                <option value="NO">未注射</option>
			                <option value="${injected}">已注射</option>
			            </select>
		            </div>
					<div class="visit_content_div">
						<input class="easyui-combobox" label="部门：" labelWidth="80px" labelAlign="right"  style="width:200px;height:28px;"
							data-options="url:'${ctx}/dic/getDeptOfModalityFromCache',valueField: 'id',textField: 'deptname',
								editable:false,panelHeight:'auto',
								onChange:setModality,
								onLoadSuccess:function(data){if(data){$(this).combobox('select', data[0].id)}}"/>
		            </div>
					<div class="visit_content_div">
						<input class="easyui-combobox" label="机房：" labelWidth="80px" labelAlign="right"  style="width:200px;height:28px;"
							id="modality_west" name="modalityid"
							data-options="valueField: 'id',textField: 'modality_name',editable:false,panelHeight:'auto'
							,multiple:true,icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); }}]">
		            </div>
					<div class="visit_content_div" style="padding-left:80px;">
						<a class="easyui-linkbutton" onclick="searchStudyorder()" style="width:120px;height:28px;">${sessionScope.locale.get("wl.dosearch")}</a>
					</div>
					<input id="datetype" name="datetype" value="${exist_sch ?'arrivedtime':'registertime'}" type="hidden">
				</form>
			</div>
		</div>
		<div data-options="region:'center',border:false">
			<table class="easyui-datagrid" id="dg_west"
				data-options="fit:true,singleSelect: true,border:false,loadMsg:'加载中...',emptyMsg:'没有查找到记录...',footer:'#footer_div_injection',
				onDblClickRow:function(index,row){
                	$(this).datagrid('selectRow',index);
                	dbClikdg_west(row);
                }">
		        <thead>
		            <tr>
		            	 <c:choose>
					      <c:when test="${exist_sch}">
						 	 <th data-options="field:'arrivedtime'">签到时间</th>
					      </c:when>
					      <c:otherwise>
					      	<th data-options="field:'regdatetime'">登记时间</th>
					      </c:otherwise>
				        </c:choose>
		            	<th data-options="field:'studyid',align:'center'">检查号</th>
		            	<th data-options="field:'orderstatus',width:70,styler:columeStyler_orderstatus_injection" sortable="true">状态</th>
		                <th data-options="field:'patientname',width:60,align:'center'">姓名</th>
		                <th data-options="field:'sexdisplay',align:'center'">性别</th>
		                <th data-options="field:'age',align:'center'">年龄</th>
		                <th data-options="field:'studyitems',width:150,align:'center'">检查项目</th>
		                <th data-options="field:'birthdate',align:'center'">出生日期</th>
		                <th data-options="field:'address',width:150">地址</th>
		            </tr>
		        </thead>
		    </table>
		    <div id="footer_div_injection" style="padding:2px 5px;">
		    	<span>共<span id="count_injection">0</span>条记录</span>
		    	<jsp:include page="footer.jsp"/>
		    </div>
		</div>
	
	</div>
</body>
</html>