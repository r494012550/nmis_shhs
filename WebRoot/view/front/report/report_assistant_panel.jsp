<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
<div class="easyui-tabs" data-options="fit:true,plain:true,justified:true,narrow:true,border:false">
<div title="常用" style="border:0;">	
	<div class="easyui-layout" data-options="fit:true,border:false" >
	 	<div data-options="region:'north',title:'标签',split:true,border:false,
		        	hideCollapsedContent:false,tools:'#reportlabel_tools_${reportid}'" style="height:230px;padding:2px;">
        	<input class="easyui-combotree" id="publiclabelct_${reportid}" data-options="url:'${ctx}/report/getReportLabel?ispublic=1&reportid=${reportid}',
                      label:'公共标签:',labelPosition:'top',textField:'display',onlyLeafCheck:true,
                      labelPosition:'top',multiple:true,
		                onBeforeCheck:function(node, checked){
		                	if(checked&&node.labelid==null){
		                		return false;
		                	}
		                }" style="width:100%;" value="${publiclabelvalue}">
                  
            <input class="easyui-combotree" privatelabelname="privatelabel_combotree" id="privatelabelct_${reportid}" data-options="url:'${ctx}/report/getReportLabel?ispublic=0&reportid=${reportid}',
                label:'个人标签:',labelPosition:'top',textField:'text',onlyLeafCheck:true,
                labelPosition:'top',multiple:true,
	                onBeforeCheck:function(node, checked){
	                	if(checked&&node.labelid==null){
	                		_message('请选择标签，当前选中的是文件夹！！' , '提醒');
	                		return false;
	                	}
	                }" style="width:100%;" value="${privatelabelvalue}">
                
            <div id="reportlabel_tools_${reportid}" style="padding:2px 5px;">
            	 <a class="easyui-tooltip" title="保存报告标签" onclick="saveReportLabel(${reportid});">
                    <i class="icon iconfont icon-baocun" style="text-align: center;vertical-align:2px;"></i></a>	
                <a class="easyui-tooltip" title="管理个人标签" onclick="openLabelDialog();">
                     <i class="icon iconfont icon-set2" style="text-align: center;vertical-align:2px;"></i>
                </a>
            </div>
            
            <input class="easyui-combobox" id="icd10labelct_${reportid}" style="width:90%;"
                data-options="url:'${ctx}/report/getIcd10?reportid=${reportid}',
                label:'ICD10:',editable:false,textField:'text',valueField:'id',
                labelPosition:'top',multiple:true,">
            <a class="easyui-tooltip" title="ICD查询" onclick="searchIcd10(${reportid});">
                <i class="icon iconfont icon-plus7" style="font-size:15px;vertical-align:2px;text-align: 2px;"></i>
            </a>
		        	
		</div>
		<div data-options="region:'center',title:'备注',border:false,tools:'#reportremark_tools_${reportid}'">           
	         <table class="easyui-datagrid" id="reportremark_dg_${reportid}" data-options="url:'${ctx}/report/getRemarks?orderid=${orderid}',fit:true,fitColumns:true,
	              scrollbarSize:0,singleSelect:true,border:false,onDblClickRow:function(index,row){openRemarkDialog(${reportid},${orderid});},
	              rowStyler:function(index,row){return 'color:#FF6A00;';}">
	              <thead>
	                  <tr>
	                      <th data-options="field:'creator_name',width:100">创建人</th>
	                      <th data-options="field:'modifytime',width:208">修改时间</th>
	                  </tr>
	              </thead>
	          </table>
	          
	          <div id="reportremark_tools_${reportid}" style="padding:2px 5px;">
	              <a class="easyui-tooltip" title="查看" onclick="openRemarkDialog(${reportid},${orderid});">
	                  <i class="icon iconfont icon-info" style="text-align: center;vertical-align:2px;"></i></a>
	              <a class="easyui-tooltip" title="${sessionScope.locale.get("delete")}" onclick="delReportRemark(${reportid});">
	                  <i class="icon iconfont icon-delete" style="font-size:15px;vertical-align:2px;text-align: center;"></i></a>
	          </div>
		</div>
                
                
		<div data-options="region:'south',border:false,collapsible:false,title:'${sessionScope.locale.get("report.history")}',tools:'#reporthistory_tools_${reportid}'" style="height:45%">
	        	<div id="toolbar_div_history_${reportid}" style="padding:2px 2px;">
			 		<input class="easyui-searchbox" data-options="prompt:'${sessionScope.locale.get('report.inputstudyid')}...',searcher:function(value){
	                            doHistoryReportSearch(value,'${patientidfk}',${orderid},${reportid})
	                        }" style="width:100%;hight:30px;">	
				</div>
		 		<table class="easyui-datagrid" id="history_report_${reportid}" style="width:194px;hight:300px;"
		            data-options="url:'${ctx}/report/historyReport?patientidfk=${patientidfk}&thisOrderid=${orderid}',showHeader:false,fit:true,border:false,
		            toolbar:'#toolbar_div_history_${reportid}',
		            onDblClickRow:function(index,row){openHistoryReport(${reportid},row)},
		            onClickRow:function(index,row){
		            	contrastImage(index,row,${reportid});
		            }">
			        <thead>
			            <tr>
			                 <th data-options="field:'studyid',width:100"></th>
			                 <th data-options="field:'patientname',width:100"></th>
			                 <th data-options="field:'studydatetime',width:120"></th>
			                 <th data-options="field:'studyitems',width:120"></th>
			            </tr>
			        </thead>
	 			</table>
	 			<div id="reporthistory_tools_${reportid}" style="padding:2px 5px;">
	 				<c:if test="${enable_plaza_callup==1}">
						<a class="easyui-tooltip" title="图像对比" id="contrastimg_btn_${reportid}" href1="${plaza_loaddata}" onclick="closeReportFlag=false;urlFlag = true;">
							<i class="icon iconfont icon-fangshe1" style="text-align: center;vertical-align:2px;"></i></a>
					</c:if>
			    </div>
		</div> 	
		       	
	</div>
