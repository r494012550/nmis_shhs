<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/common/meta.jsp"%>
</head>
<body>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'west',collapsible:false,title:'查询条件',headerCls:'panelHeaderCss_top'" style="width:280px;padding:2px 0px;">
		<header>查询条件</header>
		<div class="easyui-layout" data-options="fit:true,border:false">
			<div data-options="region:'center',border:false" style="padding:0px 5px;">
				
				<form name="searchForm_match" id="searchForm_match" method="POST">
					<div style="width:100%;margin-top:3px;" class="mylabel">
						<select class="easyui-combobox" name="datetype" id="datetype_match" style="width:100%;height:50px;" label="${sessionScope.locale.get("wl.datetime")}:" labelPosition="top" 
							data-options="editable:false,panelHeight:'auto'">
			                <option value="studytime">${sessionScope.locale.get("wl.studytime")}</option>
			            </select>
		            </div>
		
					<div class="easyui-panel" style="width:100%;margin-top:3px;padding:2px;">
						<a id="today_match" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'group_match',selected:true,plain:true" 
							onclick="$('#appdatefrom_match').datebox('setValue','');$('#appdateto_match').datebox('setValue','');$('#appdate_match').val('T');searchStudy_match();">${sessionScope.locale.get("wl.today")}</a>
				        <a  id="yesterday_match" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'group_match',plain:true" 
				        	onclick="$('#appdatefrom_match').datebox('setValue','');$('#appdateto_match').datebox('setValue','');$('#appdate_match').val('Y');searchStudy_match();">${sessionScope.locale.get("wl.yesterday")}</a>
				        <a id="threeday_match" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'group_match',plain:true" 
				        	onclick="$('#appdatefrom_match').datebox('setValue','');$('#appdateto_match').datebox('setValue','');$('#appdate_match').val('TD');searchStudy_match();">近三天</a>
					</div>
					
					<div class="easyui-panel" style="width:100%;margin-top:3px;padding:2px;">
						<a id="fiveday_match" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'group_match',plain:true" 
							onclick="$('#datefrom_match').datebox('setValue','');$('#dateto_match').datebox('setValue','');$('#appdate_match').val('FD');searchStudy_match();">近五天</a>
				        <a  id="week_match" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'group_match',plain:true" 
				        	onclick="$('#datefrom_match').datebox('setValue','');$('#dateto_match').datebox('setValue','');$('#appdate_match').val('W');searchStudy_match();">近一周</a>
				        <a id="month_match" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'group_match',plain:true" 
				        	onclick="$('#datefrom_match').datebox('setValue','');$('#dateto_match').datebox('setValue','');$('#appdate_match').val('M');searchStudy_match();">近一个月</a>
					</div>
		
					<div style="width:100%;margin-top:3px;">
						<div style="width:120px;float:left;">
							<input id="datefrom_match" name="datefrom" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
								onSelect:function(){
									$('#today_match').linkbutton({selected:false});
									$('#yesterday_match').linkbutton({selected:false});
									$('#threeday_match').linkbutton({selected:false});
									$('#fiveday_match').linkbutton({selected:false});
									$('#week_match').linkbutton({selected:false});
									$('#month_match').linkbutton({selected:false});
									$('#appdate_match').val('');
								}">
						</div>
						<div style="width:120px;float:right;">
							<input id="dateto_match" name="dateto" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
								onSelect:function(){
									$('#today_match').linkbutton({selected:false});
									$('#yesterday_match').linkbutton({selected:false});
									$('#threeday_match').linkbutton({selected:false});
									$('#fiveday_match').linkbutton({selected:false});
									$('#week_match').linkbutton({selected:false});
									$('#month_match').linkbutton({selected:false});
									$('#appdate_match').val('');
								}">
						</div>
					</div>
					<div style="width: 100%;margin-top: 3px;" class="mylabel">
						<div style="width:120px;float:left;">
							<input id="patientid_match" name="patientid" class="easyui-textbox" style="width:119px;height:50px;"
							label="病人编号：" labelPosition="top">
						</div>
						<div style="width:120px;float:right;">
							<input id="patientname_match" name="patientname" class="easyui-textbox" style="width:119px;height:50px;"
							label="姓名：" labelPosition="top">
						</div>
					</div>
					
					<div style="width: 100%;margin-top: 3px;" class="mylabel">
						<div style="width:120px;float:left;">
							<input id="accessionnumber_match" name="accessionnumber" class="easyui-textbox" style="width:119px;height:50px;"
							label="检查号：" labelPosition="top">
						</div>
						<div style="width:120px;float:right;">
							<input id="modality_match" name="modality" class="easyui-combobox" style="width:119px;height:50px;" 
								data-options="valueField:'code',textField:'name_zh',multiple:true,
								url:'syscode/getCode?type=0004',editable:false,panelHeight:'120px'"
								label="检查类型：" labelPosition="top">
						</div>
					</div>
										
					<input id="appdate_match" type="hidden" name="appdate" value="T"/>
				</form>
			</div>
			<div data-options="region:'south',collapsible:false,border:false" style="height:70px;padding:0px 5px;">
				
				<div style="margin-top: 5px;">
					<a class="easyui-linkbutton" onclick="searchStudy_match()" style="width:100%;">查询</a>
				</div>
				<div style="margin-top: 5px;">
						<a class="easyui-linkbutton c2" onclick="clearSearch_match()" style="width:100%;">条件清除</a>
				</div>
			</div>
		</div>
	</div>
	<div data-options="region:'center',border:false" >
		<table id="unmatchstudydg_match" class="easyui-datagrid"  border="0"
			data-options="rownumbers: true,showFooter: true,singleSelect:true,remoteSort:false,nowrap:false,
			toolbar:'#unmatchstudy_tool_match',fit:true,border:false,pagination:true,
			loadMsg:'加载中...',emptyMsg:'没有查找到匹配信息...',
	        onRowContextMenu: function(e,index ,row){
	            e.preventDefault();
	            $(this).datagrid('selectRow',index);
	            $('#unmatchstudy_menu_match').menu('show', {
	                left:e.pageX,
	                top:e.pageY
	            });
	        }
		">
			<thead>
				<tr>
					<th data-options="field:'patientid',width:80,align:'center'">病人编号</th>		
					<th data-options="field:'patientname',width:180,align:'center'">病人姓名</th>
					<th data-options="field:'sexdisplay',width:50,align:'center'">性别</th>
					<th data-options="field:'birthdate',width:90,align:'center'">出生日期</th>
					<th data-options="field:'accessionnumber',width:80,align:'center'">检查号</th>
					<th data-options="field:'studydescription',width:200,align:'center'" sortable="true">检查描述</th>
					<th data-options="field:'modality',width:65,align:'center'" sortable="true">设备类型</th>
					<!-- <th data-options="field:'modalityname',width:120" sortable="true">检查设备</th> -->
					<th data-options="field:'studydatetime',width:150,align:'center'" sortable="true">检查日期</th>
					<th data-options="field:'statusdisplay',width:80">状态</th>
					<th data-options="field:'numberofimages',width:80">图像数量</th>
					<th data-options="field:'matchflagdisplay',width:80">是否匹配</th>
				</tr>
			</thead>
		</table>
		
	    <div id="unmatchstudy_tool_match" style="padding:2px 5px;">
	    	<a href="#" class="easyui-linkbutton" plain="true" onClick="beforeToRegister_match()"><i class="iconfont icon-edit"></i>&nbsp;登记</a>
	    	<a href="#" class="easyui-linkbutton" plain="true" onClick="correctAccessionNo_match()"><i class="iconfont icon-edit"></i>&nbsp;更改检查号</a>
	    	<a href="#" class="easyui-linkbutton" plain="true" onClick="deleteUnmatchstudy_match()"><i class="iconfont icon-delete"></i>&nbsp;删除</a>
	    </div>
		
	    <div id="unmatchstudy_menu_match" class="easyui-menu" style="width:120px">
	    	<div onclick="beforeToRegister_match();">登记</div>	
	    	<div onclick="correctAccessionNo_match();">更改检查号</div>
	    	<div onclick="deleteUnmatchstudy_match();">删除</div>	   
	    </div>
	</div>

</div>

<div id="correctAccessNumDlg_match" class="easyui-dialog"  closed="true">
	<div style="margin-top:8px;margin-left:4px">
		<input id="accessNum_match" name="accessionnumber" class="easyui-textbox" label="检查号：" labelWidth="80px" labelAlign="right" style="height:25px;width:220px;">
	</div>
</div>
	
</body>
</html>