<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
 	<head>
	    <title></title>
	    <link rel="stylesheet" type="text/css"  href="${ctx}/layui/css/layui.css?v=${vs}">
	    
		<link rel="stylesheet" type="text/css" href="${ctx}/js/cropper/main.css?v=${vs}">
		<link rel="stylesheet" type="text/css" href="${ctx}/js/cropper/cropper.min.css?v=${vs}">
	    <%@ include file="/common/basejs.jsp"%>
	    <script src="${ctx}/js/cropper/cropper.min.js"></script>
		<script src="${ctx}/js/cropper/main.js"></script>
		<script type="text/javascript" src="${ctx}/layui/layui.js?v=${vs}"></script>
		
		<style>
			.layui-table-cell{
			     height:100%; 
			}
			.layui-form-checkbox {
    			top: 0px !important;
			}
			 /*body .layim-chat-main{height: auto;}
			
			.layui-input{
		  		width:200px;
		  		display: inline;
		  	} */
		  	
		  	.avatar-upload {
			   	padding: 10px 10px;
			    border: none;
			    border-radius: 2px;
			    background: #1E9FFF;
			}
		</style>
	</head>
<body>
  
	<div class="layui-tab layui-tab-brief" style="height:380px;">
		<ul class="layui-tab-title">
		    <li class="layui-this">找人</li>
		    <li>找群</li>
		    <li>建群</li>
		    <li>群管理</li>
		</ul>
		
		<div class="layui-tab-content">
		    <div class="layui-tab-item layui-show">
				<div class="layui-row" style="margin-top: 20px;margin-left: 20px;">
					<div class="layui-col-md4">
						<input type="text" id="searchpara" name="para" placeholder="请输入用户名/名称" autocomplete="off" 
							class="layui-input" style="width:300px;" onkeydown="fastSearchFriend(event)">
				    </div>
				    <div class="layui-col-md8">
				      	<button type="button" class="layui-btn layui-btn-normal" onclick="searchFriend()">查找</button>
				    </div>
				</div>
			
				<div style="margin-left: 20px;margin-right: 20px;">
					<table id="friendlist_find" class="layui-table" lay-skin="line" lay-data="{page:true, id:'table_friendlist_find',height:310}">
						<thead>
						    <tr>
						      <th lay-data="{field: 'avatar', title: '头像', toolbar:'#avatar',align:'center',style:'height:100%'}"></th>
						      <th lay-data="{field: 'username', title: '用户名', align:'center'}"></th>
						      <th lay-data="{field: 'name', align:'center',title: '名称'}"></th>
						      <th lay-data="{field: 'sign', align:'center',title: '签名'}"></th>
						      <th lay-data="{field: 'id', title: '操作', align:'center', toolbar:'#toAddFriend'}"></th>
						    </tr>
						</thead>
					</table>
				</div>
				<script type="text/html" id="avatar">
				<img src="${ctx}/image/getAvatarImg?path={{d.avatar}}" style="width:40px;height:40px;border-radius: 50%">
				</script>
				
				<script type="text/html" id="groupavatar">
				<img src="${ctx}/image/getGroupAvatarImg?path={{d.avatar}}" style="width:40px;height:40px;border-radius: 50%">
				</script>
		
				<script type="text/html" id="toAddFriend">
				<button type="button" class="layui-btn  layui-btn-normal inline" onclick="toAddFriend({{d.id}}, '{{d.name}}','{{d.avatar }}')">添加好友</button>
				</script>
			</div>
			 <div class="layui-tab-item">
				<div class="layui-row" style="margin-top: 20px;margin-left: 20px;">
					<div class="layui-col-md4">
						<input type="text" id="searchpara_group" name="para" placeholder="请输入群名称" autocomplete="off" class="layui-input" 
							style="width:300px;" onkeydown="fastSearchGroup(event)">
				    </div>
				    <div class="layui-col-md8">
				      	<button type="button" class="layui-btn layui-btn-normal" onclick="searchGroup()">查找</button>
				    </div>
				</div>
				<div style="margin-left: 20px;margin-right: 20px;">
					<table id="grouplist" class="layui-table" lay-skin="line" lay-data="{page:true, id:'table_grouplist',height:310}">
						<thead>
						    <tr>
						      <th lay-data="{field:'avatar',title: '头像', width: 80, toolbar:'#groupavatar',align:'center'}"></th>
						      <th lay-data="{field: 'groupname',title: '群名称',align:'center'}"></th>
						      <th lay-data="{field: 'id', title: '操作', align:'center', toolbar:'#toAddGroup'}"></th>
						    </tr>
						</thead>
					</table>
				</div>
				<script type="text/html" id="toAddGroup">
					<button type="button" class="layui-btn  layui-btn-normal inline" onclick="toAddGroup({{d.id}}, '{{d.groupname}}','{{d.avatar }}')">添加群</button>
				</script>
			</div>
		    <div class="layui-tab-item">
		    	<div class="layui-row" style="margin-top: 20px;margin-left: 20px;">
				    <div class="layui-col-md4">
				      	<input type="text" id="groupname_txt" placeholder="请输入群名称" autocomplete="off" class="layui-input" 
							style="width:300px;">
				    </div>
				    <div class="layui-col-md8">
						<button type="button" class="layui-btn layui-btn-normal" onclick="createGroup()">创建</button>
				    </div>
				</div>
				<div style="margin-left: 20px;margin-right: 20px;">
					<table id="friendlist" class="layui-table" lay-skin="line" lay-data="{url:'${ctx}/chat/getMyfriend', page:true, id:'table_myfriendlist',height:310}">
						<thead>
						    <tr>
						    	<th lay-data="{type:'checkbox'}"></th>
						      	<th lay-data="{field:'avatar',title: '头像',width: 80, toolbar:'#avatar',align:'center'}"></th>
						      	<th lay-data="{field: 'username',title: '用户名',width:100,align:'center'}"></th>
						      	<th lay-data="{field: 'name',title: '姓名',minWidth: 150,align:'center'}"></th>
						      	<th lay-data="{field: 'sign',title: '签名',minWidth: 150,align:'center'}"></th>
						    </tr>
						</thead>
					</table>
				</div>
			</div>
			<div class="layui-tab-item">
				<div style="margin-left: 20px;margin-right: 20px;">
					<div class="layui-row">
					    <div class="layui-col-xs5">
					    	<table id="grouplist_manage" class="layui-table" lay-skin="line" lay-filter="grouplist_manage_filter"
								lay-data="{url:'${ctx}/chat/getMyGroups', id:'table_grouplist_manage',height:370,cellMinWidth: 80}">
								<thead>
								    <tr>
								    	<!-- <th lay-data="{type:'checkbox'}"></th> -->
								      	<th lay-data="{field:'avatar',title: '头像', width: 80, toolbar:'#groupavatar',align:'center'}"></th>
								      	<th lay-data="{field: 'groupname',title: '群名称',width: 210,align:'center'}"></th>
								      	<th lay-data="{field: 'id', title: '操作', align:'center',minWidth: 100, toolbar:'#toManageGroup'}"></th>
								    </tr>
								</thead>
							</table>
							<script type="text/html" id="toManageGroup">
								<button type="button" class="layui-btn layui-btn-danger" onclick="delGroup({{d.id}}, '{{d.groupname}}')">删除</button>
							</script>
					    </div>
					    <div class="layui-col-xs7">
					    	<div class="layui-side-scroll" style="width:100%;">
					    	<div class="layui-row" style="margin-top: 5px;margin-left: 20px;">
					    		<b>群信息</b>
								<hr>
								<div class="layui-col-md2" >
									<div style="width:60px;height:60px;">
										<div class="container" id="crop-avatar">
							          		<div class="avatar-view" title="修改头像" style="width:40px;height: 40px;">
												<img id="avatar_img" src="${ctx}\themes\head.ico" width="40px" onchange="console.log(this.value)">
											</div>
											<div style="width:740px;height:400px;display:'none';visibility:none;" id="avatar-modal">
												<form class="avatar-form" action="" enctype="multipart/form-data" method="post">
													<div class="layui-row" style="margin-left: 20px;">
														<div class="layui-col-md9">
															<div class="avatar-wrapper"></div>
												            <div style="text-align:center;padding:5px 0px 15px 0px;" class="avatar-btns">
												            	<button type="button" class="layui-btn layui-btn-sm" data-method="rotate" data-option="-90">向左旋转90度</button>
											                    <button type="button" class="layui-btn layui-btn-sm" data-method="rotate" data-option="90">向右旋转90度</button>
												            </div>
														</div>
														<div class="layui-col-md3" style="margin-top: 5px;padding: 5px;">
															<div class="layui-row" style="text-align:center;margin-top: 20px;">
																<input class="avatar-src" name="avatar_src" hidden>
												                <input class="avatar-data" name="avatar_data" hidden>
												                <a href="#" class="avatar-upload" style="width:80px;">选择头像
											                  		<input class="avatar-input" id="avatarInput" name="avatar_file" type="file">
											                  	</a>
															</div>
															<div class="layui-row" style="margin-top: 20px;height:170px;">
																<div class="layui-card" style="margin-left: 30px;height:170px">
															        <div class="layui-card-header">预览</div>
															        <div class="layui-card-body">
															    		<div class="avatar-preview preview-sm" style="height:69px;"></div>
															        </div>
															    </div>
															</div>
															<div class="layui-row" style="text-align:center;margin-top: 90px;">
																
																	<button type="button" class="layui-btn layui-btn-normal" style="width:70px;" id="donebtn">完成</button>
																	<button type="button" class="layui-btn layui-btn-normal" style="width:70px;" id="closebtn">取消</button>
																
															</div>
													    </div>
													</div>
									          	</form>
									    	</div>
										</div>
									</div>
							      	
							    </div>
								<div class="layui-col-md8" style="margin-top: 15px;">
									<label class="layui-form-label">群名称：</label>
							      	<input type="text" id="groupname_manage_txt" placeholder="请输入群名称" autocomplete="off" class="layui-input" 
										style="width:180px;" onchange="groupname_onchange(this.value)">
							    </div>
							    <div class="layui-col-md2" style="margin-top: 15px;">
							    	<input id="groupid_manage" name="groupid_manage" type="hidden">
							    	<input id="creatorid_manage" name="creatorid_manage" type="hidden">
									<button type="button" id="save_group_btn" class="layui-btn layui-btn-disabled" disabled="disabled" onclick="saveGroup()">保存</button>
							    </div>
							</div>
					    	<div class="layui-row" style="margin-left: 20px;margin-top: 5px;">
					    		<b>群成员</b>
								<hr>
						    	<table id="groupuser_list" class="layui-table" lay-skin="line" lay-filter="friendlist_manage_filter"
									lay-data="{id:'table_groupuser',height:220,cellMinWidth: 80}">
									<thead>
									    <tr>
									    	<th lay-data="{type:'checkbox',LAY_CHECKED:true}"></th>
									    	<th lay-data="{field: 'avatar', title: '头像', toolbar:'#avatar',align:'center',style:'height:100%'}"></th>
											<th lay-data="{field: 'username', title: '用户名', align:'center'}"></th>
											<th lay-data="{field: 'name', align:'center',title: '名称'}"></th>
									    </tr>
									</thead>
								</table>
							</div>
							
							<div class="layui-row" style="margin-left: 20px;margin-top: 5px;">
					    		<b>好友列表</b>
								<hr>
						    	<table id="friend_notingroup_list" class="layui-table" lay-skin="line" lay-filter="friend_notingroup__filter"
									lay-data="{id:'table_friend_notingroup',height:220,cellMinWidth: 80}">
									<thead>
									    <tr>
									    	<th lay-data="{type:'checkbox'}"></th>
									    	<th lay-data="{field: 'avatar', title: '头像', toolbar:'#avatar',align:'center',style:'height:100%'}"></th>
											<th lay-data="{field: 'username', title: '用户名', align:'center'}"></th>
											<th lay-data="{field: 'name', align:'center',title: '名称'}"></th>
									    </tr>
									</thead>
								</table>
							</div>
					    	</div>
					    </div>
				  	</div>
				
					
				</div>
			</div>
		  </div>
	</div>