</div>
<div title="问诊信息" style="border:0;">
	    <div class="easyui-tabs" id="tabs_div" style="height:100%;width:100%" data-options="plain:true,narrow:true,justified:true,tabPosition:'bottom',fit:true,border:false">
			<div title="简要病史" data-options="selected:true,border:false">
				<div class="easyui-panel" data-options="fit:true,border:false" style="padding:0px;margin:auto;">
					<div class="easyui-panel" style="padding:5px 5px 5px 5px;border:0px">
						<div class="history_content_div" style="padding-top:8px">
							<font style="font-weight:bold">【临床诊断】</font>${inquiry.clinicaldiagnosis eq null ?'无':inquiry.clinicaldiagnosis}
						</div>
						<div class="history_content_div" style="padding-top:8px">
							<font style="font-weight:bold">【简要病史】</font>${inquiry.briefhistory eq null ?'无':inquiry.briefhistory}
						</div>
						<div class="history_content_div" style="padding-top:8px">
							<font style="font-weight:bold">【手术日期】</font>${inquiry.operationdatetime eq null ?'无':inquiry.operationdatetime}
						</div>
						<div class="history_content_div" style="padding-top:8px">
							<font style="font-weight:bold">【手术方式】</font>${inquiry.operationmethodname eq null ?'无':inquiry.operationmethodname}	
						</div>
						<div class="history_content_div" style="padding-top:8px">
							<font style="font-weight:bold">【病理】</font>${inquiry.pathology eq null ?'无':inquiry.pathology}
						</div>
						<div class="history_content_div" style="padding-top:8px">
							<font style="font-weight:bold">【末次化疗日期】</font>${inquiry.last_chemotherapy_datetime eq null ?'无':inquiry.last_chemotherapy_datetime}
						</div>
						<div class="history_content_div" style="padding-top:8px">
							<font style="font-weight:bold">【末次放疗日期】</font>${inquiry.last_radiotherapy_datetime eq null ?'无':inquiry.last_radiotherapy_datetime}	
						</div>
						<div class="history_content_div" style="padding-top:8px">
							<font style="font-weight:bold">【近期主要影像结果】</font>${inquiry.image_result eq null ?'无':inquiry.image_result}		
						</div>
						<div class="history_content_div" style="padding-top:8px">
							<font style="font-weight:bold">【肿瘤除去结果】</font>${inquiry.tumor_removal eq null ?'无':inquiry.tumor_removal}		
						</div>
					</div>
					<div class="easyui-panel" style="padding:3px 3px 3px 3px;margin-top:3px;width:100%;border-bottom:0px;border-right:0px;">
						<div  class="history_content_div" style="padding-left:3px;padding-top:8px;">
							<label><font color="blue">手术缺如部位：</font></label>
						</div>
						<div  class="history_content_div" style="padding-left:5px;padding-top:8px;">
							<font style="font-weight:bold">【胆囊.时间】</font>${inquiry.gallbladder_datetime eq null ?'无':inquiry.gallbladder_datetime}
						</div>
						<div  class="history_content_div" style="padding-left:5px;padding-top:8px;">
							<font style="font-weight:bold">【甲状腺.时间】</font>${inquiry.thyroid_datetime eq null ?'无':inquiry.thyroid_datetime}	
						</div>
						<div  class="history_content_div" style="padding-left:5px;padding-top:8px;">
							<font style="font-weight:bold">【脾.时间】</font>${inquiry.spleen_datetime eq null ?'无':inquiry.spleen_datetime}	
						</div>
						<div  class="history_content_div" style="padding-left:5px;padding-top:8px;">
							<font style="font-weight:bold">【左肾.时间】</font>${inquiry.left_kidney_datetime eq null ?'无':inquiry.left_kidney_datetime}
						</div>
						<div  class="history_content_div" style="padding-left:5px;padding-top:8px;">
							<font style="font-weight:bold">【右肾.时间】</font>${inquiry.right_kidney_datetime eq null ?'无':inquiry.right_kidney_datetime}	
						</div>
					</div>
						<div class="easyui-panel" id="female_info" data-options="closed:${inquiry.sex eq 'M' ? true:false}" style="padding:3px 3px 0px 3px;margin-top:3px;border:0px;">
							<div class="interrogation_content_div">
								<font style="font-weight:bold" color="blue">【月经情况】</font>${inquiry.menstruation_conditionsname eq null ?'无':inquiry.menstruation_conditionsname}	
							</div>
							<div class="interrogation_content_div">
								<font style="font-weight:bold">【初潮】</font>${inquiry.menarche_age eq null ?'无':inquiry.menarche_age}岁
							</div>
							<div class="interrogation_content_div">
								<font style="font-weight:bold">【经期】</font>${inquiry.menstruation_periods eq null ?'无':inquiry.menstruation_periods}天一次
							</div>
							<div class="interrogation_content_div">
								<font style="font-weight:bold">【每次】</font>${inquiry.menstruation_days eq null ?'无':inquiry.menstruation_days}天
							</div>
							<div class="interrogation_content_div">
								<font style="font-weight:bold">【近期月经】</font>${inquiry.menstruation_recent eq null ?'无':inquiry.menstruation_recent}天
							</div>
						</div>
						<div class="history_content_div">
							<font color="blue">【问诊其他信息】</font>${inquiry.other_information eq null ?'无':inquiry.other_information}
						</div>
				</div>
			</div>
		<div title="既往病史" data-options="border:false">
			<div class="easyui-panel" style="padding:3px;width:100%;border-top:0px;border-bottom:0px;border-right:0px;">
				<table style="border-collapse:separate; border-spacing:0px 5px;">
					<tr style="padding-top:5px;">
				    	<td><font style="font-weight:bold">【近期发热史】</font>${inquiry.recent_fever_history eq null ?'无':inquiry.recent_fever_history}天</td>
				   	</tr>
				   	<tr style="padding-top:3px">
				    	<td><font style="font-weight:bold">【持续时间（天）】</font>${inquiry.recent_fever_duration eq null ?'0':inquiry.recent_fever_duration}</td>
				   	</tr>
				   	<tr style="padding-top:3px">
				    	<td><font style="font-weight:bold">【脑梗病史】</font>${inquiry.cerebral_infarction_history eq null ?'无':inquiry.cerebral_infarction_history}</td>
				   	</tr>
				   	<tr style="padding-top:3px">
				    	<td><font style="font-weight:bold">【脑血管病史】</font>${inquiry.cerebral_vessels_history eq null ?'无':inquiry.cerebral_vessels_history}</td>
				   	</tr>
				   	<tr style="padding-top:3px">
				    	<td><font style="font-weight:bold">【部位】</font>${inquiry.trauma_place eq null ?'无':inquiry.trauma_place}</td>
				   	</tr>
				   	<tr style="padding-top:3px">
				    	<td><font style="font-weight:bold">【糖尿病】</font>${inquiry.diabetes eq null ?'无':inquiry.diabetes}</td>
				    </tr>
				    <tr style="padding-top:3px">
				    	<td><font style="font-weight:bold">【高血压病史】</font>${inquiry.hypertension eq null ?'无':inquiry.hypertension}</td>
				    </tr>
				    <tr style="padding-top:3px">
				    	<td><font style="font-weight:bold">【外伤史.时间】</font>${inquiry.trauma_datetime eq null ?'无':inquiry.trauma_datetime}</td>
				    </tr>
				    <tr style="padding-top:3px">
				    	<td><font style="font-weight:bold">【家族肿瘤史】</font>${inquiry.family_tumor eq null ?'无':inquiry.family_tumor}</td>
				    </tr>
				    <tr style="padding-top:3px">
				    	<td><font style="font-weight:bold">【近期钡餐等检查】</font>${inquiry.recent_examine eq null ?'无':inquiry.recent_examine}</td>
				    </tr>
				    <tr style="padding-top:3px">
				    	<td><font style="font-weight:bold">【近期升白细胞药使用.种类】</font>${inquiry.recent_leukocyte_medicine eq null ?'无':inquiry.recent_leukocyte_medicine}</td>
				    </tr>
				</table>
			</div>
			
			<div class="easyui-panel" style="padding:3px;width:100%;margin-top:3px;border-bottom:0px;border-right:0px;">
				<table style="border-collapse:separate; border-spacing:0px 5px;">
				   <tr style="padding-top:3px">
				    	<td><label><font color="blue">【其他病史】：</font></label></td>
				   </tr>
				   <tr style="padding-top:3px">
				    	<td><font style="font-weight:bold">【结核】</font>${inquiry.tuberculosis eq null ?'无':inquiry.tuberculosis}</td>
				   </tr>
				   <tr style="padding-top:3px">
				    	<td><font style="font-weight:bold">【血吸虫病史】</font>${inquiry.schistosomiasis_history eq null ?'无':inquiry.schistosomiasis_history}</td>
				   </tr>
				   <tr style="padding-top:3px">
				    	<td><font style="font-weight:bold">【肝炎、硬化病史】</font>${inquiry.hepatitis_and_sclerosis eq null ?'无':inquiry.hepatitis_and_sclerosis}</td>
				   </tr>
				   <tr style="padding-top:3px">
						<td><font style="font-weight:bold">【痔疮病史】</font>${inquiry.hemorrhoids_history eq null ?'无':inquiry.hemorrhoids_history}</td>
				   </tr>
				   <tr style="padding-top:3px">
				    	<td><font style="font-weight:bold">【吸烟史】</font>${inquiry.smoking_history eq null ?'无':inquiry.smoking_history}</td>
				   </tr>
				   <tr style="padding-top:3px">
				    	<td><font style="font-weight:bold">【其它】</font>${inquiry.other_medical_history eq null ?'无':inquiry.other_medical_history}</td>
				   </tr>
				</table>
			</div>
			
			<div class="easyui-panel" style="padding:3px;width:100%;margin-top:3px;border-bottom:0px;border-right:0px;">
			<table style="border-collapse:separate; border-spacing:0px 5px;">
				<tr><td><label><font color="blue">【留存资料】：</font></label></td></tr>
				<tr>
					<td align='left'><font style="font-weight:bold">【CT/MR 片】</font>${inquiry.ctAndMr eq null ?'0':inquiry.ctAndMr}张/份 </td>
				</tr>
				<tr>
					<td align='left'><font style="font-weight:bold">【报告】</font>${inquiry.presentation eq null ?'0':inquiry.presentation}张/份 </td>
				</tr>
				<tr>
					<td align='left'><font style="font-weight:bold">【超声报告】</font>${inquiry.ultrasonic_eport eq null ?'0':inquiry.ultrasonic_eport}张/份</td>
				</tr>
				<tr>
					<td align='left'><font style="font-weight:bold">【胃镜报告】</font>${inquiry.gastroscopy_report eq null ?'0':inquiry.gastroscopy_report}张/份</td>
				</tr>
				<tr>
					<td align='left'><font style="font-weight:bold">【肠镜报告】</font>${inquiry.enteroscopy_report eq null ?'0':inquiry.enteroscopy_report}张/份</td>
				</tr>
				<tr>
					<td align='left'><font style="font-weight:bold">【鼻咽镜报告】</font>${inquiry.npg_report eq null ?'0':inquiry.npg_report}张/份</td>
				</tr>
				<tr>
					<td  align='left'><font style="font-weight:bold">【喉镜报告】</font>${inquiry.laryngoscope_report eq null ?'0':inquiry.laryngoscope_report}张/份 </td>
				</tr>
				<tr>
					<td  align='left'><font style="font-weight:bold">【气管镜报告】</font>${inquiry.tracheoscope_report eq null ?'0':inquiry.tracheoscope_report}张/份</td>
				</tr>
				<tr>
					<td  align='left'><font style="font-weight:bold">【外院PET报告】</font>${inquiry.out_PET_report eq null ?'0':inquiry.out_PET_report}张/份</td>
				</tr>
				<tr>
					<td  align='left'><font style="font-weight:bold">【本院PET报告】</font>${inquiry.in_PET_report eq null ?'0':inquiry.in_PET_report}张/份 </td>
				</tr>
				<tr>
					<td align='left'><font style="font-weight:bold">【出院小结】</font>${inquiry.dischargeSummary eq null ?'0':inquiry.dischargeSummary}张/份 </td>
				</tr>
				<tr>
					<td align='left'><font style="font-weight:bold">【其它】</font>${inquiry.other eq null ?'0':inquiry.other}张/份 </td>
				</tr>
			  </table>
			</div>
		</div>
	</div>
