package app;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



@WebServlet(name = "test", urlPatterns = "/test")
public class TestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private Runnable test;
	
	public TestServlet(Runnable test) {
		this.test=test;
	}
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		test.run();
	}
}
