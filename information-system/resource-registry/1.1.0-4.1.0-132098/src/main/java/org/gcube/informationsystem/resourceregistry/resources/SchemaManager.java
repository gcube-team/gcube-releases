package org.gcube.informationsystem.resourceregistry.resources;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.gcube.informationsystem.resourceregistry.api.SchemaManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.gcube.informationsystem.resourceregistry.api.rest.SchemaPath;
import org.gcube.informationsystem.resourceregistry.resources.impl.SchemaManagementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * @author Lucio Lelii (lucio.lelii@isti.cnr.it)
 */
@ApplicationPath(SchemaPath.SCHEMA_PATH_PART)
public class SchemaManager {
	
	
	private static Logger logger = LoggerFactory.getLogger(SchemaManager.class);

	protected SchemaManagement schemaManager = new SchemaManagementImpl();
	
	/**
	 * e.g. PUT /resource-registry/schema/embedded?schema={...}
	 * @param jsonSchema
	 * @return
	 * @throws SchemaException
	 */
	@PUT
	@Path(SchemaPath.EMBEDDED_PATH_PART)
	public String registerEmbeddedTypeSchema(@QueryParam(SchemaPath.SCHEMA_PARAM) String jsonSchema) throws SchemaException {
		logger.trace("Requested Embedded registration with schema {}",jsonSchema);
		return schemaManager.registerEmbeddedTypeSchema(jsonSchema);
	}
	
	/**
	 * e.g. PUT /resource-registry/schema/facet?schema={...}
	 * @param jsonSchema
	 * @return
	 * @throws SchemaException
	 */
	@PUT
	@Path(SchemaPath.FACET_PATH_PART)
	public String registerFacetSchema(@QueryParam(SchemaPath.SCHEMA_PARAM) String jsonSchema) throws SchemaException {
		logger.trace("Requested Facet registration with schema {}",jsonSchema);
		return schemaManager.registerFacetSchema(jsonSchema);
	}
	
	/**
	 * e.g. PUT /resource-registry/schema/resource?schema={...}
	 * @param jsonSchema
	 * @param jsonSchema
	 * @return
	 * @throws SchemaException
	 */
	@PUT
	@Path(SchemaPath.RESOURCE_PATH_PART)
	public String registerResourceSchema(@QueryParam(SchemaPath.SCHEMA_PARAM) String jsonSchema) throws SchemaException {
		logger.trace("Requested Resource registration with schema {}",jsonSchema);
		return schemaManager.registerResourceSchema(jsonSchema);
	}

	/**
	 * e.g. PUT /resource-registry/schema/consistOf?schema={...}
	 * @param jsonSchema
	 * @return
	 * @throws SchemaException
	 */
	@PUT
	@Path(SchemaPath.CONSIST_OF_PATH_PART)
	public String registerConsistOfSchema(@QueryParam(SchemaPath.SCHEMA_PARAM) String jsonSchema) throws SchemaException {
		logger.trace("Requested ConsistOf registration with schema {} ",jsonSchema);
		return schemaManager.registerConsistOfSchema(jsonSchema);
	}

	/**
	 * e.g. PUT /resource-registry/schema/relatedTo?schema={...}
	 * @param jsonSchema
	 * @return
	 * @throws SchemaException
	 */
	@PUT
	@Path(SchemaPath.RELATED_TO_PATH_PART)
	public String registerRelatedToSchema(@QueryParam(SchemaPath.SCHEMA_PARAM) String jsonSchema) throws SchemaException {
		logger.trace("Requested RelatedTo registration with schema {} ",jsonSchema);
		return schemaManager.registerRelatedToSchema(jsonSchema);
	}
	
}
