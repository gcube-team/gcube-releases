package gr.cite.repo.auth.app.resources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/protected")
@Produces(MediaType.APPLICATION_JSON)
public class ProtectedResource {

	
	@Inject
	public ProtectedResource() {
		super();
	}


	@GET
	@Path("/provider")
	public Response provider() {
		return Response.ok("ok").build();
	}

	@GET
	@Path("/ping")
	public Response ping() {
		return Response.ok("pong").build();
	}
}
