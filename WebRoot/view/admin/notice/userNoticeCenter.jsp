<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
	<div class="easyui-layout" data-options="fit: true,border:false">
		<div data-options="region:'north',hideCollapsedContent:false,border:true" style="height: 350px;">
			<table id="user_notice_dg" class="easyui-datagrid" style="height: 600px;"
				data-options="showFooter: true,singleSelect:false,border:false,fit:true,toolbar:'#user_notice_dg_toolbar',
				url:'${ctx}/notice/getUserNoticeByCondition',fitColumns:true,scrollbarSize: 0,pagination: true,
				onDblClickRow:function(index,row){
					$(this).datagrid('selectRow',index);
					openNotice('user_center', 'user_notice_dg');
				},singleSelect:true,
				onLoadSuccess:function(data) {
					noticeDefaultSelect('${noticeId}',data);
				},
				loadMsg:'${sessionScope.locale.get('loading')}',emptyMsg:'${sessionScope.locale.get('nosearchresults')}...'
				">
				<thead>
					<tr>
						<th data-options="field:'title',width:100">标题</th>
						<th data-options="field:'mustread',width:50,formatter:mustreade_formater">是否必读</th>
						<th data-options="field:'releasetime',width:50">发布时间</th>
						<th data-options="field:'createtime',width:50">创建时间</th>
					</tr>
				</thead>
			</table>

			<div id="user_notice_dg_toolbar" style="padding:2px 5px;">
				<input class="easyui-combobox" id="user_timeType" label="时间：" labelWidth="50px" labelAlign="right" style="width:200px;"
						data-options="valueField:'value',textField:'label',value:'createtime',
						data: [{label: '创建时间',value: 'createtime'},{label: '发布时间',value: 'sendtime'}],
						panelHeight:'auto',panelHeight:'auto',editable:false"/>
						
				<input id="user_beginTime" class="easyui-datebox" style="width: 120px;"></input>-<input id="user_endTime" class="easyui-datebox" style="width: 120px;"></input>
				
				<input class="easyui-combobox" id="user_messageType" label="公告类型：" labelWidth="85px" labelAlign="right" style="width:200px;"
					data-options="valueField:'value',textField:'label',value:'0',data: [{label: '已读',value: '1'},{label: '未读',value: '0'}],
					panelHeight:'auto',panelHeight:'auto',editable:false"/>
				
				<label>标题：</label>
				<input class="easyui-textbox" id="user_noticeTitle" style="width:200px;" data-options="prompt:'请输入标题'" >
				<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="usersearchNotice();"><span class="l-btn-left"><span class="l-btn-text">搜索</span></span></a>
			</div>			
		</div>
		
		<div data-options="region:'center',border:true" style="padding: 10px;" >
			<div id="user_center" style="width: 99%;margin-left:auto;margin-right:auto;" class="easyui-panel" data-options="fit: false,border:false">
				<div style="text-align: center;margin-top: 100px;color: red;">请在顶部双击打开公告！</div>
			</div>	
		</div>	
	</div>
