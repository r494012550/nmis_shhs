<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>

</head>	
<body>
	<script type="text/javascript">
        var editIndex = undefined;
        function endEditing(){
            if (editIndex == undefined){return true}
            if ($('#printerdg').datagrid('validateRow', editIndex)){
                $('#printerdg').datagrid('endEdit', editIndex);
                editIndex = undefined;
                return true;
            } else {
                return false;
            }
        }
        function onClickCell_Print(index, field){
            if (editIndex != index){
                if (endEditing()){
                    $('#printerdg').datagrid('selectRow', index)
                            .datagrid('beginEdit', index);
                    var ed = $('#printerdg').datagrid('getEditor', {index:index,field:field});
                    if (ed){
                        ($(ed.target).data('textbox') ? $(ed.target).textbox('textbox') : $(ed.target)).focus();
                    }
                    editIndex = index;
                } else {
                    setTimeout(function(){
                        $('#printerdg').datagrid('selectRow', editIndex);
                    },0);
                }
            }
        }
        function onEndEdit_Print(index, row){
        	//var rows = $('#printerdg').datagrid('getChanges');
        	
            var ed = $(this).datagrid('getEditor', {
                index: index,
                field: 'templatedescription'
            });
        	var templatedescription=$(ed.target).textbox('getText');
        	ed = $(this).datagrid('getEditor', {
                index: index,
                field: 'printername'
            });
        	var printername=$(ed.target).textbox('getText');
        	ed = $(this).datagrid('getEditor', {
                index: index,
                field: 'printerdescription'
            });
        	
        	var printerdescription=$(ed.target).textbox('getText');
        	console.log(templatedescription);
        	console.log(printername);
        	console.log(printerdescription);
        	
        	getJSON('${ctx}/system/savePrinter',
        		{
        			id:row.id,
        			printername:printername,
        			printerdescription:printerdescription,
        			templatedescription:templatedescription
        		},
        		function(data){
        			var json=validationData(data);
			    	if(json.code!=0){
			    		$.messager.show({
			                title:'错误',
			                msg:"保存失败，请重试，如果问题依然存在，请联系系统管理员！",
			                timeout:3000,
			                border:'thin',
			                showType:'slide'
			            });
			    	}
	        });
        	
           // row.productname = $(ed.target).combobox('getText');
        }
       /*  function append(){
            if (endEditing()){
                $('#dg').datagrid('appendRow',{status:'P'});
                editIndex = $('#dg').datagrid('getRows').length-1;
                $('#dg').datagrid('selectRow', editIndex)
                        .datagrid('beginEdit', editIndex);
            }
        }
        function removeit(){
            if (editIndex == undefined){return}
            $('#dg').datagrid('cancelEdit', editIndex)
                    .datagrid('deleteRow', editIndex);
            editIndex = undefined;
        }
        function acceptit(){
            if (endEditing()){
                $('#dg').datagrid('acceptChanges');
            }
        }
        function reject(){
            $('#dg').datagrid('rejectChanges');
            editIndex = undefined;
        } */
        function getChanges(){
            var rows = $('#printerdg').datagrid('getChanges');
            alert(rows.length+' rows are changed!');
        }
        
        function operatecolumn_printname(value, row, index){	
        	return value?value:'默认打印机';
        }
    </script>
	<div class="easyui-panel" data-options="fit:true" style="padding:10px;">
	<header style="color:#8aa4af;">您当前的位置：系统管理   > 打印机</header>
		<table id="printerdg" class="easyui-datagrid" style="width:100%;"
			data-options="showFooter: true,singleSelect:true,fit:true,toolbar:'#toolbar_div_Printer',
				url:'${ctx}/system/getPrinter',autoRowHeight:true,fitColumns:true,
		        view:groupview,
		        groupField:'group',
                groupFormatter:function(value,rows){
                    return value + ' - ' + rows.length + ' 个';
                },
                <shiro:hasPermission name="edit_printer">
                onClickCell:onClickCell_Print,
                </shiro:hasPermission>
                onEndEdit:onEndEdit_Print">
			<thead>
					<tr>
						<th data-options="field:'templatename',width:140">模板名称</th>
						<th data-options="field:'templatedescription',width:140,editor:'textbox'">模板描述</th>
						<th data-options="field:'printername',width:140,editor:'textbox',formatter:operatecolumn_printname">打印机名称</th>
						<th data-options="field:'printerdescription',width:140,editor:'textbox'">打印机描述</th>
						<!-- <th data-options="field:'operate',width:155,align:'center',formatter:operatecolumn_Printer">操作</th> -->
					</tr>
			</thead>
        </table>
        <%-- <div id="cmenu_Printer" class="easyui-menu"  style="padding:2px 2px;">		
        	<shiro:hasPermission name="edit_printer">	
		        <div onclick="new_Printer();">新建</div>
		        <div onclick="ModifyPrinter();">修改</div>		   
		        <div onclick="deletePrinter();">删除</div>	  
		    </shiro:hasPermission>
		 </div> --%>
		 <div id="toolbar_div_Printer" style="padding:2px 5px;text-align:right;">				
			<%-- <shiro:hasPermission name="edit_printer">
				<a class="easyui-linkbutton easyui-tooltip" title="新建打印机" onClick="new_Printer();">新建</a>
		    </shiro:hasPermission> --%>
				<input class="easyui-searchbox" data-options="prompt:'请输入打印机名' ,searcher:doSearchPrinter" style="width:300px;">
		</div>
	</div>
</body>
</html>
