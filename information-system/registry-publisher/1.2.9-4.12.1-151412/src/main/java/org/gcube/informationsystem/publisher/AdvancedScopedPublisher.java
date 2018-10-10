package org.gcube.informationsystem.publisher;

import java.util.List;

import org.gcube.common.resources.gcore.Resource;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;


/**
 * Wrapper class of ScopedPublisherImpl class that implements the ScopedPublisher interface
 * 
 * @author rcirillo
 *
 */

public class AdvancedScopedPublisher extends AdvancedPublisherCommonUtils implements ScopedPublisher {

	private ScopedPublisher publisher;

	public AdvancedScopedPublisher(){
		publisher=new ScopedPublisherImpl();
	}
	
	public AdvancedScopedPublisher(ScopedPublisher publisher){
		this.publisher=publisher;
	}
	
	@Override
	public <T extends Resource> T create(T resource, List<String> scopes)
			throws RegistryNotFoundException {
		return publisher.create(resource, scopes);
	}

	@Override
	public <T extends Resource> T update(T resource)
			throws RegistryNotFoundException {
		return publisher.update(resource);
	}

	@Override
	public <T extends Resource> T remove(T resource, List<String> scopes)
			throws RegistryNotFoundException {
		return publisher.remove(resource, scopes);
	}

//	@Override
//	public void remove(String id, Type type, List<String> scopes)
//			throws RegistryNotFoundException {
//		publisher.remove(id, type, scopes);
//
//	}

}
