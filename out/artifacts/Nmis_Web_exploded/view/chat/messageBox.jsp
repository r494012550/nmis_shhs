<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	    <title></title>
		<link rel="stylesheet" type="text/css"  href="${ctx}/layui/css/layui.css?v=${vs}">
		<script type="text/javascript" src="${ctx}/layui/layui.js?v=${vs}"></script>
		<%@ include file="/common/basejs.jsp"%>
		<!-- <script src="https://cdn.jsdelivr.net/npm/vue"></script> -->
		
		<style>
			 body .layim-chat-main{height: auto;}
			.layui-table-cell{
			    height:100%;
			}
			.layui-input{
		  		width:200px;
		  		display: inline;
		  	} 
		</style>
	</head>
<body>

	<div style="margin-left: 20px;margin-right: 20px;">
		<table id="messagebox_list" class="layui-table" lay-skin="line" lay-data="{url:'${ctx}/chat/getMessageBoxValue', page:true, id:'table_messagebox',
			done:function(){
				$('#messagebox_list').next('.layui-table-view').find('.layui-table-header').hide();
			}
		}">
			<thead>
			    <tr>
			      <th lay-data="{field:'avatar', width: 80, toolbar:'#avatar',align:'center'}"></th>
			     <!--  <th lay-data="{field: 'username'}"></th> -->
			      <th lay-data="{field: 'name', width: 250,templet: '#name_temp'}"></th>
			      <th lay-data="{field: 'id', align:'center', toolbar:'#messagebox_op'}"></th>
			    </tr>
			</thead>
		</table>
	</div>
	
	<script type="text/html" id="name_temp">


		{{# if(d.type==1){ }}

			{{#  if(${sessionScope.user.id}==d.fromid){ }}	
				<b>已申请添加 {{d.toname}} 为好友</b><br>
				{{d.createtime}}
			{{#  } else if(${sessionScope.user.id}==d.toid){ }}
    			<b>{{d.fromname}} 申请加您为好友</b><br>
				{{d.createtime}}
			{{#  } }}

		{{# } else if(d.type==2) { }}
			{{#  if(${sessionScope.user.id}==d.fromid){ }}	
				<b>已申请加入群  {{d.groupname}}</b><br>
				{{d.createtime}}
			{{#  } else if(${sessionScope.user.id}==d.toid){ }}
    			<b>{{d.fromname}}申请加群</b><br>
				{{d.createtime}}
			{{#  } }}
		{{#  } }}

	</script>
	
	<script type="text/html" id="avatar">
		{{#  if(${sessionScope.user.id}==d.fromid){ }}	
			<img src="${ctx}/image/getAvatarImg?path={{d.toavatar}}" style="width:50px;height:50px;border-radius: 50%">
		{{#  } else if(${sessionScope.user.id}==d.toid){ }}
			<img src="${ctx}/image/getAvatarImg?path={{d.toavatar}}" style="width:50px;height:50px;border-radius: 50%">
		{{#  } }}
	</script>
	
	<script type="text/html" id="messagebox_op">
	
		{{#  if(${sessionScope.user.id}==d.fromid){ }}	
			{{#  if(d.handelresult ==0){ }}
    			对方已拒绝
			{{#  } else if(d.handelresult ==1){ }}
    			对方已同意
  			{{#  } else { }}
    			等待
  			{{#  } }}			

		{{#  } else if(${sessionScope.user.id}==d.toid){ }}
			{{#  if(d.handelresult ==0){ }}
    			您已拒绝
			{{#  } else if(d.handelresult ==1){ }}
    			您已同意
  			{{#  } else { }}
    			<button type="button" class="layui-btn layui-btn-normal" onclick="handelApply(1,{{d.id}})">同意</button>
				<button type="button" class="layui-btn layui-btn-danger" onclick="handelApply(0,{{d.id}})">拒绝</button>
  			{{#  } }}
		{{#  } }}
	
		
	</script>
	<script type="text/javascript">
		var table=null;
		layui.use('table', function(){
			table = layui.table;
		});
		
		
		function handelApply(result, id) {
			
			getJSON('${ctx}/chat/handelApply',
				{
					result:result,
					id:id
				},
				function(json) {
					var j=validationDataAll(json);
					if(j.code==0){
						table.reload('table_messagebox', {})
					}
					else{
						parent.layer.msg("操作失败！请重试，如果问题依然存在，请联系系统管理员！")
					}
				}
			)
			
			
			/* var paraType = type == 1 ? 3 : 4;
			var addFriend = {
  				checkMessagetype: paraType,
  				result: result,
  				messageId: messageId,
  				fromid: fromid,
	  		}
			parent.send(JSON.stringify(addFriend));
			var copythis = this;
			setTimeout(() => {
				 $.ajax({
			 		    type: 'post', 
			 		    data: {
			 		    	'page':page,
			 		    	'limit': limit
			 		    	}, 
			 		    url: '${ctx}/chat/getMessageBoxValue',                 
			 		    dataType:'json', 
			 		    success: function (data) {
			 		    	copythis.data = data.data;
			 		    }   
			 		});
			}, 300); */
		}
		

	</script>





	<!-- <div id="vueDiv" style="padding: 20px;">
		
			<div v-for="(i, index) in data" style="padding-bottom: 15px;">
				<div v-if="i.toid == userId">
					<div style="float: left;">
						<img :src="imgpath + i.avatar" style="width: 50px; height: 50px; border-radius: 50%;"/>
					</div>
					<div style="float: left; padding-left: 10px;">
						<div style=" ">
							<label style="font-weight: bold;">{{i.username}}</label>
							<label style="margin-left: 5px;">{{i.time}}</label>
						</div>
						<div style="">
							<div v-if="i.type == 1">
								<label style="font-weight: bold;">{{i.username}}申请加您为好友</label>
							</div>
							<div v-if="i.type == 2">
								<label style="font-weight: bold;">邀请您进群</label>
							</div>
						</div>
					</div>	
					<div v-if="i.handelresult == null">
						<div  style="float: left;position: absolute;right: 50px;margin-top: 8px;">
							<button class="layui-btn" @click="handelMessage(1, i.id, i.fromid, i.type)">同意</button>
							<button class="layui-btn" style="background-color: red"@click="handelMessage(0, i.id, i.fromid, i.type)" >拒绝</button>
						</div>
					</div>
					<div v-if="i.handelresult != null">
						<div v-if="i.handelresult == 0" style="float: left; position: absolute;right: 50px; color: blue;margin-top: 13px; font-size: 18px;">
							您已拒绝
						</div>
						<div v-if="i.handelresult == 1" style="float: left; position: absolute;right: 50px; color: blue;margin-top: 13px; font-size: 18px;">
							您已同意
						</div>
					</div>
					<div style="clear: left;"></div>
					<hr class="layui-bg-gray">
				</div>
				<div v-if="i.fromid == userId && i.handelresult != null">	
					<div>{{i.handeltime}}</div>
					<div v-if="i.type == 1">
						<label v-if="i.handelresult == 1" ><label style="color: red">系统消息：</label>{{i.username}}同意了您的好友申请！</label>
						<label v-if="i.handelresult == 0" ><label style="color: red">系统消息：</label>{{i.username}}拒绝了您的好友申请！</label>
					</div>
					<div v-if="i.type == 2">
						<label v-if="i.handelresult == 1" ><label style="color: red">系统消息：</label>{{i.username}}同意了您加入群聊!</label>
						<label v-if="i.handelresult == 0" ><label style="color: red">系统消息：</label>{{i.username}}拒绝了您加入群聊！</label>
					</div>
					<hr class="layui-bg-gray">
				</div>
			</div>
	</div>
	<div id="test1" style="text-align: center;"></div>
	
	<script type="text/javascript">
		var page;
		var limit;
	</script>
	
	<script type="text/javascript">
		var vm = new Vue({
			el:'#vueDiv',
			created: function (){
				this.getValues();
				var copythis = this;
				setTimeout(() => {
					layui.use('laypage', function(){
						var laypage = layui.laypage;
					  	laypage.render({
							elem: 'test1' 
							,limit:10
					    	,count: copythis.messageCount
					    	,jump: function(obj, first){
					    		page = obj.curr;
					    		limit = obj.limit;
					    		 if(!first){
							    	 $.ajax({
							 		    type: 'post', 
							 		    data: {
							 		    	'page':obj.curr,
							 		    	'limit': obj.limit
							 		    	}, 
							 		    url: '${ctx}/chat/getMessageBoxValue',                 
							 		    dataType:'json', 
							 		    success: function (data) {
							 		    	copythis.data = data.data;
							 		    }   
							 		});
							    }
					    	}
						});
					});
				}, 200);
			},
			data:{
				data:null,
				userId: ${sessionScope.user.id},
				imgpath : '/report/image/getAvatarImg?path=',
				messageCount:0
			},
			methods:{
				getValues : function () {
					var copythis = this;
					$.ajax({
					    type: 'post', 
					    data: {page: 1, limit: 10}, 
					    url: '${ctx}/chat/getMessageBoxValue',                 
					    dataType:'json', 
					    success: function (data) {
					    	copythis.data = data.data;
					    	copythis.messageCount = data.count;
					    }   
					});
				},
				handelMessage: function (result, messageId, fromid, type) {
					var paraType = type == 1 ? 3 : 4;
					var addFriend = {
		  				checkMessagetype: paraType,
		  				result: result,
		  				messageId: messageId,
		  				fromid: fromid,
			  		}
					parent.send(JSON.stringify(addFriend));
					var copythis = this;
					setTimeout(() => {
						 $.ajax({
					 		    type: 'post', 
					 		    data: {
					 		    	'page':page,
					 		    	'limit': limit
					 		    	}, 
					 		    url: '${ctx}/chat/getMessageBoxValue',                 
					 		    dataType:'json', 
					 		    success: function (data) {
					 		    	copythis.data = data.data;
					 		    }   
					 		});
					}, 300);
				}
			}
		});
	</script> -->

</body>
</html>
