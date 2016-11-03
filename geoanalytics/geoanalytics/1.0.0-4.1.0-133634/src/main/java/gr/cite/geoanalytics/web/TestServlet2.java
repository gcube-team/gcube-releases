package gr.cite.geoanalytics.web;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

/** Simple servlet for testing. Makes use of a helper class. */

@WebServlet("/test-with-utils")
public class TestServlet2 extends HttpServlet {

	private static final long serialVersionUID = -3399442457455233144L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String title = "Test Servlet with Utilities";
		out.println(ServletUtilities.headWithTitle(title) + "<body bgcolor=\"#fdf5e6\">\n" + "<h1>" + title + "</h1>\n"
				+ "<p>Simple servlet for testing.</p>\n" + "</body></html>");
	}
}
