<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<div style="padding: 15px;">
	<%-- <div style="margin-bottom:10px">
    	<input class="easyui-textbox" label="检查方法：" labelPosition="top" style="width:100%;" value="${studymethod}" readonly>
    </div> --%>
    <div style="margin-bottom:10px">
    	<div  class="easyui-panel" style="width:100%;height:350px;padding:5px;" data-options="">
		    <p><b>【${sessionScope.locale.get('report.studymethod')}】</b></p>
		    <p>${studymethod}</p>
		    <p><b>【${sessionScope.locale.get('wl.reportdesc')}】</b></p>
		    <p>${desccontent_html}</p>
		    <p><b>【${sessionScope.locale.get('wl.reportresult')}】</b></p>
		    <p>${resultcontent_html}</p>
		</div>
    
    </div>
</div>