</div>
<div title="其它" style="border:0;">	
	<div class="easyui-layout" data-options="fit:true,border:false" >
		<div data-options="region:'center',border:false,tools:'#rejectOpinion_tools_${reportid}'" title="驳回意见" style="height:50%">
        <table class="easyui-datagrid" id="rejectOpinion_dg_${reportid}" data-options="url:'${ctx}/report/getRejectOpinionlist?reportid=${reportid}',fit:true,fitColumns:true,
                  scrollbarSize:0,singleSelect:true,border:false,onDblClickRow:function(index,row){openRejectOpinionDialog(${reportid});},
                  rowStyler:function(index,row){return 'color:#FF6A00;';}">
                  <thead>
                      <tr>
                          <th data-options="field:'creator_name',width:100">创建人</th>
                          <th data-options="field:'createtime',width:208">创建时间</th>
                      </tr>
                  </thead>
              </table>
              <div id="rejectOpinion_tools_${reportid}" style="padding:2px 5px;">
                  <a class="easyui-tooltip" title="查看" onclick="openRejectOpinionDialog(${reportid});">
                      <i class="icon iconfont icon-info" style="text-align: center;vertical-align:2px;"></i></a>
                  <shiro:hasPermission name="reject_report">
                  <a class="easyui-tooltip" title="${sessionScope.locale.get("delete")}" onclick="delRejectOpinion(${reportid});">
                      <i class="icon iconfont icon-delete" style="font-size:15px;vertical-align:2px;text-align: center;"></i></a>
                  </shiro:hasPermission>
              </div>
		</div>
		<div data-options="region:'south',border:false,tools:'#retrialview_tools_${reportid}'" title="复审意见" style="height:50%">
	        <table class="easyui-datagrid" id="retrialview_dg_${reportid}" data-options="url:'${ctx}/report/getRetrialviewlist?reportid=${reportid}',fit:true,fitColumns:true,
                   scrollbarSize:0,singleSelect:true,border:false,onDblClickRow:function(index,row){openRetrialviewDialog(${reportid});},
                   rowStyler:function(index,row){return 'color:#FF6A00;';}">
                   <thead>
                       <tr>
                           <th data-options="field:'creator_name',width:100">创建人</th>
                           <th data-options="field:'createtime',width:208">创建时间</th>
                       </tr>
                   </thead>
               </table>
               <div id="retrialview_tools_${reportid}" style="padding:2px 5px;">
                   <a class="easyui-tooltip" title="查看" onclick="openRetrialviewDialog(${reportid});">
                       <i class="icon iconfont icon-info" style="text-align: center;vertical-align:2px;"></i></a>
                   <a class="easyui-tooltip" title="${sessionScope.locale.get("delete")}" onclick="delRetrialview(${reportid});">
                       <i class="icon iconfont icon-delete" style="font-size:15px;vertical-align:2px;text-align: center;"></i></a>
               </div>
		</div>
	</div>
</div>	
</div>
</body>
</html>