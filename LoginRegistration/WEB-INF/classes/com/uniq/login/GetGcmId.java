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
public class GetGcmId extends HttpServlet {
	private static final long serialVersionUID = 1L;
       UpdateDB updateDb = new UpdateDB();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetGcmId() {
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
		if (flag.equals("3")) {
			response.getWriter().write(updateDb.getGcm(request.getParameter("email")));
		}
	}

}
