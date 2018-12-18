package org.gcube.informationsystem.resourceregistry.schema;

import java.util.List;

import org.gcube.informationsystem.model.reference.ISManageable;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.types.TypeBinder.TypeDefinition;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface ResourceRegistrySchemaClient {

	public <ISM extends ISManageable> TypeDefinition create(Class<ISM> clz)
			throws SchemaException, ResourceRegistryException;
	
	public String create(String baseType, String typeDefinitition)
			throws SchemaException, ResourceRegistryException;
	
	
	public <ISM extends ISManageable> List<TypeDefinition> read(Class<ISM> clz, Boolean polymorphic)
			throws SchemaNotFoundException, ResourceRegistryException;
	
	public String read(String type, Boolean polymorphic)
			throws SchemaNotFoundException, ResourceRegistryException;
	
}
