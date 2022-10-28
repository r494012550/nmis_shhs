<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Insert title here</title>

</head>
<body>
<style>
	.low_row .datagrid-row{height: 20px;}
	.low_header_row .datagrid-header-row{height: 25px;}
	.high_row .datagrid-row{height: 40px;}
	.textbox_highlight{
		font-weight: bold;
	}
</style>
<div class="easyui-layout low_header_row" id="exam_layout" data-options="fit:true,border:false">
	<div data-options="region:'center',collapsible:false,border:false" style="width:25%;">
		<div class="easyui-layout" data-options="fit:true,border:false">
			  	<input id="modalityId_exam" type="hidden" value="${modality.id}"/>
			  	<input id="modality_name_exam" type="hidden" value="${modality.modality_name}"/>
			  	<input id="modalityType_exam" type="hidden" value="${modality.type}"/>
			  	<input id="workingState" type="hidden" value="${modality.working_state}"/>
			  	
			 	 <form id="waitinglist_form_exam" method="POST">
			    	<input type="hidden" name="apporderStatus" value="${injected}"/>
			    	<input type="hidden" name="apporderStatus" value="${re_examine}"/>
			    	<input type="hidden" name="apporderStatus" value="${in_process}"/>
			    	<input type="hidden" name="apporderStatus" value="${modality.id}"/>
			    	<input id="appmodalityid" type="hidden" name="appmodalityid" value="${modality.id}"/>
			    	<%-- <input id="datefrom_waiting" type="hidden" name="datefrom" value="${today}"/>
			    	<input id="dateto_waiting" type="hidden" name="dateto" value="${today}"/> --%>
			    	 <input id="timeframe" type="hidden" name="timeframe" value="1"/> 
			  		 
			    	 <%-- <div style="width: 100%;margin-top: 5px;">
	                    <div style="float:left;margin-top:5px;margin-bottom:3px;margin-left: 10px;">
	                        <input class="easyui-textbox" label="检查号：" labelAlign="right"   style="width: 225px;height:28px;" 
	                               id="appstudyid" name="appstudyid" >
	                    </div>
	                    <div style="float:left;margin-top:5px;margin-bottom:3px;margin-left: 10px;">
	                        <a class="easyui-linkbutton" style="width:80px;height:28px;" onclick="refreshWaitinglist()">查询</a>
	                    </div>
	                     <div style="float:left;margin-top:7px;margin-bottom:3px;margin-left: 10px;">
	                        <span style="width:180px;font-size:16px;font-weight:bold;"  >当前设备:<b style="color:red;">${modality.modality_name}</b></span>  
	                    </div>
	                </div> --%>	
			    </form>   
			    
		  	<!-- </div> -->
		  	<div data-options="region:'center',border:false,headerCls:'panelHeaderCss_height'" class="high_row" title='检查列表'>
		       	<div id="dg_tool_exam">
		       		<a href="#" class="easyui-menubutton" data-options="menu:'#exam_mm'">分诊</a>
			        <shiro:hasPermission name="gotoReceiveDlg">
			        <a href="#" class="easyui-linkbutton" plain="true" onclick="gotoReceiveDlg()">获取病人</a>
			        </shiro:hasPermission>
			        <a href="#" class="easyui-linkbutton" plain="true" onClick="refreshWaitinglist()">刷新</a>
			        <a href="#" class="easyui-linkbutton" plain="true" onClick="openFilterDlg()">设置</a>
			    </div>
			    <div id="exam_mm" style="width:100px;">
			        <shiro:hasPermission name="gotoTriageList">
		       		<div href="#" onClick="triageList()">分诊请求</div>
			        </shiro:hasPermission>
			        <shiro:hasPermission name="gotoTriageDlg">
			        <div href="#" onClick="openTriageDlg1()">分诊</div>
			        </shiro:hasPermission>
			        <shiro:hasPermission name="gotoTriageDlg">
			        <div href="#" onClick="openTriageDlg2()">全部分诊</div>
			        </shiro:hasPermission>
			        <shiro:hasPermission name="cancelApplyTriage">
			        <div href="#" onClick="cancelApplyTriage('waitinglistdg_exam')">取消分诊</div>
			        </shiro:hasPermission>
		        </div>
				<table id="waitinglistdg_exam" class="easyui-datagrid" sortName="studyid" sortOrder="asc"
					data-options="rownumbers: true,singleSelect:true,fit:true,border:false,scrollbarSize:0,
					toolbar:'#dg_tool_exam',showFooter: true,footer:'#footer_div_exam',
					loadMsg:'加载中...',emptyMsg:'没有查找到检查信息...',
					onDblClickRow:dgDbclick_exam,
					rowStyler: function(index,row){
		                  if (row.patientsource == 'E'){
		                      return 'background-color:#F00;font-weight:bold;';
		                  }
		                  if (row.status == '13'){
		                      return 'background-color:#FFBB77;font-weight:bold;';
		                  }
			         }
					">
					<thead>
						<tr>
							<th data-options="field:'operater',align:'center',formatter:operatecolumn_startExam,width:60">操作</th>
							<th data-options="field:'orderstatus',width:80,styler:columeStyler_orderstatus_exam" sortable="true">检查状态</th>
							<th data-options="field:'sequencenumber',width:100" sortable="true">序号</th>
							<th data-options="field:'callinghistory',width:70,formatter:examinecallingstatus">叫号状态</th>
							<th data-options="field:'studyid',width:150" sortable="true">检查号</th>
							<th data-options="field:'patientid',width:100" sortable="true">病人编号</th>
							<th data-options="field:'patientname',width:80" sortable="true">姓名</th>
							<th data-options="field:'age',width:55,formatter:age_formatter" sortable="true">年龄</th>
							<th data-options="field:'birthdate',width:100" sortable="true">出生日期</th>
							<th data-options="field:'studyitems',width:200,align:'center'" sortable="true">检查项目</th>
							<th data-options="field:'appointmenttime',width:230,align:'center'" sortable="true">预约日期</th>
							<th data-options="field:'triagemodalityname',width:200,align:'center'" sortable="true">申请分诊设备</th>
						</tr>
					</thead>
				</table>
				
			    <div id="footer_div_exam" style="padding:2px 5px;border:none">
			    	<jsp:include page="footer.jsp"/>
				</div>
			    
		    </div><!-- easyui-panel -->
