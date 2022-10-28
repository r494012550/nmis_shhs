<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
<div class="easyui-tabs" style="width:100%;height:100%;" data-options="tabPosition:'top',headerWidth:80,plain:true,narrow:true,justified:false,border:false">
        <div title="我的排班表" style="padding:10px" data-options="href:'${ctx}/worklist/showDutyRosterDetails?postcode=D'">

        </div>
        <div title="医生排班表" style="padding:10px" data-options="href:'${ctx}/worklist/showDutyRosterDetails?postcode=D'">

        </div>
        <div title="技师排班表" style="padding:10px" data-options="href:'${ctx}/worklist/showDutyRosterDetails?postcode=T'">
        	
        </div>
        <div title="护士排班表" style="padding:10px" data-options="href:'${ctx}/worklist/showDutyRosterDetails?postcode=N'">
        	
        </div>
        
        <div title="登记员排班表" data-options="href:'${ctx}/worklist/showDutyRosterDetails?postcode=REG'" style="padding:10px">
            
        </div>
    </div>
</body>
</html>