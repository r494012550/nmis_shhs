/**
 * 
 */

$(function(){
	$('#main').panel('open').panel('refresh',window.localStorage.ctx+'/admin/user');
	//$('#main').panel('open').panel('refresh','/admin/user');
});
//var temp = new Object();
//var cont;


//跳转报告模板页面
function  jumpReportTemp(){
//	temp.id = '';
//	temp.name = '';
//	temp.content = '';
//	temp.maprule = '';
//	i = 0;
	$('#main').panel('open').panel('refresh','/view/admin/templatemanage.jsp');
}
//跳转结构化字段页面
function jumpStructField(){
//	temp.id = '';
//	temp.name = '';
//	temp.content = '';
//	temp.maprule = '';
//	i = 0;
	$('#main').panel('open').panel('refresh','/view/admin/fieldmanage.jsp');
}
//跳转已存结构化模板
function jumpSavedTemp(){
//	temp.id = '';
//	temp.name = '';
//	temp.content = '';
//	temp.maprule = '';
//	i = 0;
	$('#main').panel('open').panel('refresh','/view/admin/savedTemplatemanage.jsp');
}
/**
 * 新建模板
 * @returns
 */

function jump_SRtemplate(){
	$('#main').panel('open').panel('refresh','srtemplatemanage');
}
