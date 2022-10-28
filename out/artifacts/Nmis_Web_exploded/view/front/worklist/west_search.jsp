<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
<div class="easyui-tabs" data-options="fit:true,border:false,plain:true,narrow:true,justified:true,tabPosition:'bottom'">
	<div id="w_s_accordion" class="easyui-accordion" data-options="fit:true" border="0" title="${sessionScope.locale.get("wl.search")}">
		<div title="${sessionScope.locale.get("wl.search")}" data-options="selected:true">
		
			<div class="easyui-layout" data-options="fit:true,border:false">
				<div data-options="region:'center',border:false" style="padding:2px 5px;">
					<form id="searchForm_worklist" method="post">
						<c:if test="${sr_support}">
							<select class="easyui-combobox" id="datasource" name="datasource" label="${sessionScope.locale.get("wl.datasource")}:" labelPosition="top" style="width:100%;height:56px;" 
								data-options="url: '${ctx}/worklist/getDataSource',value:'localdatabase',valueField:'value',textField:'name',editable:false,onChange:dataSourceChangeHandler" >
				                <%-- <option value="local">${sessionScope.locale.get("wl.local")}</option>
				                <option value="via">syngo.via</option> --%>
				            </select>
			            </c:if>
			            <%-- <c:if test="${workforce_support}">
				            <div style="width: 100%;margin-top: 2px;" class="mylabel">
				            	<div class="easyui-panel" title="我的报告" style="width:100%;height:200px;"
				            		data-options="collapsible:true,collapsed:${myreport_collapsed==1?true:false},
				            			onExpand:function(){saveUserProfiles_value('0','myreport_collapsed')},
				            			onCollapse:function(){saveUserProfiles_value('1','myreport_collapsed')}">
				            		<div class="easyui-datalist" id="myreport_list" data-options="
				            			url:'${ctx}/syscode/getCode?type=0007&addempty=true',
								            valueField:'code',textField:'name_zh',fit:true,border:false,
								            onSelect:function(index,row){
								            	searchForm_flag='searchForm';
								            	getMyReport();
								            }">
								    </div>
								     <!-- data: [{id:1,name:'未写'},{id:2,name:'未审'},{id:3,name:'已写'},{id:4,name:'已审'},{id:5,name:'全部'}], -->
				            	</div>
				            </div>
			            </c:if> --%>
			            <div style="margin-top: 2px;">
			            	<div class="easyui-panel" title="${sessionScope.locale.get("wl.myfilter")}" style="width:100%;height:220px;"
			            		data-options="collapsible:true,tools:'#filter_tools',collapsed:${myfilter_collapsed==1?true:false},
			            			onExpand:function(){saveUserProfiles_value('0','myfilter_collapsed')},
				            		onCollapse:function(){saveUserProfiles_value('1','myfilter_collapsed')}
			            		">
			            		<div class="easyui-datalist" id="myfilterlist_worklist" data-options="
							            url: '${ctx}/worklist/getFilters?filterType=worklist',valueField:'id',textField:'description',fit:true,border:false,
							            ${workforce_support?"groupField: '_group',":""}
							            onSelect:function(index,row){
							            	searchForm_flag='searchForm';
							            	fillParams();
							            }">
							            <%-- <header style="height:18px">${sessionScope.locale.get("wl.myfilter")}</header> --%>
							    </div>
							    <c:if test="${workforce_support}">
							    	<input id="workforce_worklist" type="hidden" name="workforce"/>
							    </c:if>
			            	</div>
				            <div id="filter_tools">
				            	<shiro:hasPermission name="save_WLfilter">
				        			<a class="easyui-tooltip" title="${sessionScope.locale.get("wl.savesearchcondition")}" onclick="openFilterSaveDialog('worklist');">
				        				<i class="icon iconfont icon-plus7" style="font-size:15px;vertical-align:2px;text-align: 2px;"></i>
				        			</a>
				        		</shiro:hasPermission>
				        		<shiro:hasPermission name="manage_WLfilter">
				        			<a class="easyui-tooltip" title="${sessionScope.locale.get("wl.managesearchcondition")}" onclick="openFilterManageDialog('worklist');">
				        				<i class="icon iconfont icon-set2" style="font-size:15px;vertical-align:2px;text-align: center;"></i>
				        			</a>
				        		</shiro:hasPermission>
				            	<a class="easyui-tooltip" title="${sessionScope.locale.get("wl.clear")}" onclick="
				            		$('#myfilterlist_worklist').datalist('clearSelections');$('#workforce_worklist').val('');">
				            		<i class="icon iconfont icon-qingkong" style="font-size:15px;vertical-align:2px;text-align: center;"></i>
				            	</a>
						    </div>
			            	
			          	  	<%-- <input id="fillParams" name="fillParams" class="easyui-combobox" label="${sessionScope.locale.get("wl.myfilter")}:" style="width:200px" labelPosition="top" 
			          	  		data-options="url:'${ctx}/worklist/getFilters',valueField:'id',textField:'description',editable:false,editable : false,
			          	  			icons: [{
										iconCls : 'icon-clear'
									}],
			          	  			onChange:function(param){
										fillParams();
									}"> --%>
			            </div>
			            
			            
			            <div style="width: 100%;margin-top: 2px;" class="mylabel">
							 <div style="width:120px;float:left;">
								<!-- <input id="modality" name="modality" class="easyui-combobox" label="设备类型：" labelPosition="top" style="width:100px;" data-options="valueField:'code',textField:'name_zh',url:'/worklist/getCode?type=0004',editable:false"> --> 
								<input class="easyui-combobox" name="modality" id="modality" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.modality")}:" labelPosition="top" 
									data-options="valueField:'code',textField:'name_zh',multiple:true,editable:false,panelHeight:'120px',onChange:findModalityname,
									url:'${ctx}/syscode/getCode?type=0004'"/>
							</div>
							<div style="width:120px;float:right;">
								<select class="easyui-combobox" name="reportstatus" id="reportStatus" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.reportstatus")}:" labelPosition="top"
								    data-options="valueField:'code',textField:'${sessionScope.syscode_lan}',editable:false,panelHeight:'auto',
									url:'${ctx}/syscode/getCode?type=0007&addempty=true'">
					            </select>
				            </div>
			            </div>
			            <div style="width: 100%;margin-top: 2px;" class="mylabel">
							 <div style="width:120px;float:left;">
								<!-- <input id="modality" name="modality" class="easyui-combobox" label="设备类型：" labelPosition="top" style="width:100px;" data-options="valueField:'code',textField:'name_zh',url:'/worklist/getCode?type=0004',editable:false"> --> 
								<input class="easyui-combobox" name="modality_dic_reg" id="modality_dic_reg" style="width:119px;height:50px;" label="检查设备:" labelPosition="top" 
									data-options="valueField:'id',textField:'modality_name',multiple:true,editable:false,panelHeight:'120px',onChange:setModalityname"/>
							</div>
			            </div>
			             <%-- <div style="width: 100%;margin-top: 2px;" class="mylabel">
								<select class="easyui-combobox" name="institutionid" id="institutionid" style="width:100%;height:50px;" label="送检机构：" labelPosition="top" 
									data-options="valueField:'id',textField:'name',url:'${ctx}/dic/getInstitution',editable:false,panelHeight:'120px',
			                        icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]" >
					            </select>
				         </div> --%>
				         
				         <div style="width:100%;margin-top:2px;" class="mylabel">
							<select class="easyui-combobox" name="datetype" id="datetype" style="width:100%;height:50px;" label="${sessionScope.locale.get("wl.datetime")}:" labelPosition="top" 
								data-options="editable:false,panelHeight:'auto'">
				               	
				                <option value="studytime">${sessionScope.locale.get("wl.studytime")}</option>
				                <option value="reporttime">报告时间</option>
				                <option value="audittime">审核时间</option>
				                <option value="appointmenttime">${sessionScope.locale.get("wl.appointmenttime")}</option>
				                 <option value="registertime">登记时间</option>
				            </select>
			            </div>
						<div class="easyui-panel" style="width:100%;margin-top:2px;padding:1px;">
							<a id="today" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
								onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('T');searchForm_flag='searchForm';searchStudyWS();">${sessionScope.locale.get("wl.today")}</a>
					        <a  id="yesterday" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
					        	onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('Y');searchForm_flag='searchForm';searchStudyWS();">${sessionScope.locale.get("wl.yesterday")}</a>
					        <a id="threeday" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',selected:true,plain:true" 
		        				onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('TD');searchForm_flag='searchForm';searchStudyWS();">近三天</a>
						</div>
						
						<div class="easyui-panel" style="width:100%;margin-top:2px;padding:1px;">
							<a id="fiveday" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
								onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('FD');searchForm_flag='searchForm';searchStudyWS();">近五天</a>
					        <a  id="week" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
					        	onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('W');searchForm_flag='searchForm';searchStudyWS();">近一周</a>
					        <a id="month" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
					        	onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('TM');searchForm_flag='searchForm';searchStudyWS();">近三个月</a>
						</div>
						
						<div style="width:100%;margin-top:2px;">
							<div style="width:120px;float:left;">
								<input id="datefrom" name="datefrom" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
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
							<div style="width:120px;float:right;">
								<input id="dateto" name="dateto" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
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
						</div>
						
						<div style="width: 100%;margin-top: 2px;" class="mylabel">
							<div style="width:120px;float:left;">
								 <input id="studyid" name="studyid" class="easyui-textbox" style="width:119px;height:50px;" type="text" label="${sessionScope.locale.get("wl.studyid")}:" labelPosition="top">
							</div>
							
							<div style="width:120px;float:right;">
								<select class="easyui-combobox" name="patientsource" id="patientsource" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.patientsource")}:" 
									labelPosition="top" data-options="valueField:'code',textField:'${sessionScope.syscode_lan}',url:'${ctx}/syscode/getCode?type=0002&addempty=true',editable:false,
									panelHeight:'auto'">
					            </select>
				            </div>
			            </div>
			            
			            <div style="width: 100%;margin-top: 2px;" class="mylabel">
							<div style="width:120px;float:left;">
								<input id="patientid" name="patientid" class="easyui-textbox" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.patientid")}:" labelPosition="top">
							</div>
							
							<div style="width:120px;float:right;">
							    <input id="patientname" name="patientname" class="easyui-textbox" style="width:119px;height:50px;" label="${sessionScope.locale.get("wl.patientname")}:" labelPosition="top" >
							</div>
						</div>
						
						<!-- <div style="width: 100%;margin-top: 3px;" class="mylabel">
							<div style="width:120px;float:left;">
								<input id="inno" name="inno" class="easyui-textbox" style="width:119px;height:50px;"
										label="住院号：" labelPosition="top">
							</div>
							<div style="width: 10px;"></div>
							<div style="width:120px;float:right;">
								<input id="outno" name="outno" class="easyui-textbox" style="width:119px;height:50px;"
									label="门诊号：" labelPosition="top">
				            </div>
				         </div> -->
				         
				         <div style="width: 100%;margin-top: 2px;" class="mylabel">
							<div style="width:120px;float:left;">
								<input id="reportphysician_name_ws" name="reportphysician_name" class="easyui-textbox" style="width:119px;height:50px;" label='${sessionScope.locale.get("report.reportphysician")}:' labelPosition="top">
							</div>
							
							<div style="width:120px;float:right;">
							    <input id="auditphysician_name_ws" name="auditphysician_name" class="easyui-textbox" style="width:119px;height:50px;" label='${sessionScope.locale.get("report.auditphysician")}:' labelPosition="top" >
							</div>
						</div>
						<div style="width: 100%;margin-top: 3px;" class="mylabel">
				            <div style="width:120px;float:left;">
								<input class="easyui-timespinner" name="startfromtime" id="startfromtime_wl" style="width:119px;height:50px;"
									 label="开始时间:" labelPosition="top" />
							</div>
				            <div style="width:120px;float:right;">
								<input class="easyui-timespinner" name="endtotime" id="endtotime_wl" style="width:119px;height:50px;"
									 label="结束时间:" labelPosition="top" />
							</div>
							<div style="width:120px;float:left;">
								<input class="easyui-textbox" name="idnumber" id="idnumber_wl" style="width:119px;height:50px;"
									 label="身份证:" labelPosition="top" />
							</div>
						 </div>
						
						<!-- <div style="margin-bottom:5px;margin-top: 15px;"> -->
						    
							<!-- <div class="easyui-panel" style="width:200px;height:148px;padding:5px 1px; overflow:hidden;"> -->
							    
								
								
							<!-- </div> -->
					       
					     <!-- </div> -->
					    
			        	<input id="appdate" type="hidden" name="appdate" value="TD"/>
			        	<input id="modality_dic_reg_id" type="hidden" name="modality_dic_reg_id"/>
					</form>
				</div>
				<div data-options="region:'south',hideCollapsedContent:false,border:false" style="height:70px;padding:2px 5px;">
					<div style="margin-top: 5px;">
						<a class="easyui-linkbutton" onclick="searchForm_flag='searchForm';searchStudyWS();" style="width:100%;height:28px">${sessionScope.locale.get("wl.dosearch")}</a>
					</div>
					
					<%-- <div style="margin-top: 5px;">
						<shiro:hasPermission name="savefilter">
		        			<a class="easyui-linkbutton" onclick="openSaveFilterDialog();"  style="width:131px;height:28px">${sessionScope.locale.get("wl.savesearchcondition")}</a>
		        		</shiro:hasPermission>
		        		<shiro:hasPermission name="managefilter">
		        			<a class="easyui-linkbutton" onclick="openFilterManageDialog();" style="width:131px;height:28px">${sessionScope.locale.get("wl.managesearchcondition")}</a>
		        		</shiro:hasPermission>
		        	</div> --%>
		
		        	<div style="margin-top: 5px;">
		        		<a class="easyui-linkbutton c2" onclick="clearManage();" style="width:100%;height:28px">${sessionScope.locale.get("wl.clearcondition")}</a>
		        	</div>
				</div>
			</div>
		</div>
		<div title="${sessionScope.locale.get("wl.favorites")}" style="overflow:auto;" data-options="href:'${ctx}/view/front/worklist/favoritestree.jsp'">
		
		</div>
		
		
	</div>
	<c:if test="${quicksearch}">
		<div title="诊断查询" style="overflow:auto;" data-options="href:'${ctx}/view/front/worklist/fulltext_search.jsp'"></div>
	</c:if>
	
	<div title="标签查询" style="overflow:auto;" data-options="href:'${ctx}/view/front/worklist/label_search.jsp'">
	
	</div>
</div>
<style type="text/css">
	.header_style{
		height: 25px;
		padding: 2px;
	}
</style>
<script type="text/javascript">

/*var myreport_data = [{name:'未写'},{name:'未审'},{name:'已写'},{name:'已审'},{name:'全部'}];

        var data = [{
           text: '我的报告',
           state: 'open',
           iconCls: 'icon-more',
           children: [{
               text: '未写',
               checked: 'checked',
               selected: true
           },{
               text: '未审'
           },{
               text: '已写'
           },{
               text: '已审'
           },{
               text: '全部'
           }]
       }]; */

       /* function toggle(){
           var opts = $('#sm').sidemenu('options');
           $('#sm').sidemenu(opts.collapsed ? 'expand' : 'collapse');
           opts = $('#sm').sidemenu('options');
           $('#sm').sidemenu('resize', {
               width: opts.collapsed ? 60 : 200
           })
       } */
</script>
</body>
</html>