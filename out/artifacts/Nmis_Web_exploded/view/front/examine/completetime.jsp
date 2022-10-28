<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title></title>
</head>
<body>
<div style="margin-top: 40px;margin-left: 22px;">
	检查完成时间：<input class="easyui-datetimespinner"  id="sptime" style="width:200px;"
		 data-options="
		 	showSeconds: true,
			icons:[{
				iconCls:'icon-clear',
				handler: function(e){
					$(e.data.target).datetimespinner('clear');
				}
			}]
			">

</div>
<script type="text/javascript">

</script>
</body>
</html>