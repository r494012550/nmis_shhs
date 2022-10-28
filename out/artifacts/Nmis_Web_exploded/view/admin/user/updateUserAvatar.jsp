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
	<div class="avatar-view" title="修改头像" style="width:100px;height:100px; margin-left:auto;margin-right:auto;text-align: center;">
		<img id="sign_img" src="image/getAvatarImg?path=${user.avatar }" alt="" width="220px">
	</div>
	
	<div class="easyui-dialog" title="修改头像" data-options="closed:true,resizable:false,border:'thin',doSize:true,modal:true,buttons: '#dialog_btn'" 
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
		            	<a href="#" class="easyui-linkbutton" data-method="rotate" data-option="-90">向左旋转90度1</a>
	                    <a href="#" class="easyui-linkbutton" data-method="rotate" data-option="90">向右旋转90度2</a>
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
<script src="${ctx}/js/cropper/cropper.min.js"></script>
<script src="${ctx}/js/cropper/main.js"></script>
<script type="text/javascript">

var es_cropAvatar=new CropAvatar($('#crop-avatar'),window.localStorage.ctx+"/updateUserAvatar",16 / 16);
</script>
        
</body>
</html>