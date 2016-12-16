package org.gcube.informationsystem.resourceregistry.resources;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.gcube.informationsystem.resourceregistry.api.SchemaManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.gcube.informationsystem.resourceregistry.api.rest.SchemaPath;
import org.gcube.informationsystem.resourceregistry.resources.impl.SchemaManagementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 * @author Lucio Lelii (ISTI - CNR)
 */
@ApplicationPath(SchemaPath.SCHEMA_PATH_PART)
public class SchemaManager {
	
	
	private static Logger logger = LoggerFactory.getLogger(SchemaManager.class);

	protected SchemaManagement schemaManager = new SchemaManagementImpl();
	
	/**
	 * e.g. PUT /resource-registry/schema/embedded
	 * 
	 * BODY: {...}
	 * 
	 * ?schema={...}
	 * @param jsonSchema
	 * @return
	 * @throws SchemaException
	 */
	@PUT
	@Path(SchemaPath.EMBEDDED_PATH_PART)
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String registerEmbeddedTypeSchema(String jsonSchema) throws SchemaException {
		logger.trace("Requested Embedded registration with schema {}",jsonSchema);
		return schemaManager.registerEmbeddedTypeSchema(jsonSchema);
	}
	
	/**
	 * e.g. PUT /resource-registry/schema/facet
	 * 
	 * BODY: {...}
	 * 
	 * @param jsonSchema
	 * @return
	 * @throws SchemaException
	 */
	@PUT
	@Path(SchemaPath.FACET_PATH_PART)
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String registerFacetSchema(String jsonSchema) throws SchemaException {
		logger.trace("Requested Facet registration with schema {}",jsonSchema);
		return schemaManager.registerFacetSchema(jsonSchema);
	}
	
	/**
	 * e.g. PUT /resource-registry/schema/resource
	 * 
	 * BODY: {...}
	 * 
	 * @param jsonSchema
	 * @param jsonSchema
	 * @return
	 * @throws SchemaException
	 */
	@PUT
	@Path(SchemaPath.RESOURCE_PATH_PART)
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String registerResourceSchema(String jsonSchema) throws SchemaException {
		logger.trace("Requested Resource registration with schema {}",jsonSchema);
		return schemaManager.registerResourceSchema(jsonSchema);
	}

	/**
	 * e.g. PUT /resource-registry/schema/consistOf
	 * 
	 * BODY: {...}
	 * 
	 * @param jsonSchema
	 * @return
	 * @throws SchemaException
	 */
	@PUT
	@Path(SchemaPath.CONSIST_OF_PATH_PART)
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String registerConsistOfSchema(String jsonSchema) throws SchemaException {
		logger.trace("Requested ConsistOf registration with schema {} ",jsonSchema);
		return schemaManager.registerConsistOfSchema(jsonSchema);
	}

	/**
	 * e.g. PUT /resource-registry/schema/relatedTo
	 * 
	 * BODY: {...}
	 * 
	 * @param jsonSchema
	 * @return
	 * @throws SchemaException
	 */
	@PUT
	@Path(SchemaPath.RELATED_TO_PATH_PART)
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String registerRelatedToSchema(String jsonSchema) throws SchemaException {
		logger.trace("Requested RelatedTo registration with schema {} ",jsonSchema);
		return schemaManager.registerRelatedToSchema(jsonSchema);
	}
	
}
