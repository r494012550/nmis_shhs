<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
<style type="text/css">
	.rule-condition{
		background-color:#FFFFFF;
		padding:5px 0px;
	}
</style>
	<div class="easyui-panel" data-options="fit:true" style="padding:10px;">
		<header style="color:#8aa4af;">您当前的位置：项目管理  > 入组规则</header>
			<table id="ruledg" class="easyui-datagrid" style="width:100%;"
				data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#rule_btn',autoRowHeight:true,fitColumns:true,url:'${ctx}/research/searchRuleList',scrollbarSize:0,
					loadMsg:'加载中...',emptyMsg:'没有查找到实验组规则...'">
				<thead>
					<tr>
						<th data-options="field:'rule_name',width:100">规则名称</th>
						<th data-options="field:'project_name',width:100">项目名称</th>
						<th data-options="field:'group_name',width:100">实验组名称</th>
						<th data-options="field:'createtime',width:100">创建时间</th>
						<th data-options="field:'creator_name',width:60">创建人</th>
						<c:if test="${enable_sr_joingroup_rules}" >
						<th data-options="field:'op',width:'60',align:'center',formatter:rulesdg_formatter">操作</th>
						</c:if>
					</tr>
				</thead>
			</table>
			<div id="rule_btn" style="padding:2px 10px;text-align:right;">
				<c:if test="${!enable_sr_joingroup_rules}" >
					开启状态：<input class="easyui-switchbutton" data-options="offText:'关闭',disabled:true,width:100"/>
				</c:if>
				<a class="easyui-linkbutton easyui-tooltip" title="新建规则" data-options="disabled:${!enable_sr_joingroup_rules}" onClick="addRule();">新建</a>
				<!-- <a class="easyui-linkbutton easyui-tooltip" title="编辑规则"  onClick="editRule();">编辑</a>
				<a class="easyui-linkbutton easyui-tooltip" title="删除规则"  onClick="deleteRule();">删除</a> -->
			</div>
	 
			<div  id="ruleDlg" class="easyui-dialog" style="width:520px;height:500px;" data-options="modal:true,closed:true,title:'维护入库规则',border:'thin',buttons:'#rule_btn1'" >
				<form name="ruleform" id="ruleform" method="POST">
					 <div class="easyui-panel" data-options="border:false" style="padding:10px 20px;">
					 	<div style="margin-bottom:10px">
					        <input class="easyui-textbox" label="规则名称：" id="rule_name" labelWidth="110" labelAlign="right"
					        	data-options="prompt:'请输入规则名称...',required:true,missingMessage:'必填'" name="rule_name" style="width:420px;">
					    </div>
					    <div style="margin-bottom:10px">
					        <input class="easyui-combobox" label="项目：" id="task_id" labelWidth="110" labelAlign="right"
					        	data-options="prompt:'请选择项目...',required:true,missingMessage:'必填',url:'${ctx}/research/searchProject',valueField:'id',textField:'name',editable:false"
					        	name="project_id" style="width:420px;">
					    </div>
					    <div style="margin-bottom:10px">
					        <input class="easyui-combobox" label="实验组：" id="test_group_id" labelWidth="110" labelAlign="right"
					        	data-options="prompt:'请选择实验组...',required:true,missingMessage:'必填',valueField:'id',textField:'name',editable:false" name="group_id" style="width:420px;">
					    </div>
					    <div style="margin-bottom:10px">		
					        <input class="easyui-combobox" label="匹配字段：" id="matching_field" labelWidth="110" labelAlign="right"
					        	data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0027',editable:false"   name="matching_field" style="width:420px;">
					        
					    </div>
					    <div id="text_condition" class="rule-condition" style="margin-bottom:10px;display:none;">
					        <input class="easyui-textbox" label="匹配值：" id="matching_value" labelWidth="110" labelAlign="right" data-options="prompt:'支持正则表达式匹配，具体可看帮助'"
					        	   name="value" style="width:420px;">
					        <a title="举例：^CT表示匹配以CT开头的数据    CT$ 表示匹配以CT结尾的数据    .*CT.* 表示数据中只要包涵CT就匹配" class="easyui-tooltip">帮助</a>	   
					    </div>
					    <div id="date_range_condition" class="rule-condition" style="margin-bottom:10px;display:none;">
						    <div style="margin-bottom:10px;">
						        <input class="easyui-datebox" label="开始时间：" id="sTime" labelWidth="110" labelAlign="right" editable="false"
						        	  name="strTime" style="width:420px;">
						    </div>
						    <div style="margin-bottom:0px;">
						        <input class="easyui-datebox" label="结束时间：" id="eTime" labelWidth="110" labelAlign="right" editable="false"
						        	  name="endTime" style="width:420px;">
						    </div>
					    </div>
					    <div id="age_range_condition" class="rule-condition" style="margin-bottom:10px;display:none;">
					        <div style="margin-bottom:10px;">
						        <input class="easyui-numberbox" data-options="min:0" label="开始年龄：" id="beginAge" labelWidth="110" labelAlign="right"
						        	  name="beginAge" style="width:420px;">
						    </div>
						    <div style="margin-bottom:0px;">
						        <input class="easyui-numberbox" data-options="min:0" label="结束年龄：" id="endAge" labelWidth="110" labelAlign="right"
						        	  name="endAge" style="width:420px;">
						    </div>
					    </div>
					    <div id="template_range_condition" class="rule-condition" style="margin-bottom:10px;display:none;">
								<input id="templateName"  class="easyui-combobox" editable="false" data-options="valueField:'id',textField:'name',url:'${ctx}/research/searchSrtemplate'" style="width:420px;"label="模板名称:" labelWidth="110" labelAlign="right">
					    </div>
					    
					    <div id="sex_range_condition" class="rule-condition" style="margin-bottom:10px;display:none;">
								<input id="sex"  class="easyui-combobox" editable="false" data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0001'" style="width:420px;"label="性别:" labelWidth="110" labelAlign="right">
					    </div>
					    
					    <div id="add_btn_div" style="margin-bottom:10px;text-align:center;">
					    	<a class="easyui-linkbutton easyui-tooltip" style="width:200px" title="添加条件"  onClick="addCondition();"><i class="icon iconfont icon-arrow-o-d"></i></a>
					    </div>
					    <div style="margin-bottom:0px;height:240px">
					        <table id="rulelinesdg" class="easyui-datagrid" style="width:100%;height:100%"
								data-options="showFooter: true,singleSelect:true,fit:true,scrollbarSize:0,autoRowHeight:true,fitColumns:true">
								<thead>
									<tr>
										<th data-options="field:'matching_field',width:'15%'">字段编码</th>
										<th data-options="field:'matching_name',width:'25%'">匹配字段</th>
										<th data-options="field:'matching_value',width:'45%'">匹配值</th>
										<th data-options="field:'caozuo',width:'15%',align:'center',formatter:rulelinesdg_formatter">操作</th>
									</tr>
								</thead>
							</table>
					    </div>
					     <input id="id" name="id"  type="hidden"/>
					 </div>
				 </form>
			</div>
			<div id="rule_btn1">
				 <a class="easyui-linkbutton" style="width:80px;" onClick="saveRule();">保存</a>
				 <a class="easyui-linkbutton" style="width:80px;" onClick="closedRule();">关闭</a>
			</div>
	<script type="text/javascript">
		$(document).ready(function () {
			$("#task_id").combobox({
				onChange: function (newValue,oldValue) {
					$('#test_group_id').combobox('setValue','');
					$("#test_group_id").combobox('reload',window.localStorage.ctx+"/research/searchTestGroups?projectId="+newValue);
				}
			});
			$("#matching_field").combobox({
				onSelect: function (value) {
					$("#matching_value").textbox("setValue","");
					$("#sTime").datebox("setValue","");
					$("#eTime").datebox("setValue","");
					$("#templateName").combobox("setValue","");
					$('#beginAge').numberbox("setValue","");
					$('#endAge').numberbox("setValue","");
					$("#sex").combobox("setValue","");
					$('#add_btn_div').show();
					$('.rule-condition').hide();
					if(value.code=="003"){
						$('#date_range_condition').show();
					}else if (value.code=="004") {
						$('#age_range_condition').show();
					}else if(value.code=="005"){
						$('#template_range_condition').show();
					}else if(value.code=="006"){
						$('#sex_range_condition').show();
					}else{
						$('#text_condition').show();
					}
				}
			});
			$('#add_btn_div').hide();
		});
		
		function rulesdg_formatter(value, row, index){
	 		return '<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="editRule('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-edit"></i></span></span></a>&nbsp;&nbsp;'+
	 				'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteRule('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-delete"></i></span></span></a>';
	 	}
	 	function addRule(){
	 		$("#ruleDlg").dialog("open");
	 		$("#ruleform").form("clear");
	 		$('.rule-condition,#add_btn_div').hide();
	 		$('#rulelinesdg').datagrid('loadData', { total: 0, rows: [] });
	 	}
	 	function closedRule(){
	 		$('#ruleDlg').dialog('close');
	 	}
	 	function deleteRule(index){
	 		$('#ruledg').datagrid('selectRow',index);
	 		var row =$("#ruledg").datagrid("getSelected");
	 		if(row==null){
	 			_message("请先选择要删除的数据!");
				return;
	 		}
	 		getJSON(window.localStorage.ctx+"/research/deleteRule",{id:row.id},function(data){
	 			var json=validationDataAll(data);
				console.log(json)
				if(json.code==0){
					$.messager.show({
			            title:'提示',
			            msg:'删除成功！',
			            timeout:3000,
			            border:'thin',
			            showType:'slide'
			        });
					$('#ruledg').datagrid('reload');
				}
 		    });
	 		
	 	}
	 	/* function findTestGroup(newValue,oldValue){
	 		 getJSON(window.localStorage.ctx+"/research/searchTestGroups",{projectId:newValue},function(data){
	 		 	$("#test_group_id").combobox('loadData',data);
	 	     });
	 	} */
	 	function saveRule(){
	 		$('#ruleform').form('submit', {
	 			url: window.localStorage.ctx+"/research/saveRule",
	 			onSubmit: function(param){
	 				if($(this).form("validate")){
	 					var rows=$('#rulelinesdg').datagrid('getRows');
		 				param.datas=JSON.stringify(rows);
	 				} else{
	 					_message("请输入完整信息！！");
	 					return false;
	 				}
	 			},
	 			success: function(data){
	 				var json=validationDataAll(data);
	 				if(json.code==0){
	 					$('#ruleDlg').dialog('close');
	 					$.messager.show({
	 			            title:'提示',
	 			            msg:'保存成功！',
	 			            timeout:3000,
	 			            border:'thin',
	 			            showType:'slide'
	 			        });
	 					$('#ruledg').datagrid('reload');
	 				}
	 				else{
	 					$.messager.show({
	 			            title:'错误提醒',
	 			            msg:json.message,
	 			            timeout:3000,
	 			            border:'thin',
	 			            showType:'slide'
	 			        });
	 					
	 				}
	 			}
	 		});
	 	}
	 	function editRule(index){
	 		$('#ruledg').datagrid('selectRow',index)
	 		var row =$("#ruledg").datagrid("getSelected");
	 		if(row==null){
	 			$.messager.alert("提醒","请先选择要编辑的数据!");
				return;
	 		}
	 		$("#ruleDlg").dialog("open");
	 		$('.rule-condition,#add_btn_div').hide();
	 		getJSON(window.localStorage.ctx+"/research/findJoinGroupRuleById",{id:row.id},function(data){
	 				$('#ruleform').form('load',data);
	 				getJSON(window.localStorage.ctx+"/research/searchRuleItems",{task_rule_id:row.id},function(data){
		 				$("#rulelinesdg").datagrid('loadData',data);
		 		});
	 		});
			$("#matching_field").combobox("setValue","");
	 	}
	 	function addCondition(){
	 		var matching_field=$("#matching_field").combobox("getValue");
	 		var matching_name=$("#matching_field").combobox("getText");
	 		var value;
	 		if(!matching_field){
	 			_message("请选择匹配字段！！");
				return;
	 		}
	 		if(matching_field=="003"){
	 			var sTime=$("#sTime").datebox("getValue");
				var eTime=$("#eTime").datebox("getValue");
				if(!sTime){
					_message("请选择开始时间！！");
					return;
				}
				if(!eTime){
					_message("请选择结束时间！！");
					return;
				}
				value=sTime+","+eTime;
	 		}else if(matching_field=="004"){
	 			var beginAge=$("#beginAge").numberbox("getValue");
	 			var endAge=$("#endAge").numberbox("getValue");
	 			if(!beginAge){
	 				_message("请输入开始年龄！！");
					return;
	 			}
	 			if(!endAge){
	 				_message("请输入结束年龄！！");
					return;
	 			}
	 			if(beginAge>endAge){
	 				_message("开始年龄不能大于结束年龄！！");
					return;
	 			}
	 			value=beginAge+","+endAge;
	 		}else if(matching_field=="005"){
	 			var templateName=$("#templateName").combobox("getText");
	 			if(!templateName){
	 				_message("请选择模板！！");
					return;
	 			}
	 			value=templateName;
	 		}else if(matching_field=="006"){
	 			var sex=$("#sex").combobox("getText");
	 			if(!sex){
	 				_message("请选择性别！！");
					return;
	 			}
	 			value=sex;
	 		}else{
	 			var matching_value=$("#matching_value").textbox("getValue");
	 			if(!matching_value){
	 				_message("请输入匹配值！！");
					return;
	 			}
	 			value=matching_value;
	 		}
	 		var rows=$('#rulelinesdg').datagrid('getRows');
	 		var flag=true;
	 		var num;
	 		if(rows){
	 			for (var i = 0; i < rows.length; i++) {
					 var row=rows[i];
					 if(matching_field==row.matching_field){
						 flag=false;
						 num=i;
					 }
				}
	 		}
	 		
	 		if(flag){
	 			$('#rulelinesdg').datagrid('appendRow',{
		 			matching_field:matching_field,
		 			matching_name: matching_name,
		 			matching_value: value
		 		});
	 		}else{
	 		 $('#rulelinesdg').datagrid('updateRow',{
				    index: num,
				    row: {
				    	matching_field:matching_field,
			 			matching_name: matching_name,
			 			matching_value: value
				    }
				});
	 		}
	 	}
	 	function rulelinesdg_formatter(value, row, index){
	 		return '<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="editRulelines('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-edit"></i></span></span></a>&nbsp;&nbsp;'+
	 				'<a class="easyui-linkbutton c5 easyui-tooltip tooltip-f l-btn l-btn-small" onclick="deleteRulelines('+index+');"><span class="l-btn-left"><span class="l-btn-text"><i class="icon iconfont icon-delete"></i></span></span></a>';
	 	}
	 	function editRulelines(index){
	 		$('#rulelinesdg').datagrid('selectRow',index)
	 		var row=$('#rulelinesdg').datagrid('getSelected');
	 		$("#matching_field").combobox("setValue",row.matching_field);
	 		
	 		$('.rule-condition').hide();
	 		$('#add_btn_div').show();
	 		if(row.matching_field=="003"){
				$('#date_range_condition').show();
				var str = row.matching_value.split(',');
				$("#sTime").datebox("setValue",str[0]);
				$("#eTime").datebox("setValue",str[1]);
			}else if (row.matching_field=="004") {
				$('#age_range_condition').show();
				$("#age").combobox("setValue",row.matching_value);
			}else{
				$('#text_condition').show();
				$("#matching_value").textbox("setValue",row.matching_value);
			}
	 		
	 		/* if(row.matching_field=="003"){
				div1.style.display='none'; 
				div2.style.display='block'; 
				//div3.style.display='block';
				var str = row.matching_value.split(',');
				$("#sTime").datebox("setValue",str[0]);
				$("#eTime").datebox("setValue",str[1]);
			}else{
				div1.style.display='block'; 
				div2.style.display='none'; 
				//div3.style.display='none'; 
				$("#matching_value").textbox("setValue",row.matching_value);
			} */
	 	}
	 	function deleteRulelines(index){
	 		$('#rulelinesdg').datagrid('selectRow',index)
	 		var row=$('#rulelinesdg').datagrid('getSelected');
	 		if(row.id){
	 			getJSON(window.localStorage.ctx+"/research/deleteRuleItem",{id:row.id},function(data){
	 				var json=validationDataAll(data);
					if(json.code==0){
						$.messager.show({
				            title:'提示',
				            msg:'删除成功！',
				            timeout:3000,
				            border:'thin',
				            showType:'slide'
				        });
						$('#rulelinesdg').datagrid('deleteRow',index);
					}
	 			});
	 		}else{
	 			$('#rulelinesdg').datagrid('deleteRow',index);
	 		}
	 	}
	</script>
</body>
</html>