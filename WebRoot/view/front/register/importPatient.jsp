<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
</head>
<body style="background:#fafafa;" class="easyui-layout">
<div class="easyui-layout" data-options="fit:true">
<div data-options="region:'west',collapsible:false,title:'查询条件',border:false" style="width:280px;">
<div>
	<form name="searchForm_im" id="searchForm_im" method="POST">

		<div style="width: 270px;margin-top: 3px;">
			<div style="width:130px;float:left;">
			<input id="patientid_im" name="patientid" class="easyui-textbox" style="width:129px;height:56px;"
			label="病人编号：" labelPosition="top">
			</div>
				
			<div style="width:130px;float:right;">
			<input id="patientname_im" name="patientname" class="easyui-textbox" style="width:129px;height:56px;"
			label="姓名：" labelPosition="top">
			</div>
		</div>
		
		<div style="width: 270px;margin-top: 3px;">
			<div style="width:130px;float:left;">
			<input id="inno_im" name="inno" class="easyui-textbox" style="width:129px;height:56px;"
			label="住院号：" labelPosition="top">
			</div>
				
			<div style="width:130px;float:right;">
			<input id="outno_im" name="outno" class="easyui-textbox" style="width:129px;height:56px;"
			label="门诊号：" labelPosition="top">
			</div>
		</div>
					
		<div style="width: 270px;margin-top: 3px;">
			<div style="width:130px;float:left;">
			<input id="studyid_im" name="studyid" class="easyui-textbox" style="width:129px;height:56px;"
			label="检查号：" labelPosition="top">
			</div>
				
			<div style="width:130px;float:right;">
			<input id="modality_im" name="modality" class="easyui-combobox" style="width:129px;height:56px;"
			label="检查类型：" labelPosition="top"
			 data-options="valueField:'code',textField:'name_zh',url:'syscode/getCode?type=0004',editable:false,
			 icons:[{iconCls:'icon-clear'}],onClickIcon:function(){$('#appmodality_im').combobox('clear');}">
			</div>
		</div>
		
		<div style="width: 270px;margin-top: 3px;">
			<div style="width:130px;float:left;">
			<input id="dept_im" name="appdept" class="easyui-combobox" style="width:129px;height:56px;"
			label="申请部门：" labelPosition="top"
			 data-options="icons:[{iconCls:'icon-clear'}],onClickIcon:function(){$('#dept_im').combobox('clear');},
                  valueField: 'id',
                  textField: 'text'">
			</div>
				
			<div style="width:130px;float:right;">
			<input id="doctor_im" name="appdoctor" class="easyui-combobox" style="width:129px;height:56px;"
			label="申请医生：" labelPosition="top"
			 data-options="icons:[{iconCls:'icon-clear'}],onClickIcon:function(){$('#doctor_im').combobox('clear');},
                  valueField: 'id',
                  textField: 'text'">
            </div>
		</div>
		
		<div style="width: 270px;margin-top: 3px;">
			<label>创建时间：</label>
		</div>

		<div class="easyui-panel" style="width:268px;margin-top:3px;padding:2px;">
				<a id="apptoday_im" class="easyui-linkbutton" style="width:83px;height:26px;" data-options="toggle:true,group:'g1',selected:true,plain:true" 
					onclick="$('#appdatefrom').datebox('setValue','');$('#appdateto').datebox('setValue','');$('#appdate').val('T');search_im();">${sessionScope.locale.get("wl.today")}</a>
		        <a  id="appyesterday_im" class="easyui-linkbutton" style="width:85px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
		        	onclick="$('#appdatefrom').datebox('setValue','');$('#appdateto').datebox('setValue','');$('#appdate').val('Y');search_im();">${sessionScope.locale.get("wl.yesterday")}</a>
		        <a id="appthreeday_im" class="easyui-linkbutton" style="width:83px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
		        	onclick="$('#appdatefrom').datebox('setValue','');$('#appdateto').datebox('setValue','');$('#appdate').val('TD');search_im();">近三天</a>
			</div>
			
			<div class="easyui-panel" style="width:268px;margin-top:3px;padding:2px;">
				<a id="appfiveday_im" class="easyui-linkbutton" style="width:83px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
					onclick="$('#appdatefrom_im').datebox('setValue','');$('#appdateto_im').datebox('setValue','');$('#appdate_im').val('FD');search_im();">近五天</a>
		        <a  id="appweek_im" class="easyui-linkbutton" style="width:85px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
		        	onclick="$('#appdatefrom_im').datebox('setValue','');$('#appdateto_im').datebox('setValue','');$('#appdate_im').val('W');search_im();">近一周</a>
		        <a id="appmonth_im" class="easyui-linkbutton" style="width:83px;height:26px;" data-options="toggle:true,group:'g1',plain:true" 
		        	onclick="$('#appdatefrom_im').datebox('setValue','');$('#appdateto_im').datebox('setValue','');$('#appdate_im').val('M');search_im();">近一个月</a>
			</div>

			<div style="width:268px;margin-top:3px;">
				<input id="appdatefrom_im" name="datefrom" class="easyui-datebox" style="width: 125px;height:26px;" data-options="
					onSelect:function(){
						$('#apptoday_im').linkbutton({selected:false});
						$('#appyesterday_im').linkbutton({selected:false});
						$('#appthreeday_im').linkbutton({selected:false});
						$('#appfiveday_im').linkbutton({selected:false});
						$('#appweek_im').linkbutton({selected:false});
						$('#appmonth_im').linkbutton({selected:false});
						$('#appdate_im').val('');
					}">
				-
				<input id="appdateto_im" name="dateto" class="easyui-datebox" style="width: 125px;height:26px;" data-options="
					onSelect:function(){
						$('#apptoday_im').linkbutton({selected:false});
						$('#appyesterday_im').linkbutton({selected:false});
						$('#appthreeday_im').linkbutton({selected:false});
						$('#appfiveday_im').linkbutton({selected:false});
						$('#appweek_im').linkbutton({selected:false});
						$('#appmonth_im').linkbutton({selected:false});
						$('#appdate_im').val('');
					}">
			</div>
		
		<div style="width: 270px;margin-top: 3px;">
			<div style="width:130px;float:left;">
			<input class="easyui-radiobutton" name="status" value="toIP" label="转登记：" Checked="True">
			</div>
				
			<div style="width:130px;float:right;">
			<input class="easyui-radiobutton" name="status" value="3" label="已登记：">
			</div>
		</div>
		
		<div style="margin-top: 30px;">
			<a class="easyui-linkbutton" onclick="search_im()" style="width:268px;height:28px">查询</a>
		</div>
		
		<div style="margin-top: 5px;">
			<a class="easyui-linkbutton" onclick="clear_im()" style="width:268px;height:28px">条件清除</a>	
		</div>
		<input id="appdate_im" type="hidden" name="appdate" value="T"/>
      </form>
