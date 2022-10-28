<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>登记</title>
</head>
<body>
<c:set var="input_width" value="240px"/>
<form name="emergencyForm" id="emergencyForm" method="POST">
<div class="easyui-panel" style="padding:5px;border-left:0;border-right:0">
	<div style="width:274px;float:left;">
	<a class="easyui-linkbutton" style="width:120px;" data-options="plain:true,iconCls:'icon-add'" onclick="clearEmergencyPara()">新建急诊</a>
	<a id="emergency_menubtn_emerg" class="easyui-menubutton" style="width:120px;" data-options="plain:true,iconCls:'icon-more'" >更多配置</a>
	</div>
	
	<input class="easyui-textbox" id="configname_emerg" name="configname" label="配置名称：" labelWidth="100" labelAlign="right"
		style="height:25px;width:${input_width};" required="true" >	
	
</div>
<div>
		<div style="margin-top: 5px;margin-left: 10px;padding:0px 0px 0px 5px;">
			<table class="mytablelaout" style="width:800px;">
				<tr>
					<td style="width:270px;">
						<input class="easyui-textbox" id="patientname_emerg" name="patientname" label="患者姓名：" labelWidth="100" labelAlign="right"
							style="height:25px;width:${input_width};" required="true" 
							data-options="onChange:function(){$('#pinyin_emerg').textbox('setValue',pinyinUtil.getPinyin($(this).val(), '', false, false, true));}">
					</td>
					<td style="width:270px;">
						<input class="easyui-textbox" id="pinyin_emerg" name="py" label="拼音：" labelWidth="100" labelAlign="right"
							style="height:25px;width:${input_width};" required="true">
					</td>
					<td style="width:270px;">
						<input class="easyui-combobox" id="sex_emerg" name="sex" label="性别：" labelWidth="100" labelAlign="right"
							style="height:25px;width:${input_width};" required="true" 
							data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0001',editable:false,panelHeight:'auto'">
					</td>
				</tr>
				<tr>
					<td>
						<input id="age_emerg" name="age" type="text" class="easyui-numberbox" label="年龄：" labelWidth="100" labelAlign="right"
							style="width:170px;height:25px;"  required="true" 
							data-options="min:0,max:200,onChange:calculate_age1_emerg">
						<input id="age_unit_emerg" name="ageunit" class="easyui-combobox" style="width:63px;height:25px;" required="true" 
							data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0008',editable:false,panelHeight:'auto',
								onLoadSuccess:function(){$(this).combobox('select', 'Y')},onChange:calculate_age2_emerg">
					</td>
					<td>
						<input id="birthdate_emerg" name="birthdate" class="easyui-datebox" label="出生日期：" labelWidth="100" labelAlign="right"
							style="height:25px;width:${input_width};" 
							data-options="onSelect:getAgeFromBirthdate_emerg">
					</td>
					<td style="width:270px;">
						<input id="patientsource_emerg" name="patientsource" class="easyui-combobox" label="患者来源：" labelWidth="100" labelAlign="right"
							style="height:25px;width:${input_width};"  required="true"  readonly="readonly"
							data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0002',editable:false,panelHeight:'auto'" 
							value="E">
					</td>
				</tr>
				<tr>
					<td>
						<input id="modality_emerg" name="modality_type" class="easyui-combobox" label="设备类型：" labelWidth="100" labelAlign="right"
						style="height:25px;width:${input_width};"  required="true" 
						data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0004',panelHeight:'120px',
						editable:false,onChange:setOrgan_emerg,onLoadSuccess:function(){$(this).combobox('select', 'CT')}">
					</td>
					<td>
						<input id="modality_dic_emerg" name="modalityid" class="easyui-combobox" label="检查设备：" labelWidth="100" labelAlign="right" 
							style="height:25px;width:${input_width};" 
							data-options="valueField:'id',textField:'modality_name',onChange:modalitydic_onChange_emerg,
		                    panelHeight:'120px',editable:false">
		            </td>
		            <td>
      					<input id="organ_emerg" class="easyui-combobox" label="部位：" labelWidth="100" labelAlign="right"
      					  style="height:25px;width:${input_width};"
							data-options="editable:false,onChange:setSuborgan_emerg,
							valueField: 'id',textField: 'treename_zh',panelHeight:'120px',
							icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]">
		            </td>
		         </tr>
		         <tr>
		         	<td>
		         		<input class="easyui-searchbox" id="searchItemtree_emerg" data-options="prompt:'项目名称或拼音'" style="height:25px;width:${input_width};">
		            	<div id="itemtree_emerg" class="easyui-datalist" style="height:176px;width:${input_width};"
							data-options="textField:'item_name',valueField:'item_name',onClickRow:treeclick_emerg">
						</div>
		         	</td>
		         	<td colspan="2">
		         		<table id="studydg_item_emerg" class="easyui-datagrid" title="" style="width:510px;height:200px;" 
							data-options="rownumbers: false,showFooter: true,singleSelect: true,
							onRowContextMenu: function(e,index ,row){
						            e.preventDefault();
						            $(this).datagrid('selectRow',index);
						            $('#cmenu_studydg_item_emerg').menu('show', {
						                left:e.pageX,
						                top:e.pageY
						            });
						        }">
					        <thead>
					            <tr>
					            	<th data-options="field:'id',width:60,hidden:true">id</th>
					                <th data-options="field:'modality',width:70">检查类型</th>
					                <th data-options="field:'item_name',width:200">检查项目</th>
					                <th data-options="field:'price',width:70">计价金额</th>
					                <th data-options="field:'realprice',width:70,align:'right',editor:{type:'numberbox',options:{precision:2}}">实收金额</th>
					                <th data-options="field:'charge_status',width:70">缴费状态</th>
					            </tr>
					        </thead>
					    </table>
					    <div id="cmenu_studydg_item_emerg" class="easyui-menu" style="width:120px">
					    	<div onclick="deleteItem('studydg_item_emerg');">删除</div>
					    	<div onclick="deleteItemAll('studydg_item_emerg');">删除全部</div>
					    </div>
		         	</td>
		         </tr>
		    </table>
		    <input id="itemsstr_emerg" name="itemsvalue" type="hidden"/>
		    <input id="defaultInfoid" name="id" type="hidden"/>
		</div>
</div>
</form>
<div id="emergency_menu_emerg" type="hidden"></div>
</body>
</html>