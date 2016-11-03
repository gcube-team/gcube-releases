package gr.cite.geoanalytics.web;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

/**
 * Simple servlet for testing. Generates HTML instead of plain text as with the
 * HelloWorld servlet.
 */

@WebServlet("/test1")
public class TestServlet extends HttpServlet {

	private static final long serialVersionUID = 8429930234931917315L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html>\n" + "<html>\n" + "<head><title>A Test Servlet</title></head>\n"
				+ "<body bgcolor=\"#fdf5e6\">\n" + "<h1>Test</h1>\n" + "<p>Simple servlet for testing.</p>\n"
				+ "</body></html>");
	}
}
