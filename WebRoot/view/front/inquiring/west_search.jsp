<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>Insert title here</title>
<body>
	<div class="easyui-layout" data-options="fit:true">
		<div data-options="region:'north',border:false" style="height:160px;">
			<div style="margin-left:auto;margin-right:auto;width:400px;height:150px;">
				<form id="westsearch_form" method="post">
	    		
	    		<div style="margin-top: 3px;">
					<input class="easyui-textbox" label="病人姓名：" labelWidth="80" labelAlign="right" style="height:25px;width:400px;"
						name="patientname">
	    		</div>
	    		
	    		<div style="width: 100%;">
					<div style="float:left;margin-top:3px;margin-bottom:3px;">
						<input class="easyui-datebox" label="开始：" labelWidth="80px" labelAlign="right" style="width: 195px;height:25px;" 
							id="datefrom_west" name="datefrom"
							data-options="value:'${today}'">
					</div>
					<div style="float:right;margin-top:3px;margin-bottom:3px;">
						<input class="easyui-datebox" label="结束：" labelWidth="80px" labelAlign="right" style="width: 195px;height:25px;" 
							id="dateto_west" name="dateto"
							data-options="value:'${today}'">
					</div>
				</div>
				
				<div style="width: 100%;">
					<div style="float:left;margin-top:3px;margin-bottom:3px;">
					<input class="easyui-combobox" label="部门：" labelWidth="80px" labelAlign="right"  style="width:195px;height:25px;"
						data-options="url:'${ctx}/dic/getDeptOfModalityFromCache',valueField: 'id',textField: 'deptname',
							editable:false,panelHeight:'auto',
							onChange:setModality,
							onLoadSuccess:function(data){if(data){$(this).combobox('select', data[0].id)}}"/>
					</div>
				
					<div style="float:right;margin-top:3px;margin-bottom:3px;">
					<input class="easyui-combobox" label="机房：" labelWidth="80px" labelAlign="right"  style="width:195px;height:25px;"
						id="modality_west" name="modalityid"
						data-options="valueField: 'id',textField: 'modality_name',editable:false,panelHeight:'auto',multiple:true
						,icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); }}]"/>
		            </div>
				</div>
				
				<div style="width: 100%;">
				<c:choose>
					<c:when test="${exist_sch}">
						 <div style="float:left;margin-top:3px;margin-bottom:3px;">
							<select class="easyui-combobox" label="签到：" labelWidth="80px" labelAlign="right"  style="width:195px;height:25px;"
								id="isArrived_west" name="isArrived"
								data-options="editable:false,panelHeight:'auto'">
								<option value="">&nbsp;</option>
				                <option value="${arrived}">已签到</option>
				            </select>
				        </div>
				        <div style="float:right;margin-top:3px;margin-bottom:3px;">
							<select class="easyui-combobox" label="问诊：" labelWidth="80px" labelAlign="right"  style="width:195px;height:25px;"
								id="isConsulted_west" name="isConsulted"
								data-options="editable:false,panelHeight:'auto'">
				                <option value="">&nbsp;</option>
				                <option value="NO">未问诊</option>
				                <option value="${consulted}">已问诊</option>
				            </select>
			            </div>
					</c:when>
					<c:otherwise>
						<div style="float:right;margin-top:3px;margin-bottom:3px;">
							<select class="easyui-combobox" label="问诊：" labelWidth="80px" labelAlign="right"  style="width:400px;height:25px;"
								id="isConsulted_west" name="isConsulted"
								data-options="editable:false,panelHeight:'auto'">
				                <option value="">&nbsp;</option>
				                <option value="NO">未问诊</option>
				                <option value="${consulted}">已问诊</option>
				            </select>
			            </div>
    				</c:otherwise>
				</c:choose>   
				</div>
				<div style="width: 400px;margin-top:3px;margin-bottom:3px;">
					<a class="easyui-linkbutton c2" onclick="onclickClear()" style="width:197px;">清空</a>
					<a class="easyui-linkbutton" onclick="searchStudyorder()" style="width:197px;">${sessionScope.locale.get("wl.dosearch")}</a>
				</div>
				
				<input id="datetype" name="datetype" value="${exist_sch ?'arrivedtime':'registertime'}" type="hidden">
				<input id="exist_sch_inq" name="dataSoures" value="${exist_sch}" type="hidden">
				</form>
			</div>
		</div>
		<div data-options="region:'center',border:false">
			<table class="easyui-datagrid" id="dg_west"
				data-options="fit:true,singleSelect: true,border:false,footer:'#footer2_div_exam',loadMsg:'加载中...',emptyMsg:'没有查找到患者信息...',
				onDblClickRow:function(index,row){
                	$(this).datagrid('selectRow',index);
                	dbClikdg_west(row);
                }">
		        <thead>
		            <tr>
		                <th data-options="field:'patientname',width:60">姓名</th>
		                <th data-options="field:'sexdisplay'">性别</th>
		                <th data-options="field:'age'">年龄</th>
		                 <th data-options="field:'orderstatus',styler:columeStyler_orderstatus_inquiring">状态</th>
		                <th data-options="field:'studyitems',width:130">检查项目</th>
		                <th data-options="field:'modality_type'">检查类型</th>
		                <th data-options="field:'birthdate'">出生日期</th>
		                <th data-options="field:'address',width:150">地址</th>
		                <th data-options="field:'telephone'">电话</th>
		                <c:choose>
					      <c:when test="${exist_sch}">
						 	 <th data-options="field:'arrivedtime'">签到时间</th>
					      </c:when>
					      <c:otherwise>
					      	<th data-options="field:'regdatetime'">登记时间</th>
					      </c:otherwise>
				        </c:choose>
		            </tr>
		        </thead>
		    </table>
		</div>
	</div>
		 <div id="footer2_div_exam" style="padding:2px 5px;">
	    	<jsp:include page="footer.jsp"/>
		</div>
</body>
</html>