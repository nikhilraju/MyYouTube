package fileop;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import video.util.ConnectDB;
import video.util.User;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		User u = new User();
		String username = request.getParameter("name");
		String password = request.getParameter("password");
		u.setPassword(password);
		u.setUsername(username);
		ConnectDB d = null;
		try {
			d = new ConnectDB();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (username == null || "".equalsIgnoreCase(username)
				|| password == null || "".equalsIgnoreCase(password)) {
			out.println("failure");
		} else {
			u.setUserid(d.loginUser(u));
			if (u.getUserid() == -1) {
				out.println("failure");
				return;
			}
			else{
				out.println("/MyYouTube/index.jsp?userid="+u.getUserid());
			}
		}
//		HttpSession session = request.getSession();
//		session.setAttribute("userid", u.getUserid());
//		// request.setAttribute("userid", u.getUserid());
//		//
//		// RequestDispatcher dispatcher = request
//		// .getRequestDispatcher("/index.jsp" + "?userid=" + u.getUserid());
//		
//		System.out.println("userid="+u.getUserid());
//		response.sendRedirect("index.jsp?userid=" + u.getUserid());
//		// dispatcher.forward(request, response);
	}
}