<script src="${ctx}/js/cropper/cropper.min.js"></script>
<script src="${ctx}/js/cropper/main.js"></script>
<script type="text/javascript">
	$('#avatar-modal').hide();
	var callback = function(){
		groupname_onchange(null);
    }
	
	var es_cropAvatar=null;

	var table,layim;
	layui.use(['element','table','layim'], function(){
		table = layui.table;
		layim = layui.layim;
		var element = layui.element;
		
		
		table.on('row(grouplist_manage_filter)', function(obj){
			console.log(obj.data) //得到当前行数据
			table.reload('table_groupuser', {
					 url: '${ctx}/chat/getGroupUserByGroupId'
					 ,where: {
						 'groupid': obj.data.id
					 }
			});
			
			table.reload('table_friend_notingroup', {
				 url: '${ctx}/chat/getFriendNotInGroup'
				 ,where: {
					 'groupid': obj.data.id
				 }
			});
			
			if(!es_cropAvatar){
				es_cropAvatar=new CropAvatar($('#crop-avatar'),window.localStorage.ctx+"/chat/uploadGroupAvatar",8 / 8,callback);
			}
			
			$('#groupname_manage_txt').val(obj.data.groupname);
			$('#groupid_manage').val(obj.data.id);
			$('#creatorid_manage').val(obj.data.creatorid);
			
			
			$('#save_group_btn').attr("disabled","disabled");
			$("#save_group_btn").addClass("layui-btn-disabled");
			$('#avatar_img').attr("src",'${ctx}/image/getGroupAvatarImg?path='+obj.data.avatar)
			
		});
		
		table.on('checkbox(friendlist_manage_filter)', function(obj){
			addOrRemoveMember($('#groupid_manage').val(),$('#groupname_manage_txt').val(),obj.data.userid,obj.checked);
		});
		
		table.on('checkbox(friend_notingroup__filter)', function(obj){
			addOrRemoveMember($('#groupid_manage').val(),$('#groupname_manage_txt').val(),obj.data.userid,obj.checked);
		});
		
		
	  	//第一个实例
	  	/*table.render({
		    elem: '#friendlist_find'
		    ,url: '${ctx}/chat/findFriend' //数据接口
		    ,page: true //开启分页
		    ,id: 'table1'
		    ,cols: [[ //表头
		      ,{field: 'avatar', title: '头像', toolbar:'#avatar',align:'center'}
		      ,{field: 'username', title: '用户名', align:'center',sort: true}
		      ,{field: 'name', align:'center',title: '名称'} 
		      ,{field: 'sign', align:'center',title: '签名'}
		      ,{field: 'id', title: '操作', align:'center', toolbar:'#toAddFriend'}
		    ]]
	  	});
	  	
	  	table.render({
		    elem: '#grouplist'
		    ,url: '${ctx}/chat/findFriend' //数据接口
		    ,page: true //开启分页
		    ,id: 'table2'
		    ,cols: [[ //表头
		      ,{field: 'avatar', title: '头像', toolbar:'#avatar',align:'center'}
		      ,{field: 'groupname', align:'center',title: '群名称'} 
		      ,{field: 'id', title: '操作', align:'center', toolbar:'#toAddGroup'}
		    ]]
	  	});
	  	
	   	table.render({
		    elem: '#friendlist'
		    ,url: '${ctx}/chat/getMyfriend' //数据接口
		    ,page: true //开启分页
		    ,id: 'table_myfriendlist'
		    ,cols: [[ //表头
		    	{type:'checkbox'}
		      ,{field: 'avatar', title: '头像', toolbar:'#avatar',align:'center'}
		      ,{field: 'username', title: '用户名', align:'center'}
		      ,{field: 'name', title: '名称',align:'center'}
		      ,{field: 'sign', title: '签名',align:'center'}
		    ]]
	  	}); */
	  	
	});
	
	
	function addOrRemoveMember(groupid,groupname,userid,checked){
		getJSON('${ctx}/chat/addOrRemoveMember',
			{
				groupid:groupid,
				groupname:groupname,
				userid:userid,
				checked:checked
			}
			,function(json){
				var j=validationDataAll(json);
				if(j.code==0){
					
				}
				else{
					layer.msg("操作失败！请重试，如果问题依然存在，请联系系统管理员！");
				}
			}
		);
	}
	
	
	function groupname_onchange(value){
		$('#save_group_btn').removeAttr("disabled");
		$("#save_group_btn").removeClass("layui-btn-disabled");
		$("#save_group_btn").addClass("layui-btn-normal");
	}
	
	function fastSearchFriend(event){
		if (event.keyCode == 13) {
			searchFriend();
		}
	}
	
	function searchFriend(){
		table.reload('table_friendlist_find', {
			 url: '${ctx}/chat/findFriend'
			 ,where: {
				 'para': $('#searchpara').val()
			 }
		});
	}
	
	function fastSearchGroup(event){
		if (event.keyCode == 13) {
			searchGroup();
		}
	}
	
	function searchGroup(){
		
		if($('#searchpara_group').val()){
			table.reload('table_grouplist', {
				 url: '${ctx}/chat/findGroupByName'
				 ,where: {
					 'name': $('#searchpara_group').val()
				 }
			});
		}
	}
	
	function toAddFriend(id, username,avatar) {
		
		getJSON('${ctx}/chat/checkIsFriend',
			{
				toId:id
			}
			,function(json){
				if(!json){
	
					//layui.use('layim', function(layim){
						layim.add({
							type: 'friend' //friend：申请加好友、group：申请加群
							,username: username //好友昵称，若申请加群，参数为：groupname
							,avatar: '${ctx}/image/getAvatarImg?path='+avatar //头像
							,submit: function(group, remark, index){ //一般在此执行Ajax和WS，以通知对方
			
								getJSON("${ctx}/chat/applyToAddFriend",
						 			{
						  				toId: id,
						  				remark: remark
							  		},
								  	function(json) {
							  			var j=validationDataAll(json);
										if(j.code==0){
								  			layer.close(parent.layer.index);
								  			if(j.data=="1"){
								  				layer.msg($('#friendname').val()+"已经是你的好友！");
								  			}
								  			else if(j.data=="2"){
								  				layer.msg("已发送好友申请请耐心等待！");
								  			}
								  			layer.close(index); //关闭改面板
										}
										else{
											layer.msg("添加好友失败！请重试，如果问题依然存在，请联系系统管理员！")
										}
									}	
								);
							}
						});
					//});
		
				}
				else{
					layer.msg(username+"已经是你的好友！");
				}
			});
	}
	
	function toAddGroup(id, name,avatar) {
		getJSON('${ctx}/chat/checkInGroup',
			{
				groupid:id
			}
			,function(json){
				if(!json){
	
					//layui.use('layim', function(layim){
						layim.add({
							type: 'group' //friend：申请加好友、group：申请加群
							,groupname: name //好友昵称，若申请加群，参数为：groupname
							,avatar: '${ctx}/image/getAvatarImg?path='+avatar //头像
							,submit: function(group, remark, index){ //一般在此执行Ajax和WS，以通知对方
			
								getJSON("${ctx}/chat/applyToAddGroup",
						 			{
						  				groupid: id,
						  				remark: remark
							  		},
								  	function(json) {
							  			var j=validationDataAll(json);
							  			console.log(j)
										if(j.code==0){
								  			layer.close(parent.layer.index);
								  			if(j.data=="1"){
								  				layer.msg("您已经是群："+name+"的成员！");
								  			}
								  			else if(j.data=="2"){
								  				layer.msg("已发送申请请耐心等待！");
								  			}
								  			layer.close(index); //关闭改面板
										}
										else{
											layer.msg("申请发送失败！请重试，如果问题依然存在，请联系系统管理员！");
										}
									}	
								); 
							}
						});
					//});
		
				}
				else{
					layer.msg("您已经是群："+name+"的成员！");
				}
			});
	}
	
	function createGroup(){
		if($('#groupname_txt').val()){
			
			var arr = table.checkStatus('table_myfriendlist').data;
			if(arr.length>0){
				var friendids="";
				for(var i=0,len=arr.length;i<len;i++){
			        friendids+=arr[i].friendid+",";
			    }
				getJSON("${ctx}/chat/createGroup",
		 			{
		  				groupname: $('#groupname_txt').val(),
		  				friendids: friendids
			  		},
				  	function(json) {
			  			var j=validationDataAll(json);
			  			console.log(j)
						if(j.code==0){
							$('#groupname_txt').val("");
							table.reload('table_grouplist_manage');
						}
						else{
							layer.msg("创建群失败！请重试，如果问题依然存在，请联系系统管理员！");
						}
					}	
				);
			}
			else{
				layer.msg("请选择好友！");
			}
		}
		else{
			layer.msg("请输入群名称！");
		}
	}
	
	function delGroup(id,groupname){
		layer.confirm('确认删除选择的群吗?', {icon: 3, title:'确认'}, function(index){
		  	
			getJSON("${ctx}/chat/delGroup",
	 			{
					groupid: id,
	  				groupname: groupname
		  		},
			  	function(json) {
		  			var j=validationDataAll(json);
					if(j.code==0){
						table.reload('table_grouplist_manage');
					}
					else{
						layer.msg("删除群失败！请重试，如果问题依然存在，请联系系统管理员！");
					}
				}	
			);

		  	layer.close(index);
		});
	}
	
	function saveGroup(){
		
		if($('#groupname_manage_txt').val()){
			
			var id=$('#groupid_manage').val(),
			groupname=$('#groupname_manage_txt').val(),
			avatar=getUrlParam('path');
			
			getJSON("${ctx}/chat/saveGroup",
	 			{
					id: id,
	  				groupname: groupname,
	  				avatar:avatar
		  		},
			  	function(json) {
		  			var j=validationDataAll(json);
					if(j.code==0){
						table.reload('table_grouplist_manage');
						/* layim.removeList({
					  		type: 'group' //或者group
						  	,id: id //好友或者群组ID
						}); 
						layim.addList({
							type: 'group' //列表类型，只支持friend和group两种
							,avatar: "${ctx}/image/getGroupAvatarImg?path="+avatar //群组头像
							,groupname: groupname //群组名称
							,id: id //群组id
						});*/
					}
					else{
						layer.msg("保存失败！请重试，如果问题依然存在，请联系系统管理员！");
					}
				}	
			);
		}
		else{
			layer.msg("请输入群名称！");
		}
	}
	
	function getUrlParam(variable) {
		var src=$('#avatar_img').attr('src');
        var query = src.substring(src.indexOf('?')+1);
        console.log(query)
        var vars = query.split("&");
        for (var i=0;i<vars.length;i++) {
                var pair = vars[i].split("=");
                if(pair[0] == variable){return pair[1];}
        }
        return(false);
    }

</script>
</body>
</html>
 