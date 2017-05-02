package org.gcube.informationsystem.publisher;

import org.gcube.common.resources.gcore.Resource;
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

	@Override
	public <T extends Resource> T vosCreate(T resource) {
		return publisher.vosCreate(resource);
	}

	@Override
	public <T extends Resource> T vosUpdate(T resource) {
		return publisher.vosUpdate(resource);
	}	
	
	
}
