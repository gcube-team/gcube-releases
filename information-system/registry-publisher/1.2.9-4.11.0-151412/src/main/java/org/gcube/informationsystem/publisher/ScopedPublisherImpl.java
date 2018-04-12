package org.gcube.informationsystem.publisher;

import java.util.List;

import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ScopedPublisherImpl implements ScopedPublisher{
	private RegistryPublisher registryPublisher;
	private static final Logger log = LoggerFactory.getLogger(ScopedPublisherImpl.class);

	
	protected ScopedPublisherImpl(){
		registryPublisher=new RegistryPublisherImpl();
	}
	
	protected ScopedPublisherImpl(RegistryPublisher rp){
		registryPublisher=rp;
	}

	@Override
	public <T extends Resource> T create(T resource, List<String> scopes) throws RegistryNotFoundException{
		return registryPublisher.vosCreate(resource, scopes);
	}
	
	@Override
	public <T extends Resource> T update(T resource) throws RegistryNotFoundException{
		log.info("[UPDATE] call to registryPublisher upadate method on resource "+resource.id()+" in scope {} with scopes {} ", ScopeProvider.instance.get(), resource.scopes().asCollection());
		return registryPublisher.vosUpdate(resource);
	}

	
//	public void remove(String id, Type type, List<String> scopes) throws RegistryNotFoundException {
//		String currentScope=ScopeProvider.instance.get();
//		for(String scope : scopes){
//			ScopeProvider.instance.set(scope);
//			log.info("call to registryPublisher remove method with scope "+scope);
//			registryPublisher.remove(id, type);
//		}
//		ScopeProvider.instance.set(currentScope);
//	}
	
	@Override
	public  <T extends Resource> T remove(T resource, List<String> scopes) throws RegistryNotFoundException{
		String currentScope=ScopeProvider.instance.get();
		for(String scope : scopes){
			ScopeProvider.instance.set(scope);
			registryPublisher.remove(resource);
		}
		ScopeProvider.instance.set(currentScope);
		return resource;
	}

}
