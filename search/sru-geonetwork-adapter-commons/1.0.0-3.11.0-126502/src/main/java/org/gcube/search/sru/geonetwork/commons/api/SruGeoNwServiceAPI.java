package org.gcube.search.sru.geonetwork.commons.api;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.rest.commons.resourceawareservice.ResourceAwareServiceRestAPI;
import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.jboss.resteasy.annotations.GZIP;

public interface SruGeoNwServiceAPI extends ResourceAwareServiceRestAPI {

	

	
	@POST
	@Path(value = "/sru")
	@Produces(MediaType.APPLICATION_XML + "; " + "charset=UTF-8")
	public Response get( 
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@FormParam("resourceID") String resourceID,
			@FormParam("operation") String operation,
			@FormParam("version") Float version,
			@FormParam("recordPacking") String recordPacking,
			@FormParam("query") String query, 
			@FormParam("maximumRecords") Integer maximumRecords, 
			@FormParam("recordSchema") String recordSchema);
	
	
	
}
