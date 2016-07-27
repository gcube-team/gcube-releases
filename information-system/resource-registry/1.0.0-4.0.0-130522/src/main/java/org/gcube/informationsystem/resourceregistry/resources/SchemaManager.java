package org.gcube.informationsystem.resourceregistry.resources;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.gcube.informationsystem.resourceregistry.api.SchemaManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * @author lucio lelii (lucio.lelii@isti.cnr.it)
 * 
 */
@ApplicationPath("schema")
public class SchemaManager {
	
	
	private static Logger logger = LoggerFactory.getLogger(SchemaManager.class);

	@Inject 
	SchemaManagement schemaManager;
	
	@Path("embedded")
	@PUT
	public String registerEmbeddedTypeSchema(String jsonSchema) throws SchemaException {
		logger.trace("Requested Embedded registration with schema {} ",jsonSchema);
		return schemaManager.registerEmbeddedTypeSchema(jsonSchema);
	}
	
	@Path("facet")
	@PUT
	public String registerFacetSchema(String jsonSchema) throws SchemaException {
		logger.trace("Requested Facet registration with schema {} ",jsonSchema);
		return schemaManager.registerFacetSchema(jsonSchema);
	}
	
	@Path("resource")
	@PUT
	public String registerResourceSchema(String jsonSchema) throws SchemaException {
		logger.trace("Requested Resource registration with schema {} ",jsonSchema);
		return schemaManager.registerResourceSchema(jsonSchema);
	}

	@Path("consistof")
	@PUT
	public String registerConsistOfSchema(String jsonSchema) throws SchemaException {
		logger.trace("Requested ConsistOf registration with schema {} ",jsonSchema);
		return schemaManager.registerConsistOfSchema(jsonSchema);
	}

	@Path("relatedto")
	@PUT
	public String registerRelatedToSchema(String jsonSchema) throws SchemaException {
		logger.trace("Requested RelatedTo registration with schema {} ",jsonSchema);
		return schemaManager.registerRelatedToSchema(jsonSchema);
	}
	
}
