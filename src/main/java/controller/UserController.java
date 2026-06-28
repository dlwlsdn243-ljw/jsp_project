package controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.User;
import service.BoardService;
import service.UserService;
import service.UserServiceImpl;


@WebServlet("/user/*")
public class UserController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// log확인
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	// 요청에 대한 응답 데이터를 jsp 형태로 만들어 전송하는 역할
	private RequestDispatcher rdp;
	// 어떤 jsp를 전송할지 경로
	private String destPage;
	
	// service 연결  (인터페이스 연결) 
	private UserService usv;
       

    public UserController() {
        usv = new UserServiceImpl();  // 구현 클래스 생성
    }


	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// doGet, doPost 처리
		
		// 한글깨짐 방지 encoding 설정
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		
		// 동기식에서 보내는 응답객체의 contentType = "text/html; charset=UTF-8"
		response.setContentType("text/html; charset=UTF-8");
				
		// uri 경로
		String uri = request.getRequestURI();
		// path
		String path = uri.substring(uri.lastIndexOf("/")+1);
				
		log.info(">>> user path {}", path);
		
		switch (path) {
		case "register" :
			// 회원가입 페이지로 이동
			destPage = "/member/register.jsp";
			rdpForward(request,response);
			break;
			
		// 회원가입 정보 BD에 저장
		case "insert":
			try {
				String id = request.getParameter("id");
				String pw = request.getParameter("pw");
				String email = request.getParameter("email");
				String phone = request.getParameter("phone");
				
				// user 객체로 생성 
				User user = new User();
				user.setId(id);
				user.setPw(pw);
				user.setEmail(email);
				user.setPhone(phone);
				
				// DB에 회원가입 정보 저장
				int isOk = usv.insert(user);
				
				log.info(">> user insert >> {}", (isOk > 0)? "성공": "실패");
				
				destPage = "/";
				response.sendRedirect(destPage);
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			break;
			
		// 로그인 페이지 열기
		case "login":
			destPage = "/member/login.jsp";
			rdpForward(request,response);
			break;
			
		// 실제 로그인이 이루어지는 캐이스 (세션(Session)에 저장됨)
		// 로그인 => 내 정보를 session 객체에 저장하는 것
		case "join":
			try {
				String id = request.getParameter("id");
				String pw = request.getParameter("pw");
				
				User loginUser = usv.getUser(new User(id,pw));
				
				// 로그인 처리
				// session 객체에 저장
				if(loginUser != null) {
					HttpSession ses = request.getSession();
					ses.setAttribute("ses", loginUser); // ses 객체에 로그인유저 정보 저장
					ses.setMaxInactiveInterval(60*10); // 로그인 유지 시간
					
					destPage = "/";  
					response.sendRedirect(destPage);
					
				}else {
					destPage = "/?login_msg=notUser";
					response.sendRedirect(destPage);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			
		case "logout":
			try {
				HttpSession ses = request.getSession();
				User loginUser = (User)ses.getAttribute("ses");
				int isOk = usv.lastLoginUpdate(loginUser.getId());
				
				// ses 객체 삭제
				ses.removeAttribute("ses");
				
				// 세션 무효화 (끊기)
				ses.invalidate();
				
				destPage = "/";
				response.sendRedirect(destPage);
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			break;
			
		
		
		// 상세페이지로
		case "modify":
			destPage = "/member/modify.jsp";
			rdpForward(request, response);
			break;
			
		// 회원 수정
		case "update":
			try {
				String id = request.getParameter("id");
				String pw = request.getParameter("pw");
				String email = request.getParameter("email");
				String phone = request.getParameter("phone");
				
				HttpSession ses = request.getSession();
				User loginUser = (User)ses.getAttribute("ses");
					
				if(pw.trim().length()==0 || pw == null) {
					pw = loginUser.getPw();
				}
				
				User user = new User();
				user.setId(id);
				user.setPw(pw);
				user.setEmail(email);
				user.setPhone(phone);
					
				int isOk = usv.update(user);
				log.info(">>> update isOk >> {}", (isOk>0)?"성공":"실패");
				
				// 세션을 끊고 다시 로그인 할 수 있게 유도
				if(isOk > 0) {
					ses.removeAttribute("ses");
					ses.invalidate();
					destPage = "/?update_msg=OK";
					response.sendRedirect(destPage);
				}else {
					request.setAttribute("update_msg", "Fail");
					destPage = "/member/modify.jsp";
					rdpForward(request, response);
				}
					
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			
		// 회원탈퇴
		case "remove":
			try {
				HttpSession ses = request.getSession();
				String id = ((User)ses.getAttribute("ses")).getId();
				
				int isOk = usv.delete(id);
				
				if(isOk >0) {
					ses.removeAttribute("ses");
					ses.invalidate();
					destPage= "/?delete_msg=OK";
					response.sendRedirect(destPage);
				}else {
					request.setAttribute("delete_msg", "Fail");
					destPage="/member/modify.jsp";
					rdpForward(request, response);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}
	
	private void rdpForward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		rdp = request.getRequestDispatcher(destPage);
		// 전송
		rdp.forward(request, response); //페이지에서 다른 페이지로 이동하는 페이지 전환 기능들이다.
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}

}
