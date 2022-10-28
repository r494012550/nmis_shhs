<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Insert title here</title>
</head>
<body>
<div class="easyui-panel" id="previousHistoryPanel" data-options="fit:true,border:false" style="padding:0px;margin:auto;">
	
	<%-- <div class="easyui-panel" style="padding:3px 3px 3px 3px;border:0px">
		<div class="interrogation_content_div" style="padding-left:1px;">
			<input class="easyui-checkbox" label="<font color='blue'></font>" labelWidth="120px" labelPosition="after"
				data-options="onChange:changePreviousHistory"/>
				<label><font color="blue">没有既往病史：</font></label>
		</div>
	</div> --%>
	
	<div class="easyui-panel" style="padding:3px;width:100%;border-top:0px;border-bottom:0px;border-right:0px;">
		<div class="history_content_div">
				<input class="easyui-numberspinner" label="近期发热史：" labelWidth="120px" labelAlign="right" style="width:${input_width}px;height:${input_height}px;"
					name="recent_fever_history"/>℃
				<input class="easyui-numberbox" label="持续时间：" labelWidth="120px" labelAlign="right" style="width:${input_width-10}px;height:${input_height}px;"
					name="recent_fever_duration"/>天
				<input class="easyui-textbox" label="脑梗病史：" labelWidth="120px" labelAlign="right" style="width:${input_width}px;height:${input_height}px;"
					name="cerebral_infarction_history"/>
				<input class="easyui-textbox" label="脑血管病史：" labelWidth="120px" labelAlign="right" style="width:${input_width}px;height:${input_height}px;"
					name="cerebral_vessels_history"/>
				<input class="easyui-textbox" label="部位：" labelWidth="120px" labelAlign="right" style="width:${input_width+40}px;height:${input_height}px;"
				name="trauma_place"/>	
		</div>
		<div class="history_content_div">
			<input class="easyui-textbox" label="糖尿病：" labelWidth="120px" labelAlign="right" style="width:${input_width_long+20}px;height:${input_height}px;"
					name="diabetes"/>
			<input class="easyui-textbox" label="高血压病史：" labelWidth="120px" labelAlign="right" style="width:${input_width_long+7}px;height:${input_height}px;"
				name="hypertension"/>
			<input class="easyui-datebox" label="外伤史.时间：" labelWidth="120px" labelAlign="right" style="width:${input_width+40}px;height:${input_height}px;"
				name="trauma_datetime"/>			
		</div>
		<div class="history_content_div">
			<input class="easyui-textbox" label="家族肿瘤史：" labelWidth="120px" labelAlign="right" style="width:${input_width_long+20}px;height:${input_height}px;"
				name="family_tumor"/>
			<input class="easyui-textbox" label="近期钡餐等检查：" labelWidth="120px" labelAlign="right" style="width:${input_width+20}px;height:${input_height}px;"
				name="recent_examine"/>
			<input class="easyui-textbox" label="近期升白细胞药使用.种类：" labelWidth="180px" labelAlign="right" style="width:${input_width_long}px;height:${input_height}px;"
				name="recent_leukocyte_medicine"/>
		</div>
	</div>
	
	<div class="easyui-panel" style="padding:3px;width:100%;margin-top:3px;border-bottom:0px;border-right:0px;">
		<table style="border-collapse:separate; border-spacing:0px 5px;">
		   <tr>
		    	<td><label><font color="blue">其他病史：</font></label></td>
		   </tr>
		   <tr>
		    	<td><input class="easyui-textbox" label="结核：" labelWidth="100px" labelAlign="right" style="width:${input_width+100}px;height:${input_height}px;"
				name="tuberculosis"/></td>
		    	<td><input class="easyui-textbox" label="血吸虫病史：" labelWidth="100px" labelAlign="right" style="width:${input_width+100}px;height:${input_height}px;"
				name="schistosomiasis_history"/></td>
		    	<td><input class="easyui-textbox" label="肝炎、硬化病史：" labelWidth="120px" labelAlign="right" style="width:${input_width+100}px;height:${input_height}px;"
				name="hepatitis_and_sclerosis"/></td>
		   </tr>
		   <tr>
		    	<td><input class="easyui-textbox" label="痔疮病史：" labelWidth="100px" labelAlign="right" style="width:${input_width+100}px;height:${input_height}px;"
				name="hemorrhoids_history"/></td>
		    	<td><input class="easyui-textbox" label="吸烟史：" labelWidth="100px" labelAlign="right" style="width:${input_width+100}px;height:${input_height}px;"
				name="smoking_history"/></td>
		    	<td><input class="easyui-textbox" label="其它：" labelWidth="120px" labelAlign="right" style="width:${input_width+100}px;height:${input_height}px;"
				name="other_medical_history"/></td>
		   </tr>
		</table>
	</div>
	
	<div class="easyui-panel" style="padding:3px;width:100%;margin-top:3px;border-bottom:0px;border-right:0px;">
	<table style="border-collapse:separate; border-spacing:0px 5px;">
		<tr><td><label><font color="blue">留存资料：</font></label></td></tr>
		<tr>
			<td  align='right'>
				<label>&nbsp;CT/MR 片：</label>
				<input class="easyui-numberspinner" name="ctAndMr"  style="width:120px;height:${input_height}px;" min="0"/>
				<label>张/份 &nbsp;</label>
			</td>
			<td  align='right'>
				<label>&nbsp;报告：</label>
				<input class="easyui-numberspinner" name="presentation" style="width:120px;height:${input_height}px;" min="0"/>
				<label>张/份 &nbsp;</label>
			</td>
			<td  align='right'>
				<label>&nbsp;超声报告：</label>
				<input class="easyui-numberspinner" name="ultrasonic_eport" style="width:120px;height:${input_height}px;" min="0"/>
				<label>张/份 &nbsp;</label>
			</td>
		</tr>
		<tr>
			<td  align='right'>
				<label>&nbsp;胃镜报告：</label>
				<input class="easyui-numberspinner" name="gastroscopy_report" style="width:120px;height:${input_height}px;" min="0"/>
				<label>张/份 &nbsp;</label>
			</td>
			<td  align='right'>
				<label>&nbsp;肠镜报告：</label>
				<input class="easyui-numberspinner" name="enteroscopy_report" style="width:120px;height:${input_height}px;" min="0"/>
				<label>张/份 &nbsp;</label>
			</td>
			<td  align='right'>
				<label>&nbsp;鼻咽镜报告：</label>
				<input class="easyui-numberspinner" name="npg_report" style="width:120px;height:${input_height}px;" min="0"/>
				<label>张/份 &nbsp;</label>
			</td>
		</tr>
		<tr>
			<td  align='right'>
				<label>&nbsp;喉镜报告：</label>
				<input class="easyui-numberspinner" name="laryngoscope_report" style="width:${number_width}px;height:${input_height}px;" min="0"/>
				<label>张/份 &nbsp;</label>
			</td>
			<td  align='right'>
				<label>&nbsp;气管镜报告：</label>
				<input class="easyui-numberspinner" name="tracheoscope_report" style="width:${number_width}px;height:${input_height}px;" min="0"/>
				<label>张/份 &nbsp;</label>	
			</td>
			<td  align='right'>
				<label>&nbsp;外院PET报告：</label>
				<input class="easyui-numberspinner" name="out_PET_report" style="width:${number_width}px;height:${input_height}px;" min="0"/>
				<label>张/份 &nbsp;</label>
			</td>
		</tr>
		<tr>
			<td  align='right'>
				<label>&nbsp;本院PET报告：</label>
				<input class="easyui-numberspinner" name="in_PET_report" style="width:${number_width}px;height:${input_height}px;" min="0"/>
				<label>张/份 &nbsp;</label>
			</td>
			<td  align='right'>
				<label>&nbsp;出院小结：</label>
				<input class="easyui-numberspinner" name="dischargeSummary" style="width:${number_width}px;height:${input_height}px;" min="0"/>
				<label>张/份 &nbsp;</label>	
			</td>
			<td  align='right'>
				<label>&nbsp;其它：</label>
				<input class="easyui-numberspinner" name="other" style="width:${number_width}px;height:${input_height}px;" min="0"/>
				<label>张/份 &nbsp;</label>
			</td>
		</tr>
	</table>
	</div>
</div>
			


<script type="text/javascript">

</script>
</body>
</html>