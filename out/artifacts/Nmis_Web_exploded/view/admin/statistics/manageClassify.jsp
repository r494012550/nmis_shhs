<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
    <table class="easyui-datagrid" id="classify_list" title="分类列表" style="width:300px;height:200px" data-options="
            url:'${ctx}/statistics/getStatisticalClassify',rownumbers: true,fit:true,fitColumns:true,singleSelect:true
           ">
           <thead>
			<tr>
				<th data-options="field:'name',width:100">名称</th>
				<th data-options="field:'createtime',width:100">创建时间</th>
			</tr>
		</thead>
    </table>
</body>
</html>