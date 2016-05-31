package org.gcube.datatransformation.adaptors.db;
//package org.gcube.application.framework.harvesting.db;
//
//
//import javax.ws.rs.GET;
//import javax.ws.rs.HeaderParam;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//import org.gcube.rest.commons.resourceawareservice.ResourceAwareServiceRestAPI;
//import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
//
//
//public interface DBHarvesterAPI extends ResourceAwareServiceRestAPI {
//	
//	
//	
//	
//	@POST
//	@Path(value = "/{id}/InitDBHarvester")
//	@Produces(MediaType.APPLICATION_XML + "; " + "charset=UTF-8")
//	public Response InitDBHarvester(
//			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader,
//			@PathParam("id") String resourceID,
//			@QueryParam("scope") final String scope,
//			@QueryParam("dbCredsXML") final String dbCredsXML,
//			@QueryParam("dbPropsXML") final String dbPropsXML
//			);
//	
//	
//	@GET
//	@Path(value = "/{id}/HarvestDB")
//	@Produces(MediaType.APPLICATION_XML + "; " + "charset=UTF-8")
//	public Response HarvestDB(
//			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader,
//			@PathParam("id") String resourceID,
//			@QueryParam("scope") final String scope,
//			@QueryParam("sourcename") final String sourcename,
//			@QueryParam("sourceprops") final String sourceprops,
//			@QueryParam("recordid") final String recordid 
//			);
//	
//	
//}
