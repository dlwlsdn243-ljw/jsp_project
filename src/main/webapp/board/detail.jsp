<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Insert title here</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/detail.css">
	</head>
	<body>
		<div class="container">
			<h1>게시글 상세 페이지</h1>
			
				<div class="image-box">
					<img alt="" src="/_fileUpload/${board.imagefile }">				
				</div>
			<table border="1">
				<tr>
					<th>게시글 번호</th>
					<td>${board.bno }</td>
				</tr>
				<tr>
					<th>제목</th>
					<td>${board.title }</td>
				</tr>		
				<tr>
					<th>작성자</th>
					<td>${board.writer }</td>
				</tr>		
				<tr>
					<th>게시글 등록일</th>
					<td>${board.regdate }</td>
				</tr>
				<tr>
					<th>게시글 수정일</th>
					<td>${board.moddate }</td>
				</tr>		
				<tr>
					<th>내용</th>
					<td>${board.contents }</td>
				</tr>
			</table>
			<div class="btn">
				<a href="/brd/modify?bno=${board.bno }"><button>수정</button></a>
				<a href="/brd/delete?bno=${board.bno }"><button>삭제</button></a>
				
				<a href="/brd/list"><button>리스트</button></a>
			</div>
			
				<div class="comment-write">
					<h3>댓글 입력</h3>
					<input type="text" id="cmtWriter" value="${ses.id }" placeholder="writer..."> <br>
					<textarea rows="3" cols="30" id="cmtText" placeholder="Add Comment..."></textarea>
					<button type="button" id="cmtAddBtn">댓글 입력</button>
				</div>
				
				<div id="commentLine">
					<div>
						<div>cno, bno, writer, regdate</div>
						<div>
							<input type="text" value="contents...">
							<button type="button">수정</button>
							<button type="button">삭제</button>
						</div>
					</div>
				</div>
		
		</div>
			
			<script type="text/javascript">
				const bno = `<c:out value = "${board.bno}" />`;
				const sesId = `<c:out value = "${ses.id}" />`
			</script>
			
			<script type="text/javascript" src="/resources/boardDetails_comment.js"></script>
					
			<script type="text/javascript">
				printCommentList(bno);
			</script>
	</body>
</html>