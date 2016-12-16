package org.gcube.data_catalogue.grsf_publish_ws;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Singleton
/**
 * The welcome service for the GRSF server.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class WelcomeService {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response sayHtmlHello() {
		return Response.ok("<html><body><h2>The grsf publisher web service is up and running!</h2></body></html>").build();
	}
}
