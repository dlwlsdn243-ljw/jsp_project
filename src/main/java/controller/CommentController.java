package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.cj.xdevapi.JsonParser;

import domain.Comment;
import service.CommentService;
import service.CommentServiceImpl;


@WebServlet("/cmt/*")
public class CommentController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(CommentController.class);
 
	private CommentService csv;
	
    public CommentController() {
    	csv = new CommentServiceImpl();
    }


	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 모든 처리는 서비스에서 처리
		
		// 한글 깨짐 현상 방지
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		
		String uri = request.getRequestURI();
		
		String path = uri.substring(uri.lastIndexOf("/")+1);
		
		switch (path) {
		case "post":
			try {
				
				BufferedReader br = request.getReader();
				
				JSONParser parser = new JSONParser();
				// string -> key:value 형태로 변환
				JSONObject jsonobj = (JSONObject)parser.parse(br);
				log.info(">>> jsonobj>>{}", jsonobj);
				
				int bno = Integer.parseInt(jsonobj.get("bno").toString());
				String writer = jsonobj.get("writer").toString();
				String contents = jsonobj.get("contents").toString();
				
				Comment comment = new Comment();
				comment.setBno(bno);
				comment.setWriter(writer);
				comment.setContents(contents);
				
				int isOk = csv.insert(comment);
				
				log.info(">> comment insert >>{}", (isOk>0)? "성공":"실패");
				
				// 결과 보내기
				PrintWriter pw = response.getWriter();
				pw.print(isOk);
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		
		// 댓글 리스트 목록
		case "list":
			try {
				int bno = Integer.parseInt(request.getParameter("bno"));
				
				List<Comment> list = csv.getList(bno);
				// List<Comment> => Json 형식으로 변환
				

				JSONArray jsonArray = new JSONArray();  
				for(Comment c : list) {
					JSONObject obj = new JSONObject();  
					obj.put("cno", c.getCno());
					obj.put("bno", c.getBno());
					obj.put("writer", c.getWriter());
					obj.put("contents", c.getContents());
					obj.put("regdate", c.getRegdate());
					
					jsonArray.add(obj);
				}
				log.info(">>> json array > {}", jsonArray);
				

				String jsonData = jsonArray.toJSONString();
				
				PrintWriter pw = response.getWriter();
				pw.print(jsonData);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		// 댓글 수정
		case "modify":
			try {
				BufferedReader br = request.getReader();
				
				JSONParser parser = new JSONParser();
				JSONObject jsonobj = (JSONObject)parser.parse(br);
				
				int cno = Integer.parseInt(jsonobj.get("cno").toString());
				String contents = jsonobj.get("contents").toString();
				
				Comment comment = new Comment();
				comment.setCno(cno);
				comment.setContents(contents);
				
				int isOk = csv.update(comment);
				
				PrintWriter pw = response.getWriter();
				pw.print(isOk);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			
		// 댓글 삭제
		case "delete":
			try {
				int cno = Integer.parseInt(request.getParameter("cno"));
				
				int isOk = csv.delete(cno);
				
				PrintWriter pw = response.getWriter();
				pw.print(isOk);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		}
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}

}
