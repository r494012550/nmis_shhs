<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
<div class="container" id="crop-avatar">
	<div style="padding:10px">
		<table>
			<tr>
				<td style="width:180px">启用电子签名:</td>
				<td><input class="easyui-switchbutton" data-options="onText:'开',offText:'关',checked:${enableesign=='1'?true:false},onChange:function(checked){switchSignature(checked)}" ></td>
			</tr>   
		</table>
	</div>
	<div class="avatar-view" title="修改签名" style="width:220px;margin-left:auto;margin-right:auto;">
		<img id="sign_img" src="${ctx}/profile/getSignImg?path=${esignpath}" alt="" width="220px">
	</div>
	
	<div class="easyui-dialog" title="修改签名" data-options="closed:true,resizable:false,border:'thin',doSize:true,modal:true,buttons: '#dialog_btn'" 
				style="width:740px;height:570px;padding:10px;" id="avatar-modal">
	
		<form class="avatar-form" action="" enctype="multipart/form-data" method="post">
     		<div class="easyui-layout" style="width:720px;height:470px;">
		        <div data-options="region:'north'" style="height:30px;padding:1px;" border="0">
			        <input class="avatar-src" name="avatar_src" type="hidden">
	                <input class="avatar-data" name="avatar_data" type="hidden">
	                <a href="#" class="avatar-upload" style="width:80px;">选择文件
                  		<input class="avatar-input" id="avatarInput" name="avatar_file" type="file">
                  	</a>
	                <!-- <input class="easyui-filebox" label="本地上传:" labelPosition="left" data-options="prompt:'Choose another file...'" id="avatarInput" name="avatar_file" style="width:400px"> -->
		        </div>
		        <!--<div data-options="region:'south',split:true" style="height:50px;" >
		        	<div class="avatar-btns">
			        	<div class="btn-group">
	                      <button class="btn btn-primary" data-method="rotate" data-option="-90" type="button" title="Rotate -90 degrees">Rotate Left</button>
	                      <button class="btn btn-primary" data-method="rotate" data-option="-15" type="button">-15deg</button>
	                      <button class="btn btn-primary" data-method="rotate" data-option="-30" type="button">-30deg</button>
	                      <button class="btn btn-primary" data-method="rotate" data-option="-45" type="button">-45deg</button>
	                    </div>
	                    
	                    
	                    
	                    <button class="btn btn-primary btn-block avatar-save" type="submit">Done</button>
		        	</div>
		        </div>-->
		        <div data-options="region:'east',split:true" style="width:200px;padding:50px 5px 0px 20px;" border="0">
		        	预览：
		        	<div class="avatar-preview preview-md"></div>
                    <!-- <div class="avatar-preview preview-lg"></div>
                    <div class="avatar-preview preview-sm"></div> -->
		        </div>
		        <div data-options="region:'center'" border="0">
		            <div class="avatar-wrapper"></div>
		            <div style="text-align:center;padding:5px;" class="avatar-btns">
		            	<a href="#" class="easyui-linkbutton" data-method="rotate" data-option="-90">向左旋转90度</a>
				        <!-- <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'g1'" data-method="rotate" data-option="-15">-15deg</a>
				        <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'g1'" data-method="rotate" data-option="-30">-30deg</a>
				        <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'g1'" data-method="rotate" data-option="-45">-45deg</a> -->
	                    
	                    
	                    <a href="#" class="easyui-linkbutton" data-method="rotate" data-option="90">向右旋转90度</a>
				        <!-- <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'g2'" data-method="rotate" data-option="15">-15deg</a>
				        <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'g2'" data-method="rotate" data-option="30">-30deg</a>
				        <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'g2'" data-method="rotate" data-option="45">-45deg</a> -->
	                    
	                    <!-- <div class="btn-group">
	                      <button class="btn btn-primary" data-method="rotate" data-option="90" type="button" title="Rotate 90 degrees">Rotate Right</button>
	                      <button class="btn btn-primary" data-method="rotate" data-option="15" type="button">15deg</button>
	                      <button class="btn btn-primary" data-method="rotate" data-option="30" type="button">30deg</button>
	                      <button class="btn btn-primary" data-method="rotate" data-option="45" type="button">45deg</button>
	                    </div>-->
		            </div>
		        </div>
		    </div>
            
            
            
             
            <!-- <div class="modal-footer">
              <button class="btn btn-default" data-dismiss="modal" type="button">Close</button>
            </div> -->
          </form>
    </div><!-- /.modal -->
	
	<div id="dialog_btn">
		<a href="#" class="easyui-linkbutton" style="width:80px;" id="donebtn">完成</a>
		<a href="#" class="easyui-linkbutton" style="width:80px;" id="closebtn">取消</a>
	</div>
	
</div>
<script src="${ctx}/js/cropper/cropper.min.js"></script>
<script src="${ctx}/js/cropper/main.js"></script>
<script type="text/javascript">
$(function(){
	//setTimeout(function () {
		
	//}, 1000);
});

function switchSignature(checked){
	saveUserProfiles(checked,'enableesign');
}

var es_cropAvatar=new CropAvatar($('#crop-avatar'),window.localStorage.ctx+"/profile/updateSign",16 / 6);
</script>
        
</body>
</html>