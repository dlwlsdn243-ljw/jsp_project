<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>    

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Insert title here</title>
	</head>
	<body>
		<h1>리스트 페이지</h1>
		
		<div>
			<form action="/brd/list" method="get">
				<select name="type">
					<option value="t">제목</option>
					<option value="w">작성자</option>
					<option value="c">내용</option>
					<option value="a">전부</option>
				</select>
				
				<input type="text" name="keyword" placeholder="검색...">
				
				<button type="submit">검색</button>
				
				<span>검색결과 : ${ph.totalCount}개</span>
			</form>
			
			<table border=1>
				<thead>
					<tr>
						<th>게시글 수</th>
						<th>제목</th>
						<th>작성자</th>
						<th>작성일</th>
					</tr>
				</thead>
				
				<tbody>
					<c:forEach items="${list }" var="board">
						<tr>
							<td>${board.bno }</td>
							<td>
								<a href="/brd/detail?bno=${board.bno }">
									<img alt="" src="/_fileUpload/th_${board.imagefile }">
									${board.title }
								</a>
							</td>
							<td>${board.writer }</td>
							<td>${regdate }</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			
		<div>
			<!-- 이전  11 ~ 20-->
			<c:if test="${ph.prev }">
				<a href="/brd/list?pageNo=${ph.startPage-1 }&qty=${10 }&type=${ph.pagingVO.type}&keyword=${ph.pagingVO.keyword}"> < </a>
			</c:if>
			
			<!-- 1~10 -->
			<c:forEach begin="${ph.startPage }" end="${ph.endPage }" var="i">
				<a href="/brd/list?pageNo=${i }&qty=${10}&type=${ph.pagingVO.type}&keyword=${ph.pagingVO.keyword}">${i } </a>
			</c:forEach>
			
			<!-- 다음 -->
			<c:if test="${ph.next }">
				<a href="/brd/list?pageNo=${ph.endPage+1 }&qty=${10 }&type=${ph.pagingVO.type}&keyword=${ph.pagingVO.keyword}"> > </a>
			</c:if>
		</div>
		</div>
	</body>
</html>