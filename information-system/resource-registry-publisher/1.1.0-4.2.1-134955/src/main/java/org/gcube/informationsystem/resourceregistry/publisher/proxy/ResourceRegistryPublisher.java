package org.gcube.informationsystem.resourceregistry.publisher.proxy;

import java.util.UUID;

import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface ResourceRegistryPublisher {

	public <F extends Facet> F createFacet(Class<F> facetClass, F facet);

	public <F extends Facet> F updateFacet(Class<F> facetClass, F facet);

	public <F extends Facet> boolean deleteFacet(F facet);

	public <R extends Resource> R createResource(Class<R> resourceClass,
			R resource);
	
	public <R extends Resource> R updateResource(Class<R> resourceClass, R resource);
	
	public <R extends Resource> boolean deleteResource(R resource);

	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> C createConsistsOf(
			Class<C> consistsOfClass, C consistsOf);

	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> boolean deleteConsistsOf(
			C consistsOf);

	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> I createIsRelatedTo(
			Class<I> isRelatedToClass, I isRelatedTo);

	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> boolean deleteIsRelatedTo(
			I isRelatedTo);

	public boolean addResourceToContext(UUID uuid)
			throws ResourceNotFoundException, ContextNotFoundException,
			ResourceRegistryException;

	public <R extends Resource> boolean addResourceToContext(R resource)
			throws ResourceNotFoundException, ContextNotFoundException,
			ResourceRegistryException;

	public boolean addFacetToContext(UUID uuid) throws FacetNotFoundException,
			ContextNotFoundException, ResourceRegistryException;

	public <F extends Facet> boolean addFacetToContext(F facet)
			throws FacetNotFoundException, ContextNotFoundException,
			ResourceRegistryException;
	
}
