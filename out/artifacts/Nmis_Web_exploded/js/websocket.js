/**
 * 
 */
var wsurl=""; 
var lockReconnect = false;  //避免ws重复连接
var urlFlag = false;
var reconn_count=0;
//重连最大次数，-1：无限制
var reconn_max_count=100;

$(function() {
	var protocal="ws://";
	if(window.location.protocol.indexOf("https")>=0){
		protocal="wss://";
	}
	wsurl=protocal+window.localStorage.serverName+":"+window.localStorage.port+window.localStorage.ctx+"/websocket/"+$("#userid_hidden").val()+"?ip="+window.localStorage.clientIP;
	createWebSocket(wsurl);
});

function createWebSocket(url) {
    try{
    	console.log("reconn_count="+reconn_count);
        if('WebSocket' in window){
        	websocket = new WebSocket(url);
        }else if('MozWebSocket' in window){  
        	websocket = new MozWebSocket(url);
        }else{
        	$.messager.show({
    			title : $.i18n.prop('error'),msg : $.i18n.prop('wl.browserdonotsupportwebsocket'),timeout : 3000,border: 'thin',showType : 'fade',style:{
                    right:'',
                    bottom:''
                }
    		});
        }
        initEventHandle();
    }catch(e){
        reconnect(url);
        console.log(e);
    }     
}


function initEventHandle(){
	//连接发生错误的回调方法
    websocket.onerror = function () {
//        setMessageInnerHTML("WebSocket连接发生错误");
    	if(reconn_max_count>0&&reconn_count>=reconn_max_count){
    		console.log("WebSocket连接错误!达到最大连接次数("+reconn_max_count+")，不再重新连接...");
	    	$.messager.show({
				title : $.i18n.prop('error'),msg : $.i18n.prop('wl.connecttoserverfailed')+"或者请重新登录！",timeout : 0,border: 'thin',showType : 'fade',style:{
	                right:'',
	                bottom:''
	            }
			});
    	} else{
	    	reconnect(wsurl);
	        console.log("WebSocket连接错误!正在重新连接...");
	    	$.messager.show({
				title : $.i18n.prop('error'),msg : $.i18n.prop('wl.connecttoserverfailed')+"或者请重新登录！",timeout : 3000,border: 'thin',showType : 'fade',style:{
	                right:'',
	                bottom:''
	            }
			});
    	}
    };    
    //连接成功建立的回调方法
    websocket.onopen = function () {
//        setMessageInnerHTML("WebSocket连接成功");
    	heartCheck.reset().start();      //心跳检测重置
        console.log("WebSocket连接成功!"+new Date());
        reconn_count=0;
    }    
    //接收到消息的回调方法
    websocket.onmessage = function (event) {
//        setMessageInnerHTML(event.data);
    	heartCheck.reset().start();      //拿到任何消息都说明当前连接是正常的
        console.log("WebSocket收到消息:" +event.data);
        if(event.data!='pong'){
        	var json=JSON.parse(event.data);
        	var service=serviceMap.get(json.op_type);
        	if(service){
        		service.exec(json.data);
        	}
        }

    }    
    //连接关闭的回调方法
    websocket.onclose = function () {
//        setMessageInnerHTML("WebSocket连接关闭");
    	if(reconn_max_count<0||reconn_count<reconn_max_count){
	    	reconnect(wsurl);
	        console.log("WebSocket连接关闭!"+new Date()+"-正在重新连接...");
    	}
    }    
    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function () {
    	console.log("urlFlag:"+urlFlag);
    	if(!urlFlag){
    		
    		closeWebSocket();
    	}else{
    		urlFlag = false;
    	}
        
    } 
}

function reconnect(url) {
    if(lockReconnect) return;
    lockReconnect = true;
    reconn_count++;
    setTimeout(function () {     //没连接上会一直重连，设置延迟避免请求过多
        createWebSocket(url);
        lockReconnect = false;
    }, 2000);
}

//心跳检测
var heartCheck = {
    timeout: 50000,        //1分钟发一次心跳
    timeoutObj: null,
    serverTimeoutObj: null,
    reset: function(){
        clearTimeout(this.timeoutObj);
        clearTimeout(this.serverTimeoutObj);
        return this;
    },
    start: function(){
        var self = this;
        this.timeoutObj = setTimeout(function(){
            //这里发送一个心跳，后端收到后，返回一个心跳消息，
            //onmessage拿到返回的心跳就说明连接正常
        	websocket.send('ping');
            //console.log("WebSocket发送消息:ping")
            self.serverTimeoutObj = setTimeout(function(){//如果超过一定时间还没重置，说明后端主动断开了
            	closeWebSocket();     //如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect 会触发onclose导致重连两次
            }, self.timeout)
        }, this.timeout)
    }
}
//关闭WebSocket连接
function closeWebSocket() {
    websocket.close();
} 

//发送消息
function send(message) {
    websocket.send(message);
}

//强行关闭WebSocket连接，不再重连，关闭心跳检测
function forceTocloseWebSocket() {
	console.log("Force to close websocket.")
	reconn_count=2;
	reconn_max_count=1;
	heartCheck.reset();
    websocket.close();
}

function reconnectWebSocket() {
	console.log("Reconnect websocket.");
	reconn_max_count=100;
	reconnect(wsurl);
}