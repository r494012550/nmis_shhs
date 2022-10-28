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
	<header style="color:#8aa4af;">您当前的位置：排班管理  > 班次管理</header>
	
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'west',collapsible:false" style="width:60%;padding:20px;" title="班次管理">
				<div class="easyui-layout" data-options="fit:true,border:false">
			        <!-- <div data-options="region:'north'" style="height:50px"></div>
			        <div data-options="region:'west'" style="width:150px;"></div> -->
			        <div data-options="region:'center',border:false" title="班次列表" style="padding:5px 0px;">
			        	<table id="shiftdg" class="easyui-datagrid" style="width:100%;"
							data-options="singleSelect:true,fit:true,
								url:'${ctx}/workforce/getDeptShift?deleted=0',autoRowHeight:true,fitColumns:true,border:true
								,onClickRow:function(index,row){
									selectOneShift(row);
								}">
							<thead>
									<tr>
										<th data-options="field:'name',width:80">班次名称</th>						
										<th data-options="field:'worktimes',width:300">时间段</th>
										<th data-options="field:'operate',width:50,align:'center',formatter:operatecolumn_deptshift">操作</th>
									</tr>
							</thead>
				        </table>
			        </div>
			        
			        <div data-options="region:'south',collapsible:false,border:false" style="height:65%;padding:10px 10px;" title="编辑班次">
			        <form name="shiftform" id="shiftform" method="POST">
			        	<div style="margin-bottom:10px">
				        	<input class="easyui-textbox" id="shiftname" name="name" label="班次名称：" labelPosition="top" style="width:100%;" required="true">
				        </div>
				        <div style="margin-bottom:10px">
				        	<select class="easyui-combobox" id="shift_type" name="type" label="类别：" labelPosition="top" style="width:100%;"
				        		data-options="editable:false,panelHeight:'auto'">
				        		<option value="23">报告班次</option>
				                <option value="27">初审班次</option>
				                <option value="31">审核班次</option>
				        	</select>
				        </div>
			        	<input id="shiftid" name="id" type="hidden" value="">
				        <input id="shiftworktimeids" name="worktimeids" type="hidden" value="">
				        <input id="shiftworktimes" name="worktimes" type="hidden" value="">
				    </form>
				        <table id="shiftworktimedg" class="easyui-datagrid" style="width:100%;height:170px;" title="时间段"
							data-options="url:'${ctx}/workforce/getWorktimes?deleted=0',autoRowHeight:true,fitColumns:true,border:true
								,selectOnCheck:true,checkOnSelect:true,singleSelect:false">
							<thead>
									<tr>
										<th data-options="field:'ck',checkbox:true"></th>
										<th data-options="field:'name',width:140">名称</th>						
										<th data-options="field:'starttime',width:140">开始时间</th>
										<th data-options="field:'endtime',width:140">结束时间</th>
									</tr>
							</thead>
				        </table>
				        
				        <div style="margin-top:10px">
				        	<a class="easyui-linkbutton easyui-tooltip" title="新建班次" style="width:100px;" onClick="newShift();">${sessionScope.locale.get('admin.create')}</a>
				        	<a class="easyui-linkbutton easyui-tooltip" title="保存班次" style="width:100px;" onClick="saveShift();">保存</a>
				        	<a class="easyui-linkbutton easyui-tooltip c2" title="清空" style="width:100px;" onClick="clearShiftFiled();">清空</a>
				        </div>
				        
				        
			        </div>
			    </div>
			</div>
			<div data-options="region:'center'" style="padding:20px;" title="时间段管理">
				<div class="easyui-layout" data-options="fit:true">
			        <!-- <div data-options="region:'north'" style="height:50px"></div>
			        <div data-options="region:'west'" style="width:150px;"></div> -->
			        <div data-options="region:'center',border:false" title="时间段列表" style="padding:5px 0px;">
			            <table id="worktimedg" class="easyui-datagrid" style="width:100%;"
							data-options="singleSelect:true,fit:true,
								url:'${ctx}/workforce/getWorktimes?deleted=0',autoRowHeight:true,fitColumns:true,border:true
								,onClickRow:function(index,row){
									selectOneWorktime(row);
								}">
							<thead>
									<tr>
										<th data-options="field:'name',width:140">名称</th>						
										<th data-options="field:'starttime',width:140">开始时间</th>
										<th data-options="field:'endtime',width:140">结束时间</th>
										<th data-options="field:'operate',width:60,align:'center',formatter:operatecolumn_worktime">操作</th>
									</tr>
							</thead>
				        </table>
			        </div>
			        
			        <div data-options="region:'south',collapsible:false,border:false" style="height:65%;padding:10px 10px;" title="编辑时间段">
			        <form name="worktimeform" id="worktimeform" method="POST">
			        	<div style="margin-bottom:10px">
				        	<input class="easyui-textbox" id="worktimename" name="name" label="名称：" labelPosition="top" style="width:100%;" required="true">
				        </div>
			        	<div style="margin-bottom:10px">
				            <input class="easyui-timespinner" id="starttime" name="starttime" label="开始时间：" labelPosition="top" value="08:00" style="width:100%;">
				        </div>
				        <div style="margin-bottom:10px">
				            <input class="easyui-timespinner" id="endtime" name="endtime" label="结束时间：" labelPosition="top" value="11:59" style="width:100%;">
				        </div>
				        <div>
				        	<a class="easyui-linkbutton easyui-tooltip" title="新建时间段" style="width:100px;" onClick="newWorktime();">${sessionScope.locale.get('admin.create')}</a>
				        	<a class="easyui-linkbutton easyui-tooltip" title="保存时间段" style="width:100px;" onClick="saveWorktime();">保存</a>
				        </div>
				        
				        <input id="worktimeid" name="id" type="hidden" value="">
				    </form>
			        </div>
			    </div>
			</div>
		</div>
	
	
		
	</div>

</body>
</html>