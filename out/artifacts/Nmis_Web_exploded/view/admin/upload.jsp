<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<p><div> 文件名: <span id='printName'></span><span></span></div></p>
	<input id='printFile' type='file' style='display:none' onchange='findFile();'/>
	<div align='center' style='margin-top:20px'>
	<a class="easyui-linkbutton" style="width:180px;height:32px" onclick="chooseFile();">选择文件</a>
	<a class="easyui-linkbutton" style="width:180px;height:32px" onclick="startUpload();">开始上传</a>
	</div>