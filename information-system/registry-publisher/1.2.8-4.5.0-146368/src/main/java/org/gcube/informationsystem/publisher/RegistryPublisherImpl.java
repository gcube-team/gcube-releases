package org.gcube.informationsystem.publisher;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

import javax.xml.ws.soap.SOAPFaultException;

import org.gcube.common.clients.stubs.jaxws.JAXWSUtils;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.ResourceMediator;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.resources.gcore.ScopeGroup;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.gcube.informationsystem.publisher.scope.ValidatorProvider;
import org.gcube.informationsystem.publisher.stubs.registry.RegistryStub;
import org.gcube.informationsystem.publisher.stubs.registry.faults.CreateException;
import org.gcube.informationsystem.publisher.stubs.registry.faults.InvalidResourceException;
import org.gcube.informationsystem.publisher.stubs.registry.faults.ResourceNotAcceptedException;
import org.gcube.informationsystem.publisher.stubs.registry.faults.UpdateException;
import org.gcube.informationsystem.publisher.utils.RegistryStubs;
import org.gcube.informationsystem.publisher.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RegistryPublisherImpl implements RegistryPublisher {

	private static final Logger log = LoggerFactory.getLogger(RegistryPublisher.class);
	private static final int REGISTRY_THRESHOLD = 5;
	private RegistryStubs registry;
	/**
	 * Used in create operation. If is not null the update on this scope will never be done
	 */
	private String scopeCreated;

	protected RegistryPublisherImpl(){
		registry=new RegistryStubs();
	};

	/** 
	 * The resource is created in the current scope and it is updated in the other scopes that are presents in the resource.
	 * If is a VRE scope then in the resource will be added also the VO and INFRA scope if they are not present
	 * If is a VO scope then in the resource will be added also the INFRA scope if it is not present
	 * @throws RegistryNotFoundException if no service endpoints can be discovered
	 * @throws InvalidResourceExceptionwhen the resource type is invalid service.
	 * @throws ResourceNotAcceptedException when the resource is not accepted cause it doesn't satisfy a requirement 
	 * @throws CreateException when something is failed on creation
	 */

	public < T extends Resource> T create(T resource){
		String scope=ScopeProvider.instance.get();
		T res = internalCreate(resource, scope);
		scopeCreated=scope;
		res=update(res);
		return res;
	}
	
	public < T extends Resource> T vosCreate(T resource){
		String scope=ScopeProvider.instance.get();
		T res = internalCreate(resource, scope);
		scopeCreated=scope;
		res=vosUpdate(res);
		return res;
	}
	
	private < T extends Resource> T internalCreate(T resource, String scope){
		log.trace("registry-publisher: create method");
		
		ValidationUtils.valid("resource", resource);// helper that throws an IllegalArgumentException if resource are null
		ValidationUtils.valid("scopes", scope);// helper that throws an IllegalArgumentException if scopes are null
		if(ValidationUtils.isPresent(resource, scope))
			throw new IllegalStateException("The scope "+scope+" is already present in the resource. The create operation can't be performed ");
		log.debug("resource id found: "+resource.id());
		if ((resource.id()== null) || (resource.id().isEmpty())){
			//  set id
			String id=UUID.randomUUID().toString();
			log.debug("id generated: "+id);
			ResourceMediator.setId(resource, id);
		}
		// set scope on resource 
		log.debug("scope setted in resource: "+scope);
		ResourceMediator.setScope(resource, scope);
		// check if the scope is compatible with the scopes defined in the resources
		ValidatorProvider.getValidator(resource).checkScopeCompatibility(resource, Arrays.asList(scope));
		// add enclosing scopes to the resource	with filtering on GenericResource, RunningIstance, and Service resources	
		String type=resource.type().toString();

		/*
		if((!type.equalsIgnoreCase("GenericResource")) && (!(type.equalsIgnoreCase("RunningInstance"))) && (!(type.equalsIgnoreCase("Service")))){
			ValidationUtils.addEnclosingScopesOnResource(resource, scope);
		}else{
			log.debug(" Resource type: "+type+": for this type of resource there isn't scope promotion");
		}*/
		try {
			Resources.validate(resource);
		}  catch (Exception e) {
			log.error("the resource is not valid", e);
			throw new IllegalArgumentException("the resource is not valid ", e.getCause());
		}
		//retrieve	registry stub
		RegistryStub stub = getRegistryStub();
		createResource(resource, scope, stub);
		return resource;
	}
	
	

	/** 
	 * The resource will be updated on all the scopes that are defined in the resource.
	 * If an updating operation fail. It will be repeated with best-effort delivery approach
	 * @throws RegistryNotFoundException if no service endpoints can be discovered
	 * @throws InvalidResourceException when the resource type is invalid service.
	 * @throws ResourceNotAcceptedException when the resource is not accepted cause it doesn't satisfy a requirement 
	 * @throws CreateException when something is failed on creation
	 */
	public <T extends Resource> T update(T resource){
		log.trace(" update resource with id : "+resource.id());
		String currentScope=ScopeProvider.instance.get();
		ValidationUtils.valid("resource", resource);// helper that throws an IllegalArgumentException if resource are null
		validateScope(resource); // is empty
		try {
			Resources.validate(resource);
		}  catch (Exception e) {
			log.error("the resource is not valid", e);
			throw new IllegalArgumentException("the resource is not valid", e);
		}		
		// retrieves the scopes on resource	
		ScopeGroup<String> scopes=resource.scopes();
		int tries=0;
		for(String scope: scopes){
			log.debug(" check update operation on scope "+scope);
			// if update is calling on create operation and the scope is not equal to create operation scope or if is a simple update operation, try to update it	
			if((scopeCreated == null) || ((scopeCreated!=null)  && (!scopeCreated.equals(scope)))){
				log.info("update operation: updating resource"+resource.id()+" on scope: "+scope);
				ScopeProvider.instance.set(scope);
				registryUpdate(resource, tries);
				tries=0;
			}else{
				log.trace("skip updating on scope "+scopeCreated);
			}

		}
		scopeCreated=null;
		// reset the scope	
		ScopeProvider.instance.set(currentScope);
		return resource;
	}

	@Override
	public <T extends Resource> T vosUpdate(T resource) {
		log.trace(" update resource with id : "+resource.id());
		String currentScope=ScopeProvider.instance.get();
		ValidationUtils.valid("resource", resource);// helper that throws an IllegalArgumentException if resource are null
		validateScope(resource); // is empty
		try {
			Resources.validate(resource);
		}  catch (Exception e) {
			log.error("the resource is not valid", e);
			throw new IllegalArgumentException("the resource is not valid", e);
		}		
		
		HashSet<String> vosScopes = new HashSet<String>();
		int tries=0;
		for(String scope: resource.scopes()){
			ScopeBean scopeBean = new ScopeBean(scope);
			if(scopeBean.is(Type.VRE))
				vosScopes.add(scopeBean.enclosingScope().toString());
			else vosScopes.add(scope);
		}	

		for (String voScope: vosScopes){
			log.debug(" check update operation on scope "+voScope);
			// if update is calling on create operation and the scope is not equal to create operation scope or if is a simple update operation, try to update it	
			if((scopeCreated == null) || ((scopeCreated!=null)  && (!scopeCreated.equals(voScope)))){
				log.info("update operation: updating resource"+resource.id()+" on scope: "+voScope);
				ScopeProvider.instance.set(voScope);
				registryUpdate(resource, tries);
				tries=0;
			}else{
				log.trace("skip updating on scope "+scopeCreated);
			}

		}
		scopeCreated=null;
		// reset the scope	
		ScopeProvider.instance.set(currentScope);
		return resource;
	}

	/** 
	 * The resource will be removed from current scope
	 * if the scope is the last scope in the resource, the profile will be deleted from IS else
	 * if it is a VRE scope then the profile will be updated without the VRE scope, 
	 * if it is a VO scope but there is another VRE scope, belong to the VO, defined in the resource then throw IllegalArgumentException
	 * 
	 * @throws IllegalArgumentException if no service endpoints can be discovered or if there is another VRE scope defined in the resource
	 */
	public  <T extends Resource> T remove(T resource){
		String currentScope=ScopeProvider.instance.get();
		log.info(" remove resource with id : "+resource.id()+" from scope: "+currentScope);
		ValidationUtils.valid("resource", resource);// helper that throws an IllegalArgumentException if resource are null
		ValidationUtils.valid("scopes", currentScope);// helper that throws an IllegalArgumentException if scopes are null
		validateScope(resource);
		try {
			Resources.validate(resource);
		}  catch (Exception e) {
			log.error("the resource is not valid", e);
			throw new IllegalArgumentException("the resource is not valid", e);
		}
		log.info(" remove "+currentScope+" scope from resource "+resource.id());
		ResourceMediator.removeScope(resource, currentScope);
		// retrieves the scopes on resource	and update it
		//		updateResource(resource, currentScope);
		try{
			updateResourceRemoveOperation(resource, currentScope);
		}catch(Exception e){
			ResourceMediator.setScope(resource, currentScope);
			log.error("exception message: "+e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
		return resource;
	}


	private void registryUpdate(Resource resource, int tries){
		log.trace("try to update resource with id: "+resource.id()+" times "+(tries+1)+" on scope: "+ScopeProvider.instance.get());
		try{
			registry.getStubs().update(resource.id(),resource.type().toString(), toXml(resource) );
		}catch(RegistryNotFoundException e){
			throw new IllegalArgumentException(e.getCause());
		}catch(InvalidResourceException e){
			throw new IllegalArgumentException(e.getCause());
		}catch(ResourceNotAcceptedException e){
			throw new IllegalArgumentException(e.getCause());
		}catch(UpdateException e){
			throw new IllegalArgumentException(e.getCause());
		}catch(SOAPFaultException e) {
			log.warn("Failed update resource on "+registry.getEndPoints().get(0)+" times: "+(tries+1));
			if(tries< REGISTRY_THRESHOLD){
				tries++;
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				registryUpdate(resource, tries);
			}
			throw new IllegalArgumentException(JAXWSUtils.remoteCause(e));
		}
	}


	private String toXml(Resource resource){
		StringWriter writer = new StringWriter();
		Resources.marshal(resource, writer);
		return writer.toString();
	}

	private void validateScope(Resource resource){
		ValidatorProvider.getValidator(resource).validate(resource);
	}

	/**
	 * If is the last scope on Resource, the resource will be removed else the resource will be updated
	 * @param resource
	 * @param currentScope
	 */
	private <T extends Resource> void updateResourceRemoveOperation(T resource, String currentScope) {
		if(!isRemoveNeeded(resource, currentScope)){
			updateResource(resource, currentScope);
		}else{ // remove the profile from IC
			log.info("the resource have only the "+currentScope+" scope defined. Remove the resource "+resource.id()+" from IC");
			// if the resource not have any scope, the resource will be removed
			try {
				log.debug("remove from IS scope "+currentScope);
				registry.getStubs().remove(resource.id(), resource.type().toString());
			} catch (Exception e) {
				log.error("the resource can't be removed ", e);
				throw new IllegalArgumentException("the resource can't be removed from scope "+currentScope, e);
			}
			//			ScopeBean currentScopeBean=new ScopeBean(currentScope);
			//			if(currentScopeBean.is(Type.VRE)){
			//				log.debug("remove from resource scope "+currentScopeBean.enclosingScope().toString());
			//				ResourceMediator.removeScope(resource, currentScopeBean.enclosingScope().toString());
			//				log.debug("remove from resource scope "+currentScope);
			//				ResourceMediator.removeScope(resource, currentScope);
			//			}else if(currentScopeBean.is(Type.VO)){
			//				log.debug("remove from resource scope "+currentScope);
			//				ResourceMediator.removeScope(resource, currentScope);
			//			}
			updateResource(resource, currentScope);
		}
	}


	private <T extends Resource> boolean isRemoveNeeded(T resource, String scope){
		if(ValidationUtils.isCompatibleScopeForRemove(resource, scope)){
			return true;
		}else{
			return false;
		}
	}

	private <T extends Resource> void createResource(T resource, String scope, RegistryStub stub) {
		try{
			log.info("create resource with id "+resource.id()+" in scope: "+scope);
			String type=null;
			type=resource.type().toString();
			log.info("resource type is: "+type);
			if(resource.type().equals("ServiceEndpoint"))
				type="RuntimeResource";
			stub.create(toXml(resource), type);
		}catch(InvalidResourceException e){
			throw new IllegalArgumentException(e.getCause());
		}catch(ResourceNotAcceptedException e){
			throw new IllegalArgumentException(e.getCause());
		}catch(CreateException e){
			throw new IllegalArgumentException(e.getCause());
		}catch(SOAPFaultException e) {
			throw new IllegalArgumentException(JAXWSUtils.remoteCause(e));
		}
		log.info("created resource "+resource.id()+" on scope "+scope);
	}

	private RegistryStub getRegistryStub() {
		RegistryStub stub=null;
		try{
			stub=registry.getStubs();
		}catch(RegistryNotFoundException e){
			throw new IllegalArgumentException(e.getCause());
		}
		return stub;
	}

	private <T extends Resource> void updateResource(T resource, String currentScope) {
		ScopeGroup scopes=resource.scopes();
		int tries=0;
		for(Iterator it=scopes.iterator();it.hasNext();){
			String scope=(String)it.next();
			ScopeProvider.instance.set(scope);
			registryUpdate(resource, tries);

		}
		// reset the scope	
		ScopeProvider.instance.set(currentScope);
	}


}
