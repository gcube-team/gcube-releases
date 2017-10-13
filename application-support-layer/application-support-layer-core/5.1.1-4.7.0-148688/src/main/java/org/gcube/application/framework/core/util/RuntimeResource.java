package org.gcube.application.framework.core.util;


import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import static org.gcube.resources.discovery.icclient.ICFactory.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.framework.core.GenericResourceInfoI;
import org.gcube.application.framework.core.cache.CachesManager;
import org.gcube.application.framework.core.genericresources.model.ISGenericResource;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuntimeResource {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(RuntimeResource.class);
	
	
	protected static ScopedPublisher scopedPublisher = null;
//	protected static RegistryPublisher registryPublisher = null;
	protected static DiscoveryClient<ServiceEndpoint> client = null;
	
	/**
	 * The D4Science session to be used
	 */
	ASLSession session;
	
	
	public RuntimeResource(String extrenalSessionID, String username)
	{
		session = SessionManager.getInstance().getASLSession(extrenalSessionID, username);
		try {
			ScopeProvider.instance.set(session.getScope());
			scopedPublisher = RegistryPublisherFactory.scopedPublisher(); 
//			registryPublisher = RegistryPublisherFactory.create();	
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
		if(client == null)
		{
			try {
				client = clientFor(ServiceEndpoint.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
				client = null;
			}
		}
	}

	
	public RuntimeResource(ASLSession session) {
		super();
		this.session = session;
		try {
			ScopeProvider.instance.set(session.getScope());
			scopedPublisher = RegistryPublisherFactory.scopedPublisher(); 
//			registryPublisher = RegistryPublisherFactory.create();
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
		if(client == null)
		{
			try {
				client = clientFor(ServiceEndpoint.class);
			} catch (Exception e) {
				logger.error("Exception:", e);
				client = null;
			}
		}
	}

	/**
	 * adds the RuntimeResource to the IS 
	 * @param runtimeResource
	 * @return the id of the newly created runtime resource.
	 * @throws RemoteException
	 */
	public String createRuntimeResource(ServiceEndpoint runtimeResource) throws RegistryNotFoundException {
		List<String> scopes = new ArrayList<String>();
		scopes.add(session.getScope());
		ServiceEndpoint se = scopedPublisher.create(runtimeResource, scopes);
//		ServiceEndpoint se = registryPublisher.create(runtimeResource);
		logger.debug("Created Runtime Resource with id: "+se.id()+" on scope: "+scopes.toString());
		return se.id();
	}
	
	/**
	 * it replaces the runtime resource with this one 
	 * @param runtimeResource
	 * @return
	 * @throws RemoteException 
	 */
	public String updateRuntimeResource(ServiceEndpoint runtimeResource) throws RemoteException {
		try {
		    ServiceEndpoint se = scopedPublisher.update(runtimeResource);
//			ServiceEndpoint se = registryPublisher.update(runtimeResource);
		    logger.debug("Updated Runtime Resource with id: "+runtimeResource.id()+"\tNew id : "+se.id());
		    return se.id();
		} catch (Exception e) {
			logger.error("Exception:", e);
			throw new RemoteException();
		}
	}
	
	
	/**
	 * it replaces the runtime resource with this one 
	 * @param runtimeResource
	 * @return
	 */
	public String deleteRuntimeResource(ServiceEndpoint runtimeResource) throws RemoteException {
		try {
			List<String> scopes=new ArrayList<String>();
		    scopes.add(session.getScope());
		    ServiceEndpoint se = scopedPublisher.remove(runtimeResource,scopes);
//		    ServiceEndpoint se = registryPublisher.remove(runtimeResource);
		    logger.debug("Deleted Runtime Resource with id: "+runtimeResource.id());
		    return se.id();
		} catch (Exception e) {
			logger.error("Exception:", e);
			throw new RemoteException();
		}
	}
	
	public List<ServiceEndpoint> getRuntimeResourceByName(String name){
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name eq '"+name+"'");
		List<ServiceEndpoint> resources = client.submit(query);
		return resources;
	}
	
	public List<ServiceEndpoint> getRuntimeResourceById(String id){
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		query.addCondition("$resource/ID eq '"+id+"'");
		List<ServiceEndpoint> resources = client.submit(query);
		return resources;
	}
	
	public List<ServiceEndpoint> getRuntimeResourceByCategory(String category){
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category eq '"+category+"'");
		List<ServiceEndpoint> resources = client.submit(query);
		return resources;
	}
	

	

}
