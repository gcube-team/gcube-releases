package org.gcube.informationsystem.resourceregistry.schema;

import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 * For JSON schema see
 * http://orientdb.com/docs/last/OrientDB-REST.html#class
 * 
 */
public interface SchemaManagement {
	
	public String create(String json, AccessType accessType) throws SchemaException;
	
	public String read(String type, boolean includeSubtypes) throws SchemaNotFoundException, SchemaException;
	
	public String update(String type, AccessType accessType, String json) throws SchemaNotFoundException, SchemaException;
	
	public String delete(String type, AccessType accessType) throws SchemaNotFoundException;
	
}
