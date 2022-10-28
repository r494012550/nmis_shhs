<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- 保存查询条件 -->
<div style="margin-top: 20px;margin-left: 10px;">
	${sessionScope.locale.get("filter.filtername")}：<input class="easyui-textbox" maxlength="20" id="condition" style="height: 30px;width:220px;" 
		data-options="prompt:'${sessionScope.locale.get("filter.inputfilterdname")}...',required:true,missingMessage:'${sessionScope.locale.get("required")}'">
</div>
