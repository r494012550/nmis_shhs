<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<div style="padding:10px 30px 5px 30px;margin-left:auto;margin-right:auto;">
		<div style="margin-bottom:5px">
		 	<input id='srtemplatename_save'  class='easyui-textbox tb' label="模板名称：" data-options="labelPosition:'before',prompt:'请输入模板名称...',required:true,missingMessage:'必填'"
				style="width:400px;height:30px;" value="${template.name}"/>
        </div>
        <div style="margin-bottom:5px">
           <input id='maprule_save'  class='easyui-textbox tb' label="映射规则：" data-options="labelPosition:'before',prompt:'请输入映射规则...',required:true,missingMessage:'必填'"
				style="width:400px;height:30px;" value="${template.maprule}"/>
        </div>
        <div style="margin-bottom:5px">
        	<b>映射规则说明</b>
        </div>
        <div style="margin-bottom:10px">
           	<table class="easyui-datagrid" align="center" style="width:430px;height:180px;"
			    		data-options="singleSelect:true,url:'${ctx}/srtemplate/findRegex',nowrap:false,
			    		rowStyler: function(index,row){
		                    if (index%2==1){
		                        return 'background-color:#F6F4F0;';
		                    }
		                }">
			    		
			    <thead>
					<tr>
						<th data-options="field:'c',width:50">字符</th>
						<th data-options="field:'desc',width:376">说明</th>
					</tr>
				</thead>
			</table>
        </div>
        <div style="margin-bottom:10px;">
        	<input class="easyui-switchbutton tb" id="enablefilter_save" style="width:110px;" gchecked="${template.enablefilter=='on'?true:false}" name="enablefilter"
	        		data-options="onText:'开',offText:'关',handleText:'启用过滤',handleWidth:70,checked:${template.enablefilter=='on'?true:false},
	        			onChange:function(checked){
		        			$('#filter_width_save').textbox({disabled:!checked});
		        			if(!checked)$('#filter_width_save').textbox('setValue','');
		        			if(checked){$(this).attr('gvalue','on');}else{$(this).attr('gvalue','off');}
	        		}" />
           <input id='filter_width_save'  class='easyui-textbox tb' label="截屏过滤条件：" data-options="labelWidth:110,prompt:'图片宽度小于...',disabled:${'on' eq template.enablefilter?false:true}" name="filter_width"
				style="width:270px;height:30px;" value="${template.filter_width}"/>
        </div>
        <div style="margin-bottom:5px;">
        	<b>页脚图片设置</b>
        </div>
        <div class="container" id="crop-avatar-logo" style="height:60px; ">
			<div class="avatar-view" title="修改页脚图片" style="width:200px;height:40px; text-align: center;">
				<img id="sign_img" src="image/getSRTemplateImg?path=${template.footer_img }" alt="" width="220px">
			</div>
			
			<div class="easyui-dialog" title="修改页脚图片" data-options="closed:true,resizable:false,border:'thin',doSize:true,modal:true,buttons: '#dialog_btn'" 
						style="width:740px;height:570px;padding:10px;" id="avatar-modal">
			
				<form class="avatar-form" action="" enctype="multipart/form-data" method="post">
		     		<div class="easyui-layout" style="width:720px;height:470px;">
				        <div data-options="region:'north'" style="height:30px;padding:1px;" border="0">
					        <input class="avatar-src" name="avatar_src" type="hidden">
			                <input class="avatar-data" name="avatar_data" type="hidden">
			                <a href="#" class="avatar-upload" style="width:80px;">选择图片
		                  		<input class="avatar-input" id="avatarInput" name="avatar_file" type="file">
		                  	</a>
				        </div>
				        <div data-options="region:'east',split:true" style="width:200px;padding:50px 5px 0px 20px;" border="0">
				        	预览：
				        	<div class="avatar-preview preview-md"></div>
				        </div>
				        <div data-options="region:'center'" border="0">
				            <div class="avatar-wrapper"></div>
				            <div style="text-align:center;padding:5px;" class="avatar-btns">
				            	<a href="#" class="easyui-linkbutton" data-method="rotate" data-option="-90">向左旋转90度</a>
			                    <a href="#" class="easyui-linkbutton" data-method="rotate" data-option="90">向右旋转90度</a>
				            </div>
				        </div>
				    </div>
		          </form>
		    </div>
			
			<div id="dialog_btn">
				<a href="#" class="easyui-linkbutton" style="width:80px;" id="donebtn">完成</a>
				<a href="#" class="easyui-linkbutton" style="width:80px;" id="closebtn">取消</a>
			</div>
			
		</div>
        <div style="width:430px;">
        	<b>页脚设置</b>
	        <div class="top" style="margin-top:5px;">
	            <table>
	                <tr>
	                    <td><div class="item" rtype="pagenum">页码</div></td>
	                    <td><div class="item" rtype="logo">图片</div></td>
	                    <td><div class="item" rtype="patientid">病人姓名</div></td>
	                </tr>
	            </table>
	        </div>
	        <div class="down">
	            <table border="1">
	                <tr>
	                    <td class="title">左</td>
	                    <td class="title">中</td>
	                    <td class="title">右</td>
	                </tr>
	                <tr>
	                    <td class="drop" rposition="left">
	                    	<c:if test="${template.footer_left eq 'pagenum'}">
   								<div class="item assigned" rtype="pagenum">页码</div>
							</c:if>
							<c:if test="${template.footer_left eq 'logo'}">
   								<div class="item assigned" rtype="logo">图片</div>
							</c:if>
							<c:if test="${template.footer_left eq 'patientid'}">
   								<div class="item assigned" rtype="patientid">病人姓名</div>
							</c:if>
	                    </td>
	                    <td class="drop" rposition="middle">
	                    	<c:if test="${template.footer_middle eq 'pagenum'}">
   								<div class="item assigned" rtype="pagenum">页码</div>
							</c:if>
							<c:if test="${template.footer_middle eq 'logo'}">
   								<div class="item assigned" rtype="logo">图片</div>
							</c:if>
							<c:if test="${template.footer_middle eq 'patientid'}">
   								<div class="item assigned" rtype="patientid">病人姓名</div>
							</c:if>
	                    </td>
	                    <td class="drop" rposition="right">
	                    	<c:if test="${template.footer_right eq 'pagenum'}">
   								<div class="item assigned" rtype="pagenum">页码</div>
							</c:if>
							<c:if test="${template.footer_right eq 'logo'}">
   								<div class="item assigned" rtype="logo">图片</div>
							</c:if>
							<c:if test="${template.footer_right eq 'patientid'}">
   								<div class="item assigned" rtype="patientid">病人姓名</div>
							</c:if>
	                    </td>
	                </tr>
	            </table>
	        </div>
	    </div>
	    <style type="text/css">
	        .top{
	            width:430px;
	            /* float:left; */
	            margin-bottom:10px;
	        }
	        .top table{
	            background:#E0ECFF;
	        }
	        .top td{
	            background:#eee;
	            width:140px;
	        }
	        .down{
	            /* float:right; */
	            width:430px;
	            background:#E0ECFF;
	        }
	        .down table{
	            background:#E0ECFF;
	            width:100%;
	            height:50px;
	            border:1px solid #E0ECFF;
	        }
	        .down td{
	            background:#E0ECFF;
	            color:#444;
	            text-align:center;
	            padding:2px;
	            width:140px;
	        }
	        .down td.drop{
	            background:#fafafa;
	            width:140px;
	            height:25px;
	        }
	        .down td.over{
	            background:#FBEC88;
	        }
	        .item{
	            text-align:center;
	            border:1px solid #499B33;
	            background:#fafafa;
	            color:#444;
	            width:136px;
	        }
	        .assigned{
	            border:1px solid #BC2A4D;
	        }
	        .trash{
	            background-color:red;
	        }
	        
	    </style>
