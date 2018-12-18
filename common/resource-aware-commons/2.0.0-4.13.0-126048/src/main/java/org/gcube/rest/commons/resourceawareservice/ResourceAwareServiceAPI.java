package org.gcube.rest.commons.resourceawareservice;

import java.util.List;

import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;

public interface ResourceAwareServiceAPI<T extends StatefulResource> extends ResourceAwareServiceRestAPI {
	
	public List<T> getAllResources();
	
	public List<T> getResourcesByFilter(String filterString);
	
	public List<String> getResourceIDsByFilter(String filterString);
	
	public void onClose();
	
	public void closeService();
	
	public void startService();
}
