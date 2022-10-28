<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>登记</title>
</head>
<body>
	<table id="sameNamedg_reg" class="easyui-datagrid" title="" style="width:100%;"
		data-options="singleSelect: true,fit:true,fitColumns:true,
		onDblClickRow:function(index,row){samePatient(row);}">
        <thead>
            <tr>
            	<th data-options="field:'patientid',width:80">病人编号</th>
            	<th data-options="field:'patientname',width:60">姓名</th>
                <th data-options="field:'sexdisplay',width:50">性别</th>
                <th data-options="field:'birthdate',width:80">出生日期</th>
                <th data-options="field:'idnumber',width:80">身份证号</th>
                <th data-options="field:'telephone',width:60">联系电话</th>
                <th data-options="field:'address',width:60">联系地址</th>
            </tr>
        </thead>
    </table>
<script type="text/javascript">
$('#sameNamedg_reg').datagrid({
    view: detailview,
    detailFormatter:function(index,row){
        return '<div style="padding:2px;position:relative;"><table class="ddv"></table></div>';
    },
    onExpandRow: function(index,row){
        var ddv = $(this).datagrid('getRowDetail',index).find('table.ddv');
        ddv.datagrid({
            url: window.localStorage.ctx+'/register/getStudyDetail?patientkid='+row.id,
            onDblClickRow:function(index,row){extractInfo(row);$('#common_dialog').dialog('close');},
            singleSelect:true,
            fitColumns:true,
            emptyMsg:'无检查记录...',
            height:'auto',
            columns:[[
            	{field:'admissionid',title:'就诊编号',width:80},
            	{field:'studyid',title:'检查编号',width:80},
            	{field:'cardno',title:'卡号',width:80},
            	{field:'regdatetime',title:'登记日期',width:120},
            	{field:'studyitems',title:'检查项目',width:100},
                {field:'modality_type',title:'类型',width:60},
                {field:'studydatetime',title:'检查日期',width:120},
                
                
            ]],
            onResize:function(){
                $('#sameNamedg_reg').datagrid('fixDetailRowHeight',index);
            },
            onLoadSuccess:function(){
                setTimeout(function(){
                    $('#sameNamedg_reg').datagrid('fixDetailRowHeight',index);
                },0);
            }
        });
        $('#sameNamedg_reg').datagrid('fixDetailRowHeight',index);
    }
});
</script>
</body>
</html>