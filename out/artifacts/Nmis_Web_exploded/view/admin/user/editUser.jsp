<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
	
			<div style="padding:5px;margin-left:auto;margin-right:auto;width:482px;">
				<div class="easyui-panel" data-options="border:true" style="padding:10px;margin-bottom:5px;">
					<div style="float:left;width:60px;">
	            		<div class="container" id="crop-avatar">
		            		<div class="avatar-view" title="修改头像" style="width:40px;height: 40px">
								<img id="avatar_img" src="${user.avatar}" alt="" width="220px">
							</div>
							<div class="easyui-dialog" title="修改头像" data-options="closed:true,resizable:false,border:'thin',doSize:true,modal:true,buttons: '#dialog_btn_user'" 
									style="width:740px;height:570px;padding:10px;" id="avatar-modal">
								<form class="avatar-form" action="" enctype="multipart/form-data" method="post">
						     		<div class="easyui-layout" style="width:720px;height:470px;">
								        <div data-options="region:'north'" style="height:30px;padding:1px;" border="0">
									        <input class="avatar-src" name="avatar_src" type="hidden">
							                <input class="avatar-data" name="avatar_data" type="hidden">
							                <a href="#" class="avatar-upload" style="width:80px;">选择头像
						                  		<input class="avatar-input" id="avatarInput" name="avatar_file" type="file">
						                  	</a>
								        </div>
								        <div data-options="region:'east',split:true" style="width:200px;padding:50px 5px 0px 20px;" border="0">
								        	预览：
								        	<div class="avatar-preview preview-sm"></div>
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
						    <div id="dialog_btn_user">
								<a href="#" class="easyui-linkbutton" style="width:80px;" id="donebtn">完成</a>
								<a href="#" class="easyui-linkbutton" style="width:80px;" id="closebtn">取消</a>
							</div>
						</div>
	            	</div>
	       <form name="userform" id="userform" method="POST">
					<div style="float:right;width:400px;">
						<div style="margin-bottom:5px;">
			                <input class="easyui-textbox" label="${sessionScope.locale.get('admin.username')}" labelAlign="right" labelWidth="70" id="username" 
			                	data-options="prompt:'${sessionScope.locale.get('login.inputusername')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}',
									onChange:checkUsername" name="username" style="width:340px;" validType="isSpace" value="${user.username}">
			            </div>
			            <div style="margin-bottom:5px">
			                <input class="easyui-textbox" id="name" label="${sessionScope.locale.get('admin.name')}" labelAlign="right" labelWidth="70"
			                	data-options="prompt:'${sessionScope.locale.get('admin.inputname')}...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" 
			                	name="name" style="width:340px;" required="true" readonly="readonly" validType="isSpace" value="${user.name}">
			            </div>
	            	</div>
			        <div style="float:left">
			            <div style="margin-bottom:5px">
			                <input class="easyui-passwordbox passwordtext" id="password" label="${sessionScope.locale.get('login.password')}" labelAlign="right" labelWidth="130"
			                	data-options="prompt:'请输入密码...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" 
			                	name="password" style="width:400px;" validType="isSpace">
			            </div>
			            <div style="margin-bottom:5px">
			                <input class="easyui-passwordbox passwordtext" id="password1" label="${sessionScope.locale.get('admin.confirmpassword')}" labelAlign="right" labelWidth="130"
			                	data-options="prompt:'请再次输入密码...',required:true,missingMessage:'${sessionScope.locale.get('required')}'" 
			                	name="password1" style="width:400px;" validType="equals['#password']" >
			            </div>
			            <div style="margin-bottom:0px">
			                <input class="easyui-textbox" id="description" label="${sessionScope.locale.get('admin.desc')}" labelAlign="right" labelWidth="130"
			                	data-options="prompt:'${sessionScope.locale.get('admin.inputdesc')}...'" name="description" style="width:400px;" value="${user.description}">
		            	</div>
		            </div>
		            
		            <input id="roles" name="roles" type="hidden" value="">
						<input id="rolenames" name="rolenames" type="hidden" value="">
						<input id="userid" name="id" type="hidden" value="${user.id}">
						<input id="employeeid" name="employeefk" type="hidden" value="${user.employeefk}">
						<input id="active" name="active" type="hidden" value="${user.active}">
						<input id="lock" name="lock" type="hidden" value="${user.lock}">
						<input id="avatar" name="avatar" type="hidden" value="${user.avatar}">
						<input type="hidden" name="saveuserToken" value="${saveuserToken}" />
						<input id="vip_flag" name="vip_flag" type="hidden" value="${user.vip_flag}">
						<%-- <input id="ca_flag" name="ca_flag" type="hidden" value="${user.ca_flag}"> --%>
			</form>
		            
	            </div>
	        <form name="userform_1" id="userform_1" method="POST">
	        	 <div class="easyui-panel" data-options="border:true" style="padding:10px 30px;margin-bottom:5px;">
		            <div style="margin-bottom:0px">
		           		账号状态设置：&nbsp;激活<input class="easyui-checkbox" id="active_check" name="active_check" data-options="checked:${user.active eq '1'},
		           		 	onChange:function(checked){
									checked?$('#active').val('1'):$('#active').val('0');
								}">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;锁定<input class="easyui-checkbox" id="lock_check" name="lock_check" data-options="checked:${user.lock eq '1'},
							onChange:function(checked){
									checked?$('#lock').val('1'):$('#lock').val('0');
								}">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input class="easyui-checkbox" id="vip_check" label="vip：" labelWidth="40px" data-options="checked:${user.vip_flag eq 1},
                            onChange:function(checked){
                                    checked?$('#vip_flag').val(1):$('#vip_flag').val(0);
                                }">	
                         <%-- &nbsp;&nbsp;&nbsp;<input class="easyui-checkbox" id="ca_check" label="ca登录："   labelWidth="65px" data-options="checked:${user.ca_flag eq 1},
                            onChange:function(checked){
                                    checked?$('#ca_flag').val(1):$('#ca_flag').val(0);
                                }">	 --%>	
		            </div>
	            </div>	
	        	 <div class="easyui-panel" data-options="border:true" style="padding:10px 30px;margin-bottom:5px;">
		            <div style="margin-bottom:0px">
		            	<input class="easyui-combobox" id="modality" name="modality" style="width:298px;height:30px;" 
								data-options="valueField:'code',textField:'name_zh',multiple:true,
								url:'syscode/getCode?type=0004',editable:false,panelHeight:'120px'"
								label="检查类型：" labelPosition="left" value="${modalitystr}">
		            </div>
	            </div>
	             <%-- <div class="easyui-panel" data-options="border:true" style="padding:10px 30px;margin-bottom:5px;">
		            <div style="margin-bottom:0px">
		            	<input class="easyui-textbox" id="devSN" name="devSN" style="width:298px;height:30px;" 
								label="CAkey：" labelPosition="left" value="${user.devSN}">
		            </div>
	            </div>
	            <div class="easyui-panel" data-options="border:true" style="padding:10px 30px;margin-bottom:5px;">
		            <div style="margin-bottom:0px">
		            	<input class="easyui-textbox" id="key_password" name="key_password" style="width:298px;height:30px;" 
								label="CA密码：" labelPosition="left" value="${user.key_password}">
		            </div>
	            </div> --%>
	            
	            <div class="easyui-panel" data-options="border:true" style="padding:10px 30px;margin-bottom:5px;">
		            <div style="margin-bottom:0px">
		            	是否设置有效期：<input class="easyui-checkbox" id="hasExpiredate" name="hasExpiredate" value="1" data-options="checked:${hasExpiredate eq '1'},
								onChange:function(checked){
									checked?$('#expiredate').datebox('enable'):$('#expiredate').datebox('setText','').datebox('disable');
								}">
						<input class="easyui-datebox" id="expiredate" name="expiredate"  label="期限：" labelPosition="left" labelWidth="50px"
							data-options="disabled:${hasExpiredate ne '1'},required:true,missingMessage:'必填',editable: false" style="width:160px;height:30px;" value="${user.expiredate}">
		            </div>
	            </div>
	        
	            <div class="easyui-panel" data-options="border:true" style="padding:10px;margin-bottom:5px;">
		            
			            <table style="width:400px;height:140px;">
			            	<tr>
			            		<td style="width:70px;"></td>
				            	<td algin="right">${sessionScope.locale.get('admin.selecteditem')}</td>
				            	<td></td>
				            	<td></td>
				            	<td>${sessionScope.locale.get('admin.availableitem')}</td>
			            	</tr>
			            	<tr>
			            		<td></td>
			            		<td algin="right">
			            			<div id="rolelist" class="easyui-datalist" style="width:110px;height:120px;" required="true"
			       						data-options="url:'${ctx}/getUserrole?userid=${user.id}',textField:'rolename',valueField:'id',singleSelect:false,ctrlSelect:true"></div>
			            		</td>
			            		<td colspan="2">
				            		<a class="easyui-linkbutton easyui-tooltip" title="${sessionScope.locale.get('admin.add')}" plain="true" onClick="addRoleToList()"><i class="icon iconfont icon-arrowleft"></i></a>
				            		<a class="easyui-linkbutton easyui-tooltip" title="${sessionScope.locale.get('delete')}" plain="true" onClick="removeRoleToList()"><i class="icon iconfont icon-arrowright"></i></a>
			            		</td>
			            		<td algin="left">
			            			<div id="rolelist1" class="easyui-datalist" style="width:110px;height:120px;"
			       						data-options="url:'${ctx}/getRoleByEmployee?employeeid=${user.employeefk}&userid=${user.id }',textField:'rolename',valueField:'id',singleSelect:false,ctrlSelect:true"></div>
			            		</td>
			            	</tr>
			            </table>
	            </div>
	           
	            
	            
	            <!-- syngo.via账号密码 -->
	            <div class="easyui-panel" data-options="border:true" style="padding:10px;margin-bottom:5px;">
		            <div style="margin-bottom:5px">
		                <input class="easyui-textbox" label="syngo.via${sessionScope.locale.get('admin.username')}" labelAlign="right" labelWidth="130" id="viausername" 
		                	data-options="prompt:'请输入syngo.via用户名...',missingMessage:'${sessionScope.locale.get('required')}',onChange:checkUsername" 
		                	name="viausername" style="width:400px;" validType="isSpace" value="${user.viausername}">
		            </div>
		             <div style="margin-bottom:0px">
		                <input class="easyui-passwordbox" id="viapassword" label="syngo.via${sessionScope.locale.get('login.password')}" labelAlign="right" labelWidth="130" 
		                	data-options="prompt:'请输入syngo.via密码...',missingMessage:'${sessionScope.locale.get('required')}'" 
		                	name="viapassword" style="width:400px;" validType="isSpace" value="${user.viapassword}">
		            </div>
	          	</div>
	             <!-- syngo.plaza账号密码 -->
	            <div class="easyui-panel" data-options="border:true" style="padding:10px;margin-bottom:5px;">
		            <div style="margin-bottom:5px">
		                <input class="easyui-textbox" label="syngo.plaza${sessionScope.locale.get('admin.username')}" labelAlign="right" labelWidth="130" id="plazausername" 
		                	data-options="prompt:'请输入syngo.plaza用户名...',missingMessage:'${sessionScope.locale.get('required')}',onChange:checkUsername" 
		                	name="plazausername" style="width:400px;" validType="isSpace" value="${user.plazausername}">
		            </div>
		             <div style="margin-bottom:0px">
		                <input class="easyui-passwordbox" id="plazapassword" label="syngo.plaza${sessionScope.locale.get('login.password')}" labelAlign="right" labelWidth="130"
		                data-options="prompt:'请输入syngo.plaza密码...',missingMessage:'${sessionScope.locale.get('required')}'" 
		                name="plazapassword" style="width:400px;" validType="isSpace" value="${user.plazapassword}">
		            </div>
	       		</div>
				<div class="easyui-panel" data-options="border:true" style="padding:10px;margin-bottom:5px;">
					<div style="margin-bottom:5px">
						<input class="easyui-textbox" label="matrix用户名" labelAlign="right" labelWidth="130" id="matrix_username"
							   data-options="prompt:'请输入matrix用户名...',missingMessage:'${sessionScope.locale.get('required')}'"
							   name="matrix_username" style="width:400px;" validType="isSpace" value="${user.matrix_username}">
					</div>
				</div>
	       		</form>
			</div>
		

	<input id="checkexist" type="hidden">
	<script src="${ctx}/js/cropper/cropper.min.js"></script>
	<script src="${ctx}/js/cropper/main.js"></script>
	<style type="text/css">
	    .error-message{
	         margin: 4px 0 0 0;
	         padding: 2;
	         color: red;
	     }
	</style>
	<script type="text/javascript">
		var es_cropAvatar=new CropAvatar($('#crop-avatar'),window.localStorage.ctx+"/avatarUpload",8 / 8);
		function error(target, message){
	    }
	</script>
</body>
</html>