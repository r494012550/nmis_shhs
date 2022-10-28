<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div class="easyui-panel" data-options="fit:true" style="padding:10px;">
	<header style="color:#8aa4af;">您当前的位置：排班管理  > 报告任务管理</header>
		<div class="easyui-layout" data-options="fit:true">
	        <!-- <div data-options="region:'north'" style="height:45px;padding:5px;">
		    </div>
	        <div data-options="region:'west'" style="width:150px;"></div> -->
	        <div data-options="region:'center'" style="padding:0px">
	        	<div style="margin-left: 10px;margin-top: 5px;margin-bottom: 5px;">
		        	<input class="easyui-switchbutton" style="width:60px;" data-options="onText:'开',offText:'关',checked:${setting.enable_report_task eq '1'?true:false},onChange:function(checked){
		        		saveReportTaskSetting('enable_report_task',checked);
		        		if(checked){
		        			$('#panel_report_task').panel('open',true);
		        		} else{
		        			$('#panel_report_task').panel('close',true);
		        		}
		        	}"/>
		        	<span style="width:200px;">启用报告任务</span>
	        	</div>
	        	<div id="panel_report_task" class="easyui-panel" title="任务设置" data-options="fit:true,border:false,closed:${setting.enable_report_task eq '1'?false:true},tools:'#footer_report_task'" style="padding:5px;">
	        		<form id="ff">
	        		<div style="margin-top: 5px;">
	        			<input class="easyui-radiobutton" name="task_type" style="" data-options="onText:'开',offText:'关',checked:${setting.enable_ADARD eq '1'?true:false},onChange:function(checked){
			        		saveReportTaskSetting('enable_ADARD',checked);
			        		if(checked){
			        			$('#institutionid_rts').combobox('enable');
			        			$('#dept_rts').combobox('enable');
			        			$('#refresh_rts').linkbutton('enable');
			        		} else{
			        			$('#institutionid_rts').combobox('disable');
			        			$('#dept_rts').combobox('disable');
			        			$('#refresh_rts').linkbutton('disable');
			        		}
			        	}"/>
		        		<span style="width:200px;">按检查报告难度分值平均分配</span>
	        		</div>
	        		<div style="margin-top: 10px;margin-left: 5px;" id="scores_div">
	        			<div style="float:left;width:100%;height:30px;">
							<div style="float:left;padding:10px 0 0 0px;">
								<span><b>当天报告医生分值详情：</b></span>
							</div>
							<div style="float:right;height:100%;padding:0px 10px;">
								<input class="easyui-combobox" id="institutionid_rts" label="机构：" labelWidth="50px" labelAlign="right" style="width:200px;height:27px;"
					                data-options="valueField: 'id',textField: 'name',url:'${ctx}/dic/getInstitution?deleted=0',disabled:${setting.enable_ADARD eq '1'?false:true},
					                panelHeight:'300px',editable:false
					                ,onChange:function(newvalue,oldvalue){
					                	$('#dept_rts').combobox('setValue','');
					                	$('#dept_rts').combobox('reload','${ctx}/dic/findDepartment?institutionid='+newvalue+'&value=&deleted=0');
					                }
					                ,onLoadSuccess:function(){
					                	var data=$(this).combobox('getData');
					                	if(data&&data[0]){
					                		$(this).combobox('select',data[0].id);
					                	} else {
					                		//clearPanel_wf();
					                	}
					                	
					                }">
					        
					        	<input class="easyui-combobox" id="dept_rts" label="科室：" labelWidth="50px" labelAlign="right" style="width:200px;height:27px;"
					                data-options="valueField: 'id',textField: 'deptname',disabled:${setting.enable_ADARD eq '1'?false:true},
					                panelHeight:'300px',editable:false
					                ,onLoadSuccess:function(){
					                	var data=$(this).combobox('getData');
					                	if(data&&data[0]){
					                		$(this).combobox('select',data[0].id);
					                	} else {
					                		//clearPanel_wf();
					                	}
					                }
					                ,onChange:function(newvalue,oldvalue){
					                	refreshPhysicianScore();
					                }">
								<a id="refresh_rts" class="easyui-linkbutton" data-options="plain:true,disabled:${setting.enable_ADARD eq '1'?false:true}," onClick="refreshPhysicianScore();"><i class="icon iconfont icon-shuaxin2"></i></a>
							</div>
							
						</div>
	        			<div id="panel_physician_score" class="easyui-panel" data-options="border:false" style="padding:5px;width:99%;">
	        			</div>
	        		</div>
	        		
	        		<div style="margin-top: 10px;height:30px;">
		        		<div style="width:45%;float:left;">
		        			<input class="easyui-radiobutton" name="task_type" style="" data-options="onText:'开',offText:'关',checked:${setting.enable_ADQ eq '1'?true:false},onChange:function(checked){
				        		saveReportTaskSetting('enable_ADQ',checked);
				        		if(checked){
				        			$('#modality_adq').checkbox('enable');
				        			$('#institutionid_rts_count').combobox('enable');
				        			$('#dept_rts_count').combobox('enable');
				        			$('#refresh_rts_count').linkbutton('enable');
				        		} else{
				        			$('#modality_adq').checkbox('disable');
				        			$('#institutionid_rts_count').combobox('disable');
			        				$('#dept_rts_count').combobox('disable');
			        				$('#refresh_rts_count').linkbutton('disable');
				        		}
				        	}"/>
		        		<span style="width:200px;">按数量平均分发</span>
		        		</div>
	        			<div style="width:45%;float:right;">
			        		<input class="easyui-checkbox" id="modality_adq" data-options="onText:'开',offText:'关',checked:${setting.enable_ADMQ eq '1'?true:false},
			        			disabled:${setting.enable_ADQ eq '1'?false:true},
			        			onChange:function(checked){
			        				saveReportTaskSetting('enable_ADMQ',checked);
			        				refreshPhysicianCount();
				        	}"/>
			        		<span style="width:200px;">按设备类型平均分配</span>
		        		</div>
	        		</div>
	        		<div style="margin-top: 10px;margin-left: 5px;" id="scores_div_adq">
		        		<div style="float:left;width:100%;height:30px;">
							<div style="float:left;padding:10px 0 0 0px;">
								<span><b>当天报告医生报告数量详情：</b></span>
							</div>
							<div style="float:right;height:100%;padding:0px 10px;">
								<input class="easyui-combobox" id="institutionid_rts_count" label="机构：" labelWidth="50px" labelAlign="right" style="width:200px;height:27px;"
					                data-options="valueField: 'id',textField: 'name',url:'${ctx}/dic/getInstitution?deleted=0',disabled:${setting.enable_ADQ eq '1'?false:true},
					                panelHeight:'300px',editable:false
					                ,onChange:function(newvalue,oldvalue){
					                	$('#dept_rts_count').combobox('setValue','');
					                	$('#dept_rts_count').combobox('reload','${ctx}/dic/findDepartment?institutionid='+newvalue+'&value=&deleted=0');
					                }
					                ,onLoadSuccess:function(){
					                	var data=$(this).combobox('getData');
					                	if(data&&data[0]){
					                		$(this).combobox('select',data[0].id);
					                	} 
					                }">
					        
					        	<input class="easyui-combobox" id="dept_rts_count" label="科室：" labelWidth="50px" labelAlign="right" style="width:200px;height:27px;"
					                data-options="valueField: 'id',textField: 'deptname',disabled:${setting.enable_ADQ eq '1'?false:true},
					                panelHeight:'300px',editable:false
					                ,onLoadSuccess:function(){
					                	var data=$(this).combobox('getData');
					                	if(data&&data[0]){
					                		$(this).combobox('select',data[0].id);
					                	} 
					                }
					                ,onChange:function(newvalue,oldvalue){
					                	refreshPhysicianCount();
					                }">
								<a id="refresh_rts_count" class="easyui-linkbutton" data-options="plain:true,disabled:${setting.enable_ADQ eq '1'?false:true}," onClick="refreshPhysicianCount();"><i class="icon iconfont icon-shuaxin2"></i></a>
							</div>
							
						</div>
	        			<div id="panel_physician_count" class="easyui-panel" data-options="border:false" style="padding:5px;width:99%;">
	        			</div>
	        		</div>
	        		<div style="margin-top: 15px;height:35px;">
	        			<input class="easyui-switchbutton" style="width:60px;" data-options="onText:'开',offText:'关',checked:${setting.enable_SD eq '1'?true:false},onChange:function(checked){
			        		saveReportTaskSetting('enable_SD',checked);
			        		if(checked){
			        			$('#panel_subscription_rule').panel('open',true);
			        			//$('#institutionid_subscription_rule').combobox('enable');
				        		//$('#dept_subscription_rule').combobox('enable');
			        			//$('#name_searcher').searchbox('enable');
			        		} else{
			        			$('#panel_subscription_rule').panel('close',true);
			        			//$('#institutionid_subscription_rule').combobox('disable');
				        		//$('#dept_subscription_rule').combobox('disable');
			        			//$('#name_searcher').searchbox('disable');
			        		}
			        	}"/>
		        		<span style="width:200px;">打开订阅分发</span>
	        		</div>
	        		
	        		<div id="panel_subscription_rule" class="easyui-panel" title="订阅规则" data-options="closed:${setting.enable_SD eq '1'?false:true}" 
	        			style="padding:10px;width:100%;height:400px;">
	        			<div style="height:35px;">
	        				<div style="float:left;">
		        				<input class="easyui-searchbox" id="name_searcher" data-options="prompt:'请输入医生姓名...',searcher:doSearch_Physicians" style="width:200px;"/>
		        			</div>
		        			<div style="float:left;margin-left: 5px;">
						        <a class="easyui-linkbutton" style="width:60px;" onclick="saveSubscriptionRule()">保存</a>
						    </div>
		        			<div style="float:right;padding:0px 10px;">
								<input class="easyui-combobox" id="institutionid_subscription_rule" label="机构：" labelWidth="50px" labelAlign="right" style="width:200px;height:27px;"
					                data-options="valueField: 'id',textField: 'name',url:'${ctx}/dic/getInstitution?deleted=0',
					                panelHeight:'300px',editable:false
					                ,onChange:function(newvalue,oldvalue){
					                	$('#dept_subscription_rule').combobox('setValue','');
					                	$('#dept_subscription_rule').combobox('reload','${ctx}/dic/findDepartment?institutionid='+newvalue+'&value=&deleted=0');
					                }
					                ,onLoadSuccess:function(){
					                	var data=$(this).combobox('getData');
					                	if(data&&data[0]){
					                		$(this).combobox('select',data[0].id);
					                	} 
					                }">
					        
					        	<input class="easyui-combobox" id="dept_subscription_rule" label="科室：" labelWidth="50px" labelAlign="right" style="width:200px;height:27px;"
					                data-options="valueField: 'id',textField: 'deptname',
					                panelHeight:'300px',editable:false
					                ,onLoadSuccess:function(){
					                	var data=$(this).combobox('getData');
					                	if(data&&data[0]){
					                		$(this).combobox('select',data[0].id);
					                	} 
					                }
					                ,onChange:function(newvalue,oldvalue){
					                	doSearch_Physicians();
					                }">
								<%-- <a id="refresh_rts_count" class="easyui-linkbutton" data-options="plain:true,disabled:${setting.enable_ADQ eq '1'?false:true}," onClick="refreshPhysicianCount();"><i class="icon iconfont icon-shuaxin2"></i></a> --%>
							</div>
	        			</div>
	        			<div style="height:300px;">
	        				<div style="float:left;">
		        				<div class="easyui-datalist" id="physicians_subscription_rule" style="width:200px;height:310px;margin-top: 5px;" data-options="
							            url: '${ctx}/workforce/getPhysicians',checkbox: true,
							            textField: 'name',onSelect:showSubscriptionRule
							            ">
							    </div>
						    </div>
						    <div style="float:left;margin-left: 5px;">
						    	<div class="easyui-panel" style="width:700px;height:310px;padding:5px;" data-options="border:true">
						    		<div style="width:100%;height:100%;">
							    		<div style="margin-left: 5px;float:left;height:100%;">
							    			按设备类型订阅：
							    			<div class="easyui-datalist" id="subscription_rule_modalitytype" style="width:150px;height:280px;"
								                data-options="url:'${ctx}/syscode/getCode?type=0004',valueField: 'code',textField: '${sessionScope.syscode_lan}',disabled:${setting.enable_SD eq '1'?false:true},
								                checkbox: true,singleSelect: false,
								                onClickRow:function(index,row){
								                	//saveSubscriptionRule(row,true,row.code);
								                },
								                onCheck:function(index,row){
								                	//saveSubscriptionRule(row,true,row.code);
								                },
								                onUncheck:function(index,row){
								                	//saveSubscriptionRule(row,false,row.code);
								                }">
								            </div>
							    		</div>
							    		<div style="margin-left: 15px;float:left;height:100%;">
							    			按检查设备订阅：
							    			<div class="easyui-datalist" id="subscription_rule_modality" label="按检查设备订阅：" style="width:190px;height:280px;"
								                data-options="url:'${ctx}/workforce/getModalitys',valueField: 'id',textField: 'modality_name',disabled:${setting.enable_SD eq '1'?false:true},
								                checkbox: true,groupField: 'type',singleSelect: false">
								            </div>
							    		</div>
							    		<div style="margin-left: 15px;float:left;height:100%;">
							    			按检查项目订阅：
							    			<div class="easyui-panel" style="width:300px;height:280px;">
								    			<ul class="easyui-tree" id="subscription_rule_examitem" 
									                data-options="url:'${ctx}/workforce/getExamItems',dnd:'true',
									                checkbox: true">
									            </ul>
								            </div>
							    		</div>
						    		</div>
							    </div>
						    </div>
	        			</div>
	        		</div>
	        		
	        		<div style="margin-top: 10px;height:30px;">
	        			<div style="width:45%;float:left;">
		        			<input class="easyui-switchbutton" style="width:60px;" data-options="onText:'开',offText:'关',checked:${setting.enable_AMEFSPTSP eq '1'?true:false},onChange:function(checked){
				        		saveReportTaskSetting('enable_AMEFSPTSP',checked);
				        	}"/>
			        		<span style="width:200px;">将同一患者的多个检查分配给同一诊断人员</span>
		        		</div>
	        			<div style="width:45%;float:right;">
			        		<input class="easyui-switchbutton" style="width:60px;" data-options="onText:'开',offText:'关',checked:${setting.enable_TORED eq '1'?true:false},onChange:function(checked){
				        		saveReportTaskSetting('enable_TORED',checked);
				        	}"/>
			        		<span style="width:200px;">超时重新分发</span>
		        		</div>
	        		</div>
	        		<div style="margin-top: 10px;height:30px;">
	        			<div style="width:45%;float:left;">
		        			<input class="easyui-switchbutton" style="width:60px;" data-options="onText:'开',offText:'关',checked:${setting.enable_EED eq '1'?true:false},onChange:function(checked){
				        		saveReportTaskSetting('enable_EED',checked);
				        	}"/>
			        		<span style="width:200px;">排除急诊进行分配</span>
	        			</div>
	        			<div style="width:45%;float:right;">
		        			<input class="easyui-switchbutton" style="width:60px;" data-options="onText:'开',offText:'关',checked:${setting.enable_EPED eq '1'?true:false},onChange:function(checked){
				        		saveReportTaskSetting('enable_EPED',checked);
				        	}"/>
			        		<span style="width:200px;">排除体检进行分配</span>
	        			</div>
	        		</div>
	        		<div style="margin-top: 10px;">
	        			<div style="width:90%;float:left;">
	        			<input class="easyui-switchbutton" style="width:60px;" data-options="onText:'开',offText:'关',checked:${setting.enable_RRRED eq '1'?true:false},onChange:function(checked){
			        		saveReportTaskSetting('enable_RRRED',checked);
			        	}"/>
		        		<span style="width:200px;">退还报告重新分发</span>
		        		</div>
	        		</div>
	        	</form>
	        	</div>
	        	
	        	<div id="footer_report_task" style="padding:5px;">
			        <a id="report_task_submit" class="icon-ok" title="立即生效" onclick=""></a>
			    </div>
	        </div>
	        <!-- <div data-options="region:'south'" style="height:100px;padding:20px;">
	        	
	        </div> -->
	    </div>
	    
	    <input id="reporttasksettingid" name="id" type="hidden" value="${setting.id}"/>
	</div>
</body>
</html>