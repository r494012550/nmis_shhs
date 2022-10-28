<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
<div data-options="region:'center',border:false" style="padding:2px 5px;">
<form id="fulltextSearchForm" method="post">
    
    <div style="width:100%;margin-top:3px;">
        <input class="easyui-textbox" id="plaintext" name="plaintext" label="报告内容:" labelPosition="top" style="width:100%;height:56px;">
    </div>
    <div style="width:100%;margin-top:3px;">
    	<!-- <select class="easyui-combobox" name="fulltext_search_type" id="fulltext_search_type" style="width:100%;height:56px;" label="查询类型:" labelPosition="top" 
            data-options="editable:false,panelHeight:'auto'">
                 <option value="contains">精确查询</option>
                 <option value="freetext">模糊查询</option>
                 <option value="all">全部</option>
        </select> -->
        <input class="easyui-radiobutton" data-options="checked:true" name="type_fulltext" value="contians" label="精确" labelWidth="60" labelPosition="after">
        <input class="easyui-radiobutton" name="type_fulltext" value="freetext" label="模糊" labelWidth="60" labelPosition="after">
        <!-- <input class="easyui-radiobutton" name="type_fulltext" value="all" label="全部" labelWidth="60" labelPosition="after"> -->
    </div>
    <div style="width:100%;margin-top:3px;">
        <input class="easyui-combobox" name="modality" id="modality3" style="width:100%;height:56px;" label="${sessionScope.locale.get("wl.modality")}:" labelPosition="top" 
            data-options="valueField:'code',textField:'name_zh',multiple:true,editable:false,
            url:'${ctx}/syscode/getCode?type=0004',panelHeight:'200px'"/>
    </div>

    <div style="width:100%;margin-top:3px;">
        <select class="easyui-combobox" name="datetype" id="datetype3" style="width:100%;height:56px;" label="${sessionScope.locale.get("wl.datetime")}:" labelPosition="top" 
            data-options="editable:false,panelHeight:'auto'">
                 <option value="reporttime">报告日期</option>
                 <option value="audittime">审核时间</option>
                 <option value="studytime">${sessionScope.locale.get("wl.studytime")}</option>
                 <option value="registertime">${sessionScope.locale.get("wl.registertime")}</option>
                 <%-- <option value="appointmenttime">${sessionScope.locale.get("wl.appointmenttime")}</option> --%>
        </select>
    </div>
    
    <div class="easyui-panel" style="width:100%;margin-top:3px;padding:2px;">
            <a id="today3" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g3',plain:true" 
                onclick="$('#datefrom3').datebox('setValue','');$('#dateto3').datebox('setValue','');$('#appdate3').val('T');fulltextSearch();">${sessionScope.locale.get("wl.today")}</a>
            <a  id="yesterday3" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g3',plain:true" 
                onclick="$('#datefrom3').datebox('setValue','');$('#dateto3').datebox('setValue','');$('#appdate3').val('Y');fulltextSearch();">${sessionScope.locale.get("wl.yesterday")}</a>
            <a id="threeday3" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g3',selected:true,plain:true" 
                onclick="$('#datefrom3').datebox('setValue','');$('#dateto3').datebox('setValue','');$('#appdate3').val('TD');fulltextSearch();">近三天</a>
    </div>
                    
    <div class="easyui-panel" style="width:100%;margin-top:3px;padding:2px;">
        <a id="fiveday3" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g3',plain:true" 
            onclick="$('#datefrom3').datebox('setValue','');$('#dateto3').datebox('setValue','');$('#appdate3').val('FD');fulltextSearch();">近五天</a>
        <a  id="week3" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g3',plain:true" 
            onclick="$('#datefrom3').datebox('setValue','');$('#dateto3').datebox('setValue','');$('#appdate3').val('W');fulltextSearch();">近一周</a>
        <a id="month3" class="easyui-linkbutton" style="width:77px;height:26px;" data-options="toggle:true,group:'g3',plain:true" 
            onclick="$('#datefrom3').datebox('setValue','');$('#dateto3').datebox('setValue','');$('#appdate3').val('TM');fulltextSearch();">近三个月</a>
    </div>
                    
    <div style="width:100%;margin-top:3px;">
       <div style="width:120px;float:left;">
           <input id="datefrom3" name="datefrom" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
            onSelect:function(){
                $('#today3').linkbutton({selected:false});
                $('#yesterday3').linkbutton({selected:false});
                $('#threeday3').linkbutton({selected:false});
                $('#fiveday3').linkbutton({selected:false});
                $('#week3').linkbutton({selected:false});
                $('#month3').linkbutton({selected:false});
                $('#appdate3').val('');
            }">
       </div>
        <div style="width:120px;float:right;">
          <input id="dateto3" name="dateto" class="easyui-datebox" style="width: 120px;height:26px;" data-options="
            onSelect:function(){
                $('#today3').linkbutton({selected:false});
                $('#yesterday3').linkbutton({selected:false});
                $('#threeday3').linkbutton({selected:false});
                $('#fiveday3').linkbutton({selected:false});
                $('#week3').linkbutton({selected:false});
                $('#month3').linkbutton({selected:false});
                $('#appdate3').val('');
            }">
        </div>
    </div>
    <div style="width:100%;margin-top:3px;">
    	<div class="easyui-panel" data-options="fit:true,border:false" style="height:50px;padding:3px;">
		    <p style="font-size:13px;font-weight:bold">注意：</p>
		    <ul style="font-size:12px">
		      <li>一、 精确查询：请输入单词或短语。比如“肿瘤”、“癌症”。</li>
			  <li>1 未见 异常：空格隔开，指报告内容既包含“未见”又包含“异常”。</li>
			  <li>2 恶性,肿瘤：用“,”隔开，指报告内容包含“恶性”或者包含“肿瘤”。</li>
			  <li>二、模糊查询：可以输入一段文本查询。比如“左侧胸壁未见肿块”，查询结果不精确。</li>
			</ul>
		</div>
    	
	</div>
    <input id="appdate3" type="hidden" name="appdate" value="TD"/>
</form>
</div>

<div data-options="region:'south',hideCollapsedContent:false,border:false" style="height:70px;padding:2px 5px;">
    <div style="margin-top: 5px;">
        <a class="easyui-linkbutton" onclick="fulltextSearch();" style="width:100%;height:28px;margin-top: 5px;">${sessionScope.locale.get("wl.dosearch")}</a>
    </div>
    <div style="margin-top: 5px;">
        <a class="easyui-linkbutton c2" onclick="clearFulltextSearch();" style="width:100%;height:28px">${sessionScope.locale.get("wl.clearcondition")}</a>
    </div>
</div>
</body>
</html>