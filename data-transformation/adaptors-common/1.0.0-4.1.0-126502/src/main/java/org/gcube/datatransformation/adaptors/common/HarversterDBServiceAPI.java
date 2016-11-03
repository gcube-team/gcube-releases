package org.gcube.datatransformation.adaptors.common;

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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.gcube.datatransformation.adaptors.common.db.exceptions.SourceIDNotFoundException;
import org.gcube.rest.commons.resourceawareservice.ResourceAwareServiceRestAPI;
import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;
import org.gcube.rest.resourcemanager.discoverer.exceptions.DiscovererException;
import org.jboss.resteasy.annotations.GZIP;


public interface HarversterDBServiceAPI extends ResourceAwareServiceRestAPI {

	@GET
	@Path(value = "/AvailableResources")
	@Produces(MediaType.APPLICATION_XML + "; " + "charset=UTF-8")
	public Response AvailableResources() throws ParserConfigurationException, TransformerException;
	
	@POST
	@Path(value = "/ReplaceDBHarvesterConfig")
	@Produces(MediaType.TEXT_PLAIN + "; " + "charset=UTF-8")
	public Response ReplaceDBHarvesterConfig(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader,
			@FormParam("dbPropsXML") final String dbPropsXML
			) throws StatefulResourceException, DiscovererException, SourceIDNotFoundException, Exception;
	
	
	@GET
	@Path(value = "/HarvestDatabase")
	@Produces(MediaType.TEXT_XML + "; " + "charset=UTF-8")
	public Response HarvestDatabase(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader,
			@QueryParam("sourcename") String sourcename,
			@QueryParam("propsname") String propsname,
			@QueryParam("recordid") String recordid
			) throws Exception;
}
