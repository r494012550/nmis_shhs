<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
<c:set var="input_width" value="300"/>
<c:set var="label_width" value="100"/>
</head>
<body>
	<div class="easyui-panel" data-options="fit:true" style="padding:10px;">
		<header style="color:#8aa4af;">您当前的位置：项目管理  > 入组申请</header>
		<table id="reportApprovaldg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,singleSelect:false,fit:true,toolbar:'#toolbar_div_joingroupapp',
				url:'${ctx}/research/searchJoingroupApply?status=1',autoRowHeight:true,fitColumns:true,scrollbarSize:0,
				loadMsg:'加载中...',emptyMsg:'没有查找到入组申请...'">
			<thead>
				<tr>
					<th data-options="field:'ck',checkbox:true,width:20,align:'center'"></th>
					<th data-options="field:'project_name',width:100,align:'center'">项目名称</th>
					<th data-options="field:'group_name',width:100,align:'center'">实验组名称</th>
					<th data-options="field:'app_datetime',width:100,align:'center'">申请时间</th>
					<th data-options="field:'valid_period',width:100,align:'center',formatter:valid_formatter">有效期</th>
					<th data-options="field:'user_name',width:100,align:'center'">申请人</th>
					<th data-options="field:'status',width:100,align:'center',formatter:reportStatus_formatter">状态</th>
					<th data-options="field:'chakan',width:'10%',align:'center',formatter:reportHtml_formatter" >查看</th>
				</tr>
			</thead>
		</table>
		<div id="toolbar_div_joingroupapp" style="padding:5px;text-align:right;height:30px;align:center;">
			<input class="easyui-textbox" id="name"     label="申请人：" labelWidth="80" labelAlign="right" style="width:200px;">
			<input id="status" class="easyui-combobox"  label="审核状态："  data-options="editable:false,valueField: 'value',textField: 'text',data: [{
                   text: '全部',
                   value: ''},  
                   {text: '申请',
                   value: '1',
                   selected:true},
                   {text: '已通过',
                   value: '2'},
                   {text: '拒绝',
                   value: '3'}]" labelWidth="80" labelAlign="right" style="width:200px;"/>
			<input class="easyui-datebox" id="strTime"  label="申请时间：" editable="false" labelWidth="80" labelAlign="right" style="width:190px;">
			<input class="easyui-datebox" id="endTime"  label="至：" labelWidth="40" labelAlign="right" style="width:150px;">
		    <a class="easyui-linkbutton easyui-tooltip" title="查询" style="width:80px;" onClick="searchReportOrder();">查询</a>
			<a class="easyui-linkbutton easyui-tooltip" title="同意" style="width:80px;" onClick="openReportApproval();">同意</a>
			<a class="easyui-linkbutton easyui-tooltip" title="拒绝" style="width:80px;" onClick="refuseApply();">拒绝</a>
		</div>
		<div  id="reportApprovalDlg" class="easyui-dialog" style="width:300px;height:200px;padding:10px;" data-options="modal:true,closed:true,title:'申请',border:'thin',
				buttons:[{
				    text:'同意',
				    width: 80,
				    handler:function(){approveApplyForJoinGroup();}
				},{
				    text:'关闭',
				    width: 80,
				    handler:function(){$('#reportApprovalDlg').dialog('close');}
				}]">
			<div style="padding-bottom:10px;">
				<input class="easyui-datebox" id="reportPrescription" editable="false" name="reportPrescription" label="报告时效：" labelWidth="${label_width-20}" labelAlign="right" 
					style="width:${input_width-30}px;">
		    </div>
		    <div>
				<input class="easyui-textbox" id="note" name="note" label="备注：" labelWidth="${label_width-20}" labelAlign="right" style="height:60px;width:${input_width-30}px;">
		    </div>
		</div>
	</div>
	<script>
		function searchReportOrder(){
			var name=$("#name").textbox("getValue");
			var status=$("#status").combobox("getValue");
			var strTime=$("#strTime").datebox("getValue");
			var endTime=$("#endTime").datebox("getValue");
			$("#reportApprovaldg").datagrid("loading");
			getJSON(window.localStorage.ctx+"/research/searchJoingroupApply",{'name':name,'status':status,'strTime':strTime,'endTime':endTime}, function(json){
		    	 $('#reportApprovaldg').datagrid('loadData',json);
		    	 $("#reportApprovaldg").datagrid("loaded");
		    });
		}
		
		function reportStatus_formatter(value,row,index){
			if(value=="1"){
				return "申请";
			}else if(value=="2"){
				return "已通过";
			}else if(value=="3"){
				return "拒绝";
			}else{
				return "";
			}
		}
		
		function reportHtml_formatter(value, row, index){
			return '<a class="easyui-linkbutton easyui-tooltip tooltip-f l-btn l-btn-small" onclick="openJoingroupApplyDetailsDlg('+index+');"><span class="l-btn-left"><span class="l-btn-text">明细</span></span></a>';
		}
		
		function refuseApply(){
			var rows=$("#reportApprovaldg").datagrid("getSelections");
			//console.log(rows)
			if(rows.length==0){
				_message('请选择申请！');
				return;
			}
			for (var i = 0; i < rows.length; i++) {
				if(rows[i].status==2){
					_message('请勿选择已经通过的申请！');
					return;
				}
			}
			$.messager.confirm({
				title: '确认',
				border:'thin',
				msg: '确认拒绝选择的申请？',
				fn: function(r){
					if (r){
						getJSON(window.localStorage.ctx+"/research/refuseApplyForJoinGroup",
				            {'datas':JSON.stringify(rows)},
				            function(data){
								var json=validationData(data);
								if(json.code==0){
									_message("操作成功！");
								    searchReportOrder();
								}else{
									$.messager.show({
						                title:'错误',
						                msg:"删除失败，请重试，如果问题依然存在，请联系系统管理员！",
						                timeout:3000,
						                border:'thin',
						                showType:'slide'
						            });
								}
					    });
					}
				}
			});
		}
		
		function valid_formatter(value,row,index){
			if(value==null||value==""||value=="undefined"){
				return "";
			}
			return value.substr(0,11);
		}
	</script>
</body>
</html>