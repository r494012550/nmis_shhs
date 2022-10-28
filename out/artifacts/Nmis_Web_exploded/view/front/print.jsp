<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
  <head>
  	<%@ include file="/common/meta.jsp"%>
  	<%@ include file="/common/basecss.jsp"%>
  	<script type="text/javascript" src="${ctx}/js/easyui/jquery.min.js?v=${vs}"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jquery.easyui.min.js?v=${vs}"></script>
    <title>报告预览</title>
    <style type="text/css">
		/* .over{
		    width:100%;
		    height: 5000px;
		    background-color: rgba(1,1,1,0);
		    position: fixed;
		    top:0px;
		    left:0px;
 
		} */
	</style>
  </head>
  <body>
	  <p class="over"></p>
	  <c:if test="${select_fontsize}">
		  <div class="easyui-panel" style="width:100%;height:35px;padding:2px;" data-options="border:false">
			<select class="easyui-combobox" id="desc_fontsize" style="width:200px;height:28px;" labelAlign="right" labelWidth="120" label="描述字体大小："
				data-options="value:'12',editable:false,panelHeight:'auto',onChange:function(newValue,oldValue){changeFontSize()}">
				<option value="9">9</option>
				<option value="10">10</option>
		        <option value="11">11</option>
		        <option value="12">12</option>
		        <option value="14">14</option>
		        <option value="16">16</option>
		        <option value="18">18</option>
		     </select>
		     <select class="easyui-combobox" id="res_fontsize" style="width:200px;height:28px;" labelAlign="right" labelWidth="120" label="诊断字体大小："
				data-options="value:'12',editable:false,panelHeight:'auto',onChange:function(newValue,oldValue){changeFontSize()}">
				<option value="9">9</option>
				<option value="10">10</option>
		        <option value="11">11</option>
		        <option value="12">12</option>
		        <option value="14">14</option>
		        <option value="16">16</option>
		        <option value="18">18</option>
			 </select>
		  </div>
	  </c:if>
	  <iframe id="reportFrame" width="100%" height="5000px" src="${ctx}/print/preview?reportid=${reportid}&issr=${issr}&fontSize=${fontSize}" 
	  	frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="no" allowtransparency="yes"></iframe>
	  <input id="basesrc" type="hidden" value="${ctx}/print/preview?reportid=${reportid}">
	  <input id="descname" type="hidden" value="${desc_fontsize_name}">
	  <input id="resname" type="hidden" value="${res_fontsize_name}">
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
	document.oncontextmenu = function () {
		return false;
	};
  
	$(document).ready(function(){
		//$("#reportFrame")[0].contentWindow.print();
		//window.close();
	});
	
	var beforePrint = function() {
	     //console.log('Functionality to run before printing.');
	};
	 
	var afterPrint = function() {
	     //console.log('Functionality to run after printing');
	};
	 
	if (window.matchMedia) {
		var mediaQueryList = window.matchMedia('print');
		mediaQueryList.addListener(function(mql) {
			if (mql.matches) {
	        	beforePrint();
			} else {
				afterPrint();
			}
		});
	}
	window.onbeforeprint = beforePrint;
	window.onafterprint = afterPrint;
	function changeFontSize(){
		var src=$("#basesrc").val();
		src=src+"&"+$('#descname').val()+"="+$('#desc_fontsize').combobox('getValue')+"&"+$('#resname').val()+"="+$('#res_fontsize').combobox('getValue')
		console.log(src)
		$("#reportFrame").attr('src',src);
	}
</script>
</html>
