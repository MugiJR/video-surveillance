package com.uniq.login;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.uniq.dao.UpdateDB;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       UpdateDB updateDb = new UpdateDB();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String flag = request.getParameter("flag");
		System.out.println(flag);
		if (flag.equals("1")) {
			updateDb.registration(request.getParameter("email").toString(), request.getParameter("password").toString(), request.getParameter("username").toString(), request.getParameter("phone").toString());
			response.getWriter().write("yes");
		}
		else if (flag.equals("2")) {
			
			System.out.println(request.getParameter("gcm_id"));
			if(updateDb.login(request.getParameter("email").toString(), request.getParameter("password").toString(), request.getParameter("gcm_id")))
			{
				response.getWriter().write("yes");
			}else{
				response.getWriter().write("no");
			}
		}
	}

}
