<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	    <title>My JSP 'chatRecord.jsp' starting page</title>
	    
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">    
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		
		<link rel="stylesheet" type="text/css"  href="${ctx}/layui/css/layui.css?v=${vs}">
		<style>
			body .layim-chat-main{height: auto;}
		</style>
	</head>
  
  <body>
	<div class="layim-chat-main">
		<ul id="LAY_view"></ul>
	</div>

	<div id="LAY_page" style="margin: 0 10px;"></div>


	<textarea title="消息模版" id="LAY_tpl" style="display:none;">
		{{# layui.each(d.data, function(index, item){
	  		if(item.fromid == parent.layui.layim.cache().mine.id){ }}
				<li class="layim-chat-mine">
					<div class="layim-chat-user">
						<img src="${ctx}/{{ item.avatar }}"><cite><i>{{ layui.data.date(item.createtime) }}</i>{{ item.username }}</cite>
					</div>
				{{#  if(item.msgtype == 0){ }}
	    			<div class="layim-chat-text">{{ layui.layim.content(item.msgcontent) }}</div>
	    		</li>
	  			{{#  } else { }}
	   			<img style="min-width:50%; max-width:50%; margin-top: 20px;" src="${ctx}/{{ item.msgcontent }}">
	 		{{#  } }}
				
	  		{{# } else { }}
			    <li>
			    	<div class="layim-chat-user">
						<img src="${ctx}/{{ item.avatar }}"><cite>{{ item.username }}<i>{{ layui.data.date(item.createtime) }}</i></cite>
					</div>
				{{#  if(item.msgtype == 0){ }}
			    	<div class="layim-chat-text">{{ layui.layim.content(item.msgcontent) }}</div>
			   	 </li>
			  	{{#  } else { }}
			   		<img style="min-width:50%; max-width:50%; margin-top: 20px;" src="${ctx}/{{ item.msgcontent}}">
			 	{{#  } }}
	  		{{# }
		}); }}
	</textarea>
<div id="test1" style="text-align: center;"></div>

</body>
<script src="${ctx}/layui/layui.js?v=${vs}"  type="text/javascript"></script>
<script src="${ctx}/js/jquery-1.7.2.min.js?v=${vs}"  type="text/javascript"></script>
<script type="text/javascript">
var res;
var param =  location.search //获得URL参数。该窗口url会携带会话id和type，他们是你请求聊天记录的重要凭据
var collectUserId = param.substring(param.indexOf('=')+1,param.indexOf('&'));
var type = param.substring(param.lastIndexOf('=')+1);
var messageCount = 0;  
$(function(){
	  //开始请求聊天记录
		$.ajax({
		    type: 'post', 
		    data: {
		    	'sendUserId':${user.id}, 
		    	'collectUserId': collectUserId,
		    	'type':type,
		    	'page':1,
		    	'limit': 20
		    	}, 
		    url: '${ctx}/chat/queryMsgByType',                 
		    dataType:'json', 
		    success: function (data) {
		    	res = data;
			    }   
		});
	  //返回消息数量
		$.ajax({
		    type: 'post', 
		    data: {
		    	'sendUserId':${user.id}, 
		    	'collectUserId': collectUserId,
		    	'type':type,
		    	}, 
		    url: '${ctx}/chat/queryMsgCountByType',                 
		    dataType:'json', 
		    success: function (data) {
		    	messageCount = data;
		    }   
		});
});

</script>
<script>
layui.use(['layim', 'laypage'], function(){
  var layim = layui.layim
  ,layer = layui.layer
  ,laytpl = layui.laytpl
  ,$ = layui.jquery
  ,laypage = layui.laypage;
  
  //聊天记录的分页此处不做演示，你可以采用laypage，不了解的同学见文档：http://www.layui.com/doc/modules/laypage.html
  

  //console.log(param)
  var html = laytpl(LAY_tpl.value).render({
	  data: res.data
  });
  $('#LAY_view').html(html);
  
  laypage.render({
	    elem: 'test1' //注意，这里的 test1 是 ID，不用加 # 号
	    ,count: messageCount //数据总数，从服务端得到
  });
	
  laypage.render({
	  elem: 'test1'
	  ,count: messageCount //数据总数，从服务端得到
	  ,layout: ['prev', 'page', 'next','skip']
  	  ,groups: 2
  	  ,limit: 20
	  ,jump: function(obj, first){
	    //obj包含了当前分页的所有参数，比如：
	    console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
	    console.log(obj.limit); //得到每页显示的条数
	    
	    //首次不执行
	    if(!first){
	    	 $.ajax({
	 		    type: 'post', 
	 		    data: {
	 		    	'sendUserId':${user.id}, 
	 		    	'collectUserId': collectUserId,
	 		    	'type':type,
	 		    	'page':obj.curr,
	 		    	'limit': obj.limit
	 		    	}, 
	 		    url: '${ctx}/chat/queryMsgByType',                 
	 		    dataType:'json', 
	 		    success: function (datas) {
	 		    	html = laytpl(LAY_tpl.value).render({
	 		    		  data: datas.data
	 		    	  });
	 		    	  $('#LAY_view').html(html);
	 		    }   
	 		});
	    }
	  }
	});
  
});
</script>
</html>
