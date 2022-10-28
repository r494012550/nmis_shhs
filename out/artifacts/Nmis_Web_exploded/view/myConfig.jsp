<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
<div class="easyui-tabs" style="width:100%;height:100%;" data-options="tabPosition:'left',headerWidth:80,border:false">
        <div title="一般配置" style="padding:10px">
        	<div style="margin-bottom:10px">
        		<input class="easyui-switchbutton" label="双击检查列表同时打开图像：" labelPosition="top"
        			data-options="onText:'开',offText:'关',checked:${up.openimageatonce=='1'?true:false},onChange:function(checked){saveUserProfiles(checked,'openimageatonce')}"/>
        		<span style="width:200px;">双击检查列表同时打开图像</span>
			</div>
			<div style="margin-bottom:10px">
        		<input class="easyui-switchbutton" label="结构化报告中的图像添加编号：" labelPosition="top"
        			data-options="onText:'开',offText:'关',checked:${openimageatonce=='1'?true:false},onChange:function(checked){saveUserProfiles(checked,'openimageatonce')}" />
        		<span style="width:200px;">结构化报告中的图像添加编号</span>
			</div>
              <div style="margin-bottom:10px">
				<span style="width:200px;">报告自动保存时间间隔(单位分钟，0表示关闭自动保存)：</span>
				<div style="margin:20px 10px 30px 30px;">
					<input class="easyui-slider" style="width:300px" data-options="value:${up.auto_save_report_interval!=null?up.auto_save_report_interval:0},showTip:true,min:0,max:10,rule: [0,'|',2,'|',4,'|',6,'|',8,'|',10],
						tipFormatter:function(value){
							if(value==0){
								return value+'分钟(关闭)';
							} else{
								return value+'分钟';
							}
						} ,
						onComplete:function(value){
							console.log(value);
							saveUserProfiles(value,'auto_save_report_interval');
						}">
				</div>
			</div>
        </div>
      
        <div title="我的角色" style="padding:10px">
        	<div style="padding-left:10px;padding-top:10px;">
        	       您拥有 <span style="font-size: large;color:red;">${user.role}</span> 的角色。
        	</div>
        </div>
        <div title="电子签名" style="padding:10px" data-options="href:'${ctx}/profile/toesign'">
        	
        </div>
        <div title="修改头像" style="padding:10px" data-options="href:'${ctx}/toUpdateUserAvatar'">
        	
        </div>
        
        <div title="我的模块" style="padding:10px">
        	<p><b>请选择默认模块，登录系统后自动进入该模块</b></p>
            <div class="easyui-datalist" id="moduleslist" data-options="
	            url: '${ctx}/profile/getMyModules',textField:'module_name',checkbox: true,
	            onLoadSuccess:function(data){checkModulesList(data);},
	            onCheck:function(index,row){setMyDefaultModule(row);}
	            ">
	    	</div>
	    	
	    	<input id="defaultmodule" name="defaultmodule" type="hidden" value="${user.defaultmodule}">
        </div>
        <div title="登记保存" style="padding:10px">
        	<div style="margin-bottom:10px">
        		<input class="easyui-switchbutton" data-options="onText:'开',offText:'关',checked:${up.before_save_scan=='1'?true:false},
		            		onChange:function(checked){saveUserProfiles(checked,'before_save_scan')}" />
        		<span style="width:200px;">点击保存前扫描申请单</span>
			</div>
			<div style="margin-bottom:10px">
        		<input class="easyui-switchbutton" data-options="onText:'开',offText:'关',checked:${up.after_save_print=='1'?true:false},
		            		onChange:function(checked){saveUserProfiles(checked,'after_save_print')}" />
        		<span style="width:200px;">点击保存后打印检查单</span>
			</div>
		        <%-- <tr>
		            <td style="width:180px">打印检查单张数:</td>
		            <td><input class="easyui-numberbox" data-options="min:1,max:5,onChange:function(newVal,oldVal){saveUserProfiles_value(newVal,'studyform_print_copies')}" value="${up.studyform_print_copies==null?1:up.studyform_print_copies}" style="width:60px;height:30px;"/>&nbsp;张</td>
		        </tr> --%>
        </div>
        <div title="修改密码" data-options="" style="padding:10px">
            <div style="padding:10px;margin-left:auto;margin-right:auto;width:220px;">
				<form name="passwordform" id="passwordform" method="POST">
				<div style="margin-bottom:10px">
	                <input class="easyui-passwordbox" label="当前密码" labelPosition="top" id="oldpassword" validType="isSpace"
	                	data-options="prompt:'请输入当前密码...',required:true,missingMessage:'必填'" name="oldpassword" style="width:200px;height:60px;">
	            </div>
	            <div style="margin-bottom:10px">
	                <input class="easyui-passwordbox" id="newpassword" label="新密码" labelPosition="top" validType="isSpace"
	                	data-options="prompt:'请输入新密码...',required:true,missingMessage:'必填'" name="newpassword" style="width:200px;height:60px;">
	            </div>
	            <div style="margin-bottom:20px">
	                <input class="easyui-passwordbox" id="newpassword1" label="确认新密码" labelPosition="top" 
	                	data-options="prompt:'请输入确认新密码...',required:true,missingMessage:'必填'" name="newpassword1" style="width:200px;height:60px;" validType="equals['#newpassword']">
	            </div>
	            <div style="margin-bottom:10px">
	                <a class="easyui-linkbutton" style="width:200px;height:30px;" onclick="modifyPassword();">保存</a>
	            </div>
	            </form>
            </div>
        </div>
      <%--   <div title="选择设备" data-options="" style="padding:10px">
        	<div style="padding:10px;margin-left:auto;margin-right:auto;width:220px;">
				<form name="modalityform" id="modalityform" method="POST">
				<div style="margin-bottom:10px">
	                <input class="easyui-combobox" label="选择设备" labelPosition="top" id="dicModality"  value="${up.dicModality}"
	                	data-options="required:true,multiple:true,missingMessage:'必填',valueField:'id',textField:'modality_name',url:'${ctx}/findDicModality'" name="dicModality" style="width:200px;height:60px;">
	            </div>
	            <div style="margin-bottom:10px">
	                <input class="easyui-combobox" label="默认设备" labelPosition="top" id="defaultDicModality"   value="${up.defaultDicModality}"
	                	data-options="required:true,missingMessage:'必填',valueField:'id',textField:'modality_name',url:'${ctx}/findDicModality'" name="defaultDicModality" style="width:200px;height:60px;">
	            </div>

	            <div style="margin-bottom:10px">
	                <a class="easyui-linkbutton" style="width:200px;height:30px;" onclick="modifyModality();">保存</a>
	            </div>
	            </form>
        	</div>
        </div> --%>
        <div title="主题" data-options="" style="padding:10px">
        	<div style="padding:10px;margin-left:auto;margin-right:auto;width:335px;">
				<div style="margin-bottom:10px">
	                <%-- <img src="${ctx}/themes/via/via.png" alt="img" style="width: 160px; height: 112px;"/>
	                <img src="${ctx}/themes/metro-blue/metro-blue.png" alt="img" style="width: 160px; height: 112px;"/> --%>
	                <form name="themesform" id="themesform" method="POST">
	                <div style="float:left;margin-bottom:5px;margin-right:5px;border:1px solid #CCC;background:#FFFFFF;">
						<div style="padding:2px;">
							<input class="easyui-radiobutton" id="via" name="themes" value="via" data-options="checked:${up.theme=='via'?true:false},
		            			onChange:function(checked){if(checked){saveUserProfiles_value('via','theme')}}">
							<div style="float: right;padding:2px 0px 0px 2px;">
								<span style="width:100px;height:20px;display:block;overflow: hidden;font-size:10px;text-align:left;color:#c2c2c2">夜空</span>
							</div>
						</div>
						<div>
							<img src="${ctx}/themes/via/via.png" alt="img" style="width: 160px; height: 112px;"/>
						</div>
					</div>
	                <div style="float:right;margin-bottom:5px;margin-right:5px;border:1px solid #CCC;background:#FFFFFF;">
						<div style="padding:2px;">
							<input class="easyui-radiobutton" id="metro-blue" name="themes" value="metro-blue" data-options="checked:${up.theme=='metro-blue'?true:false},
		            			onChange:function(checked){if(checked){saveUserProfiles_value('metro-blue','theme')}}">
							<div style="float: right;padding:2px 0px 0px 2px;">
								<span style="width:100px;height:20px;display:block;overflow: hidden;font-size:10px;text-align:left;color:#c2c2c2">淡蓝</span>
							</div>
						</div>
						<div>
							<img src="${ctx}/themes/metro-blue/metro-blue.png" alt="img" style="width: 160px; height: 112px;"/>
						</div>
					</div>
	                </form>
	                <span style="width:200px;">提示：修改主题后请重新登录。</span>
	            </div>
        	</div>
        </div>
        <div title="报告" style="padding:10px">
	       <div style="margin-bottom:10px;">
	       		<b>请选择报告编辑器的字体颜色：</b>
	       		<input type="color" name="report_editor_color" onchange="saveUserProfiles_value(this.value,'report_editor_color');" value="${empty up.report_editor_color?'#000000':up.report_editor_color}">
	       </div>
	       <div style="margin-bottom:10px;">
	       		<input class="easyui-switchbutton" data-options="onText:'开',offText:'关',checked:${up.report_audit=='0'?false:true},
		            		onChange:function(checked){saveUserProfiles(checked,'report_audit')}" />
        		<span style="width:200px;">报告审核后直接关闭</span>
	       </div>
	       
	       <div style="margin-bottom:10px;">
	       		<input class="easyui-switchbutton" data-options="onText:'开',offText:'关',checked:${up.report_submit=='1'?true:false},
		            		onChange:function(checked){saveUserProfiles(checked,'report_submit')}" />
        		<span style="width:200px;">报告提交后直接关闭</span>
	       </div>
	       
	       <div style="margin-bottom:10px;">
	       		<span style="width:200px;">默认所见字体大小：</span>
	       		<select class="easyui-combobox" style="width:60px;height:30px;" labelAlign="right" 
					data-options="value:'${up.report_desc_fontsize}',editable:false,panelHeight:'auto',onChange:function(newValue,oldValue){saveUserProfiles_value(newValue,'report_desc_fontsize')}">
					<option value="12">12</option>
					<option value="16">16</option>
	               	<option value="18">18</option>
	                <option value="24">24</option>
	                <option value="32">32</option>
	                <option value="48">48</option>
	            </select>
	       </div>
	       <div style="margin-bottom:10px;">
	       		<span style="width:200px;">默认诊断字体大小：</span>
	       		<select class="easyui-combobox" style="width:60px;height:30px;" labelAlign="left" 
					data-options="value:'${up.report_result_fontsize}',editable:false,panelHeight:'auto',onChange:function(newValue,oldValue){saveUserProfiles_value(newValue,'report_result_fontsize')}">
					<option value="12">12</option>
					<option value="16">16</option>
	               	<option value="18">18</option>
	                <option value="24">24</option>
	                <option value="32">32</option>
	                <option value="48">48</option>
	            </select>
	            
	       </div>
        </div>
    </div>
</body>
</html>