package gr.cite.repo.auth.app.resources;

import gr.cite.repo.auth.filters.SessionAttributes;
import io.dropwizard.jersey.sessions.Session;

import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.server.session.AbstractSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/sessionvalidation")
@Produces(MediaType.APPLICATION_JSON)
public class SessionValidationResource {
	String username = "username";
	String email = username + "@email.com";
	
	private static final Logger logger = LoggerFactory
			.getLogger(SessionValidationResource.class);

	@GET
	@Path("/validate")
	public Response validate(@Session HttpSession httpsession){
		httpsession.setAttribute(SessionAttributes.LOGGED_IN_ATTRNAME,
				Boolean.TRUE);
		httpsession.setAttribute(SessionAttributes.USERNAME_IN_ATTRNAME,
				username);
		httpsession.setAttribute(SessionAttributes.EMAIL_IN_ATTRNAME, email);

		logger.info("validated a session with id : " + httpsession.getId());
		printSession(httpsession);
		
		
		return Response.ok().build();
	}
	
	@GET
	@Path("/invalidate")
	public Response invalidate(@Session HttpSession httpsession){
		if (!((AbstractSession)httpsession).isValid()){
			logger.info("session with id : " + httpsession.getId() + " is already invalid");
			return Response.ok().build();
		}
		httpsession.invalidate();
		
		logger.info("invalidating session with id : " + httpsession.getId());
		printSession(httpsession);
		
		return Response.ok().build();
	}
	
	void printSession(HttpSession httpsession){
		
		logger.info("SESSION : " + httpsession.getId());
		
		if (((AbstractSession)httpsession).isValid()){
			logger.info("logged in : " + httpsession.getAttribute(SessionAttributes.LOGGED_IN_ATTRNAME));
			logger.info("username  : " + httpsession.getAttribute(SessionAttributes.USERNAME_IN_ATTRNAME));
			logger.info("email     : " + httpsession.getAttribute(SessionAttributes.EMAIL_IN_ATTRNAME));
		} else {
			logger.info(" ~> session is invalid");
		}
	}
}
