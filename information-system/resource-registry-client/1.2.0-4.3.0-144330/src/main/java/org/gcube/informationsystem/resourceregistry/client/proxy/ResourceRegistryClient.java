package org.gcube.informationsystem.resourceregistry.client.proxy;

import java.util.List;
import java.util.UUID;

import org.gcube.informationsystem.model.ER;
import org.gcube.informationsystem.model.ISManageable;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.query.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.types.TypeBinder.TypeDefinition;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface ResourceRegistryClient {

	public <ERType extends ER> ERType getInstance(
			Class<ERType> clazz, UUID uuid) throws ERNotFoundException,
			ResourceRegistryException;

	public List<? extends Entity> getInstances(
			String type, Boolean polymorphic) throws 
			ResourceRegistryException;
	
	public List<Resource> getInstancesFromEntity(
			String relationType, Boolean polymorphic, 
			UUID reference, Direction direction) throws 
			ResourceRegistryException;
	
	public <ISM extends ISManageable> List<TypeDefinition> getSchema(
			Class<ISM> clazz, Boolean polymorphic)
			throws SchemaNotFoundException, ResourceRegistryException;
	
	public String query(final String query, final int limit,
			final String fetchPlan) throws InvalidQueryException;

}