<script src="${ctx}/js/cropper/cropper.min.js"></script>
<script src="${ctx}/js/cropper/main.js"></script>
	    <script>
	        $(function(){
	            $('.top .item').draggable({
	                revert:true,
	                proxy:'clone'
	            });
	            
	            $('.down .item').draggable({
	                revert:true
	            });
	            
	            $('.down td.drop').droppable({
	                accept: '.item',
	                onDragEnter:function(){
	                    $(this).addClass('over');
	                },
	                onDragLeave:function(){
	                    $(this).removeClass('over');
	                },
	                onDrop:function(e,source){
	                    $(this).removeClass('over');
	                    if ($(source).hasClass('assigned')){
	                    	//相互交换位置
	                    	var sourceparent=$(source).parent('td');
	                    	var children=$(this).children('div');
	                        $(this).append(source);
	                        sourceparent.append(children);
	                    } else {
	                    	var rtype=$(source).attr('rtype');
	                    	if($('.down').find("div[rtype='"+rtype+"']").length>0){//通过rtype判断是否已经存在，已经存在返回false，不再添加
	                    		return false;
	                    	}
	                        var c = $(source).clone().addClass('assigned');
	                        $(this).empty().append(c);
	                        c.draggable({
	                            revert:true
	                        });
	                    }
	                }
	            });
	            $('.top').droppable({
	                accept:'.assigned',
	                onDragEnter:function(e,source){
	                    $(source).addClass('trash');
	                },
	                onDragLeave:function(e,source){
	                    $(source).removeClass('trash');
	                },
	                onDrop:function(e,source){
	                    $(source).remove();
	                }
	            });
	            
	            var es_cropAvatar=new CropAvatar($('#crop-avatar-logo'),window.localStorage.ctx+"/srtemplate/uploadTempFooterImg?tempid="+$('#templet_id').val(),16 / 3);
	            
	            
	        });
	    </script>
	</div>
</body>
</html>