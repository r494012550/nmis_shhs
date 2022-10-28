$(function(){
	//fullScreen();
	initEvent_login();
});

function initEvent_login(){
	if($('#username').next('span.textbox')[0]){
		$('#username').textbox('textbox').focus();
		$('#username').textbox('textbox').bind('keyup', function(e){
			if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
				$('#password').textbox('textbox').focus();
			}
		});
		$('#password').textbox('textbox').bind('keyup', function(e){
			if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
				dologin();
			}
		});
	}
}

function fullScreen() {  
	var el = document.documentElement;

	  var rfs = el.requestFullScreen || el.webkitRequestFullScreen;

	  if(typeof rfs != "undefined" && rfs) {

	    rfs.call(el);

	  } else if(typeof window.ActiveXObject != "undefined") {

	    var wscript = new ActiveXObject("WScript.Shell");

	    if(wscript != null) {

	        wscript.SendKeys("{F11}");

	  }

	  }else if (el.msRequestFullscreen) {
	
		  el.msRequestFullscreen();
	
	  }else if(el.oRequestFullscreen){
			
		  el.oRequestFullscreen();
		
	  }else{
	  	
	  	swal({   title: "浏览器不支持全屏调用！",   text: "请更换浏览器或按F11键切换全屏！(3秒后自动关闭)", type: "error",  timer: 3000 });	
		       
	  }
} 


function loadUser(){
	if(!window.localStorage){
        $('#errormessage').html("浏览器不支持localstorage")
        return false;
    }else{
        var storage=window.localStorage;
        $('#username').textbox('setValue',storage.getItem("username"));
        $("#password").focus();
    }
}


function validateForm() {
	 window.localStorage.setItem("username",$('#username').textbox('getValue'));	
}

function error(target, message){
    if(message==""){
    	$(target).next().next().html("&nbsp;");
    }
    else{
    	$(target).next().next().html(message);
    }
}


/**
 * 登录方法
 * @returns
 */
function dologin(reload) {
	$('#loginform').form('submit', {
		onSubmit: function(){
			$('#username').validatebox({
				required : true,
				validType : 'string',
				missingMessage : '请填写用户名',
			});
			$('#password').validatebox({
				required : true,
				validType : 'string',
				missingMessage : '请填写密码',
			});
			if($('#username').textbox('getValue')==""||$('#password').textbox('getValue')==""){
				return false;
			}
			validateForm();
		},
		success: function(data){
			var json=validationDataAll(data);
			if(json.code == 0 ){
				$.getJSON(json.data.ctx +"/cache",function(jn) {
					window.localStorage.myCache=JSON.stringify(jn[0]);
					window.localStorage.port=jn[1].port;
					window.localStorage.ctx=jn[1].ctx;
					window.localStorage.serverName=jn[1].serverName;
					window.localStorage.clientIP=jn[1].clientIP;
					window.localStorage.vs=jn[1].vs;
					window.localStorage.message_timeout=jn[1].timeout||3000;
					if(reload==null||reload){
						window.location.href = json.data.url;//'/worklist';
					} else{//重新登录，需要关闭对话框
						$('#relogin_dialog').dialog('close');
						reconnectWebSocket();
					}
				});
	         }else{
	        	 $('#errormessage').html(json.message)
	         }
		}
	});
}

//修改用户ca密码
/*function updateUserCa(){
	var causername=$("#causername").textbox("getValue");
	var oldCaPasswrod=$("#oldCaPasswrod").textbox("getValue");
	var CaPasswrod=$("#CaPasswrod").textbox("getValue");
	if(causername==null||causername==""){
		_message("请输入用户名");
		return;
	}
	if(oldCaPasswrod==null||oldCaPasswrod==""){
		_message("请输入原密码");
		return;
	}
	if(CaPasswrod==null||CaPasswrod==""){
		_message("请输入新密码");
		return;
	}
	$.getJSON(window.localStorage.ctx+"/updateUserCa?username="+causername+'&oldCaPasswrod='+oldCaPasswrod+'&CaPasswrod='+CaPasswrod, function(json){
	 	var js=validationData(json);
    	if (js.code==0) {
    		 _message("修改成功！！");
    		 $('#caKeydlg').dialog('close');
    	} else if(js.code==1){
    		_message("ca原密码错误，修改失败！！");
    	}else{
    		_message("修改失败，请联系管理员！！！");
    	}
 });
}
function opencaKeydlg(){
	 $("#causername").textbox("setValue","");
	 $("#oldCaPasswrod").textbox("setValue","");
     $("#CaPasswrod").textbox("setValue","");
	 $('#caKeydlg').dialog('open');
}*/
function updateProgressbar(value){
   $('#progressbar').progressbar('setValue', value);
}