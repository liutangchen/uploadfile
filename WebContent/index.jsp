<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<br>
	<form enctype="multipart/form-data" action="${pageContext.request.contextPath}/uploadServlet" method="post">
		选择上传的文件：<input type="file" name="file1"><br><br>
		上传的文件的描述：<input type="text" name="desc"><br><br>
		<input type="submit" value="上传"> <br><br>
	</form>
</body>
</html>