package org.gcube.portal;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.LoggerFactory;

@Path("/")
@Singleton
public class WelcomeService {

	// Logger
	private static final org.slf4j.Logger _log = LoggerFactory.getLogger(WelcomeService.class);

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response sayHtmlHello() {

		_log.info("Welcome page requested...");
		return Response.ok("<html><body><h2>The social networking web service is up and running!</h2></body></html>").build();
	}
}
