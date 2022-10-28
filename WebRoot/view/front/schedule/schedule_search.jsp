 <%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'west',collapsible:false,title:'查询条件',headerCls:'panelHeaderCss_top'" style="width:280px;padding:2px 0px;">
	<header>查询条件</header>
	<div class="easyui-layout" data-options="fit:true,border:false">	
	   <div data-options="region:'center',border:false" style="padding:0px 5px;">
	       <div class="easyui-datalist" id="myfilterlist_schedule" title="我的条件" style="width:100%;height:170px" data-options="
                    url: '${ctx}/schedule/getFilters?filterType=schedule',valueField:'id',textField:'description',tools:'#filter_tools',
                    onSelect:function(index,row){
                        fillSearchParams_sch();
                    }">
        </div>
        <div id="filter_tools">
            <shiro:hasPermission name="save_Schfilter">
                <a class="easyui-tooltip" title="保存查询条件" onclick="openFilterSaveDialog('schedule');">
                    <i class="icon iconfont icon-plus7" style="font-size:15px;vertical-align:2px;text-align: 2px;"></i>
                </a>
            </shiro:hasPermission>
            <shiro:hasPermission name="manage_Schfilter">
                <a class="easyui-tooltip" title="管理查询条件" onclick="openFilterManageDialog('schedule');">
                    <i class="icon iconfont icon-set2" style="font-size:15px;vertical-align:2px;text-align: center;"></i>
                </a>
            </shiro:hasPermission>
            <a class="easyui-tooltip" title="清空" onclick="$('#myfilterlist_schedule').datalist('clearSelections');">
                <i class="icon iconfont icon-qingkong" style="font-size:15px;vertical-align:2px;text-align: center;"></i>
            </a>
        </div>
        <form name="searchForm" id="searchForm_schedule" method="POST">
            <div style="width:100%;margin-top:3px;">
                <select class="easyui-combobox" name="datetype" id="datetype" style="width:100%;height:56px;" label="${sessionScope.locale.get("wl.datetime")}:" labelPosition="top" 
                    data-options="editable:false,panelHeight:'auto',onChange:timeSelect" >
                    <option value="appointmenttime">${sessionScope.locale.get("wl.appointmenttime")}</option>
                    <option value="arrivedtime">签到时间</option>
                    <option value="studytime">${sessionScope.locale.get("wl.studytime")}</option>
                    <!-- <option value="reporttime">报告时间</option> -->
                    
                    
                </select>
            </div>
             <div id="panel1" class="easyui-panel" style="width: 100%;margin-top:3px;padding:2px;" closed="true">
				<a id="apptoday" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',selected:true,plain:true" 
					onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('T');searchForm_flag='searchForm';searchSchstudy();">${sessionScope.locale.get("wl.today")}</a>
		        <a  id="appyesterday" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
		        	onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('Y');searchForm_flag='searchForm';searchSchstudy();">${sessionScope.locale.get("wl.yesterday")}</a>
		        <a id="appthreeday" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
			        onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('TD');searchForm_flag='searchForm';searchSchstudy();">近三天</a>
			</div>
			
			<div id="panel2" class="easyui-panel" style="width: 100%;margin-top:3px;padding:2px;" closed="true">
				<a id="appfiveday" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
					onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('FD');searchForm_flag='searchForm';searchSchstudy();">近五天</a>
		        <a  id="appweek" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
		        	onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('W');searchForm_flag='searchForm';searchSchstudy();">近一周</a>
		        <a id="appmonth" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
		        	onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('M');searchForm_flag='searchForm';searchSchstudy();">近一个月</a>
			</div>
            
            <div id="panel3" class="easyui-panel" style="width: 100%;margin-top:3px;padding:2px;">
				<a id="apptoday1" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',selected:true,plain:true" 
					onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('T1');searchForm_flag='searchForm';searchSchstudy();">今天</a>
		        <a  id="apptomorrow" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
		        	onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('TM');searchForm_flag='searchForm';searchSchstudy();">明天</a>
		        <a id="appaftertomorrow" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
			        onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('FT');searchForm_flag='searchForm';searchSchstudy();">后天</a>
			</div>
			
			<div id="panel4" class="easyui-panel" style="width: 100%;margin-top:3px;padding:2px;">
				<a id="applastfivedays" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
					onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('LF');searchForm_flag='searchForm';searchSchstudy();">后五天</a>
		        <a  id="applastweek" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
		        	onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('LW');searchForm_flag='searchForm';searchSchstudy();">后一周</a>
		        <a id="applastmonth" class="easyui-linkbutton" style="width:75px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
		        	onclick="$('#datefrom').datebox('setValue','');$('#dateto').datebox('setValue','');$('#appdate').val('LM');searchForm_flag='searchForm';searchSchstudy();">后一个月</a>
			</div>
            
           
            <div style="width:100%;margin-top:3px;">
                <div style="width:120px;float:left;">
                    <input id="datefrom" name="datefrom" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
                    onSelect:function(){
                        $('#apptoday1').linkbutton({selected:false});
                        $('#apptomorrow').linkbutton({selected:false});
                        $('#appaftertomorrow').linkbutton({selected:false});
                        $('#applastfivedays').linkbutton({selected:false});
                        $('#applastweek').linkbutton({selected:false});
                        $('#applastmonth').linkbutton({selected:false});
                        
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
                    <input id="dateto" name="dateto" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
                    onSelect:function(){
                        $('#apptoday1').linkbutton({selected:false});
                        $('#apptomorrow').linkbutton({selected:false});
                        $('#appaftertomorrow').linkbutton({selected:false});
                        $('#applastfivedays').linkbutton({selected:false});
                        $('#applastweek').linkbutton({selected:false});
                        $('#applastmonth').linkbutton({selected:false});
                        
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
					<input class="easyui-timespinner" name="startfromtime" id="startfromtime_wl" style="width:119px;height:56px;"
						 label="开始时间:" labelPosition="top" />
				</div>
	            <div style="width:120px;float:right;">
					<input class="easyui-timespinner" name="endtotime" id="endtotime_wl" style="width:119px;height:56px;"
						 label="结束时间:" labelPosition="top" />
				</div>
			</div>
            <div style="width: 100%;margin-top: 3px;">
                <div style="width:120px;float:left;">
                    <input id="appmodality" name="modality" class="easyui-combobox" style="width:119px;height:56px;" 
                        data-options="valueField:'code',textField:'name_zh',url:'syscode/getCode?type=0004',editable:false,panelHeight:'120px',multiple:true"
                        label="检查类型：" labelPosition="top">
                </div>
                <div style="width:120px;float:right;">
                    <input id="apporderStatus" name="orderstatus" class="easyui-combobox" style="width:119px;height:56px;" 
                        data-options="valueField:'code',textField:'name_zh',url:'syscode/getCode?type=0005',value:'1',editable:false,panelHeight:'120px',
                        icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]"
                        label="检查状态：" labelPosition="top">
                </div>
            </div>
             <div style="width: 100%;margin-top: 3px;">
                 <div style="width:120px;float:left;">
					<select class="easyui-combobox" name="reportstatus" id="reportStatus" style="width:119px;height:56px;" label="${sessionScope.locale.get("wl.reportstatus")}:" labelPosition="top"
					    data-options="valueField:'code',textField:'${sessionScope.syscode_lan}',editable:false,panelHeight:'auto',
						url:'${ctx}/syscode/getCode?type=0007&addempty=true'">
		            </select>
				 </div>
				<div style="width:120px;float:right;">
                    <input id="apppatientid" name="patientid" class="easyui-textbox" style="width:119px;height:56px;"
                        label="病人编号：" labelPosition="top">
                </div>
			</div>
            <div style="width: 100%;margin-top: 3px;">
                <div style="width:120px;float:left;">
                    <input id="appstudyid" name="studyid" class="easyui-textbox" style="width:119px;height:56px;"
                        label="检查编号：" labelPosition="top">
                </div>
                 <div style="width:120px;float:right;">
                    <input id="appoutno" name="outno" class="easyui-textbox" style="width:119px;height:56px;"
                        label="门诊号：" labelPosition="top">
                </div>
            </div>
            <div style="width: 100%;margin-top: 3px;">
                <div style="width:120px;float:left;">
                    <input id="appinno" name="inno" class="easyui-textbox" style="width:119px;height:56px;"
                            label="住院号：" labelPosition="top">
                </div>
                <div style="width:120px;float:right;">
                    <input id="apppatientname" name="patientname" class="easyui-textbox" style="width:119px;height:56px;"
                        label="姓名：" labelPosition="top">
                </div>
            </div>
             <div style="width: 100%;margin-top: 3px;">
                <div style="width:120px;float:left;">
                    <input id="appcardno" name="cardno" class="easyui-textbox" style="width:119px;height:56px;"
                        label="卡号：" labelPosition="top">
                </div>
             </div>
             <input id="appdate" type="hidden" name="appdate" value="T"/>
             </form>
	   </div>
	   <div data-options="region:'south',collapsible:false,border:false" style="height:70px;padding:0px 5px;">
	       <div style="margin-top: 5px;">
                <a class="easyui-linkbutton" onclick="searchForm_flag_sch='searchForm';searchSchstudy(null);" style="width:100%;">${sessionScope.locale.get("wl.dosearch")}</a>
            </div>
            <div style="margin-top: 5px;">
                <a class="easyui-linkbutton c2" onclick="clearSearch();" style="width:100%;height:28px">${sessionScope.locale.get("wl.clearcondition")}</a>
            </div>
	   </div>
	</div>
	</div>
	<div data-options="region:'center',border:false,title:'查询结果'">
		<table id="dg_search_sch" class="easyui-datagrid"  
			data-options="rownumbers: true,showFooter: true,singleSelect:true,border:false,
			toolbar:'#tool_div_sch',footer:'#footer_div_sch',fit:true,footer:'#footer_div',pagination:true,
			loadMsg:'加载中...',emptyMsg:'没有查找到预约信息...',remoteSort:false,
			rowStyler: function(index,row){
                if (row.patientsource == 'E'){
                    return 'background-color:#F00;font-weight:bold;';
                }
            },
	        onRowContextMenu: function(e,index ,row){
	            e.preventDefault();
	            $(this).datagrid('selectRow',index);
	           $('#cmenu_sch').menu('show', {
	                left:e.pageX,
	                top:e.pageY
	            }); 
	        }
		">
			<thead data-options="frozen:true">
				<tr>
					<th data-options="field:'orderstatus',width:72,styler:columeStyler_orderstatus_sch,align:'center'" sortable="true">检查状态</th>
					<th data-options="field:'studyid',width:130,align:'center'" sortable="true">检查编号</th>
				</tr>
	        </thead>
	        <thead>
				<tr>
					<th data-options="field:'reportstatusdisplay',width:80,styler:columeStyler_reportstatus_wl" sortable="true">报告状态</th>
					<th data-options="field:'patientid',width:100,align:'center'" sortable="true">患者编号</th>
					<th data-options="field:'patientname',width:80,align:'center'" sortable="true">姓名</th>
					<th data-options="field:'admissionid',width:80,align:'center'" sortable="true">入院号</th>
					<th data-options="field:'sexdisplay',width:50,align:'center'" sortable="true">性别</th>
					<th data-options="field:'age',width:50,formatter:age_formatter,align:'center'" sortable="true">年龄</th>
					<th data-options="field:'birthdate',width:100,align:'center'" sortable="true">出生日期</th>
					<th data-options="field:'sequencenumber',width:80,align:'center'" sortable="true">序号</th>
					<th data-options="field:'appointmenttime',width:140,align:'center',formatter:appointtime_formatter,styler:appointtime_styler" sortable="true">预约日期</th>
					<th data-options="field:'modality_type',width:65" sortable="true">设备类型</th>
					<th data-options="field:'modalityname',width:120" sortable="true">检查设备</th>
					<th data-options="field:'studyitems',width:200,align:'center'" sortable="true">检查项目</th>
					<th data-options="field:'inno',width:60,align:'center'" sortable="true">住院号</th>
					<th data-options="field:'outno',width:60,align:'center'" sortable="true">门诊号</th>
					<th data-options="field:'wardno',align:'center'" sortable="true">病区</th>
					<th data-options="field:'report_takenaway',align:'center',formatter:setColumnForeColor_reg" sortable="true">取报告</th>
					<th data-options="field:'film_takenaway',align:'center',formatter:setColumnForeColor_reg" sortable="true">取片</th>
				</tr>
			</thead>
		</table>
			
	    <div id="tool_div_sch" style="padding:2px 5px;">
	     <c:if test="${quicksearch}">
            <input type="text" id="quicksearch-input_sch" name="search" class="easyui-searchbox" title="搜索内" 
                data-options="prompt:'请输入您要查询的信息',searcher:quickSearch_sch,width:300,menu:'#quicksearch_mm'">
               <div id="quicksearch_mm">
                <div data-options="name:'all'">全部</div>
                <div data-options="name:'join'">关联</div>
            </div>
            <i class="icon iconfont icon-info easyui-tooltip" style="font-size:13px;" title="搜索内容后加  * 查询，搜索出的内容为包含 此内容的全部数据。如：搜索 '李*'，
                                       搜索出的数据将是所有开头为 '李' 的数据"></i>
          </c:if>
	     <shiro:hasPermission name="save_schedule">
	    	<a class="easyui-linkbutton" plain="true" onClick="modifyInfo()"style="margin-left: 10px;"><i class="iconfont icon-edit"></i> 修改</a>
	     </shiro:hasPermission>
	     <!-- 签到没有加权限，图标没有 -->
	     <a class="easyui-linkbutton" plain="true" onClick="signin();" style="margin-left: 10px;"><i class="iconfont icon-qiandao"></i> 签到</a>
	     <shiro:hasPermission name="advance_signin">
	     <a class="easyui-linkbutton" plain="true" onClick="advanceSignin();" style="margin-left: 10px;"><i class="iconfont icon-qiandao1"></i> 提前签到</a>
	     </shiro:hasPermission>
	     <%-- <shiro:hasPermission name="sch_to_reg">
	    	<a class="easyui-linkbutton" plain="true" onClick="sch_to_reg()"style="margin-left: 10px;"><i class="iconfont icon-dengjijiuzhenqia"></i> 转登记</a>
	     </shiro:hasPermission> --%>
	     <shiro:hasPermission name="deleteScheduleOrder_sch">
	        <a class="easyui-linkbutton" plain="true" onClick="deleteApp_sch()"style="margin-left: 10px;"><i class="iconfont icon-delete"></i> 删除预约信息</a>
	     </shiro:hasPermission>
	     <shiro:hasPermission name="cancelScheduleOrder_sch">
	        <a class="easyui-linkbutton" plain="true" onClick="cancelApp_sch()"style="margin-left: 10px;"><i class="iconfont icon-cancel1"></i> 取消预约</a>
		 </shiro:hasPermission>
		 <shiro:hasPermission name="deletePatient_sch">
			<a class="easyui-linkbutton" plain="true" onClick="deletePatient_sch()"style="margin-left: 10px;"><i class="iconfont icon-delete"></i> 删除病人信息</a>
		 </shiro:hasPermission>
		 <shiro:hasPermission name="printStudyorder_sch">
			<a id="print_sch_btn" class="easyui-linkbutton" plain="true" onClick="printReservation()" style="margin-left: 10px;"><i class="iconfont icon-print1"></i>  打印预约单</a>
		 </shiro:hasPermission>
	     <shiro:hasPermission name="getApply_sch">
	    	<a class="easyui-linkbutton" plain="true" onclick="toApply_sch();" style="margin-left: 10px;"><i class="icon iconfont icon-icon-test"></i>  申请单</a>
	     </shiro:hasPermission>
	     <shiro:hasPermission name="getStudyprocess_sch">
	    	<a class="easyui-linkbutton" plain="true" onClick="toProcess_sch();" style="margin-left: 10px;"><span class="iconfont icon-liuchengshuoming"></span>检查流程</a>
		 </shiro:hasPermission>
		  <a class="easyui-linkbutton" plain="true" onClick="takeAwayReport();" style="margin-left: 10px;"><span class="icon iconfont icon-import1"></span>  取走报告</a>
	      <a class="easyui-linkbutton" plain="true" onClick="takeAwayFilm();" style="margin-left: 10px;"><span class="icon iconfont icon-fangshe1"></span>  取走胶片</a>	
		</div>

	    <div id="cmenu_sch" class="easyui-menu" style="width:123px">
	    <shiro:hasPermission name="save_schedule">
	    	<div onclick="modifyInfo();">修改</div>
	    </shiro:hasPermission>
	    <div onclick="signin();">签到</div>
	    
	    <shiro:hasPermission name="advance_signin">
	    	<div onclick="advanceSignin();">提前签到</div>
	    </shiro:hasPermission>
	    <%-- <shiro:hasPermission name="sch_to_reg">
	    	<div onclick="sch_to_reg();">转登记</div>
	    </shiro:hasPermission> --%>
	    <shiro:hasPermission name="deleteScheduleOrder_sch">
	    	<div onclick="deleteApp_sch();">删除预约信息</div>
	    </shiro:hasPermission>
	    <shiro:hasPermission name="cancelScheduleOrder_sch">
	    	<div onclick="cancelApp_sch();">取消预约</div>
	    </shiro:hasPermission>
	    <shiro:hasPermission name="deletePatient_sch">
	    	<div onclick="deletePatient_sch();">删除病人信息</div>
	    </shiro:hasPermission>
	    <shiro:hasPermission name="printStudyorder_sch">
            <div onclick="printReservation();">打印预约单</div>
        </shiro:hasPermission>
    	<div class="menu-sep"></div>
	    <shiro:hasPermission name="getApply_sch">
	    	<div onClick="toApply_sch()">申请单</div>
	    </shiro:hasPermission>
	    <shiro:hasPermission name="getStudyprocess_sch">
	    	<div onclick="toProcess_sch();">检查流程</div>
	    </shiro:hasPermission>
	    <div class="menu-sep"></div>
	    <div onclick="takeAwayReport();">取走报告</div>
    	<div onclick="takeAwayFilm();">取走胶片</div>
    	<div onclick="openMergePatientDlg();">合并患者</div>
    	<div onclick="openCancelMergeDlg();">取消合并</div>
    	<div onclick="openReassignStudyDlg();">重新关联检查</div>
    	<div onclick="disassociationAdmission();">取消关联病人</div>
	    </div>
	    </div>
	    <div id="footer_div" style="padding:2px 5px;">
	    	<jsp:include page="footer.jsp"/>
	    </div>
	    
	    <input type="hidden" id="page"> 
		<input type="hidden" id="pageSize">
	</div>
</div>
<div class="gallerys"></div>
</body>
</html>