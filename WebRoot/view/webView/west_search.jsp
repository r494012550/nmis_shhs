<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div id="w_s_accordion" class="easyui-accordion" data-options="fit:true" border="0">
	
		<div title="${sessionScope.locale.get("wl.search")}" style="padding:5px;overflow:auto;" data-options="selected:true">
            <div style="margin-top: 3px;">
            	<div class="easyui-datalist" id="myfilterlist_webview" title="${sessionScope.locale.get("wl.myfilter")}" style="width:268px;height:150px" data-options="
			            url: '${ctx}/webview/getFilters?filterType=webview',valueField:'id',textField:'description',tools:'#filter_tools',
			            onSelect:function(index,row){
			            	fillParams();
			            }">
			            <%-- <header style="height:18px">${sessionScope.locale.get("wl.myfilter")}</header> --%>
			    </div>
            
	            <div id="filter_tools">
	            	<%-- <shiro:hasPermission name="savefilter"> --%>
	        			<a class="easyui-tooltip" title="${sessionScope.locale.get("wl.savesearchcondition")}" onclick="openFilterSaveDialog('webview');">
	        				<i class="icon iconfont icon-plus7" style="font-size:15px;vertical-align:2px;text-align: 2px;"></i>
	        			</a>
	        		<%-- </shiro:hasPermission>
	        		<shiro:hasPermission name="managefilter"> --%>
	        			<a class="easyui-tooltip" title="${sessionScope.locale.get("wl.managesearchcondition")}" onclick="openFilterManageDialog('webview');">
	        				<i class="icon iconfont icon-set2" style="font-size:15px;vertical-align:2px;text-align: center;"></i>
	        			</a>
	        		<%-- </shiro:hasPermission> --%>
	            	<a class="easyui-tooltip" title="${sessionScope.locale.get("wl.clear")}" onclick="$('#myfilterlist_webview').datalist('clearSelections');">
	            		<i class="icon iconfont icon-qingkong" style="font-size:15px;vertical-align:2px;text-align: center;"></i>
	            	</a>
			    </div>
            </div>
        <form id="searchForm_webview" method="post">
            <div style="width: 270px;margin-top: 3px;">
				 <div style="width:130px;float:left;">
					<!-- <input id="modality" name="modality" class="easyui-combobox" label="设备类型：" labelPosition="top" style="width:100px;" data-options="valueField:'code',textField:'name_zh',url:'/worklist/getCode?type=0004',editable:false"> --> 
					<select class="easyui-combobox" name="modality" id="modality" style="width:129px;height:56px;" label="${sessionScope.locale.get("wl.modality")}:" labelPosition="top" 
						data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0004',editable:false,panelHeight:'auto'">
		                
		            </select>
				</div>
				<div style="width: 10px;"></div>
				<div style="width:130px;float:right;">
					<select class="easyui-combobox" name="reportstatus" id="reportStatus" style="width:129px;height:56px;" label="${sessionScope.locale.get("wl.reportstatus")}:" 
						labelPosition="top" data-options="value:'${finalreport}',valueField:'code',textField:'${sessionScope.syscode_lan}',
						data: [{
							${sessionScope.syscode_lan}: '${finalreport_display}',
							code: '${finalreport}'
						}],
						editable:false,panelHeight:'auto'">
		            </select>
	            </div>
            </div>
            
			
			<div style="width: 270px;margin-top: 3px;">
				<div style="width:130px;float:left;">
					 <input id="studyid" name="studyid" class="easyui-textbox" style="width:129px;height:56px;" type="text" label="${sessionScope.locale.get("wl.studyid")}:" labelPosition="top">
				</div>
				
				<div style="width:130px;float:right;">
					<select class="easyui-combobox" name="patientsource" id="patientsource" style="width:129px;height:56px;" label="${sessionScope.locale.get("wl.patientsource")}:" 
						labelPosition="top" data-options="valueField:'code',textField:'${sessionScope.syscode_lan}',url:'${ctx}/syscode/getCode?type=0002&addempty=true',editable:false,
						panelHeight:'auto'">
					 	<!-- <option value="K">不限</option>
		                <option value="I">住院</option>
		                <option value="O">门诊</option>
		                <option value="E">急诊</option> -->
		            </select>
	            </div>
            </div>
            
            <div style="width: 270px;margin-top: 3px;">
				<div style="width:130px;float:left;">
					<input id="patientid" name="patientid" class="easyui-textbox" style="width:129px;height:56px;" label="${sessionScope.locale.get("wl.patientid")}:" labelPosition="top">
				</div>
				
				<div style="width:130px;float:right;">
				    <input id="patientname" name="patientname" class="easyui-textbox" style="width:129px;height:56px;" label="${sessionScope.locale.get("wl.patientname")}:" labelPosition="top" >
				</div>
			</div>
			<div style="width: 270px;margin-top: 3px;">
				<div style="width:130px;float:left;">
					<input id="inno" name="inno" class="easyui-textbox" style="width:129px;height:56px;"
							label="住院号：" labelPosition="top">
				</div>
				<div style="width: 10px;"></div>
				<div style="width:130px;float:right;">
					<input id="outno" name="outno" class="easyui-textbox" style="width:129px;height:56px;"
						label="门诊号：" labelPosition="top">
	            </div>
	         </div>
			<!-- <div style="margin-bottom:5px;margin-top: 15px;"> -->
			    
				<!-- <div class="easyui-panel" style="width:200px;height:148px;padding:5px 1px; overflow:hidden;"> -->
				    
					<div style="width:268px;margin-top:3px;">
						<select class="easyui-combobox" name="datetype" id="datetype" style="width:268px;height:56px;" label="${sessionScope.locale.get("wl.datetime")}:" labelPosition="top" 
							data-options="editable:false,panelHeight:'auto'">
			               
			                <option value="studytime">${sessionScope.locale.get("wl.studytime")}</option>
			                <option value="reporttime">报告时间</option>
			                <option value="registertime">${sessionScope.locale.get("wl.registertime")}</option>
			                <option value="appointmenttime">${sessionScope.locale.get("wl.appointmenttime")}</option>
			            </select>
		            </div>
					<div class="easyui-panel" style="width:268px;margin-top:3px;padding:2px;">
							<a id="today" class="easyui-linkbutton" style="width:83px;height:26px;" data-options="toggle:true,group:'g1',selected:true,plain:true" 
								onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('T');searchStudyWS();">${sessionScope.locale.get("wl.today")}</a>
					        <a  id="yesterday" class="easyui-linkbutton" style="width:85px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
					        	onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('Y');searchStudyWS();">${sessionScope.locale.get("wl.yesterday")}</a>
					        <a id="threeday" class="easyui-linkbutton" style="width:83px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
		        				onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('TD');searchStudyWS();">近三天</a>
					</div>
					
					<div class="easyui-panel" style="width:268px;margin-top:3px;padding:2px;">
						<a id="fiveday" class="easyui-linkbutton" style="width:83px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
							onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('FD');searchStudyWS();">近五天</a>
				        <a  id="week" class="easyui-linkbutton" style="width:85px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
				        	onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('W');searchStudyWS();">近一周</a>
				        <a id="month" class="easyui-linkbutton" style="width:83px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
				        	onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('M');searchStudyWS();">近一个月</a>
					</div>
					
					<div style="width:268px;margin-top:3px;">
						<input id="datefrom" name="datefrom" class="easyui-datebox" style="width: 125px;height:26px;" data-options="
							onSelect:function(){
								$('#today').linkbutton({selected:false});
								$('#yesterday').linkbutton({selected:false});
								$('#threeday').linkbutton({selected:false});
								$('#fiveday').linkbutton({selected:false});
								$('#week').linkbutton({selected:false});
								$('#month').linkbutton({selected:false});
								$('#appdate').val('');
							}">
						-
						<input id="dateto" name="dateto" class="easyui-datebox" style="width: 125px;height:26px;" data-options="
							onSelect:function(){
								$('#today').linkbutton({selected:false});
								$('#yesterday').linkbutton({selected:false});
								$('#threeday').linkbutton({selected:false});
								$('#fiveday').linkbutton({selected:false});
								$('#week').linkbutton({selected:false});
								$('#month').linkbutton({selected:false});
								$('#appdate').val('');
							}">
					</div>
					
		    <div style="margin-top: 5px;">
				<a class="easyui-linkbutton" onclick="searchStudyWS();" style="width:268px;height:28px">${sessionScope.locale.get("wl.dosearch")}</a>
			</div>


        	<div style="margin-top: 5px;">
        		<a class="easyui-linkbutton c2" onclick="clearManage();" style="width:268px;height:28px">${sessionScope.locale.get("wl.clearcondition")}</a>
        	</div>
        	<input id="appdate" type="hidden" name="appdate" value="T"/>
		</form>    
		</div>
	</div>
	
</body>
</html>