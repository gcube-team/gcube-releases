package org.gcube.informationsystem.resourceregistry.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.informationsystem.model.ER;
import org.gcube.informationsystem.model.ISManageable;
import org.gcube.informationsystem.model.entity.Context;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.query.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.types.TypeBinder.TypeDefinition;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface ResourceRegistryClient {

	public <ERType extends ER> boolean exists(Class<ERType> clazz, UUID uuid)
			throws ERNotFoundException, ERAvailableInAnotherContextException, ResourceRegistryException;

	public boolean exists(String type, UUID uuid)
			throws ERNotFoundException, ERAvailableInAnotherContextException, ResourceRegistryException;

	public <ERType extends ER> ERType getInstance(Class<ERType> clazz, UUID uuid)
			throws ERNotFoundException, ERAvailableInAnotherContextException, ResourceRegistryException;

	public String getInstance(String type, UUID uuid)
			throws ERNotFoundException, ERAvailableInAnotherContextException, ResourceRegistryException;

	public <ERType extends ER, R extends Resource> List<R> getInstances(Class<ERType> clazz, Boolean polymorphic)
			throws ResourceRegistryException;

	public String getInstances(String type, Boolean polymorphic) throws ResourceRegistryException;

	public <ERType extends ER, E extends Entity, R extends Resource> List<R> getInstancesFromEntity(Class<ERType> clazz,
			Boolean polymorphic, E reference, Direction direction) throws ResourceRegistryException;

	public <ERType extends ER, R extends Resource> List<R> getInstancesFromEntity(Class<ERType> clazz,
			Boolean polymorphic, UUID reference, Direction direction) throws ResourceRegistryException;

	public String getInstancesFromEntity(String type, Boolean polymorphic, UUID reference, Direction direction)
			throws ResourceRegistryException;

	public <R extends Resource, F extends Facet, C extends ConsistsOf<?, ?>> List<R> getFilteredResources(
			Class<R> resourceClass, Class<C> consistsOfClass, Class<F> facetClass, boolean polymorphic,
			Map<String, Object> map) throws ResourceRegistryException;

	public String getFilteredResources(String resourceType, String consistsOfType, String facetType,
			boolean polymorphic, Map<String, Object> map) throws ResourceRegistryException;

	public String query(final String query, final int limit, final String fetchPlan)
			throws InvalidQueryException, ResourceRegistryException;

	public <ISM extends ISManageable> List<TypeDefinition> getSchema(Class<ISM> clazz, Boolean polymorphic)
			throws SchemaNotFoundException, ResourceRegistryException;

	public Context getContext(UUID uuid) throws ContextNotFoundException, ResourceRegistryException;
	
	public List<Context> getAllContext() throws ResourceRegistryException;

}
