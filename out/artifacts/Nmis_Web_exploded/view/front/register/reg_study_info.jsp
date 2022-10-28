<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<c:set var="input_width" value="230"/>
<c:set var="label_width" value="100"/>
<title>Insert title here</title>
</head>
<body>

	<div class="easyui-panel" title='检查'  halign='left' style="height:300px;padding:3px 3px 0px 3px;width:990px;"
     		 	data-options="tools:'#studytools'">
      		<div id="studytools">
      			<a class="easyui-tooltip" title="检查备注" onclick="openNotes('so_remark_reg','检查备注');">
					<i class="iconfont icon-info"></i></a>
      		</div>
      		
      		<div class="reg_content_div">
      			<input id="studyid_reg" name="studyid" class="easyui-textbox" label="检查号：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
      				readonly="readonly" prompt="自动编号">
      		</div>
      		
      		<div class="reg_content_div">
      			<input class="easyui-combobox" label="优先级别：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
					id="priority_com_reg" name="priority"
					data-options="valueField:'code',textField:'name_zh',panelHeight:'auto',
						url:'${ctx}/syscode/getCode?type=0003',editable:false,onLoadSuccess:function(){$(this).combobox('select', 'N')}" required="true">
      		</div>
      		<%-- <div class="reg_content_div">
      			<input class="easyui-combobox" label="登记方式：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
					name="registration_method"
					data-options="valueField:'code',textField:'name_zh',panelHeight:'auto',
						url:'${ctx}/syscode/getCode?type=0032',editable:false,onLoadSuccess:function(){$(this).combobox('select', 'manual')}">
      		</div> --%>
      		
      		<%-- <div class="reg_content_div">
	    		<input class="easyui-numberbox" label="实收：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
					data-options="min:0,precision:2">
    		</div>
      		<div class="reg_content_div">
      			<input class="easyui-combobox" label="收费状态：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
						data-options="">
      		</div> --%>
      		
      		<div class="reg_content_div">
      			<input id="apphospitalcode" name="apphospitalcode" class="easyui-combobox" label="申请医院：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
						data-options="valueField: 'id',textField: 'name',url:'${ctx}/dic/getInstitution?deleted=0',onChange:setDeptReg,
	                    panelHeight:'200px',editable:false,icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]">
      		</div>
      		<div class="reg_content_div">
      			<input id="appdeptcode" name="appdeptcode" class="easyui-combobox" label="申请科室：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
      				data-options="icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]">
	            <input id="appdeptname_reg" class="myhidden" name="appdeptname" type="hidden">
      		</div>
      		<%-- <div class="reg_content_div">
      			<input id="doctor_reg" name="appdoctorcode" class="easyui-combobox" label="申请医生：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
						data-options="valueField: 'appdoctorcode',textField: 'appdoctorname',editable:false,panelHeight:'120px'">
				<input id="appdoctorname_reg" class="myhidden" name="appdoctorname" type="hidden">
      		</div> --%>
      		<div class="reg_content_div">
      			<input id="modality_reg" name="modality_type" class="easyui-combobox" label="设备类型：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
						data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0004',panelHeight:'120px',
						editable:false,onLoadSuccess:function(data){if(data){$(this).combobox('select', data[0].code)}},onChange:setOrgan">
      		</div>
      		<div class="reg_content_div">
      			<input id="modality_dic_reg" name="modalityid" class="easyui-combobox" label="检查设备：" labelWidth="${label_width}" labelAlign="right" 
					style="height:25px;width:${input_width}px;" required="true" data-options="
		                    valueField:'id',textField:'modality_name', 
		                   	onChange:modalitydic_onChange,
		                    panelHeight:'120px',editable:false">
      		</div>
      		
      		<div class="reg_content_div">
      			<input class="easyui-combobox" label="核素：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
						name="nuclide" required="true"
						data-options="url:'${ctx}/dic/findDicCommonFromCache?group=nuclide',valueField: 'id',textField: 'name',editable:false,panelHeight:'120px'">
      		</div>
      		<%-- <div class="reg_content_div">
      			<input class="easyui-combobox" label="示踪剂：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;" 
						name="tracer"
						data-options="url:'${ctx}/dic/findDicCommonFromCache?group=tracer',valueField: 'id',textField: 'name',editable:false,panelHeight:'120px'">
      		</div>  --%>

      		<div class="reg_content_div">
      			<input id="organ_reg" class="easyui-combobox" label="部位：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
						name="organ"
						data-options="editable:false,panelHeight:'120px',
						valueField: 'id',textField: 'treename_zh',panelHeight:'120px',
						icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]">
      		</div>
      		
      		<div class="reg_content_div">
      			<input  id="examination_method" class="easyui-combobox" label="检查方法：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width*2+3}px;" 
						name="examination_method" data-options="valueField: 'id',textField: 'name',editable:false,panelHeight:'120px'">
      		</div>
      		
      		
      		<%-- <div class="reg_content_div">
     			<input id="suborgan_reg" name="suborgan" class="easyui-combobox" label="子部位：" labelWidth="${label_width}" labelAlign="right" style="height:25px;width:${input_width}px;"
					data-options="editable:false,panelHeight:'120px',
						valueField: 'id',textField: 'treename_zh',onChange:doSearchItem,panelHeight:'120px',
						icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]">
      		</div> --%>
      		
      		<div class="reg_content_div">
      			<div style="float:left;width:${input_width+6}px;">
      			<input class="easyui-searchbox" id="searchItemtree_reg" data-options="prompt:'项目名称或拼音',searcher:doSearchItem" style="height:25px;width:${input_width}px;">
            	<div id="itemtree_reg" class="easyui-datalist" style="height:176px;width:${input_width}px;"
					data-options="textField:'item_name',valueField:'item_name',onClickRow:treeclick_reg">
				</div>
				</div>
      		
      			<div style="float:right;width:${input_width*2}px;">
      			<table id="studydg_item_reg" class="easyui-datagrid" title="" style="width:${input_width*2}px;height:200px;" 
					data-options="rownumbers: false,showFooter: true,singleSelect: true, fitColumns:true, scrollbarSize:0,
					onAfterEdit:afterChangePrice,
					onRowContextMenu: function(e,index ,row){
				            e.preventDefault();
				            $(this).datagrid('selectRow',index);
				            $('#cmenu_studyitem_reg').menu('show', {
				                left:e.pageX,
				                top:e.pageY
				            });
				        }">
			        <thead>
			            <tr>
			            	<th data-options="field:'id',width:60,hidden:true">id</th>
			                <th data-options="field:'modality',width:70">检查类型</th>
			                <th data-options="field:'item_name',width:175">检查项目</th>
			                <th data-options="field:'price',width:70">计价金额</th>
			                <th data-options="field:'realprice',width:70,align:'right',editor:{type:'numberbox',options:{precision:2}}">实收金额</th>
			                <th data-options="field:'charge_status',width:70">缴费状态</th>
			            </tr>
			        </thead>
			    </table>
			    <div id="cmenu_studyitem_reg" class="easyui-menu" style="width:120px">
			    	<div onclick="deleteItem('studydg_item_reg');">删除</div>
			    	<div onclick="deleteItemAll('studydg_item_reg');">删除全部</div>
			    </div>
			    </div>
      		</div>
      		
      		<div class="reg_content_div">
      			<c:if test="${enable_scan_module}">
					申请单
					<div class="cl imglist" id="img_ul" style="width:228px;height:140px;"></div>
				</c:if>
      		</div>
			
			<input id="studyorderid_reg" class="myhidden" name="studyorder_id" type="hidden"/>
      		<input id="so_remark_reg" class="myhidden" name="studyorderremark" type="hidden"> 
			<input id="itemsstr_reg" class="myhidden"  name="itemsvalue" type="hidden"/>
			<!-- <input id="hisorderid_reg" class="myhidden" name="hisorderid" type="hidden"/> -->
			<input id="eorderid" class="myhidden" name="eorderid" type="hidden"/>
			<input id="studyorder_status_reg" class="myhidden" name="status" type="hidden"/>
			<input id="appdatetime_reg" class="myhidden" name="appdatetime" type="hidden"/>
			
			<input id="systemid_reg" class="myhidden" name="systemid" type="hidden"/>
			<input id="requestItemId_reg" class="myhidden" name="requestItemId" type="hidden"/>
			<input id="requestItemCode_reg" class="myhidden" name="requestItemCode" type="hidden"/>
			<input id="requestItemName_reg" class="myhidden" name="requestItemName" type="hidden"/>
			
			<input id="unmatchkid_reg" class="myhidden" name="unmatchkid" type="hidden"/>
	</div>	
</body>
</html>