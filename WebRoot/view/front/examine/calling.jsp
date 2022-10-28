<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
<div style="width: 100%; height: 100%;">

    <div class="easyui-layout"  data-options="fit:true,border:false">
        <div data-options="region:'north',border:false" style="padding:5px;height:50px;">
            <div style="margin-bottom:5px;">
                <input class="easyui-textbox" data-options="readonly:true" label="患者姓名：" style="width:260px" value="${patientname}">
            </div>
        </div>
        <div data-options="region:'center',border:false">
            <table id="dicallinginfo_dg" class="easyui-datagrid" border="0"
                data-options="rownumbers: true,singleSelect:true,fit:true,loadMsg:'加载中...',emptyMsg:'没有查找到${modalityname}设备的提示信息...',url:'${ctx}/dic/getDicCalling?modalityname=${modalityname}',
                    onDblClickRow:function(index,row){$(this).datagrid('selectRow',index);diccallinginfo_send();}">
                <thead>
                    <tr>
                        <th data-options="field:'message',width:'100%'" sortable="false">提示信息</th>
                    </tr>
                </thead>
            </table>
        </div>
    </div>

</div>
<input id="examinecallingorcomplete" name="examinecallingorcomplete" value="${examinecallingorcomplete}" type="hidden">
<input id="diccalling_dialogonload" name="diccalling_dialogonload" value='true'  type="hidden">
</body>
</html>
    
