/**
 * chat js
 */

$(function() {
	// layim 会将数据本地存储（local storage），当达到一定数目时需要进行清除
	setInterval(function () {
		var layim = localStorage.getItem("layim");
		console.log(layim.length/(1024*1024),layim.length);
		if (layim.length > (1024*1024*4)) {
			localStorage.setItem("layim", "");
		}
	}, 1000*60*10);
	
	var userId = $('#userid_hidden').val(); 
	var username = $('#username_hidden').val();
	var copyLayim;
	layui.use('layim', function(layim){
		copyLayim = layim;

    	var chat_online_reminder={
			type : "onlinereminder",
			exec : function(data){
				console.log("chat_online_reminder exec "+data);
				//console.log(layim.cache())
				//layer.msg("您的好友" + data + "上线了！");
				var chat_login_reminder = $("#chat_login_reminder").val();
				if ("0" != chat_login_reminder) {
					$.messager.show({
				        title: '提醒',
				        msg: "您的好友" + data + "上线了！",
				        timeout:1000,
				        width: 250,
				        height: 100,
				        border:'thin',
				        showType:'slide'
				    });
				}
				layim.setFriendStatus(data, 'online');
			}
		},chat_offline_reminder={
			type : "offlinereminder",
			exec : function(data){
				console.log("chat_offline_reminder exec "+data);
				layim.setFriendStatus(data, 'offline');
			}
    	},chat_message_content={
			type : "messagecontent",
			exec : function(data){
				console.log("chat_message_content exec "+data);
				layim.getMessage(data);
			}
    	},chat_add_friend={
    			type : "applyfriendgroup",
    			exec : function(data){
    				console.log("chat_add_friend exec "+data);
    				if(data.checkMessagetype == 1){//申请添加好友
    					layer.msg(data.fromUsername + '申请加您为好友！您可在消息盒子中进行处理！');
    					layim.msgbox(1);
    				}else if(data.checkMessagetype == 2){//申请加群
    					if(data.fromUsername != null){
    						layer.msg(data.fromUsername + ' 申请加入群聊！您可在消息盒子中进行处理！');
    						layim.msgbox(1);
    					}else{
    						layer.msg('邀请成功，请您耐心等待！');
    					}
    				}else if(data.checkMessagetype == 3){//处理好友申请，返回参数handelUsername不为空代表申请方
						if(data.handelResult == 1){
							if(data.handelUsername)layer.msg(data.handelUsername + '同意了您的好友申请！');
    						layim.addList(data.friend);
    						layim.msgbox(1);
						}else{
							if(data.handelUsername)layer.msg(data.handelUsername + '拒绝了您的好友申请！');
							layim.msgbox(1);
						}
    				}else if(data.checkMessagetype == 4){//处理群申请，申请方添加群至聊天面板，处理方不做操作
						if(data.handelResult == 1){
							layer.msg(data.handelUsername + '同意了您的群聊申请！');
							layim.addList(data.group);
							layim.msgbox(1);
						}else{
							layer.msg(data.handelUsername + '拒绝了您的群聊申请！');
							layim.msgbox(1);
						}
    				}else if(data.checkMessagetype == 5){//创建群,添加群至聊天面板
    					layer.msg('您已加入群聊：'+data.group.groupname);
						layim.addList(data.group);
						layim.msgbox(1);
    				}else if(data.checkMessagetype == 6){//删除群,从聊天面板移除群
    					layer.msg('您已退出群聊：'+data.groupname);
    					layim.removeList({
    						type: 'group' //或者group
    						,id: data.groupid //好友或者群组ID
						});
						layim.msgbox(1);
    				}
//    				if(data.checkMessageCount != null){
//    					layim.msgbox(data.checkMessageCount);
//    				}
    			}
        	},chat_message_box={
    			type : "messageBox",
    			exec : function(data){
    				if(data > 0){
    					layim.msgbox(data);
        				layer.msg('您有' + data + '条消息，请在消息盒子中查看！');
    				}
    			}
        	};
		if(websocket){
			initService_WS(chat_online_reminder);
			initService_WS(chat_offline_reminder);
			initService_WS(chat_message_content);
			initService_WS(chat_add_friend);
			initService_WS(chat_message_box);
		}
    	
        layim.config({
            brief: false, //是否简约模式（如果true则不显示主面板）
            min: true,
            title: 'IM',
            //right:'20px',
            init: {
            	url: window.localStorage.ctx +'/chat/queryBaseInformation'
            	,data: {'userId': userId}
			},
			members: {
				url: window.localStorage.ctx +'/chat/queryFriendsByGroupId',
			}, 
			uploadImage: {
				url: window.localStorage.ctx +'/chat/uploadImg'
			}
			,uploadFile: {
				url: window.localStorage.ctx +'/chat/uploadFile'
			}
			,chatLog: window.localStorage.ctx +'/chat/toChatRecord' 
			,find: window.localStorage.ctx +'/chat/toSearchFriendOrGroup' 
			,msgbox: window.localStorage.ctx +'/chat/toMessageBox' 
        });
        
        //layim消息发送监听器
		layim.on('sendMessage', function(res) {
			console.log(res);
			var mine = res.mine; //包含我发送的消息及我的信息
			var to = res.to; //对方的信息
			var msg = {
					'collectUserId': to.id,
					'content': mine.content
			}
			send(JSON.stringify(res));
		});
		
		//监听查看群员
		layim.on('members', function(data){
			console.log(data)
			if(data&&data.list&&data.list.length>=0){
				var ids=new Array();
				for(var i=0,len=data.list.length;i<len;i++){
					ids.push(data.list[i].id);
				}
				getJSON(window.localStorage.ctx +'/chat/getGroupMemberStatus',
					{
		 		    	'ids': ids.toString()
		 		    }, 
		 		    function (json) {
		 		    	if(json.code==0){
		 		    		for(var i=0,len=json.data.length;i<len;i++){
		 						var group_item =layui.$(".layim-members-list li[data-uid='"+json.data[i].id+"']");
		 						console.log(group_item)
		 						if(group_item[0]){
		 							if(json.data[i].online){
		 								group_item.removeClass('layim-list-gray');
		 							} else{
		 								group_item.addClass('layim-list-gray');
		 							}
		 							group_item.attr("title",group_item.find("cite").first().text()+(json.data[i].lastlogintime?("  最近登录时间："+json.data[i].lastlogintime):''));
		 						}
		 		    		}
		 		    	}
		 		    });
			}
		});
		
		//每次窗口打开或切换，即更新对方的状态
		layim.on('chatChange', function(res){
//		  var type = res.data.type;
//		  if(type === 'friend'){
//		    layim.setChatStatus('<span style="color:#FF5722;">在线</span>'); //模拟标注好友在线状态
//		  } else if(type === 'group'){
//		    模拟系统消息
//		    layim.getMessage({
//		      system: true //系统消息
//		      ,id: 111111111
//		      ,type: "group"
//		      ,content: '贤心加入群聊'
//		    });
//		  }
		});
		
		/*layim.on('online', function(status){
			console.log(status); //获得online或者hide
			getJSON(window.localStorage.ctx +'/chat/updateSign',
				{
	 		    	'userId': userId, 
	 		    	'status': status,
	 		    }, 
	 		    function (datas) {
	 		    	if(datas == 0){
	 		    		layer.msg('很遗憾！您的签名修改失败。请重试！');
	 		    	}else{
	 		    		layer.msg('恭喜您！成功修改了签名！');
	 		    	}
	 		    }
	 		 );
		});*/
        
        //监听修改签名
		layim.on('sign',function(value){
			console.log(value);
			getJSON(window.localStorage.ctx +'/chat/updateSign',
				{
	 		    	'userId': userId, 
	 		    	'sign': value,
	 		    }, 
	 		    function (datas) {
	 		    	if(datas){
	 		    		layer.msg('成功修改了签名！');
	 		    	}else{
	 		    		layer.msg('您的签名修改失败。请重试！');
	 		    	}
	 		    }
	 		 );
		});     
	});
	
});
