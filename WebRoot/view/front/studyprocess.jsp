<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div style="width: 100%; height: 100%;">

	<div class="easyui-layout"  data-options="fit:true,border:false">
	    <div data-options="region:'north',border:false" style="padding:5px;height:80px;">
		    <div style="margin-bottom:5px;">
		    	<input class="easyui-textbox" data-options="disabled:true" label="患者姓名" style="width:260px" value="${record.patientname}">
		    	<input class="easyui-textbox" data-options="disabled:true" label="检查号" style="width:220px" value="${record.studyid}">
		    </div>
	    	
	    	<div>
	    		<input class="easyui-textbox" data-options="disabled:true" label="检查项目" style="width:484px" value="${record.studyitems}">
	   		</div>
	    </div>
	    <div data-options="region:'center',border:false">
	    	<table id="studyprocess_dg" class="easyui-datagrid" border="0"
				data-options="rownumbers: true,singleSelect:true,fit:true,loadMsg:'加载中...',emptyMsg:'没有查找到检查流程状态...',
					onDblClickRow:function(index,row){$(this).datagrid('selectRow',index);studyprocess_compare($(this),index,row);}">
				<thead>
					<tr>
						<th data-options="field:'statusdisplay',width:100" sortable="false">状态</th>
						<th data-options="field:'operatetime',width:150" sortable="false">时间</th>
						<th data-options="field:'operatorname',width:80" sortable="false">用户姓名</th>
						<th data-options="field:'logmessages',width:180">日志数据</th>
						<td>
					</tr>
				</thead>
			</table>
	    </div>
	</div>
	
	<div id="studyprocess_dialog" class="easyui-dialog" style="width:800px;height:800px;padding:10px 20px;" 
		 modal="true" border="thin" closed="true" buttons="#studyprocessdlg-buttons">
		<div id="mypanel_studyprocesscompare" class="easyui-panel"  style="width:100%;height:100%;padding:10px;"></div>
	</div>
	<div id="studyprocessdlg-buttons">
		<a href="#" class="easyui-linkbutton" onclick="javascript:$('#studyprocess_dialog').dialog('close')">关闭</a>
	</div>
</div>
