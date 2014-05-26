package fileop;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import video.util.ConnectDB;
import video.util.Video;

/**
 * Servlet implementation class RatingServlet
 */
public class RatingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RatingServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		HttpSession h = request.getSession();
		int userid = 0;
		String userid1 = request.getParameter("userid");
		if(!"".equalsIgnoreCase(userid1) && userid1 !=null)
			userid = Integer.parseInt(userid1);
		int videoid = Integer.parseInt(request.getParameter("videoid"));
		int rating = Integer.parseInt(request.getParameter("rating"));
		ConnectDB c = null;
		Video v = new Video();
		try {
			c = new ConnectDB();
			v = c.getVideo(videoid);
			c.insertRating(v, userid, rating);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			v = c.getVideo(videoid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println("Current Rating : "+v.getRating()+"/5");
	}
}
