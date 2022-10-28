<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>图片查看器</title>
    <link rel="stylesheet" href="/themes/photoGallery.css?v=${vs}"/>
     <script type="text/javascript" src="/js/easyui/jquery.min.js?v=${vs}"></script>
    <script type="text/javascript" src="/js/jquery.photo.gallery.js?v=${vs}"></script>
</head>
<body>
<div class="box">
	<header drag>
		<div class="winControl" noDrag>
	        <span class="closeWin" title="关闭"><i class="icon_close-big"></i></span>
	    </div>
	</header>
	<div class="gallery"></div>
</div>
<script>
	$.initGallery();
</script>
</body>
</html>