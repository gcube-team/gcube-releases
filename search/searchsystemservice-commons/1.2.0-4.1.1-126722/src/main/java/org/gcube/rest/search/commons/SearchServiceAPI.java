package org.gcube.rest.search.commons;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;

public interface SearchServiceAPI {
	public static String SCOPE_HEADER = "gcube-scope";

	@GET
	@Path(value = "/ping")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response ping();
	
	@GET
	@Path("search")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	Response search(@HeaderParam(SCOPE_HEADER) String scope,
			@QueryParam("query") String query, 
			@QueryParam("all") Boolean all,
			@QueryParam("pretty") Boolean pretty,
			@QueryParam("names") Boolean names);

	@GET
	@Path("searchSec")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	Response searchSec(@HeaderParam(SCOPE_HEADER) String scope,
			@QueryParam("query") String query, 
			@QueryParam("all") Boolean all,
			@QueryParam("pretty") Boolean pretty,
			@QueryParam("names") Boolean names,
			@QueryParam("sids") Set<String> sids);

	@GET
	@Path("/collections")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response collections(@HeaderParam(SCOPE_HEADER) String scope);
	
	@GET
	@Path("/searchableFields")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response searchableFields(@HeaderParam(SCOPE_HEADER) String scope);
	
	@GET
	@Path("/presentableFields")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response presentableFields(@HeaderParam(SCOPE_HEADER) String scope);

	
	
	@GET
	@Path("/fieldsMapping")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response fieldsMapping(@HeaderParam(SCOPE_HEADER) String scope);
	
	@GET
	@Path("/collectionsTypes")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response collectionsTypes(@HeaderParam(SCOPE_HEADER) String scope);
	
}
