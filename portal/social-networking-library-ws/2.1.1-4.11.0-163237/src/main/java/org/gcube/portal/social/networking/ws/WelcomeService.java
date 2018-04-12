package org.gcube.portal.social.networking.ws;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Singleton
public class WelcomeService {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response sayHtmlHello() {
		return Response.ok("<html><body><h2>The social networking web service is up and running!</h2></body></html>").build();
	}
}
