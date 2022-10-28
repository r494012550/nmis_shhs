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
	<div title="完成时间" style="padding:10px">
		<div style="width:470px;margin-left:auto;margin-right:auto;">
		<table style="width:450px;" cellspacing="0">
		<tr>
			<td>
				当前检查状态：${state}
			</td>
		</tr>
		<tr>
			<td>
				检查完成时间：<input class="easyui-datetimespinner"  id="sptime2" value="${completeTime}" style="width:200px;"
					 data-options="showSeconds: true,icons:[{iconCls:'icon-clear',handler: function(e){
									$(e.data.target).datetimespinner('clear');
								}
							}]">
			</td>
		</tr>
		<tr>
			<td>
				<a class="easyui-linkbutton" onclick="modifyStudyorder(${orderid});" style="width:120px;">保存修改</a>
			</td>
		</tr>
		</table>
		</div>
	</div>
	<div title="检查耗材" style="padding:10px">
		<form id="saveAppForm_mod" name="saveAppForm" method="POST">
		<div>
		<input id="id_mod" name="study.id" type="hidden" value="${study.id}"/>
		<input id="patientfk_mod" name="study.patientfk" type="hidden" value="${study.patientfk}"/>
		<input id="studyorderfk_mod" name="study.studyorderfk" type="hidden" value="${study.studyorderfk}">
		<input id="studyid_mod" name="study.studyid" type="hidden" value="${study.studyid}"/>
		<input id="modality_mod" name="study.modality" type="hidden" value="${study.modality}"/>
		</div>
		<div style="width:470px;margin-left:auto;margin-right:auto;">
		<table style="width:450px;" cellspacing="0">
			<tr>
                <td>
                    <input class="easyui-datetimebox" label="上机时间：" labelAlign="right" 
                        style="width:95%;height:28px;" value="">
                </td>
                <td>
                    <input class="easyui-combobox" style="width:220px;height:28px;" label="污染情况：" labelAlign="right"
                     data-options="url:'${ctx}/dic/findDicCommonFromCache?group=pollution',valueField:'id',textField:'name',
                        icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }],
                        editable:false,panelHeight:'auto',panelMaxHeight:'300',value:''">
                </td>
            </tr>
                           
            <tr>
                <td>
                    <input class="easyui-combobox" style="width:95%;height:28px;" label="延迟采集：" labelAlign="right"
                     data-options="url:'${ctx}/dic/findDicCommonFromCache?group=delayedacquisition',valueField:'id',textField:'name',
                        icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }],
                        editable:false,panelHeight:'auto',panelMaxHeight:'300',value:''">
                </td>
                <td>
                    <input class="easyui-datetimebox" labelAlign="right"
                     style="width:220px;height:28px;" value="">
                </td>
            </tr>
                            
            <tr>
                <td>
                    <input  class="easyui-combobox" style="width:95%;height:28px;" label="延迟采集2：" labelAlign="right"
                     data-options="url:'${ctx}/dic/findDicCommonFromCache?group=delayedacquisition',valueField:'id',textField:'name',
                        icons:[{iconCls:'icon-clear',handler: function(e){ $(e.data.target).combobox('clear'); } }],
                        editable:false,panelHeight:'auto',panelMaxHeight:'300',value:''">
                </td>
                <td>
                    <input class="easyui-datetimebox"  labelAlign="right"
                     style="width:220px;height:28px;" value="">
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <input class="easyui-textbox" style="width:97%;height:28px;" label="备注：" labelAlign="right"
                        data-options="multiline:true" value="">
                </td>
            </tr>
            <tr>
				<td colspan="2" align="center">
					<a class="easyui-linkbutton" onclick="saveApp_mod();" style="width:120px;">保存检查耗材</a>
				</td>
     	    </tr>
		</table>
		</div>
	</form>
	</div>	
</div>

<script type="text/javascript">

</script>
</body>
</html>