<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Insert title here</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/registerboard.css">
	</head>
	<body>
		<div class="container">
			<h1>글쓰기 페이지</h1>
			
			<form action="/brd/insert" method="post" enctype="multipart/form-data">
				<div class="form-group">
					<label>제목</label>
					<input type="text" name="title" placeholder="제목..."> <br>
				</div>	
				<div class="form-group">
					<label>작성자</label>
					<input type="text" name="writer" placeholder="작성자..."> <br>
				</div>
				<div class="form-group">
					<label>내용</label>
					<textarea rows="10" cols="30" name="contents" placeholder="내용..."></textarea> <br>
				</div>
				<div class="form-group">
					<label>첨부파일</label>
					<input type="file" name="imagefile">
				</div>
				<div class="btn">
					<button type="submit">등록</button>
					<button type="reset">취소</button>						
				</div>
			</form>
		</div>
	</body>
</html>