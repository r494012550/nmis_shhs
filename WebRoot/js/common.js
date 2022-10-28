$.extend($.fn.validatebox.defaults.rules, {
    equals: {
        validator: function(value,param){
            return value == $(param[0]).val();
        },
        message: '密码不匹配'
    },
	exist: {
	    validator: function(value, param){
	        return param[0]!='true';
	    },
	    message: '用户名已经存在！'
	},
	isSpace: {
        validator: function(value){
        	var regex = /(^\s+)|(\s+$)/;
            return !regex.test(value);
        },
        message: '首尾不能输入空格'
    },
    birthdate: {
		//格式yyyy-MM-dd或yyyy-M-d
		validator: function(value){
			return  /^(?:(?!0000)[0-9]{4}([-]?)(?:(?:0?[1-9]|1[0-2])\1(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])\1(?:29|30)|(?:0?[13578]|1[02])\1(?:31))|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([-]?)0?2\2(?:29))$/i.test(value);
		},
		message: '清输入合适的日期格式'
    },
    checkPhone: {
    	validator: function(value){
            return /^1[3456789]\d{9}$/.test(value);
        },
        message: '手机号填写有误'
    },
    isCardNo: {
    	validator: function(value){
            return /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(value);
        },
        message: '身份证号填写有误'
    }
});

$(function() {
	
});

//退出登录
function logout(){
	//console.log($.cookie("userLanguage"));
	window.location.href=window.localStorage.ctx+"/signout?language="+$.cookie("userLanguage");
}
function sendAjax (option) {
	$.ajax({
		url : option.url,
		contentType : option.contentType,
		type:option.type,
		dataType : option.dataType,
		data : option.data,
		success : function(data) {
			option.success(data);
//			if (data.data=="") {
//				option.success(data);
//			} else {
//				window.location.href = window.localStorage.ctx+"/login";
//			}
		},
		error:function(req,info,e){
//		  console.log(ewq.responseText);
//		  console.log(ewq.statusText);
//		  console.log(info);
//		  console.log(e);

		}
	});
}
//function sendAjax1(option){
//	$
//	.ajax({
//		url : option.url,
//		contentType : option.contentType,
//		type:option.type,
//		dataType : option.dataType,
//		data : option.data,
//		success : function(data) {
//			if (data) {
//				option.success(data);
//			} else {
//				window.location.href = window.localStorage.ctx+"/login";
//			}
//		},
//	})
//}
function getJSON(url,data,success){
	$.ajax({
		url : url,
	    contentType : "application/x-www-form-urlencoded; charset=UTF-8",
	    data: data,
		type:'Post',
		dataType:'json',
		success : function(data) {
			 success(data);
		},
		error:function(req,info,e){
//			  console.log(ewq.responseText);
//			  console.log(ewq.statusText);
			  console.log(info);
//			  console.log(e);

		}
	})
}
//$.fn.datebox.defaults.formatter = function(date){
//	var y = date.getFullYear();
//	var m = date.getMonth()+1;
//	var d = date.getDate();
//	return y+'-'+m+'-'+d;
//}

$.fn.datebox.defaults.parser = function(s){
	var t = Date.parse(s);
	if (!isNaN(t)){
		return new Date(t);
	} else {
		return new Date();
	}
}


function redirect(url,username) {
 var f=document.createElement('form');
    f.style.display='none';
    f.action=url;
    f.method='post';
    f.innerHTML='<input type="hidden" name="username" value="'+username+'"/>';
    document.body.appendChild(f);
    f.submit();
    //window.location.href="?tel=" + this.user.phone;
}
function redirect(url,username,keyWord) {
	 var f=document.createElement('form');
	    f.style.display='none';
	    f.action=url;
	    f.method='post';
	    f.innerHTML='<input type="hidden" name="username" value="'+username+'"/>';
	    f.innerHTML='<input type="hidden" name="keyWord" value="'+keyWord+'"/>';
	    document.body.appendChild(f);
	    f.submit();
	    //window.location.href="?tel=" + this.user.phone;
	}
function validationData(json){
	
	if(json){
		var js;
		try{
			js=JSON.parse(json);
		}catch(e){
			js=json;
		}
		if(js.error){
			if(js.error=="timeout"){
				redirect(window.localStorage.ctx+'/login',null);
			}
			else{
				$.messager.show({
	                title:'错误',
	                msg: js.error,
	                timeout:3000,
	                border:'thin',
	                showType:'slide'
	            });
			}
		}
		else{
			if(js.data){
				return js.data;
			}
			return js;
		}
	}
}




function validationDataAll(json){
	
	if(json){
		var js;
		try{
			js=JSON.parse(json);
		}catch(e){
			js=json;
		}
		if(js.error){
			if(js.error=="timeout"){
				redirect(window.localStorage.ctx+'/login',null);
			}
			else{
				$.messager.show({
	                title:'错误',
	                msg: js.error,
	                timeout:3000,
	                border:'thin',
	                showType:'slide'
	            });
			}
		}
		else{
			if(js.data){
				return js;
			}
			return js;
		}
	}
}

//去除空格
function trim(str){
	if(str){
		return str.replace(/^(\s|\u00A0)+/,'').replace(/(\s|\u00A0)+$/,'');
	} else {
		return '';
	}
}

