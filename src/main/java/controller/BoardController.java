package controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.Board;
import domain.PagingVO;
import handler.FileRemoveHandler;
import handler.PagingHandler;
import net.coobird.thumbnailator.Thumbnails;
import service.BoardService;
import service.BoardServiceImpl;


@WebServlet("/brd/*")
public class BoardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(BoardController.class);
	
	// RequestDispatcher 객체를 저장할 변수 하나를 만든다.
	// RequestDispatcher => JSP나 다른 서블릿의 경로(목적지)가 저장
	private RequestDispatcher rdp; 

	// 어느 jsp로 보낼지 주소(목적지)를 저장하는 변수
	private String destPage;
	
	// service 연결  (인터페이스 연결) 
	private BoardService bsv;
	
	// 클레스
    public BoardController() {
    	System.out.println("BoardController 생성");
    	bsv = new BoardServiceImpl(); //구현체
        
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// doGet, doPost 처리
		log.info(">>> BoardController 서버 테스트");
		
		// request, response 인코딩 설정
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String uri = request.getRequestURI();
		log.info(">>> uri >> {}", uri); // 주소 출력 (ex : http://localhost:8082/brd/register => /brd/register만 가지고 옴
		// uri : /brd/???????
		
		String path = uri.substring(uri.lastIndexOf("/")+1);
		log.info(">>> path >> {}", path); // (ex : uri => /brd/register 이면 /brd/ 날리고 register만 가져오게 됨
		// path : ???????
		
		switch(path) {
		case "register": 
			destPage = "/board/register.jsp";
			rdpForward(request, response);
			break;
			
		case "insert":
			try {
				Board board = new Board(); // DB저장 객체
				String savePath = getServletContext().getRealPath("/_fileUpload"); // 파일 저장 경로
				
				File fileDir = new File(savePath); 
				log.info(">> 파일경로 >> {}", fileDir.toString());
				
				DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
				fileItemFactory.setRepository(fileDir);
				fileItemFactory.setSizeThreshold(1024*1024+3);
				
				ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);
				List<FileItem> fileItem = fileUpload.parseRequest(request);
				
				for(FileItem item : fileItem) {
					log.info(">> item >> {}", item);
					switch (item.getFieldName()) {
					case "title":
						String title = item.getString("utf-8");
						board.setTitle(title);
					break;
					
					case "writer":
						String writer = item.getString("utf-8");
						board.setWriter(writer);
					break;
					
					case "contents":
						String contents = item.getString("utf-8");
						board.setContents(contents);
					break;
					
					case "imagefile":
						if(item.getSize() > 0) {
							String fileName = item.getName();
							// 중복방지 (시간기준)
							fileName = System.currentTimeMillis() + "_" + fileName;
							log.info(">>> 이미지 파일이름 >>> {}", fileName);
							
							// 파일 경로 지정
							File uploadFile = new File(fileDir+File.separator+fileName);
							log.info(">>> 이미지 파일경로 >>> {}", uploadFile.toString());
							
							// 저장
							try {
								item.write(uploadFile); // 서버에 저장
								board.setImagefile(fileName); // Board 객체에 저장
								
								// 썸내일 작업
								Thumbnails.of(uploadFile)
								.size(75, 75)
								.toFile(new File(fileDir+File.separator+"th_"+fileName));
								
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					break;
					
					}
				}
				// DB로 저장
				int isOk = bsv.insert(board);
				log.info(">>> insert {}", (isOk > 0) ? "성공" : "실패");
				
				// 페이지로 이동
				destPage="list";
				response.sendRedirect(destPage);

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		
		case "list":
			try {
				
				PagingVO pagingVO = new PagingVO(); // pageNo = 1 / qty = 10
				
				if(request.getParameter("pageNo") != null) {
					
					int pageNo = Integer.parseInt(request.getParameter("pageNo"));
					int qty = Integer.parseInt(request.getParameter("qty"));
					String type = request.getParameter("type");
					String keyword = request.getParameter("keyword");

					
					pagingVO = new PagingVO(pageNo, qty, type, keyword);
				}
				log.info(">>> pagingVO >>{}", pagingVO);
				
				// DB에서 값 가지고 옴
				List<Board> list = bsv.getList(pagingVO);
				int totalCount = bsv.getTotal(pagingVO);
				
				PagingHandler ph = new PagingHandler(pagingVO, totalCount);
				log.info(">>> ph >>{}", ph);
				
				request.setAttribute("list", list);
				request.setAttribute("ph", ph);
				destPage="/board/list.jsp";
				rdpForward(request, response);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			
		case "detail":
			try {
				int bno = Integer.parseInt(request.getParameter("bno"));
				
				Board board = bsv.getDetail(bno);
				request.setAttribute("board", board);
				destPage="/board/detail.jsp";
				rdpForward(request, response);	
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			
		case "modify":
			try {
				int bno = Integer.parseInt(request.getParameter("bno"));
				
				Board board = bsv.getModify(bno);
				request.setAttribute("board", board);
				destPage="/board/modify.jsp";
				rdpForward(request, response);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			
		case "update":
			try {
				
				Board board = new Board();
				String savePath = getServletContext().getRealPath("/_fileUpload");

				File fileDir = new File(savePath);
				int size = 1024*1024*3;
				
				DiskFileItemFactory fileItemFactory = new DiskFileItemFactory(size, fileDir);
				
				ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);
				
				List<FileItem> itemList = fileUpload.parseRequest(request);
				
				String old_file = null; // 기존 이미지 파일이 있다면 여기다 저장
				
				for(FileItem item : itemList) {
					// item fieldName => input name = ''
					switch(item.getFieldName()) {
					case "bno": 
						board.setBno(Integer.parseInt(item.getString("utf-8")));
						break;
						
					case "title": 
						board.setTitle(item.getString("utf-8"));
						break;
						
					case "contents": 
						board.setContents(item.getString("utf-8"));
						break;
						
					case "imagefile": 
						old_file = item.getString("utf-8");
						break;
						
					case "newfile":
						// 새로 추가되는 파일이 있다면...
						if(item.getSize() > 0) {
							if(old_file != null) {
								FileRemoveHandler fh = new FileRemoveHandler();
								boolean isDel = fh.deleteFile(savePath, old_file);
							}
							// 새파일 등록 작업
							String fileName = System.currentTimeMillis()+"_"+item.getName();
							// 경로 + 구분자 + 파일이름
							File uploadFile = new File(fileDir+File.separator+fileName);
							// 저장
							try {
								
								item.write(uploadFile);
								board.setImagefile(fileName);
								
								Thumbnails.of(uploadFile)
								.size(75, 75)
								.toFile(new File(fileDir+File.separator+"th_"+fileName));
								
							} catch (Exception e) {
								log.info("file upload update error");
								e.printStackTrace();
							}
						}else {
							// 새로 추가되는 파일이 없으면 ... 기존 파일값을 그대로 넣기
							board.setImagefile(old_file);
						}
						break;
					}
				}
				
				int isOk = bsv.update(board);
				log.info(">>> update {}", (isOk > 0) ? "성공" : "실패");
				
				destPage = "detail?bno="+board.getBno();
				response.sendRedirect(destPage);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			
		case "delete":
			try {
				int bno = Integer.parseInt(request.getParameter("bno"));
				
				// db 에 삭제 요청
				bsv.delete(bno);
				
				destPage = "list";
				response.sendRedirect(destPage);
				
				log.info(">>> delete {}", bno);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}
	
	private void rdpForward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		rdp = request.getRequestDispatcher(destPage);
		rdp.forward(request, response); 
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}

}
