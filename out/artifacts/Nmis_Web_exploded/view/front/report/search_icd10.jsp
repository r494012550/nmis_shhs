<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<div class="easyui-layout" style="width:100%;height:100%;">
        <div data-options="region:'north'" style="height:50px">
            <div style="margin-top: 10px;margin-left: 20px;">
                <input id="icdCode_${reportid}" name="icdCode" class="easyui-textbox" style="width:219px;" label="疾病编码:">
                <input id="icdName_${reportid}" name="icdName" class="easyui-textbox" style="width:219px;" label="疾病名称:">
                <a class="easyui-linkbutton" onclick="findDic10(${reportid});" style="width: 80px;height: 29px;">查询</a>
            </div>
        </div>
        <div data-options="region:'west',split:true" style="width:240px;">
            <ul id="nodetree_${reportid}" class="easyui-tree" data-options="url:'${ctx}/report/getIcd10Tree',method:'get',
                    lines:false,checkbox:false,onlyLeafCheck:true,
                    onClick:function(node){
                        checkDic10(node,${reportid});
                    }">
            </ul>
        </div>
        <div data-options="region:'center',iconCls:'icon-ok'">
            <table id="icd10dg" class="easyui-datagrid"
                    data-options="url:'',method:'get',border:false,singleSelect:false,fit:true,
                    emptyMsg:'${sessionScope.locale.get('nosearchresults')}',fitColumns:true">
                <thead>
                    <tr>
                        <th data-options="field:'icd_code'" width="80">疾病编码</th>
                        <th data-options="field:'icd_name'" width="100">疾病名称</th>
                        <th data-options="field:'icd_xbxz'" width="80">性别限制</th>
                        <th data-options="field:'icd_lxxz'" width="80">疗效限制</th>
                    </tr>
                </thead>
            </table>
        </div>
    </div>
	<script type="text/javascript">
		var reportid = '${reportid}';
		setTimeout("find()", 300);
		function find() {
	        $('#icdCode_' + reportid).textbox('textbox').bind('keydown', function(e){
	            if (e.keyCode == 13) {   // when press ENTER key, accept the inputed value.
	                findDic10(reportid);
	            }
	        });
	        $('#icdName_' + reportid).textbox('textbox').bind('keydown', function(e){
	            if (e.keyCode == 13) {   // when press ENTER key, accept the inputed value.
	                findDic10(reportid);
	            }
	        });
		}
	</script>
</body>
</html>