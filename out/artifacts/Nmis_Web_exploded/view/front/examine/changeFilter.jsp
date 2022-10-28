<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
	<title></title>
</head>
<body>
    <div class="easyui-tabs" style="width:100%;height:100%;" data-options="tabPosition:'left',headerWidth:80">
        <div title="工作状态" style="padding:10px">
            <div style="padding-left:20px;padding-top:10px;">
                   <input id="switchbutton" label="备注:" class="easyui-switchbutton" style="width:100px;height:32px;"
                        data-options="onText:'工作中',offText:'已停用',onChange:setModalityState,checked:${workingState}">
            </div>
            <div style="padding-left:10px;padding-top:10px;">
                  <input id="note" class="easyui-textbox" label="备注:" labelWidth="45" labelAlign="right" style="height:30px;width:300px;" 
    				name="note">	
            </div>
        </div>
        <div title="维护记录" style="padding:10px">
            <table id="modalityRecordDg" class="easyui-datagrid" data-options="fit:true,border:false,scrollbarSize:0,
				loadMsg:'加载中...',emptyMsg:'没有查到维护信息...',url:'${ctx}/examine/findModalityRecord?modalityid=${modalityId}'">
				<thead>
					<tr>
						<th data-options="field:'type',width:50">状态</th>
						<th data-options="field:'name',width:60">操作人</th>
						<th data-options="field:'note',width:200">备注</th>
						<th data-options="field:'createtime',width:150">操作时间</th>
					</tr>
				</thead>
			</table>
        </div>
        <%-- <div title="搜索条件" data-options="" style="padding:10px">
            <form name="passwordform" id="passwordform" method="POST">
			    <div style="width: 100%;">
			        <div style="float:left;margin-top:3px;margin-bottom:3px;margin-left: 10px;">
			            <input class="easyui-datebox" labelAlign="right" style="width: 145px;height:30px;" 
			                name="datefrom"
			                data-options="prompt:'开始时间',value:'2020-6-12'">
			        </div>
			        <div style="float:right;margin-top:3px;margin-bottom:3px;margin-right: 10px;">
			            <input class="easyui-datebox"  labelAlign="right" style="width: 145px;height:30px;" 
			                name="dateto"
			                data-options="prompt:'结束时间',value:'${today}'">
			        </div>
			        <div style="float:right;margin-bottom:10px;margin-top: 60px;margin-right: 10px;">
	                    <a class="easyui-linkbutton" style="width:326px;height:30px;" onclick="modifyPassword();">保存</a>
	                </div>
			    </div>
			</form>
        </div> --%>
    </div>
</body>
</html>