<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div style="padding:5px;margin-left:auto;margin-right:auto;width:420px;">
		<form name="statisticsform" id="statisticsform" method="POST" enctype="multipart/form-data">
		<div class="easyui-panel" data-options="border:false" style="padding:10px;margin-bottom:5px;background-color:#ffffff;">
			<div style="margin-bottom:10px">
		        <input class="easyui-textbox" label="名称：" id="statisticsname" labelWidth="110"
		        	data-options="prompt:'请输入报表名称...',required:true,missingMessage:'必填'" name="name" style="width:400px;" value="${sr.name}">
		    </div>
		    <div style="margin-bottom:10px">
		        <input class="easyui-combobox" id="statisticstype" label="类别：" labelWidth="110"
		        	data-options="valueField:'id',textField:'name',url:'${ctx}/statistics/getStatisticalClassify',editable:false,prompt:'请输入类别...',required:true,missingMessage:'必填',panelHeight:'auto',
		        	onLoadSuccess:function(){$(this).combobox('select', '${sr.classifyid}')}" 
		        	name="classifyid" style="width:355px;"><a title="添加" class="easyui-linkbutton" data-options="" style="width:44px;" onClick="addStatisticalClassify(1);">添加</a>
		    </div>
		    <div style="margin-bottom:10px">
	        	<input class="easyui-radiobutton" id="generation_type_template" name="generation_type" value="template" label="统计模板：" labelWidth="110"
	        		data-options="checked:${empty sr.sql?true:false},onChange:function(checked){switchGenerationType(checked,'template')}"/>&nbsp;&nbsp;&nbsp;&nbsp;
	        	<input class="easyui-radiobutton" id="generation_type_sql" name="generation_type" value="sql" label="SQL语句：" labelWidth="110"
	        		data-options="checked:${not empty sr.sql?true:false},onChange:function(checked){switchGenerationType(checked,'sql')}"/>
			</div>
		    <div style="margin-bottom:10px;${not empty sr.sql?'display: none;':''}" id="jrxml_div">
	        	<input class="easyui-filebox" id="jrxml_id" name="jrxml" label="模板设计文件：" labelWidth="110"
					data-options="prompt:'选择模板设计文件...',buttonText: '选择文件...',accept:'.jrxml'" style="width:400px;" />
			</div>
		    <div style="margin-bottom:10px;${not empty sr.sql?'display: none;':''}" id="jasper_div">				
				<input class="easyui-filebox" id="jasper_id" name="jasper" label="模板运行文件：" labelWidth="110"
					data-options="prompt:'选择模板运行文件...',buttonText: '选择文件...',accept:'.jasper'" style="width:400px;" />
			</div>
			<div style="margin-bottom:0px;${empty sr.sql?'display: none;':''}" id="sql_div">				
				<input class="easyui-textbox" label="SQL语句：" id="sql" labelWidth="110"
		        	data-options="prompt:'请输入sql语句...',multiline:true" name="sql" style="width:400px;height:150px;" value="${sr.sql}">
		        <span>提示：$P!{where}表示报表查询时的条件。</span>
			</div>
		    <input id="statisticsid" name="id" type="hidden" value="${sr.id}">
		    <input id="statisticsconditions" name="conditions" type="hidden" value="${sr.conditions}">
		    <input id="statisticsclassifyname" name="classifyname" type="hidden" value="">
	    </div>
	    </form>
	    <div class="easyui-panel" data-options="border:false" style="padding:10px;margin-bottom:5px;background-color:#ffffff;">
	    	<div style="margin-bottom:10px">
		    	<input class="easyui-checkbox" name="enable_datetime" data-options="checked:${enable_datetime=='true'?true:false},onChange:function(checked){
		    			if(checked){
		    				$('#date_type').combobox('enable');
		    			}
		    			else{
		    				$('#date_type').combobox('disable');
		    			}
		    	
		    		}" id="enable_datetime" label="启用时间条件:" labelWidth="110">
		    	<select class="easyui-combobox" name="datetype" label="   " labelWidth="50" id="date_type" style="width:250px;" data-options="value:'${datetype}',editable:false,
		    		panelHeight:'auto',required:true,disabled:${enable_datetime=='true'?false:true}">
	               <option value="regdatetime">登记时间</option>
	               <!-- <option value="studytime">检查时间</option> -->
	               <option value="studydatetime">检查时间</option>
	               <option value="reporttime">报告时间</option>
	               <option value="audittime">审核时间</option>
	           </select>
           </div>
           <div style="margin-bottom:10px">
		    	<input class="easyui-checkbox" data-options="checked:${enable_regoperator=='true'?true:false},onChange:function(checked){
		    			if(checked){
		    				$('#regoperator').combobox('enable');
		    			}
		    			else{
		    				$('#regoperator').combobox('disable');
		    			}
		    	
		    		}" id="enable_regoperator" label="启用登记医生:" labelWidth="110">
		    	<select class="easyui-combobox" label="   " labelWidth="50" id="regoperator" style="width:250px;" data-options="url:'${ctx}/getAllRole',value:'${regoperator}',
		    		textField:'rolename',valueField:'id',multiple:true,editable:false,panelHeight:'auto',required:true,disabled:${enable_regoperator=='true'?false:true}">
	              
	           </select>
           </div>
           <div style="margin-bottom:10px">
		    	<input class="easyui-checkbox" data-options="checked:${enable_technologists=='true'?true:false},onChange:function(checked){
		    			if(checked){
		    				$('#technologists').combobox('enable');
		    			}
		    			else{
		    				$('#technologists').combobox('disable');
		    			}
		    	
		    		}" id="enable_technologists" label="启用技师条件:" labelWidth="110">
		    	<select class="easyui-combobox" label="   " labelWidth="50" id="technologists" style="width:250px;" data-options="url:'${ctx}/getAllRole',value:'${technologists}',
		    		textField:'rolename',valueField:'id',multiple:true,editable:false,panelHeight:'auto',required:true,disabled:${enable_technologists=='true'?false:true}">
	              
	           </select>
           </div>
           <div style="margin-bottom:10px">
		    	<input class="easyui-checkbox" data-options="checked:${enable_reportphysician=='true'?true:false},onChange:function(checked){
		    			if(checked){
		    				$('#reportphysician').combobox('enable');
		    			}
		    			else{
		    				$('#reportphysician').combobox('disable');
		    			}
		    	
		    		}" id="enable_reportphysician" label="启用报告医生:" labelWidth="110">
		    	<select class="easyui-combobox" label="   " labelWidth="50" id="reportphysician" style="width:250px;" data-options="url:'${ctx}/getAllRole',value:'${reportphysician}',
		    		textField:'rolename',valueField:'id',multiple:true,editable:false,panelHeight:'auto',required:true,disabled:${enable_reportphysician=='true'?false:true}">
	              
	           </select>
           </div>
           <div style="margin-bottom:10px">
		    	<input class="easyui-checkbox" data-options="checked:${enable_auditphysician=='true'?true:false},onChange:function(checked){
		    			if(checked){
		    				$('#auditphysician').combobox('enable');
		    			}
		    			else{
		    				$('#auditphysician').combobox('disable');
		    			}
		    	
		    		}" id="enable_auditphysician" label="启用审核医生:" labelWidth="110">
		    	<select class="easyui-combobox" label="   " labelWidth="50" id="auditphysician" style="width:250px;" data-options="url:'${ctx}/getAllRole',value:'${auditphysician}',
		    		textField:'rolename',valueField:'id',multiple:true,editable:false,panelHeight:'auto',required:true,disabled:${enable_auditphysician=='true'?false:true}">
	              
	           </select>
           </div>
           <span class="panel-title">请选择查询条件</span>
           <div class="easyui-panel" id="conditions_panel" data-options="border:false" style="width:400px;height:55px;padding:5px;">
		    	<input id="143" class="easyui-checkbox" name="conditions" data-options="checked:${sr.conditions.indexOf('date_time_group')>=0}" label="时间分组" labelWidth="100" labelAlign="right" value="date_time_group">
		    	<input class="easyui-checkbox" name="conditions" data-options="checked:${sr.conditions.indexOf('patientsource')>=0}" label="患者来源" labelWidth="100" labelAlign="right" value="patientsource">
		    	<input class="easyui-checkbox" name="conditions" data-options="checked:${sr.conditions.indexOf('mod_type')>=0}" label="设备类型" labelWidth="100" labelAlign="right" value="mod_type">
		    	<input class="easyui-checkbox" name="conditions" data-options="checked:${sr.conditions.indexOf('modality')>=0}" label="检查设备" labelWidth="100" labelAlign="right" value="modality">
		    	<input class="easyui-checkbox" name="conditions" data-options="checked:${sr.conditions.indexOf('institution')>=0}" label="送检医院" labelWidth="100" labelAlign="right" value="institution">
		    	<%-- <input class="easyui-checkbox" name="conditions" data-options="checked:${sr.conditions.indexOf('regoperator')>=0}" label="登记医生" labelWidth="100" labelAlign="right" value="regoperator">
		    	<input class="easyui-checkbox" name="conditions" data-options="checked:${sr.conditions.indexOf('technologists')>=0}" label="技师" labelWidth="100" labelAlign="right" value="technologists">
		    	<input class="easyui-checkbox" name="conditions" data-options="checked:${sr.conditions.indexOf('reportphysician')>=0}" label="报告医生" labelWidth="100" labelAlign="right" value="reportphysician">
		    	<input class="easyui-checkbox" name="conditions" data-options="checked:${sr.conditions.indexOf('auditphysician')>=0}" label="审核医生" labelWidth="100" labelAlign="right" value="auditphysician"> --%>
		   </div>
           
           
          <!--  <ul class="easyui-datalist" id="conditions_list" style="width:400px;height:200px" data-options="
	            checkbox: true,
	            selectOnCheck: true,
	            singleSelect: false
            ">
		        <li value="date_time_group">时间分组</li>
		        <li value="patientsource">患者来源</li>
		        <li value="modality_type">设备类型</li>
		        <li value="modality">检查设备</li>
		        <li value="regoperator">登记医生</li>
		        <li value="technologists">技师</li>
		        <li value="reportphysician">报告医生</li>
		        <li value="auditphysician">审核医生</li>
		    </ul> -->
	    
	    </div>
	    <div class="easyui-panel" data-options="border:false" style="padding:10px;background-color:#ffffff;">
	    	<span class="panel-title">自定义条件</span>
	    	<table class="easyui-datagrid" id="custom_conditions" style="width:400px;height:155px"
		            data-options="url:'${ctx}/statistics/findStatisticalcustomconditions?srfk=${sr.id}',singleSelect:true,fitColumns:true,toolbar:'#toolbar_custom_conditions', 
		            			onDblClickCell: onDblClickCell_handel,
                				onEndEdit: onEndEdit_statistics">
		        <thead>
		            <tr>
		                <th data-options="field:'label',width:100,editor:{
			                	type:'textbox',
			                	options:{
			                		required:true,
			                		validType:'filterInput_name'
			                	}
		                	}">名称</th>
		                <th data-options="field:'columnname',width:100,editor:{
			                	type:'textbox',
			                	options:{
			                		required:true,
			                		validType:'filterInput_column'
			                	}
		                	}">数据库字段</th>
		                <th data-options="field:'operator',width:100,editor:{
                            type:'combobox',
                            options:{
                                valueField:'value',
                                textField:'label',
                                panelHeight:'auto',
                                required:true,
                                editable:false,
                                data: [{
									label: '=',
									value: '='
								},{
									label: '!=',
									value: '!='
								},{
									label: '>',
									value: '>'
								},{
									label: '>=',
									value: '>='
								},{
									label: '<',
									value: '<'
								},{
									label: '<',
									value: '<'
								},{
									label: 'like',
									value: 'like'
								}
								,{
									label: 'in',
									value: 'in'
								}]
                            }
                        }">运算符</th>
		                <th data-options="field:'type',width:100,
			                formatter:function(value,row){
		                            return row.type=='str'?'字符串':'非字符串';
		                        },
			                editor:{
	                            type:'combobox',
	                            options:{
	                                valueField:'value',
	                                textField:'label',
	                                panelHeight:'auto',
	                                required:true,
	                                editable:false,
	                                data: [{
										label: '字符串',
										value: 'str'
									},{
										label: '非字符串',
										value: 'nonstr'
									}]
	                            }
	                        }">类型</th>
		            </tr>
		        </thead>
		    </table>
		    
		    <div id="toolbar_custom_conditions" style="height:auto">
		        <a href="javascript:void(0)" class="easyui-linkbutton" data-options="plain:true" onclick="append()">添加</a>
		        <a href="javascript:void(0)" class="easyui-linkbutton" data-options="plain:true" onclick="removeit()">删除</a>
		        <!-- <a href="javascript:void(0)" class="easyui-linkbutton" data-options="plain:true" onclick="acceptit()">接受</a>
		        <a href="javascript:void(0)" class="easyui-linkbutton" data-options="plain:true" onclick="reject()">拒绝</a>
		        <a href="javascript:void(0)" class="easyui-linkbutton" data-options="plain:true" onclick="getChanges()">获取更改</a> -->
		    </div>
	    </div>
	    
   	</div>
   	
   	<script type="text/javascript">
   	
	   	$.extend($.fn.validatebox.defaults.rules, {
	   		filterInput_name: {
	            validator: function(value, param){
	            	var re= /'|"/i;
	               console.log(value)
	                return !(re.test(value));
	            },
	            message: '非法字符'
	        }
	   		,filterInput_column: {
	   			validator: function(value, param){
	            	var re= /select|update|delete|exec|count|'|"|=|;|>|<|%/i;
	               console.log(value)
	                return !(re.test(value));
	            },
	            message: '非法字符'
	   		}
	    });
   	
        var editIndex_statistics = undefined;
        function endEditing_statistics(){
            if (editIndex_statistics == undefined){return true}
            if ($('#custom_conditions').datagrid('validateRow', editIndex_statistics)){
                $('#custom_conditions').datagrid('endEdit', editIndex_statistics);
                editIndex_statistics = undefined;
                return true;
            } else {
                return false;
            }
        }
        function onDblClickCell_handel(index, field){
            if (editIndex_statistics != index){
                if (endEditing_statistics()){
                    $('#custom_conditions').datagrid('selectRow', index)
                            .datagrid('beginEdit', index);
                    var ed = $('#custom_conditions').datagrid('getEditor', {index:index,field:field});
                    if (ed){
                        ($(ed.target).data('textbox') ? $(ed.target).textbox('textbox') : $(ed.target)).focus();
                    }
                    editIndex_statistics = index;
                } else {
                    setTimeout(function(){
                        $('#custom_conditions').datagrid('selectRow', editIndex_statistics);
                    },0);
                }
            }
        }
        function onEndEdit_statistics(index, row){
            var ed = $(this).datagrid('getEditor', {
                index: index,
                field: 'operator'
            });
            row.operator = $(ed.target).combobox('getValue');
            ed = $(this).datagrid('getEditor', {
                index: index,
                field: 'type'
            });
            row.type = $(ed.target).combobox('getValue');
        }
        function append(){
            if (endEditing_statistics()){
                $('#custom_conditions').datagrid('appendRow',{label:'',columnname:'',operator:'=',type:'str'});
                editIndex_statistics = $('#custom_conditions').datagrid('getRows').length-1;
                $('#custom_conditions').datagrid('selectRow', editIndex_statistics)
                        .datagrid('beginEdit', editIndex_statistics);
            }
        }
        function removeit(){
        	if (endEditing_statistics()){
	        	var row=$('#custom_conditions').datagrid("getSelected");
	        	var selectIndex=undefined;
	        	if(row){
	        		selectIndex= $('#custom_conditions').datagrid("getRowIndex",row);
	        		$('#custom_conditions').datagrid('deleteRow', selectIndex);
	        		
	        		row.type='deleted';
	        		/* if(editIndex_statistics&&editIndex_statistics==selectIndex){
	        			editIndex_statistics = undefined;
	        		} */
	        	}
        	
        	}
            /* if (editIndex_statistics == undefined){return}
            $('#custom_conditions').datagrid('cancelEdit', editIndex_statistics)
                    .datagrid('deleteRow', editIndex_statistics);
            editIndex_statistics = undefined; */
        }
        function acceptit(){
            if (endEditing_statistics()){
                $('#custom_conditions').datagrid('acceptChanges');
            }
        }
        function reject(){
            $('#custom_conditions').datagrid('rejectChanges');
            editIndex_statistics = undefined;
        }
        function getChanges(){
        	endEditing_statistics();
            var rows = $('#custom_conditions').datagrid('getChanges');
            console.log(rows); 

        }
    </script>
</body>
</html>