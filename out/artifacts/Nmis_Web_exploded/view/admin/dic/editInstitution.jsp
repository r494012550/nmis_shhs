<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>
<body>
   <form name="Institutionform" id="Institutionform" method="POST">
     
     <div style="padding:10px 10px 10px 10px;margin-left:auto;margin-right:auto;width:350px;">
		<div style="margin-bottom:10px">
			<input class="easyui-textbox" id="name" label="机构名称:" labelWidth="80"
	        	data-options="prompt:'请输入机构名称...', required:true,missingMessage:'必填'," 
				 name="name" style="width:340px;height:30px;" value="${detail.name}">
	    </div>
		<div style="margin-bottom:10px">
			<input class="easyui-textbox" id="code" label="机构代码:" labelWidth="80"
	        	data-options="prompt:'请输入机构代码...'"
				 name="code" style="width:340px;height:30px;" value="${detail.code}">
	    </div>
	    <div style="margin-bottom:10px">
            <input class="easyui-textbox" id="address" label="机构地址:" labelWidth="80"
                data-options="prompt:'请输入机构地址...'"
                 name="address" style="width:340px;height:30px;" value="${detail.address}">
        </div>
		<div style="margin-bottom:10px" > 
	        <input class="easyui-textbox" id="note" label="备注:" labelWidth="80"
	        	data-options="multiline:true,prompt:'请输入备注...'" 
	        	name="note" style="width:340px;height:60px;" value="${detail.note}">     
		</div>
	   
		<div><input id="id" name="id" type="hidden" value="${id}"/></div>
	</div>

</form>
</body>
</html>