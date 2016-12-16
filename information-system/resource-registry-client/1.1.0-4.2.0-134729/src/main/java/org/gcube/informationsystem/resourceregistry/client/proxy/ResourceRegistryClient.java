package org.gcube.informationsystem.resourceregistry.client.proxy;

import java.util.UUID;

import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.resourceregistry.api.exceptions.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface ResourceRegistryClient {

	public Facet getFacet(UUID uuid)
			throws FacetNotFoundException, ResourceRegistryException;

	public <F extends Facet> F getFacet(Class<F> clazz, UUID uuid)
			throws FacetNotFoundException, ResourceRegistryException;
	
	public String getFacetSchema(String facetType)
			throws SchemaNotFoundException;

	
	public Resource getResource(UUID uuid)
			throws ResourceNotFoundException, ResourceRegistryException;
	
	public <R extends Resource> R getResource(Class<R> clazz, UUID uuid)
			throws ResourceNotFoundException, ResourceRegistryException;
	
	public String getResourceSchema(String resourceType)
			throws SchemaNotFoundException;

	
	public String query(final String query, final int limit,
			final String fetchPlan) throws InvalidQueryException;

}
