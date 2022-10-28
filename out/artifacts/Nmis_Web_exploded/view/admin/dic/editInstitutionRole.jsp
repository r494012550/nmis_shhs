<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
   <form name="institutionRoleform" id="institutionRoleform" method="POST">
     
     <input type="hidden" name="roleids" id="roleids">
     <input type="hidden" name="id" value="${id }">
     <div style="padding:10px 10px 10px 10px;margin-left:auto;margin-right:auto;">
        
        <div id="institution_role_div" data-options="region:'north',title:'请选择角色所对应的机构',border:false,split:false" style="height:50%;width: 100%;padding:5px 5px;">
            <c:forEach var="arr" items="${roleList}">
                <input class="easyui-checkbox" name="role_module_in" value="${arr[0] }" 
                      label="${arr[1] }" labelAlign="left"
                      data-options="checked:${arr[2] eq '1'?true:false}">&nbsp;&nbsp;&nbsp;&nbsp;
            </c:forEach>
        </div>
    </div>

</form>
</body>
</html>