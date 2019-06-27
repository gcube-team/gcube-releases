package org.gcube.informationsystem.publisher;

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.ResourceMediator;
import org.gcube.common.resources.gcore.ScopeGroup;
//import org.gcube.common.resources.gcore.Resource.Type;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.gcube.informationsystem.publisher.stubs.registry.RegistryStub;
import org.gcube.informationsystem.publisher.stubs.registry.faults.ResourceDoesNotExistException;
import org.gcube.informationsystem.publisher.utils.RegistryStubs;
import org.gcube.informationsystem.publisher.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AdvancedPublisherCommonUtils {
	
	protected RegistryStubs registry;
	private static final Logger log = LoggerFactory.getLogger(AdvancedPublisher.class);
	
	/**
	 * Remove the input resource:
	 * if is a VRE scope: delete profile from IS
	 * if is a VO scope: delete profile from IS
	 * if is a INFRA scope: delete profile in the infra and for every VO under the infrastructures 
	 * @throws IllegalArgumentException if there are problems to contact the Registry service 
	 */
	public <T extends Resource> T forceRemove(T resource){
		RegistryStub stub=null;
		registry=new RegistryStubs();
		String currentScope=ScopeProvider.instance.get();
		if(new ScopeBean(currentScope).is(Type.VRE) || new ScopeBean(currentScope).is(Type.VO)){
			ScopeGroup<String> scopes=resource.scopes();
			List<String> scopesToRemove=new LinkedList<String>(); 
			for(Iterator<String> it=scopes.iterator(); it.hasNext();){
				String scope=it.next();
				log.debug(" check scope: "+scope);
	// if the scope is present in the resource then The resource will be removed from VO			
				if(scope.equals(currentScope)){
					ScopeProvider.instance.set(scope);
					try{
						stub=registry.getStubs();
					}catch(RegistryNotFoundException e){
						throw new IllegalArgumentException(e.getCause());
					}
					log.debug("remove scope "+scope);
					try{
						stub.remove(resource.id(), resource.type().toString());
					}catch(ResourceDoesNotExistException e){
						// if the scope is a VRE scope probably the resource has already been deleted from VO scope
						// it must be removed from the resource
					}
					scopesToRemove.add(scope);
				}
			}
			for(String scope : scopesToRemove){
				ResourceMediator.removeScope(resource, scope);
			}

		}else{ // it is a INFRA scope, the resource will be removed from all the VO scopes under this infra
			ScopeGroup<String> scopes=resource.scopes();
			List<String> scopesToRemove=new LinkedList<String>(); 
			for(Iterator<String> it=scopes.iterator(); it.hasNext();){
				String scope=it.next();
				log.debug(" check scope: "+scope);
				String voScope=null;
				if(new ScopeBean(scope).is(Type.VRE)){
					voScope=new ScopeBean(scope).enclosingScope().toString();
				}
	// if the scope is present in the resource then The resource will be removed from VO			
				if((scope.equals(currentScope)) || (ValidationUtils.isChildScope(currentScope, scope)) || ( (voScope != null) && (ValidationUtils.isChildScope(currentScope, voScope)) )){
					ScopeProvider.instance.set(scope);
					try{
						stub=registry.getStubs();
					}catch(RegistryNotFoundException e){
						throw new IllegalArgumentException(e.getCause());
					}
					log.debug("remove scope "+scope);
					try{
						stub.remove(resource.id(), resource.type().toString());
					}catch(ResourceDoesNotExistException e){
						// if the scope is a VRE scope probably the resource has already been deleted from VO scope
						// it must be removed from the resource
					}
					scopesToRemove.add(scope);
				}
			}
			for(String scope : scopesToRemove){
				ResourceMediator.removeScope(resource, scope);
			}
			
		}
		ScopeProvider.instance.set(currentScope);
		return resource;
	}
	
	public void removeById(String id, org.gcube.common.resources.gcore.Resource.Type type){
		RegistryStub stub=null;
		registry=new RegistryStubs();
		String currentScope=ScopeProvider.instance.get();
		try{
			stub=registry.getStubs();
		}catch(RegistryNotFoundException e){
			throw new IllegalArgumentException(e.getCause());
		}
		log.debug("remove "+id+" from scope "+currentScope);
		try{
			stub.remove(id, type.toString());
		}catch(ResourceDoesNotExistException e){
			// if the scope is a VRE scope probably the resource has already been deleted from VO scope
			// it must be removed from the resource
		}
	}
	
	public void removeById(String id, org.gcube.common.resources.gcore.Resource.Type type, URI endpoint){
		RegistryStub stub=null;
		registry=new RegistryStubs();
		String currentScope=ScopeProvider.instance.get();
		try{
			stub=registry.getStubs(endpoint);
		}catch(RegistryNotFoundException e){
			throw new IllegalArgumentException(e.getCause());
		}
		log.debug("remove "+id+" from scope "+currentScope);
		try{
			stub.remove(id, type.toString());
		}catch(ResourceDoesNotExistException e){
			// if the scope is a VRE scope probably the resource has already been deleted from VO scope
			// it must be removed from the resource
		}
	}
	
}
