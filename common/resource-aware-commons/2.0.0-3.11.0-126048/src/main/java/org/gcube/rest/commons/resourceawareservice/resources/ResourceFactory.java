package org.gcube.rest.commons.resourceawareservice.resources;

import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;


public abstract class ResourceFactory<T extends StatefulResource> {
	
	public abstract String getScope();
	
	public abstract T createResource(String resourceID,
			String params) throws StatefulResourceException;
	
	public void loadResource(T resource) throws StatefulResourceException{
		resource.onLoad();
	}
	
	public void closeResource(T resource) throws StatefulResourceException{
		resource.onClose();
	}
	
	public void destroyResource(T resource) throws StatefulResourceException{
		resource.onDestroy();
	}
	
}