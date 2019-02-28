package org.gcube.informationsystem.resourceregistry.publisher;

import java.util.UUID;

import org.gcube.informationsystem.model.reference.ER;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.ConsistsOf;
import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.informationsystem.resourceregistry.api.exceptions.AlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFoundException;
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

	public <E extends ER> E create(E er)
			throws AlreadyPresentException, ResourceRegistryException;
	
	public String create(String er) throws AlreadyPresentException, ResourceRegistryException;
	
	
	public <E extends ER> E read(E er) throws NotFoundException, ResourceRegistryException;

	public String read(String erType, UUID uuid) throws NotFoundException, ResourceRegistryException;
	
	
	public <E extends ER> E update(E er)
			throws NotFoundException, ResourceRegistryException;
	
	public String update(String erType, String er) throws NotFoundException, ResourceRegistryException;
	
	public String update(String er) throws NotFoundException, ResourceRegistryException;

	
	public <E extends ER> boolean delete(E er) throws NotFoundException, ResourceRegistryException;

	public boolean delete(String erType, UUID uuid) throws NotFoundException, ResourceRegistryException;
	
	
	/* ----- */
	
	
	public <F extends Facet> F createFacet(F facet)
			throws FacetAlreadyPresentException, ResourceRegistryException;
	
	public String createFacet(String facet) throws FacetAlreadyPresentException, ResourceRegistryException;
	
	
	public <F extends Facet> F readFacet(F facet) throws FacetNotFoundException, ResourceRegistryException;

	public String readFacet(String facetType, UUID uuid) throws FacetNotFoundException, ResourceRegistryException;
	
	
	public <F extends Facet> F updateFacet(F facet)
			throws FacetNotFoundException, ResourceRegistryException;

	public String updateFacet(String facet) throws FacetNotFoundException, ResourceRegistryException;

	
	public <F extends Facet> boolean deleteFacet(F facet) throws FacetNotFoundException, ResourceRegistryException;

	public boolean deleteFacet(String facetType, UUID uuid) throws FacetNotFoundException, ResourceRegistryException;

	
	/* ----- */
	
	
	public <R extends Resource> R createResource(R resource)
			throws ResourceAlreadyPresentException, ResourceRegistryException;

	public String createResource(String resource) throws ResourceAlreadyPresentException, ResourceRegistryException;
	
	
	public <R extends Resource> R readResource(R resource)
			throws ResourceNotFoundException, ResourceRegistryException;

	public String readResource(String resourceType, UUID uuid) throws ResourceNotFoundException, ResourceRegistryException;
	

	public <R extends Resource> R updateResource(R resource)
			throws ResourceNotFoundException, ResourceRegistryException;

	public String updateResource(String resource)
			throws ResourceNotFoundException, ResourceRegistryException;
	
	
	public <R extends Resource> boolean deleteResource(R resource)
			throws ResourceNotFoundException, ResourceRegistryException;

	public boolean deleteResource(String resourceType, UUID uuid) throws ResourceNotFoundException, ResourceRegistryException;

	
	/* ----- */
	
	
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> C createConsistsOf(
			C consistsOf) throws NotFoundException, ResourceRegistryException;

	public String createConsistsOf(String consistsOf)
			throws NotFoundException, ResourceRegistryException;
	
	
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> C readConsistsOf(
			C consistsOf) throws NotFoundException, ResourceRegistryException;

	public String readConsistsOf(String consistsOfType, UUID uuid) throws NotFoundException, ResourceRegistryException;
	

	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> C updateConsistsOf(C consistsOf)
			throws NotFoundException, ResourceRegistryException;

	public String updateConsistsOf(String consistsOf)
			throws NotFoundException, ResourceRegistryException;
	
	
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> boolean deleteConsistsOf(C consistsOf)
			throws ResourceRegistryException;

	public boolean deleteConsistsOf(String consistsOfType, UUID uuid) throws ResourceRegistryException;

	
	/* ----- */
	
	
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> I createIsRelatedTo(
			I isRelatedTo) throws ResourceNotFoundException, ResourceRegistryException;

	public String createIsRelatedTo(String isRelatedTo)
			throws ResourceNotFoundException, ResourceRegistryException;
	
	
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> I readIsRelatedTo(
			I isRelatedTo) throws NotFoundException, ResourceRegistryException;

	public String readIsRelatedTo(String isRelatedToType, UUID uuid) throws NotFoundException, ResourceRegistryException;
	

	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> I updateIsRelatedTo(I isRelatedTo)
			throws NotFoundException, ResourceRegistryException;

	public String updateIsRelatedTo(String isRelatedTo)
			throws NotFoundException, ResourceRegistryException;
	
	
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> boolean deleteIsRelatedTo(I isRelatedTo)
			throws ResourceRegistryException;

	public boolean deleteIsRelatedTo(String isRelatedToType, UUID uuid) throws ResourceRegistryException;

	
	/* ----- */
	
	
	public boolean addToContext(UUID contextUUID, String erType, UUID instanceUUID)
			throws NotFoundException, ResourceRegistryException;

	public <E extends ER> boolean addToContext(UUID contextUUID, E er)
			throws NotFoundException, ResourceRegistryException;
	
	public boolean addToCurrentContext(String erType, UUID instanceUUID)
			throws NotFoundException, ResourceRegistryException;

	public <E extends ER> boolean addToCurrentContext(E er)
			throws NotFoundException, ResourceRegistryException;

	public boolean removeFromContext(UUID contextUUID, String erType, UUID instanceUUID)
			throws NotFoundException, ResourceRegistryException;

	public <E extends ER> boolean removeFromContext(UUID contextUUID, E er)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException;
	
	public boolean removeFromCurrentContext(String erType, UUID instanceUUID)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public <E extends ER> boolean removeFromCurrentContext(E er)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException;
	
	
	/* ----- */
	
	
	public boolean addResourceToContext(UUID contextUUID, String resourceType, UUID resourceUUID)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public <R extends Resource> boolean addResourceToContext(UUID contextUUID, R resource)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException;
	
	public boolean addResourceToCurrentContext(String resourceType, UUID resourceUUID)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public <R extends Resource> boolean addResourceToCurrentContext(R resource)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public boolean removeResourceFromContext(UUID contextUUID, String resourceType, UUID resourceUUID)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public <R extends Resource> boolean removeResourceFromContext(UUID contextUUID, R resource)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException;
	
	public boolean removeResourceFromCurrentContext(String resourceType, UUID resourceUUID)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public <R extends Resource> boolean removeResourceFromCurrentContext(R resource)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException;
	
	
	/* ----- */
	
	
	public boolean addFacetToContext(UUID contextUUID, String facetType, UUID facetUUID)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public <F extends Facet> boolean addFacetToContext(UUID contextUUID, F facet)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException;
	
	public boolean addFacetToCurrentContext(String facetType, UUID facetUUID)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public <F extends Facet> boolean addFacetToCurrentContext(F facet)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException;
	
	public boolean removeFacetFromContext(UUID contextUUID, String facetType, UUID facetUUID)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public <F extends Facet> boolean removeFacetFromContext(UUID contextUUID, F facet)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException;
	
	public boolean removeFacetFromCurrentContext(String facetType, UUID facetUUID)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException;

	public <F extends Facet> boolean removeFacetFromCurrentContext(F facet)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException;
}
