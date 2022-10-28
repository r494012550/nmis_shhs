<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<div class="easyui-layout" fit="true">
		<div region="west" split="true" style="width:290px;" >
		    <div id="" style="margin-top: 10px">
			    <a style="margin-right: 30px;margin-left: 10px"  class="easyui-linkbutton" href="javascript:void(0)"  onclick="initialReport('<%=request.getParameter("studyid")%>');">初始报告</a>
				<a style="margin-right: 30px"  class="easyui-linkbutton" href="javascript:void(0)"  onclick="referenceReport('<%=request.getParameter("studyid")%>');">参照报告</a>
				<a class="easyui-linkbutton" href="javascript:void(0)"  onclick="compareReport('<%=request.getParameter("studyid")%>');">报告对比</a>
		    </div>
			<div style="margin-top:20px;margin-left: 10px">
			   报告书写痕迹列表
			</div>
			<div style="margin-top:10px;margin-left:7px;" onclick="findReportById('<%=request.getParameter("studyid")%>');">
			    <table id="conDataGrid" class="easyui-datagrid" align="center" style="width:275px;height:250px;"
			    		data-options="fitColumns:true,singleSelect:true">
	    			<thead>
						<tr>
							<th data-options="field:'reportphysician_name',width:70" sortable="true">操作者</th>
							<th data-options="field:'reportstatus_zh',width:70" sortable="true">保存状态</th>
							<th data-options="field:'createtime',width:150" sortable="true">保存时间</th>
						</tr>
					</thead>
				</table>
			</div>
			<div style="margin-left: 7px;margin-top: 20px;">
			       初始报告：<input readonly="readonly" id ="initial"   type="text"  value=""  style="width:190px">
		    </div>
			<div style="margin-top:20px ;margin-left: 7px">
			        参照报告：<input readonly="readonly" id ="reference" type="text"  value="" style="width:190px">
			</div>
			<div style="margin-left: 7px;margin-top: 20px;margin-right: 10px">
				1、请从报告列表中选择初始报告和参照报告。选择完成后，点击报告对比按钮进行报告对比。
			</div>
			<div style="margin-left: 7px">
				2、单击列表可以浏览报告内容。
			</div>
		</div>

		<div region="center" border="0" >
			<div id="reporttabs" class="easyui-tabs" data-options="fit:true,border:false" >
				<div title=""> 
					<div >
						<h2 align="center">检查报告单</h2>
					</div>
					<!-- <table align="">
					    <tr style="margin-bottom: 10px">
					      <td style="text-align:right" width="80">姓名:</td>
					      <td width="80"><input readonly="readonly" id="pname" type="text" value="" style="width:90px;"></td>
					      <td style="text-align:right" width="80">性别:</td>
					      <td width="90"><input id="pgender" readonly="readonly" type="text"  style="width:90px;"></td>
					      <td style="text-align:right" width="80">年龄:</td>
					      <td width="90"><input id="page" readonly="readonly" type="text"  style="width:90px;"></td>
					      <td style="text-align:right" width="80">门诊号:</td>
					      <td width="90"><input id="poutno" readonly="readonly" type="text"  style="width:90px;"></td>
					      <td style="text-align:right" width="80">住院号:</td>
					      <td width="90"><input id="pinno" readonly="readonly" type="text"  style="width:90px;"></td>						      
					    </tr>
					    <tr style="margin-bottom: 10px;">
					      <td style="text-align:right;height:40px;" width="80";>检查号:</td>
					      <td width="90"><input id="pstudyorderstudyid" readonly="readonly" type="text"  style="width:90px;"></td>
					      <td style="text-align:right;" width="80">编号:</td>
					      <td width="90"><input id="patientid_" readonly="readonly" type="text" value="" style="width:90px;"></td>
					      <td style="text-align:right" width="80">检查日期:</td>
					      <td width="90"><input readonly="readonly" id ="checktime" type="text" value="" style="width:90px;"></td>
					      <td style="text-align:right" width="80">病区:</td>
					      <td width="90"><input id="pwardno" readonly="readonly" type="text"  style="width:90px;"></td>
					      <td style="text-align:right" width="80">床号:</td>
					      <td width="90"><input id="pbedno" readonly="readonly" type="text" style="width:90px;"></td>						      
					    </tr>
					    <tr>
					      <td style="text-align:right" width="80">检查项目:</td>
					      <td colspan=4><input id="pstudyitems" readonly="readonly" type="text" value="" style="width:100%"></td>
					    </tr>						    
					</table> -->
					<div style="margin-left: 18px; margin-bottom: 7px; margin-top: 7px;">
						检查方法:
					</div>
					<div align="right">
						<input readonly="readonly" id ="checkMethod" type="text" value=""  style="width:91%;">
					</div>
					<div style="margin-left: 18px; margin-bottom: 7px; margin-top: 7px;">
						影像学表现:
					</div>
					<div align="right">
						 <textarea readonly="readonly" id="apperance" style="width:91%; height:200px;resize:none;"></textarea>
					</div>
					<div style="margin-left: 18px; margin-bottom: 7px; margin-top: 7px;">
						影像学诊断:
					</div>
					<div align="right">
						<textarea readonly="readonly" id="diagnose" style="width:91%; height:100px;resize:none;"></textarea>
					</div>
					
				</div>
			</div>
		</div>
		<div region="south" border="false" style="text-align:right;height:30px;line-height:30px;">
			<!-- <a class="easyui-linkbutton" icon="icon-ok" href="javascript:void(0)">Ok</a>
			<a class="easyui-linkbutton" icon="icon-cancel" href="javascript:void(0)">Cancel</a> -->
		</div>
	</div>
</body>
</html>