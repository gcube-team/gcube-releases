package org.gcube.informationsystem.resourceregistry.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.informationsystem.model.reference.ER;
import org.gcube.informationsystem.model.reference.ISManageable;
import org.gcube.informationsystem.model.reference.entity.Context;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.ConsistsOf;
import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.informationsystem.resourceregistry.api.exceptions.AvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.query.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.types.TypeBinder.TypeDefinition;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface ResourceRegistryClient {
	
	public <ERType extends ER> boolean exists(Class<ERType> clazz, UUID uuid)
			throws NotFoundException, AvailableInAnotherContextException, ResourceRegistryException;
	
	public boolean exists(String type, UUID uuid)
			throws NotFoundException, AvailableInAnotherContextException, ResourceRegistryException;
	
	public <ERType extends ER> ERType getInstance(Class<ERType> clazz, UUID uuid)
			throws NotFoundException, AvailableInAnotherContextException, ResourceRegistryException;
	
	public String getInstance(String type, UUID uuid)
			throws NotFoundException, AvailableInAnotherContextException, ResourceRegistryException;
	
	public <ERType extends ER, R extends Resource> List<R> getInstances(Class<ERType> clazz, Boolean polymorphic)
			throws ResourceRegistryException;
	
	public String getInstances(String type, Boolean polymorphic) throws ResourceRegistryException;
	
	
	public <R extends Resource, C extends ConsistsOf<?,?>, F extends Facet> List<R> getResourcesFromReferenceFacet(
			Class<R> resourceClass, Class<C> consistsOfClass, Class<F> facetClass, F referenceFacet,
			boolean polymorphic) throws ResourceRegistryException;
	
	public <R extends Resource, C extends ConsistsOf<?,?>, F extends Facet> List<R> getResourcesFromReferenceFacet(
			Class<R> resourceClass, Class<C> consistsOfClass, Class<F> facetClass, UUID referenceFacetUUID,
			boolean polymorphic) throws ResourceRegistryException;
	
	public String getResourcesFromReferenceFacet(String resourceType, String consistsOfType, String facetType,
			UUID referenceFacetUUID, boolean polymorphic) throws ResourceRegistryException;
	
	
	public <R extends Resource, C extends ConsistsOf<?,?>, F extends Facet> List<R> getFilteredResources(
			Class<R> resourceClass, Class<C> consistsOfClass, Class<F> facetClass, boolean polymorphic,
			Map<String,Object> map) throws ResourceRegistryException;
	
	public String getFilteredResources(String resourceType, String consistsOfType, String facetType,
			boolean polymorphic, Map<String,Object> map) throws ResourceRegistryException;
	
	
	public <R extends Resource, I extends IsRelatedTo<?,?>, RR extends Resource> List<R> getRelatedResourcesFromReferenceResource(
			Class<R> resourceClass, Class<I> isRelatedToClass, Class<RR> referenceResourceClass, RR referenceResource,
			Direction direction, boolean polymorphic) throws ResourceRegistryException;
	
	public <R extends Resource, I extends IsRelatedTo<?,?>, RR extends Resource> List<R> getRelatedResourcesFromReferenceResource(
			Class<R> resourceClass, Class<I> isRelatedToClass, Class<RR> referenceResourceClass, UUID referenceResourceUUID,
			Direction direction, boolean polymorphic) throws ResourceRegistryException;
	
	public String getRelatedResourcesFromReferenceResource(String resourceType, String isRelatedToType,
			String referenceResourceType, UUID referenceResourceUUID, Direction direction, boolean polymorphic)
			throws ResourceRegistryException;
	
	
	public <R extends Resource, I extends IsRelatedTo<?,?>, RR extends Resource> List<R> getRelatedResources(
			Class<R> resourceClass, Class<I> isRelatedToClass, Class<RR> referenceResourceClass, Direction direction,
			boolean polymorphic) throws ResourceRegistryException;
	
	public String getRelatedResources(String resourceType, String isRelatedToType, String referenceResourceType,
			Direction direction, boolean polymorphic) throws ResourceRegistryException;
	
	/*
	public <E extends Entity, R extends Relation<?,?>, RE extends Entity> List<E> getRelated(Class<E> entityClass,
			Class<R> relationClass, Class<RE> referenceEntityClass, Direction direction, boolean polymorphic,
			Map<String,Object> map) throws ResourceRegistryException;
	
	public String getRelated(String entityType, String relationType, String referenceEntityType, Direction direction,
			boolean polymorphic, Map<String,Object> map) throws ResourceRegistryException;
	
	public <E extends Entity, R extends Relation<?,?>, RE extends Entity> List<E> getRelated(Class<E> entityClass,
			Class<R> relationClass, Class<RE> referenceEntityClass, RE referenceEntity, Direction direction,
			boolean polymorphic) throws ResourceRegistryException;
	
	public <E extends Entity, R extends Relation<?,?>, RE extends Entity> List<E> getRelated(Class<E> entityClass,
			Class<R> relationClass, Class<RE> referenceEntityClass, UUID referenceEntityUUID, Direction direction,
			boolean polymorphic) throws ResourceRegistryException;
	
	public String getRelated(String entityType, String relationType, String referenceEntityType, UUID referenceEntity,
			Direction direction, boolean polymorphic) throws ResourceRegistryException;
	*/
	
	
	public String query(final String query, final int limit, final String fetchPlan)
			throws InvalidQueryException, ResourceRegistryException;
	
	public <ISM extends ISManageable> List<TypeDefinition> getSchema(Class<ISM> clazz, Boolean polymorphic)
			throws SchemaNotFoundException, ResourceRegistryException;
	
	public Context getContext(UUID uuid) throws ContextNotFoundException, ResourceRegistryException;
	
	public Context getCurrentContext() throws ContextNotFoundException, ResourceRegistryException;
	
	public List<Context> getAllContext() throws ResourceRegistryException;
	
}
