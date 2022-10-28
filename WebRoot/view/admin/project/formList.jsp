<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div style="padding:10px;">
		<div style="margin-bottom:10px">
			<input class="easyui-searchbox"  data-options="prompt:'请输入表单名称...',searcher:searchSRTemp" style="width:553px;height:30px;">
	    </div>
	    <div style="margin-bottom:5px">
	    	<table id="tmp_dg" class="easyui-datagrid" align="center" style="width:553px;height:368px;"
			    		data-options="singleSelect:true,onDblClickRow:openResearchForm,url:'${ctx}/research/findResearchForm?withContent=false',
			    		scrollbarSize:0">
			    		
			    <thead>
					<tr>
						<th data-options="field:'name',width:'50%'" sortable="true">名称</th>
						<th data-options="field:'creatorname',width:'20%'" sortable="true">创建人</th> 
						<th data-options="field:'createtime',width:'30%'" sortable="true">创建时间</th>
					</tr>
				</thead>
			</table>
		</div>
		<div style="margin-bottom:0px">
			<form>
				<input class="easyui-radiobutton" name="opentemp" id="open_only_temp" value="only_temp" label="仅打开表单" labelWidth="100" labelAlign="right"
					data-options="checked:true">
				<input class="easyui-radiobutton" name="opentemp" id="open_temp_datamodel" value="temp_datamodel" label="打开表单和数据结构" labelWidth="150" labelAlign="right">
			</form>
	    </div>
	</div>
</body>
</html>