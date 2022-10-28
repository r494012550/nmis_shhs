<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
  <head>
  	<%@ include file="/common/meta.jsp"%>
    <title>表单预览</title>
  </head>
  <body>
  <iframe id="formdataFrame" width="100%" height="100%" src="${ctx}/research/preview?dataid=${dataid}&fontSize=${fontSize}" frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="no" allowtransparency="yes">

  </iframe>
  </body>

<script type="text/javascript">
  /*var studyid = "${studyid}";
  var printtempname = "${printtempname}"
  function getSearch() {
    var path = "${pageContext.request.contextPath}/print/preview?printtempname=" + printtempname+"&issr="+${issr};
   	if(studyid)
   		path = path + "&studyid=" + studyid
    $("#reportFrame").attr("src", path);
  }
  getSearch();*/
  
  
	$(document).ready(function(){
		$("#formdataFrame")[0].contentWindow.print();
		//window.close();
	});

</script>
</html>
