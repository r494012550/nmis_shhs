<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
		    <div style="width:445px;height:550px;padding:5px 15px;">
			    
			    <div class="easyui-layout" data-options="fit:true" border="0" style="height: 100%;">
			        <div data-options="region:'north',split:false" style="height:50px;padding:4px 0px;" border="0">
			        	<form name="roleform" id="roleform" method="POST">
							<div>
				                <input class="easyui-textbox tb" id="rolename" style="width:380px;height:30px;" name="rolename" label="${sessionScope.locale.get('admin.rolename')}：" labelWidth="100"
				                	data-options="prompt:'${sessionScope.locale.get('admin.inputrolename')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" value="${rolename}">
				            </div>
				            <input id="roleid" name="id" type="hidden" value="${id}">
				            <input id="roleids" name="roleids" type="hidden" value="">
				            <input id="modulesids" name="modulesids" type="hidden" value="">
				            <input id="institutionids" name="institutionids" type="hidden" value="">
				            <input type="hidden" name="saveroleToken" value="${saveroleToken}"/>
			            </form>
			        </div>
			        
			        
			       <div data-options="region:'south',title:'${sessionScope.locale.get('admin.selectthepermissionsthattherolehas')}',split:false,border:true" style="height:240px;" border="0">
			        	<table id="audg_role" class="easyui-datagrid"
							data-options="url:'${ctx}/getAuthorityByRoleId?roleid=${roleid}',onLoadSuccess:initaudg_role,
								singleSelect:false,selectOnCheck:true,checkOnSelect:true,border:false,fit:true,
								loadMsg:'${sessionScope.locale.get('loading')}...',
								emptyMsg:'${sessionScope.locale.get('nosearchresults')}...',
						        view:groupview,
								groupField:'modulename',
						        groupFormatter:function(value,rows){
				                    return value + ' - ' + rows.length + ' ${sessionScope.locale.get('admin.items')}';
				                }">
							<thead>
								<tr>
									<th data-options="field:'ck',checkbox:true"></th>
									<th data-options="field:'name'">${sessionScope.locale.get('admin.authorityname')}</th>
									<th data-options="field:'description',width:200">${sessionScope.locale.get('admin.authoritydesc')}</th>
								</tr>
							</thead>
						</table>
			        </div>
			        
			        <div data-options="region:'center'" style="height:300px;width: 100%">
			             <div class="easyui-layout" data-options="fit:true">
                            <div id="institution_div" data-options="region:'north',title:'请选择角色所对应的机构',border:false,split:false" style="height:50%;width: 100%;padding:5px 15px;">
                                <c:forEach var="arr" items="${institutionList}">
                                    <input class="easyui-checkbox" name="role_module_in" value="${arr[0] }" labelWidth="360px;"
                                    label="${arr[1] }" labelAlign="left" labelPosition="after"
                                    data-options="checked:${arr[2] eq '1'?true:false}"><br/>
                                </c:forEach>
                            </div>
                            <div data-options="region:'center',title:'${sessionScope.locale.get('admin.selectmodulebolongtorole')}',href:'${ctx}/getModulesByRoleId?roleid=${roleid}',border:false"></div>
                         </div>
			        </div>
			        
			    </div>

		</div>
</body>
</html>