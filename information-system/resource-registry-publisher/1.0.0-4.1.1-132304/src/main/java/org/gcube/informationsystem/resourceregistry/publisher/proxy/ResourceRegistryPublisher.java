package org.gcube.informationsystem.resourceregistry.publisher.proxy;

import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

public interface ResourceRegistryPublisher {
	
	public <F extends Facet> F createFacet(Class<F> facetClass, F facet);
	
	public <F extends Facet> F updateFacet(Class<F> facetClass, F facet);
	
	public <F extends Facet> boolean deleteFacet(F facet);
	
	
	public <R extends Resource> R createResource(Class<R> resourceClass, R resource);
	
	public <R extends Resource> boolean deleteResource(R resource);
	
	
	
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> C createConsistsOf(Class<C> consistsOfClass, C consistsOf);
	
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> boolean deleteConsistsOf(C consistsOf);
	
	
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> I createIsRelatedTo(Class<I> isRelatedToClass, I isRelatedTo);
	
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> boolean deleteIsRelatedTo(I isRelatedTo);

	
}
