package org.gcube.informationsystem.resourceregistry.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.resourceregistry.ResourceInitializer;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.gcube.informationsystem.resourceregistry.api.rest.SchemaPath;
import org.gcube.informationsystem.resourceregistry.schema.SchemaManagement;
import org.gcube.informationsystem.resourceregistry.schema.SchemaManagementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@ApplicationPath(SchemaPath.SCHEMA_PATH_PART)
public class SchemaManager {

	private static Logger logger = LoggerFactory.getLogger(SchemaManager.class);
	public static final String TYPE_PATH_PARAM = "type";
	
	/**
	 * e.g. PUT /resource-registry/schema/{E-R}
	 * 
	 * BODY: {...}
	 * 
	 * @param type
	 * @param json
	 * @return
	 * @throws SchemaException
	 */
	@PUT
	@Path("{" + TYPE_PATH_PARAM + "}")
	@Consumes({ MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8 })
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public Response create(
			@PathParam(TYPE_PATH_PARAM) String type,
			String json)
			throws SchemaException, ResourceRegistryException {
		logger.info("Requested {} registration with schema {}", type, json);
		
		AccessType accessType = null;
		try {
			accessType = AccessType.valueOf(type);
			switch (accessType) {
				case EMBEDDED:
					break;
					
				case FACET:
					break;
					
				case RESOURCE:
					break;
					
				case IS_RELATED_TO:
					break;
					
				case CONSISTS_OF:
					break;
	
				default:
					throw new Exception();

			}
		} catch (Exception e) {
			String error = String.format("Cannot register %s schema", type);
			throw new ResourceRegistryException(error);
		}
		
		SchemaManagement schemaManagement = new SchemaManagementImpl();
		String ret = schemaManagement.create(json, accessType);
		return Response.status(Status.CREATED).entity(ret).type(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8).build();
	}

}
