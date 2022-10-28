<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div class="easyui-layout"  data-options="fit:true,border:false">
        <div data-options="region:'north',border:false" style="height:140px;padding:5px;" id="north_formula">
            <script id="ue_formula" type="text/plain" name="template_html" style="width:780px;height:100px;margin-left:auto;margin-right:auto;"></script>
            <div style="margin-top:5px">
            	<span>&nbsp;&nbsp;运算符：+(加法)，-(减法)，*(乘法)，/(除法)  </span>&nbsp;&nbsp;&nbsp;&nbsp;
            <a href="javascript:void(0)" class="easyui-tooltip" data-options="
                    hideEvent: 'none',
                    showEvent: 'click',
                    content: function(){
                        return $('#toolbar_more');
                    },
                    onShow: function(){
                        var t = $(this);
                        t.tooltip('tip').focus().unbind().bind('blur',function(){
                            t.tooltip('hide');
                        });
                    }
                ">(更多)</a>
            </div>
            
            <div style="display:none">
		        <div id="toolbar_more">
		            <table style="width:500px;border-style:solid;border-color:#d4d4d4;" border='1'>
						<tbody>
						<tr>
							<th style="width:30%;height:30px;background-color:#daeef5;">运算符</th>
							<th style="height:30px;background-color:#daeef5;">描述</th>
						</tr>
						<tr>
							<td style="height:25px;">&nbsp;&nbsp;+</td>
							<td>&nbsp;&nbsp;加法&nbsp;&nbsp;&nbsp;&nbsp;1+2</td>
						</tr>
						<tr>
							<td style="height:25px;">&nbsp;&nbsp;-</td>
							<td>&nbsp;&nbsp;减法&nbsp;&nbsp;&nbsp;&nbsp;2-1</td>
						</tr>
						<tr>
							<td style="height:25px;">&nbsp;&nbsp;*</td>
							<td>&nbsp;&nbsp;乘法&nbsp;&nbsp;&nbsp;&nbsp;1*2</td>
						</tr>
						<tr>
							<td style="height:25px;">&nbsp;&nbsp;/</td>
							<td>&nbsp;&nbsp;除法&nbsp;&nbsp;&nbsp;&nbsp;1/2</td>
						</tr>
						<tr>
							<th style="width:30%;height:30px;background-color:#daeef5;">函数</th>
							<th style="height:30px;background-color:#daeef5;">描述</th>
						</tr>
						<tr>
							<td style="height:25px;">&nbsp;&nbsp;min(x,y)</td>
							<td>&nbsp;&nbsp;返回 x 和 y 中的最低值，参数可0或多个。</td>
						</tr>
						<tr>
							<td style="height:25px;">&nbsp;&nbsp;max(x,y)</td>
							<td>&nbsp;&nbsp;返回 x 和 y 中的最高值，参数可0或多个。</td>
						</tr>
						<tr>
							<td style="height:25px;">&nbsp;&nbsp;pow(x,y)</td>
							<td>&nbsp;&nbsp;返回 x 的 y 次幂。参数x是底数,必需，y是幂数,必需。全部必须是数字。</td>
						</tr>
						<tr>
							<td style="height:25px;">&nbsp;&nbsp;sqrt(x)</td>
							<td>&nbsp;&nbsp;返回数的平方根。参数必需，必须是大于等于 0 的数。</td>
						</tr>
						<tr>
							<td style="height:25px;">&nbsp;&nbsp;sin(x)</td>
							<td>&nbsp;&nbsp;返回数的正弦。参数必需，一个以弧度表示的角。将角度乘以 0.017453293 （2PI/360）即可转换为弧度。</td>
						</tr>
						</tbody>
					</table>
		        </div>
		    </div>
        </div>
        <div data-options="region:'center',title:'请选择数字输入框',border:false">
        	<div id="formula_content" class="easyui-panel mydiv" data-options="fit:true,border:false" style="padding:5px 30px;">
    						
 			</div>
        </div>
    </div>
</body>
</html>