<!-- 		    <div data-options="region:'south',border:false" style="height:40%;">
			    
			</div> -->
		</div>
	</div><!-- region:'west' -->
	<div data-options="region:'east',collapsible:true,title:'等待新的检查...',headerCls:'panelHeaderCss_height',border:false,maximizable:true,
		hideCollapsedContent:false,collapsed:true,expandMode:'dock'" 
		style="width:75%;" id="exmaPanel">
		<input id="completetime" type="hidden">
		
		<div class="easyui-layout" data-options="fit:true,border:false">
	        <input id="orderstatus_exam" type="hidden" value="${so.status}"/>
	        <div data-options="region:'west',border:false" style="width:85%;">
	        	<div class="easyui-layout low_row" data-options="fit:true">
			        <div data-options="region:'center'" style="border-top-width:0px;">
			        	<div style="width:100%;margin-left:auto;margin-right:auto;">
				        	<form id="patient_info_exam">
				        	<input id="patientpkid_exam" type="hidden" name="patientpkid" value="${so.patientpkid}"/>
	       					<input id="admissionpkid_exam" type="hidden" name="admissionpkid" value="${so.admissionpkid}"/>
	       					<input id="studyorderid_exam" type="hidden" name="studyorderpkid" value="${so.studyorderpkid}"/>
							<input id="hasRemark" type="hidden" name="" value="${so.hasRemark}">
				        	<div style="padding-top:2px;float:left;">
				        		<input class="easyui-textbox" id="patientname_exam"  name="patientname" label="病人姓名" labelAlign="right" labelWidth="80"
									data-options="cls:'textbox_highlight'" style="width:210px;height:20px;" value="${so.patientname}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" id="patientid_exam" name="patientid" label="病人编号" labelAlign="right" labelWidth="80" value="${so.patientid}"
									editable="false" style="width:210px;height:20px;padding-top:8px; " readonly="true">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" id="studyorderstudyid_exam" name="studyid" label="检查编号" labelAlign="right" labelWidth="80" 
						  			editable="false" style="width:210px;height:20px;" value="${so.studyid}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" id="sex_exam" name="sexdisplay" label="性别" labelAlign="right" labelWidth="80" editable="false" 
									style="width:210px;height:20px;" value="${so.sexdisplay}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" id="age_exam" label="年龄" labelAlign="right" labelWidth="80" editable="false" 
									style="width:210px;height:20px;" value="${so.age}${so.ageunitdisplay}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" id="birthdate_exam" name="birthdate" label="出生日期" labelAlign="right" labelWidth="80" editable="false" 
									style="width:210px;height:20px;" value="${so.birthdate}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" id="height_exam" name="height" label="身高(cm)" labelAlign="right" labelWidth="80" editable="false" 
									style="width:210px;height:20px;" value="${so.height}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" id="weight_exam" name="weight" label="体重(kg)" labelAlign="right" labelWidth="80" editable="false" 
									style="width:210px;height:20px;" value="${so.weight}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" label="核素" labelAlign="right" labelWidth="80" editable="false" 
									id="nuclide_name" name="nuclide_name" style="width:210px;height:20px;" value="${so.nuclide_name}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" label="检查药物" labelAlign="right" labelWidth="80" editable="false" 
									id="medicine_name" name="medicine_name" style="width:210px;height:20px;" value="${so.medicine_name}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" label="检查部位" labelAlign="right" labelWidth="80" editable="false" 
									id="examination_position_name" name="examination_position_name" style="width:210px;height:20px;" value="${so.examination_position_name}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" label="检查方法" labelAlign="right" labelWidth="80" editable="false" 
									id="examination_method_name" name="examination_method_name" style="width:210px;height:20px;" value="${so.examination_method_name}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" label="问诊医生" labelAlign="right" labelWidth="80" editable="false" 
									id="interrogation_doctor_name" name="interrogation_doctor_name"
									style="width:210px;height:20px;" value="${so.interrogation_doctor_name}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" label="给药方式" labelAlign="right" labelWidth="80" editable="false" 
									id="administration_method_name" name="administration_method_name"
									style="width:210px;height:20px;" value="${so.administration_method_name}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" label="注射部位" labelAlign="right" labelWidth="80" editable="false" 
									id="injection_site_name" name="injection_site_name"
									style="width:210px;height:20px;" value="${so.injection_site_name}">
							</div>
				        	<div style="padding-top:2px;float:left;">
				        		<input class="easyui-textbox" label="注射人" labelAlign="right" labelWidth="80" editable="false" 
									id="injecter_id" name="injecter_id"
									style="width:210px;height:20px;" value="${so.injecter_id}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" label="实际注射"  labelAlign="right" labelWidth="80" editable="false" 
									id="injection_dose_real" name="injection_dose_real"
									style="width:210px;height:20px;" value="${so.injection_dose_real}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" label="污染" labelAlign="right" labelWidth="80" editable="false" 
									id="pollution_name" name="pollution_name"
									style="width:210px;height:20px;" value="${so.pollution_name}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" label="给药时间" labelAlign="right" labelWidth="80" editable="false" 
									id="injection_datetime" name="injection_datetime"
									style="width:210px;height:20px;" value="${so.injection_datetime}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" label="处方剂量" labelAlign="right" labelWidth="80" editable="false" 
									id="prescription_dose" name="prescription_dose"
									style="width:210px;height:20px;" value="${so.prescription_dose}">
							</div>
							<div style="padding-top:2px;float:left;">
								<input class="easyui-textbox" label="特殊检查"  labelAlign="right" labelWidth="80" editable="false" 
									id="sp_inspection_req_name" name="sp_inspection_req_name"
									style="width:210px;height:20px;" value="${so.sp_inspection_req_name}">
							</div>
						<!-- <div style="padding-top:2px;float:left;">
							<input class="easyui-textbox" label="责任医生" labelAlign="right" labelWidth="80" editable="false" 
								style="width:210px;height:20px;" value="">
						</div> -->
							</form>
						</div>
						<table class="easyui-datagrid" id="studyitemlist_exam" style="width:100%;height:auto;padding-top:2px;"
								data-options="url:'${ctx}/examine/getStudyItem?orderid=${so.studyorderpkid}',border:false,fitColumns:true,scrollbarSize:0">
					        <thead>
					            <tr>
					                <th data-options="field:'item_name',width:120">检查项目</th>
					                <th data-options="field:'organ',width:60">部位</th>
					                <th data-options="field:'suborgan',width:60">子部位</th>
					                <th data-options="field:'price'">计价(元)</th>
					                <th data-options="field:'realprice',align:'right'">实收(元)</th>
					                <th data-options="field:'charge_status',width:60,align:'right'">缴费状态</th>
					            </tr>
					        </thead>
						</table>
						<div class="easyui-tabs" id="tabs_div_history_exam" style="height:auto;width:100%" data-options="plain:true,narrow:true,tabHeight:30,tabWidth:140,tabPosition:'top',
							border:false,tabHeight:25">
							<div title="简要病史" id="div_history_exam" data-options="selected:true,border:false">
								<div class="easyui-panel" style="padding:2px;border:0px;width:100%">
									<div style="padding-top:2px;">
										<font style="font-weight:bold">【临床诊断】</font><span id="clinicaldiagnosis">无</span>
									</div>
									<div style="padding-top:2px;">
										<font style="font-weight:bold">【简要病史】</font><span id="briefhistory">无</span>
									</div>
									<div style="padding-top:2px;">
										<font style="font-weight:bold">【手术日期】</font><span id="operationdatetime">无</span>
										<font style="font-weight:bold">【手术方式】</font><span id="operationmethodname">无</span>
										<font style="font-weight:bold">【病理】</font><span id="pathology">无</span>	
									</div>
									<div style="padding-top:2px;">
										<font style="font-weight:bold">【末次化疗日期】</font><span id="last_chemotherapy_datetime">无</span>
										<font style="font-weight:bold">【末次放疗日期】</font><span id="last_radiotherapy_datetime">无</span>	
										<font style="font-weight:bold">【近期主要影像结果】</font><span id="image_result">无</span>		
									</div>
									<div style="padding-top:2px;">
										<font style="font-weight:bold">【肿瘤除去结果】</font><span id="tumor_removal">无</span>
									</div>
								</div>
								<div class="easyui-panel" style="padding:2px;width:100%;border-bottom:0px;border-right:0px;border-left:0px;">
									<div style="padding-top:2px;">
										<label><font color="blue">【手术缺如部位】</font></label>
									</div>
									<div style="padding-top:2px;">
										<font style="font-weight:bold">【胆囊.时间】</font><span id="gallbladder_datetime">无</span>
										<font style="font-weight:bold">【甲状腺.时间】</font><span id="thyroid_datetime">无</span>
										<font style="font-weight:bold">【脾.时间】</font><span id="spleen_datetime">无</span>
									</div>
									<div style="padding-top:2px;">
										<font style="font-weight:bold">【左肾.时间】</font><span id="left_kidney_datetime">无</span>
										<font style="font-weight:bold">【右肾.时间】</font><span id="right_kidney_datetime">无</span>
									</div>
								</div>
								<div class="easyui-panel" id="female_info" data-options="closed:true" style="padding:2px;border:0px;100%">
									<div style="padding-top:2px;">
										<font style="font-weight:bold" color="blue">【月经情况】</font><span id=menstruation_conditionsname>无</span>
										<font style="font-weight:bold">【初潮】</font><span id="menarche_age"></span>岁
										<font style="font-weight:bold">【经期】</font><span id="menstruation_periods"></span>天一次
									</div>
									<div style="padding-top:2px;">
										<font style="font-weight:bold">【每次】</font><span id="menstruation_days"></span>天
										<font style="font-weight:bold">【近期月经】</font><span id="menstruation_recent"></span>天
									</div>
								</div>
								<div style="padding:2px;">
									<font color="blue">【问诊其他信息】</font><span id="other_information">无</span>
								</div>
							</div>
							<div title="既往病史" id="div_previous_history_exam" style="padding:2px;">
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【近期发热史】</font><span id="recent_fever_history">无</span>
									<font style="font-weight:bold">【持续时间（天）】</font><span id="recent_fever_duration">0</span>
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【脑梗病史】</font><span id="cerebral_infarction_history">无</span>
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【脑血管病史】</font><span id="cerebral_vessels_history">无</span>
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【部位】</font><span id="trauma_place">无</span>
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【糖尿病】</font><span id="diabetes">无</span>
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【高血压病史】</font><span id="hypertension">无</span>
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【外伤史.时间】</font><span id="trauma_datetime">无</span>
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【家族肿瘤史】</font><span id="family_tumor">无</span>
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【近期钡餐等检查】</font><span id="recent_examine">无</span>
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【近期升白细胞药使用.种类】</font><span id="recent_leukocyte_medicine">无</span>
								</div>
								<div style="padding-top:2px;">
									<label><font color="blue">【其他病史】：</font></label>
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【结核】</font><span id="tuberculosis">无</span>
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【血吸虫病史】</font><span id="schistosomiasis_history">无</span>
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【肝炎、硬化病史】</font><span id="hepatitis_and_sclerosis">无</span>
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【痔疮病史】</font><span id="hemorrhoids_history">无</span>
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【吸烟史】</font><span id="smoking_history">无</span>
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【其它】</font><span id="other_medical_history">无</span>
								</div>
								<div style="padding-top:2px;">
									<label><font color="blue">【留存资料】：</font></label>
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【CT/MR 片】</font><span id="ctAndMr">0</span>张/份
									<font style="font-weight:bold">【报告】</font><span id="presentation">0</span>张/份
									<font style="font-weight:bold">【超声报告】</font><span id="ultrasonic_eport">0</span>张/份
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【胃镜报告】</font><span id="gastroscopy_report">0</span>张/份
									<font style="font-weight:bold">【肠镜报告】</font><span id="enteroscopy_report">0</span>张/份
									<font style="font-weight:bold">【鼻咽镜报告】</font><span id="npg_report">0</span>张/份
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【喉镜报告】</font><span id="laryngoscope_report">0</span>张/份
									<font style="font-weight:bold">【气管镜报告】</font><span id="tracheoscope_report">0</span>张/份
									<font style="font-weight:bold">【外院PET报告】</font><span id="out_PET_report">0</span>张/份
								</div>
								<div style="padding-top:2px;">
									<font style="font-weight:bold">【本院PET报告】</font><span id="in_PET_report">0</span>张/份
									<font style="font-weight:bold">【出院小结】</font><span id="dischargeSummary">0</span>张/份
									<font style="font-weight:bold">【其它】</font><span id="other">0</span>张/份
								</div>
							</div>
						</div>
				        
			        </div>
			        <div data-options="region:'south'" style="height:125px;">
			        	<div class="easyui-panel" data-options="title:'采集方式及时间',headerCls:'panelHeaderCss_height',border:false,fit:true">
				             <form id="collectionForm_exam" name="saveCollectionForm" method="POST">
	                            <div style="width:100%;margin-left:auto;margin-right:auto;">
	                            	<div style="padding-top:2px;float:left;">
	                            		<input class="easyui-datetimebox" label="上机时间" id="on_equipment_time" name="on_equipment_time" labelAlign="right" labelWidth="80"
	                                    	style="width:250px;height:28px;" value="">
	                            	</div>
	                            	<div style="padding-top:2px;float:left;">
	                            		<input class="easyui-combobox" style="width:250px;height:28px;" label="污染情况"  name="exam_pollution" labelAlign="right" labelWidth="80"
	                                         data-options="url:'${ctx}/dic/findDicCommonFromCache?group=pollution',valueField:'id',textField:'name',
	                                            icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }],
	                                            editable:false,panelHeight:'auto',panelMaxHeight:'300',value:'${studyacquisitionmodedefault.exam_pollution}'">
	                            	</div>
	                            	<div style="padding-top:2px;float:left;">
	                            		 <input class="easyui-combobox" style="width:250px;height:28px;" label="延迟采集" name="delayed_acquisition" labelAlign="right" labelWidth="80"
	                                         data-options="url:'${ctx}/dic/findDicCommonFromCache?group=delayedacquisition',valueField:'id',textField:'name',
	                                            icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }],
	                                            editable:false,panelHeight:'auto',panelMaxHeight:'300',value:'${studyacquisitionmodedefault.delayed_acquisition}'">
	                            	</div>
	                            	<div style="padding-top:2px;float:left;">
	                            		<input class="easyui-datetimebox" label="采集时间" labelAlign="right" labelWidth="80" id="delayed_acquisition_time" name="delayed_acquisition_time"
	                                         style="width:250px;height:28px;" value="">
	                            	</div>
	                            	<div style="padding-top:2px;float:left;">
	                            		<input  class="easyui-combobox" style="width:250px;height:28px;" label="延迟采集2" name="delayed_acquisition1" labelAlign="right" labelWidth="80"
	                                         data-options="url:'${ctx}/dic/findDicCommonFromCache?group=delayedacquisition',valueField:'id',textField:'name',
	                                            icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }],
	                                            editable:false,panelHeight:'auto',panelMaxHeight:'300',value:'${studyacquisitionmodedefault.delayed_acquisition1}'">
	                            	</div>
	                            	<div style="padding-top:2px;float:left;">
	                            		<input class="easyui-datetimebox" label="采集时间2" labelAlign="right" labelWidth="80" id="delayed_acquisition_time1" name="delayed_acquisition_time1"
	                                         style="width:250px;height:28px;" value="">
	                            	</div>
	                            	<div style="padding-top:2px;padding-left:2px;padding-right:2px;float:right;">
	                            		<a class="easyui-linkbutton" id="save_acqMode__btn" onclick="saveStudyAcquisitionModeDefault();" style="width:100px;height:28px">保存为默认</a>
	                            	</div>
	                                <%-- <tr>
	                                    <td>
	                                        <input class="easyui-combobox" style="width:95%;height:28px;" label="机房：" labelAlign="right"
	                                            data-options="textField:'text',valueField:'id',panelHeight:'auto',panelMaxHeight:'300',value:''">
	                                    </td>
	                                    <td>
	                                        <input class="easyui-combobox" style="width:220px;height:28px;" label="操作人：" labelAlign="right"
	                                        data-options="valueField:'id',textField:'name',url:'${ctx}/dic/getEmployeeFromCache?profession=N&deptcode=${deptcode}'
	                                            ,icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }],
	                                            editable:false,panelHeight:'auto',panelMaxHeight:'300',value:''">
	                                    </td>
	                                </tr> --%>
	                            </div>
	                        </form>
				        </div>
			        </div>
			    </div>
	        </div>
	        
	        <div data-options="region:'center',border:false" style="width:15%;padding:10px;">
	            
	        	<%-- <shiro:hasPermission name="startStudyOrder">
	        	<div>
					<a class="easyui-linkbutton c7" id="next_btn" onclick="nextStudy();" style="width:150px;height:70px;">下一个</a>
				</div>
				&nbsp;
				</shiro:hasPermission> --%>
				<shiro:hasPermission name="completeStudyOrder">
				<div style="padding-top:0px;">
					<a class="easyui-linkbutton c1" id="complete_btn" onclick="completeApp('${modality.modality_name}');" style="width:100%;height:60px">检查完成</a>
				</div>
				</shiro:hasPermission>
				<div style="padding-top:5px;">
                   <a class="easyui-linkbutton" id="diccall_btn" onclick="callingSetting('${modality.modality_name}');" style="width:100%;height:60px;">叫号</a>
                </div>
