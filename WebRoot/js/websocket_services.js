/**
 * 
 */

var serviceMap=new Map();

function initService_WS(obj){
	console.log("Init WebSocket Service "+JSON.stringify(obj)+" by key:"+obj.type);
	serviceMap.set(obj.type,obj);
}