Date.prototype.format = function(format) {

    /*
     * format="yyyy-MM-dd hh:mm:ss";
     */
    var o = {
        "M+" : this.getMonth() + 1,
        "d+" : this.getDate(),
        "h+" : this.getHours(),
        "m+" : this.getMinutes(),
        "s+" : this.getSeconds(),
        "q+" : Math.floor((this.getMonth() + 3) / 3),
        "S" : this.getMilliseconds()
    };
    if (/(y+)/.test(format)) {
            format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4- RegExp.$1.length));
        }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)){
            format = format.replace(RegExp.$1, RegExp.$1.length == 1? o[k]:("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
    
};
//转化JSON日期格式
function toDate(objDate, format) {
    var date = new Date();
    date.setTime(objDate.time);
    date.setHours(objDate.hours);
    date.setMinutes(objDate.minutes);
    date.setSeconds(objDate.seconds);
    return date.format(format);
}

function uuid() {
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "-";
 
    var uuid = s.join("");
    return uuid;
}

/**
 *  身份证号码验证
 * @param card  身份证号
 * @returns
 */
function isCardNo(card) {
	// 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X  
	var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;  
	if (reg.test(card) === false) {  
		return false;     
	} else { 
		return true;
	}
}

/**
 *  验证手机号
 * @param phone  输入的手机号
 * @returns
 */
function checkPhone(phone) {
	if (!(/^1[3456789]\d{9}$/.test(phone))) {
        return false; 
    } else {
    	return true;
    }
}

function _message(msg , title){
	$.messager.show({
        title: title||'提醒',
        msg: msg,
        timeout:window.localStorage.message_timeout?window.localStorage.message_timeout:3000,
        border:'thin',
        showType:'slide'
    });
}
function _alert(msg , title){
	$.messager.alert({
        title: title||'警告',
        msg: msg,
        timeout:3000,
        border:'thin'
    });
}

function hasSQLInject(value){
	re= /select|update|delete|exec|count|'|"|=|;|>|<|%/i;
	if (re.test(value)) {
		return true;
	} else {
		return false;
	}
}
/**
 *  时间比较
 * @param startTime  开始日期
 * @param endTime    结束日期
 * @returns
 */
function compareDate(startTime,endTime){
	var starttime= new Date(Date.parse(startTime));
	var endtime=new Date(Date.parse(endTime));
	if(starttime>endtime){
		return true;
	}
	return false;
}

function validator_date (value) { 
    //格式yyyy-MM-dd或yyyy-M-d
    return/^(?:(?!0000)[0-9]{4}([-]?)(?:(?:0?[1-9]|1[0-2])\1(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])\1(?:29|30)|(?:0?[13578]|1[02])\1(?:31))|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([-]?)0?2\2(?:29))$/i.test(value); 
}

/**
 * 初始化时候 easyui-datebox 设置默认值为当前系统日期
 * @param date new Date()
 * @returns
 */
function formatterDate(date) {
    var day = date.getDate() > 9 ? date.getDate() : "0" + date.getDate();
    var month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0"
        + (date.getMonth() + 1);
    return date.getFullYear() + '/' + month + '/' + day;
}

function formatterDateTime(date) {
	var day = date.getDate() > 9 ? date.getDate() : "0" + date.getDate();
	var month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0"
	+ (date.getMonth() + 1);
	var hor = date.getHours();
	var min = date.getMinutes();
	var sec = date.getSeconds();
	return date.getFullYear() + '-' + month + '-' + day+" "+hor+":"+min+":"+sec;
}

/**
 *  非空校验
 * @param str
 * @returns
 */
function isEmpty(obj){
	return (typeof obj === 'undefined' || obj === null || obj === "");
}
function uniqueArray(arr){
	arr = arr.sort();
    console.log(arr);

    var arr1 = [arr[0]];
    for(var i=1,len=arr.length;i<len;i++){
        if(arr[i].toLowerCase() !== arr[i-1].toLowerCase()){
            arr1.push(arr[i]);
        }
    }
    return arr1;
}

function checkSessionAnRelogin(userid){
	getJSON(window.localStorage.ctx+'/toRelogin',{userid:userid},function(data){
		console.log(data)
		if(data.code==0){
			_message("你的账号正常，无需重新登录");
		} else{
			openReloginDialog()
		}
	});
	
}

function openReloginDialog(){
//	console.log($('#relogin_dialog').html())
	if($('#relogin_dialog').html()==''||$('#relogin_dialog').panel('options').closed){//||$('#relogin_dialog').panel('options').href.indexOf('toRelogin')>0
		$('#relogin_dialog').dialog(
			{
				title : '重新登录',
				width : 350,height : 320,
				closed : false,cache : false,resizable: false,minimizable: false,maximizable: false,collapsible: false,shadow:false,
				border: 'thin',
				href : window.localStorage.ctx+'/toRelogin',
				modal : true,
				onLoad :initEventForLogin
		});
	}
}

function initEventForLogin(){
//	console.log($('#username')[0])
	if($('#username').next('span.textbox')[0]){
		var username=$('#username_hidden').val();
		if(username){
			$('#username').textbox('setValue',username);
			$('#username').textbox({editable:false});
		}
		$('#username').textbox('textbox').focus();
		$('#username').textbox('textbox').bind('keyup', function(e){
			if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
				$('#password').textbox('textbox').focus();
			}
		});
		$('#password').textbox('textbox').bind('keyup', function(e){
			if (e.keyCode == 13){	// when press ENTER key, accept the inputed value.
				dologin(false);
			}
		});
	}
}
function openDevTools(){
	if(electron_enable()){
		electron_openDevTools();
	}
}
