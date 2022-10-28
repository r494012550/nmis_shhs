<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
	<head>
	  	<%@ include file="/common/meta.jsp"%>
	    <title>图像预览</title>
	</head>
	<body>
		<iframe id="imagesFrame" width="100%" height="100%" src="${ctx}/print/preview?printtempname=${printtempname}&printTempType=${printTempType}&reportid=${reportid}&orderid=${orderid}&studyid=${studyid}&imgid=${imgid}&imgids=${imgids}" 
			frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="no" allowtransparency="yes" onload="load(this)">
		</iframe>
	</body>
<script type="text/javascript">
	/* $(document).ready(function(){
		$("#imagesFrame")[0].contentWindow.print();
		//window.close();
	}); */
	
	function load(frame){
		//frame.contentWindow.print();
	}

</script>
</html>
