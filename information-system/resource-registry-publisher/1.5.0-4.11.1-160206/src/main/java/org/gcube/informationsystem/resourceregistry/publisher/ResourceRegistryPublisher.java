package org.gcube.informationsystem.resourceregistry.publisher;

import java.util.UUID;

import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceNotFoundException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface ResourceRegistryPublisher {

	@Deprecated
	public <F extends Facet> F createFacet(Class<F> facetClass, F facet)
			throws FacetAlreadyPresentException, ResourceRegistryException;

	public <F extends Facet> F createFacet(F facet)
			throws FacetAlreadyPresentException, ResourceRegistryException;
	
	public String createFacet(String facetType, String facet)
			throws FacetAlreadyPresentException, ResourceRegistryException;

	public String createFacet(String facet) throws FacetAlreadyPresentException, ResourceRegistryException;

	@Deprecated
	public <F extends Facet> F updateFacet(Class<F> facetClass, F facet)
			throws FacetNotFoundException, ResourceRegistryException;
	
	public <F extends Facet> F updateFacet(F facet)
			throws FacetNotFoundException, ResourceRegistryException;
	
	public String updateFacet(UUID uuid, String facet) throws FacetNotFoundException, ResourceRegistryException;

	public String updateFacet(String facet) throws FacetNotFoundException, ResourceRegistryException;

	public <F extends Facet> boolean deleteFacet(F facet) throws FacetNotFoundException, ResourceRegistryException;

	public boolean deleteFacet(UUID uuid) throws FacetNotFoundException, ResourceRegistryException;

	@Deprecated
	public <R extends Resource> R createResource(Class<R> resourceClass, R resource)
			throws ResourceAlreadyPresentException, ResourceRegistryException;

	public <R extends Resource> R createResource(R resource)
			throws ResourceAlreadyPresentException, ResourceRegistryException;
	
	public String createResource(String resourceType, String resource)
			throws ResourceAlreadyPresentException, ResourceRegistryException;

	public String createResource(String resource) throws ResourceAlreadyPresentException, ResourceRegistryException;

	@Deprecated
	public <R extends Resource> R updateResource(Class<R> resourceClass, R resource)
			throws ResourceNotFoundException, ResourceRegistryException;

	public <R extends Resource> R updateResource(R resource)
			throws ResourceNotFoundException, ResourceRegistryException;
	
	public String updateResource(UUID uuid, String resource)
			throws ResourceNotFoundException, ResourceRegistryException;

	public String updateResource(String resource)
			throws ResourceNotFoundException, ResourceRegistryException;
	
	public <R extends Resource> boolean deleteResource(R resource)
			throws ResourceNotFoundException, ResourceRegistryException;

	public boolean deleteResource(UUID uuid) throws ResourceNotFoundException, ResourceRegistryException;

	@Deprecated
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> C createConsistsOf(Class<C> consistsOfClass,
			C consistsOf) throws FacetNotFoundException, ResourceNotFoundException, ResourceRegistryException;
	
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> C createConsistsOf(
			C consistsOf) throws FacetNotFoundException, ResourceNotFoundException, ResourceRegistryException;

	public String createConsistsOf(String consistsOfType, String consistsOf)
			throws FacetNotFoundException, ResourceNotFoundException, ResourceRegistryException;
	
	public String createConsistsOf(String consistsOf)
			throws FacetNotFoundException, ResourceNotFoundException, ResourceRegistryException;
	
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> boolean deleteConsistsOf(C consistsOf)
			throws ResourceRegistryException;

	public boolean deleteConsistsOf(UUID uuid) throws ResourceRegistryException;

	@Deprecated
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> I createIsRelatedTo(
			Class<I> isRelatedToClass, I isRelatedTo) throws ResourceNotFoundException, ResourceRegistryException;

	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> I createIsRelatedTo(
			I isRelatedTo) throws ResourceNotFoundException, ResourceRegistryException;
	
	public String createIsRelatedTo(String isRelatedToType, String isRelatedTo)
			throws ResourceNotFoundException, ResourceRegistryException;

	public String createIsRelatedTo(String isRelatedTo)
			throws ResourceNotFoundException, ResourceRegistryException;
	
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> boolean deleteIsRelatedTo(I isRelatedTo)
			throws ResourceRegistryException;

	public boolean deleteIsRelatedTo(UUID uuid) throws ResourceRegistryException;

	public boolean addResourceToContext(UUID uuid)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public <R extends Resource> boolean addResourceToContext(R resource)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public boolean addFacetToContext(UUID uuid)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public <F extends Facet> boolean addFacetToContext(F facet)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public boolean removeResourceFromContext(UUID uuid)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public <R extends Resource> boolean removeResourceFromContext(R resource)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public boolean removeFacetFromContext(UUID uuid)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public <F extends Facet> boolean removeFacetFromContext(F facet)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException;
}
