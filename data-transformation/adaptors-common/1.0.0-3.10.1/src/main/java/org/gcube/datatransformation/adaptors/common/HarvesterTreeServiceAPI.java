package org.gcube.datatransformation.adaptors.common;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.xml.stream.XMLStreamException;

import org.gcube.rest.commons.resourceawareservice.ResourceAwareServiceRestAPI;

public interface HarvesterTreeServiceAPI extends ResourceAwareServiceRestAPI {

	@GET
	@Path(value = "/HarvestTreeCollection")
	@Produces(MediaType.TEXT_XML + "; " + "charset=UTF-8")
	public Response HarvestTreeCollection(
			@Context Request request,
			@QueryParam("treeCollectionID") final String treeCollectionID,
			@QueryParam("treeCollectionName") final String treeCollectionName,
			@QueryParam("scope") final String scope
			) throws IOException, XMLStreamException;
	
	
	
	
	
	
}
