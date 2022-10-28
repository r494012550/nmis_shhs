<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
	<div style="padding:5px 10px 5px 10px;margin-left:auto;margin-right:auto;width:420px;height:450px;">
		<form name="auform" id="auform" method="POST">
			<div style="margin-bottom:5px">
                <input class="easyui-textbox tb" label="${sessionScope.locale.get('admin.authorityname')}：" labelWidth="170" id="auname" 
                	data-options="prompt:'${sessionScope.locale.get('admin.enterauthorityname')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" name="name" style="width:420px;height:30px;" value="${name}">
            </div>
            <div style="margin-bottom:5px">
                <input class="easyui-textbox" id="audesc" label="${sessionScope.locale.get('admin.authoritydesc')}：" labelWidth="170"
                	data-options="prompt:'${sessionScope.locale.get('admin.enterauthoritydesc')}...'" name="description" style="width:420px;height:30px;" value="${description}">
            </div>
            <div style="margin-bottom:5px">
                <input class="easyui-combobox" id="aumodule" label="${sessionScope.locale.get('admin.belongmodule')}：" labelWidth="170"
                	data-options="url:'${ctx}/getModules',prompt:'${sessionScope.locale.get('admin.selectmodule')}...',valueField:'module',textField:'displayname',editable:false,
                	onChange:loadResource,icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }]" name="module" style="width:420px;height:30px;" value="${module}">
            </div>
            <input id="auid" name="id" type="hidden" value="${id}">
            <input id="auids" name="auids" type="hidden" value="${auids}">
            <input type="hidden" name="saveroleToken" value="${saveauthorityToken}"/>
           </form>
           <div style="margin-bottom:5px;height:360px;">
               <table id="resedg" class="easyui-treegrid" style="width:320px;"
					data-options="singleSelect:false,selectOnCheck:true,onLoadSuccess:initresedg,idField:'rid',treeField:'display_${language}',
					checkOnSelect:true,border:true,fit:true,loadMsg:'${sessionScope.locale.get('loading')}...',emptyMsg:'${sessionScope.locale.get('nosearchresults')}...'">
				<thead>
					<tr>
						<th data-options="field:'ck',checkbox:true"></th>
						<th data-options="field:'display_${language}',width:150">${sessionScope.locale.get('admin.resourcename')}</th>
						<th data-options="field:'resource',width:295">${sessionScope.locale.get('admin.resource')}</th>
					</tr>
				</thead>
			</table>
           </div>
	</div>
</body>
</html>