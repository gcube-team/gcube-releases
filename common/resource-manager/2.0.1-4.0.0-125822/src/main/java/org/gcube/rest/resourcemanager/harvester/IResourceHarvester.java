package org.gcube.rest.resourcemanager.harvester;

import java.util.Set;

import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;
import org.gcube.rest.resourcemanager.harvester.exceptions.ResourceHarvesterException;

public interface IResourceHarvester<T extends StatefulResource> {

	public abstract T getResourceByID(String serviceEndpoint,
			String resourceID, Class<T> resourceCls, String scope)
			throws ResourceHarvesterException;

	public abstract Set<T> getResources(String serviceEndpoint,
			Class<T> resourceCls, String scope) throws ResourceHarvesterException;

}