
/**
 * 设置语言类型： 默认为中文 
 */
var i18nLanguage = "zh_CN"; 
  
/* 
设置一下网站支持的语言种类 
zh-CN(中文简体)、en(英语) 
 */
var webLanguage = ['zh_CN','en_US']; 



var getNavLanguage = function(){
    if(navigator.appName == "Netscape"){
        var navLanguage = navigator.language;
        
        return navLanguage.replace('-','_');
//        return navLanguage.substr(0,2);
    }
    return false;
}
  
//获取网站语言 
function getWebLanguage(){
   //1.cookie是否存在 
  if ($.cookie("userLanguage")) { 
    i18nLanguage = $.cookie("userLanguage"); 
    //console.log("language cookie is "+i18nLanguage); 
    //console.log("system default language is "+$('#language').val());
    if($.cookie("userLanguage")!=$('#language').val()){
    	//window.location.href=window.localStorage.ctx+"/login?language="+i18nLanguage;
    	
    	redirect(window.localStorage.ctx+"/login?language="+i18nLanguage,"");
    	
    }
  } 
  else if($('#language').val()){
	  i18nLanguage = $('#language').val(); 
	  //console.log("language is "+i18nLanguage); 
      // 存到缓存中 
      setCookie("userLanguage",i18nLanguage)
  }
  else { 
    //2.1 获取用户设置的浏览器语言 
    var navLanguage = getNavLanguage(); 
    //console.log("user set browser language is "+navLanguage); 
    if (navLanguage) { 
      // 判断是否在网站支持语言数组里 
      var charSize = $.inArray(navLanguage, webLanguage); 
      if (charSize > -1) { 
        i18nLanguage = navLanguage; 
        // 存到缓存中 
        setCookie("userLanguage",navLanguage)
      }; 
    } else{ 
      console.log("not navigator"); 
      return false; 
    } 
  } 
    
}

function setCookie(name, value, expires, path, domain, secure) {
		var curCookie = name + "=" + escape(value) +
  	((expires) ? "; expires=" + expires.toUTCString() : "") +
  	((path) ? "; path=" + path : "") +
  	((domain) ? "; domain=" + domain : "") +
  	((secure) ? "; secure" : "");
		document.cookie = curCookie;
}

//国际化easyui中英文包 
//function changeEasyuiLanguage(languageName) {
//  // when login in China the language=zh-CN  
//  var src =$.contextPath+"/plugings/jquery-easyui/locale/easyui-lang-"+languageName.replace('-','_')+".js"; 
// console.log(src); 
// $.getScript(src); 
//}

function switchLanguage(languageName){

	setCookie("userLanguage",languageName);
	
	redirect(window.localStorage.ctx+"/login?language="+languageName,"");
	//window.location.href=window.localStorage.ctx+"/login?language="+languageName;
}



/** 
 * 执行页面i18n方法 
 * @return 
 * @author LH 
 */ 
var execI18n = function(){ 
  //获取网站语言(i18nLanguage,默认为中文简体) 

	if($('#language').val()){
		getWebLanguage(); 
	}
	else{
		i18nLanguage=$.cookie("userLanguage");
	}
	console.log("i18nLanguage="+$.cookie("userLanguage"));
    //国际化页面 
    jQuery.i18n.properties({ 
      name : "i18n", //资源文件名称 
      path : window.localStorage.ctx+"/i18n/", //资源文件路径 
      mode : 'map', //用Map的方式使用资源文件中的值 
      language : i18nLanguage, 
      cache:false, //指定浏览器是否对资源文件进行缓存,默认false 
      encoding: 'UTF-8', //加载资源文件时使用的编码。默认为 UTF-8。  
      callback : function() {//加载成功后设置显示内容 
        //以下是将要国际化的文字内容 
        //退出 
        //$("#logOut").html($.i18n.prop('logOut')); 
        //用户 
        //$("#loginUser").html($.i18n.prop('loginUser')) 
    	 
    	// window.location.href=window.localStorage.ctx+"/signout?language="+$.cookie("userLanguage");
      } 
    }); 
} 


/*页面执行加载执行*/
$(function(){ 
  /*执行I18n翻译*/
  execI18n(); 
//  console.log("网站语言： "+i18nLanguage); 
  //国际化easyui 
  //changeEasyuiLanguage(i18nLanguage);  
  
  //$.i18n.prop('wl.worklist')
});
