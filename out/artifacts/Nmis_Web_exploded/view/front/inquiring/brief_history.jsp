<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Insert title here</title>
</head>
<body>
<div class="easyui-panel" data-options="fit:true,border:false" style="padding:0px;margin:auto;">
	
	<div class="easyui-panel" style="padding:3px 3px 3px 3px;border:0px">
		<div class="history_content_div">
			<input class="easyui-textbox" label="临床诊断：" labelWidth="${label_width}" style="width:100%;height:30px;"
				name="clinicaldiagnosis"
				data-options="multiline:true"/>
		</div>
		
		<div class="history_content_div">
			<input class="easyui-textbox" label="简要病史：" labelWidth="${label_width}" style="width:100%;height:100px;"
				name="briefhistory"
				data-options="multiline:true"/>
		</div>
		
		<div class="history_content_div">
				<input class="easyui-datebox" label="手术日期：" labelWidth="110px" labelAlign="right" style="width:230px;height:${input_height}px;"
					name="operationdatetime"/>
				<input class="easyui-combobox" label="手术方式：" labelWidth="140px"  labelAlign="right" style="width:260px;height:${input_height}px;"
					 name="operationmethod"
					 data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0039',editable:false,panelHeight:'auto'"/>
				<input class="easyui-textbox" label="病理：" labelWidth="140px" labelAlign="right" style="width:260px;height:${input_height}px;"
					name="pathology"/>
		</div>
		<div class="history_content_div">
				<input class="easyui-datebox" label="末次化疗日期：" labelWidth="110px" labelAlign="right" style="width:230px;height:${input_height}px;"
					name="last_chemotherapy_datetime"/>
				<input class="easyui-datebox" label="末次放疗日期：" labelAlign="right"  labelWidth="140px" style="width:260px;height:${input_height}px;"
					name="last_radiotherapy_datetime"/>
				<input class="easyui-textbox" label="近期主要影像结果：" labelAlign="right" labelWidth="140px" style="width:260px;height:${input_height}px;"
					name="image_result"/>
		</div>
		<div class="history_content_div">
				<input class="easyui-textbox" label="肿瘤除去结果：" labelAlign="right" labelWidth="110px" style="width:230px;height:${input_height}px;"
					name="tumor_removal"/>
		</div>
	</div>
	
	<div class="easyui-panel" style="padding:3px 3px 3px 3px;margin-top:3px;width:100%;border-bottom:0px;border-right:0px;">
		<div  class="history_content_div" style="padding-left:3px;padding-top:8px;">
				<label><font color="blue">手术缺如部位：</font></label>
		</div>
		<div  class="history_content_div" style="padding-left:5px;padding-top:8px;">
				<input class="easyui-datebox" label="胆囊.时间：" labelWidth="110px" editable="false" labelAlign="right" style="width:230px;height:${input_height}px;"
				name="gallbladder_datetime" />
				<input class="easyui-datebox" label="甲状腺.时间：" labelWidth="140px" editable="false" labelAlign="right" style="width:260px;height:${input_height}px;"
				name="thyroid_datetime"  />
				<input class="easyui-datebox" label="脾.时间：" labelWidth="120px" editable="false" labelAlign="right" style="width:260px;height:${input_height}px;"
				name="spleen_datetime"  />
		</div>
		<div  class="history_content_div" style="padding-left:5px;padding-top:8px;">
				<input class="easyui-datebox" label="左肾.时间：" labelWidth="110px" editable="false" labelAlign="right" style="width:230px;height:${input_height}px;"
				name="left_kidney_datetime" />
				<input class="easyui-datebox" label="右肾.时间：" labelWidth="140px" editable="false" labelAlign="right" style="width:260px;height:${input_height}px;"
				name="right_kidney_datetime"  />
		</div>
	</div>
	
	<div class="easyui-panel" style="padding:3px 3px 0px 3px;margin-top:3px;width:100%;border-bottom:0px;border-right:0px;">
		<div class="easyui-panel" id="female_info" data-options="closed:true" style="padding:3px 3px 0px 3px;margin-top:3px;border:0px;">
			<div class="interrogation_content_div">
				<label><font color="#FF8C00">月经情况：</font></label>
				<input class="easyui-combobox" style="width:150px;height:${input_height}px;"
					name="menstruation_conditions"
					data-options="valueField:'code',textField:'name_zh',url:'${ctx}/syscode/getCode?type=0038',editable:false,panelHeight:'auto'"/>
			</div>
			<div class="interrogation_content_div">
				<label>初潮：</label>
				<input class="easyui-numberbox" style="width:100px;height:${input_height}px;"
					name="menarche_age"/>岁，
			</div>
			<div class="interrogation_content_div">
				<label>经期：</label>
				<input class="easyui-numberbox" style="width:100px;height:${input_height}px;"
					name="menstruation_periods"/>天一次，
			</div>
			<div class="interrogation_content_div">
				<label>每次：</label>
				<input class="easyui-numberbox" style="width:100px;height:${input_height}px;"
					name="menstruation_days"/>天，
			</div>
			<div class="interrogation_content_div">
				<label>近期月经：</label>
				<input class="easyui-numberbox" style="width:100px;height:${input_height}px;"
					name="menstruation_recent"/>天
			</div>
		</div>
	
		<div class="history_content_div">
		<label><font color="blue">问诊其他信息：</font></label>
		<input class="easyui-textbox" style="width:100%;height:290px;"
			name="other_information"
			data-options="multiline:true"/>
		</div>
	</div>
</div>
			


<script type="text/javascript">

</script>
</body>
</html>