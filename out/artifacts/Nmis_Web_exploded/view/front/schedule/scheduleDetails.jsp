<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div class="easyui-layout" data-options="fit:true,border:false" id="layout_schedule">
       <div data-options="region:'west'" style="width:203px;">
       	<div class="easyui-layout" data-options="fit:true,border:false">
	        <div data-options="region:'north',border:false" style="height:202px">
		        <div class="easyui-calendar" style="width:200px;height:200px;" data-options="border:false,onSelect:handleSelect,
		            validator: function(date){
		            	
		                if (date.getTime() >=((new Date()).getTime()-86400000)){
		                    return true;
		                } else {
		                    return false;
		                }
		            }
		            "></div>
	        </div>
	        
	        <div data-options="region:'center'">
		        <div id="modality_list" class="easyui-datalist" title="设备列表"
					data-options="singleSelect:true,fit:true,onClickRow:handleClickRow,onLoadSuccess:handleLoadData,border:false,
					url:'${ctx}/dic/getModalityDic?role=modality',autoRowHeight:true,loadMsg:'加载中...',emptyMsg:'没有查找到设备...',
			        groupField:'type',textField:'modality_name'">
					
				</div>
	        </div>
	   </div>
      </div>
      
      <div data-options="region:'center'">
       	11122
      </div>
	</div>
</body>
</html>