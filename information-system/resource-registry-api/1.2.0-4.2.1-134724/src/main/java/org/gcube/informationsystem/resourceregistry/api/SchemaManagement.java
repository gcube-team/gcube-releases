package org.gcube.informationsystem.resourceregistry.api;

import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;



/**
 * Service Endpoint Interface
 * @author Luca Frosini (ISTI - CNR)
 * 
 * For JSON schema see
 * http://orientdb.com/docs/last/OrientDB-REST.html#class
 * 
 */
public interface SchemaManagement {
	
	public String registerEntitySchema(String jsonSchema) throws SchemaException;
	
	public String getEntitySchema(String entityType) throws SchemaNotFoundException;
	
	public String updateEntitySchema(String entityType, String jsonSchema) throws SchemaNotFoundException, SchemaException;
	
	public String deleteEntitySchema(String entityType) throws SchemaNotFoundException;
	
	
	public String registerFacetSchema(String jsonSchema) throws SchemaException;
	
	public String getFacetSchema(String facetType) throws SchemaNotFoundException;
	
	public String updateFacetSchema(String facetType, String jsonSchema) throws SchemaNotFoundException, SchemaException;
	
	public String deleteFacetSchema(String facetType) throws SchemaNotFoundException;
	
	
	public String registerResourceSchema(String jsonSchema) throws SchemaException;
	
	public String getResourceSchema(String resourceType) throws SchemaNotFoundException;

	public String updateResourceSchema(String resourceType, String jsonSchema) throws SchemaNotFoundException, SchemaException;
	
	public String deleteResourceSchema(String resourceType) throws SchemaNotFoundException;
	
	
	public String registerEmbeddedTypeSchema(String jsonSchema) throws SchemaException;
	
	public String getEmbeddedTypeSchema(String embeddedType) throws SchemaNotFoundException;
	
	public String updateEmbeddedTypeSchema(String embeddedType, String jsonSchema) throws SchemaNotFoundException, SchemaException;
	
	public String deleteEmbeddedTypeSchema(String embeddedType) throws SchemaNotFoundException;
	
	
	public String registerRelationSchema(String jsonSchema) throws SchemaException;
	
	public String getRelationSchema(String relationType) throws SchemaNotFoundException;
	
	public String updateRelationSchema(String relationType, String jsonSchema) throws SchemaNotFoundException, SchemaException;
	
	public String deleteRelationSchema(String relationType) throws SchemaNotFoundException;
	
	
	public String registerConsistOfSchema(String jsonSchema) throws SchemaException;
	
	public String getConsistOfSchema(String consistOfType) throws SchemaNotFoundException;
	
	public String updateConsistOfSchema(String consistOfType, String jsonSchema) throws SchemaNotFoundException, SchemaException;
	
	public String deleteConsistOfSchema(String consistOfType) throws SchemaException;
	

	public String registerRelatedToSchema(String jsonSchema) throws SchemaException;
	
	public String getRelatedToSchema(String relatedToType) throws SchemaNotFoundException;
	
	public String updateRelatedToSchema(String relatedToType, String jsonSchema) throws SchemaNotFoundException, SchemaException;
	
	public String deleteRelatedToSchema(String relatedToType) throws SchemaException;
	
}