<!-- 
			<div class="easyui-layout" data-options="fit: true,border:false">
			<div data-options="region:'north',hideCollapsedContent:false,border:true" style="height: 350px;">
				<table id="notice_dg" class="easyui-datagrid" style="height: 600px;"
					data-options="showFooter: true,singleSelect:false,border:false,fit:true,toolbar:'#notice_dg_toolbar',
					url:'${ctx}/notice/getNoticeByCondition',fitColumns:true,scrollbarSize: 0,pagination: true,
					onDblClickRow:function(index,row){
						$(this).datagrid('selectRow',index);
						openNotice('center', 'notice_dg');
					},singleSelect:true,
					onRowContextMenu: function(e,index ,row){
						e.preventDefault();
						$(this).datagrid('selectRow',index);
						$('#util_div').menu('show', {
							left:e.pageX,
							top:e.pageY
						});
					},
					loadMsg:'${sessionScope.locale.get('loading')}',emptyMsg:'${sessionScope.locale.get('nosearchresults')}...'
					">
					<thead>
						<tr>
							<th data-options="field:'title',width:100">标题</th>
							<th data-options="field:'releasedflag',width:50,formatter:send_state_formater">发布状态</th>
							<th data-options="field:'mustread',width:50,formatter:mustreade_formater">是否必读</th>
							<th data-options="field:'releasetime',width:50">发布时间</th>
							<th data-options="field:'createtime',width:50">创建时间</th>
						</tr>
					</thead>
				</table>
				<div id="util_div" class="easyui-menu" style="padding:2px 2px;">
					<div onclick="toEditNotice();">编辑公告</div>
					<div onclick="delNotice();">删除公告</div>
					<div onclick="sendNotice();"> 发送公告</div>
				</div>
				<div id="notice_dg_toolbar" style="padding:2px 5px;">
					<input class="easyui-combobox" id="timeType" label="时间：" labelWidth="50px" labelAlign="right" style="width:200px;"
							data-options="valueField:'value',textField:'label',value:'createtime',
							data: [{label: '创建时间',value: 'createtime'},{label: '发布时间',value: 'sendtime'}],
							panelHeight:'auto',panelHeight:'auto',editable:false"/>
							
					<input id="beginTime" class="easyui-datebox" style="width: 120px;"></input>-<input id="endTime" class="easyui-datebox" style="width: 120px;"></input>
					
					<input class="easyui-combobox" id="messageType" label="公告类型：" labelWidth="85px" labelAlign="right" style="width:200px;"
						data-options="valueField:'value',textField:'label',value:'issend',
						data: [{label: '已发布',value: 'issend'},{label: '未发布',value: 'nosend'}],
						panelHeight:'auto',panelHeight:'auto',editable:false"/>
					
					<label>标题：</label>
					<input class="easyui-textbox" id="noticeTitle" style="width:200px;" data-options="prompt:'请输入标题'" >
					<a id="select_user" class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="searchNotice();"><span class="l-btn-left"><span class="l-btn-text">搜索</span></span></a>
					<a id="select_user" class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="toSendNotice();"><span class="l-btn-left"><span class="l-btn-text">发布公告</span></span></a>
				</div>			
			</div>
			
			<div data-options="region:'center',border:true" style="padding: 10px 273px 10px 273px;" >
				<div id="center" style="width: 800px;" class="easyui-panel" data-options="fit: false,border:false">
					<div style="text-align: center;margin-top: 100px;color: red;">请双击打开顶部公告！</div>
				</div>	
			</div>	
		</div>
 -->
 
 <!-- 
 	<div class="easyui-layout" data-options="fit: true,border:false">
		<div data-options="region:'west',hideCollapsedContent:false,border:true" style="width:560px;">
			<table id="user_notice_dg" class="easyui-datagrid" style="height: 600px;"
				data-options="showFooter: true,singleSelect:false,border:false,fit:true,toolbar:'#user_notice_dg_toolbar',
				url:'${ctx}/notice/getUserNoticeByCondition',autoRowHeight:true,fitColumns:true,scrollbarSize: 0,pagination: true,
				onDblClickRow:function(index,row){
					$(this).datagrid('selectRow',index);
					openNotice('user_center', 'user_notice_dg');
				},singleSelect:true,
				onLoadSuccess:function(data) {
					noticeDefaultSelect('${noticeId}',data);
				},
				loadMsg:'${sessionScope.locale.get('loading')}',emptyMsg:'${sessionScope.locale.get('nosearchresults')}...'
				">
				<thead>
					<tr>
						<th data-options="field:'title',width:70">标题</th>
						<th data-options="field:'send',width:15,formatter:send_state_formater">状态</th>
						<th data-options="field:'sendtime',width:30">发送时间</th>
					</tr>
				</thead>
			</table>
			<div id="user_notice_dg_toolbar" style="padding:2px 5px;">
				<label>时间：</label><input id="beginTime" class="easyui-datebox" style="width: 120px;"></input>-<input id="endTime" class="easyui-datebox" style="width: 120px;"></input>
				<input class="easyui-combobox" id="messageType" label="公告类型：" labelWidth="75px" labelAlign="right" style="width:170px;"
					data-options="valueField:'value',textField:'label',value:'noread',
					data: [{label: '未读',value: 'noread'},{'label': '已读','value': 'isread'}],
					panelHeight:'auto',panelHeight:'auto',editable:false,onChange: usersearchNotice"/>
				<a id="select_user" class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="usersearchNotice();"><span class="l-btn-left"><span class="l-btn-text">搜索</span></span></a>
			</div>
		</div>
		
		<div data-options="region:'center',border:true" style="width: 740px;">
			<div id="user_center" class="easyui-panel" data-options="fit: true,border:false" style="padding: 5px 5px 5px 5x;">
				<div style="text-align: center;margin-top: 300px;color: red;">请双击打开左侧公告！</div>
			</div>	
		</div>	
	</div>
  -->
</body>
</html>