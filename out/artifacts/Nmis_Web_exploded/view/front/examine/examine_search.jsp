<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'west',collapsible:false,title:'查询条件'" style="width:280px;padding:2px;">
		<div class="easyui-layout" data-options="fit:true,border:false">
			<div data-options="region:'center',border:false" style="padding:0px 5px;">
		<form id="searchForm_exam" method="POST">
			<div style="width: 100%;margin-top:3px;">
				<select class="easyui-combobox" name="datetype" id="datetype" style="width: 100%;height:56px;" label="${sessionScope.locale.get("wl.datetime")}:" labelPosition="top" 
					data-options="editable:false,panelHeight:'auto'">
					<option value="arrivedtime">签到时间</option>
					<option value="studytime">${sessionScope.locale.get("wl.studytime")}</option>
	            	<option value="appointmenttime">${sessionScope.locale.get("wl.appointmenttime")}</option>
	            	<option value="registertime">登记时间</option>
	                <!-- <option value="reporttime">报告时间</option> -->
	            </select>
            </div>
				
			<div class="easyui-panel" style="width: 100%;margin-top:3px;padding:2px;">
			<a id="apptoday" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',selected:true,plain:true" 
				onclick="$('#appdatefrom').datebox('setValue','');$('#appdateto').datebox('setValue','');$('#appdate').val('T');searchForm_flag='searchForm';searchExamine();">${sessionScope.locale.get("wl.today")}</a>
	        <a  id="appyesterday" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
	        	onclick="$('#appdatefrom').datebox('setValue','');$('#appdateto').datebox('setValue','');$('#appdate').val('Y');searchForm_flag='searchForm';searchExamine();">${sessionScope.locale.get("wl.yesterday")}</a>
	        <a id="appthreeday" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
		        onclick="$('#appdatefrom').datebox('setValue','');$('#appdateto').datebox('setValue','');$('#appdate').val('TD');searchForm_flag='searchForm';searchExamine();">近三天</a>
			</div>
			
			<div class="easyui-panel" style="width: 100%;margin-top:3px;padding:2px;">
				<a id="appfiveday" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
					onclick="$('#appdatefrom').datebox('setValue','');$('#appdateto').datebox('setValue','');$('#appdate').val('FD');searchForm_flag='searchForm';searchExamine();">近五天</a>
		        <a  id="appweek" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
		        	onclick="$('#appdatefrom').datebox('setValue','');$('#appdateto').datebox('setValue','');$('#appdate').val('W');searchForm_flag='searchForm';searchExamine();">近一周</a>
		        <a id="appmonth" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
		        	onclick="$('#appdatefrom').datebox('setValue','');$('#appdateto').datebox('setValue','');$('#appdate').val('M');searchForm_flag='searchForm';searchExamine();">近一个月</a>
			</div>

			<div style="width: 100%;margin-top:3px;">
				<div style="width:120px;float:left;">
					<input id="appdatefrom" name="datefrom" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
						onSelect:function(){
							$('#apptoday').linkbutton({selected:false});
							$('#appyesterday').linkbutton({selected:false});
							$('#appthreeday').linkbutton({selected:false});
							$('#appfiveday').linkbutton({selected:false});
							$('#appweek').linkbutton({selected:false});
							$('#appmonth').linkbutton({selected:false});
							$('#appdate').val('');
						}">
				</div>
				<div style="width:120px;float:right;">
					<input id="appdateto" name="dateto" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
						onSelect:function(){
							$('#apptoday').linkbutton({selected:false});
							$('#appyesterday').linkbutton({selected:false});
							$('#appthreeday').linkbutton({selected:false});
							$('#appfiveday').linkbutton({selected:false});
							$('#appweek').linkbutton({selected:false});
							$('#appmonth').linkbutton({selected:false});
							$('#appdate').val('');
						}">
				</div>
			</div>
			<div style="width: 100%;margin-top: 3px;">
				<div style="width:120px;float:left;">
					<input id="apppatientsex_exam" name="apppatientsex" class="easyui-combobox" style="width:120px;height:56px;"
					data-options="valueField:'code',textField:'name_zh',editable:false,panelHeight:'auto',
						url:'${ctx}/syscode/getCode?type=0001&addempty=true',
						onLoadSuccess:function(){$(this).combobox('select', '')}" 
						label="病人性别：" labelPosition="top"/>
				</div>
				
				<div style="width:120px;float:right;">
					<input id="apppatientsource_exam" name="apppatientsource" class="easyui-combobox" style="width:120px;height:56px;"
					data-options="valueField:'code',textField:'name_zh',editable:false, panelHeight:'auto',
						 url:'${ctx}/syscode/getCode?type=0002&addempty=true',
						 onLoadSuccess:function(){$(this).combobox('select', '')}"
						 label="病人来源：" labelPosition="top"/>
				</div>
			</div>
			<div style="width: 100%;margin-top: 3px;">
				<div style="width:120px;float:left;">
					<input id="appmodalityid_exam" name="appmodalityid" class="easyui-combobox" style="width:120px;height:56px;" 
					data-options="valueField:'id',textField:'modality_name',panelHeight:'120px',
					url:'${ctx}/dic/getModalityDic',editable:false,multiple:true"
					label="检查设备：" labelPosition="top">
				</div>
				
				<div style="width:120px;float:right;">
					<input id="apporderStatus_exam" name="apporderStatus" class="easyui-combobox" style="width:120px;height:56px;" 
					data-options="valueField:'code',textField:'${sessionScope.syscode_lan}',panelHeight:'120px',
					url:'${ctx}/syscode/getCode?type=0005',editable:false,multiple:true"
					label="检查状态：" labelPosition="top" >
				</div>
			</div>
			<div style="width: 100%;margin-top: 3px;">
				<div style="width:120px;float:left;">
					<input id="apppatientid_exam" name="apppatientid" class="easyui-textbox" style="width:120px;height:56px;"
					label="病人编号：" labelPosition="top">
				</div>
				<div style="width:120px;float:right;">
					<input id="appstudyid_exam" name="appstudyid" class="easyui-textbox" style="width:120px;height:56px;"
					label="检查编号：" labelPosition="top">
				</div>
			</div>
			<div style="width: 100%;margin-top: 3px;">
				<div style="width:120px;float:left;">
					<input id="appoutno_exam" name="appoutno" class="easyui-textbox" style="width:120px;height:56px;"
					label="门诊号：" labelPosition="top">
				</div>
				<div style="width:120px;float:right;">
					<input id="appinno_exam" name="appinno" class="easyui-textbox" style="width:120px;height:56px;"
					label="住院号：" labelPosition="top">
				</div>
			</div>
			<div style="width: 100%;margin-top: 3px;">
				<div style="width:120px;float:left;">
					<input id="apppatientname_exam" name="apppatientname" class="easyui-textbox" style="width:120px;height:56px;"
					label="姓名：" labelPosition="top">
				</div>
				<div style="width:120px;float:right;">
					<input id="appcardno_exam" name="appcardno" class="easyui-textbox" style="width:120px;height:56px;"
					label="卡号：" labelPosition="top">
				</div>
			</div>
			<input id="appdate" type="hidden" name="appdate" value="T"/>
        </form>
		</div>  
    	<div data-options="region:'south',collapsible:false,border:false" style="height:70px;padding:0px 5px;">
			<div style="margin-top: 5px;">
				<a class="easyui-linkbutton" onclick="searchForm_flag='searchForm';searchExamine()" style="width:100%;height:28px">查询</a>
			</div>
			
			<div style="margin-top: 5px;">
				<a class="easyui-linkbutton c2" onclick="clearSearch()" style="width:100%;height:28px">条件清除</a>
			</div>
		</div>
		</div>	
	</div>
	<div data-options="region:'center',border:false,headerCls:'panelHeaderCss_top'">
		<div style="height:100%">
		<table id="searchdg_exam" class="easyui-datagrid"
			data-options="rownumbers: true,showFooter: true,singleSelect:true,pagination:true,remoteSort:false,
			toolbar:'#dg2_tool_exam',footer:'#footer2_div_exam',fit:true,border:false,nowrap:false,
			loadMsg:'加载中...',emptyMsg:'没有查找到登记信息...',
			rowStyler: function(index,row){
	                  if (row.patientsource == 'E'){
	                      return 'background-color:#F00;font-weight:bold;';
	                  }
	         },
	        onRowContextMenu: function(e,index ,row){
	            e.preventDefault();
	            $(this).datagrid('selectRow',index);
	            $('#cmenu_reg').menu('show', {
	                left:e.pageX,
	                top:e.pageY
	            });
	        }
			">
			<thead>
				<tr>
					<!-- <th data-options="field:'ck',checkbox:true"></th> -->
					<th data-options="field:'orderstatus',width:80,styler:columeStyler_orderstatus_exam,align:'center'" sortable="true">检查状态</th>
					<th data-options="field:'studyid',width:130,align:'center'" sortable="true">检查编号</th>
					<th data-options="field:'patientid',width:90,align:'center'" sortable="true">患者编号</th>
				    <th data-options="field:'reportstatusdisplay',width:80,align:'center'" sortable="true">报告状态</th>
					<th data-options="field:'patientname',width:80,align:'center'" sortable="true">姓名</th>
					<th data-options="field:'sexdisplay',width:50,align:'center'" sortable="true">性别</th>
					<th data-options="field:'age',width:50,formatter:age_formatter,align:'center'" sortable="true">年龄</th>
					<th data-options="field:'birthdate',width:100,align:'center'" sortable="true">出生日期</th>
					<th data-options="field:'modality_type',width:65,align:'center'" sortable="true">设备类型</th>
					<th data-options="field:'modalityname',width:120,align:'center'" sortable="true">检查设备</th>
					<th data-options="field:'studyitems',width:200,align:'center'" sortable="true">检查项目</th>
					<th data-options="field:'regdatetime',width:150,align:'center'" sortable="true">登记日期</th>
					<th data-options="field:'appointmenttime',width:150,align:'center'" sortable="true">预约日期</th>
					<th data-options="field:'studydatetime',width:150,align:'center'" sortable="true">检查日期</th>
					<th data-options="field:'admissionid',width:80" sortable="true">入院号</th>
					<th data-options="field:'inno',width:80,align:'center'" sortable="true">住院号</th>
					<th data-options="field:'outno',width:80,align:'center'" sortable="true">门诊号</th>
					<th data-options="field:'wardno',width:80,align:'center'" sortable="true">病区</th>
				</tr>
			</thead>
		</table>

		<div id="dg2_tool_exam" style="padding:2px 5px;">
		    <c:if test="${quicksearch}">
		        <input type="text" id="quicksearch-input" name="search" class="easyui-searchbox" title="搜索内" 
		        	data-options="prompt:'请输入您要查询的信息',searcher:quickSearch,width:300,menu:'#quicksearch_mm'">
	               <div id="quicksearch_mm">
			        <div data-options="name:'all'">全部</div>
			        <div data-options="name:'join'">关联</div>
			    </div>
	            <i class="icon iconfont icon-info easyui-tooltip" style="font-size:13px;" title="搜索内容后加  * 查询，搜索出的内容为包含 此内容的全部数据。如：搜索 '李*'，
	                                       搜索出的数据将是所有开头为 '李' 的数据"></i>
          	</c:if>
			<shiro:hasPermission name="modifyExamine">
			<a href="#" class="easyui-linkbutton" plain="true" onClick="openModifyDlg()"><i class="icon iconfont icon-edit"></i>&nbsp;修改</a>
	    	</shiro:hasPermission>
	    	
	    	<shiro:hasPermission name="gotoTriageDlg">
	    	<a href="#" class="easyui-linkbutton" plain="true" onClick="openTriageDlg()"><i class="iconfont icon-fenzhen"></i>&nbsp;申请分诊</a>
	    	</shiro:hasPermission>
	    	
	    	<shiro:hasPermission name="cancelApplyTriage">
	    	<a href="#" class="easyui-linkbutton" plain="true" onClick="cancelApplyTriage('searchdg_exam')"><i class="iconfont icon-cancel1"></i>&nbsp;取消分诊</a>
	    	</shiro:hasPermission>
			<a class="easyui-linkbutton" plain="true" onClick="var row=$('#searchdg_exam').datagrid('getSelected');row?process(row.studyorderpkid,'examine'):process(null);" style="margin-left: 10px;"><span class="iconfont icon-liuchengshuoming"></span>  检查流程</a>
	    	<!-- <a href="#" class="easyui-linkbutton" plain="true" onclick="cancelImageandCheckMatch()"><i class="iconfont icon-cancel1"></i>&nbsp;取消匹配</a>-->
            <shiro:hasPermission name="cancelChecked_examine">
	    	<a href="#" class="easyui-linkbutton" plain="true" onclick="cancelChecked()"><i class="iconfont icon-cancel1"></i>&nbsp;重新检查</a>
	    	</shiro:hasPermission>
	    	
	    	<shiro:hasPermission name="cancelStudyOrder">
	    	<a href="#" class="easyui-linkbutton" plain="true" onclick="openCancelReasonDlg()"><i class="iconfont icon-cancel1"></i>&nbsp;取消检查</a>
	    	</shiro:hasPermission>
	    	
	    </div>
	    <div id="footer2_div_exam" style="padding:2px 5px;">
	    	<jsp:include page="footer.jsp"/>
		</div>
		<div id="cmenu_reg" class="easyui-menu" style="width:120px">
			<shiro:hasPermission name="modifyExamine">
	    	<div onclick="openModifyDlg();">修改</div>
	    	</shiro:hasPermission>
	    	
	    	<shiro:hasPermission name="gotoTriageDlg">
	    	<div onclick="openTriageDlg();">申请分诊</div>
	    	</shiro:hasPermission>
	    	
	    	<shiro:hasPermission name="cancelApplyTriage">
	    	<div onclick="cancelApplyTriage('searchdg_exam');">取消分诊</div>
	    	</shiro:hasPermission>
	    	
	    	<shiro:hasPermission name="cancelChecked_examine">
	    	<div onclick="cancelChecked()">重新检查</div>
	    	</shiro:hasPermission>
	    </div>
	    </div>
	</div>
</div>
	
	<input id="modalityType_search" name="modalityType" type="hidden" value="${modalityType}">
	<input type="hidden" id="page"> 
	<input type="hidden" id="pageSize">
</body>
</html>