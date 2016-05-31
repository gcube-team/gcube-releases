package org.gcube.informationsystem.publisher;

import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.ResourceMediator;
import org.gcube.common.resources.gcore.Resource.Type;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.gcube.informationsystem.publisher.stubs.registry.RegistryStub;
import org.gcube.informationsystem.publisher.stubs.registry.faults.RemoveException;
import org.gcube.informationsystem.publisher.stubs.registry.faults.ResourceDoesNotExistException;
import org.gcube.informationsystem.publisher.utils.RegistryStubs;
/**
 * Wrapper class of RegistryPublisherImpl class that implements the RegistryPublisher interface
 * 
 * @author rcirillo
 *
 */
public class AdvancedPublisher extends AdvancedPublisherCommonUtils implements RegistryPublisher {

	private RegistryPublisher publisher;
	private RegistryStubs registry;

	public AdvancedPublisher(){
		publisher=new RegistryPublisherImpl();
		registry=new RegistryStubs();
	}
	
	public AdvancedPublisher(RegistryPublisher publisher){
		this.publisher=publisher;
		registry=new RegistryStubs();
	}

	@Override
	public <T extends Resource> T create(T resource) {
		return publisher.create(resource);
	}

	@Override
	public <T extends Resource> T update(T resource) {
		return publisher.update(resource);
	}

	@Override
	public <T extends Resource> T remove(T resource) {
		return publisher.remove(resource);
	}	
	
	
}
