<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
   	<form name="examitemdicform" id="examitemdicform" method="POST">
   	<div style="padding:10px;margin-left:auto;margin-right:auto;">
		<div style="margin-bottom:5px">
			<input class="easyui-textbox"  id="item_name" label="项目名称：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"
	       		data-options="prompt:'请输入项目名称...',required:true,missingMessage:'必填',onChange:setItem_py"
	       		name="item_name" value="${item_name}">
	       	<input class="easyui-textbox"  id="py" label="拼音：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"
	        	data-options="prompt:'请输入拼音...'" name="py"  value="${py}"> 
		</div>
		<div style="margin-bottom:5px">
			<input class="easyui-textbox"  id="item_code" label="项目编号：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"
	       		data-options="prompt:'请输入项目编号...'"
	       		name="item_code" value="${item_code}">
		</div>
		<div style="margin-bottom:5px">
			<input class="easyui-combobox" id="type" label="类型：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"
	        	data-options="valueField:'code',textField:'name_zh',required:true,missingMessage:'必填',
	        	url:'${ctx}/syscode/getCode?type=0004',editable:false,prompt:'请选择类别...',
	        	onChange:function(newValue,oldValue){setOrgan(newValue,${empty organfk?-1:organfk})},
	        	onLoadSuccess:function(){$(this).combobox('select', '${type}')}" 
	        	name="type" > 
	        <input class="easyui-numberbox"  id="coefficient" label="工作量系数：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"
	       		data-options="prompt:'请输入工作量系统...',min:1,required:true,missingMessage:'必填'"
	       		name="coefficient" value="${coefficient}">
		</div>
		<div style="margin-bottom:5px">
			<input class="easyui-combobox" id="organfk" label="部位：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"
				data-options="editable:false,valueField: 'id',textField: 'treename_zh',
				onChange:function(newValue,oldValue){setSuborgan(newValue,${empty suborganfk?-1:suborganfk})}"
				name="organfk" value="${organfk}">
			<input class="easyui-combobox" id="suborganfk" label="子部位：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"
				data-options="editable:false,valueField: 'id',textField: 'treename_zh',
				icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]"
				name="suborganfk">
		</div>
		<div style="margin-bottom:5px">
			<input class="easyui-numberbox" id="report_alert_hour" label="报警时间(时)：" labelAlign="right" labelWidth="100"
	  			data-options="min:1,max:24,prompt:'请输入时间...'"  style="width:300px;height:30px;"
		        name="report_alert_hour" value="${report_alert_hour}">
		    <input class="easyui-numberbox"  id="report_alert_minute" label="报警时间(分)：" labelAlign="right" labelWidth="100"
		        data-options="min:1,max:59,prompt:'请输入时间...'"  style="width:300px;height:30px;" 
		        name="report_alert_minute" value="${report_alert_minute}">
		</div>
		<div style="margin-bottom:5px">	
			<input class="easyui-numberbox"  id="duration" label="持续时间：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"
		        data-options="prompt:'请输入时间...'"
		        name="duration" value="${duration}">
			<input class="easyui-numberbox"  id="price" label="价格：" labelWidth="100" labelAlign="right" style="width:300px;height:30px;"
		        data-options="min:0,precision:2,prompt:'请输入价格...'"
		        name="price" value="${price}">
		</div>
		<div style="margin-bottom:5px">
			<input class="easyui-textbox" id="fulldescription" label="描述：" labelAlign="right" labelWidth="100" style="width:603px;height:80px;"
	  			data-options="prompt:'请输入描述...',multiline:true "
	  			name="fulldescription" value="${fulldescription}">
		</div>
		<div style="margin-bottom:3px;padding:5px 10px 5px 100px;">
			<table>
			   	<tr>
				   	<td><label Width="300" >已选设备：</label>
				   	</td>
				   	<td style="width:100px;" >
				   	</td>	
				   	<td><label Width="300" >可选设备：</label>
				   	</td>
			   	</tr>
			   	<tr>
			   		<td>
			   		<div id="equipdg1" class="easyui-datalist" style="width:200px;height:120px;"
						data-options="textField:'modality_name',valueField:'id',singleSelect:false,ctrlSelect:true,
						url:'${ctx}/dic/getExamEquip?id=${id}'"></div>
					</td>
					
					<td>
						 <a class="easyui-linkbutton easyui-tooltip" title="添加" plain="true" style="width:93px;" onClick="addEquip()"><i class="icon iconfont icon-arrowleft"></i></a>
						<br> <a class="easyui-linkbutton easyui-tooltip" title="移除" plain="true" style="width:93px;"onClick="removeEquip()"><i class="icon iconfont icon-arrowright"></i></a>
					</td>
					
					<td>
						<div id="equipdg2" class="easyui-datalist" style="width:197px;height:120px;"
						data-options="textField:'modality_name',valueField:'id',singleSelect:false,ctrlSelect:true"></div>
						
					 </td>
			   	</tr>
		   	</table>
		</div>
   	</div>
   	
	<div>
		<input id="id" name="id" type="hidden" value="${id}" />
		<input id="equipments" name="equipments" type="hidden" value="">
	</div>
</form>
<script type="text/javascript">
function setItem_py(){
	$("#py").textbox('setValue',$(this).toPinyinFirst());
}
</script>
</body>
</html>