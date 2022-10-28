<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'west',collapsible:false,title:'查询条件',headerCls:'panelHeaderCss_top'" style="width:280px;padding:2px 0px;">
		<header>查询条件</header>
		<div class="easyui-layout" data-options="fit:true,border:false">
			<div data-options="region:'center',border:false" style="padding:0px 5px;">
				<div class="easyui-datalist" id="myfilterlist_register" title="我的条件" style="width:100%;height:220px"
				  data-options="valueField:'id',textField:'description',tools:'#filter_tools_reg',
			            url: '${ctx}/register/getFilters?filterType=register',
			            onSelect:function(index,row){
			            	searchForm_flag='searchForm';
			            	fillSearchParams();
			            }">
			    </div>
		          
				<div id="filter_tools_reg">
					<shiro:hasPermission name="save_Regfilter">
		  			<a class="easyui-tooltip" title="保存查询条件" onclick="openFilterSaveDialog('register');">
		  				<i class="icon iconfont icon-plus7" style="font-size:15px;vertical-align:2px;text-align: 2px;"></i>
		  			</a>
		      		</shiro:hasPermission>
		      		<shiro:hasPermission name="manage_Regfilter">
		     		<a class="easyui-tooltip" title="管理查询条件" onclick="openFilterManageDialog('register');">
		     			<i class="icon iconfont icon-set2" style="font-size:15px;vertical-align:2px;text-align: center;"></i>
		     		</a>
		      		</shiro:hasPermission>
		          	<a class="easyui-tooltip" title="清空" onclick="$('#myfilterlist_register').datalist('clearSelections');">
		          		<i class="icon iconfont icon-qingkong" style="font-size:15px;vertical-align:2px;text-align: center;"></i>
		          	</a>
				</div>
				
				<form name="searchForm" id="searchForm_register" method="POST">
					<div style="width:100%;margin-top:3px;" class="mylabel">
						<select class="easyui-combobox" name="datetype" id="datetype" style="width:100%;height:50px;" label="${sessionScope.locale.get("wl.datetime")}:" labelPosition="top" 
							data-options="editable:false,panelHeight:'auto'">
							<option value="registertime">${sessionScope.locale.get("wl.registertime")}</option>
			            	<%-- <option value="appointmenttime">${sessionScope.locale.get("wl.appointmenttime")}</option> --%>
			                <option value="studytime">${sessionScope.locale.get("wl.studytime")}</option>
			                <!-- <option value="reporttime">报告时间</option> -->
			            </select>
		            </div>
		
					<div class="easyui-panel" style="width:100%;margin-top:3px;padding:2px;">
						<a id="apptoday" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',selected:true,plain:true" 
							onclick="$('#appdatefrom').datebox('setValue','');$('#appdateto').datebox('setValue','');$('#appdate').val('T');searchForm_flag='searchForm';searchstudy();">${sessionScope.locale.get("wl.today")}</a>
				        <a  id="appyesterday" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
				        	onclick="$('#appdatefrom').datebox('setValue','');$('#appdateto').datebox('setValue','');$('#appdate').val('Y');searchForm_flag='searchForm';searchstudy();">${sessionScope.locale.get("wl.yesterday")}</a>
				        <a id="appthreeday" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
				        	onclick="$('#appdatefrom').datebox('setValue','');$('#appdateto').datebox('setValue','');$('#appdate').val('TD');searchForm_flag='searchForm';searchstudy();">近三天</a>
					</div>
					
					<div class="easyui-panel" style="width:100%;margin-top:3px;padding:2px;">
						<a id="appfiveday" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
							onclick="$('#appdatefrom').datebox('setValue','');$('#appdateto').datebox('setValue','');$('#appdate').val('FD');searchForm_flag='searchForm';searchstudy();">近五天</a>
				        <a  id="appweek" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
				        	onclick="$('#appdatefrom').datebox('setValue','');$('#appdateto').datebox('setValue','');$('#appdate').val('W');searchForm_flag='searchForm';searchstudy();">近一周</a>
				        <a id="appmonth" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
				        	onclick="$('#appdatefrom').datebox('setValue','');$('#appdateto').datebox('setValue','');$('#appdate').val('M');searchForm_flag='searchForm';searchstudy();">近一个月</a>
					</div>
		
					<div style="width:100%;margin-top:3px;">
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
					<div style="width: 100%;margin-top: 3px;" class="mylabel">
						<div style="width:120px;float:left;">
							<input id="apppatientid" name="patientid" class="easyui-textbox" style="width:119px;height:50px;"
							label="病人编号：" labelPosition="top">
						</div>
						<div style="width:120px;float:right;">
							<input id="appstudyid" name="studyid" class="easyui-textbox" style="width:119px;height:50px;"
							label="检查号：" labelPosition="top">
						</div>
					</div>
					
					<div style="width: 100%;margin-top: 3px;" class="mylabel">
						<div style="width:120px;float:left;">
							<input id="appmodality" name="modality" class="easyui-combobox" style="width:119px;height:50px;" 
								data-options="valueField:'code',textField:'name_zh',multiple:true,
								url:'syscode/getCode?type=0004',editable:false,panelHeight:'120px'"
								label="检查类型：" labelPosition="top">
						</div>
						<div style="width:120px;float:right;">
							<input id="apporderStatus" name="orderstatus" class="easyui-combobox" style="width:119px;height:50px;" 
								data-options="valueField:'code',textField:'name_zh',url:'syscode/getCode?type=0005',value:'3',editable:false,panelHeight:'auto',
								icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]"
								label="检查状态：" labelPosition="top">
			            </div>
					</div>
						
					<!-- <div style="width: 100%;margin-top: 3px;" class="mylabel">
						<div style="width:120px;float:left;">
							<input id="appoutno" name="appoutno" class="easyui-textbox" style="width:119px;height:50px;"
							label="门诊号：" labelPosition="top">
						</div>
						<div style="width:120px;float:right;">
							<input id="appinno" name="appinno" class="easyui-textbox" style="width:119px;height:50px;"
							label="住院号：" labelPosition="top">
						</div>
					</div> -->
						
					<div style="width: 100%;margin-top: 3px;" class="mylabel">
						<div style="width:120px;float:left;">
							<input id="apppatientname" name="patientname" class="easyui-textbox" style="width:119px;height:50px;"
							label="姓名：" labelPosition="top">
						</div>
						<div style="width:120px;float:right;">
							<input class="easyui-combobox" name="reportstatus" id="appreportStatus" style="width:119px;height:50px;"
							    data-options="valueField:'code',textField:'${sessionScope.syscode_lan}',editable:false,panelHeight:'auto',
								url:'${ctx}/syscode/getCode?type=0007&addempty=true'"
								label="${sessionScope.locale.get("wl.reportstatus")}：" labelPosition="top">
				        </div>
				        <div style="width:120px;float:left;">
							<input class="easyui-textbox" name="idnumber" id="idnumber_wl" style="width:119px;height:50px;"
									label="身份证:" labelPosition="top" />
						</div>
					</div>
						
					
					<input id="appdate" type="hidden" name="appdate" value="T"/>
					</form>
			</div>
			<div data-options="region:'south',collapsible:false,border:false" style="height:70px;padding:0px 5px;">
				
				<div style="margin-top: 5px;">
					<a class="easyui-linkbutton" onclick="searchForm_flag='searchForm';searchstudy()" style="width:100%;">查询</a>
				</div>
				<div style="margin-top: 5px;">
						<a class="easyui-linkbutton c2" onclick="clearSearch()" style="width:100%;">条件清除</a>
				</div>
			</div>
		</div>
	</div>
	<div data-options="region:'center',border:false" >
		<!-- <header>查询结果</header> ,title:'查询结果'-->
		<table id="dg1_reg" class="easyui-datagrid"  border="0"
			data-options="rownumbers: true,showFooter: true,singleSelect:true,remoteSort:false,
			toolbar:'#tool_div_reg',fit:true,border:false,pagination:true,
			loadMsg:'加载中...',emptyMsg:'没有查找到登记信息...',
			
	        onRowContextMenu: function(e,index ,row){
	            e.preventDefault();
	            $(this).datagrid('selectRow',index);
	            $('#cmenu_reg').menu('show', {
	                left:e.pageX,
	                top:e.pageY
	            });
	        },
	        view: detailview,
            detailFormatter:detailFormatter_wl_previewreport,
            onExpandRow:onExpandRow_wl_previewreport
		">
			<thead data-options="frozen:true">
	            <tr>
	            	<th data-options="field:'priority',width:30,formatter:priorityFormat,align:'center'" sortable="true"></th>
	            	<th data-options="field:'studyid',width:90,styler:columeStyler_studyid" sortable="true">检查号</th>
	                <th data-options="field:'orderstatus',width:70,styler:columeStyler_orderstatus_reg" sortable="true">检查状态</th>
	                <th data-options="field:'reportstatusdisplay',width:80,styler:columeStyler_reportstatus_reg" sortable="true">报告状态</th>
	                <!-- <th data-options="field:'sequencenumber',width:80,align:'center'" sortable="true">序号</th> -->
	            </tr>
	        </thead>
			<thead>
				<tr>		
					<th data-options="field:'patientname',width:80" sortable="true">病人姓名</th>
					<th data-options="field:'sexdisplay',width:50" sortable="true">性别</th>
					<th data-options="field:'birthdate',width:90" sortable="true">出生日期</th>
					<th data-options="field:'age',width:50,formatter:age_formatter" sortable="true">年龄</th>
					<th data-options="field:'idnumber',width:120" sortable="true">身份证号</th>
					<th data-options="field:'height',width:60" sortable="true">身高(cm)</th>
					<th data-options="field:'weight',width:60" sortable="true">体重(kg)</th>
					<th data-options="field:'studyitems',width:200,align:'center'" sortable="true">检查项目</th>
					<th data-options="field:'modality_type',width:65" sortable="true">设备类型</th>
					<th data-options="field:'modalityname',width:120" sortable="true">检查设备</th>
					<th data-options="field:'creatorname',width:80" sortable="true">登记员</th>
					<th data-options="field:'regsite',width:80" sortable="true">登记区域</th>
					<th data-options="field:'regdatetime',width:150,align:'center'" sortable="true">登记日期</th>
					<!-- <th data-options="field:'appointmenttime',width:150,align:'center'" sortable="true">预约日期</th> -->
					<th data-options="field:'studydatetime',width:150,align:'center'" sortable="true">检查日期</th>
					<th data-options="field:'reportprintcount',width:80,formatter:printcount_formatter_reg" sortable="true">报告是否打印</th>
					<th data-options="field:'patientid',width:80" sortable="true">患者编号</th>
					<th data-options="field:'admissionid',width:80" sortable="true">入院编号</th>
					<!-- <th data-options="field:'inno',width:60,align:'center'" sortable="true">住院号</th>
					<th data-options="field:'outno',width:60,align:'center'" sortable="true">门诊号</th>
					<th data-options="field:'wardno',align:'center'" sortable="true">病区</th> -->
				</tr>
			</thead>
		</table>
		
	    <div id="tool_div_reg" style="padding:2px 5px;">
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
	      <shiro:hasPermission name="save_register">
	    	<a href="#" class="easyui-linkbutton" plain="true" onClick="modifyApp()"><i class="iconfont icon-edit"></i>&nbsp;修改</a>
	      </shiro:hasPermission>
	      
	      <c:if test="${hasSchedule}">
	      <shiro:hasPermission name="reg_to_sch">
	    	<a href="#" class="easyui-linkbutton" plain="true" onClick="toSchedule()"><i class="iconfont icon-yuyue1"></i>&nbsp;转预约</a>
	   	  </shiro:hasPermission>
	   	  </c:if>
	   	  
	   	  <shiro:hasPermission name="deleteStudyorder_reg">
	        <a href="#" class="easyui-linkbutton" plain="true" onClick="deleteApp()"><i class="iconfont icon-delete"></i>&nbsp;删除检查</a>
	      </shiro:hasPermission>
	      <shiro:hasPermission name="cancelStudyorder_reg"> 
	        <a href="#" class="easyui-linkbutton" plain="true" onClick="cancelApp()"><i class="iconfont icon-qingkong"></i>&nbsp;取消检查</a>
	      </shiro:hasPermission> 
	      <!-- <a href="#" class="easyui-linkbutton" plain="true" onclick="cancelChecked()"><i class="iconfont icon-qingkong"></i>&nbsp;重新检查</a> -->
	      <shiro:hasPermission name="deletePatient_reg">
	        <a href="#" class="easyui-linkbutton" plain="true" onClick="deletePatient()"><i class="iconfont icon-delete"></i>&nbsp;删除患者</a>
	      </shiro:hasPermission>
	      <shiro:hasPermission name="printStudyorder_reg">
	        <a href="#" class="easyui-linkbutton" plain="true" onClick="printApp()"><i class="iconfont icon-print1"></i>&nbsp;打印检查单</a>
	      </shiro:hasPermission>
	      <shiro:hasPermission name="getApply_reg">
	        <a class="easyui-linkbutton" plain="true" onclick="toApply();" style="margin-left: 10px;"><i class="iconfont icon-icon-test"></i>  申请单</a>
	        <a class="easyui-linkbutton" plain="true" onclick="reTriggerScan();" style="margin-left: 10px;"><i class="iconfont icon-icon-test"></i>  补扫申请单</a>
		  </shiro:hasPermission>
		  <shiro:hasPermission name="getStudyprocess_reg">
		    <a class="easyui-linkbutton" plain="true" onClick="toProcess();" style="margin-left: 10px;"><span class="iconfont icon-liuchengshuoming"></span>  检查流程</a>
	      </shiro:hasPermission>
	    </div>
	    <div id="footer_div_reg" style="padding:2px 5px;">
		    <jsp:include page="footer.jsp"/>
		</div>
		
	    <div id="cmenu_reg" class="easyui-menu" style="width:120px">
	    <shiro:hasPermission name="save_register">
	    	<div onclick="modifyApp();">修改</div>
	    </shiro:hasPermission>
	    
	    <c:if test="${hasSchedule}">
	    <shiro:hasPermission name="reg_to_sch">
	    	<div onclick="toSchedule();">转预约</div>
	    </shiro:hasPermission>
	    </c:if>
	    
	   	<shiro:hasPermission name="deleteStudyorder_reg">
	    	<div onclick="deleteApp();">删除检查</div>
	    </shiro:hasPermission>
	    <shiro:hasPermission name="cancelStudyorder_reg"> 
	    	<div onclick="cancelApp();">取消检查</div>
	    </shiro:hasPermission> 
	       <div onclick="cancelChecked()">取消已检查</div>
	    
	    <shiro:hasPermission name="deletePatient_reg">
	    	<div onclick="deletePatient();">删除患者信息</div>
	    </shiro:hasPermission>
	    <shiro:hasPermission name="printStudyorder_reg">
	    	<div onclick="printApp();">打印检查单</div>
	    </shiro:hasPermission>
	    <shiro:hasPermission name="getApply_reg">
	    	<div class="menu-sep"></div>
	    	<div onclick="toApply();"> 申请单</div>
	    	<div onclick="reTriggerScan();"> 补扫申请单</div>
	    	<div onclick="goToUploadScanimg();"> 上传申请单</div>
	    	<div class="menu-sep"></div>
	    </shiro:hasPermission>
		<shiro:hasPermission name="getStudyprocess_reg">
	    	<div onclick="toProcess();">检查流程</div>
	    </shiro:hasPermission>
	    
	    <div data-options="name:'report'" onClick="exportReport();">导出PDF</div>
	    
	    <shiro:hasPermission name="mergePatient_reg">
	    	<div class="menu-sep"></div>
	    	<div onclick="openMergePatientDlg();">合并患者</div>
	    </shiro:hasPermission>
	    	<div onclick="openCancelMergeDlg();">取消合并</div>
	    	<div onclick="openReassignStudyDlg();">重新关联检查</div>
	    	<!-- <div onclick="disassociationAdmission();">取消关联病人</div> -->
	    </div>
	</div>
	
	<div class="gallerys">
		<!-- <img id="imageShow1" src="" class="gallery-pic" onclick="$.openPhotoGallery(this)"/>
		<img id="imageShow2" src="" class="gallery-pic" onclick="$.openPhotoGallery(this)"/>
		<img id="imageShow3" src="" class="gallery-pic" onclick="$.openPhotoGallery(this)"/> -->
	</div>
</div>
	
</body>
</html>