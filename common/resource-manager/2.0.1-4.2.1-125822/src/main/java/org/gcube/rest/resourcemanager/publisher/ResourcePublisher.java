package org.gcube.rest.resourcemanager.publisher;

import org.gcube.rest.commons.resourceawareservice.resources.GeneralResource;

public abstract class ResourcePublisher<T extends GeneralResource> {

	public abstract void deleteResource(String resourceID, String scope)
			throws ResourcePublisherException;

	public abstract void updateResource(T resource, String resourceClass, String resourceNamePref, String scope, boolean includeIdinName, boolean onlyBody)
			throws ResourcePublisherException;

	public void updateResource(T resource, String resourceClass, String resourceNamePref, String scope)
			throws ResourcePublisherException{
		this.updateResource(resource, resourceClass, resourceNamePref, scope, true, false);
	}

	public abstract void updateResource(T resource, String scope) throws ResourcePublisherException;
	
	public abstract void publishResource(T resource, String resourceClass, String resourceNamePref, String scope, boolean includeIdInName, boolean onlyBody)
			throws ResourcePublisherException;
	
	public void publishResource(T resource, String resourceClass, String resourceNamePref, String scope)
			throws ResourcePublisherException{
		this.publishResource(resource, resourceClass, resourceNamePref, scope, true, false);
	}
	
}