</div>
</div>
<div data-options="region:'center',title:'查询结果'">
	<table id="dg1_im" class="easyui-datagrid" sortName="" sortOrder="asc" border="0"
		data-options="rownumbers: true,singleSelect:true,fit:true,
		loadMsg:'加载中...',emptyMsg:'没有查找到登记信息...',
		toolbar:'#tool_div_im',remoteSort:false,
	       onRowContextMenu: function(e,index ,row){
	           e.preventDefault();
	           $(this).datagrid('selectRow',index);
	           $('#dg1menu_im').menu('show', {
	               left:e.pageX,
	               top:e.pageY
	           });
	       }
		">
	<thead>
		<tr>
			<th data-options="field:'order_status',width:70" sortable="true">申请单状态</th>
			<th data-options="field:'studyid',width:80" sortable="true">检查号</th>
			<th data-options="field:'patientid',width:80" sortable="true">病人编号</th>
			<th data-options="field:'patientname',width:80" sortable="true">姓名</th>
			<th data-options="field:'sexdisplay',width:50" sortable="true">性别</th>
			<th data-options="field:'birthdate',width:80" sortable="true">出生日期</th>
			<th data-options="field:'modality_type',width:65" sortable="true">设备类型</th>
			<th data-options="field:'studyitems',width:200,align:'center'">检查项目</th>
			<th data-options="field:'studydatetime',width:120,align:'center'" sortable="true">检查日期</th>
			<th data-options="field:'age',width:50,sortable:true,sorter:function(a,b){return (a>b?1:-1);}">年龄</th>
			<th data-options="field:'ageunitdisplay',width:35">单位</th>
			<th data-options="field:'inno',width:60,align:'center'" sortable="true">住院号</th>
			<th data-options="field:'outno',width:60,align:'center'" sortable="true">门诊号</th>
			<th data-options="field:'wardno',align:'center'" sortable="true">病区</th>
			<th data-options="field:'appdept',align:'center'" sortable="true">申请部门</th>
			<th data-options="field:'appdoctor',align:'center'" sortable="true">申请医生</th>
		</tr>
	</thead>
	</table>
	<div id="tool_div_im" style="padding:2px 5px;">
   	<a href="#" class="easyui-linkbutton" plain="true" onClick="turnIntoRegister_im()"><i class="icon iconfont icon-edit"></i>&nbsp;转登记</a>
	</div>
	<div id="dg1menu_im" class="easyui-menu" style="width:120px">
   		<div onclick="turnIntoRegister_im();">转登记</div>
	</div>
</div>
</div>
</body>
</html>