package org.gcube.informationsystem.publisher;

import java.util.List;

import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.ResourceMediator;
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
		String currentScope=ScopeProvider.instance.get();
		boolean created=false;
		boolean updateNeeded=false;
		for(String scope : scopes){
			ScopeProvider.instance.set(scope);
			if(!created){
				log.info("call to registryPublisher create method with scope "+scope);
				resource=registryPublisher.create(resource);
				created=true;
			}else{
				updateNeeded=true;
				// add the scope on the resource 
				ResourceMediator.setScope(resource, scope);
				
			}
		}
		if(updateNeeded)
			resource=registryPublisher.update(resource);
		ScopeProvider.instance.set(currentScope);
		return resource;
	}
	
	@Override
	public <T extends Resource> T update(T resource) throws RegistryNotFoundException{
		return registryPublisher.update(resource);
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
