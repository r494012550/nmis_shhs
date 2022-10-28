/**
 * 
 */

/*window.addEventListener("beforeunload", function(event){
	console.log("==beforeunload==");
    event.returnValue = 'dfsf';

}, false);*/
$(window).unload(function(){
	console.log("==unload==");
	closeUI();
});
$(window).bind('beforeunload', function (e) {
	console.log("==beforeunload==");
	closeUI();
})

window.onbeforeunload = function(e) {
	console.log("==beforeunload==");
	closeUI();
}
window.onunload  = function() {
	console.log("==unload==");
	closeUI();
}

function closeUI(){
	if(!closeReportFlag){
		closeReportFlag = true;
		return;
	}
	var ts=$('#tab').tabs('tabs');
	if(ts.length>1){
		var ids="";
    	for(var i=1;i<ts.length;i++){
    		var tabid=ts[i].panel('options').id;
    		ids+=tabid.substr(tabid.lastIndexOf("_")+1)+",";
    	}
    	
    	$.ajax({
    	    url: window.localStorage.ctx+"/report/closeReports?reportids="+ids,
    	    async: false
    	});
    	
//    	$.getJSON(window.localStorage.ctx+"/report/closeReports?reportids="+ids,function(json){
//    		
//    	});
    	console.log("==closeReports==");
	}
}