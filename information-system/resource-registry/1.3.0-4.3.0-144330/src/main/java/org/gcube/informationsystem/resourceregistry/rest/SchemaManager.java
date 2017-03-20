package org.gcube.informationsystem.resourceregistry.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.gcube.informationsystem.resourceregistry.api.rest.SchemaPath;
import org.gcube.informationsystem.resourceregistry.schema.SchemaManagement;
import org.gcube.informationsystem.resourceregistry.schema.SchemaManagementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 * @author Lucio Lelii (ISTI - CNR)
 */
/**
 * @author Luca Frosini (ISTI - CNR)
 *
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
	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public String create(
			@PathParam(TYPE_PATH_PARAM) String type,
			String json)
			throws SchemaException, ResourceRegistryException {
		logger.trace("Requested {} registration with schema {}", type, json);
		
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
		return schemaManagement.create(json, accessType);
	}
	
	
//	/**
//	 * e.g. PUT /resource-registry/schema/embedded
//	 * 
//	 * BODY: {...}
//	 * 
//	 * @param json
//	 * @return
//	 * @throws SchemaException
//	 */
//	@PUT
//	@Path(SchemaPath.EMBEDDED_PATH_PART)
//	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
//	@Produces(MediaType.APPLICATION_JSON)
//	public String registerEmbeddedTypeSchema(String json)
//			throws SchemaException {
//		logger.trace("Requested {} registration with schema {}", Embedded.NAME,
//				json);
//		SchemaManagement schemaManagement = new SchemaManagementImpl();
//		return schemaManagement.create(json, AccessType.EMBEDDED);
//	}
//
//	/**
//	 * e.g. PUT /resource-registry/schema/facet
//	 * 
//	 * BODY: {...}
//	 * 
//	 * @param json
//	 * @return
//	 * @throws SchemaException
//	 */
//	@PUT
//	@Path(SchemaPath.FACET_PATH_PART)
//	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
//	@Produces(MediaType.APPLICATION_JSON)
//	public String registerFacetSchema(String json) throws SchemaException {
//		logger.trace("Requested {} registration with schema {}", Facet.NAME,
//				json);
//		SchemaManagement schemaManagement = new SchemaManagementImpl();
//		return schemaManagement.create(json, AccessType.FACET);
//	}
//
//	/**
//	 * e.g. PUT /resource-registry/schema/resource
//	 * 
//	 * BODY: {...}
//	 * 
//	 * @param jsonSchema
//	 * @return
//	 * @throws SchemaException
//	 */
//	@PUT
//	@Path(SchemaPath.RESOURCE_PATH_PART)
//	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
//	@Produces(MediaType.APPLICATION_JSON)
//	public String registerResourceSchema(String json)
//			throws SchemaException {
//		logger.trace("Requested {} registration with schema {}", Resource.NAME,
//				json);
//		SchemaManagement schemaManagement = new SchemaManagementImpl();
//		return schemaManagement.create(json, AccessType.RESOURCE);
//	}
//
//	/**
//	 * e.g. PUT /resource-registry/schema/consistsOf
//	 * 
//	 * BODY: {...}
//	 * 
//	 * @param jsonSchema
//	 * @return
//	 * @throws SchemaException
//	 */
//	@PUT
//	@Path(SchemaPath.CONSISTS_OF_PATH_PART)
//	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
//	@Produces(MediaType.APPLICATION_JSON)
//	public String registerConsistsOfSchema(String json)
//			throws SchemaException {
//		logger.trace("Requested {} registration with schema {} ",
//				ConsistsOf.NAME, json);
//		SchemaManagement schemaManagement = new SchemaManagementImpl();
//		return schemaManagement.create(json, AccessType.CONSISTS_OF);
//	}
//
//	/**
//	 * e.g. PUT /resource-registry/schema/isRelatedTo
//	 * 
//	 * BODY: {...}
//	 * 
//	 * @param jsonSchema
//	 * @return
//	 * @throws SchemaException
//	 */
//	@PUT
//	@Path(SchemaPath.IS_RELATED_TO_PATH_PART)
//	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
//	@Produces(MediaType.APPLICATION_JSON)
//	public String registerIsRelatedToSchema(String json)
//			throws SchemaException {
//		logger.trace("Requested {} registration with schema {} ",
//				IsRelatedTo.NAME, json);
//		SchemaManagement schemaManagement = new SchemaManagementImpl();
//		return schemaManagement.create(json, AccessType.IS_RELATED_TO);
//	}

}
