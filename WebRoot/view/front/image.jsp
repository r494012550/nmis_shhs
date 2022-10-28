<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>demo</title>
    <script type="text/javascript" src="/js/jquery.js?v=${vs}"></script>
    <script type="text/javascript" src="/js/jquery.photo.gallery.js?v=${vs}"></script>
	<style>
		html,body{
			width : 100%;
			height : 100%;
			margin:0;
			overflow: hidden;
		}
		img{
			max-width: 300px;
			max-height: 200px;
		}
	</style>
</head>
<body>
<div class="gallerys">
	<img class="gallery-pic" src="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510144272517&di=19515f98906798e45ac7b9015cb8a368&imgtype=0&src=http%3A%2F%2Fimg2.niutuku.com%2Fdesk%2Fanime%2F4351%2F4351-308.jpg" ondblclick="$.openPhotoGallery(this)" />
</div>
</body>
</html>