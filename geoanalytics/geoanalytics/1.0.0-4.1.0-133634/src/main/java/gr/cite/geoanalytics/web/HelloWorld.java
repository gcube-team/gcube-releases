package gr.cite.geoanalytics.web;

import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import java.io.*; 

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.springframework.web.HttpRequestHandler;

/** Very simplistic servlet that generates plain text.
 *  Uses the @WebServlet annotation that is supported by
 *  Tomcat 7 and other servlet 3.0 containers. 
 *  
 *  From <a href="http://courses.coreservlets.com/Course-Materials/">the
 *  coreservlets.com tutorials on servlets, JSP, Struts, JSF 2.x, 
 *  Ajax, jQuery, GWT, Spring, Hibernate/JPA, Hadoop, and Java programming</a>.
 */

@WebServlet("/hello")
public class HelloWorld extends HttpServlet implements HttpRequestHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PrincipalDao principalDao;
	
	@Inject
	public void setPrincipalDao(PrincipalDao principalDao) {
		this.principalDao = principalDao;
	}
	
  @Override
  public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
      throws ServletException, IOException {
    PrintWriter out = response.getWriter();
    out.println("Hello World");
    try {
		Principal principal = principalDao.systemPrincipal();
		if(principal == null) out.println("System User not present");
		out.println("System User: " + principal.getId() + ": " + principal.getPrincipalData().getFullName() + ", " + principal.getCreationDate().toString());
	} catch (Exception e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}
}