<%-- 				<shiro:hasPermission name="repeatCall"> --%>
<!-- 				<div> -->
<!-- 					<a class="easyui-linkbutton" id="repeat_btn" onclick="repeatCall();" style="width:150px;height:45px">重复叫号</a> -->
<!-- 				</div> -->
<!-- 				&nbsp; -->
<%-- 				</shiro:hasPermission> --%>
				<shiro:hasPermission name="cancelStudyOrder">
				<div style="padding-top:5px;">
					<a class="easyui-linkbutton c2" id="cancel_btn" onclick="examinecancel();" style="width:100%;height:40px;">检查取消</a>
				</div>
				</shiro:hasPermission>
				<shiro:hasPermission name="skipStudyOrder">
				<div style="padding-top:5px;">
					<a class="easyui-linkbutton" id="skip_btn" onclick="skipApp();" style="width:100%;height:40px">过号</a>
				</div>
				</shiro:hasPermission>
				<shiro:hasPermission name="getApply">
				<div style="padding-top:5px;">
					<a class="easyui-linkbutton" id="appform_btn" onclick="toApply();" style="width:100%;height:40px">申请单</a>
				</div>
				</shiro:hasPermission>
				<shiro:hasPermission name="getStudyprocess">
				<div style="padding-top:5px;">
					<a class="easyui-linkbutton" id="process_btn" onclick="toProcess();" style="width:100%;height:40px">检查流程</a>
				</div>
				</shiro:hasPermission>
				<shiro:hasPermission name="openRemark_examine">
				<div style="padding-top:5px;">
					<a class="easyui-linkbutton" id="remark_btn" onclick="openRemarkDialog();" style="width:100%;height:40px">病人备注</a>
				</div>
				</shiro:hasPermission>
				<div style="padding-top:5px;">
					<a class="easyui-linkbutton" id="time_btn" onclick="openCompletetimeDlg();" style="width:100%;height:40px">检查时间</a>
				</div>
				<div style="padding-top:5px;">
					<a class="easyui-linkbutton c8" id="time_btn" onclick="maxOrRestoreExam(this);" data-options="toggle:true" style="width:100%;height:40px">最大化</a>
				</div>
				<div style="padding-top:5px;">
					<a class="easyui-linkbutton c6" id="time_btn" onclick="collapsedExamPanel();" style="width:100%;height:40px">返回列表</a>
				</div>
	        </div>
	    	
	    </div>
	</div>
</div>
</body>
</html>